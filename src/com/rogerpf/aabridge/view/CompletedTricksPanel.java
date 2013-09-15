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
import java.util.ArrayList;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.model.Card;
import com.rogerpf.aabridge.model.Hand;
import com.rogerpf.aabridge.model.Zzz;
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
					// System.out.println( trickRequested);
					trickRequested = i;
					if (trickRequested != trickShowing) {
						repaint();
					}
					return;
				}
			}

			boolean trickWasShowing = (trickShowing != -1);
			trickShowing = -1;
			trickRequested = -1;
			if (trickWasShowing) {
				repaint();
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
		setBackground(Aaa.baizeGreen);

		Graphics2D g2 = (Graphics2D) g;

		Aaa.commonGraphicsSettings(g2);

		Dimension panelSize = getSize();

		// fill the scoreLozenge ----------------------------------------------

		float panelWidth = (float) panelSize.width;
		float panelHeight = (float) panelSize.height;

		float marginLeft = panelWidth * 0.05f;
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
		int targetSeat = 0;
		switch (App.scoringFollows) {
		case App.scoringFollowsYou:
			targetSeat = App.deal.getTheYouSeat();
			break;
		case App.scoringFollowsDeclarer:
			targetSeat = App.deal.getDeclarerOrTheDefaultYouSeat();
			break;
		case App.scoringFollowsSouthZone:
			targetSeat = App.deal.hands[App.compassFromPhyScreenPos(Zzz.South)].compass;
			break;
		}
		int targetAxis = targetSeat % 2;

		cardRectsCache.clear();

		int countWon = 0;
		int countLost = 0;

		int actualTricksPlayed = App.deal.prevTrickWinner.size() - 1;
		int actualCardsPlayed = App.deal.countCardsPlayed();

		int reviewCardIncTricks = App.reviewTrick * 4 + App.reviewCard;

		boolean youSeatDeclarer = App.deal.isYouSeatDeclarerAxis();

		for (int step = 0; step < 13; step++) {
			// note - the zeroth is the leader to the first trick

			if (App.isMode(Aaa.REVIEW_PLAY)) {
				if (reviewCardIncTricks < actualCardsPlayed) { // we WILL want to stop if we have gone to high
					if (step <= actualTricksPlayed) {
						if ((step > App.reviewTrick) || (step == App.reviewTrick && App.reviewCard < 4))
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

			if (step < actualTricksPlayed) { // step starts from 0
				Hand winner = App.deal.prevTrickWinner.get(step + 1);
				won = winner.axis() == targetAxis;
			}
			else {
				if (App.deal.endedWithClaim == false)
					break;
				claimed = true;
				won = (youSeatDeclarer) ? (countWon < App.deal.tricksClaimed) : !(countLost < App.deal.tricksClaimed);
			}

			if (won)
				countWon++;
			else
				countLost++;

			float height = (won) ? cardHeight : cardWidth;
			float width = (won) ? cardWidth : cardHeight;
			float top = (won) ? marginActivityTop : marginActivityTop + (cardHeight - cardWidth);

			// draw a cardback ----------------------------------------------
			rr.setRoundRect(marginLeft + (cardSpacing * step), top, width, height, curve, curve);
			g2.setColor(Color.white);
			g2.fill(rr);

			g2.setColor(claimed ? Aaa.weedyBlack : Color.black);
			g2.setStroke(new BasicStroke(blackLineWidth));
			g2.draw(rr);

			rr.setRoundRect(marginLeft + (cardSpacing * step) + wbw, top + wbw, width - 2 * wbw, height - 2 * wbw, curve, curve);
			g2.setColor(claimed ? Aaa.cardBackColorClm : Aaa.cardBackColor);
			g2.fill(rr);

			/* cache the hittest version */
			cardRectsCache.add(new Rectangle2D.Float(marginLeft + (cardSpacing * step), top, width, height - scoreLozengeHeight));
		}

		RoundRectangle2D rd = new RoundRectangle2D.Float(scoreLozengeX, scoreLozengeY, scoreLozengeWidth, scoreLozengeHeight, curve, curve);

		// Fill the scorelozenge background
		// --------------------------------------------------
		g2.setColor(Aaa.scoreBkColor);
		g2.setStroke(new BasicStroke(blackLineWidth * 1.3f));
		g2.fill(rd);
		g2.setColor(Color.white);
		g2.draw(rd);

		// Player Direction (compass) text
		// ------------------------------------------------------------------

		x = scoreLozengeX;
		y = scoreLozengeY + scoreLozengeHeight * (1.0f - 0.23f);

		int contractLevel = App.deal.contract.getLevel();
		int contractSuit = App.deal.contract.getSuit();
		int contractCompass = App.deal.contractCompass;

		Font cardFaceFont = BridgeFonts.faceAndSymbFont.deriveFont(scoreLozengeHeight * 0.8f);
		Font suitSymbolsFont = BridgeFonts.faceAndSymbFont.deriveFont(scoreLozengeHeight * 0.70f);
		Font stdTextFont = BridgeFonts.bridgeLightFont.deriveFont(scoreLozengeHeight * 0.6f);
		Font scoreFont = BridgeFonts.bridgeBoldFont.deriveFont(scoreLozengeHeight * 0.8f);
		Font doubleRedoubleFont = BridgeFonts.bridgeBoldFont.deriveFont(scoreLozengeHeight * 0.80f);
		Font claimFont = BridgeFonts.bridgeBoldFont.deriveFont(scoreLozengeHeight * 0.90f);

		if (App.deal.contract.isPass()) {
			x += scoreLozengeWidth * 0.01f;
			g2.setColor(Color.BLACK);
			g2.setFont(stdTextFont);
			g2.drawString("Passed Out", x, y);
		}
		else { // we have a normal contact
			x += scoreLozengeWidth * 0.02f;
			g2.setColor(Color.BLACK);
			g2.setFont(cardFaceFont);
			g2.drawString(Zzz.level_to_levelSt[contractLevel] + "", x, y);

			x += scoreLozengeWidth * 0.065f;
			g2.setColor(Aaa.cdhsColors[contractSuit]);
			g2.setFont(suitSymbolsFont);
			g2.drawString(Zzz.suit_to_cdhsntSt[contractSuit], x, y);

			{ // Double and redouble or by
				g2.setColor(Color.BLACK);
				float x2 = x + scoreLozengeWidth * 0.12f;
				String dblRdbl = App.deal.doubleOrRedoubleString();
				if (dblRdbl.length() > 0) {
					g2.setFont(doubleRedoubleFont);
					g2.drawString(dblRdbl, x2, y + scoreLozengeWidth * 0.022f);
				}
				else {
					g2.setFont(stdTextFont);
					g2.drawString("by", x2, y);
				}
			}

			x += scoreLozengeWidth * 0.22f;
			g2.setColor(Color.BLACK);
			g2.setFont(scoreFont);
			g2.drawString(Zzz.compass_to_nesw_st[contractCompass], x, y);
		}

		// Now we do the tricks won so far
		Point score = new Point(countWon, countLost); // declarers count is always in the 'x' value
		int firstPairTrickCount = score.x;
		int secondPairTrickCount = score.y;
		String firstPair = Zzz.compass_to_ns_ew_st[targetAxis];
		String secondPair = Zzz.compass_to_ns_ew_st[((targetAxis + 1) % 2)];
		if ((App.deal.getTheYouSeat() % 2) == targetAxis) {
			firstPair = "You";
		}
		else {
			secondPair = "You";
		}

		x = scoreLozengeX + scoreLozengeWidth * 0.43f; // jump to the middle of the lozenge
		g2.setFont(stdTextFont);
		g2.drawString(String.format("%s", firstPair), x, y);

		x += scoreLozengeWidth * 0.15f;
		g2.setFont(scoreFont);
		g2.drawString(String.format("%d", firstPairTrickCount), x, y);

		x += scoreLozengeWidth * 0.15f;
		g2.setFont(stdTextFont);
		g2.drawString(String.format("%s", secondPair), x, y);

		x += scoreLozengeWidth * 0.14f;
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
		float marginTop = panelHeight * 0.01f;

		cardWidth = activityWidth * 0.21f;
		cardHeight = activityWidth * 0.135f;
		curve = cardHeight * 0.55f;

		float colorLineWidth = activityWidth * 0.0095f;

		float faceFontSize = cardHeight * 0.85f;
		float symbolFontSize = cardHeight * 0.60f;

		Font faceFont = BridgeFonts.faceAndSymbFont.deriveFont(faceFontSize);
		Font symbolFont = BridgeFonts.faceAndSymbFont.deriveFont(symbolFontSize);

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
		new Point2D.Float(0.00f, 0.28f) /* W */};

		if (trickRequested + 1 >= App.deal.prevTrickWinner.size()) {
			// we just assume that this is the result of asking to show a claim trick

			float left = marginLeft + activityWidth * leftTopPercent[Zzz.South].x + ((float) trickRequested) / 13.0f * activityWidth * 0.52f;
			float bot = marginTop + activityHeight * 1.2f * leftTopPercent[Zzz.South].y;
			g2.setColor(Aaa.cardBackColor);
			g2.setFont(claimFont);
			g2.drawString(" claim", left, bot);

		}
		else {
			Hand trickWinner = App.deal.prevTrickWinner.get(trickRequested + 1);
			Hand leader = App.deal.prevTrickWinner.get(trickRequested);

			for (int compass : Zzz.rota[leader.compass]) {
				Hand hand = App.deal.hands[compass];
				assert (compass == hand.compass);

				Card card = hand.played.get(trickRequested);
				int suit = card.getSuit();
				int rank = card.getRank();

				int phyPos = App.phyScreenPosFromCompass(hand.compass);
				float left = marginLeft + activityWidth * leftTopPercent[phyPos].x + ((float) trickRequested) / 13.0f * activityWidth * 0.52f;
				float top = marginTop + activityHeight * leftTopPercent[phyPos].y;

				float symbolLeft = left + boarderLeft;
				float symbolBottom = top + cardHeight - symbolBoarderBot;

				float faceLeft = left + boarderInLeft;
				float faceBottom = top + cardHeight - faceBoarderBot;

				// fill the lozenge ----------------------------------------------
				rr.setRoundRect(left, top, cardWidth, cardHeight, curve, curve);

				g2.setPaint(Color.white);
				g2.fill(rr);

				g2.setColor(Aaa.cdhsWeakColors[suit]);

				g2.setStroke(new BasicStroke(colorLineWidth * ((hand == trickWinner) ? 1.95f : 1)));
				g2.draw(rr);

				// Suit Value
				{
					g2.setColor(Aaa.cdhsWeakColors[suit]);
					g2.setFont(symbolFont);
					g2.drawString(Zzz.suit_to_cdhsntSt[suit], symbolLeft, symbolBottom);
				}

				// Face Value
				{
					g2.setColor(Aaa.cdhsColors[suit]);
					g2.setFont(faceFont);
					g2.drawString(Zzz.rank_to_rankSt[rank] + "", faceLeft, faceBottom);
				}

			}
		}
	}

}
