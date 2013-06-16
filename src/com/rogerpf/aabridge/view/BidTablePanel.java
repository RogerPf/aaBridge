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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import net.miginfocom.swing.MigLayout;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.model.Bal;
import com.rogerpf.aabridge.model.Bid;
import com.rogerpf.aabridge.model.Hand;
import com.rogerpf.aabridge.model.Zzz;
import com.rpsd.bridgefonts.BridgeFonts;

/**   
 */
public class BidTablePanel extends ClickPanel { /* Constructor */

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	BidTablePanel2 rtp = null;

	// ----------------------------------------
	BidTablePanel() { /* Constructor */

		setLayout(new MigLayout("flowy, insets 0 0 0 0, gap 0! 0!", "2%[98.5%]", "[15%][79%]2%"));

		JScrollPane scroller = new JScrollPane(new BidTablePanel2());
		scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroller.setBorder(BorderFactory.createEmptyBorder());

		add(new BidTablePanelHeader(), "width 100%, height 100%");
		add(scroller, "width 100%, height 100%");

		setVisible(true);
	}

	/**
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		setBackground(Aaa.baizeGreen);
	}
}

/**   
 */
class BidTablePanelHeader extends ClickPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 */
	// /*con*/ BidTablePanelHeader() {
	// setVisible(true);
	// }

	/**
	 */
	public void paintComponent(Graphics g) { /* Table Header */
		// super.paintComponent(g);
		// setBackground(new Color(245, 245, 245));

		Graphics2D g2 = (Graphics2D) g;
		Aaa.commonGraphicsSettings(g2);

		float width = (float) getWidth();
		float height = (float) getHeight();

		g2.setFont(BridgeFonts.bridgeLightFont.deriveFont(height * 0.8f));

		float xBlk = 0;
		float sep = width * 0.24f;
		float x = width * 0.04f;
		float y = height * (1 - 0.17f);
		int c = 0;
		for (Hand hand : App.deal.rota[App.compassFromPhyScreenPos(Zzz.WEST)]) {
			boolean vun = App.deal.vunerability[hand.compass % 2];
			g2.setColor((vun) ? Aaa.vunerableColor : Aaa.handAreaOffWhite);
			g2.fill(new Rectangle2D.Float(xBlk, 0, sep + 0.5f + ((++c == 4) ? 100 : 0), height));
			g2.setColor((vun) ? Aaa.handAreaOffWhite : Aaa.weedyBlack);
			g2.drawString(hand.getCompassStLong(), x, y);
			xBlk += sep;
			x += sep;
		}
	}
}

/**   
 */
class BidTablePanel2 extends ClickPanel implements MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int origGbpWidth = 0;
	int originalWidth = 0;
	int originalHeight = 0;
	int requestedHeight = 0;

	// Handle mouse events.

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
		App.gbp.biddingDisplayToggle();
	}

	/**
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		setBackground(Aaa.bidTableBkColor);

		Graphics2D g2 = (Graphics2D) g;
		Aaa.commonGraphicsSettings(g2);

		if (originalWidth == 0) {
			originalWidth = getWidth();
			origGbpWidth = App.gbp.getWidth();
			originalHeight = getHeight();
			requestedHeight = originalHeight;
		}

		float maxLines = 5;
		float scalar = (float) App.gbp.getWidth() / (float) origGbpWidth;
		float lineHeight = scalar * (float) originalHeight / (float) maxLines;
		float sep = scalar * originalWidth * 0.24f;
		float x;
		float y;

		Font cardFaceFont = BridgeFonts.faceAndSymbFont.deriveFont(lineHeight * 0.80f);
		Font suitSymbolsFont = BridgeFonts.faceAndSymbFont.deriveFont(lineHeight * 0.80f);
		Font stdTextFont = BridgeFonts.bridgeLightFont.deriveFont(lineHeight * 0.6f);
		Font DoubleRedoubleFont = BridgeFonts.bridgeBoldFont.deriveFont(lineHeight * 0.75f);

		int dealer = App.deal.dealer;
		int cell = ((4 + dealer - App.compassFromPhyScreenPos(Zzz.WEST)) % 4) - 1;

		int rounds;
		if (App.isMode(Aaa.REVIEW_BIDDING)) {
			rounds = App.gbp.c1_1__bfdp.bidA[dealer].size();
		}
		else {
			rounds = App.deal.hands[dealer].bids.size();
		}

		for (int r = 0; r < rounds; r++) {
			for (int i : Zzz.rota[dealer]) {
				Bal bids;
				if (App.isMode(Aaa.REVIEW_BIDDING)) {
					bids = App.gbp.c1_1__bfdp.bidA[i];
				}
				else {
					bids = App.deal.hands[i].bids;
				}

//				Hand hand = App.deal.hands[i];
				cell++;
				if (bids.size() == r)
					break;
				Bid bid = bids.get(r);
				x = (cell % 4) * sep;
				y = lineHeight * (1 + cell / 4) - (lineHeight * 0.2f);

				if (bid == App.deal.PASS) {
					g2.setColor(Aaa.weedyBlack);
					x += lineHeight * 0.40f;
					g2.setFont(stdTextFont);
					g2.drawString(bid.getCallSt(), x, y);
				}
				else if (bid.isCall()) { // i.e. PASS DOUBLE or REDOUBLE

					g2.setColor(Aaa.weedyBlack);
					x += lineHeight * 0.40f;
					g2.setFont(DoubleRedoubleFont);
					g2.drawString(bid.getCallSt(), x, y);
				}
				else { // Normal suit and NT bids
					x += lineHeight * 0.50f;
					g2.setColor(Aaa.weedyBlack);
					g2.setFont(cardFaceFont);
					g2.drawString(bid.getLevelSt(), x, y);

					x += lineHeight * 0.50f;
					g2.setColor(Aaa.cdhsWeakColors[bid.getSuitValue()]);
					g2.setFont(suitSymbolsFont);
					g2.drawString(bid.getSuitSt(), x, y);
				}
			}
			int wantedHeight = (int) (((cell + 3) / 4) * lineHeight);
			if (requestedHeight != wantedHeight) {
				requestedHeight = wantedHeight;
				// System.out.println( wantedHeight);
				setPreferredSize(new Dimension(getWidth(), wantedHeight));
				revalidate();
			}
		}
	}

}
