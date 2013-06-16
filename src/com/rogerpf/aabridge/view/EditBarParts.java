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

import javax.swing.JButton;

import net.miginfocom.swing.MigLayout;

import com.rogerpf.aabridge.controller.Aaa;

/**   
 */
class EditBarChoosePart0 extends ClickPanel {

	private static final long serialVersionUID = 1L;

	EditBarChoosePart0() { /* Constructor */
		setLayout(new MigLayout("insets 1 1 1 1, gap 0! 0!", "push[c]push", "push[c]push"));

		JButton b;
		b = new RpfResizeButton(1, "editHands", -7, 70);
		b.setForeground(Aaa.heartsColor);
		add(b);

		setVisible(false);
	}

	// ----------------------------------------
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		setBackground(Aaa.baizePink);
	}
}

/**   
 */
class EditBarChoosePart1 extends ClickPanel {

	private static final long serialVersionUID = 1L;

	EditBarChoosePart1() { /* Constructor */
		setLayout(new MigLayout("insets 0 0 0 0, gap 0! 0!", "push[c]push", "push[c]push"));

		JButton b;
		b = new RpfResizeButton(1, "editBidding", -7, 70);
		b.setForeground(Aaa.heartsColor);
		add(b);

		setVisible(false);
	}

	// ----------------------------------------
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		setBackground(Aaa.baizePink);
	}
}

/**   
 */
class EditBiddingPart1 extends ClickPanel {

	private static final long serialVersionUID = 1L;

	EditBiddingPart1() { /* Constructor */
		setLayout(new MigLayout("insets 0 0 0 0, gap 0! 0!", "push[c]push", "push[c]push"));

		JButton b;
		b = new RpfResizeButton(1, "editBiddingXall", -3, 70);
		b.setForeground(Aaa.heartsColor);
		add(b);

		setVisible(false);
	}

	// ----------------------------------------
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		setBackground(Aaa.baizePink);
	}
}

/**   
 */
class EditBarChoosePart2 extends ClickPanel {

	private static final long serialVersionUID = 1L;

	EditBarChoosePart2() { /* Constructor */
		setLayout(new MigLayout("insets 0 0 0 0, gap 0! 0!", "push[c]push", "push[c]push"));

		JButton b;
		b = new RpfResizeButton(1, "editPlay", -7, 70);
		b.setForeground(Aaa.heartsColor);
		add(b);

		setVisible(false);
	}

	// ----------------------------------------
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		setBackground(Aaa.baizePink);
	}
}

/**   
 */
class EditPlayPart2 extends ClickPanel {

	private static final long serialVersionUID = 1L;

	EditPlayPart2() { /* Constructor */
		setLayout(new MigLayout("insets 0 0 0 0, gap 0! 0!", "push[c]push", "push[c]push"));

		JButton b;
		b = new RpfResizeButton(1, "editPlayXall", -2, 70);
		b.setForeground(Aaa.heartsColor);
		add(b);

		setVisible(false);
	}

	// ----------------------------------------
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		setBackground(Aaa.baizePink);
	}
}
