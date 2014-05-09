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

	public static int buildNo = 2308;    static String y = "2014", m = "May", d = "09";

	// 2014-05-09  2308  Release
	// 2014-05-09  2307  incompletly specified deals now better auto filled in 
	// 2014-05-08  2306  Seat kibits 'sk' now accepts y or Y  to  show all (not just beign empty)
	// 2014-05-08  2305  Tweek to git igonre to excude links
	// 2014-04-07  2304  More consistant options in DFC Exam mode
	// 2014-04-07  2303  Now uses Extra as the contributions name
	// 2014-04-06  2302  Two YouTube buttons
	// 2014-04-04  2301  Added YouTube Links to both front pages - videos now needed
	// 2014-03-20  2300  Release
	// 2014-03-20  2298  Missing links is question answering added back in
	// 2014-03-20  2297  Better handling of multi and single book defaults 
	// 2014-03-20  2296  Release
	// 2014-03-20  2295  Muti startup option simplified  
	// 2014-03-19  2294  Release
	// 2014-03-19  2293  Exam easier defautls - better multibook entry
	// 2014-03-15  2292  Release
	// 2014-03-15  2291  Exam display wrong place bug fix and add hyphen option
	// 2014-03-15  2290  Release
	// 2014-03-15  2289  Cleaned it up nicely
	// 2014-03-14  2288  seat selection exam version basicaly working
	// 2014-03-14  2287  Safety
	// 2014-03-13  2286  Safety
	// 2014-03-13  2285  Time display added to DFC exam
	// 2014-03-12  2284  Add text to opening page *New in red on DFC  Release
	// 2014-03-12  2282  Release  version now 2 . 2
	// 2014-03-12  2281  More varibility in Exam
	// 2014-03-12  2280  Exam button highlight added
	// 2014-03-12  2279  Exam Difficulty option added
	// 2014-03-11  2278  Exam feature working needs a hint
	// 2014-03-11  2277  Training & Exam now Working but needs a little more polish
	// 2014-03-11  2276  Safety - training back working
	// 2014-03-11  2275  invented  lb x  question changed to  lb z
	// 2014-03-11  2274  Safety
	// 2014-03-11  2273  question now starting to be displayed
	// 2014-03-11  2272  exam steps starting to emerge
	// 2014-03-11  2271  Safety moving to useing the newer suitVisibilityControl system
	// 2014-03-10  2270  Safety - about to move to newer suit visibility control
	// 2014-03-10  2269  Safety
	// 2014-03-10  2268  Train and Exam buttons added to lb:x question
	// 2014-03-10  2267  Starting work on DFC Exam separated from Train
	// 2014-03-09  2266  Opt Menu Tweeks and Release
	// 2014-03-09  2265  Deal Size and font split from StartUp Opts
	// 2014-03-09  2264  Suit colors and DFC separated into two panels - Safety
	// 2014-03-09  2263  minor prefs panels clean up - about to split panels
	// 2014-03-08  2262  Options - DFC added to title of Color option - Release
	// 2014-03-08  2261  font family overridie name was not being saved correctly
	// 2014-03-08  2260  lin parse now discards unknown bb codes - increased relisance
	// 2014-03-06  2259  dropped .LIN files now also can be read
	// 2014-03-06  2258  dfc option to show 'cards as greek letter alpha' added
	// 2014-03-06  2257  dfc improved option layout reverse sort opt added - blobs tbd
	// 2014-03-06  2256  Missing test for <= 7 cards re-added Release
	// 2014-03-06  2254  Release
	// 2014-03-06  2253  Spades now the longest option on Flsh Cards
	// 2014-03-05  2252  Color option added to  Distr Flsh Cards  Release
	// 2014-03-05  2251  Button to show options added
	// 2014-03-05  2250  Auto Next added
	// 2014-03-05  2249  Added to menu and Watsons version now 2.1.0
	// 2014-03-04  2248  Improved dist trainer
	// 2014-03-03  2247  Working on a distribution trainer like 'Flash Cards'
	// 2014-03-03  2246  Release
	// 2014-03-03  2245  tiny.cc urls now also correctly looked up and if lin locally launched
	// 2014-03-02  2244  removed all use of launcher prefs use - launched jar is now always the last sorted by name
	// 2014-03-02  2243  char 0x91 now also mapped to single quote (as is 0x92)
	// 2014-03-01  2242  Release
	// 2014-03-01  2241  tweeks to make  Shuf Op visible  by default
	// 2014-02-27  2240  Release
	// 2014-02-27  2238  known t i n y . c c  to lins are hand mapped so can be launched
	// 2014-02-27  2237  Old style multi bid defs with hyphens causeing bid/seat  misalignment
	// 2014-02-27  2236  Suits now only inserted my MD when zero is first char
	// 2014-02-26  2235  MD Make Deal with 0 for suits now returns unplayed cards to pack
	// 2014-02-24  2234  Release
	// 2014-02-24  2233  Dropped jar or folder not showing menu - fixed
	// 2014-02-20  2232  Release - runs on Java 6 
	// 2014-02-20  2231  Centering 'spill to earlier line' bug fixed
	// 2014-02-17  2230  No changes just different build number for loader testing
	// 2014-02-16  2229  Tweeks to make it build and run with Java 6
	// 2014-02-05  2228  Release
	// 2014-02-04  2227  Stray display of score panel when entering deal (after prev showed panel) fixed
	// 2014-02-03  2226  Release - link to   MusingsOnBridge blog   added
	// 2014-02-03  2225  Question and Ans page added to Watson
	// 2014-02-03  2224  Fith deal size option added now 0-4
	// 2014-02-03  2223  suit symobls now gray out completly with the gray text (actually even lighter)
	// 2014-02-03  2222  Grey got (paused ind) now darker same as Button txt color
	// 2014-02-02  2221  The four deal sizes now have the correct ratios
	// 2014-02-01  2220  Bidding Q answers - No Trump ans now show correctly when 'tell me' clicked
	// 2014-01-31  2219  Karen's fix - decimal sep now always '.' even on non eglish systems else breaks miglayout
	// 2014-01-30  2218  Release
	// 2014-01-30  2217  Changed to require java 7
	// 2014-01-29  2216  Release Candidate 2
	//                   jump for no good reason :)   Too many '14's
	// 2014-01-29  2212  Release Candidate - Title line simplified
	// 2014-01-28  2211  Small clean-up to the lins
	// 2014-01-28  2210  New lin intro's complete
	// 2014-01-27  2209  'w' option added so hondo lins are not loaded but folowed
	// 2014-01-26  2208  Tweek to installer (example in book)
	// 2014-01-26  2207  Single Book now shows single book 
	// 2014-01-26  2206  Final clean up of lin files
	// 2014-01-26  2205  'How to Wite a book'  improved
	// 2014-01-26  2204  Build for Kia
	// 2014-01-25  2203  About cleaned up
	// 2014-01-25  2202  Show 'multi book' setting added and default default
	// 2014-01-25  2201  extra 0 removed from Watson chapter titles
	// 2014-01-25  2200  Hondo links page completed
	// 2014-01-25  2199  .gitignore changed again, correctly this time
	// 2014-01-25  2198  the books in their new positions
	// 2014-01-25  2197  .gitignore changed - books will be back under git
	// 2014-01-25  2196  Tweeks - decided to do  Watson Edition
	// 2014-01-24  2195  Save and archive
	// 2014-01-23  2194  More small adjustments to what is showing on menus
	// 2014-01-23  2193  Menu improvements
	// 2014-01-23  2192  Condensed the Option panels from 7 down to 5
	// 2014-01-21  2191  Adding FLAGS e.g. single book for Watsons...
	// 2014-01-21  2190  first save  -  all previous files
	// 2014-01-21  2189  all lin files now in new positions
	//
	// 2014-01-21        repo on lucca   java__aaBridge05.git    created
	

	static String ver = "2.2.0.";
	
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
		return ver + buildNo;
	}

	public static String getStatus() {
		return status;
	}

	public static String getVer() {
		return ver;
	}

	public static String getBuildNo() {
		return "" + buildNo;
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
