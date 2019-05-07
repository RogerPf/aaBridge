

set RPf=x86_amd64

call "C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\bin\amd64\vcvarsamd64.bat"

echo .
echo .
echo .
echo .
echo .

cd C:\c\e_wk\441_aaB\dds-2.8.2\src


nmake -f .\Makefiles\Makefile_Visual_Windows  clean

nmake -f .\Makefiles\Makefile_Visual_Windows

if NOT %RPf%.==x86_amd64. copy .\dds.dll C:\c\e_wk\441_aaB\aaBridge\src\win32-x86\com.rogerpf.aabridge.dds.dll
if NOT %RPf%.==x86_amd64. copy .\dds.dll C:\c\e_wk\441_aaB\aaBridge\bin\win32-x86\com.rogerpf.aabridge.dds.dll

if     %RPf%.==x86_amd64. copy .\dds.dll C:\c\e_wk\441_aaB\aaBridge\src\win32-x86-64\com.rogerpf.aabridge.dds.dll
if     %RPf%.==x86_amd64. copy .\dds.dll C:\c\e_wk\441_aaB\aaBridge\bin\win32-x86-64\com.rogerpf.aabridge.dds.dll
