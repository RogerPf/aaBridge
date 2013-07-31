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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.controller.CmdHandler;
import com.rpsd.bridgefonts.BridgeFonts;

/**   
 */
public class RpfResizeButton extends JButton implements MouseListener {
	/**
	 * 
	 */
	Dimension dim;

	boolean hover = false;
	boolean pressed = false;

	private static final long serialVersionUID = 1L;
	int widthPc = 20; // if neg then it means heightPc * (-widthPc);
	int heightPc = 20;
	float fontToHeightRatio = 0.85f;

	Font font = BridgeFonts.bridgeBoldFont;
	Color bgColor = Aaa.bidButsBkColor;
	Color fgColor = Aaa.weedyBlack;
	Color hoverColor = Aaa.hoverColor;
	Color pressedColor = Aaa.pressedColor;

	public RpfResizeButton(int con, String cmd, int widthPc, int heightPc) { /* Constructor */
		super();
		btnDefCon(con, cmd, widthPc, heightPc, this.fontToHeightRatio);
	}

	public RpfResizeButton(int con, String cmd, int widthPc, int heightPc, float fontToHeightRatio) { /* Constructor */
		super();
		btnDefCon(con, cmd, widthPc, heightPc, fontToHeightRatio);
	}

	void btnDefCon(int con, String cmd, int widthPc, int heightPc, float fontToHeightRatio) {
		setActionCommand(cmd);

		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false), "none");
		this.widthPc = widthPc;
		this.heightPc = heightPc;
		this.fontToHeightRatio = fontToHeightRatio;
		setBorder(new EmptyBorder(0, 0, 0, 0));
		setFocusable(false);
		setMinimumSize(new Dimension(1, 1));

		addMouseListener(this);

		if (con != 0) {
			CmdHandler.RpfBtnDef bd = CmdHandler.getDef(cmd);
			setText(bd.btnText);
			setToolTipText(bd.tooltip);
			addActionListener(App.con);
		}
		else {
			setText(cmd);
		}
	}

	public Dimension correctSize() {

		Dimension d = getParent().getSize();

		d.height = (d.height * heightPc) / (100);
		if (widthPc < 0) {
			d.width = d.height * (-widthPc);
		}
		else
			d.width = (d.width * widthPc) / (100);
		return d;
	}

	public Dimension getPreferredSize() {
		return correctSize();
	}

	public Dimension getMinimumSize() {
		return correctSize();
	}

	public Dimension getMaximumSize() {
		return correctSize();
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public void setHoverColor(Color color) {
		this.hoverColor = color;
	}

	public void setBackground(Color color) {
		this.bgColor = color;
	}

	public void setForeground(Color color) {
		this.fgColor = color;
	}

	public void mousePressed(MouseEvent e) {
		pressed = true;
		repaint();
	}

	public void mouseReleased(MouseEvent e) {
		pressed = false;
		repaint();
	}

	public void mouseEntered(MouseEvent e) {
		hover = true;
		repaint();
	}

	public void mouseExited(MouseEvent e) {
		hover = false;
		repaint();
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g.create();
		Aaa.commonGraphicsSettings(g2);

		if (pressed)
			g2.setColor(pressedColor);
		else if (hover)
			g2.setColor(hoverColor);
		else
			g2.setColor(bgColor);

		g2.fillRect(0, 0, getWidth(), getHeight());

		g2.setFont(font.deriveFont(getHeight() * fontToHeightRatio));
		g2.setColor(fgColor);

		Aaa.drawCenteredString(g2, getText(), 0, 0, getWidth(), getHeight());

	}

}
