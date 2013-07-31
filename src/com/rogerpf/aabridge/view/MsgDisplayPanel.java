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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;

import net.miginfocom.swing.MigLayout;

import com.rogerpf.aabridge.controller.Aaa;
import com.rpsd.bridgefonts.BridgeFonts;

/**
 */
public class MsgDisplayPanel extends ClickPanel implements ComponentListener {

	private static final long serialVersionUID = 1L;

	JTextArea textArea = new JTextArea();

	/**
	 */
	MsgDisplayPanel() {

		addComponentListener(this);

		setLayout(new MigLayout("insets 0 0 0 0, gap 0! 0!", "", ""));
		add(textArea, "width 100%, height 100%, wmin 70%");
		textArea.setLineWrap(true);
		textArea.setFocusable(false);
		textArea.setForeground(Color.BLACK);
		textArea.setBackground(Aaa.genOffWhite);

		setVisible(false);
	}

	public void actionPerformed(ActionEvent e) {
	}

	public void componentHidden(ComponentEvent e) {
	}

	public void componentMoved(ComponentEvent e) {
	}

	public void componentShown(ComponentEvent e) {
	}

	public void componentResized(ComponentEvent e) {
		int panelWidth = getWidth();
		int bt = (int) (panelWidth * 0.02f + 0.5f); // yes integer !!!
		int mt = (int) (panelWidth * 0.03f + 0.5f); // yes integer !!!

		textArea.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Aaa.weedyBlack, bt), BorderFactory.createEmptyBorder(0, mt, 0, mt)));

		textArea.setFont(BridgeFonts.bridgeLightFont.deriveFont(panelWidth * 0.064f));

	}

	/**
	 */
	public void paintComponent(Graphics g) { // WeTheyScorePanel

		super.paintComponent(g);
		setBackground(Aaa.baizeGreen);

		Graphics2D g2 = (Graphics2D) g;
		Aaa.commonGraphicsSettings(g2);

	}

}
