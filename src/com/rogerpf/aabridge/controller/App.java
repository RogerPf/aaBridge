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

import java.util.prefs.Preferences;

import com.rogerpf.aabridge.model.Deal;
import com.rogerpf.aabridge.view.AaOuterFrame;
import com.rogerpf.aabridge.view.GreenBaizePanel;

// @formatter:off

/**
 */
public class App {

	public static String savesPath;
	static String dealExt = "aaBridge";
	
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
	static public void setEwVisible(boolean visible) {
		visCards = visCardsOpts[(visible) ? Aaa.VisC__SHOW_ALL : Aaa.VisC__SHOW_NS_ONLY];
	};

	/**   
	 */
	static public boolean isEwVisible() {
		return (visCards == visCardsOpts[Aaa.VisC__SHOW_ALL]);  
	};

	/**   
	 */
	static public void setEwVisibleOnFinishedState() {
		setEwVisible(App.deal.isFinished());
	};

	private static int compassPhyOffset = 0; // 0 to 3
           
	
	public static boolean visCardsOptsAlways[][] = {
		{true,  true,  true,  true},   
	    {true,  true,  true,  true}    
		};
	public static boolean visCardsOptsStandard[][] = {
		{true,  false, true, false},   
	    {true,  true,  true, true }    
		};
	public static boolean visCardsOpts[][] = {
		{true,  false, true, false},    // 0 show N S only 
	    {true,  true,  true, true }     // 1  show all
		};
	public static boolean visCards[] = visCardsOpts[Aaa.VisC__SHOW_NS_ONLY]; 
	
	
	public static boolean autoBidOptsAll[][] = {
		{false, false, false, false},
		{true,  true,  true, true },  
		};
	public static boolean autoBidOptsStd[][] = {  
		{false, false, false, false},  
		{true,  true,  false, true }, 
	};
	public static boolean autoBidOpts[][] = { // nesw
		{false, false, false, false},  // 0  NONE
		{true,  true,  false, true },  // 1  ALL_BUT_SOUTH  
		};
	public static boolean autoBid[] = autoBidOpts[Aaa.ABid__ALL_BUT_SOUTH];
	
	
	public static boolean autoPlayOptions[][] = { // nesw
		{false, false, false, false},  // 0  NONE
		{false, true,  false, true },  // 1  EW_ONLY  
		};
	public static boolean autoPlay[] = autoPlayOptions[Aaa.APlay__EW_ONLY];
	
	
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
	public static boolean showWelcome = true;
	public static boolean alwaysShowEW = false;
	public static boolean showPoints = false;
	public static boolean nsAutoAdjacent = true;

	public static boolean nsAutoSingletons = false;

	public static boolean nsAutoplayAlways = false;
	public static boolean nsAutoplayPause = false;
	
	public static boolean nsFinessesMostlyFail = false;
           
	public static boolean showRotationBtns = false;
	public static boolean fillHandDisplay = false;
	public static boolean unusedRPanelT = false;
	public static boolean unusedRPanelV = false;
	
	public static String dealCriteria = "userBids";
	public static boolean watchBidding = true;


	public static int playPluseTimerMs = 600;
	public static int bidPluseTimerMs = 750;
	//************* Preferences that are saved and restored - end   *****************
	

	public static void loadPreferences() {
		Preferences appPrefs = Preferences.userRoot().node("com.rogerpf.aabridge.prefs");

		frameLocationX   = appPrefs.getInt("frameLocationX", 150);
		frameLocationY   = appPrefs.getInt("frameLocationY", 100);
		frameWidth       = appPrefs.getInt("frameWidth", 720);
		frameHeight      = appPrefs.getInt("frameHeight", 500);
		maximized        = appPrefs.getBoolean("maximized",        false);
		horzDividerLocation = appPrefs.getInt("horzDividerLocation", 99999);
		vertDividerLocation = appPrefs.getInt("vertDividerLocation", 99999);
		ropSelPrevTabIndex	= appPrefs.getInt("ropSelectedTabIndex", 0);	
		
		showWelcome      = appPrefs.getBoolean("showWelcome",      true);
		showBidPlayMsgs  = appPrefs.getBoolean("showBidPlayMsgs",  true);
		showPoints       = appPrefs.getBoolean("showPoints",       false);
		nsAutoSingletons = appPrefs.getBoolean("nsAutoSingletons", false);
		nsAutoAdjacent   = appPrefs.getBoolean("nsAutoAdjacent",   true);
		
		nsFinessesMostlyFail = appPrefs.getBoolean("nsFinessesMostlyFail", false);

		alwaysShowEW     = appPrefs.getBoolean("alwaysShowEW",     false);
		nsAutoplayAlways = appPrefs.getBoolean("nsAutoplayAlways", false);
		nsAutoplayPause  = appPrefs.getBoolean("nsAutoplayPause",  true);
		showRotationBtns = appPrefs.getBoolean("showRotationBtns", false);
		//fillHandDisplay  = appPrefs.getBoolean("fillHandDisplay", false); - not saved or restored
		unusedRPanelT    = appPrefs.getBoolean("unusedRPanelT",    false);
		unusedRPanelV    = appPrefs.getBoolean("unusedRPanelV",   false);
		
		playPluseTimerMs = appPrefs.getInt("playPluseTimerMs", 600);
		bidPluseTimerMs  = appPrefs.getInt("bidPluseTimerMs",  750);
		dealCriteria     = appPrefs.get("dealCriteria",      "twoSuitSlam_E");
		dealCriteria     = Deal.validateDealCriteria( dealCriteria);
		
		watchBidding     = appPrefs.getBoolean("watchBidding",  true);

		implement_alwaysShowEW();
        implement_showRotationBtns();
        setAutoBidOpts();
	}

	public static void savePreferences() {
		Preferences appPrefs = Preferences.userRoot().node("com.rogerpf.aabridge.prefs");
		
		appPrefs.putInt("frameLocationX",  frameLocationX);
		appPrefs.putInt("frameLocationY",  frameLocationY);
		appPrefs.putInt("frameWidth",      frameWidth);
		appPrefs.putInt("frameHeight",     frameHeight);
		appPrefs.putBoolean("maximized",   maximized);
		appPrefs.putInt("horzDividerLocation", horzDividerLocation);
		appPrefs.putInt("vertDividerLocation", vertDividerLocation);
		appPrefs.putInt("ropSelectedTabIndex", ropSelectedTabIndex);
		
		appPrefs.putBoolean("showWelcome",      showWelcome);
		appPrefs.putBoolean("showBidPlayMsgs",  showBidPlayMsgs);
		appPrefs.putBoolean("alwaysShowEW",     alwaysShowEW);
		appPrefs.putBoolean("showPoints",       showPoints);
		appPrefs.putBoolean("nsAutoSingletons", nsAutoSingletons);
		appPrefs.putBoolean("nsAutoAdjacent",   nsAutoAdjacent);
		
		appPrefs.putBoolean("nsFinessesMostlyFail", nsFinessesMostlyFail);
		
		appPrefs.putBoolean("nsAutoplayAlways", nsAutoplayAlways);
		appPrefs.putBoolean("nsAutoplayPause",  nsAutoplayPause);
		appPrefs.putBoolean("showRotationBtns", showRotationBtns);
		//appPrefs.putBoolean("fillHandDisplay",  fillHandDisplay);  - not saved or restored
		appPrefs.putBoolean("unusedRPanelT",    unusedRPanelT);
		appPrefs.putBoolean("unusedRPanelV",    unusedRPanelV);
		
		appPrefs.putInt("playPluseTimerMs",     playPluseTimerMs);
		appPrefs.putInt("bidPluseTimerMs",      bidPluseTimerMs);
		appPrefs.put("dealCriteria",         dealCriteria);
		appPrefs.putBoolean("watchBidding",      watchBidding);
	}
	
	// @formatter:on

	public static void implement_alwaysShowEW() {
		visCardsOpts = (App.alwaysShowEW ? App.visCardsOptsAlways : App.visCardsOptsStandard);
		visCards = App.visCardsOpts[(App.alwaysShowEW ? Aaa.VisC__SHOW_ALL : Aaa.VisC__SHOW_NS_ONLY)];
	}

	public static void implement_showRotationBtns() {
		if (allConstructionComplete) {
			App.gbp.c0_0__tlp.setRotationBtnsVisibility();
		}
	}

	public static void implement_fillHandDisplay() {
		if (allConstructionComplete) {
			App.frame.repaint();
		}
	}

	public static void setAutoBidOpts() {
		autoBidOpts = (Deal.southBiddingRequired(App.dealCriteria) ? App.autoBidOptsStd : App.autoBidOptsAll);
		autoBid = App.autoBidOpts[Aaa.ABid__ALL_BUT_SOUTH];
	}

	/**   
	 */
	public static boolean isMode(int modeV) {
		return (mode == modeV);
	};

	/** 
	 *   
	 */
	static public void setMode(int newMode) {

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
			autoBid = autoBidOpts[Aaa.ABid__ALL_BUT_SOUTH];
			autoPlay = autoPlayOptions[Aaa.APlay__EW_ONLY];
			App.setEwVisibleOnFinishedState();
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
			App.setEwVisible(true);
			break;

		case Aaa.EDIT_HANDS:
			App.setEwVisible(true);
			break;

		case Aaa.EDIT_BIDDING:
			App.deal.wipeContractAndPlay();
			App.deal.removeAnyFinalPasses();
			autoBid = autoBidOpts[Aaa.ABid__NONE];
			App.setEwVisible(true);
			break;

		case Aaa.EDIT_PLAY:
			App.deal.finishBiddingIfIncomplete();
			autoPlay = autoPlayOptions[Aaa.APlay__NONE];
			App.gbp.c1_1__tfdp.normalTrickDisplayTimer_startIfNeeded();
			App.setEwVisible(true);
			break;
		}

		App.gbp.matchPanelsToDealState();
	};

	public static AaOuterFrame frame = null;
	public static Controller con = new Controller();
	public static GreenBaizePanel gbp = null;
	public static Deal deal = null;

}
