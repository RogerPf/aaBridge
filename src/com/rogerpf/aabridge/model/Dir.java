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
import com.rogerpf.aabridge.controller.LangdeckList;

public enum Dir {

	North(0), East(1), South(2), West(3), Invalid(0); // Invalid is better than null and 0 should be safe

	public Dir rotate(int plusIsClockwise) {
		return dirFromInt(this.v + plusIsClockwise);
	}

	public Dir rotate(Dir alwayClockwise) {
		return dirFromInt(this.v + alwayClockwise.v);
	}

	public Dir rotate180() {
		return dirFromInt(this.v + 2);
	}

	public Dir nextClockwise() {
		return dirFromInt(this.v + 1);
	}

	public Dir prevAntiClockwise() {
		return dirFromInt(this.v - 1);
	}

	public static Dir dirFromInt(int posClockwise) {
		return ceAy[(posClockwise + 64) % 4];
	}

	public String pbnChar() {
		if (this == Invalid)
			return "N";
		else
			return name().substring(0, 1);
	}

	/**   
	 */
	public static Dir directionFromChar(char c) {
		switch (c) {
		case 'N':
		case 'n':
			return Dir.North;
		case 'E':
		case 'e':
			return Dir.East;
		case 'S':
		case 's':
			return Dir.South;
		case 'W':
		case 'w':
			return Dir.West;
		}
		return Dir.Invalid;
	}

	/**   
	 */
	public static Dir directionFromInt(int i) {
		switch ((i + 4) % 4) {
		case 0:
			return Dir.North;
		case 1:
			return Dir.East;
		case 2:
			return Dir.South;
		case 3:
			return Dir.West;
		}
		return Dir.South;
	}

	/**   
	 */
	public static String neswToString(char c) {
		switch (c) {
		case 'N':
		case 'n':
			return "North";
		case 'E':
		case 'e':
			return "East";
		case 'S':
		case 's':
			return "South";
		case 'W':
		case 'w':
			return "West";
		}
		return c + "";
	}

	public char toChar() {
		return compass_to_nesw_ch[v];
	}

	public char toLowerChar() {
		return compass_to_nesw_lower_ch[v];
	}

	public String toStr() {
		return compass_to_nesw_str[v];
	}

	public String toLongStr() {
		return compass_to_nesw_str_long[v];
	}

	public String toOpenResp() {
		return compass_to_open_resp_str[v];
	}

	public String toAxisStr() {
		return compass_to_ns_ew_str[v];
	}

	public static String axisStr(int val) {
		return compass_to_ns_ew_str[val];
	}

	public static final int NS = 0;
	public static final int EW = 1;
	public final static int[] axies = { NS, EW };

	//@formatter:off
	private final static char[]  compass_to_nesw_ch        = { 'N', 'E', 'S', 'W' };
	private final static char[]  compass_to_nesw_lower_ch  = { 'n', 'e', 's', 'w' };
	public  final static String[] compass_to_nesw_str      = { "N", "E", "S", "W" };
	private final static String[] compass_to_nesw_str_long = { "North", "East", "South", "West" };
	private final static String[] compass_to_open_resp_str = { "", "Resp..", "", "Open.."};
	private final static String[] compass_to_ns_ew_str     = { "NS", "EW", "NS", "EW" };

	public final static Dir nesw[] = { North, East,  South, West  };
	public final static Dir eswn[] = { East,  South, West,  North };
	public final static Dir swne[] = { South, West,  North, East  };
	public final static Dir wnes[] = { West,  North, East,  South };
	
	public final static Dir rota[][] = { nesw, eswn, swne, wnes };
	//@formatter:off

	private Dir(int v) {
		this.v = v;
	}

	private static Dir ceAy[] = { North, East, South, West };

	public final int v;
	
	public static char langDirChar[] = { 'N', 'E', 'S', 'W' };
	
	public static void initLangDirNSEW(String s) { // in-comming order is NSEW
		s = s.trim();
		if (s.length() >= 4) {
			langDirChar[Dir.North.v] = s.charAt(0);
			langDirChar[Dir.East.v] = s.charAt(2);  // east
			langDirChar[Dir.South.v] = s.charAt(1); // south
			langDirChar[Dir.West.v] = s.charAt(3);
		}
	}
	
	public static char getLangDirChar(Dir dir) {
		if (LangdeckList.isDeckOverridden())
			return compass_to_nesw_ch[dir.v]; // we use English direction letters if the language is over-ridden
		else
			return langDirChar[dir.v];

	}
	
	public static String[] langDirNESWNames = { "North", "South", "East", "West" };
	
	public static void initLangDirNSEW(String n, String s, String e, String w) {
		langDirNESWNames[Dir.North.v] = n;
		langDirNESWNames[Dir.South.v] = s;
		langDirNESWNames[Dir.East.v] = e;
		langDirNESWNames[Dir.West.v] = w;
	}
	
	public static String getLangDirStr(Dir dir) {
		if (Aaf.iso_deck_lang.contentEquals("default") == false)
			return compass_to_nesw_str_long[dir.v]; // if the deck language is overridden we use English direction words 
		else
			return langDirNESWNames[dir.v];
	}
}
