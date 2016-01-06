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

	public static int buildNo = 2822;    static String y = "2016", m = "Jan", d = "03";


	// 2016-01-03  2822  Release Candidate
	// 2016-01-03  2821  _d   More Lin tweeks
	// 2016-01-03  2821  _c   Hondo lins for 2015 made public
	// 2015-12-28  2821  _b   build procedure doc updated - hondo lins
	// 2015-12-16  2821  _a   OCP added
	// 2015-12-15  2820  Release Candidate
	// 2015-12-15  2819  _f   Ability to remove and autoadd cards added (rc and aa cmnds)
	// 2015-12-15  2819  _e   add latest OCP
	// 2015-12-12  2819  _d   Only Marked Chapters in Menu  -  Recently viewed removed
	// 2015-12-06  2819  _c   OCP and Hondo lin added
	// 2015-12-01  2819  _b   Third DlaE button added
	// 2015-12-01  2819  _a   Tweak to text on the two Seat Cho-ice DlaE buttons  
	// 2015-11-30  2818  Release Candidate
	// 2015-11-30  2817  _f   Quick Paste now only works nears the Step and Flow buttons
	// 2015-11-29  2817  _e   More drag drop link fiddles
	// 2015-11-19  2817  _d   Tweeks to right and bottom panel text
	// 2015-11-19  2817  _c   now always creates a a non-existent saves folder
	// 2015-11-19  2817  _b   Users can now set their own Saves Folder
	// 2015-11-16  2817  _a   drag drop link text bug (alert text) fixed
	// 2015-11-16  2816  Release Candidate
	// 2015-11-15  2815  _d   typo fixed
	// 2015-11-15  2815  _c   jar now re-compressed to make it smaller
	// 2015-11-14  2815  _b   DLaE new button reset and DFC default tweek
	// 2015-11-14  2815  _a   Another DLAE settings bug  [sigh]
	// 2015-11-13  2814  Release Candidate
	// 2015-11-12  2813  _o   all OCP 2D revisited and 2814 needed
	// 2015-11-12  2813  _n   Deal to BBO two buggets fixed
	// 2015-11-10  2813  _m   Deal to BBO link added to men u etc
	// 2015-11-08  2813  _l   massive change of hondo lins removing  fb!110
	// 2015-11-08  2813  _k   reduced eating of spaces to one after double up arrow
	// 2015-11-08  2813  _j   tweek to make 'pf' message reset line spacing
	// 2015-11-08  2813  _i   added bbo hand link text drag and paste  click
	// 2015-11-06  2813  _h	  now have 4 NSEW visibility override checkboxes
	// 2015-11-05  2813  _g   tweeks to box positions
	// 2015-11-05  2813  _f   one tweek to lin
	//
	//                          Migrated to repo 09
	//
	// 2015-11-05  2813  _d   misplaced demo
	// 2015-11-05  2813  _c   rq now kill  nt  as well as  at
	// 2015-11-05  2813  _b   lin doc added for Lg  
	// 2015-11-04  2813  _a   lg  line gap (spacing)  added
	//             2812         skipped

	// see    repo_08  for more

	
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
