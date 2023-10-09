#!/bin/bash

set -e
echo "\nRestarting vsftpd service\n"
service vsftpd restart&
echo "\nRestarting ssh server\n"
service ssh restart&
ENABLE_FRONTEND="/configuration/ENABLE-FRONTEND-TRUE"
if [ -e $ENABLE_FRONTEND ]; then
  echo "\nRestarting frontend\n"
  cd frontend
  pm2 --name frontend start npm -- start
  cd ..
else
  echo "\nFrontend disabled\n"
fi
echo "\nServers restarted successfully\n"