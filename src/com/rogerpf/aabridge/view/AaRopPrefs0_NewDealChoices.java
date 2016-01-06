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
import com.rogerpf.aabridge.model.Dir;

/**   
 */
class AaRopPrefs0_NewDealChoices extends ClickPanel implements ItemListener, ActionListener {

	private static final long serialVersionUID = 1L;

	QLabel anyLabel;

	QLabel whichSeatNew;
	ButtonGroup rbGroupSeatNew = new ButtonGroup();
	QRadioButton southNew;
	QRadioButton eastNew;
	QRadioButton westNew;

	ButtonGroup rbGroupBids = new ButtonGroup();
	QRadioButton biddingShow;
	QRadioButton biddingHide;

	ButtonGroup rbGroupDeal = new ButtonGroup();
	QRadioButton userBids;
	QRadioButton chosenGame28;
	QRadioButton chosenGame27;
	QRadioButton chosenGame26;

	QRadioButton twoSuitSlam_E1;
	QRadioButton twoSuitSlam_E2;
	QRadioButton twoSuitSlam_M1;
	QRadioButton twoSuitSlam_M2;
	QRadioButton twoSuitSlam_I1;
	QRadioButton twoSuitSlam_I2;
	QRadioButton twoSuitSlam_H1;
	QRadioButton twoSuitSlam_H2;

	QRadioButton ntSmall_M;

	QRadioButton ntGrand_E;
	QRadioButton ntGrand_M;
	QRadioButton ntGrand_H;

	QButton applyDefaults;

	public AaRopPrefs0_NewDealChoices() {
		setOpaque(true); // remember - we derive from ClickPanel which is setOpaque(false)
//		setBackground(SystemColor.control);

		Border bdr1 = BorderFactory.createEmptyBorder(1, 3, 1, 0);

		setLayout(new MigLayout(App.simple + ", flowy", "[][]", "[][]"));

		String rbInset = "gapx 7";

		// @formatter:off	
		add(anyLabel  = new QLabel("  New Deals  "), "gapy 5");
		anyLabel.setForeground(Aaa.optionsTitleGreen);

		add(whichSeatNew   = new QLabel("Your Seat?               Declarer (recommended)  OR  defend sitting East / West  "), "gapy 6");
		whichSeatNew.setForeground(Aaa.optionsTitleGreen);
		
		boolean eastVal  = (App.youSeatForNewDeal == Dir.East);
		boolean westVal  = (App.youSeatForNewDeal == Dir.West);
		boolean southVal = !(eastVal || westVal);
		
		add(westNew        = new QRadioButton(this, rbGroupSeatNew, bdr1, westVal,  "west",  "LHO"), "split2, gapx 2, flowx");
		add(eastNew        = new QRadioButton(this, rbGroupSeatNew, bdr1, eastVal,  "east",  "RHO"), "flowy");
		add(southNew       = new QRadioButton(this, rbGroupSeatNew, bdr1, southVal, "south", "Declarer  (recommended)"), "gapx 27");

		add(anyLabel  = new QLabel("Bidding  -  watch it happen?"), "gapy 8");
		anyLabel.setForeground(Aaa.optionsTitleGreen);

		Border bdr  = BorderFactory.createEmptyBorder(1, 0, 0, 0);
		Border bdr2 = BorderFactory.createEmptyBorder(1, 0, 5, 0);

		add(biddingShow = new QRadioButton(this, rbGroupBids, bdr,  App.watchBidding, "biddingShow", "Yes - show the bidding  "), rbInset);
		add(biddingHide = new QRadioButton(this, rbGroupBids, bdr, !App.watchBidding, "biddingHide", "No - go straight to the play  "), rbInset);
		
		add(anyLabel    = new QLabel("Choose    one        of the Deal Options below  (N-S always get the best hands)"), "gapy 12");
		anyLabel.setForeground(Aaa.optionsTitleGreen);
		
		add(userBids       = new QRadioButton(this, rbGroupDeal, bdr2, App.dealCriteria.contentEquals("userBids"      ), "userBids",       "23+ points,  YOU pick the contract - you need to be declarer to pick the contract  "), rbInset + ", gapy 0");
		add(chosenGame28   = new QRadioButton(this, rbGroupDeal, bdr,  App.dealCriteria.contentEquals("chosenGame28"  ), "chosenGame28",   "28    Game -  EASY  "), rbInset);
		add(chosenGame27   = new QRadioButton(this, rbGroupDeal, bdr,  App.dealCriteria.contentEquals("chosenGame27"  ), "chosenGame27",   "27    Game -  MODERATE  "), rbInset);
		add(chosenGame26   = new QRadioButton(this, rbGroupDeal, bdr2, App.dealCriteria.contentEquals("chosenGame26"  ), "chosenGame26",   "26    Game -  HARDER  "), rbInset);
		add(twoSuitSlam_E1 = new QRadioButton(this, rbGroupDeal, bdr,  App.dealCriteria.contentEquals("twoSuitSlam_E1"), "twoSuitSlam_E1", "28+   E  -  slam two suit fit 5-5, 5-4 -  EASIEST 1 "), rbInset);
		add(twoSuitSlam_E2 = new QRadioButton(this, rbGroupDeal, bdr,  App.dealCriteria.contentEquals("twoSuitSlam_E2"), "twoSuitSlam_E2", "28+   E  -  slam two suit fit 5-4, 5-4 -  EASIEST 2 "), rbInset);
		add(twoSuitSlam_M1 = new QRadioButton(this, rbGroupDeal, bdr,  App.dealCriteria.contentEquals("twoSuitSlam_M1"), "twoSuitSlam_M1", "29+   M -  slam two suit fit 5-4, 5-3 -  MODERATE 1 "), rbInset);
		add(twoSuitSlam_M2 = new QRadioButton(this, rbGroupDeal, bdr2, App.dealCriteria.contentEquals("twoSuitSlam_M2"), "twoSuitSlam_M2", "29+   M -  slam two suit fit 5-3, 5-3 -  MODERATE 2 "), rbInset);
		add(twoSuitSlam_I1 = new QRadioButton(this, rbGroupDeal, bdr,  App.dealCriteria.contentEquals("twoSuitSlam_I1"), "twoSuitSlam_I1", "30+    I  -  slam two suit fit 5-4, 4-4 -  INTERESTING 1 "), rbInset);
		add(twoSuitSlam_I2 = new QRadioButton(this, rbGroupDeal, bdr,  App.dealCriteria.contentEquals("twoSuitSlam_I2"), "twoSuitSlam_I2", "30+    I  -  slam two suit fit 5-3, 4-4 -  INTERESTING 2 "), rbInset);
		add(twoSuitSlam_H1 = new QRadioButton(this, rbGroupDeal, bdr,  App.dealCriteria.contentEquals("twoSuitSlam_H1"), "twoSuitSlam_H1", "31+   H  -  slam two suit fit 4-4, 4-4 -  HARDER 1 "), rbInset);
		add(twoSuitSlam_H2 = new QRadioButton(this, rbGroupDeal, bdr,  App.dealCriteria.contentEquals("twoSuitSlam_H2"), "twoSuitSlam_H2", "31+   H  -  slam two suit fit 4-4, 4-3 -  HARDER 2 "), rbInset);
 		add(ntSmall_M      = new QRadioButton(this, rbGroupDeal, bdr2, App.dealCriteria.contentEquals("ntSmall_M"     ), "ntSmall_M",      "32+  6NT -  Easy or Hard?    what do you think?  "), rbInset);
		add(ntGrand_E      = new QRadioButton(this, rbGroupDeal, bdr,  App.dealCriteria.contentEquals("ntGrand_E"     ), "ntGrand_E",      "37+  7NT -  EASY  "), rbInset);
		add(ntGrand_M      = new QRadioButton(this, rbGroupDeal, bdr,  App.dealCriteria.contentEquals("ntGrand_M"     ), "ntGrand_M",      "35+  7NT -  MODERATE  "), rbInset);
		add(ntGrand_H      = new QRadioButton(this, rbGroupDeal, bdr,  App.dealCriteria.contentEquals("ntGrand_H"     ), "ntGrand_H",      "33+  7NT -  HARDEST  "), rbInset);
				
		add(applyDefaults = new QButton(this, "Apply Defaults"), "gapy20, gapx4");
		applyDefaults.setToolTipText("Reset all  Seat Options  to default values  ");
		if (App.onMac == false)
		    applyDefaults.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 4));
		// @formatter:on
	}

	public void actionPerformed(ActionEvent e) {

		Object source = e.getSource();
		if (source == applyDefaults) {
			App.youSeatForNewDeal = Dir.South;
			westNew.setSelected(false);
			eastNew.setSelected(false);
			southNew.setSelected(true);

			App.watchBidding = true;
			biddingShow.setSelected(true);
			biddingHide.setSelected(false);

			App.dealCriteria = "ntGrand_E";
			userBids.setSelected(false);
			chosenGame28.setSelected(false);
			chosenGame27.setSelected(false);
			chosenGame26.setSelected(false);
			twoSuitSlam_E1.setSelected(false);
			twoSuitSlam_E2.setSelected(false);
			twoSuitSlam_M1.setSelected(false);
			twoSuitSlam_M2.setSelected(false);
			twoSuitSlam_I1.setSelected(false);
			twoSuitSlam_I2.setSelected(false);
			twoSuitSlam_H1.setSelected(false);
			twoSuitSlam_H2.setSelected(false);
			ntSmall_M.setSelected(false);
			ntGrand_E.setSelected(true);
			ntGrand_M.setSelected(false);
			ntGrand_H.setSelected(false);

			App.savePreferences();

			repaint();
		}
	}

	/** This listens for the check box changed event */
	public void itemStateChanged(ItemEvent e) {

		boolean b = (e.getStateChange() == ItemEvent.SELECTED);
		Object source = e.getItemSelectable();

		// we are only interested in the selected values for the buttons
		if (b == false) {
			; // do nothing
		}

		else if (source == southNew) {
			App.youSeatForNewDeal = Dir.South;
		}
		else if (source == westNew) {
			App.youSeatForNewDeal = Dir.West;
		}
		else if (source == eastNew) {
			App.youSeatForNewDeal = Dir.East;
		}

		else if (source == biddingShow) {
			App.watchBidding = true;
		}
		else if (source == biddingHide) {
			App.watchBidding = false;
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
		else if (source == twoSuitSlam_E1) {
			App.dealCriteria = "twoSuitSlam_E1";
		}
		else if (source == twoSuitSlam_E2) {
			App.dealCriteria = "twoSuitSlam_E2";
		}
		else if (source == twoSuitSlam_M1) {
			App.dealCriteria = "twoSuitSlam_M1";
		}
		else if (source == twoSuitSlam_M2) {
			App.dealCriteria = "twoSuitSlam_M2";
		}
		else if (source == twoSuitSlam_I1) {
			App.dealCriteria = "twoSuitSlam_I1";
		}
		else if (source == twoSuitSlam_I2) {
			App.dealCriteria = "twoSuitSlam_I2";
		}
		else if (source == twoSuitSlam_H1) {
			App.dealCriteria = "twoSuitSlam_H1";
		}
		else if (source == twoSuitSlam_H2) {
			App.dealCriteria = "twoSuitSlam_H2";
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

		if (App.allConstructionComplete) {
			App.savePreferences();
			App.frame.repaint();
		}
	}
}
