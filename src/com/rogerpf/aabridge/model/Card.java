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

/**
 * 
 * Card
 */
public class Card {

	/**
	 * 
	 */
	public final Rank rank;
	transient char rankCh;
	transient Rank rankRel;
	transient Rank rankEqu;

	public Suit suit;
	transient char suitCh;

	Card(Rank rank, Suit suit) { /* Constructor */
		this.rank = rank;
		rankCh = rank.toChar();
		rankRel = Rank.Invalid;
		rankEqu = Rank.Invalid;
		this.suit = suit;
		suitCh = suit.toChar();
	}

	public String toString() {
		return rank.toStr() + suit.toStr();
	}

	public boolean matches(Rank rank, Suit suit) {
		return (this.rank == rank && this.suit == suit);
	}

	public boolean isBetterThan(Card bestSoFar, Suit suitTrumps) {
		//@formatter:off
		return  ((suit == bestSoFar.suit) && (rank.v > bestSoFar.rank.v))
			 || ((suit != bestSoFar.suit) && (suit == suitTrumps)); 
		//@formatter:on
	}

	public boolean isMaster() {
		return rankRel == Rank.Ace;
	}

	public String toLinStr() {
		return suit.toStr() + rank.toStr();
	}

	public static Card singleCardFromLinStr(String hs) {
		if (hs.length() != 2)
			return null;

		Suit suit = Suit.charToSuit(hs.charAt(0));
		if (suit == Suit.Invalid)
			return null;

		Rank rank = Rank.charToRank(hs.charAt(1));
		if (rank == Rank.Invalid)
			return null;

		return new Card(rank, suit);
	}

	public String toLinAnswerString(Suit suitV[]) {
		suitV[0] = suit;
		return rank == Rank.Ten ? "10" : rank.toStr();
	}

}
