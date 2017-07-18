/******************'*************************************************************
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
import java.util.ResourceBundle;

import javax.swing.JMenuItem;

/**
 * Last to hold supported Iso languages
 */

/**   
 */
public class LangdeckList extends ArrayList<LangdeckList.LangEntry> {
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	private static int active_deck_ind = 0;

	LangdeckList() {
	}

	void delete_if_exists(String new_iso_lang) {
		for (LangEntry entry : this) {
			if (entry.iso_lang.contentEquals(new_iso_lang)) {
				this.remove(entry);
				break;
			}
		}
	}

	public static boolean isDeckOverridden() {
		return active_deck_ind != 0;
	}

	/*
	 *  Fills the list  -  MUST ONLY BE CALLED ONCE
	 */
	void fillFromDecklistBundle(String bName) {
		// =================================================

		ResourceBundle bundle_decklist;

		String s = Aaf.gT("game.akqj");
		s = s.startsWith(Aaf.aa__dot_for_test) ? s.substring(1) : s;

		add(new LangEntry("default", s, "↑ ↑ ↑ ↑       " + Aaf.gT("menuLang.altDeckChoice")));
		add(new LangEntry("en_US", "AKQJ", "A K Q J   -   English"));

		try {
			bundle_decklist = Utf8ResourceBundle.getBundle(bName);
//			System.out.println("Read decklist bundle: " + bName + Aaf.bundle_dot_ext);
		} catch (Exception e) {
			System.out.println("Failed to read decklist bundle: " + bName + Aaf.bundle_dot_ext);
			return;
		}

		String[] keys;

		try {
			keys = bundle_decklist.getString("languages").split("\\s+");
		} catch (Exception e) {
			System.out.println("Failed to read decklist bundle / language list: " + bName + Aaf.bundle_dot_ext);
			return;
		}

		for (String key : keys) {
			try {

				validateAndInsertLine(key, bundle_decklist.getString(key), bName);

			} catch (Exception e) {
				System.out.println("Failed to read decklist bundle key : " + key + "  from " + bName + Aaf.bundle_dot_ext);
			}
		}

		int i = -1;
		for (LangEntry entry : this) {
			i++;
			if (entry.iso_lang.contentEquals(Aaf.iso_deck_lang)) {
				active_deck_ind = i;
				return;
			}
		}

		Aaf.iso_deck_lang = "default";
		active_deck_ind = 0;

	}

	private void validateAndInsertLine(String key, String other_v, String bName) {
		// =================================================
		if (key.contentEquals("default"))
			return;

		if (key.contentEquals("en_US"))
			return;

		String key_good = "";

		if (key.length() == 5) {
			key_good = key.substring(0, 2).toLowerCase() + "_" + key.substring(3, 5).toUpperCase();
		}

		if (key.contentEquals(key_good) == false) {
			System.out.println("Langlist bundle Key: " + key + "NOT  ll_HH  format     from " + bName + Aaf.bundle_dot_ext);
			return;
		}

		String[] params = other_v.split("\\s*,\\s*");

		if (params.length != 2)
			return;

		delete_if_exists(key);

		add(new LangEntry(key, params[0], params[1]));
	}

	public char cardLetterLangConvert(char c) {
		// =================================================

		int i;
		switch (c) {
		// @formatter:off
			case 'A': i = 0; break;
			case 'K': i = 1; break;
			case 'Q': i = 2; break;
			case 'J': i = 3; break;	
			case 'T': return 't';	// 't' (lowercase) is shown as a '10' by the font
			default: return c;
		// @formatter:on
		}
		return get(active_deck_ind).akqj.charAt(i);
	}

	public String cardLetterLangConvForQuestions(char c) {
		// =================================================

		int i;
		switch (c) {
		// @formatter:off
			case 'A': i = 0; break;
			case 'K': i = 1; break;
			case 'Q': i = 2; break;
			case 'J': i = 3; break;	
			case 'T': return "10";	// 't' (lowercase) is shown as a '10' by the font
			default: return c + "";
		// @formatter:on
		}
		return get(active_deck_ind).akqj.charAt(i) + "";
	}

	public class LangEntry {
		// ---------------------------------- sub CLASS -------------------------------------
		public String iso_lang;
		public String akqj;
		public String menuText;

		public JMenuItem menuItem;

		/**
		 */
		public LangEntry(String iso_lang_v, String akqj_v, String menuText_v) {
			// =================================================

			iso_lang = iso_lang_v;
			akqj = akqj_v + "????";
			menuText = menuText_v;

			menuItem = null;
		}

		public void setMenuItem(JMenuItem menuItem_v) {
			// =================================================
			menuItem = menuItem_v;
		}

	}

}
