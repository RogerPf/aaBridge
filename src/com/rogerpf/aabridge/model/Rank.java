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

import com.rogerpf.aabridge.controller.Aaf;

public enum Rank { // => a Call enum

	//@formatter:off
	Invalid(0), BelowAll(1), Two(2), Three(3), Four(4), Five(5), Six(6), Seven(7), 
	            Eight(8), Nine(9), Ten(10), Jack(11), Queen(12), King(13), Ace(14), PlusDotX(15);
	//@formatter:on

	public static char rankToLanguage(char c, char showAsX /*, boolean useShowAsX */) {
		if (/* useShowAsX && */ showAsX != '-') {
			if (showAsX == 'x') {
				c = 'x';
			}
			else if (showAsX == '*') {   // * asterisk = hide blot out
				c = ' ';
			}
			else {
				int xv = 0;
				if ('0' <= showAsX && showAsX <= '9')
					xv = showAsX - '0';
				else {
					switch (showAsX) {
					default:
						xv = 14;
						break; // Ace x etc
					case 'k':
						xv = 13;
						break;
					case 'q':
						xv = 12;
						break;
					case 'j':
						xv = 11;
						break;
					case 't':
						xv = 10;
						break;
					}
				}
				int cv = 0;
				if (('2' <= c) && (c <= '9'))
					cv = c - '0';
				else {
					switch (c) {
					default:
						cv = 14;
						break; // Ace x etc
					case 'K':
						cv = 13;
						break;
					case 'Q':
						cv = 12;
						break;
					case 'J':
						cv = 11;
						break;
					case 'T':
						cv = 10;
						break;
					}
				}
				if (xv >= cv) {
					c = 'x';
				}
			}
		}

		return Aaf.rankList.cardLetterLangConvert(c);
	}

	public static Rank rankFromInt(int value) {
//		if ((Two.v <= value && value <= Ace.v) == false) {
//			@SuppressWarnings("unused")
//			int z = 0; // put your breakpoint here
//		}		
		assert (Two.v <= value && value <= Ace.v);
		return instAy[value];
	}

	public char toChar() {
		return rank_to_rankCh[v];
	}

	public String toStr() {
		return rank_to_rankStr[v];
	}

	/**   
	 */
	public static Rank charToRank(char c) {

		if (('2' <= c) && (c <= '9')) {
			return instAy[c - '0'];
		}
		switch (c) {
		case 'T':
		case 't':
			return Ten;
		case 'J':
		case 'j':
			return Jack;
		case 'Q':
		case 'q':
			return Queen;
		case 'K':
		case 'k':
			return King;
		case 'A':
		case 'a':
			return Ace;
		}
		return Invalid;
	}

	/**   
	 */
	public static Rank charToRank_StarDotPlus(char c) {

		if (('2' <= c) && (c <= '9')) {
			return instAy[c - '0'];
		}
		switch (c) {
		case 'T':
			return Ten;
		case 'J':
			return Jack;
		case 'Q':
			return Queen;
		case 'K':
			return King;
		case 'A':
			return Ace;
		case '+':
			return PlusDotX;
		case '.':
			return PlusDotX;
		case 'x':
			return PlusDotX;
		}
		return Invalid;
	}

	//@formatter:off
	public  final static Rank    allThriteenRanks[] = { Two, Three, Four, Five, Six, Seven, Eight, Nine, Ten, Jack, Queen, King, Ace };
	private final static Rank    instAy[]           = { Invalid, BelowAll, Two, Three, Four, Five, Six, Seven, Eight, Nine, Ten, Jack, Queen, King, Ace, PlusDotX};
	private final static char   rank_to_rankCh[]   = {'?','-','2','3','4','5','6','7','8','9','T','J','Q','K','A','+'};
	private final static String rank_to_rankStr[]  = {"?","-","2","3","4","5","6","7","8","9","T","J","Q","K","A","+"};
	//@formatter:on

	private Rank(int v) {
		this.v = v;
	}

	public final int v;
}
