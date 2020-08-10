
@if [%1]==[] @echo .
@if [%1]==[] @echo usage   aaDealAndMerge  <merge-File-No-Ext>   [mo (or) do]     Merge Only   or   Deal only
@if [%1]==[] @echo .
@if [%1]==[] pause
@if [%1]==[] exit(2)

@set mergeOnly=
@set dealOnly=
@set add=

@if [%2]==[no_pause] shift /2 

@if [%2]==[mo] set mergeOnly=true
@if [%2]==[mo] shift /2

@if [%2]==[do] set dealOnly=true
@if [%2]==[do] shift /2

@if not [%2]==[] @set add=%2

@set targ_fldr=.\pbns
@set practice_fldr=..\..\__generated__PBNs
@set merg=-_merged
 

@if [%mergeOnly%]==[true] goto merge

@rem  generate the deals

@mkdir  %targ_fldr%                1> nul 2> nul
@del   "%targ_fldr%\%1*.pbn"   /Q  1> nul 2> nul
@for %%e IN (%1*.txt) DO (
     @ping 127.0.0.1 -n 2 > nul
     call dealer.exe %%e  >  "%targ_fldr%\%%~ne.pbn"
     )

@if [%dealOnly%]==[true] goto end


:merge
php.exe -f C:\c\cphp\aa_merge_pbn\aamerge.php -- -f %1.merge_list

@set mergedName=%1%merg%%add%

copy "yyMERGED__inc__%mergedName%.pbn"     "%targ_fldr%\%mergedName%.pbn"
copy "yyMERGED__inc__%mergedName%.pbn"     "%practice_fldr%\%mergedName%.pbn"

:end
