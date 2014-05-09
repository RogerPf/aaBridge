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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.model.Cc;

/**   
 */
class AaLowerOptionsPanel extends ClickPanel implements ChangeListener, ActionListener, ItemListener {

	private static final long serialVersionUID = 1L;

	QButton resetPrefs;
	QCheckBox outlineCardEdge;
	JSlider colorIntensity;
	JSlider colorTint;
	JSlider bidSpeed;
	JSlider playSpeed;
	JSlider eotDelay;
	JLabel label;

	public AaLowerOptionsPanel() {
		setBackground(SystemColor.control);

		setLayout(new MigLayout(App.simple + ", flowy", "push[][][]20[][][]", "1[][]"));

		add(resetPrefs = new QButton(this, "Rst Colors"));
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

		add(new JLabel("Bid Display Speed"), "center");
		add(bidSpeed = new JSlider(JSlider.HORIZONTAL), "wmax 130, wrap");
		bidSpeed.addChangeListener(this);
		bidSpeed.setLabelTable(labTab);
		bidSpeed.setPaintLabels(true);
		bidSpeed.setValue(pluseToPercent(App.bidPluseTimerMs));

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
		if      (source == outlineCardEdge) {
            App.outlineCardEdge = b;
        }
		// @formatter:on

		if (App.allConstructionComplete) {
			App.savePreferences();
			App.frame.repaint();
		}

	}

}
