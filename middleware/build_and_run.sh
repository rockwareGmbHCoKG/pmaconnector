#!/bin/bash

set -e
echo "Loading environment variables"
source ../environment.sh
echo "\nBuilding sources (middleware)\n"
mvn clean install -DskipTests=true
echo "\nBuilding image pma_connector_img\n"
docker build -t pma_connector_img .
echo "\Start pma_connector container\n"
/bin/zsh run.sh