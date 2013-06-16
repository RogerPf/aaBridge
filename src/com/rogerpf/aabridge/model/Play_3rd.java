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

public class Play_3rd {

	static Card act(Hand h, PlayGen g) {
		// ****************************** 3rd 3rd 3rd ******************************
		Card card = null;

		if (g.haveLedSuit) {
			// we have some of the led suit so

			if (g.bestByTrumping) { // nothing we can do
				card = g.lowestOfLed;
			}
			else if (g.areWeWinning) {
				if (g.bestCard.faceValue > g.highestOfLed.faceValue) {
					card = g.lowestOfLed;
				}
				// CHEAT unless LHO is dummy CHEAT CHEAT CHEAT CHEAT CHEAT
				else if (g.LHO_haveLedSuit == false) { // the LHO does not have this suit
					card = g.lowestOfLed;
				}
				else { // the LHO has this suit
					if (g.LHO.frags[g.suitValueLed].getLowestThatBeats(g.bestCard.faceValue) == null) {
						// No - LHO cannot beat partners winning cards
						card = g.lowestOfLed;
					}
					else {
						int LHO_best = g.LHO.frags[g.suitValueLed].getCard(0).faceValue;
						card = h.frags[g.suitValueLed].getLowestThatBeats(LHO_best);
						if (card == null && g.LHO.frags[g.suitValueLed].size() > 1) {

							int LHO_2ndBest = g.LHO.frags[g.suitValueLed].getCard(1).faceValue;
							card = h.frags[g.suitValueLed].getLowestThatBeats(LHO_2ndBest);
						}
						if (card == null && g.LHO.frags[g.suitValueLed].size() > 2) {
							int LHO_3rdBest = g.LHO.frags[g.suitValueLed].getCard(2).faceValue;
							card = h.frags[g.suitValueLed].getLowestThatBeats(LHO_3rdBest);
						}
						if (card == null) {
							card = g.lowestOfLed;
						}
						// more DEEP STUDY here :)
					}
				}
			}

			else { // we are NOT winning this trick

				// cover if we can - yes it migh be trumped
				Card pos = h.frags[g.suitValueLed].getLowestThatBeats(g.bestCard.faceValue);
				if (pos == null) {
					card = g.lowestOfLed;
				}

				// Note - We have a candidate that covers the winner in - poss

				else if (g.LHO_haveLedSuit == false) { // the LHO does not have this suit
					card = pos;
				}
				// the LHO has this suit
				else if (g.LHO.frags[g.suitValueLed].getLowestThatBeats(pos.faceValue) == null) {
					// No - LHO cannot beat our poss card
					card = pos;
				}
				else {
					int LHO_best = g.LHO.frags[g.suitValueLed].getCard(0).faceValue;
					card = h.frags[g.suitValueLed].getLowestThatBeats(LHO_best);
					if (card == null) {
						card = pos; // cover the card even though we know it will be beaten
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
					if (g.LHO_haveLedSuit) {
						// will we still be winning after LHO plays
						if (g.LHO.frags[g.suitValueLed].getLowestThatBeats(g.bestCard.faceValue) == null) {
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

				if (g.haveLedSuit && !g.bestByTrumping) {
					// we have some of the led suit
					card = h.frags[g.suitValueLed].getLowestThatBeatsOrLowest(g.bestCard.faceValue);
				}
				else if (g.haveTrumps) {
					if (g.bestByTrumping) {
						card = h.frags[g.trumpSuit].getLowestThatBeats(g.bestCard.faceValue);
						if (card == null) {
							card = h.pickBestDiscard(g);
						}
					}
					else {
						if (g.LHO_haveLedSuit) {
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
		return card;
	}
}
