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

public enum Call { // => a Call enum

	NullBid(0), Pass(1), Double(2), ReDouble(3), RealBid(4), Invalid(0); // Invalid is better than null and 0 is the best way to avoid a crash

	public static Call callFromInt(int value) {
		return instAy[value & 0x0f];
	}

	private Call(int v) {
		this.v = v;
	}

	public String toString() {
		return toBidPanelString();
	}

	public String toEarlyContractDisplayString() {
		return call_to_string_short[v];
	}

	public String toBidPanelString() {
		return call_to_bid_panel_string[v];
	}

	public String toCmdString() {
		return call_to_cmd_string[v];
	}

	public String toLinStr() {
		return call_to_lin_string[v];
	}

	//@formatter:off
	private final static String[] call_to_bid_panel_string = { "-", "Pass", "X",   "XX",    "RealBid" };
	private final static String[] call_to_string_short     = { "",  "P",    "*",   "**",    "RealBid" };
	private final static String[] call_to_cmd_string       = { "",  "p",    "*",   "r",     "RealBid" };
	private final static String[] call_to_lin_string       = { "",  "p",    "d",   "r",     "RealBid" };

	public  static Call twoCalls[] = {                Double, ReDouble };
	private static Call instAy[]   = { NullBid, Pass, Double, ReDouble, RealBid };
	//@formatter:on

	public final int v;
}
