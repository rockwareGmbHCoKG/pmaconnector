# PMA Connector

## Description

The project is organized as a Maven multi-module application, composed by 5 modules:

<li><b>common</b>: POJOs and utility classes</li>
<li><b>configuration</b>: utilities to organize configuration and mappings object creation for the main application</li>
<li><b>middleware</b>: the actual application</li>
<li><b>reststub</b>: a stub application that simulates the behavior of PMA rest endpoints</li>

Once started, the application creates 2 Docker containers:

<li><b>pma_connector</b>: the actual application, along with a SFTP and an FTP server</li>
<li><b>reststub</b>: the PMA rest endpoint stub application</li>

## Run the containers

### UNIX

If you need to rebuild the containers (for instance, after a Git project update) → Run “build_and_run.sh” script. This
will build the projects, create the containers and start them accordingly with the customization defined in
“environment.sh”.

If you only need to restart previously stopped containers (without updating the sources) → Run “run.sh” script. In that
way, all data will be maintained.

### Windows

f you need to rebuild the containers (for instance, after a Git project update) → Run “build_and_run.bat” script. This
will build the projects, create the containers and start them accordingly with the customization defined in
“environment.bat”.

If you only need to restart a previously stopped container (without updating the sources) → Run “run.bat” script. In
that way, all data will be maintained.

## Customization

To define the global properties for selecting the behavior of the application, you need to edit the environment.sh (
UNIX) or environment.bat (Windoes) files.
All the scripts that build and start the containers will inherit the customizations defined here.
