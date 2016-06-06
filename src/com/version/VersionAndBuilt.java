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
	static String ver = "3.1.0.";  public static String devExtra = "";    // extra is dev only, normally null

	public static int buildNo = 2882;    static String y = "2016", m = "June", d = "06";


	// 2016-06_06  2882  Release Candidate
	// 2016-06_06  2881  _n   and nearer
	// 2016-06_06  2881  _m   getting near release
	// 2016-06_05  2881  _k   again
	// 2016-06_04  2881  _j   Improve your Bridge  is now complete
	// 2016-06_04  2881  _h   same
	// 2016-06_04  2881  _g   Starting to add  Improve your Bridge
	// 2016-06_04  2881  _f   Red arrow hints improved
	// 2016-06_04  2881  _e   Clean Up of the settings and in particular Reset all Defaults
	// 2016-06_04  2881  _d   Again - More lin Changes moving away from the blog
	// 2016-06_04  2881  _c   More lin Changes moving away from the blog
	// 2016-06_04  2881  _b   More menu items directing locally not to the blog
	// 2016-06_04  2881  _a   Video button added to DFC
	// 2016-06_03  2880  Release Candidate
	// 2016-06_03  2877  _c   Visualize menu option added
	// 2016-05_28  2877  _b   Visualize example hand added
	// 2016-05_28  2877  _a   Additional color option added but not enabled (as no backwards compatibility)
	// 2016-05_28  2876  Release Candidate
	// 2016-05_28  2875  _f   Color tweaks to Poor Defense hint
	// 2016-05_28  2875  _e   Edit time  'cl ick  to be you' removed 
	// 2016-05_28  2875  _d   Shows the  Poo r  Defense hint more appropriately
	// 2016-05_28  2875  _c   POO R Defense hint  now shown by cont and 1st
	// 2016-05_24  2875  _b   lin files can now be just the single md|| command 
	// 2016-05_23  2875  _a   user can now quick type deals without using suit symbols
	// 2016-05_22  2874  Release Candidate
	// 2016-05_22  2873  _m   Includes new How to Enter (type) doc
	// 2016-05_21  2873  _k   e b|c| added   1 st and c ont  buttons reversed
	// 2016-05_20  2873  _j   Can now force ETD visible   Special use setting
	// 2016-05_19  2873  _i   1 st   now internally coded as  f
	// 2016-05_19  2873  _h   more visibility messages and pf letters now y b c f w x
	// 2016-05_19  2873  _g   fix to 1 st   co nt  visibility
	// 2016-05_18  2873  _f   lin docs changed to cover new vis feature
	// 2016-05_18  2873  _e   old e p1  renames
	// 2016-05_18  2873  _d   visibility of  Et_D  1 st and c ont  button added
	// 2016-05_14  2873  _c   co nt fix to menu menu ist
	// 2016-05_14  2873  _b   co nt & 1 st updated in lins
	// 2016-05_14  2873  _a   docs updated to replace Ep 1 by c ont & 1 st
	// 2016-05_14  2872  Release Candidate
	// 2016-05_13  2871  _h   Cont and 1st are now the names
	// 2016-05_13  2871  _g   Now trying  End  and  Start as button names
	// 2016-05_13  2871  _f   Try button names  E  and  B1
	// 2016-05_13  2871  _e   Epe button added still experimental don't like the name
	// 2016-05_08  2871  _d   Change to  E T D  and  E P 1  hover text
	// 2016-05_08  2871  _c   fixed (just added) bug in display of first page
	// 2016-05_04  2871  _b   added a second pf message  't' "rest of play"
	// 2016-05_04  2871  _a   now two not just one gray dot
	// 2016-05_04  2870  Release Candidate
	// 2016-05_04  2869  _a   fixed small bug single deals were showing initially without their play
	// 2016-05_03  2868  Release Candidate
	// 2016-05_03  2867  _f   clean build
	// 2016-05_03  2867  _e   deal name list extended
	// 2016-05_03  2867  _d   Change to leading policy by defender
	// 2016-05_02  2867  _c   correction to broken mg lin (chapter) loader
	// 2016-05_02  2867  _b   tc Table conceal now works as original intended
	// 2016-05_02  2867  _a   Single deals now can start down the rabbit hole
	// 2016-05_01  2866  Release Candidate
	// 2016-05_01  2865  _i   final clean ups
	// 2016-05_01  2865  _h   Fixed new board to reset universal rotator and hondo
	// 2016-05_01  2865  _g   Tweaked dummy  color
	// 2016-05_01  2865  _f   You Seat can now be anchored to a compass point
	// 2016-05_01  2865  _e   Change default   show you seat   policy to hide (from unchanged)
	// 2016-04_30  2865  _d   Thin black line suppressed when not applicable
	// 2016-04_25  2865  _c   universal rotation not now cleared by a new drop
	// 2016-04_25  2865  _b   Corrected hide / show  B1st  mis-display
	// 2016-04_24  2865  _a   Review fwd a full trick now also shows DDS if on
	// 2016-04_24  2864  Release Candidate
	// 2016-04_24  2863  _d   More
	// 2016-04_24  2863  _c   Small Tweaks
	// 2016-04_23  2863  _b   Changes to the docs to match '_a'
	// 2016-04_18  2863  _a   'Play' now discards future cards
	// 2016-04_18  2862  Release Candidate
	// 2016-04_15  2861  _d   mass hondo tweaks
	// 2016-04_15  2861  _c   hondo added
	// 2016-04_15  2861  _b   More changes to when to show the DDS scores
	// 2016-04_15  2861  _a   green color on website menu  dds now always starts as off
	//
	//         migrated to  repo-10
	//
	// 2016-04_14  2860  Release Candidate
	// 2016-04_14  2858       repeated
	// 2016-04_14  2858  Release Candidate
	// 2016-04_14  2857  _m   docs
	// 2016-04_14  2857  _k   plus menu entry
	// 2016-04_14  2857  _j   working at last
	// 2016-04_13  2857  _h   lin file changed to match
	// 2016-04_08  2857  _f   more work on the docs  collection
	// 2016-04_08  2857  _d   has all .docs packed in as well
	// 2016-04_08  2857  _c   Pink dot Blog entry added
	// 2016-04_08  2857  _b   Hondo files
	// 2016-04_08  2857  _a   Blog Box added next to Play Video on Welcome

	
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
