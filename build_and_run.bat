@ECHO off

ECHO Loading environment variables
CALL environment.bat
ECHO Beginning of code compilation
CALL mvn clean install -DskipTests=true
ECHO Building reststub
cd .\reststub\
CALL build.bat
ECHO Building middleware
cd ..\middleware\
CALL build.bat
cd ..
ECHO Docker containers successfully created
ECHO Removing DB, logs and configuration
RMDIR "volumes\data_dir" /S /Q
RMDIR "volumes\log_dir" /S /Q
RMDIR "volumes\shared" /S /Q
Del /Q "volumes\*"
call run.bat
