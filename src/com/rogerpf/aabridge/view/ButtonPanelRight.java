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

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.Aaf;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.model.Lin;

/**
 */
public class ButtonPanelRight extends JPanel {
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	RpfResizeButton ddsAnalyse;
	RpfResizeButton ddsReinstateAnalyser;
	RpfResizeButton ddsLabel;
	RpfResizeButton ddsScoreOnOff;

	RpfResizeButton hiddenHandsShowHide_b;
	RpfResizeButton hiddenHandsClick1_b;
	RpfResizeButton hiddenHandsClick2_b;

//	RpfResizeButton autoEnterLabelPbn;
	RpfResizeButton autoEnterLabelAuto;
	RpfResizeButton autoEnterLabelEnter;
	RpfResizeButton autoEnterOnOff;

	/**
	 */
	ButtonPanelRight() { /* Constructor */
		// ==============================================================================================
		setOpaque(false);
		// setBackground(Aaa.baizePink);
		// setBackground(Aaa.baizeGreen);

		// @formatter:off
			
		setLayout(new MigLayout(App.simple + ", flowy, align right", "[c]", "2%[]0.75%[]7.0%[][]10.0%[][]2%[]26%[][]0.5%[]"));

		add(ddsAnalyse	         = new RpfResizeButton(Aaa.s_Std,   "ddsAnalyse", 70, 5));
		add(ddsReinstateAnalyser = new RpfResizeButton(Aaa.s_Std,   "ddsReinstateAnalyser", 55, 3));
		add(ddsLabel             = new RpfResizeButton(Aaa.s_Label, "ddsLabel", 75, 6));
		add(ddsScoreOnOff        = new RpfResizeButton(Aaa.s_Std,   "ddsScoreOnOff", 65, 5));

		add(hiddenHandsClick1_b   = new RpfResizeButton(Aaa.s_Label, "hiddenHandsClick1", 75, 4));
		add(hiddenHandsClick2_b   = new RpfResizeButton(Aaa.s_Label, "hiddenHandsClick2", 75, 4));
		add(hiddenHandsShowHide_b = new RpfResizeButton(Aaa.s_Std, "hiddenHandsShowHide", 65, 6));		

//		add(autoEnterLabelPbn    = new RpfResizeButton(Aaa.s_Label, "autoEnterLabelPbn", 75, 5));
		add(autoEnterLabelAuto   = new RpfResizeButton(Aaa.s_Label, "autoEnterLabelAuto", 75, 5));
		add(autoEnterLabelEnter  = new RpfResizeButton(Aaa.s_Label, "autoEnterLabelEnter", 75, 5));
		add(autoEnterOnOff       = new RpfResizeButton(Aaa.s_Std,   "autoEnterOnOff", 65, 5));

		// @formatter:on
	}

	/**   
	 */
	public void calcApplyBarVisiblity() {
		// =============================================================

		// @formatter:off
		// @formatter:on

		{
			boolean anaVisible = (App.visualMode == App.Vm_InsideADeal) && App.haglundsDDSavailable;
			ddsAnalyse.setVisible(anaVisible);

			boolean keepOnVisible = anaVisible /* &&  App.pbnAutoEnter */&& (App.cameFromPbnOrSimilar() || App.isLin__Virgin());

			ddsReinstateAnalyser.setVisible(keepOnVisible);
			ddsReinstateAnalyser.setBackground(App.reinstateAnalyser ? Aaa.buttonBkgColorYes : Aaa.buttonBkgColorStd);

			boolean ddsVisible = (App.visualMode == App.Vm_InsideADeal) && App.haglundsDDSavailable;
			ddsLabel.setVisible(ddsVisible);
			ddsScoreOnOff.setVisible(ddsVisible);
			ddsScoreOnOff.setText(App.ddsScoreShow ? Aaf.rhp_isOn : Aaf.rhp_isOff);
			ddsScoreOnOff.setBackground(App.ddsScoreShow ? Aaa.buttonBkgColorYes : Aaa.buttonBkgColorStd);
		}

		{
			boolean visible = (App.mg.lin.linType != Lin.FullMovie) || (App.visualMode == App.Vm_InsideADeal);

			boolean handsShowing = App.localShowHidden;

			String text = handsShowing ? Aaf.rhp_hide : Aaf.rhp_show;

			hiddenHandsShowHide_b.setText(text);
			hiddenHandsShowHide_b.setVisible(visible);

			hiddenHandsClick1_b.setVisible(visible & !handsShowing);
			hiddenHandsClick2_b.setVisible(visible & !handsShowing);
		}

		{
			boolean reVisible = (App.cameFromPbnOrSimilar());
//			autoEnterLabelPbn.setVisible(reVisible);
			autoEnterLabelAuto.setVisible(reVisible);
			autoEnterLabelEnter.setVisible(reVisible);
			autoEnterOnOff.setVisible(reVisible);
			autoEnterOnOff.setText(App.pbnAutoEnter ? Aaf.rhp_isOn : Aaf.rhp_isOff);
			autoEnterOnOff.setBackground(App.pbnAutoEnter ? Aaa.buttonBkgColorYes : Aaa.buttonBkgColorStd);
		}

	}

}
