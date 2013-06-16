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

import java.util.Arrays;

public class PlayGen {
	
	//@formatter:off

	Hand    leader              ;
	boolean leaderUs 			;
	int     trickNumb 			;
	Card    cardLed 			;
	int 	faceValueLed 		;
	int 	suitValueLed 		;
	int 	trumpSuit 			;
	boolean noTrumps 			;
	boolean areWeWinning 		;
	boolean ourContract 		;
	int 	myTrumps 			;
	int 	partnersTrumps 		;
	boolean haveLedSuit 		;
	boolean haveTrumps 			;
	int 	outstandingTrumps	;
	Hand 	bestCardPlayer 		;
	Card 	bestCard 			;
	boolean bestByTrumping 		;
	                        	
	Card    lowestTrump 		;
	Card    lowestOfLed			;
	Card    highestTrump 		;
	Card    highestOfLed		;
	
	Hand	partner				;
	Hand	LHO					;
	Hand	RHO					;
	
	boolean LHO_haveLedSuit 	;
	boolean LHO_haveTrumps 		;
	
	Frag[] bothHands			;
	Frag[] coct;
	Frag[] oct;
	Frag[] ct;
	

	PlayGen(Hand h) { // Constructor and everything
		
		Deal deal = h.deal;
	
		deal.setAllFaceRel(); // must do this first
	
		leader 				= deal.getCurTrickLeader();
		leaderUs 			= (h == leader);
		trickNumb 			= deal.getCurTrickIndex();
		cardLed 			= (leaderUs) ? null : leader.played.getLast();
		faceValueLed 		= (cardLed == null) ? -1 : cardLed.faceValue;
		suitValueLed 		= (cardLed == null) ? -1 : cardLed.suitValue;
		trumpSuit 			= deal.contract.suitValue;
		noTrumps 			= (trumpSuit == Zzz.NOTRUMPS);
		areWeWinning 		= (cardLed == null) ? false : h.areWeWinning();
		ourContract 		= (deal.contractCompass % 2) == h.axis();
		myTrumps 			= (noTrumps) ? -1 : h.frags[trumpSuit].size();
		partnersTrumps 		= (noTrumps) ? -1 : h.partner().frags[trumpSuit].size();
		haveLedSuit 		= (cardLed == null) ? false : (h.frags[suitValueLed].size() > 0);
		haveTrumps 			= (!noTrumps && (myTrumps > 0));
		outstandingTrumps	= 13 - deal.countTrumpsPlayed() - partnersTrumps - myTrumps;
		bestCardPlayer 		= (leaderUs) ? null : h.getBestCardOfTrickPlayer();
		bestCard 			= (leaderUs) ? null : bestCardPlayer.played.getLast();
		bestByTrumping 		= (leaderUs) ? false : suitValueLed != bestCard.suitValue;
		                	
		lowestTrump 		= (noTrumps) ? null : h.frags[trumpSuit].getLast();
		lowestOfLed			= (leaderUs) ? null : h.frags[suitValueLed].getLast();
		highestTrump 		= (noTrumps) ? null : h.frags[trumpSuit].getCard(0);
		highestOfLed		= (leaderUs) ? null : h.frags[suitValueLed].getCard(0);
		
		partner				= h.partner();
		LHO					= h.LHO();
		RHO					= h.RHO();
		
		LHO_haveLedSuit 	= (cardLed == null) ? false : (LHO.frags[suitValueLed].size() > 0);
		LHO_haveTrumps 		= (!noTrumps && ((LHO.frags[trumpSuit].size() > 0)));
		
		bothHands = new Frag[4];
		bothHands[0] = (Frag)(h.frags[0].clone());
		bothHands[1] = (Frag)(h.frags[1].clone());
		bothHands[2] = (Frag)(h.frags[2].clone());
		bothHands[3] = (Frag)(h.frags[3].clone());	
		h.addPartnersCurrentCards( bothHands);
		Arrays.sort(bothHands, Frag.ContigiousnessTrumpsLast);
		
		coct = h.frags.clone();
		Arrays.sort(coct, Frag.CombinedOrigThenCurLenTrumpsLast);

		oct = h.frags.clone();
		Arrays.sort(oct, Frag.OrigThenCurLenTrumpsLast);

		ct = h.frags.clone();
		Arrays.sort(ct, Frag.CurLenTrumpsLast);
		
		//@formatter:on
	}
}
