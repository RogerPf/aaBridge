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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.Aaf;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.controller.Controller;
import com.rogerpf.aabridge.controller.q_;
import com.rogerpf.aabridge.igf.MassGi.GraInfo;
import com.rogerpf.aabridge.view.RpfResizeButton;

import net.miginfocom.swing.MigLayout;

/**   
 */
public class QuAskButtsPanel extends ConsumePanel implements ActionListener {
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	/**
	 */
	QuAskButtsPanel() { /* Constructor */
		// ============================================================================
		setOpaque(false);

	}

	public void fillButtonsWithAnswers() {
		// ============================================================================

		mg = App.mg; // a class member, just for simple (less to write) access;

		if (mg.end_pg == 0)
			return;

		if (mg.stop_gi != mg.end_pg)
			return;

		GraInfo gi = App.mg.giAy.get(mg.end_pg);

		if (gi.qt != q_.lb)
			return;

		removeAll();

		int points = 0;

		char c = gi.bb.get(1).charAt(0);

		if (c == 'p' || c == 't' || c == 'l' || c == 's' || c == 'd') {

			points = MassGi_utils.ans_lb_ptlsd_points(App.tup.qp.deal1, c);

			setLayout(new MigLayout("insets 0 0 0 0, gap 2%! 0!", "push[][][][][]push", "push[]push"));

			int base = points + (int) (Math.random() * 5) - 4; // -4 to 0

			if (base < 0)
				base = 0;

			RpfResizeButton b;
			for (int i = base; i < base + 5; i++) {
				b = new RpfResizeButton(Aaa.s_SelfCmd, "" + i, -2, 35);
				b.setForeground(Color.black);
				b.addActionListener(this);
				add(b);
			}
			return;
		}

		if (!(c == 'm' || c == 'y' || c == 'z'))
			return; // should never happen

//		setLayout(new MigLayout("insets 0 0 0 0, gap 1%! 0!", "", ""));

		/** 
		 * with the m case the answers need to be unpacked and the buttons are therefore variable widths
		 */

		if (c == 'y') {
			setLayout(new MigLayout("insets 0 0 0 0, gap 3%! 0!", "push[][]push", "push[]push"));
			gi.bb.set(3, Aaf.quest_yes + "~" + Aaf.quest_no);
			char a = (gi.bb.getSafe(4) + " ").toLowerCase().charAt(0);
			char yes1st = (Aaf.quest_yes + " ").toLowerCase().charAt(0);
			gi.bb.set(4, (yes1st == a ? Aaf.quest_yes : Aaf.quest_no));
		}
		else { // m and z
			setLayout(new MigLayout("insets 0 0 0 0, gap 1%! 0!", "push[][][][][][][][][]push", "push[]push"));
		}

		String a[] = gi.bb.getSafe(3).split("\\~");

		for (int i = 0; i < a.length; i++) {
			a[i] = Aaa.deAtQuestionAndBubbleText(a[i]);
		}

		if (c == 'm') {
			// we need to make some effort to shrink the answers if they are too long

			int max = 90;
			int allowed = 50;
			int shrinkBy = 2;
			int totLen = 0;

			for (int i = 0; i < a.length; i++) {
				totLen += a[i].length() + ((a[i].length() < 4) ? 4 : 2);
			}

			while (totLen > max) {
				int n = 0;
				for (int i = 0; i < a.length; i++) {
					String s = a[i];
					int len = s.length();
					if (len > allowed) {
						s = s.substring(0, allowed - 1);
						a[i] = s;
						len = s.length();
					}
					n += len + ((len < 4) ? 4 : 2);
				}
				totLen = n;
				allowed -= shrinkBy;
			}
		}

		RpfResizeButton b;
		for (int i = 0; i < a.length; i++) {
			b = new RpfResizeButton(Aaa.s_SelfCmd, a[i], 0, 52);
			b.setForeground(Color.black);
			b.addActionListener(this);
			add(b);
		}

	}

	public void actionPerformed(ActionEvent e) {
		// ============================================================================
		Controller.tutorialAnswerButtonClicked(e.getActionCommand());
	}

}
