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

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.model.Zzz;

/**   
 */
class AaRopPrefs2_SeatChoice extends ClickPanel implements ItemListener {

	private static final long serialVersionUID = 1L;

	QCheckBox yourFinnessesMostlyFail;

	ButtonGroup rbGroupSeat = new ButtonGroup();
	QRadioButton south;
	QRadioButton east;
	QRadioButton west;

	ButtonGroup rbGroupBids = new ButtonGroup();
	QRadioButton biddingShow;
	QRadioButton biddingHide;

	QLabel topLine;
	QLabel whichSeat;
	QLabel watchLabel;

	public AaRopPrefs2_SeatChoice() {
		setLayout(new MigLayout("insets 0 0 0 0, gap 0! 0!, flowy", "[][]", "[][]"));

		String rbInset = "gapx 7";

		// @formatter:off		
		add(topLine  = new QLabel("Make YOUR finnesses mostly fail"), "gapy 5");
		topLine.setForeground(Aaa.optionsTitleGreen);

		add(yourFinnessesMostlyFail= new QCheckBox(this, App.yourFinnessesMostlyFail, "Make YOUR finnesses mostly fail - so you have to think harder  (applies to declarer play only)  "), "gapy 2, gapx 5");

		add(whichSeat   = new QLabel("Which Seat?  -  Declarer (recommended) or defend sitting East / West  -  PLEASE NOTE - The declarer PLAY ENIGNE is very much a 'work in progress'  "), "gapy 18");
		whichSeat.setForeground(Aaa.optionsTitleGreen);
		
		boolean eastVal  = (App.youSeatForNewDeal == Zzz.East);
		boolean westVal  = (App.youSeatForNewDeal == Zzz.West);
		boolean southVal = !(eastVal || westVal);
		
		add(west        = new QRadioButton(this, rbGroupSeat,  westVal,  "west",  "West"), "split2, gapx 5, flowx");
		add(east        = new QRadioButton(this, rbGroupSeat,  eastVal,  "east",  "East"), "flowy");
		add(south       = new QRadioButton(this, rbGroupSeat,  southVal, "south", "South  (Declarer)"), "gapx 37");

		add(watchLabel  = new QLabel("Watch the Bidding?  -  Do you want to watch the bidding happen?"), "gapy 15");
		watchLabel.setForeground(Aaa.optionsTitleGreen);
		add(biddingShow = new QRadioButton(this, rbGroupBids,  App.watchBidding, "biddingShow", "Yes - show the bidding  "), rbInset);
		add(biddingHide = new QRadioButton(this, rbGroupBids, !App.watchBidding, "biddingHide", "No - go straight to the play  "), rbInset);
		
		// @formatter:on
	}

	/** This listens for the check box changed event */
	public void itemStateChanged(ItemEvent e) {

		boolean b = (e.getStateChange() == ItemEvent.SELECTED);
		Object source = e.getItemSelectable();

		// we are only interested in the selected values for the buttons
		if (source == yourFinnessesMostlyFail) {
			App.yourFinnessesMostlyFail = b;
		}
		else if (b == false) {
			; // do nothing
		}
		else if (source == biddingShow) {
			App.watchBidding = true;
		}
		else if (source == biddingHide) {
			App.watchBidding = false;
		}

		else if (source == south) {
			App.youSeatForNewDeal = Zzz.South;
		}
		else if (source == west) {
			App.youSeatForNewDeal = Zzz.West;
		}
		else if (source == east) {
			App.youSeatForNewDeal = Zzz.East;
		}

		if (App.allConstructionComplete) {
			App.frame.repaint();
		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		setBackground(SystemColor.control);
	}

}
