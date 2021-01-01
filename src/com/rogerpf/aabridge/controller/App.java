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
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import com.rogerpf.aabridge.controller.Book.LinChapter;
import com.rogerpf.aabridge.igf.BubblePanel;
import com.rogerpf.aabridge.igf.History;
import com.rogerpf.aabridge.igf.MassGi;
import com.rogerpf.aabridge.igf.MassGi_utils;
import com.rogerpf.aabridge.igf.TutNavigationBar;
import com.rogerpf.aabridge.igf.TutorialPanel;
import com.rogerpf.aabridge.model.Cc;
import com.rogerpf.aabridge.model.Deal;
import com.rogerpf.aabridge.model.Dir;
import com.rogerpf.aabridge.model.Hand;
import com.rogerpf.aabridge.model.Lin;
import com.rogerpf.aabridge.model.Zzz;
import com.rogerpf.aabridge.view.AaBookPanel;
import com.rogerpf.aabridge.view.AaHomeBtnPanel;
import com.rogerpf.aabridge.view.AaaMenu;
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
		
	public static String homePath; 
	public static String desktop_folder;
	public static String cmdsAndScripts_folder;
	public static String cached_lins_folder;
	public static String temp_MyHands_folder;
	public static String temp_Other_folder;
	public static String realSaves_folder = "";
	public static String defaultSaves_folder = "";
	public static String default_downloads_folder = "";
	public static String downloads_folder = "";
	public static String dealExt = "aaBridge";
	public static String dotAaBridgeExt = '.' + dealExt;
	public static String linExt = "lin";
	public static String dotLinExt = '.' + linExt;
	
	public static String DEV_config_filename = "__aaBridge__DEV_config.txt";
	
	public static String debug_linfile_partner_path = "";
	public static String debug_linfile_partner_ext = "";
	public static boolean debug_suppress_single_undelt = false;
	
	
	public static final int numbAllowed_in_Temp_Other = 35;
	
	public static final int daysToKeepInLinCache = 91;
	public static final int miniutes_between_bbo_retry = 60;
	public static int seconds_between_bbo_call_attempts = 3;

	
	public static final String bbo_base_url = "http://www.bridgebase.com";
	
	public static boolean handPanelNameAreaInfoNumbersShow = true;
	
	public static String debug_pathlastLinLoaded = "";
	
	public static boolean observeReleaseDates = true;

	public static boolean haglundsDDSavailable = false;

	static int mode = Aaa.NORMAL_ACTIVE;

	public static int ratioFiddle; // 100 - 125 has an assumed divisor of 100
	public static final int defaultLinux_ratioFiddle = 115;

	// Visual Mode
	public static final int Vm_InsideADeal = 1;
	public static final int Vm_DealAndTutorial = 2;
	public static final int Vm_TutorialOnly = 3;

	public static boolean ddsAnalyserPanelVisible = false;
	public static boolean reinstateAnalyser = false;

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
	public static boolean dfc_earlyEndMassGi = false;
	public static boolean rqb_earlyEndMassGi = false;

	public static final String xxx_lin_name = "__xxx.lin";
	
	public static final float GBP_WING_PANEL_SIZE_PC = 11.0f;
	public static final float GBP_SIDE_EDGE_GAP_PC = 1.0f;


	public static final float GBP_CORE_SIMPLE_WIDTH =  2100;
	public static final float GBP_CORE_SIMPLE_HEIGHT = 1000;


	public static final float CMD_BAR_PERCENT = 5.5f;
	public static final float NAV_BAR_PERCENT = 5.0f;

	public static final float GAP_SMALL_GRAY_PERCENT = 0.5f;

	public static boolean onMac = false;
	public static boolean onWin = false;
	public static boolean onLinux = false;  // later assumed if other two are false
	public static boolean onMacOrLinux = false; 
	public static boolean using_java_6 = false;	
	public static boolean runningExpanded = false;
	public static String bundleSep = "/";

	public final static String hm0oneHun = "hidemode 0, width 100%, height 100%";
	public final static String hm1oneHun = "hidemode 1, width 100%, height 100%";
	public final static String hm3oneHun = "hidemode 3, width 100%, height 100%";
	public final static String simple = "insets 0 0 0 0, gap 0! 0!";
	
	public final static int RopTab_0_NewDealChoices = 0;
	public final static int RopTab_1_Autoplay = 1;
	public final static int RopTab_2_KibSeat = 2;
	public final static int RopTab_3_DFC = 3;
	public final static int RopTab_4_SuitColors = 4;
	public final static int RopTab_5_DSizeFont = 5;
	public final static int RopTab_6_RedHints = 6;
	public final static int RopTab_7_ShowOptionalBtns = 7;
	public final static int RopTab_Max = 7;
	
	public final static int Metric_None    = 0;
	public final static int Metric_KnR     = 1;
	public final static int Metric_LTC_Bas = 2;
	public final static int Metric_LTC_Ref = 3;
	public final static int Metric_Banzai  = 4;
	public final static int Metric_LAST    = 4;

	public final static float menuInfoSize = 1.1f;
	
	public static int tutRotate = 0;

	public static boolean isUsing__en_US = false; // set later

	public static boolean study_deal_maker = false;


	/*  reviewTrick = 7
	 *  reviewCard  = 3   
	 *   Means all of the cards in tricks indexed 0 to 6 inclusive 
	 *   and the first three cards (0,1,2) of trick indexed by 7 have been 
	 *   'played' i.e. removed from the hand, the last 3 cards being on the game. 
	 *  
	 *  7 4  follows after 7 3  and will show all of trick 7
	 *  8 0  follows after 7 4  show a blank table
	 *         
	 *  0 0   shows the original state with no cards played.			     
	 * 
	 */


	private static final Color mnHeaderColorAy[] = {
		    new Color(110, 150, 110),  // StdGreen - first 0
			new Color(66, 198, 198),   // 1
			new Color(123, 100, 30),   // 2
			new Color(60, 90, 140),    // 3
			new Color(42, 99, 127),
			new Color(131, 69, 141),
			new Color(140, 69, 76),
			new Color(172, 149, 88),
			new Color(76, 157, 158),
			new Color(130, 177, 98),
			new Color(160, 98, 170),
			new Color(176, 101, 126),
			new Color( 40,  60, 140),
	};

	public static Color mnHeaderColor = mnHeaderColorAy[3]; // 3 is soft dark blue
	
	/**
	 */
	public static float now_always_one() {
		// =============================================================================		
		return /* (onLinux && fixLinuxLineSep) ? 1.125f :*/  1.0f;
	}
	
	/**
	 */
	public static void selectMnHeaderColor() {
		// =============================================================================
		if (App.randomMnHeaderColor) {
			mnHeaderColor = mnHeaderColorAy[ (int) (Math.random() * mnHeaderColorAy.length) ];
		}
	}


	/**
	 */
	static public void incOffsetClockwise() {
		compassPhyOffset = (compassPhyOffset - 1 + 4) % 4;  // yes -1 not +1
		App.gbp.dealDirectionChange();
		App.frame.repaint();
	};

	/**
	 */
	static public void incOffsetAntiClockwise() {
		compassPhyOffset = (compassPhyOffset + 1) % 4;  // +1  not -1
		App.gbp.dealDirectionChange();
		App.frame.repaint();
	};

	/**
	 */
	static public Dir cpeFromPhyScreenPos(Dir phyPos) {
		return phyPos.rotate(compassPhyOffset - App.tutRotate);
	};

	/**
	 */
	static public Dir phyScreenPosFromCompass(Dir compass) {
		return compass.rotate(-compassPhyOffset + App.tutRotate);
	};


	/**
	 */
	public static int getCompassPhyOffset() {
		return compassPhyOffset + App.tutRotate;
	}

	/**
	 */
	public static void resetPhyOffset() {
		compassPhyOffset = compassAllTwister;
	}

	/**
	 */
	public static void allTwister_reset() {
		compassAllTwister = Dir.North.v;  
	}
	
	/**
	 */
	public static void allTwister_left() {		
		if (++compassAllTwister > 3) {
			compassAllTwister = 0;
		}
	}
	
	/**
	 */
	public static void allTwister_right() {
		if (--compassAllTwister < 0) {
			compassAllTwister = 3;
		}
	}

	/**
	 */
	static public Dir calcCompassPhyOffset() {

		compassPhyOffset =  compassAllTwister; //  was once   Dir.North.v  

		if (App.dlaeActive && !App.deal.isBidding() && !App.deal.contract.isPass()) {
			// put the you? 'seat' in the south zone
			Dir seat = Dir.directionFromInt(App.dlaeValue);
			compassPhyOffset = App.deal.contractCompass.rotate(seat.v).v;
			return seat;
		}
		return null;
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
	
	/**
	 */
	public static boolean isStudyDeal() {
		// return (isVmode_dealShowing() && (App.dealHasBotInstructions() || deal.zm_studyDeal_forced));
		return (isVmode_dealShowing() && deal.zm_studyDeal_forced );
	};

	/**
	 */
	public static boolean dealHasBotInstructions() {
		return App.deal.botExtra.hasBotInstructions();
	};

	/**
	 */
	public static void cloneToDealBk_or_makeDealBkNull() {
		// if (App.deal.botExtra.haveCardsMoved()) {
		// System.out.println(" Trying to Bk clone a deal with cardsMoved eeek");
		// assert(false);	
		//}
		App.deal_bk = null;
		if (App.dealHasBotInstructions()) {
			App.deal_bk = App.deal.deepClone();
		}
	}
	
	// @formatter:on

//	/**
//	 */
//	public static void biddingVisibilityCheck() {
//
//		if (App.cameFromPbnOrSimilar() && App.reinstateAnalyser && App.pbnAutoEnter) {
//			ddsAnalyserPanelVisible = true;
//		}
//		else {
//			ddsAnalyserPanelVisible = false;
//			return;
//		}
//
//		if (App.isVmode_Tutorial()) {
//			CmdHandler.tutorialIntoDealClever();
//		}
//		App.ddsAnalyserPanelVisible = false;
//
//		CmdHandler.ddsAnalyse(); // flips the above
//
//		App.ddsAnalyserPanelVisible = true;
//		App.pbnAutoEnter = true;
//
//		App.gbp.matchPanelsToDealState();
//	}

	// @formatter:off

	public static int compassPhyOffset = 0; // 0 to 3 
	public static int compassAllTwister = 0;  // 0 to 3


	//************* Preferences that are saved and restored - start *****************

	public static int prevPrefsBuildNo;    // added in 2228

	public static boolean startedWithCleanSettings;

	public static String lastRunAaBridgeJar;
	
	public static int frameLocationX;
	public static int frameLocationY;
	public static int frameWidth;
	public static int frameHeight;
	public static boolean maximized_both;
	public static boolean maximized_horiz;
	public static boolean maximized_vert;
	public static boolean frameDividersChangeWanted = false;
	public static int horzDividerLocation;
	public static int vertDividerLocation;
	public static int ropSelPrevTabIndex;	
	public static int ropSelectedTabIndex;	

	public static boolean showMouseWheelSplash;
	public static boolean showRedNewBoardArrow;
	public static boolean showRedEditArrow;
	public static boolean showRedDividerArrow;
	public static boolean showRedVuGraphArrow;
	public static boolean showDfcExamHlt;
	public static boolean showBidPlayMsgs;
	public static boolean showSuitSymbols;

	public static boolean ddsScoreShow;
	
	public static int  pap_who = 2;	// 2 = partner
	public static boolean papXes = true;	
	public static int     papBits = 0;
	
	public static boolean alwaysShowHidden;
	public static boolean force_N_HiddenTut;
	public static boolean force_W_HiddenTut;
	public static boolean force_E_HiddenTut;
	public static boolean force_S_HiddenTut;

	public static boolean localShowHidden;
	public static int     localShowHiddPolicy;

	public static boolean tutorialShowAuction;
	public static boolean showHCPs;
	public static int     show2ndMetric; 
//	public static boolean fixLinuxLineSep;  
	public static boolean youAutoAdjacent;
	
	public static boolean useDDSwhenAvaialble_autoplay;

	public static boolean youAutoSingletons;

	public static boolean youAutoplayAlways;
	public static boolean youAutoplayPause;
	public static boolean youAutoplayFAST;
	public static boolean youPlayerEotWait;

	public static boolean yourFinessesMostlyFail;
	
	public static boolean dfcWordsForCount;
	public static boolean dfcHyphenForVoids;
	public static boolean dfcAnonCards;
	public static int     dfcTrainingSuitSort;
	public static int     dfcAutoNext;
	public static int     dfcExamDifficulity;
	public static Dir     dfcExamYou;
	public static boolean dfcExamBottomYou;

	public static boolean showPoorDefHint;
	public static boolean showContNeededHint;
	public static int     showContFlashCount = 0;
	public static boolean showClaimBtn;

	public static boolean showRotationBtns;
	public static boolean showSaveBtns;
	public static boolean showShfWkPlBtn;

	public static boolean fillHandDisplay = false;
	
	public static boolean dlaeActive = false;
	
	public static boolean force_savename_xxx;
	public static boolean renumberDealsLin;  
	public static boolean bboUpStripped;  
	public static boolean prob_force_comments = false;  
	public static boolean linSaveRotateDecSouth;   
	public static boolean includeRotationsSetBelow; 
	public static boolean multiSaveBboTourny;
	public static boolean saveNoBidOrPlay;
	public static boolean saveOnlyTheLead;
	public static boolean discardPlayerNames;
	public static boolean prependYyySavePrefix;
	public static boolean alsoDealerSouth;
	public static int     linSaveUglyRotationCount = 0;

	public static String  dealCriteria;
	public static boolean watchBidding;
	public static int     dealFilter;
	public static Dir     youSeatForNewDeal;

	public static int     dlae_inactive = 0;
	public static int     dlae_RHO = 1;
	public static int     dlae_Declarer = 2;
	public static int     dlae_LHO = 3;
	
	public static int     dlaeValue = dlae_inactive;
	
	public static int     defenderSignals; 
	public static boolean reviewFromPlay;
	public static boolean showOpeningLead;
	public static boolean youSeatPartnerVis;

	public static boolean showDdAsMin;
	public static boolean showDdWithResults;
	public static boolean showDdResultTots;
	public static int     tutorialDealSize; // 0 to 4
	
	public static boolean showDdsScore_aaB_style;

	public static final int WMouse_FLOW = 0;
	public static final int WMouse_STEP = 1;
	public static final int WMouse_SINGLE = 2;
	public static       int mouseWheelDoes;

	public static       int mouseWheelSensitivity;   // 0 is max more is less sensitive
	public static boolean mouseWheelInverted;
	
	public static int     rotateWhenSaving;
	public static int     fixedQuarterTurns;
	public static boolean useCreationNameForSave;
	public static boolean forceSaveMultiDealToSavesFolder;
	
	public static boolean pbnInfoFirst = true;

	public static final int linFmt_BBO = 0;
	public static final int linFmt_Std = 1;
	public static final int linFmt_Prob = 2;
	public static final int linFmt_PrNoOp = 3;
	public static int     linfileSaveFormat;
	
	public static boolean outlineCardEdge;
	public static boolean fontfamilyStandardAvailable = true; // set false later if absent
	public static String  fontfamilyStandard = "Arial";
		
	public static boolean rtFound() {return App.mg.lin.rt_found; }
	
	public static boolean obeyAeCmd;
	public static boolean obeyRtCmd;

	public static boolean showBooksZMenu;
	public static boolean showLanguageMenu;
	public static boolean showTheDotTest;
	public static boolean showAllLangLin;
	public static boolean showDdsOnPlayedCards;
	public static boolean randomMnHeaderColor;
	public static boolean decodeWith1252;
	
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
	
	public static final String preferencesNode   = "com.rogerpf.aabridge-v3.prefs";
	
	
	// @formatter:on
	public static void deletePreferencesNode() {

		Preferences appPrefs = Preferences.userRoot().node(preferencesNode);
		try {
			appPrefs.removeNode(); // deletes all the preferences
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}

	}

	public static void SetOptionsToDefaultAndClose() {

		App.exitWipePrefsAndRelaunch(); // never returns
	}

	// @formatter:off

	//************* Preferences that are saved and restored - end   *****************
	
	public static int defaultColorIntensity = -60;
	public static int defaultplayPluseTimerMs = 420;
	public static int defaultEotExtendedDisplay = 3;

	public static void loadPreferences() {

		Preferences appPrefs = Preferences.userRoot().node(preferencesNode);

		prevPrefsBuildNo     = appPrefs.getInt("prevPrefsBuildNo",         0); // added in 2228  not currently used
		
		startedWithCleanSettings   = appPrefs.getBoolean("startedWithCleanSettings", true); // but always saved as false

		App.ratioFiddle = appPrefs.getInt("ratioFiddle", 99);
		if (App.ratioFiddle < 100 || App.ratioFiddle > 120) {
			if (App.onLinux) {
				App.ratioFiddle = App.defaultLinux_ratioFiddle;
			}
			else {
				App.ratioFiddle = 100;
			}
		}

		int orig_width = 1008;
		int orig_height = 724;

		int orig_horz_div  = orig_width - AaaMenu.O_STD;
		int orig_vert_div  = orig_height - AaaMenu.L_STD;

		frameLocationX     = appPrefs.getInt("frameLocationX",          50);
		frameLocationY     = appPrefs.getInt("frameLocationY",          35);
		frameWidth         = appPrefs.getInt("frameWidth",                 orig_width);
		frameHeight        = appPrefs.getInt("frameHeight",                orig_height);
		maximized_both     = appPrefs.getBoolean("maximized",        false); // not maximized_both 
		maximized_horiz    = appPrefs.getBoolean("maximized_horiz",  false);
		maximized_vert     = appPrefs.getBoolean("maximized_vert",   false);
		horzDividerLocation= appPrefs.getInt("horzDividerLocation",        orig_horz_div);
		vertDividerLocation= appPrefs.getInt("vertDividerLocation",        orig_vert_div);
		ropSelPrevTabIndex = appPrefs.getInt("ropSelectedTabIndex",      0);
		if (ropSelPrevTabIndex > App.RopTab_Max)
			ropSelPrevTabIndex = 0;
		

		ropSelPrevTabIndex = RopTab_2_KibSeat;  // We now always start in Kib Seat

		showMouseWheelSplash= appPrefs.getBoolean("showMouseWheelSplash",true);
		showRedNewBoardArrow= appPrefs.getBoolean("showRedNewBoardArrow",true);
		showRedEditArrow   = appPrefs.getBoolean("showRedEditArrow",   true);
		showRedDividerArrow= appPrefs.getBoolean("showRedDividerArrow",false);
		showRedVuGraphArrow= appPrefs.getBoolean("showRedVuGraphArrow",true);
		showDfcExamHlt     = appPrefs.getBoolean("showDfcExamHlt",    true);
		showBidPlayMsgs    = appPrefs.getBoolean("showBidPlayMsgs",   true);
		showSuitSymbols    = appPrefs.getBoolean("showSuitSymbols",   false);
		showHCPs           = appPrefs.getBoolean("showHCPs",          true);
		show2ndMetric      = appPrefs.getInt("show2ndMetric",         Metric_None);
		if (show2ndMetric < Metric_None || show2ndMetric > Metric_LAST)
			show2ndMetric = Metric_None;
//		fixLinuxLineSep    = appPrefs.getBoolean("fixLinuxLineSep",   App.onLinux);
		youAutoSingletons  = appPrefs.getBoolean("youAutoSingletons", false);
		youAutoAdjacent    = appPrefs.getBoolean("youAutoAdjacent",   false);

		useDDSwhenAvaialble_autoplay = true;
		// useDDSwhenAvaialble_autoplay = appPrefs.getBoolean("useDDSwhenAvaialble_autoplay", true);
		yourFinessesMostlyFail = appPrefs.getBoolean("yourFinessesMostlyFail", false);

		dfcWordsForCount   = appPrefs.getBoolean("dfcWordsForCount",  true);
		dfcHyphenForVoids  = appPrefs.getBoolean("dfcHyphenForVoids", true);
		dfcAnonCards    = appPrefs.getBoolean("dfcAnonCards",   false);
		dfcTrainingSuitSort= appPrefs.getInt("dfcTrainingSuitSort",      3);
		if (dfcTrainingSuitSort < 0 || dfcTrainingSuitSort > 2)
			dfcTrainingSuitSort = 0;
		dfcAutoNext        = appPrefs.getInt("dfcAutoNext",              1);
		if (dfcAutoNext < 0 || dfcAutoNext > 3)
			dfcAutoNext = 1;
		dfcExamDifficulity = appPrefs.getInt("dfcExamDifficulity",       0);
		if (dfcExamDifficulity < 0 || dfcExamDifficulity > 3)
			dfcExamDifficulity = 3;
		dfcExamYou  = Dir.dirFromInt(appPrefs.getInt("dfcExamYou",  Dir.West.v));
		if (dfcExamYou != Dir.West && dfcExamYou != Dir.East && dfcExamYou!= Dir.South)
			dfcExamYou = Dir.West;
		dfcExamBottomYou   = appPrefs.getBoolean("dfcExamBottomYou",  true);

		// ddsScoreShow       = appPrefs.getBoolean("ddsScoreShow",     false);
		ddsScoreShow       = false;  // now we always start with DDS show false

		compassAllTwister = 0;
		alwaysShowHidden  = false;
		force_N_HiddenTut = false;
		force_W_HiddenTut = false;
		force_E_HiddenTut = false;
		force_S_HiddenTut = false;

		if (App.su_clear_overide || App.devMode) { 
			
			alwaysShowHidden    = appPrefs.getBoolean("alwaysShowHidden",  false);
			
			if (App.su_clear_overide) {
    			force_N_HiddenTut   = appPrefs.getBoolean("force_N_HiddenTut", false);
    			force_W_HiddenTut   = appPrefs.getBoolean("force_W_HiddenTut", false);
    			force_E_HiddenTut   = appPrefs.getBoolean("force_E_HiddenTut", false);
    			force_S_HiddenTut   = appPrefs.getBoolean("force_S_HiddenTut", false);
    			
    			compassAllTwister   = appPrefs.getInt("compassAllTwister", 0);
    			if (compassAllTwister < 0 || compassAllTwister > 3) {
    				compassAllTwister = 0;
    			}
			}
		}
			
		renumberDealsLin    = false;
		bboUpStripped       = false;
		linSaveRotateDecSouth    = false;	
		multiSaveBboTourny  = false;
		saveNoBidOrPlay     = false;
		saveOnlyTheLead     = false;
		discardPlayerNames  = false;
		prependYyySavePrefix = false;

		force_savename_xxx  = appPrefs.getBoolean("force_savename_xxx",   false);
		prependYyySavePrefix = appPrefs.getBoolean("prependYyySavePrefix",   false);
		

		localShowHidden    = appPrefs.getBoolean("localShowHidden",   true);
		localShowHiddPolicy= appPrefs.getInt("localShowHiddPolicy",      0); // 0 = don't show,  1 = show, 2 = no change
		if (localShowHiddPolicy < 0 || localShowHiddPolicy > 2)
			localShowHiddPolicy = 0;

//		youAutoplayAlways  = appPrefs.getBoolean("youAutoplayAlways", false); 
//		if (!devMode) 
		youAutoplayAlways = false; 
//		youAutoplayPause   = appPrefs.getBoolean("youAutoplayPause",  false);
		youAutoplayPause   = false;
//		youAutoplayFAST    = appPrefs.getBoolean("youAutoplayFAST",   false);
		youAutoplayFAST    = false;
		youPlayerEotWait   = appPrefs.getBoolean("youPlayerEotWait",  true);
		showPoorDefHint    = appPrefs.getBoolean("showPoorDefHint",   true);
		showContNeededHint = appPrefs.getBoolean("showContNeededHint",true);
		showClaimBtn       = appPrefs.getBoolean("showClaimBtn",      false);

//		showRotationBtns   = appPrefs.getBoolean("showRotationBtns",  true);
		showRotationBtns   = true;
//		showSaveBtns       = appPrefs.getBoolean("showSaveBtns",      true);
		showSaveBtns       = true;
//		showShfWkPlBtn     = appPrefs.getBoolean("showShfWkPlBtn",    true);
		showShfWkPlBtn     = true;

		youSeatForNewDeal  = Dir.South;
		
		dealCriteria       = appPrefs.get("dealCriteria_2",  "twoSuitSlam_H2");
		dealCriteria       = Deal.validateDealCriteria( dealCriteria);
		watchBidding       = appPrefs.getBoolean("watchBidding",  true);
		dealFilter         = appPrefs.getInt("dealFilter", 4);
		if (dealFilter < 0 || dealFilter > 4)
			dealFilter = 4;

		defenderSignals    = appPrefs.getInt("defenderSignals", Zzz.NoSignals);
		if (defenderSignals > Zzz.HighestSignal) defenderSignals = Zzz.NoSignals;

		dlaeActive         = false;
		
		reviewFromPlay     = appPrefs.getBoolean("reviewFromPlay",      true);
		showOpeningLead    = appPrefs.getBoolean("showOpeningLead",     true);
		youSeatPartnerVis  = appPrefs.getBoolean("youSeatPartnerVis",   false);
		showDdAsMin        = appPrefs.getBoolean("showDdAsMin",         false);
		showDdWithResults  = appPrefs.getBoolean("showDdWithResults",   true);
		showDdResultTots   = appPrefs.getBoolean("showDdResultTots",    true);
		tutorialDealSize   = appPrefs.getInt("tutorialDealSize",        4);
		if (tutorialDealSize < 0 || tutorialDealSize > 4)
			tutorialDealSize = 4;
		
		showDdsScore_aaB_style = appPrefs.getBoolean("showDdsScore_aaB_style", true);

		Cc.deckColorStyle  = appPrefs.getInt("deckColorStyle", Cc.Dk__Green_Blue_Red_Black);
		if (Cc.deckColorStyle > Cc.Dk__last) {
			Cc.deckColorStyle = Cc.Dk__Green_Blue_Red_Black;
		}
		Cc.deckCardsBlack  = appPrefs.getInt("deckCardsBlack", 0);

		mouseWheelDoes     = appPrefs.getInt("mouseWheelDoes",  WMouse_FLOW);
		if (mouseWheelDoes < WMouse_FLOW || mouseWheelDoes > WMouse_SINGLE)
			mouseWheelDoes = WMouse_FLOW;

		mouseWheelSensitivity = appPrefs.getInt("mouseWheelSensitivity",  (App.onMac ? 4 : 0));
		if (mouseWheelSensitivity < 0 || mouseWheelSensitivity > 8)
			mouseWheelSensitivity = 0;

		mouseWheelInverted = appPrefs.getBoolean("mouseWheelInverted",  false);	
		
		seconds_between_bbo_call_attempts = appPrefs.getInt("seconds_between_bbo_call_attempts", 3);
		if (seconds_between_bbo_call_attempts < 1 || seconds_between_bbo_call_attempts > 3)
			seconds_between_bbo_call_attempts = 3;

		rotateWhenSaving = 0;
		fixedQuarterTurns = 0;
		useCreationNameForSave = appPrefs.getBoolean("useCreationNameForSave",  true);
		forceSaveMultiDealToSavesFolder = appPrefs.getBoolean("forceSaveMultiDealToSavesFolder",  false);
		
		linfileSaveFormat     = appPrefs.getInt("linfileSaveFormat",  0 /* BBO upload format */);
		if (linfileSaveFormat  < 0) linfileSaveFormat = 0; // 0 => BBO
		if (linfileSaveFormat  > 1) linfileSaveFormat = 0; // 1 => Normal 
		
		outlineCardEdge       = appPrefs.getBoolean("outlineCardEdge",        false);
				
		obeyAeCmd            = true;
		obeyRtCmd            = true;
		
		sd_dev_visibility = false;
		if (App.devMode) {
			obeyAeCmd      = appPrefs.getBoolean("obeyAeCmd",           true);
			obeyRtCmd      = appPrefs.getBoolean("obeyRtCmd",           true);
			sd_dev_visibility = appPrefs.getBoolean("sd_dev_visibility",      false);
		}
		showLanguageMenu = appPrefs.getBoolean("showLanguageMenu",  false);
		showBooksZMenu        = appPrefs.getBoolean("showBooksZMenu",         false);
		showTheDotTest        = appPrefs.getBoolean("showTheDotTest",         false);
		
		showAllLangLin        = appPrefs.getBoolean("showAllLangLin",         false);	
		showDdsOnPlayedCards       = appPrefs.getBoolean("showDdsOnPlayedCards",         true);
		randomMnHeaderColor   = appPrefs.getBoolean("randomMnHeaderColor",     true);
		decodeWith1252        = appPrefs.getBoolean("decodeWith1252",          true);	

		colorIntensity        = appPrefs.getInt("colorIntensity",      defaultColorIntensity); // -255 to +255
		
		colorTint          = appPrefs.getInt("colorTint",           0); //  -50 to +50
		playPluseTimerMs   = appPrefs.getInt("playPluseTimerMs",  defaultplayPluseTimerMs);
//		playPluseTimerMs   = defaultplayPluseTimerMs;
//		bidPluseTimerMs    = appPrefs.getInt("bidPluseTimerMs",   700);
		bidPluseTimerMs    = 700;
		eotExtendedDisplay = appPrefs.getInt("eotExtendedDisplay",  defaultEotExtendedDisplay);
//		eotExtendedDisplay = 2;
				
		realSaves_folder   = appPrefs.get("real SavesPath", "");
		downloads_folder   = appPrefs.get("downloads_folder", "");
		
		// pollDownloadsFolder = appPrefs.getBoolean("pollDownloadsFolder", true);		
		
		Aaf.loadPreferences(appPrefs);

		implement_showRotationBtns();
		implement_showSaveBtns();
		implement_showSaveBtns();
	}

	
	public static boolean isPauseAtEotClickWanted() {	
		int played = deal.countCardsPlayed(); 
		
		boolean ans =      (youAutoplayAlways && youAutoplayPause)
			   ||  
			         !youAutoplayAlways 
			   && (   youPlayerEotWait && (played > 0) && (played % 4 == 0))
				   ;
//		System.out.println( "isPause: " + (ans?"t":"f") + "   ");
		return ans;
	}

	public static void savePreferences() {

		Preferences appPrefs = Preferences.userRoot().node(preferencesNode);

		appPrefs.putInt("prevPrefsBuildNo",  VersionAndBuilt.buildNo);

		appPrefs.putBoolean("startedWithCleanSettings", false); //  always saved as false
		
		appPrefs.putInt("ratioFiddle", ratioFiddle);

		maximized_both  = (App.frame.getExtendedState() == java.awt.Frame.MAXIMIZED_BOTH);
		maximized_horiz = (App.frame.getExtendedState() == java.awt.Frame.MAXIMIZED_HORIZ);
		maximized_vert  = (App.frame.getExtendedState() == java.awt.Frame.MAXIMIZED_VERT);

		appPrefs.putInt("frameLocationX",      frameLocationX);
		appPrefs.putInt("frameLocationY",      frameLocationY);
		appPrefs.putInt("frameWidth",          frameWidth);
		appPrefs.putInt("frameHeight",         frameHeight);
		appPrefs.putBoolean("maximized",       maximized_both); // saved as maximized for legacy reasons
		appPrefs.putBoolean("maximized_horiz", maximized_horiz);
		appPrefs.putBoolean("maximized_vert",  maximized_vert);
		appPrefs.putInt("horzDividerLocation", horzDividerLocation);
		appPrefs.putInt("vertDividerLocation", vertDividerLocation);
		appPrefs.putInt("ropSelectedTabIndex", ropSelectedTabIndex);

		appPrefs.putBoolean("showMouseWheelSplash",  showMouseWheelSplash);
		appPrefs.putBoolean("showRedNewBoardArrow",  showRedNewBoardArrow);
		appPrefs.putBoolean("showRedEditArrow",  showRedEditArrow);
		appPrefs.putBoolean("showRedDividerArrow",  showRedDividerArrow);
		appPrefs.putBoolean("showRedVuGraphArrow",  showRedVuGraphArrow);
		appPrefs.putBoolean("showDfcExamHlt",   showDfcExamHlt);
		appPrefs.putBoolean("showBidPlayMsgs",   showBidPlayMsgs);
		appPrefs.putBoolean("showSuitSymbols",   showSuitSymbols);
		appPrefs.putBoolean("ddsScoreShow",      ddsScoreShow);
		
		
		appPrefs.putInt("compassAllTwister",  compassAllTwister);
		appPrefs.putBoolean("alwaysShowHidden",   alwaysShowHidden);
		appPrefs.putBoolean("force_N_HiddenTut",  force_N_HiddenTut);
		appPrefs.putBoolean("force_W_HiddenTut",  force_W_HiddenTut);
		appPrefs.putBoolean("force_E_HiddenTut",  force_E_HiddenTut);
		appPrefs.putBoolean("force_S_HiddenTut",  force_S_HiddenTut);
		
		
		appPrefs.putBoolean("localShowHidden",   localShowHidden);
		appPrefs.putInt("localShowHiddPolicy",    localShowHiddPolicy);

		appPrefs.putBoolean("showHCPs",          showHCPs);
		appPrefs.putInt("show2ndMetric",         show2ndMetric);
//		appPrefs.putBoolean("fixLinuxLineSep",   fixLinuxLineSep);
		appPrefs.putBoolean("youAutoSingletons", youAutoSingletons);
		appPrefs.putBoolean("youAutoAdjacent",   youAutoAdjacent);

		appPrefs.putBoolean("useDDSwhenAvaialble_autoplay", useDDSwhenAvaialble_autoplay);
		appPrefs.putBoolean("yourFinessesMostlyFail", yourFinessesMostlyFail);
		
		appPrefs.putBoolean("dfcWordsForCount",  dfcWordsForCount);
		appPrefs.putBoolean("dfcHyphenForVoids", dfcHyphenForVoids);
		appPrefs.putBoolean("dfcAnonCards",      dfcAnonCards);
		appPrefs.putInt("dfcTrainingSuitSort",   dfcTrainingSuitSort);
		appPrefs.putInt("dfcAutoNext",           dfcAutoNext);
		appPrefs.putInt("dfcExamDifficulity",    dfcExamDifficulity);
		appPrefs.putInt("dfcExamYou",            dfcExamYou.v);
		appPrefs.putBoolean("dfcExamBottomYou",  dfcExamBottomYou);

		appPrefs.putBoolean("youAutoplayAlways", youAutoplayAlways);
		appPrefs.putBoolean("youAutoplayPause",  youAutoplayPause);
		appPrefs.putBoolean("youAutoplayFAST",   youAutoplayFAST);
		appPrefs.putBoolean("youPlayerEotWait",  youPlayerEotWait);
		appPrefs.putBoolean("showPoorDefHint",   showPoorDefHint);
		appPrefs.putBoolean("showContNeededHint",showContNeededHint);
		appPrefs.putBoolean("showClaimBtn",      showClaimBtn);

		appPrefs.putBoolean("showRotationBtns",  showRotationBtns);
		appPrefs.putBoolean("showSaveBtns",      showSaveBtns);
		appPrefs.putBoolean("showShfWkPlBtn",    showShfWkPlBtn);

		appPrefs.putBoolean("force_savename_xxx", force_savename_xxx);		
		appPrefs.putBoolean("prependYyySavePrefix",   prependYyySavePrefix);			

		appPrefs.put("dealCriteria_r2",          dealCriteria);
		appPrefs.putBoolean("watchBidding",      watchBidding);
		appPrefs.putInt("dealFilter",            dealFilter);
		
		appPrefs.putInt("defenderSignals",       defenderSignals);
		appPrefs.putBoolean("showDdAsMin",       showDdAsMin);
		appPrefs.putBoolean("showDdWithResults", showDdWithResults);
		appPrefs.putBoolean("showDdResultTots",  showDdResultTots);

		appPrefs.putBoolean("reviewFromPlay",    reviewFromPlay);
		appPrefs.putBoolean("showOpeningLead",   showOpeningLead);
		appPrefs.putBoolean("youSeatPartnerVis", youSeatPartnerVis);
		appPrefs.putInt("tutorialDealSize",      tutorialDealSize);
		
		appPrefs.putBoolean("showDdsScore_aaB_style", showDdsScore_aaB_style);

		appPrefs.putInt("deckColorStyle",        Cc.deckColorStyle);
		appPrefs.putInt("deckCardsBlack",        Cc.deckCardsBlack);

		appPrefs.putInt("mouseWheelDoes",        mouseWheelDoes);
		
		appPrefs.putInt("mouseWheelSensitivity", mouseWheelSensitivity);
		appPrefs.putBoolean("mouseWheelInverted",    mouseWheelInverted);
		appPrefs.putInt("seconds_between_bbo_call_attempts", seconds_between_bbo_call_attempts);

		appPrefs.putBoolean("useCreationNameForSave",   useCreationNameForSave);
		appPrefs.putBoolean("forceSaveMultiDealToSavesFolder",   forceSaveMultiDealToSavesFolder);
	
		appPrefs.putInt("linfileSaveFormat",           linfileSaveFormat);
		appPrefs.putBoolean("outlineCardEdge",         outlineCardEdge);
		appPrefs.putBoolean("obeyAeCmd",               obeyAeCmd);
		appPrefs.putBoolean("obeyRtCmd",               obeyRtCmd);
		appPrefs.putBoolean("sd_dev_visibility",       sd_dev_visibility);
		appPrefs.putBoolean("showLanguageMenu",   showLanguageMenu);
		appPrefs.putBoolean("showBooksZMenu",          showBooksZMenu);
		appPrefs.putBoolean("showTheDotTest",          showTheDotTest);
		appPrefs.putBoolean("showAllLangLin",          showAllLangLin);
		
		appPrefs.putInt("colorIntensity",        colorIntensity);
		appPrefs.putInt("colorTint",             colorTint);
		appPrefs.putInt("playPluseTimerMs",      playPluseTimerMs);
//		appPrefs.putInt("bidPluseTimerMs",       bidPluseTimerMs);
		appPrefs.putInt("eotExtendedDisplay",    eotExtendedDisplay);

		appPrefs.putBoolean("showDdsOnPlayedCards",    showDdsOnPlayedCards);
		appPrefs.putBoolean("randomMnHeaderColor",randomMnHeaderColor);

		appPrefs.put("real SavesPath",            realSaves_folder);
		appPrefs.put("downloads_folder",          downloads_folder);
		// if (App.poller_boss) {
		//    appPrefs.putBoolean("pollDownloadsFolder",pollDownloadsFolder);
		// }
			
		Aaf.savePreferences(appPrefs);
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

	public static void implement_showB1stBtn() {
		if (allConstructionComplete) {
			App.gbp.matchPanelsToDealState();
		}
	}

	public static void implement_showContBtn() {
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
	public static String removeExtension(String f) {
		// ========================================================================
		String ext = getDotAndExtension(f);
		return f.substring(0, f.length() - ext.length());
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

		// App.pbnAutoEnter = false;

		App.ddsDeal = null;
		// System.out.println(" App.setMode    ddsDeal  set to null");

		int oldMode = mode;

		if (oldMode == Aaa.EDIT_BIDDING) {
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
			// assert oldMode!=newMode;
			App.cloneToDealBk_or_makeDealBkNull();

			if (App.isLin__Virgin())
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
			// assert oldMode != newMode;
			App.cloneToDealBk_or_makeDealBkNull();

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
	static public boolean cameFromPbnOrSimilar() {
		// ========================================================================
		if (App.mg.cameFromPbn)
			return true;

		if (App.mg.lin.md_count >= 2 && App.mg.lin.mb_count == 0)
			return true;

		return false;
	}

	/**   
	 */
	static public boolean isSeatVisible(Hand hand) {
		// ========================================================================

		Dir compass = hand.compass;

		if (App.isStudyDeal() && App.sd_dev_visibility == false) {

			if (compass.v == App.deal.youSeatHint.v) {
				return true; // yes as we are the youSeat
			}

			if (App.deal.isDeclarerValid() == false) {
				return false; // Should not really be here ?
			}

			return (compass == App.deal.contractCompass.rotate180() && App.deal.countCardsPlayed() > 0);
		}

		if (force_N_HiddenTut && compass == Dir.North /* && App.isVmode_Tutorial() */) {
			return false;
		}

		if (force_W_HiddenTut && compass == Dir.West /* && App.isVmode_Tutorial() */) {
			return false;
		}

		if (force_E_HiddenTut && compass == Dir.East /* && App.isVmode_Tutorial() */) {
			return false;
		}

		if (force_S_HiddenTut && compass == Dir.South /* && App.isVmode_Tutorial() */) {
			return false;
		}

		if (alwaysShowHidden) {
			return true;
		}

		if (hand.vh_tutorial_vis != '-') {
			if (App.isVmode_Tutorial()) {
				if (hand.vh_tutorial_vis == 'h')
					return false;
				if (hand.vh_tutorial_vis == 'v')
					return true;
			}
		}

		if (localShowHidden) {
			return true;
		}

		if (isModeAnyEdit()) {
			return true;
		}

		if ((mode == Aaa.NORMAL_ACTIVE) && App.deal.isFinished() || ((mode == Aaa.REVIEW_BIDDING || mode == Aaa.REVIEW_PLAY) && localShowHidden)) {
			return true;
		}

		boolean declarerValid = App.deal.isDeclarerValid();

		if (compass.v == 3) {
			@SuppressWarnings("unused")
			int z = 0;
		}

		if (declarerValid && App.dlaeActive) {
			boolean isDummy = App.deal.contractCompass.v == compass.rotate180().v;
			if ((((2 + compass.v - App.deal.contractCompass.v) % 4) == ((6 - App.dlaeValue) % 4)) && !isDummy) {
				return true;
			}
		}

		Dir youSeatHint = App.deal.youSeatHint;

		if (!declarerValid) {
			if (App.isLin__Virgin()) { // so the North can be seen when entering contract to play
				int youSeatHintAxis = youSeatHint.v % 2;
				int compassAxis = compass.v % 2;
				if (compassAxis == Dir.NS && youSeatHintAxis == Dir.NS) {
					return (App.isVmode_Tutorial()) ? (compass == youSeatHint) : true; // yes for both North and South
				}
			}
			if (App.youSeatPartnerVis && (compass.v == (youSeatHint.v + 2) % 4) && App.isModeAnyReview())
				return true;
			else
				return compass == youSeatHint;
		}

		boolean dummyOk = App.isMode(Aaa.REVIEW_BIDDING);
		int declarer = App.deal.contractCompass.v;
		int dummy = (declarer + 2) % 4;
		int youSeat = (!dummyOk && (youSeatHint.v == dummy)) ? declarer : youSeatHint.v;

		if (compass.v == youSeat)
			return true;

		if (App.youSeatPartnerVis && (compass.v == (youSeat + 2) % 4) && App.isModeAnyReview()) {
			return true;
		}

		if (compass.v == dummy) {
			int RHO = (declarer + 3) % 4;
			if ((App.mg.lin.linType == Lin.SimpleDealVirgin) && (youSeat == declarer || youSeat == RHO) && App.youAutoplayAlways) {
				return App.isVmode_InsideADeal(); // we need to see the full two hands before play starts (unless we are a tutorial)
			}

			if (App.isMode(Aaa.REVIEW_BIDDING)) {
				return false;
			}

			boolean partial_deal = (App.deal.countOrigCards() < 52);

			if ((partial_deal == false) && App.isMode(Aaa.REVIEW_PLAY) && App.isVmode_InsideADeal() && (App.reviewCard == 0) && (App.reviewTrick == 0)) {
				return false;
			}

			if (partial_deal || (App.deal.countCardsPlayed() > 0)) {
				return true;
			}
		}

		return false;
	}

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
			if (!App.dlaeActive && App.deal.youSeatInLoadedLin) {
				; // the youSeatHint was preset at (lin) load time
			}
			else {
				if (App.dlaeActive) {
					App.deal.youSeatHint = App.deal.contractCompass.rotate(App.dlaeValue - 2);
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
		else if (App.deal.isBidding() || App.reviewFromPlay == false) {
			App.setMode(Aaa.REVIEW_BIDDING);
		}
		else {
			App.setMode(Aaa.REVIEW_PLAY);
			if ((App.deal.countCardsPlayed() > 0) && App.showOpeningLead && !App.deal.suppress_autoshow_opening_lead) {
				App.reviewCard = 1; // We like to show the first lead if there is one
			}
		}

		if (App.localShowHiddPolicy != 2) {
			App.localShowHidden = (App.localShowHiddPolicy == 1);
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
		else {
			if (!App.dealEnteredOnce) {
				App.dealEnteredOnce = true;
				App.frame.executeCmd("rightPanelPrefs2_KibSeat");
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

		if (App.mg.lin.linType == Lin.SimpleDealVirgin /* || App.mg.lin.linType == Lin.SimpleDealSingle */) {
			App.youSeatHint = Dir.South;

			App.setVisualMode(App.Vm_InsideADeal);
			Deal deal = App.mg.lin.virginDeal;
			App.deal = deal;
			App.switchToDeal(deal);

		}
		else if (App.mg.lin.linType == Lin.SimpleDealSingle) {
			App.youSeatHint = Dir.South;

			App.setMode(Aaa.NORMAL_ACTIVE); // should this happen elsewhere !!! ?
			App.setVisualMode(App.Vm_DealAndTutorial);
			App.mg.setTheReadPoints_FirstTime();
			MassGi_utils.do_tutorialIntoDealClever();

			// part of the bbo php myhands rotation mechainism
			if ((App.mg.lin.ae_count > 0) && App.obeyRtCmd && (App.mg.lin.first_rt_value > 0)) {
				for (int i = 0; i < App.mg.lin.first_rt_value; i++) {
					incOffsetClockwise();
				}
			}

		}
		else { // we are a Tutorial mode (including vuGraph) or Other
			App.youSeatHint = Dir.South;

			App.localShowHidden = false;
			App.setMode(Aaa.NORMAL_ACTIVE); // should this happen elsewhere !!! ?
			App.setVisualMode(App.Vm_DealAndTutorial);
			App.mg.setTheReadPoints_FirstTime();
		}

		App.gbp.matchPanelsToDealState();

		App.frame.setTitleAsRequired();
	}

	/**
	 */
	public static boolean isLin__FullMovie() {
		// ==============================================================================================
		return App.mg.lin.linType == Lin.FullMovie;
	}

	/**
	 */
	public static boolean isLin__Virgin_or_Single() {
		// ==============================================================================================
		return (App.mg.lin.linType == Lin.SimpleDealVirgin || App.mg.lin.linType == Lin.SimpleDealSingle);
	}

	/**
	 */
	public static boolean isLin__Virgin() {
		// ==============================================================================================
		return (App.mg.lin.linType == Lin.SimpleDealVirgin);
	}

	/**
	 */
	public static boolean isLin__Single() {
		// ==============================================================================================
		return (App.mg.lin.linType == Lin.SimpleDealSingle);
	}

	/**   
	 */
	public static boolean isLin__VuGraphAndTwoTeams() {
		// ==============================================================================================
		return (App.mg.lin.linType == Lin.VuGraph || App.mg.lin.twoTeams);
	}

	/**   
	 */
	public static boolean isVmode_dealShowing() {
		// ==============================================================================================
		return (visualMode == Vm_InsideADeal || visualMode == Vm_DealAndTutorial);
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
	public static boolean isVmode_DealAndTutorial() {
		// ==============================================================================================
		return visualMode == Vm_DealAndTutorial;
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

		if (App.aaHomeBtnPanel != null) {
			App.aaHomeBtnPanel.calcApplyBarVisiblity();
		}

		App.frame.repaint();
	}

	/**
	 */
	public static void exitAndRelaunch() {
		// ==============================================================================================
		App.savePreferences();
		if (System.getenv("rpf_in_eclipse") == null) {
			MassGi_utils.launch_2nd_aaBridge_WITH(App.args);
		}
		System.exit(0); // SHUTS DOWN aaBridge NOW
	}

	/**
	 */
	public static void exitWipePrefsAndRelaunch() {
		// ==============================================================================================
		deletePreferencesNode();
		if (System.getenv("rpf_in_eclipse") == null) {
			MassGi_utils.launch_2nd_aaBridge_WITH(App.args);
		}
		System.exit(0); // SHUTS DOWN aaBridge NOW
	}

	/**
	 */
	public static void dealMajorChange() {
		App.gbp.dealMajorChange();
	}

	/**
	 */
	public static void colorIntensityChange() {
		// ==============================================================================================
		Cc.colorIntensityChange();
		// App.ccb.colorIntensityChange();
		Aaa.colorIntensityChange();
		App.dualDealListBtns.matchToLin();
	}

	/**
	 */
	public static Boolean validateWindowPosition() {
		// ==============================================================================================
		// this changes the co-ords if the window is not clearly on the main screen
		int sideMinOverlap = 100;
		boolean canBeSeen = false;
		Rectangle aaBridgeInnerTop = new Rectangle(App.frameLocationX + sideMinOverlap, App.frameLocationY + 200, App.frameWidth - (2 * sideMinOverlap), 2);
		Rectangle aaBridgeInnerBot = new Rectangle(App.frameLocationX + sideMinOverlap, App.frameLocationY + 12, App.frameWidth - (2 * sideMinOverlap), 2);
		GraphicsDevice[] gs = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		for (int j = 0; j < gs.length; j++) {
			GraphicsConfiguration[] gc = gs[j].getConfigurations();
			for (int i = 0; i < gc.length; i++) {
				Rectangle bounds = gc[i].getBounds();
				if (bounds.intersects(aaBridgeInnerTop) && bounds.intersects(aaBridgeInnerBot)) {
					canBeSeen = true;
					break;
				}
			}
			if (canBeSeen)
				break;
		}

		if (canBeSeen == false) {
			App.frameLocationX = 90;
			App.frameLocationY = 65;
			App.frameWidth = 900;
			App.frameHeight = 650;
		}
		return (canBeSeen == false); // so true means overridden
	}

	/**
	 */
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

	/**
	 */
	private static Point midpoint(Point p1, Point p2) {
		return new Point((int) ((p1.x + p2.x) / 2.0), (int) ((p1.y + p2.y) / 2.0));
	}

	public static int menuTabExtra = 6;
	public static String menu2_text = "menu2";
	public static int menu2_ind = 20;

	public static Dir youSeatHint = Dir.South;
	public static boolean su_clear_overide = false;
	public static boolean sd_dev_visibility = false;
	public static boolean devMode = false;

	public static boolean showDevTestLins = false;
	public static String mruNodeSubNode = "mrumarks";
	public static boolean EOLalwaysLF = false;

	public static final VersionAndBuilt vnb = new VersionAndBuilt();

	public static BookshelfArray bookshelfArray = null;

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
	public static AaBookPanel aaBookPanel = null;
	public static AaHomeBtnPanel aaHomeBtnPanel = null;
	public static JMenu bookmarksMenu = null;

	public static BubblePanel[] bubblePanels = { null, null, null, null };
	public static DualDealListButtonsPanel dualDealListBtns = null;
	public static Book book = new Book();
	public static boolean newDealAsRequested = false; // only used by newMainDeal
	public static Deal deal = null;
	public static Deal deal_bk = null;

	public static Deal ddsDeal = null;
	public static Boolean ddsDealHandDispExamNumbWordsSuppress = false;
	public static DePointAy dePointAy = null;
	public static MassGi mg = null;
	public static boolean dealEnteredOnce = false;

	public static File[] lastDroppedList = null;

	public static boolean pbnAutoEnter = false;

	public static boolean show_poor_def_msg = false;

	public static LinChapter lastLoadedChapter = null;
	public static History history = new History();

	final public static int hist_max_display = 5; // do not set above 9
	final public static int mark_max_display = 10;
	public static MruCollection mruCollection = new MruCollection();

	public static Boolean runningInJar = false; // set at boot time
	public static String thisAppBaseFolder = ""; // set at boot time only one of these two can be set EXCEPT when using ghost jar
	public static String thisAppBaseJar = ""; // set at boot time only one of these two can be set EXCEPT when using ghost jar
	public static String thisAppBaseJarIncPath = "";
	public static String thisAppBaseJarIncPath_orig = "";

	public static boolean debug_using_ghost_jar = false; // set true by having _aaBridge_d__debug_using_ghost_jar.txt etc
	public static String java_info = "";
	public static JMenuItem menuItemPollDLF;
	public static boolean pollDownloadsFolder = true;
	public static boolean poller_boss = false;
	public static JCheckBoxMenuItem pollMenuItem;
	public static boolean cont_button_is_cont__cashed = true;
	public static String Books_E_renamed_to = "";
	public static JMenu books_B__menu = null;
	public static JMenu books_E__menu = null;
	public static JMenu books_S__menu = null;
	public static JMenu books_V__menu = null;
	public static JMenu books_Z__menu = null;
	public static JMenu lang_menu = null;
	public static JMenu help_menu = null;
	public static JMenuBar menuBar = null;

	final public static String ghost_jar = "aaBridge_ghost.jar"; // You set this here

	public static Hand ddsKeptHands[][] = new Hand[13][4];

	public static void clearDdsKept() {
		for (int i = 0; i < 13; i++) {
			for (int j = 0; j < 4; j++) {
				ddsKeptHands[i][j] = null;
			}
		}
	}

	public static void clearDdsKept_at(int n) {
		ddsKeptHands[n / 13][n % 4] = null;
	}

	public App() {
		clearDdsKept();
	}

}
