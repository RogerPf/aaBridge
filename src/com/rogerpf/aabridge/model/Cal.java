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
import java.util.ArrayList;

/**
 * 
 * 
 */
public class Cal extends ArrayList<Card> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3704044939801788494L;

	Cal() { /* Constructor */
		super();
	}

	Cal(int n) { /* Constructor */
		super(n);
	}

	public Card getIfFaceExists(int faceV) {
		for (Card card : this) {
			if (card.faceValue == faceV) {
				return card;
			}
		}
		return null;
	}

	public Card getLast() {
		if (size() == 0)
			return null;
		else
			return get(size() - 1);
	}

	public Card removeLast() {
		if (size() == 0)
			return null;
		else
			return remove(size() - 1);
	}

	/** 
	 */
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		for (Card c : this) {
			sb.append(c.getFaceCh());
			sb.append(' ');
		}
		return sb.toString();
	}

	/** 
	 */
	public String toScrnStr() {
		final StringBuilder sb = new StringBuilder();

		for (Card c : this) {
			sb.append(c.getFaceCh());
		}

		return sb.toString();
	}

	/** 
	 */
	public Card getCard(int index) {
		if (index >= size())
			return null;
		return get(index);
	}

	/** 
	 */
	public Card getCardOrLowest(int index) {
		if (index >= size())
			index = size() - 1;
		if (index < 0)
			return null;

		return get(index);
	}

	/** 
	 */
	public Card removeCard(int index) {
		return remove(index);
	}

	/** 
	 */
	public void addPlayedCard(Card card) {
		add(card);
	}

	/** 
	 */
	public void addDeltCard(Card card) {
		if (size() > 0) {
			for (int i = 0; i < size(); i++) {
				if (get(i).faceValue < card.faceValue) {
					add(i, card);
					return;
				}
			}
		}
		add(card);
	}

	/** 
	 */
	public int countPoints() {
		int v = 0;
		for (Card card : this) {
			v += (card.faceValue > 10) ? card.faceValue - 10 : 0;
		}
		return v;
	}

	/** 
	 */
	public int countSuit(int suitV) {
		int c = 0;
		for (Card card : this) {
			if (card.suitValue == suitV)
				c++;
		}
		return c;
	}
}
