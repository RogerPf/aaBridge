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

import net.miginfocom.swing.MigLayout;

import com.rogerpf.aabridge.controller.App;

/**   
 */
public class QuAskAnswerAndTellMePanel extends JPanel {
	// ---------------------------------- CLASS -------------------------------------

	private static final long serialVersionUID = 1L;

	QuAskPanel qaskp;
	QuAnswerPanel qansp;
	QuTellMePanel tmep;

	/**
	 */
	QuAskAnswerAndTellMePanel() { /* Constructor */
		// ============================================================================
		setLayout(new MigLayout(App.simple + ", flowy", "[35%][28%][27%]", "[50%][30%][20%]"));

		setOpaque(false);

		qaskp = new QuAskPanel();

		qansp = new QuAnswerPanel();

		tmep = new QuTellMePanel();

		add(qaskp, App.hm1oneHun + ", cell 0 0, spanx 3, center");
		add(qansp, App.hm1oneHun + ", cell 0 1, spanx 3, center");
		add(tmep, App.hm1oneHun + ", cell 1 2");

	}

//	// -------------------------------------
//	public void paintComponent(Graphics g) {
//		// ============================================================================
//		super.paintComponent(g);
//		Graphics2D g2 = (Graphics2D) g;
//		Aaa.commonGraphicsSettings(g2);
//	}

}
