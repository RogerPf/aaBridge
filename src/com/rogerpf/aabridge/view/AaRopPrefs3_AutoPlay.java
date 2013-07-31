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

import java.awt.Graphics;
import java.awt.SystemColor;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import net.miginfocom.swing.MigLayout;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;

/**   
 */
class AaRopPrefs3_AutoPlay extends ClickPanel implements ItemListener {

	private static final long serialVersionUID = 1L;

	QCheckBox youDeclarerPause;
	QCheckBox youDefenderPause;
	QCheckBox youAutoSingletons;
	QCheckBox youAutoAdjacent;

	QCheckBox youAutoplayAlways;
	QCheckBox youAutoplayPause;
	QCheckBox alwaysShowHidden;
	QCheckBox fillHandDisplay;
	QCheckBox runTestsAtStartUp;
	QCheckBox showTestsLogAtEnd;

	QLabel topLine;
	QLabel testingText;

	public AaRopPrefs3_AutoPlay() {
		setLayout(new MigLayout("insets 0 0 0 0, gap 0! 0!, flowy"));

		String cbInset = "gapx 10";

		// @formatter:off
		add(topLine  = new QLabel("Pause at end of trick  and  AutoPlay options"), "gapy 5");
		topLine.setForeground(Aaa.optionsTitleGreen);

		add(youDeclarerPause     = new QCheckBox(this, App.youDeclarerPause,  "Declarer Pause After   -   Pause AFTER each trick (only when you are the Declarer)  -  click anywhere to continue  "), " gapy 5");
		add(youDefenderPause     = new QCheckBox(this, App.youDefenderPause,  "Defender Pause After   -   Pause AFTER each trick (only when you are a Defender)  -  click anywhere to continue  "));
		add(youAutoSingletons    = new QCheckBox(this, App.youAutoSingletons, "You AutoPlay Singletons  "));
		add(youAutoAdjacent      = new QCheckBox(this, App.youAutoAdjacent,   "You AutoPlay Equal Cards  "), cbInset);
		
		add(testingText = new QLabel(" TESTING - The options below are for testing   -   however  you are welcome to try them out  "), "gapy 22");
		testingText.setForeground(Aaa.optionsTitleGreen);

		add(youAutoplayAlways    = new QCheckBox(this, App.youAutoplayAlways, "You AutoPlay ALWAYS  (for fun)  "), "gapy 5");
		add(youAutoplayPause     = new QCheckBox(this, App.youAutoplayPause,  "You pause BEFORE each trick (only when 'You AutoPlay ALWAYS')  -  click anywhere to continue  "), cbInset);
		add(alwaysShowHidden     = new QCheckBox(this, App.alwaysShowHidden,  "Show Hidden Hands   -   Always show the normally hidden hands     :)  "), " gapy 5");
		alwaysShowHidden.setForeground(Aaa.heartsColor);
		add(fillHandDisplay      = new QCheckBox(this, App.fillHandDisplay,  "Fill the hands with cards   -   for testing ONLY  "), " gapy 15");
		add(runTestsAtStartUp    = new QCheckBox(this, App.runTestsAtStartUp,"Tests - Always run the tests at start up  "));
		add(showTestsLogAtEnd    = new QCheckBox(this, App.showTestsLogAtEnd,"Show the test results file, once the tests have completed  "));
		// @formatter:on
	}

	/** This listens for the check box changed event */
	public void itemStateChanged(ItemEvent e) {

		boolean b = (e.getStateChange() == ItemEvent.SELECTED);
		Object source = e.getItemSelectable();

		// @formatter:off
		if (source == youAutoSingletons) {
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
		else if (source == youDeclarerPause) {
                       App.youDeclarerPause = b;
		}
		else if (source == youDefenderPause) {
                       App.youDefenderPause = b;
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
			App.frame.repaint();
		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		setBackground(SystemColor.control);
	}

}
