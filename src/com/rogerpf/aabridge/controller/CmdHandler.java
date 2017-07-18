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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.rogerpf.aabridge.igf.MassGi;
import com.rogerpf.aabridge.igf.MassGi_utils;
import com.rogerpf.aabridge.model.Deal;
import com.rogerpf.aabridge.model.Dir;
import com.rogerpf.aabridge.model.Hand;
import com.rogerpf.aabridge.model.Lin;
import com.rogerpf.aabridge.model.Suit;

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
		//             "openAutoSavesFolder" entry EXISTS but not as a button
		//             "openSavesFolder" entry EXISTS but not as a button
		//             "runTests"        entry EXISTS but not as a button
		//             "nullCommand"     entry EXISTS but not as a button
		new RpfBtnDef( "menuSaveStd",				Aaf.gT("lhp.save"),				Aaf.gT("lhp.save_TT")),
		new RpfBtnDef( "menuSaveAs",				Aaf.gT("lhp.saveAs"),			Aaf.gT("lhp.saveAs_TT")),
	                                                
		new RpfBtnDef( "editHandsRemoveRes",		Aaf.gT("lhp.eH.remRes"),		Aaf.gT("lhp.eH.remRes_TT")),
		new RpfBtnDef( "editHandsShuffOp",		    Aaf.gT("lhp.eH.shufOp"),		Aaf.gT("lhp.eH.shufOp_TT")),
		new RpfBtnDef( "editHands_ssS",		        Aaf.gT("lhp.eH.s"),				Aaf.gT("lhp.eH.s_TT")),
		new RpfBtnDef( "editHands_ssH",		        Aaf.gT("lhp.eH.h"),				Aaf.gT("lhp.eH.h_TT")),
		new RpfBtnDef( "editHands_ssD",		        Aaf.gT("lhp.eH.d"),				Aaf.gT("lhp.eH.d_TT")),
		new RpfBtnDef( "editHands_ssC",		        Aaf.gT("lhp.eH.c"),				Aaf.gT("lhp.eH.c_TT")),
		new RpfBtnDef( "editHands_low",		        Aaf.gT("lhp.eH.low"),			Aaf.gT("lhp.eH.low_TT")),
		new RpfBtnDef( "editHands",					Aaf.gT("lhp.eH.hands"),			Aaf.gT("lhp.eH.hands_TT")),
		new RpfBtnDef( "editHandsRotateAnti",		"<",							Aaf.gT("lhp.eH.anti_TT")),
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
		new RpfBtnDef( "mainUndo",					Aaf.gT("gbl.undo"),				Aaf.gT("gbl.undo_TT")),
                                                    
		new RpfBtnDef( "ddsAnalyse",				Aaf.gT("rhp.analyse"),			Aaf.gT("rhp.analyse_TT")),
		new RpfBtnDef( "ddsReinstateAnalyser",		Aaf.gT("rhp.keepon"),			Aaf.gT("rhp.keepon_TT")),
		new RpfBtnDef( "ddsLabel",					Aaf.gT("rhp.dds"),				Aaf.gT("rhp.dds_TT")),
		new RpfBtnDef( "ddsScoreOnOff",				Aaf.rhp_isOn,				    Aaf.gT("rhp.isOn_TT")),
		new RpfBtnDef( "hiddenHandsShowHide",		Aaf.gT("rhp.show"),				Aaf.gT("rhp.show_TT")),
		new RpfBtnDef( "hiddenHandsClick1",			Aaf.gT("rhp.click"),			Aaf.gT("rhp.click_TT")),
		new RpfBtnDef( "hiddenHandsClick2",			Aaf.gT("rhp.aName"),			Aaf.gT("rhp.aName_TT")),
                                                    
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
		new RpfBtnDef( "tutorialIntoDealCont",		Aaf.gT("cmdBar.cont"),			Aaf.gT("cmdBar.cont_TT")),
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
	static void mainUndo() {
		// ==============================================================================================
		if (App.deal.isDoneHand()) // so we skip the 'done hand'
			return;

		if (App.isModeAnyReview())
			return;

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

		if (App.deal.eb_blocker && (App.deal.eb_min_card >= App.deal.countCardsPlayed())) {
			; // we do nothing i.e. do NOT allow the undo
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

			if (App.deal.countCardsPlayed() > 0) {
				Hand hand = App.deal.getLastHandThatPlayed();
				hand.undoLastPlay();
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
				Hand hand = App.deal.getLastHandThatPlayed();
				hand.undoLastPlay();
				if (App.isAutoPlay(hand.compass) == false) {
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
	}

	/**   
	 */
	public static void mainNewBoard() {
		// ==============================================================================================
		doAutoSave();

//		boolean refreshSeatChoice = (App.respectLinYou == false);

		boolean re_analyse = App.ddsAnalyserPanelVisible && App.reinstateAnalyser;
		App.ddsAnalyserPanelVisible = false;

		boolean re_dds = App.devMode && App.ddsScoreShow;
		App.ddsScoreShow = false;

		App.allTwister_reset();
		App.respectLinYou = true;

		App.frame.rop.p2_SeatChoice.respectLinYouSetBy_mainNewBoard(true /* skip_alwaysShowHidden */);

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
			App.frame.aaDragGlassPane.showNewDealScreen();
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
	public static void AnalyserNewBoard(Deal deal) {
		// ==============================================================================================
//		doAutoSave();

//		boolean refreshSeatChoice = (App.respectLinYou == false);

		App.respectLinYou = true;
		App.frame.rop.p2_SeatChoice.respectLinYouSetBy_mainNewBoard(false /* skip_alwaysShowHidden */);

//		App.handPanelNameAreaInfoNumbersShow = true;

		App.flowOnlyCommandBar = false; // ugly should not need to do this here DO WE?
		App.hideCommandBar = false; // ugly should not need to do this here DO WE?
		App.hideTutNavigationBar = false; // ugly should not need to do this here DO WE?
		App.ddsAnalyserPanelVisible = false;

		App.reviewTrick = 0;
		App.reviewCard = 0;
		App.reviewBid = 0;
		App.gbp.c1_1__tfdp.clearAllCardSuggestions();

//		Deal deal = Deal.newBoard(App.deal.realBoardNo, (App.watchBidding == false), App.dealCriteria, App.youSeatForNewDeal);

		App.mg = new MassGi(deal);
		App.switchToNewMassGi("");

		App.calcCompassPhyOffset();
		App.frame.setTitleAsRequired();

		App.gbp.c0_2__blp.hideClaimButtonsIfShowing();

		App.dealMajorChange();
		App.gbp.matchPanelsToDealState();
		App.frame.repaint();

		App.setMode(Aaa.EDIT_PLAY);

		if (App.isPauseAtEotClickWanted()) {
			App.gbp.c1_1__tfdp.setShowCompletedTrick();
		}
		if (App.deal.isBidding())
			App.con.startAutoBidDelayTimerIfNeeded();
		else
			App.gbp.c1_1__tfdp.normalTrickDisplayTimer_startIfNeeded();

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
	static void openAutoSavesFolder() {
		// ==============================================================================================
		try {
			Desktop.getDesktop().open(new File(App.autoSavesPath));
		} catch (IOException e) {
		}
	}

	/**   
	 */
	static void openCmdsFolder() {
		// ==============================================================================================
		try {
			Desktop.getDesktop().open(new File(App.cmdsAndPhpPath));
		} catch (IOException e) {
		}
	}

	/**   
	 */
	static void openTempMyHandsFolder() {
		// ==============================================================================================
		try {
			Desktop.getDesktop().open(new File(App.tempMyHandsPath));
		} catch (IOException e) {
		}
	}

	/**   
	 */
	static void execute_aamh() {
		// ==============================================================================================
		try {
			MassGi_utils.launch__aamh();
			App.frame.delayed_process__temp_MyHands();
		} catch (Exception e) {
		}
	}

	/**   
	 */
	static void openSavesFolder() {
		// ==============================================================================================
		try {
			Desktop.getDesktop().open(new File(App.realSavesPath));
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
		fc.setCurrentDirectory(new File(App.realSavesPath));
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
	public static void doAutoSave() {
		// ==============================================================================================
		if (App.deal.worthAutosavingSaving() == false)
			return;

		try {

			String dealName = makeDealFileNameAndPath(App.deal, "", "");

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
	public static void doDebugAutoSave(Deal d) {
		// ==============================================================================================
		try {

			SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd__HH-mm-ss");

			String s = "000" + incval++;
			s = s.substring(s.length() - 4);

			String dealName = App.autoSavesPath + "a__" + sdfDate.format(new Date()) + "__" + s + ".lin";

			saveDealAsSingleLinFile(d, dealName);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**   
	 */
	static void editPlayWipe() {
		// ==============================================================================================
		if (App.deal.isDoneHand()) { // so we skip the 'done hand'
			return;
		}
		doAutoSave();

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
				mainUndo(); // this will undo the last two cards or any claim
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
			String dealName = makeDealFileNameAndPath(App.deal, App.deal.lastSavedAsPathWithSep, App.deal.lastSavedAsFilename);

			saveDealAsSingleLinFile(App.deal, dealName);

			App.deal.lastDealNameSaved_FULL = new File(dealName).getAbsolutePath();

			App.deal.lastSavedAsFilename = new File(dealName).getName();

			App.frame.setTitleAsRequired();
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
			pathWithSep = App.realSavesPath;
		}

		fc.setCurrentDirectory(new File(pathWithSep));
		fc.setDialogTitle(type);

		String dealName = makeDealFileNameAndPath(App.deal, pathWithSep, App.deal.lastSavedAsFilename);

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

		App.frame.setTitleAsRequired();
	}

	/**  
	 * Empty string return shows OK 
	 */
	public static String saveDealAsSingleLinFile(Deal deal, String dealName) {
		// ==============================================================================================
		try {
			FileOutputStream fileOut = new FileOutputStream(dealName);

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fileOut, "utf-8"));
			Lin.saveDealAsSingleLinFileBW(deal, "", writer);
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

		if (App.deal.eb_blocker && ((App.reviewTrick * 4 + App.reviewCard) < App.deal.eb_min_card)) {
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

		App.show_poor_def_msg = true;

		if (App.isMode(Aaa.REVIEW_PLAY) /* || App.isMode(Aaa.REVIEW_BIDDING) && App.deal.isFinished() */) {
			App.deal.fastUndoBackTo(App.reviewTrick, App.reviewCard, false /* setDdsNextCard */);
		}

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

		if (App.reviewCard == 0 && App.reviewTrick == 0) { // except stop after opening lead
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
		if (App.reviewCard != 4 && !(App.reviewCard == 1 && App.reviewTrick == 0 /* Opening lead excluded from Flow */))
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
	static void hiddenHandsShowHide() {
		if (App.deal.isDoneHand()) // so we skip the 'done hand'
			return;
		App.localShowHidden = !App.localShowHidden;
		App.calcApplyBarVisiblity();
	}

	/**   
	 */
	static void ddsScoreOnOff() {
		if (App.deal.isDoneHand()) // so we skip the 'done hand'
			return;
		App.ddsScoreShow = !App.ddsScoreShow;
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
	static void ddsAnalyse() {
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

		if (!atEnd && App.isMode(Aaa.REVIEW_PLAY)) {
			if (App.reviewCard < 4) {
				App.gbp.c1_1__tfdp.reviewTrickDisplayTimer.start();
			}
			else {
				App.con.controlerInControl();
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
	public static void reviewBidDisplayTimerFired() {
		int old_rb = App.reviewBid;
		App.reviewBid++;
		validateBiddingIndex();
		App.frame.repaint();
		if (App.reviewBid >= App.deal.countBids() && App.deal.isContractReal()) {
			App.setMode(Aaa.REVIEW_PLAY);
			App.reviewCard = 0;
			App.reviewTrick = 0;
			validateReviewIndexes();
			App.gbp.matchPanelsToDealState();
			App.frame.repaint();
			return;
		}
		if (old_rb != App.reviewBid) {
			App.gbp.c1_1__bfdp.reviewBidDisplayTimer.start();
		}
	}

	/**   
	 */
	static void reviewFwdShowBidding() {
		if (App.reviewBid >= App.deal.countBids())
			App.reviewBid = 0;
		validateBiddingIndex();
		App.frame.repaint();
		App.gbp.c1_1__bfdp.reviewBidDisplayTimer.start();
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
	public static void editHandsRemoveRes() {
		App.deal.eb_blocker = false;
		App.deal.eb_min_card = 0;
		App.deal.clearAnyKeptCards();
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
		if (App.deal.isContractReal()) {
			App.setMode(Aaa.NORMAL_ACTIVE);
			App.gbp.c1_1__tfdp.makeCardSuggestions();
		}

		if (App.localShowHiddPolicy == 0 /*hidden*/) {
			App.localShowHidden = false;
		}
		else if (App.localShowHiddPolicy == 1 /*show*/) {
			App.localShowHidden = true;
		}
		else if (App.localShowHiddPolicy == 2 /*leave as is */) {
			App.localShowHidden = old_localShowHidden;
		}

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
		App.deal.eb_blocker = false;
		App.deal.clearPlayerNames();
		App.setMode(Aaa.EDIT_HANDS);
		App.gbp.matchPanelsToDealState();
		App.frame.repaint();
	}

	static void editBidding() {
		App.deal.eb_blocker = false;
		App.deal.showBidQuestionMark = false;
		App.deal.clearPlayerNames();
		App.deal.wipeContractAndPlay();
		App.setMode(Aaa.EDIT_BIDDING);
		App.gbp.matchPanelsToDealState();
		App.frame.repaint();
	}

	static void editBiddingWipe() {
		App.deal.eb_blocker = false;
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
	public static String makeDealFileNameAndPath(Deal deal, String pathWithSep, String origName) {
		// ==============================================================================================
		String s;

		if (pathWithSep == null || pathWithSep.contentEquals("")) {
			pathWithSep = App.autoSavesPath;
		}

		if (App.isFilenameThrowAway(origName)) {
			origName = "";
		}

		if (App.force_savename_xxx) {
			if (pathWithSep.contentEquals(App.autoSavesPath)) {
				pathWithSep = App.realSavesPath;
			}
			s = pathWithSep + App.xxx_lin_name;
		}
		else if (origName.contentEquals("")) {

			SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd__HH-mm-ss");
			s = pathWithSep + "d__" + sdfDate.format(new Date()) + "__";

			s += deal.contractAndResult();

			s += "__" + Aaa.cleanString(deal.ahHeader, false /* true => spaceOk */);
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
			App.mg.tutorialFlowFwd((App.movieBidFlowDoesFlow == false) && App.isMode(Aaa.NORMAL_ACTIVE));
		else if (App.isMode(Aaa.REVIEW_BIDDING))
			reviewFwdShowBidding();
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
	public static void tutorialIntoDealCont() {
		MassGi_utils.do_tutorialIntoDealCont();
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
