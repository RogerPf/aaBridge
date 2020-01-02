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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.geom.Rectangle2D;

import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.model.Cc;

import net.miginfocom.swing.MigLayout;

/**
 */
public class GreenBaizeRigid extends ClickPanel {
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	public GreenBaizeRigid() { /* Constructor */
		// =============================================================
		setOpaque(false);

		setPreferredSize(new Dimension(5000, 5000));

		setSideSpaceSize();
	}

	/**
	*/
	void setSideSpaceSize() {
		// =============================================================

		removeAll();

		setLayout(new MigLayout(App.simple, App.frame.layOut_columns__gbr, App.frame.layOut_rows__gbr));
		add(App.gbm, App.hm3oneHun + ", grow y");
	}

	/**
	*/
	public void paintComponent(Graphics g) {
		// =============================================================
		Graphics2D g2 = (Graphics2D) g;

		float width = getWidth();
		float height = getHeight();

		Rectangle2D.Float rect = new Rectangle2D.Float(0, 0, width, height);
		g2.setColor(Cc.g(Cc.baizeGreen));

		// @formatter:off
		if (    (App.colorIntensity < 0) 
			 && Cc.secondLighterGreen(Cc.baizeGreen, Cc.baizeGreen_c) ) {
			// @formatter:on
			Dimension d = new Dimension();
			Point p = new Point();
			boolean skip = true;

			if (App.gbp.c1_1__tfdp.isVisible()) {
				d = App.gbp.c1_1__tfdp.getSize();
				p = App.gbp.c1_1__tfdp.getLocationOnScreen();
				skip = false;
			}
			else if (App.gbp.c1_1__bfdp.isVisible()) {
				d = App.gbp.c1_1__bfdp.getSize();
				p = App.gbp.c1_1__bfdp.getLocationOnScreen();
				skip = false;
			}

			if (!skip) {
				Point us = getLocationOnScreen();

				float x = (p.x - us.x) + d.width / 2;
				float y = (p.y - us.y) + d.height / 2;

				float diameter = d.width * 0.67f;

				// @formatter:off
				g2.setPaint(new RadialGradientPaint(x, y, diameter, new float[] { 0.0f, 0.8f, 1.0f }, 
						new Color[] { 
						Cc.g(Cc.baizeGreen_c), 
						Cc.g(Cc.baizeGreen_c),
						Cc.g(Cc.baizeGreen) }));
				// @formatter:on
			}
		}

		g2.fill(rect);
	}

}
