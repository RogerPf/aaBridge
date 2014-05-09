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
package com.rogerpf.aabridge.model;

import java.awt.geom.RoundRectangle2D;

/**
 * Bid
 */
public class Bid {

	public Call call;
	public Level level;
	public Suit suit;
	public boolean alert = false;
	public transient String alertText;
	public transient char suitCh;
	public transient RoundRectangle2D.Float rr2dBid = null;
	public transient RoundRectangle2D.Float rr2dAlertText = null;
	public transient boolean hover;

	public Bid(Level level, Suit suit) {
		assert (level == Level.One || level == Level.Two || level == Level.Three || level == Level.Four || level == Level.Five || level == Level.Six || level == Level.Seven);
		assert (suit == Suit.Clubs || suit == Suit.Diamonds || suit == Suit.Hearts || suit == Suit.Spades || suit == Suit.NoTrumps);
		this.call = Call.RealBid;
		this.level = level;
		this.suit = suit;
		suitCh = suit.toChar();
		alert = false;
		alertText = "";
		hover = false;
	}

	public Bid(Call c) {
		if (!(c == Call.NullBid || c == Call.Pass || c == Call.Double || c == Call.ReDouble)) {
			@SuppressWarnings("unused")
			int z = 0; // put your breakpoint here
		}
		assert (c == Call.NullBid || c == Call.Pass || c == Call.Double || c == Call.ReDouble);
		call = c;
		level = Level.Invalid;
		suit = Suit.Invalid;
		alert = false;
		alertText = "";
		hover = false;
	}

	public boolean isNullBid() {
		return (call == Call.NullBid);
	}

	public boolean isPass() {
		return (call == Call.Pass);
	}

	public boolean isDouble() {
		return (call == Call.Double);
	}

	public boolean isReDouble() {
		return (call == Call.ReDouble);
	}

	public boolean isCall() {
		return (call != Call.RealBid);
	}

	public boolean isValidBid() {
		return (call == Call.RealBid);
	}

	// --------------------------------
	public Call getCall() {
		return (isCall()) ? call : Call.Invalid;
	}

	// --------------------------------
	public String toString() {
		return (isCall()) ? call.toString() : level.toStr() + suit.toStr();
	}

	// --------------------------------
	public String toInnocuousAnswer() {
		return (isCall()) ? call.toCmdString() : level.toStr() + suit.toStrLower();
	}

	// --------------------------------
	public String toLinAnswerString(Suit suitV[]) {
		if (isCall()) {
			suitV[0] = Suit.Invalid;
			return call.toBidPanelString();
		}
		else if (suit == Suit.NoTrumps) {
			suitV[0] = Suit.Invalid;
			return (level.v + " NT");
		}

		suitV[0] = suit;
		return (level.v + "");
	}

//	// --------------------------------
//	public String toLinAnswerString() {
//		if (isCall()) {
//			return call.toBidPanelString();
//		}
//		else if (suit == Suit.NoTrumps) {
//			return (level.v + " NT");
//		}
//		
//		return (level.v + "");
//	}

	// --------------------------------
	public String toLinStr() {
		return (isCall()) ? call.toLinStr() : level.toStr() + suit.toLinStr();
	}

	/**
	 */
	public boolean isLowerThanOrEqual(Bid cb) {
		if (isCall())
			return true; // as PASS sometimes be used as the initial
							// compariator
		if (cb.isCall())
			return false; // they should not do this
		if (level.v > cb.level.v)
			return false;
		if (level.v < cb.level.v)
			return true;
		// equal levels
		if (suit.v > cb.suit.v)
			return false;
		if (suit.v < cb.suit.v)
			return true;
		return true; // this incomming bid is the same and so invalid hence we
						// return true
	}

	/**
	 * Called with a Lin style bid which (old format) may have MULTIPLE bids in the one string 
	 */
	public static Bid linStringToSingleBid(String bids) {
		// ==============================================================================================
		Level level = Level.Invalid;
		Suit suit = Suit.Invalid;
		Bid prevBid = null;

		for (int i = 0; i < bids.length(); i++) {
			char c = bids.charAt(i);

			if (c == 'p' || c == 'P') {
				return new Bid(Call.Pass);
			}
			if ((level == Level.Invalid) && (c == 'd' || c == 'D' || c == '*' || c == 'x' || c == 'X')) { // 'd' is double '3d' is 3 Diamonds
				return new Bid(Call.Double);
			}
			if (c == 'r' || c == 'R') {
				return new Bid(Call.ReDouble);
			}
			if ('1' <= c && c <= '7') {
				level = Level.levelFromInt(c - '0');
				continue;
			}
			if (c == '!') {
				if (prevBid != null)
					prevBid.alert = true;
				continue;
			}
			// @formatter:off
			switch (c) {
				case 'N': case 'n': suit = Suit.NoTrumps; break;
				case 'S': case 's': suit = Suit.Spades;   break;
				case 'H': case 'h': suit = Suit.Hearts;   break;
				case 'D': case 'd': suit = Suit.Diamonds; break; // lower case 'd' is 'double'  !
				case 'C': case 'c': suit = Suit.Clubs;    break;
				default: suit = Suit.Invalid;
			}
			// @formatter:on
		}

		if (suit == Suit.Invalid || level == Level.Invalid) {
			return new Bid(Call.Invalid);
		}

		return new Bid(level, suit);
	}

}
