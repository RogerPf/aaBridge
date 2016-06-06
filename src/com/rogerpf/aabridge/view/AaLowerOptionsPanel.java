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
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.model.Cc;

/**   
 */
class AaLowerOptionsPanel extends ClickPanel implements ChangeListener, ActionListener, ItemListener {

	private static final long serialVersionUID = 1L;

	JLabel label;

	QCheckBox saveAsBboUploadFormat;
	JTextField tfSavesFolderDisplay;
	QCheckBox saveAsBboUploadExtraS;

	QButton openSavesFolder;
	QButton useDefaultSavesFolder;
	QButton browseForSavesFolder;

	QButton resetPrefs;
	QCheckBox outlineCardEdge;
	QCheckBox movieBidFlowDoesFlow;
	JSlider colorIntensity;
	JSlider colorTint;
	JSlider bidSpeed;
	JSlider playSpeed;
	JSlider eotDelay;

	JLabel bidSpeelabel;

	public void realSavesPathNowAvailable() {
		tfSavesFolderDisplay.setText(App.realSavesPath);
	}

	public AaLowerOptionsPanel() {
		// setBackground(SystemColor.control); no don't use this

		setLayout(new MigLayout(App.simple + ", flowy", "push[]35[]10[][][]20[][][]", "1[][]"));

		add(label = new QLabel("Saves Folder"), ", flowx, split2");
		label.setForeground(Aaa.optionsTitleGreen);

		add(openSavesFolder = new QButton(this, "Open Saves Folder"), "gapx20, flowx");
		openSavesFolder.setToolTipText("Once the Saves folder is Open  -  you can  Drag & Drop  any of the deals (lin files) on to aaBridge  ");
		if (App.onMac == false)
			openSavesFolder.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 4));

		add(saveAsBboUploadFormat = new QCheckBox(this, App.saveAsBboUploadFormat, "Save using BBO uploadable format",
				"Note - The BBO uploadable format requires that each deal is on its own line  -  so no extra line feed  "));

		add(tfSavesFolderDisplay = new JTextField((App.onMacOrLinux ? 23 : 19)), "gapy2, split2");
		tfSavesFolderDisplay.setEditable(false);

		add(saveAsBboUploadExtraS = new QCheckBox(this, App.saveAsBboUploadExtraS, "Add extra spaces to BBO format",
				"Adds extra spaces to the BBO upload format  -  to help with  'manual'  readability  "));

		add(browseForSavesFolder = new QButton(this, "Select a different  Saves Folder"), "");
		browseForSavesFolder.setToolTipText("Lets you select a different  'Saves Folder'   -   on a MAC you need to  TYPE IN  the  'final folder'  name  ");
		if (App.onMac == false)
			browseForSavesFolder.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 4));

		add(label = new QLabel("Please re-start  aaBridge  if changed"), "gapy 4, flowx");
//	    label.setForeground(Aaa.optionsTitleGreen);

		add(useDefaultSavesFolder = new QButton(this, "Restore the  Default  Saves Folder"), "gapy 4, wrap");
		if (App.onMac == false)
			useDefaultSavesFolder.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 4));

		add(resetPrefs = new QButton(this, "Rst Colors"));
		if (App.onMac == false)
			resetPrefs.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 4));
		resetPrefs.setToolTipText("Restore the default colors");

		add(outlineCardEdge = new QCheckBox(this, App.outlineCardEdge, "Outline", "Outline the cards on the baize with white  "), "wrap");

		Hashtable<Integer, JLabel> colLab = new Hashtable<Integer, JLabel>();
		colLab.put(new Integer(-200), new JLabel("Dark"));
		colLab.put(new Integer(+170), new JLabel("Pastel"));

		add(new JLabel("Screen Colors"), "center");
		add(colorIntensity = new JSlider(JSlider.HORIZONTAL, -255, +255, 0), "wmax 150, wrap");
		colorIntensity.addChangeListener(this);
		colorIntensity.setLabelTable(colLab);
		colorIntensity.setPaintLabels(true);
		colorIntensity.setValue(App.colorIntensity);

		Hashtable<Integer, JLabel> tintLab = new Hashtable<Integer, JLabel>();
		tintLab.put(new Integer(-25), new JLabel("Orange"));
		tintLab.put(new Integer(+35), new JLabel("Blue"));

		add(new JLabel("Screen Tint"), "center");
		add(colorTint = new JSlider(JSlider.HORIZONTAL, -50, +50, 0), "wmax 140, wrap");
		colorTint.addChangeListener(this);
		colorTint.setLabelTable(tintLab);
		colorTint.setPaintLabels(true);
		colorTint.setValue(App.colorTint);

		Hashtable<Integer, JLabel> labTab = new Hashtable<Integer, JLabel>();
		labTab.put(new Integer(10), new JLabel("Slow"));
		labTab.put(new Integer(90), new JLabel("Fast"));

		add(movieBidFlowDoesFlow = new QCheckBox(this, App.movieBidFlowDoesFlow, "",
				"When  UN-checked  then for   Bridge Movie BID display   makes   Flow >   instantly show the bidding  "), "spany 2, wrap");

		add(bidSpeelabel = new JLabel("Bid Display Speed"), "center");
		add(bidSpeed = new JSlider(JSlider.HORIZONTAL), "wmax 130, wrap");
		bidSpeed.addChangeListener(this);
		bidSpeed.setLabelTable(labTab);
		bidSpeed.setPaintLabels(true);
		bidSpeed.setValue(pluseToPercent(App.bidPluseTimerMs));
		bidSpeed.setEnabled(App.movieBidFlowDoesFlow);
		bidSpeelabel.setEnabled(App.movieBidFlowDoesFlow);

		add(new JLabel("Play Display Speed"), "center");
		add(playSpeed = new JSlider(JSlider.HORIZONTAL), "wmax 130, wrap");
		playSpeed.addChangeListener(this);
		playSpeed.setLabelTable(labTab);
		playSpeed.setPaintLabels(true);
		playSpeed.setValue(pluseToPercent(App.playPluseTimerMs));

		Hashtable<Integer, JLabel> labTab2 = new Hashtable<Integer, JLabel>();
		labTab2.put(new Integer(2), new JLabel("No extra"));
		labTab2.put(new Integer(7), new JLabel("Longer"));

		String ttext = "Used ONLY when the - WAIT - option is set  - OFF -  (see   AutoPlay   options TAB)  ";

		add(label = new JLabel("End of Trick Pause"), "center");
		label.setToolTipText(ttext);
		add(eotDelay = new JSlider(JSlider.HORIZONTAL), "wmax 150, wrap");
		eotDelay.setToolTipText(ttext);
		eotDelay.addChangeListener(this);
		eotDelay.setInverted(true);
		eotDelay.setLabelTable(labTab2);
		eotDelay.setPaintLabels(true);
		eotDelay.setValue(App.eotExtendedDisplay);
		eotDelay.setMinimum(0);
		eotDelay.setMaximum(9);
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
		if (e.getSource() == resetPrefs) {

			App.colorIntensity = App.defaultColorIntensity;
			App.colorTint = 0;
			if (App.allConstructionComplete) {
				colorIntensityDelayTimer.restart();
			}
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
		else if (source == bidSpeed) {
			int pluse = percentToPluse(source.getValue());
			App.bidPluseTimerMs = pluse;
		}
		else if (source == playSpeed) {
			int pluse = percentToPluse(source.getValue());
			App.playPluseTimerMs = pluse;
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

	public void itemStateChanged(ItemEvent e) {
		boolean b = (e.getStateChange() == ItemEvent.SELECTED);
		Object source = e.getItemSelectable();

		// @formatter:off
		if (source == saveAsBboUploadFormat) {
            App.saveAsBboUploadFormat = b;
        }
		else if (source == saveAsBboUploadExtraS) {
            App.saveAsBboUploadExtraS = b;
        }
		else if (source == outlineCardEdge) {
            App.outlineCardEdge = b;
        }
		else if      (source == movieBidFlowDoesFlow) {
            App.movieBidFlowDoesFlow = b;
            bidSpeed.setEnabled(App.movieBidFlowDoesFlow);
    		bidSpeelabel.setEnabled(App.movieBidFlowDoesFlow);
        }
		// @formatter:on

		if (App.allConstructionComplete) {
			App.savePreferences();
			App.frame.repaint();
		}

	}

}
