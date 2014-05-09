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

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.view.RpfResizeButton;

/**   
 */
public class QuTellMePanel extends JPanel {
	// ---------------------------------- CLASS -------------------------------------

	private static final long serialVersionUID = 1L;

	/**
	 */
	QuTellMePanel() { /* Constructor */
		// ============================================================================
		setLayout(new MigLayout(App.simple, "[center]", "push[center]"));

		setOpaque(false);

		// Font stdTextFont = BridgeFonts.bridgeLightFont.deriveFont(14f);

		RpfResizeButton b = new RpfResizeButton(Aaa.s_Std, "questionTellMe", 20, 20, 0.7f);
		b.addActionListener(App.con);
		add(b, App.hm1oneHun);
	}

}
