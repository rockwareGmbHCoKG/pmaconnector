@ECHO off

ECHO Starting image reststub_img to reststub container
docker stop reststub
docker rm reststub
docker run -d -p %RESTSTUB_HTTP_PORT%:8080 -p %RESTSTUB_DEBUG_PORT%:5005 -e TZ=%TIMEZONE% --name reststub reststub_img