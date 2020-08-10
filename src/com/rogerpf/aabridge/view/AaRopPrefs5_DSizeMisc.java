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
class AaRopPrefs5_DSizeMisc extends ClickPanel implements ItemListener, ActionListener {

	private static final long serialVersionUID = 1L;

	QLabel anyLabel;

	ButtonGroup rbGroupSig = new ButtonGroup();
	QRadioButton size0;
	QRadioButton size1;
	QRadioButton size2;
	QRadioButton size3;
	QRadioButton size4;

	ButtonGroup dGroupSig = new ButtonGroup();
	QRadioButton dSize0;
	QRadioButton dSize1;
	QRadioButton dSize2;
	QRadioButton dSize3;
	QRadioButton dSize4;
	QRadioButton dSize5;
	QRadioButton dSize6;
	QRadioButton dSize7;
	QRadioButton dSize8;

	QCheckBox mouseWheelInverted;

	ButtonGroup vGroupSig = new ButtonGroup();
	QRadioButton vSize1;
	QRadioButton vSize2;
	QRadioButton vSize3;

	QCheckBox showDdsOnPlayedCards;
	QCheckBox randomMnHeaderColor;
	QCheckBox decodeWith1252;
//	QCheckBox fixLinuxLineSep;
	QCheckBox showAllLangLin;

	QButton applyDefaults;

	public AaRopPrefs5_DSizeMisc() {
		setOpaque(true); // remember - we derive from ClickPanel which is setOpaque(false)
//		setBackground(SystemColor.control);

		Border bdr4 = BorderFactory.createEmptyBorder(1, 4, 1, 4);

		setLayout(new MigLayout(App.simple + ", flowy"));

		// @formatter:off
		add(anyLabel  = new QLabel(Aaf.gT("menuOpt.size")), "gapx 5, gapy 5");
		anyLabel.setForeground(Aaa.optionsTitleGreen);
		Font slightlyBiggerFont = anyLabel.getFont().deriveFont(anyLabel.getFont().getSize() * 1.09f);
	    anyLabel.setFont(slightlyBiggerFont);
		

		add(anyLabel = new QLabel(Aaf.gT("sizeFontTab.dealArea")), "gapy 15");
		anyLabel.setForeground(Aaa.optionsTitleGreen);
		Border bdr1 = BorderFactory.createEmptyBorder(1, 0, 1, 0);
		
		add(size0  = new QRadioButton(this, rbGroupSig,  bdr1, App.tutorialDealSize == 0,  "Size 0",  Aaf.gT("sizeFontTab.tiny")), "gapx 9");
		add(size1  = new QRadioButton(this, rbGroupSig,  bdr1, App.tutorialDealSize == 1,  "Size 1",  Aaf.gT("sizeFontTab.small")), "gapx 9");
		add(size2  = new QRadioButton(this, rbGroupSig,  bdr1, App.tutorialDealSize == 2,  "Size 2",  Aaf.gT("sizeFontTab.standard")), "gapx 9");
		add(size3  = new QRadioButton(this, rbGroupSig,  bdr1, App.tutorialDealSize == 3,  "Size 3",  Aaf.gT("sizeFontTab.medium")), "gapx 9");
		add(size4  = new QRadioButton(this, rbGroupSig,  bdr1, App.tutorialDealSize == 4,  "Size 4",  Aaf.gT("sizeFontTab.large")), "gapx 9");
	
		add(anyLabel  = new QLabel(Aaf.gT("showTab.wSens")), "gapy 20");
	    anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(anyLabel  = new QLabel(Aaf.gT("showTab.high") + " . . . . . . . . . . . . . . . . .  " + Aaf.gT("showTab.low")), "gapx4");
		    anyLabel.setForeground(Aaa.optionsTitleGreen);
		    
		add(dSize0  = new QRadioButton(this, dGroupSig,  null, App.mouseWheelSensitivity == 0,  "",  ""), "gapx4, split9, flowx");
		add(dSize1  = new QRadioButton(this, dGroupSig,  null, App.mouseWheelSensitivity == 1,  "",  ""));
		add(dSize2  = new QRadioButton(this, dGroupSig,  null, App.mouseWheelSensitivity == 2,  "",  ""));
		add(dSize3  = new QRadioButton(this, dGroupSig,  null, App.mouseWheelSensitivity == 3,  "",  ""));
		add(dSize4  = new QRadioButton(this, dGroupSig,  null, App.mouseWheelSensitivity == 4,  "",  ""));
		add(dSize5  = new QRadioButton(this, dGroupSig,  null, App.mouseWheelSensitivity == 5,  "",  ""));
		add(dSize6  = new QRadioButton(this, dGroupSig,  null, App.mouseWheelSensitivity == 6,  "",  ""));
		add(dSize7  = new QRadioButton(this, dGroupSig,  null, App.mouseWheelSensitivity == 7,  "",  ""));
		add(dSize8  = new QRadioButton(this, dGroupSig,  null, App.mouseWheelSensitivity == 8,  "",  ""));
		if (App.onMac == false) {
			Border bdr6 = BorderFactory.createEmptyBorder(0, 1, 0, 0);
			dSize0.setBorder(bdr6);
			dSize1.setBorder(bdr6);
			dSize2.setBorder(bdr6);
			dSize3.setBorder(bdr6);
			dSize4.setBorder(bdr6);
			dSize5.setBorder(bdr6);
			dSize6.setBorder(bdr6);
			dSize7.setBorder(bdr6);
			dSize8.setBorder(bdr6);
		}
	
		add(mouseWheelInverted = new QCheckBox(this, App.mouseWheelInverted,    Aaf.gT("showTab.invert")), "gapy 5");
		if (App.onMac == false) {
		    mouseWheelInverted.setBorder(bdr4);
		}
		
		
		add(anyLabel = new QLabel(Aaf.gT("sizeFontTab.ddsonplayed_TT")), "gapy 15");
    	anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(showDdsOnPlayedCards = new QCheckBox(this, App.showDdsOnPlayedCards, Aaf.gT("sizeFontTab.ddsonplayed")), "gapx 5");
		if (App.onMac == false)
			showDdsOnPlayedCards.setBorder(bdr1);
		

    	add(anyLabel  = new QLabel(Aaf.gT("sizeFontTab.header0")), "gapy 15");
    	anyLabel.setForeground(Aaa.optionsTitleGreen);
    	add(randomMnHeaderColor = new QCheckBox(this, App.randomMnHeaderColor, Aaf.gT("sizeFontTab.header1")), "gapx 5");
    	   randomMnHeaderColor.setToolTipText(Aaf.gT("sizeFontTab.header1"));
    	if (App.onMac == false)
			randomMnHeaderColor.setBorder(bdr1);
		
			
		add(anyLabel  = new QLabel(Aaf.gT("sizeFontTab.decodeCp0")), "gapy 15");
    	anyLabel.setForeground(Aaa.optionsTitleGreen);
    	add(decodeWith1252 = new QCheckBox(this, App.decodeWith1252, Aaf.gT("sizeFontTab.decodeCp1")), "gapx 5");
		if (App.onMac == false)
			decodeWith1252.setBorder(bdr1);

        
//		add(anyLabel  = new QLabel(Aaf.gT("showTab.linux")), "gapy 20");
//	    anyLabel.setForeground(Aaa.optionsTitleGreen);
//	    add(fixLinuxLineSep     = new QCheckBox(this, App.fixLinuxLineSep,    Aaf.gT("showTab.toClose")));
//	        fixLinuxLineSep.setBorder(bdr4);
//	        fixLinuxLineSep.setEnabled(App.onLinux);

		add(anyLabel  = new QLabel(Aaf.gT("showTab.bboDelay_TT")), "gapy 20");
		anyLabel.setToolTipText(Aaf.gT("showTab.bboDelay_TT"));
	    anyLabel.setForeground(Aaa.optionsTitleGreen);
		    
		add(vSize1  = new QRadioButton(this, vGroupSig,  null, App.seconds_between_bbo_call_attempts == 1,  "",  "1"), "gapx0, split3, flowx");
		add(vSize2  = new QRadioButton(this, vGroupSig,  null, App.seconds_between_bbo_call_attempts == 2,  "",  "2"));
		add(vSize3  = new QRadioButton(this, vGroupSig,  null, App.seconds_between_bbo_call_attempts == 3,  "",  "3"));
		if (App.onMac == false) {
			Border bdr7 = BorderFactory.createEmptyBorder(0, 10, 0, 0);
			vSize1.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
			vSize2.setBorder(bdr7);
			vSize3.setBorder(bdr7);
		}
		
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

			App.mouseWheelSensitivity = (App.onMac ? 4 : 0);

			dSize0.setSelected(App.mouseWheelSensitivity == 0);
			dSize1.setSelected(App.mouseWheelSensitivity == 1);
			dSize2.setSelected(App.mouseWheelSensitivity == 2);
			dSize3.setSelected(App.mouseWheelSensitivity == 3);
			dSize4.setSelected(App.mouseWheelSensitivity == 4);
			dSize5.setSelected(App.mouseWheelSensitivity == 5);
			dSize6.setSelected(App.mouseWheelSensitivity == 6);
			dSize7.setSelected(App.mouseWheelSensitivity == 7);
			dSize8.setSelected(App.mouseWheelSensitivity == 8);

			App.mouseWheelInverted = false;

			vSize1.setSelected(App.seconds_between_bbo_call_attempts == 1);
			vSize2.setSelected(App.seconds_between_bbo_call_attempts == 2);
			vSize3.setSelected(App.seconds_between_bbo_call_attempts == 3);

			App.showDdsOnPlayedCards = true;
			showDdsOnPlayedCards.setSelected(App.showDdsOnPlayedCards);

			App.randomMnHeaderColor = true;
			randomMnHeaderColor.setSelected(App.randomMnHeaderColor);

			App.decodeWith1252 = true;
			showAllLangLin.setSelected(App.decodeWith1252 == true);

			App.showAllLangLin = false;
			showAllLangLin.setSelected(App.showAllLangLin == false);

//			App.fixLinuxLineSep = App.onLinux;
//			fixLinuxLineSep.setSelected(App.fixLinuxLineSep);

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

		if (source == showDdsOnPlayedCards) {
			App.showDdsOnPlayedCards = b;
		}
		
		if (source == randomMnHeaderColor) {
			App.randomMnHeaderColor = b;
		}
		
		else if (source == decodeWith1252) {
			App.decodeWith1252 = !b;			
		}		
		
		else if (source == showAllLangLin) {
			App.showAllLangLin = !b;			
			App.exitAndRelaunch(); // never returns
		}		
		
		else if (source == mouseWheelInverted) {
            App.mouseWheelInverted = b;
		}
		
//		else if (source == fixLinuxLineSep) {
//            App.fixLinuxLineSep = b;
//		}

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
		
		else if (source == dSize0) {
			App.mouseWheelSensitivity = 0;  // low is MORE sensitive
		}
		else if (source == dSize1) {
			App.mouseWheelSensitivity = 1;
		}
		else if (source == dSize2) {
			App.mouseWheelSensitivity = 2;
		}
		else if (source == dSize3) {
			App.mouseWheelSensitivity = 3;
		}
		else if (source == dSize4) {
			App.mouseWheelSensitivity = 4;
		}
		else if (source == dSize5) {
			App.mouseWheelSensitivity = 5;
		}
		else if (source == dSize6) {
			App.mouseWheelSensitivity = 6;
		}
		else if (source == dSize7) {
			App.mouseWheelSensitivity = 7;
		}
		else if (source == dSize8) {
			App.mouseWheelSensitivity = 8;
		}
		
		else if (source == vSize1) {
			App.seconds_between_bbo_call_attempts = 1;  
		}
		else if (source == vSize2) {
			App.seconds_between_bbo_call_attempts = 2;
		}
		else if (source == vSize3) {
			App.seconds_between_bbo_call_attempts = 3;
		}


		if (App.allConstructionComplete) {
			App.savePreferences();
			// App.calcCompassPhyOffset();
			// App.gbp.dealDirectionChange();
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
