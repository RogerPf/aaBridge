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

// @formatter:off

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import com.rogerpf.aabridge.model.Call;
import com.rogerpf.aabridge.model.Cc;
import com.rogerpf.aabridge.model.Rank;
import com.rogerpf.aabridge.model.Suit;

public class Aaa {

	// mode
	public static final int			NORMAL_ACTIVE		= 0;
	public static final int			REVIEW_PLAY			= 1;
	public static final int			REVIEW_BIDDING		= 2;
	public static final int			EDIT_HANDS			= 3;
	public static final int			EDIT_BIDDING		= 4;
	public static final int			EDIT_PLAY			= 5;

	// Tutorial mouse state
	public static final int			MOUSE_NONE			= 0;
	public static final int			MOUSE_PRESSED		= 1;
	public static final int			MOUSE_HOVER			= 2;

	public static final int			VisC__SHOW_NS_ONLY	= 0;
	public static final int			VisC__SHOW_ALL		= 1;

	public static final int			ABid__NONE			= 0;
	public static final int			ABid__ALL_BUT_SOUTH	= 1;

	public static final int			APlay__NONE			= 0;
	public static final int			APlay__EW_ONLY		= 1;

	public static final int			UNDO				= 'U';
	public static final int			ALERT				= '!';

	public static final int			upperLowerDif 		= ((int) 'a' - (int) 'A');
	
	// See RpfResizeButton
	public static final int			s_SelfCmd			=  0;
	public static final int			s_Std				=  1;
	public static final int			s_Label				=  2;
	public static final int			s_SelfLabel			=  3;
	public static final int			m_Std				= 11;
	public static final int			m_Label				= 12;
	public static final int			m_Hidden			= 13;



	public static final int			CMD_SUIT			= 0x0100;						// C D H S
	public static final int			CMD_SUITN			= 0x0200;						// C D H S N
	public static final int			CMD_FACE			= 0x0400;						// 2 to 14
	public static final int			CMD_LEVEL			= 0x0800;						// 1 to 7
	public static final int			CMD_CALL			= 0x1000;						// Pass Double Redouble
	public static final int			CMD_ADMIN			= 0x2000;						// e.g. Undo
	public static final int			CMD_ALERT			= 0x4000;						// e.g. ! (alert)
	
	public static final Color		darkGrayBg			= new Color(152, 152, 152);
	public static final Color		baizeGreen			= new Color(170, 210, 170);
	public static final Color		baizeGreenNav		= new Color(160, 200, 160);
	public static final Color		baizeGreen_bdk		= new Color(125, 165, 125);
	public static final Color		baizePink			= new Color(210, 170, 170);
	public static final Color		baizeMustard		= new Color(210, 210, 100);
	public static final Color		greenishWhite		= new Color(230, 240, 230);
	public static final Color		tutorialBackground	= new Color(243, 240, 240);

	public static final Color		navDarkNormal		= new Color(152, 152, 152);
	public static final Color		navDarkIntense		= new Color(140, 140, 140);
	public static final Color		navDarkText			= new Color(120, 120, 120);
	public static final Color		navLightText		= new Color(255, 255, 255);
	public static final Color		navLightNormal		= new Color(240, 240, 240);
	public static final Color		navLightIntense		= new Color(255, 255, 255);
	public static final Color		navClaimedNormal	= new Color(199, 230, 240);
	public static final Color		navClaimedIntense	= new Color(190, 220, 230);

	public static final Color		lightGrayBubble		= new Color(215, 215, 215);
	public static final Color		mediumGray			= new Color(190, 190, 190);
	
	public static final Color		buttonBkgColorStd	= new Color(199, 230, 240);
	public static final Color		buttonBkgColorYes   = new Color(180, 210, 140);
	public static final Color		buttonBkgColorNo    = new Color(199, 170, 140);
	public static final Color		trainExamUnSelectedBackground = new Color(190, 223, 255);

	public static final Color		pressedColor		= new Color(199, 199, 199);
	public static final Color		hoverColor			= new Color(235, 216, 140);
	public static final Color		strongHoverColor	= new Color(255, 210, 0);

	public static final Color		weedyBlack			= new Color(110, 110, 110);
	public static final Color		veryWeedyBlacHid	= new Color(180, 180, 180);
	public static final Color		veryWeedyBlack		= new Color(200, 200, 200);
	public static final Color		hoverButFontCol     = new Color( 80,  80,  80);
	public static final Color		selectedButFontCol  = new Color( 60,  60,  60);
	public static final Color		mButtonOutlineCol   = new Color( 40,  40,  40);
	public static final Color		veryVeryWeedyYel	= new Color(215, 200, 130);
	public static final Color		bidRequestLine		= new Color(255, 110, 255);

	public static final Color		biddingBkColor		= new Color(235, 225, 200);
	public static final Color		bidTableBkColor		= new Color(205, 230, 230);
	public static final Color		bidAlertBubbleCol	= new Color(196, 190, 227);
	public static final Color		bidAlertHasTxtColor = new Color(235, 225, 200);

	public static final Color		optionsTitleGreen	= new Color(20, 90, 20);
	public static final Color		optionsTitleBLue	= new Color(20, 20, 255);

	public static final Color		cardClickedOn		= new Color(150, 150, 150);
	public static final Color		cardHover			= new Color(240, 190, 0);
	public static final Color		generalLightGray	= new Color(203, 203, 203);
	public static final Color		handAreaOffWhite	= new Color(245, 240, 240);
	public static final Color		handBkColorStd		= new Color(235, 230, 230);

	public static final Color		handBkColorDummy	= new Color(208, 202, 202);
// 	public static final Color		handBkColorDummy	= new Color(212, 207, 207);
	public static final Color		vunOffWhite			= new Color(245, 245, 245);
	public static final Color		youSeatBannerBk		= new Color(255, 226, 226);
	public static final Color		othersBannerBk		= new Color(235, 233, 233);
	public static final Color		othersBannerTxt		= new Color(205, 205, 205);
	public static final Color		teamBannerTxt		= new Color(190, 190, 190);
	public static final Color		scoreBkColor		= new Color(190, 210, 220);
	public static final Color		cardBackColor		= new Color(150, 150, 200);
	public static final Color		cardBackColorClm	= new Color(190, 190, 230);
	public static final Color		handNeswBkColor		= new Color(141, 203, 193);
	public static final Color		handActiveColor		= new Color(235, 216, 140);
	public static final Color		vulnerableColor		= new Color(203, 120, 120);
	public static final Color		vulnerabilityBox	= new Color(153, 204, 204);
	public static final Color		genOffWhite			= new Color(245, 245, 245);
	public static final Color		passButtonColor		= new Color(140, 200, 140);
	public static final Color		dblButtonColor		= new Color(190, 120, 120);
	public static final Color		redblButtonColor	= new Color(120, 160, 230);
	public static final Color		tutMnGreenStrong	= new Color( 20,  90,  20);
	public static final Color		tutMnGreenWeak		= new Color(110, 150, 110);
	public static final Color		questionPanelBkColor= new Color(240, 233, 233);
	public static final Color		tut_old_text_gray	= new Color(150, 150, 150);
	public static final Color		tut_old_suit_gray	= new Color(180, 180, 180);
	public static final Color		tutorialLinkNorm_f	= new Color(  0,   0,   0);
	public static final Color		tutorialLinkNorm_g	= new Color( 16,  16, 225);
	public static final Color		tutorialLinkNorm_h	= new Color( 16,  16, 225);
	public static final Color		tutorialLinkHover_f	= new Color( 20, 180,  20);
	public static final Color		tutorialLinkHover_g	= new Color( 20, 180,  20);
	public static final Color		tutorialLinkHover_h	= new Color(208,  97,   0);

	public static final Color		blueTeamStrong		= new Color(123, 156, 206);
	public static final Color		blueTeamDD			= new Color(205, 230, 235);
	public static final Color		blueTeamBanner		= new Color(205, 225, 235);
	
	public static final Color		purpleTeamStrong	= new Color(190, 190, 230);
	public static final Color		purpleTeamDD		= new Color(217, 209, 247);
	public static final Color		purpleTeamBanner	= new Color(220, 216, 234);

	public static final Color[][]   teamBannerColorAy	= { {blueTeamBanner, purpleTeamBanner }, {purpleTeamBanner, blueTeamBanner} };
	public static final Color[][]   teamDDColorAy		= { {blueTeamDD,     purpleTeamDD     }, {purpleTeamDD,     blueTeamDD    } };

	/**
	 */
	public static void colorIntensityChange() {
		// ************************************************************************
		teamBannerColorAy[0][1] = Cc.g(Cc.purpleTeamBanner);
		teamBannerColorAy[1][0] = Cc.g(Cc.purpleTeamBanner);
	}

	/**
	 */
	public static void commonGraphicsSettings(Graphics2D g2) {
		// ************************************************************************
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	}

	/**
	 */
	public static float drawCenteredString(Graphics2D g2, String text, float xOrg, float yOrg, float wOrg, float hOrg) {
		// ************************************************************************
		FontMetrics fm = g2.getFontMetrics(g2.getFont());
		Rectangle2D rect = fm.getStringBounds(text, g2);
		int textHeight = (int) (rect.getHeight());
		int textWidth = (int) (rect.getWidth());

		float x = xOrg + (wOrg - textWidth) / 2f;
		float y = yOrg + (hOrg - textHeight) / 2f + fm.getAscent() * 0.95f;

		g2.drawString(text, x, y);
		return x;
	}

	/**
	 */
	public static void drawLeftString(Graphics2D g2, String text, float xOrg, float yOrg, float wOrg, float hOrg) {
		// ************************************************************************
		FontMetrics fm = g2.getFontMetrics(g2.getFont());
		Rectangle2D rect = fm.getStringBounds(text, g2);
		int textHeight = (int) (rect.getHeight());
		// int textWidth = (int) (rect.getWidth());

		// float x = xOrg + (wOrg - textWidth) / 2f;
		float y = yOrg + (hOrg - textHeight) / 2f + fm.getAscent() * 0.95f;

		g2.drawString(text, xOrg, y);
	}

	/**   
	 */
	public static int cmdFromChar(char c) {
		// ************************************************************************
		if (('1' <= c) && (c <= '9')) {
			return ((('2' <= c) && (c <= '9')) ? Aaa.CMD_FACE : 0) | ((('1' <= c) && (c <= '7')) ? Aaa.CMD_LEVEL : 0) | (c - '0');
		}

		switch (c) {

		case 'u':
		case 'U':
			return Aaa.CMD_ADMIN | Aaa.UNDO;

		case '!':
			return Aaa.CMD_ALERT | Aaa.ALERT;

		case 'c':
		case 'C':
			return Aaa.CMD_SUITN | Aaa.CMD_SUIT | Suit.Clubs.v;
		case 'd':
		case 'D':
			return Aaa.CMD_SUITN | Aaa.CMD_SUIT | Suit.Diamonds.v;
		case 'h':
		case 'H':
			return Aaa.CMD_SUITN | Aaa.CMD_SUIT | Suit.Hearts.v;
		case 's':
		case 'S':
			return Aaa.CMD_SUITN | Aaa.CMD_SUIT | Suit.Spades.v;
		case 'n':
		case 'N':
			return Aaa.CMD_SUITN | Suit.NoTrumps.v;

		case '1':
		case 't':
		case 'T':
			return Aaa.CMD_FACE | Rank.Ten.v;
		case 'j':
		case 'J':
			return Aaa.CMD_FACE | Rank.Jack.v;
		case 'q':
		case 'Q':
			return Aaa.CMD_FACE | Rank.Queen.v;
		case 'k':
		case 'K':
			return Aaa.CMD_FACE | Rank.King.v;
		case 'a':
		case 'A':
			return Aaa.CMD_FACE | Rank.Ace.v;

		case 'p':
		case 'P':
			return Aaa.CMD_CALL | Call.Pass.v;
		case '*':
		case 'x':
		case 'X':
			return Aaa.CMD_CALL | Call.Double.v;
		case 'r':
		case 'R':
			return Aaa.CMD_CALL | Call.ReDouble.v;

		}
		return 0;
	}

	/**
	 */
	public static int parseIntWithFallback(String s, int fallBack) {
		// ==============================================================================================
		int val;
		try {
			val = Integer.parseInt(s);
		} catch (NumberFormatException nfe) {
			val = fallBack;
		}
		return val;
	}

	/**
	 */
	public static int extractPositiveInt(String s) {
		// ==============================================================================================
		int n = 0;
		boolean found = false;
		for (int i=0; i < s.length(); i++) {
			char c = s.charAt(i);
			if ('0' <= c && c <= '9') {
				found = true;
				n = n * 10 + (c - (int)'0');
			}
			else {
				if (found)
					break;
			}
		}
		return found ? n : -1;
	}

	/**
	 */
	public static int extractPositiveIntOrZero(String s) {
		// ==============================================================================================
		int n = 0;
		boolean found = false;
		for (int i=0; i < s.length(); i++) {
			char c = s.charAt(i);
			if ('0' <= c && c <= '9') {
				found = true;
				n = n * 10 + (c - (int)'0');
			}
			else {
				if (found)
					break;
			}
		}
		return found ? n : 0;
	}

	/**
	 */
	public static int extractPositiveIntAfter(String s0, String after) {
		// ==============================================================================================
		int pos = s0.indexOf(after);
		if (pos < 0) 
			return -1;
		pos += after.length();
		return extractPositiveInt(s0.substring(pos));
	}

	/**
	 */
	public static String stripFrontDigitsAndClean(String s) {
		// ==============================================================================================
		int p = -1;
		for (int i=0; i < s.length(); i++) {
			char c = s.charAt(i);
			if ('0' <= c && c <= '9')
				p = i;
			else
				break;
		}
		s = s.substring(p + 1);
		
		p = -1;
		for (int i=0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == '_')
				p = i;
			else
				break;
		}
		s = s.substring(p + 1);
		s = s.trim();
		return s;
	}

	/**
	 */
	public static char toLower(char c) {
		// =============================================================================
		return ('A' <= c && c <= 'Z') ? (char) ((int) c + upperLowerDif) : c;
	}
	
	public final static String YourTextHere = "Your text here";

	/**
	 */
	public static String cleanString(String s, boolean spaceOk) {
		// =============================================================================
		if (s == null)
			return "";
		
		if (s.contentEquals(YourTextHere))
			s = "";

		if (s.length() > 0) {
			StringBuilder good = new StringBuilder();
			for (char c : s.toCharArray()) {
				if ((spaceOk && c == ' ') || c == '.' || c == '-' || Character.isJavaIdentifierPart(c)) {
					good.append(c);
				}
				else {
					good.append('_');
				}
			}
			s = "" + good;
		}
		return s;
	}

	/**
	 */
	public static String deAtAlertText(String s) {
		// =============================================================================
		if (s.length() == 0)
			return "";

		boolean exFound = (s.indexOf('!') >= 0);
		boolean atFound = (s.indexOf('@') >= 0);

		if (!atFound && !exFound)
			return s;

		if (exFound) {
			s = s.replace("!S", "@s");
			s = s.replace("!s", "@s");
			s = s.replace("!H", "@h");
			s = s.replace("!h", "@h");
			s = s.replace("!D", "@d");
			s = s.replace("!d", "@d");
			s = s.replace("!C", "@c");
			s = s.replace("!c", "@c");
		}
		
		if (atFound) {
			s = s.replace("@S", "@s");
//			s = s.replace("@s", "@s");
			s = s.replace("@H", "@h");
//			s = s.replace("@h", "@h");
			s = s.replace("@D", "@d");
//			s = s.replace("@d", "@d");
			s = s.replace("@C", "@c");
//			s = s.replace("@c", "@c");
		}
		
		//s = s.replace("!hH", "!h H"); // so  !HHxx is less confusing now being  !H Hxx
		
		return s;
	}


	/**
	 */
	public static String deAtQuestionAndBubbleText(String s) {
		// =============================================================================
		if (s.length() == 0)
			return "";

		boolean exFound = (s.indexOf('!') >= 0);
		boolean atFound = (s.indexOf('@') >= 0);

		if (!atFound && !exFound)
			return s;

		if (exFound) {
			s = s.replace("!S", "S");
			s = s.replace("!s", "S");
			s = s.replace("!H", "H ");
			s = s.replace("!h", "H ");
			s = s.replace("!D", "D");
			s = s.replace("!d", "D");
			s = s.replace("!C", "C");
			s = s.replace("!c", "C");
		}

		if (atFound) {
			s = s.replace("@S", "S");
			s = s.replace("@s", "S");
			s = s.replace("@H", "H ");
			s = s.replace("@h", "H ");
			s = s.replace("@D", "D");
			s = s.replace("@d", "D");
			s = s.replace("@C", "C");
			s = s.replace("@c", "C");
		}

		//s = s.replace("!hH", "!h H"); // so  !HHxx is less confusing now being  !H Hxx

		return s;
	}
	
	private static String linFileEditorPath = null;

	public static String getLinFileEditorPath() {
		// ==============================================================================================
		
		if (linFileEditorPath != null) {
			return linFileEditorPath;
		}
		
		if (!App.onWin) {
			return linFileEditorPath = "desktop";
		}
		
		String np = "";
		boolean found = false;

		if (!found) {
			np = "C:\\Program Files\\Notepad++\\notepad++.exe";
			found = new File(np).exists();
		}

		if (!found) {
			np = "C:\\Program Files (x86)\\Notepad++\\notepad++.exe";
			found = new File(np).exists();
		}

		if (!found) {
			np = "C:\\ProgramRPf\\Notepad++\\notepad++.exe";
			found = new File(np).exists();
		}
		
		return found ? np : "";
	}
	
	
	public static URLClassLoader makeJarZipLoader(String pathToJarOrZip)  {
		// ==============================================================================================
		URL[] urls = null;
		try {
			// System.out.println("jarOrZipName " + jarOrZipName);
			urls = new URL[] { new File(pathToJarOrZip).toURI().toURL() };
			return new URLClassLoader(urls);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}


	public static final int uniThreashold = 191;

	public static boolean hasUni(String data) {
		// =============================================================================
		boolean found = false;
		for (int i = 0; i < data.length(); i++) {
	        found |= (data.charAt(i) > uniThreashold);
	    }
		return found;
	}
	
	public static boolean hasUni(char c) {
		// =============================================================================
		return  c > uniThreashold;
	}
	
	public static boolean isLatinFaceCard(char c) {
		// =============================================================================
		return  ('2' <= c && c <= '9') || ('A' <= c && c <= 'Z') || (c == 't');
	}
	
}
