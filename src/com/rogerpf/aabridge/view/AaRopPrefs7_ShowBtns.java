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
import com.rogerpf.aabridge.model.Cc;

/**   
 */
class AaRopPrefs7_ShowBtns extends ClickPanel implements ItemListener, ActionListener {

	private static final long serialVersionUID = 1L;

	QLabel anyLabel;

	QCheckBox showSaveBtns;
	QCheckBox showShfWkPlBtn;
	QCheckBox showRotationBtns;
	QCheckBox showClaimBtn;
	QCheckBox showPoorDefHint;

	QCheckBox showB1stBtn;
	QCheckBox showContBtn;

	QCheckBox showPoints;
	QCheckBox showLTC;

	QCheckBox fixLinuxLineSep;

	QButton applyDefaults;

	ButtonGroup mwGrp = new ButtonGroup();

	QButton resetAllPrefs;

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
		add(showSaveBtns        = new QCheckBox(this, App.showSaveBtns,       "'Save' & 'Save As'  buttons   -   Shows the    'Save' & 'Save As'  buttons"), "gapy 3");
		    showSaveBtns.setBorder(bdr4);
//		}
		add(showShfWkPlBtn      = new QCheckBox(this, App.showShfWkPlBtn,     "'Shuf Op' button  -  Shows  the   Shuf Op - 'Shuffle Weakest Pair and enter Play' button, most of the time  "));
	    showShfWkPlBtn.setBorder(bdr4);
		add(showRotationBtns    = new QCheckBox(this, App.showRotationBtns,   "'Clockwise and Anti-clockwise'  -  Shows the rotation buttons  "));
		    showRotationBtns.setBorder(bdr4);
		add(showClaimBtn        = new QCheckBox(this, App.showClaimBtn,       "'Claim' button   -  Shows the  'Claim' button   which Allows you to end a hand with a claim  "));
		    showClaimBtn.setBorder(bdr4);
		    
		add(anyLabel = new QLabel("With 'Enter the Deal' and also show"), "gapy 18");
			anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(showB1stBtn          = new QCheckBox(this, App.showB1stBtn,       "'1st' button     -  Shows the  '1st' button   which does an    'Enter the Deal'  then goes into  'Play mode'  starts from just after the  1st  lead  "), "gapy 3");
		    showB1stBtn.setBorder(bdr4);
		add(showContBtn          = new QCheckBox(this, App.showContBtn,       "'Cont' button   -  Shows the  'Cont' button   which does an    'Enter the Deal'  then goes into  'Play mode'  continues after all existing play  "));
		    showContBtn.setBorder(bdr4);

		add(anyLabel = new QLabel("In Deal Guidence   show"), "gapy 18");
			anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(showPoorDefHint         = new QCheckBox(this, App.showPoorDefHint, "Poor Defense hint    -  Shows / Hides  the  'Poor Defense Hint'   that shows when you enter  'Play'  mode  "), "gapy 3");
		    showPoorDefHint.setBorder(bdr4);

		add(anyLabel = new QLabel("Hand Info - Show the . . ."), "gapy 18");
		anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(showPoints          = new QCheckBox(this, App.showPoints,         "Point Count   (HCP)  "), "gapy 3");
		    showPoints.setBorder(bdr4);
		add(showLTC             = new QCheckBox(this, App.showLTC,            "Losing Trick Count - See Wikipedia - Losing Trick Count with refinements  "));
		    showLTC.setBorder(bdr4);
		    		
		add(anyLabel  = new QLabel("Linux Only"), "gapy 18");
		    anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(fixLinuxLineSep     = new QCheckBox(this, App.fixLinuxLineSep,    "Fix 'too close' lines   in Tutorials  "));
		   fixLinuxLineSep.setBorder(bdr4);
		   fixLinuxLineSep.setEnabled(App.onLinux);

		add(applyDefaults = new QButton(this, "Apply Defaults"), "gapy20, gapx4");
		if (App.onMac == false)
			applyDefaults.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 4));

		add(anyLabel  = new QLabel("BELOW Resets ALL Options in aaBridge to the default"), "gapy 55");
	        anyLabel.setForeground(Cc.RedStrong);
		add(resetAllPrefs = new QButton(this, "Reset ALL & Close"), "gapx10");
		    resetAllPrefs.setForeground(Cc.RedStrong);
		if (App.onMac == false)
		    resetAllPrefs.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 4));
		add(anyLabel  = new QLabel("TAKE CARE"));
            anyLabel.setForeground(Cc.RedStrong);		 			    
		// @formatter:on
	}

	public void actionPerformed(ActionEvent e) {

		Object source = e.getSource();

		if (source == applyDefaults) {

			App.showSaveBtns = true;
			App.showShfWkPlBtn = true;
			App.showRotationBtns = true;
			App.showClaimBtn = false;
			App.showPoorDefHint = true;

			App.showB1stBtn = false;
			App.showContBtn = false;

			App.showPoints = true;
			App.showLTC = false;
			App.fixLinuxLineSep = App.onLinux;

			showSaveBtns.setSelected(App.showSaveBtns);
			showShfWkPlBtn.setSelected(App.showShfWkPlBtn);
			showRotationBtns.setSelected(App.showRotationBtns);
			showClaimBtn.setSelected(App.showClaimBtn);
			showPoorDefHint.setSelected(App.showPoorDefHint);

			showB1stBtn.setSelected(App.showB1stBtn);
			showContBtn.setSelected(App.showContBtn);

			showPoints.setSelected(App.showPoints);
			showLTC.setSelected(App.showLTC);
			fixLinuxLineSep.setSelected(App.fixLinuxLineSep);

			App.savePreferences();
			App.calcCompassPhyOffset();
			App.gbp.dealDirectionChange();
			App.frame.repaint();

		}

		if (source == resetAllPrefs) {
			App.SetOptionsToDefaultAndClose();
			// it never comes back !!
		}

	}

	/** This listens for the check box changed event */
	public void itemStateChanged(ItemEvent e) {

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
		else if (source == showPoorDefHint) {
                       App.showPoorDefHint = b;
            App.implement_showClaimBtn();
		}
		else if (source == showClaimBtn) {
                       App.showClaimBtn = b;
            App.implement_showClaimBtn();
        }
		else if (source == showContBtn) {
					   App.showContBtn = b;
			 App.implement_showContBtn();
		}
		else if (source == showB1stBtn) {
			           App.showB1stBtn = b;
	         App.implement_showB1stBtn();
        }
		else if (source == fixLinuxLineSep) {
                       App.fixLinuxLineSep = b;
		}

		if (b == false) {
			; // do nothing
		}

		if (App.allConstructionComplete) {
			App.savePreferences();
			App.calcCompassPhyOffset();
			App.gbp.dealDirectionChange();
			App.frame.repaint();
		}
		// @formatter:on

	}
}
