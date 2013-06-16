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

import javax.swing.ButtonGroup;

import net.miginfocom.swing.MigLayout;

import com.rogerpf.aabridge.controller.App;

/**   
 */
class AaRopDealChoices extends ClickPanel implements ItemListener {

	private static final long serialVersionUID = 1L;

	ButtonGroup rbGroupDeal = new ButtonGroup();
	QRadioButton userBids;
	QRadioButton chosenGame28;
	QRadioButton chosenGame27;
	QRadioButton chosenGame26;

	QRadioButton twoSuitSlam_E;
	QRadioButton twoSuitSlam_M;
	QRadioButton twoSuitSlam_H;

	QRadioButton ntSmall_M;
	
	QRadioButton ntGrand_E;
	QRadioButton ntGrand_M;
	QRadioButton ntGrand_H;

	ButtonGroup rbGroupBids = new ButtonGroup();
	QRadioButton biddingShow;
	QRadioButton biddingHide;

	public AaRopDealChoices() {
		setLayout(new MigLayout("insets 10 0 0 0, gap 0! 0!, flowy"));

		String rbInset = "gapx 7";

		// @formatter:off	
		add(new QLabel("New deals - choose one of the Deal Options below (NS always get the best hands)"));
		add(userBids       = new QRadioButton(this, rbGroupDeal, "userBids",       "23+ points,  YOU pick the contract"), rbInset);
		add(chosenGame28   = new QRadioButton(this, rbGroupDeal, "chosenGame28",   "28    Game -  EASY"), rbInset);
		add(chosenGame27   = new QRadioButton(this, rbGroupDeal, "chosenGame27",   "27    Game -  MODERATE"), rbInset);
		add(chosenGame26   = new QRadioButton(this, rbGroupDeal, "chosenGame26",   "26    Game -  HARDER"), rbInset);
		add(twoSuitSlam_E   = new QRadioButton(this, rbGroupDeal, "twoSuitSlam_E", "28+   E -  slam two suit fit 5-4, 5-4 -  EASIEST"), rbInset);
		add(twoSuitSlam_M   = new QRadioButton(this, rbGroupDeal, "twoSuitSlam_M", "29+   M - slam two suit fit 5-4, 4-4 -  MODERATE"), rbInset);
		add(twoSuitSlam_H   = new QRadioButton(this, rbGroupDeal, "twoSuitSlam_H", "30+   H -  slam two suit fit 4-4, 4-4 -  HARD"), rbInset);
		add(ntSmall_M  = new QRadioButton(this, rbGroupDeal, "ntSmall_M", "32+  6NT -  Easy or Hard?    what do you think?"), rbInset);
		add(ntGrand_E  = new QRadioButton(this, rbGroupDeal, "ntGrand_E", "37+  7NT -  EASY"), rbInset);
		add(ntGrand_M  = new QRadioButton(this, rbGroupDeal, "ntGrand_M", "35+  7NT -  MODERATE"), rbInset);
		add(ntGrand_H  = new QRadioButton(this, rbGroupDeal, "ntGrand_H", "33+  7NT -  HARDEST"), rbInset);
		
		add(new QLabel("Watch the Bidding, do you want to watch the bidding happen?"), "gapy 10");
		add(biddingShow = new QRadioButton(this, rbGroupBids,  App.watchBidding, "biddingShow", "Yes - show the bidding"), rbInset);
		add(biddingHide = new QRadioButton(this, rbGroupBids, !App.watchBidding, "biddingHide", "No - go straight to the play"), rbInset);
		
		// @formatter:on
	}

	/** This listens for the check box changed event */
	public void itemStateChanged(ItemEvent e) {

		boolean b = (e.getStateChange() == ItemEvent.SELECTED);
		Object source = e.getItemSelectable();

		// we are only interested in the selected values for the buttons
		if (b == false) {
			; // do nothing
		}
		else if (source == userBids) {
			App.dealCriteria = "userBids";
		}
		else if (source == chosenGame28) {
			App.dealCriteria = "chosenGame28";
		}
		else if (source == chosenGame27) {
			App.dealCriteria = "chosenGame27";
		}
		else if (source == chosenGame26) {
			App.dealCriteria = "chosenGame26";
		}
		else if (source == twoSuitSlam_E) {
			App.dealCriteria = "twoSuitSlam_E";
		}
		else if (source == twoSuitSlam_M) {
			App.dealCriteria = "twoSuitSlam_M";
		}
		else if (source == twoSuitSlam_H) {
			App.dealCriteria = "twoSuitSlam_H";
		}
		else if (source == ntSmall_M) {
			App.dealCriteria = "ntSmall_M";
		}
		else if (source == ntGrand_E) {
			App.dealCriteria = "ntGrand_E";
		}
		else if (source == ntGrand_M) {
			App.dealCriteria = "ntGrand_M";
		}
		else if (source == ntGrand_H) {
			App.dealCriteria = "ntGrand_H";
		}

		else if (source == biddingShow) {
			App.watchBidding = true;
		}
		else if (source == biddingHide) {
			App.watchBidding = false;
		}

		App.setAutoBidOpts();

		if (App.allConstructionComplete) {
			App.frame.repaint();
		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		setBackground(SystemColor.control);
	}

}
