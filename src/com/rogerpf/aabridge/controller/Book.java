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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.regex.Pattern;

import com.version.VersionAndBuilt;

/**   
 */
public class Book extends ArrayList<Book.LinChapter> {
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	public String bookJarName = "";
	public String bookJarExtra = "";
	public String bookFolderName = "";
	public String displayTitle = "";
	public String bookPath = "";
	public String originText = "";
	public boolean dividerBefore = false;

	public int lastChapterIndexLoaded = -1;

	public boolean displayTitle_is_langSpecific = false;

	int frontNumber = 0;

	public class LinChapter {
		// ---------------------------------- CLASS -------------------------------------
		public Book book;
		public char type;
		String parentOrJar;
		public String filenamePlus;
		public String filename;
		public String displayNoExt;
		public String displayNoNumb;
		public String displayNoUscore;
		public int index;
		public boolean langSpecific;

		public String sortableFilename() {
			// =============================================================
			String basename = displayNoExt.replace('_', '0').replace(' ', '0');
			// still has the number still at front
			return basename;

//			int f = basename.indexOf(' ');
//			if (f < 0)
//				return filename;
//			String s = basename.substring(0, f+1);
//			if (basename.length() <= f+2)
//				return basename;
//			String r = basename.substring(f+1);
//			r = r.replace(' ', '0');
//			return s + r;
		}

		public String getName() {
			// =============================================================
			return displayNoUscore;
		}

		public Boolean isEditable() {
			// =============================================================
			// System.out.println(filename + "  " + type + "  " + "  ");
			return type == 'f';
		}

		public LinChapter(Book book, char type, String base, String filenameIn) { // Constructor
			// ==============================================================================================
			this.book = book;
			this.type = type;
			this.parentOrJar = base;
			filenamePlus = filenameIn;
			String s = filenameIn;

			langSpecific = false;

			int sl = s.lastIndexOf('/'); // always "/" never sep
			if (sl > -1) {
				s = s.substring(sl + 1);
			}
			filename = s;

//			System.out.println("const chap: " + type + "   " + filename);

			assert (s.toLowerCase().endsWith(".lin") || s.toLowerCase().endsWith(".pbn"));
			s = s.substring(0, s.length() - 4);

			String langDisplay = "";

			int ind = s.indexOf("__" + Aaf.iso_lang_active + "__");
			if (ind > -1) {
				langSpecific = true;
				langDisplay = (s.substring(ind + 8)).replace('_', ' ').trim();
				s = s.substring(0, ind);
			}

			displayNoExt = s = s.trim();

			int i = 0;
			for (; i < s.length(); i++) {
				char c = s.charAt(i);
				if (('0' <= c && c <= '9'))
					continue;
				s = s.substring(i);
				break;
			}
			displayNoNumb = s = s.trim();
			if (displayNoNumb.endsWith("regen")) {
				s = displayNoNumb = displayNoNumb.substring(0, displayNoNumb.length() - 5);
			}
			s = s.replace('_', ' ');
			displayNoUscore = s = s.trim();

			if (!langDisplay.isEmpty())
				displayNoUscore = langDisplay;

//			System.out.println("add chap: " + filename);

			add(this);
		}

		private long timeStamp = 0;
		boolean report_false = false;

		File fileCh = null;

		/**
		 *  must be called reqularly and must report its time interval ms
		*/
		public boolean hasLinFileChanged(int interval) {
			// =============================================================

			if (report_false)
				return false;

			if (type != 'f')
				return false; // only real files can ever changed

			if (fileCh == null) {
				fileCh = new File(book.bookFolderName + this.filename);
				if (fileCh.canRead() == false) {
					report_false = true;
				}
				timeStamp = fileCh.lastModified();
				return false;
			}

			long prev = timeStamp;

			timeStamp = fileCh.lastModified();

			if (timeStamp == prev) {
				return false;
			}

			return true;
		}

		/**
		*/
		public void setTitleBookStyle() {
			// =============================================================
			String s = "aaBridge  " + VersionAndBuilt.verAndBuildNo();

			s += "                                ";

			s += originText + "   /   ";

			if (!displayTitle.isEmpty()) {
				s += displayTitle + "   /   ";
			}

			s += displayNoUscore;

			App.frame.setTitle(s);
		}

		public boolean load() {
			// ==============================================================================================
			Boolean success = false;

//			App.handPanelNameAreaInfoNumbersShow = true;

			App.tutRotate = 0;
			App.flowOnlyCommandBar = false;
			App.hideCommandBar = false; // only set true by the lbx (distr Flash cards question special)
			App.hideTutNavigationBar = false; // only set true by the lbx (distr Flash cards question special)
//			if (App.devMode == false) {
//  			App.allTwister_reset();
//			}

			// the loaders use this to save history so we do it in advance
			LinChapter prev_load = App.lastLoadedChapter;
			App.lastLoadedChapter = this;

			if (type == 'r') {
				success = BridgeLoader.readLinOrPbnResourseIfExists(bookJarName, filenamePlus);
				App.debug_pathlastLinLoaded = "";
			}

			if (type == 'f') {
				success = BridgeLoader.readLinOrPbnFileIfExists(bookFolderName, filenamePlus, bookFolderName);
				App.debug_pathlastLinLoaded = book.bookFolderName + filename;
			}

			if (success) {
				lastChapterIndexLoaded = index;
				setTitleBookStyle();
				App.biddingVisibilityCheck();

//				App.history.histRecordChange( "chap_load");
			}
			else {
				// if the failed replace the chapter name
				App.lastLoadedChapter = prev_load;
			}

			return success;
		}

		public boolean loadWithShow(String replace) {
			// ==============================================================================================
			// System.out.println("loadWithShow " + replace);

			int prev_pg_numb = App.mg.get_best_pg_number_for_history();

			boolean loaded = load();

			if (loaded) {
				App.lastLoadedChapter = this;

				App.book = Book.this;
				if (replace.contentEquals("replaceBookPanel")) {
					App.aaBookPanel.matchToAppBook();
				}

				App.aaBookPanel.showChapterAsSelected(getName());

				if (App.isLin__VuGraphAndTwoTeams() && App.showRedVuGraphArrow) {
					App.gbo.showVuGraphHint();
				}

				App.biddingVisibilityCheck();

				String srcName = (type == 'r') ? bookJarName : bookFolderName;

				App.mg.mruChap = App.mruCollection.createMatchingMru(type, srcName, filenamePlus, displayNoUscore, prev_pg_numb);

				App.ddsScoreShow = false;
				App.tutRotate = 0;

				App.gbp.c1_1__tfdp.clearAllCardSuggestions(); // DIRTY nasty way to do things - its a bug fix - no excuse

				App.gbp.c1_1__tfdp.makeCardSuggestions();

				App.frame.rop.setSelectedIndex(App.RopTab_2_KibSeat);

				App.frame.rop.p2_KibSeat.showButtonStates();

				return loaded;
			}

			return false;
		}

		public boolean matchAndActionReleaseDate(String sFile) {
			// ==============================================================================================
			if (sFile.contentEquals(displayNoExt) == false)
				return false;

			return true;
		}

		public String generateMruKey() {
			// ==============================================================================================
			String srcName = (type == 'r') ? bookJarName : bookFolderName;

			return MruCollection.generateMruKey(type, srcName, filenamePlus, displayNoUscore);
		}
	}

	public Book() { // Constructor of an empty book
		// ==============================================================================================
	}

	public String longId() {
		// ==============================================================================================
		return bookJarName + " " + bookFolderName;
	}

	public Book(String basePathIn, File[] onlyThese) { // Constructor from an external source - drag drop
		// ==============================================================================================
		commonBookConstructor(basePathIn, "", onlyThese, "", basePathIn);
	}

	public Book(String basePathIn, String extraPath, String shelfname, String shelfDisplayName, boolean dividerBefore) {
		// ==============================================================================================
		this.dividerBefore = dividerBefore;
		commonBookConstructor(basePathIn, extraPath, null, shelfname, shelfDisplayName);
	}

	private final static String sep = File.separator;

	static String all_lin_files_in_this_book = "all_lin_files_in_this_book";

	public void commonBookConstructor(String basePathIn, String extraPath, File[] onlyThese, String shelfname, String originText) {
		// ==============================================================================================

		String basePath = basePathIn;

		this.originText = originText;

		if (extraPath.length() > 0) {
			frontNumber = Aaa.extractPositiveInt(extraPath);
//			if (frontNumber == 19) {
//				@SuppressWarnings("unused")
//				int z = 0;
//			}
		}

//		if (basePath.isEmpty()) {
//
//			assert (false); // I don't think we EVER have an empty book path now (since having bookshelves)
//
//			URL locationMethodUrl = AaBridge.class.getProtectionDomain().getCodeSource().getLocation();
//			File locMethodFile = null;
//			try {
//				locMethodFile = new File(locationMethodUrl.toURI());
//			} catch (Exception e1) {
//				System.out.println("book (cons) - locationMethodUrl FAILED  help! - " + e1.getMessage());
//				return;
//			}
//
//			if (basePath.toLowerCase().endsWith(".jar") == false)
//				basePath = locMethodFile.getPath() + sep + shelfname;
//		}

		/** for basic testing only
		 */
		if (App.debug_using_ghost_jar && basePath.startsWith(App.thisAppBaseFolder)) {
			basePath = App.thisAppBaseJarIncPath;
		}

		if (basePath.toLowerCase().endsWith(".jar") || basePath.toLowerCase().endsWith(".zip") || basePath.toLowerCase().endsWith(".linzip")) {
			/** 
			 * clearly we are in a jar file
			 */
			File jarFile = new File(basePath);
			if (!jarFile.exists()) {
				System.out.println("Book (cons) - Given a jar/zip file name that does not exist " + basePath);
				return;
			}

			bookJarName = jarFile.getPath();
			if (basePath.toLowerCase().endsWith(".jar"))
				bookJarExtra = shelfname + "/" + extraPath;

			// We are running in a .jar on either windows, mac or linux or ...

			Pattern pattern = Pattern.compile(bookJarExtra + ".*[.lin|book_title|.reldate]");

			ArrayList<String> ret = (ArrayList<String>) ResourceList.getResourcesFromJarFile(jarFile, pattern);

			boolean process_as_normal = true;

			// parse for whole book reldate
			if (App.observeReleaseDates) {
				for (String sFile : ret) {
					if (sFile.endsWith(".reldate") == false)
						continue;
					if (sFile.contains(all_lin_files_in_this_book) == false)
						continue;
					if (isValidReldateAndInFuture(sFile) == false)
						continue;

					process_as_normal = false;
					break;
				}
			}

			// parse each of the files really for the .lin files
			if (process_as_normal) {

				for (String sFile : ret) {
					String low = sFile.toLowerCase();

					if (low.contains("__macosx"))
						continue;

					if (low.endsWith("book_title"))
						setDisplayTitle(sFile, "/", sFile, 'r');

					if (low.endsWith(".lin") || low.endsWith(".pbn")) {

						if (App.showAllLangLin == false) {

							boolean ans[] = new boolean[2];
							String searchName = Aaf.has_iso_lang(sFile, ans, "/");
							boolean new_specificLang = ans[0];
							boolean new_activeLang = ans[1];

							if (new_specificLang && !new_activeLang)
								continue; // skip this one not a language we are currently interesting in

							LinChapter existing = this.getChapterByDisplayNamePart(searchName);
							if (existing != null) {
								if (new_specificLang && existing.langSpecific == false) {
									this.remove(existing.index);
								}
								else {
									System.out.println("Clash - lin file name clash - may have language set:  " + sFile + "       " + existing.filenamePlus);
								}
							}
						}
						new LinChapter(this, 'r', bookJarName, sFile);
					}
				}

				for (String sFile : ret) {
					String low = sFile.toLowerCase();

					if (App.showAllLangLin)
						continue; // this 2nd parse causes double entries when showing all

					if (low.contains("__macosx"))
						continue;

					if (low.endsWith("book_title"))
						setDisplayTitle(sFile, "/", sFile, 'r');

					if (App.showAllLangLin == false && (low.endsWith(".lin_title") || low.endsWith(".pbn_title"))) {

						boolean ans[] = new boolean[2];
						String searchName = Aaf.has_iso_lang(sFile, ans, "/");
						boolean new_specificLang = ans[0];
						boolean new_activeLang = ans[1];

						if (new_specificLang && !new_activeLang)
							continue; // skip this one not a language we are currently interesting in

						LinChapter existing = this.getChapterByDisplayNamePart(searchName);
						if (existing == null)
							continue;

						String s = Aaf.readfirstlineOfFileOrRes(sFile, basePathIn, 'r');
						if (s.trim().length() > 1) {
							existing.displayNoUscore = s;
						}
					}
				}
			}

			// parse for specific lin file rel dates
			if (App.observeReleaseDates) {
				for (String sFile : ret) {
					if (isValidReldateAndInFuture(sFile) == false)
						continue;
					String filenameNoLin = extract_linfilename(sFile);
					for (LinChapter ch : this) {
						if (filenameNoLin.contentEquals(ch.displayNoExt)) {
							remove(ch);
							break;
						}
					}
				}
			}

			if (displayTitle.isEmpty())
				setDisplayTitle(bookJarExtra, "/", bookJarExtra, 'r');

			@SuppressWarnings("unused")
			int z = 0;
		}

		else { // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< r above - f below <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
			/**
			 *  We are in eclipse getting the local book pseudo resource 
			 *  OR someone has dragged and dropped a folder on to us
			 *  either way we want the lins and title
			 */
			assert (basePath.length() > 0);

			if (basePath.endsWith(sep) == false)
				basePath += sep;

			bookFolderName = basePath + extraPath + sep;

			// Get the list of all the potential files
			File[] files = null;
			try {
				files = new File(bookFolderName).listFiles();
				if (files != null) {

					boolean load_lins_as_normal = true;

					// parse for whole book reldate
//					if (App.observeReleaseDates) {
//						for (File file : files) {
//							String sFile = file.getName();
//							if (sFile.endsWith(".reldate") == false)
//								continue;
//							if (sFile.contains(all_lin_files_in_this_book) == false)
//								continue;
//							if (isValidReldateAndInFuture(sFile) == false)
//								continue;
//
//							load_lins_as_normal = false;
//							break;
//						}
//					}

					// parse each of the files really for the .lin files
					if (load_lins_as_normal) {
						for (File file : files) {

							if (file.getPath().toLowerCase().contains("__macosx"))
								continue;

							String sFile = file.getName();

							/** filter the files by  the onlyThese  list
							 */
							if (onlyThese != null) {
								boolean found = false;
								for (File f : onlyThese) {
									if (sFile.contentEquals(f.getName())) {
										found = true;
										break;
									}
								}
								if (!found)
									continue;
							}

							String low = sFile.toLowerCase();

							if (low.endsWith(".lin") || low.endsWith(".pbn")) {

								if (App.showAllLangLin == false) {

									boolean ans[] = new boolean[2];
									String searchName = Aaf.has_iso_lang(sFile, ans, sep);
									boolean new_specificLang = ans[0];
									boolean new_activeLang = ans[1];

									if (new_specificLang && !new_activeLang)
										continue; // skip this one not a language we are currently interesting in

									LinChapter existing = this.getChapterByDisplayNamePart(searchName);
									if (existing != null) {
										if (new_specificLang && existing.langSpecific == false) {
											this.remove(existing.index);
										}
//										else {
//											System.out.println("Clash - lin file name clash - may have language set:  " + sFile + "  " + existing.filenamePlus);
//										}
									}
									// System.out.println("new Lin Chapter: " + sFile);
								}
								new LinChapter(this, 'f', "", sFile);
							}
						}

						/* 
						 * now we do a second pass for any title overwrites
						 */
						for (File file : files) {

							if (App.showAllLangLin)
								continue; // this 2nd pass causes double entries when showing all

							if (file.getPath().toLowerCase().contains("__macosx"))
								continue;

							String sFile = file.getName();

							/** filter the files by  the onlyThese  list
							 */
							if (onlyThese != null) {
								boolean found = false;
								for (File f : onlyThese) {
									if (sFile.contentEquals(f.getName())) {
										found = true;
										break;
									}
								}
								if (!found)
									continue;
							}

							String low = sFile.toLowerCase();

							if (low.endsWith("book_title"))
								setDisplayTitle(sFile, sep, file.getPath(), 'f');

							if (App.showAllLangLin == false && (low.endsWith(".lin_title") || low.endsWith(".pbn_title"))) {

								boolean ans[] = new boolean[2];
								String searchName = Aaf.has_iso_lang(sFile, ans, sep);
								boolean new_specificLang = ans[0];
								boolean new_activeLang = ans[1];

								if (new_specificLang && !new_activeLang)
									continue; // skip this one not a language we are currently interesting in

								LinChapter existing = this.getChapterByDisplayNamePart(searchName);
								if (existing == null)
									continue;

								String s = Aaf.readfirstlineOfFileOrRes(sFile, file.getPath(), 'f');
								if (s.trim().length() > 1) {
									existing.displayNoUscore = s;
								}
							}
						}
					}

//					if (App.observeReleaseDates) {
//						for (File file : files) {
//							String sFile = file.getName();
//							if (isValidReldateAndInFuture(sFile) == false)
//								continue;
//							String filenameNoLin = extract_linfilename(sFile);
//							for (LinChapter ch : this) {
//								if (filenameNoLin.contentEquals(ch.displayNoExt)) {
//									remove(ch);
//									break;
//								}
//							}
//						}
//					}
				}

			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			if (displayTitle.isEmpty()) {
				setDisplayTitle(bookFolderName, sep, bookFolderName, 'f');
			}
		}

//		System.out.println(basePathIn + "   " + shelfname + "   " + originText + "   " + this.bookFolderName );

//		if (this.bookFolderName == "79  System  -  Self Practice") {
//			System.out.println("Folder:    " + this.bookFolderName);
//		}

		Collections.sort(this, new Comparator<LinChapter>() {
			public int compare(LinChapter ch1, LinChapter ch2) {
				return (ch1.sortableFilename().compareTo(ch2.sortableFilename()));
			}
		});

		for (int i = 0; i < size(); i++) {
			get(i).index = i;
		}
	}

	public String getMenuKey(String basePath) {
		// =============================================================
		return basePath + "//" + bookJarExtra + "//" + displayTitle;
	}

	private String extract_linfilename(String sFile) {
		// ==============================================================================================
		int p = sFile.lastIndexOf('/');
		if (p != -1) {
			sFile = sFile.substring(p + 1); // in zips and jars we get the full pusdo path
		}

		int len = sFile.length();
		if (len < 21)
			return "";
		return sFile.substring(0, len - 20);
	}

	static DateFormat reldateFormat = new SimpleDateFormat("yyyy-MM-dd");

	static boolean isValidReldateAndInFuture(String sFile) {
		// ==============================================================================================

		int p = sFile.lastIndexOf('/');
		if (p != -1) {
			sFile = sFile.substring(p + 1); // in zips and jars we get the full psudo path
		}

		int len = sFile.length();
		if (len < 21)
			return false;

		if (sFile.toLowerCase().endsWith(".reldate") == false) {
			return false;
		}

		if (sFile.substring(len - 20, len - 18).contentEquals("__") == false) {
			return false;
		}

		Date reldate;

		try {
			reldate = reldateFormat.parse(sFile.substring(len - 18, len - 8));
		} catch (ParseException e) {
			return false;
		}

		return reldate.after(new Date() /* now */);
	}

	public String getFirstWordOfTitle() {
		// ==============================================================================================
		String[] parts = displayTitle.split(" ", 1);
		if (parts.length > 0)
			return parts[0];
		return "";
	}

	private void setDisplayTitle(String text_v, String sep, String cpath, char res_type) {
		// ==============================================================================================
		String text = text_v;

		int cutAt = text.lastIndexOf(".book_title");
		if (cutAt > -1)
			text = text.substring(0, cutAt);

		if (text.toLowerCase().endsWith(sep))
			text = text.substring(0, text.length() - sep.length());

		/* titles in jar's arrive with a long path embedded 
		 * so we do see unix sep on windows */
		int p = text.lastIndexOf(sep);
		if (p > -1)
			text = text.substring(p + sep.length());

		boolean ans[] = new boolean[2];
		text = Aaf.has_title_iso_lang(text, ans);
		boolean new_specificLang = ans[0];
		boolean new_activeLang = ans[1];

		if (new_specificLang == true && new_activeLang == false)
			return;

		if (new_specificLang == false && this.displayTitle_is_langSpecific)
			return;

		this.displayTitle_is_langSpecific = new_specificLang;

		displayTitle = Aaa.stripFrontDigitsAndClean(text);

		if (displayTitle.isEmpty() && displayTitle_is_langSpecific) {
			String s = Aaf.readfirstlineOfFileOrRes(text_v, cpath, res_type);
			if (s.trim().length() > 1) {
				displayTitle = s;
			}
		}

	}

	public LinChapter getChapterByDisplayNamePart(String s) {
		// ==============================================================================================
		s = s.toLowerCase();
		for (LinChapter h : this) {
			// @formatter:off
			if (   (h.displayNoUscore.toLowerCase().contains(s)) 
				|| (h.displayNoNumb.toLowerCase().contains(s)) 
				|| (h.displayNoExt.toLowerCase().contains(s)) 
				|| (h.filename.toLowerCase().contains(s))) {
				return h;
			}
			// @formatter:on
		}
		return null;
	}

	public LinChapter getChapterByIndex(int index) {
		// ==============================================================================================
		for (LinChapter h : this) {
			if (h.index == index) {
				return h;
			}
		}
		return null;
	}

	public boolean loadChapterByDisplayNamePart(String s) {
		// ==============================================================================================
		LinChapter chapter = getChapterByDisplayNamePart(s);
		if (chapter != null) {
			App.lastLoadedChapter = chapter;
			return chapter.loadWithShow("");
		}
		return false;
	}

	public boolean loadChapterByIndex(int index) {
		// ==============================================================================================
		LinChapter chapter = getChapterByIndex(index);
		if (chapter != null) {
			App.lastLoadedChapter = chapter;
			return chapter.loadWithShow("");
		}
		return false;
	}

//	public boolean loadChapterByIndexNoShow(int index) {
//		// ==============================================================================================
//		LinChapter chapter = getChapterByIndex(index);
//		if (chapter != null) {
//			App.lastLoadedChapter = chapter;
//			return chapter.load();
//		}
//		return false;
//	}

	public void reduceToOnly(File[] files) {
		// ==============================================================================================
		if (files == null)
			return;

		for (int i = size() - 1; i >= 0; i--) {
			LinChapter h = get(i);
			boolean found = false;
			for (File file : files) {
				String name = file.getName();
				if (h.filename.contentEquals(name)) {
					found = true;
					break;
				}
			}
			if (!found)
				remove(h);
		}
	}

	public int randAdjustedSize(boolean first) {
		// ==============================================================================================
		return (first && frontNumber >= 90) ? 0 : size();
	}

	public LinChapter pickRandomLinFile() {
		// ==============================================================================================
		int ind = (int) ((double) size() * Math.random());
//		System.out.print( "    Chapter " + (ind+1) + " of " + size() + "\n");
		return get(ind);
	}

}
