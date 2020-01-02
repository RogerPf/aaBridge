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

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.Aaf;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.model.Lin;

import net.miginfocom.swing.MigLayout;

/**
 */
public class ButtonPanelRight extends JPanel {
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	RpfResizeButton ddsAnalyse;
	RpfResizeButton ddsReinstateAnalyser;
	RpfResizeButton ddsLabel;
	RpfResizeButton ddsScoreOnOff;

	RpfResizeButton ddsStyle_btn;

	RpfResizeButton hHandsShowHideCap1_b;
	RpfResizeButton hHandsShowHideCap2_b;
	RpfResizeButton hiddenHandsShow_b;
	RpfResizeButton hiddenHandsHide_b;
//	RpfResizeButton hiddenHandsClick1_b;
//	RpfResizeButton hiddenHandsClick2_b;

	RpfResizeButton smLabelStudy;
	RpfResizeButton smLabelDeal;

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
			
		setLayout(new MigLayout(App.simple + ", flowy, align right", "[c]", 
				  "[]"         // Analyse
				+ "0.75%[]"      // KeepOn
				+ "7.0%[][]"     // DDS & isOn/isOff          
				+ "0.50%[]"      // ovals & squares single button
				+ "10.0%[][]"    // Non Kib Seats
				+ "0.75%[]"      // Show
				+ "0.75%[]"      // Hide
				+ "1%[][]"       // Study Mode 
				+ "1%[][]"       // Auto Enter
				+ "0.5%[]"       // isOn/isOff 
				));

		add(ddsAnalyse	         = new RpfResizeButton(Aaa.s_Std,         "ddsAnalyse", 70, 5));
		add(ddsReinstateAnalyser = new RpfResizeButton(Aaa.s_Std,         "ddsReinstateAnalyser", 55, 3));
		add(ddsLabel             = new RpfResizeButton(Aaa.s_BurstLabel,  "ddsLabel", 75, 6));
		ddsLabel.setBurstHint(0.89f);
		add(ddsScoreOnOff        = new RpfResizeButton(Aaa.s_Std,         "ddsScoreOnOff", 65, 5));
		add(ddsStyle_btn         = new RpfResizeButton(Aaa.g_oval_squ,    "ddsStyle_btn", 65, 4, 0.80f));


		add(hHandsShowHideCap1_b = new RpfResizeButton(Aaa.s_BurstLabel,  "hHandsShowHideCap1", 62, 4));
		    hHandsShowHideCap1_b.setBurstHint(0.89f);
		add(hHandsShowHideCap2_b = new RpfResizeButton(Aaa.s_BurstLabel,  "hHandsShowHideCap2", 62, 4));
		    hHandsShowHideCap2_b.setBurstHint(0.89f);
		    
		add(hiddenHandsShow_b     = new RpfResizeButton(Aaa.m_Std,         "hiddenHandsShow", 65, 6));		
		add(hiddenHandsHide_b     = new RpfResizeButton(Aaa.m_Std,         "hiddenHandsHide", 65, 6));		

		add(smLabelStudy   = new RpfResizeButton(Aaa.s_BurstLabel,         "smLabelStudy", 75, 7));
		    smLabelStudy.setBurstHint(1.5f);
		add(smLabelDeal  = new RpfResizeButton(Aaa.s_BurstLabel,           "smLabelDeal", 75, 7));
		    smLabelDeal.setBurstHint(1.5f);

		add(autoEnterLabelAuto   = new RpfResizeButton(Aaa.s_BurstLabel,   "autoEnterLabelAuto", 75, 5));
		    autoEnterLabelAuto.setBurstHint(0.58f);
		add(autoEnterLabelEnter  = new RpfResizeButton(Aaa.s_BurstLabel,   "autoEnterLabelEnter", 75, 5));
		    autoEnterLabelEnter.setBurstHint(0.7f);
		add(autoEnterOnOff       = new RpfResizeButton(Aaa.s_Std,          "autoEnterOnOff", 65, 5));

		// @formatter:on
	}

	/**   
	 */
	public void calcApplyBarVisiblity() {
		// =============================================================

		// @formatter:off
		// @formatter:on

		boolean sd = App.isStudyDeal();
		boolean sdDev = sd & !App.sd_dev_visibility;
		boolean insideDeal = (App.visualMode == App.Vm_InsideADeal);

		{
			boolean anaVisible = insideDeal && App.haglundsDDSavailable && !sdDev;
			ddsAnalyse.setVisible(anaVisible);

			boolean keepOnVisible = (anaVisible /* &&  App.pbnAutoEnter */ && (App.cameFromPbnOrSimilar() || App.isLin__Virgin())) && !sdDev;

			ddsReinstateAnalyser.setVisible(keepOnVisible && !sdDev);
			ddsReinstateAnalyser.setBackground(App.reinstateAnalyser ? Aaa.buttonBkgColorYes : Aaa.buttonBkgColorStd);

			boolean ddsVisible = (App.visualMode == App.Vm_InsideADeal) && App.haglundsDDSavailable && !sdDev;
			ddsLabel.setVisible(ddsVisible);
			ddsScoreOnOff.setVisible(ddsVisible);
			ddsScoreOnOff.setText(App.ddsScoreShow ? Aaf.rhp_isOn : Aaf.rhp_isOff);
			ddsScoreOnOff.setBackground(App.ddsScoreShow ? Aaa.buttonBkgColorYes : Aaa.buttonBkgColorStd);

			ddsStyle_btn.setVisible(ddsVisible && App.ddsScoreShow);
		}

		{
			boolean visible = (App.mg.lin.linType != Lin.FullMovie) || insideDeal;

			boolean handsShowing = App.localShowHidden;

			hHandsShowHideCap1_b.setVisible(visible && !sdDev);
			hHandsShowHideCap2_b.setVisible(visible && !sdDev);

			hiddenHandsShow_b.changeType((visible && !sdDev) ? (handsShowing ? Aaa.m_Label : Aaa.m_Std) : Aaa.m_Hidden);
			hiddenHandsHide_b.changeType((visible && !sdDev) ? (handsShowing ? Aaa.m_Std : Aaa.m_Label) : Aaa.m_Hidden);

			smLabelStudy.setVisible(/* insideDeal && */ sd);
			smLabelDeal.setVisible(/* insideDeal && */ sd);
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
