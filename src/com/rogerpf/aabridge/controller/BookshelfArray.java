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
package com.rogerpf.aabridge.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.rogerpf.aabridge.controller.Book.LinChapter;

/**   
 */
public class BookshelfArray extends ArrayList<Bookshelf> {
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	/**   
	 */
	public BookshelfArray() {
		// ==============================================================================================

		add(new Bookshelf("")); // always added even if empty

		for (char c = 'A'; c <= 'Z'; c++) {
			Bookshelf shelf = new Bookshelf("" + c);
			if (!shelf.isEmpty()) {
				add(shelf);
			}
		}

		// sort by read insort order
		Collections.sort(this, new Comparator<Bookshelf>() {
			public int compare(Bookshelf bs1, Bookshelf bs2) {
				if (bs1.sort_order == bs2.sort_order) {
					return (bs1.shelfname.compareTo(bs2.shelfname));
				}
				return ((bs1.sort_order < bs2.sort_order) ? -1 : 1);
			}
		});

	}

	public LinChapter pickRandomLinFile() {
		// ==============================================================================================

		double total_weight = 0;
		for (Bookshelf shelf : this) {
			total_weight += Math.sqrt(shelf.randAdjustedSize());
		}

		double chosenShelf = total_weight * Math.random();

		double weight = 0;
		for (Bookshelf shelf : this) {
			weight += Math.sqrt(shelf.randAdjustedSize());
			if (chosenShelf <= weight) {
//				System.out.print( shelf.shelfname + " of " + size());
				return shelf.pickRandomLinFile();
			}
		}
		return null;
	}

}
