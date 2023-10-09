#!/bin/bash

set -e
echo "\nStart: updating apt\n"
apt update
echo "\nInstalling vsftpd\n"
apt install -y vsftpd
echo "\nWriting vsftpd.conf file\n"
rm -rf /etc/vsftpd.conf
cp /configuration/vsftpd.conf /etc/vsftpd.conf
echo "\nCreating shared directory\n"
mkdir -p /shared
chown nobody:nogroup /shared
chmod -R 777 /shared
echo "\nAdding user pmaconnector\n"
useradd -m -d /shared pmaconnector
echo "\nSetting pmaconnector password\n"
echo "pmaconnector:pmaconnectorpwd" | chpasswd
echo "\nInstalling openssh-server\n"
apt install -y openssh-server
ENABLE_FRONTEND="/configuration/ENABLE-FRONTEND-TRUE"
if [ -e $ENABLE_FRONTEND ]; then
  echo "\nUnzipping frontend\n"
  tar -xvf frontend.tar.gz
  echo "\nInstalling pm2\n"
  cd frontend
  npm install pm2 -g
  echo "\nInstalling frontend\n"
  npm install
  cd ..
else
  echo "\nFrontend disabled\n"
fi