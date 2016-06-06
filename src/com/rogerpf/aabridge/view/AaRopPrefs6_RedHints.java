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

	QCheckBox showBidPlayMsgs;

	QCheckBox showRedEditArrow;
	QCheckBox showRedNewBoardArrow;
	QCheckBox showRedDividerArrow;
	QCheckBox showRedVuGraphArrow;
	QCheckBox showDfcExamHlt;

	QButton applyDefaults;

	public AaRopPrefs6_RedHints() {
		setOpaque(true); // remember - we derive from ClickPanel which is setOpaque(false)
//		setBackground(SystemColor.control);

		setLayout(new MigLayout(App.simple + ", flowy"));

		Border bdr4 = BorderFactory.createEmptyBorder(1, 4, 1, 4);

		// @formatter:off
		add(anyLabel  = new QLabel("  Red Hints              -  Hide or Show"), "gapy 5");
		anyLabel.setForeground(Aaa.optionsTitleGreen);
		
		add(showMouseWheelSplash = new QCheckBox(this, App.showMouseWheelSplash, "'Wheel Mouse' hint   "), "gapy 8");
		    showMouseWheelSplash.setBorder(bdr4);

		add(anyLabel = new QLabel("Prompts - Show the . . ."), "gapy 18");
		anyLabel.setForeground(Aaa.optionsTitleGreen);

		add(showBidPlayMsgs     = new QCheckBox(this, App.showBidPlayMsgs,     "'Bid' and 'Play' prompt messages   "), "gapy 3");
        showBidPlayMsgs.setBorder(bdr4);

		add(anyLabel  = new QLabel("Red Arrows"), "gapy 18");
		anyLabel.setForeground(Aaa.optionsTitleGreen);

		add(showRedNewBoardArrow = new QCheckBox(this, App.showRedNewBoardArrow, "'Click  New Board' hint   "), "gapy 3");
			showRedNewBoardArrow.setBorder(bdr4);
		add(showRedVuGraphArrow = new QCheckBox(this, App.showRedVuGraphArrow, "'Extra Bar 4 Clickalbe Columns' hint   "));
	        showRedVuGraphArrow.setBorder(bdr4);
		add(showRedEditArrow    = new QCheckBox(this, App.showRedEditArrow,    "'Edit button usage' hint   "));
		    showRedEditArrow.setBorder(bdr4);
		add(showRedDividerArrow = new QCheckBox(this, App.showRedDividerArrow, "'Drag Divider...' hint   "));
		    showRedDividerArrow.setBorder(bdr4);

		add(anyLabel  = new QLabel("Red Border"), "gapy 18");
			anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(showDfcExamHlt      = new QCheckBox(this, App.showDfcExamHlt,      "DFC  Exam btn border  highlight   "), "gapy 3");
		    showDfcExamHlt.setBorder(bdr4);

		anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(applyDefaults = new QButton(this, "Apply Defaults"), "gapy20, gapx4");
		if (App.onMac == false)
			applyDefaults.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 4));
		// @formatter:on
	}

	public void actionPerformed(ActionEvent e) {

		Object source = e.getSource();

		if (source == applyDefaults) {

			App.showMouseWheelSplash = true;
			App.showBidPlayMsgs = true;
			App.showRedNewBoardArrow = true;
			App.showRedVuGraphArrow = true;
			App.showRedEditArrow = true;
			App.showRedDividerArrow = false;
			App.showDfcExamHlt = true;

			showMouseWheelSplash.setSelected(App.showMouseWheelSplash);
			showBidPlayMsgs.setSelected(App.showBidPlayMsgs);
			showRedNewBoardArrow.setSelected(App.showRedNewBoardArrow);
			showRedVuGraphArrow.setSelected(App.showRedVuGraphArrow);
			showRedEditArrow.setSelected(App.showRedEditArrow);
			showRedDividerArrow.setSelected(App.showRedDividerArrow);
			showDfcExamHlt.setSelected(App.showDfcExamHlt);
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
