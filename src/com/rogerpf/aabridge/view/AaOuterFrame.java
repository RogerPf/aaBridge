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
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Toolkit;
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

import version.Version;

import com.rogerpf.aabridge.controller.App;
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
		setTitle("aaBridge" + Version.v);
		java.net.URL imageFileURL = AaOuterFrame.class.getResource("aaBridge_proto_icon.png");
		setIconImage(Toolkit.getDefaultToolkit().createImage(imageFileURL));
		
		this.addWindowListener(this);
		this.addComponentListener(this);
		App.loadPreferences();

		App.deal = Deal.nextBoard(0, (App.watchBidding == false), App.dealCriteria);

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
		menuItem = new JMenuItem("Open", KeyEvent.VK_O);
		menuItem.setActionCommand("menuOpen");
		menuItem.addActionListener(App.con);
		menu.add(menuItem);

		// Quick Save
		menuItem = new JMenuItem("Quick Save", KeyEvent.VK_Q);
		menuItem.setActionCommand("menuQuickSave");
		menuItem.addActionListener(App.con);
		menu.add(menuItem);

		// Save
		menuItem = new JMenuItem("Save", KeyEvent.VK_S);
		menuItem.setActionCommand("menuSave");
		menuItem.addActionListener(App.con);
		menu.add(menuItem);

		// Save As
		menuItem = new JMenuItem("Save As", KeyEvent.VK_A);
		menuItem.setActionCommand("menuSaveAs");
		menuItem.addActionListener(App.con);
		menu.add(menuItem);

		// Show Saves Folder
		menuItem = new JMenuItem("Open 'saves' folder and show the contents", KeyEvent.VK_F);
		menuItem.setActionCommand("openSavesFolder");
		menuItem.addActionListener(App.con);
		menu.add(menuItem);

		// Options - MENU
		menu = new JMenu("Options");
		menu.setMnemonic(KeyEvent.VK_O);
		menuBar.add(menu);

		// Right Panel - Preferences
		menuItem = new JMenuItem("Show - Preferences", KeyEvent.VK_R);
		menuItem.setActionCommand("rightPanelPreferences");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		// Right Panel - DealChoices
		menuItem = new JMenuItem("Show - Deal Choices", KeyEvent.VK_R);
		menuItem.setActionCommand("rightPanelDealChoices");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		// Bottom Panel
		menuItem = new JMenuItem("Show - Speed Selection", KeyEvent.VK_L);
		menuItem.setActionCommand("lowerPanel");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		// Help - MENU
		menu = new JMenu("Help");
		menu.setMnemonic(KeyEvent.VK_H);
		menuBar.add(menu);
		
		// Help About
		menuItem = new JMenuItem("Help", KeyEvent.VK_H);
		menuItem.setActionCommand("menuHelpHelp");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		// Help About
		menuItem = new JMenuItem("About", KeyEvent.VK_A);
		menuItem.setActionCommand("menuHelpAbout");
		menuItem.addActionListener(this);
		menu.add(menuItem);



		//-----------------------------------------------------
		
		// Make the inner a fixed ratio (AspectBoundable) so creating the middle panel
		payloadPanel = new AaPayloadPanel();
		PreferredSizeGridLayout psgl = new PreferredSizeGridLayout(1, 1);
		psgl.setBoundableInterface(new AspectBoundable());
		payloadPanel.setLayout(psgl);
		payloadPanel.add(App.gbp);

		rop = new AaRightOptionsPanel();
		lop = new AaLowerOptionsPanel();
		rop.setMinimumSize(new Dimension(0, 0));
		lop.setMinimumSize(new Dimension(0, 0));

		// Create a split pane with the two scroll panes in it.
		splitPaneVert = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, payloadPanel, lop);
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

		App.savesPath = appHomePath + "saves" + File.separator;

		File appHome = new File(appHomePath);
		File saves = new File(App.savesPath);

		appHome.mkdir();
		saves.mkdir();

		setVisible(false); // set true by the timer below

		App.con.postContructionInitTimer.start();
	}
	
	
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
		if (cmd == "rightPanelPreferences") {
			App.frame.splitPaneHorz.setDividerLocation(App.frame.getWidth() - 230);
			App.frame.rop.setSelectedIndex(1);
		}
		if (cmd == "rightPanelDealChoices") {
			App.frame.splitPaneHorz.setDividerLocation(App.frame.getWidth() - 230);
			App.frame.rop.setSelectedIndex(0);
		}
		else if (cmd == "lowerPanel") {
			App.frame.splitPaneVert.setDividerLocation(App.frame.getHeight() - 130);
		}
		else if (cmd == "menuHelpHelp") {
			new AaHelp();
		}
		else if (cmd == "menuHelpAbout") {
			java.net.URL imageFileURL = AaOuterFrame.class.getResource("aaBridge_proto_icon.png");
			final ImageIcon icon = new ImageIcon(imageFileURL);
//			final ImageIcon icon = Toolkit.getDefaultToolkit().createImage(imageFileURL);

			String  s = "AaBridge written by Roger Pfister\n\n"
					+ "This is " + Version.v + "\n\n"
					+ "see - http://RogerPf.com \n\n" 
					+ "Open source (written in Java), sources\n"
					+ "available from (to be added before release)";
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
		App.maximized = (this.getExtendedState() == MAXIMIZED_BOTH);
		App.savePreferences();
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
}
