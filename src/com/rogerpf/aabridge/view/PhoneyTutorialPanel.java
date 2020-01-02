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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.Timer;

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

	/**
	*/
	public Timer showContNeededTimer = new Timer(200, new ActionListener() {
		// =============================================================================
		public void actionPerformed(ActionEvent evt) {
			if (App.showContFlashCount <= 0) {
				showContNeededTimer.stop();
				return;
			}
			App.showContFlashCount--;
			App.gbp.matchPanelsToDealState();
			App.frame.invalidate();
			App.frame.repaint();
		}
	});

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
	float lineHeight2;
	float x;
	float y;

	boolean show_wanted = true;

	static Color studyDeal_color = new Color(130, 130, 130);
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
		fSize = width * 0.018f * 100 / App.ratioFiddle;
		boldMult = 1.0f;
		lump = width * 0.0010f;
		lineHeight = width * 0.04f * 100 / App.ratioFiddle;
		lineHeight2 = width * 0.028f * 100 / App.ratioFiddle;
		x = width * 0.05f;
		y = width * 0.035f * 100 / App.ratioFiddle;
		;

		boolean normal_active = App.isMode(Aaa.NORMAL_ACTIVE);

		boolean show_contractNeeded_hint = App.showContNeededHint && !App.cameFromPbnOrSimilar() && !App.deal.isContractReal();
		boolean show_poorDefense_hint = App.showPoorDefHint && App.show_poor_def_msg && normal_active;

		// System.out.println(show_contractNeeded_hint + "   " + App.showContFlashCount);

		if ((App.showContFlashCount % 2 == 1)) {
			return;
		}

		if (App.isStudyDeal() && normal_active) {
			boldMult = 1.35f;

			Font std = BridgeFonts.internationalFont.deriveFont(fSize);
			Font bold = BridgeFonts.internatBoldFont.deriveFont(fSize * boldMult);

			g2.setColor(studyDeal_color);

			y += lineHeight * 0.5f;

			x = width * 0.07f;
			drawStringMove_X(g2, std, Aaf.bigHint_sd_a);

			x += width * 0.02f;
			drawStringMove_X(g2, bold, Aaf.bigHint_sd_sd);

			y += lineHeight * 1.2f;

			x = width * 0.07f;
			drawStringMove_X(g2, std, Aaf.bigHint_sd_b);

			y += lineHeight * 0.6f;
			x = width * 0.60f;
			drawStringMove_X(g2, std, Aaf.bigHint_sd_c);

		}
		else if (show_contractNeeded_hint || (App.showContFlashCount > 0) || App.deal.isContractReal() == false) {

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
			drawStringMove_X(g2, bold, "  " + Aaf.bigHint_UB);
			drawStringMove_X(g2, std, "        " + Aaf.bigHint_playThe);

			y += lineHeight;
			x = width * 0.07f;
			drawStringMove_X(g2, std, Aaf.bigHint_click);
			drawStringMove_X(g2, bold, "  " + Aaf.bigHint_play);
			drawStringMove_X(g2, std, "     " + Aaf.bigHint_andCont);

		}
		else {
			g2.setColor(Aaa.veryWeedyBlack);

			g2.setFont(BridgeFonts.internationalFont.deriveFont((float) (bounds.width / 40)));

			Aaa.drawCenteredString(g2, Aaf.bigHint_notUsed, 0, 0, bounds.width, bounds.height);
		}

	}

}
