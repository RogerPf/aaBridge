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
import com.rogerpf.aabridge.controller.Aaf;
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

	QCheckBox showPoorDefHint;
	QCheckBox showContNeededHint;

	QButton applyDefaults;

	public AaRopPrefs6_RedHints() {
		setOpaque(true); // remember - we derive from ClickPanel which is setOpaque(false)

		setLayout(new MigLayout(App.simple + ", flowy"));

		Border bdr4 = BorderFactory.createEmptyBorder(1, 4, 1, 4);

		// @formatter:off
		add(anyLabel  = new QLabel(Aaf.menuOpt_red_D), "gapy 5");
		anyLabel.setForeground(Aaa.optionsTitleGreen);
		
		add(showMouseWheelSplash = new QCheckBox(this, App.showMouseWheelSplash, Aaf.gT("redHintsTab.wheel")), "gapy 8");
		    showMouseWheelSplash.setBorder(bdr4);

		add(anyLabel = new QLabel(Aaf.gT("redHintsTab.prompts")), "gapy 18");
		anyLabel.setForeground(Aaa.optionsTitleGreen);

		add(showBidPlayMsgs     = new QCheckBox(this, App.showBidPlayMsgs,       Aaf.gT("redHintsTab.bidAnd")), "gapy 3");
        showBidPlayMsgs.setBorder(bdr4);

		add(anyLabel  = new QLabel("Red Arrows"), "gapy 20");
		anyLabel.setForeground(Aaa.optionsTitleGreen);

		add(showRedNewBoardArrow = new QCheckBox(this, App.showRedNewBoardArrow, Aaf.gT("redHintsTab.newBoard")), "gapy 3");
			showRedNewBoardArrow.setBorder(bdr4);
		add(showRedVuGraphArrow = new QCheckBox(this, App.showRedVuGraphArrow,   Aaf.gT("redHintsTab.vugraph")));
	        showRedVuGraphArrow.setBorder(bdr4);
	        
		add(showRedEditArrow    = new QCheckBox(this, App.showRedEditArrow,      Aaf.gT("redHintsTab.edit")));
		    showRedEditArrow.setBorder(bdr4);
		add(showRedDividerArrow = new QCheckBox(this, App.showRedDividerArrow,   Aaf.gT("redHintsTab.divider")));
		    showRedDividerArrow.setBorder(bdr4);

		add(anyLabel  = new QLabel(Aaf.gT("redHintsTab.redBorder")), "gapy 20");
			anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(showDfcExamHlt      = new QCheckBox(this, App.showDfcExamHlt,        Aaf.gT("redHintsTab.exam")), "gapy 3");
		    showDfcExamHlt.setBorder(bdr4);

		add(anyLabel = new QLabel(Aaf.gT("redHintsTab.guidance")), "gapy 20");
			anyLabel.setForeground(Aaa.optionsTitleGreen);
			
		add(showPoorDefHint     = new QCheckBox(this, App.showPoorDefHint,    Aaf.gT("redHintsTab.poorDef")), "gapy 3");
		    showPoorDefHint.setBorder(bdr4);
		add(showContNeededHint  = new QCheckBox(this, App.showContNeededHint, Aaf.gT("redHintsTab.contract")), "gapy 3");
		    showContNeededHint.setBorder(bdr4);

		anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(applyDefaults = new QButton(this, Aaf.gT("cmnTab.applyDef")), "gapy20, gapx4");
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
			App.showPoorDefHint = true;
			App.showPoorDefHint = true;

			showMouseWheelSplash.setSelected(App.showMouseWheelSplash);
			showBidPlayMsgs.setSelected(App.showBidPlayMsgs);
			showRedNewBoardArrow.setSelected(App.showRedNewBoardArrow);
			showRedVuGraphArrow.setSelected(App.showRedVuGraphArrow);
			showRedEditArrow.setSelected(App.showRedEditArrow);
			showRedDividerArrow.setSelected(App.showRedDividerArrow);
			showDfcExamHlt.setSelected(App.showDfcExamHlt);
			showPoorDefHint.setSelected(App.showPoorDefHint);
			showPoorDefHint.setSelected(App.showPoorDefHint);
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
		else if (source == showPoorDefHint) {
            App.showPoorDefHint = b;
        }
		else if (source == showContNeededHint) {
            App.showContNeededHint = b;
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
