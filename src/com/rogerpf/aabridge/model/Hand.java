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

import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.model.Deal.DumbAutoDirectives;
import com.rogerpf.aabridge.model.Lin.BarBlock;

/**
 * 
 * Hand
 */
public class Hand implements Comparable<Hand> {

	/**
	 * 
	 */
	public final Deal deal;

	public Dir compass;
	public String playerName;
	public final Frag[] fOrgs = new Frag[4];
	public final Frag[] frags = new Frag[4];
	public final Cal played = new Cal();
	public final Bal bids = new Bal();

	char compassCh; // for easy viewing when in debug

	Deal dealClone = null;
	Strategy strategy = null;

	public String bubbleText = "";

	public boolean ddsValuesAssigned;
	public char vh_tutorial_vis = '-';

	/** 
	 */
	public Hand(Deal dealV, Dir compass) { // constructor
		deal = dealV;
		this.compass = compass;
		compassCh = compass.toLowerChar();
		playerName = "";
		ddsValuesAssigned = false;
		for (Suit su : Suit.cdhs) {
			fOrgs[su.v] = new Frag(this, su);
			frags[su.v] = new Frag(this, su);
		}
	}

	public void partialClear() {
		// ==============================================================================================
		for (Frag frag : frags) {
			frag.clear();
		}
		for (Frag forg : fOrgs) {
			forg.clear();
		}
		played.clear();
		bids.clear();
	}

	public void restoreUnplayedCardsToDeck() {
		// ==============================================================================================
		for (int j : Zzz.zto3) {
			Frag frag = frags[j];
			Frag forg = fOrgs[j];
			for (int i = frag.size() - 1; i >= 0; i--) {
				Card card = frag.get(i);
				frag.remove(card);
				forg.remove(card);
				// deal.packPristine.addDeltCard(card);
				deal.packPristine.add(card);
			}
		}
	}

	public void restoreUnplayedCardsToDeck_skipKept() {
		// ==============================================================================================
		for (int j : Zzz.zto3) {
			Frag frag = frags[j];
			Frag forg = fOrgs[j];
			for (int i = frag.size() - 1; i >= 0; i--) {
				Card card = frag.get(i);
				if (card.isKept())
					continue;
				frag.remove(card);
				forg.remove(card);
				// deal.packPristine.addDeltCard(card);
				deal.packPristine.add(card);
			}
		}
	}

	/**
	 */
	public boolean areAnyCardsKept() {
		// ==============================================================================================
		for (int j : Zzz.zto3) {
			Frag forg = fOrgs[j];
			for (Card card : forg) {
				if (card.isKept())
					return true;
			}
		}
		return false;
	}

	/**
	 */
	public void clearAllKeptFlags() {
		// ==============================================================================================
		for (int j : Zzz.zto3) {
			fOrgs[j].clearAllKeptFlags();
		}
	}

	// ==============================================================================================
	public boolean didHandStartWith13Cards() {
		int tot = 0;
		for (Suit suit : Suit.cdhs) {
			tot += fOrgs[suit.v].size();
		}
		return (tot == 13);
	}

	/** 
	 */
	public String toString() {
		return compass.toLongStr();
	}

	public int axis() {
		return compass.v % 2;
	}

	/** 
	 */
	public void setToVirgin() {
		for (Suit su : Suit.cdhs) {
			fOrgs[su.v].clear();
			frags[su.v].clear();
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
		fOrgs[card.suit.v].addDeltCard(card);
		frags[card.suit.v].addDeltCard(card);
	}

	/** 
	 */
	public void playCard(Card card) {

		assert (played.size() < deal.prevTrickWinner.size());

		frags[card.suit.v].remove(card);
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

		Suit suitLed = prevWinner.played.getCard(curTrickIndex).suit;

		if (suitLed == frag.suit)
			return true; // we are selecting the
		// led suit

		if (frags[suitLed.v].isEmpty())
			return true; // we have none of the
		// led suit

		return false;
	}

	/** 
	 */
	public int cardSelectableCount(Rank rank) {
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
	public Card cardSelectableGetOnlyCard(Rank rank) {
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
	public boolean isSuitSelectable(Suit suit) {

		if (frags[suit.v].isEmpty())
			return false; // we have none of the
		// selected suit

		int curTrickIndex = deal.getCurTrickIndex();
		Hand prevWinner = deal.getCurTrickLeader();
		if (prevWinner == this)
			return true; // we are on lead

		Suit suitLed = prevWinner.played.getCard(curTrickIndex).suit;

		if (suitLed == suit)
			return true; // we are selecting the led suit

		if (frags[suitLed.v].isEmpty())
			return true; // we have none of the led suit

		return false;
	}

	/** 
	 */
	public Card getCardIfSingletonInSuit(Suit suit) {

		if (frags[suit.v].size() != 1)
			return null; // not singleton

		return frags[suit.v].get(0);
	}

	/** 
	 */
	public Card getCardHighestIn_Frag(Suit suit) {

		if (frags[suit.v].size() == 0)
			return null; // none

		return frags[suit.v].get(0);
	}

	/** 
	 */
	public Card getCardMediumIn_Frag(Suit suit) {
		int size = frags[suit.v].size();
		if (size == 0)
			return null; // none

		return frags[suit.v].get(size / 2);
	}

	/** 
	 */
	public Card getCardLowestIn_Frag(Suit suit) {
		int size = frags[suit.v].size();
		if (size == 0)
			return null; // none

		return frags[suit.v].get(size - 1);
	}

	/** 
	 */
	public Card getCardIfMatching(Suit suit, Rank rank) {
		for (Card card : frags[suit.v]) {
			if (card.rank == rank)
				return card;
		}
		return null;
	}

	/** 
	 */
	public Card getCardIfMatching_Orig(Suit suit, Rank rank) {

		for (Card card : fOrgs[suit.v]) {
			if (card.rank == rank)
				return card;
		}
		return null;
	}

	/** 
	 */
	public Card getCardIfMatchingRankRel(Suit suit, Rank rankRel) {

		for (Card card : frags[suit.v]) {
			if (card.rankRel == rankRel)
				return card;
		}
		return null;
	}

	/** 
	 */
	public Rank getRankIfMatchingRankRel(Suit suit, Rank rankRel) {

		for (Card card : frags[suit.v]) {
			if (card.rankRel == rankRel)
				return card.rank;
		}
		return Rank.Invalid;
	}

	/** 
	 */
	private Suit makeSuitSuggestionIfOnlyOneSuit() {
		Suit suit = Suit.Invalid;
		for (Frag frag : frags) {
			if (frag.size() == 0)
				continue;
			if (suit != Suit.Invalid)
				return Suit.Invalid;
			suit = frag.suit;
		}
		return suit;
	}

	/** 
	 */
	public Suit makeSuitSuggestion() {
		if (deal.getNextHandToPlay() != this)
			return Suit.Invalid;

		int curTrickIndex = deal.getCurTrickIndex();
		Hand prevWinner = deal.getCurTrickLeader();
		if (prevWinner == this) {
			return makeSuitSuggestionIfOnlyOneSuit();
		}

		Suit suitLed = prevWinner.played.getCard(curTrickIndex).suit;

		if (frags[suitLed.v].size() > 0) {
			return suitLed;
		}

		return makeSuitSuggestionIfOnlyOneSuit();
	}

	/** 
	 */
	public Card getSelfPlayableCard(boolean selfPlayAdjacent) {

		int curTrickIndex = deal.getCurTrickIndex();
		Hand prevWinner = deal.getCurTrickLeader();
		Suit suit = Suit.Invalid;
		if (prevWinner == this) {
			suit = makeSuitSuggestionIfOnlyOneSuit();
			if (suit == Suit.Invalid)
				return null;
		}
		else {
			Suit suitLed = prevWinner.played.getCard(curTrickIndex).suit;
			if (frags[suitLed.v].size() > 0) {
				suit = suitLed;
			}
			else { // we have none of the led suit
				suit = makeSuitSuggestionIfOnlyOneSuit();
			}
		}
		if (suit == Suit.Invalid)
			return null;

		Frag frag = frags[suit.v];

		if (frag.size() == 1) {
			return frag.get(0);
		}

		if (selfPlayAdjacent == false)
			return null;

		Card high = frag.get(0);
		for (int i = 1; i < frag.size(); i++) {
			Card mid = frag.get(i);
			for (int j = high.rank.v - 1; j > mid.rank.v; j--) {
				Rank rank = Rank.rankFromInt(j);
				if (deal.isActiveCard(frag, rank, frag.suit))
					return null;
			}
			high = mid;
		}
		return high; // which will actually NOW be the lowest :)
	}

//	/** 
//	 */
//	private boolean hasBeenPlayed(Rke rank, Sue suit) {
//		for (Hand hand : deal.hands) {
//			if (frags[suit.v].getIfRankExists(rank) != null) {
//				return false;
//			}
//			if (played.size() == deal.prevTrickWinner.size()
//					&& played.getLast().matches(suit, rank)) {
//				return false;
//			}
//		}
//		return true; // not in the hands so it has been played
//	}

	/** 
	 */
	public Card getRandomPlayableCard() {

		int from = (int) (Math.random() * 4);

		for (Suit su : Suit.cdhs) {
			Frag frag = frags[(su.v + from) % 4];
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

		for (Suit su : Suit.cdhs) {
			Frag frag = frags[(su.v + from) % 4];
			if (frag.size() == 0)
				continue;
			return frag.get(0); // the first is any
		}
		return null;
	}

	/** 
	 */
	public boolean isDummy() {
		return !deal.contract.isCall() && (compass == deal.contractCompass.rotate180());
	}

	/** 
	 */
	public void undoLastBid() {
		// assert (bids.size() > 1);
		bids.removeLast();
	}

	/**
	 */
	public Card undoLastPlay() {

		// int numb_played = deal.countCardsPlayed();	
		// System.out.println( "np = " + numb_played);
		// App.clearDdsKept_at(numb_played);

		deal.clearAllStrategies();

		if (deal.isCurTrickComplete()) {
			deal.prevTrickWinner.removeLast();
		}

		Card card = played.removeLast();
		// card.setKept(false);
		frags[card.suit.v].addDeltCard(card);
		return card;
	}

//	/**
//	 */
//	int count_Bergen_starting() {
//		int t = 0;
//
//		int adj3 = 0;
//
//		for (Frag fOrg : fOrgs) {
//			
//			int top5 = 0;
//			
//			boolean has_Ace = false;
//			boolean has_King = false;
//			boolean has_Queen = false;
//			boolean has_Jack = false;
//
//			for (Card card : fOrg) {
//				Rank r = card.rank;
//				// @formatter:off
//			    switch (r) {
//			    	default: continue;
//			    	case Ace:   t += 4;   has_Ace   = true; adj3++; top5++; continue;  // HCPs
//			    	case King:  t += 3;   has_King  = true;         top5++; continue;
//			    	case Queen: t += 2;   has_Queen = true; adj3--; top5++; continue;
//			    	case Jack:  t += 1;   has_Jack  = true; adj3--; top5++; continue;	    	
//			    	case Ten:                               adj3++; top5++; continue;	    	
//			    }
//			    // @formatter:on
//			}
//
//			if (fOrg.size() >= 4 && top5 >= 3) { // Quality Suit
//				t = t + 1;
//			}
//
//			if (fOrg.size() >= 5) { // Suits 5 or longer
//				t = t + (fOrg.size() - 4);
//			}
//
//			boolean noAceOrKing = !(has_Ace || has_King); // dubious doubletons
//			if ((fOrg.size() == 2) && ((noAceOrKing && (has_Queen || has_Jack)) || (has_King && has_Jack) || (has_King && has_Queen))) {
//				t = t - 1;
//			}
//			else if ((fOrg.size() == 1) && (has_King || has_Queen || has_Jack)) {
//				t = t - 1;
//			}
//		}
//
//		// adjust 3 diff between quacks and AT's
//		if (adj3 > 0)
//			t = t + adj3 / 3;
//		else if (adj3 < 0)
//			t = t - (-adj3) / 3;
//
//		return t;
//	}
//
//
//	/**
//	 */
//	public int count_Bergen() {
//
//		Suit suit = Suit.Invalid;
//		boolean short_hand = false;
//		boolean long_hand = false;
//		
//		// much stuff needed here
//
//		if (suit != Suit.Invalid && long_hand) {
//			return count_Bergen_declarer(suit);
//		}
//		else if (suit != Suit.Invalid && short_hand) {
//			return count_Bergen_dummy(suit);
//		}
//		else 
//		return count_Bergen_starting();
//	}

	/**
	 */
	public int count_LongSuitPoints() {
		int v = 0;
		for (Frag fOrg : fOrgs) {
			v += fOrg.count_LongSuitPoints();
		}
		return v;
	}

	/**
	 */
	public int count_ShortSuitPoints() {
		int v = 0;
		for (Frag fOrg : fOrgs) {
			v += fOrg.count_ShortSuitPoints();
		}
		return v;
	}

	/**
	 */
	public int count_HighCardPoints() {
		int v = 0;
		for (Frag fOrg : fOrgs) {
			v += fOrg.count_HighCardPoints();
		}
		return v;
	}

	/**
	 *  As published in 1980s in Bridge World see   http://www.rpbridge.net/8j19.htm
	 *  Kaplan and Rubens
	 */
	public double count_KnR() {
		double t = 0;
		int count_3 = 0;

		for (Frag fOrg : fOrgs) {

			t += fOrg.count_KnR();

			if (fOrg.size() == 3)
				count_3++;
		}

		t = t - 1; // Step Final A

		if (count_3 == 3) { // Step Final B
			t = t + 0.5;
		}

		return t;
	}

	/**
	 */
	public int countLosingTricks_Basic_x2() {
		int v = 0;
		for (Frag fOrg : fOrgs) {
			v += fOrg.countLosingTricks_Basic_x2();
		}
		return v;
	}

	/**
	 */
	public int countLosingTricks_Ref_x2() {
		int v = 0;
		for (Frag fOrg : fOrgs) {
			v += fOrg.countLosingTricks_Ref_x2();
		}
		return v;
	}

	/**
	 */
	public int count_Banzai() {
		int v = 0;
		for (Frag fOrg : fOrgs) {
			v += fOrg.count_Banzai();
		}
		return v;
	}

	/**
	 */
	public Frag[] makeFragsAsOf(int reviewTrick, int reviewCard) {
		Frag[] frs = new Frag[4];
		for (Suit su : Suit.cdhs) {
			frs[su.v] = (Frag) fOrgs[su.v].clone();
		}

		if ((deal.prevTrickWinner.size() > 0) && (played.size() > 0)) {

			// remove all the cards played in all the tricks BEFORE the review trick
			for (int i = 0; i < (reviewTrick); i++) {
				if (played.size() < i)
					continue;
				if (i >= played.size()) {
					@SuppressWarnings("unused")
					int z = 0; // put your breakpoint here
					continue;
				}
				Card card = played.get(i);
				frs[card.suit.v].remove(card);
			}

			Hand trickLeader = deal.prevTrickWinner.get(reviewTrick);

			int i = -1;
			for (Hand hand : deal.rota[trickLeader.compass.v]) {
				i++;
				if (hand != this || i >= reviewCard)
					continue;
				Card card = hand.played.get(reviewTrick);
				frs[card.suit.v].remove(card);
				break;
			}
		}

		return frs;
	}

	/** 
	 */
	public Frag[] fragsfOrgsVisFeintMerge() {
		Frag[] frs = new Frag[4];

		boolean suppressHidding = /* App.devMode && */ App.alwaysShowHidden;

		for (Suit su : Suit.cdhs) {
			char c = fOrgs[su.v].showPlayedVis;

			if (!suppressHidding && (c == '*')) {
				Frag fr = (Frag) frags[su.v].clone();
				fr.clear();
				frs[su.v] = fr;
			}
			else if (c == 'd') {
				Frag fr = (Frag) frags[su.v].clone();
				fr.clear();
				for (Card card : frags[su.v]) {
					fr.addDeltCard(new Card(card, c /* feint */));
				}
				frs[su.v] = fr;
			}
			else if (c == 'p') {
				Frag fr = (Frag) frags[su.v].clone();
				for (Card card : fOrgs[su.v]) {
					if (fr.contains(card))
						continue;
					fr.addDeltCard(new Card(card, c /* feint */));
				}
				frs[su.v] = fr;
			}
			else {
				frs[su.v] = frags[su.v];
			}
		}

		return frs;
	}

	/** 
	 */
	public int compareTo(Hand other) {
		return other.count_HighCardPoints() - count_HighCardPoints();
	}

	/**
	 */
	public boolean isThisCardNeededToWinWhen4thToPlay(Card bi_card) {

		if (deal.countCardsPlayed() % 4 != 3)
			return false; // Not 4th to play

		Hand leader = deal.getCurTrickLeader();

		Card bestCard = leader.played.getLast();
		Hand bestHand = leader;
		for (Hand hand : deal.rota[leader.nextHand().compass.v]) {
			if (hand.played.size() < leader.played.size())
				break; // end of played cards in this trick
			Card card = hand.played.getLast();
			if (card.isBetterThan(bestCard, deal.contract.suit)) {
				bestHand = hand;
				bestCard = card;
			}
		}
		if (bestHand.axis() == axis())
			return false; // partner is already winning

		return bi_card.isBetterThan(bestCard, deal.contract.suit);
	}

	/** 
	 */
	public boolean areWeWinning() {

		Hand leader = deal.getCurTrickLeader();
		if (leader.nextHand() == this)
			return false; // second to play so the answer must be no

		Hand bestHand = leader;
		Card bestCard = leader.played.getLast();
		for (Hand hand : deal.rota[leader.nextHand().compass.v]) {
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

		for (Hand hand : deal.rota[leader.nextHand().compass.v]) {
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
				if (card.rank == Rank.King || card.rank == Rank.Ace)
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
			for (Card card : partner().frags[f.suit.v]) {
				f.addDeltCard(card);
			}
		}
	}

	/**
	 */
	boolean areOurTopHoldingsContigious(Suit suit) {
		// ==============================================================================================
		Frag myFrag = frags[suit.v];
		Frag pnFrag = partner().frags[suit.v];
		if (myFrag.size() == 0 || pnFrag.size() == 0)
			return false;
		return myFrag.get(0).rankEqu == pnFrag.get(0).rankEqu;
	}

	boolean doesPartnerHaveMaster(Suit suit) {
		// ==============================================================================================
		Frag pnFrag = partner().frags[suit.v];
		return (pnFrag.size() > 0) && (pnFrag.get(0).rankRel == Rank.Ace);
	}

	static int oppsMax(Gather g, Suit suit) {
		// ==============================================================================================
		return Math.max(g.LHO.frags[suit.v].size(), g.RHO.frags[suit.v].size());
	}

	static int oppsAceDownRunDepth(Gather g, Suit suit) {
		// ==============================================================================================
		int d = 0;
		int t = Rank.Ace.v;
		for (Card card : g.oppsBoth[suit.v]) {
			if (card.rankRel.v != t--)
				break;
			d++;
		}
		return Math.min(d, oppsMax(g, suit));
	}

	static int ourInnerRunDepth(Gather g, Suit suit, int ord) {
		// ==============================================================================================
		int d = 0;
		int t = Rank.Ace.v - ord;
		for (Card card : g.ourBoth[suit.v]) {
			if (card.rankRel.v != t--)
				break;
			d++;
		}
		return d;
	}

	/**
	 */
	public Card pickBestDiscard(Gather g) {
		// ==============================================================================================
		if (g.declarerAxis == axis())
			return Qlay_5_Discard.pickBest(g);
		else
			return Rlay_5_Discard.pickBest(g);
	}

	public void clearStrategy() {
		// ==============================================================================================
		dealClone = null;
		strategy = null;
	}

	public Strategy getStrategy() {
		// ==============================================================================================
		return strategy;
	}

	public void calcStrategy(DumbAutoDirectives dumbAutoDir) {
		// ==============================================================================================
		// note - we are in a ****** hand ******

		boolean skipFirst = true;

		if (getStrategy() == null) {
			// print a message if we are the first of the stratergies to be created
//			if (deal.testId == 0 && deal.hands[(compass.v + 3) % 4].getStrategy() == null) {
//				System.out.println("Board no " + deal.realBoardNo + "     cycle " + (++(deal.cycle))
//						+ "  ----------------------------------------------------------------------");
//			}

			Deal d2 = deal.deepClone();
			d2.wipePlay();
			dealClone = d2;
			strategy = new Strategy(d2, this);
			skipFirst = false;
		}

		/** 
		 * We now go through each played card updating the statergy before each play
		 * This is only needed as it re-creates all the history of a Strategy lost
		 * because of an UNDO or a 'load'.  So 99% of the time this will do nothing
		 * 
		 * Actually we are re-creating the play to update the embedded deals,
		 * it is the side effects on the embedded deals that we need to recreate
		 * as the (relativly) transient stratergies are updated.
		 */
//		Card cardThatWould;
		int mainDealPlayed = deal.countCardsPlayed();
		Deal d2 = dealClone;
		int d2Played = d2.countCardsPlayed();

		for (int i = d2Played; i < mainDealPlayed; i++) {

			Card card = deal.getCardThatWasPlayed(i);
			int h2compass = deal.getCompassThatWasPlayed(i);

//			Hand h2 = dealClone.hands[h2compass];

			if (h2compass == compass.v) {
				// update the strategy - which MAY alter the cloneDeal (but does not currently)
				if (skipFirst) {
					// we are skipping because this update would have been done on the last round
					// the thing that did not happen was that the card was not played (as it was yet to be chosen)
					skipFirst = false;
				}
//				else {
//					Gather g2 = strategy.update(dumbAutoDir);
//					// for now we assume we can call this for all defenders
//					if (h2.axis() == d2.defenderAxis()) {
//						cardThatWould = h2.dumbAutoInner(g2);
//						if (cardThatWould != null && (cardThatWould.rank != card.rank || cardThatWould.suit != cardThatWould.suit)) {
//							System.out.println(" RERUN ===  " + card + " was played,  this time would play  " + cardThatWould);
//						}
//						cardThatWould = null;
//					}
//				}
			}

			// we can now update the clone deal
			d2.playCardExternal(card.suit, card.rank);
		}

		strategy.update(dumbAutoDir);

		// now we can go off to have a new card played for real
	}

	/**
	 */
	public Card dumbAuto(DumbAutoDirectives dumbAutoDir) {
		// ==============================================================================================

		calcStrategy(dumbAutoDir);

		Gather g = new Gather(this, dumbAutoDir, /* strategyCreated */false);

		Card card = dumbAutoInner(g);

		if (card == null) {
			card = getRandomPlayableCard();
			System.out.println("===>   ERROR  -  dumbAuto picked    NULL   - instead of a card");
		}

		// System.out.print(card + "   ");

		return card;
	}

	/**
	 */
	public Card dumbAutoInner(Gather g) {
		// ==============================================================================================
		Card card = null;

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
			case 0: if (g.trickNumb == 0) { 
					       card = Rlay_0th__TheOpeningLead.act(g);
			          } else { 
			        	   card = Rlay_1st__Defender.act(g);
			          }; 					break;
			case 1: card = Rlay_2nd.act(g); break;
			case 2: card = Rlay_3rd.act(g); break;
			case 3: card = Rlay_4th.act(g); break;
			default: assert(false);
			}
		}
		// @formatter:on

		return card;
	}

	public String cardsForLinSave() {
		// ==============================================================================================
		String s = "";
		for (Suit su : Suit.shdc) { // Spades first
			Frag fOrg = fOrgs[su.v];
			int sl = fOrg.size();
			if (sl > 0) {
				s += su.toStrLower();
				for (int j = 0; j < sl; j++) {
					s += fOrg.get(j).rank.toStr();
				}
			}
		}
		return s;
	}

	public int countOriginalCards() {
		// ==============================================================================================
		int tot = 0;
		for (Suit su : Suit.cdhs) {
			Frag fOrg = fOrgs[su.v];
			tot += fOrg.size();
		}
		return tot;
	}

	public int countUnplayedCards() {
		// ==============================================================================================
		int tot = 0;
		for (Suit su : Suit.cdhs) {
			Frag frag = frags[su.v];
			tot += frag.size();
		}
		return tot;
	}

	public void setHandShowXes(String s) {
		// ==============================================================================================
		for (Suit suit : Suit.shdc) {
			int i = 3 - suit.v;
			char c = s.charAt(i);
			fOrgs[suit.v].setShowXes(c);
			frags[suit.v].setShowXes(c);
		}

	}

	public void setHandShowPlayedVis(String s) {
		// ==============================================================================================
		for (Suit suit : Suit.shdc) {
			int i = 3 - suit.v;
			char c = s.charAt(i);
			fOrgs[suit.v].setShowPlayedVis(c);
			frags[suit.v].setShowPlayedVis(c);
		}
	}

	public void setHandShowTutorialVis(char c) {
		// ==============================================================================================
		if (c != 'v' && c != 'h')
			c = '-';
		vh_tutorial_vis = c;
	}

	public String pdbHand() {
		// ==============================================================================================
		String rtn = "";

		rtn += fOrgs[Suit.Spades.v].pdbSuit() + ".";
		rtn += fOrgs[Suit.Hearts.v].pdbSuit() + ".";
		rtn += fOrgs[Suit.Diamonds.v].pdbSuit() + ".";
		rtn += fOrgs[Suit.Clubs.v].pdbSuit();

		return rtn;
	}

	static final String infn = "  inside getUnplayedCardMatchingRaw()   ";

	public Card getUnplayedCardMatchingRaw(String rawCards, BarBlock bb, boolean selectableNeeded) {
		// ==============================================================================================
		Card card = null;
		String raw = rawCards.trim().toLowerCase() + " ";
		int len = raw.length();

		for (int i = 0; i < len; i++) {
			if (raw.charAt(i) == ' ')
				continue;
			Suit suit = Suit.charToSuit(raw.charAt(i));
			if (suit == Suit.Invalid) {
				System.out.println("  line:" + bb.lineNumber + infn + " SUIT Invalid  rawCards  >>> " + rawCards + " <<<   character at pos:" + (i + 1)
						+ "\n    bb:" + bb);
				return null;
			}

			if (selectableNeeded && isSuitSelectable(suit) == false) {
				i++; // skip the supposed rank character
				continue;
			}

			char rank_c = raw.charAt(i + 1);	// i.e. we look ahead one character		

			if (rank_c == 'u') {
				i++;
				card = getCardHighestIn_Frag(suit);
				if (card == null)
					continue;
				return card;
			}

			else if (rank_c == 'm') {
				i++;
				card = getCardMediumIn_Frag(suit);
				if (card == null)
					continue;
				return card;
			}

			else if (rank_c == 'l') {
				i++;
				card = getCardLowestIn_Frag(suit);
				if (card == null)
					continue;
				return card;
			}

			Rank rank = Rank.charToRank(rank_c);

			if (rank == Rank.Invalid) {
				// NO  i++;  ...  it is probably another suit
				card = getCardLowestIn_Frag(suit);
				if (card == null)
					continue;
				return card;
			}

			i++; // it was a valid rank so eat it
			card = getCardIfMatching(suit, rank);
			if (card == null)
				continue;
			return card;
		}

		return null;
	}

}
