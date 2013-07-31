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

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * 
 */
public class Cal extends ArrayList<Card> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3704044939801788494L;

	Cal() { /* Constructor */
		super();
	}

	Cal(int n) { /* Constructor */
		super(n);
	}

	public Card getIfRankExists(int rankV) {
		for (Card card : this) {
			if (card.rank == rankV)
				return card;
		}
		return null;
	}

	public Card getIfRankAndSuitExists(int rankV, int suitV) {
		for (Card card : this) {
			if (card.rank == rankV && card.suit == suitV)
				return card;
		}
		return null;
	}

	public Card getIfRelExist(int rankRel) {
		for (Card card : this) {
			if (card.rankRel == rankRel)
				return card;
		}
		return null;
	}

	public Card getIfEquExistsHigh(int rankEqu) {
		for (Card card : this) {
			if (card.rankEqu == rankEqu)
				return card;
		}
		return null;
	}

	public Card getIfEquExistsLow(int rankEqu) {
		for (int i = size() - 1; i >= 0; i--) {
			Card card = this.get(i);
			if (card.rankEqu == rankEqu)
				return card;
		}
		return null;
	}

	public Card getIfEquExists(boolean high, int rankEqu) {
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
			sb.append(c.getRankCh());
			sb.append(' ');
		}
		return sb.toString();
	}

	/** 
	 */
	public String toScrnStr() {
		final StringBuilder sb = new StringBuilder();

		for (Card c : this) {
			sb.append(c.getRankCh());
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
				if (get(i).rank < card.rank) {
					add(i, card);
					return;
				}
			}
		}
		add(card);
	}

	/** 
	 */
	public int countPoints() {
		int v = 0;
		for (Card card : this) {
			v += (card.rank > 10) ? card.rank - 10 : 0;
		}
		return v;
	}

	/** countLosingTricks
	 */
	public int countLosingTricks_x2() { // note the times two x 2
		int size = size();

		if (size == 0)
			return 0;

		int r0 = get(0).rank;
		if (size == 1) {
			if (r0 == Zzz.Ace)
				return 0;
			return 2;
		}

		int r1 = get(1).rank;

		if (size == 2) {
			if (r0 == Zzz.Ace) {
				if (r1 == Zzz.King)
					return 0;

				if (r1 == Zzz.Queen)
					return 1; // remember odd numbers show half a losing trick

				return 2;
			}

			if (r0 == Zzz.King) {
				if (r1 == Zzz.Queen)
					return 2;
				return 3; // yes Kx is 3 half losers
			}

			return 4;
		}

		// size >= 3
		int r2 = get(2).rank;

		if (r0 == Zzz.Ace) {
			if (r1 == Zzz.King)
				return (r2 == Zzz.Queen) ? 0 : 2;

			if (r1 == Zzz.Queen)
				return 2;

			if (r1 == Zzz.Jack && r2 == Zzz.Ten)
				return 2;

			return 4;
		}

		if (r0 == Zzz.King) {
			if (r1 == Zzz.Queen)
				return 2;

			if (r1 == Zzz.Jack && r2 == Zzz.Ten)
				return 3;

			return 4;
		}

		if (r0 == Zzz.Queen) {
			if (r1 == Zzz.Jack && r2 == Zzz.Ten)
				return 4;
			return 5;
		}

		return 6;
	}

	/** 
	 */
	public int countSuit(int suitV) {
		int c = 0;
		for (Card card : this) {
			if (card.suit == suitV)
				c++;
		}
		return c;
	}
}
