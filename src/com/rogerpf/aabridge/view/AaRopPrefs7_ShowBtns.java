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
import com.rogerpf.aabridge.controller.Aaf;
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
	QCheckBox showEdHandsSuitSwap;

	QCheckBox showB1stBtn;
	QCheckBox showContBtn;

	QCheckBox showHCPs;

	ButtonGroup rbG_show2nd = new ButtonGroup();
	QRadioButton m_None;
	QRadioButton m_KnR;
	QRadioButton m_LTC_b;
	QRadioButton m_LTC_r;
	QRadioButton m_Banzai;
//	QRadioButton m_Bergen;

	ButtonGroup rbGroupSig = new ButtonGroup();
	QRadioButton size0;
	QRadioButton size1;
	QRadioButton size2;
	QRadioButton size3;
	QRadioButton size4;
	QRadioButton size5;
	QRadioButton size6;
	QRadioButton size7;
	QRadioButton size8;

	QCheckBox mouseWheelInverted;
	QCheckBox extraDepthOn3and4;

	QCheckBox fixLinuxLineSep;

	QButton applyDefaults;

	QButton resetAllPrefs;

	public AaRopPrefs7_ShowBtns() {
		setOpaque(true); // remember - we derive from ClickPanel which is setOpaque(false)
//		setBackground(SystemColor.control);

		setLayout(new MigLayout(App.simple + ", flowy"));

		Border bdr4 = BorderFactory.createEmptyBorder(1, 4, 1, 4);
		Border bdr0 = BorderFactory.createEmptyBorder(0, 0, 0, 0);

		// @formatter:off
		add(anyLabel  = new QLabel(Aaf.menuOpt_show_D), "gapy 5");
		anyLabel.setForeground(Aaa.optionsTitleGreen);

		add(anyLabel = new QLabel(Aaf.gT("showTab.buttons")), "gapy 10");
		anyLabel.setForeground(Aaa.optionsTitleGreen);

		add(showSaveBtns        = new QCheckBox(this, App.showSaveBtns,       Aaf.gT("showTab.save&")), "gapy 3");
		    showSaveBtns.setBorder(bdr4);

		add(showShfWkPlBtn      = new QCheckBox(this, App.showShfWkPlBtn,     Aaf.gT("showTab.shufOp")));
	    showShfWkPlBtn.setBorder(bdr4);
		add(showRotationBtns    = new QCheckBox(this, App.showRotationBtns,   Aaf.gT("showTab.rotation")));
		    showRotationBtns.setBorder(bdr4);
		add(showClaimBtn        = new QCheckBox(this, App.showClaimBtn,       Aaf.gT("showTab.claim")));
		    showClaimBtn.setBorder(bdr4);

		add(showEdHandsSuitSwap = new QCheckBox(this, App.showEdHandsSuitSwap,Aaf.gT("showTab.swap")));
		    showEdHandsSuitSwap.setBorder(bdr4);

		add(anyLabel = new QLabel(Aaf.gT("showTab.etdAlso")), "gapy 20");
			anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(showB1stBtn          = new QCheckBox(this, App.showB1stBtn,       Aaf.gT("showTab.1st")), "gapy 3");
		    showB1stBtn.setBorder(bdr4);
		add(showContBtn          = new QCheckBox(this, App.showContBtn,       Aaf.gT("showTab.cont")));
		    showContBtn.setBorder(bdr4);

		add(anyLabel = new QLabel(Aaf.gT("showTab.handInfo")), "gapy 15");
		    anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(showHCPs          = new QCheckBox(this, App.showHCPs,             Aaf.gT("showTab.hcp")), "gapy 3");
		    showHCPs.setBorder(bdr4);

		add(anyLabel = new QLabel(Aaf.gT("showTab.2ndMetric")), "gapy 20");
		    anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(m_None   = new QRadioButton(this, rbG_show2nd,  null, App.show2ndMetric == App.Metric_None,  "", Aaf.gT("showTab.none")), "gapx 4");
		    m_None.setBorder(bdr0);
		add(m_KnR    = new QRadioButton(this, rbG_show2nd,  null, App.show2ndMetric == App.Metric_KnR,   "", Aaf.gT("showTab.KnR")), "gapx 4");
	        m_KnR.setBorder(bdr0);
		add(m_LTC_b  = new QRadioButton(this, rbG_show2nd,  null, App.show2ndMetric == App.Metric_LTC_Bas, "", Aaf.gT("showTab.LTC")), "gapx 4");    
	        m_LTC_b.setBorder(bdr0);
		add(m_LTC_r  = new QRadioButton(this, rbG_show2nd,  null, App.show2ndMetric == App.Metric_LTC_Ref, "", Aaf.gT("showTab.LTC+")), "gapx 4");    
	        m_LTC_r.setBorder(bdr0);
		add(m_Banzai = new QRadioButton(this, rbG_show2nd, null, App.show2ndMetric == App.Metric_Banzai, "", Aaf.gT("showTab.banzai")), "gapx 4");    
	        m_Banzai.setBorder(bdr0);

		add(anyLabel  = new QLabel(Aaf.gT("showTab.wSens")), "gapy 20");
		    anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(anyLabel  = new QLabel(Aaf.gT("showTab.high") + " . . . . . . . . . . . . . . . " + Aaf.gT("showTab.low")), "gapx4");
		    anyLabel.setForeground(Aaa.optionsTitleGreen);
		    
		add(size0  = new QRadioButton(this, rbGroupSig,  null, App.mouseWheelSensitivity == 0,  "",  ""), "gapx4, split9, flowx");
		add(size1  = new QRadioButton(this, rbGroupSig,  null, App.mouseWheelSensitivity == 1,  "",  ""));
		add(size2  = new QRadioButton(this, rbGroupSig,  null, App.mouseWheelSensitivity == 2,  "",  ""));
		add(size3  = new QRadioButton(this, rbGroupSig,  null, App.mouseWheelSensitivity == 3,  "",  ""));
		add(size4  = new QRadioButton(this, rbGroupSig,  null, App.mouseWheelSensitivity == 4,  "",  ""));
		add(size5  = new QRadioButton(this, rbGroupSig,  null, App.mouseWheelSensitivity == 5,  "",  ""));
		add(size6  = new QRadioButton(this, rbGroupSig,  null, App.mouseWheelSensitivity == 6,  "",  ""));
		add(size7  = new QRadioButton(this, rbGroupSig,  null, App.mouseWheelSensitivity == 7,  "",  ""));
		add(size8  = new QRadioButton(this, rbGroupSig,  null, App.mouseWheelSensitivity == 8,  "",  ""));
		if (App.onMac == false) {
			Border bdr1 = BorderFactory.createEmptyBorder(0, 1, 0, 0);
			size0.setBorder(bdr1);
			size1.setBorder(bdr1);
			size2.setBorder(bdr1);
			size3.setBorder(bdr1);
			size4.setBorder(bdr1);
			size5.setBorder(bdr1);
			size6.setBorder(bdr1);
			size7.setBorder(bdr1);
			size8.setBorder(bdr1);
		}

		add(mouseWheelInverted = new QCheckBox(this, App.mouseWheelInverted,    Aaf.gT("showTab.invert")), "gapy4");
		if (App.onMac == false)
		    mouseWheelInverted.setBorder(bdr4);
		    
		add(extraDepthOn3and4 = new QCheckBox(this, App.extraDepthOn3and4,    Aaf.gT("showTab.extraDepth")), "gapy24");
		if (App.onMac == false)
			extraDepthOn3and4.setBorder(bdr4);
		    
		add(anyLabel  = new QLabel(Aaf.gT("showTab.linux")), "gapy 20");
		    anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(fixLinuxLineSep     = new QCheckBox(this, App.fixLinuxLineSep,    Aaf.gT("showTab.toClose")));
		   fixLinuxLineSep.setBorder(bdr4);
		   fixLinuxLineSep.setEnabled(App.onLinux);

		add(applyDefaults = new QButton(this, Aaf.gT("cmnTab.applyDef")), "gapy20, gapx4");
		if (App.onMac == false)
			applyDefaults.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 4));

		add(anyLabel  = new QLabel(Aaf.gT("showTab.belowReset")), "gapy 55");
	        anyLabel.setForeground(Cc.RedStrong);
		add(resetAllPrefs = new QButton(this, Aaf.gT("showTab.resetAll")), "gapx10");
		    resetAllPrefs.setForeground(Cc.RedStrong);
		if (App.onMac == false)
		    resetAllPrefs.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 4));
		add(anyLabel  = new QLabel(Aaf.gT("showTab.takeCare")));
            anyLabel.setForeground(Cc.RedStrong);		 			    
		// @formatter:on
	}

	public void actionPerformed(ActionEvent e) {

		Object source = e.getSource();

		if (source == applyDefaults) {
			applyDefaults();
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

	void applyDefaults() {

		App.showSaveBtns = true;
		App.showShfWkPlBtn = true;
		App.showRotationBtns = true;
		App.showClaimBtn = false;
		App.showEdHandsSuitSwap = false;

		App.showB1stBtn = false;
		App.showContBtn = false;

		App.showHCPs = true;
		App.show2ndMetric = App.Metric_None;
		App.mouseWheelSensitivity = (App.onMac ? 4 : 0);
		App.fixLinuxLineSep = App.onLinux;

		showSaveBtns.setSelected(App.showSaveBtns);
		showShfWkPlBtn.setSelected(App.showShfWkPlBtn);
		showRotationBtns.setSelected(App.showRotationBtns);
		showClaimBtn.setSelected(App.showClaimBtn);
		showEdHandsSuitSwap.setSelected(App.showEdHandsSuitSwap);

		m_None.setSelected(true);
		m_KnR.setSelected(false);
		m_LTC_b.setSelected(false);
		m_LTC_r.setSelected(false);
		m_Banzai.setSelected(false);

		showB1stBtn.setSelected(App.showB1stBtn);
		showContBtn.setSelected(App.showContBtn);

		showHCPs.setSelected(App.showHCPs);

		size0.setSelected(App.mouseWheelSensitivity == 0);
		size1.setSelected(App.mouseWheelSensitivity == 1);
		size2.setSelected(App.mouseWheelSensitivity == 2);
		size3.setSelected(App.mouseWheelSensitivity == 3);
		size4.setSelected(App.mouseWheelSensitivity == 4);
		size5.setSelected(App.mouseWheelSensitivity == 5);
		size6.setSelected(App.mouseWheelSensitivity == 6);
		size7.setSelected(App.mouseWheelSensitivity == 7);
		size8.setSelected(App.mouseWheelSensitivity == 8);

		fixLinuxLineSep.setSelected(App.fixLinuxLineSep);
	}

	/** This listens for the check box changed event */
	public void itemStateChanged(ItemEvent e) {

		boolean b = (e.getStateChange() == ItemEvent.SELECTED);
		Object source = e.getItemSelectable();

		// @formatter:off
		if (source == showHCPs) {
			App.showHCPs = b;
		}
		else if (source == m_None) {
	        App.show2ndMetric = App.Metric_None;
		}
		else if (source == m_KnR) {
	        App.show2ndMetric = App.Metric_KnR;
		}
		else if (source == m_LTC_b) {
	        App.show2ndMetric = App.Metric_LTC_Bas;
		}
		else if (source == m_LTC_r) {
	        App.show2ndMetric = App.Metric_LTC_Ref;
		}
		else if (source == m_Banzai) {
	        App.show2ndMetric = App.Metric_Banzai;
		}
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
		else if (source == showEdHandsSuitSwap) {
                       App.showEdHandsSuitSwap = b;
             App.calcApplyBarVisiblity();
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
		else if (source == mouseWheelInverted) {
                       App.mouseWheelInverted = b;
		}
		else if (source == extraDepthOn3and4) {
                       App.extraDepthOn3and4 = b;
		}

		else if (source == size0) {
			App.mouseWheelSensitivity = 0;  // low is MORE sinsitive
		}
		else if (source == size1) {
			App.mouseWheelSensitivity = 1;
		}
		else if (source == size2) {
			App.mouseWheelSensitivity = 2;
		}
		else if (source == size3) {
			App.mouseWheelSensitivity = 3;
		}
		else if (source == size4) {
			App.mouseWheelSensitivity = 4;
		}
		else if (source == size5) {
			App.mouseWheelSensitivity = 5;
		}
		else if (source == size6) {
			App.mouseWheelSensitivity = 6;
		}
		else if (source == size7) {
			App.mouseWheelSensitivity = 7;
		}
		else if (source == size8) {
			App.mouseWheelSensitivity = 8;
		}
		
		if (b == false) {
			; // do nothing
		}

		if (App.allConstructionComplete) {
			App.savePreferences();
			App.calcCompassPhyOffset();
			App.gbp.dealDirectionChange();
			App.calcApplyBarVisiblity();
			App.frame.invalidate();
			App.frame.repaint();
		}
		// @formatter:on

	}
}
