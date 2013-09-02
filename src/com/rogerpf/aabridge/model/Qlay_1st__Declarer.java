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

public class Qlay_1st__Declarer {

	static Card act(Gather g) {
		Hand h = g.hand;
		// ****************************** 1st Declarer and Dummy (index 0) ******************************
		Card card = null;

		int brk = 0;
		if (g.trickNumb == 8)
			if ((g.compass == 0))
				brk++; // put your breakpoint here :)

		if (brk > 0)
			brk++; // put your breakpoint here :)

		Strategy stra = h.getStrategy();
		assert (stra != null);
		for (StraStep step : stra) {

			if (step.idea == Strategy.DrawTrumps) {
				// --------------------------------------------------
				assert (!(g.outstandingTrumps == 0 || (g.outstandingTrumps == 1 && g.isTopOutstandingTrumpMaster)));

				if (g.myTrumps <= g.pnTrumps) {
					card = g.highestTrump;
				}
				else { // partners trumps are shorter
					boolean topContig = h.areOurTopHoldingsContigious(g.trumpSuit);
					if (topContig)
						card = g.lowestTrump;
					else
						card = g.highestTrump;
				}

				if (card != null)
					break;
			}

			if (step.idea == Strategy.PlayCard) {
				// --------------------------------------------------
				card = h.frags[step.suit].getIfRelExist(step.rankRel);
				if (card == null) {
					@SuppressWarnings("unused")
					int x = 0; // put your breakpoint here
				}
				assert (card != null);

				if (card != null)
					break;
			}

			if (step.idea == Strategy.RunSuit) {
				// ----------------------------------------------------
				int sv = step.suit;
				int myHolding = h.frags[sv].size();
				if (myHolding == 0)
					continue;
				int pnHolding = h.partner().frags[sv].size();
				if (myHolding <= pnHolding) {
					card = h.frags[sv].get(0);
				}
				else { // partners suit is shorter
					boolean topContig = h.areOurTopHoldingsContigious(sv);
					if (topContig) {
						card = h.frags[sv].getLast();
					}
					else {
						card = h.frags[sv].get(0);
					}
				}
				if (card != null)
					break;
			}

			if (step.idea == Strategy.RunTopTricksInSuit) {
				// ----------------------------------------------------
				int sv = step.suit;
				int myHolding = h.frags[sv].size();
				if (myHolding == 0)
					continue;
				int pnHolding = h.partner().frags[sv].size();
				if ((h.frags[sv].get(0).rankEqu != Zzz.Ace) && (pnHolding > 0) && (g.partner.frags[sv].get(0).rankEqu != Zzz.Ace)) {
					continue;
				}
				if (myHolding <= pnHolding) {
					card = h.frags[sv].get(0);
				}
				else { // partners suit is shorter
					boolean topContig = h.areOurTopHoldingsContigious(sv);
					if (topContig) {
						card = h.frags[sv].getLast();
					}
					else {
						card = h.frags[sv].get(0);
					}
				}
				if (card != null)
					break;
			}

			System.out.println("Strategy **USE**  Time - Unknown Step - " + step.idea);

		}

		if (card == null) {

			// We are out of Stratergy Steps, so we default to a 'reasonable tactic'

			if (g.trumpContract) { // we are onlead in OUR own SUIT contract (so never to the 1st trick)
				// Primary stratergy is to draw trumps
				if (g.outstandingTrumps == 0 || (g.outstandingTrumps == 1 && g.isTopOutstandingTrumpMaster)) {
					; // card is still null
				}
				else { // (outstandingTrumps > 0)
					if (g.myTrumps <= g.pnTrumps) {
						card = g.highestTrump;
					}
					else { // partners trumps are shorter
						boolean topContig = h.areOurTopHoldingsContigious(g.trumpSuit);
						if (topContig)
							card = g.lowestTrump;
						else
							card = g.highestTrump;
					}
				}
			}

			if (card == null) {
				g.sort_ourTopTricks();
				FragAnal fa = g.fragAnals[0];
				if ((fa.pnFragLen < fa.myFragLen) && h.areOurTopHoldingsContigious(fa.suit)) {
					card = fa.myFrag.getLast();
				}
				else {
					card = fa.myFrag.getFirst();
				}
			}
		}

		return card;
	}
}
