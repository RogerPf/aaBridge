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

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.border.Border;

import net.miginfocom.swing.MigLayout;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.Aaf;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.model.Cc;
import com.rogerpf.aabridge.model.Dir;

/**   
 */
public class AaRopPrefs2_SeatChoice extends ClickPanel implements ItemListener, ActionListener {

	private static final long serialVersionUID = 1L;

	QLabel anyLabel;

	QLabel existingDealsLabel;

	ButtonGroup showFineBG = new ButtonGroup();
	QRadioButton showFine0;
	QRadioButton showFine1;

	boolean showFine = false;

	QLabel southZoneLabel1;
	QLabel southZoneLabel2;
	ButtonGroup rbGroupSouthZone = new ButtonGroup();
	QRadioButton southZoneSouth;
	QRadioButton southZoneDeclarer;
	QRadioButton southZoneYouSeat;

	ButtonGroup rbRespectLinYou = new ButtonGroup();
	QRadioButton respLinYou;
	QRadioButton overrideLinYou;

	QLabel seatLinLabel1;
	QLabel seatLinLabel2;
	ButtonGroup seatLinBG = new ButtonGroup();
	QRadioButton southLin;
	QRadioButton eastLin;
	QRadioButton westLin;

	QCheckBox reviewFromPlay;
	QCheckBox showOpeningLead;

	ButtonGroup rbGroupPolicy = new ButtonGroup();
	QRadioButton lsh_policy0;
	QRadioButton lsh_policy1;
	QRadioButton lsh_policy2;

	QCheckBox youSeatPartnerVis;

	QButton applyDlaeLHO;
	QButton applyDlaeRHO;
	QButton applyDlaeDeclarer;

	QButton applyDefaults;

	QCheckBox alwaysShowHidden;
	QCheckBox force_N_HiddenTut;
	QCheckBox force_W_HiddenTut;
	QCheckBox force_E_HiddenTut;
	QCheckBox force_S_HiddenTut;

	QButton twisterReset;
	QButton twisterLeft;
	QButton twisterRight;

	QButton compassClear;

	QCheckBox forceYouSeatToSouthZone;

	QCheckBox forceShowEtd;

	public AaRopPrefs2_SeatChoice() {
		setOpaque(true); // remember - we derive from ClickPanel which is setOpaque(false)
//		setBackground(SystemColor.control);

		Border bdr0 = BorderFactory.createEmptyBorder(0, 0, 0, 0);
		Border bdr1 = BorderFactory.createEmptyBorder(1, 3, 1, 0);
		Border bdr4 = BorderFactory.createEmptyBorder(0, 4, 0, 4);
		Border bdr2 = BorderFactory.createEmptyBorder(1, 4, 1, 4);

		setLayout(new MigLayout(App.simple + ", hidemode 1, flowy", "[][]", "[][]"));

		String rbInset = "gapx 7";

		// @formatter:off
		add(anyLabel  = new QLabel(Aaf.gT("menuOpt.seat")), "gapx 5, gapy 5");
		anyLabel.setForeground(Aaa.optionsTitleGreen);

		add(anyLabel          = new QLabel(Aaf.gT("sChoiceTab.extra")), "gapy 9");
		anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(showFine1         = new QRadioButton(this, showFineBG, bdr0, showFine == true,   "Show", Aaf.gT("sChoiceTab.show")), rbInset + ", split2, flowx");
		add(showFine0         = new QRadioButton(this, showFineBG, bdr0, showFine == false,  "Hide", Aaf.gT("sChoiceTab.hide")), "gapx 15, flowy");

		add(southZoneLabel1   = new QLabel(Aaf.gT("sChoiceTab.edt1")), "gapy 5");
		    southZoneLabel1.setForeground(Aaa.optionsTitleBLue);
		    
		add(southZoneLabel2   = new QLabel(Aaf.gT("sChoiceTab.edt2")), "gapx 15");
		    southZoneLabel2.setForeground(Aaa.optionsTitleBLue);
		    
		add(southZoneSouth    = new QRadioButton(this, rbGroupSouthZone, bdr1, App.putWhoInSouthZone == 0, "southZoneSouth",    Aaf.gT("sChoiceTab.actual")), rbInset);
		add(southZoneDeclarer = new QRadioButton(this, rbGroupSouthZone, bdr1, App.putWhoInSouthZone == 1, "southZoneDeclarer", Aaf.gT("cmnTab.decl")), rbInset);
		add(southZoneYouSeat  = new QRadioButton(this, rbGroupSouthZone, bdr1, App.putWhoInSouthZone == 2, "southZoneYouSeat",  Aaf.gT("sChoiceTab.asYou")), rbInset);

		add(seatLinLabel1     = new QLabel(Aaf.gT("sChoiceTab.edtSetsThe")));
		    seatLinLabel1.setForeground(Aaa.optionsTitleBLue);
		    seatLinLabel1.setBorder(BorderFactory.createEmptyBorder((App.onMac ? 8 : 14), 4, 1, 4));
		    
		add(seatLinLabel2       = new QLabel(Aaf.gT("sChoiceTab.youSeatTo")), "gapx 15");
		    seatLinLabel2.setForeground(Aaa.optionsTitleBLue);
		    
		add(respLinYou     = new QRadioButton(this, rbRespectLinYou, bdr1,  App.respectLinYou, "respLinYou", Aaf.gT("sChoiceTab.uInDeal")), rbInset);
		add(overrideLinYou = new QRadioButton(this, rbRespectLinYou, bdr1, !App.respectLinYou, "overrideLinYou", Aaf.gT("sChoiceTab.seatBelow")), rbInset);

		boolean eastValLin  = (App.youSeatForLinDeal == Dir.East);
		boolean westValLin  = (App.youSeatForLinDeal == Dir.West);
		boolean southValLin = !(eastValLin || westValLin);

		Border bdr5 = BorderFactory.createEmptyBorder(0, 3, 1, 0);
		add(westLin        = new QRadioButton(this, seatLinBG, bdr1, westValLin,  "LHO",  Aaf.gT("sChoiceTab.lho")), "split2, gapx 2, flowx");
		add(eastLin        = new QRadioButton(this, seatLinBG, bdr1, eastValLin,  "RHO",  Aaf.gT("sChoiceTab.rho")), "flowy, gapy3");
		add(southLin       = new QRadioButton(this, seatLinBG, bdr5, southValLin, "Declarer", Aaf.gT("cmnTab.decl")), "gapx 28");
		
		add(anyLabel       = new QLabel(Aaf.gT("sChoiceTab.etdShows")), (App.onMac ? "gapy8" : "gapy14"));
		anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(reviewFromPlay = new QCheckBox(this, App.reviewFromPlay, Aaf.gT("sChoiceTab.fromPlay")), "gapx 8");
		reviewFromPlay.setBorder(bdr1);
		add(showOpeningLead = new QCheckBox(this, App.showOpeningLead, Aaf.gT("sChoiceTab.fromOpen")), "gapx 8");
		showOpeningLead.setBorder(bdr1);

		add(anyLabel       = new QLabel(Aaf.gT("sChoiceTab.non")), (App.onMac ? "gapy8" : "gapy16"));
		anyLabel.setForeground(Aaa.optionsTitleGreen);

		Border bdr3 = BorderFactory.createEmptyBorder(0, 3, 0, 0);
		add(lsh_policy1     = new QRadioButton(this, rbGroupPolicy, bdr3, App.localShowHiddPolicy == 1,  "p1", Aaf.gT("sChoiceTab.show")), rbInset );
		add(lsh_policy2     = new QRadioButton(this, rbGroupPolicy, bdr3, App.localShowHiddPolicy == 2,  "p2", Aaf.gT("sChoiceTab.leaveAs")), rbInset);
		add(lsh_policy0     = new QRadioButton(this, rbGroupPolicy, bdr3, App.localShowHiddPolicy == 0,  "p0", Aaf.gT("sChoiceTab.hide")), rbInset );

		add(youSeatPartnerVis = new QCheckBox(this, App.youSeatPartnerVis, Aaf.gT("sChoiceTab.pAlways")), "gapx 5, gapy 3");
		youSeatPartnerVis.setBorder(bdr1);
		    
		add(anyLabel       = new QLabel(Aaf.gT("sChoiceTab.dlaeQuick")), "gapy 18");
			anyLabel.setForeground(Aaa.optionsTitleGreen);
			
		if (App.onMac == false) {
		    add(applyDlaeLHO = new QButton(this, Aaf.gT("sChoiceTab.lho")), "gapx6, split 2, flowx");
				applyDlaeLHO.setBorder(BorderFactory.createEmptyBorder(2, 4, 1, 4));				
		    add(applyDlaeRHO = new QButton(this, Aaf.gT("sChoiceTab.rho")), "gapx10");
				applyDlaeRHO.setBorder(BorderFactory.createEmptyBorder(2, 4, 1, 4));
		}
		else  { // no gaps or borders on the MAC - they "break" the buttons appearance or (gaps are not wanted)
		    add(applyDlaeLHO = new QButton(this, Aaf.gT("sChoiceTab.lho")), "split 2, flowx");					
		    add(applyDlaeRHO = new QButton(this, Aaf.gT("sChoiceTab.rho")), "");
		}
		
		applyDlaeLHO.setToolTipText(Aaf.gT("sChoiceTab.dlaeLho_TT"));
		applyDlaeRHO.setToolTipText(Aaf.gT("sChoiceTab.dlaeRho_TT"));
				
	    add(applyDlaeDeclarer = new QButton(this, Aaf.gT("cmnTab.decl")), "gapx16");
	        applyDlaeDeclarer.setToolTipText(Aaf.gT("sChoiceTab.dlaeDecl_TT"));
		if (App.onMac == false)
			applyDlaeDeclarer.setBorder(BorderFactory.createEmptyBorder(2, 4, 1, 4));
		
		

		// bright purple-pink dot
	    add(anyLabel      = new QLabel("" + (char) 0x25cf /* a dot */), "split 2, flowx");
	    {
	    	Font fo = anyLabel.getFont();
	    	anyLabel.setFont(fo.deriveFont(fo.getSize() * 1.2f));
	    }
	    anyLabel.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
	    anyLabel.setForeground(new Color(0xdd00ff));
	    anyLabel.setToolTipText(Aaf.gT("sChoiceTab.applyDef_TT"));

		add(applyDefaults = new QButton(this, Aaf.gT("cmnTab.applyDef")), "gapx2, gapy25");
			applyDefaults.setToolTipText(Aaf.gT("sChoiceTab.applyDef_TT"));			
		if (App.onMac == false)
		    applyDefaults.setBorder(BorderFactory.createEmptyBorder(5, 1, 4, 4));
		

		
		add(anyLabel     = new QLabel(Aaf.gT("sChoiceTab.override")), (App.onMac ? "gapy8" : "gapy25"));
		    anyLabel.setForeground(Aaa.optionsTitleGreen);
		    
		add(forceShowEtd = new QCheckBox(this, App.forceShowEtd,  Aaf.gT("sChoiceTab.forceEtd")));
	        forceShowEtd.setBorder(bdr2);
	        forceShowEtd.setForeground(Cc.RedStrong);


	        
	    add(anyLabel       = new QLabel(Aaf.gT("sChoiceTab.special")), (App.onMac ? "gapy8" : "gapy25"));
		    anyLabel.setForeground(Aaa.optionsTitleGreen);
		
		add(twisterReset = new QButton(this, Aaf.gT("sChoiceTab.r")), "gapy2, split 4, flowx");
			twisterReset.setToolTipText(Aaf.gT("sChoiceTab.r_TT"));
			twisterReset.setForeground(Cc.BlueStrong);
			twisterReset.setBorder(bdr4); // even on a MAC  -  they will have to suffer this one "no border" button
		
		add(twisterLeft = new QButton(this, "<"), "gapx 8");
			twisterLeft.setToolTipText(Aaf.gT("sChoiceTab.anti_TT"));
			twisterLeft.setForeground(Cc.BlueStrong);
			twisterLeft.setBorder(bdr4); // even on a MAC  -  they will have to suffer this one "no border" button
		
		add(twisterRight = new QButton(this, ">"), "gapx 8");
			twisterRight.setToolTipText(Aaf.gT("sChoiceTab.clock_TT"));
			twisterRight.setForeground(Cc.BlueStrong);
			twisterRight.setBorder(bdr4); // even on a MAC  -  they will have to suffer this one "no border" button
	    
		add(anyLabel = new QLabel(Aaf.gT("sChoiceTab.uni")), "gapx 8");
		    anyLabel.setForeground(Cc.RedStrong);
	    
		add(compassClear = new QButton(this, Aaf.gT("sChoiceTab.c")), "split 2, flowx");
		    compassClear.setToolTipText(Aaf.gT("sChoiceTab.c_TT"));
		    compassClear.setForeground(Cc.BlueStrong);
		    compassClear.setBorder(bdr4); // even on a MAC  -  they will have to suffer this one "no border" button

		add(alwaysShowHidden  = new QCheckBox(this, App.alwaysShowHidden,  Aaf.gT("sChoiceTab.allAlways")), "gapx13, gapy4");
		    alwaysShowHidden.setBorder(bdr0);
		    alwaysShowHidden.setForeground(Cc.RedStrong);

		add(force_N_HiddenTut = new QCheckBox(this, App.force_N_HiddenTut, Dir.getLangDirChar(Dir.North) + "      " + Aaf.gT("sChoiceTab.forceHidden")), "gapx13, gapy4");
    		force_N_HiddenTut.setBorder(bdr2);
    		force_N_HiddenTut.setForeground(Cc.RedStrong);
   
		add(force_W_HiddenTut = new QCheckBox(this, App.force_W_HiddenTut,  ""),  "split 2, flowx");
	        force_W_HiddenTut.setBorder(bdr4);
		    force_W_HiddenTut.setForeground(Cc.RedStrong);
		   
		add(force_E_HiddenTut = new QCheckBox(this, App.force_E_HiddenTut,  Dir.getLangDirChar(Dir.East) + "      " + Aaf.gT("sChoiceTab.forceHidden")));
			force_E_HiddenTut.setBorder(bdr4);
			force_E_HiddenTut.setForeground(Cc.RedStrong);
	
		add(force_S_HiddenTut = new QCheckBox(this, App.force_S_HiddenTut,  Dir.getLangDirChar(Dir.South) + "      " + Aaf.gT("sChoiceTab.forceHidden")), "gapx13");
			force_S_HiddenTut.setBorder(bdr2);
			force_S_HiddenTut.setForeground(Cc.RedStrong);
	   		    
		add(forceYouSeatToSouthZone = new QCheckBox(this, App.forceYouSeatToSouthZone,  Aaf.gT("sChoiceTab.forceYouS")), "gapy 3");
		    forceYouSeatToSouthZone.setBorder(bdr2);
		    forceYouSeatToSouthZone.setForeground(Cc.RedStrong);

		    // @formatter:on

		showButtonStates();
	}

	public void showButtonStates() {

		southZoneLabel1.setVisible(showFine == true);
		southZoneLabel2.setVisible(showFine == true);
		southZoneSouth.setVisible(showFine == true);
		southZoneDeclarer.setVisible(showFine == true);
		southZoneYouSeat.setVisible(showFine == true);

		respLinYou.setVisible(showFine == true);
		overrideLinYou.setVisible(showFine == true);

		seatLinLabel1.setVisible(showFine == true);
		seatLinLabel2.setVisible(showFine == true);
		southLin.setVisible(showFine == true);
		eastLin.setVisible(showFine == true);
		westLin.setVisible(showFine == true);

		westLin.setEnabled(!App.respectLinYou);
		eastLin.setEnabled(!App.respectLinYou);
		southLin.setEnabled(!App.respectLinYou);

		if (App.visualMode == App.Vm_DealAndTutorial) {
			App.mg.refresh_for_youseat_change();
		}

		alwaysShowHidden.setSelected(App.alwaysShowHidden);
		force_N_HiddenTut.setSelected(App.force_N_HiddenTut);
		force_W_HiddenTut.setSelected(App.force_W_HiddenTut);
		force_E_HiddenTut.setSelected(App.force_E_HiddenTut);
		force_S_HiddenTut.setSelected(App.force_S_HiddenTut);
		forceYouSeatToSouthZone.setSelected(App.forceYouSeatToSouthZone);
		forceShowEtd.setSelected(App.forceShowEtd);
	}

	public void respectLinYouSetBy_mainNewBoard(boolean skip_alwaysShowHidden) {

		App.putWhoInSouthZone = 0; // South
		southZoneSouth.setSelected(true);
		southZoneDeclarer.setSelected(false);
		southZoneYouSeat.setSelected(false);

		respLinYou.setSelected(App.respectLinYou);
		overrideLinYou.setSelected(!App.respectLinYou);

		westLin.setEnabled(!App.respectLinYou);
		eastLin.setEnabled(!App.respectLinYou);
		southLin.setEnabled(!App.respectLinYou);

		if (!skip_alwaysShowHidden) {
			App.alwaysShowHidden = false;
			App.force_N_HiddenTut = false;
			App.force_W_HiddenTut = false;
			App.force_E_HiddenTut = false;
			App.force_S_HiddenTut = false;
		}
		App.forceYouSeatToSouthZone = false;

		showButtonStates();
		App.frame.rop.p1_AutoPlay.showButtonStates();
	}

	public void setDlaeCommon() {

		App.putWhoInSouthZone = 2; // You seat set below
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

		App.alwaysShowHidden = false;
		App.force_N_HiddenTut = false;
		App.force_W_HiddenTut = false;
		App.force_E_HiddenTut = false;
		App.force_S_HiddenTut = false;
		App.forceYouSeatToSouthZone = false;

		showButtonStates();
		App.frame.rop.p1_AutoPlay.showButtonStates();
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
			applyDefaults();
			App.frame.rop.p1_AutoPlay.applyDefaults();

			App.savePreferences();
			App.calcCompassPhyOffset();
			App.gbp.dealMajorChange();
			App.frame.repaint();
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
		}

		else if (source == twisterReset) {
			App.allTwister_reset();
			App.calcCompassPhyOffset();
			App.gbp.dealMajorChange();
		}

		else if (source == twisterLeft) {
			App.allTwister_left();
			App.calcCompassPhyOffset();
			App.gbp.dealMajorChange();
		}

		else if (source == twisterRight) {
			App.allTwister_right();
			App.calcCompassPhyOffset();
			App.gbp.dealMajorChange();
		}

		if (App.allConstructionComplete) {
			App.savePreferences();

			showButtonStates();
			App.frame.rop.p1_AutoPlay.showButtonStates();

			App.frame.repaint();
		}
	}

	void applyDefaults() {

		showFine = false;
		showFine0.setSelected(showFine == false);
		showFine1.setSelected(showFine == true);

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

		App.localShowHiddPolicy = 0;
		lsh_policy1.setSelected(false); // 1 = show
		lsh_policy2.setSelected(false); // 2 = no change
		lsh_policy0.setSelected(true); // 0 = hide

		// App.localShowHidden = true; // well this is the intial default

		App.youSeatPartnerVis = false;
		youSeatPartnerVis.setSelected(false);

		App.alwaysShowHidden = false;
		App.force_N_HiddenTut = false;
		App.force_W_HiddenTut = false;
		App.force_E_HiddenTut = false;
		App.force_S_HiddenTut = false;
		App.forceYouSeatToSouthZone = false;
		App.forceShowEtd = false;

		showButtonStates();

		App.allTwister_reset();
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
		else if (source == forceYouSeatToSouthZone) {
			App.forceYouSeatToSouthZone = b;
		}
		else if (source == forceShowEtd) {
			App.forceShowEtd = b;
		}

		else if (source == showFine0) {
			showFine = false;
			showButtonStates();
		}
		else if (source == showFine1) {
			showFine = true;
			showButtonStates();
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
			// App.youSeatPartnerVis = false;
			// youSeatPartnerVis.setSelected(false);
//			posCalcPlease = true;
		}
		else if (source == lsh_policy2) {
			App.localShowHiddPolicy = 2;
			// App.youSeatPartnerVis = false;
			// youSeatPartnerVis.setSelected(false);
//			posCalcPlease = true;
		}
		else if (source == forceYouSeatToSouthZone) {
			App.forceYouSeatToSouthZone = b;
		}

		if (App.allConstructionComplete) {
			App.savePreferences();

			if (posCalcPlease) {
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

			if (youSeatChange) {
				Dir newYou = App.deal.contractCompass;
				if (!App.respectLinYou) {
					newYou = Dir.directionFromInt((App.deal.contractCompass.v + (App.youSeatForLinDeal.v - 2)) % 4);
				}
				App.deal.setYouSeatHint(newYou);
			}

			App.frame.invalidate();
			App.gbp.matchPanelsToDealState();

			showButtonStates();
			App.frame.rop.p1_AutoPlay.showButtonStates();
		}

	}
}
