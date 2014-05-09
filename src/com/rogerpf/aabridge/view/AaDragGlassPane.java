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

import java.awt.AWTEvent;
import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.text.AttributedString;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.model.Cc;
import com.rogerpf.aabridge.model.Suit;
import com.rpsd.bridgefonts.BridgeFonts;
import com.version.VersionAndBuilt;

/** ***************************
 * See https://weblogs.java.net/blog/2006/09/20/well-behaved-glasspane
 */
public class AaDragGlassPane extends JPanel implements AWTEventListener {

	private static final long serialVersionUID = 1L;

	private final JFrame frame;
	private Point mouseWas = null;
	private BufferedImage dragImage = null;
	private Dimension offset = new Dimension();

	/**
	 */
	public AaDragGlassPane(JFrame frame) {
		super(null);
		this.frame = frame;
		setOpaque(false);
	}

	/**
	*/
	public void showExpiredScreen() {
		splashScreenCompleteTimer.start();
		setVisible(true);
		// App.frame.repaint();
	};

	/**
	*/
	public Timer splashScreenCompleteTimer = new Timer(4500, new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			splashScreenCompleteTimer.stop();
			setVisible(false);
			// App.frame.repaint();
			if (VersionAndBuilt.hasExpired()) {
				System.exit(0);
			}
		}
	});

	/**
	 */
	public void SetDragImage(BufferedImage image) {
		dragImage = image;
		if (image == null) {
			mouseWas = null;
		}
		else {
			offset.width = (image.getWidth() * 10) / 16;
			offset.height = (image.getHeight() * 10) / 16;
		}
	}

	/**
	 * If someone adds a mouseListener to the GlassPane or sets a new cursor
	 * we expect that he knows what he is doing
	 * and return the super.contains(x, y)
	 * otherwise we return false to respect the cursors
	 * for the underneath components
	 */
	public boolean contains(int x, int y) {
		if (getMouseListeners().length == 0 && getMouseMotionListeners().length == 0 && getMouseWheelListeners().length == 0
				&& getCursor() == Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)) {
			return false;
		}
		return super.contains(x, y);
	}

	/**
	 */
	public void eventDispatched(AWTEvent event) {
		if (event instanceof MouseEvent) {
			MouseEvent me = (MouseEvent) event;
			if (!SwingUtilities.isDescendingFrom(me.getComponent(), frame)) {
				return;
			}
			if (me.getID() == MouseEvent.MOUSE_EXITED && me.getComponent() == frame) {
				mouseWas = null;
			}
			else {
				MouseEvent converted = SwingUtilities.convertMouseEvent(me.getComponent(), me, frame.getGlassPane());
				mouseWas = converted.getPoint();
			}
			App.frame.repaint();
		}
	}

	/**
	 */
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		Aaa.commonGraphicsSettings(g2);

		if (dragImage != null && mouseWas != null) {
			g2.drawImage(dragImage, null, mouseWas.x - offset.width, mouseWas.y - offset.height);
		}
		else if (splashScreenCompleteTimer.isRunning() || VersionAndBuilt.hasExpired()) {

			float panelWidth = (float) getWidth();
			float panelHeight = (float) getHeight();

			float marginLeft = panelWidth * 0.26f;
			float marginRight = panelWidth * 0.26f;
			float marginTop = panelHeight * 0.26f;
			float marginBottom = panelHeight * 0.26f;

			float activityWidth = panelWidth - (marginLeft + marginRight);
			float activityHeight = panelHeight - (marginTop + marginBottom);

			float curve = panelHeight * 0.25f;

			RoundRectangle2D.Float rr = new RoundRectangle2D.Float();

			// fill the lozenge ----------------------------------------------
			g2.setColor(Aaa.biddingBkColor);
			rr.setRoundRect(marginLeft, marginTop, activityWidth, activityHeight, curve, curve);
			g2.fill(rr);

			g2.setStroke(new BasicStroke(activityWidth * 0.03f));
			g2.setColor(Aaa.weedyBlack);
			g2.draw(rr);

			// Add in the text
			float fontSize = activityHeight * 0.25f;

			String t1 = "Welcome to";
			String t2 = " aaBridge ";
			if (VersionAndBuilt.hasExpired()) {
				t1 = "This Software";
				t2 = "Time Expired";
			}

			g2.setFont(BridgeFonts.bridgeBoldFont.deriveFont(fontSize * 0.5f));
			Aaa.drawCenteredString(g2, t1, marginLeft, marginTop + activityHeight * 0.10f, activityWidth, activityHeight * 0.20f);

			g2.setFont(BridgeFonts.bridgeBoldFont.deriveFont(fontSize * 0.75f));
			float x = Aaa.drawCenteredString(g2, t2, marginLeft, marginTop + activityHeight * 0.30f, activityWidth, activityHeight * 0.35f);

			String t3 = "C D H S ";
			AttributedString at = new AttributedString(t3);
			Font bf = BridgeFonts.faceAndSymbFont.deriveFont(fontSize);
			at.addAttribute(TextAttribute.FONT, bf, 0, t3.length());

			for (Suit suit : Suit.cdhs) {
				at.addAttribute(TextAttribute.FOREGROUND, suit.color(Cc.Ce.Strong), suit.v * 2, suit.v * 2 + 1);
			}
			FontRenderContext frc = g2.getFontRenderContext();
			TextLayout tl = new TextLayout(at.getIterator(), frc);
			tl.draw(g2, x /* marginLeft + activityWidth * 0.12f */, marginTop + activityHeight * 0.90f);

			if (VersionAndBuilt.hasExpired()) {
				App.frame.aaDragGlassPane.showExpiredScreen();
			}
		}
	}
}
