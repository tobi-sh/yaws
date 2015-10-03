# yaws ![Unknown Build-Status](https://travis-ci.org/tobi-sh/yaws.svg?branch=master)

Just another web server serving static content build with plain java without any HTTP specific libs for educational reasons. 
After all this is just my playground to get a better understanding how HTTP/1.1 should work.

At the moment only GET and HEAD requests are implemented.

# Getting started

This repo is self content you can just build the server with
 `./gradlew build` and run the jar which will be created under build/libs with `java -jar build/libs/yaws-1.0.0.jar`
 

You have two optional command line options
* `-p [port]` defines the portnumber where the server should listen too (default is 8080)`
* `-d [documentDirectory]` defines the document dir (as an absolute path) where your content is located at (default is your current working dir)

