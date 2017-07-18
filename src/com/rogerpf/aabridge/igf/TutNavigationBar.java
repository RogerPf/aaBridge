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
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JPanel;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.controller.CmdHandler;
import com.rogerpf.aabridge.model.Cc;
import com.rpsd.bridgefonts.BridgeFonts;

/**
 */
public class TutNavigationBar extends JPanel implements MouseListener {
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	static float LINE_END_GAP = 0.03f;
	static float BAR_THICKNESS = 0.255f; // 0.1 is lost in the drop for a clean base
	static float BIG_MARK_THICKNESS = 0.7f; // ditto

	static Color COLOR_LINE_UNPLAYED = Aaa.tutorialBackground;
	static Color COLOR_LINE_PLAYED = Aaa.darkGrayBg;

	Graphics2D g2;
	MassGi mg;

	float width;
	float height;

	public boolean entered = false;

	/**   
	 */
	public TutNavigationBar() { /* Constructor */
		// =============================================================
		setBackground(Aaa.baizeGreen);

		setPreferredSize(new Dimension(5000, 500)); // We just try to fill the available space
		addMouseListener(this);
	}

	/**   
	 */
	float convertXcoordToRelLoc(float x) {
		// ============================================================================
		float line_end_gap = width * LINE_END_GAP;

		if (x <= line_end_gap)
			return 0.0f;
		if (x >= (width - line_end_gap))
			return 1.0f;

		x = (x - line_end_gap) / (width - 2 * line_end_gap);

		return x;
	}

//	/**
//	 */
//	boolean isInsideWheelButton(MouseEvent e) {
//		// =============================================================
//		float bWidth = width * Aaa.butWheelWidthFraction;
//		float bHeight = height * Aaa.butWheelHeightFraction;
//		float xBut = width - bWidth;
//		float yBut = height * (1.0f - Aaa.butWheelHeightFraction);
//
//		return (new Rectangle2D.Float(xBut, yBut, bWidth, bHeight)).contains(new Point.Float(e.getX(), e.getY()));
//	}

	/**   
	 */
	public void mouseReleased(MouseEvent e) {
		// =============================================================
		if (App.hideCommandBar)
			return;

//		if (isInsideWheelButton(e)) {
//			App.useMouseWheel = !App.useMouseWheel;
//			App.frame.repaint();
//			return;
//		}

		float relLoc = convertXcoordToRelLoc((float) e.getX());
		int gi_index = mg.jpPointAy.relLocToGiIndex(relLoc);
		mg.tutNavBarClicked(gi_index);
		boolean ctrlKey_depressed = ((e.getModifiers() & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK);
		if (ctrlKey_depressed) {
			if (App.mg.mruChap != null) {
				int current_pg_numb = App.mg.get_current_pg_number_display();
				App.aaHomeBtnPanel.fill_chapterMarkerMenu();
				App.mg.mruChap.toggleChapterMark(current_pg_numb);
				App.frame.repaint();
				App.aaHomeBtnPanel.mruDelayedSaveTimer_Short_Start();
			}
		}
		App.frame.repaint();
		if (e.getButton() == MouseEvent.BUTTON3) {
			// so the "other button" will take us into the deal
			CmdHandler.tutorialIntoDealClever();
		}
	}

	public void mouseEntered(MouseEvent e) {
		if (entered == false) {
			entered = true;
			repaint();
		}
	}

	public void mouseExited(MouseEvent e) {
		if (entered == true) {
			entered = false;
			repaint();
		}
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	}

	/**   
	 */
	public void paintComponent(Graphics g) {
		// ============================================================================
		setBackground(Cc.g(Cc.baizeGreen));

		if (App.hideTutNavigationBar) {
			super.paintComponent(g);
			return;
		}

		Color unplayedColor = entered ? Cc.g(Cc.navUnplayedEntered) : Aaa.navDarkIntense;
		Color playedColor = entered ? Aaa.navLightIntense : Aaa.navLightNormal;
		Color backgroundColor = entered ? Aaa.baizeGreenNav : Cc.g(Cc.baizeGreen);
		setBackground(backgroundColor);

		super.paintComponent(g);

		g2 = (Graphics2D) g;
		Aaa.commonGraphicsSettings(g2);

		mg = App.mg; // for ease of access

		Dimension wh = new Dimension();
		getSize(wh); // the width and height of the panel now

		width = (float) wh.width;
		height = (float) wh.height;

		if (entered) {
			int h = wh.height;
			Rectangle2D r = new Rectangle(0, 0, wh.width, h);
			g2.setPaint(new GradientPaint(0, 0, Cc.g(Cc.baizeGreen), 0, h, Aaa.baizeGreenNav));
			g2.fill(r);
		}

		/**
		 *  Draw the UNPLAYED line all the way across
		 */
		float line_end_gap = width * LINE_END_GAP;
		float x = line_end_gap;

		float adjust = 1.2f;

		float hl = height * BAR_THICKNESS * adjust;
		float h = height * BIG_MARK_THICKNESS * adjust;

		float yb = height * 1.1f;

		float lengthMainLine = width - 2 * line_end_gap;

		g2.setColor(unplayedColor);
		g2.fill(new Rectangle2D.Float(x, yb - hl, lengthMainLine, hl));

		if (mg.jpPointAy == null)
			return;

		/**
		 *  Draw the PLAYED line to the point we have reached in the tutorial
		 */
		float relLoc = mg.jpPointAy.giIndexToRelLoc(mg.end_pg);
		// System.out.println("reLoc :" + relLoc);
		float playedLength = (int) (lengthMainLine * relLoc);

		g2.setColor(playedColor);
		g2.fill(new Rectangle2D.Float(x, yb - hl, playedLength, hl));

		float playedLineEnd = x + playedLength;

		Font font = null;
		/**
		 */
		for (JpPoint jp : mg.jpPointAy) {

			float mark = jp.mark * lengthMainLine;
			x = line_end_gap + jp.x0 * lengthMainLine;

			g2.setColor((playedLineEnd > x) ? playedColor : unplayedColor);
			g2.fill(new RoundRectangle2D.Float(x, yb - h, mark, h, h * 0.4f, h * 0.4f));

			if (jp.large) {
				if (font == null) {
					font = BridgeFonts.internatBoldFont.deriveFont(h * 0.5f);
				}
				g2.setColor((playedLineEnd > x) ? Cc.g(Cc.navUnplayedEntered) : playedColor);
				g2.setFont(font);
				Aaa.drawCenteredString(g2, jp.name, x, yb - h, mark, h * 0.65f);
			}
		}

		if (mg.mruChap != null) {
			float mh = hl * 1.8f;
			float mw = hl * 0.8f;
			float curve = hl * 0.4f;

			float xAdjustPos = mw * 0.8f;

			float xAdjustTest = mw * 0.1f;

			float y2 = yb - (mh + hl * 0.65f);

			Color rimColor;
			Color fillColor;

			for (int marked_pg : mg.mruChap.marks) {
				int giInd = mg.get_gi_numb_from_pg_number_display(marked_pg);
				float markLoc = mg.jpPointAy.giIndexToRelLoc(giInd);
				// System.out.println("mark: " + marked_pg + "  gi: " + giInd + "  markLoc: " + markLoc);

				float x2 = line_end_gap + lengthMainLine * markLoc - xAdjustPos;

				float lowPoint = x2 - xAdjustTest;
				float highPoint = x2 + xAdjustTest;

				float plEnd_adj = playedLineEnd - xAdjustPos;

				if (plEnd_adj < lowPoint) {
					fillColor = Cc.BlackWeak;
					rimColor = playedColor;
				}
				else if (highPoint < plEnd_adj) {
					fillColor = Cc.BlackWeedy;
					rimColor = Cc.BlackStrong;
				}
				else {
					fillColor = Aaa.hoverColor;
					fillColor = Aaa.buttonBkgColorStd;
					rimColor = Cc.BlackStrong;
				}

				RoundRectangle2D mShape = new RoundRectangle2D.Float(x2, y2, mw, mh, curve, curve);
				g2.setColor(fillColor);
				g2.fill(mShape);

				g2.setStroke(new BasicStroke(hl * 0.2f));
				g2.setColor(rimColor);
				g2.draw(mShape);
			}
		}

		@SuppressWarnings("unused")
		int z = 0; // put your breakpoint here
	}

}
