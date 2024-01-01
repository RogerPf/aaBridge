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
package com.rogerpf.aabridge.igf;

import java.awt.Color;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

import com.rogerpf.aabridge.controller.AaBridge;
import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.controller.Book;
import com.rogerpf.aabridge.controller.Book.LinChapter;
import com.rogerpf.aabridge.controller.Bookshelf;
import com.rogerpf.aabridge.controller.CmdHandler;
import com.rogerpf.aabridge.controller.q_;
import com.rogerpf.aabridge.igf.MassGi.GraInfo;
import com.rogerpf.aabridge.model.Bid;
import com.rogerpf.aabridge.model.Card;
import com.rogerpf.aabridge.model.Deal;
import com.rogerpf.aabridge.model.Dir;
import com.rogerpf.aabridge.model.Hand;
import com.rogerpf.aabridge.model.Lin;
import com.rogerpf.aabridge.model.Zzz;
import com.rogerpf.aabridge.util.Util;
import com.version.VersionAndBuilt;

/**
 * Under Windows (and other OSes?) when you launch a new process or a run exec something
 * Java expects you to read any output that is spewed into stderror or stdio IF YOU DONT the 
 * PROCESS will STOP and wait for you to consume it -  Gobbler conusmes just such stuff
 */
class StreamGobbler extends Thread {
	// ---------------------------------- CLASS -------------------------------------
	InputStream is;
	String type;

	StreamGobbler(InputStream is, String type) {
		// ==========================================================================
		this.is = is;
		this.type = type;
	}

	public void run() {
		// ==========================================================================
		try {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			@SuppressWarnings("unused")
			String line = null;
			while ((line = br.readLine()) != null) {
				// System.out.println(type + "> " + line);
			}
		} catch (IOException ioe) {
			// ioe.printStackTrace();
		}
	}
}

class externalUrlLauncher extends Thread {
	// ---------------------------------- CLASS -------------------------------------
	String url;
	boolean loadIfLin;

	externalUrlLauncher(String url, boolean loadIfLin) {
		// ==========================================================================
		this.url = url;
		this.loadIfLin = loadIfLin;
	}

	public void run() {
		// ==========================================================================
		MassGi_utils.launchLinkViaItsOwnThread(url, loadIfLin);
	}

}

public class MassGi_utils {

	/**
	 */
	public static int ans_lb_ptlsd_points(Deal d, char qType) {
		// =============================================================================
		// @formatter:off
		int points = 0;
		switch (qType) {
			case 'p':
				points =   d.hands[Dir.South.v].count_HighCardPoints();
				break;
			case 't':
				points =   d.hands[Dir.South.v].count_HighCardPoints()
				         + d.hands[Dir.South.v].count_LongSuitPoints();
				break;
			case 'l':
				points =   d.hands[Dir.South.v].count_LongSuitPoints();
				break;
			case 's':
				points =   d.hands[Dir.South.v].count_ShortSuitPoints();
				break;
			case 'd':
				points =   d.hands[Dir.South.v].count_HighCardPoints()
				         + d.hands[Dir.South.v].count_ShortSuitPoints();
				break;
		}
		return points;
		// @formatter:on
	}

	public static void do_dealmodeBackToMovie() {
		// ==========================================================================

		/** So we must want to transition  into 'tutorial' aka movie mode
		 */
		App.setMode(Aaa.NORMAL_ACTIVE);
		App.setVisualMode(App.Vm_DealAndTutorial);

		/* 2020 Nov 09 */
		boolean pos_restore_hidden = (App.mg.lin.linType != Lin.FullMovie); // && !App.isStudyDeal() && !App.sd_dev_visibility;
		if (pos_restore_hidden && App.localShowHiddPolicy == 0 /* Hide */) {
			App.localShowHidden = false; // so we still DONT show the normally hidden hands
		}

		App.gbp.matchPanelsToDealState();
		App.mg.setTheReadPoints(App.mg.stop_gi, false /* not used */);
		if (App.mg.lin.linType == Lin.VuGraph) {
			App.deal.youSeatHint = App.youSeatHint = Dir.South;
		}

		App.frame.repaint();
	}

	static String unHidEOL(String hid, String eol_or_blank) {
		return hid.replace("\\", eol_or_blank + "\\");
	}

	static int tourney_vul_numb = 1;

	/**   
	 */
	static public String getDealAsLinSave_BBOorSTD(Deal deal, String kib_seat, int new_number, boolean first_of_set, boolean single_deal_save) {
		// ===================================================================================

		boolean bbo = (App.linfileSaveFormat == App.linFmt_BBO);
		boolean stripped = bbo && App.bboUpStripped;
		boolean tourney = App.multiSaveBboTourny;

		tourney_vul_numb = first_of_set ? 1 : ++tourney_vul_numb;

		String eol_or_blank = bbo ? "" : Zzz.get_lin_EOL();
		String extra_space = stripped ? "" : (bbo ? " " : "");
		String add_o = bbo ? "o" : "";
		String pg_bars = "pg|" + extra_space + "|";

		// String signifier = ""; 

		String local_displayId;
		int v;
		if (new_number > 0) {
			local_displayId = new_number + "";
		}
		else if (deal.qx_number > 1) {
			local_displayId = deal.qx_number + "";
		}
		else if ((v = Aaa.extractPositiveInt(deal.displayBoardId)) > 1) {
			local_displayId = (bbo) ? (v + "") : deal.displayBoardId;
		}
		else {
			local_displayId = "1";
		}

		String signfBoardId = App.deal.signfBoardId.isEmpty() ? "Board" : App.deal.signfBoardId;
		String dispBoardId = App.deal.displayBoardId;
		String possHash = "";

		if (bbo) {
			signfBoardId = "Board";
			dispBoardId = local_displayId;
		}
		else if (deal.signfBoardId_is_hash_sig) {
			possHash = "#";
		}
		else if (signfBoardId.isEmpty()) {
			signfBoardId = "Board";
			dispBoardId = local_displayId;
		}

		String out = "qx|" + add_o + local_displayId + "|";

		if (App.discardPlayerNames == false) {
			out += deal.PlayerNamesForLinFile(extra_space);
		}
		out += eol_or_blank;

		// Headers and our invented Display Board Number
		out += "rh||";

		String ahText = possHash + signfBoardId + " " + dispBoardId + " ";
		deal.ahHeader.trim();
		deal.ahHeadHid.trim();

		if (deal.ahHeader.isEmpty() == false && App.bboUpStripped == false) {
			Aaa.cleanString(deal.ahHeader, true /* true => spaceOk */);
			ahText += " " + deal.ahHeader + " ";
		}

		if (deal.ahHeadHid.isEmpty() == false && App.discardPlayerNames == false) {
			ahText += deal.ahHeadHid.replace("\\", eol_or_blank + "\\");
			if (eol_or_blank.isEmpty())
				ahText += " ";
		}

		out += "ah|" + ahText + "|" + eol_or_blank;

		out += "md|"; // md => make deal

		// now as the **** first character of the South hand defintion **** we write the dealer id
		// for lin '1'=South, '4'=East, aaBridge internal 2=South, 1=East

		int dealer_int = deal.dealer.v;

		out += "" + (char) (((dealer_int + 2) % 4 + 1 + '0'));

		// note we might want East's hand for manual editing so NOT omitted

		out += deal.cardsForLinSave(false /* all hands */);
		out += "|";
		out += eol_or_blank;

		String bh_bi = stripped ? "" : deal.botExtra.getBotHintsAndInstructionsAsLinSave(eol_or_blank, extra_space);

		if (bh_bi.isEmpty() == false) {
			out += (bbo ? "" : "%");
			out += bh_bi;
			out += (bbo ? "" : "%") + eol_or_blank;
		}

		if (single_deal_save == false && stripped == false) {
			if (deal.zd_dealer_script.isEmpty() == false) {
				out += "zd|" + deal.zd_dealer_script + "|" + eol_or_blank;
			}

			if (deal.zg_merge_list.isEmpty() == false) {
				out += "zg|" + deal.zg_merge_list + "|" + eol_or_blank;
			}
		}

		// sv => side vulnerability
		String vulLetter = "";
		if (tourney) {
			vulLetter = Deal.getSingleVulnerabilityLetterFromBoardNumber(tourney_vul_numb);
			// int ax = deal.dealer.v % 2;
			if (((deal.dealer.v + (tourney_vul_numb - 1)) % 2) == 1) {
				if (vulLetter.contentEquals("e"))
					vulLetter = "n";
				else if (vulLetter.contentEquals("n"))
					vulLetter = "e";
			}
		}
		else {
			vulLetter = deal.getSingleVulnerabilityLetter();
		}

		out += "sv|" + vulLetter + "|";

		String kib = "";

		boolean kibSeatValid = (Dir.directionFromChar((kib_seat + ' ').charAt(0)) != Dir.Invalid);

		if (kibSeatValid) {
			kib = kib_seat;
		}
		else if (deal.youSeatHint != Dir.Invalid) {
			kib = "" + deal.youSeatHint.toLowerChar();
		}

		out += "sk|" + kib + "|";

		if (kib.isEmpty() == false) {
			out += "sk||";
		}

		out += pg_bars;
		out += eol_or_blank;

		// Add the bidding
		if (App.saveNoBidOrPlay == false && deal.countBids() > 0) {
			out += deal.bidsForLinSave();
			out += pg_bars;
			out += eol_or_blank;
		}

		// Add the card play
		if (App.saveNoBidOrPlay == false) {
			out += deal.cardPlayForLinSave(0 /* stage 0, 1-3 */);
			// adds its own Zzz.get_lin_EOL() if / as needed

			if (deal.endedWithClaim && (App.saveOnlyTheLead == false)) {
				out += "mc|" + deal.tricksClaimed + "|" + pg_bars + eol_or_blank;
			}
		}

		out += Zzz.get_lin_EOL();

		if (App.bboUpStripped == false) {
			out += Zzz.get_lin_EOL();
		}

		return out;
	}

	/**   
	 */
	static public String getDealAsLinSave_Problem(Deal deal, String kib_seat, int new_number) {
		// ===================================================================================		

		String clearAllButNotTopLeft = "ht|e|n^|3|at|^e@5@4^z@3^b@2^^|ht|e|n^|5|at|^e|";

		boolean bTest = App.prob_force_comments;

		String pling_test = bTest ? "|" : "!";
		String barbar_test = bTest ? "||" : "";

		String EOL = Zzz.get_lin_EOL();

		String ahText = Aaa.cleanString(deal.ahHeader, true /* true => spaceOk */);

		boolean omitOpps = (App.linfileSaveFormat == App.linFmt_PrNoOp) || (deal.countOriginal_defender_cards() < 26);

		boolean saveAsStudyDeal = ahText.toLowerCase().contains("zm") && (omitOpps == false);

		String local_displayId;

		if (new_number > 0) {
			local_displayId = new_number + "";
		}
		else if (deal.qx_number > 1) {
			local_displayId = deal.qx_number + "";
		}
		else if (Aaa.extractPositiveInt(deal.displayBoardId) > 1) {
			local_displayId = deal.displayBoardId;
		}
		else {
			local_displayId = "1";
		}

		String out = "-------------------------------------------------" + EOL;

		out += "qx|" + local_displayId + "|";  // no player names

		out += "bv|a,h|" + EOL;

		// Headers with Problem Number
		out += "rh||";

		{ // useful for extra info ? could come out 
			if (!ahText.isEmpty()) {
				out += "ah|" + ahText.trim() + "|";
			}
		}

		out += "ah|Problem " + local_displayId + "|" + EOL;
		out += "nt|^b@2^^|n^|5|at|^*bProblem " + local_displayId + "^*n ^e the hand,      |" + EOL;
		if (saveAsStudyDeal) {
			out += "zm|+study+|" + EOL;
		}

		// now as the **** first character of the South hand definition **** we write the dealer id
		// for lin files  '1'=South, '4'=East,   aaBridge internal 2=South, 1=East
		String dlrChar = "" + (char) (((deal.dealer.v + 2) % 4 + 1 + '0'));

		// We always output the FULL deal and smother it later if  omitOpps is set
		out += "md|" + dlrChar + deal.cardsForLinSave(false /* don't omitOpps */) + "|" + EOL;

		if (omitOpps) {
			// 2020-07-21 --- we can ALSO output the no opps hand which when read hides the actual cards.
			out += "md|" + dlrChar + deal.cardsForLinSave(omitOpps) + "|" + EOL;
		}

		out += "%";
		String bh_bi = deal.botExtra.getBotHintsAndInstructionsAsLinSave(EOL, "" /* = never add extra space */);
		if (bh_bi.isEmpty()) {
			out += EOL + "bh!Play, W-E, sH, res, on-lead, T3-7, always!    %% EXAMPLE bot hint  (disabled)" + EOL;
		}
		else {
			out += bh_bi;
		}
		out += "%" + EOL;

		// sv => side vulnerability
		out += "sv|" + deal.getSingleVulnerabilityLetter() + "|";

		// we ignore supplied and always set our own kib_seat
		kib_seat = (deal.contractCompass == Dir.Invalid) ? "" : deal.contractCompass.toLowerChar() + "";

		if (kib_seat.length() > 0) {
			out += "sk|" + kib_seat + "|";
		}

		out += "pg||" + EOL;
		if (omitOpps) {
			out += "aa|y|" + EOL;
		}

		out += "at|^h the bidding,|" + EOL;

		// Add the bidding
		if (deal.countBids() > 0) {
			out += deal.bidsForLinSave() + "pg||" + EOL;
		}

		// Add the card play
		out += "at|^k    the lead, ^n|" + EOL;
		out += deal.cardPlayForLinSave(1 /* stage 0, 1-3 */ );

		// int cardsPlayed = deal.countCardsPlayed();

		String s2 = deal.cardPlayForLinSave(2 /* stage 0, 1-3 */);
		if (s2.isEmpty() == false) {
			out += "at|^n   the early play, ^r|" + EOL;
			out += s2;
		}
		out += "at|    ^*b think NOW^*n         |" + EOL;
		out += "%%" + EOL;
		out += "%%  below, change the two  !  to   bars  show the  'an intro follows'   text." + EOL;
		out += "%%" + EOL;
		out += "at" + pling_test + " an intro follows" + pling_test + EOL;
		out += "pg||" + EOL + EOL;

		out += clearAllButNotTopLeft + EOL;
		out += "at|^*b Introduction^*n|" + EOL;
		out += "lg|c|n#|3|" + EOL;
		out += "at|^^This example intro text will not be seen unless "; // no EOL
		out += "you edit the  'pg'  below and put 2 bars after it.  "; // no EOL
		out += "You can also copy this block of lines to make a second  'intro'  page.|" + EOL;
		out += "n#|5|at|^^By using the illustrated commands "; // no EOL
		out += "you can space out the longer passages, so making it easier for "; // no EOL
		out += "your readers to read the test. |" + EOL;
		out += "pg" + barbar_test + EOL + EOL;  // no bar bar

		out += clearAllButNotTopLeft + EOL;
		out += "ht|m|at|^e ^*bClick^*n  the blue  { ^*b 1st ^*n }  button below to|" + EOL;
		out += "cp|red|at|^*b  play out the hand^*n|cp||" + EOL;
		out += "at|.     ^*b Flow ^*n   will |" + EOL;
		out += "%%" + EOL;
		out += "%%  Edit the text  'end the problem'  below to match what happens in YOUR problem." + EOL;
		out += "%%" + EOL;
		if (bTest) {
			out += "at  end the problem." + EOL;
			out += "at| show a ^*b Comment^*n.|" + EOL;
		}
		else {
			out += "at| end the problem.|" + EOL;
			out += "at  show a ^*b Comment^*n." + EOL;
		}
		out += "%%" + EOL;
		out += "pf|first-button|" + EOL;
		out += "bv|f,v|" + EOL;
		out += "pg||" + EOL + EOL;

		out += clearAllButNotTopLeft + EOL;
		out += "at|^*b Comment^*n  |" + EOL;
		out += "lg|c|n#|3|" + EOL;
		out += "at|^^You have just had an opportunity to play the hand and still can.  "; // no EOL
		out += "The core of the problem is .... |" + EOL;
		out += "n#|3|at|^^This example  'comment'  text will not be seen "; // no EOL
		out += "unless you edit the  'pg'  below and put 2 bars after it.  "; // no EOL;
		out += "You can copy this block of lines to make a second  'comments'  page.  "; // no EOL;
		out += "You can also put such blocks between any additional card play. |" + EOL;
		out += "pf|first-button|" + EOL;
		out += "bv|f,v|" + EOL;
		out += "pg" + barbar_test + EOL + EOL;  // no bar bar

		String s3 = deal.cardPlayForLinSave(3 /* stage 0, 1-3 */);
		if (s3.isEmpty() == false) {
			out += clearAllButNotTopLeft + EOL;
			out += "sk||" + EOL;
			out += "ht|m|at|^e Play follows ...|" + EOL;
			out += "bv|f,v|" + EOL;
			out += "pf|first-button|" + EOL;
			out += "pg||" + EOL + EOL;

			out += "%%_aapg-NEAR" + EOL;
			out += s3;  // s3 has an ending EOL already
			out += "%%_aapg-STD" + EOL + EOL;
		}

		String L1 = "m";
		String L3 = "u";

		if (saveAsStudyDeal) {
			out += "zm|-study-|" + EOL;
		}

		out += clearAllButNotTopLeft + EOL;
		out += "sk||";
		out += "ht|" + L1 + "|at|^e Complete   -   to play out any remaining ";
		out += "cards ^*b Click ^*n the blue  { ^*b Cont ^*n }  button.|" + EOL;

		if (saveAsStudyDeal) {
			out += "ht|" + L3 + "|at|^c'Study Deal'  restrictions lifted.  "; // no EOL
			out += "So, in the deal the  ^*b DDS  { Off/On ^*n }  buttons, "; // no EOL
			out += "etc are |cp|red|at|^*b NOW ^*n|cp||at| available.|" + EOL;
		}

		out += "pf|both-first-cont|" + EOL;
		out += "bv|c,v|" + EOL;
		out += "pg||" + EOL + EOL;

		out += "sk||eb||" + EOL;
		out += clearAllButNotTopLeft + EOL;
		out += "lg|c|ht|j|at|^c|" + EOL;
		out += "%%" + EOL;
		out += "%%  any final comments can go in the 'at' below" + EOL;
		out += "at|" + EOL;
		out += "|" + EOL;
		out += "ht|w|at|^b^*bEnd^*n|" + EOL;
		out += "pf|rem-button|" + EOL;
		out += "bv|a,v|" + EOL;
		out += "pg||" + EOL + EOL;

		out += "bv|a,h|md|1,,,|nt||n^|5|at|^^^bcleared|sk||pg||" + EOL;
		out += "" + EOL;
		out += "" + EOL;

		return out;
	}

	public static String getDealAsLinSave(Deal deal_in, String kibSeat, int new_number, boolean first_of_set, boolean single) {
		// =============================================================================

		Deal deal = deal_in.deepClone();

		if (App.linSaveRotateDecSouth) {
			if (deal.contractCompass != Dir.South && deal.contractCompass != Dir.Invalid) {
				int rot = (4 + 2 - deal.contractCompass.v) % 4;
				while (rot-- > 0) {
					deal.rotateHands(1);
				}
			}
			deal.youSeatHint = Dir.South;
		}

		if (App.linSaveRotateDecSouth && App.alsoDealerSouth) {
			deal.adjustDealerToSouthWithPasses();
		}

		if (App.includeRotationsSetBelow) {
			int rotate = App.fixedQuarterTurns + App.linSaveUglyRotationCount % 4;
			App.linSaveUglyRotationCount += App.rotateWhenSaving; // for next time
			while (rotate-- > 0) {
				deal.rotateHands(1);
			}
		}

		if (App.saveOnlyTheLead) {
			deal.compressBiddingToMinimum();
		}

		if (App.linfileSaveFormat >= App.linFmt_Prob)
			return getDealAsLinSave_Problem(deal, kibSeat, new_number);
		else
			return getDealAsLinSave_BBOorSTD(deal, kibSeat, new_number, first_of_set, single);
	}

	private static String make_problem_format_prepend(int count) {
		// =============================================================================
		int prob_fmt_req_number = VersionAndBuilt.getProbFmtReqNumber();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date = sdf.format(new Date());

		String EOL = Zzz.get_lin_EOL();

		String plu = (count > 1) ? "s" : "";
		String verb = (count > 1) ? "were" : "was";

		String s = "%%" + EOL + "%%  Tell users of old win player they need aaBridge" + EOL;
		s += "bt||          %%" + EOL;
		s += "mn|This  Movie  requires the  aaBridge  player|" + EOL;
		s += "ht|j|at|^c This  Bridge Movie  requires the ^*b aaBridge ^*n player available  FREE  from |" + EOL;
		s += "cb|8255|cp|8|ht|L|at|   ^i  ^*hrogerpf.com/aaBridge^*n|" + EOL;
		s += "ip||          %%  non aaBridge  lin file players   STOP  on this  'ip'  command" + EOL;
		s += "mn||nt||  %%  Clean up the above mess  - turn off mn headers and  CLEAR THE SCREEN" + EOL;
		s += "bt||          %%" + EOL + EOL + EOL;

		s += "%%" + EOL;
		s += "bt||mn|" + count + " Deal" + plu + " in Problem Format|" + EOL;
		s += "%%" + EOL;
		s += "fh|b45|cr|p255|cg|p0|cb|p255|ht|e|n^|3|at|^b@2^^|";
		s += "lg|5|at|The " + count + " deal" + plu + " included in this .lin file " + verb + " saved in  'Problem'  format on ";
		s += "  " + date + ".^^^^The original .lin file was called:|fp|b|" + EOL;
		s += "at|^e@2" + EOL + EOL;

		s += App.mg.lin.filename.replace("#", "##") + EOL + EOL; // original filename

		s += "|fp||lg|m|n^|7|at|^b^*bInstructions^*n";
		s += "^^^c@2Click the  { ^*b Flow ^*n }  button and ^*b WHEN TOLD ^*n click the  { ^*b 1st ^*n }  button.";
		s += "^^When you ^*n click the  { ^*b 1st ^*n }  button you will go  'inside the deal'.   Like Alice going 'down the rabbit hole'.";
		s += "^^To return to the level of this  'Bridge Movie'  you click the  { ^*b Return to Movie ^*n }  button.|";

		s += "at|^^^^^b@2^*bWhen you are  'Inside a Deal'^*n ^^^c@2You can Play against the aaBridge 'bots'.";
		s += "   |cp|red|at|^*bClick on a card to play it.^*n|cp||at|    If problems click  { ^*b Play ^*n }  on the left.";
		s += "^^{ ^*b Shuf Op ^*n }  (top left),  will shuffle the Opps cards and|cp|green|at|^*b  will  ADD  them, if none are there^*n|cp||at|.";
		s += "^^{ ^*b Analyse ^*n }  (top right), will show you all the possible contracts and who can make what.";
		s += "^^^*bDDS  { is Off/On ^*n }  (top right),  the light green ovals on each card show the best play(s) for that side.";
		s += "^^|n#|a|lg||at|^^@1^b^*bBefore you start    ^*n@-    Please click the  |fp|b|cp|p|n^|2|at|@.|n#|2|cp||fp||";
		s += "at| { ^*b Apply Defaults ^*n }  button  in the right hand column.|";
		s += "n#|3|at|^^^^To start click  { ^*b Flow ^*n }    OR   (recommended)   use your ^*b Mouse Wheel ^*n /  Mac Magic Mouse  ";
		s += "'invisible wheel'.|fh|940|fm|9|fp|9|at|@0@1|ht|i|rq|" + prob_fmt_req_number;
		s += "|at|@4^^^^^^^^^^ ^- This   Bridge Movie   requires a more recent version of  ^*b aaBridge^*n.     ^^^^ ";
		s += "Your copy of ^*b aaBridge ^*n is out of date.    You can get the latest version   ^^^^  FREE   from    ";
		s += "^*h rogerpf.com/aaBridge ^*n ^^^^   click the link above to visit. ^-|" + EOL;
		s += "qx|Prob|" + EOL + "pg||" + EOL + "st||" + EOL + EOL + EOL;

		return s;
	}

	public static void multiDealSaveAsLin(boolean separate) {
		// =============================================================================

		MassGi mg = App.mg;

		int size = mg.giAy.size();

		App.linSaveUglyRotationCount = 0;

//		String info_text = "Originally from file " + mg.lin.filename;

		boolean renumber = (App.renumberDealsLin); // || ((App.linfileSaveFormat == App.linFmt_BBO) && App.multiSaveBboTourny);

		int new_number = 0;

		ArrayList<String> deals_str = new ArrayList<String>();

		Deal deal_Cand = null;
		int score_Cand = 0;

		int saved = 0;

		for (int i = 1; i < size; i++) {
			GraInfo gi = mg.giAy.get(i);
			if (gi.qt != q_.pg)
				continue;

			Deal deal = gi.deal;

			if (deal_Cand == null) {
				deal_Cand = deal;
				continue;
			}

			if (deal_Cand.countOrigCards() == 0) {
				// our current candidate has no cards dealt so dump it
				deal_Cand = deal;
				score_Cand = deal.completenessScore();
				continue;
			}

			int comp_res = deal.coreCompare(deal_Cand);

			if (comp_res < 2 /*  < 2 means not equal inc cards removed but no cards added */) {
				if (renumber)
					new_number++;
				deals_str.add(getDealAsLinSave(deal_Cand, "", new_number, (++saved == 1) /* first of a set */, false /* single */));

				deal_Cand = deal;
				score_Cand = deal.completenessScore();
				continue;
			}

			int score = deal.completenessScore();

			if (score > score_Cand) {
				deal_Cand = deal;
				score_Cand = score;
				continue;
			}
		}
		if (deal_Cand != null && (deal_Cand.countOrigCards() > 0)) {
			if (renumber)
				new_number++;
			deals_str.add(getDealAsLinSave(deal_Cand, "", new_number, (++saved == 1) /* first of a set */, false /* single */));
		}

		if (deals_str.size() == 0) {
			return; // no deals to save
		}

		if (separate) {
			String mutli_fldr = "0000 0000 separate";

			String fldr = mg.lin.getMultiDealDestFolder() + mutli_fldr + File.separator;
			{
				File fl = new File(fldr);
				fl.mkdir();
				File[] listOfFiles = fl.listFiles();
				for (File file : listOfFiles) {
					if (file.isFile()) {
						file.delete();
					}
				}
			}

			for (int i = 0; (i < deals_str.size()); i++) {
				String s = "";
				if (App.linfileSaveFormat >= App.linFmt_Prob) {
					s = make_problem_format_prepend(1 /* as separate */);
				}
				s += deals_str.get(i);
				String v = "00" + (i + 1);
				String multi_id = "___" + v.substring(v.length() - 3);
				String fn = mg.lin.getMultiDealSaveAs_filename("lin", mutli_fldr, multi_id);
				save_lin_inner(fn, s);
			}
		}
		else {  // normal - the deals are combined into one lin file

			String fn = mg.lin.getMultiDealSaveAs_filename("lin", "", "");
			String s = "";
			if (App.linfileSaveFormat >= App.linFmt_Prob) {
				s = make_problem_format_prepend(deals_str.size());
			}
			for (int i = 0; (i < deals_str.size()); i++) {
				s += deals_str.get(i);
			}
			save_lin_inner(fn, s);
		}

	}

	private static void save_lin_inner(String fn, String content) {
		// =============================================================================

		try {

			FileWriter fw = new FileWriter(fn);
			BufferedWriter bw = new BufferedWriter(fw);

			bw.write(content);
			bw.write(Zzz.get_lin_EOL());

			bw.flush();
			bw.close();
			fw.close();

		} catch (IOException e) {
			// int z =0;
		}

	}

	public static void multiDealSaveAsPbn_noCardPlay() {
		// =============================================================================

		MassGi mg = App.mg;

		Deal lastWritten = null;

		int size = mg.giAy.size();

		int count = 0;

		ArrayList<String> ay = new ArrayList<String>(1000);

		int saved = 0;

		int new_number = (App.renumberDealsLin) ? 1 : 0;

		for (int i = 0; i < size; i++) {
			GraInfo gi = mg.giAy.get(i);
			Deal deal = gi.deal;
			if (deal == null || deal.isDoneHand())
				continue;

			if (deal.countOrigCards() < 1)
				continue;

			if (lastWritten == deal)
				continue;

			if (lastWritten != null && lastWritten.pbnEqualTo(deal))
				continue;

			deal.writePbnToSaveableArray(ay, new_number, ++saved == 1);

			if (App.renumberDealsLin)
				new_number++;

			lastWritten = deal;
			count++;
		}

		if (count == 0)
			return;

		String fn = mg.lin.getMultiDealSaveAs_filename("pbn", "", "");

		try {
			FileWriter fw = new FileWriter(fn);
			BufferedWriter bw = new BufferedWriter(fw);

			for (String s : ay) {
				bw.write(s);
				bw.write(Zzz.get_lin_EOL());
			}

			bw.flush();
			bw.close();
			fw.close();

		} catch (IOException e) {
		}
	}

	public static void do_tutorialIntoDealStd() {
		// ==========================================================================

		if (App.visualMode != App.Vm_DealAndTutorial)
			return;

		/** We are in a tutorial mode and wish to examine the current App.deal	
		 */
//		if (App.deal.has52Cards() == false)
//			return;

		App.ddsScoreShow = false;

		if (App.dlaeActive) { // full complex tutorials do their own thing
			App.deal.youSeatHint = App.deal.contractCompass.rotate(App.dlaeValue - 2);
		}

		App.setVisualMode(App.Vm_InsideADeal);

		App.reviewBid = 0;
		App.reviewTrick = 0;
		App.reviewCard = 0;

		if (App.deal.isBidding() || App.reviewFromPlay == false) {
			App.setMode(Aaa.REVIEW_BIDDING);
		}
		else {
			App.setMode(Aaa.REVIEW_PLAY);

			if ((App.reviewTrick * 4 + App.reviewCard) < App.deal.eb_min_card) {
				App.reviewTrick = App.deal.eb_min_card / 4;
				App.reviewCard = App.deal.eb_min_card % 4;
			}
			else if ((App.deal.countCardsPlayed() > 0) && App.showOpeningLead && !App.deal.suppress_autoshow_opening_lead) {
				App.reviewCard = 1;
			}
		}

		if (App.localShowHiddPolicy != 2) {
			App.localShowHidden = (App.localShowHiddPolicy == 1);
		}

		if (!App.dealEnteredOnce) {
			App.dealEnteredOnce = true;
			App.frame.executeCmd("rightPanelPrefs2_KibSeat");
			App.frame.repaint();
		}

		if ((App.mg.lin.linType == Lin.FullMovie) && App.showRedEditArrow) {
			App.gbo.showEditHint();
		}

	}

	public static void do_tutorialIntoDealClever() {
		// ==========================================================================

		if (App.visualMode != App.Vm_DealAndTutorial)
			return;

		/** We are in a tutorial mode and wish to examine the current App.deal
		 *  First we do some safety checks	
		 */
//		if ((App.isLin__Single() == false) && (App.deal.has52Cards() == false))
//			return; /* the user needs to be on deal that can be entered */

		MassGi mg = App.mg;

		int pg_org = mg.end_pg;
		Deal pg_orgDeal = mg.giAy.get(pg_org).deal;
		Deal ap_deal = App.deal;

		if (ap_deal.localId != pg_orgDeal.localId) {
			@SuppressWarnings("unused")
			int z = 0;
			assert (false);
			return;
		}

		int pg;

		/* Firstly we find a forwards candidate */

		int pg_fwdCand = pg_org;
		Deal deal_fwdCand = pg_orgDeal;
		int score_fwdCand = pg_orgDeal.completenessScore();

		pg = pg_fwdCand;
		for (int i = 0; i < 100; i++) {
			pg = mg.getNextPg(pg);
			GraInfo gi = mg.giAy.get(pg);
			Deal deal = gi.deal;
			if (deal_fwdCand.countOrigCards() == 0) {
				// our current candidate has no cards dealt so skip it
				deal_fwdCand = deal;
				continue;
			}
			if (deal_fwdCand.coreEqualTo(deal) == false)
				break;
			int score = deal.completenessScore();
			if (score > score_fwdCand) {
				deal_fwdCand = deal;
				score_fwdCand = score;
				pg_fwdCand = pg;
				continue;
			}
			if (score < score_fwdCand) {
				break;
			}
		}

		/* Secondly we find a backwards candidate */

		int pg_bckCand = pg_org;
		Deal deal_bckCand = pg_orgDeal;
		int score_bckCand = pg_orgDeal.completenessScore();

		pg = pg_bckCand;
		for (int i = 0; i < 100; i++) {
			pg = mg.getPrevPg(pg);
			GraInfo gi = mg.giAy.get(pg);
			Deal deal = gi.deal;
			if ((deal.eb_min_card > 0) && (deal.eb_min_card >= deal.countCardsPlayed()))
				break;
			if (deal_bckCand.coreEqualTo(deal) == false)
				break;
			int score = deal.completenessScore();
			if (score > score_bckCand) {
				deal_bckCand = deal;
				score_bckCand = score;
				pg_bckCand = pg;
				continue;
			}
			if (score < score_bckCand) {
				break;
			}
		}

		Deal best_cand = (score_bckCand > score_fwdCand) ? deal_bckCand : deal_fwdCand;

		// By not setting the readpoints we keep our position in the movie
		// App.mg.setTheReadPoints(pg_cand, false /* not_used */);

		// So we must make our own clone to stop the original being changed by "Play"
		App.deal = best_cand.deepClone();

		// App.deal.setDealShowXes(null);

		App.calcCompassPhyOffset();
		App.dealMajorChange();
		do_tutorialIntoDealStd();

		App.gbp.matchPanelsToDealState();
	}

	public static void do_tutorialIntoDealB1st() {
		// ==========================================================================
		if (App.visualMode != App.Vm_DealAndTutorial)
			return;

		/** We are in a tutorial mode and wish to examine the current App.deal
		 *  First we do some safety checks	
		 */
//		if (App.deal.has52Cards() == false)
//			return; /* the user needs to be on deal that can be entered */

		boolean old_fromPlay = App.reviewFromPlay; // save policy

		App.reviewFromPlay = true;
		do_tutorialIntoDealClever();
		App.reviewFromPlay = old_fromPlay; // restore policy

		if (App.deal.isContractReal() == false) {
			App.show_poor_def_msg = false;
		}
		else {
			App.show_poor_def_msg = true;

			if (App.deal.eb_min_card == 0) {
				App.reviewTrick = 0;
				App.reviewCard = App.deal.suppress_autoshow_opening_lead ? 0 : 1;
			}

			CmdHandler.leftWingNormal();

			App.gbp.c1_1__tfdp.clearShowCompletedTrick();

			App.setMode(Aaa.NORMAL_ACTIVE);
			App.gbp.c1_1__tfdp.makeCardSuggestions();
			App.gbp.matchPanelsToDealState();
		}

		if (App.deal.has52Cards() && App.study_deal_maker == false)
			App.localShowHidden = false; // policy override - start hidden
//		else
//			App.localShowHidden = true; // As it is a fragment we show it to them
	}

	public static void do_tutorialIntoDealContdds() {
		// ==========================================================================

		if (App.cont_button_is_cont__cashed) {
			do_tutorialIntoDealCont();
		}
		else {
			do_tutorialIntoDealB1st();
			if (App.ddsScoreShow == false)
				CmdHandler.ddsScoreOnOff();
		}
	}

	public static void do_tutorialIntoDealCont() {
		// ==========================================================================
		if (App.visualMode != App.Vm_DealAndTutorial)
			return;

		/** We are in a tutorial mode and wish to examine the current App.deal
		 *  First we do some safety checks	
		 */
//		if (App.deal.has52Cards() == false)
//			return; /* the user needs to be on deal that can be entered */

		boolean old_fromPlay = App.reviewFromPlay; // save policy

		App.reviewFromPlay = true;
		do_tutorialIntoDealClever();
		App.reviewFromPlay = old_fromPlay; // restore policy

		if (App.deal.isContractReal() == false) {
			App.show_poor_def_msg = false;
		}
		else {
			App.show_poor_def_msg = true;

			App.reviewTrick = 13;
			App.reviewCard = 3;

			CmdHandler.leftWingNormal();

			App.gbp.c1_1__tfdp.clearShowCompletedTrick();

			App.setMode(Aaa.NORMAL_ACTIVE);
			App.gbp.c1_1__tfdp.makeCardSuggestions();
			App.gbp.matchPanelsToDealState();
		}

		if (App.deal.has52Cards() && App.study_deal_maker == false)
			App.localShowHidden = false; // policy override - start hidden
//		else
//			App.localShowHidden = true; // As it is a fragment we show it to them
	}

	public static void launch_2nd_aaBridge_WITH(String[] args) {
		// ==========================================================================
		ArrayList<String> log = new ArrayList<String>(); // ultra simple log maker

		launch_2nd_aaBridge_WITH_inner(log, args);

		// save the results to a log file
		try {
			String logFilePath = App.temp_Other_folder + "aaBridge__launch_lin_file__log.txt";
			FileWriter fw = new FileWriter(logFilePath);
			BufferedWriter bw = new BufferedWriter(fw);
			for (String line : log) {
				bw.write(line);
				bw.newLine();
			}
			bw.flush();
			bw.close();
			fw.close();

			// Desktop.getDesktop().open(new File(logFilePath)); // for testing only

		} catch (Exception e) {
		}
	}

	public static boolean isRunningExpanded() {
		// ==========================================================================
		try {
			URL locationMethodUrl = AaBridge.class.getProtectionDomain().getCodeSource().getLocation();
			File locMethodFile = new File(locationMethodUrl.toURI());

			String runTargetNameLower = locMethodFile.getName().toLowerCase();

			if (runTargetNameLower.endsWith(".jar")) {
				// We are running in a .jar on either windows, mac or linux or ...
				return false;
			}
			else if (runTargetNameLower.endsWith(".exe")) {
				// We are running in .exe on windows
				return false;
			}
			return true;

		} catch (Exception e) {
		}

		return false;
	}

	public static void launch_2nd_aaBridge_WITH_inner(ArrayList<String> log, String[] args) {
		// ==========================================================================

		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd__HH-mm-ss");

		log.add("Go into deal - button clicked - " + sdfDate.format(new Date()));

		String[] commands = { "", "", "", "", "", "", };

		/**
		 * Where is our own 'java' code  i.e. 'us'  is located? - so we can launch another instance
		 */

		URL locationMethodUrl = AaBridge.class.getProtectionDomain().getCodeSource().getLocation();
		File locMethodFile = null;
		try {
			locMethodFile = new File(locationMethodUrl.toURI());
			log.add("locMethodFile: " + locMethodFile);
		} catch (Exception e1) {
			String s = " locationMethodUrl FAILED  help! - " + e1.getMessage();
			System.out.println(s);
			log.add(s);
			// return;
		}

		String runTargetName = locMethodFile.getName();
		File pwdFile = null;
		int prams_at = 0;

		// FOR simple testing ONLY
		// runTargetName = "C:\\a\\aaBridge.exe";
		// runTargetName = "C:\\a\\aaBridge_1.2.0.1111.jar";
		// locMethodFile = new File(runTargetName);

		if (runTargetName.toLowerCase().endsWith(".jar")) {

			// We are running in a .jar on either windows, mac or linux or ...
			pwdFile = locMethodFile.getParentFile();
			commands[0] = "java";
			commands[1] = "-jar";
			commands[2] = runTargetName;
			prams_at = 3;
		}
		else if (runTargetName.endsWith(".exe")) {

			// We are running in .exe on windows
			pwdFile = locMethodFile.getParentFile();
			commands[0] = runTargetName;
			prams_at = 1;
		}
		else {
			// Assume that we are a .class file
			// running in an expanded state not in a .jar or .exe possibibly
			// even in the eclipse IDE - but we can still launch another instance
			// by using the class file and setting the missing (miglib) on to our classpath

//			// Alternate method of getting the location - kept for info
//
//			URL resourceMethodUrl = AaBridge.class.getResource(AaBridge.class.getSimpleName() + ".class");
//			File resMethodFile = null;
//			try {
//				resMethodFile = new File(resourceMethodUrl.toURI());
//				log.add("resMethodFile: " + resMethodFile);
//			} catch (Exception e1) {
//				String s = " resourceMethodUrl FAILED help! - " + e1.getMessage();
//				System.out.println(s);
//				log.add(s);
//				// return;
//			}

			String colon = File.pathSeparator;
			String sep = File.separator;
			pwdFile = locMethodFile;
			commands[0] = "java";
			commands[1] = "-cp";
			commands[2] = "." + colon + "." + sep + ".." + sep + "lib" + sep + "*";
			commands[3] = AaBridge.class.getName();
			prams_at = 4;
		}

		for (int i = 1; i < args.length; i++) {
			commands[prams_at++] = args[i];
		}

		log.add("");
		log.add("pwd = " + pwdFile);
		log.add("- - - commands - - -");
		for (int i = 0; i < commands.length; i++) {
			log.add(commands[i]);
		}

		ProcessBuilder pb = new ProcessBuilder(commands);
		pb.directory(pwdFile);
		try {

			Process proc = pb.start();

			// Create threads to eat the stderror and stdio
			StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERROR");
			StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "OUTPUT");
			// and start them
			errorGobbler.start();
			outputGobbler.start();

			// int exitVal = proc.waitFor(); // NO NO NO - we do not wait !!!
			// System.out.println("ExitValue: " + exitVal);

		} catch (IOException e) {
			String s = " ProcessBuilder FAILED = " + e.getMessage();
			System.out.println(s);
			log.add(s);
			return;
		}

		@SuppressWarnings("unused")
		int z = 0; // put your breakpoint here
	}

	public static void do_tutorialTellMe() {
		// ==========================================================================

		MassGi mg = App.mg;

		GraInfo gi = mg.giAy.get(mg.stop_gi);
		gi.userAns = "tellme";

		@SuppressWarnings("unused")
		int z = 0; // put your breakpoint here
	}

	public static void callback_questionAnswered(Bid bid) {
		// ==========================================================================
		MassGi mg = App.mg;

		GraInfo gi = mg.giAy.get(mg.stop_gi);
		// @formatter:off
		if (   (gi.qt == q_.lb && gi.bb.size() >= 5)
			&& (gi.bb.get(1).length() == 1)
			&& (gi.bb.get(1).contentEquals("b")) // <=====  b  ===
		    )
		{
			gi.userAns = bid.toLinStr().toLowerCase();
			App.frame.repaint();
		}
		// @formatter:on

		@SuppressWarnings("unused")
		int z = 0; // put your breakpoint here
	}

	public static void callback_questionAnswered(Hand hand, Card card) {
		// ==========================================================================
		MassGi mg = App.mg;

		GraInfo gi = mg.giAy.get(mg.stop_gi);
		// @formatter:off
		if (   (gi.qt == q_.lb && gi.bb.size() >= 6)
				&& (gi.bb.get(1).length() == 1)
				&& (gi.bb.get(1).contentEquals("c"))  // <=====  c  ===
			)
		{
			/** 
			 * We are the answer to the   pick a card  question
			 */
			gi.userAns = card.toLinStr().toLowerCase();
			App.frame.repaint();
		}
		
		
		else if (   (gi.qt == q_.lb && gi.bb.size() >= 7)
				&& (gi.bb.get(1).length() == 1)
				&& (gi.bb.get(1).contentEquals("h"))  // <=====  h  ===
			)
		{
			/** 
			 * We are the answer to the   pick a hand  question
			 */
			gi.userAns = "1"; // Left
			if (hand.deal == App.tup.qp.deal2) {
				gi.userAns = "2";  // Right
			}
			App.frame.repaint();
		}
		// @formatter:on

		@SuppressWarnings("unused")
		int z = 0; // put your breakpoint here
	}

	public static void callback_questionAnswered(String ans) {
		// ==========================================================================
		MassGi mg = App.mg;

		GraInfo gi = mg.giAy.get(mg.stop_gi);
		// @formatter:off
		if (   (gi.qt == q_.lb && gi.bb.size() >= 6)
				&& (gi.bb.get(1).length() == 1)
				&& (   gi.bb.get(1).contentEquals("p")   // <=====  p  ===
					|| gi.bb.get(1).contentEquals("t")   // <=====  t  ===
					|| gi.bb.get(1).contentEquals("l")   // <=====  l  ===
					|| gi.bb.get(1).contentEquals("d")   // <=====  d  ===
					|| gi.bb.get(1).contentEquals("s")   // <=====  s  ===
					|| gi.bb.get(1).contentEquals("t"))  // <=====  t  ===
			)
		{
			/** 
			 * We are the answer to a the   click a button  question
			 */
			gi.userAns = ans;
			App.frame.repaint();
		}
		
		else 
			if (   (gi.qt == q_.lb )
				&& (gi.bb.get(1).length() == 1)
				&& (   gi.bb.get(1).contentEquals("m") && (gi.bb.size() >= 5)   // <=====  m  ===
				    || gi.bb.get(1).contentEquals("y") && (gi.bb.size() >= 5)   // <=====  y  ===
				    || gi.bb.get(1).contentEquals("z") && (gi.bb.size() >= 4)   // <=====  z  ===
				   )
			)
		{
			/** 
			 * We are the answer to a the   click a button  question
			 */
			gi.userAns = ans;
			App.frame.repaint();
		}
		
		// @formatter:on

		@SuppressWarnings("unused")
		int z = 0; // put your breakpoint here
	}

	public static void launchLinkViaItsOwnThread(String origUrl, boolean loadIfLin) {
		// =============================================================================
		/** this is called by
		  externalUrlLauncher
		 */

		String linUrl = "";

		origUrl = origUrl.replace(" ", "%20");  // very partial replacement for safety with older pre-encodedlinks

		if (loadIfLin)
			linUrl = extractLinDownloadUrl(origUrl);

		if (linUrl.isEmpty() == false) {
			/* we found a lin file reference in the link so lets try read and launch */
			boolean success = readLinFileFromWebsiteAndLaunchApp(linUrl);
			if (success == false) {
				/* try the old fashioned way */
				displayUrlInBrowser(origUrl);
			}
			return;
		}

		/**
		 * We did not find a reference to a lin file in the link but it still
		 * could be a tinyUrl (or equiv) redirection to one, so lets check and see
		 */

		String redirectedUrl = "";

		if (loadIfLin)
			redirectedUrl = fetchRedirectedUrl(origUrl);

		if (redirectedUrl.isEmpty() == false) {
			linUrl = extractLinDownloadUrl(redirectedUrl);
			if (linUrl.isEmpty() == false) {
				/* we found a lin file reference in the link so lets try read and launch */
				boolean success = readLinFileFromWebsiteAndLaunchApp(linUrl);
				if (success)
					return;
			}
		}

		displayUrlInBrowser(origUrl); /* use the original incase our redirection is not working */
	}

	public static void displayUrlInBrowser(String url) {
		// =============================================================================

		try {
			Desktop.getDesktop().browse(new java.net.URI(url));
		} catch (Exception ev) {
		}
	}

	private static final String extrahondo = "/hondoviewer/";
	private static final String hondoviewer = "/hondoviewer.php?sf=";
	private static final String downloadlin = "/downloadlin.php?filename=";

	public static String extractLinDownloadUrl(String origUrl) {
		// =============================================================================

		String linUrl = origUrl;
		String t;

		boolean tryLinFetch = false;

		if (linUrl.contains(downloadlin)) {
			// leave it as it is
			tryLinFetch = true;
		}
		else if (linUrl.contains(hondoviewer)) {
			tryLinFetch = true;
			if (linUrl.contains(extrahondo)) {
				t = linUrl.replace(extrahondo, "/");
				linUrl = t;
			}
			t = linUrl.replace(hondoviewer, downloadlin);
			if (t.toLowerCase().endsWith(".lin") == false) {
				t += ".lin";
			}
			linUrl = t;
		}
		else if (linUrl.toLowerCase().endsWith(".lin")) {
			tryLinFetch = true;
		}

		return tryLinFetch ? linUrl : "";
	}

	static class Pair {
		Pair(String s, String d) {
			src = s;
			dest = d;
		}

		String src;
		String dest;
	}

	final static Pair[] knownTinyccUrls = {
		// @formatter:off	
//		new Pair("023ipw", ""),
//		new Pair("0dpf5w", ""),
		new Pair("0jplzw", "negativedoubles"),
//		new Pair("0tlf5w", ""),
//		new Pair("0tm7qw", ""),
		new Pair("0y4nvw", "bergenhandevaluation"),
		new Pair("1eeqsw", "reopeningdouble"),
//		new Pair("2el25w", ""),
//		new Pair("2hpf5w", ""),
		new Pair("2l76ww", "takeoutdoublespart3"),
		new Pair("2o6bzw", "ntresponses55"),
//		new Pair("2xlf5w", ""),
//		new Pair("2z4zyw", ""),
//		new Pair("343ipw", ""),
		new Pair("3gew4w", "blackwoodleadtrump"),
		new Pair("3vc0tw", "weaktwobids"),
		new Pair("4nu3vw", "raisesincompetition"),
		new Pair("4tklvw", "raisesincompetition"),
//		new Pair("52vmvw", ""),
		
//		new Pair("5b4ipw", ""),
//		new Pair("5cpruw", ""),
		new Pair("5elqvw", "restrictedchoice"),
//		new Pair("6cxtkw", ""),
		new Pair("6eyhsw", "bergenhandevaluation"),
//		new Pair("6j9vvw", ""),
//		new Pair("6v1p7w", ""),
		new Pair("74kqvw", "negativedoubles"),
		new Pair("7j86ww", "takeoutdoublespart1"),
//		new Pair("7llf5w", ""),
		new Pair("7tp3uw", "weaktwobids"),
//		new Pair("81xg5w", ""),
		new Pair("8f6kyw", "takeoutdoublespart1"),
//		new Pair("9epruw", ""),
//		new Pair("a0315w", ""),
//		new Pair("baknrw", "constructivebiddingintro"),
		new Pair("bbf2vw", "watson23"),
//		new Pair("bh9vvw", ""),
		new Pair("bmnuvw", "competitivebidding"),
//		new Pair("bp5bzw", ""),
		new Pair("bqf5rw", "watson17"),
//		new Pair("buz6uw", ""),
//		new Pair("c0ltvw", ""),
//		new Pair("c3315w", ""),
		
//		new Pair("c3xhpw", ""),
//		new Pair("cykc7w", ""),
		new Pair("d28vvw", "competitivebidding"),
//		new Pair("dolf5w", ""),
		new Pair("e5jdvw", "ntauctions"),
		new Pair("eicuww", "raisesincompetition"),
//		new Pair("ejwf5w", ""),
//		new Pair("enpf5w", ""),
		new Pair("f7rw4w", "counting"),
//		new Pair("fwkc7w", ""),
//		new Pair("fzj6uw", ""),
//		new Pair("gjlf5w", ""),
//		new Pair("gtu7tw", ""),
		new Pair("h93tww", "invertedminors"),
//		new Pair("hfh7vw", ""),
		new Pair("i5fouw", "4sf"),
		new Pair("i663sw", "balancing"),
		new Pair("ifvmvw", "dont"),
		new Pair("itk5rw", "4sf"),
//		new Pair("jh56tw", ""),
//		new Pair("jriazw", ""),
//		new Pair("kr03qw", ""),
//		new Pair("lgk6uw", ""),
		new Pair("lr03sw", "weaktwobids"),
		
//		new Pair("m1b7tw", ""),
		new Pair("muk5rw", "slamlesson2"),
//		new Pair("n4xg5w", ""),
		new Pair("n5rw4w", "carding"),
		new Pair("n8aluw", "bergenhandevaluation"),
//		new Pair("njm25w", ""),
//		new Pair("p4kepw", ""),
		new Pair("pcl2vw", "ntauctions"),
		new Pair("pcwpuw", "twosuitedovercalls"),
//		new Pair("pppf5w", ""),
//		new Pair("pzaukw", ""),
//		new Pair("q63ipw", ""),
		new Pair("q95tww", "ntauctions"),
		new Pair("qhbuww", "weaktwobids"),
		new Pair("rdm26w", "slamlesson3"),
//		new Pair("rdo4nw", ""),
		new Pair("rqsluw", "twosuitedovercalls"),
		new Pair("sd83sw", "bergenhandevaluation"),
		new Pair("umd6sw", "ruleof20"),
		new Pair("urjdvw", "raisesincompetition"),
//		new Pair("usxbvw", ""),
		new Pair("utbluw", "managingentries"),
		new Pair("v291vw", "ruleof20"),
		new Pair("v7idvw", "supportdoubles"),
//		new Pair("v7kllw", ""),
		
		new Pair("w376ww", "takeoutdoublespart2"),
		new Pair("wyo8rw", "invertedminors"),
		new Pair("x6vpsw", "bergenhandevaluation"),
		new Pair("x6vqvw", "bergenhandevaluation"),
		new Pair("x9y3vw", "losingtrickcount"),
		new Pair("y31j7w", "watson18"),
		new Pair("y6o3uw", "twosuitedovercalls"),
		new Pair("yckzyw", "raisesincompetition"),
		new Pair("yd91vw", "twosuitedovercalls"),
		new Pair("ylocvw", "balancing"),
		new Pair("zofw4w", "blackwoodleadpartnerssuit"),
		new Pair("zuzyvw", "competitivebidding"),
		// @formatter:on

	};

	public static String fetchRedirectedUrl(String origUrl) {
		// =============================================================================

		// for known tiny.cc urls we have cached looked up (historic but why remove it)

		for (Pair pair : knownTinyccUrls) {
			if (("http://tiny.cc/" + pair.src.toLowerCase()).contains(origUrl.toLowerCase()) && pair.dest.length() > 0) {
				return "http://www.bridgesights.com/hondobridge/hondoviewer/hondoviewer.php?sf=" + pair.dest;
			}
		}

		String redirectedUrl = "";
		int resp;

		try {
			URL url = new URL(origUrl);
			// System.out.println( "orignal url: " + origUrl );

			// We know that this always fails with tiny.cc so we won't bother
			if (origUrl.toLowerCase().startsWith("http://tiny.cc/") == false) {
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.setInstanceFollowRedirects(false);
				resp = con.getResponseCode();
				if (resp == 301) {
					redirectedUrl = con.getHeaderField("location");
					// System.out.println( "redirectedUrl (simple): " + redirectedUrl );
				}
			}

			// try instead with a user-agent in the headers - some don't want it, some (tiny.cc) require it
			if (redirectedUrl.isEmpty()) {
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.setInstanceFollowRedirects(false);
				con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64)");
				resp = con.getResponseCode();
				if (resp == 301) {
					redirectedUrl = con.getHeaderField("location");
					// System.out.println( "redirectedUrl (+agent header): " + redirectedUrl );
				}
			}
		} catch (Exception e1) {
			// e1.printStackTrace();
		}
		return redirectedUrl;
	}

	public static boolean readLinFileFromWebsiteAndLaunchApp(String linUrl) {
		// =============================================================================
		/**
		 *  We now need to extract the file name 
		 *  so we can create a local file with a resonable name
		 */
		int end = 0;
		String endChar = "=/?";
		for (int i = 0; i < endChar.length(); i++) {
			int p = linUrl.lastIndexOf(endChar.charAt(i)) + 1;
			if (p > end)
				end = p;
		}

		/* must (to get) here have .lin at the end */

		String web_short_name = linUrl.substring(end, linUrl.length() - 4 /* the .lin */);

		/* change of design we now save the file in temp_Other and
		 * view it in the current aaBridge
		 */

		String fn = readLinFileFromWebsite(linUrl, web_short_name);

		if (fn.isEmpty())
			return false;  // so they can try to open in a browser
		else
			CmdHandler.imp_TempOtherFolder_timer.start();

		return true;
	}

	public static String readLinFileFromWebsite(String linUrl, String info) {
		// =============================================================================

		String EOL = Zzz.get_lin_EOL();

		try {
			StringBuilder sb = new StringBuilder();

			URL url = new URL(linUrl);
			BufferedReader in;
			in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
			String str;
			while ((str = in.readLine()) != null) {
				sb.append(str).append(EOL);
			}
			in.close();

			return saveStringAsLinFile(sb.toString(), info);

		} catch (IOException e) {
			return "";
		}
	}

	public static String readLinFileFromWebsiteAsString(String linUrl) {
		// =============================================================================

		String EOL = Zzz.get_lin_EOL();

		try {
			StringBuilder sb = new StringBuilder();

			URL url = new URL(linUrl);
			BufferedReader in;
			in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
			String str;
			while ((str = in.readLine()) != null) {
				sb.append(str).append(EOL);
			}
			in.close();

			return sb.toString();

		} catch (IOException e) {
			return "";
		}
	}

	public static String readTextFileToString(File file, boolean use_space_as_EOL) {
		// =============================================================================

		String EOL = use_space_as_EOL ? " " : Zzz.get_lin_EOL();

		try {
			StringBuilder sb = new StringBuilder();

			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			String str;
			while ((str = in.readLine()) != null) {
				sb.append(str).append(EOL);
			}
			in.close();

			return sb.toString();

		} catch (IOException e) {
			return "";
		}
	}

	public static String saveStringAsLinFile(String s, String info) {
		// =============================================================================

		if (s.contains("|") == false) {
			return ""; // fail
		}

		if (s.contains("%7C") || s.contains("%7c")) {
			try {
				s = URLDecoder.decode(s, "UTF-8");
			} catch (UnsupportedEncodingException e1) {
				return ""; // fail
			}
		}

		String filename = Util.filename_for_created_lin(info, s);

		try {
			FileOutputStream fileOut = new FileOutputStream(filename);

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fileOut, "UTF-8"));
			writer.write(s);
			writer.close();

			fileOut.close();

		} catch (Exception e) {
			return ""; // fail
		}
		return filename;
	}

	public static boolean saveStringAsLinFile_direct(String s, String filename) {
		// =============================================================================

		if (s.contains("|") == false) {
			return false; // fail
		}

		if (s.contains("%7C") || s.contains("%7c")) {
			try {
				s = URLDecoder.decode(s, "UTF-8");
			} catch (UnsupportedEncodingException e1) {
				return false; // fail
			}
		}

		try {
			FileOutputStream fileOut = new FileOutputStream(filename);

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fileOut, "UTF-8"));
			writer.write(s);
			writer.close();

			fileOut.close();

		} catch (Exception e) {
			return false; // fail
		}
		return true;
	}

	public static String createLinFileFromText(String in, String info) {
		// =============================================================================

		in = in.trim();
		if ((in.startsWith("\"") && in.endsWith("\"")) || (in.startsWith("'") && in.endsWith("'"))) {
			in = in.substring(1, in.length() - 1);
		}

		String proofLin1 = "bbo=y&lin=";
		String proofLin2 = "bbo=y&amp;lin=";
		String proofLin3 = "html?lin=";

		String proofLinurl = "linurl=";

		if ((in.contains(proofLinurl))) {
			int start = in.indexOf(proofLinurl) + proofLinurl.length();
			String innerUrl = in.substring(start);

			// this may take a while !!!!
			String dealName = readLinFileFromWebsite(innerUrl, info);

			return dealName;
		}
		int start = -1;

		if (in.contains(proofLin1)) {
			start = in.indexOf(proofLin1) + proofLin1.length();
		}
		else if (in.contains(proofLin2)) {
			start = in.indexOf(proofLin2) + proofLin2.length();
		}
		else if (in.contains(proofLin3)) {
			start = in.indexOf(proofLin3) + proofLin3.length();
		}

		if (start > -1) {
			String lin = in.substring(start);
			try {
				lin = URLDecoder.decode(lin, "UTF-8");
			} catch (UnsupportedEncodingException e1) {
			}

			Deal deal = new Deal(0);
			String dealName = CmdHandler.makeDealFileNameAndPath(deal, "", "", /* allow_xxx */ false);

			dealName = dealName.replace("Not-Yet-Bid", "dropped-text");

			try {
				FileOutputStream fileOut = new FileOutputStream(dealName);

				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fileOut, "UTF-8"));
				writer.write(lin);
				writer.close();

				fileOut.close();

			} catch (Exception e) {
				return ""; // fail
			}

			return dealName;
		}

		int qPos = in.indexOf("?");

		if (qPos > 0)
			in = in.substring(qPos + 1);

		// 2023-05-27  RPf Issue FIX
		// BBO have started SOMETIMES !!! UUencoding "{" and "}"
		// this code needs them as characters so alerts and other comments
		// can be spotted in the input so the are now hand decoded here
		in = in.replace("%7B", "{");
		in = in.replace("%7b", "{");
		in = in.replace("%7D", "}");
		in = in.replace("%7d", "}");

		String low = in.toLowerCase();
		if ((low.indexOf("s=") == -1) && (low.indexOf("w=") == -1) && (low.indexOf("n=") == -1) && (low.indexOf("e=") == -1)) {
			return "";
		}

		String hands[] = { "", "", "", "" };
		String names[] = { "", "", "", "" };

		String boardNo = "1", auction = "", play = "", kibitz = "", vul = "";
		String title = "", initialText = "", claim = "";
		int linDealer = 3; // north

		String EOL = Zzz.get_lin_EOL();
		String xs = "";

		if (App.linfileSaveFormat == App.linFmt_BBO) {
			EOL = "";
			xs = App.bboUpStripped ? "" : " ";
		}

		String decodeCp = App.decodeWith1252 ? "Windows-1252" : "UTF-8";

		StringTokenizer st = new StringTokenizer(in, "&");

		while (st.hasMoreTokens()) {

			String pair = st.nextToken();

			StringTokenizer tp = new StringTokenizer(pair, "=");

			if (tp.countTokens() != 2)
				continue;

			String type = tp.nextToken().toLowerCase();

			String val_orig = tp.nextToken();
			String val = "";
			try {
				val = URLDecoder.decode(val_orig, decodeCp);
			} catch (UnsupportedEncodingException e) {
				// do nothing about it
			}

			if (type.contentEquals("lin")) {
				String s = val;
				s = s.replace("!S", "@S");
				s = s.replace("!s", "@s");
				s = s.replace("!H", "@H");
				s = s.replace("!h", "@h");
				s = s.replace("!D", "@D");
				s = s.replace("!d", "@d");
				s = s.replace("!C", "@C");
				s = s.replace("!c", "@c");
				String fileName = saveStringAsLinFile(s, info);
				return fileName;
			}

			else if (type.contentEquals("b")) {
				boardNo = val;
			}

			else if (type.contentEquals("sn")) {
				names[0] = val;
			}
			else if (type.contentEquals("wn")) {
				names[1] = val;
			}
			else if (type.contentEquals("nn")) {
				names[2] = val;
			}
			else if (type.contentEquals("en")) {
				names[3] = val;
			}

			else if (type.contentEquals("s")) {
				hands[0] = val;
			}
			else if (type.contentEquals("w")) {
				hands[1] = val;
			}
			else if (type.contentEquals("n")) {
				hands[2] = val;
			}
			else if (type.contentEquals("e")) {
				hands[3] = val;
			}

			else if (type.contentEquals("d")) { // dealer
				Dir dir = Dir.directionFromChar((val + "n").charAt(0));
				// for lin '1'=South, '4'=East, aaBridge internal 2=South, 0=North
				linDealer = ((dir.v + 2) % 4) + 1; // the + 1 is adding the BBO extra code value of 0
			}
			else if (type.contentEquals("a")) {
				auction = val_orig;
			}
			else if (type.contentEquals("v")) {
				vul = val;
			}
			else if (type.contentEquals("b")) {
				boardNo = val; // Aaa.extractPositiveInt(val) + "";
			}
			else if (type.contentEquals("p")) {
				play = val_orig;
			}
			else if (type.contentEquals("c")) {
				claim = val;
			}
			else if (type.contentEquals("k")) {
				kibitz = val;
			}
			else if (type.contentEquals("t")) {
				title = "   " + val;
			}
			else if (type.contentEquals("i")) {
				initialText = val;
			}
			else if (type.contentEquals("c")) {
				initialText = val;
			}
			else if (type.contentEquals("tbt")) {
				// is ... y/n. use "y" to have the NEXT button step by trick. "n" to step by card
				// stepByTrick = (val.toLowerCase() + "y").charAt(0) == 'y';
				// imebedded value ignored we do trick by trick
			}
			else {
				System.out.print("createLinFileFromText  unknown token:   " + type);
			}
		}

		String clearPage = "nt|^z@3^b@2^^|";

		String s = "";

		if ((names[0] + names[1] + names[2] + names[3]).length() > 0) {
			s += "pn|" + names[0] + "," + names[1] + "," + names[2] + "," + names[3] + "|" + EOL;
		}

		if ((hands[0] + hands[1] + hands[2] + hands[3]).length() > 0) {
			s += "md|" + linDealer + hands[0] + "," + hands[1] + "," + hands[2] + "," + hands[3] + "|" + EOL;
		}

		s += "qx|o" + boardNo + ",wide|" + EOL;
		s += "rh||ah|Board " + boardNo + title + "|" + EOL;
		s += "sv|" + vul + "|";
		s += "sk|" + kibitz + "|" + EOL;

		boolean pageClearedOnce = false;

		if (!initialText.isEmpty()) {
			pageClearedOnce = true;
			s += clearPage;
			s += "at|" + initialText + "|pg|" + xs + "|" + EOL;
		}

		String pendingPg = "";

		String bids = "";

		if (auction.length() > 0 && auction.charAt(0) == '-') {

			if (auction.length() == 4) {
				// we are only setting the contract
				int dea = linDealer - 1;
				int dec = Dir.directionFromChar(auction.charAt(3)).v;
				int passes = (dec + 4 + 2 - dea) % 4;
				bids = "pppp".substring(0, passes) + auction.subSequence(1, 3) + "ppp";
			}
			else if (auction.length() == 3) {
				// ending (incomplete deal)
				bids = "pp1" + auction.charAt(1) + "ppp|" + EOL + "|ha|y|wt|" + auction.charAt(2);
			}
			else {
				// shrug
			}

		}
		else {
			// normal auction
			for (int i = 0; i < auction.length(); i++) {

				char c = auction.charAt(i);

				if (c == '(') {
					if (!bids.isEmpty()) {
						s += "mb|" + bids + "|";
						bids = "";
					}

					int e = auction.indexOf(')', i);
					if (e > i) {
						String alert = auction.substring(i + 1, e);
						try {
							alert = URLDecoder.decode(alert, decodeCp);
						} catch (UnsupportedEncodingException e2) {
							// do nothing about it
						}
						i = e;
						if (alert.length() > 0) {
							s += "an|" + alert + "|" + EOL;
						}
						continue;
					}
				}

				if (c == '{') {
					if (!bids.isEmpty()) {
						s += "mb|" + bids + "|";
						bids = "";
					}

					int e = auction.indexOf('}', i);
					if (e > i) {
						if (auction.charAt(i + 1) == '+') {
							i++; // skip over the plus
						}
						else {
							s += clearPage;
							pageClearedOnce = true;
						}
						if (pageClearedOnce == false) {
							pageClearedOnce = true;
							s += clearPage;
						}
						String text = auction.substring(i + 1, e);
						try {
							text = URLDecoder.decode(text, decodeCp);
							text = text.replace("<br>", "^^");
							text = text.replace('!', '@');
						} catch (UnsupportedEncodingException e2) {
							// do nothing about it
						}
						i = e;
						if (text.length() > 0) {
							s += "at|" + text + "|pg|" + xs + "|" + EOL;
						}
						continue;
					}
				}

				bids += auction.charAt(i);
			}
		}

		if (!bids.isEmpty()) {
			s += "mb|" + bids + "|pg|" + xs + "|" + EOL;
			bids = "";
		}

		s += "qx|lead,wide|" + EOL;

		String cards = "";

		int cards_tot = 0;

		for (int i = 0; i < play.length(); i++) {

			char c = play.charAt(i);

			if (c == '{') {
				if (!cards.isEmpty()) {
					s += "pc|" + cards + "|";
					cards = "";
				}

				int e = play.indexOf('}', i);
				if (e > i) {
					if (play.charAt(i + 1) == '+') {
						i++; // skip over the plus
					}
					else {
						s += clearPage;
						pageClearedOnce = true;
					}
					if (pageClearedOnce == false) {
						pageClearedOnce = true;
						s += clearPage;
					}
					String text = play.substring(i + 1, e);
					try {
						text = URLDecoder.decode(text, decodeCp);
						text = text.replace("<br>", "^^");
						text = text.replace('!', '@');
					} catch (UnsupportedEncodingException e2) {
						// do nothing about it
					}
					i = e;
					if (text.length() > 0) {
						s += "at|" + text + "|pg|" + xs + "|" + EOL;
						pendingPg = "";
					}
					continue;
				}
			}

			if (pendingPg.length() > 0) {
				s += pendingPg;
				pendingPg = "";
			}

			cards += play.charAt(i);
			if ((cards.length() % 2) == 1) {
				continue; // two characters per card
			}
			cards_tot++;

			if ((cards_tot % 4 == 0) || (cards_tot == 1)) {
				s += "pc|" + cards + "|";
				pendingPg = "pg|" + xs + "|" + EOL;
				cards = "";
			}
		}

		if (!cards.isEmpty()) {
			s += "pc|" + cards + "|pg|" + xs + "|";
			cards = "";
		}

		s += EOL;

		if (!claim.isEmpty()) {
			s += "qx|clam,wide|" + EOL;
			s += "mc|" + claim + "|pg|" + xs + "|" + EOL;
		}
		else {
			s += "qx|fin,wide|pg||" + EOL;
		}

		String fileName = saveStringAsLinFile(s, info);

		return fileName;
	}

	public static void deleteDirectoryRecursionJava6(File file) throws IOException {
		// ==========================================================================
		if (file.isDirectory()) {
			File[] entries = file.listFiles();
			if (entries != null) {
				for (File entry : entries) {
					deleteDirectoryRecursionJava6(entry);
				}
			}
		}
		if (!file.delete()) {
			throw new IOException("Failed to delete " + file);
		}
	}

	public static void joinLinFiles() {
		// ==========================================================================

		String ls = Zzz.get_lin_EOL();

		String destFolder = "";
		String joined = "";

		for (LinChapter chap : App.book) {
			if (chap.type != 'f')
				continue;
			if (chap.filename.toLowerCase().endsWith(".lin") == false)
				continue;
			if (chap.filename.toLowerCase().contains("joined"))
				continue;
			String s = readFile(chap.book.bookFolderName + chap.filename);
			if (s.length() > 0) {
				joined += s + ls + ls + ls;
				if (destFolder.length() == 0) {
					destFolder = chap.book.bookFolderName;
				}
			}
		}

		if (joined.length() > 0) {

			if (App.forceSaveMultiDealToSavesFolder) {
				destFolder = App.realSaves_folder;
			}

			String filename = destFolder + "0000 0000 joined.lin";

			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter(new FileWriter(filename));
				writer.write(joined);
			} catch (IOException e) {
			} finally {
				try {
					writer.close();
				} catch (Exception e) {
				}
			}
		}
	}

	public static String readFile(String file) {
		// ==========================================================================
		FileReader fileReader;
		try {
			fileReader = new FileReader(file);
			BufferedReader reader = new BufferedReader(fileReader);
			try {
				String line = null;
				StringBuilder stringBuilder = new StringBuilder();
				String ls = Zzz.get_lin_EOL();

				try {
					while ((line = reader.readLine()) != null) {
						stringBuilder.append(line);
						stringBuilder.append(ls);
					}
					return stringBuilder.toString();

				} catch (Exception e) {
					return "";
				}
			} finally {
				reader.close();
			}
		} catch (Exception e) {
			return "";
		}
	}
}

/**
* A capture of the state of the tutorial graphics variables
* If the user needs to change any of these values then they must clone it first
*/
class Capture_gi_env {
	// ---------------------------------- CLASS -------------------------------------

	boolean centered;
	boolean bold;
	boolean italic;
	boolean underline;
	boolean boxed;
	boolean backgroundDarker;
	boolean fillFloatingHandBackground;

	Color color_cp; // cp is font color
	Color color_cs; // cs is 'user made box' fill color
	Color color_co; // co is a color override used for graying

	public boolean gray_fade;
	public boolean tidyTrick;
	public boolean overlapShrink;

	int lb_position; // /* question position override - 'u' 20th letter counting 'a' = 0 means bottom of the screen as normal */

	boolean pdl_allSeatsVisible;
	boolean pdl_auctionVisible;
	int visualModeRequested;

	int font_slot_fp;

	Hyperlink hyperlink;

	int zm_clear_su;
	boolean zm_studyDeal_forced;

	boolean mn_show_tu;
	int mn_lines;
	int mn_pg_countDown;
	int page_numb_display;
	String mn_text;

	char bv_etd;
	char bv_1st;
	char bv_cont;
	char bv_dds;

	boolean playerNameNumbsVisible;

	int tut_rotation;

	/**
	 */
	Capture_gi_env(Capture_gi_env o) { // 'Copy' Constructor
		// ==========================================================================

		centered = o.centered;
		bold = o.bold;
		italic = o.italic;
		underline = o.underline;

		boxed = o.boxed;
		backgroundDarker = o.backgroundDarker;
		fillFloatingHandBackground = o.fillFloatingHandBackground;

		color_cp = o.color_cp; // This is the default colour used by font 0
		color_cs = o.color_cs;

		zm_clear_su = o.zm_clear_su;
		gray_fade = o.gray_fade;
		lb_position = o.lb_position;

		tidyTrick = o.tidyTrick;
		overlapShrink = o.overlapShrink;

		pdl_allSeatsVisible = o.pdl_allSeatsVisible;
		pdl_auctionVisible = o.pdl_auctionVisible;
		visualModeRequested = o.visualModeRequested;

		font_slot_fp = o.font_slot_fp;
		hyperlink = o.hyperlink;

		zm_studyDeal_forced = o.zm_studyDeal_forced;

		mn_show_tu = o.mn_show_tu;
		mn_lines = o.mn_lines;
		mn_pg_countDown = o.mn_pg_countDown;
		page_numb_display = o.page_numb_display;

		mn_text = o.mn_text;
		bv_etd = o.bv_etd;
		bv_1st = o.bv_1st;
		bv_cont = o.bv_cont;
		bv_dds = o.bv_dds;

		playerNameNumbsVisible = o.playerNameNumbsVisible;

		tut_rotation = o.tut_rotation;
	}

	/**
	 */
	Capture_gi_env() { // Constructor
		// ==========================================================================

		centered = false;
		bold = false;
		italic = false;
		underline = false;

		boxed = false;
		backgroundDarker = false;
		fillFloatingHandBackground = false;

		color_cp = Color.BLACK; // black This is the default colour used by font 0
		color_cs = Aaa.tutorialBackground;

		zm_clear_su = 0;     // 0 = don't   1 = once    2 = always
		gray_fade = false; // re-instated with a different use now user controlled
		lb_position = 20; /* 'u' 20th letter counting 'a' = 0  means bottom of the screen as normal */

		tidyTrick = false;
		overlapShrink = false;

		pdl_allSeatsVisible = true;
		pdl_auctionVisible = true;
		visualModeRequested = App.Vm_DealAndTutorial;

		font_slot_fp = 0; // interal slot that is set to Ariel !
		hyperlink = null;

		mn_show_tu = true;
		mn_lines = 0;
		mn_pg_countDown = 0; //
		page_numb_display = 99999; // should not be used util set by pg of question

		mn_text = "";
		bv_etd = 'v';
		bv_1st = 'h';
		bv_cont = 'h';
		bv_dds = 'h';

		playerNameNumbsVisible = true;

		tut_rotation = 0;
	}

	/**
	 */
	void reset_for_nt() {
		// ==========================================================================
		centered = false;
		bold = false;
		italic = false;
		underline = false;
		boxed = false;

		font_slot_fp = 0;
		color_cp = Color.BLACK;
		color_cs = Aaa.tutorialBackground;

		// zm_clear_su unchanged
		// gray_fade unchanged

		// lb_position unchanged

		// pdl unchanged

		hyperlink = null;

		// mn_show_tu unchanged note - pg clears mn_show_tu;
		// mn_lines unchanged
		// mn_text unchanged
		// visualModeRequested unchanged
	}
}

/**
 */
class Hyperlink {
	// ---------------------------------- CLASS -------------------------------------
	int mouse = Aaa.MOUSE_NONE;
	String linkInfo = "";
	public boolean loadIfLin = true;

	Color getHoverColor() {
		assert (false);
		return Color.WHITE; // virtual stub
	}

	Color getNormalColor() {
		assert (false);
		return Color.WHITE; // virtual stub
	}

	Color getLinkColor(Color std /* param not used */) {
		// =============================================================================
		switch (mouse) {
		case Aaa.MOUSE_PRESSED:
			return Aaa.tut_old_text_gray;
		case Aaa.MOUSE_HOVER:
			return getHoverColor();
		}
		return getNormalColor();
		// return std; umm not currently used
	}

	public String getLinkInfo() {
		// =============================================================================
		return linkInfo;
	}

	public void actionLink(Boolean ctrlKey_depressed) {
		// =============================================================================
		assert (false); // always overridden
	}

	public String getLabelText() {
		// =============================================================================
		return linkInfo;
	}

}

/**
 */
class Hyperlink_f_fct extends Hyperlink {
	// ---------------------------------- CLASS -------------------------------------

	/**
	 */
	Hyperlink_f_fct(String section) { // constructor
		// =============================================================================
		this.linkInfo = section.trim();

		App.mg.hyperlinkAy.add(this);
	}

	@Override
	Color getNormalColor() {
		// =============================================================================
		return Aaa.tutorialLinkNorm_f;
	}

	@Override
	Color getHoverColor() {
		// =============================================================================
		return Aaa.tutorialLinkHover_f;
	}

	@Override
	public void actionLink(Boolean ctrlKey_depressed) {
		// =============================================================================
		/** so this is an internal fucnt call
		 */
		String label = getLinkInfo();
		App.frame.executeCmd(label);
	}

}

/**
 */
class Hyperlink_h_ext extends Hyperlink {
	// ---------------------------------- CLASS -------------------------------------
	String url;

	public Hyperlink_h_ext(String url) {
		// =============================================================================
		url = url.trim();
		if (url.contains("://") == false) {
			// java appears to require more in the url before
			// it will pass it to the browser [SIGH]
			url = "http://" + url;
		}
		linkInfo = url;
	}

	@Override
	Color getNormalColor() {
		// =============================================================================
		return Aaa.tutorialLinkNorm_h;
	}

	@Override
	Color getHoverColor() {
		// =============================================================================
		return Aaa.tutorialLinkHover_h;
	}

	static long lastExternalUrlClicked = 0;

	@Override
	public void actionLink(Boolean ctrlKey_depressed) {
		// =============================================================================
		String origUrl = getLinkInfo();

		origUrl = origUrl.replace("##", "#");

		if (ctrlKey_depressed == false) {
			boolean done = checkForAndDoInternalHondo(origUrl);
			if (done)
				return;
		}

		long now = new Date().getTime();

		if (lastExternalUrlClicked + 5000 /* 5 seconds */ > now)
			return;

		lastExternalUrlClicked = now;

		new externalUrlLauncher(origUrl, loadIfLin).start();
	}

	private static final String hondoviewer1 = "hondoviewer.php?sf=";
	private static final String hondoviewer2 = "hondoviewer.php?ndf=";

	private boolean checkForAndDoInternalHondo(String origUrl) {
		// =============================================================================
		String name = origUrl;

		String hondoviewer = hondoviewer1;
		int from = name.indexOf(hondoviewer);
		if (from < 1) {
			hondoviewer = hondoviewer2;
			from = name.indexOf(hondoviewer);
			if (from < 1) {
				return false;
			}
		}

		name = name.substring(from + hondoviewer.length()).trim();
		int to = 0;
		for (; to < name.length(); to++) {
			char c = name.charAt(to);
			if (c == ',')
				break;
			if (c == '^')
				break;
		}

		name = name.substring(0, to).trim();

		if (name.length() < 3 || 50 < name.length()) {
			return false;
		}

		boolean chapterLoaded = false;

		for (Bookshelf shelf : App.bookshelfArray) {
			for (Book book : shelf) {
				LinChapter chapter = book.getChapterByDisplayNamePart(name);
				if (chapter != null) {
					chapterLoaded = chapter.loadWithShow("replaceBookPanel");
					break;
				}
			}
		}

		return chapterLoaded;
	}
}

/**
 */
class Hyperlink_g_int extends Hyperlink {
	// ---------------------------------- CLASS -------------------------------------

	/**
	 */
	Hyperlink_g_int(String section) { // constructor
		// =============================================================================
		this.linkInfo = section.trim();

		App.mg.hyperlinkAy.add(this);
	}

	@Override
	Color getNormalColor() {
		// =============================================================================
		return Aaa.tutorialLinkNorm_g;
	}

	@Override
	Color getHoverColor() {
		// =============================================================================
		return Aaa.tutorialLinkHover_g;
	}

	@Override
	public void actionLink(Boolean ctrlKey_depressed) {
		// =============================================================================
		/** so this is an internal link
		 */

		String label = getLinkInfo().toLowerCase();

		if (label.contains(":") == false /* contains NONE */) {
			/**
			 *  <target_label>
			 *
			 *  Standard local (in this lin file) labels NEVER contain colons
			 *  so we can just jump to the label in THIS lin file
			 */
			jumpToQxLabel(label);
			return;
		}

		if (label.contains(":::")) {
			/**
			 *  :::<lin_file_name>        (jump to opening page assumed)
			 *
			 *  This is the name of a lin file in ANY book in the system
			 *  Hopefully the name is unique or the user may end up in an unexpected place
			 */

			String sb[] = label.split(":::");
			if (sb.length < 2)
				return;
			String part_chap_name = sb[1];

			// @SuppressWarnings("unused")
			boolean chapterLoaded = false;

			for (Bookshelf shelf : App.bookshelfArray) {
				for (Book book : shelf) {
					LinChapter chapter = book.getChapterByDisplayNamePart(part_chap_name);
					if (chapter != null) {
						chapterLoaded = chapter.loadWithShow("replaceBookPanel");
						jumpToQxLabel(sb[0]);
						break;
					}
				}
			}

			if (chapterLoaded == false && App.book != null) {
				LinChapter chapter = App.book.getChapterByDisplayNamePart(part_chap_name);
				if (chapter != null) {
					chapterLoaded = chapter.loadWithShow("replaceBookPanel");
					jumpToQxLabel(sb[0]);
				}
			}
			return;
		}

		if (label.contains("::")) {

			/** 
			 * <targetLabel>:<part_name_linfile>::<part_book_name> 
			 * 
			 * we need to look in other lin files in other books
			 */

			if (label.contains("::#")) {
				String sb[] = label.split("::#");
				if (sb.length < 2)
					return;
				String scc[] = sb[1].split(":");
				if (scc.length > 0) {
					String book_id = scc[0];
					if (book_id.length() != 2)
						return;
					char c0 = book_id.charAt(0);
					char c1 = book_id.charAt(1);
					if (('0' > c0 || c0 > '9') || ('0' > c1 || c1 > '9'))
						return;
					int id = (c0 - '0') * 10 + (c1 - '0');
					if (App.book != null && App.book.shelf != null) {
						Book book = App.book.shelf.getBookByFrontNumb(id);
						if (book != null) {
							int chapter_index = 0;
							if (scc.length > 0) {
								chapter_index = Aaa.extractPositiveIntOrZero(scc[1]);
							}
							LinChapter chapter = book.getChapterByIndex(chapter_index);
							chapter.loadWithShow("replaceBookPanel");
						}
					}
				}
				return;
			}

			/** 
			 * <targetLabel>:<part_name_linfile>::<part_book_name> 
			 * 
			 * or 
			 * 
			 * ::<part_book_name>    first lin file implied
			 * 
			 */
			String sb[] = label.split("::");
			if (sb.length < 2 || sb[1].isEmpty())
				return;

			String labelPlus = sb[0];
			String partBookName = sb[1].toLowerCase();

			/**  Cut again 
			 **/
			String partName = "";
			String sa[] = labelPlus.split(":");

			if (sa.length >= 2) {
				label = sa[0];
				partName = sa[1];
			}
			else {
				label = "";
			}

			LinChapter chapter = null;

			for (Bookshelf shelf : App.bookshelfArray) {
				for (Book book : shelf) {
					if (book.displayTitle.toLowerCase().contains(partBookName) == false)
						continue;

					if (partName.isEmpty())
						chapter = book.getChapterByIndex(0);
					else
						chapter = book.getChapterByDisplayNamePart(partName);

					if (chapter != null) {
						chapter.loadWithShow("replaceBookPanel");
						if (label.length() > 0) {
							jumpToQxLabel(label);
						}
						break;
					}
				}
				if (chapter != null)
					break;
			}
			return;
		}

		/**
			 * <targetLabel>:<part_name_linfile>     (in the current book)
			 * 
			 *  we need to look in other lin files in *THIS*  book
			 */
		String sa[] = label.split(":");
		if (sa.length < 2 || sa[1].isEmpty())
			return;

		label = sa[0];
		String partName = sa[1];

		if (App.book != null) {
			boolean success = App.book.loadChapterByDisplayNamePart(partName);
			if (success) {
				jumpToQxLabel(label);
			}
		}
	}

	private static boolean jumpToQxLabel(String label) {
		// =============================================================================
		MassGi mg = App.mg;

		for (GraInfo gi : mg.giAy) {
			if (gi.qt == q_.qx) {
				if (gi.text.toLowerCase().contains(label)) {
					for (int i = gi.index; i < mg.giAy.size(); i++) {
						GraInfo gi2 = mg.giAy.get(i);
						if (gi2.qt == q_.pg || gi2.qt == q_.lb) {
							mg.setTheReadPoints(gi2.index, false /* not used */);
							App.frame.repaint();
							return true;
						}
					}
				}
			}
		}
		return false;
	}

}

/**
 */
class LinColor {
	// ---------------------------------- CLASS -------------------------------------
	int rgb[] = { 0, 0, 0 };
	boolean seen[] = { false, false, false };

	public void setRGB(int i, int val, int slot, Color cAy_std[], Color cAy_fill[]) {
		// ==============================================================================================
//		if (seen[i])
//			return; // the user may not set a color part more than once
//
//		seen[i] = true;
		rgb[i] = val;

		// std colors
		{
			int cA[] = rgb.clone();

			for (int j = 0; j < 3; j++) {
				int v = cA[j];
				int mid = 128;
				if (v > mid) {
					v = mid + ((v - mid) * 70) / 100;
				}
				else {
					v = mid - ((mid - v) * 90) / 100;
				}
				cA[j] = v;
			}

			cAy_std[slot] = new Color(cA[0], cA[1], cA[2]);
		}

		// fill colors
		{
			int cA[] = rgb.clone();

			for (int j = 0; j < 3; j++) {
				int v = cA[j];

				int mid = 178;
				if (v > mid) {
					v = mid + ((v - mid) * 50) / 100;
				}
				else {
					v = mid - ((mid - v) * 80) / 100;
				}
				cA[j] = v;
			}

			cAy_fill[slot] = new Color(cA[0], cA[1], cA[2]);
		}
	}

}
