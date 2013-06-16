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
 * Bid
 */
public class Bid implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6958033952065344529L;
	public final int levelValue;
	final int suitValue;
	transient char suitCh;

	public Bid(int levelV, int suitV) {
		assert (1 <= levelV && levelV <= 7);
		assert (suitV == Zzz.CLUBS || suitV == Zzz.DIAMONDS || suitV == Zzz.HEARTS || suitV == Zzz.SPADES || suitV == Zzz.NOTRUMPS);
		levelValue = levelV;
		suitValue = suitV;
		suitCh = (char) Zzz.suitValue_to_cdhsnCh[suitValue];
	}

	public Bid(int c) {
		assert (c == Zzz.NULL_BID || c == Zzz.PASS || c == Zzz.DOUBLE || c == Zzz.REDOUBLE);
		levelValue = c;
		suitValue = -1;
	}

	public boolean isCall() {
		return (suitValue == -1);
	}

	// --------------------------------
	public int getCallValue() {
		return (isCall()) ? levelValue : -1;
	}

	// public char getCallCh() {
	// return (isCall()) ? Zzz.callValue_to_char[suitValue] : '?';
	// }
	public String getCallSt() {
		return (isCall()) ? Zzz.call_to_string[levelValue] : "?";
	}

	public String getCallStShort() {
		return (isCall()) ? Zzz.call_to_string_short[levelValue] : "?";
	}

	// --------------------------------
	public int getLevelValue() {
		return (isCall()) ? -1 : levelValue;
	}

	public char getLevelCh() {
		return (isCall()) ? '?' : Zzz.levelValue_to_levelCh[levelValue];
	}

	public String getLevelSt() {
		return (isCall()) ? "?" : Zzz.levelValue_to_levelSt[levelValue];
	}

	// --------------------------------
	public int getSuitValue() {
		return (isCall()) ? -1 : suitValue;
	}

	public char getSuitCh() {
		return (isCall()) ? '?' : Zzz.suitValue_to_cdhsnCh[suitValue];
	}

	public String getSuitSt() {
		return (isCall()) ? "?" : Zzz.suitValue_to_cdhsntSt[suitValue];
	}

	// --------------------------------
	public String toString() {
		return (isCall() == false) ? getLevelSt() + getSuitSt() : Zzz.call_to_string[levelValue];
	}

	/**
	 */
	public boolean isLowerThanOrEqual(Bid cb) {
		if (isCall())
			return true; // as PASS sometimes be used as the initial
							// compariator
		if (cb.isCall())
			return false; // they should not do this
		if (levelValue > cb.levelValue)
			return false;
		if (levelValue < cb.levelValue)
			return true;
		// equal levels
		if (suitValue > cb.suitValue)
			return false;
		if (suitValue < cb.suitValue)
			return true;
		return true; // this incomming bid is the same and so invalid hence we
						// return true
	}

}
