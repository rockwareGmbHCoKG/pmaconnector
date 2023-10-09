@ECHO off

ECHO Loading environment variables
CALL ../environment.bat
ECHO Building sources (reststub)
mvn clean install -DskipTests=true
ECHO Building image reststub_img
docker build -t reststub_img .
ECHO Start reststub docker container
CALL run.bat