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

import javax.swing.OverlayLayout;

import com.rogerpf.aabridge.controller.App;

/**
 */
public class GreenBaizeMerged extends ClickPanel {
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	public GreenBaizeMerged() { /* Constructor */

		// setPreferredSize(new Dimension(5000, 5000));

		setLayout(new OverlayLayout(this));
		App.gbp.setAlignmentX(0.0f);
		App.gbp.setAlignmentY(0.0f);
		add(App.gbo);
		add(App.gbp);
	}

}
