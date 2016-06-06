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
import com.rogerpf.aabridge.model.Dir;

/**   
 */
class AaRopPrefs3_DFC extends ClickPanel implements ItemListener, ActionListener {

	private static final long serialVersionUID = 1L;

	QLabel label;

	QButton videoDFC;
	QButton showDFC;

	ButtonGroup dkColGroup = new ButtonGroup();
	QRadioButton dkCol0;
	QRadioButton dkCol1;
	QRadioButton dkCol2;
	QRadioButton dkCol3;

	ButtonGroup dkBkGroup = new ButtonGroup();
	QRadioButton dkCardsColored;
	QRadioButton dkCardsAllBlack;

	ButtonGroup orderGroup = new ButtonGroup();
	QRadioButton order0;
	QRadioButton order1;
	QRadioButton order2;

	ButtonGroup autoGroup = new ButtonGroup();
	QRadioButton auto0;
	QRadioButton auto1;
	QRadioButton auto2;
	QRadioButton auto3;

	ButtonGroup diffGroup = new ButtonGroup();
	QRadioButton diff0;
	QRadioButton diff1;
	QRadioButton diff2;
	QRadioButton diff3;

	ButtonGroup youGroup = new ButtonGroup();
	QRadioButton youWest;
	QRadioButton youEast;
	QRadioButton youDecl;

	ButtonGroup bottomGroup = new ButtonGroup();
	QRadioButton bottomYou;
	QRadioButton bottomDecl;

	QCheckBox dfcWordsForCount;
	QCheckBox dfcHyphenForVoids;
	QCheckBox dfcCardsAsBlobs;

	QButton applyDefaults;

	public AaRopPrefs3_DFC() {
		setOpaque(true); // remember - we derive from ClickPanel which is setOpaque(false)
//		setBackground(SystemColor.control);

		setLayout(new MigLayout(App.simple + ", flowy", "[][]", "[][]"));

		Border bdr1 = BorderFactory.createEmptyBorder(0, 0, 0, 0);

		// @formatter:off

		add(label  = new QLabel("     DFC                      -  Distribution Flash Cards  "), "gapy 5");
		label.setForeground(Aaa.optionsTitleGreen);

		add(videoDFC = new QButton(App.frame, "vid"), "gapy5, gapx 4, split 2, flowx");
		    videoDFC.setActionCommand("playVideo_distrFlashCards");
			if (App.onMac == false)
		        videoDFC.setBorder(BorderFactory.createEmptyBorder(2, 1, 0, 1));
		    videoDFC.setToolTipText("Play the YouTube video about  Distribution Flash Cards  ");
		add(showDFC = new QButton(App.frame, "Show DFC"), "gapx6");
		    showDFC.setActionCommand("openPage_distrFlashCards");
			if (App.onMac == false)
		        showDFC.setBorder(BorderFactory.createEmptyBorder(2, 4, 0, 4));
		    showDFC.setToolTipText("Show the Distribution Flash Card  page  ");	

		add(label  = new QLabel("Training ONLY  "), "gapy 12");
		    label.setForeground(Aaa.optionsTitleGreen);
		
		add(label  = new QLabel("Suit Length Order  "), "gapx 3, gapy 2");
		    label.setForeground(Aaa.optionsTitleGreen);
		add(order0  = new QRadioButton(this, orderGroup,  bdr1, App.dfcTrainingSuitSort == 0,  "order 0",  "Spades the longest suit, Hearts next longest ...  "), "gapx 9");
		add(order1  = new QRadioButton(this, orderGroup,  bdr1, App.dfcTrainingSuitSort == 1,  "order 1",  "Clubs the longest suit, Dimonds next longest ...  "), "gapx 9");
		add(order2  = new QRadioButton(this, orderGroup,  bdr1, App.dfcTrainingSuitSort == 2,  "order 2",  "Random"), "gapx 9");

		add(label  = new QLabel("Auto Next  "), "gapx3, gapy 6");
		    label.setForeground(Aaa.optionsTitleGreen);
		add(auto0  = new QRadioButton(this, autoGroup,  bdr1, App.dfcAutoNext == 0,  "auto 0",  "Fast"), "gapx 9, split2, flowx");
		add(label  = new QLabel(" < try It  "));
		    label.setForeground(Cc.RedStrong);
		add(auto1  = new QRadioButton(this, autoGroup,  bdr1, App.dfcAutoNext == 1,  "auto 1",  "Medium"), "gapx 9");
		add(auto2  = new QRadioButton(this, autoGroup,  bdr1, App.dfcAutoNext == 2,  "auto 2",  "Slow"), "gapx 9");
		add(auto3  = new QRadioButton(this, autoGroup,  bdr1, App.dfcAutoNext == 3,  "auto 3",  "No Auto Next"), "gapx 9");
		
		add(label  = new QLabel("Exam ONLY  "), "gapy 15");
		    label.setForeground(Aaa.optionsTitleGreen);
		add(youWest  = new QRadioButton(this, youGroup,  bdr1, App.dfcExamYou == Dir.West,  "W",  "W"), "gapx 9, split 3, flowx");
		add(youEast  = new QRadioButton(this, youGroup,  bdr1, App.dfcExamYou == Dir.East,  "E",  "E"), "gapx 6");
		add(label    = new QLabel("You  -  the Seat you want to sit in"), "gapx8");
		    label.setForeground(Aaa.optionsTitleGreen);
		add(youDecl  = new QRadioButton(this, youGroup,  bdr1, App.dfcExamYou == Dir.South,  "Decl",  "Decl"), "gapx 28");
		
		add(label  = new QLabel("Bottom Zone - Who is in the 'South seat' zone  "), "gapx3, gapy 10");
		    label.setForeground(Aaa.optionsTitleGreen);
		add(bottomYou   = new QRadioButton(this, bottomGroup,  bdr1,  App.dfcExamBottomYou,  "You",  "You"), "gapx 9, split 2, flowx");
		add(bottomDecl  = new QRadioButton(this, bottomGroup,  bdr1, !App.dfcExamBottomYou,  "Decl",  "Decl"), "gapx 6");

		add(label  = new QLabel("Difficulity  "), "gapx3, gapy 10");
		    label.setForeground(Aaa.optionsTitleGreen); 
		add(diff0  = new QRadioButton(this, diffGroup,  bdr1, App.dfcExamDifficulity == 0,  "diff 0",  "E"),   "gapx 9, split4, flowx");
		add(diff1  = new QRadioButton(this, diffGroup,  bdr1, App.dfcExamDifficulity == 1,  "diff 1",  "M"), "gapx 9");
		add(diff2  = new QRadioButton(this, diffGroup,  bdr1, App.dfcExamDifficulity == 2,  "diff 2",  "H"),   "gapx 9");
		add(diff3  = new QRadioButton(this, diffGroup,  bdr1, App.dfcExamDifficulity == 3,  "diff 3",  "Hardest  -  Impossible ?  "),     "gapx 9");
		
		add(label  = new QLabel("Both Exam and Training  "), "gapy 15");
		    label.setForeground(Aaa.optionsTitleGreen);

		add(dfcHyphenForVoids = new QCheckBox(this, App.dfcHyphenForVoids,    "Use hyphen for voids instead of a feint Zero  "), "gapx 9, gapy 2");
		    dfcHyphenForVoids.setBorder(bdr1);
		add(dfcCardsAsBlobs   = new QCheckBox(this, App.dfcCardsAsBlobs,      "Anon cards - Show all the cards as the greek letter 'alpha'  "), "gapx 9, gapy 2");
		    dfcCardsAsBlobs.setBorder(bdr1);
		    
//		add(label  = new QLabel("Exam ONLY  "), "gapy 15");
//		    label.setForeground(Aaa.optionsTitleGreen);
		add(dfcWordsForCount  = new QCheckBox(this, App.dfcWordsForCount,     "Use words when showing the number of cards in a suit     EXAM only  "), "gapx 9, gapy 6");
	        dfcWordsForCount.setBorder(bdr1);
		
		add(applyDefaults = new QButton(this, "Apply Defaults"), "gapy20, gapx4");
		    applyDefaults.setToolTipText("Reset all  Seat Options  to default values  ");
			if (App.onMac == false)
		        applyDefaults.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 4));

		// @formatter:on

	}

//	public void showSuitSymbolsUpdated() {
//		showSuitSymbols.setSelected(App.showSuitSymbols);
//	}

	public void actionPerformed(ActionEvent e) {

		Object source = e.getSource();
		if (source == applyDefaults) {

			App.frame.rop.p2_SeatChoice.applyDefaultsAction();

			App.dfcTrainingSuitSort = 0;
			order0.setSelected(true);
			order1.setSelected(false);
			order2.setSelected(false);

			App.dfcAutoNext = 1; // 1 = medium don't forget to change the settings below
			auto0.setSelected(false);
			auto1.setSelected(true);
			auto2.setSelected(false);
			auto3.setSelected(false);

			App.dfcExamYou = Dir.West;
			youWest.setSelected(true);
			youEast.setSelected(false);
			youDecl.setSelected(false);

			App.dfcExamBottomYou = true;
			bottomYou.setSelected(true);
			bottomDecl.setSelected(false);

			App.dfcExamDifficulity = 0;
			diff0.setSelected(true);
			diff1.setSelected(false);
			diff2.setSelected(false);
			diff3.setSelected(false);

			App.dfcWordsForCount = true;
			dfcWordsForCount.setSelected(true);

			App.dfcHyphenForVoids = true;
			dfcHyphenForVoids.setSelected(true);

			App.dfcCardsAsBlobs = false;
			dfcCardsAsBlobs.setSelected(false);

			App.savePreferences();

			App.frame.executeCmd("openPage_distrFlashCards");

			App.frame.repaint();
		}
	}

	/** This listens for the check box changed event */
	public void itemStateChanged(ItemEvent e) {

		boolean b = (e.getStateChange() == ItemEvent.SELECTED);
		Object source = e.getItemSelectable();

		if (source == dfcWordsForCount) {
			App.dfcWordsForCount = b;
		}
		else if (source == dfcHyphenForVoids) {
			App.dfcHyphenForVoids = b;
		}
		else if (source == dfcCardsAsBlobs) {
			App.dfcCardsAsBlobs = b;
		}

		// we are only interested in the selected values for the buttons
		if (b == false) {
			; // do nothing
		}

		else if (source == order0) {
			App.dfcTrainingSuitSort = 0;
		}
		else if (source == order1) {
			App.dfcTrainingSuitSort = 1;
		}
		else if (source == order2) {
			App.dfcTrainingSuitSort = 2;
		}

		else if (source == auto0) {
			App.dfcAutoNext = 0;
			App.frame.executeCmd("openPage_distrFlashCards");
		}
		else if (source == auto1) {
			App.dfcAutoNext = 1;
			App.frame.executeCmd("openPage_distrFlashCards");
		}
		else if (source == auto2) {
			App.dfcAutoNext = 2;
			App.frame.executeCmd("openPage_distrFlashCards");
		}
		else if (source == auto3) {
			App.dfcAutoNext = 3;
			App.frame.executeCmd("openPage_distrFlashCards");
		}

		else if (source == diff0) {
			App.dfcExamDifficulity = 0;
		}
		else if (source == diff1) {
			App.dfcExamDifficulity = 1;
		}
		else if (source == diff2) {
			App.dfcExamDifficulity = 2;
		}
		else if (source == diff3) {
			App.dfcExamDifficulity = 3;
		}

		else if (source == youWest) {
			App.dfcExamYou = Dir.West;
		}
		else if (source == youEast) {
			App.dfcExamYou = Dir.East;
		}
		else if (source == youDecl) {
			App.dfcExamYou = Dir.South;
		}

		else if (source == bottomYou) {
			App.dfcExamBottomYou = true;
		}
		else if (source == bottomDecl) {
			App.dfcExamBottomYou = false;
		}

		if (App.allConstructionComplete) {
			App.savePreferences();
//			App.frame.rop.p4_SuitColors.showSuitSymbolsUpdated();
			App.frame.repaint();
		}
	}

}
