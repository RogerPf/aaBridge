package com.rogerpf.aabridge.model;

import java.awt.Point;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Lin extends ArrayList<Deal> {
	// ---------------------------------- CLASS -------------------------------------

	private static final long serialVersionUID = 1L;

	static final char bar = 0x7c;
	static final char carriageReturn = '\r';
	static final char newLine = '\n';

	public ArrayList<BarBlock> bbAy = new ArrayList<BarBlock>();
	public String headingInfo = "";

	public boolean twoTeams = false;

	public class BarBlock extends ArrayList<String> {
		// ---------------------------------- CLASS -------------------------------------
		private static final long serialVersionUID = 1L;
		public String type;

		BarBlock(String type) {
			// ==============================================================================================
			this.type = type;
		}
	}

	void lin_bp() {
		// ==============================================================================================
		@SuppressWarnings("unused")
		int x = 0; // put your breakpoint here
	}

	public Lin(FileInputStream fis, File fileIn, String dotExt, boolean hideResults) throws IOException {
		// ==============================================================================================

		boolean firstBarSeen = false;

		{
			String s = "";
			int i;
			char c;
			BarBlock bb = null;

			while ((i = fis.read()) != -1) {
				c = (char) i;
				// System.out.print(c);

				if (c == '\r' || c == '\n') {
					if (firstBarSeen == false)
						s = "";
					// and we just ignore them
					continue;
				}

				if (c == ',') {
					if (bb == null) { // should not happen
						lin_bp();
						throw new IOException();
					}
					else {
						bb.add(s);
						s = "";
					}
					continue;
				}

				if (c == bar) {
					if (firstBarSeen == false) {
						firstBarSeen = true;
						int sLen = s.length();
						if (sLen > 2)
							s = s.substring(sLen - 2);
					}

					if (bb == null) {
						if (s.isEmpty()) { // should not happen barBlock needs an Id (token/type)
							lin_bp();
							throw new IOException();
						}
						bb = new BarBlock(s);
						bbAy.add(bb);
						s = "";
					}
					else {
						bb.add(s);
						bb = null;
						s = "";
					}
					s = "";
				}
				else {
					s += c;
				}
			}
		}

		int qxCount = 0;
		for (BarBlock bb : bbAy) {
			if (bb.type.contentEquals("qx")) {
				qxCount++;
			}
		}
		boolean multiDealLin = (qxCount > 1);

		/**
		 *  qx appears to be the main 'new hand' marker  we add an extra one
		 *   to force the final deal to be saved more easily
		 */
		bbAy.add(new BarBlock("qx"));

		int dealCount = -1;

		int dealer = Zzz.North;
		boolean vunerability[] = { false, false }; // ns, ew
		ArrayList<String> playerNamesOpen = new ArrayList<String>();
		ArrayList<String> playerNamesClosed = new ArrayList<String>();
		BarBlock results = null;
		BarBlock madeClaim = null;
		BarBlock boardNumbers = null;
//		BarBlock boardHeader = null;
		Deal deal = null;
		String qxText = "";

		boolean skipToNext_qx = false;

		int bbAySize = bbAy.size();
		for (int bbInd = 0; bbInd < bbAySize; bbInd++) {

			BarBlock bb = bbAy.get(bbInd);
			BarBlock bbNext = (bbInd + 1 < bbAySize) ? bbAy.get(bbInd + 1) : bbAy.get(0) /* any valid bb */;

			if (skipToNext_qx && !bb.type.contentEquals("qx"))
				continue;

			if (bb.type.contentEquals("vg")) { // vg => view graph ?
				if (bb.size() >= 8) {
					headingInfo = bb.get(0) + "   " + bb.get(1) + "   " + bb.get(5) + " - " + bb.get(7);
				}
				continue;
			}

			if (bb.type.contentEquals("bn")) { // bn => boardNumbers
				boardNumbers = bb;
				continue;
			}

//			if (bb.type.contentEquals("ah")) { // ah => all header
//				boardHeader = bb;
//				continue;
//			}

			if (bb.type.contentEquals("pn")) { // pn => player names
				/** there can be one of these for each board 
				 *  OR  for teams only one for the whole file then normally 8 entries
				 */
				playerNamesOpen.clear();
				playerNamesClosed.clear();
				if (bb.size() == 0) {
					; // do nothing
				}
				else if (bb.size() == 4) {
					for (int i : Zzz.zto3)
						playerNamesOpen.add(bb.get(i));
					for (int i : Zzz.zto3)
						playerNamesClosed.add(bb.get(i));
				}
				else if (bb.size() == 8) {
					twoTeams = true; // ++++++++++++++++++++++++++++++++++++++++++++
					for (int i : Zzz.zto3)
						playerNamesOpen.add(bb.get(i));

					for (int i : Zzz.zto3)
						playerNamesClosed.add(bb.get(i + 4));
				}
				else {
					lin_bp();
					throw new IOException();
				}
				continue;
			}

			if (bb.type.contentEquals("rs")) { // rs => results (in teams)
				results = bb;
				continue;
			}

			if (bb.type.contentEquals("mc")) { // mc => made claim
				madeClaim = bb;
				continue;
			}

			if (bb.type.contentEquals("qx")) { // qx => who knows? shows start end of hand
				skipToNext_qx = false;
				dealCount++;

				if (deal != null) {
					/**  here we all all the final values for the actual Deal - before we 
					 *   then go and and read in all the values for the next deal
					 */
					if (!deal.youSeatInLoadedLin && !deal.isBidding()) {
						deal.youSeatHint = deal.contractCompass; // override the simple default of South
					}

					if (madeClaim != null && madeClaim.size() > 0) {
						Point score = deal.getContractTrickCountSoFar();
						int claimed = Integer.parseInt(madeClaim.get(0));
						if (claimed >= score.x) {
							deal.endedWithClaim = true;
							deal.tricksClaimed = claimed;
						}
					}
					madeClaim = null;

					{
						String s = "";
						if (results != null) {
							int rsIndex = (twoTeams) ? dealCount - 1 : dealCount * 2 - 2;
							if (rsIndex < results.size() && !results.get(rsIndex).isEmpty()) {
								s = results.get(rsIndex);
								if (s.length() >= 4) {
									String t = s;
									s = t.substring(2, 3) + "  " + t.substring(0, 2) + t.substring(3);
								}
								int slen = s.length();
								if (slen > 1 && hideResults) {
									if (slen > 3 && s.endsWith("="))
										s = s.substring(0, slen - 1);
									else if (slen > 3)
										s = s.substring(0, slen - 2);
								}
							}
						}

						if (s.isEmpty()) {
							/* there is no offical contract and score 
							 * so we try to recreate them from the deal
							 */
							s = deal.contractAndResShort(hideResults);
						}

						deal.linResult = s;
						if (s.length() > 1) {
							deal.description = Zzz.neswToString(s.charAt(0)) + s.substring(1);
						}

					}
					String sHandNo = "";
					// Put up a nice board number
					if (boardNumbers != null) {
						int bnIndex = dealCount - 1;
						if (bnIndex < boardNumbers.size() && boardNumbers.get(bnIndex) != "") {
							sHandNo = boardNumbers.get(bnIndex);
						}
						deal.linRowText = sHandNo;
						deal.boardNo = Integer.parseInt(deal.linRowText);
					}

					if (deal.linRowText == "") {
						sHandNo = qxText;
						if (sHandNo.length() == 0) {
							sHandNo = ((dealCount % 2 == 0) ? "o" : "c") + (dealCount / 2 + 1);
						}
						if (sHandNo.length() == 2) {
							sHandNo = sHandNo.substring(0, 1) + "0" + sHandNo.substring(1, 2);
						}

						deal.linRowText = sHandNo.substring(1);
						deal.boardNo = Integer.parseInt(deal.linRowText);
					}

					deal.lastSavedAsPathWithSep = fileIn.getParent() + File.separator;
					String sFile = fileIn.getName();

					String s = sFile.substring(0, sFile.lastIndexOf('.'));
					if (multiDealLin) {
						s += " hand " + sHandNo + "  " + deal.description;
					}
					deal.lastSavedAsFilename = s + dotExt;

					add(deal); // DONE - deal is now complete

					deal = null; // clear the way for the next deal in this lin file
					// we don't reset the players names as they stay for the whole of the lin file.
					dealer = Zzz.North;
					vunerability[Zzz.NS] = false;
					vunerability[Zzz.EW] = false;
				}

				qxText = (bb.isEmpty()) ? "" : bb.get(0);

				continue;
			}

			if (bb.type.contentEquals("md")) { // md => maKe deal ?

				if ((bb.size() != 3) && (bb.size() != 4)) { // we must have 3 or 4 hands
					// lin_bp();
					// throw new IOException();
					// BBO can produce such files if there are less than three players at the moment of dealing
					// at the table
					skipToNext_qx = true;
					dealCount--;
					continue;
				}

				/************************************/
				deal = new Deal("", Zzz.South); // Zzz.South will be overriden if/when we know the declarer
				/************************************/

				Cal packPristCopy = deal.packPristine;

				for (int i : Zzz.zto3) {
					int handCompass = (i + 2) % 4; // So starting at South (aaBridge encoding) which is the first hand in the linBlock
					Hand hand = deal.hands[handCompass];
					String hs = bb.get(i);
					int suit = -1;
					for (int j = 0; j < hs.length(); j++) {
						char c = hs.charAt(j);
						if (i == 0 && j == 0 && '1' <= c && c <= '4') {
							dealer = ((c - '0') + 1) % 4; // for lin '1'=South, '4'=East, aaBridge internal 2=South, 0=North
							deal.setDealer(dealer);
							deal.setPlayerNames((dealCount % 2 == 0 ? playerNamesOpen : playerNamesClosed));
							if (dealCount == 6) {
								@SuppressWarnings("unused")
								int x = 0; // put your breakpoint here
							}

							continue;
						}
						// @formatter:off
						switch (c) {
							case 'S': suit = Zzz.Spades;   continue;
							case 'H': suit = Zzz.Hearts;   continue;
							case 'D': suit = Zzz.Diamonds; continue;
							case 'C': suit = Zzz.Clubs;    continue;
						}
						// @formatter:on
						int rank = Zzz.rankChToRank(c);

						Card card = packPristCopy.getIfRankAndSuitExists(rank, suit);

						if (card == null) { // the card must exist in the pack !
							lin_bp();
							throw new IOException();
						}
						packPristCopy.remove(card);
						hand.fOrgs[suit].addDeltCard(card);
						hand.frags[suit].addDeltCard(card);

						// System.out.println(hand + " " + card + "  ");
					}
				}

				if (bb.get(3).length() == 0) {
					// the bastards have been too lazy to list the 4th hand (the East hand) - sigh
					Hand hand = deal.hands[Zzz.East];
					for (Card card : packPristCopy) {
						int suit = card.suit;
						hand.fOrgs[suit].addDeltCard(card);
						hand.frags[suit].addDeltCard(card);
					}
				}

				continue;
			}

			if (bb.type.contentEquals("mb")) { // mb => make bids ?

				String bids = bb.get(0);
				int level = -1;
				int suit = -1;
				Bid prevBid = null;
				for (int i = 0; i < bids.length(); i++) {
					if (deal == null) {
						lin_bp();
						throw new IOException();
					}

					char c = bids.charAt(i);
					if (c == '-')
						continue;
					if (c == 'p') {
						deal.makeBid(prevBid = new Bid(Zzz.PASS));
						continue;
					}
					if (c == 'd') {
						deal.makeBid(prevBid = new Bid(Zzz.DOUBLE));
						continue;
					}
					if (c == 'r') {
						deal.makeBid(prevBid = new Bid(Zzz.REDOUBLE));
						continue;
					}
					if ('1' <= c && c <= '7') {
						level = c - '0';
						continue;
					}
					if (c == '!') {
						if (prevBid != null)
							prevBid.alert = true;
						continue;
					}
					// @formatter:off
					switch (c) {
						case 'N': suit = Zzz.Notrumps; break;
						case 'S': suit = Zzz.Spades;   break;
						case 'H': suit = Zzz.Hearts;   break;
						case 'D': suit = Zzz.Diamonds; break;
						case 'C': suit = Zzz.Clubs;    break;
						default: lin_bp(); throw new IOException();
					}
					// @formatter:on
					if (suit == -1 || level == -1) {
						lin_bp();
						throw new IOException();
					}
					prevBid = new Bid(level, suit);

					// System.out.println(deal.getNextHandToBid() + " " + prevBid + "  ");

					deal.makeBid(prevBid);
					level = -1;
					suit = -1;
				}
				if (prevBid != null && bbNext.type.contentEquals("an") && bbNext.size() == 1) { // anouncement ?
					prevBid.alert = true;
					prevBid.alertText = bbNext.get(0);
				}

				continue;
			}

			if (bb.type.contentEquals("pc")) { // pc => play card ?

				String cd = bb.get(0);
				int suit = -1;
				for (int i = 0; i < cd.length(); i++) { // well there are only TWO chars in each string
					char c = cd.charAt(i);
					// @formatter:off
					switch (c) {
						case 'S': case 's': suit = Zzz.Spades;   continue;
						case 'H': case 'h': suit = Zzz.Hearts;   continue;
						case 'D': case 'd': suit = Zzz.Diamonds; continue;
						case 'C': case 'c': suit = Zzz.Clubs;    continue;
					}
					// @formatter:on
					int rank = Zzz.rankChToRank(c);

					if (suit == -1) {
						lin_bp();
						throw new IOException();
					}

					deal.playCardExternal(suit, rank);
				}

				continue;
			}

			if (bb.type.contentEquals("sv")) { // st => set unverability

				if ((bb.size() != 1) || (bb.get(0).length() != 1)) { // we should have 1 and only one entry
					lin_bp();
					throw new IOException();
				}

				// @formatter:off
				switch (bb.get(0).charAt(0)) {
					case 'b': vunerability[Zzz.NS] = true;  vunerability[Zzz.EW] = true;  break;
					case 'n': vunerability[Zzz.NS] = true;  vunerability[Zzz.EW] = false; break;
					case 'e': vunerability[Zzz.NS] = false; vunerability[Zzz.EW] = true;  break;
					case '0': vunerability[Zzz.NS] = false; vunerability[Zzz.EW] = false; break; // OR can be assumed ?
				}
				// @formatter:on

				deal.vunerability = vunerability.clone();
				deal.rotateDealerAndVunerability(0); // NOTE - rotation is zero so just gets correct board no.
				continue;
			}

			if (bb.type.contentEquals("kp")) { // kp => kibitz player RPf invention !!!!!!!!!!

				if ((bb.size() != 1) || (bb.get(0).length() != 1)) { // we should have 1 and only one entry
					lin_bp();
					// throw new IOException();
					continue; // just ignore it as we 'invented' it
				}

				deal.youSeatInLoadedLin = true;
				deal.youSeatHint = Zzz.South;

				// @formatter:off
				char ch = bb.get(0).charAt(0);
				switch (ch) {
					case 'N': case 'n':
					case 'S': case 's':
					case 'E': case 'e':
					case 'W': case 'w': 
						deal.youSeatHint = Zzz.charToCompass(ch);
				}
				// @formatter:on
				continue;
			}

			if (bb.type.contentEquals("pg")) { // pg => page which we currently ignore
				continue;
			}

			if (bb.type.contentEquals("st")) { // st => single table we currently ignore
				continue;
			}

			if (bb.type.contentEquals("an")) { // an => anotation (on a bid only?)
				continue;
			}

			if (bb.type.contentEquals("ah")) { // ah => (alphabetic?) Board header
				continue;
			}

			if (bb.type.contentEquals("rh")) { // rh => appears to be always null
				continue;
			}

			System.out.println("Unknown lin code: " + bb.type);
		}

		if (size() == 0) { // if there are no deals then this lin is (for us) counted as being invalid
			lin_bp();
			throw new IOException();
		}

		@SuppressWarnings("unused")
		int x = 0; // put your breakpoint here
	}

	static public void saveDealAsSingleLinFile(Deal deal, BufferedWriter w) throws IOException {
		// ===================================================================================
		String unixEOL = "" + (char) 0x0a; // works either way but lets pick one that is ultra common

		w.write("pn|");
		for (Hand hand : deal.rota[Zzz.South]) {
			w.write(hand.playerName);
			if (hand.compass != Zzz.East)
				w.write(",");
		}
		w.write("|");
		w.write(unixEOL);

		w.write("st||md|"); // st => single table ?? who knows md => make deal ?

		// now as the **** first character of the South hand defintion **** we write the dealer id
		// for lin '1'=South, '4'=East, aaBridge internal 2=South, 1=East

		w.write("" + (char) (((deal.dealer + 2) % 4 + 1 + '0')));

		// write out the first three hands leaving East's to be deduced
		for (Hand hand : deal.rota[Zzz.South]) {
			if (hand.compass == Zzz.East)
				break;
			w.write(hand.cardsForLinSave());
			w.write(",");
		}
		w.write("|");
		w.write(unixEOL);

		w.write("rh||ah|Board " + deal.boardNo + "|");

		// sv => side vunerability
		w.write("sv|");

		if (deal.vunerability[Zzz.NS] && deal.vunerability[Zzz.EW])
			w.write("b|");
		else if (deal.vunerability[Zzz.NS])
			w.write("n|");
		else if (deal.vunerability[Zzz.EW])
			w.write("e|");
		else
			w.write("e|");

		// Add the bidding

		w.write(deal.bidsForLinSave());
		w.write(unixEOL);

		// Add the card play

		w.write(deal.cardPlayForLinSave());
		// adds its own unixEOL // w.write(unixEOL);

		// kp => Kibitz Player is an RPf extension - of course someone else may have also used it - eeek
		w.write("kp|" + Zzz.compass_to_nesw_st[deal.youSeatHint].toLowerCase() + "|");
		w.write(unixEOL);

	}

}
