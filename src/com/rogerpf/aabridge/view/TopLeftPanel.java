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

import net.miginfocom.swing.MigLayout;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;

/**
 */
public class TopLeftPanel extends ClickPanel {

	private static final long serialVersionUID = 1L;

	public RpfTextField descEntry = new RpfTextField(90, 13);
	public VunerabilityDisplayPanel c0_0__vdp = new VunerabilityDisplayPanel();

	RpfResizeButton quicksave;
	RpfResizeButton playagain;
	RpfResizeButton easySave;
	RpfResizeButton clock;
	RpfResizeButton anti;

	/**
	 */
	TopLeftPanel() { /* Constructor */

		descEntry.setBorder(Aaa.emptyBorder);
		descEntry.setFocusable(false);
		descEntry.setForeground(Aaa.weedyBlack);
		descEntry.setBackground(Aaa.greenishWhite);

		descEntry.setText(App.deal.description);

		quicksave = new RpfResizeButton(1, "menuQuickSave", 30, 14, 0.7f);
		easySave = new RpfResizeButton(1, "menuEasySave", 27, 14, 0.7f);
		playagain = new RpfResizeButton(1, "menuPlayAgain", 20, 14, 0.7f);
		clock = new RpfResizeButton(1, "mainClock", 7, 14, 0.9f);
		anti = new RpfResizeButton(1, "mainAnti", 7, 14, 0.9f);

		setRotationBtnsVisibility();
		setEasySaveVisibility();
		setPlayAgainVisibility();

		setLayout(new MigLayout("insets 0 0 0 0, gap 0! 0!, flowy", "[60%][]5%", "5%[15%]8%[12%]8%[12%]8%[12%]2%[1%]"));
		add(descEntry, "pushx, align right, wmin 90%, wmax 90%, span 2");
		add(c0_0__vdp, "gapx 4%, wmin 52%, hmin 72%, spany, wrap");
		add(quicksave, "gapx 4%, wmin 10%, hmin 10%, align left");
		add(easySave, "hidemode 3, gapx 4%, wmin 10%, hmin 10%");
		add(playagain, "split3, gapx 4%, wmin 4%, hmin 10%, flowx");
		add(anti, "gapx 4%, wmin 6%, hmin 10%");
		add(clock, "gapx 2%, wmin 6%, hmin 10%");

	}

	/**   
	 */
	public void setRotationBtnsVisibility() {
		clock.setVisible(App.showRotationBtns);
		anti.setVisible(App.showRotationBtns);
	}

	/**   
	 */
	public void setEasySaveVisibility() {
		easySave.setVisible(App.showEasySave);
	}

	/**   
	 */
	public void setPlayAgainVisibility() {
		playagain.setVisible(App.showPlayAgain);
	}

	/**   
	 */
	public void dealMajorChange() {
		descEntry.setText(App.deal.description);
	}

	/**
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		setBackground(Aaa.baizeGreen);
	}

}
