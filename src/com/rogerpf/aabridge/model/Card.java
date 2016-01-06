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
	char rankCh;
	Rank rankRel;
	Rank rankEqu;
	int ddsScore;
	boolean ddsNextCard;

	public Suit suit;
	transient char suitCh;

	Card(Rank rank, Suit suit) { /* Constructor */
		this.rank = rank;
		rankCh = rank.toChar();
		rankRel = Rank.Invalid;
		rankEqu = Rank.Invalid;
		this.suit = suit;
		suitCh = suit.toChar();
		ddsScore = -1; // magic value meaning - not set
		ddsNextCard = false;
	}

	public String toString() {
		return rank.toStr() + suit.toStr();
	}

	public boolean matches(Rank rank, Suit suit) {
		return (this.rank == rank && this.suit == suit);
	}

	public boolean matches(Card card) {
		return (this.rank == card.rank && this.suit == card.suit);
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

	// --------------------------------
	public String toInnocuousAnswer() {
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

	public int getDdsScore() {
		return ddsScore;
	}

	public void setDdsScore(int ddsScore) {
		this.ddsScore = ddsScore;
	}

	public String getDisplayScore(int wonSoFar) {
		// @formatter:off
		int adjustedScore = ddsScore + wonSoFar;
		switch (adjustedScore) {
		    case  1: return "1>"; // shows as 1 a bit to the left
			case 10: return "T";  // shows as 10
			case 11: return "X";  // shows as 11
			case 12: return "Y";  // shows as 12
			case 13: return "Z";  // shows as 13
			default: return adjustedScore + "";
		}
		// @formatter:on
	}

	public void setDdsNextCard(boolean ddsNextCard) {
		this.ddsNextCard = ddsNextCard;
	}

	public boolean getDdsNextCard() {
		return ddsNextCard;
	}

}
