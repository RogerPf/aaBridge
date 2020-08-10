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

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.controller.Controller;

/**    
 */
public class ClickPanel extends JPanel implements MouseListener, MouseWheelListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// ----------------------------------------
	public ClickPanel() { /* Constructor */
		setOpaque(false);
		// setBackground(Aaa.baizeGreen);
		// setBackground(Aaa.baizePink);

		this.addMouseListener(this);
		this.addMouseWheelListener(this);
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {

		if (e.getButton() == MouseEvent.BUTTON3) {
			App.frame.rightClickPasteTimer.start();
			return;
		}

		App.gbp.c1_1__tfdp.clearShowCompletedTrick();
		if (App.gbp.c0_0__tlp.descEntry.hasFocus()) {
			App.gbp.c0_0__tlp.descEntry.setFocusable(false);
		}
		App.gbp.hideClaimButtonsIfShowing();
	}

	static double scroll_rs_value = 0;

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {

		boolean real_up_down = ((e.getModifiers() & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK);

		int rot_int = e.getWheelRotation();
		double rot_doub = rot_int;

		if (!App.using_java_6) {
			rot_doub = e.getPreciseWheelRotation();
		}

		if (App.mouseWheelSensitivity == 0) { // the original way things were done

			if ((rot_int > 0) || (rot_doub > 0.01)) {
				down(real_up_down);
			}
			else if ((rot_int < 0) || (rot_doub < -0.01)) {
				up(real_up_down);
			}
			return;
		}

		double threashold;

		switch (App.mouseWheelSensitivity) {
		// @formatter:off
		   default: threashold = 1.0;  break;  //  case 1 and all uncovered
		   case 2: threashold = 1.5;   break;
		   case 3: threashold = 2.0;   break;
		   case 4: threashold = 2.5;   break;
		   case 5: threashold = 3.0;   break;
		   case 6: threashold = 3.5;   break;
		   case 7: threashold = 4.0;   break;
		   case 8: threashold = 5.0;   break;
		// @formatter:on	
		}

		scroll_rs_value += rot_doub;

		// System.out.println("Mouse: " + rot_int + "    " + rot_doub + "   tot: " + scroll_rs_value + "   threashold: " + threashold);

		while (scroll_rs_value >= threashold) {
			down(real_up_down);
			scroll_rs_value -= threashold;
		}

		while (scroll_rs_value <= -threashold) {
			up(real_up_down);
			scroll_rs_value += threashold;
		}
	}

	private void up(boolean real_up_down) {
		if (real_up_down) {
			App.ddsAnalyserPanelVisible = false;
			if (App.mouseWheelInverted)
				Controller.Down_keyPressed();
			else
				Controller.Up_keyPressed();
		}
		else {
			if (App.mouseWheelInverted)
				Controller.Right_keyPressed();
			else
				Controller.Left_keyPressed();
		}
	}

	private void down(boolean real_up_down) {
		if (real_up_down) {
			App.ddsAnalyserPanelVisible = false;
			if (App.mouseWheelInverted)
				Controller.Up_keyPressed();
			else
				Controller.Down_keyPressed();
		}
		else {
			if (App.mouseWheelInverted)
				Controller.Left_keyPressed();
			else
				Controller.Right_keyPressed();
		}
	}

}

/**    
 */
class ClickPanCbar extends ClickPanel implements MouseListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// ----------------------------------------
	public ClickPanCbar() { /* Constructor */
		setOpaque(false);
		// setBackground(Aaa.baizeGreen);
		// setBackground(Aaa.baizePink);

		this.addMouseListener(this);
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) {
			App.frame.rightClickPasteTimer.start();
			return;
		}
	}

}

/**   
 */
class DarkGrayHiddenPanel extends ClickPanel {

	private static final long serialVersionUID = 1L;

	// -------------------------------------
	DarkGrayHiddenPanel() { // constructor
		// setBackground(Aaa.darkGrayBg);
		// setBackground(Aaa.baizePink);
		setVisible(false);
	}
}

/**   
 */
class DarkGrayHiddenPanel_pink extends DarkGrayHiddenPanel {

	private static final long serialVersionUID = 1L;

	// -------------------------------------
	DarkGrayHiddenPanel_pink() { // constructor
		// setBackground(Aaa.darkGrayBg);
		setOpaque(true);
		setBackground(Aaa.baizePink);
		setVisible(true);
	}
}

/**   
 */
class AaFixedRatioPanel extends ClickPanel {

	private static final long serialVersionUID = 1L;

	public AaFixedRatioPanel() {
		setOpaque(false);
//		setPreferredSize(new Dimension(5000, 5000));
	}

}
