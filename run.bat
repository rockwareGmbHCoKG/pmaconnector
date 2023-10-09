@ECHO off

ECHO Loading environment variables
CALL environment.bat
ECHO Starting reststub
cd .\reststub\
CALL run.bat
ECHO Starting middleware
cd ..\middleware\
CALL run.bat
cd ..
ECHO Docker containers successfully started
ECHO Creating configuration and mappings in middleware shared dir
java -jar configuration/target/configuration-0.0.1-jar-with-dependencies.jar ^
%MIDDLEWARE_SHARED_DIR_PATH% %CONFIGURATION_TYPE% %MAPPINGS_TYPE%
