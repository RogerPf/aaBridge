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
package com.rogerpf.aabridge.dds;

import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.dds.deal.ByValue;
import com.rogerpf.aabridge.model.Card;
import com.rogerpf.aabridge.model.Deal;
import com.rogerpf.aabridge.model.Dir;
import com.rogerpf.aabridge.model.Frag;
import com.rogerpf.aabridge.model.Hand;
import com.rogerpf.aabridge.model.Rank;
import com.rogerpf.aabridge.model.Suit;

public class Z_ddsCalculate {
	// ---------------------------------- CLASS -------------------------------------

	static public boolean is_dds_available() {

		boolean ans = false;

		try {
			ComRogerpfAabridgeDdsLibrary dds = ComRogerpfAabridgeDdsLibrary.INSTANCE;
			deal dl = new deal.ByValue();
			dl.trump = Suit.NoTrumps.vX();
			dl.first = Dir.South.v;

			for (int i = 0; i < 2; i++) {
				dl.currentTrickSuit[i] = 0;
				dl.currentTrickRank[i] = 0;
			}

			for (int i = 0; i < 16; i++) {
				dl.remainCards[i] = 0;
			}

			dl.remainCards[Dir.East.v * 4 + Suit.Hearts.vX()] = (1 << Rank.Ace.v) + (1 << Rank.Queen.v);
			dl.remainCards[Dir.East.v * 4 + Suit.Diamonds.vX()] = (1 << Rank.Nine.v);
			dl.remainCards[Dir.East.v * 4 + Suit.Clubs.vX()] = (1 << Rank.Two.v);

			dl.remainCards[Dir.South.v * 4 + Suit.Hearts.vX()] = (1 << Rank.King.v) + (1 << Rank.Ten.v);
			dl.remainCards[Dir.South.v * 4 + Suit.Diamonds.vX()] = (1 << Rank.Two.v);
			dl.remainCards[Dir.South.v * 4 + Suit.Clubs.vX()] = (1 << Rank.Jack.v);

			dl.remainCards[Dir.North.v * 4 + Suit.Hearts.vX()] = (1 << Rank.Three.v) + (1 << Rank.Two.v);
			dl.remainCards[Dir.North.v * 4 + Suit.Diamonds.vX()] = (1 << Rank.Three.v);
			dl.remainCards[Dir.North.v * 4 + Suit.Clubs.vX()] = (1 << Rank.Three.v);

			dl.remainCards[Dir.West.v * 4 + Suit.Hearts.vX()] = (1 << Rank.Five.v) + (1 << Rank.Four.v);
			dl.remainCards[Dir.West.v * 4 + Suit.Diamonds.vX()] = (1 << Rank.Five.v);
			dl.remainCards[Dir.West.v * 4 + Suit.Clubs.vX()] = (1 << Rank.Five.v);

			int target = 0; // Ignored with solutions = 3 and mode >= 1
			int solutions = 3; // All cards returned (or duplicates) with scores
			int mode = 1; // Always serach to find the score
			int threadIndex = 0;
			futureTricks futp = new futureTricks();

			int resp = dds.SolveBoard((ByValue) dl, target, solutions, mode, futp, threadIndex);

			@SuppressWarnings("unused")
			int z = 0;

			if (resp == 1 && futp.cards == 4 && futp.score[1] == 1 && futp.score[0] == 2 && futp.rank[0] == Rank.Jack.v) {
				ans = true;
			}
		} catch (UnsatisfiedLinkError e) {
			System.out.println("RPf Invoking -dds- UnsatisfiedLinkError:  " + e.getMessage());
			ans = false;
		} catch (Exception e) {
			System.out.println("RPf Invoking -dds- Exception:  " + e.getMessage());
			ans = false;
		} catch (Error e) {
			// I know but what if future systems fail in a nasty way for this OPTIONAL feature ?
			System.out.println("RPf Invoking -dds- Error:  " + e.getMessage());
			ans = false;
		}

		return ans;
	}

	/**
	 * 
	 */
	public static Card improveDumbPlay(Hand abHand, Card abCard) {

		ComRogerpfAabridgeDdsLibrary dds = ComRogerpfAabridgeDdsLibrary.INSTANCE;

		Deal abDeal = abHand.deal;

		deal dl = new deal.ByValue(); // this 'deal' is the dds deal

		dl.trump = abDeal.contract.suit.vX();

		dl.first = abDeal.getCurTrickLeader().compass.v;

		for (int i = 0; i < 2; i++) {
			dl.currentTrickSuit[i] = 0;
			dl.currentTrickRank[i] = 0;
		}

		int countCardsPlayed = abDeal.countCardsPlayed();
		int playedThisTrick = countCardsPlayed % 4;

		if (playedThisTrick != 0) {
			int indFirst = countCardsPlayed - playedThisTrick;
			for (int i = 0; i < playedThisTrick; i++) {
				Card played = abDeal.getCardThatWasPlayed(indFirst + i);
				dl.currentTrickSuit[i] = played.suit.vX();
				dl.currentTrickRank[i] = played.rank.v;
			}
		}

		for (int i = 0; i < 16; i++) {
			dl.remainCards[i] = 0;
		}

		for (Hand hand : abDeal.hands) {
			for (Suit suit : Suit.cdhs) {
				Frag frag = hand.frags[suit.v];
				dl.remainCards[hand.compass.v * 4 + suit.vX()] = frag.asDdsBits();
			}
		}

		int target = -1; // (13 - (countCardsPlayed-1)/4); // all the rest
		int solutions = 2; // All cards returned (or duplicates) with scores
		int mode = 1; // Always search to find the score
		int threadIndex = 0;
		futureTricks futp = new futureTricks();

		int resp = dds.SolveBoard((ByValue) dl, target, solutions, mode, futp, threadIndex);

		// System.out.println( "\n  " + resp + " AAAAAAAAAA   " + abHand.compass + " " + abCard);

		if (resp != 1)
			return abCard;

		int cand_suit = abCard.suit.vX();
		int cand_rank = abCard.rank.v;
		int cand_rBit = 1 << cand_rank;

		int topScore = futp.score[0];

		if (topScore == 0)
			return abCard;

		for (int i = 0; i < 13; i++) {

			if (futp.score[i] != topScore)
				break;

			if (futp.suit[i] == cand_suit) {
				if ((futp.rank[i] == cand_rank) || ((futp.equals$[i] & cand_rBit) > 0)) {
					return abCard;
				}
			}
		}

		// so we have a different card suggested so take it

		Suit suit = Suit.suitFromInt(3 - futp.suit[0]);
		Rank rank = Rank.rankFromInt(futp.rank[0]);
		Card cand = abHand.getCardIfMatching(suit, rank);

		if (cand != null) {// How can it be null !
			if (App.devMode) {
				String s = ">>>>>>>>>>> Trick: " + (countCardsPlayed / 4 + 1) + "   card:" + (countCardsPlayed % 4 + 1) + "      " + abHand.compass
						+ "    aaB: " + abCard.toLinStr() + "    dds: " + cand.toLinStr();
				System.out.println(s);
			}
			return cand;
		}

		@SuppressWarnings("unused")
		int z = 0;

		return abCard;
	}

	/**
	 * 
	 */
	public static Deal scoreCardsInNextHandToPlay(Deal abDeal) {

		ComRogerpfAabridgeDdsLibrary dds = ComRogerpfAabridgeDdsLibrary.INSTANCE;

		Hand abHand = abDeal.getNextHandToPlay();

		if (abHand == null)
			return null;

		deal dl = new deal.ByValue(); // this 'deal' of course is the dds deal

		dl.trump = abDeal.contract.suit.vX();

		dl.first = abDeal.getCurTrickLeader().compass.v;

		for (int i = 0; i < 2; i++) {
			dl.currentTrickSuit[i] = 0;
			dl.currentTrickRank[i] = 0;
		}

		int countCardsPlayed = abDeal.countCardsPlayed();
		int playedThisTrick = countCardsPlayed % 4;

		if (playedThisTrick != 0) {
			int indFirst = countCardsPlayed - playedThisTrick;
			for (int i = 0; i < playedThisTrick; i++) {
				Card played = abDeal.getCardThatWasPlayed(indFirst + i);
				dl.currentTrickSuit[i] = played.suit.vX();
				dl.currentTrickRank[i] = played.rank.v;
			}
		}

		for (int i = 0; i < 16; i++) {
			dl.remainCards[i] = 0;
		}

		for (Hand hand : abDeal.hands) {
			for (Suit suit : Suit.cdhs) {
				Frag frag = hand.frags[suit.v];
				dl.remainCards[hand.compass.v * 4 + suit.vX()] = frag.asDdsBits();
			}
		}

		int target = 0; // is don't care when solutions = 3
		int solutions = 3; // return all cards that can legally be played with their scores
		int mode = 1; // Always search to find the score
		int threadIndex = 0; // not used for single threaded but must be zero

		futureTricks futp = new futureTricks();

		int resp = dds.SolveBoard((ByValue) dl, target, solutions, mode, futp, threadIndex); // <<<<<<<<<<<<<<

		if (resp != 1) // should not happen
			return null;

		abHand.ddsValuesAssigned = true;

		for (int i = 0; i < 13; i++) {

			int ddsRank = futp.rank[i];
			if (ddsRank == 0)
				break;

			int ddsSuit = futp.suit[i];
			int score = futp.score[i];
			int equals = futp.equals$[i] | (1 << ddsRank); // OR in the main one so we can treat them all the same

			Suit abSuit = Suit.suitFromInt(3 - ddsSuit);

			for (ddsRank = 14; (equals != 0); ddsRank--) {
				int bitRank = (1 << ddsRank);
				if ((equals & bitRank) != 0) {
					equals &= ~bitRank; // mark it done
					Rank abRank = Rank.rankFromInt(ddsRank);
					Card abCard = abHand.getCardIfMatching(abSuit, abRank);
					if (abCard != null) // how can it be null
						abCard.setDdsScore(score);
				}
			}
		}

		return abDeal;
	}

	/**
	 * 
	 */
	public static Z_bothResults analyse(Deal abDeal) {

		ComRogerpfAabridgeDdsLibrary dds = ComRogerpfAabridgeDdsLibrary.INSTANCE;

		Hand abHand = abDeal.getNextHandToPlay();

		Z_bothResults rtn = new Z_bothResults();

		if (abHand == null) {
			rtn.resp = 99;
			rtn.errStr = "99 - No Valid player found in deal - getNextHandToPlay() !";
			return rtn;
		}

		ddTableDeal.ByValue tableDl = new ddTableDeal.ByValue();

		for (Hand hand : abDeal.hands) {
			for (Suit suit : Suit.cdhs) {
				Frag fOrg = hand.fOrgs[suit.v];
				tableDl.cards[hand.compass.v * 4 + suit.vX()] = fOrg.asDdsBits();
			}
		}

		rtn.resp = dds.CalcDDtable(tableDl, rtn.ddTableRes);

		if (rtn.resp != 1) { // should not happen
			rtn.errStr = rtn.resp + ""; // Error string lookup may be added
			return rtn;
		}

		rtn.resp = dds.DealerParBin(rtn.ddTableRes, rtn.parResMaster, abDeal.getDdsVulnerability(), abDeal.contractCompass.v);

		if (rtn.resp != 1) { // should not happen
			rtn.errStr = rtn.resp + ""; // Error string lookup may be added
		}

		return rtn;
	}

}