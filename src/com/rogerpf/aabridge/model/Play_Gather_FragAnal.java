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

import java.awt.Point;
import java.util.Arrays;

import com.rogerpf.aabridge.model.Deal.DumbAutoDirectives;
import com.rogerpf.aabridge.model.Play_Mpat.Mpat;

/**
 * A   Gather  is a gathering togeather of all the analysis of the deal from the
 * point of view of the next hand to play a key part is the FragAnal
 * which hold info centered on THAT PLAYERS suit fragments
 */
class Gather {
	// ---------------------------------- CLASS -------------------------------------
	//@formatter:off
		
	public void sort_oneOppsOurInnerRun() {
		for (FragAnal fa : fragAnals) {
			fa.calc_oneOppsOurInnerRun();
		}
		Arrays.sort(fragAnals);
	}
	
	public void sort_twoOppsOurInnerRun() {
		for (FragAnal fa : fragAnals) {
			fa.calc_twoOppsOurInnerRun();
		}
		Arrays.sort(fragAnals);
	}
	
	public void sort_ourTopTricks() {
		for (FragAnal fa : fragAnals) {
			fa.calc_ourTopTricks();
		}
		Arrays.sort(fragAnals);
	}
	
	public void sort_oppsTopTricks() {
		for (FragAnal fa : fragAnals) {
			fa.calc_oppsTopTricks();
		}
		Arrays.sort(fragAnals);
	}
	
	public void sort_entryIntoPartner() {
		for (FragAnal fa : fragAnals) {
			fa.calc_entryIntoPartner();
		}
		Arrays.sort(fragAnals);
	}
	
	public void sort_simpleOrigLenOrder() {
		for (FragAnal fa : fragAnals) {
			fa.calc_simpleOrigLenOrder();
		}
		Arrays.sort(fragAnals);
	}
	
	public void sort_topTricksButWithBadEntriesFirst() {
		for (FragAnal fa : fragAnals) {
			fa.calc_topTricksButWithBadEntriesFirst();
		}
		Arrays.sort(fragAnals);
	}
	
	public void sort_finesseSuitablity(int who) {
		for (FragAnal fa : fragAnals) {
			fa.calc_finesseSuitablity(who);
		}
		Arrays.sort(fragAnals);
	}
	
	public void sort_crossruffSuitability(int who) {
		for (FragAnal fa : fragAnals) {
			fa.calc_crossruffSuitablity(who);
		}
		Arrays.sort(fragAnals);
	}

	DumbAutoDirectives dumbAutoDir;
	
	Hand    hand				;
	Deal    deal				;
	int     compass				;
	char    compassCh			;
	Hand    declarer			;
	int		declarerCompass		;
	int		declarerAxis		;
	boolean	declarersSide		;
	boolean	defendersSide		;
	Hand    leader				;
	boolean leaderUs 			;
	int     positionInTrick		;
	int     posFromDeclarer		;
	int     trickNumb 			;
	Card    cardLed 			;
	int 	rankLed 			;
	int 	suitLed 			;
	int 	trumpSuit 			;
	boolean noTrumps 			;
	boolean trumpContract		;
	boolean areWeWinning		;
	boolean ourContract 		;
	boolean z					;   // viZ   Declarer/Dummy mormally play highest of touching Defence lowest
	int 	myTrumps 			;
	int 	pnTrumps 			;
	boolean haveSuitLed 		;
	boolean pnHaveSuitLed 		;
	boolean haveTrumps 			;
	boolean pnHaveTrumps 		;
	int 	outstandingTrumps	;
	Hand 	bestCardPlayer 		;
	Card 	bestCard 			;
	boolean bestByTrumping 		;

	Card    lowestTrump 		;
	Card    lowestOfLed			;
	Card    highContLowestOfLed ;
	Card    highestTrump 		;
	Card    highestOfLed		;
	
	Card    pnHighestOfLed		;

	int     tricksPlayed		;
	int     tricksRemaining		;
	int     declarersTarget		;
	int     defendersTarget		;
	int		ourTarget			;
	int		oppsTarget			;
	
	Hand	partner				;
	Hand	LHO					;
	Hand	RHO					;
	
	boolean LHO_hasLedSuit 		;
	boolean LHO_hasTrumps 		;
	
	Hand    winnerSoFar			;
	
	boolean isTopOutstandingTrumpMaster;
	boolean secondPlayerFollowedSuit = false;
	
	boolean drawTrumpsHint      ; // meaningfull only to the declaring side
	boolean trumpsRunable       ; //   "                    "
	
	MpatRtn mpatRtn = null; // this is set to the last tested FragAnal mpatRtn
	
	FragAnal[] fragAnals;
	
	int oppsTopTricksTot = 0;
	int ourTopTricksTot = 0;
	
	Frag[] ourBoth;
	Frag[] oppsBoth;
	
	Frag     fragLed = null;
	Frag     fragTrumps = null;
	Frag     pnFragLed = null;
	Frag     pnFragTrumps = null;
	FragAnal faLed = null;
	FragAnal faTrumps = null;
	
	boolean  pnHasSuitLed = false;
	int      pnHighestOfLedRank;
	int      debug_suit = -1;
	
	int      finMaDepth = 9; // used by Finesse paternMatching to pass down the finesse depth param, 9 means there will be no limit



	Gather(Hand h, DumbAutoDirectives dumbAutoDir, boolean strategyCreated) { // Constructor and everything
		// ==============================================================================================
		/** 
		 * Early on we must create the Epat entires and more importatly the
		 * Rel and Equ values for all the active cards.  This way
		 * they are available for all the other analysis functions to use.
		 */
		this.dumbAutoDir	= dumbAutoDir;
		hand				= h;
		deal				= h.deal;
		leader 				= deal.getCurTrickLeader();
		positionInTrick		= (4 + h.compass - leader.compass) % 4;  // 0 = leader
		trickNumb 			= deal.getCurTrickIndex();
		compass				= h.compass;
		compassCh			= h.compassCh;
		
		Mpat[] mpatAyEqu = new Mpat[4];
		Mpat[] mpatAyRel = new Mpat[4];
		
		if (trickNumb == 1) {
			if ((compass == 0)) {
				@SuppressWarnings("unused")
				int x = 0; // put your breakpoint here
			}
		}
		deal.setAllrankRelEquMpat_part1(true, h, mpatAyEqu, mpatAyRel, positionInTrick, trickNumb); // see comment above
		
		declarer			= deal.hands[h.deal.contractCompass];
		declarerCompass		= h.deal.contractCompass;
		declarerAxis		= h.deal.contractAxis();
		leaderUs 			= (h == leader);
		posFromDeclarer		= (4 + h.compass - declarerCompass) % 4;
		
		declarersSide		= posFromDeclarer % 2 == 0;
		defendersSide		= posFromDeclarer % 2 == 1;

		cardLed 			= (leaderUs) ? null : leader.played.getLast();
		rankLed 			= (cardLed == null) ? -1 : cardLed.rank;
		suitLed 			= (cardLed == null) ? -1 : cardLed.suit;
		trumpSuit 			= deal.contract.suit;
		noTrumps 			= (trumpSuit == Zzz.Notrumps);
		trumpContract       = !noTrumps;
		areWeWinning 		= (cardLed == null) ? false : h.areWeWinning();
		ourContract 		= (deal.contractAxis()) == h.axis();
		z					= ourContract;
		myTrumps 			= (noTrumps) ? 0 : h.frags[trumpSuit].size();
		pnTrumps 			= (noTrumps) ? 0 : h.partner().frags[trumpSuit].size();
		haveSuitLed 		= (cardLed == null) ? false : (h.frags[suitLed].size() > 0);
		pnHaveSuitLed 		= (cardLed == null) ? false : (h.partner().frags[suitLed].size() > 0);
		haveTrumps 			= (!noTrumps && (myTrumps > 0));
		pnHaveTrumps 		= (!noTrumps && (pnTrumps > 0));
		bestCardPlayer 		= (leaderUs) ? null : h.getBestCardOfTrickPlayer();
		bestCard 			= (leaderUs) ? null : bestCardPlayer.played.getLast();
		bestByTrumping 		= (leaderUs) ? false : suitLed != bestCard.suit;
		                	
		lowestTrump 		= (noTrumps) ? null : h.frags[trumpSuit].getLast();
		lowestOfLed			= (leaderUs) ? null : h.frags[suitLed].getLast();
		highContLowestOfLed = (leaderUs) ? null : h.frags[suitLed].highestContigWith(lowestOfLed);
		highestTrump 		= (noTrumps) ? null : h.frags[trumpSuit].getCard(0);
		highestOfLed		= (leaderUs) ? null : h.frags[suitLed].getCard(0);
		
		tricksPlayed		= trickNumb;
		tricksRemaining     = 13 - tricksPlayed;
		Point trickCountPt  = h.deal.getContractTrickCountSoFar();
		declarersTarget     = 6 + h.deal.contract.level - trickCountPt.x;
		defendersTarget     = 13 - declarersTarget + 1  - trickCountPt.y;
		ourTarget			= (ourContract) ? declarersTarget : defendersTarget;
		oppsTarget			= (ourContract) ? defendersTarget : declarersTarget;
		
		secondPlayerFollowedSuit = (positionInTrick <= 1) ? false : leader.LHO().played.getLast().suit == cardLed.suit;
		
		partner				= h.partner();
		LHO					= h.LHO();
		RHO					= h.RHO();

		LHO_hasLedSuit 	    = (cardLed == null) ? false : (LHO.frags[suitLed].size() > 0);
		LHO_hasTrumps 		= (!noTrumps && ((LHO.frags[trumpSuit].size() > 0)));
		
		fragAnals  = new FragAnal[4];
		ourBoth    = new Frag[4];
		oppsBoth   = new Frag[4];
		for (int i : Zzz.cdhs) {
			ourBoth[i]        = (Frag)(h.frags[i].clone());
			oppsBoth[i]       = (Frag)(LHO.frags[i].clone());
		}	
		h.addPartnersCurrentCards( ourBoth);
		LHO.addPartnersCurrentCards( oppsBoth);
		
		for (int i : Zzz.cdhs) {
			fragAnals[i] = new FragAnal(h.frags[i]);
			FragAnal fa = fragAnals[i];

			fa.myFrag           = h.frags[i];
			fa.pnFrag           = partner.frags[i];
			fa.frag[0]          = fa.myFrag;
			fa.frag[1]          = fa.pnFrag;
			
			// this hand and partner
			fa.myFragLen        = fa.myFrag.size();
			fa.pnFragLen        = fa.pnFrag.size();		
			fa.fragLen[0]       = fa.myFragLen;
			fa.fragLen[1]       = fa.pnFragLen;		
			
			// orig hand and partner
			Frag myOrigFrag     = h.fOrgs[i];
			Frag pnOrigFrag     = partner.fOrgs[i];
			fa.myOrigFragLen    = myOrigFrag.size();
			fa.pnOrigFragLen    = pnOrigFrag.size();		
			fa.origFragLen[0]   = fa.myOrigFragLen;
			fa.origFragLen[1]   = fa.pnOrigFragLen;		

			fa.isTrumps         = (fa.suit == deal.contract.suit);
			fa.ourCombLen       = fa.fragLen[0] + fa.fragLen[1];
			fa.ourOrigCombLen   = fa.origFragLen[0] + fa.origFragLen[1];
			fa.ourMaxFragLen    = Math.max(fa.myFragLen, fa.pnFragLen);			
			fa.ourTopContigCards = ourBoth[i].contigCards(0);
			fa.ourTopTricksRaw  = Math.min(fa.ourTopContigCards, fa.ourMaxFragLen);
			fa.ourTopTricksCor  = fa.ourTopTricksRaw;
			fa.ourMissingKing   = ourBoth[i].isMissingKing();
			fa.ourMissingAceQueen = ourBoth[i].isMissingAceQueen();
			
			// the two opps
			fa.oppsLhoFragLen   = LHO.frags[i].size();
			fa.oppsRhoFragLen   = RHO.frags[i].size();
			fa.oppsCombLen      = fa.oppsLhoFragLen + fa.oppsRhoFragLen;
			fa.oppsOrigCombLen  = 13 - fa.ourOrigCombLen;
			fa.oppsMaxFragLen   = Math.max(fa.oppsLhoFragLen, fa.oppsRhoFragLen);	
			fa.oppsTopContigCards = oppsBoth[i].contigCards(0);
			fa.oppsTopTricksRaw = Math.min(fa.oppsTopContigCards, fa.oppsMaxFragLen);
			fa.oppsTopTricksCor = fa.oppsTopTricksRaw;
			
			// fa.outstanding  -  does not include any card played this trick
			fa.outstanding      = fa.oppsCombLen; // not a cheat (at least for declearer) just a quick way to calc
			
			// these each use values from the other block
			if (fa.ourTopTricksRaw >= fa.oppsMaxFragLen) {
				fa.ourTopTricksCor = fa.ourMaxFragLen;
			}	
			ourTopTricksTot  += fa.ourTopTricksCor;

			if (fa.oppsTopTricksRaw >= fa.ourMaxFragLen) {
				fa.oppsTopTricksCor = fa.oppsMaxFragLen;
			}	
			oppsTopTricksTot  += fa.oppsTopTricksCor;

			fa.ourInnerRun    = ourBoth[i].contigCards(fa.oppsTopContigCards);
			fa.oppsInnerRun   = oppsBoth[i].contigCards(fa.ourTopContigCards);

			if (fa.myFragLen > 0 && fa.pnFragLen > 0) {
				Card pnHigh = fa.pnFrag.get(0);
				fa.isEntryIntoPartner = (pnHigh.rankEqu == Zzz.Ace) && (pnHigh.rank > fa.myFrag.getLast().rank);
			}
			
			fa.mpatEqu = mpatAyEqu[i];
			fa.mpatRel = mpatAyRel[i];
			
			// Cross Entry Estimate
			fa.ourCrossEntryEst = 0;
			if (fa.ourTopTricksCor > 0 && fa.myFragLen > 0 && fa.pnFragLen > 0) {
				int my = 0, pn = 0;
				int pnl = fa.pnFragLen;
				for (Card card : fa.myFrag) {
					if (card.rankEqu != Zzz.Ace) break;
					if (--pnl < 0) break;
					if (card.rank > fa.pnFrag.get(pnl).rank)
						my ++;
				}
				
				int myl = fa.myFragLen;
				for (Card card : fa.pnFrag) {
					if (card.rankEqu != Zzz.Ace) break;
					if (--myl < 0) break;
					if (card.rank > fa.myFrag.get(myl).rank)
						pn ++;
				}
				fa.ourCrossEntryEst = Math.min(my, pn) * 10 + Math.max(my, pn);
			}
			// any use of Mpat or CPat must be done in a 'gather create part 2'
		}
		outstandingTrumps = (noTrumps) ? 0 : fragAnals[trumpSuit].outstanding;
		isTopOutstandingTrumpMaster = (noTrumps || oppsBoth[trumpSuit].isEmpty()) ? false : (oppsBoth[trumpSuit].get(0).rankRel == Zzz.Ace);
		
		fragLed = (cardLed == null) ? null : h.frags[cardLed.suit];
		fragTrumps = (noTrumps) ? null : h.frags[trumpSuit];
		
		pnFragLed = (cardLed == null) ? null : partner.frags[cardLed.suit];
		pnFragTrumps = (noTrumps) ? null : h.frags[trumpSuit];

		pnHasSuitLed 		= (pnFragLed == null) ? false : (pnFragLed.size() > 0);
		pnHighestOfLedRank  = (pnHasSuitLed == false) ? 0 : pnFragLed.get(0).rank;
		
		faLed = (cardLed == null) ? null : fragAnals[cardLed.suit];
		faTrumps = (noTrumps) ? null : fragAnals[trumpSuit];
		
		drawTrumpsHint = (noTrumps) ? false : !(outstandingTrumps == 0 || (outstandingTrumps == 1 && isTopOutstandingTrumpMaster));
		trumpsRunable  = (noTrumps) ? false : (faTrumps.ourTopTricksCor >= Math.min(4, outstandingTrumps));
		
		winnerSoFar = deal.winnerSoFar();
	
		deal.setAllrankRelEquMpat_part2(true); // see comment above

		@SuppressWarnings("unused")
		int x = 0; // put your breakpoint here
		//@formatter:on
	}

}

/**
 * The FragAnal holds point summary info centered on a PARTICULAR PLAYERS suit fragments
 */
class FragAnal implements Comparable<FragAnal> {
	// ---------------------------------- CLASS -------------------------------------
	public int suit;
	public char suitCh;

	public Frag myFrag;
	public Frag pnFrag;
	public Frag frag[] = new Frag[2];

	public int myFragLen = -1;
	public int pnFragLen = -1;
	public int fragLen[] = new int[2];

	public int myOrigFragLen = -1;
	public int pnOrigFragLen = -1;
	public int origFragLen[] = new int[2];

	public Mpat mpatEqu;
	public Mpat mpatRel;
	MpatRtn mpatRtn;

	public boolean isTrumps = false;
	public int ourCombLen = -1;
	public int ourOrigCombLen = -1;
	public int ourMaxFragLen = -1;

	public int ourTopContigCards = -1;
	public int ourTopTricksCor = -1;
	public int ourTopTricksRaw = -1;
	public int ourInnerRun = -1;
	public boolean ourMissingKing = false;
	public boolean ourMissingAceQueen = false;

	public int outstanding = -1;

	public int oppsLhoFragLen = -1;
	public int oppsRhoFragLen = -1;
	public int oppsCombLen = -1;
	public int oppsOrigCombLen = -1;
	public int oppsMaxFragLen = -1;

	public int oppsTopContigCards = -1;
	public int oppsTopTricksCor = -1;
	public int oppsTopTricksRaw = -1;
	public int oppsInnerRun = -1;

	public int ourCrossEntryEst = -1;

	public boolean isEntryIntoPartner = false;

	FragAnal(Frag frag) { // constructor
		// ==============================================================================================
		this.frag[0] = frag;
		this.suit = frag.suit;
		this.suitCh = frag.suitCh;
		mpatRtn = new MpatRtn();
	}

	public int compareTo(FragAnal o) {
		// ==============================================================================================
		// Subtract from the 'other' so we get the best at the low end (zero end) of the array
		return (o.mpatRtn.rating - this.mpatRtn.rating);
	}

	/**
	 */
	public int calc_oneOppsOurInnerRun() {
		// ==============================================================================================
		MpatRtn rtn = mpatRtn;
		// @ formatter:off
		if (fragLen[0] == 0)
			return rtn.rating = -4;
		if (isTrumps)
			return rtn.rating = -3;
		// @ formatter:on

		int r = 0;
		if (oppsTopTricksCor == 1 && ourInnerRun > 2) {
			r += ourInnerRun;
		}
		return rtn.rating = r;
	}

	/**
	 */
	public int calc_twoOppsOurInnerRun() {
		// ==============================================================================================
		MpatRtn rtn = mpatRtn;
		// @ formatter:off
		if (fragLen[0] == 0)
			return rtn.rating = -4;
		if (isTrumps)
			return rtn.rating = -3;
		// @ formatter:on

		int r = 0;
		if (oppsTopTricksCor == 2 && ourInnerRun > 2 && oppsMaxFragLen == 2) {
			r += ourInnerRun;
		}
		return rtn.rating = r;
	}

	/**
	 */
	public int calc_strat_ourTopTricks() {
		// ==============================================================================================
		MpatRtn rtn = mpatRtn;
		// @ formatter:off
		// if (fragLen[0] == 0) return rtn.rating = -4;
		if (isTrumps)
			return rtn.rating = -3;
		// @ formatter:on

		return rtn.rating = ourTopTricksCor * 100 + ourCombLen;
	}

	/**
	 */
	public int calc_ourTopTricks() {
		// ==============================================================================================
		MpatRtn rtn = mpatRtn;
		// @ formatter:off
		if (fragLen[0] == 0)
			return rtn.rating = -4;
		if (isTrumps)
			return rtn.rating = -3;
		// @ formatter:on

		return rtn.rating = ourTopTricksCor * 100 + ourCombLen;
	}

	/**
	 */
	public int calc_oppsTopTricks() {
		// ==============================================================================================
		MpatRtn rtn = mpatRtn;
		// @ formatter:off
		if (fragLen[0] == 0)
			return rtn.rating = -4;
		if (isTrumps)
			return rtn.rating = -3;
		// @ formatter:on

		int oTopTricks = (myFragLen == 0 || pnFragLen == 0) ? oppsTopTricksCor : oppsTopTricksRaw;
		return rtn.rating = oTopTricks * 100 + (20 - oppsCombLen);
	}

	/**
	 */
	public int calc_entryIntoPartner() {
		// ==============================================================================================
		MpatRtn rtn = mpatRtn;
		// @ formatter:off
		if (fragLen[0] == 0)
			return rtn.rating = -4;
		if (isTrumps)
			return rtn.rating = -3;
		// @ formatter:on

		return rtn.rating = (isEntryIntoPartner) ? 1 : 0;
	}

	/**
	 */
	public int calc_simpleOrigLenOrder() {
		// ==============================================================================================
		MpatRtn rtn = mpatRtn;
		// @ formatter:off
		if (fragLen[0] == 0)
			return rtn.rating = -4;
		if (isTrumps)
			return rtn.rating = -3;
		// @ formatter:on

		return rtn.rating = 20 + ourOrigCombLen - oppsOrigCombLen;
	}

	/**
	 */
	public int calc_topTricksButWithBadEntriesFirst() {
		// ==============================================================================================
		MpatRtn rtn = mpatRtn;
		// @ formatter:off
		if (fragLen[0] == 0)
			return rtn.rating = -4;
		if (isTrumps)
			return rtn.rating = -3;
		if (ourTopTricksCor == 0)
			return rtn.rating = -2;
		// @ formatter:on

		return rtn.rating = 10000 - 100 * ourCrossEntryEst + ourTopTricksCor - (ourOrigCombLen - oppsOrigCombLen);
	}

	/**
	 */
	public int calc_finesseSuitablity(int who) { // 0 = me, 1 = partner
		// ==============================================================================================
		MpatRtn rtn = mpatRtn;
		// @ formatter:off
		if (fragLen[who] == 0)
			return rtn.rating = -4;
		if (isTrumps)
			return rtn.rating = -3;
		if (rtn.matchEntryId == 0)
			return rtn.rating = -2;
		// @ formatter:on

		return rtn.rating = 20 + ourOrigCombLen - oppsOrigCombLen;
	}

	/**
	 */
	public int calc_crossruffSuitablity(int who) { // 0 = my, 1 = partner
		// ==============================================================================================
		MpatRtn rtn = mpatRtn;

		// @ formatter:off
		if (isTrumps)
			return 0;

		if (fragLen[who] == 0) // the whole point is to be able to lead them
			return 0;

		int oth = (who == Zzz.Me) ? Zzz.Pn : Zzz.Me;

		if (fragLen[oth] > 0) // and the other person should have none
			return 0;

		int losersInSuit = fragLen[who] - ourTopTricksCor;
		if (losersInSuit == 0) // we have all top tricks in this suit
			return 0;

		// @ formatter:on

		return rtn.rating = losersInSuit;
	}

}
