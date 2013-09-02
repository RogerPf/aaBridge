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
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;

import net.miginfocom.swing.MigLayout;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.model.Deal;

/**   
 */
public class AaLinButtonsPanel extends ClickPanel implements ItemListener {
	// ---------------------------------- CLASS -------------------------------------

	private static final long serialVersionUID = 1L;

	ButtonGroup group = new ButtonGroup();
	QLabel label;

	ArrayList<QLabel> labels = new ArrayList<QLabel>();
	ArrayList<QRadioButton> rBtns = new ArrayList<QRadioButton>();

	public AaLinButtonsPanel() {
		// ==============================================================================================

		setLayout(new MigLayout("insets 0 0 0 0, gap 0! 0!", "3[]2[]2[]4", "18[]3[]4[]"));

		setVisible(false);
	}

	public void matchToAppLin() {
		// ==============================================================================================

		clear();

		if (App.lin == null || App.lin.size() == 1) {
			setVisible(false);
			return;
		}

		// get some additional info from the lin

		add(label = new QLabel("Title - HOVER HERE", App.lin.headingInfo), "span 3, center, wrap");
		labels.add(label);
		label.setForeground(Aaa.optionsTitleGreen);
		if (App.lin.headingInfo.isEmpty())
			label.setVisible(false);

		add(label = new QLabel("       "));
		labels.add(label);

		add(label = new QLabel("Open Rm"), "hidemode 2");
		labels.add(label);
		label.setForeground(Aaa.optionsTitleGreen);
		label.setVisible(App.lin.twoTeams);

		add(label = new QLabel("Closed Rm", ""), "hidemode 2, wrap");
		labels.add(label);
		label.setForeground(Aaa.optionsTitleGreen);
		label.setVisible(App.lin.twoTeams);

		int i = 0;
		while (i < App.lin.size()) {
			Deal d = App.lin.get(i);

			String sCount = "" + (i / 2 + 1);
			QLabel label = new QLabel(d.linRowText, "");
			add(label);
			labels.add(label);

			QRadioButton rBtn = new QRadioButton(this, group, "o" + sCount, d.linResult);
			add(rBtn);
			rBtns.add(rBtn);
			i++;
			if (i >= App.lin.size())
				break;

			d = App.lin.get(i);
			rBtn = new QRadioButton(this, group, "c" + sCount, d.linResult);
			add(rBtn, "wrap");
			rBtns.add(rBtn);
			i++;
		}

		rBtns.get(0).setSelected(true);

		// setVisible(false);
		// App.frame.pack();
		setVisible(true);
	}

	public void clear() {
		// ==============================================================================================

		while (labels.size() > 0) {
			remove(labels.get(0));
			labels.remove(0);
		}

		while (rBtns.size() > 0) {
			remove(rBtns.get(0));
			rBtns.remove(0);
		}

	}

	public void componentResized(ComponentEvent e) {
	}

	public void componentMoved(ComponentEvent e) {
	}

	public void componentShown(ComponentEvent e) {
	}

	public void componentHidden(ComponentEvent e) {
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

	/** 
	 * This listens for the check box changed event
	 */
	public void itemStateChanged(ItemEvent e) {
		// ==============================================================================================

		boolean b = (e.getStateChange() == ItemEvent.SELECTED);

		if (b == false) {
			return; // do nothing - we can ignore de-selection
		}

		Object source = e.getItemSelectable();

		int i = -1;
		for (QRadioButton rBtn : rBtns) {
			i++;
			if (source == rBtn) {
				Deal d = App.lin.get(i).deepClone();
				App.switchToDeal(d);
			}
		}
	}

}
