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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

import javax.swing.BorderFactory;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.Timer;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.Aaf;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.model.Bal;
import com.rogerpf.aabridge.model.Bid;
import com.rogerpf.aabridge.model.Call;
import com.rogerpf.aabridge.model.Cc;
import com.rogerpf.aabridge.model.Deal;
import com.rogerpf.aabridge.model.Dir;
import com.rogerpf.aabridge.model.Hand;
import com.rogerpf.aabridge.model.Suit;
import com.rpsd.bridgefonts.BridgeFonts;

import net.miginfocom.swing.MigLayout;

/**   
 */
public class BidTablePanel extends ClickPanel { /* Constructor */
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	Deal deal;
	boolean floating;

	BidTablePanelHeader btph;
	JScrollPane scroller;
	public BidTablePanel2 btp2;

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
		btp2 = new BidTablePanel2(deal, floating, this);

		scroller = new JScrollPane(btp2);
		scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroller.setBorder(BorderFactory.createEmptyBorder());

		scroller.getVerticalScrollBar().setUnitIncrement(8); // 16 * n

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

	/**
	 */
	public void setAlteredPosition(int x) {
		// ==============================================================================================
		assert (floating);

		setLocation(x, getLocation().y); // keep our y position
	}

	/**
	*/
	public Timer scrollToEndDelayTimer = new Timer(0 /* millisecs*/, new ActionListener() {
		// ---------------------------------- Timer -------------------------------------
		public void actionPerformed(ActionEvent evt) {
			// =============================================================================
			scrollToEndDelayTimer.stop();
			JScrollBar sb = scroller.getVerticalScrollBar();
			sb.setIgnoreRepaint(false);
			sb.setValue(sb.getMaximum()); // 100% far end
		}
	});

	public void biddingTableHasExpanded_ind() {
		// ==============================================================================================
		JScrollBar sb = scroller.getVerticalScrollBar();
		sb.setIgnoreRepaint(true);
		sb.setUnitIncrement(4);
		sb.setValue(sb.getMaximum());
		scrollToEndDelayTimer.start();
	}

	public void displayFinalAnotation(boolean showIt) {
		btp2.displayFinalAnotation(showIt);
	}

	public void AlertDisplaySet(boolean b) {
		btp2.lastBidAlertDisplay = b;
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

		g2.setFont(BridgeFonts.internationalFont.deriveFont(height * 0.78f));

		float xBlk = 0;
		float sep = width * 0.24f * 4 / deal.columnsInBidTable;
		float x = width * 0.04f;
		float y = height * (1 - 0.20f);
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
			String text = (deal.columnsInBidTable == 2 ? hand.compass.toOpenResp() : Dir.getLangDirStr(hand.compass));
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

	boolean lastBidAlertDisplay = true;
	int previousBidCount = 0;

	BidTablePanel owner = null;

	public BidTablePanel2(Deal deal, Boolean floating, BidTablePanel owner_p) { // constructor
		// ==============================================================================================
		owner = owner_p;
		setOpaque(true);
		setBackground(Aaa.bidTableBkColor);
		this.deal = deal;
		this.floating = floating;
		addMouseMotionListener(this);
	}

	public void displayFinalAnotation(boolean showIt) {

		if (showIt) {
			if (anotationAssistTimer.isRunning() == false) {
				anotationAssistTimer.start();
			}
		}
		else {
			lastBidAlertDisplay = false;
		}
	}

	/**
	*/
	public Timer anotationAssistTimer = new Timer(50, new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			// =============================================================
			anotationAssistTimer.stop();
			anotationAssistTimer.setDelay(50);
			lastBidAlertDisplay = true;
			// System.out.println(" set lastBidAlertDisplay ON");
			invalidate();
		}
	});

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
		lastBidAlertDisplay = false;
	}

	public void mouseExited(MouseEvent e) {
		// ==============================================================================================
//		System.out.println("MouseExited");
		lastBidAlertDisplay = false;

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
		lastBidAlertDisplay = false;
	}

	public void mouseReleased(MouseEvent e) {
		lastBidAlertDisplay = false;
	}

	public void mouseClicked(MouseEvent e) {
		lastBidAlertDisplay = false;
	}

	public void mouseDragged(MouseEvent e) {
		lastBidAlertDisplay = false;
	}

	public void mouseMoved(MouseEvent e) {
		// System.out.println("Mousemoved");
		lastBidAlertDisplay = false;

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
		// ==============================================================================================
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
		int countBids = 0;
		if (App.isMode(Aaa.REVIEW_BIDDING)) {
			rounds = App.gbp.c1_1__bfdp.bidA[dealer.v].size();
			countBids = App.gbp.c1_1__bfdp.bidA[0].size() + App.gbp.c1_1__bfdp.bidA[1].size() + App.gbp.c1_1__bfdp.bidA[2].size()
					+ App.gbp.c1_1__bfdp.bidA[3].size();
		}
		else {
			rounds = deal.hands[dealer.v].bids.size();
			countBids = deal.countBids();
		}

		boolean qmShowing = deal.showBidQuestionMark && (App.isVmode_InsideADeal() == false);
		Dir dirNextToBid = deal.getNextHandToBid().compass;
		if (qmShowing && (dirNextToBid == dealer)) {
			rounds++;
		}

		float lineHeight = (widthScroller * singleLineHeightAsRatioOfWidth * 4) / deal.columnsInBidTable;

		float sep = widthScroller / 3.9f;
		x = 0;
		y = 0;

		Font cardFaceFont = BridgeFonts.faceAndSymbolFont.deriveFont(lineHeight * 0.80f);
		Font suitSymbolsFont = BridgeFonts.faceAndSymbolFont.deriveFont(lineHeight * 0.80f);
		Font ntFont = BridgeFonts.internatBoldFont.deriveFont(lineHeight * 0.65f);

		Font passFont = BridgeFonts.internationalFont.deriveFont(lineHeight * 0.5f);
		Font symbolsFont = BridgeFonts.faceAndSymbolFont.deriveFont(lineHeight * 0.75f);
		Font doubleRedoubleFont = BridgeFonts.internatBoldFont.deriveFont(lineHeight * 0.75f);

		Font alertFont = BridgeFonts.internatBoldFont.deriveFont(lineHeight * 0.75f);

		int cell = ((4 + dealer.v - App.cpeFromPhyScreenPos(Dir.West).v) % 4) - 1;

		if (cell == 0 /*1st column*/ && rounds >= 5 && (countBids % 4 == 0)) {
			// we are trying to fix the case where the last bid should be displayed in an extended table but is ignored
			rounds++;
		}

		int bidCount = 0;
		Bid lastBid = null;

		// lastBidAlertDisplay = false; // now set externally when we enter tutorial mode

		for (int r = 0; r < rounds; r++) {
			for (Dir p : Dir.rota[dealer.v]) {
				cell++; // so we are on zero if west
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

				bidCount += bids.size();

				if (qmShowing && (p == dirNextToBid)) {
					bids = (Bal) bids.clone(); // so we don't mess up the original
					bids.add(new Bid(Call.NullBid)); // will show as a Question mark
				}

				if (bids.size() == r)  // yes (bids.size() > r) passes through !
					break;

				Bid bid = bids.get(r);
				lastBid = bid;

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

						width *= (deal.columnsInBidTable == 2) ? 1.8f : 0.9f; // 2018-04-02 wider 2 col aterts

						bid.rr2dBid = new RoundRectangle2D.Float(x + leftBorder, yB, width, height, curve, curve);
						if (bid.alertText.isEmpty() == false) {
							g2.setPaint(Aaa.bidAlertHasTxtColor);
							g2.fill(bid.rr2dBid);
						}
						// outline the actual alert box
						g2.setColor(Cc.RedStrong);
						g2.setStroke(new BasicStroke(lineHeight * 0.06f));
						g2.draw(bid.rr2dBid);
					}

					if (bid.isNullBid()) { // used by us to show a question mark
						g2.setColor(floating ? Color.black : Cc.g(Cc.blackWeak));
						x += lineHeight * 0.40f;
						g2.setFont(doubleRedoubleFont);
						g2.drawString("  ?", x, y);
					}
					else if (bid.isPass()) {
						g2.setColor(Aaa.weedyBlack);
						x += lineHeight * 0.40f;
						g2.setFont(passFont);
						float adj = lineHeight * 0.2f;
						g2.drawString(Aaf.game_pass, x + adj, y - lineHeight * 0.12f);
					}
					else if (bid.isDouble()) { // i.e. DOUBLE or REDOUBLE
						g2.setColor(floating ? Color.black : Cc.g(Cc.blackWeak));
						x += lineHeight * 0.40f;
						g2.setFont(doubleRedoubleFont);
						float adj = lineHeight * 0.5f;
						g2.drawString("X", x + adj, y);
					}
					else if (bid.isReDouble()) { // i.e. DOUBLE or REDOUBLE
						g2.setColor(floating ? Color.black : Cc.g(Cc.blackWeak));
						x += lineHeight * 0.40f;
						g2.setFont(doubleRedoubleFont);
						float adj = lineHeight * 0.25f;
						g2.drawString("XX", x + adj, y);
					}
					else { // Normal suit and NT bids
						x += lineHeight * 0.50f;
						g2.setColor(floating ? Color.black : Cc.g(Cc.blackWeak));
						g2.setFont(cardFaceFont);
						g2.drawString(bid.level.toStr(), x, y);

						x += lineHeight * 0.50f;
						g2.setColor(bid.suit.color(floating ? Cc.Ce.Strong : Cc.Ce.Weak));
						if (bid.suit == Suit.NoTrumps) {
							g2.setFont(ntFont);
							g2.drawString(Aaf.game_nt, x, y);
						}
						else {
							g2.setFont(suitSymbolsFont);
							g2.drawString(bid.suit.toStrLower(), x, y);
						}
					}
				}
			}
		}

		if (bidCount != previousBidCount) {
			lastBidAlertDisplay = true;
			previousBidCount = bidCount;
		}

		int activeLines = (cell + 3) / 4;
		int wantedHeight = (int) (0.5f + activeLines * lineHeight);

		if (reportedHeight > 0 && requestedHeight != wantedHeight) {
			requestedHeight = wantedHeight;
			setPreferredSize(new Dimension(getWidth(), wantedHeight));
			owner.biddingTableHasExpanded_ind();
			revalidate();
		}

		reportedHeight = wantedHeight;

		// second parse for the annotations / alerts

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

				cell++;
				if (bids.size() <= r)
					break; // Otherwise the null bid = question mark causes problems - display thread fails

				Bid bid = bids.get(r);

				if ((bid.rr2dBid == null || (!bid.hover && !(this.lastBidAlertDisplay && (bid == lastBid))) || bid.alertText.isEmpty())) {
					continue;
				}

				// we have alert text to display

				// first outline the select alert box
				{
					float pc = 0.05f;
					float ax = bid.rr2dBid.width * pc, ax2 = ax * 2f;
					float ay = bid.rr2dBid.height * pc * 2f, ay2 = ay * 2f;
					RoundRectangle2D.Float rbigger = new RoundRectangle2D.Float(bid.rr2dBid.x - ax, bid.rr2dBid.y - ay, bid.rr2dBid.width + ax2,
							bid.rr2dBid.height + ay2, bid.rr2dBid.arcwidth, bid.rr2dBid.archeight);

					g2.setColor(Aaa.bidAlertBubbleCol);
					g2.setStroke(new BasicStroke(lineHeight * 0.30f));
					g2.draw(rbigger);

					// replace the alert box that has just been overwritten

					g2.setColor(Cc.RedStrong);
					g2.setStroke(new BasicStroke(lineHeight * 0.055f));
					g2.draw(bid.rr2dBid);
				}

				String sIn = bid.alertText;
				String sTemp = sIn.replace("@", "");
				if (sTemp.length() <= 7) {
					if (sTemp.length() < 3) {
						sIn = "     " + sIn + "     ";
					}
					else if (sTemp.length() < 4) {
						sIn = "   " + sIn + "   ";
					}
					else if (sTemp.length() < 5) {
						sIn = "  " + sIn + "  ";
					}
					else
						sIn = " " + sIn + " ";
				}
				String sPs = sIn.replace("@", "");
				AttributedString astr = new AttributedString(sPs);

				astr.addAttribute(TextAttribute.FONT, alertFont);

				boolean prevWasAt = false;
				for (int i = 0, j = 0; i < sIn.length(); i++) {
					char c = sIn.charAt(i);
					if (c == '@') {
						prevWasAt = true;
						continue;
					}
					j++;
					if (prevWasAt == false)
						continue;
					prevWasAt = false;

					Suit suit = Suit.charToSuit(c);
					if (suit == Suit.Invalid)
						continue;

					astr.addAttribute(TextAttribute.FONT, symbolsFont, j - 1, j);
					astr.addAttribute(TextAttribute.FOREGROUND, suit.color(Cc.Ce.Strong), j - 1, j);
				}

				float curve = bid.rr2dBid.height * 0.20f;

				float leftBorder = widthScroller * 0.02f;
				float rightBorder = widthScroller * 0.01f;
				float widthMaxLozengeWidth = widthScroller - (leftBorder + rightBorder);
				float singleLozengeHeight = lineHeight * 1.3f;

				float widthMaxDisplay = widthMaxLozengeWidth - (leftBorder + rightBorder); // yes again

				float xA = 0;

				float yA = 0;
				float yB = 0;

				RoundRectangle2D.Float rr2d;

				AttributedCharacterIterator para = astr.getIterator();
				int paragraphStart = para.getBeginIndex();
				int paragraphEnd = para.getEndIndex();

				LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(para, g2.getFontRenderContext());

				lineMeasurer.setPosition(paragraphStart);

				TextLayout layout = lineMeasurer.nextLayout(widthMaxDisplay);

				if (lineMeasurer.getPosition() == paragraphEnd) {

					yB = lineHeight * ((1 + cell / 4) - 1) + (lineHeight * 0.08f);
					yA = yB - lineHeight * 0.1f;

					float textWidth = layout.getAdvance() + widthMaxDisplay * 0.03f;
					if (textWidth > widthMaxDisplay)
						textWidth = widthMaxDisplay;

					yA = yB - lineHeight * 0.2f;

					if (cell >= 16)
						yA = yB - (2 * singleLozengeHeight) + lineHeight * 0.3f;

					// X position Where ?
					int column = cell % 4;
					switch (column) {
					case 0:
						// in first column need need do nothing more
						break;

					case 1:
						// in 2nd column
						if (bid.rr2dBid.x + textWidth + 2 * rightBorder < widthMaxDisplay)
							xA = bid.rr2dBid.x - 4 * leftBorder;
						// otherwise leave on the right
						break;

					case 2:
						// in third column
						if ((widthMaxDisplay - bid.rr2dBid.x - 4 * leftBorder) > textWidth) {
							xA = bid.rr2dBid.x - 4 * leftBorder;
							break;
						}
						// fall through int the end column case

					case 3:
						// we are in the last column so move all the the right margin
						xA = widthMaxDisplay - (textWidth + rightBorder);
						break;
					}

					rr2d = new RoundRectangle2D.Float(xA + leftBorder, yA + lineHeight, textWidth + 2 * rightBorder, singleLozengeHeight, curve * 2, curve * 2);

					g2.setColor(Aaa.bidAlertBubbleCol);
					g2.fill(rr2d);
					g2.setColor(Color.black);

					layout.draw(g2, rr2d.x + leftBorder, rr2d.y + rr2d.height * 0.7f);
				}
				else {
					// we are a multi line alert but we only show two of them

					yB = lineHeight * ((1 + cell / 4) - 1) - (lineHeight * 0.02f);
					yA = yB - lineHeight * 0.1f;

					float textWidth = widthMaxDisplay;

					if (cell >= 12)
						yA = yB - (3 * singleLozengeHeight) + lineHeight * 0.8f;

					rr2d = new RoundRectangle2D.Float(xA + leftBorder, yA + lineHeight, textWidth + 2 * rightBorder, singleLozengeHeight * 1.7f, curve * 2,
							curve * 2);

					g2.setColor(Aaa.bidAlertBubbleCol);
					g2.fill(rr2d);
					g2.setColor(Color.black);

					layout.draw(g2, rr2d.x + leftBorder, rr2d.y + rr2d.height * 0.4f);

					layout = lineMeasurer.nextLayout(widthMaxDisplay);

					layout.draw(g2, rr2d.x + leftBorder, rr2d.y + rr2d.height * 0.85f);
				}

//				TextLayout layout = new TextLayout(astr.getIterator(), g2.getFontRenderContext());

			}
		}

	}

}
