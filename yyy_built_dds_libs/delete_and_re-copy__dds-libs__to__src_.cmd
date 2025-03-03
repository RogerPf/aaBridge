
@echo.
@echo  delete all  dds libs  and re-copy  to  '../src'
@echo.
@echo . . . are you sure ?

pause

set dest=..\src


del  /q %dest%\darwin-aarch64\*.*
del  /q %dest%\darwin-x86-64\*.*

del  /q %dest%\linux-aarch64\*.*
del  /q %dest%\linux-x86\*.*
del  /q %dest%\linux-x86-64\*.*

del  /q %dest%\win32-aarch64\*.*
del  /q %dest%\win32-x86\*.*
del  /q %dest%\win32-x86-64\*.*


copy /y darwin-aarch64___S-th__282__RPf\*        %dest%\darwin-aarch64\
copy /y darwin-x86-64____S-th__282__RPf\*        %dest%\darwin-x86-64\

copy /y linux-aarch64____S-th__284__RPf\*        %dest%\linux-aarch64\
copy /y linux-x86________S-th__284__BOH\*        %dest%\linux-x86\
copy /y linux-x86-64_____S-th__284__RPf\*        %dest%\linux-x86-64\

copy /y win32-aarch64___S-th___290__RPf\*        %dest%\win32-aarch64\
copy /y win32-x86_______S-th___284__BOH\*        %dest%\win32-x86\
copy /y win32-x86-64____S-th___290__RPf\*        %dest%\win32-x86-64\  


pause


