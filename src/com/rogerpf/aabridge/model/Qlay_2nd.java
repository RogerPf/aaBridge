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

public class Qlay_2nd {

	static Card act(Gather g) {
		Hand h = g.hand;
		// ****************************** 2nd 2nd 2nd Declarer and Dummy (index 1) ******************************
		Card card = null;

		int brk = 0;
		if (g.trickNumb == 2)
			if (g.compass == 2)
				brk++; // put your breakpoint here :)

		if (brk > 0)
			brk++; // put your breakpoint here :)

		if (g.haveSuitLed) { // we have some of the led suit

			if (g.bestCard.rank > g.highestOfLed.rank) {
				card = g.lowestOfLed; // nothing we can do
			}

			if (card == null) {
				if (g.bestCard.rankEqu == Zzz.King && g.highestOfLed.rankEqu == Zzz.Ace) {
					if (g.pnHighestOfLedRank != Zzz.Ace)
						card = g.highestOfLed;
					else {
						// TODO: MORE (non pattern) 2nd player anal
					}
				}
			}

			// We are trying to find reasons NOT to, use the lowestOfLed

			// if (we know LHO has NONE) {
			// TODO: decl 2nd and we know LHO has none
			// }

			if (card == null) {
				if (h.getStrategy().containsGetToHand(h.compass)) {
					card = g.highestOfLed;
				}
			}

			if (card == null) {
				card = Play_Mpat.cardByPatternMatch(g, g.suitLed, Zzz.Second_Pos);
			}

			if (card == null) {
				Card ptnr_card = g.pnFragLed.getLowestThatBeats(g.z, g.bestCard.rank);
				if (ptnr_card != null) {
					// can partner beat the current lead

					card = g.lowestOfLed; // we will play low and let partner do her stuff
				}
			}

			if (card == null) {
				card = g.lowestOfLed;
			}

		}

		else { // we have NONE of the led suit
			if (g.noTrumps) {
				card = h.pickBestDiscard(g);
				; // lowest of current longest suit
			}
			else { //
				if (g.haveTrumps) {
					if (h.doesPartnerHaveMaster(g.suitLed)) {
						card = h.pickBestDiscard(g);
					}
					else {
						card = g.lowestTrump; // ruff low
					}
				}
				else {
					card = h.pickBestDiscard(g);
					; // lowest of current longest suit
				}
			}
		}
		return card;
	}

}
