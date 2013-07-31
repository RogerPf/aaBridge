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
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.AttributedString;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.model.Card;
import com.rogerpf.aabridge.model.Frag;
import com.rogerpf.aabridge.model.Hand;
import com.rogerpf.aabridge.model.Zzz;
import com.rpsd.bridgefonts.BridgeFonts;

/**
 */
class FragDisplayInfo {

	// Frag frag;

	TextLayout tl;
	int highlightIndex;
	boolean highlightSel;

	float layoutOriginX;
	float layoutOriginY;
	Rectangle2D bounds;

	FragDisplayInfo() { /* Constructor */
		clearHighlight();
		tl = null;
	}

	void clearHighlight() {
		highlightIndex = -1;
		highlightSel = false;
	}

}

/**
 */
public class HandDisplayPanel extends JPanel { // ============ HandDisplayPanel

	private static final long serialVersionUID = 1L;

	private static Card dragCard = null;

	int phyScreenPos;
	public Hand hand = null;

	Rectangle2D youDisplayRect;

	FragDisplayInfo[] fdiA = new FragDisplayInfo[4];

	void repaintTl(FragDisplayInfo fdi) {
		repaint(0, (int) fdi.bounds.getX(), (int) fdi.bounds.getY(), (int) fdi.bounds.getWidth(), (int) fdi.bounds.getHeight());
	}

	/**
	 */
	HandDisplayPanel(int phyScreenPos) { /* Constructor */
		this.phyScreenPos = phyScreenPos;
		addMouseMotionListener(new MouseMotionListener());
		addMouseListener(new MouseListener());
		// this.setDropTarget(dt)

		setHand();
	}

	/**
	 */
	public void dealDirectionChange() {
		setHand();
	}

	/**
	 */
	public void dealMajorChange() {
		setHand();
	}

	/**
	 */
	void setHand() {
		hand = App.deal.getHand(App.compassFromPhyScreenPos(phyScreenPos));
		for (int s : Zzz.cdhs) {
			fdiA[s] = new FragDisplayInfo();
		}
	}

	/**
	 */
	public void startDrag(Card card) {
		dragCard = card;

		int width = (int) ((float) getWidth() * 0.18f);
		BufferedImage dragImage = new BufferedImage(width, width, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2 = dragImage.createGraphics();
		Aaa.commonGraphicsSettings(g2);

		g2.setFont(BridgeFonts.faceAndSymbFont.deriveFont(width * 0.8f));
		g2.setColor(Aaa.cdhsColors[card.getSuit()]);

		String text = card.getRankSt();
		Aaa.drawCenteredString(g2, text, 0, 0, width, width);

		App.con.setDragImage(dragImage);
	}

	/**
	 */
	public Card stopDrag(Point p, Hand[] rtnHand) {

		Card card = dragCard;
		dragCard = null;
		if (card == null)
			return null;

		// we always stop the drag as soon as possible
		App.con.setDragImage(null);

		rtnHand[0] = null;
		if (p == null)
			return null;

		/* Because we roll our own drag and drop
		 * we have to find out ourselves which hand
		 * mouse was over when it was released. So which 
		 * HandDisplayPanel has this been dropped over?
		 */
		for (HandDisplayPanel hdp : App.gbp.hdps) {
			if (hdp == this)
				continue; // not interested if it is us, the 'drag from' hand
			// get the rectange of the hdp in our hdp coords.
			Rectangle r = SwingUtilities.convertRectangle(hdp.getParent(), hdp.getBounds(), this);

			if (r.contains(p)) {
				rtnHand[0] = hdp.hand;
				return card;
			}
		}

		return null; // the drop was outside of a hand
	}

	/**
	 */
	public void keyCommand(int cmd) {
		assert (App.deal.getNextHandToPlay() == hand);

		Boolean playing = App.deal.isPlaying();
		if (!playing || playing && !App.isSeatVisible(hand.compass))
			return;

		Card card = null;
		boolean repaintNeeded = false;
		int rankSuggested = App.gbp.c1_1__tfdp.getSuggestedRank(phyScreenPos);
		int suitSuggested = App.gbp.c1_1__tfdp.getSuggestedSuit(phyScreenPos);

		if ((cmd & Aaa.CMD_SUIT) != 0) { // *** The user has pressed a Suit Key
			int suit = (cmd & 0xff);
			if (hand.isSuitSelectable(suit)) {
				repaintNeeded = true;
				// set/clear these two values now, but it will often have been
				// unneeded
				App.gbp.c1_1__tfdp.setSuggestedRank(phyScreenPos, -1);
				App.gbp.c1_1__tfdp.setSuggestedSuit(phyScreenPos, suit);

				card = hand.getCardIfSingletonInSuit(suit);
				if (card == null) { // must have at least two as suit isselectable
					if (rankSuggested > 0) {
						card = hand.getCardIfMatching(suit, rankSuggested);
					}
				}
			}

		}
		else if ((cmd & Aaa.CMD_FACE) != 0) { // *** The user has pressed a Face
												// Key
			int rank = (cmd & 0xff);
			int count = hand.cardSelectableCount(rank);
			if (count > 0) {
				// set/clear these two values now, but it will often have been unneeded
				App.gbp.c1_1__tfdp.setSuggestedRank(phyScreenPos, rank);
				App.gbp.c1_1__tfdp.setSuggestedSuit(phyScreenPos, -1);

				if (count == 1) {
					card = hand.cardSelectableGetOnlyCard(rank);
					if (card == null) {
						if (suitSuggested > -1) {
							card = hand.getCardIfMatching(suitSuggested, rank);
						}
					}
				}
				else { // (count > 1)
					App.gbp.c1_1__tfdp.setSuggestedRank(phyScreenPos, rank);
					repaintNeeded = true;
				}
			}
		}
		else {
			return; // shrug - not our key
		}

		if (card != null) {
			App.con.tableTheCard(hand, card);
		}
		else if (repaintNeeded) {
			App.frame.repaint();
		}
	}

	/**
	 */
	private class MouseMotionListener extends MouseMotionAdapter {

		/**
		 */
		public void mouseMoved(MouseEvent e) {
			// System.out.println("Mousemoved");

			if (App.youAutoplayAlways && App.isMode(Aaa.NORMAL) && App.deal.isPlaying())
				return;

			Boolean editing = App.isMode(Aaa.EDIT_HANDS);
			Boolean playing = App.isMode(Aaa.EDIT_PLAY) || App.isMode(Aaa.NORMAL) && App.deal.isPlaying();

			if (!(editing || playing && App.isSeatVisible(hand.compass) && !App.isAutoPlay(hand.compass)))
				return;

			Point ep = e.getPoint();

			Frag[] frags = getAppropriateFrags();

			for (Frag frag : frags) {
				FragDisplayInfo fdi = fdiA[frag.suit];
				if (fdi.tl == null)
					continue; // no cards displayed in this frag
								// at the moment

				if ((fdi.highlightIndex > -1) && fdi.highlightSel) {
					return; // preserve clicked selection
				}
			}

			for (Frag frag : frags) {
				FragDisplayInfo fdi = fdiA[frag.suit];
				if (fdi.tl == null)
					continue; // no cards displayed in this frag at the moment

				if (fdi.bounds.contains(ep) && (editing || playing && hand.isCardSelectable(frag))) {
					float clickX = (float) (ep.getX() - fdi.layoutOriginX);
					// Get the character position of the mouse click.
					TextHitInfo currentHit = fdi.tl.hitTestChar(clickX, 0);
					int charIndex = currentHit.getCharIndex() / 3;
					if (fdi.highlightIndex != charIndex) {
						fdi.highlightIndex = charIndex;
						fdi.highlightSel = false; // we are just 'hover'
						repaintTl(fdi);
					}
				}
				else {
					if (fdi.highlightIndex > -1) {
						fdi.clearHighlight();
						repaintTl(fdi);
					}
				}
			}
		}
	}

	/**
	 */
	private class MouseListener extends MouseAdapter {

		/**
		 */
		public void mouseReleased(MouseEvent e) {
			// System.out.println("Mouse Released on HandDisplayPanel");

			Hand rtnHand[] = new Hand[1];
			Card card = stopDrag(e.getPoint(), rtnHand);

			if ((card != null) && (rtnHand[0] != null) && App.isMode(Aaa.EDIT_HANDS)) {
				App.deal.moveCardToHandDragTime(card, rtnHand[0]);
				App.gbp.dealMajorChange();
				App.frame.repaint();
				return;
			}

			if (App.isMode(Aaa.EDIT_PLAY) && youDisplayRect.contains(e.getPoint())) {
				App.deal.youSeatHint = hand.compass;
				App.gbp.dealMajorChange();
				App.frame.repaint();
				return;
			}

			App.gbp.c1_1__tfdp.ClearShowCompletedTrick();

			Boolean playing = App.isMode(Aaa.EDIT_PLAY) || App.isMode(Aaa.NORMAL) && App.deal.isPlaying();
			if (!(playing && App.isSeatVisible(hand.compass) && !App.isAutoPlay(hand.compass))) {
				return;
			}

			Frag[] frags = getAppropriateFrags();

			// Action existing selected card (if any)
			for (Frag frag : frags) {
				FragDisplayInfo fdi = fdiA[frag.suit];
				if ((fdi.highlightIndex > -1) && fdi.highlightSel) {
					card = frag.get(fdi.highlightIndex);
					fdi.clearHighlight();
					repaintTl(fdi);
					App.con.tableTheCard(hand, card);
					return;
				}
			}

			for (Frag frag : frags) {
				FragDisplayInfo fdi = fdiA[frag.suit];
				if (fdi.tl == null)
					continue; // no cards displayed in this suit
								// at the moment

				if (!fdi.bounds.contains(e.getPoint()))
					continue;

				if (!hand.isCardSelectable(frag))
					return;

				float clickX = (float) (e.getX() - fdi.layoutOriginX);

				// Get the character position of the mouse click.
				TextHitInfo currentHit = fdi.tl.hitTestChar(clickX, 0);
				int charIndex = currentHit.getCharIndex() / 3;
				fdi.highlightIndex = charIndex;
				fdi.highlightSel = false;
				repaintTl(fdi);
				return;
			}

		}

		/**
		 */
		public void mousePressed(MouseEvent e) {
			// System.out.println("Mouse Pressed on HandDisplayPanel");

			Boolean editing = App.isMode(Aaa.EDIT_HANDS);
			Boolean playing = App.isMode(Aaa.EDIT_PLAY) || App.isMode(Aaa.NORMAL) && App.deal.isPlaying();

			if (!(editing || playing && App.isSeatVisible(hand.compass) && !App.isAutoPlay(hand.compass)))
				return;

			Frag[] frags = getAppropriateFrags();

			for (Frag frag : frags) {
				FragDisplayInfo fdi = fdiA[frag.suit];
				if (fdi.tl == null)
					continue; // no cards displayed in this suit
								// at the moment

				if (!fdi.bounds.contains(e.getPoint()))
					continue;

				float clickX = (float) (e.getX() - fdi.layoutOriginX);
				// Get the character position of the mouse click.

				TextHitInfo currentHit = fdi.tl.hitTestChar(clickX, 0);

				int charIndex = currentHit.getCharIndex() / 3;

				if (playing && hand.isCardSelectable(frag)) {
					fdi.highlightIndex = charIndex;
					fdi.highlightSel = true;
					repaintTl(fdi);
					return;
				}

				if (App.isMode(Aaa.EDIT_HANDS)) {
					// It's Drag (of Drag and Drop time)
					fdi.highlightIndex = charIndex;
					fdi.highlightSel = true;
					repaintTl(fdi);

					startDrag(frag.get(fdi.highlightIndex));
					return;
				}
				return;
			}
		}

		/**
		 */
		public void mouseExited(MouseEvent e) {
			// System.out.println("Mouse Exited HandDisplayPanel");

			Frag[] frags = getAppropriateFrags();

			for (Frag frag : frags) {
				FragDisplayInfo fdi = fdiA[frag.suit];
				if (fdi.highlightIndex == -1)
					continue;
				if (fdi.tl == null)
					continue; // no cards displayed in this suit
								// at the moment
				fdi.highlightIndex = -1;
				repaintTl(fdi);
			}
		}

	}

	/**
	 */
	public String addPadding(String cards) {
		/*
		 * The bridge symb and face font has some characters set to blanks with known width
		 * (picas) these are => 10 + => 100 , => 200 - => 300 . => 500 / => 750
		 * space => 1000
		 */

		int len = cards.length();
		char before = '*';
		char after = '*';
		if (len <= 7) {
			before = ',';
			after = '-';
		}
		else if (len == 8) {
			before = ',';
			after = ',';
		}
		else if (len == 9) {
			before = '*';
			after = ',';
		}

		final StringBuilder sb = new StringBuilder();

		for (int i = 0; i < len; i++) {
			char c = cards.charAt(i);
			char bef = (c == 'A' || c == 'K' || c == 'Q') ? '*' : before;
			if (i == 0)
				bef = '<';
			if (i == len - 1)
				after = '>';
			sb.append(bef);
			sb.append(c);
			sb.append(after);
		}
		return sb.toString();
	}

	/**
	 */
	private Frag[] getAppropriateFrags() {

		if (App.isMode(Aaa.REVIEW_PLAY)) {
			return hand.makeFragsAsOf(App.reviewTrick, App.reviewCard);
		}
		else if ((App.isMode(Aaa.NORMAL) && App.deal.isPlaying()) || App.isMode(Aaa.EDIT_PLAY)) {
			return hand.frags;
		}
		else {
			return hand.fOrgs;
		}
	}

	/**
	*/
	public String generateTestCards(Frag frag) {
		if (hand.compass == Zzz.North) {
			if (frag.suit == Zzz.Spades)
				return new String("AKQJT98765432");
			if (frag.suit == Zzz.Hearts)
				return new String("AKQJT9876543");
			if (frag.suit == Zzz.Diamonds)
				return new String("AKQJT987654");
			if (frag.suit == Zzz.Clubs)
				return new String("AKQJT98765");
		}
		else if ((hand.compass == Zzz.East)) {
			if (frag.suit == Zzz.Spades)
				return new String("AKQJT9876");
			if (frag.suit == Zzz.Hearts)
				return new String("JT9876543");
			if (frag.suit == Zzz.Diamonds)
				return new String("AKQJT987");
			if (frag.suit == Zzz.Clubs)
				return new String("J9876543");
		}
		else if ((hand.compass == Zzz.South)) {
			if (frag.suit == Zzz.Spades)
				return new String("AKQJT98");
			if (frag.suit == Zzz.Hearts)
				return new String("J987654");
			if (frag.suit == Zzz.Diamonds)
				return new String("AKQJT9");
			if (frag.suit == Zzz.Clubs)
				return new String("JT9876");
		}
		else if ((hand.compass == Zzz.West)) {
			if (frag.suit == Zzz.Spades)
				return new String("QT98");
			if (frag.suit == Zzz.Hearts)
				return new String("AQ7");
			if (frag.suit == Zzz.Diamonds)
				return new String("");
			if (frag.suit == Zzz.Clubs)
				return new String("KJ7432");
		}
		return new String("");
	}

	/**
	 */
	public void paintComponent(Graphics g) {

		super.paintComponent(g);
		setBackground(Aaa.baizeGreen);

		Graphics2D g2 = (Graphics2D) g;

		FontRenderContext frc = g2.getFontRenderContext();
		Aaa.commonGraphicsSettings(g2);

		Dimension panelSize = getSize();

		// fill the dealLozenge ----------------------------------------------

		float lineThickness = ((float) panelSize.height) / 130f;
		float boarderThickness = ((float) panelSize.height) / 30f;

		BasicStroke ourOutline = new BasicStroke(lineThickness);

		float dealLozengeWidth = ((float) panelSize.width) - boarderThickness * 2.0f;
		float dealLozengeHeight = ((float) panelSize.height) - boarderThickness * 2.0f;

		Rectangle2D rd = new Rectangle2D.Float(boarderThickness, boarderThickness, dealLozengeWidth, dealLozengeHeight);

		// Fill the Hands background
		// --------------------------------------------------
		g2.setColor(hand.isDummy() ? Aaa.handBkColorDummy : Aaa.handBkColorStd);
		g2.fill(rd);

		g2.setStroke(ourOutline);
		g2.setColor(Aaa.handAreaOffWhite);
		g2.draw(rd);

		// fill the you Lozenge
		// ---------------------------------------------------

		float youLozengeHeight = (dealLozengeHeight) * 0.18f;

		/* kept */youDisplayRect = new Rectangle2D.Float(boarderThickness, boarderThickness, dealLozengeWidth, youLozengeHeight);

		boolean youSeatUs = (App.deal.getTheYouSeat() == hand.compass);

		Color bannerColor = (youSeatUs) ? Aaa.youSeatBannerBk : Aaa.handBannerBk;
		Color pointsColor = (youSeatUs) ? Aaa.handBannerText : Aaa.handBannerText;

		g2.setColor(bannerColor);
		g2.fill(youDisplayRect);

		g2.setStroke(ourOutline);
		g2.setColor(Color.white);
		g2.draw(youDisplayRect);

		float xy = boarderThickness;
		float nlh = youLozengeHeight;

		// fill the NESW indicator
		// -----------------------------------------------------

		Rectangle2D rtNESW = new Rectangle2D.Float(xy, xy, nlh, nlh);

		g2.setColor(Aaa.handNeswBkColor);
		g2.fill(rtNESW);

		g2.setStroke(ourOutline);
		g2.setColor(Color.white);
		g2.draw(rtNESW);

		float bridgeLightFontSize = (float) nlh * 0.73f;
		Font bridgeLightFont = BridgeFonts.bridgeLightFont.deriveFont(bridgeLightFontSize);
		g2.setFont(bridgeLightFont);

		String letter = hand.getCompassSt();

		g2.setColor(Color.WHITE);
		Aaa.drawCenteredString(g2, letter, xy, xy, nlh, nlh);

		g2.setColor(pointsColor); // Also used by the 'You' field

		// show the points
		// ------------------------------------------------------------------
		boolean visSeat = App.isSeatVisible(hand.compass);
		String hiddenText = "hidden";
		if (App.deal.isDoneHand()) {
			hiddenText = "Welcome";
			visSeat = false;
		}

		Font pointsFont = BridgeFonts.bridgeBoldFont.deriveFont(nlh * 1.0f);
		if (App.showPoints && visSeat) { // && isModeAnyEdit()
			g2.setFont(pointsFont);
			Aaa.drawCenteredString(g2, Integer.toString(hand.countPoints()), dealLozengeWidth * 0.88f, xy, nlh, nlh);
		}

		boolean showLTC = App.showLTC && visSeat && App.isModeAnyEdit() == false;
		if (showLTC) { //
			g2.setFont(pointsFont);
			int v = hand.countLosingTricks_x2();
			float p = 0.57f;
			Aaa.drawCenteredString(g2, Integer.toString(v / 2), dealLozengeWidth * p, xy, nlh, nlh);
			if (v % 2 == 1) {
				p += 0.09;
				Aaa.drawCenteredString(g2, "" + (char) 0xbd, dealLozengeWidth * p, xy, nlh, nlh);
			}
		}

		// The "You" text
		// ------------------------------------------------------------------
		if (youSeatUs) {
			float youTextFontSize = bridgeLightFontSize * 1.2f;
			Font youTextFont = BridgeFonts.bridgeLightFont.deriveFont(youTextFontSize);
			g2.setFont(youTextFont);
			String s = (showLTC) ? "   " : "       ";
			g2.drawString(s + "You", (int) (xy + nlh * 1.5), (int) (xy + nlh - youTextFontSize * 0.16f));
		}
		else if (App.isMode(Aaa.EDIT_PLAY)) {
			if (App.deal.isDeclarerValid() && App.deal.contractCompass == hand.partner().compass) {
				// nothing we don't want to show this on the dummy hand
			}
			else {
				float youTextFontSize = bridgeLightFontSize * 1.0f;
				Font youTextFont = BridgeFonts.bridgeLightFont.deriveFont(youTextFontSize);
				g2.setFont(youTextFont);
				g2.drawString("Click to be You", (int) (xy + nlh * 1.5), (int) (xy + nlh - youTextFontSize * 0.16f));
			}
		}

		// The four Suits
		// ------------------------------------------------------------------

		// the four hands

		float suitAreaHeight = (float) (dealLozengeHeight - youLozengeHeight);

		float suitLineHeight = (float) suitAreaHeight / 4.0f;

		float suitLineStartY = (float) (boarderThickness + lineThickness + youLozengeHeight);

		float suitLineStartX = (float) (boarderThickness + lineThickness);

		float handFontSize = (float) (suitLineHeight) * 0.97f;

		if (!visSeat) {
			g2.setFont(BridgeFonts.bridgeLightFont.deriveFont(handFontSize));
			g2.setColor(Aaa.veryWeedyBlack);
			g2.drawString(hiddenText, dealLozengeWidth * 0.3f, dealLozengeHeight * 0.650f);
		}

		Font suitSymbolsFont = BridgeFonts.faceAndSymbFont.deriveFont(handFontSize * 0.65f);
		Font cardFaceFont = BridgeFonts.faceAndSymbFont.deriveFont(handFontSize);

		Frag[] frags = getAppropriateFrags();

		for (Frag frag : frags) {

			FragDisplayInfo fdi = fdiA[frag.suit];

			// Spades on the top row down to clubs last
			int row = Zzz.Spades - frag.suit;
			fdi.tl = null;

			String rawCards = frag.toScrnStr();
			if (App.fillHandDisplay) {
				rawCards = generateTestCards(frag); // <<<<<<<<<<<<<<<<<<<< TEST CARD GENERATOR >>>>>>>>
			}
			String cards = addPadding(rawCards);

			boolean showSuitSymbol = true;
			float lhs = suitLineStartX + suitLineHeight / 8f;
			float normStart = lhs + suitLineHeight * 1.1f;
			float x = normStart;
			float y = suitLineStartY * 0.87f + suitLineHeight * (row + 1);

			if (cards.length() / 3 >= 11) {
				showSuitSymbol = false;
				x = lhs;
			}

			// Suit Symbol
			if (showSuitSymbol) {
				g2.setColor(Aaa.cdhsWeakColors[frag.suit]);
				g2.setFont(suitSymbolsFont);
				g2.drawString(frag.getSuitSt(), lhs, y - suitLineStartY * 0.09f);
				g2.setColor(Color.black);
			}

			// Cards of the frag
			if (cards.isEmpty() || !visSeat)
				continue;

			AttributedString astr = new AttributedString(cards);
			astr.addAttribute(TextAttribute.FONT, cardFaceFont);

			if ((fdi.highlightIndex > -1) && (cards.length() / 3 > fdi.highlightIndex)) {
				Color col = (fdi.highlightSel) ? Aaa.cardClickedOn : Aaa.cardHover;
				astr.addAttribute(TextAttribute.BACKGROUND, col, fdi.highlightIndex * 3, fdi.highlightIndex * 3 + 3);
			}

			fdi.tl = new TextLayout(astr.getIterator(), frc);
			fdi.layoutOriginX = x;
			fdi.layoutOriginY = y;
			g2.setColor(Aaa.cdhsColors[frag.suit]);
			fdi.tl.draw(g2, x, y);

			// Compute the mouse click location relative to
			// textLayout's origin and cache it for mouse hit/movement testing.
			fdi.bounds = fdi.tl.getBounds();
			// adjust to actual position in the panel
			fdi.bounds.setRect(fdi.bounds.getX() + fdi.layoutOriginX, fdi.bounds.getY() + fdi.layoutOriginY, fdi.bounds.getWidth(), fdi.bounds.getHeight());

			// // Debugging aid
			// g2.draw(fdi.bounds);
		}
	}

}
