package com.rogerpf.aabridge.model;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
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

	public String linType;

	public ArrayList<BarBlock> bbAy = new ArrayList<BarBlock>();

	public Deal virginDeal = null;

	public String headingInfo = "";

	public boolean twoTeams = false;

	/**   
	 */
	public class BarBlock extends ArrayList<String> {
		// ---------------------------------- CLASS -------------------------------------
		private static final long serialVersionUID = 1L;

		public String type;
		public int qt;
		public int lineNumber;

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
	 *    Constructor  -  the main construcor
	 */
	public Lin(InputStream fis, String parentPath, String frName, boolean isLin) throws IOException {
		// ==============================================================================================

		if (isLin) {
			linFileToBbArray(fis, parentPath, frName);
			calcLinType();
		}
		else { // assume pbn
			pbnFileToBbArray(fis, parentPath, frName);
			calcLinType();
			@SuppressWarnings("unused")
			int z = 0;
		}
	}

	/**   
	 */
	public void calcLinType() throws IOException {
		// ==============================================================================================

		int qx_count = 0;
		int bt_count = 0;
		int md_count = 0;
		int at_count = 0;
		@SuppressWarnings("unused")
		int nt_count = 0;

		if (bbAy.size() < 2) { // 2 is probably too low
			String s = "calcLinType - too few commands in lin file";
			System.out.println(s);
			throw new IOException(s);
		}

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
			if (t == q_.bt) bt_count++;
			if (t == q_.md) md_count++;
			if (t == q_.at) at_count++;
			if (t == q_.nt) nt_count++;
		}
		// @formatter:on

		if (bt_count > 0 || at_count > 0) {
			linType = Lin.FullMovie;
		}
		else if (md_count == 1) { // so single deals are NEVER vugraph
			linType = Lin.SimpleDealSingle;
		}
		else if (vgFirst && qx_count > 1 && qx_count == md_count) {
			linType = Lin.VuGraph;
		}
		else if (vgFirst && qx_count > 1 && qx_count > md_count && md_count > 1 && all_qx_start_with_o_or_c) {
			linType = Lin.VuGraph; // incomplete tourny records
		}
		else {
			linType = Lin.Other; // for now we will call all others - other
		}
	}

	// @formatter:off
	static final String pf_injector_text =
			  "|fp||cp||cs||lg||ht|s||at|@4@0@1^^|ht|x|at|^^^a To  explore  /  PLAY  the hand"
			+ "  -  click  {^*b Enter the Deal ^*n}  then click  {^*b Edit ^*n}"
			+ " and click a CARD"    // must end in a pg  to replace the one xx'ed out
	
	        + " |cp|blue|at|   ^*bOR^*n   |cp||at| click  {^*b > ^*n}  to Review|ht|a|pg||";   // must end in a pg  to replace the one xx'ed out
	
	// @formatter:on

	/**   
	 */
	static public void saveDealAsSingleLinFile(Deal deal, BufferedWriter w) throws IOException {
		// ===================================================================================

		w.write("st||"); // st => standard table /
		w.write(Zzz.lin_EOL);

		w.write("pn|");
		for (Hand hand : deal.rota[Dir.South.v]) {
			w.write(hand.playerName);
			if (hand.compass != Dir.East)
				w.write(",");
		}
		w.write("|");
		w.write(Zzz.lin_EOL);

		// Headers and our invented Display Board Number
		w.write("rh||");
		String ahText = Aaa.cleanString(deal.ahHeader, true /* true => spaceOk */);
		if (!ahText.isEmpty()) {
			w.write("ah|" + ahText.trim() + "|");
		}
		String signfBoardId = (App.deal.signfBoardId.trim().isEmpty() ? "Board" : App.deal.signfBoardId);

		if (deal.displayBoardId.length() > 0) {
			w.write("ah|" + signfBoardId + " " + deal.displayBoardId + "|");
		}
		else {
			w.write("ah|" + signfBoardId + " " + deal.realBoardNo + "|");
		}

		// sv => side vulnerability
		w.write("sv|");

		if (deal.vulnerability[Dir.NS] && deal.vulnerability[Dir.EW])
			w.write("b|");
		else if (deal.vulnerability[Dir.NS])
			w.write("n|");
		else if (deal.vulnerability[Dir.EW])
			w.write("e|");
		else
			w.write("-|");

		w.write("sk|" + deal.youSeatHint.toLowerChar() + "|");
		w.write(Zzz.lin_EOL);

		w.write("md|"); // test A BRIDGE TABLE is displayed md => make deal ?

		// now as the **** first character of the South hand defintion **** we write the dealer id
		// for lin '1'=South, '4'=East, aaBridge internal 2=South, 1=East

		w.write("" + (char) (((deal.dealer.v + 2) % 4 + 1 + '0')));

		// note we might want Easts hand for manual editing so NOT omitted
		for (Hand hand : deal.rota[Dir.South.v]) {
			w.write(hand.cardsForLinSave());
			if (hand.compass != Dir.East)
				w.write(",");
		}
		w.write("|");
		w.write(Zzz.lin_EOL);

		// Add the bidding

		w.write(deal.bidsForLinSave());
		w.write(Zzz.lin_EOL);
		w.write("pg||");
		w.write(Zzz.lin_EOL);

		// Add the card play

		w.write(deal.cardPlayForLinSave());
		// adds its own Zzz.lin_EOL // w.write(Zzz.lin_EOL);

//		w.write(Zzz.lin_EOL);

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
		int i;
		char c = 0, a0 = 0, a1 = 0;
		char eol = 0;
		char eol_skip = 0;
		BarBlock bb = null;

		int e2Seen = 0;
		int e1 = 0;

		boolean huntingTwoAlpha = true;

		int lineNumber = 1;

		String injector = "";
		int injector_read_next = 0;
		boolean inject_at_next_pg = false;

		while (true) {

			if (injector.length() > injector_read_next) {
				c = injector.charAt(injector_read_next++);
				i = c;
			}
			else {
				i = fis.read();
				if (i == -1)
					break;
				c = (char) i;
			}

			// System.out.print(c);
			// System.out.println(c + " " + i);

			// @formatter:off  - Uggly attempt to clean up UTF8 encodings for right single quote
			if (e2Seen > 0) {
				if (e2Seen == 1) {
					e2Seen++;
					e1 = i;
					continue;
				}
				e2Seen = 0;
				System.out.println( lineNumber + " >>>>>>>>>>>>>>>>>>>>>>>> " + (int)e1 + "  " + (int)c );
				if (e1 == 0x80 && i == 0x94) {
					s += "@";
					c = '-';
				} // DROP THROUGH // dash
				else if (e1 == 0x80 && i == 0x98) c = '\''; // DROP THROUGH // standard asci single quote
				else if (e1 == 0x80 && i == 0x99) c = '\''; // DROP THROUGH // standard asci single quote
				else if (e1 == 0x80 && i == 0x9c) c = '"'; // DROP THROUGH // standard asci double quote
				else if (e1 == 0x80 && i == 0x9d) c = '"'; // DROP THROUGH // standard asci double quote
				else
					continue; // we eat them all
			}
			if (i == 0xe2) {
				// System.out.println("e2 e2 e2" + c + " " + i);
				e2Seen = 1; // uggly ..... we eat the next two chars
				continue;
			} 
			
			if (i == 0xc2) {
				// System.out.println("c2 seen);font
				// uggly ..... we just eat it   copyright sometimes next char
				continue;
			} 
			
			if (c == 133) { // 133 => 0x85  
				s += "..";
				c = '.'; // elipses
			}
			
			if (c == 149) { // 149 => 0x95
				s += "@";
				c = '.'; // some kind of dot
			}
			
			if (c == 150) { // 150 => 0x96
				c = '-'; // a standard hyphen
			}
			
			if (c == 151) { // 151 => 0x97
				s += "@";
				c = '-'; // aaBridge code for "em dash"
			}
			
			//       0x91        0x92
			if (c == 145 || c == 146 || c == 226 || c == 128) { // a circumflex or back/fronta single quote  => std single quote
				c = '\'';
			}
            //       0x93        0x94
			if (c == 147 || c == 148) { // open close double quotes (code page 1251 windows ?)
				c = '"';
			}

			// @formatter:on

			if (eol == 0) {
				if ((c == carriageReturn || c == newLine)) {
					eol = c;
					eol_skip = (c == carriageReturn) ? newLine : carriageReturn;
					lineNumber++;
					continue;
				}
			}
			else if (c == eol_skip) {
				continue;
			}
			else if (c == eol) {
				lineNumber++;
			}

			if (huntingTwoAlpha) {
				if (c == eol || c == ' ' || c == 0x09 /* tab*/)
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

				s = "";
				huntingTwoAlpha = false;
				continue;
			}

			assert (bb != null);

			if (c == ',') {
				if (bb.qt == q_.nt || bb.qt == q_.at || bb.qt == q_.mn || bb.qt == q_.sb || bb.qt == q_.ia || bb.qt == q_.an || bb.qt == q_.lb
						|| bb.qt == q_.rc) {
					s += c; // treat as normal
				}
				else {
					bb.add(s); // treat as comma separated variable
					s = "";
				}
				continue;
			}

			if (c == eol) {
				if (bb.qt == q_.nt || bb.qt == q_.at || bb.qt == q_.mn) { // new text add text
					bb.add(s); // treat as a multi section at
					s = "";
				}
				continue; // skip for all other fields
			}

			if (c == bar) {
				bb.add(s);
				if (bb.qt == q_.pf) {
					inject_at_next_pg = (s.trim().length() > 0) /* && s.toLowerCase().contains("y") */;
				}
				if ((bb.qt == q_.pg) && inject_at_next_pg) {
					inject_at_next_pg = false;
					injector = pf_injector_text;
					injector_read_next = 0;
					bb.qt = q_.xx;
					bb.type = "xx";
				}
				bb = null;
				s = "";
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

			s += c; // add it to the current string
		}
	}

	/**   
	 */
	public void pbnFileToBbArray(InputStream fis, String parentPath, String frName) throws IOException {
		// ==============================================================================================

		/**
		 *    We are only read the board numbers and the cards [hand] nothing else
		 *    Currently the only intended use for this is to read the output of 
		 *    Hans van Staveren  now very old dealer app
		 *    
		 *    http://henku.home.xs4all.nl/html/production.html
		 *    
		 */
		String s = "";
		int i;

		StringBuilder protoLine = new StringBuilder();

		BarBlock bb;

		boolean eol = false;
		boolean end_of_file = false;

		boolean firstEvent = true;

		int lineNumber = 1; // goes inot the bar block for diagnostics

		String boardNo = "";
		String linVul = "";
		String linDealerDigit = "";

		bb = new BarBlock("qx", 0);
		bb.add(" ");
		bbAy.add(bb);

		bb = new BarBlock("gf", 0);
		bb.add("n");
		bbAy.add(bb);

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

				String main_text =
"This 'pbn' reading feature of aaBridge is designed to work only with  Hans van Staveren's  "
+ "now very old dealer app.^^^^Only the   ^*bHands^*n,  ^*bDeclarer^*n  and  ^*bVunerability^*n  are read. "
+ "    ^*bALL^*n  bidding and play  are skipped.";
				bb = new BarBlock("mn", 0); bb.add("Simple  pbn  Reader"); bbAy.add(bb);
				bb = new BarBlock("bt", 0); bb.add(""); bbAy.add(bb);
				bb = new BarBlock("ht", 0); bb.add("f"); bbAy.add(bb);
				bb = new BarBlock("at", 0); bb.add("^b@2^z@3"); bbAy.add(bb);
				bb = new BarBlock("at", 0); bb.add(main_text); bbAy.add(bb);
				bb = new BarBlock("at", 0); bb.add("^^^^^^^^"); bbAy.add(bb);
				bb = new BarBlock("at", 0); bb.add("^f" + data); bbAy.add(bb);
				bb = new BarBlock("pg", 0); bb.add(""); bbAy.add(bb);
				bb = new BarBlock("st", 0); bb.add(""); bbAy.add(bb);
				bb = new BarBlock("nt", 0); bb.add("^^^^"); bbAy.add(bb);
				// @formatter:on
			}
			else if (lineType.contentEquals("Board")) {
				boardNo = data;
			}
			else if (lineType.contentEquals("Dealer")) {
				linDealerDigit = "";
				switch (data.charAt(0)) {
				// @formatter:off
					case 'S':  linDealerDigit = "1";  break;
					case 'W':  linDealerDigit = "2";  break;
					case 'N':  linDealerDigit = "3";  break;
					case 'E':  linDealerDigit = "4";  break;
				    // @formatter:on		
				}
			}
			else if (lineType.contentEquals("Vulnerable")) {
				linVul = "-";
				if (data.contentEquals("NS"))
					linVul = "n";
				else if (data.contentEquals("EW"))
					linVul = "e";
				else if (data.contentEquals("Both") || data.equals("All")) {
					linVul = "b";
				}
			}
			else if (lineType.contentEquals("Deal")) {
				// @formatter:off
				bb = new BarBlock("qx", lineNumber); bb.add(boardNo); bbAy.add(bb);
				bb = new BarBlock("rh", lineNumber); bb.add(""); bbAy.add(bb);
				bb = new BarBlock("ah", lineNumber); bb.add("Board " + boardNo); bbAy.add(bb);
				bb = new BarBlock("sv", lineNumber); bb.add(linVul); bbAy.add(bb);
				bb = new BarBlock("md", lineNumber); bb.addAll(pbnDealToLinBBDeal(data, linDealerDigit)); bbAy.add(bb);
				bb = new BarBlock("pg", lineNumber); bb.add(""); bbAy.add(bb);
				boardNo = "";
				linVul = "";
				linDealerDigit = "";
				// @formatter:on
			}

		}

	}

	static char sutChar[] = { 's', 'h', 'd', 'c' };

	ArrayList<String> pbnDealToLinBBDeal(String s, String linDealerDigit) {

		ArrayList<String> ay = new ArrayList<String>();

		if (s.length() < 2 || s.charAt(1) != ':') {
			ay.add("");
			return ay;
		}

		int outAdj = 0;

		String outH = linDealerDigit;

		switch (s.charAt(0)) {
		// @formatter:off
			case 'S':  outAdj = 0;  break;
			case 'W':  outAdj = 3;  break;
			case 'N':  outAdj = 2;  break;
			case 'E':  outAdj = 1;  break;
		    // @formatter:on		
		}

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
