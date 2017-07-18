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

//	static int debug_count = 0;

	static Card act(Gather g) {
		Hand h = g.hand;
		// ****************************** 1st Defender ******************************

//		if (g.trickNumb == 0) { // We are leading to the first trick so CANNOT see the dummy
//			Ignore this for now - as we ALWAYS defend (and play) double dummy
//		}
		boolean frags_sorted_OK = false;
		boolean from_top_tricks = false;

//		if (debug_count++ == 2) {
//			@SuppressWarnings("unused")
//			int z = 0;  // break point here
//		}

		if (g.ourTopTricksTot >= g.defendersTarget) { // we need to try to cash in our tricks
			g.sort_ourTopTricks();
			frags_sorted_OK = true;
			from_top_tricks = true;
		}

		if (frags_sorted_OK == false) { // attack declarers weakness (if he has one)
			g.sort_oneOppsOurInnerRun();
			if (g.fragAnals[0].oppsTopTricksCor == 1 && g.fragAnals[0].ourInnerRun > 2) {
				frags_sorted_OK = true;
			}
			else if (g.fragAnals[0].oppsTopTricksCor == 2 && g.fragAnals[0].ourInnerRun > 2 && g.fragAnals[0].oppsMaxFragLen == 2) {
				frags_sorted_OK = true;
			}
		}

		if (frags_sorted_OK == false) { // pick a super safe (passive) lead
			g.sort_ourTopTricks();
			frags_sorted_OK = true;
			from_top_tricks = true;
		}

		Card card = null;

		int fa_len = g.fragAnals.length;

		// @formatter:off
			
		/**  We are trying NOT to give away "easy" tricks on the lead.
		 */
		for (int fa_i = 0; fa_i < fa_len; fa_i++) {
			FragAnal fa = g.fragAnals[fa_i];
			
			card = pick_high_or_low(fa, h);
			Suit suit = card.suit;
			// Frag frag = h.frags[suit.v];
			Frag pnFrag = g.partner.frags[suit.v];
			Frag lhoFrag = g.LHO.frags[suit.v];
			Frag rhoFrag = g.RHO.frags[suit.v];
			
			if ((fa_i == fa_len - 1) || ((g.fragAnals[fa_i + 1]).myFragLen == 0)) {
				break; // as no more in the fragAnal list to choose from
			}
			
			if (from_top_tricks) {

				boolean trumping_a_prob =  
					 (g.LHO_hasTrumps && g.LHO.frags[fa.suit.v].isEmpty() && (g.RHO.frags[fa.suit.v].size() > 1))  
				  || (g.RHO_hasTrumps && g.RHO.frags[fa.suit.v].isEmpty() && (g.LHO.frags[fa.suit.v].size() > 1));
					     
			    boolean contig_and_they_have =   (h.areOurTopHoldingsContigious(fa.suit) == false)
			    		                && (     (lhoFrag.size() > 1)
			    		                     ||  (rhoFrag.size() > 1)
			    		                   );
			    
			    boolean partner_has_high_OK = (    (rhoFrag.size() > 0) 
			    		                        && (pnFrag.size() > 0)
		    		                    	    && (pnFrag.getFirst().rankRel.v > Rank.Ten.v)
		    		                            && (   pnFrag.getFirst().rankRel.v
		    		                    	        < rhoFrag.getFirst().rankRel.v
		    		                    	       )
		    		                      );
	
				// override if our lead is top tricks and we will get trumped or lead into a tenace
				if (    trumping_a_prob 
				     || (    contig_and_they_have
				         && ( (card.rankRel.v >= Rank.Jack.v)
				             || partner_has_high_OK
				            )
				    )
			       ) {
					continue; // try the next
				}
			}
	
		    // block below was switched back on 2017 march
			if (   g.LHO.frags[fa.suit.v].areAllBetterThan(card.rank) 
			    || g.RHO.frags[fa.suit.v].areAllBetterThan(card.rank)) {
				card = fa.myFrag.getLast();
			}

			break;  // this card will do just fine
		}
		
		// @formatter:on

		return card;
	}

	static Card pick_high_or_low(FragAnal fa, Hand h) {
		// =============================================================================

		if ((fa.pnFragLen < fa.myFragLen) && h.areOurTopHoldingsContigious(fa.suit)) {
			return fa.myFrag.getLast();
		}
		else {
			return fa.myFrag.getFirst();
		}
	}

}
