package com.rogerpf.aabridge.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

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
	public String filenaDisp = "";

	public String linType;

	public ArrayList<BarBlock> bbAy = new ArrayList<BarBlock>();

	public Deal virginDeal = null;

	public String headingInfo = "";

	public boolean twoTeams = false;

	public boolean cameFromPbn = false;

	public String origFilename = "";
	public String origFilename_noExt = "";
	public String origSourceFolder = "";

	public String merge_filename_noExt = "";

	public String getMultiDealDestFolder() {
		if (App.forceSaveMultiDealToSavesFolder || origSourceFolder.isEmpty())
			return App.realSaves_folder;
		else
			return origSourceFolder;
	}

	public int qx_count = 0;
	public int bt_count = 0;
	public int bv_count = 0;
	public int md_count = 0;
	public int at_count = 0;
	public int nt_count = 0;
	public int mb_count = 0;
	public int ae_count = 0;
	public boolean rt_found = false;
	public int first_rt_value = 0;

	public boolean xf_found = false;
	public boolean xe_found = false;

	String lin__zd_dealer_script_name = "";
	String lin__zg_merge_list_name = "";

	public class StringAndPos {
		String s;
		int pos;

		public StringAndPos(String s_v) {
			this.s = s_v;
			this.pos = 0;
		}
	}

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
	public Lin(InputStream fis, String parentPath, String frName, boolean fromPbn, String srcFolder) throws IOException {
		// ==============================================================================================

		setFileAndDispName(frName);

		origSourceFolder = srcFolder;
		origFilename = frName;
		origFilename_noExt = App.removeExtension(frName);

		if (!App.thisAppBaseFolder.isEmpty() && origSourceFolder.contains(App.thisAppBaseFolder) /* debug case */) {
			// Probably running in eclipse  -  ugly but it works
			origSourceFolder = App.realSaves_folder;
		}

		if (fromPbn == false) { // so is a 'normal' lin file
			cameFromPbn = false;

			linFileToBbArray(fis, parentPath, frName);

			calcLinType();
		}

		else {
			/**
			 *  Read in the pbn file and store it as a bbArray (lin file)
			 *  the aaArray is filled by the  pbnFileToBbArray  call below
			 */

			cameFromPbn = true;

			pbnFileToBbArray(fis, parentPath, frName);

			linType = Lin.Other;
		}
	}

	public String getMultiDealSaveAs_filename(String new_ext, String multi_folder, String multi_id) {
		// ==============================================================================================

		String core_noExt;

		final String const_prefix = "yy___";

		if (App.mg.cameFromPbn && new_ext.contains("lin") && origFilename_noExt.startsWith(const_prefix)) {
			core_noExt = origFilename_noExt;
		}
		else if (App.useCreationNameForSave) {
			core_noExt = App.removeExtension(lin__zg_merge_list_name);
			if (core_noExt.isEmpty()) {
				core_noExt = App.removeExtension(lin__zd_dealer_script_name);
				if (core_noExt.isEmpty()) {
					core_noExt = origFilename_noExt;
				}
			}
		}
		else {
			core_noExt = origFilename_noExt;
		}

		String rt = "";

		String prefix = (App.prependYyySavePrefix && !core_noExt.startsWith(const_prefix)) ? const_prefix : "";

		if (new_ext.contains("pbn")) {
			return getMultiDealDestFolder() + prefix + core_noExt + ".pbn";
		}

		if (App.includeRotationsSetBelow) {

			// @formatter:off
			switch (App.fixedQuarterTurns) {
				default:rt += "___--"; break; // 0
				case 1: rt += "___ew"; break;
				case 2: rt += "___sn"; break;
				case 3: rt += "___we"; break;
			}
	
			switch (App.rotateWhenSaving) {
				default:rt += "___0_rotation"; break; // 0
				case 1: rt += "__90_rotation"; break;
				case 2: rt += "_180_rotation"; break;
			}
			// @formatter:on
		}

		else {

			// @formatter:off
			switch (App.linfileSaveFormat) {
				case 0: rt += "__BBO"; break;
				case 1: rt += "__Std"; break;
				case 2: rt += "__Prob"; break;
				case 3: rt += "__Prb_no-op"; break;
				default: rt += "__type_unknown"; break;
			}
			// @formatter:on
		}

		String folder = getMultiDealDestFolder();

		if (multi_folder.length() > 0) {
			folder += multi_folder + File.separator;
		}

		return folder + prefix + core_noExt + rt + multi_id + ".lin";
	}

	/**   
	 */
	public void calcLinType() throws IOException {
		// ==============================================================================================

		qx_count = 0;
		bt_count = 0;
		bv_count = 0;
		md_count = 0;
		at_count = 0;
		nt_count = 0;
		mb_count = 0;
		ae_count = 0;
		rt_found = false;
		first_rt_value = 0;

		boolean zd_found = false;
		boolean zg_found = false;

		linType = Lin.Other;

		if ((bbAy.size() >= 1) || ((bbAy.size() == 1) && (bbAy.get(0).qt == q_.md))) {
			// we are good to go
		}
		else {
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
			else if (t == q_.bt) bt_count++;
			else if (t == q_.bv) bv_count++;
			else if (t == q_.md) md_count++;
			else if (t == q_.at) at_count++;
			else if (t == q_.nt) nt_count++;
			else if (t == q_.mb) mb_count++;
			else if (t == q_.ae) ae_count++;
			else if (t == q_.zd) { if (!zd_found) {zd_found = true; lin__zd_dealer_script_name = bb.get(0); } }
			else if (t == q_.zg) { if (!zg_found) {zd_found = true; lin__zg_merge_list_name = bb.get(0); } }
			else if (t == q_.xf) xf_found = true;
			else if (t == q_.xe) xe_found = true;
			else if (t == q_.vr) ae_count++;  // old name for AE command
			else if (t == q_.rt && !rt_found && !App.dlaeActive) {
				rt_found = true;
				if (bb.get(0).length() > 0) {
					first_rt_value = bb.get(0).charAt(0) - '0';
					if (first_rt_value < 0 || first_rt_value > 3)
						first_rt_value = 0;
				}
			};
		}
		// @formatter:on

		if (md_count == 1 && (at_count == 0 && nt_count == 0 || (ae_count > 0 && App.obeyAeCmd))) {
			linType = Lin.SimpleDealSingle;
		}
		else if (vgFirst && qx_count > 1 && qx_count == md_count) {
			linType = Lin.VuGraph;
		}
		else if (vgFirst && qx_count > 1 && qx_count > md_count && md_count > 1 && all_qx_start_with_o_or_c) {
			linType = Lin.VuGraph; // incomplete tourny records
		}
		else if (bt_count > 0 || bv_count > 1) {
			linType = Lin.FullMovie;
		}
		else {
			linType = Lin.Other; // for now we will call all others - other
		}
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

		String pfInjector = "";
		int pfInjector_read_next = 0;
		String inject_at_next_pg__sName = "";
		char inject_at_next_pg__ht = '-';

		InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
		BufferedReader br = new BufferedReader(isr);

		boolean uni_c = false;
		boolean s_has_uni = false;

		boolean vg_found = false;

		String current_lg_letter = "";

		HashMap<String, String> pf_collection = new HashMap<String, String>();

		add_builtin_pfs(pf_collection);

		HashMap<String, String> ss_collection = new HashMap<String, String>();

		ArrayList<StringAndPos> siInjector_ay = new ArrayList<StringAndPos>();

		while (true) {

			boolean c_valid = false;

			while (siInjector_ay.size() > 0) {
				int lastInd = siInjector_ay.size() - 1;
				StringAndPos snp = siInjector_ay.get(lastInd);

				if (snp.s.length() <= snp.pos) {
					siInjector_ay.remove(lastInd);
					continue;
				}
				c = snp.s.charAt(snp.pos++);
				uni_c = (c > Aaa.uniThreashold);
				c_valid = true;
				break;
			}

			if (c_valid) {
				; // we have it
			}
			else if (pfInjector.length() > pfInjector_read_next) {
				c = pfInjector.charAt(pfInjector_read_next++);
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
				if (c == eol || c == ' ' || c == 0x09 /* tab*/ || uni_c)
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
				// BBO vu-graphs commentators supply bad characters between the ! an the suit letter
				// this discards them - but is still ugly
				continue;
			}

			if (c == ',') {
				// @formatter:off				
				if (   bb.qt == q_.nt || bb.qt == q_.at || bb.qt == q_.mn || bb.qt == q_.sb 
					|| bb.qt == q_.ia || bb.qt == q_.an || bb.qt == q_.lb || bb.qt == q_.rc 
					|| bb.qt == q_.pd || bb.qt == q_.ss || bb.qt == q_.ah
			     /* || bb.qt == q_.pf */  ) 
				{				
					s += c; // treat as normal		
				}
				else {
					bb.add(s); // treat as comma separated variable
					s = "";
				}
				// @formatter:on		
				// s_has_uni is not reset
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

				if (bb.qt == q_.pd) { // The define a message cmd
					if (s.length() >= 2) {
						int com = s.indexOf(',');
						if (com != -1) {
							String sName = s.substring(0, com).trim().toLowerCase();
							if (sName.length() > 0) {
								String s1 = s.substring(com + 1);
								pf_collection.put(sName, s1.replace('`', '|'));
							}
						}
					}
				}

				else if (bb.qt == q_.pf) { // The pf mechanism ---------------------------------------------
					inject_at_next_pg__sName = "";
					inject_at_next_pg__ht = 'z';

					String sName = bb.get(0).trim().toLowerCase();
					if (sName.length() > 0) {
						String val = pf_collection.get(sName);
						if (val != null) {
							inject_at_next_pg__sName = sName;
						}
					}
					// read the ht letter if any
					if (bb.size() > 1) {
						char ht = (bb.get(1).trim() + "z").charAt(0);
						if ('a' <= ht && ht <= 'z') {
							inject_at_next_pg__ht = ht;
						}
					}

				}

				else if ((bb.qt == q_.pg) && (inject_at_next_pg__sName.length() > 0)) {
					String sName = inject_at_next_pg__sName;
					String val = pf_collection.get(sName);
					if (val != null && val.isEmpty()) {
						val = sName + "-UNDEFINED-pf";
					}
					// @formatter:off
					pfInjector =   form_top(inject_at_next_pg__ht)
							     + val
							     + form_tail(inject_at_next_pg__ht, current_lg_letter)
							     ;
					// @formatter:on

					inject_at_next_pg__sName = "";
					pfInjector_read_next = 0;
					bb.qt = q_.xx;
					bb.type = "xx";
				}

				else if (bb.qt == q_.ss) { // special processing for Set String -----------------
					if (s.length() >= 2) {
						int com = s.indexOf(',');
						if (com != -1) {
							String sName = s.substring(0, com).trim().toLowerCase();
							if (sName.length() > 0) {
								String s1 = s.substring(com + 1);
								ss_collection.put(sName, s1.replace('`', '|'));
							}
						}
					}
					bb.qt = q_.xx;
					bb.type = "xx";
				}

				else if (bb.qt == q_.vv) { // special processing for insert string
					s = s.trim().toLowerCase();
					if (s.length() >= 1) {
						String val = ss_collection.get(s);
						if (val != null) {
							siInjector_ay.add(new StringAndPos(val));
						}
					}
					bb.qt = q_.xx;
					bb.type = "xx";
				}

				else if (bb.qt == q_.uu) { // special processing for wide insert string
					if (bb.size() >= 2) {
						// process the prefix
						String s0 = bb.get(0).replace('`', '|');
						String sName = bb.get(1).trim().toLowerCase();
						if (sName.length() > 0) {
							String val = ss_collection.get(sName);
							if (val == null) {
								val = sName + "-UNDEFINDED-ss";
							}
							String s2 = (bb.size() == 2) ? "" : bb.get(2).replace('`', '|');
							siInjector_ay.add(new StringAndPos(s0 + val + s2));
						}
					}
					bb.qt = q_.xx;
					bb.type = "xx";
				}

				else if (bb.qt == q_.lg) {
					current_lg_letter = bb.get(0);
				}

				bb = null;
				s = "";
				s_has_uni = false;

				huntingTwoAlpha = true;
				continue;
			}

			if (bb.qt == q_.lb) { // special processing for the 'question' type
				if (bb.size() == 0) { // this is the first character
					assert (s.isEmpty());
					bb.add("" + c); // FYI the first character should always be an '*'
					continue;
				}
				if (bb.size() == 1) { // this is the second character
					assert (s.isEmpty());
					if ('A' <= c && c <= 'Z') { // to lower case
						c = (char) ((int) c + Aaa.upperLowerDif);
					}
					bb.add("" + c); // FYI the second character is the command character
					continue;
				}
				if (c == '^') {
					bb.add(s.trim()); // trim added to stop card decode bug 2014 May 21
					s = "";
					continue;
				}
			}

			if (bb.qt == q_.nt || bb.qt == q_.at) {
				if (bb.qt == q_.nt) {
					current_lg_letter = "";
				}
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

		}
		@SuppressWarnings("unused")
		int z = 0;

	}

	void add_builtin_pfs(HashMap<String, String> pf_collection) {
		// @formatter:off
		pf_collection.put("y",               pf_injector_text__y_yes           );
		pf_collection.put("all-res-lifted",  pf_injector_text__all_res_lifted  );
		pf_collection.put("cont-button",     pf_injector_text__cont_button     );
		pf_collection.put("both-first-cont", pf_injector_text__both_first_cont );
		pf_collection.put("first-button",    pf_injector_text__first_button    );
		pf_collection.put("mid-first",       pf_injector_text__mid_first       );
		pf_collection.put("show-all-always", pf_injector_text__show_all_always );
		pf_collection.put("rem-button",      pf_injector_text__rem_button      );
		pf_collection.put("enter-play",      pf_injector_text__enter_play      );
		pf_collection.put("any-time",        pf_injector_text__any_time        );
		// @formatter:on
	}

	String top_A = "|fp||cp||cs||lg||ht|";
	String top_B = "|bd|y|n^|5|at|@4@0@1^^|bd||ht|";
	String top_C = "|n^|3|at|^^^a ";

	String tail_A = "|ht|";
	String tail_B = "|n#|g|at|@4@0@1|ht|a|";

	private String form_top(char ch) {
		return top_A + ch + top_B + ch + top_C;
	}

	private String form_tail(char ch, String lg_letter) {
		return tail_A + ch + tail_B + "lg|" + lg_letter + "|pg||"; // must end in a pg to replace the one xx'ed out
	}

	// @formatter:off
	
	static String pf_injector_text__y_yes =
			  "To  explore   click  {^*b Enter the Deal ^*n}  then click  {^*b Edit ^*n} or {^*b Play ^*n} "
			+ " and a CARD  |cp|blue|at|    ^*bOR^*n    |cp||at| just click  {^*b > ^*n}  to  ^*bReview^*n";  
	
	static String pf_injector_text__all_res_lifted =
			  "Any restrictions on the three buttons   { ^*b 1st ^*n }     { ^*b Cont ^*n }"
			+ "   and   { ^*b Enter the Deal ^*n }   are now lifted.";
	
	static String pf_injector_text__both_first_cont =
			  "You can  PLAY  from the start using the  { ^*b 1st ^*n }  button"
			+ "   |cp|blue|at|   ^*bOR^*n   |cp||at|   PLAY  out the rest using the  { ^*b Cont ^*n }  button ";
	
	static String pf_injector_text__cont_button =
			  "You can continue and  PLAY  out the rest of the hand "
			+ "using the  { ^*b Cont ^*n }  button.";
	
	static String pf_injector_text__first_button = 
			"You can  PLAY  out the hand from near the opening lead by clicking the light blue  { ^*b 1st ^*n }  button (below).";		
	
	static String pf_injector_text__mid_first = 
			"You can  PLAY  out the hand from around the current position by clicking the light blue { ^*b 1st ^*n }  button (below).";
	
	static String pf_injector_text__show_all_always =
			  "To see all 4 hands  check  the   {    }|cp|red|at|^*b Show ALL Always ^*n|"
			+ "cp||at|  checkbox   in the FAR right hand column.";
	
	static String pf_injector_text__rem_button = 
			  "When in the deal|cp|red|at| ^*b click ^*n|cp||at| the  {^*b Rem ^*n}  "
			+ "button to remove any restrictions on  { ^*b 1st ^*n }   { ^*b Cont ^*n }  and  { ^*b Enter the Deal ^*n }";
	
	static String pf_injector_text__enter_play = "  You can click the { ^*b Enter the Deal ^*n }  "
			+ "button and ^*b THEN ^*n click  { ^*b Play ^*n }  on the LEFT,  to play the hand yourself.";
	
	static String pf_injector_text__any_time = "^c     With all these hands  - ^*b At any time ^*n|at| you can"
			+ "|cp|red|at| ^*b Click ^*n |cp||at|the  { ^*b 1st ^*n }  button below to play out the deal.";
	
	// @formatter:on

	String extract_marked_field(String line, String marker) {
		String front = "<" + marker + ">";
		String back = "</" + marker + ">";
		int f = line.indexOf(front);
		if (f < 0)
			return "";
		int e = line.indexOf(back);
		if (e < 0)
			return "";
		return line.substring(f + front.length(), e);
	}

	/**   
	 */
	public void pbnFileToBbArray(InputStream fis, String parentPath, String frName) throws IOException {
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

		StringBuilder protoLine = new StringBuilder();

		BarBlock bb;

		boolean eol = false;
		boolean end_of_file = false;

		int firstIfOne = 0;

		int lineNumber = 1; // goes into the bar block for diagnostics

		String boardNo = "";
		String linVul = "";
		String linDealerDigit = "";

		String sk_pbn_value = "";

		char linDealerChar = 'N';

		int linDealer = 3;

		bb = new BarBlock("qx", 0);
		bb.add(" ");
		bbAy.add(bb);

		int kib_offset_from_south = 0;
		int kib_offset_from_dealer = 2;

		String zd_name = "";

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

			String line = s.substring(firstSpace).trim();

			if (line.startsWith("\"") == false || line.endsWith("\"") == false)
				continue;

			line = line.substring(1, line.length() - 1).trim();

			String fill_in = "";

			if (lineType.contentEquals("Event")) {
				// Event

				kib_offset_from_south = 0;
				kib_offset_from_dealer = 2;

				firstIfOne++;

				// @formatter:off
				
				String marker_orig_dealt = "dealer with file";
				String marker_orig_end = ", seed";
				
				zd_name = line;
				int end = zd_name.lastIndexOf(marker_orig_end);
				
				int start = zd_name.indexOf(marker_orig_dealt);
				if (start > 0 && end > 4 && start < end) {
					start += marker_orig_dealt.length();
					zd_name = zd_name.substring(start, end).trim();					
				}
				else {
					zd_name = "";
				}
				
				if (zd_name == "") {
					zd_name = extract_marked_field(line, Aaa.zd_dealer_script_marker);
				}
						
				if (firstIfOne == 1) 
				{					
					lin__zd_dealer_script_name = zd_name;
				}
				
				{
					String low_data = line.toLowerCase();
					kib_offset_from_south = 0;
					     if (low_data.contains("_w.txt,"))  kib_offset_from_south = 1;
					else if (low_data.contains("_n.txt,"))  kib_offset_from_south = 2;
					else if (low_data.contains("_e.txt,"))  kib_offset_from_south = 3;	
				}
			}


			else if (lineType.contentEquals("Site")) {
				// Site 
				
				sk_pbn_value = Aaa.extract_pbn_field(line, Aaa.sk_seat_kib_marker);
				
				if (App.pbnInfoFirst && firstIfOne == 1) {
				
					lin__zg_merge_list_name = Aaa.extract_pbn_field(line, Aaa.zg_merge_list_marker);
				
					if (lin__zg_merge_list_name.length() > 0) {
						fill_in = ""
							+ "       This  .pbn  file was ^*b MERGED ^*n using an  ^*b aadm ^*n  script file, named:^^^^"
							+ "^c ^*b" + lin__zg_merge_list_name + "^*n"
							+ "  ^r" /* + rt*/;
					} 
					else if (lin__zd_dealer_script_name.length() > 0) {
						fill_in = ""
							+ "       This  .pbn  file was ^*b DEALT ^*n using a  DEALER  script file, named:^^^^"
							+ "^c ^*b" + lin__zd_dealer_script_name + "^*n"
							+ "  ^r" /* + rt*/;
					}
					else {
						fill_in = ""
							+ "       ^^^^"
							+ "^c ^*n No vaild dealer script name found in the first line of the pbn file^*n"
							+ "  ^r" /* + rt*/;
					}		

// @formatter:off

				String main_text =
"This 'pbn' file feature of aaBridge is designed to work only with very simple .pbn files such as those made by Hans van Staveren's "
+ "dealer app and ACBL issued hands.   Only the   ^*bHands^*n,  ^*bDeclarer^*n  and  ^*bVulnerability^*n  are read. "
+ "^^^*bALL^*n  bidding and play  are ignored. "

+ fill_in

+ "^^^^" 
+ "When moving from each deal to the next (within these .pbn files) you can use "
+ "the  { ^*b Auto Enter ^*n }  button ^^ which will automatically click  { ^*bEnter the Deal^*n }  for you as you move through the generated deals.    "
+ "^^"
+ "^^Doing this gives you easy access to the  { ^*bAnalyse^*n }  and  { ^*bShuf Op^*n }  buttons.^^"
+ "In addition you, can then click on each seats  'name area'  to easily view only that hand."	
+ "^^^^See the documents   ";
				
	            bb = new BarBlock("cr", 0); bb.add("1200"); bbAy.add(bb);
				bb = new BarBlock("cg", 0); bb.add("1255"); bbAy.add(bb);
				bb = new BarBlock("cb", 0); bb.add("1255"); bbAy.add(bb);

				bb = new BarBlock("mn", 0); bb.add("aaBridge   SIMPLE   .pbn  File Reader"); bbAy.add(bb);
				bb = new BarBlock("bt", 0); bb.add(""); bbAy.add(bb);
				bb = new BarBlock("ht", 0); bb.add("e"); bbAy.add(bb);
				bb = new BarBlock("lg", 0); bb.add("m"); bbAy.add(bb);
				bb = new BarBlock("at", 0); bb.add("^b@2^z@3"); bbAy.add(bb);
				bb = new BarBlock("at", 0); bb.add(main_text); bbAy.add(bb);
				
				bb = new BarBlock("cp", 0); bb.add("red"); bbAy.add(bb);
				bb = new BarBlock("at", 0); bb.add("^f^*b     Practice with aaBridge or on BBO  -  with the 1827 hands.doc^*n"); bbAy.add(bb);
				bb = new BarBlock("cp", 0); bb.add(""); bbAy.add(bb);
				
				bb = new BarBlock("at", 0); bb.add("^^^eand"); bbAy.add(bb);
				
				bb = new BarBlock("cp", 0); bb.add("red"); bbAy.add(bb);
				bb = new BarBlock("at", 0); bb.add("^f^*b     Deal and Merge  -  Why it is useful to You.doc ^*n"); bbAy.add(bb);
				bb = new BarBlock("cp", 0); bb.add(""); bbAy.add(bb);

				bb = new BarBlock("at", 0); bb.add("^^^c     both in the"); bbAy.add(bb);
				bb = new BarBlock("cs", 0); bb.add("1"); bbAy.add(bb);
				bb = new BarBlock("n#", 0); bb.add("3"); bbAy.add(bb);
				bb = new BarBlock("at", 0); bb.add("^^^f { "); bbAy.add(bb);
				bb = new BarBlock("n#", 0); bb.add("4"); bbAy.add(bb);
				bb = new BarBlock("at", 0); bb.add("^*f openPage_DocCollection,  aaBridge Document Collection^*n"); bbAy.add(bb);
				bb = new BarBlock("n#", 0); bb.add("2"); bbAy.add(bb);
				bb = new BarBlock("at", 0); bb.add("  }"); bbAy.add(bb);
				bb = new BarBlock("cp", 0); bb.add("blue"); bbAy.add(bb);
				bb = new BarBlock("n^", 0); bb.add("2"); bbAy.add(bb);
				bb = new BarBlock("at", 0); bb.add("    ^*b    < < < < < < <     Click 'btn' to Read More ^*n"); bbAy.add(bb);
				bb = new BarBlock("cp", 0); bb.add(""); bbAy.add(bb);
				bb = new BarBlock("n#", 0); bb.add("4"); bbAy.add(bb);

				bb = new BarBlock("pg", 0); bb.add(""); bbAy.add(bb);
				
				bb = new BarBlock("st", 0); bb.add(""); bbAy.add(bb);

				// @formatter:on

				}
			}

			else if (lineType.contentEquals("Board")) {

				boardNo = line;
			}

			else if (lineType.contentEquals("Dealer")) {

//				rotate = App.fixedQuarterTurns + count % 4;
//				count += App.rotateWhenSaving; // for next time

				linDealerChar = line.charAt(0);
				switch (linDealerChar) {
				// @formatter:off
					case 'S':  linDealer = 1;  break;
					case 'W':  linDealer = 2;  break;
					case 'N':  linDealer = 3;  break;
					case 'E':  linDealer = 4;  break;
				    // @formatter:on		
				}

				// if (firstIfOne == 1) 
				{
					kib_offset_from_dealer = ((linDealer - 1) + kib_offset_from_south) % 4;
				}

//				linDealer = (((linDealer - 1) + rotate) % 4) + 1;
				linDealerDigit = String.valueOf(linDealer);

			}

			else if (lineType.contentEquals("Vulnerable")) {
				String dataUp = line.toUpperCase();
				linVul = "O";
				if (dataUp.contentEquals("NS"))
					linVul = "N";
				else if (dataUp.contentEquals("EW"))
					linVul = "E";
				else if (dataUp.contentEquals("BOTH") || dataUp.contentEquals("ALL")) {
					linVul = "B";
				}
			}

			else if (lineType.contentEquals("Deal")) {

				// @formatter:off
				bb = new BarBlock("qx", lineNumber); bb.add(boardNo); bbAy.add(bb);
				bb = new BarBlock("rh", lineNumber); bb.add(""); bbAy.add(bb);
				// bb = new BarBlock("ah", lineNumber); bb.add("Board " + boardNo); bbAy.add(bb);
							
				ArrayList<String> theFourHands = pbnDealToLinBBDeal(line, linDealerDigit, 0 /* rotate */);
				
				bb = new BarBlock("md", lineNumber); bb.addAll(theFourHands); bbAy.add(bb);
				
				String zd_out = zd_name;
				
				if (zd_out.isEmpty()) {
					zd_out = lin__zd_dealer_script_name;
 				}

				if (!zd_out.isEmpty()) {
 					bb = new BarBlock("zd", lineNumber); bb.add(zd_out); bbAy.add(bb);
 					App.deal.zd_dealer_script = zd_out;
 				}

				if (!lin__zg_merge_list_name.isEmpty()) {
					bb = new BarBlock("zg", lineNumber); bb.add(lin__zg_merge_list_name); bbAy.add(bb);
					App.deal.zg_merge_list = lin__zg_merge_list_name;
				}

				bb = new BarBlock("ah", lineNumber); bb.add("Board " + boardNo); bbAy.add(bb);
				
				bb = new BarBlock("sv", lineNumber); bb.add(linVul); bbAy.add(bb);
				
				Dir sk_pbn = Dir.directionFromString(sk_pbn_value);				
				if (sk_pbn == Dir.Invalid) {
					sk_pbn = Dir.South;
				}
							
				int offset = ((linDealer + 3) + kib_offset_from_dealer +  sk_pbn.v + 4 /* lin dealer has the rotate already */) % 4;
				
				String kib_seat = Dir.directionFromInt(offset).toString();
				     
				bb = new BarBlock("sk", lineNumber); bb.add(kib_seat); bbAy.add(bb);
				bb = new BarBlock("sk", lineNumber); bb.add(""); bbAy.add(bb);	
				
				String ss;
				if (zd_name.length() > 0) 
					ss = zd_name;
				else
					ss = "???";
				
				bb = new BarBlock("nt", 0); bb.add("^^^bDeal from   " + ss); bbAy.add(bb);
				
				bb = new BarBlock("pg", lineNumber); bb.add(""); bbAy.add(bb);
				// @formatter:on

				// for next cycle ?
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

	public void setFileAndDispName(String filename_in) {
		filename = filename_in;
		if (filename.isEmpty())
			return;
		if (filename.length() <= 23) {
			filenaDisp = filename + "   ";
		}
		else {
			filenaDisp = filename.substring(0, 20) + "...   ";
		}

	}

}
