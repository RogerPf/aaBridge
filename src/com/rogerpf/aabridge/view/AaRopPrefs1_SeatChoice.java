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

import javax.swing.ButtonGroup;

import net.miginfocom.swing.MigLayout;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.model.Cc;
import com.rogerpf.aabridge.model.Dir;

/**   
 */
class AaRopPrefs1_SeatChoice extends ClickPanel implements ItemListener {

	private static final long serialVersionUID = 1L;

	QLabel watchLabel;
	QLabel changeLabel;

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
	ButtonGroup rbGroupOldSouth = new ButtonGroup();
	QRadioButton oldSouthSouth;
	QRadioButton oldSouthDeclarer;

	public AaRopPrefs1_SeatChoice() {
		setOpaque(true); // remember - we derive from ClickPanel which is setOpaque(false)
		setBackground(SystemColor.control);

		setLayout(new MigLayout(App.simple + ", flowy", "[][]", "[][]"));

		String rbInset = "gapx 7";

		// @formatter:off

		add(whichSeatNew   = new QLabel("For  NEW  deals - Which Seat is You?  -  Declarer (recommended) or defend sitting East / West  -  This is for NEW deals   "), "gapy 5");
		whichSeatNew.setForeground(Aaa.optionsTitleGreen);
		
		boolean eastVal  = (App.youSeatForNewDeal == Dir.East);
		boolean westVal  = (App.youSeatForNewDeal == Dir.West);
		boolean southVal = !(eastVal || westVal);
		
		add(westNew        = new QRadioButton(this, rbGroupSeatNew, null, westVal,  "west",  "West"), "split2, gapx 5, flowx");
		add(eastNew        = new QRadioButton(this, rbGroupSeatNew, null, eastVal,  "east",  "East"), "flowy");
		add(southNew       = new QRadioButton(this, rbGroupSeatNew, null, southVal, "south", "South  (Declarer)"), "gapx 37");

		add(changeLabel    = new QLabel("You can change the  'You Seat'  at ANY TIME by clicking on that hands compass banner area  "), "gapy 12");
		changeLabel.setForeground(Cc.RedStrong);

		add(watchLabel     = new QLabel("When entering deals  -  Which Seat is You?  "), "gapy 12");
		watchLabel.setForeground(Aaa.optionsTitleGreen);
		add(respLinYou     = new QRadioButton(this, rbRespectLinYou, null,  App.respectLinYou, "respLinYou", "Use the  'You Seat'  set in the deal  (if any)   use for preset bridge problems"), rbInset);
		add(overrideLinYou = new QRadioButton(this, rbRespectLinYou, null, !App.respectLinYou, "overrideLinYou", "Use the  'You Seat'  set below"), rbInset);

		boolean eastValLin  = (App.youSeatForLinDeal == Dir.East);
		boolean westValLin  = (App.youSeatForLinDeal == Dir.West);
		boolean southValLin = !(eastValLin || westValLin);
		
		add(westLin        = new QRadioButton(this, rbGroupSeatLin, null, westValLin,  "LHO  ",  "LHO"), "split2, gapx 3, flowx");
		add(eastLin        = new QRadioButton(this, rbGroupSeatLin, null, eastValLin,  "RHO",  "RHO"), "flowy");
		add(southLin       = new QRadioButton(this, rbGroupSeatLin, null, southValLin, "Declarer", "Declarer"), "gapx 35");

		add(watchLabel     = new QLabel("When entering  deals  -  the bottom (South) seat should be ?   "), "gapy 12");
		watchLabel.setForeground(Aaa.optionsTitleGreen);
		add(oldSouthSouth  = new QRadioButton(this, rbGroupOldSouth,   null, !App.putDeclarerSouth, "oldSouthSouth",    "Actual South hand  "), rbInset);
		add(oldSouthDeclarer = new QRadioButton(this, rbGroupOldSouth, null,  App.putDeclarerSouth, "oldSouthDeclarer", "Declarer  "), rbInset);

		// @formatter:on
	}

	public void putDeclarerSouthChanged() {
		oldSouthSouth.setSelected(!App.putDeclarerSouth);
		oldSouthDeclarer.setSelected(App.putDeclarerSouth);
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

		else if (source == respLinYou) {
			App.respectLinYou = true;
		}
		else if (source == overrideLinYou) {
			App.respectLinYou = false;
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

		else if (source == oldSouthSouth) {
			App.putDeclarerSouth = false;
		}
		else if (source == oldSouthDeclarer) {
			App.putDeclarerSouth = true;
		}

		if (App.allConstructionComplete) {
			App.savePreferences();
			App.frame.repaint();
		}

	}

}
