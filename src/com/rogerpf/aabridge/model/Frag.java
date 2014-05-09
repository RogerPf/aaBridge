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
 * Frag...ment of a Suit
 */
public class Frag extends Cal {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Hand hand;
	public Suit suit;
	public int suitVisControl;
	public transient char suitCh;

	Frag(Hand hand, Suit suit) { /* Constructor */
		super();
		this.hand = hand;
		this.suit = suit;
		suitCh = suit.toChar();
		suitVisControl = Suit.SVC_cards;
	}

	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(' ');
		sb.append(suit.toChar());
		sb.append(" - ");

		return sb.toString() + super.toString();
	}

	public String relFaceSt() {
		final StringBuilder sb = new StringBuilder();
		for (Card card : this) {
			sb.append(card.rankRel.toChar());
		}
		return sb.toString();
	}

	public String equFaceSt() {
		final StringBuilder sb = new StringBuilder();
		for (Card card : this) {
			sb.append(card.rankEqu.toChar());
		}
		return sb.toString();
	}

	boolean areTopTwoContigious() {
		if (size() < 2)
			return false;
		return (get(0).rankRel.v == get(1).rankRel.v + 1);
	}

	public boolean areAllBetterThan(Rank rank) {
		if (size() == 0)
			return false;
		return getLast().rank.v > rank.v;
	}

	public int countContigious() {
		// ==============================================================================================
		if (hand.frags[suit.v].size() == 0)
			return 0;
		int prev = get(0).rankRel.v + 1;
		int v = 0;
		for (Card card : this) {
			if (prev > card.rankRel.v + 1)
				break;
			v++;
			prev = card.rankRel.v;
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
		int t = Rank.Ace.v - startCardCountBelowAce;
		for (Card card : this) {
			if (card.rankRel.v != t--)
				break;
			d++;
		}
		return d;
	}

	boolean isMissingKing() {
		// ==============================================================================================
		return (size() > 1) && this.get(1).rankRel != Rank.King;
	}

	boolean isMissingAceQueen() {
		// ==============================================================================================
		return (size() > 2) && (this.get(0).rankRel != Rank.Ace) && (this.get(2).rankRel != Rank.Queen);
	}

	public Card getHighest(boolean contigHigh) {
		// ==============================================================================================
		if (size() == 0)
			return null;

		if (contigHigh) {
			return get(0);
		}

		Rank rankEqu = get(0).rankEqu;
		for (int k = size() - 1; k >= 0; k--) {
			if (get(k).rankEqu == rankEqu)
				return get(k);
		}
		return getLast();
	}

	public Card getLowestThatBeatsOrLowest(boolean contigHigh, Rank rankToBeat) {
		// ==============================================================================================
		for (int i = size() - 1; i >= 0; i--) {
			if (get(i).rank.v > rankToBeat.v) {
				if (!contigHigh) {
					return get(i);
				}
				else {
					Rank rankEqu = get(i).rankEqu;
					for (int k = 0; k <= i; k++) {
						if (get(k).rankEqu == rankEqu)
							return get(k);
					}
				}
			}
		}
		return getLast();
	}

	public Card getLowestThatBeatsOrLowestEqu(boolean contigHigh, Rank rankToBeat) {
		// ==============================================================================================
		for (int i = size() - 1; i >= 0; i--) {
			if (get(i).rankEqu.v > rankToBeat.v) {
				if (!contigHigh) {
					return get(i);
				}
				else {
					Rank rankEqu = get(i).rankEqu;
					for (int k = 0; k <= i; k++) {
						if (get(k).rankEqu == rankEqu)
							return get(k);
					}
				}
			}
		}
		return getLast();
	}

	public Card getLowestThatBeats(boolean contigHigh, Rank rankToBeat) {
		// ==============================================================================================
		for (int i = size() - 1; i >= 0; i--) {
			if (get(i).rank.v > rankToBeat.v) {
				if (!contigHigh) {
					return get(i);
				}
				else {
					Rank rankEqu = get(i).rankEqu;
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

	public Card getLowestThatBeatsEqu(boolean contigHigh, Rank rankToBeat) {
		// ==============================================================================================
		for (int i = size() - 1; i >= 0; i--) {
			if (get(i).rankEqu.v > rankToBeat.v) {
				if (!contigHigh) {
					return get(i);
				}
				else {
					Rank rankEqu = get(i).rankEqu;
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

	public Card getHighestThatLosesTo(Rank rankToLoseTo) {
		for (int i = 0; i < size(); i++) {
			if (get(i).rank.v < rankToLoseTo.v)
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
