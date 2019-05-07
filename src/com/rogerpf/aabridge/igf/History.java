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
package com.rogerpf.aabridge.igf;

import java.util.LinkedList;
import java.util.ListIterator;

import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.controller.Book.LinChapter;

/**   
 */
public class History extends LinkedList<History.HistEntry> {

	private static final long serialVersionUID = 1L;
	private int id_counter = 0;
	private ListIterator<HistEntry> iterr;

	boolean calledFrom_insideHistory = false;

	boolean at_beginning = true;

	HistEntry lastActive = null;

	public History() { /* Constructor */
		// ============================================================================
		iterr = this.listIterator();
	}

	public boolean isFwdPos() {
		// ============================================================================
		return iterr.hasNext();
	}

	public boolean isBackPos() {
		// ============================================================================
		return at_beginning == false;
	}

	public void backOne() {
		// ============================================================================

		if (at_beginning)
			return;

		if (iterr.hasPrevious() == false) {
			System.out.println("HIST - backOne -  has no previous when at the beginnig!");
			at_beginning = true;
			lastActive = iterr.next();
			return;
		}

		lastActive = iterr.previous();

		if (iterr.hasPrevious() == false) {
			System.out.println("HIST - backOne -  Was at the beginning but this was still called at_beginning");
			// now back at the beginning
			at_beginning = true;
			// and step forward so we are always positioned at the "add" point
			lastActive = iterr.next(); // will always be the same value as just read
			return;
		}

		// now we can do the REAL previous

		lastActive = iterr.previous();

		at_beginning = (iterr.hasPrevious() == false);

		lastActive = iterr.next(); // will always be the same value as just read

		LinChapter newChapter = lastActive.linChapter;

		LinChapter prevChapter = App.lastLoadedChapter;
		App.lastLoadedChapter = newChapter;

		if (prevChapter.book != newChapter.book) {
			App.book = newChapter.book;
			App.aaBookPanel.matchToAppBook();
			// App.aaBookPanel.showChapterAsSelected(0);
		}

		calledFrom_insideHistory = true;

		if (prevChapter != newChapter) { // they should NOW always be different
			newChapter.loadWithShow("");
		}

//		System.out.println("back: " + lastActive.stop_gi + "   " + newChapter.displayNoUscore);

		App.mg.setTheReadPoints(lastActive.stop_gi, false /* not used */);

		calledFrom_insideHistory = false;

		App.gbp.matchPanelsToDealState();
	}

	public void fwdOne() {
		// ============================================================================

		if (iterr.hasNext() == false)
			return; // should not have been called as we can't go forward

		at_beginning = false;

		lastActive = iterr.next();

		LinChapter newChapter = lastActive.linChapter;

		LinChapter prevChapter = App.lastLoadedChapter;

		calledFrom_insideHistory = true;

		App.lastLoadedChapter = newChapter;

		if (prevChapter.book != newChapter.book) {
			App.book = newChapter.book;
			App.aaBookPanel.matchToAppBook();
		}

		if (prevChapter != newChapter) { // they should NOW always be different
			newChapter.loadWithShow("");
		}

//		System.out.println("fwd: " + lastActive.stop_gi + "   " + newChapter.displayNoUscore);

		App.mg.setTheReadPoints(lastActive.stop_gi, false /* not used */);

		calledFrom_insideHistory = false;

		App.gbp.matchPanelsToDealState();
	}

	public void fwdEnd() {
		// ============================================================================

		if (iterr.hasNext() == false)
			return; // should not have been called as we can't go forward

		at_beginning = false;

		while (iterr.hasNext()) {
			lastActive = iterr.next();
		}

		LinChapter newChapter = lastActive.linChapter;

		LinChapter prevChapter = App.lastLoadedChapter;

		calledFrom_insideHistory = true;

		App.lastLoadedChapter = newChapter;

		if (prevChapter.book != newChapter.book) {
			App.book = newChapter.book;
			App.aaBookPanel.matchToAppBook();
		}

		if (prevChapter != newChapter) { // they should NOW always be different
			newChapter.loadWithShow("");
		}

//		System.out.println("fwd: " + lastActive.stop_gi + "   " + newChapter.displayNoUscore);

		App.mg.setTheReadPoints(lastActive.stop_gi, false /* not used */);

		calledFrom_insideHistory = false;

		App.gbp.matchPanelsToDealState();

	}

	public void histRecordChange(String type) {
		// ============================================================================

		if (calledFrom_insideHistory)
			return;

		// @formatter:off
		if (   (lastActive != null) 
			&& (lastActive.linChapter == App.lastLoadedChapter)
			) {
			// if we are in the same chapter we just overwrite the position
			lastActive.stop_gi = App.mg.stop_gi;
	
//			System.out.println("updt: " + lastActive.stop_gi + "   " + lastActive.linChapter.displayNoUscore);
			return; // no change
		}
		// @formatter:on

		while (iterr.hasNext()) {
			iterr.next();
			iterr.remove();
		}

//		if (lastActive != null) {
//			System.out.println("");
//			System.out.println("prev: " + lastActive.stop_gi + "   " + lastActive.linChapter.displayNoUscore);
//		}
//		else {
//			System.out.println("prev: lastActive is null");
//		}
//
//		System.out.println("Add : " + App.mg.stop_gi + "   " + App.lastLoadedChapter.displayNoUscore);

		at_beginning = (iterr.hasPrevious() == false);

		iterr.add(lastActive = new HistEntry(type));
	}

	public class HistEntry {
		// ---------------------------------- CLASS -------------------------------------

		int id = 0;
		String type = "";

		int stop_gi;

		LinChapter linChapter;

		public HistEntry(String type_v) { /* Constructor */
			// ============================================================================
			type = type_v;
			stop_gi = App.mg.stop_gi;
			linChapter = App.lastLoadedChapter;
			id = id_counter++;
		}
	}

}
