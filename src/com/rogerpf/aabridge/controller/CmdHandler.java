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
		new RpfBtnDef( "menuSaveStd",				"Save",				"Does a standard Save, offering you a file name if one has not been set.  ALWAYS automaticaly overwrites an older file  "),
		new RpfBtnDef( "menuSaveAs",				"Save As",			"Saves the deal, you always get a chance to set or change the filename  "),

		new RpfBtnDef( "mainAnti",					"<",				"Rotate the seats Anti-clockwise  "),
		new RpfBtnDef( "mainClock",					">",				"Rotate the seats Clockwise  "),
		new RpfBtnDef( "claimBtn",					"Claim",			"Claim  -  Claim as many of the remaining tricks as you wish.  Your claim we be accepted and not tested.  "),
		new RpfBtnDef( "mainUndo",					"Undo",				""),
		new RpfBtnDef( "mainNewBoard",				"New Board",		"Discard the existing hands, shuffle and deal the next board  (always does an AutoSave first)  "),

		new RpfBtnDef( "leftWingEdit",				"Edit",				"Edit - wipes and play after this point and  THEN  lets you  Edit  the play, bidding  or even the cards  "),
		new RpfBtnDef( "leftWingNormal",			"Play",		        "Bid-Play - lets you  Bid or Play  more of the hand  "), 
		new RpfBtnDef( "leftWingReview",			"Review",			"Review - lets you  Review  the cards played so far  "), 
                                                                        
		new RpfBtnDef( "reviewBackOneCard",			"<",				"Go back one card  best used with  DDS  then Mouse Wheel"),
		new RpfBtnDef( "reviewFwdOneCard",			">",		  		"Go forward one card  best used with  DDS  then Mouse Wheel"),
		new RpfBtnDef( "ddsShowBids",				"Show Bids", 		"Restores the Bidding Table without having to load another hand"),
		new RpfBtnDef( "ddsAnalyse",				"Analyse", 			"Works out the highest number of tricks that each side can win in any suit or No Trumps"),
		new RpfBtnDef( "ddsLabel",					"DDS", 				"DDS  -  Double Dummy Solver  -   On  /  Off"),
		new RpfBtnDef( "ddsScoreOnOff",				"On", 			    "On/Off Shows what WILL happen when you click the button "),
		new RpfBtnDef( "hiddenHandsShowHide",		"Show", 			"Show / Hide the normally hidden hands "),
		new RpfBtnDef( "hiddenHandsClick1",			"Click  ", 			"Click a name panel - to show (and become) that hand  "),
		new RpfBtnDef( "hiddenHandsClick2",			"a Name  ", 		"Click a name panel - to show (and become) that hand  "),

		new RpfBtnDef( "reviewBackOneBid",			"<",		  		"Go back one bid  "),
		new RpfBtnDef( "reviewFwdOneBid",			">",		  		"Go forward one bid  "),

		new RpfBtnDef( "editHandsShuffWeak",		"Shuf Op",			"Shuf Op - Shuffle the weakest pair of hands and jump directly into Play/Bid  "),
		new RpfBtnDef( "editHands",					"Hands",			"Drag and Drop the cards from hand to hand  "),
		new RpfBtnDef( "editHandsRotateAnti",		"<",				"Rotate the CARDS Anti-clockwise - this is a TRUE ROTATE - the SEATS 'N S E W' do NOT move  "),
		new RpfBtnDef( "editHandsRotateClock",		">",				"Rotate the CARDS Clockwise - this is a TRUE ROTATE - the SEATS 'N S E W' do NOT move  "),
		new RpfBtnDef( "editBidding",				"Bidding",			"Edit / Add  Bidding for all four hands  "),
		new RpfBtnDef( "editBiddingWipe",			"Wipe",				"Remove All the existing BIDDING  "),
		new RpfBtnDef( "editPlay",					"Edit Play",		"Edit / Add  Play for all four hands  "),
		new RpfBtnDef( "editPlayWipe",				"Wipe",			    "Remove All the existing PLAY  "),

		new RpfBtnDef( "commonStepBack",			"<",				"Go back one trick  "),
		new RpfBtnDef( "commonFlowBack",			"<",				"Go back one trick  "),
		new RpfBtnDef( "commonStepFwd",				"Step  >",			"Jump Forward to the next stopping point  "),
		new RpfBtnDef( "commonFlowFwd",				"Flow  >",			"Run Forward to the next stopping point - showing each card being played  "),
		new RpfBtnDef( "dealmodeBackToMovie",		"Back  to Movie",	"Continues the  Tutorial  also know as - Bridge movie  "),
		new RpfBtnDef( "tutorialIntoDealClever",	"Enter the Deal",	"Enter the Deal  and go into   'Review' mode   *Right Click*  on the  'Nav Bar'  is a short cut  so you won't see the hands   "),

		new RpfBtnDef( "questionTellMe",			"Tell Me",			"Shows you the Answer  "),
		new RpfBtnDef( "question_z_Next",			"New",				"New - Show New  Hand Shape  question  "),
		new RpfBtnDef( "question_z_NextAndTell",	"New & Tell",		""),
		new RpfBtnDef( "question_z_Options",		"Options",			"Show - Distribution Flash Card - options  "),
		new RpfBtnDef( "question_z_Train",			"Train",			""),
		new RpfBtnDef( "question_z_Exam",			"Exam",				""),
		
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
	}

	/**   
	 */
	static void mainClock() {
		// ==============================================================================================
		if (App.deal.isDoneHand()) // so we skip the 'done hand'
			return;

		App.incOffsetClockwise(); // includes repaint
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
				hand.undoLastBid();
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

		App.respectLinYou = true;
		App.frame.rop.p2_SeatChoice.respectLinYouSetBy_mainNewBoard();

		App.flowOnlyCommandBar = false; // ugly should not need to do this here DO WE?
		App.hideCommandBar = false; // ugly should not need to do this here DO WE?
		App.hideTutNavigationBar = false; // ugly should not need to do this here DO WE?
		App.ddsAnalyserVisible = false;

		App.setMode(Aaa.NORMAL_ACTIVE);
		App.reviewTrick = 0;
		App.reviewCard = 0;
		App.reviewBid = 0;
		App.gbp.c1_1__tfdp.clearAllCardSuggestions();

		Deal deal = Deal.newBoard(App.deal.realBoardNo, (App.watchBidding == false), App.dealCriteria, App.youSeatForNewDeal);
		App.mg = new MassGi(deal);
		App.switchToNewMassGi("");

		App.calcCompassPhyOffset();
		App.frame.setTitleAsRequired();

		App.gbp.c0_2__blp.hideClaimButtonsIfShowing();

		App.dealMajorChange();
		App.gbp.matchPanelsToDealState();
		App.frame.repaint();
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
	public static void AnalyserNewBoard(Deal deal) {
		// ==============================================================================================
//		doAutoSave();

//		boolean refreshSeatChoice = (App.respectLinYou == false);

		App.respectLinYou = true;
		App.frame.rop.p2_SeatChoice.respectLinYouSetBy_mainNewBoard();

		App.flowOnlyCommandBar = false; // ugly should not need to do this here DO WE?
		App.hideCommandBar = false; // ugly should not need to do this here DO WE?
		App.hideTutNavigationBar = false; // ugly should not need to do this here DO WE?
		App.ddsAnalyserVisible = false;

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

		if (App.isFilenameThrowAway(App.deal.lastSavedAsFilename)) {
			saveCommon("Save");
		}
		else {
			String dealName = makeDealFileNameAndPath(App.deal, App.deal.lastSavedAsPathWithSep, App.deal.lastSavedAsFilename);

			saveDealAsSingleLinFile(App.deal, dealName);

			App.deal.lastSavedAsFilename = new File(dealName).getName();

			App.frame.setTitleAsRequired();
		}
	}

	/**   
	 */
	static void menuSaveAs() {
		// ==============================================================================================
		saveCommon("SaveAs");
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
			Lin.saveDealAsSingleLinFile(deal, writer);
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
	static void leftWingNormal() { // button also shows with name 'Normal'
		// ==============================================================================================
		if (App.deal.isDoneHand()) { // so we skip the 'done hand'
			return;
		}

		App.setMode(Aaa.NORMAL_ACTIVE);

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
		if (App.ddsScoreShow == false) {
			App.savePreferences();
		}
		ddsShowBids();
	}

	/**   
	 */
	static void ddsShowBids() {
		if (App.deal.isDoneHand()) // so we skip the 'done hand'
			return;
		App.gbp.c2_0__ddsAnal.showBidsButtonClicked();
	}

	/**   
	 */
	static void ddsAnalyse() {
		App.gbp.c2_0__ddsAnal.analyseButtonClicked();
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
		if (!atEnd && App.isMode(Aaa.REVIEW_PLAY) && App.reviewCard < 4) {
			App.gbp.c1_1__tfdp.reviewTrickDisplayTimer.start();
		}
		App.gbp.matchPanelsToDealState();
		App.frame.repaint();
	}

	/**   
	 */
	static void leftWingEdit() {
		if (App.deal.isDoneHand()) // so we skip the 'done hand'
			return;
		if (App.isMode(Aaa.REVIEW_PLAY) /* || App.isMode(Aaa.REVIEW_BIDDING) && App.deal.isFinished() */) {
			App.deal.fastUndoBackTo(App.reviewTrick, App.reviewCard, false /* setDdsNextCard */);
		}
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
	public static void editHandsShuffWeak() {
		App.deal.eb_blocker = false;
		App.ddsAnalyserVisible = false;
		App.deal.clearPlayerNames();
		App.deal.ShuffleWeakestAxisHands();
		App.dealMajorChange();
		App.gbp.matchPanelsToDealState();
		App.setMode(Aaa.NORMAL_ACTIVE);
		App.frame.repaint();
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
		App.deal.clearPlayerNames();
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
		App.deal.clearPlayerNames();
		App.deal.rotateHands(-1);
		App.dealMajorChange();
		App.gbp.matchPanelsToDealState();
		App.frame.repaint();
	}

	static void editHandsRotateClock() {
		App.deal.clearPlayerNames();
		App.deal.rotateHands(+1);
		App.dealMajorChange();
		App.gbp.matchPanelsToDealState();
		App.frame.repaint();
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

		if (origName.contentEquals("")) {

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
		App.ddsAnalyserVisible = false;
		MassGi_utils.do_dealmodeBackToMovie();
		App.frame.repaint();
		// App.biddingVisibilityCheck();
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
	static void questionTellMe() {
		MassGi_utils.do_tutorialTellMe();
		App.frame.repaint();
	}

	/**
	 */
	public static void question_z_Next() {
		App.lbx_nextAndTellClicked = false;
		App.book.loadChapterByIndex(App.book.lastChapterIndexLoaded);
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
