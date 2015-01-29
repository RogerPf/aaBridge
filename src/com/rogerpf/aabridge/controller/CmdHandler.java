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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.rogerpf.aabridge.igf.MassGi;
import com.rogerpf.aabridge.igf.MassGi_utils;
import com.rogerpf.aabridge.model.Deal;
import com.rogerpf.aabridge.model.DepFin;
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

		new RpfBtnDef( "depFinOut",					"df Exp",			"deepFinesse - Write (export) the current deal to the file  -  " + App.depFinOutInBoth + "  "),
		new RpfBtnDef( "depFinIn",					"Im",				"deepFinesse - Read (import) the 'LAST' deal from the file  -  " + App.depFinOutInBoth + "  "),

		new RpfBtnDef( "mainAnti",					"<",				"Rotate the seats Anti-clockwise  "),
		new RpfBtnDef( "mainClock",					">",				"Rotate the seats Clockwise  "),
		new RpfBtnDef( "claimBtn",					"Claim",			"Claim  -  Claim as many of the remaining tricks as you wish.  Your claim we be accepted and not tested.  "),
		new RpfBtnDef( "mainUndo",					"Undo",				""),
		new RpfBtnDef( "mainNewBoard",				"New Board",		"Discard the existing hands, shuffle and deal the next board  (always does and AutoSave first)  "),

		new RpfBtnDef( "leftWingReview",			"Review",			"Review - change mode so you can  Review  the hand  "), 
		new RpfBtnDef( "leftWingNormal",			"Play",		        "Bid-Play - change mode so you can  Bid or Play  the hand'  "), 
		new RpfBtnDef( "leftWingEdit",				"Edit",				"Edit - change mode so you can  Edit  the hand  "),
                                                                        
 		new RpfBtnDef( "reviewBackToStartOfPlay",	"<<<",				"Rewind to the start of play  "),
 		new RpfBtnDef( "reviewFwdToEndOfPlay",  	">>>",				"Jump forward to the end of play  "),
		new RpfBtnDef( "reviewBackOneTrick",		"<<",				"Jump back one trick  "),
		new RpfBtnDef( "reviewFwdOneTrick",			">>",				"Jump forward one trick  "),
		new RpfBtnDef( "reviewFwdShowOneTrick",		"Play 1",			"Play Forward 1 trick showing each card being played  "),
		new RpfBtnDef( "reviewBackOneCard",			"<",				"Go back one card  "),
		new RpfBtnDef( "reviewFwdOneCard",			">",		  		"Go forward one card  "),
		new RpfBtnDef( "hiddenHandsShowHide",		"Show", 			"Show / Hide the normally hidden hands "),
		new RpfBtnDef( "hiddenHandsClick1",			"Click  ", 			"Click a name panel - to show (and become) that hand  "),
		new RpfBtnDef( "hiddenHandsClick2",			"a Name  ", 		"Click a name panel - to show (and become) that hand  "),

 		new RpfBtnDef( "reviewBackToStartOfBidding","<<<",				"Rewind to the start of the bidding  "),
		new RpfBtnDef( "reviewFwdShowBidding",		"Show Bidding",		"Forward Showing all the bidding  "),
		new RpfBtnDef( "reviewBackOneBid",			"<",		  		"Go back one bid  "),
		new RpfBtnDef( "reviewFwdOneBid",			">",		  		"Go forward one bid  "),

		new RpfBtnDef( "editHandsShuffWeak",		"Shuf Op",			"Shuf Op - Shuffle the weakest pair of hands and jump directly into Play/Bid  "),
		new RpfBtnDef( "editHands",					"Hands",			"Drag and Drop the cards from hand to hand  "),
		new RpfBtnDef( "editHandsRotateAnti",		"<",				"Rotate the CARDS Anti-clockwise - this is a TRUE ROTATE - the SEATS 'N S E W' do NOT move  "),
		new RpfBtnDef( "editHandsRotateClock",		">",				"Rotate the CARDS Clockwise - this is a TRUE ROTATE - the SEATS 'N S E W' do NOT move  "),
		new RpfBtnDef( "editBidding",				"Bidding",			"Edit / Add  Bidding for all four hands  "),
		new RpfBtnDef( "editBiddingWipe",			"wipe",				"Remove All the existing BIDDING  "),
		new RpfBtnDef( "editPlay",					"Edit Play",		"Edit / Add  Play for all four hands  "),
		new RpfBtnDef( "editPlayWipe",				"wipe",			    "Remove All the existing PLAY  "),

		new RpfBtnDef( "tutorialBackOne",			"<",				"Go back one step  "),
		new RpfBtnDef( "tutorialStepFwd",			"Step  >",			"Jump Forward to the next stopping point  "),
		new RpfBtnDef( "tutorialFlowFwd",			"Flow  >",			"Run Forward to the next stopping point - showing each being bid made or card being played  "),
		new RpfBtnDef( "tutorialBackToMovie",		"Back  to Movie",	"Continues the  Tutorial  also know as - Bridge movie  "),
		new RpfBtnDef( "tutorialIntoDealEdit",		"Edit",	            "Enter the Deal  and go into   'Edit' mode   so you can invesigate the deal Double Dummy - use  Wipe or Undo  if needed  "),
		new RpfBtnDef( "tutorialIntoDealPlay",		"Play",	            "Enter the Deal  and go into   'Play' mode   with active 'playing' opponents - good for  pre-set problems  "),
		new RpfBtnDef( "tutorialIntoDealClever",	"Enter the Deal",	"Enter the Deal  and go into   'Review' mode  so you have fine control over the replay of the deal   "),

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

		if (App.deal.isFinished()) {

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

		boolean refreshSeatChoice = (App.respectLinYou == false);

		App.respectLinYou = true;

		App.flowOnlyCommandBar = false; // ugly should not need to do this here DO WE?
		App.hideCommandBar = false; // ugly should not need to do this here DO WE?
		App.hideTutNavigationBar = false; // ugly should not need to do this here DO WE?

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

		if (refreshSeatChoice)
			App.frame.rop.p1_SeatChoice.respectLinYouChanged();
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
			Desktop.getDesktop().open(new File(App.savesPath));
		} catch (IOException e) {
		}
	}

	/**   
	 */
	static void menuOpen() {
		// ==============================================================================================
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new FileNameExtensionFilter("aabridge Deals", App.linExt));
		fc.setCurrentDirectory(new File(App.savesPath));
		setFcPreferredSize(fc);

		int returnVal = fc.showOpenDialog(App.frame);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String pathWithSep = fc.getSelectedFile().getParent();
			if (pathWithSep != null && !pathWithSep.contentEquals("")) {
				pathWithSep += File.separator;
			}

			// CmdHandler.doAutoSave();
			BridgeLoader.readLinFileIfExists(pathWithSep, fc.getSelectedFile().getName());
		}

	}

	/**   
	 */
	public static void doAutoSave() {
		// ==============================================================================================
		if (App.deal.isDoneHand()) // so we skip the 'done hand'
			return;

		try {

			String dealName = makeDealFileNameAndPath("", "");

			saveDealAsSingleLinFile(dealName);

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
			String dealName = makeDealFileNameAndPath(App.deal.lastSavedAsPathWithSep, App.deal.lastSavedAsFilename);

			saveDealAsSingleLinFile(dealName);

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
			pathWithSep = App.savesPath;
		}

		fc.setCurrentDirectory(new File(pathWithSep));
		fc.setDialogTitle(type);

		String dealName = makeDealFileNameAndPath(pathWithSep, App.deal.lastSavedAsFilename);

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

			saveDealAsSingleLinFile(dealName);
		}

		App.frame.setTitleAsRequired();
	}

	/**  
	 * Empty string return shows OK 
	 */
	public static String saveDealAsSingleLinFile(String dealName) {
		// ==============================================================================================
		try {
			FileOutputStream fileOut = new FileOutputStream(dealName);

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fileOut, "utf-8"));
			Lin.saveDealAsSingleLinFile(App.deal, writer);
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

		App.localShowHidden = App.deal.isFinished();

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
		App.reviewTrick = 0;
		App.reviewCard = 0;
		App.frame.repaint();
	}

	/**   
	 */
	static void reviewFwdToEndOfPlay() {
		// ==============================================================================================
		App.reviewTrick = 13;
		App.reviewCard = 4;
		validateReviewIndexes();
		App.frame.repaint();
	}

	/**   
	 */
	static void reviewBackOneTrick() {
		// ==============================================================================================
		App.reviewTrick--;
		App.reviewCard = 4;
		validateReviewIndexes();
		App.frame.repaint();
	}

	/**   
	 */
	static void reviewFwdOneTrick() {
		// ==============================================================================================
		if (App.reviewCard < 3) {
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
	static void reviewFwdShowOneTrick() {
		App.reviewCard++;
		validateReviewIndexes();
		App.frame.repaint();
		if (App.reviewCard != 4)
			App.gbp.c1_1__tfdp.reviewTrickDisplayTimer.start();
	}

	/**   
	 */
	static void reviewBackOneCard() {
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
		App.deal.clearPlayerNames();
		App.setMode(Aaa.EDIT_HANDS);
		App.gbp.matchPanelsToDealState();
		App.frame.repaint();
	}

	static void editBidding() {
		App.deal.clearPlayerNames();
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
	public static void depFinOut() {

		if (App.deal.isDoneHand())
			return;

		int bNumb = 1;

		FileInputStream fis = null;
		BufferedReader reader = null;

		boolean pre_create_folder = false;

		try {
			fis = new FileInputStream(App.depFinOutInBoth);
			reader = new BufferedReader(new InputStreamReader(fis));

			bNumb = DepFin.extractLastBoardNumber(reader);
			bNumb++;

			fis.close();
		} catch (Exception e) {
			pre_create_folder = true;
			bNumb = 1;
		}

		if (pre_create_folder) {
			try {
				File to_from_folder = new File(App.depFinOutInPath);
				to_from_folder.mkdir();
			} catch (Exception e) {
			}
		}

		try {
			FileWriter fileOut = new FileWriter(App.depFinOutInBoth, true /* true => append */);

			BufferedWriter writer = new BufferedWriter(fileOut);
			DepFin.appendDealInDeepFinesseFormat(App.deal, writer, bNumb);
			writer.close();

			fileOut.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		@SuppressWarnings("unused")
		int z = 0;
	}

	/**
	 */
	public static void depFinIn() {

		FileInputStream fis = null;
		BufferedReader reader = null;

		try {
			fis = new FileInputStream(App.depFinOutInBoth);
			reader = new BufferedReader(new InputStreamReader(fis));

			Deal deal = DepFin.extractLastDeal(reader, App.deal);

			fis.close();

			if (deal != null) {
				App.deal = deal;
				App.reviewTrick = 0;
				App.reviewCard = 0;
				if (App.deal.countCardsPlayed() > 0)
					App.reviewCard = 1;
				App.localShowHidden = true;
				App.setMode(Aaa.REVIEW_PLAY);
				App.dealMajorChange();
				App.gbp.matchPanelsToDealState();
				App.frame.repaint();
			}

		} catch (Exception e) {
			return;
		}
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
	public static String makeDealFileNameAndPath(String pathWithSep, String origName) {
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
			s = pathWithSep + sdfDate.format(new Date()) + "__";

			s += App.deal.contractAndResult();

			s += "__" + Aaa.cleanString(App.deal.ahHeader, false /* true => spaceOk */);
			if (App.deal.displayBoardId.length() > 0) {
				s += "_Board_" + App.deal.displayBoardId;
			}
		}
		else {
			s = pathWithSep + origName;
		}

		return checkExtension(s);
	}

	/**
	 */
	static void tutorialBackOne() {

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
	static void tutorialStepFwd() {

		if (App.isVmode_Tutorial())
			App.mg.tutorialStepFwd();
		else if (App.isMode(Aaa.REVIEW_BIDDING))
			reviewFwdOneBid(); // reviewStepShowBidding();
		else if (App.isMode(Aaa.REVIEW_PLAY))
			reviewFwdOneTrick();

		App.gbp.matchPanelsToDealState();
		App.frame.repaint();
	}

	/**
	 */
	static void tutorialFlowFwd() {

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
	static void tutorialBackToMovie() {
		MassGi_utils.do_tutorialBackToMovie();
		App.frame.repaint();
	}

	/**
	 */
	public static void tutorialIntoDealEdit() {
		MassGi_utils.do_tutorialIntoDealEdit();
		App.frame.repaint();
	}

	/**
	 */
	public static void tutorialIntoDealPlay() {
		MassGi_utils.do_tutorialIntoDealPlay();
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
		App.frame.executeCmd("rightPanelPrefs4_DFC");
		App.frame.payloadPanelShaker();
		App.frame.repaint();
	}

	/**
	 */
	public static void question_z_Step() {
		tutorialStepFwd();
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
