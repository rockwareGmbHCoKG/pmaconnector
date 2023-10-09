@ECHO off

Del /Q "volumes\shared\*"
copy ".\execution-resources\" ".\volumes\shared\"

exit