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

import java.awt.Color;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import com.rogerpf.aabridge.igf.BubblePanel;
import com.rogerpf.aabridge.igf.MassGi;
import com.rogerpf.aabridge.igf.TutNavigationBar;
import com.rogerpf.aabridge.igf.TutorialPanel;
import com.rogerpf.aabridge.model.Cc;
import com.rogerpf.aabridge.model.Deal;
import com.rogerpf.aabridge.model.Dir;
import com.rogerpf.aabridge.model.Lin;
import com.rogerpf.aabridge.model.Zzz;
import com.rogerpf.aabridge.view.AaBookPanel;
import com.rogerpf.aabridge.view.AaaOuterFrame;
import com.rogerpf.aabridge.view.ButtonPanelLeft;
import com.rogerpf.aabridge.view.ButtonPanelRight;
import com.rogerpf.aabridge.view.CommonCmdBar;
import com.rogerpf.aabridge.view.DePointAy;
import com.rogerpf.aabridge.view.DealNavigationBar;
import com.rogerpf.aabridge.view.DualDealListButtonsPanel;
import com.rogerpf.aabridge.view.GreenBaizeMerged;
import com.rogerpf.aabridge.view.GreenBaizeOverlay;
import com.rogerpf.aabridge.view.GreenBaizePanel;
import com.rogerpf.aabridge.view.GreenBaizeRigid;
import com.rogerpf.aabridge.view.PhoneyTutorialPanel;
import com.version.VersionAndBuilt;

// @formatter:off

/**
 */
public class App {
	
	// ONLY set here by hand - start
	
	public static boolean FLAG_canOpen = true;
	public static boolean FLAG_canSave = true;
	public static boolean FLAG_drag_n_drop = true;
	public static boolean FLAG_expires = false;
	
	// ONLY set here by hand - end

	public static String autoSavesPath;
	public static String savesPath;
	public static String dealExt = "aaBridge";
	public static String dotAaBridgeExt = '.' + dealExt;
	public static String linExt = "lin";
	public static String dotLinExt = '.' + linExt;
	
	public static String depFinOutInPath     = "C:\\ProgramSmall\\Deep Finesse\\aaBridge_to_from";
	public static String depFinOutInFilename = "Sample Deals.txt";
	public static String depFinOutInBoth     = depFinOutInPath + "\\" + depFinOutInFilename;

	public static boolean observeReleaseDates = true;

	static int mode = Aaa.NORMAL_ACTIVE;

	// Visual Mode
	public static final int Vm_InsideADeal = 1;
	public static final int Vm_DealAndTutorial = 2;
	public static final int Vm_TutorialOnly = 3;


	public static int visualMode = Vm_InsideADeal;  // visualMode

	public static int reviewTrick = 0; 
	public static int reviewCard  = 0;

	public static int reviewBid = 0; 

	public static boolean saveDialogShowing = false;  // state
	public static boolean allConstructionComplete = false;
	
	public static boolean flowOnlyCommandBar = false;
	public static boolean hideCommandBar = false;
	public static boolean hideTutNavigationBar = false;
	public static boolean lbx_nextAndTellClicked = false;
	public static boolean lbx_modeExam = false;
	public static boolean lbx_earlyEndMassGi = false;

	
	public static final float GBP_WING_PANEL_SIZE_PC = 11.0f;
	public static final float GBP_SIDE_EDGE_GAP_PC = 1.0f;


	public static final float GBP_CORE_SIMPLE_WIDTH =  2100;
	public static final float GBP_CORE_SIMPLE_HEIGHT = 1000;


	public static final float CMD_BAR_PERCENT = 5.5f;
	public static final float NAV_BAR_PERCENT = 5.0f;

	public static final float GAP_SMALL_GRAY_PERCENT = 0.5f;



	public final static String hm0oneHun = "hidemode 0, width 100%, height 100%";
	public final static String hm1oneHun = "hidemode 1, width 100%, height 100%";
	public final static String hm3oneHun = "hidemode 3, width 100%, height 100%";
	public final static String simple = "insets 0 0 0 0, gap 0! 0!";
	
	public final static int RopTab_0_Deals = 0;
	public final static int RopTab_1_Seat = 1;
	public final static int RopTab_2_Autoplay = 2;
	public final static int RopTab_3_SuitColors = 3;
	public final static int RopTab_4_DFC = 4;
	public final static int RopTab_5_DSizeFont = 5;
	public final static int RopTab_6_RedHints = 6;
	public final static int RopTab_7_ShowBtns = 7;
	public final static int RopTab_Max = 7;


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


	private static final Color mnHeaderColorAy[] = {
		    new Color(110, 150, 110),  // StdGreen - first
			new Color(66, 198, 198),
			new Color(123, 100, 30),
			new Color(60, 90, 140),
			new Color(42, 99, 127),
			new Color(131, 69, 141),
			new Color(140, 69, 76),
			new Color(172, 149, 88),
			new Color(76, 157, 158),
			new Color(130, 177, 98),
			new Color(160, 98, 170),
			new Color(176, 101, 126),
	};

	public static Color  mnHeaderColor = mnHeaderColorAy[0]; 

	/**
	 */
	public static void selectMnHeaderColor() {
		// =============================================================================
		if (App.randomMnHeaderColor) {
			mnHeaderColor = mnHeaderColorAy[ (int) (Math.random() * mnHeaderColorAy.length) ];
//			mnHeaderColor = mnHeaderColorAy[ 7 ]; // testing old ones
//			mnHeaderColor = mnHeaderColorAy[ mnHeaderColorAy.length - 1 ]; // testing new ones
		}
	}


	/**
	 */
	static public void incOffsetClockwise() {
		compassPhyOffset = (compassPhyOffset - 1 + 4)%4;  // yes -1 not +1
		App.gbp.dealDirectionChange();
		App.frame.repaint();
	};

	/**
	 */
	static public void incOffsetAntiClockwise() {
		compassPhyOffset = (compassPhyOffset + 1)%4;  // +1  not -1
		App.gbp.dealDirectionChange();
		App.frame.repaint();
	};

	/**
	 */
	static public Dir cpeFromPhyScreenPos(Dir phyPos) {
		return phyPos.rotate(compassPhyOffset);
	};

	/**
	 */
	static public Dir phyScreenPosFromCompass(Dir compass) {
		return compass.rotate(-compassPhyOffset);
	};


	/**
	 */
	public static int getCompassPhyOffset() {
		return compassPhyOffset;
	}

	/**
	 */
	public static void resetPhyOffset() {
		compassPhyOffset = 0;
	}

	/**
	 */
	static public void calcCompassPhyOffset() {

		compassPhyOffset =  Dir.North.v; //  ie zero  or none

		if ((putWhoInSouthZone > 0) && !App.deal.isBidding() && !App.deal.contract.isPass()) {

			if (putWhoInSouthZone == 1) {
				// put the declarer in the south zone
				compassPhyOffset = App.deal.contractCompass.rotate180().v;
			} else { // case 2
				// put the 'you seat' in the south zone
				Dir youSeat = (respectLinYou) ? youSeatHint : youSeatForLinDeal;
				compassPhyOffset = App.deal.contractCompass.rotate(youSeat.v).v;
			}
		}
	};

	/**
	 */
	static public void dfcCalcCompassPhyOffset() {
		App.resetPhyOffset();
		if (App.lbx_modeExam == false || dfcExamBottomYou == false) {
			return;
		}

		if (dfcExamYou == Dir.West) {
			incOffsetAntiClockwise();
		}
		else if (dfcExamYou == Dir.East) {
			incOffsetClockwise();
		}
	};




	public static int compassPhyOffset = 0; // 0 to 3   
	public static boolean localShowHidden = false;


	//************* Preferences that are saved and restored - start *****************

	public static int prevPrefsBuildNo;    // added in 2228  not currently used

	public static boolean startedWithCleanSettings;

	public static String lastRunAaBridgeJar;

	public static int frameLocationX;
	public static int frameLocationY;
	public static int frameHeight;
	public static int frameWidth;
	public static boolean maximized;
	public static int horzDividerLocation;
	public static int vertDividerLocation;
	public static int ropSelPrevTabIndex;	
	public static int ropSelectedTabIndex;	

	public static boolean showRedEditArrow;
	public static boolean showRedDividerArrow;
	public static boolean showRedNewBoardArrow;
	public static boolean showRedVuGraphArrow;
	public static boolean showDfcExamHlt;
	public static boolean showBidPlayMsgs;
	public static boolean showSuitSymbols;
	public static boolean alwaysShowHidden;
	public static boolean tutorialShowAuction;
	public static boolean showPoints;
	public static boolean showLTC;
	public static boolean youAutoAdjacent;

	public static boolean youAutoSingletons;

	public static boolean youAutoplayAlways;
	public static boolean youAutoplayPause;
	public static boolean youPlayerEotWait;

	public static boolean yourFinessesMostlyFail;
	
	public static boolean dfcHyphenForVoids;
	public static boolean dfcCardsAsBlobs;
	public static int     dfcTrainingSuitSort;
	public static int     dfcAutoNext;
	public static int     dfcExamDifficulity;
	public static Dir     dfcExamYou;
	public static boolean dfcExamBottomYou;

	public static boolean showClaimBtn;
	public static boolean showRotationBtns;
	public static boolean showSaveBtns;
	public static boolean showDepFinBtns;
	public static boolean showShfWkPlBtn;

	public static boolean showEdPyCmdBarBtns;
	public static boolean fillHandDisplay = false;
	public static boolean runTestsAtStartUp = false;
	public static boolean showTestsLogAtEnd = true;
	public static int     putWhoInSouthZone = 0; // 0 to 2;

	public static String  dealCriteria;
	public static boolean watchBidding;
	public static Dir     youSeatForNewDeal;  
	public static Dir     youSeatForLinDeal;  // South => declarer
	public static int     defenderSignals; 
	public static boolean respectLinYou;
	public static boolean reviewFromPlay;
	public static boolean showOpeningLead;

	public static boolean showDdAsMin;
	public static boolean showDdWithResults;
	public static boolean showDdResultTots;
	public static int     tutorialDealSize; // 0 to 4   was  0 to 3

	public static boolean outlineCardEdge;
	public static boolean movieBidFlowDoesFlow;
	public static boolean useFamilyOverride;
	public static boolean fontfamilyStandardAvailable = true; // set false later if absent
	public static String  fontfamilyStandard = "Arial";
	public static String  fontfamilyOverride = "";


	public static boolean randomMnHeaderColor;	
	public static int colorIntensity;
	public static int colorTint;
	public static int eotExtendedDisplay;
	public static int playPluseTimerMs;
	public static int bidPluseTimerMs;
	public static int finalCardTimerMs;
	
	// currently these are fixed constants *********************************************
	// none
	// currently these are fixed constants *********************************************

	public static final int scoringFollowsYou = 0;
	public static final int scoringFollowsDeclarer = 1;
	public static final int scoringFollowsSouthZone = 2;     
	public static int       scoringFollows = scoringFollowsYou;    // not (yet) saved or setable
	
	public static final String preferencesNode   = "com.rogerpf.watson-v2.prefs";
	

	// @formatter:on
	public static void SetOptionsToDefaultAndClose() {

		Preferences appPrefs = Preferences.userRoot().node(preferencesNode);
		try {
			appPrefs.removeNode(); // deletes all the preferences
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
		System.exit(0); // SHUTS DOWN aaBridge NOW
	}

	// @formatter:off

	//************* Preferences that are saved and restored - end   *****************
	
	public static int defaultColorIntensity = -70;

	public static void loadPreferences() {

		Preferences appPrefs = Preferences.userRoot().node(preferencesNode);

		prevPrefsBuildNo     = appPrefs.getInt("prevPrefsBuildNo",         0); // added in 2228  not currently used
		
		startedWithCleanSettings   = appPrefs.getBoolean("startedWithCleanSettings", true); // but always saved as false

		int orig_width = 940;
		int orig_height = 700;

		int orig_horz_div  = orig_width - App.frame.RIGHT_OPT_PANEL_WIDTH__narrow;
		int orig_vert_div  = orig_height - App.frame.BOTTOM_OPT_PANEL_HEIGHT;

		frameLocationX     = appPrefs.getInt("frameLocationX",          50);
		frameLocationY     = appPrefs.getInt("frameLocationY",          35);
		frameWidth         = appPrefs.getInt("frameWidth",                 orig_width);
		frameHeight        = appPrefs.getInt("frameHeight",                orig_height);
		maximized          = appPrefs.getBoolean("maximized",        false);
		horzDividerLocation= appPrefs.getInt("horzDividerLocation",        orig_horz_div);
		vertDividerLocation= appPrefs.getInt("vertDividerLocation",        orig_vert_div);
		ropSelPrevTabIndex = appPrefs.getInt("ropSelectedTabIndex",      0);
		if (ropSelPrevTabIndex > App.RopTab_Max)
			ropSelPrevTabIndex = 0;

		showRedEditArrow   = appPrefs.getBoolean("showRedEditArrow",   true);
		showRedDividerArrow= appPrefs.getBoolean("showRedDividerArrow",true);
		showRedNewBoardArrow= appPrefs.getBoolean("showRedNewBoardArrow",true);
		showRedVuGraphArrow= appPrefs.getBoolean("showRedVuGraphArrow",true);
		showDfcExamHlt     = appPrefs.getBoolean("showDfcExamHlt",    true);
		showBidPlayMsgs    = appPrefs.getBoolean("showBidPlayMsgs",   true);
		showSuitSymbols    = appPrefs.getBoolean("showSuitSymbols",   false);
		showPoints         = appPrefs.getBoolean("showPoints",        false);
		showLTC            = appPrefs.getBoolean("showLTC",           false);
		youAutoSingletons  = appPrefs.getBoolean("youAutoSingletons", false);
		youAutoAdjacent    = appPrefs.getBoolean("youAutoAdjacent",   true);

		yourFinessesMostlyFail = appPrefs.getBoolean("yourFinessesMostlyFail", false);
		
		dfcHyphenForVoids  = appPrefs.getBoolean("dfcHyphenForVoids", true);
		dfcCardsAsBlobs    = appPrefs.getBoolean("dfcCardsAsBlobs",   false);
		dfcTrainingSuitSort= appPrefs.getInt("dfcTrainingSuitSort",       3);
		if (dfcTrainingSuitSort < 0 || dfcTrainingSuitSort > 2)
			dfcTrainingSuitSort = 0;
		dfcAutoNext        = appPrefs.getInt("dfcAutoNext",              3);
		if (dfcAutoNext < 0 || dfcAutoNext > 3)
			dfcAutoNext = 3;
		dfcExamDifficulity = appPrefs.getInt("dfcExamDifficulity",       0);
		if (dfcExamDifficulity < 0 || dfcExamDifficulity > 3)
			dfcExamDifficulity = 3;
		dfcExamYou  = Dir.dirFromInt(appPrefs.getInt("dfcExamYou",  Dir.West.v));
		if (dfcExamYou != Dir.West && dfcExamYou != Dir.East && dfcExamYou!= Dir.South)
			dfcExamYou = Dir.West;
		dfcExamBottomYou   = appPrefs.getBoolean("dfcExamBottomYou", true);

		alwaysShowHidden   = appPrefs.getBoolean("alwaysShowHidden", false);
		youAutoplayAlways  = false; // appPrefs.getBoolean("youAutoplayAlways",false); so it needs to me manualy set each time
		youAutoplayPause   = appPrefs.getBoolean("youAutoplayPause",  true);
		youPlayerEotWait   = appPrefs.getBoolean("youPlayerEotWait",  true);
		showClaimBtn       = appPrefs.getBoolean("showClaimBtn",     false);
		showRotationBtns   = appPrefs.getBoolean("showRotationBtns",  true);
		showSaveBtns       = appPrefs.getBoolean("showSaveBtns",     false);
		showDepFinBtns     = appPrefs.getBoolean("showDepFinBtns",   false);
		showEdPyCmdBarBtns = appPrefs.getBoolean("showEdPyCmdBarBtns", false);
		showShfWkPlBtn     = appPrefs.getBoolean("showShfWkPlBtn",   true);
		// fillHandDisplay = appPrefs.getBoolean("fillHandDisplay", false); - never saved or restored
		runTestsAtStartUp  = appPrefs.getBoolean("runTestsAtStartUp",false);
		showTestsLogAtEnd  = appPrefs.getBoolean("showTestsLogAtEnd", true);

		youSeatForNewDeal  = Dir.dirFromInt(appPrefs.getInt("youSeatForNewDeal",  Dir.South.v));
		youSeatForLinDeal  = Dir.dirFromInt(appPrefs.getInt("youSeatForLinDeal",  Dir.South.v));
		dealCriteria       = appPrefs.get("dealCriteria",  "ntGrand_E");
		dealCriteria       = Deal.validateDealCriteria( dealCriteria);
		watchBidding       = appPrefs.getBoolean("watchBidding",  true);

		defenderSignals    = appPrefs.getInt("defenderSignals", Zzz.NoSignals);
		if (defenderSignals > Zzz.HighestSignal) defenderSignals = Zzz.NoSignals;
		putWhoInSouthZone  = appPrefs.getInt("putWhoInSouthZone",       0);
		if (putWhoInSouthZone < 0 || putWhoInSouthZone > 2)
			putWhoInSouthZone = 0;
		respectLinYou      = appPrefs.getBoolean("respectLinYou",       true);
		reviewFromPlay     = appPrefs.getBoolean("reviewFromPlay",      true);
		showOpeningLead    = appPrefs.getBoolean("showOpeningLead",     true);
		showDdAsMin        = appPrefs.getBoolean("showDdAsMin",         false);
		showDdWithResults  = appPrefs.getBoolean("showDdWithResults",   true);
		showDdResultTots   = appPrefs.getBoolean("showDdResultTots",    true);
		tutorialDealSize   = appPrefs.getInt("tutorialDealSize",        4);
		if (tutorialDealSize < 0 || tutorialDealSize > 4)
			tutorialDealSize = 4;

		Cc.deckColorStyle  = appPrefs.getInt("deckColorStyle", Cc.Dk__Green_Blue_Red_Black);
		Cc.deckCardsBlack  = appPrefs.getInt("deckCardsBlack", 0);

		outlineCardEdge    = appPrefs.getBoolean("outlineCardEdge", false);
		movieBidFlowDoesFlow= appPrefs.getBoolean("movieBidFlowDoesFlow", false);
		colorIntensity     = appPrefs.getInt("colorIntensity",      defaultColorIntensity); // -255 to +255
		colorTint          = appPrefs.getInt("colorTint",           0); //  -50 to +50
		playPluseTimerMs   = appPrefs.getInt("playPluseTimerMs",  300);
		bidPluseTimerMs    = appPrefs.getInt("bidPluseTimerMs",   700);
		eotExtendedDisplay = appPrefs.getInt("eotExtendedDisplay",  2);

		randomMnHeaderColor= appPrefs.getBoolean("randomMnHeaderColor", true);
		useFamilyOverride  = appPrefs.getBoolean("useFamilyOverride", false);
		fontfamilyOverride = appPrefs.get("fontfamilyOverride", "Times Roman");

		implement_showRotationBtns();
		implement_showSaveBtns();
		implement_showSaveBtns();
	}
	
	public static boolean isPauseAtEotClickWanted() {	
		return      (youAutoplayAlways && youAutoplayPause)
			   ||  
			         !youAutoplayAlways 
			   && (   youPlayerEotWait && (deal.countCardsPlayed() > 0) )
				   ;
	}

	public static void savePreferences() {

		Preferences appPrefs = Preferences.userRoot().node(preferencesNode);

		appPrefs.putInt("prevPrefsBuildNo",  VersionAndBuilt.buildNo);

		appPrefs.putBoolean("startedWithCleanSettings", false); //  always saved as false

		App.maximized = (App.frame.getExtendedState() == java.awt.Frame.MAXIMIZED_BOTH);

		appPrefs.putInt("frameLocationX",      frameLocationX);
		appPrefs.putInt("frameLocationY",      frameLocationY);
		appPrefs.putInt("frameWidth",          frameWidth);
		appPrefs.putInt("frameHeight",         frameHeight);
		appPrefs.putBoolean("maximized",       maximized);
		appPrefs.putInt("horzDividerLocation", horzDividerLocation);
		appPrefs.putInt("vertDividerLocation", vertDividerLocation);
		appPrefs.putInt("ropSelectedTabIndex", ropSelectedTabIndex);

		appPrefs.putBoolean("showRedEditArrow",  showRedEditArrow);
		appPrefs.putBoolean("showRedDividerArrow",  showRedDividerArrow);
		appPrefs.putBoolean("showRedNewBoardArrow",  showRedNewBoardArrow);
		appPrefs.putBoolean("showRedVuGraphArrow",  showRedVuGraphArrow);
		appPrefs.putBoolean("showDfcExamHlt",   showDfcExamHlt);
		appPrefs.putBoolean("showBidPlayMsgs",   showBidPlayMsgs);
		appPrefs.putBoolean("showSuitSymbols",   showSuitSymbols);
		appPrefs.putBoolean("alwaysShowHidden",  alwaysShowHidden);
		appPrefs.putBoolean("showPoints",        showPoints);
		appPrefs.putBoolean("showLTC",           showLTC);
		appPrefs.putBoolean("youAutoSingletons", youAutoSingletons);
		appPrefs.putBoolean("youAutoAdjacent",   youAutoAdjacent);

		appPrefs.putBoolean("yourFinessesMostlyFail", yourFinessesMostlyFail);
		
		appPrefs.putBoolean("dfcHyphenForVoids", dfcHyphenForVoids);
		appPrefs.putBoolean("dfcCardsAsBlobs",   dfcCardsAsBlobs);
		appPrefs.putInt("dfcTrainingSuitSort",   dfcTrainingSuitSort);
		appPrefs.putInt("dfcAutoNext",           dfcAutoNext);
		appPrefs.putInt("dfcExamDifficulity",    dfcExamDifficulity);
		appPrefs.putInt("dfcExamYou",            dfcExamYou.v);
		appPrefs.putBoolean("dfcExamBottomYou",  dfcExamBottomYou);

		appPrefs.putBoolean("youAutoplayAlways", youAutoplayAlways);
		appPrefs.putBoolean("youAutoplayPause",  youAutoplayPause);
		appPrefs.putBoolean("youPlayerEotWait",  youPlayerEotWait);
		appPrefs.putBoolean("showClaimBtn",      showClaimBtn);

		appPrefs.putBoolean("showRotationBtns",  showRotationBtns);
		appPrefs.putBoolean("showSaveBtns",      showSaveBtns);
		appPrefs.putBoolean("showDepFinBtns",    showDepFinBtns);
		appPrefs.putBoolean("showEdPyCmdBarBtns",showEdPyCmdBarBtns);
		appPrefs.putBoolean("showShfWkPlBtn",    showShfWkPlBtn);
	  //appPrefs.putBoolean("fillHandDisplay",   fillHandDisplay);  - not saved or restored
		appPrefs.putBoolean("runTestsAtStartUp", runTestsAtStartUp);
		appPrefs.putBoolean("showTestsLogAtEnd", showTestsLogAtEnd);
		appPrefs.putInt("youSeatForNewDeal",     youSeatForNewDeal.v);
		appPrefs.putInt("youSeatForLinDeal",     youSeatForLinDeal.v);
		appPrefs.put("dealCriteria",             dealCriteria);
		appPrefs.putBoolean("watchBidding",      watchBidding);
		appPrefs.putInt("defenderSignals",       defenderSignals);
		appPrefs.putBoolean("showDdAsMin",       showDdAsMin);
		appPrefs.putBoolean("showDdWithResults", showDdWithResults);
		appPrefs.putBoolean("showDdResultTots",  showDdResultTots);
		appPrefs.putInt("putWhoInSouthZone",     putWhoInSouthZone);
		appPrefs.putBoolean("respectLinYou",     respectLinYou);
		appPrefs.putBoolean("reviewFromPlay",    reviewFromPlay);
		appPrefs.putBoolean("showOpeningLead",   showOpeningLead);
		appPrefs.putInt("tutorialDealSize",      tutorialDealSize);

		appPrefs.putInt("deckColorStyle",        Cc.deckColorStyle);
		appPrefs.putInt("deckCardsBlack",        Cc.deckCardsBlack);

		appPrefs.putBoolean("outlineCardEdge", outlineCardEdge);
		appPrefs.putBoolean("movieBidFlowDoesFlow", movieBidFlowDoesFlow);
		appPrefs.putInt("colorIntensity",        colorIntensity);
		appPrefs.putInt("colorTint",             colorTint);
		appPrefs.putInt("playPluseTimerMs",      playPluseTimerMs);
		appPrefs.putInt("bidPluseTimerMs",       bidPluseTimerMs);
		appPrefs.putInt("eotExtendedDisplay",    eotExtendedDisplay);

		appPrefs.putBoolean("randomMnHeaderColor",randomMnHeaderColor);
		appPrefs.putBoolean("useFamilyOverride", useFamilyOverride);
		appPrefs.put("fontfamilyOverride",       fontfamilyOverride);
		
		appPrefs.putBoolean("multiBookDisplay",  true);  // this gets older versions to start in multibook mode
	}
	
	// @formatter:on

	public static void implement_youAutoplayAlways() {
		if (allConstructionComplete) {
			if (App.mode == Aaa.NORMAL_ACTIVE && App.youAutoplayAlways) {
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

	public static void implement_showSaveBtns() {
		if (allConstructionComplete) {
			App.gbp.matchPanelsToDealState();
		}
	}

	public static void implement_showDepFinBtns() {
		if (allConstructionComplete) {
			App.gbp.matchPanelsToDealState();
		}
	}

	public static void implement_showShfWkPlBtn() {
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
	public static String getDotAndExtension(String f) {
		// ========================================================================
		int len = f.length();
		for (int i = len - 1; i >= 0; i--) {
			char c = f.charAt(i);
			if (c == '\\' || c == '/')
				return "";
			if (c == '.') {
				return f.substring(i);
			}
		}
		return "";
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
			if (newMode == Aaa.NORMAL_ACTIVE) {
				; // do nothing
			}
			else {
				App.deal.finishBiddingIfIncomplete();
			}
		}

		App.con.stopAllTimers();

		mode = newMode;

		switch (mode) {

		case Aaa.NORMAL_ACTIVE:
			if (App.isLin__Simple())
				App.localShowHidden = false;
			App.con.startAutoBidDelayTimerIfNeeded();
			App.gbp.c1_1__tfdp.normalTrickDisplayTimer_startIfNeeded();
			break;

		case Aaa.REVIEW_BIDDING:
			App.reviewBid = 0;
			App.gbp.c1_1__bfdp.reviewBiddingMakeCopy();
			break;

		case Aaa.REVIEW_PLAY:
			// only reached from Normal
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
		return (mode >= Aaa.EDIT_HANDS /* includes all edit modes */);
	}

	/**   
	 */
	static public boolean isModeAnyReview() {
		// ========================================================================
		return (mode == Aaa.REVIEW_BIDDING || mode == Aaa.REVIEW_PLAY);
	}

	/**   
	 */
	static public boolean isSeatVisible(Dir compass) {
		// ========================================================================
		if ((alwaysShowHidden || localShowHidden) || isModeAnyEdit()) {
			return true;
		}

		if ((mode == Aaa.NORMAL_ACTIVE) && App.deal.isFinished() || ((mode == Aaa.REVIEW_BIDDING || mode == Aaa.REVIEW_PLAY) && localShowHidden)) {
			return true;
		}

		boolean declarerValid = App.deal.isDeclarerValid();
		Dir youSeatHint = App.deal.youSeatHint;
		int youSeatHintAxis = youSeatHint.v % 2;
		int compassAxis = compass.v % 2;

		if (!declarerValid) {
			if (compassAxis == Dir.NS && youSeatHintAxis == Dir.NS)
				return (App.isVmode_Tutorial()) ? (compass == youSeatHint) : true;// yes for both North and South
			else
				return compass == youSeatHint;
		}

		int declarer = App.deal.contractCompass.v;
		int dummy = (declarer + 2) % 4;
		int youSeat = (youSeatHint.v == dummy) ? declarer : youSeatHint.v;

		if (compass.v == youSeat)
			return true;

		if (compass.v == dummy) {
			int RHO = (declarer + 3) % 4;
			if ((App.mg.lin.linType == Lin.SimpleDealVirgin) && (youSeat == declarer || youSeat == RHO) && App.youAutoplayAlways) {
				return App.isVmode_InsideADeal(); // we need to see the full two hands before play starts (unless we are a tutorial)
			}

			if (App.isMode(Aaa.REVIEW_BIDDING) || App.isMode(Aaa.REVIEW_PLAY) && App.isVmode_InsideADeal() && (App.reviewCard == 0) && (App.reviewTrick == 0)) {
				return false;
			}

			if ((App.deal.countCardsPlayed() > 0)) {
				return true;
			}
		}

		return false;
	};

	/**   
	 */
	static public boolean isAutoPlay(Dir compass) {
		// ========================================================================

		if (isVmode_Tutorial() || (mode == Aaa.EDIT_HANDS) || (mode == Aaa.EDIT_BIDDING) || (mode == Aaa.EDIT_PLAY) || !App.deal.isPlaying()) {
			return false;
		}

		if (App.youAutoplayAlways) {
			return true;
		}

		Dir youSeatHint = App.deal.youSeatHint;
		Dir declarer = App.deal.contractCompass;
		Dir dummy = declarer.rotate180();
		Dir youSeat = (youSeatHint == dummy) ? declarer : youSeatHint;

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
	static public boolean isAutoBid(Dir compass) {
		// ========================================================================

		if ((mode == Aaa.EDIT_HANDS) || (mode == Aaa.EDIT_BIDDING) || (mode == Aaa.EDIT_PLAY) || !App.deal.isBidding()) {
			return false;
		}

		Dir youSeatHint = App.deal.youSeatHint;
		int compassAxis = compass.v % 2;

		if (compassAxis == Dir.NS && App.dealCriteria.contentEquals("userBids")) {
			// you only bid (AutoBid == false) if you are the youSeatHint
			return !(compass.v == youSeatHint.v);
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

		App.dealMajorChange();

		if (App.mg.lin != null) {
			if (App.respectLinYou && App.deal.youSeatInLoadedLin) {
				; // the youSeatHint was preset at (lin) load time
			}
			else {
				// South as a value represents the Declarer etc
				if (App.respectLinYou == false) {
					App.deal.youSeatHint = App.deal.contractCompass.rotate(App.youSeatForLinDeal.rotate(Dir.South));
				}
			}
		}

		App.setVisualMode(App.Vm_InsideADeal);

		App.reviewBid = 0;
		App.reviewTrick = 0;
		App.reviewCard = 0;

		if (App.mg.lin.linType == Lin.SimpleDealVirgin) {
			App.setMode(Aaa.NORMAL_ACTIVE); // We will start in bidding or playing (if there is a pre set contract)
		}
		else if (App.deal.isBidding()) {
			App.setMode(Aaa.REVIEW_BIDDING);
		}
		else {
			App.setMode(Aaa.REVIEW_PLAY);
			if (App.deal.countCardsPlayed() > 0)
				App.reviewCard = 1; // We like to show the first lead if there is one
		}

		if ((App.mg.lin.linType == Lin.FullMovie) && App.showRedEditArrow) {
			App.gbo.showEditHint();
		}

		App.gbp.matchPanelsToDealState();

		if (App.mg.lin.linType == Lin.SimpleDealVirgin) {
			if (App.deal.isPlaying()) {
				// do we need to start a play timer ?
				App.gbp.c1_1__tfdp.clearAllCardSuggestions();
				App.gbp.c1_1__tfdp.makeCardSuggestions(); // for the "test" file if loaded
			}
			else if (App.deal.isBidding()) {
				// do we need to start the bidding timer ?
			}
		}
		App.frame.setTitleAsRequired();

		App.frame.repaint();
	}

	/**   
	 */
	public static void switchToNewMassGi(String inf) {
		// ========================================================================

		App.dualDealListBtns.matchToLin();

		if (App.mg.lin.linType == Lin.SimpleDealVirgin || App.mg.lin.linType == Lin.SimpleDealSingle) {

			App.setVisualMode(App.Vm_InsideADeal);

			Deal deal = null;

			if (App.mg.lin.linType == Lin.SimpleDealVirgin) {
				deal = App.mg.lin.virginDeal;
			}
			else /* if (App.mg.lin.linType == Lin.SimpleDealSingle) */{
				deal = App.mg.getBestSingleSimpleDeal();
			}

			App.switchToDeal(deal);

		}
		else {
			// we are a Tutorial mode (including vuGraph)
			App.setMode(Aaa.NORMAL_ACTIVE); // should this happen elsewhere !!! ?
			App.setVisualMode(App.Vm_DealAndTutorial);
			App.mg.setTheReadPoints_FirstTime();
		}

		App.gbp.matchPanelsToDealState();

		App.frame.setTitleAsRequired();

		// App.frame.validate();
	}

	/**
	 */
	public static boolean isLin__FullMovie() {
		// ==============================================================================================
		return App.mg.lin.linType == Lin.FullMovie;
	}

	/**
	 */
	public static boolean isLin__Simple() {
		// ==============================================================================================
		return (App.mg.lin.linType == Lin.SimpleDealVirgin || App.mg.lin.linType == Lin.SimpleDealSingle);
	}

	/**   
	 */
	public static boolean isLin__VuGraphAndTwoTeams() {
		// ==============================================================================================
		return (App.mg.lin.linType == Lin.VuGraph || App.mg.lin.twoTeams);
	}

	/**   
	 */
	public static boolean isVmode_InsideADeal() {
		// ==============================================================================================
		return visualMode == Vm_InsideADeal;
	}

	/**
	 */
	public static boolean isVmode_Tutorial() {
		// ==============================================================================================
		return visualMode == Vm_DealAndTutorial || visualMode == Vm_TutorialOnly;
	}

	/**
	 */
	public static int getVisualMode() {
		// ==============================================================================================
		return visualMode;
	}

	/**
	 */
	public static void setVisualMode() {
		// ==============================================================================================
		App.frame.setVisualMode(App.visualMode);
	}

	/**
	 */
	public static void setVisualMode(int vMode) {
		// ==============================================================================================
		App.frame.setVisualMode(vMode);
		App.calcApplyBarVisiblity();
		App.frame.repaint();
	}

	/**
	 */
	public static void calcApplyBarVisiblity() {
		// ==============================================================================================

		if (App.ccb != null)
			App.ccb.calcApplyBarVisiblity();

		if (App.bpl != null)
			App.bpl.calcApplyBarVisiblity();

		if (App.bpr != null)
			App.bpr.calcApplyBarVisiblity();

		App.frame.repaint();
	}

	/**
	 */
	public static void dealMajorChange() {
		App.gbp.dealMajorChange();
	}

	/**
	 */
	public static void colorIntensityChange() {
		Cc.colorIntensityChange();
		App.ccb.colorIntensityChange();
		Aaa.colorIntensityChange();
		App.dualDealListBtns.matchToLin();
	}

	public static Shape createArrowShape(Point fromPt, Point toPt) {
		// ==============================================================================================
		Polygon arrowPolygon = new Polygon();
		arrowPolygon.addPoint(-6, 1);
		arrowPolygon.addPoint(3, 1);
		arrowPolygon.addPoint(3, 3);
		arrowPolygon.addPoint(6, 0);
		arrowPolygon.addPoint(3, -3);
		arrowPolygon.addPoint(3, -1);
		arrowPolygon.addPoint(-6, -1);

		Point midPoint = midpoint(fromPt, toPt);

		double rotate = Math.atan2(toPt.y - fromPt.y, toPt.x - fromPt.x);

		AffineTransform transform = new AffineTransform();
		transform.translate(midPoint.x, midPoint.y);
		double ptDistance = fromPt.distance(toPt);
		double scale = ptDistance / 12.0; // 12 because it's the length of the arrow polygon.
		transform.scale(scale, scale);
		transform.rotate(rotate);

		return transform.createTransformedShape(arrowPolygon);
	}

	private static Point midpoint(Point p1, Point p2) {
		return new Point((int) ((p1.x + p2.x) / 2.0), (int) ((p1.y + p2.y) / 2.0));
	}

	public static Dir youSeatHint = Dir.South;
	public static boolean devMode = false;

	public static final VersionAndBuilt vnb = new VersionAndBuilt();

	public static BookshelfArray bookshelfArray = new BookshelfArray();
//	public static Bookshelf shelf1 = bookshelfArray.get(1); // for debug viewing only
//	public static Bookshelf shelf2 = bookshelfArray.get(2); // for degug viewing only
	public static String[] args;
	public static AaaOuterFrame frame = null;
	public static Controller con = new Controller();
	public static ButtonPanelLeft bpl = null;
	public static ButtonPanelRight bpr = null;
	public static GreenBaizePanel gbp = null;
	public static GreenBaizeOverlay gbo = null;
	public static GreenBaizeMerged gbm = null;
	public static GreenBaizeRigid gbr = null;
	public static PhoneyTutorialPanel ptp = null;
	public static TutorialPanel tup = null;
	public static DealNavigationBar dnb = null;

	public static CommonCmdBar ccb = null;
	public static TutNavigationBar tnb = null;
	public static AaBookPanel bookPanel = null;
	public static BubblePanel[] bubblePanels = { null, null, null, null };
	public static DualDealListButtonsPanel dualDealListBtns = null;
	public static Book book = new Book();
	public static Deal deal = null;
	public static DePointAy dePointAy = null;
	public static MassGi mg = null;
//	public static MassGi mg_to_restore = null;

}
