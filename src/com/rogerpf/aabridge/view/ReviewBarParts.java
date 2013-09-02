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
class ReviewBarPlayPart0 extends ClickPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	ReviewBarPlayPart0() { /* Constructor */
		setLayout(new MigLayout("insets 0 0 0 0, gap 1 1 1 0!", "3%[]15%[]3%[]", "15%[]"));
		// setLayout(new FlowLayout(FlowLayout.LEADING, 1, 3));

		JButton b;

		b = new RpfResizeButton(1, "reviewBidding", -4, 70);
		add(b, "hidemode 0");

		b = new RpfResizeButton(1, "reviewBackToStartOfPlay", -2, 70);
		add(b);

		b = new RpfResizeButton(1, "reviewFwdToEndOfPlay", 15, 70);
		add(b);

		setVisible(false);
	}

	// ----------------------------------------
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		setBackground(Aaa.baizeGreen);
	}
}

class ReviewBarBiddingPart0 extends ClickPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	ReviewBarBiddingPart0() { /* Constructor */
		setLayout(new MigLayout("insets 0 0 0 0, gap 1 1 1 0!", "3%[]15%[]3%[]", "15%[]"));
		// setLayout(new FlowLayout(FlowLayout.LEADING, 1, 3));

		JButton b;

		b = new RpfResizeButton(1, "reviewPlay", -7, 70);
		add(b);

		b = new RpfResizeButton(1, "reviewBackToStartOfBidding", -2, 70);
		add(b);
//
//		b = new RpfResizeButton(1, "reviewFwdToEndOfPlay", 15, 70);
//		add(b);

		setVisible(false);
	}

	// ----------------------------------------
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		setBackground(Aaa.baizeGreen);
	}
}

/**   
 */
class ReviewBarPlayPart1 extends ClickPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	ReviewBarPlayPart1() { /* Constructor */
		setLayout(new MigLayout("insets 0 0 0 0, gap 1 1 1 0!", "[]2%[]push[center]push[]2%[]", "15%[]"));

		JButton b;

		b = new RpfResizeButton(1, "reviewBackOneTrick", 11, 70);
		add(b);

		b = new RpfResizeButton(1, "reviewFwdOneTrick", 11, 70);
		add(b);

		b = new RpfResizeButton(1, "reviewFwdShowOneTrick", 40, 70, 1.0f);
		add(b);

		b = new RpfResizeButton(1, "reviewBackOneCard", 10, 70);
		add(b);

		b = new RpfResizeButton(1, "reviewFwdOneCard", 10, 70);
		add(b);

		setVisible(false);
	}

	// ----------------------------------------
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		setBackground(Aaa.baizeGreen);
	}
}

/**   
 */
class ReviewBarBiddingPart1 extends ClickPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	ReviewBarBiddingPart1() { /* Constructor */
		setLayout(new MigLayout("insets 0 0 0 0, gap 1 1 1 0!", "push[center]push[]3%[]", "15%[]"));

		JButton b;

//		b = new RpfResizeButton(1, "reviewBackOneTrick", 12, 70);
//		add(b);
//
//		b = new RpfResizeButton(1, "reviewFwdOneTrick", 12, 70);
//		add(b);
//
		b = new RpfResizeButton(1, "reviewFwdShowBidding", -7, 70, 0.9f);
		add(b);

		b = new RpfResizeButton(1, "reviewBackOneBid", 10, 70);
		add(b);

		b = new RpfResizeButton(1, "reviewFwdOneBid", 10, 70);
		add(b);

		setVisible(false);
	}

	// ----------------------------------------
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		setBackground(Aaa.baizeGreen);
	}
}

/**   
 */
class ReviewBarCmnPart2 extends ClickPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	ReviewBarCmnPart2() { /* Constructor */
		setLayout(new MigLayout("insets 0 0 0 0, gap 1 1 1 0!", "10%[]push[]3%", "15%[]"));
		// setLayout(new FlowLayout(FlowLayout.LEADING, 1, 3));

		JButton b;

		b = new RpfResizeButton(1, "reviewShowEW", 46, 73, 0.70f);
		add(b);

		b = new RpfResizeButton(1, "reviewEdit", 30, 73);
		b.setForeground(Aaa.heartsColor);
		add(b);

		setVisible(false);
	}

	// ----------------------------------------
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		setBackground(Aaa.baizeGreen);
	}
}
