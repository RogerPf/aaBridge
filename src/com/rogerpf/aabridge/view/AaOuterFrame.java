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

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSplitPane;
import javax.swing.Timer;
import javax.swing.TransferHandler;

import net.miginfocom.swing.MigLayout;
import version.VersionAndBuilt;

import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.controller.CmdHandler;
import com.rogerpf.aabridge.model.Deal;
import com.rpsd.ratiolayout.AspectBoundable;
import com.rpsd.ratiolayout.PreferredSizeGridLayout;

/**    
 */
public class AaOuterFrame extends JFrame implements ComponentListener, ActionListener, WindowListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AaLowerOptionsPanel lop;
	public AaRightOptionsPanel rop;
	AaPayloadPanel payloadPanel;
	AaPayloadCasePanel plcp;
	DarkGrayHiddenPanel rjp; // RightJigglePanel
	DarkGrayHiddenPanel bjp; // BottomJigglePanel

	AaLinAndPayloadCasePanel linPlcp;

	public JSplitPane splitPaneHorz;
	public JSplitPane splitPaneVert;

	JMenuBar menuBar;
	JMenu menu, submenu;
	JMenuItem menuItem;
	JRadioButtonMenuItem rbMenuItem;
	JCheckBoxMenuItem cbMenuItem;

	KeyboardFocusManager kbFocusManager;

	public AaDragGlassPane aaDragGlassPane;

	// ----------------------------------------
	public AaOuterFrame() { /* Constructor */
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setVisible(false); // set true by the timer below
		java.net.URL imageFileURL = AaOuterFrame.class.getResource("aaBridge_proto_icon.png");
		setIconImage(Toolkit.getDefaultToolkit().createImage(imageFileURL));

		this.addWindowListener(this);
		this.addComponentListener(this);
		App.loadPreferences();

		App.deal = new Deal(Deal.makeDoneHand, App.youSeatForNewDeal);

		aaDragGlassPane = new AaDragGlassPane(this);
		setGlassPane(aaDragGlassPane);

		// Combine the gbp and the lowButts to make the inner panel
		App.gbp = new GreenBaizePanel();

		// Create the menu bar.
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		// File - MENU
		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(menu);

		// Open
//		menuItem = new JMenuItem("Open  a Saved Deal  -  you can instead just  'Drag and Drop' .aaBridge files", KeyEvent.VK_O);
//		menuItem.setActionCommand("menuOpen");
//		menuItem.addActionListener(App.con);
//		menu.add(menuItem);
//
//		// Save
//		menuItem = new JMenuItem("Save", KeyEvent.VK_S);
//		menuItem.setActionCommand("menuSave");
//		menuItem.addActionListener(App.con);
//		menu.add(menuItem);
//
		// Save Std
		menuItem = new JMenuItem("Save               -  Save using the file name you last set with 'Save As'", KeyEvent.VK_S);
		menuItem.setActionCommand("menuSaveStd");
		menuItem.addActionListener(App.con);
		menu.add(menuItem);

		// Save As
		menuItem = new JMenuItem("Save As        -  Save the deal,  this is the way you get to choose the file name", KeyEvent.VK_A);
		menuItem.setActionCommand("menuSaveAs");
		menuItem.addActionListener(App.con);
		menu.add(menuItem);

//		// Play Wipe
//		menuItem = new JMenuItem("Wipe              -  Wipe the play, for you to play that deal again", KeyEvent.VK_G);
//		menuItem.setActionCommand("menuPlayWipe");
//		menuItem.addActionListener(App.con);
//		menu.add(menuItem);

		menu.addSeparator();

		// Open Saves Folder
		menuItem = new JMenuItem("Open  'saves'  folder       -       THEN    -   use   'Drag and Drop'    to open any deal", KeyEvent.VK_F);
		menuItem.setActionCommand("openSavesFolder");
		menuItem.addActionListener(App.con);
		menu.add(menuItem);

		// Open autoSaves Folder
		menuItem = new JMenuItem("Open  'autosaves'  folder");
		menuItem.setActionCommand("openAutoSavesFolder");
		menuItem.addActionListener(App.con);
		menu.add(menuItem);

		// menu.addSeparator();

//		// Open Results Folder
//		menuItem = new JMenuItem("Open  'results'  folder");
//		menuItem.setActionCommand("openResultsFolder");
//		menuItem.addActionListener(App.con);
//		menu.add(menuItem);
//
//		// Open Tests Folder
//		menuItem = new JMenuItem("Open  'tests'   folder");
//		menuItem.setActionCommand("openTestsFolder");
//		menuItem.addActionListener(App.con);
//		menu.add(menuItem);
//
//		// Run Tests
//		menuItem = new JMenuItem("Run Tests", KeyEvent.VK_T);
//		menuItem.setActionCommand("runTests");
//		menuItem.addActionListener(App.con);
//		menu.add(menuItem);

		// Options - MENU
		menu = new JMenu("Options");
		menu.setMnemonic(KeyEvent.VK_O);
		menuBar.add(menu);

		// Right Panel - Prefs 1 DealChoices
		menuItem = new JMenuItem("Deals  -  Deal Choices", KeyEvent.VK_D);
		menuItem.setActionCommand("rightPanelPrefs1_DealChoices");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		// Right Panel - Prefs 5 Bidding
		menuItem = new JMenuItem("Bids  -  Watching the Bidding", KeyEvent.VK_S);
		menuItem.setActionCommand("rightPanelPrefs5_Bidding");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		// Right Panel - Prefs 2 SeatChoices
		menuItem = new JMenuItem("Seat Choices", KeyEvent.VK_S);
		menuItem.setActionCommand("rightPanelPrefs2_SeatChoice");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		// Right Panel - Prefs 3 AutoPlay
		menuItem = new JMenuItem("AutoPlay  and  Pause  options", KeyEvent.VK_A);
		menuItem.setActionCommand("rightPanelPrefs3_AutoPlay");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		// Right Panel - Prefs 4 StartUp
		menuItem = new JMenuItem("StartUp  and  Button display  options", KeyEvent.VK_U);
		menuItem.setActionCommand("rightPanelPrefs4_StartUp");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menu.addSeparator();

		// Bottom Panel
		menuItem = new JMenuItem("Speed Selection", KeyEvent.VK_P);
		menuItem.setActionCommand("lowerPanel");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		// Help - MENU
		menu = new JMenu("Help");
		menu.setMnemonic(KeyEvent.VK_H);
		menuBar.add(menu);

		// Help general
		menuItem = new JMenuItem("Introduction", KeyEvent.VK_H);
		menuItem.setActionCommand("menuHelpHelp");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menu.addSeparator();

		// Help Swap lin file player
		menuItem = new JMenuItem("How do I  -  Swap between aaBridge and BBO as the (dblclick) .lin file player");
		menuItem.setActionCommand("menuSwapLinFilePlayer");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("How do I  -  Use aaBridge to practice my Hand Counting");
		menuItem.setActionCommand("menuPracticeCounting");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("What is a  -  .Lin file");
		menuItem.setActionCommand("menuWhatIsALinFile");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menu.addSeparator();

		// Help LookAtWebsite
		menuItem = new JMenuItem("Show aaBridge Website - so you can check to see if you have the latest version", KeyEvent.VK_W);
		menuItem.setActionCommand("menuLookAtWebsite");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		// Help About
		menuItem = new JMenuItem("About", KeyEvent.VK_A);
		menuItem.setActionCommand("menuHelpAbout");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		// -----------------------------------------------------

		// Make the inner a fixed ratio (AspectBoundable) so creating the middle panel
		PreferredSizeGridLayout psgl = new PreferredSizeGridLayout(1, 1);
		psgl.setBoundableInterface(new AspectBoundable());
		payloadPanel = new AaPayloadPanel();
		payloadPanel.setLayout(psgl);
		payloadPanel.add(App.gbp);

		rjp = new DarkGrayHiddenPanel();
		bjp = new DarkGrayHiddenPanel();

		plcp = new AaPayloadCasePanel();

		plcp.setLayout(new MigLayout("insets 0 0 0 0, gap 0! 0!", "[grow][]", "[grow][]"));
		plcp.add(payloadPanel, "growx, growy");
		plcp.add(rjp, "hidemode 2, growy, wrap");
		plcp.add(bjp, "hidemode 2, growx, spanx 2");

		linPlcp = new AaLinAndPayloadCasePanel();

		linPlcp.setLayout(new MigLayout("insets 0 0 0 0, gap 0! 0!", "[][grow]", "[grow]"));
		App.linBtns = new AaLinButtonsPanel();
		linPlcp.add(App.linBtns, "hidemode 2, growy, center, hmin 0");
		linPlcp.add(plcp, "growx, growy");

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
		App.testsPath = appHomePath + "tests" + File.separator;
		App.resultsPath = appHomePath + "results" + File.separator;

		File appHome = new File(appHomePath);
		File autoSaves = new File(App.autoSavesPath);
		File saves = new File(App.savesPath);
		File tests = new File(App.testsPath);
		File results = new File(App.resultsPath);

		appHome.mkdir();
		autoSaves.mkdir();
		saves.mkdir();
		tests.mkdir();
		results.mkdir();

		if (App.deleteAutoSaves)
			clearOutOldDealsFromFolder(App.autoSavesPath, 7);

		setTitleAsRequired();

		App.con.postContructionInitTimer.start();

		setTransferHandler(handler);
	}

	static int resizeTicks = 0;
	static int resizeTicksTot = 40;

	/**
	*/
	public void payloadPanelHasResized() {
		// =============================================================
		if (afterPlpResizedTimer.isRunning())
			afterPlpResizedTimer.stop();

		App.gbp.hideClaimButtonsIfShowing();

		afterPlpResizedTimer.start();
		resizeTicks = resizeTicksTot;
	}

	/**
	*/
	public Timer afterPlpResizedTimer = new Timer(100, new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			// =============================================================
			afterPlpResizedTimer.setDelay(50);
			if (resizeTicks == resizeTicksTot)
				App.gbp.kick();

			resizeTicks--;

			boolean odd = (resizeTicks % 2 == 1);

			if (odd && App.gbp.areAllThreeColumnsMatchedInSize(resizeTicks)) {
				afterPlpResizedTimer.stop();
				return;
			}

			rjp.setVisible(odd);
			bjp.setVisible(odd);

			if (resizeTicks <= 0 && !odd)
				afterPlpResizedTimer.stop();
		}
	});

	/**
	*/
	public boolean isSplashTimerRunning() {
		return aaDragGlassPane.splashScreenCompleteTimer.isRunning();
	}

	public void setDragImage(BufferedImage image) {

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

	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd == "rightPanelPrefs1_DealChoices") {
			App.frame.splitPaneHorz.setDividerLocation(App.frame.getWidth() - 320);
			App.frame.rop.setSelectedIndex(0);
		}
		if (cmd == "rightPanelPrefs5_Bidding") {
			App.frame.splitPaneHorz.setDividerLocation(App.frame.getWidth() - 320);
			App.frame.rop.setSelectedIndex(1);
		}
		if (cmd == "rightPanelPrefs2_SeatChoice") {
			App.frame.splitPaneHorz.setDividerLocation(App.frame.getWidth() - 320);
			App.frame.rop.setSelectedIndex(2);
		}
		if (cmd == "rightPanelPrefs3_AutoPlay") {
			App.frame.splitPaneHorz.setDividerLocation(App.frame.getWidth() - 320);
			App.frame.rop.setSelectedIndex(3);
		}
		if (cmd == "rightPanelPrefs4_StartUp") {
			App.frame.splitPaneHorz.setDividerLocation(App.frame.getWidth() - 320);
			App.frame.rop.setSelectedIndex(4);
		}
		else if (cmd == "lowerPanel") {
			App.frame.splitPaneVert.setDividerLocation(App.frame.getHeight() - 130);
		}
		else if (cmd == "menuHelpHelp") {
			new AaHelp();
		}
		else if (cmd == "menuLookAtWebsite") {
			try {
				Desktop.getDesktop().browse(new java.net.URI("http://rogerpf.com/z_bridge_area/bridge/aaBridge.php"));
			} catch (Exception ev) {
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
		else if (cmd == "menuHelpAbout") {
			java.net.URL imageFileURL = AaOuterFrame.class.getResource("aaBridge_proto_icon.png");
			final ImageIcon icon = new ImageIcon(imageFileURL);
//			final ImageIcon icon = Toolkit.getDefaultToolkit().createImage(imageFileURL);

			String s = "AaBridge written by Roger Pfister\n\n" + "This is version -  " + VersionAndBuilt.getVer() + "\n" + "Build Number -    "
					+ VersionAndBuilt.getBuildNo() + "\n" + "Built on         -    " + VersionAndBuilt.getBuilt() + "\n\n" + "see - http://RogerPf.com\n\n";
			;
			JOptionPane.showMessageDialog(this, s, "About - aaBridge", JOptionPane.INFORMATION_MESSAGE, icon);
		}

	}

	public void componentHidden(ComponentEvent e) {
	}

	public void componentMoved(ComponentEvent e) {
		if (this.getExtendedState() == NORMAL) {
			Point p = this.getLocation();
			App.frameLocationX = p.x;
			App.frameLocationY = p.y;
		}
	}

	public void componentShown(ComponentEvent e) {
	}

	public void componentResized(ComponentEvent e) {
		if (this.getExtendedState() == NORMAL) {
			App.frameWidth = getWidth();
			App.frameHeight = getHeight();
		}
	}

	public void windowOpened(WindowEvent e) {
	}

	public void windowClosing(WindowEvent e) {
		App.savePreferences();
		CmdHandler.doAutoSave();
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

	public void setTitleAsRequired() {
		String s = "aaBridge  " + VersionAndBuilt.verAndBuildNo();
		if ((App.deal.lastSavedAsFilename != null) && (App.deal.lastSavedAsFilename.length() > 0)) {
			s += "    -    " + App.deal.lastSavedAsFilename;
		}
		setTitle(s);
	}

	/**
	 *  drag and drop support for externaly (outside java from host OS) dropped deal files
	 */
	private TransferHandler handler = new TransferHandler() {

		private static final long serialVersionUID = 1L;

		public boolean canImport(TransferHandler.TransferSupport support) {
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

			try {

				@SuppressWarnings("unchecked")
				java.util.List<File> list = (java.util.List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);

				for (File f : list) {
					CmdHandler.doAutoSave();
					CmdHandler.readFileIfExists(f.getPath(), "");
					return true; // we ONLY EVER care about the first item in the list
				}
			} catch (Exception e) {
				return false;
			}

			return false;
		}
	};

	/**   
	 */
	static void clearOutOldDealsFromFolder(String folderNameAndPath, long deleteAfterDays) {
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
				if (file.getName().startsWith("20") == false)
					continue;
				if (file.getName().endsWith(".aaBridge") == false && file.getName().endsWith(App.dotLinExt) == false)
					continue;
				if (file.lastModified() > deleteEarlierThan)
					continue;

				file.delete();

			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}
}
