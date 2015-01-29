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
package com.rogerpf.aabridge.igf;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.TextAttribute;
import java.util.Map;

import net.miginfocom.swing.MigLayout;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.controller.q_;
import com.rogerpf.aabridge.igf.MassGi.FontBlock;
import com.rogerpf.aabridge.igf.MassGi.GraInfo;
import com.rogerpf.aabridge.model.Deal;
import com.rogerpf.aabridge.model.Dir;
import com.rogerpf.aabridge.view.BidButtsPanel;
import com.rogerpf.aabridge.view.HandDisplayPanel;

public class QuestionPanel extends ConsumePanel {
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	Graphics2D g2;

	Dimension wh = new Dimension();

	public QuAskAndTellMePanel askAndTellMep;
	public QuAskAnswerAndTellMePanel askAnswerAndTellMep;
	public QuAskButtonsAndTellMePanel askButtsAndTellMep;
	public BidButtsPanel bbp;
	public HandDisplayPanel hdp1;
	public HandDisplayPanel hdp2;
	public QuAnswerPanel qansp;
	public Qu_m_or_y_Panel quest_m_panel;
	public Qu_z_Panel quest_z_panel;
	boolean backgroundDarker = false;

	Deal deal1 = new Deal(0);
	Deal deal2 = new Deal(0);

	public QuestionPanel() { // constructor
		// ==============================================================================================
		setOpaque(true); // questions are not transarent (but all the parts are)
		// setBackground(Aaa.tutorialBackground); see paintComponent

		bbp = new BidButtsPanel(false /* not greenBackground */);
		hdp1 = new HandDisplayPanel(Dir.South, deal1, true /* a question hand */);
		hdp2 = new HandDisplayPanel(Dir.South, deal2, true /* a question hand */);
		qansp = new QuAnswerPanel();

		askAndTellMep = new QuAskAndTellMePanel();
		askAnswerAndTellMep = new QuAskAnswerAndTellMePanel();
		askButtsAndTellMep = new QuAskButtonsAndTellMePanel();
		quest_m_panel = new Qu_m_or_y_Panel();
		quest_z_panel = new Qu_z_Panel();
	}

	public void setPositionYonlySetSize(int lb_position, float rowSpacing) {
		// ==============================================================================================

		float qpHeightAsFractionOfTupWidth = 0.242f;

		int y = 0;
		int width = App.tup.getWidth();
		int heightTup = App.tup.getHeight();
		int height = heightTup;
		backgroundDarker = false;

		if (App.visualMode == App.Vm_TutorialOnly) {
			height = (int) (heightTup * qpHeightAsFractionOfTupWidth + 0.5);
			if (0 <= lb_position && lb_position < 20) {
				y = (int) (lb_position * rowSpacing);
				backgroundDarker = true;
			}
			else
				y = heightTup - height;
		}

		setBounds(0, y, width, height);
	}

	public void matchToQuestion(char c, char t) {
		// ==============================================================================================

		removeAll();

		if (c == 'b') {
			setLayout(new MigLayout(App.simple, "[37%][26%][37%]", "6%[90%]4%"));
			add(askAndTellMep, App.hm3oneHun);
			add(bbp, App.hm3oneHun);
			add(qansp, App.hm3oneHun);
		}
		else if (c == 'c') {
			setLayout(new MigLayout(App.simple, "[37%][26%][37%]", "6%[90%]4%"));
			add(askAndTellMep, App.hm3oneHun);
			add(hdp1, App.hm3oneHun);
			add(qansp, App.hm3oneHun);
		}
		else if (c == 'h') {
			setLayout(new MigLayout(App.simple, "6%[24%][40%]2%[24%]4%", "6%[90%]4%"));
			add(hdp1, App.hm3oneHun);
			add(askAnswerAndTellMep, App.hm3oneHun);
			add(hdp2, App.hm3oneHun);
		}
		else if (c == 'm' || c == 'y') {
			setLayout(new MigLayout(App.simple, "[100%]", "[100%]"));
			add(quest_m_panel, App.hm3oneHun + ", spany 2");
			quest_m_panel.fillButtonsWithAnswers();
		}
		else if (c == 'p' || c == 't' || c == 'l' || c == 's' || c == 'd') {
			setLayout(new MigLayout(App.simple, "[37%][26%][37%]", "6%[90%]4%"));
			add(askButtsAndTellMep, App.hm3oneHun);
			add(hdp1, App.hm3oneHun);
			add(qansp, App.hm3oneHun);
			askButtsAndTellMep.fillButtonsWithAnswers();
		}
		else if (c == 'z') {
			setLayout(new MigLayout(App.simple, "[100%]", "[100%]"));
			add(quest_z_panel, App.hm3oneHun + ", spany 2");
			quest_z_panel.matchToQuestion(t); // <============== different ==========
		}
		else {
			setLayout(new MigLayout(App.simple, "[37%][26%][37%]", "6%[90%]4%"));
			add(askAnswerAndTellMep, App.hm3oneHun);
			askAndTellMep.setVisible(true);
		}

	}

	public void paintComponent(Graphics g) {
		// ==============================================================================================
		setBackground(backgroundDarker ? Aaa.questionPanelBkColor : Aaa.tutorialBackground);
		super.paintComponent(g);

		display_page_number(g);
	}

	public void display_page_number(Graphics g) {
		// ==============================================================================================

		GraInfo gi_o = App.mg.giAy.get(App.mg.end_pg);

		int page_numb_display = gi_o.capEnv.page_numb_display;

		if (page_numb_display == 0 || (gi_o.qt == q_.lb && (page_numb_display < 11 || page_numb_display >= 99999)))
			return; // we do not display on the zero'th page (opening screen)

		g2 = (Graphics2D) g;

		Aaa.commonGraphicsSettings(g2);

		FontBlock fb = App.mg.fbAy[0];

		@SuppressWarnings("unchecked")
		Map<TextAttribute, Object> attributes = (Map<TextAttribute, Object>) fb.font.getAttributes();

		scaleFrac = getWidth() / LIN_STANDARD_WIDTH;
		fontScaleFrac = FONT_SCALE_FRAC * scaleFrac;

		attributes.put(TextAttribute.SIZE, ((float) fb.linFontSize) * fontScaleFrac);
		attributes.put(TextAttribute.FOREGROUND, Color.BLACK);

		Font font = fb.font.deriveFont(attributes);

		g2.setFont(font);

		int x = (int) (getWidth() * DIS_NUMB_X);
		int y = (int) (getHeight() * DIS_NUMB_SML_Y);

		page_numb_display++; // as we are one behind the (next) number in the pg/

		g2.drawString("" + page_numb_display, x, y);
	}

}
