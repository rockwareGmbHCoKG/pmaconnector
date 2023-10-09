@ECHO off

ECHO Loading environment variables
CALL ../environment.bat
ECHO Building sources (middleware)
mvn clean install -DskipTests=true
ECHO Building image pma_connector_img
docker build -t pma_connector_img .
ECHO Start pma_connector container
CALL run.bat