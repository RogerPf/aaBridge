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
package com.rogerpf.aabridge.igf;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.Timer;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.controller.Controller;
import com.rogerpf.aabridge.controller.q_;
import com.rogerpf.aabridge.model.Bid;
import com.rogerpf.aabridge.model.Call;
import com.rogerpf.aabridge.model.Card;
import com.rogerpf.aabridge.model.Deal;
import com.rogerpf.aabridge.model.Dir;
import com.rogerpf.aabridge.model.Level;
import com.rogerpf.aabridge.model.Lin;
import com.rogerpf.aabridge.model.Lin.BarBlock;
import com.rogerpf.aabridge.model.Rank;
import com.rogerpf.aabridge.model.Suit;
import com.rogerpf.aabridge.view.BidTablePanel;
import com.rogerpf.aabridge.view.DualDeal.EachDeal;
import com.rogerpf.aabridge.view.DualDealAy;
import com.rogerpf.aabridge.view.HandDisplayGrid;
import com.rpsd.bridgefonts.BridgeFonts;

/** 
 * Graphical Information (block)  gi Maker
 * 
 * The Model reads lin files and the View displays the information to the user.
 * Igf is a format that is distilled from the lin file but which is easy for 
 * the View package to display
 * 
 * @author roger
 *
 */
public class MassGi {
	// ---------------------------------- CLASS -------------------------------------

	public ArrayList<GraInfo> giAy = new ArrayList<GraInfo>();

	BarBlock activeBb = null;
	Deal deal = null;

	public Lin lin = null;
	public JpPointAy jpPointAy = null;
	public DualDealAy ddAy = null;

	StringBuilder outBuf = new StringBuilder();

	int graInfo_nextIndex = 0;

	int source_mn = 0;
	int start_nt = 0;
	int middle_pg = 0;
	public int end_pg = 0;
	int stop_gi = 0;

	Capture_gi_env capEnv = new Capture_gi_env();

	ArrayList<Hyperlink> hyperlinkAy = new ArrayList<Hyperlink>();

	/**
	 */
	public MassGi(Deal deal) { // constructor
		// =============================================================================
		assert (deal != null);
		this.lin = new Lin(deal);
	}

	/**
	 */
	public MassGi(Lin lin) { // constructor
		// =============================================================================
		assert (lin != null);
		this.lin = lin;

		App.tup.cleanUp();

		if (lin.linType == Lin.VuGraph) {
			ddAy = new DualDealAy(this, lin);
		}

		try {

			oneTimeParse_lin_create_giAy();
			jpPointAy = new JpPointAy(this); // the data for the Tutoral (and multi deal) Navigation Control

		} catch (IOException e) {
			e.printStackTrace();
		}

		if (lin.linType == Lin.VuGraph) {
			ddAy.afterGiAyCreated_constructor_part2(this);
		}
	}

	/**
	 */
	public Deal getBestSingleSimpleDeal() {
		// =============================================================================
		assert (lin.linType == Lin.SimpleDealSingle);

		for (int i = giAy.size() - 1; i > 0; i--) {
			GraInfo gi = giAy.get(i);
			if (gi.qt == q_.pg && gi.deal != null) {
				return gi.deal.deepClone();
			}
		}
		return new Deal(0); // eeek
	}

	// @formatter:off
	LinColor linColorAy[] = { 
			new LinColor(), new LinColor(), 
			new LinColor(), new LinColor(), 
			new LinColor(), new LinColor(), 
			new LinColor(), new LinColor(),
			new LinColor(), new LinColor() 
			};

	Color cAy_std[] = { 
			Color.black, Color.black, 
			Color.black, Color.black, 
			Color.black, Color.black, 
			Color.black, Color.black, 
			Color.black, Color.black,
			Color.WHITE };

	Color cAy_fill[] = { 
			Color.black, Color.black, 
			Color.black, Color.black, 
			Color.black, Color.black, 
			Color.black, Color.black, 
			Color.black, Color.black,
			Color.WHITE };
	// @formatter:on

	public class GraInfo {
		// ---------------------------------- CLASS -------------------------------------
		public final BarBlock bb;
		String type;
		public int qt;
		int index;
		public int numb = -1;
		String text = "";
		String userAns = "";
		public Deal deal = null;
		Deal deal_ih_ia = null;
		HandDisplayGrid hdg = null;
		BidTablePanel btp = null;

		Capture_gi_env capEnv;

		public String toString() {
			// ==========================
			String s = type + " " + numb + " - " + text + " - ";
			for (String bb_s : bb) {
				s += bb_s + " -- ";
			}
			return s;
		}

		public GraInfo(GraInfo o) { // 'Copy' constructor used for questions writing pass
			// ==============================================================================================
			bb = (BarBlock) o.bb.clone();
			capEnv = new Capture_gi_env(o.capEnv);
			deal = null;
			deal_ih_ia = o.deal_ih_ia; // no clone as we do not expect it to be changed
			qt = o.qt;
			type = o.type;
			text = o.text;
			numb = o.numb;
			userAns = o.userAns;
			index = o.index;
			// hdg and btp are NOT coppied
		}

		public void kill() {
			// ==============================================================================================
			type = "xx";
			qt = q_.q(type);
		}

		void commonBit() {
			// ==============================================================================================
			capEnv = new Capture_gi_env(MassGi.this.capEnv);
			index = graInfo_nextIndex++;
			giAy.add(this);
		}

		GraInfo(String typeV) {
			// ==============================================================================================
			bb = activeBb;
			type = typeV;
			qt = q_.q(type);

			if (qt == q_.at) {
				if (outBuf.length() >= 0) {
					text = outBuf.toString();
					outBuf = new StringBuilder();
				}
			}

			commonBit();
		}

		GraInfo(String typeV, String s) {
			// ==============================================================================================
			bb = activeBb;
			type = typeV;
			qt = q_.q(type);

			text = s;

			commonBit();
		}

		GraInfo(String typeV, int n) { //
			// ==============================================================================================
			bb = activeBb;
			type = typeV;
			qt = q_.q(type);

			numb = n;

			commonBit();
		}

		GraInfo(BarBlock bbV) {
			// ==============================================================================================
			assert (bbV == activeBb);
			bb = activeBb;
			if (bb.size() == 0)
				bb.add("");

			type = bb.type;
			qt = q_.q(type);

			commonBit();
		}

	}

	/**
	 */
	void insertColorPart(String type, String ns) {
		// =============================================================================
		int len = ns.length();
		if (len < 2)
			return;

		int slot = (int) ns.charAt(0) - '0';
		if (slot < 1 || slot > 9)
			return;

		int val = Aaa.extractPositiveInt(ns.substring(1));
		if (val < 0 || val > 255)
			return;

		if (type.length() != 2) // note by now - they are always lower case
			return;

		// @formatter:off
		switch (type.charAt(1)) { 
			case 'r': linColorAy[slot].setRGB(0, val, slot, cAy_std, cAy_fill); break; 
			case 'g': linColorAy[slot].setRGB(1, val, slot, cAy_std, cAy_fill); break;
			case 'b': linColorAy[slot].setRGB(2, val, slot, cAy_std, cAy_fill); break;
		// @formatter:on
		}
	}

	int LIN_MIN_AND_STD_FONT_SIZE = 30;

	int fontBlockIndex = 0;

	/**
	 */
	class FontBlock {
		// ---------------------------------- CLASS -------------------------------------
		int index = 0;
		boolean active = false;
		float linFontSize = LIN_MIN_AND_STD_FONT_SIZE;
		String family = App.fontfamilyStandard;
		int bold = 0;
		boolean italic = false;
		boolean underline = false;
		Font font;

		FontBlock() { // Constructor
			// =============================================================================
			index = fontBlockIndex++;
			if (index == 10) { // 0 is built in default, 10 is the white font for 'mn' headers, 11 is for lb z
				linFontSize = 56;
				bold = 5;
			}

			if (index == 11) { // 0 is built in default, 10 is the white font for 'mn' headers, 11 is for lb z
				linFontSize = 78;
			}

			fontMake(); // make it first just to help the poor punters
			active = false;
		}

		public void fontMake() {
			// =============================================================================
			// System.out.println( " fontMake " + index);
			if (active)
				return;
			active = true; // you only get one official make font chance

			String fontFamily = family;

			if (index <= 10) {
				if (App.useFamilyOverride && App.fontfamilyOverride.length() > 0) {
					fontFamily = App.fontfamilyOverride;
				}
				font = new Font(fontFamily, 0, 0); // pre make them anyway
			}
			else { // index = 11
				font = BridgeFonts.faceAndSymbFont;
			}

		}
	}

	// @formatter:off
	FontBlock fbAy[] = { 
			 new FontBlock(), new FontBlock(), new FontBlock(),
			 new FontBlock(), new FontBlock(), new FontBlock(),
			 new FontBlock(), new FontBlock(), new FontBlock(),
			 new FontBlock(), //  0 - 9 (10 of them) are the aaBridge standard fonts
			 new FontBlock(), // 10 is for mn headers
			 new FontBlock(), // 11 is for lb z  question answers
	};
	// @formatter:on

	/**
	 */
	void insertFontHeight(String ns) {
		// =============================================================================
		if (ns.length() < 2)
			return;

		int slot = (int) ns.charAt(0) - '0';
		if (slot < 1 || slot > 9)
			return;

		if (fbAy[slot].active)
			return; // Thou shalt not mess with a 'made' font

		int val = Aaa.extractPositiveInt(ns.substring(1));
		int minSize = LIN_MIN_AND_STD_FONT_SIZE / (slot > 4 ? 2 : 1);
		if (val < minSize || val > 999)
			val = minSize;

		fbAy[slot].linFontSize = val;
	}

	/**
	 */
	void insertFontFamily(String ns) {
		// =============================================================================
		if (ns.length() < 2)
			return;

		int slot = (int) ns.charAt(0) - '0';
		if (slot < 1 || slot > 9)
			return;

		if (fbAy[slot].active)
			return; // Thou shalt not mess with a 'made' font

		fbAy[slot].family = ns.substring(1);
	}

	/**
	 */
	void insertFontBold(String ns) {
		// =============================================================================
		if (ns.length() < 2)
			return;

		int slot = (int) ns.charAt(0) - '0';
		if (slot < 1 || slot > 9)
			return;

		int val = ns.charAt(1) - '0'; // ignore the third char (if any)
		if (val < 0 || val > 9) // we have a boolean cutoff <= 4 and 5 >=
			return;

		if (fbAy[slot].active)
			return; // Thou shalt not mess with a 'made' font

		fbAy[slot].bold = val;
	}

	/**
	 */
	void insertFontItalic(String ns) {
		// =============================================================================
		if (ns.length() < 1)
			return;

		int slot = (int) ns.charAt(0) - '0';
		if (slot < 1 || slot > 9)
			return;

		if (fbAy[slot].active)
			return; // Thou shalt not mess with a 'made' font

		fbAy[slot].italic = true;
	}

	/**
	 */
	void insertFontUnderline(String ns) {
		// =============================================================================
		if (ns.length() < 1)
			return;

		int slot = (int) ns.charAt(0) - '0';
		if (slot < 1 || slot > 9)
			return;

		if (fbAy[slot].active)
			return; // Thou shalt not mess with a 'made' font

		fbAy[slot].underline = true;
	}

	/**
	 */
	void insertFontMake(String ns) {
		// =============================================================================
		if (ns.length() != 1)
			return;

		int slot = (int) ns.charAt(0) - '0';
		if (slot < 1 || slot > 9)
			return;

		if (fbAy[slot].active)
			return; // Thou shalt not mess with a 'made' font

		fbAy[slot].fontMake();
	}

	/**
	 */
	void setFont_fp(String ns) {
		// =============================================================================
		if (ns.length() == 0)
			ns = "0";

		int slot = (int) ns.charAt(0) - '0';
		if (slot < 1 || slot > 9)
			slot = 0;

		capEnv.font_slot_fp = slot;
	}

	/**
	 */
	void setColor_bg(String ns) {
		// =============================================================================
		if (ns.length() == 0) {
			capEnv.color_bg = Color.WHITE;
			return;
		}

		int slot = (int) ns.charAt(0) - '0';
		if (slot < 1 || slot > 9)
			slot = 0;

		capEnv.color_bg = cAy_fill[slot];
	}

	/**
	 */
	void setColor_cp(String ns) {
		// =============================================================================
		if (ns.length() == 0) {
			capEnv.color_cp = Color.BLACK;
			return;
		}

		int slot = (int) ns.charAt(0) - '0';
		if (slot < 1 || slot > 9)
			slot = 0;

		capEnv.color_cp = cAy_std[slot];
	}

	/**
	 */
	void setColor_cq(String ns) {
		// =============================================================================
		if (ns.length() != 1) {
			capEnv.color_cq = Color.WHITE;
			return;
		}

		int slot = (int) ns.charAt(0) - '0';
		if (slot < 1 || slot > 9)
			slot = 0;

		capEnv.color_cq = cAy_fill[slot];
	}

	/**
	 */
	void setColor_cs(String ns) {
		// =============================================================================
		if (ns.length() != 1) {
			capEnv.color_cs = Color.WHITE;
			return;
		}

		int slot = (int) ns.charAt(0) - '0';
		if (slot < 1 || slot > 9)
			slot = 0;

		capEnv.color_cs = cAy_fill[slot];
	}

	public boolean isEndAQuestion() {
		// =============================================================================
		return (giAy.get(stop_gi).qt == q_.lb);
	}

	/**
	 */
	void setQuestPosition_hf(String letter) {
		// =============================================================================
		letter = letter.trim();
		if (letter.length() == 0) {
			letter = "z";
		}
		capEnv.lb_position = alphaToRowColNumb(letter.charAt(0), 20 /* 'u' 20th letter counting 'a' = 0 */);
	}

	/**
	 */
	int alphaToRowColNumb(char c, int defaultValue) {
		// =============================================================================
		if ('A' <= c && c <= 'Z') {
			return (int) c - 'A';
		}
		if ('a' <= c && c <= 'z') {
			return (int) c - 'a';
		}
		return defaultValue;
	}

	/**
	 */
	void process_ht(BarBlock bb) {
		// =============================================================================
		String s = bb.get(0);

		if (s.length() == 0) {
			s = "a";
		}
		char c = s.charAt(0);

		int n = alphaToRowColNumb(c, 0);

		new GraInfo("ht", n);
	}

	/**
	 */
	void process_tu(BarBlock bb) {
		// =============================================================================
		String s = bb.get(0);
		capEnv.mn_hideable_by_pg = (s.length() == 0);
		capEnv.mn_showing = (s.length() > 0);
		capEnv.mn_pg_countDown = (s.length() > 0) ? 2 : 0;
		new GraInfo("tu", s);
	}

	/**
	 */
	void process_st(BarBlock bb) {
		// =============================================================================
		String s = bb.get(0);
		capEnv.visualModeRequested = App.Vm_DealAndTutorial;
		new GraInfo("st", s);
	}

	/**
	 */
	void process_bt(BarBlock bb) {
		// =============================================================================
		String s = bb.get(0);
		capEnv.visualModeRequested = App.Vm_TutorialOnly;
		new GraInfo("bt", s);
	}

	public void process_qx(BarBlock bb) {
		// =============================================================================
		GraInfo gi = new GraInfo(bb);
		gi.text = bb.get(0);

		if (lin.linType != Lin.VuGraph)
			return; // only Vugraph has "open" and "closed" rooms

		// Open closed room assistance follows

//		String t = gi.text.trim().toLowerCase() + ' ';
		char r = gi.text.charAt(0);

		char prev_qx_room = App.deal.qx_room;
		// int prev_qx_number = App.deal.qx_number;

		App.deal.qx_room = (r == 'c') ? 'c' : 'o';
		App.deal.qx_number = Aaa.extractPositiveInt(gi.text);

		App.deal.changed = true;

		if (App.deal.qx_room == 'o' && prev_qx_room == 'o') {
			return; // this must a one column Dual Table and needs no player name assistance
		}

		EachDeal ed = ddAy.getEachDealMatchingQx(bb);

		// if (App.deal.qx_room == 'c' & ) {
		App.deal.setPlayerNames(ed.playerNames);
		// }

	}

	/**
	 */
	public void setTheReadPoints_BwdOne() {
		// =============================================================================

		for (int i = stop_gi - 1; i >= 0; i--) {
			GraInfo gi = giAy.get(i);
			if (gi.qt == q_.pg || gi.qt == q_.lb) {
				setTheReadPoints(i, false);
				return;
			}
		}
		setTheReadPoints_FirstTime();
	}

	/**
	 */
	public void setTheReadPoints_FirstTime() {
		// =============================================================================
		int stop = -1;

		for (GraInfo gi : giAy) {
			if (gi.qt == q_.pg || gi.qt == q_.lb) {
				stop = gi.index;
				break;
			}
		}

		assert (stop > -1);

		setTheReadPoints(stop, true);
	}

	/**
	 */
	public void setTheReadPoints_FwdOne() {
		// =============================================================================
		if (stop_gi >= giAy.size() - 1)
			return;

		for (int i = stop_gi + 1; i < giAy.size(); i++) {
			GraInfo gi = giAy.get(i);
			if (gi.qt == q_.pg || gi.qt == q_.lb) {
				setTheReadPoints(i, true);
				return;
			}
		}
		assert (false);
	}

	/**
	 */
	public void setTheReadPoints_ToEnd() {
		// =============================================================================
		if (stop_gi >= giAy.size() - 1)
			return;

		setTheReadPoints(giAy.size() - 1, true);
	}

	/**
	 */
	public void tutorialBackOne() {
		// =============================================================================
		int stop_gi_OLD = stop_gi;
		setTheReadPoints_BwdOne();
		if (stop_gi == stop_gi_OLD) {
			if (App.flowOnlyCommandBar)
				return;
			else
				Controller.loadBookChapter_prev(true /* show_end = true */);
		}
	}

	/**
	 */
	public void tutorialStepFwd() {
		// =============================================================================
		if (stop_gi >= giAy.size() - 1) {
			Controller.loadBookChapter_next();
			return;
		}
		setTheReadPoints_FwdOne();
	}

	/**
	 */
	public void tutNavBarClicked(int gi_index) {
		// =============================================================================
		setTheReadPoints(gi_index, false);
	}

	// @formatter:off
	static final int dispEither[] = { q_.pc, q_.up, q_.mc, q_.mb, q_.ub, q_.an, };
	public boolean isQtEither(int t) {
		for (int qt : dispEither) { if (qt == t) return true; }
		return false;
	}
	
	static final int halter[] = { q_.md, q_.up, q_.mc, q_.ub, q_.an };
	public boolean isHalter(int t) {
		for (int qt : halter) { if (qt == t) return true; }
		return false;
	}
	
	static final int dispCard[] = { q_.pc, q_.up, q_.mc, };
	public boolean isQtCard(int t) {
		for (int qt : dispCard) { if (qt == t) return true; }
		return false;
	}
	
	static final int dispBid[] = { q_.mb, q_.ub, q_.an, };
	public boolean isQtBid(int t) {
		for (int qt : dispBid) { if (qt == t) return true; }
		return false;
	}
	// @formatter:on

	/**
	 * We never set the read points only
	 *   setTheReadPoints    does that
	 */
	public void setTheReadPoints_FwdMini() {
		// =============================================================================
		int lastInd = giAy.size() - 1;
		if (stop_gi >= lastInd)
			return;

		int stop_gi__OLD = stop_gi;

		/**
		 * Scan fwd seraching for the next pg or lb
		 */
		int ahead_pg = -1;
		for (int i = stop_gi__OLD + 1; i <= lastInd; i++) {
			GraInfo gi = giAy.get(i);
			int t = gi.qt;

			if (t == q_.pg || t == q_.lb) { // the only official two stoppers
				ahead_pg = i;
				break;
			}
		}

		assert (ahead_pg > 0);
		int stop_gi_candidate = -1;

		boolean first_back_seen = false;

		for (int i = ahead_pg; i > stop_gi__OLD; i--) {
			GraInfo gi = giAy.get(i);
			int t = gi.qt;

			if (t == q_.pc || t == q_.mb) { // for now just these two
				if (first_back_seen == false) {
					first_back_seen = true;
					continue;
				}
				stop_gi_candidate = i;
			}

			if (isHalter(t))
				break;
		}

		if (stop_gi_candidate == -1) {
			setTheReadPoints_FwdOne();
			return;
		}

		int t = giAy.get(stop_gi_candidate).qt;

		int ms = 0;
		if (t == q_.mb) {
			ms = App.bidPluseTimerMs;
		}
		else if (t == q_.pc) {
			ms = App.playPluseTimerMs;
		}
		else {
			assert (false);
		}

		setTheReadPoints(stop_gi_candidate, true /* fwd => true */); // <==== i ====

		tutorialPlayTimer.setInitialDelay(ms);
		tutorialPlayTimer.start();
	}

	final static int Fh_NONE = 0;
	final static int Fh_ADDED = 1;
	final static int Fh_TIMERRUNNING = 2;

	int fh_state = Fh_NONE;

	/** 
	 * 'FloatingHands' as generated by the .ih command, are not possitioned 
	 * until PAINT time. This can mean that their SIZE is SOMETIMES out of sync.
	 * A workarround for this is to use a timer that forces a re-display
	 * after any 'floating hand' has been added.
	 */
	public void tpPaintCompleteIndication() {
		// =============================================================================
		if (fh_state == Fh_ADDED) {
			fh_state = Fh_TIMERRUNNING;
			floatingHandExtraDisplayTimer.start();
		}
	}

	/**
	*/
	public Timer floatingHandExtraDisplayTimer = new Timer(10 /* millisecs*/, new ActionListener() {
		// ---------------------------------- Timer -------------------------------------
		public void actionPerformed(ActionEvent evt) {
			// =============================================================================
			floatingHandExtraDisplayTimer.stop();
			setTheReadPoints(stop_gi, true); // The forced redisplay
			fh_state = Fh_NONE;
		}
	});

	/**
	 */
	public void setTheReadPoints(int stop, boolean fwd__not_currently_used) {
		// =============================================================================

		assert (stop >= 0 && stop < giAy.size());

		GraInfo giStop = giAy.get(stop);

		source_mn = 0;
		start_nt = 0;
		middle_pg = 0;
		stop_gi = stop;
		end_pg = 0;

		if (giStop.qt == q_.pg || giStop.qt == q_.lb) {
			end_pg = stop_gi;
		}
		else {
			/**
			 *  we need to find the end_pg by walking fwd FWD <=====
			 */
			for (int i = stop_gi + 1; i < giAy.size(); i++) {
				GraInfo gi = giAy.get(i);
				assert (gi.index == i); // simple safety test
				int t = gi.qt;

				if (t == q_.pg || t == q_.lb) {
					end_pg = i;
					break;
				}
			}
		}

		/**
		 * Walk back to find the middle_pg and start_nt
		 */
		for (int i = end_pg - 1; i >= 0; i--) {
			GraInfo gi = giAy.get(i);
			assert (gi.index == i); // simple safety test
			int t = gi.qt;

			if (source_mn == 0 && t == q_.mn) {
				source_mn = i;
			}
			if (start_nt == 0 && t == q_.nt) {
				start_nt = i;

				if (source_mn == 0)
					source_mn = i;

				if (middle_pg == 0)
					middle_pg = i;

				break;
			}

			if (middle_pg == 0 && (gi.qt == q_.pg || gi.qt == q_.lb)) {
				middle_pg = i;
			}
		}

		/**
		 * Set the correct visual mode princpaly this sets tp (Tutorial Panel) size correctly
		 */

		if (giStop.capEnv.visualModeRequested == App.Vm_DealAndTutorial) {
			App.tup.setSize(3000, 100); // We force shink the tp so it will expand if needed
		}

		App.setVisualMode(giStop.capEnv.visualModeRequested);

		/** vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv================
		 * look for any   insert hands    - ih
		 *           or   insert auction  - ia 
		 *    but first we clear out any old ones
		 */
		boolean floaterSeen = false;
		App.tup.clearAllFloatersIncQp();
		for (int i = start_nt; i < end_pg; i++) {
			GraInfo gi = giAy.get(i);
			if (gi.qt == q_.ih) {
				gi.hdg = App.tup.addFloatingHand(gi.hdg, gi.deal_ih_ia);
				floaterSeen = true;
			}
			if (gi.qt == q_.ia) {
				gi.btp = App.tup.addBidTablePanel(gi.btp, gi.deal_ih_ia);
				floaterSeen = true;
			}
		}

		/** vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv===============
		 *  vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv===============
		 * Question Display  - only seen when the final gi to display (giStop) is a question
		 */
		if (giStop.qt == q_.lb) {
			giStop.userAns = ""; // We clear any old user answers when we land on a question.
			BarBlock bb = giStop.bb;
			// @formatter:off
			// System.out.println("Set Read Points lb Question ---- " 
			//				+ bb.getSafe(1) + " - " + bb.getSafe(2) + " - " + bb.getSafe(3)
			//		+ " - " + bb.getSafe(4) + " - " + bb.getSafe(5) + " - " + bb.getSafe(6));
			// @formatter:on

			char c = bb.get(1).charAt(0);
			char t = 0;
			if (c == 'z')
				t = bb.get(2).charAt(0);

			App.tup.addQp(); // qp is a floater
			App.tup.matchToQuestion(c, t);

			fh_state = Fh_ADDED;

			if (c == 'b' || c == 'm' || c == 'y') {
				// nothing else - anything else (if needed) is done in the question panels
			}

			else if (c == 'z') {
				App.flowOnlyCommandBar = App.lbx_modeExam;
				App.hideCommandBar = !App.lbx_modeExam; // uggly
				App.hideTutNavigationBar = !App.lbx_modeExam; // uggly
				giStop.userAns = "force Question"; // anything to force an answer - which for type x is also the question
				if (App.lbx_nextAndTellClicked) {
					giStop.userAns = "tellme";
				}
			}

			else if (c == 'c') {
				ArrayList<String> handAy = new ArrayList<String>();
				handAy.add(bb.getSafe(5));

				App.mg.deal = new Deal(0);
				App.mg.deal.fillDealExternal(handAy, Deal.noFill); // South is assumed
				App.tup.qp.hdp1.dealMajorChange(App.mg.deal);
			}

			else if (c == 'h') {
				ArrayList<String> handAy1 = new ArrayList<String>();
				handAy1.add(bb.getSafe(5));
				App.tup.qp.deal1 = new Deal(0);
				App.tup.qp.deal1.fillDealExternal(handAy1, Deal.noFill); // South is assumed
				App.tup.qp.hdp1.dealMajorChange(App.tup.qp.deal1);

				ArrayList<String> handAy2 = new ArrayList<String>();
				handAy2.add(bb.getSafe(6));
				App.tup.qp.deal2 = new Deal(0);
				App.tup.qp.deal2.fillDealExternal(handAy2, Deal.noFill); // South is assumed
				App.tup.qp.hdp2.dealMajorChange(App.tup.qp.deal2);
			}

			else if (c == 'p' || c == 't') {
				ArrayList<String> handAy = new ArrayList<String>();
				handAy.add(bb.getSafe(5));

				App.tup.qp.deal1 = new Deal(0);
				App.tup.qp.deal1.fillDealExternal(handAy, Deal.noFill); // South is assumed
				App.tup.qp.hdp1.dealMajorChange(App.tup.qp.deal1);
			}
		}

		/** vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv===============
		* floater last part - this has to after question as qp is a floater
		*/
		App.tup.lastFloaterAdded();
		if (floaterSeen && fh_state == Fh_NONE) {
			fh_state = Fh_ADDED;
		}

		if (giStop.capEnv.pdl_allSeatsVisible == false) {
			@SuppressWarnings("unused")
			int z = 0;
		}

		if (App.mg.lin.linType == Lin.FullMovie) {
			App.localShowHidden = giStop.capEnv.pdl_allSeatsVisible;
		}

		App.tutorialShowAuction = giStop.capEnv.pdl_auctionVisible;

		GraInfo gi_end_pg = giAy.get(end_pg);
		if (gi_end_pg.deal == null) {
			@SuppressWarnings("unused")
			int z = 0;
		}

		assert (gi_end_pg.deal != null);
		App.deal = gi_end_pg.deal.deepClone();

		if (App.mg.lin.linType == Lin.VuGraph) {
			App.deal.youSeatHint = App.youSeatHint;
		}

		if (stop_gi != end_pg) {
			adjustDealToMatch_stop();
		}

		// the order of these three is important
		if (App.deal.dfcDeal) {
			App.dfcCalcCompassPhyOffset();
		}
		else {
			// the normal case
			App.calcCompassPhyOffset();
		}
		App.dealMajorChange();
		App.gbp.matchPanelsToDealState();

		App.gbp.c1_1__tfdp.setShowCompletedTrick();

		/**
		 * The complexities of the speech bubble implementation are such that 
		 * it is best if that (transparent) panel is only visible (active) when it needs to be
		 */
		App.gbo.setVisible(App.deal.isAnyBubbleTextSet());

		App.frame.invalidate();
		App.frame.repaint();

		// System.out.println("Set Read Points - gi indexes - start, prev, stop = " + start_nt + "  " + middle_pg + " " + stop_gi);
	}

	/**
	 */
	public void adjustDealToMatch_stop() {
		// =============================================================================
		assert (stop_gi < end_pg);

		for (int i = end_pg - 1; i > stop_gi; i--) {
			GraInfo gi = giAy.get(i);
			int t = gi.qt;

			if (t == q_.pc) {
				App.deal.undoLastPlay();
			}
			else if (t == q_.mb) {
				App.deal.undoLastBid();
			}
		}

	}

	/**
	*/
	public Timer tutorialPlayTimer = new Timer(App.playPluseTimerMs, new ActionListener() {
		// ---------------------------------- Timer -------------------------------------
		public void actionPerformed(ActionEvent evt) {
			// =============================================================================
			tutorialFlowFwd();
		}
	});

	/**
	 */
	public void tutorialFlowFwd() {
		// =============================================================================
		tutorialPlayTimer.stop(); // in case it is running

		if (stop_gi >= giAy.size() - 1) {
			Controller.loadBookChapter_next();
			return;
		}

		setTheReadPoints_FwdMini();
	}

	/**
	 */
	public void send_at() {
		// =============================================================================
		if (outBuf.length() == 0)
			return;
		new GraInfo("at");
	}

	String fourCharZeroes = (char) (0) + "" + (char) (0) + "" + (char) (0) + "" + (char) (0);

//	int md_count = 0;

	/**
	 */
	public void parse_original_mn(BarBlock bb) {
		// =============================================================================
		assert (bb.qt == q_.mn);

		int mn_lines = bb.size();
		if (mn_lines > 2)
			return; // cos BBO only allows a max of one newline in an 'mn' header

		String text = bb.get(0) + (mn_lines == 2 ? bb.get(1) : "");

		if (text.length() == 0)
			return; // as per BBO app

		// build this 'gi' by hand as it is such a 'one off'
		capEnv.mn_showing = true;
		capEnv.mn_pg_countDown = (capEnv.mn_hideable_by_pg ? 2 : 0);
		capEnv.mn_lines = mn_lines;
		capEnv.mn_text = text;
		capEnv.mn_pg_countDown = 2;
		new GraInfo("mn");
	}

	/**
	 */
	public void parse_original_nt(BarBlock bb) {
		// =============================================================================
		capEnv.reset_for_nt();
		new GraInfo(bb);
		parse_original_at(bb); // in case the user has added any text
	}

	/**
	 */
	public void parse_original_at(BarBlock bb) {
		// =============================================================================

		int size_of_last_text_sent = 0;

		assert (bb.qt == q_.at || bb.qt == q_.nt);

		for (int z = 0; z < bb.size(); z++) { // each fragment should go on its own line
			if (z > 0) {
				if (capEnv.boxed && size_of_last_text_sent == 0) { // first send a space
					// outBuf.append(' ');
					// send_at();
				}
				new GraInfo("NL"); // multi part 'at' only exist to have new line between each part
			}

			String text = bb.get(z) + fourCharZeroes; // so we can always peek ahead

			int len_of_at_section = text.length();
			int i = 0;

			while (i < len_of_at_section) {
				char c0 = text.charAt(i++);

				if (c0 == 0) {
					// we have finished
					break;
				}
				// so from now on we can always peek two chars ahead
				char c1 = Aaa.toLower(text.charAt(i)); // never added to out
				char c2 = Aaa.toLower(text.charAt(i + 1)); // never added to out

				if (c0 == '{') {
					send_at();
					capEnv.boxed = true;
					new GraInfo("XB"); // BoxDraw begin
					continue;
				}

				if (c0 == '}') {
					send_at();
					new GraInfo("XE"); // BoxDraw end
					capEnv.boxed = false;
					continue;
				}

				if (c0 == '^') {
					i++; // eat c1

					if ('a' <= c1 && c1 <= 'z') { // c1 already lower case
						send_at();
						new GraInfo("VT", (c1 - (int) ('a')));
						continue;
					}

					if (c1 == '^') {
						send_at();
						new GraInfo("NL");
						while (i < len_of_at_section && text.charAt(i) == ' ')
							// eat all spaces
							i++;
						continue;
					}

					if (c1 == '-') {
						send_at();
						capEnv.centered = !capEnv.centered;
						// System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + capEnv.centered);
						new GraInfo(capEnv.centered ? "CE" : "CB");
						continue;
					}

					if (c1 == '*') {
						i++; // eat c2
						if (c2 == 'u') {
							send_at();
							capEnv.underline = true;
							continue;
						}
						if (c2 == 'b') {
							send_at();
							capEnv.bold = true;
							continue;
						}
						if (c2 == 'i') {
							send_at();
							capEnv.italic = true;
							continue;
						}
						if (c2 == 'u') {
							send_at();
							capEnv.underline = true;
							continue;
						}
						if (c2 == 'f') { // h = hyperlink g = INTERNAL hyperlink f = internaly defined funtion
							send_at();
							capEnv.bold = true; // links are 'auto bold'
							StringBuilder sectionName = new StringBuilder(0);
							char c;
							for (; i < text.length() - 1; i++) {
								if ((c = text.charAt(i)) == ',') {
									i++; // eat the comma
									break;
								}
								sectionName.append(c);
							}
							capEnv.hyperlink = new Hyperlink_f_fct(sectionName.toString());
							new GraInfo("YB");
							continue;
						}
						if (c2 == 'g') { // h = hyperlink g = INTERNAL hyperlink f = internaly defined funtion
							send_at();
							capEnv.bold = true; // links are 'auto bold'
							StringBuilder sectionName = new StringBuilder(0);
							char c;
							for (; i < text.length() - 1; i++) {
								if ((c = text.charAt(i)) == ',') {
									i++; // eat the comma
									break;
								}
								sectionName.append(c);
							}
							capEnv.hyperlink = new Hyperlink_g_int(sectionName.toString());
							new GraInfo("YB");
							continue;
						}
						if (c2 == 'h') { // h = hyperlink g = INTERNAL hyperlink f = internaly defined funtion
							send_at();
							capEnv.bold = true; // links are 'auto bold'
							StringBuilder url = new StringBuilder(0);
							char c;
							// @formatter:off
							int k;
							for (k = i; k < text.length() - 3; k++) { // uggly scan ahead
								if ((c = text.charAt(k)) == '^' 
									 && (text.charAt(k+1) == '*') 
									 && (text.charAt(k+2) == 'n' ||  text.charAt(k+2) == 'N') )
								{
									break;
								} else
									url.append(c);
							}
							// @formatter:on
							capEnv.hyperlink = new Hyperlink_h_ext(url.toString());
							new GraInfo("YB");

							boolean xw_marker_found = false;
							int from = k + 3;
							int to = (text.length() < from + 128) ? text.length() : from + 128;
							// @formatter:off
							for (k = from; k < to - 3; k++) { // ANOTHER - uggly scan ahead
								c = text.charAt(k);
								if (                   (c == '^') 
									 && (text.charAt(k+1) == '*') 
									 && (    (text.charAt(k+2) == 'x' ||  text.charAt(k+2) == 'X') 
									      || (text.charAt(k+2) == 'w' ||  text.charAt(k+2) == 'W') 
									    )
									)
								{
									xw_marker_found = true;
									break;
								}
								if (c == '|' || c == '^' || c =='@') 
									break;
							}
							// @formatter:on
							if (xw_marker_found) {
								// move the main index into the file forward to skip the display of the url
								// otherwise the url will now be parsed again in the normal way and sent as an 'at'
								i = from;
							}
							continue;
						}
						if (c2 == 'n' || c2 == 'x' || c2 == 'w') { // close of hyperlinks and bolding underline etc
							// x, w is included here to close the skip forward of the ^*h 'x' extension see above
							send_at();
							capEnv.bold = false;
							capEnv.italic = false;
							capEnv.underline = false;
							if (capEnv.hyperlink != null) {
								capEnv.hyperlink.loadIfLin = (c2 != 'w');
								capEnv.hyperlink = null;
								new GraInfo("YE");
							}
							continue;
						}
					}
				}

				if (c0 == '@') {
					i++; // eat c1

					if (c1 == '-') {
						send_at();
						new GraInfo("@-"); // we need a special to stop any line breaks c0 = 0x2014; // unicode for an em-dash
						continue;
					}

					if (c1 == '.') {
						send_at();
						new GraInfo("@."); // bullet point
						continue;
					}

					if ('0' <= c1 && c1 <= '3') {
						send_at();
						new GraInfo("@N", (c1 - (int) ('0')));
						continue;
					}

					if (c1 == '4') {
						send_at();
						new GraInfo("@4"); // clear to end of screen
						continue;
					}

					if (c1 == 'c') {
						send_at();
						new GraInfo("@S", Suit.Clubs.v);
						continue;
					}

					if (c1 == 'd') {
						send_at();
						new GraInfo("@S", Suit.Diamonds.v);
						continue;
					}

					if (c1 == 'h') {
						send_at();
						new GraInfo("@S", Suit.Hearts.v);
						continue;
					}

					if (c1 == 's') {
						send_at();
						new GraInfo("@S", Suit.Spades.v);
						continue;
					}

					if (c1 == '@') {
						// drop through - two @'s make one @
					}
					else if (c1 == '^') {
						// drop through - show as the ^ an aaBridge only extension
						c0 = '^';
					}
					else if (c1 == '{') {
						// drop through - show as the ^ an aaBridge only extension
						c0 = '{';
					}
					else if (c1 == '}') {
						// drop through - show as the ^ an aaBridge only extension
						c0 = '}';
					}
					else {
						// discard the unknown command - assume user error
						continue;
					}

				}

				if (c0 == '#') {
					// TO DO finish # impl
					// int real_C1 = (int) text.charAt(i);
					i++; // eat c1
					if (c1 == '#') {
						// drop through as c0 is still # and will be sent
					}
					// the >>>> else <<<< is important !!!!!!!!!!!!!!!!!!!
					else {
						// int n = real_C1 - (int) '0';
						// set offset
						continue;
					}

				}
				outBuf.append(c0);
			}
			size_of_last_text_sent = outBuf.length();

			send_at(); // send anything in the output buffer
		}
	}

	/**
	 */
	public void pdl__ih(BarBlock bb) {
		// =============================================================================
		// @formatter:off
		// System.out.println("p2 p2 p2 p2 p2 - ih insert hand  " + bb.getSafe(0) 
		//				  + " - " + bb.getSafe(1) + " - " + bb.getSafe(2)	+ " - " + bb.getSafe(3) + " <");
		// @formatter:on
		GraInfo gi = new GraInfo(bb);
		gi.deal_ih_ia = new Deal(0 /* local id */);
		gi.deal_ih_ia.fillDealExternal(bb, Deal.noFill);

		@SuppressWarnings("unused")
		int z = 0;
	}

	/**
	 */
	public void pdl__ia(BarBlock bb) {
		// =============================================================================
		// System.out.println("p2 p2 p2 p2 p2 - ia insert auction  " + bb.getSafe(0) + " <");

		GraInfo gi = new GraInfo(bb);
		gi.deal_ih_ia = new Deal(0 /* local id */);
		gi.deal_ih_ia.setDealer(Dir.West);
		gi.deal_ih_ia.makeLinBid_FrontAlert(bb.get(0));

		@SuppressWarnings("unused")
		int z = 0;
	}

	/**
	 */
	public void pdl__sb(BarBlock bb) { // speach bubble
		// =============================================================================
		App.deal.changed = true;
		if (bb.get(0).isEmpty())
			return; // Need to have a Direction as first char
		char ch = bb.get(0).charAt(0);
		String text = bb.get(0).substring(1).trim();
		App.deal.setBubbleText(ch, text);

		// new GraInfo(bb); why bother
	}

	private Deal prev_clone = null;

	/**
	 */
	public void pdlx__lb(BarBlock bb) { // question
		// =============================================================================
		capEnv.font_slot_fp = 0; // cos that's a side effect of BBO
		GraInfo gi = new GraInfo(bb);

		if ((gi.qt == q_.lb && gi.bb.size() == 5) && (gi.bb.get(0).length() == 1) && (gi.bb.get(0).charAt(0) == '*') && (gi.bb.get(1).length() == 1)
				&& (gi.bb.get(1).contentEquals("m"))) {
			String s = gi.bb.get(3).replace("&", "");
			gi.bb.set(3, s);
		}

		if (gi.bb.get(1).contentEquals("z")) { // z => Dirtribution training and exam question
			App.deal.dfcDeal = true;
			App.deal.changed = true;

			String stage = gi.bb.get(2);

			// convert to training (if should be)
			if (stage.contentEquals("1") && (App.lbx_modeExam == false)) {
				gi.bb.set(2, "0");
				stage = "0";
			}

			gi.bb.add("0~1~2~3~4~5~6~7");

			if (stage.contentEquals("0")) {
				App.deal.fillDealDistribution_0_Training(App.dfcTrainingSuitSort);
				App.lbx_earlyEndMassGi = true; // end processing of lin bb's
			}
			else if (stage.contentEquals("1")) {
				// we pick either east or west
				capEnv.pdl_allSeatsVisible = true;
				App.deal.fillDealDistributionExam_1_Deal(App.dfcExamDifficulity, App.dfcExamYou, App.dfcExamBottomYou);
			}
			else if (stage.contentEquals("B")) {
				boolean kill = App.deal.fillDealDistributionExam_2_Bid(App.dfcExamDifficulity, App.dfcExamYou, App.dfcExamBottomYou);
				if (kill) {
					gi.kill();
				}
			}
			else if (stage.contentEquals("D")) {
				App.deal.setDealer(Dir.South);
				App.deal.makeBid(new Bid(Level.One, Suit.Spades));
				App.deal.makeBid(new Bid(Call.Pass));
				App.deal.makeBid(new Bid(Call.Pass));
				App.deal.makeBid(new Bid(Call.Pass));

				App.deal.fillDealDistributionExam_3_Dummy();
			}
			else if (stage.contentEquals("4")) {
				boolean kill = App.deal.fillDealDistributionExam_4_Play(App.dfcExamDifficulity, App.dfcExamYou, App.dfcExamBottomYou);
				if (kill) {
					gi.kill();
				}
			}
			else if (stage.contentEquals("5")) {
				App.deal.fillDealDistributionExam_5_Play(App.dfcExamDifficulity, App.dfcExamYou, App.dfcExamBottomYou);
			}
			else if (stage.contentEquals("6")) {
				App.deal.fillDealDistributionExam_6_Play(App.dfcExamDifficulity, App.dfcExamYou, App.dfcExamBottomYou);
			}
			else if (stage.contentEquals("Q")) {
				App.deal.fillDealDistributionExam_7_Question(App.dfcExamDifficulity, App.dfcExamYou, App.dfcExamBottomYou);
			}
			else if (stage.contentEquals("T")) {
				App.deal.fillDealDistributionExam_8_Tell();
			}
			else if (stage.contentEquals("A")) {
				App.deal.fillDealDistributionExam_9_All();
			}
			else if (stage.contentEquals("X")) {
				App.deal.fillDealDistributionExam_9_XClear();
			}
//			else if (stage.contentEquals("R")) {
//				App.deal.fillDealDistributionExam_R_Restart();
//				// Nothing needed
//			}

			@SuppressWarnings("unused")
			int z = 0;
		}

		// prev_clone = null;
		if (App.deal.changed || prev_clone == null) {
			prev_clone = App.deal;
			gi.deal = App.deal;
			App.deal = gi.deal.deepClone();
			App.deal.localId = Deal.idCounter.getAndIncrement();
		}
		else {
			gi.deal = prev_clone;
		}
	}

	/**
	 */
	public void pdlx__pg(BarBlock bb) { // pg
		// =============================================================================
		if (capEnv.mn_pg_countDown > 0) {
			capEnv.mn_pg_countDown--;
			if (capEnv.mn_pg_countDown == 0 && capEnv.mn_hideable_by_pg) {
				capEnv.mn_showing = false;
			}
		}

		GraInfo gi = new GraInfo(bb);
		// prev_clone = null;
		if (App.deal.changed || prev_clone == null) {
			prev_clone = App.deal;
			gi.deal = App.deal;
			App.deal = gi.deal.deepClone();
			App.deal.localId = Deal.idCounter.getAndIncrement();
		}
		else {
			gi.deal = prev_clone;
		}
	}

	/**
	 */
	public void pdl__ub(BarBlock bb) { // undo (last) abid(s)
		// =============================================================================
		App.deal.changed = true;
		App.deal.undoLastBids_ignoreTooMany(Aaa.parseIntWithFallback(bb.get(0), 1));
		new GraInfo(bb);
	}

	/**
	 */
	public void pdl__up(BarBlock bb) { // undo (last) play(s)
		// =============================================================================
		App.deal.changed = true;
		App.deal.undoLastPlays_ignoreTooMany(Aaa.parseIntWithFallback(bb.get(0), 1));
		new GraInfo(bb);
	}

	/**
	 */
	public void pdl__md(BarBlock bb) { // Make Deal
		// =============================================================================
		App.deal.changed = true;
		App.deal.endedWithClaim = false;
		// @formatter:off
		// System.out.println("p2 p2 p2 p2 p2 - md make deal  " + bb.getSafe(0) 
		//				  + " - " + bb.getSafe(1) + " - " + bb.getSafe(2)	+ " - " + bb.getSafe(3) + " <");
		// @formatter:on

//		if (App.visualMode = ? && (bb.size() < 3)) { // we must have 3 or 4 hands (non tutorial)
//			// @SuppressWarnings("unused")
//			// int z = 0; // put your breakpoint here
//			// throw new IOException();
//			// BBO can produce such files if there are less than three players at the moment of dealing
//			// at the table
//			// All the above maybe true but we STILL need this for tutorial lin files.
//			App.deal = new Deal(0);
//			return;
//		}

		App.deal.fillDealExternal(bb, Deal.yesFill);
		new GraInfo(bb);
	}

	/**
	 */
	public void pdl__pn(BarBlock bb) { // Player Names
		// =============================================================================
		App.deal.changed = true;
		App.deal.setPlayerNames(bb);
		new GraInfo(bb);
	}

	/**
	 */
	public void pdl__sk(BarBlock bb) { // Seat Kibitz
		// =============================================================================
		// in theory multiple values can be supplied and none means hide none
		// we are going to make the first letter of the you seat
		App.deal.changed = true;
		String s = bb.get(0);
		if (s.length() > 0)
			App.deal.setYouSeatHint(s);
		capEnv.pdl_allSeatsVisible = s.isEmpty() || (s.length() > 0) && (s.charAt(0)=='y' || s.charAt(0)=='Y');
		// GraInfo gi = new GraInfo(bb);
		// System.out.println("gi.index :" + gi.index + " s: " + s + "-");
	}

	/**
	 */
	public void pdl__ha(BarBlock bb) { // Hide Auction
		// =============================================================================
		App.deal.changed = true;
		String s = bb.get(0);
		capEnv.pdl_auctionVisible = s.isEmpty();
		new GraInfo(bb);
	}

	/**
	 */
	public void pdl__sv(BarBlock bb) { // Set Vulnerability
		// =============================================================================
		// in theory multiple values can be supplied and none means hide none
		// we are going to make the first letter of the you seat
		App.deal.changed = true;
		App.deal.setVulnerability(bb.get(0));
		new GraInfo(bb);
	}

	/**
	 */
	public void pdl__an(BarBlock bb) { // make bid
		// =============================================================================
		App.deal.changed = true;
		App.deal.addAnouncementToLastBid(bb.get(0));
		new GraInfo(bb);
	}

	/**
	 */
	public void pdl__rh(BarBlock bb) { // remove all header parts
		// =============================================================================
		App.deal.changed = true;
		App.deal.ahHeader = "";
		new GraInfo(bb);
	}

	/**
	 */
	public void pdl__ah(BarBlock bb) { // add to header
		// =============================================================================
		App.deal.changed = true;

		String h = bb.get(0).toLowerCase();
		int p = h.indexOf("board");
		int q = h.indexOf("brd");
		String s = "";
		if ((p == -1 && q == -1) /* || App.deal.displayBoardId.isEmpty() */) {
			App.deal.ahHeader += bb.get(0) + " ";
		}
		else {

			h = bb.get(0) + "  "; // restore
			if (p > -1)
				s = h.substring(p + 6);
			else
				s = h.substring(p + 4);
			s = s.trim() + " ";
			h = s.substring(0, s.indexOf(' '));
			// int n = Aaa.extractPositiveInt(h);

			App.deal.displayBoardId = h;
		}
		new GraInfo(bb);
	}

	/**
	 */
	public void pdl__mc(BarBlock bb) { // make claim
		// =============================================================================
		App.deal.changed = true;
		Point score = App.deal.getContractTrickCountSoFar();
		int claimed = Aaa.extractPositiveInt(bb.get(0));
		if (claimed >= score.x) {
			App.deal.endedWithClaim = true;
			App.deal.tricksClaimed = claimed;
		}
		new GraInfo(bb);
	}

	/**
	 */
	private void addBid(String s) {
		// =============================================================================
		// System.out.print(" " + s + "");
		App.deal.makeLinBid_FrontAlert(s);
		GraInfo gi = new GraInfo(activeBb);
		gi.text = s;
	}

	/**
	 */
	public void pdl__mb(BarBlock bb) { // Make bid
		// =============================================================================
		App.deal.changed = true;
		// System.out.println("");
		// System.out.print("pdl__mb - " + bb.get(0) + " - ");

		String bids = bb.get(0);
		Level level = Level.Invalid;
		Suit suit = Suit.Invalid;

		String thisAlert = "";
		;
//		boolean skipping = true;
		boolean qmSeen = false;

		for (int i = 0; i < bids.length(); i++) {
			char c = bids.charAt(i);
			char next = (i + 1 < bids.length()) ? bids.charAt(i + 1) : 0x00;
			thisAlert = (next == '!') ? "!" : "";

			if (c == '?') {
				qmSeen = true;
				continue;
			}
			if (c == '-' || c == ' ' || c == ',') {
				// removed as it appears to cause a bug with older lins - it is just wrong headed
				// if (skipping)
				// App.deal.dealer = App.deal.dealer.nextClockwise();
				continue;
			}
//			skipping = false;

			if (c == '!') {
				continue; // leftover from previous bid
			}

			if (c == 'p' || c == 'P') {
				addBid(thisAlert + "p");
				continue;
			}
			if ((level == Level.Invalid) && (c == 'd' || c == 'D' || c == 'x' || c == 'X' || c == '*')) { // d and D do double duty
				addBid(thisAlert + "d");
				continue;
			}
			if (c == 'r' || c == 'R') {
				addBid(thisAlert + "r");
				continue;
			}
			if ('1' <= c && c <= '7') {
				level = Level.levelFromInt(c - '0');
				continue;
			}

			// @formatter:off
			switch (c) {
				case 'N': case 'n': suit = Suit.NoTrumps; break;
				case 'S': case 's': suit = Suit.Spades;   break;
				case 'H': case 'h': suit = Suit.Hearts;   break;
				case 'D': case 'd': suit = Suit.Diamonds; break; // d and D can also mean double
				case 'C': case 'c': suit = Suit.Clubs;    break;
				default:
					// System.out.println("pdl__mb unknown char: " + c);
					break; // ??????
			}
			// @formatter:on
			if (suit == Suit.Invalid || level == Level.Invalid) {
				// System.out.println("pdl__mb   Suit or Level INVALID");
				continue; // ????????
			}
			addBid(thisAlert + level.toLinStr() + suit.toLinStr()); // calls makeLinBid_FrontAlert() !

			level = Level.Invalid;
			suit = Suit.Invalid;
		}
		App.deal.showBidQuestionMark = qmSeen;

//		System.out.println("");
	}

	/**
	 */
	public void pdl__wt(BarBlock bb) { // set vulnerability
		// =============================================================================
		App.deal.changed = true;
		App.deal.setLastTrickWinnerExternal(bb.get(0));
		new GraInfo(bb);
	}

	/**
	 */
	public void pdl__pc(BarBlock bb) { // Make Deal
		// =============================================================================
		App.deal.changed = true;
		String cds = bb.get(0);

		// System.out.println(bb.lineNumber + " pdl__pc arrive: " + cds);

		// PC|qd3| causes NetBridgeVu to play the lowest diamond
		// when neither the q or 3 are present in the hand
		// we need to mimic this PC|d| plays the lowest diamond

		if (App.deal.prevTrickWinner.size() == 0) {
			/**
			 * When here is of no contract and very likley
			 * only fragments of hands then apparently the
			 * default is that South is the dealer and 
			 * declarer? with no contract and the first to lead !
			 */
			App.deal.dealer = Dir.South;
			App.deal.contractCompass = Dir.South;
			App.deal.prevTrickWinner.add(App.deal.hands[Dir.South.v]);
		}

		Suit suit = Suit.Invalid;
		Rank rank = Rank.Invalid;
		boolean lowestForced = false;

		cds += "2"; // this forces the lowest card of that suit IFF there was a bare suit just before it

		for (int i = 0; i < cds.length(); i++) { // well there SHOULD BE a max of TWO chars in each string
			char c = cds.charAt(i);
			if (suit == Suit.Invalid) {
				suit = Suit.charToSuit(c);
				// we ignore any chars when we do not have a valid suit.
				// and we continue for the level when we do
				continue;
			}

			lowestForced = i == (cds.length() - 1);

			/** To get here we have a valid suit and another character in hand 
			 */
			if (Suit.charToSuit(c) != Suit.Invalid) {
				// this to is a valid suit so we force the lowest card
				i--; // so we will get it next time when the current suit has been eaten
				rank = Rank.BelowAll;
				lowestForced = true;
			}
			else {
				rank = Rank.charToRank(c);
				if (rank == Rank.Invalid) {
					System.out.println(bb.lineNumber + " pdl__pc - Invalid Rank: " + cds + " " + suit + " " + c);
					continue;
				}
			}

			if (rank != Rank.BelowAll) {
				if (App.deal.checkCardExternal(suit, rank) == false) {
					if (!lowestForced)
						System.out.println(bb.lineNumber + " pdl__pc - Card played not in hand!: " + suit + " " + rank + "  will try to play lowest");
					rank = Rank.BelowAll; // so it will try the lowest
				}
			}

			if (rank == Rank.BelowAll) {
				Card card2 = App.deal.getLowestCardExternal(suit);
				if (card2 == null) {
					System.out.println(bb.lineNumber + " pdl__pc - No lowest card in suit: " + cds + " " + suit);
					suit = Suit.Invalid;
					continue;
				}
				rank = card2.rank;
				System.out.println(bb.lineNumber + " pdl__pc - 'Below all' selected: " + suit + " " + rank);
			}

			if (App.deal.checkCardExternal(suit, rank) == false) {
				System.out.println(bb.lineNumber + " pdl__pc - Card not found: " + cds + " " + rank + " " + suit);
				continue;
			}
			String oCard = suit.toLinStr() + rank.toStr();
			App.deal.playLinCard(oCard);
			new GraInfo(activeBb); // clones the deal

			suit = Suit.Invalid; // so we will get the next

			// System.out.println(bb.lineNumber + " pdl__pc depart: " + oCard);
		}

	}

	/** *********************************************************************************
	 * Called once  (per lin file)  to create the   giAy  Array List
	 * **********************************************************************************
	 */
	public void oneTimeParse_lin_create_giAy() throws IOException {
		// =============================================================================

		/** 
		 * This is the biggie that does the, constructor time, heavy lifting
		 * it adds to the GraInfo array  (automaticaly in the called functions)
		 * 
		 * First we add a few that will reset when re return to start
		 */
		App.deal = new Deal(0); // real index will also be zero
		App.deal.setDealer(Dir.South);
		// App.dealMajorChange();

		new GraInfo(activeBb = lin.new BarBlock("AA", 0)); // a null one to be the first
		new GraInfo(activeBb = lin.new BarBlock("st", 0)); // st standard table - the lin spec default
		new GraInfo(activeBb = lin.new BarBlock("nt", 0)); // nt

		int bbCount = lin.bbAy.size();

		App.lbx_earlyEndMassGi = false;

		for (int i = 0; i < bbCount; i++) {
			if (App.lbx_earlyEndMassGi)
				break;

			BarBlock bb = lin.bbAy.get(i);
			activeBb = bb;

			int t = bb.qt;
			if (bb.size() == 0) {
				@SuppressWarnings("unused")
				int z = 0; // put your breakpoint here
				System.out.println("bb missing its data string - '" + bb.type + "'");
				// assert(false);
				// this will be a corrupt / badly formed .lin
				// we just skip this bb
				continue;
			}
			String s = bb.get(0);

			// System.out.println(bb.lineNumber + " Make_gi oneTimeParse - " + bb.type + " " + s);

			// @formatter:off
			if (t == q_.cr || 
				t == q_.cg || 
				t == q_.cb)  { insertColorPart(bb.type, s); 	continue; }
			if (t == q_.fh)  { insertFontHeight(s);         	continue; }
			if (t == q_.ff)  { insertFontFamily(s);         	continue; }
			if (t == q_.fb)  { insertFontBold(s);           	continue; }
			if (t == q_.fi)  { insertFontItalic(s);         	continue; }
			if (t == q_.fu)  { insertFontUnderline(s);      	continue; }
			if (t == q_.fm)  { insertFontMake(s);           	continue; }
			
			if (t == q_.fp)  { setFont_fp(s);               	continue; }
			if (t == q_.cp)  { setColor_cp(s);              	continue; }
			if (t == q_.cq)  { setColor_cq(s);              	continue; }
			if (t == q_.cs)  { setColor_cs(s);              	continue; }
			
			if (t == q_.hf)  { setQuestPosition_hf(s);       	continue; }
			
			if (t == q_.ht)  { process_ht(bb);               	continue; }
			if (t == q_.tu)  { process_tu(bb);               	continue; }
			
			if (t == q_.bt)  { process_bt(bb);                	continue; }
			if (t == q_.st)  { process_st(bb);                	continue; }
			if (t == q_.qx)  { process_qx(bb);                	continue; }


			if (t == q_.nt)  { parse_original_nt(bb);         	continue; }
			if (t == q_.at)  { parse_original_at(bb);         	continue; }
			if (t == q_.mn)  { parse_original_mn(bb);         	continue; }

			if (t == q_.lb)  { pdlx__lb(bb);                 	continue; }
			if (t == q_.pg)  { pdlx__pg(bb);                 	continue; }
			if (t == q_.sb)  { pdl__sb(bb);                 	continue; }
			if (t == q_.pn)  { pdl__pn(bb);                 	continue; }
			if (t == q_.sk)  { pdl__sk(bb);                 	continue; }
			if (t == q_.ha)  { pdl__ha(bb);                 	continue; }
			if (t == q_.md)  { pdl__md(bb);                 	continue; }
			if (t == q_.mb)  { pdl__mb(bb);                 	continue; }
			if (t == q_.an)  { pdl__an(bb);                 	continue; }
			if (t == q_.up)  { pdl__up(bb);                 	continue; }
			if (t == q_.ub)  { pdl__ub(bb);                 	continue; }
			if (t == q_.mc)  { pdl__mc(bb);                 	continue; }
			if (t == q_.pc)  { pdl__pc(bb);                 	continue; }
			if (t == q_.sv)  { pdl__sv(bb);                 	continue; }
			if (t == q_.rh)  { pdl__rh(bb);                 	continue; }
			if (t == q_.ah)  { pdl__ah(bb);                 	continue; }
			if (t == q_.wt)  { pdl__wt(bb);                 	continue; }
			if (t == q_.ih)  { pdl__ih(bb);                 	continue; }
			if (t == q_.ia)  { pdl__ia(bb);                 	continue; }
			if (t == q_.sc)  { /* ignored */                	continue; }
			if (t == q_.pf)  { /* ignored */                	continue; }
			if (t == q_.va)  { /* ignored */                	continue; }
			if (t == q_.d3)  { /* ignored */                	continue; }
			if (t == q_.hc)  { /* ignored */                	continue; }
			if (t == q_.lc)  { /* ignored */                	continue; }
			if (t == q_.hs)  { /* ignored */                	continue; }
			if (t == q_.ls)  { /* ignored */                	continue; }
			if (t == q_.pw)  { /* ignored */                	continue; }
			if (t == q_.bn)  { /* ignored */                	continue; }
			if (t == q_.bg)  { /* ignored */                	continue; }
			if (t == q_.se)  { /* ignored */                	continue; }
			if (t == q_.bm)  { /* ignored */                	continue; }
			if (t == q_.wb)  { /* ignored */                	continue; }
			if (t == q_.hb)  { /* ignored */                	continue; }
			if (t == q_.xx)  { /* ignored */                	continue; }
			if (t == q_.vg)  { /* ignored */                	continue; }
			if (t == q_.rs)  { /* ignored */                	continue; }
			if (t == q_.mp)  { /* ignored */                	continue; }
			if (t == q_.lf)  { /* ignored */                	continue; }
			if (t == q_.tc)  { /* ignored */                	continue; }
 			
			
			System.out.println(bb.lineNumber + " Make_gi oneTimeParse - unknown bb type -" + bb.type + "- " + s);

			// @formatter:on
		}

		/**
		 *  add a pg on the end if there is not already one there 
		 */
		if (lin.bbAy.get(lin.bbAy.size() - 1).qt != q_.pg) {
			pdlx__pg(activeBb = lin.new BarBlock("pg", 0));
		}

		setTheReadPoints_FirstTime();
	}

	public int findPgIdBeforeThisBarBlock(BarBlock qx_bb) {
		// =============================================================================
		for (GraInfo gi : giAy) {
			if (gi.bb == qx_bb) {
				for (int i = gi.index; i > 0; i--) {
					GraInfo gi2 = giAy.get(i);
					if (gi2.qt == q_.qx)
						return i;
				}
			}
		}
		return 0;
	}

	public int findPgIdBeforeNextQx(BarBlock qx_bb) {
		// =============================================================================
		for (int i = 0; i < giAy.size(); i++) {
			GraInfo gi = giAy.get(i);

			if (gi.bb == qx_bb) { // this is the known qx now we want the next
				int gi_pg_index = 0;
				// now we want the next qx
				for (int k = i + 1; k < giAy.size(); k++) {
					GraInfo gi2 = giAy.get(k);

					if (gi2.qt == q_.pg)
						gi_pg_index = k;

					if (gi2.qt == q_.qx || (k == giAy.size() - 1)) {
						return gi_pg_index;
					}
				}
			}
		}
		return 0;
	}

	public int getPrevPg(int pg1) {
		// =============================================================================
		for (int i = pg1 - 1; i > 0; i--) {
			GraInfo gi = giAy.get(i);
			if (gi.qt == q_.pg)
				return i;
		}
		return pg1;
	}

	public int getNextPg(int pg1) {
		// =============================================================================
		for (int i = pg1 + 1; i < giAy.size(); i++) {
			GraInfo gi = giAy.get(i);
			if (gi.qt == q_.pg)
				return i;
		}
		return pg1;
	}

}
