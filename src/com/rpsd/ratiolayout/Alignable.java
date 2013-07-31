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

public interface Alignable {
	int NORTHWEST = 1;
	int North = 2;
	int NORTHEAST = 3;
	int East = 4;
	int SOUTHEAST = 5;
	int South = 6;
	int SOUTHWEST = 7;
	int West = 8;
	int CENTER = 9;

	public int getAlignment();

	public void setAlignment(int a);
}
