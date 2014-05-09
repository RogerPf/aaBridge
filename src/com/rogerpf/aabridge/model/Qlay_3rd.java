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
			if (g.compass.v == 2)
				brk++; // put your breakpoint here :)

		if (brk > 0)
			brk++; // put your breakpoint here :)

		if (g.haveSuitLed) {
			if (card == null) {
				if (g.bestCard.rank.v > g.highestOfLed.rank.v) {
					card = g.lowestOfLed; // nothing we can do
				}
			}
		}

		if (card == null) {
			for (StraStep step : h.getStrategy()) {
				if (step.idea == Strategy.PlayCard) {
					// --------------------------------------------------
					assert (step.suit == g.suitLed);
					card = h.frags[step.suit.v].getIfRelExist(step.rankRel);
					if (card == null) {
						brk++; // put your breakpoint here :)
					}
					assert (card != null);
					break;
				}

				if (step.idea == Strategy.GetToHand) {
					// --------------------------------------------------
					if (step.compass == g.compass) {
						if (g.bestByTrumping == false && g.highestOfLed != null && g.highestOfLed.rank.v > g.bestCard.rank.v) {
							card = g.fragLed.getHighest(g.z);
							break;
						}

						if (!g.haveSuitLed && g.haveTrumps && !g.bestByTrumping) {
							card = g.lowestTrump;
							break;
						}
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
					if (g.cardLed.rankEqu.v < g.highestOfLed.rankEqu.v)
						card = g.highestOfLed;
					else
						card = g.lowestOfLed;
			}

			if (card == null) {
				if (g.bestByTrumping) { // nothing we can do
					card = g.lowestOfLed;
				}
				else if (g.bestCard.rank.v > g.highestOfLed.rank.v) { // We can't beat what has been played
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
						if (g.bestCard.rankEqu.v >= g.highestOfLed.rankEqu.v)
							card = g.lowestOfLed;
						else
							card = g.highestOfLed;
					}
					else {
						if (g.highestOfLed.rank.v > g.bestCard.rank.v) {
							card = g.highestOfLed;
						}
						else {
							card = g.lowestOfLed;
						}
					}
				}
			}

			if (card == null) {
				if (g.cardLed.rankEqu.v < Rank.Ace.v && g.faLed.ourTopTricksCor >= g.faLed.myFragLen) {
					card = g.fragLed.getLowestThatBeatsEqu(g.z, Rank.King);
				}
			}

			if (card == null) {
				if (g.areWeWinning) {
					// TO DO: rd 3rd 3rd Declarer and Dummy - WHEN currently winning
					// we need to estimate what LHO may hold - but the pattern matching should do a lot to help
					// or do we CHEAT no no no
					// card = ...
					if (g.bestCard.rankEqu != Rank.Ace) { // i.e. We might loose
						FragAnal fa = g.faLed;
						if (fa.ourTopTricksCor >= fa.oppsCombLen)
							card = g.highestOfLed;
						else {
							// errr TO DO: thid hand decision
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
				if (card.rank.v > g.bestCard.rank.v)
					card = g.highContLowestOfLed; // lowest but high contig - as we are declarer axis
			}

		}
		else { // we have NONE of the led suit

			if (card == null) {
				if (g.noTrumps) {
					card = h.pickBestDiscard(g);
				}
			}

			if (card == null) {
				if (g.areWeWinning) { // YES
					if (g.cardLed.rankEqu == Rank.Ace)
						card = h.pickBestDiscard(g);
				}
			}

			if (card == null) {
				if (g.haveTrumps) {
					// so we will ALWAYS ruff partners lead
					// Need to add patterns for ruffing finness if you want it otherwise
					if (g.bestByTrumping) {
						card = h.frags[g.trumpSuit.v].getLowestThatBeats(false, g.bestCard.rank);
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
