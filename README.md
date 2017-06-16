# spark-common-words

## Overview 
The application is implemented using Scala + Spark + SBT
The top 10 most commonly used words are calculated twice using different approaches - `Spark Core` and `Spark SQL`

SBT (not Maven) was chosen as a more 'comfortable' tool. 
Plus, I could re-use some things related to Docker packaging stuff, because I worked with it before. :)

## How to get
The application is published on Docker Hub:

https://hub.docker.com/r/vbachurin/spark-common-words/

The Docker image be pulled using the command:

`docker pull vbachurin/spark-common-words`

Alterntatively, the Docker image can be built from sources using the SBT:

`sbt docker:publishLocal`

## How to run
Docker run command must be appended with a comma-separated list of URLs:

`docker run vbachurin/spark-common-words spark.apache.org,github.com`

### Run output example

```
$ docker run vbachurin/spark-common-words spark.apache.org,github.com
==== SPARK CORE ====
Using Spark's default log4j profile: org/apache/spark/log4j-defaults.properties
[Stage 0:=============================>                             (1 + 1) / 2]
URL: http://spark.apache.org
(spark,33)
(and,16)
(the,13)
(in,11)
(to,11)
(apache,10)
(on,9)
(a,8)
(you,7)
(1,6)

URL: http://github.com
(github,29)
(and,21)
(to,20)
(your,16)
(you,16)
(for,14)
(the,10)
(code,10)
(a,9)
(work,9)
Spark Core processing took 7057 ms.

==== SPARK SQL ====
+--------------------+------+-----------+
|                 url|  word|count(word)|
+--------------------+------+-----------+
|http://spark.apac...| spark|         33|
|http://spark.apac...|   and|         16|
|http://spark.apac...|   the|         13|
|http://spark.apac...|    to|         11|
|http://spark.apac...|    in|         11|
|http://spark.apac...|apache|         10|
|http://spark.apac...|    on|          9|
|http://spark.apac...|     a|          8|
|http://spark.apac...|   you|          7|
|http://spark.apac...|    or|          6|
+--------------------+------+-----------+

+-----------------+------+-----------+
|              url|  word|count(word)|
+-----------------+------+-----------+
|http://github.com|github|         29|
|http://github.com|   and|         21|
|http://github.com|    to|         20|
|http://github.com|   you|         16|
|http://github.com|  your|         16|
|http://github.com|   for|         14|
|http://github.com|  code|         10|
|http://github.com|   the|         10|
|http://github.com|     a|          9|
|http://github.com|  work|          9|
+-----------------+------+-----------+

Spark SQL processing took 19077 ms.


```
