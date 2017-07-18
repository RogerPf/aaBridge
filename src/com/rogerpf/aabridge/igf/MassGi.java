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
import com.rogerpf.aabridge.controller.Aaf;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.controller.Controller;
import com.rogerpf.aabridge.controller.MruCollection;
import com.rogerpf.aabridge.controller.q_;
import com.rogerpf.aabridge.model.Bid;
import com.rogerpf.aabridge.model.Call;
import com.rogerpf.aabridge.model.Card;
import com.rogerpf.aabridge.model.Cc;
import com.rogerpf.aabridge.model.Deal;
import com.rogerpf.aabridge.model.Dir;
import com.rogerpf.aabridge.model.Hand;
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
import com.version.VersionAndBuilt;

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

	public boolean buildTime__tc_suppress_pc_display = false;
	public boolean pc_autoClear__buildTime__tc_suppress_pc_display = false;

	public boolean autoAdd_missing_playedCards = false;

	StringBuilder outBuf = new StringBuilder();

	int graInfo_nextIndex = 0;

	boolean xx_next_at_command = false;
	boolean abort_after_next_at_command = false;

	int source_mn = 0;
	int start_nt = 0;
	int middle_pg = 0;
	public int end_pg = 0;
	public int stop_gi = 0;

	public int page_numb_display = 0; // used by pg and questions to show page number

	public MruCollection.MruChapter mruChap = null;

	public boolean cameFromPbn = false;

	Capture_gi_env capEnv = new Capture_gi_env();

	ArrayList<Hyperlink> hyperlinkAy = new ArrayList<Hyperlink>();

	// @formatter:off
	LinColor linColorAy[] = { 
			new LinColor(),   new LinColor(),     new LinColor(),   new LinColor(),
			new LinColor(),   new LinColor(),     new LinColor(),   new LinColor(),
			new LinColor(),   new LinColor(),     new LinColor(),   new LinColor(),
			new LinColor(),   new LinColor(),     new LinColor(),   new LinColor(),
			new LinColor(),   new LinColor(),     new LinColor(),   new LinColor(),

			new LinColor(),   new LinColor(),     new LinColor(),   new LinColor(),
			new LinColor(),   new LinColor(),     new LinColor(),   new LinColor(),
			new LinColor(),   new LinColor(),     new LinColor(),   new LinColor(),
			new LinColor(),   new LinColor(),     new LinColor(),   new LinColor(),
			new LinColor(),   new LinColor(),     new LinColor(),   new LinColor(),
			};

	Color cAy_std[] = {  // these are the default text colors for all 40 slots
			Color.black,   Color.black,       Color.black,   Color.black,
			Color.black,   Color.black,       Color.black,   Color.black,
			Color.black,   Color.black,       Color.black,   Color.black,
			Color.black,   Color.black,       Color.black,   Color.black,
			Color.black,   Color.black,       Color.black,   Color.black,
			
			Color.black,   Color.black,       Color.black,   Color.black,
			Color.black,   Color.black,       Color.black,   Color.black,
			Color.black,   Color.black,       Color.black,   Color.black,
			Color.black,   Color.black,       Color.black,   Color.black,
			Color.black,   Color.black,       Color.black,   Color.black,
			};

	Color cAy_fill[] = {  // these are the default fill colors for all 40 slots
			Aaa.tutorialBackground,   Aaa.tutorialBackground,       Aaa.tutorialBackground,   Aaa.tutorialBackground,
			Aaa.tutorialBackground,   Aaa.tutorialBackground,       Aaa.tutorialBackground,   Aaa.tutorialBackground,
			Aaa.tutorialBackground,   Aaa.tutorialBackground,       Aaa.tutorialBackground,   Aaa.tutorialBackground,
			Aaa.tutorialBackground,   Aaa.tutorialBackground,       Aaa.tutorialBackground,   Aaa.tutorialBackground,
			Aaa.tutorialBackground,   Aaa.tutorialBackground,       Aaa.tutorialBackground,   Aaa.tutorialBackground,
			
			Aaa.tutorialBackground,   Aaa.tutorialBackground,       Aaa.tutorialBackground,   Aaa.tutorialBackground,
			Aaa.tutorialBackground,   Aaa.tutorialBackground,       Aaa.tutorialBackground,   Aaa.tutorialBackground,
			Aaa.tutorialBackground,   Aaa.tutorialBackground,       Aaa.tutorialBackground,   Aaa.tutorialBackground,
			Aaa.tutorialBackground,   Aaa.tutorialBackground,       Aaa.tutorialBackground,   Aaa.tutorialBackground,
			Aaa.tutorialBackground,   Aaa.tutorialBackground,       Aaa.tutorialBackground,   Aaa.tutorialBackground,
			};
	// @formatter:on

	final static int zero_color_slot = 0;
	final static int invalid_slot = -1;
	final static int test_for_named_color = -3;
	final static int last_letter_color_slot = 35; // 0-9, a-z, => 0-35,

	final static int red_slot = last_letter_color_slot + 1;
	final static int blue_slot = last_letter_color_slot + 2;
	final static int green_slot = last_letter_color_slot + 3;
	final static int mustard_slot = last_letter_color_slot + 4;

	void construct_named_colors() {
		linColorAy[red_slot].setRGB(0, 255, red_slot, cAy_std, cAy_fill);

		linColorAy[blue_slot].setRGB(2, 230, blue_slot, cAy_std, cAy_fill);

		linColorAy[green_slot].setRGB(0, 70, green_slot, cAy_std, cAy_fill);
		linColorAy[green_slot].setRGB(1, 185, green_slot, cAy_std, cAy_fill);
		linColorAy[green_slot].setRGB(2, 60, green_slot, cAy_std, cAy_fill);

		linColorAy[mustard_slot].setRGB(0, 255, mustard_slot, cAy_std, cAy_fill);
		linColorAy[mustard_slot].setRGB(1, 190, mustard_slot, cAy_std, cAy_fill);
		linColorAy[mustard_slot].setRGB(2, 0, mustard_slot, cAy_std, cAy_fill);
	}

	/**
	 */
	public MassGi(Deal deal) { // constructor
		// =============================================================================
		construct_named_colors();
		assert (deal != null);
		this.lin = new Lin(deal);
	}

	/**
	 */
	public MassGi(Lin lin) { // constructor
		// =============================================================================

		App.handPanelNameAreaInfoNumbersShow = true;

		construct_named_colors();
		assert (lin != null);
		this.lin = lin;
		this.cameFromPbn = lin.cameFromPbn;

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
	int extractColorSlot(String ns, int default_slot) {
		// =============================================================================
		if (ns.isEmpty())
			return default_slot;

		if (ns.length() > 1 && (ns.charAt(1) >= 'A')) {
			return test_for_named_color;
		}

		char c = ns.charAt(0);

		if ('0' <= c && c <= '9')
			return c - '0';

		if ('a' <= c && c <= 'z')
			return c - 'a' + 10;

		if ('A' <= c && c <= 'Z')
			return c - 'A' + 10;

		return default_slot;
	}

	/**
	 */
	private void insertColorPart(String type, String ns) {
		// =============================================================================
		int slot = extractColorSlot(ns, invalid_slot /* default */);
		if (slot == invalid_slot || slot == test_for_named_color)
			return;

		int len = ns.length();
		if (len < 2)
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

	/**
	 * The caller will (must) have tested that the string is a least 2 chars long
	 */
	private int getBuiltInColorSlot(String ns) {
		// =============================================================================
		String s = ns.toLowerCase();

		// @formatter:off
		if (s.startsWith("re")) return red_slot;
		if (s.startsWith("bl")) return blue_slot;
		if (s.startsWith("gr")) return green_slot;
		if (s.startsWith("mu")) return mustard_slot;
		// @formatter:on
		return invalid_slot;
	}

	/**
	 * The caller will (must) have tested that the string is a least 2 chars long
	 */
	private Color getNamedSuitColor_text(String ns) {
		// =============================================================================
		String s = ns.toLowerCase();

		// @formatter:off
		if (s.startsWith("sp")) return Cc.SuitColor(Suit.Spades, Cc.Ce.Strong);
		if (s.startsWith("he")) return Cc.SuitColor(Suit.Hearts, Cc.Ce.Strong);
		if (s.startsWith("di")) return Cc.SuitColor(Suit.Diamonds, Cc.Ce.Strong);
		if (s.startsWith("cl")) return Cc.SuitColor(Suit.Clubs, Cc.Ce.Strong);
		if (s.startsWith("bg")) return Aaa.tutorialBackground;
		if (s.startsWith("wh")) return Color.WHITE; 
		if (s.startsWith("lg")) return Aaa.lightGrayBubble;
		if (s.startsWith("mg")) return Aaa.mediumGray;
		if (s.startsWith("dg")) return Aaa.darkGrayBg;
		// @formatter:on
		return null;
	}

	/**
	 * The caller will (must) have tested that the string is a least 2 chars long
	 */
	private Color getNamedSuitColor_fill(String ns) {
		// =============================================================================
		String s = ns.toLowerCase();

		// @formatter:off
		if (s.startsWith("sp")) return Cc.SuitColor(Suit.Spades, Cc.Ce.Strong);
		if (s.startsWith("he")) return Cc.SuitColor(Suit.Hearts, Cc.Ce.Strong);
		if (s.startsWith("di")) return Cc.SuitColor(Suit.Diamonds, Cc.Ce.Strong);
		if (s.startsWith("cl")) return Cc.SuitColor(Suit.Clubs, Cc.Ce.Strong);
		if (s.startsWith("bg")) return Aaa.tutorialBackground;
		if (s.startsWith("wh")) return Color.WHITE;   // converted to tutorialBackground at fill time.
		if (s.startsWith("lg")) return Aaa.lightGrayBubble;
		if (s.startsWith("mg")) return Aaa.mediumGray;
		if (s.startsWith("dg")) return Aaa.darkGrayBg;
		// @formatter:on
		return null;
	}

	/**
	 */
	void setColor_cp(String ns) { // color pick - current font color
		// =============================================================================
		int slot = extractColorSlot(ns, zero_color_slot /* default_slot */);

		if (slot == test_for_named_color) {
			Color color = getNamedSuitColor_text(ns);
			if (color != null) {
				capEnv.color_cp = color;
				return;
			}

			slot = getBuiltInColorSlot(ns);
			if (slot == invalid_slot)
				return;
		}

		capEnv.color_cp = cAy_std[slot];
	}

	/**
	 */
	void setColor_cs(String ns) { // color set - current BOX FILL color
		// =============================================================================
		int slot = extractColorSlot(ns, zero_color_slot /* default_slot */);

//		if (slot == zero_color_slot) {
//		    // of course backgrounds do not use the built-in zero default of "black"
//			capEnv.color_cs = Aaa.tutorialBackground;
//			return;
//		}

		if (slot == test_for_named_color) {
			Color color = getNamedSuitColor_fill(ns);
			if (color != null) {
				capEnv.color_cs = color;
				return;
			}

			slot = getBuiltInColorSlot(ns);
			if (slot == invalid_slot)
				return;
		}

		capEnv.color_cs = cAy_fill[slot];
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
		boolean uni = false;

		public Capture_gi_env capEnv;

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
			uni = o.uni;
			// hdg and btp are NOT copied
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

		GraInfo(String typeV, String s, boolean v_uni) {
			// ==============================================================================================
			bb = activeBb;
			type = typeV;
			qt = q_.q(type);
			uni = v_uni;

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

		GraInfo(BarBlock bbV, int numb_in) {
			// ==============================================================================================
			assert (bbV == activeBb);
			bb = activeBb;
			if (bb.size() == 0)
				bb.add("");

			type = bb.type;
			qt = q_.q(type);

			numb = numb_in;

			commonBit();
		}

		public int get_CapEnv__bv_etd() {
			// ==============================================================================================
			return capEnv.bv_etd;
		}

		public int get_CapEnv__bv_1st() {
			// ==============================================================================================
			return capEnv.bv_1st;
		}

		public int get_CapEnv__bv_cont() {
			// ==============================================================================================
			return capEnv.bv_cont;
		}

	}

	int LIN_STD_FONT_SIZE__smaller_than_min = 30;
	int LIN_MIN_FONT_SIZE__bigger_than_standard = 35;

	int fontBlockIndex = 0;

	/**
	 */
	class FontBlock {
		// ---------------------------------- CLASS -------------------------------------
		int index = 0;
		// boolean active = false;
		float linFontSize = LIN_STD_FONT_SIZE__smaller_than_min;
		String family = App.fontfamilyStandard;
		int bold = 0;
		boolean italic = false;
		boolean underline = false;
		Font font;

		FontBlock() { // Constructor
			// =============================================================================
			index = fontBlockIndex++;
			if (index == 0) { // 0 is built in default
				linFontSize = LIN_STD_FONT_SIZE__smaller_than_min;
			}

			if (index == mn_header_font_slot) { // font for 'mn' headers
				linFontSize = 56;
				bold = 5;
			}

			if (index == dfc_font_slot) { // dfc (lb z questions)
				linFontSize = 78;
			}

			fontMake(); // make it first just to help the poor punters
			// active = false;
		}

		public void fontMake() {
			// =============================================================================
			// System.out.println( " fontMake " + index);

//			active = true; // not really used these days

			if (index <= last_letter_font_slot || index == mn_header_font_slot) {
				if (App.useFamilyOverride && App.fontfamilyOverride.length() > 0) {
					font = new Font(App.fontfamilyOverride, 0, 0);
				}
				else if (family.contentEquals(App.fontfamilyStandard) && App.fontfamilyStandardAvailable == false) {
					font = BridgeFonts.bridgeTextStdFont;
				}
				else {
					font = new Font(family, 0, 0); //
				}
			}
			else if (index == dfc_font_slot) {
				font = BridgeFonts.faceAndSymbolFont;
			}

		}
	}

	// @formatter:off
	FontBlock fbAy[] = { 
			 new FontBlock(), new FontBlock(), new FontBlock(), new FontBlock(),
			 new FontBlock(), new FontBlock(), new FontBlock(), new FontBlock(),
			 new FontBlock(), new FontBlock(), new FontBlock(), new FontBlock(),
			 new FontBlock(), new FontBlock(), new FontBlock(), new FontBlock(),
			 new FontBlock(), new FontBlock(), new FontBlock(), new FontBlock(),
			 
			 new FontBlock(), new FontBlock(), new FontBlock(), new FontBlock(),
			 new FontBlock(), new FontBlock(), new FontBlock(), new FontBlock(),
			 new FontBlock(), new FontBlock(), new FontBlock(), new FontBlock(),
			 new FontBlock(), new FontBlock(), new FontBlock(), new FontBlock(),
			 new FontBlock(), new FontBlock(), new FontBlock(), new FontBlock(),

	};
	// @formatter:on

	static final int last_letter_font_slot = 35; // 0-9, a-z => 0 - 35
	static final int mn_header_font_slot = last_letter_font_slot + 1;
	static final int dfc_font_slot = last_letter_font_slot + 2;

	/**
	 */
	int firstDigitBaseThirtySix(String ns) {
		// =============================================================================
		if (ns.isEmpty())
			return -1;

		char c = ns.charAt(0);

		if ('0' <= c && c <= '9')
			return c - '0';

		if ('a' <= c && c <= 'z')
			return c - 'a' + 10;

		if ('A' <= c && c <= 'Z')
			return c - 'A' + 10;

		return -1;
	}

	/**
	 */
	public void pdl__lg(BarBlock bb) { // line gap
		// =============================================================================
		int val = firstDigitBaseThirtySix(bb.get(0).trim());

		if (val == -1)
			val = 10; // 10 = 'a' => 100%, 9 => 90%, b => 110%

		new GraInfo(bb, val);
	}

	/**
	 */
	void insertFontHeight(String ns) {
		// =============================================================================
		if (ns.length() < 2)
			return;

		int slot = firstDigitBaseThirtySix(ns);
		if (slot < 1)
			return;

//		if (fbAy[slot].active)
//			return; // Thou shalt not mess with a 'made' font

		int val = Aaa.extractPositiveInt(ns.substring(1));

//		int minSize = LIN_MIN_FONT_SIZE__bigger_than_standard;
		int standardSize = LIN_STD_FONT_SIZE__smaller_than_min;

		if (val > 120) {
			val = 120;
		}

		if (slot <= 4) {

//			if (val < minSize) {
//				val = minSize;
//			}
			if (val < 30) {
				val = (val * 7) / 5;
				if (val > 30) {
					val = 30;
				}
			}
			// try to mimic netbridgevu strange font sizes
			if ((30 <= val && val <= 35)) {
				val = standardSize; // 30 i.e. SMALLER than min
			}
			else if ((36 <= val && val <= 39)) {
				val = standardSize + (val - 35) * 2;
			}
		}

		fbAy[slot].linFontSize = val;
		fbAy[slot].fontMake();
	}

	/**
	 */
	void insertFontFamily(String ns) {
		// =============================================================================
		if (ns.length() < 2)
			return;

		int slot = firstDigitBaseThirtySix(ns);
		if (slot < 1)
			return;

//		if (fbAy[slot].active)
//			return; // Thou shalt not mess with a 'made' font

		fbAy[slot].family = ns.substring(1);
		fbAy[slot].fontMake();
	}

	/**
	 */
	void insertFontBold(String ns) {
		// =============================================================================
		if (ns.length() < 1)
			return;
		if (ns.length() == 1)
			ns += '0';

		if (ns.charAt(1) < '0' || ns.charAt(1) > '9')
			ns = ns.charAt(0) + "9"; // so y etc converts to 9 (bold)

		int slot = firstDigitBaseThirtySix(ns);
		if (slot < 1)
			return;

		int val = ns.charAt(1) - '0'; // ignore the third char (if any)
		if (val < 0 || val > 9) // later we have a boolean cutoff <= 4 and 5 >=
			return;

//		if (fbAy[slot].active)
//			return; // Thou shalt not mess with a 'made' font

		fbAy[slot].bold = val;
		fbAy[slot].fontMake();
	}

	/**
	 */
	void insertFontItalic(String ns) {
		// =============================================================================
		if (ns.length() < 1)
			return;

		int slot = firstDigitBaseThirtySix(ns);
		if (slot < 1)
			return;

//		if (fbAy[slot].active)
//			return; // Thou shalt not mess with a 'made' font

		fbAy[slot].italic = true;
		fbAy[slot].fontMake();
	}

	/**
	 */
	void insertFontUnderline(String ns) {
		// =============================================================================
		if (ns.length() < 1)
			return;
		if (ns.length() == 1)
			ns += '0';

		int slot = firstDigitBaseThirtySix(ns);
		if (slot < 1)
			return;

//		if (fbAy[slot].active)
//			return; // Thou shalt not mess with a 'made' font

		fbAy[slot].underline = true;
		fbAy[slot].fontMake();
	}

	/**
	 *  now happens automatically every time a param is changed
	 */
	void insertFontMake(String ns) {
		// =============================================================================
//		if (ns.isEmpty())
//			return;
//
//		int slot = firstDigitBaseThirtySix(ns);
//		if (slot < 1)
//			return;
//
//		// if (fbAy[slot].active)
//		//  	return; // Thou shalt not mess with a 'made' font
//
//		fbAy[slot].fontMake();
	}

	/**
	 */
	void setFont_fp(String ns) {
		// =============================================================================
		if (ns.isEmpty())
			ns = "0";

		int slot = firstDigitBaseThirtySix(ns);
		if (slot < 0) // this is the only time you are allowed reference slot 0
			return;

		capEnv.font_slot_fp = slot;
		capEnv.bold = false;
	}

	public boolean isEndABiddingQuestion() {
		// =============================================================================
		GraInfo gi = giAy.get(stop_gi);
		return (gi.qt == q_.lb && gi.bb.get(0).toLowerCase().startsWith("b"));
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

		int n = alphaToRowColNumb(c, -1);

		if (n > -1)
			new GraInfo("ht", n);
	}

	/**
	 */
	void process_tu(BarBlock bb) {
		// =============================================================================
		String s = bb.get(0);
		capEnv.mn_show_tu = (s.length() > 0);
		if (capEnv.mn_pg_countDown != -1)
			capEnv.mn_pg_countDown = (s.length() > 0) ? -1 : 2;
		new GraInfo("tu", s);
	}

	/**
	 */
	void process_fg(BarBlock bb) {
		// =============================================================================
		String s = bb.get(0);
		capEnv.gray_fade = (s.toLowerCase().startsWith("y"));
		new GraInfo("fg", s);
	}

	/**
	 */
	public void pdl__tt(BarBlock bb) { // tidy trick
		// =============================================================================
		String s = bb.get(0);
		capEnv.tidyTrick = (s.toLowerCase().startsWith("y"));
//		new GraInfo("tt", s);
	}

	/**
	 */
	void process_st(BarBlock bb) {
		// =============================================================================
		String s = bb.get(0);
		capEnv.visualModeRequested = App.Vm_DealAndTutorial;
		App.deal.clearAllBubbleText();
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
		gi.text = bb.get(0).trim();

		if (gi.text.isEmpty()) {
			if (bb.size() > 1)
				bb.set(1, "thin");
			else
				bb.add("thin");
		}

		if (lin.linType != Lin.VuGraph) {
			App.deal.qx_number = Aaa.extractPositiveIntOrZero(gi.text);
			return; // only Vugraph has "open" and "closed" rooms
		}

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
				setTheReadPoints(i, false /* not used */);
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

		setTheReadPoints(stop, false /* not used */);
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
				setTheReadPoints(i, false /* not used */);
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

		setTheReadPoints(giAy.size() - 1, false /* not used */);
	}

	/**
	 */
	public void refresh_for_youseat_change() {
		// =============================================================================
		if (stop_gi >= giAy.size() - 1)
			return;
		tutorialStepFwd();
		tutorialBackOne();
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
		setTheReadPoints(gi_index, false /* not used */);
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
	 * @param stepIfBidding 
	 */
	public void setTheReadPoints_FwdMini(boolean stepIfBidding) {
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

			if (t == q_.pc || (t == q_.mb && !stepIfBidding)) { // for now just these two
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

		setTheReadPoints(stop_gi_candidate, false /* not used */); // <==== i ====

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
			setTheReadPoints(stop_gi, false /* not used */); // The forced redisplay
			fh_state = Fh_NONE;
		}
	});

	/**
	 */
	public void setTheReadPoints(int stop, boolean not_used) {
		// =============================================================================

		if (stop < 0)
			stop = 0;

		if (stop > giAy.size())
			stop = giAy.size() - 1;

		// assert (stop >= 0 && stop < giAy.size());

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
//		for (int i = start_nt; i < end_pg; i++) {
		for (int i = end_pg - 1; i >= start_nt; i--) {
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
				t = bb.get(2).charAt(0); // nasty

			App.tup.addQp(); // qp is a floater
			App.tup.matchToQuestion(c, t);

			fh_state = Fh_ADDED;

			if (c == 'b' || c == 'm' || c == 'y') {
				// nothing else - anything else (if needed) is done in the question panels
			}

			else if (c == 'z') {
				App.flowOnlyCommandBar = true; // was App.lbx_modeExam;
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
				App.mg.deal.fillDealExternal(handAy, Deal.noFill, 0); // South is assumed
				App.tup.qp.hdp1.dealMajorChange(App.mg.deal);
			}

			else if (c == 'h') {
				ArrayList<String> handAy1 = new ArrayList<String>();
				handAy1.add(bb.getSafe(5));
				App.tup.qp.deal1 = new Deal(0);
				App.tup.qp.deal1.fillDealExternal(handAy1, Deal.noFill, 0); // South is assumed
				App.tup.qp.hdp1.dealMajorChange(App.tup.qp.deal1);

				ArrayList<String> handAy2 = new ArrayList<String>();
				handAy2.add(bb.getSafe(6));
				App.tup.qp.deal2 = new Deal(0);
				App.tup.qp.deal2.fillDealExternal(handAy2, Deal.noFill, 0); // South is assumed
				App.tup.qp.hdp2.dealMajorChange(App.tup.qp.deal2);
			}

			else if (c == 'p' || c == 't') {
				ArrayList<String> handAy = new ArrayList<String>();
				handAy.add(bb.getSafe(5));

				App.tup.qp.deal1 = new Deal(0);
				App.tup.qp.deal1.fillDealExternal(handAy, Deal.noFill, 0); // South is assumed
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

		GraInfo gi_end_pg = giAy.get(end_pg);
		if (gi_end_pg.deal == null) {
			@SuppressWarnings("unused")
			int z = 0;
		}

		assert (gi_end_pg.deal != null);
		App.deal = gi_end_pg.deal.deepClone();

		App.tutorialShowAuction = giStop.capEnv.pdl_auctionVisible; // don't do this - && (App.deal != null) && App.deal.didAnyHandStartWith13Cards(); /* i.e.
																	// has 52 cards */

		if (App.mg.lin.linType == Lin.VuGraph) {
			if (App.forceYouSeatToSouthZone) {
				App.deal.youSeatHint = Dir.directionFromInt(App.compassAllTwister).rotate(Dir.South);
			}
			else
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

		App.history.histRecordChange("pg_change");

		// System.out.println("Set Read Points - gi indexes - start, prev, stop = " + start_nt + "  " + middle_pg + " " + stop_gi);
	}

	/**
	 */
	public Capture_gi_env getStopCapEnv() {
		// =============================================================================
		GraInfo gi = giAy.get(stop_gi);

		return gi.capEnv;
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
			tutorialFlowFwd(false);
		}
	});

	/**
	 */
	public void tutorialFlowFwd(boolean stepIfBidding) {
		// =============================================================================
		tutorialPlayTimer.stop(); // in case it is running

		if (stop_gi >= giAy.size() - 1) {
			Controller.loadBookChapter_next();
			return;
		}

		setTheReadPoints_FwdMini(stepIfBidding);
	}

	/**
	 */
	public void send_at() {
		// =============================================================================
		if (outBuf.length() == 0)
			return;
		new GraInfo("at");
	}

	static final String sixCharZeroes = (char) (0) + "" + (char) (0) + "" + (char) (0) + "" + (char) (0) + "" + (char) (0) + "" + (char) (0);

//	int md_count = 0;

	/**
	 */
	public void parse_original_mn(BarBlock bb) {
		// =============================================================================
		assert (bb.qt == q_.mn);

		int lines = bb.size();
		String text = bb.get(0) + (lines > 1 ? bb.get(1) : "");

		if (lines > 2) {
			text = "";
			lines = 0; // cos BBO only allows a max of one newline in an 'mn' header
		}

		// set capEnv values
		capEnv.mn_lines = lines;
		capEnv.mn_text = text;

		if (capEnv.mn_pg_countDown >= 0 && capEnv.mn_show_tu) {
			capEnv.mn_pg_countDown = 2;
		}

		new GraInfo("mn");
	}

	/**
	 */
	public void parse_original_nt(BarBlock bb) {
		// =============================================================================
		if (xx_next_at_command) {
			xx_next_at_command = false;
			bb.qt = q_.xx;
			bb.type = "xx";
			return;
		}
		capEnv.reset_for_nt();
		new GraInfo(bb);
		parse_original_at(bb); // in case the user has added any text
	}

	/**
	 */
	public void parse_original_at(BarBlock bb) {
		// =============================================================================

//		int size_of_last_text_sent = 0;

		assert (bb.qt == q_.at || bb.qt == q_.nt);

		if (xx_next_at_command) {
			xx_next_at_command = false;
			bb.qt = q_.xx;
			bb.type = "xx";
			return;
		}

		if (bb.uni) {
			send_at(); // 'finsihes' anything in the buffer
			for (int z = 0; z < bb.size(); z++) { // each fragment should go on its own line
				if (z > 0) {
//					if (capEnv.boxed && size_of_last_text_sent == 0) { // first send a space
//						// outBuf.append(' ');
//						// send_at();
//					}
					new GraInfo("NL"); // multi part 'at' only exist to have new line between each part
				}

				String utext = bb.get(z);
				if (!utext.isEmpty()) {
					new GraInfo("at", utext, bb.uni);
				}
			}
			return;
		}

		for (int z = 0; z < bb.size(); z++) { // each fragment should go on its own line
			if (z > 0) {
				new GraInfo("NL"); // multi part 'at' only exist to have new line between each part
			}

			String text = bb.get(z) + sixCharZeroes; // so we can always peek ahead

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
						if (i < len_of_at_section && text.charAt(i) == ' ')
							i++; // just eat one space
//						while (i < len_of_at_section && text.charAt(i) == ' ')
//							i++; // eat all spaces
						continue;
					}

					if (c1 == '$') {
						int v = 0;
						while (true) {
							char cn = text.charAt(i);
							if ('0' <= cn && cn <= '9') {
								i++; // eat the char
								v = v * 10 + (cn - '0');
							}
							else {
								if (v == 40)
									outBuf.append("Flow");
								else if (v == 6)
									outBuf.append("Undo");
								else if (v > 0)
									outBuf.append("Navbar");
								break;
							}
						}
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
						if (c2 == 'g') { // h = hyperlink g = INTERNAL hyperlink f = internally defined function
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
						if (c2 == 'h' || c2 == 'a') { // h = hyperlink g = INTERNAL hyperlink f = internaly defined funtion a == Audio like H (from Hondo)
							send_at();
							capEnv.bold = true; // links are 'auto bold'
							StringBuilder url = new StringBuilder(0);
							char c;
							// @formatter:off
							int k;
							boolean comma_found = false;
							for (k = i; k < text.length() - 3; k++) { // uggly scan ahead
								c = text.charAt(k);
								if (c == ',') {
									comma_found = true;
									break;
								}
								else if (    (c == '^') 
									      && (text.charAt(k+1) == '*') 
									      && (text.charAt(k+2) == 'n' ||  text.charAt(k+2) == 'N')  )
								{
									break;
								} 
								else
									url.append(c);
							}
							// @formatter:on
							capEnv.hyperlink = new Hyperlink_h_ext(url.toString());
							new GraInfo("YB");

							if (comma_found) {
								i = k + 1;
								int m;
								for (m = k + 1; m < text.length() - 3; m++) { // uggly scan ahead
									if ((c = text.charAt(m)) == '^' && (text.charAt(m + 1) == '*') && (text.charAt(m + 2) == 'n' || text.charAt(m + 2) == 'N')) {
										break;
									}
									else
										url.append(c);
								}
							}

							boolean xw_marker_found = false;
							int from = k + (comma_found ? 1 : 3);
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
						// NOW treat all as general close if (c2 == 'n' || c2 == 'x' || c2 == 'w')
						{ // close of hyperlinks and bolding underline etc
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

					if (c1 == 'z') {
						send_at();
						new GraInfo("@Z");
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
//			size_of_last_text_sent = outBuf.length();

			send_at(); // send anything in the output buffer

			if (abort_after_next_at_command) {
				abort_after_next_at_command = false;
				App.rqb_earlyEndMassGi = true;
			}
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
		gi.deal_ih_ia.fillDealExternal(bb, Deal.noFill, bb.lineNumber);

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

		capEnv.page_numb_display = page_numb_display++;

		if ((gi.qt == q_.lb && gi.bb.size() == 5) && (gi.bb.get(0).length() == 1) && (gi.bb.get(0).charAt(0) == '*') && (gi.bb.get(1).length() == 1)
				&& (gi.bb.get(1).contentEquals("m"))) {
			String s = gi.bb.get(3);
			s = s.replace("&&", "&"); // cos that is what other lin file players do
//			s = s.replace("@", ""); // better than nothing clean up of the four suits we can't do the symbols
			gi.bb.set(3, s);
		}

		if (gi.bb.get(1).contentEquals("z")) { // z => Dirtribution training and exam question
			App.deal.dfcDeal = true;
			App.deal.changed = true;
			// capEnv.page_numb_display = -1; // we dont want this for Z questions (does not do the job)

			String stage = gi.bb.get(2);

			// convert to training (if should be)
			if (stage.contentEquals("1") && (App.lbx_modeExam == false)) {
				gi.bb.set(2, "0");
				stage = "0";
			}

			gi.bb.add("0~1~2~3~4~5~6~7");

			if (stage.contentEquals("0")) {
				App.deal.fillDealDistribution_0_Training(App.dfcTrainingSuitSort);
				App.dfc_earlyEndMassGi = true; // end processing of lin bb's
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
			if (capEnv.mn_pg_countDown == 0) {
				capEnv.mn_pg_countDown = -1;
				capEnv.mn_show_tu = false;
			}
		}

		capEnv.page_numb_display = page_numb_display++;

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

		pc_autoClear__buildTime__tc_suppress_pc_display = false;
		buildTime__tc_suppress_pc_display = false;
		App.deal.tc_suppress_pc_display = false;
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
	public void pdl__md(BarBlock bb) { // Make Deal
		// =============================================================================
		App.deal.changed = true;

		if (bb.get(0).toLowerCase().startsWith("0") == false) {

			capEnv.tut_rotation = 0;

			// a zero is an amendment to the hand
			// so we want the non-zero ie 'new hands'
			App.deal.forceDifferent++;
			App.deal.endedWithClaim = false;
			App.deal.eb_blocker = false;
			App.deal.eb_min_card = 0;
			App.deal.clearAnyKeptCards();
		}

		App.deal.fillDealExternal(bb, Deal.yesFill, bb.lineNumber);
		new GraInfo(bb);
	}

	/**
	 */
	public void pdl__kc(BarBlock bb) { // Keep Cards
		// =============================================================================
		App.deal.markCardsKept(bb);

		GraInfo gi = new GraInfo(bb);

		new GraInfo(bb);

		prev_clone = App.deal;
		gi.deal = App.deal;
		App.deal = gi.deal.deepClone();
		App.deal.localId = Deal.idCounter.getAndIncrement();
	}

	/**
	 */
	public void pdl__rc(BarBlock bb) { // Remove Cards
		// =============================================================================
		App.deal.changed = true;
		App.deal.endedWithClaim = false;
		App.deal.eb_blocker = false;

		App.deal.removeCards(bb);

		GraInfo gi = new GraInfo(bb);

		// added feb 2016 umm - so rc now works in same 'pg' as an md|0...|
		prev_clone = App.deal;
		gi.deal = App.deal;
		App.deal = gi.deal.deepClone();
		App.deal.localId = Deal.idCounter.getAndIncrement();
	}

	/**
	 */
	public void pdl__bv(BarBlock bb) { // button vivibility
		// =============================================================================
		char btn = ((bb.get(0) + "-").toLowerCase()).charAt(0);

		int state = 2;
		if (bb.size() > 1) {
			char c = ((bb.get(1) + "-").toLowerCase()).charAt(0);
			if (c == 'h')
				state = 0;
			if (c == 'v')
				state = 1;
		}

		if /* */(btn == 'e') {
			MassGi.this.capEnv.bv_etd = state;
		}
		else if (btn == 'f') {
			MassGi.this.capEnv.bv_1st = state;
		}
		else if (btn == 'c') {
			MassGi.this.capEnv.bv_cont = state;
		}
		else if (btn == 'a') {
			MassGi.this.capEnv.bv_etd = state;
			MassGi.this.capEnv.bv_1st = state;
			MassGi.this.capEnv.bv_cont = state;
		}
		else {
			MassGi.this.capEnv.bv_etd = 2; // no override
			MassGi.this.capEnv.bv_1st = 2; // no override
			MassGi.this.capEnv.bv_cont = 2; // no override
		}

		// int x = 0;
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
	public void pdl__px(BarBlock bb) { // Player Names info suppress
		// =============================================================================
		String s = bb.get(0);
		capEnv.playerNameNumbsVisible = (!s.trim().toLowerCase().startsWith("n"));
	}

	/**
	 */
	public void pdl__sj(BarBlock bb) { // Seat Kibitz
		// =============================================================================
		//
		App.deal.changed = true;
		String s = bb.get(0);

		App.deal.adjustYouSeatHint(s);
		// Note apEnv.pdl_allSeatsVisible is left untouched

		// GraInfo gi = new GraInfo(bb);
		// System.out.println("gi.index :" + gi.index + " s: " + s + "-");
	}

	/**
	 */
	public void pdl__sk(BarBlock bb) { // Seat Kibitz
		// =============================================================================
		// in theory multiple values can be supplied (we do not support this)
		// and none means hide none
		// we are going to make the first letter of the list the you seat
		App.deal.changed = true;
		String s = bb.get(0);
		if (s.length() > 0)
			App.deal.setYouSeatHint(s);
		capEnv.pdl_allSeatsVisible = s.isEmpty() || (s.length() > 0) && (s.charAt(0) == 'y' || s.charAt(0) == 'Y');
		// GraInfo gi = new GraInfo(bb);
		// System.out.println("gi.index :" + gi.index + " s: " + s + "-");
	}

	/**
	 */
	public void pdl__eb(BarBlock bb) {
		// =============================================================================
		// System.out.println("p2 p2 p2 p2 p2 - eb  " + bb.getSafe(0) + " <");
		char c = (bb.get(0) + ' ').toLowerCase().charAt(0);

		if (c == 'c') {
			// very special case
			App.deal.forceDifferent++;
		}
		else {
			// normal eb stuff
			App.deal.changed = true;
			App.deal.eb_min_card = 0;
			App.deal.eb_blocker = (c == 'y');
			if (App.deal.eb_blocker) {
				App.deal.eb_min_card = App.deal.countCardsPlayed();
			}
		}
	}

	/**
	 */
	public void pdl__xs(BarBlock bb) {
		// =============================================================================
		App.deal.setDealShowXes(bb);
		App.deal.changed = true;
	}

	/**
	 */
	public void pdl__rt(BarBlock bb) {
		// =============================================================================
		int r = (bb.get(0) + " ").charAt(0) - '0';
		capEnv.tut_rotation = ((0 <= r) && (r <= 3)) ? r : 0;
		// System.out.println( capEnv.tut_rotation);
		App.deal.changed = true;
	}

	/**
	 */
	public void big_sml_extract(BarBlock bb, int v[]) {
		// =============================================================================
		// System.out.println("p2 p2 p2 p2 p2 - eb  " + bb.getSafe(0) + " <");

		int big = -1, sml = -1;

		String s = bb.get(0).trim().toLowerCase();
		if (s.isEmpty()) {
			v[0] = big;
			v[1] = sml;
			return;
		}

		char c0 = s.charAt(0);
		char c1 = 0;

		if (s.length() > 1) {

			c1 = s.charAt(1);

			if ('0' <= c1 && c1 <= '9') {
				sml = c1 - '0';
			}
			else if ('a' <= c1 && c1 <= 'z') {
				sml = c1 - 'a' + 10;
			}

			if (sml != -1) {
				if ('a' <= c0 && c0 <= 'z') {
					big = c0 - 'a';
					// success on both
				}
				else {
					// c0 not valid so we scrub what we have
					sml = -1;
				}
			}
		}

		if (sml == -1) {
			if ('0' <= c0 && c0 <= '9') {
				sml = c0 - '0';
			}
			else if ('a' <= c0 && c0 <= 'z') {
				sml = c0 - 'a' + 10;
			}
		}

		v[0] = big;
		v[1] = sml;
	}

	/**
	 */
	public void pdl__nD(BarBlock bb) {
		// =============================================================================

		int v[] = new int[2]; // big in 0 sml in 1

		big_sml_extract(bb, v);

		if (v[0] > -1)
			new GraInfo("hT", v[0]); // big capital means suppress wipe of xPos

		if (v[1] > 0)
			new GraInfo("n#", v[1]); // sml
	}

	/**
	 */
	public void pdl__nU(BarBlock bb) {
		// =============================================================================

		int v[] = new int[2]; // big in 0 sml in 1

		big_sml_extract(bb, v);

		if (v[0] > -1)
			new GraInfo("hT", v[0]); // big capital means suppress wipe of xPos

		if (v[1] > 0)
			new GraInfo("n#", -v[1]); // sml - for Up
	}

	/**
	 */
	public void pdl__nR(BarBlock bb) {
		// =============================================================================

		int v[] = new int[2]; // big in 0 sml in 1

		big_sml_extract(bb, v);

		if (v[0] > -1)
			new GraInfo("VT", v[0]); // big capital means suppress wipe of xPos

		if (v[1] > 0)
			new GraInfo("n>", v[1]); // sml
	}

	/**
	 */
	public void pdl__nL(BarBlock bb) {
		// =============================================================================

		int v[] = new int[2]; // big in 0 sml in 1

		big_sml_extract(bb, v);

		if (v[0] > -1)
			new GraInfo("VT", v[0]);

		if (v[1] > 0)
			new GraInfo("n>", -v[1]); // sml - for Left
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
	public void pdl__an(BarBlock bb) { // anouncement
		// =============================================================================
		App.deal.changed = true;
		App.deal.addAnouncementToLastBid(bb.get(0));
		new GraInfo(bb);
	}

	/**
	 */
	public void pdl__rq(BarBlock bb) { // reqire build number
		// =============================================================================
		int required = Aaa.extractPositiveInt(bb.get(0));
		if (required > VersionAndBuilt.buildNo) {
			abort_after_next_at_command = true;
		}
		else {
			xx_next_at_command = true;
		}
		new GraInfo(bb);
	}

	/**
	 */
	public void pdl__rh(BarBlock bb) { // remove all header parts
		// =============================================================================
		App.deal.changed = true;
		App.deal.ahHeader = "";
		App.deal.displayBoardId = "";
		App.deal.signfBoardId = "";
		new GraInfo(bb);
	}

	private final static String[] brdSignfAy = { "board", "brd", "hand", "deal", "example", "ex", "student", "study", "stud", "practice", "practise", "prac",
			"teaching", "teach", "numb", "number", "variation", "var", "end", "block", "set", "group", "case", "problem", "prob", "solution", "sol", "item",
			"set", "body", "table", "open", "closed", "extra", "xtra", "page", "pg", "book", "volume", "vol", "answer", "ans", "core", "pos", "game", "roger" };

	/**
	 */
	public void pdl__ah(BarBlock bb) { // add to header
		// =============================================================================
		App.deal.changed = true;

		boolean stillNeedToAdd = true;

//		if (App.deal.ahHeader.trim().isEmpty()) {
//			App.deal.signfBoardId = "";
//		}

//		if (App.deal.signfBoardId.isEmpty()) {

		String h = bb.get(0).toLowerCase();

		int from = -1;
		int to = -1;
		for (String signf : brdSignfAy) {
			from = h.indexOf(signf);
			if (from > -1) {
				to = from + signf.length();
				h = bb.get(0) + "  "; // this time we have kept the case
				App.deal.signfBoardId = h.substring(from, to);

				h = h.substring(to).trim() + " ";

				App.deal.displayBoardId = h.substring(0, h.indexOf(' '));

				stillNeedToAdd = false;
				break;
			}
		}
//		}

		if (stillNeedToAdd) {
			App.deal.ahHeader += bb.get(0) + " ";
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
	public void pdl__wt(BarBlock bb) { // whose trick ?
		// =============================================================================
		App.deal.changed = true;
		App.deal.setLastTrickWinnerExternal(bb.get(0));
		new GraInfo(bb);
	}

	/**
	 */
	public void pdl__aa(BarBlock bb) { // c = suppress updates other eg. (r) restore?
		// =============================================================================
		char v = (bb.get(0) + 'n').toLowerCase().charAt(0); // so the default if empty is n
		boolean old = autoAdd_missing_playedCards;
		autoAdd_missing_playedCards = (v == 'y'); // y = yes

		if ((old != /* new */autoAdd_missing_playedCards == false)) {
			App.deal.changed = true;
		}
	}

	/**
	 */
	public void pdl__up(BarBlock bb) { // undo (last) play(s)
		// =============================================================================
		App.deal.changed = true;
		App.deal.undoLastPlays_ignoreTooMany(Aaa.parseIntWithFallback(bb.get(0), 1));
		App.deal.endedWithClaim = false;
		App.deal.tricksClaimed = 0;
		App.deal.tc_suppress_pc_display = true;
		pc_autoClear__buildTime__tc_suppress_pc_display = true;
		new GraInfo(bb);
	}

	/**
	 */
	public void pdl__tc(BarBlock bb) { // c = suppress updates other eg. (r) restore?
		// =============================================================================
		char v = (bb.get(0) + 'r').toLowerCase().charAt(0); // so the default if empty is r

		buildTime__tc_suppress_pc_display = ((v == 'c') || (v == 'y')); // c/y = conceal ? r = reveal ?

		App.deal.changed = true;

		if (buildTime__tc_suppress_pc_display == false) {
			pc_autoClear__buildTime__tc_suppress_pc_display = true;
		}
		else {
			pc_autoClear__buildTime__tc_suppress_pc_display = false;
		}

	}

	/**
	 */
	public void pdl__pc(BarBlock bb) { // Play card
		// =============================================================================
		App.deal.changed = true;
		String cds = bb.get(0);

		String bbinf = "Line " + bb.lineNumber + "  pg " + page_numb_display + "   ";

		// System.out.println(bbinf + "pdl__pc arrive: " + cds);

		// PC|qd3| causes NetBridgeVu to play the lowest diamond
		// when neither the q or 3 are present in the hand
		// we need to mimic this PC|d| plays the lowest diamond

//		App.deal.tc_suppress_pc_display = buildTime__tc_suppress_pc_display;
		if (buildTime__tc_suppress_pc_display) { // so we need to stop the view time display
			bb.type = "xx";
			bb.qt = q_.xx;
		}

		if (pc_autoClear__buildTime__tc_suppress_pc_display) {
			App.deal.tc_suppress_pc_display = buildTime__tc_suppress_pc_display;
			pc_autoClear__buildTime__tc_suppress_pc_display = false;
			buildTime__tc_suppress_pc_display = false;
		}

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
					System.out.println(bbinf + "pdl__pc - Invalid Rank: " + cds + " " + suit + " " + c);
					continue;
				}
			}

			if (rank != Rank.BelowAll) {
				if (App.deal.checkCardExternal(suit, rank) == false) {
					if (autoAdd_missing_playedCards == false) {
						if (!lowestForced)
							System.out.println(bbinf + "pdl__pc - Card played not in hand!: " + suit + " " + rank + "  will try to play lowest");
						rank = Rank.BelowAll; // so it will try the lowest
					}
					else {
						/* we know the card is not in the current hand 
						   we will now try to add that card to the hand (but only if it is not yet played)
						 */
						Card cardX = App.deal.packPristine.getIfRankAndSuitExists(rank, suit);
						if (cardX != null) {
							Hand hand = App.deal.getNextHandToPlay();
							App.deal.packPristine.remove(cardX);
							hand.fOrgs[suit.v].addDeltCard(cardX);
							hand.frags[suit.v].addDeltCard(cardX);
						}
					}
				}
			}

			if (rank == Rank.BelowAll) {
				Card card2 = App.deal.getLowestCardExternal(suit);
				if (card2 == null) {
					System.out.println(bbinf + "pdl__pc - No lowest card in suit: " + cds + " " + suit);
					suit = Suit.Invalid;
					continue;
				}
				rank = card2.rank;
				// System.out.println(bbinf + "pdl__pc - 'Below all' selected: " + suit + " " + rank);
			}

			if (App.deal.checkCardExternal(suit, rank) == false) {
				System.out.println(bbinf + "pdl__pc - Card not found: " + cds + " " + rank + " " + suit);
				continue;
			}
			String oCard = suit.toLinStr() + rank.toStr();
			App.deal.playLinCard(oCard);

			new GraInfo(activeBb); // clones the deal

			suit = Suit.Invalid; // so we will get the next

			// System.out.println(bbinf + "pdl__pc depart: " + oCard);
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
		 * it adds to the GraInfo array  (automatically in the called functions)
		 * 
		 * First we add a few that will reset when re return to start
		 */
		App.deal = new Deal(0); // real index will also be zero
		App.deal.setDealer(Dir.South);
		// App.dealMajorChange();

		new GraInfo(activeBb = lin.new BarBlock("AA", 0)); // a null one to be the first
		new GraInfo(activeBb = lin.new BarBlock("qx", 0)); // qx
		activeBb.set(0, Aaf.navbar_start);
		activeBb.add("wide");
		new GraInfo(activeBb = lin.new BarBlock("st", 0)); // st standard table - the lin spec default
		new GraInfo(activeBb = lin.new BarBlock("nt", 0)); // nt

		int bbCount = lin.bbAy.size();

		App.dfc_earlyEndMassGi = false;

		for (int i = 0; i < bbCount; i++) {
			if (App.dfc_earlyEndMassGi)
				break;

			if (App.rqb_earlyEndMassGi) {
				break;
			}

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
			if (t == q_.at)  { parse_original_at(bb);         	continue; }
			if (t == q_.nt)  { parse_original_nt(bb);         	continue; }
			
			if (t == q_.fp)  { setFont_fp(s.trim());          	continue; }
			if (t == q_.cp)  { setColor_cp(s.trim());           continue; }
			if (t == q_.cs)  { setColor_cs(s.trim());           continue; }		
			
			if (t == q_.ht)  { process_ht(bb);               	continue; }
			if (t == q_.tu)  { process_tu(bb);               	continue; }
			
			if (t == q_.bt)  { process_bt(bb);                	continue; }
			if (t == q_.st)  { process_st(bb);                	continue; }
			if (t == q_.qx)  { process_qx(bb);                	continue; }
			if (t == q_.fg)  { process_fg(bb);                	continue; }

			if (t == q_.lb)  { pdlx__lb(bb);                 	continue; }
			if (t == q_.pg)  { pdlx__pg(bb);                 	continue; }
			if (t == q_.sb)  { pdl__sb(bb);                 	continue; }
			if (t == q_.pn)  { pdl__pn(bb);                 	continue; }
			if (t == q_.pi)  { pdl__px(bb);                 	continue; } // handPanelNameAreaInfoNumbersShow
			if (t == q_.sk)  { pdl__sk(bb);                 	continue; }
			if (t == q_.sj)  { pdl__sj(bb);                 	continue; }
			if (t == q_.ha)  { pdl__ha(bb);                 	continue; }
			if (t == q_.md)  { pdl__md(bb);                 	continue; }
			if (t == q_.kc)  { pdl__kc(bb);                 	continue; }  // keep cards (from shuff op)
			if (t == q_.rc)  { pdl__rc(bb);                 	continue; }  // remove cards
			if (t == q_.mb)  { pdl__mb(bb);                 	continue; }
			if (t == q_.an)  { pdl__an(bb);                 	continue; }
			if (t == q_.up)  { pdl__up(bb);                 	continue; }
			if (t == q_.ub)  { pdl__ub(bb);                 	continue; }
			if (t == q_.lg)  { pdl__lg(bb);                 	continue; } // line gap (line separation)
			if (t == q_.mc)  { pdl__mc(bb);                 	continue; }
			if (t == q_.pc)  { pdl__pc(bb);                 	continue; }
			if (t == q_.aa)  { pdl__aa(bb);                 	continue; }
			if (t == q_.tc)  { pdl__tc(bb);                 	continue; } // show hide pc and bid? updates
			if (t == q_.sv)  { pdl__sv(bb);                 	continue; }
			if (t == q_.rh)  { pdl__rh(bb);                 	continue; }
			if (t == q_.tt)  { pdl__tt(bb);                 	continue; } // tidy trick
			if (t == q_.ah)  { pdl__ah(bb);                 	continue; }
			if (t == q_.rq)  { pdl__rq(bb);                 	continue; }
			if (t == q_.wt)  { pdl__wt(bb);                 	continue; }
			if (t == q_.ih)  { pdl__ih(bb);                 	continue; }
			if (t == q_.ia)  { pdl__ia(bb);                 	continue; }
			if (t == q_.eb)  { pdl__eb(bb);                 	continue; }
			if (t == q_.xs)  { pdl__xs(bb);                 	continue; } // show some cards as x'es
			if (t == q_.rt)  { pdl__rt(bb);                 	continue; } 
			
			if (t == q_.bv)  { pdl__bv(bb);                 	continue; } // button visibility 2874
			if (t == q_.nD)  { pdl__nD(bb);                 	continue; } // nudge Down  n#
			if (t == q_.nU)  { pdl__nU(bb);                 	continue; } // nudge Up    n^
			if (t == q_.nL)  { pdl__nL(bb);                 	continue; } // nudge Left  n<
			if (t == q_.nR)  { pdl__nR(bb);                 	continue; } // nudge Right n>

			if (t == q_.mn)  { parse_original_mn(bb);         	continue; }
			if (t == q_.hf)  { setQuestPosition_hf(s);       	continue; }
			
			if (t == q_.cr || 
				t == q_.cg || 
				t == q_.cb)  { insertColorPart(bb.type, s); 	continue; }
			if (t == q_.fh)  { insertFontHeight(s);         	continue; }
			if (t == q_.ff)  { insertFontFamily(s);         	continue; }
			if (t == q_.fb)  { insertFontBold(s);           	continue; }
			if (t == q_.fi)  { insertFontItalic(s);         	continue; }
			if (t == q_.fu)  { insertFontUnderline(s);      	continue; }
			if (t == q_.fm)  { insertFontMake(s);           	continue; }
				
			if (t == q_.pf)  { /* ignored */                	continue; } // invokes injector in the incomming pass
			if (t == q_.cq)  { /* ignored */     				continue; } // question color not supported
			if (t == q_.bg)  { /* ignored */                	continue; } // background color not supported
			if (t == q_.sc)  { /* ignored */                	continue; } // original vertical adjust 
			if (t == q_.va)  { /* ignored */                	continue; }
			if (t == q_.d3)  { /* ignored */                	continue; }
			if (t == q_.hc)  { /* ignored */                	continue; }
			if (t == q_.lc)  { /* ignored */                	continue; }
			if (t == q_.hs)  { /* ignored */                	continue; }
			if (t == q_.ls)  { /* ignored */                	continue; }
			if (t == q_.pw)  { /* ignored */                	continue; }
			if (t == q_.bn)  { /* ignored */                	continue; }
			if (t == q_.se)  { /* ignored */                	continue; }
			if (t == q_.bm)  { /* ignored */                	continue; }
			if (t == q_.ip)  { /* ignored */                	continue; }
			if (t == q_.wb)  { /* ignored */                	continue; }
			if (t == q_.hb)  { /* ignored */                	continue; }
			if (t == q_.xx)  { /* ignored */                	continue; }
			if (t == q_.vg)  { /* ignored */                	continue; }
			if (t == q_.vr)  { /* ignored */                	continue; } // treat  as virgin deal
			if (t == q_.rs)  { /* ignored */                	continue; }
			if (t == q_.mp)  { /* ignored */                	continue; }
			if (t == q_.lf)  { /* ignored */                	continue; }
			if (t == q_.sa)  { /* ignored */                	continue; }
 						
			System.out.println("line " + bb.lineNumber + "  Make_gi oneTimeParse - unknown bb type -" + bb.type + "- " + s);

			// @formatter:on
		}

		/**
		 *  add a pg on the end if there is not already one there 
		 */
		if (App.rqb_earlyEndMassGi) {
			App.rqb_earlyEndMassGi = false;
			pdlx__pg(activeBb = lin.new BarBlock("pg", 0));
		}
		else if ((lin.bbAy.get(lin.bbAy.size() - 1).qt != q_.pg) && (App.dfc_earlyEndMassGi == false)) {
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

	public void openWebPage(String url) {
		// =============================================================================
		(new Hyperlink_h_ext(url)).actionLink();
	}

	public int get_best_pg_number_for_history() {
		// =============================================================================

		// System.out.println( "get_best_pg_number_for_history - linType: " + App.mg.lin.linType);

		if (App.mg.lin.linType == Lin.FullMovie || App.mg.lin.linType == Lin.VuGraph) {
			return get_current_pg_number_display();
		}
		else {
			return 0;
		}
	}

	public int get_current_pg_number_display() {
		// =============================================================================

		int count = 0;
		int last = giAy.size();

		for (int i = 1; i < last; i++) {
			GraInfo gi = giAy.get(i);
			int t = gi.qt;
			if (t == q_.pg || t == q_.lb) { // the only official two stoppers
				count++;
			}
			if (i == end_pg)
				return count;
		}
		return last - 1;
	}

	public void jump_to_pg_number_display(int numb) {
		// =============================================================================
		int count = 0;

		int to = 999999999;

		for (int i = 0; i < giAy.size(); i++) {
			GraInfo gi = giAy.get(i);
			int t = gi.qt;
			if (t == q_.pg || t == q_.lb) { // the only official two stoppers
				count++;
				if (count == numb) {
					to = i;
					break;
				}
			}
		}

		setTheReadPoints(to, false /* not used */);
	}

	public int get_gi_numb_from_pg_number_display(int numb) {
		// =============================================================================
		int count = 0;

		for (int i = 0; i < giAy.size(); i++) {
			GraInfo gi = giAy.get(i);
			int t = gi.qt;
			if (t == q_.pg || t == q_.lb) { // the only official two stoppers
				count++;
				if (count == numb) {
					return i;
				}
			}
		}
		return 0;
	}

}
