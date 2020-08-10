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
import com.rogerpf.aabridge.model.Cc;
import com.rogerpf.aabridge.model.Dir;
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
		setOpaque(false);
		// setBackground(Aaa.baizeGreen);
		setVisible(false);
	}

	/**
	 */
	public void paintComponent(Graphics g) { // WeTheyScorePanel
		super.paintComponent(g);

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
		g2.setColor(Cc.g(Cc.blackWeak));
		g2.draw(rr);
		g2.setColor(Aaa.weedyBlack);
		g2.setFont(BridgeFonts.internationalFont.deriveFont(lozWidth * 0.065f));

		// We display the cards as seen by - 'the target'

		int targetSeat = 0; // No Instead just have the North South as the left and column
//		switch (App.scoringFollows) {
//		case App.scoringFollowsYou: targetSeat = App.deal.getTheYouSeat(); break;
//		case App.scoringFollowsDeclarer: targetSeat = App.getDeclarerOrTheDefaultYouSeat(); break;
//		case App.scoringFollowsSouthZone: targetSeat = App.deal.hands[App.cpeFromPhyScreenPos(Cpe.South)].compass.v; break;
//		}
		int targetAxis = targetSeat % 2;

		String firstPair;
		String secondPair;
		if ((App.deal.getTheYouSeat().v % 2) == targetAxis) {
			firstPair = "You";
			secondPair = "They";
		}
		else {
			firstPair = "They";
			secondPair = "You";
		}

		g2.drawString(firstPair + " (" + Dir.axisStr(targetAxis) + ")", x1, y1);
		g2.drawString(secondPair + " (" + Dir.axisStr((targetAxis + 1) % 2) + ")", x1 + lozWidth * 0.42f, y1);

		float inset = lozWidth * 0.10f;
		float left = leftInset + inset;
		float top = topInset + lozWidth * 0.25f;

		g2.setStroke(new BasicStroke(panelWidth * 0.008f));
		g2.draw(new Line2D.Float(left, top, left + lozWidth - inset * 2, top));

		float leftHalf = panelWidth / 2;
		float lTop = topInset + lozHeight * 0.10f;
		g2.draw(new Line2D.Float(leftHalf, lTop, leftHalf, lTop + lozHeight * 0.81f));

		if (App.deal.isDoneHand())
			return;

		Point score = App.deal.getBoardScore();

		int mult = ((score.x + score.y > 0) ? 1 : -1);
		int side = ((score.x + score.y < 0) ? 1 : 0);

		x1 += lozWidth * 0.15f + lozWidth * 0.30f * side;
		y1 += lozWidth * 0.15f;

		g2.setFont(BridgeFonts.internatBoldFont.deriveFont(lozWidth * 0.080f));

		if (score.x != 0) {
			g2.drawString(String.format("%d", score.x * mult), x1, y1);
		}

		y1 += lozWidth * 0.12f;

		if (score.y != 0) {
			g2.drawString(String.format("%d", score.y * mult), x1, y1);
		}

	}

}
