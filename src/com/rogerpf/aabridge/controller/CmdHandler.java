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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.rogerpf.aabridge.model.Deal;
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
		new RpfBtnDef( "menuPlayWipe",				"Wipe",				"If the deal is finished then it 'undoes' the final two tricks - otherwise, all the card play is wiped so you can play the deal again.  (Does an AutoSave first)  "),

		new RpfBtnDef( "mainAnti",					"<",				"Rotate the seats Anti-clockwise  "),
		new RpfBtnDef( "mainClock",					">",				"Rotate the seats Clockwise  "),
		new RpfBtnDef( "editPlay2",					"Set Play",			"Set the Play - Jump straight to 'Set the Play' mode, normally got to via 'Review', 'Edit' and 'Set the Play'  "),
		new RpfBtnDef( "claimBtn",					"Claim",			"Claim  -  Claim as many of the remaining tricks as you wish.  Your claim we be accepted and not tested.  "),
		new RpfBtnDef( "mainUndo",					"Undo",				""),
		new RpfBtnDef( "mainNextBoard",				"Next Board",		"Discard the existing hands, shuffle and deal the next board  (always does and AutoSave first)  "),
		new RpfBtnDef( "mainReview",				"Review",			"Swap between 'Review' and 'Normal Play'  "), 
                                                                        
		new RpfBtnDef( "reviewBidding",				"Bidding",			"Review the Bidding  "), 
 		new RpfBtnDef( "reviewBackToStartOfPlay",	"<<<",				"Rewind to the start of play  "),
 		new RpfBtnDef( "reviewFwdToEndOfPlay",  	">>>",				"Jump forward to the end of play  "),
		new RpfBtnDef( "reviewBackOneTrick",		"<<",				"Jump back one trick  "),
		new RpfBtnDef( "reviewFwdOneTrick",			">>",				"Jump forward one trick  "),
		new RpfBtnDef( "reviewFwdShowOneTrick",		"Play 1",			"Play Forward 1 trick showing each card being played  "),
		new RpfBtnDef( "reviewBackOneCard",			"<",		  		"Go back one card  "),
		new RpfBtnDef( "reviewFwdOneCard",			">",		  		"Go forward one card  "),
		new RpfBtnDef( "reviewShowEW",				"Show / Hide", 	    "Show / Hide the normally hidden hands "),
		new RpfBtnDef( "reviewEdit",				"Edit",				"Swap between 'Edit' and 'Review'  "),

		new RpfBtnDef( "reviewPlay",				"Review the Play",	"Review the Play  "), 
 		new RpfBtnDef( "reviewBackToStartOfBidding","<<<",				"Rewind to the start of the bidding  "),
		new RpfBtnDef( "reviewFwdShowBidding",		"Show Bidding",		"Forward Showing all the bidding  "),
		new RpfBtnDef( "reviewBackOneBid",			"<",		  		"Go back one bid  "),
		new RpfBtnDef( "reviewFwdOneBid",			">",		  		"Go forward one bid  "),
                                                                        
		new RpfBtnDef( "editHands",					"Set the Hands",	"Drag and Drop the cards from hand to hand  "),
		new RpfBtnDef( "editHandsRotateAnti",		"<",				"Rotate the CARDS Anti-clockwise - this is a TRUE ROTATE - the SEATS 'N S E W' do NOT move  "),
		new RpfBtnDef( "editHandsRotateClock",		">",				"Rotate the CARDS Clockwise - this is a TRUE ROTATE - the SEATS 'N S E W' do NOT move  "),
		new RpfBtnDef( "editBidding",				"Set the Bidding",	"Add Bidding for all four hands  "),
		new RpfBtnDef( "editBiddingXall",			"X all",			"Remove All the existing BIDDING  "),
		new RpfBtnDef( "editPlay",					"Set the Play",		"Add play for all four hands  "),
		new RpfBtnDef( "editPlayXall",				"X all",			"Remove All the existing PLAY  "),
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

		if (App.isMode(Aaa.REVIEW_BIDDING))
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
			App.gbp.c2_2__bbp.startAutoBidDelayTimerIfNeeded();
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
				App.setMode(Aaa.NORMAL);
			}

			App.gbp.c1_1__tfdp.SetShowCompletedTrick();
			App.gbp.c1_1__tfdp.makeCardSuggestions();
			App.gbp.matchPanelsToDealState();
		}

		else if (App.isMode(Aaa.EDIT_PLAY) || App.isMode(Aaa.REVIEW_PLAY)) {

			if (App.deal.countCardsPlayed() > 0) {
				Hand hand = App.deal.getLastHandThatPlayed();
				hand.undoLastPlay();
			}
			if (App.isMode(Aaa.REVIEW_PLAY)) {
				App.setMode(Aaa.NORMAL);
			}

			App.gbp.c1_1__tfdp.SetShowCompletedTrick();
		}

		else if (App.isMode(Aaa.NORMAL) && App.deal.isPlaying() && App.youAutoplayAlways) {

			// as all hands are on auto we can't just undo until we get to one that is not auto
			// instead we just go back the last number of cards that made a whole trick
			int played;
			while ((played = App.deal.countCardsPlayed()) > 0) {
				Hand hand = App.deal.getLastHandThatPlayed();
				hand.undoLastPlay();
				if (((played - 1) % 4) == 0)
					break;
			}
			App.gbp.c1_1__tfdp.SetShowCompletedTrick();
			App.gbp.c1_1__tfdp.makeCardSuggestions();
		}

		else if (App.isMode(Aaa.NORMAL) && App.deal.isPlaying()) {

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
			App.gbp.c1_1__tfdp.SetShowCompletedTrick();
			App.gbp.c1_1__tfdp.makeCardSuggestions();
		}

		App.gbp.c1_1__tfdp.normalTrickDisplayTimer_startIfNeeded();
		App.frame.repaint();
	}

	/**   
	 */
	static void mainNextBoard() {
		// ==============================================================================================
		doAutoSave();

		App.switchToNewLin(null);

		App.setMode(Aaa.NORMAL);
		App.reviewTrick = 0;
		App.reviewCard = 0;
		App.reviewBid = 0;
		App.gbp.c1_1__tfdp.clearAllCardSuggestions();

		App.deal = Deal.nextBoard(App.deal.boardNo, (App.watchBidding == false), App.dealCriteria, App.youSeatForNewDeal);

		App.calcCompassPhyOffset();
		App.frame.setTitleAsRequired();

		App.gbp.dealMajorChange();
		App.gbp.matchPanelsToDealState();
		App.frame.repaint();
		if (App.isPauseAtEotClickWanted()) {
			App.gbp.c1_1__tfdp.SetShowCompletedTrick();
		}
		App.gbp.c2_2__bbp.startAutoBidDelayTimerIfNeeded();
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
			Desktop.getDesktop().open(new File(App.savesPath));
		} catch (IOException e) {
		}
	}

	/**   
	 */
	static void openResultsFolder() {
		// ==============================================================================================
		try {
			Desktop.getDesktop().open(new File(App.resultsPath));
		} catch (IOException e) {
		}
	}

	/**   
	 */
	static void openTestsFolder() {
		// ==============================================================================================
		try {
			Desktop.getDesktop().open(new File(App.testsPath));
		} catch (IOException e) {
		}
	}

//	/**   
//	 */
//	static void menuOpen() {
//		// ==============================================================================================
//		JFileChooser fc = new JFileChooser();
//		fc.setFileFilter(new FileNameExtensionFilter("aabridge Deals", App.linExt));
//		fc.setCurrentDirectory(new File(App.savesPath));
//		setFcPreferredSize(fc);
//
//		int returnVal = fc.showOpenDialog(App.frame);
//		if (returnVal == JFileChooser.APPROVE_OPTION) {
//			String pathWithSep = fc.getSelectedFile().getParent();
//			if (pathWithSep != null && !pathWithSep.contentEquals("")) {
//				pathWithSep += File.separator;
//			}
//			
//			CmdHandler.doAutoSave();
//			readFileIfExists(pathWithSep, fc.getSelectedFile().getName());
//		}
//
//	}

	/**   
	 */
	public static boolean readFileIfExists(String pathWithSep, String dealName) {
		// ==============================================================================================

		String s = (pathWithSep + dealName).toLowerCase();

		if (s.endsWith(App.dotLinExt.toLowerCase())) {
			return readLinFileIfExists(pathWithSep, dealName);
		}

		return false;
	}

	/**   
	 */
	public static boolean readLinFileIfExists(String pathWithSep, String dealName) {
		// ==============================================================================================

		if (pathWithSep == null || pathWithSep.contentEquals("")) {
			pathWithSep = App.savesPath;
		}

		File fileIn = new File(pathWithSep + dealName);
		if (!fileIn.exists()) {
			return false;
		}

		Lin lin = null;

		try {

			FileInputStream fis = new FileInputStream(fileIn);

			lin = new Lin(fis, fileIn, App.dotLinExt, App.showFinAsReview);

			fis.close();

		} catch (IOException i) {
			System.out.println("lin file rejected, tutorial format? bad format?");
			// App.deal = new Deal(Deal.makeDoneHand, App.youSeatForNewDeal);
			// i.printStackTrace();
			return false;
		}

		App.switchToNewLin(lin);

		return true;
	}

//	/**   
//	 */
//	public static void deepCloneDealAndUse() { // TESTING ONLY !!!!!!!!!!!!!!!!!!!!!!!!!!!!
//		// ==============================================================================================
//		Deal d_old = App.deal;
//
//		if (d_old == null)
//			return;
//
//		Deal d = d_old.deepClone();
//
//		App.deal = d;
//
//		App.gbp.dealMajorChange();
//		App.setMode(Aaa.NORMAL);
//		App.gbp.matchPanelsToDealState();
//		App.frame.repaint();
//		App.gbp.c2_2__bbp.startAutoBidDelayTimerIfNeeded();
//
//		App.frame.setTitleAsRequired();
//	}

	/**   
	 */
	public static void doAutoSave() {
		// ==============================================================================================
		if (App.deal.isDoneHand()) // so we skip the 'done hand'
			return;

		App.deal.description = App.gbp.c0_0__tlp.descEntry.getText();
		String dealName = makeDealFileNameAndPath("", "");

		saveDealAsSingleLinFile(dealName);

		if (App.isFilenameThrowAway(App.deal.lastSavedAsFilename)) {
			File f = new File(dealName);
			App.deal.lastSavedAsFilename = f.getName();
		}

		App.frame.setTitleAsRequired();
	}

	/**   
	 */
	static void menuPlayWipe() {
		// ==============================================================================================
		if (App.deal.isDoneHand()) { // so we skip the 'done hand'
			return;
		}
		doAutoSave();

		if ((App.isMode(Aaa.NORMAL) || App.isMode(Aaa.REVIEW_PLAY)) && App.deal.isFinished()) {
			mainUndo();
			return;
		}

		// App.deal.lastSavedAsFilename = "";
		App.frame.setTitleAsRequired();

		editPlayXall();
		App.reviewTrick = 0;
		App.reviewCard = 0;
		App.reviewBid = 0;

		App.gbp.dealMajorChange();
		App.setMode(Aaa.NORMAL);
		App.gbp.matchPanelsToDealState();
		App.frame.repaint();
//		if (App.isPauseAtEotClickWanted()) {
//			App.gbp.c1_1__tfdp.SetShowCompletedTrick();
//		}
		App.gbp.c2_2__bbp.startAutoBidDelayTimerIfNeeded();
		App.gbp.c1_1__tfdp.normalTrickDisplayTimer_startIfNeeded();
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
			App.deal.description = App.gbp.c0_0__tlp.descEntry.getText();
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
		App.deal.description = App.gbp.c0_0__tlp.descEntry.getText();
		String dealName = makeDealFileNameAndPath(pathWithSep, App.deal.lastSavedAsFilename);

		fc.setSelectedFile(new File(dealName));
		setFcPreferredSize(fc);

		int returnVal = fc.showSaveDialog(App.frame);
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
	 */
	static void saveDealAsSingleLinFile(String dealName) {
		// ==============================================================================================
		try {
			FileOutputStream fileOut = new FileOutputStream(dealName);

//			ObjectOutputStream out = new ObjectOutputStream(fileOut);
//			out.writeObject(App.deal);
//			out.close();

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fileOut, "utf-8"));
			Lin.saveDealAsSingleLinFile(App.deal, writer);
			writer.close();

			fileOut.close();

		} catch (IOException i) {
			i.printStackTrace();
		}

	}

	/**   
	 */
	static boolean validateReviewIndexes() {
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
	static boolean validateBiddingIndex() {
		// ==============================================================================================
		App.gbp.c1_1__bfdp.reviewBiddingMakeCopy();
		return false;
	}

	/**   
	 */
	static void mainReview() { // button also shows with name 'Normal'
		// ==============================================================================================
		if (App.deal.isDoneHand()) { // so we skip the 'done hand'
			return;
		}

		if (App.isMode(Aaa.EDIT_BIDDING)) {
			// App.deal.finishBiddingIfIncomplete();
		}

		if (App.isMode(Aaa.NORMAL)) {
			App.localShowHidden = App.deal.isFinished();

			if (App.deal.isBidding()) {
				App.setMode(Aaa.REVIEW_BIDDING);
				reviewBackToStartOfBidding(); // index set
			}
			else {
				App.setMode(Aaa.REVIEW_PLAY);
				reviewBackToStartOfPlay(); // index set
			}
		}
		else {
			App.setMode(Aaa.NORMAL);
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
		App.reviewTrick = 13;
		App.reviewCard = 4;
		validateReviewIndexes();
		App.frame.repaint();
	}

	/**   
	 */
	static void reviewBackOneTrick() {
		App.gbp.c1_1__tfdp.reviewTrickDisplayTimer.stop(); // just in case
		App.reviewTrick--;
		App.reviewCard = 4;
		validateReviewIndexes();
		App.frame.repaint();
	}

	/**   
	 */
	static void reviewFwdOneTrick() {
		App.gbp.c1_1__tfdp.reviewTrickDisplayTimer.stop(); // just in case
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
		App.gbp.c1_1__tfdp.reviewTrickDisplayTimer.start();
	}

	/**   
	 */
	static void reviewBackOneCard() {
		App.gbp.c1_1__tfdp.reviewTrickDisplayTimer.stop(); // just in case
		App.reviewCard--;
		validateReviewIndexes();
		App.frame.repaint();
	}

	/**   
	 */
	static void reviewFwdOneCard() {
		App.gbp.c1_1__tfdp.reviewTrickDisplayTimer.stop(); // just in case
		App.reviewCard++;
		validateReviewIndexes();
		App.frame.repaint();
	}

	/**   
	 */
	static void reviewShowEW() {
		App.localShowHidden = !App.localShowHidden;
		App.frame.repaint();
	}

	/**   
	 */
	public static void reviewTimeRequestToShowTrick(int trickIndex) {
		App.gbp.c1_1__tfdp.reviewTrickDisplayTimer.stop(); // just in case
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
		App.frame.repaint();
	}

	/**   
	 */
	static void reviewEdit() {
		App.setMode(Aaa.EDIT_CHOOSE);
		App.frame.repaint();
	}

	/**   
	 */
	static void reviewPlay() {
		App.setMode(Aaa.REVIEW_PLAY);
		App.frame.repaint();
	}

	/**   
	 */
	static void reviewBidding() {
		App.setMode(Aaa.REVIEW_BIDDING);
		App.reviewBid = 999;
		validateBiddingIndex();
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
		if (old_rb != App.reviewBid) {
			App.gbp.c1_1__bfdp.reviewBidDisplayTimer.start();
		}
	}

	/**   
	 */
	static void reviewFwdShowBidding() {
		App.reviewBid = 1;
		validateBiddingIndex();
		App.frame.repaint();
		App.gbp.c1_1__bfdp.reviewBidDisplayTimer.start();
	}

	/**   
	 */
	static void reviewBackOneBid() {
		App.reviewBid--;
		validateBiddingIndex();
		App.frame.repaint();
	}

	/**   
	 */
	static void reviewFwdOneBid() {
		App.reviewBid++;
		validateBiddingIndex();
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

	static void editBiddingXall() {
		App.deal.wipeContractBiddingAndPlay();
		App.gbp.dealMajorChange();
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
		App.deal.wipePlay(false /* (don't) keepFirstCardPlayed */);
		App.gbp.dealMajorChange();
		App.frame.repaint();
	}

	static void editHandsRotateAnti() {
		App.deal.rotateHands(-1);
		App.gbp.dealMajorChange();
		App.gbp.matchPanelsToDealState();
		App.frame.repaint();
	}

	static void editHandsRotateClock() {
		App.deal.rotateHands(+1);
		App.gbp.dealMajorChange();
		App.gbp.matchPanelsToDealState();
		App.frame.repaint();
	}

	static void runTests() {
		TestSystemRunner.performAllTests();
	}

	/**   
	 */
	public static void actionPerfString(String actCmd) { // called but the Controller who is the ActionListener
		// ==============================================================================================
		// System.out.println(actCmd + " invoked");
		try {

			CmdHandler.class.getDeclaredMethod(actCmd).invoke(CmdHandler.class);

		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
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

			if (App.deal.description.length() > 0) {
				StringBuilder good = new StringBuilder();
				for (char c : App.deal.description.toCharArray()) {
					if (c == '.' || c == '-' || Character.isJavaIdentifierPart(c)) {
						good.append(c);
					}
					else if (c == ' ') {
						good.append('_');
					}
				}
				s += "__" + good;
			}
			s += "_";
		}
		else {
			s = pathWithSep + origName;
		}

		return checkExtension(s);
	}

}
