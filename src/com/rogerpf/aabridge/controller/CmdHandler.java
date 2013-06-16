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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.rogerpf.aabridge.model.Deal;
import com.rogerpf.aabridge.model.Hand;

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
		//             "openSavesFolder" entry EXISTS but not as a button
		//             "menuOpen"        entry EXISTS but not as a button
		//             "menuSave"        entry EXISTS but not as a button
		//             "menuSaveAs"      entry EXISTS but not as a button
		new RpfBtnDef( "menuQuickSave",				"Quick Save",		"Save the deal now, using the curent time and date in the file name"),
		                                                                
		new RpfBtnDef( "mainClock",					">",				"Rotate the seats Clockwise"),
		new RpfBtnDef( "mainAnti",					"<",				"Rotate the seats Anti-clockwise"),
		new RpfBtnDef( "mainShowBidding",			"Show Bidding",		""),
		new RpfBtnDef( "mainUndo",					"Undo",				""),
		new RpfBtnDef( "mainNextBoard",				"Next Board",		"Discard the existing hands, shuffle and deal the next board"),
		new RpfBtnDef( "mainReview",				"Review",			"Swap between 'Review' and 'Normal Play'"), 
                                                                        
		new RpfBtnDef( "reviewBidding",				"Bidding",			"Review the Bidding"), 
 		new RpfBtnDef( "reviewBackToStartOfPlay",	"<<<",				"Rewind to the start of play"),
 		new RpfBtnDef( "reviewFwdToEndOfPlay",  	">>>",				"Jump forward the end of play"),
		new RpfBtnDef( "reviewBackOneTrick",		"<<",				"Jump back one trick"),
		new RpfBtnDef( "reviewFwdOneTrick",			">>",				"Jump forward one trick"),
		new RpfBtnDef( "reviewFwdShowOneTrick",		"Show 1",			"Forward 1 trick showing each card being played"),
		new RpfBtnDef( "reviewBackOneCard",			"<",		  		"Go back one card"),
		new RpfBtnDef( "reviewFwdOneCard",			">",		  		"Go forward one card"),
		new RpfBtnDef( "reviewShowEW",				"Show/Hide EW", 	""),
		new RpfBtnDef( "reviewEdit",				"Edit",				"Swap between 'Edit' and 'Review'"),

		new RpfBtnDef( "reviewPlay",				"Review the Play",	"Review the Play"), 
 		new RpfBtnDef( "reviewBackToStartOfBidding","<<<",				"Rewind to the start of the bidding"),
		new RpfBtnDef( "reviewFwdShowBidding",		"Show Bidding",		"Forward Showing all the bidding"),
		new RpfBtnDef( "reviewBackOneBid",			"<",		  		"Go back one bid"),
		new RpfBtnDef( "reviewFwdOneBid",			">",		  		"Go forward one bid"),
                                                                        
		new RpfBtnDef( "editHands",					"Set the Hands",	"Drag and Drop the cards from hand to hand"),
		new RpfBtnDef( "editBidding",				"Set the Bidding",	"Add Bidding for all four hands"),
		new RpfBtnDef( "editBiddingXall",			"X all",			"Remove All the existing BIDDING"),
		new RpfBtnDef( "editPlay",					"Set the Play",		"Add play for all four hands"),
		new RpfBtnDef( "editPlayXall",				"X all",			"Remove All the existing PLAY"),
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
	static void mainAnti() {
		// ==============================================================================================
		App.incOffsetAntiClockwise(); // includes repaint
		return;
	}

	/**   
	 */
	static void mainClock() {
		// ==============================================================================================
		App.incOffsetClockwise(); // includes repaint
		App.savePreferences();
		return;
	}

	/**   
	 */
	static void mainUndo() {
		// ==============================================================================================
		if (App.deal.isBidding() || App.isMode(Aaa.EDIT_BIDDING)) {
			if (App.autoBid[App.deal.getNextHandToBid().compass]) {
				// We have to (do we?) wait until the autoplayer has finished
				// return;
			}
			while (true) {
				Hand hand = App.deal.getLastHandThatBid();
				if (hand == null)
					break;
				boolean wasAuto = App.autoBid[hand.compass];
				hand.undoLastBid();
				if (!wasAuto)
					break;
			}
			App.frame.repaint();
			App.gbp.c2_2__bbp.startAutoBidDelayTimerIfNeeded();
		}
		else if (App.deal.isPlaying() || App.isMode(Aaa.EDIT_PLAY)) {

			if (App.autoPlay[App.deal.getNextHandToPlay().compass]) {
				// We have to wait until the autoplayer has finished
				return;
			}
			while (true) {
				Hand hand = App.deal.getLastHandThatPlayed();
				if (hand == null)
					break;
				if (App.deal.countCardsPlayed() == 0)
					break;
				boolean wasAuto = App.autoPlay[hand.compass];
				hand.undoLastPlay();
				if (!wasAuto)
					break;
			}
			App.gbp.c1_1__tfdp.ClearShowCompletedTrick();
			App.gbp.c1_1__tfdp.makeCardSuggestions();

			App.frame.repaint();
			App.gbp.c1_1__tfdp.normalTrickDisplayTimer_startIfNeeded();
		}
	}

	/**   
	 */
	static void mainNextBoard() {
		// ==============================================================================================
		App.setMode(Aaa.NORMAL);
		App.setEwVisible(false);
		App.reviewTrick = 0;
		App.reviewCard = 0;
		App.reviewBid = 0;

		App.deal = Deal.nextBoard(App.deal.boardNo, (App.watchBidding == false), App.dealCriteria);

		App.gbp.dealMajorChange();
		App.gbp.matchPanelsToDealState();
		App.frame.repaint();
		if (App.nsAutoplayAlways && App.nsAutoplayPause) {
			App.gbp.c1_1__tfdp.showCompletedTrick = true;
		}
		App.gbp.c2_2__bbp.startAutoBidDelayTimerIfNeeded();
		App.gbp.c1_1__tfdp.normalTrickDisplayTimer_startIfNeeded();
	}
	

	/**   
	 */
	static void setFcPreferredSize(JFileChooser fc) {
		// ==============================================================================================
		Dimension wh = App.frame.getSize();
		wh.width = (wh.width * 110) / 100;
		wh.height = (wh.height * 70) / 100;
		fc.setPreferredSize(wh);
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
		fc.setFileFilter(new FileNameExtensionFilter("aabridge Deals", App.dealExt));
		fc.setCurrentDirectory(new File(App.savesPath));
		setFcPreferredSize(fc);

		int returnVal = fc.showOpenDialog(App.frame);
		if (returnVal == JFileChooser.APPROVE_OPTION) {

			Deal d = null;

			try {
				File fileIn = fc.getSelectedFile();
				FileInputStream fis = new FileInputStream(fileIn);
				ObjectInputStream in = new ObjectInputStream(fis);
				d = (Deal) in.readObject();
				d.restoreTransientSuitChForClearerDebug();
				d.origFilename = fileIn.getName();
				in.close();
				fis.close();
			} catch (IOException i) {
				//i.printStackTrace();
				return;
			} catch (ClassNotFoundException c) {
				//System.out.println("Deal? class not found");
				//c.printStackTrace();
				return;
			}

			App.deal = d;
			App.gbp.dealMajorChange();
			App.setMode(Aaa.NORMAL);
			App.frame.repaint();
			App.gbp.c2_2__bbp.startAutoBidDelayTimerIfNeeded();
		}

	}

	/**   
	 */
	static void menuQuickSave() {
		// ==============================================================================================
		App.deal.description = App.gbp.c0_0__tlp.descEntry.getText();
		String dealName = makeFileNameAndPath("");
		try {
			FileOutputStream fileOut = new FileOutputStream(dealName);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(App.deal);
			out.close();
			fileOut.close();
		} catch (IOException i) {
			i.printStackTrace();
		}
	
		if (App.deal.origFilename.contentEquals("")) {
			File f = new File(dealName);
			App.deal.origFilename = f.getName();
		}

	}

	/**   
	 */
	static void menuSave() {
		// ==============================================================================================
		App.deal.description = App.gbp.c0_0__tlp.descEntry.getText();
		String dealName = makeFileNameAndPath(App.deal.origFilename);
		try {
			FileOutputStream fileOut = new FileOutputStream(dealName);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(App.deal);
			out.close();
			fileOut.close();
		} catch (IOException i) {
			i.printStackTrace();
		}
	
		if (App.deal.origFilename.contentEquals("")) {
			File f = new File(dealName);
			App.deal.origFilename = f.getName();
		}

	}

	/**   
	 */
	static void menuSaveAs() {
		// ==============================================================================================
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new FileNameExtensionFilter("aabridge Deals", App.dealExt));
		fc.setCurrentDirectory(new File(App.savesPath));
		fc.setDialogTitle("Save As");
		App.deal.description = App.gbp.c0_0__tlp.descEntry.getText();
		String dealName = makeFileNameAndPath(App.deal.origFilename);
		
		fc.setSelectedFile(new File(dealName));
		setFcPreferredSize(fc);

		int returnVal = fc.showSaveDialog(App.frame);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			dealName = checkExtension( fc.getSelectedFile().getAbsolutePath());
			try {
				FileOutputStream fileOut = new FileOutputStream(dealName);
				ObjectOutputStream out = new ObjectOutputStream(fileOut);
				out.writeObject(App.deal);
				out.close();
				fileOut.close();
			} catch (IOException i) {
				i.printStackTrace();
			}
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
		if (App.isMode(Aaa.EDIT_BIDDING)) {
			// App.deal.finishBiddingIfIncomplete();
		}

		if (App.isMode(Aaa.NORMAL)) {
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
		App.reviewTrick++;
		App.reviewCard = 4;
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
		App.setEwVisible(!App.isEwVisible());
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
		App.setMode(Aaa.EDIT_HANDS);
		App.gbp.matchPanelsToDealState();
		App.frame.repaint();
	}

	static void editBidding() {
		App.setMode(Aaa.EDIT_BIDDING);
		App.gbp.matchPanelsToDealState();
		App.frame.repaint();
	}

	static void editBiddingXall() {
		App.deal.wipeContractBiddingAndPlay();
		App.gbp.dealMajorChange();
		App.frame.repaint();
	}

	static void editPlay() {
		App.setMode(Aaa.EDIT_PLAY);
		App.gbp.matchPanelsToDealState();
		App.frame.repaint();
	}

	static void editPlayXall() {
		App.deal.wipePlay();
		App.gbp.dealMajorChange();
		App.frame.repaint();
	}

	/**   
	 */
	public static void actionPerfString(String actCmd) { // called but the Controller who is the ActionListener
		// ==============================================================================================
		System.out.println(actCmd + " invoked");
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
	static void readDealIfExists(String dealName) {
		// ==============================================================================================
		Deal d = null;

		File fileIn = new File(App.savesPath + dealName);
		if (fileIn.exists() == false)
			return;

		{
			try {
				FileInputStream fis = new FileInputStream(fileIn);
				ObjectInputStream in = new ObjectInputStream(fis);
				d = (Deal) in.readObject();
				d.restoreTransientSuitChForClearerDebug();
				d.origFilename = fileIn.getName();
				in.close();
				fis.close();
			} catch (IOException i) {
				i.printStackTrace();
				return;
			} catch (ClassNotFoundException c) {
				System.out.println("Deal? class not found");
				c.printStackTrace();
				return;
			}
		}

		App.deal = d;
		App.gbp.dealMajorChange();
		App.gbp.matchPanelsToDealState();
		App.frame.repaint();
		App.gbp.c2_2__bbp.startAutoBidDelayTimerIfNeeded();
	}

	/**   
	 */
	public static String makeFileNameAndPath(String origName) {
		// ==============================================================================================
		String s;
		if (origName.contentEquals("")) {
			
			SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd__HH-mm-ss");
			s = App.savesPath + sdfDate.format(new Date()) + "__";
	
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
			s = App.savesPath + origName;
		}
		
		return checkExtension(s);
	}
	
	
	/**   
	 */
	public static String checkExtension(String s) {
		// ==============================================================================================
		if (s.endsWith("." + App.dealExt) == false) {
			s += "." + App.dealExt;
		}
		return s;
	}

}
