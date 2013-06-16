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
import java.util.Comparator;

/**
 * 
 * Frag...ment of a Suit
 */
public class Frag extends Cal implements Serializable, Comparable<Frag> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2945787433797684005L;

	public final Hand hand;
	public final int suitValue;
	transient char suitCh;

	Frag(Hand handV, int suitV) { /* Constructor */
		super();
		hand = handV;
		suitValue = suitV;
		suitCh = Zzz.suitValue_to_cdhsnCh[suitV];
	}

	public int getSuitValue() {
		return suitValue;
	}

	public char getSuitCh() {
		return Zzz.suitValue_to_cdhsnCh[suitValue];
	}

	public String getSuitSt() {
		return Zzz.suitValue_to_cdhsnSt[suitValue];
	}

	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(' ');
		sb.append(getSuitCh());
		sb.append(" - ");

		return sb.toString() + super.toString();
	}

	public int compareTo(Frag other) {
		assert (false); // NOT USED (but required) - instead we use fuction comps
		return 0;
	}

	boolean areTopTwoContigious() {
		if (size() < 2)
			return false;
		return (get(0).faceRel == get(1).faceRel + 1);
	}

	public int countContigious() {
		if (hand.frags[suitValue].size() == 0)
			return 0;
		int prev = get(0).faceRel + 1;
		int v = 0;
		for (Card card : this) {
			if (prev > card.faceRel + 1)
				break;
			v++;
			prev = card.faceRel;
		}

		return v;
	}

	int gen_ContigiousnessTrumpsLast() {
		if (hand.frags[suitValue].size() == 0)
			return -1;

		if (hand.deal.isTrumps(suitValue))
			return 0;

		int prev = get(0).faceRel + 1;
		int v = 0;
		for (Card card : this) {
			if (prev > card.faceRel + 1)
				break;
			v++;
			prev = card.faceRel;
		}

		int sizOrg = hand.fOrgs[suitValue].size();
		int sizOrgPn = hand.partner().fOrgs[suitValue].size();

		return v * 4 + sizOrg + sizOrgPn;
	}

	public static Comparator<Frag> ContigiousnessTrumpsLast = new Comparator<Frag>() {
		public int compare(Frag f1, Frag f2) {
			// f2 - f1 is decending because we want the BEST at the low end of the array
			return f2.gen_ContigiousnessTrumpsLast() - f1.gen_ContigiousnessTrumpsLast();
		}
	};

	int gen_CombinedOrigThenCurLenTrumpsLast() {
		int sizeF = hand.frags[suitValue].size();
		int any = (sizeF == 0 ? 0 : 1);
		int sizOrg = hand.fOrgs[suitValue].size();
		int sizOrgPn = hand.partner().fOrgs[suitValue].size();

		if (hand.deal.isTrumps(suitValue)) {
			sizOrg = 0;
			sizOrgPn = 0;
			sizeF = 0;
		} // trumps last
		return (100 * (sizOrg + sizOrgPn) + sizeF * 10 + suitValue) * any;
	}

	public static Comparator<Frag> CombinedOrigThenCurLenTrumpsLast = new Comparator<Frag>() {
		public int compare(Frag f1, Frag f2) {
			// f2 - f1 is decending because we want the BEST at the low end of the array
			return f2.gen_CombinedOrigThenCurLenTrumpsLast() - f1.gen_CombinedOrigThenCurLenTrumpsLast();
		}
	};

	int gen_OrigThenCurLenTrumpsLast() {
		int sizeF = hand.frags[suitValue].size();
		int any = (sizeF == 0 ? 0 : 1);
		int sizOrg = hand.fOrgs[suitValue].size();
		if (hand.deal.isTrumps(suitValue)) {
			sizOrg = 0;
			sizeF = 0;
		} // trumps last
		return (100 * sizOrg + sizeF * 10 + suitValue) * any;
	}

	public static Comparator<Frag> OrigThenCurLenTrumpsLast = new Comparator<Frag>() {
		public int compare(Frag f1, Frag f2) {
			// f2 - f1 is decending because we want the BEST at the low end of the array
			return f2.gen_OrigThenCurLenTrumpsLast() - f1.gen_OrigThenCurLenTrumpsLast();
		}
	};

	int gen_curLenTrumpsLast() {
		int sizeF = hand.frags[suitValue].size();
		int any = (sizeF == 0 ? 0 : 1);
		if (hand.deal.isTrumps(suitValue)) {
			sizeF = 0;
		} // trumps last
		return (sizeF * 10 + suitValue) * any;
	}

	public static Comparator<Frag> CurLenTrumpsLast = new Comparator<Frag>() {
		public int compare(Frag f1, Frag f2) {
			// f2 - f1 is decending because we want the BEST at the low end of the array
			return f2.gen_curLenTrumpsLast() - f1.gen_curLenTrumpsLast();
		}
	};

	public Card getLowestThatBeatsOrLowest(int faceToBeat) {
		for (int i = size() - 1; i >= 0; i--) {
			if (get(i).faceValue > faceToBeat)
				return get(i);
		}
		return getLast();
	}

	public Card getLowestThatBeats(int faceToBeat) {
		for (int i = size() - 1; i >= 0; i--) {
			if (get(i).faceValue > faceToBeat)
				return get(i);
		}
		return null;
	}

}
