#!/bin/bash

ALREADY_CONFIGURED="/configuration/ALREADY_CONFIGURED"
if [ ! -e $ALREADY_CONFIGURED ]; then
    touch $ALREADY_CONFIGURED
    echo "\nFirst container startup\n"
    /bin/sh /configuration/configure.sh
else
    echo "\nFTP and SFTP servers already configured\n"
fi
/bin/sh /configuration/restart_servers.sh
