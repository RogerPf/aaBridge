@echo off
rem    if Java is not in your path then add it to the command line below 
rem    Eg:  
rem      "C:\Program Files (x86)\Java\jre1.8.0_77\bin\java"  -cp ...
rem    Your  Java  LOCATION WILL BE DIFFERENT

echo on

java  -cp %~dp0  -Xmx100m com.rogerpf.aabridge.controller.AaBridge

pause