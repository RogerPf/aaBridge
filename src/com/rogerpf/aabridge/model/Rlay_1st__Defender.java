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

public class Rlay_1st__Defender {

	static Card act(Gather g) {
		Hand h = g.hand;
		// ****************************** 1st Defender ******************************
		Card card = null;

//		if (g.trickNumb == 0) { // We are leading to the first trick so CANNOT see the dummy
//			Ignore this for now - we just always defend double dummy
//		}
		FragAnal fa = null;

		if (g.ourTopTricksTot >= g.defendersTarget) { // we need to try to cash in our tricks
			g.sort_ourTopTricks();
			fa = g.fragAnals[0];
		}

		if (fa == null) { // attack declarers weakness (if he has one)
			g.sort_oneOppsOurInnerRun();
			if (g.fragAnals[0].oppsTopTricksCor == 1 && g.fragAnals[0].ourInnerRun > 2) {
				fa = g.fragAnals[0];
			}
		}

		if (fa == null) { // attack declarers weakness (if he has one)
			g.sort_twoOppsOurInnerRun();
			if (g.fragAnals[0].oppsTopTricksCor == 2 && g.fragAnals[0].ourInnerRun > 2 && g.fragAnals[0].oppsMaxFragLen == 2) {
				fa = g.fragAnals[0];
			}
		}

		if (fa == null) { // pick a super safe (passive) lead
			g.sort_ourTopTricks();
			fa = g.fragAnals[0];
		}

		if ((fa.pnFragLen < fa.myFragLen) && h.areOurTopHoldingsContigious(fa.suit)) {
			card = fa.myFrag.getLast();
		}
		else {
			card = fa.myFrag.getFirst();
			// a quick extra check **** cheat check
			// @formatter:off
			if (   g.LHO.frags[fa.suit].areAllBetterThan(card.rank) 
			    || g.RHO.frags[fa.suit].areAllBetterThan(card.rank)) {
				card = fa.myFrag.getLast();
			}
			// @formatter:on
		}

		return card;
	}
}
