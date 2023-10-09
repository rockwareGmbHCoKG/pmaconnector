#!/bin/bash

set -e
echo "\nStarting image pma_connector_img to pma_connector container\n"
docker stop pma_connector || true
docker rm pma_connector || true
docker run -d -p $MIDDLEWARE_HTTP_PORT:8080 -p $MIDDLEWARE_DEBUG_PORT:5005 -p $MIDDLEWARE_FTP_PORT:21 \
-p $MIDDLEWARE_SSH_PORT:22 -p \
$MIDDLEWARE_FTP_PASSIVE_INITIAL_PORT-$MIDDLEWARE_FTP_PASSIVE_FINAL_PORT:$MIDDLEWARE_FTP_PASSIVE_INITIAL_PORT-$MIDDLEWARE_FTP_PASSIVE_FINAL_PORT -p $MIDDLEWARE_FRONTEND_PORT:3000 \
-v $MIDDLEWARE_SHARED_DIR_PATH:/shared -v $MIDDLEWARE_DATA_DIR_PATH:/data_dir -v $MIDDLEWARE_LOG_DIR_PATH:/log_dir \
-e TZ=$TIMEZONE --name pma_connector pma_connector_img