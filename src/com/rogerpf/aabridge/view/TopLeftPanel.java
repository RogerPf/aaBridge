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

	RpfResizeButton saveAs;
	RpfResizeButton playWipe;
	RpfResizeButton saveStd;
	RpfResizeButton clock;
	RpfResizeButton anti;

	/**
	 */
	TopLeftPanel() { /* Constructor */

		RpfResizeButton b;

		descEntry.setBorder(Aaa.emptyBorder);
		descEntry.setFocusable(false);
		descEntry.setForeground(Aaa.weedyBlack);
		descEntry.setBackground(Aaa.greenishWhite);

		descEntry.setText(App.deal.description);

		saveStd = new RpfResizeButton(1, "menuSaveStd", 18, 14, 0.7f);
		saveAs = new RpfResizeButton(1, "menuSaveAs", 24, 14, 0.7f);
		playWipe = new RpfResizeButton(1, "menuPlayWipe", 14, 14, 0.7f);
		b /*   */= new RpfResizeButton(0, "invis", 50, 14, 0.7f);
		b.setVisible(false);
		clock = new RpfResizeButton(1, "mainClock", 7, 12, 0.9f);
		anti = new RpfResizeButton(1, "mainAnti", 7, 12, 0.9f);

		setRotationBtnsVisibility();
		setSaveAsVisibility();
		setSaveStdVisibility();
		setWipeVisibility();

		setLayout(new MigLayout("insets 0 0 0 0, gap 0! 0!, flowy", "[60%][]5%", "5%[15%]8%[12%]8%[12%]8%[12%]2%[12%]"));
		add(descEntry, "pushx, align right, wmin 90%, wmax 90%, span 2");
		add(c0_0__vdp, "align left, gapx 4%, wmin 52%, wmax 52%, hmin 70%, hmax 70%, spany, wrap");
		add(saveStd, "gapx 4%, wmin 10%, hmin 10%");
		add(saveAs, "gapx 4%, wmin 10%, hmin 10%, align left");
		add(playWipe, "gapx 4%, wmin 4%, hmin 10%");
		add(b, "split3, gapx 4%, wmin 4%, hmin 6%, flowx");
		add(anti, "hidemode 1, gapx 4%, wmin 6%, hmin 6%");
		add(clock, "hidemode 1, gapx 3%, wmin 6%, hmin 6%");

		// earlier version

//		setLayout(new MigLayout("insets 0 0 0 0, gap 0! 0!, flowy", "[60%][]5%", "5%[15%]8%[12%]8%[12%]8%[12%]2%[1%]"));
//		add(descEntry, "pushx, align right, wmin 90%, wmax 90%, span 2");
//		add(c0_0__vdp, "gapx 4%, wmin 52%, hmin 72%, spany, wrap");
//		add(quicksave, "gapx 4%, wmin 10%, hmin 10%, align left");
//		add(saveStd, "hidemode 1, gapx 4%, wmin 10%, hmin 10%");
//		add(playWipe, "split3, gapx 4%, wmin 4%, hmin 10%, flowx");
//		add(anti, "gapx 4%, wmin 6%, hmin 10%");
//		add(clock, "gapx 2%, wmin 6%, hmin 10%");

	}

	/**   
	 */
	public void setRotationBtnsVisibility() {
		clock.setVisible(App.showRotationBtns);
		anti.setVisible(App.showRotationBtns);
	}

	/**   
	 */
	public void setSaveAsVisibility() {
		saveAs.setVisible(App.showSaveAs);
	}

	/**   
	 */
	public void setSaveStdVisibility() {
		saveStd.setVisible(App.showSaveStd);
	}

	/**   
	 */
	public void setWipeVisibility() {
		playWipe.setVisible(App.showWipe);
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
