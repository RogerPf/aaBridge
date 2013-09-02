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

import java.util.ArrayList;

import com.rogerpf.aabridge.model.Deal.DumbAutoDirectives;

class StraStep {
	// ---------------------------------- CLASS -------------------------------------
	String idea;
	String cause_info_only;
	int rankRel;
	int suit;
	int compass;

	StraStep(String idea, String cause, int rankRel, int suit, int compass) { // constructor
		// ==============================================================================================

		this.idea = idea;
		this.cause_info_only = cause;
		this.rankRel = rankRel;
		this.suit = suit;
		this.compass = compass;
	}

	Card getAdvisedCard(Hand h) {
		// ==============================================================================================
		return (rankRel < 2) ? null : h.getCardIfMatchingRankRel(suit, rankRel);
	}
}

/**
 * 
 * @author roger
 *
 */
public class Strategy extends ArrayList<StraStep> {
	// ---------------------------------- CLASS -------------------------------------

	private static final long serialVersionUID = 1L;

	Hand hExt;

	Deal d;
	Hand h;

	boolean drawTrumpsHint = false;
	boolean signalWanted[] = { true, true, true, true };

	public static final String PlayCard = "PlayCard";
	public static final String DrawTrumps = "DrawTrumps";
	public static final String RunSuit = "RunSuit";
	public static final String RunTopTricksInSuit = "RunTopTricksInSuit";
	public static final String GetToHand = "GetToHand";
	public static final String TakeFinnesse = "TakeFinnesse";
	public static final String PlayGivenCard = "PlayGivenCard";

	public Deal getEmdeddedDeal() {
		// ==============================================================================================
		return d;
	}

	public boolean containsGetToHand(int compass) {
		// ==============================================================================================
		for (StraStep step : this) {
			if (step.idea == GetToHand) {
				if (step.compass == compass)
					return true;
			}
			break;
		}
		return false;
	}

	public boolean containsRunTopTricksInSuit() {
		// ==============================================================================================
		for (StraStep step : this) {
			if (step.idea == RunTopTricksInSuit) {
				return true;
			}
		}
		return false;
	}

	/**
	 * A Strategy is created only ONCE for the defense and ONCE for the declarer. This happens 
	 * (for the defence) just before the first card is played and for declarer just before the
	 * dummys first card is played
	 * 
	 * @param d   a deapclone of the current deal that the strategy keeps for its life
	 */
	Strategy(Deal d, Hand hExt) { // constructor
		// ==============================================================================================
		this.d = d;
		this.hExt = hExt;
		this.h = d.hands[hExt.compass]; // reminds us that every Stratergy is focused on a hand
		this.h.strategy = this; // set the clone hand to have its us as its strategy
		this.h.dealClone = d; // set the clone hand to have its us as its strategy

		// A Strategy is always *CREATED* when and only when that side is ready to play their first card

//		if (h.axis() == d.contractAxis()) {
//			assert (d.countCardsPlayed() == 1);
//		}
//		else {
//			assert (d.countCardsPlayed() == 0);
//		}

		String s = (d.testId == 0) ? "" : "test_" + d.testId + "  ";
		System.out.println(s + "Strategy Created  " + d.countCardsPlayed() + "  " + Zzz.axis_st[h.axis()] + "  Tk " + (d.countCardsPlayed() / 4 + 1) + " "
				+ Zzz.playOrd_st[d.countCardsPlayed() % 4]);

		if (h.axis() == d.contractAxis()) {
			// This is INITIAL declarer strategy **********
			// drawTrumpsHint = (d.contract.suit != Zzz.Notrumps); // assumed
		}
		else {
			// The defenders initial strategy currently has no content
		}

		/**
		 *  'update' can now be called by the Strategy creator 
		 */
	}

	/**
	 * 
	 */
	public Gather update(DumbAutoDirectives dumbAutoDir) {
		// ==============================================================================================
		Hand h = d.getNextHandToPlay(); // reminds us that every Stratergy is focused on a hand / axis

		clear(); // discard any previous - must do this before the re-analyze

		// assert( axis == h.axis());

		// assert(axis != h.axis()); // no need to / MUST NOT / analyse unless it is our sides turn to play

		Gather g = new Gather(h, dumbAutoDir, /* strategyCreated */true);

		String s = (d.testId == 0) ? "" : "test_" + d.testId + " ";
		System.out.println(s + "Strgy Upd  Tk " + (d.countCardsPlayed() / 4) + "  " + Zzz.compass_to_nesw_st[h.compass] + " "
				+ Zzz.playOrd_st[d.countCardsPlayed() % 4]);

		if (h.axis() != d.contractAxis())
			return g; // for now we just want the declarer side

		// @formatter:off
		if (g.ourContract) {
			switch(g.positionInTrick) {
			case 0: Qstr_1st__Declarer.act(this, g); break;
			case 1: Qstr_2nd.act(this, g); break;
			case 2: Qstr_3rd.act(this, g); break;
			case 3: Qstr_4th.act(this, g); break;
			default: assert(false);
			}
		}
		else {
//			switch(g.positionInTrick) {
//			case 0: { if (g.trickNumb == 0) { 
//					card = Rstr_0th__TheOpeningLead.act(this, g); break; }
//			 else { card = Rstr_1st__Defender.act(this, g); break; } }
//			case 1: card = Rstr_2nd.act(this, g); break;
//			case 2: card = Rstr_3rd.act(this, g); break;
//			case 3: card = Rstr_4th.act(this, g); break;
//			default: assert(false);
//			}
		}
		// @formatter:on

		return g;
	}

	static Card selectBestEntryIntoPartner(Gather g) {
		// ==============================================================================================
		g.sort_entryIntoPartner();
		FragAnal fa = g.fragAnals[0];
		if (fa.mpatRtn.rating < 1)
			return null;

		return fa.myFrag.getLast();
	}

}
