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

public class Play_1st_DummyDeclarerLead {

	static Card act(Hand h, PlayGen g) {
		// ****************************** 1st and Deummy Declarer  ******************************
		Card card = null;
		
		if (g.noTrumps == false) { // we are onlead in OUR own SUIT contract (so never to the 1st trick) 

			// Primary stratergy is to draw trumps
			if (g.outstandingTrumps == 0 || (g.outstandingTrumps == 1 && h.outstandingTrumpIsMaster())) {
				; // card is still null
			}
			else { // (outstandingTrumps > 0)
				if (g.myTrumps <= g.partnersTrumps) {
					card = g.highestTrump;
				}
				else { // partners trumps are shorter
					boolean topContig = h.areOurTopHoldingsContigious(g.trumpSuit);
					if (topContig) {
						card = g.lowestTrump;
					}
					else {
						card = g.highestTrump;
					}
				}
			}
		}
		
		if (card == null) { // so No Trumps + other cases
			// We are on lead to the second or later trick
			// It is our contract - either NO TRUMPS or all the trumps we want to draw have been drawn.
			// or this hand has not got any
			// Card pos = bestSlider(coct);
			Card pos = h.bestSlider(g.bothHands);
			int myHolding = h.frags[pos.suitValue].size();
			int pnHolding = h.partner().frags[pos.suitValue].size();
			if (myHolding <= pnHolding) {
				card = h.frags[pos.suitValue].get(0);
			}
			else { // partners suit is shorter
				boolean topContig = h.areOurTopHoldingsContigious(pos.suitValue);
				if (topContig) {
					card = h.frags[pos.suitValue].getLast();
				}
				else {
					card = h.frags[pos.suitValue].get(0);
				}
			}
		}
		
		return card;
	}
}
