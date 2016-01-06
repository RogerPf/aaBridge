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
import com.rogerpf.aabridge.model.Dir;

/**   
 */
public class AaRopPrefs2_SeatChoice extends ClickPanel implements ItemListener, ActionListener {

	private static final long serialVersionUID = 1L;

	QLabel anyLabel;

	QLabel existingDealsLabel;

	ButtonGroup rbRespectLinYou = new ButtonGroup();
	QRadioButton respLinYou;
	QRadioButton overrideLinYou;

	QLabel whichSeatLin;
	ButtonGroup rbGroupSeatLin = new ButtonGroup();
	QRadioButton southLin;
	QRadioButton eastLin;
	QRadioButton westLin;

	QLabel oldSouthLabel;
	ButtonGroup rbGroupSouthZone = new ButtonGroup();
	QRadioButton southZoneSouth;
	QRadioButton southZoneDeclarer;
//	QRadioButton southZoneName;
	QRadioButton southZoneYouSeat;

//	JTextField playerName;

	QCheckBox reviewFromPlay;
	QCheckBox showOpeningLead;

	ButtonGroup rbGroupPolicy = new ButtonGroup();
	QRadioButton lsh_policy0;
	QRadioButton lsh_policy1;
	QRadioButton lsh_policy2;

	QCheckBox youSeatPartnerVis;

	QCheckBox alwaysShowHidden;
	QCheckBox force_N_HiddenTut;
	QCheckBox force_W_HiddenTut;
	QCheckBox force_E_HiddenTut;
	QCheckBox force_S_HiddenTut;

	QButton compassClear;

	QButton applyDlaeLHO;
	QButton applyDlaeRHO;
	QButton applyDlaeDeclarer;

	QButton applyDefaults;

	public AaRopPrefs2_SeatChoice() {
		setOpaque(true); // remember - we derive from ClickPanel which is setOpaque(false)
//		setBackground(SystemColor.control);

		Border bdr0 = BorderFactory.createEmptyBorder(0, 0, 0, 0);
		Border bdr1 = BorderFactory.createEmptyBorder(1, 3, 1, 0);
		Border bdr4 = BorderFactory.createEmptyBorder(0, 4, 0, 4);
		Border bdr2 = BorderFactory.createEmptyBorder(1, 4, 1, 4);

		setLayout(new MigLayout(App.simple + ", flowy", "[][]", "[][]"));

		String rbInset = "gapx 7";

		// @formatter:off
		add(anyLabel  = new QLabel("  Seat Choice             for   Bridge Movies   and   'Entered Deals'  "), "gapy 5");
		anyLabel.setForeground(Aaa.optionsTitleGreen);

		add(anyLabel          = new QLabel("In a Movie with a contract showing on the table  OR  when 'Enter the Deal' is clicked  then the "), "gapy 5");
		anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(anyLabel          = new QLabel("      (South) area shows ?  "));
		anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(southZoneSouth    = new QRadioButton(this, rbGroupSouthZone, bdr1, App.putWhoInSouthZone == 0, "southZoneSouth",    "Actual South hand  "), rbInset);
		add(southZoneDeclarer = new QRadioButton(this, rbGroupSouthZone, bdr1, App.putWhoInSouthZone == 1, "southZoneDeclarer", "Declarer  "), rbInset);
//		add(southZoneName     = new QRadioButton(this, rbGroupSouthZone, bdr1, App.putWhoInSouthZone == 3, "southZoneName",     "Name"), rbInset + ", split2, flowx");
//		add(playerName        = new JTextField(12), "flowy");
		add(southZoneYouSeat  = new QRadioButton(this, rbGroupSouthZone, bdr1, App.putWhoInSouthZone == 2, "southZoneYouSeat",  "'You Seat'  set below       i.e. the same as  'Enter the Deal'  You Seat  "), rbInset);

//		playerName.setEditable(false);
//		playerName.setText( App.nameInSouthZone);

		add(anyLabel       = new QLabel("'Enter the Deal' sets the   "), "gapy 14");
		anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(anyLabel       = new QLabel("       'You Seat' to ?  "));
		anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(respLinYou     = new QRadioButton(this, rbRespectLinYou, bdr1,  App.respectLinYou, "respLinYou", "The  'You Seat'  in the deal  -  use this for pre-set bridge problems  "), rbInset);
		add(overrideLinYou = new QRadioButton(this, rbRespectLinYou, bdr1, !App.respectLinYou, "overrideLinYou", "The Seat below"), rbInset);

		boolean eastValLin  = (App.youSeatForLinDeal == Dir.East);
		boolean westValLin  = (App.youSeatForLinDeal == Dir.West);
		boolean southValLin = !(eastValLin || westValLin);

		Border bdr5 = BorderFactory.createEmptyBorder(0, 3, 1, 0);
		add(westLin        = new QRadioButton(this, rbGroupSeatLin, bdr1, westValLin,  "LHO  ",  "LHO"), "split2, gapx 2, flowx");
		add(eastLin        = new QRadioButton(this, rbGroupSeatLin, bdr1, eastValLin,  "RHO",  "RHO"), "flowy, gapy3");
		add(southLin       = new QRadioButton(this, rbGroupSeatLin, bdr5, southValLin, "Declarer", "Declarer"), "gapx 28");

		add(anyLabel       = new QLabel("You can change the  'You Seat'  at ANY TIME by clicking on players names / compass banner area  "), "gapx90, gapy3");
	    anyLabel.setForeground(Cc.RedStrong);
		
		add(anyLabel       = new QLabel("'Enter the Deal'  shows ?"), "gapy3");
		anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(reviewFromPlay = new QCheckBox(this, App.reviewFromPlay, "from Play  (skip Bidding)  "), "gapx 8");
		reviewFromPlay.setBorder(bdr1);
		add(showOpeningLead = new QCheckBox(this, App.showOpeningLead, "Opening Lead  -  OR Use the  '>' button  to show it  "), "gapx 8");
		showOpeningLead.setBorder(bdr1);

		add(anyLabel       = new QLabel("Non 'You Seat's  are set ?         when   'Entering the Deal'"), "gapy 20");
		anyLabel.setForeground(Aaa.optionsTitleGreen);

		Border bdr3 = BorderFactory.createEmptyBorder(0, 3, 0, 0);
		add(lsh_policy1     = new QRadioButton(this, rbGroupPolicy, bdr3, App.localShowHiddPolicy == 1,  "p1", "Show  "), rbInset );
		add(lsh_policy2     = new QRadioButton(this, rbGroupPolicy, bdr3, App.localShowHiddPolicy == 2,  "p2", "leave as is  "), rbInset);
		add(lsh_policy0     = new QRadioButton(this, rbGroupPolicy, bdr3, App.localShowHiddPolicy == 0,  "p0", "Hide  "), rbInset );

		add(youSeatPartnerVis = new QCheckBox(this, App.youSeatPartnerVis, "Partner of 'You Seat'  set  'Always Show'   -  useful when you are following only one side's BIDDING  "), "gapx 18");
		youSeatPartnerVis.setBorder(bdr1);

		add(anyLabel       = new QLabel("Special Use"), "gapy 20");
		anyLabel.setForeground(Aaa.optionsTitleGreen);
		
		add(force_N_HiddenTut = new QCheckBox(this, App.force_N_HiddenTut,  "N       Hide ALWAYS  - (when in turtorial mode)  "), "gapx13");
    		force_N_HiddenTut.setBorder(bdr2);
    		force_N_HiddenTut.setForeground(Cc.RedStrong);
   
		add(force_W_HiddenTut = new QCheckBox(this, App.force_W_HiddenTut,  ""),  "split 2, flowx");
	        force_W_HiddenTut.setBorder(bdr4);
		    force_W_HiddenTut.setForeground(Cc.RedStrong);
		   
		add(force_E_HiddenTut = new QCheckBox(this, App.force_E_HiddenTut,  "E    Hide ALWAYS  - (when in turtorial mode)  "));
			force_E_HiddenTut.setBorder(bdr4);
			force_E_HiddenTut.setForeground(Cc.RedStrong);
	
		add(force_S_HiddenTut = new QCheckBox(this, App.force_S_HiddenTut,  "S       Hide ALWAYS  - (when in turtorial mode)  "), "gapx13");
			force_S_HiddenTut.setBorder(bdr2);
			force_S_HiddenTut.setForeground(Cc.RedStrong);
	   
		add(compassClear = new QButton(this, "c"), "split 2, flowx");
		    compassClear.setToolTipText("Clear all 4 hides");
		    compassClear.setForeground(Cc.BlueStrong);
		    compassClear.setBorder(bdr4); // even on a MAC  -  they will have to suffer this one "no border" button
		    
		add(alwaysShowHidden  = new QCheckBox(this, App.alwaysShowHidden,  "Show ALL Hidden ALWAYS  - (in BOTH tutorial and std mode)  "), "gapx13, gapy4");
		    alwaysShowHidden.setBorder(bdr0);
		    alwaysShowHidden.setForeground(Cc.RedStrong);
		    
		add(anyLabel       = new QLabel("DlaE - Quick Set                    Defend like an Expert    options,  see the  Welcome Page  "), "gapy 18");
			anyLabel.setForeground(Aaa.optionsTitleGreen);
			
		if (App.onMac == false) {
		    add(applyDlaeLHO = new QButton(this, "LHO"), "gapx6, split 2, flowx");
				applyDlaeLHO.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 4));				
		    add(applyDlaeRHO = new QButton(this, "RHO"), "gapx10");
				applyDlaeRHO.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 4));
		}
		else  { // no gaps or borders on the MAC - they "break" the buttons appearance or (gaps are not wanted)
		    add(applyDlaeLHO = new QButton(this, "LHO"), "split 2, flowx");					
		    add(applyDlaeRHO = new QButton(this, "RHO"), "");
		}
		
		applyDlaeLHO.setToolTipText("Reset to - Defend Like an Expert (LHO)    options,  see the  Welcome Page  ");
		applyDlaeRHO.setToolTipText("Reset to - Defend Like an Expert (RHO)    options,  see the  Welcome Page  ");
				
	    add(applyDlaeDeclarer = new QButton(this, "Declarer"), "gapx16");
	        applyDlaeDeclarer.setToolTipText("Reset to - Defend Like an Expert (Declarer)    options,  see the  Welcome Page  ");
		if (App.onMac == false)
			applyDlaeDeclarer.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 4));
				
		add(applyDefaults = new QButton(this, "Apply Defaults"), "gapx4, gapy10");
			applyDefaults.setToolTipText("Reset all  Seat Options  to default values  ");
		if (App.onMac == false)
		    applyDefaults.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 4));
	
		// @formatter:on

		showButtonStates();
	}

	public void showButtonStates() {
		westLin.setEnabled(!App.respectLinYou);
		eastLin.setEnabled(!App.respectLinYou);
		southLin.setEnabled(!App.respectLinYou);

		if (App.visualMode == App.Vm_DealAndTutorial) {
			App.mg.refresh_for_youseat_change();
		}

		force_N_HiddenTut.setSelected(App.force_N_HiddenTut);
		force_W_HiddenTut.setSelected(App.force_W_HiddenTut);
		force_E_HiddenTut.setSelected(App.force_E_HiddenTut);
		force_S_HiddenTut.setSelected(App.force_S_HiddenTut);
		alwaysShowHidden.setSelected(App.alwaysShowHidden);
	}

	public void respectLinYouSetBy_mainNewBoard() {

		App.putWhoInSouthZone = 0; // South
		southZoneSouth.setSelected(true);
		southZoneDeclarer.setSelected(false);
		southZoneYouSeat.setSelected(false);

		respLinYou.setSelected(App.respectLinYou);
		overrideLinYou.setSelected(!App.respectLinYou);

		westLin.setEnabled(!App.respectLinYou);
		eastLin.setEnabled(!App.respectLinYou);
		southLin.setEnabled(!App.respectLinYou);

	}

	public void setDlaeCommon() {

		App.putWhoInSouthZone = 2; // South
		southZoneSouth.setSelected(false);
		southZoneDeclarer.setSelected(false);
		southZoneYouSeat.setSelected(true);

		App.respectLinYou = false;
		respLinYou.setSelected(false);
		overrideLinYou.setSelected(true);

		// App.youSeatForLinDeal = // all set by caller
		// westLin.setSelected(...);
		// eastLin.setSelected(...);
		// southLin.setSelected(...);

		App.reviewFromPlay = false;
		reviewFromPlay.setSelected(App.reviewFromPlay);
		App.showOpeningLead = true;
		showOpeningLead.setSelected(App.showOpeningLead);

		App.localShowHiddPolicy = 0; // hide
		lsh_policy1.setSelected(false); // 1 = show
		lsh_policy2.setSelected(false); // 2 = no change
		lsh_policy0.setSelected(true); // 0 = hide

		// App.localShowHidden = true; // well this is the initial default

		App.youSeatPartnerVis = false;
		youSeatPartnerVis.setSelected(false);

		App.force_N_HiddenTut = false;
		App.force_W_HiddenTut = false;
		App.force_E_HiddenTut = false;
		App.force_S_HiddenTut = false;
		App.alwaysShowHidden = false;
		force_N_HiddenTut.setSelected(App.force_N_HiddenTut);
		force_W_HiddenTut.setSelected(App.force_W_HiddenTut);
		force_E_HiddenTut.setSelected(App.force_E_HiddenTut);
		force_S_HiddenTut.setSelected(App.force_S_HiddenTut);
		alwaysShowHidden.setSelected(App.alwaysShowHidden);

	}

	public void actionPerformed(ActionEvent e) {

		if (App.allConstructionComplete == false)
			return;

		Object source = e.getSource();

		if (source == applyDlaeLHO) {

			setDlaeCommon();

			App.youSeatForLinDeal = Dir.West; // LHO
			westLin.setSelected(true);
			eastLin.setSelected(false);
			southLin.setSelected(false);

			App.savePreferences();
			showButtonStates();
			repaint();
		}

		else if (source == applyDlaeRHO) {

			setDlaeCommon();

			App.youSeatForLinDeal = Dir.East; // RHO
			westLin.setSelected(false);
			eastLin.setSelected(true);
			southLin.setSelected(false);

			App.savePreferences();
			showButtonStates();
			repaint();
		}

		else if (source == applyDlaeDeclarer) {

			setDlaeCommon();

			App.youSeatForLinDeal = Dir.South; // Declarer
			westLin.setSelected(false);
			eastLin.setSelected(false);
			southLin.setSelected(true);

			App.savePreferences();
			showButtonStates();
			repaint();
		}

		else if (source == applyDefaults) {

			App.putWhoInSouthZone = 0; // South
			southZoneSouth.setSelected(true);
			southZoneDeclarer.setSelected(false);
			southZoneYouSeat.setSelected(false);

			App.respectLinYou = true;
			respLinYou.setSelected(true);
			overrideLinYou.setSelected(false);

			App.youSeatForLinDeal = Dir.South; // declarer
			westLin.setSelected(false);
			eastLin.setSelected(false);
			southLin.setSelected(true);

			App.reviewFromPlay = true;
			reviewFromPlay.setSelected(App.reviewFromPlay);
			App.showOpeningLead = true;
			showOpeningLead.setSelected(App.showOpeningLead);

			App.localShowHiddPolicy = 2;
			lsh_policy1.setSelected(false); // 1 = show
			lsh_policy0.setSelected(false); // 0 = hide
			lsh_policy2.setSelected(true); // 2 = no change

			// App.localShowHidden = true; // well this is the intial default

			App.youSeatPartnerVis = false;
			youSeatPartnerVis.setSelected(false);

			App.force_N_HiddenTut = false;
			App.force_W_HiddenTut = false;
			App.force_E_HiddenTut = false;
			App.force_S_HiddenTut = false;
			App.alwaysShowHidden = false;
			force_N_HiddenTut.setSelected(App.force_N_HiddenTut);
			force_W_HiddenTut.setSelected(App.force_W_HiddenTut);
			force_E_HiddenTut.setSelected(App.force_E_HiddenTut);
			force_S_HiddenTut.setSelected(App.force_S_HiddenTut);
			alwaysShowHidden.setSelected(App.alwaysShowHidden);

			App.savePreferences();
			showButtonStates();
			repaint();
		}
		else if (source == compassClear) {

			App.force_N_HiddenTut = false;
			App.force_W_HiddenTut = false;
			App.force_E_HiddenTut = false;
			App.force_S_HiddenTut = false;
			App.alwaysShowHidden = false;

			force_N_HiddenTut.setSelected(App.force_N_HiddenTut);
			force_W_HiddenTut.setSelected(App.force_W_HiddenTut);
			force_E_HiddenTut.setSelected(App.force_E_HiddenTut);
			force_S_HiddenTut.setSelected(App.force_S_HiddenTut);
			alwaysShowHidden.setSelected(App.alwaysShowHidden);

			showButtonStates();
			App.frame.rop.p1_AutoPlay.showButtonStates();
			App.frame.repaint();
		}

	}

	/** This listens for the check box changed event */
	public void itemStateChanged(ItemEvent e) {

		boolean posCalcPlease = false;
		boolean youSeatChange = false;

		boolean b = (e.getStateChange() == ItemEvent.SELECTED);
		Object source = e.getItemSelectable();

		if (source == reviewFromPlay) {
			App.reviewFromPlay = b;
		}
		else if (source == showOpeningLead) {
			App.showOpeningLead = b;
		}
		else if (source == youSeatPartnerVis) {
			App.youSeatPartnerVis = b;
		}
		else if (source == force_N_HiddenTut) {
			App.force_N_HiddenTut = b;
		}
		else if (source == force_W_HiddenTut) {
			App.force_W_HiddenTut = b;
		}
		else if (source == force_E_HiddenTut) {
			App.force_E_HiddenTut = b;
		}
		else if (source == force_S_HiddenTut) {
			App.force_S_HiddenTut = b;
		}
		else if (source == alwaysShowHidden) {
			App.alwaysShowHidden = b;
		}

		// we are only interested in the selected values for the radio buttons

		else if (b == false) {
			; // do nothing
		}

		else if (source == southZoneSouth) {
			App.putWhoInSouthZone = 0;
			posCalcPlease = true;
		}
		else if (source == southZoneDeclarer) {
			App.putWhoInSouthZone = 1;
			posCalcPlease = true;
		}
//		else if (source == southZoneName) {
//			App.putWhoInSouthZone = 3;
//			posCalcPlease = true;
//		}
		else if (source == southZoneYouSeat) {
			App.putWhoInSouthZone = 2;
			posCalcPlease = true;
		}

		else if (source == respLinYou) {
			App.respectLinYou = true;
			posCalcPlease = true;
		}
		else if (source == overrideLinYou) {
			App.respectLinYou = false;
			posCalcPlease = true;
		}

		else if (source == southLin) {
			App.youSeatForLinDeal = Dir.South; // declarer
			posCalcPlease = true;
			youSeatChange = (App.respectLinYou == false);
		}
		else if (source == westLin) {
			App.youSeatForLinDeal = Dir.West; // LHO
			posCalcPlease = true;
			youSeatChange = (App.respectLinYou == false);
		}
		else if (source == eastLin) {
			App.youSeatForLinDeal = Dir.East; // RHO
			posCalcPlease = true;
			youSeatChange = (App.respectLinYou == false);
		}

		else if (source == lsh_policy0) {
			App.localShowHiddPolicy = 0;
//			posCalcPlease = true;
		}
		else if (source == lsh_policy1) {
			App.localShowHiddPolicy = 1;
			App.youSeatPartnerVis = false;
			youSeatPartnerVis.setSelected(false);
//			posCalcPlease = true;
		}
		else if (source == lsh_policy2) {
			App.localShowHiddPolicy = 2;
			App.youSeatPartnerVis = false;
			youSeatPartnerVis.setSelected(false);
//			posCalcPlease = true;
		}

		if (App.allConstructionComplete) {
			App.savePreferences();

			if (App.isVmode_InsideADeal() && App.deal.isContractReal()) {
				// if we are in a tutorial this happens when the tutorial is 'calculated'
				// to match the inside deal to look the same we also do it here.

				if (posCalcPlease) {
//					if (App.putWhoInSouthZone == 3) {
//						App.calcCompassPhyOffset();
//					}
//					else 
					{
						int offsetWanted = 0; // 0 = no extra rotation (keep south south)
						if (App.putWhoInSouthZone == 0 /* default so South in South zone */) {
							//
						}
						else {
							offsetWanted = App.deal.contractCompass.rotate180().v;
							if (!App.respectLinYou && App.putWhoInSouthZone == 2) { // /* "You Seat" set below */ /* Assume true == south */) {
								offsetWanted = (4 + offsetWanted + (App.youSeatForLinDeal.v - 2)) % 4;
							}
						}
						while (App.getCompassPhyOffset() != offsetWanted)
							App.incOffsetClockwise();
					}
				}

				if (youSeatChange) {
					Dir newYou = App.deal.contractCompass;
					if (!App.respectLinYou) {
						newYou = Dir.directionFromInt((App.deal.contractCompass.v + (App.youSeatForLinDeal.v - 2)) % 4);
					}
					App.deal.setYouSeatHint(newYou);
				}

				App.gbp.matchPanelsToDealState();
			}
			showButtonStates();
			App.frame.rop.p1_AutoPlay.showButtonStates();
			App.frame.repaint();
		}

	}
}
