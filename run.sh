#!/bin/bash

set -e
echo "Loading environment variables"
source environment.sh
echo "\nStarting reststub\n"
cd reststub
/bin/zsh run.sh
echo "\nStarting middleware\n"
cd ../middleware
/bin/zsh run.sh
cd ..
echo "\nDocker containers successfully created and started\n"
echo "Creating configuration and mappings in middleware shared dir\n"
java -jar configuration/target/configuration-0.0.1-jar-with-dependencies.jar \
 ${MIDDLEWARE_SHARED_DIR_PATH} $CONFIGURATION_TYPE $MAPPINGS_TYPE