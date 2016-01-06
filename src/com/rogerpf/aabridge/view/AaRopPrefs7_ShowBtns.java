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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
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
//	QCheckBox showOldTextGray;
	QCheckBox showSaveBtns;
	QCheckBox showShfWkPlBtn;
	QCheckBox showRotationBtns;
	QCheckBox showClaimBtn;

	ButtonGroup mwGrp = new ButtonGroup();

	QButton resetAllPrefs;

	QButton rpfChoices;

	public AaRopPrefs7_ShowBtns() {
		setOpaque(true); // remember - we derive from ClickPanel which is setOpaque(false)
//		setBackground(SystemColor.control);

		setLayout(new MigLayout(App.simple + ", flowy"));

		Border bdr4 = BorderFactory.createEmptyBorder(1, 4, 1, 4);
//		Border bdr0 = BorderFactory.createEmptyBorder(0, 0, 0, 0);

		// @formatter:off
		add(anyLabel  = new QLabel("  Show                      -  Optional Buttons etc"), "gapy 5");
		anyLabel.setForeground(Aaa.optionsTitleGreen);

		add(anyLabel = new QLabel("Buttons - Show the . . ."), "gapy 10");
		anyLabel.setForeground(Aaa.optionsTitleGreen);
//		if (App.FLAG_canSave == true) {
		add(showSaveBtns        = new QCheckBox(this, App.showSaveBtns,       "'Save As' button  "), "gapy 3");
		    showSaveBtns.setBorder(bdr4);
//		}
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
//		add(showOldTextGray     = new QCheckBox(this, App.showOldTextGray,    "Gray out old text     -    Some movies themselves turn off the   Gray out   feature"), "gapy 10");
//		    showOldTextGray.setBorder(bdr4);

		    		
		add(anyLabel  = new QLabel("Reset ALL Options to the default"), "gapy 25");
		anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(resetAllPrefs = new QButton(this, "Reset & Close"), "gapx4");
		if (App.onMac == false)
		    resetAllPrefs.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 4));
		 
		if (App.devMode) {
			add(rpfChoices = new QButton(this, "RPf Choices"), "gapy20, gapx4");
			if (App.onMac == false)
			    rpfChoices.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 4));
		}
			    
		// @formatter:on
	}

	public void actionPerformed(ActionEvent e) {

		Object source = e.getSource();

		if (source == resetAllPrefs) {
			App.SetOptionsToDefaultAndClose();
			// it never comes back !!
		}
		if (source == rpfChoices) {

			App.showPoints = true;

//			App.showOldTextGray = true;

			App.showMouseWheelSplash = false;
			App.showRedNewBoardArrow = false;
			App.showRedVuGraphArrow = false;
			App.showRedEditArrow = false;
			App.showRedDividerArrow = false;

			App.showDfcExamHlt = false;
			App.showBidPlayMsgs = false;

			App.tutorialDealSize = 0;

			App.watchBidding = false;

			App.dfcAutoNext = 0; // fast/

			App.realSavesPath = "C:\\a\\";

			App.savePreferences();
			System.exit(0); // SHUTS DOWN aaBridge NOW
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
//		else if (source == showOldTextGray) {
//	        App.showOldTextGray = b;
//		}
		else if (source == showSaveBtns) {
		    App.showSaveBtns = b;
		    App.implement_showSaveBtns();
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
