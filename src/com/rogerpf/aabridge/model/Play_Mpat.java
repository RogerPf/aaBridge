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

public class Play_Mpat {

	/**
	 * This is a search for 'well known' patterns in the visible cards of a single suit.
	 * The stored patterns are created with the leader in column zero. (North is also defined as zero)
	 * So as to most easily match the incomming pattern, the 'real' values are also always presented 
	 * with the North in the zero column.  
	 */

	static int entryId_static = 0; // debug time assist - so we know where we are in the table roughly
	final static String Equ = "Equ";
	final static String Rel = "Rel";
	final static String Skp = "Skp"; // so the tablle means Rels start on a new 'ten' count

	/**
	 */
	static class Mpat {

		// ---------------------------------- CLASS -------------------------------------
		/**
		 */
		int entryId = 0;
		String type = new String();
		String ma[] = new String[4];
		int ma_len[] = new int[4];
		int unused = 0;
		int ma_pes[] = new int[4];
		int ma_xes[] = new int[4];
		boolean ma_hasDot[] = new boolean[4];

		String mb[] = new String[4];
		String pd[] = new String[4];
		String pl[] = new String[4];
		int x_means_below = -1; // overwritten by a higher value later

		String seeTests;

// @formatter:off

final static Mpat[][] knownCases = { 
{
	new Mpat( Rel,  "Q+.",    "KJx.",   "A.",     ".",     "Q", "KJx",  "",    0,   "Q", "K", "Ax",   "",   "2034" ),
	
},{
//					 -1-       2nd       3rd      4th   What did they play?        Should play (in response as appropriate)
//                                                       actual  play               (should)   resp
	new Mpat( Equ,  "xxx.",   "KJ9.",   "AQT8.",  ".",     "x", "KJ9x", "",    0,   "x", "x", "AQT8", "",   "2001-4" ),
	new Mpat( Equ,  "Qxx.",   "KJJ.",   "AQQ+.",  ".",     "Q", "KJx",  "",    0,   "Q", "K", "AQx",  "",   "2001-4" ),
	new Mpat( Equ,  "Qxx.",   "KJJ.",   "AQQ+.",  ".",     "x", "KJx",  "",    0,   "Q", "x", "AQQ",  "",   "2001-4" ),
	new Mpat( Equ,  "++.",    "KJ.",    "AQT.",   ".",     "+", "KJx",  "",    0,   "+", "x", "AQT",  "",   "2005-7" ),
	
	new Mpat( Equ,  "QT.",    "KJ.",    "AQT.",   ".",     "+", "KJx",  "",    0,   "+", "x", "AQT",  "",   "2005-7" ),
	new Mpat( Equ,  "Q+.",    "KJx.",   "A.",     ".",     "x", "KJx",  "",    0,   "Q", "K", "Ax",   "",   "2034" ),
	new Mpat( Equ,  "Q+.",    "KJx.",   "A.",     ".",     "x", "KJx",  "",    0,   "x", "x", "Ax",   "",   "2034" ),
	new Mpat( Equ,  "QQx.",   "K.",     "AQxx.",  ".",     "Q", "Kx",   "",    0,   "Q", "x", "Ax",   "",   "2041" ),
	new Mpat( Equ,  "QQx.",   "K.",     "AQxx.",  ".",     "x", "Kx",   "",    0,   "x", "x", "AQ",   "",   "2041" ),

	new Mpat( Equ,  "QQx.",   "K.",     "Axx.",   ".",     "Q", "Kx",   "",    0,   "Q", "x", "Ax",   "",   "2042" ),
	new Mpat( Equ,  "QQx.",   "K.",     "Axx.",   ".",     "x", "Kx",   "",    0,   "Q", "x", "Ax",   "",   "2042" ),
	new Mpat( Equ,  "Q.",     "Kx",     "AAx.",   ".",     "Q", "Kx",   "",    0,   "Q", "K", "Ax",   "",   "2062" ),
	new Mpat( Equ,  "Q+.",    "Kx.",    "AQ+.",   ".",     "x", "Kx",   "",    0,   "Q", "x", "Ax",   "",   "2062" ),
	new Mpat( Equ,  "Q+.",    "KJ.",    "AQ+.",   ".",     "Q", "Kx",   "",    0,   "Q", "K", "Ax",   "",   "2062" ),

	new Mpat( Equ,  "Q+.",    "Kx.",    "AQ+.",   ".",     "Q", "Kx",   "",    0,   "Q", "x", "Ax",   "",   "2062" ),
	new Mpat( Equ,  "Qx.",    "K.",     "AQ.",    ".",     "Q", "Kx",   "",    0,   "Q", "K", "Ax",   "",   "2062" ),
	new Mpat( Equ,  "x.",     "Kx.",    "AQQ.",   ".",     "x", "Kx",   "",    0,   "x", "x", "AQ",   "",   "2062" ),
	new Mpat( Equ,  "x.",     "K.",     "AQ.",    ".",     "x", "Kx",   "",    0,   "x", "x", "AQ",   "",   "2008-9" ),
	new Mpat( Equ,  "+.",     "KK.",    "AAQ.",   ".",     "x", "Kx",   "",    0,   "x", "x", "AQ",   "",   "2034" ),
	
	new Mpat( Equ,  "Q.",     "KJ.",    "AAx.",   ".",     "Q", "Kx",   "",    0,   "Q", "K", "Ax",   "",   "2034" ),
	new Mpat( Equ,  "Q.",     "KK.",    "AQ.",    ".",     "Q", "Kx",   "",    0,   "Q", "K", "Ax",   "",   "2034" ),

}};

// @formatter:on

		boolean checkAgainst(Mpat real, Gather g, int matchPosition, int matchAs) {
			// ==============================================================================================
			// assumes that the cards for the current trick have been returned to the hand !
			assert (type == real.type);
			g.mpatRtn.clear();

			Dir callerCompass = g.hand.compass;
			int trickNumb = g.trickNumb;
			Dir declarerCompass = g.declarerCompass;
			Dir dummyCompass = declarerCompass.rotate180();
			int callerPositionInTrick = g.positionInTrick;
			int declarer_validOnlyWhenDummy = (callerCompass == dummyCompass) ? declarerCompass.v : -999 /* i.e. never */;

			/**
			 * The caller wants to see if there is a match with a given seat
			 * with any of the 'positions' in the table above.
			 * Both the table and incomming entry (real) are arranged with the
			 * leader in the first column (column index = 0) i.e. the 'logical north' 
			 * SO the (real) entry rotated by leaderCompass to line up with the 
			 * actual positions of the players.
			 * 
			 * Assuming we are at seat X (nesw) AND we are interested in matching
			 * with the leader position (0 zero pos) in the main ma table then 
			 * our entry will appear in the X'th poistion in the four (main) ma entries
			 * and in the zeroth position in the ma_real table  (indexed by 'j')
			 * Therefore inorder to keep the index (j) into the ma_real values
			 * in step we need to have 'j' running X steps ahead of 'i'.
			 * we are interested in. 
			 * 
			 *  So while 'i' runs from   0 to 3   j runs from  (0 + X)  to  (3 + X)
			 *  
			 *   j = i + callerCompass               (assume 'clock' arithmatic)
			 *   
			 *   remember that the callerCompass relates to the CURRENT trick 
			 *   which is often NOT the trick pattern being matched
			 * 
			 */

			int j_offset = ((+callerCompass.v - matchPosition + matchAs) + 16) % 4;

			int brk = 0;
//			if (entryId == 2) // from 1 change the constant entryId to match the target
			if (trickNumb == 5) // from 0 (zero)
				if (callerPositionInTrick == 1) // from 0 (zero) so 2 => 3rd
					if (matchPosition == 1) // from 0 (zero)
						if (matchAs == 0) // from 0 (zero), zero is self
							if (g.debug_suit == Suit.suitFromInt(2))
								brk++; // put your breakpoint here :) - Yes this hang is corrent

			for (int i : Zzz.zto3) {
				int j = ((i + j_offset) + 4) % 4; // j is the index into the "real" match info (ma)

				// There the only two seats we (the caller) can in truth see.
				if (!((j == callerCompass.v) || (j == dummyCompass.v) || (j == declarer_validOnlyWhenDummy)))
					continue;

				if (brk > 0)
					brk++; // put your breakpoint here

				if (real.ma[j].startsWith(ma[i]) == false)
					return false;

				// 'x'es and '+'es are manadatory

				if (ma_pes[i] > 0) { // we Need to check that the '+' matches are not to high and sufficent
					if (real.ma_len[j] < ma_len[i] + ma_pes[i])
						return false;
					// note the difference see below, here '+' is alowed to be one higher than 'x'
					if (Rank.charToRank_StarDotPlus(real.ma[j].charAt(ma_len[i] /* i.e. one after */)).v > x_means_below)
						return false;
				}

				if (ma_xes[i] > 0) { // we Need to check that the 'x' matches are not to high and sufficent
					if (real.ma_len[j] < ma_len[i] + ma_pes[i] + ma_xes[i])
						return false;
					if (Rank.charToRank_StarDotPlus(real.ma[j].charAt(ma_len[i] + ma_pes[i] /* i.e. one after */)).v >= x_means_below)
						return false;
				}

				if (ma_hasDot[i] == false) { // no dot - lengths must match
					if (real.ma_len[j] != ma_len[i] + ma_pes[i] + ma_xes[i])
						return false;
				}
				else {
					// there is a dot and we need to see that the 'x' match is not too high are not to high and sufficent
					if (real.ma_len[j] > ma_len[i] + ma_pes[i] + ma_xes[i]) {
						// a dot counts as 'x' not '+'
						if (Rank.charToRank_StarDotPlus(real.ma[j].charAt(ma_len[i] + ma_pes[i] + ma_xes[i] /* i.e. one after */)).v > x_means_below)
							return false;
					}
				}
			}

			if (brk > 0)
				brk++; // put your breakpoint here

			/**
			 *  if the request is to match more than just the lead Position we need to 
			 *  check that we have a match on the played cards.   pd are played, pl are TO BE played
			 */
			boolean pdMatch = true; // true or dont care
			int k = 0;
			int i = 0;
			boolean isWhatToPlayWanted = (callerPositionInTrick == matchPosition && matchAs == Zzz.MatchAsSelf);

			if (isWhatToPlayWanted) {

				int maxDepth = g.finMaDepth;

				for (i = 0; i < callerPositionInTrick; i++) {

					// j is the index into the "real" match info (ma)
					int j = (i + j_offset + 4) % 4;

					if (brk > 0)
						brk++; // put your breakpoint here

					if (real.pd[j].length() != 1) {
						brk++; // put your breakpoint here
						System.out.println("ERR === > Mpat 'checkAgaints' Real played missing   j = " + j + "   " + real);
					}
					assert (real.pd[j].length() == 1);

					char r_pd = real.pd[j].charAt(0);
					int r_pd_rank = Rank.charToRank_StarDotPlus(r_pd).v;
					int len = pd[i].length();
					if (r_pd == '*') {
						// they did not follow suit - so treat as a match of the lowest
						k = (maxDepth <= len - 1) ? maxDepth : len - 1;
					}
					else {
						for (k = 0; k < len; k++) {

							if (k == maxDepth)
								break; // success - the caller wants us to treat this as a match at this level

							char c_pd = pd[i].charAt(k);
							if (r_pd == c_pd)
								break; // success - the real pd == candidate pd

							if (c_pd == 'x') {
								if (r_pd_rank >= x_means_below)
									continue;
								break; // success - the real pd == candidate pd
							}

							if (c_pd == '+') {
								if (r_pd_rank > x_means_below) // <=== DIFF see above '+' is alowed to be one higher than 'x'
									continue;
								break; // success - the real pd == candidate pd
							}
						}
					}

					if (k >= len) {
						if (brk > 0)
							brk++; // put your breakpoint here
						pdMatch = false;
						break;
					}

					// so - we go arround again to try to match the next REAL played card with values in the next pd entry
				}
			}

			char plChar = 0;
			// we only report the rank to be played if the caller is in the seat asked about
			if (pdMatch && isWhatToPlayWanted && (pl[matchPosition].length() > k)) { // note dpMatch = true is also dont care
				plChar = pl[matchPosition].charAt(k);
			}

			// Success we have (some sort of a) a match
//			{
//				String s = (g.deal.testId > 0) ? "test_" + g.deal.testId + " " : "         ";
//				s += (pdMatch ? " " : "X") + " ";
//				s += "Tk " + g.trickNumb;
//				s += "  Pos " + Zzz.playOrd_st[g.positionInTrick] + " " + g.hand.compass.toStr();
//				s += "  mPos " + Zzz.playOrd_st[matchPosition];
//				// s += "  Lead " + g.leader.compass.toStr();
//				s += "  " + this + "   " + g.debug_suit.toString() + (plChar == 0 ? "" : (" " + plChar));
//				System.out.println(s);
//			}

			if (brk > 0)
				brk++; // put your breakpoint here

			// we only report the rank to be played if the caller is in the seat asked about
			if (isWhatToPlayWanted) {
				if (pdMatch == false || (pl[matchPosition].length() <= k)) {
//					if (i > 0) {
//						System.out.println("ERROR ====> Mpat " + ((g.deal.testId > 0) ? "test_" + g.deal.testId : " ") + " matchPosition " + g.positionInTrick
//								+ " missing pl[] entry 'what to play' -  Mpat " + this);
//					}
					return false;
				}

				Rank rankXxx = Rank.charToRank_StarDotPlus(plChar);
				if (type == Rel)
					g.mpatRtn.rankRel = rankXxx;
				else
					g.mpatRtn.rankEqu = rankXxx;
			}

			g.mpatRtn.matchEntryId = entryId;
			g.mpatRtn.rating = unused;

			return true;
		}

		/**
		 *  the main   CONSTRUCTOR   for table Mpats
		 */
		// @formatter:off
		Mpat(   String type,
				String m0, String m1, String m2, String m3,
				String d0, String d1, String d2,
				int unused,
				String p0, String p1, String p2, String p3,
				String seeTests) { // -------------------- constructor ----------------------------
			
		// @formatter:off		
			// ==============================================================================================
			entryId = ++entryId_static; // ++ first so we start from 1;
			this.type = type;
			mb[0] = m0;
			mb[1] = m1;
			mb[2] = m2;
			mb[3] = m3;
	
			pd[0] = d0;
			pd[1] = d1;
			pd[2] = d2;
			pd[3] = "";
			
//			unused = leadScore;
			
			pl[0] = p0;
			pl[1] = p1;
			pl[2] = p2;
			pl[3] = p3;
			
			this.seeTests = seeTests;
			
			x_means_below = Rank.Ace.v;
			for (int i : Zzz.zto3 ) {
				for (int j=0; j < mb[i].length(); j++) {
					int rnk = Rank.charToRank_StarDotPlus(mb[i].charAt(j)).v;
					if (x_means_below > rnk) {
						x_means_below = rnk;
					}
				}
			}
			
			for (int i : Zzz.zto3) {
				String m = mb[i];
				int m_len = m.length();

				int posD = m.indexOf('.');
				if (posD > -1) {
					ma_hasDot[i] = true;
					m_len = posD;
				} 
				
				int posX = m.indexOf('x');
				if (posX > -1) {
					ma_xes[i] =  m_len - posX;
					m_len = posX;
				} 
				
				int posP = m.indexOf('+');
				if (posP > -1) {
					ma_pes[i] = m_len - posP;
					m_len = posP;
				} 

				ma[i] = m.substring(0, m_len);
				ma_len[i] = m_len;
			}

			@SuppressWarnings("unused")
			int z = 0; // put your breakpoint here
		};
		
		/**
		 * 
		 * The main constructor for   REAL  Mpates
		 */
		Mpat(String type, Deal deal, Suit suit) { // constructor 
			// ==============================================================================================
			// assumes that the cards for the current trick have been returned to the hand !
			this.type = type;
			
			for (Dir p : Dir.nesw ) {  // for *this suit* each hand get its own 'ma' column
				int i = p.v;
				Hand hand = deal.hands[i];
				Frag frag = hand.frags[suit.v];
				ma[i] = (type == Rel) ? frag.relFaceSt() : frag.equFaceSt();
				ma_len[i] = ma[i].length();
				
				pd[i] = "";
				if (hand.played.size() == deal.prevTrickWinner.size()) {
					Card card = hand.played.getLast();
					if (card.suit == suit)
						pd[i] = "" + ((type == Rel) ? card.rankRel.toChar() : card.rankEqu.toChar());
					else 
						pd[i] = "*"; // i.e.  means did not follow suit
				}
			}
		}
		
		// @formatter:off
		public String toString() {
			// ==============================================================================================
			return " Mpat " + type + " " + entryId + "    " + mb[0] + "  " + mb[1] + "  " + mb[2] + "  " + mb[3]
					                + "        " + pd[0] + " "  + pd[1] + " "  + pd[2] 
					                + "  " + unused + "  "
					                + "        " + pl[0] + " "  + pl[1] + " "  + pl[2] + " "  + pl[3]
					                + "        " + seeTests;
		}
		// @formatter:on

		public boolean findMatch(Gather g, int matchPosition, int matchAs) {
			// ==============================================================================================

			for (Mpat mpatEntry : knownCases[((type == Rel) ? 0 : 1)]) {
				if (mpatEntry.type == Skp)
					continue;
				if (mpatEntry.checkAgainst(this, g, matchPosition, matchAs))
					return true;
			}
			return false;
		}
	}

	/**
	 */
	static boolean isPatternMatch(Gather g, Suit suit, int matchPosition, int matchAs) {
		// ==============================================================================================
		g.debug_suit = suit;
		FragAnal fa = g.fragAnals[suit.v];
		g.mpatRtn = fa.mpatRtn;
		g.mpatRtn.clear();

		// first we try the Rel set of patterns
		fa.mpatRel.findMatch(g, matchPosition, matchAs);
		// > Ace means play 'x' or '+' or '.'
		if (g.mpatRtn.matchEntryId > 0) {
			if ((g.positionInTrick == matchPosition) && (matchAs == Zzz.MatchAsSelf)) {
				if (g.mpatRtn.rankRel == Rank.PlusDotX) { // '> Ace' means the value in the table was 'x' or '+' or '.'
					g.mpatRtn.rankRel = fa.myFrag.getLowestThatBeatsOrLowest(true /* contig high */,
							Rank.BelowAll /* BellowAll is beaten by everything */).rankRel;
				}
				// else the rankRel value is already in place and correct
			}
			return true;
		}

		// Secondly we try the Equ set of patterns
		fa.mpatEqu.findMatch(g, matchPosition, matchAs);
		if (g.mpatRtn.matchEntryId > 0) {
			if ((g.positionInTrick == matchPosition) && (matchAs == Zzz.MatchAsSelf)) {
				if (g.mpatRtn.rankEqu == Rank.PlusDotX) { // '> Ace' means the value in the table was 'x' or '+' or '.'
					g.mpatRtn.rankRel = fa.myFrag.getLowestThatBeatsOrLowest(g.z /* contig high */,
							Rank.BelowAll /* BellowAll is beaten by everything */).rankRel;
				}
				else {
					// we are required to ALSO return the REL card as the caller may want to know it
					// and we need to convert Equ to Rel - cos rel is the standard format in the strategy zone
					Card card = fa.myFrag.getIfEquExists((matchPosition % 2 == 0), g.mpatRtn.rankEqu);
					if (card == null) {
						@SuppressWarnings("unused")
						int z = 0; // put your breakpoint here
					}
					assert (card != null);
					g.mpatRtn.rankRel = card.rankRel;
				}
			}
			return true;
		}

		return false;
	}

	/**
	 */
	static Card cardByPatternMatch(Gather g, Suit suit, int matchPosition) {
		// ==============================================================================================
		g.debug_suit = suit;
		FragAnal fa = g.fragAnals[suit.v];
		g.mpatRtn = fa.mpatRtn;
		g.mpatRtn.clear();

		// First - we try the Rel set of patterns
		fa.mpatRel.findMatch(g, matchPosition, Zzz.MatchAsSelf);
		if (g.mpatRtn.matchEntryId > 0) {
			if (g.mpatRtn.rankRel == Rank.PlusDotX) { // '> Ace' means the value in the table was 'x' or '+' or '.'
				g.mpatRtn.rankRel = fa.myFrag.getLowestThatBeatsOrLowest(g.z /* contig high */, Rank.BelowAll /* BellowAll is beaten by everything */).rankRel;
			}
			return fa.myFrag.getIfRelExist(g.mpatRtn.rankRel);
		}

		// Second - we try the Equ set of patterns
		fa.mpatEqu.findMatch(g, matchPosition, Zzz.MatchAsSelf);
		if (g.mpatRtn.matchEntryId > 0) {
			if (g.mpatRtn.rankEqu == Rank.PlusDotX) { // '> Ace' means the value in the table was 'x' or '+' or '.'
				g.mpatRtn.rankEqu = fa.myFrag.getLowestThatBeatsOrLowest(g.z /* contig high */, Rank.BelowAll /* BellowAll is beaten by everything */).rankEqu;
			}
			return fa.myFrag.getIfEquExists(g.z /* contig high */, g.mpatRtn.rankEqu);
		}

		return null;
	}
}

/**
 */
class MpatRtn {
	// ---------------------------------- CLASS -------------------------------------
	int matchEntryId; // 0 <= invalid, > 0 means valid match
	Rank rank;
	Rank rankRel; // The rank to lead, should you get the chance
	Rank rankEqu; // The rank to lead, should you get the chance
	int rating;

	public MpatRtn() { // Constructor {
		clear();
	}

	void clear() {
		matchEntryId = 0;
		rank = Rank.Invalid;
		rankRel = Rank.Invalid;
		rankEqu = Rank.Invalid;
		rating = 0;
	}
}
