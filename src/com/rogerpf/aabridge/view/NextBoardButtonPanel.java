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

import java.awt.Graphics;

import net.miginfocom.swing.MigLayout;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;

/**   
 */
public class NextBoardButtonPanel extends ClickPanel {
	private static final long serialVersionUID = 1L;

	public RpfResizeButton reviewButton;

	/**
	 */
	NextBoardButtonPanel() { /* Constructor */
		setLayout(new MigLayout("insets 0 0 0 0, gap 0! 0!", "[]push[]5%", "[]10%"));

//		Font stdTextFont = BridgeFonts.bridgeBoldFont.deriveFont(14f);

		RpfResizeButton b;
		b = new RpfResizeButton(1, "mainNextBoard", 48, 85, 0.75f);
		add(b, "wmin 10%");

		b = new RpfResizeButton(1, "mainReview", 36, 85);
		add(b);

		reviewButton = b;

		setVisible(true);
	}

	/**
	 */
	public void setReviewButtonText() {
		reviewButton.setText((!App.isMode(Aaa.NORMAL)) ? "Normal" : "Review");
	}

	/**
	 */
	public void paintComponent(Graphics g) {
		// super.paintComponent(g);
		// setBackground(new Color(0, 80, 0)); // some green
	}

}
