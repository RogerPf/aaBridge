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
package com.rogerpf.aabridge.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.rogerpf.aabridge.controller.App;

public class Util {

	public static long reverseOrderNumber() {
		// =============================================================================
		return 10L * 1000L * 1000L * 1000L * 1000L - (new Date()).getTime();
	}

	public static String last_4_chars(String s) {
		// =============================================================================
		return ("0000" + s).substring(s.length());
	}

	public static int create_4_digit_hash(String data) {
		// =============================================================================
		int h = (data.hashCode() % 10000);
		if (h < 0)
			h = -h;
		if (h < 1000)
			h += 1000;
		return h;
	}

	public static String filename_for_created_lin(String info, String data) {
		// =============================================================================
		long r = Util.reverseOrderNumber();
		String dateText = new SimpleDateFormat("E    yyyy-MM-dd  HH.mm    ").format(new Date());
		String fn = r + " q" + Util.create_4_digit_hash(data) + "   " + info + "   " + dateText;
		return App.temp_Other_folder + fn + ".lin";
	}

	public static String extract_simple(String haystack, String before, String after) {
		// =============================================================================
		int p_start = haystack.indexOf(before) + before.length();
		int p_end = haystack.indexOf(after, p_start);
		return haystack.substring(p_start, p_end);
	}

	public static String extract_simple(String haystack, String before) {
		// =============================================================================
		int p_start = haystack.indexOf(before) + before.length();
		return haystack.substring(p_start);
	}

}