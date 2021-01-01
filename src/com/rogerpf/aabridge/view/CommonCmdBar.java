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

import javax.swing.JButton;
import javax.swing.JPanel;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.Aaf;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.igf.MassGi.GraInfo;
import com.rogerpf.aabridge.model.Cc;

import net.miginfocom.swing.MigLayout;

/**
 */
public class CommonCmdBar extends ClickPanCbar {
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	JPanel c0 = new ClickPanCbar();
	JPanel c1 = new ClickPanCbar();
	JPanel c2 = new ClickPanCbar();
	JPanel c3 = new ClickPanCbar();
	JPanel c4 = new ClickPanCbar();

	//@formatter:off
	public JPanel            T0_9__empt = new ClickPanCbar();
	public CommonBar0        T0_9__tbp0 = new CommonBar0();

	public JPanel            T1_9__empt = new ClickPanCbar();
	public CommonBar1        T1_9__tbp1 = new CommonBar1();

	public JPanel            T2_9__empt = new ClickPanCbar();
	public CommonBar2        T2_9__tbp2 = new CommonBar2();

	public JPanel            T3_9__empt = new ClickPanCbar();
	public ReviewBar3        T3_9__rvb3 = new ReviewBar3();
	public CommonBar3        T3_9__tbp3 = new CommonBar3();

	public JPanel            T4_9__empt = new ClickPanCbar();
	public MovieModePart4    T4_9__movm = new MovieModePart4();
	public CommonBar4        T4_9__tbp4 = new CommonBar4();
	
	public boolean is1stBtnVisible() {
		return T3_9__tbp3.is1stBtnVisible();
	}
	
	public boolean isBackToMovieBtnVisible() {
		return T4_9__movm.isBackToMovieBtnVisible();
	}
	
	public boolean isEnterTheDealBtnVisible() {
		return T4_9__tbp4.isEnterTheDealBtnVisible();
	}

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
		// ============================================================================
		setBackground(Cc.g(Cc.baizeGreen));
	}

	/**   
	 */
	public void paintComponent(Graphics g) {
		// ============================================================================
		setBackground(Cc.g(Cc.baizeGreen));
		super.paintComponent(g);
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
		boolean lin_virgin = App.isLin__Virgin() || App.hideCommandBar || App.flowOnlyCommandBar;

		boolean S_vm = (App.visualMode == App.Vm_InsideADeal) || App.hideCommandBar;
		boolean T_vm = !S_vm;
		boolean deal_review = S_vm && App.isModeAnyReview();
		
		boolean edt_is_V = false;
		boolean oth_is_V = false;
//
//         DO NOT reinstate the following code is it one big BUG  RPf  2019-08-04
//
//		if (App.mg != null && App.mg.giAy.size() > 0) {
//			GraInfo gi = App.mg.giAy.get(App.mg.stop_gi);
//			edt_is_V = (gi.get_CapEnv__bv_etd() != 'H');
//			oth_is_V = (gi.get_CapEnv__bv_1st() != 'H') && (gi.get_CapEnv__bv_contdds() == 'H');
//		}
//
		
		boolean deal_enterable = /* (App.isLin__Single()) && */ (App.visualMode == App.Vm_DealAndTutorial);

		T0_9__empt.setVisible( S_vm );
		T0_9__tbp0.setVisible( T_vm );
		
		T1_9__empt.setVisible( !(T_vm || deal_review) && !App.flowOnlyCommandBar);
		T1_9__tbp1.setVisible(  (T_vm || deal_review) && !App.flowOnlyCommandBar);

		T2_9__empt.setVisible( !(T_vm || deal_review));
		T2_9__tbp2.setVisible(  (T_vm || deal_review));

		boolean show_other = !lin_virgin && !S_vm && (deal_enterable || oth_is_V);

		T3_9__empt.setVisible( !show_other && !deal_review );
		T3_9__rvb3.setVisible( deal_review );
		T3_9__tbp3.setVisible( show_other && !deal_review);
		if (/*show_enter &&*/ !deal_review) {
		   T3_9__tbp3.set1stContVisibility();
		}
		
		boolean show_enter = !lin_virgin && !S_vm && (deal_enterable || edt_is_V);

		T4_9__empt.setVisible(  lin_virgin         );
		T4_9__movm.setVisible( !lin_virgin &&  S_vm);
		T4_9__tbp4.setVisible( show_enter);
//		if (show_enter) {
			T4_9__tbp4.setEtdVisibility();
//		}
		
		// @formatter:on
	}
}

/**   
 */
class CommonBar0 extends ClickPanCbar {
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
class CommonBar1 extends ClickPanCbar {
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	// =============================================================
	CommonBar1() { /* Constructor */
		setOpaque(false);
		setLayout(new MigLayout(App.simple, "5%[]5%[]", "push[]push"));
		// setLayout(new FlowLayout(FlowLayout.LEADING, 1, 3));

		@SuppressWarnings("unused")
		JButton b;

		add(b = new RpfResizeButton(Aaa.s_Std, "commonStepBack", 15, 65));

		add(b = new RpfResizeButton(Aaa.s_Std, "commonStepFwd", 50, 70, 0.9f));

		setVisible(false);
	}
}

/**   
 */
class CommonBar2 extends ClickPanCbar {
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	CommonBar2() { /* Constructor */
		// =============================================================
		setOpaque(false);
		setLayout(new MigLayout(App.simple, "6%[]5%[]", "push[]push"));

		@SuppressWarnings("unused")
		JButton b;

		add(b = new RpfResizeButton(Aaa.s_Std, "commonFlowBack", 15, 65));

		add(b = new RpfResizeButton(Aaa.s_Std, "commonFlowFwd", 65, 75, 0.93f));

		setVisible(false);
	}
}

/**   
 */
class ReviewBar3 extends ClickPanCbar {
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	ReviewBar3() { /* Constructor */
		// ============================================================================
		setOpaque(false);
		// setBackground(Aaa.baizeGreen);

		setLayout(new MigLayout(App.simple, "22%[]3%[]", "push[]push"));

		@SuppressWarnings("unused")
		JButton b;

		add(b = new RpfResizeButton(Aaa.s_Std, "reviewBackOneCard", 12, 65));
		add(b = new RpfResizeButton(Aaa.s_Std, "reviewFwdOneCard", 12, 65));

		setVisible(false);
	}

}

/**   
 */
class CommonBar3 extends ClickPanCbar {
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	private RpfResizeButton b1st;
	private RpfResizeButton contdds;

	public boolean is1stBtnVisible() {
		return b1st.isVisible();
	}

	public boolean isEnterTheDealBtnVisible() {
		return b1st.isVisible();
	}

	CommonBar3() { /* Constructor */
		// ============================================================================
		setOpaque(false);
		// setBackground(Aaa.baizeGreen);

		setLayout(new MigLayout(App.simple, "push[]5%[]5%", "push[]push"));

		add(b1st = new RpfResizeButton(Aaa.s_Std, "tutorialIntoDealB1st", 21, 60, 0.75f));
		add(contdds = new RpfResizeButton(Aaa.s_Std, "tutorialIntoDealContdds", 26, 60, 0.75f));

		setVisible(false);
	}

	public void set1stContVisibility() {
		// =============================================================
		{
			boolean vis = false;
			if (App.isLin__Virgin_or_Single() == false) {
				if (App.mg != null) {
					GraInfo gi = App.mg.giAy.get(App.mg.stop_gi);
					char bv = gi.get_CapEnv__bv_1st();
					if (bv == 'v')
						vis = true;
				}
			}
			b1st.setVisible(vis);
		}

		{
			boolean vis = false;
			App.cont_button_is_cont__cashed = true;

			if (App.isLin__Virgin_or_Single() == false) {
				if (App.mg != null) {
					GraInfo gi = App.mg.giAy.get(App.mg.stop_gi);
					char bv = gi.get_CapEnv__bv_contdds();
					if (bv != 'h') {
						vis = true;
						contdds.setText((bv == 'c') ? Aaf.cmdBar_cont : Aaf.cmdBar_dds);
						contdds.setToolTipText((bv == 'c') ? Aaf.cmdBar_cont_TT : Aaf.cmdBar_dds_TT);
						App.cont_button_is_cont__cashed = (bv == 'c');
					}
				}
			}
			contdds.setVisible(vis);
		}
	}
}

/**   
 */
class MovieModePart4 extends ClickPanCbar {
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	RpfResizeButton btm;

	boolean isBackToMovieBtnVisible() {
		return btm.isVisible() && App.isVmode_InsideADeal();
	}

	MovieModePart4() { /* Constructor */
		// =============================================================
		setOpaque(false);
		setLayout(new MigLayout(App.simple, "[]", "push[]push"));

		add(btm = new RpfResizeButton(Aaa.s_Std, "dealmodeBackToMovie", 95, 70, 0.75f));

		setVisible(false);
	}

}

/**   
 */
class CommonBar4 extends ClickPanCbar {
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	RpfResizeButton etd;

	boolean isEnterTheDealBtnVisible() {
		return etd.isVisible() && !App.isVmode_InsideADeal();
	}

	CommonBar4() { /* Constructor */
		// =============================================================
		setOpaque(false);
		setLayout(new MigLayout(App.simple, "[]", "push[]push"));

		add(etd = new RpfResizeButton(Aaa.s_Std, "tutorialIntoDealClever", 95, 70, 0.80f));

		setVisible(false);
	}

	public void setEtdVisibility() {
		// =============================================================
		boolean vis = true;
		if ((App.isLin__Virgin_or_Single() == false)) {
			if (App.mg != null) {
				GraInfo gi = App.mg.giAy.get(App.mg.stop_gi);
				int bv = gi.get_CapEnv__bv_etd();
				if (bv == 'h')
					vis = false;
			}
		}
		etd.setVisible(vis);
		// System.out.println( vis);
	}
}
