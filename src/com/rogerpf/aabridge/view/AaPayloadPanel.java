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
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Rectangle2D;

import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.model.Cc;

/**   
 */
class AaPayloadPanel extends ClickPanel implements ComponentListener {
	// ---------------------------------- CLASS -------------------------------------

	private static final long serialVersionUID = 1L;

	AaPayloadPanel() {
		// -------------------------------------
		// setOpaque(true);
		// setBackground(Aaa.baizeMustard);
		// setBackground(Aaa.darkGrayBg);

		addComponentListener(this);
	}

	public void componentResized(ComponentEvent e) {
		App.horzDividerLocation = App.frame.splitPaneHorz.getDividerLocation();
		App.vertDividerLocation = App.frame.splitPaneVert.getDividerLocation();

		App.aaBookPanel.playloadPanel_resized_check();
	}

	public void componentMoved(ComponentEvent e) {
	}

	public void componentShown(ComponentEvent e) {
	}

	public void componentHidden(ComponentEvent e) {
	}
}

/**    
 */
class AaPayloadCasePanel extends ClickPanel implements ComponentListener {
	// ---------------------------------- CLASS -------------------------------------

	private static final long serialVersionUID = 1L;

	AaPayloadCasePanel() {
		// -------------------------------------
		setOpaque(false);

		addComponentListener(this);
	}

	public void componentResized(ComponentEvent e) {
//		App.frame.payloadPanelShaker();
	}

	public void componentMoved(ComponentEvent e) {
	}

	public void componentShown(ComponentEvent e) {
	}

	public void componentHidden(ComponentEvent e) {
	}
}

/**    
 */
class AaDdlAndPayloadCasePanel extends ClickPanel implements ComponentListener {
	// ---------------------------------- CLASS -------------------------------------

	private static final long serialVersionUID = 1L;

	AaDdlAndPayloadCasePanel() {
		// -------------------------------------
		setOpaque(false);
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
//		super.paintComponent(g); we do it all below
		Graphics2D g2 = (Graphics2D) g;

		/**
		 *  This way we control the appearance what ever the system
		 */
		Rectangle2D.Float rect = new Rectangle2D.Float(0, 0, getWidth(), getHeight());
		g2.setColor(Cc.g(Cc.darkGrayBg));
//		g2.setColor(Cc.g(Cc.blueWeak));
		g2.fill(rect);
	}

}
