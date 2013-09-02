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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * 
 * Bid
 */
public class Bid implements Serializable {

	private static final long serialVersionUID = 6958033952065344529L;
	public int level;
	public int suit;
	public boolean alert = false; // added in 1398 - 2013
	public transient char suitCh;

	public Bid(int levelV, int suitV) {
		assert (1 <= levelV && levelV <= 7);
		assert (suitV == Zzz.Clubs || suitV == Zzz.Diamonds || suitV == Zzz.Hearts || suitV == Zzz.Spades || suitV == Zzz.Notrumps);
		level = levelV;
		suit = suitV;
		suitCh = (char) Zzz.suit_to_cdhsnCh[suit];
		alert = false;
	}

	public Bid(int c) {
		assert (c == Zzz.NULL_BID || c == Zzz.PASS || c == Zzz.DOUBLE || c == Zzz.REDOUBLE);
		level = c;
		suit = -1;
		alert = false;
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		ObjectInputStream.GetField fields = in.readFields(); // magic

		// field renamed on 2013-July-09
		if (fields.defaulted("level")) {
			level = fields.get("levelValue", (int) 0xdeadbeef);
		}
		else {
			level = fields.get("level", (int) 0xdeadbeef);
		}

		// field renamed on 2013-July-09
		if (fields.defaulted("suit")) {
			suit = fields.get("suitValue", (int) 0xdeadbeef);
		}
		else {
			suit = fields.get("suit", (int) 0xdeadbeef);
		}

		// field added added in 1.0.7.1398 - 2013 August 13
		if (fields.defaulted("alert")) {
			alert = false;
		}
		else {
			alert = fields.get("alert", false);
		}
	}

	public boolean isCall() {
		return (suit == -1);
	}

	// --------------------------------
	public int getCall() {
		return (isCall()) ? level : -1;
	}

	public String getCallSt() {
		return (isCall()) ? Zzz.call_to_string[level] : "?";
	}

	public String getCallStShort() {
		return (isCall()) ? Zzz.call_to_string_short[level] : "?";
	}

	// --------------------------------
	public int getLevel() {
		return (isCall()) ? -1 : level;
	}

	public char getLevelCh() {
		return (isCall()) ? '?' : Zzz.level_to_levelCh[level];
	}

	public String getLevelSt() {
		return (isCall()) ? "?" : Zzz.level_to_levelSt[level];
	}

	// --------------------------------
	public int getSuit() {
		return (isCall()) ? -1 : suit;
	}

	// --------------------------------
	public boolean getAlert() {
		return (isCall()) ? false : alert;
	}

	// --------------------------------
	public void setAlert(boolean halfBidAlert) {
		if (isCall() == false)
			alert = halfBidAlert;
	}

	public char getSuitCh() {
		return (isCall()) ? '?' : Zzz.suit_to_cdhsnCh[suit];
	}

	public String getSuitSt() {
		return (isCall()) ? "?" : Zzz.suit_to_cdhsntSt[suit];
	}

	// --------------------------------
	public String toString() {
		return (isCall() == false) ? getLevelSt() + getSuitSt() : Zzz.call_to_string[level];
	}

	/**
	 */
	public boolean isLowerThanOrEqual(Bid cb) {
		if (isCall())
			return true; // as PASS sometimes be used as the initial
							// compariator
		if (cb.isCall())
			return false; // they should not do this
		if (level > cb.level)
			return false;
		if (level < cb.level)
			return true;
		// equal levels
		if (suit > cb.suit)
			return false;
		if (suit < cb.suit)
			return true;
		return true; // this incomming bid is the same and so invalid hence we
						// return true
	}
}
