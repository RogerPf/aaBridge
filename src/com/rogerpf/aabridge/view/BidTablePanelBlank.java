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

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.miginfocom.swing.MigLayout;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;
import com.rpsd.bridgefonts.BridgeFonts;

/**   
 */
public class BidTablePanelBlank extends ClickPanel implements ActionListener {
	private static final long serialVersionUID = 1L;

	public RpfResizeButton showBiddingBtn;

	/**
	 */
	BidTablePanelBlank() { /* Constructor */
		setOpaque(false);
		// setBackground(Aaa.baizeGreen);

		setLayout(new MigLayout(App.simple, "push []6%", "7%[]"));

		Font stdTextFont = BridgeFonts.bridgeLightFont.deriveFont(14f);

		RpfResizeButton b = new RpfResizeButton(Aaa.s_SelfCmd, "Show Bidding", 50, 15, 0.8f);
		b.addActionListener(this);
		b.setFont(stdTextFont);
		add(b);

		showBiddingBtn = b;

		setVisible(false);
	}

	/**
	 */
	public void actionPerformed(ActionEvent e) {
		// String a = e.getActionCommand();

		App.gbp.biddingDisplayToggle();
	}
}
