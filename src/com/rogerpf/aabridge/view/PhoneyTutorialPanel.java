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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.Aaf;
import com.rogerpf.aabridge.controller.App;
import com.rpsd.bridgefonts.BridgeFonts;

/**    
 */
public class PhoneyTutorialPanel extends ClickPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// ----------------------------------------
	public PhoneyTutorialPanel() { /* Constructor */
		setOpaque(true);
		setBackground(Aaa.handBkColorStd);

		setPreferredSize(new Dimension(5000, 1000)); // We just try to fill the available space
	}

	public void mousePressed(MouseEvent e) {

		if (e.getButton() == MouseEvent.BUTTON3) {
			App.frame.rightClickPasteTimer.start();
			return;
		}

		App.gbp.c1_1__tfdp.clearShowCompletedTrick();
		if (App.gbp.c0_0__tlp.descEntry.hasFocus()) {
			App.gbp.c0_0__tlp.descEntry.setFocusable(false);
		}
		App.gbp.hideClaimButtonsIfShowing();

		show_wanted = !show_wanted;

		App.frame.repaint();
	}

	float width;
	float fSize;
	float boldMult;
	float lump;
	float lineHeight;
	float x;
	float y;

	boolean show_wanted = true;

	static Color contrtactNeeded_color = new Color(80, 80, 80);
	static Color poorDefense_color = new Color(130, 130, 130);

	void drawStringMove_X(Graphics2D g2, Font font, String text) {
		// =============================================================
		g2.setFont(font);
		g2.drawString(text, x, y);
		x += getFontMetrics(font).stringWidth(text);
	}

	public void paintComponent(Graphics g) {
		// =============================================================

		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		Aaa.commonGraphicsSettings(g2);

		Rectangle bounds = getBounds();

		g2.setColor(Aaa.handBkColorStd);
		g2.fill(bounds);

		width = bounds.width;
		fSize = width * 0.018f;
		boldMult = 1.3f;
		lump = width * 0.0010f;
		lineHeight = width * 0.04f;
		x = width * 0.05f;
		y = width * 0.035f;

		boolean show_contractNeeded_hint = /* show_wanted && */App.showContNeededHint && !App.cameFromPbnOrSimilar() && !App.deal.isContractReal();
		boolean show_poorDefense_hint = /* show_wanted && */App.showPoorDefHint && App.show_poor_def_msg;

		if (show_contractNeeded_hint) {

			boldMult = 1.3f;

			Font std = BridgeFonts.internationalFont.deriveFont(fSize);
			Font bold = BridgeFonts.internatBoldFont.deriveFont(fSize * boldMult);

			g2.setColor(contrtactNeeded_color);

			g2.setFont(std);
			drawStringMove_X(g2, std, Aaf.bigHint_youMay);

			y += lineHeight;
			x = width * 0.07f;
			drawStringMove_X(g2, std, Aaf.bigHint_clickThe);
			drawStringMove_X(g2, bold, "  " + Aaf.bigHint_analyse);
			drawStringMove_X(g2, std, "   " + Aaf.bigHint_buttonTop);

			y += lineHeight;
			x = width * 0.07f;
			drawStringMove_X(g2, std, Aaf.bigHint_clickAny);

			y += lineHeight * 0.9;
			x = width * 0.13f;
			drawStringMove_X(g2, std, Aaf.bigHint_orYou);

		}
		else if (show_poorDefense_hint) {

			boldMult = 1.3f;

			Font std = BridgeFonts.internationalFont.deriveFont(fSize);
			Font bold = BridgeFonts.internatBoldFont.deriveFont(fSize * boldMult);

			g2.setColor(poorDefense_color);

			g2.setFont(std);
			drawStringMove_X(g2, std, Aaf.bigHint_ifYou);

			y += lineHeight * 1.2f;
			x = width * 0.07f;
			drawStringMove_X(g2, std, Aaf.bigHint_click);
			drawStringMove_X(g2, bold, " " + Aaf.bigHint_edit);
			drawStringMove_X(g2, std, "    " + Aaf.bigHint_click);
			drawStringMove_X(g2, bold, " " + Aaf.bigHint_undo);
			drawStringMove_X(g2, std, "     " + Aaf.bigHint_playThe);

			y += lineHeight;
			x = width * 0.07f;
			drawStringMove_X(g2, std, Aaf.bigHint_click);
			drawStringMove_X(g2, bold, " " + Aaf.bigHint_play);
			drawStringMove_X(g2, std, "    " + Aaf.bigHint_andCont);

		}
		else {
			g2.setColor(Aaa.veryWeedyBlack);

			g2.setFont(BridgeFonts.internationalFont.deriveFont((float) (bounds.width / 40)));

			Aaa.drawCenteredString(g2, Aaf.bigHint_notUsed, 0, 0, bounds.width, bounds.height);
		}

	}

}
