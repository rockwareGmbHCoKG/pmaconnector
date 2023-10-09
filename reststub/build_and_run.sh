#!/bin/bash

set -e
echo "Loading environment variables"
source ../environment.sh
echo "\nBuilding sources (reststub)\n"
mvn clean install -DskipTests=true
echo "\nBuilding image reststub_img\n"
docker build -t reststub_img .
echo "\Start reststub docker container\n"
/bin/zsh run.sh