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

public enum Level { // => a Call enum

	Invalid(0), One(1), Two(2), Three(3), Four(4), Five(5), Six(6), Seven(7); // Invalid is better than null and 0 is the best way to avoid a crash

	public static Level levelFromInt(int v) {
		if (v < 1 || v > 7)
			v = 1;
		return instAy[v];
	}

	public static Level levelFromChar(char c) {
		return levelFromInt(c - '0');
	}

	public char toChar() {
		return level_to_levelCh[v];
	}

	public String toStr() {
		return level_to_levelStr[v];
	}

	public String toLinStr() {
		return level_to_levelStr[v];
	}

	private Level(int v) {
		this.v = v;
	}

	//@formatter:off
	private final static char[]   level_to_levelCh  =  {'?','1','2','3','4','5','6','7'};
	private final static String[] level_to_levelStr =  {"?","1","2","3","4","5","6","7"};
	private final static Level instAy[] = { Invalid, One, Two, Three, Four, Five, Six, Seven };
	//@formatter:on

	public final int v;
}
