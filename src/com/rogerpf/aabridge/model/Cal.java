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

	public Card getIfRankAndSuitExists(Rank rank, Suit suit) {
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
	public String toScrnStr() {
		final StringBuilder sb = new StringBuilder();

		for (Card c : this) {
			sb.append(c.rank.toChar());
		}

		return sb.toString();
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
	public int countHighCardPoints() {
		int v = 0;
		for (Card card : this) {
			v += (card.rank.v > Rank.Ten.v) ? card.rank.v - Rank.Ten.v : 0;
		}
		return v;
	}

	/** 
	 */
	public int countLongSuitPoints() {
		return (size() < 5) ? 0 : size() - 4;
	}

	/** 
	 */
	public int countShortSuitPoints() {
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

	/** countLosingTricks
	 */
	public int countLosingTricks_x2() { // note the times two x 2
		int size = size();

		if (size == 0)
			return 0;

		Rank r0 = get(0).rank;
		if (size == 1) {
			if (r0 == Rank.Ace)
				return 0;
			return 2;
		}

		Rank r1 = get(1).rank;

		if (size == 2) {
			if (r0 == Rank.Ace) {
				if (r1 == Rank.King)
					return 0;

				if (r1 == Rank.Queen)
					return 1; // remember odd numbers show half a losing trick

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

}
