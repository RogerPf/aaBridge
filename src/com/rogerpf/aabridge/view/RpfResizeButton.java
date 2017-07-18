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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;

import javax.swing.JButton;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.controller.CmdHandler;
import com.rogerpf.aabridge.model.Cc;
import com.rpsd.bridgefonts.BridgeFonts;

/**   
 */
public class RpfResizeButton extends JButton implements MouseListener {
	/**
	 * 
	 */
	boolean hover = false;
	boolean pressed = false;
	public boolean suit_symbol = false;
	int original_type;
	int current_type;

	private static final long serialVersionUID = 1L;
	int widthPc = 20; // if neg then it means heightPc * (-widthPc);
	int heightPc = 20;
	float fontToHeightRatio = 0.85f;

	Font font = BridgeFonts.internatBoldFont;
	Color bgColor = Aaa.buttonBkgColorStd;
	Color fgColorOverride = Aaa.weedyBlack;
	boolean useFgColorOverride = false;
	Color hoverColor = Aaa.hoverColor;
	Color pressedColor = Aaa.pressedColor;
	Color selectedBackground = Aaa.baizeGreen_bdk;

	CmdHandler.RpfBtnDef rpfButtonDef;

	boolean autoFontSizeAdjust = false;

	public RpfResizeButton(int type, String cmd, int widthPc, int heightPc) { /* Constructor */
		super();
		autoFontSizeAdjust = true;
		btnCommonConstructor(type, cmd, widthPc, heightPc, this.fontToHeightRatio);
	}

	public RpfResizeButton(int type, String cmd, int widthPc, int heightPc, boolean autoFontSizeAdjust_v) { /* Constructor */
		super();
		autoFontSizeAdjust = autoFontSizeAdjust_v;
		btnCommonConstructor(type, cmd, widthPc, heightPc, this.fontToHeightRatio);
	}

	public RpfResizeButton(int type, String cmd, int widthPc, int heightPc, float fontToHeightRatio) { /* Constructor */
		super();
		autoFontSizeAdjust = true;
		btnCommonConstructor(type, cmd, widthPc, heightPc, fontToHeightRatio);
	}

	public RpfResizeButton(int type, String cmd, int widthPc, int heightPc, float fontToHeightRatio, boolean autoFontSizeAdjust_v) { /* Constructor */
		super();
		autoFontSizeAdjust = autoFontSizeAdjust_v;
		btnCommonConstructor(type, cmd, widthPc, heightPc, fontToHeightRatio);
	}

	void btnCommonConstructor(int type, String cmd, int widthPc, int heightPc, float fontToHeightRatio) {
		setOpaque(false);
		this.original_type = type;
		this.current_type = type;

		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false), "none");
		this.widthPc = widthPc;
		this.heightPc = heightPc;
		this.fontToHeightRatio = fontToHeightRatio;
		setBorder(new EmptyBorder(0, 0, 0, 0));
		setFocusable(false);
		setMinimumSize(new Dimension(1, 1));

		addMouseListener(this);

		if (type == Aaa.s_SelfCmd) { // self listening button like bid butts and claim
			setActionCommand(cmd);
			setText(cmd);
		}
		else if (type == Aaa.s_Std || type == Aaa.m_Std) {
//			System.out.println(cmd);
			setActionCommand(cmd);
			rpfButtonDef = CmdHandler.getDef(cmd);
			setText(rpfButtonDef.btnText);
			setToolTipText(rpfButtonDef.tooltip);
			addActionListener(App.con);
		}
		else if (type == Aaa.s_Label) { // fake label - no actionListener
			rpfButtonDef = CmdHandler.getDef(cmd);
			setToolTipText(rpfButtonDef.tooltip);
			setText(rpfButtonDef.btnText);
		}
		else if (type == Aaa.s_SelfLabel) { // fake (self) label - no actionListener
			setText(cmd);
		}
		else {
			assert (false);
		}
	}

	public void changeType(int type) {
		assert (original_type == Aaa.m_Std);

		if (type == current_type) {
			return;
		}

		if (type == Aaa.m_Std) { // normal button
			setEnabled(true);
		}

		else if (type == Aaa.m_Label) {
			setEnabled(false);
		}

		else if (type == Aaa.m_Hidden) {
			setEnabled(false);
		}

		setVisible(type != Aaa.m_Hidden);

		current_type = type;
	}

	public Dimension correctSize() {

		Dimension dUs = getParent().getSize();

		dUs.height = (dUs.height * heightPc) / (100);
		if (widthPc < 0) {
			dUs.width = dUs.height * (-widthPc);
		}
		else if (widthPc > 0) {
			dUs.width = (dUs.width * widthPc) / (100);
		}
		else {
			/* width was (and still is) set to be zero */
			Font font = getParent().getFont();
			setFont(font);
			FontMetrics fm = getFontMetrics(font);
			String text = getText();

			int len = text.length();
			if (len < 2) {
				text += "WW";
			}
			else if (len < 3) {
				text += "ws";
			}
			else if (len < 4) {
				text += "W";
			}
			else if (len < 5) {
				text += "s";
			}
			int orgTextWidth = (int) fm.stringWidth(text);

			float ffactor = 1f;

			// ugly but much more stable than using - GetParent.getWidth();
			int tupWidth = App.tup.getWidth();

			if (tupWidth == 3000) {
				tupWidth = (App.frame.getWidth() * 8) / 10; // nasty work arround for a silly side effect
			}

			if (tupWidth == 0) {
				/* we don't know our parents real width */
			}
			else {
				ffactor = (float) (tupWidth / 550f); // more ugglyness
			}
			dUs.width = (int) (orgTextWidth * ffactor);

			// System.out.println( orgTextWidth + " " + ffactor + "  tup " + tupWidth + " ================================================");
		}
		return dUs;
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

	public void setSelectedBackground(Color selectedBackground) {
		this.selectedBackground = selectedBackground;
	}

	public void setForeground(Color color) {
		this.useFgColorOverride = true;
		this.fgColorOverride = color;
	}

	public void mousePressed(MouseEvent e) {
		pressed = true;
		repaint(); // not App.frame.repaint()
	}

	public void mouseReleased(MouseEvent e) {
		pressed = false;
		repaint(); // not App.frame.repaint()
	}

	public void mouseEntered(MouseEvent e) {
		hover = true;
		repaint(); // not App.frame.repaint()
	}

	public void mouseExited(MouseEvent e) {
		hover = false;
		repaint(); // not App.frame.repaint()
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void paintComponent(Graphics g) {

		// super.paintComponent(g); not a good idea we get a std button

		Graphics2D g2 = (Graphics2D) g.create();
		Aaa.commonGraphicsSettings(g2);

		if (current_type == Aaa.s_Label || current_type == Aaa.s_SelfLabel || current_type == Aaa.m_Hidden) {
//			g2.setColor(Aaa.baizeGreen);
		}
		else if (current_type == Aaa.m_Label) {
			g2.setColor(selectedBackground);
			g2.fillRect(0, 0, getWidth(), getHeight());

			int w = getWidth();
			int h = getHeight();

			float lw = (float) h * 0.05f;
			Rectangle2D.Float rec = new Rectangle2D.Float(lw / 2, lw / 2, (float) w - lw, (float) h - lw);
			g2.setStroke(new BasicStroke(lw));
//			g2.setColor(Color.BLACK);
			g2.setColor(Aaa.mButtonOutlineCol);
			g2.draw(rec);
		}
		else {
			if (pressed)
				g2.setColor(pressedColor);
			else if (hover)
				g2.setColor(hoverColor);
			else
				g2.setColor(bgColor);

			g2.fillRect(0, 0, getWidth(), getHeight());
		}

		if (current_type == Aaa.m_Hidden)
			return;

		Color fgc = useFgColorOverride ? fgColorOverride : Cc.g(Cc.rpfDefBtnColor);
		if (current_type == Aaa.s_Label || current_type == Aaa.m_Label) {
			fgc = Aaa.selectedButFontCol;
		}
		else if (hover) {
			fgc = Aaa.hoverButFontCol;
		}

		g2.setColor(fgc);

		String text = getText();
		if (text == null) {
			System.out.println("Rpf button with  NULL  text");
			text = "null";
		}

		int height = getHeight();

		Font f;

		if (App.isUsing__en_US || suit_symbol) {
			f = font.deriveFont(height * fontToHeightRatio); // so currently no en_US buttons can show accents
		}
		else {
			f = BridgeFonts.internatBoldFont.deriveFont(height * fontToHeightRatio);

			if (autoFontSizeAdjust && (text.length() > 3)) {
				int width = getWidth();

				int fm_twidth = getFontMetrics(f).stringWidth("-" + text + "-");

				if (fm_twidth > 0 && fm_twidth > width) {
					float ratio = (float) width / (float) fm_twidth;
					f = font.deriveFont(height * fontToHeightRatio * ratio);
					// System.out.println( text + "   L:" + text.length() + "  fm_wt:" + fm_twidth + "   r:" + ratio + "  fr:" + fontToHeightRatio + "    w:" +
					// width + "    h:" + height );
				}
			}
		}

		g2.setFont(f);

		char first = (text.length() > 0) ? text.charAt(0) : ' ';
		float nudgeUp = ((first == '>') || (first == '<')) ? -0.04f * height * fontToHeightRatio : 0.0f;

		Aaa.drawCenteredString(g2, text, 0, nudgeUp, getWidth(), height);

	}

}
