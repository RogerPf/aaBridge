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

import javax.swing.JPanel;

import com.rogerpf.aabridge.controller.App;

import net.miginfocom.swing.MigLayout;

/**   
 */
public class QuAskAndTellMePanel extends JPanel {
	// ---------------------------------- CLASS -------------------------------------

	private static final long serialVersionUID = 1L;

	QuAskPanel qaskp;

	QuTellMePanel tmep;

	/**
	 */
	QuAskAndTellMePanel() { /* Constructor */
		// ============================================================================
		setLayout(new MigLayout(App.simple, "[42%][28%][30%]", "[80%]push[20%]"));

		setOpaque(false);

		qaskp = new QuAskPanel();

		tmep = new QuTellMePanel();

		add(qaskp, App.hm3oneHun + ", spanx 3");
		add(tmep, App.hm3oneHun + ", cell 1 1");

	}

//	// -------------------------------------
//	public void paintComponent(Graphics g) {
//		// ============================================================================
//		super.paintComponent(g);
//		Graphics2D g2 = (Graphics2D) g;
//		Aaa.commonGraphicsSettings(g2);
//	}

}
