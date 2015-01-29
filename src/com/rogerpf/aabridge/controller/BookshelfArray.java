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
		for (int i = 0; i <= 9; i++) {
			add(new Bookshelf(i)); // note 0 is never used BUT uses up the zero index
		}
	}

	public LinChapter pickRandomLinFile() {
		// ==============================================================================================

		double total_weight = 0;
		for (Bookshelf shelf : this) {
			total_weight += shelf.getLinWeighting();
		}

		double chosenShelf = total_weight * Math.random();

		double weight = 0;
		for (Bookshelf shelf : this) {
			weight += shelf.getLinWeighting();
			if (chosenShelf < weight) {
				return shelf.pickRandomLinFile();
			}
		}
		return null;
	}

	public void fillWithBooks() {
		// ==============================================================================================
		for (Bookshelf shelf : App.bookshelfArray) {
			if (shelf.ind == 0)
				continue; // skip zero to stop users from trying to create and use it
			shelf.fillWithBooks("");
		}

		// add cosmetic spaces to the last shelf in the list
		for (int i = 9; i >= 0; i--) {
			Bookshelf shelf = App.bookshelfArray.get(i);
			if (shelf.hasValidBooksForMenu()) {
				shelf.shelfDisplayName += "       ";
				break;
			}
		}

	}

}
