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

public class Rlay_4th {

	static Card act(Gather g) {
		Hand h = g.hand;
		// ****************************** 4th 4th 4th Defender ******************************
		Card card = null;
		boolean card_swaped_with_partner = false;

		int brk = 0;
		if (g.trickNumb == 11)
			if (g.compass.v == 1)
				brk++; // put your breakpoint here :)

		if (brk > 0)
			brk++; // put your breakpoint here :)

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
					if (card.rank.v < g.bestCard.rank.v && (h.axis() == Dir.EW) && g.dumbAutoDir.yourFinessesMostlyFail) {
						if (g.pnFragLed.size() > 0) {
							Card pCard = g.pnFragLed.getLowestThatBeatsOrLowest(g.z, g.bestCard.rank);
							if (pCard.rank.v > g.bestCard.rank.v) {
								h.deal.moveCardToHandDuringPlay(pCard, h, card);
								card_swaped_with_partner = true;
								card = pCard;
							}
						}
					}
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

		if (card_swaped_with_partner == false) {
			card = Rlay_5_Discard.ChangeIntoSignalIfAppropriate(g, card);
		}

		return card;
	}
}
