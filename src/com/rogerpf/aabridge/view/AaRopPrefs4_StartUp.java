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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import net.miginfocom.swing.MigLayout;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;

/**   
 */
class AaRopPrefs4_StartUp extends ClickPanel implements ItemListener {

	private static final long serialVersionUID = 1L;

	QCheckBox showWelcome;
	QCheckBox startWithDoneHand;
	QCheckBox showBidPlayMsgs;
	QCheckBox showSuitSymbols;
	QCheckBox showPoints;
	QCheckBox showLTC;
	QCheckBox showEasySave;
	QCheckBox showPlayAgain;
	QCheckBox showRotationBtns;
	QCheckBox showEditPlay2Btn;
	QCheckBox showClaimBtn;
	QCheckBox deleteQuickSaves;
	QCheckBox deleteAutoSaves;

	QLabel topLine;

	public AaRopPrefs4_StartUp() {
		setLayout(new MigLayout("insets 0 0 0 0, gap 0! 0!, flowy"));

		// @formatter:off
		add(topLine  = new QLabel("Start Up and Button display  options"), "gapy 8");
		topLine.setForeground(Aaa.optionsTitleGreen);
		add(showWelcome         = new QCheckBox(this, App.showWelcome,      "At Start - Show Welcome Splash screen"), "gapy 5");
		add(startWithDoneHand   = new QCheckBox(this, App.startWithDoneHand,"At Start - Show the 'Blue Welcome Box' -  When unchecked the first deal will start straight away  "));
		add(showBidPlayMsgs     = new QCheckBox(this, App.showBidPlayMsgs,  "Show the 'Bid' and 'Play' prompt messages"));
		add(showSuitSymbols     = new QCheckBox(this, App.showSuitSymbols,  "Show the four Suit Symbols in the hand display area"));
		add(showPoints          = new QCheckBox(this, App.showPoints,       "Show the Point Count"));
		add(showLTC             = new QCheckBox(this, App.showLTC,          "Show the Losing Trick Count - See Wikipedia - Losing Trick Count with refinements"));
		add(showEasySave        = new QCheckBox(this, App.showEasySave,     "Show the 'Easy Save' button - Saves the file, if you have set at filename otherwise it does a 'SaveAs'  "), "gapy 10");
		add(showPlayAgain       = new QCheckBox(this, App.showPlayAgain,    "Show the 'Wipe' button - 'Wipe' gives a fast wipe of played cards, so you can replay that same deal  "));
		add(showRotationBtns    = new QCheckBox(this, App.showRotationBtns, "Show the Clockwise and Anti-clockwise rotation buttons  "));
		add(showEditPlay2Btn    = new QCheckBox(this, App.showEditPlay2Btn, "Show the 'Set Play' button - gives faster access to editing of play  "));
		add(showClaimBtn        = new QCheckBox(this, App.showClaimBtn,     "Show the 'Claim' button - Allows you to end a hand with a claim  "));
		add(deleteQuickSaves    = new QCheckBox(this, App.deleteQuickSaves, "Delete any QuickSaves that are 30 days old,  only QuickSaves in the QuickSave folder are examined  "), "gapy 10");
		add(deleteAutoSaves     = new QCheckBox(this, App.deleteAutoSaves,  "Delete any AutoSaves that are 7 days old,  only AutoSaves in the AutoSave folder are examined  "));
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
		else if (source == startWithDoneHand) {
	                   App.startWithDoneHand = b;
		}
		else if (source == showBidPlayMsgs) {
                       App.showBidPlayMsgs = b;
		}
		else if (source == showSuitSymbols) {
                       App.showSuitSymbols = b;
        }
		else if (source == showPoints) {
			           App.showPoints = b;
		}
		else if (source == showLTC) {
	                   App.showLTC = b;
		}
		else if (source == showEasySave) {
                       App.showEasySave = b;
             App.implement_showEasySave();
        }
		else if (source == showPlayAgain) {
                       App.showPlayAgain = b;
             App.implement_showPlayAgain();
        }
		else if (source == showRotationBtns) {
                       App.showRotationBtns = b;
             App.implement_showRotationBtns();
		}
		else if (source == showEditPlay2Btn) {
                       App.showEditPlay2Btn = b;
             App.implement_showEditPlay2Btn();
		}
		else if (source == showClaimBtn) {
                       App.showClaimBtn = b;
             App.implement_showClaimBtn();
}
		else if (source == deleteQuickSaves) {
                       App.deleteQuickSaves = b;
        }
		else if (source == deleteAutoSaves) {
                       App.deleteAutoSaves = b;
        }

		// @formatter:on

		if (App.allConstructionComplete) {
			App.savePreferences();
			App.frame.repaint();
		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		// setBackground(SystemColor.control);
	}

}
