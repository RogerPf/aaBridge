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

import java.io.Serializable;

/**
 * 
 * Hand
 */
public class Hand implements Serializable, Comparable<Hand> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4810060743667804430L;

	final Deal deal;

	public int compass;
	public final String playerName;
	public final Frag[] fOrgs = new Frag[4];
	public final Frag[] frags = new Frag[4];
	public final Cal played = new Cal();
	public final Bal bids = new Bal();

	transient char compassCh; // for easy viewing when in debug

	/** 
	 */
	public Hand(Deal dealV, int compassV) { // constructor
		deal = dealV;
		compass = compassV;
		compassCh = Zzz.compass_to_nesw_ch[compass];
		playerName = "";
		for (int sv : Zzz.cdhs) {
			fOrgs[sv] = new Frag(this, sv);
			frags[sv] = new Frag(this, sv);
		}
	}

	/** 
	 */
	public int axis() {
		return compass % 2;
	}

	/** 
	 */
	public String getCompassSt() {
		return Zzz.compass_to_nesw_st[compass];
	}

	/** 
	 */
	public String getCompassStLong() {
		return Zzz.compass_to_nesw_st_long[compass];
	}

	/** 
	 */
	public void setToVirgin() {
		for (int s : Zzz.cdhs) {
			fOrgs[s].clear();
			frags[s].clear();
		}
		played.clear();
		bids.clear();
	}

	/** 
	 */
	public Hand nextHand() {
		return deal.nextHand(this);
	}

	/** 
	 */
	public Hand prevHand() {
		return deal.prevHand(this);
	}

	/** 
	 */
	public Hand partner() {
		return nextHand().nextHand();
	}

	/** 
	 */
	public Hand LHO() {
		return deal.nextHand(this);
	}

	/** 
	 */
	public Hand RHO() {
		return deal.prevHand(this);
	}

	/** 
	 */
	void addDeltCard(Card card) {
		fOrgs[card.suit].addDeltCard(card);
		frags[card.suit].addDeltCard(card);
	}

	/** 
	 */
	public void playCard(Card card) {
		assert (played.size() < deal.prevTrickWinner.size());

		frags[card.suit].remove(card);
		played.add(card);

		assert (played.size() == deal.prevTrickWinner.size());

		deal.cardJustPlayed();
	}

	/** 
	 */
	public boolean isCardSelectable(Frag frag) {
		if (deal.getNextHandToPlay() != this)
			return false;

		int curTrickIndex = deal.getCurTrickIndex();
		Hand prevWinner = deal.getCurTrickLeader();
		if (prevWinner == this)
			return true; // we are on lead

		int suitLed = prevWinner.played.getCard(curTrickIndex).suit;

		if (suitLed == frag.suit)
			return true; // we are selecting the
							// led suit

		if (frags[suitLed].isEmpty())
			return true; // we have none of the
							// led suit

		return false;
	}

	/** 
	 */
	public int cardSelectableCount(int rank) {
		int count = 0;
		for (Frag frag : frags) {
			for (Card card : frag) {
				if (card.rank == rank) {
					if (isSuitSelectable(card.suit)) {
						count++;
						break;
					}
				}
			}
		}
		return count;
	}

	/** 
	 */
	public Card cardSelectableGetOnlyCard(int rank) {
		for (Frag frag : frags) {
			for (Card card : frag) {
				if (card.rank == rank) {
					if (isSuitSelectable(card.suit)) {
						return card;
					}
				}
			}
		}
		assert (false);
		return null;
	}

	/** 
	 */
	public boolean isSuitSelectable(int suit) {

		if (frags[suit].isEmpty())
			return false; // we have none of the
							// selected suit

		int curTrickIndex = deal.getCurTrickIndex();
		Hand prevWinner = deal.getCurTrickLeader();
		if (prevWinner == this)
			return true; // we are on lead

		int suitLed = prevWinner.played.getCard(curTrickIndex).suit;

		if (suitLed == suit)
			return true; // we are selecting the led suit

		if (frags[suitLed].isEmpty())
			return true; // we have none of the led suit

		return false;
	}

	/** 
	 */
	public Card getCardIfSingletonInSuit(int suit) {

		if (frags[suit].size() != 1)
			return null; // not singleton

		return frags[suit].get(0);
	}

	/** 
	 */
	public Card getCardIfMatching(int suit, int rank) {

		for (Card card : frags[suit]) {
			if (card.rank == rank)
				return card;
		}
		return null;
	}

	/** 
	 */
	public Card getCardIfMatchingRankRel(int suit, int rankRel) {

		for (Card card : frags[suit]) {
			if (card.rankRel == rankRel)
				return card;
		}
		return null;
	}

	/** 
	 */
	public int getRankIfMatchingRankRel(int suit, int rankRel) {

		for (Card card : frags[suit]) {
			if (card.rankRel == rankRel)
				return card.rank;
		}
		return 0;
	}

	/** 
	 */
	private int makeSuitSuggestionIfOnlyOneSuit() {
		int suit = -1;
		for (Frag frag : frags) {
			if (frag.size() == 0)
				continue;
			if (suit != -1)
				return -1;
			suit = frag.suit;
		}
		return suit;
	}

	/** 
	 */
	public int makeSuitSuggestion() {
		if (deal.getNextHandToPlay() != this)
			return -1;

		int curTrickIndex = deal.getCurTrickIndex();
		Hand prevWinner = deal.getCurTrickLeader();
		if (prevWinner == this) {
			return makeSuitSuggestionIfOnlyOneSuit();
		}

		int suitLed = prevWinner.played.getCard(curTrickIndex).suit;

		if (frags[suitLed].size() > 0) {
			return suitLed;
		}

		return makeSuitSuggestionIfOnlyOneSuit();
	}

	/** 
	 */
	public Card getSelfPlayableCard(boolean selfPlayAdjacent) {

		int curTrickIndex = deal.getCurTrickIndex();
		Hand prevWinner = deal.getCurTrickLeader();
		int suitIndex = -1;
		if (prevWinner == this) {
			suitIndex = makeSuitSuggestionIfOnlyOneSuit();
			if (suitIndex == -1)
				return null;
		}
		else {
			int suitLed = prevWinner.played.getCard(curTrickIndex).suit;
			if (frags[suitLed].size() > 0) {
				suitIndex = suitLed;
			}
			else { // we have none of the led suit
				suitIndex = makeSuitSuggestionIfOnlyOneSuit();
			}
		}
		if (suitIndex == -1)
			return null;

		Frag frag = frags[suitIndex];

		if (frag.size() == 1) {
			return frag.get(0);
		}

		if (selfPlayAdjacent == false)
			return null;

		Card high = frag.get(0);
		for (int i = 1; i < frag.size(); i++) {
			Card mid = frag.get(i);
			for (int j = high.rank - 1; j > mid.rank; j--) {
				if (deal.isActiveCard(frag, j, frag.suit))
					return null;
			}
			high = mid;
		}
		return high; // which will actually NOW be the lowest :)
	}

//	/** 
//	 */
//	private boolean hasBeenPlayed(int rank, int suit) {
//		for (Hand hand : deal.hands) {
//			if (frags[suit].getIfRankExists(rank) != null) {
//				return false;
//			}
//			if (played.size() == deal.prevTrickWinner.size()
//					&& played.getLast().matches(rank, suit)) {
//				return false;
//			}
//		}
//		return true; // not in the hands so it has been played
//	}

	/** 
	 */
	public Card getRandomPlayableCard() {

		int from = (int) (Math.random() * 4);

		for (int i : Zzz.cdhs) {
			Frag frag = frags[(i + from) % 4];
			if (frag.size() == 0)
				continue;
			if (isSuitSelectable(frag.suit)) {
				return frag.get((int) (Math.random() * frag.size()));
			}
		}
		return null;
	}

	/** 
	 */
	public Card getAnyCard() {

		int from = (int) (Math.random() * 4);

		for (int i : Zzz.cdhs) {
			Frag frag = frags[(i + from) % 4];
			if (frag.size() == 0)
				continue;
			return frag.get(0); // the first is any
		}
		return null;
	}

	/** 
	 */
	public boolean isDummy() {
		return !deal.contract.isCall() && (compass == (deal.contractCompass + 2) % 4);
	}

	/** 
	 */
	public void undoLastBid() {
		// assert (bids.size() > 1);
		bids.removeLast();
	}

	/** 
	 */
	public void undoLastPlay() {
		assert (played.size() >= 1);

		deal.clearStrategy();

		if (deal.isCurTrickComplete()) {
			deal.prevTrickWinner.removeLast();
		}

		Card card = played.removeLast();
		frags[card.suit].addDeltCard(card);
	}

	/** 
	 */
	public int countPoints() {
		int v = 0;
		for (Frag fOrg : fOrgs) {
			v += fOrg.countPoints();
		}
		return v;
	}

	/** 
	 */
	public int countLosingTricks_x2() {
		int v = 0;
		for (Frag fOrg : fOrgs) {
			v += fOrg.countLosingTricks_x2();
		}
		return v;
	}

	/**
	 */
	public Frag[] makeFragsAsOf(int reviewTrick, int reviewCard) {
		Frag[] fr = new Frag[4];
		for (int i : Zzz.cdhs) {
			fr[i] = (Frag) fOrgs[i].clone();
		}

		if (deal.prevTrickWinner.size() > 0) {

			// remove all the cards played in all the tricks BEFORE the review trick
			for (int i = 0; i < (reviewTrick); i++) {
				if (played.size() < i)
					continue;
				Card card = played.get(i);
				fr[card.suit].remove(card);
			}

			Hand trickLeader = deal.prevTrickWinner.get(reviewTrick);

			int i = -1;
			for (Hand hand : deal.rota[trickLeader.compass]) {
				i++;
				if (hand != this || i >= reviewCard)
					continue;
				Card card = hand.played.get(reviewTrick);
				fr[card.suit].remove(card);
				break;
			}
		}

		return fr;
	}

	/** 
	 */
	public int compareTo(Hand other) {
		return other.countPoints() - countPoints();
	}

	/** 
	 */
	public boolean areWeWinning() {

		Hand leader = deal.getCurTrickLeader();
		if (leader.nextHand() == this)
			return false; // second to play so the answer must be no

		Hand bestHand = leader;
		Card bestCard = leader.played.getLast();
		for (Hand hand : deal.rota[leader.nextHand().compass]) {
			if (hand.played.size() < leader.played.size())
				break; // end of played cards in this trick
			Card card = hand.played.getLast();
			if (card.isBetterThan(bestCard, deal.contract.suit)) {
				bestHand = hand;
				bestCard = card;
			}
		}
		return bestHand.axis() == axis();
	}

	/** 
	 */
	public Hand getBestCardOfTrickPlayer() {

		Hand leader = deal.getCurTrickLeader();
		Hand bestHand = leader;
		Card bestCard = leader.played.getLast();
		if (leader.nextHand() == this)
			return leader; // second to play so the answer must be this one

		for (Hand hand : deal.rota[leader.nextHand().compass]) {
			if (hand.played.size() < leader.played.size())
				break; // end of played cards in this trick
			Card card = hand.played.getLast();
			if (card.isBetterThan(bestCard, deal.contract.suit)) {
				bestCard = card;
				bestHand = hand;
			}
		}
		return bestHand;
	}

	/**
	 */
	public Card lastSlider(Cal[] ca) {

		Card card = ca[0].getLast(); // lowest of current longest suit
		if (card == null)
			card = ca[1].getLast();
		if (card == null)
			card = ca[2].getLast();
		if (card == null)
			card = ca[3].getLast();
		return card;
	}

	/**
	 */
	public Card bestSlider(Cal[] ca) {

		Card card = ca[0].getCard(0); // highest of current longest suit
		if (card == null)
			card = ca[1].getCard(0);
		if (card == null)
			card = ca[2].getCard(0);
		if (card == null)
			card = ca[3].getCard(0);
		return card;
	}

	/**
	 */
	public boolean doesHandHaveAKingOrAce() {
		// ==============================================================================================
		for (Frag frag : frags) {
			for (Card card : frag) {
				if (card.rank == Zzz.King || card.rank == Zzz.Ace)
					return true;
			}
		}
		return false;
	}

	/**
	 */
	public void addPartnersCurrentCards(Frag cpys[]) {
		// ==============================================================================================
		for (Frag f : cpys) {
			for (Card card : partner().frags[f.suit]) {
				f.addDeltCard(card);
			}
		}
	}

	/**
	 */
	boolean areOurTopHoldingsContigious(int suit) {
		// ==============================================================================================
		Hand hand = this;
		Hand partner = hand.partner();
		Frag myFrag = hand.frags[suit];
		Frag pnFrag = partner.frags[suit];
		if (myFrag.size() == 0 || pnFrag.size() == 0)
			return false;
		int low;
		Frag highF;
		if (((highF = myFrag).get(0).rankRel) > (low = pnFrag.get(0).rankRel)) {
		}
		else {
			highF = pnFrag;
			low = myFrag.get(0).rank;
		}

		int prevH = highF.get(0).rankRel + 1;
		for (int i = 0; i < highF.size(); i++) {
			int high = highF.get(i).rankRel;
			if ((prevH == high + 1) && (high - 1 == low))
				return true;
			if (prevH != high + 1)
				return false;
			prevH = high;
		}
		return false;
	}

	boolean doesPartnerHaveMaster(int suit) {
		// ==============================================================================================
		if (partner().frags[suit].size() > 0) {
			return partner().frags[suit].get(0).rankRel == Zzz.Ace;
		}
		return false;
	}

	static int oppsMax(Gather g, int suit) {
		// ==============================================================================================
		return Math.max(g.LHO.frags[suit].size(), g.RHO.frags[suit].size());
	}

	static int oppsAceDownRunDepth(Gather g, int suit) {
		// ==============================================================================================
		int d = 0;
		int t = Zzz.Ace;
		for (Card card : g.oppsBoth[suit]) {
			if (card.rankRel != t--)
				break;
			d++;
		}
		return Math.min(d, oppsMax(g, suit));
	}

	static int ourInnerRunDepth(Gather g, int suit, int ord) {
		// ==============================================================================================
		int d = 0;
		int t = Zzz.Ace - ord;
		for (Card card : g.ourBoth[suit]) {
			if (card.rankRel != t--)
				break;
			d++;
		}
		return d;
	}

	/**
	 */
	public Card pickBestDiscard(Gather g) {
		// ==============================================================================================
		return Play_5_Discard.pickBest(g);
	}

	public void clearStrategy() {
		// ==============================================================================================
		deal.clearStrategy(this);
	}

	public void setStrategy(Strategy stra) {
		// ==============================================================================================
		deal.setStrategy(this, stra);
	}

	public Strategy getStrategy() {
		// ==============================================================================================
		return deal.getStrategy(this);
	}

	public void calcStrategy() {
		// ==============================================================================================
		// note - we are in a ****** hand ******

		if (getStrategy() == null) {
			// print a message if we are the first of the two stratergies to be created
			if (deal.testId == 0 && deal.hands[(compass + 1) % 4].getStrategy() == null) {
				System.out.println("Board no " + deal.boardNo + "     cycle " + (++(deal.cycle))
						+ "  ----------------------------------------------------------------------");
			}

			Deal d2 = deal.deepClone();
			d2.wipePlay((axis() == deal.contractAxis()) /* keepFirstCardPlayed */);

			Strategy stra = new Strategy(d2);
			setStrategy(stra);
			stra.update();
		}

		/** 
		 * We now go through each played card updating the statergy after each play
		 * This is only needed as it re-creates all the history of a Strategy lost
		 * because of an UNDO or a 'load'.  So 99% of the time this will do nothing
		 */
		Strategy stra = getStrategy();

		Deal d2 = stra.getEmdeddedDeal();
		int played_d = deal.countCardsPlayed();
		int played_d2 = d2.countCardsPlayed();
		assert (played_d >= played_d2); // which is why undo etc must clear the strategies
		for (int i = played_d2; i < played_d; i++) {
			Card card = deal.getCardThatWasPlayed(i);
			d2.playCardExternal(card.suit, card.rank);
			stra.update();
		}

	}

	/**
	 */
	public Card dumbAuto() {
		// ==============================================================================================
		Card card = null;

		calcStrategy();

		Gather g = new Gather(this, /* strategyCreated */false);

		// @formatter:off
		if (g.ourContract) {
			switch(g.positionInTrick) {
			case 0: card = Qlay_1st__Declarer.act(g); break;
			case 1: card = Qlay_2nd.act(g); break;
			case 2: card = Qlay_3rd.act(g); break;
			case 3: card = Qlay_4th.act(g); break;
			default: assert(false);
			}
		}
		else {
			switch(g.positionInTrick) {
			case 0: { if (g.trickNumb == 0) { 
					card = Rlay_0th__TheOpeningLead.act(g); break; }
			 else { card = Rlay_1st__Defender.act(g); break; } }
			case 1: card = Rlay_2nd.act(g); break;
			case 2: card = Rlay_3rd.act(g); break;
			case 3: card = Rlay_4th.act(g); break;
			default: assert(false);
			}
		}
		// @formatter:on

		if (card == null) {
			card = getRandomPlayableCard();
			System.out.println("===>   ERROR  -  dumbAuto picked    NULL   - instead of a card");
		}

		return card;
	}

}
