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
class AaRopPrefs2_AutoPlay extends ClickPanel implements ItemListener {

	private static final long serialVersionUID = 1L;

	QLabel defSig;
	ButtonGroup rbGroupSig = new ButtonGroup();
	QRadioButton noSignal;
	QRadioButton stdEvenCount;
	QRadioButton udcOddCount;

	QLabel anyLabel;
	QCheckBox youPlayerEotWait;
	QCheckBox youAutoSingletons;
	QCheckBox youAutoAdjacent;

	QCheckBox yourFinessesMostlyFail;

	QCheckBox youAutoplayAlways;
	QCheckBox youAutoplayPause;
	QCheckBox alwaysShowHidden;
	QCheckBox fillHandDisplay;
	QCheckBox runTestsAtStartUp;
	QCheckBox showTestsLogAtEnd;

	QLabel testingText;

	public AaRopPrefs2_AutoPlay() {
		setOpaque(true); // remember - we derive from ClickPanel which is setOpaque(false)
		setBackground(SystemColor.control);

		Border bdr4 = BorderFactory.createEmptyBorder(1, 4, 1, 4);

		setLayout(new MigLayout(App.simple + ", flowy"));

		String cbInset = "gapx 10";
		String rbInset = "gapx 7";

		// @formatter:off
		add(anyLabel  = new QLabel("AutoPlay"), "gapy 5");
		anyLabel.setForeground(Aaa.optionsTitleGreen);

		add(defSig      = new QLabel("Defender Signaling  -  Defenders, when just following suit / discarding, will use a 'peter' HIGH then LOW to show ?"), "gapy 10");
		defSig.setForeground(Aaa.optionsTitleGreen);
		add(noSignal    = new QRadioButton(this, rbGroupSig,  bdr4, App.defenderSignals == Zzz.NoSignals,     "NoSignal",     "Nothing -  defenders won't signal"), rbInset);
		add(stdEvenCount= new QRadioButton(this, rbGroupSig,  bdr4, App.defenderSignals == Zzz.StdEvenCount,  "StdEvenCount", "Std   -  an EVEN number in the suit EXCEPT Trumps when shows an ODD number.     Ten and above are not used to signal  "), rbInset);
		add(udcOddCount = new QRadioButton(this, rbGroupSig,  bdr4, App.defenderSignals == Zzz.UdcOddCount,   "UdcOddCount",  "UDC  -  an ODD number in the suit including Trumps,  (UDC => Updside Down Count).     Ten and above are not used to signal  "), rbInset);

		add(anyLabel  = new QLabel("Make YOUR finesses mostly fail"), "gapy 18");
		anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(yourFinessesMostlyFail= new QCheckBox(this, App.yourFinessesMostlyFail, "Make YOUR finesses mostly fail - so you have to think harder  (applies to declarer play only)  "), "gapy 2, gapx 5");

		add(anyLabel  = new QLabel("End of trick  and  AutoPlay options"), "gapy 20");
		anyLabel.setForeground(Aaa.optionsTitleGreen);

		add(youPlayerEotWait     = new QCheckBox(this, App.youPlayerEotWait,  "Wait at the end of each trick  -  click on green to continue (gray dot indicator"), " gapy 5");
		add(youAutoSingletons    = new QCheckBox(this, App.youAutoSingletons, "You AutoPlay Singletons  "));
		add(youAutoAdjacent      = new QCheckBox(this, App.youAutoAdjacent,   "You AutoPlay Equal Cards  "), cbInset);
		
		add(testingText = new QLabel(" TESTING - The options below are for testing   -   however  you are welcome to try them out  "), "gapy 32");
		testingText.setForeground(Aaa.optionsTitleGreen);

		add(youAutoplayAlways    = new QCheckBox(this, App.youAutoplayAlways, "You AutoPlay ALWAYS  (for fun)  "), "gapy 5");
		add(youAutoplayPause     = new QCheckBox(this, App.youAutoplayPause,  "You wait BEFORE each trick (only when 'You AutoPlay ALWAYS')  -  click anywhere to continue  "), cbInset);
		add(alwaysShowHidden     = new QCheckBox(this, App.alwaysShowHidden,  "Show Hidden Hands   -   Always show the normally hidden hands     :)  "), " gapy 15");
		alwaysShowHidden.setForeground(Cc.RedStrong);
		if (App.devMode) {
			add(fillHandDisplay   = new QCheckBox(this, App.fillHandDisplay,  "Fill the hands with cards   -   for testing ONLY  "));
			add(runTestsAtStartUp = new QCheckBox(this, App.runTestsAtStartUp,"Tests - Always run the tests at start up  "));
		}
		// @formatter:on
	}

	/** This listens for the check box changed event */
	public void itemStateChanged(ItemEvent e) {

		boolean b = (e.getStateChange() == ItemEvent.SELECTED);
		Object source = e.getItemSelectable();

		// @formatter:off
		if (source == yourFinessesMostlyFail) {
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

		// @formatter:on

		if (App.allConstructionComplete) {
			App.savePreferences();
			App.frame.repaint();
		}
	}
}
