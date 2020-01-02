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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.Aaf;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.controller.CmdHandler;
import com.rogerpf.aabridge.controller.q_;
import com.rogerpf.aabridge.igf.MassGi.GraInfo;
import com.rogerpf.aabridge.model.Bid;
import com.rogerpf.aabridge.model.Card;
import com.rogerpf.aabridge.model.Cc;
import com.rogerpf.aabridge.model.Deal;
import com.rogerpf.aabridge.model.Dir;
import com.rogerpf.aabridge.model.Frag;
import com.rogerpf.aabridge.model.Hand;
import com.rogerpf.aabridge.model.Suit;

/**   
 */
public class QuAnswerPanel extends ConsumePanel {

	private static final long serialVersionUID = 1L;

	// ---------------------------------- CLASS -------------------------------------

	/**
	 */
	QuAnswerPanel() { /* Constructor */
		// ============================================================================
		setOpaque(false);

	}

	public void paintComponent(Graphics g) {
		// ============================================================================
		super.paintComponent(g);

//		{
//			// this is just an attempt to highlight the exam button
//			g.setColor(Cc.RedStrong);
//			
//			float w = getWidth();
//			int fromx = 0; //(int)(w * 0.55);
//			int farx = (int)w; //(int)(w * 0.39);
//		
//			g.fillRect(fromx, 0, farx, getHeight());
//		}

		g2 = (Graphics2D) g;
		Aaa.commonGraphicsSettings(g2);

		mg = App.mg; // a class member, just for simple (less to write) access;

		GraInfo gi_o = App.mg.giAy.get(mg.stop_gi);

		if (gi_o.qt != q_.lb)
			return;

		GraInfo gi = mg.new GraInfo(gi_o); // make a copy

		frc = g2.getFontRenderContext(); // used to iterate through text

		getSize(wh); // the width and height of the panel now

		width = getParent().getParent().getWidth(); // (float) wh.width;
		height = (float) wh.height;

		scaleFrac = width / LIN_STANDARD_WIDTH;
		fontScaleFrac = FONT_SCALE_FRAC * scaleFrac;
		heightOfCurFontFrac = LINE_SEPARTATION_FRAC; // no mult this is a const // * scaleFrac;
		lineSpacing_multiplier = App.now_always_one();
		heightOfCurFont = 0; // set later by calc
		maxHeightOnCurLine = 0; // set later by calc
		nonFont_on_this_line = false;

		columnWidth = COLUMN_WIDTH_FRAC * width;
		// OLD WAY rowSpacing = ROW_HEIGHT_FRAC * width; // yes width - NEVER the 'two value' height
		// new way below
		// float linesOnPage = (App.visualMode == App.Vm_DealAndTutorial ? LIN_LINES_ON_PAGE_SMALL : LIN_LINES_ON_PAGE_STD);
		float linesOnPage = LIN_LINES_ON_PAGE_SMALL;
		rowSpacing = height / linesOnPage;

		topAdjust = 2 * TOP_ADJUST_FRAC * width; // yes width - NEVER the 'two value' height

		leftMargin = 1 /* LEFT_MARGIN_DEFAULT */ * columnWidth;
		rightMargin = 9 /* RIGHT_MARGIN_DEFAULT */ * columnWidth;
		minMarginSeparation = MIN_MARGIN_SERPARTATION * columnWidth;

		yRow = 0.0f + topAdjust;
		xCol = leftMargin;

		segs.clear(); // clear all from the last page display
		segs.add(new Seg(leftMargin, rightMargin)); // add our first new one WE MUST always have at least one seg

		/** 
		 * We examine the state of the 'gi' at   mg.stop_pg   as that is the state we want the screen 
		 * to be in when this  draw  is finished.
		 * 
		 * if it does not match then we set what we want, which will cause another draw
		 */

//		GraInfo giLast = mg.giAy.get(mg.stop_gi);
//		skip__mn_text = (giLast.capEnv.visualModeRequested != App.Vm_TutorialOnly); // ugg - WIDE AREA FLAG - ugg
//

//		gi.text = gi.bb.get(2);
//		gi.capEnv.centered = true;
//
//		consume_at__uni(gi); 

		if (gi.userAns.isEmpty())
			return;

		char c = gi.bb.get(1).charAt(0);

		switch (c) {
		// @formatter:off
			case 'b': ans_lb_b(gi); break;
			case 'c': ans_lb_c(gi); break;
			case 'h': ans_lb_h(gi); break;
			case 'm': ans_lb_m_and_y(gi, c); break;
			case 'p': ans_lb_ptlsd(gi, c); break;
			case 't': ans_lb_ptlsd(gi, c); break;
			case 'l': ans_lb_ptlsd(gi, c); break;
			case 's': ans_lb_ptlsd(gi, c); break;
			case 'd': ans_lb_ptlsd(gi, c); break;
			case 'y': ans_lb_m_and_y(gi, c); break;
			case 'z': ans_lb_z(gi); break;
			default:  ans_lb_unimp(gi); break;
			// @formatter:on
		}

		drawAllSegs();
	}

	/**
	 */
	public void ans_lb_b(GraInfo gi) {
		// =============================================================================

		leftMargin = 0f * columnWidth;
		rightMargin = 9f * columnWidth;

		yRow = 5 /* rowConstA */ * rowSpacing + topAdjust;
		xCol = leftMargin;

		setCurSegEol();
		startNewSeg();
		gi.capEnv.centered = false;

		String trueAnsStr[] = gi.bb.get(4).split(",");

		Bid trueAns[] = new Bid[trueAnsStr.length];

		for (int i = 0; i < trueAnsStr.length; i++) {
			trueAns[i] = Bid.linStringToSingleBid(trueAnsStr[i]);
		}

		boolean told = gi.userAns.contentEquals("tellme");

		if (told == false) {

			Bid userAns = Bid.linStringToSingleBid(gi.userAns);

			int correct = -1;
			String ua = userAns.toString();
			for (int i = 0; i < trueAnsStr.length; i++) {
				if (trueAns[i].toString().contentEquals(ua)) {
					correct = i;
					break;
				}
			}

			String last_part = "";

			if (correct != -1) {
				gi.text = "   " + Aaf.quest_yes + "    " + Aaf.quest_cor + "   ";
			}
			else {
				gi.text = "   " + Aaf.quest_youClk + "   ";
				last_part = "   " + Aaf.quest_inCor;
			}
			consume_at__uni(gi);

			if (userAns.isCall()) {
				if (userAns.isPass()) {
					gi.text = Aaf.game_pass; // not a lookup
				}
				else if (userAns.isDouble()) {
					gi.text = Aaf.game_double; // not a lookup
				}
				else if (userAns.isReDouble()) {
					gi.text = Aaf.game_redouble;
				}
				gi.capEnv.bold = true;
				consume_at__uni(gi);
			}
			else {
				gi.capEnv.bold = true;
				gi.text = userAns.level.v + "";
				consume_at__uni(gi);

				if (userAns.suit == Suit.NoTrumps) {
					gi.capEnv.bold = true;
					gi.text = Aaf.game_nt;
					consume_at__uni(gi);
				}
				else if (userAns.suit != Suit.Invalid) {
					gi.numb = userAns.suit.v;
					consume_suitSymbol(gi, 's' /* a suit symbol */);
				}
			}

			if (last_part.length() > 0) {
				gi.capEnv.bold = false;
				gi.text = last_part;
				consume_at__uni(gi);
			}

		}
		else {

			gi.text = "    " + Aaf.quest_cor + "   ";
			consume_at__uni(gi);

			gi.capEnv.bold = true;

			for (int i = 0; i < trueAnsStr.length; i++) {
				Bid ta = trueAns[i];
				if (i > 0) {
					gi.text = "  /  ";
					gi.capEnv.bold = false;
					consume_at__uni(gi);
					gi.text = "";
				}

				if (ta.isCall()) {
					if (ta.isPass()) {
						gi.text = Aaf.game_pass; // not a lookup
					}
					else if (ta.isDouble()) {
						gi.text = Aaf.game_double; // not a lookup
					}
					else if (ta.isReDouble()) {
						gi.text = Aaf.game_redouble;
					}
					gi.capEnv.bold = true;
					consume_at__uni(gi);
				}
				else {
					gi.capEnv.bold = true;
					gi.text = ta.level.v + "";
					consume_at__uni(gi);

					if (ta.suit == Suit.NoTrumps) {
						gi.capEnv.bold = true;
						gi.text = Aaf.game_nt;
						consume_at__uni(gi);
					}
					else if (ta.suit != Suit.Invalid) {
						gi.numb = ta.suit.v;
						consume_suitSymbol(gi, 's' /* a suit symbol */);
					}
				}
			}
		}

		setCurSegEol();
		startNewSeg();
	}

	/**
	 */
	public void ans_lb_c(GraInfo gi) {
		// =============================================================================

		leftMargin = 0f * columnWidth;
		rightMargin = 9f * columnWidth;

		yRow = 5 /* rowConstA */ * rowSpacing + topAdjust;
		xCol = leftMargin;

		setCurSegEol();
		startNewSeg();
		gi.capEnv.centered = false;

		String trueAnsStr[] = gi.bb.get(4).split(",");

		Suit suitU[] = { Suit.Invalid };

//		Suit   suit[] = new Suit[trueAnsStr.length];
//		Level level[] = new Level[trueAnsStr.length];
		Card trueAns[] = new Card[trueAnsStr.length];

		for (int i = 0; i < trueAnsStr.length; i++) {
			trueAns[i] = Card.singleCardFromLinStr(trueAnsStr[i]);
		}

		boolean told = gi.userAns.contentEquals("tellme");

		if (told == false) {

			Card userAns = Card.singleCardFromLinStr(gi.userAns);

			suitU[0] = Suit.Invalid;
			String userAnsLevel = userAns.rankLetterOr10(suitU);

			int correct = -1;
			String ua = userAns.toString();
			for (int i = 0; i < trueAnsStr.length; i++) {
				if (trueAns[i].toString().contentEquals(ua)) {
					correct = i;
					break;
				}
			}

			String last_part = "";

			if (correct != -1) {
				gi.text = Aaf.quest_yes + "    " + Aaf.quest_cor + "  ";
			}
			else {
				gi.text = Aaf.quest_youClk + "  ";
				last_part = "   " + Aaf.quest_inCor;
			}
			consume_at__uni(gi);

			if (suitU[0] != Suit.Invalid) {
				gi.numb = suitU[0].v;
				consume_suitSymbol(gi, 's' /* a suit symbol */);
			}

			if (userAnsLevel.length() > 0) {
				gi.capEnv.bold = true;
				gi.text = Aaf.rankList.cardLetterLangConvForQuestions(userAns.rank.toChar());
				consume_at__uni(gi);
			}

			if (last_part.length() > 0) {
				gi.capEnv.bold = false;
				gi.text = last_part;
				consume_at__uni(gi);
			}

		}
		else {

			gi.text = "    " + Aaf.quest_cor + "   ";
			consume_at__uni(gi);

			gi.capEnv.bold = true;

			for (int i = 0; i < trueAnsStr.length; i++) {
				Card ta = trueAns[i];

				if (i > 0) {
					gi.text = "  /  ";
					gi.capEnv.bold = false;
					consume_at__uni(gi);
					gi.text = "";
				}

				if ((ta.suit != Suit.Invalid) && (ta.suit.v >= Suit.Clubs.v) && (ta.suit.v <= Suit.Spades.v)) {
					gi.numb = ta.suit.v;
					consume_suitSymbol(gi, 's' /* a suit symbol */);
				}

				gi.capEnv.bold = true;
				gi.text = Aaf.rankList.cardLetterLangConvForQuestions(ta.rank.toChar());
				consume_at__uni(gi);
				gi.capEnv.bold = false;
			}
		}

		setCurSegEol();
		startNewSeg();

	}

	/**
	 */
	public void ans_lb_h(GraInfo gi) {
		// =============================================================================

		leftMargin = 0f * columnWidth;
		rightMargin = 10f * columnWidth;

		yRow = -7 /* rowConstA */ * rowSpacing + topAdjust;
		xCol = leftMargin;

		setCurSegEol();
		Seg seg = startNewSeg();
		seg.leftMargin = leftMargin;
		seg.rightMargin = rightMargin;
		gi.capEnv.centered = true;

		String trueAns = gi.bb.getSafe(4).contentEquals("1") ? Aaf.quest_left : Aaf.quest_right;
		boolean told = gi.userAns.contentEquals("tellme");
		String userAns = (told) ? trueAns : (gi.userAns.contentEquals("1") ? Aaf.quest_left : Aaf.quest_right);

		if (trueAns.contentEquals(userAns)) {
			gi.text = (told ? "" : Aaf.quest_yes + "  ") + Aaf.quest_corHand;
		}
		else {
			gi.text = Aaf.gT("quest.inCor");
		}
		consume_at__uni(gi);

		if (trueAns.contentEquals(userAns)) {
			gi.text = trueAns;
			gi.capEnv.bold = true;
			consume_at__uni(gi);
		}

		setCurSegEol();
		startNewSeg();

	}

	/**
	 */
	public void ans_lb_ptlsd(GraInfo gi, char c) {
		// =============================================================================

		leftMargin = 0f * columnWidth;
		rightMargin = 10f * columnWidth;

		yRow = 5 /* rowConstA */ * rowSpacing + topAdjust;
		xCol = leftMargin;

		setCurSegEol();
		Seg seg = startNewSeg();
		seg.leftMargin = leftMargin;
		seg.rightMargin = rightMargin;
		// gi.capEnv.centered = true;

		String trueAns = MassGi_utils.ans_lb_ptlsd_points(App.tup.qp.deal1, c) + "";

		boolean told = gi.userAns.contentEquals("tellme");
		String userAns = (told) ? trueAns : gi.userAns;

		if (trueAns.contentEquals(userAns)) {
			gi.text = (told ? "" : Aaf.quest_yes + "  ") + Aaf.quest_cor + "  ";
		}
		else {
			gi.text = "No, incorrect answer";
		}
		consume_at__uni(gi);

		if (trueAns.contentEquals(userAns)) {
			gi.text = trueAns;
			gi.capEnv.bold = true;
			consume_at__uni(gi);
		}

		setCurSegEol();
		startNewSeg();
	}

	/**
	 */
	public void ans_lb_m_and_y(GraInfo gi, char c) {
		// =============================================================================

//		leftMargin = 1f * columnWidth;
//		rightMargin = 25f * columnWidth;

		leftMargin = 0;
		rightMargin = 27f * columnWidth;

		yRow = -10 /* rowConstA */ * rowSpacing + topAdjust;
		xCol = leftMargin;

		setCurSegEol();
		Seg seg = startNewSeg();
		seg.leftMargin = leftMargin;
		seg.rightMargin = rightMargin;
		gi.capEnv.centered = true;

		String trueAns = "";

		if (c == 'y') {
			gi.bb.set(3, Aaf.quest_yes + "~" + Aaf.quest_no);
			char a = (gi.bb.getSafe(4) + " ").toUpperCase().charAt(0);
			trueAns = (a == 'N') ? Aaf.quest_no : Aaf.quest_yes; // the n or N is in the lin file
		}

		String s[] = gi.bb.getSafe(3).split("\\~");

		if (c == 'm') {
			int ansPos = Aaa.extractPositiveInt(gi.bb.getSafe(4));
			if (ansPos < 1 || ansPos > s.length)
				return;

			ansPos--; // as the string[] indexes from zero

			trueAns = Aaa.deAtQuestionAndBubbleText(s[ansPos]);
		}

		boolean told = gi.userAns.contentEquals("tellme");
		String userAns = (told) ? trueAns : gi.userAns;

		String shortTrueAns = trueAns;

		if (userAns.length() < trueAns.length()) {
			shortTrueAns = trueAns.substring(0, userAns.length());
		}

		if (shortTrueAns.contentEquals(userAns)) {
			gi.text = (told ? "" : Aaf.quest_yes + "  ") + Aaf.quest_cor + " -   ";
		}
		else {
			gi.text = Aaf.quest_inCor;
		}

		consume_at__uni(gi);

		if (shortTrueAns.contentEquals(userAns)) {
			gi.text = trueAns;
			gi.capEnv.bold = true;
			consume_at__uni(gi);
		}
		setCurSegEol();
		startNewSeg();

	}

	/**
	 */
	public void ans_lb_unimp(GraInfo gi) {
		// =============================================================================
	}

	Color vDarkGray = new Color(80, 80, 80);

	/**
	 */
	static Timer autoNextTimer_z = new Timer(1000, new ActionListener() {
		// =============================================================================
		public void actionPerformed(ActionEvent evt) {
			autoNextTimer_z.stop();
			CmdHandler.question_z_Next();
		}

	});

	/**
	 */
	public void ans_lb_z(GraInfo gi) {
		// =============================================================================

		leftMargin = 0f * columnWidth;
		rightMargin = 6.6f * columnWidth;

		yRow = 2 /* rowConstA */ * rowSpacing + topAdjust;
		xCol = leftMargin;

		setCurSegEol();
		Seg seg = startNewSeg();
		seg.leftMargin = leftMargin;
		seg.rightMargin = rightMargin;
		gi.capEnv.centered = true;

		Deal deal = App.deal;

		String stage = gi.bb.get(2);

		if (stage.contentEquals("R")) {
			// just getting here means we need to reload the Distr Training Lin File
			autoNextTimer_z.stop();
			autoNextTimer_z.setInitialDelay(10); // 10 ms is logicaly zero
			autoNextTimer_z.start();
			return;
		}

		String trueAns = "";

		Hand ansHand = deal.hands[Dir.South.v];

		for (Hand hand : deal.hands) {
			for (Suit suit : Suit.cdhs) {
				Frag frag = hand.frags[suit.v];
				if ((frag.suitVisControl & Suit.SVC_ansHere) == Suit.SVC_ansHere) {
					trueAns = "" + frag.size();
					ansHand = hand;
					break;
				}
			}
		}

		if (gi.userAns.contentEquals("tellme"))
			gi.userAns = trueAns; // Force the answer to be correct

		if (gi.userAns.contentEquals(trueAns)) {

			if (stage.contentEquals("0")) { // this is Training

				for (Suit suit : Suit.cdhs) {
					Frag frag = ansHand.frags[suit.v];
					if ((frag.suitVisControl & Suit.SVC_ansHere) == Suit.SVC_ansHere) {
						frag.suitVisControl = Suit.SVC_cards | Suit.SVC_qaCount;
						break;
					}
				}

				if (App.dfcAutoNext != 3 && !autoNextTimer_z.isRunning() && !App.lbx_nextAndTellClicked) {
					int ms = 500; // not used
					// @formatter:off
					switch (App.dfcAutoNext) {
						case 0: ms = 1000; break;
						case 1: ms = 1750; break;
						case 2:	ms = 2500; break;
					}
					// @formatter:on
					autoNextTimer_z.setInitialDelay(ms);
					autoNextTimer_z.start();
				}
			}

			if (stage.contentEquals("Q")) { // Exam question (answered correctly)
				CmdHandler.question_z_Step(); // so we hop along to the next step
			}

		}

		for (Suit suit : Suit.shdc) { // yes Spades first
			Frag frag = ansHand.frags[suit.v];

			gi.text = "" + frag.size();

			gi.capEnv.font_slot_fp = MassGi.dfc_font_slot; // Bridge Face and symbol font
			gi.capEnv.color_cp = Cc.BlackStrong;
			gi.capEnv.bold = false;

			if ((frag.suitVisControl & Suit.SVC_qaDot) == Suit.SVC_qaDot) {
				gi.text = "o"; // the 'o' lower case o will appear as a BIG dot
			}
			else if ((frag.suitVisControl & Suit.SVC_qaCount) == Suit.SVC_qaCount) {
				// do nothing extra and the frag size will appear
				if (frag.size() == 0) {
					if (App.dfcHyphenForVoids)
						gi.text = "~"; // the "~" is changed in font to a bold hyphen
					else
						gi.capEnv.color_cp = Cc.BlackWeedy; // zero shows faint
				}
			}
			else {
				// to hide it we make it the background color so it will always be/stay the correct size
				gi.capEnv.color_cp = Aaa.tutorialBackground;
			}

			consume_at(gi); // NOT consume_at__uni(gi);

			if (frag.suit != Suit.Clubs) {
				gi.text = " ,,";
//				gi.capEnv.font_slot_fp = 0; // smaller font
				consume_at(gi);
			}
		}

		setCurSegEol();
		startNewSeg();
	}

}
