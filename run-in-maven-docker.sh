#!/bin/sh
if [ -z "$1" ]
  then
    echo "Please provide the tag of the maven docker container as first argument (e.g. 3.8.4-openjdk-17-slim)."
    exit 1
fi

docker run -it --rm --name maven -v "$(pwd)":/opt/japicmp -v ~/.m2/repository:/root/.m2/repository -w /opt/japicmp maven:$1 mvn clean install
