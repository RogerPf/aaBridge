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
 * Card
 */
public class Card implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1326940816839081697L;
	int rank;
	transient int rankRel;
	transient int rankEqu;
	int suit;
	transient char suitCh;

	Card(int rank, int suit) { /* Constructor */
		this.rank = rank;
		rankRel = 0;
		rankEqu = 0;
		this.suit = suit;
		suitCh = Zzz.suit_to_cdhsnCh[suit];
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		ObjectInputStream.GetField fields = in.readFields(); // magic

		// field renamed on 2013-July-09
		if (fields.defaulted("rank")) {
			rank = fields.get("faceValue", (int) 0xdeadbeef);
		}
		else {
			rank = fields.get("rank", (int) 0xdeadbeef);
		}

		// field renamed on 2013-July-09
		if (fields.defaulted("suit")) {
			suit = fields.get("suitValue", (int) 0xdeadbeef);
		}
		else {
			suit = fields.get("suit", (int) 0xdeadbeef);
		}
	}

	// --------------------------------
	public int getRank() {
		return rank;
	}

	public String getRankSt() {
		return Zzz.rank_to_rankSt[rank];
	}

	public char getRankCh() {
		return Zzz.rank_to_rankCh[rank];
	}

	public char getRankRelCh() {
		return Zzz.rank_to_rankCh[rankRel];
	}

	public char getRankEquCh() {
		return Zzz.rank_to_rankCh[rankEqu];
	}

	// --------------------------------
	public int getSuit() {
		return suit;
	}

	public char getSuitCh() {
		return Zzz.suit_to_cdhsnCh[suit];
	}

	public String getSuitSt() {
		return Zzz.suit_to_cdhsntSt[suit];
	}

	public String toString() {
		return getRankSt() + getSuitSt();
	}

	public boolean matches(int rank, int suit) {
		return (this.rank == rank && this.suit == suit);
	}

	public boolean isBetterThan(Card bestSoFar, int suitTrumps) {
		//@formatter:off
		return  ((suit == bestSoFar.suit) && (rank > bestSoFar.rank))
			 || ((suit != bestSoFar.suit) && (suit == suitTrumps)); 
		//@formatter:on
	}

	public boolean isMaster() {
		return rankRel == Zzz.Ace;
	}
}
