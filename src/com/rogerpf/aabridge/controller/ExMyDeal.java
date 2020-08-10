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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.rogerpf.aabridge.model.Zzz;
import com.rogerpf.aabridge.util.Util;

/**   
 */
public class ExMyDeal { // Incoming played deal from BBO via html
	// ---------------------------------- CLASS
	// -------------------------------------
	String o_or_c = "";
	boolean found_ok = false;
	String type = "StdDeals"; // extract My hands
	String bboid_searched_on = "";
	int seqn = 0;
	String seqs = "";
	String handnum = "";
	String qx_numb = "";
	String youseat = "";
	int youseat_ind = 0;
	Date when;
	String date_text = "";
	String time_text = "";
	String imps_or_mp = "";
	String score_pts = "";
	String score = "";
	String score_red = "";
	String grade = "-";
	String contract_n_result = "";
	String[] pnames = new String[4];
	String deal_etc = "";
	String hand_url = "";
	String trav_url = "";
	String fname = "";

	boolean highlighted = false;

	static int parity = 0;

	// ****************************************************************************
	ExMyDeal(String href, String type_in, int sequnum) { // Constructor
		// =============================================================================

		o_or_c = (parity++ % 2 == 0) ? "o" : "c";

		type = type_in;
		bboid_searched_on = "";
		seqn = sequnum;
		seqs = sequnum + "";

		try {
			deal_etc = URLDecoder.decode(href, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
		}
		deal_etc = Util.extract_simple(deal_etc, "&lin=");

		qx_numb = Util.extract_simple(deal_etc, "ah|Board ", "|");

		String players = Util.extract_simple(deal_etc, "pn|", "|") + ",,,";

		String[] names = players.split(",");

		for (int i = 0; i <= 3; i++) {
			pnames[i] = names[i];
		}
		found_ok = true;

//		int y = 0;
//		y++;
	}

	// ****************************************************************************
	ExMyDeal(Element row, String type_in, int sequnum, String date_t, String bboid_s_on) { // Constructor
		// =============================================================================

		// Standard Hands (session) constructor

		type = type_in;
		date_text = date_t;
		bboid_searched_on = bboid_s_on;
		seqn = sequnum;
		seqs = sequnum + "";

		Elements tds = row.select("td");

		if (tds.size() > 1) {
			time_text = tds.get(1 /* 2nd td entry */).text();
			String fmt_str = "yyyy-MM-dd HH:mm";
			String dt = "";
			if (time_text.length() == 16) {
				type = "Trav";
				dt = time_text;
			}
			else if (date_text.length() == 10 && time_text.length() == 5) {
				dt = date_text + " " + time_text;
			}
			else if (time_text.length() == 5) {
				fmt_str = "HH:mm";
				dt = time_text;
			}
			else {
				fmt_str = "HH:mm";
				dt = "00:00";
			}

			DateFormat lFormatter = new SimpleDateFormat(fmt_str);
			try {
				when = (Date) lFormatter.parse(dt);
			} catch (ParseException e) {
			}
		}

		handnum = tds.select("td[class=handnum]").text();

		String s = tds.select("td[class=result]").text();
		contract_n_result = clean_contract_and_result(s);
		pnames[0] = tds.select("td[class=south]").text();
		pnames[1] = tds.select("td[class=west]").text();
		pnames[2] = tds.select("td[class=north]").text();
		pnames[3] = tds.select("td[class=east]").text();

		Elements scors = tds.select("td[class$=score]");
		score_pts = scors.text();
		if (scors.size() > 1) {
			score = scors.get(1 /* 2nd score entry */).text();
			grade_from_score();
		}

		Element movie = row.select("td[class=movie]").first();
		if (movie == null) {
			System.out.println("\n No   BBO deal 'Movie' found in  Sequ No:" + sequnum);
			return;
		}

		hand_url = movie.select("a").first().attr("href");
		if (!hand_url.toLowerCase().startsWith("http")) {
			hand_url = App.bbo_base_url + hand_url;
		}

		deal_etc = movie.select("a").attr("onclick");
		deal_etc = Util.extract_simple(deal_etc, "hv_popuplin('", "');");
		try {
			deal_etc = URLDecoder.decode(deal_etc, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
		}
		if (deal_etc.toLowerCase().contains("md|") == false) {

			// so the deal_etc is NOW damaged, so refresh it
			deal_etc = movie.select("a").attr("onclick");

			String m_id = Util.extact_M_id(deal_etc);

			//------------------------------------------------------------ lin_cache used here
			deal_etc = Util.read_from_lin_cache_or_queue_for_fetch(m_id);

			if (deal_etc.toLowerCase().contains("md|") == false) {
				found_ok = false;
				return;
			}

		}
		qx_numb = Util.extract_simple(deal_etc, "|rh||ah|Board ", "|sv|");

		trav_url = "";
		Element trav = row.select("td[class=traveller]").first();
		if (trav != null) {
			trav_url = trav.select("a").first().attr("href");
			if (!trav_url.toLowerCase().startsWith("http")) {
				trav_url = App.bbo_base_url + trav_url;
			}
			bboid_searched_on = Util.extract_simple(trav_url, "username=");
		}

		// set the seat of the searched-on user
		youseat = "s";
		youseat_ind = 0;
		for (int i = 0; (i <= 3); i++) {
			if (pnames[i].equalsIgnoreCase(bboid_searched_on)) {
				youseat_ind = i;
				youseat = ("swne").charAt(i) + "";
				if (type.contentEquals("Trav"))
					highlighted = true;
				break;
			}
		}
		found_ok = true;
	}

	// ****************************************************************************
	ExMyDeal(Element row, String type_in, int sequnum, String date_t) { // Constructor
		// =============================================================================

		// Daylong and Group Challenge constructor

		type = type_in;
		date_text = date_t;
		seqn = sequnum;
		seqs = sequnum + "";

		Elements tds = row.select("td");

		if (tds.size() > 1) {
			time_text = tds.get(1 /* 2nd td entry */).text();
			String fmt_str = "yyyy-MM-dd HH:mm";
			String dt = "";
			if (time_text.length() == 16) {
				type = "Trav";
				dt = time_text;
			}
			else if (date_text.length() == 10 && time_text.length() == 5) {
				dt = date_text + " " + time_text;
			}
			else if (time_text.length() == 5) {
				fmt_str = "HH:mm";
				dt = time_text;
			}
			else {
				fmt_str = "HH:mm";
				dt = "00:00";
			}

			DateFormat lFormatter = new SimpleDateFormat(fmt_str);
			try {
				when = (Date) lFormatter.parse(dt);
			} catch (ParseException e) {
			}
		}

		pnames[0] = tds.get(0).text();
		pnames[1] = "-";
		pnames[2] = "-";
		pnames[3] = "-";

		handnum = tds.get(1).text();
		contract_n_result = clean_contract_and_result(tds.get(3).text());
		score = tds.get(5).text();
		// grade_from_score();

		Element movie = tds.get(6);
		if (movie == null) {
			System.out.println("\n No  DL-GC  BBO deal 'Movie' found in  Sequ No:" + sequnum);
			return;
		}

		hand_url = movie.select("a").first().attr("href");
		if (!hand_url.toLowerCase().startsWith("http")) {
			hand_url = App.bbo_base_url + hand_url;
		}

		deal_etc = hand_url;
		// deal_etc = Util.extract_simple(hand_url, "hv_popuplin('", "');");
		try {
			deal_etc = URLDecoder.decode(deal_etc, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
		}

		// add in any missing  rh||
		if (deal_etc.contains("rh||") == false) {
			String deal_etc2 = deal_etc;
			deal_etc = deal_etc2.replace("ah|", "rh||ah|");
		}

		qx_numb = Util.extract_simple(deal_etc, "ah|Board ", "|sv|");

		trav_url = "";
		found_ok = true;

//		int y = 0;
//		y++;
	}

	// ****************************************************************************
	void grade_from_score() {

		if (score.contains("%")) {
			String score_2 = score.replace("%", " ");
			imps_or_mp = "MP";
			float score_int = Float.parseFloat(score_2);
			score_red = (score_int < 50) ? "red" : "";

			// @formatter:off
			if      (score_int >  60) { grade = "a"; }
			else if (score_int >  55) { grade = "b"; }
			else if (score_int >  45) { grade = "c"; }
			else if (score_int >  40) { grade = "d"; }
			else                      { grade = "e"; }
			// @formatter:on

		}
		else {

			imps_or_mp = "IMP's";
			float score_f = Float.parseFloat(score);
			score_red = (score_f < 0.0f) ? "red" : "";

			// @formatter:off
			if      (score_f >  4  ) { grade = "A"; }
			else if (score_f >  1.5) { grade = "B"; }
			else if (score_f > -1.5) { grade = "C";	}
			else if (score_f > -4  ) { grade = "D"; }
			else                     { grade = "E"; }
			// @formatter:on
		}
	}

	// ****************************************************************************
	static String clean_contract_and_result(String raw) {

		raw = raw.trim();

//		if (seqn == 23) {
//			int z = 0;
//			z++;
//		}
//
		if (raw.contentEquals("PASS")) {
			return "-PASS-";
		}

		if (raw.contentEquals("A==")) {
			return "-AVE-";
		}

		if (raw.length() < 4) {
			return "-QQQQ-";
		}

		char level = raw.charAt(0);
		if (level < '0' || '7' < level) {
			return "level-" + level;
		}

		String rest = raw.substring(2);
		if (rest.startsWith("xx"))
			rest = "xx " + rest.substring(2);
		else if (rest.startsWith("x"))
			rest = "x " + rest.substring(1);
		else
			rest = " " + rest;

		String si = "";
		if (raw.toUpperCase().charAt(1) == 'N') {
			si = "N"; // NT      
		}
		else {
			if (raw.indexOf("♣") > 0) {
				si = "C";
			}
			else if (raw.indexOf("♦") > 0) {
				si = "D";
			}
			else if (raw.indexOf("♥") > 0) {
				si = "H";
			}
			else if (raw.indexOf("♠") > 0) {
				si = "S";
			}
			else {
				return "--Q--";
			}
		}
		return level + si + rest;
	}

	// ****************************************************************************	
	static String padLeftSpaces(String str, int n) {
		return String.format("%" + n + "s", str);
	}

	// ****************************************************************************	
	static String padRightSpaces(String str, int n) {
		return String.format("%1$-" + n + "s", str);
	}

	// ****************************************************************************	
	String getAsOneHandFormatLin(boolean rotate_you_to_south, boolean enable_AutoEnter) {

		String EOL = Zzz.get_lin_EOL();

		String bn = ("   " + qx_numb).substring(qx_numb.length());

		String hn3 = "";
		String sq = "";
		String hl = "";

		if (type.contentEquals("Trav")) {
			sq = ("000" + seqs).substring(seqs.length());
			fname = sq + " " + sq;
			qx_numb = handnum;
			hl = (highlighted) ? "===" : "";
		}
		else if (type.contentEquals("StdDeals")) {
			hn3 = (((("" + handnum).length()) == 1) ? "0" : "") + handnum;
			fname = ((when.getTime() / 10L) + seqn) + " " + hn3 + " " + bn;
		}

		fname += "  " + grade + "  " + contract_n_result + " " + hl;

		fname = padRightSpaces(fname, 40);

		SimpleDateFormat fmt = new SimpleDateFormat("E    yyyy-MM-dd  HH.mm    ");

		fname += fmt.format(when);

		for (int i = 0; (i <= 3); i++) {
			fname += padRightSpaces(pnames[i], 12);
		}
		fname += ".lin";

		String out = "";
		if (enable_AutoEnter) {
			out += "ae|| %% enable  Auto Enter  (the deal)" + EOL;
		}

		out += "nt|^b@2^^|" + EOL;

		out += "at|Played on        ";

		SimpleDateFormat fmt2 = new SimpleDateFormat("EEEE   ^'h'  yyyy-MMM-dd  HH.mm   (h:mm a) ");
		out += fmt2.format(when);

		out += " ^p      " + imps_or_mp + "^r |cp|" + score_red + "|at|^*b" + score + "^*n|cp||at|";
		out += " ^u ' " + grade + " '^^^^^^|" + EOL + EOL;

		if (!hand_url.isEmpty()) {
			out += "at|^m From the BBO Hand Viewer website ^*h " + hand_url + " , ^v Show the Deal^*n^^^^|" + EOL + EOL;
		}

		if (!trav_url.isEmpty()) {
			out += "at|^m From the BBO MyHands website ^*h " + trav_url + " , ^v Show the Traveler^*n";
			out += "^^^^ |" + EOL + EOL;
		}
		else {
			out += "at|^m This hand was downloaded from a link in the traveler^^^^|" + EOL + EOL;
		}

		out += getLinWithAddedFormatting(qx_numb, rotate_you_to_south) + EOL;

		return out;
	}

	// ****************************************************************************	
	static void saveToFile(String out, String fname, String type) {

		try {
			String path = type.equals("StdDeals") ? App.temp_MyHands_folder : App.temp_Other_folder;
			new File(path).mkdir();
			FileWriter fw = new FileWriter(path + fname);
			BufferedWriter bw = new BufferedWriter(fw);

			bw.write(out);

			bw.flush();
			bw.close();
			fw.close();

		} catch (IOException e) {
			// int z = 0;
			// z++;
		}
	}

	// ****************************************************************************
	String getLinWithAddedFormatting(String qx_numb, boolean rotate_you_to_south) {

		String EOL = Zzz.get_lin_EOL();

		String rot_cmd = rotate_you_to_south ? ("rt|" + ((4 - youseat_ind) % 4) + "|" + EOL) : "";

		// strip off the st|| and players names - they go back later

		int p = deal_etc.indexOf("|md|");

		String lin = deal_etc.substring(p + 1);

		if (type.equals("Trav")) {
			lin = lin.replace("rh||ah|Board", "rh||ah|Traveler");
		}

		String s = "qx|" + o_or_c + qx_numb + "|";

		if (type.equals("Trav") || type.equals("DL-GC")) {
			s += "ts|" + (highlighted ? "H " : "") + score + "|";
		}

		s += "pn|" + pnames[0] + "," + pnames[1] + "," + pnames[2] + "," + pnames[3] + "|" + EOL;

		s += lin;

		s = s.replace("|rh||", "|" + EOL + rot_cmd + "rh||");

		int sv_start = s.indexOf("|sv|");
		int sv_end = s.indexOf("|", sv_start + 4);
		if (sv_start >= 0 && sv_end > sv_start) {
			String sv = s.substring(sv_start, sv_end + 1);
			s = s.replace(sv, sv + "sk|" + youseat + "|" + EOL);
		}

		String mc = "";

		int mc_pos = s.indexOf("|mc|");
		if (mc_pos > -1) {
			mc = s.substring(mc_pos + 1);
			s = s.substring(0, mc_pos + 1);
		}

		boolean pg_at_end = false;
		String pc = "";
		int pc_pos = s.indexOf("|pc|");
		if (pc_pos > -1) {
			pc = s.substring(pc_pos);
			s = s.substring(0, pc_pos + 1);

			String[] parts = pc.split("[|]pc[|]");

			int last = parts.length - 1;
			if (last >= 0 && parts[last].length() > 2) {
				parts[last] = parts[last].substring(0, 2);
			}

			for (int i = 0; i < parts.length; i++) {
				String card = parts[i];

				pg_at_end = false;

				if (i == 0) {
					; // do nothing  this entry will be blank
				}
				else if (i == 1) {
					s += "pg||" + EOL + "pc|" + card + "|pg||" + EOL;
					pg_at_end = true;
				}
				else if (i % 4 == 0) {
					s += "pc|" + card + "|pg||" + EOL;
					pg_at_end = true;
				}
				else {
					s += "pc|" + card + "|";
				}
			}

			if (pg_at_end == false) {
				s += "pg||" + EOL;
			}

			if (mc.length() > 0) {
				s += mc;
			}
//			int z = 0;
//			z++;
		}

		String pos_end = "pg||" + EOL;

		if (s.endsWith(pos_end)) {
			s = s.substring(0, s.length() - pos_end.length());
		}

		if (type.contentEquals("StdDeals")) {
			s += EOL + EOL + "qx|end,wide|ht|z|at|^^^a^*bend^*n|pg||" + EOL + EOL;
		}
		else {
			s += "pg||" + EOL + EOL;
		}

		return s;
	}

}
