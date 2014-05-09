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
package com.rogerpf.aabridge.igf;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.controller.q_;
import com.rogerpf.aabridge.igf.MassGi.GraInfo;
import com.rogerpf.aabridge.model.Deal;
import com.rogerpf.aabridge.view.BidTablePanel;
import com.rogerpf.aabridge.view.HandDisplayGrid;

public class TutorialPanel extends ConsumePanel implements MouseListener, MouseMotionListener {
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	public QuestionPanel qp;

	public TutorialPanel() { // constructor
		// ==============================================================================================
		setOpaque(true);
		setBackground(Aaa.tutorialBackground);
		Dimension tryToBeAsBigAsPossible = new Dimension(3000, 2000);
		setPreferredSize(tryToBeAsBigAsPossible); // So that AspectBoundable can later squish us down

		addMouseMotionListener(this);
//		addMouseListener(this); // something else setting us as a "MouseListener" 
		// and we don't want to be on the list twice

		qp = new QuestionPanel();

		setVisible(true);
	}

	/**
	 * There is and can only ever be one question panel 
	 */
	public void action_lb(GraInfo gi) {
		// =============================================================================

		qp.setPositionYonlySetSize(gi.capEnv.lb_position, rowSpacing);
	}

	// ==============================================================================================
	public void cleanUp() {

//		for (Component c : getComponents()) {
//
//			@SuppressWarnings("unused")
//			int z = 0;
//		}

		removeAll();
	}

	public void clearAllFloatersIncQp() {
		// ==============================================================================================
//		for (Component c : getComponents()) {
//		    if ((c instanceof HandDisplayGrid) || (c instanceof BidTablePanel ))
//		    	remove( c);
//			@SuppressWarnings("unused")
//			int z = 0;
//		}
//		remove(qp);

		removeAll();

		// System.out.println("clearAllFloaters " + this);
	}

	public HandDisplayGrid addFloatingHand(HandDisplayGrid hdg, Deal deal) {
		// ==============================================================================================
		if (hdg == null) {
			hdg = new HandDisplayGrid(deal);
		}
		add(hdg);

		// System.out.println("addFloatingHand " + hdg);
		return hdg;
	}

	public BidTablePanel addBidTablePanel(BidTablePanel btp, Deal deal) {
		// ==============================================================================================
		if (btp == null) {
			btp = new BidTablePanel(deal);
		}
		add(btp);

		// System.out.println("addBidTablePanel " + btp);
		return btp;
	}

	public void lastFloaterAdded() {
		// ==============================================================================================
		// setVisible(getComponentCount() > 0);
		// System.out.println("lastFloaterAdded " + this);
	}

	public void addQp() {
		// ==============================================================================================
		add(qp);
	}

	public void matchToQuestion(char c, char t) {
		// ==============================================================================================
		qp.matchToQuestion(c, t);
	}

	/**
	 */
	public void action_ht(GraInfo gi) {
		// =============================================================================
		getCurSeg().eol = true;
		startNewSeg();
		yRow = (float) gi.numb * rowSpacing + topAdjust;
		xCol = leftMargin;

		heightOfNextLine = lineSeparation;
	}

	/**
	 */
	public void action_VT(GraInfo gi) {
		// =============================================================================
		xCol = (float) gi.numb * columnWidth;
//		if (xCol < leftMargin)
//			xCol = leftMargin;
	}

	/**
	 */
	public void action_margin(GraInfo gi) {
		// =============================================================================

		switch (gi.numb) {
		case 0:
			leftMargin = LEFT_MARGIN_DEFAULT * columnWidth;
			break;

		case 1:
			rightMargin = RIGHT_MARGIN_DEFAULT * columnWidth;
			break;

		case 2:
			leftMargin = xCol;
			if (leftMargin > rightMargin - minMarginSeparation)
				leftMargin = LEFT_MARGIN_DEFAULT * columnWidth;
			break;

		case 3:
			rightMargin = xCol;
			if (rightMargin < leftMargin + minMarginSeparation)
				rightMargin = RIGHT_MARGIN_DEFAULT * columnWidth;
			break;
		}

	}

//	final float SYMBOL_SCALE_FRAC = 1.15f;
//	final float SYMBOL_YPOS_ADJUST = 0.2f;

	public void mouseMoved(MouseEvent e) {
		// =============================================================================
		Point p = e.getPoint();
		boolean change = false;

		for (Seg seg : segs) {
			if (seg.hyperlink != null) {
				for (Ras ras : seg) {
					Rectangle2D bounds = ras.getCorrectedBounds();
					int mouse = seg.hyperlink.mouse;
//					if (mouse == Aaa.MOUSE_PRESSED)
//						continue;
					boolean prev = (mouse == Aaa.MOUSE_HOVER);
					boolean contains = bounds.contains(p);
					if (prev != contains) {
						seg.hyperlink.mouse = (contains) ? Aaa.MOUSE_HOVER : Aaa.MOUSE_NONE;
						change = true;
					}
				}
			}
		}
		if (change) {
			App.frame.repaint();
		}
	}

	public void mouseExited(MouseEvent e) {
		// =============================================================================
		boolean change = false;

		for (Seg seg : segs) {
			if (seg.hyperlink != null) {
				if (seg.hyperlink.mouse != Aaa.MOUSE_NONE) {
					seg.hyperlink.mouse = Aaa.MOUSE_NONE;
					change = true;
				}
			}
		}
		if (change) {
			App.frame.repaint();
		}
	}

	public void mousePressed(MouseEvent e) {
		// =============================================================================
		Point p = e.getPoint();
		boolean change = false;

		for (Seg seg : segs) {
			if (seg.hyperlink != null) {
				for (Ras ras : seg) {
					Rectangle2D bounds = ras.getCorrectedBounds();
					int mouse = seg.hyperlink.mouse;
					boolean prev = (mouse == Aaa.MOUSE_PRESSED);
					boolean contains = bounds.contains(p);
					if (prev != contains) {
						seg.hyperlink.mouse = (contains) ? Aaa.MOUSE_PRESSED : Aaa.MOUSE_NONE;
						change = true;
					}
				}
			}
		}
		if (change) {
			App.frame.repaint();
		}
	}

	public void mouseReleased(MouseEvent e) {
		// =============================================================================
		Point p = e.getPoint();
		boolean change = false;

		for (Seg seg : segs) {
			if (seg.hyperlink != null) {
				for (Ras ras : seg) {
					Rectangle2D bounds = ras.getCorrectedBounds();
					int mouse = seg.hyperlink.mouse;
					boolean prev = (mouse == Aaa.MOUSE_PRESSED);
					boolean contains = bounds.contains(p);
					if (prev != contains) {
						seg.hyperlink.mouse = (contains) ? Aaa.MOUSE_HOVER : Aaa.MOUSE_NONE;
						change = true;
					}
					if (contains) {
						seg.hyperlink.actionLink();
					}
				}
			}
		}
		if (change) {
			App.frame.repaint();
		}
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	}

	/**
	 */
	public void paintComponent(Graphics g) {
		// =============================================================================
		if (App.mg == null)
			return; // Can it realy happen that TutorialGraphics is null ? !

		mg = App.mg; // a class member, just for simple (less to write) access;

		super.paintComponent(g);

		g2 = (Graphics2D) g;
		Aaa.commonGraphicsSettings(g2);
		frc = g2.getFontRenderContext(); // used to iterate through text

		getSize(wh); // the width and height of the panel now

		width = (float) wh.width;
		height = (float) wh.height;

		scaleFrac = width / LIN_STANDARD_WIDTH;
		fontScaleFrac = FONT_SCALE_FRAC * scaleFrac;
		lineSeparationFrac = LINE_SEPARTATION_FRAC; // no mult this is a const // * scaleFrac;
		lineSeparation = 0;
		heightOfNextLine = 0; // set later by calc

		columnWidth = COLUMN_WIDTH_FRAC * width;
		// OLD WAY rowSpacing = ROW_HEIGHT_FRAC * width; // yes width - NEVER the 'two value' height
		// new way below
		float linesOnPage = (App.visualMode == App.Vm_DealAndTutorial ? LIN_LINES_ON_PAGE_SMALL : LIN_LINES_ON_PAGE_STD);
		rowSpacing = height / linesOnPage;

		topAdjust = TOP_ADJUST_FRAC * width; // yes width - NEVER the 'two value' height

		leftMargin = LEFT_MARGIN_DEFAULT * columnWidth;
		rightMargin = RIGHT_MARGIN_DEFAULT * columnWidth;
		minMarginSeparation = MIN_MARGIN_SERPARTATION * columnWidth;

		yRow = 0.0f + topAdjust;
		xCol = leftMargin;

		segs.clear(); // clear all from the last page display
		segs.add(new Seg(leftMargin, rightMargin)); // add our first new one WE MUST always have at least one seg

		/** 
		 * We examine the state of the 'gi' at   mg.stop_gi   as that is the state we want the screen 
		 * to be in when this  draw  is finished.
		 * 
		 * if it does not match then we set what we want, which will cause another draw
		 */

		GraInfo giLast = mg.giAy.get(mg.stop_gi);
		skip__mn_text = (giLast.capEnv.visualModeRequested != App.Vm_TutorialOnly); // ugg - WIDE AREA FLAG - ugg

		int ih_seen = 0;

		for (int i = mg.start_nt; i <= mg.end_pg; i++) {
			GraInfo gi = mg.giAy.get(i);
			int t = gi.qt;

			use_gray_text = (i < mg.middle_pg); // WIDE AREA FLAG - ugg

			// System.out.println(/* new Date().getTime() + " " + */gi);

			if (i == mg.start_nt) {
				// A special - the state of the mn-header is as known at the end
				// but it is displayed "under" everything else
				// NOT MY DESIGN
				consume_mn_TEXT(mg.giAy.get(mg.end_pg));
				continue;
			}

			// @formatter:off
			//			AA		CAPS means an RPf invention
			if (t == q_.sb) { /* detected in the sb panel so nothing at this time */ continue; } 
			if (t == q_.at) { consume_at(gi); continue; }
			if (t == q_.nt) { consume_nt(gi); /* calls consume_at() */ continue; }
			if (t == q_.mn) { consume_mn(gi); continue; }
			if (t == q_.ZS) { consume_suitSymbol(gi); continue; } 
			if (t == q_.Zd) { consume_emDash(gi); continue; } 
			if (t == q_.Zo) { consume_bulletPoint(gi); continue; } 
			if (t == q_.NL) { consume_newline(gi); continue; }
			if (t == q_.CB) { consume_centeringBegin(gi); continue; } 
			if (t == q_.CE) { consume_centeringEnd(gi); continue; } 
			if (t == q_.XB) { consume_boxDrawBegin(gi); continue; } 
			if (t == q_.XE) { consume_boxDrawEnd(gi); continue; } 
			if (t == q_.YB) { consume_hyperlinkBegin(gi); continue; } 
			if (t == q_.YE) { consume_hyperlinkEnd(gi); continue; } 
			if (t == q_.Z4) { consume_clearToEndOfScreen(gi); continue; } 
			if (t == q_.ia) { consume_insertAuction(gi); continue; } 
			if (t == q_.ih) { consume_insertHand(gi); ih_seen++; continue; } 
			if (t == q_.lb) { action_lb(gi); continue; }
			if (t == q_.ht) { action_ht(gi); continue; }
			if (t == q_.VT) { action_VT(gi); continue; }
			if (t == q_.ZN) { action_margin(gi); continue; } 
			if (t == q_.bt) { /* nothing at this time */ continue; } 
			if (t == q_.an) { /* nothing at this time */ continue; } 
			if (t == q_.st) { /* nothing at this time */ continue; } 
			if (t == q_.sk) { /* nothing at this time */ continue; } 
			if (t == q_.ha) { /* nothing at this time */ continue; } 
			if (t == q_.tu) { /* nothing at this time */ continue; } 
			if (t == q_.pg) { /* nothing at this time */ continue; } 
			if (t == q_.ub) { /* nothing at this time */ continue; } 
			if (t == q_.up) { /* nothing at this time */ continue; } 
			if (t == q_.mc) { /* nothing at this time */ continue; } 
			if (t == q_.pn) { /* nothing at this time */ continue; } 
			if (t == q_.rh) { /* nothing at this time */ continue; } 
			if (t == q_.ah) { /* nothing at this time */ continue; } 
			if (t == q_.md) { /* nothing at this time */ continue; } 
			if (t == q_.qx) { /* nothing at this time */ continue; } 
			if (t == q_.sv) { /* nothing at this time */ continue; } 
			if (t == q_.pc) { /* nothing at this time */ continue; } 
			if (t == q_.mb) { /* nothing at this time */ continue; } 
			if (t == q_.sk) { /* nothing at this time */ continue; } 
			if (t == q_.sb) { /* nothing at this time */ continue; } 
			if (t == q_.wt) { /* nothing at this time */ continue; } 
			if (t == q_.xx) { /* nothing at this time */ continue; } 
							
			// @formatter:on

			System.out.println("Tutorial Panel UnProcessed-gi: " + gi.type);
		}

		drawAllSegs(); // member of SeglinePanel our 'super'

		if (ih_seen > 0)
			floatingHandShrinkerBodge();

		App.mg.tpPaintCompleteIndication();
	}

	/**
	 */
	public void floatingHandShrinkerBodge() {
		// =============================================================================
		for (Component c1 : getComponents()) {
			if ((c1 instanceof HandDisplayGrid) == false)
				continue;
			HandDisplayGrid hdg1 = (HandDisplayGrid) c1;

			for (Component c2 : getComponents()) {
				if (c2 == c1)
					continue;
				if ((c2 instanceof HandDisplayGrid) == false)
					continue;
				HandDisplayGrid hdg2 = (HandDisplayGrid) c2;

				if (hdg1.getBounds().intersects(hdg2.getBounds())) {
					hdg1.shrink = true;
					hdg2.shrink = true;
				}
			}
		}

	}
}
