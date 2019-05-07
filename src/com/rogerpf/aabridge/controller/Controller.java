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
import java.io.IOException;
import java.util.Date;

import javax.swing.Timer;

import com.rogerpf.aabridge.controller.Book.LinChapter;
import com.rogerpf.aabridge.dds.Z_ddsCalculate;
import com.rogerpf.aabridge.igf.MassGi_utils;
import com.rogerpf.aabridge.model.Bid;
import com.rogerpf.aabridge.model.Call;
import com.rogerpf.aabridge.model.Card;
import com.rogerpf.aabridge.model.Cc;
import com.rogerpf.aabridge.model.Deal;
import com.rogerpf.aabridge.model.Deal.DumbAutoDirectives;
import com.rogerpf.aabridge.model.Dir;
import com.rogerpf.aabridge.model.Hand;
import com.rogerpf.aabridge.view.AaDragGlassPane;

/**   
 */
public class Controller implements KeyEventDispatcher, ActionListener {
	// ---------------------------------- CLASS -------------------------------------

	private static char previous_char = 0;
	private static long previous_time = 0;

	DumbAutoDirectives dumbAutoDir = new DumbAutoDirectives();

	/**
	 * This ia a logical 'constructor' except that is does not happen until all
	 * the initial views are constructed and ready to be "controlled"
	 */
	public Timer postContructionInitTimer = new Timer(100, new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			// =============================================================================
			postContructionInitTimer.stop();

			//	CmdHandler.runTests();  // only enable FOR dev use  <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

			App.mruCollection.loadCollection();

			App.aaHomeBtnPanel.fillMenuDelayed();

			boolean chapterLoaded = false;
			boolean keepTrying = true;

			// Is there a file on the command line - as used by windows file associations
			if (App.args != null && App.args.length >= 1 && App.args[0] != null && !App.args[0].isEmpty()) {
				File file = new File(App.args[0]);
				if (file != null) {
					File fAy[] = { file };
					chapterLoaded = BridgeLoader.processDroppedList(fAy);
				}
				keepTrying = !chapterLoaded; // the user will be expecting her/his file to load so we should not do something else ?
			}

			// Is there a (dev mode) linFile we want to start with
			//
			boolean dev_playBridge = false;

			if (/* App.devMode && */(chapterLoaded == false) && keepTrying) {
				String dealName = "";

//				dev_playBridge = true;
//				dealName = "Distr Flash Cards";

//				App.lbx_modeExam = true; // testing only
//				dealName = "hand 8";
//				dealName = "Make Deal";
//				dealName = "mentoring 2013";
//				dealName = "mentoring1604a";				
//				dealName = "quick test";
//				dealName = "mentoring1705a"; // =============== <<<<<<<<<<<<<<<<<<<<

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
					keepTrying = false; // the DEV user (you) will be expecting her/his file to load so we should not do something else ?

					if (chapterLoaded) {
//						App.pbnAutoEnter = true;
//						App.reinstateAnalyser = true;

//						App.mg.jump_to_pg_number_display(1 + 115);

//						CmdHandler.tutorialIntoDealStd();
//						CmdHandler.tutorialIntoDealClever();
//						CmdHandler.ddsScoreOnOff();
//						CmdHandler.tutorialIntoDealB1st();
//						CmdHandler.tutorialIntoDealCont();
//						CmdHandler.leftWingNormal();
//						CmdHandler.leftWingEdit();
//						CmdHandler.mainUndo();
//						CmdHandler.mainUndo();

//						App.frame.executeCmd("copyFolder_Doc_Collection");
//						MassGi_utils.multiDealSaveAsPbn_noCardPlay();
//						App.frame.executeCmd("rightPanelPrefs7_ShowBtns");
//						CmdHandler.tutorialIntoDealB1st();
//						CmdHandler.tutorialIntoDealClever();
//						CmdHandler.leftWingEdit();
//						CmdHandler.editHands();
//						if (App.ddsScoreShow == false)
//							CmdHandler.ddsScoreOnOff();
//						CmdHandler.ddsAnalyse();
					}
				}
			}

			/**
			 *  look at the user set start-up option
			 */

			if (chapterLoaded == false && keepTrying) {
				ShowHelpAndWelcome();
			}

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

			App.validateWindowPosition();

			App.frame.setLocation(App.frameLocationX, App.frameLocationY);
			App.frame.setSize(App.frameWidth, App.frameHeight);

			if (App.maximized_both) {
				App.frame.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
			}
			else if (App.maximized_horiz) {
				App.frame.setExtendedState(java.awt.Frame.MAXIMIZED_HORIZ);
			}
			else if (App.maximized_vert) {
				App.frame.setExtendedState(java.awt.Frame.MAXIMIZED_VERT);
			}

			if (App.showMouseWheelSplash) {
				App.frame.aaDragGlassPane.showOverlayScreen(AaDragGlassPane.mouse_wheel);
			}

			App.allConstructionComplete = true;

			Cc.intensitySliderChange();

			App.colorIntensityChange();

			App.frame.setVisible(true);

			controlerInControl();

			if (App.devMode && dev_playBridge) {
				App.frame.executeCmd("playBridge_and_dealChoice");
				CmdHandler.mainNewBoard();
			}

			bossPicker = new BossPicker();

			if (App.poller_boss) {
				// yes we always run the TIMER (not the poll) 
				// even if the user does want the poll			
				App.frame.downloads__scan_timer.start();
			}
			else {
				App.pollMenuItem.setEnabled(false);
				App.pollMenuItem.setSelected(false);
			}
		}
	});

	BossPicker bossPicker;

	static int tutRattle = 3;

	/** ******************************************************************************
	 */
	Timer rattleTimer = new Timer(10, new ActionListener() {
		public void actionPerformed(ActionEvent evt) {

//			App.frame.rrp.setVisible(false);
//
//			App.frame.rrp.setVisible(tutRattle % 2 == 1);
//			App.frame.brp.setVisible(tutRattle % 2 == 1);

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
		App.gbp.c1_1__bfdp.biddingCompleteDelayTimer.stop();
		App.gbp.c1_1__bfdp.biddingCompleteDelayTimer_part2.stop();

		App.mg.tutorialPlayTimer.stop();

		// postContructionInitTimer.stop(); never this one
		// App.gbp.c1_1__tfdp.finalCardUnDisplayTimer.stop(); not this one
		// floatingHandExtraDisplayTimer.stop(); not this one
		// afterPlpShakerTimer.stop(); or this one

		App.con.autoBidDelayTimer.stop();
	}

	/**
	 */
	public static void stopDisplayAndTutorialTimers() {
		// =============================================================================
		App.gbp.c1_1__tfdp.normalTrickDisplayTimer.stop();
		App.gbp.c1_1__tfdp.reviewTrickDisplayTimer.stop();

		App.mg.tutorialPlayTimer.stop();
	}

	public void ShowHelpAndWelcome() {
		// =============================================================================
		Book b = App.bookshelfArray.get(0).getBookByFrontNumb(90 /* The Main Welcome & Help */);
		if (b != null) {
			boolean chapterLoaded = b.loadChapterByIndex(0);
			if (chapterLoaded) {
				App.book = b;
				App.aaBookPanel.matchToAppBook();
				App.aaBookPanel.showChapterAsSelected(0);
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

		boolean skip_DDS[] = new boolean[1];
		skip_DDS[0] = false;

		card = hand.deal.botExtra.cardForHintCandidate(card, skip_DDS);

		if ((skip_DDS[0] == false) && App.haglundsDDSavailable && App.useDDSwhenAvaialble_autoplay) { // Double Dummy
			card = Z_ddsCalculate.improveDumbPlay(hand, card);
		}

		App.con.tableTheCard(hand, card);
	}

	/**
	 */
	public void tableTheCard(Hand curHand, Card playedCard) {
		// =============================================================================

		App.isVmode_InsideADeal(); // 

		curHand.playCard(playedCard);

		App.ddsDeal = null;
//		System.out.println("  *  tableTheCard()    App.ddsDeal    set to null");

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

		if (App.youAutoplayFAST && App.youAutoplayAlways && !App.youAutoplayPause)
			App.gbp.c1_1__tfdp.normalTrickDisplayTimer_FAST_startIfNeeded();
		else
			App.gbp.c1_1__tfdp.normalTrickDisplayTimer_startIfNeeded();

		curHand.deal.botExtra.apply_botInstructions();

		App.frame.repaint();

		// was RRRRRRR here

		controlerInControl();
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
				return; // wait until the splash has cleared
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
		 * The System can be doing very different things mainly depending on the type of 
		 * .lin file (if any) that is currently loaded.  The main indication of what is
		 * happening is the current  Aaa.visualMode
		 */

		if (App.visualMode == App.Vm_InsideADeal) {
			/**
			 * We have a simple deal OR multiple simple deals there will always be
			 * a valid deal in App.deal   
			 */

			if (App.deal.isDoneHand()) {
				/** 
				 * nothing should happening we are just waiting for 
				 *   them to click on - New Deal or load a lin file
				 */
				return;
			}

			if (App.mode == Aaa.NORMAL_ACTIVE) {
				if (App.deal.isBidding()) {
					startAutoBidDelayTimerIfNeeded();
					return;
				}
			}

			// System.out.print("*");

			if (App.haglundsDDSavailable && App.ddsScoreShow) { // RRRRRRR

				boolean revTDT_running = App.gbp.c1_1__tfdp.reviewTrickDisplayTimer.isRunning();

				if (App.mode == Aaa.NORMAL_ACTIVE) {

					Hand toPlay = App.deal.getNextHandToPlay();

					if (toPlay != null && !App.isAutoPlay(toPlay.compass)) {

						// System.out.println(" *** controllerInControl()  A  -  NORMAL_ACTIVE ");

						Deal clone = App.deal.deepClone();
						App.ddsDeal = Z_ddsCalculate.scoreCardsInNextHandToPlay(clone);
					}
				}

				else if (App.mode == Aaa.EDIT_PLAY && App.deal.isPlaying()) {

					// System.out.println(" *** controllerInControl()  B  -  EDIT_PLAY ");

					Deal clone = App.deal.deepClone();
					App.ddsDeal = Z_ddsCalculate.scoreCardsInNextHandToPlay(clone);
				}

				else if (App.mode == Aaa.REVIEW_PLAY && (revTDT_running == false)) {

					// System.out.println(" *** controllerInControl()  C  -  REVIEW_PLAY    revTDT_running:" + revTDT_running);

					Deal clone = App.deal.deepClone();
					clone.fastUndoBackTo(App.reviewTrick, App.reviewCard, true /* setDdsNextCard */);
					App.ddsDeal = Z_ddsCalculate.scoreCardsInNextHandToPlay(clone);
				}
			}
			App.gbp.c1_1__tfdp.normalTrickDisplayTimer_startIfNeeded();
		}

	}

	public static void loadBookChapter_prev(boolean show_end) {
		// =============================================================================
		int i = App.aaBookPanel.getLoadedChapterIndex();
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
				App.aaBookPanel.showChapterAsSelected(i);
				if (show_end)
					App.mg.setTheReadPoints_ToEnd();
				break;
			}
			App.aaBookPanel.showChapterAsBroken(i);
		}
	}

	public static void loadBookChapter_next() {
		// =============================================================================
		int i = App.aaBookPanel.getLoadedChapterIndex();
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
				App.aaBookPanel.showChapterAsSelected(i);
				break;
			}
			App.aaBookPanel.showChapterAsBroken(i);
		}
	}

	public static void Left_keyPressed() {
		// =============================================================================
		if (App.cameFromPbnOrSimilar()) {

			if (App.pbnAutoEnter) {
				if (App.isVmode_Tutorial()) {
					App.mg.tutorialBackOne();
					CmdHandler.tutorialIntoDealClever();
				}
				else {
					boolean prev_analyser = App.ddsAnalyserPanelVisible;
					App.ddsAnalyserPanelVisible = false;
					MassGi_utils.do_dealmodeBackToMovie();
					App.mg.tutorialBackOne();
					CmdHandler.tutorialIntoDealClever();
					if (prev_analyser && App.reinstateAnalyser) {
						CmdHandler.ddsAnalyse();
					}
				}
				App.pbnAutoEnter = true;
				App.gbp.matchPanelsToDealState();
				return;
			}
		}

		if (App.isVmode_Tutorial()) {
			stopDisplayAndTutorialTimers();
			App.mg.tutorialBackOne();
		}
		else if (App.isMode(Aaa.REVIEW_PLAY)) {
			stopDisplayAndTutorialTimers();
			// @ formatter:off
			switch (App.mouseWheelDoes) {
			case App.WMouse_STEP:
				CmdHandler.reviewBackOneTrick();
				break;
			case App.WMouse_FLOW:
				CmdHandler.reviewBackOneTrick();
				break;
			case App.WMouse_SINGLE:
				CmdHandler.reviewBackOneCard();
				break;
			}
			// @ formatter:on
		}
		else if (App.isMode(Aaa.REVIEW_BIDDING)) {
			stopDisplayAndTutorialTimers();
			CmdHandler.reviewBackOneBid();
		}
		else if (App.isVmode_InsideADeal() && App.isMode(Aaa.NORMAL_ACTIVE) /* real 'playing' */) {
			App.gbp.c1_1__tfdp.clearShowCompletedTrick();
		}
		App.con.controlerInControl();
	}

	public static void Right_keyPressed() {
		// =============================================================================
		if (App.cameFromPbnOrSimilar()) {

			if (App.pbnAutoEnter) {
				if (App.isVmode_Tutorial()) {
					App.mg.tutorialFlowFwd(false /* stepIfBidding */);
					CmdHandler.tutorialIntoDealClever();
				}
				else {
					boolean prev_analyser = App.ddsAnalyserPanelVisible;
					App.ddsAnalyserPanelVisible = false;
					MassGi_utils.do_dealmodeBackToMovie();
					App.mg.tutorialFlowFwd(false /* stepIfBidding */);
					CmdHandler.tutorialIntoDealClever();
					if (prev_analyser && App.reinstateAnalyser) {
						CmdHandler.ddsAnalyse();
					}
				}
				App.pbnAutoEnter = true;
				App.gbp.matchPanelsToDealState();
				return;
			}
		}

		if (App.isVmode_Tutorial()) {
			// @ formatter:off
			switch (App.mouseWheelDoes) {
			case App.WMouse_STEP:
				App.mg.tutorialStepFwd();
				break;
			case App.WMouse_FLOW:
				App.mg.tutorialFlowFwd(true /* stepIfBidding */);
				break;
			case App.WMouse_SINGLE:
				App.mg.tutorialFlowFwd(true /* stepIfBidding */);
				break;
			}
			// @ formatter:on
		}
		else if (App.isMode(Aaa.REVIEW_PLAY)) {
			// @ formatter:off
			switch (App.mouseWheelDoes) {
			case App.WMouse_STEP:
				CmdHandler.reviewFwdOneTrick();
				break;
			case App.WMouse_FLOW:
				CmdHandler.reviewFwdShowOneTrick();
				break;
			case App.WMouse_SINGLE:
				CmdHandler.reviewFwdOneCard();
				break;
			}
			// @ formatter:on

		}
		else if (App.isMode(Aaa.REVIEW_BIDDING)) {
			CmdHandler.reviewFwdOneBid();
		}
		else if (App.isVmode_InsideADeal() && App.isMode(Aaa.NORMAL_ACTIVE) /* real 'playing' */) {
			App.gbp.c1_1__tfdp.clearShowCompletedTrick();
		}
		App.con.controlerInControl();
	}

	public static void Up_keyPressed() {
//		if (App.isVmode_Tutorial()) {
		loadBookChapter_prev(false);
//		}
	}

	public static void Down_keyPressed() {
//		if (App.isVmode_Tutorial()) {
		loadBookChapter_next();
//		}
	}

}

class BossPicker extends Thread {
	// ---------------------------------- CLASS -------------------------------------

	File semaphore;

	BossPicker() {
		// ==========================================================================
		try {
			semaphore = new File(App.cmdsAndScripts_folder + ".semaphore");
			App.poller_boss = semaphore.createNewFile();

			if (App.poller_boss == false) {
				long now = new Date().getTime();
				long lm = semaphore.lastModified();
				long diff = now - lm;
				if (diff < 1500)
					return; // we will never start

				App.poller_boss = true;
				semaphore.setLastModified((new Date()).getTime());
				start();
			}

		} catch (IOException e) {
		}
	}

	public void run() {
		// ==========================================================================
		try {

			for (;;) {
				semaphore.setLastModified((new Date()).getTime());
				sleep(610);
			}

		} catch (Exception e) {
		}
	}
}
