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
package com.rogerpf.aabridge.controller;

import java.util.ArrayList;

public class q_ {

	public static ArrayList<Integer> all = new ArrayList<Integer>(200);

	public static int q(String s) /* q => quick */{
		int v = (s.charAt(0) << 8) + (int) s.charAt(1);
		all.add(v);
		return v;
	}

	public static boolean isQtKnown(char c0, char c1) {
		// slow but who cares - cos fast enough
		int qt = (c0 << 8) + (int) c1;
		for (Integer i : all) {
			if (i == qt)
				return true;
		}
		return false;
	}

	public final static int pc = q("pc");
	public final static int at = q("at");
	public final static int pg = q("pg");
	public final static int mb = q("mb");
	public final static int nt = q("nt");

	public final static int aa = q("aa"); // aaBridge only auto add
	public final static int cr = q("cr");
	public final static int cg = q("cg");
	public final static int cb = q("cb");
	public final static int fh = q("fh");
	public final static int ff = q("ff");
	public final static int fb = q("fb");
	public final static int fi = q("fi");
	public final static int fu = q("fu");
	public final static int fm = q("fm");

	public final static int fp = q("fp");
	public final static int cp = q("cp");
	public final static int cq = q("cq");
	public final static int cs = q("cs");

	public final static int ht = q("ht");
	public final static int hT = q("hT"); // internal - like ht but preserves the xcol position
	public final static int qx = q("qx");
	public final static int tu = q("tu");

	public final static int bt = q("bt");
	public final static int st = q("st");
	public final static int pa = q("pa");

	public final static int mn = q("mn");

	public final static int pn = q("pn");
	public final static int sj = q("sj"); // seat jump aaBridge only
	public final static int sk = q("sk"); // seat kibitz

	public final static int ZS = q("@S"); // @ => Z

	public final static int Zd = q("@-"); // @ => Z
	public final static int Zo = q("@."); // @ => Z
	public final static int NL = q("NL");
	public final static int CB = q("CB");
	public final static int CE = q("CE");
	public final static int XB = q("XB");
	public final static int XE = q("XE");
	public final static int YB = q("YB");
	public final static int YE = q("YE");
	public final static int Z4 = q("@4"); // @ => Z

	public final static int VT = q("VT");
	public final static int ZN = q("@N"); // @ => Z, where n is 0 - 3

	public final static int md = q("md");
	public final static int rc = q("rc"); // aaBridge only Remove Cards
	public final static int vg = q("vg");
	public final static int rh = q("rh");
	public final static int ah = q("ah");

	public final static int rs = q("rs");
	public final static int mc = q("mc");
	public final static int ub = q("ub");

	public final static int up = q("up");

	public final static int sv = q("sv");
	public final static int kp = q("kp");
	public final static int an = q("an");
	public final static int mp = q("mp");

	public final static int hf = q("hf");
	public final static int lb = q("lb");
	public final static int wt = q("wt");
	public final static int sb = q("sb");
	public final static int ha = q("ha");
	public final static int ih = q("ih");
	public final static int ia = q("ia");

	public final static int nU = q("n^"); // aaBridge only nudge Up added 2814
	public final static int nD = q("n#"); // aaBridge only nudge Down "
	public final static int nL = q("n<"); // aaBridge only nudge Left "
	public final static int nR = q("n>"); // aaBridge only nudge Right "
	public final static int lg = q("lg"); // line gap (line separation) "

	public final static int rq = q("rq"); // aaBridge only Require Build No
	public final static int eb = q("eb"); // aaBridge only 'Enter the deal' Blocker
	public final static int tc = q("tc"); // BBO system to hide show card display updates - so you can jump forward
	public final static int gf = q("gf"); // grey fade y Y = Yes other = no // ignored since 2814

	public final static int xx = q("xx"); // aaBridge only used to 'kill' (turn off) an existing command

	public final static int va = q("va"); // BBO (not supported) Vertical Adjust - we have 4 nudges
	public final static int sa = q("sa"); // BBO (not supported)
	public final static int sc = q("sc"); // BBO (not supported) Show (Played) Cards
	public final static int pf = q("pf"); // Allow the user to enter the deal - not needed can always do so
	public final static int d3 = q("3d"); // too ugly (to 1990's) to implement
	public final static int hc = q("hc"); // highlight card - not considered a worthwhile feature
	public final static int lc = q("lc"); // low light card - not considered a worthwhile feature
	public final static int hs = q("hs"); // highlight suit - not considered a worthwhile feature
	public final static int ls = q("ls"); // low light suit - not considered a worthwhile feature
	public final static int pw = q("pw"); // player wide ? appears to be a type of PN (player names) currently ignored
	public final static int bn = q("bn"); // VuGraph - Board Number - got by other means
	public final static int bg = q("bg"); // Background color - ignored - WE choose the background color
	public final static int sr = q("sr"); // unknown takes a 'y'
	public final static int se = q("se"); // Sound External - plays and external sound - not supported
	public final static int bm = q("bm"); // Bitmap image - - not supported
	public final static int wb = q("wb"); // bitmap positioning x
	public final static int hb = q("hb"); // bitmap positioning y
	public final static int ip = q("ip"); // bitmap ??? howard ???
	public final static int lf = q("lf"); // part of the (unused) BBO link system
	public final static int tb = q("tb"); // BBO ???
	public final static int by = q("by"); // BBO ???
	public final static int sd = q("sd"); // BBO ???
	public final static int dn = q("dn"); // BBO ???
}
