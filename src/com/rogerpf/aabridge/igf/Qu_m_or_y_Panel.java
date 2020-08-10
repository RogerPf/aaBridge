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
public class Qu_m_or_y_Panel extends JPanel {
	// ---------------------------------- CLASS -------------------------------------

	private static final long serialVersionUID = 1L;

	QuAskPanel qaskp;
	QuAskButtsPanel qbutsp;
	QuAnswerPanel qansp;
	QuTellMePanel tmep;

	/**
	 */
	Qu_m_or_y_Panel() { /* Constructor */
		// ============================================================================
		setLayout(new MigLayout(App.simple, "[45%][10%][45%]", "[25%][30%][23%][20%]2%"));

		setOpaque(false);

		qaskp = new QuAskPanel();
		qbutsp = new QuAskButtsPanel();
		qansp = new QuAnswerPanel();
		tmep = new QuTellMePanel();

		add(qaskp, App.hm1oneHun + ", cell 0 0, spanx 3, center");
		add(qbutsp, App.hm1oneHun + ", cell 0 1, spanx 3, center");
		add(qansp, App.hm1oneHun + ", cell 0 2, spanx 3, center");
		add(tmep, App.hm1oneHun + ", cell 1 3");

	}

	public void fillButtonsWithAnswers() {
		// ============================================================================
		qbutsp.fillButtonsWithAnswers();
	}

}
