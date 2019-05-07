

for %%* in (.) do set thisDir=%%~nx*

del "..\%thisDir%.linzip"

"C:\ProgramRPf\7z1604-extra-CLI\7za.exe" a -mcu=on -tzip "..\%thisDir%.linzip" *