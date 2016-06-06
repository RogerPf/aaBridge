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
class AaRopPrefs5_DSizeFont extends ClickPanel implements ItemListener, ActionListener {

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

	public AaRopPrefs5_DSizeFont() {
		setOpaque(true); // remember - we derive from ClickPanel which is setOpaque(false)
//		setBackground(SystemColor.control);

		setLayout(new MigLayout(App.simple + ", flowy"));

		// @formatter:off
		add(anyLabel  = new QLabel("  Size & Font             -   Deal Size   and   Movie Font override options"), "gapy 5");
		anyLabel.setForeground(Aaa.optionsTitleGreen);
		

		add(anyLabel = new QLabel("Deal Area Size?"), "gapy 20");
		anyLabel.setForeground(Aaa.optionsTitleGreen);
		Border bdr1 = BorderFactory.createEmptyBorder(1, 0, 1, 0);
		add(size0  = new QRadioButton(this, rbGroupSig,  bdr1, App.tutorialDealSize == 0,  "Size 0",  "Tiny"), "gapx 9");
		add(size1  = new QRadioButton(this, rbGroupSig,  bdr1, App.tutorialDealSize == 1,  "Size 1",  "Small"), "gapx 9");
		add(size2  = new QRadioButton(this, rbGroupSig,  bdr1, App.tutorialDealSize == 2,  "Size 2",  "Standard"), "gapx 9");
		add(size3  = new QRadioButton(this, rbGroupSig,  bdr1, App.tutorialDealSize == 3,  "Size 3",  "Medium"), "gapx 9");
		add(size4  = new QRadioButton(this, rbGroupSig,  bdr1, App.tutorialDealSize == 4,  "Size 4",  "Large"), "gapx 9");

		Border bdr42 = BorderFactory.createEmptyBorder(0, 4, 1, 2);

		add(anyLabel = new QLabel("Font for Bridge Movies  -  needs chapter reload to take effect"), "gapy 20");
		anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(useStdFont         = new QRadioButton(this, osfGrp,  bdr42, App.useFamilyOverride == false,  "useStdFont",  "Use the Standard Font and lin font definitions in the normal way"), "gapx 7");
		add(useFamilyOverride  = new QRadioButton(this, osfGrp,  bdr42, App.useFamilyOverride == true,   "useFamilyOverride",  "Use ONLY the Font Family below"), "gapx 7");
		useFamilyOverride.setBorder(BorderFactory.createEmptyBorder(0, 4, 1, 2));
		add(fontChooser = new AaFontChooser(), "gapx 3, width 150!");
		fontChooser.selectFamilyIfPresent(App.fontfamilyOverride);
		fontChooser.setMaximumRowCount(25);

		add(applyDefaults = new QButton(this, "Apply Defaults"), "gapy20, gapx4");
		if (App.onMac == false)
		    applyDefaults.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 4));

		// @formatter:on
	}

	public void actionPerformed(ActionEvent e) {

		Object source = e.getSource();

		if (source == applyDefaults) {

			App.tutorialDealSize = 4;
			size0.setSelected(false);
			size1.setSelected(false);
			size2.setSelected(false);
			size3.setSelected(false);
			size4.setSelected(true);

			App.useFamilyOverride = false;
			useStdFont.setSelected(true);
			useFamilyOverride.setSelected(false);

			App.fontfamilyOverride = "Times Roman";
			fontChooser.selectFamilyIfPresent(App.fontfamilyOverride);

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


		if (b == false) {
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
