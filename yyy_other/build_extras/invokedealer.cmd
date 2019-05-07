@echo off

if     "%1"=="" echo   You MUST supply the script filename
if     "%1"=="" goto end

rem set "RP_outfile=%1"
rem set "RP_outfile=%RP_outfile:~0,-4%"

set "RP_outfile=%~n1.pbn"

@echo on
dealer.exe %1  >  "..\__generated__Self_Practice\%RP_outfile%"


:end
