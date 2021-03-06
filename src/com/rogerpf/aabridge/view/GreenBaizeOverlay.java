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
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.Aaf;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.model.Cc;
import com.rogerpf.aabridge.model.Dir;
import com.rpsd.bridgefonts.BridgeFonts;

import net.miginfocom.swing.MigLayout;

/**
 */
public class GreenBaizeOverlay extends JPanel implements AWTEventListener {
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	private final JFrame frame;
	@SuppressWarnings("unused")
	private Point mouseWas = null;

	public GreenBaizeOverlay(JFrame frame) { /* Constructor */

		super(null);
		this.frame = frame;

		setLayout(new MigLayout(App.simple, App.frame.layOut_columns__gbo, App.frame.layOut_rowsA__gbo));

		add(App.bubblePanels[Dir.West.v], App.hm1oneHun);
		add(App.bubblePanels[Dir.North.v], App.hm1oneHun + ", wrap");
		add(App.bubblePanels[Dir.South.v], App.hm1oneHun);
		add(App.bubblePanels[Dir.East.v], App.hm1oneHun);

		setVisible(false); // Only shown when needed - we are mainly transparent anyway
		setOpaque(false);
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

	// ----------------------------------------------------------------------

	Boolean showEditHint = false;
	/**
	*/
	public Timer editHintTimer = new Timer(3000 /* ms */, new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			editHintTimer.stop();
			showEditHint = false;
			setVisible(false);
		}
	});

	public void showEditHint() {
		if (App.isStudyDeal())
			return;
		editHintTimer.start();
		showEditHint = true;
		setVisible(true);
	}

	// ----------------------------------------------------------------------

	Boolean showVuGraphHint = false;
	/**
	*/
	public Timer vuGraphHintTimer = new Timer(4000 /* ms */, new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			vuGraphHintTimer.stop();
			showVuGraphHint = false;
			setVisible(false);
		}
	});

	public void showVuGraphHint() {
		vuGraphHintTimer.start();
		showVuGraphHint = true;
		setVisible(true);
	}

	// ----------------------------------------------------------------------

	Boolean showNewBoardHint = false;
	/**
	*/
	public Timer newBoardHintTimer = new Timer(3000 /* ms */, new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			newBoardHintTimer.stop();
			showNewBoardHint = false;
			setVisible(false);
		}
	});

	public void showNewBoardHint() {
		if (showDividerHint)
			return;
		newBoardHintTimer.start();
		showNewBoardHint = true;
		setVisible(true);
	}

	// ----------------------------------------------------------------------

	Boolean showDividerHint = false;
	/**
	*/
	public Timer dividerHintTimer = new Timer(3000 /* ms */, new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			dividerHintTimer.stop();
			showDividerHint = false;
			setVisible(false);
		}
	});

	public void showDividerHint() {
		if (showNewBoardHint)
			return;
		dividerHintTimer.start();
		showDividerHint = true;
		setVisible(true);
	}

	/**
	 */
	public void paintComponent(Graphics g) {
		// =============================================================
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;

		Aaa.commonGraphicsSettings(g2);

		int width = getWidth();
		int height = getHeight();

		if (showEditHint) {

			float from_w = width * 0.265f;
			float to_w = width * 0.10f;

			float tf_h = height * 0.688f;

			Point from = new Point((int) from_w, (int) tf_h);
			Point to = new Point((int) to_w, (int) tf_h);

			Shape arrow = App.createArrowShape(from, to);

			g2.setColor(Cc.RedStrong);
			g2.fill(arrow);
			g2.setColor(Cc.BlackStrong);
			g2.draw(arrow);

			Font font = BridgeFonts.internatBoldFont.deriveFont((float) height * 0.038f);
			g2.setFont(font);
			g2.setColor(Color.WHITE);

			g2.drawString(Aaf.redArrow_canEdit, to_w * 1.20f, tf_h * 1.015f);
		}

		if (showNewBoardHint) {

			float from_w = width * 0.42f;
			float to_w = width * 0.25f;

			float tf_h = height * 0.95f;

			Point from = new Point((int) from_w, (int) tf_h);
			Point to = new Point((int) to_w, (int) tf_h);

			Shape arrow = App.createArrowShape(from, to);

			g2.setColor(Cc.RedStrong);
			g2.fill(arrow);
			g2.setColor(Cc.BlackStrong);
			g2.draw(arrow);

			Font font = BridgeFonts.internatBoldFont.deriveFont((float) height * 0.033f);
			g2.setFont(font);
			g2.setColor(Color.WHITE);

			g2.drawString(Aaf.redArrow_newBoard, to_w * 1.08f, tf_h * 1.01f);
		}

		if (showVuGraphHint) {

			float from_w = width * 0.30f;
			float to_w = width * 0.00f;

			float tf_h = height * 0.2f;

			Point from = new Point((int) from_w, (int) tf_h);
			Point to = new Point((int) to_w, (int) tf_h);

			Shape arrow = App.createArrowShape(from, to);

			g2.setColor(Cc.RedStrong);
			g2.fill(arrow);
			g2.setColor(Cc.BlackStrong);
			g2.draw(arrow);

			Font font = BridgeFonts.internatBoldFont.deriveFont((float) height * 0.052f);
			g2.setFont(font);
			g2.setColor(Color.WHITE);

			g2.drawString(Aaf.redArrow_4Cols, width * 0.02f, tf_h * 1.08f);
		}

		if (showDividerHint) {

			float from_w = width * 0.73f;
			float to_w = width * 1.0f;

			float tf_h = height * 0.80f;

			Point from = new Point((int) from_w, (int) tf_h);
			Point to = new Point((int) to_w, (int) tf_h);

			Shape arrow = App.createArrowShape(from, to);

			g2.setColor(Cc.RedStrong);
			g2.fill(arrow);
			g2.setColor(Cc.BlackStrong);
			g2.draw(arrow);

			Font font = BridgeFonts.internatBoldFont.deriveFont((float) height * 0.038f);
			g2.setFont(font);
			g2.setColor(Color.WHITE);

			g2.drawString(Aaf.redArrow_dragDiv, from_w * 1.03f, tf_h * 1.015f);
		}

	}

}
