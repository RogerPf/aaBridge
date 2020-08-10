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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JPanel;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.model.Dir;
import com.rpsd.bridgefonts.BridgeFonts;

import net.miginfocom.swing.MigLayout;

/**
 */
public class BubblePanel extends JPanel {
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	Dir phyDir;
	private BubblePanelInner bpi;

	/**
	 */
	public BubblePanel(Dir phyDir) { // Constructor
		// ==============================================================================================
		bpi = new BubblePanelInner(phyDir);
		this.phyDir = phyDir;
		if (phyDir == Dir.North) {
			setLayout(new MigLayout(App.simple, "0%[92%]8%", "2%[70%]28%"));
		}
		if (phyDir == Dir.East) {
			setLayout(new MigLayout(App.simple, "8%[92%]0%", "0%[70%]28%"));
		}
		if (phyDir == Dir.South) {
			setLayout(new MigLayout(App.simple, "8%[92%]0%", "15%[67%]"));
		}
		if (phyDir == Dir.West) {
			setLayout(new MigLayout(App.simple, "3%[89%]8%", "30%[70%]0%"));
		}

		add(bpi, App.hm3oneHun);
		setOpaque(false);
	}

	/**
	 */
	public void paintComponent(Graphics g) {
		// ============================================================================
		// super.paintComponent(g);
	}
}

/**
 */
class BubblePanelInner extends JPanel {
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	Dir phyDir;

	AffineTransform tx = new AffineTransform();
	Line2D.Double line = new Line2D.Double(40, 50, 30, 20);

	Polygon arrowHead = new Polygon();

	public BubblePanelInner(Dir phyDir) { // Constructor
		// ==============================================================================================
		this.phyDir = phyDir;

		arrowHead.addPoint(0, 5);
		arrowHead.addPoint(-5, -5);
		arrowHead.addPoint(5, -5);

		setOpaque(false);
	}

	/**
	 */
	public void paintComponent(Graphics g) {
		// ============================================================================
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;
		Aaa.commonGraphicsSettings(g2);

		Dir dir = App.cpeFromPhyScreenPos(phyDir);
		String text = App.deal.hands[dir.v].bubbleText;
		if (text.isEmpty())
			return;

		text = Aaa.deAtQuestionAndBubbleText(text);

		int width = getWidth();
		int height = getHeight();

		float wp = width * 0.05f;
		float hp = height * 0.05f;
		float round = wp * 3;

		RoundRectangle2D.Float rr = new RoundRectangle2D.Float();

		if (phyDir == Dir.North) { // North --------------------------------------
			Point from = new Point(width / 3, (int) (height * 0.38f));
			Point to = new Point(0, (int) (height * 0.38f));

			Shape arrow = App.createArrowShape(from, to);
			g2.setColor(Aaa.weedyBlack);
			g2.fill(arrow);

			rr.setRoundRect(hp * 3, hp, width - 4 * hp, height - 4 * hp, round, round);
			g2.setColor(Aaa.lightGrayBubble);
			g2.fill(rr);

			g2.setStroke(new BasicStroke(hp));
			g2.setColor(Aaa.weedyBlack);
			g2.draw(rr);

			to.x += height * 0.07;
			arrow = App.createArrowShape(from, to);
			g2.setColor(Aaa.lightGrayBubble);
			g2.fill(arrow);
		}

		else if (phyDir == Dir.East) { // East --------------------------------------
			Point from = new Point((width * 2) / 8, height * 3 / 4);
			Point to = new Point((width * 2) / 8, 0);

			Shape arrow = App.createArrowShape(from, to);
			g2.setColor(Aaa.weedyBlack);
			g2.fill(arrow);

			rr.setRoundRect(hp, 3 * hp, width - 4 * hp, height - 4 * hp, round, round);
			g2.setColor(Aaa.lightGrayBubble);
			g2.fill(rr);

			g2.setStroke(new BasicStroke(hp));
			g2.setColor(Aaa.weedyBlack);
			g2.draw(rr);

			to.y += height * 0.07;
			arrow = App.createArrowShape(from, to);
			g2.setColor(Aaa.lightGrayBubble);
			g2.fill(arrow);
		}

		else if (phyDir == Dir.South) { // South --------------------------------------
			Point from = new Point(width * 2 / 3, (int) (height * 0.45f));
			Point to = new Point(width, (int) (height * 0.45f));

			Shape arrow = App.createArrowShape(from, to);
			g2.setColor(Aaa.weedyBlack);
			g2.fill(arrow);

			rr.setRoundRect(hp, hp, width - 4.2 * hp, height - 3 * hp, round, round);
			g2.setColor(Aaa.lightGrayBubble);
			g2.fill(rr);

			g2.setStroke(new BasicStroke(hp));
			g2.setColor(Aaa.weedyBlack);
			g2.draw(rr);

			to.x = (int) (width * 0.97f);
			arrow = App.createArrowShape(from, to);
			g2.setColor(Aaa.lightGrayBubble);
			g2.fill(arrow);
		}

		else if (phyDir == Dir.West) { // West --------------------------------------
			Point from = new Point(width / 3, height / 4);
			Point to = new Point(width / 3, height);

			Shape arrow = App.createArrowShape(from, to);
			g2.setColor(Aaa.weedyBlack);
			g2.fill(arrow);

			rr.setRoundRect(hp, hp, width - 2 * hp, height - 4 * hp, round, round);
			g2.setColor(Aaa.lightGrayBubble);
			g2.fill(rr);

			g2.setStroke(new BasicStroke(hp));
			g2.setColor(Aaa.weedyBlack);
			g2.draw(rr);

			to.y -= height * 0.06;
			arrow = App.createArrowShape(from, to);
			g2.setColor(Aaa.lightGrayBubble);
			g2.fill(arrow);
		}

		/**
		 * Time for the text, Zebedie
		 */

		float fSize = App.gbp.getHeight() * 0.032f;
		Font font = BridgeFonts.internatBoldFont.deriveFont(fSize);

		g2.setFont(font);
		g2.setColor(Color.black);

		if (text.length() > 99)
			text = text.substring(0, 99);

		int max = 24;
		int back = 7;
		float x = rr.x + 2 * hp;
		float y = rr.y + hp + fSize;
		while (text.length() > 0) {
			String s;
			if (text.length() <= max) {
				s = text;
				text = "";
			}
			else {
				int i = max;
				for (; i > max - back; i--) {
					if (text.charAt(i) == ' ')
						break;
				}
				if (i < max - back)
					i = max;
				s = text.substring(0, i);
				text = text.substring(i);
			}
			g2.drawString(s.trim(), x, y);
			y += fSize;
		}
	}

//	/**
//	 */
//	public static float drawCenteredString(Graphics2D g2, String text, float xOrg, float yOrg, float wOrg, float hOrg) {
//		// ************************************************************************
//		FontMetrics fm = g2.getFontMetrics(g2.getFont());
//		Rectangle2D rect = fm.getStringBounds(text, g2);
//		int textHeight = (int) (rect.getHeight());
//		int textWidth = (int) (rect.getWidth());
//
//		float x = xOrg + (wOrg - textWidth) / 2f;
//		float y = yOrg + (hOrg - textHeight) / 2f + fm.getAscent() * 0.95f;
//
//		g2.drawString(text, x, y);
//		return x;
//	}

}
