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

import java.util.ArrayList;

/**
 * 
 * 
 */
public class Cal extends ArrayList<Card> implements Comparable<Cal> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Cal() { /* Constructor */
		super();
	}

	Cal(int n) { /* Constructor */
		super(n);
	}

	@Override
	public int compareTo(Cal arg0) {
		if (arg0.size() == this.size())
			return 0;
		if (arg0.size() > this.size())
			return -1;
		return 1;
	}

	public Card getIfRankExists(Rank rank) {
		for (Card card : this) {
			if (card.rank == rank)
				return card;
		}
		return null;
	}

	public Card getIfSuitAndRankExists(Suit suit, Rank rank) {
		for (Card card : this) {
			if (card.rank == rank && card.suit == suit)
				return card;
		}
		return null;
	}

	public Card getIfRelExist(Rank rankRel) {
		for (Card card : this) {
			if (card.rankRel == rankRel)
				return card;
		}
		return null;
	}

	public Card getIfEquExistsHigh(Rank rankEqu) {
		for (Card card : this) {
			if (card.rankEqu == rankEqu)
				return card;
		}
		return null;
	}

	public Card getIfEquExistsLow(Rank rankEqu) {
		for (int i = size() - 1; i >= 0; i--) {
			Card card = this.get(i);
			if (card.rankEqu == rankEqu)
				return card;
		}
		return null;
	}

	public Card getIfEquExists(boolean high, Rank rankEqu) {
		if (high)
			return getIfEquExistsHigh(rankEqu);
		else
			return getIfEquExistsLow(rankEqu);
	}

	public Card getFirst() {
		if (size() == 0)
			return null;
		else
			return get(0);
	}

	public Card getSecond() {
		if (size() == 0)
			return null;
		else if (size() == 1)
			return get(0);
		else
			return get(1);
	}

	public Card getLast() {
		if (size() == 0)
			return null;
		else
			return get(size() - 1);
	}

	public Card removeLast() {
		if (size() == 0)
			return null;
		else
			return remove(size() - 1);
	}

	/** 
	 */
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		for (Card c : this) {
			sb.append(c.rank.toChar());
			sb.append(' ');
		}
		return sb.toString();
	}

	/** 
	 */
	public String pdbSuit() {
		final StringBuilder sb = new StringBuilder();
		for (Card c : this) {
			sb.append(c.rank.toChar());
		}
		return sb.toString();
	}

	/** 
	 */
	public String toScrnStr() {
		final StringBuilder sb = new StringBuilder();

		for (Card c : this) {
			sb.append(c.rank.toChar());
		}

		return sb.toString();
	}

	/** 
	 */
	public String toDisplayStr() {
		final StringBuilder sb = new StringBuilder();

		for (Card card : this) {
			sb.append(card.displayChar);
		}

		return sb.toString();
	}

	/** 
	 */
	public void clearAllKeptFlags() {
		for (Card c : this) {
			c.setKept(false);
		}
	}

	/** 
	 */
	public Card getCard(int index) {
		if (index >= size())
			return null;
		return get(index);
	}

	/** 
	 */
	public Card getCardOrLowest(int index) {
		if (index >= size())
			index = size() - 1;
		if (index < 0)
			return null;

		return get(index);
	}

	/** 
	 */
	public Card removeCard(int index) {
		return remove(index);
	}

	/** 
	 */
	public void addDeltCard(Card card) {
		if (size() > 0) {
			for (int i = 0; i < size(); i++) {
				if (get(i).rank.v < card.rank.v) {
					add(i, card);
					return;
				}
			}
		}
		add(card);
	}

	/** 
	 */
	public int count_HighCardPoints() {
		int v = 0;
		for (Card card : this) {
			v += (card.rank.v > Rank.Ten.v) ? card.rank.v - Rank.Ten.v : 0;
		}
		return v;
	}

	/** 
	 */
	public int count_LongSuitPoints() {
		return (size() < 5) ? 0 : size() - 4;
	}

	/** 
	 */
	public int count_ShortSuitPoints() {
		switch (size()) {
		case 0:
			return 5;
		case 1:
			return 3;
		case 2:
			return 1;
		}
		return 0;
	}

	/** suitQuality
	 */
	public int suitQuality() {

		int top3 = 0;
		int JorT = 0;

		for (Card card : this) {
			Rank r = card.rank;
			// @formatter:off
		    switch (r) {
                default: continue;
				case Ace:
				case King:
				case Queen: top3++; continue;
				case Jack:
				case Ten:   JorT++; continue;
		    }
		    // @formatter:on
		}

		int t = size() + top3 + (top3 > 1 ? JorT : 0);

		return t;
	}

	/** count_Banzai
	 */
	public int count_Banzai() {
		int t = 0;

		for (Card card : this) {
			t += (card.rank.v >= Rank.Ten.v) ? (card.rank.v - Rank.Nine.v) : 0;
		}

		if (size() >= 5) {
			t += 2;
			if (suitQuality() >= 8) {
				t += 1;
			}
		}

		return t;
	}

	/** count_KnR
	*/
	public double count_KnR() {

		double t = 0;

		int top3 = 0;
		int top4 = 0;

		boolean has_Ace = false;
		boolean has_King = false;
		boolean has_Queen = false;
		boolean has_Jack = false;
		boolean has_Ten = false;
		boolean has_Nine = false;
		boolean has_Eight = false;

		int len = size();

		for (Card card : this) {
			Rank r = card.rank;
			// @formatter:off
		    switch (r) {
		    	default: continue;
		    	case Ace:   t += 4;   has_Ace   = true; top4++; top3++; continue;  // Step 1
		    	case King:  t += 3;   has_King  = true; top4++; top3++; continue;  // Step 2
		    	case Queen: t += 2;   has_Queen = true; top4++; top3++; continue;  // Step 3
		    	case Jack:  t += 1;   has_Jack  = true; top4++; continue;  // Step 4
		        case Ten:   t += 0.5; has_Ten   = true; continue;  // Step 5
		    	case Nine:            has_Nine  = true; continue;		    	
		    	case Eight:           has_Eight = true; continue;		    	
		    }
		    // @formatter:on
		}

		if (2 <= len && len <= 6) {
			if (has_Ten && (has_Jack || top3 >= 2)) // Step 6
				t += 0.5;
			if (has_Nine && (has_Eight || has_Ten || top4 == 2)) // Step 7
				t += 0.5;
		}

		if (4 <= len && len <= 6) {
			if (has_Nine && (!has_Eight && !has_Ten && top4 == 3)) // Step 8
				t += 0.5;
		}

		if (7 <= len && (!has_Queen || !has_Jack)) // Step 9
			t += 1;

		if (8 <= len && (!has_Queen)) // Step 10
			t += 1;

		if (9 <= len && (!has_Queen && !has_Jack)) // Step 11
			t += 1;

		t = (t * len) / 10; // Step 12a

		if (has_Ace) // Step 12b
			t += 3;

		if (has_King) // Step 13 and 14
			t += (len == 1) ? 0.5 : 2;

		if (has_Queen && (len >= 3)) // Step 15 and 16
			t += (has_Ace || has_King) ? 1 : 0.75;

		if (has_Queen && (len == 2))
			t += (has_Ace || has_King) ? 0.5 : 0.25; // Step 17 and 18

		if (has_Jack && top3 == 2) // Step 19
			t += 0.5;

		if (has_Jack && top3 == 1) // Step 20
			t += 0.25;

		if (has_Ten && top4 == 2) // Step 21
			t += 0.25;

		if (has_Ten && has_Nine && (top4 == 1)) // Step 22
			t += 0.25;

		if (len == 0) // Step 23
			t += 3;

		if (len == 1) // Step 24
			t += 2;

		if (len == 2) // Step 25
			t += 1;

		return t;
	}

	/** 
	 */
	public int countSuit(Suit suit) {
		int c = 0;
		for (Card card : this) {
			if (card.suit == suit)
				c++;
		}
		return c;
	}

	/** countLosingTricks Basic method
	 */
	public int countLosingTricks_Basic_x2() { // note the times two x 2
		int size = size();

		if (size == 0)
			return 0;

		Rank r0 = get(0).rank;
		if (size == 1) {
			return (r0 == Rank.Ace) ? 0 : 2; // remember odd numbers show half a losing trick
		}

		Rank r1 = get(1).rank;
		if (size == 2) {
			if (r0 == Rank.Ace) {
				if (r1 == Rank.King)
					return 0;

				if (r1 == Rank.Queen)
					return 2; // was once 1 // remember odd numbers show half a losing trick

				return 2;
			}

			if (r0 == Rank.King) {
				return 2;
			}

			return 4;
		}

		// size >= 3
		Rank r2 = get(2).rank;
		if (r0 == Rank.Ace) {
			if (r1 == Rank.King)
				return (r2 == Rank.Queen) ? 0 : 2;

			if (r1 == Rank.Queen)
				return 2;

			return 4;
		}

		if (r0 == Rank.King) {
			if (r1 == Rank.Queen)
				return 2;

			return 4;
		}

		if (r0 == Rank.Queen) {
			return 4;
		}

		return 6;
	}

	/** countLosingTricks with Refinements
	 */
	public int countLosingTricks_Ref_x2() { // note the times two x 2
		int size = size();

		if (size == 0)
			return 0;

		Rank r0 = get(0).rank;
		if (size == 1) {
			return (r0 == Rank.Ace) ? 0 : 2; // remember odd numbers show half a losing trick
		}

		Rank r1 = get(1).rank;

		if (size == 2) {
			if (r0 == Rank.Ace) {
				if (r1 == Rank.King)
					return 0;

				if (r1 == Rank.Queen)
					return 0; // was 1 remember odd numbers show half a losing trick

				return 2;
			}

			if (r0 == Rank.King) {
				if (r1 == Rank.Queen)
					return 2;
				return 3; // yes Kx is 3 half losers
			}

			return 4;
		}

		// size >= 3
		Rank r2 = get(2).rank;

		if (r0 == Rank.Ace) {
			if (r1 == Rank.King)
				return (r2 == Rank.Queen) ? 0 : 2;

			if (r1 == Rank.Queen)
				return 2;

			if (r1 == Rank.Jack && r2 == Rank.Ten)
				return 2;

			return 4;
		}

		if (r0 == Rank.King) {
			if (r1 == Rank.Queen)
				return 2;

			if (r1 == Rank.Jack && r2 == Rank.Ten)
				return 3;

			return 4;
		}

		if (r0 == Rank.Queen) {
			if (r1 == Rank.Jack && r2 == Rank.Ten)
				return 4;
			return 5;
		}

		return 6;
	}

}
