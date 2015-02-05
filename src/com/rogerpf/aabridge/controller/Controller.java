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
import java.io.File;

import javax.swing.Timer;

import com.rogerpf.aabridge.controller.Book.LinChapter;
import com.rogerpf.aabridge.igf.MassGi_utils;
import com.rogerpf.aabridge.model.Bid;
import com.rogerpf.aabridge.model.Call;
import com.rogerpf.aabridge.model.Card;
import com.rogerpf.aabridge.model.Cc;
import com.rogerpf.aabridge.model.Deal.DumbAutoDirectives;
import com.rogerpf.aabridge.model.Dir;
import com.rogerpf.aabridge.model.Hand;
import com.version.VersionAndBuilt;

/**   
 */
public class Controller implements KeyEventDispatcher, ActionListener {
	// ---------------------------------- CLASS -------------------------------------

	private static char previous_char = 0;
	private static long previous_time = 0;

	DumbAutoDirectives dumbAutoDir = new DumbAutoDirectives();

	/**
	 * This ia a logical 'constructor' except that is does not happen until all
	 * the initial views are constructed and ready to be "controled"
	 */
	public Timer postContructionInitTimer = new Timer(100, new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			// =============================================================================
			postContructionInitTimer.stop();

			if (App.runTestsAtStartUp && App.devMode) {
				CmdHandler.runTests();
			}

			boolean chapterLoaded = false;
			boolean keepTrying = true;

			// Is there a file on the command line - as used by windows file associations
			if (App.args != null && App.args.length >= 1 && App.args[0] != null && !App.args[0].isEmpty()) {
				File file = BridgeLoader.copyFileToAutoSavesFolderIfLinFileExists(App.args[0]);
				if (file != null) {
					File fAy[] = { file };
					chapterLoaded = BridgeLoader.processDroppedList(fAy);
				}
				keepTrying = !chapterLoaded; // the user will be expecting her/his file to load so we should not do something else ?
			}

			// Is there a (dev mode) linFile we want to start with
			//
			if (App.devMode && (chapterLoaded == false) && keepTrying) {
				String dealName = "";

//				App.lbx_modeExam = true; // testing only
//				dealName = "Distr Flash Cards"'

//				dealName = "weaktwobids";
//				dealName = "mentoring1406a2";
//				dealName = "book into jar";
//				dealName = "Make Claim";
//				dealName = "Play Card";
//				dealName = "Table Conceal";
//				dealName = "Speech bubble";
//				dealName = "LinesZonesFonts";
//				dealName = "mentoring 2013 06c";
//				dealName = "bergenhandevaluation";

				if (dealName.length() > 0) {
					for (Bookshelf shelf : App.bookshelfArray) {
						for (Book book : shelf) {
							LinChapter chapter = book.getChapterByDisplayNamePart(dealName);
							if (chapter != null) {
								chapterLoaded = chapter.loadWithShow("replaceBookPanel");
								break;
							}
						}
					}
					keepTrying = false;// the DEV user (you) will be expecting her/his file to load so we should not do something else ?
				}
			}

			/**
			 *  look at the user set start-up option
			 */

			if (chapterLoaded == false && keepTrying) {
				ShowHelpAndWelcome();
			}

//			if (App.devMode) {
//				CmdHandler.mainNewBoard();
//			}

			App.frame.splitPaneHorz.setDividerLocation(App.horzDividerLocation);
			App.frame.splitPaneVert.setDividerLocation(App.vertDividerLocation);
			App.frame.rop.setSelectedIndex(App.ropSelPrevTabIndex);

			App.gbp.matchPanelsToDealState();
			// App.gbp.c2_2__bbp.startAutoBidDelayTimerIfNeeded();
			if (App.deal.isPlaying()) {
				App.gbp.c1_1__tfdp.makeCardSuggestions(); // for the "test" file if loaded
			}
			App.gbp.c1_1__tfdp.normalTrickDisplayTimer_startIfNeeded();

			App.dealMajorChange();
			App.frame.invalidate();

			App.frame.setMinimumSize(new Dimension(300, 200));
			App.frame.setSize(App.frameWidth, App.frameHeight);
			App.frame.setLocation(App.frameLocationX, App.frameLocationY);

			if (App.maximized) {
				App.frame.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
			}

			if (VersionAndBuilt.hasExpired()) {
				App.frame.aaDragGlassPane.showExpiredScreen();
			}

			App.allConstructionComplete = true;

			Cc.intensitySliderChange();

			App.colorIntensityChange();

			App.frame.setVisible(true);

			controlerInControl();

			if (App.isVmode_Tutorial()) {
//				rattleTimer.start();
			}
		}
	});

	static int tutRattle = 3;
	/**
	 */
	Timer rattleTimer = new Timer(10, new ActionListener() {
		public void actionPerformed(ActionEvent evt) {

			App.frame.rrp.setVisible(false);

			App.frame.rrp.setVisible(tutRattle % 2 == 1);
			App.frame.brp.setVisible(tutRattle % 2 == 1);

			if (tutRattle <= 0)
				rattleTimer.stop();
			tutRattle--;
		}

	});

	/**   
	 */
	public void stopAllTimers() {
		// =============================================================================
		App.gbp.c1_1__tfdp.normalTrickDisplayTimer.stop();
		App.gbp.c1_1__tfdp.reviewTrickDisplayTimer.stop();
		App.gbp.c1_1__bfdp.reviewBidDisplayTimer.stop();
		App.gbp.c1_1__bfdp.biddingCompleteDelayTimer.stop();
		App.gbp.c1_1__bfdp.biddingCompleteDelayTimer_part2.stop();

		App.mg.tutorialPlayTimer.stop();

		// postContructionInitTimer.stop(); never this one
		// App.gbp.c1_1__tfdp.finalCardUnDisplayTimer.stop(); not this one
		// floatingHandExtraDisplayTimer.stop(); not this one
		// afterPlpShakerTimer.stop(); or this one

		App.con.autoBidDelayTimer.stop();
	}

	public void ShowHelpAndWelcome() {

		Book b = App.bookshelfArray.get(0).getBookByFrontNumb(90 /* The Main Welcome & Help */);
		if (b != null) {
			boolean chapterLoaded = b.loadChapterByIndex(0);
			if (chapterLoaded) {
				App.book = b;
				App.bookPanel.matchToAppBook();
				App.bookPanel.showChapterAsSelected(0);
			}
		}
	}

	/**   
	 */
	public boolean dispatchKeyEvent(KeyEvent e) {
		// =============================================================================
		if (App.saveDialogShowing || App.gbp.c0_0__tlp.descEntry.hasFocus()) {
			return false;
		}

		int keyCode = e.getKeyCode();
		char c = e.getKeyChar();

		if ((c == previous_char) && (previous_time + 500 > e.getWhen()))
			return false;

		previous_char = c;
		previous_time = e.getWhen();

		// @formatter:off
		switch (keyCode) {
			case KeyEvent.VK_LEFT: Left_keyPressed(); return true;
			case KeyEvent.VK_RIGHT: Right_keyPressed(); return true;
			case KeyEvent.VK_UP: Up_keyPressed(); return true;
			case KeyEvent.VK_DOWN: Down_keyPressed(); return true;
		}
		// @formatter:on

		if (App.deal.isPlaying())
			App.gbp.c1_1__tfdp.clearShowCompletedTrick();

		App.gbp.hideClaimButtonsIfShowing();

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

	/** ****************************************************************************************** 
	 * This is the core command action point  <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 
	 */
	public void actionPerformed(ActionEvent e) {
		// =============================================================================
		CmdHandler.actionPerfString(e.getActionCommand());
		controlerInControl();
	}

	/**   
	 */
	public void setDragImage(BufferedImage image) {
		// =============================================================================
		App.frame.setDragImage(image);
	}

	/**
	 */
	public void reviewTrickDisplayTimerFired() {
		// =============================================================================
		CmdHandler.reviewTrickDisplayTimerFired();
	}

	/**
	 */
	public void reviewTimeRequestToShowTrick(int trickIndex) {
		// =============================================================================
		CmdHandler.reviewTimeRequestToShowTrick(trickIndex);
	}

	/**   
	 */
	public void reviewBidDisplayTimerFired() {
		// =============================================================================
		CmdHandler.reviewBidDisplayTimerFired();
	}

	/**   
	 */
	public void voiceTheBid(Bid b) {
		// =============================================================================
		// System.out.println(b);

		/**
		 * Called from normal bidding in the play of a hand
		 */
		if ((App.visualMode == App.Vm_InsideADeal) && (App.mode == Aaa.NORMAL_ACTIVE || App.mode == Aaa.EDIT_BIDDING)) {

			if (b != null) { // real bid
				App.deal.makeBid(b);
				if (App.deal.isAuctionFinished()) {
					App.calcCompassPhyOffset();
					App.gbp.dealDirectionChange();
					if (App.deal.contract.isPass())
						voiceTheBid(null); // recursive
					else
						App.gbp.c1_1__bfdp.biddingCompleteDelayTimer.start();
					return;
				}
				startAutoBidDelayTimerIfNeeded();
				App.gbp.c0_2__blp.matchPanelsToDealState();
				App.frame.repaint();
				return;
			}

			// the null bid shows we have just transitioned into playing

			assert (App.deal.isAuctionFinished());
			App.gbp.matchPanelsToDealState();

			App.gbp.c1_1__tfdp.normalTrickDisplayTimer_startIfNeeded();

			App.frame.repaint();
			return;
		}

		/**
		 * Called from a bidding question in the tutorial
		 */
		if ((App.visualMode == App.Vm_DealAndTutorial) || (App.visualMode == App.Vm_TutorialOnly)) {

			MassGi_utils.callback_questionAnswered(b);

			@SuppressWarnings("unused")
			int z = 0;
		}
	}

	/**
	 */
	public void cardSelected(Hand curHand, Card playedCard) {
		// =============================================================================
		/**
		 *  called from a handDisplayPanel a mouse click
		 */
		if (App.isVmode_InsideADeal()) {
			/** 
			 * A request to play the card
			 */
			tableTheCard(curHand, playedCard);
			return;
		}

		/**
		 *  A question has been answered, a 'choose a card' question
		 *  or a pick, one of two hands, question.
		 */

		MassGi_utils.callback_questionAnswered(curHand, playedCard);
	}

	/**
	 */
	public static void tutorialAnswerButtonClicked(String ans) {
		// =============================================================================
		/**
		 *  called from the question panel
		 */

		/**
		 *  A question has been answered, a 'choose a card' question
		 *  or a pick, one of two hands, question.
		 */

		MassGi_utils.callback_questionAnswered(ans);
	}

	/**
	 */
	public void selfPlayOpportunity(Hand hand) {
		// =============================================================================
		assert (App.isAutoPlay(hand.compass) == false); // only non autoplay hands are allowed to call this

		if (App.isVmode_Tutorial())
			return;

		Card card = null;

		if (App.youAutoSingletons && App.isMode(Aaa.NORMAL_ACTIVE)) {
			card = hand.getSelfPlayableCard(App.youAutoAdjacent);
		}

		if (card != null) {
			App.con.tableTheCard(hand, card);
		}
	}

	/**
	 */
	public void autoPlayRequest(Hand hand) {
		// =============================================================================
		dumbAutoDir.yourFinessesMostlyFail = App.yourFinessesMostlyFail;
		dumbAutoDir.defenderSignals = App.defenderSignals;

		Card card = hand.dumbAuto(dumbAutoDir);

		App.con.tableTheCard(hand, card);
	}

	/**
	 */
	public void tableTheCard(Hand curHand, Card playedCard) {
		// =============================================================================
		curHand.playCard(playedCard);

		if (App.deal.isFinished()) {
			App.gbp.c1_1__tfdp.setShowCompletedTrick();
			App.gbp.matchPanelsToDealState();
			App.frame.repaint();
			return;
		}

		App.gbp.c0_2__blp.matchPanelsToDealState();

		App.gbp.c1_1__tfdp.makeCardSuggestions();

		if (App.deal.isCurTrickComplete()) {
			App.gbp.c1_1__tfdp.setShowCompletedTrick();
		}

		App.gbp.c1_1__tfdp.normalTrickDisplayTimer_startIfNeeded();

		App.frame.repaint();
	}

	/**
	 */
	public void startAutoBidDelayTimerIfNeeded() {
		// =============================================================================
		if (App.deal.isBidding()) {
			Hand hand = App.deal.getNextHandToBid();
			if (App.isAutoBid(hand.compass)) {
				autoBidDelayTimer.start();
			}
			App.gbp.c2_2__empt.setVisible(App.isAutoBid(hand.compass) == true);
			App.gbp.c2_2__bbp.setVisible(App.isAutoBid(hand.compass) == false);
		}
	}

	/**
	*/
	public Timer autoBidDelayTimer = new Timer(App.bidPluseTimerMs, new ActionListener() {
		// =============================================================================
		public void actionPerformed(ActionEvent evt) {
			if (App.frame.isSplashTimerRunning())
				return; // wait until the splasHskh has cleared
			autoBidDelayTimer.stop();
			autoBidDelayTimer.setInitialDelay(App.bidPluseTimerMs);
			if (App.deal.isBidding()) {
				Hand hand = App.deal.getNextHandToBid();
				if (App.isAutoBid(hand.compass)) {
					App.gbp.c2_2__bbp.clearHalfBids();
					Bid bid;
					if (hand.compass == Dir.South /* && App.isMode(Aaa.NORMAL) */) {
						bid = App.deal.generateSouthBid(App.dealCriteria);
					}
					else if (hand.compass == Dir.North) {
						bid = new Bid(Call.Pass);
					}
					else {
						bid = App.deal.generateEastWestBid(hand);
					}
					App.con.voiceTheBid(bid);
				}
			}
		}
	});

	/**
	 */
	public void controlerInControl() {
		// =============================================================================
		/** 
		 * The System can be doing very dfferent things mainly depenting on the type of 
		 * .lin file (if any) that is currently loaded.  The main indication of what is
		 * happening is the current  Aaa.visualMode
		 */

		if (App.visualMode == App.Vm_InsideADeal) {
			/**
			 * We have a simple deal OR multiple simple deals there will always be
			 * a valid deal in App.deal   
			 */

			if (App.deal.isDoneHand())
				/** 
				 * nothing should happening we are just waiting for 
				 *   them to click on - New Deal or load a lin file
				 */
				return;

			if (App.mode == Aaa.NORMAL_ACTIVE) {
				if (App.deal.isBidding()) {
					startAutoBidDelayTimerIfNeeded();
					return;
				}
			}
		}

//		App.calcApplyBarVisiblity();

	}

	public static void loadBookChapter_prev(boolean show_end) {
		// =============================================================================
		int i = App.bookPanel.getLoadedChapterIndex();
		if (i == -1)
			return;

		Boolean success = false;
		while (success == false) {

			if (--i < 0)
				return;

			try {
				success = App.book.loadChapterByIndex(i);
			} catch (Exception e) {
				e.printStackTrace();
				success = false;
			}

			if (success) {
				App.bookPanel.showChapterAsSelected(i);
				if (show_end)
					App.mg.setTheReadPoints_ToEnd();
				break;
			}
			App.bookPanel.showChapterAsBroken(i);
		}
	}

	public static void loadBookChapter_next() {
		// =============================================================================
		int i = App.bookPanel.getLoadedChapterIndex();
		if (i == -1)
			return;

		int tot = App.book.size();

		Boolean success = false;
		while (success == false) {

			if (++i > tot - 1)
				return;

			try {
				success = App.book.loadChapterByIndex(i);
			} catch (Exception e) {
				e.printStackTrace();
				success = false;
			}

			if (success) {
				App.bookPanel.showChapterAsSelected(i);
				break;
			}
			App.bookPanel.showChapterAsBroken(i);
		}
	}

	public static void Left_keyPressed() {
		if (App.isVmode_Tutorial()) {
			App.mg.tutorialBackOne();
		}
		else if (App.isMode(Aaa.REVIEW_PLAY)) {
			CmdHandler.reviewBackOneTrick();
		}
		else if (App.isMode(Aaa.REVIEW_BIDDING)) {
			CmdHandler.reviewBackOneBid();
		}
	}

	public static void Right_keyPressed() {
		if (App.isVmode_Tutorial()) {
			App.mg.tutorialStepFwd();
		}
		else if (App.isMode(Aaa.REVIEW_PLAY)) {
			CmdHandler.reviewFwdOneTrick();
		}
		else if (App.isMode(Aaa.REVIEW_BIDDING)) {
			CmdHandler.reviewFwdOneBid();
		}
	}

	public static void Up_keyPressed() {
		if (App.isVmode_Tutorial()) {
			loadBookChapter_prev(false);
		}
	}

	public static void Down_keyPressed() {
		if (App.isVmode_Tutorial()) {
			loadBookChapter_next();
		}
	}

}
