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

import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

import net.miginfocom.swing.MigLayout;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;

/**   
 */
class AaRopPrefs7_ShowBtns extends ClickPanel implements ItemListener, ActionListener {

	private static final long serialVersionUID = 1L;

	QLabel anyLabel;

	QCheckBox showPoints;
	QCheckBox showLTC;
	QCheckBox showSaveBtns;
	QCheckBox showDepFinBtns;
	QCheckBox showEdPyCmdBarBtns;
	QCheckBox showShfWkPlBtn;
	QCheckBox showRotationBtns;
	QCheckBox showClaimBtn;

	QButton resetAllPrefs;

	public AaRopPrefs7_ShowBtns() {
		setOpaque(true); // remember - we derive from ClickPanel which is setOpaque(false)
		setBackground(SystemColor.control);

		setLayout(new MigLayout(App.simple + ", flowy"));

		Border bdr4 = BorderFactory.createEmptyBorder(1, 4, 1, 4);
//		Border bdr0 = BorderFactory.createEmptyBorder(0, 0, 0, 0);

		// @formatter:off
		add(anyLabel  = new QLabel("Show  -  Optional Buttons etc"), "gapy 5");
		anyLabel.setForeground(Aaa.optionsTitleGreen);

		add(anyLabel = new QLabel("Buttons - Show the . . ."), "gapy 10");
		anyLabel.setForeground(Aaa.optionsTitleGreen);
//		if (App.FLAG_canSave == true) {
		add(showSaveBtns        = new QCheckBox(this, App.showSaveBtns,       "'Save As' button  "), "gapy 3");
		    showSaveBtns.setBorder(bdr4);
		add(showDepFinBtns      = new QCheckBox(this, App.showDepFinBtns,     "DeepFinesse - Export and Import buttons.   They use the file  -   " + App.depFinOutInBoth + "  "));
			showDepFinBtns.setBorder(bdr4);
//		}
		add(showEdPyCmdBarBtns  = new QCheckBox(this, App.showEdPyCmdBarBtns, "'Edit' & 'Play' CmdBar buttons   -  these let you   Enter the Deal   quickly it that mode  "));
		showEdPyCmdBarBtns.setBorder(bdr4);
		add(showShfWkPlBtn      = new QCheckBox(this, App.showShfWkPlBtn,     "'Shuf Op' button  -  show  the   Shuf Op - 'Shuffle Weakest Pair and enter Play' button, most of the time "));
	    showShfWkPlBtn.setBorder(bdr4);
		add(showRotationBtns    = new QCheckBox(this, App.showRotationBtns,   "'Clockwise and Anti-clockwise' rotation buttons  "));
		    showRotationBtns.setBorder(bdr4);
		add(showClaimBtn        = new QCheckBox(this, App.showClaimBtn,       "'Claim' button - Allows you to end a hand with a claim  "));
		    showClaimBtn.setBorder(bdr4);

		add(anyLabel = new QLabel("Hand Info - Show the . . ."), "gapy 15");
		anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(showPoints          = new QCheckBox(this, App.showPoints,         "Point Count   (HCP)  "));
		    showPoints.setBorder(bdr4);
		add(showLTC             = new QCheckBox(this, App.showLTC,            "Losing Trick Count - See Wikipedia - Losing Trick Count with refinements  "));
		    showLTC.setBorder(bdr4);


		add(anyLabel  = new QLabel("Reset ALL Options to the default"), "gapy 15");
		anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(resetAllPrefs = new QButton(this, "Reset & Close"), "gapx12");
		    resetAllPrefs.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 4));
		// @formatter:on
	}

	public void actionPerformed(ActionEvent e) {

		if ("Reset & Close".equals(e.getActionCommand())) {
			if (App.allConstructionComplete) {
				App.SetOptionsToDefaultAndClose();
				// it never comes back !!
			}
		}
	}

	/** This listens for the check box changed event */
	public void itemStateChanged(ItemEvent e) {

		boolean shaker = false;

		boolean b = (e.getStateChange() == ItemEvent.SELECTED);
		Object source = e.getItemSelectable();

		// @formatter:off
		if (source == showPoints) {
			App.showPoints = b;
		}
		else if (source == showLTC) {
	        App.showLTC = b;
		}
		else if (source == showSaveBtns) {
		    App.showSaveBtns = b;
		    App.implement_showSaveBtns();
		}
		else if (source == showDepFinBtns) {
		    App.showDepFinBtns = b;
		    App.implement_showDepFinBtns();
		}
		else if (source == showEdPyCmdBarBtns) {
		    App.showEdPyCmdBarBtns = b;
		    App.calcApplyBarVisiblity();
		}
		else if (source == showShfWkPlBtn) {
            App.showShfWkPlBtn = b;
		    App.calcApplyBarVisiblity();
        }
		else if (source == showRotationBtns) {
                      App.showRotationBtns = b;
            App.implement_showRotationBtns();
		}
		else if (source == showClaimBtn) {
                       App.showClaimBtn = b;
            App.implement_showClaimBtn();
		}

		if (b == false) {
			; // do nothing
		}
		


		if (App.allConstructionComplete) {
			App.savePreferences();
			App.calcCompassPhyOffset();
			App.gbp.dealDirectionChange();
			if (shaker) {
				App.frame.calcAllMigLayoutStrings();
				App.setVisualMode(App.visualMode);
				App.frame.payloadPanelShaker();
			}
			App.frame.repaint();
		}
		// @formatter:on

	}
}
