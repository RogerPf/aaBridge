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

import com.rogerpf.aabridge.controller.Aaa;

/**   
 */
class EmptyPanel extends ClickPanel {

	private static final long serialVersionUID = 1L;

	// -------------------------------------
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		setBackground(Aaa.baizeGreen);
		Graphics2D g2 = (Graphics2D) g;
		Aaa.commonGraphicsSettings(g2);
	}
}

/**   
 */
class NoFillPanel extends ClickPanel {

	private static final long serialVersionUID = 1L;

	// -------------------------------------
	public void paintComponent(Graphics g) {
//		super.paintComponent(g);
//		setBackground(Aaa.baizeGreen);
		Graphics2D g2 = (Graphics2D) g;
		Aaa.commonGraphicsSettings(g2);
	}
}

/**   
 */
class DarkGrayHiddenPanel extends ClickPanel {

	private static final long serialVersionUID = 1L;

	// -------------------------------------
	DarkGrayHiddenPanel() { // constructor
		setVisible(false);
	}

	// -------------------------------------
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		setBackground(Aaa.darkGrayBg);
	}
}

/**   
 */
class DarkGrayBgPanel extends ClickPanel {

	private static final long serialVersionUID = 1L;

	// -------------------------------------
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		setBackground(Aaa.darkGrayBg);
		Graphics2D g2 = (Graphics2D) g;
		Aaa.commonGraphicsSettings(g2);
	}
}

/**   
 */
class PinkBgPanel extends ClickPanel {

	private static final long serialVersionUID = 1L;

	// -------------------------------------
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		setBackground(Aaa.baizePink);
	}
}
