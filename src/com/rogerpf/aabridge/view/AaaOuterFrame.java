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
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.ToolTipManager;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;

import com.rogerpf.aabridge.controller.AaBridge;
import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.Aaf;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.controller.Book;
import com.rogerpf.aabridge.controller.Book.LinChapter;
import com.rogerpf.aabridge.controller.Bookshelf;
import com.rogerpf.aabridge.controller.BookshelfArray;
import com.rogerpf.aabridge.controller.BridgeLoader;
import com.rogerpf.aabridge.controller.CmdHandler;
import com.rogerpf.aabridge.controller.LangdeckList;
import com.rogerpf.aabridge.controller.LanguageList;
import com.rogerpf.aabridge.controller.MruCollection;
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

import net.miginfocom.swing.MigLayout;

/**    
 */
public class AaaOuterFrame extends JFrame implements ComponentListener, ActionListener, ItemListener, WindowListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AaLowerOptionsPanel lop;
	public AaRightOptionsPanel rop;
	public AaFixedRatioPanel fixedRatioPanel;
	AaPayloadPanel payloadPanel;
//	AaPayloadCasePanel plcp;
//	DarkGrayHiddenPanel rjp; // rightJigglePanel
//	DarkGrayHiddenPanel bjp; // bottomJigglePanel

//	public JPanel rrp; // rightRattlePanel
//	public JPanel brp; // bottomRattlePanel

	AaDdlAndPayloadCasePanel linPlcp;

	public JSplitPane splitPaneHorz;
	public JSplitPane splitPaneVert;

	JMenuBar menuBar;
	JMenu menu, submenu;
	JMenuItem menuItem;
	JLabel label;

//	private Action menuSaveStdAction;
//	private Action menuSaveAsAction;

	KeyboardFocusManager kbFocusManager;

	public AaDragGlassPane aaDragGlassPane;

	JMenuItem showUserExampleMenu;

	// ----------------------------------------
	public AaaOuterFrame() { /* Constructor */

		App.frame = this; // I know this is pre setting it *before* it is constructed - but we want access to our OWN constants

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		/**
		 * Check for the emergency reset request
		 */
		if (App.args != null && App.args.length >= 1 && App.args[0] != null && !App.args[0].isEmpty()) {
			String s = App.args[0].trim().toLowerCase();
			if (s.matches("reset")) {
				App.deletePreferencesNode();
				MruCollection.delete_whole_mru();
				App.args[0] = "";
			}
			else if (s.matches("reset1")) {
				App.deletePreferencesNode();
				App.args[0] = "";
			}
		}

		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Cancel"); //$NON-NLS-1$
		getRootPane().getActionMap().put("Cancel", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				System.out.println("Esc pressed");  // help with message dialogs
			}
		});

		/** 
		 * Spin the random number generator - well why not
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
		String flag_folder = "";
		try {
			URL locationMethodUrl = AaBridge.class.getProtectionDomain().getCodeSource().getLocation();
			File locMethodFile = new File(locationMethodUrl.toURI());

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
				// System.out.println("E_2 App.thisAppBaseFolder:" + s);
				if (s.endsWith(File.separator + "bin")) {
					App.thisAppBaseFolder = s.substring(0, s.length() - 3) + "src";
				}
				else {
					App.thisAppBaseFolder = s;
				}
				// assume we are dev running in eclipse or the like in windows
				// and default to the normal install place far aaBridge (testing only)
				flag_folder = "C:\\ProgramSmall\\aaBridge\\";
			}

			if (new File(flag_folder + "_aaBridge_a__su_clear_overide.txt").exists()) {
				App.su_clear_overide = true;
			}

			if (new File(flag_folder + "_aaBridge_b__use_devmode.txt").exists()) {
				App.devMode = true;
			}

			if (new File(flag_folder + "_aaBridge_c__show_dev_test_lins.txt").exists()) {
				App.showDevTestLins = true;
			}

			App.java_info = System.getProperty("java.version");
			String bitSize = System.getProperty("sun.arch.data.model");

			if (bitSize.isEmpty() == false) {
				App.java_info += " (" + bitSize + " bit)";
			}

			String j_vendor = System.getProperty("java.vendor");

			if (j_vendor.isEmpty() == false) {
				j_vendor = "   From: " + j_vendor;
			}
			App.java_info += j_vendor;

			System.out.println("");
			System.out.println("aaBridge_" + VersionAndBuilt.verAndBuildNo() + "   Running on Java: " + App.java_info);

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

			if (new File(flag_folder + "_aaBridge_e__ignore_all_reldates.txt").exists()) {
				App.observeReleaseDates = false;
			}

			if (new File(flag_folder + "_aaBridge_f__use_mru_dev.txt").exists()) {
				App.mruNodeSubNode = "mru_dev";
			}

			if (new File(flag_folder + "_aaBridge_h__write_mini_log.txt").exists()) {
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

			if (new File(flag_folder + "aaBridge__aaa_use_LF.txt").exists()) {
				App.EOLalwaysLF = true;
			}

			File[] list = new File(flag_folder).listFiles();
			DateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");
			String front = "aaBridge__study_deal__";
			int frontLen = front.length();
			int fullLen = front.length() + 10 /* date */ + 4 /*.txt*/;

			App.study_deal_maker = false;

			for (File f : list) {
				if (f.isFile() == false)
					continue;
				String fname = f.getName();
				if (fname.length() != fullLen)
					continue;
				if (fname.substring(0, frontLen).equalsIgnoreCase(front) == false)
					continue;
				if (fname.substring(frontLen + 10).equalsIgnoreCase(".txt") == false)
					continue;
				String dateStr = fname.substring(frontLen, frontLen + 10);

				try {
					Date inDate = sdFormat.parse(dateStr);
					Calendar cal = Calendar.getInstance();
					Date now = new Date() /* now */;
					cal.setTime(now);
					cal.add(Calendar.DATE, 15);
					Date now_plus_15 = cal.getTime();
					if (now.before(inDate) && now_plus_15.after(inDate)) {
						App.study_deal_maker = true;
						break;
					}
				} catch (ParseException e) {
				}
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
		App.runningExpanded = MassGi_utils.isRunningExpanded();

		// App.default_downloads_folder
		if (App.onWin) {
			String homedrive = System.getenv("HOMEDRIVE");
			String homepath = System.getenv("HOMEPATH");
			App.default_downloads_folder = homedrive + homepath + "\\Downloads";
		}
		else {
			String home = System.getenv("home");
			App.default_downloads_folder = home + "/Downloads";
		}
		App.default_downloads_folder += File.separator;

//		assert(App.runningExpanded != App.runningInJar);  // these need to be joined up

		App.bundleSep = (App.runningInJar) ? "/" : File.separator;

		setVisible(false); // set true by the timer below
		java.net.URL imageFileURL = AaaOuterFrame.class.getResource("aaBridge_proto_icon.png");
		setIconImage(Toolkit.getDefaultToolkit().createImage(imageFileURL));

		this.addWindowListener(this);
		this.addComponentListener(this);

		App.loadPreferences();

		Aaf.LoadPrefsPostProcess();

		if (App.startedWithCleanSettings) {
			; // not used but still available
		}

		App.selectMnHeaderColor();

		float magnif = 1.2f;

		try {
			ToolTipManager.sharedInstance().setDismissDelay(12000);

			Font f = (Font) UIManager.get("ToolTip.font");
			Font f2 = f.deriveFont(Font.BOLD, f.getSize() * magnif);
			UIManager.put("ToolTip.font", f2);
			UIManager.put("ToolTip.background", Aaa.tooltipYellow);
		} catch (Exception e) {
		}

		// Active Bo Haglunds DDS
		App.haglundsDDSavailable = Z_ddsCalculate.is_dds_available();

		/** The 'donehand' has already been constructed. Now we create the mg
		 *  to match it.  From now on there will ALWAYS be a valid mg (and lin inside it)
		 */

		App.deal = new Deal(Deal.makeDoneHand, Dir.South);/** The 'donehand' has just been constructed. Now we create the mg
														   *  to match it.  From now on there will ALWAYS be a valid mg (and lin inside it)
														   */
		App.mg = new MassGi(App.deal);

		aaDragGlassPane = new AaDragGlassPane(this);

		setGlassPane(aaDragGlassPane);

		App.bookshelfArray = new BookshelfArray(); // this is the only instance

		App.aaHomeBtnPanel = new AaHomeBtnPanel();

		// -----------------------------------------------------

		// create folders etc
		{
			App.desktop_folder = FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath();
			if (!App.onWin) {
				if (App.desktop_folder.toLowerCase().endsWith("Desktop") == false) {
					App.desktop_folder += File.separator + "Desktop";
				}
			}
			App.desktop_folder += File.separator;
			String user_home = System.getProperty("user.home") + File.separator;
			App.homePath = user_home + "aaBridge" + File.separator;

			App.cached_lins_folder = App.homePath + "cached_lins" + File.separator;
			App.cmdsAndScripts_folder = App.homePath + "cmds_and_scripts" + File.separator;
			App.temp_Other_folder = App.homePath + "temp_Other" + File.separator;
			App.temp_MyHands_folder = App.homePath + "temp_MyHands" + File.separator;

			App.defaultSaves_folder = App.homePath + "saves" + File.separator;
			if (App.realSaves_folder.isEmpty()) {
				App.realSaves_folder = App.defaultSaves_folder;
			}

			App.default_downloads_folder = user_home + "Downloads" + File.separator;
			if (App.downloads_folder.isEmpty()) {
				App.downloads_folder = App.default_downloads_folder;
			}

			File appHome = new File(App.homePath);
			File clns = new File(App.cached_lins_folder);
			File cmds = new File(App.cmdsAndScripts_folder);
			File tmh = new File(App.temp_MyHands_folder);
			File tof = new File(App.temp_Other_folder);
			File defaultSaves = new File(App.defaultSaves_folder);
			File saves = new File(App.realSaves_folder);
			// tests are in with the books

			appHome.mkdir();
			clns.mkdir();
			cmds.mkdir();
			tof.mkdir();
			tmh.mkdir();
			defaultSaves.mkdir(); // so we make the default, even if use have set our own
			saves.mkdir();

			// cleanup some old folders

			try {
				File oldFolderNotWanted = new File(App.homePath + "cmds_and_php");
				MassGi_utils.deleteDirectoryRecursionJava6(oldFolderNotWanted);
			} catch (IOException e1) {
			}

			try {
				File oldFolderNotWanted = new File(App.homePath + "cmds");
				MassGi_utils.deleteDirectoryRecursionJava6(oldFolderNotWanted);
			} catch (IOException e1) {
			}

			try {
				File oldFolderNotWanted = new File(App.homePath + "autosaves");
				MassGi_utils.deleteDirectoryRecursionJava6(oldFolderNotWanted);
			} catch (IOException e1) {
			}
		}

		/*
		 * Using Java properties IO to read in values currently RPf dev use only
		 */
		Properties prop = new Properties();
		String fileName = flag_folder + App.DEV_config_filename;
		InputStream is = null;
		try {
			is = new FileInputStream(fileName);
			try {
				prop.load(is);
				if (App.realSaves_folder.contentEquals(App.defaultSaves_folder)) {
					App.realSaves_folder = prop.getProperty("saves_folder");
				}
				if (App.downloads_folder.contentEquals(App.default_downloads_folder)) {
					App.downloads_folder = prop.getProperty("downloads_folder");
				}
				App.debug_linfile_partner_path = prop.getProperty("debug_linfile_partner_path");
				App.debug_linfile_partner_ext = prop.getProperty("debug_linfile_partner_ext");
				String res = prop.getProperty("debug_suppress_single_undelt");
				if (res.toLowerCase().trim().contentEquals("true")) {
					App.debug_suppress_single_undelt = true;
				}
			} catch (IOException ex) {
			}
		} catch (Exception e) {
//			int z =0;
//			z++;
		}

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

		App.dualDealListBtns = new DualDealListButtonsPanel(); // here because it is hidden by setVisualMode()

		setVisualMode(App.Vm_InsideADeal);

//		rjp = new DarkGrayHiddenPanel_pink();
//		bjp = new DarkGrayHiddenPanel_pink();

//		plcp = new AaPayloadCasePanel();

//		plcp.setLayout(new MigLayout(App.simple, "[grow][]", "[grow][]"));
//		plcp.add(payloadPanel, "growy");
//		plcp.add(rjp, "hidemode 2, growy, wrap");
//		plcp.add(bjp, "hidemode 2, growx, spanx 2");

//		rrp = new DarkGrayHiddenPanel();
//		brp = new DarkGrayHiddenPanel();

		linPlcp = new AaDdlAndPayloadCasePanel();

		linPlcp.setLayout(new MigLayout(App.simple, "", ""));

		linPlcp.add(App.aaHomeBtnPanel, "split2, flowy, hidemode 1");
		linPlcp.add(App.aaBookPanel, "hidemode 1, push, grow, hmin 0");
		linPlcp.add(App.dualDealListBtns, "hidemode 1, growy, center, hmin 0");
//		linPlcp.add(plcp, "growy");
		linPlcp.add(payloadPanel, "growy" /*, "wmax 900" */);

//		linPlcp.add(rrp, "hidemode 2, growy, wrap");
//		linPlcp.add(brp, "hidemode 2, growx, spanx 3");

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

		lop.realSaves_folderNowAvailable();

		setTitleAsRequired();

		// is the standard text font available
		GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
		String[] fonts = g.getAvailableFontFamilyNames();
		App.fontfamilyStandardAvailable = true;
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

		fixedRatioPanel_RATIO.width = (int) (10 * (tutFullHeightPc * tut_widthHeightRatio * App.ratioFiddle) / 100);
		// 1.0012f is a fudge factor (of only around a pixel for normal sizes) which appears to help !
		fixedRatioPanel_RATIO.width = (int) ((float) fixedRatioPanel_RATIO.width * 1.0012f);

		fixedRatioPanel_RATIO.height = (int) 10 * 100;

		/**
		 * full_tut_height / full_tut_width  = full tut ratio
		 * rgb_height / rgb_width            =  rbg ratio
		 * 
		 * as the widths are equal we can calc the percentage of the split tutorial area
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
		 * Calculate the major column layout for the gpb
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

		// @formatter:off
		layOut_columns__gbo = String.format(Locale.US,
				"%.2f%%[%.2f%%]%.2f%%[%.2f%%]%.2f%%", 
				side,     c0,    c1,    c2,    side);
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
		 * hence the use of the impericial method below e.g. - by the fundgfactor
		 */

		final float LOW_GREEN_ROW[] = { 3.4f, 3.3f, 3f, 2.5f, 2f };
		final float SIDE_SPACE[] = { 10.5f, 8.4f, 5.3f, 2.7f, 0f }; // see tutorialDealSize
		final float FUDGE_FACTOR[] = { 230f, 150f, 130f, 70f, 0f }; // to hard to calculate

		// this is the only place where SIDE SPACE is used
		float sideSpacePc = SIDE_SPACE[App.tutorialDealSize];
		float lowGrRowHeightPc = LOW_GREEN_ROW[App.tutorialDealSize]; // percent of gbr !
		float fudgeFactor = FUDGE_FACTOR[App.tutorialDealSize];

		float gbmPc = 100f - 2 * (sideSpacePc);

		// @formatter:off
		layOut_columns__gbr = String.format(Locale.US, "%.2f%%[%.2f%%]%.2f%%", 
				                                    sideSpacePc, gbmPc, sideSpacePc);
		// @formatter:on

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

		AaaMenu.menuSaveStdAction.setEnabled(tutorial == false);
		AaaMenu.menuSaveAsAction.setEnabled(tutorial == false);

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

//			rjp.setVisible(odd);
//			bjp.setVisible(odd);

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

	public void addDroppedShelfToMenu(Bookshelf shelf, boolean forceDisplayTheMenu) {

		if (App.books_E__menu != null)
			App.menuBar.remove(App.books_E__menu);

		App.books_E__menu = shelf.createMenu(this);

		App.books_E__menu.setForeground(Cc.RedStrong);

		reLayoutMenubarEnd();

		if (forceDisplayTheMenu == true) {
			show_E_Menu_timer.start();
		}
	}

	public void reLayoutMenubarEnd() {

		App.menuBar.remove(App.help_menu);
		App.menuBar.remove(App.lang_menu);
		if (App.books_E__menu != null)
			App.menuBar.remove(App.books_E__menu);

		App.menuBar.remove(App.books_B__menu);

		if (App.books_Z__menu != null)
			App.menuBar.remove(App.books_Z__menu);

		// put them all back

		if (App.showLanguageMenu && App.showBooksZMenu && App.books_Z__menu != null)
			App.menuBar.add(App.books_Z__menu);

		App.menuBar.add(App.books_B__menu);

		if (App.books_E__menu != null)
			App.menuBar.add(App.books_E__menu);

		if (App.showLanguageMenu)
			App.menuBar.add(App.lang_menu);

		App.menuBar.add(App.help_menu);

		App.menuBar.revalidate();
		App.menuBar.repaint();
	}

	/** ******************************************************************************
	 */
	Timer show_E_Menu_timer = new Timer(200, new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			show_E_Menu_timer.stop();

			App.books_E__menu.doClick();
		}

	});

	/**   
	 */
	public void createAndAddAllMenus(int droppedBookshelfCount) {
		// ==================================================================
		// Remove the previous Menu bar if any
		setJMenuBar(null);

		// Create the menu bar.
		menuBar = new JMenuBar();
		App.menuBar = menuBar;

		// File - MENU
		menuBar.add(AaaMenu.makeFileMenu(this, Aaf.gT("menubar.file")));

		// Bookmarks - MENU
		menuBar.add(App.bookmarksMenu = new JMenu(" " + Aaf.gT("menubar.bookmarks")));

		// Ideal Size - MENU
		menuBar.add(AaaMenu.makeIdealSizeMenu(this, "  " + Aaf.gT("menubar.idealsize")));

		// Options - MENU
		menuBar.add(AaaMenu.makeOptionsMenu(this, "   " + Aaf.gT("menubar.options")));

		// Books to Books-Z - MENUS
		for (Bookshelf shelf : App.bookshelfArray) {

			JMenu menu = shelf.createMenu(this);
			menuBar.add(menu);

			if (shelf.shelfname.contentEquals("Books-Z")) {
				App.books_Z__menu = menu;
				if (App.showBooksZMenu == false || App.showLanguageMenu == false)
					menuBar.remove(menu); // we did not really want to see it after all
				continue;
			}

			if (shelf.shelfname.contentEquals("Books-B")) {
				App.books_B__menu = menu;
			}

			if (shelf.shelfname.contentEquals("Books-S")) {
				App.books_S__menu = menu;
			}

			if (shelf.shelfname.contentEquals("Books-V")) {
				App.books_V__menu = menu;
			}

		}

		// Language - MENU
		App.lang_menu = AaaMenu.makeLangMenu(this, "      " + Aaf.gT("menubar.launguage")); // Create language choice to the Options menu
		if (App.showLanguageMenu) {
			menuBar.add(App.lang_menu); // add language choice to the menubar
		}

		// Help - MENU
		menuBar.add(App.help_menu = AaaMenu.makeHelpMenu(this, this, "   " + Aaf.gT("menubar.help")));

		setJMenuBar(menuBar);

	}

	/**
	*/
	public void actionPerformed(ActionEvent e) {
		// =============================================================

		executeCmd(e.getActionCommand());
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		// =============================================================
		if (App.allConstructionComplete == false)
			return;

		boolean restart = false;

		JMenuItem item = (JMenuItem) e.getItem();

		String prevLang = Aaf.iso_lang_req;

		for (LanguageList.LangEntry entry : Aaf.langList) {
			if (entry.menuItem == item) {
				Aaf.iso_lang_req = entry.iso_lang;
			}
		}

		String prevDeckLang = Aaf.iso_deck_lang;
		for (LangdeckList.LangEntry entry : Aaf.rankList) {
			if (entry.menuItem == item) {
				Aaf.iso_deck_lang = entry.iso_lang;
			}
		}

		if (prevDeckLang.contentEquals(Aaf.iso_deck_lang) == false) {
			restart = true;
		}
		else if (!Aaf.iso_lang_req.equals(prevLang)) {
			restart = true;
		}
		else if (item == AaaMenu.exampleMenuShowHide) {
			App.showBooksZMenu = item.isSelected();
			App.frame.reLayoutMenubarEnd();
			//restart = true;
		}
		else if (item == AaaMenu.languageMenuShowHide) {
			App.showLanguageMenu = item.isSelected();
			App.frame.reLayoutMenubarEnd();
			// restart = true;
		}
		else if (item == AaaMenu.theDotTest) {
			App.showTheDotTest = item.isSelected();
			restart = true;
		}
		else if (e.getStateChange() == ItemEvent.DESELECTED) {
			return;
		}

		if (restart) {
			App.savePreferences();

			if (System.getenv("rpf_in_eclipse") == null) {
				MassGi_utils.launch_2nd_aaBridge_WITH(App.args);
			}

			System.exit(0); // SHUTS DOWN aaBridge NOW
		}

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

//	public int RIGHT_OPT_PANEL_WIDTH__narrow = 128;
//	public int RIGHT_OPT_PANEL_WIDTH__menu = 210;
//	public int RIGHT_OPT_PANEL_WIDTH__wide = 320;
//	public int BOTTOM_OPT_PANEL_HEIGHT = 128;
//
//	public int RIGHT_OPT_PANEL_WIDTH__small_wider_adjust_A = 135;
//	public int RIGHT_OPT_PANEL_WIDTH__small_wider_adjust_B = 170;
//
//	int WIDTH_EXTRA = 100;
//	int HEIGHT_EXTRA = 42;
//
//	public int BOOK_IDEAL_WIDTH_STD__NO_EXTRA = 1176;
//	public int BOOK_IDEAL_HEIGHT_STD__NO_EXTRA = 874;
//
//	public int BOOK_IDEAL_WIDTH_STD = BOOK_IDEAL_WIDTH_STD__NO_EXTRA + WIDTH_EXTRA;
//	public int BOOK_IDEAL_HEIGHT_STD = BOOK_IDEAL_HEIGHT_STD__NO_EXTRA + HEIGHT_EXTRA;
//
//	int WIDTH_SMALL_MINUS = 66;
//	int HEIGHT_SMALL_MINUS = 90;
//
//	public int BOOK_IDEAL_WIDTH_SMALL__NO_EXTRA = BOOK_IDEAL_WIDTH_STD__NO_EXTRA - WIDTH_SMALL_MINUS;
//	public int BOOK_IDEAL_HEIGHT_SMALL__NO_EXTRA = BOOK_IDEAL_HEIGHT_STD__NO_EXTRA - HEIGHT_SMALL_MINUS;
//
//	public int BOOK_IDEAL_WIDTH_SMALL = BOOK_IDEAL_WIDTH_SMALL__NO_EXTRA + WIDTH_EXTRA + RIGHT_OPT_PANEL_WIDTH__small_wider_adjust_B;
//	public int BOOK_IDEAL_HEIGHT_SMALL = BOOK_IDEAL_HEIGHT_SMALL__NO_EXTRA; // + HEIGHT_EXTRA; reduced height on row res laptops
//
//	int WIDTH_BIG_PLUS = 66;
//	int HEIGHT_BIG_PLUS = 40;
//
//	public int BOOK_IDEAL_WIDTH_BIG__NO_EXTRA = BOOK_IDEAL_WIDTH_STD__NO_EXTRA + WIDTH_BIG_PLUS;
//	public int BOOK_IDEAL_HEIGHT_BIG__NO_EXTRA = BOOK_IDEAL_HEIGHT_STD__NO_EXTRA + HEIGHT_BIG_PLUS;
//
//	public int BOOK_IDEAL_WIDTH_BIG = BOOK_IDEAL_WIDTH_BIG__NO_EXTRA + WIDTH_EXTRA;
//	public int BOOK_IDEAL_HEIGHT_BIG = BOOK_IDEAL_HEIGHT_BIG__NO_EXTRA + HEIGHT_EXTRA;
//
//	int WIDTH_VBIG_PLUS = 116;
//	int HEIGHT_VBIG_PLUS = 80;
//
//	public int BOOK_IDEAL_WIDTH_VBIG__NO_EXTRA = BOOK_IDEAL_WIDTH_STD__NO_EXTRA + WIDTH_VBIG_PLUS;
//	public int BOOK_IDEAL_HEIGHT_VBIG__NO_EXTRA = BOOK_IDEAL_HEIGHT_STD__NO_EXTRA + HEIGHT_VBIG_PLUS;
//
//	public int BOOK_IDEAL_WIDTH_VBIG = BOOK_IDEAL_WIDTH_VBIG__NO_EXTRA + WIDTH_EXTRA;
//	public int BOOK_IDEAL_HEIGHT_VBIG = BOOK_IDEAL_HEIGHT_VBIG__NO_EXTRA + HEIGHT_EXTRA;
//
//	int WIDTH_VVBIG_PLUS = 176;
//	int HEIGHT_VVBIG_PLUS = 120;
//
//	public int BOOK_IDEAL_WIDTH_VVBIG__NO_EXTRA = BOOK_IDEAL_WIDTH_STD__NO_EXTRA + WIDTH_VVBIG_PLUS;
//	public int BOOK_IDEAL_HEIGHT_VVBIG__NO_EXTRA = BOOK_IDEAL_HEIGHT_STD__NO_EXTRA + HEIGHT_VVBIG_PLUS;
//
//	public int BOOK_IDEAL_WIDTH_VVBIG = BOOK_IDEAL_WIDTH_VVBIG__NO_EXTRA + WIDTH_EXTRA;
//	public int BOOK_IDEAL_HEIGHT_VVBIG = BOOK_IDEAL_HEIGHT_VVBIG__NO_EXTRA + HEIGHT_EXTRA;

	int macOrLinuxAdjust(int orig) {
		// =============================================================
		return orig + (App.onMacOrLinux ? 40 : 0);
	}

	/**
	*/
	public void executeCmd(String cmd) {
		// =============================================================

		if (cmd.contentEquals("exit")) {

			App.exitAndRelaunch(); // never returns
		}

		if (cmd.contentEquals("acceptPaste")) {

			App.frame.rightClickPasteTimer.start();
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
			// App.dlaeValue = App.dlae_Declarer // or invalid ? or active off
			App.dualDealListBtns.setVisible(false);
			AaaMenu.checkAndSetMininumOptionsPanelSize();
			App.frame.rop.setSelectedIndex(App.RopTab_0_NewDealChoices);
			App.book = new Book(); // which will be empty
			App.aaBookPanel.matchToAppBook();
			App.setVisualMode(App.Vm_InsideADeal);
			App.setMode(Aaa.NORMAL_ACTIVE);
			CmdHandler.playBridgeBlueCenter();
			return;
		}

		if (cmd.contentEquals("openPage_Interesting_Deals")) {
			String chapterPartName = "Interesting";
			Book b = App.bookshelfArray.get(0).getBookWithChapterPartName(chapterPartName);
			if (b != null) {
				LinChapter chapter = b.getChapterByDisplayNamePart(chapterPartName);
				if (chapter != null) {
					/* chapterLoaded */chapter.loadWithShow("replaceBookPanel");
				}
			}
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
					AaaMenu.checkAndSetMininumOptionsPanelSize();
					App.frame.rop.setSelectedIndex(App.RopTab_3_DFC);
				}
			}
			return;
		}

		if (cmd.contentEquals("openPage_pathToAdvanced")) {
			String chapterPartName = "Path to Advanced";
			Book b = App.bookshelfArray.get(0).getBookWithChapterPartName(chapterPartName);
			if (b != null) {
				LinChapter chapter = b.getChapterByDisplayNamePart(chapterPartName);
				if (chapter != null) {
					/* chapterLoaded */chapter.loadWithShow("replaceBookPanel");
				}
			}
			return;
		}

//		if (cmd.contentEquals("openPage_DefendExpert")) {
//			String chapterPartName = "Defend like an Expert";
//			Book b = App.bookshelfArray.get(0).getBookWithChapterPartName(chapterPartName);
//			if (b != null) {
//				LinChapter chapter = b.getChapterByDisplayNamePart(chapterPartName);
//				if (chapter != null) {
//					/* chapterLoaded */chapter.loadWithShow("replaceBookPanel");
//				}
//			}
//			return;
//		}

		if (cmd.contentEquals("openPage_CountingDeclarer")) {
			String chapterPartName = "Counting Declarer";
			Book b = App.bookshelfArray.get(0).getBookWithChapterPartName(chapterPartName);
			if (b != null) {
				LinChapter chapter = b.getChapterByDisplayNamePart(chapterPartName);
				if (chapter != null) {
					/* chapterLoaded */chapter.loadWithShow("replaceBookPanel");
				}
			}
			return;
		}

//		if (cmd.contentEquals("openPage_VisualizeHands")) {
//			String chapterPartName = "Visualize Hands";
//			Book b = App.bookshelfArray.get(0).getBookWithChapterPartName(chapterPartName);
//			if (b != null) {
//				LinChapter chapter = b.getChapterByDisplayNamePart(chapterPartName);
//				if (chapter != null) {
//					/* chapterLoaded */chapter.loadWithShow("replaceBookPanel");
//				}
//			}
//			return;
//		}

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

		if (cmd.contentEquals("openPage_Play_Kib_DDS")) {
			String chapterPartName = "Play  Kib  DDS";
			Book b = App.bookshelfArray.get(0).getBookWithChapterPartName(chapterPartName);
			if (b != null) {
				LinChapter chapter = b.getChapterByDisplayNamePart(chapterPartName);
				if (chapter != null) {
					/* chapterLoaded */chapter.loadWithShow("replaceBookPanel");
				}
			}
			return;
		}

		if (cmd.contentEquals("openPage_Practice")) {
			String chapterPartName = "Practice";
			Book b = App.bookshelfArray.get(0).getBookWithChapterPartName(chapterPartName);
			if (b != null) {
				LinChapter chapter = b.getChapterByDisplayNamePart(chapterPartName);
				if (chapter != null) {
					/* chapterLoaded */chapter.loadWithShow("replaceBookPanel");
				}
			}
			return;
		}

		if (cmd.contentEquals("openPage_bboHandsToaaBridge")) {
			String chapterPartName = "BBO hands to";
			Book b = App.bookshelfArray.get(0).getBookWithChapterPartName(chapterPartName);
			if (b != null) {
				LinChapter chapter = b.getChapterByDisplayNamePart(chapterPartName);
				if (chapter != null) {
					/* chapterLoaded */chapter.loadWithShow("replaceBookPanel");
				}
			}
			return;
		}

		if (cmd.contentEquals("openPage_Save_n_Send")) {
			String chapterPartName = "Save n Send";
			Book b = App.bookshelfArray.get(0).getBookWithChapterPartName(chapterPartName);
			if (b != null) {
				LinChapter chapter = b.getChapterByDisplayNamePart(chapterPartName);
				if (chapter != null) {
					/* chapterLoaded */chapter.loadWithShow("replaceBookPanel");
				}
			}
			return;
		}

		if (cmd.contentEquals("openPage_Courses")) {
			String chapterPartName = "Courses";
			Book b = App.bookshelfArray.get(0).getBookWithChapterPartName(chapterPartName);
			if (b != null) {
				LinChapter chapter = b.getChapterByDisplayNamePart(chapterPartName);
				if (chapter != null) {
					/* chapterLoaded */chapter.loadWithShow("replaceBookPanel");
				}
			}
			return;
		}

		if (cmd.contentEquals("localize-how-to")) {
			String chapterPartName = "localize";
			Book b = App.bookshelfArray.get(0).getBookWithChapterPartName(chapterPartName);
			if (b != null) {
				LinChapter chapter = b.getChapterByDisplayNamePart(chapterPartName);
				if (chapter != null) {
					/* chapterLoaded */chapter.loadWithShow("replaceBookPanel");
				}
			}
			return;
		}

		if (cmd.contentEquals("openPage_UseDealerScripts")) {
			String chapterPartName = "Use Dealer Scripts";
			Book b = App.bookshelfArray.get(0).getBookWithChapterPartName(chapterPartName);
			if (b != null) {
				LinChapter chapter = b.getChapterByDisplayNamePart(chapterPartName);
				if (chapter != null) {
					/* chapterLoaded */chapter.loadWithShow("replaceBookPanel");
				}
			}
			return;
		}

		if (cmd.contentEquals("openPage_BBO_to_aaBridge")) {
			String chapterPartName = "BBO hands to aaBridge";
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

		if (cmd.contentEquals("openPage_LinFilesAStarter")) {
			String chapterPartName = "Lin Files - a Starter";
			Book b = App.bookshelfArray.get(0).getBookWithChapterPartName(chapterPartName);
			if (b != null) {
				LinChapter chapter = b.getChapterByDisplayNamePart(chapterPartName);
				if (chapter != null) {
					/* chapterLoaded */chapter.loadWithShow("replaceBookPanel");
				}
			}
			return;
		}

		if (cmd.contentEquals("openPage_Deal_and_Merge")) {
			String chapterPartName = "Deal and Merge";
			Book b = App.bookshelfArray.get(0).getBookWithChapterPartName(chapterPartName);
			if (b != null) {
				LinChapter chapter = b.getChapterByDisplayNamePart(chapterPartName);
				if (chapter != null) {
					/* chapterLoaded */chapter.loadWithShow("replaceBookPanel");
				}
			}
			return;
		}

//		if (cmd.contentEquals("openPage_KeyDocuments")) {
//		String chapterPartName = "Key Documents";
//		Book b = App.bookshelfArray.get(0).getBookWithChapterPartName(chapterPartName);
//		if (b != null) {
//			LinChapter chapter = b.getChapterByDisplayNamePart(chapterPartName);
//			if (chapter != null) {
//				/* chapterLoaded */chapter.loadWithShow("replaceBookPanel");
//			}
//		}
//		return;
//	}

		if (cmd == "openPage_DownRabbitHole") {
			String chapterPartName = "Down Rabbit Hole";
			for (Bookshelf shelf : App.bookshelfArray) {
				Book b = shelf.getBookWithChapterPartName(chapterPartName);
				if (b != null) {
					LinChapter chapter = b.getChapterByDisplayNamePart(chapterPartName);
					if (chapter != null) {
						/* chapterLoaded */chapter.loadWithShow("replaceBookPanel");
					}
				}
			}
			return;
		}

		if (cmd == "openPage_BBOHandsToaaBridge") {
			String chapterPartName = "BBO hands to";
			for (Bookshelf shelf : App.bookshelfArray) {
				Book b = shelf.getBookWithChapterPartName(chapterPartName);
				if (b != null) {
					LinChapter chapter = b.getChapterByDisplayNamePart(chapterPartName);
					if (chapter != null) {
						/* chapterLoaded */chapter.loadWithShow("replaceBookPanel");
					}
				}
			}
			return;
		}

		if (cmd == "openPage_OCP_intro") {
			String chapterPartName = "OCP Intro";
			for (Bookshelf shelf : App.bookshelfArray) {
				Book b = shelf.getBookWithChapterPartName(chapterPartName);
				if (b != null) {
					LinChapter chapter = b.getChapterByDisplayNamePart(chapterPartName);
					if (chapter != null) {
						/* chapterLoaded */chapter.loadWithShow("replaceBookPanel");
					}
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

		if (cmd.contentEquals("openPage_OCPQuickTables")) {
			String chapterPartName = "Quick Tables";
			for (Bookshelf shelf : App.bookshelfArray) {
				Book b = shelf.getBookWithChapterPartName(chapterPartName);
				if (b != null) {
					LinChapter chapter = b.getChapterByDisplayNamePart(chapterPartName);
					if (chapter != null) {
						/* chapterLoaded */chapter.loadWithShow("replaceBookPanel");
					}
				}
			}
			return;
		}

		if (cmd.contentEquals("set_size_1")) {
			AaaMenu.actionSetSize(1);
			return;
		}

		if (cmd.contentEquals("set_size_2")) {
			AaaMenu.actionSetSize(2);
			return;
		}

		if (cmd.contentEquals("set_size_3")) {
			AaaMenu.actionSetSize(3);
			return;
		}

		if (cmd.contentEquals("set_size_4")) {
			AaaMenu.actionSetSize(4);
			return;
		}

		if (cmd.contentEquals("set_size_5")) {
			AaaMenu.actionSetSize(5);
			return;
		}

		if (cmd.contentEquals("set_size_6")) {
			AaaMenu.actionSetSize(6);
			return;
		}

		if (cmd.contentEquals("set_size_7")) {
			AaaMenu.actionSetSize(7);
			return;
		}

		if (cmd.contentEquals("set_size_8")) {
			AaaMenu.actionSetSize(8);
			return;
		}

		if (cmd.contentEquals("set_size_9")) {
			AaaMenu.actionSetSize(9);
			return;
		}

		if (cmd.contentEquals("set_size_0")) {
			AaaMenu.actionSetSize(10);
			return;
		}

		if (cmd.contentEquals("set_size_user_A")) {
			AaaMenu.actionUserSizeEntry("A");
			return;
		}

		if (cmd.contentEquals("set_size_user_B")) {
			AaaMenu.actionUserSizeEntry("B");
			return;
		}

		if (cmd.contentEquals("set_size_user_C")) {
			AaaMenu.actionUserSizeEntry("C");
			return;
		}

		if (cmd.contentEquals("showStartUpOpts")) {
			AaaMenu.checkAndSetMininumOptionsPanelSize();
			App.frame.rop.setSelectedIndex(App.RopTab_7_ShowOptionalBtns);
			return;
		}

		if (cmd.contentEquals("showRedHintsOpts")) {
			AaaMenu.checkAndSetMininumOptionsPanelSize();
			App.frame.rop.setSelectedIndex(App.RopTab_6_RedHints);
			return;
		}

		if (cmd == "rightPanelPrefs0_NewDealChoices") {
			AaaMenu.checkAndSetMininumOptionsPanelSize();
			App.frame.rop.setSelectedIndex(App.RopTab_0_NewDealChoices);
			return;
		}

		if (cmd == "rightPanelPrefs1_AutoPlay") {
			AaaMenu.checkAndSetMininumOptionsPanelSize();
			App.frame.rop.setSelectedIndex(App.RopTab_1_Autoplay);
			return;
		}

		if (cmd == "rightPanelPrefs2_KibSeat") {
			AaaMenu.checkAndSetMininumOptionsPanelSize();
			App.frame.rop.setSelectedIndex(App.RopTab_2_KibSeat);
			return;
		}

		if (cmd == "rightPanelPrefs3_DFC") {
			AaaMenu.checkAndSetMininumOptionsPanelSize();
			App.frame.rop.setSelectedIndex(App.RopTab_3_DFC);
			return;
		}

		if (cmd == "rightPanelPrefs4_SuitColors") {
			AaaMenu.checkAndSetMininumOptionsPanelSize();
			App.frame.rop.setSelectedIndex(App.RopTab_4_SuitColors);
			return;
		}

		if (cmd == "rightPanelPrefs5_DSizeFont") {
			AaaMenu.checkAndSetMininumOptionsPanelSize();
			App.frame.rop.setSelectedIndex(App.RopTab_5_DSizeFont);
			return;
		}

		if (cmd == "rightPanelPrefs6_RedHints") {
			AaaMenu.checkAndSetMininumOptionsPanelSize();
			App.frame.rop.setSelectedIndex(App.RopTab_6_RedHints);
			return;
		}

		if (cmd == "rightPanelPrefs7_ShowBtns") {
			AaaMenu.checkAndSetMininumOptionsPanelSize();
			App.frame.rop.setSelectedIndex(App.RopTab_7_ShowOptionalBtns);
			return;
		}

		if (cmd == "lowerPanel") {
			App.frame.splitPaneVert.setDividerLocation(App.frame.getHeight() - 3 * AaaMenu.L_STD);
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

		if (cmd == "youtube_SuitDistrib") {
			try {
				Desktop.getDesktop().browse(new java.net.URI("https://www.youtube.com/watch?v=8xWEyuyViF8&list=UUjqx0Cofc7-TT-N0tfYR8rg"));
			} catch (Exception ev) {
			}
			return;
		}

		if (cmd == "youtube_FontsTooSmall") {
			try {
				Desktop.getDesktop().browse(new java.net.URI("https://www.youtube.com/watch?v=6pM6QQ2vUXY&t=26s"));
			} catch (Exception ev) {
			}
			return;
		}

		if (cmd == "youtube_aaBridgeVideos") {
			try {
				Desktop.getDesktop().browse(new java.net.URI("https://www.youtube.com/channel/UCjqx0Cofc7-TT-N0tfYR8rg"));
			} catch (Exception ev) {
			}
			return;
		}

		if (cmd == "menuSwapLinFilePlayer") {
			try {
				Desktop.getDesktop().browse(new java.net.URI("http://musingsonbridge.rogerpf.com/2013/08/swap-between-aabridge-and-bbo-as-lin.html"));
			} catch (Exception ev) {
			}
			return;
		}

		if (cmd == "blog_WhatIsaaBridge") {
			try {
				Desktop.getDesktop().browse(new java.net.URI("http://musingsonbridge.rogerpf.com/2016/02/what-is-aabridge.html"));
			} catch (Exception ev) {
			}
			return;
		}

		if (cmd == "blog_MemorizeSuitDistrib") {
			try {
				Desktop.getDesktop().browse(new java.net.URI("http://musingsonbridge.rogerpf.com/2016/04/memorize-suit-distributions-how-to.html"));
			} catch (Exception ev) {
			}

			return;
		}

		if (cmd == "blog_kibSeatAndThatPinkDot") {
			try {
				Desktop.getDesktop().browse(new java.net.URI("http://musingsonbridge.rogerpf.com/2016/04/seat-choice-and-that-pink-dot.html"));
			} catch (Exception ev) {
			}
			return;
		}

		if (cmd == "blog_BboToaaBridge") {
			try {
				Desktop.getDesktop().browse(new java.net.URI("http://musingsonbridge.rogerpf.com/2015/11/how-do-i-get-bbo-hand-into-aabridge.html"));
			} catch (Exception ev) {
			}
			return;
		}

		if (cmd == "blog_PlayExistingDeal") {
			try {
				Desktop.getDesktop().browse(new java.net.URI("http://musingsonbridge.rogerpf.com/2016/04/play-existing-deal-in-aabridge.html"));
			} catch (Exception ev) {
			}
			return;
		}

		if (cmd == "blog_aaBridgeToBbo") {
			try {
				Desktop.getDesktop().browse(new java.net.URI("http://musingsonbridge.rogerpf.com/2015/12/how-do-i-get-aabridge-hands-up-to-bbo.html"));
			} catch (Exception ev) {
			}
			return;
		}

		if (cmd == "blog_TypeIntoaaBridge") {
			try {
				Desktop.getDesktop().browse(new java.net.URI("http://musingsonbridge.rogerpf.com/2015/12/how-do-i-enter-type-hands-into-aabridge.html"));
			} catch (Exception ev) {
			}
			return;
		}

		if (cmd == "blog_VisualizeHands") {
			try {
				Desktop.getDesktop().browse(new java.net.URI("http://musingsonbridge.rogerpf.com/2016/06/learn-to-visualize-hands-with-aabridge.html"));
			} catch (Exception ev) {
			}
			return;
		}

		if (cmd == "blog_UseTheDDS") {
			try {
				Desktop.getDesktop().browse(new java.net.URI("http://musingsonbridge.rogerpf.com/2015/07/double-dummy-solver-added-to-aabridge.html"));
			} catch (Exception ev) {
			}
			return;
		}

		if (cmd == "blog_debugBboDealerScripts") {
			try {
				Desktop.getDesktop().browse(new java.net.URI("http://musingsonbridge.rogerpf.com/2015/09/debugging-bbo-bridge-dealer-scripts.html"));
			} catch (Exception ev) {
			}
			return;
		}

		if (cmd == "web_LookAtBlog") {
			try {
				Desktop.getDesktop().browse(new java.net.URI("http://musingsonbridge.rogerpf.com/"));
			} catch (Exception ev) {
			}
			return;
		}

		if (cmd == "web_OCPQuickTables") {
			try {
				Desktop.getDesktop().browse(new java.net.URI("http://rogerpf.com/ocp"));
			} catch (Exception ev) {
			}
			return;
		}

		if (cmd == "web_OCPWebsite") {
			try {
				Desktop.getDesktop().browse(new java.net.URI("http://ocp.pigpen.org.uk/"));
			} catch (Exception ev) {
			}
			return;
		}

		if (cmd == "web_OCPVideos") {
			try {
				Desktop.getDesktop().browse(new java.net.URI("https://www.youtube.com/channel/UCTGNUS8jbx85QOMUPlHoRMA"));
			} catch (Exception ev) {
			}
			return;
		}

		if (cmd == "web_ArchiveGoogleDrive") {
			try {
				Desktop.getDesktop().browse(new java.net.URI(
						"https://drive.google.com/drive/folders/0B8ErhAQp22E4WjFZTGJwWVVUQ1U?resourcekey=0-3wVcg0NeRdJ0uvEVb13R3Q&usp=sharing"));
			} catch (Exception ev) {
			}
			return;
		}

		if (cmd == "web_aaBridgeFacebook") {
			try {
				Desktop.getDesktop().browse(new java.net.URI("https://facebook.com/aaBridge"));
			} catch (Exception ev) {
			}
			return;
		}

		if (cmd == "web_aaBridgeWebSite") {

			String url = "http://rogerpf.com/aaBridge";
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date today = new Date();

				String date_url_expires = "2025-11-01";
				// String date_url_expires = "2019-11-01";  // for testing only

				if (today.after(sdf.parse(date_url_expires))) {
					// so we go direct to the google sites site
					url = "https://sites.google.com/view/rogerpf-com/aaBridge";
				}
			} catch (Exception e1) {
			}

			try {
				Desktop.getDesktop().browse(new java.net.URI(url));
			} catch (Exception ev) {
			}
			return;
		}

		if (cmd == "web_aaBridgeAnnouncements") {
			try {
				Desktop.getDesktop().browse(new java.net.URI("https://groups.google.com/forum/#!forum/aabridge-announcements"));
			} catch (Exception ev) {
			}
			return;
		}

		if (cmd == "web_aaBridgeUsersGroup") {
			try {
				Desktop.getDesktop().browse(new java.net.URI("https://groups.google.com/forum/#!forum/aabridge-users"));
			} catch (Exception ev) {
			}
			return;
		}

		if (cmd == "internal_HelpAbout") {

			java.net.URL imageFileURL = AaaOuterFrame.class.getResource("aaBridge_proto_icon.png");
			final ImageIcon icon = new ImageIcon(imageFileURL);

			String s = "      ";

			String t = "aaBridge written by Roger Pfister\n\n" + "This is version -  " + VersionAndBuilt.getVer() + "\n" + "Build Number   -               "
					+ VersionAndBuilt.getBuildNo() + "\n" + "Built on              -  " + VersionAndBuilt.getBuilt() + "\n\n\n" + "Thanks go to\n" + s
					+ "Charlene Gallaty\n" + s + "Sanja A\n" + s + "Filiz Sarıoğlu\n";

			JOptionPane.showMessageDialog(this, t, "About - aaBridge", JOptionPane.INFORMATION_MESSAGE, icon);
			return;
		}

		// lastly look and see if we match an internal lin file
		for (Bookshelf shelf : App.bookshelfArray) {
			Book book = shelf.getBookByBasePathAndBookDisplayTitle(cmd);
			if (book != null) {
				int chap_ind = 0;
				if (cmd.contains("Books-E") && book.frontNumber != 0) {
					int p = cmd.indexOf("Books-E");
					if (p >= 0) {
						char c = cmd.charAt(p + 7);
						if ('0' <= c && c <= '9') {
							chap_ind = c - '0';
						}
					}
				}
				boolean chapterLoaded = book.loadChapterByIndex(chap_ind);
				if (chapterLoaded) {
					App.book = book;
					App.aaBookPanel.matchToAppBook();
					App.aaBookPanel.showChapterAsSelected(chap_ind);
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

		if (App.frameDividersChangeWanted) {
			App.frameDividersChangeWanted = false;
			App.frame.splitPaneHorz.setDividerLocation(App.horzDividerLocation);
			App.frame.splitPaneVert.setDividerLocation(App.vertDividerLocation);
		}
	}

	public void windowClosing(WindowEvent e) {
		App.savePreferences();
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

	private final static String myhands_fetchlin = "www.bridgebase.com/myhands/fetchlin.php?id=";
	private final static String handviewer_myhand = "www.bridgebase.com/tools/handviewer.html?bbo=y&myhand=M-";
//	private final static String handviewer_lin = "www.bridgebase.com/tools/handviewer.html?lin=";

	/**
	 *  drag and drop support for externally (outside java from host OS) dropped deal files
	 */
	private TransferHandler handler = new TransferHandler() {
		// =============================================================
		private static final long serialVersionUID = 1L;

		public boolean canImport(TransferHandler.TransferSupport support) {

			if (support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				try {
					support.setDropAction(COPY);
				} catch (Exception e) {
				}
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

				boolean found = true;

				Transferable t = support.getTransferable();
				File[] files = null;
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
					found = false;
				}

				if (found) {
					if (App.ddsAnalyserPanelVisible) {
						App.reinstateAnalyser = false;
						CmdHandler.ddsAnalyse(); // to switch it off
					}
					String fnLow0 = files[0].getName().toLowerCase();

					if ((files.length == 1) && fnLow0.endsWith(".url")) {
						// drop through
					}
					else {

						if (fnLow0.endsWith(".html") || fnLow0.endsWith(".htm")) {
							return CmdHandler.lins_from_BBO_html(files[0]);
						}
						else {
							App.lastDroppedList = files.clone();
							return BridgeLoader.processDroppedList(files);
						}
					}

					// This should be a single dropped link, to a lin file ? Most likely we are on windows.
					String s = "";
					try {
						FileInputStream fis = new FileInputStream(files[0]);
						byte[] buf = new byte[8 * 1024];
						fis.read(buf);
						s = new String(buf);
						fis.close();
					} catch (IOException e) {
						return false;
					}

					int from = s.toLowerCase().indexOf("http://");
					if (from == -1) {
						from = s.toLowerCase().indexOf("https://");
					}
					int to = s.indexOf('\n', from);
					if (to > 0 && (s.charAt(to - 1) == '\r')) {
						to--;
					}
					if (to < 0 && from > 0)
						to = s.length();

					String u = "";
					if (from > 0 && to > from) {
						u = s.substring(from, to);
						if (u.contains("linurl=")) {
							u = u.substring(u.indexOf("linurl=") + 7);
						}

						return common_drop_and_right_click(u, "url  drop-or-paste");
					}
					else {
						return false;
					}
				}
			}

			/**
			 *  so no dirs or files or jars (with books) so let try looking for lins or links
			 */
			if (support.isDataFlavorSupported(DataFlavor.stringFlavor)) {

				Transferable t = support.getTransferable();
				String s_in;
				try {
					// @SuppressWarnings("unchecked")
					s_in = ((String) t.getTransferData(DataFlavor.stringFlavor)).trim();
				} catch (Exception e) {
					return false;
				}

				if (App.ddsAnalyserPanelVisible) {
					App.reinstateAnalyser = false;
					CmdHandler.ddsAnalyse(); // to switch it off
				}
				// drag and dropped lin link (on a MAC ?)
				return common_drop_and_right_click(s_in, "lin  drop-or-paste");
			}

			return false;
		}
	};

	/**
	*/
	public Timer rightClickPasteTimer = new Timer(10, new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			// =============================================================
			rightClickPasteTimer.stop();
			rightClickPasteTimer.setDelay(10);

			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

			Transferable clipData = clipboard.getContents(clipboard);
			String s_in = "";
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

			common_drop_and_right_click(s_in, "lin pasted");
		}
	});

	public boolean common_drop_and_right_click(String s_in, String info) {
		// =============================================================

		// Drag and dropped Tiny URL Win and MAC

		String temp_filename = "";

		int ind = -1;

		if (s_in.length() < 40 && s_in.startsWith("http")) { // assume it is a 'tiny url' of some find
			String fetched = MassGi_utils.fetchRedirectedUrl(s_in);
			if (fetched.length() < 40) // take this as a min file min length
				return false;
			temp_filename = MassGi_utils.createLinFileFromText(fetched, info);
		}

		else if ((ind = s_in.indexOf(myhands_fetchlin)) >= 0) {
			temp_filename = MassGi_utils.readLinFileFromWebsite(s_in, info);
		}

		else if ((ind = s_in.indexOf(handviewer_myhand)) >= 0) {
			String rem = s_in.substring(ind + handviewer_myhand.length());
			String ay[] = rem.split("-");
			if (ay.length != 2)
				return false;
			String url = "http://" + myhands_fetchlin + ay[0] + "&when_played=" + ay[1];
			temp_filename = MassGi_utils.readLinFileFromWebsite(url, info);
		}

		else {
			temp_filename = MassGi_utils.createLinFileFromText(s_in, info);

			if (temp_filename.isEmpty()) {
				temp_filename = MassGi_utils.saveStringAsLinFile(s_in, info);
			}
		}

		if (temp_filename.isEmpty()) {
			return false;
		}

//		File[] files = new File[1];
//		files[0] = new File(temp_filename);
//		return BridgeLoader.processDroppedList(files);

		CmdHandler.imp_TempOtherFolder();

		return true;
	}

	static long count = 0;
	static String previous_name = "";
	static long previous_lastmod = 0;

	/**
	*/
	public Timer downloads__scan_timer = new Timer(790, new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			// =============================================================

			if (App.pollDownloadsFolder == false) {
				count = 0;
				previous_name = "";
				previous_lastmod = 0;
				return;
			}

			count++;

			File dl_folder = new File(App.downloads_folder);
			File[] listOfFiles = dl_folder.listFiles();

			long lastmod = 0;
			File chosen = null;

			for (File file : listOfFiles) {
				String low = file.getName().toLowerCase();
				if (file.isFile() && (low.endsWith(".htm") || low.endsWith(".html")) && file.length() > 256) {
					// long this mod
					if (lastmod < file.lastModified()) {
						chosen = file;
						lastmod = file.lastModified();
					}
				}
			}

			if (chosen == null) {
				// count = 0;
				previous_name = "";
				previous_lastmod = 0;
				return;
			}

			long chosen_lastmod = chosen.lastModified();

			boolean action = false;

			if (!previous_name.equals(chosen.getName()))
				action = true;
			else {
				if (previous_lastmod != chosen_lastmod)
					action = true;
			}

			if (action == true) {
				previous_name = chosen.getName();
				previous_lastmod = chosen_lastmod;
				if (count > 1) {
					CmdHandler.lins_from_BBO_html(chosen);
				}
			}

		}
	});

}
