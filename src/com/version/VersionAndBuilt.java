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
package com.version;

import java.util.Calendar;
import java.util.Date;

import com.rogerpf.aabridge.controller.Aaa;

//@formatter:off

public class VersionAndBuilt {
	static int prob_fmt_req_numb = /* best to leave this alone ... best to leave THIS setting alone unless */6000;// compatibility needed
	
	static String ver = "6.0.0.";  public static String devExtra = "";    // extra is dev only, normally null

	public static int buildNo = 6003;    static String y = "2021", m = "Jan", d = "01";

   
	// 2021-01-01  6002  Release Candidate
	// 2021-01-01  6000       pre major release test build
	// 2021-01-01  5229  _p8  Grouping Books and Menubar  -  a How To.doc  updated
	// 2021-01-01  5229  _p7  More shortcuts added for buttons
	// 2020-12-31  5229  _p6  change to vp|-*--,...| now CAN show with  Show all hands
	// 2020-12-30  5229  _p5  Book list scrolling and shortcuts added to
	// 2020-12-29  5229  _p4  F = 1st,  E = ETD, B = Back to M,  O = toggle DDS sOlver, I = toggle kIb visibility
	// 2020-12-28  5229  _p3  F = 1st,  E = ETD, B = Back to M,  D = toggle DDS,        K = toggle Kib visibility
	// 2020-12-28  5229  _p2  F Key now actions enter 1st button
	// 2020-12-27  5229  _p1  hondo mentoring2012d (last of year) added
	// 2020-12-27  5229  _n9  first on repo-18
	//
	//			migrated to repo-18
	//
	// 2020-12-27  5229  _n8  last on repo-17
	// 2020-12-27  5229  _n7  Bil-2020 now blue and Language menu hidden by default
	// 2020-12-26  5229  _n6  BIL-2020 now in place
	// 2020-12-24  5229  _n5  Re-struct of Books-B continues menu now looks better
	// 2020-12-23  5229  _n4  Re-struct of Books,  BIL-2020 (Books-B) replaces Books-X
	// 2020-12-22  5229  _n3  mass inclusion of BIL not sorted out fully
	// 2020-12-20  5229  _n2  hondo mentoring2012c added
	// 2020-12-17  5229  _n1  DEV_config file params added to help debug lin from xml
	// 2020-12-15  5229  _n0  hondo mentoring2012b added 
	// 2020-12-12  5229  _m9  rework of _m8 which is buggy like m3 !
	// 2020-12-12  5229  _m8  auto add now does NOT stop attempt to play lowest card
	// 2020-12-12  5229  _m7  fixed bug added _m3 single letter cards eg play a diamond   pg|d| would not play
	// 2020-12-11  5229  _m6  debug lin file partner quick edit added
	// 2020-12-09  5229  _m5  new menu support jumping to 2nd lin
	// 2020-12-09  5229  _m4  Improvements to 'vh' command
	// 2020-12-07  5229  _m3  vh visibility hands  any hand can now be shown or hidden
	// 2020-12-07  5229  _m2a  fixed extra '2' report on cards PROBLEMS with drag n drop ALL NOW FIXED
	// 2020-12-06  5229  _m1  Hondo mentoring12a added
	// 2020-12-05  5229  _k9  single missing cards (as delt) not now reported if in DEV mode
	// 2020-12-02  5229  _k8  drag drop Books-E  book  appears   stable
	// 2020-12-01  5229  _k7  dropped books book marks sort of working
	// 2020-12-01  5229  _k6  Can drag and drop "books" well sort of 
	// 2020-11-30  5229  _k5  #text now preserves rh header id
	// 2020-11-19  5229  _k4  bug fixed in 'Alo' handling
	// 2020-11-16  5229  _k3  Testing the 'Also' extension to Bot Instructions 'bi'
	// 2020-11-15  5229  _k2  Hondo mentoring2011c
	// 2020-11-15  5229  _k1  BI Now stable first field is West's card the second is East's
	// 2020-11-14  5229  _k0  Some stability now  -  BI bot instructions changed 
	// 2020-11-13  5229  _j9  bi bot instruction bug fixed (fingers crossed)
	// 2020-11-11  5229  _j8  changes to  pf 1st button message
	// 2020-11-10  5229  _j7  tweek to the - fix the non-restore hidden - below
	// 2020-11-10  5229  _j6  fix the non-restore hidden when in virgin semi tutorial mode
	// 2020-11-09  5229  _j5  Hondo mentoring2011b  and BM 540 into archive
	// 2020-11-07  5229  _j4  internal link to file checks current loaded (possibly external) book
	// 2020-11-07  5229  _j3  aapg package now 3.52  supports multi pg|formats|
	// 2020-11-02  5229  _j2  L0 - L9 'super names' added
	// 2020-11-02  5229  _j1  Hondo mentoring2011a
	// 2020-10-26  5229  _h9  Hondo mentoring2010c
	// 2020-10-19  5229  _h8  Hondo mentoring2010b added and aadm fix to 2nd copy
	// 2020-10-04  5229  _h7  Hondo mentoring2010a added
	// 2020-09-27  5229  _h6  Hondo mentoring2009d added
	// 2020-09-21  5229  _h5  Hondo mentoring2009c added
	// 2020-09-13  5229  _h4  Hondo mentoring2009b added and some bot hints on brennan
	// 2020-09-08  5229  _h3  rename zaXXons to fit contents better
	// 2020-09-08  5229  _h2  clean up Brannen problems
	// 2020-09-08  5229  _h1  clean up on zaXXon and Brannen problems and in problem menu
	// 2020-09-07  5229  _g   now 5.3 and Hondo mentoring2009a added  and Book-T problems tidied
	// 2020-08-31  5229  _f   Hondo mentoring2008d added
	// 2020-08-23  5229  _e   Hondo mentoring2008c added
	// 2020-08-17  5229  _d   Hondo mentoring2008b added
	// 2020-08-15  5229  _c   tiny fixes to LTPB1
	// 2020-08-15  5229  _b   sk||  now   sk|-|   (in saved lin files)
	// 2020-08-10  5229  _a   Hondo mentoring2008a added  and lin file tweaks
	// 2020-08-07  5228  Release Candidate
	// 2020-08-07  5227  _w   doc now proof read TY Charlene
	// 2020-08-04  5227  _v   Lin_Files - a Starter  inc Problem Format to Charlene
	// 2020-08-02  5227  _u   same as u7
	// 2020-08-01  5227  _u7  new setting added
	// 2020-08-01  5227  _u6  and again
	// 2020-07-31  5227  _u5  and again
	// 2020-07-31  5227  _u4  tiny fix - Stop clearing the "y" of Sally
	// 2020-07-30  5227  _u3  tiny fix
	// 2020-07-30  5227  _u2  tiny fix
	// 2020-07-30  5227  _u1  renamed and extended - Lin_Files_-_a_Starter__inc_Problem_Format.doc 
	// 2020-07-29  5227  _t   now usable
	// 2020-07-28  5227  _t2  And more
	// 2020-07-27  5227  _t1  More tweaks 
	// 2020-07-27  5227  _s   Problem mode more tweaks
	// 2020-07-27  5227  _r   Problem mode extended to include hidden places for user hints
	// 2020-07-26  5227  _q   Hondo mentoring2007c added   and BOT Hints unzipped
	// 2020-07-26  5227  _p   Final Crazy slam set issued so creating  Set 9
	// 2020-07-25  5227  _n   New Crazy Slams added and old now new Books-V Set 8
	// 2020-07-22  5227  _m   Problem Format is now NOT saved as a "Study Deal"
	// 2020-07-15  5227  _k   Added Roger's Find number 33
	// 2020-07-15  5227  _j   Added a 'list of Problems' to main  index
	// 2020-07-13  5227  _h   Hondo mentoring2007b added
	// 2020-07-08  5227  _g   Hondo mentoring2007a added
	// 2020-06-25  5227  _f   Hondo mentoring2006c added and internal link fixes
	// 2020-06-08  5227  _e   fixes to 1827 generation
	// 2020-06-08  5227  _d   Hondo mentoring2006b added
	// 2020-06-07  5227  _c   Hondo mentoring2006a added
	// 2020-06-01  5227  _b   Hondo mentoring2005e added
	// 2020-05-24  5227  _a   Hondo mentoring2005d added
	// 2020-05-21  5226  Release candidate
	// 2020-05-21  5225  _s   pre unannounced release candidate
	// 2020-05-13  5225  _r   Latest hondo added mentoring2005c
	// 2020-05-12  5225  _q   # used instead of n
	// 2020-05-12  5225  _p3  debug start up now removed :)
	// 2020-05-12  5225  _p2  dds numbers have boarders
	// 2020-05-11  5225  _p1  TrickFourD... rewritten
	// 2020-05-11  5225  _n8  small adjustment to DDS digit colours (again)
	// 2020-05-10  5225  _n7  hondo mentoring2005b added
	// 2020-05-10  5225  _n6  colours changed on DDS card display (nicer)
	// 2020-05-10  5225  _n5  DDS display on played cards appears done
	// 2020-05-10  5225  _n4  and more
	// 2020-05-10  5225  _n3  more safety working on DDS on played cards
	// 2020-05-09  5225  _n2  more of the same
	// 2020-05-09  5225  _n1  trying to add DDS value for each PLAYED card part 1
	// 2020-05-08  5225  _m   new set of Crazy Slams added
	// 2020-05-05  5225  _k   oldest first and missing hondo2005a
	// 2020-05-05  5225  _j   missing hands now fetched (slowly)
	// 2020-05-04  5225  _h3  and again
	// 2020-05-04  5225  _h2  more safety
	// 2020-05-03  5225  _h1  Safety and latest hondo
	// 2020-05-01  5225  _g   missing fetched tourney hands now does not stop others
	// 2020-04-27  5225  _f   Hondo mentoring2004c added
	// 2020-04-23  5225  _e   includes dev and dates option on use of google sites
	// 2020-04-21  5225  _d   group/all  travellers now regen and-display the analyse if it is on
	// 2020-04-16  5225  _c   Daylong hand set  now decoded correctly
	// 2020-04-15  5225  _b   Challenge hand set now decoded correctly
	// 2020-04-10  5225  _b1  tweek to gsite address
	// 2020-04-06  5225  _a   hondo2004a added
	// 2020-04-01  5224  Release Candidate
	// 2020-03-29  5223  _r   hondo2003e added
	// 2020-03-23  5223  _p   hondo2003d added
	// 2020-03-16  5223  _n   hondo2003c added
	// 2020-03-08  5223  _m   hondo2003b added
	// 2020-03-03  5223  _k   hondo2003a added
	// 2020-02-16  5223  _j5  hondo2002c added
	// 2020-02-12  5223  _j4  removal of more wrongly coded long hyphens from lin files
	// 2020-02-11  5223  _j3  removal of some apostrophes and bad hyphens in wrong code system
	// 2020-02-10  5223  _j2  Tweaking on the ah| ...|  clearing text with the ns checkbox
	// 2020-02-09  5223  _j   extra ah|...|  text lines can now be 'NOT saved' with the  np  checkbox
	// 2020-02-09  5223  _i2  hondo 2002b added
	// 2020-02-09  5223  _i1  mod to the back slash start newline and more such documentation.
	// 2020-02-07  5223  _h   fixed lin decode of dropped hand from tiny url
	// 2020-02-05  5223  _h1  back slash will start a new line in an ah|| command v good for comments
	// 2020-02-05  5223  _g   hondo mentoring2002a   and new Rogers find
	// 2020-02-01  5223  _f   Tourney options documention now complete
	// 2020-01-27  5223  _e   Hondo mentoring 2001d and new "tourny save" options
	// 2020-01-21  5223  _d   Tourney options done but not documentation + hondo mentoring2001c
	// 2020-01-20  5223  _d3  appears to be as wanted - hints to be fixed
	// 2020-01-19  5223  _d2  some re-naming
	// 2020-01-18  5223  _d1  More work on "tourney save" options 
	// 2020-01-17  5223  _c   discard players names option added
	// 2020-01-16  5223  _b   Hondo mentoring2001b added + new BBO format save options
	// 2020-01-06  5223  _a   Hondo mentoring2001a added
	// 2020-01-01  5222  Release Candidate
	// 2020-01-01  5221  _k   pre release build
	// 2019-12-25  5221  _j   New 100 plus and 150 + slams
	// 2019-12-17  5221  _h   Hondo mentoring1912c added
	// 2019-12-09  5221  _g   Hondo mentoring1912b added
	// 2019-12-01  5221  _f   Hondo mentoring1912a added
	// 2019-11-25  5221  _e   Hondo mentoring1911d added
	// 2019-11-20  5221  _d   Hondo mentoring1911c added
	// 2019-11-11  5221  _c   Hondo mentoring1911b added
	// 2019-11-07  5221  _b   less reliance on Web Site
	// 2019-11-05  5221  _a   Hondo mentoring1911a added
	// 2019-11-01  5220  Release Candidate    -   DECLARED COMPLETE
	// 2019-11-01  5219  _u   pre release build
	// 2019-10-21  5219  _t   New charlene PATH and course 1 reordered
	// 2019-10-19  5219  _s   Roger's final pass of "Path to " completed
	// 2019-10-18  5219  _r   Path to advanced doc  back from Charlene
	// 2019-10-17  5219  _p   Trump patterns from r Pav  added
	// 2019-10-11  5219  _n   Misc Squeezes added and French & Chinese Welcome updated
	// 2019-10-08  5219  _m   Path to Advanced doc considered "done"
	// 2019-10-06  5219  _k6  built for java 6 (1.6)  not java 8  (1.8)
	// 2019-10-05  5219  _k5  first try as above
	// 2019-09-28  5219  _k4  Clash squeezes added in
	// 2019-09-25  5219  _k3  Hondo and not much more
	// 2019-09-17  5219  _k2  4 sets now in Books-V  and new set in Books-L
	// 2019-09-16  5219  _k1  Book-S now have Set names
	// 2019-09-16  5219  _j   hondo and work on path to advanced - counting merged in
	// 2019-09-15  5219  _h   safety build
	// 2019-09-13  5219  _g   Path to Advanced very incomplete but added to aaBridge (same as f)

	//			migrated to repo-17

	// 2019-09-13  5219  _f   Path to Advanced very incomplete but added to aaBridge
	// 2019-09-12  5219  _e   saver with path to advanced files not in the build
	// 2019-09-12  5219  _d   Courses are now numbered and some welcome lins removed / moved
	// 2019-09-10  5219  _c   Welcome now includes 'button' for Path to Advanced
	// 2019-09-09  5219  _b   Hondo 1909b added
	// 2019-09-02  5219  _a   Settings
	// 2019-09-02  5218  Release Candidate
	// 2019-09-02  5217  _g3  pre-build
	// 2019-09-01  5217  _g2  Charlene's double squeeze RPav added and  Books-V created
	// 2019-08-30  5217  _g1  Declarer Planning proofed by Charlene
	// 2019-08-29  5217  _f7  Declarer Planning added in
	// 2019-08-29  5217  _f6  safety - not built
	// 2019-08-29  5217  _f5  Suit Comb & Prob proof read  by Charlene
	// 2019-08-28  5217  _f4  Suit Comb and Prob ready for proof reading
	// 2019-08-28  5217  _f3  Charlene's proof read of Techniques added
	// 2019-08-27  5217  _f2  more of the same fix
	// 2019-08-26  5217  _f1  fix to dealNavbar not showing correct highlights while in bidding
	// 2019-08-24  5217  _e9  rc!all! added
	// 2019-08-19  5217  _e8  Hondo 1908c and hold down ctrl key for skip local hondo look up
	// 2019-08-16  5217  _e7  patterns for Ruff-sluff added
	// 2019-08-15  5217  _e6  Try again
	// 2019-08-15  5217  _e5  Build for Charlene
	// 2019-08-14  5217  _e4  techniques removed for safety
	// 2019-08-14  5217  _e3  ruff and ruffing finesse added
	// 2019-08-14  5217  _e2  ruff and ruffing finesse added
	// 2019-08-13  5217  _e1  checkmark added as a symbol
	// 2019-08-12  5217  _e0  techniques removed for safety
	// 2019-08-12  5217  _d9  hondo and latest  techniques
	// 2019-08-10  5217  _d8  all "Techniques" sections removed
	// 2019-08-10  5217  _d7  safety build - trump control has finesse examples still
	// 2019-08-09  5217  _d6  for safety the "4 Techniques" are removed
	// 2019-08-06  5217  _d5  first 4 Techniques now included
	// 2019-08-06  5217  _d4  Techniques removed for dev safety
	// 2019-08-05  5217  _d3  Techniques now showing under Books-S  + Hondo
	// 2019-08-04  5217  _d2  deal navbar now shows preset tricks
	// 2019-08-04  5217  _d1  safety
	// 2019-08-03  5217  _c   Techniques in Books-S menu tidy
	// 2019-08-03  5217  _b   Enter the deal bug fixed and Techniques moved to Books-S
	// 2019-08-03  5217  _a   bs command now shows correct grayed  "grayed" played tricks"
	// 2019-08-03  5215  Release Candidate
	// 2019-08-01  5215  _p4  pre-build
	// 2019-07-31  5215  _p3  Two more throw-in example hands added e6 e7
	// 2019-07-29  5215  _p2  Charlene's changes added
	// 2019-07-29  5215  _p1  Techniques Throw-in  minor edits 
	// 2019-07-29  5215  _m9  Richard Pavlicek Throw-in material added with permission
	// 2019-07-27  5215  _m8  Question marks now do not show on the bidding when inside a deal
	// 2019-07-27  5215  _m7  Roger on Throw-ins is now a Techniques section
	// 2019-07-26  5215  _m6  The Cont button can now be a DDS button
	// 2019-07-24  5215  _m5  'Techniques' added  and scroll into view fixed
	// 2019-07-22  5215  _m4  analyser now shows on flowed incomplete f trick
	// 2019-07-21  5215  _m3  bug fix - analyser stayed on a little too much
	// 2019-07-20  5215  _m2  slides into and out of pbn auto-entered Analyser better
	// 2019-07-20  5215  _m1  AutoEnter no-longer forced off between pbn files
	// 2019-07-17  5215  _k   Roger's Find 31  ::: jump to lin file added
	// 2019-07-16  5215  _j   doc tidy to match Counting declarer rename 2
	// 2019-07-16  5215  _h   Counting declarer rename
	// 2019-07-14  5215  _g   PaP revamped and documented can also peek Opps
	// 2019-07-13  5215  _e   Peek at Partner - reasonable
	// 2019-07-13  5215  _d4  Again
	// 2019-07-13  5215  _d3  Peek at Partner working but not TIDY 
	// 2019-07-13  5215  _d2  Safety
	// 2019-07-12  5215  _d1  Peek on Partner buttons added but no actions yet
	// 2019-07-08  5215  _c   Central bidding not showing is hands incomplete in tutorial - bug fix
	// 2019-07-08  5215  _b   will now take '10' not just 'T' as a rank (in MD cmd) and  Hondo mentoring 1907a added
	// 2019-07-04  5215  _a   Hondo's  Weak 1NT reformatted - pdf's removed
	// 2019-07-02  5214  Release Candidate
	// 2019-07-02  5213  _j   pre-build
	// 2019-06-12  5213  _h   hondo mentoring1906b added
	// 2019-06-11  5213  _g   150 Crazy Slams Added
	// 2019-06-10  5213  _f   clean-ups of OCP
	// 2019-06-04  5213  _e   hondo mentoring1906a and automatic Squ example added
	// 2019-05-29  5213  _d   hondo mentoring1905d added and Roger's Find # 30
	// 2019-05-20  5213  _c   hondo mentoring1905c added
	// 2019-05-13  5213  _b   AnnMortons corrections to - Easy Counting Practice doc
	// 2019-05-13  5213  _a   hondo mentoring1905b added
	// 2019-05-06  5212  Release Candidate
	// 2019-05-06  5211  _s6  Pre build
	// 2019-05-06  5211  _s5  Hondo and near done
	// 2019-05-03  5211  _s4  Multi Save Separated added
	// 2019-05-02  5211  _s3  not sure
	// 2019-05-01  5211  _s2  counting practice RHO LHO - just need doc and video !!!!
	// 2019-04-30  5211  _s1  100 plus RHO LHO now have hot seat always south
	// 2019-04-29  5211  _r9  RHO LHO now 133 and 144 hands each
	// 2019-04-29  5211  _r8  Books-L 'nnn hands' not now under git
	// 2019-04-27  5211  _r7  200 hands rho  and 200 hands lho added
	// 2019-04-23  5211  _r6  bergen samples non gen version added and Ariks made visible
	// 2019-04-22  5211  _r5  Hondo and aapg detected issues fixed in lin files
	// 2019-04-21  5211  _r4  Problems for New Players tidy up finished
	// 2019-04-20  5211  _r3  Join Lin files button added   Claim button  restored
	// 2019-04-19  5211  _r2  Study mode now not so fierce and more work on - Probs for new players
	// 2019-04-18  5211  _r1  Fix to rotate bug (Charlene bug report) and bi bot instruction fix
	// 2019-04-17  5211  _p9  note _p8 was saved late
	// 2019-04-17  5211  _p8  More moved in to the public sections and ndf= 
	// 2019-04-16  5211  _p7  PFNP  Study info added
	// 2019-04-16  5211  _p6  Hondo added
	// 2019-04-15  5211  _p5  Charlene's input and new player stuff cleaned
	// 2019-04-14  5211  _p4  Problems for new players (section) added
	// 2019-04-10  5211  _p3  Charlene's changes to LTPB2 1.2 added
	// 2019-04-10  5211  _p2  More highlight card and lowlight card/suit removed
	// 2019-04-09  5211  _p1  Some bot hints added to LTPB2 and fix
	// 2019-04-09  5211  _n9  highlights and lowlights removed from LTPB1 and 2
	// 2019-04-09  5211  _n8  os and hz cmds added va cmd supported all documented 
	// 2019-04-08  5211  _n7  More on old hondo links
	// 2019-04-07  5211  _n6  Watsons renamed
	// 2019-04-07  5211  _n5  Hondo links now (almost) all forced internal 
	// 2019-04-07  5211  _n4  Build before working on hondo
	// 2019-04-06  5211  _n3  ltpb2  parse complete
	// 2019-04-05  5211  _n2  6 and 7 left to do
	// 2019-04-05  5211  _n1  upto 4.2 on LTPB2
	// 2019-04-04  5211  _k9  Yet again fixing the cards display bug
	// 2019-04-03  5211  _k8  more Charlene fixes to LTPB1
	// 2019-04-03  5211  _k7  Beginners link to LTPB1 in welcome menu
	// 2019-04-02  5211  _k6  more bidding panel display correction
	// 2019-04-02  5211  _k5  ltpb1 - Most bolding removed 
	// 2019-04-02  5211  _k4  ltpb1 Before BOLD removal
	// 2019-04-02  5211  _k3  highlighted (alerted) floating bids better size
	// 2019-04-02  5211  _k2  ltpb1 completed first parse
	// 2019-04-02  5211  _k1  "floating" Hands now displayed with tutorial background color
	// 2019-03-29  5211  _j   hondo   and   bug fix from _e  (cards show at start of bidding)
	// 2019-03-29  5211  _h   unknown command "do" aka "dO" ignored what it ???
	// 2019-03-29  5211  _g   page number display now stays on right
	// 2019-03-29  5211  _f   os  overlapShrink added  (cmd)
	// 2019-03-26  5211  _e   bug fix cards not showing on table and LTPB1 work
	// 2019-03-26  5211  _d   ltpb2 added
	// 2019-03-18  5211  _c   inverted minors
	// 2019-03-17  5211  _b   hondo
	// 2019-03-10  5211  _a   Settings and Hondo
	// 2019-03-10  5210  Release Candidate
	// 2019-03-10  5209  _c   pre pre release
	// 2019-03-06  5209  _b   Getting new to 5.1 release

	//          version number increased to 5.1.0.

	// 2019-03-06  5208  for making videos with a 'nice' name
	// 2019-03-06  5207  _j4  Ariks cleaned up 
	// 2019-03-04  5207  _j3  hondo added, new doc is about OK - videos needed
	// 2019-03-02  5207  _j2  has new  BBO to aab doc
	// 2019-03-01  5207  _j1  Working on the docs
	// 2019-02-28  5177  _h9  Sort of stable
	// 2019-02-27  5177  _h8  multi entry file menu  but not clear so will change
	// 2019-02-27  5177  _h7  Auto Saves (still there) but no longer used by anything
	// 2019-02-24  5177  _h6  Stability approaches
	// 2019-02-24  5177  _h5  better menu names
	// 2019-02-24  5177  _h4  Only one aaBridge is now the master poller
	// 2019-02-24  5177  _h3  now polls for changes to the html files in the downloads folder
	// 2019-02-23  5177  _h2  trTm now has a new name (more of the same)
	// 2019-02-23  5177  _h1  Fixed teams (two html versions of) issue
	// 2019-02-22  5177  _g   travlers and Teams and std MyHands now all supported in java
	// 2019-02-21  5177  _g8  trav score now almost reasonable
	// 2019-02-20  5177  _g7  teams starting to work in vg display mode
	// 2019-02-20  5177  _g6  Download folder now can be chosen
	// 2019-02-19  5177  _g5  extractMyHands revision continues
	// 2019-02-18  5177  _g4  safety new extractMyHands getting better and Hondo
	// 2019-02-18  5177  _g3  new built-in Extract my hands is now starting to come to life
	// 2019-02-16  5177  _g2  mass formatting  because of moving back to eclipse neon 4.6.3
	// 2019-02-16  5177  _g1  jsoup flickering into life
	// 2019-02-11  5177  _f   starting to add jsoup
	// 2019-02-11  5177  _e   Hondo mentoring added
	// 2019-01-31  5177  _d   Angel Blue now all converted to LHO RHO
	// 2019-01-20  5177  _c   3rd Hondo
	// 2019-01-16  5177  _b   2nd Hondo of 2019
	// 2019-01-06  5177  _a   First Hondo of 2019
	// 2019-01-02  5176  Release Candidate
	// 2018-12-30  5175  _n   Final Hondo of 2018 added
	// 2018-12-27  5175  _j   Arik
	// 2018-12-27  5175  _h   Arik
	// 2018-12-11  5175  _g   Small tweeks to the php using packages
	// 2018-12-05  5175  _f   Angel Blue 2018 up to wk 33
	// 2018-12-02  5175  _e   Hondos
	// 2018-11-27  5175  _d   Hondos
	// 2018-11-07  5175  _c   corrections to arik  2018   121  numbering
	// 2018-11-04  5175  _b   Some Arik renumbering for Sanya
	// 2018-10_28  5175  _a   Hondo mentoring
	// 2018-10_27  5174  Release Candidate
	// 2018-10_27  5173  _n   doc from Charlene
	// 2018-10_23  5173  _k   deal and merge flyer  updated
	// 2018-10_22  5173  _j7  aadm doc back from Charlene and dealer scripts doc created
	// 2018-10_18  5173  _j5  aadm comming along 
	// 2018-10_17  5173  _j4  Leading against NT scripts added
	// 2018-10_15  5173  _j3  Charlene's corrections to Size Position GRID added
	// 2018-10_14  5173  _j2  Hondo and Size Position GRID
	// 2018-10_12  5173  _j1  Starting on cleaning up aa_dm use of php much to document
	// 2018-10_11  5173  _h9  Documentation added for the "main panel width"
	// 2018-10_10  5173  _h8  final touches to "main panel width"
	// 2018-10_10  5173  _h7  cleaned up and tooltip added
	// 2018-10_09  5173  _h6  width setting added (mainly for linux)
	// 2018-10_09  5173  _h5  non growing frp (main panel) now fixed
	// 2018-10_09  5173  _h4  'fiddle' added to  AaOuterFrame.java  (line 595 ish)  but not used
	// 2018-10_07  5173  _h3  Hondo and ah comma fix
	// 2018-09_28  5173  _h2  deal_and_merge now stable
	// 2018-09_26  5173  _h1  'pre' button moved - first aadm zip inluded
	// 2018-09_18  5173  _g   names settled down
	// 2018-09_18  5173  _f   kib seat now getting from aadm to aaBridge
	// 2018-09_14  5173  _e   more adjustments
	// 2018-09_14  5173  _d   more to do with merge compatibility
	// 2018-09_04  5173  _c   save as pbn button now visible to all
	// 2018-09_04  5173  _b   Left col now fills space
	// 2018-09_04  5172  Release Candidate
	// 2018-09_04  5171  _j   sort issue now fixed (again) !
	// 2018-09_04  5171  _h   1st ldp dealer scripts - also - OCP scripts has 1N escape added
	// 2018-09_03  5171  _g   dlae dummy show bug fixed ! and ldp 1N dbl by opps
	// 2018-09_03  5171  _f   ldp precision 1N added to scripts
	// 2018-09_03  5171  _e   4th attempt // this time apply defaults only sets south to be you seat when inside a deal or is vugraph
	// 2018-09_03  5171  _d   third attempt
	// 2018-09_03  5171  _c   Another attempt at the bug fix
	// 2018-09_01  5171  _b   Hondo added and "bug fix on bad initial you seat and "
	// 2018-09_01  5171  _a   settings
	// 2018-09_01  5170  Release Candidate
	// 2018-09_01  5169  _x3  Endplays  6 now done
	// 2018-08_31  5169  _x2  Arik Endplays complete
	// 2018-08_30  5169  _x1  Better tool tips
	// 2018-08_30  5168  Release candidate  (pseudo)
	// 2018-08_30  5167  _v8  lin files a starter doc   cleaned up
	// 2018-08_29  5167  _v7  Tooltips can now be on multi lines
	// 2018-08_28  5167  _v6  What does on inside a lin file clean up
	// 2018-08_28  5167  _v5  Small cleaning - lin files a starter
	// 2018-08_28  5167  _v4  deleted more old dead (commented out) code
	// 2018-08_28  5167  _v3  dlae seat now shows when in tutorial mode
	// 2018-08_28  5167  _v2  much cleaning of commented out code 
	// 2018-08_28  5167  _v1  endplays 2 and copy doc collection now shows messages
	// 2018-08_27  5167  _u9  Practice lin  is now  Practice 1827 lin  
	// 2018-08_27  5167  _u8  amber green & red green + better menus all very clean
	// 2018-08_26  5167  _u7  Help and How do I tided-up
	// 2018-08_26  5167  _u6  amber and square red option on right hand panel
	// 2018-08_26  5167  _u5  Safety again as sent to Steve
	// 2018-08_25  5167  _u4  plus charlenes doc
	// 2018-08_24  5167  _u3  Defend like an expert updated
	// 2018-08_24  5167  _u2  Play  Kib  DDS  Analyse  and  Shuf-Op.doc  added
	// 2018-08_23  5167  _u1  Changes to dlae (dlae active and respect lin you need merging)
	// 2018-08_22  5167  _s9  More cleanUps to  Kib se-at panel
	// 2018-08_21  5167  _s8  Practice on BBO and with aaBridge rewritten
	// 2018-08_21  5167  _s7  with java version in show options and arik endplay changes
	// 2018-08_20  5167  _s6  same as s5 - built after install testing
	// 2018-08_20  5166  _s5  Small tweaks
	// 2018-08_20  5165  _s4  Twisters reasonable 
	// 2018-08_19  5165  _s3  starts from now show Bidding and play as options
	// 2018-08_19  5165  _s2  many OLD Kib Seat settings commented out (were previously hidden)
	// 2018-08_19  5164  _s1  test build of 5.0    to test the new inno scripts compiler
	// 2018-08_19  5162  _s1  test build of 5.0

	//          version number increased to 5.0.0.
	
	// 2018-08_18  3169  _r9  Playing a Hand  doc started
	// 2018-08_18  3169  _r8  Show Hide in Left column now two "radio buttons"
	// 2018-08_18  3169  _r7  Name changes of file and variables to match
	// 2018-08_18  3169  _r6  Seat Choice now has text of  Kib Seat  (no name changes - yet)
	// 2018-08_17  3169  _r5  Seat Choice cleaned up
	// 2018-08_17  3169  _r4  new rotation now in the docs
	// 2018-08_16  3169  _r3  fixed bug in bottom panel enables
	// 2018-08_16  3169  _r2  Save as PBN now restored but as DEV mode only1X it were my doing
	// 2018-08_16  3169  _r1  New multisave and Rotate layout works
	// 2018-08_15  3169  _p9  tiny tweak to to OCP 14.2
	// 2018-08_15  3169  _p8  Identical to p7  p7 p7
	
	//			migrated to  repo-16   (after crash)
	
	// 2018-08_15  3169  _p7  Identical to p6  p6 p6
	// 2018-08_15  3169  _p6  new welcome lins added
	// 2018-08_14  3169  _p5  Lesson 11 and 12 of basics and more changes to Doc Collection
	// 2018-08_14  3169  _p4  'a how to' removed from names
	// 2018-08_13  3169  _p3  Doc Collection much changed
	// 2018-08_12  3169  _p2  More cleaning on Endplays
	// 2018-08_12  3169  _p1  Endplays and archive now part ziped
	// 2018-08_11  3169  _n9  Ariks Endplays now included
	// 2018-08_08  3169  _n8  VuGraph and 'Interesting' Deals  lesson added
	// 2018-08_07  3169  _n7  Bookmarks done in lefthand column again
	// 2018-08_07  3169  _n6  Bookmarks done in lefthand column
	// 2018-08_05  3169  _n5  lesson 5 added
	// 2018-08_03  3169  _n4  lesson 1 of aaBridge basics added
	// 2018-08_02  3169  _n3  safety
	// 2018-08_02  3169  _n2  welcome much rewritten
	// 2018-08_01  3169  _n1  More of the same
	// 2018-07-31  3169  _k   More on the new - new user introduction
	// 2018-07-30  3169  _h   Much cut from the Welcome menu
	// 2018-07-29  3169  _g   hondo WeakNT sorted out
	// 2018-07-29  3169  _f   Extra Settings on 'Seat Choice' now hidden 
	// 2018-07-19  3169  _e   md should default to south and not inherit dealer dir
	// 2018-07-18  3169  _d   Lin file Course added v1.10 added
	// 2018-07-09  3169  _c   prob_fmt_req_numb  added
	// 2018-07-08  3169  _b   Hondo and lin changes
	// 2018-07-03  3169  _a   Settings
	// 2018-07-03  3168  Release Candidate

	
	static String built = y + " " + m + " " + d;

	static String status = "  built   " + built + "   ";
	
	static Date dateBuilt;
	static Date dateExpires;
	
	public static int getProbFmtReqNumber() {		
		return prob_fmt_req_numb;
	}

	public VersionAndBuilt() { // constructor
		int im = -1;
		while (true) {
			if (m == "Jan") { im = Calendar.JANUARY; break; }
			if (m == "Feb") { im = Calendar.FEBRUARY; break; }
			if (m == "Mar") { im = Calendar.MARCH; break; }
			if (m == "Apr") { im = Calendar.APRIL; break; }
			if (m == "May") { im = Calendar.MAY; break; }
			if (m == "June") { im = Calendar.JUNE; break; }
			if (m == "July") { im = Calendar.JULY; break; }
			if (m == "Aug") { im = Calendar.AUGUST; break; }
			if (m == "Sept") { im = Calendar.SEPTEMBER; break; }
			if (m == "Oct") { im = Calendar.OCTOBER; break; }
			if (m == "Nov") { im = Calendar.NOVEMBER; break; }
			if (m == "Dec") { im = Calendar.DECEMBER; break; }
			break;
		}
		assert (im != -1);

		int iy = Aaa.extractPositiveInt(y);

		int id = Aaa.extractPositiveInt(d);
		assert (1 <= id && id <= 31);
		
		Calendar cal = Calendar.getInstance();
		
	    cal.set(Calendar.YEAR, iy);
	    cal.set(Calendar.MONTH, im);
	    cal.set(Calendar.DAY_OF_MONTH, id);
	
		dateBuilt = cal.getTime();
		
		int vaild_for_days = 60; 
		
		cal.add(Calendar.DATE, vaild_for_days);
		
		dateExpires = cal.getTime();
	}

	public static String verAndBuildNo() {
		return ver + buildNo + devExtra;
	}

	public static String getStatus() {
		return status;
	}

	public static String getVer() {
		return ver;
	}

	public static String getBuildNo() {
		return "" + buildNo + devExtra;
	}

	public static String getBuilt() {
		return built;
	}

}
