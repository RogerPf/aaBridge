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

import com.rpsd.bridgefonts.BridgeFonts;

/**   
 */
public class UndoButtonPanel extends ClickPanel {
	private static final long serialVersionUID = 1L;

	/**
	 */
	UndoButtonPanel() { /* Constructor */
		setLayout(new MigLayout("insets 0 0 0 0, gap 0! 0!", "28%[]", "20%[]"));

		RpfResizeButton b = new RpfResizeButton(1, "mainUndo", -4, 20);
		b.setFont(BridgeFonts.bridgeBoldFont.deriveFont(16f));
		add(b, "");

		setVisible(false);
	}

	/**
	 */
	public void paintComponent(Graphics g) {
		// super.paintComponent(g);
		// setBackground(new Color(0, 80, 0)); // some green
	}
}
