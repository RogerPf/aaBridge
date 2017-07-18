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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.dds.Z_ddsCalculate;
import com.rogerpf.aabridge.model.Play_Mpat.Mpat;
import com.rogerpf.aabridge.model.Zzz.BoardData;

/**
 * Deal
 */
public class Deal {
	// ---------------------------------- CLASS -------------------------------------

	public String lastSavedAsPathWithSep = "";
	public String lastSavedAsFilename = "";
	public String lastDealNameSaved_FULL = "";
	public NsSummary nsSummary = new NsSummary();
	public int testId = 0;
	public int cycle = 0; // debug message use only
	public boolean hideFinish = false;
	public String linRowText = "";
	public String linResult = "";
	public boolean youSeatInLoadedLin = false;
	public long localId;
	public int localLast_pg = 0;

	public final static String makeDoneHand = "makeDoneHand";

	public String ahHeader = ""; // Aaa.YourTextHere;

	public int realBoardNo = 0;
	public String displayBoardId = "";
	public String signfBoardId = "";
	public Dir dealer = Dir.Invalid; /* compass.v nesw */
	public boolean vulnerability[] = { false, false }; // ns, ew
	public Dir contractCompass = Dir.Invalid;
	public boolean endedWithClaim = false;
	public int tricksClaimed = 0;

	public boolean tc_suppress_pc_display = false;

	public Bid contract = new Bid(Call.NullBid);
	public Bid contractDblRe = new Bid(Call.NullBid);

	public final Hand[] hands = new Hand[4];
	public Hal prevTrickWinner = new Hal();

	public Cal packPristine = new Cal(52);

	public Hand[][] rota = new Hand[4][4];

	public Dir youSeatHint = Dir.South; // not part of the model but provided as a aid to the users of the model
	private boolean doneHand = false;
	public boolean changed = false;
	public boolean eb_blocker = false;
	public int eb_min_card = 0;
	public boolean showBidQuestionMark = false;
	public int columnsInBidTable = 4;
	public char qx_room = '-'; // '-' or 'o' or 'c'
	public int qx_number = 0; // valid if 1 or higher

	public String blueScore = "";
	public String purpleScore = "";

	public boolean dfcDeal = false;
	public int forceDifferent = 0; // used to stop do_tutorialIntoDealClever searching too far

	/**
	 */
	public static class DumbAutoDirectives {
		// ---------------------------------- CLASS -------------------------------------
		public boolean yourFinessesMostlyFail = false;
		public int defenderSignals = Zzz.NoSignals;

		public DumbAutoDirectives() { // constructor
		}

		public DumbAutoDirectives(boolean yourFin, int defSig) { // constructor
			yourFinessesMostlyFail = yourFin;
			defenderSignals = defSig;
		}
	}

	/**
	 */
	class FragSummary implements Comparable<FragSummary> {
		// ---------------------------------- CLASS -------------------------------------
		Suit suit = Suit.Invalid;
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
		// ---------------------------------- CLASS -------------------------------------
		Deal deal = null;
		int nsCombPoints = 0;
		public FragSummary fSum[] = { new FragSummary(), new FragSummary(), new FragSummary(), new FragSummary() };

		NsSummary() {
			deal = null;
		}

		public NsSummary(Deal d) {
			deal = d;
			NsSummarize(d);
		}

		public Suit longestSuit() {
			return fSum[0].suit;
		}

		public void NsSummarize(Deal d) {

			deal = d;
			nsCombPoints = d.north().count_HighCardPoints() + d.south().count_HighCardPoints();

			for (Suit suit : Suit.cdhs) {
				int i = suit.v;
				fSum[i].suit = suit;
				int n = d.north().frags[i].size();
				int s = d.south().frags[i].size();
				if (n > s) {
					fSum[i].lLen = n;
					fSum[i].sLen = s;
				}
				else {
					fSum[i].lLen = s;
					fSum[i].sLen = n;
				}
			}
			Arrays.sort(fSum); // so the longest two pairs will be in the 0 and 1 slots
		}

		public boolean areTopTwo_4444_OrLonger() { // we are only interested in the top two suits
			return ((fSum[0].lLen >= 4) && (fSum[0].sLen >= 4) && (fSum[1].lLen >= 4) && (fSum[1].sLen == 4));
		}

		public boolean areTopTwo_4343_OrLonger() { // we are only interested in the top two suits
			return ((fSum[0].lLen >= 4) && (fSum[0].sLen >= 3) && (fSum[1].lLen >= 4) && (fSum[1].sLen >= 3));
		}

		public boolean areTopTwo_5344_OrLonger() { // we are only interested in the top two suits
			return ((fSum[0].lLen >= 5) && (fSum[0].sLen >= 3) && (fSum[1].lLen >= 4) && (fSum[1].sLen == 4));
		}

		public int Divergence(SuitShape[] sh) {
			int a = fSum[0].lLen - sh[0].lLen;
			int b = fSum[0].sLen - sh[0].sLen;
			int c = fSum[1].lLen - sh[1].lLen;
			int d = fSum[1].sLen - sh[1].sLen;
			return a * a + b * b + c * c + d * d;
		}

		Suit bestTrumpSuit() {
			if (fSum[0].both() > fSum[1].both())
				return fSum[0].suit;
			// the top two are of equal (combined) length so we use a replicateable tie breaker
			// of the parity of the sum of the cards of that suit in the north hand
			// this is the only use of the 'deal' field
			int tot = 0;
			for (Card cards : deal.north().frags[fSum[0].suit.v]) {
				tot += cards.rank.v;
			}
			return fSum[tot % 2].suit;
		}
	}

	/**
	 */
	// ==============================================================================================
	public boolean isIncomplete() {
		// used to tell if a tutorial is trying to display just one suit
		int tot = 0;

		for (Dir dir : Dir.nesw) {
			for (Suit suit : Suit.cdhs) {
				tot += hands[dir.v].fOrgs[suit.v].size();
			}
		}
		return (0 < tot && tot < 52);
	}

	/**
	 */
	// ==============================================================================================
	public boolean isDoneHand() { // the Done hand has broken (empty!) fOrgs
		return doneHand;
	}

	/**
	 */
	// ==============================================================================================
	public boolean isSaveable() { // Have to have 52 cards
		int tot = 0;

		for (Dir dir : Dir.nesw) {
			for (Suit suit : Suit.cdhs) {
				tot += hands[dir.v].fOrgs[suit.v].size();
			}
		}
		return tot == 52;
	}

//	/**
//	 */
//	// ==============================================================================================
//	public boolean didAnyHandStartWith13Cards() {
//		for (Dir dir : Dir.nesw) {
//			if (hands[dir.v].didHandStartWith13Cards()) {
//				return true;
//			}
//		}
//		return false;
//	}

	/**
	 */
	public Deal() { /* Constructor */// used in emergency only when loading bad lins
		// ==============================================================================================
		this.localId = 0;
		localLast_pg = 0;
	}

	/**
	 */
	public Deal(int localId /* ignored */) { /* Constructor */// used by Make_giAY
		// ==============================================================================================
		this.localId = idCounter.getAndIncrement();

		localLast_pg = 0;

		deal_common("", Dir.South);
		setNextDealerAndVulnerability(16 /* ticks over from this */);
	}

	/**
	 */
	public Deal(String dealType, Dir youSeatHint) { /* normall Constructor */
		// ==============================================================================================
		localId = idCounter.getAndIncrement();
		localLast_pg = 0;

		deal_common(dealType, youSeatHint);
	}

	public static AtomicLong idCounter = new AtomicLong();

	/**
	 */
	public void deal_common(String dealType, Dir youSeatHint) { /* Constructor */
		// ==============================================================================================

		localLast_pg = 0;

		this.youSeatHint = youSeatHint;

		// create the pack with the cards
		for (Suit suit : Suit.cdhs) {
			for (Rank rank : Rank.allThriteenRanks) {
				packPristine.add(new Card(rank, suit));
			}
		}

		// Create the 4 seats and their hands
		for (Dir compass : Dir.nesw) {
			hands[compass.v] = new Hand(this, compass);
		}

		// for EASE Of ACCESS fill the Hands rota
		// like the Dir.rota for the indexes, but for the hands
		for (Dir row : Dir.nesw) {
			int i = 0;
			for (Dir col : Dir.rota[row.v]) {
				rota[row.v][i++] = hands[col.v];
			}
		}

		contractCompass = Dir.Invalid;
		contract = new Bid(Call.NullBid);
		contractDblRe = new Bid(Call.NullBid);

		setNextDealerAndVulnerability(15);

		if (dealType != makeDoneHand)
			return;

		doneHand = true;

		// From here on we make the - DONE HAND

		// West
		for (int i = 51; i > 38; i--) {
			west().frags[Suit.Spades.v].addDeltCard(packPristine.removeCard(i));
		}
		// East
		for (int i = 38; i > 33; i--) {
			east().frags[Suit.Hearts.v].addDeltCard(packPristine.removeCard(i));
		}
		for (int i = 25; i > 21; i--) {
			east().frags[Suit.Diamonds.v].addDeltCard(packPristine.removeCard(i));
		}
		for (int i = 12; i > 8; i--) {
			east().frags[Suit.Clubs.v].addDeltCard(packPristine.removeCard(i));
		}

		// North South
		while (packPristine.size() > 0) {
			north().addDeltCard(packPristine.removeCard((int) (Math.random() * packPristine.size())));
			south().addDeltCard(packPristine.removeCard((int) (Math.random() * packPristine.size())));
		}

		// we are set as board 16 - so West is the dealer

		makeBid(new Bid(Call.Pass));
		makeBid(new Bid(Call.Pass));
		makeBid(new Bid(Call.Pass));
		makeBid(new Bid(Level.Seven, Suit.NoTrumps));
		makeBid(new Bid(Call.Pass));
		makeBid(new Bid(Call.Pass));
		makeBid(new Bid(Call.Pass));

		// now, which is the whole point, we can play out the hand to the end by ONLY RANDOM cards
		// the dumb auto player can remain unused and so easier to test using the 'tests'.

		endedWithClaim = true;
		tricksClaimed = 0;

	}

	public Hand north() {
		return hands[Dir.North.v];
	}

	public Hand east() {
		return hands[Dir.East.v];
	}

	public Hand south() {
		return hands[Dir.South.v];
	}

	public Hand west() {
		return hands[Dir.West.v];
	}

	public int contractAxis() {
		return contractCompass.v % 2;
	}

	public int defenderAxis() {
		return (contractCompass.v + 1) % 2;
	}

	public int tricksOver() {
		if (!contract.isValidBid() || isFinished() == false)
			return 0;
		return getContractTrickCountSoFar().x - (contract.level.v + 6);
	}

	/** 
	 */
	public boolean isNewBetter_pointsAndAces(Deal dNew, boolean swapped, int min, int max, int aces) {
		// ==============================================================================================
		int pointsNew = dNew.nsSummary.nsCombPoints;

		if (pointsNew < min || max < pointsNew || (dNew.nsHaveAtLeastSoManyAces(aces) == false)) {
			return false;
		}

//		if (!swapped)
		return true; // we can't trust the origial yet, it may have too many points or too few aces

		// now we are in a position to do the points test as we know the current 'bestSoFar' (us) is fully valid

//		return (pointsNew > this.nsSummary.nsCombPoints);
	}

	/** 
	 */
	public boolean arePointsAndAcesOk(int min, int max, int aces) {
		// ==============================================================================================

		if (nsSummary.nsCombPoints < min || max < nsSummary.nsCombPoints || (nsHaveAtLeastSoManyAces(aces) == false)) {
			return false;
		}
		else {
			return true;
		}
	}

	/**
	 */
	static public class SuitShape {
		// ------------------------- CLASS ----------------------
		int lLen;
		int sLen;

		SuitShape(int l, int s) {
			lLen = l;
			sLen = s;
		}
	}

	/**
	 */
	public boolean isNewBetter_pointsAcesAndShape(Deal dNew, boolean swapped, int min, int max, int aces, SuitShape[] sh) {
		// ==============================================================================================
		int pointsNew = dNew.nsSummary.nsCombPoints;

		if (pointsNew < min || max < pointsNew || (dNew.nsHaveAtLeastSoManyAces(aces) == false)) {
			return false;
		}

		if (!swapped)
			return true; // we can't trust the origial yet, it may have too many points or too few aces

//		if (sh[0].sLen == 3 || sh[1].sLen == 3) {
		if (dNew.nsSummary.areTopTwo_4343_OrLonger() == false)
			return false;
//		}
//		else {
//			if (dNew.nsSummary.areTopTwo4444OrLonger() == false)
//				return false;
//		}

		int oldDiver = this.nsSummary.Divergence(sh);
		int newDiver = dNew.nsSummary.Divergence(sh);

		return newDiver < oldDiver;
	}

	/**
	 */
	public void rotateHands(int amountOfRotaion) {
		// ==============================================================================================

		if (amountOfRotaion == -1) {
			Hand kep = hands[0];
			hands[0] = hands[1];
			hands[1] = hands[2];
			hands[2] = hands[3];
			hands[3] = kep;
		}
		else if (amountOfRotaion == 1) {
			Hand kep = hands[3];
			hands[3] = hands[2];
			hands[2] = hands[1];
			hands[1] = hands[0];
			hands[0] = kep;
		}
		// else assume it is nothing

		// fix the old now broken compass.v points
		for (Dir p : Dir.nesw) {
			Hand hand = hands[p.v];
			hand.compass = p;
		}

		// re-create the ease of access rota
		for (Dir row : Dir.nesw) {
			int i = 0;
			for (Dir col : Dir.rota[row.v]) {
				rota[row.v][i++] = hands[col.v];
			}
		}

		youSeatHint = youSeatHint.rotate(amountOfRotaion);

		rotateDealerAndVulnerability(amountOfRotaion);

		if (!contract.isNullBid() && !contract.isPass()) {
			contractCompass = contractCompass.rotate(amountOfRotaion);
			// just a quick cross check - about which we do nothing if it fails !
			Dir recalcCompass = getHandThatMadePartnershipFirstCallOfSuit(contract).compass;
			assert (contractCompass == recalcCompass);
		}

		// was restoreDealTransients();

		/** 
		 */
		{
			for (Card card : packPristine) {
				card.suitCh = card.suit.toChar();
			}

			for (Hand hand : hands) {
				hand.compassCh = hand.compass.toLowerChar();

				for (Frag frag : hand.frags) {
					frag.suitCh = frag.suit.toChar();
				}
				for (Frag fOrg : hand.fOrgs) {
					fOrg.suitCh = fOrg.suit.toChar();
				}
				for (Bid bid : hand.bids) {
					if (bid.isCall() == false) {
						bid.suitCh = bid.suit.toChar();
					}
				}
			}
		}

	}

	/**
	 */
	public Card getCardSearchAllHands(Rank rank, Suit suit) {
		// ==============================================================================================
		for (Hand hand : hands) {
			Card card = hand.getCardIfMatching(suit, rank);
			if (card != null)
				return card;
		}
		return null;
	}

	/**
	 */
	public Card getCardSearchAllHands_Orig(Rank rank, Suit suit) {
		// ==============================================================================================
		for (Hand hand : hands) {
			Card card = hand.getCardIfMatching_Orig(suit, rank);
			if (card != null)
				return card;
		}
		return null;
	}

	/**
	 */
	public boolean removeCardIncFOrgs(Card cdRem) {
		// ==============================================================================================
		for (Hand hand : hands) {
			for (Card card : hand.frags[cdRem.suit.v]) {
				if (card == cdRem) {
					hand.fOrgs[card.suit.v].remove(card);
					hand.frags[card.suit.v].remove(card);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 */
	public void partialClear() {
		// ==============================================================================================
		// we want to preserve player names dealer and vulnerability and ...
		for (Hand hand : hands) {
			hand.partialClear();
		}

		packPristine.clear();

		// re-create the pack with the cards
		for (Suit suit : Suit.cdhs) {
			for (Rank rank : Rank.allThriteenRanks) {
				packPristine.add(new Card(rank, suit));
			}
		}

		// displayBoardId = "";
		// signfBoardId = "";
		// ahHeader = ""; nope

		endedWithClaim = false;
		prevTrickWinner.clear();
		contract = new Bid(Call.NullBid);
		contractDblRe = new Bid(Call.NullBid);
		contractCompass = Dir.Invalid;
		clearAllStrategies();
	}

	/**
	 */
	public void hideAllSuitsInAllHands() {
		// ==============================================================================================
		for (Hand hand : hands) {
			for (Suit suit : Suit.cdhs) {
				Frag frag = hand.frags[suit.v];
				frag.suitVisControl = Suit.SVC_noneSet;
			}
		}
	}

	/**
	 */
	public void hideSuitsInHand(Hand hand) {
		// ==============================================================================================
		for (Suit suit : Suit.cdhs) {
			Frag frag = hand.frags[suit.v];
			frag.suitVisControl = Suit.SVC_noneSet;
		}
	}

	/**
	 */
	public void showSuitsInHand(Hand hand) {
		// ==============================================================================================
		for (Suit suit : Suit.cdhs) {
			Frag frag = hand.frags[suit.v];
			frag.suitVisControl = Suit.SVC_cards;
		}
	}

	/**
	 */
	public void fillDealDistribution_0_Training(int dfcTrainingSuitSort) {
		// ==============================================================================================

		Dir dir = Dir.South;

		Hand hand = hands[dir.v];

		boolean ok;
		do {
			ok = true;

			partialClear();

			Collections.shuffle(packPristine);

			for (int i = 0; i < 13; i++) {
				hand.addDeltCard(packPristine.remove(packPristine.size() - 1));
			}

			for (Frag forg : hand.fOrgs) {
				if (forg.size() > 7)
					ok = false;
			}

		} while (ok == false);

		packPristine.clear(); // so we won't have any duplicate cards

		if (dfcTrainingSuitSort == 0 /* spades longest */|| dfcTrainingSuitSort == 1 /* clubs longest */) {
			Arrays.sort(hand.fOrgs);
			Arrays.sort(hand.frags);

			if (dfcTrainingSuitSort == 1 /* clubs longest */) {
				/* we need to reverse the sorted order */

				Frag f;
				f = hand.fOrgs[0];
				hand.fOrgs[0] = hand.fOrgs[3];
				hand.fOrgs[3] = f;

				f = hand.fOrgs[1];
				hand.fOrgs[1] = hand.fOrgs[2];
				hand.fOrgs[2] = f;
			}

			// We now need force the suits to be correct for their new position
			for (Suit suit : Suit.cdhs) {
				Frag forg = hand.fOrgs[suit.v];
				Frag frag = hand.frags[suit.v];
				forg.suit = suit;
				frag.suit = suit;
				frag.clear();
				for (Card card : forg) {
					card.suit = suit;
					frag.add(card);
				}
			}
		}

		int suitIndex = (int) (Math.random() * 4);
		Suit ansSuit = Suit.cdhs[suitIndex];

		for (Frag frag : hand.frags) {
			if (frag.suit == ansSuit) {
				frag.suitVisControl = Suit.SVC_ansHere;
			}
			else {
				frag.suitVisControl = Suit.SVC_cards | Suit.SVC_qaCount;
			}
		}
	}

	/**
	 */
	public void fillDealDistributionExam_1_Deal(int difficulity, Dir examYou, boolean bottomYou) {
		// ==============================================================================================
		setYouSeatHint(examYou);

		do {
			redeal();
		} while (isAnySuitLongerThan7());

		hideAllSuitsInAllHands();

		Hand you = hands[getTheYouSeat().v];
//		Hand partnerIfDefender = hands[you.compass.rotate180().v];
//		Hand dummy = hands[Dir.North.v];
//		Hand declarer = hands[Dir.South.v];

		showSuitsInHand(you);
	}

	/**
	 */
	public boolean fillDealDistributionExam_2_Bid(int difficulity, Dir examYou, boolean bottomYou) {
		// ==============================================================================================

		hideAllSuitsInAllHands();

		Hand you = hands[getTheYouSeat().v];
//		Hand partnerIfDefender = hands[you.compass.rotate180().v];
//		Hand dummy = hands[Dir.North.v];
		Hand declarer = hands[Dir.South.v];

		showSuitsInHand(you);

		if (difficulity == 3)
			return true; // no length info revealed by this bidding :)

		Hand target = declarer;

		if (you == declarer) {
			target = (Math.random() < 0.5) ? hands[Dir.West.v] : hands[Dir.East.v];
		}

		Suit infoSuit;
		do {
			infoSuit = Suit.cdhs[(int) (Math.random() * 4)];
		} while (target.frags[infoSuit.v].size() < 4); // we want a suit of at least 4 cards

		// show all the and dots
		for (Suit suit : Suit.cdhs) {
			Frag frag = target.frags[suit.v];
			frag.suitVisControl = Suit.SVC_dot;
		}

		// change one to be info
		Frag infoFrag = target.frags[infoSuit.v];
		Frag infoFOrg = target.fOrgs[infoSuit.v];
		infoFrag.suitVisControl = Suit.SVC_count;
		infoFOrg.suitVisControl = Suit.SVC_count; // save it for later 'pages' in the FOrg

		return false;
	}

	/**
	 */
	public void fillDealDistributionExam_3_Dummy() {
		// ==============================================================================================

		hideAllSuitsInAllHands();

		Hand you = hands[getTheYouSeat().v];
//		Hand partnerIfDefender = hands[you.compass.rotate180().v];
		Hand dummy = hands[Dir.North.v];
//		Hand declarer = hands[Dir.South.v];

		showSuitsInHand(you);
		showSuitsInHand(dummy);
	}

	/**
	 */
	public boolean fillDealDistributionExam_4_Play(int difficulity, Dir examYou, boolean bottomYou) {
		// ==============================================================================================

		hideAllSuitsInAllHands();

		Hand you = hands[getTheYouSeat().v];
		Hand partnerIfDefender = hands[you.compass.rotate180().v];
		Hand dummy = hands[Dir.North.v];
		Hand declarer = hands[Dir.South.v];

		boolean taken[] = { false, false, false, false };
		int takenCount = 0;

		// find all previously shown counts
		for (Hand hand : hands) {
			for (Suit suit : Suit.cdhs) {
				Frag fOrg = hand.fOrgs[suit.v];
				if ((fOrg.suitVisControl & Suit.SVC_count) == Suit.SVC_count) {
					taken[suit.v] = true;
					takenCount++;
				}
			}
		}

		if (takenCount > 0) {
			return true;
		}

		if (difficulity <= 2) {
			showSuitsInHand(you);
			showSuitsInHand(dummy);
		}

		Hand target;

		if (you == declarer) {
			target = (Math.random() < 0.5) ? hands[Dir.West.v] : hands[Dir.East.v];
		}
		else {
//			if (difficulity >= 2) 
			target = partnerIfDefender; // when on max
//			else
//				target = (Math.random() < 0.80) ? partnerIfDefender : declarer;
		}

		Suit infoSuit;
		do {
			infoSuit = Suit.cdhs[(int) (Math.random() * 4)];
		} while (taken[infoSuit.v]);

		// show all the and dots
		for (Suit suit : Suit.cdhs) {
			Frag frag = target.frags[suit.v];
			frag.suitVisControl = Suit.SVC_dot;
		}

		// change one to be info
		Frag infoFrag = target.frags[infoSuit.v];
		Frag infoFOrg = target.fOrgs[infoSuit.v];
		infoFrag.suitVisControl = Suit.SVC_count;
		infoFOrg.suitVisControl = Suit.SVC_count; // save it for later 'pages' in the FOrg

		return false;
	}

	/**
	 */
	public void fillDealDistributionExam_5_Play(int difficulity, Dir examYou, boolean bottomYou) {
		// ==============================================================================================

		hideAllSuitsInAllHands();

		Hand you = hands[getTheYouSeat().v];
		Hand partnerIfDefender = hands[you.compass.rotate180().v];
		Hand dummy = hands[Dir.North.v];
		Hand declarer = hands[Dir.South.v];

		if (difficulity <= 1) {
			showSuitsInHand(you);
			showSuitsInHand(dummy);
		}

		boolean taken[] = { false, false, false, false };

		// find all previously shown counts
		for (Hand hand : hands) {
			for (Suit suit : Suit.cdhs) {
				Frag fOrg = hand.fOrgs[suit.v];
				if ((fOrg.suitVisControl & Suit.SVC_count) == Suit.SVC_count)
					taken[suit.v] = true;
			}
		}

		Hand target;

		if (you == declarer) {
			target = (Math.random() < 0.5) ? hands[Dir.West.v] : hands[Dir.East.v];
		}
		else {
//			if (difficulity >= 2) 
			target = partnerIfDefender; // when on max
//			else
//				target = (Math.random() < 0.80) ? partnerIfDefender : declarer;
		}

		int infoSuitIndex;

		do {
			infoSuitIndex = (int) (Math.random() * 4);
		} while (taken[infoSuitIndex]);

		Suit infoSuit = Suit.cdhs[infoSuitIndex];

		// show all the and dots
		for (Suit suit : Suit.cdhs) {
			Frag frag = target.frags[suit.v];
			frag.suitVisControl = Suit.SVC_dot;
		}

		// change one to be info
		Frag infoFrag = target.frags[infoSuit.v];
		Frag infoFOrg = target.fOrgs[infoSuit.v];
		infoFrag.suitVisControl = Suit.SVC_count;
		infoFOrg.suitVisControl = Suit.SVC_count; // save it for later 'pages' in the FOrg
	}

	/**
	 */
	public void fillDealDistributionExam_6_Play(int difficulity, Dir examYou, boolean bottomYou) {
		// ==============================================================================================

		hideAllSuitsInAllHands();

		Hand you = hands[getTheYouSeat().v];
		Hand partnerIfDefender = hands[you.compass.rotate180().v];
		Hand dummy = hands[Dir.North.v];
		Hand declarer = hands[Dir.South.v];

		if (difficulity == 0) {
			showSuitsInHand(you);
			showSuitsInHand(dummy);
		}

		boolean taken[] = { false, false, false, false };

		// find all previously shown counts
		for (Hand hand : hands) {
			for (Suit suit : Suit.cdhs) {
				Frag fOrg = hand.fOrgs[suit.v];
				if ((fOrg.suitVisControl & Suit.SVC_count) == Suit.SVC_count)
					taken[suit.v] = true;
			}
		}

		Hand target;

		if (you == declarer) {
			target = (Math.random() < 0.5) ? hands[Dir.West.v] : hands[Dir.East.v];
		}
		else {
			target = partnerIfDefender;
//			target = (Math.random() < 0.20) ? partnerIfDefender : declarer;
		}

		int infoSuitIndex;

		do {
			infoSuitIndex = (int) (Math.random() * 4);
		} while (taken[infoSuitIndex]);

		Suit infoSuit = Suit.cdhs[infoSuitIndex];

		// show all the and dots
		for (Suit suit : Suit.cdhs) {
			Frag frag = target.frags[suit.v];
			frag.suitVisControl = Suit.SVC_dot;
		}

		// change one to be info
		Frag infoFrag = target.frags[infoSuit.v];
		Frag infoFOrg = target.fOrgs[infoSuit.v];
		infoFrag.suitVisControl = Suit.SVC_count;
		infoFOrg.suitVisControl = Suit.SVC_count; // save it for later 'pages' in the FOrg
	}

	/**
	 */
	public void fillDealDistributionExam_7_Question(int difficulity, Dir examYou, boolean bottomYou) {
		// ==============================================================================================

		hideAllSuitsInAllHands();

		Hand you = hands[getTheYouSeat().v];
		Hand partnerIfDefender = hands[you.compass.rotate180().v];
		Hand dummy = hands[Dir.North.v];
		Hand declarer = hands[Dir.South.v];

		if (difficulity == 0) {
			showSuitsInHand(you);
			showSuitsInHand(dummy);
		}

		boolean taken[] = { false, false, false, false };

		// find all previously shown counts
		for (Hand hand : hands) {
			for (Suit suit : Suit.cdhs) {
				Frag fOrg = hand.fOrgs[suit.v];
				if ((fOrg.suitVisControl & Suit.SVC_count) == Suit.SVC_count)
					taken[suit.v] = true;
			}
		}

		Hand target;

		if (you == declarer) {
			target = (Math.random() < 0.5) ? hands[Dir.West.v] : hands[Dir.East.v];
		}
		else if (App.dfcExamBottomYou) {
			target = declarer; // as partner is in the north zone
		}
		else {
			target = (Math.random() < 0.20) ? partnerIfDefender : declarer;
		}

		int infoSuitIndex;

		do {
			infoSuitIndex = (int) (Math.random() * 4);
		} while (taken[infoSuitIndex]);

		Suit infoSuit = Suit.cdhs[infoSuitIndex];

		// show all the and dots
		for (Suit suit : Suit.cdhs) {
			Frag frag = target.frags[suit.v];
			frag.suitVisControl = Suit.SVC_dot | Suit.SVC_qaDot;
		}

		// change one to be info
		Frag infoFrag = target.frags[infoSuit.v];
		infoFrag.suitVisControl = Suit.SVC_ansHere;

	}

	/**
	 */
	public void suffleLowerCards(int upto) {
		// ==============================================================================================

		for (Suit suit : Suit.shdc) {
			int counted[] = { 0, 0, 0, 0 };
			Cal cards = new Cal(13);
			for (Hand hand : hands) {
				Frag fOrg = hand.fOrgs[suit.v];
				Frag frag = hand.frags[suit.v];
				while (!fOrg.isEmpty() && fOrg.getLast().rank.v <= upto) {
					counted[hand.compass.v]++;
					cards.add(fOrg.removeLast());
					frag.removeLast();
				}
			}

			Collections.shuffle(cards);

			for (Hand hand : hands) {
				Frag fOrg = hand.fOrgs[suit.v];
				Frag frag = hand.frags[suit.v];
				while (counted[hand.compass.v]-- > 0) {
					fOrg.addDeltCard(cards.getLast());
					frag.addDeltCard(cards.removeLast());
				}
			}
		}

	}

	/**
	 */
	public void fillDealDistributionExam_8_Tell() {
		// ==============================================================================================

		Hand target = hands[Dir.South.v];
		for (Hand hand : hands) {
			for (Suit suit : Suit.cdhs) {
				Frag frag = hand.frags[suit.v];
				if ((frag.suitVisControl & Suit.SVC_ansHere) == Suit.SVC_ansHere)
					target = hand;
				frag.suitVisControl &= Suit.SVC_ansHere; // clear everything except the ansHere
			}
		}

		// show all the and dots
		for (Suit suit : Suit.cdhs) {
			Frag frag = target.frags[suit.v];
			frag.suitVisControl |= Suit.SVC_dot | Suit.SVC_qaDot; // the |= preservers the ansHere
		}

		// hide ALL suits
		for (Hand hand : hands) {
			for (Suit suit : Suit.cdhs) {
				Frag frag = hand.frags[suit.v];
				frag.suitVisControl = Suit.SVC_noneSet;
			}
		}

		boolean taken[] = { false, false, false, false };

		// find all previously shown counts
		for (Hand hand : hands) {
			for (Suit suit : Suit.cdhs) {
				Frag fOrg = hand.fOrgs[suit.v];
				if ((fOrg.suitVisControl & Suit.SVC_count) == Suit.SVC_count)
					taken[suit.v] = true;
			}
		}

		int infoSuitIndex;

		do {
			infoSuitIndex = (int) (Math.random() * 4);
		} while (taken[infoSuitIndex]);

		Suit infoSuit = Suit.cdhs[infoSuitIndex];

		// show all the and dots
		for (Suit suit : Suit.cdhs) {
			Frag frag = target.frags[suit.v];
			frag.suitVisControl = Suit.SVC_dot | Suit.SVC_qaDot;
		}

		// change one to be info
		Frag infoFrag = target.frags[infoSuit.v];
		infoFrag.suitVisControl = Suit.SVC_count | Suit.SVC_qaCount | Suit.SVC_ansHere;

	}

	/**
	 */
	public void fillDealDistributionExam_9_All() {
		// ==============================================================================================

		Hand target = hands[Dir.South.v];
		for (Hand hand : hands) {
			for (Suit suit : Suit.cdhs) {
				Frag frag = hand.frags[suit.v];
				if ((frag.suitVisControl & Suit.SVC_ansHere) == Suit.SVC_ansHere) {
					target = hand;
				}
				frag.suitVisControl &= Suit.SVC_ansHere;
			}
		}

		for (Hand hand : hands) {
			for (Suit suit : Suit.cdhs) {
				Frag frag = hand.frags[suit.v];
				int preserveAnsHere = frag.suitVisControl & Suit.SVC_ansHere;
				frag.suitVisControl = Suit.SVC_cards | (hand == target ? Suit.SVC_qaCount : 0) | preserveAnsHere;
			}
		}
	}

	/**
	 */
	public void fillDealDistributionExam_9_XClear() {
		// ==============================================================================================

		undoLastBid();
		undoLastBid();
		undoLastBid();
		undoLastBid();

		for (Hand hand : hands) {
			for (Suit suit : Suit.cdhs) {
				Frag frag = hand.frags[suit.v];
				frag.suitVisControl = Suit.SVC_noneSet;
			}
		}

	}

//	/**
//	 */
//	public void fillDealDistributionExam_R_Restart() {
//		// ==============================================================================================
//
//		// hide all hands
//		for (Hand hand : hands) {
//			for (Suit suit : Suit.cdhs) {
//				Frag frag = hand.frags[suit.v];
//				frag.suitVisControl = 0;
//			}
//		}
//	}

	public final static String yesFill = "yesFill";
	public final static String noFill = "noFill";

	/**
	 */
	public void fillDealExternal(ArrayList<String> hsAy, String action, int line_no_info) {
		// ==============================================================================================

		boolean insertMode = false;
		boolean dealerSet = false;

		int maxSeats = (hsAy.size() > 4) ? 4 : hsAy.size(); // kia very first mentoring has 4 commas = 5 "seats" :)

		if (maxSeats > 0) {
			String hs = hsAy.get(0).trim();
			insertMode = (hs.length() > 0 && hs.charAt(0) == '0');
		}

		if (insertMode) {
			// this means we are inserting cards
			// i.e. we do NOT by default clear the hands before inserting cards
			dealerSet = true; // we are keeping the old one (probally)!
			@SuppressWarnings("unused")
			int z = 0;
//
//          This is a bad edit - left as a comment because .....
//
//			for (int i = 0; i < maxSeats; i++) {
//				int handCompass = (i + 2) % 4; // So starting at South (aaBridge encoding) which is the first hand in the linBlock
//				Hand hand = hands[handCompass];
//
//				String hs = hsAy.get(i).trim();
//				boolean zeroInFront = (hs.length() > 0 && hs.charAt(0) == '0');
//
//				if (zeroInFront == false) {
//					// we assume that a zeroInFront means that the hand is to remain unchanged
//					// while not having one means that all cards are to be cleared - THE NORMAL CASE
//					// However is some do and some don't the we restore the cards of the ones that dont
//					hand.restoreUnplayedCardsToPack();
//				}
//			}
		}
		else {
			partialClear(); // this is where you have to pray that this is what is expected !
		}

		boolean suit_symb = false; // no suit symbols

		for (String hs : hsAy) {
			hs = hs.toLowerCase();
			if (hs.contains("s") || hs.contains("h") || hs.contains("d") || hs.contains("c")) {
				suit_symb = true;
				break; // this is the very common case
			}
		}

		if (suit_symb == false) {
			// the idea here is a faster way of inputting suits that aaBridge will 'clean up' for you
			// spaces separate the suits and a hyphen means a void s h d c order

//			   System.out.println("suit_symb == false  "  +  " >" + hsAy.get(0) + "< ");

			for (int i = 0; i < maxSeats; i++) {
				int handCompass = (i + 2) % 4; // So starting at South (aaBridge encoding) which is the first hand in the linBlock
				Hand hand = hands[handCompass];

				String hs = hsAy.get(i).trim();

				Suit suit = Suit.Spades; // set a default suit
				boolean eating_spaces = true;
				// System.out.println("");
				for (int j = 0; j < hs.length(); j++) {

					char c = hs.charAt(j);
					// System.out.print(c);
					if (handCompass == Dir.South.v && j == 0 && '0' <= c && c <= '4') {
						if (c == '0') {
							continue; // Keep current dealer
						}
						dealerSet = true;
						Dir dealer = Dir.dirFromInt(((c - '0') + 1) % 4); // for lin '1'=South, '4'=East, aaBridge internal 2=South, 0=North
						setDealer(dealer);
						continue; // found the dealer
					}

					if (c == '-') {
						if (eating_spaces == false) {
							suit = suit.suitBelow();
						}
						eating_spaces = false;
						c = ' '; // and process as a space
					}

					if (c == ' ') {
						if (eating_spaces == false) {
							suit = suit.suitBelow();
							eating_spaces = true;
						}
						continue;
					}

					Rank rank = Rank.charToRank(c);

					if (rank == Rank.Invalid)
						continue; // ignore it perhaps a '-' or a space

					eating_spaces = false;

//					if (rank.v == 9 && suit.v == 0) {
//						@SuppressWarnings("unused")
//						int z = 0;
//					}

					Card card = packPristine.getIfRankAndSuitExists(rank, suit);
					if (card != null) {
						packPristine.remove(card);
					}
					else {
						/**
						 * As the card is not in the pack we must assume that it is
						 * in one of the hands and is currently unplayed.  The only
						 * sensible way to remove a played card is to use UNDO !
						 * But I have never seen 'the spec' so how would I know !
						 */
						card = getCardSearchAllHands(rank, suit);
						if (card == null) {
							System.out.println("md  -  Card not in deck " + rank + " " + suit);
							continue;
						}

						boolean success = removeCardIncFOrgs(card);
						if (success == false) {
							System.out.println("md  -  Can't remove played card " + rank + " " + suit);
							continue;
						}
					}
					/** 
					 * Now that the card has been removed from the pack or from a hand
					 * We can add it back where we want it.
					 */

					hand.fOrgs[suit.v].addDeltCard(card);
					hand.frags[suit.v].addDeltCard(card);

					// System.out.println(hand + " " + card + "  ");
				}
			}
		}
		else {
			// standard lin file format case ie we have suit symbols 99.99 % case
			Suit suit;
			for (int i = 0; i < maxSeats; i++) {
				int handCompass = (i + 2) % 4; // So starting at South (aaBridge encoding) which is the first hand in the linBlock
				Hand hand = hands[handCompass];

				String hs = hsAy.get(i).trim();

				suit = Suit.Spades; // set a default suit
				for (int j = 0; j < hs.length(); j++) {
					char c = hs.charAt(j);
					if (handCompass == Dir.South.v && j == 0 && '0' <= c && c <= '4') {
						if (c == '0')
							continue; // c = '1'; // South
						dealerSet = true;
						Dir dealer = Dir.dirFromInt(((c - '0') + 1) % 4); // for lin '1'=South, '4'=East, aaBridge internal 2=South, 0=North
						setDealer(dealer);
						continue; // found the dealer
					}
					// @formatter:off
					switch (c) {
						case 'S': case 's': suit = Suit.Spades;   continue;
						case 'H': case 'h': suit = Suit.Hearts;   continue;
						case 'D': case 'd': suit = Suit.Diamonds; continue;
						case 'C': case 'c': suit = Suit.Clubs;    continue;
					}
					// @formatter:on
					Rank rank = Rank.charToRank(c);

					if (rank == Rank.Invalid)
						continue; // ignore it perhaps a '-' or a space

					// if (rank.v == 9 && suit.v == 0) {
					// @SuppressWarnings("unused")
					// int z = 0;
					// }

					Card card = packPristine.getIfRankAndSuitExists(rank, suit);
					if (card != null) {
						packPristine.remove(card);
					}
					else {
						/**
						 * As the card is not in the pack we must assume that it is
						 * in one of the hands and is currently unplayed.  The only
						 * sensible way to remove a played card is to use UNDO !
						 * But I have never seen 'the spec' so how would I know !
						 */
						card = getCardSearchAllHands(rank, suit);
						if (card == null) {
							System.out.println("b Card not in deck " + rank + " " + suit);
							continue;
						}

						boolean success = removeCardIncFOrgs(card);
						if (success == false) {
							System.out.println("Can't remove played card " + rank + " " + suit);
							continue;
						}
						if (insertMode == false) {
							System.out.println("Line: " + line_no_info + "  duplicated card>>> " + card);
						}
					}
					/** 
					 * Now that the card has been removed from the pack or from a hand
					 * We can add it back where we want it.
					 */

					hand.fOrgs[suit.v].addDeltCard(card);
					hand.frags[suit.v].addDeltCard(card);

					// System.out.println(hand + " " + card + "  ");
				}
			}
		}

		/**
		 * The user could be specifiying just a one or two handed deal, in which case
		 * some of the hands will be empty. If we then find any hand is missing
		 * exactly the number of cards that are left in the deck then we add them
		 */
		String report = "";
		int added = 0;
		if (action == yesFill && packPristine.size() <= 13) {
			for (Hand hand : hands) {
				if (13 - hand.countOriginalCards() == packPristine.size()) {
					for (Card card : packPristine) {
						Suit suit = card.suit;
						hand.fOrgs[suit.v].addDeltCard(card);
						hand.frags[suit.v].addDeltCard(card);
						report += " " + card;
						added++;
					}
					packPristine.clear();
				}
			}
		}
		if (report.length() != 0 && added != 13) {
			System.out.println("Line: " + line_no_info + "  undealt cards added>>>" + report);
		}

		if (packPristine.size() == 0 && dealerSet == false) {
			// setDealer(Dir.South); ummm
		}
	}

	public void markCardsKept(ArrayList<String> hsAy) {
		// ==============================================================================================

		String hs = hsAy.get(0).trim();

		if (hs.isEmpty() || hs.toLowerCase().startsWith("n")) {
			this.clearAnyKeptCards();
			return;
		}

		Suit suit = Suit.Spades; // set a default suit
		for (int j = 0; j < hs.length(); j++) {
			char c = hs.charAt(j);

			// @formatter:off
			switch (c) {
				case 'S': case 's': suit = Suit.Spades;   continue;
				case 'H': case 'h': suit = Suit.Hearts;   continue;
				case 'D': case 'd': suit = Suit.Diamonds; continue;
				case 'C': case 'c': suit = Suit.Clubs;    continue;
			}
			// @formatter:on
			Rank rank = Rank.charToRank(c);

			if (rank == Rank.Invalid)
				continue; // ignore it perhaps a '-' or a space

			Card card = packPristine.getIfRankAndSuitExists(rank, suit);
			if (card != null) {
				continue; // the card is in the unplayed pack so it is already removed from any hand
			}

			/**
			 * As the card is not in the pack we must assume that it is
			 * in one of the hands and is currently unplayed.
			 */
			card = getCardSearchAllHands_Orig(rank, suit); // ORIG ORIG ORIG ORIG <<<<<<<<<<<<<<<<<
			if (card == null) {
				System.out.println("kc  -  Card not in deck " + rank + " " + suit);
				continue;
			}

			card.setKept(true);

			// System.out.println(hand + " " + card + "  ");
		}

	}

	/**
	 */
	public void removeCards(ArrayList<String> hsAy) {
		// ==============================================================================================

		String hs = hsAy.get(0).trim();

		Suit suit = Suit.Spades; // set a default suit
		for (int j = 0; j < hs.length(); j++) {
			char c = hs.charAt(j);

			// @formatter:off
				switch (c) {
					case 'S': case 's': suit = Suit.Spades;   continue;
					case 'H': case 'h': suit = Suit.Hearts;   continue;
					case 'D': case 'd': suit = Suit.Diamonds; continue;
					case 'C': case 'c': suit = Suit.Clubs;    continue;
				}
				// @formatter:on
			Rank rank = Rank.charToRank(c);

			if (rank == Rank.Invalid)
				continue; // ignore it perhaps a '-' or a space

			Card card = packPristine.getIfRankAndSuitExists(rank, suit);
			if (card != null) {
				continue; // the card is in the unplayed pack so it is already removed from any hand
			}

			/**
			 * As the card is not in the pack we must assume that it is
			 * in one of the hands and is currently unplayed.  The only
			 * sensible way to remove a played card is to use UNDO !
			 * But I have never seen 'the spec' so how would I know !
			 */
			card = getCardSearchAllHands(rank, suit);
			if (card == null) {
				System.out.println("rc - Card not in deck " + rank + " " + suit);
				continue;
			}

			boolean success = removeCardIncFOrgs(card);
			if (success == false) {
				System.out.println("rc - Can't remove played card " + rank + " " + suit);
				continue;
			}

			// System.out.println(hand + " " + card + "  ");
		}

	}

	/**
	 */
	public boolean isAnyBubbleTextSet() {
		// ==============================================================================================
		for (Hand hand : hands) {
			if (hand.bubbleText.length() > 0)
				return true;
		}
		return false;
	}

	/**
	 */
	public void clearAllBubbleText() {
		// ==============================================================================================
		for (Hand hand : hands) {
			hand.bubbleText = "";
		}
	}

	/**
	 */
	public void setBubbleText(char c, String text) {
		// ==============================================================================================
		Dir dir = Dir.directionFromChar(c);
		if (dir != Dir.Invalid)
			hands[dir.v].bubbleText = text.trim();
	}

	/**
	 */
	public Deal deepClone() {
		// ==============================================================================================
		Deal d = new Deal("", Dir.South /* Here South is - don't care */);

		d.localId = localId;
		d.localLast_pg = 0;

		d.tc_suppress_pc_display = tc_suppress_pc_display;

		d.testId = testId; // a transient

		d.realBoardNo = realBoardNo;
		d.displayBoardId = displayBoardId;
		d.signfBoardId = signfBoardId;
		d.dealer = dealer; /* compass.v nesw */
		d.vulnerability[Dir.NS] = vulnerability[Dir.NS];
		d.vulnerability[Dir.EW] = vulnerability[Dir.EW];
		d.contractCompass = contractCompass;

		d.youSeatInLoadedLin = youSeatInLoadedLin;
		d.youSeatHint = youSeatHint;
		d.ahHeader = ahHeader;
		// d.buildNumber in construct time field def

		d.lastSavedAsPathWithSep = lastSavedAsPathWithSep;
		d.lastSavedAsFilename = lastSavedAsFilename;
		d.endedWithClaim = endedWithClaim;
		d.tricksClaimed = tricksClaimed;
		d.doneHand = doneHand;
		d.changed = false; // a clone by definition starts unchanged
		d.showBidQuestionMark = showBidQuestionMark;
		d.columnsInBidTable = columnsInBidTable;
		d.qx_room = qx_room;
		d.qx_number = qx_number;
		d.blueScore = blueScore;
		d.purpleScore = purpleScore;

		d.eb_blocker = eb_blocker;
		d.eb_min_card = eb_min_card;

		d.dfcDeal = dfcDeal;
		d.forceDifferent = forceDifferent;

		// contract
		if (contract.isCall()) {
			d.contract = new Bid(contract.call);
		}
		else {
			d.contract = new Bid(contract.level, contract.suit);
		}

		// contractDblRe
		{
			d.contractDblRe = new Bid(contractDblRe.call);
		}

		for (Dir p : Dir.nesw) {
			Hand o_hand = hands[p.v];
			Hand d_hand = d.hands[p.v];

			d_hand.playerName = o_hand.playerName;
			d_hand.bubbleText = o_hand.bubbleText;

			// compass.v, is preset

			// fOrgs
			for (Suit su : Suit.cdhs) {
				Frag o_fOrg = o_hand.fOrgs[su.v];
				Frag d_fOrg = d_hand.fOrgs[su.v];
				d_fOrg.suitVisControl = o_fOrg.suitVisControl;
				d_fOrg.showXes = o_fOrg.showXes;
				// hand, suit, suitCh all preset
				for (Card o_card : o_fOrg) {
					Card d_card = d.packPristine.getIfRankAndSuitExists(o_card.rank, o_card.suit);
//					if (d_card == null) {
//						@SuppressWarnings("unused")
//						int z = 0;
//						System.out.println( o_card);
//						continue; // !!!!
//					}
					d_card.setKept(o_card.isKept());
					d_fOrg.addDeltCard(d_card);
					// no remove here
				}
			}

			// frags
			for (Suit su : Suit.cdhs) {
				Frag o_frag = o_hand.frags[su.v];
				Frag d_frag = d_hand.frags[su.v];
				d_frag.suitVisControl = o_frag.suitVisControl;
				d_frag.showXes = o_frag.showXes;
				// hand, suit, suitCh all preset
				for (Card o_card : o_frag) {
					Card d_card = d.packPristine.getIfRankAndSuitExists(o_card.rank, o_card.suit);
//					if (d_card == null) {
//						@SuppressWarnings("unused")
//					    int z = 0;					   
//						System.out.println( o_card);
//						continue; // !!!!
//					}	
					d_frag.addDeltCard(d_card);
					d.packPristine.remove(d_card);
				}
			}

			// played
			for (Card o_card : o_hand.played) {
				Card d_card = d.packPristine.getIfRankAndSuitExists(o_card.rank, o_card.suit);
//				if (d_card == null) {
//					@SuppressWarnings("unused")
//				    int z = 0;					   
//					System.out.println( o_card);
//					continue; // !!!!
//				}	
				d_hand.played.add(d_card);
				d.packPristine.remove(d_card);
			}

			// bids
			for (Bid bid : o_hand.bids) {
				Bid d_bid = null;
				if (bid.isCall()) {
					d_bid = new Bid(bid.call);
				}
				else {
					if ((contract != null) && (bid.level == contract.level && bid.suit == contract.suit))
						d_bid = d.contract;
					else
						d_bid = new Bid(bid.level, bid.suit);
				}
				d_bid.alert = bid.alert;
				d_bid.alertText = bid.alertText;

				d_hand.bids.add(d_bid);
			}
		}

//		assert (pack.isEmpty());
//		System.out.println(" ppppppppppkk: " + d.packPristine.size());

		// prevTrickWinner
		for (Hand o_winner : prevTrickWinner) {
			d.prevTrickWinner.add(d.hands[o_winner.compass.v]);
//			if (d.prevTrickWinner.get(0).compass.v != Dir.East.v) {
//				@SuppressWarnings("unused")
//				int z = 0;
//			}
		}

		return d;
	}

	/**
	 */
	public boolean coreEqualTo(Deal d) {
		// ==============================================================================================
		if (qx_room != d.qx_room)
			return false; // this is the Open an Closed state of the room

		if (dealer != d.dealer)
			return false;

		if (vulnerability[Dir.NS] != d.vulnerability[Dir.NS])
			return false;

		if (vulnerability[Dir.EW] != d.vulnerability[Dir.EW])
			return false;

		if (forceDifferent != d.forceDifferent)
			return false;

		/** 
		 * the contract CAN be different / not exist yet
		 */

		for (Dir p : Dir.nesw) {
			Hand o_hand = hands[p.v];
			Hand d_hand = d.hands[p.v];

			// fOrgs
			for (Suit su : Suit.cdhs) {
				Frag o_fOrg = o_hand.fOrgs[su.v];
				Frag d_fOrg = d_hand.fOrgs[su.v];
				if (o_fOrg.size() != d_fOrg.size())
					return false;
				for (int i = 0; i < o_fOrg.size() - 1; i++) {
					Card o_card = o_fOrg.get(i);
					Card d_card = d_fOrg.get(i);
					if (o_card.rank != d_card.rank)
						return false;
				}
			}
		}
		return true;
	}

	/**
	 */
	public boolean pbnEqualTo(Deal d) {
		// ==============================================================================================

		if (dealer != d.dealer)
			return false;

		if (vulnerability[Dir.NS] != d.vulnerability[Dir.NS])
			return false;

		if (vulnerability[Dir.EW] != d.vulnerability[Dir.EW])
			return false;

		/** 
		 * the contract CAN be different / not exist yet
		 */

		for (Dir p : Dir.nesw) {
			Hand o_hand = hands[p.v];
			Hand d_hand = d.hands[p.v];

			// fOrgs
			for (Suit su : Suit.cdhs) {
				Frag o_fOrg = o_hand.fOrgs[su.v];
				Frag d_fOrg = d_hand.fOrgs[su.v];
				if (o_fOrg.size() != d_fOrg.size())
					return false;
				for (int i = 0; i < o_fOrg.size(); i++) {
					Card o_card = o_fOrg.get(i);
					Card d_card = d_fOrg.get(i);
					if (o_card.rank != d_card.rank)
						return false;
				}
			}
		}
		return true;
	}

	/**
	 */
	public void setDealer(Dir dealerCompass) {
		// ==============================================================================================
		dealer = dealerCompass;
		assert (prevTrickWinner.size() <= 1);
		prevTrickWinner.clear();
		prevTrickWinner.add(hands[dealer.v]); // Why is this here ? 2017-05-23
	}

	/**
	 */
	public static Deal newBoard(int prevBoardNo, boolean presetTheContract, String criteria, Dir youSeatForNewDeal, int dealFilter) {
		// ==============================================================================================
		App.newDealAsRequested = false; // crude but gets the job done

		Deal bestSoFar = new Deal("", youSeatForNewDeal);
		bestSoFar.setNextDealerAndVulnerability(prevBoardNo);
		bestSoFar.redeal();
		Deal d = new Deal("", youSeatForNewDeal);
		d.setNextDealerAndVulnerability(prevBoardNo);

		if (criteria.contentEquals("userBids")) {
			; // just use the bestSoFar as it is
			if (presetTheContract && (((youSeatForNewDeal.v % 2) == Dir.EW) || (southBiddingRequired(criteria) == false))) {
				bestSoFar.autoBidContract(criteria);
			}
			App.newDealAsRequested = true;
			return bestSoFar;
		}

		int aces = 0;
		int min = 23;
		int max = 40;

		SuitShape sh[] = new SuitShape[2];
		boolean suitshape_used = false;

		if (criteria.startsWith("chosenGame")) {
			aces = 0;
			min = max = 28;
			if (criteria.endsWith("27"))
				min = max = 27;
			else if (criteria.endsWith("26"))
				min = max = 26;
			else if (criteria.endsWith("25"))
				min = max = 25;
			else if (criteria.endsWith("24"))
				min = max = 24;
		}

		else if (criteria.startsWith("twoSuitSlam")) {
			suitshape_used = true;
			{
				min = 28;
				max = 29;
				sh[0] = new SuitShape(5, 5);
				sh[1] = new SuitShape(5, 4);
			}
			if (criteria.endsWith("_E2")) {
				min = 28;
				max = 29;
				sh[0] = new SuitShape(5, 4);
				sh[1] = new SuitShape(5, 4);
			}

			if (criteria.endsWith("_M1")) {
				min = 29;
				max = 30;
				sh[0] = new SuitShape(5, 4);
				sh[1] = new SuitShape(5, 3);
			}
			if (criteria.endsWith("_M2")) {
				min = 29;
				max = 30;
				sh[0] = new SuitShape(5, 3);
				sh[1] = new SuitShape(5, 3);
			}
			if (criteria.endsWith("_I1")) {
				min = 30;
				max = 31;
				sh[0] = new SuitShape(5, 4);
				sh[1] = new SuitShape(4, 4);
			}
			if (criteria.endsWith("_I2")) {
				min = 30;
				max = 31;
				sh[0] = new SuitShape(5, 3);
				sh[1] = new SuitShape(4, 4);
			}

			if (criteria.endsWith("_H1")) {
				min = 31;
				max = 32;
				sh[0] = new SuitShape(4, 4);
				sh[1] = new SuitShape(4, 4);
			}
			if (criteria.endsWith("_H2")) {
				min = 31;
				max = 32;
				sh[0] = new SuitShape(4, 4);
				sh[1] = new SuitShape(4, 3);
			}
		}

		else if (criteria.startsWith("ntGrand") || criteria.startsWith("ntSmall")) {

			if (criteria.contentEquals("ntSmall_M")) {
				min = 32;
				max = 33;
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
			}
			else {
				assert (false);
			}
		}

		// @formatter:off
		boolean checkForInteresting = ((dealFilter > 0) && App.haglundsDDSavailable
				                       && (     presetTheContract && (((youSeatForNewDeal.v % 2) == Dir.EW) 
				                             || (southBiddingRequired(criteria) == false))));
		// @formatter:on

//		System.out.println("checkForInteresting:" + checkForInteresting);

		/* 
		 * now we go find a compliant deal
		 */
		boolean pointsAndAcesOk = false;
		boolean suitshapeOk = false;
		boolean ddsOk = false;
		boolean please_swap = false;
		boolean success = false;

		int min_orig = min;
		int max_orig = max;

		int i = 0;
		while (true) {
			if (please_swap) {
				Deal tmp = bestSoFar;
				bestSoFar = d;
				d = tmp;
				please_swap = false;
			}

			if (success || (i++ > 100000))
				break;

			if ((i % 10000) == 0) {
				int v = i / 10000;
				int vl = v / 2 + 1;
				int vh = v / 2;
				min = min_orig - vl;
				max = max_orig + vh;
//				int z = 0;
			}

			d.redeal();

			if (d.arePointsAndAcesOk(min, max, aces) == false)
				continue;

			if (pointsAndAcesOk == false) {
				pointsAndAcesOk = true;
				please_swap = true;
				// so now we have one that is at least the required points and Aces

				if (!suitshape_used && !checkForInteresting) {
					success = true;
					continue;
				}
			}

			if (suitshape_used) {
				if (d.nsSummary.Divergence(sh) != 0)
					continue;

				if (suitshapeOk == false) {
					suitshapeOk = true;
					please_swap = true;
					// so now we have one that is at least the required suit shape

					if (!checkForInteresting) {
						success = true;
						continue;
					}
				}
			}

			/*
			 *   dealfilter  = 0     no checkForInteresting
			 *   dealfilter  = 1     use DDS to limit hand to set contract ONLY
			 *   dealfilter  = 2     also use DDS assiatance to trick 7 
			 *   dealfilter  = 3     also use DDS assiatance to trick 9 
			 *   dealfilter  = 4     also use DDS assiatance to trick 9 + finesses fail
			 */

			if (checkForInteresting) {
				/* we now compare the tricks taken by the dumbAuto and the DDS
				 * we reject hands where the DDS is no better than dumbAuto
				 */

				d.autoBidContract(criteria);

				boolean assistance_run = false;
				if (App.yourFinessesMostlyFail || (dealFilter == 4)) {
					d.dumbAutoPlayToEnd_with_SOME_DDS_Assistance(dealFilter); // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
					// d.wipeContractBiddingAndPlay();
					// d.autoBidContract(criteria); // rerun in case
					assistance_run = true;
				}

				int targetTricks = d.contract.level.v + 6;

				int ddsTricks = Z_ddsCalculate.calcMaxDeclarerTricks(d);

				if (ddsTricks != targetTricks)
					continue; // reject this deal

				if (dealFilter == 1) {
					please_swap = true;
					success = true;
					continue;
				}

				if (!assistance_run) {
					d.dumbAutoPlayToEnd_with_SOME_DDS_Assistance(dealFilter); // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
				}
				int dumbTricks = d.getContractTrickCountSoFar().x;

//				// @formatter:off
//				String dumb = (dumbTricks != targetTricks - 1) ? "x" : " ";
//				String dds  = (ddsTricks != targetTricks) ? "x" : " ";
//				System.out.println("df:" + dealFilter + "  i: " + i + "   min: " + min + "  max: " + max 
//						      + "    targ: " + targetTricks 
//						      + "    dumb: " + dumbTricks + " " + dumb
//						      + "    dds: " + ddsTricks + " " + dds
//						      + "    md|" + d.cardsForLinSave() + "|");
//				// @formatter:on

				if (ddsOk == false) {
					ddsOk = true;
					please_swap = true;
					// so now we have one that is at least the match to the dds
				}

				if (dumbTricks == targetTricks)
					continue; // reject this deal

				success = true;
			}

			please_swap = true;
			continue;
		}

		App.newDealAsRequested = success;

		bestSoFar.clearAllStrategies();
		bestSoFar.wipeContractAndPlay();

		if (presetTheContract && (((youSeatForNewDeal.v % 2) == Dir.EW) || (southBiddingRequired(criteria) == false))) {
			bestSoFar.autoBidContract(criteria);
		}

//		System.out.println("df:" + dealFilter + "  i: " + i + "    Board        md|" + bestSoFar.cardsForLinSave() + "|  ");

		return bestSoFar;
	}

	/**
	 *  South MUST BE the declarer
	 */
	public void dumbAutoPlayToEnd_with_SOME_DDS_Assistance(int dealFilter) {
		// =============================================================================
		DumbAutoDirectives dumbAutoDir = new DumbAutoDirectives();
		dumbAutoDir.yourFinessesMostlyFail = App.yourFinessesMostlyFail || (dealFilter == 4);
		dumbAutoDir.defenderSignals = App.defenderSignals;

		int stopAssistanceAfter = -1; // if unchanged means no assistance

		if (dealFilter == 2) {
			stopAssistanceAfter = 7;
		}
		else if (dealFilter == 3) {
			stopAssistanceAfter = 9;
		}
		else if (dealFilter == 4) {
			// auto your finesses mostly fail is switched on above
			stopAssistanceAfter = 9;
		}
		else {
			// no change
		}

		clearAllStrategies();

		for (int i = 0; i < 13; i++) {
			Hand leader = getCurTrickLeader();
			for (Hand hand : rota[leader.compass.v]) {

				Card card = hand.dumbAuto(dumbAutoDir);

				if (App.haglundsDDSavailable) {
					if ((i < stopAssistanceAfter) || (hand.compass == Dir.West) || (hand.compass == Dir.East)) {
						card = Z_ddsCalculate.improveDumbPlay(hand, card);
					}
				}
				hand.playCard(card);
			}
		}
	}

	/**
	 */
	void autoBidContract(String criteria) {
		// ==============================================================================================

		// add passes until we get to South
		for (Hand hand : rota[dealer.v]) {
			if (hand.compass == Dir.South) {
				break;
			}
			makeBid(new Bid(Call.Pass));
		}

		makeBid(generateSouthBid(criteria));

		Bid bid = null;
		while (isBidding()) {
			for (Hand hand : rota[Dir.West.v]) {

				if (hand.compass == Dir.East) {
					bid = generateEastWestBid(hand);
				}
				else if (hand.compass == Dir.West) {
					bid = generateEastWestBid(hand);
				}
				else if (hand.compass == Dir.North) {
					bid = new Bid(Call.Pass);
				}
				else if (hand.compass == Dir.South) {
					bid = generateSouthBid(criteria);
				}
				makeBid(bid);

				if (isBidding() == false)
					break;
			}
		}
	}

	/**
	 */
	public static String validateDealCriteria(String in) {
		// ==============================================================================================

		if (in.contentEquals("userBids"))
			return in;
		else if (in.contentEquals("chosenGame28"))
			return in;
		else if (in.contentEquals("chosenGame27"))
			return in;
		else if (in.contentEquals("chosenGame26"))
			return in;
		else if (in.contentEquals("chosenGame25"))
			return in;
		else if (in.contentEquals("chosenGame24"))
			return in;
		else if (in.contentEquals("twoSuitSlam_E1"))
			return in;
		else if (in.contentEquals("twoSuitSlam_E2"))
			return in;
		else if (in.contentEquals("twoSuitSlam_M1"))
			return in;
		else if (in.contentEquals("twoSuitSlam_M2"))
			return in;
		else if (in.contentEquals("twoSuitSlam_I1"))
			return in;
		else if (in.contentEquals("twoSuitSlam_I2"))
			return in;
		else if (in.contentEquals("twoSuitSlam_H1"))
			return in;
		else if (in.contentEquals("twoSuitSlam_H2"))
			return in;
		else if (in.contentEquals("ntSmall_M"))
			return in;
		else if (in.contentEquals("ntGrand_E"))
			return in;
		else if (in.contentEquals("ntGrand_M"))
			return in;
		else if (in.contentEquals("ntGrand_H"))
			return in;

		return "ntGrand_E";
	}

	/**
	 *   
	 */
	public static boolean southBiddingRequired(String criteria) {
		// ==============================================================================================
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
		// ==============================================================================================
		Bid bid = null;

		Bid high = getHighestBid();

		if (high.isPass() == false) {
			bid = new Bid(Call.Pass);
			Hand bidder = getHandThatMadeBid(high);
			if (bidder.axis() == hands[Dir.South.v].axis()) {
				Bid dr = this.getLastDblOrRedblAfter(high);
				if (dr.isDouble() && vulnerability[0]) {
					bid = new Bid(Call.ReDouble);
				}
			}
		}

		else if (criteria.startsWith("userBids")) {
			bid = pickGoodNorthSouthGameContract(-1);
		}

		else if (criteria.startsWith("chosenGame")) {
			bid = pickGoodNorthSouthGameContract(0);
		}

		else if (criteria.startsWith("twoSuitSlam")) {
			bid = pickGoodNorthSouthSlamContract();
		}

		else if (criteria.startsWith("underTest")) {
			bid = pickGoodNorthSouthSlamContract();
		}

		else if (criteria.startsWith("ntSmall")) {
			bid = new Bid(Level.Six, Suit.NoTrumps);
		}

		else if (criteria.startsWith("ntGrand")) {
			bid = new Bid(Level.Seven, Suit.NoTrumps);
		}

		assert (bid != null);
		return bid;
	}

	/**
	 *  
	 */
	public Bid generateEastWestBid(Hand hand) {
		// ==============================================================================================
		Bid bid = new Bid(Call.Pass);

		Bid high = getHighestBid();
		if (high.isPass() == false) {
			Hand bidder = getHandThatMadeBid(high);
			if (bidder.axis() == hands[Dir.South.v].axis()) {
				Bid dr = this.getLastDblOrRedblAfter(high);
				if (dr.isNullBid()) {
					// so now I can check to see if we are playing aginst 7N
					if (high.suit == Suit.NoTrumps && high.level == Level.Seven) {
						if (hand.doesHandHaveAKingOrAce()) {
							bid = new Bid(Call.Double);
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
		// ==============================================================================================
		if (wanted == 0)
			return true;

		int count = 0;

		for (Frag frag : north().frags) {
			if ((frag.isEmpty() == false) && (frag.get(0).rank == Rank.Ace))
				count++;
		}
		for (Frag frag : south().frags) {
			if ((frag.isEmpty() == false) && (frag.get(0).rank == Rank.Ace))
				count++;
		}
		return (count >= wanted);
	}

	/**
	 */
	public void setNextDealerAndVulnerability(int oldBoardNo) {
		// ==============================================================================================
		realBoardNo = oldBoardNo;
		realBoardNo++;
		if (realBoardNo > 16) {
			realBoardNo = 1;
		}
		Zzz.BoardData board = Zzz.getBoardData(realBoardNo);

		dealer = board.dealer;
		boolean v0 = board.vulnerability[0];
		boolean v1 = board.vulnerability[1];
		vulnerability[0] = v0;
		vulnerability[1] = v1;
	}

	/**
	 */
	public void rotateDealerAndVulnerability(int amountOfRotaion) {
		// ==============================================================================================
		dealer = dealer.rotate(amountOfRotaion + 4);

		if ((amountOfRotaion + 4) % 2 == 1) {
			boolean kept0 = vulnerability[0];
			boolean kept1 = vulnerability[1];

			vulnerability[0] = kept1;
			vulnerability[1] = kept0;
		}

		// find the boardnumber that matches
		realBoardNo = 0; // so misses will be harmless - but there should not be any
		for (int i = 1; i <= 16; i++) {
			BoardData b = Zzz.getBoardData(i);
			if (b.dealer == dealer && vulnerability[0] == b.vulnerability[0] && vulnerability[1] == b.vulnerability[1]) {
				realBoardNo = i;
				break;
			}
		}
	}

	/**
	 */
	public void setVulnerability(String s) {
		// ==============================================================================================
		if (s.isEmpty())
			s = "o";

		// @formatter:off
		switch (s.charAt(0)) {
			case 'b': case 'B':                     vulnerability[Dir.NS] = true;  vulnerability[Dir.EW] = true;  break;
			case 'n': case 'N': case 's': case 'S': vulnerability[Dir.NS] = true;  vulnerability[Dir.EW] = false; break;
			case 'e': case 'E': case 'w': case 'W': vulnerability[Dir.NS] = false; vulnerability[Dir.EW] = true;  break;
			default :                               vulnerability[Dir.NS] = false; vulnerability[Dir.EW] = false; break;
		}
		// @formatter:on

		vulnerability = vulnerability.clone();
		rotateDealerAndVulnerability(0); // NOTE - rotation is zero so just gets correct board no.
		@SuppressWarnings("unused")
		int z = 0;
	}

	/**
	 */
	public void adjustYouSeatHint(String s) {
		// ==============================================================================================
		if (s.isEmpty())
			return;

		int adjust = 0;

		// @formatter:off
		switch (s.charAt(0)) {
			case 'l': case 'L':  adjust = 1;  break;
			case 'c': case 'C':  adjust = 2;  break;
			case 'r': case 'R':  adjust = 3;  break;
			default : return;
		}
		// @formatter:on

		if (youSeatHint == Dir.Invalid) // can it be !
			youSeatHint = Dir.South;

		int d = (youSeatHint.v + adjust) % 4;

		youSeatHint = Dir.directionFromInt(d);
	}

	public void setYouSeatHint(String s) {
		// ==============================================================================================
		if (s.isEmpty())
			s = "s";

		youSeatHint = Dir.directionFromChar(s.charAt(0));

		if (youSeatHint == Dir.Invalid)
			youSeatHint = Dir.South;
	}

	/**
	 */
	public void setYouSeatHint(Dir you) {
		// ==============================================================================================
		youSeatHint = you;

		if (youSeatHint == Dir.Invalid)
			youSeatHint = Dir.South;
	}

	/**
	 */
	public void setHeader(String s) {
		// ==============================================================================================
		ahHeader = s;
	}

	/**
	 */
	public void redeal() {
		// ===============================================================================================

		contractCompass = Dir.Invalid;
		contract = new Bid(Call.NullBid);
		contractDblRe = new Bid(Call.NullBid);

		for (Hand h : hands) {
			h.setToVirgin();
		}

		packPristine.clear();

		prevTrickWinner.clear(); // there should not be any yet !!!

		// create the pnew ack with the cards
		for (Suit suit : Suit.cdhs) {
			for (Rank rank : Rank.allThriteenRanks) {
				packPristine.add(new Card(rank, suit));
			}
		}
		Collections.shuffle(packPristine);

		while (!packPristine.isEmpty()) {
			for (Hand h : hands) {
				h.addDeltCard(packPristine.remove(packPristine.size() - 1));
			}
		}

		// make NS have the best hands
		{
			Arrays.sort(hands); // the best two hands are now in pos 0 (North) and 1 (East)

			Hand x = hands[Dir.South.v];
			hands[Dir.South.v] = hands[Dir.North.v];
			hands[Dir.North.v] = hands[Dir.East.v];
			hands[Dir.East.v] = x;

			for (Dir p : Dir.nesw) {
				hands[p.v].compass = p;
				hands[p.v].compassCh = p.toLowerChar();
			}

			// for EASE Of ACCESS - Create a Hands rota
			// like the Dir.rota for the indexes, but for the hands
			for (Dir row : Dir.nesw) {
				int i = 0;
				for (Dir col : Dir.rota[row.v]) {
					rota[row.v][i++] = hands[col.v];
				}
			}

			nsSummary.NsSummarize(this);
		}

	}

	/**
	 */
	public boolean isAnySuitLongerThan7() {
		// ===============================================================================================
		for (Hand h : hands) {
			for (Frag forg : h.fOrgs) {
				if (forg.size() > 7)
					return true;
			}
		}
		return false;
	}

	/**
	 * adjust is a tweek when 'you' is defender and therefor does not set the contract for a partscore
	 * this sets it for him
	 */
	public Bid pickGoodNorthSouthGameContract(int adjust) {
		// ==============================================================================================
		int toBeLevel;
		Suit suit;

		nsSummary = new NsSummary(this); // incase it is out of date e.g. by hand edit or load

		int longestCombLen = nsSummary.fSum[0].both();
		int shortestCombLen = nsSummary.fSum[3].both();

		if ((longestCombLen == 7) || (longestCombLen == 8) && (shortestCombLen >= 6)) {
			suit = Suit.NoTrumps;
			toBeLevel = 3 + adjust;
		}
		else {
			suit = nsSummary.bestTrumpSuit();
			toBeLevel = (suit.v >= Suit.Hearts.v) ? 4 + adjust : 5 + (2 * adjust);
			if (toBeLevel == 5 && longestCombLen < 9) { // lets not play in an 8 card minor game
				suit = Suit.NoTrumps;
				toBeLevel = 3 + adjust;
			}
		}

		return new Bid(Level.levelFromInt(toBeLevel), suit);
	}

	/**
	 */
	public Bid pickGoodNorthSouthSlamContract() {
		// ==============================================================================================

		nsSummary = new NsSummary(this); // incase it is out of date e.g. by hand edit or load

		Suit suit = nsSummary.bestTrumpSuit();
		Level level = Level.Six;

		return new Bid(level, suit);
	}

	/** 
	 */
	public String contractAndResShort(boolean hideResults) {
		// ==============================================================================================
		if (contract.isNullBid()) {
			return "- bid";
		}
		if (contract.isPass()) {
			return "- PO";
		}

		Point tricks = getContractTrickCountSoFar();

		String s;
		s = contractCompass.toStr() + " ";
		s += contract.level.toChar() + "" + contract.suit.toStr() + doubleOrRedoubleStringX();

		if (isFinished() && hideResults == false) {

			int trickDiff = tricks.x - (6 + contract.level.v);

			if (trickDiff > 0) {
				s += "+" + Integer.toString(trickDiff);
			}
			else if (trickDiff == 0) {
				s += "=";
			}
			else {
				s += "-" + Integer.toString(-trickDiff);
			}
		}

		return s;
	}

	/** 
	 */
	public String contractAndResult() {
		// ==============================================================================================
		if (contract.isNullBid()) {
			return "Not-Yet-Bid";
		}
		if (contract.isPass()) {
			return "Passed-Out";
		}

		Point tricks = getContractTrickCountSoFar();

		String s;
		s = contract.level.toStr() + contract.suit.toStr() + doubleOrRedoubleStringX();
		s += "-by-" + contractCompass.toLongStr() + "__";
		s += contractCompass.toAxisStr() + "-" + Integer.toString(tricks.x) + "_";
		s += contractCompass.nextClockwise().toAxisStr() + "-" + Integer.toString(tricks.y);

		if (isFinished()) {
			s += "__";

			int trickDiff = tricks.x - (6 + contract.level.v);

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
		// ==============================================================================================
		if (contractDblRe.isDouble())
			return new String("*");
		if (contractDblRe.isReDouble())
			return new String("**");
		return new String("");
	}

	/**
	 */
	public String doubleOrRedoubleStringX() {
		// ==============================================================================================
		if (contractDblRe.isDouble())
			return new String("x");
		if (contractDblRe.isReDouble())
			return new String("xx");
		return new String("");
	}

	/**
	 * The incomming playerNames start from the SOUTH seat  where as the aaBridge has North at index 0;
	 */
	public void setPlayerNames(ArrayList<String> as) {
		// ==============================================================================================
		if (as.size() >= 4) {

			int useOtherSet = 0;

			if (as.size() >= 8 && as.get(0).isEmpty() && as.get(1).isEmpty() && as.get(2).isEmpty() && as.get(3).isEmpty()) {
				useOtherSet = 4;
			}

			for (int i : Zzz.zto3) {
				hands[i].playerName = as.get(useOtherSet + (i + 2) % 4);
			}
		}
	}

	/**
	 * The incomming playerNames start from the SOUTH seat  where as the aaBridge has North at index 0;
	 */
	public void clearPlayerNames() {
		// ==============================================================================================
		for (int i : Zzz.zto3) {
			hands[i].playerName = "";
		}
	}

	/**
	 */
	public Hand handFromCompass(Dir compass) {
		return hands[compass.v];
	}

	/**
	 */
	public Hand nextHand(Hand hand) {
		return hands[(hand.compass.v + 1) % 4];
	}

	/**
	 */
	public Hand prevHand(Hand hand) {
		return hands[(hand.compass.v + 3) % 4];
	}

	/**
	 */
	public boolean isBidding() {
		return (contract.isNullBid());
	}

	/**
	 */
	public boolean isContractReal() {
		return !((contract.isPass()) || (contract.isNullBid()));
	}

	/**
	 */
	public boolean isDeclarerValid() {
		return !((contract.isPass()) || (contract.isNullBid()));
	}

	/**
	 */
	public Dir getTheYouSeat(boolean dummyOk) {
		if (!dummyOk && isDeclarerValid() && (youSeatHint.rotate(Dir.South) == contractCompass)) {
			return contractCompass; // YouSeat can't be the dummy, it has to be the declarer
		}
		return youSeatHint;
	};

	/**
	 */
	public Dir getTheYouSeat() {
		return getTheYouSeat(false);
	};

	/**
	 */
	public boolean isYouSeatDeclarerAxis() {
		if (isDeclarerValid()) {
			return getTheYouSeat().v % 2 == contractCompass.v % 2;
		}
		return youSeatHint.v % 2 != Dir.NS; // Assume that this means they wont (can't) bid
	};

	/**   
	 */
	public boolean isYouSeatDefenderAxis() {
		if (isDeclarerValid()) {
			return getTheYouSeat().v % 2 != contractCompass.v % 2;
		}
		return youSeatHint.v % 2 == Dir.NS; // Assume that this means they wont (can't) bid
	};

	/**   
	 */
	public Dir getDeclarerOrTheDefaultYouSeat() {
		if (isDeclarerValid()) {
			return contractCompass;
		}
		return youSeatHint;
	};

	/**
	 */
	public boolean isPlaying() {
		return isContractReal() && !endedWithClaim && (prevTrickWinner.size() < 14 || ((prevTrickWinner.size() == 14) && (hideFinish == true)));
	}

	/**
	 */
	public boolean isFinished() {
		return (contract.isPass()) || endedWithClaim || (prevTrickWinner.size() == 14 && (hideFinish == false));
	}

	/**
	 */
	public boolean isTrumps(Suit suit) {
		return contract.suit == suit;
	}

	/**
	 */
	public boolean isLevelAllowed(Level level) {
		Bid bid = getHighestBid();
		if (bid.level.v < level.v)
			return true;
		if ((bid.level.v == level.v) && (bid.suit.v < Suit.NoTrumps.v))
			return true;
		return false;
	}

	/**
	 */
	public int getHighestLevelAllowed() {
		Bid bid = getHighestBid();
		if (bid.suit == Suit.NoTrumps)
			return bid.level.v + 1;
		return bid.level.v;
	}

	/**
	 */
	public boolean isCallAllowed(Call bv) {

		if (bv == Call.Pass)
			return true; // always OK

		assert (bv == Call.Double || bv == Call.ReDouble);
		Bid highest = getHighestBid();
		if (highest.isCall())
			return false; // invalid

		int bAxis = (getNextHandToBid().compass.v) % 2;
		Bid xorxx = getLastDblOrRedblAfter(highest);

		if (xorxx.isReDouble())
			return false; // nowhere to go after a redouble

		if (xorxx.isNullBid()) { // i.e there was NO *previous* Double or Redouble
			if (bv == Call.ReDouble)
				return false; // invalid
			int highAxis = (getHandThatMadeBid(highest).compass.v) % 2;
			if (bAxis == highAxis)
				return false; // doubles must be by the other axis
			return true;
		}

		if (xorxx.isDouble()) {
			if (bv == Call.Double)
				return false; // invalid
			int highAxis = (getHandThatMadeBid(highest).compass.v) % 2;
			if (bAxis != highAxis)
				return false; // redoubles must be by the same axis as the core bid
			return true;
		}

		return false;
	}

	/** 
	 */
	public Card undoLastPlay() {
		// ==============================================================================================
		Hand hand = getLastHandThatPlayed();
		assert (hand != null);
		return hand.undoLastPlay();
	}

	/** 
	 */
	public void undoLastPlays_ignoreTooMany(int numb) {
		// ==============================================================================================
		for (int zz = 0; zz < numb; zz++) {
			Hand hand = getLastHandThatPlayed();
			if (hand == null)
				break; // too many
			hand.undoLastPlay();
		}
	}

	/** 
	 */
	public Card undoLastPlay_ignoreTooMany() {
		// ==============================================================================================
		endedWithClaim = false;
		tricksClaimed = 0;

		Hand hand = getLastHandThatPlayed();
		if (hand == null)
			return null;
		return hand.undoLastPlay();
	}

	/**
	 */
	public void fastUndoBackTo(int reviewTrick, int reviewCard, boolean setDdsNextCard) {
		// ==============================================================================================
		endedWithClaim = false;
		tricksClaimed = 0;
		int cardsPlayed = countCardsPlayed();
		int undoCount = cardsPlayed - (reviewTrick * 4 + reviewCard);
		if (undoCount > 0) {
			undoLastPlays_ignoreTooMany(undoCount);
		}
		Card card = getLastCardPlayed();
		if (card != null && setDdsNextCard)
			card.setDdsNextCard(true);
	}

	/** 
	 */
	public void playLinCard(String cd) {
		// ==============================================================================================

		// PC|qd3| causes NetBridgeVu to play the lowest diamond
		// when neither the q or 3 are present in the hand
		// we need to mimic this PC|d| plays the lowest diamond

		Suit suit = Suit.Invalid;
		Rank rank = Rank.Invalid;
		for (int i = 0; i < cd.length(); i++) { // well there SHOULD BE a max of TWO chars in each string
			char c = cd.charAt(i);
			// @formatter:off
			switch (c) {
				case 'S': case 's': suit = Suit.Spades;   continue;
				case 'H': case 'h': suit = Suit.Hearts;   continue;
				case 'D': case 'd': suit = Suit.Diamonds; continue;
				case 'C': case 'c': suit = Suit.Clubs;    continue;
				default: if (suit == Suit.Invalid)
					continue; // ie ignore any chars before the first good suit
			}
				// @formatter:on

			rank = Rank.charToRank(c);
			if (rank != Rank.Invalid) {
				if (checkCardExternal(suit, rank) == false) {
					rank = Rank.Invalid; // so it will try the lowest
				}
			}
		}

		if (suit != Suit.Invalid) {
			if (rank == Rank.Invalid) {
				// return; !!!
				rank = getLowestCardExternal(suit).rank;
			}
			if (rank != Rank.Invalid) {
				playCardExternal(suit, rank);
			}
			// otherwise dont worry - as the bbo player is still happy then so are we
		}
	}

	/** 
	 */
	public void undoLastBid() {
		// ==============================================================================================
		Hand hand = getLastHandThatBid();
		assert (hand != null);
		assert (hand.bids.size() > 0);
		hand.bids.removeLast();
		if (contract.isNullBid() == false) {
			contractCompass = Dir.South; // logical null
			contract = new Bid(Call.NullBid);
			contractDblRe = new Bid(Call.NullBid);
		}
	}

	/** 
	 */
	public void undoLastBids_ignoreTooMany(int numb) {
		// ==============================================================================================
		for (int zz = 0; zz < numb; zz++) {
			Hand hand = getLastHandThatBid();
			if (hand == null || hand.bids.size() == 0)
				break; // too many

			hand.bids.removeLast();
			if (contract.isNullBid() == false) {
				contractCompass = Dir.South; // logical null
				contract = new Bid(Call.NullBid);
				contractDblRe = new Bid(Call.NullBid);
			}
		}
	}

	/**
	 */
	public void makeBid(Bid b) {
		// ==============================================================================================
		if (contract.isNullBid() == false) {
			return; // we won't add bids unless the contract is null
		}

		if (b.isCall() == false) {
			Bid prev = getHighestBid();
			if (b.isLowerThanOrEqual(prev)) {
				// System.out.println(b + " is invalid - to low");
				return; // they should not have sent this really
			}
		}
		else {
			if (!isCallAllowed(b.getCall()))
				return;
		}

		getNextHandToBid().bids.add(b);

		if (isAuctionFinished()) {

			// assert (contract.isNullBid()); // this fails after a bids have been edited
			// assert (prevTrickWinner.isEmpty()); // this fails after a bids have been edited
			// FFS - when we are at the end of **editing** the bidding
			// the above tests fail (and have therefor been turned off)
			contract = new Bid(Call.NullBid);
			prevTrickWinner.clear();

//			for (Hand hand : rota[Dir.North.v]) {
//				if (hand.played.isEmpty() == false) {
//					@SuppressWarnings("unused")
//					int z = 0; // put your breakpoint here
//				}
//				// assert (hand.played.isEmpty());
//			}

			contract = getHighestBid();

			if (contract.isPass()) {
				contractCompass = Dir.South; // use DIr.Invalid or would an arbritary real direction be better YES - picked South?
				contractDblRe = new Bid(Call.NullBid);
			}
			else {
				contractCompass = getHandThatMadePartnershipFirstCallOfSuit(contract).compass;
				contractDblRe = getLastDblOrRedblAfter(contract);
			}

			// make the first entry, which shows the leader to the first trick
			prevTrickWinner.add(hands[contractCompass.nextClockwise().v]);
		}
	}

	public boolean false_afterBid(Bid prevBid, boolean alert) {
		// ==============================================================================================
		prevBid.alert = alert;

		if (columnsInBidTable == 2) {
			makeBid(new Bid(Call.Pass));
		}

		return false; // to clear any alert
	}

	/**
	* Called with a Lin style bid which (old format) may have MULTIPLE bids in the one string
	* it can be called from the  ia  commands (insert auction) in which case the deal
	* is not   App.deal   but is its own special one  hence it needs to detect  "O"
	* 
	* uses front alert because that is what  "ia" Insert Auction uses - NOT MY DESIGN
	* 
	*/
	public void makeLinBid_FrontAlert(String bids) {
		// ==============================================================================================
		Level level = Level.Invalid;
		Suit suit = Suit.Invalid;
		Bid prevBid = null;
		boolean skipping = true;
		boolean alert = false;

		boolean qmSeen = false;

		for (int i = 0; i < bids.length(); i++) {
			char c = bids.charAt(i);
			if (c == '?') {
				qmSeen = true;
				continue;
			}
			if (skipping && (c == 'O' || c == 'o')) {
				columnsInBidTable = 2;
				continue;
			}
			if (c == '-' || c == ' ' || c == ',') {
				if (skipping)
					dealer = dealer.nextClockwise();
				continue;
			}
			skipping = false;
			if (c == 'p' || c == 'P') {
				makeBid(prevBid = new Bid(Call.Pass));
				alert = false_afterBid(prevBid, alert);
				continue;
			}
			if ((level == Level.Invalid) && (c == 'd' || c == 'D' || c == '*' || c == 'x' || c == 'X')) {
				makeBid(prevBid = new Bid(Call.Double));
				alert = false_afterBid(prevBid, alert);
				continue;
			}
			if (c == 'r' || c == 'R') {
				makeBid(prevBid = new Bid(Call.ReDouble));
				alert = false_afterBid(prevBid, alert);
				continue;
			}
			if (c == '!') {
				alert = true;
				continue;
			}
			if ('1' <= c && c <= '7') {
				level = Level.levelFromInt(c - '0');
				continue;
			}
			// @formatter:off
			switch (c) {
				case 'N': case 'n': suit = Suit.NoTrumps; break;
				case 'S': case 's': suit = Suit.Spades;   break;
				case 'H': case 'h': suit = Suit.Hearts;   break;
				case 'D': case 'd': suit = Suit.Diamonds; break; // yes d and D do double duty
				case 'C': case 'c': suit = Suit.Clubs;    break;
				default: suit = Suit.Invalid;
			}
			// @formatter:on
			if (suit == Suit.Invalid || level == Level.Invalid) {
				return;
			}

			makeBid(prevBid = new Bid(level, suit));
			alert = false_afterBid(prevBid, alert);

			// System.out.println(deal.getNextHandToBid() + " " + prevBid + "  ");

			level = Level.Invalid;
			suit = Suit.Invalid;
		}

		showBidQuestionMark = qmSeen;

//		if (prevBid != null && bbNext.type.contentEquals("an") && bbNext.size() == 1) { // anouncement ?
//			prevBid.alert = true;
//			prevBid.alertText = bbNext.get(0);
//		}

	}

	/**
	*    used by drop of web links and web text 'lins'
	*/
	public void makeLinBid_RearAlert(String bids) {
		// ==============================================================================================
		Level level = Level.Invalid;
		Suit suit = Suit.Invalid;
		Bid prevBid = null;
		boolean skipping = true;
		boolean alert = false;

		boolean qmSeen = false;

		boolean eating_alert_text = false;
		String alertText = "";

		for (int i = 0; i < bids.length(); i++) {
			char c = bids.charAt(i);
			if (c == ')') {
				eating_alert_text = false;
				continue;
			}
			if (c == '(') {
				eating_alert_text = true;
				continue;
			}
			if (eating_alert_text) {
				alertText += c;
				continue;
			}
			if (c == '?') {
				qmSeen = true;
				continue;
			}
			if (skipping && (c == 'O' || c == 'o')) {
				columnsInBidTable = 2;
				continue;
			}
			if (c == '-' || c == ' ' || c == ',') {
				if (skipping)
					dealer = dealer.nextClockwise();
				continue;
			}
			skipping = false;
			if (c == 'p' || c == 'P') {
				/* do any previous alert */
				if (alert || alertText.isEmpty() == false) {
					try {
						alertText = URLDecoder.decode(alertText, "UTF-8").trim();
					} catch (UnsupportedEncodingException e) {
					}
					prevBid.alert = true;
					prevBid.alertText = alertText;
					alert = false;
					alertText = "";
				}
				makeBid(prevBid = new Bid(Call.Pass));
				level = Level.Invalid;
				suit = Suit.Invalid;
				continue;
			}
			if ((level == Level.Invalid) && (c == 'd' || c == 'D' || c == '*' || c == 'x' || c == 'X')) {
				makeBid(prevBid = new Bid(Call.Double));
				alert = false_afterBid(prevBid, alert);
				continue;
			}
			if (c == 'r' || c == 'R') {
				makeBid(prevBid = new Bid(Call.ReDouble));
				alert = false_afterBid(prevBid, alert);
				continue;
			}
			if (c == '!') {
				prevBid.alert = true;
				alert = true;
				continue;
			}
			if ('1' <= c && c <= '7') {
				level = Level.levelFromInt(c - '0');
				continue;
			}
			// @formatter:off
			switch (c) {
				case 'N': case 'n': suit = Suit.NoTrumps; break;
				case 'S': case 's': suit = Suit.Spades;   break;
				case 'H': case 'h': suit = Suit.Hearts;   break;
				case 'D': case 'd': suit = Suit.Diamonds; break; // yes d and D do double duty
				case 'C': case 'c': suit = Suit.Clubs;    break;
				default: suit = Suit.Invalid;
			}
			// @formatter:on
			if (suit == Suit.Invalid || level == Level.Invalid) {
				return;
			}

			/* do any previous alert */
			if (alert || alertText.isEmpty() == false) {
				/* do any previous alert */
				try {
					alertText = URLDecoder.decode(alertText, "UTF-8").trim();
				} catch (UnsupportedEncodingException e) {
				}
				prevBid.alert = true;
				prevBid.alertText = alertText;
				alert = false;
				alertText = "";
			}

			makeBid(prevBid = new Bid(level, suit));

			level = Level.Invalid;
			suit = Suit.Invalid;
		}

		showBidQuestionMark = qmSeen;

	}

	/**
	 */
	public void addAnouncementToLastBid(String anounceText) {
		// ==============================================================================================
		Hand hand = getLastHandThatBid();
		if (hand == null || hand.bids.size() == 0)
			return;
		Bid bid = hand.bids.getLast();
		bid.alert = true;
		bid.alertText = Aaa.deAtAlertText(anounceText);
	}

	/**
	 */
	public void wipePlay() {
		// ==============================================================================================
		// set the frags to match the original cards and wipe the played
		clearAllStrategies();
		App.deal.endedWithClaim = false;

		for (Hand hand : rota[Dir.North.v]) {
			for (Suit su : Suit.cdhs) {
				hand.frags[su.v] = (Frag) hand.fOrgs[su.v].clone();
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
		// ==============================================================================================
		// set the frags to match the original cards and wipe the played
		App.deal.endedWithClaim = false;

		for (Hand hand : rota[Dir.North.v]) {
			for (Suit su : Suit.cdhs) {
				hand.frags[su.v] = (Frag) hand.fOrgs[su.v].clone();
			}
			hand.played.clear();
		}
		prevTrickWinner.clear();
		contract = new Bid(Call.NullBid);
		contractDblRe = new Bid(Call.NullBid);
		contractCompass = Dir.Invalid;
		clearAllStrategies();
	}

	/**
	 */
	public void wipeContractBiddingAndPlay() {
		// ==============================================================================================
		// set the frags to match the original cards and wipe the played
		endedWithClaim = false;
		tricksClaimed = 0;

		for (Hand hand : rota[Dir.North.v]) {
			for (Suit su : Suit.cdhs) {
				hand.frags[su.v] = (Frag) hand.fOrgs[su.v].clone();
			}
			hand.bids.clear();
			hand.played.clear();
		}
		displayBoardId = "";
		signfBoardId = "";
		prevTrickWinner.clear();
		contract = new Bid(Call.NullBid);
		contractDblRe = new Bid(Call.NullBid);
		contractCompass = Dir.Invalid;
		clearAllStrategies();
		// the dealer has been preset by the user, that is why this routine exists
	}

	/**
	 */
	public void finishBiddingIfIncomplete() {
		// ==============================================================================================
		while (isAuctionFinished() == false) {
			makeBid(new Bid(Call.Pass));
		}
	}

	/**
	 */
	public Hand getNextHandToBid() {
		// ==============================================================================================
		Hand h = null;
		int size = 999, sn;
		for (Hand hand : rota[dealer.v]) {
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
		// ==============================================================================================
		int n = 0;
		for (Hand hand : rota[dealer.v]) {
			n += hand.bids.size();
		}
		return n;
	}

	/**
	 */
	public Hand getNthHandToBid(int n) {
		// ==============================================================================================
		Hand h = null;
		int size = 999, sn;
		for (Hand hand : rota[dealer.v]) {
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
			if (hand.bids.getLast().isPass() == false)
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
			for (Hand hand : rota[leader.compass.v]) {
				if (hand.played.size() < leader.played.size())
					return hand.prevHand();
			}
		}
		assert (false);
		return null;
	}

	/**
	 */
	public Card getLastCardPlayed() {

		Hand hand = null;

		if (isCurTrickComplete()) {
			if (hands[0/* any */].played.size() == 0)
				return null;
			hand = prevTrickWinner.get(prevTrickWinner.size() - 2).prevHand();
		}
		else {
			Hand leader = getCurTrickLeader();
			for (Hand h : rota[leader.compass.v]) {
				if (h.played.size() < leader.played.size()) {
					hand = h.prevHand();
					break;
				}
			}
		}
		if (hand == null)
			return null;

		return hand.played.getLast();
	}

//	/**
//	 */
//	public Hand cardPlayForLinSavesxxxx() {
//		if (isCurTrickComplete()) {
//			if (hands[0/* any */].played.size() == 0)
//				return null;
//			return prevTrickWinner.get(prevTrickWinner.size() - 2).prevHand();
//		}
//		else {
//			Hand leader = getCurTrickLeader();
//			for (Hand hand : rota[leader.compass.v]) {
//				if (hand.played.size() < leader.played.size())
//					return hand.prevHand();
//			}
//		}
//		assert (false);
//		return null;
//	}

	public String cardPlayForLinSave() {
		String s = "";

		String eol_or_blank = (App.saveAsBboUploadFormat) ? "" : Zzz.get_lin_EOL();
		String extra_space = (App.saveAsBboUploadFormat && App.saveAsBboUploadExtraS) ? " " : "";

		int countPlayed = countCardsPlayed();
		if (endedWithClaim == false && countPlayed == 0) {
			return s;
		}

		int tk = 0;
		for (int i = 0; i < countPlayed; i++) {
			Card card = getCardThatWasPlayed(i);
			s += "pc|" + card.suit.toCharLower() + "" + card.rank.toChar() + "|";
			if (i == 0 && countPlayed > 1) {
				s += "pg|" + extra_space + "|"; // add an 'extra' pg|| after the first card
			}
			tk = (tk + 1) % 4;
			if (tk == 0) {
				s += "pg|" + extra_space + "|" + eol_or_blank;
			}
		}
		if (tk != 0) // we ended part way through a trick
			s += "pg|" + extra_space + "|" + eol_or_blank;

		if (endedWithClaim) {
			s += "mc|" + tricksClaimed + "|pg|" + extra_space + "|" + eol_or_blank;
		}

		return s;
	}

	/**
	 *  index is the card play order in the range (0 - 51) really (0 - our last entry)
	 */
	public Card getCardThatWasPlayed(int index) {
		int trick = index / 4;
		int turn = index % 4;
		if (prevTrickWinner.size() < trick)
			return null;
		int histLeaderCompass = prevTrickWinner.get(trick).compass.v;
		Hand ourPlayer = hands[(histLeaderCompass + turn) % 4];
		if (ourPlayer.played.size() <= trick)
			return null;
		return ourPlayer.played.get(trick);
	}

	public int getCompassThatWasPlayed(int index) {
		int trick = index / 4;
		int turn = index % 4;
		if (prevTrickWinner.size() < trick)
			return 0;
		int histLeaderCompass = prevTrickWinner.get(trick).compass.v;
		return (histLeaderCompass + turn) % 4;
	}

	/**
	 */
	private Hand getHandThatMadeBid(Bid bid) {

		int rounds = hands[dealer.v].bids.size();
		for (int r = 0; r < rounds; r++) {
			for (Hand hand : rota[dealer.v]) {
				if (hand.bids.size() == r)
					break;
				if (hand.bids.get(r) == bid)
					return hand;
			}
		}
		@SuppressWarnings("unused")
		int z = 0; // put your breakpoint here

		// if the .lin is corupt - e.g. the bidding is incomplete
		// but the play is supplied we can end up here - this is OK
		// assert (false);
		return null;
	}

	private Hand getHandThatMadePartnershipFirstCallOfSuit(Bid bid) {

		if (bid.isPass())
			return null;

		Hand bidHand = getHandThatMadeBid(bid);

		int rounds = hands[dealer.v].bids.size();
		for (int r = 0; r < rounds; r++) {
			for (Hand hand : rota[dealer.v]) {
				if (hand.bids.size() == r)
					break;
				if (hand.axis() != bidHand.axis())
					continue;
				if (hand.bids.get(r).suit == bid.suit)
					return hand;
			}
		}
		assert (false);
		return null;
	}

	String bidsForLinSave() {
		String s = "";
		int rounds = hands[dealer.v].bids.size();
		for (int r = 0; r < rounds; r++) {
			for (Hand hand : rota[dealer.v]) {
				if (hand.bids.size() == r)
					break;
				Bid b = hand.bids.get(r);
				s += "mb|" + b.toLinStr();
				if (b.alert)
					s += "!";
				s += "|";
				if (b.alert && b.alertText.length() > 0)
					s += "an|" + b.alertText + "|";
			}
		}
		return s;
	}

	/**
	 */
	private Bid getLastDblOrRedblAfter(Bid bid) {

		Bid last = new Bid(Call.NullBid);
		boolean bidFound = false;
		int rounds = hands[dealer.v].bids.size();
		for (int r = 0; r < rounds; r++) {
			for (Hand hand : rota[dealer.v]) {
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
				if (b.isPass())
					continue;
				last = b;
			}
		}
		return last;
	}

	/**
	 */
	public boolean isBiddingInteresting() {
		int rounds = hands[dealer.v].bids.size();
		int interesting = 0;
		for (int r = 0; r < rounds; r++) {
			for (Hand hand : rota[dealer.v]) {
				if (hand.bids.size() == r)
					break;
				Bid cb = hand.bids.get(r);
				if (cb.isPass())
					continue;
				interesting++;
			}
		}
		return (interesting > 1);
	}

	/**
	 */
	private Bid getHighestBid() {
		Bid pb = new Bid(Call.Pass);
		int rounds = hands[dealer.v].bids.size();

		for (int r = 0; r < rounds; r++) {
			for (Hand hand : rota[dealer.v]) {
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

		int rounds = hands[dealer.v].bids.size();
		int countPass = 0;
		int skip = 0;
		for (int r = 0; r < rounds; r++) {
			for (Hand hand : rota[dealer.v]) {
				if (hand.bids.size() == r)
					return false;
				Bid cb = hand.bids.get(r);
				if ((skip++ != 0) && (cb.isPass()))
					countPass++;
				else
					countPass = 0;
				if (countPass == 3)
					return true;
			}
		}
		return false;
	}

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
		for (Hand hand : rota[leader.compass.v]) {
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
		if (leader == null)
			return null;

		for (Hand hand : rota[leader.compass.v]) {
			if (hand.played.size() == curTrickIndex) {
				return hand;
			}
		}
		@SuppressWarnings("unused")
		int z = 0; // put your breakpoint here

//		assert (false);
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
//		int s[] = { 0, 0, 0, 0 };
//		for (Cpe compass : Dir.nesw) {
//			s[compass.v] = hands[compass.v].played.size();
//		}
//		return ((s[0] == s[1]) && (s[1] == s[2]) && (s[2] == s[3]));
		return (countCardsPlayed() % 4 == 0);
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
	public int countOrigCards() {
		int t = 0;
		for (Hand hand : hands) {
			t += hand.countOriginalCards();
		}
		return t;
	}

	/**
	 */
	public int countTrumpsPlayed() {
		int t = 0;
		for (Hand hand : hands) {
			t += hand.played.countSuit(contract.suit);
		}
		return t;
	}

	/**
	 */
	public boolean isCurTrickThreeCards() {
		return (countCardsPlayed() % 4) == 3;
//		Hand leader = getCurTrickLeader();
//		int s[] = { 0, 0, 0, 0 };
//		for (int seat : Dir.rota[leader.compass.v]) {
//			s[seat] = hands[seat].played.size();
//		}
//		return ((s[0] == s[1]) && (s[1] == s[2]) && (s[2] == s[3] + 1));
	}

	public void claimTricks(int i) {
		// ==============================================================================================
		endedWithClaim = true;
		tricksClaimed = i;
	}

	/**
	 * returns a point - x is the decelarers tricks and y is defenders
	 */
	public Point getContractTrickCountSoFar() {
		// ==============================================================================================
		Point trickCount = new Point(); // comes set to 0,0

		if (endedWithClaim) {
			trickCount.x = tricksClaimed;
			trickCount.y = 13 - trickCount.x;
		}
		else {
			boolean skipFirst = true;
			Dir plA = contractCompass;
			Dir plB = contractCompass.rotate180();
			Dir opA = contractCompass.nextClockwise();
			Dir opB = contractCompass.prevAntiClockwise();

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
		}
		return trickCount;
	}

	/**
	 * returns a point - x is the decelarers tricks and y is defenders
	 */
	public Point getContractTrickCountToTrick(int reviewTrick) {
		// ==============================================================================================
		Point trickCount = new Point(0, 0);
		boolean skipFirst = true;
		Dir plA = contractCompass;
		Dir plB = contractCompass.rotate180();
		Dir opA = contractCompass.nextClockwise();
		Dir opB = contractCompass.prevAntiClockwise();
		int n = 0;
		for (Hand winner : prevTrickWinner) {
			if (skipFirst) {
				skipFirst = false;
				continue;
			} // skip the first as it the leader to the first trick
			n++;
			if (n > reviewTrick)
				break;
			if (winner.compass == plA || winner.compass == plB)
				trickCount.x++;
			if (winner.compass == opA || winner.compass == opB)
				trickCount.y++;
		}
		return trickCount;
	}

	/**
	 * returns a point - x is the decelarers tricks and y is defenders
	 */
	public int getContractTrickCountForDirecton(Dir compass) {
		// ==============================================================================================
		int trickCount = 0;
		boolean skipFirst = true;
		Dir partnerCompass = compass.rotate180();
		for (Hand winner : prevTrickWinner) {
			if (skipFirst) {
				skipFirst = false;
				continue;
			} // skip the first as it the leader to the first trick
			if (winner.compass == compass || winner.compass == partnerCompass)
				trickCount++;
		}
		return trickCount;
	}

	/**
	 */
	void cardJustPlayed() {
		// ==============================================================================================
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
				if (card.isBetterThan(bestCard, contract.suit)) {
					bestHand = hand;
					bestCard = card;
				}
			}
			prevTrickWinner.add(bestHand);
		}
	}

	/**
	 */
	Hand winnerSoFar() {
		// ==============================================================================================
		int cardsPlayed = countCardsPlayed() % 4;
		if (cardsPlayed == 0)
			return null;

		int curTrickIndex = getCurTrickIndex();
		Hand leader = getCurTrickLeader();
		if (cardsPlayed == 1)
			return leader;

		Hand bestHand = leader;
		Card bestCard = leader.played.getCard(curTrickIndex);
		Hand hand = leader;

		for (int i = 1; i < cardsPlayed; i++) {
			hand = leader.nextHand();
			Card card = hand.played.getCard(curTrickIndex);

			if (card.isBetterThan(bestCard, contract.suit)) {
				bestHand = hand;
				bestCard = card;
			}
		}
		return bestHand;
	}

	/**
	 */
	public boolean isNextCardLastInTrick() {
		return isCurTrickThreeCards();
	}

	/**
	 */
	public Hand getHand(Dir compass) {
		return hands[compass.v];
	}

	/**
	 * Declarer sinple score ...
	 */
	public int getBoardSimpleScore() {
		Point p = getBoardScore();
		return p.x + p.y;
	}

	public Point getBoardScore() {
		Point score = new Point(0, 0);

		if (contract.isPass() || !isFinished())
			return score;

		int reMult = (contractDblRe.isReDouble()) ? 2 : 1;
		int dblMult = (reMult == 2 || contractDblRe.isDouble()) ? 2 : 1;
		int vunMult = (vulnerability[contractAxis()]) ? 2 : 1;

		int trickDiff = getContractTrickCountSoFar().x - (contract.level.v + 6);

		if (trickDiff >= 0) { // they made
			score.y = (contract.level.v * Zzz.scoreRate[contract.suit.v] + ((contract.suit == Suit.NoTrumps) ? 10 : 0)) * dblMult * reMult;

			if (contract.level == Level.Seven) {
				score.x = (vunMult == 1) ? 1000 : 1500;
			}
			else if (contract.level == Level.Six) {
				score.x = (vunMult == 1) ? 500 : 750;
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

			if (dblMult == 2) { // doubled or redoubled insult
				score.x += 50 * reMult;
			}

			if (contractDblRe.isNullBid()) { // not doubbled or redoubled
				score.x += trickDiff * Zzz.scoreRate[contract.suit.v];
			}
			else {
				score.x += 100 * trickDiff * vunMult * reMult;
			}
		}
		else { // they went down trickDiff is NEGATIVE

			// score.y is always nil

			if (contractDblRe.isNullBid()) {
				score.x = 50 * vunMult * trickDiff;
			}
			else if (vulnerability[contractAxis()] == false) {
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
			else { // the bidders are vulnerable
				score.x = -200 * reMult;
				trickDiff++;
				if (trickDiff < 0) {
					score.x += -300 * reMult * (-1) * trickDiff;
				}

			}

		}

		// We return the score as seen by North South (as that is how they are currently displayed)

		if (contractAxis() == Dir.EW) {
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
	int countPlayedOfSuit(Suit suit) {
		int count = 0;

		int trick = -1;
		for (Hand leader : prevTrickWinner) {
			trick++;
			for (Hand hand : rota[leader.compass.v]) {
				if (hand.played.size() >= trick)
					break;
				if (hand.played.get(trick).suit == suit) {
					count++;
				}
			}
		}
		return count;
	}

	/** 
	 */
	boolean isActiveCard(Frag fragSkip, Rank rank, Suit suit) {

		for (Hand hand : hands) {
			if (hand.frags[suit.v] == fragSkip)
				continue;
			if (hand.frags[suit.v].getIfRankExists(rank) != null) {
				return true;
			}
			if (hand.played.size() == prevTrickWinner.size() && hand.played.getLast().matches(rank, suit)) {
				return true; // it is in the current trick
			}
		}
		return false; // not in the hands so it has been played
	}

	/** 
	 */
	boolean isActiveCardExcludeTrick(Frag fragSkip, Rank rank, Suit suit) {

		for (Hand hand : hands) {
			if (hand.frags[suit.v] == fragSkip)
				continue;
			if (hand.frags[suit.v].getIfRankExists(rank) != null) {
				return true;
			}
//			if (hand.played.size() == prevTrickWinner.size() && hand.played.getLast().matches(rank, suit)) {
//				return true; // it is in the current trick
//			}
		}
		return false; // not in the hands so it has been played
	}

	/** 
	 *  Set the relative rank for all cards in the hands
	 */
	public void setAllrankRelEquMpat_part1(boolean trick_anal_time, Hand us, Mpat[] mpatAyEqu, Mpat[] mpatAyRel, int positionInTrick, int debug_trickNumb) {

		for (Card c : packPristine) {
			c.rankRel = Rank.Invalid;
			c.rankEqu = Rank.Invalid;
		}

		// temporarily return all played cards (in this trick) max 3 to their hand
		if (trick_anal_time) {
			for (Hand hand : hands) {
				if (hand.played.size() == prevTrickWinner.size()) {
					Card card = hand.played.getLast();
					hand.frags[card.suit.v].addDeltCard(card);
				}
			}
		}

		// calc all the 'rankRel' values for all active cards
		for (Hand hand : hands) {
			for (Frag frag : hand.frags) {
				int inactive = 0;
				int topFace = Rank.Ace.v + 1;
				for (Card c : frag) {
					for (int i = c.rank.v + 1; i < topFace; i++) {
						Rank rank = Rank.rankFromInt(i);
						if (isActiveCardExcludeTrick(frag, rank, c.suit) == false) {
							inactive++;
						}
					}
					topFace = c.rank.v; // next search starts from the prev card
					c.rankRel = Rank.rankFromInt(c.rank.v + inactive);
				}
			}
		}

		// calc all the 'rankEqu' values
		{
			// calc all the 'rankEqu' values - Stage One
			Frag axisBoth[][] = new Frag[2][4];
			for (int i : Zzz.zto3) {
				axisBoth[0][i] = (Frag) (hands[0].frags[i].clone());
				axisBoth[1][i] = (Frag) (hands[1].frags[i].clone());
			}
			hands[0].addPartnersCurrentCards(axisBoth[0]);
			hands[1].addPartnersCurrentCards(axisBoth[1]);
			for (int ax : Dir.axies) {
				for (Frag fragBoth : axisBoth[ax]) {
					int prevRankRel = Rank.Ace.v + 2;
					int curRankEqu = Rank.Ace.v + 2;
					for (Card c : fragBoth) {
						int rankRelV = c.rankRel.v;
						if (rankRelV != prevRankRel - 1) {
							curRankEqu = rankRelV;
						}
						c.rankEqu = Rank.rankFromInt(curRankEqu);
						prevRankRel = rankRelV;
					}
				}
			}

			// calc all the 'rankEqu' values - Stage Two
			for (int ax : Dir.axies) {
				for (Frag fragBoth : axisBoth[ax]) {
					if (fragBoth.size() > 0) {
						int cur_equ = fragBoth.get(0).rankEqu.v;
						int up_equ = (cur_equ == Rank.Ace.v) ? Rank.Ace.v : Rank.King.v;
						for (Card c : fragBoth) {
							if (c.rankEqu.v != cur_equ) {
								cur_equ = c.rankEqu.v;
								up_equ -= 2;
							}
							c.rankEqu = Rank.rankFromInt(up_equ);
						}
					}
				}
			}
		}

		// generate all the mpat and epat "Card Patern matchers"
		if (trick_anal_time) {

			for (Suit su : Suit.cdhs) {
				mpatAyEqu[su.v] = new Mpat(Play_Mpat.Equ, this, su);
				mpatAyRel[su.v] = new Mpat(Play_Mpat.Rel, this, su);
			}
		}

		// undo the earlier card replacement - i.e. remove this tricks played cards from the hand
		//
		// this now happens in _part2
		// if (trick_anal_time) {
		// for (Hand hand : hands) {
		// if (hand.played.size() == prevTrickWinner.size()) {
		// Card card = hand.played.getLast();
		// hand.frags[card.suit.v].remove(card);
		// }
		// }
		// }

	}

	public void setAllrankRelEquMpat_part2(boolean trick_anal_time) {

		// undo the earlier card replacement - i.e. remove this tricks played cards from the hand
		//
		if (trick_anal_time) {
			for (Hand hand : hands) {
				if (hand.played.size() == prevTrickWinner.size()) {
					Card card = hand.played.getLast();
					hand.frags[card.suit.v].remove(card);
				}
			}
		}
	}

	/** 
	 */
	public void moveCardToHandDragTime(Card cardIn, Hand handTo) {
		Rank rank = cardIn.rank;
		Suit suit = cardIn.suit;
		Card card = null;
		Hand handFrom = null;

		clearAllStrategies();

		for (Hand hand : rota[Dir.North.v]) {
			// We scan the original cards, of course
			card = hand.fOrgs[suit.v].getIfRankExists(rank);
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

		handFrom.fOrgs[suit.v].remove(card);
		handTo.fOrgs[suit.v].addDeltCard(card);

		handTo.fOrgs[unwanted.suit.v].remove(unwanted);
		handFrom.fOrgs[unwanted.suit.v].addDeltCard(unwanted);

		// set the frags to match the original cards and wipe the played
		for (Hand hand : rota[Dir.North.v]) {
			for (Suit su : Suit.cdhs) {
				hand.frags[su.v] = (Frag) hand.fOrgs[su.v].clone();
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
		Rank rank = cardIn.rank;
		Suit suit = cardIn.suit;
		Card card = null;
		Hand handFrom = null;

		for (Hand hand : rota[Dir.North.v]) {
			// We scan the original cards, of course
			card = hand.fOrgs[suit.v].getIfRankExists(rank);
			if (card != null) {
				if (handTo == hand) {
					return; // Already in the correct hand
				}
				handFrom = hand;
				break;
			}
		}

		assert (altCard != null);

		handFrom.frags[suit.v].remove(card);
		handTo.frags[suit.v].addDeltCard(card);

		handTo.frags[altCard.suit.v].remove(altCard);
		handFrom.frags[altCard.suit.v].addDeltCard(altCard);

		handFrom.fOrgs[suit.v].remove(card);
		handTo.fOrgs[suit.v].addDeltCard(card);

		handTo.fOrgs[altCard.suit.v].remove(altCard);
		handFrom.fOrgs[altCard.suit.v].addDeltCard(altCard);

		clearAllStrategies(); // As they are now invalid (as the hands have changed)
	}

	public void clearAllStrategies() {
		for (Hand hand : rota[Dir.North.v]) {
			hand.clearStrategy();
		}
	}

	public Card getLowestCardExternal(Suit suit) {
		Hand hand = getNextHandToPlay();
		return hand.frags[suit.v].getLast();
	}

	public Card getCardExternal(Suit suit, Rank rank) {
		Hand hand = getNextHandToPlay();
		return hand.frags[suit.v].getIfRankExists(rank);
	}

	public boolean checkCardExternal(Suit suit, Rank rank) {
		Hand hand = getNextHandToPlay();
		Card card = hand.frags[suit.v].getIfRankExists(rank);
		return (card != null);
	}

	public boolean playCardExternal(Suit suit, Rank rank) {
		Hand hand = getNextHandToPlay();
		Card card = hand.frags[suit.v].getIfRankExists(rank);
		if (card == null) {
			@SuppressWarnings("unused")
			int z = 0; // put your breakpoint here
			return false;
		}
		assert (card != null);
		hand.playCard(card);
		return true;
	}

	public void setLastTrickWinnerExternal(String s) {
		// DUMB incomplete hand mode only
		s += "s";
		Dir d = Dir.directionFromChar(s.charAt(0));
		// we may be here to overrule a previous trick winner
		if (prevTrickWinner.size() > 0) {
			Dir next = getNextHandToPlay().compass;
			if (d.v == next.v)
				return; // as there is nothing we need do
			prevTrickWinner.remove(prevTrickWinner.size() - 1);
		}
		prevTrickWinner.add(hands[d.v]);
	}

	public static int convertToImps(int i) {
		int n = (i < 0) ? -1 : 1;

		// @formatter:off
		i = i * n;
		if (i <=   10) return       0;
		if (i <=   40) return  n *  1;
		if (i <=   80) return  n *  2;
		if (i <=  120) return  n *  3;
		if (i <=  160) return  n *  4;
		if (i <=  210) return  n *  5;
		if (i <=  260) return  n *  6;
		if (i <=  310) return  n *  7;
		if (i <=  360) return  n *  8;
		if (i <=  420) return  n *  9;
		if (i <=  490) return  n * 10;
		if (i <=  590) return  n * 11;
		if (i <=  740) return  n * 12;
		if (i <=  890) return  n * 13;
		if (i <= 1090) return  n * 14;
		if (i <= 1290) return  n * 15;
		if (i <= 1490) return  n * 16;
		if (i <= 1740) return  n * 17;
		if (i <= 1990) return  n * 18;
		if (i <= 2240) return  n * 19;
		if (i <= 2490) return  n * 20;
		if (i <= 2990) return  n * 21;
		if (i <= 3490) return  n * 22;
		if (i <= 3990) return  n * 23;
		               return  n * 24;
		// @formatter:on
	}

	public int completenessScore() {
		if (isSaveable() == false)
			return 0;
		if (isPlaying() || isFinished()) {
			int countPlayed = countCardsPlayed();
			return 100 + countPlayed + 60 * ((countPlayed > 0) && isFinished() ? 1 : 0);
		}
		if (isBidding()) {
			return 1 + countBids();
		}
		return 0;
	}

	/** 
	 */
	public boolean hasShufOpRestrictions() {
		// ==============================================================================================
		return ((eb_min_card > 0) || areAnyCardsKept());
	}

	/** 
	 */
	public void shufOp_ShuffleDefendersHands() {
		// ==============================================================================================

		for (Hand hand : hands) {
			if (hand.countOriginalCards() > 13)
				return; // we have to be sensible here :)
		}

		int goodGuys;

		if (this.contract.isValidBid()) {
			goodGuys = contractAxis();
		}
		else {
			int ns = hands[Dir.North.v].count_HighCardPoints() + hands[Dir.South.v].count_HighCardPoints();
			int ew = hands[Dir.East.v].count_HighCardPoints() + hands[Dir.West.v].count_HighCardPoints();
			goodGuys = (ns >= ew) ? Dir.NS : Dir.EW;
		}

		// fill in any un-delt cards

		if (!packPristine.isEmpty()) {
			Collections.shuffle(packPristine);
			for (Hand hand : hands) {
				while (!packPristine.isEmpty() && (hand.countOriginalCards() < 13)) {
					hand.addDeltCard(packPristine.remove(packPristine.size() - 1));
				}
			}
		}

		Hand hds[] = new Hand[2];

		if (goodGuys == Dir.NS) {
			hds[0] = hands[Dir.East.v];
			hds[1] = hands[Dir.West.v];
		}
		else {
			hds[0] = hands[Dir.North.v];
			hds[1] = hands[Dir.South.v];
		}

		if (hasShufOpRestrictions()) {
			int played = this.countCardsPlayed();
			undoLastPlays_ignoreTooMany(played - eb_min_card);

			for (Hand hand : hds) {
				hand.restoreUnplayedCardsToDeck_skipKept();
			}
		}
		else {
			wipePlay();

			for (Hand hand : hds) {
				for (Cal frag : hand.frags) {
					frag.clear();
				}
				for (Cal fOrg : hand.fOrgs) {
					packPristine.addAll(fOrg);
					fOrg.clear();
				}
			}
		}

		Collections.shuffle(packPristine);

		for (Hand hand : hds) {
			while (!packPristine.isEmpty() && (hand.countOriginalCards() < 13)) {
				hand.addDeltCard(packPristine.remove(packPristine.size() - 1));
			}
		}

		assert (packPristine.size() == 0);
	}

	/** 
	 */
	// ==============================================================================================
	public void swapSuits(Suit s1, Suit s2) {

		for (Dir p : Dir.nesw) {
			Hand h = hands[p.v];

			// fOrgs
			Frag f = h.fOrgs[s1.v];
			h.fOrgs[s1.v] = h.fOrgs[s2.v];
			h.fOrgs[s2.v] = f;

			h.fOrgs[s1.v].changeSuitTo(s1);
			h.fOrgs[s2.v].changeSuitTo(s2);

			// Frags
			f = h.frags[s1.v];
			h.frags[s1.v] = h.frags[s2.v];
			h.frags[s2.v] = f;

			h.frags[s1.v].changeSuitTo(s1);
			h.frags[s2.v].changeSuitTo(s2);

		}
	}

	public boolean isOrigCardsSame(Deal ad) {

		for (Dir p : Dir.nesw) {
			Hand i_hand = hands[p.v];
			Hand a_hand = ad.hands[p.v];

			// fOrgs
			for (Suit su : Suit.cdhs) {
				Frag i_fOrg = i_hand.fOrgs[su.v];
				Frag a_fOrg = a_hand.fOrgs[su.v];
				if (i_fOrg.size() != a_fOrg.size())
					return false;
				for (int i = 0; i < i_fOrg.size() - 1; i++) {
					Card i_card = i_fOrg.get(i);
					Card a_card = a_fOrg.get(i);
					if (i_card.rank != a_card.rank)
						return false;
				}
			}
		}
		return true;
	}

	public boolean worthAutosavingSaving() {
		if (isDoneHand()) // so we skip the 'done hand'
			return false;

		if (countOrigCards() != 52) {
			return false;
		}

		return true;
	}

	public int getHighestDdsScore() {
		int highest = -1;
		for (Hand hand : hands) {
			if (hand.ddsValuesAssigned) {
				for (Frag frag : hand.frags) {
					for (Card card : frag) {
						if (card.ddsScore > highest) {
							highest = card.ddsScore;
						}
					}
				}
				return highest;
			}
		}
		return highest;
	}

	public int getDdsVulnerability() {
		// @ formatter:off
		if (vulnerability[Dir.NS] && vulnerability[Dir.EW])
			return 1;
		if (vulnerability[Dir.NS] && !vulnerability[Dir.EW])
			return 2;
		if (!vulnerability[Dir.NS] && vulnerability[Dir.EW])
			return 3;
		// @ formatter:on

		return 0;
	}

	public Dir playerMatching(String playerName) {
		if (playerName == null || playerName.isEmpty())
			return null;

		for (Hand hand : hands) {
			if (hand.playerName.compareToIgnoreCase(playerName) == 0 /* match */) {
				return hand.compass;
			}
		}

		return null; // can't use invalid as invalid == North // Dir.Invalid;
	}

	public boolean areAnyCardsKept() {
		for (Hand hand : hands) {
			if (hand.areAnyCardsKept())
				return true;
		}
		return false;
	}

	/** 
	 */
	public void clearAnyKeptCards() {
		this.packPristine.clearAllKeptFlags();
		for (Hand hand : hands) {
			hand.clearAllKeptFlags();
		}
	}

	/** 
	 */
	public String cardsForLinSave() {
		String s = "";
		for (Hand hand : rota[Dir.South.v]) {
			s += hand.cardsForLinSave();
			if (hand.compass != Dir.East)
				s += ',';
		}
		return s;
	}

	public void setDealShowXes(ArrayList<String> as) {

		if (as == null)
			as = new ArrayList<String>(4);

		// @ formatter:off
		if (as.size() == 0)
			as.add("");
		if (as.size() == 1)
			as.add("");
		if (as.size() == 2)
			as.add("");
		if (as.size() == 3)
			as.add("");
		// @ formatter:on

		for (int i : Zzz.zto3) {
			Hand hand = hands[i];
			int k = (i + 2) % 4; // North is zero in the hands and south is zero in the strings
			String s = as.get(k).trim().toLowerCase() + "cccc";
			s = s.substring(0, 4);
			hand.setHandShowXes(s);
		}
	}

	String pbnVulnerability() {

		if (!vulnerability[0] && vulnerability[1])
			return "EW";
		if (vulnerability[0] && !vulnerability[1])
			return "NS";
		if (vulnerability[0] && vulnerability[1])
			return "Both";
		return "None";
	}

	String pbnDeal() {

		String rtn = "N:";

		rtn += hands[Dir.North.v].pdbHand() + " ";
		rtn += hands[Dir.East.v].pdbHand() + " ";
		rtn += hands[Dir.South.v].pdbHand() + " ";
		rtn += hands[Dir.West.v].pdbHand();

		return rtn;
	}

	public void writePbnToSaveableArray(ArrayList<String> ay, String info_text, int new_number) {

		ay.add("[Event \"" + info_text + ", seed 000000000\"]");

		String board_id = displayBoardId.isEmpty() == false ? displayBoardId : (qx_number + "");

		ay.add("[Board \"" + ((new_number > 0) ? new_number : board_id) + "\"]");

//		ay.add("[Date \"" + date_goes_here + "\"]");

		ay.add("[West \"" + hands[Dir.West.v].playerName + "\"]");
		ay.add("[North \"" + hands[Dir.North.v].playerName + "\"]");
		ay.add("[East \"" + hands[Dir.East.v].playerName + "\"]");
		ay.add("[South \"" + hands[Dir.South.v].playerName + "\"]");

		ay.add("[Dealer \"" + dealer.pbnChar() + "\"]");
		ay.add("[Vulnerable \"" + pbnVulnerability() + "\"]");
		ay.add("[Deal \"" + pbnDeal() + "\"]");
		ay.add("[Declarer \"?\"]");
		ay.add("[Contract \"?\"]");
		ay.add("[Result \"?\"]");

		ay.add(""); // and a blank line to finish
	}

	public Hand initialLeader() {
		if (contract.isValidBid() == false) {
			return hands[Dir.West.v];
		}
		return hands[((contractCompass.v + 1) % 4)];
	}

}
