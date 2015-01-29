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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
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

	/**   
	 */
	public void mouseReleased(MouseEvent e) {
		// =============================================================
		if (App.hideCommandBar)
			return;

		float relLoc = convertXcoordToRelLoc((float) e.getX());
		int gi_index = mg.jpPointAy.relLocToGiIndex(relLoc);
		mg.tutNavBarClicked(gi_index);
		App.frame.repaint();
		if (e.getButton() == MouseEvent.BUTTON3) {
			// so the "other button" will take us into the deal
			CmdHandler.tutorialIntoDealClever();
		}
	}

	public void mouseEntered(MouseEvent arg0) {
		if (entered == false) {
			entered = true;
			repaint();
		}
	}

	public void mouseExited(MouseEvent arg0) {
		if (entered == true) {
			entered = false;
			repaint();
		}
	}

	public void mousePressed(MouseEvent arg0) {
	}

	public void mouseClicked(MouseEvent arg0) {
	}

	/**   
	 */
	public void paintComponent(Graphics g) {
		// ============================================================================
		if (App.hideTutNavigationBar) {
			setBackground(Cc.g(Cc.baizeGreen));
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
					font = BridgeFonts.bridgeBoldFont.deriveFont(h * 0.5f);
				}
				g2.setColor((playedLineEnd > x) ? Cc.g(Cc.navUnplayedEntered) : playedColor);
				g2.setFont(font);
				Aaa.drawCenteredString(g2, jp.name, x, yb - h, mark, h * 0.65f);
			}
		}

		@SuppressWarnings("unused")
		int z = 0; // put your breakpoint here
	}

}
