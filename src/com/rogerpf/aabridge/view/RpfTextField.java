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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.rogerpf.aabridge.controller.App;

/**   
 */
public class RpfTextField extends JTextField implements MouseListener {

	private static final long serialVersionUID = 1L;

	int heightPcOfParent;
	float widthMultOfHeight;

	// ----------------------------------------
	public RpfTextField(int heightPcOfParent, float widthMultOfHeight) { /* Constructor */
		super();

		this.addMouseListener(this);

		this.heightPcOfParent = heightPcOfParent;
		this.widthMultOfHeight = widthMultOfHeight;

	}

	// ----------------------------------------
	static public RpfTextField createRpfTextField(int heightPcOfParent, float widthMultOfHeight) {
		RpfTextField tf = new RpfTextField(heightPcOfParent, widthMultOfHeight);
		tf.getDocument().addDocumentListener(new RpfTextFieldListener(tf));
		return tf;
	}

	// ----------------------------------------
	public Dimension correctSize() {
		int heightParent = getParent().getHeight();
		if (heightParent == 0)
			return new Dimension(90, 13); // who knows !

		Dimension rtn = new Dimension();

		rtn.height = (heightParent * heightPcOfParent) / 100;
		rtn.width = (int) ((heightParent * heightPcOfParent * widthMultOfHeight) / 100);
		return rtn;
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

class RpfTextFieldListener implements DocumentListener {
	// ===================================================================================
	private JTextField textField;

	public RpfTextFieldListener(JTextField textField) {
		this.textField = textField;
	}

	private void updateDealValue() {
		if (App.deal != null)
			App.deal.ahHeader = textField.getText();
	}

	public void insertUpdate(DocumentEvent e) {
		updateDealValue();
	}

	public void removeUpdate(DocumentEvent e) {
		updateDealValue();
	}

	public void changedUpdate(DocumentEvent e) {
		updateDealValue();
	}

}
