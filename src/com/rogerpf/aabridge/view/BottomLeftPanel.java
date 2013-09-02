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
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.miginfocom.swing.MigLayout;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;
import com.rpsd.bridgefonts.BridgeFonts;

/**
 */
public class BottomLeftPanel extends ClickPanel {

	private static final long serialVersionUID = 1L;

	public EmptyPanel c0_2_0__empt = new EmptyPanel();
	public MsgDisplayPanel c0_2_0__mdp = new MsgDisplayPanel();
	public WeTheyScorePanel c0_2_0__wtsp = new WeTheyScorePanel();
	public UndoPanel c0_2_0__undo = new UndoPanel();
	public NextBoardButtonPanel c0_2_1__nbbp = new NextBoardButtonPanel();

	/**
	 */
	BottomLeftPanel() { /* Constructor */

		setLayout(new MigLayout("insets 0 0 0 0, gap 0! 0!, flowy", "3%[]4%", "5%[70%]5%[20%]"));
		add(c0_2_0__empt, "hidemode 3, width 100%, height 100%");
		add(c0_2_0__mdp, " hidemode 3, width 100%, height 100%");
		add(c0_2_0__wtsp, "hidemode 3, width 100%, height 100%");
		add(c0_2_0__undo, "hidemode 3, width 100%, height 100%");
		add(c0_2_1__nbbp, "            width 100%, height 100%");

	}

	/**   
	 */
	public void dealMajorChange() {

	}

	/**
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		setBackground(Aaa.baizeGreen);
	}

	/**   
	 */
	public void matchPanelsToDealState() {

		if (App.isMode(Aaa.REVIEW_PLAY) || App.isMode(Aaa.REVIEW_BIDDING) || App.isMode(Aaa.EDIT_CHOOSE) || App.isMode(Aaa.EDIT_HANDS)) {

			c0_2_0__empt.setVisible(true);
			c0_2_0__undo.setVisible(false);
			c0_2_0__mdp.setVisible(false);
			c0_2_0__wtsp.setVisible(false);
		}
		else if (App.deal.isBidding() || App.isMode(Aaa.EDIT_BIDDING)) {

			c0_2_0__empt.setVisible(false);
			boolean showMsg = (!App.isAutoBid(App.deal.getNextHandToBid().compass)) && !App.isMode(Aaa.EDIT_BIDDING) && App.showBidPlayMsgs;
			c0_2_0__undo.setVisible(!showMsg);
			c0_2_0__mdp.textArea.setText("\nPlease BID using the Bidding\nPanel and or your Keyboard.");
			c0_2_0__mdp.setVisible(showMsg);
			c0_2_0__wtsp.setVisible(false);
		}
		else if (App.deal.isPlaying()) {

			c0_2_0__empt.setVisible(false);
			boolean showMsg = (!App.isAutoPlay(App.deal.getNextHandToPlay().compass)) && !App.isMode(Aaa.EDIT_PLAY) && App.showBidPlayMsgs
					&& App.deal.lessThanTwoCardsPlayed();
			c0_2_0__undo.setVisible(!showMsg);
			c0_2_0__mdp.textArea.setText("\nPLAY by clicking on a CARD\nor by using your Keyboard.");
			c0_2_0__mdp.setVisible(showMsg);
			c0_2_0__wtsp.setVisible(false);
		}
		else if (App.deal.isFinished()) {
			boolean showUndo = (App.isMode(Aaa.EDIT_PLAY) || App.isMode(Aaa.EDIT_BIDDING));
			c0_2_0__empt.setVisible(false);
			c0_2_0__undo.setVisible(showUndo);
			c0_2_0__mdp.setVisible(false);
			c0_2_0__wtsp.setVisible(!showUndo);
		}
		else {
			assert (false);
		}

		c0_2_1__nbbp.setReviewButtonText();

		// setEotClickLabelVisibility();
		c0_2_0__undo.editPlay2.setVisible(App.showEditPlay2Btn && (App.isMode(Aaa.NORMAL) || App.isMode(Aaa.REVIEW_BIDDING) || App.isMode(Aaa.REVIEW_PLAY)));
		c0_2_0__undo.claimBtn.setVisible(App.showClaimBtn && (App.isMode(Aaa.NORMAL) || App.isMode(Aaa.EDIT_PLAY)));
	}

	public void hideClaimButtonsIfShowing() {
		// TODO Auto-generated method stub
		c0_2_0__undo.hideClaimButtonsIfShowing();
	}

}

/**   
 */
class UndoPanel extends ClickPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	public RpfResizeButton editPlay2;
	public RpfResizeButton claimBtn;
	RpfResizeButton claimValBtns[] = new RpfResizeButton[14];

	/**
	 */
	UndoPanel() { /* Constructor */

		Font cardFaceFont = BridgeFonts.faceAndSymbFont.deriveFont(24f);

		setLayout(new MigLayout("insets 0 0 0 0, gap 0! 0!, flowx", "5%[]push[]5%", "12%[]10%[]5%[]"));

		RpfResizeButton b;

		b = new RpfResizeButton(1, "editPlay2", 22, 18);
		add(b, "");
		editPlay2 = b;

		b = new RpfResizeButton(1, "mainUndo", -3, 25);
		add(b, "align right, wrap");

		b = new RpfResizeButton(0, "Claim", 22, 18);
		b.addActionListener(this);
		add(b, "hidemode 0, span 2, split 8");
		claimBtn = b;

		for (int i = 0; i <= 5; i++) {
			b = new RpfResizeButton(0, String.valueOf(i), 8, 16);
			b.addActionListener(this);
			b.setFont(cardFaceFont);
			add(b, "hidemode 3" + ((i == 5) ? ", wrap" : "") + ((i == 0) ? ", gapx 4%" : ""));
			b.setVisible(false);
			claimValBtns[i] = b;
		}

		b = new RpfResizeButton(0, "invis", 5, 16);
		add(b, "hidemode 0, span 2, split 9");
		b.setVisible(false);

		for (int i = 6; i <= 13; i++) {
			b = new RpfResizeButton(0, String.valueOf(i), 8, 16);
			b.addActionListener(this);
			b.setFont(cardFaceFont);
			add(b, "hidemode 3");
			b.setVisible(false);
			claimValBtns[i] = b;
		}
	}

	/**
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		setBackground(Aaa.baizeGreen);
	}

	public void hideClaimButtonsIfShowing() {
		if (claimValBtns[0].isVisible()) {
			for (RpfResizeButton b : claimValBtns) {
				b.setVisible(false);
			}
		}
	}

	public void actionPerformed(ActionEvent e) {

		String cmd = e.getActionCommand();

		if (cmd.contentEquals("Claim")) {

			if (claimValBtns[0].isVisible()) {
				hideClaimButtonsIfShowing();
				return;
			}

			if (!(App.deal.isPlaying() && (App.isMode(Aaa.NORMAL) || App.isMode(Aaa.EDIT_PLAY)))) {
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

		Object source = e.getSource();
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
