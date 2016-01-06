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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.text.AttributedString;

import javax.swing.JPanel;
import javax.swing.Timer;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.model.Bal;
import com.rogerpf.aabridge.model.Bid;
import com.rogerpf.aabridge.model.Cc;
import com.rogerpf.aabridge.model.Dir;
import com.rogerpf.aabridge.model.Hand;
import com.rogerpf.aabridge.model.Level;
import com.rogerpf.aabridge.model.Suit;
import com.rogerpf.aabridge.model.Zzz;
import com.rpsd.bridgefonts.BridgeFonts;

/**   
 */
public class BidsFourDisplayPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	Bal bidA[] = { new Bal(), new Bal(), new Bal(), new Bal() };

	/**
	*/
	public BidsFourDisplayPanel() { // Constructor
		setOpaque(false);
		// setBackground(Aaa.baizeGreen);
	}

	/**
	*/
	public Timer biddingCompleteDelayTimer = new Timer(700, new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			biddingCompleteDelayTimer.stop();
			biddingCompleteDelayTimer_part2.start();
			App.frame.repaint();
		}
	});

	/**
	*/
	public Timer biddingCompleteDelayTimer_part2 = new Timer(1800, new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			biddingCompleteDelayTimer_part2.stop();
			// assume the controler has used us as the end of contract pause
			// timer
			if (App.isMode(Aaa.EDIT_BIDDING) && App.deal.isAuctionFinished()) {
				// ignore any bids added to a completed Auction
				// the user should use undo first
				return;
			}
			App.con.voiceTheBid(null);
			App.frame.repaint();
		}
	});

	/**
	*/
	public Timer reviewBidDisplayTimer = new Timer(App.bidPluseTimerMs, new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			reviewBidDisplayTimer.stop();
			reviewBidDisplayTimer.setInitialDelay(App.bidPluseTimerMs);

			if (App.isMode(Aaa.REVIEW_BIDDING)) {
				App.con.reviewBidDisplayTimerFired();
			}
		}
	});

	/**   
	 */
	public void dealDirectionChange() {

	}

	/**  
	 * Creates a set of Bidding arraylists that match known bid number
	 */
	public void reviewBiddingMakeCopy() {
		if (App.reviewBid < 0) {
			App.reviewBid = 0;
		}

		int count = App.deal.countBids();
		if (App.reviewBid > count) {
			App.reviewBid = count;
		}

		for (Dir compass : Dir.rota[App.deal.dealer.v]) {
			bidA[compass.v].clear();
		}

		boolean end = false;
		int j = -1;
		while (!end) {
			for (Dir compass : Dir.rota[App.deal.dealer.v]) {
				j++;
				int i = j / 4;
				Bal bids = bidA[compass.v];
				Hand hand = App.deal.hands[compass.v];
				if (j == App.reviewBid || i >= hand.bids.size()) {
					end = true;
					break;
				}
				bids.add(hand.bids.get(i));
			}
		}
	}

	/**   
	 */
	public void paintComponent(Graphics g) { // BidDisplayPanel
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;
		Aaa.commonGraphicsSettings(g2);
		FontRenderContext frc = g2.getFontRenderContext();

		Dimension panelSize = getSize();

		// First a diversion to make the user fully aware of the contract

		if (biddingCompleteDelayTimer_part2.isRunning()) {

			float panelWidth = (float) panelSize.width;
			float panelHeight = (float) panelSize.height;

			float marginLeft = panelWidth * 0.04f;
			float marginRight = panelWidth * 0.04f;
			float marginTop = panelHeight * 0.04f;
			float marginBottom = panelHeight * 0.03f;

			float activityWidth = panelWidth - (marginLeft + marginRight);
			float activityHeight = panelHeight - (marginTop + marginBottom);

			float curve = panelHeight * 0.25f;

			RoundRectangle2D.Float rr = new RoundRectangle2D.Float();

			// fill the lozenge ----------------------------------------------
			g2.setColor(Aaa.biddingBkColor);
			rr.setRoundRect(marginLeft, marginTop, activityWidth, activityHeight, curve, curve);
			g2.fill(rr);

			g2.setStroke(new BasicStroke(activityWidth * 0.01f));
			g2.setColor(Aaa.weedyBlack);
			g2.draw(rr);

			// Add in the text
			float fontSize = activityHeight * 0.25f;
			Font df = BridgeFonts.bridgeBoldFont.deriveFont(fontSize);
			Font bf = BridgeFonts.faceAndSymbFont.deriveFont(fontSize);

			String ct = App.deal.contract.suit.toStr();
			int ctLen = ct.length();

			String pl = App.deal.contractCompass.toLongStr();

			String dr = App.deal.contractDblRe.call.toEarlyContractDisplayString();

			String t = App.deal.contract.level.toStr() + ct + dr + " by " + pl;
			AttributedString at = new AttributedString(t);

			g2.setColor(new Color(70, 70, 70));

			at.addAttribute(TextAttribute.FONT, df, 0, t.length());
			// at.addAttribute(TextAttribute.FOREGROUND, Aaa.weedyBlack, 0, t.length());
			at.addAttribute(TextAttribute.FONT, bf, 0, 1 + ctLen);
			at.addAttribute(TextAttribute.FOREGROUND, App.deal.contract.suit.color(Cc.Ce.Strong), 1, 1 + ctLen);
			TextLayout tl = new TextLayout(at.getIterator(), frc);

			tl.draw(g2, marginLeft + activityWidth * 0.12f, marginTop + activityHeight * 0.45f);

			g2.setFont(BridgeFonts.bridgeBoldFont.deriveFont(fontSize * 0.8f));

			t = App.deal.contractCompass.nextClockwise().toLongStr() + " to lead";

			Aaa.drawCenteredString(g2, t, marginLeft, marginTop + activityHeight * 0.45f, activityWidth, activityHeight * 0.6f);

			return;
		}

		// preset as many common values as possible
		// ----------------------------------------

		float panelWidth = (float) panelSize.width;
		float panelHeight = (float) panelSize.height;

		float marginLeft = panelWidth * 0.00f;
		float marginRight = panelWidth * 0.02f;
		float marginTop = panelHeight * 0.04f;
		float marginBottom = panelHeight * 0.03f;

		float activityWidth = panelWidth - (marginLeft + marginRight);
		float activityHeight = panelHeight - (marginTop + marginBottom);

		float cardWidth = activityWidth * 0.28f;
		float cardHeight = cardWidth * 0.65f;
		float curve = cardHeight * 0.50f;

		float blackLineWidth = activityWidth * 0.007f;

		float levelFontSize = cardHeight * 0.80f;
		float symbolFontSize = cardHeight * 0.70f;

		Font levelFont = BridgeFonts.faceAndSymbFont.deriveFont(levelFontSize);
		Font symbolFont = BridgeFonts.faceAndSymbFont.deriveFont(symbolFontSize);
		Font passFont = BridgeFonts.bridgeLightFont.deriveFont(cardHeight * 0.6f);
		Font doubleFont = BridgeFonts.bridgeBoldFont.deriveFont(cardHeight * 0.60f);
		Font redoubleFont = BridgeFonts.bridgeBoldFont.deriveFont(cardHeight * 0.50f);
		Font alertFont = BridgeFonts.bridgeBoldFont.deriveFont(cardHeight * 0.70f);
		Font qmFont = BridgeFonts.bridgeBoldFont.deriveFont(cardHeight * 0.90f);

		// ------------------------------------------------------------------

//		float boarderLeft = cardWidth * 0.12f;
		float boarderInLeft = cardWidth * 0.44f;
		float symbolBoarderBot = cardHeight * 0.20f;
		float levelBoarderBot = cardHeight * 0.20f;

		//@formatter:off
		Point2D.Float leftTopPercent[] = new Point2D.Float[] { 
				new Point2D.Float(0.38f, 0.00f), /* N */
				new Point2D.Float(0.67f, 0.28f), /* E */
				new Point2D.Float(0.38f, 0.57f), /* S */
				new Point2D.Float(0.06f, 0.28f)  /* W */ };
		//@formatter:on

		Hand nextToBid;
		if (App.isMode(Aaa.REVIEW_BIDDING)) {
			nextToBid = App.deal.hands[(App.reviewBid + App.deal.dealer.v) % 4];
		}
		else {
			nextToBid = App.deal.getNextHandToBid();
			if (nextToBid == null)
				return;
		}

		Dir startCompass = nextToBid.compass.rotate(1);

		Boolean showQm = App.deal.showBidQuestionMark || !App.deal.showBidQuestionMark && App.isLin__FullMovie() && App.mg.isEndAQuestion()
				&& !App.deal.dfcDeal;

		for (int i : Zzz.zto3) {

			Hand hand = App.deal.getHand(startCompass.rotate(i));

			Dir phyPos = App.phyScreenPosFromCompass(hand.compass);

			Bal bids = (App.isMode(Aaa.REVIEW_BIDDING)) ? bidA[hand.compass.v] : hand.bids;

			Bid bid = null;

			Level level = Level.Invalid;
			Suit suit = Suit.Invalid;
			boolean alert = false;

			float left = marginLeft + activityWidth * leftTopPercent[phyPos.v].x;
			float top = marginTop + activityHeight * leftTopPercent[phyPos.v].y;

			float levelBottom = top + cardHeight - levelBoarderBot;

			float symbolLeft = left + boarderInLeft;
			float symbolBottom = top + cardHeight - symbolBoarderBot;

			if (i < 3) {
				if (bids.isEmpty())
					continue;
				if ((i == 0) && showQm)
					continue;
				bid = bids.getLast(); // get their last bid
				level = bid.level;
				suit = bid.suit;
				alert = bid.alert;
			}
			else if (i == 3 && App.isMode(Aaa.REVIEW_BIDDING)) {
				continue;
			}
			else if (App.isAutoBid(hand.compass) == false) {
				level = App.gbp.c2_2__bbp.getHalfBidLevel();
				suit = App.gbp.c2_2__bbp.getHalfBidSuit();
				alert = App.gbp.c2_2__bbp.getHalfBidAlert();

				// fill the lozenge
				// ----------------------------------------------
				RoundRectangle2D.Float rr = new RoundRectangle2D.Float(left, top, cardWidth, cardHeight, curve, curve);
				if (App.isVmode_InsideADeal()) {
					float dash[] = { 0.04f * cardWidth, 0.05f * cardWidth };
					g2.setStroke(new BasicStroke(blackLineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 2.0f, dash, 2.0f));
					g2.setColor(Color.BLACK);
					g2.draw(rr);
				}
			}

			// fill the lozenge ----------------------------------------------

			if (i == 3 && showQm) { // special case ? required
				g2.setColor(Cc.SuitColor(Suit.NoTrumps, Cc.Ce.Strong));
				g2.setFont(qmFont);
				float eastAdjust = (phyPos == Dir.East) ? 0.2f : 0.0f;
				g2.drawString("?", symbolLeft - cardWidth * (0.15f - eastAdjust), symbolBottom);
				continue;
			}

			Cc.Ce strength[] = { Cc.Ce.Weak, Cc.Ce.Weak, Cc.Ce.Strong, Cc.Ce.Strong };

			String sLevel = "";
			String sBid = "";
			String sAlert = (alert) ? "!" : "";
			Color levelColor = Cc.SuitColor(Suit.NoTrumps, strength[i]);
			Font bidFont = null;
			Color bidColor = null;

			if (bid != null && bid.isCall()) {
				sBid = bid.call.toString();
				bidColor = Cc.SuitColor(Suit.NoTrumps, strength[i]);

				if (bid.isPass()) {
					bidFont = passFont;
				}
				else if (bid.isDouble()) {
					bidFont = doubleFont;
				}
				else if (bid.isReDouble()) {
					bidFont = redoubleFont;
				}
			}
			else {

				// Level Value
				if (level != Level.Invalid) {
					sLevel = level.toStr();
				}

				// Suit Value
				if ((suit != Suit.Invalid) && (Suit.Clubs.v <= suit.v) && (suit.v <= Suit.NoTrumps.v)) {

					bidColor = Cc.SuitColor((i != 0 ? suit : Suit.NoTrumps), strength[i]);
					sBid = suit.toStrNu();
					bidFont = symbolFont;

				}
			}

			if (sLevel.length() + sBid.length() == 0)
				continue;

			String sAll = sLevel + sBid + sAlert;
			AttributedString astr = new AttributedString(sAll);

			astr.addAttribute(TextAttribute.FONT, levelFont);
			astr.addAttribute(TextAttribute.FOREGROUND, levelColor);
			int pos = sLevel.length();
			int posNext = pos + sBid.length();
			if (bidFont != null) {
				astr.addAttribute(TextAttribute.FONT, bidFont, pos, posNext);
				astr.addAttribute(TextAttribute.FOREGROUND, bidColor, pos, posNext);
			}
			if (alert) {
				Color alertColor = Cc.SuitColor(Suit.Hearts, strength[i]);
				pos = posNext;
				posNext += sAlert.length();
				astr.addAttribute(TextAttribute.FONT, alertFont, pos, posNext);
				astr.addAttribute(TextAttribute.FOREGROUND, alertColor, pos, posNext);
			}

			// This adjustment is purely because that is the way I like to see them :)
			//
			float adjust = 0.10f;

			if ((phyPos != Dir.West) && (bid != null && (bid.isPass() || bid.isReDouble()))) {
				adjust = 0.0f;
			}
			else if ((phyPos == Dir.North || phyPos == Dir.South) && (bid != null && (bid.suit == Suit.NoTrumps))) {
				adjust = 0.05f;
			}
			else if ((phyPos == Dir.East) && (bid != null && bid.isValidBid())) {
				adjust = 0.21f;
			}

			TextLayout tl = new TextLayout(astr.getIterator(), frc);
			float x = left + cardWidth * adjust;
			float y = levelBottom;

			tl.draw(g2, x, y);

		}
	}
}
