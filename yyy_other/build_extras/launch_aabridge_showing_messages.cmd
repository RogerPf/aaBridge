@echo\
@echo  This launches the latest (alphabetically last) version of aaBridge
@echo    in windows 'cmd' box.  
@echo  Doing this lets you see any messages genertated by aaBridge as you use it.
@echo    such as:-   Line xxx  pg nn  Card played not in hand! ...
@echo off
@FOR /F "delims=|" %%I IN ('DIR "aaBridge_*.jar" /B /O:N') DO SET HighestVersion=%%I
@echo on

java -jar %HighestVersion%
