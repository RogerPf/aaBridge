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
import java.awt.Graphics;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.model.Hand;
import com.rogerpf.aabridge.model.Zzz;

/**   
 */
public class GreenBaizePanel extends ClickPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JPanel c0 = new DarkGrayBgPanel();
	JPanel c1 = new DarkGrayBgPanel();
	JPanel c2 = new DarkGrayBgPanel();

	//@formatter:off
	public TopLeftPanel             c0_0__tlp  = new TopLeftPanel();
	public BottomLeftPanel          c0_2__blp  = new BottomLeftPanel();
	public DarkGrayBgPanel          C0_9__empt = new DarkGrayBgPanel();
	public ReviewBarBiddingPart0    C0_9__rbb0 = new ReviewBarBiddingPart0();
	public ReviewBarPlayPart0       C0_9__rbp0 = new ReviewBarPlayPart0();
	public EditBarChoosePart0       C0_9__ech0 = new EditBarChoosePart0();
	public PinkBgPanel           	C0_9__empp = new PinkBgPanel();
	
	public EmptyPanel               c1_1__empt = new EmptyPanel();
	public TricksFourDisplayPanel   c1_1__tfdp = new TricksFourDisplayPanel();
	public BidsFourDisplayPanel     c1_1__bfdp = new BidsFourDisplayPanel();
	public MsgDisplayPanel          c1_1__mdp  = new MsgDisplayPanel();
	public DarkGrayBgPanel          C1_9__empt = new DarkGrayBgPanel();
	public ReviewBarBiddingPart1    C1_9__rbb1 = new ReviewBarBiddingPart1();
	public ReviewBarPlayPart1       C1_9__rbp1 = new ReviewBarPlayPart1();
	public EditBarChoosePart1       C1_9__ech1 = new EditBarChoosePart1();
	public EditBiddingPart1      	C1_9__xall = new EditBiddingPart1();

	public BidTablePanel            c2_0__btp  = new BidTablePanel();
	public BidTablePanelBlank       c2_0__btpBlank = new BidTablePanelBlank();
	public EmptyPanel               c2_2__empt = new EmptyPanel();
	public CompletedTricksPanel     c2_2__ctp  = new CompletedTricksPanel();
	public BidButtsPanel            c2_2__bbp  = new BidButtsPanel();
	public DarkGrayBgPanel          C2_9__empt = new DarkGrayBgPanel();
	public ReviewBarCmnPart2        C2_9__rbc2 = new ReviewBarCmnPart2();
	public EditBarChoosePart2       C2_9__ech2 = new EditBarChoosePart2();
	public EditPlayPart2          	C2_9__xall = new EditPlayPart2();
	
	public HandDisplayPanel[] hdps = { 
			new HandDisplayPanel(0),
			new HandDisplayPanel(1), 
			new HandDisplayPanel(2),
			new HandDisplayPanel(3) };
	//@formatter:on

	/**   
	 */
	public HandDisplayPanel getHandDisplayPanel(Hand hand) {
		return hdps[App.phyScreenPosFromCompass(hand.compass)];
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
			hdp.dealMajorChange();
		}
		c0_0__tlp.dealMajorChange();
		c1_1__tfdp.dealMajorChange();
		c2_2__bbp.dealMajorChange();
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
	public GreenBaizePanel() { /* Constructor */

		Dimension ratio = new Dimension(400, 270);
		setPreferredSize(ratio); // Really sets the RATIO of the sides
		setMinimumSize(ratio);

		// Set up the content pane.
		String hm3oneHun = "hidemode 3, width 100%, height 100%";
		String simple = "gap 0!, insets 0 0 0 0"; // , nocache";

		c0.setLayout(new MigLayout("flowy, " + simple, "", "1%[28.35%][33.3%][28.35%]3%[6%]"));
		c0.add(c0_0__tlp, hm3oneHun);
		c0.add(hdps[Zzz.West], hm3oneHun);
		c0.add(c0_2__blp, hm3oneHun);
		c0.add(C0_9__empt, hm3oneHun);
		c0.add(C0_9__rbb0, hm3oneHun);
		c0.add(C0_9__rbp0, hm3oneHun);
		c0.add(C0_9__ech0, hm3oneHun);
		c0.add(C0_9__empp, hm3oneHun);

		c1.setLayout(new MigLayout("flowy, " + simple, "", "1%[33.3%][23.4%][33.3%]3%[6%]"));
		c1.add(hdps[Zzz.North], hm3oneHun);
		c1.add(c1_1__empt, hm3oneHun);
		c1.add(c1_1__bfdp, hm3oneHun);
		c1.add(c1_1__tfdp, hm3oneHun);
		c1.add(c1_1__mdp, hm3oneHun);
		c1.add(hdps[Zzz.South], hm3oneHun);
		c1.add(C1_9__empt, hm3oneHun);
		c1.add(C1_9__rbb1, hm3oneHun);
		c1.add(C1_9__rbp1, hm3oneHun);
		c1.add(C1_9__ech1, hm3oneHun);
		c1.add(C1_9__xall, hm3oneHun);

		c2.setLayout(new MigLayout("flowy, " + simple, "", "1%[28.35%][33.3%][28.35%]3%[6%]"));
		c2.add(c2_0__btp, hm3oneHun);
		c2.add(c2_0__btpBlank, hm3oneHun);
		c2.add(hdps[Zzz.East], hm3oneHun);
		c2.add(c2_2__empt, hm3oneHun);
		c2.add(c2_2__bbp, hm3oneHun);
		c2.add(c2_2__ctp, hm3oneHun);
		c2.add(C2_9__empt, hm3oneHun);
		c2.add(C2_9__rbc2, hm3oneHun);
		c2.add(C2_9__ech2, hm3oneHun);
		c2.add(C2_9__xall, hm3oneHun);

		setLayout(new MigLayout(simple, "0.5%[33%][33%][33%]0.5%", ""));
		add(c0, hm3oneHun);
		add(c1, hm3oneHun);
		add(c2, hm3oneHun);

		applyReviewBarVisiblity();
	}

	/**   
	 */
	public void matchPanelsToDealState() {

		// c0_0__ltp is always visible
		// c0_2__blp is always visible

		if (App.isMode(Aaa.EDIT_CHOOSE)) {

			c1_1__empt.setVisible(true);
			c1_1__bfdp.setVisible(false);
			c1_1__tfdp.setVisible(false);
			c1_1__mdp.setVisible(false);

			c2_0__btpBlank.setVisible(true);
			c2_0__btp.setVisible(false);

			c2_2__empt.setVisible(true);
			c2_2__bbp.setVisible(false);
			c2_2__ctp.setVisible(false);

		}
		else if (App.isMode(Aaa.EDIT_HANDS)) {

			c1_1__empt.setVisible(false);
			c1_1__bfdp.setVisible(false);
			c1_1__tfdp.setVisible(false);
			c1_1__mdp.textArea.setText("Drag and Drop the cards from\none hand to another.\n"
					+ "First, for all hands, move ALL\nthe spades, ONLY THEN\nstart on the hearts ...");
			c1_1__mdp.setVisible(true);

			c2_0__btpBlank.setVisible(true);
			c2_0__btp.setVisible(false);

			c2_2__empt.setVisible(true);
			c2_2__bbp.setVisible(false);
			c2_2__ctp.setVisible(false);

		}
		else if (App.deal.isBidding() || App.isMode(Aaa.EDIT_BIDDING) || App.isMode(Aaa.REVIEW_BIDDING)) {

			boolean showBB = App.deal.isBidding() || App.isMode(Aaa.EDIT_BIDDING);

			c1_1__empt.setVisible(false);
			c1_1__bfdp.setVisible(true);
			c1_1__tfdp.setVisible(false);
			c1_1__mdp.setVisible(false);

			c2_0__btpBlank.setVisible(false);
			c2_0__btp.setVisible(true);

			c2_2__empt.setVisible(!showBB);
			c2_2__bbp.setVisible(showBB);
			c2_2__ctp.setVisible(false);

		}
		else if (App.deal.isPlaying() || App.isMode(Aaa.REVIEW_PLAY)) {

			c1_1__empt.setVisible(false);
			c1_1__bfdp.setVisible(false);
			c1_1__tfdp.setVisible(true);
			c1_1__mdp.setVisible(false);

			c2_0__btpBlank.setVisible(true);
			c2_0__btp.setVisible(false);

			c2_2__empt.setVisible(false);
			c2_2__bbp.setVisible(false);
			c2_2__ctp.setVisible(true);

		}
		else { // board finished

			c1_1__empt.setVisible(false);
			c1_1__bfdp.setVisible(false);
			c1_1__tfdp.setVisible(true);
			c1_1__mdp.setVisible(false);

			c2_0__btpBlank.setVisible(false);
			c2_0__btp.setVisible(true);

			c2_2__empt.setVisible(false);
			c2_2__bbp.setVisible(false);
			c2_2__ctp.setVisible(true);

		}

		c0_2__blp.matchPanelsToDealState();

		applyReviewBarVisiblity();

		validate();
	}

	/**   
	 */
	public void applyReviewBarVisiblity() {

		boolean N = App.isMode(Aaa.NORMAL);
		boolean R_Bi = App.isMode(Aaa.REVIEW_BIDDING);
		boolean R_Pl = App.isMode(Aaa.REVIEW_PLAY);
		boolean E_Ch = App.isMode(Aaa.EDIT_CHOOSE);
		boolean E_Ha = App.isMode(Aaa.EDIT_HANDS);
		boolean E_Bi = App.isMode(Aaa.EDIT_BIDDING);
		boolean E_Pl = App.isMode(Aaa.EDIT_PLAY);
		boolean o = false;

		// @formatter:off
		C0_9__empt.setVisible( N );
		C1_9__empt.setVisible( N );
		C2_9__empt.setVisible( N );

		C0_9__rbb0.setVisible( R_Bi         );
		C0_9__rbp0.setVisible(         R_Pl );
		C1_9__rbb1.setVisible( R_Bi         );
		C1_9__rbp1.setVisible(         R_Pl );
		C2_9__rbc2.setVisible( R_Bi || R_Pl );

		C0_9__ech0.setVisible( E_Ch ||  o   || E_Bi || E_Pl );
		C0_9__empp.setVisible(         E_Ha                 );
		
		C1_9__ech1.setVisible( E_Ch || E_Ha ||  o   || E_Pl );
		C1_9__xall.setVisible(                 E_Bi         );
		
		C2_9__ech2.setVisible( E_Ch || E_Ha || E_Bi ||  o   );
		C2_9__xall.setVisible(                         E_Pl );
		// @formatter:on
	}

	/**   
	 */
	public void paintComponent(Graphics g) { // GreenBaizePanel

		super.paintComponent(g);
		setBackground(Aaa.darkGrayBg);

	}

}
