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

import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.miginfocom.swing.MigLayout;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.Aaf;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.model.Cc;
import com.rogerpf.aabridge.model.Lin;
import com.rpsd.bridgefonts.BridgeFonts;

/**
 */
public class BottomLeftPanel extends ClickPanel {

	private static final long serialVersionUID = 1L;

	public ClickPanel c0_2_0__empt = new ClickPanel();
	public MsgDisplayPanel c0_2_0__mdp = new MsgDisplayPanel();
	public WeTheyScorePanel c0_2_0__wtsp = new WeTheyScorePanel();
	public ClaimPanel c0_2_0__clam = new ClaimPanel();
	public NextBoardButtonPanel c0_2_1__nbbp = new NextBoardButtonPanel();

	/**
	 */
	BottomLeftPanel() { /* Constructor */
		setOpaque(false);

		setLayout(new MigLayout(App.simple + ", flowy", "3%[]4%", "5%[70%]5%[20%]"));
		add(c0_2_0__empt, "hidemode 3, width 100%, height 100%");
		add(c0_2_0__mdp, " hidemode 3, width 100%, height 100%");
		add(c0_2_0__wtsp, "hidemode 3, width 100%, height 100%");
		add(c0_2_0__clam, "hidemode 3, width 100%, height 100%");
		add(c0_2_1__nbbp, "            width 100%, height 100%");

	}

	/**   
	 */
	public void dealMajorChange() {

	}

	/**   
		 */
	public void matchPanelsToDealState() {

		if (App.isVmode_Tutorial()) {
			boolean showScore = App.deal.isFinished() && App.tutorialShowAuction;
			c0_2_0__empt.setVisible(!showScore);
			c0_2_0__clam.setVisible(false);
			c0_2_0__mdp.setVisible(false);
			c0_2_0__wtsp.setVisible(showScore);
		}
		else if (App.isMode(Aaa.REVIEW_PLAY) || App.isMode(Aaa.REVIEW_BIDDING) || App.isModeAnyEdit()) {

			boolean show_wtsp = App.tutorialShowAuction && App.isMode(Aaa.REVIEW_PLAY) && App.deal.isFinished()
					&& ((App.reviewTrick * 4 + App.reviewCard) >= App.deal.countCardsPlayed());

			boolean clam = (!show_wtsp && App.isMode(Aaa.EDIT_PLAY) && (App.deal.isFinished() == false));

			c0_2_0__empt.setVisible(!show_wtsp && !clam);
			c0_2_0__clam.setVisible(clam);
			c0_2_0__mdp.setVisible(false);
			c0_2_0__wtsp.setVisible(show_wtsp);
		}
		else if (App.deal.isBidding() || App.isMode(Aaa.EDIT_BIDDING)) {

			c0_2_0__empt.setVisible(false);
			boolean showMsg = (!App.isAutoBid(App.deal.getNextHandToBid().compass)) && !App.isMode(Aaa.EDIT_BIDDING) && App.showBidPlayMsgs;
			c0_2_0__clam.setVisible(!showMsg);
			c0_2_0__mdp.textArea.setText(Aaf.instruct_bid);
			c0_2_0__mdp.setVisible(showMsg);
			c0_2_0__wtsp.setVisible(false);
		}
		else if (App.deal.isPlaying()) {

			c0_2_0__empt.setVisible(false);
//			boolean xx = App.deal.lessThanTwoCardsPlayed();
//			boolean yy = (!App.isAutoPlay(App.deal.getNextHandToPlay().compass));
			boolean showMsg = (!App.isAutoPlay(App.deal.getNextHandToPlay().compass)) && !App.isMode(Aaa.EDIT_PLAY) && App.showBidPlayMsgs
					&& App.deal.lessThanTwoCardsPlayed();
			c0_2_0__clam.setVisible(!showMsg);
			c0_2_0__mdp.textArea.setText(Aaf.instruct_play);
			c0_2_0__mdp.setVisible(showMsg);
			c0_2_0__wtsp.setVisible(false);
		}
		else if (App.deal.isFinished()) {

			// boolean showUndo = (App.isMode(Aaa.EDIT_PLAY) || App.isMode(Aaa.EDIT_BIDDING));
			boolean showScore = (App.tutorialShowAuction);
			c0_2_0__empt.setVisible(!showScore);
			c0_2_0__clam.setVisible(false);
			c0_2_0__mdp.setVisible(false);
			c0_2_0__wtsp.setVisible(showScore);
		}
		else {
			assert (false);
		}

		c0_2_1__nbbp.set_NextBoard_and_Undo_visibility();

		c0_2_0__clam.claimBtn.setVisible(App.showClaimBtn && (App.isMode(Aaa.NORMAL_ACTIVE) || App.isMode(Aaa.EDIT_PLAY)));
	}

	public void hideClaimButtonsIfShowing() {
		c0_2_0__clam.hideClaimButtonsIfShowing();
	}

}

/**   
 */
class ClaimPanel extends ClickPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	public RpfResizeButton claimBtn;
	RpfResizeButton claimValBtns[] = new RpfResizeButton[14];

	/**
	 */
	ClaimPanel() { /* Constructor */

		setOpaque(false);

		Font cardFaceFont = BridgeFonts.faceAndSymbolFont.deriveFont(24f);

		setLayout(new MigLayout("insets 0 0 0 0, gap 0! 0!, flowx", "1%[]push[]1%", "22%[]10%[]5%[]"));

		RpfResizeButton b;

		b = new RpfResizeButton(Aaa.s_SelfCmd, Aaf.gT("playBridge.claim"), 32, 18);
		b.addActionListener(this);
		add(b, "hidemode 0, span 2, split 8");
		claimBtn = b;

		for (int i = 0; i <= 5; i++) {
			b = new RpfResizeButton(Aaa.s_SelfCmd, String.valueOf(i), 8, 16);
			b.addActionListener(this);
			b.setFont(cardFaceFont);
			add(b, "hidemode 3" + ((i == 5) ? ", wrap" : "") + ((i == 0) ? ", gapx 4%" : ""));
			b.setVisible(false);
			claimValBtns[i] = b;
		}

		b = new RpfResizeButton(Aaa.s_SelfCmd, "invis", 5, 16);
		add(b, "hidemode 0, span 2, split 9");
		b.setVisible(false);

		for (int i = 6; i <= 13; i++) {
			b = new RpfResizeButton(Aaa.s_SelfCmd, String.valueOf(i), 8, 16);
			b.addActionListener(this);
			b.setFont(cardFaceFont);
			add(b, "hidemode 3");
			b.setVisible(false);
			claimValBtns[i] = b;
		}
	}

	public void hideClaimButtonsIfShowing() {
		if (claimValBtns[0].isVisible()) {
			for (RpfResizeButton b : claimValBtns) {
				b.setVisible(false);
			}
		}
	}

	public void actionPerformed(ActionEvent e) {

		Object source = e.getSource();

		if (source == claimBtn) {

			if (claimValBtns[0].isVisible()) {
				hideClaimButtonsIfShowing();
				return;
			}

			if (!(App.deal.isPlaying() && (App.isMode(Aaa.NORMAL_ACTIVE) || App.isMode(Aaa.EDIT_PLAY)))) {
				hideClaimButtonsIfShowing();
				return;
			}

			Point score = App.deal.getContractTrickCountSoFar();
			int remainingTricks = 13 - (score.x + score.y);
			if (remainingTricks == 0)
				hideClaimButtonsIfShowing();
			else
				for (int i = 0; i < remainingTricks + 1; i++) {
					claimValBtns[i].setVisible(true);
				}
			return;
		}

		// Object source = e.getSource();
		for (int i = 0; i < claimValBtns.length; i++) {
			if (claimValBtns[i] == source) {

				Point score = App.deal.getContractTrickCountSoFar();
				int ourScore = (App.deal.isYouSeatDeclarerAxis() ? score.x : score.y);
				// int remainingTricks = 13 - (score.x + score.y);
				int claim = ourScore + i;

				// we need to turn this into the declarer perspective
				if (App.deal.isYouSeatDeclarerAxis() == false) {
					claim = 13 - claim;
				}
				hideClaimButtonsIfShowing();
				App.deal.claimTricks(claim);
				App.gbp.matchPanelsToDealState();
				App.frame.repaint();
			}
		}

	}
}

/**   
 */
class NextBoardButtonPanel extends ClickPanel {
	private static final long serialVersionUID = 1L;

	public RpfResizeButton mainNewBoard;
	public RpfResizeButton mainUndo;

	/**
	 */
	NextBoardButtonPanel() { /* Constructor */
		setOpaque(false);

		setLayout(new MigLayout(App.simple, "[]push[]5%", "[]10%"));

		RpfResizeButton b;

		b = new RpfResizeButton(Aaa.s_Std, "mainNewBoard", 50, 85, 0.75f);
		b.setForeground(Cc.RedStrong);
		add(b, "wmin 10%");

		mainNewBoard = b;

		b = new RpfResizeButton(Aaa.s_Std, "mainUndo", 32, 85);
		add(b, "");

		mainUndo = b;

		setVisible(true);
	}

	/**
	 */
	public void set_NextBoard_and_Undo_visibility() {
		mainNewBoard.setVisible(/*App.mg.lin.linType == Lin.SimpleDealSingle ||*/App.mg.lin.linType == Lin.SimpleDealVirgin);
		mainUndo.setVisible(App.isVmode_InsideADeal() && (App.isModeAnyReview() == false));
	}

}
