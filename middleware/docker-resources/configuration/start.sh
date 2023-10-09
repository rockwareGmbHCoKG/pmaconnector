#!/bin/bash

/bin/sh /configuration/install.sh &
java -agentlib:jdwp=transport=dt_socket,address=*:5005,server=y,suspend=n -jar /app.jar