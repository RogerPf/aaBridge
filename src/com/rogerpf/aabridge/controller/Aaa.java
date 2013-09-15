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

//@formatter:off

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

import javax.swing.border.EmptyBorder;

import com.rogerpf.aabridge.model.Zzz;

public class Aaa {
	
	// mode
	public static final int NORMAL = 0;
	public static final int REVIEW_PLAY = 1;
	public static final int REVIEW_BIDDING = 2;
	public static final int EDIT_CHOOSE = 3;
	public static final int EDIT_HANDS = 4;
	public static final int EDIT_BIDDING = 5;
	public static final int EDIT_PLAY = 6;
	
	public static final int VisC__SHOW_NS_ONLY = 0;
	public static final int VisC__SHOW_ALL = 1;

	public static final int ABid__NONE = 0;
	public static final int ABid__ALL_BUT_SOUTH = 1;
	
	public static final int APlay__NONE = 0;
	public static final int APlay__EW_ONLY = 1;
	
	public static final int UNDO = 'U';
	public static final int ALERT = '!';

	public static final int CMD_SUIT  = 0x0100; //  C D H S
	public static final int CMD_SUITN = 0x0200; //  C D H S N
	public static final int CMD_FACE  = 0x0400; //  2 to 14
	public static final int CMD_LEVEL = 0x0800; //  1 to 7
	public static final int CMD_CALL  = 0x1000; //  Pass Double Redouble
	public static final int CMD_ADMIN = 0x2000; //  e.g. Undo
	public static final int CMD_ALERT = 0x4000; //  e.g. ! (alert)
                        
	public static final EmptyBorder emptyBorder = new EmptyBorder(0, 5, 2, 5);
                        
	public static final Color baizeGreen       = new Color(170, 210, 170);
	public static final Color baizePink        = new Color(210, 170, 170);
	public static final Color greenishWhite    = new Color(230, 240, 230);
	public static final Color darkGrayBg       = new Color(152, 152, 152);
                        
	public static final Color pressedColor     = new Color(199, 199, 199);
	public static final Color hoverColor       = new Color(235, 216, 140);
	public static final Color strongHoverColor = new Color(255, 210,   0);
	                    
	public static final Color weedyBlack       = new Color(110, 110, 110);
	public static final Color veryWeedyBlack   = new Color(200, 200, 200);
	public static final Color eotDotColor      = new Color(160, 160, 160);
	public static final Color veryVeryWeedyBlack= new Color(232, 232, 232);
	public static final Color veryVeryWeedyYel = new Color(215, 200, 130);
	public static final Color bidRequestLine   = new Color(255, 110, 255);
                        
	public static final Color bubbleAnotateCol = new Color(182, 171, 223);
	public static final Color bidAlertColor    = new Color(235, 225, 200);
	public static final Color bidEmpAlertColor = new Color(215, 215, 180);
	public static final Color biddingBkColor   = new Color(235, 225, 200);
	public static final Color bidButsBkColor   = new Color(199, 230, 240);
 	public static final Color bidTableBkColor  = new Color(205, 230, 230);
                        
	public static final Color optionsTitleGreen= new Color( 20,  90,  20);

	public static final Color cardClickedOn    = new Color(150, 150, 150);
//	public static final Color cardHover        = new Color(255, 206, 0);
	public static final Color cardHover        = new Color(240, 190, 0);
	public static final Color generalDarkGray  = new Color(128, 128, 0);
	public static final Color generalLightGray = new Color(203, 203, 203);
	public static final Color handAreaOffWhite = new Color(245, 240, 240);
	public static final Color handBkColorStd   = new Color(235, 230, 230);
	public static final Color handBkColorDummy = new Color(217, 213, 213);
	public static final Color vunOffWhite      = new Color(245, 245, 245);
	public static final Color youSeatBannerBk  = new Color(255, 230, 230);
	public static final Color othersBannerBk   = new Color(235, 233, 233);
	public static final Color youSeatBannerTxt = new Color(205, 205, 205);
	public static final Color othersBannerTxt  = new Color(205, 205, 205);
	public static final Color scoreBkColor     = new Color(190, 210, 220);
	public static final Color cardBackColor    = new Color(150, 150, 200);
	public static final Color cardBackColorClm = new Color(190, 190, 230);
	public static final Color handNeswBkColor  = new Color(141, 203, 193);
	public static final Color handActiveColor  = new Color(235, 216, 140);
	public static final Color vunerableColor   = new Color(203, 120, 120);
	public static final Color vunerabilityBox  = new Color(153, 204, 204);
	public static final Color genOffWhite      = new Color(245, 245, 245);
	public static final Color passButtonColor  = new Color(140, 200, 140);
	public static final Color dblButtonColor   = new Color(180,  60,  60);
                        
	public static final Color clubsSugColor    = new Color( 80, 140,  80);
	public static final Color diamondsSugColor = new Color(120, 160, 230);
	public static final Color heartsSugColor   = new Color(190, 140, 140);
	public static final Color spadesSugColor   = new Color(140, 140, 140);
	public static final Color notrumpsSugColor = new Color(140, 140, 140);
                        
	public static final Color clubsWeakColor   = new Color(140, 180, 140);
	public static final Color diamondsWeakColor= new Color(120, 160, 230);
	public static final Color heartsWeakColor  = new Color(190, 140, 140);
	public static final Color spadesWeakColor  = new Color(140, 140, 140);
	public static final Color notrumpsWeakColor= new Color(140, 140, 140);
                        
	public static final Color clubsColor       = new Color( 20, 120,  20);
	public static final Color diamondsColor    = new Color( 40,  70, 220);
	public static final Color heartsColor      = new Color(160,  20,  20);
	public static final Color spadesColor      = new Color( 40,  40,  40);
	public static final Color notrumpsColor    = new Color( 40,  40,  40);
                        
	public static final Color[] cdhsSugColors  = { clubsSugColor,  diamondsSugColor,  heartsSugColor,  spadesSugColor,  notrumpsSugColor };
	public static final Color[] cdhsWeakColors = { clubsWeakColor, diamondsWeakColor, heartsWeakColor, spadesWeakColor, notrumpsWeakColor };
	public static final Color[] cdhsColors     = { clubsColor,     diamondsColor,     heartsColor,     spadesColor,     notrumpsColor };
                        
	public static final Color noChosenSuit = new Color(90, 90, 90);
	
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
	public static float drawCenteredString(Graphics2D g2, String text, 
			float xOrg, float yOrg, float wOrg, float hOrg) {
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
	public static int cmdFromChar(char c) {
		// ************************************************************************
		if (('1' <= c) && (c <= '9')) {
			return ((('2' <= c) && (c <= '9')) ? Aaa.CMD_FACE : 0) | ((('1' <= c) && (c <= '7')) ? Aaa.CMD_LEVEL : 0)
					| (c - '0');
		}

		switch (c) {
		
		case 'u':
		case 'U':
			return Aaa.CMD_ADMIN | Aaa.UNDO;

		case '!':
			return Aaa.CMD_ALERT | Aaa.ALERT;
			
		case 'c':
		case 'C':
			return Aaa.CMD_SUITN | Aaa.CMD_SUIT | Zzz.Clubs;
		case 'd':
		case 'D':
			return Aaa.CMD_SUITN | Aaa.CMD_SUIT | Zzz.Diamonds;
		case 'h':
		case 'H':
			return Aaa.CMD_SUITN | Aaa.CMD_SUIT | Zzz.Hearts;
		case 's':
		case 'S':
			return Aaa.CMD_SUITN | Aaa.CMD_SUIT | Zzz.Spades;
		case 'n':
		case 'N':
			return Aaa.CMD_SUITN | Zzz.Notrumps;

		case '1':
		case 't':
		case 'T':
			return Aaa.CMD_FACE | 10;
		case 'j':
		case 'J':
			return Aaa.CMD_FACE | 11;
		case 'q':
		case 'Q':
			return Aaa.CMD_FACE | 12;
		case 'k':
		case 'K':
			return Aaa.CMD_FACE | 13;
		case 'a':
		case 'A':
			return Aaa.CMD_FACE | 14;

		case 'p':
		case 'P':
			return Aaa.CMD_CALL | Zzz.PASS;
		case '*':
			return Aaa.CMD_CALL | Zzz.DOUBLE;
		case 'r':
		case 'R':
			return Aaa.CMD_CALL | Zzz.REDOUBLE;
			
		}
		return 0;
	}
	
}
