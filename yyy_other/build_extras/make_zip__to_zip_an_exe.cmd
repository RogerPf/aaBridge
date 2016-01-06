@echo off

if     "%1"=="" echo no filename supplied

if NOT "%1"=="" set fne=%1
if NOT "%1"=="" set fne=%fne:~0,-4%
rem   CURRENTLY REMOVED   the  -pa  adds the password which is the single letter    a
if NOT "%1"=="" "C:\ProgramRPf\7-Zip-CLI\7z.exe" a %fne%.zip -tzip   %1
