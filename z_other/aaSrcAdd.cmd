@echo off


"C:\Program Files\7-Zip\7z.exe" a %1 C:\c\e_wk\431_aaB\aaBridge\src\com  C:\ProgramRPf\bin\All_java_source_files_are_included_in_the_com_folder


rem 2 delete two the test books    add an x after the jar file name

if %2.==x.  "C:\Program Files\7-Zip\7z.exe" d %1 books8 books9 
