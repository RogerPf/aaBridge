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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import com.rogerpf.aabridge.controller.App;

/**    
 */
public class ClickPanel extends JPanel implements MouseListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// ----------------------------------------
	public ClickPanel() { /* Constructor */
		this.addMouseListener(this);
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		App.gbp.c1_1__tfdp.ClearShowCompletedTrick();
		if (App.gbp.c0_0__tlp.descEntry.hasFocus()) {
			App.gbp.c0_0__tlp.descEntry.setFocusable(false);
		}
		App.gbp.hideClaimButtonsIfShowing();
	}

}
