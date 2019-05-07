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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

import javax.swing.Timer;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.dds.Z_bothResults;
import com.rogerpf.aabridge.dds.Z_ddsCalculate;
import com.rogerpf.aabridge.dds.contractType;
import com.rogerpf.aabridge.dds.parResultsMaster;
import com.rogerpf.aabridge.model.Bid;
import com.rogerpf.aabridge.model.Call;
import com.rogerpf.aabridge.model.Cc;
import com.rogerpf.aabridge.model.Cc.Ce;
import com.rogerpf.aabridge.model.Deal;
import com.rogerpf.aabridge.model.Dir;
import com.rogerpf.aabridge.model.Level;
import com.rogerpf.aabridge.model.Suit;
import com.rpsd.bridgefonts.BridgeFonts;

import net.miginfocom.swing.MigLayout;

/**   
 */
/**   
 */
public class DdsAnalyserPanel extends ClickPanel implements ActionListener {
	private static final long serialVersionUID = 1L;

	public RpfResizeButton showBiddingBtn;

	public boolean analInProgress = false;

	RpfResizeButton topLine;
	AnBtn anBtn[][] = new AnBtn[5][4];
	RpfResizeButton bottomLine;

	/**
	 */
	DdsAnalyserPanel() { /* Constructor */
//		setOpaque(true);
//		setBackground(Aaa.baizeMustard);

		setLayout(new MigLayout(App.simple + ", flowx", "7%[17%]3%[12%][12%][12%][12%]3%[16%]", "3%[10%]2%[8%]3%[14%][14%]1%[14%][14%][]"));

		for (int j = 0; j < 7; j++) {
			if (j == 0) {
				add(new RpfResizeButton(Aaa.s_SelfLabel, "Par:", -2, 12, 0.95f), "span 7, split2");
				add(topLine = new RpfResizeButton(Aaa.s_SelfLabel, "Top line goes here", -9, 13, 0.95f), ", wrap");
				continue;
			}
			else if (j == 6) {
				bottomLine = new RpfResizeButton(Aaa.s_SelfLabel, "", -12, 12, 0.75f);
				add(bottomLine, "hidemode 2, span 7");
				continue;
			}
			else if (j == 1) {
				for (int i = 0; i < 6; i++) {
					if (i == 0) {
						add(new RpfResizeButton(Aaa.s_SelfLabel, "", -1, 5, 1.0f));
					}
					else if (i < 5) {
						Suit suit = Suit.suitFromInt(i - 1);
						char sLet = suit.toCharLower();
						RpfResizeButton btn = new RpfResizeButton(Aaa.s_SelfLabel, "" + sLet, -1, 12, 1.0f);
						btn.setForeground(Cc.SuitColor(suit, Ce.Weak));
						btn.setFont(BridgeFonts.faceAndSymbolFont);
						btn.suit_symbol = true;
						add(btn);
					}
					else if (i == 5) {
						RpfResizeButton btn = new RpfResizeButton(Aaa.s_SelfLabel, "NT  ", -2, 12, 1.0f);
						btn.setForeground(Cc.BlackWeak);
						add(btn, "wrap");
					}
				}
				continue;
			}

			// j = 2 to 6 is the core table

			for (int i = 0; i < 6; i++) {
				final char[] directions = { 'N', 'S', 'E', 'W' };
				Dir dir = Dir.directionFromChar(directions[j - 2]);
				if (i == 0) {
					String d = (directions[j - 2] == 'W') ? " W" : directions[j - 2] + "";
					RpfResizeButton btn = new RpfResizeButton(Aaa.s_SelfLabel, d, -2, 13, 1.0f);
					btn.setForeground(Cc.BlackWeak);
					add(btn);
					continue;
				}
				Suit suit = Suit.suitFromInt(i - 1);
				RpfResizeButton b = new RpfResizeButton(Aaa.s_SelfCmd, j + "" + i, -1, 13, 1.0f);
				b.addActionListener(this);
				anBtn[i - 1][j - 2] = new AnBtn(b, suit, dir);
				add(b, ((i == 5) ? "wrap" : ""));
			}
		}
		setVisible(false);
	}

	/**
	 */
	public void actionPerformed(ActionEvent e) {

		AnBtn btn = null;
		for (int s = 0; s < 5; s++) {
			for (int d = 0; d < 4; d++) {
				if (anBtn[s][d].rpfBtn == e.getSource()) {
					btn = anBtn[s][d];
					break;
				}
			}
		}
		if (btn == null)
			return;

		App.ddsAnalyserPanelVisible = false;

		Deal deal = App.deal.deepClone();

		App.deal = deal;

		deal.wipeContractBiddingAndPlay();

		while (deal.getNextHandToBid().compass != btn.declarer) {
			deal.makeBid(new Bid(Call.Pass));
		}

		int level = (btn.posTricks < 7) ? 1 : btn.posTricks - 6;
		deal.makeBid(new Bid(Level.levelFromInt(level), btn.suit));

		deal.makeBid(new Bid(Call.Pass));
		deal.makeBid(new Bid(Call.Pass));
		deal.makeBid(new Bid(Call.Pass));

		App.calcCompassPhyOffset();
		App.dealMajorChange();

		App.setMode(Aaa.NORMAL_ACTIVE);

		App.con.controlerInControl();
		App.gbp.matchPanelsToDealState();
	}

	/**
	 */
	public void reset() {
		topLine.setText("...                        ");
		bottomLine.setText("Please Wait  -  Analysing");
		for (int s = 0; s < 5; s++) {
			for (int d = 0; d < 4; d++) {
				anBtn[s][d].setPosTricks(-1);
			}
		}
	}

	/**
	 */
	public void analyseButtonClicked() {
		App.ddsAnalyserPanelVisible = !App.ddsAnalyserPanelVisible;
		reset();
		App.gbp.matchPanelsToDealState();
		if (App.ddsAnalyserPanelVisible == false)
			return;
		// Delay before starting the Slow analysis (couple of seconds, or hundreds !)
		// so our analysing message will display
		ddsAnalysePart2Timer.start();
	}

	/**
	 */
	public void reinstateAnalyserButtonClicked() {
		App.reinstateAnalyser = !App.reinstateAnalyser;
		App.gbp.matchPanelsToDealState();
	}

//	/**
//	 */
//	public void analyseAgain() {
//		reset();
//		App.gbp.matchPanelsToDealState();
//		// Delay before starting the Slow analysis (couple of seconds, or hundreds !)
//		// so our analysing message will display
//		ddsAnalysePart2Timer.start();
//	}

	/**
	*/
	public Timer ddsAnalysePart2Timer = new Timer(10 /* ms */, new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			ddsAnalysePart2Timer.stop();

			if (App.deal.countOrigCards() < 52) {
				bottomLine.setText("52 cards ?   Try   Shuf Op   first");
				return;
			}

			Z_bothResults rtn = Z_ddsCalculate.analyse(App.deal);

			if (rtn.resp != 1) {
				bottomLine.setText(rtn.errStr);
				return;
			}
			for (int k = 0; k < 5; k++) {
				int s = (k <= 3) ? 3 - k : 4;
				for (int j = 0; j < 4; j++) {
					final int conv[] = { 0, 2, 1, 3 };
					int d = conv[j];
					int posTricks = rtn.ddTableRes.resTable[k * 4 + j];
					anBtn[s][d].setPosTricks(posTricks);
				}
			}
			String par = "No answer !";
			parResultsMaster r = rtn.parResMaster;
			if (r.number > 0) {
				String denom = "";
				contractType ct0 = r.contracts[0];
				for (int i = 0; i < r.number; i++) {
					contractType ct = r.contracts[i];
					final String ddsParDenom_to_str[] = { "NT", "S", "H", "D", "C" };
					String lineDenom = ct.level + ddsParDenom_to_str[ct.denom];
					if (i == 0) {
						denom = lineDenom;
						continue;
					}
					if (ct0.seats == ct.seats && ct0.overTricks == ct.overTricks && ct0.underTricks == ct.underTricks) {
						denom += "/" + lineDenom;
					}
				}

				par = denom;

				if (ct0.overTricks > 0) {
					par += " +" + ct0.overTricks;
				}
				else if (ct0.underTricks > 0) {
					par += "* -" + ct0.underTricks;
				}

				String ddsParSeat_to_str[] = { "N", "E", "S", "W", "NS", "EW" };
				int ddsParSeat_score_inv[] = { 1, -1, 1, -1, 1, -1 };
				par += "  by " + ddsParSeat_to_str[ct0.seats];
				par += "  " + r.score * ddsParSeat_score_inv[ct0.seats];

			}
			topLine.setText(par);

			bottomLine.setText("Click on any contract above");
		}
	});

	/**
	 */
	public void paintComponent(Graphics g) { // BidButtsPanel
		// =============================================================================
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;
		Aaa.commonGraphicsSettings(g2);

		float width = (float) getWidth();
		float height = (float) getHeight();
		float curve = height * 0.10f;

		// fill the lozenge ----------------------------------------------

		float leftBorder = width * 0.015f;
		float rightBorder = 0f; // width * 0.03f;
		width -= leftBorder + rightBorder;

		float topBorder = 0; // height * 0.07f;
		float botBorder = 0; // height * 0.07f;
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

	/**
	 */
	class AnBtn {
		// =============================================================================
		RpfResizeButton rpfBtn;
		public int posTricks;
		public Suit suit;
		public Dir declarer;

		AnBtn(RpfResizeButton b, Suit suit, Dir declarer) {
			this.rpfBtn = b;
			this.suit = suit;
			this.declarer = declarer;
			setPosTricks(-1);
			b.setForeground(Cc.SuitColor(suit, Ce.Strong));
			b.setBackground(Aaa.biddingBkColor);
			b.setHoverColor(Cc.BlueWeedy);
		}

		public void setPosTricks(int posTricks) {
			this.posTricks = posTricks;
			String text = ".";
			String tt = "";
			if (posTricks < 0) {
				text = "";
			}
			else if (posTricks <= 6) {
				text = "-";
				tt = "1 " + suit.toStrNt() + "  by " + declarer.toString() + " goes " + (7 - posTricks) + " down";
			}
			else if (posTricks > 6) {
				text = (posTricks - 6) + "";
				tt = (posTricks - 6) + " " + suit.toStrNt() + " by " + declarer.toString() + " can make";
			}
			rpfBtn.setText(text);
			rpfBtn.setToolTipText(tt);
		}
	}

}
