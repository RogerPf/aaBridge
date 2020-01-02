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
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.AttributedString;

import javax.swing.Timer;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.controller.CmdHandler;
import com.rogerpf.aabridge.model.Cc;
import com.rogerpf.aabridge.model.Deal;
import com.rogerpf.aabridge.model.Dir;
import com.rogerpf.aabridge.model.Lin;

import net.miginfocom.swing.MigLayout;

/**   
 */
public class DualDealListButtonsPanel extends ClickPanel implements ItemListener, ActionListener {
	// ---------------------------------- CLASS -------------------------------------

	private static final long serialVersionUID = 1L;

	QLabel label;

	QCheckBox showDdAsMin;
	QCheckBox showDdWithResults;

	QCheckBox showDdResultTots;

	public DualDealListButtonsPanel() {
		// ==============================================================================================
		setOpaque(true); // remember - we derive from ClickPanel which is setOpaque(false)
		setBackground(SystemColor.control);

		setVisible(false);
	}

	public void matchToLin() {
		// ==============================================================================================
		setVisible(false);
		removeAll();

		DualDealAy ddAy = App.mg.ddAy;

		if (App.mg.lin.linType != Lin.VuGraph || ddAy == null || ddAy.isEmpty()) {
			return;
		}

		setVisible(true);

		boolean min = App.showDdAsMin;
		boolean oneExtended = (ddAy.oneColExtended && !ddAy.twoColumn);

		if (min) {
			setLayout(new MigLayout(App.simple, "", ""));
			setBackground(Cc.g(Cc.darkGrayBg));
		}
		else if (oneExtended) {
			setLayout(new MigLayout(App.simple, "3[c][][c]3", ""));
			setBackground(SystemColor.control);
		}
		else {
			setLayout(new MigLayout(App.simple, "3[c][][][c]3", ""));
			setBackground(SystemColor.control);
		}

		String mw = min ? ", wrap" : "";

		// @formatter:off
		add(showDdAsMin       = new QCheckBox(this, App.showDdAsMin, (min ? "" : "min"), "Shows the Contracts and Results  "),     "span 4, align left, split" + mw);
		
		if (min) {
			add(showDdResultTots = new QCheckBox(this, App.showDdResultTots, "", "Shows the running total Imp scores"),     "align left" + mw);
			showDdResultTots.setBackground(Cc.g(Cc.darkGrayBg));
			showDdResultTots.setVisible(ddAy.twoColumn && App.showDdWithResults);
			showDdAsMin.setBackground(Cc.g(Cc.darkGrayBg));
		} 
		else {
			showDdAsMin.setBackground(SystemColor.control);
			
			add(showDdWithResults = new QCheckBox(this, App.showDdWithResults, ddAy.twoColumn ? "show results" : "rs"), "wrap");
			add(showDdResultTots  = new QCheckBox(this, App.showDdResultTots, "", "Shows the running total Imp scores"),     "align left" + mw);
			showDdResultTots.setVisible(ddAy.twoColumn && App.showDdWithResults);

			// Title
			add(label = new QLabel(ddAy.twoColumn ? "Title - HOVER HERE" : "  HOVER", ddAy.competitionTitle), "span 3, center, wrap" + (ddAy.twoColumn == false ? ", align left" : "") );
			label.setForeground(Aaa.optionsTitleGreen);
			label.setVisible(label.getText().length() > 0);
	
			PButton pLabel;
			if (ddAy.twoColumn && App.showDdWithResults && App.showDdResultTots) {
				add(pLabel = new PButton( ddAy.startingScore[0]), "wmin 35, wmax 35, align right, hidemode 1, span 2, gapx 3");
				pLabel.setBackground(Aaa.teamDDColorAy[DualDeal.Open][0]);
				
				add(pLabel = new PButton( ddAy.startingScore[1]), "wmin 35, wmax 35, align left, hidemode 1, span 2, gapx 3, wrap");
				pLabel.setBackground(Aaa.teamDDColorAy[DualDeal.Closed][0]);
			}
			else {
				add(pLabel = new PButton(" "), "wmin 30, wmax 30, hidemode 1, span 4, wrap");
			}

			add(label = new QLabel(" Open Rm", "OPEN Room:  NS (blue) Team, " + ddAy.blueTeam + "   -   EW (purple) Team, " + ddAy.purpleTeam), "hidemode 2, span 2");
			label.setForeground(Aaa.optionsTitleGreen);
			label.setVisible(ddAy.twoColumn);
	
			add(label = new QLabel("    Closed Rm", "CLOSED Room:  NS (purple) Team, " + ddAy.purpleTeam + "   -   EW (blue) Team, " + ddAy.blueTeam), "hidemode 2, span 2, wrap");
			label.setForeground(Aaa.optionsTitleGreen);
			label.setVisible(ddAy.twoColumn);
			
		}
		// @formatter:on

		AttributedString at;

		for (DualDeal dd : App.mg.ddAy) {

			DualDeal.EachDeal openDeal = dd.eachDeal[DualDeal.Open];

			add(openDeal.movieBtn = new PButton(this, dd.qx_number + "", null), "wmin 20, wmax 20, gapy 3" + mw);
			openDeal.movieBtn.setBackground(getBackground() /* this panels background */);
			if (oneExtended && dd.highlight) {
				openDeal.movieBtn.setBackground(min ? Aaa.baizeMustard : Aaa.tooltipYellow);
			}

			if (!min) {

				at = openDeal.makeAttributedStringFromContract(App.showDdWithResults);

				add(openDeal.reviewBtn = new PButton(this, openDeal.declarerCompass(), at),
						"wmin 52, wmax 52, gap 3" + ((ddAy.twoColumn || oneExtended) ? "" : ", wrap"));
				if (openDeal.declarer != Dir.Invalid) {
					openDeal.reviewBtn.setBackground(Aaa.teamDDColorAy[DualDeal.Open][openDeal.declarer.v % 2]);
				}
				openDeal.reviewBtn.setPreferredSize(new Dimension(80, 18));

				if (oneExtended) {
					if (dd.highlight) {
						openDeal.reviewBtn.setBackground(Aaa.tooltipYellow);
					}

					add(openDeal.travBtn = new PButton(this, App.showDdWithResults ? dd.sImps : " ", null), "wmin 37, wmax 37, gap 3, wrap");
					if (App.showDdWithResults && dd.impsValid && dd.scoreDiff != 0) {
						openDeal.travBtn.setBackground(Aaa.teamDDColorAy[1][(dd.scoreDiff > 0 ? 1 : 0)]);
					}
					openDeal.travBtn.setPreferredSize(new Dimension(80, 18));
					if (dd.highlight) {
						openDeal.travBtn.setBackground(Aaa.tooltipYellow);
					}
				}

				if (ddAy.twoColumn == false)
					continue;

				DualDeal.EachDeal closedDeal = dd.eachDeal[DualDeal.Closed];

				at = closedDeal.makeAttributedStringFromContract(App.showDdWithResults);

				add(closedDeal.reviewBtn = new PButton(this, closedDeal.declarerCompass(), at), "wmin 56, wmax 56, gap 3");
				if (closedDeal.declarer != Dir.Invalid) {
					closedDeal.reviewBtn.setBackground(Aaa.teamDDColorAy[DualDeal.Closed][closedDeal.declarer.v % 2]);
				}
				closedDeal.reviewBtn.setPreferredSize(new Dimension(80, 18));

				add(closedDeal.movieBtn = new PButton(this, App.showDdWithResults ? dd.sImps : " ", null), "wmin 20, wmax 20, gap 3, wrap");
				if (App.showDdWithResults && dd.impsValid && dd.scoreDiff != 0) {
					closedDeal.movieBtn.setBackground(Aaa.teamDDColorAy[1][(dd.scoreDiff > 0 ? 1 : 0)]);
				}
			}
		}

		PButton pLabel;
		if (!min && ddAy.twoColumn && App.showDdWithResults && App.showDdResultTots) {
			add(pLabel = new PButton(ddAy.finalScore[0] + " "), "wmin 35, wmax 35, hidemode 1, align right, hidemode 1, span2, gapy 5, gapx 3");
			pLabel.setBackground(Aaa.teamDDColorAy[DualDeal.Open][0]);

			add(pLabel = new PButton(ddAy.finalScore[1] + " "), "wmin 35, wmax 35, hidemode 1, align left, hidemode 1, span 2, gapx 3");
			pLabel.setBackground(Aaa.teamDDColorAy[DualDeal.Closed][0]);
		}

		// setVisible(true);
	}

	public void componentResized(ComponentEvent e) {
	}

	public void componentMoved(ComponentEvent e) {
	}

	public void componentShown(ComponentEvent e) {
	}

	public void componentHidden(ComponentEvent e) {
	}

	/** 
	 * This listens for the check box changed event
	 */
	public void itemStateChanged(ItemEvent e) {
		// ==============================================================================================

		boolean b = (e.getStateChange() == ItemEvent.SELECTED);
		Object source = e.getItemSelectable();
		boolean change = false;
		boolean minChange = false;

		if (source == showDdAsMin) {
			App.showDdAsMin = b;
			minChange = true;
			change = true;
		}
		else if (source == showDdWithResults) {
			App.showDdWithResults = b;
			change = true;
		}
		else if (source == showDdResultTots) {
			App.showDdResultTots = b;
			change = true;
		}

		if (change && App.allConstructionComplete) {
			App.savePreferences();
			App.setVisualMode();
			App.gbp.matchPanelsToDealState();
			matchToLinTimer.start();
			if (minChange)
				App.frame.payloadPanelShaker();
		}
	}

	/**
	*/
	public Timer matchToLinTimer = new Timer(0, new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			matchToLinTimer.stop();
			matchToLin();
			repaint();
			App.frame.repaint();
		}
	});

	/**
	 *  button clicked listener 
	 */
	public void actionPerformed(ActionEvent e) {
		// ==============================================================================================

		DualDealAy ddAy = App.mg.ddAy;

		App.con.stopAllTimers();

		for (DualDeal dd : ddAy) {
			for (int k = 0; k < 2; k++) {
				DualDeal.EachDeal ed = dd.eachDeal[k];
				if (ed == null)
					continue;

				if (ed.movieBtn == e.getSource() || ed.travBtn == e.getSource()) {
					App.setMode(Aaa.NORMAL_ACTIVE);
					App.setVisualMode(App.Vm_DealAndTutorial);
					App.mg.setTheReadPoints(App.mg.findPgIdBeforeThisBarBlock(ed.qx_bb), false /* not used */);
//					App.ddsAnalyserPanelVisible = false;
					App.gbp.matchPanelsToDealState();
					return;
				}

				if (ed.reviewBtn == e.getSource()) {
					int pg_index = App.mg.findPgIdBeforeNextQx(ed.qx_bb);
					Deal deal = App.mg.giAy.get(pg_index).deal;

					if (deal != null) {
						/* set the readpoints for our return to movie - if we ever do it */
						App.mg.setTheReadPoints(pg_index, false /* not used */);
						App.deal = deal;
						App.dealMajorChange();
						CmdHandler.tutorialIntoDealStd();
						App.setVisualMode(App.Vm_InsideADeal);
//						App.ddsAnalyserPanelVisible = false;
						App.gbp.matchPanelsToDealState();
						App.frame.repaint();
					}
					return;
				}
			}
			add(label = new QLabel(""), "wrap");
		}
		@SuppressWarnings("unused")
		int z = 0;
	}

}
