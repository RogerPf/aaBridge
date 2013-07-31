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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * 
 * Frag...ment of a Suit
 */
public class Frag extends Cal implements Serializable, Comparable<Frag> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2945787433797684005L;

	public Hand hand;
	public int suit;
	transient char suitCh;

	Frag(Hand handV, int suitV) { /* Constructor */
		super();
		hand = handV;
		suit = suitV;
		suitCh = Zzz.suit_to_cdhsnCh[suitV];
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		ObjectInputStream.GetField fields = in.readFields(); // magic

		hand = Hand.class.cast(fields.get("hand", null));

		// field renamed on 2013-July-09
		if (fields.defaulted("suit")) {
			suit = fields.get("suitValue", (int) 0xdeadbeef);
		}
		else {
			suit = fields.get("suit", (int) 0xdeadbeef);
		}
	}

	public int getSuit() {
		return suit;
	}

	public char getSuitCh() {
		return Zzz.suit_to_cdhsnCh[suit];
	}

	public String getSuitSt() {
		return Zzz.suit_to_cdhsnSt[suit];
	}

	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(' ');
		sb.append(getSuitCh());
		sb.append(" - ");

		return sb.toString() + super.toString();
	}

	public String relFaceSt() {
		final StringBuilder sb = new StringBuilder();
		for (Card card : this) {
			sb.append(card.getRankRelCh());
		}
		return sb.toString();
	}

	public String equFaceSt() {
		final StringBuilder sb = new StringBuilder();
		for (Card card : this) {
			sb.append(card.getRankEquCh());
		}
		return sb.toString();
	}

	public int compareTo(Frag other) {
		assert (false); // NOT USED (but required) - instead we use fuction comps
		return 0;
	}

	boolean areTopTwoContigious() {
		if (size() < 2)
			return false;
		return (get(0).rankRel == get(1).rankRel + 1);
	}

	public boolean areAllBetterThan(int rank) {
		if (size() == 0)
			return false;
		return getLast().rank > rank;
	}

	public int countContigious() {
		// ==============================================================================================
		if (hand.frags[suit].size() == 0)
			return 0;
		int prev = get(0).rankRel + 1;
		int v = 0;
		for (Card card : this) {
			if (prev > card.rankRel + 1)
				break;
			v++;
			prev = card.rankRel;
		}

		return v;
	}

	/**
	 * How many of the cards are contigious with a given start point.
	 * @param startCardCountBelowAce
	 * @return depth of the run of the combined hands
	 * 	When run on 'combined' hands 
	 *    the caller still needs to allow for the max of the two sepaerate Frag sizes
	 */
	int contigCards(int startCardCountBelowAce) {
		// ==============================================================================================
		int d = 0;
		int t = Zzz.Ace - startCardCountBelowAce;
		for (Card card : this) {
			if (card.rankRel != t--)
				break;
			d++;
		}
		return d;
	}

	boolean isMissingKing() {
		// ==============================================================================================
		return (size() > 1) && this.get(1).rankRel != Zzz.King;
	}

	boolean isMissingAceQueen() {
		// ==============================================================================================
		return (size() > 2) && (this.get(0).rankRel != Zzz.Ace) && (this.get(2).rankRel != Zzz.Queen);
	}

	public Card getHighest(boolean contigHigh) {
		// ==============================================================================================
		if (size() == 0)
			return null;

		if (contigHigh) {
			return get(0);
		}

		int rankEqu = get(0).rankEqu;
		for (int k = size() - 1; k >= 0; k--) {
			if (get(k).rankEqu == rankEqu)
				return get(k);
		}
		return getLast();
	}

	public Card getLowestThatBeatsOrLowest(boolean contigHigh, int rankToBeat) {
		// ==============================================================================================
		for (int i = size() - 1; i >= 0; i--) {
			if (get(i).rank > rankToBeat) {
				if (!contigHigh) {
					return get(i);
				}
				else {
					int rankEqu = get(i).rankEqu;
					for (int k = 0; k <= i; k++) {
						if (get(k).rankEqu == rankEqu)
							return get(k);
					}
				}
			}
		}
		return getLast();
	}

	public Card getLowestThatBeats(boolean contigHigh, int rankToBeat) {
		// ==============================================================================================
		for (int i = size() - 1; i >= 0; i--) {
			if (get(i).rank > rankToBeat) {
				if (!contigHigh) {
					return get(i);
				}
				else {
					int rankEqu = get(i).rankEqu;
					for (int k = 0; k <= i; k++) {
						if (get(k).rankEqu == rankEqu) {
							return get(k);
						}
					}
				}
			}
		}
		return null;
	}

	public Card getHighestThatLosesTo(int rankToLoseTo) {
		for (int i = 0; i < size(); i++) {
			if (get(i).rank < rankToLoseTo)
				return get(i);
		}
		return null;
	}

	public void keepHighest(int n) {
		while (size() > n) {
			remove(size() - 1);
		}
	}

	public Card highestContigWith(Card target) {
		for (Card card : this) {
			if (card.rankEqu == target.rankEqu)
				return card;
		}
		return target;
	}

}
