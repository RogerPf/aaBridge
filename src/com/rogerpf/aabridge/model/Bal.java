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
package com.rogerpf.aabridge.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * Bal BidArrayList
 */
public class Bal extends ArrayList<Bid> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6958033952065344529L;

	public Bal() { /* Constructor */
		super();
	}

	public Bid getLast() {
		if (size() == 0)
			return null;
		else
			return get(size() - 1);
	}

	public Bid removeLast() {
		if (size() == 0)
			return null;
		else
			return remove(size() - 1);
	}
}
