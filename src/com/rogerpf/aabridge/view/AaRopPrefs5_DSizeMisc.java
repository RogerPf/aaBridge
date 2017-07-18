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
class AaRopPrefs5_DSizeMisc extends ClickPanel implements ItemListener, ActionListener {

	private static final long serialVersionUID = 1L;

	QLabel anyLabel;

	ButtonGroup osfGrp = new ButtonGroup();
	QRadioButton useStdFont;
	QRadioButton useFamilyOverride;
	AaFontChooser fontChooser;

	ButtonGroup rbGroupSig = new ButtonGroup();
	QRadioButton size0;
	QRadioButton size1;
	QRadioButton size2;
	QRadioButton size3;
	QRadioButton size4;

	QButton applyDefaults;

	QCheckBox respectVrCmd;
	QCheckBox useOsStdEOL;
	QCheckBox showAllLangLin;
	QCheckBox randomMnHeaderColor;

	public AaRopPrefs5_DSizeMisc() {
		setOpaque(true); // remember - we derive from ClickPanel which is setOpaque(false)
//		setBackground(SystemColor.control);

		setLayout(new MigLayout(App.simple + ", flowy"));

		// @formatter:off
		add(anyLabel  = new QLabel(Aaf.gT("menuOpt.size")), "gapy 5");
		anyLabel.setForeground(Aaa.optionsTitleGreen);
		

		add(anyLabel = new QLabel(Aaf.gT("sizeFontTab.dealArea")), "gapy 20");
		anyLabel.setForeground(Aaa.optionsTitleGreen);
		Border bdr1 = BorderFactory.createEmptyBorder(1, 0, 1, 0);
		
		add(size0  = new QRadioButton(this, rbGroupSig,  bdr1, App.tutorialDealSize == 0,  "Size 0",  Aaf.gT("sizeFontTab.tiny")), "gapx 9");
		add(size1  = new QRadioButton(this, rbGroupSig,  bdr1, App.tutorialDealSize == 1,  "Size 1",  Aaf.gT("sizeFontTab.small")), "gapx 9");
		add(size2  = new QRadioButton(this, rbGroupSig,  bdr1, App.tutorialDealSize == 2,  "Size 2",  Aaf.gT("sizeFontTab.standard")), "gapx 9");
		add(size3  = new QRadioButton(this, rbGroupSig,  bdr1, App.tutorialDealSize == 3,  "Size 3",  Aaf.gT("sizeFontTab.medium")), "gapx 9");
		add(size4  = new QRadioButton(this, rbGroupSig,  bdr1, App.tutorialDealSize == 4,  "Size 4",  Aaf.gT("sizeFontTab.large")), "gapx 9");

		Border bdr42 = BorderFactory.createEmptyBorder(0, 4, 1, 2);

		add(anyLabel = new QLabel(Aaf.gT("sizeFontTab.fontOverride")), "gapy 20");
		anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(useStdFont         = new QRadioButton(this, osfGrp,  bdr42, App.useFamilyOverride == false,  "useStdFont",  Aaf.gT("sizeFontTab.stdFont")), "gapx 7");
		add(useFamilyOverride  = new QRadioButton(this, osfGrp,  bdr42, App.useFamilyOverride == true,   "useFamilyOverride",  Aaf.gT("sizeFontTab.override")), "gapx 7, split 2, flowx");
		useFamilyOverride.setBorder(BorderFactory.createEmptyBorder(0, 4, 1, 2));
		add(anyLabel = new QLabel(Aaf.gT("sizeFontTab.notRec")), "flowy, gapx 8");
		anyLabel.setForeground(Cc.RedStrong);
		add(fontChooser = new AaFontChooser(), "gapx 3, width 150!");
		fontChooser.selectFamilyIfPresent(App.fontfamilyOverride);
		fontChooser.setMaximumRowCount(25);
		
	        	
		add(anyLabel  = new QLabel(Aaf.gT("sizeFontTab.vr1")), "gapy 30");
	    anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(respectVrCmd = new QCheckBox(this, App.respectVrCmd, Aaf.gT("sizeFontTab.vr2")), "gapx 5");
		if (App.onMac == false)
			respectVrCmd.setBorder(bdr1);

		add(anyLabel  = new QLabel(Aaf.gT("sizeFontTab.eol")), "gapy 15");
	    anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(useOsStdEOL = new QCheckBox(this, App.useOsStdEOL, Aaf.gT("sizeFontTab.std1") + "            " +  Aaf.gT("sizeFontTab.std2")), "gapx 5");
		if (App.onMac == false)
			useOsStdEOL.setBorder(bdr1);

    	add(anyLabel  = new QLabel(Aaf.gT("sizeFontTab.header0")), "gapy 15");
    	anyLabel.setForeground(Aaa.optionsTitleGreen);
    	add(randomMnHeaderColor = new QCheckBox(this, App.randomMnHeaderColor, Aaf.gT("sizeFontTab.header1")), "gapx 5");
		if (App.onMac == false)
			randomMnHeaderColor.setBorder(bdr1);
        
		add(anyLabel  = new QLabel(Aaf.gT("sizeFontTab.linLangVis0")), "gapy 15");
    	anyLabel.setForeground(Cc.RedStrong);
    	add(showAllLangLin = new QCheckBox(this, App.showAllLangLin == false, Aaf.gT("sizeFontTab.linLangVis1")), "gapx 5");
		if (App.onMac == false)
			showAllLangLin.setBorder(bdr1);

        
	    add(applyDefaults = new QButton(this, Aaf.gT("cmnTab.applyDef")), "gapy35, gapx4");
		if (App.onMac == false)
		    applyDefaults.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 4));

		// @formatter:on
	}

	public void actionPerformed(ActionEvent e) {

		Object source = e.getSource();

		if (source == applyDefaults) {

			App.tutorialDealSize = 4;
			size0.setSelected(App.tutorialDealSize == 0);
			size1.setSelected(App.tutorialDealSize == 1);
			size2.setSelected(App.tutorialDealSize == 2);
			size3.setSelected(App.tutorialDealSize == 3);
			size4.setSelected(App.tutorialDealSize == 4);

			App.useFamilyOverride = false;
			useStdFont.setSelected(App.useFamilyOverride == false);
			useFamilyOverride.setSelected(App.useFamilyOverride);

			App.fontfamilyOverride = "Times Roman";
			fontChooser.selectFamilyIfPresent(App.fontfamilyOverride);

			App.respectVrCmd = true;
			respectVrCmd.setSelected(App.respectVrCmd);

			App.useOsStdEOL = true;
			useOsStdEOL.setSelected(App.useOsStdEOL);

			App.randomMnHeaderColor = true;
			randomMnHeaderColor.setSelected(App.randomMnHeaderColor);

			App.showAllLangLin = false;
			showAllLangLin.setSelected(App.showAllLangLin == false);

			App.savePreferences();

			App.frame.repaint();
		}
	}

	/** This listens for the check box changed event */
	public void itemStateChanged(ItemEvent e) {

		boolean shaker = false;

		boolean b = (e.getStateChange() == ItemEvent.SELECTED);
		Object source = e.getItemSelectable();

		// @formatter:off

		if (source == respectVrCmd) {
			App.respectVrCmd = b;
		}
		
		else if (source == useOsStdEOL) {
			App.useOsStdEOL = b;
		}
		
		else if (source == randomMnHeaderColor) {
			App.randomMnHeaderColor = b;
		}
		
		else if (source == showAllLangLin) {
			App.showAllLangLin = !b;			
			App.exitAndRelaunch(); // never returns
		}		
		
		else if (b == false) {
			; // do nothing
		}
		
		else if (source == size0) {
			App.tutorialDealSize = 0;
			shaker = true;
		}
		else if (source == size1) {
			App.tutorialDealSize = 1;
			shaker = true;
		}
		else if (source == size2) {
			App.tutorialDealSize = 2;
			shaker = true;
		}
		else if (source == size3) {
			App.tutorialDealSize = 3;
			shaker = true;
		}
		else if (source == size4) {
			App.tutorialDealSize = 4;
			shaker = true;
		}

		else if (source == useStdFont) {
			App.useFamilyOverride = false;
		}
		else if (source == useFamilyOverride) {
			App.useFamilyOverride = true;
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
