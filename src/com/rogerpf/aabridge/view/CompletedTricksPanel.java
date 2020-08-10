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
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.RoundRectangle2D.Float;
import java.util.ArrayList;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.Aaf;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.model.Card;
import com.rogerpf.aabridge.model.Cc;
import com.rogerpf.aabridge.model.Dir;
import com.rogerpf.aabridge.model.Hand;
import com.rogerpf.aabridge.model.Level;
import com.rogerpf.aabridge.model.Rank;
import com.rogerpf.aabridge.model.Suit;
import com.rpsd.bridgefonts.BridgeFonts;
// import java.awt.RenderingHints;
// import java.awt.geom.Point2D;
// import java.awt.geom.RoundRectangle2D;
// import java.util.Hashtable;

/**   
 */
public class CompletedTricksPanel extends ClickPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	int trickShowing = -1;
	int trickRequested = -1;

	ArrayList<Rectangle2D.Float> cardRectsCache = new ArrayList<Rectangle2D.Float>();

	// ----------------------------------------
	CompletedTricksPanel() { /* Constructor */
		setOpaque(false);
		// setBackground(Aaa.baizeGreen);
		addMouseMotionListener(new MouseMotionListener());
		addMouseListener(new MouseListener());
		setVisible(false);
	}

	/**
	 */
	private class MouseMotionListener extends MouseMotionAdapter {

		/**
		 */
		public void mouseMoved(MouseEvent e) {
			// System.out.println("Mouse click on HandDisplayPanel");

			Point ep = e.getPoint();

			for (int i = cardRectsCache.size() - 1; i >= 0; i--) {
				Rectangle2D.Float rect = cardRectsCache.get(i);
				if (rect.contains(ep)) {
					trickRequested = i;
					// System.out.println( trickRequested);
					if (trickRequested != trickShowing) {
						App.frame.repaint();
					}
					return;
				}
			}

			boolean trickWasShowing = (trickShowing != -1);
			trickShowing = -1;
			trickRequested = -1;
			if (trickWasShowing) {
				App.frame.repaint();
			}
		}
	}

	/**
	 */
	private class MouseListener extends MouseAdapter {

		/**
		 */
		public void mouseExited(MouseEvent e) {
			// System.out.println("Mouse Exited WeTheyDisplayPanel");
			mouseMoved(e);
		}

		public void mouseClicked(MouseEvent e) {
			// System.out.println("Mouse Clicked WeTheyDisplayPanel");

			if (!App.isMode(Aaa.REVIEW_PLAY))
				return;

			Point ep = e.getPoint();

			for (int i = cardRectsCache.size() - 1; i >= 0; i--) {
				Rectangle2D.Float rect = cardRectsCache.get(i);
				if (rect.contains(ep)) {
					App.con.reviewTimeRequestToShowTrick(i);
					return;
				}
			}

		}
	}

	// ----------------------------------------
	public void paintComponent(Graphics g) { // CompletedTricksPanel

		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;

		if (App.flowOnlyCommandBar) // so very ugly
			return;

		Aaa.commonGraphicsSettings(g2);

		Dimension panelSize = getSize();

		// fill the scoreLozenge ----------------------------------------------

		float panelWidth = (float) panelSize.width;
		float panelHeight = (float) panelSize.height;

		float marginLeft = panelWidth * 0.03f;
		float marginRight = panelWidth * 0.02f;

		/* calculated here so we know the scoreLozengeHeight early */
		float scoreLozengeX = 0 + marginLeft;
		float scoreLozengeY = panelHeight * (1.0f - 0.21f);

		float scoreLozengeWidth = panelWidth - (marginLeft + marginRight);
		float scoreLozengeHeight = panelHeight - scoreLozengeY;
		/* end early calculation */

		float activityWidth = panelWidth - (marginLeft + marginRight);

		float cardWidth = activityWidth * (93f / 403f);
		float cardHeight = cardWidth * 1.25f;
		float cardSpacing = (activityWidth - cardHeight) / 12f;
		float curve = cardHeight / 7.0f;

		float activityHeight = cardHeight;
		float marginActivityTop = panelHeight - activityHeight;

		float blackLineWidth = panelWidth / 400f;
		float wbw = panelWidth / 80f; // whiteBorderWidth

		float x = marginLeft;
		float y = activityHeight;

		RoundRectangle2D.Float rr = new RoundRectangle2D.Float();

		// We display the cards as seen by - 'the target'
		Dir targetSeat = Dir.Invalid;
		switch (App.scoringFollows) {
		case App.scoringFollowsYou:
			targetSeat = App.deal.getTheYouSeat();
			break;
		case App.scoringFollowsDeclarer:
			targetSeat = App.deal.getDeclarerOrTheDefaultYouSeat();
			break;
		case App.scoringFollowsSouthZone:
			targetSeat = App.deal.hands[App.cpeFromPhyScreenPos(Dir.South).v].compass;
			break;
		}
		int targetAxis = targetSeat.v % 2;

		cardRectsCache.clear();

		int countWon = 0;
		int countLost = 0;

		int actualTricksPlayed = App.deal.prevTrickWinner.size() - 1;
		int actualCardsPlayed = App.deal.countCardsPlayed();

		int reviewCardIncTricks = App.reviewTrick * 4 + App.reviewCard;

		boolean youSeatDeclarer = App.deal.isYouSeatDeclarerAxis();

		int pre_won = App.deal.preset_trick_count_x;
		int pre_lost = App.deal.preset_trick_count_y;

		int preset_tricks = pre_won + pre_lost;

		/**
		 * Display all the cards backs in their correct direction
		 * including the fainter 'claimed' ones.
		 */
		int step = 0;
		for (; step < 13; step++) {
			// note - the zeroth is the leader to the first trick

			if (App.isMode(Aaa.REVIEW_PLAY)) {
				if (reviewCardIncTricks < actualCardsPlayed) { // we WILL want to stop if we have gone to high
					if (step - preset_tricks <= actualTricksPlayed) {
						if ((step - preset_tricks > App.reviewTrick) || (step - preset_tricks == App.reviewTrick && App.reviewCard < 4))
							break;
					}
				}
//				else {
//					if (App.deal.endedWithClaim == false && actualCardsPlayed < 52) {
//						break;
//					}
//				}
			}

			boolean won = true;
			boolean claimed = false;
			boolean preset = false;

			if (step < preset_tricks) { // step starts from 0
				preset = true;
				if (step < pre_lost)
					won = !youSeatDeclarer;
				else
					won = youSeatDeclarer;
			}
			else if (step - preset_tricks < actualTricksPlayed) {
				Hand winner = App.deal.prevTrickWinner.get(step - preset_tricks + 1);
				won = (winner.axis() == targetAxis);
			}
			else {
				if (App.deal.endedWithClaim == false)
					break;
				claimed = true;
				won = (youSeatDeclarer) ? (countWon < App.deal.tricksClaimed - pre_won) : !(countLost < App.deal.tricksClaimed - pre_lost);
			}

			if (!preset) {
				if (won)
					countWon++;
				else
					countLost++;
			}

			float height = (won) ? cardHeight : cardWidth;
			float width = (won) ? cardWidth : cardHeight;
			float top = (won) ? marginActivityTop : marginActivityTop + (cardHeight - cardWidth);

			// draw a cardback ----------------------------------------------
			rr.setRoundRect(marginLeft + (cardSpacing * step), top, width, height, curve, curve);
			g2.setColor(Color.WHITE);
			g2.fill(rr);

			g2.setColor(claimed ? Aaa.weedyBlack : Color.black);
			g2.setStroke(new BasicStroke(blackLineWidth));
			g2.draw(rr);

			Color cardBackColorPreset = new Color(190, 190, 210);

			rr.setRoundRect(marginLeft + (cardSpacing * step) + wbw, top + wbw, width - 2 * wbw, height - 2 * wbw, curve, curve);
			g2.setColor(preset ? cardBackColorPreset : (claimed ? Aaa.cardBackColorClm : Aaa.cardBackColor));
			g2.fill(rr);

			/* cache the hittest version */
			cardRectsCache.add(new Rectangle2D.Float(marginLeft + (cardSpacing * step), top, width, height - scoreLozengeHeight));
		}

		/** 
		 * If the above step has dislayed all thirteen cards then (and only then) should
		 *    we display the colored    Made / Over / Down / Passed-Out
		 */

		if ((step >= 13 && !App.deal.isDoneHand()) || App.deal.contract.isPass()) {

			Rectangle2D.Float r2 = new Rectangle2D.Float();

			float marginLeft2 = panelWidth * 0.28f;
			float marginTop2 = panelHeight * 0.05f;

			float activityWidth2 = panelWidth - (marginLeft2 * 2);
			float activityHeight2 = panelWidth * 0.16f;

			float fontSize = activityHeight2 * 0.52f;

			int trickDiff = App.deal.getContractTrickCountSoFar().x - (6 + App.deal.contract.level.v);

			boolean outline = true;
			String text = "";
			if (App.deal.contract.isPass()) {
				g2.setColor(Cc.BlueWeak);
				fontSize *= 0.8f;
				text = Aaf.game_passedOut;
			}
			else if (trickDiff > 0) {
				outline = true;
				g2.setColor(Cc.GreenWeak);
				text = Integer.toString(trickDiff) + " " + Aaf.game_over;
			}
			else if (trickDiff == 0) {
				outline = true;
				g2.setColor(Cc.GreenWeak);
				fontSize *= 1.1f;
				text = Aaf.game_made;
			}
			else {
				g2.setColor(Cc.RedWeak);
				text = Integer.toString(-trickDiff) + " " + Aaf.game_down;
			}

			// fill the lozenge ----------------------------------------------
			r2.setRect(marginLeft2, marginTop2, activityWidth2, activityHeight2);
			g2.fill(r2);
			g2.setFont(BridgeFonts.internatBoldFont.deriveFont(fontSize));
			g2.setColor(Aaa.handBkColorStd);

			Aaa.drawCenteredString(g2, text, marginLeft2, marginTop2, activityWidth2, activityHeight2);

			if (outline) {
				g2.setStroke(new BasicStroke(activityHeight2 * 0.02f));
				g2.setColor(Aaa.handBkColorStd);
				g2.draw(r2);
			}
		}

		Rectangle2D.Float r2 = new Rectangle2D.Float(scoreLozengeX, scoreLozengeY, scoreLozengeWidth, scoreLozengeHeight);

		float rnd = scoreLozengeWidth * 0.1f;
		RoundRectangle2D.Float r3 = new RoundRectangle2D.Float(scoreLozengeX * 0.8f, scoreLozengeY * 0.97f, scoreLozengeWidth * 0.22f,
				scoreLozengeHeight * 2.0f, rnd, rnd);
		Rectangle2D.Float r4 = new Rectangle2D.Float(scoreLozengeX * 0.8f, scoreLozengeY * 0.97f, scoreLozengeWidth / 20, scoreLozengeHeight * 2.0f);

		// Fill the scorelozenge background
		// --------------------------------------------------
		g2.setColor(Aaa.scoreBkColor);
		g2.setStroke(new BasicStroke(blackLineWidth * 1.3f));
		g2.fill(r2);
		g2.fill(r3);
		g2.fill(r4);

		// Player Direction (compass.v) text
		// ------------------------------------------------------------------

		x = scoreLozengeX;
		y = scoreLozengeY + scoreLozengeHeight * (1.0f - 0.23f);

		Level contractLevel = App.deal.contract.level;
		Suit contractSuit = App.deal.contract.suit;
		Dir contractCompass = App.deal.contractCompass;

		Font contractLevelFont = BridgeFonts.faceAndSymbolFont.deriveFont(scoreLozengeHeight * 1.2f);
		Font contractSymbolFont = BridgeFonts.faceAndSymbolFont.deriveFont(scoreLozengeHeight * 1.1f);
		Font generalFont = BridgeFonts.internatBoldFont.deriveFont(scoreLozengeHeight * 0.60f);
		Font declarerFont = BridgeFonts.internatBoldFont.deriveFont(scoreLozengeHeight * 0.82f);
		Font scoreFont = BridgeFonts.internatBoldFont.deriveFont(scoreLozengeHeight * 0.8f);
		Font claimFont = BridgeFonts.internatBoldFont.deriveFont(scoreLozengeHeight * 0.90f);

		if (App.deal.contract.isPass()) {
			x += scoreLozengeWidth * 0.01f;
			g2.setColor(Color.BLACK);
			g2.setFont(generalFont);
			g2.drawString("    - - - - -", x, y);
		}
		else { // we have a normal contact
			x += scoreLozengeWidth * 0.006f;
			g2.setColor(Color.BLACK);
			g2.setFont(contractLevelFont);
			g2.drawString(contractLevel.toStr(), x, y * 1.02f);

			x += scoreLozengeWidth * 0.090f;
			g2.setColor(contractSuit.color(Cc.Ce.Strong));
			if (contractSuit == Suit.NoTrumps) {
				if (Aaf.game_nt.equals("NT")) { // so we convert the English NT into just N
					g2.setFont(BridgeFonts.internatBoldFont.deriveFont(scoreLozengeHeight * 1.05f));
					g2.drawString("N", x * 0.98f, y * 1.02f);
				}
				else {
					g2.setFont(generalFont);
					g2.drawString(Aaf.game_nt, x * 1.0f, y * 0.98f);
				}
			}
			else {
				g2.setFont(contractSymbolFont);
				g2.drawString(contractSuit.toStrLower(), x * 0.98f, y * 1.02f);
			}

			{ // Double and redouble or by
				g2.setColor(Color.BLACK);
				float x2 = x + scoreLozengeWidth * 0.12f;
				if (App.deal.contractDblRe.isDouble()) {
					g2.setFont(BridgeFonts.faceAndSymbolFont.deriveFont(scoreLozengeHeight * 0.70f));
					g2.drawString("x", x2 * 0.95f, y - scoreLozengeWidth * 0.008f);
				}
				else if (App.deal.contractDblRe.isReDouble()) {
					g2.setFont(BridgeFonts.faceAndSymbolFont.deriveFont(scoreLozengeHeight * 0.5f));
					g2.drawString("xx", x2 * 0.95f, y - scoreLozengeWidth * 0.018f);
				}
			}

			x += scoreLozengeWidth * 0.21f;
			g2.setColor(Color.BLACK);
			g2.setFont(declarerFont);
			g2.drawString(Dir.getLangDirChar(contractCompass) + "", x, y * 1.011f);
		}

		// Now we do the tricks won so far
		Point score = new Point(countWon, countLost); // declarers count is always in the 'x' value

		int youSeatAxis = (App.deal.getTheYouSeat().v % 2);
		int contractAxis = App.deal.contractAxis();

		int firstPairTrickCount = score.x += (youSeatAxis == contractAxis) ? App.deal.preset_trick_count_x : App.deal.preset_trick_count_y;
		int secondPairTrickCount = score.y += (youSeatAxis != contractAxis) ? App.deal.preset_trick_count_x : App.deal.preset_trick_count_y;

		if (firstPairTrickCount > 13)
			firstPairTrickCount = 13;

		if (secondPairTrickCount > (13 - firstPairTrickCount))
			secondPairTrickCount = (13 - firstPairTrickCount);

		String firstPair = Dir.axisStr(targetAxis);
		String secondPair = Dir.axisStr((targetAxis + 1) % 2);

		// @formatter:off
		if (youSeatAxis == targetAxis) {
			firstPair = Aaf.game_youShort;
			secondPair =    Dir.getLangDirChar( Dir.directionFromChar(secondPair.charAt(0))) + 
					   "" + Dir.getLangDirChar( Dir.directionFromChar(secondPair.charAt(1)));
		}
		else {
			firstPair =     Dir.getLangDirChar( Dir.directionFromChar(firstPair.charAt(0))) + 
					   "" + Dir.getLangDirChar( Dir.directionFromChar(firstPair.charAt(1)));
			secondPair = Aaf.game_youShort;
		}
		// @formatter:on

		y = scoreLozengeY + scoreLozengeHeight * (1.0f - 0.2f);

		x = scoreLozengeX + scoreLozengeWidth * 0.425f; // jump to the middle of the lozenge
		g2.setFont(generalFont);
		g2.drawString(String.format("%s", firstPair), x, y);

		x += scoreLozengeWidth * 0.160f;
		g2.setFont(scoreFont);
		g2.drawString(String.format("%d", firstPairTrickCount), x, y);

		x += scoreLozengeWidth * 0.160f;
		g2.setFont(generalFont);
		g2.drawString(String.format("%s", secondPair), x, y);

		x += scoreLozengeWidth * 0.135f;
		g2.setFont(scoreFont);
		g2.drawString(String.format("%d", secondPairTrickCount), x, y);

		// System.out.println( String.format( "%d    %d", trickRequested, trickShowing));

		if ((trickRequested == -1) || (trickRequested == trickShowing))
			return;

		trickShowing = trickRequested;

		/*
		 * Display the requested previously completed trick
		 */

		marginLeft = marginLeft / 3;
		float marginTop = panelHeight * 0.02f;

		cardWidth = activityWidth * 0.21f;
		cardHeight = activityWidth * 0.135f;
		curve = cardHeight * 0.55f;

		float colorLineWidth = activityWidth * 0.0095f;

		float faceFontSize = cardHeight * 0.85f;
		float symbolFontSize = cardHeight * 0.60f;

		Font symbolFont = BridgeFonts.faceAndSymbolFont.deriveFont(symbolFontSize);
		Font faceFont = BridgeFonts.faceAndSymbolFont.deriveFont(faceFontSize);
		Font faceInternationalFont = BridgeFonts.internatBoldFont.deriveFont(faceFontSize);

		// ------------------------------------------------------------------

		float boarderLeft = cardWidth * 0.16f;
		float boarderInLeft = cardWidth * 0.45f;
		float symbolBoarderBot = cardHeight * 0.27f;
		float faceBoarderBot = cardHeight * 0.17f;

		// RoundRectangle2D.Float rr = new RoundRectangle2D.Float();

		// We display the cards as seen by the player in the phyiscal Pos
		// 'South' seat (the target)

		Point2D.Float leftTopPercent[] = new Point2D.Float[] { new Point2D.Float(0.17f, 0.00f), /* N */
				new Point2D.Float(0.34f, 0.28f), /* E */
				new Point2D.Float(0.17f, 0.57f), /* S */
				new Point2D.Float(0.00f, 0.28f) /* W */ };

		boolean claimed = App.deal.endedWithClaim;

		if (trickRequested < preset_tricks) {
			; // do nothing
		}
		else if (claimed && (trickRequested >= (App.deal.prevTrickWinner.size() + preset_tricks - 1))) {

			float left = marginLeft + activityWidth * leftTopPercent[Dir.South.v].x + ((float) trickRequested) / 13.0f * activityWidth * 0.52f;
			float bot = panelHeight - cardHeight * 2.2f;

			g2.setColor(Aaa.cardBackColor);
			g2.setFont(claimFont);
			g2.drawString(" claim", left, bot);
		}
		else {
			Hand trickWinner = App.deal.prevTrickWinner.get(trickRequested - preset_tricks + 1);
			Hand leader = App.deal.prevTrickWinner.get(trickRequested - preset_tricks);

			Boolean xesUseVaild = App.deal.showXesValidPlayedCards && (App.deal.showXesValidInsideDeal || (App.isVmode_InsideADeal() == false));

			for (Dir compass : Dir.rota[leader.compass.v]) {
				Hand hand = App.deal.hands[compass.v];
				assert (compass.v == hand.compass.v);

				Card card = hand.played.get(trickRequested - preset_tricks);
				Suit suit = card.suit;
				Rank rank = card.rank;
				char showAsX = xesUseVaild ? (hand.frags[card.suit.v].showXes) : '-';
				if (showAsX == '*') {
					showAsX = 'x'; // we do not support 'invisible' for displayed cards in a trick
				}

				Dir phyPos = App.phyScreenPosFromCompass(hand.compass);
				float left = marginLeft + activityWidth * leftTopPercent[phyPos.v].x + ((float) trickRequested) / 13.0f * activityWidth * 0.52f;
				float top = marginTop * 0.2f + activityHeight * leftTopPercent[phyPos.v].y;

				float symbolLeft = left + boarderLeft;
				float symbolBottom = top + cardHeight - symbolBoarderBot;

				float faceLeft = left + boarderInLeft;
				float faceBottom = top + cardHeight - faceBoarderBot;

				// fill the lozenge ----------------------------------------------
				rr.setRoundRect(left, top, cardWidth, cardHeight, curve, curve);

				g2.setPaint(Color.WHITE);
				g2.fill(rr);

				float stkSize = colorLineWidth * ((hand == trickWinner) ? 1.95f : 1);
				if (App.outlineCardEdge) {
					RoundRectangle2D.Float rr2 = (Float) rr.clone();
					g2.setColor(Aaa.handBkColorStd);
					g2.setStroke(new BasicStroke(stkSize + colorLineWidth * 1.1f));
					g2.draw(rr2);
				}
				g2.setColor(suit.color(Cc.Ce.Weak));
				g2.setStroke(new BasicStroke(stkSize));
				g2.draw(rr);

				// Suit Value
				{
					g2.setColor(suit.color(Cc.Ce.Weak));
					g2.setFont(symbolFont);
					g2.drawString(suit.toStrLower(), symbolLeft, symbolBottom);
				}

				// Face Value
				{
					g2.setColor(suit.colorCd(Cc.Ce.Strong));
					char c = Rank.rankToLanguage(rank.toChar(), showAsX);
					g2.setFont(Aaa.isLatinFaceCard(c) ? faceFont : faceInternationalFont);
					g2.drawString(c + "", faceLeft, faceBottom);
				}
			}
		}
	}
}
