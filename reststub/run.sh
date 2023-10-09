#!/bin/bash

set -e
echo "\nStarting image reststub_img to reststub container\n"
docker stop reststub || true
docker rm reststub || true
docker run -d -p $RESTSTUB_HTTP_PORT:8080 -p $RESTSTUB_DEBUG_PORT:5005 -e TZ=$TIMEZONE --name reststub reststub_img