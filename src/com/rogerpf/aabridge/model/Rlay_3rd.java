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

public class Rlay_3rd {

	static Card act(Gather g) {
		Hand h = g.hand;
		// ****************************** 3rd 3rd 3rd Defender ******************************
		Card card = null;

		if (g.haveSuitLed) {
			// we have some of the led suit so

			if (g.bestByTrumping) { // nothing we can do
				card = g.lowestOfLed;
			}
			else if (g.areWeWinning) {
				if (g.bestCard.rank > g.highestOfLed.rank) {
					card = g.lowestOfLed;
				}
				else if (g.LHO_hasLedSuit == false) { // the LHO does not have this suit
					card = g.lowestOfLed;
				}
				else { // the LHO has this suit
					if (g.LHO.frags[g.suitLed].getLowestThatBeats(g.z, g.bestCard.rank) == null) {
						// No - LHO cannot beat partners winning cards
						card = g.lowestOfLed;
					}
					else { // yes LHO I can beat partners card
						/**
						 * Should I try to force a higher card to be played ?
						 * What is the highest card in the LHO (4th Hand) that partners lead
						 * can currenlty beat?
						 * And Can I do better ?
						 */
						Card LHO_plt = g.LHO.frags[g.suitLed].getHighestThatLosesTo(g.bestCard.rank);
						Card LHO_mlt = g.LHO.frags[g.suitLed].getHighestThatLosesTo(g.fragLed.get(0).rank);

						if (LHO_mlt == LHO_plt) { // our best can do no better
							card = g.lowestOfLed;
						}
						else {
							card = g.fragLed.getLowestThatBeats(g.z, LHO_mlt.rank);
						}
					}
				}
			}

			else { // we are NOT winning this trick

				// cover if we can - yes it migh be trumped
				Card posib = g.fragLed.getLowestThatBeats(g.z, g.bestCard.rank);
				if (posib == null) {
					card = g.lowestOfLed;
				}

				// We have a candidate that covers the current winner in - posib

				else if (g.LHO_hasLedSuit == false) { // the LHO does not have this suit
					card = posib;
				}
				// the LHO has this suit
				else {
					Card LHO_posib_beater = g.LHO.frags[g.suitLed].getLowestThatBeats(g.z, posib.rank);
					if (LHO_posib_beater == null) {
						// No - LHO cannot beat our posibs card
						card = posib;
					}
					else {
						// LHO can beat our current 'posib' - can we do better
						Card LHO_mlt = g.LHO.frags[g.suitLed].getHighestThatLosesTo(g.fragLed.get(0).rank);
						if (LHO_mlt == null) {
							card = posib;
						}
						else if (LHO_mlt.rank < posib.rank) {
							card = posib;
						}
						else {
							card = g.fragLed.getLowestThatBeats(g.z, LHO_mlt.rank);
							if (card == null) {
								card = posib;
							}
						}
					}
				}
			}
		}
		else { // we have NONE of the led suit
			if (g.noTrumps) {
				card = h.pickBestDiscard(g);
			}
			else if (g.areWeWinning) { // YES

				if (g.haveTrumps) {
					if (g.LHO_hasLedSuit) {
						// will we still be winning after LHO plays
						if (g.LHO.frags[g.suitLed].getLowestThatBeats(g.z, g.bestCard.rank) == null) {
							// No - LHO cannot beat partners winning cards
							card = h.pickBestDiscard(g);
						}
						else {
							// we need to ruff partners bestsofar card
							card = g.lowestTrump; // ruff low
						}
					}
					else {
						card = h.pickBestDiscard(g);
						// TO DO ruff high to out ruff LHO ?
					}

				}
				else {
					card = h.pickBestDiscard(g);
				}

			}
			else { // No we are loosing this trick

				if (g.haveTrumps) {
					if (g.bestByTrumping) {
						card = h.frags[g.trumpSuit].getLowestThatBeats(g.z, g.bestCard.rank);
						if (card == null) {
							card = h.pickBestDiscard(g);
						}
					}
					else {
						if (g.LHO_hasLedSuit) {
							card = g.lowestTrump; // ruff low
						}
						else {
							card = g.lowestTrump; // ruff low
							// TOD O ruff high to out ruff LHO ?
						}
					}
				}
				else {
					card = h.pickBestDiscard(g); // lowest of current longest suit
				}
			}
		}

		card = Rlay_5_Discard.ChangeIntoSignalIfAppropriate(g, card);

		return card;
	}
}
