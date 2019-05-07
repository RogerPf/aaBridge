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

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.border.Border;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.Aaf;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.model.Cc;

import net.miginfocom.swing.MigLayout;

/**   
 */
class AaRopPrefs7_ShowBtns extends ClickPanel implements ItemListener, ActionListener {

	private static final long serialVersionUID = 1L;

	QLabel anyLabel;

	QCheckBox showClaimBtn;

	QCheckBox showHCPs;

	ButtonGroup rbG_show2nd = new ButtonGroup();
	QRadioButton m_None;
	QRadioButton m_KnR;
	QRadioButton m_LTC_b;
	QRadioButton m_LTC_r;
	QRadioButton m_Banzai;

	QButton applyDefaults;

	QButton resetAllPrefs;

	public AaRopPrefs7_ShowBtns() {
		setOpaque(true); // remember - we derive from ClickPanel which is setOpaque(false)
//		setBackground(SystemColor.control);

		setLayout(new MigLayout(App.simple + ", flowy"));

		Border bdr4 = BorderFactory.createEmptyBorder(1, 4, 1, 4);
		Border bdr0 = BorderFactory.createEmptyBorder(0, 0, 0, 0);

		// @formatter:off
		add(anyLabel  = new QLabel(Aaf.menuOpt_show_D), "gapx 5, gapy 5");
		anyLabel.setForeground(Aaa.optionsTitleGreen);
		Font slightlyBiggerFont = anyLabel.getFont().deriveFont(anyLabel.getFont().getSize() * 1.09f);
	    anyLabel.setFont(slightlyBiggerFont);

		add(anyLabel = new QLabel(Aaf.gT("showTab.buttons")), "gapy 10");
		anyLabel.setForeground(Aaa.optionsTitleGreen);
		
		add(showClaimBtn        = new QCheckBox(this, App.showClaimBtn,       Aaf.gT("showTab.claim")));
	    showClaimBtn.setBorder(bdr4);
	    
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

		    		    
		add(applyDefaults = new QButton(this, Aaf.gT("cmnTab.applyDef")), "gapy40, gapx4");
		if (App.onMac == false)
			applyDefaults.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 4));

		add(anyLabel  = new QLabel("Java Version"), "gapy40, gapx 10");
		add(anyLabel  = new QLabel(App.java_info), "gapx 4");
		

		add(anyLabel  = new QLabel(Aaf.gT("showTab.belowReset")), "gapy 20");
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

		App.showClaimBtn = false;

		App.showHCPs = true;
		App.show2ndMetric = App.Metric_None;

		showClaimBtn.setSelected(App.showClaimBtn);

		showHCPs.setSelected(App.showHCPs);

		m_None.setSelected(true);
		m_KnR.setSelected(false);
		m_LTC_b.setSelected(false);
		m_LTC_r.setSelected(false);
		m_Banzai.setSelected(false);
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
			App.calcApplyBarVisiblity();
			App.frame.invalidate();
			App.frame.repaint();
		}
		// @formatter:on

	}
}
