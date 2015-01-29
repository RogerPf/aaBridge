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
import com.rogerpf.aabridge.model.Dir;

/**   
 */
public class AaRopPrefs1_SeatChoice extends ClickPanel implements ItemListener {

	private static final long serialVersionUID = 1L;

	QLabel anyLabel;

	QLabel whichSeatNew;
	ButtonGroup rbGroupSeatNew = new ButtonGroup();
	QRadioButton southNew;
	QRadioButton eastNew;
	QRadioButton westNew;

	QLabel existingDealsLabel;

	ButtonGroup rbRespectLinYou = new ButtonGroup();
	QRadioButton respLinYou;
	QRadioButton overrideLinYou;

	QLabel whichSeatLin;
	ButtonGroup rbGroupSeatLin = new ButtonGroup();
	QRadioButton southLin;
	QRadioButton eastLin;
	QRadioButton westLin;

	QLabel oldSouthLabel;
	ButtonGroup rbGroupSouthZone = new ButtonGroup();
	QRadioButton southZoneSouth;
	QRadioButton southZoneDeclarer;
	QRadioButton southZoneYouSeat;

	QCheckBox reviewFromPlay;
	QCheckBox showOpeningLead;

	public AaRopPrefs1_SeatChoice() {
		setOpaque(true); // remember - we derive from ClickPanel which is setOpaque(false)
		setBackground(SystemColor.control);

		Border bdr1 = BorderFactory.createEmptyBorder(1, 3, 1, 0);

		setLayout(new MigLayout(App.simple + ", flowy", "[][]", "[][]"));

		String rbInset = "gapx 7";

		// @formatter:off
		add(anyLabel  = new QLabel("Seat     -    Seat Choice"), "gapy 5");
		anyLabel.setForeground(Aaa.optionsTitleGreen);

		add(whichSeatNew   = new QLabel("New Deals - Which Seat is You?  -  Declarer (recommended) or defend sitting East / West  -  This is for NEW deals   "), "gapy 10");
		whichSeatNew.setForeground(Aaa.optionsTitleGreen);
		
		boolean eastVal  = (App.youSeatForNewDeal == Dir.East);
		boolean westVal  = (App.youSeatForNewDeal == Dir.West);
		boolean southVal = !(eastVal || westVal);
		
		add(westNew        = new QRadioButton(this, rbGroupSeatNew, null, westVal,  "west",  "West"), "split2, gapx 5, flowx");
		add(eastNew        = new QRadioButton(this, rbGroupSeatNew, null, eastVal,  "east",  "East"), "flowy");
		add(southNew       = new QRadioButton(this, rbGroupSeatNew, bdr1, southVal, "south", "South  (Declarer)"), "gapx 37");

		add(anyLabel       = new QLabel("You can change the  'You Seat'  at ANY TIME by clicking on that hands compass banner area  "), "gapy 12");
		anyLabel.setForeground(Cc.RedStrong);


		add(anyLabel       = new QLabel("In a Movie with a contract showing on the table  OR  when 'Enter the Deal' is first clicked  then the "), "gapy 12");
		anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(anyLabel       = new QLabel("      (South) area shows ?  "));
		anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(southZoneSouth    = new QRadioButton(this, rbGroupSouthZone, bdr1, App.putWhoInSouthZone == 0, "southZoneSouth",    "Actual South hand  "), rbInset);
		add(southZoneDeclarer = new QRadioButton(this, rbGroupSouthZone, bdr1, App.putWhoInSouthZone == 1, "southZoneDeclarer", "Declarer  "), rbInset);
		add(southZoneYouSeat  = new QRadioButton(this, rbGroupSouthZone, bdr1, App.putWhoInSouthZone == 2, "southZoneYouSeat",  "'You Seat'  set below       i.e. the same as  'Enter the Deal'  You Seat  "), rbInset);


		add(anyLabel       = new QLabel("'Enter the Deal' sets the   "), "gapy 14");
		anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(anyLabel       = new QLabel("       'You Seat' to ?  "));
		anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(respLinYou     = new QRadioButton(this, rbRespectLinYou, bdr1,  App.respectLinYou, "respLinYou", "The  'You Seat'  in the deal  -  use this for pre-set bridge problems  "), rbInset);
		add(overrideLinYou = new QRadioButton(this, rbRespectLinYou, bdr1, !App.respectLinYou, "overrideLinYou", "The Seat below"), rbInset);

		boolean eastValLin  = (App.youSeatForLinDeal == Dir.East);
		boolean westValLin  = (App.youSeatForLinDeal == Dir.West);
		boolean southValLin = !(eastValLin || westValLin);
		
		add(westLin        = new QRadioButton(this, rbGroupSeatLin, null, westValLin,  "LHO  ",  "LHO"), "split2, gapx 2, flowx");
		add(eastLin        = new QRadioButton(this, rbGroupSeatLin, null, eastValLin,  "RHO",  "RHO"), "flowy");
		add(southLin       = new QRadioButton(this, rbGroupSeatLin, bdr1, southValLin, "Declarer", "Declarer"), "gapx 35");

		add(anyLabel       = new QLabel("'Enter the Deal'  shows ?"), "gapy 12");
		anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(reviewFromPlay = new QCheckBox(this, App.reviewFromPlay, "from Play  (skip Bidding)  "), "gapx 12");
		reviewFromPlay.setBorder(bdr1);
		add(showOpeningLead = new QCheckBox(this, App.showOpeningLead, "Opening Lead  -  OR Use the  '>' button  to show it  "), "gapx 12");
		showOpeningLead.setBorder(bdr1);
		// @formatter:on

		showButtonStates();
	}

	public void showButtonStates() {
		westLin.setEnabled(!App.respectLinYou);
		eastLin.setEnabled(!App.respectLinYou);
		southLin.setEnabled(!App.respectLinYou);

		if (App.visualMode == App.Vm_DealAndTutorial) {
			App.mg.refresh_for_youseat_change();
		}
	}

	public void respectLinYouChanged() {
		respLinYou.setSelected(App.respectLinYou);
		// overrideLinYou.setSelected(!App.respectLinYou);
		App.frame.invalidate();
	}

	/** This listens for the check box changed event */
	public void itemStateChanged(ItemEvent e) {

		boolean youSeatDeal = false;

		boolean b = (e.getStateChange() == ItemEvent.SELECTED);
		Object source = e.getItemSelectable();

		if (source == reviewFromPlay) {
			App.reviewFromPlay = b;
		}
		else if (source == showOpeningLead) {
			App.showOpeningLead = b;
		}
		// we are only interested in the selected values for the radio buttons

		else if (b == false) {
			; // do nothing
		}

		else if (source == southNew) {
			App.youSeatForNewDeal = Dir.South;
			youSeatDeal = true;
		}
		else if (source == westNew) {
			App.youSeatForNewDeal = Dir.West;
			youSeatDeal = true;
		}
		else if (source == eastNew) {
			App.youSeatForNewDeal = Dir.East;
			youSeatDeal = true;
		}

		else if (source == respLinYou) {
			App.respectLinYou = true;
		}
		else if (source == overrideLinYou) {
			App.respectLinYou = false;
			if (App.youSeatForNewDeal != Dir.South) {
				App.youSeatForNewDeal = Dir.South;
				eastNew.setSelected(false);
				westNew.setSelected(false);
				southNew.setSelected(true);
				youSeatDeal = false;
			}
		}

		else if (source == southLin) {
			App.youSeatForLinDeal = Dir.South; // declarer
		}
		else if (source == westLin) {
			App.youSeatForLinDeal = Dir.West; // LHO
		}
		else if (source == eastLin) {
			App.youSeatForLinDeal = Dir.East; // RHO
		}

		else if (source == southZoneSouth) {
			App.putWhoInSouthZone = 0;
		}
		else if (source == southZoneDeclarer) {
			App.putWhoInSouthZone = 1;
		}
		else if (source == southZoneYouSeat) {
			App.putWhoInSouthZone = 2;
		}

		if (App.allConstructionComplete) {
			App.savePreferences();
			if (youSeatDeal) {
				App.respectLinYou = true;
				respLinYou.setSelected(true);
			}
			showButtonStates();
		}

	}

}
