#!/bin/bash

set -e
echo "Loading environment variables"
source environment.sh
echo "Beginning of code compilation\n"
mvn clean install -DskipTests=true
echo "\nBuilding reststub\n"
cd reststub
/bin/zsh build.sh
echo "\nBuilding middleware\n"
cd ../middleware
/bin/zsh build.sh
cd ..
echo "\nDocker containers successfully created\n"
echo "\nRemoving DB, logs and configuration\n"
rm -rf volumes/*  || true
/bin/zsh run.sh
