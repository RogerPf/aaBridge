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

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.model.Lin;

/**
 */
public class ButtonPanelRight extends JPanel {
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	RpfResizeButton hiddenHandsShowHide_b;
	RpfResizeButton hiddenHandsClick1_b;
	RpfResizeButton hiddenHandsClick2_b;

	/**
	 */
	ButtonPanelRight() { /* Constructor */
		// ==============================================================================================
		setOpaque(false);
		// setBackground(Aaa.baizePink);
		// setBackground(Aaa.baizeGreen);

		// setPreferredSize(new Dimension(200, 1000)); // We just try to fill the available space

		// @formatter:off
		setLayout(new MigLayout(App.simple + ", flowy, align right", "[]", "33%[][]3%[]"));
		// @formatter:on

		add(hiddenHandsClick1_b = new RpfResizeButton(Aaa.s_Label, "hiddenHandsClick1", 70, 4));
		add(hiddenHandsClick2_b = new RpfResizeButton(Aaa.s_Label, "hiddenHandsClick2", 70, 4));
		add(hiddenHandsShowHide_b = new RpfResizeButton(Aaa.s_Std, "hiddenHandsShowHide", 70, 4));
	}

	/**   
	 */
	public void calcApplyBarVisiblity() {
		// =============================================================

		// @formatter:off
		boolean handsShowing = App.localShowHidden;
		
		String text = handsShowing ? "Hide" : "Show";
		
		// @formatter:off
		boolean visible =    (App.mg.lin.linType != Lin.FullMovie) || (App.visualMode == App.Vm_InsideADeal);
		// @formatter:on

		hiddenHandsShowHide_b.setText(text);
		hiddenHandsShowHide_b.setVisible(visible);

		hiddenHandsClick1_b.setVisible(visible & !handsShowing);
		hiddenHandsClick2_b.setVisible(visible & !handsShowing);
		// @formatter:on
	}

}
