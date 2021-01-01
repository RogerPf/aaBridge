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

import java.awt.Desktop;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.Aaf;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.controller.Book;
import com.rogerpf.aabridge.controller.Book.LinChapter;
import com.rogerpf.aabridge.controller.Bookshelf;
import com.rogerpf.aabridge.controller.BridgeLoader;
import com.rogerpf.aabridge.controller.CmdHandler;
import com.rogerpf.aabridge.controller.MruCollection;
import com.rogerpf.aabridge.controller.MruCollection.MruChapter;
import com.rogerpf.aabridge.model.Cc;
import com.rpsd.bridgefonts.BridgeFonts;

import net.miginfocom.swing.MigLayout;

/** *********************************************************************************  
 */
public class AaHomeBtnPanel extends ClickPanel implements ActionListener {
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	JPopupMenu chapterMarkerMenu = null;

	QLabel anyLabel;

	QButton home_btn;
	QButton histBack_btn;
	QButton histBack_btn1;
	QButton histBack_btn2;
	QButton histBack_btn3;
	QButton histFwd_btn;
	QButton histFwdEnd_btn;
	QButton toggleChapterMark_btn;
	QButton sizeA_btn;
	QButton sizeB_btn;
	QButton sizeC_btn;
	QButton linEdit_btn;

	public AaHomeBtnPanel() { // Constructor
		// ==============================================================================================

		setLayout(new MigLayout(App.simple + ", " + App.hm0oneHun + ", flowx", "[]", "[]"));

		add(home_btn = new QButton(this, "z" /* HOME icon in font */), "gapx4, gapy6, split4");
		float stdSize = home_btn.getFont().getSize();
		Font home_btnFont = BridgeFonts.faceAndSymbolFont.deriveFont(stdSize * 3.0f);
		home_btn.setFont(home_btnFont);
		home_btn.setBorder(BorderFactory.createEmptyBorder(2, 4, 4, 4)); // yes even on the mac
		String tt = Aaf.gT("homePanel.home_TT") + "    " + Aaf.gT("homePanel.homeRC_TT");
		home_btn.setToolTipText(tt);

		home_btn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					CmdHandler.actionPerfString("execute_extract_lins_from_latest_html");
				}
			}
		});

		add(histBack_btn = new QButton(this, "{" /* { = left arrow in font*/), "gapx7");
		histBack_btn.setFont(BridgeFonts.faceAndSymbolFont.deriveFont(stdSize * 2.4f));
		histBack_btn.setBorder(BorderFactory.createEmptyBorder(1, 1, 4, 2)); // yes even on the mac
		histBack_btn.setToolTipText(Aaf.gT("homePanel.histBack_TT"));

		add(histFwd_btn = new QButton(this, "}" /* } = right arrow in font*/), "gapx7");
		histFwd_btn.setFont(BridgeFonts.faceAndSymbolFont.deriveFont((float) (stdSize * 2.0f)));
		histFwd_btn.setBorder(BorderFactory.createEmptyBorder(2, 3, 3, 2)); // yes even on the mac
		histFwd_btn.setToolTipText(Aaf.gT("homePanel.histFwd_TT"));

		add(histFwdEnd_btn = new QButton(this, "y" /* y = right arrow + bar in font*/), "gapx15, wrap");
		histFwdEnd_btn.setFont(BridgeFonts.faceAndSymbolFont.deriveFont((float) (stdSize * 2.0f)));
		histFwdEnd_btn.setBorder(BorderFactory.createEmptyBorder(2, 2, 3, 1)); // yes even on the mac
		histFwdEnd_btn.setToolTipText(Aaf.gT("homePanel.histFwdEnd_TT"));

		add(toggleChapterMark_btn = new QButton(this, "b" /* b = bookmark symbol in font */), "gapy8, gapx4, split5");
		toggleChapterMark_btn.setFont(BridgeFonts.faceAndSymbolFont.deriveFont((float) (stdSize * 2.0f)));
		toggleChapterMark_btn.setBorder(BorderFactory.createEmptyBorder(1, 2, 3, 5));
		toggleChapterMark_btn.setToolTipText(Aaf.gT("homePanel.add_mark_TT"));

		add(sizeA_btn = new QButton(this, "A"), "gapx11");
		sizeA_btn.setFont(BridgeFonts.faceAndSymbolFont.deriveFont((float) (stdSize * 1.20f)));
		sizeA_btn.setBorder(BorderFactory.createEmptyBorder(4, 3, 3, 3)); // yes even on the mac
		sizeA_btn.setToolTipText(Aaf.gT("homePanel.sizeABC_TT"));

		add(sizeB_btn = new QButton(this, "B"), "gapx5");
		sizeB_btn.setFont(BridgeFonts.faceAndSymbolFont.deriveFont((float) (stdSize * 1.20f)));
		sizeB_btn.setBorder(BorderFactory.createEmptyBorder(4, 2, 3, 2)); // yes even on the mac
		sizeB_btn.setToolTipText(Aaf.gT("homePanel.sizeABC_TT"));

		add(sizeC_btn = new QButton(this, "C"), "gapx5");
		sizeC_btn.setFont(BridgeFonts.faceAndSymbolFont.deriveFont((float) (stdSize * 1.20f)));
		sizeC_btn.setBorder(BorderFactory.createEmptyBorder(4, 2, 3, 2)); // yes even on the mac
		sizeC_btn.setToolTipText(Aaf.gT("homePanel.sizeABC_TT"));

		add(linEdit_btn = new QButton(this, "e"), "gapx11, gapy4, wrap");
		linEdit_btn.setFont(BridgeFonts.faceAndSymbolFont.deriveFont((float) (stdSize * 1.8f)));
		linEdit_btn.setBorder(BorderFactory.createEmptyBorder(2, 2, 3, 2));
		linEdit_btn.setToolTipText(Aaf.gT("homePanel.edit_TT"));

		add(new QLabel(""), "gapy9");

		linEdit_btn.setEnabled(false); // enabled at load time
	}

	public void calcApplyBarVisiblity() {
		// ==============================================================================================

		// Home button is always enabled

		histBack_btn.setEnabled(App.history.isBackPos());
		histFwd_btn.setEnabled(App.history.isFwdPos());
		histFwdEnd_btn.setEnabled(App.history.isFwdPos());

		// @formatter:off
		boolean lineEdit_enabled =    (Aaa.getLinFileEditorPath().isEmpty() == false)
				
                                   && ((     App.isVmode_Tutorial()
			                             && (App.lastLoadedChapter != null) 
			                             && (App.lastLoadedChapter.isEditable()
			                            )
			                         || (    App.isVmode_InsideADeal() 
			                		     && (App.deal.lastDealNameSaved_FULL.isEmpty() == false)
			                		    )
			                		 ));
		
	    linEdit_btn.setEnabled( lineEdit_enabled);  	                	 
		// @formatter:off
		
		toggleChapterMark_btn.setEnabled(App.isVmode_Tutorial());
	}
	

	

	JMenuItem bm_bookmarks;

	static boolean left = true;

	public void actionPerformed(ActionEvent e) {
		// ==============================================================================================
		Object source = e.getSource();
		MruChapter hit = null;
		String key = "";

		boolean hist = false;
		boolean mark = false;
		boolean ctrlKey_depressed = ((e.getModifiers() & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK);
		boolean altKey_depressed  = ((e.getModifiers() & InputEvent.ALT_MASK) == InputEvent.ALT_MASK);
		boolean altGraphKey_depressed  = ((e.getModifiers() & InputEvent.ALT_GRAPH_MASK) == InputEvent.ALT_GRAPH_MASK);

		if (source == histBack_btn) {
			App.history.backOne();
		}

		else if (source == histFwd_btn) {
			App.history.fwdOne();
		}

		else if (source == histFwdEnd_btn) {
			App.history.fwdEnd();
		}
		
		else if ((source == sizeA_btn) || (source == sizeB_btn) || (source == sizeC_btn)) {
			
			if (ctrlKey_depressed ) { /* save */
				Boolean forceRestorePos = (altKey_depressed || altGraphKey_depressed);				
				AaaMenu.writeUserSizeEntry(e.getActionCommand(), forceRestorePos);
			}
			else {	/* restore */			
				AaaMenu.actionUserSizeEntry(e.getActionCommand());				
			}	
		}		

		else if (source == home_btn) {
			if (ctrlKey_depressed == false) {
				App.con.ShowHelpAndWelcome();
			}
			else {
				// reposition to exactly to the top left / right of the current screen

				boolean shiftKey_depressed = ((e.getModifiers() & InputEvent.SHIFT_MASK) == InputEvent.SHIFT_MASK);

				int w10fix = shiftKey_depressed ? 7 : 0;

				Point topLeft = App.frame.getLocationOnScreen();

				App.frameLocationX = 0;
				App.frameLocationY = 0;

				GraphicsDevice[] gs = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
				for (int j = 0; j < gs.length; j++) {
					GraphicsConfiguration[] gc = gs[j].getConfigurations();
					for (int i = 0; i < gc.length; i++) {
						Rectangle bounds = gc[i].getBounds();

						if (bounds.contains(topLeft)) {
							App.frameLocationY = bounds.y;
							if (left) {
								App.frameLocationX = bounds.x - w10fix;
							}
							else {
								App.frameLocationX = bounds.x + (bounds.width - App.frame.getWidth()) + w10fix;
							}
							left = !left;
							break;
						}
					}
				}

				App.frame.setLocation(App.frameLocationX, App.frameLocationY);
			}
		}

		else if (source == linEdit_btn) {
			String path = "";
			if (App.isVmode_InsideADeal())
				path = App.deal.lastDealNameSaved_FULL;
			
			else { // if (App.isVmode_Tutorial())  
	            path = App.debug_pathlastLinLoaded;
			}
			
			if (path == null || path.isEmpty() || new File(path).exists() == false)
				return;

			if (ctrlKey_depressed && !App.debug_linfile_partner_path.isEmpty() && !App.debug_linfile_partner_ext.isEmpty()) {
				 String fn = new File(path).getName();
				 String new_path = App.debug_linfile_partner_path + fn.substring(0, fn.length() - 3) + App.debug_linfile_partner_ext;
				 if (new File(new_path).exists()) {
					 path = new_path;
				 }
			}

			try {
				String np = Aaa.getLinFileEditorPath();
				
				if (np.contentEquals("desktop")) { // mac and linux
					Desktop.getDesktop().open(new File(path));
				} else if (np.isEmpty() == false) { // win
					Runtime.getRuntime().exec(new String[] { np, path });
				}
			} catch (Exception e1) {
			}
		}

		else if (source == toggleChapterMark_btn) {
			if (App.mg.mruChap != null) {
				// int current_pg_numb = (App.mg.lin.linType == Lin.FullMovie) ? App.mg.get_current_pg_number_display() : 0;
				int current_pg_numb = App.mg.get_current_pg_number_display();

				App.mg.mruChap.toggleChapterMark(current_pg_numb);
				App.frame.repaint();
				mruDelayedSaveTimer_Short_Start();
			}
		}

		else if (source == bm_bookmarks && ctrlKey_depressed) {
			App.mruCollection.clearAllMarksInCollection();
			fill_chapterMarkerMenu();
			mruDelayedSaveTimer_Short_Start();
		}

		else if (source == bm_bookmarks) {
			String chapterPartName = "Bookmarks";
			Book b = App.bookshelfArray.get(0).getBookWithChapterPartName(chapterPartName);
			if (b != null) {
				LinChapter chapter = b.getChapterByDisplayNamePart(chapterPartName);
				if (chapter != null) {
					chapter.loadWithShow("replaceBookPanel");
				}
			}
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
				fill_chapterMarkerMenu();
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
				 *  Check to see it if is was in an external book
				 *  
				 */
				boolean success = App.bookshelfArray.makeBookshelfFromDroppedPath(hit.src, false /* we DONT want the chapter loaded */);
				if (success) {
					for (Bookshelf shelf : App.bookshelfArray) {
						Book b = shelf.getBookWithChapterPartName(hit.displayNoUscore);
						if (b != null) {
							boolean chapterLoaded = b.loadChapterByDisplayNamePart(hit.displayNoUscore);
							if (chapterLoaded) {
								App.book = b;
								App.aaBookPanel.matchToAppBook();
								App.aaBookPanel.showChapterAsSelected(hit.displayNoUscore);
								App.mg.jump_to_pg_number_display((history_override != 0) ? history_override : hit.hist_pgNumb);
								return;
							}
							break;
						}
					}
				}

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

	final static String menubar_bookmarks = Aaf.gT("menubar.bookmarks");
	final static String menubar_bookmarksHelp = Aaf.gT("menuBookM.bookmarksHelp");
	final static String menuBookM_noMarks = Aaf.gT("menuBookM.noMarks");
	final static String menuBookM_removeAll = Aaf.gT("menuBookM.removeAll");
	final static String menuBookM_toggle = Aaf.gT("menuBookM.toggle");

	public JMenu fill_chapterMarkerMenu() {
		// ==============================================================================================

		JLabel label;
		JMenuItem menuItem;
		JMenu menu = App.bookmarksMenu;

		menu.removeAll();

		List<MruCollection.MruChapter> mruByTimeStamp = App.mruCollection.sortByTimestamp();
		bm_bookmarks = menuItem = new JMenuItem(menubar_bookmarksHelp);
		menuItem.addActionListener(this);
		menuItem.setFont(menuItem.getFont().deriveFont(menuItem.getFont().getSize() * App.menuInfoSize));
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
			label = new JLabel("         " + menuBookM_noMarks);
			label.setFont(label.getFont().deriveFont(label.getFont().getSize() * App.menuInfoSize));
			label.setForeground(Cc.BlueStrong);
			menu.add(label);
		}

		if (mark_count > 0) {
			menu.addSeparator();

			label = new JLabel("   " + menuBookM_removeAll);
			label.setFont(label.getFont().deriveFont(label.getFont().getSize() * App.menuInfoSize));
			label.setForeground(Cc.GreenStrong);
			menu.add(label);

			label = new JLabel("   " + menuBookM_toggle);
			label.setFont(label.getFont().deriveFont(label.getFont().getSize() * App.menuInfoSize));
			label.setForeground(Cc.BlueWeak);
			menu.add(label);
		}

		return menu;
	}

	/**
	*/
	public Timer shortDelayTimer = new Timer(200 /* ms */, new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			fill_chapterMarkerMenu();
			shortDelayTimer.stop();
		}
	});

	public void fillMenuDelayed() {
		shortDelayTimer.start();
	}

}
