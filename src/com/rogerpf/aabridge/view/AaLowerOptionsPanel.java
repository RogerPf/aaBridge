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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.Aaf;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.igf.MassGi_utils;
import com.rogerpf.aabridge.model.Cc;

/**   
 */
class AaLowerOptionsPanel extends ClickPanel implements ChangeListener, ActionListener, ItemListener {

	private static final long serialVersionUID = 1L;

	JLabel label;

	JPanel b1 = new ClickPanel();
	JPanel b2 = new ClickPanel();
	JPanel b3 = new ClickPanel();
	JPanel b4 = new ClickPanel();

	QCheckBox saveAsBboUploadFormat;
	QCheckBox saveAsCombine_md_sv;
	QCheckBox saveAsLowerCaseSuit;
	JTextField tfSavesFolderDisplay;
	QCheckBox saveAsBboUploadExtraS;

	QButton openSavesFolder;
	QButton useDefaultSavesFolder;
	QButton browseForSavesFolder;

	ButtonGroup rotGroup = new ButtonGroup();
	QRadioButton pbnRotateWhenLoading0;
	QRadioButton pbnRotateWhenLoading1;
	QRadioButton pbnRotateWhenLoading2;

	QCheckBox pbnRenumberAtReadin;
	QCheckBox alwaysSavePbnToLinToAutosavesFolder;

	ButtonGroup qurGroup = new ButtonGroup();
	QRadioButton pbnFixedQuarterTurns0;
	QRadioButton pbnFixedQuarterTurns1;
	QRadioButton pbnFixedQuarterTurns2;
	QRadioButton pbnFixedQuarterTurns3;

	QButton openAutosavesFolder;

	QCheckBox force_savename_xxx;
	QCheckBox pbnRenumberDeals;

	QButton saveDealsAsPbnNoPlay;

	QCheckBox movieBidFlowDoesFlow;
	JSlider colorIntensity;
	QCheckBox difColorInsideDeal;
	JSlider colorTint;
	QButton resetColors;

	JSlider playSpeed;
	JSlider bidSpeed;
	JSlider eotDelay;

	JLabel bidSpeelabel;

	QButton resetSpeeds;

	public void realSavesPathNowAvailable() {
		tfSavesFolderDisplay.setText(App.realSavesPath);
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

		b1.add(label = new QLabel(Aaf.gT("botPanel.restartAfter"), ""), "gapy 6, span 3, split 3");
		label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

		b1.add(useDefaultSavesFolder = new QButton(this, Aaf.gT("botPanel.restSavesF")), "gapy 12");
		if (App.onMac == false)
			useDefaultSavesFolder.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 4));

		b1.add(label = new QLabel("_______________________________", ""), "center, gapy4");
		label.setForeground(Aaa.optionsTitleGreen);

		b1.add(saveDealsAsPbnNoPlay = new QButton(this, Aaf.gT("botPanel.saveDealsAs") + "  .pbn"), "gapy10, gapx4, split2, flowx");
		saveDealsAsPbnNoPlay.setToolTipText(Aaf.gT("botPanel.saveDealsAs_TT"));
		if (App.onMac == false)
			saveDealsAsPbnNoPlay.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 4));

		b1.add(pbnRenumberDeals = new QCheckBox(this, App.pbnRenumberDeals, Aaf.gT("botPanel.renumber"), ""), "gapx 4");

		add(b1, "growy");

//		------- next column ---------------------------

		b2.add(saveAsBboUploadFormat = new QCheckBox(this, App.saveAsBboUploadFormat, Aaf.gT("botPanel.bboFmt"), Aaf.gT("botPanel.bboFmt_TT")),
				"flowy, span 2, split 2");
		if (App.onMac == false)
			saveAsBboUploadFormat.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 4));

		b2.add(saveAsBboUploadExtraS = new QCheckBox(this, App.saveAsBboUploadExtraS, Aaf.gT("botPanel.extraSpaces"), Aaf.gT("botPanel.extraSpaces_TT")),
				"gapx 20");
		saveAsBboUploadExtraS.setEnabled(App.saveAsBboUploadFormat);

		b2.add(saveAsCombine_md_sv = new QCheckBox(this, App.saveAsCombine_md_sv, Aaf.gT("botPanel.mdSv"), Aaf.gT("botPanel.mdSv_TT")), "gapx 20");
		if (App.onMac == false)
			saveAsCombine_md_sv.setBorder(BorderFactory.createEmptyBorder(3, 4, 2, 4));
		saveAsCombine_md_sv.setEnabled(App.saveAsBboUploadFormat == false);

		b2.add(saveAsLowerCaseSuit = new QCheckBox(this, App.saveAsLowerCaseSuit, Aaf.gT("botPanel.suitLower"), Aaf.gT("botPanel.suitLower_TT")),
				"flowy, span 2, split 2");
		if (App.onMac == false)
			saveAsLowerCaseSuit.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 4));

		b2.add(label = new QLabel("_______________________________", ""), "center");
		label.setForeground(Aaa.optionsTitleGreen);

		b2.add(label = new QLabel(Aaf.gT("botPanel.pbnProc")), "gapy 12");
		label.setForeground(Aaa.optionsTitleGreen);

		b2.add(label = new QLabel(Aaf.gT("botPanel.rotate")), "gapx 12, gapy 4, split 4, flowx");
		label.setForeground(Aaa.optionsTitleGreen);
		label.setToolTipText(Aaf.gT("botPanel.rotate_TT"));

		b2.add(pbnRotateWhenLoading0 = new QRadioButton(this, rotGroup, null, App.pbnRotateWhenLoading == 0, "", "0°"), "gapx 18");
		b2.add(pbnRotateWhenLoading1 = new QRadioButton(this, rotGroup, null, App.pbnRotateWhenLoading == 1, "", "90°"), "gapx 15");
		b2.add(pbnRotateWhenLoading2 = new QRadioButton(this, rotGroup, null, App.pbnRotateWhenLoading == 2, "", "180°"), "gapx 15");

//		if (App.onMac == false) {
//			pbnRotateWhenLoading0.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
//			pbnRotateWhenLoading1.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
//			pbnRotateWhenLoading2.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
//		}		

		b2.add(label = new QLabel(Aaf.gT("botPanel.fixed90s"), ""), "gapx 12, gapy 0, split 5, flowx");
		label.setForeground(Aaa.optionsTitleGreen);
		label.setToolTipText(Aaf.gT("botPanel.fixed90s_TT"));

		b2.add(pbnFixedQuarterTurns0 = new QRadioButton(this, qurGroup, null, App.pbnFixedQuarterTurns == 0, "", "0"), "gapx " + (App.onMac ? "5" : "10"));
		b2.add(pbnFixedQuarterTurns1 = new QRadioButton(this, qurGroup, null, App.pbnFixedQuarterTurns == 1, "", "1"), "gapx " + (App.onMac ? "3" : "8"));
		b2.add(pbnFixedQuarterTurns2 = new QRadioButton(this, qurGroup, null, App.pbnFixedQuarterTurns == 2, "", "2"), "gapx " + (App.onMac ? "3" : "8"));
		b2.add(pbnFixedQuarterTurns3 = new QRadioButton(this, qurGroup, null, App.pbnFixedQuarterTurns == 3, "", "3"), "gapx " + (App.onMac ? "3" : "8"));

		b2.add(pbnRenumberAtReadin = new QCheckBox(this, App.pbnRenumberAtReadin, Aaf.gT("botPanel.renumbReadIn"), Aaf.gT("botPanel.renumbReadIn_TT")),
				"gapy2, gapx 10");

		b2.add(alwaysSavePbnToLinToAutosavesFolder = new QCheckBox(this, App.alwaysSavePbnToLinToAutosavesFolder, Aaf.gT("botPanel.pbnToLin"), Aaf
				.gT("botPanel.pbnToLin_TT")), "gapx 10");

		b2.add(openAutosavesFolder = new QButton(this, Aaf.gT("botPanel.openAuto")), "center, gapx4, flowx");
		openAutosavesFolder.setToolTipText(Aaf.gT("botPanel.openAuto_TT"));
		if (App.onMac == false)
			openAutosavesFolder.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 4));

		add(b2, "growy");

//		------- next column ---------------------------

		Hashtable<Integer, JLabel> colLab = new Hashtable<Integer, JLabel>();
		colLab.put(new Integer(-200), new JLabel(Aaf.gT("botPanel.dark")));
		colLab.put(new Integer(+170), new JLabel(Aaf.gT("botPanel.pastel")));

		b3.add(new JLabel(Aaf.gT("botPanel.scrCols")), "center");
		b3.add(colorIntensity = new JSlider(JSlider.HORIZONTAL, -255, +255, 0), "wmin 120, wmax 150");
		colorIntensity.addChangeListener(this);
		colorIntensity.setLabelTable(colLab);
		colorIntensity.setPaintLabels(true);
		colorIntensity.setValue(App.colorIntensity);

		b3.add(difColorInsideDeal = new QCheckBox(this, App.difColorInsideDeal, Aaf.gT("botPanel.darker"), Aaf.gT("botPanel.darker_TT")), "gapy 7, gapx 15");

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

		add(b3, "growy");

//		------- next column ---------------------------

//		add(movieBidFlowDoesFlow = new QCheckBox(this, App.movieBidFlowDoesFlow, "",
//				"When  UN-checked  then for   Bridge Movie BID display   makes   Flow >   instantly show the bidding  "), "spany 2, wrap");

		Hashtable<Integer, JLabel> labTab = new Hashtable<Integer, JLabel>();
		labTab.put(new Integer(10), new JLabel(Aaf.gT("botPanel.slow")));
		labTab.put(new Integer(90), new JLabel(Aaf.gT("botPanel.fast")));

		b4.add(new JLabel(Aaf.gT("botPanel.playSpeed")), "center");
		b4.add(playSpeed = new JSlider(JSlider.HORIZONTAL), "center, wmin 120, wmax 150");
		playSpeed.addChangeListener(this);
		playSpeed.setLabelTable(labTab);
		playSpeed.setPaintLabels(true);
		playSpeed.setValue(pluseToPercent(App.playPluseTimerMs));

//		b4.add(bidSpeelabel = new JLabel("Bid Display Speed"), "center");
//		b4.add(bidSpeed = new JSlider(JSlider.HORIZONTAL), "wmax 130");
//		bidSpeed.addChangeListener(this);
//		bidSpeed.setLabelTable(labTab);
//		bidSpeed.setPaintLabels(true);
//		bidSpeed.setValue(pluseToPercent(App.bidPluseTimerMs));
//		bidSpeed.setEnabled(App.movieBidFlowDoesFlow);
//		bidSpeelabel.setEnabled(App.movieBidFlowDoesFlow);

		b4.add(label = new QLabel(Aaf.gT("botPanel.whenWaitOff")), "gapy 18, center");
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

		b4.add(resetSpeeds = new QButton(this, Aaf.gT("botPanel.resetSpeeds")), "gapy 20, center");
		if (App.onMac == false)
			resetSpeeds.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 4));

		add(b4, "growy");

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

		if (e.getSource() == saveDealsAsPbnNoPlay) {
			try {
				MassGi_utils.saveDealsAsPbnNoPlay();
			} catch (Exception ee) {
			}
		}

		else if (e.getSource() == resetColors) {

			App.colorIntensity = App.defaultColorIntensity;
			App.colorTint = 0;
			App.difColorInsideDeal = true;
			difColorInsideDeal.setSelected(App.difColorInsideDeal == true);
			if (App.allConstructionComplete) {
				colorIntensityDelayTimer.restart();
			}
		}

		else if (e.getSource() == resetSpeeds) {
			App.playPluseTimerMs = App.defaultplayPluseTimerMs;
			playSpeed.setValue(pluseToPercent(App.playPluseTimerMs));

			App.eotExtendedDisplay = App.defaultEotExtendedDisplay;
			eotDelay.setValue(App.eotExtendedDisplay);
		}

		else if (e.getSource() == useDefaultSavesFolder) {
			App.realSavesPath = App.defaultSavesPath;
			tfSavesFolderDisplay.setText(App.realSavesPath);
		}

		else if (e.getSource() == browseForSavesFolder) {
			JFileChooser fc = new JFileChooser();
			fc.setCurrentDirectory(new java.io.File(App.realSavesPath)); // start at folder
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnVal = fc.showSaveDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File yourFolder = fc.getSelectedFile();
				if (yourFolder != null) {
					try {
						App.realSavesPath = yourFolder.getCanonicalPath() + File.separator;
						tfSavesFolderDisplay.setText(App.realSavesPath);
						yourFolder.mkdir();
					} catch (IOException e1) {
					}
				}
			}
		}

		else if (e.getSource() == openSavesFolder) {
			try {
				Desktop.getDesktop().open(new File(App.realSavesPath));
			} catch (IOException e1) {
			}
		}

		else if (e.getSource() == openAutosavesFolder) {
			try {
				Desktop.getDesktop().open(new File(App.autoSavesPath));
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
//		else if (source == bidSpeed) {
//	  	    int pluse = percentToPluse(source.getValue());
//		    App.bidPluseTimerMs = pluse;
//	    }
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

	public void itemStateChanged(ItemEvent e) {
		boolean b = (e.getStateChange() == ItemEvent.SELECTED);
		Object source = e.getItemSelectable();

		// @formatter:off
		if (source == saveAsBboUploadFormat) {
            App.saveAsBboUploadFormat = b;
    		saveAsCombine_md_sv.setEnabled(App.saveAsBboUploadFormat == false);
    		saveAsBboUploadExtraS.setEnabled(App.saveAsBboUploadFormat);
        }
		else if (source == saveAsCombine_md_sv) {
            App.saveAsCombine_md_sv = b;
        }
		else if (source == saveAsLowerCaseSuit) {
            App.saveAsLowerCaseSuit = b;
        }
		else if (source == saveAsBboUploadExtraS) {
            App.saveAsBboUploadExtraS = b;
        }
		else if (source == pbnRotateWhenLoading0) {
			App.pbnRotateWhenLoading = 0;
		}
		else if (source == pbnRotateWhenLoading1) {
			App.pbnRotateWhenLoading = 1;
		}
		else if (source == pbnRotateWhenLoading2) {
			App.pbnRotateWhenLoading = 2;
		}
		else if (source == pbnFixedQuarterTurns0) {
			App.pbnFixedQuarterTurns = 0;
		}
		else if (source == pbnFixedQuarterTurns1) {
			App.pbnFixedQuarterTurns = 1;
		}
		else if (source == pbnFixedQuarterTurns2) {
			App.pbnFixedQuarterTurns = 2;
		}
		else if (source == pbnFixedQuarterTurns3) {
			App.pbnFixedQuarterTurns = 3;
		}
		else if (source == alwaysSavePbnToLinToAutosavesFolder) {
			App.alwaysSavePbnToLinToAutosavesFolder = b;
		}
		else if (source == pbnRenumberAtReadin) {
			App.pbnRenumberAtReadin = b;
		}
		else if (source == pbnRenumberDeals) {
			App.pbnRenumberDeals = b;
		}
		else if (source == force_savename_xxx) {
			App.force_savename_xxx = b;
		}
		else if (source == difColorInsideDeal) {
			App.difColorInsideDeal = b;
			if (App.allConstructionComplete) {
				colorIntensityDelayTimer.restart();
			}
		}			
		
//		else if      (source == movieBidFlowDoesFlow) {
//          App.movieBidFlowDoesFlow = b;
//          bidSpeed.setEnabled(App.movieBidFlowDoesFlow);
//    		bidSpeelabel.setEnabled(App.movieBidFlowDoesFlow);
//        }
		// @formatter:on

		if (App.allConstructionComplete) {
			App.savePreferences();
			App.frame.repaint();
		}

	}

}
