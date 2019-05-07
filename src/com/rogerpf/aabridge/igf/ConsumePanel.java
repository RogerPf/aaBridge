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
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.Map;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.igf.MassGi.FontBlock;
import com.rogerpf.aabridge.igf.MassGi.GraInfo;
import com.rogerpf.aabridge.model.Cc;
import com.rogerpf.aabridge.model.Cc.Ce;
import com.rogerpf.aabridge.model.Suit;
import com.rpsd.bridgefonts.BridgeFonts;

public class ConsumePanel extends SeglinePanel {
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	public ConsumePanel() { // constructor
		// ==============================================================================================

	}

	/**
	 */
	public void consume_newline(GraInfo gi) {
		// =============================================================================

		FontBlock fb = mg.fbAy[gi.capEnv.font_slot_fp];
		@SuppressWarnings("unchecked")
		Map<TextAttribute, Object> attributes = (Map<TextAttribute, Object>) fb.font.getAttributes();
		attributes.put(TextAttribute.SIZE, (float) fb.linFontSize * fontScaleFrac);
		Font font = fb.font.deriveFont(attributes);
		g2.setFont(font);
		heightOfCurFont = heightOfCurFontFrac * (float) g2.getFontMetrics().getHeight();
		if (maxHeightOnCurLine < heightOfCurFont)
			maxHeightOnCurLine = heightOfCurFont;

//		System.out.println(">>>>>>>++++++++++++++  " + gi.capEnv.font_slot_fp + "  " + heightOfCurFont + "   " + heightOfCurFontFrac + "  "
//				+ g2.getFontMetrics().getHeight());

		String oneChar = " ";

		Ras ras = new Ras(oneChar);
		ras.addAttribute(TextAttribute.FONT, font);

		float x = xCol;
		float y = yRow;

		ras.calcLayout(frc, x, y);
		ras.hidden = true;

		// Seg seg = getCurSeg();

		segs.addPart(ras, gi);

		setCurSegEol();

		newLine__sets_yRow_xCol();

		maxHeightOnCurLine = heightOfCurFont;
	}

	/**
	 */
	public void newLine__sets_yRow_xCol() {
		// =============================================================================
		float spacing = heightOfCurFont * lineSpacing_multiplier;

		if (nonFont_on_this_line && (maxHeightOnCurLine > spacing)) {
			yRow += maxHeightOnCurLine;
		}
		else {
			yRow += spacing;
		}

		nonFont_on_this_line = false;
		xCol = leftMargin;
		// heightOfCurFont should be cleared if appropriate by the caller
	}

	/**
	 */
	public void consume_centeringBegin(GraInfo gi) {
		// =============================================================================
		startNewSeg();
		// getCurSeg().lineCentered = true; // BAD
	}

	/**
	 */
	public void consume_centeringEnd(GraInfo gi) {
		// =============================================================================
		Seg seg = getCurSeg();
		seg.eol = true;
		// seg.lineCentered = true; // not needed as already done
	}

	private static int boxNumber = 0;

	/**
	 */
	public void consume_boxDrawBegin(GraInfo gi) {
		// =============================================================================
		startNewSeg();
		Seg seg = getCurSeg();
		seg.boxNumber = ++boxNumber;
		seg.fillColor = gi.capEnv.color_cs;
	}

	/**
	 */
	public void consume_boxDrawEnd(GraInfo gi) {
		// =============================================================================

//		RpfLine from = null;
//		int i;
//		for (i = 0; i < lines.size(); i++) {
//			from = lines.get(i);
//			if (from.boxNumber == boxNumber) {
//				break; // must at least find us
//			}
//		}
//		
//		for (int j = i; j < lines.size(); j++) {
//			RpfLine seg = lines.get(j);
//			seg.boxNumber = boxNumber;
//		}

		Seg seg = startNewSeg();
		seg.boxNumber = 0;

	}

	/**
	 */
	public void consume_hyperlinkBegin(GraInfo gi) {
		// =============================================================================

		Seg seg = startNewSeg();
		seg.hyperlink = gi.capEnv.hyperlink;

	}

	/**
	/**
	 */
	public void consume_hyperlinkEnd(GraInfo gi) {
		// =============================================================================

		Seg seg = startNewSeg();
		seg.hyperlink = null;

	}

	/**
	 */
	public void consume_wipePartOfScreen(GraInfo gi, int style) {
		// =============================================================================

		Seg seg = startNewSeg();
		seg.wipePartOfScreen = style;

		seg.fillColor = gi.capEnv.backgroundDarker ? Aaa.backgroundDarkerCol : Aaa.tutorialBackground;

		FontBlock fb = mg.fbAy[gi.capEnv.font_slot_fp];

		@SuppressWarnings("unchecked")
		Map<TextAttribute, Object> attributes = (Map<TextAttribute, Object>) fb.font.getAttributes();
		attributes.put(TextAttribute.SIZE, (float) fb.linFontSize * fontScaleFrac);

		Font font = fb.font.deriveFont(attributes);

		g2.setFont(font);

		float x = xCol;
		float y = yRow;

		Ras ras = new Ras("."); // any non null character that will never be displayed
		ras.addAttribute(TextAttribute.FONT, font);

		ras.calcLayout(frc, x, y); // this way we can adjust were the y will start in the clear process
		ras.hidden = true;

		segs.addPart(ras, gi);
	}

	/**
	 */
	public void consume_insertHand(GraInfo gi) {
		// =============================================================================

		int wh[] = { 0, 0 };

		float yRowAdjust = maxHeightOnCurLine * 0.70f;

		if (yRow < yRowAdjust)
			yRowAdjust = 0;

		yRow -= yRowAdjust;

		gi.hdg.setPositionReturnSize((int) xCol, (int) yRow, App.tup.getWidth(), wh);

		xCol += wh[0];
		maxHeightOnCurLine = wh[1]; // + heightOfCurFont;
		nonFont_on_this_line = true;

		if (xCol > rightMargin) {
			newLine__sets_yRow_xCol();
		}

		yRow += yRowAdjust;

		if (gi.capEnv.centered) {
			Ras ras = new Ras("", gi.hdg);
			ras.y = yRow;
			ras.height = wh[0];
			ras.width = wh[1];
			getCurSeg().open = false;
			Seg seg = segs.addPart(ras, gi, false); // so we have a one ras seg
			seg.surpressBox = true;
		}
		else {
			Seg seg = getCurSeg();
			seg.surpressBox = true;
		}
	}

	/**
	 */
	public void consume_insertAuction(GraInfo gi) {
		// =============================================================================

		int wh[] = { 0, 0 };

		gi.btp.setPositionReturnSize((int) xCol, (int) (yRow - heightOfCurFont), App.tup.getWidth(), wh);

		xCol += wh[0];
		maxHeightOnCurLine = wh[1];
		nonFont_on_this_line = true;

		if (xCol > rightMargin) {
			newLine__sets_yRow_xCol();
			// maxHeightOnCurLine = heightOfCurFont;
		}

		if (gi.capEnv.centered) {
			Ras ras = new Ras("", gi.btp);
			ras.y = yRow;
			ras.height = wh[0];
			ras.width = wh[1];
			getCurSeg().open = false;
			Seg seg = segs.addPart(ras, gi, false); // so we have a one ras seg
			seg.surpressBox = true;
		}
		else {
			Seg seg = getCurSeg();
			seg.surpressBox = true;
		}

	}

	final float SYMBOL_SCALE_FRAC = 1.15f;
	final float SYMBOL_YPOS_ADJUST = 0.2f;

	/**
	 */
	public void consume_suitSymbol(GraInfo gi, char type) {
		// =============================================================================

		// first we work out the seg separation for the current font
		FontBlock fb = mg.fbAy[gi.capEnv.font_slot_fp];
		@SuppressWarnings("unchecked")
		Map<TextAttribute, Object> attributes = (Map<TextAttribute, Object>) fb.font.getAttributes();
		attributes.put(TextAttribute.SIZE, (float) fb.linFontSize * fontScaleFrac);
		Font font_fp = fb.font.deriveFont(attributes);
		g2.setFont(font_fp);
		heightOfCurFont = heightOfCurFontFrac * (float) g2.getFontMetrics().getHeight();
		if (maxHeightOnCurLine < heightOfCurFont)
			maxHeightOnCurLine = heightOfCurFont;

		// now we make our real symbol font
		float fontSize = fb.linFontSize * fontScaleFrac * SYMBOL_SCALE_FRAC;
		Font font = BridgeFonts.faceAndSymbolFont.deriveFont(fontSize);
		g2.setFont(font_fp);

		String oneChar = "";
		boolean symbol = true;
		// @formatter: off
		switch (type) {
		case 'z':
			oneChar += type;
			break;
		case '{':
			oneChar += type;
			break;
		case '}':
			oneChar += type;
			break;
		default:
			symbol = false;
			oneChar = Suit.cdhs[gi.numb].toStrLower();
		}
		// @formatter: on

		// Line wrap test
		float available = rightMargin - xCol;
		float wanted = (float) g2.getFontMetrics().stringWidth(oneChar);
		if (wanted > available) {
			newLine__sets_yRow_xCol();
			maxHeightOnCurLine = heightOfCurFont;
			setCurSegEol();
		}

		float x = xCol;
		float y = yRow;

		Ras ras = new Ras(oneChar);
		ras.addAttribute(TextAttribute.FONT, font);
		Color useColor = (use_gray_text ? Aaa.tut_old_suit_gray : (symbol ? Cc.BlackStrong : Suit.cdhs[gi.numb].color(Ce.Strong)));
//		Color useColor = (use_gray_text ? Suit.cdhs[gi.numb].color(Ce.Weedy) : Suit.cdhs[gi.numb].color(Ce.Strong));
//		Color useColor = Suit.cdhs[gi.numb].color(Ce.Strong);
		ras.addAttribute(TextAttribute.FOREGROUND, useColor);

		ras.calcLayout(frc, x, y);

		segs.addPart(ras, gi);

		xCol += ras.width;
	}

	/**
	 */
	public void consume_emDash(GraInfo gi) {
		// =============================================================================
		consume_common(gi, (char) 0x2014);
	}

	/**
	 */
	public void consume_bulletPoint(GraInfo gi) {
		// =============================================================================
		consume_common(gi, (char) 0x25cf);
	}

	/**
	 */
	public void consume_common(GraInfo gi, char char16b /* a 16 bit char */) {
		// =============================================================================

		FontBlock fb = mg.fbAy[gi.capEnv.font_slot_fp];
		@SuppressWarnings("unchecked")
		Map<TextAttribute, Object> attributes = (Map<TextAttribute, Object>) fb.font.getAttributes();
		attributes.put(TextAttribute.SIZE, (float) fb.linFontSize * fontScaleFrac);
		Font font = fb.font.deriveFont(attributes);
		// g2.setFont(font);
		// heightOfCurFont = heightOfCurFontFrac * (float) g2.getFontMetrics().getHeight();

		String oneChar = "" + char16b;

		Ras ras = new Ras(oneChar);
		ras.addAttribute(TextAttribute.FONT, font);
		Color useColor = (use_gray_text && !isBoxed() ? Aaa.tut_old_text_gray : gi.capEnv.color_cp);
//		Color useColor = gi.capEnv.color_cp;
		ras.addAttribute(TextAttribute.FOREGROUND, useColor);
		if (gi.capEnv.underline) {
			ras.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		}
		// Line wrap test
		float available = rightMargin - xCol;
		float wanted = (float) g2.getFontMetrics().stringWidth(oneChar);
		if (wanted > available) {
			// emDash ignores the EOL but sets it for others to obay
			// newLine__sets_yRow_xCol();
			setCurSegEol();
		}

		float x = xCol;
		float y = yRow;

		ras.calcLayout(frc, x, y);

		segs.addPart_ignoreEOL(ras, gi); // emDash's NEVER causes a line break;

		xCol += ras.width;
	}

	/**
	 */
	public void consume_nt(GraInfo gi) { // any text is processes as if it were an 'at'
		// =============================================================================
		leftMargin = LEFT_MARGIN_DEFAULT * columnWidth;
		rightMargin = RIGHT_MARGIN_DEFAULT * columnWidth;

		yRow = 0.0f + topAdjust;
		xCol = leftMargin;

		lineSpacing_multiplier = App.now_always_one(); // as we calc this at display time not via capenv.

		consume_at(gi); // uses the standard 'text'
	}

	/**
	 */
	public void consume_mn(GraInfo gi) {
		// =============================================================================
		/** 
		 * All of the work (and there is a LOT of it) is done in  consume_mn_TEXT  which
		 * called as a special first thing in every display parse ;
		 * 
		 * the current mn_header text travels in the capEnv 
		 */
	}

	/**
	 * @param mn_text_uni 
	 */
	public void consume_mn_TEXT(GraInfo gi) {
		// =============================================================================

		// Nasty wide area flag that tells us we are in reduced window mode
		if (skip__mn_text || gi.capEnv.mn_show_tu == false)
			return;

		String mn_text = gi.capEnv.mn_text;
		int mn_text_len = mn_text.length();
		if (mn_text_len == 0)
			return;

		FontBlock fb = mg.fbAy[MassGi.mn_header_font_slot]; //
		Font font_in = (Aaa.hasUni(mn_text) ? BridgeFonts.internationalFont : fb.font);

		@SuppressWarnings("unchecked")
		// Map<TextAttribute, Object> attributes = (Map<TextAttribute, Object>) fb.font.getAttributes();
		Map<TextAttribute, Object> attributes = (Map<TextAttribute, Object>) font_in.getAttributes();

		attributes.put(TextAttribute.SIZE, ((float) fb.linFontSize) * fontScaleFrac);
		attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);

		Font font = fb.font.deriveFont(attributes);
		g2.setFont(font);

		int available = g2.getFontMetrics().stringWidth("An Mn Header Used as a Size Discovery Test I");
		int wanted = g2.getFontMetrics().stringWidth(mn_text);

		int lines = 1;
		if (wanted > 2 * available)
			return; // two lines max or nothing, as per BBO

		if (wanted > available)
			lines = 2;

		if (lines == 1) {
			lines = (gi.capEnv.mn_lines); // in case two lines were ordered anyway
		}

		ArrayList<String> tAy = new ArrayList<String>();

		float y = 0;

		if (lines == 2) {
			int mid = mn_text_len / 2;
			int max_search = (mid + 20);
			if (max_search > mn_text_len)
				max_search = mn_text_len;
			for (int i = mid; i < max_search; i++) { // find a space to break at
				if (mn_text.charAt(i) == ' ') {
					mid = i;
					break;
				}
			}
			tAy.add("    " + mn_text.substring(0, mid) + "    ");
			tAy.add(mn_text.substring(mid + 1));

			y = INITIAL_MN_HEADER_Y_TWO_LINE * rowSpacing;
		}
		else {
			tAy.add("    " + mn_text + "    ");
			y = INITIAL_MN_HEADER_Y_ONE_LINE * rowSpacing;
		}

		Color textColor = Aaa.tutorialBackground;
		Color fillColor = App.mnHeaderColor;

		int seg_boxNumber = ++boxNumber;

		for (int i = 0; i < tAy.size(); i++) {
			String segText = tAy.get(i);

			Ras ras = new Ras(segText);

			ras.addAttribute(TextAttribute.FONT, font);
			ras.addAttribute(TextAttribute.FOREGROUND, textColor);

			ras.calcLayout(frc, 0 /* x */, y);
			ras.x = (width /* full width of the panel */ - ras.width) / 2;

			y += ras.height;

			startNewSeg();
			Seg seg = segs.addPart(ras, gi);
			seg.boxNumber = seg_boxNumber;
			seg.fillColor = gi.capEnv.color_cs;
			seg.open = false;
			seg.eol = true;
			seg.fillColor = fillColor;
			seg.mnBox = true;
		}
		startNewSeg().boxNumber = 0;
	}

	public static float DIS_NUMB_X = 0.96f;
	public static float DIS_NUMB_TALL_Y = 0.985f;
	public static float DIS_NUMB_SML_Y = 0.947f;

	/**
	 */
	public void consume_add_fake_page_number(GraInfo gi) {
		// =============================================================================

		if (gi.capEnv.page_numb_display == 0)
			return; // we do not display on the zero'th page (opening screen)

		// This only called as the last item of a screen paint so it can do no real harm
		// as of course screen paints are discarded directly after being painted

		FontBlock fb = mg.fbAy[0];
		@SuppressWarnings("unchecked")
		Map<TextAttribute, Object> attributes = (Map<TextAttribute, Object>) fb.font.getAttributes();
		attributes.put(TextAttribute.SIZE, ((float) fb.linFontSize) * fontScaleFrac);

		Font font = fb.font.deriveFont(attributes);
		g2.setFont(font);

		float x = width * DIS_NUMB_X;
		float y = height * ((gi.capEnv.visualModeRequested == App.Vm_TutorialOnly) ? DIS_NUMB_TALL_Y : DIS_NUMB_SML_Y);

		Ras ras = new Ras("" + gi.capEnv.page_numb_display);

		ras.addAttribute(TextAttribute.FONT, font);
		ras.addAttribute(TextAttribute.FOREGROUND, Color.BLACK);

		ras.calcLayout(frc, x, y);

		Seg seg = segs.addPart(ras, gi, false /* true => leaveOpen */);
		seg.lineCentered = false;
	}

	static String forceFontZero = "forceFontZero";
	static String std = "std";

	/**
	 */
	public void consume_at__uni(GraInfo gi) {
		// =============================================================================
		// if (Aaa.hasUni(gi.text)) { // this does something strange !
		boolean orig = gi.uni;
		gi.uni = true;
		consume_at(gi);
		gi.uni = orig;
		// }
	}

	/**
	 */
	public void consume_at(GraInfo gi) {
		// =============================================================================
		if (gi.text.isEmpty()) {
			return; // this will normally be an 'nt' which rarely has text
		}

		FontBlock fb = mg.fbAy[gi.capEnv.font_slot_fp];
		Font fromFont = (gi.uni ? BridgeFonts.internationalFont : fb.font);

		@SuppressWarnings("unchecked")
		Map<TextAttribute, Object> attributes = (Map<TextAttribute, Object>) fromFont.getAttributes();

		attributes.put(TextAttribute.SIZE, ((float) fb.linFontSize) * fontScaleFrac);
		if (gi.capEnv.italic || fb.italic == true) {
			attributes.put(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
		}
		if (gi.capEnv.bold || fb.bold >= 5) {
			attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
		}
		Font font = fromFont.deriveFont(attributes);

		g2.setFont(font);
		heightOfCurFont = heightOfCurFontFrac * (float) g2.getFontMetrics().getHeight();

		if (maxHeightOnCurLine < heightOfCurFont)
			maxHeightOnCurLine = heightOfCurFont;

		// System.out.println(">>>>>>>>>>" + heightOfCurFont + "   " + heightOfCurFontFrac + "  " + g2.getFontMetrics().getHeight());

		String remainder = gi.text;
		String thisBite = "";

		while (remainder.length() > 0) {

			int okChars = calcBiteSize(remainder);
			if (okChars == 0) {
				newLine__sets_yRow_xCol();
				maxHeightOnCurLine = heightOfCurFont;
				// heightOfCurFont unchanged
				continue;
			}

			thisBite = remainder.substring(0, okChars);
			remainder = remainder.substring(okChars);

			float x = xCol;
			float y = yRow;

			Ras ras = new Ras(thisBite);
			ras.addAttribute(TextAttribute.FONT, font);

			Color useColor = gi.capEnv.color_cp;
			if (use_gray_text && !isBoxed()) {
				useColor = Aaa.tut_old_text_gray;
			}
			else {
				Hyperlink hyperlink = getCurSeg().hyperlink;
				if (hyperlink != null) {
					useColor = hyperlink.getLinkColor(gi.capEnv.color_cp);
				}
			}

			ras.addAttribute(TextAttribute.FOREGROUND, useColor);

			if (gi.capEnv.underline) {
				ras.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
			}

			ras.calcLayout(frc, x, y);

			segs.addPart(ras, gi, (remainder.length() == 0) /* true => leaveOpen */);

			if (remainder.length() > 0) {
				xCol = leftMargin;
				newLine__sets_yRow_xCol();
				maxHeightOnCurLine = heightOfCurFont;
				// heightOfCurFont unchanged
				continue;
			}

			xCol += ras.width;
		}
	}

	final static int BASE_POINT = 5;
	final static int LOW_POINT = 20;

	/**
	 */
	private int calcBiteSize(String core) {
		// =============================================================================

		int okChars = 0;

		int coreLen = core.length();

		float available;
		float wanted;

		available = rightMargin - xCol;
		wanted = (float) g2.getFontMetrics().stringWidth(core);

		if (wanted < available) {
			return coreLen;
		}

		okChars = (int) ((float) coreLen * available / wanted);

		while (true) {

			// System.out.println(okChars);
			if (okChars == 0) {
				// well we may have negative or zero space BUT we can still eat a spaces

				for (int k = 0; k < coreLen; k++) {
					if (core.charAt(k) != ' ')
						return k;
				}
				return 0;
			}

			if (okChars < 0 || okChars > coreLen) {
				@SuppressWarnings("unused")
				int z = 0; // put your breakpoint here
				// eek
				return 0;
			}

			char c = core.charAt(okChars - 1);

			if (c != ' ') {
				// hunt for the first space
				// int lowest = (okChars < (LOW_POINT + 5)) ? 5 : okChars - LOW_POINT;
				int lowest = 1; // to be like netbridgevu
				int i = okChars;
				while (c != ' ' && lowest <= i) {
					c = core.charAt(--i);
					if (c == ' ') {
						okChars = i + 1;
						break;
					}
				}
			}

			if (c == ' ') {
				// hunt for the first non space
				int lowest = (okChars < (LOW_POINT + 5)) ? 5 : okChars - LOW_POINT;
				int i = okChars;
				while (c == ' ' && lowest < i) {
					c = core.charAt(--i);
					if (c != ' ') {
						okChars = i + 1;
						break;
					}
				}
			}

			// Check
			// Here we should be down to good space break
			wanted = (float) g2.getFontMetrics().stringWidth(core.substring(0, okChars));

			if (wanted > available && okChars > BASE_POINT) {
				okChars--;
				continue;
			}

			// We have passed the test for size now we climb up again
			// taking any spaces

			// Is the next one a space (assuming we are not) ie are we the end of a word.
			c = core.charAt(okChars - 1);
			if (c != ' ' && okChars < coreLen && core.charAt(okChars) == ' ') {
				c = ' ';
				okChars++; // drop into the next loop
			}
			if (c == ' ') {
				// swallow any white space after us
				while (c == ' ' && okChars < coreLen) {
					c = core.charAt(okChars);
					if (c == ' ') {
						okChars++;
					}
					else {
						break;
					}
				}
			}

			break;
		}
		return okChars;
	}

//	public void paintComponent(Graphics g) {
//		// ==============================================================================================
//		super.paintComponent(g);
//		g2 = (Graphics2D) g;
//		Aaa.commonGraphicsSettings(g2);
//
//		/**
//		 * Yes this does nothing
//		 * Unusually we require our decendent TutorialPanel to "Draw" first
//		 * so producing all the segments which we draw when it calls back with
//		 * 
//		 *   drawAllSegs(g2);  to do the actual drawing
//		 *   
//		 *   see the  ABOVE  function
//		 */
//	}

}
