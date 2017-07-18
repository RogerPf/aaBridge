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
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.RoundRectangle2D.Float;

import javax.swing.JPanel;
import javax.swing.Timer;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.Aaf;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.model.Card;
import com.rogerpf.aabridge.model.Cc;
import com.rogerpf.aabridge.model.Dir;
import com.rogerpf.aabridge.model.Hand;
import com.rogerpf.aabridge.model.Rank;
import com.rogerpf.aabridge.model.Suit;
import com.rpsd.bridgefonts.BridgeFonts;

/**   
 */
public class TricksFourDisplayPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public boolean showCompletedTrick = false;
	public boolean showThinBox = false;
	// public boolean finalCardSpecial = false;

	int endOfTrickDownCounter = 0;

	// -----------------------------
	class Suggestion {
		Rank rank;
		Suit suit;

		Suggestion() { /* Constructor */
			clear();
		}

		void clear() {
			rank = Rank.Invalid;
			suit = Suit.Invalid;
		}
	}

	Suggestion[] suggestions = new Suggestion[] { new Suggestion(), new Suggestion(), new Suggestion(), new Suggestion() };

//	public void dealLoadedNotification() {
//		showThinBox = false;
//		for (Suggestion suggestion : suggestions) {
//			suggestion.clear();
//		}
//	}

	/**
	 */
	TricksFourDisplayPanel() { /* Constructor */
		setOpaque(false);

		setVisible(false);
	}

	/**
	*/
	public Timer normalTrickDisplayTimer = new Timer(App.playPluseTimerMs, new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			if (App.frame.isSplashTimerRunning())
				return; // wait until the splash has cleared
			normalTrickDisplayTimer.stop();
			normalTrickDisplayTimer.setInitialDelay(App.playPluseTimerMs);

			// App.deal.hideFinish = false; // just in case

			if (App.deal.isPlaying() && App.isMode(Aaa.NORMAL_ACTIVE)) {
				Hand hand = App.deal.getNextHandToPlay();

				if (showCompletedTrick /* && App.deal.haveAllHandsPlayedTheSameNumberOfCards() */) {

					// End of trick - click to continue
					if (App.isPauseAtEotClickWanted() && App.isAutoPlay(hand.compass)) {
						// go around again - we are waiting for showCompletedTrick to be cleared by mouse click
						App.gbp.c1_1__tfdp.normalTrickDisplayTimer_startIfNeeded();
						return;
					}

					if (App.youAutoplayFAST && App.youAutoplayAlways && !App.youAutoplayPause) {
						endOfTrickDownCounter = 0;
						showCompletedTrick = false;
						App.gbp.c1_1__tfdp.normalTrickDisplayTimer_FAST_startIfNeeded();
						return;
					}

					if (App.isAutoPlay(hand.compass) && (App.eotExtendedDisplay > 0)) {
						if (endOfTrickDownCounter == 0) {
							endOfTrickDownCounter = 1 + App.eotExtendedDisplay;
						}

						if (--endOfTrickDownCounter > 0) {
							App.gbp.c1_1__tfdp.normalTrickDisplayTimer_startIfNeeded();
							return;
						}
						showCompletedTrick = false;
					}
				}

				int played = App.deal.countCardsPlayed();
				if (played == 52) {
					return; // happens because we are messing with 'isFinished()'
				}

				if (played == 51) {
					App.deal.hideFinish = true;
				}

				if (App.isAutoPlay(hand.compass)) {
					App.con.autoPlayRequest(hand);
				}
				else {
					App.con.selfPlayOpportunity(hand);
					showThinBox = App.isVmode_InsideADeal(); // was true;
					App.frame.repaint();
				}

				played = App.deal.countCardsPlayed();
				if (played == 52) {
					App.deal.hideFinish = true; // Yes well - I know it is already set to true
					finalCardUnDisplayTimer.start();
				}
				else if (App.deal.hideFinish == true) {
					App.deal.hideFinish = false; // the self play opportunity did not happen
					// App.frame.repaint();
				}

			}
		}
	});

	/**
	*/
	public Timer finalCardUnDisplayTimer = new Timer(App.finalCardTimerMs, new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			finalCardUnDisplayTimer.stop();
			// finalCardUnDisplayTimer.setInitialDelay(App.finalCardTimerMs);
			App.deal.hideFinish = false;
			App.gbp.matchPanelsToDealState();
			App.frame.repaint();
		}
	});

	/**
	*/
	public Timer reviewTrickDisplayTimer = new Timer(App.playPluseTimerMs, new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			reviewTrickDisplayTimer.stop();
			reviewTrickDisplayTimer.setInitialDelay(App.playPluseTimerMs);

			if (App.isMode(Aaa.REVIEW_PLAY)) {
				App.con.reviewTrickDisplayTimerFired();
			}
		}
	});

	/**
	 */
	public void normalTrickDisplayTimer_startIfNeeded() {
		if (App.deal.isPlaying()) {
			normalTrickDisplayTimer.setInitialDelay(App.playPluseTimerMs);
			normalTrickDisplayTimer.setDelay(App.playPluseTimerMs);
			normalTrickDisplayTimer.start();
			showThinBox = (App.isMode(Aaa.EDIT_PLAY)); // was false;
		}
	}

	/**
	 */
	public void normalTrickDisplayTimer_FAST_startIfNeeded() {
		if (App.deal.isPlaying()) {
			normalTrickDisplayTimer.stop();
			normalTrickDisplayTimer.setInitialDelay(0);
			normalTrickDisplayTimer.setDelay(0);
			normalTrickDisplayTimer.start();
			showThinBox = (App.isMode(Aaa.EDIT_PLAY)); // was false;
		}
	}

	/**   
	 */
	public Rank getSuggestedRank(Dir phyScreenPos) {
		Dir compass = App.cpeFromPhyScreenPos(phyScreenPos);
		return suggestions[compass.v].rank;
	}

	/**   
	 */
	public void setSuggestedRank(Dir phyScreenPos, Rank rank) {
		Dir compass = App.cpeFromPhyScreenPos(phyScreenPos);
		suggestions[compass.v].rank = rank;
	}

	/**   
	 */
	public Suit getSuggestedSuit(Dir phyScreenPos) {
		Dir compass = App.cpeFromPhyScreenPos(phyScreenPos);
		return suggestions[compass.v].suit;
	}

	/**   
	 */
	public void setSuggestedSuit(Dir phyScreenPos, Suit suit) {
		Dir compass = App.cpeFromPhyScreenPos(phyScreenPos);
		suggestions[compass.v].suit = suit;
	}

	/**   
	 */
	public void dealDirectionChange() {
	}

	/**   
	 */
	public void dealMajorChange() {
		clearAllCardSuggestions();
	}

	/**   
	 */
	public void clearAllCardSuggestions() {
		showThinBox = false;
		for (Suggestion sug : suggestions) {
			sug.clear();
		}
	}

	/**   
	 */
	public void makeCardSuggestions() {
		Hand hand = App.deal.getNextHandToPlay();
		if (hand == null)
			return; // end of board?
		Suit suit = hand.makeSuitSuggestion();
		// if (suit > -1) {
		suggestions[hand.compass.v].suit = suit;
		// }
	}

	/**   
	 */
	public void clearShowCompletedTrick() {
		if (showCompletedTrick) {
			showCompletedTrick = false;
			App.frame.repaint();
		}
	}

	public void toggleShowCompletedTrick_passive(boolean state) {
		showCompletedTrick = state;
	}

	/**   
	 */
	public void setShowCompletedTrick() {
		showCompletedTrick = true;
	}

	/**   
	 */
	public void paintComponent(Graphics g) { // TrickDisplayPanel

		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;
		Aaa.commonGraphicsSettings(g2);

		Dimension panelSize = getSize();

		float panelWidth = (float) panelSize.width;
		float panelHeight = (float) panelSize.height;

		if (App.deal.isDoneHand()) {

			RoundRectangle2D.Float rr = new RoundRectangle2D.Float();

			float marginLeft = panelWidth * 0.09f;
			float marginTop = panelHeight * 0.18f;
			float marginBottom = panelHeight * 0.18f;
			float curve = panelWidth * 0.00f;

			float activityWidth = panelWidth - (marginLeft * 2);
			float activityHeight = panelHeight - (marginTop + marginBottom);

			float fontSize = activityHeight * 0.37f;

			g2.setColor(Cc.BlueStrong);
			// fill the lozenge ----------------------------------------------
			rr.setRoundRect(marginLeft, marginTop, activityWidth, activityHeight, curve, curve);
			g2.draw(rr);
			g2.fill(rr);

			g2.setFont(BridgeFonts.internatBoldFont.deriveFont(fontSize * 0.80f));
			g2.setColor(Color.WHITE);

			// boolean showInstructions = App.isVmode_InsideADeal();
			Aaa.drawCenteredString(g2, Aaf.playBridge_click, marginLeft, marginTop, activityWidth, activityHeight * 0.5f);

			g2.setFont(BridgeFonts.internatBoldFont.deriveFont(fontSize));

			Aaa.drawCenteredString(g2, Aaf.playBridge_newBoard, marginLeft, marginTop + activityHeight * 0.5f, activityWidth, activityHeight * 0.4f);

			return;
		}

		// preset the as many common values as possible
		// ----------------------------------------------

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
		float colorLineWidth = activityWidth * 0.010f;

		float faceFontSize = cardHeight * 0.78f;
		float symbolFontSize = cardHeight * 0.50f;

		Font faceFont = BridgeFonts.faceAndSymbolFont.deriveFont(faceFontSize);
		Font faceInternationalFont = BridgeFonts.internatBoldFont.deriveFont(faceFontSize * 0.95f);
		Font symbolFont = BridgeFonts.faceAndSymbolFont.deriveFont(symbolFontSize);

		// ------------------------------------------------------------------

		float boarderLeft = cardWidth * 0.17f;
		float boarderInLeft = cardWidth * 0.45f;
		float symbolBoarderBot = cardHeight * 0.28f;
		float faceBoarderBot = cardHeight * 0.20f;

		RoundRectangle2D.Float rr = new RoundRectangle2D.Float();

		// We display the cards as seen by the player in the physical Pos
		// 'South' seat (the target)

		int trickRequested = 0;
		Hand trickWinner = null;

		if (App.isMode(Aaa.REVIEW_PLAY)) {
			trickRequested = App.reviewTrick;
			if (trickRequested < 0)
				return;
			if (App.reviewCard % 4 == 0) {
				// 1362 the "+1" was removed
				// 1407 the "+1" below was restored - the issue is that the
				// winning card is being MISS displayed - assume that the value of
				// trickRequested is being 'defined' elsewhere !!!
				// OR
				// There issue appears to be about hands that are claimed during the first trick
				// so there is never a 'normal' winner of the first trick
				// We currently just return the declarer as the winner - lin files need better checking?
				if (App.deal.prevTrickWinner.size() > trickRequested + 1)
					trickWinner = App.deal.prevTrickWinner.get(trickRequested + 1);
				else
					trickWinner = App.deal.hands[App.deal.contractCompass.v]; // declarer
			}
		}
		else { // normal mode
			trickRequested = App.deal.getCurTrickIndex();

			if (!App.deal.tc_suppress_pc_display && showCompletedTrick && (trickRequested > 0) && App.deal.isCurTrickComplete()) {
				trickWinner = App.deal.prevTrickWinner.get(trickRequested);
				trickRequested--;
			}
		}

		// Display the 'eot click required' indication (a dot)
		if (App.isVmode_InsideADeal() && showCompletedTrick /* && App.deal.isCurTrickComplete() */&& App.isMode(Aaa.NORMAL_ACTIVE)
				&& App.isPauseAtEotClickWanted() && App.isAutoPlay(App.deal.getNextHandToPlay().compass)) {

			g2.setColor(Cc.g(Cc.rpfDefBtnColor));
			double diameter = activityWidth * 0.04;

			double x = marginLeft + activityWidth * 0.04;
			double y = marginTop + activityHeight * 0.90;
			Ellipse2D.Double circle = new Ellipse2D.Double(x, y, diameter, diameter);
			g2.fill(circle);

			x = marginLeft + activityWidth * 0.94;
			y = marginBottom + activityHeight * 0.04;
			circle = new Ellipse2D.Double(x, y, diameter, diameter);
			g2.fill(circle);

//			x = marginLeft + activityWidth * 0.04;
//			y = marginBottom + activityHeight * 0.04;
//			circle = new Ellipse2D.Double(x, y, diameter, diameter);
//			g2.fill(circle);
//
//			x = marginLeft + activityWidth * 0.94;
//			y = marginBottom + activityHeight * 0.94;
//			circle = new Ellipse2D.Double(x, y, diameter, diameter);
//			g2.fill(circle);
		}

		// System.out.println( trickRequested );

		Point2D.Float leftTopPercent[] = new Point2D.Float[] { new Point2D.Float(0.38f, 0.00f), /* N */
		new Point2D.Float(0.61f, 0.28f), /* E */
		new Point2D.Float(0.38f, 0.57f), /* S */
		new Point2D.Float(0.16f, 0.28f) /* W */};

		if (trickRequested < 0)
			return; // a virgin deal ! with no cards? no nothing

		Hand leader = App.deal.prevTrickWinner.get(trickRequested);

		boolean review_outline = false;

		int revInd = -1;
		for (Dir compass : Dir.rota[leader.compass.v]) {
			revInd++;
			if (App.isMode(Aaa.REVIEW_PLAY) && (revInd >= App.reviewCard + 1) || App.deal.dfcDeal) {
				return;
			}
			if (App.isMode(Aaa.REVIEW_PLAY) && (revInd == App.reviewCard)) {
				review_outline = true;
				if (App.gbp.c1_1__tfdp.reviewTrickDisplayTimer.isRunning())
					return;
			}

			Hand hand = App.deal.hands[compass.v];
			assert (compass.v == hand.compass.v);

			Dir phyPos = App.phyScreenPosFromCompass(hand.compass);

			Card card = null;
			if ((trickRequested < hand.played.size())) {
				card = hand.played.get(trickRequested);
			}

			if (review_outline) {
				card = null;
			}

			// @formatter:off
			if ( (card == null) && !review_outline &&
					(    App.isAutoPlay(hand.compass) 
					|| ( App.deal.getNextHandToPlay() != hand) 
					|| ((App.deal.getNextHandToPlay() == hand) && (showThinBox == false))
					)
			   ) {
				return;
			}
			// @formatter:on

			Rank rank = Rank.Invalid;
			Suit suit = Suit.Invalid;

			if (App.isMode(Aaa.NORMAL_ACTIVE) || App.isMode(Aaa.EDIT_PLAY)) {
				rank = suggestions[hand.compass.v].rank;
				suit = suggestions[hand.compass.v].suit;
			}

			if (card != null) {
				suggestions[hand.compass.v].rank = Rank.Invalid;
				suggestions[hand.compass.v].suit = Suit.Invalid;
				rank = card.rank;
				suit = card.suit;
			}

			float left = marginLeft + activityWidth * leftTopPercent[phyPos.v].x;
			float top = marginTop + activityHeight * leftTopPercent[phyPos.v].y;

			float symbolLeft = left + boarderLeft;
			float symbolBottom = top + cardHeight - symbolBoarderBot;

			float faceLeft = left + boarderInLeft;
			float faceBottom = top + cardHeight - faceBoarderBot;

			if (card == null) {
				// Shrink the the thin line lozenge as it can look too big -
				// Optical illusion
				float pc = 0.02f;
				left += cardWidth * pc;
				cardWidth -= cardWidth * 2f * pc;
				top += cardHeight * pc;
				cardHeight -= cardHeight * 2f * pc;
			}

			// fill the lozenge ----------------------------------------------
			rr.setRoundRect(left, top, cardWidth, cardHeight, curve, curve);

			if (card != null) {
				g2.setPaint(Color.WHITE);
				g2.fill(rr);
			}

			float stkSize = (card == null) ? blackLineWidth : (colorLineWidth * ((hand == trickWinner) ? 2.5f : 1));
			if (App.outlineCardEdge && card != null) {
				RoundRectangle2D.Float rr2 = (Float) rr.clone();
				g2.setColor(Aaa.handBkColorStd);
				g2.setStroke(new BasicStroke(stkSize + colorLineWidth * 0.80f));
				g2.draw(rr2);
			}

			if (review_outline) { // faint dots added to the review play back so the user knows the mode they are in
				g2.setColor(Cc.BlackStrong);
				float dash[] = { 0.006f * cardWidth, 0.05f * cardWidth };
				g2.setStroke(new BasicStroke(blackLineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 2.0f, dash, 2.0f));
			}
			else {
				g2.setColor((suit == Suit.Invalid) ? Cc.NoChosenSuit : suit.color(Cc.Ce.Weak));
				g2.setStroke(new BasicStroke(stkSize));
			}
			g2.draw(rr);

			Color cardColor = (suit == Suit.Invalid) ? Cc.NoChosenSuit : suit.color(Cc.Ce.Weak);

			// Suit
			if (suit != Suit.Invalid && Suit.Clubs.v <= suit.v && suit.v <= Suit.Spades.v) {
				g2.setColor((card == null) ? cardColor : suit.color(Cc.Ce.Weak));
				g2.setFont(symbolFont);
				g2.drawString(suit.toStrLower(), symbolLeft, symbolBottom);
			}

			// Rank
			if (Rank.Two.v <= rank.v && rank.v <= Rank.Ace.v) {
				g2.setColor((card == null) ? cardColor : suit.colorCd(Cc.Ce.Strong));
				char c = Rank.rankToLanguage(rank.toChar());
				g2.setFont(Aaa.isLatinFaceCard(c) ? faceFont : faceInternationalFont);
				g2.drawString(c + "", faceLeft, faceBottom);
			}
		}
	}
}
