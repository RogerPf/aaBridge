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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.JMenuItem;

/**
 * List to hold supported ISO languages
 */

/**   
 */
public class LanguageList extends ArrayList<LanguageList.LangEntry> {
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	LanguageList() {
		// =================================================
		;
	}

	void delete_if_exists(String new_iso_lang) {
		// =================================================
		for (LangEntry entry : this) {
			if (entry.iso_lang.contentEquals(new_iso_lang)) {
				this.remove(entry);
				break;
			}
		}
	}

	/*
	 *  Fills the list  -  MUST ONLY BE CALLED ONCE
	 */
	void fillFromLanglistBundle(String bundleRoot, String name_langlist_and_menu) {
		// =================================================

		String[] codes = directFileRead_LangCodes(bundleRoot, name_langlist_and_menu);

		String bName = bundleRoot + App.bundleSep + name_langlist_and_menu;

		ResourceBundle bundle_langlist;
		try {
			bundle_langlist = Utf8ResourceBundle.getBundle(bName);
			// System.out.println("Read langlist bundle: " + bName + Aaf.bundle_dot_ext);
		} catch (Exception e) {
			System.out.println("Failed to open langlist bundle: " + bName + Aaf.bundle_dot_ext);
			System.out.println(" " + e);
			return;
		}

//      We now read the Keys direrctly see start of function
//		String ks = ""; 
//		for (int i = 1; i < 10; i++) {
//			try {
//				ks += bundle_langlist.getString("languages_" + i) + " ";
//			} catch (Exception e) {
//				break;
//			}
//		}
//
//		String[] codes = ks.split("\\s+");

		add(new LangEntry("auto", "Automatic", true, false));
		add(new LangEntry("en_US", "English                  - US, UK, World  English", false, true));

		for (String key : codes) {
			try {
				validateAndInsertLine(key, bundle_langlist.getString(key), bName);
			} catch (Exception e) {
				System.out.println("Failed to read langlist bundle key: " + key + "  from " + bName + Aaf.bundle_dot_ext);
			}
		}

	}

	private String[] directFileRead_LangCodes(String bundleRoot, String name_langlist_and_menu) {
		// =================================================

		ArrayList<String> codes = new ArrayList<String>();

		try {
			URLClassLoader betterLoader = null;
			InputStream is;
			if ((App.runningInJar == false) /* type 'f' */) {
				String bName = App.thisAppBaseFolder + File.separator + bundleRoot + File.separator + name_langlist_and_menu + Aaf.bundle_dot_ext;
				// System.out.println("J1 Direct (f) read of bundle:  " + bName);
				is = new FileInputStream(bName);
			}
			else { /* type r' */
				betterLoader = Aaa.makeJarZipLoader(App.thisAppBaseJarIncPath);
				String res = bundleRoot + "/" + name_langlist_and_menu + Aaf.bundle_dot_ext;
				// System.out.println("J2 Direct (r) read of bundle: " + App.thisAppBaseJarIncPath +"     res:" + res );
				is = betterLoader.getResourceAsStream(res);
			}
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			BufferedReader br = new BufferedReader(isr);

			String s;
			int ind = 0; //
			while ((s = br.readLine()) != null) {

				if (s.startsWith(App.menu2_text)) {
					App.menu2_ind = ind + 2;
					continue;
				}

				if (s.length() < 8) {
					continue;
				}
				if (s.indexOf(" = ") != 5) {
					continue;
				}
				s = s.substring(0, 5);
				codes.add(s);
				ind++;
			}
			br.close();
			isr.close();
			is.close();
			if (betterLoader != null && !App.using_java_6) {
				betterLoader.close();
			}

		} catch (Exception e) {
			System.out.println("J4 - can't direct read the language menu  file " + name_langlist_and_menu);
		}

		return codes.toArray(new String[codes.size()]);
	}

	private void validateAndInsertLine(String key, String menuText, String bName) {
		// =================================================
		if (key.contentEquals("auto"))
			return;

		if (key.contentEquals("en_US"))
			return;

//		System.out.println("inserting: " + key + "   " + menuText + "   " + bName + Aaf.bundle_dot_ext);

		String key_good = "";

		if (key.length() == 5) {
			key_good = key.substring(0, 2).toLowerCase() + "_" + key.substring(3, 5).toUpperCase();
		}

		if (key.contentEquals(key_good) == false) {
			System.out.println("Langlist bundle Key: " + key + "NOT  ll_HH  format     from " + bName + Aaf.bundle_dot_ext);
			return;
		}

		delete_if_exists(key);

		add(new LangEntry(key, menuText, false /* not the automatic entry */, false /* entry is not fallback */));
	}

	public class LangEntry {
		// ---------------------------------- sub CLASS -------------------------------------
		public String iso_lang;
		public String menuText;
		public boolean automatic;
		public boolean fallback;
		public JMenuItem menuItem;

		/**
		 */
		public LangEntry(String iso_lang_v, String menuText_v, boolean automatic_v, boolean fallback_v) {
			// =================================================
			iso_lang = iso_lang_v;
			automatic = automatic_v;
			fallback = fallback_v;
			menuItem = null;

			setMenuText(menuText_v);
		}

		/**
		 */
		public void setMenuItem(JMenuItem menuItem_v) {
			// =================================================
			menuItem = menuItem_v;
		}

		/**
		 */
		public void setMenuText(String text) {
			// =================================================
			String parts[] = text.split("-");
			if (parts.length == 2) {
				String exmaple_long_string = "Neederlandais";
				int firstPartLen = Aaf.getMetricsLength(new JMenuItem(), exmaple_long_string) + App.menuTabExtra;
				menuText = Aaf.spacedOut(new JMenuItem(), parts[0].trim(), firstPartLen) + "-    " + parts[1];
			}
			else {
				menuText = text;
			}

		}
	}
}
