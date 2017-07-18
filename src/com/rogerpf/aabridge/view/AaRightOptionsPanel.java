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

import com.rogerpf.aabridge.controller.Aaf;
import com.rogerpf.aabridge.controller.App;

public class AaRightOptionsPanel extends JTabbedPane implements ChangeListener {

	private static final long serialVersionUID = 1L;

	public AaRopPrefs0_NewDealChoices p0_NewDealChoices;
	public AaRopPrefs1_AutoPlay p1_AutoPlay;
	public AaRopPrefs2_SeatChoice p2_SeatChoice;
	public AaRopPrefs3_DFC p3_DFC;
	public AaRopPrefs4_SuitColors p4_SuitColors;
	public AaRopPrefs5_DSizeMisc p5_DSizeMisc;
	public AaRopPrefs6_RedHints p6_RedHints;
	public AaRopPrefs7_ShowBtns p7_ShowBtns;

	AaRightOptionsPanel() { /* Constructor */

		addChangeListener(this);

		p0_NewDealChoices = new AaRopPrefs0_NewDealChoices();
		p1_AutoPlay = new AaRopPrefs1_AutoPlay();
		p2_SeatChoice = new AaRopPrefs2_SeatChoice();
		p3_DFC = new AaRopPrefs3_DFC();
		p4_SuitColors = new AaRopPrefs4_SuitColors();
		p5_DSizeMisc = new AaRopPrefs5_DSizeMisc();
		p6_RedHints = new AaRopPrefs6_RedHints();
		p7_ShowBtns = new AaRopPrefs7_ShowBtns();

		addTab(Aaf.gT("menuOpt.newDeals"), null, p0_NewDealChoices, Aaf.gT("menuOpt.newDeals_TT"));
		addTab(Aaf.gT("menuOpt.autoPlay"), null, p1_AutoPlay, Aaf.gT("menuOpt.autoPlay_TT"));
		addTab(Aaf.gT("menuOpt.seat"), null, p2_SeatChoice, Aaf.gT("menuOpt.seat_TT"));
		addTab(Aaf.gT("menuOpt.dfc"), null, p3_DFC, Aaf.gT("menuOpt.dfc_TT"));
		addTab(Aaf.gT("menuOpt.colors"), null, p4_SuitColors, Aaf.gT("menuOpt.colors_TT"));
		addTab(Aaf.gT("menuOpt.size"), null, p5_DSizeMisc, Aaf.gT("menuOpt.size_TT"));
		addTab(Aaf.gT("menuOpt.red"), null, p6_RedHints, Aaf.gT("menuOpt.red_TT"));
		addTab(Aaf.gT("menuOpt.show"), null, p7_ShowBtns, Aaf.gT("menuOpt.show_TT"));

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
