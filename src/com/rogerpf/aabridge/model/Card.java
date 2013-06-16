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
 * Card
 */
public class Card implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1326940816839081697L;
	transient int faceRel;
	final int faceValue;
	final int suitValue;
	transient char suitCh;

	Card(int faceV, int suitV) { /* Constructor */
		faceRel = 0;
		faceValue = faceV;
		suitValue = suitV;
		suitCh = Zzz.suitValue_to_cdhsnCh[suitValue];
	}

	// --------------------------------
	public int getFaceValue() {
		return faceValue;
	}

	public char getFaceCh() {
		return Zzz.faceValue_to_faceCh[faceValue];
	}

	public String getFaceSt() {
		return Zzz.faceValue_to_faceSt[faceValue];
	}

	// --------------------------------
	public int getSuitValue() {
		return suitValue;
	}

	public char getSuitCh() {
		return Zzz.suitValue_to_cdhsnCh[suitValue];
	}

	public String getSuitSt() {
		return Zzz.suitValue_to_cdhsntSt[suitValue];
	}

	public String toString() {
		return getSuitSt() + getFaceSt();
	}

	public boolean matches(int faceValue, int suitValue) {
		return (this.faceValue == faceValue && this.suitValue == suitValue);
	}

	public boolean isBetterThan(Card bestSoFar, int suitTrumps) {
		//@formatter:off
		return  ((suitValue == bestSoFar.suitValue) && (faceValue > bestSoFar.faceValue))
			 || ((suitValue != bestSoFar.suitValue) && (suitValue == suitTrumps)); 
		//@formatter:on
	}

	public boolean isMaster() {
		return faceRel == Zzz.ACE;
	}
}
