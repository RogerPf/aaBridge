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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import com.rogerpf.aabridge.controller.Aaa;
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

	public void paintComponent(Graphics g) {
		// =============================================================
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		Aaa.commonGraphicsSettings(g2);

		Rectangle bounds = getBounds();

		g2.setColor(Aaa.handBkColorStd);
		g2.fill(bounds);

		g2.setFont(BridgeFonts.bridgeLightFont.deriveFont((float) (bounds.width / 40)));
		g2.setColor(Aaa.veryWeedyBlack);

		String text = "not used";

		Aaa.drawCenteredString(g2, text, 0, 0, bounds.width, bounds.height);
	}

}
