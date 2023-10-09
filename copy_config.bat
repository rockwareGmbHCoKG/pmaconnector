@ECHO off

call environment.bat

copy ".\configuration\target\configs\%CONFIGURATION_TYPE%\" ".\volumes\shared\"
copy ".\configuration\target\mappings\%MAPPINGS_TYPE%" ".\volumes\shared\"