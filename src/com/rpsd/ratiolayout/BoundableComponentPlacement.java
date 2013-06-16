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
package com.rpsd.ratiolayout;

/*
 * ratiolayout - The code is not from my hand. It was obtained pre 2009 and now
 * in 2013 I have no idea where it originated. It may have been/was tweeked in
 * the past. Many thanks to the orginal author.
 */

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;

public class BoundableComponentPlacement implements BoundableInterface {

	public int position = Alignable.NORTHWEST;

	public void setBounds(Component c, int x, int y, int w, int h) {
		Dimension preferredSize = c.getPreferredSize();
		Dimension cellSize = new Dimension(w, h);
		Dimension newSize = min(cellSize, preferredSize);
		if (newSize.equals(cellSize)) {
			c.setBounds(x, y, cellSize.width, cellSize.height);
			return;
		}
		if (c instanceof Alignable) {
			Alignable a = (Alignable) c;
			position = a.getAlignment();
		}
		Point dp = getPosition(new Point(x, y), newSize, cellSize);
		c.setBounds(dp.x, dp.y, newSize.width, newSize.height);
	}

	public Dimension min(Dimension d1, Dimension d2) {
		if (d1.width < d2.width)
			return d1;
		if (d1.height < d2.height)
			return d1;
		return d2;
	}

	public Point getPosition(Point startPoint, Dimension newSize, Dimension cellSize) {

		int west = startPoint.x;
		int east = startPoint.x + cellSize.width - newSize.width;
		int centerX = startPoint.x + cellSize.width / 2 - newSize.width / 2;

		int north = startPoint.y;
		int south = startPoint.y + cellSize.height - newSize.height;
		int centerY = startPoint.y + cellSize.height / 2 - newSize.height / 2;

		switch (position) {
		case Alignable.NORTHWEST:
			return new Point(west, north);
		case Alignable.NORTH:
			return new Point(centerX, north);
		case Alignable.NORTHEAST:
			return new Point(east, north);
		case Alignable.EAST:
			return new Point(east, centerY);
		case Alignable.SOUTHEAST:
			return new Point(east, south);
		case Alignable.SOUTH:
			return new Point(centerX, south);
		case Alignable.SOUTHWEST:
			return new Point(west, south);
		case Alignable.WEST:
			return new Point(west, centerY);
		case Alignable.CENTER:
			return new Point(centerX, centerY);
		}
		return new Point(centerX, centerY);
	}
}
