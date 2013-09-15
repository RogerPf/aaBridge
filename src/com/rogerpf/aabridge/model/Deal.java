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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import version.VersionAndBuilt;

import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.model.Play_Mpat.Mpat;
import com.rogerpf.aabridge.model.Zzz.BoardData;

/**
 * Deal
 */
public class Deal implements Serializable {
	// ---------------------------------- CLASS -------------------------------------

	private static final long serialVersionUID = -9001401454121134510L;

	transient public String lastSavedAsPathWithSep = "";
	transient public String lastSavedAsFilename = "";
	transient public NsSummary nsSummary = new NsSummary();
	transient public int testId = 0;
	transient public int cycle = 0; // debug message use only
	transient public boolean hideFinish = false;
	transient public String linRowText = "";
	transient public String linResult = "";
	transient public boolean youSeatInLoadedLin = false;

	public String description = "Your text here";

//	public final Bid NULL_BID = new Bid(Zzz.NULL_BID);
//	public final Bid PASS = new Bid(Zzz.PASS);
//	public final Bid DOUBLE = new Bid(Zzz.DOUBLE);
//	public final Bid REDOUBLE = new Bid(Zzz.REDOUBLE);

	public int boardNo = 0;
	public int dealer = 0; /* compass nesw */
	public boolean vunerability[] = { false, false }; // ns, ew
	public int contractCompass = 0;
	public boolean endedWithClaim = false;
	public int tricksClaimed = 0;

	public Bid contract = new Bid(Zzz.NULL_BID);
	public Bid contractDblRe = new Bid(Zzz.NULL_BID);

	public final Hand[] hands = new Hand[4];
	public Hal prevTrickWinner = new Hal();

	public Cal packPristine = new Cal(52);

	public Hand[][] rota = new Hand[4][4];

	public int buildNumber = VersionAndBuilt.buildNo;
	public int youSeatHint = Zzz.South; // not part of the model but provided as a aid to the users of the model

	/** 
	 */
	public void restoreDealTransients() {
		// ==============================================================================================

		nsSummary = null; // never used by

		for (Card card : packPristine) {
			card.suitCh = (char) Zzz.suit_to_cdhsnCh[card.suit];
		}

		for (Hand hand : hands) {
			hand.compassCh = Zzz.compass_to_nesw_ch[hand.compass];

			for (Frag frag : hand.frags) {
				frag.suitCh = Zzz.suit_to_cdhsnCh[frag.suit];
				frag.signalHasHappened = false; // needs to be recalculated for each card in each trick
			}
			for (Frag fOrg : hand.fOrgs) {
				fOrg.suitCh = Zzz.suit_to_cdhsnCh[fOrg.suit];
				fOrg.signalHasHappened = false;
			}
			for (Bid bid : hand.bids) {
				if (bid.isCall() == false) {
					bid.suitCh = (char) Zzz.suit_to_cdhsnCh[bid.suit];
				}
			}
		}
	}

	/** 
	 * Must be called straight after a deal has been read in by read object
	 */
	public void PostReadObjectFixups() {
		// ==============================================================================================
		restoreDealTransients();
		if (contract.isNullBid()) {
			contractCompass = 0; // the old 'dead' default was -1, 0 is now considered safer
		}

		// 2013 July 16 RPf - buildNumber 1293 is the first to save the buildNumber field
		if (buildNumber == 0) {
			youSeatHint = Zzz.South;
		}

		// 2013 August 13 RPf - buildNumber 1398 is the first to save endedWithClaim field
		if (buildNumber < 1398) {
			endedWithClaim = false;
			tricksClaimed = 0;
		}
		else {
			@SuppressWarnings("unused")
			int x = 0; // put your breakpoint here
		}

		buildNumber = VersionAndBuilt.buildNo;
	}

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
		int suit = 0;
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

		public int longestSuit() {
			return fSum[0].suit;
		}

		public void NsSummarize(Deal d) {

			deal = d;
			nsCombPoints = d.north().countPoints() + d.south().countPoints();

			for (int i : Zzz.cdhs) {
				fSum[i].suit = i;
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

		int bestTrumpSuit() {
			if (fSum[0].both() > fSum[1].both())
				return fSum[0].suit;
			// the top two are of equal (combined) length so we use a replicateable tie breaker
			// of the parity of the sum of the cards of that suit in the north hand
			// this is the only use of the 'deal' field
			int tot = 0;
			for (Card cards : deal.north().frags[fSum[0].suit]) {
				tot += cards.rank;
			}
			return fSum[tot % 2].suit;
		}
	}

	/**
	 */
	public boolean isDoneHand() { // the Done hand has broken (empty!) fOrgs
		int tot = 0;
		for (int suit : Zzz.cdhs) {
			tot += hands[Zzz.West].fOrgs[suit].size();
		}
		return tot == 0;
	}

	static final public String makeDoneHand = "makeDoneHand";

	/**
	 */
	public Deal(String dealType, int youSeatHint) { /* Constructor */
		// ==============================================================================================

		this.youSeatHint = youSeatHint;

		// create the pack with the cards
		for (int suit : Zzz.cdhs) {
			for (int rank : Zzz.allThriteenCards) {
				packPristine.add(new Card(rank, suit));
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
			for (int col : Zzz.rota[row]) {
				rota[row][i++] = hands[col];
			}
		}

		buildNumber = VersionAndBuilt.buildNo;

		contractCompass = 0;
		contract = new Bid(Zzz.NULL_BID);
		contractDblRe = new Bid(Zzz.NULL_BID);

		setNextDealerAndVunerability(15);

		if (dealType != makeDoneHand)
			return;

		// From here on we make the - DONE HAND

		Cal pack = Cal.class.cast(packPristine.clone());
		// West
		for (int i = 51; i > 38; i--) {
			west().frags[Zzz.Spades].addDeltCard(pack.removeCard(i));
		}
		// East
		for (int i = 38; i > 33; i--) {
			east().frags[Zzz.Hearts].addDeltCard(pack.removeCard(i));
		}
		for (int i = 25; i > 21; i--) {
			east().frags[Zzz.Diamonds].addDeltCard(pack.removeCard(i));
		}
		for (int i = 12; i > 8; i--) {
			east().frags[Zzz.Clubs].addDeltCard(pack.removeCard(i));
		}

		// North South
		while (pack.size() > 0) {
			north().addDeltCard(pack.removeCard((int) (Math.random() * pack.size())));
			south().addDeltCard(pack.removeCard((int) (Math.random() * pack.size())));
		}

		// we are set as board 16 - so West is the dealer

		makeBid(new Bid(Zzz.PASS));
		makeBid(new Bid(Zzz.PASS));
		makeBid(new Bid(Zzz.PASS));
		makeBid(new Bid(7, Zzz.Notrumps));
		makeBid(new Bid(Zzz.PASS));
		makeBid(new Bid(Zzz.PASS));
		makeBid(new Bid(Zzz.PASS));

		// now, which is the whole point, we can play out the hand to the end by ONLY RANDOM cards
		// the dumb auto player can remain unused and so easier to test using the 'tests'.

		endedWithClaim = true;
		tricksClaimed = 0;
//		for (int i = 0; i < 13; i++) {
//			for (Hand hand : rota[Zzz.West]) {
//				hand.playCard(hand.getAnyCard());
//			}
//		}
	}

	// formatter:off
	public Hand north() {
		return hands[Zzz.North];
	}

	public Hand east() {
		return hands[Zzz.East];
	}

	public Hand south() {
		return hands[Zzz.South];
	}

	public Hand west() {
		return hands[Zzz.West];
	}

	public int contractAxis() {
		return contractCompass % 2;
	}

	public int defenderAxis() {
		return (contractCompass + 1) % 2;
	}

	/** 
	 */
	public boolean isNewBetter_pointsAndAces(Deal dNew, boolean swapped, int min, int max, int aces) {
		// ==============================================================================================
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

		// fix the old now broken compass points
		for (int i : Zzz.nesw) {
			Hand hand = hands[i];
			hand.compass = i;
		}

		// re-create the ease of access rota
		for (int row : Zzz.nesw) {
			int i = 0;
			for (int col : Zzz.rota[row]) {
				rota[row][i++] = hands[col];
			}
		}

		youSeatHint = (youSeatHint + amountOfRotaion + 4) % 4;

		rotateDealerAndVunerability(amountOfRotaion);

		if (contract.isNullBid() == false) {
			contractCompass = (contractCompass + amountOfRotaion + 4) % 4;
			// just a quick cross check - about which we do nothing if it fails !
			int recalcCompass = getHandThatMadePartnershipFirstCallOfSuit(contract).compass;
			assert (contractCompass == recalcCompass);
		}

		restoreDealTransients();
	}

	/**
	 */
	public Deal deepClone() {
		// ==============================================================================================
		Deal d = new Deal("", 0);

		d.testId = testId; // a transient

		d.boardNo = boardNo;
		d.dealer = dealer; /* compass nesw */
		d.vunerability[Zzz.NS] = vunerability[Zzz.NS];
		d.vunerability[Zzz.EW] = vunerability[Zzz.EW];
		d.contractCompass = contractCompass;

		d.youSeatInLoadedLin = youSeatInLoadedLin;
		d.youSeatHint = youSeatHint; // but who cares
		d.description = description;
		// d.buildNumber in construct time field def

		d.lastSavedAsPathWithSep = lastSavedAsPathWithSep;
		d.lastSavedAsFilename = lastSavedAsFilename;
		d.endedWithClaim = endedWithClaim;
		d.tricksClaimed = tricksClaimed;

		// contract
		if (contract.isCall()) {
			d.contract = new Bid(contract.level);
		}
		else {
			d.contract = new Bid(contract.level, contract.suit);
		}

		// contractDblRe
		{
			d.contractDblRe = new Bid(contractDblRe.level);
		}

		Cal pack = Cal.class.cast(d.packPristine.clone());

		for (int i : Zzz.nesw) {
			Hand o_hand = hands[i];
			Hand d_hand = d.hands[i];

			d_hand.playerName = o_hand.playerName;
			// compass, is preset

			// fOrgs
			for (int k : Zzz.cdhs) {
				Frag o_fOrg = o_hand.fOrgs[k];
				Frag d_fOrg = d_hand.fOrgs[k];
				// hand, suit, suitCh all preset
				for (Card o_card : o_fOrg) {
					Card d_card = pack.getIfRankAndSuitExists(o_card.rank, o_card.suit);
					d_fOrg.addDeltCard(d_card);
					// no remove here
				}
			}

			// frags
			for (int k : Zzz.cdhs) {
				Frag o_frag = o_hand.frags[k];
				Frag d_frag = d_hand.frags[k];
				// hand, suit, suitCh all preset
				for (Card o_card : o_frag) {
					Card d_card = pack.getIfRankAndSuitExists(o_card.rank, o_card.suit);
					d_frag.addDeltCard(d_card);
					pack.remove(d_card);
				}
			}

			// played
			for (Card o_card : o_hand.played) {
				Card d_card = pack.getIfRankAndSuitExists(o_card.rank, o_card.suit);
				d_hand.played.add(d_card);
				pack.remove(d_card);
			}

			// bids
			for (Bid bid : o_hand.bids) {
				Bid d_bid = null;
				if (bid.isCall()) {
					d_bid = new Bid(bid.level);
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

		assert (pack.isEmpty());

		// prevTrickWinner
		for (Hand o_winner : prevTrickWinner) {
			d.prevTrickWinner.add(d.hands[o_winner.compass]);
		}

		// restoreDealTransients();

		return d;
	}

	/**
	 */
	public void setDealer(int dealerCompass) {
		// ==============================================================================================
		dealer = dealerCompass;
		assert (prevTrickWinner.size() <= 1);
		prevTrickWinner.add(hands[dealer]);
	}

	/**
	 */
	public static Deal nextBoard(int prevBoardNo, boolean presetTheContract, String criteria, int youSeatForNewDeal) {
		// ==============================================================================================
		Deal bestSoFar = new Deal("", youSeatForNewDeal);
		bestSoFar.setNextDealerAndVunerability(prevBoardNo);
		bestSoFar.redeal();
		Deal d = new Deal("", youSeatForNewDeal);
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
			SuitShape sh[] = new SuitShape[2];
			int min;
			int max;
			{
				min = 28;
				max = 30;
				sh[0] = new SuitShape(5, 5);
				sh[1] = new SuitShape(5, 4);
			}
			if (criteria.endsWith("_E2")) {
				min = 28;
				max = 30;
				sh[0] = new SuitShape(5, 4);
				sh[1] = new SuitShape(5, 4);
			}

			if (criteria.endsWith("_M1")) {
				min = 29;
				max = 31;
				sh[0] = new SuitShape(5, 4);
				sh[1] = new SuitShape(5, 3);
			}
			if (criteria.endsWith("_M2")) {
				min = 29;
				max = 31;
				sh[0] = new SuitShape(5, 3);
				sh[1] = new SuitShape(5, 3);
			}
			if (criteria.endsWith("_I1")) {
				min = 30;
				max = 32;
				sh[0] = new SuitShape(5, 4);
				sh[1] = new SuitShape(4, 4);
			}
			if (criteria.endsWith("_I2")) {
				min = 30;
				max = 32;
				sh[0] = new SuitShape(5, 3);
				sh[1] = new SuitShape(4, 4);
			}

			if (criteria.endsWith("_H1")) {
				min = 31;
				max = 33;
				sh[0] = new SuitShape(4, 4);
				sh[1] = new SuitShape(4, 4);
			}
			if (criteria.endsWith("_H2")) {
				min = 31;
				max = 33;
				sh[0] = new SuitShape(4, 4);
				sh[1] = new SuitShape(4, 3);
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

					if (bestSoFar.nsSummary.Divergence(sh) == 0) {
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
			}
			else {
				assert (false);
			}

			boolean swapped = false;
			for (int i = 0; i < 10000; i++) {
				d.redeal();
				if (bestSoFar.isNewBetter_pointsAndAces(d, swapped, min, max, aces)) {
					Deal tmp = bestSoFar;
					bestSoFar = d;
					d = tmp;
					if (!swapped) {
						swapped = true;
						// continue; we don't need this
					}

					int points = bestSoFar.nsSummary.nsCombPoints;

					if ((min <= points) && (points <= max)) {
						break;
					}
				}
			}
			// System.out.println(c + " " + bestSoFar.nsSummary.nsCombPoints);
		}

		if (presetTheContract && (((youSeatForNewDeal % 2) == Zzz.EW) || (southBiddingRequired(criteria) == false))) {

			// add passes until we get to South
			for (Hand hand : bestSoFar.rota[bestSoFar.dealer]) {
				if (hand.compass == Zzz.South) {
					break;
				}
				bestSoFar.makeBid(new Bid(Zzz.PASS));
			}

			bestSoFar.makeBid(bestSoFar.generateSouthBid(criteria));

			Bid bid = null;
			while (bestSoFar.isBidding()) {
				for (Hand hand : bestSoFar.rota[Zzz.West]) {

					if (hand.compass == Zzz.East) {
						bid = bestSoFar.generateEastWestBid(hand);
					}
					else if (hand.compass == Zzz.West) {
						bid = bestSoFar.generateEastWestBid(hand);
					}
					else if (hand.compass == Zzz.North) {
						bid = new Bid(Zzz.PASS);
					}
					else if (hand.compass == Zzz.South) {
						bid = bestSoFar.generateSouthBid(criteria);
					}
					bestSoFar.makeBid(bid);

					if (bestSoFar.isBidding() == false)
						break;
				}
			}
		}

		// System.out.println("NextBoard " + bestSoFar.boardNo);

		return bestSoFar;
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

		return "twoSuitSlam_M1";
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
			bid = new Bid(Zzz.PASS);
			Hand bidder = getHandThatMadeBid(high);
			if (bidder.axis() == hands[Zzz.South].axis()) {
				Bid dr = this.getLastDblOrRedblAfter(high);
				if (dr.isDouble() && vunerability[0]) {
					bid = new Bid(Zzz.REDOUBLE);
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
			bid = new Bid(6, Zzz.Notrumps);
		}

		else if (criteria.startsWith("ntGrand")) {
			bid = new Bid(7, Zzz.Notrumps);
		}

		assert (bid != null);
		return bid;
	}

	/**
	 *  
	 */
	public Bid generateEastWestBid(Hand hand) {
		// ==============================================================================================
		Bid bid = new Bid(Zzz.PASS);

		Bid high = getHighestBid();
		if (high.isPass() == false) {
			Hand bidder = getHandThatMadeBid(high);
			if (bidder.axis() == hands[Zzz.South].axis()) {
				Bid dr = this.getLastDblOrRedblAfter(high);
				if (dr.isNullBid()) {
					// so now I can check to see if we are playing aginst & N
					if (high.suit == Zzz.Notrumps && high.level == 7) {
						if (hand.doesHandHaveAKingOrAce()) {
							bid = new Bid(Zzz.DOUBLE);
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
			if ((frag.isEmpty() == false) && (frag.get(0).rank == Zzz.Ace))
				count++;
		}
		for (Frag frag : south().frags) {
			if ((frag.isEmpty() == false) && (frag.get(0).rank == Zzz.Ace))
				count++;
		}
		return (count >= wanted);
	}

	/**
	 */
	public void setNextDealerAndVunerability(int oldBoardNo) {
		// ==============================================================================================
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
	public void rotateDealerAndVunerability(int amountOfRotaion) {
		// ==============================================================================================
		dealer = (dealer + amountOfRotaion + 4) % 4;

		if ((amountOfRotaion + 4) % 2 == 1) {
			boolean kept = vunerability[0];
			vunerability[0] = vunerability[1];
			vunerability[1] = kept;
		}

		// find the boardnumber that matches
		boardNo = 0; // so misses will be harmless - but there should not be any
		for (int i = 1; i <= 16; i++) {
			BoardData b = Zzz.getBoardData(i);
			if (b.dealer == dealer && vunerability[0] == b.vunerability[0] && vunerability[1] == b.vunerability[1]) {
				boardNo = i;
				break;
			}
		}
	}

	/**
	 */

	public void redeal() {
		// =============== ===============================================================================
		contractCompass = 0;
		contract = new Bid(Zzz.NULL_BID);
		contractDblRe = new Bid(Zzz.NULL_BID);

		for (Hand h : hands) {
			h.setToVirgin();
		}

		prevTrickWinner.clear();

		Cal pack = Cal.class.cast(packPristine.clone());

		Collections.shuffle(pack);

		while (!pack.isEmpty()) {
			for (Hand h : hands) {
				h.addDeltCard(pack.remove(pack.size() - 1));
			}
		}

		// make NS have the best hands
		{
			Arrays.sort(hands); // the best two hands are now in pos 0 (North) and 1 (East)

			Hand x = hands[Zzz.South];
			hands[Zzz.South] = hands[Zzz.North];
			hands[Zzz.North] = hands[Zzz.East];
			hands[Zzz.East] = x;

			for (int i : Zzz.nesw) {
				hands[i].compass = i;
			}

			// for EASE Of ACCESS - Create a Hands rota
			// like the Zzz.rota for the indexes, but for the hands
			for (int row : Zzz.nesw) {
				int i = 0;
				for (int col : Zzz.rota[row]) {
					rota[row][i++] = hands[col];
				}
			}

			nsSummary.NsSummarize(this);
		}

	}

	/**
	 * adjust is a tweek when 'you' is defender and therefor does not set the contract for a partscore
	 * this sets it for him
	 */
	public Bid pickGoodNorthSouthGameContract(int adjust) {
		// ==============================================================================================
		int level;
		int suit;

		nsSummary = new NsSummary(this); // incase it is out of date e.g. by hand edit or load

		int longestCombLen = nsSummary.fSum[0].both();
		int shortestCombLen = nsSummary.fSum[3].both();

		if ((longestCombLen == 7) || (longestCombLen == 8) && (shortestCombLen >= 6)) {
			suit = Zzz.Notrumps;
			level = 3 + adjust;
		}
		else {
			suit = nsSummary.bestTrumpSuit();
			level = (suit >= Zzz.Hearts) ? 4 + adjust : 5 + (2 * adjust);
			if (level == 5 && longestCombLen < 9) { // lets not play in an 8 card minor game
				suit = Zzz.Notrumps;
				level = 3 + adjust;
			}
		}

		return new Bid(level, suit);
	}

	/**
	 */
	public Bid pickGoodNorthSouthSlamContract() {
		// ==============================================================================================

		nsSummary = new NsSummary(this); // incase it is out of date e.g. by hand edit or load

		int suit = nsSummary.bestTrumpSuit();
		int level = 6;

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

		Point score = getContractTrickCountSoFar();

		String s;
		s = Zzz.compass_to_nesw_st[contractCompass] + " ";
		s += contract.getLevelCh() + "" + contract.getSuitCh() + doubleOrRedoubleStringX();

		if (isFinished() && hideResults == false) {

			int trickDiff = score.x - (6 + contract.level);

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

		Point score = getContractTrickCountSoFar();

		String s;
		s = contract.getLevelCh() + "" + contract.getSuitCh() + doubleOrRedoubleStringX();
		s += "-by-" + Zzz.compass_to_nesw_st_long[contractCompass] + "__";
		s += Zzz.compass_to_ns_ew_st[contractCompass] + "-" + Integer.toString(score.x) + "_";
		s += Zzz.compass_to_ns_ew_st[(contractCompass + 1) % 4] + "-" + Integer.toString(score.y);

		if (isFinished()) {
			s += "__";

			int trickDiff = score.x - (6 + contract.level);

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
			for (int i : Zzz.zto3) {
				hands[i].playerName = as.get((i + 2) % 4);
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
	public int getTheYouSeat() {
		if (isDeclarerValid() && (((youSeatHint + 2) % 4) == contractCompass)) {
			return contractCompass; // YouSeat can't be the dummy, it has to be the declarer
		}
		return youSeatHint;
	};

	/**   
	 */
	public boolean isYouSeatDeclarerAxis() {
		if (isDeclarerValid()) {
			return getTheYouSeat() % 2 == contractCompass % 2;
		}
		return youSeatHint % 2 != Zzz.NS; // Assume that this means they wont (can't) bid
	};

	/**   
	 */
	public boolean isYouSeatDefenderAxis() {
		if (isDeclarerValid()) {
			return getTheYouSeat() % 2 != contractCompass % 2;
		}
		return youSeatHint % 2 == Zzz.NS; // Assume that this means they wont (can't) bid
	};

	/**   
	 */
	public int getDeclarerOrTheDefaultYouSeat() {
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
	public boolean isTrumps(int suit) {
		return contract.suit == suit;
	}

	/**
	 */
	public boolean isLevelAllowed(int level) {
		Bid bid = getHighestBid();
		if (bid.getLevel() < level)
			return true;
		if ((bid.getLevel() == level) && (bid.suit < Zzz.Notrumps))
			return true;
		return false;
	}

	/**
	 */
	public int getHighestLevelAllowed() {
		Bid bid = getHighestBid();
		if (bid.suit == Zzz.Notrumps)
			return bid.getLevel() + 1;
		return bid.getLevel();
	}

	/**
	 */
	public boolean isCallAllowed(int bv) {

		if (bv == Zzz.PASS)
			return true; // always OK

		assert (bv == Zzz.DOUBLE || bv == Zzz.REDOUBLE);
		Bid highest = getHighestBid();
		if (highest.isCall())
			return false; // invalid

		int bAxis = (getNextHandToBid().compass) % 2;
		Bid xorxx = getLastDblOrRedblAfter(highest);

		if (xorxx.isReDouble())
			return false; // nowhere to go after a redouble

		if (xorxx.isNullBid()) { // i.e there was NO *previous* Double or Redouble
			if (bv == Zzz.REDOUBLE)
				return false; // invalid
			int highAxis = (getHandThatMadeBid(highest).compass) % 2;
			if (bAxis == highAxis)
				return false; // doubles must be by the other axis
			return true;
		}

		if (xorxx.isDouble()) {
			if (bv == Zzz.DOUBLE)
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
		// ==============================================================================================
		if (b.isCall() == false) {
			Bid prev = getHighestBid();
			if (b.isLowerThanOrEqual(prev)) {
				// System.out.println(b + " is invalid - to low");
				return; // they should not have sent this really
			}
		}
		else {
			if (!isCallAllowed(b.level))
				return;
		}

		getNextHandToBid().bids.add(b);

		if (isAuctionFinished()) {

			// assert (contract.isNullBid()); // this fails after a bids have been edited
			// assert (prevTrickWinner.isEmpty()); // this fails after a bids have been edited
			// FFS - when we are at the end of **editing** the bidding
			// the above tests fail (and have therefor been turned off)
			contract = new Bid(Zzz.NULL_BID);
			prevTrickWinner.clear();

			for (Hand hand : rota[Zzz.North]) {
				assert (hand.played.isEmpty());
			}

			contract = getHighestBid();
			contractCompass = getHandThatMadePartnershipFirstCallOfSuit(contract).compass;
			contractDblRe = getLastDblOrRedblAfter(contract);

			// make the first entry, which shows the leader to the first trick
			prevTrickWinner.add(hands[(contractCompass + 1) % 4]);
		}
	}

	/**
	 */
	public void wipePlay() {
		// set the frags to match the original cards and wipe the played
		clearAllStrategies();

		for (Hand hand : rota[Zzz.North]) {
			for (int i : Zzz.cdhs) {
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
	public void wipePlay(boolean keepFirstCardPlayed) {
		// set the frags to match the original cards and wipe the played
		clearAllStrategies();
		App.deal.endedWithClaim = false;

		Card first = null;
		if (keepFirstCardPlayed && !prevTrickWinner.isEmpty()) {
			if (!prevTrickWinner.get(0).played.isEmpty())
				first = prevTrickWinner.get(0).played.get(0);
		}

		for (Hand hand : rota[Zzz.North]) {
			for (int i : Zzz.cdhs) {
				hand.frags[i] = (Frag) hand.fOrgs[i].clone();
			}
			hand.played.clear();
		}

		// leave only the original leader
		while (prevTrickWinner.size() > 1) {
			prevTrickWinner.removeLast();
		}

		if (first != null)
			prevTrickWinner.get(0).playCard(first);
	}

	/**
	 */
	public void wipeContractAndPlay() {
		// set the frags to match the original cards and wipe the played
		App.deal.endedWithClaim = false;

		for (Hand hand : rota[Zzz.North]) {
			for (int i : Zzz.cdhs) {
				hand.frags[i] = (Frag) hand.fOrgs[i].clone();
			}
			hand.played.clear();
		}
		prevTrickWinner.clear();
		contract = new Bid(Zzz.NULL_BID);
		contractDblRe = new Bid(Zzz.NULL_BID);
		contractCompass = 0;
		clearAllStrategies();
	}

	/**
	 */
	public void wipeContractBiddingAndPlay() {
		// set the frags to match the original cards and wipe the played
		App.deal.endedWithClaim = false;

		for (Hand hand : rota[Zzz.North]) {
			for (int i : Zzz.cdhs) {
				hand.frags[i] = (Frag) hand.fOrgs[i].clone();
			}
			hand.bids.clear();
			hand.played.clear();
		}
		prevTrickWinner.clear();
		contract = new Bid(Zzz.NULL_BID);
		contractDblRe = new Bid(Zzz.NULL_BID);
		contractCompass = 0;
		clearAllStrategies();
		// the dealer has been preset by the user, that is why this routine exists
	}

	/**
	 */
	public void finishBiddingIfIncomplete() {

		while (isAuctionFinished() == false) {
			makeBid(new Bid(Zzz.PASS));
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
	public Hand cardPlayForLinSavesxxxx() {
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

	public String cardPlayForLinSave() {
		String unixEOL = "" + (char) 0x0a;
		String s = "";

		int countPlayed = countCardsPlayed();
		if (endedWithClaim == false && countPlayed == 0) {
			return s;
		}

		int tk = 0;
		for (int i = 0; i < countPlayed; i++) {
			Card card = getCardThatWasPlayed(i);
			s += "pc|" + card.getSuitCh() + "" + card.getRankCh() + "|";
			tk = (tk + 1) % 4;
			if (tk == 0) {
				s += "pg||" + unixEOL;
			}
		}
		if (tk != 0) // we ended part way through a trick
			s += "pg||" + unixEOL;

		if (endedWithClaim) {
			s += "mc|" + tricksClaimed + "|" + unixEOL;
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
		int histLeaderCompass = prevTrickWinner.get(trick).compass;
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
		int histLeaderCompass = prevTrickWinner.get(trick).compass;
		return (histLeaderCompass + turn) % 4;
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

	private Hand getHandThatMadePartnershipFirstCallOfSuit(Bid bid) {

		Hand bidHand = getHandThatMadeBid(bid);

		int rounds = hands[dealer].bids.size();
		for (int r = 0; r < rounds; r++) {
			for (Hand hand : rota[dealer]) {
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
		int rounds = hands[dealer].bids.size();
		for (int r = 0; r < rounds; r++) {
			for (Hand hand : rota[dealer]) {
				if (hand.bids.size() == r)
					break;
				Bid b = hand.bids.get(r);
				s += "mb|" + b.toStringForLin();
				if (b.alert)
					s += "!";
				s += "|";
				if (b.alert && b.alertText.length() > 0)
					s += "an|" + b.alertText + "|";
			}
		}
		s += "pg||";
		return s;
	}

	/**
	 */
	private Bid getLastDblOrRedblAfter(Bid bid) {

		Bid last = new Bid(Zzz.NULL_BID);
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
		int rounds = hands[dealer].bids.size();
		int interesting = 0;
		for (int r = 0; r < rounds; r++) {
			for (Hand hand : rota[dealer]) {
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
		Bid pb = new Bid(Zzz.PASS);
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
				if ((skip++ != 0) && (cb.isPass()))
					count++;
				else
					count = 0;
				if (count == 3)
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
//		int s[] = { 0, 0, 0, 0 };
//		for (int compass : Zzz.nesw) {
//			s[compass] = hands[compass].played.size();
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
//		for (int seat : Zzz.rota[leader.compass]) {
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
		Point trickCount = new Point(); // comes set to 0,0

		if (endedWithClaim) {
			trickCount.x = tricksClaimed;
			trickCount.y = 13 - trickCount.x;
		}
		else {
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
		}
		return trickCount;
	}

	/**
	 * returns a point - x is the decelarers tricks and y is defenders
	 */
	public Point getContractTrickCountToTrick(int reviewTrick) {
		Point trickCount = new Point(0, 0);
		boolean skipFirst = true;
		int plA = contractCompass;
		int plB = (contractCompass + 2) % 4;
		int opA = (contractCompass + 1) % 4;
		int opB = (contractCompass + 3) % 4;
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
	public Hand getHand(int compass) {
		return hands[compass];
	}

	/**
	 * Declarer score ...
	 */
	public Point getBoardScore() {
		Point score = new Point(0, 0);

		if (contract.isPass() || !isFinished())
			return score;

		int reMult = (contractDblRe.isReDouble()) ? 2 : 1;
		int dblMult = (contractDblRe.isDouble()) ? 2 : 1;

		int trickDiff = getContractTrickCountSoFar().x - (contract.level + 6);

		if (trickDiff >= 0) { // they made
			score.y = (contract.level * Zzz.scoreRate[contract.suit] + ((contract.suit == Zzz.Notrumps) ? 10 : 0)) * dblMult;

			int vunMult = (vunerability[contractAxis()]) ? 2 : 1;

			if (contract.level == 7) {
				score.x = 750 * vunMult;
			}
			else if (contract.level == 6) {
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

			if (contractDblRe.isNullBid()) {
				score.x += trickDiff * Zzz.scoreRate[contract.suit];
			}
			else {
				score.x += (50 + (50 * trickDiff * vunMult)) * reMult;
			}
		}
		else { // they went down trickDiff is NEGATIVE

			// score.y is always nil

			if (contractDblRe.isNullBid()) {
				score.x = 50 * trickDiff;
			}
			else if (vunerability[contractAxis()] == false) {
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

		// We return the score as seen by North South (as that is how they are currently displayed)

		if (contractAxis() == Zzz.EW) {
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
	int countPlayedOfSuit(int suit) {
		int count = 0;

		int trick = -1;
		for (Hand leader : prevTrickWinner) {
			trick++;
			for (Hand hand : rota[leader.compass]) {
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
	boolean isActiveCard(Frag fragSkip, int rank, int suit) {

		for (Hand hand : hands) {
			if (hand.frags[suit] == fragSkip)
				continue;
			if (hand.frags[suit].getIfRankExists(rank) != null) {
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
	boolean isActiveCardExcludeTrick(Frag fragSkip, int rank, int suit) {

		for (Hand hand : hands) {
			if (hand.frags[suit] == fragSkip)
				continue;
			if (hand.frags[suit].getIfRankExists(rank) != null) {
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
			c.rankRel = 0;
			c.rankEqu = 0;
		}

		// temporarily return all played cards (in this trick) max 3 to their hand
		if (trick_anal_time) {
			for (Hand hand : hands) {
				if (hand.played.size() == prevTrickWinner.size()) {
					Card card = hand.played.getLast();
					hand.frags[card.suit].addDeltCard(card);
				}
			}
		}

		// calc all the 'rankRel' values for all active cards
		for (Hand hand : hands) {
			for (Frag frag : hand.frags) {
				int inactive = 0;
				int topFace = Zzz.Ace + 1;
				for (Card c : frag) {
					for (int i = c.rank + 1; i < topFace; i++) {
						if (isActiveCardExcludeTrick(frag, i, c.suit) == false) {
							inactive++;
						}
					}
					topFace = c.rank; // next search starts from the prev card
					c.rankRel = c.rank + inactive;
				}
			}
		}

		// calc all the 'rankEqu' values
		{
			// calc all the 'rankEqu' values - Stage One
			Frag axisBoth[][] = new Frag[2][4];
			for (int i : Zzz.cdhs) {
				axisBoth[0][i] = (Frag) (hands[0].frags[i].clone());
				axisBoth[1][i] = (Frag) (hands[1].frags[i].clone());
			}
			hands[0].addPartnersCurrentCards(axisBoth[0]);
			hands[1].addPartnersCurrentCards(axisBoth[1]);
			for (int ax : Zzz.axies) {
				for (Frag fragBoth : axisBoth[ax]) {
					int prevRankRel = Zzz.Ace + 2;
					int curRankEqu = Zzz.Ace + 2;
					for (Card c : fragBoth) {
						int rankRel = c.rankRel;
						if (rankRel != prevRankRel - 1) {
							curRankEqu = rankRel;
						}
						c.rankEqu = curRankEqu;
						prevRankRel = rankRel;
					}
				}
			}

			// calc all the 'rankEqu' values - Stage Two
			for (int ax : Zzz.axies) {
				for (Frag fragBoth : axisBoth[ax]) {
					if (fragBoth.size() > 0) {
						int cur_equ = fragBoth.get(0).rankEqu;
						int up_equ = (cur_equ == Zzz.Ace) ? Zzz.Ace : Zzz.King;
						for (Card c : fragBoth) {
							if (c.rankEqu != cur_equ) {
								cur_equ = c.rankEqu;
								up_equ -= 2;
							}
							c.rankEqu = up_equ;
						}
					}
				}
			}
		}

		// generate all the mpat and epat "Card Patern matchers"
		if (trick_anal_time) {

			for (int i : Zzz.cdhs) {
				mpatAyEqu[i] = new Mpat(Play_Mpat.Equ, this, i);
				mpatAyRel[i] = new Mpat(Play_Mpat.Rel, this, i);
			}
		}

		// undo the earlier card replacement - i.e. remove this tricks played cards from the hand
		//
		// this now happens in _part2
		// if (trick_anal_time) {
		// for (Hand hand : hands) {
		// if (hand.played.size() == prevTrickWinner.size()) {
		// Card card = hand.played.getLast();
		// hand.frags[card.suit].remove(card);
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
					hand.frags[card.suit].remove(card);
				}
			}
		}
	}

	/** 
	 */
	public void moveCardToHandDragTime(Card cardIn, Hand handTo) {
		int rank = cardIn.rank;
		int suit = cardIn.suit;
		Card card = null;
		Hand handFrom = null;

		clearAllStrategies();

		for (Hand hand : rota[Zzz.North]) {
			// We scan the original cards, of course
			card = hand.fOrgs[suit].getIfRankExists(rank);
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

		handFrom.fOrgs[suit].remove(card);
		handTo.fOrgs[suit].addDeltCard(card);

		handTo.fOrgs[unwanted.suit].remove(unwanted);
		handFrom.fOrgs[unwanted.suit].addDeltCard(unwanted);

		// set the frags to match the original cards and wipe the played
		for (Hand hand : rota[Zzz.North]) {
			for (int i : Zzz.cdhs) {
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
		int rank = cardIn.rank;
		int suit = cardIn.suit;
		Card card = null;
		Hand handFrom = null;

		for (Hand hand : rota[Zzz.North]) {
			// We scan the original cards, of course
			card = hand.fOrgs[suit].getIfRankExists(rank);
			if (card != null) {
				if (handTo == hand) {
					return; // Already in the correct hand
				}
				handFrom = hand;
				break;
			}
		}

		assert (altCard != null);

		handFrom.frags[suit].remove(card);
		handTo.frags[suit].addDeltCard(card);

		handTo.frags[altCard.suit].remove(altCard);
		handFrom.frags[altCard.suit].addDeltCard(altCard);

		handFrom.fOrgs[suit].remove(card);
		handTo.fOrgs[suit].addDeltCard(card);

		handTo.fOrgs[altCard.suit].remove(altCard);
		handFrom.fOrgs[altCard.suit].addDeltCard(altCard);
	}

	public void clearAllStrategies() {
		for (Hand hand : rota[Zzz.North]) {
			hand.clearStrategy();
		}
	}

	public void playCardExternal(int suit, int rank) {
		Hand hand = getNextHandToPlay();
		Card card = hand.frags[suit].getIfRankExists(rank);
		if (card == null) {
			@SuppressWarnings("unused")
			int x = 0; // put your breakpoint here
		}
		assert (card != null);
		hand.playCard(card);
	}

}
