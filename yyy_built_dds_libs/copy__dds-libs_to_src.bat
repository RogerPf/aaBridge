
@rem  Copy dds libs to aaBridge

@if %1.==multi. goto MULTI

set DEST=..\src
set DDS_ADD=com.rogerpf.aabridge.dds

REM single-threaded the NORMAL ONE
copy .\darwin_________single-th__dual__282\libdds.dylib     %DEST%\darwin\lib%DDS_ADD%.dylib
copy .\linux-x86______single-th__282\libdds.so              %DEST%\linux-x86\lib%DDS_ADD%.so
copy .\linux-x86-64___single-th__282\libdds.so              %DEST%\linux-x86-64\lib%DDS_ADD%.so
copy .\win32-x86______single-th__282\dds.dll                %DEST%\win32-x86\%DDS_ADD%.dll
copy .\win32-x86-64___single-th__282\dds.dll                %DEST%\win32-x86-64\%DDS_ADD%.dll

@goto END


:MULTI

REM multi is NOT for release  OMP support issues on OSX
copy .\darwin_________multi-omp__64only__282\libdds.dylib   %DEST%\darwin\lib%DDS_ADD%.dylib
copy .\linux-x86______multi-omp__282\libdds.so              %DEST%\linux-x86\lib%DDS_ADD%.so
copy .\linux-x86-64___multi-omp__282\libdds.so              %DEST%\linux-x86-64\lib%DDS_ADD%.so
copy .\win32-x86______multi-th___282\dds.dll                %DEST%\win32-x86\%DDS_ADD%.dll
copy .\win32-x86-64___multi-th___282\dds.dll                %DEST%\win32-x86-64\%DDS_ADD%.dll


:END

pause
