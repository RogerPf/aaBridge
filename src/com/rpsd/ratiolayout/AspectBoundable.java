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

public class AspectBoundable implements BoundableInterface {
	public void setBounds(Component c, int x, int y, int w, int h) {
		Dimension wantedSize = new Dimension(w, h);
		Dimension d = c.getPreferredSize();
		d = scale(d, wantedSize);
		c.setBounds(x + (w - d.width) / 2, y + (h - d.height) / 2, d.width, d.height);
	}

	/**
	 * scale returns a new dimension that has the same aspect ratio as the first
	 * dimension but has no part larger than the second dimension
	 */
	public Dimension scale(Dimension imageDimension, Dimension availableSize) {
		double ar = imageDimension.width / (imageDimension.height * 1.0);
		double availableAr = availableSize.width / (availableSize.height * 1.0);

		int newHeight = (int) (availableSize.width / ar);
		int newWidth = (int) (availableSize.height * ar);
		if (availableAr < ar)
			return new Dimension(availableSize.width, newHeight);
		return new Dimension(newWidth, availableSize.height);
	}

	public Dimension scaleWidth(Dimension d1, Dimension d2) {
		double scaleFactor = d2.width / (d1.width * 1.0);
		return scale(d1, scaleFactor);
	}

	private Dimension scale(Dimension d1, double scaleFactor) {
		return new Dimension((int) (d1.width * scaleFactor), (int) (d1.height * scaleFactor));
	}

	public Dimension scaleHeight(Dimension d1, Dimension d2) {
		double scaleFactor = d2.height / (d1.height * 1.0);
		return scale(d1, scaleFactor);
	}

}
