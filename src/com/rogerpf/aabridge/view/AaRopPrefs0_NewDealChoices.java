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
import com.rogerpf.aabridge.controller.Aaf;
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
	QRadioButton chosenGame25;
	QRadioButton chosenGame24;

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

	ButtonGroup dealFilter = new ButtonGroup();

	QRadioButton dealFilter0;
	QRadioButton dealFilter1;
	QRadioButton dealFilter2;
	QRadioButton dealFilter3;
	QRadioButton dealFilter4;

	QButton applyDefaults;

	public AaRopPrefs0_NewDealChoices() {
		setOpaque(true); // remember - we derive from ClickPanel which is setOpaque(false)
//		setBackground(SystemColor.control);

		Border bdr1 = BorderFactory.createEmptyBorder(1, 3, 1, 0);

		setLayout(new MigLayout(App.simple + ", flowy", "[][]", "[][]"));

		String rbInset = "gapx 7";

		// @formatter:off	
		add(anyLabel  = new QLabel(Aaf.menuOpt_newDeals_D), "gapx5, gapy 5");
		anyLabel.setForeground(Aaa.optionsTitleGreen);

		add(whichSeatNew   = new QLabel(Aaf.gT("newDealTab.yourSeat")), "gapy 6");
		whichSeatNew.setForeground(Aaa.optionsTitleGreen);
		
		boolean eastVal  = (App.youSeatForNewDeal == Dir.East);
		boolean westVal  = (App.youSeatForNewDeal == Dir.West);
		boolean southVal = !(eastVal || westVal);
		
		add(westNew        = new QRadioButton(this, rbGroupSeatNew, bdr1, westVal,  "west",  Aaf.gT("newDealTab.lho")), "split2, gapx 2, flowx");
		add(eastNew        = new QRadioButton(this, rbGroupSeatNew, bdr1, eastVal,  "east",  Aaf.gT("newDealTab.rho")), "flowy");
		add(southNew       = new QRadioButton(this, rbGroupSeatNew, bdr1, southVal, "south", Aaf.gT("newDealTab.declarer")), "gapx 27");

		add(anyLabel  = new QLabel(Aaf.gT("newDealTab.bidding")), "gapy 8");
		anyLabel.setForeground(Aaa.optionsTitleGreen);

		Border bdr  = BorderFactory.createEmptyBorder(1, 0, 0, 0);
		Border bdr2 = BorderFactory.createEmptyBorder(1, 0, 5, 0);

		add(biddingShow = new QRadioButton(this, rbGroupBids, bdr,  App.watchBidding, "biddingShow", Aaf.gT("newDealTab.biddingY")), rbInset);
		add(biddingHide = new QRadioButton(this, rbGroupBids, bdr, !App.watchBidding, "biddingHide", Aaf.gT("newDealTab.biddingN")), rbInset);
		
		add(anyLabel    = new QLabel(Aaf.gT("newDealTab.choose")), "gapy 12");
		    anyLabel.setForeground(Aaa.optionsTitleGreen);

		String game    = Aaf.gT("newDealTab.game");
		String slam    = Aaf.gT("newDealTab.slam");
		
		add(userBids       = new QRadioButton(this, rbGroupDeal, bdr2, App.dealCriteria.contentEquals("userBids"      ), "userBids",       "23+  " + Aaf.gT("newDealTab.youPick")), rbInset + ", gapy 0");
		add(chosenGame28   = new QRadioButton(this, rbGroupDeal, bdr,  App.dealCriteria.contentEquals("chosenGame28"  ), "chosenGame28",   "28    " + game), rbInset);
		add(chosenGame27   = new QRadioButton(this, rbGroupDeal, bdr,  App.dealCriteria.contentEquals("chosenGame27"  ), "chosenGame27",   "27    " + game), rbInset);
		add(chosenGame26   = new QRadioButton(this, rbGroupDeal, bdr,  App.dealCriteria.contentEquals("chosenGame26"  ), "chosenGame26",   "26    " + game), rbInset);
		add(chosenGame25   = new QRadioButton(this, rbGroupDeal, bdr,  App.dealCriteria.contentEquals("chosenGame25"  ), "chosenGame25",   "25    " + game), rbInset);
		add(chosenGame24   = new QRadioButton(this, rbGroupDeal, bdr2, App.dealCriteria.contentEquals("chosenGame24"  ), "chosenGame24",   "24    " + game), rbInset);
		add(twoSuitSlam_E1 = new QRadioButton(this, rbGroupDeal, bdr,  App.dealCriteria.contentEquals("twoSuitSlam_E1"), "twoSuitSlam_E1", "28+  " + slam + "  5-5, 5-4"), rbInset);
		add(twoSuitSlam_E2 = new QRadioButton(this, rbGroupDeal, bdr,  App.dealCriteria.contentEquals("twoSuitSlam_E2"), "twoSuitSlam_E2", "28+  " + slam + "  5-4, 5-4"), rbInset);
		add(twoSuitSlam_M1 = new QRadioButton(this, rbGroupDeal, bdr,  App.dealCriteria.contentEquals("twoSuitSlam_M1"), "twoSuitSlam_M1", "29+  " + slam + "  5-4, 5-3"), rbInset);
		add(twoSuitSlam_M2 = new QRadioButton(this, rbGroupDeal, bdr2, App.dealCriteria.contentEquals("twoSuitSlam_M2"), "twoSuitSlam_M2", "29+  " + slam + "  5-3, 5-3"), rbInset);
		add(twoSuitSlam_I1 = new QRadioButton(this, rbGroupDeal, bdr,  App.dealCriteria.contentEquals("twoSuitSlam_I1"), "twoSuitSlam_I1", "30+  " + slam + "  5-4, 4-4"), rbInset);
		add(twoSuitSlam_I2 = new QRadioButton(this, rbGroupDeal, bdr,  App.dealCriteria.contentEquals("twoSuitSlam_I2"), "twoSuitSlam_I2", "30+  " + slam + "  5-3, 4-4"), rbInset);
		add(twoSuitSlam_H1 = new QRadioButton(this, rbGroupDeal, bdr,  App.dealCriteria.contentEquals("twoSuitSlam_H1"), "twoSuitSlam_H1", "31+  " + slam + "  4-4, 4-4"), rbInset);
		add(twoSuitSlam_H2 = new QRadioButton(this, rbGroupDeal, bdr,  App.dealCriteria.contentEquals("twoSuitSlam_H2"), "twoSuitSlam_H2", "31+  " + slam + "  4-4, 4-3"), rbInset);
 		add(ntSmall_M      = new QRadioButton(this, rbGroupDeal, bdr2, App.dealCriteria.contentEquals("ntSmall_M"     ), "ntSmall_M",      "32+  6" + Aaf.game_nt), rbInset);
		add(ntGrand_E      = new QRadioButton(this, rbGroupDeal, bdr,  App.dealCriteria.contentEquals("ntGrand_E"     ), "ntGrand_E",      "37+  7" + Aaf.game_nt), rbInset);
		add(ntGrand_M      = new QRadioButton(this, rbGroupDeal, bdr,  App.dealCriteria.contentEquals("ntGrand_M"     ), "ntGrand_M",      "35+  7" + Aaf.game_nt), rbInset);
		add(ntGrand_H      = new QRadioButton(this, rbGroupDeal, bdr,  App.dealCriteria.contentEquals("ntGrand_H"     ), "ntGrand_H",      "33+  7" + Aaf.game_nt), rbInset);
			
	
		add(anyLabel    = new QLabel(Aaf.gT("newDealTab.interesting")), "gapy 12, gapx 5");
		    anyLabel.setForeground(Aaa.optionsTitleGreen);

		add(dealFilter0    = new QRadioButton(this, dealFilter, bdr,  App.dealFilter == 0, "", ""), "gapx4, split5, flowx");
		add(dealFilter1    = new QRadioButton(this, dealFilter, bdr,  App.dealFilter == 1, "", ""), "");
		add(dealFilter2    = new QRadioButton(this, dealFilter, bdr,  App.dealFilter == 2, "", ""), "");
		add(dealFilter3    = new QRadioButton(this, dealFilter, bdr,  App.dealFilter == 3, "", ""), "");
		add(dealFilter4    = new QRadioButton(this, dealFilter, bdr,  App.dealFilter == 4, "", ""), "");

		add(anyLabel    = new QLabel(Aaf.gT("newDealTab.lessMore")), "gapx 5");

		
		
		add(applyDefaults = new QButton(this, Aaf.gT("cmnTab.applyDef")), "gapy20, gapx4");
		if (App.onMac == false)
		    applyDefaults.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 4));
		// @formatter:on
	}

	public void actionPerformed(ActionEvent e) {

		Object source = e.getSource();
		if (source == applyDefaults) {

			App.frame.rop.p1_AutoPlay.applyDefaults();
			App.frame.rop.p2_SeatChoice.applyDefaults();

			App.youSeatForNewDeal = Dir.South;
			westNew.setSelected(false);
			eastNew.setSelected(false);
			southNew.setSelected(true);

			App.watchBidding = true;
			biddingShow.setSelected(true);
			biddingHide.setSelected(false);

			App.dealCriteria = "twoSuitSlam_H2";
			userBids.setSelected(false);
			chosenGame28.setSelected(false);
			chosenGame27.setSelected(false);
			chosenGame26.setSelected(false);
			chosenGame25.setSelected(false);
			chosenGame24.setSelected(false);
			twoSuitSlam_E1.setSelected(false);
			twoSuitSlam_E2.setSelected(false);
			twoSuitSlam_M1.setSelected(false);
			twoSuitSlam_M2.setSelected(false);
			twoSuitSlam_I1.setSelected(false);
			twoSuitSlam_I2.setSelected(false);
			twoSuitSlam_H1.setSelected(false);
			twoSuitSlam_H2.setSelected(true);
			ntSmall_M.setSelected(false);
			ntGrand_E.setSelected(false);
			ntGrand_M.setSelected(false);
			ntGrand_H.setSelected(false);

			App.dealFilter = 4;
			dealFilter0.setSelected(App.dealFilter == 0);
			dealFilter1.setSelected(App.dealFilter == 1);
			dealFilter2.setSelected(App.dealFilter == 2);
			dealFilter3.setSelected(App.dealFilter == 3);
			dealFilter4.setSelected(App.dealFilter == 4);

			App.savePreferences();

			repaint();
		}
	}

	/** This listens for the check box changed event */
	public void itemStateChanged(ItemEvent e) {

		boolean b = (e.getStateChange() == ItemEvent.SELECTED);
		Object source = e.getItemSelectable();

		// for the buttons, we are only interested in the selected values
		if (b == false) {
			; // do nothing
		}

		else if (source == dealFilter0) {
			App.dealFilter = 0;
		}
		else if (source == dealFilter1) {
			App.dealFilter = 1;
		}
		else if (source == dealFilter2) {
			App.dealFilter = 2;
		}
		else if (source == dealFilter3) {
			App.dealFilter = 3;
		}
		else if (source == dealFilter4) {
			App.dealFilter = 4;
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
		else if (source == chosenGame25) {
			App.dealCriteria = "chosenGame25";
		}
		else if (source == chosenGame24) {
			App.dealCriteria = "chosenGame24";
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
