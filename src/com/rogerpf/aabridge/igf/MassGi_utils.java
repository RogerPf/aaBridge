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
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.rogerpf.aabridge.controller.AaBridge;
import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.controller.q_;
import com.rogerpf.aabridge.igf.MassGi.GraInfo;
import com.rogerpf.aabridge.model.Bid;
import com.rogerpf.aabridge.model.Card;
import com.rogerpf.aabridge.model.Deal;
import com.rogerpf.aabridge.model.Dir;
import com.rogerpf.aabridge.model.Hand;
import com.rogerpf.aabridge.model.Lin;

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
				// System.out.println(type + ">" + line);
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
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
				points =   d.hands[Dir.South.v].countHighCardPoints();
				break;
			case 't':
				points =   d.hands[Dir.South.v].countHighCardPoints()
				         + d.hands[Dir.South.v].countLongSuitPoints();
				break;
			case 'l':
				points =   d.hands[Dir.South.v].countLongSuitPoints();
				break;
			case 's':
				points =   d.hands[Dir.South.v].countShortSuitPoints();
				break;
			case 'd':
				points =   d.hands[Dir.South.v].countHighCardPoints()
				         + d.hands[Dir.South.v].countShortSuitPoints();
				break;
		}
		return points;
		// @formatter:on
	}

	public static void do_tutorialBackToMovie() {
		// ==========================================================================

		/** So we must want to transition  into 'tutorial' aka movie mode
		 */

		App.setMode(Aaa.NORMAL_ACTIVE);
		App.setVisualMode(App.Vm_DealAndTutorial);
		App.gbp.matchPanelsToDealState();
		App.mg.setTheReadPoints(App.mg.stop_gi, false);
		App.frame.repaint();
	}

	public static void do_tutorialIntoDealStd() {
		// ==========================================================================

		if (App.visualMode != App.Vm_DealAndTutorial)
			return;

		/** We are in a tutorial mode and wish to examine the current App.deal	
		 */
		if (App.deal.isSaveable() == false)
			return;

		App.localShowHidden = false;

		if (App.respectLinYou == false) { // full complex tutorials do their own thing
			App.deal.youSeatHint = App.deal.contractCompass.rotate(App.youSeatForLinDeal.rotate(Dir.South));
		}

		App.setVisualMode(App.Vm_InsideADeal);

		if (App.deal.isBidding()) {
			App.setMode(Aaa.REVIEW_BIDDING);
			App.reviewBid = 0;
			App.reviewTrick = 0;
			App.reviewCard = 0;
		}
		else {
			if ((App.mg.lin.linType == Lin.FullMovie) && App.showRedEditArrow)
				App.gbo.showEditHint();

			App.setMode(Aaa.REVIEW_PLAY);
			App.reviewBid = 0;
			App.reviewTrick = 0;
			App.reviewCard = (App.deal.countCardsPlayed() > 0) ? 1 : 0;
		}

	}

	public static void do_tutorialIntoDealClever() {
		// ==========================================================================

		if (App.visualMode != App.Vm_DealAndTutorial)
			return;

		/** We are in a tutorial mode and wish to examine the current App.deal
		 *  First we do some safety checks	
		 */
		if (App.deal.isSaveable() == false)
			return; /* the user needs to be on deal that can be entered */

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
		// App.mg.setTheReadPoints(pg_cand, false /* fwd__not_currently_used */); not used see below

		// So we must make our own clone to stop the original being changed by "Play"
		App.deal = best_cand.deepClone();

		App.calcCompassPhyOffset();
		App.dealMajorChange();
		do_tutorialIntoDealStd();

		App.gbp.matchPanelsToDealState();

	}

	public static void do_launchLinFile(String filename) {
		// ==========================================================================
		ArrayList<String> log = new ArrayList<String>(); // ultra simple log maker

		do_launchLinFile_inner(log, filename);

		// save the results to a log file
		try {
			String logFilePath = App.autoSavesPath + File.separator + "aaBridge__launch_lin_file__log.txt";
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
			// Sigh - what do they expect the poor programmer to do !
		}
	}

	public final static String addPositionInfo = "addPositionInfo";

	public static void do_launchLinFile_inner(ArrayList<String> log, String filename) {
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

		if (runTargetName.endsWith(".jar")) {

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

		commands[prams_at++] = filename;

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
			 * We are the answer to a the   pick a card  question
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

		displayUrlInBrowser(origUrl); /* use the origina incase our redirection is not working */
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

		String filename = linUrl.substring(end, linUrl.length() - 4 /* the .lin */);

		filename = App.autoSavesPath + filename + "_" + new Date().getTime() + ".lin";

		try {
			URL website = new URL(linUrl);
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			FileOutputStream fos = new FileOutputStream(filename);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
			MassGi_utils.do_launchLinFile(filename);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
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

	Color color_bg; // bg is the background color
	Color color_cp; // cp is font color
	Color color_cq; // cq is question box fill color
	Color color_cs; // cs is self made box fill

	int lb_position; // /* question position override - 'u' 20th letter counting 'a' = 0 means bottom of the screen as normal */

	boolean pdl_allSeatsVisible;
	boolean pdl_auctionVisible;
	int visualModeRequested;

	int font_slot_fp;

	Hyperlink hyperlink;

	boolean mn_showing;
	boolean mn_hideable_by_pg;
	int mn_lines;
	int mn_pg_countDown;
	String mn_text;

	/**
	 */
	Capture_gi_env(Capture_gi_env o) { // 'Copy' Constructor
		// ==========================================================================

		centered = o.centered;
		bold = o.bold;
		italic = o.italic;
		underline = o.underline;

		boxed = o.boxed;

		color_bg = o.color_bg;
		color_cp = o.color_cp; // This is the default colour used by font 0
		color_cq = o.color_cq;
		color_cs = o.color_cs;

		lb_position = o.lb_position;

		pdl_allSeatsVisible = o.pdl_allSeatsVisible;
		pdl_auctionVisible = o.pdl_auctionVisible;
		visualModeRequested = o.visualModeRequested;

		font_slot_fp = o.font_slot_fp;

		hyperlink = o.hyperlink;

		mn_showing = o.mn_showing;
		mn_hideable_by_pg = o.mn_hideable_by_pg;
		mn_lines = o.mn_lines;
		mn_pg_countDown = o.mn_pg_countDown;
		mn_text = o.mn_text;
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

		color_bg = Color.WHITE;
		color_cp = Color.BLACK; // This is the default colour used by font 0
		color_cq = Color.WHITE;
		color_cs = Color.WHITE;

		lb_position = 20; /* 'u' 20th letter counting 'a' = 0  means bottom of the screen as normal */

		pdl_allSeatsVisible = true;
		pdl_auctionVisible = true;
		visualModeRequested = App.Vm_DealAndTutorial;

		font_slot_fp = 0; // interal slot that is set to Ariel !

		hyperlink = null;

		mn_showing = true;
		mn_hideable_by_pg = true;
		mn_lines = 0;
		mn_pg_countDown = 0;
		mn_text = "";

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

		// lb_position unchanged

		// pdl unchanged

		hyperlink = null;

		// mn_showing unchanged pg clear mn-showing;

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

	public void actionLink() {
		// =============================================================================
		assert (false); // always overridden
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
	public void actionLink() {
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
	public void actionLink() {
		// =============================================================================
		String origUrl = getLinkInfo();

		long now = new Date().getTime();

		if (lastExternalUrlClicked + 5000 /* 5 seconds */> now)
			return;

		lastExternalUrlClicked = now;

		new externalUrlLauncher(origUrl, loadIfLin).start();
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
	public void actionLink() {
		// =============================================================================
		/** so this is an internal linl
		 */

		String label = getLinkInfo().toLowerCase();

		if (label.contains(":") == false) {
			/* Standard local (in this lin file) labels do not contain colons
			 */
			jumpToQxLabel(label);
			return;
		}

		/** we need to look in other lin files in this book
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
						if (gi2.qt == q_.pg) {
							mg.setTheReadPoints(gi2.index, false);
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
