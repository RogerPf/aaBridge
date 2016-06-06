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
import java.awt.font.TextAttribute;
import java.util.Map;

import com.rogerpf.aabridge.controller.Aaa;
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
		App.gbp.c1_1__tfdp.clearShowCompletedTrick();
		if (App.gbp.c0_0__tlp.descEntry.hasFocus()) {
			App.gbp.c0_0__tlp.descEntry.setFocusable(false);
		}
		App.gbp.hideClaimButtonsIfShowing();

		show_wanted = !show_wanted;

		App.frame.repaint();
	}

	boolean show_wanted = true;

	static Color textGray = new Color(145, 145, 145);

	public void paintComponent(Graphics g) {
		// =============================================================
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		Aaa.commonGraphicsSettings(g2);

		Rectangle bounds = getBounds();

		g2.setColor(Aaa.handBkColorStd);
		g2.fill(bounds);

		if (!(App.show_poor_def_msg && show_wanted && App.showPoorDefHint)) {

			g2.setFont(BridgeFonts.bridgeLightFont.deriveFont((float) (bounds.width / 40)));
			g2.setColor(Aaa.veryWeedyBlack);

			String text = "not used";

			Aaa.drawCenteredString(g2, text, 0, 0, bounds.width, bounds.height);
		}
		else {

			g2.setColor(textGray);

			float width = bounds.width;
			float fSize = width * 0.018f;
			float lump = width * 0.02f;
			float lineHeight = width * 0.04f;
			float x = width * 0.05f;
			float y = width * 0.035f;

			Font std = BridgeFonts.bridgeTextStdFont.deriveFont(fSize);

			@SuppressWarnings("unchecked")
			Map<TextAttribute, Object> attributes = (Map<TextAttribute, Object>) BridgeFonts.bridgeTextStdFont.getAttributes();
			attributes.put(TextAttribute.SIZE, fSize * 1.15);
			attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);

			Font bold = BridgeFonts.bridgeTextStdFont.deriveFont(attributes);

			g2.setFont(std);
			String text = "If you get a POOR defense from the aaBridge bots.  This is what you do -";
			g2.drawString(text, x, y);

			y += lineHeight * 1.2f;

			x = width * 0.07f;
			text = "Click ";
			g2.drawString(text, x, y);
			x += lump * 2.5f;

			text = " Edit ";
			g2.setFont(bold);
			g2.drawString(text, x, y);
			x += lump * 5.5f;

			text = "Click ";
			g2.setFont(std);
			g2.drawString(text, x, y);
			x += lump * 2.5f;

			text = " Undo ";
			g2.setFont(bold);
			g2.drawString(text, x, y);
			x += lump * 7.5f;

			text = "Play the card for the defense that they should have played.";
			g2.setFont(std);
			g2.drawString(text, x, y);

			x = width * 0.07f;
			y += lineHeight;

			text = "Click ";
			g2.drawString(text, x, y);
			x += lump * 2.5f;

			text = " Play ";
			g2.setFont(bold);
			g2.drawString(text, x, y);
			x += lump * 4.0f;

			g2.setFont(std);
			text = "and continue to play out the hand.";
			g2.drawString(text, x, y);

		}
	}

}
