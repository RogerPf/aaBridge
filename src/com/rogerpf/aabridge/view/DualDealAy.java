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
package com.rogerpf.aabridge.view;

import java.util.ArrayList;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.q_;
import com.rogerpf.aabridge.igf.MassGi;
import com.rogerpf.aabridge.igf.MassGi.GraInfo;
import com.rogerpf.aabridge.model.Deal;
import com.rogerpf.aabridge.model.Lin;
import com.rogerpf.aabridge.model.Lin.BarBlock;
import com.rogerpf.aabridge.view.DualDeal.EachDeal;

/**
 */
public class DualDealAy extends ArrayList<DualDeal> {
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	int qx_count = 0;
	int pg_count = 0;
	int nt_count = 0;
	int at_count = 0;

	BarBlock vg = null;
	BarBlock rs = null;
	BarBlock pw = null;
	BarBlock pn = null;
	BarBlock mp = null;
	BarBlock bn = null;

	public String competitionTitle = "";
	public String blueTeam = "";
	public String purpleTeam = "";

	public boolean twoColumn = false; // until we find otherwise

	int initialScore[] = { 0, 0 };
	String startingScore[] = { "", "" };
	String finalScore[] = { "", "" };

	boolean result;

	MassGi mg;
	Lin lin;

	/**     DualDealAy()  ---------- Constructor  ----------
	 * 
	 *  Non Vugraph format lin files are unchanged by this call.
	 *  
	 *  There are two part to the construction of the DualDealAy
	 *  
	 *  (1) - below  We parse the original bbAy extracting all the fields needed
	 *  AND as a side event we change the  bbAy  into an  'eaiser to read'
	 *  format.  e.g. changing some 'nt's  into 'at's.
	 *  
	 *  (2) - see fillFromGiAy();
	 *  In which we find and insert the correct deals (embedded into the newly
	 *  created mg.giAy)
	 *   
	 *  Note - while it is called  - DualDeal -  there can be a from where the
	 *  input file only supplies one of the two 'rooms' (normally the open room). 
	 */
	public DualDealAy(MassGi mg, Lin lin) {
		// =============================================================================

		this.mg = mg;
		this.lin = lin;

		if (lin.linType != Lin.VuGraph)
			return; // So creating the DualDealAy but doing nothing

		result = scanBbAy_constructor_part1();

		/** Improve the VuGraph display ?
		 */
//		if (at_count < 2 && nt_count > 0) {
		ImproveVugraphDisplay();
//		}

		if (result == false) {
			/** something was not right with the format of this vuGraph so we turn off this 'extra feature' */
			lin.linType = Lin.FullMovie;
			return;
		}
	}

	public boolean scanBbAy_constructor_part1() {
		// =============================================================================

		int bbAySize = lin.bbAy.size();

		DualDeal dd = null;

		int index = 0;
//		int closed_count = 0;
//		int open_count = 0;

		for (int i = 0; i < bbAySize; i++) {
			BarBlock bb = lin.bbAy.get(i);
			int t = bb.qt;

			if (t == q_.qx) {
				qx_count++;

				String text = bb.get(0).toLowerCase();

				char qx_room = (text + '-').charAt(0);
				int qx_number = Aaa.extractPositiveInt(text);

				if (qx_room == 'o') {
//					open_count++;
					add(dd = new DualDeal(index++, qx_number, bb /* the bb for this 'open' qx */));
				}
				else if (qx_room == 'c') {
//					closed_count++;
					if (dd == null || dd.qx_number != qx_number)
						return false;
					dd.eachDeal[DualDeal.Closed].qx_bb = bb;
					twoColumn = true;
				}
				else { // eeek
					return false;
				}
			}

			// @formatter:off
			if (t == q_.pg) { pg_count++; continue; }
			if (t == q_.nt) { nt_count++; continue; }
			if (t == q_.at) { at_count++; continue; }
			// @formatter:on

			/** 
			 * Note - the following are  *only*  called BEFORE we see the first qx or pg
			 */
			// @formatter:off
			if (qx_count > 0 || pg_count > 0) continue;
			if (t == q_.vg) { vg = bb; continue; } // view graph heading info etc
			if (t == q_.rs) { rs = bb; continue; } // Results => contract - direction - plus minus or equal
			if (t == q_.pn) { pn = bb; continue; } // players names ?
			if (t == q_.pw) { pw = bb; continue; } // players names ? web version ?
			if (t == q_.mp) { mp = bb; continue; } // imp scores (match point?)
			if (t == q_.bn) { bn = bb; continue; } // Board Numbers
			// @formatter:on
		}

		if (vg != null && vg.size() >= 9) {

			competitionTitle = vg.get(0) + "  -  " + vg.get(1);
			blueTeam = vg.get(5);
			purpleTeam = vg.get(7);
			if (vg.get(2).contentEquals("I")) {
				initialScore[0] = Aaa.extractPositiveInt(vg.get(6));
				initialScore[1] = Aaa.extractPositiveInt(vg.get(8));
			}

		}

		/** add in the results - there may be more results than played deals
		 *  as sometimes there is a blank result at the end.
		 */
		if (rs != null && rs.size() >= size() * 2) {
			for (int i = 0; i < size(); i++) {
				dd = get(i);
				dd.eachDeal[DualDeal.Open].setResult(rs.get(i * 2));
				dd.eachDeal[DualDeal.Closed].setResult(rs.get(i * 2 + 1));
			}
		}

		if ((pn != null) && (pn.size() >= 4)) {
			for (int i = 0; i < size(); i++) {
				dd = get(i);
				dd.eachDeal[DualDeal.Open].setPlayerNames(pn, 0);
				if ((pn.size() >= 8))
					dd.eachDeal[DualDeal.Closed].setPlayerNames(pn, 4);
			}
		}

		return true;
	}

	/**
	 * 
	 */
	public void ImproveVugraphDisplay() {
		// =============================================================================

		int maxlines = 6;
		int linesShowing = 0;

		int pn_count = 0;
		boolean prev_was_pn = false;
		int qx_count = 0;

		BarBlock prev_mbPass = null;
		BarBlock prevPrev_mbPass = null;
		int mbPassLevel = 0;

		boolean prev_was_nt = false;

		int bbAySize = lin.bbAy.size();

		boolean first_pg_seen = false;

		for (int i = 0; i < bbAySize; i++) {
			BarBlock bb = lin.bbAy.get(i);

			if ((mbPassLevel == 3)) {
				if ((bb.qt == q_.pc) && (bb.get(0).length() == 2)) {
					prevPrev_mbPass.set(0, "pp"); // put two passes where there was only one
					prev_mbPass.type = "pc";
					prev_mbPass.qt = q_.pc;
					prev_mbPass.set(0, bb.get(0)); // convert the now free mb into the pc played card
					bb.type = "pg";
					bb.qt = q_.pg;
					bb.set(0, ""); // and set the now free spot to be a pg - all without messing with the sequence Id's
					mbPassLevel = 0;
				}
			}

			if ((bb.qt == q_.mb) && (bb.get(0).length() == 1) && (bb.get(0).toLowerCase().charAt(0) == 'p')) {
				prevPrev_mbPass = prev_mbPass;
				prev_mbPass = bb;
				mbPassLevel++;
			}
			else {
				if (mbPassLevel != 3)
					mbPassLevel = 0;
			}

			if (prev_was_pn && qx_count == 0 && pn_count == 1 && bb.qt == q_.pg) {
				// we can kill off this 'pg'
				bb.type = "xx";
				bb.qt = q_.xx;
				qx_count++; // Take care later
			}

			if (bb.qt == q_.pn) {
				pn_count++;
				linesShowing = 0; // to force the next nt NOT be to converted to an at
			}

			if (prev_was_nt && (bb.qt == q_.pg)) {
				/* Is there another pg before the next qx
				 */
				boolean pg_found = false;
				for (int j = i + 1; j < bbAySize; j++) {
					BarBlock bbj = lin.bbAy.get(j);
					if (bbj.qt == q_.qx)
						break;
					if (bbj.qt == q_.pg) {
						pg_found = true;
						break;
					}
				}

				if (pg_found) {
					// we can kill of this one
					bb.type = "xx";
					bb.qt = q_.xx;
				}
			}

			if (bb.qt == q_.nt) {
				bb.add("");
				int lCount = (1 + bb.get(0).length() / 120);
				if (linesShowing >= 1) {
					bb.type = "at";
					bb.qt = q_.at;
				}
				else if (linesShowing == 0) {
					bb.set(0, "^^ ^b@2" + bb.get(0));
				}

				linesShowing += lCount;

				if (linesShowing >= maxlines) {
					linesShowing = 0;
				}
				prev_was_nt = true;
			}
			else {
				prev_was_nt = false;
			}

			if (bb.qt == q_.pg) {
				if ((first_pg_seen == false) && at_count == 0 && nt_count == 0) {
					bb.qt = q_.at;
					bb.set(0, "^^ ^^ ^cThis  .lin  file contains no commentary or other display text.");
				}
				first_pg_seen = true;
			}

			if (bb.qt == q_.pn) {
				prev_was_pn = true;
			}
			else
				prev_was_pn = false;
		}

	}

	/**
	 */
	public void afterGiAyCreated_constructor_part2(MassGi mg) {
		// =============================================================================

		if (lin.linType != Lin.VuGraph)
			return;

		startingScore[0] = (initialScore[0] == 0) ? "--" : initialScore[0] + "";
		startingScore[1] = (initialScore[1] == 0) ? "--" : initialScore[1] + "";

		int tot[] = { initialScore[0], initialScore[1] };
		boolean totValid = true;

		for (DualDeal dd : this) {

			DualDeal.EachDeal openDeal = dd.eachDeal[DualDeal.Open];

			dd.scoreAnchor_gi_index = mg.findPgIdBeforeThisBarBlock(openDeal.qx_bb);

			int pg_index_o = mg.findPgIdBeforeNextQx(openDeal.qx_bb);
			openDeal.deal = mg.giAy.get(pg_index_o).deal;
			if (openDeal.deal == null)
				openDeal.deal = new Deal();
			openDeal.vulnerability = openDeal.deal.vulnerability;
			openDeal.calculateScore();

			DualDeal.EachDeal closedDeal = dd.eachDeal[DualDeal.Closed];

			int pg_index_c = mg.findPgIdBeforeNextQx(closedDeal.qx_bb);
			closedDeal.deal = mg.giAy.get(pg_index_c).deal; // can be null !
			closedDeal.vulnerability = openDeal.deal.vulnerability; // use the open one as ours could be missing
			closedDeal.calculateScore();

			if (openDeal.scoreValid && closedDeal.scoreValid) {
				dd.impsValid = true;
				dd.scoreDiff = openDeal.score - closedDeal.score;
				if (-10 <= dd.scoreDiff && dd.scoreDiff <= 10) {
					dd.scoreDiff = 0;
					dd.sImps = "--";
					dd.imps = 0;
				}
				else {
					dd.imps = Deal.convertToImps(Math.abs(dd.scoreDiff));
					dd.sImps = dd.imps + "";
				}

				if (totValid) {
					dd.sTot[0] = (tot[0] == 0) ? "--" : tot[0] + ""; // save the previous total as a string
					dd.sTot[1] = (tot[1] == 0) ? "--" : tot[1] + ""; // save the previous total as a string

					tot[0] += (dd.scoreDiff) > 0 ? dd.imps : 0;
					tot[1] += (dd.scoreDiff) < 0 ? dd.imps : 0;

					finalScore[0] = (tot[0] == 0) ? "--" : tot[0] + "";
					finalScore[1] = (tot[1] == 0) ? "--" : tot[1] + "";
				}
				else {
					finalScore[0] = " ";
					finalScore[1] = " ";
				}
			}
			else {
				if (totValid) {
					dd.sTot[0] = (tot[0] == 0) ? "--" : tot[0] + ""; // save the previous total as a string
					dd.sTot[1] = (tot[1] == 0) ? "--" : tot[1] + ""; // save the previous total as a string
				}
				else {
					finalScore[0] = " ";
					finalScore[1] = " ";
				}
				totValid = false; // so first seen next time arround
				dd.impsValid = false;
				dd.sImps = "-";
			}
		}

		/** retrofit the scores back into all the deals
		 * Starting from the last and going back we 
		 *  match up the scores
		 */

		int ddIndex = size() - 1;
		DualDeal dd = get(ddIndex);

		for (int i = mg.giAy.size() - 1; i > 0; i--) {
			GraInfo gi = mg.giAy.get(i);
			if (gi.deal != null) {
				if (i < dd.scoreAnchor_gi_index) {
					if (ddIndex > 0)
						ddIndex--;
					dd = get(ddIndex);
				}
				gi.deal.blueScore = dd.sTot[0];
				gi.deal.purpleScore = dd.sTot[1];
			}
		}

		@SuppressWarnings("unused")
		int z = 0; // put your breakpoint here
	}

	/**
	 */
	public DualDeal getLast() {
		// ============================================================================
		return get(size() - 1);
	}

	public EachDeal getEachDealMatchingQx(BarBlock bb) {
		// ============================================================================
		for (DualDeal dd : this) {
			for (int k = 0; k < 2; k++) {
				DualDeal.EachDeal ed = dd.eachDeal[k];
				if (ed == null)
					continue;

				if (ed.qx_bb == bb) {
					return ed;
				}
			}
		}
		return null;
	}

}
