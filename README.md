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
[Stage 0:>                                                          (0 + 2) / 2]
URL: http://github.com
List((github,29), (and,21), (to,20), (your,16), (you,16), (for,14), (the,10), (code,10), (a,9), (work,9))

URL: http://spark.apache.org
List((spark,33), (and,16), (the,13), (in,11), (to,11), (apache,10), (on,9), (a,8), (you,7), (1,6))
Spark Core processing took 6407 ms.

==== SPARK SQL ====

URL: http://github.com
WrappedArray(github 29, and 21, to 20, you 16, your 16, for 14, code 10, the 10, a 9, work 9)

URL: http://spark.apache.org
WrappedArray(spark 33, and 16, the 13, to 11, in 11, apache 10, on 9, a 8, you 7, or 6)
Spark SQL processing took 16475 ms.


```
