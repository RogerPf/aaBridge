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

/**   
 */
class AaRopPrefs3_SuitColors extends ClickPanel implements ItemListener {

	private static final long serialVersionUID = 1L;

	QLabel label;
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

	public AaRopPrefs3_SuitColors() {
		setOpaque(true); // remember - we derive from ClickPanel which is setOpaque(false)
		setBackground(SystemColor.control);

		setLayout(new MigLayout(App.simple + ", flowy", "[][]", "[][]"));

		String rbInset = "gapx 7";

		Border bdr1 = BorderFactory.createEmptyBorder(1, 0, 1, 0);

		// @formatter:off

		add(label  = new QLabel("Suit Colors    -    Colors of the Suits and Suit symbols"), "gapy 5");
		label.setForeground(Aaa.optionsTitleGreen);
		add(scrnColors = new QButton(App.frame, "Screen Colors"), "split 2, flowx, gapx8, gapy");
		add(label  = new QLabel("setting will be visible in the bottom left  "), "flowy");
		scrnColors.setActionCommand("lowerPanel");
		scrnColors.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 4));
		scrnColors.setToolTipText("Show the Screen Color Settings  ");

		add(label  = new QLabel("Colors of the Suit Symbols   "), "gapy 8");
		label.setForeground(Aaa.optionsTitleGreen);
		add(dkCol0 = new QRadioButton(this, dkColGroup,  bdr1, Cc.deckColorStyle == Cc.Dk__Green_Blue_Red_Black,    "dkCol0", "4 Color - C Green, D Blue,  H Red, S Black  -  As used in actual 4 Color Decks  "), rbInset);
		add(dkCol1 = new QRadioButton(this, dkColGroup,  bdr1, Cc.deckColorStyle == Cc.Dk__Green_Orange_Red_Blue,   "dkCol1", "4 Color - C Green, D Orange, H Red, S Blue  -  Bidding Box Colors  "), rbInset);
		add(dkCol2 = new QRadioButton(this, dkColGroup,  bdr1, Cc.deckColorStyle == Cc.Dk__Green_Blue_Red_Orange,   "dkCol2", "4 Color - C Green, D Blue,  H Red, S Orange   -  Alternate on-line color set  "), rbInset);
		add(dkCol3 = new QRadioButton(this, dkColGroup,  bdr1, Cc.deckColorStyle == Cc.Dk__Black_Red_Red_Black,     "dkCol3", "2 Color - C Black,  D Red,   H Red, S Black     - Traditional  "), rbInset);

		add(label  = new QLabel("Colors of the Card Symbols  A K Q ...   "), "gapy 15");
		label.setForeground(Aaa.optionsTitleGreen);
		add(dkCardsColored  = new QRadioButton(this, dkBkGroup,  bdr1, Cc.deckCardsBlack == 0,  "dkCardsColored",  "Use the above Colors  "), rbInset);
		add(dkCardsAllBlack = new QRadioButton(this, dkBkGroup,  bdr1, Cc.deckCardsBlack == 1,  "dkCardsAllBlack", "Always use Black"), rbInset);

		add(label  = new QLabel("The Four Suit Symbols"), "gapy 15");
		label.setForeground(Aaa.optionsTitleGreen);
		add(showSuitSymbols     = new QCheckBox(this, App.showSuitSymbols,  "Show the four Suit Symbols in each hand display area  "), "gapx 3");

		// @formatter:on

	}

//	public void showSuitSymbolsUpdated() {
//		showSuitSymbols.setSelected(App.showSuitSymbols);
//	}

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
//			App.frame.rop.p4_DFC.showSuitSymbolsUpdated();
			App.gbp.c2_2__bbp.suitColorsChanged();
			App.frame.repaint();
		}
	}
}
