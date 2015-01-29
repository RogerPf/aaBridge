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

import javax.swing.JButton;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.model.Cc;

/**
 */
public class CommonCmdBar extends ClickPanel {
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	JPanel c0 = new ClickPanel();
	JPanel c1 = new ClickPanel();
	JPanel c2 = new ClickPanel();
	JPanel c3 = new ClickPanel();
	JPanel c4 = new ClickPanel();

	//@formatter:off
	public JPanel            T0_9__empt = new ClickPanel();
	public CommonBar0        T0_9__tbp0 = new CommonBar0();

	public JPanel            T1_9__empt = new ClickPanel();
	public CommonBar1        T1_9__tbp1 = new CommonBar1();

	public JPanel            T2_9__empt = new ClickPanel();
	public CommonBar2        T2_9__tbp2 = new CommonBar2();

	public JPanel            T3_9__empt = new ClickPanel();
	public ReviewBar3        T3_9__rvb3 = new ReviewBar3();
	public CommonBar3        T3_9__tbp3 = new CommonBar3();

	public JPanel            T4_9__empt = new ClickPanel();
	public MovieModePart4    T4_9__movm = new MovieModePart4();
	public CommonBar4        T4_9__tbp4 = new CommonBar4();

	//@formatter:on

	/**   
	 */
	public CommonCmdBar() { /* Constructor */
		// =============================================================
		setOpaque(true);
		colorIntensityChange();
		setPreferredSize(new Dimension(5000, 500)); // We just try to fill the available space

		// Set up the content pane.

		c0.setLayout(new MigLayout(App.simple + ", flowy", "", "[100%]"));
		c0.add(T0_9__empt, App.hm3oneHun);
		c0.add(T0_9__tbp0, App.hm3oneHun);

		c1.setLayout(new MigLayout(App.simple + ", flowy", "", "[100%]"));
		c1.add(T1_9__empt, App.hm3oneHun);
		c1.add(T1_9__tbp1, App.hm3oneHun);

		c2.setLayout(new MigLayout(App.simple + ", flowy", "", "[100%]"));
		c2.add(T2_9__empt, App.hm3oneHun);
		c2.add(T2_9__tbp2, App.hm3oneHun);

		c3.setLayout(new MigLayout(App.simple + ", flowy", "", "[100%]"));
		c3.add(T3_9__empt, App.hm3oneHun);
		c3.add(T3_9__rvb3, App.hm3oneHun);
		c3.add(T3_9__tbp3, App.hm3oneHun);

		c4.setLayout(new MigLayout(App.simple + ", flowy", "", "[100%]"));
		c4.add(T4_9__empt, App.hm3oneHun);
		c4.add(T4_9__movm, App.hm3oneHun);
		c4.add(T4_9__tbp4, App.hm3oneHun);

		setLayout(new MigLayout(App.simple, "[18%][22%][20%][22%][18%]", "[100%]"));
		add(c0, App.hm3oneHun);
		add(c1, App.hm3oneHun);
		add(c2, App.hm3oneHun);
		add(c3, App.hm3oneHun);
		add(c4, App.hm3oneHun);

		calcApplyBarVisiblity();
	}

	/**   
	 */
	public void colorIntensityChange() {
		setBackground(Cc.g(Cc.baizeGreen));
	}

	/**   
	 */
	public void matchPanelsToDealState() {
		// =============================================================
		calcApplyBarVisiblity();

		validate();
	}

	/**   
	 */
	public void calcApplyBarVisiblity() {
		// =============================================================

//		boolean o = false;
//		boolean y = true;

		// @formatter:off
		boolean lin_virgin_or_single = App.isLin__Simple() || App.hideCommandBar || App.flowOnlyCommandBar;

		boolean S_vm = (App.visualMode == App.Vm_InsideADeal) || App.hideCommandBar;
		boolean T_vm = !S_vm;
		boolean T_vmComp = T_vm && !lin_virgin_or_single;
		boolean deal_review = S_vm && App.isModeAnyReview();
		
		boolean deal_enterable = App.deal.isSaveable() && (App.visualMode == App.Vm_DealAndTutorial);

		T0_9__empt.setVisible( S_vm );
		T0_9__tbp0.setVisible( T_vm );
		
		T1_9__empt.setVisible( !(T_vm || deal_review) && !App.flowOnlyCommandBar);
		T1_9__tbp1.setVisible(  (T_vm || deal_review) && !App.flowOnlyCommandBar);

		T2_9__empt.setVisible( !(T_vm || deal_review));
		T2_9__tbp2.setVisible(  (T_vm || deal_review));

		T3_9__empt.setVisible( !deal_review && !(App.showEdPyCmdBarBtns && T_vmComp && deal_enterable));
		T3_9__tbp3.setVisible( !deal_review &&  (App.showEdPyCmdBarBtns && T_vmComp && deal_enterable));
		T3_9__rvb3.setVisible(  deal_review );

		T4_9__empt.setVisible(  lin_virgin_or_single         );
		T4_9__movm.setVisible( !lin_virgin_or_single &&  S_vm);
		T4_9__tbp4.setVisible( !lin_virgin_or_single && !S_vm && deal_enterable);
		// @formatter:on
	}
}

/**   
 */
class CommonBar0 extends ClickPanel {
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	CommonBar0() { /* Constructor */
		// =============================================================
		setOpaque(false);
//		setLayout(new MigLayout(App.simple, "push[]5%[]10%", "push[]"));
		// setLayout(new FlowLayout(FlowLayout.LEADING, 1, 3));

//		JButton b;

		setVisible(false);
	}
}

/**   
 */
class CommonBar1 extends ClickPanel {
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	// =============================================================
	CommonBar1() { /* Constructor */
		setOpaque(false);
		setLayout(new MigLayout(App.simple, "5%[]5%[]", "push[]push"));
		// setLayout(new FlowLayout(FlowLayout.LEADING, 1, 3));

		@SuppressWarnings("unused")
		JButton b;

		add(b = new RpfResizeButton(Aaa.s_Std, "tutorialBackOne", 15, 65));

		add(b = new RpfResizeButton(Aaa.s_Std, "tutorialStepFwd", 50, 70, 0.9f));

		setVisible(false);
	}
}

/**   
 */
class CommonBar2 extends ClickPanel {
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	CommonBar2() { /* Constructor */
		// =============================================================
		setOpaque(false);
		setLayout(new MigLayout(App.simple, "6%[]5%[]", "push[]push"));

		@SuppressWarnings("unused")
		JButton b;

		add(b = new RpfResizeButton(Aaa.s_Std, "tutorialBackOne", 15, 65));

		add(b = new RpfResizeButton(Aaa.s_Std, "tutorialFlowFwd", 65, 75, 0.93f));

		setVisible(false);
	}
}

/**   
 */
class ReviewBar3 extends ClickPanel {
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	ReviewBar3() { /* Constructor */
		// ============================================================================
		setOpaque(false);
		// setBackground(Aaa.baizeGreen);

		setLayout(new MigLayout(App.simple, "22%[]3%[]", "push[]push"));

		@SuppressWarnings("unused")
		JButton b;

		add(b = new RpfResizeButton(Aaa.s_Std, "reviewBackOneCard", 10, 55));
		add(b = new RpfResizeButton(Aaa.s_Std, "reviewFwdOneCard", 10, 55));

		setVisible(false);
	}

}

/**   
 */
class CommonBar3 extends ClickPanel {
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	CommonBar3() { /* Constructor */
		// =============================================================
		setOpaque(false);
		setLayout(new MigLayout(App.simple, "push[]3%[]3%", "push[]push"));

		add(new RpfResizeButton(Aaa.s_Std, "tutorialIntoDealEdit", 16, 48, 0.77f));
		add(new RpfResizeButton(Aaa.s_Std, "tutorialIntoDealPlay", 16, 48, 0.77f));

		setVisible(false);
	}
}

/**   
 */
class MovieModePart4 extends ClickPanel {
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	MovieModePart4() { /* Constructor */
		// =============================================================
		setOpaque(false);
		setLayout(new MigLayout(App.simple, "[]", "push[]push"));

		JButton b;

		b = new RpfResizeButton(Aaa.s_Std, "tutorialBackToMovie", 95, 70, 0.75f);
		add(b);

		setVisible(false);
	}
}

/**   
 */
class CommonBar4 extends ClickPanel {
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	CommonBar4() { /* Constructor */
		// =============================================================
		setOpaque(false);
		setLayout(new MigLayout(App.simple, "[]", "push[]push"));

		JButton b;

		b = new RpfResizeButton(Aaa.s_Std, "tutorialIntoDealClever", 95, 70, 0.80f);
		add(b);

		setVisible(false);
	}
}
