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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

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

		setLayout(new MigLayout("flowy, insets 0 0 0 0, gap 0! 0!", "push[98%]", "[15%][79%]push"));

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
		for (Hand hand : App.deal.rota[App.compassFromPhyScreenPos(Zzz.West)]) {
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
class BidTablePanel2 extends ClickPanel implements MouseListener, MouseMotionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int origGbpWidth = 0;
	int originalWidth = 0;
	int originalHeight = 0;
	int requestedHeight = 0;

	public BidTablePanel2() { // constructor
		addMouseMotionListener(this);
	}

	// Handle mouse events.

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
//		System.out.println("MouseExited");
		int dealer = App.deal.dealer;
		int rounds;
		if (App.isMode(Aaa.REVIEW_BIDDING)) {
			rounds = App.gbp.c1_1__bfdp.bidA[dealer].size();
		}
		else {
			rounds = App.deal.hands[dealer].bids.size();
		}

		boolean change = false;
		for (int r = 0; r < rounds; r++) {
			for (int i : Zzz.rota[dealer]) {
				Bal bids;
				if (App.isMode(Aaa.REVIEW_BIDDING)) {
					bids = App.gbp.c1_1__bfdp.bidA[i];
				}
				else {
					bids = App.deal.hands[i].bids;
				}

				for (Bid bid : bids) {
					if (bid.rr2dBid != null) {
						if (bid.hover) {
							bid.hover = false;
							change = true;
						}
					}
				}
			}
		}
		if (change)
			repaint();
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
		App.gbp.biddingDisplayToggle();
	}

	public void mouseDragged(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
		// System.out.println("Mousemoved");

		Point ep = e.getPoint();

		int dealer = App.deal.dealer;

		int rounds;
		if (App.isMode(Aaa.REVIEW_BIDDING)) {
			rounds = App.gbp.c1_1__bfdp.bidA[dealer].size();
		}
		else {
			rounds = App.deal.hands[dealer].bids.size();
		}

		boolean change = false;

		for (int r = 0; r < rounds; r++) {
			for (int i : Zzz.rota[dealer]) {
				Bal bids;
				if (App.isMode(Aaa.REVIEW_BIDDING)) {
					bids = App.gbp.c1_1__bfdp.bidA[i];
				}
				else {
					bids = App.deal.hands[i].bids;
				}

				for (Bid bid : bids) {
					if (bid.rr2dBid != null && bid.alert && bid.alertText.isEmpty() == false) {
						boolean containsEp = bid.rr2dBid.contains(ep);
						// if (containsEp) {
						// System.out.println(ep + " " + bid + " " + bid.rr2dBid.x + " " + bid.rr2dBid.y);
						// }
						if (bid.hover != containsEp) {
							bid.hover = containsEp;
							change = true;
						}
					}
				}
			}
		}

		if (change)
			repaint();
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
		int cell = ((4 + dealer - App.compassFromPhyScreenPos(Zzz.West)) % 4) - 1;

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

				bid.rr2dBid = null;

				if (bid.alert) {
					// System.out.println(bid + " " + bid.alertText);
					float yB = lineHeight * ((1 + cell / 4) - 1) + (lineHeight * 0.08f);
					float width = sep * 0.86f;
					float height = lineHeight;
					float curve = height * 0.20f;

					// fill the lozenge ----------------------------------------------

					float leftBorder = width * 0.04f;
					float rightBorder = width * 0.03f;
					width -= leftBorder + rightBorder;

					float topBorder = height * 0.07f;
					float botBorder = height * 0.07f;
					height = height - topBorder - botBorder;
					g2.setPaint(bid.alertText.isEmpty() ? Aaa.bidEmpAlertColor : Aaa.bidAlertColor);
					bid.rr2dBid = new RoundRectangle2D.Float(x + leftBorder, yB, width, height, curve, curve);
					g2.fill(bid.rr2dBid);

					if (bid.hover && bid.alert && bid.alertText.isEmpty() == false) {
						g2.setFont(stdTextFont);
						FontMetrics fm = g2.getFontMetrics();
						int strWidth = fm.stringWidth(bid.alertText + "a");
						float xA = 0;
						if (cell % 4 == 3) {
							xA = 4 * sep * 0.95f - strWidth;
						}
						else if (cell % 4 > 0)
							xA = 2 * sep - strWidth / 2;

						float yA = yB - lineHeight * 0.1f;
						if (cell > 16)
							yA = yB - lineHeight * 2 + lineHeight * 0.1f;

						bid.rr2dAlertText = new RoundRectangle2D.Float(xA + leftBorder, yA + lineHeight, strWidth, height, curve * 2, curve * 2);
					}
				}

				if (bid.isPass()) {
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
					g2.setColor(Aaa.cdhsWeakColors[bid.getSuit()]);
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

		// second parse for the annotations

		cell = ((4 + dealer - App.compassFromPhyScreenPos(Zzz.West)) % 4) - 1;

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

				if (bid.hover && bid.rr2dAlertText != null && bid.alert && bid.alertText.isEmpty() == false) {
					g2.setColor(Aaa.bubbleAnotateCol);
					g2.fill(bid.rr2dAlertText);
					g2.setColor(Color.black);
					Aaa.drawCenteredString(g2, bid.alertText, bid.rr2dAlertText.x, bid.rr2dAlertText.y, bid.rr2dAlertText.width, bid.rr2dAlertText.height);
				}

			}
		}
	}

}
