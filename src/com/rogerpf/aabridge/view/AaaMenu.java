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

package com.rogerpf.aabridge.view;

import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.prefs.Preferences;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

import com.rogerpf.aabridge.controller.Aaf;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.controller.Bookshelf;
import com.rogerpf.aabridge.controller.LangdeckList;
import com.rogerpf.aabridge.controller.LanguageList;
import com.rogerpf.aabridge.model.Cc;

public class AaaMenu {

	public static Action menuSaveStdAction;
	public static Action menuSaveAsAction;
	public static JMenuItem exampleMenuShowHide;
	public static JMenuItem theDotTest;

	public static JMenu makeFileMenu(ActionListener listener, String text) {
		// =============================================================
		JMenu menu = new JMenu(text);

		JMenuItem menuItem;

		// Open
		menuItem = new JMenuItem(Aaf.menuFile_open_D, KeyEvent.VK_O);
		menuItem.setActionCommand("menuOpen");
		menuItem.addActionListener(App.con);
		menu.add(menuItem);

		// Save Std
		menuSaveStdAction = App.frame.new RpfMenuAction(Aaf.menuFile_save_D, "menuSaveStd", KeyEvent.VK_S);
		menuItem = new JMenuItem(menuSaveStdAction);
		menuItem.setAction(menuSaveStdAction);
		menuItem.addActionListener(App.con);
		menu.add(menuItem);

		// Save As
		menuSaveAsAction = App.frame.new RpfMenuAction(Aaf.menuFile_saveAs_D, "menuSaveAs", KeyEvent.VK_A);
		menuItem = new JMenuItem(menuSaveAsAction);
		menuItem.setAction(menuSaveAsAction);
		menuItem.addActionListener(App.con);
		menu.add(menuItem);

		menu.addSeparator();

		// Show aaBridgeBase Folder
		menuItem = new JMenuItem(Aaf.menuFile_openAAB_D);
		menuItem.setActionCommand("openaaBridgeBaseFolder");
		menuItem.addActionListener(App.con);
		menu.add(menuItem);

		// Show Saves Folder
		menuItem = new JMenuItem(Aaf.menuFile_openSF_D);
		menuItem.setActionCommand("openSavesFolder");
		menuItem.addActionListener(App.con);
		menu.add(menuItem);

		// Show temp_MyHands Folder
		menuItem = new JMenuItem(Aaf.menuFile_openTMF_D);
		menuItem.setActionCommand("openTempMyHandsFolder");
		menuItem.addActionListener(App.con);
		menu.add(menuItem);

		// Show temp_Other Folder
		menuItem = new JMenuItem(Aaf.menuFile_openTOF_D);
		menuItem.setActionCommand("openTempOtherFolder");
		menuItem.addActionListener(App.con);
		menu.add(menuItem);

		// Import Saves Folder
		menuItem = new JMenuItem(Aaf.menuFile_impSF_D);
		menuItem.setActionCommand("imp_SavesFolder");
		menuItem.addActionListener(App.con);
		menu.add(menuItem);

		// Import temp_MyHands Folder
		menuItem = new JMenuItem(Aaf.menuFile_impTMF_D);
		menuItem.setActionCommand("imp_TempMyHandsFolder");
		menuItem.addActionListener(App.con);
		menu.add(menuItem);

		// Import temp_Other Folder
		menuItem = new JMenuItem(Aaf.menuFile_impTOF_D);
		menuItem.setActionCommand("imp_TempOtherFolder");
		menuItem.addActionListener(App.con);
		menu.add(menuItem);

		menu.addSeparator();

		// Read-in HEADING  BBO Hands 
		menuItem = new JMenuItem("" + Aaf.gT("menuFile.headingBBO"));
		menuItem.setForeground(Cc.GreenStrong);
		Font slightlyBiggerFont1 = menuItem.getFont().deriveFont(menuItem.getFont().getSize() * 1.15f);
		menuItem.setFont(slightlyBiggerFont1);
		menuItem.setActionCommand("openPage_BBOHandsToaaBridge");
		menuItem.addActionListener(App.con);
		menu.add(menuItem);

		// Open Downloads Folder
		menuItem = new JMenuItem(Aaf.menuFile_openDLF_D);
		menuItem.setActionCommand("openDownloadsFolder");
		menuItem.addActionListener(App.con);
		menu.add(menuItem);

		// Checkbox item to poll Downloads folder
		JCheckBoxMenuItem cbmenuItem = new JCheckBoxMenuItem(Aaf.gT("menuFile.pollDLF"));
		App.menuItemPollDLF = cbmenuItem;
		Font slightlyBiggerFont2 = cbmenuItem.getFont().deriveFont(cbmenuItem.getFont().getSize() * 1.15f);
		cbmenuItem.setFont(slightlyBiggerFont2);
		cbmenuItem.setActionCommand("execute_pollDLF_toggled");
		cbmenuItem.addActionListener(App.con);
		cbmenuItem.setSelected(App.pollDownloadsFolder);
		App.pollMenuItem = cbmenuItem;
		menu.add(cbmenuItem);

		// Execute extract hands from tempMyHands
		menuItem = new JMenuItem(Aaf.menuFile_processMH);
		menuItem.setActionCommand("execute_extract_lins_from_latest_html");
		menuItem.addActionListener(App.con);
		menu.add(menuItem);

		// Execute extract hands from tempMyHands - part 2
		menuItem = new JMenuItem("" + Aaf.gT("menuFile.processMH_Green"));
		menuItem.setForeground(Cc.GreenStrong);
		menuItem.setActionCommand("execute_extract_lins_from_latest_html");
		menuItem.addActionListener(App.con);
		menu.add(menuItem);

		menu.addSeparator();

		// Paste - Accepts a Paste (normally of a tiny url)
		menuItem = new JMenuItem(Aaf.menuFile_paste_D, KeyEvent.VK_P);
		menuItem.setActionCommand("acceptPaste");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

		// Exit
		menuItem = new JMenuItem(Aaf.menuFile_exit_D);
		menuItem.setActionCommand("exit");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

		return menu;
	}

	public static JMenu makeIdealSizeMenu(ActionListener listener, String text) {
		// =============================================================
		JMenu menu = new JMenu(text);

		JMenuItem menuItem;

		menuItem = new JMenuItem("1     " + Aaf.gT("menuSize.1"), KeyEvent.VK_1);
		menuItem.setActionCommand("set_size_1");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

		menuItem = new JMenuItem("2     " + Aaf.gT("menuSize.2"), KeyEvent.VK_2);
		menuItem.setActionCommand("set_size_2");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

		menu.addSeparator();

		menuItem = new JMenuItem("3     " + Aaf.gT("menuSize.3"), KeyEvent.VK_3);
		menuItem.setActionCommand("set_size_3");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

		menuItem = new JMenuItem("4     " + Aaf.gT("menuSize.4"), KeyEvent.VK_4);
		menuItem.setActionCommand("set_size_4");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

		menu.addSeparator();

		menuItem = new JMenuItem("5     " + Aaf.gT("menuSize.5"), KeyEvent.VK_5);
		menuItem.setActionCommand("set_size_5");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

		menuItem = new JMenuItem("6     " + Aaf.gT("menuSize.6"), KeyEvent.VK_6);
		menuItem.setActionCommand("set_size_6");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

		menu.addSeparator();

		menuItem = new JMenuItem("7     " + Aaf.gT("menuSize.7"), KeyEvent.VK_7);
		menuItem.setActionCommand("set_size_7");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

		menuItem = new JMenuItem("8     " + Aaf.gT("menuSize.8"), KeyEvent.VK_8);
		menuItem.setActionCommand("set_size_8");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

		menu.addSeparator();

		menuItem = new JMenuItem("9     " + Aaf.gT("menuSize.9"), KeyEvent.VK_9);
		menuItem.setActionCommand("set_size_9");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

		menuItem = new JMenuItem("10   " + Aaf.gT("menuSize.0"), KeyEvent.VK_0);
		menuItem.setActionCommand("set_size_0");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

		menu.addSeparator();

		menuItem = new JMenuItem("A     " + Aaf.gT("menuSize.ABC") + "  A", KeyEvent.VK_A);
		menuItem.setActionCommand("set_size_user_A");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

		menuItem = new JMenuItem("B     " + Aaf.gT("menuSize.ABC") + "  B", KeyEvent.VK_B);
		menuItem.setActionCommand("set_size_user_B");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

		menuItem = new JMenuItem("C     " + Aaf.gT("menuSize.ABC") + "  C", KeyEvent.VK_C);
		menuItem.setActionCommand("set_size_user_C");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

		return menu;
	}

	public static JMenu makeOptionsMenu(ActionListener listener, String text) {
		// =============================================================
		JMenu menu = new JMenu(text);

		JMenuItem menuItem;

		// Right Panel - Prefs 7 ShowBtns
		menuItem = new JMenuItem(Aaf.menuOpt_show_D, KeyEvent.VK_W);
		menuItem.setActionCommand("rightPanelPrefs7_ShowBtns");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

		// Right Panel - Prefs 6 RedHints
		menuItem = new JMenuItem(Aaf.menuOpt_red_D, KeyEvent.VK_R);
		menuItem.setActionCommand("rightPanelPrefs6_RedHints");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

		// Right Panel - Prefs 5 DSizeFont
		menuItem = new JMenuItem(Aaf.menuOpt_size_D, KeyEvent.VK_F);
		menuItem.setActionCommand("rightPanelPrefs5_DSizeFont");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

		// Right Panel - Prefs 4 SuitColors
		menuItem = new JMenuItem(Aaf.menuOpt_colors_D, KeyEvent.VK_C);
		menuItem.setActionCommand("rightPanelPrefs4_SuitColors");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

		// Right Panel - Prefs 3 DFC
		menuItem = new JMenuItem(Aaf.menuOpt_dfc_D, KeyEvent.VK_D);
		menuItem.setActionCommand("rightPanelPrefs3_DFC");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

		// Right Panel - Prefs 2 KibSeat
		menuItem = new JMenuItem(Aaf.menuOpt_seat_D, KeyEvent.VK_S);
		menuItem.setActionCommand("rightPanelPrefs2_KibSeat");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

		// Right Panel - Prefs 1 AutoPlay
		menuItem = new JMenuItem(Aaf.menuOpt_autoPlay_D, KeyEvent.VK_A);
		menuItem.setActionCommand("rightPanelPrefs1_AutoPlay");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

		// Right Panel - Prefs 0 DealChoices
		menuItem = new JMenuItem(Aaf.menuOpt_newDeals_D, KeyEvent.VK_N);
		menuItem.setActionCommand("rightPanelPrefs0_NewDealChoices");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

		menu.addSeparator();

		// Bottom Panel
		menuItem = new JMenuItem(Aaf.gT("menuOpt.bottom"), KeyEvent.VK_P);
		menuItem.setActionCommand("lowerPanel");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

		return menu;
	}

	public static JMenu makeLangMenu(ItemListener listener, String text) {
		// =============================================================
		JMenu menu = new JMenu(text);

		JMenuItem menuItem;

		exampleMenuShowHide = menuItem = new JCheckBoxMenuItem(Aaf.gT("menuLang.showExampleMenu"), App.showBooksZMenu);
		menuItem.addItemListener(listener);
		menu.add(menuItem);

		menu.addSeparator();

		Font slightlyBiggerFont = menuItem.getFont().deriveFont(menuItem.getFont().getSize() * App.menuInfoSize);

		menuItem = new JMenuItem(Aaf.gT("menuLang.menuIsDemo"));
		menuItem.setFont(slightlyBiggerFont);
		menuItem.setActionCommand("localize-how-to");
		menuItem.setForeground(Cc.GreenStrong);
		menuItem.addActionListener((ActionListener) listener);
		menu.add(menuItem);

		menuItem = new JMenuItem(Aaf.gT("menuLang.localizeHowTo"));
		menuItem.setFont(slightlyBiggerFont);
		menuItem.setActionCommand("localize-how-to");
		menuItem.setForeground(Cc.RedStrong);
		menuItem.addActionListener((ActionListener) listener);
		menu.add(menuItem);

		menu.addSeparator();

		ButtonGroup groupLang = new ButtonGroup();
		JMenu menuOther = null;
		;

		int ind = -1;
		for (LanguageList.LangEntry entry : Aaf.langList) {
			JMenu menuAddTo;
			ind++;

			if (ind >= App.menu2_ind) {
				if (ind == App.menu2_ind) {
					menuOther = new JMenu("Other Languages");
					menuOther.setFont(slightlyBiggerFont);
					menu.add(menuOther);
				}
				menuAddTo = menuOther;
			}
			else {
				menuAddTo = menu;
			}

			menuItem = new JRadioButtonMenuItem(entry.menuText);
			entry.setMenuItem(menuItem);
			menuItem.addItemListener(listener);
			groupLang.add(menuItem);
			menuAddTo.add(menuItem);
			if (entry.iso_lang.contentEquals(Aaf.iso_lang_req)) {
				menuItem.setSelected(true);
			}
		}

		menu.addSeparator();

		theDotTest = menuItem = new JCheckBoxMenuItem(Aaf.gT("menuLang.dotTest"), App.showTheDotTest);
		menuItem.addItemListener(listener);
		menu.add(menuItem);

		menuItem = new JMenuItem(Aaf.gT("menuLang.closeAfterChange"));
		menuItem.setFont(menuItem.getFont().deriveFont(menuItem.getFont().getSize() * App.menuInfoSize));
		menuItem.setForeground(Cc.GreenStrong);
		menuItem.addActionListener((ActionListener) listener);
		menu.add(menuItem);

		menu.addSeparator();

		ButtonGroup groupRank = new ButtonGroup();

		for (LangdeckList.LangEntry entry : Aaf.rankList) {
			menuItem = new JRadioButtonMenuItem(entry.menuText);
			entry.setMenuItem(menuItem);
			menuItem.addItemListener(listener);
			groupRank.add(menuItem);
			menu.add(menuItem);
			if (entry.iso_lang.contentEquals(Aaf.iso_deck_lang)) {
				menuItem.setSelected(true);
			}
		}

		return menu;
	}

	public static JMenu makeHelpMenu(ActionListener listener, String menuBarText) {
		// =============================================================

		JMenu menu = new JMenu(menuBarText);
		JMenuItem menuItem = new JMenuItem(); // the first one is only used as a get font method

		int firstPartLen = Aaf.getMetricsLength(menuItem, Aaf.helpMenu_howdoI) + App.menuTabExtra;
		if (firstPartLen < 60)
			firstPartLen = 60;

		String web = "(" + Aaf.gT("helpMenu.web") + ")";
		String courses = Aaf.gT("helpMenu.courses");
		String videos = Aaf.gT("helpMenu.videos");

		String ocp = "OCP";

		// OCP QuickTables
		menuItem = new JMenuItem(Aaf.spacedOut(menuItem, ocp, firstPartLen) + Aaf.gT("helpMenu.ocpTables") + "           " + web);
		menuItem.setActionCommand("web_OCPQuickTables");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

		// OCP_resources
		menuItem = new JMenuItem(Aaf.spacedOut(menuItem, ocp, firstPartLen) + Aaf.gT("helpMenu.ocpResources") + "               " + web);
		menuItem.setActionCommand("web_OCPResources");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

		// OCP Video Lessons
		menuItem = new JMenuItem(Aaf.spacedOut(menuItem, ocp, firstPartLen) + Aaf.gT("helpMenu.ocpVideos"));
		menuItem.setActionCommand("web_OCPVideos");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

		// OCP Lesson in aaBridge
		menuItem = new JMenuItem(Aaf.spacedOut(menuItem, ocp, firstPartLen) + Aaf.gT("helpMenu.ocpaab"));
		menuItem.setActionCommand("openPage_OCP_intro");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

		// Down the rabbit Hole
		menuItem = new JMenuItem(Aaf.spacedOut(menuItem, "", firstPartLen) + Aaf.gT("helpMenu.rabbitHole"));
		menuItem.setActionCommand("openPage_DownRabbitHole");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

		// doc collection
		menuItem = new JMenuItem(Aaf.spacedOut(menuItem, "", firstPartLen) + Aaf.gT("helpMenu.docCollection"));
		menuItem.setActionCommand("openPage_DocCollection");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

		// Memorize suit distributions
		menuItem = new JMenuItem(Aaf.spacedOut(menuItem, "", firstPartLen) + Aaf.gT("helpMenu.memorize"));
		menuItem.setActionCommand("openPage_MemorizeDistributions");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

		// Enter (type) hands into aaBridge
		menuItem = new JMenuItem(Aaf.spacedOut(menuItem, "", firstPartLen) + Aaf.gT("helpMenu.enterHands"));
		menuItem.setActionCommand("openPage_KeyDocuments");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

		// Play Deal in aaBridge
		menuItem = new JMenuItem(Aaf.spacedOut(menuItem, "", firstPartLen) + Aaf.gT("helpMenu.playDealInaab"));
		menuItem.setActionCommand("openPage_Play_Kib_DDS");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

		// Courses
		menuItem = new JMenuItem(Aaf.spacedOut(menuItem, courses, firstPartLen) + Aaf.gT("helpMenu.coursesInaaB"));
		menuItem.setActionCommand("openPage_Courses");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

		// aaBridge Videos
		menuItem = new JMenuItem(Aaf.spacedOut(menuItem, videos, firstPartLen) + Aaf.gT("helpMenu.aabVids"));
		menuItem.setActionCommand("youtube_aaBridgeVideos");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

		// BBO to aaBridge
		menuItem = new JMenuItem(Aaf.spacedOut(menuItem, "", firstPartLen) + Aaf.gT("helpMenu.bboToaaBridge"));
		menuItem.setActionCommand("openPage_bboHandsToaaBridge");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

		// Save n Send to BBO
		menuItem = new JMenuItem(Aaf.spacedOut(menuItem, "", firstPartLen) + Aaf.gT("helpMenu.aaBridgeToBbo"));
		menuItem.setActionCommand("openPage_Save_n_Send");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

		// BBO to aaBridge
		menuItem = new JMenuItem(Aaf.spacedOut(menuItem, "", firstPartLen) + Aaf.gT("helpMenu.bboMonthsHands"));
		menuItem.setActionCommand("openPage_BBO_to_aaBridge");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

		// Rotate hands
		menuItem = new JMenuItem(Aaf.spacedOut(menuItem, "", firstPartLen) + Aaf.gT("helpMenu.rotateHands"));
		menuItem.setActionCommand("openPage_Practice");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

		// use the DDS
		menuItem = new JMenuItem(Aaf.spacedOut(menuItem, "", firstPartLen) + Aaf.gT("helpMenu.useTheDDS"));
		menuItem.setActionCommand("openPage_Play_Kib_DDS");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

		// Defend like an expert
		menuItem = new JMenuItem(Aaf.spacedOut(menuItem, Aaf.helpMenu_howdoI, firstPartLen) + Aaf.gT("helpMenu.dlae"));
		menuItem.setActionCommand("openPage_DefendExpert");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

		// Lin Files a Starter
		menuItem = new JMenuItem(Aaf.spacedOut(menuItem, "", firstPartLen) + Aaf.gT("helpMenu.linFilesStart"));
		menuItem.setActionCommand("openPage_LinFilesAStarter");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

		// Key Documents
		menuItem = new JMenuItem(Aaf.spacedOut(menuItem, "", firstPartLen) + Aaf.gT("helpMenu.useDealerScripts"));
		menuItem.setActionCommand("openPage_KeyDocuments");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

		// fonts to small
		menuItem = new JMenuItem(Aaf.spacedOut(menuItem, Aaf.helpMenu_howdoI, firstPartLen) + Aaf.gT("helpMenu.fontsTooSmall"));
		menuItem.setActionCommand("youtube_FontsTooSmall");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

		// localize
		menuItem = new JMenuItem(Aaf.spacedOut(menuItem, "", firstPartLen) + Aaf.gT("helpMenu.localize"));
		menuItem.setActionCommand("localize-how-to");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

//		menu.addSeparator();

//		menuItem = new JMenuItem(Aaf.spacedOut(menuItem, "", (int)((firstPartLen * 10) / (App.menuInfoSize * 12))) + Aaf.menuCmn_whatGoesOn + " " + Aaf.menuCmn_inside);
//		Font slightlyBiggerFont = menuItem.getFont().deriveFont(menuItem.getFont().getSize() * App.menuInfoSize);
//		menuItem.setFont(slightlyBiggerFont);
//		menuItem.setForeground(Cc.RedStrong);
//		Bookshelf firstShelf = App.bookshelfArray.get(0);
//		menuItem.setActionCommand(book.getMenuKey(firstShelf.basePath));
//		menu.add(menuItem);

		Bookshelf.addFirstShelf_90s_toMenu(listener, menu);

		menu.addSeparator();

		// Help LookAtWebsite
		menuItem = new JMenuItem(Aaf.spacedOut(menuItem, Aaf.gT("helpMenu.website"), firstPartLen) + Aaf.gT("helpMenu.aaBWeb"));
		menuItem.setForeground(Cc.GreenStrong);
		menuItem.setActionCommand("web_LookAtWebsite");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

		// Help aaBridge Archive
		// String arc = Aaf.gT("helpMenu.archive");
		String relArcOn = Aaf.gT("helpMenu.relArcOn");
		menuItem = new JMenuItem(Aaf.spacedOut(menuItem, "", firstPartLen) + "aaBridge  " + relArcOn + "  Google Drive");
		menuItem.setActionCommand("web_ArchiveGoogleDrive");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

		// aaBridge-announcements
		menuItem = new JMenuItem(Aaf.spacedOut(menuItem, "", firstPartLen) + Aaf.gT("helpMenu.notify"));
		menuItem.setActionCommand("web_aaBridgeAnnouncements");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

		// aaBridge-usersgroup
		menuItem = new JMenuItem(Aaf.spacedOut(menuItem, "", firstPartLen) + Aaf.gT("helpMenu.usersGroup"));
		menuItem.setActionCommand("web_aaBridgeUsersGroup");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

		// Help About
		menuItem = new JMenuItem(Aaf.spacedOut(menuItem, Aaf.gT("helpMenu.about"), firstPartLen) + "aaBridge");
		menuItem.setActionCommand("internal_HelpAbout");
		menuItem.addActionListener(listener);
		menu.add(menuItem);

		return menu;
	}

	public static final int O_STD = 128;
	public static final int L_STD = 124;

	public static void checkAndSetMininumOptionsPanelSize() {
		// ==============================================================================================

		int frameWidth = App.frame.getWidth();
		int right_panel_width = frameWidth - App.frame.splitPaneHorz.getDividerLocation();

		if (right_panel_width < O_STD) {
			App.frame.splitPaneHorz.setDividerLocation(frameWidth - AaaMenu.O_STD);
		}

	}

	final static String posPrefsNode = "com.rogerpf.aabridge-userpos-v1/pos";

	final static String posVer = "v1";

	public static void writeUserSizeEntry(String letter, boolean forceRestorePos) {
		// ==============================================================================================

		try {
			Preferences prefs = Preferences.userRoot().node(posPrefsNode);

			// @formatter:off
			String s =    posVer 
					    + ",  "   + App.frameLocationX  + ", " +  App.frameLocationY
					    + ",  "   + (forceRestorePos ? "y" : "n") 
					    + ",  "   + App.frameWidth      + ", " +  App.frameHeight
					    + ",  "   + (App.frame.getExtendedState() & java.awt.Frame.MAXIMIZED_BOTH)
					    + ",  "   + App.frame.splitPaneHorz.getDividerLocation()
					    + ", "    + App.frame.splitPaneVert.getDividerLocation();
			// @formatter:on

			// System.out.println("out: " + letter + " - " + s);

			prefs.put(letter, s);
			prefs.flush();

		} catch (Exception e) {
		}
	}

	static int POS_X = 1;
	static int POS_Y = 2;
	static int RESTPOS = 3;
	static int WIDTH = 4;
	static int HEIGHT = 5;
	static int MAXI = 6;
	static int HORZ = 7;
	static int VERT = 8;

	public static void actionUserSizeEntry(String letter) {
		// ==============================================================================================

		try {

			Preferences prefs = Preferences.userRoot().node(posPrefsNode);

			String s = prefs.get(letter, "");

			// System.out.println("in:  " + letter + " - " + s);

			String in[] = s.split("\\s*,\\s*");

			if (in.length < 8) {
				throw new Exception();
			}

			if (in[RESTPOS].contentEquals("y")) {
				App.frame.setLocation(Integer.parseInt(in[POS_X]), Integer.parseInt(in[POS_Y]));
			}

			int curWidth = App.frame.getWidth();
			int curHeight = App.frame.getHeight();

			App.frameWidth = Integer.parseInt(in[WIDTH]);
			App.frameHeight = Integer.parseInt(in[HEIGHT]);

			if (App.validateWindowPosition()) {
				App.frame.setSize(App.frameWidth, App.frameHeight);
				return;
			}

			App.horzDividerLocation = Integer.parseInt(in[HORZ]);
			App.vertDividerLocation = Integer.parseInt(in[VERT]);

			if (curWidth == App.frameWidth && curHeight == App.frameHeight) {
				// so there is no size to change so we move the divders now
				App.frame.splitPaneHorz.setDividerLocation(App.horzDividerLocation);
				App.frame.splitPaneVert.setDividerLocation(App.vertDividerLocation);
			}
			else {
				App.frameDividersChangeWanted = true; // actioned where and when the resize is detected
				App.frame.setSize(App.frameWidth, App.frameHeight);
			}

			App.frame.setExtendedState(Integer.parseInt(in[MAXI]));

			App.frame.invalidate();

		} catch (Exception e) {
			actionSetSize(4);
		}
	}

	public static void actionSetSize(int i) {
		// ==============================================================================================

		ASize aSize = table[i];

		int macLinuxWidthCorrect = (App.onMacOrLinux ? 40 : 0); // their fonts are wider sometimes. This attempts to allow for it.

		App.frame.splitPaneHorz.setDividerLocation(App.frame.getWidth() - aSize.widthOpts);
		App.frame.splitPaneVert.setDividerLocation(App.frame.getHeight() - aSize.heightLower);
		Rectangle r = App.frame.getBounds();
		r.width = aSize.width + macLinuxWidthCorrect;
		r.height = aSize.height; // + additionalDepth34;
		App.frame.setBounds(r);
		return;
	}

	static class ASize {
		// ---------------------------------- CLASS -------------------------------------
		int index;
		int width;
		int height;
		int widthOpts;
		int heightLower;

		// @formatter:off	
		ASize( int index_v, int width_v, int height_v, int widthOpts_v, int heightLower_v) {
			index = index_v;  width = width_v;  height = height_v;  widthOpts = widthOpts_v;  heightLower = heightLower_v;
		}
		//@formatter:on
	}

	//@formatter:off
	final static ASize[] table = { 
	  //                  width               height          wOpts    hLower
		new ASize( 0,   0               ,   0              ,      0,        0                 ),
		new ASize( 1,   1176 -  66      ,   874 -  90      ,      0,        0                 ),
		new ASize( 2,   1176 -  66 + 100,   874 -  90 +  42,  O_STD,    L_STD - 2             ),
		new ASize( 3,   1176 +   0      ,   874 +   0      ,      0,        0                 ),
		new ASize( 4,   1176 +   0 + 100,   874 +   0 +  42,  O_STD,    L_STD                 ),
		new ASize( 5,   1176 +  66      ,   874 +  40      ,      0,        0                 ),
		new ASize( 6,   1176 +  66 + 100,   874 +  40 +  42,  O_STD,    L_STD                 ),
		new ASize( 7,   1176 + 116      ,   874 +  80      ,      0,        0                 ),
		new ASize( 8,   1176 + 116 + 100,   874 +  80 +  42,  O_STD,    L_STD                 ),
		new ASize( 9,   1176 + 176      ,   874 + 120      ,      0,        0                 ),
	    new ASize(10,   1176 + 176 + 100,   874 + 120 +  42,  O_STD,    L_STD                 ),
	};
	//@formatter:on

}
