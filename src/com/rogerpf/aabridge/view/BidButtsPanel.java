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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JButton;
import javax.swing.Timer;

import net.miginfocom.swing.MigLayout;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.model.Bid;
import com.rogerpf.aabridge.model.Hand;
import com.rogerpf.aabridge.model.Zzz;
import com.rpsd.bridgefonts.BridgeFonts;

/**   
 */
public class BidButtsPanel extends ClickPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	int halfBidLevel = -1;
	int halfBidSuit = -1;

	JButton doubleBtn;
	JButton redoubleBtn;
	JButton levelBtn[] = new JButton[8];

	/**
	*/
	Timer autoBidDelayTimer = new Timer(App.bidPluseTimerMs, new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			if (App.frame.isSplashTimerRunning())
				return; // wait until the splash has cleared
			autoBidDelayTimer.stop();
			autoBidDelayTimer.setInitialDelay(App.bidPluseTimerMs);
			if (App.deal.isBidding()) {
				Hand hand = App.deal.getNextHandToBid();
				if (App.isAutoBid(hand.compass)) {
					clearHalfBids();
					Bid bid;
					if (hand.compass == Zzz.South /* && App.isMode(Aaa.NORMAL) */) {
						bid = App.deal.generateSouthBid(App.dealCriteria);
					}
					else if (hand.compass == Zzz.North) {
						bid = App.deal.PASS;
					}
					else {
						bid = App.deal.generateEastWestBid(hand);
					}
					App.con.voiceTheBid(bid);
				}
			}
		}
	});

	/**
	 */
	BidButtsPanel() { /* Constructor */

		setLayout(new MigLayout("insets 0 0 0 0, gap 0! 0! nocache", "7%[10%]1.0%[10%]1.0%[10%]1.0%[10%]1.0%[10%]1.0%[10%]1.0%[10%]5%", "15%[]5%[]3.0%[]"));

		Font cardFaceFont = BridgeFonts.faceAndSymbFont.deriveFont(24f);
		Font bridgeBoldFont = BridgeFonts.bridgeBoldFont.deriveFont(20f);

		RpfResizeButton b;

		b = new RpfResizeButton(0, Zzz.call_to_string[Zzz.PASS], -2, 20, 0.75f);
		b.addActionListener(this);
		b.setFont(bridgeBoldFont);
		b.setHoverColor(Aaa.clubsColor);
		b.setBackground(Aaa.passButtonColor);
		b.setForeground(Color.WHITE);
		add(b, "span 2");

		b = new RpfResizeButton(0, Zzz.call_to_string[Zzz.DOUBLE], -2, 20, 0.75f);
		b.addActionListener(this);
		b.setFont(bridgeBoldFont);
		b.setHoverColor(Aaa.heartsColor);
		b.setBackground(Aaa.dblButtonColor);
		b.setForeground(Color.WHITE);
		add(b, "hidemode 2, span 2");
		doubleBtn = b;
		doubleBtn.setVisible(false);

		b = new RpfResizeButton(0, Zzz.call_to_string[Zzz.REDOUBLE], -3, 20, 0.75f);
		b.addActionListener(this);
		b.setFont(bridgeBoldFont);
		b.setHoverColor(Aaa.heartsColor);
		b.setBackground(Aaa.dblButtonColor);
		b.setForeground(Color.WHITE);
		add(b, "hidemode 2, span 3, wrap");
		redoubleBtn = b;
		redoubleBtn.setVisible(false);

		for (int i = 1; i <= 7; i++) {
			b = new RpfResizeButton(0, String.valueOf(i), -1, 20);
			b.addActionListener(this);
			b.setHoverColor(Aaa.strongHoverColor);
			b.setBackground(Aaa.bidButsBkColor);
			b.setForeground(Color.BLACK);
			b.setFont(cardFaceFont);
			add(b, "hidemode 0, " + ((i == 7) ? " wrap" : ""));
			levelBtn[i] = b;
		}

		for (int i = 0; i < 5; i++) {
			b = new RpfResizeButton(0, Zzz.suit_to_cdhsntSt[i], ((i == 4) ? -2 : -1), 20);
			b.addActionListener(this);
			b.setHoverColor(Aaa.strongHoverColor);
			b.setBackground(Aaa.bidButsBkColor);
			b.setForeground(Aaa.cdhsColors[i]);
			b.setFont(cardFaceFont);
			add(b, ((i == 4) ? "span 2, wrap" : ""));
		}

		setVisible(true);
	}

	/**   
	 */
	public void dealMajorChange() {
		halfBidLevel = -1;
		halfBidSuit = -1;
		setBidButtonVisibility();
	}

	/**
	 */
	void clearHalfBids() {
		halfBidLevel = -1;
		halfBidSuit = -1;
		App.gbp.c1_1__bfdp.repaint();
	}

	/**
	 */
	public int getHalfBidLevel() {
		return halfBidLevel;
	}

	/**
	 */
	public int getHalfBidSuit() {
		return halfBidSuit;
	}

	/**
	 */
	private void setHalfBidLevel(int level) {
		halfBidLevel = (App.deal.isLevelAllowed(level) == false) ? -1 : level;
		App.gbp.c1_1__bfdp.repaint();
	}

	/**
	 */
	private void setHalfBidSuit(int suit) {
		halfBidSuit = suit;
		App.gbp.c1_1__bfdp.repaint();
	}

	/**
	 */
	private void setBidButtonVisibility() {
		doubleBtn.setVisible(App.deal.isCallAllowed(App.deal.DOUBLE));
		redoubleBtn.setVisible(App.deal.isCallAllowed(App.deal.REDOUBLE));

		int levelAllowed = App.deal.getHighestLevelAllowed();

		for (int i = 1; i <= 7; i++) {
			levelBtn[i].setVisible(i >= levelAllowed);
		}
	}

	/**
	 */
	public void actionPerformed(ActionEvent e) {

		if (!App.isSeatVisible(App.deal.getNextHandToBid().compass))
			return;

		String a = e.getActionCommand();
		int cmd = 0;
		for (int i = Zzz.PASS; i <= Zzz.REDOUBLE; i++) {
			if (a == Zzz.call_to_string[i]) {
				cmd = Aaa.CMD_CALL | i;
				break;
			}
		}

		if (cmd == 0) {
			for (int i = 1; i < 8; i++) {
				if (a.equals(Character.toString((char) (i + '0')))) { // ugly
					setHalfBidLevel(i);
					break;
				}
			}
			for (int i = 0; i < 5; i++) {
				if (a == Zzz.suit_to_cdhsntSt[i]) {
					setHalfBidSuit(i);
					break;
				}
			}
		}
		// we can now process it as if it was a key Command
		keyCommand(cmd);
	}

	/**
	 */
	public void keyCommand(int cmd) {
		Bid b = null;

		if ((cmd & Aaa.CMD_CALL) != 0) {
			int call = (cmd & 0xff);
			if (call == Zzz.PASS) {
				b = App.deal.PASS;
			}
			else if (call == Zzz.DOUBLE) {
				b = App.deal.DOUBLE;
			}
			else if (call == Zzz.REDOUBLE) {
				b = App.deal.REDOUBLE;
			}
		}
		else if ((cmd & Zzz.CMD_SUITN) != 0) { // *** The user has pressed a
												// Suit Key (inc N)
			int suit = (cmd & 0xff);
			setHalfBidSuit(suit);
		}
		else if ((cmd & Zzz.CMD_LEVEL) != 0) { // *** The user has pressed a
												// level Key (1 - 7)
			int level = (cmd & 0xff);
			setHalfBidLevel(level);
		}

		if (halfBidLevel > -1 && halfBidSuit > -1) {
			b = new Bid(halfBidLevel, halfBidSuit);
		}

		if (b != null) {
			clearHalfBids();
			App.con.voiceTheBid(b);
			startAutoBidDelayTimerIfNeeded();
		}
	}

	/**
	 */
	public void startAutoBidDelayTimerIfNeeded() {
		if (App.deal.isBidding()) {
			Hand hand = App.deal.getNextHandToBid();
			if (App.isAutoBid(hand.compass)) {
				autoBidDelayTimer.start();
			}
			App.gbp.c2_2__empt.setVisible(App.isAutoBid(hand.compass) == true);
			App.gbp.c2_2__bbp.setVisible(App.isAutoBid(hand.compass) == false);
		}
	}

	/**
	 */
	public void paintComponent(Graphics g) { // BidButtsPanel

		super.paintComponent(g);
		setBackground(Aaa.baizeGreen);

		setBidButtonVisibility(); // ooo er - can we do this here! apparently we can :)

		Graphics2D g2 = (Graphics2D) g;
		Aaa.commonGraphicsSettings(g2);

		float width = (float) getWidth();
		float height = (float) getHeight();
		float curve = height * 0.15f;

		// fill the lozenge ----------------------------------------------

		float leftBorder = width * 0.04f;
		float rightBorder = width * 0.03f;
		width -= leftBorder + rightBorder;

		float topBorder = height * 0.07f;
		float botBorder = height * 0.07f;
		height = height - topBorder - botBorder;
		g2.setPaint(Aaa.biddingBkColor);
		g2.fill(new RoundRectangle2D.Float(leftBorder, topBorder, width, height, curve, curve));

		// draw the fine dark line around the bidding box
		{
			float pc = 0.012f;
			float lw = height * pc;
			g2.setStroke(new BasicStroke(lw));
			g2.setColor(Aaa.weedyBlack);
			g2.draw(new RoundRectangle2D.Float(leftBorder, topBorder, width, height, curve, curve));
		}

	}

}
