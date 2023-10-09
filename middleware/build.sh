#!/bin/bash

set -e
echo "\nBuilding image pma_connector_img\n"
docker build --build-arg pasv_min_port=$MIDDLEWARE_FTP_PASSIVE_INITIAL_PORT \
--build-arg pasv_max_port=$MIDDLEWARE_FTP_PASSIVE_FINAL_PORT \
--build-arg pasv_address=$DOCKER_HOST_IP \
--build-arg frontend_active=$MIDDLEWARE_FRONTEND_ACTIVE -t pma_connector_img .