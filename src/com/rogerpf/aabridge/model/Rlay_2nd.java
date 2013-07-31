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

import com.rogerpf.aabridge.controller.App;

public class Rlay_2nd {

	static Card act(Gather g) {
		Hand h = g.hand;
		// ****************************** 2nd 2nd 2nd Defender ******************************
		Card card = null;

		int brk = 0;
		if (g.trickNumb == 1)
			if (g.compass == 3)
				brk++; // put your breakpoint here :)

		if (brk > 0)
			brk++; // put your breakpoint here :)

		if (g.haveSuitLed) { // we have some of the led suit

			if (card == null && h.getStrategy().containsGetToHand(h.compass)) {
				Card possib = g.fragLed.getLowestThatBeats(g.z, g.bestCard.rank);
				if (possib != null) {
					Card LHO_card = g.LHO.frags[g.suitLed].getLowestThatBeats(g.z, possib.rank);
					if (LHO_card != null) {
						possib = g.fragLed.getLowestThatBeats(g.z, LHO_card.rank);
					}
					if (possib != null) {
						Card ptnr_card = g.pnFragLed.getLowestThatBeats(g.z, possib.rank);
						if (ptnr_card != null) {
							card = g.fragLed.getLowestThatBeats(g.z, ptnr_card.rank);
						}
					}
				}
			}

			if (card == null && (g.bestCard.rankEqu == Zzz.King) && (g.highestOfLed.rankEqu == Zzz.Ace)) {
				card = g.highestOfLed;
			}

			if (card == null) {
				card = Play_Mpat.cardByPatternMatch(g, g.suitLed, Zzz.Second_Pos);
			}

			if (card == null && g.LHO_hasLedSuit) {

				// are they trying to sneak a cheaky trick past me
				// test_1119
				if (card == null) {
					int my_cc = g.fragLed.contigCards(0);
					if (my_cc > 0) { // this implies I have the 1 or more "ACES" in this suit
						if (g.cardLed.rankEqu == Zzz.Ace - my_cc) {
							card = g.fragLed.getLowestThatBeats(g.z, g.LHO.frags[g.suitLed].get(0).rank);
						}
						if (card == null) {
							int LHO_rd = g.LHO.frags[g.suitLed].contigCards(my_cc);
							if (LHO_rd > 0) {
								if (g.LHO.frags[g.suitLed].size() == LHO_rd || (my_cc > 1)) {
									// they are def trying to sneak a trick through
									card = g.fragLed.getLowestThatBeats(g.z, g.LHO.frags[g.suitLed].get(0).rank);
								}
							}
						}
					}
				}

				if (card == null) {
					if (App.yourFinnessesMostlyFail && g.pnFragLed.size() > 0) {
						// if we are in a finessing situaton we can duck this one
						Card myBest = g.fragLed.get(0);
						if ((myBest.rank > g.bestCard.rank) && (myBest.rank < g.LHO.frags[g.suitLed].get(0).rank))
							// If LHO tries a finesse it will fail
							card = g.lowestOfLed;
					}
				}

				// From now on we are trying to find reasons to not just use the lowestOfLed
				// and there are not very many good ones (reasons)

				if (card == null) {
					Card ptnr_card = g.pnFragLed.getLowestThatBeats(g.z, g.bestCard.rank);
					if (ptnr_card != null) {
						// can partner beat the current lead

						card = g.lowestOfLed; // we will play low and let partner do their stuff
					}
				}

				if (card == null) {
					card = g.lowestOfLed;
				}
			}

			else { // LHO has none of this suit

				if (card == null && (g.noTrumps || g.LHO_hasTrumps == false)) {

					if (card == null) {
						Card ptnr_card = g.pnFragLed.getLowestThatBeats(g.z, g.bestCard.rank);
						if (ptnr_card != null) {
							// can partner beat the current lead
							card = g.lowestOfLed; // we will play low and let partner do their stuff
						}
						else {
							card = g.fragLed.getLowestThatBeats(g.z, g.bestCard.rank);
						}
					}
				}

				if (card == null) {
					card = g.lowestOfLed;
				}
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

		// one last check - we are in 2nd pos
		assert (card != null);
		if (g.bestCard.suit == card.suit && g.bestCard.rankRel == card.rankRel + 1) {
			Card card2 = g.fragLed.getIfEquExistsLow(g.bestCard.rankRel + 1);
			if (card2 != null)
				card = card2;
		}

		return card;
	}

}
