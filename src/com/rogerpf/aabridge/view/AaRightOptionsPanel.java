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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.Aaf;
import com.rogerpf.aabridge.controller.App;

public class AaRightOptionsPanel extends JTabbedPane implements ChangeListener {

	private static final long serialVersionUID = 1L;

	public AaRopPrefs0_NewDealChoices p0_NewDealChoices;
	public AaRopPrefs1_AutoPlay p1_AutoPlay;
	public AaRopPrefs2_KibSeat p2_KibSeat;
	public AaRopPrefs3_DFC p3_DFC;
	public AaRopPrefs4_SuitColors p4_SuitColors;
	public AaRopPrefs5_DSizeMisc p5_DSizeMisc;
	public AaRopPrefs6_RedHints p6_RedHints;
	public AaRopPrefs7_ShowBtns p7_ShowBtns;

	AaRightOptionsPanel() { /* Constructor */

		addChangeListener(this);

		p0_NewDealChoices = new AaRopPrefs0_NewDealChoices();
		p1_AutoPlay = new AaRopPrefs1_AutoPlay();
		p2_KibSeat = new AaRopPrefs2_KibSeat();
		p3_DFC = new AaRopPrefs3_DFC();
		p4_SuitColors = new AaRopPrefs4_SuitColors();
		p5_DSizeMisc = new AaRopPrefs5_DSizeMisc();
		p6_RedHints = new AaRopPrefs6_RedHints();
		p7_ShowBtns = new AaRopPrefs7_ShowBtns();

		addTab(Aaf.gT("menuOpt.newDeals"), null, p0_NewDealChoices, Aaf.gT("menuOpt.newDeals_TT"));
		addTab(Aaf.gT("menuOpt.autoPlay"), null, p1_AutoPlay, Aaf.gT("menuOpt.autoPlay_TT"));
		addTab(Aaf.gT("menuOpt.seat"), null, p2_KibSeat, Aaf.gT("menuOpt.seat_TT"));
		addTab(Aaf.gT("menuOpt.dfc"), null, p3_DFC, Aaf.gT("menuOpt.dfc_TT"));
		addTab(Aaf.gT("menuOpt.colors"), null, p4_SuitColors, Aaf.gT("menuOpt.colors_TT"));
		addTab(Aaf.gT("menuOpt.size"), null, p5_DSizeMisc, Aaf.gT("menuOpt.size_TT"));
		addTab(Aaf.gT("menuOpt.red"), null, p6_RedHints, Aaf.gT("menuOpt.red_TT"));
		addTab(Aaf.gT("menuOpt.show"), null, p7_ShowBtns, Aaf.gT("menuOpt.show_TT"));

	}

	public void showButtonStates() {

	}

	public void stateChanged(ChangeEvent e) {
		App.ropSelectedTabIndex = getSelectedIndex();
	}

	public void update_rightTabbed_outer_panels() {
		rightTabbedPanelsRefreshTimer.stop();
		rightTabbedPanelsRefreshTimer.start();
	}

	static boolean previous_sd = false;

	/**
	 * Yes we also control the lower options panel as well
	*/
	public Timer rightTabbedPanelsRefreshTimer = new Timer(100, new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			rightTabbedPanelsRefreshTimer.stop();

			boolean sd = App.isStudyDeal();

			boolean switched_to_true = (sd && !previous_sd);
			boolean switched = (sd != previous_sd);

			if (switched_to_true) {
				// apply defaults automatically does the showButtonStates
				p1_AutoPlay.applyDefaults();
				p2_KibSeat.applyDefaults();
				App.frame.lop.applyDefaults(); // only changes enable disable
			}
			else if (switched) {
				p1_AutoPlay.showButtonStates();
				p2_KibSeat.showButtonStates();
				App.frame.lop.showButtonStates(); // only does enable disable
			}

			if (switched_to_true && (App.ropSelectedTabIndex != App.RopTab_2_KibSeat)) {
				setSelectedIndex(App.RopTab_2_KibSeat); // p2_KibSeat
			}

			previous_sd = sd;
		}
	});

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

	QRadioButton(ItemListener listener, ButtonGroup rbGroup, Border border, Boolean initialValue, String actionCmd, String label, String tip) {
		super(label);
		setToolTipText(tip);
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
class QSelfDrawButton extends JButton {

	private static final long serialVersionUID = 1L;

	QSelfDrawButton(ActionListener listener, String label) {
		super(label);
		addActionListener(listener);
		setFocusable(false);
		setActionCommand(label);
		setEnabled(true);
		setOpaque(false);
	}

	public void paintComponent(Graphics g) {

		// super.paintComponent(g); not a good idea we get a std button

		Graphics2D g2 = (Graphics2D) g.create();
		Aaa.commonGraphicsSettings(g2);

		int w = getWidth();
		int h = getHeight();

		g2.setColor(getBackground());
		g2.fillRect(0, 0, h, w);

		g2.setColor(getForeground());
		Aaa.drawCenteredString(g2, getText(), 0, 0, w, h);
	}
}
