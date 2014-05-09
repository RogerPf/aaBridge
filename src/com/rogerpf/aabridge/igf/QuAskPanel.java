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

import java.awt.Graphics;
import java.awt.Graphics2D;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.controller.q_;
import com.rogerpf.aabridge.igf.MassGi.GraInfo;

/**   
 */
public class QuAskPanel extends ConsumePanel {
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	/**
	 */
	QuAskPanel() { /* Constructor */
		// ============================================================================
		setOpaque(false);
	}

	/**
	 */
	public void paintComponent(Graphics g) {
		// ============================================================================
		super.paintComponent(g);

		mg = App.mg; // a class member, just for simple (less to write) access;

		if (mg.stop_gi != mg.end_pg)
			return;

		GraInfo gi_o = App.mg.giAy.get(mg.end_pg);

		if (gi_o.qt != q_.lb)
			return;

		GraInfo gi = mg.new GraInfo(gi_o); // make a copy

//		// @formatter:off
//		System.out.println("paint paint pa - Question ---- " 
//						+ gi.bb.getSafe(1) + " - " + gi.bb.getSafe(2) + " - " + gi.bb.getSafe(3)
//				+ " - " + gi.bb.getSafe(4) + " - " + gi.bb.getSafe(5) + " - " + gi.bb.getSafe(6));
//		// @formatter:on

		g2 = (Graphics2D) g;
		Aaa.commonGraphicsSettings(g2);

		frc = g2.getFontRenderContext(); // used to iterate through text

		getSize(wh); // the width and height of the panel now

		width = getParent().getParent().getWidth(); // (float) wh.width;
		height = (float) wh.height;

		scaleFrac = width / LIN_STANDARD_WIDTH;
		fontScaleFrac = FONT_SCALE_FRAC * scaleFrac;
		lineSeparationFrac = LINE_SEPARTATION_FRAC; // no mult this is a const // * scaleFrac;
		lineSeparation = 0;
		heightOfNextLine = 0; // set later by calc

		columnWidth = COLUMN_WIDTH_FRAC * width;
		// OLD WAY rowSpacing = ROW_HEIGHT_FRAC * width; // yes width - NEVER the 'two value' height
		// new way below
		// float linesOnPage = (App.visualMode == App.Vm_DealAndTutorial ? LIN_LINES_ON_PAGE_SMALL : LIN_LINES_ON_PAGE_STD);
		float linesOnPage = LIN_LINES_ON_PAGE_SMALL;
		rowSpacing = height / linesOnPage;

		topAdjust = 2 * TOP_ADJUST_FRAC * width; // yes width - NEVER the 'two value' height

		leftMargin = 1 /* LEFT_MARGIN_DEFAULT */* columnWidth;
		rightMargin = 9 /* RIGHT_MARGIN_DEFAULT */* columnWidth;
		minMarginSeparation = MIN_MARGIN_SERPARTATION * columnWidth;

		yRow = 0.0f + topAdjust;
		xCol = leftMargin;

		segs.clear(); // clear all from the last page display
		segs.add(new Seg(leftMargin, rightMargin)); // add our first new one WE MUST always have at least one seg

		/** 
		 * We examine the state of the 'gi' at   mg.stop_pg   as that is the state we want the screen 
		 * to be in when this  draw  is finished.
		 * 
		 * if it does not match then we set what we want, which will cause another draw
		 */

//		GraInfo giLast = mg.giAy.get(mg.stop_gi);
//		skip__mn_text = (giLast.capEnv.visualModeRequested != App.Vm_TutorialOnly); // ugg - WIDE AREA FLAG - ugg
//
		char c = gi.bb.get(1).charAt(0);

		switch (c) {
		case 'b':
		case 'c':
		case 'h':
		case 'm':
		case 'p':
		case 'l':
		case 's':
		case 'd':
		case 't':
		case 'y':
			gi.text = gi.bb.get(2);
			break;
		case 'z':
			gi.text = "";
			break;
		default:
			gi.text = "Unknown question type: !lb!*" + gi.bb.get(1);
			break;
		}

		if (c == 'm' || c == 'y' || c == 'z') {
			rightMargin = 25 * columnWidth;
		}

		gi.capEnv.centered = true;

		consume_at(gi);

		drawAllSegs();
	}

}
