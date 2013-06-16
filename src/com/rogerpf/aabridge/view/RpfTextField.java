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

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTextField;

/**   
 */
public class RpfTextField extends JTextField implements MouseListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	int widthPc = 90;
	int heightPc = 13;

	// ----------------------------------------
	public RpfTextField(int widthPc, int heightPc) { /* Constructor */
		super();

		this.addMouseListener(this);
		this.widthPc = widthPc;
		this.heightPc = heightPc;
	}

	public Dimension correctSize() {

		Dimension d = getParent().getSize();
		d.height = (d.height * heightPc) / 100;
		d.width = (d.width * widthPc) / 100;
		return d;
	}

	public Dimension getPreferredSize() {
		return correctSize();
	}

	public Dimension getMinimumSize() {
		return correctSize();
	}

	public Dimension getMaximumSize() {
		return correctSize();
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
		boolean canBeFocus = isFocusable();
		if (canBeFocus) {
			setFocusable(false);
		}
		else {
			setFocusable(true);
			requestFocus();
		}
	}

}
