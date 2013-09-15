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

import javax.swing.ButtonGroup;

import net.miginfocom.swing.MigLayout;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;

/**   
 */
class AaRopPrefs5_Bidding extends ClickPanel implements ItemListener {

	private static final long serialVersionUID = 1L;

	QLabel watchLabel;
	ButtonGroup rbGroupBids = new ButtonGroup();
	QRadioButton biddingShow;
	QRadioButton biddingHide;

	QLabel yourFinnLab;
	QCheckBox yourFinessesMostlyFail;

	public AaRopPrefs5_Bidding() {
		setLayout(new MigLayout("insets 0 0 0 0, gap 0! 0!, flowy", "[][]", "[][]"));

		String rbInset = "gapx 7";

		// @formatter:off

		add(watchLabel  = new QLabel("For  NEW  deals   Do you want to watch the bidding happen?  -  This is for NEW deals   "), "gapy 8");
		watchLabel.setForeground(Aaa.optionsTitleGreen);
		add(biddingShow = new QRadioButton(this, rbGroupBids,  App.watchBidding, "biddingShow", "Yes - show the bidding  "), rbInset);
		add(biddingHide = new QRadioButton(this, rbGroupBids, !App.watchBidding, "biddingHide", "No - go straight to the play  "), rbInset);
		
		add(yourFinnLab  = new QLabel("Make YOUR finesses mostly fail"), "gapy 30");
		yourFinnLab.setForeground(Aaa.optionsTitleGreen);

		add(yourFinessesMostlyFail= new QCheckBox(this, App.yourFinessesMostlyFail, "Make YOUR finesses mostly fail - so you have to think harder  (applies to declarer play only)  "), "gapy 2, gapx 5");



		// @formatter:on
	}

	/** This listens for the check box changed event */
	public void itemStateChanged(ItemEvent e) {

		boolean b = (e.getStateChange() == ItemEvent.SELECTED);
		Object source = e.getItemSelectable();

		if (source == yourFinessesMostlyFail) {
			App.yourFinessesMostlyFail = b;
		}

		else // we are only interested in the selected values for the buttons
		if (b == false) {
			; // do nothing
		}
		else if (source == biddingShow) {
			App.watchBidding = true;
		}
		else if (source == biddingHide) {
			App.watchBidding = false;
		}

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
