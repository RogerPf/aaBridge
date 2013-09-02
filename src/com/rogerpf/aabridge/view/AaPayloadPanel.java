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
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;

/**   
 */
class AaPayloadPanel extends ClickPanel implements ComponentListener {
	// ---------------------------------- CLASS -------------------------------------

	private static final long serialVersionUID = 1L;

	AaPayloadPanel() {
		// -------------------------------------
		addComponentListener(this);
	}

	public void componentResized(ComponentEvent e) {
		App.horzDividerLocation = App.frame.splitPaneHorz.getDividerLocation();
		App.vertDividerLocation = App.frame.splitPaneVert.getDividerLocation();
	}

	public void componentMoved(ComponentEvent e) {
	}

	public void componentShown(ComponentEvent e) {
	}

	public void componentHidden(ComponentEvent e) {
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		setBackground(Aaa.darkGrayBg);
	}
}

/**    
 */
class AaPayloadCasePanel extends ClickPanel implements ComponentListener {
	// ---------------------------------- CLASS -------------------------------------

	private static final long serialVersionUID = 1L;

	AaPayloadCasePanel() {
		// -------------------------------------
		addComponentListener(this);
	}

	public void componentResized(ComponentEvent e) {
		App.frame.payloadPanelHasResized();
	}

	public void componentMoved(ComponentEvent e) {
	}

	public void componentShown(ComponentEvent e) {
	}

	public void componentHidden(ComponentEvent e) {
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		setBackground(Aaa.darkGrayBg);
	}
}

/**    
 */
class AaLinAndPayloadCasePanel extends ClickPanel implements ComponentListener {
	// ---------------------------------- CLASS -------------------------------------

	private static final long serialVersionUID = 1L;

	AaLinAndPayloadCasePanel() {
		// -------------------------------------
		addComponentListener(this);
	}

	public void componentResized(ComponentEvent e) {
	}

	public void componentMoved(ComponentEvent e) {
	}

	public void componentShown(ComponentEvent e) {
	}

	public void componentHidden(ComponentEvent e) {
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		// never never have two calls to setBackground
		setBackground(Aaa.darkGrayBg);
	}
}
