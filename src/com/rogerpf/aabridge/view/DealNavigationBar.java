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
import java.awt.Dimension;
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
import com.rogerpf.aabridge.igf.MassGi;
import com.rogerpf.aabridge.model.Cc;
import com.rpsd.bridgefonts.BridgeFonts;

/**
 */
public class DealNavigationBar extends JPanel implements MouseListener {
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	static float LINE_END_GAP = 0.03f;
	static float BAR_THICKNESS = 0.255f; // 0.1 is lost in the drop for a clean base
	static float BIG_MARK_THICKNESS = 0.7f; // ditto

	Graphics2D g2;
	MassGi mg;

	float width;
	float height;

	public boolean entered = false;

	/**
	 */
	public DealNavigationBar() { /* Constructor */
		// =============================================================
		setOpaque(false); // we do the background ourselves

		setPreferredSize(new Dimension(5000, 500)); // We just try to fill the available space
		addMouseListener(this);
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
		float x = (float) e.getX();

		if (App.deal.isDoneHand()) // so we skip the 'done hand'
			return;

//		if (isInsideWheelButton(e)) {
//			App.useMouseWheel = !App.useMouseWheel;
//			App.frame.repaint();
//			return;
//		}

		for (int i = 0; i < App.dePointAy.size(); i++) {
			boolean last = (i == App.dePointAy.size() - 1);
			DePoint dp = App.dePointAy.get(i);

			if (dp.to > x || last) {

				if (dp.type == 'b') {
					App.setMode(Aaa.REVIEW_BIDDING);
					App.reviewBid = dp.bidInd;
					CmdHandler.validateBiddingIndex();
					validate();
					break;
				}

				if (dp.type == 'c') {
					App.setMode(Aaa.REVIEW_PLAY);
					App.reviewTrick = dp.trickInd;
					App.reviewCard = dp.cardInd;
					CmdHandler.validateReviewIndexes();
					break;
				}

				if (dp.type == 'f') {
					App.setMode(Aaa.REVIEW_PLAY);
					App.reviewTrick = dp.trickInd;
					App.reviewCard = dp.cardInd;
					CmdHandler.validateReviewIndexes();
				}
			}
		}

		App.gbp.matchPanelsToDealState();
		App.frame.repaint();
		App.con.controlerInControl();
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
		Color claimedColor = entered ? Aaa.navClaimedIntense : Aaa.navClaimedNormal;
		Color unplayedColor = entered ? Cc.g(Cc.navUnplayedEntered) : Aaa.navDarkIntense;
		Color playedColor = entered ? Aaa.navLightIntense : Aaa.navLightNormal;

//		Color backgroundColor = entered ? Aaa.baizeGreenNav : Cc.g(Cc.baizeGreen);
		super.paintComponent(g);

		g2 = (Graphics2D) g;
		Aaa.commonGraphicsSettings(g2);

		mg = App.mg; // for ease of access

		Dimension wh = getSize(); // the width and height of the panel now

		Rectangle2D r = new Rectangle(0, 0, wh.width, wh.height);
		if (entered) {
			g2.setPaint(new GradientPaint(0, 0, Cc.g(Cc.baizeGreen), 0, wh.height, Aaa.baizeGreenNav));
		}
		else {
			g2.setColor(Cc.g(Cc.baizeGreen));
		}
		g2.fill(r);

		if (App.deal.isDoneHand()) // so we skip the 'done hand'
			return;

		width = (float) wh.width;
		height = (float) wh.height;

		App.dePointAy = new DePointAy(width, LINE_END_GAP);

		float line_end_gap = width * LINE_END_GAP;
		float x = line_end_gap;
		// float y;
		float yb = height * 1.1f;

		float adjust = 1.2f;

		float hl = height * BAR_THICKNESS * adjust;
		float h = height * BIG_MARK_THICKNESS * adjust;

		g2.setFont(BridgeFonts.internatBoldFont.deriveFont(h * 0.5f));

		float futureStart = 0;
		/**
		 */
		for (int i = 0; i < App.dePointAy.size(); i++) {
			DePoint dp = App.dePointAy.get(i);

			float mark = dp.to - dp.from;
			x = dp.from;

			if (futureStart == 0.0f && dp.state == 'u') {
				futureStart = dp.from;
			}

			if (dp.spike == false)
				continue;

			g2.setColor((dp.state == 'h') ? playedColor : ((dp.state == 'm') ? claimedColor : unplayedColor));
			g2.fill(new RoundRectangle2D.Float(x, yb - h, mark, h, h * 0.4f, h * 0.4f));

			if (dp.text.length() > 0) {
				g2.setColor((dp.state == 'h') ? Cc.g(Cc.navUnplayedEntered) : Aaa.navLightText);

				Aaa.drawCenteredString(g2, dp.text, x, yb - h, mark, h * 0.65f);
			}
		}

		/**
		 *  Draw the UN/PLAYED line across as needed
		 */

		float mainFrom = App.dePointAy.get(0).from;
		float mainTo = App.dePointAy.getLast().to;
		g2.setColor(playedColor);
		g2.fill(new Rectangle2D.Float(mainFrom, yb - hl, mainTo - mainFrom, hl));

		if (futureStart == 0) {
			futureStart = mainTo;
		}
		float gig = 0.05f;
		g2.setColor(unplayedColor);
		g2.fill(new Rectangle2D.Float(futureStart, yb - hl * (1 + gig), mainTo - futureStart, hl * (1 - gig)));

	}

}
