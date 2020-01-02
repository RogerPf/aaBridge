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
package com.rogerpf.aabridge.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.rogerpf.aabridge.model.Zzz;
import com.rogerpf.aabridge.util.Util;

/**   
 */
public class ExtractMyHands {
	// ---------------------------------- CLASS
	// -------------------------------------
	static boolean extractMyHands;

	static String readFile(String file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		String ls = Zzz.get_lin_EOL(); // String ls = "" + (char) 0x0a; // 0x0a = LF ;  was // System.getProperty("line.separator");

		try {
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(ls);
			}
			return stringBuilder.toString();
		} finally {
			reader.close();
		}
	}

	// =============================================================================
	public static int extractFromHtmlFile(File chosen) {

		// returns the number of deals saved in the tempMyHands folder

		if (chosen == null)
			return 0;

		String EOL = Zzz.get_lin_EOL();

		Document doc;
		try {
			doc = Jsoup.parse(chosen, "UTF-8", "");
		} catch (IOException e) {
			return 0;  // silently give up
		}

		String type = "";
		String bboid_searched_on = "";

		Element table = doc.select("table[class=body]").first();
		if (table != null) {
			/* ***********************************************************
			 * this is (the norm) a list of  Single deals  OR  a Traveler
			 * ***********************************************************/
			Element what = table.select("tr > th[colspan=\"10\"]").first();
			if (what != null) {
				type = "Trav";

				Element who = what.select("span[class=\"username\"]").first();
				if (who != null) {
					bboid_searched_on = who.text();
				}

				String text = what.text();

				String parts[] = (text + "-a-b-c-d-e").split(" |-", 5);
				long tm = Extract10Digit_Long(parts[2]) * 1000L;
				String dateText = new SimpleDateFormat("E    yyyy-MM-dd  HH.mm    ").format(new Date(tm));

				String id = (parts[1].length() == 4) ? parts[1] : Util.last_4_chars(parts[3]);
				long r = Util.reverseOrderNumber();
				String fname = r + " =" + id + "  Trav   " + dateText + bboid_searched_on + "   " + parts[1] + "-" + parts[2] + "-" + parts[3] + "   .lin";

				int sequnum = 0;
				// @formatter:off
            		String selector = 
    				    "tr[class=\"highlight\"]"
    				+ ", tr[class=\"tourney\"]"
    				+ ", tr[class=\"team\"]"
    				+ ", tr[class=\"mbc\"]"
    				;
            		// @formatter:on
				Elements rows = table.select(selector);
				String out = "vg||" + EOL + EOL;
				for (Element row : rows) {

					ExMyDeal myDeal = new ExMyDeal(row, type, ++sequnum, dateText, bboid_searched_on);
					if (myDeal.ok == false)
						continue; // for now we silently drop bad deals

					myDeal.o_or_c = "o";
					String qx_numb = sequnum + "";

					out += myDeal.getLinWithAddedFormatting(qx_numb, false /* rotate_you_to_south */ );
				}

				ExMyDeal.saveToFile(out, fname, type);

				return -1; // end of TRAVELER processing   -1 = one file put in trTm folder
			}

			/* ***************************************************************************
			 * "StdDeals"  the more common case a list of deals that one person has played
			 * ***************************************************************************/
			type = "StdDeals";

			String dateText = "";
			int sequnum = 0;
			int saved_count = 0;
			// @formatter:off
    		String selector = 
				    "tr[class=\"highlight\"]"
				+ ", tr[class=\"tourney\"]"
				+ ", tr[class=\"team\"]"
				+ ", tr[class=\"mbc\"]"
				+ ", th[colspan=\"11\"]"; // this lets us get the "date played"
    		// @formatter:on
			Elements rows = table.select(selector);
			for (Element row : rows) {
				String d = getDateTextIfValid(row);
				if (d != null) {
					if (d.length() == 10)
						dateText = d;
					continue;
				}
				ExMyDeal myDeal = new ExMyDeal(row, type, ++sequnum, dateText, bboid_searched_on);
				if (myDeal.ok == false)
					continue; // for now we silently drop bad deals

				if (saved_count++ == 0) {
					delete_all_lin_files_in_tempMyHandsFolder();
				}
				String out_one = myDeal.getAsOneHandFormatLin(true /*rotate_you_to_south*/, true /*enable_AutoEnter*/);

				ExMyDeal.saveToFile(out_one, myDeal.fname, type);
			}

			return saved_count;  // end of "StdDeals" processing   saved_count = number of files added to temp_MyHands
		}

		Elements tds = null;
		try {
			tds = doc.select("tr > td[class=\"resultcell\"] > a[href]");
		} catch (Exception e) {
			tds = null;
		}
		if (tds.size() != 0) {

			/* ******************************************************************
			 *  This as a Match between TWO Teams
			 * ******************************************************************/

			/* Try to use the date that appears to be in a header banner */
			String href = doc.select("body > div > iframe").attr("src");
			long tm = Extract10Digit_Long(href) * 1000L;
			if (tm == 0L) {
				String scr = doc.select("table[class=bbo_t_l] > tbody").outerHtml();
				tm = Extract10Digit_Long(scr) * 1000L;
			}
			String dateText = new SimpleDateFormat("E    yyyy-MM-dd  HH.mm    ").format(new Date(tm));

			String title = doc.select("table[class=bbo_t_l] > tbody > tr > td[class=bbo_tlv]").first().text();
			title = sanitize_for_filename(title);
			String ppp[] = (title).split(" ", 2);

			long r = Util.reverseOrderNumber();
			String fname = r + " " + ppp[0] + "  Teams   " + dateText + bboid_searched_on + "   " + title + "   .lin";

			int sequnum = 0;
			String out = "vg||" + EOL + EOL;
			type = "TwoTeams";
			for (Element td : tds) {
				String href_txt = td.attr("href");

				ExMyDeal myDeal = new ExMyDeal(href_txt, type, ++sequnum);
				if (myDeal.ok == false)
					continue; // for now we silently drop bad deals

				out += myDeal.getLinWithAddedFormatting(myDeal.qx_numb, false /* rotate_you_to_south */ );
			}

			ExMyDeal.saveToFile(out, fname, type);

			return -1; // end of  TEAMS  processing   -1 = one file put in trTm folder
		}

		return 0;  // we just silently return having done nothing
	}

	// =============================================================================
	static String getDateTextIfValid(Element row) {
		Attributes aaa = row.attributes();
		String aa = aaa.get("colspan");
		if (aa.isEmpty())
			return null;
		else
			return row.text();
	}

	// =============================================================================
	static void delete_all_lin_files_in_tempMyHandsFolder() {
		File folder = new File(App.temp_MyHands_folder);
		File[] listOfFiles = folder.listFiles();

		for (File file : listOfFiles) {
			if (file.isFile() && file.getName().toLowerCase().endsWith(".lin")) {
				file.delete();
			}
		}
	}

	// =============================================================================
	static String sanitize_for_filename(String in) {
		// remove illegal file system characters
		// https://en.wikipedia.org/wiki/Filename#Reserved_characters_and_words

		return in.replaceAll("[\\\\/:*?\"<>|]", "_");
	}

	// =============================================================================
	static String Extract10Digit_Str(String input) {
		input = " " + input + " ";
		int from = 0;
		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if ('0' <= c && c <= '9') {
				if (from == 0)
					from = i;
			}
			else {
				if ((from > 0) && (i - from == 10))
					return (input.substring(from, i));
				from = 0;
			}
		}
		return "";
	}

	// =============================================================================
	static long Extract10Digit_Long(String input) {
		String s = Extract10Digit_Str(input);
		if (s.length() == 10)
			return Long.parseLong(s);
		else
			return 0L;
	}

}
