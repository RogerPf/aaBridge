/*******************************************************************************
 * Copyright (c) 2013 Roger Pfister.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Roger Pfister - initial API and implementation
 ******************************************************************************/
package version;

//@formatter:off

public class VersionAndBuilt {

	public static int buildNo = 1372;    static String built = "2013 July 30";

	// 2013-07-30  1372  Release
	// 2013-07-30  1371  File association support added - needs installer this added to the windows inno one
	// 2013-07-30  1370  Added - Show the 'Losing Trick Count'
	// 2013-07-29  1369  Fixed the non display of the score caused by 1368
	// 2013-07-29  1368  Hacked a delay after showing the final card
	// 2013-07-29  1367  Warnings about breakpoint code now suppress to get clean build
	// 2013-07-28  1366  Bug fix - review was displaying total count of tricks won lost not count at that point
	// 2013-07-28  1365  Review can now toggle the hidden hands on AND off 
	// 2013-07-28  1364  Release
	// 2013-07-28  1363  Menu display bug and REWIEW trick display bug and menu rename
	// 2013-07-28  1362  Release
	// 2013-07-28  1361  Fixed the "not start bug" - phew
	// 2013-07-27  1360  Release
	// 2013-07-27  1359  Better generation of two suit fit deals deals
	// 2013-07-27  1358  Adjustments and deal generation fixes
	// 2013-07-27  1357  Seat options panel spun off from deal choices
	// 2013-07-27  1356  More small slam 'deal choices' added  5-3 dist etc
	// 2013-07-27  1355  Test clearout and first entry into Rel table
	// 2013-07-27  1354  Mpat diagnostic messages now clearer 
	// 2013-07-27  1353  Reordered    E q u   and   R e l   
	// 2013-07-27  1352  Safety
	// 2013-07-26  1351  Safety
	// 2013-07-26  1350  Splitting Strategy into files per hand
	// 2013-07-26  1349  minor cleanup and use of rating bug fix
	// 2013-07-26  1347  Added own Savefiles folder and auto cleanup of Autosaves and quicksave files
	// 2013-07-26  1346  Messages added to Strategy creation
	// 2013-07-26  1345  East West strategy needs clearing - it was not being done
	// 2013-07-26  1344  And more match lines added
	// 2013-07-25  1343  More match lines added
	// 2013-07-25  1342  tripple and double finnesses in trumps and no trumps added to new table
	// 2013-07-25  1341  fixed - review "winner card" shows the wrong one 
	// 2013-07-25  1340  safety
	// 2013-07-24  1339  rebuilding the tests and match table from scratch
	// 2013-07-24  1338  release tweeks
	// 2013-07-24  1337  Build for Release - not all tests working
	// 2013-07-23  1336  Moving all the pattern matches into strategy pt 1
	// 2013-07-23  1334  Safety more tweeks to Strategy
	// 2013-07-23  1333  crossentry evaluation added
	// 2013-07-22  1332  Safety - still in flux
	// 2013-07-22  1331  Safety - Working but... f r a g A n a l  now has own sort
	// 2013-07-22  1330  Safety - More Still working on NT finnessing
	// 2013-07-21  1329  Safety - Still working on NT finnessing
	// 2013-07-21  1328  Sort of staggering back into life
	// 2013-07-21  1327  SAFETY - took its first finnesse in NT  (just about)
	// 2013-07-21  1326  Add Show Web Site option to the help menu
	// 2013-07-21  1325  Prefs now have better title and file names
	// 2013-07-20  1324  Release
	// 2013-07-20  1323  Working on 'trick cleared when should remain' vis bug - Pt 2
	// 2013-07-20  1322  Working on 'trick cleared when should remain' vis bug - Pt 1
	// 2013-07-20  1321  Numbering corrected for local usage build
	// 2013-07-20  1320  Welcome display now blue and more welcome
	// 2013-07-19  1317  Safety
	// 2013-07-19  1316  13 down deal now called the Blue Welcome box
	// 2013-07-19  1315  All (sub 7000) tests passing
	// 2013-07-19  1314  Safety  M p a t  tests starting to come good
	// 2013-07-19  1313  Safety  M p a t   and all pattern recognition  NOT functional at the moment
	// 2013-07-19  1312  Safety while working on the new    M p a t
	// 2013-07-19  1311  C p a t  =>  E p a t (Equ)     D p a t   is still for (Rel)
	// 2013-07-19  1310  Starting to invistigate what to do with C p a t
	// 2013-07-19  1309  Fixed Undo  BUG and added undo out of finished state U by keyboard 
	// 2013-07-18  1308  Release 
	// 2013-07-18  1307  Small clean up for release tweeks  -  RELEASE 
	// 2013-07-18  1306  C l i c k  prompt removed from bot left put in f o u r trick panel  as dot
	// 2013-07-18  1305  Seat selection added to    p r e f s 2
	// 2013-07-17  1304  End of trick pause timer added
	// 2013-07-17  1303  Cleaning and setting  ver to    1 . 0 . 6
	// 2013-07-17  1302  And now works
	// 2013-07-17  1301  Declarer and defender trick pause starting to work
	// 2013-07-17  1300  Prefs Menu now split into two
	// 2013-07-17  1299  Renamed a lot of old   nsNames  to   youNames
	// 2013-07-17  1298  Old   a u t o p l a y   booleans  etc removed from code
	// 2013-07-17  1297  To first apperances the new autobid and autoplay are workings (supports EW stuff)
	// 2013-07-17  1296  new a u t o p l a y  logic added
	// 2013-07-17  1295  new visibility of seats code implemented
	// 2013-07-16  1294  the y o u S e a t  can now be set in edit play for a particular deal
	// 2013-07-16  1293  y o u S e a t  now stored in the deal - b u i l d N u m b e r   now saved in the deal
	// 2013-07-16  1292  E W  removed from message text  and  you / hidden added 
	// 2013-07-16  1291  W E S T    to     W e s t
	// 2013-07-15  1290  Safety
	// 2013-07-15  1289  Added the "You" identifier to the hand display
	// 2013-07-15  1288  Added editTime options to rotate the hands
	// 2013-07-15  1287  Declarer is now shown in the 'South' zone
	// 2013-07-15  1286  Safety
	// 2013-07-15  1285  Safety after eclipse crash
	// 2013-07-14  1284  A few more small GUI tweeks
	// 2013-07-14  1283  Much in the way of usability and button re-ordering
	// 2013-07-13  1282  AutoSave improved and Easy Save options added
	// 2013-07-13  1281  Safety working on save menu buttons etc
	// 2013-07-13  1280  Lots of name shortening
	// 2013-07-12  1279  Some work on running a simple suit
	// 2013-07-12  1277  F05 now passes :) - ie.e returns to hand to take the second finesse
	// 2013-07-11  1276  FINALLY found the bug in d p a t and so now working on  test F05  crossing to partner
	// 2013-07-11  1275  Tweeks for release
	// 2013-07-11  1274  Added the Quick Again button
	// 2013-07-11  1273  Set Button added as fast way to  Edit the Play
	// 2013-07-11  1272  Now also checks played cards match
	// 2013-07-11  1271  Chooses card by use of the finesse table for the first time
	// 2013-07-10  1270  now different code for declarer and defender at every position
	// 2013-07-10  1269  Safety - working on test F03
	// 2013-07-10  1268  Safety - tests now all moved to 6000
	// 2013-07-10  1267  Nightly Save
	// 2013-07-09  1266  And more tiny clean ups 
	// 2013-07-09  1265  Many many small name cleanups
	// 2013-07-09  1264  Card transients now just   r a n k R e l   and   r a n k E q u
	// 2013-07-09  1263  s u i t V a l u e  now   s u i t    everywhere 
	// 2013-07-09  1262  Card  faceValue   now  just   r a n k
	// 2013-07-09  1261  Bid   l e v e l V a l u e  now just   l e v e l
	// 2013-07-09  1260  Old saves now readable
	// 2013-07-09  1259  class Bid  now uses  s u i t   not   s u i t V a l u e   but cannot (yet) read old saves
	// 2013-07-09  1258  Changed the only breaking use of    s u i t   as a variable  added   C h
	// 2013-07-09  1257  Safety and tests save
	// 2013-07-08  1255  Strategy frame work improved structure
	// 2013-07-08  1254  ns AutoPlay Always now has message to tell you are in that state
	// 2013-07-07  1253  ns AutoPlay Always  now works better with U N D O
	// 2013-07-07  1252  Primative NS strategy now working
	// 2013-07-07  1251  NS Stratergy statring to opperate
	// 2013-07-07  1250  Added   S t r a t e g y  class
	// 2013-07-06  1249  D e e p C l o n e  ability added to deal
	// 2013-07-06  1248  e q u F a c e  now fully reduced 
	// 2013-07-06  1247  Tiny msg tweek - Safety 
	// 2013-07-06  1246  C p a t  generation Optimised 
	// 2013-07-06  1245  Moved again to the   F r a g A n a l
	// 2013-07-06  1244  C p a t    moved from   H a n d   to   D e a l
	// 2013-07-05  1243  More on f r a g A n a l C p a t at lead time
	// 2013-07-05  1242  Pattern Matcher  C p a t  can now match for lead
	// 2013-07-05  1241  Pattern Matcher  C p a t  now in its own file
	// 2013-07-05  1240  Calling the tests re-organized
	// 2013-07-05  1239  2nd hand play improvement
	// 2013-07-05  1238  improved test display
	// 2013-07-05  1237  '+', 'x' and '.'  now all work in   C p a t  (+ not really used yet)
	// 2013-07-05  1236  Moving Second player code into the lookup table
	// 2013-07-04  1235  Card to play lookup added - still primative
	// 2013-07-04  1234  Some work on the defense
	// 2013-07-03  1233  Safety  more name changes in the -  F r a g A n a l
	// 2013-07-03  1232  Safety after much minor name changing
	// 2013-07-03  1231  Second player now tries to stop sneaked passed tricks
	// 2013-07-03  1230  Third and first are now more cooperative - high to low, long to short 
	// 2013-07-02  1229  Defence is now more aggresieve when it could beat the contract 
	// 2013-07-01  1228  Defence now leads to strongest declarer, suit to give nothing away 
	// 2013-06-22  1227  Tests can now ask for NOT a particular card 
	// 2013-06-22  1226  Loadable Tests added 
	// 2013-06-21  1225  Drag and Drop of deal files added
	// 2013-06-21  1224  Bug fix and added - Open a test - menu option
	// 2013-06-21  1223  Save As now offers previous loaded folder
	// 2013-06-21  1222  Tests can now be automaticaly run
	// 2013-06-21  1221  Added a 'done' hand first display choice
	// 2013-06-20  1220  Improvements to the discard play in the endings
	// 2013-06-20  1219  Play engine bug fix, 4th in hand played high on a trumped tick
	// 2013-06-19  1218  remove spurious "suit suggestions" that were shown after some undos
	// 2013-06-17  1217  Again with tweeks
	// 2013-06-17  1217  Investigating discard policies - best against squeezes needed
	// 2013-06-16  1216  With INNO installer
	

	public static String all() {
		return ver + "." + buildNo + " - " + built;
	}
	
	static String ver = "1.0.6";
	
	public static String getVer() {
		return ver;
	}
	
	public static String getBuildNo() {
		return "" + buildNo;
	}
	
	public static String getBuilt() {
		return built;
	}
}
