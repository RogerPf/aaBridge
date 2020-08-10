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

import java.util.ArrayList;
import java.util.Stack;
import java.util.StringTokenizer;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.model.Lin.BarBlock;

/**
 *  BotExtra
 */
public class BotExtra {

	private Deal deal;
	private BotHints bh;
	private BotInstr bi;

	private boolean cardsMoved_by_bot_instructions;

	BotExtra(Deal d) {
		deal = d;
		bh = new BotHints(this);
		bi = new BotInstr(this);
	}

	public BotExtra(Deal d, BotExtra botExtra) {
		deal = d;
		bh = new BotHints(this, botExtra.bh);
		bi = new BotInstr(this, botExtra.bi);
	}

	public void clear_botHints() {
		bh.clear();
	}

	public void clear_botInstructions() {
		bi.clear();
	}

	public Card cardForHintCandidate(Card card, boolean[] skip_DDS) {
		return bh.cardForHintCandidate(card, skip_DDS);
	}

	public void new_BotHint(BarBlock bb) {
		bh.add(bh.new RowBh(this, bb));
	}

	public void new_botInstruction(BarBlock bb) {
		bi.add(bi.new RowBi(this, bb));
	}

	public void apply_botInstructions() {
		bi.apply_botInstructions();
	}

	public boolean haveCardsMoved() {
		return cardsMoved_by_bot_instructions;
	}

	public boolean hasBotHints() {
		return (bh.size() > 0);
	}

	public boolean hasBotInstructions() {
		return (bi.size() > 0);
	}

	public String getBotHintsAndInstructionsAsLinSave(String eol_or_blank, String extra_space) {
		String out = "";
		String one_off_eol = eol_or_blank;

		for (BotHints.RowBh rowBh : bh) {
			// System.out.println("bhh--- " + rowBh.bb);
			boolean first = true;
			out += one_off_eol;
			one_off_eol = "";
			for (String s : rowBh.bb) {
				if (first) {
					first = false;
					out += "bh|" + s.trim();
					continue;
				}
				out += ", " + s.trim();
			}
			out += extra_space + "|" + eol_or_blank;
		}

		for (BotInstr.RowBi rowBi : bi) {
			// System.out.println("bi --- " + rowBi.bb);
			boolean first = true;
			out += one_off_eol;
			one_off_eol = "";
			for (String s : rowBi.bb) {
				if (first) {
					first = false;
					out += "bi|" + s.trim();
					continue;
				}
				out += ", " + s.trim();
			}
			out += extra_space + "|" + eol_or_blank;
		}

		return out;
	}

	static private int extract1stInt(String s) {
		int i = Aaa.extractPositiveInt(s);
		return (i <= 13) ? i : -1;
	}

	static private int extract2ndInt(String s) {
		int hyphen = s.indexOf('-');

		if (hyphen > 1) {
			int i = Aaa.extractPositiveInt(s.substring(hyphen + 1));
			if (i <= 13) {
				return i;
			}
		}
		return Aaa.extractPositiveInt(s);
	}

	static private char charToLower(char c) {
		if ('A' <= c && c <= 'Z')
			return (char) (c - 'A' + 'a');
		else
			return c;
	}

	static private char getLastChar(String s) {
		return s.charAt(s.length() - 1);
	}

	static private int charToInt(char c) {
		if ('0' <= c && c <= '9')
			return (char) (c - '0');
		else
			return -1;
	}

	private int getPositionInTrick() {
		return (deal.countCardsPlayed() % 4);
	}

	private boolean hasAnyValidCardDesc(String swapOneOf) {
		if (swapOneOf.isEmpty())
			return false;
		if (Suit.charToSuit(swapOneOf.charAt(0)) == Suit.Invalid)
			return false;
		if (swapOneOf.length() == 1)
			return true;
		// @formatter:off
		char c = swapOneOf.charAt(1); 
		switch (c) {
		case 'U':; case 'u':; case 'H':; case 'h':; case 'M':; case 'm':; case 'L':; case'l': return true;
		}
		// @formatter:on
		if (Rank.charToRank(swapOneOf.charAt(1)) == Rank.Invalid)
			return false;
		return true;
	}

	private boolean hasValidPositionList(String pl) {
		return pl.toLowerCase().contains("lead") || pl.toLowerCase().startsWith("l") || pl.contains("1") || pl.contains("2") || pl.contains("3")
				|| pl.contains("4");
	}

	private ArrayList<Hand> extractDirectionList(String dirs_in) {

		ArrayList<Hand> list = new ArrayList<Hand>();

		StringTokenizer st = new StringTokenizer(dirs_in, "-");

		while (st.hasMoreTokens()) {
			String s = st.nextToken();
			char c = s.toLowerCase().charAt(0);
			// @formatter:off
			switch (c) {
				case 'n': list.add(deal.hands[Dir.North.v]); break;
				case 's': list.add(deal.hands[Dir.South.v]); break;
				case 'e': list.add(deal.hands[Dir.East.v]); break;
				case 'w': list.add(deal.hands[Dir.West.v]); break;
				case 'd': list.add(deal.hands[deal.contractCompass.v]); break;
				case 'y': 
				case 't': list.add(deal.hands[deal.contractCompass.rotate180().v]); break;
				case 'l': list.add(deal.hands[deal.contractCompass.nextClockwise().v]); break;
				case 'r': list.add(deal.hands[deal.contractCompass.prevAntiClockwise().v]); break;				
				default: list.clear(); return list;
			}
			// @formatter:on
		}
		return list;
	}

	private String evaluateCondition(String cond, Stack<Boolean> stack, Hand hand, Card bi_card) {
		String errMsg = null;
		StringTokenizer st = new StringTokenizer(cond, " ");

		boolean invert = false;
		try {
			if (st.hasMoreTokens()) {
				String typeStr = st.nextToken();
				char type = charToLower(typeStr.charAt(0));

				if (type == '!') {
					invert = true;
					if (typeStr.length() > 1) {
						type = charToLower(typeStr.charAt(1));
					}
					else {
						typeStr = st.nextToken();
						type = charToLower(typeStr.charAt(0));
					}
				}

				switch (type) {
				default:
					return "  Type: " + typeStr + "   not known.";

				case 't': /* True */ {
					stack.push(true ^ invert);
					break;
				}

				case 'f': /* False */ {
					stack.push(false ^ invert);
					break;
				}

				case 'r': /* Rounds */ {
					String suitOpNumb = st.nextToken();
					Suit suit = Suit.charToSuit(suitOpNumb.charAt(0));
					if (suit.equals(Suit.Invalid)) {
						errMsg = "Suit in >>> " + suitOpNumb + " <<< not understood";
						break;
					}

					int tricks = charToInt(getLastChar(suitOpNumb));
					if (tricks < 0) {
						errMsg = "Number of tricks at end of >>> " + suitOpNumb + " <<< not understood";
						break;
					}

					String snOp = extractOperator(suitOpNumb);
					if (snOp == null) {
						errMsg = "Operator  <, >, <=, >=, !=, =    >>> " + suitOpNumb + " <<<  not found";
						break;
					}

					// -------------------------------------

					String wonLostNumb = st.nextToken();
					char wlChar = charToLower(wonLostNumb.charAt(0));

					if (wlChar != 'w' && wlChar != 'l') {
						errMsg = "Won or Lost in >>> " + wonLostNumb + " <<< not understood";
						break;
					}

					int wlTricks = charToInt(getLastChar(wonLostNumb));
					if (wlTricks < 0) {
						errMsg = "Number of tricks won lost at end of >>> " + wonLostNumb + " <<< not understood";
						break;
					}

					String wlOp = extractOperator(suitOpNumb);
					if (wlOp == null) {
						errMsg = "Operator  <, >, <=, >=, !=, =    >>> " + suitOpNumb + " <<<  not found";
						break;
					}

					// --------------------------------

					boolean res = hasDeclarerWonLostAsRequired(hand, suit, snOp, tricks, wlChar, wlOp, wlTricks);
					// System.out.println("    Rounds:  " + cond  +  "     res:" + res);
					stack.push(res ^ invert);

					break;
				}

				case 'p': /* Played */ {
					if (this.getPositionInTrick() == 0) {
						stack.push(false /* ^ invert */); // We are the leader and are about to lead so false this condition
						return null;
					}

					String playerSeatStr = st.nextToken();

					ArrayList<Hand> handList = extractDirectionList(playerSeatStr);
					if (handList.isEmpty()) {
						errMsg = "Player(s)   >>> " + playerSeatStr + " <<<   not understood  expexted E-W-Dec-Table etc";
						break;
					}

					Hand leader = deal.getCurTrickLeader();
					int wantedSize = deal.getCurTrickIndex() + 1;

					Cal cardsPlayedToTrickFiltered = new Cal();

					for (Hand hr : deal.rota[leader.compass.v]) {
						if (hr.played.size() != wantedSize) {
							break; // end of played card in the trick
						}
						for (Hand hd : handList) {
							if (hd == hr) {
								cardsPlayedToTrickFiltered.add(hd.played.getLast());
							}
						}
					}

					if (cardsPlayedToTrickFiltered.isEmpty()) {
						stack.push(false ^ invert); // no player / hand / position match
						return null;
					}

					String cardStr = st.nextToken();

					if (cardStr.length() % 2 == 1) {
						cardStr += "*";
					}

					boolean matched = false;

					for (int i = 0; i < cardStr.length(); i += 2) {

						Suit suit = Suit.charToSuit(cardStr.charAt(i));
						if (suit == Suit.Invalid) {
							errMsg = "Invalid suit    >>> " + cardStr + " <<<  in card list";
							break;
						}

						Rank rank = null; // means don't care what rank
						if (cardStr.length() > i + 1) {
							char r = cardStr.toUpperCase().charAt(i + 1);
							if (r == '*' || r == 'U' || r == 'L') {
								// OK this shows dont care about the rank
							}
							else {
								rank = Rank.charToRank(r);
								if (rank == Rank.Invalid) {
									errMsg = "Rank of card in     >>> " + cardStr + " <<<   not understood";
									break;
								}
							}
						}

						for (Card card : cardsPlayedToTrickFiltered) {
							if (suit == card.suit && (rank == null || rank == card.rank)) {
								matched = true;
								break;
							}
						}
					}

					if (errMsg != null)
						break;

					stack.push(matched ^ invert);   // success
					return null;
				}

				case 'n': /* neededToWin - is the supplied card needed to win (4th in hand only)*/ {

					if ((bi_card != null) && hand.isThisCardNeededToWinWhen4thToPlay(bi_card)) {
						stack.push(true ^ invert);   // yes
						return null;
					}
					else {
						stack.push(false ^ invert);   // no
						return null;
					}
				}
				}
			}
		} catch (java.util.NoSuchElementException e) {
			errMsg = " condition has too few parts.";
		}

		return errMsg;
	}

	private String extractOperator(String suitOpNumb) {

		int len = suitOpNumb.length();
		if (len < 3)
			return null;

		String bit = suitOpNumb.substring(len - 3, len - 1);
		if (bit.contentEquals("!=") || bit.contentEquals("==") || bit.contentEquals(">=") || bit.contentEquals("<=")) {
			return bit;
		}
		char e = suitOpNumb.charAt(len - 2);
		if (e == '=' || e == '>' || e == '<') {
			return e + "";
		}
		return null;
	}

	private boolean hasDeclarerWonLostAsRequired(Hand hand, Suit suit, String ledOp, int ledTricks, char wlChar, String wlOp, int wlTricks) {

		int led = 0;
		int won = 0;
		int lost = 0;

		int tcount = -2;
		Hand leader = null;
		for (Hand leaderPlus1 : deal.prevTrickWinner) {
			tcount++;
			if (tcount == -1) {
				leader = leaderPlus1;
				continue;
			}
			// this way we know that the leader has led to a completed trick
			if (leader.played.get(tcount).suit != suit) {
				continue;
			}
			led++;
			if (leaderPlus1.axis() == deal.contractAxis())
				won++;
			else
				lost++;
			leader = leaderPlus1;
		}

		return compare(ledTricks, ledOp, led) && compare(wlTricks, wlOp, (wlChar == 'w' ? won : lost));

	}

	private boolean compare(int n1, String op, int n2) {

		if (op.contentEquals("=") || op.contentEquals("=="))
			return n1 == n2;

		if (op.contentEquals("!="))
			return n1 != n2;

		if (op.contentEquals("<"))
			return n1 < n2;

		if (op.contentEquals(">"))
			return n1 > n2;

		if (op.contentEquals("<="))
			return n1 <= n2;

		if (op.contentEquals(">="))
			return n1 >= n2;

		return false;
	}

	private boolean processToken(Stack<Boolean> stack, int ind, String lineOne, BarBlock bb, Hand hand, int played, Card bi_card) {
		String token;
		if (bb.size() <= ind || bb.get(ind).isEmpty()) {
			System.out.println(lineOne);
			System.out.println("    'Condition / opperand' eg 'Rounds Dia3 Won3' or AND / OR / XOR   etc   misssing  at pos:" + (ind + 1));
			return false;
		}
		token = bb.get(ind).trim();
		String tokenL = token.toLowerCase();

		char opp = 'c';
		if (tokenL.contentEquals("and")) {
			opp = 'a';
		}
		else if (tokenL.contentEquals("or")) {
			opp = 'o';
		}
		else if (tokenL.contentEquals("xor")) {
			opp = 'x';
		}

		if (opp == 'c') {
			String errorMsg = evaluateCondition(token, stack, hand, bi_card /* optional card */);
			if (errorMsg != null) {
				System.out.println(lineOne);
				System.out.println("    Error in  conditon:" + token + "    from pos:" + (ind + 1));
				System.out.println("      Condition error report:  " + token + "      " + errorMsg);
				return false;
			}
			return true;
		}
		else {
			if (stack.size() < 2) {
				System.out.println(lineOne);
				System.out.println("     Too few conditions (need 2) to do the >>> " + tokenL + " <<< at pos:" + (ind + 1));
				return false;
			}

			boolean c1 = stack.pop();
			boolean c2 = stack.pop();

			if (opp == 'a')
				stack.push(c1 && c2);
			else if (opp == 'o')
				stack.push(c1 || c2);
			else if (opp == 'x')
				stack.push(c1 ^ c2);
		}

		return true; // token processed OK
	}

	@SuppressWarnings("serial")
	/**
	 *  BotHints
	 */
	class BotHints extends ArrayList<BotHints.RowBh> {
		// ==============================================================================================

		BotExtra owner;

		private BotHints(BotExtra botExtra_in) { /* Constructor */
			owner = botExtra_in;
		}

		BotHints(BotExtra botExtra_in, BotHints o) { /* Constructor */
			owner = botExtra_in;
			for (RowBh o_rowBh : o) {
				add(new RowBh(owner, o_rowBh));
			}
		}

		Card cardForHintCandidate(Card card, boolean skip_DDS[]) {
			Hand hand = deal.getNextHandToPlay();
			int played = deal.countCardsPlayed();
			Card replacement = null;
			for (RowBh actBh : this) {
				replacement = actBh.cardForHintCandidate(card, hand, played, skip_DDS);
				if (replacement != null)
					return replacement;
			}
			return card;
		}

		/**
		 * RowBh (RowBotHint)
		 */
		public class RowBh {
			// ==============================================================================================

			BotExtra owner;
			BarBlock bb;

			public RowBh(BotExtra o_owner, BarBlock b) { /* Constructor */
				owner = o_owner;
				bb = b;
			}

			private RowBh(BotExtra o_owner, RowBh o_Bh) { /* duplicate Constructor */
				owner = o_owner;
				bb = o_Bh.bb;
			}

			String diagStr(Card card, Hand hand, int played) {
				int trick = (played / 4) + 1;  // so users can use 1 based counting
				int posit = (played % 4) + 1;  // so users can use 1 based counting
				return "   Card:" + card + "   Hand:" + hand.compass + "  Seat:p" + posit + "   Trick:" + trick + "\n    bh:" + bb;
			}

			static final String infn = "  inside cardForHintCandidate()   ";

			public Card cardForHintCandidate(Card card, Hand hand, int played, boolean skip_DDS[]) {
				int trick = (played / 4) + 1;  // so users can use 1 based counting
				int posit = (played % 4) + 1;  // so users can use 1 based counting

				String lineOne = "  line:" + bb.lineNumber + infn + diagStr(card, hand, played);

				int ind = 0; //--------------------------------------------------		
				String play_text = (bb.size() <= ind) ? "" : bb.get(ind).trim();
				if (play_text.equalsIgnoreCase("play") == false) {
					System.out.println(lineOne);
					System.out.println("    The word  'Play'  not at front  >>> " + play_text + " <<< found");
					return null;
				}

				ind = 1; //--------------------------------------------------
				String dirListStr = (bb.size() <= ind) ? "" : bb.get(ind).trim();
				ArrayList<Hand> seats = extractDirectionList(dirListStr);
				if (seats.isEmpty()) {
					System.out.println(lineOne);
					System.out.println("   Need  'Direction List'  E-W etc  got  >>> " + dirListStr + " <<<  at pos:" + (ind + 1));
					return null;
				}
				// @formatter:off
				boolean seatMatch =          (hand == seats.get(0)) 
				    || (seats.size() > 1) && (hand == seats.get(1))
				    || (seats.size() > 2) && (hand == seats.get(2));
				// @formatter:on
				if (seatMatch == false) {
					return null;
				}

				ind = 2; //--------------------------------------------------
				String rawCards = (bb.size() <= ind) ? "" : bb.get(ind).trim();
				if (hasAnyValidCardDesc(rawCards) == false) {
					System.out.println(lineOne);
					System.out.println("    missing or invalid 'Card to play' list  eg:  hKdQhL   >>> " + rawCards + " <<< at pos:" + (ind + 1));
					return null;
				}
				Card replacement = hand.getUnplayedCardMatchingRaw(rawCards, bb, true /* selectableNeeded */);
				if (replacement == null) {
					// we do not report if the card cannot be played - the problem composer - is supposed to NOTICE :)	
					return null;
				}

				ind = 3; //--------------------------------------------------
				String extra = (bb.size() <= ind) ? "" : bb.get(ind).trim();
				String extraLower = extra.toLowerCase();
				boolean local_skipDDS = extraLower.contains("bad"); // we only set the real one  if we have a successful change
				if (local_skipDDS == false && extra.contains("res") == false) {
					System.out.println(lineOne);
					System.out.println("    'BAD' or 'res'  NOT >>> " + extraLower + " <<< needed in the item at  pos:" + (ind + 1));
					return null;
				}

				ind = 4; //--------------------------------------------------
				String positionList = (bb.size() <= ind) ? "" : bb.get(ind).trim();
				if (hasValidPositionList(positionList) == false) {
					System.out.println(lineOne);
					System.out.println("    missing 'Position in Trick' list  eg '1234' or 'lead'  >>> " + positionList + " <<<  at pos:" + (ind + 1));
					return null;
				}
				if (positionList.toLowerCase().contains("lead"))
					positionList += "1";
				if (positionList.contains((posit /* remember our users use 1 based counting */) + "") == false) {
					return null;
				}

				ind = 5; //--------------------------------------------------
				String trickNumberList = (bb.size() <= ind) ? "" : bb.get(ind).trim();
				int first = extract1stInt(trickNumberList);
				int last = extract2ndInt(trickNumberList);
				if (first < 1 || last < first) {
					System.out.println(lineOne);
					System.out
							.println("    'Trick number range' missing or invalid eg 'T1'  or  T7-13'  >>> " + trickNumberList + " <<<   at pos:" + (ind + 1));
					return null;
				}
				if (trick < first || last < trick) {
					return null;
				}

				ind = 6; //--------------------------------------------------
				Stack<Boolean> stack = new Stack<Boolean>();

				String can = (bb.size() <= ind) ? "" : bb.get(ind).trim();
				String canLower = can.toLowerCase();
				if (canLower.contains("always")) {
					skip_DDS[0] = local_skipDDS;
					return replacement;
				}
				if (canLower.contains("never")) {
					return null;
				}
				if ((canLower.contains("cond") == false)) {
					System.out.println(lineOne);
					System.out.println("   invalid 'Cond (conditions) / Always / Never'  declaration   at pos:" + (ind + 1));
					return null;
				}

				ind = 7; //--------------------------------------------------
				for (int i = ind; i < bb.size(); i++) {
					boolean processed_OK = processToken(stack, i, lineOne, bb, hand, played, null /* no bi card for bh */);
					if (!processed_OK)
						return null;
				}
				if (stack.size() != 1) {
					System.out.println(lineOne);
					System.out.println("    All conditions and operators processed leaving >>> " + stack.size() + " <<< answers - should only be ONE");
					return null;
				}
				if (stack.pop()) {
					skip_DDS[0] = local_skipDDS;
					return replacement;
				}

				return null;
			}
		}
	}

	@SuppressWarnings("serial")
	/**
	 * BotInstructions
	 */
	class BotInstr extends ArrayList<BotInstr.RowBi> {
		// ==============================================================================================
		BotExtra owner;

		private BotInstr(BotExtra botExtra_in) { /* Constructor */
			owner = botExtra_in;
		}

		private BotInstr(BotExtra botExtra_in, BotInstr o) { /* Constructor */
			owner = botExtra_in;
			for (RowBi o_bi : o) {
				add(new RowBi(owner, o_bi));
			}
		}

		private boolean apply_botInstructions() {
			if (size() == 0)
				return false;
			Hand hand = deal.getNextHandToPlay();
			if ((hand.compass.axis() != Dir.EW) || (hand.countUnplayedCards() < 1))
				return false;
			Boolean deal_changed = false;
			int played = deal.countCardsPlayed();
			for (RowBi actionBi : this) {
				deal_changed |= actionBi.apply_botInstructions(hand, played);
			}
			cardsMoved_by_bot_instructions |= deal_changed;
			return deal_changed; // tell them we have made a change THIS TIME
		}

		/**
		 * RowBi  (RowBotInstruction)
		 */
		class RowBi {
			BotExtra owner;
			BarBlock bb;

			RowBi(BotExtra o_owner, BarBlock b) { /* Constructor */
				owner = o_owner;
				bb = b;
			}

			RowBi(BotExtra o_owner, RowBi o_Bh) { /* duplicate Constructor */
				owner = o_owner;
				bb = o_Bh.bb;
			}

			String diagStr(Hand hand, int played) {
				int trick = (played / 4) + 1;  // so users can use 1 based counting
				int posit = (played % 4) + 1;  // so users can use 1 based counting
				return "   Hand:" + hand.compass + "  Seat:p" + posit + "   Trick:" + trick + "\n    bi:" + bb;
			}

			static final String infn = "  inside cardForHintCandidate() ";

			public boolean apply_botInstructions(Hand hand, int played) {

				String lineOne = "  line:" + bb.lineNumber + infn + diagStr(hand, played);

				int trick = (played / 4) + 1;  // so users can use 1 based counting
				int posit = (played % 4) + 1;  // so users can use 1 based counting

				int ind = 0; //--------------------------------------------------
				String swap_text = (bb.size() <= ind) ? "" : bb.get(ind).trim();
				if (swap_text.equalsIgnoreCase("swap") == false) {
					System.out.println(lineOne);
					System.out.println("    The word  'Swap'  not at front.  >>> " + swap_text + " <<< found");
					return false;
				}

				ind = 1; //--------------------------------------------------				
				String dirListStr = (bb.size() <= ind) ? "" : bb.get(ind).trim();
				ArrayList<Hand> seats = extractDirectionList(dirListStr);
				if (seats.isEmpty() || (seats.get(0).axis() != Dir.EW)) {
					System.out.println(lineOne);
					System.out.println("    Need  East, E,  West, W,  E-W or W-E   >>> " + dirListStr + " <<< 'Direction List' at pos:" + (ind + 1));
					return false;
				}
				// @formatter:off
				boolean seatMatch =          (hand == seats.get(0)) 
				    || (seats.size() > 1) && (hand == seats.get(1));
				// @formatter:on
				if (seatMatch == false) {
					return false;
				}
				Hand swapToHand = seats.get(0);
				Hand swapFromHand = swapToHand.partner();

				ind = 2; //--------------------------------------------------
				String swapOneOf = (bb.size() <= ind) ? "" : bb.get(ind).trim();
				if (hasAnyValidCardDesc(swapOneOf) == false) {
					System.out.println(lineOne);
					System.out.println("    missing or invalid 'Swap one of' list  eg:  hKdQhL   >>> " + swapOneOf + " <<< at pos:" + (ind + 1));
					return false;
				}
				Card toSwap = swapFromHand.getUnplayedCardMatchingRaw(swapOneOf, bb, false /* selectableNeeded */);
				if (toSwap == null) {
					// we do not report if the card cannot be played - the problem composer - is supposed to NOTICE :)	
					return false;
				}
//				if (swapFromHand == /* target */ hand) {
//					return false; // the card to swap is already in the target first hand
//				}

				ind = 3; //--------------------------------------------------
				String swapForStr = (bb.size() <= ind) ? "" : bb.get(ind).trim();
				if (hasAnyValidCardDesc(swapForStr) == false) {
					System.out.println(lineOne);
					System.out.println("    missing  'Replace by card'  eg:  c5   >>> " + swapForStr + " <<< at pos:" + (ind + 1));
					return false;
				}
				Card swapFor = swapToHand.getUnplayedCardMatchingRaw(swapForStr, bb, false /* selectableNeeded */);
				if (swapFor == null) {
					// we do not report if the card cannot be played - the problem composer - is supposed to NOTICE :)
					// it will often be already played by this hint.
					return false;
				}

				ind = 4; //--------------------------------------------------
				String positionList = (bb.size() <= ind) ? "" : bb.get(ind).trim();
				if (hasValidPositionList(positionList) == false) {
					System.out.println(lineOne);
					System.out.println("    missing 'Position in Trick' list  eg '1234' or 'lead'  >>> " + positionList + " <<<  at pos:" + (ind + 1));
					return false;
				}
				if (positionList.toLowerCase().contains("lead"))
					positionList += "1";
				if (positionList.contains((posit /* remember our users use 1 based counting */) + "") == false) {
					return false;
				}

				ind = 5; //--------------------------------------------------
				String trickNumberList = (bb.size() <= ind) ? "" : bb.get(ind);
				int first = extract1stInt(trickNumberList);
				int last = extract2ndInt(trickNumberList);
				if (first < 1 || last < first) {
					System.out.println(lineOne);
					System.out.println("     'Trick number range' missing or invalid >>> " + trickNumberList + " <<< eg 'T1'  or  T7-13' at pos:" + (ind + 1));
					return false;
				}
				if (trick < first || last < trick) {
					return false;
				}

				ind = 6; //--------------------------------------------------
				Stack<Boolean> stack = new Stack<Boolean>();
				String can = (bb.size() <= ind) ? "" : bb.get(ind).trim();
				String canLower = can.toLowerCase();
				if (canLower.contains("always")) {
					stack.push(true);
				}
				else if (canLower.contains("never")) {
					return false;
				}
				else if ((canLower.contains("cond"))) {
					if (bb.size() == ind + 1) {
						System.out.println(lineOne);
						System.out.println("    Cond specified BUT no conditions suplied");
						return false;
					}
				}
				else {
					System.out.println(lineOne);
					System.out.println("    invalid 'Cond (conditions) / Always / Never'  declaration >>> " + can + " <<< at pos:" + (ind + 1));
					return false;
				}

				ind = 7; //--------------------------------------------------
				for (int i = ind; i < bb.size(); i++) {
					boolean processed_OK = processToken(stack, i, lineOne, bb, hand, played, toSwap);
					if (!processed_OK)
						return false;
				}

				if (stack.size() != 1) {
					System.out.println(lineOne);
					System.out.println("    All conditions and operators processed leaving >>> " + stack.size() + " <<< answers - should only be ONE");
					return false;
				}

				if (stack.pop()) {
					deal.swapCards(toSwap, swapFor);
					return true;
				}

				return false;
			}

		}
	}
}
