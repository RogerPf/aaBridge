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
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.Aaf;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.model.Cc;
import com.rpsd.bridgefonts.BridgeFonts;

/** ***************************
 * See https://weblogs.java.net/blog/2006/09/20/well-behaved-glasspane
 */
public class AaDragGlassPane extends JPanel implements AWTEventListener {

	private static final long serialVersionUID = 1L;

	private final JFrame frame;
	private Point mouseWas = null;
	private BufferedImage dragImage = null;
	private Dimension offset = new Dimension();

	public final static int none = 0;
	public final static int mouse_wheel = 1;
	public final static int new_deal_levels = 2;
	public final static int docCol_copied = 3;
	public final static int docCol_error = 4;

	private int splash_id = none;

	/**
	 */
	public AaDragGlassPane(JFrame frame) {
		super(null);
		this.frame = frame;
		setOpaque(false);
	}

	/**
	*/
	public void showOverlayScreen(int id) {
		splash_id = id;
		splashScreenCompleteTimer.start();
		setVisible(true);
	};

	/**
	*/
	public Timer splashScreenCompleteTimer = new Timer(5000, new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			splashScreenCompleteTimer.stop();
			setVisible(false);
			splash_id = none;
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
		else if (splashScreenCompleteTimer.isRunning()) {

			float panelWidth = (float) getWidth();
			float panelHeight = (float) getHeight();

			float marginLeft = panelWidth * 0.375f;
			float marginRight = panelWidth * 0.375f;
			float marginTop = panelHeight * 0.375f;
			float marginBottom = panelHeight * 0.40f;

			float activityWidth = panelWidth - (marginLeft + marginRight);
			float activityHeight = panelHeight - (marginTop + marginBottom);

			float curve = panelHeight * 0.001f;

			RoundRectangle2D.Float rr = new RoundRectangle2D.Float();

			// fill the lozenge ----------------------------------------------

			switch (splash_id) {
			case docCol_error:
			case mouse_wheel:
				g2.setColor(Cc.RedWeak);
				break;

			case docCol_copied:
			case new_deal_levels:
				g2.setColor(Cc.GreenWeak);
				break;
			}

			//g2.setColor(splash_msg_NewDeal ? Cc.GreenWeak : Cc.RedWeak);
			rr.setRoundRect(marginLeft, marginTop, activityWidth, activityHeight, curve, curve);
			g2.fill(rr);

			g2.setStroke(new BasicStroke(activityWidth * 0.03f));
			g2.setColor(Color.darkGray);
			g2.draw(rr);

			// Add in the text
			float fontSize = activityWidth * 0.14f;

			String docC = "aaBridge - Doc Collection";

			switch (splash_id) {

			case mouse_wheel:
				// mouse wheel message
				g2.setColor(Aaa.genOffWhite);
				g2.setFont(BridgeFonts.internatBoldFont.deriveFont(fontSize * 0.5f));
				Aaa.drawCenteredString(g2, Aaf.splash_wh1, marginLeft, marginTop + activityHeight * 0.05f, activityWidth, activityHeight * 0.25f);

				g2.setFont(BridgeFonts.internatBoldFont.deriveFont(fontSize * 0.65f));
				Aaa.drawCenteredString(g2, Aaf.splash_wh2, marginLeft, marginTop + activityHeight * 0.10f, activityWidth, activityHeight * 0.75f);

				g2.setFont(BridgeFonts.internatBoldFont.deriveFont(fontSize * 0.55f));
				Aaa.drawCenteredString(g2, Aaf.splash_wh3, marginLeft, marginTop + activityHeight * 0.52f, activityWidth, activityHeight * 0.45f);
				break;

			case new_deal_levels:
				// new deal message
				g2.setColor(Aaa.genOffWhite);
				g2.setFont(BridgeFonts.internatBoldFont.deriveFont(fontSize * 0.5f));
				Aaa.drawCenteredString(g2, Aaf.splash_deal1, marginLeft, marginTop + activityHeight * 0.05f, activityWidth, activityHeight * 0.25f);

				g2.setFont(BridgeFonts.internatBoldFont.deriveFont(fontSize * 0.55f));
				Aaa.drawCenteredString(g2, Aaf.splash_deal2, marginLeft, marginTop + activityHeight * 0.10f, activityWidth, activityHeight * 0.75f);

				g2.setFont(BridgeFonts.internatBoldFont.deriveFont(fontSize * 0.55f));
				Aaa.drawCenteredString(g2, Aaf.splash_deal3, marginLeft, marginTop + activityHeight * 0.52f, activityWidth, activityHeight * 0.45f);
				break;

			case docCol_error:
				// docColl delete needed
				g2.setColor(Aaa.genOffWhite);
				g2.setFont(BridgeFonts.internatBoldFont.deriveFont(fontSize * 0.60f));
				Aaa.drawCenteredString(g2, Aaf.splash_dcEr1, marginLeft, marginTop + activityHeight * 0.05f, activityWidth, activityHeight * 0.25f);

				g2.setFont(BridgeFonts.internatBoldFont.deriveFont(fontSize * 0.50f));
				Aaa.drawCenteredString(g2, docC, marginLeft, marginTop + activityHeight * 0.11f, activityWidth, activityHeight * 0.75f);

				g2.setFont(BridgeFonts.internatBoldFont.deriveFont(fontSize * 0.60f));
				Aaa.drawCenteredString(g2, Aaf.splash_dcEr2, marginLeft, marginTop + activityHeight * 0.55f, activityWidth, activityHeight * 0.45f);
				break;

			case docCol_copied:
				// docColl copied OK
				g2.setColor(Aaa.genOffWhite);
				g2.setFont(BridgeFonts.internatBoldFont.deriveFont(fontSize * 0.50f));
				Aaa.drawCenteredString(g2, docC, marginLeft, marginTop + activityHeight * 0.22f, activityWidth, activityHeight * 0.25f);

				g2.setFont(BridgeFonts.internatBoldFont.deriveFont(fontSize * 0.63f));
				Aaa.drawCenteredString(g2, Aaf.splash_dcOk1, marginLeft, marginTop + activityHeight * 0.32f, activityWidth, activityHeight * 0.75f);

			}
		}
	}
}
