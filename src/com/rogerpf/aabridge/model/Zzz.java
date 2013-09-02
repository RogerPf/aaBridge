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

//@formatter:off


public class Zzz {

	public final static boolean[] falseTrue = { false, true};
	public final static boolean[] trueFalse = { true, false};

	public final static int Me = 0;
	public final static int Pn = 1;

	public final static int North = 0;
	public final static int East = 1;
	public final static int South = 2;
	public final static int West = 3;

	public final static int[] nesw = { North, East, South, West };
	public final static int[] eswn = { East, South, West, North };
	public final static int[] swne = { South, West, North, East };
	public final static int[] wnes = { West, North, East, South };

	public final static int[][] rota = { nesw, eswn, swne, wnes };
	
	public final static int NoSignals = 0;
	public final static int StdEvenCount = 1;
	public final static int UdcOddCount = 2;
	public final static int HighestSignal = 2;

	public final static int MatchAsSelf    = 0;
	public final static int MatchAsPartner = 2;

	public final static int Leader_Pos = 0;
	public final static int Second_Pos = 1;
	public final static int Third_Pos = 2;
	public final static int Fourth_Pos = 3;

	public final static int Clubs = 0;
	public final static int Diamonds = 1;
	public final static int Hearts = 2;
	public final static int Spades = 3;
	public final static int Notrumps = 4;
	
	public static final String[] playOrd_st = { "-1-", "2nd", "3rd", "4th" };
	
	public final static int[] zN2 =  { 0, 2 }; // Like North and South but more general
	public final static int[] zto1 = { 0, 1 }; 
	public final static int[] zto2 = { 0, 1, 2 }; 
	public final static int[] zto3 = { 0, 1, 2, 3 }; // really the same a cdhs BUT used when the items are not in suit order
	public final static int[] cdhs = { Clubs, Diamonds, Hearts, Spades };
	public final static int[] shdc = { Spades, Hearts, Diamonds, Clubs };

	public final static int NULL_BID = 0;
	public final static int PASS = 1;
	public final static int DOUBLE = 2;
	public final static int REDOUBLE = 3;
	
	public final static char[]   compass_to_nesw_ch     = { 'N', 'E', 'S', 'W' };
	public final static String[] compass_to_nesw_st     = { "N", "E", "S", "W" };
	public final static String[] compass_to_nesw_st_long= { "North", "East", "South", "West" };
	public final static String[] compass_to_ns_ew_st    = { "NS", "EW", "NS", "EW" };

	public final static String[] call_to_string         = { "-", "Pass", "Dbl", "ReDbl" };
	public final static String[] call_to_string_short   = { "",  "P", "*", "**" };
	public final static char[]   suit_to_cdhsnCh        = { 'C', 'D', 'H', 'S', 'N' };
	public final static String[] suit_to_cdhsnSt        = { "C", "D", "H", "S", "N" };
	public final static String[] suit_to_cdhsnStLong    = { "Clubs", "Diamonds", "Hearts", "Spades", "No_Trumps" };
	public final static String[] suit_to_cdhsntSt       = { "C", "D", "H", "S", "NU" }; // yes NU (becomes)=> NT via the font
	public final static int[]    scoreRate              = {  20,  20,  30,  30,  30 };

	public final static char[]   rank_to_rankCh         = {'-','-','2','3','4','5','6','7','8','9','T','J','Q','K','A'};
	public final static String[] rank_to_rankSt         = {"-","-","2","3","4","5","6","7","8","9","T","J","Q","K","A"};
	
	public final static char[]   level_to_levelCh       = {'-','1','2','3','4','5','6','7'};
	public final static String[] level_to_levelSt       = {"-","1","2","3","4","5","6","7"};
	public final static int      Ace                    = 14;
	public final static int      King                   = 13;
	public final static int      Queen                  = 12;
	public final static int      Jack                   = 11;
	public final static int      Ten                    = 10;
	public final static int      Nine                   =  9;

	public final static int[]    allThriteenCards       = { 2, 3, 4, 5, 6, 7, 8, 9, Ten, Jack, Queen, King, Ace };
	
	public static final int NS = 0;
	public static final int EW = 1;
	
	public final static int[]    axies  = { NS, EW };
	public final static String[] axis_st = { "NS", "EW"};
	
	/**
	 */
	static class BoardData {
		public int numb;
		public int dealer;
		public boolean vunerability[] = {false, false};

		BoardData(int n, int d, boolean ns, boolean ew) {
			numb = n;
			dealer = d;
			vunerability[NS] = ns;
			vunerability[EW] = ew;
		};
	}

	/**
	 */
	final static BoardData[] BoardDataTable = { /* ns ew */
		new BoardData( 1, North, false, false), 
		new BoardData( 2, East,  true,  false),
		new BoardData( 3, South, false, true ), 
		new BoardData( 4, West,  true,  true ),
		new BoardData( 5, North, true,  false), 
		new BoardData( 6, East,  false, true ),
		new BoardData( 7, South, true,  true ), 
		new BoardData( 8, West,  false, false),
		new BoardData( 9, North, false, true ), 
		new BoardData(10, East,  true,  true ),
		new BoardData(11, South, false, false), 
		new BoardData(12, West,  true,  false),
		new BoardData(13, North, true,  true ), 
		new BoardData(14, East,  false, false),
		new BoardData(15, South, true,  false), 
		new BoardData(16, West,  false, true ), 
	};

	public static BoardData getBoardData(int boardNo) {
		return BoardDataTable[(boardNo - 1) % 16];
	};
	
//	/**
//	 */
//	public final static int rankChToRank_alt_method(char ch) {
//		int i=0;
//		for (i = rank_to_rankCh.length - 1; i >= 2; i--) { // hits will mostly be at the top
//			if (ch == rank_to_rankCh[i])
//				break;
//		}
//		if (2 <= i && i <= Ace)
//			return i;
//		else
//			return 0;
//	}
//

	/**   
	 */
	public static String neswToString(char c) {
		switch (c) {
			case 'N': case 'n': return "North";
			case 'E': case 'e': return "East";
			case 'S': case 's': return "South";
			case 'W': case 'w': return "West";
		}
		return c + "";
	}
	/**   
	 */
	public static int rankChToRank(char c) {

		if (('2' <= c) && (c <= '9')) {
			return c - '0';
		}
		switch (c) {
		case 'T': return Ten;
		case 'J': return Jack;
		case 'Q': return Queen;
		case 'K': return King;
		case 'A': return Ace;
		}
		
		return Ace + 1; // so that others are never "lowest"
	}
	
	public static int convertOutstandingToDepth(int outstanding, boolean secondPlayerFollowedSuit) {

		if (secondPlayerFollowedSuit == false)
			return 0; // as all the cards are AFTER us, we use 0 (zero) to make us to play high
		
		// the second players card * IS * included in the outstanding param value
		
		if (outstanding <= 1)
			return 0;         // So the only outstanding card has just been played
		
		if (outstanding <= 2)
			return 1;
		
		if (outstanding <= 3)
			return 1;
		
		if (outstanding <= 4)
			return 3;
		
		if (outstanding <= 5)
			return 4;
		
		return 5;
	}


}
