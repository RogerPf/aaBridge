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
package com.rogerpf.aabridge.view;

import java.util.ArrayList;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.model.Deal;

/**
 */
class DePoint {
	// ---------------------------------- CLASS -------------------------------------
	int index;
	char type;
	char state;
	String text; // or id ?
	float from; // percent of total width
	float to;
	boolean spike;
	int bidInd;
	int trickInd;
	int cardInd;

	/**
	 */
	DePoint(int index, char type, char state, String text, float from, float to, boolean spike, int bidInd, int trickInd, int cardInd) {
		// ============================================================================
		this.index = index;
		this.type = type;
		this.state = state;
		this.text = text; // or id ?
		this.from = from;
		this.to = to;
		this.spike = spike;
		this.bidInd = bidInd;
		this.trickInd = trickInd;
		this.cardInd = cardInd;
	}
}

/**
 */
public class DePointAy extends ArrayList<DePoint> {
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	/**
	 * @param width 
	 * @param lINE_END_GAP 
	 */
	public DePointAy(float width, float LINE_END_GAP) {
		// =============================================================================
		/**
		 * We parse the deal making a list of the display and click points
		 */

		Deal deal = App.deal;

		float BID_MARK_WIDTH = 0.04f;
		float PLAY_MARK_WIDTH = 0.05f;
		float FIN_MARK_WIDTH = 0.04f;

		float BIDDING_WIDTH = 0.15f;

		int index = 0;

		float from = LINE_END_GAP * width;
		float to = (LINE_END_GAP + BID_MARK_WIDTH) * width;

		add(new DePoint(index++, 'b', 'h', "Bid", from, to, true, 0, 0, 0));

		int bids = deal.countBids();

		if (bids == 0)
			return;

		int STD_BID_COUNT = 12;

		int bidDiv = (bids < STD_BID_COUNT) ? STD_BID_COUNT : bids;

		float bidWidth = (BIDDING_WIDTH * width) / bidDiv;

		char state = 'h';

		boolean contractReal = App.deal.isContractReal();

		for (int i = 1; i < bids + 1; i++) {
			boolean spike = (i > 0) && (i != bids) && (i % 4 == 0);

			if (App.isMode(Aaa.REVIEW_BIDDING)) {
				if (i > App.reviewBid)
					state = 'u';
			}

			from = to;
			to += bidWidth;
			char b_or_c = (i == bids && contractReal) ? 'c' : 'b';
			add(new DePoint(index++, b_or_c, state, "", from, to, spike, i, 0, 0));
		}

		if (bids < STD_BID_COUNT) {
			from = to;
			to += bidWidth * (STD_BID_COUNT - bids);
			add(new DePoint(index++, 'c', state, "", from, to, false, 0, 0, 0));
		}

		if (deal.isAuctionFinished() == false)
			return;

		float playWidth = (1.0f - 2 * LINE_END_GAP - BID_MARK_WIDTH - BIDDING_WIDTH - PLAY_MARK_WIDTH - FIN_MARK_WIDTH) * width;

		float unitWidth = playWidth / (52 * 2 + 12 * 3); // we have a five cycle one blank and a 3 to 2 ratio

		float cardWidth = unitWidth * 2;
		float spikeWidth = unitWidth * 3;

		from = to;
		to += PLAY_MARK_WIDTH * width;
		add(new DePoint(index++, 'c', state, "Play", from, to, true, 0, 0, 0));

		int playedCards = App.deal.countCardsPlayed();

		int reviewCard = App.reviewTrick * 4 + App.reviewCard;

		int tricks = 0;
		int cards = 0;

		for (int i = 0; i < playedCards + 1; i++) {
			tricks = i / 4;
			cards = i % 4;
			if (App.isMode(Aaa.REVIEW_PLAY)) {
				if (i > reviewCard)
					state = 'u';
			}

			if (i == 52) {
				break; // the fin blob is always added at the end so we don't duplicate it here
			}

			from = to;
			to += cardWidth;

			if (cards == 0) {
				if (tricks > 0) {
					to += (spikeWidth - cardWidth);
					boolean spike = true;

					add(new DePoint(index++, 'c', state, "", from, to, spike, 0, tricks - 1, 4));

					from = to;
					to += cardWidth;
				}
				if (state == 'h' && App.isMode(Aaa.REVIEW_PLAY)) {
					if (App.reviewCard == 4 && (tricks - 1 == App.reviewTrick) && !((i == playedCards) && (reviewCard % 4 == 0)))
						state = 'u';
				}

			}

			add(new DePoint(index++, 'c', state, "", from, to, false, 0, tricks, cards));
		}

		if (deal.isFinished() == false)
			return;

		if (tricks > 0 && cards == 0) {
			tricks--;
			cards = 4;
		}

		for (int i = playedCards + 1; i < 52; i++) { // 1 - 52 but 52 is the fin blob
			char stateM = 'm';
			int m_tricks = i / 4;
			int m_cards = i % 4;
			boolean spike = false;
			if (m_tricks > 0 && m_cards == 0) {
				m_tricks--;
				m_cards = 4;
				spike = true;
			}

			from = to;
			to += cardWidth;

			if (spike) {
				to += (spikeWidth - cardWidth);

				add(new DePoint(index++, 'c', stateM, "", from, to, spike, 0, tricks, cards));

				from = to;
				to += cardWidth;
			}

			add(new DePoint(index++, 'c', stateM, "", from, to, false, 0, tricks, cards));
		}

		from = to;
		to += FIN_MARK_WIDTH * width;

		add(new DePoint(index++, 'f', state, "Fin", from, to, true, 0, tricks, cards));

		@SuppressWarnings("unused")
		int z = 0; // put your breakpoint here
	}

	/**
	 */
	public DePoint getLast() {
		// ============================================================================
		return get(size() - 1);
	}

}
