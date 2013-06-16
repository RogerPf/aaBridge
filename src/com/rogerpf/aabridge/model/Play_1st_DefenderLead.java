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

public class Play_1st_DefenderLead {

	static Card act(Hand h, PlayGen g) {
		// ****************************** 1st and Defender ******************************
		Card card = null;

		if (g.trickNumb == 0) { // We are leading to the first trick so CANNOT see the dummy
			
			if (g.noTrumps) {
				card = g.oct[0].getCard(3 /* 3 is the 4th highest and must exist */);
				if (h.frags[card.suitValue].areTopTwoContigious()) {
					card = h.frags[card.suitValue].get(0);
				}
			}
			else { // we have trumps
				if ((h.frags[g.trumpSuit].size() > 0) && (Math.random() < 0.10)) {
					card = g.lowestTrump;
				}
				else if (Math.random() > 0.333) {
					card = g.oct[0].getCardOrLowest(3 /* 3 is the 4th highest */);
					if (h.frags[card.suitValue].areTopTwoContigious()) {
						card = h.frags[card.suitValue].get(0);
					}
				}
				else {
					card = g.oct[1].getCardOrLowest(3); /* 1 is second suit */
					if (h.frags[card.suitValue].areTopTwoContigious()) {
						card = h.frags[card.suitValue].get(0);
					}
				}
			}
		}

		else { // This is NOT the first trick so we CAN see the dummmy
			
			if (g.noTrumps) {
				card = h.bestSlider(g.oct);
				if (h.frags[card.suitValue].areTopTwoContigious()) {
					card = h.frags[card.suitValue].get(0);
				}
				else if (h.frags[card.suitValue].get(0).isMaster()) {
					card = h.frags[card.suitValue].get(0);
				}
			}
			else { // we are onlead (not 1st trick) in an opps suit contract
				card = h.lastSlider(g.oct);
				if (h.frags[card.suitValue].areTopTwoContigious()) {
					card = h.frags[card.suitValue].get(0);
				}
				else if (h.frags[card.suitValue].get(0).isMaster()) {
					card = h.frags[card.suitValue].get(0);
				}
			}
			
		}
		return card;
	}
}
