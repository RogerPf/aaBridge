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
import java.awt.Component;
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
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.text.AttributedString;

import javax.swing.SwingUtilities;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.Aaf;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.model.Card;
import com.rogerpf.aabridge.model.Cc;
import com.rogerpf.aabridge.model.Deal;
import com.rogerpf.aabridge.model.Dir;
import com.rogerpf.aabridge.model.Frag;
import com.rogerpf.aabridge.model.Hand;
import com.rogerpf.aabridge.model.Rank;
import com.rogerpf.aabridge.model.Suit;
import com.rpsd.bridgefonts.BridgeFonts;

/**
 */
class FragDisplayInfo {
	// ---------------------------------- CLASS -------------------------------------
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
public class HandDisplayPanel extends ClickPanel { // ============ HandDisplayPanel
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	Deal deal = null;
	boolean floatingHand = false;
	boolean questionHand = false;
	private static Card dragCard = null;

	Dir phyScreenPos;
	public Hand hand = null;

	Rectangle2D youDisplayRect;

	FragDisplayInfo[] fdiA = new FragDisplayInfo[4];

	void repaintTl(FragDisplayInfo fdi) {
		// =============================================================
		// repaint(0, (int) fdi.bounds.getX(), (int) fdi.bounds.getY(), (int) fdi.bounds.getWidth(), (int) fdi.bounds.getHeight());
		App.frame.repaint();
	}

	/**
	 */
	public HandDisplayPanel(Dir phyScreenPos, Deal deal, boolean questionHand) { /* Constructor */
		// =============================================================
		setOpaque(false);
		this.deal = deal;
		this.phyScreenPos = phyScreenPos;
		this.floatingHand = true;
		this.questionHand = questionHand;
		addMouseMotionListener(new MouseMotionListener());
		addMouseListener(new MouseListener());
		setHand();
	}

	/**
	 */
	public HandDisplayPanel(Dir phyScreenPos) { /* Constructor */
		// =============================================================
		setOpaque(false);
		deal = App.deal;
		this.phyScreenPos = phyScreenPos;
		addMouseMotionListener(new MouseMotionListener());
		addMouseListener(new MouseListener());
		setHand();
	}

	/**
	 */
	public void dealDirectionChange() {
		// =============================================================
		setHand();
	}

	/**
	 */
	public void dealMajorChange(Deal dealNew) {
		// =============================================================
		deal = dealNew;
		setHand();
	}

	/**
	 */
	void setHand() {
		// =============================================================
		if (floatingHand)
			hand = deal.getHand(phyScreenPos); // floating deals this is the actual postion
		else
			hand = deal.getHand(App.cpeFromPhyScreenPos(phyScreenPos));

		for (Suit su : Suit.cdhs) {
			fdiA[su.v] = new FragDisplayInfo();
		}
	}

	/**
	 */
	public void startDrag(Card card) {
		// =============================================================
		dragCard = card;

		int width = (int) ((float) getWidth() * 0.18f);
		BufferedImage dragImage = new BufferedImage(width, width, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2 = dragImage.createGraphics();
		Aaa.commonGraphicsSettings(g2);

		g2.setColor(card.suit.colorCd(Cc.Ce.Strong));

		char c = Rank.rankToLanguage(card.rank.toChar());
		if (Aaa.isLatinFaceCard(c))
			g2.setFont(BridgeFonts.faceAndSymbolFont.deriveFont(width * 0.8f));
		else
			g2.setFont(BridgeFonts.internatBoldFont.deriveFont(width * 0.8f));

		Aaa.drawCenteredString(g2, "" + c, 0, 0, width, width);

		App.con.setDragImage(dragImage);
	}

	/**
	 */
	public Card stopDrag(Point p, Hand[] rtnHand) {
		// =============================================================
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
		// =============================================================
		assert (deal.getNextHandToPlay() == hand);

		Boolean playing = deal.isPlaying();
		if (!playing || playing && !App.isSeatVisible(hand.compass))
			return;

		Card card = null;
		boolean repaintNeeded = false;
		Rank rankSuggested = App.gbp.c1_1__tfdp.getSuggestedRank(phyScreenPos);
		Suit suitSuggested = App.gbp.c1_1__tfdp.getSuggestedSuit(phyScreenPos);

		if ((cmd & Aaa.CMD_SUIT) != 0) { // *** The user has pressed a Suit Key
			Suit suit = Suit.suitFromInt(cmd & 0xff);
			if (hand.isSuitSelectable(suit)) {
				repaintNeeded = true;
				// set/clear these two values now, but it will often have been
				// unneeded
				App.gbp.c1_1__tfdp.setSuggestedRank(phyScreenPos, Rank.Invalid);
				App.gbp.c1_1__tfdp.setSuggestedSuit(phyScreenPos, suit);

				card = hand.getCardIfSingletonInSuit(suit);
				if (card == null) { // must have at least two as suit isselectable
					if (rankSuggested != Rank.Invalid) {
						card = hand.getCardIfMatching(suit, rankSuggested);
					}
				}
			}

		}
		else if ((cmd & Aaa.CMD_FACE) != 0) { // *** The user has pressed a Face
												// Key
			Rank rank = Rank.rankFromInt(cmd & 0xff);
			int count = hand.cardSelectableCount(rank);
			if (count > 0) {
				// set/clear these two values now, but it will often have been unneeded
				App.gbp.c1_1__tfdp.setSuggestedRank(phyScreenPos, rank);
				App.gbp.c1_1__tfdp.setSuggestedSuit(phyScreenPos, Suit.Invalid);

				if (count == 1) {
					card = hand.cardSelectableGetOnlyCard(rank);
					if (card == null) {
						if (suitSuggested != Suit.Invalid) {
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
			// =============================================================
			// System.out.println("Mousemoved");

			if (App.youAutoplayAlways && App.isMode(Aaa.NORMAL_ACTIVE) && deal.isPlaying())
				return;

			Boolean editing = App.isMode(Aaa.EDIT_HANDS);
			Boolean playing = deal.isPlaying() && (App.isMode(Aaa.EDIT_PLAY) && (App.deal.isFinished() == false) || App.isMode(Aaa.NORMAL_ACTIVE));

			if (App.isVmode_Tutorial()) {
				if (!questionHand)
					return;
			}
			else if (!(editing || playing && App.isSeatVisible(hand.compass) && !App.isAutoPlay(hand.compass)))
				return;

			Point ep = e.getPoint();

			Frag[] frags = getAppropriateFrags();

			for (Frag frag : frags) {
				FragDisplayInfo fdi = fdiA[frag.suit.v];
				if (fdi.tl == null)
					continue; // no cards displayed in this frag
								// at the moment

				if ((fdi.highlightIndex > -1) && fdi.highlightSel) {
					return; // preserve clicked selection
				}
			}

			for (Frag frag : frags) {
				FragDisplayInfo fdi = fdiA[frag.suit.v];
				if (fdi.tl == null)
					continue; // no cards displayed in this frag at the moment

				if (fdi.bounds.contains(ep) && (floatingHand || editing || playing && hand.isCardSelectable(frag))) {
					float clickX = (float) (ep.getX() - fdi.layoutOriginX);
					// Get the character position of the mouse click.
					TextHitInfo currentHit = fdi.tl.hitTestChar(clickX, 0);
					int charIndex = currentHit.getCharIndex() / 3;
					if (fdi.highlightIndex != charIndex) {
						fdi.highlightIndex = charIndex;
						fdi.highlightSel = false; // we are just 'hover'
						// repaintTl(fdi);
						App.frame.repaint();
					}
				}
				else {
					if (fdi.highlightIndex > -1) {
						fdi.clearHighlight();
						// repaintTl(fdi);
						App.frame.repaint();
					}
				}
			}
		}
	}

	/**
	 */
	private class MouseListener extends MouseAdapter {
		// ---------------------------------- CLASS -------------------------------------

		/**
		 */
		public void mouseReleased(MouseEvent e) {
			// =============================================================
			// System.out.println("Mouse Released on HandDisplayPanel");

			if (/*App.isMode(Aaa.EDIT_PLAY) && */!floatingHand && youDisplayRect.contains(e.getPoint())) {
				deal.youSeatHint = hand.compass;
				App.youSeatHint = hand.compass;
//				App.nameInSouthZone = hand.playerName.trim();
				App.dealMajorChange();
				App.frame.repaint();
				return;
			}

			if (App.isVmode_Tutorial() && !questionHand)
				return;

			boolean tutFloating = App.isVmode_Tutorial() && questionHand;

			Hand rtnHand[] = new Hand[1];
			Card card = stopDrag(e.getPoint(), rtnHand);

			if (!tutFloating && (card != null) && (rtnHand[0] != null) && App.isMode(Aaa.EDIT_HANDS)) {
				deal.moveCardToHandDragTime(card, rtnHand[0]);
				App.dealMajorChange();
				App.frame.repaint();
				return;
			}

			App.gbp.c1_1__tfdp.clearShowCompletedTrick();

			Boolean playing = App.isMode(Aaa.EDIT_PLAY) || App.isMode(Aaa.NORMAL_ACTIVE) && deal.isPlaying();
			if (!tutFloating && !(playing && App.isSeatVisible(hand.compass) && !App.isAutoPlay(hand.compass))) {
				return;
			}

			Frag[] frags = getAppropriateFrags();

			// Action existing selected card (if any)
			for (Frag frag : frags) {
				FragDisplayInfo fdi = fdiA[frag.suit.v];
				if ((fdi.highlightIndex > -1) && fdi.highlightSel) {
					card = frag.get(fdi.highlightIndex);
					fdi.clearHighlight();
					repaintTl(fdi);
					App.con.cardSelected(hand, card);
					return;
				}
			}

			for (Frag frag : frags) {
				FragDisplayInfo fdi = fdiA[frag.suit.v];
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
			// =============================================================
			// System.out.println("Mouse Pressed on HandDisplayPanel");

			Boolean editing = App.isMode(Aaa.EDIT_HANDS);
			Boolean playing = (App.isMode(Aaa.EDIT_PLAY) && (App.deal.isFinished() == false)) || App.isMode(Aaa.NORMAL_ACTIVE) && deal.isPlaying();

			if (App.isVmode_Tutorial()) {
				if (!questionHand)
					return;
			}
			else if (!(editing || playing && App.isSeatVisible(hand.compass) && !App.isAutoPlay(hand.compass)))
				return;

			Frag[] frags = getAppropriateFrags();

			for (Frag frag : frags) {
				FragDisplayInfo fdi = fdiA[frag.suit.v];
				if (fdi.tl == null)
					continue; // no cards displayed in this suit
								// at the moment

				if (!fdi.bounds.contains(e.getPoint()))
					continue;

				float clickX = (float) (e.getX() - fdi.layoutOriginX);
				// Get the character position of the mouse click.

				TextHitInfo currentHit = fdi.tl.hitTestChar(clickX, 0);

				int charIndex = currentHit.getCharIndex() / 3;

				if (floatingHand || playing && hand.isCardSelectable(frag)) {
					fdi.highlightIndex = charIndex;
					fdi.highlightSel = true;
					// repaintTl(fdi);
					App.frame.repaint();
					return;
				}

				if (App.isMode(Aaa.EDIT_HANDS)) {
					// It's Drag (of Drag and Drop time)
					fdi.highlightIndex = charIndex;
					fdi.highlightSel = true;
					// repaintTl(fdi);
					App.frame.repaint();

					startDrag(frag.get(fdi.highlightIndex));
					return;
				}
				return;
			}
		}

		/**
		 */
		public void mouseExited(MouseEvent e) {
			// =============================================================
			// System.out.println("Mouse Exited HandDisplayPanel");

			if (App.isVmode_Tutorial())
				return;

			Frag[] frags = getAppropriateFrags();

			for (Frag frag : frags) {
				FragDisplayInfo fdi = fdiA[frag.suit.v];
				if (fdi.highlightIndex == -1)
					continue;
				if (fdi.tl == null)
					continue; // no cards displayed in this suit
								// at the moment
				fdi.highlightIndex = -1;
				// repaintTl(fdi);
				App.frame.repaint();
			}
		}

	}

	/**
	 */
	public String addTransformationsAndPadding(String cards, char showXes, int[] leftpos) {
		// =============================================================
		/*
		 * The bridge symb and face font has some characters set to blanks with known width
		 * (picas) these are => 10 + => 100 , => 200 - => 300 . => 500 / => 750
		 * < and >  are 300 but with tiny dot (invisible) in the left and right corners respectivly
		 * AKQ & (ten) are  1479  the rest inc J are 1139
		 * space => 1000
		 */

		// @formatter:off

		int len = cards.length();
		char before = '*';
		char after  = '*';
		int beforeI = 10;
		int afterI  = 10;
		if (len <= 7) {
			before = ',';
			after  = '-';
			beforeI = 200;
			afterI  = 300;
		}
		else if (len == 8) {
			before = ',';
			after  = ',';
			beforeI = 200;
			afterI  = 200;
		}
		else if (len == 9) {
			after  = ',';
			afterI  = 200;
		}

		int cum = 0;
		
		final StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < len; i++) {
			
			char c = cards.charAt(i);

			/*
			 *  Is this card to be conveted to an x ?
			 */
			if (showXes != '-') {			
				if (showXes == 'x') {
					c = 'x';
				}
				else if (showXes == 'v') {
					c = ' ';
				}
				else {	
					int xv  = 0;
					if ('0' <= showXes  &&  showXes <= '9') 
						 xv = showXes - '0';
					else {
						switch(showXes) {
							default:  xv = 14; break; // Ace x etc
							case 'k': xv = 13; break;
							case 'q': xv = 12; break;
							case 
							'j': xv = 11; break;
							case 't': xv = 10; break;
						}
					}	
					int cv = 0;
					if (('2' <= c)  &&  (c <= '9')) 
						 cv = c - '0';
					else {
						switch(c) {
							default:  cv = 14; break; // Ace x etc
							case 'K': cv = 13; break;
							case 'Q': cv = 12; break;
							case 'J': cv = 11; break;
							case 'T': cv = 10; break;
						}
					}				
					if (xv >= cv)
						c = 'x';
				}
			}		
		 
			/* 
			 * Do the honour language substitutions			
			 */
			c = Rank.rankToLanguage(c);
						
			/*
			 *  Put extra 'space' around each characters
			 */
			char bef = before;
			int befI = beforeI;
			int core = 1139;
			
			if ( /* (c > ???) ||*/ c == 'A' || c == 'K' || c == 'Q' || c == 'R'|| c == 'D' || c == 'B') {
				core = 1479;
				bef = '*';
				befI = 500;
			}
			else if (c == 't') { // the T character is converted to lowercase t  which is shown as a '10' by the font
				core = 1479;
			}
			
			if (i == 0) {
				bef = '<';
				befI = 500;
			}
			if (i == len - 1) {
				after = '>';
				afterI = 500;
			}
			sb.append(bef);
			
			int picas = befI + core + afterI;
			
			cum += picas;
			
			leftpos[i] = cum;
			
			sb.append(c);
			
			sb.append(after);

		}
	
		return sb.toString();
		// @formatter:on
	}

	/**
	 */
	private Frag[] getAppropriateFrags() {
		// =============================================================
		if (App.isMode(Aaa.REVIEW_PLAY)) {
			int reviewCardTot = App.reviewTrick * 4 + App.reviewCard;
			int cardsPlayed = deal.countCardsPlayed();
			if (deal.endedWithClaim == false || reviewCardTot <= cardsPlayed - 1) {
				return hand.makeFragsAsOf(App.reviewTrick, App.reviewCard);
			}
			else
				return hand.frags;
		}
		// @formatter:off
		else if (   (     App.isMode(Aaa.NORMAL_ACTIVE) 
				      && (    deal.eb_blocker
				    	  ||  deal.isPlaying() 
				          || (App.visualMode == App.Vm_DealAndTutorial)
				         )
				     )
				  ||  App.isMode(Aaa.EDIT_PLAY)
			    ) {
		// @formatter:on
			return hand.frags;
		}
		else {
			return hand.fOrgs;
		}
	}

	/**
	*/
	public String generateTestCards(Frag frag) {
		// =============================================================
		if (hand.compass == Dir.North) {
			if (frag.suit == Suit.Spades)
				return new String("AKQJT98765432");
			if (frag.suit == Suit.Hearts)
				return new String("AKQJT9876543");
			if (frag.suit == Suit.Diamonds)
				return new String("AKQJT987654");
			if (frag.suit == Suit.Clubs)
				return new String("AKQJT98765");
		}
		else if ((hand.compass == Dir.East)) {
			if (frag.suit == Suit.Spades)
				return new String("AKQJT9876");
			if (frag.suit == Suit.Hearts)
				return new String("JT9876543");
			if (frag.suit == Suit.Diamonds)
				return new String("AKQJT987");
			if (frag.suit == Suit.Clubs)
				return new String("J9876543");
		}
		else if ((hand.compass == Dir.South)) {
			if (frag.suit == Suit.Spades)
				return new String("AKQJT98");
			if (frag.suit == Suit.Hearts)
				return new String("J987654");
			if (frag.suit == Suit.Diamonds)
				return new String("AKQJT9");
			if (frag.suit == Suit.Clubs)
				return new String("JT9876");
		}
		else if ((hand.compass == Dir.West)) {
			if (frag.suit == Suit.Spades)
				return new String("QT98");
			if (frag.suit == Suit.Hearts)
				return new String("AQ7");
			if (frag.suit == Suit.Diamonds)
				return new String("");
			if (frag.suit == Suit.Clubs)
				return new String("KJ7432");
		}
		return new String("");
	}

	/**
	 */
	public boolean doFragsMatchCardsAndCopyScore(Frag[] fragsA, Frag[] fragsD) {
		// =============================================================

		for (int i = 0; i < 4; i++) {

			Frag fragA = fragsA[i];
			Frag fragD = fragsD[i];
			if (fragA.size() != fragD.size())
				return false;

			for (int j = 0; j < fragA.size(); j++) {
				if (fragA.get(j).matches(fragD.get(j)) == false) {
					return false;
				}
			}

			for (int j = 0; j < fragA.size(); j++) {
				fragA.get(j).setDdsScore(fragD.get(j).getDdsScore());
				fragA.get(j).setDdsNextCard(fragD.get(j).getDdsNextCard());
			}
		}
		return true;
	}

	/**
	 */
	public void paintComponent(Graphics g) {
		// =============================================================
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;

		FontRenderContext frc = g2.getFontRenderContext();
		Aaa.commonGraphicsSettings(g2);

		Dimension panelSize = getSize();
		// System.out.println("paint HandDisplayPanel - Size " + panelSize);

		if (getParent() instanceof HandDisplayPanel) {
			for (Component c1 : getParent().getComponents()) {
				if ((c1 instanceof HandDisplayPanel))
					continue;
				@SuppressWarnings("unused")
				int z = 0; // We should find nothing
				getParent().remove(c1);
			}
		}

		boolean slim = floatingHand && !questionHand;

		// fill the dealLozenge ----------------------------------------------

		float lineThickness = ((float) panelSize.height) / 130f;
		float boarderThickness = ((float) panelSize.height) / 30f;

		BasicStroke ourOutline = new BasicStroke(lineThickness);

		float dealLozengeWidth = ((float) panelSize.width) - boarderThickness * (slim ? 0 : 2.0f);
		float dealLozengeHeight = ((float) panelSize.height) - boarderThickness * (slim ? 0 : 2.0f);

		Rectangle2D rd = new Rectangle2D.Float(boarderThickness, boarderThickness, dealLozengeWidth, dealLozengeHeight);

		// Fill the Hands background
		// --------------------------------------------------
		if (!floatingHand) {
			g2.setColor(hand.isDummy() ? Aaa.handBkColorDummy : Aaa.handBkColorStd);
			g2.fill(rd);

			g2.setStroke(ourOutline);
			g2.setColor(Aaa.handAreaOffWhite);
			g2.draw(rd);
		}

		// fill the you Lozenge
		// ---------------------------------------------------

		float youLozengeHeight = 0.0f;

		boolean visSeat = true;
		String displayedWordInBox = Aaf.game_hidden;
		Color displayedWordColor = hand.isDummy() ? Aaa.veryWeedyBlacHid : Aaa.veryWeedyBlack;

		if (!floatingHand) {

			boolean dummyOk = App.isMode(Aaa.REVIEW_BIDDING);

			boolean youSeatUs = (deal.getTheYouSeat(dummyOk) == hand.compass);

			Color bannerColor = Aaa.othersBannerBk;
			Color pointsColor = Cc.g(Cc.pointsColor);

			if (youSeatUs) {
				bannerColor = Cc.g(Cc.youSeatBannerBk);
			}
			else if (deal.qx_room == 'o' || deal.qx_room == 'c') {
				int oc = (deal.qx_room == 'o') ? 0 : 1;
				bannerColor = Aaa.teamBannerColorAy[oc][hand.compass.v % 2];
			}

			youLozengeHeight = (dealLozengeHeight) * 0.18f;

			/* kept */youDisplayRect = new Rectangle2D.Float(boarderThickness, boarderThickness, dealLozengeWidth, youLozengeHeight);

			g2.setColor(bannerColor);
			g2.fill(youDisplayRect);

			g2.setStroke(ourOutline);
			g2.setColor(Color.WHITE);
			g2.draw(youDisplayRect);

			float xy = boarderThickness;
			float nlh = youLozengeHeight;

			// fill the NESW indicator
			// -----------------------------------------------------
			Rectangle2D rtNESW = new Rectangle2D.Float(xy, xy, nlh, nlh);

			g2.setColor(Aaa.handNeswBkColor);
			g2.fill(rtNESW);

			g2.setStroke(ourOutline);
			g2.setColor(Color.WHITE);
			g2.draw(rtNESW);

			float bridgeTextFontSize = (float) nlh * 0.73f;

			String seat = Dir.getLangDirChar(hand.compass) + "";

			g2.setFont(BridgeFonts.internationalFont.deriveFont(bridgeTextFontSize));

			g2.setColor(Color.WHITE);
			Aaa.drawCenteredString(g2, seat, xy, xy, nlh, nlh);

			g2.setColor(pointsColor); // Also used by the 'You' field

			// show the points
			// ------------------------------------------------------------------
			visSeat = App.isSeatVisible(hand.compass);
			if (deal.isDoneHand()) {
				displayedWordInBox = Aaf.box_text[hand.compass.v];
				displayedWordColor = Aaa.weedyBlack;
				visSeat = false;
			}

			boolean displayMetrics = !deal.dfcDeal && visSeat && hand.didHandStartWith13Cards() && !deal.isDoneHand();
			boolean displayHCPs = displayMetrics && App.showHCPs;
			boolean display2ndMetric = displayMetrics && (App.show2ndMetric != App.Metric_None);

			// The "You" text and name
			// ------------------------------------------------------------------
			float yAdj = 0.226f;
			if (youSeatUs && (hand.playerName.isEmpty() || App.isMode(Aaa.EDIT_PLAY))) {
				float youTextFontSize = bridgeTextFontSize * 1.2f;
				Font youTextFont = BridgeFonts.internationalFont.deriveFont(youTextFontSize);
				g2.setFont(youTextFont);
				String s = (display2ndMetric) ? "  " : "       ";
				g2.drawString(s + Aaf.game_youSeat, (int) (xy + nlh * 1.5), (int) (xy + nlh - youTextFontSize * yAdj));
			}
			else if (App.isMode(Aaa.EDIT_PLAY)) {
				// we don't show names when in edit_play mode
				// names are CLEARED when entering other EDIT modes or when play changes are made
			}
			else if (hand.playerName.isEmpty() == false) {
				String shown = hand.playerName;
				if (App.handPanelNameAreaInfoNumbersShow == false) {
					// do nothing extra
				}
				else if (display2ndMetric && (App.show2ndMetric == App.Metric_KnR) && (shown.length() > 4)) {
					shown = shown.substring(0, 4);
				}
				else if (display2ndMetric && (shown.length() > 6)) {
					shown = shown.substring(0, 6);
				}
				else if (displayHCPs && shown.length() > 13) {
					shown = shown.substring(0, 13);
				}

				if ((App.show2ndMetric != App.Metric_None) && (shown.length() > 2)) {
					;
					shown = shown.charAt(0) + shown.substring(1).toLowerCase();
				}

				float youTextFontSize = bridgeTextFontSize * 1.2f;
				Font youTextFont = BridgeFonts.internationalFont.deriveFont(youTextFontSize);
				g2.setFont(youTextFont);
				g2.drawString(shown, (int) (xy + nlh * 1.5), (int) (xy + nlh - youTextFontSize * yAdj));
			}

			Font pointsFont = BridgeFonts.internatBoldFont.deriveFont(nlh * 1.0f);
			if (App.handPanelNameAreaInfoNumbersShow && displayHCPs) {
				Rectangle2D bkgRect = new Rectangle2D.Float(dealLozengeWidth * 0.88f, xy + (nlh * 0.10f), nlh, nlh * 0.80f);
				Color colText = g2.getColor();
				g2.setColor(bannerColor);
				g2.fill(bkgRect);

				g2.setColor(colText);
				g2.setFont(pointsFont);
				Aaa.drawCenteredString(g2, Integer.toString(hand.count_HighCardPoints()), dealLozengeWidth * 0.88f, xy, nlh, nlh);
			}

			if (App.handPanelNameAreaInfoNumbersShow && display2ndMetric) {
				float p = 0.57f;

				Rectangle2D bkgRect = new Rectangle2D.Float(dealLozengeWidth * p, xy + (nlh * 0.10f), nlh, nlh * 0.80f);
				Color colText = g2.getColor();
				g2.setColor(bannerColor);
				g2.fill(bkgRect);

				g2.setColor(colText);
				g2.setFont(pointsFont);

				if (App.show2ndMetric == App.Metric_LTC_Bas) { //
					int v = hand.countLosingTricks_Basic_x2();
					Aaa.drawCenteredString(g2, Integer.toString(v / 2), dealLozengeWidth * p, xy, nlh, nlh);
					if (v % 2 == 1) {
						p += 0.09;
						bkgRect = new Rectangle2D.Float(dealLozengeWidth * p, xy + (nlh * 0.10f), nlh, nlh * 0.80f);
						colText = g2.getColor();
						g2.setColor(bannerColor);
						g2.fill(bkgRect);

						g2.setColor(colText);
						Aaa.drawCenteredString(g2, "" + (char) 0xbd, dealLozengeWidth * p, xy, nlh, nlh);
					}
				}

				if (App.show2ndMetric == App.Metric_LTC_Ref) { //
					int v = hand.countLosingTricks_Ref_x2();
					Aaa.drawCenteredString(g2, Integer.toString(v / 2), dealLozengeWidth * p, xy, nlh, nlh);
					if (v % 2 == 1) {
						p += 0.09;
						bkgRect = new Rectangle2D.Float(dealLozengeWidth * p, xy + (nlh * 0.10f), nlh, nlh * 0.80f);
						colText = g2.getColor();
						g2.setColor(bannerColor);
						g2.fill(bkgRect);

						g2.setColor(colText);
						Aaa.drawCenteredString(g2, "" + (char) 0xbd, dealLozengeWidth * p, xy, nlh, nlh);
					}
				}

				else if (App.show2ndMetric == App.Metric_KnR) { //
					double val = hand.count_KnR();
					String s = String.format("%.2f", val);
					Aaa.drawCenteredString(g2, s, dealLozengeWidth * p, xy, nlh, nlh);
				}

				else if (App.show2ndMetric == App.Metric_Banzai) { //
					int t = hand.count_Banzai();
					Aaa.drawCenteredString(g2, Integer.toString(t), dealLozengeWidth * p, xy, nlh, nlh);
				}

			}

		}

		// The four Suits
		// ------------------------------------------------------------------

		// the four hands

		float suitAreaHeight = (float) (dealLozengeHeight - youLozengeHeight);

		float suitLineHeight = (float) suitAreaHeight * (slim ? 0.25f : 0.25f);

		float suitLineStartY = (float) (boarderThickness + lineThickness + youLozengeHeight);

		float suitLineStartX = (float) (boarderThickness + lineThickness);

		float handFontSize = (float) (suitLineHeight) * (slim ? 0.98f : 0.97f);

		if (!floatingHand && !visSeat) {
			g2.setFont(BridgeFonts.internationalFont.deriveFont(handFontSize));
			g2.setColor(displayedWordColor);
			g2.drawString(displayedWordInBox, dealLozengeWidth * (deal.isDoneHand() ? 0.15f : 0.3f), dealLozengeHeight * 0.650f);
		}

		Font suitSymbolFont = BridgeFonts.faceAndSymbolFont.deriveFont(handFontSize * 0.65f);
		Font cardFaceFont = BridgeFonts.faceAndSymbolFont.deriveFont(handFontSize);
		Font cardFaceInternational = BridgeFonts.internatBoldFont.deriveFont(handFontSize);

		Frag[] frags = getAppropriateFrags();

		boolean ddsInUse = false;

		if ((App.ddsDeal != null) && App.ddsScoreShow) {
			Hand ddsHand = App.ddsDeal.hands[hand.compass.v];
			Frag ddsFrags[] = ddsHand.frags;
			if (ddsHand.ddsValuesAssigned) {
				if ((ddsInUse = doFragsMatchCardsAndCopyScore(frags, ddsFrags)) == false) {
					System.out.println("doFragsMatchCardsAndCopyScore()  returned  FALSE");
				}
			}
		}

		for (Frag frag : frags) {

			FragDisplayInfo fdi = fdiA[frag.suit.v];

			// Spades on the top row down to clubs last
			int row = Suit.Spades.v - frag.suit.v;
			fdi.tl = null;

			char showXes = frag.getShowXes();

			// System.out.print(showXes);

			if (deal.dfcDeal && App.dfcAnonCards) {
				showXes = 'x';
			}

			String rawCards = frag.toScrnStr();
			if (App.fillHandDisplay) {
				rawCards = generateTestCards(frag); // <<<<<<<<<<<<<<<<<<<< TEST CARD GENERATOR >>>>>>>>
				showXes = '-';
			}

			int[] ddsRightPos = new int[13];
			String cards = addTransformationsAndPadding(rawCards, showXes, ddsRightPos);

			boolean showSuitSymbol = !deal.dfcDeal && App.showSuitSymbols;
			float lhs = suitLineStartX * (slim ? 0.00f : 1.0f) + suitLineHeight * (slim ? 0.00f : 0.125f);
			float normStart = lhs + suitLineHeight * (slim ? 0.8f : 0.9f);
			float x = normStart;
			float y = suitLineStartY * (slim ? -0.8f : 0.87f) + suitLineHeight * (row + 1);

			if (cards.length() / 3 >= (slim ? 8 : 11)) {
				showSuitSymbol = false;
				x = lhs * (slim ? 0.00f : 1.0f);
			}

			// Suit Symbol
			if (deal.dfcDeal) { // symbols are never shown but instead we CAN show count or dots or words
				if ((frag.suitVisControl & Suit.SVC_count) == Suit.SVC_count) {
					String v = "";
					g2.setColor(Cc.BlackStrong);
					if (App.dfcWordsForCount && (App.ddsDealHandDispExamNumbWordsSuppress == false)) {
						Font wordsFont = BridgeFonts.internatBoldFont.deriveFont(handFontSize * 0.95f);
						g2.setFont(wordsFont);
						v = Aaf.numbersAsWords[frag.size()];
					}
					else {
						g2.setFont(cardFaceFont);
						v = frag.size() + "";
						if (frag.size() == 0) {
							if (App.dfcHyphenForVoids)
								v = "~"; // ~ - changed in font to be same size as other numbers and higher
							else
								g2.setColor(Cc.BlackWeedy);
						}
					}
					g2.drawString(v, lhs, y - suitLineStartY * 0.0f);
				}
				else if ((frag.suitVisControl & Suit.SVC_dot) == Suit.SVC_dot) {
					g2.setColor(Cc.BlackStrong);
					g2.setFont(suitSymbolFont);
					g2.drawString("o" /* dot in dcf font */, lhs, y - suitLineStartY * 0.2f); // the "(" will appear as a BIG dot
				}
			}
			else if (showSuitSymbol) {
				g2.setColor(frag.suit.color(Cc.Ce.Weak));
				g2.setFont(suitSymbolFont);
				g2.drawString(frag.suit.toStrLower(), lhs, y - suitLineStartY * (slim ? 0.5f : 0.1f));
				g2.setColor(Color.black);
			}

			// Cards of the frag
			if (cards.isEmpty() || !visSeat || (deal.dfcDeal && ((frag.suitVisControl & Suit.SVC_cards) != Suit.SVC_cards) /* the dfc hide suit mechanism */))
				continue;

			AttributedString astr = new AttributedString(cards);
			astr.addAttribute(TextAttribute.FONT, cardFaceFont);

			for (int i = 0; i < cards.length(); i += 3) {
				if (Aaa.hasUni(cards.charAt(i + 1))) {
					astr.addAttribute(TextAttribute.FONT, cardFaceInternational, i + 1, i + 2);
				}
			}

			if ((fdi.highlightIndex > -1) && (cards.length() / 3 > fdi.highlightIndex)) {
				Color col = (fdi.highlightSel) ? Aaa.cardClickedOn : Aaa.cardHover;
				astr.addAttribute(TextAttribute.BACKGROUND, col, fdi.highlightIndex * 3, fdi.highlightIndex * 3 + 3);
			}

			fdi.tl = new TextLayout(astr.getIterator(), frc);
			fdi.layoutOriginX = x;
			fdi.layoutOriginY = y;
			g2.setColor(frag.suit.colorCd(Cc.Ce.Strong));
			fdi.tl.draw(g2, x, y);

			// Compute the mouse click location relative to
			// textLayout's origin and cache it for mouse hit/movement testing.
			fdi.bounds = fdi.tl.getBounds();
			// adjust to actual position in the panel
			fdi.bounds.setRect(fdi.bounds.getX() + fdi.layoutOriginX, fdi.bounds.getY() + fdi.layoutOriginY, fdi.bounds.getWidth(), fdi.bounds.getHeight());

			// Debugging aid
			// g2.draw(fdi.bounds);

			if (ddsInUse) {

				float fontScale = 0.55f;
				double boxScale = 1.40f;
				Font ddsScoreFont = BridgeFonts.faceAndSymbolFont.deriveFont(handFontSize * fontScale);
				TextLayout tlc = new TextLayout("T", ddsScoreFont, frc);
				double ddsCharWidth = tlc.getBounds().getWidth() * boxScale;
				double ddsCharHeight = tlc.getBounds().getHeight() * boxScale;
				float dotDiameter = (float) ddsCharWidth * 0.55f;

				int totalCards = frag.size();

				int highestDdsScore = App.ddsDeal.getHighestDdsScore();

				int wonSoFar = App.ddsDeal.getContractTrickCountForDirecton(hand.compass);

				for (int i = 0; i < totalCards; i++) {

					Card card = frag.get(i);
					if (card.getDdsScore() == -1)
						continue;

					x = (float) ((fdi.bounds.getMinX() + (fdi.bounds.getWidth() * ddsRightPos[i]) / ddsRightPos[totalCards - 1]) - ddsCharWidth);
					y = (float) (fdi.bounds.getMaxY() + ddsCharHeight * 0.30);

					double w = ddsCharWidth * 1.10;
					double w2 = ddsCharWidth * 0.35;
					double h = ddsCharHeight * 1.10;
					RoundRectangle2D ddsBackground = new RoundRectangle2D.Double((double) x + w2, y - h, w, h, w, h);

					// fill with the score background box colour
					g2.setColor(((card.getDdsScore() == highestDdsScore) ? scoreBestFill : scoreOthrFill));
					g2.fill(ddsBackground);

					g2.setColor(Cc.BlackStrong);
					TextLayout text = new TextLayout(card.getDisplayScore(wonSoFar), ddsScoreFont, frc);
					drawTextlayoutCenter(g2, text, ddsBackground.getBounds2D());

					if (card.getDdsNextCard()) {
						Ellipse2D.Float nextCardToPlayDot = new Ellipse2D.Float((x + (float) ddsCharWidth * 0.4f), (y - (float) ddsCharHeight * 1.70f),
								dotDiameter, dotDiameter);
						g2.setColor(((card.getDdsScore() == highestDdsScore) ? scoreBestFill : scoreOthrFill));
						g2.fill(nextCardToPlayDot);
						// add outline
						g2.setColor(((card.getDdsScore() == highestDdsScore) ? scoreBestDot : scoreOthrDot));
						g2.setStroke(new BasicStroke((float) ddsCharWidth * 0.12f));
						g2.draw(nextCardToPlayDot);
					}

				}
			}

		}

		// best to clear up these copied over scores
		for (Frag frag : frags) {
			for (Card card : frag) {
				card.setDdsScore(-1);
				card.setDdsNextCard(false);
			}
		}
	}

	// @formatter:off
	final static Color scoreBestFill = new Color(200, 240, 190);
	final static Color scoreOthrFill = new Color(224, 224,  90);
	
	final static Color scoreBestOutl = new Color(180, 230, 174);
	final static Color scoreOthrOutl = new Color(215, 215,  80);
	
	final static Color scoreBestDot  = new Color(100, 200, 124);
	final static Color scoreOthrDot  = new Color(150, 150,  20);	
	// @formatter:on

	public void drawTextlayoutCenter(Graphics2D g2, TextLayout text, Rectangle2D background) {

		Rectangle2D bounds = text.getBounds();

		float x = (float) (background.getX() + (((background.getWidth() - bounds.getWidth()) / 2)) * 0.90 /* because it looks better */);
		float y = (float) (background.getMaxY() - (((background.getHeight() - bounds.getHeight()) / 2)) * 0.90 /* because it looks better */);

		text.draw(g2, x, y);
	}

}
