@echo off

if ".%1" == "." echo  no file supplied
if ".%1" == "." goto end

echo on

set aaB=C:\c\e_wk\111_aaB\aaBridge

set temp_base=D:\Temp\RPf_aab__make_aab


rmdir /S /Q "%temp_base%"
mkdir       "%temp_base%"

rem unzip the jar to the temp folder
"C:\ProgramRPf\7z1604-extra-CLI\7za.exe" x -tzip %1 -o"%temp_base%"


set src_com=%temp_base%\zzz_current  src - com
rmdir /S /Q "%src_com%"
mkdir       "%src_com%"
xcopy /E /Q   "%aaB%\src\com\*.*"   "%src_com%\"


set lib_only=%temp_base%\zzz_current  lib
rmdir /S /Q "%lib_only%"
mkdir       "%lib_only%"
xcopy /E /Q   "%aaB%\lib\*.*"   "%lib_only%\"


set yyy_other=%temp_base%\yyy_other
rmdir /S /Q "%yyy_other%"
mkdir       "%yyy_other%"
xcopy /E /Q   "%aaB%\yyy_other\*.*"   "%yyy_other%\"



xcopy "%yyy_other%\build_extras\How to*"       %temp_base%
xcopy "%yyy_other%\build_extras\this jar*"     %temp_base%
xcopy "%yyy_other%\build_extras\aaBridge.ico"  %temp_base%
xcopy "%yyy_other%\build_extras\launch_a*.cmd"  %temp_base%
xcopy "%yyy_other%\build_extras\start_a*.cmd"  %temp_base%
xcopy "%yyy_other%\build_extras\start_a*.command"  %temp_base%

rem delete the original jar
del    %1

rem create the zip  -mcu=on  gives you utf8 encoded filenames :)
"C:\ProgramRPf\7z1604-extra-CLI\7za.exe" a -mtc=off -mcu=on -tzip %1 "%temp_base%\*"


:end
