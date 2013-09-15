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
import com.rogerpf.aabridge.model.Zzz;
import com.rpsd.bridgefonts.BridgeFonts;

/**
 * See - "A well-behaved GlassPane"
 * http://weblogs.java.net/blog/alexfromsun/
 * <p/>
 * This is the final version of the GlassPane
 * it is transparent for MouseEvents,
 * and respects underneath component's cursors by default,
 * it is also friedly for other users,
 * if someone adds a mouseListener to this GlassPane
 * or set a new cursor it will respect them
 *
 * @author Alexander Potochkin
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
	public void showSplashScreen() {
		splashScreenCompleteTimer.start();
		setVisible(true);
		App.frame.repaint();
	};

	/**
	*/
	public Timer splashScreenCompleteTimer = new Timer(2500, new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			splashScreenCompleteTimer.stop();
			setVisible(false);
			App.frame.repaint();
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
	 * If someone adds a mouseListener to the GlassPane or set a new cursor
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
			repaint();
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
		else if (splashScreenCompleteTimer.isRunning()) {

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

			String t;

			t = "Welcome to";
			g2.setFont(BridgeFonts.bridgeBoldFont.deriveFont(fontSize * 0.5f));
			Aaa.drawCenteredString(g2, t, marginLeft, marginTop + activityHeight * 0.10f, activityWidth, activityHeight * 0.20f);

			t = " aaBridge ";
			g2.setFont(BridgeFonts.bridgeBoldFont.deriveFont(fontSize * 0.95f));
			float x = Aaa.drawCenteredString(g2, t, marginLeft, marginTop + activityHeight * 0.30f, activityWidth, activityHeight * 0.35f);

			t = "C D H S ";
			AttributedString at = new AttributedString(t);
			Font bf = BridgeFonts.faceAndSymbFont.deriveFont(fontSize);
			at.addAttribute(TextAttribute.FONT, bf, 0, t.length());

			for (int i : Zzz.cdhs) {
				at.addAttribute(TextAttribute.FOREGROUND, Aaa.cdhsColors[i], i * 2, i * 2 + 1);
			}
			FontRenderContext frc = g2.getFontRenderContext();
			TextLayout tl = new TextLayout(at.getIterator(), frc);
			tl.draw(g2, x /* marginLeft + activityWidth * 0.12f */, marginTop + activityHeight * 0.90f);

		}

		g2.dispose();
	}

}
