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
package com.rogerpf.aabridge.view;

import java.awt.Font;
import java.awt.font.TextAttribute;
import java.text.AttributedString;
import java.util.ArrayList;

import javax.swing.JButton;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.model.Bid;
import com.rogerpf.aabridge.model.Call;
import com.rogerpf.aabridge.model.Cc;
import com.rogerpf.aabridge.model.Deal;
import com.rogerpf.aabridge.model.Dir;
import com.rogerpf.aabridge.model.Level;
import com.rogerpf.aabridge.model.Lin.BarBlock;
import com.rogerpf.aabridge.model.Suit;
import com.rpsd.bridgefonts.BridgeFonts;

/**
 */
public class DualDeal {

	// ---------------------------------- CLASS -------------------------------------

	public int index = 0;
	public int qx_number = 0;
	public EachDeal[] eachDeal = { new EachDeal('o'), new EachDeal('c') };
	public boolean impsValid = false;
	public int scoreDiff = 0;
	public String sImps = "";
	public int imps = 0;
	public String sTot[] = { "", "" };
	public int scoreAnchor_gi_index = 0;

	public static final int Open = 0;
	public static final int Closed = 1;

	/**
	 */
	DualDeal(int index, int qx_number, BarBlock qx_bb) {
		// ============================================================================
		this.index = index;
		this.qx_number = qx_number;
		eachDeal[Open].qx_bb = qx_bb;
	}

	public class EachDeal {
		// ---------------------------------- CLASS -------------------------------------
		char type;
		public ArrayList<String> playerNames = new ArrayList<String>();
		public BarBlock qx_bb = null;

		public Dir declarer = Dir.Invalid;
		public Bid contract = new Bid(Call.NullBid);
		public Bid contractDblRe = new Bid(Call.NullBid);
		public int tricksOver = 0; // tricks up or (-) down
		public JButton movieBtn = null;
		public JButton reviewBtn = null;
		public boolean vulnerability[] = { false, false };
		public Deal deal = null;
		public int score = 0;
		public boolean scoreValid = false;
		public int openDealGiIndex = 0;

		EachDeal(char type) {
			// ============================================================================
			this.type = type;
			playerNames.add("");
			playerNames.add("");
			playerNames.add("");
			playerNames.add("");
		}

		public void setResult(String s) {
			// ============================================================================
			s = s.toLowerCase();

			if (s.startsWith("p")) {
				// passed out
				contract = new Bid(Call.Pass); // PassedOut
			}
			else if (s.length() >= 4) {
				Level level = Level.levelFromChar(s.charAt(0));
				Suit suit = Suit.charToSuitOrNt(s.charAt(1));
				if (suit != Suit.Invalid) {
					contract = new Bid(level, suit);
				}
				declarer = Dir.directionFromChar(s.charAt(2));
				if (s.contains("xx")) {
					contractDblRe = new Bid(Call.ReDouble);
					s = s.substring(s.indexOf("xx") + 2);
				}
				else if (s.contains("x")) {
					contractDblRe = new Bid(Call.Double);
					s = s.substring(s.indexOf("x") + 1);
				}
				else {
					s = s.substring(3);
				}
				int neg = (s.charAt(0) == '-') ? -1 : 1;
				tricksOver = Aaa.extractPositiveIntOrZero(s) * neg;

				@SuppressWarnings("unused")
				int z = 0;
			}
		}

		public void setPlayerNames(BarBlock pn, int offset) {
			// ============================================================================
			for (int i = 0; i < 4; i++) {
				playerNames.set(i, pn.get(i + offset));
			}
		}

		public String declarerCompass() {
			// ============================================================================
			if (contract.isCall())
				return " ";
			return declarer.toStr().toLowerCase();
		}

		public AttributedString makeAttributedStringFromContract(boolean showResults) {
			// ============================================================================
			float fontSize = 12;

			String s;
			AttributedString at;

			if (contract.isCall()) {
				s = "" + (contract.isPass() ? "PO" : "-");
				at = new AttributedString(s);
				// at.addAttribute(TextAttribute.FONT, stdFont, 0, s.length());
				return at;
			}

			s = contract.toLinStr();
			if (contractDblRe.isDouble()) {
				s += "*";
			}
			else if (contractDblRe.isDouble()) {
				s += "r";
			}
			else {
				s += " ";
			}
			if (showResults) {
				if (tricksOver == 0)
					s += "=";
				else if (tricksOver > 0)
					s += "+" + tricksOver;
				else
					s += tricksOver;
			}

			at = new AttributedString(s);
			Font bfont = BridgeFonts.faceAndSymbFont.deriveFont(fontSize);
			at.addAttribute(TextAttribute.FONT, bfont, 0, 2);
			at.addAttribute(TextAttribute.FOREGROUND, contract.suit.color(Cc.Ce.Strong), 0, 2);
			at.addAttribute(TextAttribute.FOREGROUND, Suit.NoTrumps.color(Cc.Ce.Strong), 2, 3);

			return at;
		}

		public void calculateScore() {
			/** 
			 * We the current settings have been collected from the results table supplied
			 * in the rs letter pair.  However if they did not exisit  or our entry is missing
			 * then we had best recreate it from the deal - assuming it is here and is valid
			 */
			if ((contract.isValidBid() == false) && (contract.isPass() == false)) {
				if (deal == null) {
					scoreValid = false;
					return;
				}
				declarer = deal.contractCompass;
				contract = deal.contract;
				contractDblRe = deal.contractDblRe;
				tricksOver = deal.tricksOver();
				// vulnerability is already set as best we can set it.
				// these values are now in place for the contract display
			}

			Deal d = new Deal(0);
			d.contract = contract;
			d.contractCompass = declarer;
			d.contractDblRe = contractDblRe;
			d.vulnerability = vulnerability; // got from the other play of this hand

			// fake a claim - which is all that is needed to get the result into the deal
			d.endedWithClaim = true;
			d.tricksClaimed = 6 + contract.level.v + tricksOver;

			score = d.getBoardSimpleScore();

			scoreValid = true;
		}

	}

}
