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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**   
 */
public class MruCollection extends HashMap<String, MruCollection.MruChapter> {
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	final static String mruPrefsNode = "com.rogerpf.aabridge/";
	final static String ver = "ver115";
	final static String magic = "__����退��__"; // do not change
	final static String none_left = "none_left";

	boolean loaded = false;

	String prev__key = "";

	MruCollection() {
		// ==============================================================================================
		// empty at the moment
	}

	public static void delete_whole_mru() {
		// ==============================================================================================
		Preferences mruPrefs_old = Preferences.userRoot().node(mruPrefsNode + App.mruNodeSubNode);
		try {
			mruPrefs_old.removeNode(); // deletes all
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	public MruChapter createMatchingMru(char type, String src, String filenamePlus, String displayNoUscore, int prev_hist_pgNumb) {
		// ==============================================================================================

		// System.out.println(type + "    <" + src + ">    <" + filenamePlus + ">    <" + displayNoUscore + ">   hist_pgNumb: " + hist_pgNumb);

		// @formatter:off
		if (   ((type == 'r') &&  App.runningInJar && src.endsWith(App.thisAppBaseJar))
		//	|| ((type == 'f') && !App.runningInJar && src.startsWith(App.thisAppBaseFolder))
		   )
		{
			src = "BUILT-IN";
		}
		// @formatter:on

		MruChapter chap_new = new MruChapter(type, src, filenamePlus, displayNoUscore);

		String key = chap_new.getMruKey();

		MruChapter mru = get(key);

		if (mru == null) {
			put(key, chap_new);
			mru = chap_new;
		}

		mru.updateTimestamp();

		update_prev_hist_pgNumb(prev_hist_pgNumb);

		prev__key = key;

		return mru;
	}

	public void update_prev_hist_pgNumb(int hist_pgNumb) {
		// ==============================================================================================
		if (hist_pgNumb > 1 && (prev__key.isEmpty() == false)) {
			MruChapter prev__chap = get(prev__key);
			if (prev__chap != null)
				prev__chap.hist_pgNumb = hist_pgNumb;
		}
	}

	void loadCollection() {
		// ==============================================================================================

		try {

			Preferences mruPrefs = Preferences.userRoot().node(mruPrefsNode + App.mruNodeSubNode);

			loaded = true;

			for (int i = 1; true; i++) {
				String ps = mruPrefs.get("" + i, none_left);
				if (ps.contentEquals(none_left))
					break;
				// System.out.println(ps);
				String tok[] = ps.split(magic);
				// System.out.println(tok);

				int tok_len = tok.length;
				if (tok_len < 10)
					continue;

				MruChapter mru = new MruChapter();

				// tok[0] should be null
				// tok[1] should be ver115
				mru.type = tok[2].charAt(0);
				mru.timeStamp = Long.parseLong(tok[3]);
				mru.src = tok[4];
				mru.filenamePlus = tok[5];
				mru.displayNoUscore = tok[6];
				mru.mruKey = tok[7];
				mru.hist_pgNumb = Aaa.extractPositiveIntOrZero(tok[8]);
				int mark_count = Aaa.extractPositiveIntOrZero(tok[9]);
				if (tok_len < 10 + mark_count)
					continue;

				for (int j = 0; j < mark_count; j++) {
					mru.marks.add(Aaa.extractPositiveIntOrZero(tok[10 + j]));
				}
				put(mru.mruKey, mru);

				// System.out.println("" + i + " load " + mru.toString());
			}

		} catch (Exception e) {
		}
	}

	public void saveCollection() {
		// ==============================================================================================

		if (loaded == false)
			return;

		Preferences mruPrefs_old = Preferences.userRoot().node(mruPrefsNode + App.mruNodeSubNode);
		try {
			mruPrefs_old.removeNode(); // deletes all
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}

		try {
			mruPrefs_old.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}

		Preferences mruPrefs = Preferences.userRoot().node(mruPrefsNode + App.mruNodeSubNode);

		int indexer = 0;
		int hist_count = 0;
		int mark_count = 0;

		List<MruChapter> mruByTimeStamp = sortByTimestamp();

		for (MruChapter mru : mruByTimeStamp) { // yes we need them by time stamp

			boolean hist = false;
			boolean mark = false;
			boolean save = false;

			if (mru.hist_pgNumb > 1) {
				if (hist_count >= App.hist_max_display) {
					mru.hist_pgNumb = 0;
				}
				else {
					hist_count++;
					hist = true;
				}
			}

			if (mru.marks.size() > 0) {
				if (mark_count >= App.mark_max_display) {
					mru.marks.clear();
				}
				else {
					mark_count++;
					mark = true;
				}
			}

			if (hist || mark) {
				save = true;
				indexer++;
			}

			// System.out.println("" + indexer + ((save) ? "  Saved " : "        ") + mru.toString());

			if (save) {
				mruPrefs.put("" + indexer, mru.asPrefsString());
			}
		}

		try {
			mruPrefs.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}

		// System.out.println("mruCollection Saved");
	}

	public void clearSingleHistory(String key) {
		// ==============================================================================================
		MruChapter mru = get(key);
		if (mru == null)
			return;
		mru.hist_pgNumb = 0;
	}

//	public void clearAllHistories() {
//		// ==============================================================================================
//		Set<?> set = entrySet();
//		Iterator<?> iterator = set.iterator();
//		while (iterator.hasNext()) {
//			@SuppressWarnings("rawtypes")
//			MruChapter mru = (MruChapter) ((Map.Entry) iterator.next()).getValue();
//
//			mru.hist_pgNumb = 0;
//		}
//	}

	public void clearChapterMarks(String key) {
		// ==============================================================================================
		MruChapter mru = get(key);
		if (mru == null)
			return;
		mru.marks.clear();
	}

	public void clearAllMarksInCollection() {
		// ==============================================================================================
		Set<?> set = entrySet();
		Iterator<?> iterator = set.iterator();
		while (iterator.hasNext()) {
			@SuppressWarnings("rawtypes")
			MruChapter mru = (MruChapter) ((Map.Entry) iterator.next()).getValue();

			mru.marks.clear();
		}
	}

	public int getChapterMarksCount(String mruKey) {
		// ==============================================================================================

		MruChapter mru = get(mruKey);

		if (mru != null) {
			return mru.marks.size();
		}
		return 0;
	}

	static final String spline = "                                                                                                        ";

	public static String generateMruKey(char type, String src, String filenamePlus, String displayNoUscore) {
		// ==============================================================================================

		// remove unwanted extra file separators (which should not be there)
		while (src.endsWith("\\\\") || src.endsWith("//")) {
			src = src.substring(0, src.length() - 1);
		}

		if (((type == 'r') && App.runningInJar && src.endsWith(App.thisAppBaseJar))
		// || ((type == 'f') && !App.runningInJar && src.startsWith(App.thisAppBaseFolder))
		) {
			src = "BUILT-IN";
		}

		String mruKey = displayNoUscore.trim();

		int spaces = 0;
		int lower = 0;
		int upper = 0;
		int digit = 0;
		int other = 0;

		double w_space = 1.0;
		double w_lower = 1.95;
		double w_upper = 2.4;
		double w_digit = 2.0;
		double w_other = 1.1;

		int alignAt = 55;

		for (int i = 0; i < mruKey.length(); i++) {
			char c = mruKey.charAt(i);
			if (c == ' ' || c == 'i') // 'i' counts as the width of a space
				spaces++;
			else if (c == 'm' || c == 'w') // 'i' counts as the width of a upper
				upper++;
			else if (Character.isLowerCase(c))
				lower++;
			else if (Character.isUpperCase(c))
				upper++;
			else if (Character.isDigit(c))
				digit++;
			else
				other++;
		}

		double adjustedLength = (spaces * w_space) + (lower * w_lower) + (upper * w_upper) + (digit * w_digit) + (other * w_other);

		if (adjustedLength < alignAt) {
			mruKey += spline.substring(0, (int) (alignAt - adjustedLength));
		}

		String third_lump = filenamePlus;

		String sep = /*(type == 'f') ? File.separator :*/"/"; // yes always forward slash

		int ind = filenamePlus.lastIndexOf(sep, 9999);
		if (ind > 0) {
			third_lump = filenamePlus.substring(0, ind);
		}

		mruKey += "     " + src + "     " + third_lump;

		return mruKey;
	}

	public class MruChapter {
		// ---------------------------------- CLASS -------------------------------------

		public char type;
		public String src;
		public String filenamePlus;
		public String displayNoUscore;
		private String mruKey;
		public int hist_pgNumb = 0;
		public ArrayList<Integer> marks = new ArrayList<Integer>();

		public long timeStamp;

		public String asPrefsString() {
			// ==============================================================================================
			String ps = magic;

			ps += ver + magic + type + magic + timeStamp + magic + src + magic + filenamePlus + magic;
			ps += displayNoUscore + magic + mruKey + magic + hist_pgNumb + magic;

			ps += ("" + marks.size()) + magic;
			for (Integer v : marks) {
				ps += ("" + v) + magic;
			}

			return ps;
		}

		File fileCh = null;

		public String toString() {
			// ==============================================================================================
			// @formatter:off
			return "" + type + "  " + ((hist_pgNumb > 0) ? "h" : "-") + ((marks.size() > 0) ? "m" : "-") + "  "
					  + timeStamp  + " "
					  + "    <" + src + ">    <" + filenamePlus + ">    <" + displayNoUscore + ">   hist_pgNumb: " + hist_pgNumb
					  + "  " + marks;
			// @formatter:on

		}

		public MruChapter() { // Constructor
			// ==============================================================================================
		}

		public MruChapter(char type_IN, String src_IN, String filenamePlus_IN, String displayNoUscore_IN) { // Constructor
			// ==============================================================================================
			type = type_IN;
			src = src_IN;
			filenamePlus = filenamePlus_IN;
			displayNoUscore = displayNoUscore_IN;
			timeStamp = System.currentTimeMillis();

			// remove unwanted extra file separators (which should not be there)
			while (src.endsWith("\\\\") || src.endsWith("//")) {
				src = src.substring(0, src.length() - 1);
			}

			mruKey = generateMruKey(type, src, filenamePlus, displayNoUscore);
		}

		public String getMruKey() {
			return mruKey;
		}

		public void updateTimestamp() {
			timeStamp = System.currentTimeMillis();
		}

		public void toggleChapterMark(Integer pg_numb) {
			if (marks.contains(pg_numb)) {
				marks.remove(pg_numb);
			}
			else {
				marks.add(pg_numb);
			}
			Collections.sort(marks);
			App.aaHomeBtnPanel.fill_chapterMarkerMenu();

			// System.out.println("marks: " + marks);
		}

		public int getFirstMark() {
			if (marks.isEmpty())
				return 1;
			return marks.get((int) 0);
		}

	}

	public List<MruCollection.MruChapter> sortByTimestamp() {
		// ==============================================================================================
		List<MruCollection.MruChapter> mruByTimeStamp = new ArrayList<MruCollection.MruChapter>(App.mruCollection.values());

		Collections.sort(mruByTimeStamp, new Comparator<MruCollection.MruChapter>() {
			public int compare(MruCollection.MruChapter ch1, MruCollection.MruChapter ch2) {
				return (int) (ch2.timeStamp - ch1.timeStamp);
			}
		});

		return mruByTimeStamp;
	}

//
//  not used - user does it when needed
//
//	public List<MruCollection.MruChapter> sortByDisplayText() {
//		// ==============================================================================================
//		List<MruCollection.MruChapter> mruByDisplayText = new ArrayList<MruCollection.MruChapter>(App.mruCollection.values());
//
//		Collections.sort(mruByDisplayText, new Comparator<MruCollection.MruChapter>() {
//			public int compare(MruCollection.MruChapter ch1, MruCollection.MruChapter ch2) {
//				return (int) (ch1.displayNoUscore.compareToIgnoreCase(ch2.displayNoUscore));
//			}
//		});
//
//		return mruByDisplayText;
//	}

}
