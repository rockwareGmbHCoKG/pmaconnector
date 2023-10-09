# PMA Connector - Rest stub

Docker application that exposes REST API that have the same behavior of the PMA endpoint from DHL.de.

## Run the container

### UNIX

If you need to rebuild the container (for instance, after a Git project update) → Run “build_and_run.sh” script. This
will build the project, create the container and start it accordingly with the customization defined in
“environment.sh”.

If you only need to restart a previously stopped container (without updating the sources) → Run “run.sh” script. In that
way, all data will be maintained.

### Windows

f you need to rebuild the container (for instance, after a Git project update) → Run “build_and_run.bat” script. This
will build the project, create the container and start it accordingly with the customization defined in
“environment.bat”.

If you only need to restart a previously stopped container (without updating the sources) → Run “run.bat” script. In
that way, all data will be maintained.