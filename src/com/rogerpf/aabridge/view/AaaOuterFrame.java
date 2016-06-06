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
import java.awt.GraphicsEnvironment;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
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
import javax.swing.filechooser.FileSystemView;

import net.miginfocom.swing.MigLayout;

import com.rogerpf.aabridge.controller.AaBridge;
import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.controller.Book;
import com.rogerpf.aabridge.controller.Book.LinChapter;
import com.rogerpf.aabridge.controller.Bookshelf;
import com.rogerpf.aabridge.controller.BookshelfArray;
import com.rogerpf.aabridge.controller.BridgeLoader;
import com.rogerpf.aabridge.controller.CmdHandler;
import com.rogerpf.aabridge.dds.Z_ddsCalculate;
import com.rogerpf.aabridge.igf.BubblePanel;
import com.rogerpf.aabridge.igf.MassGi;
import com.rogerpf.aabridge.igf.MassGi_utils;
import com.rogerpf.aabridge.igf.TutNavigationBar;
import com.rogerpf.aabridge.igf.TutorialPanel;
import com.rogerpf.aabridge.model.Cc;
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

//		/**
//		 *  We always do the following when the application is properly closed
//		 */
//		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
//			public void run() {
//				App.aaHomeBtnPanel.mruDelayedSaveTimer.stop();
//				System.out.println("In - shutdownhook");
//				App.savePreferences();
//
//				int prev_pg_numb = App.mg.get_best_pg_number_for_history();
//				App.mruCollection.update_prev_hist_pgNumb(prev_pg_numb);
//				App.mruCollection.saveCollection();
//			}
//		}));

		/** 
		 * Spin the random number generator - well old habits die hard
		 */
		{
			int n = (int) ((System.currentTimeMillis() % 1000) & 0x3f) + 10;
			for (int i = 0; i < n; i++) {
				Math.random();
			}
		}

		/**
		 * An now the real start
		 */
		try {
			URL locationMethodUrl = AaBridge.class.getProtectionDomain().getCodeSource().getLocation();
			File locMethodFile = new File(locationMethodUrl.toURI());

			String flag_folder = "";
			if (locMethodFile.getName().toLowerCase().endsWith(".jar")) {
				App.runningInJar = true;
				App.thisAppBaseJar = locMethodFile.getName();
				App.thisAppBaseJarIncPath = locMethodFile.getPath();
				App.thisAppBaseJarIncPath_orig = locMethodFile.getPath();
				;
				flag_folder = locMethodFile.getParent() + File.separator;
			}
			else {
				/* we must be running in Eclipse or the like as source */
				String s = locMethodFile.getPath();
				if (s.endsWith(File.separator + "bin")) {
					App.thisAppBaseFolder = s.substring(0, s.length() - 3) + "src";
				}
				// assume we are dev running in eclipse or the like in windows
				// and default to the normal install place far aaBridge (testing only)
				flag_folder = "C:\\programSmall\\aaBridge\\";
			}

			if (new File(flag_folder + "_aaBridge_a__ignore_all_reldates.txt").exists()) {
				App.observeReleaseDates = false;
			}

			if (new File(flag_folder + "_aaBridge_b__use_devmode.txt").exists()) {
				App.devMode = true;
			}

			if (new File(flag_folder + "_aaBridge_c__show_dev_test_lins.txt").exists()) {
				App.showDevTestLins = true;
			}

			// @formatter:off
			if (      (App.runningInJar == false)
				   && (App.thisAppBaseFolder.isEmpty() == false)
				   && (App.ghost_jar.toLowerCase().endsWith(".jar"))
				   && (new File(flag_folder + "_aaBridge_d__debug_use_ghost_jar.txt")).exists())    {
				
				File f = new File(flag_folder + App.ghost_jar);
				if (f.exists()) {
					App.debug_using_ghost_jar = true; // master switch is now ON
					App.runningInJar = true;
					App.thisAppBaseJar = f.getName(); // so making later tests for 'INTERNAL' work
					App.thisAppBaseJarIncPath = f.getPath();
				}
			}
			// @formatter:on

			if (new File(flag_folder + "_aaBridge_e__use_mru_dev.txt").exists()) {
				App.mruNodeSubNode = "mru_dev";
			}

			if (new File(flag_folder + "_aaBridge_f__write_mini_log.txt").exists()) {
				// Debug ONLY
				String logFilePath = flag_folder + "_aaBridge_mini_log.txt";
				FileWriter fw = new FileWriter(logFilePath);
				BufferedWriter bw = new BufferedWriter(fw);
				{
					bw.write(flag_folder + "  " + App.observeReleaseDates + "  " + App.devMode + "  " + App.showDevTestLins + "\n");
					bw.write("\n");
					bw.write("App.runningInJar:               " + App.runningInJar + "\n");
					bw.write("App.thisAppBaseFolder:          " + App.thisAppBaseFolder + "\n");
					bw.write("\n");
					bw.write("App.debug_using_ghost_jar:      " + App.debug_using_ghost_jar + "\n");
					bw.write("App.thisAppBaseJar:             " + App.thisAppBaseJar + "\n");
					bw.write("App.thisAppBaseJarIncPath:      " + App.thisAppBaseJarIncPath + "\n");
					bw.write("\n");
					bw.write("App.thisAppBaseJarIncPath_orig: " + App.thisAppBaseJarIncPath_orig + "\n");
				}
				bw.flush();
				bw.close();
				fw.close();
			}

		} catch (Exception e1) {
		}

		/* MAC's have display issues in Java when told to use small borders with Swing Buttons
		 * We set a flag here so we can easily tell later
		 */
		String OS = System.getProperty("os.name").toLowerCase();
		App.onMac = (OS.indexOf("mac") >= 0);
		App.onWin = (OS.indexOf("win") >= 0);
		App.onLinux = !App.onMac && !App.onWin;
		App.onMacOrLinux = App.onMac || App.onLinux;
		App.using_java_6 = System.getProperty("java.version").startsWith("1.6");

		setVisible(false); // set true by the timer below
		java.net.URL imageFileURL = AaaOuterFrame.class.getResource("aaBridge_proto_icon.png");
		setIconImage(Toolkit.getDefaultToolkit().createImage(imageFileURL));

		this.addWindowListener(this);
		this.addComponentListener(this);

		App.loadPreferences();

		App.selectMnHeaderColor();

		// Active Bo Haglunds DDS
		App.haglundsDDSavailable = Z_ddsCalculate.is_dds_available();

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

		if (App.startedWithCleanSettings) {
			// not currently used but still abvaliable
			// App.startedWithCleanSettings = false;
		}

		App.bookshelfArray = new BookshelfArray(); // this is the only instance

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

		App.aaBookPanel = new AaBookPanel();
		App.aaHomeBtnPanel = new AaHomeBtnPanel();

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
		// to remove the homeBtn feature comment out the line below
		linPlcp.add(App.aaHomeBtnPanel, "split2, flowy, hidemode 1");
		linPlcp.add(App.aaBookPanel, "hidemode 1, growy, center, hmin 0");
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

		App.desktopFolderPath = FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath();
		if (!App.onWin) {
			if (App.desktopFolderPath.toLowerCase().endsWith("Desktop") == false) {
				App.desktopFolderPath += File.separator + "Desktop";
			}
		}
		App.desktopFolderPath += File.separator;

		String appHomePath = System.getProperty("user.home") + File.separator + "aaBridge" + File.separator;
		App.autoSavesPath = appHomePath + "autosaves" + File.separator;
		App.defaultSavesPath = appHomePath + "saves" + File.separator;
		if (App.realSavesPath.isEmpty()) {
			App.realSavesPath = App.defaultSavesPath;
		}

		File appHome = new File(appHomePath);
		File autoSaves = new File(App.autoSavesPath);
		File defaultSaves = new File(App.defaultSavesPath);
		File saves = new File(App.realSavesPath);
		// tests are in with the books

		appHome.mkdir();
		autoSaves.mkdir();
		defaultSaves.mkdir(); // so we make the default even if use have set our own
		saves.mkdir();

		lop.realSavesPathNowAvailable();

		clearOutOldFilesFromFolder(App.autoSavesPath, 7);

		setTitleAsRequired();

		// is the standard text font available
		GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
		String[] fonts = g.getAvailableFontFamilyNames();
		App.fontfamilyStandardAvailable = false;
		for (int i = 0; i < fonts.length; i++) {
			if (fonts[i].equals(App.fontfamilyStandard)) {
				App.fontfamilyStandardAvailable = true;
				break;
			}
		}

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

		App.aaBookPanel.setVisible(false);
		App.aaBookPanel.setCorrectWidth(App.dualDealListBtns.isVisible());
		App.gbr.setVisible(false);
		App.tup.setVisible(false);
		App.ccb.setVisible(false);
		App.tnb.setVisible(false);
		App.aaBookPanel.setVisible(true);

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
			menuItem = new JMenuItem("Open          -  Saved Deal (lin file) - you can instead just  'Drag and Drop'  the file", KeyEvent.VK_O);
			menuItem.setActionCommand("menuOpen");
			menuItem.addActionListener(App.con);
			menu.add(menuItem);
		}

		// Save Std
		menuSaveStdAction = new RpfMenuAction("Save          -  Save using the file name you last set with 'Save As'", "menuSaveStd", KeyEvent.VK_S);
		menuItem = new JMenuItem(menuSaveStdAction);
		menuItem.setAction(menuSaveStdAction);
		menuItem.addActionListener(App.con);
		if (App.FLAG_canSave)
			menu.add(menuItem);

		// Save As
		menuSaveAsAction = new RpfMenuAction("Save As    -  Save the deal,  this is the way you get to choose the file name", "menuSaveAs", KeyEvent.VK_A);
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

//		if (App.devMode) {
//			// Run Tests
//			menuItem = new JMenuItem("Run Tests", KeyEvent.VK_T);
//			menuItem.setActionCommand("runTests");
//			menuItem.addActionListener(App.con);
//			menu.add(menuItem);
//		}

		// Paste - Accepts a Paste (normally of a tiny url)
		menuItem = new JMenuItem("Paste        -  Accepts a 'Paste',  normally of a 'Tiny Url',  to load that deal", KeyEvent.VK_P);
		menuItem.setActionCommand("acceptPaste");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		// Exit
		menuItem = new JMenuItem("Exit");
		menuItem.setActionCommand("exit");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		// Ideal Size - MENU
		menu = new JMenu("Ideal Size       ");
		menu.setMnemonic(KeyEvent.VK_I);
		menuBar.add(menu);

		menuItem = new JMenuItem("1  Small Laptop", KeyEvent.VK_1);
		menuItem.setActionCommand("tutorial_idealSize_small_noExtra");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("2  Small Laptop +  Extra", KeyEvent.VK_2);
		menuItem.setActionCommand("tutorial_idealSize_small");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menu.addSeparator();

		menuItem = new JMenuItem("3  Ideal", KeyEvent.VK_3);
		menuItem.setActionCommand("tutorial_idealSize_std_noExtra");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("4  Ideal  +  Extra", KeyEvent.VK_4);
		menuItem.setActionCommand("tutorial_idealSize_std");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menu.addSeparator();

		menuItem = new JMenuItem("5  Big", KeyEvent.VK_5);
		menuItem.setActionCommand("tutorial_idealSize_big_noExtra");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("6  Big  +  Extra", KeyEvent.VK_6);
		menuItem.setActionCommand("tutorial_idealSize_big");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menu.addSeparator();

		menuItem = new JMenuItem("7  Very Big", KeyEvent.VK_7);
		menuItem.setActionCommand("tutorial_idealSize_vbig_noExtra");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("8  Very Big  +  Extra", KeyEvent.VK_8);
		menuItem.setActionCommand("tutorial_idealSize_vbig");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menu.addSeparator();

		menuItem = new JMenuItem("9  Very very Big", KeyEvent.VK_9);
		menuItem.setActionCommand("tutorial_idealSize_vvbig_noExtra");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("0  Very very Big  +  Extra", KeyEvent.VK_0);
		menuItem.setActionCommand("tutorial_idealSize_vvbig");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		// Options - MENU
		menu = new JMenu("Options        ");
		menu.setMnemonic(KeyEvent.VK_O);
		menuBar.add(menu);

		// Right Panel - Prefs 7 ShowBtns
		menuItem = new JMenuItem("Show                  -   Show / Hide  optional Buttons", KeyEvent.VK_W);
		menuItem.setActionCommand("rightPanelPrefs7_ShowBtns");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		// Right Panel - Prefs 6 RedHints
		menuItem = new JMenuItem("Red Hints           -   Show / Hide the  Red Arrow  hints", KeyEvent.VK_R);
		menuItem.setActionCommand("rightPanelPrefs6_RedHints");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		// Right Panel - Prefs 5 DSizeFont
		menuItem = new JMenuItem("Size & Font        -   Deal Size and Movie Font Override", KeyEvent.VK_F);
		menuItem.setActionCommand("rightPanelPrefs5_DSizeFont");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		// Right Panel - Prefs 4 SuitColors
		menuItem = new JMenuItem("Suit Colors        -   includes Symbol display choice", KeyEvent.VK_C);
		menuItem.setActionCommand("rightPanelPrefs4_SuitColors");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		// Right Panel - Prefs 3 DFC
		menuItem = new JMenuItem("DFC                      -   Distribution Flash Cards  ", KeyEvent.VK_D);
		menuItem.setActionCommand("rightPanelPrefs3_DFC");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		// Right Panel - Prefs 2 SeatChoices
		menuItem = new JMenuItem("Seat Choice      -   Where do you want to sit", KeyEvent.VK_S);
		menuItem.setActionCommand("rightPanelPrefs2_SeatChoice");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		// Right Panel - Prefs 1 AutoPlay
		menuItem = new JMenuItem("AutoPlay            -   includes Pause setting", KeyEvent.VK_A);
		menuItem.setActionCommand("rightPanelPrefs1_AutoPlay");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		// Right Panel - Prefs 0 DealChoices
		menuItem = new JMenuItem("New Deals         -   Choose strong or weak hands", KeyEvent.VK_N);
		menuItem.setActionCommand("rightPanelPrefs0_NewDealChoices");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menu.addSeparator();

		// Bottom Panel
		menuItem = new JMenuItem("Screen Color  &  Speed Sliders", KeyEvent.VK_P);
		menuItem.setActionCommand("lowerPanel");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		for (Bookshelf shelf : App.bookshelfArray) {
			shelf.addToMenuBar(this, menuBar);
		}

		// Help - MENU
		menu = new JMenu("Help  &  How do I ?");
		menu.setMnemonic(KeyEvent.VK_H);
		menuBar.add(menu);

		menuItem = new JMenuItem("                             View the aaBridge Document Collection");
		menuItem.setActionCommand("openPage_DocCollection");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("How do I             Use aaBridge to learn the Suit Distributions and count the hands     (YouTube) ");
		menuItem.setActionCommand("youtube_SuitDistrib");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("How do I             Debug - BBO Dealer Scripts     (Blog) ");
		menuItem.setActionCommand("blog_debugBboDealerScripts");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("How do I             Swap between aaBridge and another app as the (DblClick) .lin file player     (Blog)");
		menuItem.setActionCommand("menuSwapLinFilePlayer");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("                             What is aaBridge ?");
		menuItem.setActionCommand("openPage_WhatIsaaBridge");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("How do I             Use the Double Dummy Solver");
		menuItem.setActionCommand("openPage_BoHaglundDDS");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("                             Seat Choice and that  Pink Dot !       (Blog)");
		menuItem.setActionCommand("blog_seatChoiceAndThatPinkDot");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("How do I             Memorize the Suit Distributions");
		menuItem.setActionCommand("openPage_MemorizeDistributions");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("                             Down the Rabbit Hole   -   An aaBridge Key Concept");
		menuItem.setActionCommand("openPage_DocCollection");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("How do I             Enter (Type) hands into aaBridge");
		menuItem.setActionCommand("openPage_DocCollection");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("How do I             Get aaBridge hands up to BBO");
		menuItem.setActionCommand("openPage_DocCollection");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("How do I             Get a BBO hand into aaBridge");
		menuItem.setActionCommand("openPage_DocCollection");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("How do I             'Play' an existing deal in aaBridge");
		menuItem.setActionCommand("openPage_DocCollection");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("                             Visualize Hands with aaBridge");
		menuItem.setActionCommand("openPage_VisualizeHands");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("How do I             Defend like an Expert");
		menuItem.setActionCommand("openPage_DefendExpert");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("                             Improve Your Bridge");
		menuItem.setActionCommand("openPage_ImproveYourBridge");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menu.addSeparator();

		Bookshelf.addFirstShelf_90s_toMenu(this, menu);
		menu.addSeparator();

		// Roger Pf - Blog
		menuItem = new JMenuItem("Blog                     MusingsOnBridge  blog   -  for releated info  ", KeyEvent.VK_B);
		menuItem.setActionCommand("web_LookAtBlog");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		// Help LookAtWebsite
		menuItem = new JMenuItem("Website              aaBridge Website  -  so you can check to see if you have the latest version  ", KeyEvent.VK_W);
		menuItem.setForeground(Cc.GreenStrong);
		menuItem.setActionCommand("web_LookAtWebsite");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		// Help About
		menuItem = new JMenuItem("About                   aaBridge", KeyEvent.VK_A);
		menuItem.setActionCommand("internal_HelpAbout");
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

	public int RIGHT_OPT_PANEL_WIDTH__narrow = 128;
	public int RIGHT_OPT_PANEL_WIDTH__menu = 210;
	public int RIGHT_OPT_PANEL_WIDTH__wide = 320;
	public int BOTTOM_OPT_PANEL_HEIGHT = 128;

	public int RIGHT_OPT_PANEL_WIDTH__small_wider_adjust_A = 135;
	public int RIGHT_OPT_PANEL_WIDTH__small_wider_adjust_B = 170;

	int WIDTH_EXTRA = 100;
	int HEIGHT_EXTRA = 42;

	public int BOOK_IDEAL_WIDTH_STD__NO_EXTRA = 1176;
	public int BOOK_IDEAL_HEIGHT_STD__NO_EXTRA = 874;

	public int BOOK_IDEAL_WIDTH_STD = BOOK_IDEAL_WIDTH_STD__NO_EXTRA + WIDTH_EXTRA;
	public int BOOK_IDEAL_HEIGHT_STD = BOOK_IDEAL_HEIGHT_STD__NO_EXTRA + HEIGHT_EXTRA;

	int WIDTH_SMALL_MINUS = 66;
	int HEIGHT_SMALL_MINUS = 90;

	public int BOOK_IDEAL_WIDTH_SMALL__NO_EXTRA = BOOK_IDEAL_WIDTH_STD__NO_EXTRA - WIDTH_SMALL_MINUS;
	public int BOOK_IDEAL_HEIGHT_SMALL__NO_EXTRA = BOOK_IDEAL_HEIGHT_STD__NO_EXTRA - HEIGHT_SMALL_MINUS;

	public int BOOK_IDEAL_WIDTH_SMALL = BOOK_IDEAL_WIDTH_SMALL__NO_EXTRA + WIDTH_EXTRA + RIGHT_OPT_PANEL_WIDTH__small_wider_adjust_B;
	public int BOOK_IDEAL_HEIGHT_SMALL = BOOK_IDEAL_HEIGHT_SMALL__NO_EXTRA; // + HEIGHT_EXTRA; reduced height on row res laptops

	int WIDTH_BIG_PLUS = 66;
	int HEIGHT_BIG_PLUS = 40;

	public int BOOK_IDEAL_WIDTH_BIG__NO_EXTRA = BOOK_IDEAL_WIDTH_STD__NO_EXTRA + WIDTH_BIG_PLUS;
	public int BOOK_IDEAL_HEIGHT_BIG__NO_EXTRA = BOOK_IDEAL_HEIGHT_STD__NO_EXTRA + HEIGHT_BIG_PLUS;

	public int BOOK_IDEAL_WIDTH_BIG = BOOK_IDEAL_WIDTH_BIG__NO_EXTRA + WIDTH_EXTRA;
	public int BOOK_IDEAL_HEIGHT_BIG = BOOK_IDEAL_HEIGHT_BIG__NO_EXTRA + HEIGHT_EXTRA;

	int WIDTH_VBIG_PLUS = 116;
	int HEIGHT_VBIG_PLUS = 80;

	public int BOOK_IDEAL_WIDTH_VBIG__NO_EXTRA = BOOK_IDEAL_WIDTH_STD__NO_EXTRA + WIDTH_VBIG_PLUS;
	public int BOOK_IDEAL_HEIGHT_VBIG__NO_EXTRA = BOOK_IDEAL_HEIGHT_STD__NO_EXTRA + HEIGHT_VBIG_PLUS;

	public int BOOK_IDEAL_WIDTH_VBIG = BOOK_IDEAL_WIDTH_VBIG__NO_EXTRA + WIDTH_EXTRA;
	public int BOOK_IDEAL_HEIGHT_VBIG = BOOK_IDEAL_HEIGHT_VBIG__NO_EXTRA + HEIGHT_EXTRA;

	int WIDTH_VVBIG_PLUS = 176;
	int HEIGHT_VVBIG_PLUS = 120;

	public int BOOK_IDEAL_WIDTH_VVBIG__NO_EXTRA = BOOK_IDEAL_WIDTH_STD__NO_EXTRA + WIDTH_VVBIG_PLUS;
	public int BOOK_IDEAL_HEIGHT_VVBIG__NO_EXTRA = BOOK_IDEAL_HEIGHT_STD__NO_EXTRA + HEIGHT_VVBIG_PLUS;

	public int BOOK_IDEAL_WIDTH_VVBIG = BOOK_IDEAL_WIDTH_VVBIG__NO_EXTRA + WIDTH_EXTRA;
	public int BOOK_IDEAL_HEIGHT_VVBIG = BOOK_IDEAL_HEIGHT_VVBIG__NO_EXTRA + HEIGHT_EXTRA;

	int macOrLinuxAdjust(int orig) {
		// =============================================================
		return orig + (App.onMacOrLinux ? 40 : 0);
	}

	/**
	*/
	public void executeCmd(String cmd) {
		// =============================================================
		if (cmd.contentEquals("exit")) {

			App.savePreferences();
			System.exit(0); // SHUTS DOWN aaBridge NOW
			return;
		}

		if (cmd.contentEquals("acceptPaste")) {

			App.frame.clickPasteTimer.start();
			return;
		}

		if (cmd.contentEquals("playBridge_playBridge")) {

			App.dualDealListBtns.setVisible(false);
			App.book = new Book(); // which will be empty
			App.aaBookPanel.matchToAppBook();
			App.setVisualMode(App.Vm_InsideADeal);
			App.setMode(Aaa.NORMAL_ACTIVE);
			CmdHandler.playBridgeBlueCenter();
			return;
		}

		if (cmd.contentEquals("playBridge_and_dealChoice")) {

			App.dualDealListBtns.setVisible(false);
			App.frame.splitPaneHorz.setDividerLocation(App.frame.getWidth() - RIGHT_OPT_PANEL_WIDTH__narrow);
			App.frame.rop.setSelectedIndex(App.RopTab_0_NewDealChoices);
			App.frame.splitPaneVert.setDividerLocation(App.frame.getHeight() - BOTTOM_OPT_PANEL_HEIGHT);
			App.book = new Book(); // which will be empty
			App.aaBookPanel.matchToAppBook();
			App.setVisualMode(App.Vm_InsideADeal);
			App.setMode(Aaa.NORMAL_ACTIVE);
			CmdHandler.playBridgeBlueCenter();
			return;
		}

		if (cmd.contentEquals("open_FirstShelf_Book01")) {
			Book b = App.bookshelfArray.get(0).getBookByFrontNumb(01 /* shelf 1 book 01   was always 'Watsons' book */);
			if (b != null) {
				LinChapter chapter = b.getChapterByIndex(0);
				if (chapter != null) {
					/* chapterLoaded */chapter.loadWithShow("replaceBookPanel");
				}
			}
			return;
		}

		if (cmd.contentEquals("open_Welcome_New_User")) {
			Book b = App.bookshelfArray.get(0).getBookByFrontNumb(90 /* shelf 1 book 90   Help & Welcome */);
			if (b != null) {
				LinChapter chapter = b.getChapterByDisplayNamePart("New User");
				if (chapter != null) {
					/* chapterLoaded */chapter.loadWithShow("replaceBookPanel");
				}
			}
			return;
		}

		if (cmd.contentEquals("open_random_lin_file")) {
			LinChapter chapter = App.bookshelfArray.pickRandomLinFile();
			if (chapter != null) {
				/* chapterLoaded */chapter.loadWithShow("replaceBookPanel");
			}
			return;
		}

		if (cmd.contentEquals("copyFolder_Doc_Collection")) {
			Bookshelf.copy_folder_to_desktop("doc-collection", "aaBridge - Doc Collection");
			return;
		}

		if (cmd.contentEquals("playVideo_distrFlashCards")) {
			App.mg.openWebPage("https://www.youtube.com/watch?v=8xWEyuyViF8");
			return;
		}

		if (cmd.contentEquals("openPage_distrFlashCards")) {
			String chapterPartName = "Distr Flash Cards";
			Book b = App.bookshelfArray.get(0).getBookWithChapterPartName(chapterPartName);
			if (b != null) {
				LinChapter chapter = b.getChapterByDisplayNamePart(chapterPartName);
				if (chapter != null) {
					/* chapterLoaded */chapter.loadWithShow("replaceBookPanel");

					App.frame.splitPaneHorz.setDividerLocation(App.frame.getWidth() - RIGHT_OPT_PANEL_WIDTH__narrow);
					App.frame.rop.setSelectedIndex(App.RopTab_3_DFC);
				}
			}
			return;
		}

		if (cmd.contentEquals("openPage_DefendExpert")) {
			String chapterPartName = "Defend like an Expert";
			Book b = App.bookshelfArray.get(0).getBookWithChapterPartName(chapterPartName);
			if (b != null) {
				LinChapter chapter = b.getChapterByDisplayNamePart(chapterPartName);
				if (chapter != null) {
					/* chapterLoaded */chapter.loadWithShow("replaceBookPanel");
				}
			}
			return;
		}

		if (cmd.contentEquals("openPage_VisualizeHands")) {
			String chapterPartName = "Visualize Hands";
			Book b = App.bookshelfArray.get(0).getBookWithChapterPartName(chapterPartName);
			if (b != null) {
				LinChapter chapter = b.getChapterByDisplayNamePart(chapterPartName);
				if (chapter != null) {
					/* chapterLoaded */chapter.loadWithShow("replaceBookPanel");
				}
			}
			return;
		}

		if (cmd.contentEquals("openPage_ImproveYourBridge")) {
			String chapterPartName = "Improve Your";
			Book b = App.bookshelfArray.get(0).getBookWithChapterPartName(chapterPartName);
			if (b != null) {
				LinChapter chapter = b.getChapterByDisplayNamePart(chapterPartName);
				if (chapter != null) {
					/* chapterLoaded */chapter.loadWithShow("replaceBookPanel");
				}
			}
			return;
		}

		if (cmd.contentEquals("openPage_MemorizeDistributions")) {
			String chapterPartName = "Memorize Suit";
			Book b = App.bookshelfArray.get(0).getBookWithChapterPartName(chapterPartName);
			if (b != null) {
				LinChapter chapter = b.getChapterByDisplayNamePart(chapterPartName);
				if (chapter != null) {
					/* chapterLoaded */chapter.loadWithShow("replaceBookPanel");
				}
			}
			return;
		}

		if (cmd.contentEquals("openPage_BoHaglundDDS")) {
			String chapterPartName = "Bo Haglund";
			Book b = App.bookshelfArray.get(0).getBookWithChapterPartName(chapterPartName);
			if (b != null) {
				LinChapter chapter = b.getChapterByDisplayNamePart(chapterPartName);
				if (chapter != null) {
					/* chapterLoaded */chapter.loadWithShow("replaceBookPanel");
				}
			}
			return;
		}

		if (cmd.contentEquals("openPage_WhatIsaaBridge")) {
			String chapterPartName = "What is aaBridge";
			Book b = App.bookshelfArray.get(0).getBookWithChapterPartName(chapterPartName);
			if (b != null) {
				LinChapter chapter = b.getChapterByDisplayNamePart(chapterPartName);
				if (chapter != null) {
					/* chapterLoaded */chapter.loadWithShow("replaceBookPanel");
				}
			}
			return;
		}

		if (cmd.contentEquals("openPage_DocCollection")) {
			String chapterPartName = "Doc Collection";
			Book b = App.bookshelfArray.get(0).getBookWithChapterPartName(chapterPartName);
			if (b != null) {
				LinChapter chapter = b.getChapterByDisplayNamePart(chapterPartName);
				if (chapter != null) {
					/* chapterLoaded */chapter.loadWithShow("replaceBookPanel");
				}
			}
			return;
		}

		if (cmd.contentEquals("openPage_ListOfInterestingLins")) {
			String chapterPartName = "List of Interesting Lins";
			Book b = App.bookshelfArray.get(0).getBookWithChapterPartName(chapterPartName);
			if (b != null) {
				LinChapter chapter = b.getChapterByDisplayNamePart(chapterPartName);
				if (chapter != null) {
					/* chapterLoaded */chapter.loadWithShow("replaceBookPanel");
				}
			}
			return;
		}

		if (cmd.contentEquals("tutorial_idealSize_small_noExtra")) {
			App.frame.splitPaneHorz.setDividerLocation(App.frame.getWidth());
			App.frame.splitPaneVert.setDividerLocation(App.frame.getHeight());
			Rectangle r = getBounds();
			r.width = macOrLinuxAdjust(BOOK_IDEAL_WIDTH_SMALL__NO_EXTRA); // - RIGHT_OPT_PANEL_WIDTH__narrow ;
			r.height = BOOK_IDEAL_HEIGHT_SMALL__NO_EXTRA; // - BOTTOM_OPT_PANEL_HEIGHT;
			setBounds(r);
			return;
		}

		if (cmd.contentEquals("tutorial_idealSize_small")) {
			App.frame.splitPaneHorz.setDividerLocation(App.frame.getWidth() - RIGHT_OPT_PANEL_WIDTH__narrow - RIGHT_OPT_PANEL_WIDTH__small_wider_adjust_A);
			App.frame.splitPaneVert.setDividerLocation(App.frame.getHeight()); // - BOTTOM_OPT_PANEL_HEIGHT);
			Rectangle r = getBounds();
			r.width = macOrLinuxAdjust(BOOK_IDEAL_WIDTH_SMALL);
			r.height = BOOK_IDEAL_HEIGHT_SMALL;
			setBounds(r);
			return;
		}

		if (cmd.contentEquals("tutorial_idealSize_std_noExtra")) {
			App.frame.splitPaneHorz.setDividerLocation(App.frame.getWidth());
			App.frame.splitPaneVert.setDividerLocation(App.frame.getHeight());
			Rectangle r = getBounds();
			r.width = macOrLinuxAdjust(BOOK_IDEAL_WIDTH_STD__NO_EXTRA); // - RIGHT_OPT_PANEL_WIDTH__narrow ;
			r.height = BOOK_IDEAL_HEIGHT_STD__NO_EXTRA; // - BOTTOM_OPT_PANEL_HEIGHT;
			setBounds(r);
			return;
		}

		if (cmd.contentEquals("tutorial_idealSize_std")) {
			App.frame.splitPaneHorz.setDividerLocation(App.frame.getWidth() - RIGHT_OPT_PANEL_WIDTH__narrow);
			App.frame.splitPaneVert.setDividerLocation(App.frame.getHeight() - BOTTOM_OPT_PANEL_HEIGHT);
			Rectangle r = getBounds();
			r.width = macOrLinuxAdjust(BOOK_IDEAL_WIDTH_STD);
			r.height = BOOK_IDEAL_HEIGHT_STD;
			setBounds(r);
			return;
		}

		if (cmd.contentEquals("tutorial_idealSize_big_noExtra")) {
			App.frame.splitPaneHorz.setDividerLocation(App.frame.getWidth());
			App.frame.splitPaneVert.setDividerLocation(App.frame.getHeight());
			Rectangle r = getBounds();
			r.width = macOrLinuxAdjust(BOOK_IDEAL_WIDTH_BIG__NO_EXTRA); // - RIGHT_OPT_PANEL_WIDTH__narrow ;
			r.height = BOOK_IDEAL_HEIGHT_BIG__NO_EXTRA; // - BOTTOM_OPT_PANEL_HEIGHT;
			setBounds(r);
			return;
		}

		if (cmd.contentEquals("tutorial_idealSize_big")) {
			App.frame.splitPaneHorz.setDividerLocation(App.frame.getWidth() - RIGHT_OPT_PANEL_WIDTH__narrow);
			App.frame.splitPaneVert.setDividerLocation(App.frame.getHeight() - BOTTOM_OPT_PANEL_HEIGHT);
			Rectangle r = getBounds();
			r.width = macOrLinuxAdjust(BOOK_IDEAL_WIDTH_BIG);
			r.height = BOOK_IDEAL_HEIGHT_BIG;
			setBounds(r);
			return;
		}

		if (cmd.contentEquals("tutorial_idealSize_vbig_noExtra")) {
			App.frame.splitPaneHorz.setDividerLocation(App.frame.getWidth());
			App.frame.splitPaneVert.setDividerLocation(App.frame.getHeight());
			Rectangle r = getBounds();
			r.width = macOrLinuxAdjust(BOOK_IDEAL_WIDTH_VBIG__NO_EXTRA); // - RIGHT_OPT_PANEL_WIDTH__narrow ;
			r.height = BOOK_IDEAL_HEIGHT_VBIG__NO_EXTRA; // - BOTTOM_OPT_PANEL_HEIGHT;
			setBounds(r);
			return;
		}

		if (cmd.contentEquals("tutorial_idealSize_vbig")) {
			App.frame.splitPaneHorz.setDividerLocation(App.frame.getWidth() - RIGHT_OPT_PANEL_WIDTH__narrow);
			App.frame.splitPaneVert.setDividerLocation(App.frame.getHeight() - BOTTOM_OPT_PANEL_HEIGHT);
			Rectangle r = getBounds();
			r.width = macOrLinuxAdjust(BOOK_IDEAL_WIDTH_VBIG);
			r.height = BOOK_IDEAL_HEIGHT_VBIG;
			setBounds(r);
			return;
		}

		if (cmd.contentEquals("tutorial_idealSize_vvbig_noExtra")) {
			App.frame.splitPaneHorz.setDividerLocation(App.frame.getWidth());
			App.frame.splitPaneVert.setDividerLocation(App.frame.getHeight());
			Rectangle r = getBounds();
			r.width = macOrLinuxAdjust(BOOK_IDEAL_WIDTH_VVBIG__NO_EXTRA); // - RIGHT_OPT_PANEL_WIDTH__narrow ;
			r.height = BOOK_IDEAL_HEIGHT_VVBIG__NO_EXTRA; // - BOTTOM_OPT_PANEL_HEIGHT;
			setBounds(r);
			return;
		}

		if (cmd.contentEquals("tutorial_idealSize_vvbig")) {
			App.frame.splitPaneHorz.setDividerLocation(App.frame.getWidth() - RIGHT_OPT_PANEL_WIDTH__narrow);
			App.frame.splitPaneVert.setDividerLocation(App.frame.getHeight() - BOTTOM_OPT_PANEL_HEIGHT);
			Rectangle r = getBounds();
			r.width = macOrLinuxAdjust(BOOK_IDEAL_WIDTH_VVBIG);
			r.height = BOOK_IDEAL_HEIGHT_VVBIG;
			setBounds(r);
			return;
		}

		if (cmd.contentEquals("showSeatChoiceOpts_noSizeChange")) {
			App.frame.rop.setSelectedIndex(App.RopTab_2_SeatChoice);
			return;
		}

		if (cmd.contentEquals("showStartUpOpts")) {
			int menuSetRightOptWidth = App.frame.getWidth() - RIGHT_OPT_PANEL_WIDTH__menu;
			App.frame.splitPaneHorz.setDividerLocation(menuSetRightOptWidth);
			App.frame.rop.setSelectedIndex(App.RopTab_7_ShowOptionalBtns);
			return;
		}

		if (cmd.contentEquals("showRedHintsOpts")) {
			int menuSetRightOptWidth = App.frame.getWidth() - RIGHT_OPT_PANEL_WIDTH__menu;
			App.frame.splitPaneHorz.setDividerLocation(menuSetRightOptWidth);
			App.frame.rop.setSelectedIndex(App.RopTab_6_RedHints);
			return;
		}

		int menuSetRightOptWidth = App.frame.getWidth() - RIGHT_OPT_PANEL_WIDTH__menu;

		if (cmd == "rightPanelPrefs0_NewDealChoices") {
			App.frame.splitPaneHorz.setDividerLocation(menuSetRightOptWidth);
			App.frame.rop.setSelectedIndex(App.RopTab_0_NewDealChoices);
			return;
		}
		if (cmd == "rightPanelPrefs1_AutoPlay") {
			App.frame.splitPaneHorz.setDividerLocation(menuSetRightOptWidth);
			App.frame.rop.setSelectedIndex(App.RopTab_1_Autoplay);
			return;
		}
		if (cmd == "rightPanelPrefs2_SeatChoice") {
			App.frame.splitPaneHorz.setDividerLocation(menuSetRightOptWidth);
			App.frame.rop.setSelectedIndex(App.RopTab_2_SeatChoice);
			return;
		}
		if (cmd == "rightPanelPrefs3_DFC") {
			App.frame.splitPaneHorz.setDividerLocation(menuSetRightOptWidth);
			App.frame.rop.setSelectedIndex(App.RopTab_3_DFC);
			return;
		}
		if (cmd == "rightPanelPrefs4_SuitColors") {
			App.frame.splitPaneHorz.setDividerLocation(menuSetRightOptWidth);
			App.frame.rop.setSelectedIndex(App.RopTab_4_SuitColors);
			return;
		}
		if (cmd == "rightPanelPrefs5_DSizeFont") {
			App.frame.splitPaneHorz.setDividerLocation(menuSetRightOptWidth);
			App.frame.rop.setSelectedIndex(App.RopTab_5_DSizeFont);
			return;
		}
		if (cmd == "rightPanelPrefs6_RedHints") {
			App.frame.splitPaneHorz.setDividerLocation(menuSetRightOptWidth);
			App.frame.rop.setSelectedIndex(App.RopTab_6_RedHints);
			return;
		}
		if (cmd == "rightPanelPrefs7_ShowBtns") {
			App.frame.splitPaneHorz.setDividerLocation(menuSetRightOptWidth);
			App.frame.rop.setSelectedIndex(App.RopTab_7_ShowOptionalBtns);
			return;
		}
		if (cmd == "lowerPanel") {
			App.frame.splitPaneVert.setDividerLocation(App.frame.getHeight() - BOTTOM_OPT_PANEL_HEIGHT);
			return;
		}
		if (cmd == "menuHelpHelp") {
			Book b = App.bookshelfArray.get(0).getBookByFrontNumb(91 /* The Standard Help */);
			if (b != null) {
				boolean chapterLoaded = b.loadChapterByIndex(0);
				if (chapterLoaded) {
					App.book = b;
					App.aaBookPanel.matchToAppBook();
					App.aaBookPanel.showChapterAsSelected(0);
				}
			}
			return;
		}
		if (cmd == "menuSwapLinFilePlayer") {
			try {
				Desktop.getDesktop().browse(new java.net.URI("http://musingsonbridge.blogspot.com/2013/08/swap-between-aabridge-and-bbo-as-lin.html"));
			} catch (Exception ev) {
			}
			return;
		}
		if (cmd == "blog_WhatIsaaBridge") {
			try {
				Desktop.getDesktop().browse(new java.net.URI("http://musingsonbridge.blogspot.com/2016/02/what-is-aabridge.html"));
			} catch (Exception ev) {
			}
			return;
		}
		if (cmd == "blog_MemorizeSuitDistrib") {
			try {
				Desktop.getDesktop().browse(new java.net.URI("http://musingsonbridge.blogspot.com/2016/04/memorize-suit-distributions-how-to.html"));
			} catch (Exception ev) {
			}

			return;
		}
		if (cmd == "blog_DownTheRabbitHole") {
			try {
				Desktop.getDesktop().browse(new java.net.URI("http://musingsonbridge.blogspot.com/2016/04/down-rabbit-hole.html"));
			} catch (Exception ev) {
			}
			return;
		}
		if (cmd == "blog_seatChoiceAndThatPinkDot") {
			try {
				Desktop.getDesktop().browse(new java.net.URI("http://musingsonbridge.blogspot.com/2016/04/seat-choice-and-that-pink-dot.html"));
			} catch (Exception ev) {
			}
			return;
		}
		if (cmd == "blog_BboToaaBridge") {
			try {
				Desktop.getDesktop().browse(new java.net.URI("http://musingsonbridge.blogspot.com/2015/11/how-do-i-get-bbo-hand-into-aabridge.html"));
			} catch (Exception ev) {
			}
			return;
		}
		if (cmd == "blog_PlayExistingDeal") {
			try {
				Desktop.getDesktop().browse(new java.net.URI("http://musingsonbridge.blogspot.com/2016/04/play-existing-deal-in-aabridge.html"));
			} catch (Exception ev) {
			}
			return;
		}
		if (cmd == "blog_aaBridgeToBbo") {
			try {
				Desktop.getDesktop().browse(new java.net.URI("http://musingsonbridge.blogspot.com/2015/12/how-do-i-get-aabridge-hands-up-to-bbo.html"));
			} catch (Exception ev) {
			}
			return;
		}
		if (cmd == "blog_TypeIntoaaBridge") {
			try {
				Desktop.getDesktop().browse(new java.net.URI("http://musingsonbridge.blogspot.com/2015/12/how-do-i-enter-type-hands-into-aabridge.html"));
			} catch (Exception ev) {
			}
			return;
		}
		if (cmd == "youtube_SuitDistrib") {
			try {
				Desktop.getDesktop().browse(new java.net.URI("https://www.youtube.com/watch?v=8xWEyuyViF8&list=UUjqx0Cofc7-TT-N0tfYR8rg"));
			} catch (Exception ev) {
			}
			return;
		}
		if (cmd == "blog_VisualizeHands") {
			try {
				Desktop.getDesktop().browse(new java.net.URI("http://musingsonbridge.blogspot.com/2016/06/learn-to-visualize-hands-with-aabridge.html"));
			} catch (Exception ev) {
			}
			return;
		}
		if (cmd == "blog_UseTheDDS") {
			try {
				Desktop.getDesktop().browse(new java.net.URI("http://musingsonbridge.blogspot.com/2015/07/double-dummy-solver-added-to-aabridge.html"));
			} catch (Exception ev) {
			}
			return;
		}
		if (cmd == "blog_debugBboDealerScripts") {
			try {
				Desktop.getDesktop().browse(new java.net.URI("http://musingsonbridge.blogspot.com/2015/09/debugging-bbo-bridge-dealer-scripts.html"));
			} catch (Exception ev) {
			}
			return;
		}
		if (cmd == "web_LookAtBlog") {
			try {
				Desktop.getDesktop().browse(new java.net.URI("http://musingsonbridge.blogspot.com/"));
			} catch (Exception ev) {
			}
			return;
		}
		if (cmd == "web_LookAtWebsite") {
			try {
				Desktop.getDesktop().browse(new java.net.URI("http://rogerpf.com/aaBridge"));
			} catch (Exception ev) {
			}
			return;
		}
		if (cmd == "internal_HelpAbout") {

			java.net.URL imageFileURL = AaaOuterFrame.class.getResource("aaBridge_proto_icon.png");
			final ImageIcon icon = new ImageIcon(imageFileURL);
//			final ImageIcon icon = Toolkit.getDefaultToolkit().createImage(imageFileURL);

			String s = "AaBridge written by Roger Pfister\n\n" + "This is version -  " + VersionAndBuilt.getVer() + "\n" + "Build Number   -               "
					+ VersionAndBuilt.getBuildNo() + "\n" + "Built on              -  " + VersionAndBuilt.getBuilt() + "\n\n" + "see - http://RogerPf.com\n\n";
			;
			JOptionPane.showMessageDialog(this, s, "About - aaBridge", JOptionPane.INFORMATION_MESSAGE, icon);
			return;
		}

		// lastly look and see if we match an internal lin file
		for (Bookshelf shelf : App.bookshelfArray) {
			Book b = shelf.getBookByBasePathAndBookDisplayTitle(cmd);
			if (b != null) {
				boolean chapterLoaded = b.loadChapterByIndex(0);
				if (chapterLoaded) {
					App.book = b;
					App.aaBookPanel.matchToAppBook();
					App.aaBookPanel.showChapterAsSelected(0);
				}
				return;
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

			if (support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				support.setDropAction(COPY);
				return true;
			}

			if (support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				support.setDropAction(COPY);
				return true;
			}

			return false;
		}

		public boolean importData(TransferHandler.TransferSupport support) {

			if (support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {

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

				// is this a single dropped link to a lin file (probably on windows)
				if ((files.length == 1) && files[0].getName().toLowerCase().endsWith(".url")) {
					String s = "";
					try {
						FileInputStream fis = new FileInputStream(files[0]);
						byte[] buf = new byte[1024];
						fis.read(buf);
						s = new String(buf);
						fis.close();
					} catch (IOException e) {
					}

					int from = s.toLowerCase().indexOf("http://");
					int to = s.indexOf('\n', from);
					if (to > 0 && (s.charAt(to - 1) == '\r')) {
						to--;
					}
					if (to < 0 && from > 0)
						to = s.length();

					String u = "";
					if (from > 0 && to > from) {
						u = s.substring(from, to);
						String temp_filename = MassGi_utils.readLinFileFromWebsite(u);
						files[0] = new File(temp_filename);
					}
					else
						return false;
				}

				return BridgeLoader.processDroppedList(files);
			}

			/**
			 *  so no dirs or files or jars (with books) so let try looking for lin's
			 */
			if (support.isDataFlavorSupported(DataFlavor.stringFlavor)) {

				Transferable t = support.getTransferable();
				String s_in, s;
				try {
					// @SuppressWarnings("unchecked")
					s_in = ((String) t.getTransferData(DataFlavor.stringFlavor)).trim();
				} catch (Exception e) {
					return false;
				}

				// drag and dropped lin link (on a MAC)
				s = s_in;
				if (s_in.toLowerCase().contains("http://www.bridgebase.com/")) {
					String temp_filename = MassGi_utils.readLinFileFromWebsite(s);
					if (temp_filename.isEmpty() == false) {
						File[] files = new File[1];
						files[0] = new File(temp_filename);
						return BridgeLoader.processDroppedList(files);
					}
					return false;
				}

				// Drag and dropped Tiny URL Win and MAC
				if (s_in.length() < 40 && s_in.startsWith("http")) { // assume it is a 'tiny url' of some find
					s = MassGi_utils.fetchRedirectedUrl(s_in);
				}
				if (s.length() < 40)
					return false;

				String filename = MassGi_utils.createLinFileFromText(s);

				if (filename.isEmpty())
					return false;

				File[] files = new File[1];
				files[0] = new File(filename);
				return BridgeLoader.processDroppedList(files);
			}

			return false;
		}
	};

	/**
	*/
	public Timer clickPasteTimer = new Timer(10, new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			// =============================================================
			clickPasteTimer.stop();
			clickPasteTimer.setDelay(10);

			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

			Transferable clipData = clipboard.getContents(clipboard);
			String s = "", s_in = "";
			if (clipData != null) {
				try {
					if (clipData.isDataFlavorSupported(DataFlavor.stringFlavor)) {
						s_in = ((String) clipData.getTransferData(DataFlavor.stringFlavor)).trim();
					}
				} catch (Exception ex) {
					return;
				}
			}

			// System.out.println("Clicked - " + s_in);

			s = s_in;
			if (s_in.length() < 40 && s_in.startsWith("http")) { // assume it is a 'tiny url' of some find
				s = MassGi_utils.fetchRedirectedUrl(s_in);
				if (s.length() < 40)
					return;
			}

			String filename = MassGi_utils.createLinFileFromText(s);

			if (filename.isEmpty())
				return;

			File[] files = new File[1];
			files[0] = new File(filename);
			BridgeLoader.processDroppedList(files);
		}
	});

}
