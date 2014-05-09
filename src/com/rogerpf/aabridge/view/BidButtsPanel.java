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

import net.miginfocom.swing.MigLayout;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.model.Bid;
import com.rogerpf.aabridge.model.Call;
import com.rogerpf.aabridge.model.Cc;
import com.rogerpf.aabridge.model.Level;
import com.rogerpf.aabridge.model.Suit;
import com.rpsd.bridgefonts.BridgeFonts;

/**   
 */
public class BidButtsPanel extends ClickPanel implements ActionListener {
	// ---------------------------------- CLASS -------------------------------------

	private static final long serialVersionUID = 1L;

	boolean greenBackground;

	Level halfBidLevel = Level.Invalid;
	Suit halfBidSuit = Suit.Invalid;
	boolean halfBidAlert = false;

	JButton doubleBtn;
	JButton redoubleBtn;
	JButton suitBtn[] = new JButton[5];
	JButton levelBtn[] = new JButton[8];
	JButton alertBtn;

	/**
	 */
	public BidButtsPanel(boolean greenBackground) { /* Constructor */
		// =============================================================================
		setOpaque(false);

		setLayout(new MigLayout(App.simple, "7%[10%]1.0%[10%]1.0%[10%]1.0%[10%]1.0%[10%]1.0%[10%]1.0%[10%]5%", "15%[]5%[]3.0%[]"));

		// this.greenBackground = greenBackground;

		Font cardFaceFont = BridgeFonts.faceAndSymbFont.deriveFont(24f);
		Font bridgeBoldFont = BridgeFonts.bridgeBoldFont.deriveFont(20f);

		RpfResizeButton b;

		b = new RpfResizeButton(Aaa.s_SelfCmd, Call.Pass.toBidPanelString(), -2, 20, 0.75f);
		b.addActionListener(this);
		b.setFont(bridgeBoldFont);
		b.setHoverColor(Cc.GreenStrong);
		b.setBackground(Aaa.passButtonColor);
		b.setForeground(Color.WHITE);
		add(b, "span 2");

		b = new RpfResizeButton(Aaa.s_SelfCmd, Call.Double.toBidPanelString(), -2, 20, 0.75f);
		b.addActionListener(this);
		b.setFont(bridgeBoldFont);
		b.setHoverColor(Cc.RedStrong);
		b.setBackground(Aaa.dblButtonColor);
		b.setForeground(Color.WHITE);
		add(b, "hidemode 2, span 2");
		doubleBtn = b;
		doubleBtn.setVisible(false);

		b = new RpfResizeButton(Aaa.s_SelfCmd, Call.ReDouble.toBidPanelString(), -3, 20, 0.75f);
		b.addActionListener(this);
		b.setFont(bridgeBoldFont);
		b.setHoverColor(Cc.BlueStrong);
		b.setBackground(Aaa.redblButtonColor);
		b.setForeground(Color.WHITE);
		add(b, "hidemode 2, span 3, wrap");
		redoubleBtn = b;
		redoubleBtn.setVisible(false);

		for (int i = 1; i <= 7; i++) {
			b = new RpfResizeButton(Aaa.s_SelfCmd, String.valueOf(i), -1, 20);
			b.addActionListener(this);
			b.setHoverColor(Aaa.strongHoverColor);
			b.setBackground(Aaa.buttonBkgColorStd);
			b.setForeground(Color.BLACK);
			b.setFont(cardFaceFont);
			add(b, "hidemode 0, " + ((i == 7) ? " wrap" : ""));
			levelBtn[i] = b;
		}

		for (Suit su : Suit.fiveDenoms) {
			int i = su.v;
			b = new RpfResizeButton(Aaa.s_SelfCmd, su.toStrNu(), ((i == 4) ? -2 : -1), 20);
			b.addActionListener(this);
			b.setHoverColor(Aaa.strongHoverColor);
			b.setBackground(Aaa.buttonBkgColorStd);
			b.setForeground(su.color(Cc.Ce.Strong));
			b.setFont(cardFaceFont);
			add(b, ((i == 4) ? "span 2" : ""));
			suitBtn[i] = b;
		}

		{
			b = new RpfResizeButton(Aaa.s_SelfCmd, "!", -1, 20);
			b.addActionListener(this);
			b.setHoverColor(Aaa.strongHoverColor);
			b.setBackground(Aaa.buttonBkgColorStd);
			b.setForeground(Cc.RedStrong);
			b.setFont(bridgeBoldFont);
			b.setToolTipText("Alert");
			add(b, "");
			alertBtn = b;
		}
		setVisible(true);
	}

	/**   
	 */
	public void suitColorsChanged() {
		// =============================================================================
		for (Suit su : Suit.fiveDenoms) {
			int i = su.v;
			suitBtn[i].setForeground(su.color(Cc.Ce.Strong));
		}
	}

	/**   
	 */
	public void dealMajorChange() {
		// =============================================================================
		halfBidLevel = Level.Invalid;
		halfBidSuit = Suit.Invalid;
		halfBidAlert = false;
		setBidButtonVisibility();
	}

	/**
	 */
	public void clearHalfBids() {
		// =============================================================================
		halfBidLevel = Level.Invalid;
		halfBidSuit = Suit.Invalid;
		halfBidAlert = false;
		App.frame.repaint();
	}

	/**
	 */
	public Level getHalfBidLevel() {
		// =============================================================================
		return halfBidLevel;
	}

	/**
	 */
	public Suit getHalfBidSuit() {
		// =============================================================================
		return halfBidSuit;
	}

	/**
	 */
	public boolean getHalfBidAlert() {
		// =============================================================================
		return halfBidAlert;
	}

	/**
	 */
	private void setHalfBidLevel(Level level) {
		// =============================================================================
		halfBidLevel = (App.deal.isLevelAllowed(level) == false) ? Level.Invalid : level;
		App.frame.repaint();
	}

	/**
	 */
	private void setHalfBidSuit(Suit suit) {
		// =============================================================================
		halfBidSuit = suit;
		App.frame.repaint();
	}

	/**
	 */
	private void setHalfBidAlert() {
		// =============================================================================
		halfBidAlert = !halfBidAlert;
		App.frame.repaint();
	}

	/**
	 */
	private void setBidButtonVisibility() {
		// =============================================================================
		doubleBtn.setVisible(App.deal.isCallAllowed(Call.Double));
		redoubleBtn.setVisible(App.deal.isCallAllowed(Call.ReDouble));

		int levelAllowed = App.deal.getHighestLevelAllowed();

		for (int i = 1; i <= 7; i++) {
			levelBtn[i].setVisible(i >= levelAllowed);
		}

		alertBtn.setVisible(App.isVmode_InsideADeal());
	}

	/**
	 */
	public void actionPerformed(ActionEvent e) {
		// =============================================================================

//		if (!App.isSeatVisible(App.deal.getNextHandToBid().compass))
//			return;

		String a = e.getActionCommand();
		int cmd = 0;
		for (Call call : Call.threeCalls) {
			if (a.contentEquals(call.toBidPanelString())) {
				cmd = Aaa.CMD_CALL | call.v;
				break;
			}
		}

		if (cmd == 0) {
			for (int i = 1; i <= 7; i++) {
				if (a.equals(Character.toString((char) (i + '0')))) { // ugly
					setHalfBidLevel(Level.levelFromInt(i));
					break;
				}
			}
			for (Suit su : Suit.fiveDenoms) {
				if (a.contentEquals(su.toStrNu())) {
					setHalfBidSuit(su);
					break;
				}
			}
			{
				if (a == "!") {
					setHalfBidAlert();
				}
			}
		}
		// we can now process it as if it was a key Command
		keyCommand(cmd);
	}

	/**
	 */
	public void keyCommand(int cmd) {
		// =============================================================================
		Bid b = null;

		if ((cmd & Aaa.CMD_CALL) != 0) {
			Call call = Call.callFromInt(cmd & 0xff);
			b = new Bid(call);
			b.alert = halfBidAlert;
		}
		else if ((cmd & Aaa.CMD_SUITN) != 0) {
			// *** The user has pressed a Suit Key (inc N)
			Suit suit = Suit.suitFromInt(cmd & 0xff);
			setHalfBidSuit(suit);
		}
		else if ((cmd & Aaa.CMD_LEVEL) != 0) {
			// *** The user has pressed a level Key (1 - 7)
			Level level = Level.levelFromInt(cmd & 0xff);
			setHalfBidLevel(level);
		}
		else if ((cmd & Aaa.CMD_ALERT) != 0) {
			// *** The user has pressed the alert "!" key
			setHalfBidAlert();
		}

		if (halfBidLevel.v >= Level.One.v && halfBidSuit != Suit.Invalid) {
			b = new Bid(halfBidLevel, halfBidSuit);
			b.alert = halfBidAlert;
		}

		if (b != null) {
			clearHalfBids();
			App.con.voiceTheBid(b);
			// startAutoBidDelayTimerIfNeeded();
		}
	}

	/**
	 */
	public void paintComponent(Graphics g) { // BidButtsPanel
		// =============================================================================
		super.paintComponent(g);

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
