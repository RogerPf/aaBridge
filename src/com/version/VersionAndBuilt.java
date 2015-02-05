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
	static String ver = "3.0.0.";

	public static int buildNo = 2724;    static String y = "2015", m = "Feb", d = "05";

	// 2015-02-05  2724  Release
	// 2015-02-05  2723  prepareing for release
	// 2015-02-05  2722  lin files - multiple clean up
	// 2015-02-05  2721  Fixed lost dividers in .jar runner
	// 2015-02-04  2720  Release
	// 2015-02-04  2719  Going for release a ...20
	// 2015-02-04  2718  Not - First release with full lin file collection
	// 2015-02-04  2717  Preparing for 3.0 release
	// 2015-02-04  2716  3.x First release - no change of mind we are scrapping 3.1 name    3.0 it is
	// 2015-02-04  2715  Small lin file corrections
	// 2015-02-04  2714  improved pg and card play error debugging
	// 2015-02-04  2713  better debug added for lin files pg reporting in debugger
	// 2015-02-03  2712  build a full 'release' of 3.1  NOT for release
	// 2015-02-03  2711  added all the lins and the 'books'
	// 2015-02-03  .     changed .gitignore to allow back in all the 'books'
	// 2015-02-03  2710  re-added 3.0 post process so the same source can be built as 3.1 or 3.0
	// 2015-02-03  2709  manual menu divider added (between books)
	// 2015-02-03  2708  Shelf ordering overide in jars and Thank you added
	// 2015-02-03  2707  Menubar Shelf ordering added
	// 2015-02-03  2706  Another attempt at a better r-andom lin picker
	// 2015-02-02  2705  Mass rename of lins to keep to 2 spaces after nn number
	// 2015-02-02  2704  digit 3 added to desktop aaBridge icon under text
	// 2015-02-02  2703  Changed version number
	// 2015-02-02  2702  More mentoring lins added to git system
	// 2015-02-02  2701  Starting work on 3.1
	//
	// 2015-02-02  2618  3.0  First Release
	// 2015-02-02  2617  Navbar lin doc improvements
	// 2015-02-02  2616  Other mentorings lins added
	// 2015-02-02  2615  Starting to add more mentorings
	// 2015-02-02  2614  3.0 Release Candidate 1
	// 2015-02-02  2613  Randlin tweeks
	// 2015-02-01  2612  mentoring2014  (reduced set)  added to git
	// 2015-02-01  2611  booksY booksZ  Visibility Test lins   added to git
	// 2015-02-01  2610  Unfixable classloader resource warning suppressed
	// 2015-02-01  2609  More adjustments
	// 2015-02-01  2608  More welcome page cleaning
	// 2015-02-01  2607  shelves now  "" and  1-9  A-Z
	// 2015-02-01  2606  Added video 'button' to for DFC  Removed EXTRA
	// 2015-01-31  2605  Start of version 3.0
	//
	// 2015-01-31  2442  Release of 2.4  (again)
	// 2015-01-31  2440  Additional early entry
	// 2015-01-31  2439  final tweek
	//
	// 2015-01-31  0000  repo on lucca   java__aaBridge06.git    created
	//

	
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
