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
import com.rogerpf.aabridge.model.Cc;

/**   
 */
class AaRopPrefs4_SuitColors extends ClickPanel implements ItemListener, ActionListener {

	private static final long serialVersionUID = 1L;

	QLabel anyLabel;
	QButton scrnColors;

	ButtonGroup dkColGroup = new ButtonGroup();
	QRadioButton dkCol0;
	QRadioButton dkCol1;
	QRadioButton dkCol2;
	QRadioButton dkCol3;

	ButtonGroup dkBkGroup = new ButtonGroup();
	QRadioButton dkCardsColored;
	QRadioButton dkCardsAllBlack;

	QCheckBox showSuitSymbols;

	QButton applyDefaults;

	public AaRopPrefs4_SuitColors() {
		setOpaque(true); // remember - we derive from ClickPanel which is setOpaque(false)
//		setBackground(SystemColor.control);

		setLayout(new MigLayout(App.simple + ", flowy", "[][]", "[][]"));

		String rbInset = "gapx 7";

		Border bdr1 = BorderFactory.createEmptyBorder(1, 0, 1, 0);

		// @formatter:off

		add(anyLabel  = new QLabel("  Suit Colors              -    Colors of the Suits and Suit symbols"), "gapy 5");
		anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(scrnColors = new QButton(App.frame, "Screen Colors"), "split 2, flowx, gapx4, gapy 8");
		add(anyLabel  = new QLabel("    settings will be visible in the bottom far left  "), "flowy");
		scrnColors.setActionCommand("lowerPanel");
		if (App.onMac == false)
		    scrnColors.setBorder(BorderFactory.createEmptyBorder(4, 2, 2, 2));
		scrnColors.setToolTipText("Show the Screen Color Settings  ");

		add(anyLabel  = new QLabel("Colors of the Suit Symbols   "), "gapy 12");
		anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(dkCol0 = new QRadioButton(this, dkColGroup,  bdr1, Cc.deckColorStyle == Cc.Dk__Green_Blue_Red_Black,    "dkCol0", "4 Color - C Green, D Blue,  H Red, S Black  -  As used in actual 4 Color Decks  "), rbInset);
		add(dkCol1 = new QRadioButton(this, dkColGroup,  bdr1, Cc.deckColorStyle == Cc.Dk__Green_Orange_Red_Blue,   "dkCol1", "4 Color - C Green, D Orange, H Red, S Blue  -  Bidding Box Colors  "), rbInset);
		add(dkCol2 = new QRadioButton(this, dkColGroup,  bdr1, Cc.deckColorStyle == Cc.Dk__Green_Blue_Red_Orange,   "dkCol2", "4 Color - C Green, D Blue,  H Red, S Orange   -  Alternate on-line color set  "), rbInset);
		add(dkCol3 = new QRadioButton(this, dkColGroup,  bdr1, Cc.deckColorStyle == Cc.Dk__Black_Red_Red_Black,     "dkCol3", "2 Color - C Black,  D Red,   H Red, S Black     - Traditional  "), rbInset);

		add(anyLabel  = new QLabel("Colors of the Card Symbols  A K Q ...   "), "gapy 15");
		   anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(dkCardsColored  = new QRadioButton(this, dkBkGroup,  bdr1, Cc.deckCardsBlack == 0,  "dkCardsColored",  "Use the above Colors  "), rbInset);
		add(dkCardsAllBlack = new QRadioButton(this, dkBkGroup,  bdr1, Cc.deckCardsBlack == 1,  "dkCardsAllBlack", "Always use Black"), rbInset);

		add(anyLabel  = new QLabel("The Four Suit Symbols"), "gapy 15");
		    anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(showSuitSymbols     = new QCheckBox(this, App.showSuitSymbols,  "Show the four Suit Symbols in each hand display area  "), "gapx 3");

		add(applyDefaults = new QButton(this, "Apply Defaults"), "gapy20, gapx4");
		applyDefaults.setToolTipText("Reset all  Seat Options  to default values  ");
		if (App.onMac == false)
		    applyDefaults.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 4));
		    
		// @formatter:on

	}

	public void actionPerformed(ActionEvent e) {

		Object source = e.getSource();

		if (source == applyDefaults) {
			Cc.deckColorStyle = Cc.Dk__Green_Blue_Red_Black;
			dkCol0.setSelected(true);
			dkCol1.setSelected(false);
			dkCol2.setSelected(false);
			dkCol3.setSelected(false);

			Cc.deckCardsBlack = 0;
			dkCardsColored.setSelected(true);
			dkCardsAllBlack.setSelected(false);

			App.showSuitSymbols = false;
			showSuitSymbols.setSelected(false);

			App.savePreferences();
		}
	}

	/** This listens for the check box changed event */
	public void itemStateChanged(ItemEvent e) {

		boolean b = (e.getStateChange() == ItemEvent.SELECTED);
		Object source = e.getItemSelectable();

		if (source == showSuitSymbols) {
			App.showSuitSymbols = b;
		}

		// we are only interested in the selected values for the buttons
		if (b == false) {
			; // do nothing
		}

		else if (source == dkCol0) {
			Cc.deckColorStyle = Cc.Dk__Green_Blue_Red_Black;
		}
		else if (source == dkCol1) {
			Cc.deckColorStyle = Cc.Dk__Green_Orange_Red_Blue;
		}
		else if (source == dkCol2) {
			Cc.deckColorStyle = Cc.Dk__Green_Blue_Red_Orange;
		}
		else if (source == dkCol3) {
			Cc.deckColorStyle = Cc.Dk__Black_Red_Red_Black;
		}

		else if (source == dkCardsColored) {
			Cc.deckCardsBlack = 0;
		}
		else if (source == dkCardsAllBlack) {
			Cc.deckCardsBlack = 1;
		}

		if (App.allConstructionComplete) {
			App.savePreferences();
//			App.frame.rop.p3_DFC.showSuitSymbolsUpdated();
			App.gbp.c2_2__bbp.suitColorsChanged();
			App.frame.repaint();
		}
	}
}
