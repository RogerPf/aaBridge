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

public class Play_4th {

	static Card act(Hand h, PlayGen g) {
		// ****************************** 4th 4th 4th ******************************	
		Card card = null;
	
		if (g.haveLedSuit) {
			// we have some of the led suit
			if (g.areWeWinning) {
				card = g.lowestOfLed;
			}
			else { // we are losing
				card = h.frags[g.suitValueLed].getLowestThatBeatsOrLowest(g.bestCard.faceValue);
				if (card.faceValue < g.bestCard.faceValue && (h.axis() == Zzz.EW) && App.nsFinessesMostlyFail) {
					if (g.partner.frags[g.suitValueLed].size() > 0) {
						Card pCard = g.partner.frags[g.suitValueLed].getLowestThatBeatsOrLowest(g.bestCard.faceValue);
						if (pCard.faceValue > g.bestCard.faceValue) {
							h.deal.moveCardToHandDuringPlay(pCard, h, card);
							//App.gbp.dealMajorChange();
							card = pCard;
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
						card = g.lowestTrump; // ruff low
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
