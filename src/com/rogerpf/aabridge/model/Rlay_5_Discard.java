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

class Rlay_5_Discard {
	// ---------------------------------- CLASS -------------------------------------

	public static Card pickBest(Gather g) {
		Hand h = g.hand;
		// ****************************** Discard ******************************

		assert (g.positionInTrick > 0); // We can't be the first to play AND be wanting to discard

		/** 
		 * We are now in discard time - so cards in the current trick can be 
		 *   taken into account, in terms of being 'out of it'
		 */
		h.deal.setAllrankRelEquMpat_part1(false, h, null, null, g.positionInTrick, g.trickNumb);

		Card best = null;
		float score = -999.0f;

		// for (int i : Dir.rota[(int)((Math.random()*4.0f)%4.0f)]){
		for (int i : Zzz.zto3) {
			Frag frag = h.frags[i];
			Card card = frag.getLast();
			if (card == null)
				continue;
			float cs = scoreDiscard(g, frag, card);
			if (best == null || (cs > score)) {
				best = card;
				score = cs;
			}
		}
		return best;
	}

	/**
	 * We are really testing the suit and are currently assuming 
	 * that we have been offered the lowest one as a discard candidate
	 * 
	 *  -1.0 means keep if you can
	 *   0.0 means no opinion
	 *   1.0 good for discard
	 */
	static private float scoreDiscard(Gather g, Frag frag, Card card) {
//		Hand h = g.hand;
		// ************************************************************************

		Suit suit = card.suit;
		int oppsMax = Hand.oppsMax(g, suit);
		int fragSize = frag.size();

		int pnFragSize = g.partner.frags[card.suit.v].size();
		Card pnHighCard = null;
		if (pnFragSize > 0) {
			pnHighCard = g.partner.frags[card.suit.v].get(0);
		}

		if (suit == g.trumpSuit)
			return -1.0f; // normally best not to discard trumps :)

		int brk = 0;
		if (g.trickNumb == 11)
			if ((g.compass.v == 1))
				brk++; // put your breakpoint here :)

		if (brk > 0)
			brk++; // put your breakpoint here :)

		Card high = frag.get(0);

		int oppsRunDepth = Hand.oppsAceDownRunDepth(g, suit);

		if (oppsRunDepth == 0) { // so we have the master

			if (high.rankRel == Rank.Ace) {
				// CHEAT WARNING
				boolean lhoHigh = g.LHO.frags[card.suit.v].size() > 0 && g.LHO.frags[card.suit.v].get(0).rankRel == Rank.King;
				boolean rhoHigh = g.RHO.frags[card.suit.v].size() > 0 && g.RHO.frags[card.suit.v].get(0).rankRel == Rank.King;

				float extra = (rhoHigh && g.winnerSoFar == g.RHO || lhoHigh && g.winnerSoFar == g.LHO) ? -0.05f : 0.00f;

				// note it can't be both, they both can't have the rel king
				if (lhoHigh || rhoHigh) {
					// So I have the logical ace and they have the king
					if (fragSize == 1) {
						return extra - 0.9f;
					}
					if (fragSize == 2)
						return extra + 0.7f; // we can spare it
					if (fragSize == 3)
						return extra + 0.8f; // we can spare it
					if (fragSize == 4)
						return extra + 0.9f; // we can spare it
					return extra + 1.0f;
				}
				else {
					if (oppsMax == 0) {
						return 1.0f;
					}
					else {
						return 0.9f; // ummm how to kick a good value ????
					}
				}
			}
			else { // so partner has the logical Ace
				return 0.1f;
			}

		}

		// they have the master

		if (fragSize > oppsRunDepth + 1) {
			// we have good discard potential here
			if (oppsMax < fragSize) {
				return 1.0f;
			}
			if (oppsMax == fragSize) {
				return 0.1f;
			}
			return 0.9f; // ummm how to pick a good value ????
		}

		if (fragSize < oppsRunDepth + 1) {
			return 1.0f; // currently I can't stop the rot in this suit
		}

		// a quick check that our highest card will do the job
		// of course here oppsRunDepth + 1 == frag.size()

		int ourInner = Hand.ourInnerRunDepth(g, suit, g.oppsBoth[suit.v].contigCards(0));

		if (Rank.Ace.v - oppsRunDepth - ourInner > high.rankRel.v) { // TO DO: THIS TEST NEED MORE WORK - is it broken? = what does it do
			return 1.0f; // no good our highest is not high enough
		}

		// can partner stop the opps taking more
		if (pnFragSize > oppsRunDepth && pnHighCard.rankEqu.v >= high.rankEqu.v) {
			return 0.8f; // yes he can so we can dump a card
		}

		return 0.0f; // so we assume that we want to keep this one if we can
	}

	static Card ChangeIntoSignalIfAppropriate(Gather g, Card card) {
		// ************************************************************************

//		if (g != null)
//			return card;

		int brk = 0;
		if (g.trickNumb == 3)
			if (g.compass.v == 3)
				brk++; // put your breakpoint here :)

		if (brk > 0)
			brk++; // put your breakpoint here :)

		Hand h = g.hand;
		assert (g.defendersSide);
		int defenderSignals = g.dumbAutoDir.defenderSignals;
		if (defenderSignals == Zzz.NoSignals)
			return card;

		Suit suit = card.suit;

		FragAnal fa = g.fragAnals[suit.v];
		Frag frag = fa.myFrag;
		Strategy stra = h.getStrategy();

		if (stra.signalWanted[suit.v] == false)
			return card;

		if (card != frag.getLast())
			return card;

		// We are never comming back past here again
		stra.signalWanted[suit.v] = false;

		if (fa.myFragLen < 2)
			return card;

		if (fa.myOrigFragLen >= fa.myFragLen + 2)
			return card;

		// So this is the first or second card played in this suit (following discarded or led)

		Card card2 = frag.get(fa.myFragLen - 2); // get the one before last
		if (card2.rank.v >= Rank.Nine.v) // we don't signal with high cards
			return card;

//		boolean signalWanted = (fa.myFragLen % 2 == 1) ^ (defenderSignals == Zzz.StdEvenCount);
		boolean signalWanted = (fa.myOrigFragLen % 2 == 1) ^ (defenderSignals == Zzz.StdEvenCount); // if on second card we still sig ORIG len

		if (defenderSignals == Zzz.StdEvenCount && card.suit == g.trumpSuit) {
			signalWanted = !signalWanted; // cos this is the 'standard' system
		}

		if (signalWanted) {
			// Can we improve on the existing signal card ? it may be a little low
			int i = fa.myFragLen - 2; // this is our current signal card position
			for (; i >= 0; i--) {
				Card c = frag.get(i);
				if (i < 2 && c.rank.v == card2.rank.v + 1) { // fine only if they are contigious
					card2 = c;
					continue;
				}
				if (c.rank.v >= Rank.Nine.v)
					break;
				card2 = c;
			}

//			 System.out.println("Signal " + card2 + " substituted for " + card);
			card = card2;
		}

		return card;
	}

}