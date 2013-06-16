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
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;

import com.rogerpf.aabridge.controller.App;


/**
 * Deal
 */
public class Deal implements Serializable {
	//==============================================================================================

	private static final long serialVersionUID = -9001401454121134510L;
	
	transient public String origFilename = "";
	transient public NsSummary nsSummary = new NsSummary();

	public String description = "Your text here";

	public final Bid NULL_BID = new Bid(Zzz.NULL_BID);
	public final Bid PASS = new Bid(Zzz.PASS);
	public final Bid DOUBLE = new Bid(Zzz.DOUBLE);
	public final Bid REDOUBLE = new Bid(Zzz.REDOUBLE);

	public int boardNo = 0;
	public int dealer = 0; /* compass nesw */
	public boolean vunerability[] = { false, false }; // ns, ew
	public int contractCompass = -1;

	public Bid contract = NULL_BID;
	public Bid contractDblRe = NULL_BID;

	public final Hand[] hands = new Hand[4];
	public Hal prevTrickWinner = new Hal();

	public Cal packPristine = new Cal(52);

	public Hand[][] rota = new Hand[4][4];


	/**
	 */
	class FragSummary implements Comparable<FragSummary>{
		//==============================================================================================
		int suitValue = 0;
		int lLen = 0;
		int sLen = 0;
		public int both() {
			return lLen + sLen;
		}
		public int compareTo(FragSummary o) {
			return (o.lLen + o.sLen - (this.lLen + this.sLen));
		}
	}

	public class NsSummary {
		//---------------------------------- CLASS -------------------------------------
		Deal deal = null;
		int nsCombPoints = 0;
		public FragSummary fSum[] = {new FragSummary(), new FragSummary(), new FragSummary(), new FragSummary()};
		
		NsSummary(){
			deal = null;
		}

		public NsSummary(Deal d){
			deal = d;
			NsSummarize(d);
		}
		
		public int longestSuit() {
			return fSum[0].suitValue;
		}

		public void NsSummarize(Deal d) {
			
			deal = d;			
			nsCombPoints = d.north().countPoints() + d.south().countPoints();

			for (int i : Zzz.cdhs) {
				fSum[i].suitValue = i;
				int n = d.north().frags[i].size();
				int s = d.south().frags[i].size();
				if (n>s) {
					fSum[i].lLen = n;
					fSum[i].sLen = s;
				} else {
					fSum[i].lLen = s;
					fSum[i].sLen = n;
				}
			}
			Arrays.sort(fSum); // so the longest two pairs will be in the 0 and 1 slots
		}

		public boolean areTopTwoFourOrLonger() { // we are only interested in the top two suits
			return ((fSum[0].lLen >= 4) && (fSum[0].sLen >= 4) && (fSum[1].lLen >= 4) && (fSum[1].sLen >= 4));
		}

		public int Divergence(SuitShape[] sh) {
			int a = fSum[0].lLen - sh[0].lLen;
			int b = fSum[0].sLen - sh[0].sLen;
			int c = fSum[1].lLen - sh[1].lLen;
			int d = fSum[1].sLen - sh[1].sLen;
			return a*a + b*b + c*c + d*d;
		}
		
		int bestTrumpSuit() {	
			if (fSum[0].both() > fSum[1].both())
				return fSum[0].suitValue;
			// the top two are of equal (combined) length so we use a replicateable tie breaker
			//  of the parity of the sum of the cards of that suit in the north hand
			// this is the only use of the 'deal' field
			int tot = 0;
			for (Card cards : deal.north().frags[fSum[0].suitValue]) {
				tot += cards.faceValue;
			}
			return fSum[tot%2].suitValue;
		}
	}
		

	
	/**
	 */
	public Deal() { /* Constructor */
		//==============================================================================================

		// create the pack with the cards
		for (int suitValue : Zzz.cdhs) {
			for (int faceValue : Zzz.allThriteenCards) {
				packPristine.add(new Card(faceValue, suitValue));
			}
		}

		// Create the 4 seats and their hands
		for (int compass : Zzz.nesw) {
			hands[compass] = new Hand(this, compass);
		}

		// for EASE Of ACCESS fill the Hands rota
		// like the Zzz.rota for the indexes, but for the hands
		for (int row : Zzz.nesw) {
			int i = 0;
			for (int compass : Zzz.rota[row]) {
				rota[row][i++] = hands[compass];
			}
		}

		contractCompass = -1;
		contract = NULL_BID;
		contractDblRe = NULL_BID;

		setNextDealerAndVunerability(0);
	}

	// formatter:off
	public Hand north() {
		return hands[Zzz.NORTH];
	}

	public Hand east() {
		return hands[Zzz.EAST];
	}

	public Hand south() {
		return hands[Zzz.SOUTH];
	}

	public Hand west() {
		return hands[Zzz.WEST];
	}


	/** 
	 */
	public boolean isNewBetter_pointsAndAces(Deal dNew, boolean swapped, int min, int max, int aces) {
		//==============================================================================================
		int pointsNew = dNew.nsSummary.nsCombPoints;

		if (pointsNew < min || max < pointsNew || (dNew.nsHaveAtLeastSoManyAces(aces) == false)) {
			return false;
		}

		if (!swapped)
			return true; // we can't trust the origial yet, it may have too many points or too few aces

		// now we are in a position to do the points test as we know the current 'bestSoFar' (us) is fully valid

		return (pointsNew > this.nsSummary.nsCombPoints);
	}

	/**
	 */
	static public class SuitShape {
		//------------------------- CLASS ----------------------
		int lLen; 
		int sLen;  
		SuitShape (int l, int s) {
			lLen = l; sLen = s;
		}
	}

	/**
	 * 
	 */
	public boolean isNewBetter_pointsAcesAndShape(Deal dNew, boolean swapped, int min, int max, int aces, SuitShape[] sh) {
		//==============================================================================================
		int pointsNew = dNew.nsSummary.nsCombPoints;

		if (pointsNew < min || max < pointsNew || (dNew.nsHaveAtLeastSoManyAces(aces) == false)) {
			return false;
		}

		if (!swapped)
			return true; // we can't trust the origial yet, it may have too many points or too few aces
		
		if (dNew.nsSummary.areTopTwoFourOrLonger() == false)
			return false;
		
		int oldDiver = this.nsSummary.Divergence( sh);
		int newDiver = dNew.nsSummary.Divergence( sh);
		
		return newDiver < oldDiver;
	}
	

	/**
	 */
	public static Deal nextBoard(int prevBoardNo, boolean presetTheContract, String criteria) {
		//==============================================================================================
		Deal bestSoFar = new Deal();
		bestSoFar.setNextDealerAndVunerability(prevBoardNo);
		bestSoFar.redeal();
		Deal d = new Deal();
		d.setNextDealerAndVunerability(prevBoardNo);

		if (criteria.contentEquals("userBids")) {
			; // just use the bestSoFar as it is
		}

		if (criteria.startsWith("chosenGame")) {
			int min = 28;
			if (criteria.endsWith("27"))
				min = 27;
			if (criteria.endsWith("26"))
				min = 26;

			boolean swapped = false;
			for (int i = 0; i < 10000; i++) {
				d.redeal();
				if (bestSoFar.isNewBetter_pointsAndAces(d, swapped, min, min, 0)) {
					Deal tmp = bestSoFar;
					bestSoFar = d;
					d = tmp;
					if (!swapped) {
						swapped = true;
						continue;
					}

					if ((bestSoFar.north().countPoints() + bestSoFar.south().countPoints()) >= min) {
						break;
					}
				}
			}
		}

		if (criteria.startsWith("twoSuitSlam")) {
			int min = 28;
			int max = 30;
			SuitShape sh[] = new SuitShape[2];
			sh[0] = new SuitShape(5,4);
			sh[1] = new SuitShape(5,4);
			if (criteria.endsWith("_M")) {
				min = 29;
				max = 31;
				sh[0] = new SuitShape(5,4);
				sh[1] = new SuitShape(4,4);
			}
			if (criteria.endsWith("_H")) {
				min = 30;
				max = 32;
				sh[0] = new SuitShape(4,4);
				sh[1] = new SuitShape(4,4);
			}		

			boolean swapped = false;
			for (int i = 0; i < 10000; i++) {
				d.redeal();
				if (bestSoFar.isNewBetter_pointsAcesAndShape(d, swapped, min, max, 3, sh)) {
					Deal tmp = bestSoFar;
					bestSoFar = d;
					d = tmp;
					if (!swapped) {
						swapped = true;
						continue;
					}
	
					if (bestSoFar.nsSummary.Divergence( sh) == 0) {
						break;
					}
				}
			}
		}

		else if (criteria.startsWith("ntGrand") || criteria.startsWith("ntSmall")) {

			int min = 0;
			int max = 0;
			int aces = 0;
			
			if (criteria.contentEquals("ntSmall_M")) {
				min = 32;
				max = 34;
				aces = 3;
			}
			else if (criteria.contentEquals("ntGrand_E")) {
				min = 37;
				max = 40;
				aces = 4;
			}
			else if (criteria.contentEquals("ntGrand_M")) {
				min = 35;
				max = 36;
				aces = 4;
			}
			else if (criteria.contentEquals("ntGrand_H")) {
				min = 33;
				max = 34;
				aces = 4;
			} else {
				assert(false);
			}

			boolean swapped = false;
			int c=0;
			for (int i = 0; i < 10000; i++,c++) {
				d.redeal();
				if (bestSoFar.isNewBetter_pointsAndAces(d, swapped, min, max, aces)) {
					Deal tmp = bestSoFar;
					bestSoFar = d;
					d = tmp;					
					if (!swapped) {
						swapped = true;
						// continue;  we don't need this
					}
					
					int points = bestSoFar.nsSummary.nsCombPoints;

					if ((min <= points) && (points <= max)) {
						break;
					}
				}
			}
			System.out.println(c + " " + bestSoFar.nsSummary.nsCombPoints);
		}

		if (presetTheContract && (southBiddingRequired(criteria) == false)) {

			// add passes until we get to South
			for (Hand hand : bestSoFar.rota[bestSoFar.dealer]) {
				if (hand.compass == Zzz.SOUTH) {
					break;
				}
				bestSoFar.makeBid(bestSoFar.PASS);
			}

			bestSoFar.makeBid(bestSoFar.generateSouthBid(criteria));
			
			Bid bid = null;
			while (bestSoFar.isBidding()) {
				for (Hand hand : bestSoFar.rota[Zzz.WEST]) {
	
					if (hand.compass == Zzz.EAST) {
						bid = bestSoFar.generateEastWestBid(hand);
					}
					else if (hand.compass == Zzz.WEST) {
						bid = bestSoFar.generateEastWestBid(hand);
					}
					else if (hand.compass == Zzz.NORTH) {
						bid = bestSoFar.PASS;
					}
					else if (hand.compass == Zzz.SOUTH) {
						bid = bestSoFar.generateSouthBid(criteria);
					}
					bestSoFar.makeBid(bid);
					
					if (bestSoFar.isBidding() == false)
						break;
				}
			}		
		}

		return bestSoFar;
	}

	/**
	 */
	public static String validateDealCriteria(String in) {
		//==============================================================================================

		if (in.contentEquals("userBids"))
			return in;
		else if (in.contentEquals("chosenGame28"))
			return in;
		else if (in.contentEquals("chosenGame27"))
			return in;
		else if (in.contentEquals("chosenGame26"))
			return in;
		else if (in.contentEquals("twoSuitSlam_E"))
			return in;
		else if (in.contentEquals("twoSuitSlam_M"))
			return in;
		else if (in.contentEquals("twoSuitSlam_H"))
			return in;
		else if (in.contentEquals("ntSmall_M"))
			return in;
		else if (in.contentEquals("ntGrand_E"))
			return in;
		else if (in.contentEquals("ntGrand_M"))
			return in;
		else if (in.contentEquals("ntGrand_H"))
			return in;

		return "userBids";
	}

	/**
	 *   
	 */
	public static boolean southBiddingRequired(String criteria) {
		//==============================================================================================
		if (criteria.contentEquals("userBids")) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 *  
	 */
	public Bid generateSouthBid(String criteria) {
		//==============================================================================================
		Bid bid = null;
		
		Bid high = getHighestBid();
		
		if (high != PASS) {
			bid = PASS;
			Hand bidder = getHandThatMadeBid(high);
			if (bidder.axis() == hands[Zzz.SOUTH].axis()) {
				Bid dr = this.getLastDblOrRedblAfter(high);
				if (dr == DOUBLE && vunerability[0]) {
					bid = REDOUBLE;
				}
			}
		}

		else if (criteria.startsWith("chosenGame")) {
			bid = pickGoodNorthSouthGameContract();
		}

		else if (criteria.startsWith("twoSuitSlam")) {
			bid = pickGoodNorthSouthSlamContract();
		}

		else if (criteria.startsWith("ntSmall")) {
			bid = new Bid(6, Zzz.NOTRUMPS);
		}

		else if (criteria.startsWith("ntGrand")) {
			bid = new Bid(7, Zzz.NOTRUMPS);		
		}

		assert (bid != null);
		return bid;
	}

	/**
	 *  
	 */
	public Bid generateEastWestBid(Hand hand) {
		//==============================================================================================
		Bid bid = PASS;
		
		Bid high = getHighestBid();
		if (high != PASS) {
			Hand bidder = getHandThatMadeBid(high);
			if (bidder.axis() == hands[Zzz.SOUTH].axis()) {
				Bid dr = this.getLastDblOrRedblAfter(high);
				if (dr == NULL_BID) {
					// so now I can check to see if we are playing aginst & N
					if (high.suitValue == Zzz.NOTRUMPS && high.levelValue == 7) {
						if (hand.doesHandHaveAKingOrAce()) {
							bid = DOUBLE;
						}
					}
				}
			}
		}

		assert (bid != null);
		return bid;
	}

	
	/**
	 */
	boolean nsHaveAtLeastSoManyAces(int wanted) {
		//==============================================================================================
		if (wanted == 0)
			return true;

		int count = 0;

		for (Frag frag : north().frags) {
			if ((frag.isEmpty() == false) && (frag.get(0).faceValue == Zzz.ACE))
				count++;
		}
		for (Frag frag : south().frags) {
			if ((frag.isEmpty() == false) && (frag.get(0).faceValue == Zzz.ACE))
				count++;
		}
		return (count >= wanted);
	}

	/**
	 */
	public void setNextDealerAndVunerability(int oldBoardNo) {
		//==============================================================================================
		boardNo = oldBoardNo;
		boardNo++;
		if (boardNo > 16) {
			boardNo = 1;
		}
		Zzz.BoardData board = Zzz.getBoardData(boardNo);

		dealer = board.dealer;
		vunerability = board.vunerability;
	}

	/**
	 */

	public void redeal() {
		//==============================================================================================
		contractCompass = -1;
		contract = NULL_BID;
		contractDblRe = NULL_BID;

		for (Hand h : hands) {
			h.setToVirgin();
		}

		prevTrickWinner.clear();

		Cal pack = Cal.class.cast(packPristine.clone());

		Collections.shuffle(pack);

		while (!pack.isEmpty()) {
			for (Hand h : hands) {
				h.AddDeltCard(pack.remove(pack.size() - 1));
			}
		}

		// make NS have the best hands
		{
			Arrays.sort(hands); // the best two hands are now in pos 0 (North) and 1 (East)

			Hand x = hands[Zzz.SOUTH];
			hands[Zzz.SOUTH] = hands[Zzz.NORTH];
			hands[Zzz.NORTH] = hands[Zzz.EAST];
			hands[Zzz.EAST] = x;

			for (int compass : Zzz.nesw) {
				hands[compass].compass = compass;
			}

			// for EASE Of ACCESS - Create a Hands rota
			// like the Zzz.rota for the indexes, but for the hands
			for (int row : Zzz.nesw) {
				int i = 0;
				for (int compass : Zzz.rota[row]) {
					rota[row][i++] = hands[compass];
				}
			}
			
			nsSummary.NsSummarize(this);
		}

	}

	/**
	 */
	public Bid pickGoodNorthSouthGameContract() {
		//==============================================================================================
		int level;
		int suit;

		nsSummary = new NsSummary( this); // incase it is out of date e.g.  by  hand edit or load
		
		int longestCombLen  = nsSummary.fSum[0].both();
		int shortestCombLen = nsSummary.fSum[3].both();

		if ((longestCombLen == 7) || (longestCombLen == 8) && (shortestCombLen >= 6)) {
			suit = Zzz.NOTRUMPS;
			level = 3;
		}
		else {
			suit = nsSummary.bestTrumpSuit();
			level = (suit >= Zzz.HEARTS) ? 4 : 5;
			if (level == 5 && longestCombLen < 9) { // lets not play in an 8 card minor game
				suit = Zzz.NOTRUMPS;
				level = 3;
			}
		}

		return new Bid(level, suit);
	}

	/**
	 */
	public Bid pickGoodNorthSouthSlamContract() {
		//==============================================================================================

		nsSummary = new NsSummary( this); // incase it is out of date e.g.  by  hand edit or load

		int suit = nsSummary.bestTrumpSuit();
		int level = 6;

		return new Bid(level, suit);
	}

	/** 
	 */
	public void restoreTransientSuitChForClearerDebug() {
		//==============================================================================================
		for (Hand hand : hands) {
			for (Frag frag : hand.frags) {
				frag.suitCh = Zzz.suitValue_to_cdhsnCh[frag.suitValue];
			}
			for (Frag fOrg : hand.fOrgs) {
				fOrg.suitCh = Zzz.suitValue_to_cdhsnCh[fOrg.suitValue];
				for (Card card : fOrg) {
					card.suitCh = (char) Zzz.suitValue_to_cdhsnCh[card.suitValue];
				}
			}
			for (Bid bid : hand.bids) {
				if (bid.isCall() == false) {
					bid.suitCh = (char) Zzz.suitValue_to_cdhsnCh[bid.suitValue];
				}
			}
		}
	}

	/** 
	 */
	public String contractAndResult() {
		//==============================================================================================
		if (contract == NULL_BID) {
			return "Not-Yet-Bid";
		}
		if (contract == PASS) {
			return "Passed-Out";
		}

		Point score = App.deal.getContractTrickCountSoFar();

		String s;
		s = contract.getLevelCh() + "" + contract.getSuitCh() + doubleOrRedoubleStringX();
		s += "-by-" + Zzz.compass_to_nesw_st_long[App.deal.contractCompass] + "__";
		s += Zzz.compass_to_ns_ew_st[App.deal.contractCompass] + "-" + Integer.toString(score.x) + "_";
		s += Zzz.compass_to_ns_ew_st[(App.deal.contractCompass + 1) % 4] + "-" + Integer.toString(score.y);

		if (isFinished()) {
			s += "__";

			int trickDiff = score.x - (6 + contract.levelValue);

			if (trickDiff > 0) {
				s += Integer.toString(trickDiff) + "-Over";
			}
			else if (trickDiff == 0) {
				s += "Made";
			}
			else {
				s += Integer.toString(-trickDiff) + "-Down";
			}
		}

		return s;
	}

	/**
	 */
	public String doubleOrRedoubleString() {
		//==============================================================================================
		if (contractDblRe == DOUBLE)
			return new String("*");
		if (contractDblRe == REDOUBLE)
			return new String("**");
		return new String("");
	}

	/**
	 */
	public String doubleOrRedoubleStringX() {
		//==============================================================================================
		if (contractDblRe == DOUBLE)
			return new String("x");
		if (contractDblRe == REDOUBLE)
			return new String("xx");
		return new String("");
	}

	/**
	 */
	public Hand handFromCompass(int compass) {
		return hands[compass];
	}

	/**
	 */
	public Hand nextHand(Hand hand) {
		return hands[(hand.compass + 1) % 4];
	}

	/**
	 */
	public Hand prevHand(Hand hand) {
		return hands[(hand.compass + 3) % 4];
	}

	/**
	 */
	public boolean isBidding() {
		return (contract == NULL_BID);
	}

	/**
	 */
	public boolean isPlaying() {
		return ((contract != NULL_BID) && (contract != PASS) && (prevTrickWinner.size() < 14));
	}

	/**
	 */
	public boolean isFinished() {
		return ((contract == PASS) || (prevTrickWinner.size() == 14));
	}

	public boolean isTrumps(int suitValue) {
		return contract.suitValue == suitValue;
	}

	/**
	 */
	public boolean isLevelAllowed(int level) {
		Bid bid = getHighestBid();
		if (bid.getLevelValue() < level)
			return true;
		if ((bid.getLevelValue() == level) && (bid.suitValue < Zzz.NOTRUMPS))
			return true;
		return false;
	}

	/**
	 */
	public int getHighestLevelAllowed() {
		Bid bid = getHighestBid();
		if (bid.suitValue == Zzz.NOTRUMPS)
			return bid.getLevelValue() + 1;
		return bid.getLevelValue();
	}

	/**
	 */
	public boolean isCallAllowed(Bid b) {

		if (b == PASS)
			return true; // always OK

		assert (b == DOUBLE || b == REDOUBLE);
		Bid highest = getHighestBid();
		if (highest.isCall())
			return false; // invalid

		int bAxis = (getNextHandToBid().compass) % 2;
		Bid xorxx = getLastDblOrRedblAfter(highest);

		if (xorxx == REDOUBLE)
			return false; // nowhere to go after a redouble

		if (xorxx == NULL_BID) { // i.e there was NO *previous* Double or Redouble
			if (b == REDOUBLE)
				return false; // invalid
			int highAxis = (getHandThatMadeBid(highest).compass) % 2;
			if (bAxis == highAxis)
				return false; // doubles must be by the other axis
			return true;
		}

		if (xorxx == DOUBLE) {
			if (b == DOUBLE)
				return false; // invalid
			int highAxis = (getHandThatMadeBid(highest).compass) % 2;
			if (bAxis != highAxis)
				return false; // redoubles must be by the same axis as the core bid
			return true;
		}

		return false;
	}

	/**
	 */
	public void makeBid(Bid b) {
		//==============================================================================================
		if (b.isCall() == false) {
			Bid prev = getHighestBid();
			if (b.isLowerThanOrEqual(prev)) {
				// System.out.println(b + " is invalid - to low");
				return; // they should not have sent this really
			}
		}
		else {
			if (!isCallAllowed(b))
				return;
		}

		getNextHandToBid().bids.add(b);

		if (isAuctionFinished()) {

			assert (contract == NULL_BID);
			assert (prevTrickWinner.isEmpty());
			for (Hand hand : rota[Zzz.NORTH]) {
				assert (hand.played.isEmpty());
			}

			contract = getHighestBid();
			contractCompass = getHandThatMadePartnershipFirstCallDemom(contract).compass;
			contractDblRe = getLastDblOrRedblAfter(contract);

			// make the first entry, which shows the leader to the first trick
			prevTrickWinner.add(hands[(contractCompass + 1) % 4]);
		}
	}

	/**
	 */
	public void wipePlay() {
		// set the frags to match the original cards and wipe the played
		for (Hand hand : rota[Zzz.NORTH]) {
			for (int i = 0; i < 4; i++) {
				hand.frags[i] = (Frag) hand.fOrgs[i].clone();
			}
			hand.played.clear();
		}
		
		// leave only the original leader
		while (prevTrickWinner.size() > 1) {
			prevTrickWinner.removeLast();
		}
	}

	/**
	 */
	public void wipeContractAndPlay() {
		// set the frags to match the original cards and wipe the played
		for (Hand hand : rota[Zzz.NORTH]) {
			for (int i = 0; i < 4; i++) {
				hand.frags[i] = (Frag) hand.fOrgs[i].clone();
			}
			hand.played.clear();
		}
		prevTrickWinner.clear();
		contract = App.deal.NULL_BID;
		contractDblRe = App.deal.NULL_BID;
		contractCompass = 0;
	}

	/**
	 */
	public void wipeContractBiddingAndPlay() {
		// set the frags to match the original cards and wipe the played
		for (Hand hand : rota[Zzz.NORTH]) {
			for (int i = 0; i < 4; i++) {
				hand.frags[i] = (Frag) hand.fOrgs[i].clone();
			}
			hand.bids.clear();
			hand.played.clear();
		}
		prevTrickWinner.clear();
		contract = App.deal.NULL_BID;
		contractDblRe = App.deal.NULL_BID;
		contractCompass = 0;
		// the dealer has been preset by the user, that is why this routine exists
	}

	/**
	 */
	public void finishBiddingIfIncomplete() {

		while (isAuctionFinished() == false) {
			makeBid(PASS);
		}
	}

	/**
	 */
	public Hand getNextHandToBid() {
		Hand h = null;
		int size = 999, sn;
		for (Hand hand : rota[dealer]) {
			if ((sn = hand.bids.size()) < size) {
				h = hand;
				size = sn;
			}
		}
		return h;
	}

	/**
	 */
	public int countBids() {
		int n = 0;
		for (Hand hand : rota[dealer]) {
			n += hand.bids.size();
		}
		return n;
	}

	/**
	 */
	public Hand getNthHandToBid(int n) {
		Hand h = null;
		int size = 999, sn;
		for (Hand hand : rota[dealer]) {
			if ((sn = hand.bids.size()) < size) {
				h = hand;
				size = sn;
			}
		}
		return h;
	}

	/**
	 */
	public Hand getLastHandThatBid() {
		Hand hn = getNextHandToBid();
		Hand hand = hn.prevHand();
		return (hand.bids.size() == 0) ? null : hand;
	}

	/**
	 */
	public void removeAnyFinalPasses() {
		while (true) {
			Hand hand = getLastHandThatBid();
			if (hand == null || hand.bids.size() == 0)
				break;
			if (hand.bids.getLast() != PASS)
				break;
			hand.bids.removeLast();
		}
		// NOTE any contract and existing play is preserved
	}

	/**
	 */
	public Hand getLastHandThatPlayed() {
		if (isCurTrickComplete()) {
			if (hands[0/* any */].played.size() == 0)
				return null;
			return prevTrickWinner.get(prevTrickWinner.size() - 2).prevHand();
		}
		else {
			Hand leader = getCurTrickLeader();
			for (Hand hand : rota[leader.compass]) {
				if (hand.played.size() < leader.played.size())
					return hand.prevHand();
			}
		}
		assert (false);
		return null;
	}

	/**
	 */
	private Hand getHandThatMadeBid(Bid bid) {

		int rounds = hands[dealer].bids.size();
		for (int r = 0; r < rounds; r++) {
			for (Hand hand : rota[dealer]) {
				if (hand.bids.size() == r)
					break;
				if (hand.bids.get(r) == bid)
					return hand;
			}
		}
		assert (false);
		return null;
	}

	private Hand getHandThatMadePartnershipFirstCallDemom(Bid bid) {

		Hand bidHand = getHandThatMadeBid(bid);

		int rounds = hands[dealer].bids.size();
		for (int r = 0; r < rounds; r++) {
			for (Hand hand : rota[dealer]) {
				if (hand.bids.size() == r)
					break;
				if (hand.axis() != bidHand.axis())
					continue;
				if (hand.bids.get(r).suitValue == bid.suitValue)
					return hand;
			}
		}
		assert (false);
		return null;
	}

	/**
	 */
	private Bid getLastDblOrRedblAfter(Bid bid) {

		Bid last = NULL_BID;
		boolean bidFound = false;
		int rounds = hands[dealer].bids.size();
		for (int r = 0; r < rounds; r++) {
			for (Hand hand : rota[dealer]) {
				if (hand.bids.size() == r)
					break;
				Bid b = hand.bids.get(r);
				if (!bidFound) {
					if (b != bid)
						continue;
					bidFound = true;
				}
				if (b.isCall() == false)
					continue;
				if (b == PASS)
					continue;
				last = b;
			}
		}
		return last;
	}

	/**
	 */
	private Bid getHighestBid() {
		Bid pb = PASS;
		int rounds = hands[dealer].bids.size();

		for (int r = 0; r < rounds; r++) {
			for (Hand hand : rota[dealer]) {
				if (hand.bids.size() == r)
					break;
				Bid cb = hand.bids.get(r);
				if (cb.isCall())
					continue;
				pb = cb;
			}
		}
		return pb;
	}

	/**
	 */
	public boolean isAuctionFinished() {

		int rounds = hands[dealer].bids.size();
		int count = 0;
		int skip = 0;
		for (int r = 0; r < rounds; r++) {
			for (Hand hand : rota[dealer]) {
				if (hand.bids.size() == r)
					return false;
				Bid cb = hand.bids.get(r);
				if ((skip++ != 0) && (cb == PASS))
					count++;
				else
					count = 0;
				if (count == 3)
					return true;
			}
		}
		return false;
	}

//	/**
//	 */
//	private void testPlay(int tricks, int cards) {
//
//		cards = (tricks * 4 + cards) % 52;
//		tricks = cards / 4;
//		cards = cards % 4;
//
//		for (int t = 0; t < tricks; t++) {
//			int onLead = prevTrickWinner.get(t).compass;
//			for (int j = 0; j < 4; j++) {
//				Hand hand = hands[(onLead + j) % 4];
//				Card card = hand.dumbAuto();
//				hand.playCard(card);
//			}
//		}
//
//		int onLead = prevTrickWinner.get(getCurTrickIndex()).compass;
//		for (int c = 0; c < cards; c++) {
//			Hand hand = hands[(onLead + c) % 4];
//			Card card = hand.dumbAuto();
//			hand.playCard(card);
//		}
//	}
//
	/**
	 */
	public int getCurTrickIndex() {
		return prevTrickWinner.size() - 1;
	}

	/**
	 */
	public int getCurCardIndex() {
		Hand leader = getCurTrickLeader();
		int i = -1;
		for (Hand hand : rota[leader.compass]) {
			i++;
			if (hand.played.size() == getCurTrickIndex()) {
				return i;
			}
		}
		return 0; // all four cards are pressent ?
	}

	/**
	 */
	public Hand getCurTrickLeader() {
		return prevTrickWinner.getLast();
	}

	/**
	 */
	public Hand getNextHandToAct() {
		if (isBidding())
			return getNextHandToBid();
		else if (isPlaying())
			return getNextHandToPlay();
		else
			return null;
	}

	/**
	 */
	public Hand getNextHandToPlay() {
		int curTrickIndex = getCurTrickIndex();
		Hand leader = getCurTrickLeader();
		for (Hand hand : rota[leader.compass]) {
			if (hand.played.size() == curTrickIndex) {
				return hand;
			}
		}
		assert (false);
		return null; // should never happen
	}

	/**
	 */
	public boolean isCurTrickComplete() {
		return (getCurTrickIndex() == hands[0].played.size()) && (haveAllHandsPlayedTheSameNumberOfCards() == true);
	}

	/**
	 */
	public boolean haveAllHandsPlayedTheSameNumberOfCards() {
		int s[] = { 0, 0, 0, 0 };
		for (int compass : Zzz.nesw) {
			s[compass] = hands[compass].played.size();
		}
		return ((s[0] == s[1]) && (s[1] == s[2]) && (s[2] == s[3]));
	}

	/**
	 */
	public boolean lessThanTwoCardsPlayed() {
		return (countCardsPlayed() < 2);
	}

	/**
	 */
	public int countCardsPlayed() {
		int t = 0;
		for (Hand hand : hands) {
			t += hand.played.size();
		}
		return t;
	}

	/**
	 */
	public int countTrumpsPlayed() {
		int t = 0;
		for (Hand hand : hands) {
			t += hand.played.countSuit(contract.suitValue);
		}
		return t;
	}

	/**
	 */
	public String getPlayerNameFromPhypos(int phyPos) {
		return hands[App.compassFromPhyScreenPos(phyPos)].playerName;
	}

	/**
	 */
	public boolean isCurTrickThreeCards() {
		Hand leader = getCurTrickLeader();
		int s[] = { 0, 0, 0, 0 };
		for (int seat : Zzz.rota[leader.compass]) {
			s[seat] = hands[seat].played.size();
		}
		return ((s[0] == s[1]) && (s[1] == s[2]) && (s[2] == s[3] + 1));
	}

	/**
	 */
	public Point getContractTrickCountSoFar() {
		Point trickCount = new Point(); // comes set to 0,0
		boolean skipFirst = true;
		int plA = contractCompass;
		int plB = (contractCompass + 2) % 4;
		int opA = (contractCompass + 1) % 4;
		int opB = (contractCompass + 3) % 4;

		for (Hand winner : prevTrickWinner) {
			if (skipFirst) {
				skipFirst = false;
				continue;
			} // skip the first as it the leader to the first trick
			if (winner.compass == plA || winner.compass == plB)
				trickCount.x++;
			if (winner.compass == opA || winner.compass == opB)
				trickCount.y++;
		}
		return trickCount;
	}

	/**
	 */
	void cardJustPlayed() {

		if (haveAllHandsPlayedTheSameNumberOfCards()) {

			// calc And Set CurTrickWinner
			int curTrickIndex = getCurTrickIndex();
			Hand leader = getCurTrickLeader();
			Hand bestHand = leader;
			Card bestCard = leader.played.getCard(curTrickIndex);

			for (Hand hand : hands) {
				if (hand == leader)
					continue; // skip our start
				Card card = hand.played.getCard(curTrickIndex);
				if (card.isBetterThan(bestCard, contract.suitValue)) {
					bestHand = hand;
					bestCard = card;
				}
			}
			prevTrickWinner.add(bestHand);
		}
	}

	/**
	 */
	public boolean isNextCardLastInTrick() {
		return isCurTrickThreeCards();
	}

	/**
	 */
	public Hand getHand(int compass) {
		return hands[compass];
	}

	/**
	 */
	public Point getBoardScore() {
		Point score = new Point(0, 0);

		if (contract == PASS || !isFinished())
			return score;

		int reMult = (contractDblRe == REDOUBLE) ? 2 : 1;

		int trickDiff = getContractTrickCountSoFar().x - (contract.levelValue + 6);

		if (trickDiff >= 0) { // they made
			score.y = contract.levelValue * Zzz.scoreRate[contract.suitValue] + ((contract.suitValue == Zzz.NOTRUMPS) ? 10 : 0);

			int vunMult = (vunerability[contractCompass % 2]) ? 2 : 1;
			
			if (contract.levelValue == 7) {
				score.x = 750 * vunMult;
			}
			else if (contract.levelValue == 6) {
				score.x = 500 * vunMult;
			}
			else {
				score.x = 0;
			}
			
			if (score.y >= 100) {
				score.x += (vunMult == 2) ? 500 : 300;
			}
			else {
				score.x += 50;
			}

			if (contractDblRe == NULL_BID) {
				score.x += trickDiff * Zzz.scoreRate[contract.suitValue];
			}
			else {
				score.x += (50 + (50 * trickDiff * vunMult)) * reMult;
			}
		}
		else { // they went down trickDiff is NEGATIVE

			// score.y is always nil

			if (contractDblRe == NULL_BID) {
				score.x = 50 * trickDiff;
			}
			else if (vunerability[contractCompass % 2] == false) {
				score.x = -100 * reMult;
				trickDiff++;
				if (trickDiff < 0) {
					score.x += -200 * reMult;
					trickDiff++;
				}
				if (trickDiff < 0) {
					score.x += -200 * reMult;
					trickDiff++;
				}
				if (trickDiff < 0) {
					score.x += -300 * reMult * (-1) * trickDiff;
				}
			}
			else { // the bidders are vunerable
				score.x = -200 * reMult;
				trickDiff++;
				if (trickDiff < 0) {
					score.x += -300 * reMult * (-1) * trickDiff;
				}

			}

		}

		// swap from the contract owner to "physical south" direction

		if (App.deal.hands[App.compassFromPhyScreenPos(Zzz.SOUTH)].compass % 2 != contractCompass % 2) {
			score.x *= -1;
			score.y *= -1;
		}

		return score;
	}

	/** 
	 * We are counting all the played cards of given suit.  This is used at 
	 * DISCARD calculation time.  So if a card is played on this round it is inactive
	 * because by def we cannot follow to the led suit so neither could the player of that card.
	 */
	int countPlayedOfSuit(int suitValue) {
		int count = 0;

		int trick = -1;
		for (Hand leader : prevTrickWinner) {
			trick++;
			for (Hand hand : rota[leader.compass]) {
				if (hand.played.size() >= trick)
					break;
				if (hand.played.get(trick).suitValue == suitValue) {
					count++;
				}
			}
		}
		return count;
	}

	/** 
	 */
	boolean isActiveCard(Frag fragSkip, int faceValue, int suitValue) {

		for (Hand hand : hands) {
			if (hand.frags[suitValue] == fragSkip)
				continue;
			if (hand.frags[suitValue].getIfFaceExists(faceValue) != null) {
				return true;
			}
			if (hand.played.size() == prevTrickWinner.size() && hand.played.getLast().matches(faceValue, suitValue)) {
				return true; // it is in the current trick
			}
		}
		return false; // not in the hands so it has been played
	}

	/** 
	 */
	boolean isActiveCardExcludeTrick(Frag fragSkip, int faceValue, int suitValue) {

		for (Hand hand : hands) {
			if (hand.frags[suitValue] == fragSkip)
				continue;
			if (hand.frags[suitValue].getIfFaceExists(faceValue) != null) {
				return true;
			}
//			if (hand.played.size() == prevTrickWinner.size() && hand.played.getLast().matches(faceValue, suitValue)) {
//				return true; // it is in the current trick
//			}
		}
		return false; // not in the hands so it has been played
	}

	/** 
	 *  Set the relative face value for all cards in the hands
	 */
	public void setAllFaceRel() {

		for (Card c : packPristine) {
			c.faceRel = 0;
		}

		// tempoarly return all played cards (in this trick) max 3 to their hand

		for (Hand hand : hands) {
			if (hand.played.size() == prevTrickWinner.size()) {
				Card card = hand.played.getLast();
				hand.frags[card.suitValue].addDeltCard(card);
			}
		}

		for (Hand hand : hands) {
			for (Frag frag : hand.frags) {
				int inactive = 0;
				int topFace = Zzz.ACE + 1;
				for (Card c : frag) {
					for (int i = c.faceValue + 1; i < topFace; i++) {
						if (isActiveCardExcludeTrick(frag, i, c.suitValue) == false) {
							inactive++;
						}
					}
					topFace = c.faceValue; // next search starts from the prev card
					c.faceRel = c.faceValue + inactive;
				}
			}
		}

		for (Hand hand : hands) {
			if (hand.played.size() == prevTrickWinner.size()) {
				Card card = hand.played.getLast();
				hand.frags[card.suitValue].remove(card);
			}
		}

	}

	/** 
	 */
	public void moveCardToHandDragTime(Card cardIn, Hand handTo) {
		int faceValue = cardIn.faceValue;
		int suitValue = cardIn.suitValue;
		Card card = null;
		Hand handFrom = null;

		for (Hand hand : rota[Zzz.NORTH]) {
			// We scan the original cards, of course
			card = hand.fOrgs[suitValue].getIfFaceExists(faceValue);
			if (card != null) {
				if (handTo == hand) {
					return; // Already in the correct hand
				}
				handFrom = hand;
				break;
			}
		}

		// select an unwanted card (i.e. the lowest) from the 'To' hand
		Card unwanted = null;
		for (Frag fr : handTo.fOrgs) { // clubs are first
			if (fr.size() > 0) {
				unwanted = fr.getLast();
				break;
			}
		}
		assert (unwanted != null);

		handFrom.fOrgs[suitValue].remove(card);
		handTo.fOrgs[suitValue].addDeltCard(card);

		handTo.fOrgs[unwanted.suitValue].remove(unwanted);
		handFrom.fOrgs[unwanted.suitValue].addDeltCard(unwanted);

		// set the frags to match the original cards and wipe the played
		for (Hand hand : rota[Zzz.NORTH]) {
			for (int i = 0; i < 4; i++) {
				hand.frags[i] = (Frag) hand.fOrgs[i].clone();
			}
			hand.played.clear();
		}

		// leave only the original leader
		while (prevTrickWinner.size() > 1) {
			prevTrickWinner.removeLast();
		}
	}


	/** 
	 */
	public void moveCardToHandDuringPlay(Card cardIn, Hand handTo, Card altCard) {
		int faceValue = cardIn.faceValue;
		int suitValue = cardIn.suitValue;
		Card card = null;
		Hand handFrom = null;
	
		for (Hand hand : rota[Zzz.NORTH]) {
			// We scan the original cards, of course
			card = hand.fOrgs[suitValue].getIfFaceExists(faceValue);
			if (card != null) {
				if (handTo == hand) {
					return; // Already in the correct hand
				}
				handFrom = hand;
				break;
			}
		}
	
		assert (altCard != null);
	
		handFrom.frags[suitValue].remove(card);
		handTo.frags[suitValue].addDeltCard(card);
	
		handTo.frags[altCard.suitValue].remove(altCard);
		handFrom.frags[altCard.suitValue].addDeltCard(altCard);
	
		handFrom.fOrgs[suitValue].remove(card);
		handTo.fOrgs[suitValue].addDeltCard(card);
	
		handTo.fOrgs[altCard.suitValue].remove(altCard);
		handFrom.fOrgs[altCard.suitValue].addDeltCard(altCard);
	}
}
