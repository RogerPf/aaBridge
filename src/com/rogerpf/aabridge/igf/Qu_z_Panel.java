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

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.Aaf;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.controller.CmdHandler;
import com.rogerpf.aabridge.model.Cc;
import com.rogerpf.aabridge.model.Dir;
import com.rogerpf.aabridge.model.Frag;
import com.rogerpf.aabridge.model.Hand;
import com.rogerpf.aabridge.model.Suit;
import com.rogerpf.aabridge.view.RpfResizeButton;

/**
 */
public class Qu_z_Panel extends JPanel {
	// ---------------------------------- CLASS -------------------------------------

	private static final long serialVersionUID = 1L;

	QuAskButtsPanel qbutsp;
	QuAnswerPanel qansp;
	Qu_z_AnsBoxPanel qaboxp;
	QuTellMePanel tmep;
	Qu_z_CFlowPanel cFlowp;
	Qu_z_CTmePanel cTmep;
	Qu_z_NewPanelT newpT;
	Qu_z_NewPanelE newpE;
	Qu_z_NewAndTellPanel newAndTellp;
	Qu_z_VideoPanel qvidp;
	Qu_z_OptionsPanel optionsp;
	Qu_z_TrainExamPanel trainExamp;

	/**
	 */
	Qu_z_Panel() { /* Constructor */
		// ============================================================================
		setLayout(new MigLayout(App.simple, "[34%][8%]3%[10%]3%[12%]push[20%]", "push[30%][30%]5%[22%]5%"));

		setOpaque(false);

		qansp = new QuAnswerPanel();
		qaboxp = new Qu_z_AnsBoxPanel();
		qbutsp = new QuAskButtsPanel();
		tmep = new QuTellMePanel();
		cFlowp = new Qu_z_CFlowPanel();
		cTmep = new Qu_z_CTmePanel();
		newpT = new Qu_z_NewPanelT();
		newpE = new Qu_z_NewPanelE();
		newAndTellp = new Qu_z_NewAndTellPanel();
		qvidp = new Qu_z_VideoPanel();
		optionsp = new Qu_z_OptionsPanel();
		trainExamp = new Qu_z_TrainExamPanel();

	}

	public void matchToQuestion(char t) { // Qu_z_Panel
		// ============================================================================
		removeAll();

		qbutsp.setVisible(true);
		qbutsp.fillButtonsWithAnswers();

		add(cFlowp, App.hm3oneHun + ", cell 0 2");
		add(cTmep, App.hm3oneHun + ", cell 0 2");
		add(qaboxp, App.hm0oneHun + ", cell 0 0, spanx, center");
		qaboxp.matchToQuestion(t);
		add(qvidp, App.hm0oneHun + ", cell 0 1, spanx, left, split3, gap 2%, width 15%, flowx");
		add(qbutsp, App.hm0oneHun + ", left, width 70%, flowx");
		add(optionsp, App.hm0oneHun + ", width 10%, flowy, gap 5%");
		add(trainExamp, App.hm0oneHun + ", cell 4 2");

		App.ddsDealHandDispExamNumbWordsSuppress = false;

		if (t == '0') { // DFC Training
			add(newpT, App.hm0oneHun + ", cell 1 2");
			add(tmep, App.hm0oneHun + ", cell 2 2");
			add(newAndTellp, App.hm0oneHun + ", cell 3 2");
			cFlowp.setVisible(false);
			cTmep.setVisible(App.dfcAutoNext == 3 ? false : true);
			newpT.setVisible(App.dfcAutoNext == 3);
			newAndTellp.setVisible(App.dfcAutoNext == 3);
		}
		else if (t == '1') { // DFC Exam
			cTmep.setVisible(false);
			cFlowp.setVisible(true);
			add(newpE, App.hm0oneHun + ", cell 3 2");
			newpE.setVisible(true);
			qbutsp.setVisible(false);
		}
		else if (t == 'Q') {
			newpE.setVisible(true);
			add(newpE, App.hm0oneHun + ", cell 3 2");
			add(qaboxp, App.hm0oneHun + ", cell 0 0, spanx, center");
		}
		else if (t == 'T') {
			newpE.setVisible(true);
			add(newpE, App.hm0oneHun + ", cell 3 2");
			add(qaboxp, App.hm0oneHun + ", cell 0 0, spanx, center");
			qbutsp.setVisible(false);
			App.ddsDealHandDispExamNumbWordsSuppress = true;
		}
		else if (t == 'A' || t == 'R') {
			newpE.setVisible(true);
			add(newpE, App.hm0oneHun + ", cell 3 2");
			add(qaboxp, App.hm0oneHun + ", cell 0 0, spanx, center");
			qbutsp.setVisible(false);
		}
		else if (t == 'X') {
			newpE.setVisible(true);
			add(newpE, App.hm0oneHun + ", cell 3 2");
			add(qaboxp, App.hm0oneHun + ", cell 0 0, spanx, center");
			qbutsp.setVisible(false);
		}
		else {
			newpE.setVisible(true);
			add(newpE, App.hm0oneHun + ", cell 3 2");
			qbutsp.setVisible(false);
		}
	}

}

/**   
 */
class Qu_z_AnsBoxPanel extends JPanel {
	// ---------------------------------- CLASS -------------------------------------

	private static final long serialVersionUID = 1L;

	QuAnswerPanel qansp;
	RpfResizeButton timeTaken;
	String timeTakenStr = "";
	Calendar startTime = Calendar.getInstance();
	int col = 0;

	/**
	 */
	Qu_z_AnsBoxPanel() { /* Constructor */
		// ============================================================================
		qansp = new QuAnswerPanel();
		timeTaken = new RpfResizeButton(Aaa.s_SelfCmd, "0:00", 0, 5);
		timeTaken.setBackground(Aaa.tutorialBackground);
		timeTaken.setHoverColor(Aaa.tutorialBackground);

		timeTakenStr = "";
		startTime = Calendar.getInstance();

		setLayout(new MigLayout(App.simple, "[11%]2%[24%][24%]2%[24%]2%[11%]", "[100%]"));
//		setLayout(new MigLayout(App.simple, "[11%][26%][24%]2%[24%]2%[11%]", "[100%]"));

		setOpaque(false);
	}

	private Dir extractAnsDirection() {
		// ============================================================================
		for (Hand hand : App.deal.hands) {
			for (Suit suit : Suit.cdhs) {
				Frag frag = hand.frags[suit.v];
				if ((frag.suitVisControl & Suit.SVC_ansHere) == Suit.SVC_ansHere) {
					return hand.compass;
				}
			}
		}
		return Dir.Invalid;
	}

	public void matchToQuestion(char t) { // Qu_z_AnsBoxPanel
		// ============================================================================
		removeAll();

//		setVisible(true);

		if (t == '0') {
			add(qansp, App.hm0oneHun + ", cell 2 0, center");
		}
		else if (t == '1') {
			col = 0;
			timeTaken.setText("");
			timeTakenStr = "";
			startTime = Calendar.getInstance();
		}
		else if (t == 'Q' || t == 'T' || t == 'A') {

			if (col == 0) {
				Dir ansDir = extractAnsDirection();
				if (ansDir == Dir.Invalid)
					return;
				col = 2;
				if (App.compassPhyOffset == 1) {
					col = 3;
				}
				else if (App.compassPhyOffset == 3) {
					col = 1;
				}
				else {
					if (ansDir == Dir.West) {
						col = 1;
					}
					if (ansDir == Dir.East) {
						col = 3;
					}
				}
			}

			add(qansp, App.hm0oneHun + ", cell " + col + " 0, center");
			if (t != 'Q') {
				if (timeTakenStr.isEmpty()) {
					int secondsRun = (int) ((Calendar.getInstance().getTimeInMillis() - startTime.getTimeInMillis()) / 1000);
					if (secondsRun > 599)
						secondsRun = 599;
					int mins = secondsRun / 60;
					int secs = secondsRun % 60;
					timeTakenStr = "     " + mins + ":" + ((secs > 9) ? secs + "" : ("0" + secs));
					timeTaken.setText(timeTakenStr);
				}
				add(timeTaken, App.hm0oneHun + ", cell 4 0, height 50%");
			}
		}
		else if (t == 'R') { // || t == 'X') {
			add(qansp, App.hm0oneHun + ", cell 2 0, center");
		}

	}

}

/**   
 */
class Qu_z_CFlowPanel extends JPanel {
	// ---------------------------------- CLASS -------------------------------------

	private static final long serialVersionUID = 1L;

	/**
	 */
	Qu_z_CFlowPanel() { /* Constructor */
		// ============================================================================
		setLayout(new MigLayout(App.simple, "[]", "2%[center]5%"));

		setOpaque(false);

		RpfResizeButton b = new RpfResizeButton(Aaa.s_SelfLabel, "               " + Aaf.gT("quest.kcFlow"), 15, 15, 0.7f);
		b.addActionListener(App.con);
		add(b, App.hm1oneHun);
	}

}

/**   
 */
class Qu_z_CTmePanel extends JPanel {
	// ---------------------------------- CLASS -------------------------------------

	private static final long serialVersionUID = 1L;

	/**
	 */
	Qu_z_CTmePanel() { /* Constructor */
		// ============================================================================
		setLayout(new MigLayout(App.simple, "[]", "2%[center]5%"));

		setOpaque(false);

		RpfResizeButton b = new RpfResizeButton(Aaa.s_SelfLabel, "             " + Aaf.gT("quest.kcTellMe"), 15, 15, 0.7f);
		b.addActionListener(App.con);
		add(b, App.hm1oneHun);
	}

}

/**   
 */
class Qu_z_NewPanelT extends JPanel {
	// ---------------------------------- CLASS -------------------------------------

	private static final long serialVersionUID = 1L;

	/**
	 */
	Qu_z_NewPanelT() { /* Constructor */
		// ============================================================================
		setLayout(new MigLayout(App.simple, "[center]", "5%[center]5%"));

		setOpaque(false);

		RpfResizeButton b = new RpfResizeButton(Aaa.s_Std, "question_z_Next", 15, 15, 0.7f);
		b.addActionListener(App.con);
		add(b, App.hm1oneHun);
	}

}

/**   
 */
class Qu_z_NewPanelE extends JPanel {
	// ---------------------------------- CLASS -------------------------------------

	private static final long serialVersionUID = 1L;

	/**
	 */
	Qu_z_NewPanelE() { /* Constructor */
		// ============================================================================
		setLayout(new MigLayout(App.simple, "20%[center]20%", "5%[center]5%"));

		setOpaque(false);

		RpfResizeButton b = new RpfResizeButton(Aaa.s_Std, "question_z_Next", 15, 15, 0.7f);
		b.addActionListener(App.con);
		add(b, App.hm1oneHun);
	}

}

/**   
*/
class Qu_z_NewAndTellPanel extends JPanel {
	// ---------------------------------- CLASS -------------------------------------

	private static final long serialVersionUID = 1L;

	/**
	 */
	Qu_z_NewAndTellPanel() { /* Constructor */
		// ============================================================================
		setLayout(new MigLayout(App.simple, "[center]", "5%[center]5%"));

		setOpaque(false);

		RpfResizeButton b = new RpfResizeButton(Aaa.s_Std, "question_z_NextAndTell", 15, 15, 0.65f);
		b.addActionListener(App.con);
		add(b, App.hm1oneHun);
	}
}

/**   
 */
class Qu_z_VideoPanel extends JPanel {
	// ---------------------------------- CLASS -------------------------------------

	private static final long serialVersionUID = 1L;

	/**
	 */
	Qu_z_VideoPanel() { /* Constructor */
		// ============================================================================
		setLayout(new MigLayout(App.simple, "[]10%[]", "20%[center]20%"));

		setOpaque(false);

		RpfResizeButton b;

		b = new RpfResizeButton(Aaa.s_Std, "question_z_Learn", 20, 15, 0.65f);
		b.setForeground(Cc.BlueStrong);
		add(b, App.hm1oneHun);

		b = new RpfResizeButton(Aaa.s_Std, "question_z_Video", 20, 15, 0.70f);
		b.setForeground(Cc.RedStrong);
		add(b, App.hm1oneHun);

	}
}

/**   
*/
class Qu_z_OptionsPanel extends JPanel {
	// ---------------------------------- CLASS -------------------------------------

	private static final long serialVersionUID = 1L;

	/**
	 */
	Qu_z_OptionsPanel() { /* Constructor */
		// ============================================================================
		setLayout(new MigLayout(App.simple, "[center]15%", "20%[center]20%"));

		setOpaque(false);

		RpfResizeButton b = new RpfResizeButton(Aaa.s_Std, "question_z_Options", 20, 15, 0.60f);
		b.addActionListener(App.con);
		add(b, App.hm1oneHun);
	}

}

/**   
*/
class Qu_z_TrainExamPanel extends JPanel implements ActionListener {
	// ---------------------------------- CLASS -------------------------------------

	private static final long serialVersionUID = 1L;

	RpfResizeButton train_b;
	RpfResizeButton exam_b;

	/**
	 */
	Qu_z_TrainExamPanel() { /* Constructor */
		// ============================================================================
		setLayout(new MigLayout(App.simple, "21%[]4%[]10%", "15%[center]15%"));

		setOpaque(false);

		train_b = new RpfResizeButton(Aaa.m_Std, "question_z_Train", 3, 2, 0.7f);
		train_b.addActionListener(this);
		add(train_b, App.hm1oneHun);

		exam_b = new RpfResizeButton(Aaa.m_Std, "question_z_Exam", 3, 2, 0.7f);
		exam_b.addActionListener(this);
		add(exam_b, App.hm1oneHun);

		train_b.setSelectedBackground(Aaa.tutorialBackground);
		exam_b.setSelectedBackground(Aaa.tutorialBackground);

		train_b.setBackground(Aaa.trainExamUnSelectedBackground);
		exam_b.setBackground(Aaa.trainExamUnSelectedBackground);

		applyButtonVisiblity();
	}

	/**   
	 */
	public void applyButtonVisiblity() {
		// =============================================================

		// @formatter:off

		train_b.changeType(!App.lbx_modeExam ? Aaa.m_Label : Aaa.m_Std );
		exam_b .changeType( App.lbx_modeExam ? Aaa.m_Label : Aaa.m_Std );

		// @formatter:on
	}

	/**   
	 */
	public void actionPerformed(ActionEvent e) {
		// =============================================================

		if (e.getSource() == train_b) {
			App.lbx_modeExam = false;
			App.flowOnlyCommandBar = false;
			App.hideCommandBar = true;
			App.hideTutNavigationBar = true;
		}

		if (e.getSource() == exam_b) {
			App.lbx_modeExam = true;
			App.flowOnlyCommandBar = true;
			App.hideCommandBar = false;
			App.hideTutNavigationBar = false;
		}

		applyButtonVisiblity();

		CmdHandler.actionPerfString("question_z_Next");
	}

	boolean highlightExamButton = true;
	static Calendar firstUse = null;

	/**   
	 */
	public void paintComponent(Graphics g) { // Qu_z_TrainExamPanel
		// =============================================================

		if (App.lbx_modeExam == false && App.showDfcExamHlt && highlightExamButton) {

			if (firstUse == null) {
				firstUse = Calendar.getInstance();
			}
			if ((Calendar.getInstance().getTimeInMillis() - firstUse.getTimeInMillis()) > 10000) {
				highlightExamButton = false;
			}
			// this is just an attempt to highlight the exam button
			g.setColor(Cc.RedStrong);

			float w = getWidth();
			int fromx = (int) (w * 0.55);
			int farx = (int) (w * 0.39);

			g.fillRect(fromx, 0, farx, getHeight());
		}

		super.paintComponent(g);
	}

}
