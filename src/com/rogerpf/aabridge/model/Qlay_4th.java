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

public class Qlay_4th {

	static Card act(Gather g) {
		Hand h = g.hand;
		// ****************************** 4th 4th 4th Declarer and Dummy (index 3) ******************************
		Card card = null;

		if (g.haveSuitLed) {
			// we have some of the led suit
			if (g.areWeWinning) {
				if (h.getStrategy().containsGetToHand(h.compass)) {
					card = g.fragLed.getLowestThatBeats(g.z, g.bestCard.rank);
				}
				if (card == null) {
					card = g.lowestOfLed;
				}
			}
			else { // we are losing
				if (g.bestByTrumping) { // nothing we can do
					card = g.lowestOfLed;
				}
				else {
					card = g.fragLed.getLowestThatBeatsOrLowest(g.z, g.bestCard.rank);
				}
			}
		}
		else { // we have NONE of the led suit
			if (g.areWeWinning) {
				card = h.pickBestDiscard(g);
			}
			else {
				// we are NOT winning - 4th to play - none of led suit
				if (g.noTrumps) {
					card = h.pickBestDiscard(g);
				}
				else { //
					if (g.haveTrumps) {
						if (g.bestCard.suit != g.trumpSuit) {
							card = g.lowestTrump; // ruff low
						}
						else {
							card = h.frags[g.trumpSuit.v].getLowestThatBeats(g.z, g.bestCard.rank);
							if (card == null) {
								card = h.pickBestDiscard(g);
							}
						}
					}
					else {
						card = h.pickBestDiscard(g);
					}
				}
			}
		}
		return card;
	}
}
