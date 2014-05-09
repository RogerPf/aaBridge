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

import java.awt.Color;

public enum Suit {

	Clubs(0), Diamonds(1), Hearts(2), Spades(3), NoTrumps(4), Invalid(0); // Invalid is better than null and 0 is the best way to avoid a crash

	private Suit(int v) {
		this.v = v;
	}

	public final int v;

	public static Suit suitFromInt(int value) {
		return instAy[value & 0x0f];
	}

	public char toChar() {
		return suit_to_cdhsnCh[v];
	}

	public String toLinStr() {
		return suit_to_cdhsnSt[v];
	}

	public String toStr() {
		return suit_to_cdhsnSt[v];
	}

	public String toStrLower() {
		return suit_to_cdhsnLowSt[v];
	}

	public String toStrNu() {
		return suit_to_cdhsntSt[v];
	}

	//@formatter:off
	// suit visibility control constants
	public static final int SVC_noneSet = 0x00;
	public static final int SVC_cards   = 0x01;
	public static final int SVC_count   = 0x02;
	public static final int SVC_dot     = 0x04;
	
	public static final int SVC_ansHere = 0x10;
	public static final int SVC_qaCount = 0x20;
	public static final int SVC_qaDot   = 0x40;

	public final static Suit[] cdhs = { Clubs,  Diamonds, Hearts,   Spades };
	public final static Suit[] shdc = { Spades, Hearts,   Diamonds, Clubs  };

	private final static char[]   suit_to_cdhsnCh     = { 'C', 'D', 'H', 'S', 'N' };
	private final static String[] suit_to_cdhsnLowSt  = { "c", "d", "h", "s", "n" };
	private final static String[] suit_to_cdhsnSt     = { "C", "D", "H", "S", "N" };
	private final static String[] suit_to_cdhsntSt    = { "C", "D", "H", "S", "NU" }; // yes NU (becomes)=> NT via the font

	public  static Suit fiveDenoms[] = { Clubs, Diamonds, Hearts, Spades, NoTrumps};
	private static Suit instAy[]     = { Clubs, Diamonds, Hearts, Spades, NoTrumps, Invalid };
	//@formatter:on

	public Color color(Cc.Ce power) {
		return Cc.SuitColor(this, power);
	}

	public Color colorCd(Cc.Ce power) {
		return Cc.SuitColorCd(this, power);
	}

	public static Suit charToSuit(char c) {
		switch (c) {
		case 'S':
		case 's':
			return Spades;
		case 'H':
		case 'h':
			return Hearts;
		case 'D':
		case 'd':
			return Diamonds;
		case 'C':
		case 'c':
			return Clubs;
		}
		return Suit.Invalid;
	}

	public static Suit charToSuitOrNt(char c) {
		switch (c) {
		case 'S':
		case 's':
			return Spades;
		case 'H':
		case 'h':
			return Hearts;
		case 'D':
		case 'd':
			return Diamonds;
		case 'C':
		case 'c':
			return Clubs;
		case 'N':
		case 'n':
			return NoTrumps;
		}
		return Suit.Invalid;
	}

	public static Suit strToSuit(String s) {
		if (s.length() > 0) {
			return charToSuit(s.charAt(0));
		}
		return Suit.Invalid;
	}

}
