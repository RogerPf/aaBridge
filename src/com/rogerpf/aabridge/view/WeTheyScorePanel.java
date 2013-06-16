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

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.model.Zzz;
import com.rpsd.bridgefonts.BridgeFonts;

/**
 */
public class WeTheyScorePanel extends ClickPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 */
	WeTheyScorePanel() {
		setVisible(false);
	}

	/**
	 */
	public void paintComponent(Graphics g) { // WeTheyScorePanel

		super.paintComponent(g);
		setBackground(Aaa.baizeGreen);
//		setBackground(Color.black);

		Graphics2D g2 = (Graphics2D) g;
		Aaa.commonGraphicsSettings(g2);

		float panelWidth = (float) getWidth();
		float panelHeight = (float) getHeight();

		// fill the lozenge ----------------------------------------------
		float leftInset = panelWidth * 0.02f;
		float topInset = panelWidth * 0.04f;
		float lozWidth = panelWidth - leftInset * 2;
		float lozHeight = panelHeight - topInset * 2;
		float curve = lozWidth * 0.10f;

		RoundRectangle2D.Float rr = new RoundRectangle2D.Float(leftInset, topInset, lozWidth, lozHeight, curve, curve);
		g2.setColor(Aaa.genOffWhite);

		float x1 = leftInset + lozWidth * 0.15f;
		float y1 = topInset + lozWidth * 0.07f;

		g2.fill(rr);
		g2.setStroke(new BasicStroke(panelWidth * 0.015f));
		g2.setColor(Aaa.weedyBlack);
		g2.draw(rr);
		g2.setFont(BridgeFonts.bridgeLightFont.deriveFont(lozWidth * 0.065f));

		// We display the cards as seen by the player in the phypos 'South'
		int compassWe = App.deal.hands[App.compassFromPhyScreenPos(Zzz.SOUTH)].compass;

		g2.drawString("We (" + Zzz.compass_to_ns_ew_st[compassWe] + ")", x1, y1);
		g2.drawString("They (" + Zzz.compass_to_ns_ew_st[(compassWe + 1) % 4] + ")", x1 + lozWidth * 0.42f, y1);

		float inset = lozWidth * 0.10f;
		float left = leftInset + inset;
		float top = topInset + lozWidth * 0.25f;

		g2.setStroke(new BasicStroke(panelWidth * 0.008f));
		g2.draw(new Line2D.Float(left, top, left + lozWidth - inset * 2, top));

		float leftHalf = panelWidth / 2;
		float lTop = topInset + lozHeight * 0.10f;
		g2.draw(new Line2D.Float(leftHalf, lTop, leftHalf, lTop + lozHeight * 0.81f));

		Point score = new Point(-500, -120);

		score = App.deal.getBoardScore();

		int mult = ((score.x + score.y > 0) ? 1 : -1);
		int side = ((score.x + score.y < 0) ? 1 : 0);

		x1 += lozWidth * 0.15f + lozWidth * 0.30f * side;
		y1 += lozWidth * 0.15f;

		g2.setFont(BridgeFonts.bridgeBoldFont.deriveFont(lozWidth * 0.080f));

		if (score.x != 0) {
			g2.drawString(String.format("%d", score.x * mult), x1, y1);
		}

		y1 += lozWidth * 0.12f;

		if (score.y != 0) {
			g2.drawString(String.format("%d", score.y * mult), x1, y1);
		}

	}

}
