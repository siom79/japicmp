#!/bin/sh
if [ -z "$1" ]
  then
    echo "Please provide the tag of the maven docker container as first argument (e.g. 3.8.4-openjdk-17-slim)."
    exit 1
fi

docker run -it --rm \
  --user $(id -u):$(id -g) \
  -v ~/.m2:/var/maven/.m2:rw \
  -e MAVEN_CONFIG=/var/maven/.m2 \
  -v $PWD:$PWD:rw \
  -w $PWD \
  maven:$1 \
  mvn -Duser.home=/var/maven clean install
