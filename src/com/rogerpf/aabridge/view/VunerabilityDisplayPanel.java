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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JPanel;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.model.Cc;
import com.rogerpf.aabridge.model.Dir;
import com.rogerpf.aabridge.model.Lin;
import com.rpsd.bridgefonts.BridgeFonts;

/**
 */
class VulnerabilityDisplayPanel extends JPanel implements MouseListener {

	/**
	 */
	private static final long serialVersionUID = 1L;

	/**
	 */
	VulnerabilityDisplayPanel() { /* Constructor */
		setOpaque(false);
		// setBackground(Aaa.baizeGreen);
		this.addMouseListener(this);
	}

	public static Color getVulnerabilityColor(boolean vulnerability) {
		return ((vulnerability) ? Aaa.vulnerableColor : Aaa.vunOffWhite);
	}

	public static Color getVulnerabilityTextColor(boolean vulnerability) {
		return ((vulnerability) ? Aaa.vunOffWhite : Color.BLACK);
	}

	public void mouseReleased(MouseEvent e) {
		if (App.isMode(Aaa.EDIT_HANDS)) {
			App.deal.setNextDealerAndVulnerability(App.deal.realBoardNo);
			App.deal.wipeContractBiddingAndPlay();
			App.dealMajorChange();
			App.frame.repaint();
		}
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	/**
	 */
	private void fillSeat(Graphics2D g2, Dir phyPos, float x, float y, float w, float h, float c) {

		Dir compass = App.cpeFromPhyScreenPos(phyPos);

		g2.setColor(getVulnerabilityColor(App.deal.vulnerability[compass.v % 2]));

		g2.fill(new RoundRectangle2D.Double(x, y, w, h, c, c));

		if (App.deal.dealer.v == compass.v) {
			Font font = BridgeFonts.bridgeBoldFont.deriveFont(w < h ? w : h);
			g2.setFont(font);
			g2.setColor(getVulnerabilityTextColor(App.deal.vulnerability[compass.v % 2]));

			Aaa.drawCenteredString(g2, new String("D"), x, y, w, h);
		}
	}

	/**
	 */
	private void fillCenterBox(Graphics2D g2, float x, float y, float w, float h) {

		g2.setColor(Aaa.vunOffWhite);
		g2.fill(new Rectangle2D.Float(x, y, w, h));

		float gap = h / 20;

		if (App.isMode(Aaa.EDIT_HANDS)) {
			g2.setColor(Cc.RedWeak);
			g2.fill(new Rectangle2D.Float(x + gap, y + gap, w - gap * 2, h - gap * 2));

			Font font = BridgeFonts.bridgeLightFont.deriveFont(h * 0.4f);
			g2.setFont(font);
			g2.setColor(Color.WHITE);
			Aaa.drawCenteredString(g2, "Click", x, y, w, h / 1.7f);

			Aaa.drawCenteredString(g2, "Me", x, y + h / 2.1f, w, h / 2);

		}
		else {
			String text = (App.deal.signfBoardId.trim().isEmpty() ? "Board" : App.deal.signfBoardId);
			String bNumbText = (App.deal.displayBoardId.length() > 0) ? App.deal.displayBoardId : App.deal.realBoardNo + "";
			if (App.mg.lin.linType == Lin.VuGraph) {
				if (App.mg.ddAy.twoColumn) {
					text = (App.deal.qx_room == 'o') ? "Open" : "Closed";
				}
				bNumbText = App.deal.qx_number + "";
			}

			g2.setColor(Aaa.vulnerabilityBox);
			g2.fill(new Rectangle2D.Float(x + gap, y + gap, w - gap * 2, h - gap * 2));

			Font font = BridgeFonts.bridgeLightFont.deriveFont(h * 0.4f);
			g2.setFont(font);
			g2.setColor(Color.BLACK);
			Aaa.drawCenteredString(g2, text, x, y, w, h / 1.7f);

			Aaa.drawCenteredString(g2, bNumbText + "", x, y + h / 2.1f, w, h / 2);
		}

	}

	/**
	 */
	public void paintComponent(Graphics g) { // VulnerabilityDisplayPanel
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;
		Aaa.commonGraphicsSettings(g2);

		float panelWidth = (float) getWidth();
		float panelHeight = (float) getHeight();

		// fill the lozenge ----------------------------------------------
		float insetFromLeft = panelWidth * 0.05f;
		float insetFromTop = panelHeight * 0.05f;
		float useableWidth = panelWidth - insetFromLeft * 2;
		float useableHeight = panelHeight - insetFromTop * 2;
		float curve = useableHeight * 0.10f;

		// g2.setPaint(Color.blue);
		//
		// g2.fill( new Rectangle2D.Double( insetFromLeft, insetFromLeft,
		// useableWidth, useableHeight) );

		float vulLozengeThickness = useableHeight / 5.0f;
		float gap = vulLozengeThickness / 8.0f;
		float northSouthWidth = useableWidth - (vulLozengeThickness + gap) * 2.0f;
		float eastWestHeight = useableHeight - (vulLozengeThickness + gap) * 2.0f;

		fillSeat(g2, Dir.West, insetFromLeft, insetFromLeft + vulLozengeThickness + gap, vulLozengeThickness, eastWestHeight, curve);

		fillSeat(g2, Dir.East, insetFromLeft + vulLozengeThickness + northSouthWidth + gap * 2, insetFromLeft + vulLozengeThickness + gap, vulLozengeThickness,
				eastWestHeight, curve);

		fillSeat(g2, Dir.North, insetFromLeft + vulLozengeThickness + gap, insetFromLeft, northSouthWidth, vulLozengeThickness, curve);

		fillSeat(g2, Dir.South, insetFromLeft + vulLozengeThickness + gap, insetFromLeft + vulLozengeThickness + eastWestHeight + gap * 2, northSouthWidth,
				vulLozengeThickness, curve);

		fillCenterBox(g2, insetFromLeft + vulLozengeThickness + gap, insetFromLeft + vulLozengeThickness + gap, northSouthWidth, eastWestHeight);

	}

}
