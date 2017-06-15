package com.afree

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, SparkSession}

object Top10WordsOnPages {

  def main(args: Array[String]): Unit = {
    require(args.nonEmpty, "Argument required is a comma-separated string consisting of URLs (http(s) is optional")

    Logger.getLogger("org").setLevel(Level.ERROR)

    val addrs = args.flatMap(_.split(","))

    val urls = addrs.map{addr => if (addr startsWith "http") addr else "http://" + addr }

    // retrieve <body> text from `url`
    def bodyText(url: String) = JsoupBrowser().get(url).body.text.toLowerCase

    // split by non-alphanumeric chars, map to (url, word) pairs
    def pairWithContent(url: String) = bodyText(url).split("\\W+").map((url, _))

    val contents = urls.flatMap(pairWithContent)

    println("==== SPARK CORE ====")
    val scStart = System.currentTimeMillis()
    sparkCoreProcessing(contents)
    val scStop = System.currentTimeMillis()
    println(s"Spark Core processing took ${scStop - scStart} ms.\n")

    println("==== SPARK SQL ====")
    val sqStart = System.currentTimeMillis()
    sparkSqlProcessing(contents)
    val sqStop = System.currentTimeMillis()
    println(s"Spark SQL processing took ${sqStop - sqStart} ms.\n")
  }


  private def sparkCoreProcessing(contents: Seq[(String, String)]) = {
    val sc = new SparkContext("local[*]", "Top10WordsOnPagesCore")
    val rdd = sc.parallelize(contents)

    val reducedByWord: RDD[((String, String), Int)] = rdd.map((_, 1)).reduceByKey(_ + _)

    val swapped = reducedByWord.map { case ((url, word), count) => (url, (word, count)) }

    // sort mapValues (word, count) in descending order and take top 10 pairs for each key
    val top10ByUrl = swapped.groupByKey().mapValues(_.toList.sortBy(_._2)(Ordering[Int].reverse).take(10))

    top10ByUrl foreach {case (url, pairs) => {
        println(s"\nURL: $url")
        println(pairs)
      }
    }
  }

  case class Contents(url: String, word: String)

  private def sparkSqlProcessing(contents: Seq[(String, String)]) = {
    val spark = SparkSession.builder().appName("Top10WordsOnPagesSQL").getOrCreate()

    import spark.implicits._
    val contentsDS = contents.map{case (url, word) => Contents(url, word)}.toDS()

    // construct row "url", "word", "count"
    val counts = contentsDS.groupBy("url", "word").count()

    import org.apache.spark.sql.functions._
    // sort rows by url and then by count
    val sorted = counts.sort(asc("url"), desc("count"))

    // user defined function to take n first elements from a sequence
    val limitUDF = udf { (stats: Seq[String], limit: Int) => stats.take(limit) }

    // aggregate all "word", "count" pairs with the corresponding url
    val top10ByUrl = sorted.groupBy("url").agg(
        collect_list(concat(col("word"), lit(" "), col("count"))).alias("all")
      // construct the column with top 10 "word", "count" pairs
      ).withColumn("top10", limitUDF($"all", lit(10)))

    top10ByUrl.select("url", "top10").collect().foreach{case Row(url, pairs) => {
        println(s"\nURL: $url")
        println(pairs)
      }
    }

    spark.stop()
  }

}
