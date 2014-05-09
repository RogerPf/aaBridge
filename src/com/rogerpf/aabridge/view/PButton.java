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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.SystemColor;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.text.AttributedString;

import javax.swing.JButton;
import javax.swing.border.EmptyBorder;

import com.rogerpf.aabridge.controller.Aaa;
import com.rpsd.bridgefonts.BridgeFonts;

/**   
 */
class PButton extends JButton implements MouseListener {
	// ==============================================================================================

	private static final long serialVersionUID = 1L;

	boolean asBtn = false;

	boolean hover = false;
	boolean pressed = false;

	Font font = BridgeFonts.bridgeBoldFont;
	Color bgColor = SystemColor.control;
	Color fgColor = Aaa.weedyBlack;
	Color hoverColor = Aaa.hoverColor;
	Color pressedColor = Aaa.pressedColor;

	AttributedString at;

	PButton(ActionListener listener, String label, AttributedString at) {
		super(label);
		addActionListener(listener);

		addMouseListener(this);

		this.at = at;

		setFocusable(false);
		setActionCommand(label);
		setEnabled(true);
		setOpaque(false);
		setBorder(new EmptyBorder(0, 0, 0, 0));
		setFocusable(false);
		// setMinimumSize(new Dimension(20, 15));
	}

	PButton(String label) {
		super(label);

		asBtn = true;

		setFocusable(false);

		setOpaque(false);
		setBorder(new EmptyBorder(0, 0, 0, 0));
		setFocusable(false);
		// setMinimumSize(new Dimension(20, 15));
	}

	/**
	 */
	public void paintComponent(Graphics g) {
		// ============================================================================
		// super.paintComponent(g); not a good idea we get a std button

		Graphics2D g2 = (Graphics2D) g.create();
		Aaa.commonGraphicsSettings(g2);

		if (pressed)
			g2.setColor(pressedColor);
		else if (hover)
			g2.setColor(hoverColor);
		else
			g2.setColor(getBackground());

		g2.fillRect(0, 0, getWidth(), getHeight());

		Color fgc = fgColor;

		fgc = Aaa.selectedButFontCol;
		if (hover) {
			fgc = Aaa.hoverButFontCol;
		}

		g2.setColor(fgc);

		int height = getHeight();
		String text = getText();

		if (at != null) {
			Aaa.drawCenteredString(g2, text, 0, 0, 14, height);

			FontRenderContext frc = g2.getFontRenderContext();
			TextLayout tl = new TextLayout(at.getIterator(), frc);
			tl.draw(g2, 18, height * 4 / 5);
		}
		else {
			Aaa.drawCenteredString(g2, text, 0, 0, getWidth(), height);
		}

	}

	public void mousePressed(MouseEvent e) {
		pressed = true;
		repaint(); // not App.frame.repaint()
	}

	public void mouseReleased(MouseEvent e) {
		pressed = false;
		repaint(); // not App.frame.repaint()
	}

	public void mouseEntered(MouseEvent e) {
		hover = true;
		repaint(); // not App.frame.repaint()
	}

	public void mouseExited(MouseEvent e) {
		hover = false;
		repaint(); // not App.frame.repaint()
	}

	public void mouseClicked(MouseEvent e) {
	}

}
