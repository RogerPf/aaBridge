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

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.border.Border;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.Aaf;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.model.Cc;
import com.rogerpf.aabridge.model.Dir;

import net.miginfocom.swing.MigLayout;

/**   
 */
class AaRopPrefs3_DFC extends ClickPanel implements ItemListener, ActionListener {

	private static final long serialVersionUID = 1L;

	QLabel anyLabel;

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
	QCheckBox dfcAnonCards;

	QButton applyDefaults;

	public AaRopPrefs3_DFC() {
		setOpaque(true); // remember - we derive from ClickPanel which is setOpaque(false)
//		setBackground(SystemColor.control);

		setLayout(new MigLayout(App.simple + ", flowy", "[][]", "[][]"));

		Border bdr1 = BorderFactory.createEmptyBorder(0, 0, 0, 0);

		// @formatter:off

		add(anyLabel = new QLabel(Aaf.menuOpt_dfc_D), "gapx 15, gapy 5");
		anyLabel.setForeground(Aaa.optionsTitleGreen);
		Font slightlyBiggerFont = anyLabel.getFont().deriveFont(anyLabel.getFont().getSize() * 1.09f);
	    anyLabel.setFont(slightlyBiggerFont);

		add(videoDFC = new QButton(App.frame, Aaf.gT("dfcTab.vid")), "gapy5, gapx 4, split 2, flowx");
		    videoDFC.setActionCommand("playVideo_distrFlashCards");
			if (App.onMac == false)
		        videoDFC.setBorder(BorderFactory.createEmptyBorder(2, 1, 0, 1));
		    videoDFC.setToolTipText(Aaf.gT("dfcTab.vid_TT"));
		    
		add(showDFC = new QButton(App.frame, Aaf.gT("dfcTab.showDfc")), "gapx6");
		    showDFC.setActionCommand("openPage_distrFlashCards");
			if (App.onMac == false)
		        showDFC.setBorder(BorderFactory.createEmptyBorder(2, 4, 0, 4));

		add(anyLabel  = new QLabel(Aaf.gT("dfcTab.trainOnly")), "gapy 12");
		    anyLabel.setForeground(Aaa.optionsTitleGreen);
		
		add(anyLabel  = new QLabel(Aaf.gT("dfcTab.slo")), "gapx 3, gapy 2");
		    anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(order0  = new QRadioButton(this, orderGroup,  bdr1, App.dfcTrainingSuitSort == 0,  "order 0",  Aaf.gT("dfcTab.spadesLong")), "gapx 9");
		add(order1  = new QRadioButton(this, orderGroup,  bdr1, App.dfcTrainingSuitSort == 1,  "order 1",  Aaf.gT("dfcTab.clubsLong")), "gapx 9");
		add(order2  = new QRadioButton(this, orderGroup,  bdr1, App.dfcTrainingSuitSort == 2,  "order 2",  Aaf.gT("dfcTab.random")), "gapx 9");

		add(anyLabel  = new QLabel(Aaf.gT("dfcTab.autoNext")), "gapx3, gapy 6");
		    anyLabel.setForeground(Aaa.optionsTitleGreen);
		    
		add(auto0  = new QRadioButton(this, autoGroup,  bdr1, App.dfcAutoNext == 0,  "auto 0",  Aaf.gT("dfcTab.fast")), "gapx 9, split2, flowx");
		add(anyLabel  = new QLabel(Aaf.gT("dfcTab.tryIt")));
		    anyLabel.setForeground(Cc.RedStrong);
		    
		add(auto1  = new QRadioButton(this, autoGroup,  bdr1, App.dfcAutoNext == 1,  "auto 1",  Aaf.gT("dfcTab.medium")), "gapx 9");
		add(auto2  = new QRadioButton(this, autoGroup,  bdr1, App.dfcAutoNext == 2,  "auto 2",  Aaf.gT("dfcTab.slow")), "gapx 9");
		add(auto3  = new QRadioButton(this, autoGroup,  bdr1, App.dfcAutoNext == 3,  "auto 3",  Aaf.gT("dfcTab.noAutoNext")), "gapx 9");

		add(anyLabel  = new QLabel(Aaf.gT("dfcTab.examOnly")), "gapy 15");
		    anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(youWest  = new QRadioButton(this, youGroup,  bdr1, App.dfcExamYou == Dir.West,  "W",  Dir.getLangDirChar(Dir.West) + ""), "gapx 9, split 3, flowx");
		add(youEast  = new QRadioButton(this, youGroup,  bdr1, App.dfcExamYou == Dir.East,  "E",  Dir.getLangDirChar(Dir.East) + ""), "gapx 6");
		add(anyLabel    = new QLabel(Aaf.gT("dfcTab.youSeat")), "gapx8");
		    anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(youDecl  = new QRadioButton(this, youGroup,  bdr1, App.dfcExamYou == Dir.South,  "Decl",  Aaf.gT("dfcTab.decl")), "gapx 28");
		
		add(anyLabel  = new QLabel(Aaf.gT("dfcTab.botZone")), "gapx3, gapy 10");
		    anyLabel.setForeground(Aaa.optionsTitleGreen);
		add(bottomYou   = new QRadioButton(this, bottomGroup,  bdr1,  App.dfcExamBottomYou,  "You",  Aaf.gT("dfcTab.you")), "gapx 9, split 2, flowx");
		add(bottomDecl  = new QRadioButton(this, bottomGroup,  bdr1, !App.dfcExamBottomYou,  "Decl",  Aaf.gT("dfcTab.decl")), "gapx 6");

		add(anyLabel  = new QLabel(Aaf.gT("dfcTab.diff")), "gapx3, gapy 10");
		    anyLabel.setForeground(Aaa.optionsTitleGreen); 
		add(diff0  = new QRadioButton(this, diffGroup,  bdr1, App.dfcExamDifficulity == 0,  "diff 0",  ""), "gapx 9, split4, flowx");
		add(diff1  = new QRadioButton(this, diffGroup,  bdr1, App.dfcExamDifficulity == 1,  "diff 1",  ""), "gapx 9");
		add(diff2  = new QRadioButton(this, diffGroup,  bdr1, App.dfcExamDifficulity == 2,  "diff 2",  ""), "gapx 9");
		add(diff3  = new QRadioButton(this, diffGroup,  bdr1, App.dfcExamDifficulity == 3,  "diff 3",  ""), "gapx 9");
		add(anyLabel  = new QLabel(Aaf.gT("dfcTab.okHard")), "gapx 8");
		
		add(anyLabel  = new QLabel(Aaf.gT("dfcTab.both")), "gapy 15");
		    anyLabel.setForeground(Aaa.optionsTitleGreen);

		add(dfcHyphenForVoids = new QCheckBox(this, App.dfcHyphenForVoids,    Aaf.gT("dfcTab.hyphen")), "gapx 9, gapy 2");
		    dfcHyphenForVoids.setBorder(bdr1);
		add(dfcAnonCards   = new QCheckBox(this, App.dfcAnonCards,      Aaf.gT("dfcTab.anonCards")), "gapx 9, gapy 2");
		    dfcAnonCards.setBorder(bdr1);
		    
//		add(label  = new QLabel("Exam ONLY  "), "gapy 15");
//		    label.setForeground(Aaa.optionsTitleGreen);
		add(dfcWordsForCount  = new QCheckBox(this, App.dfcWordsForCount,     Aaf.gT("dfcTab.words")), "gapx 9, gapy 6");
	        dfcWordsForCount.setBorder(bdr1);
		
		add(applyDefaults = new QButton(this, Aaf.gT("cmnTab.applyDef")), "gapy20, gapx4");
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

			App.frame.rop.p1_AutoPlay.applyDefaults();
			App.frame.rop.p2_KibSeat.applyDefaults();

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

			App.dfcAnonCards = false;
			dfcAnonCards.setSelected(false);

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
		else if (source == dfcAnonCards) {
			App.dfcAnonCards = b;
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
