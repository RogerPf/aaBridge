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

public interface BoundableInterface {
	void setBounds(Component c, int x, int y, int w, int h);
}
