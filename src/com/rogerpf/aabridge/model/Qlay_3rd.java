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

public class Qlay_3rd {

	static Card act(Gather g) {
		Hand h = g.hand;
		// ****************************** 3rd 3rd 3rd Declarer and Dummy (index 2) ******************************
		Card card = null;

		int brk = 0;
		if (g.trickNumb == 6)
			if (g.compass == 0)
				brk++; // put your breakpoint here :)

		for (StraStep step : h.getStrategy()) {
			if (step.idea == Strategy.PlayCard) {
				// --------------------------------------------------
//				assert (step.suit == g.suitLed);
				card = h.frags[step.suit].getIfRelExist(step.rankRel);
				if (card == null) {
					@SuppressWarnings("unused")
					int x = 0; // put your breakpoint here
				}
				assert (card != null);
				return card;
			}

			if (step.idea == Strategy.GetToHand) {
				// --------------------------------------------------
				if (step.compass == g.compass) {
					if (g.faLed.myFragLen > 0) {
						card = g.fragLed.getHighest(g.z);
						return card;
					}
					if (g.haveTrumps && !g.bestByTrumping) {
						card = g.lowestTrump;
						return card;
					}
				}
			}

		}

		if (brk > 0)
			brk++; // put your breakpoint here :)

		if (g.haveSuitLed) {
			// we have some of the led suit so

//			if (card == null) {
//				card = Play_Mpat.cardByPatternMatch(g, g.suitLed, Zzz.Third_Pos);
//			}

			if (card == null) {
				if (h.getStrategy().containsRunTopTricksInSuit())
					if (g.cardLed.rankEqu < g.highestOfLed.rankEqu)
						card = g.highestOfLed;
					else
						card = g.lowestOfLed;
			}

			if (card == null) {
				if (g.bestByTrumping) { // nothing we can do
					card = g.lowestOfLed;
				}
				else if (g.bestCard.rank > g.highestOfLed.rank) { // We can't beat what has been played
					card = g.lowestOfLed;
				}
			}

			if (card == null) {
				if (h.getStrategy().containsGetToHand(h.compass)) {
					card = g.fragLed.getLowestThatBeats(g.z, g.bestCard.rank);
				}
			}

			if (card == null) {
				if (g.suitLed == g.trumpSuit) {
					// so we are drawing trumps and have NOT pattern matched
					if (g.areWeWinning) {
						if (g.bestCard.rankEqu >= g.highestOfLed.rankEqu)
							card = g.lowestOfLed;
						else
							card = g.highestOfLed;
					}
					else {
						if (g.highestOfLed.rank > g.bestCard.rank) {
							card = g.highestOfLed;
						}
						else {
							card = g.lowestOfLed;
						}
					}
				}
			}

			if (card == null) {

				if (g.areWeWinning) {
					// TODO: rd 3rd 3rd Declarer and Dummy - WHEN currently winning
					// we need to estimate what LHO may hold - but the pattern matching should do a lot to help
					// or do we CHEAT no no no
					// card = ...
					if (g.bestCard.rankEqu != Zzz.Ace) { // i.e. We might loose
						FragAnal fa = g.faLed;
						if (fa.ourTopTricksCor >= fa.oppsCombLen)
							card = g.highestOfLed;
						else {
							// errr TODO: thid hand decision
						}
					}
				}
				else { // we are NOT winning this trick
						// cover if we can - yes it migh be trumped
					card = g.fragLed.getLowestThatBeats(g.z, g.bestCard.rank);
				}

			}
			if (card == null) {
				card = g.lowestOfLed;
				if (card.rank > g.bestCard.rank)
					card = g.highContLowestOfLed; // lowest but high contig - as we are declarer axis
			}

		}
		else { // we have NONE of the led suit

			if (g.noTrumps) {

				card = h.pickBestDiscard(g);

			}
			else if (g.areWeWinning) { // YES

				card = h.pickBestDiscard(g);

			}
			else { // No we are loosing this trick

				if (g.haveTrumps) {
					if (g.bestByTrumping) {
						card = h.frags[g.trumpSuit].getLowestThatBeats(false, g.bestCard.rank);
					}
					else {
						card = g.lowestTrump; // ruff low
					}
				}
			}

			if (card == null) {
				card = h.pickBestDiscard(g);
			}

		}
		return card;
	}
}
