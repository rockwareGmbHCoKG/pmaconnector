#!/bin/bash

set -e
echo "Loading environment variables"
source environment.sh
echo "Build integration_testing module\n"
cd integration_testing
mvn clean install -DskipTests=true
cd ..
echo "\nRun imtegration tests\n"
java -jar integration_testing/target/integration_testing-0.0.1-jar-with-dependencies.jar ${DOCKER_HOST_IP} \
$MIDDLEWARE_HTTP_PORT $RESTSTUB_HTTP_PORT pmaconnector pmaconnectorpwd
