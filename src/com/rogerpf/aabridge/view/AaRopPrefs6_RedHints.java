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
import javax.swing.border.Border;

import net.miginfocom.swing.MigLayout;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;

/**   
 */
class AaRopPrefs6_RedHints extends ClickPanel implements ItemListener, ActionListener {

	private static final long serialVersionUID = 1L;

	QLabel anyLabel;

	QCheckBox showMouseWheelSplash;
	QCheckBox showRedEditArrow;
	QCheckBox showRedNewBoardArrow;
	QCheckBox showRedDividerArrow;
	QCheckBox showRedVuGraphArrow;
	QCheckBox showDfcExamHlt;
	QCheckBox showBidPlayMsgs;

	QButton resetAllPrefs;

	public AaRopPrefs6_RedHints() {
		setOpaque(true); // remember - we derive from ClickPanel which is setOpaque(false)
//		setBackground(SystemColor.control);

		setLayout(new MigLayout(App.simple + ", flowy"));

		Border bdr4 = BorderFactory.createEmptyBorder(1, 4, 1, 4);

		// @formatter:off
		add(anyLabel  = new QLabel("  Red Hints              -  Hide or Show"), "gapy 5");
		anyLabel.setForeground(Aaa.optionsTitleGreen);
		
		add(showMouseWheelSplash = new QCheckBox(this, App.showMouseWheelSplash, "'Mouse Wheel' hint   "), "gapy 3");
		    showMouseWheelSplash.setBorder(bdr4);

		add(anyLabel  = new QLabel("Red Arrows"), "gapy 10");
		anyLabel.setForeground(Aaa.optionsTitleGreen);

		add(showRedNewBoardArrow = new QCheckBox(this, App.showRedNewBoardArrow, "'Click  New Board' hint   "), "gapy 3");
			showRedNewBoardArrow.setBorder(bdr4);
		add(showRedVuGraphArrow = new QCheckBox(this, App.showRedVuGraphArrow, "'Extra Bar 4 Clickalbe Columns' hint   "));
	        showRedVuGraphArrow.setBorder(bdr4);
		add(showRedEditArrow    = new QCheckBox(this, App.showRedEditArrow,    "'Click  Edit  & use...' hint   "));
		    showRedEditArrow.setBorder(bdr4);
		add(showRedDividerArrow = new QCheckBox(this, App.showRedDividerArrow, "'Drag Divider...' hint   "));
		    showRedDividerArrow.setBorder(bdr4);

		add(anyLabel  = new QLabel("Red Border"), "gapy 15");
			anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(showDfcExamHlt      = new QCheckBox(this, App.showDfcExamHlt,      "DFC  Exam btn border  highlight   "), "gapy 3");
		    showDfcExamHlt.setBorder(bdr4);

		add(anyLabel = new QLabel("Prompts - Show the . . ."), "gapy 20");
		anyLabel.setForeground(Aaa.optionsTitleGreen);

		add(showBidPlayMsgs     = new QCheckBox(this, App.showBidPlayMsgs,     "'Bid' and 'Play' prompt messages   "), "gapy 3");
        showBidPlayMsgs.setBorder(bdr4);

		add(anyLabel  = new QLabel("Reset ALL Options to the default"), "gapy 25");
		anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(resetAllPrefs = new QButton(this, "Reset & Close"), "gapx4");
		if (App.onMac == false)
		    resetAllPrefs.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 4));
		// @formatter:on
	}

	public void actionPerformed(ActionEvent e) {

		Object source = e.getSource();

		if (source == resetAllPrefs) {
			App.SetOptionsToDefaultAndClose();
			// it never comes back !!
		}
	}

	/** This listens for the check box changed event */
	public void itemStateChanged(ItemEvent e) {

		boolean shaker = false;

		boolean b = (e.getStateChange() == ItemEvent.SELECTED);
		Object source = e.getItemSelectable();

		// @formatter:off
		if      (source == showMouseWheelSplash) {
            App.showMouseWheelSplash = b;
        }
		else if (source == showRedEditArrow) {
            App.showRedEditArrow = b;
        }
		else if (source == showRedDividerArrow) {
            App.showRedDividerArrow = b;
        }
		else if (source == showRedNewBoardArrow) {
            App.showRedNewBoardArrow = b;
        }
		else if (source == showRedVuGraphArrow) {
            App.showRedVuGraphArrow = b;
        }
		else if (source == showDfcExamHlt) {
            App.showDfcExamHlt = b;
        }
		else if (source == showBidPlayMsgs) {
            App.showBidPlayMsgs = b;
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
