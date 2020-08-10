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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.controller.Controller;
import com.rogerpf.aabridge.igf.MassGi_utils;

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
		if (p_end < p_start)
			return "";
		return haystack.substring(p_start, p_end);
	}

	public static String extract_simple(String haystack, String before) {
		// =============================================================================
		int p_start = haystack.indexOf(before) + before.length();
		return haystack.substring(p_start);
	}

	public static String removeExt(String filename) {
		// ==========================================================================

		if (filename.indexOf(".") > 0) {
			return filename.substring(0, filename.lastIndexOf("."));
		}
		else {
			return filename;
		}
	}

	private final static String M_hyphen = "M-";
	private final static String end_marker = ")";

	public static String extact_M_id(String s_in) {
		// =============================================================================
		int ind = s_in.indexOf(M_hyphen);
		if (ind < 0)
			return "";

		int end = s_in.indexOf(end_marker) - 1; // And remove an extra char eg "'"

		String rem = s_in.substring(ind, end);

		String ay[] = rem.split("-");
		if (ay.length != 3)
			return "";

		if (ay[0].length() != 1 || ay[1].length() < 10 || ay[2].length() < 10)
			return ("");

		return rem;
	}

	public static String format_non_fetched_lin(int count, String bbo_date) {
		// =============================================================================
		return "nt|^^^d " + count + " " + bbo_date + "  Not yet fetched|";
	}

	public static String read_from_lin_cache_or_queue_for_fetch(String M_id) {
		// =============================================================================

		String data = "";

		String filename = App.cached_lins_folder + M_id + ".lin";

		File f = new File(filename);

		if (f.exists()) {
			data = MassGi_utils.readFile(filename);
			if (data.contains("md|") || data.contains("MD|")) {
				return data;
			}
			return "";
		}

		data = format_non_fetched_lin(0, "0000000000");

		MassGi_utils.saveStringAsLinFile_direct(data, f.getAbsolutePath());

		Controller.nudgeLinCacheAdmin();

		return "";
	}

}