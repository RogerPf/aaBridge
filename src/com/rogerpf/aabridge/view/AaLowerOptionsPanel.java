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
import java.awt.SystemColor;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import com.rogerpf.aabridge.controller.App;

/**   
 */
class AaLowerOptionsPanel extends ClickPanel implements ChangeListener {

	private static final long serialVersionUID = 1L;

	JSlider bidSpeed;
	JSlider playSpeed;
	JSlider eotDelay;

	public AaLowerOptionsPanel() {
		setLayout(new MigLayout("insets 0 0 0 0, gap 0! 0!, flowy ", "5[]30[]", "5[center][center]"));

		Hashtable<Integer, JLabel> labTab = new Hashtable<Integer, JLabel>();
		labTab.put(new Integer(0), new JLabel("Slow"));
		labTab.put(new Integer(100), new JLabel("Fast"));

		add(new JLabel("Bidding Display Speed"), "center");
		add(bidSpeed = new JSlider(JSlider.HORIZONTAL), "wrap");
		bidSpeed.addChangeListener(this);
		bidSpeed.setLabelTable(labTab);
		bidSpeed.setPaintLabels(true);
		bidSpeed.setValue(pluseToPercent(App.bidPluseTimerMs));

		add(new JLabel("Play Display Speed"), "center");
		add(playSpeed = new JSlider(JSlider.HORIZONTAL), "wrap");
		playSpeed.addChangeListener(this);
		playSpeed.setLabelTable(labTab);
		playSpeed.setPaintLabels(true);
		playSpeed.setValue(pluseToPercent(App.playPluseTimerMs));

		Hashtable<Integer, JLabel> labTab2 = new Hashtable<Integer, JLabel>();
		labTab2.put(new Integer(0), new JLabel("No extra"));
		labTab2.put(new Integer(9), new JLabel("Longer"));

		add(new JLabel("End of Trick Pause"), "center");
		add(eotDelay = new JSlider(JSlider.HORIZONTAL), "wrap");
		eotDelay.addChangeListener(this);
		eotDelay.setInverted(true);
		eotDelay.setLabelTable(labTab2);
		eotDelay.setPaintLabels(true);
		eotDelay.setValue(App.eotExtendedDisplay);
		eotDelay.setMinimum(0);
		eotDelay.setMaximum(9);

	}

	public void stateChanged(ChangeEvent e) {

		JSlider source = (JSlider) e.getSource();

		if (source == bidSpeed) {
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

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		setBackground(SystemColor.control);
	}

}
