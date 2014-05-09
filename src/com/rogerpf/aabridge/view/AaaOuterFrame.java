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

import java.awt.AWTEvent;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.Timer;
import javax.swing.TransferHandler;

import net.miginfocom.swing.MigLayout;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.controller.Book;
import com.rogerpf.aabridge.controller.Book.LinChapter;
import com.rogerpf.aabridge.controller.BridgeLoader;
import com.rogerpf.aabridge.controller.CmdHandler;
import com.rogerpf.aabridge.igf.BubblePanel;
import com.rogerpf.aabridge.igf.MassGi;
import com.rogerpf.aabridge.igf.TutNavigationBar;
import com.rogerpf.aabridge.igf.TutorialPanel;
import com.rogerpf.aabridge.model.Deal;
import com.rogerpf.aabridge.model.Dir;
import com.rogerpf.aabridge.model.Lin;
import com.rpsd.ratiolayout.AspectBoundable;
import com.rpsd.ratiolayout.PreferredSizeGridLayout;
import com.version.VersionAndBuilt;

/**    
 */
public class AaaOuterFrame extends JFrame implements ComponentListener, ActionListener, WindowListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AaLowerOptionsPanel lop;
	public AaRightOptionsPanel rop;
	public AaFixedRatioPanel fixedRatioPanel;
	AaPayloadPanel payloadPanel;
	AaPayloadCasePanel plcp;
	DarkGrayHiddenPanel rjp; // rightJigglePanel
	DarkGrayHiddenPanel bjp; // bottomJigglePanel

	public JPanel rrp; // rightRattlePanel
	public JPanel brp; // bottomRattlePanel

	AaDdlAndPayloadCasePanel linPlcp;

	public JSplitPane splitPaneHorz;
	public JSplitPane splitPaneVert;

	JMenuBar menuBar;
	JMenu menu, submenu;
	JMenuItem menuItem;

	private Action menuSaveStdAction;
	private Action menuSaveAsAction;

	KeyboardFocusManager kbFocusManager;

	public AaDragGlassPane aaDragGlassPane;

	// ----------------------------------------
	public AaaOuterFrame() { /* Constructor */

		App.frame = this; // I know this is pre setting it *before* it is constructed - but we want access to our OWN constants

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		String sepFname = File.separator + "_aaBridge_dev.txt";

		// @formatter:off
		App.devMode = (new File(sepFname)).exists() 
				   || (new File(File.separator + "a" + sepFname)).exists()
			       || (new File(File.separator + "programSmall" + File.separator + "aaBridge" + sepFname)).exists();
		// @formatter:on

		setVisible(false); // set true by the timer below
		java.net.URL imageFileURL = AaaOuterFrame.class.getResource("aaBridge_proto_icon.png");
		setIconImage(Toolkit.getDefaultToolkit().createImage(imageFileURL));

		this.addWindowListener(this);
		this.addComponentListener(this);

		App.loadPreferences();

		App.selectMnHeaderColor();

		/** The 'donehand' has already been constructed. Now we create the mg
		 *  to match it.  From now on there will ALWAYS be a valid mg (and lin inside it)
		 */

		App.deal = new Deal(Deal.makeDoneHand, Dir.South);
		/** The 'donehand' has just been constructed. Now we create the mg
		 *  to match it.  From now on there will ALWAYS be a valid mg (and lin inside it)
		 */
		App.mg = new MassGi(App.deal);

		aaDragGlassPane = new AaDragGlassPane(this);
		setGlassPane(aaDragGlassPane);

		App.ourBookshelf.fillWithBooks("");

		App.singleBookOnly = App.ourBookshelf.isDefaultToSingleBook();

		if (App.startedWithCleanSettings) {
			App.startedWithCleanSettings = false;
			// As this is a clean start we accept the singleBookSetting
			App.multiBookDisplay = !App.singleBookOnly;
		}
		else {
			// As this is a subsequent start we allow previous selction of mulitbook
			// but force it to true if we are in a genuine multbook jar
			if (App.multiBookDisplay == false && App.singleBookOnly == false)
				App.multiBookDisplay = true;

			// and force the single book to be the invert of multibook
			App.singleBookOnly = !App.multiBookDisplay;
		}

//		if (App.devMode)
//			App.singleBookOnly = false;

//		if (App.singleBookOnly)
//			App.startUpOption = App.startUp_1__book;

		createAndAddAllMenus(0);

		// -----------------------------------------------------

		// Make the inner a fixed ratio (AspectBoundable) so creating the middle panel

		calcAllMigLayoutStrings();

		PreferredSizeGridLayout psgl = new PreferredSizeGridLayout(1, 1); // real ratio calculated and set later
		psgl.setBoundableInterface(new AspectBoundable());
		payloadPanel = new AaPayloadPanel();
		payloadPanel.setLayout(psgl);

		fixedRatioPanel = new AaFixedRatioPanel();
		payloadPanel.add(fixedRatioPanel);

		fixedRatioPanel.setLayout(new MigLayout(App.simple + ", flowy", "", "")); // real one set later

		for (Dir dir : Dir.nesw) {
			App.bubblePanels[dir.v] = new BubblePanel(dir);
		}

		App.gbp = new GreenBaizePanel();
		App.gbo = new GreenBaizeOverlay(this);
		App.gbm = new GreenBaizeMerged();
		App.gbr = new GreenBaizeRigid();
		App.ptp = new PhoneyTutorialPanel();
		App.tup = new TutorialPanel();
		App.ccb = new CommonCmdBar();
		App.tnb = new TutNavigationBar();
		App.dnb = new DealNavigationBar();

		App.bookPanel = new AaBookPanel();
		App.dualDealListBtns = new DualDealListButtonsPanel(); // here because it is hidden by setVisualMode()

		setVisualMode(App.Vm_InsideADeal);

		rjp = new DarkGrayHiddenPanel();
		bjp = new DarkGrayHiddenPanel();

		plcp = new AaPayloadCasePanel();

		plcp.setLayout(new MigLayout(App.simple, "[grow][]", "[grow][]"));
		plcp.add(payloadPanel, "growx, growy");
		plcp.add(rjp, "hidemode 2, growy, wrap");
		plcp.add(bjp, "hidemode 2, growx, spanx 2");

		rrp = new DarkGrayHiddenPanel();
		brp = new DarkGrayHiddenPanel();

		linPlcp = new AaDdlAndPayloadCasePanel();

		linPlcp.setLayout(new MigLayout(App.simple, "[][][grow][]", "[grow][]"));
		linPlcp.add(App.bookPanel, "hidemode 1, growy, center, hmin 0");
		linPlcp.add(App.dualDealListBtns, "hidemode 1, growy, center, hmin 0");
		linPlcp.add(plcp, "growx, growy");
		linPlcp.add(rrp, "hidemode 2, growy, wrap");
		linPlcp.add(brp, "hidemode 2, growx, spanx 3");

		rop = new AaRightOptionsPanel();
		lop = new AaLowerOptionsPanel();
		rop.setMinimumSize(new Dimension(0, 0));
		lop.setMinimumSize(new Dimension(0, 0));

		// Create a split pane with the two scroll panes in it.
		splitPaneVert = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, linPlcp, lop);
		splitPaneVert.setOneTouchExpandable(true);
		splitPaneVert.setResizeWeight(1.0);

		// Create a split pane with the two scroll panes in it.
		splitPaneHorz = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, splitPaneVert, rop);
		splitPaneHorz.setOneTouchExpandable(true);
		splitPaneHorz.setResizeWeight(1.0);

		getContentPane().add(splitPaneHorz);

		kbFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		kbFocusManager.addKeyEventDispatcher(App.con);

		String appHomePath = System.getProperty("user.home") + File.separator + ".aaBridge" + File.separator;
		App.autoSavesPath = appHomePath + "autosaves" + File.separator;
		App.savesPath = appHomePath + "saves" + File.separator;

		File appHome = new File(appHomePath);
		File autoSaves = new File(App.autoSavesPath);
		File saves = new File(App.savesPath);
		// tests are in with the books

		appHome.mkdir();
		autoSaves.mkdir();
		saves.mkdir();

		clearOutOldFilesFromFolder(App.autoSavesPath, 7);

		setTitleAsRequired();

		App.con.postContructionInitTimer.start();

		setTransferHandler(handler);
	}

	// @formatter:off
	String layOut_columns__gbo = " g+L  [34.3%]    31.5%    [34.2%]   R+g";
	String layOut_columns__gbp = "g [L] [33.33%]  [33.33%]  [33.33%] [R] g";
	
	String layOut_rowsA__gbo   = "[33%]    34%    [33%]"; // used twice (c0, c2)
	String layOut_rowsA__gbp   = "[31.5%] [37%]   [31.5%]"; // used twice (c0, c2)
	String layOut_rowsB__gbp   = "[37%]   [26%]   [37%]";  
	
	
	String layOut_columns__gbr = "";
	String layOut_rows__gbr   = "";  


	String layOut_rows__tutorialOnly;
	String layOut_rows__handAndTutorial;
	// @formatter:on

	Dimension fixedRatioPanel_RATIO = new Dimension(1, 1); // calculated later

	/**
	*/
	void calcAllMigLayoutStrings() {
		// =============================================================

		/**
		 * Visual Mode   =    Vm_DealAndTutorial
		 * 
		 * This is the simple case as we know the height of the command bars and can take
		 */
		float gap_gray_top = 0.5f;
		float gap_gray_bottom = 1.0f;

		float tutFullHeightPc = 100 - (gap_gray_top + App.CMD_BAR_PERCENT + App.NAV_BAR_PERCENT + gap_gray_bottom);

		// @formatter:off
		layOut_rows__tutorialOnly = String.format(Locale.US, "%.2f%%[%.2f%%][%.2f%%][%.2f%%]%.2f%%", 
				gap_gray_top, 
					tutFullHeightPc, 
					App.CMD_BAR_PERCENT, 
					App.NAV_BAR_PERCENT,
				gap_gray_bottom
				);
		// @formatter:on

		float tut_widthHeightRatio = TutorialPanel.LIN_STANDARD_WIDTH / TutorialPanel.LIN_STANDARD_HEIGHT;

		/** this is  THE RATIO  that keeps the payload the correct shape
		 */
		// 1.5 is a fudge factor to allow for the extra gray thin width marigns
		fixedRatioPanel_RATIO.width = (int) (10 * (tutFullHeightPc * tut_widthHeightRatio + 1.5f));
		fixedRatioPanel_RATIO.height = (int) 10 * 100;

		/**
		 * full_tut_height / full_tut_width  = full tut ratio
		 * rgb_height / rgb_width            =  rbg ratio
		 * 
		 * as the width are equal we can calc the percentage of the split tutorial area
		*/
		float reduced_over_standard = TutorialPanel.LIN_REDUCED_HEIGHT / TutorialPanel.LIN_STANDARD_HEIGHT;

		float tutMini_asPc_of_total = tutFullHeightPc * reduced_over_standard;
		float gbr_H_asPc_of_total = tutFullHeightPc - tutMini_asPc_of_total;

		// @formatter:off
		layOut_rows__handAndTutorial = String.format(Locale.US, "%.2f%%[%.2f%%][%.2f%%][%.2f%%][%.2f%%]%.2f%%", 
				gap_gray_top,
					gbr_H_asPc_of_total,
					tutMini_asPc_of_total, 
					App.CMD_BAR_PERCENT, 
					App.NAV_BAR_PERCENT,
				gap_gray_bottom
				);

		// @formatter:on

		/**
		 * Calculate the major column layout for the bgp
		 */

		float col = (100f - 2 * (App.GBP_SIDE_EDGE_GAP_PC + App.GBP_WING_PANEL_SIZE_PC)) / 3;

		/**
		 * Calc the main gbp layout including the intergral L & R wings and small green edge (whitch is a gap)
		 */
		layOut_columns__gbp = String.format(Locale.US,
			// @formatter:off
				"%.2f%%[%.2f%%][%.2f%%][%.2f%%][%.2f%%][%.2f%%]%.2f%%", 
				App.GBP_SIDE_EDGE_GAP_PC, App.GBP_WING_PANEL_SIZE_PC, col, col, col, App.GBP_WING_PANEL_SIZE_PC, App.GBP_SIDE_EDGE_GAP_PC);
			// @formatter:on

		/**
		 * Calc the  column layout that matches the above  
		 *  used in the gb0 => bubble panel OVERLAY 
		 */
		float side = App.GBP_SIDE_EDGE_GAP_PC + App.GBP_WING_PANEL_SIZE_PC;

		float gbpw_overlay_pc = (100f - 2 * side);

		float c0 = gbpw_overlay_pc * 0.343f;
		float c1 = gbpw_overlay_pc * 0.315f;
		float c2 = gbpw_overlay_pc * 0.342f;

		layOut_columns__gbo = String.format(Locale.US,
// @formatter:off
				"%.2f%%[%.2f%%]%.2f%%[%.2f%%]%.2f%%", 
				      side, c0, c1, c2, side);
			// @formatter:on

		// gbp and gbo are merged to form => gbm which is fed into gbr

		/**
		 * Calc the bgr layout (Side Space columns)  -  used to make gbr (which has a set ratio)
		 */

		if (App.tutorialDealSize < 0 || App.tutorialDealSize > 4)
			App.tutorialDealSize = 0;

		/**
		 *  It is possible to generate a formula to calculate nearly all the values needed 
		 * to generate the all four sizes.  But not only is this complicated but
		 * it does not allows for the discreet jumps in font size and the deal size changes
		 * hence the use of the imperical method below e.g. - by the fundgfactor
		 */

		final float LOW_GREEN_ROW[] = { 3.4f, 3.3f, 3f, 2.5f, 2f };
		final float SIDE_SPACE[] = { 10.5f, 8.4f, 5.3f, 2.7f, 0f }; // see tutorialDealSize
		final float FUDGE_FACTOR[] = { 230f, 150f, 130f, 70f, 0f }; // to hard to calculate

		// this is the only place where SIDE SPACE is used
		float sideSpacePc = SIDE_SPACE[App.tutorialDealSize];
		float lowGrRowHeightPc = LOW_GREEN_ROW[App.tutorialDealSize]; // percent of gbr !
		float fudgeFactor = FUDGE_FACTOR[App.tutorialDealSize];

		float gbmPc = 100f - 2 * (sideSpacePc);

		// @ formatter:off
		layOut_columns__gbr = String.format(Locale.US, "%.2f%%[%.2f%%]%.2f%%", sideSpacePc, gbmPc, sideSpacePc);
		// @ formatter:on

		float gbr_WidthHeightRatio = (App.GBP_CORE_SIMPLE_WIDTH + fudgeFactor) / App.GBP_CORE_SIMPLE_HEIGHT;

		float h = (1 / gbr_WidthHeightRatio) * (tut_widthHeightRatio * 100 - (2 * sideSpacePc)) / ((gbr_H_asPc_of_total) / (tutFullHeightPc));

		layOut_rows__gbr = String.format(Locale.US, "push[%.2f%%]%.2f%%", h, lowGrRowHeightPc);

		@SuppressWarnings("unused")
		int z = 0; // put your breakpoint here
	}

	/**
	*/
	public void setVisualMode(int vm) {
		// =============================================================

		App.bookPanel.setVisible(false);
		App.bookPanel.setCorrectWidth(App.dualDealListBtns.isVisible());
		App.gbr.setVisible(false);
		App.tup.setVisible(false);
		App.ccb.setVisible(false);
		App.tnb.setVisible(false);
		App.bookPanel.setVisible(true);

		fixedRatioPanel.removeAll();

		boolean resized = false;

		String raw = App.simple + ", flowy";

		String half_99_half = "0.5%[99%]0.5%";

		switch (vm) {

		case App.Vm_InsideADeal:
			fixedRatioPanel.setLayout(new MigLayout(raw, half_99_half, layOut_rows__handAndTutorial));
			fixedRatioPanel.setPreferredSize(fixedRatioPanel_RATIO); // Really sets the RATIO of the sides
			App.gbr.setSideSpaceSize();

			fixedRatioPanel.add(App.gbr);

			fixedRatioPanel.add(App.ptp);
			fixedRatioPanel.add(App.ccb);
			fixedRatioPanel.add(App.dnb);

			// resized = (App.visualMode != App.Vm_InsideADeal);
			break;

		case App.Vm_DealAndTutorial:
			fixedRatioPanel.setLayout(new MigLayout(raw, half_99_half, layOut_rows__handAndTutorial));
			fixedRatioPanel.setPreferredSize(fixedRatioPanel_RATIO); // Really sets the RATIO of the sides
			App.gbr.setSideSpaceSize();

			fixedRatioPanel.add(App.gbr);

			fixedRatioPanel.add(App.tup);
			fixedRatioPanel.add(App.ccb);
			fixedRatioPanel.add(App.tnb);

			// resized = shakeNeeded || !((App.visualMode == App.Vm_TutorialOnly) || (App.visualMode == App.Vm_DealAndTutorial));
			break;

		case App.Vm_TutorialOnly:
			fixedRatioPanel.setLayout(new MigLayout(raw, half_99_half, layOut_rows__tutorialOnly));
			fixedRatioPanel.setPreferredSize(fixedRatioPanel_RATIO); // Really sets the RATIO of the sides

			// fixedRatioPanel.add(App.gbr); NEVER this is tutorial only

			fixedRatioPanel.add(App.tup);
			fixedRatioPanel.add(App.ccb);
			fixedRatioPanel.add(App.tnb);

			resized = false;
			// shakeNeeded = !((App.visualMode == App.Vm_TutorialOnly) || (App.visualMode == App.Vm_DealAndTutorial));
			break;

		}
		App.gbr.setVisible(true);
		App.ccb.setVisible(true);
		App.tnb.setVisible(true);
		App.tup.setVisible(true);

		if (resized || shakeNeeded)
			payloadPanelShaker();

		App.visualMode = vm;

		boolean tutorial = App.isVmode_Tutorial();

		menuSaveStdAction.setEnabled(tutorial == false);
		menuSaveAsAction.setEnabled(tutorial == false);

	}

	static int resizeTicks = 0;
	static int resizeTicksTot = 6;

	static boolean shakeNeeded = false;

	/**
	*/
	public void payloadPanelShaker() {
		// =============================================================
		if (App.gbp.isVisible() == false) {
			shakeNeeded = true;
			// System.out.println("Shake Needed - set true");
			return;
		}

		if (afterPlpShakerTimer.isRunning())
			afterPlpShakerTimer.stop();

		App.gbp.hideClaimButtonsIfShowing();

		afterPlpShakerTimer.start();
		resizeTicks = resizeTicksTot;
		shakeNeeded = false; // cos we are doing it now
		// System.out.println("Shake Needed - cleared");
	}

	/**
	*/
	public Timer afterPlpShakerTimer = new Timer(100, new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			// =============================================================
			afterPlpShakerTimer.setDelay(10);
			if (resizeTicks == resizeTicksTot)
				App.gbp.kick();

			resizeTicks--;

			boolean odd = (resizeTicks % 2 == 1);

			if (odd && App.gbp.areAllThreeColumnsMatchedInSize(resizeTicks)) {
				afterPlpShakerTimer.stop();
				return;
			}

			rjp.setVisible(odd);
			bjp.setVisible(odd);

			if (resizeTicks <= 0 && !odd)
				afterPlpShakerTimer.stop();
		}
	});

	/**   
	 */
	class RpfMenuAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public RpfMenuAction(String text, String cmd_text, Integer mnemonic) {
			super(text);
			// putValue(SHORT_DESCRIPTION, text); // tooltip
			putValue(ACTION_COMMAND_KEY, cmd_text);
			putValue(MNEMONIC_KEY, mnemonic);
		}

		public void actionPerformed(ActionEvent e) {
			// not use we set our own listener
		}
	}

	/**   
	 */
	public void createAndAddAllMenus(int droppedBookshelfCount) {
		// ==================================================================
		// Remove the previous Menu bar if any
		setJMenuBar(null);

		// Create the menu bar.
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		// File - MENU
		menu = new JMenu("File       ");
		menu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(menu);

		if (App.FLAG_canOpen) {
			// Open
			menuItem = new JMenuItem("Open  a Saved Deal  -  you can instead just  'Drag and Drop'  the deal file", KeyEvent.VK_O);
			menuItem.setActionCommand("menuOpen");
			menuItem.addActionListener(App.con);
			menu.add(menuItem);
		}

		// Save Std
		menuSaveStdAction = new RpfMenuAction("Save                       -  Save using the file name you last set with 'Save As'", "menuSaveStd",
				KeyEvent.VK_S);
		menuItem = new JMenuItem(menuSaveStdAction);
		menuItem.setAction(menuSaveStdAction);
		menuItem.addActionListener(App.con);
		if (App.FLAG_canSave)
			menu.add(menuItem);

		// Save As
		menuSaveAsAction = new RpfMenuAction("Save As                -  Save the deal,  this is the way you get to choose the file name", "menuSaveAs",
				KeyEvent.VK_A);
		menuItem = new JMenuItem(menuSaveAsAction);
		menuItem.setAction(menuSaveAsAction);
		menuItem.addActionListener(App.con);
		if (App.FLAG_canSave)
			menu.add(menuItem);

		if (App.FLAG_canSave)
			menu.addSeparator();

		// Open Saves Folder
		menuItem = new JMenuItem("Open  'saves'  folder       -       THEN    -   use   'Drag and Drop'    to open any deal", KeyEvent.VK_F);
		menuItem.setActionCommand("openSavesFolder");
		menuItem.addActionListener(App.con);
		if (App.FLAG_canSave)
			menu.add(menuItem);

		// Open autoSaves Folder
		menuItem = new JMenuItem("Open  'autosaves'  folder");
		menuItem.setActionCommand("openAutoSavesFolder");
		menuItem.addActionListener(App.con);
		if (App.FLAG_canSave)
			menu.add(menuItem);

		if (App.FLAG_canSave)
			menu.addSeparator();

		if (App.devMode) {

			// Run Tests
			menuItem = new JMenuItem("Run Tests", KeyEvent.VK_T);
			menuItem.setActionCommand("runTests");
			menuItem.addActionListener(App.con);
			menu.add(menuItem);
		}

		// Exit
		menuItem = new JMenuItem("Exit");
		menuItem.setActionCommand("exit");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		// Play Bridge - MENU
		menu = new JMenu("Play Bridge       ");
		menu.setMnemonic(KeyEvent.VK_P);
		menuBar.add(menu);

		// Play Bridge
		menuItem = new JMenuItem("Play Bridge          -   Play Bridge", KeyEvent.VK_P);
		menuItem.setActionCommand("playBridge_playBridge");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		// Play Bridge and deal choices
		menuItem = new JMenuItem("Play Bridge          -   Play Bridge  &  show Deal Choices", KeyEvent.VK_B);
		menuItem.setActionCommand("playBridge_and_dealChoice");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		// Distr Flash Cards
		menuItem = new JMenuItem("D. Flash Cards    -   Distribution Flash Cards", KeyEvent.VK_D);
		menuItem.setActionCommand("playBridge_distrFlashCards");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		if (App.singleBookOnly == false) {
			// Open at Start
			menuItem = new JMenuItem("Open at Start      -   Open at the Start,  the reason for this application.", KeyEvent.VK_M);
			menuItem.setActionCommand("playBridge_openMainBook");
			menuItem.addActionListener(this);
			menu.add(menuItem);
		}

		menu.addSeparator();
		// Play Bridge and deal choices

		menuItem = new JMenuItem("Book Mode          -  Set Ideal size and layout", KeyEvent.VK_O);
		menuItem.setActionCommand("tutorial_idealSize");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("Book Mode          -  Set Ideal size no extra", KeyEvent.VK_K);
		menuItem.setActionCommand("tutorial_idealSize_noExtra");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		// Options - MENU
		menu = new JMenu("Options        ");
		menu.setMnemonic(KeyEvent.VK_O);
		menuBar.add(menu);

		// Right Panel - Prefs 0 DealChoices
		menuItem = new JMenuItem("Deals                  -   Choose strong or weak hands", KeyEvent.VK_D);
		menuItem.setActionCommand("rightPanelPrefs0_DealChoices");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		// Right Panel - Prefs 1 SeatChoices
		menuItem = new JMenuItem("Seat Choices   -   Where do you want to sit", KeyEvent.VK_S);
		menuItem.setActionCommand("rightPanelPrefs1_SeatChoice");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		// Right Panel - Prefs 2 AutoPlay
		menuItem = new JMenuItem("AutoPlay            -   includes Pause setting", KeyEvent.VK_A);
		menuItem.setActionCommand("rightPanelPrefs2_AutoPlay");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		// Right Panel - Prefs 3 SuitColors
		menuItem = new JMenuItem("Suit Colors        -   includes Symbol display choice", KeyEvent.VK_C);
		menuItem.setActionCommand("rightPanelPrefs3_SuitColors");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		// Right Panel - Prefs 4 DFC
		menuItem = new JMenuItem("DFC                      -   Distribution Flash Cards  ", KeyEvent.VK_C);
		menuItem.setActionCommand("rightPanelPrefs4_DFC");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		// Right Panel - Prefs 5 DSizeFont
		menuItem = new JMenuItem("Size & Font        -   Deal Size and Movie Font Override", KeyEvent.VK_U);
		menuItem.setActionCommand("rightPanelPrefs5_DSizeFont");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		// Right Panel - Prefs 6 StartUp
		menuItem = new JMenuItem("StartUp               -   includes Button display choices", KeyEvent.VK_U);
		menuItem.setActionCommand("rightPanelPrefs6_StartUp");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menu.addSeparator();

		// Bottom Panel
		menuItem = new JMenuItem("Screen Color  &  Speed Sliders", KeyEvent.VK_P);
		menuItem.setActionCommand("lowerPanel");
		menuItem.addActionListener(this);
		menu.add(menuItem);

//		if (App.singleBookOnly == false) {
		// The (possible) 2 BookShelves
		String bookText = App.singleBookOnly ? "Book        " : "Books      ";
		menu = App.ourBookshelf.addToMenuBar(this, menuBar, bookText, KeyEvent.VK_B, true /*obeySingleBook*/, App.singleBookOnly ? 0 : -1);

		if (menu != null && App.singleBookOnly) {
			menu.addSeparator();

			// Multiple books
			menuItem = new JMenuItem("Show Mutltiple Books    (needs app restart)   to reset see under  'Start Up'  options  ", KeyEvent.VK_M);
			menuItem.setActionCommand("showMultipleBooksNextTime");
			menuItem.addActionListener(this);
			menu.add(menuItem);
		}

		App.droppedBookshelf.addToMenuBar(this, menuBar, "Books Plus " + droppedBookshelfCount + "     ", KeyEvent.VK_P, false /*obeySingleBook*/,
				droppedBookshelfCount);
//		}

		// Help - MENU
		if (App.singleBookOnly == false) {
			menu = new JMenu("Welcome,  Help  &  Examples");
			menu.setMnemonic(KeyEvent.VK_H);
			menuBar.add(menu);

			App.ourBookshelf.add90sToMenu(this, menu, true /*obeySingleBook*/);
			menu.addSeparator();
		}
		else {
			menu = new JMenu("Help  &  About");
			menu.setMnemonic(KeyEvent.VK_H);
			menuBar.add(menu);
		}

		// Help Swap lin file player
		menuItem = new JMenuItem("How do I             Swap between aaBridge and another app as the (dblclick) .lin file player  ");
		menuItem.setActionCommand("menuSwapLinFilePlayer");
		menuItem.addActionListener(this);
		menu.add(menuItem);

//		menuItem = new JMenuItem("How do I             Use aaBridge to practice my Hand Counting  ");
//		menuItem.setActionCommand("menuPracticeCounting");
//		menuItem.addActionListener(this);
//		menu.add(menuItem);

//		menuItem = new JMenuItem("How do I             Set Opera Browser to give me One-Click access to web .lin files  ");
//		menuItem.setActionCommand("menuOperaOneClick");
//		menuItem.addActionListener(this);
//		menu.add(menuItem);

		menu.addSeparator();

		// RogerPf - Blog
		menuItem = new JMenuItem("Blog                     MusingsOnBridge  blog   -  for releated info  ", KeyEvent.VK_B);
		menuItem.setActionCommand("menuLookAtBlog");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		// Help LookAtWebsite
		menuItem = new JMenuItem("Website              aaBridge Website  -  so you can check to see if you have the latest version  ", KeyEvent.VK_W);
		menuItem.setActionCommand("menuLookAtWebsite");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		// Help About
		menuItem = new JMenuItem("About                   aaBridge", KeyEvent.VK_A);
		menuItem.setActionCommand("menuHelpAbout");
		menuItem.addActionListener(this);
		menu.add(menuItem);

	}

	/**
	*/
	public boolean isSplashTimerRunning() {
		// =============================================================
		return aaDragGlassPane.splashScreenCompleteTimer.isRunning();
	}

	/**
	*/
	public void setDragImage(BufferedImage image) {
		// =============================================================
		aaDragGlassPane.SetDragImage(image);

		if (image != null) {
			Toolkit.getDefaultToolkit().addAWTEventListener(aaDragGlassPane, AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK);
			aaDragGlassPane.setVisible(true);
		}
		else {
			Toolkit.getDefaultToolkit().removeAWTEventListener(aaDragGlassPane);
			aaDragGlassPane.setVisible(false);
		}
	}

	/**
	*/
	public void actionPerformed(ActionEvent e) {
		// =============================================================
		executeCmd(e.getActionCommand());
	}

	public int BOOK_IDEAL_HEIGHT = 916;
	public int BOOK_IDEAL_WIDTH = 1276;

	public int RIGHT_OPT_PANEL_WIDTH__narrow = 128;
	public int RIGHT_OPT_PANEL_WIDTH__menu = 210;
	public int RIGHT_OPT_PANEL_WIDTH__wide = 320;
	public int BOTTOM_OPT_PANEL_HEIGHT = 128;

	public int BOOK_IDEAL_HEIGHT__NO_EXTRA = 874;
	public int BOOK_IDEAL_WIDTH__NO_EXTRA = 1176;

	/**
	*/
	public void executeCmd(String cmd) {
		// =============================================================

		if (cmd.contentEquals("exit")) {
			App.savePreferences();
			System.exit(0); // SHUTS DOWN aaBridge NOW
			return;
		}

		if (cmd.contentEquals("playBridge_playBridge")) {
			if (App.showRedDividerArrow) {
				App.gbo.showDividerHint();
			}
			App.dualDealListBtns.setVisible(false);
			if (App.singleBookOnly == false) {
				App.book = new Book(); // which will be empty
				App.bookPanel.matchToAppBook();
			}
			App.setVisualMode(App.Vm_InsideADeal);
			App.setMode(Aaa.NORMAL_ACTIVE);
			CmdHandler.playBridgeBlueCenter();
		}

		if (cmd.contentEquals("playBridge_and_dealChoice")) {
			if (App.showRedDividerArrow) {
				App.gbo.showDividerHint();
			}
			App.dualDealListBtns.setVisible(false);
			App.frame.splitPaneHorz.setDividerLocation(App.frame.getWidth() - RIGHT_OPT_PANEL_WIDTH__narrow);
			App.frame.rop.setSelectedIndex(App.RopTab_0_Deals);
			App.frame.splitPaneVert.setDividerLocation(App.frame.getHeight() - BOTTOM_OPT_PANEL_HEIGHT);
			if (App.singleBookOnly == false) {
				App.book = new Book(); // which will be empty
				App.bookPanel.matchToAppBook();
			}
			App.setVisualMode(App.Vm_InsideADeal);
			App.setMode(Aaa.NORMAL_ACTIVE);
			CmdHandler.playBridgeBlueCenter();
		}

		if (cmd.contentEquals("playBridge_openMainBook")) {
			Book b = App.ourBookshelf.getAutoOpenBook();
			if (b != null) {
				LinChapter chapter = b.getChapterByIndex(0);
				if (chapter != null) {
					/* chapterLoaded */chapter.loadWithShow("replaceBookPanel");
				}
			}
		}

		if (cmd.contentEquals("playBridge_distrFlashCards")) {
			String chapterPartName = "Distr Flash Cards";
			Book b = App.ourBookshelf.getBookWithChapterPartName(chapterPartName);
			if (b != null) {
				LinChapter chapter = b.getChapterByDisplayNamePart(chapterPartName);
				if (chapter != null) {
					App.frame.splitPaneHorz.setDividerLocation(App.frame.getWidth() - RIGHT_OPT_PANEL_WIDTH__narrow);
					App.frame.rop.setSelectedIndex(App.RopTab_4_DFC);

					/* chapterLoaded */chapter.loadWithShow("replaceBookPanel");
				}
			}
		}

		if (cmd.contentEquals("tutorial_idealSize")) {
			App.frame.splitPaneHorz.setDividerLocation(App.frame.getWidth() - RIGHT_OPT_PANEL_WIDTH__narrow);
			App.frame.splitPaneVert.setDividerLocation(App.frame.getHeight() - BOTTOM_OPT_PANEL_HEIGHT);
			Rectangle r = getBounds();
			r.width = BOOK_IDEAL_WIDTH;
			r.height = BOOK_IDEAL_HEIGHT;
			setBounds(r);
		}

		if (cmd.contentEquals("tutorial_idealSize_noExtra")) {
			App.frame.splitPaneHorz.setDividerLocation(App.frame.getWidth());
			App.frame.splitPaneVert.setDividerLocation(App.frame.getHeight());
			Rectangle r = getBounds();
			r.width = BOOK_IDEAL_WIDTH__NO_EXTRA; // - RIGHT_OPT_PANEL_WIDTH__narrow ;
			r.height = BOOK_IDEAL_HEIGHT__NO_EXTRA; // - BOTTOM_OPT_PANEL_HEIGHT;
			setBounds(r);
		}

		if (cmd.contentEquals("showMultipleBooksNextTime")) {
			App.multiBookDisplay = true;
		}

		if (cmd.contentEquals("showStartUpOpts")) {
			App.frame.splitPaneHorz.setDividerLocation(App.frame.getWidth() - RIGHT_OPT_PANEL_WIDTH__wide);
			App.frame.rop.setSelectedIndex(App.RopTab_6_StartUp);
			App.frame.splitPaneVert.setDividerLocation(App.frame.getHeight() - BOTTOM_OPT_PANEL_HEIGHT);
		}

		int menuSetRightOptWidth = App.frame.getWidth() - RIGHT_OPT_PANEL_WIDTH__menu;

		if (cmd == "rightPanelPrefs0_DealChoices") {
			App.frame.splitPaneHorz.setDividerLocation(menuSetRightOptWidth);
			App.frame.rop.setSelectedIndex(App.RopTab_0_Deals);
		}
		if (cmd == "rightPanelPrefs1_SeatChoice") {
			App.frame.splitPaneHorz.setDividerLocation(menuSetRightOptWidth);
			App.frame.rop.setSelectedIndex(App.RopTab_1_Seat);
		}
		if (cmd == "rightPanelPrefs2_AutoPlay") {
			App.frame.splitPaneHorz.setDividerLocation(menuSetRightOptWidth);
			App.frame.rop.setSelectedIndex(App.RopTab_2_Autoplay);
		}
		if (cmd == "rightPanelPrefs3_SuitColors") {
			App.frame.splitPaneHorz.setDividerLocation(menuSetRightOptWidth);
			App.frame.rop.setSelectedIndex(App.RopTab_3_SuitColors);
		}
		if (cmd == "rightPanelPrefs4_DFC") {
			App.frame.splitPaneHorz.setDividerLocation(menuSetRightOptWidth);
			App.frame.rop.setSelectedIndex(App.RopTab_4_DFC);
		}
		if (cmd == "rightPanelPrefs5_DSizeFont") {
			App.frame.splitPaneHorz.setDividerLocation(menuSetRightOptWidth);
			App.frame.rop.setSelectedIndex(App.RopTab_5_DSizeFont);
		}
		if (cmd == "rightPanelPrefs6_StartUp") {
			App.frame.splitPaneHorz.setDividerLocation(menuSetRightOptWidth);
			App.frame.rop.setSelectedIndex(App.RopTab_6_StartUp);
		}
		else if (cmd == "lowerPanel") {
			App.frame.splitPaneVert.setDividerLocation(App.frame.getHeight() - BOTTOM_OPT_PANEL_HEIGHT);
		}
		else if (cmd == "menuHelpHelp") {
			Book b = App.ourBookshelf.getBookByFrontNumb(91 /* The Standard Help */);
			if (b != null) {
				boolean chapterLoaded = b.loadChapterByIndex(0);
				if (chapterLoaded) {
					App.book = b;
					App.bookPanel.matchToAppBook();
					App.bookPanel.showChapterAsSelected(0);
				}
			}
		}
		else if (cmd == "menuSwapLinFilePlayer") {
			try {
				Desktop.getDesktop().browse(new java.net.URI("http://musingsonbridge.rogerpf.com/2013/08/swap-between-aabridge-and-bbo-as-lin.html"));
			} catch (Exception ev) {
			}
		}
		else if (cmd == "menuPracticeCounting") {
			try {
				Desktop.getDesktop().browse(new java.net.URI("http://musingsonbridge.rogerpf.com/2013/08/counting-hand-1.html"));
			} catch (Exception ev) {
			}
		}
		else if (cmd == "menuWhatIsALinFile") {
			try {
				Desktop.getDesktop().browse(new java.net.URI("http://musingsonbridge.rogerpf.com/2013/08/aabridge-and-lin-files.html"));
			} catch (Exception ev) {
			}
		}
		else if (cmd == "menuLookAtBlog") {
			try {
				Desktop.getDesktop().browse(new java.net.URI("http://musingsonbridge.rogerpf.com/"));
			} catch (Exception ev) {
			}
		}
		else if (cmd == "menuLookAtWebsite") {
			try {
				Desktop.getDesktop().browse(new java.net.URI("http://rogerpf.com/z_bridge_area/bridge/aaBridge.php"));
			} catch (Exception ev) {
			}
		}
		else if (cmd == "menuHelpAbout") {

			java.net.URL imageFileURL = AaaOuterFrame.class.getResource("aaBridge_proto_icon.png");
			final ImageIcon icon = new ImageIcon(imageFileURL);
//			final ImageIcon icon = Toolkit.getDefaultToolkit().createImage(imageFileURL);

			String s = "AaBridge written by Roger Pfister\n\n" + "This is version -  " + VersionAndBuilt.getVer() + "\n" + "Build Number   -               "
					+ VersionAndBuilt.getBuildNo() + "\n" + "Built on              -  " + VersionAndBuilt.getBuilt() + "\n\n" + "see - http://RogerPf.com\n\n";
			;
			JOptionPane.showMessageDialog(this, s, "About - aaBridge", JOptionPane.INFORMATION_MESSAGE, icon);

		}
		else {
			Book b = App.ourBookshelf.getBookByBasePathAndDisplayTitle(cmd);
			if (b != null) {
				boolean chapterLoaded = b.loadChapterByIndex(0);
				if (chapterLoaded) {
					App.book = b;
					App.bookPanel.matchToAppBook();
					App.bookPanel.showChapterAsSelected(0);
				}
				return;
			}

			b = App.droppedBookshelf.getBookByBasePathAndDisplayTitle(cmd);
			if (b != null) {
				boolean chapterLoaded = b.loadChapterByIndex(0);
				if (chapterLoaded) {
					App.book = b;
					App.bookPanel.matchToAppBook();
					App.bookPanel.showChapterAsSelected(0);
				}
			}
		}
	}

	public void componentMoved(ComponentEvent e) {
		if (this.getExtendedState() == NORMAL) {
			Point p = this.getLocation();
			App.frameLocationX = p.x;
			App.frameLocationY = p.y;
		}
	}

	public void componentResized(ComponentEvent e) {
		if (this.getExtendedState() == NORMAL) {
			App.frameWidth = getWidth();
			App.frameHeight = getHeight();
		}
	}

	public void windowClosing(WindowEvent e) {
		App.savePreferences();
		CmdHandler.doAutoSave();
	}

	public void componentHidden(ComponentEvent e) {
	}

	public void componentShown(ComponentEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}

	/**
	*/
	public void setTitleAsRequired() {
		// =============================================================
		String s = "aaBridge  " + VersionAndBuilt.verAndBuildNo();

		if (App.mg != null && App.mg.lin != null && App.mg.lin.linType == Lin.FullMovie) {
			s += "   -   " + App.mg.lin.filename;
		}
		else if ((App.deal.lastSavedAsFilename != null) && (App.deal.lastSavedAsFilename.length() > 0)) {
			s += "   -   " + App.deal.lastSavedAsFilename;
		}

		setTitle(s);
	}

	/**
	 *  drag and drop support for externaly (outside java from host OS) dropped deal files
	 */
	private TransferHandler handler = new TransferHandler() {
		// =============================================================
		private static final long serialVersionUID = 1L;

		public boolean canImport(TransferHandler.TransferSupport support) {
			if (App.FLAG_drag_n_drop == false)
				return false;

			if (!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				return false;
			}
			support.setDropAction(COPY);
			return true;
		}

		public boolean importData(TransferHandler.TransferSupport support) {
			if (!canImport(support)) {
				return false;
			}

			Transferable t = support.getTransferable();
			File[] files;
			try {
				@SuppressWarnings("unchecked")
				java.util.List<File> list = (java.util.List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
				if (list.size() == 0)
					return false;
				/**
				 *  convert to the Files array type used elsewhere
				 *  in this app
				 */
				files = new File[list.size()];
				int i = 0;
				for (File f : list) {
					files[i++] = f;
				}

			} catch (Exception e) {
				return false;
			}

			return BridgeLoader.processDroppedList(files);
		}
	};

	/**   
	 */
	static void clearOutOldFilesFromFolder(String folderNameAndPath, long deleteAfterDays) {
		// ==============================================================================================

		// Get the list of all the potential tests
		File[] files = null;
		try {
			files = new File(folderNameAndPath).listFiles();
			long now = System.currentTimeMillis();
			long deleteEarlierThan = now - (deleteAfterDays * 24L * 60L * 60L * 1000L);
			// deleteEarlierThan = now - (30L*1000L); // testing only
			for (File file : files) {
				if (file.isFile() == false)
					continue;
				if (file.lastModified() > deleteEarlierThan)
					continue;

				file.delete(); // does the business

			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

}
