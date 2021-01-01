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

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFileChooser;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.rogerpf.aabridge.igf.MassGi;
import com.rogerpf.aabridge.igf.MassGi_utils;
import com.rogerpf.aabridge.model.Card;
import com.rogerpf.aabridge.model.Deal;
import com.rogerpf.aabridge.model.Dir;
import com.rogerpf.aabridge.model.Hand;
import com.rogerpf.aabridge.model.Suit;
import com.rogerpf.aabridge.util.Util;
import com.rogerpf.aabridge.view.AaDragGlassPane;

public final class CmdHandler {

	public final static RpfBtnDef getDef(String cmd) {
		for (RpfBtnDef bd : buttonDefs) {
			if (cmd.equals(bd.cmd))
				return bd;
		}
		assert (false);
		return null;
	}

	public final static RpfBtnDef[] buttonDefs = {
			// ==============================================================================================
			// @formatter:off
		//             "openSavesFolder" entry EXISTS but not as an RpfButton
		//             "runTests"        entry EXISTS but not as a button
		//             "nullCommand"     entry EXISTS but not as a button
		new RpfBtnDef( "menuSaveStd",				Aaf.gT("lhp.save"),				Aaf.gT("lhp.save_TT")),
		new RpfBtnDef( "menuSaveAs",				Aaf.gT("lhp.saveAs"),			Aaf.gT("lhp.saveAs_TT")),
	                                                
		new RpfBtnDef( "editHandsAddRes",			Aaf.gT("lhp.eH.addRes"),		Aaf.gT("lhp.eH.addRes_TT")),
		new RpfBtnDef( "editHandsRemoveRes",		Aaf.gT("lhp.eH.remRes"),		Aaf.gT("lhp.eH.remRes_TT")),
		new RpfBtnDef( "editHandsShuffOp",		    Aaf.gT("lhp.eH.shufOp"),		Aaf.gT("lhp.eH.shufOp_TT")),
		new RpfBtnDef( "editHands_ssS",		        Aaf.gT("lhp.eH.s"),				Aaf.gT("lhp.eH.s_TT")),
		new RpfBtnDef( "editHands_ssH",		        Aaf.gT("lhp.eH.h"),				Aaf.gT("lhp.eH.h_TT")),
		new RpfBtnDef( "editHands_ssD",		        Aaf.gT("lhp.eH.d"),				Aaf.gT("lhp.eH.d_TT")),
		new RpfBtnDef( "editHands_ssC",		        Aaf.gT("lhp.eH.c"),				Aaf.gT("lhp.eH.c_TT")),
		new RpfBtnDef( "editHands_low",		        Aaf.gT("lhp.eH.low"),			Aaf.gT("lhp.eH.low_TT")),
		new RpfBtnDef( "editHands",					Aaf.gT("lhp.eH.hands"),			Aaf.gT("lhp.eH.hands_TT")),
		new RpfBtnDef( "editHandsRotateAnti",		"<",							Aaf.gT("lhp.eH.anti_TT")),
		new RpfBtnDef( "editHandsSwapOpps",			"=",		    				Aaf.gT("lhp.eH.swapOpps_TT")),
		new RpfBtnDef( "editHandsRotateClock",		">",		    				Aaf.gT("lhp.eH.clock_TT")),
                                                    
		new RpfBtnDef( "editBidding",				Aaf.gT("lhp.eBid.bid"),			Aaf.gT("lhp.eBid.bid_TT")),
		new RpfBtnDef( "editBiddingWipe",			Aaf.gT("lhp.eBid.wipe"),		Aaf.gT("lhp.eBid.wipe_TT")),
		new RpfBtnDef( "editPlay",					Aaf.gT("lhp.ePlay.edit"),		Aaf.gT("lhp.ePlay.edit_TT")),
                                                    
		new RpfBtnDef( "leftWingEdit",				Aaf.gT("lhp.edit"),				Aaf.gT("lhp.edit_TT")),
		new RpfBtnDef( "leftWingNormal",			Aaf.gT("lhp.play"),				Aaf.gT("lhp.play_TT")),
		new RpfBtnDef( "leftWingReview",			Aaf.gT("lhp.review"),			Aaf.gT("lhp.review_TT")),
                                                    
		new RpfBtnDef( "mainAnti",					"<",							Aaf.gT("gbl.anti_TT")),
		new RpfBtnDef( "mainClock",					">",							Aaf.gT("gbl.clock_TT")),
		new RpfBtnDef( "claimBtn",					Aaf.gT("gbl.claim"),			Aaf.gT("gbl.claim_TT")),
		new RpfBtnDef( "mainNewBoard",				Aaf.gT("gbl.newBoard"),			Aaf.gT("gbl.newBoard_TT")),
		new RpfBtnDef( "mainUndoBot",			    Aaf.gT("gbl.undoBot"),			Aaf.gT("gbl.undoBot_TT")),
		new RpfBtnDef( "mainUndoStandard",			Aaf.gT("gbl.undo"),				Aaf.gT("gbl.undo_TT")),
                                                    
		new RpfBtnDef( "ddsAnalyse",				Aaf.gT("rhp.analyse"),			Aaf.gT("rhp.analyse_TT")),
		new RpfBtnDef( "ddsReinstateAnalyser",		Aaf.gT("rhp.keepon"),			Aaf.gT("rhp.keepon_TT")),
		new RpfBtnDef( "ddsLabel",					Aaf.gT("rhp.dds"),				Aaf.gT("rhp.dds_TT")),
		new RpfBtnDef( "ddsScoreOnOff",				Aaf.rhp_isOn,				    Aaf.gT("rhp.dds_TT")),
		new RpfBtnDef( "ddsStyle_btn",				"",	/* no text */    			Aaf.gT("rhp.dds_TT")),
		
		new RpfBtnDef( "hHandsShowHideCap1",		Aaf.gT("rhp.nonKib"),		    ""),
		new RpfBtnDef( "hHandsShowHideCap2",		Aaf.gT("rhp.seats"),		    ""),
		new RpfBtnDef( "hiddenHandsShow",		    Aaf.gT("rhp.show"),				Aaf.gT("rhp.show_TT")),
		new RpfBtnDef( "hiddenHandsHide",		    Aaf.gT("rhp.hide"),				Aaf.gT("rhp.hide_TT")),
//		new RpfBtnDef( "hiddenHandsClick1",			Aaf.gT("rhp.click"),			Aaf.gT("rhp.click_TT")),
//		new RpfBtnDef( "hiddenHandsClick2",			Aaf.gT("rhp.aName"),			Aaf.gT("rhp.aName_TT")),
                                                    
		new RpfBtnDef( "smLabelStudy",				Aaf.gT("rhp.study"),			""),
		new RpfBtnDef( "smLabelDeal",				Aaf.gT("rhp.deal"),				""),
		
		new RpfBtnDef( "autoEnterLabelAuto",		Aaf.gT("rhp.auto"),				Aaf.gT("rhp.auto_TT")),
		new RpfBtnDef( "autoEnterLabelEnter",		Aaf.gT("rhp.enter"),			Aaf.gT("rhp.auto_TT")),
		new RpfBtnDef( "autoEnterOnOff",			Aaf.rhp_isOff,			        Aaf.gT("rhp.auto_TT")),
	                                                                                             
		new RpfBtnDef( "questionTellMe",			Aaf.gT("questTb.tellMe"),		Aaf.gT("questTb.tellMe_TT")),
		new RpfBtnDef( "question_z_Video",			Aaf.gT("questTb.video"),		Aaf.gT("questTb.video_TT")),
		new RpfBtnDef( "question_z_Learn",			Aaf.gT("questTb.learn"),		Aaf.gT("questTb.learn_TT")),
		new RpfBtnDef( "question_z_Next",			Aaf.gT("questTb.new"),			Aaf.gT("questTb.new_TT")),
		new RpfBtnDef( "question_z_NextAndTell",	Aaf.gT("questTb.new&Tell"),		Aaf.gT("questTb.new&Tell_TT")),
		new RpfBtnDef( "question_z_Options",		Aaf.gT("questTb.options"),		Aaf.gT("questTb.options_TT")),
		new RpfBtnDef( "question_z_Train",			Aaf.gT("questTb.train"),		Aaf.gT("questTb.train_TT")),
		new RpfBtnDef( "question_z_Exam",			Aaf.gT("questTb.exam"),			Aaf.gT("questTb.exam_TT")),
                                                    
		new RpfBtnDef( "reviewBackOneCard",			"<",		                    Aaf.gT("cmdBar.b1card_TT")),
		new RpfBtnDef( "reviewFwdOneCard",			">",		                    Aaf.gT("cmdBar.f1card_TT")),
                                                                                    
		new RpfBtnDef( "reviewBackOneBid",			"<",		                    Aaf.gT("cmdBar.b1bid_TT")),
		new RpfBtnDef( "reviewFwdOneBid",			">",		                    Aaf.gT("cmdBar.f1bid_TT")),
                                                                                    
		new RpfBtnDef( "commonStepBack",			"<",		                    Aaf.gT("cmdBar.stepBack_TT")),
		new RpfBtnDef( "commonFlowBack",			"<",		                    Aaf.gT("cmdBar.flowBack_TT")),
			
		new RpfBtnDef( "commonStepFwd",				Aaf.cmdBar_step_AM,			    Aaf.gT("cmdBar.step_TT")),
		new RpfBtnDef( "commonFlowFwd",				Aaf.cmdBar_flow_AM,			    Aaf.gT("cmdBar.flow_TT")),
		new RpfBtnDef( "dealmodeBackToMovie",		Aaf.gT("cmdBar.backTo"),        Aaf.gT("cmdBar.backTo_TT")),
		new RpfBtnDef( "tutorialIntoDealClever",	Aaf.gT("cmdBar.enter"),	        Aaf.gT("cmdBar.enter_TT")),
		new RpfBtnDef( "tutorialIntoDealContdds",	Aaf.gT("cmdBar.contdds_c"),		Aaf.gT("cmdBar.contdds_c_TT")),
		new RpfBtnDef( "tutorialIntoDealB1st",		Aaf.gT("cmdBar.1st"),			Aaf.gT("cmdBar.1st_TT")),	

		// @formatter:on                    		
	};

	public static class RpfBtnDef {
		// ==============================================================================================
		public String cmd;
		public String btnText;
		public String tooltip;

		RpfBtnDef(String c, String b, String t) { /* Constructor */
			cmd = c;
			btnText = b;
			tooltip = t;
		}
	}

	/**   
	 */
	static void nullCommand() {
		// ==============================================================================================
	}

	/**   
	 */
	static void mainAnti() {
		// ==============================================================================================
		if (App.deal.isDoneHand()) // so we skip the 'done hand'
			return;

		App.incOffsetAntiClockwise(); // includes repaint
		App.frame.invalidate();
	}

	/**   
	 */
	static void mainClock() {
		// ==============================================================================================
		if (App.deal.isDoneHand()) // so we skip the 'done hand'
			return;

		App.incOffsetClockwise(); // includes repaint
		App.frame.invalidate();
	}

	/**   
	 */
	static void mainUndoBot() {
		// ==============================================================================================

		boolean success = mainUndo_common(true /* bot_undo_only */);

		if (success && App.isMode(Aaa.NORMAL_ACTIVE)) {
			App.setMode(Aaa.EDIT_PLAY);
		}
	}

	/**   
	 */
	static void mainUndoStandard() {
		// ==============================================================================================

		mainUndo_common(false /* NOT bot_undo_only */);
	}

	/**   
	 */
	static boolean mainUndo_common(boolean bot_undo_only) {
		// ==============================================================================================
		if (App.deal.isDoneHand()) // so we skip the 'done hand'
			return false;

		if (App.isModeAnyReview())
			return false;

		App.deal.clearPlayerNames();

		App.deal.clearAllStrategies();

		if (App.deal.isBidding() || App.isMode(Aaa.EDIT_BIDDING)) {

			App.gbp.c2_2__bbp.clearHalfBids();

			if (App.isAutoBid(App.deal.getNextHandToBid().compass)) {
				// We have to (do we?) wait until the autoplayer has finished
				// return;
			}
			while (true) {
				Hand hand = App.deal.getLastHandThatBid();
				if (hand == null)
					break;
				boolean wasAuto = App.isAutoBid(hand.compass);
				App.deal.undoLastBid();
				if (!wasAuto)
					break;
			}
			// App.gbp.c2_2__bbp.startAutoBidDelayTimerIfNeeded();
			App.frame.repaint();
		}

		// all the rest of the ifs cover HAND editing

		if (App.deal.eb_min_card >= App.deal.countCardsPlayed()) {
			; // we do nothing i.e. do NOT allow the undo
			return false;
		}

		else if (App.deal.isFinished()) {

			if (App.deal.endedWithClaim) {
				App.deal.endedWithClaim = false;
				App.deal.tricksClaimed = 0;
			}
			else {
				while ((App.deal.countCardsPlayed()) > 44) {
					Hand hand = App.deal.getLastHandThatPlayed();
					hand.undoLastPlay();
				}
			}
			if (App.isMode(Aaa.REVIEW_PLAY)) {
				App.setMode(Aaa.NORMAL_ACTIVE);
			}

			App.gbp.c1_1__tfdp.setShowCompletedTrick();
			App.gbp.c1_1__tfdp.makeCardSuggestions();
			App.gbp.matchPanelsToDealState();
		}

		else if (App.isMode(Aaa.EDIT_PLAY) || App.isMode(Aaa.REVIEW_PLAY)) {

			int played = App.deal.countCardsPlayed();
			if (played > 0) {
				if (App.deal.botExtra.haveCardsMoved()) {
					int wanted_undone = 1;
					studydeal_cardUndo(wanted_undone);
				}
				else { // the std easy way
					Hand hand = App.deal.getLastHandThatPlayed();
					hand.undoLastPlay();
				}
			}

			if (App.isMode(Aaa.REVIEW_PLAY)) {
				App.setMode(Aaa.NORMAL_ACTIVE);
			}

			App.gbp.c1_1__tfdp.setShowCompletedTrick();
			App.gbp.c1_1__tfdp.makeCardSuggestions();
		}

		else if (App.isMode(Aaa.NORMAL_ACTIVE) && App.deal.isPlaying() && App.youAutoplayAlways) {

			// as all hands are on auto we can't just undo until we get to one that is not auto
			// instead we just go back the last number of cards that made a whole trick
			int played;
			while ((played = App.deal.countCardsPlayed()) > 0) {
				Hand hand = App.deal.getLastHandThatPlayed();
				hand.undoLastPlay();
				if (((played - 1) % 4) == 0)
					break;
			}
			App.gbp.c1_1__tfdp.setShowCompletedTrick();
			App.gbp.c1_1__tfdp.makeCardSuggestions();
		}

		else if (App.isMode(Aaa.NORMAL_ACTIVE) && App.deal.isPlaying()) {
			while (App.deal.countCardsPlayed() > 0) {

				if (App.deal.eb_min_card >= App.deal.countCardsPlayed()) {
					break; // we have "undone" back to an eb blocker
				}

				Hand hand;
				if (App.deal.botExtra.haveCardsMoved()) {
					int wanted_undone = 1;
					studydeal_cardUndo(wanted_undone);
					hand = App.deal.getNextHandToPlay();
				}
				else { // the std easy way
					hand = App.deal.getLastHandThatPlayed();
					hand.undoLastPlay();
				}
				if (App.isAutoPlay(hand.compass) == false || bot_undo_only) {
					if (App.youAutoSingletons) {
						if (hand.getSelfPlayableCard(App.youAutoAdjacent) != null)
							continue; // undo this one as well
					}
					break;
				}
			}
			App.gbp.c1_1__tfdp.setShowCompletedTrick();
			App.gbp.c1_1__tfdp.makeCardSuggestions();
		}

		App.gbp.c1_1__tfdp.normalTrickDisplayTimer_startIfNeeded();
		App.frame.repaint();

		return true;
	}

	/**   
	 */
	private static void studydeal_cardUndo(int wanted_undone) {
		// ==============================================================================================

		assert (App.deal.botExtra.haveCardsMoved());

		Deal withPlay = App.deal;
		int prePlayed = App.deal_bk.countCardsPlayed();
		int playedNow = withPlay.countCardsPlayed();
		int playTo = playedNow - wanted_undone;

		if (withPlay.eb_min_card > playTo) {
			playTo = withPlay.eb_min_card;
		}

		App.deal = App.deal_bk.deepClone();

		for (int i = prePlayed; i < playTo; i++) {
			Card card_o = withPlay.getCardThatWasPlayed(i);
			Hand handAy[] = new Hand[1];
			Card card = App.deal.getCardSearchAllHands_Frag(card_o.suit, card_o.rank, handAy);
			assert (card != null);
			assert (handAy[0] != null);
			(handAy[0]).playCard(card);
			App.deal.botExtra.apply_botInstructions();
		}

		App.dealMajorChange();

		@SuppressWarnings("unused")
		int z = 0;
	}

	/**   
	 */
	private static void studydeal_redoPlay(int cards_to_play) {
		// ==============================================================================================

		assert (App.deal.botExtra.haveCardsMoved());

		Deal withPlay = App.deal;
		int prePlayed = App.deal_bk.countCardsPlayed();
//		int playedNow = withPlay.countCardsPlayed();
		int playTo = cards_to_play;

		if (withPlay.eb_min_card > playTo) { // ummmm should not happen
			playTo = withPlay.eb_min_card;
		}

		App.deal = App.deal_bk.deepClone();

		for (int i = prePlayed; i < playTo; i++) {
			Card card_o = withPlay.getCardThatWasPlayed(i);
			Hand handAy[] = new Hand[1];
			Card card = App.deal.getCardSearchAllHands_Frag(card_o.suit, card_o.rank, handAy);
			assert (card != null);
			assert (handAy[0] != null);
			(handAy[0]).playCard(card);
			App.deal.botExtra.apply_botInstructions();
		}

		App.dealMajorChange();

		@SuppressWarnings("unused")
		int z = 0;
	}

	/**   
	 */
	public static void matchDealBkToReview() {
		// ==============================================================================================
		if (App.deal.botExtra.haveCardsMoved() == false)
			return;

		// not needed - only undo stuff when the re-enter PLAY
	}

	/**   
	 */
	public static void mainNewBoard() {
		// ==============================================================================================
		boolean re_analyse = App.ddsAnalyserPanelVisible && App.reinstateAnalyser;
		App.ddsAnalyserPanelVisible = false;

		boolean re_dds = App.devMode && App.ddsScoreShow;
		App.ddsScoreShow = false;

		App.allTwister_reset();

		App.frame.rop.p2_KibSeat.clear_dlaeActive(false /* clear_alwaysShowHidden */);

//		App.handPanelNameAreaInfoNumbersShow = true;

		App.flowOnlyCommandBar = false; // ugly should not need to do this here DO WE?
		App.hideCommandBar = false; // ugly should not need to do this here DO WE?
		App.hideTutNavigationBar = false; // ugly should not need to do this here DO WE?
		App.ddsAnalyserPanelVisible = false;

		App.setMode(Aaa.NORMAL_ACTIVE);
		App.reviewTrick = 0;
		App.reviewCard = 0;
		App.reviewBid = 0;
		App.gbp.c1_1__tfdp.clearAllCardSuggestions();

		Deal deal = Deal.newBoard(App.deal.realBoardNo, (App.watchBidding == false), App.dealCriteria, App.youSeatForNewDeal, App.dealFilter);
		App.deal = deal;

		// Inserted 2018-mar-01 
		doAutoSave( /* allow_xxx */ false);
		imp_TempOtherFolder();
		App.frame.rop.setSelectedIndex(App.RopTab_0_NewDealChoices);
		// Inserted 2018-mar-01 

		App.mg = new MassGi(deal);

		App.switchToNewMassGi("");

		App.calcCompassPhyOffset();
		App.frame.setTitleAsRequired();

		App.gbp.c0_2__blp.hideClaimButtonsIfShowing();

		App.dealMajorChange();

		if (App.devMode) {
			App.localShowHidden = (App.localShowHiddPolicy == 1);
		}
		else
			App.localShowHidden = false;

		App.gbp.matchPanelsToDealState();
		App.frame.repaint();
		if (App.isPauseAtEotClickWanted()) {
			App.gbp.c1_1__tfdp.setShowCompletedTrick();
		}
		if (App.deal.isBidding())
			App.con.startAutoBidDelayTimerIfNeeded();
		else
			App.gbp.c1_1__tfdp.normalTrickDisplayTimer_startIfNeeded();

		if (App.newDealAsRequested == false) {
			App.frame.aaDragGlassPane.showOverlayScreen(AaDragGlassPane.new_deal_levels);
			return;
		}

		if (re_analyse) {
			ddsAnalyse();
		}

		if (re_dds) {
			App.ddsScoreShow = true;
			App.gbp.matchPanelsToDealState();
		}

	}

	/**   
	 */
	static void setFcPreferredSize(JFileChooser fc) {
		// ==============================================================================================
		Dimension wh = App.frame.getSize();
		wh.width = (wh.width * 85) / 100;
		wh.height = (wh.height * 70) / 100;
		wh.width = (wh.width * 65) / 100;
		wh.height = (wh.height * 55) / 100;

		if (!App.onWin && wh.height < 400) {
			wh.height = 400;
		}
		fc.setPreferredSize(wh);
	}

	/**   
	 */
	static void openCmdsFolder() {
		// ==============================================================================================
		try {
			Desktop.getDesktop().open(new File(App.cmdsAndScripts_folder));
		} catch (IOException e) {
		}
	}

	/**   
	 */
	static void openTempMyHandsFolder() {
		// ==============================================================================================
		try {
			Desktop.getDesktop().open(new File(App.temp_MyHands_folder));
		} catch (IOException e) {
		}
	}

	/**   
	 */
	static void openPage_BBOHandsToaaBridge() {
		// ==============================================================================================

		App.frame.executeCmd("openPage_bboHandsToaaBridge");

	}

	/**   
	 */
	static void openTempOtherFolder() {
		// ==============================================================================================
		try {
			Desktop.getDesktop().open(new File(App.temp_Other_folder));
		} catch (IOException e) {
		}
	}

	/**   
	 */
	static void openDownloadsFolder() {
		// ==============================================================================================
		try {
			Desktop.getDesktop().open(new File(App.downloads_folder));
		} catch (IOException e) {
		}
	}

	/**   
	 */
	static void imp_SavesFolder() {
		// ==============================================================================================
		File[] files = new File[1];
		files[0] = new File(App.realSaves_folder);
		if (files.length == 0)
			return;
		App.lastDroppedList = files;

		BridgeLoader.processDroppedList(App.lastDroppedList);
	}

	/**   
	 */
	static void imp_TempMyHandsFolder() {
		// ==============================================================================================
		File[] files = new File[1];
		files[0] = new File(App.temp_MyHands_folder);
		if (files.length == 0)
			return;
		App.lastDroppedList = files;

		BridgeLoader.processDroppedList(App.lastDroppedList);
	}

	public static Timer imp_TempOtherFolder_timer = new Timer(10, new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			// ==============================================================================================	
			imp_TempOtherFolder_timer.stop();
			imp_TempOtherFolder();
		}
	});

	/**   
	 */
	public static void imp_TempOtherFolder() {
		// ==============================================================================================

		Aaa.delete_all_but_the_newest_files(App.temp_Other_folder, App.numbAllowed_in_Temp_Other);

		File[] files = new File[1];
		files[0] = new File(App.temp_Other_folder);
		if (files.length == 0)
			return;
		App.lastDroppedList = files;

		BridgeLoader.processDroppedList(App.lastDroppedList);
	}

	static void execute_pollDLF_toggled() {
		// ==============================================================================================
		App.pollDownloadsFolder = App.menuItemPollDLF.isSelected();
//		int z=0;
//		z++;
	}

	/**   
	 */
	static void execute_extract_lins_from_latest_html() {
		// ==============================================================================================

		File dl_folder = new File(App.downloads_folder);
		File[] listOfFiles = dl_folder.listFiles();

		long lastmod = 0;
		File chosen = null;

		for (File file : listOfFiles) {
			String low = file.getName().toLowerCase();
			if (file.isFile() && (low.endsWith(".htm") || low.endsWith(".html")) && file.length() > 256) {
				if (lastmod < file.lastModified()) {
					chosen = file;
					lastmod = file.lastModified();
				}
			}
		}

		if (App.ddsAnalyserPanelVisible) {
			App.reinstateAnalyser = false;
			CmdHandler.ddsAnalyse(); // to switch it off
		}

		lins_from_BBO_html(chosen);
	}

	/**   
	 */
	public static boolean lins_from_BBO_html(File chosen) {
		// ==============================================================================================

		File tmh = new File(App.temp_MyHands_folder);
		File tof = new File(App.temp_Other_folder);
		tmh.mkdir();
		tof.mkdir();

		int created = ExtractMyHands.extractFromHtmlFile(chosen);

		if (created > 0) {
			imp_TempMyHandsFolder();
		}
		else if (created < 0) {
			imp_TempOtherFolder();
		}
		return created != 0;
	}

	/**   
	 */
	static void openaaBridgeBaseFolder() {
		// ==============================================================================================
		try {
			Desktop.getDesktop().open(new File(App.homePath));
		} catch (IOException e) {
		}
	}

	/**   
	 */
	static void openSavesFolder() {
		// ==============================================================================================
		try {
			Desktop.getDesktop().open(new File(App.realSaves_folder));
		} catch (IOException e) {
		}
	}

	/**   
	 */
	static void openDownlFolder() {
		// ==============================================================================================

		try {
			Desktop.getDesktop().open(new File(App.downloads_folder));
		} catch (IOException e) {
		}
	}

	/**   
	 */
	static void menuOpen() {
		// ==============================================================================================
		JFileChooser fc = new JFileChooser();
		fc.setMultiSelectionEnabled(true);
		fc.setFileFilter(new FileNameExtensionFilter("aabridge .lin etc", "lin", "pbn", "zip", "linzip"));
		fc.setCurrentDirectory(new File(App.realSaves_folder));
		setFcPreferredSize(fc);

		int returnVal = fc.showOpenDialog(App.frame);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String pathWithSep = fc.getSelectedFile().getParent();
			if (pathWithSep != null && !pathWithSep.contentEquals("")) {
				pathWithSep += File.separator;
			}

			BridgeLoader.processDroppedList(fc.getSelectedFiles());
		}

	}

	/**   
	 */
	public static void doAutoSave(boolean allow_xxx) {
		// ==============================================================================================
		if (App.deal.worthAutosavingSaving() == false)
			return;

		try {

			String dealName = makeDealFileNameAndPath(App.deal, "", "", allow_xxx);

			saveDealAsSingleLinFile(App.deal, dealName);

			if (App.isFilenameThrowAway(App.deal.lastSavedAsFilename)) {
				File f = new File(dealName);
				App.deal.lastSavedAsFilename = f.getName();
			}

			App.frame.setTitleAsRequired();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static int incval = 1;

	/**   
	 */
	static void editPlayWipe() {
		// ==============================================================================================
		if (App.deal.isDoneHand()) { // so we skip the 'done hand'
			return;
		}

		if (App.isMode(Aaa.REVIEW_PLAY)) {
			int reviewPlayed = App.reviewTrick * 4 + App.reviewCard;
			if (reviewPlayed > 0 && reviewPlayed < App.deal.countCardsPlayed()) {
				App.deal.clearPlayerNames();
				App.deal.clearAllStrategies();
				App.deal.endedWithClaim = false;
				App.deal.tricksClaimed = 0;
				while (reviewPlayed < App.deal.countCardsPlayed()) {
					Hand hand = App.deal.getLastHandThatPlayed();
					hand.undoLastPlay();
				}
				App.setMode(Aaa.NORMAL_ACTIVE);
				App.frame.repaint();
				return;
			}
		}

		if (App.isMode(Aaa.NORMAL_ACTIVE) || App.isMode(Aaa.REVIEW_PLAY)) {
			if (App.deal.isFinished()) {
				mainUndoStandard(); // this will undo the last two cards or any claim
				return;
			}
		}

		// App.deal.lastSavedAsFilename = "";
		App.frame.setTitleAsRequired();

		editPlayXall();
		App.reviewTrick = 0;
		App.reviewCard = 0;
		App.reviewBid = 0;

		App.dealMajorChange();
//		App.setMode(Aaa.NORMAL_ACTIVE);
//		if (App.isPauseAtEotClickWanted()) {
//			App.gbp.c1_1__tfdp.setShowCompletedTrick();
//		}
		App.gbp.c1_1__tfdp.normalTrickDisplayTimer_startIfNeeded();
		if (App.isMode(Aaa.EDIT_PLAY))
			App.gbp.c1_1__tfdp.makeCardSuggestions();
		if (App.isMode(Aaa.EDIT_BIDDING))
			App.con.startAutoBidDelayTimerIfNeeded();

		App.gbp.matchPanelsToDealState();
		App.frame.repaint();
	}

	/**   
	 */
	static void menuSaveStd() {
		// ==============================================================================================
		if (App.deal.isDoneHand()) { // so we skip the 'done hand'
			return;
		}

		if (App.isFilenameThrowAway(App.deal.lastSavedAsFilename) && !App.force_savename_xxx) {
			saveCommon("Save");
		}
		else {
			String dealName = makeDealFileNameAndPath(App.deal, App.deal.lastSavedAsPathWithSep, App.deal.lastSavedAsFilename, /* allow_xxx */ true);
			saveDealAsSingleLinFile(App.deal, dealName);

			App.deal.lastDealNameSaved_FULL = new File(dealName).getAbsolutePath();

			App.deal.lastSavedAsFilename = new File(dealName).getName();

			// App.frame.setTitleAsRequired();
		}
		App.calcApplyBarVisiblity();
		App.frame.repaint();
	}

	/**   
	 */
	static void menuSaveAs() {
		// ==============================================================================================
		saveCommon("SaveAs");
		App.calcApplyBarVisiblity();
		App.frame.repaint();
	}

	/**   
	 */
	static void saveCommon(String type) {
		// ==============================================================================================
		if (App.deal.isDoneHand()) { // so we skip the 'done hand'
			return;
		}

		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new FileNameExtensionFilter("Bridge .lin file", App.linExt));

		String pathWithSep = App.deal.lastSavedAsPathWithSep;
		if (pathWithSep == null || pathWithSep.contentEquals("")) {
			pathWithSep = App.realSaves_folder;
		}

		fc.setCurrentDirectory(new File(pathWithSep));
		fc.setDialogTitle(type);

		String dealName = makeDealFileNameAndPath(App.deal, pathWithSep, App.deal.lastSavedAsFilename, /* allow_xxx */ true);

		fc.setSelectedFile(new File(dealName));
		setFcPreferredSize(fc);

		App.saveDialogShowing = true;
		int returnVal = fc.showSaveDialog(App.frame);
		App.saveDialogShowing = false;

		if (returnVal == JFileChooser.APPROVE_OPTION) {

			File chosen = fc.getSelectedFile();
			App.deal.lastSavedAsPathWithSep = chosen.getParent() + File.separator;

			App.deal.lastSavedAsFilename = checkExtension(chosen.getName());
			App.deal.lastDealNameSaved_FULL = checkExtension(chosen.getAbsolutePath());

			dealName = checkExtension(chosen.getAbsolutePath());

			saveDealAsSingleLinFile(App.deal, dealName);
		}

		// App.frame.setTitleAsRequired();
	}

	/**  
	 * Empty string return shows OK 
	 */
	public static String saveDealAsSingleLinFile(Deal deal, String dealName) {
		// ==============================================================================================
		try {
			FileOutputStream fileOut = new FileOutputStream(dealName);

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fileOut, "UTF-8"));

			String content = MassGi_utils.getDealAsLinSave(deal, "", 0 /* 0 => no renumbering */, false /* first_of_set */, true /* single */);

			writer.write(content);

			writer.close();

			fileOut.close();

		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
		return ""; // emptyOK
	}

	/**   
	 */
	public static boolean validateReviewIndexes() {
		// ==============================================================================================
		boolean atEnd = false;

		if ((App.reviewTrick * 4 + App.reviewCard) < App.deal.eb_min_card) {
			App.reviewTrick = App.deal.eb_min_card / 4;
			App.reviewCard = App.deal.eb_min_card % 4;
		}

		if (App.reviewCard > 4) {
			App.reviewCard = 0;
			App.reviewTrick++;
		}

		if (App.reviewCard < 0) {
			App.reviewCard = 4;
			App.reviewTrick--;
		}

		if (App.reviewTrick < 0) {
			App.reviewTrick = 0;
			App.reviewCard = 0;
		}

		int curTrick = App.deal.getCurTrickIndex();
		int curCard = App.deal.getCurCardIndex();

		if (App.reviewTrick == curTrick) {
			if (App.reviewCard > curCard) {
				App.reviewCard = curCard;
				atEnd = true;
			}
		}
		else if (App.reviewTrick > curTrick) {
			App.reviewTrick = curTrick;
			App.reviewCard = curCard;
			atEnd = true;
		}

		if (App.reviewTrick > 12) {
			App.reviewTrick = 12;
			App.reviewCard = 4;
			atEnd = true;
		}

		return atEnd;
	}

	/**
	 */
	public static boolean validateBiddingIndex() {
		// ==============================================================================================
		App.gbp.c1_1__bfdp.reviewBiddingMakeCopy();
		return false;
	}

	/**
	 */
	public static void leftWingNormal() { // button shows as 'Play'
		// ==============================================================================================
		if (App.deal.isDoneHand()) { // so we skip the 'done hand'
			return;
		}

		// App.clearDdsKept();

		if (App.deal.isContractReal() == false) {
			App.showContFlashCount = 3;
			App.ptp.showContNeededTimer.start();

			App.gbp.matchPanelsToDealState();
			App.frame.repaint();
			return;
		}

		App.show_poor_def_msg = true;

		if (App.deal.botExtra.haveCardsMoved()) {
			assert (App.isMode(Aaa.REVIEW_PLAY));
			studydeal_redoPlay(App.reviewTrick * 4 + App.reviewCard);
		}
		else if (App.isMode(Aaa.REVIEW_PLAY) /* || App.isMode(Aaa.REVIEW_BIDDING) && App.deal.isFinished() */) {
			App.deal.fastUndoBackTo(App.reviewTrick, App.reviewCard, false /* setDdsNextCard */);
		}

		App.gbp.c1_1__tfdp.clearShowCompletedTrick_passive();

		App.setMode(Aaa.NORMAL_ACTIVE);

		App.gbp.c1_1__tfdp.makeCardSuggestions();
		App.gbp.matchPanelsToDealState();

		App.frame.repaint();
	}

	/**
	 */
	static void leftWingReview() {
		// ==============================================================================================
		if (App.deal.isDoneHand()) { // so we skip the 'done hand'
			return;
		}

		// App.clearDdsKept();

		if (App.deal.isFinished()) {
			App.localShowHidden = true;
		}
		else if (App.localShowHiddPolicy == 0 /*hidden*/) {
			App.localShowHidden = false;
		}
		else if (App.localShowHiddPolicy == 1 /*show*/) {
			App.localShowHidden = true;
		}
		else if (App.localShowHiddPolicy == 2 /*leave as is */) {
			; // leave alone
		}

		if (App.deal.isBidding()) {
			App.setMode(Aaa.REVIEW_BIDDING);

			App.reviewBid = 99;
			validateBiddingIndex();
		}
		else {
			App.setMode(Aaa.REVIEW_PLAY);

			App.reviewTrick = 13;
			App.reviewCard = 4;
			validateReviewIndexes();
		}

		App.gbp.matchPanelsToDealState();

		App.frame.repaint();
	}

	/**   
	 */
	static void reviewBackToStartOfPlay() {
		// ==============================================================================================
		App.ddsDeal = null;

		App.reviewTrick = 0;
		App.reviewCard = 0;
		App.frame.repaint();
	}

	/**   
	 */
	static void reviewFwdToEndOfPlay() {
		// ==============================================================================================
		App.ddsDeal = null;

		App.reviewTrick = 13;
		App.reviewCard = 4;
		validateReviewIndexes();
		App.frame.repaint();
	}

	/**   
	 */
	static void reviewBackOneTrick() {
		// ==============================================================================================
		App.ddsDeal = null;

		App.reviewTrick--;
		App.reviewCard = 4;
		validateReviewIndexes();
		App.frame.repaint();
		App.gbp.matchPanelsToDealState();
	}

	/**   
	 */
	static void reviewFwdOneTrick() { // Step
		// ==============================================================================================
		App.ddsDeal = null;

		int preset = App.deal.getPresetTrickCount();

		if ((App.reviewCard == 0 && App.reviewTrick == 0 && preset == 0)) { // except stop after opening lead
			App.reviewCard = 1;
		}
		else if (App.reviewCard < 3) {
			App.reviewCard = 4;
		}
		else {
			App.reviewTrick++;
			App.reviewCard = 4; // it will already be 4
		}
		validateReviewIndexes();
		App.frame.repaint();
	}

	/**   
	 */
	static void reviewFwdShowOneTrick() { // Flow
		App.ddsDeal = null;

		App.reviewCard++;
		validateReviewIndexes();
		App.frame.repaint();
		int preset = App.deal.getPresetTrickCount();
		if (App.reviewCard != 4 && !(App.reviewCard == 1 && App.reviewTrick == 0 && preset == 0/* Opening lead excluded from Flow */))
			App.gbp.c1_1__tfdp.reviewTrickDisplayTimer.start();
	}

	/**   
	 */
	static void reviewBackOneCard() {
		App.ddsDeal = null;

		App.mouseWheelDoes = App.WMouse_SINGLE;

		if (App.isMode(Aaa.REVIEW_BIDDING)) {
			reviewBackOneBid();
		}
		else {
			App.reviewCard--;
			validateReviewIndexes();
			App.frame.repaint();
		}
		App.gbp.matchPanelsToDealState();
	}

	/**   
	 */
	static void reviewFwdOneCard() {
		App.ddsDeal = null;

		App.mouseWheelDoes = App.WMouse_SINGLE;

		if (App.isMode(Aaa.REVIEW_BIDDING)) {
			reviewFwdOneBid();
		}
		else {
			App.reviewCard++;
			validateReviewIndexes();
			App.frame.repaint();
		}
		App.gbp.matchPanelsToDealState();
	}

	/**   
	 */
	static void hiddenHandsShow() {
		if (App.deal.isDoneHand()) // so we skip the 'done hand'
			return;
		App.localShowHidden = true;
		App.calcApplyBarVisiblity();
	}

	/**   
	 */
	static void hiddenHandsHide() {
		if (App.deal.isDoneHand()) // so we skip the 'done hand'
			return;
		App.localShowHidden = false;
		App.calcApplyBarVisiblity();
	}

	/**   
	 */
	static void hiddenHandsToggle() {
		if (App.deal.isDoneHand()) // so we skip the 'done hand'
			return;
		App.localShowHidden = !App.localShowHidden;
		App.calcApplyBarVisiblity();
	}

	/**   
	 */
	public static void ddsScoreOnOff() {
		if (App.deal.isDoneHand()) // so we skip the 'done hand'
			return;
		App.ddsScoreShow = !App.ddsScoreShow;
		App.gbp.matchPanelsToDealState();
	}

	/**   
	 */
	static void ddsStyle_btn() {
		if (App.deal.isDoneHand()) // so we skip the 'done hand'
			return;
		App.showDdsScore_aaB_style = !App.showDdsScore_aaB_style;
		App.gbp.matchPanelsToDealState();
	}

	/**   
	 */
	static void autoEnterOnOff() {
		if (App.deal.isDoneHand()) // so we skip the 'done hand'
			return;

		App.pbnAutoEnter = !App.pbnAutoEnter;

		if (App.pbnAutoEnter && App.cameFromPbnOrSimilar() && App.isVmode_Tutorial()) {
			CmdHandler.tutorialIntoDealClever();
			App.pbnAutoEnter = true;
		}
		else if (!App.pbnAutoEnter && App.isVmode_InsideADeal()) {
			MassGi_utils.do_dealmodeBackToMovie();
		}
		App.gbp.matchPanelsToDealState();
	}

	/**   
	 */
	public static void ddsAnalyse() {
		if (App.deal.isDoneHand()) // so we skip the 'done hand'
			return;
		App.gbp.c2_0__ddsAnal.analyseButtonClicked();
	}

	/**   
	 */
	static void ddsReinstateAnalyser() {
		if (App.deal.isDoneHand()) // so we skip the 'done hand'
			return;

		App.gbp.c2_0__ddsAnal.reinstateAnalyserButtonClicked();
	}

	/**   
	 */
	public static void reviewTimeRequestToShowTrick(int trickIndex) {
		App.reviewTrick = trickIndex;
		App.reviewCard = 4;
		validateReviewIndexes();
		App.frame.repaint();
	}

	/**   
	 */
	public static void reviewTrickDisplayTimerFired() {
		App.reviewCard++;
		boolean atEnd = validateReviewIndexes();

		if (!atEnd) {
			if (App.isMode(Aaa.REVIEW_PLAY)) {
				if (App.reviewCard < 4) {
					App.gbp.c1_1__tfdp.reviewTrickDisplayTimer.start();
				}
				else {
					App.con.controlerInControl(false /* false = not special */);
				}
			}
		}
		else if (App.isMode(Aaa.REVIEW_PLAY)) {
			int played = App.deal.countCardsPlayed();
			int review_card = App.reviewTrick * 4 + App.reviewCard;
			boolean final_trick_incomplete = (played == review_card);
			if (final_trick_incomplete) {
				App.con.controlerInControl(final_trick_incomplete);
			}
		}

		App.gbp.matchPanelsToDealState();
		App.frame.repaint();
	}

	/**   
	 */
	public static void leftWingEdit() {
		if (App.deal.isDoneHand()) // so we skip the 'done hand'
			return;

		// App.clearDdsKept();

		if (App.isMode(Aaa.REVIEW_PLAY) /* || App.isMode(Aaa.REVIEW_BIDDING) && App.deal.isFinished() */) {
			App.deal.fastUndoBackTo(App.reviewTrick, App.reviewCard, false /* setDdsNextCard */);
		}

		App.gbp.c1_1__tfdp.makeCardSuggestions();
		App.setMode(Aaa.EDIT_PLAY);
		App.frame.repaint();
	}

	/**   
	 */
	static void reviewBackToStartOfBidding() {
		App.reviewBid = 0;
		validateBiddingIndex();
		App.frame.repaint();
	}

	/**   
	 */
	static void reviewBackOneBid() {
		App.reviewBid--;
		validateBiddingIndex();
		App.gbp.matchPanelsToDealState();
		App.frame.repaint();
	}

	/**   
	 */
	static void reviewFwdOneBid() {
		App.reviewBid++;
		validateBiddingIndex();
		App.gbp.matchPanelsToDealState();
		App.frame.repaint();
		if (App.reviewBid >= App.deal.countBids() && App.deal.isContractReal()) {
			App.setMode(Aaa.REVIEW_PLAY);
			App.reviewCard = 0;
			App.reviewTrick = 0;
			validateReviewIndexes();
			App.gbp.matchPanelsToDealState();
			App.frame.repaint();
		}
	}

	/**
	 */
	public static void editHandsAddRes() {
		if (App.isModeAnyReview()) {
			App.deal.eb_min_card = App.reviewTrick * 4 + App.reviewCard;
		}
		else {
			App.deal.eb_min_card = App.deal.countCardsPlayed();
		}
		App.deal.clearAnyKeptCards();  // these are set in linfiles only
		App.dealMajorChange();
		App.gbp.matchPanelsToDealState();
	}

	/**
	 */
	public static void editHandsRemoveRes() {
		App.deal.eb_min_card = 0;
		App.deal.clearAnyKeptCards();  // these are set in linfiles only
		App.deal.botExtra.clear_botHints();
		App.deal.botExtra.clear_botInstructions();
		App.dealMajorChange();
		App.gbp.matchPanelsToDealState();
	}

	/**
	 */
	public static void editHandsShuffOp() {
		if (App.deal.isDoneHand()) // so we skip the 'done hand'
			return;

		boolean old_localShowHidden = App.localShowHidden;

		boolean analyserWasOn = App.ddsAnalyserPanelVisible;
		App.ddsAnalyserPanelVisible = false;
		App.deal.clearPlayerNames();
		App.deal.shufOp_ShuffleDefendersHands();
		App.dealMajorChange();
		App.gbp.matchPanelsToDealState();

//		boolean locShowHidden = true;
//		if (App.localShowHiddPolicy == 0 /*hidden*/) {
//			locShowHidden = false;
//		}
//		else if (App.localShowHiddPolicy == 1 /*show*/) {
//			locShowHidden = true;
//		}
//		else if (App.localShowHiddPolicy == 2 /*leave as is */) {
//			locShowHidden = old_localShowHidden;
//		}

		boolean locShowHidden = old_localShowHidden;

		if (locShowHidden == false && App.deal.isContractReal()) { // only enter play if there is a contract
			App.setMode(Aaa.NORMAL_ACTIVE);
			App.gbp.c1_1__tfdp.makeCardSuggestions();
		}
		else {
			leftWingEdit();
		}

		App.localShowHidden = locShowHidden;

		App.frame.repaint();
		if (analyserWasOn) {
			ddsAnalyse();
		}
	}

	public static void editHands_ssS() {
		boolean analyserWasOn = App.ddsAnalyserPanelVisible;
		App.ddsAnalyserPanelVisible = false;
		App.deal.clearPlayerNames();
		App.deal.wipePlay();

		App.deal.swapSuits(Suit.Spades, Suit.Hearts);

		App.dealMajorChange();
		App.gbp.matchPanelsToDealState();
		App.frame.repaint();
		if (analyserWasOn) {
			ddsAnalyse();
		}
	}

	public static void editHands_ssH() {
		boolean analyserWasOn = App.ddsAnalyserPanelVisible;
		App.ddsAnalyserPanelVisible = false;
		App.deal.clearPlayerNames();
		App.deal.wipePlay();

		App.deal.swapSuits(Suit.Hearts, Suit.Diamonds);

		App.dealMajorChange();
		App.gbp.matchPanelsToDealState();
		App.frame.repaint();
		if (analyserWasOn) {
			ddsAnalyse();
		}
	}

	public static void editHands_ssD() {
		boolean analyserWasOn = App.ddsAnalyserPanelVisible;
		App.ddsAnalyserPanelVisible = false;
		App.deal.clearPlayerNames();
		App.deal.wipePlay();

		App.deal.swapSuits(Suit.Diamonds, Suit.Clubs);

		App.dealMajorChange();
		App.gbp.matchPanelsToDealState();
		App.frame.repaint();
		if (analyserWasOn) {
			ddsAnalyse();
		}
	}

	public static void editHands_ssC() {
		boolean analyserWasOn = App.ddsAnalyserPanelVisible;
		App.ddsAnalyserPanelVisible = false;
		App.deal.clearPlayerNames();
		App.deal.wipePlay();

		App.deal.swapSuits(Suit.Clubs, Suit.Spades);

		App.dealMajorChange();
		App.gbp.matchPanelsToDealState();
		App.frame.repaint();
		if (analyserWasOn) {
			ddsAnalyse();
		}
	}

	public static void editHands_low() {
		boolean analyserWasOn = App.ddsAnalyserPanelVisible;
		App.ddsAnalyserPanelVisible = false;
		App.deal.clearPlayerNames();
		App.deal.wipePlay();

		int upto_default = 6; // magic number

		int upto = App.gbp.c0_0__tlp.getuptoValue(upto_default);

		App.deal.suffleLowerCards(upto);

		App.dealMajorChange();
		App.gbp.matchPanelsToDealState();
		App.frame.repaint();
		if (analyserWasOn) {
			ddsAnalyse();
		}
	}

	/**   
	 */
	static void editHands() {
		// App.deal.clearPlayerNames();
		App.setMode(Aaa.EDIT_HANDS);
		App.gbp.matchPanelsToDealState();
		App.frame.repaint();
	}

	static void editBidding() {
		App.deal.showBidQuestionMark = false;
		App.deal.clearPlayerNames();
		App.deal.wipeContractAndPlay();
		App.setMode(Aaa.EDIT_BIDDING);
		App.gbp.matchPanelsToDealState();
		App.frame.repaint();
	}

	static void editBiddingWipe() {
		App.deal.wipeContractBiddingAndPlay();
		App.dealMajorChange();
		App.frame.repaint();
	}

	static void editPlay2() {
		editPlay();
	}

	static void editPlay() {
		App.deal.clearPlayerNames();
		App.setMode(Aaa.EDIT_PLAY);
		App.gbp.matchPanelsToDealState();
		App.frame.repaint();
	}

	static void editPlayXall() {
		App.deal.clearPlayerNames();
		App.deal.wipePlay();
		App.dealMajorChange();
		App.frame.repaint();
	}

	static void editHandsRotateAnti() {
		// App.deal.clearPlayerNames();
		App.deal.rotateHands(-1);
		App.gbp.dealDirectionChange();
		App.gbp.dealMajorChange();
		App.gbp.matchPanelsToDealState();
		App.frame.repaint();
		App.frame.invalidate();
	}

	static void editHandsSwapOpps() {
		// App.deal.clearPlayerNames();
		App.deal.swapOppsHands();
		App.gbp.dealDirectionChange();
		App.dealMajorChange();
		App.gbp.matchPanelsToDealState();
		App.frame.repaint();
		App.frame.invalidate();
	}

	static void editHandsRotateClock() {
		// App.deal.clearPlayerNames();
		App.deal.rotateHands(+1);
		App.gbp.dealDirectionChange();
		App.dealMajorChange();
		App.gbp.matchPanelsToDealState();
		App.frame.repaint();
		App.frame.invalidate();
	}

	static void runTests() {
		TestSystemRunner.performAllTests();
	}

	/**   
	 */
	public static void actionPerfString(String actCmd) { // called by the Controller who is the ActionListener
		// ==============================================================================================
		// System.out.println(actCmd + " invoked");
		try {

			App.con.stopAllTimers();

			CmdHandler.class.getDeclaredMethod(actCmd).invoke(CmdHandler.class);

		} catch (NoSuchMethodException e1) {
			// e1.printStackTrace();
		} catch (SecurityException e1) {
			// e1.printStackTrace();
		} catch (IllegalAccessException e) {
			// e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// e.printStackTrace();
		} catch (InvocationTargetException e) {
			// e.printStackTrace();
		}
	}

	/**   
	 */
	public static String checkExtension(String s) {
		// ==============================================================================================
		if (s.endsWith(App.dotLinExt) == false) {
			s += App.dotLinExt;
		}
		return s;
	}

	/**   
	 */
	public static String makeDealFileNameAndPath(Deal deal, String pathWithSep, String origName, boolean allow_xxx) {
		// ==============================================================================================
		String s;

		if (pathWithSep == null || pathWithSep.contentEquals("")) {
			pathWithSep = App.temp_Other_folder;
		}

		if (App.isFilenameThrowAway(origName)) {
			origName = "";
		}

		if (App.force_savename_xxx && allow_xxx) {
			pathWithSep = App.realSaves_folder;
			s = pathWithSep + App.xxx_lin_name;
		}
		else if (origName.contentEquals("")) {
			long r = Util.reverseOrderNumber();
			String id = Util.last_4_chars("" + r);
			SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd  HH.mm");
			String dateText = sdfDate.format(new Date());
			s = pathWithSep + r + " ~" + id + " " + deal.contractAndResult() + "  " + dateText;

			// s += "__" + Aaa.cleanString(deal.ahHeader, true /* true => spaceOk */);
			if (deal.displayBoardId.length() > 0) {
				s += "_Board_" + deal.displayBoardId;
			}
		}
		else {
			s = pathWithSep + origName;
		}

		return checkExtension(s);
	}

	/**
	 */
	static void commonStepBack() {

		App.mouseWheelDoes = App.WMouse_STEP;

		if (App.cameFromPbnOrSimilar() && App.pbnAutoEnter) {
			Controller.Left_keyPressed();
			return;
		}

		if (App.isVmode_Tutorial())
			App.mg.tutorialBackOne();
		else if (App.isMode(Aaa.REVIEW_BIDDING))
			reviewBackOneBid();
		else if (App.isMode(Aaa.REVIEW_PLAY))
			reviewBackOneTrick();

		App.gbp.matchPanelsToDealState();
		App.frame.repaint();
	}

	/**
	 */
	static void commonFlowBack() {

		App.mouseWheelDoes = App.WMouse_FLOW;

		if (App.cameFromPbnOrSimilar() && App.pbnAutoEnter) {
			Controller.Left_keyPressed();
			return;
		}

		if (App.isVmode_Tutorial())
			App.mg.tutorialBackOne();
		else if (App.isMode(Aaa.REVIEW_BIDDING))
			reviewBackOneBid();
		else if (App.isMode(Aaa.REVIEW_PLAY))
			reviewBackOneTrick();

		App.gbp.matchPanelsToDealState();
		App.frame.repaint();
	}

	/**
	 */
	static void commonStepFwd() { // also called by "inside a deal" Step Forward

		App.mouseWheelDoes = App.WMouse_STEP;

		if (App.cameFromPbnOrSimilar() && App.pbnAutoEnter) {
			Controller.Right_keyPressed();
			return;
		}

		if (App.isVmode_Tutorial())
			App.mg.tutorialStepFwd();
		else if (App.isMode(Aaa.REVIEW_BIDDING))
			reviewFwdOneBid(); // reviewStepShowBidding();
		else if (App.isMode(Aaa.REVIEW_PLAY)) {
			reviewFwdOneTrick();
		}

		App.gbp.matchPanelsToDealState();
		App.frame.repaint();
	}

	/**
	 */
	static void commonFlowFwd() { // also called by "inside a deal" Flow Forward

		App.mouseWheelDoes = App.WMouse_FLOW;

		if (App.cameFromPbnOrSimilar() && App.pbnAutoEnter) {
			Controller.Right_keyPressed();
			return;
		}

		if (App.isVmode_Tutorial())
			App.mg.tutorialFlowFwd(App.isMode(Aaa.NORMAL_ACTIVE));
		else if (App.isMode(Aaa.REVIEW_BIDDING))
			reviewFwdOneBid();
		else if (App.isMode(Aaa.REVIEW_PLAY))
			reviewFwdShowOneTrick();

		App.gbp.matchPanelsToDealState();
		App.frame.repaint();
	}

	/**
	 */
	static void dealmodeBackToMovie() {
		App.ddsAnalyserPanelVisible = false;
		MassGi_utils.do_dealmodeBackToMovie();
		App.frame.repaint();
	}

	/**
	 */
	public static void tutorialIntoDealStd() {
		MassGi_utils.do_tutorialIntoDealStd();
		App.frame.repaint();
	}

	/**
	 */
	public static void tutorialIntoDealClever() {
		MassGi_utils.do_tutorialIntoDealClever();
		App.frame.repaint();
	}

	/**
	 */
	public static void tutorialIntoDealContdds() {
		MassGi_utils.do_tutorialIntoDealContdds();
		App.gbp.matchPanelsToDealState();
		// App.frame.repaint();
	}

	/**
	 */
	public static void tutorialIntoDealB1st() {
		MassGi_utils.do_tutorialIntoDealB1st();
		App.gbp.matchPanelsToDealState();
		// App.frame.repaint();
	}

	/**
	 */
	static void questionTellMe() {
		MassGi_utils.do_tutorialTellMe();
		App.frame.repaint();
	}

	/**
	 */
	public static void question_z_Next() {
		App.lbx_nextAndTellClicked = false;
		App.book.loadChapterByIndex(App.book.lastChapterIndexLoaded);

		App.frame.rop.setSelectedIndex(App.RopTab_3_DFC);

		App.frame.repaint();
	}

	/**
	 */
	static void question_z_NextAndTell() {
		App.lbx_nextAndTellClicked = true;
		App.book.loadChapterByIndex(App.book.lastChapterIndexLoaded);

		App.frame.repaint();
	}

	/**
	 */
	static void question_z_Options() {
//		App.lbx_nextAndTellClicked = false;
		App.frame.executeCmd("rightPanelPrefs3_DFC");
		App.frame.payloadPanelShaker();
		App.frame.repaint();
	}

	/**
	 */
	static void question_z_Video() {
		App.frame.executeCmd("playVideo_distrFlashCards");
	}

	/**
	 */
	static void question_z_Learn() {
		App.frame.executeCmd("openPage_MemorizeDistributions");
	}

	/**
	 */
	public static void question_z_Step() {
		commonStepFwd();
	}

	/**
	 */
	public static void playBridgeBlueCenter() {
		App.deal = new Deal(Deal.makeDoneHand, Dir.South);
		App.mg = new MassGi(App.deal);
		App.switchToDeal(App.deal);

		if (App.showRedNewBoardArrow) {
			App.gbo.showNewBoardHint();
		}

		if (App.showRedDividerArrow) {
			App.gbo.showDividerHint();
		}
	}

}
