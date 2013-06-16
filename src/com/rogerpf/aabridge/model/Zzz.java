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

	public final static int NORTH = 0;
	public final static int EAST = 1;
	public final static int SOUTH = 2;
	public final static int WEST = 3;

	public final static int[] nesw = { NORTH, EAST, SOUTH, WEST };
	public final static int[] eswn = { EAST, SOUTH, WEST, NORTH };
	public final static int[] swne = { SOUTH, WEST, NORTH, EAST };
	public final static int[] wnes = { WEST, NORTH, EAST, SOUTH };

	public final static int[][] rota = { nesw, eswn, swne, wnes };

	public final static int CLUBS = 0;
	public final static int DIAMONDS = 1;
	public final static int HEARTS = 2;
	public final static int SPADES = 3;
	public final static int NOTRUMPS = 4;


	public final static int CMD_SUIT  = 0x0100; //  C D H S
	public final static int CMD_SUITN = 0x0200; //  C D H S N
	public final static int CMD_FACE  = 0x0400; //  2 to 14
	public final static int CMD_LEVEL = 0x0800; //  1 to 7
	public final static int CMD_CALL  = 0x1000; //  Pass Double Redouble

	public final static int[] cdhs = { CLUBS, DIAMONDS, HEARTS, SPADES };
	public final static int[] shdc = { SPADES, HEARTS, DIAMONDS, CLUBS };

	public final static int NULL_BID = 0;
	public final static int PASS = 1;
	public final static int DOUBLE = 2;
	public final static int REDOUBLE = 3;
	
	public final static int UNDO = 'U';

	public final static String[] compass_to_nesw_st     = { "N", "E", "S", "W" };
	public final static String[] compass_to_nesw_st_long= { "North", "East", "South", "West" };
	public final static String[] compass_to_ns_ew_st    = { "NS", "EW", "NS", "EW" };

	public final static String[] call_to_string        = { "-", "Pass", "Dbl", "ReDbl" };
	public final static String[] call_to_string_short  = { "",  "P", "*", "**" };
	public final static char[]   suitValue_to_cdhsnCh  = { 'C', 'D', 'H', 'S', 'N' };
	public final static String[] suitValue_to_cdhsnSt  = { "C", "D", "H", "S", "N" };
	//public final static String[] suitValue_to_cdhsnStLong = { "Clubs", "Diamonds", "Hearts", "Spades", "No_Trumps" };
	public final static String[] suitValue_to_cdhsntSt = { "C", "D", "H", "S", "NU" }; // yes NU (becomes)=> NT via the font
	public final static int[]    scoreRate             = {  20,  20,  30,  30,  30 };


	public final static char[]   faceValue_to_faceCh   = {'-','-','2','3','4','5','6','7','8','9','T','J','Q','K','A'};
	public final static String[] faceValue_to_faceSt   = {"-","-","2","3","4","5","6","7","8","9","T","J","Q","K","A"};
	
	public final static char[]   levelValue_to_levelCh = {'-','1','2','3','4','5','6','7'};
	public final static String[] levelValue_to_levelSt = {"-","1","2","3","4","5","6","7"};
	public final static int      ACE                   = 14;
	public final static int      KING                  = 13;
	public final static int[]    allThriteenCards      = { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, KING, ACE };
	
	public static final int NS = 0;
	public static final int EW = 1;

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
	};

	/**
	 */
	final static BoardData[] BoardDataTable = { /* ns ew */
			new BoardData( 1, NORTH, false, false), 
			new BoardData( 2, EAST,  true,  false),
			new BoardData( 3, SOUTH, false, true ), 
			new BoardData( 4, WEST,  true,  true ),
			new BoardData( 5, NORTH, true,  false), 
			new BoardData( 6, EAST,  false, true ),
			new BoardData( 7, SOUTH, true,  true ), 
			new BoardData( 8, WEST,  false, false),
			new BoardData( 9, NORTH, false, true ), 
			new BoardData(10, EAST,  true,  true ),
			new BoardData(11, SOUTH, false, false), 
			new BoardData(12, WEST,  true,  false),
			new BoardData(13, NORTH, true,  true ), 
			new BoardData(14, EAST,  false, false),
			new BoardData(15, SOUTH, true,  false), 
			new BoardData(16, WEST,  false, true ), };

	public static BoardData getBoardData(int boardNo) {
		return BoardDataTable[(boardNo - 1) % 16];
	};
	
}
