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

public class Rlay_0th__TheOpeningLead {

	static Card act(Gather g) {
		// Hand h = g.hand;
		// ****************************** Oth The Opening Lead (defender) ******************************
		Card card = null;

		// for now we just cheat
		card = Rlay_1st__Defender.act(g);

		return card;
	}
}
