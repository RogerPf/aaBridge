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
import com.rogerpf.aabridge.model.Zzz;

import net.miginfocom.swing.MigLayout;

/**   
 */
class AaRopPrefs1_AutoPlay extends ClickPanel implements ItemListener, ActionListener {

	private static final long serialVersionUID = 1L;

	QLabel anyLabel;

	QCheckBox useDDSwhenAvaialble_autoplay;

	ButtonGroup rbGroupSig = new ButtonGroup();
	QRadioButton noSignal;
	QRadioButton stdEvenCount;
	QRadioButton udcOddCount;

	QCheckBox yourFinessesMostlyFail;

	QCheckBox youPlayerEotWait;
	QCheckBox youAutoSingletons;
	QCheckBox youAutoAdjacent;

	QCheckBox youAutoplayAlways;
	QCheckBox youAutoplayPause;
	QCheckBox youAutoplayFAST;

	QCheckBox fillHandDisplay;

	QButton applyDefaults;

	QLabel testingText;

	public AaRopPrefs1_AutoPlay() {
		setOpaque(true); // remember - we derive from ClickPanel which is setOpaque(false)
//		setBackground(SystemColor.control);

		Border bdr4 = BorderFactory.createEmptyBorder(0, 4, 0, 4);
		Border bdr2 = BorderFactory.createEmptyBorder(1, 4, 1, 4);

		setLayout(new MigLayout(App.simple + ", flowy"));

		String rbInset = "gapx7";
		String cbInset = "gapx16";

		// @formatter:off
		
		add(anyLabel = new QLabel(Aaf.gT("menuOpt.autoPlay")), "gapx 5, gapy 5");
	    anyLabel.setForeground(Aaa.optionsTitleGreen);
		Font slightlyBiggerFont = anyLabel.getFont().deriveFont(anyLabel.getFont().getSize() * 1.09f);
	    anyLabel.setFont(slightlyBiggerFont);

		add(anyLabel = new QLabel(Aaf.gT("autoPlayTab.dds")),  "gapy 5");
	    anyLabel.setForeground(Aaa.optionsTitleGreen);

		if (App.haglundsDDSavailable) {
			add(anyLabel = new QLabel(Aaf.gT("autoPlayTab.avail")), "gapx 10");
		    anyLabel.setForeground(Aaa.optionsTitleGreen);
		}
		else {
			add(anyLabel  = new QLabel(Aaf.gT("autoPlayTab.not")), "gapx 10");
		    anyLabel.setForeground(Cc.RedStrong);			
		}
		add(useDDSwhenAvaialble_autoplay = new QCheckBox(this, App.useDDSwhenAvaialble_autoplay, Aaf.gT("autoPlayTab.use")), rbInset);
		    useDDSwhenAvaialble_autoplay.setBorder(bdr4);
		    useDDSwhenAvaialble_autoplay.setEnabled(App.haglundsDDSavailable);		
	
		add(anyLabel    = new QLabel(Aaf.gT("autoPlayTab.defSig")), "gapy 10");
		    anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(noSignal    = new QRadioButton(this, rbGroupSig,  bdr4, App.defenderSignals == Zzz.NoSignals,     "NoSignal",     Aaf.gT("autoPlayTab.none")), rbInset);
		add(stdEvenCount= new QRadioButton(this, rbGroupSig,  bdr4, App.defenderSignals == Zzz.StdEvenCount,  "StdEvenCount", Aaf.gT("autoPlayTab.std")), rbInset);
		add(udcOddCount = new QRadioButton(this, rbGroupSig,  bdr4, App.defenderSignals == Zzz.UdcOddCount,   "UdcOddCount",  Aaf.gT("autoPlayTab.udc")), rbInset);

		add(anyLabel  = new QLabel(Aaf.gT("autoPlayTab.finFail1")), "gapy 12");
		   anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(yourFinessesMostlyFail= new QCheckBox(this, App.yourFinessesMostlyFail, Aaf.gT("autoPlayTab.finFail2")), rbInset);
		    yourFinessesMostlyFail.setBorder(bdr4);

		
		add(anyLabel  = new QLabel(Aaf.gT("autoPlayTab.eot")), "gapy 12");
		    anyLabel.setForeground(Aaa.optionsTitleGreen);

		add(youPlayerEotWait     = new QCheckBox(this, App.youPlayerEotWait,  Aaf.gT("autoPlayTab.wait")), rbInset);
		    youPlayerEotWait.setBorder(bdr2);
		add(youAutoSingletons    = new QCheckBox(this, App.youAutoSingletons, Aaf.gT("autoPlayTab.sing")), rbInset);
		    youAutoSingletons.setBorder(bdr2);
		add(youAutoAdjacent      = new QCheckBox(this, App.youAutoAdjacent,   Aaf.gT("autoPlayTab.equal")), cbInset);
		    youAutoAdjacent.setBorder(bdr2);
		
		add(testingText = new QLabel(Aaf.gT("autoPlayTab.testing")), "gapy20");
		testingText.setForeground(Aaa.optionsTitleGreen);

		add(youAutoplayAlways    = new QCheckBox(this, App.youAutoplayAlways, Aaf.gT("autoPlayTab.always")), rbInset);
		    youAutoplayAlways.setBorder(bdr2);

		add(youAutoplayPause     = new QCheckBox(this, !App.youAutoplayPause, Aaf.gT("autoPlayTab.noPause")), cbInset);
		    youAutoplayPause.setBorder(bdr2);

		add(youAutoplayFAST      = new QCheckBox(this, App.youAutoplayFAST,   Aaf.gT("autoPlayTab.fast")), cbInset);
			youAutoplayFAST.setBorder(bdr2);

		if (App.devMode) {
			add(anyLabel = new QLabel(" DevMode Settings  "), "gapy20");
			    anyLabel.setForeground(Aaa.optionsTitleGreen);
			add(fillHandDisplay   = new QCheckBox(this, App.fillHandDisplay,  "Fill the hands with cards   -   seen in devmode ONLY  "), rbInset);
			    fillHandDisplay.setBorder(bdr2);
		}
		
		add(applyDefaults = new QButton(this, Aaf.gT("cmnTab.applyDef")), "gapy20, gapx4");
	    if (App.onMac == false)
	        applyDefaults.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 4));
	   		    
		// @formatter:on
	}

	public void showButtonStates() {

		boolean en = !App.isStudyDeal();

		useDDSwhenAvaialble_autoplay.setEnabled(en);

		noSignal.setEnabled(en);
		stdEvenCount.setEnabled(en);
		udcOddCount.setEnabled(en);

		yourFinessesMostlyFail.setEnabled(en);

		youPlayerEotWait.setEnabled(en);
		youAutoSingletons.setEnabled(en);
		youAutoAdjacent.setEnabled(en);

		youAutoplayAlways.setEnabled(en);
		youAutoplayPause.setEnabled(en);
		youAutoplayFAST.setEnabled(en);

		applyDefaults.setEnabled(en);
	}

	public void actionPerformed(ActionEvent e) {

		if (App.allConstructionComplete == false)
			return;

		Object source = e.getSource();

		if (source == applyDefaults) {

			applyDefaults();

			App.calcCompassPhyOffset();
			App.gbp.dealMajorChange();
		}

		if (App.allConstructionComplete) {
			App.savePreferences();

			showButtonStates();

			App.frame.repaint();
		}
	}

	public void applyDefaults() {

		App.useDDSwhenAvaialble_autoplay = true;
		useDDSwhenAvaialble_autoplay.setSelected(App.useDDSwhenAvaialble_autoplay);

		App.defenderSignals = Zzz.NoSignals;
		noSignal.setSelected(true);
		stdEvenCount.setSelected(false);
		udcOddCount.setSelected(false);

		App.yourFinessesMostlyFail = false;
		yourFinessesMostlyFail.setSelected(App.yourFinessesMostlyFail);

		App.youPlayerEotWait = true;
		App.youAutoSingletons = false;
		App.youAutoAdjacent = true;
		youPlayerEotWait.setSelected(App.youPlayerEotWait);
		youAutoSingletons.setSelected(App.youAutoSingletons);
		youAutoAdjacent.setSelected(App.youAutoAdjacent);

		App.youAutoplayAlways = false;
		App.youAutoplayPause = true;
		App.youAutoplayFAST = false;
		youAutoplayAlways.setSelected(App.youAutoplayAlways);
		youAutoplayPause.setSelected(App.youAutoplayPause);
		youAutoplayFAST.setSelected(App.youAutoplayFAST);

		App.alwaysShowHidden = false;
		App.force_N_HiddenTut = false;
		App.force_W_HiddenTut = false;
		App.force_E_HiddenTut = false;
		App.force_S_HiddenTut = false;

		App.fillHandDisplay = false;
		if (App.devMode) {
			fillHandDisplay.setSelected(App.fillHandDisplay);
		}

		showButtonStates();
	}

	/** This listens for the check box changed event */
	public void itemStateChanged(ItemEvent e) {

		boolean b = (e.getStateChange() == ItemEvent.SELECTED);
		Object source = e.getItemSelectable();

		// @formatter:off
		if (source == useDDSwhenAvaialble_autoplay) {
			App.useDDSwhenAvaialble_autoplay = b;
		}

		else if (source == yourFinessesMostlyFail) {
			App.yourFinessesMostlyFail = b;
		}

		else if (source == noSignal) {
			           App.defenderSignals = Zzz.NoSignals;
		}
		else if (source == stdEvenCount) {
			           App.defenderSignals = Zzz.StdEvenCount;
		}
		else if (source == udcOddCount) {
			           App.defenderSignals = Zzz.UdcOddCount;
		}

		else if (source == youAutoSingletons) {
			           App.youAutoSingletons = b;
		}
		else if (source == youAutoAdjacent) {
			           App.youAutoAdjacent = b;
		}

		else if (source == youAutoplayAlways) {
			           App.youAutoplayAlways = b;
			           App.implement_youAutoplayAlways();
		}
		else if (source == youAutoplayPause) {
                       App.youAutoplayPause = !b; // inverted for this display
		}
		else if (source == youAutoplayFAST) {
                       App.youAutoplayFAST = b;
		}
		else if (source == youPlayerEotWait) {
                       App.youPlayerEotWait = b;
		}
		else if (source == fillHandDisplay) {
                       App.fillHandDisplay = b;
                       App.implement_fillHandDisplay();
		}

		// @formatter:on

		if (App.allConstructionComplete) {
			App.savePreferences();

			showButtonStates();

			App.frame.repaint();
		}
	}
}
