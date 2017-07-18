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
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.model.Cc;
import com.rogerpf.aabridge.model.Lin;

/**
 */
public class ButtonPanelLeft extends JPanel {
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	RpfResizeButton saveStd_b;
	RpfResizeButton saveAs_b;

	RpfResizeButton depFinOut_b;
	RpfResizeButton depFinIn_b;

	RpfResizeButton blueScore_b;
	RpfResizeButton purpleScore_b;

	RpfResizeButton editHandsRemoveRes_b;

	RpfResizeButton editHandsShuffOp_b;

	RpfResizeButton editHands_ssS_b;
	RpfResizeButton editHands_ssH_b;
	RpfResizeButton editHands_ssD_b;
	RpfResizeButton editHands_ssC_b;
	RpfResizeButton editHands_low_b;

	RpfResizeButton editHands_b;
	RpfResizeButton editHandsRotateAnti_b;
	RpfResizeButton editHandsRotateClock_b;

	RpfResizeButton editBidding_b;
	RpfResizeButton editBiddingWipe_b;

	RpfResizeButton editPlay_b;
	// RpfResizeButton editPlayWipe_b;

	RpfResizeButton ehere_b;
	RpfResizeButton edit_b;
	RpfResizeButton normal_b;
	RpfResizeButton review_b;

	/**
	 */
	ButtonPanelLeft() { /* Constructor */
		// ==============================================================================================
		setOpaque(false);
		// setBackground(Aaa.baizePink);
		// setBackground(Aaa.baizeGreen);

		// setPreferredSize(new Dimension(200, 1000)); // We just try to fill the available space

		// @formatter:off
		setLayout(new MigLayout(App.simple + ", flowy", "[c]", 
		   
		 "[]"                 // Save 
		 + "1%[]"			  // SaveAs           
//		 + "push"
		 + "2%[]"            // score - later split into 2
		 + "2%[]"              // Reset Restrictions and Shuf OOp 
		 + "1%[]"              // shuff op
		 + "3.5%[]"         //  suit re-order buttons slipt into 5
		 + "0.5%[]"           // Edit Hands - note the third 
		 + "0.5%[]"         // field is split (later)
		 + "2.5%[]0.5%[]"        // Edit Bidding + wipe
		 + "2.5%[]"        // Edit Play 
		 + "13%[]4.5%[]4.5%[]"   // EditHere - Edit - Normal - Review
		 + "push"
		 ));

		// @formatter:on

		add(saveStd_b = new RpfResizeButton(Aaa.s_Std, "menuSaveStd", 55, 3));
		add(saveAs_b = new RpfResizeButton(Aaa.s_Std, "menuSaveAs", 55, 3));

		add(blueScore_b = new RpfResizeButton(Aaa.s_SelfCmd, " ", 30, 3, 0.95f), "flowx, split 2");
		blueScore_b.setBackground(Aaa.teamBannerColorAy[0][0]);
		add(purpleScore_b = new RpfResizeButton(Aaa.s_SelfCmd, " ", 30, 3, 0.95f), "flowy");
		purpleScore_b.setBackground(Aaa.teamDDColorAy[1][0]);

		add(editHandsRemoveRes_b = new RpfResizeButton(Aaa.s_Std, "editHandsRemoveRes", 60, 3, 0.80f));
		add(editHandsShuffOp_b = new RpfResizeButton(Aaa.s_Std, "editHandsShuffOp", 66, 5, 0.72f));
		editHandsShuffOp_b.autoFontSizeAdjust = true;

		add(editHands_ssS_b = new RpfResizeButton(Aaa.s_Std, "editHands_ssS", 10, 3), "flowx, split 5");
		add(editHands_ssH_b = new RpfResizeButton(Aaa.s_Std, "editHands_ssH", 10, 3), "");
		add(editHands_ssD_b = new RpfResizeButton(Aaa.s_Std, "editHands_ssD", 10, 3), "");
		add(editHands_ssC_b = new RpfResizeButton(Aaa.s_Std, "editHands_ssC", 10, 3), "");
		add(editHands_low_b = new RpfResizeButton(Aaa.s_Std, "editHands_low", 14, 3), "");

		add(editHands_b = new RpfResizeButton(Aaa.m_Std, "editHands", 70, 4));

		add(editHandsRotateAnti_b = new RpfResizeButton(Aaa.s_Std, "editHandsRotateAnti", 15, 3), "flowx, split 2");
		editHandsRotateAnti_b.setForeground(Cc.RedStrong);

		add(editHandsRotateClock_b = new RpfResizeButton(Aaa.s_Std, "editHandsRotateClock", 15, 3), "flowy");
		editHandsRotateClock_b.setForeground(Cc.RedStrong);

		add(editBidding_b = new RpfResizeButton(Aaa.m_Std, "editBidding", 70, 4));
		add(editBiddingWipe_b = new RpfResizeButton(Aaa.s_Std, "editBiddingWipe", 34, 3));

		add(editPlay_b = new RpfResizeButton(Aaa.m_Std, "editPlay", 70, 4));

		add(edit_b = new RpfResizeButton(Aaa.m_Std, "leftWingEdit", 75, 6));

		add(normal_b = new RpfResizeButton(Aaa.m_Std, "leftWingNormal", 75, 6, 0.95f));

		add(review_b = new RpfResizeButton(Aaa.m_Std, "leftWingReview", 75, 6, 0.78f));
	}

	/**   
	 */
	public void calcApplyBarVisiblity() {
		// =============================================================

		// @formatter:off

		boolean insideADeal = App.isVmode_InsideADeal();
		boolean anyEdit = App.isModeAnyEdit() && insideADeal;
		boolean play = App.isMode(Aaa.NORMAL_ACTIVE) && insideADeal;
		boolean review = App.isModeAnyReview() && insideADeal;
		
		boolean editHands = App.isMode(Aaa.EDIT_HANDS) && insideADeal;
		boolean editBidding = App.isMode(Aaa.EDIT_BIDDING) && insideADeal;
		boolean editPlay = App.isMode(Aaa.EDIT_PLAY) && insideADeal;
		
		saveStd_b.setVisible( insideADeal && App.showSaveBtns);
		saveAs_b.setVisible(  insideADeal && App.showSaveBtns);
		
		setScoreDisplayVisibility();
		
		boolean showShuffOp = editHands || App.showShfWkPlBtn && insideADeal && (anyEdit || play || review);

		editHandsRemoveRes_b.setVisible( showShuffOp && App.deal.hasShufOpRestrictions());
		editHandsShuffOp_b.setVisible( showShuffOp);
		editHands_ssS_b.setVisible( editHands && App.showEdHandsSuitSwap);
		editHands_ssH_b.setVisible( editHands && App.showEdHandsSuitSwap);
		editHands_ssD_b.setVisible( editHands && App.showEdHandsSuitSwap);
		editHands_ssC_b.setVisible( editHands && App.showEdHandsSuitSwap);
		editHands_low_b.setVisible( editHands && App.showEdHandsSuitSwap);
		editHands_b.changeType( !anyEdit ? Aaa.m_Hidden : (editHands ? Aaa.m_Label : Aaa.m_Std) );
		editHandsRotateAnti_b.setVisible( editHands);
		editHandsRotateClock_b.setVisible( editHands);
		
		editBidding_b.changeType( !anyEdit ? Aaa.m_Hidden : (editBidding ? Aaa.m_Label : Aaa.m_Std) );
		editBiddingWipe_b.setVisible(editBidding);
		
		editPlay_b.changeType( !anyEdit ? Aaa.m_Hidden : (editPlay ? Aaa.m_Label : Aaa.m_Std) );
		//editPlayWipe_b.setVisible(editPlay);
		
		edit_b  .changeType(!insideADeal ? Aaa.m_Hidden : (anyEdit ? Aaa.m_Label : Aaa.m_Std) );
		normal_b.changeType(!insideADeal ? Aaa.m_Hidden : (play ? Aaa.m_Label : Aaa.m_Std) );
		review_b.changeType(!insideADeal ? Aaa.m_Hidden : (review ? Aaa.m_Label : Aaa.m_Std) );
		
		// @formatter:on
	}

	/**   
	 */
	public void setScoreDisplayVisibility() {

		// @formatter:off
		boolean vis = (App.mg.lin.linType == Lin.VuGraph) 
				   && (App.mg.ddAy != null) 
				   && (App.mg.ddAy.isEmpty() == false) 
				   &&  App.mg.ddAy.twoColumn
				   &&  App.showDdWithResults
				   &&  App.showDdResultTots;
		// @formatter:on

		blueScore_b.setVisible(vis);
		purpleScore_b.setVisible(vis);
	}

	/**   
	 */
	public void dealMajorChange() {
		blueScore_b.setText(App.deal.blueScore);
		purpleScore_b.setText(App.deal.purpleScore);
	}

}
