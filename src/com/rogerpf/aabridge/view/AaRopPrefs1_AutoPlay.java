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
import com.rogerpf.aabridge.model.Zzz;

/**   
 */
class AaRopPrefs1_AutoPlay extends ClickPanel implements ItemListener, ActionListener {

	private static final long serialVersionUID = 1L;

	QLabel anyLabel;

	QCheckBox useDDSwhenAvaialble;

	ButtonGroup rbGroupSig = new ButtonGroup();
	QRadioButton noSignal;
	QRadioButton stdEvenCount;
	QRadioButton udcOddCount;

	QCheckBox youPlayerEotWait;
	QCheckBox youAutoSingletons;
	QCheckBox youAutoAdjacent;

	QCheckBox yourFinessesMostlyFail;

	QCheckBox youAutoplayAlways;
	QCheckBox youAutoplayPause;
	QCheckBox fillHandDisplay;
	QCheckBox runTestsAtStartUp;
	QCheckBox showTestsLogAtEnd;

	QLabel testingText;

	QButton twisterReset;
	QButton twisterLeft;
	QButton twisterRight;

	QCheckBox force_N_HiddenTut;
	QCheckBox force_W_HiddenTut;
	QCheckBox force_E_HiddenTut;
	QCheckBox force_S_HiddenTut;
	QCheckBox alwaysShowHidden;
	QButton compassClear;
	QCheckBox forceYouSeatToSouthZone;

	QButton applyDefaults;

	QCheckBox forceShowEtd;

	public AaRopPrefs1_AutoPlay() {
		setOpaque(true); // remember - we derive from ClickPanel which is setOpaque(false)
//		setBackground(SystemColor.control);

		Border bdr0 = BorderFactory.createEmptyBorder(0, 0, 0, 0);
		Border bdr4 = BorderFactory.createEmptyBorder(0, 4, 0, 4);
		Border bdr2 = BorderFactory.createEmptyBorder(1, 4, 1, 4);

		setLayout(new MigLayout(App.simple + ", flowy"));

		String rbInset = "gapx7";
		String cbInset = "gapx16";

		// @formatter:off
		
		add(anyLabel = new QLabel("  AutoPlay"), "gapy 5");
	    anyLabel.setForeground(Aaa.optionsTitleGreen);

		add(anyLabel = new QLabel("DDS - Double Dummy Solver --- google - Bo Haglund DDS"),  "gapy 5");
	    anyLabel.setForeground(Aaa.optionsTitleGreen);

		if (App.haglundsDDSavailable) {
			add(anyLabel  = new QLabel("DDS Available"), "gapx 10");
		    anyLabel.setForeground(Aaa.optionsTitleGreen);
		}
		else {
			add(anyLabel  = new QLabel("DDS not Available"), "gapx 10");
		    anyLabel.setForeground(Cc.RedStrong);			
		}
		add(useDDSwhenAvaialble = new QCheckBox(this, App.useDDSwhenAvaialble, "Use DDS when Available  -  AutoPlays will (should) be perfect"), rbInset);
		useDDSwhenAvaialble.setBorder(bdr4);
		
	
		add(anyLabel    = new QLabel("Defender Signaling  -  Defenders, when just following suit / discarding, will use a 'peter' HIGH then LOW to show ?"), "gapy 10");
		    anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(noSignal    = new QRadioButton(this, rbGroupSig,  bdr4, App.defenderSignals == Zzz.NoSignals,     "NoSignal",     "Nothing -  defenders won't signal"), rbInset);
		add(stdEvenCount= new QRadioButton(this, rbGroupSig,  bdr4, App.defenderSignals == Zzz.StdEvenCount,  "StdEvenCount", "Std   -  an EVEN number in the suit EXCEPT Trumps when shows an ODD number.     Ten and above are not used to signal  "), rbInset);
		add(udcOddCount = new QRadioButton(this, rbGroupSig,  bdr4, App.defenderSignals == Zzz.UdcOddCount,   "UdcOddCount",  "UDC  -  an ODD number in the suit including Trumps,  (UDC => Updside Down Count).     Ten and above are not used to signal  "), rbInset);

		add(anyLabel  = new QLabel("Make YOUR finesses mostly fail"), "gapy 12");
		   anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(yourFinessesMostlyFail= new QCheckBox(this, App.yourFinessesMostlyFail, "Make YOUR finesses mostly fail - so you have to think harder  (applies to declarer play only)  "), rbInset);
		    yourFinessesMostlyFail.setBorder(bdr4);

		
		add(anyLabel  = new QLabel("End of trick  and  AutoPlay options"), "gapy 12");
		    anyLabel.setForeground(Aaa.optionsTitleGreen);

		add(youPlayerEotWait     = new QCheckBox(this, App.youPlayerEotWait,  "Wait at the end of each trick  -  click *anywhere* on green to continue (gray dots indicator"), rbInset);
		    youPlayerEotWait.setBorder(bdr2);
		add(youAutoSingletons    = new QCheckBox(this, App.youAutoSingletons, "Singletons - You AutoPlay  "), rbInset);
		    youAutoSingletons.setBorder(bdr2);
		add(youAutoAdjacent      = new QCheckBox(this, App.youAutoAdjacent,   "Equal Cards - You AutoPlay  "), cbInset);
		    youAutoAdjacent.setBorder(bdr2);
		
		add(testingText = new QLabel(" TESTING - The options below are for testing   -   however  you are welcome to try them out  "), "gapy20");
		testingText.setForeground(Aaa.optionsTitleGreen);

		add(youAutoplayAlways    = new QCheckBox(this, App.youAutoplayAlways, "You AutoPlay ALWAYS  (for fun)  "), rbInset);
		    youAutoplayAlways.setBorder(bdr2);
		add(youAutoplayPause     = new QCheckBox(this, App.youAutoplayPause,  "You wait BEFORE each trick (only when 'You AutoPlay ALWAYS')  -  click anywhere to continue  "), cbInset);
		    youAutoplayPause.setBorder(bdr2);

		if (App.devMode) {
			add(anyLabel = new QLabel(" DevMode Settings  "), "gapy20");
			    anyLabel.setForeground(Aaa.optionsTitleGreen);
			add(fillHandDisplay   = new QCheckBox(this, App.fillHandDisplay,  "Fill the hands with cards   -   seen in devmode ONLY  "), rbInset);
			    fillHandDisplay.setBorder(bdr2);
//			add(runTestsAtStartUp = new QCheckBox(this, App.runTestsAtStartUp,"Run Tests - Always run the tests at start up  "),  rbInset);
//			    runTestsAtStartUp.setBorder(bdr2);
//			add(showTestsLogAtEnd = new QCheckBox(this, App.showTestsLogAtEnd,"Show Log - Show log at end of tests  "),  rbInset);
//			    showTestsLogAtEnd.setBorder(bdr2);
		}
		
		add(applyDefaults = new QButton(this, "Apply Defaults"), "gapy20, gapx4");
		    applyDefaults.setToolTipText("Reset all  Seat Options  to default values  ");
	    if (App.onMac == false)
	        applyDefaults.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 4));


		add(anyLabel       = new QLabel("Special Use"), "gapy 20");
		anyLabel.setForeground(Aaa.optionsTitleGreen);
		
		add(twisterReset = new QButton(this, "r"), "split 4, flowx");
			twisterReset.setToolTipText("Reset to  -  South as South");
			twisterReset.setForeground(Cc.BlueStrong);
			twisterReset.setBorder(bdr4); // even on a MAC  -  they will have to suffer this one "no border" button
		
		add(twisterLeft = new QButton(this, "<"), "gapx 8");
			twisterLeft.setToolTipText("< Anti-clockwise - rotate Tutorial and ALL Deal displays");
			twisterLeft.setForeground(Cc.BlueStrong);
			twisterLeft.setBorder(bdr4); // even on a MAC  -  they will have to suffer this one "no border" button
		
		add(twisterRight = new QButton(this, ">"), "gapx 8");
			twisterRight.setToolTipText("> Clockwise - rotate Tutorial and ALL Deal displays");
			twisterRight.setForeground(Cc.BlueStrong);
			twisterRight.setBorder(bdr4); // even on a MAC  -  they will have to suffer this one "no border" button
	    
		add(anyLabel = new QLabel("Universal  Rotator   -    in Deal mode  LHO, RHO & Declarer buttons  have precedence so use  Apply Defaults  first  "), "gapx 8");
		    anyLabel.setForeground(Cc.RedStrong);
	    
		add(compassClear = new QButton(this, "c"), "split 2, flowx");
		    compassClear.setToolTipText("Clear all 4 hides");
		    compassClear.setForeground(Cc.BlueStrong);
		    compassClear.setBorder(bdr4); // even on a MAC  -  they will have to suffer this one "no border" button

		add(alwaysShowHidden  = new QCheckBox(this, App.alwaysShowHidden,  "Show ALL Always  -  buttons below will override  -  (in BOTH tutorial and std mode)  "), "gapx13, gapy4");
		    alwaysShowHidden.setBorder(bdr0);
		    alwaysShowHidden.setForeground(Cc.RedStrong);

		add(force_N_HiddenTut = new QCheckBox(this, App.force_N_HiddenTut,  "N       force  Hidden  "), "gapx13, gapy4");
    		force_N_HiddenTut.setBorder(bdr2);
    		force_N_HiddenTut.setForeground(Cc.RedStrong);
   
		add(force_W_HiddenTut = new QCheckBox(this, App.force_W_HiddenTut,  ""),  "split 2, flowx");
	        force_W_HiddenTut.setBorder(bdr4);
		    force_W_HiddenTut.setForeground(Cc.RedStrong);
		   
		add(force_E_HiddenTut = new QCheckBox(this, App.force_E_HiddenTut,  " E    force  Hidden"  ));
			force_E_HiddenTut.setBorder(bdr4);
			force_E_HiddenTut.setForeground(Cc.RedStrong);
	
		add(force_S_HiddenTut = new QCheckBox(this, App.force_S_HiddenTut,  "S       force  Hidden  "), "gapx13");
			force_S_HiddenTut.setBorder(bdr2);
			force_S_HiddenTut.setForeground(Cc.RedStrong);
		    
		add(forceYouSeatToSouthZone = new QCheckBox(this, App.forceYouSeatToSouthZone,  "Force You Seat  'South'   -   When  Entering a Deal always make the 'South Zone' the You Seat  "), "gapy 4");
		    forceYouSeatToSouthZone.setBorder(bdr2);
		    forceYouSeatToSouthZone.setForeground(Cc.RedStrong);
	   		    
		add(forceShowEtd = new QCheckBox(this, App.forceShowEtd,  "Show ETD       Overrides suppression of 'Enter the Deal' visibility"), "gapy 4");
		    forceShowEtd.setBorder(bdr2);
		    forceShowEtd.setForeground(Cc.RedStrong);
	   		    
		// @formatter:on
	}

	public void showButtonStates() {
		alwaysShowHidden.setSelected(App.alwaysShowHidden);
		force_N_HiddenTut.setSelected(App.force_N_HiddenTut);
		force_W_HiddenTut.setSelected(App.force_W_HiddenTut);
		force_E_HiddenTut.setSelected(App.force_E_HiddenTut);
		force_S_HiddenTut.setSelected(App.force_S_HiddenTut);
		forceYouSeatToSouthZone.setSelected(App.forceYouSeatToSouthZone);
		forceShowEtd.setSelected(App.forceShowEtd);
	}

	public void actionPerformed(ActionEvent e) {

		if (App.allConstructionComplete == false)
			return;

		Object source = e.getSource();

		if (source == compassClear) {

			App.alwaysShowHidden = false;
			App.force_N_HiddenTut = false;
			App.force_W_HiddenTut = false;
			App.force_E_HiddenTut = false;
			App.force_S_HiddenTut = false;
			App.forceYouSeatToSouthZone = false;

		}
		else if (source == applyDefaults) {

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
			youAutoplayAlways.setSelected(App.youAutoplayAlways);
			youAutoplayPause.setSelected(App.youAutoplayPause);

			App.alwaysShowHidden = false;
			App.force_N_HiddenTut = false;
			App.force_W_HiddenTut = false;
			App.force_E_HiddenTut = false;
			App.force_S_HiddenTut = false;
			App.forceYouSeatToSouthZone = false;
			App.forceShowEtd = false;

			App.fillHandDisplay = false;
			App.runTestsAtStartUp = false;
			App.showTestsLogAtEnd = false;
			if (App.devMode) {
				fillHandDisplay.setSelected(App.fillHandDisplay);
				// runTestsAtStartUp.setSelected(App.runTestsAtStartUp);
				// showTestsLogAtEnd.setSelected(App.showTestsLogAtEnd);
			}

			App.allTwister_reset();
			App.calcCompassPhyOffset();
			App.gbp.dealMajorChange();
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
			App.frame.rop.p2_SeatChoice.showButtonStates();

			App.frame.repaint();
		}
	}

	/** This listens for the check box changed event */
	public void itemStateChanged(ItemEvent e) {

		boolean b = (e.getStateChange() == ItemEvent.SELECTED);
		Object source = e.getItemSelectable();

		// @formatter:off
		if (source == useDDSwhenAvaialble) {
			App.useDDSwhenAvaialble = b;
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
		else if (source == youAutoplayAlways) {
			           App.youAutoplayAlways = b;
			           App.implement_youAutoplayAlways();
		}
		else if (source == youAutoplayPause) {
	                   App.youAutoplayPause = b;
		}
		else if (source == youPlayerEotWait) {
                       App.youPlayerEotWait = b;
		}
		else if (source == fillHandDisplay) {
                       App.fillHandDisplay = b;
                       App.implement_fillHandDisplay();
		}
		else if (source == runTestsAtStartUp) {
	                   App.runTestsAtStartUp = b;
		}
		else if (source == showTestsLogAtEnd) {
                       App.showTestsLogAtEnd = b;
		}
		else if (source == forceYouSeatToSouthZone) {
					   App.forceYouSeatToSouthZone = b;
		}
		else if (source == forceShowEtd) {
			           App.forceShowEtd = b;
        }
		

		// @formatter:on

		if (App.allConstructionComplete) {
			App.savePreferences();

			showButtonStates();
			App.frame.rop.p2_SeatChoice.showButtonStates();

			App.frame.repaint();
		}
	}
}
