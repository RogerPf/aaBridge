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

public class Play_2nd {

	static Card act(Hand h, PlayGen g) {
		// ****************************** 2nd 2nd 2nd ******************************
		Card card = null;
		
		if (g.haveLedSuit) {
			// we have some of the led suit
			Card ptnr = g.partner.frags[g.suitValueLed].getLowestThatBeats(g.bestCard.faceValue);
			if (ptnr == null) {
				card = h.frags[g.suitValueLed].getLowestThatBeats(g.bestCard.faceValue);
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
					if (h.doesPartnerHaveMaster(g.suitValueLed)) {
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
