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
import com.rogerpf.aabridge.controller.App;

//@formatter:off

public class VersionAndBuilt {
	static String ver = "4.0.0.";  public static String devExtra = "";    // extra is dev only, normally null

	public static int buildNo = 3140;    static String y = "2017", m = "July", d = "17";


	// 2017-07-17  3140  Release Candidate
	// 2017-07-17  3139  _n   last tweak
	// 2017-07-17  3139  _m   Hondo added
	// 2017-07-13  3139  _k   Tweeks to OCP and aaB vid link
	// 2017-07-12  3139  _j   Changes to the main menu eg OCP AGAIN
	// 2017-07-12  3139  _h   links to users group etc added
	// 2017-07-12  3139  _g   OCP 410 1D-1H added
	// 2017-07-05  3139  _f   OCP clean up
	// 2017-07-05  3139  _e   Hondo and score position tweak
	// 2017-07-05  3139  _d   AVE fixed in extract my hands
	// 2017-07-05  3139  _c   Added link to new Announcement group
	// 2017-07-05  3139  _b   Add menu item to archive on Google Drive
	// 2017-07-05  3139  _a   settings
	// 2017-07-04  3138  Release Candidate   (fixed non deletion of  cmds and php  folder)
	// 2017-07-04  3137  _b   settings
	// 2017-07-04  3136  Release Candidate
	// 2017-07-04  3135  _m   pre release
	// 2017-07-03  3135  _k   updated extract myhands doc from Charlene
	// 2017-07-03  3135  _j   tiny fix
	// 2017-07-03  3135  _h   BBO MyHands will now go directly into the deal my default (vr)
	// 2017-07-02  3135  _g   Hondo added
	// 2017-07-02  3135  _f   extract Myhands documented
	// 2017-07-02  3135  _e   now   cmds_and_scripts
	// 2017-07-01  3135  _d   Small adjustments and Hondo
	// 2017-06-24  3135  _c   now uses new name for php_script
	// 2017-06-24  3135  _b   php run by aaBridge on mac
	// 2017-06-24  3135  _a   Settings
	// 2017-06-23  3134  Release Candidate
	// 2017-06-23  3133  _p   last tweak
	// 2017-06-22  3133  _n   Can now invoke  aamh.cmd  from  menu
	// 2017-06-21  3133  _m   fixed bug in rt rotate command  & Sanja's updates
	// 2017-06-21  3133  _k   right click on home re-reads last dropped single folder
	// 2017-06-20  3133  _j   vr command added
	// 2017-06-19  3133  _h   launch with messages .cmd file added
	// 2017-06-19  3133  _g   passed through g trans
	// 2017-06-19  3133  _f   Clean ups to changed languages (pre go ogle trans processing)
	// 2017-06-18  3133  _e   Sanya's languages added in (inc Croatian) raw 
	// 2017-06-18  3133  _d   Missing spellings in en_US language file fixed
	// 2017-06-18  3133  _c   Added work around for the no suit symbols buttons when NOT en_US
	// 2017-06-16  3133  _b   Fixes to language
	// 2017-06-15  3133  _a   Settings
	// 2017-06-15  3132  Release Candidate
	// 2017-06-15  3131  _f   Updated Hand eval Matrix in roger's finds
	// 2017-06-14  3131  _e   fixed History bug - internal links and jumps not recorded correctly
	// 2017-06-12  3131  _d   Diagnostics added for Charlene when in an md
	// 2017-06-12  3131  _c   changes to the display of the DDS tricks
	// 2017-06-12  3131  _b   Shuf Op doc added
	// 2017-06-11  3131  _a   Settings
	// 2017-06-11  3130  Release Candidate
	// 2017-06-11  3129  _p   Some stability
	// 2017-06-11  3129  _p_2   More Safety
	// 2017-06-11  3129  _p_1   Safety
	// 2017-06-11  3129  _n   tests changed to use as base point
	// 2017-06-10  3129  _m   dumb auto lead improved again
	// 2017-06-09  3129  _k   tt cmd "tidy trick" improved
	// 2017-06-09  3129  _j   pencil now never hidden
	// 2017-06-08  3129  _h   Fixed the bidding bug
	// 2017-06-08  3129  _g   quick lin edit doc added to doc collection
	// 2017-06-08  3129  _f   mac now uses built in editor for same
	// 2017-06-08  3129  _e   Edit lin (pencil) now will also edit a saved DEAL after the save.
	// 2017-06-07  3129  _d   Dropped or right clicked text is tested to see if a linfile before being saved
	// 2017-06-06  3129  _c   Change to dumb auto less leading of king into tenace
	// 2017-06-06  3129  _b   Hints - contract needed  and  defence display size - fixed
	// 2017-06-05  3129  _a   settings
	// 2017-06-04  3128  Release Candidate
	// 2017-06-04  3127  _a   tinyurl bug fixed
	// 2017-06-04  3126  Release Candidate
	// 2017-06-04  3125  _m   final fix and hondos
	// 2017-06-04  3125  _k   movie fix
	// 2017-06-04  3125  _j   with mac fix
	// 2017-06-04  3125  _h   left on mac
	// 2017-06-03  3125  _g   Looks OK
	// 2017-06-03  3125  _f   tried to fix bugs in drag drop added more
	// 2017-06-03  3125  _e   Most read able links and now be drag and drop or right clicked
	// 2017-05-29  3125  _d   Right click to paste now works on tutorial panel as well (and phoney)
	// 2017-05-29  3125  _c   Change to dumb auto so it works for a particular hand 6506
	// 2017-05-28  3125  _b   hondo files
	// 2017-05-28  3125  _a   Settings
	// 2017-05-27  3124  Release Candidate
	// 2017-05-27  3123  _h   Changes to Roger on Squeezes
	// 2017-05-26  3123  _g   tweeks
	// 2017-05-26  3123  _f   Appears OK
	// 2017-05-26  3123  _e   Improvements
	// 2017-05-24  3123  _d   A B C button documented in the welcome screen layout lin
	// 2017-05-24  3123  _c   working but needs documentation
	// 2017-05-24  3123  _b   Lin Edit button  is now a pencil
	// 2017-05-23  3123  _a   save settings
	// 2017-05-23  3122  Release Candidate
	// 2017-05-23  3121  _q   Found the pre shown single deals with play walked into break - bug
	// 2017-05-22  3121  _p   fixed the bug --- later --- but had NOT
	// 2017-05-22  3121  _n   better but
	// 2017-05-22  3121  _m   hist fwd back icons added
	// 2017-05-22  3111  _l   number jump
    // ..................................
	// 2017-05-22  2957  _k   Language files updated for new buttons
	// 2017-05-22  2957  _j   Home button now an font icon
	// 2017-05-21  2957  _h   Appears OK
	// 2017-05-20  2957  _g   hondo and safety
	// 2017-05-20  2957  _f   Saves only the last position in a file
	// 2017-05-20  2957  _e   local files sort of have a history
	// 2017-05-20  2957  _d   hist back and hist Fwd added only works for first Welcome "book"
	// 2017-05-20  2957  _c   hist buttons added but do nothing
	// 2017-05-20  2957  _b   Post repo change build

	//			migrated to  repo-13

	// 2017-05-20  2957  _a   Dev build pre-repo change
	// 2017-05-15  2956  Release Candidate
	// 2017-05-15  2955  _m   Assertion commented out appears not to be an issue
	// 2017-05-15  2955  _k   Enter the deal override moved up and Hondo
	// 2017-05-13  2955  _j   shift tweak also added 7 pixel adjust
	// 2017-05-13  2955  _h   ctrl & home button now moves app to top left of current screen
	// 2017-05-12  2955  _g   mn-header colour can now be random or set to dark blue
	// 2017-05-11  2955  _f   devs can stop rand mn-header color
	// 2017-05-11  2955  _e   and More
	// 2017-05-10  2955  _d   More de-cluttering
	// 2017-05-09  2955  _c   De-cluttered the welcome page
	// 2017-05-07  2955  _b   Auto button text resizing added only used when NOT en_US
	// 2017-05-07  2955  _a   settings and new Hondo
	// 2017-05-06  2954  Release Candidate   java 6 bug fix
	// 2017-05-06  2953  _k   Changes from Sanja in the Portuguese translation
	// 2017-05-06  2953  _j   red box around "welcome youtube" (new vids have been made)
	// 2017-05-04  2953  _h   More tweeks of the language files
	// 2017-05-03  2953  _g   big message display in international font improved
	// 2017-05-02  2953  _f   slightly improved vugraph handling
	// 2017-05-01  2953  _e   better Turkish Translation added (partial)
	// 2017-05-01  2953  _d   bet ter loa der. now not closed in java 6
	// 2017-04-29  2953  _c   Added to Roger on squeezes
	// 2017-04-29  2953  _b   tt command tidy trick added  needs testing
	// 2017-04-28  2953  _a   marker
	// 2017-04-28  2952  Release Candidate
	// 2017-04-28  2951  _h   additional depth option added
	// 2017-04-27  2951  _g   3 Club OCP tutorial complete and added.
	// 2017-04-26  2951  _f   2nd try at fix  en_US is now in properties filename
	// 2017-04-23  2951  _e   Fix of the "always English" problem  thank you Sanya
	// 2017-04-23  2951  _d   Bugs fixed and part of OCP 3C added
	// 2017-04-23  2951  _c   Possibly fixed bid display bug also chasing question language deficiencies
	// 2017-04-23  2951  _b   another 18 languages added
	// 2017-04-23  2951  _a   Initial values
	// 2017-04-22  2950  Release Candidate   -   Google translated languages
	// 2017-04-22  2949  _o   Added green splash if deal is not as requested
	// 2017-04-22  2949  _n   Revised the Localisation doc
	// 2017-04-21  2949  _m   Menu layout much improved
	// 2017-04-21  2949  _k   language selection now working - instruction in lang files
	// 2017-04-21  2949  _j   Language files all updated with reasonable AKQJ
	// 2017-04-20  2949  _i   lang menu cleanups
	// 2017-04-20  2949  _h   language menu again visible when expanded 
	// 2017-04-20  2949  _f   Language menu fit for release
	// 2017-04-19  2949  _e   properties renamed and lang keys directly read
	// 2017-04-18  2949  _d   Now matched to new lang files produced by aaTranslate
	// 2017-04-17  2949  _c   More menu and i18n cleanups removing many < and > 
	// 2017-04-16  2949  _b   game and table 'values' now separated
	// 2017-04-16  2949  _a   Hondo lins
	// 2017-04-11  2948  Release Candidate  -  with internationalisation v 4 . 0
	// 2017-04-11  2947  _t_9   changed to version 4 . 0
	// 2017-04-11  2947  _t_8   build for testing 1
	// 2017-04-10  2947  _t_7   build for Charlene
	// 2017-04-10  2947  _t_6   Safe-Y (Books-Y) now only in the doc collection
	// 2017-04-10  2947  _t_5   includes Safe-Y (for now) same again  nasty menu bug found and fixed
	// 2017-04-10  2947  _t_4   includes Safe-Y (for now)
	// 2017-04-10  2947  _t_3   A few tidy ups
	// 2017-04-09  2947  _t_2   Added make all language lin variants visible
	// 2017-04-08  2947  _s_9   doc save and zip linzip bug now diagnosed
	// 2017-04-08  2947  _s_8   more doc written
	// 2017-04-08  2947  _s_7   Minor Menu errors fixed
	// 2017-04-08  2947  _s_6   U moved to X
	// 2017-04-07  2947  _s_5   some i18n missed entries added
	// 2017-04-07  2947  _s_4   i18n messages all in lang file
	// 2017-04-06  2947  _s_3   bottom tab i18n
	// 2017-04-05  2947  _s_2   all tab panels now i18n
	// 2017-04-05  2947  _s_1   Seat Choice now i18n
	// 2017-04-05  2947  _r_9   Seat Choice now i18n
	// 2017-04-03  2947  _r_8   two big hints completed
	// 2017-04-03  2947  _r_7   progress on i18n
	// 2017-04-03  2947  _r_6   Options menu i18n'ed
	// 2017-04-02  2947  _r_5   Button table i18n'ed and Hondo
	// 2017-04-02  2947  _r_4   navbar language support
	// 2017-04-02  2947  _r_3   fix 86 68 typo
	// 2017-04-02  2947  _r_2   More translation keys added
	// 2017-04-01  2947  _r_1   For clean issue
	// 2017-04-01  2947  _p_9   Fix menu mis-display bug
	// 2017-04-01  2947  _p_8   notepad checked for in multiple places
	// 2017-04-01  2947  _p_7   can now add dot string to developing language files and Jack lead fix
	// 2017-04-01  2947  _p_6   lines for the file menu 
	// 2017-03-30  2947  _p_5   Bug fixed filename error in bundles
	// 2017-03-30  2947  _p_4   Better deck and language files and started on Localisation doc
	// 2017-03-29  2947  _p_3   Menu example done - now need to write the documentation etc
	// 2017-03-29  2947  _p_2   Hondo added
	// 2017-03-29  2947  _p_1   Self restart added
	// 2017-03-28  2947  _n_9   The menu section moved   to  A a f.Menus
	// 2017-03-28  2947  _n_7   More fixes
	// 2017-03-28  2947  _n_6   A a f  split from A a p
	// 2017-03-28  2947  _n_5   bug fixes and more   g T
	// 2017-03-28  2947  _n_4   code changed to match
	// 2017-03-28  2947  _n_3   Books-1  now back to Books
	// 2017-03-28  2947  _n_2   All books restored with cap B (books ends in a 1 for now
	// 2017-03-28  2947  _n_1   All books deleted
	// 2017-03-27  2947  _m_9   More work on menus Help and How do I 
	// 2017-03-27  2947  _m_8   Completed trick panel now internationalised
	// 2017-03-27  2947  _m_7   NSEW and Dealer letter now working in most places
	// 2017-03-27  2947  _m_6   orig Font refernces now removed
	// 2017-03-27  2947  _m_5   Started to deal with Orig fonts
	// 2017-03-27  2947  _m_4   has-Uni extended to char bug fix in international played card display
	// 2017-03-27  2947  _m_3   Bridge fonts now converged down to three + International
	// 2017-03-26  2947  _m_2   Combining the fonts - now can remove unwanted
	// 2017-03-26  2947  _m_1   Need to keep transition of fonts to keep mac looking good
	// 2017-03-26  2947  _k_8   Books Y and Z now   Risky and Safe
	// 2017-03-25  2947  _k_7   unreleased books now all in BooksU  Unreleased   and zip issued fied with -mru-on  on 7z cmd line
	// 2017-03-25  2947  _k_6   2nd Way of adding unicode latin titles added
	// 2017-03-25  2947  _k_5   safety
	// 2017-03-25  2947  _k_4   card deck reasonable settled
	// 2017-03-24  2947  _k_3   makeshift demo Russian cards now showing
	// 2017-03-24  2947  _k_2   font file now separated
	// 2017-03-24  2947  _k_1   more on cleaner fonts
	// 2017-03-24  2947  _i_7   Symbol and Face fonts  now separated 
	// 2017-03-23  2947  _i_6   Double and Redouble now X and XX
	// 2017-03-23  2947  _i_5   All utf-8 removed from filenames and now it will rezip hooray
	// 2017-03-23  2947  _i_4   Additional format for alternate names being added
	// 2017-03-23  2947  _i_3   menu examples Kippling added
	// 2017-03-23  2947  _i_2   for clarity
	// 2017-03-23  2947  _i_    'chapt er marks' now called book-marks
	// 2017-03-23  2947  _h  current BooksZ is good will run from jar lots of chinese but no brackets (braces)
	// 2017-03-22  2947  _g  French has SA not NT  and Menu button gone now Chaptermarks on menubar
	// 2017-03-20  2947  _e  Cleaned up a bit
	// 2017-03-20  2947  _d  Alternate per-Language based linfiles are now work
	// 2017-03-19  2947  _c  Started adding Internationalisation of the buttons 
	// 2017-03-19  2947  _b  Darker in deal option added
	// 2017-03-18  2947  _a  set to next numb
	// 2017-03-18  2946  Release Candidate   includes 'inter-esting' deals feature
	// 2017-03-18  2945  _v  Transferring the Guard  now added
	// 2017-03-17  2945  _u  dumbAuto defence now covers in 2nd seat to promte partner
	// 2017-03-17  2945  _s  Interesting deals link now in Play Bridge button
	// 2017-03-17  2945  _r  corrections
	// 2017-03-15  2945  _q  More of   R on Squ and nearly ready for candidate
	// 2017-03-15  2945  _p  user can now clear an area of the screen using wh as bg color
	// 2017-03-15  2945  _n  Interesting deals feature complete
	// 2017-03-11  2945  _m  stop Assistance After added to dumbAuto interesting + honds
	// 2017-03-11  2945  _k  more tweaks but no real changes
	// 2017-03-10  2945  _i  tweaks to interesting hands
	// 2017-03-10  2945  _h  first dev outing of interesting hands 
	// 2017-03-10  2945  _g_06 dumb auto now agrees with its self 
	// 2017-03-10  2945  _g_05 DDS tricks now also calculated for deal
	// 2017-03-10  2945  _g_04 Tried to stop lead of the king in dumb Auto
	// 2017-03-10  2945  _g_03 Keep Analyser on now works for new Main Board
	// 2017-03-09  2945  _g_02 work on inter hands + stuff op now respects show loc hid policy
	// 2017-03-08  2945  _g_01 starting on 'interesting hands'
	// 2017-03-08  2945  _f  release to Yuchong
	// 2017-03-08  2945  _e  player name area numb info visibility now called pi
	// 2017-03-08  2945  _d  bugs fixed so it works in .jars
	// 2017-03-07  2945  _c  Version with Chinese  FAKE help added as BooksZ
	// 2017-03-07  2945  _b  px (now pi) command added  suppresses hand player names numbers info
	// 2017-03-07  2945  _a  set to next numb
	// 2017-03-06  2944  Release Candidate
	// 2017-03-06  2943  _d  Pre-candidate build
	// 2017-03-06  2943  _c  Supports Hondo new style external link with comma
	// 2017-03-05  2943  _b  BUG in Distrbution Training Questions FIXED
	// 2017-03-01  2943  _a  set to next numb
	// 2017-03-01  2942  Release Candidate
	// 2017-03-01  2941  _n  cleaning
	// 2017-03-01  2941  _m  Final cleaning of Chinese UTF-8 file
	// 2017-02-28  2941  _k  Hondo added (no Kantar Quiz this week)
	// 2017-02-28  2941  _j  Finaly a tollerable build for international text
	// 2017-02-28  2941  _i  International text now stable
	// 2017-02-28  2941  _h  Doc improved for international text
	// 2017-02-28  2941  _g  Exactly as below International font support added
	
	//			migrated to  repo-12

	// 2017-02-28  2941  _f_09  warnings were no real issue now fixed
	// 2017-02-28  2941  _f_08  formatting bug fixed BUT  still java warnings to fix
	// 2017-02-28  2941  _f_07  All commands now support International characters BUT bug in at formatting lost
	// 2017-02-28  2941  _f_06  Mn header OK but always uses java font
	// 2017-02-28  2941  _f_05  Questions now working 
	// 2017-02-27  2941  _f_04  questions still broken but Chinese UTF-8.lin  written
	// 2017-02-26  2941  _f_03  good text but questions in a mess
	// 2017-02-26  2941  _f_02  play char now displayed NOT real thing :)
	// 2017-02-25  2941  _f_01  chars now getting to output encoder
	// 2017-02-25  2941  _f     no change from e
	//
	//         migrated to  repo-11
	//
	// 2017-02-25  2941  _e   change the up to default to 6
	// 2017-02-24  2941  _d   tweak to upto 
	// 2017-02-23  2941  _c   Low card shuffle also added
	// 2017-02-23  2941  _b   suit swap added (not low card mixing)
	// 2017-02-20  2941  _a   no real changes
	// 2017-02-20  2940  Release Candidate
	// 2017-02-20  2939  _h   hondo lins
	// 2017-02-19  2939  _g   tweeks and script gen 6.11
	// 2017-02-06  2939  _f   OCP tables added to menu
	// 2017-02-06  2939  _e   hondo added
	// 2017-02-05  2939  _d   tweek to alpha table in OCP
	// 2017-02-02  2939  _c   test added to tell users of ew etc twists
	// 2017-02-02  2939  _b   fixed extra 90 twists added
	// 2017-02-02  2939  _a   Save with deal lowercase suits added

	
	static String built = y + " " + m + " " + d;

	static String status = "  built   " + built + "   ";
	
	static Date dateBuilt;
	static Date dateExpires;

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

	public static boolean hasExpired() {
		if (App.devMode)
			return false;
		else
			return App.FLAG_expires && dateExpires.before(new Date());
	}
}
