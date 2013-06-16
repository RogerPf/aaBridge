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
class AaropPreferences extends ClickPanel implements ItemListener {

	private static final long serialVersionUID = 1L;

	QCheckBox showWelcome;
	QCheckBox showBidPlayMsgs;
	QCheckBox showPoints;
	QCheckBox nsAutoSingletons;
	QCheckBox nsAutoAdjacent;
	QCheckBox nsFinessesMostlyFail;
	QCheckBox unusedRPanelT;
	QCheckBox unusedRPanelV;

	QCheckBox alwaysShowEW;
	QCheckBox nsAutoplayAlways;
	QCheckBox nsAutoplayPause;
	QCheckBox showRotationBtns;
	QCheckBox fillHandDisplay;

	public AaropPreferences() {
		setLayout(new MigLayout("insets 10 0 0 0, gap 0! 0!, flowy"));

		String cbInset = "gapx 10";
//		String rbInset = "gapx 7";

		// @formatter:off
		add(showWelcome         = new QCheckBox(this, App.showWelcome,      "Show Welcome Splash at start"));
		add(showBidPlayMsgs     = new QCheckBox(this, App.showBidPlayMsgs,  "Show the 'Bid' and 'Play' prompt messages"));
		add(showPoints          = new QCheckBox(this, App.showPoints,       "Show Point Count"));
		add(nsAutoSingletons    = new QCheckBox(this, App.nsAutoSingletons, "NS auto play Singletons"));
		add(nsAutoAdjacent      = new QCheckBox(this, App.nsAutoAdjacent,   "NS auto play Equal Cards"), cbInset);
		add(nsFinessesMostlyFail= new QCheckBox(this, App.nsFinessesMostlyFail, "NS Finesses mostly fail - so you have to think harder"));
		
		add(new QLabel(" TESTING - The Options below are for testing - but you can try them out if you like"), "gapy 22");
		add(alwaysShowEW        = new QCheckBox(this, App.alwaysShowEW,     "EW Always show the EW hands  -  FOR TESTING     :)"), " gapy 8");
		alwaysShowEW.setForeground(Aaa.heartsColor);
		add(nsAutoplayAlways    = new QCheckBox(this, App.nsAutoplayAlways, "NS auto play ALWAYS (for fun)  -  and FOR TESTING"));
		add(nsAutoplayPause     = new QCheckBox(this, App.nsAutoplayPause,  "NS pause BEFORE each trick (when 'NS auto play ALWAYS') - click anywhere to continue  -  FOR TESTING"), cbInset);
		add(showRotationBtns    = new QCheckBox(this, App.showRotationBtns, "Show the Clockwise and Anti-clockwise rotation buttons  -  FOR TESTING"), " gapy 15");
		add(fillHandDisplay     = new QCheckBox(this, App.fillHandDisplay,  "Fill the hands with cards  -  FOR TESTING ONLY"));
		// @formatter:on
	}

	/** This listens for the check box changed event */
	public void itemStateChanged(ItemEvent e) {

		boolean b = (e.getStateChange() == ItemEvent.SELECTED);
		Object source = e.getItemSelectable();

		// @formatter:off
		if (     source == showWelcome) {
	                   App.showWelcome = b;
		}
		if (     source == showBidPlayMsgs) {
                       App.showBidPlayMsgs = b;
		}
		else if (source == alwaysShowEW) {
	                   App.alwaysShowEW = b;
	         App.implement_alwaysShowEW();
		} 
		else if (source == showPoints) {
			           App.showPoints = b;
		}
		else if (source == nsAutoSingletons) {
			           App.nsAutoSingletons = b;
		}
		else if (source == nsAutoAdjacent) {
			           App.nsAutoAdjacent = b;
		}
		else if (source == nsFinessesMostlyFail) {
	                   App.nsFinessesMostlyFail = b;
		}
		else if (source == nsAutoplayAlways) {
			           App.nsAutoplayAlways = b;
		}
		else if (source == nsAutoplayPause) {
	                   App.nsAutoplayPause = b;
		}
		else if (source == showRotationBtns) {
	                   App.showRotationBtns = b;
	         App.implement_showRotationBtns();
		}
		else if (source == fillHandDisplay) {
                       App.fillHandDisplay = b;
             App.implement_fillHandDisplay();
		}
		else if (source == unusedRPanelT) {
			           App.unusedRPanelT = b;
		}
		else if (source == unusedRPanelV) {
	                   App.unusedRPanelV = b;
		}

		App.setAutoBidOpts();

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
