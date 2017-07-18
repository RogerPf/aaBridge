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

import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.model.Rank;

/**
 */
public class TopLeftPanel extends ClickPanel {

	private static final long serialVersionUID = 1L;

	public RpfTextField descEntry = RpfTextField.createRpfTextField(15, 10.2f);
	public VulnerabilityDisplayPanel c0_0__vdp = new VulnerabilityDisplayPanel();

//	RpfResizeButton blueScore;
//	RpfResizeButton purpleScore;

	RpfResizeButton clock;
	RpfResizeButton anti;

	/**
	 */
	TopLeftPanel() { /* Constructor */

		setOpaque(false);
		// setBackground(Aaa.baizeGreen);
		// setBackground(Aaa.baizePink);

		descEntry.setBorder(new EmptyBorder(0, 5, 2, 5));
		descEntry.setFocusable(false);
		descEntry.setForeground(Aaa.weedyBlack);
		descEntry.setBackground(Aaa.greenishWhite);
		descEntry.setText(App.deal.ahHeader);

		clock = new RpfResizeButton(Aaa.s_Std, "mainClock", 7, 12, 0.9f);
		anti = new RpfResizeButton(Aaa.s_Std, "mainAnti", 7, 12, 0.9f);

		ClickPanel p1 = new ClickPanel();
		p1.setOpaque(false);
		// p1.setBackground(Aaa.baizePink);
		// p1.setBackground(Aaa.baizeGreen);

		setLayout(new MigLayout(App.simple, "[]", "2%[]"));

		add(descEntry, "gapx 5%, wrap");
		add(c0_0__vdp, "split 3, top, align left, gapy 3%, gapx 0%, w 54%, h 70%");

		add(p1, "w 44%, h 100%");

		p1.setLayout(new MigLayout(App.simple, "5%[]", "35%[]2%"));

//		p1.add(blueScore = new RpfResizeButton(Aaa.s_SelfCmd, " ", 10, 15), "gapx 15%, w 30%, h 10%, split");
//		blueScore.setBackground(Aaa.teamDDColorAy[0][0]);
//		p1.add(purpleScore = new RpfResizeButton(Aaa.s_SelfCmd, " ", 10, 15), "gapx 5%, w 30%, h 10%, wrap");
//		purpleScore.setBackground(Aaa.teamDDColorAy[1][0]);

		p1.add(anti, "push y, gapx 30%, w 18%, h 15%, gapy 30%, split");
		p1.add(clock, "gapx 5%, w 15%, h 15%");

//
//		
//		add(descEntry, "wmin 80%, wmax 80%");
//		add(new JLabel("WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW"), "wmin 80%, wmax 80%");

//		add(c0_0__vdp, "align left, gapx 4%, wmin 52%, wmax 52%, hmin 70%, hmax 70%, spany, wrap, pushy");
//
//		add(anti, "push, flowx, split 2, pushy, pushx, gapx 4%, wmin 6%, hmin 6%");
//		add(clock, "gapx 3%, wmin 6%, hmin 6%");
	}

	/**
	 */
	public void setButtonVisibility() {
		setRotationBtnsVisibility();
		setScoreDisplayVisibility();
		setRotationBtnsVisibility();
	}

	/**
	 */
	public int getuptoValue(int def) {
		String s = descEntry.getText().toLowerCase().trim();
		if (s.length() == 1 || (s.length() > 1 && (s.charAt(1) == ' '))) {
			char c = s.charAt(0);
			Rank rank = Rank.charToRank(c);
			if ((2 < rank.v) && (rank.v < 14)) {
				return rank.v;
			}
		}
		if (s.length() == 2 || (s.length() > 2 && (s.charAt(2) == ' '))) {
			if (s.startsWith("10")) {
				return 10;
			}
		}
		return def;
	}

	/**   
	 */
	public void setRotationBtnsVisibility() {
		// @formatter:off
		boolean vis =     App.showRotationBtns 
				      && ((App.visualMode == App.Vm_InsideADeal) /* || (App.mg.lin.linType != Lin.FullMovie)*/);
		// @formatter:on
		clock.setVisible(vis);
		anti.setVisible(vis);
	}

	/**   
	 */
	public void setScoreDisplayVisibility() {

//		// @formatter:off
//		boolean vis = (App.mg.lin.linType == Lin.VuGraph) 
//				   && (App.mg.ddAy != null) 
//				   && (App.mg.ddAy.isEmpty() == false) 
//				   && (App.showDdAsMin == false)
//				   &&  App.showDdWithResults;
//		// @formatter:on
//		 
//		blueScore.setVisible(false /*vis*/ );
//		purpleScore.setVisible(false /*vis*/);
	}

	/**   
	 */
	public void dealMajorChange() {
		descEntry.setText(App.deal.ahHeader);
//		blueScore.setText(App.deal.blueScore);
//		purpleScore.setText(App.deal.purpleScore);
	}

}
