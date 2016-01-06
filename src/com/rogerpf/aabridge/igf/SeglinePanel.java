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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.view.ClickPanel;

public class SeglinePanel extends ClickPanel {
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	Graphics2D g2;

	FontRenderContext frc;

	MassGi mg;

//	boolean use_gray_text = false;
	boolean skip__mn_text = false;

	public static final float FONT_SCALE_FRAC = 0.604f;
	public static final float LINE_SEPARTATION_FRAC = 0.908f; // 0.921f;

	public static final float LIN_STANDARD_WIDTH = 1016;
	public static final float LIN_STANDARD_HEIGHT = 685;
	public static final float LIN_REDUCED_HEIGHT = 166;

	public static final float LIN_LINES_ON_PAGE_STD = LIN_STANDARD_HEIGHT / 25;
	public static final float LIN_LINES_ON_PAGE_SMALL = LIN_REDUCED_HEIGHT / 5;

	protected static final float TOP_ADJUST_FRAC = 0.017f;
	protected static final float COLUMN_WIDTH_FRAC = 1f / 26f;

	protected static final float LEFT_MARGIN_DEFAULT = 0.1f;
	protected static final float RIGHT_MARGIN_DEFAULT = 25.9f;
	protected static final float MIN_MARGIN_SERPARTATION = 0.4f;

	protected static final float INITIAL_MN_HEADER_Y_ONE_LINE = 3.1f;
	protected static final float INITIAL_MN_HEADER_Y_TWO_LINE = 2.2f;

	protected float width;
	protected float height;

	protected float fontScaleFrac;
	protected float lineSpacing_multiplier;
	protected float heightOfCurFontFrac;
	protected float heightOfCurFont;
	protected float maxHeightOnCurLine;
	protected boolean nonFont_on_this_line;

	protected float topAdjust;
	protected float columnWidth;
	protected float rowSpacing;

	protected float leftMargin;
	protected float rightMargin;
	protected float scaleFrac;

	protected float minMarginSeparation;

	protected float xCol;
	protected float yRow;

	Dimension wh = new Dimension();

	protected Segs segs = new Segs(this);

	public SeglinePanel() { // constructor
		// ==============================================================================================
	}

	/**
	 */
	public boolean isBoxed() {
		// =============================================================================
		return (segs.get(segs.size() - 1).boxNumber > 0);
	}

	/**
	 */
	public Seg getCurSeg() {
		// =============================================================================
		return segs.get(segs.size() - 1);
	}

	/**
	 */
	public void setCurSegEol() {
		// =============================================================================
		Seg seg = getCurSeg();
		seg.eol = true;
		seg.open = false;
	}

	/**
	 */
	public void setCurSegClosed() {
		// =============================================================================
		Seg seg = getCurSeg();
		seg.open = false;
	}

	/**
	 */
	public Seg startNewSeg() {
		// =============================================================================
		Seg seg = getCurSeg();
		int boxNumber = seg.boxNumber;
		seg.open = false;

		seg = new Seg(leftMargin, rightMargin);
		seg.boxNumber = boxNumber;
		segs.add(seg);

		return seg;
	}

	/**
	 */
	public void getNextBox(int from[], int to[]) {
		// =============================================================================
		from[0] = -1; // says we are done
		int prev = to[0];
		int numb = 0;
		for (int i = prev + 1; i < segs.size(); i++) {
			Seg seg = segs.get(i);
			if (numb == 0 && seg.boxNumber != 0) {
				numb = seg.boxNumber;
				from[0] = i;
				to[0] = i;
				continue;
			}
			if (numb != 0) {
				if (numb == seg.boxNumber) {
					to[0] = i;
					continue;
				}
				break;
			}
		}
	}

	/**
	 */
	public void calcBoxFrom(int fromInd, int mnBox[]) {
		// =============================================================================
		int from[] = { fromInd };
		int to[] = { -1 };
		while (true) {

			getNextBox(from, to);
			if (from[0] < 0)
				break;

			calcBox(from[0], to[0], mnBox);
		}
	}

	/**
	 */
	public void calcBox(int from, int to, int mnBox[]) {
		// =============================================================================
		float left = 99999999; // we are after a min value
		float right = 0;
		float top = 0;
		float bottom = 0;

		Color fill_color = Color.BLACK;

		Seg segBox = null;

		Boolean is_mnBox = false;

		for (int i = from; i <= to; i++) {
			Seg seg = segs.get(i);
			if (seg.size() == 0)
				return; // I am not playing this game

			is_mnBox = is_mnBox || seg.mnBox;

			Ras rasFirst = seg.get(0);
			Ras rasLast = seg.get(seg.size() - 1);

			if (i == from) { // first ras of first seg
				top = rasFirst.y - 1.0f * rasFirst.tl.getAscent(); // + rasFirst.tl.getDescent();
				fill_color = seg.fillColor;
				segBox = seg;
			}

			if (i == to) { // first ras of first seg - yes last ras would work as well)
				bottom = rasFirst.y + 1.7f * rasFirst.tl.getDescent();
			}

			if (left > rasFirst.x)
				left = rasFirst.x;

			float r = rasLast.x + rasLast.width;
			if (right < r) {
				right = r;
			}
		}

		if (is_mnBox) {
			for (int i = from; i <= to; i++) {
				Seg seg = segs.get(i);
				seg.mnBox = true;
			}
			mnBox[0] = from;
		}

		float width = right - left;
		float height = bottom - top;

		segBox.boxRect = new Rectangle2D.Float(left, top, width, height);

//		if (fill_color != null && fill_color.getRGB() == Color.WHITE.getRGB()) {
//			fill_color = Aaa.tutorialBackground;
//		}
		segBox.fillColor = fill_color;

//		g2.setColor(new Color(200, 10, 10));
//		g2.fill(segBox.boxRect);
//
//		g2.setStroke(new BasicStroke(0.9f * tp.scaleFrac));
//		g2.setColor(Color.BLACK);
//		

	}

	/**
	 */
	public void fillBox(int from) {
		// =============================================================================

		Seg boxSeg = segs.get(from);

		if (boxSeg == null || boxSeg.boxRect == null)
			return;

		g2.setColor(boxSeg.fillColor);
		g2.fill(boxSeg.boxRect);

		if (boxSeg.fillColor == Aaa.tutorialBackground) {
			g2.setStroke(new BasicStroke(0.9f * scaleFrac));
			g2.setColor(Color.BLACK);
			g2.draw(boxSeg.boxRect);
		}

	}

	/**
	 */
	public void drawAllSegs() { // remember - this is a ONE TIME CALL, new lines created on every refresh
		// =============================================================================

		// note that g2 is common and is set by the caller

		segs.centerAsNeeded();

		int mnBox[] = { -1 };

		int curBox = 0;

		/**
		 *  as a quick fix we want to suppress all boxes around hands in segs
		 */
		for (int i = 0; i < segs.size(); i++) {
			Seg seg = segs.get(i);

			if (seg.boxNumber > 0 && seg.surpressBox) {
				int supBox = seg.boxNumber;

				for (int k = 0; k < segs.size(); k++) {
					Seg segI = segs.get(k);
					if (segI.boxNumber == supBox)
						segI.boxNumber = 0;
				}
			}
		}

		/**
		 * First we calcualte all the positions of all the boxes
		 */
		for (int i = 0; i < segs.size(); i++) {
			Seg seg = segs.get(i);

			if (seg.boxNumber == 0)
				curBox = 0;

			if (seg.boxNumber != curBox) {
				curBox = seg.boxNumber;
				calcBoxFrom(i, mnBox);
			}
		}

		curBox = 0;

//		if (mnBox[0] != -1) {
//
//			Seg mnBoxSeg = segs.get(mnBox[0]);
//			int mnBoxNumber = mnBoxSeg.boxNumber;
//
//			for (int i = 0; i < segs.size(); i++) {
//				Seg seg = segs.get(i);
//
//				if (seg.boxNumber == mnBoxNumber)
//					continue; // we of course skip the mnBox itself
//
//				if (seg.boxNumber == 0)
//					curBox = 0;
//
//				if (seg.boxNumber == curBox)
//					continue; // we have seen this or it is not a 'box' seg
//
//				// Here we have the first box of (a series of) box segs
//
//				if (mnBoxSeg.boxRect == null || seg.boxRect == null) {
//					continue;
//				}
//
//				if (mnBoxSeg.boxRect.intersects(seg.boxRect)) {
//					segs.setBoxToAllHidden(mnBox[0]);
//					break;
//				}
//			}
//		}
//
//		curBox = 0;
//
//		/**
//		 * now fill the un obscured boxes
//		 */
//		for (int i = 0; i < segs.size(); i++) {
//			Seg seg = segs.get(i);
//
//			if (seg.boxNumber == 0)
//				curBox = 0;
//
//			if (seg.boxNumber != curBox) {
//				curBox = seg.boxNumber;
//				fillBox(i);
//			}
//		}

		for (int i = 0; i < segs.size(); i++) {
			Seg seg = segs.get(i);

			if (seg.boxNumber != 0 && seg.boxRect != null) {
				fillBox(i);
			}

			seg.draw(g2);
		}
	}

//	public void paintComponent(Graphics g) {
//		// ==============================================================================================
//
//		super.paintComponent(g);
//		g2 = (Graphics2D) g;
//		Aaa.commonGraphicsSettings(g2);
//
//		/**
//		 * Yes this does nothing
//		 * Unusually we require our decendent TutorialPanel to "Draw" first
//		 * so producing all the segments which we draw when it calls back with
//		 * 
//		 *   drawAllSegs(g2);  to do the actual drawing
//		 *   
//		 *   see the  ABOVE  function
//		 */
//	}

}
