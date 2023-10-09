@ECHO off

ECHO Loading environment variables
CALL environment.bat
ECHO Build integration_testing module
cd integration_testing
CALL mvn clean install -DskipTests=true
cd ..
ECHO Run imtegration tests
java -jar integration_testing/target/integration_testing-0.0.1-jar-with-dependencies.jar %DOCKER_HOST_IP% ^
%MIDDLEWARE_HTTP_PORT% %RESTSTUB_HTTP_PORT% pmaconnector pmaconnectorpwd
