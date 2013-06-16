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

	/** 
	 */
	public Hand(Deal dealV, int compassV) { // constructor
		deal = dealV;
		compass = compassV;
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
	Hand partner() {
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
	void AddDeltCard(Card card) {
		fOrgs[card.suitValue].addDeltCard(card);
		frags[card.suitValue].addDeltCard(card);
	}

	/** 
	 */
	public void playCard(Card card) {
		assert (played.size() < deal.prevTrickWinner.size());

		frags[card.suitValue].remove(card);
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

		int ledSuitValue = prevWinner.played.getCard(curTrickIndex).suitValue;

		if (ledSuitValue == frag.suitValue)
			return true; // we are selecting the
							// led suit

		if (frags[ledSuitValue].isEmpty())
			return true; // we have none of the
							// led suit

		return false;
	}

	/** 
	 */
	public int faceSelectableCount(int faceValue) {
		int count = 0;
		for (Frag frag : frags) {
			for (Card card : frag) {
				if (card.faceValue == faceValue) {
					if (isSuitSelectable(card.suitValue)) {
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
	public Card faceSelectableGetOnlyCard(int faceValue) {
		for (Frag frag : frags) {
			for (Card card : frag) {
				if (card.faceValue == faceValue) {
					if (isSuitSelectable(card.suitValue)) {
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
	public boolean isSuitSelectable(int suitValue) {

		if (frags[suitValue].isEmpty())
			return false; // we have none of the
							// selected suit

		int curTrickIndex = deal.getCurTrickIndex();
		Hand prevWinner = deal.getCurTrickLeader();
		if (prevWinner == this)
			return true; // we are on lead

		int ledSuitValue = prevWinner.played.getCard(curTrickIndex).suitValue;

		if (ledSuitValue == suitValue)
			return true; // we are selecting the led suit

		if (frags[ledSuitValue].isEmpty())
			return true; // we have none of the led suit

		return false;
	}

	/** 
	 */
	public Card getCardIfSingletonInSuit(int suitValue) {

		if (frags[suitValue].size() != 1)
			return null; // not singleton

		return frags[suitValue].get(0);
	}

	/** 
	 */
	public Card getCardIfMatching(int suitValue, int faceValue) {

		for (Card card : frags[suitValue]) {
			if (card.faceValue == faceValue)
				return card;
		}
		return null;
	}

	/** 
	 */
	private int makeSuitSuggestionIfOnlyOneSuit() {
		int suitValue = -1;
		for (Frag frag : frags) {
			if (frag.size() == 0)
				continue;
			if (suitValue != -1)
				return -1;
			suitValue = frag.suitValue;
		}
		return suitValue;
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

		int ledSuitValue = prevWinner.played.getCard(curTrickIndex).suitValue;

		if (frags[ledSuitValue].size() > 0) {
			return ledSuitValue;
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
			int ledSuitValue = prevWinner.played.getCard(curTrickIndex).suitValue;
			if (frags[ledSuitValue].size() > 0) {
				suitIndex = ledSuitValue;
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
			for (int j = high.faceValue - 1; j > mid.faceValue; j--) {
				if (deal.isActiveCard(frag, j, frag.suitValue))
					return null;
			}
			high = mid;
		}
		return high; // which will actually NOW be the lowest :)
	}

//	/** 
//	 */
//	private boolean hasBeenPlayed(int faceValue, int suitValue) {
//		for (Hand hand : deal.hands) {
//			if (frags[suitValue].getIfFaceExists(faceValue) != null) {
//				return false;
//			}
//			if (played.size() == deal.prevTrickWinner.size()
//					&& played.getLast().matches(faceValue, suitValue)) {
//				return false;
//			}
//		}
//		return true; // not in the hands so it has been played
//	}

	/** 
	 */
	public Card getRandomPlayableCard() {

		int from = (int) (Math.random() * 4);

		for (int i = 0; i < 4; i++) {
			Frag frag = frags[(i + from) % 4];
			if (frag.size() == 0)
				continue;
			if (isSuitSelectable(frag.suitValue)) {
				return frag.get((int) (Math.random() * frag.size()));
			}
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

		if (deal.isCurTrickComplete()) {
			deal.prevTrickWinner.removeLast();
		}

		Card card = played.removeLast();
		frags[card.suitValue].addDeltCard(card);
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
				fr[card.suitValue].remove(card);
			}

			Hand trickLeader = deal.prevTrickWinner.get(reviewTrick);

			int i = -1;
			for (Hand hand : deal.rota[trickLeader.compass]) {
				i++;
				if (hand != this || i >= reviewCard)
					continue;
				Card card = hand.played.get(reviewTrick);
				fr[card.suitValue].remove(card);
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
	public Frag getLongestSuitNotTrumps() {
		int ln = -1;
		Frag fr = null;
		for (Frag f : frags) {
			if (f.suitValue == deal.contract.suitValue)
				continue;
			if (f.size() > ln) {
				ln = f.size();
				fr = frags[f.suitValue];
			}
		}

		return fr;
	}
	


//	/** 
//	 */
//	private int outstandingTrumps_not_cur_used() { // Assumes we are NOT in no trumps
//		int ts = deal.contract.suitValue;
//		return 13 - deal.countTrumpsPlayed() - (frags[ts].size() + partner().frags[ts].size());
//	}
//
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
			if (card.isBetterThan(bestCard, deal.contract.suitValue)) {
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
			if (card.isBetterThan(bestCard, deal.contract.suitValue)) {
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
		for (Frag frag : frags) {
			for (Card card : frag) {
				if (card.faceValue == Zzz.KING || card.faceValue == Zzz.ACE)
					return true;
			}
		}
		return false;
	}

	/**
	 */
	public void addPartnersCurrentCards(Frag cpys[]) {
		for (Frag f : cpys) {
			for (Card card : partner().frags[f.suitValue]) {
				f.addDeltCard(card);
			}
		}
	}

	/**
	 */
	boolean areOurTopHoldingsContigious(int suitValue) {
		Hand hand = this;
		Hand partner = hand.partner();
		Frag myFrag = hand.frags[suitValue];
		Frag pnFrag = partner.frags[suitValue];
		if (myFrag.size() == 0 || pnFrag.size() == 0)
			return false;
		int low;
		Frag highF;
		if (((highF = myFrag).get(0).faceRel) > (low = pnFrag.get(0).faceRel)) {
		}
		else {
			highF = pnFrag;
			low = myFrag.get(0).faceValue;
		}

		int prevH = highF.get(0).faceRel + 1;
		for (int i = 0; i < highF.size(); i++) {
			int high = highF.get(i).faceRel;
			if ((prevH == high + 1) && (high - 1 == low))
				return true;
			if (prevH != high + 1)
				return false;
			prevH = high;
		}
		return false;
	}

	boolean doesPartnerHaveMaster(int suitValue) {
		if (partner().frags[suitValue].size() > 0) {
			return partner().frags[suitValue].get(0).faceRel == Zzz.ACE;
		}
		return false;
	}

	/**
	 *  
	 */
	public boolean outstandingTrumpIsMaster() {
		// can assume that there is one and only one trump outstanding
		Card card = nextHand().frags[deal.contract.suitValue].getLast();
		if (card != null)
			return (card.faceRel == Zzz.ACE);

		card = prevHand().frags[deal.contract.suitValue].getLast();
		return (card.faceRel == Zzz.ACE);
	}

	/**
	 *  
	 */
	public Card pickBestDiscard(PlayGen g) {
		Card best = null;
		float score = 0;
		
		for (int i : Zzz.rota[(int)((Math.random()*4.0f)%4.0f)]){
			Frag frag = frags[i];
			Card card = frag.getLast();
			if (card == null) continue;
			float cs = scoreDiscard(g, frag, card);
			if (best == null || (cs > score)) {
				best = card;
				score = cs;
			}
		}
		return best;
	}

	private float scoreDiscard(PlayGen g, Frag frag, Card card) {

		if (card.getSuitValue() == g.trumpSuit)
			return -1.0f;
		
		return scoreByImportance(g, frag, card);
	}

	private float scoreByImportance(PlayGen g, Frag frag, Card card) {

		int countPlayed = deal.countPlayedOfSuit(card.suitValue);
		
		Frag f = frag;		
		if (isPartnerVisible()) {
			f = g.bothHands[frag.suitValue];
		}
		
		int ourContig = f.countContigious();		
		int oppsMaxHolding = (13 - countPlayed - f.size());
		int ourExcess = ourContig - oppsMaxHolding;		
		if (ourExcess < 0) return 1.0f;
		
		if (frag.size() <= g.partner.frags[card.suitValue].size()) {
			// we have the smaller holding
			return 0.5f;	
		}
		return 0.0f;
	}

	private boolean isPartnerVisible() { // yes if we are the contract holders
		return (deal.contractCompass%2 == compass%2);
	}

	/**
	 */
	public Card dumbAuto() {

		Card card = null;
		
		// card = getSelfPlayableCard(true /* we always want to play adjacent */);

		if (card == null) {
			card = dumbAutoInner();
		}

		if (card == null) {
			card = getRandomPlayableCard();
		}

		return card;
	}

	/**
	 */
	public Card dumbAutoInner() {
		
		Card card = null;
		
		PlayGen g = new PlayGen(this);

		if (g.leaderUs) {
			
			if (g.ourContract)  
				card = Play_1st_DummyDeclarerLead.act( this, g);
			else
				card = Play_1st_DefenderLead.act( this, g);

		} 
		else if (g.RHO == g.leader)  
		 
			card = Play_2nd.act( this, g);
			
		else if (g.partner == g.leader) 

			card = Play_3rd.act( this, g);
			
		else if (g.LHO == g.leader)
		
			card = Play_4th.act( this, g);
			
		else {

			assert(false); // should never happen
		}
			
		return card;
	}

}
