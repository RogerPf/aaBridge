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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;

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
		App.gbp.c1_1__tfdp.clearShowCompletedTrick();
		if (App.gbp.c0_0__tlp.descEntry.hasFocus()) {
			App.gbp.c0_0__tlp.descEntry.setFocusable(false);
		}
		App.gbp.hideClaimButtonsIfShowing();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
//		System.out.println("Wheel Moved " + e.getWheelRotation() + "  " + e.getPreciseWheelRotation());

//		if (App.useMouseWheel == false)
//			return;

		int rotation = e.getWheelRotation();

		double rot_doub = 0;

		if (App.using_java_6 == false) {
			rot_doub = e.getPreciseWheelRotation();
		}

		if ((rotation > 0) || (rot_doub > 0.01)) {
			Controller.Right_keyPressed();
		}
		else if ((rotation < 0) || (rot_doub < -0.01)) {
			Controller.Left_keyPressed();
		}
		else {
//			System.out.println("Wheel Moved both reported zero - " + e.getWheelRotation()  + "  " + e.getPreciseWheelRotation());
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
			App.frame.clickPasteTimer.start();
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
class AaFixedRatioPanel extends ClickPanel {

	private static final long serialVersionUID = 1L;

	public AaFixedRatioPanel() {
		setOpaque(false);
//		setPreferredSize(new Dimension(5000, 5000));
	}

}
