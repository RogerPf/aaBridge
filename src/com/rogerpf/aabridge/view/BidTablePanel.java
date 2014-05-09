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
import com.rogerpf.aabridge.model.Call;
import com.rogerpf.aabridge.model.Cc;
import com.rogerpf.aabridge.model.Deal;
import com.rogerpf.aabridge.model.Dir;
import com.rogerpf.aabridge.model.Hand;
import com.rpsd.bridgefonts.BridgeFonts;

/**   
 */
public class BidTablePanel extends ClickPanel { /* Constructor */
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	Deal deal;
	boolean floating;

	BidTablePanelHeader btph;
	JScrollPane scroller;
	BidTablePanel2 btp2;

	/**
	 */
	BidTablePanel() { /* Constructor */
		// ==============================================================================================
		deal = App.deal;
		floating = false;
		common_constructor(); // adds the real panel
	}

	/**
	 */
	public BidTablePanel(Deal deal) { /* Constructor for floating  bid table */
		// ==============================================================================================
		this.deal = deal;
		floating = true;
		common_constructor(); // adds the real panel
	}

	/**
	 */
	void common_constructor() {
		// ==============================================================================================
		setOpaque(false);
//		setBackground(Aaa.baizeGreen);

		if (floating) {
			/** 
			 * When floating we have to self position and size this because it changes
			 * length depending on the number of lines in is showing
			 */
			setLayout(null);
		}
		else {
			setLayout(new MigLayout(App.simple + ", flowy", "2%[98%]", "[15%][79%]6%"));
		}

		btph = new BidTablePanelHeader(deal, floating);
		btp2 = new BidTablePanel2(deal, floating);

		scroller = new JScrollPane(btp2);
		scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroller.setBorder(BorderFactory.createEmptyBorder());

		if (floating) {
			add(btph);
			add(scroller);
		}
		else {
			add(btph, App.hm1oneHun);
			add(scroller, App.hm1oneHun);
		}
	}

	/**   
	 */
	public void dealMajorChange() {
		// =============================================================================
		assert (floating == false);
		deal = App.deal;
		btp2.dealMajorChange();
		btph.dealMajorChange();
	}

	private static final float tableWidthAsFractionOfTupWidth = 0.32f;
	private static final float headerHeightAsFractionOfTableWidth = 0.08f;

	/**
	 */
	public void setPositionReturnSize(int x, int y, int tupWidth, int wh[]) {
		// ==============================================================================================
		/** We base all size calculations on the WIDTH of the current Tutorial Panel
		 *  Note we are being called at 'paint time' so these value must exist valid
		 */
		assert (floating);

		int width = wh[0] = (int) (tupWidth * tableWidthAsFractionOfTupWidth * (float) deal.columnsInBidTable / 4 + 0.5f);
		int thHeader = (int) (width * headerHeightAsFractionOfTableWidth * 4 / deal.columnsInBidTable + 0.5f);

		int btp2Width = (width); // -20
		int btp2Height = btp2.getCalculatedHeight(btp2Width);

		wh[1] = thHeader + btp2Height; // (int) (tupWidth * tableWidthAsFractionOfTupWidth * heightFactor + 0.5f);

		setBounds(x, y, wh[0], wh[1]);

		btph.setBounds(0, 0, width, thHeader); // co-ords are internal to us
		scroller.setBounds(0, thHeader, btp2Width, thHeader + btp2Height);
		// btp2.setBounds(0, thHeader, btp2Width, thHeader + btp2Height);

		// System.out.println("BidTablePanel - setPositionReturnSize" + x + " " + y + " " + wh[0] + " " + wh[1] + " tup width " + App.tup.getWidth());
	}

}

/**   
 */
class BidTablePanelHeader extends ClickPanel {
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	Deal deal;
	boolean floating;

	/**
	 */
	public BidTablePanelHeader(Deal deal, Boolean floating) { /* Constructor for floating  bid table */
		// ==============================================================================================

		this.deal = deal;
		this.floating = floating;
		setVisible(true);
	}

	/**   
	 */
	public void dealMajorChange() {
		// =============================================================================
		assert (floating == false);
		deal = App.deal;
	}

	/**
	 */
	public void paintComponent(Graphics g) { /* Table Header */
		// ==============================================================================================
		super.paintComponent(g);
		// setBackground(new Color(245, 245, 245));

		Graphics2D g2 = (Graphics2D) g;
		Aaa.commonGraphicsSettings(g2);

		float width = (float) getWidth();
		float height = (float) getHeight();

		g2.setFont(BridgeFonts.bridgeLightFont.deriveFont(height * 0.8f));

		float xBlk = 0;
		float sep = width * 0.24f * 4 / deal.columnsInBidTable;
		float x = width * 0.04f;
		float y = height * (1 - 0.17f);
		int c = 0;
		int i = -1;
		for (Hand hand : deal.rota[App.cpeFromPhyScreenPos(Dir.West).v]) {
			i++;
			if (deal.columnsInBidTable == 2 && (i % 2 == 1))
				continue;
			boolean vun = deal.vulnerability[hand.compass.v % 2];
			g2.setColor((vun) ? Aaa.vulnerableColor : Aaa.handAreaOffWhite);
			g2.fill(new Rectangle2D.Float(xBlk, 0, sep + 0.5f + ((++c == 4) ? 100 : 0), height));
			g2.setColor((vun) ? Aaa.handAreaOffWhite : Aaa.weedyBlack);
			String text = (deal.columnsInBidTable == 2 ? hand.compass.toOpenResp() : hand.compass.toLongStr());
			g2.drawString(text, x, y);
			xBlk += sep;
			x += sep;
		}
	}
}

/**   
 */
class BidTablePanel2 extends ClickPanel implements MouseListener, MouseMotionListener {
	// ==============================================================================================
	private static final long serialVersionUID = 1L;

	Deal deal;
	boolean floating;
	int widthSetByBoss = 0;
	int reportedHeight = 0;
	int requestedHeight = 0;

	public BidTablePanel2(Deal deal, Boolean floating) { // constructor
		// ==============================================================================================
		setOpaque(true);
		setBackground(Aaa.bidTableBkColor);
		this.deal = deal;
		this.floating = floating;
		addMouseMotionListener(this);
	}

	/**   
	 */
	public void dealMajorChange() {
		// =============================================================================
		assert (floating == false);
		deal = App.deal;
	}

	// Handle mouse events.

	public void mouseEntered(MouseEvent e) {
		// ==============================================================================================
	}

	public void mouseExited(MouseEvent e) {
		// ==============================================================================================
//		System.out.println("MouseExited");
		Dir dealer = deal.dealer;
		int rounds;
		if (App.isMode(Aaa.REVIEW_BIDDING)) {
			rounds = App.gbp.c1_1__bfdp.bidA[dealer.v].size();
		}
		else {
			rounds = deal.hands[dealer.v].bids.size();
		}

		boolean change = false;
		for (int r = 0; r < rounds; r++) {
			for (Dir p : Dir.rota[dealer.v]) {
				Bal bids;
				if (App.isMode(Aaa.REVIEW_BIDDING)) {
					bids = App.gbp.c1_1__bfdp.bidA[p.v];
				}
				else {
					bids = deal.hands[p.v].bids;
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
			App.frame.repaint();
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

		Dir dealer = deal.dealer;

		int rounds;
		if (App.isMode(Aaa.REVIEW_BIDDING)) {
			rounds = App.gbp.c1_1__bfdp.bidA[dealer.v].size();
		}
		else {
			rounds = deal.hands[dealer.v].bids.size();
		}

		boolean change = false;

		for (int r = 0; r < rounds; r++) {
			for (Dir p : Dir.rota[dealer.v]) {
				Bal bids;
				if (App.isMode(Aaa.REVIEW_BIDDING)) {
					bids = App.gbp.c1_1__bfdp.bidA[p.v];
				}
				else {
					bids = deal.hands[p.v].bids;
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
			App.frame.repaint();
	}

	/**
	 */
	public int getCalculatedHeight(int btp2Width) {
		widthSetByBoss = btp2Width;
		if (reportedHeight == 0)
			reportedHeight = 100;
		return reportedHeight;
	}

	private static final float singleLineHeightAsRatioOfWidth = 0.092f;

	/**
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;
		Aaa.commonGraphicsSettings(g2);

		float x;
		float y;

		float widthScroller;

		if (floating)
			widthScroller = widthSetByBoss;
		else
			widthScroller = getParent().getWidth(); // as the scroller 'of course' does not resize us

		Dir dealer = deal.dealer;
		int rounds;
		if (App.isMode(Aaa.REVIEW_BIDDING)) {
			rounds = App.gbp.c1_1__bfdp.bidA[dealer.v].size();
		}
		else {
			rounds = deal.hands[dealer.v].bids.size();
		}

		boolean qmShowing = deal.showBidQuestionMark;
		Dir dirNextToBid = deal.getNextHandToBid().compass;
		if (qmShowing && (dirNextToBid == dealer)) {
			rounds++;
		}

		float lineHeight = widthScroller * singleLineHeightAsRatioOfWidth * 4 / deal.columnsInBidTable;

		float sep = widthScroller / 4.1f;
		x = 0;
		y = 0;

		Font cardFaceFont = BridgeFonts.faceAndSymbFont.deriveFont(lineHeight * 0.80f);
		Font suitSymbolsFont = BridgeFonts.faceAndSymbFont.deriveFont(lineHeight * 0.80f);
		Font stdTextFont = BridgeFonts.bridgeLightFont.deriveFont(lineHeight * 0.6f);
		Font doubleRedoubleFont = BridgeFonts.bridgeBoldFont.deriveFont(lineHeight * 0.75f);

		int cell = ((4 + dealer.v - App.cpeFromPhyScreenPos(Dir.West).v) % 4) - 1;

		for (int r = 0; r < rounds; r++) {
			for (Dir p : Dir.rota[dealer.v]) {
				Bal bids;
				if (floating) {
					bids = deal.hands[p.v].bids;
				}
				else if (App.isMode(Aaa.REVIEW_BIDDING)) {
					bids = App.gbp.c1_1__bfdp.bidA[p.v];
				}
				else {
					bids = deal.hands[p.v].bids;
				}

				if (qmShowing && (p == dirNextToBid)) {
					bids = (Bal) bids.clone(); // so we don't mess up the original
					bids.add(new Bid(Call.NullBid)); // will show as a Question mark
				}

//				Hand hand = deal.hands[i];
				cell++;
				if (bids.size() == r)
					break;
				Bid bid = bids.get(r);
				x = (cell % 4) * sep;
				y = lineHeight * (1 + cell / 4) - (lineHeight * 0.2f);

				bid.rr2dBid = null;

				if ((deal.columnsInBidTable == 4) || (p == Dir.West) || (p == Dir.East)) {
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
						g2.drawString(bid.call.toBidPanelString(), x, y);
					}
					else if (bid.isNullBid()) { // used by us to show a question mark
						g2.setColor(floating ? Color.black : Cc.g(Cc.blackWeak));
						x += lineHeight * 0.40f;
						g2.setFont(doubleRedoubleFont);
						g2.drawString("  ?", x, y);
					}
					else if (bid.isCall()) { // i.e. PASS DOUBLE or REDOUBLE
						g2.setColor(floating ? Color.black : (bid.isPass() ? Aaa.weedyBlack : Cc.g(Cc.blackWeak)));
						x += lineHeight * 0.40f;
						g2.setFont(doubleRedoubleFont);
						g2.drawString(bid.call.toBidPanelString(), x, y);
					}
					else { // Normal suit and NT bids
						x += lineHeight * 0.50f;
						g2.setColor(floating ? Color.black : Cc.g(Cc.blackWeak));
						g2.setFont(cardFaceFont);
						g2.drawString(bid.level.toStr(), x, y);

						x += lineHeight * 0.50f;
						g2.setColor(bid.suit.color(floating ? Cc.Ce.Strong : Cc.Ce.Weak));
						g2.setFont(suitSymbolsFont);
						g2.drawString(bid.suit.toStrNu(), x, y);
					}
				}
			}
		}
		int activeLines = (cell + 3) / 4;
		int wantedHeight = (int) (0.5f + activeLines * lineHeight);

		if (reportedHeight > 0 && requestedHeight != wantedHeight) {
			requestedHeight = wantedHeight;
			// System.out.println( wantedHeight);
			setPreferredSize(new Dimension(getWidth(), wantedHeight));
			revalidate();
		}

		reportedHeight = wantedHeight;

		// second parse for the annotations

		cell = ((4 + dealer.v - App.cpeFromPhyScreenPos(Dir.West).v) % 4) - 1;

		for (int r = 0; r < rounds; r++) {
			for (Dir p : Dir.rota[dealer.v]) {
				Bal bids;
				if (App.isMode(Aaa.REVIEW_BIDDING)) {
					bids = App.gbp.c1_1__bfdp.bidA[p.v];
				}
				else {
					bids = deal.hands[p.v].bids;
				}

				if (qmShowing && (p == dirNextToBid)) {
					bids = (Bal) bids.clone(); // so we don't mess up the original
					bids.add(new Bid(Call.NullBid)); // will show as a Question mark
				}

//				Hand hand = deal.hands[p.v];
				cell++;
				if (bids.size() == r)
					break;
				Bid bid = bids.get(r);

				if (bid.hover && bid.rr2dAlertText != null && bid.alert && bid.alertText.isEmpty() == false) {
					g2.setFont(stdTextFont);
					g2.setColor(Aaa.bubbleAnotateCol);
					g2.fill(bid.rr2dAlertText);
					g2.setColor(Color.black);
					Aaa.drawCenteredString(g2, bid.alertText, bid.rr2dAlertText.x, bid.rr2dAlertText.y, bid.rr2dAlertText.width, bid.rr2dAlertText.height);
				}

			}
		}
	}

}
