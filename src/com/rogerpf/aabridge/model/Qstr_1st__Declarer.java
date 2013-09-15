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

public class Qstr_1st__Declarer {

	static void act(Strategy stra, Gather g) {
		Hand h = g.hand; // a gather is just a collection of data from the point of view of THIS hand

		// ****************************** STRATEGY - 1st Declarer and Dummy (index 0) ******************************

		int brk = 0;
		if (g.trickNumb == 4)
			if (g.compass == 0)
				brk++; // put your breakpoint here :)

		if (g.drawTrumpsHint) {

			if (g.trumpsRunable && g.haveTrumps) {
				// play the trumps in the boring old normal way - so NO finnessing
				stra.add(new StraStep(Strategy.DrawTrumps, "", -1, -1, -1));
				return;
			}

			// is there a finesse/positional play that we would do well to take ?
			if (Play_Mpat.isPatternMatch(g, g.trumpSuit, Zzz.Leader_Pos, Zzz.MatchAsSelf)) {
				stra.add(new StraStep(Strategy.PlayCard, "a finesse in trumps", g.mpatRtn.rankRel, g.trumpSuit, -1));
				return;
			}

			boolean tryToEnterPartner = (g.myTrumps == 0 && g.pnTrumps > 0); // of course we still want to draw them

			if (!tryToEnterPartner) { // much work here
				// would we be better off leading from the other hand ?
				tryToEnterPartner = Play_Mpat.isPatternMatch(g, g.trumpSuit, Zzz.Leader_Pos, Zzz.MatchAsPartner);
			}

			if (tryToEnterPartner) {
				Card card = Strategy.selectBestEntryIntoPartner(g);
				if (card != null) {
					stra.add(new StraStep(Strategy.PlayCard, "to get to partners hand", card.rankRel, card.suit, h.partner().compass));
					return;
				}
			}

			if (g.haveTrumps) {
				// play the trumps in the boring old normal way - so NO finnessing
				stra.add(new StraStep(Strategy.DrawTrumps, "", -1, -1, -1));
				return;
			}

			// If we are here we are - on lead - wanting to draw trumps - have none - and can't find an entry to partner

			// TODO: ADD - can we find a lead that partner can trump ?
		}

		/** 
		 * From here on this is all about which suit to best develop for traking tricks 
		 * 
		 * This is no trumps or trumps are drawn or a master is left with the opps or
		 * they have trump control
		 *  or you just have no entry to to the trump drawing hand - umm not so !
		 *  
		 *  So think NO TRUMPS
		 *  
		 *  UNLESS we can crossruff
		 */

		if (g.ourTopTricksTot >= g.tricksRemaining || g.ourTopTricksTot >= g.ourTarget) {
			g.sort_topTricksButWithBadEntriesFirst();
			stra.add(new StraStep(Strategy.RunTopTricksInSuit, "", -1, g.fragAnals[0].suit, -1));
			return;
		}

		if (brk > 0)
			brk++; // put your breakpoint here :)

		/**
		 *  OK so we dont have enought top tricks but we MAY be able to many others by a cross ruff
		 */
		if (g.haveTrumps && g.pnHaveTrumps) {

			if (g.myTrumps >= g.pnTrumps) {
				g.sort_crossruffSuitability(Zzz.Me);
				FragAnal fa = g.fragAnals[0];
				if (fa.mpatRtn.rating > 0) {
					stra.add(new StraStep(Strategy.PlayCard, "for Partner to Ruff", fa.myFrag.getLast().rankRel, fa.suit, -1));
				}
			}

			if (g.myTrumps <= g.pnTrumps) { // pn has more trumps that I do (or equal and no Xruff earlier)
				g.sort_crossruffSuitability(Zzz.Pn);
				FragAnal fa = g.fragAnals[0];
				if (fa.mpatRtn.rating > 0) {
					Card card = Strategy.selectBestEntryIntoPartner(g);
					if (card != null) {
						// System.out.println(Zzz.compass_to_nesw_st_long[h.compass] + "  GetToPartner " + card);
						stra.add(new StraStep(Strategy.PlayCard, "get to partner", card.rankRel, card.suit, h.partner().compass));
						return;
					}
				}
			}
		}

		// is there a finesse/positional play that we might like to take ?
		for (FragAnal fa : g.fragAnals) {
			// results are stored in fa[suit].mpatRtn
			Play_Mpat.isPatternMatch(g, fa.suit, Zzz.Leader_Pos, Zzz.MatchAsSelf);
		}
		// now we can sort on this info
		g.sort_finesseSuitablity(Zzz.Me);
		// well do we have a good finesse to take ?
		{
			FragAnal fa = g.fragAnals[0];
			if (fa.mpatRtn.rating > 0) {
				Card card = h.frags[fa.suit].getIfRelExist(fa.mpatRtn.rankRel);
				if (card == null) {
					@SuppressWarnings("unused")
					int x = 0; // put your breakpoint here
				}
				assert (card != null);
				// System.out.println(Zzz.compass_to_nesw_st_long[h.compass] + "  TakeFinesse " + card);
				stra.add(new StraStep(Strategy.PlayCard, "take finesse", fa.mpatRtn.rankRel, fa.suit, -1));
				return;
			}
		}

		// is there a finesse/positional play that we might *partner* to take ?
		for (FragAnal fa : g.fragAnals) {
			// results are stored in fa[suit].mpatRtn
			Play_Mpat.isPatternMatch(g, fa.suit, Zzz.Leader_Pos, Zzz.MatchAsPartner);
		}
		// now we can sort on this info
		g.sort_finesseSuitablity(Zzz.Pn);
		// well do we have a good finesse for *partner* to take ?
		{
			FragAnal fa = g.fragAnals[0];
			if (fa.mpatRtn.rating > 0) {
				Card card = Strategy.selectBestEntryIntoPartner(g);
				if (card != null) {
					// System.out.println(Zzz.compass_to_nesw_st_long[h.compass] + "  GetToPartner " + card);
					stra.add(new StraStep(Strategy.PlayCard, "get to partner", card.rankRel, card.suit, h.partner().compass));
					return;
				}
			}
		}

		// As a last resort we go back to the simple run our top tricks
		g.sort_topTricksButWithBadEntriesFirst();
		stra.add(new StraStep(Strategy.RunTopTricksInSuit, "", -1, g.fragAnals[0].suit, -1));

	}
}
