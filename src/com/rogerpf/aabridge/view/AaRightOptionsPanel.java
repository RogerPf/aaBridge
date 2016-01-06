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

import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.ToolTipManager;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.rogerpf.aabridge.controller.App;

public class AaRightOptionsPanel extends JTabbedPane implements ChangeListener {

	private static final long serialVersionUID = 1L;

	public AaRopPrefs0_NewDealChoices p0_NewDealChoices;
	public AaRopPrefs1_AutoPlay p1_AutoPlay;
	public AaRopPrefs2_SeatChoice p2_SeatChoice;
	public AaRopPrefs3_DFC p3_DFC;
	public AaRopPrefs4_SuitColors p4_SuitColors;
	public AaRopPrefs5_DSizeFont p5_DSizeFont;
	public AaRopPrefs6_RedHints p6_RedHints;
	public AaRopPrefs7_ShowBtns p7_ShowBtns;

	AaRightOptionsPanel() { /* Constructor */

		addChangeListener(this);
		p0_NewDealChoices = new AaRopPrefs0_NewDealChoices();
		p1_AutoPlay = new AaRopPrefs1_AutoPlay();
		p2_SeatChoice = new AaRopPrefs2_SeatChoice();
		p3_DFC = new AaRopPrefs3_DFC();
		p4_SuitColors = new AaRopPrefs4_SuitColors();
		p5_DSizeFont = new AaRopPrefs5_DSizeFont();
		p6_RedHints = new AaRopPrefs6_RedHints();
		p7_ShowBtns = new AaRopPrefs7_ShowBtns();

		addTab("New Deals", null, p0_NewDealChoices, "What shape of hand do you want to be delt ?   ");
		addTab("AutoPlay", null, p1_AutoPlay, "Pause at end of trick  and  AutoPlay  options   ");
		addTab("Seat Choice", null, p2_SeatChoice, "Seat Choice    for   Bridge Movies   and   'Entered Deals'  ");
		addTab("DFC", null, p3_DFC, "DFC        Disrtribution Flash Card  options   ");
		addTab("Suit Colors", null, p4_SuitColors, "Suit Symbol Colors   ");
		addTab("Size & Font", null, p5_DSizeFont, "Deal Size and  Movie Font override   ");
		addTab("Red Hints", null, p6_RedHints, "Red Hints   ");
		addTab("Show", null, p7_ShowBtns, "Show,  Button Display  and  Reset all options   ");

		ToolTipManager.sharedInstance().setDismissDelay(12000);
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

	QCheckBox(ItemListener listener, boolean value, String label, String tip) {
		super(label);
		setToolTipText(tip);
		addItemListener(listener);
		setSelected(false);
		setFocusable(false);
		setSelected(value);
	}
}

/**   
 */
class QButton extends JButton {

	private static final long serialVersionUID = 1L;

	QButton(ActionListener listener, String label) {
		super(label);
		addActionListener(listener);
		setFocusable(false);
		setActionCommand(label);
		setEnabled(true);
	}
}

/**   
 */
class QRadioButton extends JRadioButton {

	private static final long serialVersionUID = 1L;

	QRadioButton(ItemListener listener, ButtonGroup rbGroup, Border border, Boolean initialValue, String actionCmd, String label) {
		super(label);
		setToolTipText(label);
		if (border != null)
			setBorder(border);
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

	QLabel(String label, String tip) {
		super(label);
		setToolTipText(tip);
	}
}
