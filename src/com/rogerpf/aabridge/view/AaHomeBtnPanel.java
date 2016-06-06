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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.Timer;
import javax.swing.border.Border;

import net.miginfocom.swing.MigLayout;

import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.controller.Book;
import com.rogerpf.aabridge.controller.Bookshelf;
import com.rogerpf.aabridge.controller.BridgeLoader;
import com.rogerpf.aabridge.controller.MruCollection;
import com.rogerpf.aabridge.controller.MruCollection.MruChapter;
import com.rogerpf.aabridge.model.Cc;

/** *********************************************************************************  
 */
public class AaHomeBtnPanel extends ClickPanel implements ActionListener {
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	JPopupMenu chapterMarkerMenu = null;

	QLabel anyLabel;

	QButton homeBtn;
	QButton menuBtn;
	QButton linEdit;

	QButton toggleChapterMarkBtn;

//	QButton listM;

	public AaHomeBtnPanel() { // Constructor
		// ==============================================================================================

		setLayout(new MigLayout(App.simple + ", flowx", "[]push[]"));

		Border bdr4 = BorderFactory.createEmptyBorder(0, 4, 1, 4);

		add(homeBtn = new QButton(this, "Home"), "gapx4, gapy6");
		if (App.onMac == false)
			homeBtn.setBorder(bdr4);
		homeBtn.setToolTipText("Jump to the  -  Welcome  page");

		Font std = homeBtn.getFont();
		Font large = std.deriveFont((float) (homeBtn.getFont().getSize() * 1.2));
		Font sml = std.deriveFont((float) (homeBtn.getFont().getSize() * 0.92));

		homeBtn.setFont(large);

		if (App.devMode) {
			add(linEdit = new QButton(this, "Ed"), "gapx6, gapy4, split");
			linEdit.setFont(sml);
			if (App.onMac == false)
				linEdit.setBorder(BorderFactory.createEmptyBorder(0, 4, 1, 4));
			linEdit.setToolTipText("Attempt to edit the current .lin file");
		}

		String menuBtnText = (App.devMode) ? "Mu" : "Menu";
		String xGap = (App.onMac) ? "" : "gapx8, ";
		add(menuBtn = new QButton(this, menuBtnText), xGap + "gapy0, wrap");
		menuBtn.setFont(sml);

		if (App.onMac == false)
			menuBtn.setBorder(bdr4);
		// menuBtn.setToolTipText("Recently viewed  Chapters  and  Chapter Marks  "); DONT add this

		String togChaperText = (App.onMacOrLinux) ? "Toggle Chapter Mark" : "Tog Chapter Mark";
		String yGap = (App.onMac) ? "" : "gapy8, ";
		add(toggleChapterMarkBtn = new QButton(this, togChaperText), "gapx4, " + yGap + "span3");
		toggleChapterMarkBtn.setFont(sml);
		if (App.onMac == false)
			toggleChapterMarkBtn.setBorder(bdr4);
		toggleChapterMarkBtn.setToolTipText("Toggle  (Add or Remove)  a  Chapter Mark  at this point in the Chapter ");
	}

	public void calcApplyBarVisiblity() {
		// ==============================================================================================
		toggleChapterMarkBtn.setVisible(App.isVmode_Tutorial());
	}

//	final static String recentlyViewedChapters = "Recently Viewed Chapters";
	final static String markedChapters = "Marked Chapters";

	public void actionPerformed(ActionEvent e) {
		// ==============================================================================================
		Object source = e.getSource();
		MruChapter hit = null;
		String key = "";

		boolean hist = false;
		boolean mark = false;
		boolean ctrlKey_depressed = ((e.getModifiers() & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK);

		if (source == homeBtn) {
			App.con.ShowHelpAndWelcome();
		}

		else if (source == linEdit) {
			if (App.debug_pathlastLinLoaded.isEmpty())
				return;
			if (new File(App.debug_pathlastLinLoaded).exists() == false)
				return;

			try {
				Runtime.getRuntime().exec(new String[] { "notepad.exe", App.debug_pathlastLinLoaded });
				// Runtime.getRuntime().exec(new String[] { "C:\\ProgramRPf\\Notepad++\\notepad++.exe", App.debug_pathlastLinLoaded });
			} catch (Exception e1) {
			}
		}

		else if (source == menuBtn) {
			chapterMarkerMenu = fill_chapterMarkerMenu();
			chapterMarkerMenu.show(menuBtn, menuBtn.getWidth() + 4, -8);
		}

		else if (source == toggleChapterMarkBtn) {
			if (App.mg.mruChap != null) {
				// int current_pg_numb = (App.mg.lin.linType == Lin.FullMovie) ? App.mg.get_current_pg_number_display() : 0;
				int current_pg_numb = App.mg.get_current_pg_number_display();

				App.mg.mruChap.toggleChapterMark(current_pg_numb);
				App.frame.repaint();
				// App.mruCollection.saveCollection();
				mruDelayedSaveTimer_Short_Start();
			}
		}

//		else if (ctrlKey_depressed && e.getActionCommand().contentEquals(recentlyViewedChapters)) {
//			App.mruCollection.clearAllHistories();
//			mruDelayedSaveTimer_Short_Start();
//		}

		else if (ctrlKey_depressed && e.getActionCommand().contentEquals(markedChapters)) {
			App.mruCollection.clearAllMarksInCollection();
			mruDelayedSaveTimer_Short_Start();
		}

		else {
			String cmd = e.getActionCommand();

			if (cmd.startsWith("Hist_"))
				hist = true;
			else if (cmd.startsWith("Mark_"))
				mark = true;

			if (hist || mark) { // remove the Hist_ or Mark_
				key = cmd.substring(5);
			}
			hit = App.mruCollection.get(key);
		}

		if (hit == null)
			return;

		/** 
		 * So here we are processing the hit on a chapter 
		 * */

		if (hist && ctrlKey_depressed) {
			App.mruCollection.clearSingleHistory(key);
			App.mruCollection.saveCollection();
			App.frame.repaint();
			return;
		}

		int history_override = 0; // means no override;

		if (mark) {
			if (ctrlKey_depressed) {
				// System.out.println("Control Key is pressed and perhaps other keys as well");

				App.mruCollection.clearChapterMarks(key);
				App.mruCollection.saveCollection();
				App.frame.repaint();
				return;
			}
			// fall through and be processed as a History

			history_override = hit.getFirstMark();
		}

		if (hit.type == 'r') { // r is resource either an external zip etc or BUILT-IN

			String src = hit.src;

			if (src.contentEquals("BUILT-IN")) {
				for (Bookshelf shelf : App.bookshelfArray) {
					Book b = shelf.getBookWithChapterPartName(hit.displayNoUscore);
					if (b != null) {
						boolean chapterLoaded = b.loadChapterByDisplayNamePart(hit.displayNoUscore);
						if (chapterLoaded) {
							App.book = b;
							App.aaBookPanel.matchToAppBook();
							App.aaBookPanel.showChapterAsSelected(hit.displayNoUscore);
							App.mg.jump_to_pg_number_display((history_override != 0) ? history_override : hit.hist_pgNumb);
						}
						break;
					}
				}
			}
			else {
				/**
				 *  is the hit entry a zip or equiv ?
				 */
				String low = src.toLowerCase();
				if (low.endsWith(".zip") || low.endsWith(".linzip")) {
					boolean chapterLoaded = BridgeLoader.makeBookFromPath(src, null);
					if (chapterLoaded) {
						App.book.loadChapterByDisplayNamePart(hit.displayNoUscore);
						App.mg.jump_to_pg_number_display((history_override != 0) ? history_override : hit.hist_pgNumb);
					}
				}
			}
		}

		else if (hit.type == 'f') {
			/**
			 * These are .lins and .pbns  or Eclipse mode "local files" which would normally be BUILT-IN
			 */
			String low = hit.filenamePlus.toLowerCase();
			if (low.endsWith(".lin") || low.endsWith(".pbn")) {
				boolean success = BridgeLoader.makeBookFromPath(hit.src, null);
				if (success) {
					App.book.loadChapterByDisplayNamePart(hit.displayNoUscore);
					App.mg.jump_to_pg_number_display((history_override != 0) ? history_override : hit.hist_pgNumb);
				}
			}
		}

		App.gbp.matchPanelsToDealState();
	}

	static final int mruDelayedSaveTimer_ShortMs = 1 * 1000;

	/**
	*/
	public Timer mruDelayedSaveTimer = new Timer(mruDelayedSaveTimer_ShortMs /* ms */, new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			mruDelayedSaveTimer.stop();

			App.mruCollection.saveCollection();
		}
	});

	public void mruDelayedSaveTimer_Short_Start() {
		// ==============================================================================================
		mruDelayedSaveTimer.stop();
		mruDelayedSaveTimer.setInitialDelay(mruDelayedSaveTimer_ShortMs);
		mruDelayedSaveTimer.start();
	}

	public JPopupMenu fill_chapterMarkerMenu() {
		// ==============================================================================================

		JMenuItem menuItem;
		JPopupMenu menu = new JPopupMenu(" ");

		// System.out.println(" fill_chapterMarkerMenu " + App.mg.end_pg + "  " + App.mg.stop_gi);
//		menuItem = new JMenuItem(recentlyViewedChapters);
//		menuItem.setActionCommand(recentlyViewedChapters);
//		menuItem.addActionListener(this);
//		menuItem.setForeground(Cc.GreenStrong);
//		menu.add(menuItem);
//
		List<MruCollection.MruChapter> mruByTimeStamp = App.mruCollection.sortByTimestamp();
//
//		int hist_count = 0;
//		for (MruCollection.MruChapter mru : mruByTimeStamp) {
//
//			if (mru.hist_pgNumb <= 1)
//				continue;
//
//			if (mru.filenamePlus.contains("90  Help Welcome and Examples"))
//				continue;
//
//			String menuText = mru.getMruKey();
//
//			hist_count++;
//			menuItem = new JMenuItem("      " + hist_count + ".    " + menuText);
//			menuItem.setActionCommand("Hist_" + menuText);
//			menuItem.addActionListener(this);
//			menu.add(menuItem);
//			if (hist_count >= App.hist_max_display)
//				break;
//		}
//
//		if (hist_count == 0) {
//			menuItem = new JMenuItem("    - - no History yet - -");
//			menuItem.setForeground(Cc.BlueStrong);
//			menu.add(menuItem);
//		}
//
//		menu.addSeparator();
		menuItem = new JMenuItem(markedChapters);
		menuItem.setActionCommand(markedChapters);
		menuItem.addActionListener(this);
		menuItem.setForeground(Cc.GreenStrong);
		menu.add(menuItem);

		// Create an empty list
		List<MruCollection.MruChapter> mruByDisplayText = new ArrayList<MruCollection.MruChapter>();

		//
		// add the newest N
		int mark_count = 0;
		for (MruCollection.MruChapter mru : mruByTimeStamp) {// so we can take the N newist

			if (mru.marks.size() == 0)
				continue;

			// we allow all marked chapters even those in the "help and welcome"
			// if (mru.filenamePlus.contains("90  Help Welcome and Examples"))
			// continue;

			mark_count++;
			mruByDisplayText.add(mru);

			if (mark_count >= App.mark_max_display)
				break;
		}

		Collections.sort(mruByDisplayText, new Comparator<MruCollection.MruChapter>() {
			public int compare(MruCollection.MruChapter ch1, MruCollection.MruChapter ch2) {
				return (int) (ch1.displayNoUscore.compareToIgnoreCase(ch2.displayNoUscore));
			}
		});

		for (MruCollection.MruChapter mru : mruByDisplayText) { // yes we need them by time stamp to start with
			String menuText = mru.getMruKey();
			menuItem = new JMenuItem("      " + menuText);
			menuItem.setActionCommand("Mark_" + menuText);
			menuItem.addActionListener(this);
			menu.add(menuItem);
		}

		if (mark_count == 0) {
			menuItem = new JMenuItem("    - - no Chapter Marks yet - -");
			menuItem.setForeground(Cc.BlueStrong);
			menu.add(menuItem);
		}

		if (mark_count > 0) {
			menu.addSeparator();

			menuItem = new JMenuItem("To   REMOVE ALL   of a   Chapter's Marks   at once  -  Hold down  the  Ctrl  Key  -   while clicking    THAT entry  ");
			menuItem.setForeground(Cc.GreenStrong);
			menu.add(menuItem);

			menuItem = new JMenuItem("You can also   TOGGLE     a single   Chapter Mark     -  Hold down  the  Ctrl  Key  -   while clicking    THE Navbar");
			menuItem.setForeground(Cc.BlueWeak);
			menu.add(menuItem);
		}

		return menu;
	}

}
