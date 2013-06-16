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
package com.rogerpf.aabridge.controller;

import java.awt.Dimension;
import java.awt.KeyEventDispatcher;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.Timer;

import com.rogerpf.aabridge.model.Bid;
import com.rogerpf.aabridge.model.Card;
import com.rogerpf.aabridge.model.Hand;

/**   
 */
public class Controller implements KeyEventDispatcher, ActionListener {

	/**
	 */
	public Timer postContructionInitTimer = new Timer(100, new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			postContructionInitTimer.stop();

			//CmdHandler.readDealIfExists("test21.aaBridge"); // <<<<<<<<<<<<<<<<<<< TEST <<<<<<<<<<<<<<

			App.frame.splitPaneHorz.setDividerLocation(App.horzDividerLocation);
			App.frame.splitPaneVert.setDividerLocation(App.vertDividerLocation);
			App.frame.rop.setSelectedIndex(App.ropSelPrevTabIndex);

			App.gbp.matchPanelsToDealState();
			App.gbp.c2_2__bbp.startAutoBidDelayTimerIfNeeded();

			App.gbp.c1_1__tfdp.normalTrickDisplayTimer_startIfNeeded();

			App.gbp.dealMajorChange();
			App.frame.invalidate();

			App.frame.setMinimumSize(new Dimension(300, 200));
			App.frame.setSize(App.frameWidth, App.frameHeight);
			App.frame.setLocation(App.frameLocationX, App.frameLocationY);

			if (App.maximized) {
				App.frame.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
			}

//			// set the outer starting size and position
//			App.frame.setSize(720, 500);
//			App.frame.setLocation(1620, 0);

			if (App.showWelcome) {
				App.frame.aaDragGlassPane.showSplashScreen();
			}

			App.allConstructionComplete = true;
			App.frame.setVisible(true);

			extraTestTimer.start();
		}
	});

	/**
	 */
	Timer extraTestTimer = new Timer(500, new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			extraTestTimer.stop();
//			
//			App.setMode(Aaa.REVIEW_BIDDING);
//			
//			App.gbp.matchPanelsToDealState();
//			App.frame.repaint();
		}
	});

	private static char previous_char = 0;
	private static long previous_time = 0;

	/**   
	 */
	public boolean dispatchKeyEvent(KeyEvent e) {

		char c = e.getKeyChar();

		if (App.gbp.c0_0__tlp.descEntry.hasFocus()) {
			return false;
		}

		if ((c == previous_char) && (previous_time + 500 > e.getWhen()))
			return false;

		previous_char = c;
		previous_time = e.getWhen();

		if (App.deal.isPlaying())
			App.gbp.c1_1__tfdp.ClearShowCompletedTrick();

		// System.out.println( c );
		int cmd = Aaa.cmdFromChar(c);
		if (cmd == 0)
			return false; // it is no use to us

		if (cmd == (Aaa.CMD_ADMIN | 'U')) {
			CmdHandler.actionPerfString("mainUndo");
			return false;
		}

		if (App.deal.isBidding()) {
			App.gbp.c2_2__bbp.keyCommand(cmd);
			return false;
		}

		if (App.deal.isPlaying()) {
			Hand hand = App.deal.getNextHandToAct();
			App.gbp.getHandDisplayPanel(hand).keyCommand(cmd);
			return false;
		}

		return false;
	}

	/**   
	 */
	public void setDragImage(BufferedImage image) {
		App.frame.setDragImage(image);
	}

	/**   
	 */
	public void actionPerformed(ActionEvent e) {
		CmdHandler.actionPerfString(e.getActionCommand());
	}

	/**
	 */
	public void reviewTrickDisplayTimerFired() {
		CmdHandler.reviewTrickDisplayTimerFired();
	}

	/**
	 */
	public void reviewTimeRequestToShowTrick(int trickIndex) {
		CmdHandler.reviewTimeRequestToShowTrick(trickIndex);
	}

	public void reviewBidDisplayTimerFired() {
		CmdHandler.reviewBidDisplayTimerFired();
	}

	/**   
	 */
	public void voiceTheBid(Bid b) {
		// System.out.println(b);

		if (b != null) { // real bid
			App.deal.makeBid(b);
			if (App.deal.isAuctionFinished()) {
				if (App.deal.contract == App.deal.PASS)
					voiceTheBid(null);
				else
					App.gbp.c1_1__bfdp.biddingCompleteDelayTimer.start();
				return;
			}
			App.gbp.c2_2__bbp.startAutoBidDelayTimerIfNeeded();
			App.gbp.c0_2__blp.matchPanelsToDealState();
			App.frame.repaint();
			return;
		}

		// the null bid shows we have just transitioned into playing

		assert (App.deal.isAuctionFinished());
		App.gbp.matchPanelsToDealState();

		App.gbp.c1_1__tfdp.normalTrickDisplayTimer_startIfNeeded();

		App.frame.repaint();
	}

	/**
	 */
	public void selfPlayOpportunity(Hand hand) { // Only the NS get here

		Card card = null;

		if (App.nsAutoSingletons && App.isMode(Aaa.NORMAL)) {
			card = hand.getSelfPlayableCard(App.nsAutoAdjacent);
		}

		if (card == null) {
			if (App.nsAutoplayAlways && App.isMode(Aaa.NORMAL)) {
				card = hand.dumbAuto();
			}
		}

		if (card != null) {
			App.con.tableTheCard(hand, card);
		}
		else {
			App.gbp.c1_1__tfdp.showThinBox = true;
			App.gbp.c1_1__tfdp.repaint();
		}
	}

	/**
	 */
	public void autoPlayRequest(Hand hand) {

		Card card = hand.dumbAuto();
		App.con.tableTheCard(hand, card);
	}

	/**
	 */
	public void tableTheCard(Hand curHand, Card playedCard) {

		curHand.playCard(playedCard);

		if (App.deal.isFinished()) {
			App.gbp.c1_1__tfdp.SetShowCompletedTrick();
			App.gbp.matchPanelsToDealState();
			App.frame.repaint();
			return;
		}

		App.gbp.c0_2__blp.matchPanelsToDealState();

		App.gbp.c1_1__tfdp.makeCardSuggestions();

		if (App.deal.isCurTrickComplete()) {
			App.gbp.c1_1__tfdp.SetShowCompletedTrick();
		}

		App.gbp.c1_1__tfdp.normalTrickDisplayTimer_startIfNeeded();

		App.frame.repaint();
	}

}
