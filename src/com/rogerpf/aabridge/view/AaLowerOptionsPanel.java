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

import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.Aaf;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.igf.MassGi_utils;
import com.rogerpf.aabridge.model.Cc;

import net.miginfocom.swing.MigLayout;

/**   
 */
class AaLowerOptionsPanel extends ClickPanel implements ChangeListener, ActionListener, ItemListener {

	private static final long serialVersionUID = 1L;

	JLabel label;

	JPanel b1 = new ClickPanel();
	JPanel b2 = new ClickPanel();
	JPanel b3 = new ClickPanel();
	JPanel b4 = new ClickPanel();

	QCheckBox saveAsCombine_md_sv;

	QCheckBox saveAsProblem;
	QCheckBox saveAsPrOmitOpps;

	ButtonGroup linGroup = new ButtonGroup();
	QRadioButton linfileSaveFormat_0;
	QRadioButton linfileSaveFormat_1;
	QRadioButton linfileSaveFormat_2;
	QRadioButton linfileSaveFormat_3;

	QButton openSavesFolder;
	QButton restoreDefaultSavesFolder;
	QButton browseForSavesFolder;
	JTextField tfSavesFolderDisplay;

	QButton openDownlFolder;
	QButton restoreDefaultDownlFolder;
	QButton browseForDownlFolder;
	JTextField tfDownlFolderDisplay;

	ButtonGroup rotGroup = new ButtonGroup();
	QRadioButton rotateWhenSaving0;
	QRadioButton rotateWhenSaving1;
	QRadioButton rotateWhenSaving2;

	QCheckBox useCreationNameForSave;
	QCheckBox forceSaveMultiDealToSavesFolder;

	ButtonGroup qurGroup = new ButtonGroup();
	QRadioButton fixedQuarterTurns0;
	QRadioButton fixedQuarterTurns1;
	QRadioButton fixedQuarterTurns2;
	QRadioButton fixedQuarterTurns3;

	QCheckBox force_savename_xxx;

	QButton multiDealSaveAsPbn_noCardPlay;

	QButton multiDealSaveAsLin;
	QButton multiSeparateAsLin;
	QCheckBox renumberDealsLin;
	QCheckBox bboUpStripped;
	QCheckBox linSaveRotateDec;
	QCheckBox includeRotationsSetBelow;
	QCheckBox prependYyySavePrefix;

	JLabel labRot1;
	JLabel labRot2;

	JSlider colorIntensity;
	JSlider colorTint;
	QButton resetColors;

	QButton joinLinFiles;

	JSlider mainWidth;
	JSlider playSpeed;
	JSlider bidSpeed;
	JSlider eotDelay;

	JLabel bidSpeelabel;

	QButton resetSpeeds;

	public void realSaves_folderNowAvailable() {
		tfSavesFolderDisplay.setText(App.realSaves_folder);
		tfDownlFolderDisplay.setText(App.downloads_folder);
	}

	public void applyDefaults() {
		// this is fake and currently only does ...	
		showButtonStates();
	}

	public void showButtonStates() {

		boolean en = !App.isStudyDeal();

		multiDealSaveAsPbn_noCardPlay.setEnabled(en);

		multiDealSaveAsLin.setEnabled(en);
		multiSeparateAsLin.setEnabled(en);

		prependYyySavePrefix.setEnabled(en);

	}

	public AaLowerOptionsPanel() {
		// setBackground(SystemColor.control); no don't use this

		b1.setLayout(new MigLayout(App.simple + ", flowy", "[]", "[]"));
		b2.setLayout(new MigLayout(App.simple + ", flowy", "[]", "[]"));
		b3.setLayout(new MigLayout(App.simple + ", flowy", "[]", "[]"));
		b4.setLayout(new MigLayout(App.simple + ", flowy", "[]", "[]"));

		setLayout(new MigLayout(App.simple + ", flowx", "90  push [] 40 push [] 20 push [] 20 push [] 5 push ", "[]push"));

		b1.add(label = new QLabel(Aaf.gT("botPanel.savesF"), ""), "flowx, split2");
		label.setForeground(Aaa.optionsTitleGreen);
		Font slightlyBiggerFont = label.getFont().deriveFont(label.getFont().getSize() * 1.09f);
		label.setFont(slightlyBiggerFont);

		b1.add(openSavesFolder = new QButton(this, Aaf.gT("botPanel.openSF")), "gapx20, flowx");
		openSavesFolder.setToolTipText(Aaf.gT("botPanel.openSF_TT"));
		if (App.onMac == false)
			openSavesFolder.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 4));

		b1.add(tfSavesFolderDisplay = new JTextField((App.onMacOrLinux ? 23 : 19)), "gapy 6");
		tfSavesFolderDisplay.setEditable(false);

		b1.add(force_savename_xxx = new QCheckBox(this, App.force_savename_xxx, Aaf.gT("botPanel.forceName") + "  =  \"" + App.xxx_lin_name + "\"", ""),
				"gapx 4");

		b1.add(browseForSavesFolder = new QButton(this, Aaf.gT("botPanel.chooseF")), "gapy 6");
		browseForSavesFolder.setToolTipText(Aaf.gT("botPanel.chooseF_TT"));
		if (App.onMac == false)
			browseForSavesFolder.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 4));

		b1.add(label = new QLabel(Aaf.gT("botPanel.restartAfter"), ""), "gapy 4, span 3, split 3");
		label.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));

		b1.add(restoreDefaultSavesFolder = new QButton(this, Aaf.gT("botPanel.restoreSavesF")), "gapy 6");
		if (App.onMac == false)
			restoreDefaultSavesFolder.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 4));

		b1.add(label = new QLabel("______________________________", ""), "center");
		label.setBorder(BorderFactory.createEmptyBorder(2, 2, 0, 0));

		b1.add(label = new QLabel(Aaf.gT("botPanel.downlF"), ""), "gapy 12, flowx, split2");
		label.setForeground(Aaa.optionsTitleGreen);
		label.setFont(slightlyBiggerFont);

		b1.add(openDownlFolder = new QButton(this, Aaf.gT("botPanel.openDownlF")), "gapx20, flowx");
		openDownlFolder.setToolTipText(Aaf.gT("botPanel.openDownlF_TT"));
		if (App.onMac == false)
			openDownlFolder.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 4));

		b1.add(tfDownlFolderDisplay = new JTextField((App.onMacOrLinux ? 23 : 19)), "gapy 6");
		tfDownlFolderDisplay.setEditable(false);

		b1.add(browseForDownlFolder = new QButton(this, Aaf.gT("botPanel.chooseDownlF")), "gapy 8");
		browseForDownlFolder.setToolTipText(Aaf.gT("botPanel.chooseDownlF_TT"));
		if (App.onMac == false)
			browseForDownlFolder.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 4));

		b1.add(restoreDefaultDownlFolder = new QButton(this, Aaf.gT("botPanel.restoreDownlF")), "gapy 8");
		if (App.onMac == false)
			restoreDefaultDownlFolder.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 4));

		add(b1, "growy");

//		------- next column ---------------------------

		b2.add(linfileSaveFormat_0 = new QRadioButton(this, linGroup, null, App.linfileSaveFormat == 0, "", Aaf.gT("botPanel.linFmt_BBO")),
				"split 2, gapy 2, gapx 0, flowx");

		b2.add(bboUpStripped = new QCheckBox(this, App.bboUpStripped, Aaf.gT("botPanel.bboUpStripped"), ""), "gapy0, gapx 15");
		bboUpStripped.setForeground(Aaa.navDarkText);
		bboUpStripped.setToolTipText(Aaf.gT("botPanel.bboUpStripped_TT"));
		bboUpStripped.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

		b2.add(linfileSaveFormat_1 = new QRadioButton(this, linGroup, null, App.linfileSaveFormat == 1, "", Aaf.gT("botPanel.linFmt_Std")), "gapx 0");
		b2.add(linfileSaveFormat_2 = new QRadioButton(this, linGroup, null, App.linfileSaveFormat == 2, "", Aaf.gT("botPanel.linFmt_Prob")), "gapx 0");
		b2.add(linfileSaveFormat_3 = new QRadioButton(this, linGroup, null, App.linfileSaveFormat == 3, "", Aaf.gT("botPanel.linFmt_PrNoOpps")), "gapx 0");

		linfileSaveFormat_0.setToolTipText(Aaf.gT("botPanel.linFmt_BBO_TT"));
		linfileSaveFormat_1.setToolTipText(Aaf.gT("botPanel.linFmt_Std_TT"));
		linfileSaveFormat_2.setToolTipText(Aaf.gT("botPanel.linFmt_Prob_TT"));
		linfileSaveFormat_3.setToolTipText(Aaf.gT("botPanel.linFmt_PrNoOpps_TT"));

		if (App.onMac == false) {
			Border bf = BorderFactory.createEmptyBorder(5, 0, 0, 0);
			linfileSaveFormat_0.setBorder(bf);
			linfileSaveFormat_1.setBorder(bf);
			linfileSaveFormat_2.setBorder(bf);
			linfileSaveFormat_3.setBorder(bf);
		}

		// MULTI DEAL SAVE 
		b2.add(multiDealSaveAsLin = new QButton(this, Aaf.gT("botPanel.multiDealSave")), "gapy 7, gapx 12, split 2, flowx");  // split 2, flowx
		multiDealSaveAsLin.setToolTipText(Aaf.gT("botPanel.multiDealSave_TT"));
		if (App.onMac == false)
			multiDealSaveAsLin.setBorder(BorderFactory.createEmptyBorder(5, 8, 3, 8));

		// MULTI SEPARTATE 
		b2.add(multiSeparateAsLin = new QButton(this, Aaf.gT("botPanel.multiSeparate")), "gapy 7, gapx 12, split 2, flowx");  // split 2, flowx
		multiSeparateAsLin.setToolTipText(Aaf.gT("botPanel.multiSeparate_TT"));
		if (App.onMac == false)
			multiSeparateAsLin.setBorder(BorderFactory.createEmptyBorder(5, 8, 3, 8));

		//  RE-NUMBER
		b2.add(renumberDealsLin = new QCheckBox(this, App.renumberDealsLin, Aaf.gT("botPanel.renumberOther"), ""), "gapy 3,  gapx 6, split 3, flowx");
		if (App.onMac == false) {
			renumberDealsLin.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
		}

		//  AS PBN
		b2.add(multiDealSaveAsPbn_noCardPlay = new QButton(this, Aaf.gT("botPanel.multiSaveAsPbn")), "gapx 30");
		multiDealSaveAsPbn_noCardPlay.setToolTipText(Aaf.gT("botPanel.multiSaveAsPbn_TT"));
		if (App.onMac == false)
			multiDealSaveAsPbn_noCardPlay.setBorder(BorderFactory.createEmptyBorder(2, 5, 0, 5));

		//  PRE (prefix)
		b2.add(prependYyySavePrefix = new QCheckBox(this, App.prependYyySavePrefix, Aaf.gT("botPanel.incYyySavePrefix"),
				Aaf.gT("botPanel.incYyySavePrefix_TT")), "gapx 16");
		//		prependYyySavePrefix.setForeground(Aaa.navDarkText);
		if (App.onMac == false) {
			prependYyySavePrefix.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
		}

		//  Rotate DECLARER to South
		b2.add(linSaveRotateDec = new QCheckBox(this, App.linSaveRotateDec, Aaf.gT("botPanel.linSaveRotateDec"), Aaf.gT("botPanel.linSaveRotateDec_TT")),
				"gapx 6");
		if (App.onMac == false) {
			linSaveRotateDec.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
		}

		b2.add(includeRotationsSetBelow = new QCheckBox(this, App.includeRotationsSetBelow, Aaf.gT("botPanel.linSaveIncRotBelow"),
				Aaf.gT("botPanel.linSaveIncRotBelow_TT")), "gapx 6, flowx");
		if (App.onMac == false) {
			includeRotationsSetBelow.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
		}

		b2.add(labRot1 = new QLabel(Aaf.gT("botPanel.rotate")), "gapx 12, gapy 9, split 4, flowx");
		labRot1.setForeground(Aaa.optionsTitleGreen);
		labRot1.setToolTipText(Aaf.gT("botPanel.rotate_TT"));

		b2.add(rotateWhenSaving0 = new QRadioButton(this, rotGroup, null, App.rotateWhenSaving == 0, "", "0\u00b0"), "gapx 18");
		b2.add(rotateWhenSaving1 = new QRadioButton(this, rotGroup, null, App.rotateWhenSaving == 1, "", "90\u00b0"), "gapx 15");
		b2.add(rotateWhenSaving2 = new QRadioButton(this, rotGroup, null, App.rotateWhenSaving == 2, "", "180\u00b0"), "gapx 15");

//		if (App.onMac == false) {
//			rotateWhenSaving0.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
//			rotateWhenSaving1.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
//			rotateWhenSaving2.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
//		}		

		b2.add(labRot2 = new QLabel(Aaf.gT("botPanel.fixed90s"), ""), "gapx 12, gapy 0, split 5, flowx");
		labRot2.setForeground(Aaa.optionsTitleGreen);
		labRot2.setToolTipText(Aaf.gT("botPanel.fixed90s_TT"));

		b2.add(fixedQuarterTurns0 = new QRadioButton(this, qurGroup, null, App.fixedQuarterTurns == 0, "", "0"), "gapx " + (App.onMac ? "5" : "10"));
		b2.add(fixedQuarterTurns1 = new QRadioButton(this, qurGroup, null, App.fixedQuarterTurns == 1, "", "1"), "gapx " + (App.onMac ? "3" : "8"));
		b2.add(fixedQuarterTurns2 = new QRadioButton(this, qurGroup, null, App.fixedQuarterTurns == 2, "", "2"), "gapx " + (App.onMac ? "3" : "8"));
		b2.add(fixedQuarterTurns3 = new QRadioButton(this, qurGroup, null, App.fixedQuarterTurns == 3, "", "3"), "gapx " + (App.onMac ? "3" : "8"));

		b2.add(useCreationNameForSave = new QCheckBox(this, App.useCreationNameForSave, Aaf.gT("botPanel.creationNameSave"),
				Aaf.gT("botPanel.creationNameSave_TT")), ", gapy 5, gapx 2");

		b2.add(forceSaveMultiDealToSavesFolder = new QCheckBox(this, App.forceSaveMultiDealToSavesFolder, Aaf.gT("botPanel.forceMulti"),
				Aaf.gT("botPanel.forceMulti_TT")), ", gapy 0, gapx 2");

		add(b2, "growy");

//		------- next column ---------------------------

		Hashtable<Integer, JLabel> colLab = new Hashtable<Integer, JLabel>();
		colLab.put(new Integer(-200), new JLabel(Aaf.gT("botPanel.dark")));
		colLab.put(new Integer(+170), new JLabel(Aaf.gT("botPanel.pastel")));

		b3.add(new JLabel(Aaf.gT("botPanel.scrCols")), "gapy 2, center");
		b3.add(colorIntensity = new JSlider(JSlider.HORIZONTAL, -255, +255, 0), "gapy 1, wmin 120, wmax 150");
		colorIntensity.addChangeListener(this);
		colorIntensity.setLabelTable(colLab);
		colorIntensity.setPaintLabels(true);
		colorIntensity.setValue(App.colorIntensity);

		b3.add(label = new QLabel(" ", ""), "center");

		Hashtable<Integer, JLabel> tintLab = new Hashtable<Integer, JLabel>();
		tintLab.put(new Integer(-25), new JLabel(Aaf.gT("botPanel.orange")));
		tintLab.put(new Integer(+35), new JLabel(Aaf.gT("botPanel.blue")));

		b3.add(new JLabel(Aaf.gT("botPanel.scrTint")), "gapy 10, center");
		b3.add(colorTint = new JSlider(JSlider.HORIZONTAL, -50, +50, 0), "wmin 120, wmax 150");
		colorTint.addChangeListener(this);
		colorTint.setLabelTable(tintLab);
		colorTint.setPaintLabels(true);
		colorTint.setValue(App.colorTint);

		b3.add(resetColors = new QButton(this, Aaf.gT("botPanel.resetCols")), "gapy 12, center");
		if (App.onMac == false)
			resetColors.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 4));

		b3.add(label = new QLabel("________________", ""), "center,  gapy 6");
		label.setBorder(BorderFactory.createEmptyBorder(2, 2, 0, 0));

		//  MERGE LIN FILES - heading A
		b3.add(label = new QLabel(Aaf.gT("botPanel.joinLinHeadA"), Aaf.gT("botPanel.joinLinFiles_TT")), "center, gapy 25");
		label.setForeground(Aaa.optionsTitleGreen);

		//  MERGE LIN FILES - button
		b3.add(joinLinFiles = new QButton(this, Aaf.gT("botPanel.joinLinFiles")), "gapy 0, center");
		joinLinFiles.setToolTipText(Aaf.gT("botPanel.joinLinFiles_TT"));
		if (App.onMac == false)
			joinLinFiles.setBorder(BorderFactory.createEmptyBorder(4, 5, 2, 5));

		//  MERGE LIN FILES - heading B
		b3.add(label = new QLabel(Aaf.gT("botPanel.joinLinHeadB"), Aaf.gT("botPanel.joinLinFiles_TT")), "gapy 2, center");
		label.setForeground(Aaa.optionsTitleGreen);

		add(b3, "growy");

//		------- next column ---------------------------

//		Hashtable<Integer, JLabel> labTab0 = new Hashtable<Integer, JLabel>();
//		labTab0.put(new Integer(10), new JLabel(Aaf.gT("botPanel.std")));
//		labTab0.put(new Integer(90), new JLabel(Aaf.gT("botPanel.wide")));

		String mWidth_TT = Aaf.gT("botPanel.mainWidth_TT");

		b4.add(new QLabel(Aaf.gT("botPanel.mainWidth"), mWidth_TT), "gapy 2, center");
		b4.add(mainWidth = new JSlider(100, 120, App.ratioFiddle), "gapy 1, center, wmin 130, wmax 130");
		mainWidth.setToolTipText(mWidth_TT);
		mainWidth.addChangeListener(this);
//		mainWidth.setLabelTable(labTab0);
//		mainWidth.setPaintLabels(true);
		mainWidth.setMinorTickSpacing(1);
		mainWidth.setMajorTickSpacing(5);
		mainWidth.setPaintTicks(true);
		mainWidth.setSnapToTicks(true);
		b4.add(label = new QLabel(Aaf.gT("botPanel.std"), mWidth_TT), "split2, gapx 7, flowx, left, grow");
		b4.add(label = new QLabel(Aaf.gT("botPanel.wide") + "  ", mWidth_TT), "right");

		b4.add(label = new QLabel(" ", ""), "center");

		Hashtable<Integer, JLabel> labTab1 = new Hashtable<Integer, JLabel>();
		labTab1.put(new Integer(10), new JLabel(Aaf.gT("botPanel.slow")));
		labTab1.put(new Integer(90), new JLabel(Aaf.gT("botPanel.fast")));

		b4.add(new JLabel(Aaf.gT("botPanel.playSpeed")), "gapy 10, center");
		b4.add(playSpeed = new JSlider(JSlider.HORIZONTAL), "center, wmin 120, wmax 150");
		playSpeed.addChangeListener(this);
		playSpeed.setLabelTable(labTab1);
		playSpeed.setPaintLabels(true);
		playSpeed.setValue(pluseToPercent(App.playPluseTimerMs));

		b4.add(label = new QLabel(" ", ""), "center");

		b4.add(label = new QLabel(Aaf.gT("botPanel.whenWaitOff")), "gapy 10, center");
		// label.setForeground(Aaa.optionsTitleGreen);

		Hashtable<Integer, JLabel> labTab2 = new Hashtable<Integer, JLabel>();
		labTab2.put(new Integer(2), new JLabel(Aaf.gT("botPanel.noExtra")));
		labTab2.put(new Integer(7), new JLabel(Aaf.gT("botPanel.longer")));

		String ttext = Aaf.gT("botPanel.eotPause_TT");

		b4.add(label = new JLabel(), "center, ");
		label.setToolTipText(ttext);
		b4.add(eotDelay = new JSlider(JSlider.HORIZONTAL), "wmax 150");
		eotDelay.setToolTipText(ttext);
		eotDelay.addChangeListener(this);
		eotDelay.setInverted(true);
		eotDelay.setLabelTable(labTab2);
		eotDelay.setPaintLabels(true);
		eotDelay.setValue(App.eotExtendedDisplay);
		eotDelay.setMinimum(0);
		eotDelay.setMaximum(9);

		b4.add(label = new QLabel(" ", ""), "center");

		b4.add(resetSpeeds = new QButton(this, Aaf.gT("botPanel.resetEtc")), "gapy 12, center");
		if (App.onMac == false)
			resetSpeeds.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 4));

		add(b4, "growy");

		setRotateEnDis(); // here may not be good
	}

	void setRotateEnDis() {
		if (App.includeRotationsSetBelow) {
			App.linfileSaveFormat = 0;
			linfileSaveFormat_0.setSelected(true);
			linfileSaveFormat_1.setSelected(false);
			linfileSaveFormat_2.setSelected(false);
			linfileSaveFormat_3.setSelected(false);
		}

		boolean b = App.includeRotationsSetBelow;

		multiDealSaveAsPbn_noCardPlay.setEnabled(!b);

		linfileSaveFormat_1.setEnabled(!b);
		linfileSaveFormat_2.setEnabled(!b);
		linfileSaveFormat_3.setEnabled(!b);

		labRot1.setEnabled(b);
		rotateWhenSaving0.setEnabled(b);
		rotateWhenSaving1.setEnabled(b);
		rotateWhenSaving2.setEnabled(b);

		labRot2.setEnabled(b);
		fixedQuarterTurns0.setEnabled(b);
		fixedQuarterTurns1.setEnabled(b);
		fixedQuarterTurns2.setEnabled(b);
		fixedQuarterTurns3.setEnabled(b);
	}

	Timer colorIntensityDelayTimer = new Timer(100, new ActionListener() {
		public void actionPerformed(ActionEvent evt) {

			colorIntensityDelayTimer.stop();
			Cc.intensitySliderChange();
			colorIntensity.setValue(App.colorIntensity);
			colorTint.setValue(App.colorTint);
			App.colorIntensityChange();
			App.frame.repaint();
		}
	});

	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == joinLinFiles) {
			try {
				MassGi_utils.joinLinFiles();
			} catch (Exception ee) {
			}
		}

		if (e.getSource() == multiDealSaveAsPbn_noCardPlay) {
			try {
				MassGi_utils.multiDealSaveAsPbn_noCardPlay();
			} catch (Exception ee) {
			}
		}

		if (e.getSource() == multiDealSaveAsLin) {
			try {
				MassGi_utils.multiDealSaveAsLin(false /* separate */);
			} catch (Exception ee) {
			}
		}
		
		if (e.getSource() == multiSeparateAsLin) {
			try {
				MassGi_utils.multiDealSaveAsLin(true /* separate */);
			} catch (Exception ee) {
			}
		}


		else if (e.getSource() == resetColors) {

			App.colorIntensity = App.defaultColorIntensity;
			App.colorTint = 0;
			if (App.allConstructionComplete) {
				colorIntensityDelayTimer.restart();
			}
		}

		else if (e.getSource() == resetSpeeds) {
			App.playPluseTimerMs = App.defaultplayPluseTimerMs;
			playSpeed.setValue(pluseToPercent(App.playPluseTimerMs));

			App.eotExtendedDisplay = App.defaultEotExtendedDisplay;
			eotDelay.setValue(App.eotExtendedDisplay);

			App.ratioFiddle = (App.onLinux) ? App.defaultLinux_ratioFiddle : 100;
			mainWidth.setValue(App.ratioFiddle);
			App.frame.calcAllMigLayoutStrings();
			App.aaBookPanel.playloadPanel_resized_check();
			App.frame.payloadPanelShaker();
		}

		else if (e.getSource() == restoreDefaultSavesFolder) {
			App.realSaves_folder = App.defaultSaves_folder;
			tfSavesFolderDisplay.setText(App.realSaves_folder);
		}

		else if (e.getSource() == restoreDefaultDownlFolder) {
			App.downloads_folder = App.default_downloads_folder;
			tfDownlFolderDisplay.setText(App.downloads_folder);
		}

		else if (e.getSource() == browseForSavesFolder) {
			JFileChooser fc = new JFileChooser();
			fc.setCurrentDirectory(new java.io.File(App.realSaves_folder)); // start at folder
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnVal = fc.showSaveDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File yourFolder = fc.getSelectedFile();
				if (yourFolder != null) {
					try {
						App.realSaves_folder = yourFolder.getCanonicalPath() + File.separator;
						tfSavesFolderDisplay.setText(App.realSaves_folder);
						yourFolder.mkdir();
					} catch (IOException e1) {
					}
				}
			}
		}

		else if (e.getSource() == browseForDownlFolder) {
			JFileChooser fc = new JFileChooser();
			fc.setCurrentDirectory(new java.io.File(App.downloads_folder)); // start at folder
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnVal = fc.showSaveDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File yourFolder = fc.getSelectedFile();
				if (yourFolder != null) {
					try {
						App.downloads_folder = yourFolder.getCanonicalPath() + File.separator;
						tfDownlFolderDisplay.setText(App.downloads_folder);
						yourFolder.mkdir();
					} catch (IOException e1) {
					}
				}
			}
		}

		else if (e.getSource() == openSavesFolder) {
			try {
				Desktop.getDesktop().open(new File(App.realSaves_folder));
			} catch (IOException e1) {
			}
		}

		else if (e.getSource() == openDownlFolder) {
			try {
				Desktop.getDesktop().open(new File(App.downloads_folder));
			} catch (IOException e1) {
			}
		}

	}

	public void stateChanged(ChangeEvent e) {

		JSlider source = (JSlider) e.getSource();

		if (source == colorIntensity) {
			if (App.allConstructionComplete) {
				source.getValue();
				App.colorIntensity = source.getValue();
				colorIntensityDelayTimer.restart();
			}
		}
		if (source == colorTint) {
			if (App.allConstructionComplete) {
				source.getValue();
				App.colorTint = source.getValue();
				colorIntensityDelayTimer.restart();
			}
		}
		else if (source == playSpeed) {
			int pluse = percentToPluse(source.getValue());
			App.playPluseTimerMs = pluse;
		}
		else if (source == mainWidth) {
			App.ratioFiddle = source.getValue();
			if (App.allConstructionComplete) {
				App.frame.calcAllMigLayoutStrings();
				App.aaBookPanel.playloadPanel_resized_check();
//				App.dealMajorChange();
//				App.gbp.matchPanelsToDealState();
//				App.frame.invalidate();
//				App.frame.repaint();
				App.frame.payloadPanelShaker();
			}
		}
		else if (source == eotDelay) {
			App.eotExtendedDisplay = source.getValue();
		}
	}

	int h = 1300;
	int min = 100;

	int percentToPluse(int pc) {
		return min + (h * (100 - pc)) / 100;
	}

	int pluseToPercent(int pluse) {
		return 100 - (100 * (pluse - min)) / h;
	}

//	int ratioFiddleToPercent(float rf) {
//		if (rf < 1.0f)
//			rf = 1.0f;
//		if (rf > 1.2f)
//			rf = 1.2f;
//		int v = (int) ((rf - 1) * 500);
//
//		return v;
//	}
//
//	float percentToRatioFiddle(int val) {
//		return 1.0f + (float) val / 500.0f;
//	}

	public void itemStateChanged(ItemEvent e) {
		boolean b = (e.getStateChange() == ItemEvent.SELECTED);
		Object source = e.getItemSelectable();

		// @formatter:off
		if (source == linfileSaveFormat_0) {
			App.linfileSaveFormat = 0; // BBO
		}
		else if (source == linfileSaveFormat_1) {
			App.linfileSaveFormat = 1; // Std
		}
		else if (source == linfileSaveFormat_2) {
			App.linfileSaveFormat = 2; // Problem
		}
		else if (source == linfileSaveFormat_3) {
			App.linfileSaveFormat = 3; // Problem no Opps 
		}
		else if (source == rotateWhenSaving0) {
			App.rotateWhenSaving = 0;
		}
		else if (source == rotateWhenSaving1) {
			App.rotateWhenSaving = 1;
		}
		else if (source == rotateWhenSaving2) {
			App.rotateWhenSaving = 2;
		}
		else if (source == fixedQuarterTurns0) {
			App.fixedQuarterTurns = 0;
		}
		else if (source == fixedQuarterTurns1) {
			App.fixedQuarterTurns = 1;
		}
		else if (source == fixedQuarterTurns2) {
			App.fixedQuarterTurns = 2;
		}
		else if (source == fixedQuarterTurns3) {
			App.fixedQuarterTurns = 3;
		}
		else if (source == useCreationNameForSave) {
			App.useCreationNameForSave = b;
		}
		else if (source == forceSaveMultiDealToSavesFolder) {
			App.forceSaveMultiDealToSavesFolder = b;
		}
		else if (source == renumberDealsLin) {
			App.renumberDealsLin = b;
		}
		else if (source == linSaveRotateDec) {
			App.linSaveRotateDec = b;
		}
		else if (source == bboUpStripped) {
			App.bboUpStripped = b;
		}
		else if (source == force_savename_xxx) {
			App.force_savename_xxx = b;
		}
		else if (source == includeRotationsSetBelow) {
			App.includeRotationsSetBelow = b;
		}
		else if (source == prependYyySavePrefix) {
			App.prependYyySavePrefix = b;
		}		
		
		// @formatter:on

		if (App.allConstructionComplete) {
			setRotateEnDis();
			App.savePreferences();
			App.frame.repaint();
		}

	}

}
