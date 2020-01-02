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

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.ScrollPaneLayout;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.controller.Book.LinChapter;
import com.rogerpf.aabridge.controller.CmdHandler;
import com.rogerpf.aabridge.model.Cc;
import com.rogerpf.aabridge.model.Lin;

import net.miginfocom.swing.MigLayout;

/** *********************************************************************************  
 */
public class AaBookPanel extends JPanel {
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	public static int defaultWidthPixels = /* App.onMacOrLinux ? 160 : */ 120;

	public AaBookPanelInner aaBookPanelInner;

	JScrollPane scroller;

	public AaBookPanel() { // Constructor
		// ==============================================================================================

		aaBookPanelInner = new AaBookPanelInner();
		setMinimumSize(new Dimension(defaultWidthPixels, 5000));

		// setVisible(false);
	}

	public void setCorrectWidth(boolean ddArrayListVis) {
		// ==============================================================================================

//		int w = defaultWidthPixels;
//
//		if (ddArrayListVis)
//			w -= 21;
//
//		setMinimumSize(new Dimension(w, 0));
	}

	@SuppressWarnings("serial")
	public void matchToAppBook() {
		// ==============================================================================================
		removeAll();

		aaBookPanelInner.stopTimers();
		aaBookPanelInner = new AaBookPanelInner();
		aaBookPanelInner.matchToAppBook();

		setLayout(new MigLayout(App.simple + ", flowy", "[100%]", "[100%]"));

		JScrollPane scroller = new JScrollPane(aaBookPanelInner);
		scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		scroller.setBorder(BorderFactory.createEmptyBorder());

		add(scroller, "width 100%, height 100%, push, grow");

		scroller.setLayout(new ScrollPaneLayout() {
			@Override
			public void layoutContainer(Container parent) {
				JScrollPane scrollPane = (JScrollPane) parent;
				scrollPane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
				super.layoutContainer(parent);
				scrollPane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
			}
		});
		scroller.setVisible(true);
		setVisible(true);
	}

	public int getLoadedChapterIndex() {
		// ==============================================================================================
		return aaBookPanelInner.getLoadedChapterIndex();
	}

	public void showChapterAsSelected(String s) {
		// ==============================================================================================
		aaBookPanelInner.showChapterAsSelected(s);
	}

	public void showChapterAsSelected(int ind) {
		// ==============================================================================================
		aaBookPanelInner.showChapterAsSelected(ind);
	}

	public void showChapterAsBroken(int ind) {
		// ==============================================================================================
		aaBookPanelInner.showChapterAsBroken(ind);
	}

	static Dimension bkp = new Dimension();
	static Dimension frp = new Dimension();
	static Dimension pcp = new Dimension();

	static Dimension nMin = new Dimension(0, 0);
	static Dimension nMax = new Dimension(0, 5000);

	public void playloadPanel_resized_check() {
		// ==============================================================================================

		plpResizedTimer.start();

	}

	/**
	*/
	public Timer plpResizedTimer = new Timer(50 /* ms */, new ActionListener() {
		// =============================================================================
		public void actionPerformed(ActionEvent evt) {
			plpResizedTimer.stop();

			App.aaBookPanel.getSize(bkp);
			App.frame.fixedRatioPanel.getSize(frp);
			App.frame.linPlcp.getSize(pcp);

			int hight_dif = (pcp.height - frp.height);

			boolean frp_at_fullHight = (hight_dif <= 1);

			int width_spare = pcp.width - frp.width;

			int width_vugraph_col = App.dualDealListBtns.isVisible() ? App.dualDealListBtns.getWidth() : 0;

			width_spare -= width_vugraph_col;

			if (frp_at_fullHight) {
				if ((width_spare > bkp.width)) {
					nMin.width = width_spare;
					nMax.width = width_spare;
					setMinimumSize(nMin);
					setMaximumSize(nMax);
					setSize(nMax);
					App.frame.repaint();
					App.frame.revalidate();
				}
			}
			else if (bkp.width > defaultWidthPixels) {
				/* We know that the core panel is NOT at full height
				 * and we know that we are above minimum width 
				 * 
				 * So we try to calc what a good width would be
				 * assuming that the pcp wants to be the same ratio
				 * as the frp
				 */
				int desired_frp_width = (1000 * pcp.height * frp.width) / (frp.height * 1000);

				int w = pcp.width - width_vugraph_col - desired_frp_width;

				if (w < defaultWidthPixels) {
					w = defaultWidthPixels;
				}
				nMin.width = w;
				nMax.width = w;
				setMinimumSize(nMin);
				setMaximumSize(nMax);
				setSize(nMax);
				App.frame.repaint();
				App.frame.revalidate();
			}
		}
	});

	/**
	 * We are 90% filled by scroller which is the scroll bar plus inner
	 */
	/**
	 */
	public void paintComponent(Graphics g) {
		// ==============================================================================================
//		super.paintComponent(g);
	}

}

/***********************************************************************************  
 */
class AaBookPanelInner extends ClickPanel {
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	ArrayList<BookNameBtn> buttons = new ArrayList<BookNameBtn>();

	private int linfileChangePollTimer_ms = 933;

	String linfileChangePrevName = "";

	Timer linfileChangePollTimer = new Timer(linfileChangePollTimer_ms, new ActionListener() {
		// ==============================================================================================
		public void actionPerformed(ActionEvent evt) {

			int index = getLoadedChapterIndex();

			App.aaBookPanel.playloadPanel_resized_check();  // ugly but it helps

			if ((index < 0) || (index > (buttons.size() - 2))) // there is a hidden empty button top
				return;

			boolean change = App.book.getChapterByIndex(index).hasLinFileChanged(linfileChangePollTimer_ms);

			if (change) {
				boolean old_autoEnter = App.pbnAutoEnter;
				boolean restart_analyser = App.ddsAnalyserPanelVisible && App.reinstateAnalyser;
				// System.out.println(" file change detected ");
				int prev_end = (App.mg.lin.linType == Lin.FullMovie || App.mg.lin.linType == Lin.Other) ? App.mg.get_current_pg_number_display() : -1;
				App.book.loadChapterByIndex(index);
				if (prev_end > 0) {
					App.mg.jump_to_pg_number_display(prev_end);
				}
				if (old_autoEnter && App.cameFromPbnOrSimilar()) {
					CmdHandler.tutorialIntoDealClever();
					App.pbnAutoEnter = true;
					if (restart_analyser) {
						App.gbp.c2_0__ddsAnal.analyseButtonClicked();
					}
					App.gbp.matchPanelsToDealState();
				}
			}
		}
	});

	public AaBookPanelInner() {
		// ==============================================================================================
		setLayout(new MigLayout(App.simple + ", flowy", "[]", "grow"));
		setVisible(true);
		setOpaque(true);

		linfileChangePollTimer.setInitialDelay(linfileChangePollTimer_ms);
		linfileChangePollTimer.start();
	}

	public void stopTimers() {
		// ==============================================================================================
		linfileChangePollTimer.stop();
	}

	public int getLoadedChapterIndex() {
		// ==============================================================================================
		for (BookNameBtn butt : buttons) {
			if (butt.currentLin) {
				return butt.index;
			}
		}
		return -1;
	}

	public void matchToAppBook() {
		// ==============================================================================================

//		BookNameBtn button = new BookNameBtn(this, " ", -1, "");
//		add(button);
//		buttons.add(button);
		setBackground(Cc.g(Cc.darkGrayBg));

		BookNameBtn button;

		int i = 0;
		while (i < App.book.size()) {
			LinChapter h = App.book.get(i);

			String mruKey = h.generateMruKey();

			button = new BookNameBtn(this, h.displayNoUscore, i, mruKey);
			add(button);
			buttons.add(button);
			if (i == 0) {
				button.setButtonCurrentLin(true);
			}
			i++;
		}

		// null blank button at end for clarity
		button = new BookNameBtn(this, " ", -1, "");
		add(button);
		buttons.add(button);

		setVisible(true);
	}

	public void componentResized(ComponentEvent e) {
	}

	public void componentMoved(ComponentEvent e) {
	}

	public void componentShown(ComponentEvent e) {
	}

	public void componentHidden(ComponentEvent e) {
	}

	public void showChapterAsSelected(String chapterName) {
		// ==============================================================================================
		for (BookNameBtn button : buttons) {
			if (button.getText().contains(chapterName)) {
				setButtonCurrentLin(button);
				last_button_selected = button;
				scroll_to_visible_timer.start();
				return;
			}
		}
	}

	BookNameBtn last_button_selected = null;

	Timer scroll_to_visible_timer = new Timer(10 /*ms*/, new ActionListener() {
		// ==============================================================================================
		public void actionPerformed(ActionEvent evt) {
			scroll_to_visible_timer.stop();
			if (last_button_selected != null) {
				Rectangle r = last_button_selected.getBounds();
				int h = r.height;
				r.y -= h;
				r.height += h * 3;
				scrollRectToVisible(r);
			}
		}
	});

	public void setButtonCurrentLin(BookNameBtn button) {
		// ==============================================================================================
		for (BookNameBtn butt : buttons) {
			butt.setButtonCurrentLin(butt == button);
		}
	}

	public void showChapterAsSelected(int ind) {
		// ==============================================================================================
		for (BookNameBtn button : buttons) {
			if (button.index == ind) {
				setButtonCurrentLin(button);
				scrollRectToVisible(button.getBounds());
				return;
			}
		}
	}

	public void showChapterAsBroken(int ind) {
		// ==============================================================================================
		for (BookNameBtn button : buttons) {
			if (button.index == ind) {
				button.brokenLin = true;
				scrollRectToVisible(button.getBounds());
				return;
			}
		}
	}

	public void bookButtonClicked(BookNameBtn button) {
		// ==============================================================================================

		int index = button.index;
		if (index < 0)
			return; // top and bottom end spacers

		Boolean success = false;
		try {
			// App.reinstateAnalyser = false;
			App.ddsAnalyserPanelVisible = false;
			success = App.book.loadChapterByIndex(index);
		} catch (Exception e) {
			e.printStackTrace();
			success = false;
		}

		if (success) {
			scrollRectToVisible(button.getBounds());
			setButtonCurrentLin(button);
			App.gbp.dealMajorChange();
		}
		else
			button.brokenLin = true;

	}

	public void paintComponent(Graphics g) {
		// ==============================================================================================
//		super.paintComponent(g); we do it all below
		Graphics2D g2 = (Graphics2D) g;

		/**
		 *  This way we control the appearance what ever the system
		 */
		Rectangle2D.Float rect = new Rectangle2D.Float(0, 0, getWidth(), getHeight());
		g2.setColor(Cc.g(Cc.darkGrayBg));
		g2.fill(rect);
	}

}

/** *********************************************************************************  
 */
class BookNameBtn extends JButton implements MouseListener {
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	boolean hover = false;
	boolean pressed = false;
	boolean currentLin = false;
	boolean brokenLin = false;
	boolean examined = false;
	String mruKey = "";
	int index;

	AaBookPanelInner boss;

	public BookNameBtn(AaBookPanelInner boss, String text, int index, String mruKey) { /* Constructor */
		// ==============================================================================================
		super();

		this.boss = boss;
		this.index = index;
		this.mruKey = mruKey;

		setBorder(new EmptyBorder(0, 0, 0, 0));
		setFocusable(false);
//		setMinimumSize(new Dimension(1, 1));

		addMouseListener(this);

		setText(text + "                                             ");

		setToolTipText(text + "   ");
	}

	public void setButtonCurrentLin(boolean current) {
		if (current) {
			examined = true;
			brokenLin = false;
			currentLin = true;
		}
		else
			currentLin = false;
	}

	public void setButtonBrokenLin() {
		brokenLin = true;
	}

	public void mousePressed(MouseEvent e) {
		pressed = true;
		App.frame.repaint();
	}

	public void mouseReleased(MouseEvent e) {
		pressed = false;
		// a hack to make the two (empty) separation buttons act like background space
		if (e.getButton() == MouseEvent.BUTTON3 && getText().contentEquals("                                              ")) {
			App.frame.rightClickPasteTimer.start();
			return;
		}
		boss.bookButtonClicked(this);
		App.frame.repaint();
	}

	public void mouseEntered(MouseEvent e) {
		hover = true;
		App.frame.repaint();
	}

	public void mouseExited(MouseEvent e) {
		hover = false;
		App.frame.repaint();
	}

	public void mouseClicked(MouseEvent e) {
	}

	final static Color unexaminedColor = Aaa.veryWeedyBlack;
	final static Color examinedColor = Color.BLACK;
	final static Color hoverColor = Color.WHITE; // new Color( 0, 255, 0);
	final static Color pressedColor = Aaa.tutorialLinkNorm_g;
	final static Color currentLinColor = Color.BLACK;
	final static Color brokenColor = Color.RED;
	final static Color hasChapterMarksColor = Aaa.buttonBkgColorStd;

	public void paintComponent(Graphics g) {
//		super.paintComponent(g);  we do it all
		Graphics2D g2 = (Graphics2D) g;
		Aaa.commonGraphicsSettings(g2);

		g2.setColor(currentLin ? Aaa.tutorialBackground : Cc.g(Cc.darkGrayBg));

		int wb = getWidth();
		int hb = getHeight();

		g2.fillRect(0, 0, wb, hb);

		if ((mruKey.isEmpty() == false) && (App.mruCollection.getChapterMarksCount(mruKey) > 0)) {
			g2.setColor(hasChapterMarksColor);
			g2.fillRect(AaBookPanel.defaultWidthPixels - 20, 0, 6, hb);
		}

		// System.out.println(index + " " + examined);

		if (pressed)
			g2.setColor(currentLin ? Aaa.tutorialLinkHover_h : pressedColor);
		else if (brokenLin)
			g2.setColor(brokenColor);
		else if (currentLin)
			g2.setColor(currentLinColor);
		else if (hover)
			g2.setColor(hoverColor);
		else if (examined)
			g2.setColor(examinedColor);
		else
			g2.setColor(unexaminedColor);

		int height = getHeight();
		String text = getText();
		char first = (text.length() > 0) ? text.charAt(0) : 0x00;
		float nudgeUp = ((first == '>') || (first == '<')) ? -0.04f * height * 1 : 0.0f;

		Aaa.drawLeftString(g2, text, 4, nudgeUp, getWidth(), height);
	}
}