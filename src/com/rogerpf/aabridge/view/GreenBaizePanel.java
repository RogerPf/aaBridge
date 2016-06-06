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

import java.awt.Dimension;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.model.Dir;
import com.rogerpf.aabridge.model.Hand;
import com.rogerpf.aabridge.model.Zzz;

/**
 */
public class GreenBaizePanel extends ClickPanel {
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	// App.bpl = see the constructor
	JPanel c0 = new ClickPanel();
	JPanel c1 = new ClickPanel();
	JPanel c2 = new ClickPanel();
	// App.bpr = see the constructor

	// @formatter:off
	public TopLeftPanel             c0_0__tlp  = new TopLeftPanel();
	public BottomLeftPanel          c0_2__blp  = new BottomLeftPanel();

	public JPanel                   c1_1__empt = new TransparentPanel();
	public TricksFourDisplayPanel   c1_1__tfdp = new TricksFourDisplayPanel();
	public BidsFourDisplayPanel     c1_1__bfdp = new BidsFourDisplayPanel();
	public MsgDisplayPanel          c1_1__mdp  = new MsgDisplayPanel();

	public BidTablePanel            c2_0__btp  = new BidTablePanel();
	public BidTablePanelBlank       c2_0__btpBlank = new BidTablePanelBlank();
	public DdsAnalyserPanel         c2_0__ddsAnal = new DdsAnalyserPanel();
	public JPanel                   c2_2__empt = new TransparentPanel();
	public CompletedTricksPanel     c2_2__ctp  = new CompletedTricksPanel();
	public BidButtsPanel            c2_2__bbp  = new BidButtsPanel(true /* greenBackground */);

	public HandDisplayPanel[] hdps = { 
			new HandDisplayPanel(Dir.North),
			new HandDisplayPanel(Dir.East), 
			new HandDisplayPanel(Dir.South),
			new HandDisplayPanel(Dir.West) };
	// @formatter:on

	/**   
	 */
	public int getTrueWidth() {
		return c0.getWidth() + c1.getWidth() + c2.getWidth();
	}

	/**   
	 */
	public HandDisplayPanel getHandDisplayPanel(Hand hand) {
		return hdps[App.phyScreenPosFromCompass(hand.compass).v];
	}

	/**   
	 */
	public void dealDirectionChange() {

		for (HandDisplayPanel hdp : hdps) {
			hdp.dealDirectionChange();
		}
		c1_1__tfdp.dealDirectionChange();
		c1_1__tfdp.dealDirectionChange();
	}

	/**   
	 */
	public void dealMajorChange() {

		for (HandDisplayPanel hdp : hdps) {
			hdp.dealMajorChange(App.deal);
		}
		c0_0__tlp.dealMajorChange();
		c1_1__tfdp.dealMajorChange();
		c2_0__btp.dealMajorChange();
		c2_2__bbp.dealMajorChange();

		App.bpl.dealMajorChange();
	}

	/**   
	 */
	public void biddingDisplayToggle() {
		boolean showBidTable = true;
		if (App.deal.isPlaying()) {
			showBidTable = !App.gbp.c2_0__btp.isVisible();
		}
		App.gbp.c2_0__btp.setVisible(showBidTable);
		App.gbp.c2_0__btpBlank.setVisible(!showBidTable);
		App.frame.repaint();
	}

	/**   
	 */
	public void biddingDisplayTrue() {
		boolean showBidTable = true;
		App.gbp.c2_0__btp.setVisible(showBidTable);
		App.gbp.c2_0__btpBlank.setVisible(!showBidTable);
		App.frame.repaint();
	}

	/**   
	 */
	public GreenBaizePanel() { /* Constructor */
		// setOpaque(false);
		// setOpaque(true);

		// setBackground(Aaa.baizePink);

		App.bpl = new ButtonPanelLeft();
		App.bpr = new ButtonPanelRight();

		Dimension tryToBeAsBigAsPossible = new Dimension(5000, 5000);
		setPreferredSize(tryToBeAsBigAsPossible); // So that AspectBoundable can later squish us down
		Dimension small = new Dimension(42, 27);
		setMinimumSize(small);

		// Set up the content pane.

		c0.setLayout(new MigLayout(App.simple + ", flowy", "", App.frame.layOut_rowsA__gbp));
		c0.add(c0_0__tlp, App.hm3oneHun);
		c0.add(hdps[Dir.West.v], App.hm3oneHun);
		c0.add(c0_2__blp, App.hm3oneHun);

		c1.setLayout(new MigLayout(App.simple + ", flowy", "", App.frame.layOut_rowsB__gbp));
		c1.add(hdps[Dir.North.v], App.hm3oneHun);
		c1.add(c1_1__empt, App.hm3oneHun);
		c1.add(c1_1__bfdp, App.hm3oneHun);
		c1.add(c1_1__tfdp, App.hm3oneHun);
		c1.add(c1_1__mdp, App.hm3oneHun);
		c1.add(hdps[Dir.South.v], App.hm3oneHun);

		c2.setLayout(new MigLayout(App.simple + ", flowy", "", App.frame.layOut_rowsA__gbp));
		c2.add(c2_0__btp, App.hm3oneHun);
		c2.add(c2_0__btpBlank, App.hm3oneHun);
		c2.add(c2_0__ddsAnal, App.hm3oneHun);
		c2.add(hdps[Dir.East.v], App.hm3oneHun);
		c2.add(c2_2__empt, App.hm3oneHun);
		c2.add(c2_2__bbp, App.hm3oneHun);
		c2.add(c2_2__ctp, App.hm3oneHun);

		setLayout(new MigLayout(App.simple, App.frame.layOut_columns__gbp, "[100%]"));
		add(App.bpl, App.hm3oneHun);
		add(c0, App.hm3oneHun);
		add(c1, App.hm3oneHun);
		add(c2, App.hm3oneHun);
		add(App.bpr, App.hm3oneHun);

		// setWings(wings_off, true /* recalc */);

		App.calcApplyBarVisiblity();
	}

	/**   
	 */
	public void matchPanelsToDealState() {
		// =============================================================

		// c0_0__ltp is always visible
		// c0_2__blp is always visible

		boolean tutorial = App.isVmode_Tutorial();

		// boolean tutForceAuctionShow = tutorial && App.tutorialShowAuction;
		// boolean tutForceAuctionHide = tutorial && !App.tutorialShowAuction;

		boolean alwaysShowAnal = (!tutorial && App.ddsAnalyserPanelVisible);
		// boolean alwaysShowBidding = (tutorial && App.tutorialShowAuction || !tutorial && !alwaysShowAnal);
		boolean alwaysShowBidding = (App.tutorialShowAuction && !alwaysShowAnal);
		boolean alwaysShowBlank = !alwaysShowAnal && !alwaysShowBidding;

		if (App.isMode(Aaa.EDIT_HANDS)) {

			c1_1__empt.setVisible(false);
			c1_1__bfdp.setVisible(false);
			c1_1__tfdp.setVisible(false);
			c1_1__mdp.textArea.setText("Drag and Drop the cards from\none hand to another.\n"
					+ "First, for all hands, move ALL\nthe spades, ONLY THEN\nstart on the hearts ...");
			c1_1__mdp.setVisible(true);

			c2_0__btpBlank.setVisible(alwaysShowBlank);
			c2_0__btp.setVisible(alwaysShowBidding);
			c2_0__ddsAnal.setVisible(alwaysShowAnal);

			c2_2__empt.setVisible(true);
			c2_2__bbp.setVisible(false);
			c2_2__ctp.setVisible(false);

		}
		else if (App.deal.isBidding() || App.isMode(Aaa.EDIT_BIDDING) || App.isMode(Aaa.REVIEW_BIDDING)) {

			boolean showBB = !App.isMode(Aaa.REVIEW_BIDDING) && App.isVmode_InsideADeal() && (App.deal.isBidding() || App.isMode(Aaa.EDIT_BIDDING));
			boolean showCentralBidding = (tutorial && App.tutorialShowAuction) || (!App.deal.isIncomplete()); // 1 to 51 cards delt so far

			c1_1__empt.setVisible(false);
			c1_1__bfdp.setVisible(showCentralBidding);
			c1_1__tfdp.setVisible(!showCentralBidding);
			c1_1__mdp.setVisible(false);

			c2_0__btpBlank.setVisible(alwaysShowBlank);
			c2_0__btp.setVisible(alwaysShowBidding);
			c2_0__ddsAnal.setVisible(alwaysShowAnal);

			c2_2__empt.setVisible(!showBB);
			c2_2__bbp.setVisible(showBB);
			c2_2__ctp.setVisible(false);

		}
		else if (App.deal.isPlaying() || App.isMode(Aaa.REVIEW_PLAY)) {

			c1_1__empt.setVisible(false);
			c1_1__bfdp.setVisible(false);
			c1_1__tfdp.setVisible(true);
			c1_1__mdp.setVisible(false);

			c2_0__btpBlank.setVisible(alwaysShowBlank);
			c2_0__btp.setVisible(alwaysShowBidding);
			c2_0__ddsAnal.setVisible(alwaysShowAnal);

			c2_2__empt.setVisible(false);
			c2_2__bbp.setVisible(false);
			c2_2__ctp.setVisible(true);

		}
		else { // board finished

			c1_1__empt.setVisible(false);
			c1_1__bfdp.setVisible(false);
			c1_1__tfdp.setVisible(true);
			c1_1__mdp.setVisible(false);

			c2_0__btpBlank.setVisible(!alwaysShowBidding && !alwaysShowAnal);
			c2_0__btp.setVisible(alwaysShowBidding);
			c2_0__ddsAnal.setVisible(alwaysShowAnal);

			c2_2__empt.setVisible(false);
			c2_2__bbp.setVisible(false);
			c2_2__ctp.setVisible(true);

		}

		c0_2__blp.matchPanelsToDealState();

		c0_0__tlp.setButtonVisibility();

		App.calcApplyBarVisiblity();

		validate();
	}

	/**   
	 */
	public void kick() { // GreenBaizePanel
		// this helps to get column 0 re-laying out - goodness knows why
		c0.setVisible(false);
		c0.setVisible(true);
	}

	/**   
	 */
	public boolean areAllThreeColumnsMatchedInSize(int ticksRemaining) { // GreenBaizePanel
		{
			Dimension dAy[] = { c0.getSize(), c1.getSize(), c2.getSize() };

			for (int i : Zzz.zto1) {
				Dimension da = dAy[i];
				Dimension db = dAy[i + 1];

				if (ticksRemaining > 34) {
					if (da.width != db.width || da.width != db.width)
						return false;
					if (da.height != db.height || da.height != db.height)
						return false;
				}
				else if (ticksRemaining > 30) {
					if (da.width + 1 < db.width || da.width == db.width)
						return false;
					if (da.height + 1 < db.height || da.height == db.height)
						return false;
				}
				else {
					if (da.width + 1 < db.width || da.width - 1 > db.width)
						return false;
					if (da.height + 1 < db.height || da.height - 1 > db.height)
						return false;
				}
			}
		}

//		{
//			Dimension dAy[] = { C0_9__rbp0.getSize(), C1_9__rbp1.getSize(), C2_9__rbc2.getSize() };
//
//			for (int i : Zzz.zto1) {
//				Dimension da = dAy[i];
//				Dimension db = dAy[i + 1];
//
//				if (ticksRemaining > 34) {
//					if (da.width != db.width || da.width != db.width)
//						return false;
//					if (da.height != db.height || da.height != db.height)
//						return false;
//				}
//				else if (ticksRemaining > 30) {
//					if (da.width + 1 < db.width || da.width == db.width)
//						return false;
//					if (da.height + 1 < db.height || da.height == db.height)
//						return false;
//				}
//				else {
//					if (da.width + 1 < db.width || da.width - 1 > db.width)
//						return false;
//					if (da.height + 1 < db.height || da.height - 1 > db.height)
//						return false;
//				}
//
//			}
//		}

		return true;
	}

	public void hideClaimButtonsIfShowing() {

		c0_2__blp.hideClaimButtonsIfShowing();

	}

}
