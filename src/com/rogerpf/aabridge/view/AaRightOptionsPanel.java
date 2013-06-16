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

import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.rogerpf.aabridge.controller.App;

public class AaRightOptionsPanel extends JTabbedPane implements ChangeListener {

	private static final long serialVersionUID = 1L;

	public AaropPreferences ropPreferences;
	public AaRopDealChoices ropDealChoices;

	AaRightOptionsPanel() { /* Constructor */

		addChangeListener(this);
		ropPreferences = new AaropPreferences();
		ropDealChoices = new AaRopDealChoices();
		addTab("Deal Choices", ropDealChoices);
		addTab("Preferences", ropPreferences);

	}

	public void stateChanged(ChangeEvent e) {
		App.ropSelectedTabIndex = getSelectedIndex();
	}
}

/**   
 */
class QCheckBox extends JCheckBox {

	private static final long serialVersionUID = 1L;

	QCheckBox(ItemListener listener, boolean value, String label) {
		super(label);
		setToolTipText(label);
		addItemListener(listener);
		setSelected(false);
		setFocusable(false);
		setSelected(value);
	}
}

/**   
 */
class QRadioButton extends JRadioButton {

	private static final long serialVersionUID = 1L;

	QRadioButton(ItemListener listener, ButtonGroup rbGroup, String actionCmd, String label) {
		super(label);
		setToolTipText(label);
		addItemListener(listener);
		setSelected(App.dealCriteria.contentEquals(actionCmd));
		setFocusable(false);
		rbGroup.add(this);
	}

	QRadioButton(ItemListener listener, ButtonGroup rbGroup, Boolean initialValue, String actionCmd, String label) {
		super(label);
		setToolTipText(label);
		addItemListener(listener);
		setSelected(initialValue);
		setFocusable(false);
		rbGroup.add(this);
	}
}

/**   
 */
class QLabel extends JLabel {

	private static final long serialVersionUID = 1L;

	QLabel(String label) {
		super(label);
		setToolTipText(label);
	}
}
