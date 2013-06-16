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
	}

	public void stateChanged(ChangeEvent e) {

		JSlider source = (JSlider) e.getSource();

		int pluse = percentToPluse(source.getValue());

		if (source == bidSpeed) {
			App.bidPluseTimerMs = pluse;
		}
		else if (source == playSpeed) {
			App.playPluseTimerMs = pluse;
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
