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

import com.rogerpf.aabridge.controller.App;

//@formatter:off


public class Zzz {
	
	private final static String win_EOL = (char) 0x0d + "" + (char) 0x0a;  // 0d = CR   0a = LF
	private final static String mac_EOL = (char) 0x0a + "";

	public final static int Me = 0;
	public final static int Pn = 1;
	
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
	
	public static final String[] playOrd_st = { "-1-", "2nd", "3rd", "4th" };
	
	public final static int[] zN2 =  { 0, 2 };
	public final static int[] zto1 = { 0, 1 }; 
	public final static int[] zto2 = { 0, 1, 2 }; 
	public final static int[] zto3 = { 0, 1, 2, 3 }; 
	
	public final static int[]    scoreRate = {  20,  20,  30,  30,  30 };
	
	
	/**
	 */
	public static String get_lin_EOL() {
		
		if (App.EOLalwaysLF) {
			return Zzz.mac_EOL;
		}
	
		return (App.onWin) ? Zzz.win_EOL : Zzz.mac_EOL;
	}

	
	/**
	 */
	static class BoardData {
		final public int numb;
		final public Dir dealer;
		final public boolean[] vulnerability = new boolean[2];

		BoardData(int n, Dir d, boolean ns, boolean ew) {
			numb = n;
			dealer = d;
			vulnerability[Dir.NS] = ns;
			vulnerability[Dir.EW] = ew;
		};
	}

	/**
	 */
	final static BoardData[] BoardDataTable = { /* ns ew */
		new BoardData( 1, Dir.North, false, false), 
		new BoardData( 2, Dir.East,  true,  false),
		new BoardData( 3, Dir.South, false, true ), 
		new BoardData( 4, Dir.West,  true,  true ),
		new BoardData( 5, Dir.North, true,  false), 
		new BoardData( 6, Dir.East,  false, true ),
		new BoardData( 7, Dir.South, true,  true ), 
		new BoardData( 8, Dir.West,  false, false),
		new BoardData( 9, Dir.North, false, true ), 
		new BoardData(10, Dir.East,  true,  true ),
		new BoardData(11, Dir.South, false, false), 
		new BoardData(12, Dir.West,  true,  false),
		new BoardData(13, Dir.North, true,  true ), 
		new BoardData(14, Dir.East,  false, false),
		new BoardData(15, Dir.South, true,  false), 
		new BoardData(16, Dir.West,  false, true ), 
	};

	public static BoardData getBoardData(int boardNo) {
		return BoardDataTable[(boardNo - 1) % 16];
	};
	
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
