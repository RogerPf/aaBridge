package com.rogerpf.aabridge.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;

public class DepFin {
	// ---------------------------------- CLASS -------------------------------------

	private static String spaces = "                                                               ";

	/**   
	 */
	static public void appendDealInDeepFinesseFormat(Deal deal, BufferedWriter w, int bNumb) throws IOException {
		// ===================================================================================

		String s = "";

		w.write(Zzz.lin_EOL);
		w.write(Zzz.lin_EOL);

		String north = deal.hands[Dir.North.v].cardsForDepFinSave();
		String south = deal.hands[Dir.South.v].cardsForDepFinSave();
		String west = deal.hands[Dir.West.v].cardsForDepFinSave();
		String east = deal.hands[Dir.East.v].cardsForDepFinSave();

		s = "Deal: " + bNumb + " ";
		w.write(s);
		w.write(spaces.substring(0, 40 - s.length()));
		w.write(north);
		w.write(Zzz.lin_EOL);

		String contract = "1NT-South";
		String leadCompass = "";

		if (deal.contract.isValidBid()) {
			contract = deal.contract.toDepFinString() + "-" + deal.contractCompass.toLongStr();
			leadCompass = deal.contractCompass.rotate(1).toLongStr();
		}

		s = "Contract: " + contract + " ";

		String fill = "";

		int fLen = 39 - (s.length() + west.length());

		if (fLen > 0) // almost always true
			fill = spaces.substring(0, fLen);

		s += fill + west;

		int nsMax = (south.length() > north.length()) ? south.length() : north.length();
		fill = spaces.substring(0, nsMax + 2);
		w.write(s + fill + east);
		w.write(Zzz.lin_EOL);

		s = "OnLead: " + leadCompass + " ";
		w.write(s);
		w.write(spaces.substring(0, 40 - s.length()));
		w.write(south);
		w.write(Zzz.lin_EOL);

		w.write("Lead: " + deal.cardPlayForDepFinSave("Any"));
		w.write(Zzz.lin_EOL);

		w.write("Result: ?");
		w.write(Zzz.lin_EOL);

		w.write("Commentary:");
		w.write(Zzz.lin_EOL);

		SimpleDateFormat sdfDate = new SimpleDateFormat(" yyyy-MMMM-dd  HH:mm:ss ");
		String date = sdfDate.format(new Date());

		w.write("This deal was added by aaBridge on  " + date);
		w.write(Zzz.lin_EOL);

		if (App.mg != null && App.mg.lin != null && App.mg.lin.filename != null && App.mg.lin.filename.length() > 4) {
			w.write("Previous filename   " + App.mg.lin.filename);
			w.write(App.deal.lastSavedAsFilename);
			w.write(Zzz.lin_EOL);
		}

		w.write(Zzz.lin_EOL);
	}

	/**   
	 */
	public static int extractLastBoardNumber(BufferedReader reader) throws IOException {
		// ===================================================================================

		int bNumb = 0;
		String s;

		while (true) {
			s = reader.readLine();
			if (s == null)
				break;

			if (s.startsWith("Deal:") && s.length() > 6) {
				s = s.substring(5);
				int i = Aaa.extractPositiveInt(s.trim());
				if (i > 0)
					bNumb = i;
			}
		}
		return bNumb;
	}

	/**   
	 */
	public static class DepFinDeal {
		// ---------------------------------- CLASS -------------------------------------

		String[] hands = { "", "", "", "" };
		String cont = "";
		String decl = "";
		String leader = "";
		String play = "";
		int bNumb = 0;

//		public DepFinDeal() {
//		}
	}

	public static Deal extractLastDeal(BufferedReader reader, Deal appDeal) throws IOException {

		DepFinDeal dfd = null;

		String s;
		int sl;

		while (true) {
			s = reader.readLine();
			if (s == null)
				break;

			if (s.startsWith("Deal:")) {
				dfd = null; // wipe the existing proto deal
				sl = s.length();

				if (sl > 20) {
					s = s.substring(5).trim(); // remome Deal:
					int i = Aaa.extractPositiveInt(s.trim());
					if (i < 1)
						continue;

					dfd = new DepFinDeal();
					dfd.bNumb = i;
					int sta = s.indexOf(' ');
					dfd.hands[Dir.North.v] = s.substring(sta).trim();
				}
			}

			if (dfd == null)
				continue;

			if (s.startsWith("Contract:") && (dfd != null)) {

				sl = s.length();

				if (sl < 35) {
					dfd = null; // wipe the existing proto deal
					continue;
				}

				s = s.substring(9).trim(); // remove Contract:

				int after = s.indexOf(' ');
				dfd.cont = s.substring(0, after);

				int hy = dfd.cont.indexOf('-');
				if (hy > -1) {
					dfd.decl = dfd.cont.substring(hy + 1);
					dfd.cont = dfd.cont.substring(0, hy);
				}

				s = s.substring(after).trim();
				sl = s.length() / 2;

				dfd.hands[Dir.West.v] = s.substring(0, sl).trim();
				dfd.hands[Dir.East.v] = s.substring(sl).trim();
			}

			if (s.startsWith("OnLead:") && (dfd != null)) {
				sl = s.length();

				if (sl < 25) {
					dfd = null; // wipe the existing proto deal
					continue;
				}

				s = s.substring(7).trim(); // remove OnLead:

				int after = s.indexOf(' ');
				dfd.leader = s.substring(0, after);

				dfd.hands[Dir.South.v] = s.substring(after).trim();
			}

			if (s.startsWith("Lead:") && (dfd != null)) {
				sl = s.length();

				dfd.play = s.substring(5).trim();

				if (dfd.play.startsWith("Any")) {
					dfd.play = "";
				}
			}
		}

		if (dfd == null)
			return null;

		Deal deal = new Deal(1 /* this int ignored */);

		deal.displayBoardId = dfd.bNumb + "";
		deal.signfBoardId = "Deal";

		for (Dir dir : Dir.nesw) {
			deal.hands[dir.v].fillHandDepFin(dfd.hands[dir.v]);
		}

		Dir declarer = Dir.Invalid;
		if (dfd.decl.length() > 0) {
			declarer = Dir.directionFromChar(dfd.decl.charAt(0));
		}

		Dir leader = Dir.Invalid;
		if (dfd.leader.length() > 0) {
			leader = Dir.directionFromChar(dfd.leader.charAt(0));
		}

		if (declarer == Dir.Invalid) {
			if (leader == Dir.Invalid)
				declarer = Dir.South;
			else
				declarer = leader.prevAntiClockwise();
		}

		Level level = Level.Invalid;
		Suit suit = Suit.Invalid;
		if (dfd.cont.length() > 1) {
			level = Level.levelFromChar(dfd.cont.charAt(0));
			suit = Suit.charToSuit(dfd.cont.charAt(1));
		}

		if (level == Level.Invalid)
			level = Level.One;

		if (suit == Suit.Invalid)
			suit = Suit.NoTrumps;

		deal.dealer = declarer;

		deal.makeBid(new Bid(level, suit));
		deal.makeBid(new Bid(Call.Pass));
		deal.makeBid(new Bid(Call.Pass));
		deal.makeBid(new Bid(Call.Pass));

		boolean ok = deal.playCardsDepFinExternal(dfd.play);
		if (!ok)
			return null;

		if (deal.ahHeader.startsWith("df: ") == false) {
			deal.ahHeader = "df: " + deal.ahHeader;
		}

		// at this point we believe the constructed deal is GOOD

		// if the existing appDeal matches us for hands, contract & declarer then we can just upate
		// the play in the existing hand and so keep the bidding etc that would otherwise be lost

		if (deal.contractCompass != appDeal.contractCompass)
			return deal;

		if (deal.contract.level != appDeal.contract.level || deal.contract.suit != appDeal.contract.suit)
			return deal;

		if (deal.isOrigCardsSame(appDeal) == false) {
			return deal;
		}

		if (deal.countCardsPlayed() == 0)
			return null;

		Deal dealMerge = appDeal.deepClone();

		dealMerge.wipePlay();

		boolean ok2 = dealMerge.playCardsDepFinExternal(dfd.play);
		if (!ok2)
			return deal;

		if (dealMerge.ahHeader.startsWith("df: ") == false) {
			dealMerge.ahHeader = "df: " + dealMerge.ahHeader;
		}

		return dealMerge;
	}

}
