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
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
	public String shelfDesc = "";
	public int linCount = 0;
	public boolean autoOpen = false;

	public boolean defaultToSingleBook = false;

	public int lastChapterIndexLoaded = -1;

	int frontNumber = 0;

	public class LinChapter {
		// ---------------------------------- CLASS -------------------------------------
		char type;
		String parentOrJar;
		public String filenamePlus;
		public String filename;
		public String displayNoExt;
		public String displayNoNumb;
		public String displayNoUscore;
		public int index;

		public String getName() {
			return displayNoUscore;
		}

		public LinChapter(char type, String base, String filenameIn) { // Constructor
			// ==============================================================================================
			this.type = type;
			this.parentOrJar = base;
			filenamePlus = filenameIn;
			String s = filenameIn;

			int sl = s.lastIndexOf('/'); // always "/" never sep
			if (sl > -1) {
				s = s.substring(sl + 1);
			}
			filename = s;
			assert (s.toLowerCase().endsWith(".lin"));
			s = s.substring(0, s.length() - 4); // it always ends with .lin
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
			s = s.replace('_', ' ');
			displayNoUscore = s = s.trim();

			add(this);
		}

		/**
		*/
		public void setTitleBookStyle() {
			// =============================================================
			String s = "aaBridge  " + VersionAndBuilt.verAndBuildNo();

			s += "   -   ";

			if (shelfDesc.length() > 0) {
				s += shelfDesc + " / ";
			}

			s += displayTitle + " / " + displayNoUscore;

			App.frame.setTitle(s);
		}

		public boolean load() {
			// ==============================================================================================
			Boolean success = false;

			App.flowOnlyCommandBar = false;
			App.hideCommandBar = false; // only set true by the lbx (distr Flash cards question special)
			App.hideTutNavigationBar = false; // only set true by the lbx (distr Flash cards question special)

			if (type == 'r') {
				success = BridgeLoader.readLinResourseIfExists(bookJarName, filenamePlus);
			}

			if (type == 'f') {
				success = BridgeLoader.readLinFileIfExists(bookFolderName, filenamePlus);
			}

			if (success) {
				lastChapterIndexLoaded = index;
				setTitleBookStyle();
			}

			return success;
		}

		public boolean loadWithShow(String type) {
			// ==============================================================================================
			boolean loaded = load();

			if (loaded) {
				App.book = Book.this;
				if (type.contentEquals("replaceBookPanel"))
					App.bookPanel.matchToAppBook();
				App.bookPanel.showChapterAsSelected(getName());

				if (App.isLin__VuGraphAndTwoTeams() && App.showRedVuGraphArrow) {
					App.gbo.showVuGraphHint();
				}

				return loaded;
			}

			return false;
		}
	}

	public Book() { // Constructor of an empty book
		// ==============================================================================================
	}

	public String longId() {
		// ==============================================================================================
		return bookJarName + " " + bookFolderName;
	}

	public Book(String basePathIn) { // Constructor from a path
		// ==============================================================================================
		commonBookConstructor(basePathIn, "", null);
	}

	public Book(String basePathIn, File[] onlyThese) { // Constructor from a path
		// ==============================================================================================
		commonBookConstructor(basePathIn, "", onlyThese);
	}

	public Book(String basePathIn, String extraPath) {
		// ==============================================================================================
		commonBookConstructor(basePathIn, extraPath, null);
	}

	private final static String sep = File.separator;
	private final static String auto_open_first = "auto_open_this_book_first.txt";

	public void commonBookConstructor(String basePathIn, String extraPath, File[] onlyThese) {
		// ==============================================================================================

		String basePath = basePathIn;

		if (extraPath.length() > 0) {
			frontNumber = Aaa.extractPositiveInt(extraPath);
		}

		if (basePath.isEmpty()) {

			assert (false); // I don't think we EVER have an empty book path now (since having bookshelf)

			URL locationMethodUrl = AaBridge.class.getProtectionDomain().getCodeSource().getLocation();
			File locMethodFile = null;
			try {
				locMethodFile = new File(locationMethodUrl.toURI());
			} catch (Exception e1) {
				String s = "book (cons) - locationMethodUrl FAILED  help! - " + e1.getMessage();
				System.out.println(s);
				return;
			}

			if (basePath.endsWith(".jar") == false)
				basePath = locMethodFile.getPath() + sep + "books";
		}

		/** for basic testing only
		 */
//		basePath = "C:\\a\\aaBridge_2.0.1.2120.jar";

		if (basePath.endsWith(".jar")) {
			/** 
			 * clearly we are in a jar file
			 */
			File jarFile = new File(basePath);
			if (!jarFile.exists()) {
				System.out.println("Book (cons) - Given a jar file name that does not exist " + basePath);
				return;
			}

			bookJarName = jarFile.getPath();
			bookJarExtra = "books/" + extraPath;

			// We are running in a .jar on either windows, mac or linux or ...

			Pattern pattern = Pattern.compile(bookJarExtra + ".*[.lin|.title|" + auto_open_first + "]");

			ArrayList<String> ret = (ArrayList<String>) ResourceList.getResourcesFromJarFile(jarFile, pattern);

			for (String sFile : ret) {
				if (sFile.toLowerCase().endsWith(".title"))
					setDisplayTitle(sFile, "/");

				if (sFile.toLowerCase().endsWith(".txt") && sFile.toUpperCase().contains("DEFAULT_TO_SINGLE_BOOK"))
					defaultToSingleBook = true;

				if (sFile.toLowerCase().endsWith(".lin")) {
					new LinChapter('r', bookJarName, sFile);
					linCount++;
				}
				if (sFile.contains(auto_open_first))
					autoOpen = true;
			}
			if (displayTitle.isEmpty())
				setDisplayTitle(bookJarExtra, "/");

			@SuppressWarnings("unused")
			int z = 0;
		}

		else {
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

					for (File file : files) {
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

						if (sFile.toLowerCase().endsWith(".title"))
							setDisplayTitle(sFile, sep);

						if (sFile.toLowerCase().endsWith(".txt") && sFile.toUpperCase().contains("DEFAULT_TO_SINGLE_BOOK"))
							defaultToSingleBook = true;

						if (sFile.toLowerCase().endsWith(".lin"))
							new LinChapter('f', "", sFile);

						if (sFile.contains(auto_open_first))
							autoOpen = true;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			if (displayTitle.isEmpty()) {
				setDisplayTitle(bookFolderName, sep);
			}
		}

		Collections.sort(this, new Comparator<LinChapter>() {
			public int compare(LinChapter ch1, LinChapter ch2) {
				return (ch1.filename.compareTo(ch2.filename));
			}
		});

		for (int i = 0; i < size(); i++) {
			get(i).index = i;
		}
	}

	public String getFirstWordOfTitle() {
		// ==============================================================================================
		String[] parts = displayTitle.split(" ");
		if (parts.length > 0)
			return parts[0];
		return "";
	}

	private void setDisplayTitle(String text, String sepSpecial) {
		// ==============================================================================================
		if (text.toLowerCase().endsWith(".title"))
			text = text.substring(0, text.length() - 6);

		if (text.toLowerCase().endsWith(sepSpecial))
			text = text.substring(0, text.length() - sepSpecial.length());

		/* titles in jar's arrive with a long path embedded 
		 * so we do see unix sep on windows */
		int p = text.lastIndexOf(sepSpecial);
		if (p >= 0)
			text = text.substring(p + 1);

		displayTitle = Aaa.stripFrontDigitsAndClean(text);
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
			return chapter.loadWithShow("");
		}
		return false;
	}

	public boolean loadChapterByIndex(int index) {
		// ==============================================================================================
		LinChapter chapter = getChapterByIndex(index);
		if (chapter != null) {
			return chapter.loadWithShow("");
		}
		return false;
	}

	public boolean loadChapterByIndexNoShow(int index) {
		// ==============================================================================================
		LinChapter chapter = getChapterByIndex(index);
		if (chapter != null) {
			return chapter.load();
		}
		return false;
	}

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

}
