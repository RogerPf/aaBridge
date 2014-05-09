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
class AaRopPrefs6_StartUp extends ClickPanel implements ItemListener, ActionListener {

	private static final long serialVersionUID = 1L;

	QLabel anyLabel;

//	ButtonGroup startUpGrp = new ButtonGroup();
//
//	QRadioButton s0_welcomeAndHelp;
//	QRadioButton s1_startWithBook;
//	QRadioButton s2_playBridge;

	QCheckBox showRedEditArrow;
	QCheckBox showRedDividerArrow;
	QCheckBox showRedVuGraphArrow;
	QCheckBox showDfcExamHlt;
	QCheckBox showBidPlayMsgs;
	QCheckBox showPoints;
	QCheckBox showLTC;
	QCheckBox showSaveBtns;
	QCheckBox showShfWkPlBtn;
	QCheckBox showRotationBtns;
	QCheckBox showClaimBtn;

	QCheckBox multiBookDisplay;

	QButton resetAllPrefs;

	public AaRopPrefs6_StartUp() {
		setOpaque(true); // remember - we derive from ClickPanel which is setOpaque(false)
		setBackground(SystemColor.control);

		setLayout(new MigLayout(App.simple + ", flowy"));

		Border bdr4 = BorderFactory.createEmptyBorder(1, 4, 1, 4);
//		Border bdr0 = BorderFactory.createEmptyBorder(0, 0, 0, 0);

		// @formatter:off
		add(anyLabel  = new QLabel("Start Up and Display options"), "gapy 5");
		anyLabel.setForeground(Aaa.optionsTitleGreen);
		
//		if (App.singleBookOnly == false) {
//			add(anyLabel = new QLabel("At  Start-Up  go to ?"), "gapy 8");
//			anyLabel.setForeground(Aaa.optionsTitleGreen);
//			String rbInset = "gapx 7";
//			ButtonGroup startUpGrp  = new ButtonGroup();
//			add(s0_welcomeAndHelp   = new QRadioButton(this, startUpGrp,  bdr0, App.startUpOption == App.startUp_0__welcome,       "sta0", "Welcome  Help  &  Examples"), rbInset);
//			add(s1_startWithBook    = new QRadioButton(this, startUpGrp,  bdr0, App.startUpOption == App.startUp_1__book,          "sta1", "Open at Start   -   the pre-selected  Start Book  "), rbInset);
//			add(s2_playBridge       = new QRadioButton(this, startUpGrp,  bdr0, App.startUpOption == App.startUp_2__playBridge,    "sta2", "Play Bridge"), rbInset);
//		}

		add(anyLabel = new QLabel("Prompts - Show the . . ."), "gapy 15");
		anyLabel.setForeground(Aaa.optionsTitleGreen);

		add(showBidPlayMsgs     = new QCheckBox(this, App.showBidPlayMsgs,     "'Bid' and 'Play' prompt messages   "));
	        showBidPlayMsgs.setBorder(bdr4);
		add(showRedVuGraphArrow = new QCheckBox(this, App.showRedVuGraphArrow, "Red Arrow, 'Extra Bar 4 Clickalbe Columns' hint   "));
	        showRedVuGraphArrow.setBorder(bdr4);
		add(showRedEditArrow    = new QCheckBox(this, App.showRedEditArrow,    "Red Arrow, 'Click  Edit  & use...' hint   "));
		    showRedEditArrow.setBorder(bdr4);
		add(showRedDividerArrow = new QCheckBox(this, App.showRedDividerArrow, "Red Arrow, 'Drag Divider...' hint   "));
		    showRedDividerArrow.setBorder(bdr4);
		add(showDfcExamHlt      = new QCheckBox(this, App.showDfcExamHlt,      "DFC  Exam btn boarder  highlight   "));
		    showDfcExamHlt.setBorder(bdr4);

		add(anyLabel = new QLabel("Buttons - Show the . . ."), "gapy 15");
		anyLabel.setForeground(Aaa.optionsTitleGreen);
		if (App.FLAG_canSave == false) {
			add(showSaveBtns        = new QCheckBox(this, App.showSaveBtns,       "'Save and Save As' buttons  "));
			    showSaveBtns.setBorder(bdr4);
		}
		add(showShfWkPlBtn      = new QCheckBox(this, App.showShfWkPlBtn,     "'Shuf Op' button  -  show  the   Shuf Op - 'Shuffle Weakest Pair and enter Play' button, most of the time "));
		    showShfWkPlBtn.setBorder(bdr4);
		add(showRotationBtns    = new QCheckBox(this, App.showRotationBtns,   "'Clockwise and Anti-clockwise' rotation buttons  "));
		    showRotationBtns.setBorder(bdr4);
		add(showClaimBtn        = new QCheckBox(this, App.showClaimBtn,       "'Claim' button - Allows you to end a hand with a claim  "));
		    showClaimBtn.setBorder(bdr4);

		add(anyLabel = new QLabel("Hand Info - Show the . . ."), "gapy 15");
		anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(showPoints          = new QCheckBox(this, App.showPoints,         "Point Count"));
		    showPoints.setBorder(bdr4);
		add(showLTC             = new QCheckBox(this, App.showLTC,            "Losing Trick Count - See Wikipedia - Losing Trick Count with refinements"));
		    showLTC.setBorder(bdr4);


		if (App.ourBookshelf.isDefaultToSingleBook()) // if we can be a single book then we let them swithc it to multi
		    add(multiBookDisplay = new QCheckBox(this, App.multiBookDisplay, "Show multiple books in book menu   -   needs aaBridge restart  "), "gapy 10");

		add(anyLabel  = new QLabel("Reset ALL Options to the default"), "gapy 10");
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
		if      (source == showRedEditArrow) {
            App.showRedEditArrow = b;
        }
		else if (source == showRedDividerArrow) {
            App.showRedDividerArrow = b;
        }
		else if (source == showRedVuGraphArrow) {
            App.showRedVuGraphArrow = b;
        }
		else if (source == showDfcExamHlt) {
            App.showDfcExamHlt = b;
        }
		else if (source == showBidPlayMsgs) {
            App.showBidPlayMsgs = b;
        }
		else if (source == showPoints) {
			App.showPoints = b;
		}
		else if (source == showLTC) {
	        App.showLTC = b;
		}
		else if (source == showSaveBtns) {
		               App.showSaveBtns = b;
		    App.implement_showSaveBtns();
		}
		else if (source == showShfWkPlBtn) {
		               App.showShfWkPlBtn = b;
		    App.implement_showSaveBtns();
		}
		else if (source == showRotationBtns) {
                       App.showRotationBtns = b;
            App.implement_showRotationBtns();
		}
		else if (source == showClaimBtn) {
                       App.showClaimBtn = b;
            App.implement_showClaimBtn();
		}
		else if (source == multiBookDisplay) {
            App.multiBookDisplay = b;
//			if (App.multiBookDisplay) { // the NEW val
//				App.startUpOption = App.startUp_0__welcome;
//			}
		}

		if (b == false) {
			; // do nothing
		}
//		else if (source == s0_welcomeAndHelp) {
//            App.startUpOption = App.startUp_0__welcome;
//        }
//		else if (source == s1_startWithBook) {
//            App.startUpOption = App.startUp_1__book;
//        }
//		else if (source == s2_playBridge) {
//            App.startUpOption = App.startUp_2__playBridge;
//        }
		


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
