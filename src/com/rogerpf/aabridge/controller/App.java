/*******************************************************************************
 * Copyright (c) 2013 Roger Pfister.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Roger Pfister - initial API and implementation
 *******************************************************************************/
package com.rogerpf.aabridge.controller;

import java.util.prefs.Preferences;

import com.rogerpf.aabridge.model.Deal;
import com.rogerpf.aabridge.model.Lin;
import com.rogerpf.aabridge.model.Zzz;
import com.rogerpf.aabridge.view.AaLinButtonsPanel;
import com.rogerpf.aabridge.view.AaOuterFrame;
import com.rogerpf.aabridge.view.GreenBaizePanel;

// @formatter:off

/**
 */
public class App {

	public static String autoSavesPath;
	public static String quickSavesPath;
	public static String savesPath;
	public static String testsPath;
	public static String resultsPath;
	public static String dealExt = "aaBridge";
	public static String dotAaBridgeExt = '.' + dealExt;
	public static String linExt = "lin";
	public static String dotLinExt = '.' + linExt;
	
	static int mode = Aaa.NORMAL;
	
	public static int reviewTrick = 0; 
	public static int reviewCard  = 0;
	
	public static int reviewBid = 0; 
	
	public static boolean allConstructionComplete = false;
	
	/*  reviewTrick = 7
	 *  reviewCard  = 3   
	 *   Means all of the cards in tricks indexed 0 to 6 inclusive 
	 *   and the first three cards (0,1,2) of trick indexed by 7 have been 
	 *   'played' i.e. removed from the hand, the last 3 cards being on the table. 
	 *  
	 *  7 4  follows after 7 3  and will show all of trick 7
	 *  8 0  follows after 7 4  show a blank table
	 *         
	 *  0 0   shows the original state with no cards played.			     
	 * 
	 */

	/**
	 */
	static public void incOffsetClockwise() {
		compassPhyOffset = (compassPhyOffset + 3) % 4;
		gbp.dealDirectionChange();
		frame.repaint();
	};

	/**
	 */
	static public void incOffsetAntiClockwise() {
		compassPhyOffset = (compassPhyOffset + 1) % 4;
		gbp.dealDirectionChange();
		frame.repaint();
	};

	/**
	 */
	static public int compassFromPhyScreenPos(int phyPos) {
		return (phyPos + compassPhyOffset) % 4;
	};

	/**
	 */
	static public int phyScreenPosFromCompass(int compass) {
		return (compass + 4 - compassPhyOffset) % 4;
	};

	/**
	 */
	static public void calcCompassPhyOffset() {
		compassPhyOffset = (App.putDeclarerSouth && (!App.deal.isBidding() && (App.deal.contract != App.deal.PASS))) 
			? ((App.deal.contractCompass - Zzz.South)+4) % 4 : 0;
	};
	

	private static int compassPhyOffset = 0; // 0 to 3	
	public static boolean localShowHidden = false;
	
	//************* Preferences that are saved and restored - start *****************
	public static int frameLocationX = 75;
	public static int frameLocationY = 50;
	public static int frameHeight = 500;
	public static int frameWidth  = 720;
	public static boolean maximized = false;
	public static int horzDividerLocation = 99999;
	public static int vertDividerLocation = 99999;
	public static int ropSelPrevTabIndex  = 0;	
	public static int ropSelectedTabIndex = 0;	
	
	public static boolean showBidPlayMsgs = true;
	public static boolean showSuitSymbols = true;
	public static boolean showWelcome = true;
	public static boolean alwaysShowHidden = false;
	public static boolean showPoints = false;
	public static boolean showLTC = false;
	public static boolean youAutoAdjacent = true;

	public static boolean youAutoSingletons = false;

	public static boolean youAutoplayAlways = false;
	public static boolean youAutoplayPause = true;
	public static boolean youDeclarerPause = true;
	public static boolean youDefenderPause = true;
	
	public static boolean yourFinnessesMostlyFail = false;
           
	public static boolean showEditPlay2Btn = false;
	public static boolean showClaimBtn = false;
	public static boolean deleteQuickSaves = false;
	public static boolean deleteAutoSaves = false;
	public static boolean showRotationBtns = false;
	public static boolean showPlayAgain = true;
	public static boolean showEasySave  = true;
	public static boolean fillHandDisplay = false;
	public static boolean startWithDoneHand = false;
	public static boolean runTestsAtStartUp = false;
	public static boolean showTestsLogAtEnd = true;
	
	public static String  dealCriteria = "twoSuitSlam_E";
	public static boolean watchBidding = true;
	public static int     youSeatForNewDeal = Zzz.South;  
	public static int     youSeatForLinDeal = Zzz.South;  // South => declarer
	public static int     defenderSignals = Zzz.NoSignals; 
	public static boolean putDeclarerSouth = true;
	public static boolean showOldFinAsReview = false;

	public static int eotExtendedDisplay = 2;
	
	public static int playPluseTimerMs = 600;
	public static int bidPluseTimerMs = 750;
	public static int finalCardTimerMs = 1200;
	
	
	
	public static final int scoringFollowsYou = 0;     
	public static final int scoringFollowsDeclarer = 1;     
	public static final int scoringFollowsSouthZone = 2;     
	public static int     scoringFollows   = scoringFollowsYou;    // not (yet) saved or setable
	

	//************* Preferences that are saved and restored - end   *****************
	

	public static void loadPreferences() {
		Preferences appPrefs = Preferences.userRoot().node("com.rogerpf.aabridge.prefs");

		frameLocationX     = appPrefs.getInt("frameLocationX", 150);
		frameLocationY     = appPrefs.getInt("frameLocationY", 100);
		frameWidth         = appPrefs.getInt("frameWidth", 720);
		frameHeight        = appPrefs.getInt("frameHeight", 500);
		maximized          = appPrefs.getBoolean("maximized",        false);
		horzDividerLocation= appPrefs.getInt("horzDividerLocation", 99999);
		vertDividerLocation= appPrefs.getInt("vertDividerLocation", 99999);
		ropSelPrevTabIndex = appPrefs.getInt("ropSelectedTabIndex", 0);	
		
		showWelcome        = appPrefs.getBoolean("showWelcome",      true);
		showBidPlayMsgs    = appPrefs.getBoolean("showBidPlayMsgs",  true);
		showSuitSymbols    = appPrefs.getBoolean("showSuitSymbols",  true);
		showPoints         = appPrefs.getBoolean("showPoints",       false);
		showLTC            = appPrefs.getBoolean("showLTC",          false);
		youAutoSingletons  = appPrefs.getBoolean("youAutoSingletons", false);
		youAutoAdjacent    = appPrefs.getBoolean("youAutoAdjacent",   true);
		
		yourFinnessesMostlyFail = appPrefs.getBoolean("yourFinnessesMostlyFail", false);

		alwaysShowHidden   = appPrefs.getBoolean("alwaysShowHidden", false);
		youAutoplayAlways  = appPrefs.getBoolean("youAutoplayAlways", false);
		youAutoplayPause   = appPrefs.getBoolean("youAutoplayPause",  true);
		youDeclarerPause   = appPrefs.getBoolean("youDeclarerPause",  true);
		youDefenderPause   = appPrefs.getBoolean("youDefenderPause",  true);
		showEditPlay2Btn   = appPrefs.getBoolean("showEditPlay2Btn", false);
		showClaimBtn       = appPrefs.getBoolean("showClaimBtn", false);
		showRotationBtns   = appPrefs.getBoolean("showRotationBtns", false);
		showEasySave       = appPrefs.getBoolean("showEasySave",     false);
		showPlayAgain      = appPrefs.getBoolean("showPlayAgain",    false);
		deleteQuickSaves   = appPrefs.getBoolean("deleteQuickSaves", true);
		deleteAutoSaves    = appPrefs.getBoolean("deleteAutoSaves",  true);
		//fillHandDisplay    = appPrefs.getBoolean("fillHandDisplay", false); - not saved or restored
		startWithDoneHand  = appPrefs.getBoolean("startWithDoneHand",false);
		runTestsAtStartUp  = appPrefs.getBoolean("runTestsAtStartUp",false);
		showTestsLogAtEnd  = appPrefs.getBoolean("showTestsLogAtEnd",true);
		
		youSeatForNewDeal  = appPrefs.getInt("youSeatForNewDeal",  Zzz.South);
		youSeatForLinDeal  = appPrefs.getInt("youSeatForLinDeal",  Zzz.South);
		dealCriteria       = appPrefs.get("dealCriteria",  "twoSuitSlam_M1");
		dealCriteria       = Deal.validateDealCriteria( dealCriteria);
		watchBidding       = appPrefs.getBoolean("watchBidding",  true);
		
		defenderSignals    = appPrefs.getInt("defenderSignals", Zzz.NoSignals);
		if (defenderSignals > Zzz.HighestSignal) defenderSignals = Zzz.NoSignals;
		putDeclarerSouth   = appPrefs.getBoolean("putDeclarerSouth",  true);
		showOldFinAsReview = appPrefs.getBoolean("showOldFinAsReview",  false);
		
		playPluseTimerMs   = appPrefs.getInt("playPluseTimerMs", 600);
		bidPluseTimerMs    = appPrefs.getInt("bidPluseTimerMs",  750);
		eotExtendedDisplay = appPrefs.getInt("eotExtendedDisplay",  2);

        implement_showRotationBtns();
        implement_showEasySave();
        implement_showPlayAgain();
	}
	
	public static boolean isPauseAtEotClickWanted() {	
		return      (youAutoplayAlways && youAutoplayPause)
			   ||  
			         !youAutoplayAlways 
			   && (   youDeclarerPause && deal.isYouSeatDeclarerAxis() && (deal.countCardsPlayed() > 0)
				    || youDefenderPause && deal.isYouSeatDefenderAxis() && (deal.countCardsPlayed() > 0)
				    )
				   ;
	}

	public static void savePreferences() {
		Preferences appPrefs = Preferences.userRoot().node("com.rogerpf.aabridge.prefs");
		
		App.maximized = (App.frame.getExtendedState() == java.awt.Frame.MAXIMIZED_BOTH);
		
		appPrefs.putInt("frameLocationX",  frameLocationX);
		appPrefs.putInt("frameLocationY",  frameLocationY);
		appPrefs.putInt("frameWidth",      frameWidth);
		appPrefs.putInt("frameHeight",     frameHeight);
		appPrefs.putBoolean("maximized",   maximized);
		appPrefs.putInt("horzDividerLocation", horzDividerLocation);
		appPrefs.putInt("vertDividerLocation", vertDividerLocation);
		appPrefs.putInt("ropSelectedTabIndex", ropSelectedTabIndex);
		
		appPrefs.putBoolean("showWelcome",       showWelcome);
		appPrefs.putBoolean("showBidPlayMsgs",   showBidPlayMsgs);
		appPrefs.putBoolean("showSuitSymbols",   showSuitSymbols);
		appPrefs.putBoolean("alwaysShowHidden",  alwaysShowHidden);
		appPrefs.putBoolean("showPoints",        showPoints);
		appPrefs.putBoolean("showLTC",           showLTC);
		appPrefs.putBoolean("youAutoSingletons", youAutoSingletons);
		appPrefs.putBoolean("youAutoAdjacent",   youAutoAdjacent);
		
		appPrefs.putBoolean("yourFinnessesMostlyFail", yourFinnessesMostlyFail);
		
		appPrefs.putBoolean("youAutoplayAlways", youAutoplayAlways);
		appPrefs.putBoolean("youAutoplayPause",  youAutoplayPause);
		appPrefs.putBoolean("youDeclarerPause",  youDeclarerPause);
		appPrefs.putBoolean("youDefenderPause",  youDefenderPause);
		appPrefs.putBoolean("showEditPlay2Btn",  showEditPlay2Btn);
		appPrefs.putBoolean("showClaimBtn",      showClaimBtn);
		appPrefs.putBoolean("deleteQuickSaves",  deleteQuickSaves);
		appPrefs.putBoolean("deleteAutoSaves",   deleteAutoSaves);
		appPrefs.putBoolean("showRotationBtns",  showRotationBtns);
		appPrefs.putBoolean("showEasySave",      showEasySave);
		appPrefs.putBoolean("showPlayAgain",     showPlayAgain);
	  //appPrefs.putBoolean("fillHandDisplay",   fillHandDisplay);  - not saved or restored
		appPrefs.putBoolean("startWithDoneHand", startWithDoneHand);
		appPrefs.putBoolean("runTestsAtStartUp", runTestsAtStartUp);
		appPrefs.putBoolean("showTestsLogAtEnd", showTestsLogAtEnd);
		appPrefs.putInt("youSeatForNewDeal",     youSeatForNewDeal);
		appPrefs.putInt("youSeatForLinDeal",     youSeatForLinDeal);
		appPrefs.put("dealCriteria",             dealCriteria);
		appPrefs.putBoolean("watchBidding",      watchBidding);
		appPrefs.putInt("defenderSignals",       defenderSignals);
		appPrefs.putBoolean("showOldFinAsReview",showOldFinAsReview);
		appPrefs.putBoolean("putDeclarerSouth",  putDeclarerSouth);
		
		
		
		appPrefs.putInt("playPluseTimerMs",      playPluseTimerMs);
		appPrefs.putInt("bidPluseTimerMs",       bidPluseTimerMs);
		appPrefs.putInt("eotExtendedDisplay",    eotExtendedDisplay);
	}
	
	// @formatter:on

	public static void implement_youAutoplayAlways() {
		if (allConstructionComplete) {
			if (App.mode == Aaa.NORMAL && App.youAutoplayAlways) {
				App.gbp.c1_1__tfdp.normalTrickDisplayTimer_startIfNeeded();
			}
			App.gbp.matchPanelsToDealState();
		}
	}

	public static void implement_showRotationBtns() {
		if (allConstructionComplete) {
			App.gbp.c0_0__tlp.setRotationBtnsVisibility();
		}
	}

	public static void implement_showEasySave() {
		if (allConstructionComplete) {
			App.gbp.c0_0__tlp.setEasySaveVisibility();
		}
	}

	public static void implement_showPlayAgain() {
		if (allConstructionComplete) {
			App.gbp.c0_0__tlp.setPlayAgainVisibility();
		}
	}

	public static void implement_showEditPlay2Btn() {
		if (allConstructionComplete) {
			App.gbp.matchPanelsToDealState();
		}
	}

	public static void implement_showClaimBtn() {
		if (allConstructionComplete) {
			App.gbp.matchPanelsToDealState();
		}
	}

	public static void implement_fillHandDisplay() {
		if (allConstructionComplete) {
			App.frame.repaint();
		}
	}

	/**   
	 */
	public static boolean isFilenameThrowAway(String filename) {
		// ========================================================================
		return filename == null || filename.isEmpty() || filename.contains("__Your_text_here_");
	}

	/**   
	 */
	public static boolean isMode(int modeV) {
		// ========================================================================
		return (mode == modeV);
	};

	/** 
	 */
	static public void setMode(int newMode) {
		// ========================================================================
		if (/* old */mode == Aaa.EDIT_BIDDING) {
			if (newMode == Aaa.NORMAL) {
				; // do nothing
			}
			else {
				App.deal.finishBiddingIfIncomplete();
			}
		}

		App.gbp.c1_1__tfdp.normalTrickDisplayTimer.stop();
		App.gbp.c1_1__tfdp.reviewTrickDisplayTimer.stop();
		App.gbp.c1_1__bfdp.reviewBidDisplayTimer.stop();
		App.gbp.c1_1__bfdp.biddingCompleteDelayTimer.stop();
		App.gbp.c1_1__bfdp.biddingCompleteDelayTimer_part2.stop();

		mode = newMode;

		switch (mode) {

		case Aaa.NORMAL:
			App.localShowHidden = false;
			App.gbp.c2_2__bbp.startAutoBidDelayTimerIfNeeded();
			App.gbp.c1_1__tfdp.normalTrickDisplayTimer_startIfNeeded();
			break;

		case Aaa.REVIEW_BIDDING:
			App.reviewBid = 0;
			App.gbp.c1_1__bfdp.reviewBiddingMakeCopy();
			break;

		case Aaa.REVIEW_PLAY:
			// only reached from Normal
			break;

		case Aaa.EDIT_CHOOSE:
			break;

		case Aaa.EDIT_HANDS:
			break;

		case Aaa.EDIT_BIDDING:
			App.deal.wipeContractAndPlay();
			App.deal.removeAnyFinalPasses();
			break;

		case Aaa.EDIT_PLAY:
			App.deal.finishBiddingIfIncomplete();
			App.gbp.c1_1__tfdp.normalTrickDisplayTimer_startIfNeeded();
			break;
		}

		App.gbp.matchPanelsToDealState();
	};

	/**   
	 */
	static public boolean isModeAnyEdit() {
		// ========================================================================
		return (mode >= Aaa.EDIT_CHOOSE /* includes all edit modes */);
	}

	/**   
	 */
	static public boolean isSeatVisible(int compass) {
		// ========================================================================
		if (alwaysShowHidden || (mode >= Aaa.EDIT_CHOOSE /* includes all edit modes */)) {
			return true;
		}

		if ((mode == Aaa.NORMAL) && App.deal.isFinished() || ((mode == Aaa.REVIEW_BIDDING || mode == Aaa.REVIEW_PLAY) && localShowHidden)) {
			return true;
		}

		boolean declarerValid = App.deal.isDeclarerValid();
		int youSeatHint = App.deal.youSeatHint;
		int youSeatHintAxis = youSeatHint % 2;
		int compassAxis = compass % 2;

		if (!declarerValid) {
			if (compassAxis == Zzz.NS && youSeatHintAxis == Zzz.NS)
				return true;// yes for both North and South
			else
				return compass == youSeatHint;
		}

		int declarer = App.deal.contractCompass;
		int dummy = (declarer + 2) % 4;
		int youSeat = (youSeatHint == dummy) ? declarer : youSeatHint;

		if (compass == youSeat)
			return true;

		if (compass == dummy) {
			if (youSeat == declarer && App.youAutoplayAlways)
				return true; // we need to see the full two hands before play starts
			return App.deal.countCardsPlayed() > 0;
		}

		return false;
	};

	/**   
	 */
	static public boolean isAutoPlay(int compass) {
		// ========================================================================

		if ((mode == Aaa.EDIT_HANDS) || (mode == Aaa.EDIT_BIDDING) || (mode == Aaa.EDIT_PLAY) || !App.deal.isPlaying()) {
			return false;
		}

		if (App.youAutoplayAlways) {
			return true;
		}

		int youSeatHint = App.deal.youSeatHint;
		int declarer = App.deal.contractCompass;
		int dummy = (declarer + 2) % 4;
		int youSeat = (youSeatHint == dummy) ? declarer : youSeatHint;

		if (compass == youSeat) {
			return false; // manual play if you are the - youSeat
		}

		if (compass == dummy && youSeat == declarer) {
			return false; // manual play if you are the - dummy AND partner of the youSeat
		}

		return true;
	};

	/**   
	 */
	static public boolean isAutoBid(int compass) {
		// ========================================================================

		if ((mode == Aaa.EDIT_HANDS) || (mode == Aaa.EDIT_BIDDING) || (mode == Aaa.EDIT_PLAY) || !App.deal.isBidding()) {
			return false;
		}

		int youSeatHint = App.deal.youSeatHint;
		int compassAxis = compass % 2;

		if (compassAxis == Zzz.NS && App.dealCriteria.contentEquals("userBids")) {
			// you only bid (AutoBid == false) if you are the youSeatHint
			return !(compass == youSeatHint);
		}

		return true;
	};

	/**   
	 */
	static void reviewBackToStartOfPlay() {
		// ========================================================================
		App.reviewTrick = 0;
		App.reviewCard = 0;
		App.frame.repaint();
	}

	/**
	 */
	public static void switchToDeal(Deal deal) {
		// ========================================================================

		App.deal = deal;
		App.calcCompassPhyOffset();
		App.gbp.dealMajorChange();

		App.reviewTrick = 0;
		App.reviewCard = 0;

		App.setMode(Aaa.NORMAL);

		if (App.lin != null) {
			if (App.deal.isBidding()) {
				App.deal.youSeatHint = Zzz.South;
			}
			else {
				// South as a value represents the Declarer etc
				App.deal.youSeatHint = (App.deal.contractCompass + (App.youSeatForLinDeal - Zzz.South) + 4) % 4;
			}
		}

		if (App.showOldFinAsReview && App.deal.isFinished()) {
			App.setMode(Aaa.REVIEW_PLAY);
			App.localShowHidden = false;
			App.reviewTrick = 0;
			App.reviewCard = 0;
		}

		App.gbp.matchPanelsToDealState();
		App.frame.repaint();
		App.gbp.c2_2__bbp.startAutoBidDelayTimerIfNeeded();
		if (App.deal.isPlaying()) {
			App.gbp.c1_1__tfdp.clearAllCardSuggestions();
			App.gbp.c1_1__tfdp.makeCardSuggestions(); // for the "test" file if loaded
		}
		App.frame.setTitleAsRequired();
	}

	/**   
	 */
	public static void switchToNewLin(Lin lin) {
		// ========================================================================

		App.lin = lin;

		App.linBtns.matchToAppLin();

		if (lin != null) {
			App.switchToDeal(lin.get(0).deepClone());
		}
		App.frame.validate();
	}

	public static String[] args;
	public static AaOuterFrame frame = null;
	public static Controller con = new Controller();
	public static GreenBaizePanel gbp = null;
	public static AaLinButtonsPanel linBtns = null;
	public static Deal deal = null;
	public static Lin lin = null;

}
