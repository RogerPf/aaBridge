package com.rogerpf.aabridge.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.controller.q_;

public class Lin {
	// ---------------------------------- CLASS -------------------------------------

	static final char bar = 0x7c;
	static final char carriageReturn = '\r';
	static final char newLine = '\n';

	public static final String Other = "Other";
	public static final String SimpleDealVirgin = "SimpleDealVirgin";
	public static final String SimpleDealSingle = "SimpleDealSingle";
	public static final String VuGraph = "VuGraph";
	public static final String FullMovie = "FullMovie";

	public String filename = "";
//	public String chapterName = "";

	public String linType;

	public ArrayList<BarBlock> bbAy = new ArrayList<BarBlock>();

	public Deal virginDeal = null;

	public String headingInfo = "";

	public boolean twoTeams = false;

	public boolean cameFromPbn = false;

	public String saveAsPbnNoPlayPath = "";

	public int qx_count = 0;
	public int bt_count = 0;
	public int md_count = 0;
	public int at_count = 0;
	public int nt_count = 0;
	public int mb_count = 0;
	public int vr_count = 0;
	public int first_rt_value = 0;

	/**   
	 */
	public class BarBlock extends ArrayList<String> {
		// ---------------------------------- CLASS -------------------------------------
		private static final long serialVersionUID = 1L;

		public String type;
		public int qt;
		public int lineNumber;
		public boolean uni = false;

		public BarBlock(String type, int lineNumber) {
			// ==============================================================================================
			this.type = type;
			this.qt = q_.q(type);
			this.lineNumber = lineNumber;
		}

		/**   
		 */
		public String getSafe(int index) {
			// ==============================================================================================
			if (index >= size())
				return "";
			else
				return get(index);
		}

	}

	/**   
	 *   Constructor (trivial) - creates minimal lin from an internally pre-created deal
	 */
	public Lin(Deal deal) {
		// ==============================================================================================
		/**
		 */
		virginDeal = deal;
		linType = Lin.SimpleDealVirgin;

	}

	/**   
	 *    Constructor  -  the main constructor
	 */
	public Lin(InputStream fis, String parentPath, String frName, boolean fromPbn, String pbnToLinFolder) throws IOException {
		// ==============================================================================================

		if (fromPbn == false) { // so is a 'normal' lin file

			linFileToBbArray(fis, parentPath, frName);
			calcLinType();

			// ugly

			saveAsPbnNoPlayPath = App.autoSavesPath;

			if (!App.thisAppBaseFolder.isEmpty() && pbnToLinFolder.contains(App.thisAppBaseFolder) /* debug case */) {
				// no change
			}
			else if (!pbnToLinFolder.isEmpty()) {
				saveAsPbnNoPlayPath = pbnToLinFolder;
			}
		}

		else {
			cameFromPbn = true;

			linType = Lin.Other;

			/**
			 *  Create and save the 'auto saved' (was pbn) now lin file
			 */

			String sRtn[] = new String[2];

			pbnFileToBbArray(fis, parentPath, frName, sRtn);

			String linName = sRtn[1];
			if (!linName.isEmpty()) {
				File f = new File(linName);
				linName = f.getName().replaceFirst("[.][^.]+$", ""); // regexp removes the extension
			}

			if (linName.isEmpty()) {
				int p = frName.lastIndexOf("/");
				linName = frName.substring(p + 1); // in zips and jars we get the full pseudo path
			}

			String rt = "";
			// @formatter:off
			switch (App.pbnFixedQuarterTurns) {
				default:rt += "___--"; break; // 0
				case 1: rt += "___ew"; break;
				case 2: rt += "___sn"; break;
				case 3: rt += "___we"; break;
			}

			switch (App.pbnRotateWhenLoading) {
				default:rt += "___0_rotation"; break; // 0
				case 1: rt += "__90_rotation"; break;
				case 2: rt += "_180_rotation"; break;
			}
			// @formatter:on

			linName = "yyy__lin_from__" + linName + rt + ".lin";

			try {
				String pbnPath = App.autoSavesPath;

				// @formatter:off
				
				if (    App.alwaysSavePbnToLinToAutosavesFolder
					||  !App.thisAppBaseFolder.isEmpty() && pbnToLinFolder.contains(App.thisAppBaseFolder)  // debug case
				   ) {
					// no change
				}
				else if (!pbnToLinFolder.isEmpty()) {
					pbnPath = pbnToLinFolder;
				}
				
				// @formatter:on

				FileWriter fw = new FileWriter(pbnPath + linName);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(sRtn[0]);
				// bw.newLine();
				bw.flush();
				bw.close();
				fw.close();

				saveAsPbnNoPlayPath = pbnPath;

			} catch (IOException e) {
				// Sigh - what do they expect to be done !
			}
		}

//		int len = frName.length();
//		saveAsPbnNoPlayPath += "yyz__pbn_from___" + frName.substring(0, len - 4) + ".pbn";
		saveAsPbnNoPlayPath += "yyz__pbn_from___" + frName + "___.pbn";

	}

	/**   
	 */
	public void calcLinType() throws IOException {
		// ==============================================================================================

		qx_count = 0;
		bt_count = 0;
		md_count = 0;
		at_count = 0;
		nt_count = 0;
		mb_count = 0;
		vr_count = 0;
		first_rt_value = 0;

		linType = Lin.Other;

		if ((bbAy.size() >= 1) || ((bbAy.size() == 1) && (bbAy.get(0).qt == q_.md))) {
			// we are good to go
		}
		else {
			String s = "calcLinType - too few commands in lin file";
			System.out.println(s);
			throw new IOException(s);
		}

		boolean rt_found = false;

		boolean vgFirst = (bbAy.get(0).qt == q_.vg);

		boolean all_qx_start_with_o_or_c = true;
		// @formatter:off
		for (BarBlock bb : bbAy) {
			int t = bb.qt;
			if (t == q_.qx) {
				qx_count++;
				char ch = (bb.get(0).length() > 0) ? bb.get(0).charAt(0) : ' '; 
				if (ch != 'c' &&  ch != 'o') {
					all_qx_start_with_o_or_c = false;
				}
			}
			else if (t == q_.bt) bt_count++;
			else if (t == q_.md) md_count++;
			else if (t == q_.at) at_count++;
			else if (t == q_.nt) nt_count++;
			else if (t == q_.mb) mb_count++;
			else if (t == q_.vr) vr_count++;
			else if (t == q_.rt && rt_found == false) {
				rt_found = true;
				if (bb.get(0).length() > 0) {
					first_rt_value = bb.get(0).charAt(0) - '0';
					if (first_rt_value < 0 || first_rt_value > 3)
						first_rt_value = 0;
				}
			};
		}
		// @formatter:on

		if (md_count == 1 && (at_count == 0 && nt_count == 0 || (vr_count > 0 && App.respectVrCmd))) {
			linType = Lin.SimpleDealSingle;
		}
		else if (vgFirst && qx_count > 1 && qx_count == md_count) {
			linType = Lin.VuGraph;
		}
		else if (vgFirst && qx_count > 1 && qx_count > md_count && md_count > 1 && all_qx_start_with_o_or_c) {
			linType = Lin.VuGraph; // incomplete tourny records
		}
		else if (bt_count > 0 || at_count > 0) {
			linType = Lin.FullMovie;
		}
		else {
			linType = Lin.Other; // for now we will call all others - other
		}
	}

	/**   
	 */
	static public void saveDealAsSingleLinFileBW(Deal deal, String kib_seat, BufferedWriter w) throws IOException {
		// ===================================================================================

		String eol_or_blank = (App.saveAsBboUploadFormat) ? "" : Zzz.get_lin_EOL();
		String extra_space = (App.saveAsBboUploadFormat && App.saveAsBboUploadExtraS) ? " " : "";
		String add_o = (App.saveAsBboUploadFormat) ? "o" : "";

		String local_displayId = "1";
		if (Aaa.extractPositiveInt(deal.displayBoardId) > 1) {
			local_displayId = deal.displayBoardId;
		}

		w.write("qx|" + add_o + local_displayId + "|");

		// @formatter:off
		if ((   deal.hands[Dir.South.v].playerName.isEmpty() 
			 && deal.hands[Dir.West.v ].playerName.isEmpty() 
			 && deal.hands[Dir.North.v].playerName.isEmpty() 
			 && deal.hands[Dir.East.v ].playerName.isEmpty()
			) == false) {
			
			w.write("pn|");
			for (Hand hand : deal.rota[Dir.South.v]) {
				w.write(hand.playerName);
				if (hand.compass != Dir.East)
					w.write(",");
			}
			w.write(extra_space + "|" + eol_or_blank);		
		}
	    // @formatter:on

		// Headers and our invented Display Board Number
		w.write("rh||");
		if (App.saveAsBboUploadFormat == false) {
			String ahText = Aaa.cleanString(deal.ahHeader, true /* true => spaceOk */);
			if (!ahText.isEmpty()) {
				w.write("ah|" + ahText.trim() + "|");
			}
		}
		String signfBoardId = (App.deal.signfBoardId.trim().isEmpty() ? "Board" : App.deal.signfBoardId);

		if (App.saveAsCombine_md_sv) {
			w.write("ah|Board " + local_displayId);
		}
		else if (deal.displayBoardId.length() > 0) {
			w.write("ah|" + signfBoardId + " " + deal.displayBoardId);
		}
		else {
			w.write("ah|" + signfBoardId + " " + deal.realBoardNo);
		}
		w.write(extra_space + "|" + eol_or_blank);

		w.write("md|"); // test A BRIDGE TABLE is displayed md => make deal ?

		// now as the **** first character of the South hand defintion **** we write the dealer id
		// for lin '1'=South, '4'=East, aaBridge internal 2=South, 1=East

		w.write("" + (char) (((deal.dealer.v + 2) % 4 + 1 + '0')));

		// note we might want Easts hand for manual editing so NOT omitted

		w.write(deal.cardsForLinSave());
		w.write("|");
		if (App.saveAsCombine_md_sv == false)
			w.write(eol_or_blank);

		// sv => side vulnerability
		w.write("sv|");

		if (deal.vulnerability[Dir.NS] && deal.vulnerability[Dir.EW])
			w.write("B|");
		else if (deal.vulnerability[Dir.NS])
			w.write("N|");
		else if (deal.vulnerability[Dir.EW])
			w.write("E|");
		else
			w.write("O|");

		if (App.saveAsCombine_md_sv)
			w.write(eol_or_blank);

		if (kib_seat.length() > 0) {
			w.write("sk|" + kib_seat + "|");
		}

		w.write("sk||");
		w.write("pg|" + extra_space + "|");
		w.write(eol_or_blank);
		// Add the bidding

		if (deal.countBids() > 0) {
			w.write(deal.bidsForLinSave());
			w.write("pg|" + extra_space + "|");
			w.write(eol_or_blank);
		}

		// Add the card play

		w.write(deal.cardPlayForLinSave());
		// adds its own Zzz.get_lin_EOL() as is proper

		if (App.saveAsBboUploadFormat)
			w.write(Zzz.get_lin_EOL());
	}

	/**   
	 */
	public void linFileToBbArray(InputStream fis, String parentPath, String frName) throws IOException {
		// ==============================================================================================
		/**
		 *  I take the syntax to be -   
		 *  
		 *  rubbish (inc c/r) <two alpha> <opening bar> STUFF <closing bar> more rubbish repeat ...
		 *  
		 *  STUFF is <two alpha> dependent c/r can be valid e.g in text some defs includes comma seperated fields
		 *      these of course cannot cannot contain additional commas.
		 *      
		 *      This scheme covers all end of line comments and all %% which form commented lines
		 *      
		 *  All the  <two alpha>  are changed to lower case for convenience
		 *  
		 *  We fill the  bbAy   (Bar Block Array)
		 *  
		 */
		String s = "";
		char c = 0, a0 = 0, a1 = 0;
		char eol = 0;
		char eol_skip = 0;
		BarBlock bb = null;

		boolean huntingTwoAlpha = true;

		int lineNumber = 1;

		String injector = "";
		int injector_read_next = 0;
		char inject_at_next_pg = '-';

		InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
		BufferedReader br = new BufferedReader(isr);

		boolean uni_c = false;
		boolean s_has_uni = false;

		boolean vg_found = false;

		while (true) {

			if (injector.length() > injector_read_next) {
				c = injector.charAt(injector_read_next++);
				uni_c = false;
			}
			else {
				int i = br.read();
				if (i == -1)
					break;
				c = (char) i;
				uni_c = (i > Aaa.uniThreashold);
			}

			if (eol == 0) {
				if ((c == carriageReturn || c == newLine)) {
					eol = c;
					eol_skip = (c == carriageReturn) ? newLine : carriageReturn;
					lineNumber++;
					// continue;
				}
			}
			else if (c == eol_skip) {
				continue;
			}
			else if (c == eol) {
				lineNumber++;
			}

//			if (lineNumber == 376) {
//				int z = 0;
//			}

			if (huntingTwoAlpha) {
				if (c == eol || c == ' ' || c == 0x09 /* tab*/|| uni_c)
					continue;
				if (c != bar) {
					/* we are trying to get the last two non white space chars converted to lowercase */
					if ('A' <= c && c <= 'Z') {
						c = (char) ((int) c + Aaa.upperLowerDif);
					}
					a0 = a1;
					a1 = c;
					continue;
				}

				if (a0 == 0 || a1 == 0) {
					// an invalid two alpha - should not happen
					// @SuppressWarnings("unused")
					// int z = 0; // put your breakpoint here
					// throw new IOException();
					continue;
				}

				/** we now want to check that this is a known bb letter pair 
				 * if not we will skip it and keep searching we are trying to
				 * be more resilient to bad scripts that get out of sync with their bars
				 */
				if (q_.isQtKnown(a0, a1) == false) {
					System.out.println(frName + "  line " + lineNumber + "  'linFileToBbArray' command letter pair unknown to aaBridge -" + a0 + "" + a1 + "-");
					continue;
				}

				assert (bb == null);
				bb = new BarBlock(a0 + "" + a1, lineNumber);
				a0 = a1 = 0;

				bbAy.add(bb);
				if (bb.qt == q_.pa) { // special processing for pa
					// pa is a pg with a pause - we force it to be a standard pg
					bb.qt = q_.pg;
					bb.type = "pg";
				}

				if (bb.qt == q_.vg) {
					vg_found = true;
				}

				s = "";
				huntingTwoAlpha = false;
				continue;
			}

			assert (bb != null);

			if (vg_found && (c == '\u00c2' || c == '\u00a8')) {
				// BBO vu-graphs commentors supply bad characters between the ! an the suit letter
				// this discards them - but is still ugly
				continue;
			}

			if (c == ',') {
				if (bb.qt == q_.nt || bb.qt == q_.at || bb.qt == q_.mn || bb.qt == q_.sb || bb.qt == q_.ia || bb.qt == q_.an || bb.qt == q_.lb
						|| bb.qt == q_.rc) {
					s += c; // treat as normal
					// s_has_uni is not reset
				}
				else {
					bb.add(s); // treat as comma separated variable
					s = "";
					// s_has_uni is not reset
				}
				continue;
			}

			if (c == eol) {
				if (bb.qt == q_.nt || bb.qt == q_.at || bb.qt == q_.mn) { // new text add text
					bb.add(s); // treat as a multi selection at
					bb.uni |= s_has_uni;
					s = "";
					s_has_uni = false;
				}
				continue; // skip for all other fields
			}

			if (c == bar) {
				bb.add(s);
				bb.uni |= s_has_uni;
				if (bb.qt == q_.pf) {
					inject_at_next_pg = '-';
					if (s.trim().length() > 0) {
						if (s.toLowerCase().contains("y"))
							inject_at_next_pg = 'y';
						else if (s.toLowerCase().contains("f"))
							inject_at_next_pg = 'f';
						else if (s.toLowerCase().contains("p"))
							inject_at_next_pg = 'p';
						else if (s.toLowerCase().contains("c"))
							inject_at_next_pg = 'c';
						else if (s.toLowerCase().contains("b"))
							inject_at_next_pg = 'b';
						else if (s.toLowerCase().contains("w"))
							inject_at_next_pg = 'w';
						else if (s.toLowerCase().contains("x"))
							inject_at_next_pg = 'x';
						else if (s.toLowerCase().contains("a"))
							inject_at_next_pg = 'a';
					}
				}
				if ((bb.qt == q_.pg) && inject_at_next_pg != '-') {
					injector = "";
					if (inject_at_next_pg == 'y')
						injector = pf_injector_text__y;
					else if (inject_at_next_pg == 'f')
						injector = pf_injector_text__f;
					else if (inject_at_next_pg == 'p')
						injector = pf_injector_text__p;
					else if (inject_at_next_pg == 'c')
						injector = pf_injector_text__c;
					else if (inject_at_next_pg == 'b')
						injector = pf_injector_text__b;
					else if (inject_at_next_pg == 'w')
						injector = pf_injector_text__w;
					else if (inject_at_next_pg == 'x')
						injector = pf_injector_text__x;
					else if (inject_at_next_pg == 'a')
						injector = pf_injector_text__a;

					inject_at_next_pg = '-';
					injector_read_next = 0;
					bb.qt = q_.xx;
					bb.type = "xx";
				}

				bb = null;
				s = "";
				s_has_uni = false;

				huntingTwoAlpha = true;
				continue;
			}

			if (bb.qt == q_.lb) { // special processing for the 'question' type
				if (bb.size() == 0) { // this is the first charater
					assert (s.isEmpty());
					bb.add("" + c); // FYI the first character should always be an '*'
					continue;
				}
				if (bb.size() == 1) { // this is the second charater
					assert (s.isEmpty());
					if ('A' <= c && c <= 'Z') { // to lower case
						c = (char) ((int) c + Aaa.upperLowerDif);
					}
					bb.add("" + c); // FYI the second character is the command charater
					continue;
				}
				if (c == '^') {
					bb.add(s.trim()); // trim added to stop card decode bug 2014 May 21
					s = "";
					continue;
				}
			}

			if (bb.qt == q_.nt || bb.qt == q_.at) {
				if (s.isEmpty()) {
					if ((bb.size() > 0) && (bb.uni != uni_c)) {
						bb.add(s); // yes empty - this an inline blank line

						bb = new BarBlock("at", bb.lineNumber);
						bbAy.add(bb);
					}
				}
				else if ((s_has_uni != uni_c) || ((bb.size() > 0) && (bb.uni != uni_c))) {
					bb.uni = s_has_uni;
					bb.add(s);
					s = "";

					bb = new BarBlock("at", bb.lineNumber);
					bbAy.add(bb);
				}
				s_has_uni = uni_c;
				s += c;
				continue;
			}

			s_has_uni |= uni_c;

			s += c; // add it to the current string NOT setting the uni flag

			@SuppressWarnings("unused")
			int z = 0;

		}
	}

	// @formatter:off
	
	static String top  = "|fp||cp||cs||lg||ht|s||at|@4@0@1^^|ht|x|at|^^^a  ";	
	static String tail = "|ht|a|pg||";  // must end in a pg  to replace the one xx'ed out
	
	static String pf_injector_text__y =
			  top 
			+ "To  explore   click  {^*b Enter the Deal ^*n}  then click  {^*b Edit ^*n} or {^*b Play ^*n} "
			+ " and a CARD  |cp|blue|at|    ^*bOR^*n    |cp||at| just click  {^*b > ^*n}  to  ^*bReview^*n"
		    + tail;  
	
	static String pf_injector_text__b =
			  top 
			+ "You can  PLAY  from the start using the  { ^*b 1st ^*n }  button"
			+ "   |cp|blue|at|   ^*bOR^*n   |cp||at|   PLAY  out the rest using the  { ^*b Cont ^*n }  button "
		    + tail;
	
	static String pf_injector_text__c =
			  top 
			+ "You can continue and  PLAY  out the rest of the hand "
			+ "using the  { ^*b Cont ^*n }  button."
		    + tail;
	
	static String pf_injector_text__f =
			  top 
			+ "You can  PLAY  out the hand from near the opening lead by using the  { ^*b 1st ^*n }  button."
		    + tail;
	
	static String pf_injector_text__p =
			  top 
			+ "You can  PLAY  out the hand from the  current point in the play  by using the  { ^*b 1st ^*n }  button."
		    + tail;
	
	static String pf_injector_text__w =
			  top 
			+ "To  PLAY  out the hand starting from around the 1st lead"
			+ "  click  {^*b Enter the Deal ^*n}  then click on  {^*b Play ^*n}"
			+ tail;
	
	static String pf_injector_text__x =
			  top 
			+ "To play out the rest  click  {^*b Enter the Deal ^*n}  then "
			+ "click on the  RIGHT HAND  end of the  ^*bNavbar^*n  then on  {^*b Play ^*n}"
			+ tail;
	
	static String pf_injector_text__a =
			  top 
			+ "All restrictions on the three buttons   { ^*b 1st ^*n }     { ^*b Cont ^*n }"
			+ "   and   { ^*b Enter the Deal ^*n }   are now lifted."
			+ tail;
	
	// @formatter:on

	/**   
	 */
	public void pbnFileToBbArray(InputStream fis, String parentPath, String frName, String[] sRtn) throws IOException {
		// ==============================================================================================

		/**
		 *    We only read the board numbers and the cards [hand] nothing else
		 *    Currently the only intended use for this is to read the output of 
		 *    Hans van Staveren  now very old dealer app
		 *    
		 *    http://henku.home.xs4all.nl/html/production.html
		 *    
		 */
		String s = "";
		int i;

		sRtn[0] = "";
		sRtn[1] = "";

		StringBuilder protoLine = new StringBuilder();

		BarBlock bb;

		boolean eol = false;
		boolean end_of_file = false;

		boolean firstEvent = true;
		boolean firstDealer = true;

		int lineNumber = 1; // goes into the bar block for diagnostics

		String boardNo = "";
		String linVul = "";
		String linDealerDigit = "";
		char linDealerChar = 'N';

		int linDealer = 3;

		bb = new BarBlock("qx", 0);
		bb.add(" ");
		bbAy.add(bb);

//		char kib_seat = 0;
		int kib_offset_from_south = 0;
		int kib_offset_from_dealer = 2;

		String orig_script_filename = "";

		int rotate = 0;
		int count = 0;

		int new_deal_numb = 0;

		while (end_of_file == false) {

			i = fis.read();
			if (i == -1) {
				end_of_file = true;
				eol = true;
			}
			else if (i == newLine) {
				lineNumber++;
				eol = true;
			}
			else if (i == carriageReturn) {
				eol = true;
			}
			else {
				protoLine.append((char) i);
				continue;
			}

			if (eol == false || protoLine.length() == 0)
				continue;

			eol = false;

			s = "" + protoLine;
			protoLine = new StringBuilder();

			s = s.trim();

			if (s.startsWith("[") == false || s.endsWith("]") == false)
				continue;
			s = s.substring(1, s.length() - 1);

			s = s.trim();

			int firstSpace = s.indexOf(' ');

			String lineType = s.substring(0, firstSpace);

			String data = s.substring(firstSpace).trim();

			if (data.startsWith("\"") == false || data.endsWith("\"") == false)
				continue;

			data = data.substring(1, data.length() - 1).trim();

			if (lineType.contentEquals("Event") && firstEvent) {
				// @formatter:off
				firstEvent = false;

				String low_data = data.toLowerCase();
				kib_offset_from_south = 0;
				     if (low_data.contains("_w.txt,"))  kib_offset_from_south = 1;
				else if (low_data.contains("_n.txt,"))  kib_offset_from_south = 2;
				else if (low_data.contains("_e.txt,"))  kib_offset_from_south = 3;

				orig_script_filename = data;
				String smarker = "with file ";

				int start = orig_script_filename.indexOf(smarker);
				int end = orig_script_filename.lastIndexOf(".txt") + 4;
				if (start > 0 && end > 4 && start < end) {
					start += smarker.length();
					orig_script_filename = orig_script_filename.substring(start, end);
					sRtn[1] = orig_script_filename;

					// String sdm = ", seed ";
					// orig_seed = data.substring(end  + sdm.length());
				}
				else {
					orig_script_filename = "No_valid_dealer_script_filename_found_in_first_line_of_pbn_file.txt";
					sRtn[1] = frName;
				}
				
				String tw = "";
				// @formatter:off
				switch (App.pbnFixedQuarterTurns) {
					default:tw += ""; break; // 0
					case 1: tw += "(ew)  "; break;
					case 2: tw += "(sn)  "; break;
					case 3: tw += "(we)  "; break;
				}

				String rt = "";
				switch (App.pbnRotateWhenLoading) {
					default:rt += tw + ""; break; // 0
					case 1: rt += tw + "+  succesive deals  90  rotation"; break; 
					case 2: rt += tw + "+  succesive deals  180  rotation"; break;
				}

				String rt2 = "";
				switch (App.pbnRotateWhenLoading) {
					default:rt2 += " ^^^^^^^^^^ ^b " + tw; break; // 0
					case 1: rt2 += " ^^^^^^^^^^ ^b " + tw + "inc  90  deg rotation"; break; 
					case 2: rt2 += " ^^^^^^^^^^ ^b " + tw + "inc  180  deg rotation"; break;
				}
				// @formatter:on
// @formatter:off

				String main_text =
"This 'pbn' file feature of aaBridge is designed to work only with very simple .pbn files such as those made by Hans van Staveren's "
+ "dealer app and ACBL issued hands.   Only the   ^*bHands^*n,  ^*bDeclarer^*n  and  ^*bVunerability^*n  are read. "
+ "^^^*bALL^*n  bidding and play  are ignored. "

+ "       This  .pbn  file was generated using a script file named -^^^^"
+ "^c ^*b" + orig_script_filename + "^*n"
+ "  ^r" + rt	

+ "^^^^" 
+ "When moving from each deal to the next (within these .pbn files) you can use "
+ "the   ^*bAuto Enter^*n  button ^^ to automatically  ^*bEnter the Deal^*n  as you move through the generated deals.    "
+ "^^"
+ "^^Doing this gives you easy access to the  ^*bAnalyse^*n  and  ^*bShuf Op^*n  buttons.^^"
+ "In addition you, can then click on each seats  'name area'  to easily view only that hand."	
+ "^^^^See the document    ";
				
	            bb = new BarBlock("cr", 0); bb.add("1200"); bbAy.add(bb);
				bb = new BarBlock("cg", 0); bb.add("1255"); bbAy.add(bb);
				bb = new BarBlock("cb", 0); bb.add("1255"); bbAy.add(bb);

//				bb = new BarBlock("fh", 0); bb.add("740"); bbAy.add(bb);
//				bb = new BarBlock("fm", 0); bb.add("7"); bbAy.add(bb);

				bb = new BarBlock("mn", 0); bb.add("aaBridge   SIMPLE   .pbn  File Reader"); bbAy.add(bb);
				bb = new BarBlock("bt", 0); bb.add(""); bbAy.add(bb);
				bb = new BarBlock("ht", 0); bb.add("e"); bbAy.add(bb);
				bb = new BarBlock("lg", 0); bb.add("m"); bbAy.add(bb);
				bb = new BarBlock("at", 0); bb.add("^b@2^z@3"); bbAy.add(bb);
				bb = new BarBlock("at", 0); bb.add(main_text); bbAy.add(bb);
				
				bb = new BarBlock("cp", 0); bb.add("red"); bbAy.add(bb);
				bb = new BarBlock("at", 0); bb.add("^*b Self Practice with aaBridge ^*n"); bbAy.add(bb);
				bb = new BarBlock("cp", 0); bb.add(""); bbAy.add(bb);
				
				bb = new BarBlock("at", 0); bb.add("    and the file   "); bbAy.add(bb);
				
				bb = new BarBlock("cp", 0); bb.add("green"); bbAy.add(bb);
				bb = new BarBlock("at", 0); bb.add("^*b dealer_scripts.zip ^*n"); bbAy.add(bb);
				bb = new BarBlock("cp", 0); bb.add(""); bbAy.add(bb);

				bb = new BarBlock("at", 0); bb.add("   both in the"); bbAy.add(bb);
				bb = new BarBlock("cs", 0); bb.add("1"); bbAy.add(bb);
				bb = new BarBlock("n#", 0); bb.add("3"); bbAy.add(bb);
				bb = new BarBlock("at", 0); bb.add("^^^d { "); bbAy.add(bb);
				bb = new BarBlock("n#", 0); bb.add("4"); bbAy.add(bb);
				bb = new BarBlock("at", 0); bb.add("^*f openPage_DocCollection,  aaBridge Document Collection^*n"); bbAy.add(bb);
				bb = new BarBlock("n#", 0); bb.add("2"); bbAy.add(bb);
				bb = new BarBlock("at", 0); bb.add("  ^^ }"); bbAy.add(bb);
				bb = new BarBlock("cs", 0); bb.add(""); bbAy.add(bb);
				bb = new BarBlock("n#", 0); bb.add("4"); bbAy.add(bb);
				bb = new BarBlock("at", 0); bb.add("   click the button just above. ^^"); bbAy.add(bb);

//				bb = new BarBlock("at", 0); bb.add("^n This file was generated from a  "); bbAy.add(bb);
//				bb = new BarBlock("fp", 0); bb.add("7"); bbAy.add(bb);
//				bb = new BarBlock("at", 0); bb.add("^*b dealer script. ^*n"); bbAy.add(bb);

				bb = new BarBlock("pg", 0); bb.add(""); bbAy.add(bb);
				bb = new BarBlock("st", 0); bb.add(""); bbAy.add(bb);

				bb = new BarBlock("nt", 0); bb.add("^^^b.pbn  from      " + orig_script_filename + rt2); bbAy.add(bb);
				// @formatter:on

				sRtn[0] += "nt|^^^b.lin  from      " + orig_script_filename + rt2 + "|" + Zzz.get_lin_EOL();
			}
			else if (lineType.contentEquals("Board")) {
				boardNo = data;
			}
			else if (lineType.contentEquals("Dealer")) {

				rotate = App.pbnFixedQuarterTurns + count % 4;
				count += App.pbnRotateWhenLoading; // for next time

				linDealerChar = data.charAt(0);
				switch (linDealerChar) {
				// @formatter:off
					case 'S':  linDealer = 1;  break;
					case 'W':  linDealer = 2;  break;
					case 'N':  linDealer = 3;  break;
					case 'E':  linDealer = 4;  break;
				    // @formatter:on		
				}

				if (firstDealer) {
					firstDealer = false;
					kib_offset_from_dealer = ((linDealer - 1) + kib_offset_from_south) % 4;
				}

				linDealer = (((linDealer - 1) + rotate) % 4) + 1;
				linDealerDigit = String.valueOf(linDealer);

			}
			else if (lineType.contentEquals("Vulnerable")) {
				linVul = "O";
				if (data.contentEquals("NS"))
					linVul = (rotate % 2 == 0) ? "N" : "E";
				else if (data.contentEquals("EW"))
					linVul = (rotate % 2 == 0) ? "E" : "N";
				else if (data.contentEquals("Both") || data.equals("All")) {
					linVul = "B";
				}
			}
			else if (lineType.contentEquals("Deal")) {

				new_deal_numb++;

				String deal_numb = (App.pbnRenumberAtReadin ? new_deal_numb + "" : boardNo);

				// @formatter:off
				bb = new BarBlock("qx", lineNumber); bb.add(deal_numb); bbAy.add(bb);
				bb = new BarBlock("rh", lineNumber); bb.add(""); bbAy.add(bb);
				bb = new BarBlock("ah", lineNumber); bb.add("Board " + deal_numb); bbAy.add(bb);

				ArrayList<String> theFourHands = pbnDealToLinBBDeal(data, linDealerDigit, rotate);
				
				bb = new BarBlock("md", lineNumber); bb.addAll(theFourHands); bbAy.add(bb);
				
				bb = new BarBlock("sv", lineNumber); bb.add(linVul); bbAy.add(bb);
				
				String kib_seat = "S";
				int offset = ((linDealer - 1) + kib_offset_from_dealer /* lin dealer has the rotate already */) % 4;
				     if (offset == 1)  kib_seat = "W";
				else if (offset == 2)  kib_seat = "N";
				else if (offset == 3)  kib_seat = "E";
				     
				bb = new BarBlock("sk", lineNumber); bb.add(kib_seat); bbAy.add(bb);
				bb = new BarBlock("sk", lineNumber); bb.add(""); bbAy.add(bb);
				
				bb = new BarBlock("pg", lineNumber); bb.add(""); bbAy.add(bb);
				// @formatter:on

				/*** 
				 * Create a temp deal in order to save this deal to a lin file
				 * so making the conversion of the whole pbn file to a lin file. 
				 */

				Deal dealTemp = new Deal(0);
				dealTemp.fillDealExternal(theFourHands, Deal.yesFill, 0);
				dealTemp.ahHeader = "Board " + deal_numb;
				dealTemp.signfBoardId = deal_numb;
				dealTemp.displayBoardId = deal_numb;
				dealTemp.setVulnerability(linVul);

				boolean origVal = App.saveAsBboUploadFormat;
				try {
					StringWriter sw = new StringWriter();
					BufferedWriter bw = new BufferedWriter(sw);

					App.saveAsBboUploadFormat = true;

					saveDealAsSingleLinFileBW(dealTemp, kib_seat, bw);

					bw.flush();
					sw.flush();
					sRtn[0] += sw.toString();
				} finally {
					App.saveAsBboUploadFormat = origVal;
				}

				@SuppressWarnings("unused")
				int z = 0;

				boardNo = "";
				linVul = "";
				linDealerDigit = "";
			}

		}

	}

	static char sutChar[] = { 's', 'h', 'd', 'c' };

	ArrayList<String> pbnDealToLinBBDeal(String s, String rotatedLinDealerDigit, int rotate) {

		ArrayList<String> ay = new ArrayList<String>();

		if (s.length() < 2 || s.charAt(1) != ':') {
			ay.add("");
			return ay;
		}

		int outAdj = 0;

		String outH = rotatedLinDealerDigit;

		switch (s.charAt(0)) {
		// @formatter:off
			case 'S':  outAdj = 0;  break;
			case 'W':  outAdj = 3;  break;
			case 'N':  outAdj = 2;  break;
			case 'E':  outAdj = 1;  break;
		    // @formatter:on		
		}

		outAdj = (outAdj + (4 - rotate)) % 4;

		s = s.substring(2).trim();

		String hands[] = s.split(" ");

		if (hands.length < 4) {
			String h2[] = { "", "", "", "" };
			for (int i = 0; (i < hands.length); i++) {
				h2[i] = hands[i];
			}
			hands = h2;
		}

		for (int k = 0; k < 4; k++) {
			int j = (k + outAdj) % 4;
			String hand = hands[j];
			String suiits[] = hand.split("\\.");
			for (int i = 0; (i < 4 && i < suiits.length); i++) {
				outH += sutChar[i] + suiits[i];
			}
			ay.add(outH);
			outH = "";
		}

		return ay;
	}

}
