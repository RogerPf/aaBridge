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
	public String shelfDisplayName = "";

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
			if (displayNoNumb.endsWith("regen")) {
				s = displayNoNumb = displayNoNumb.substring(0, displayNoNumb.length() - 5);
			}
			s = s.replace('_', ' ');
			displayNoUscore = s = s.trim();

			add(this);
		}

		/**
		*/
		public void setTitleBookStyle() {
			// =============================================================
			String s = "aaBridge  " + VersionAndBuilt.verAndBuildNo();

			s += "                                ";

			if (shelfDisplayName.length() > 0) {
				s += shelfDisplayName.trim() + "  /  ";
			}
			else {
				s += "OUTSIDE" + "  /  ";
			}

			if (displayTitle.length() > 0) {
				s += displayTitle + "  /  ";
			}

			s += displayNoUscore;

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

		public boolean matchAndActionReleaseDate(String sFile) {
			// ==============================================================================================
			if (sFile.contentEquals(displayNoExt) == false)
				return false;

			return true;
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
		commonBookConstructor(basePathIn, "", null, "");
	}

	public Book(String basePathIn, File[] onlyThese, String shelfname) { // Constructor from a path
		// ==============================================================================================
		commonBookConstructor(basePathIn, "", onlyThese, shelfname);
	}

	public Book(String basePathIn, String extraPath, String shelfname) {
		// ==============================================================================================
		commonBookConstructor(basePathIn, extraPath, null, shelfname);
	}

	private final static String sep = File.separator;

	static String all_lin_files_in_this_book = "all_lin_files_in_this_book";

	public void commonBookConstructor(String basePathIn, String extraPath, File[] onlyThese, String shelfname) {
		// ==============================================================================================

		String basePath = basePathIn;

		if (extraPath.length() > 0) {
			frontNumber = Aaa.extractPositiveInt(extraPath);
//			if (frontNumber == 19) {
//				@SuppressWarnings("unused")
//				int z = 0;
//			}
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
				basePath = locMethodFile.getPath() + sep + shelfname;
		}

		/** for basic testing only
		 */
//		basePath = "C:\\a\\aaBridge_Watson_Edition_2.4.0.2421.jar";   // for local testing only

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
			if (basePath.endsWith(".jar"))
				bookJarExtra = shelfname + "/" + extraPath;

			// We are running in a .jar on either windows, mac or linux or ...

			Pattern pattern = Pattern.compile(bookJarExtra + ".*[.lin|.title|.reldate]");

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

			// parse each of the files realy for the .lin files
			if (process_as_normal) {
				for (String sFile : ret) {
					if (sFile.toLowerCase().endsWith(".title"))
						setDisplayTitle(sFile, "/");

					if (sFile.toLowerCase().endsWith(".lin")) {
						new LinChapter('r', bookJarName, sFile);
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

					boolean load_lins_as_normal = true;

					// parse for whole book reldate
					if (App.observeReleaseDates) {
						for (File file : files) {
							String sFile = file.getName();
							if (sFile.endsWith(".reldate") == false)
								continue;
							if (sFile.contains(all_lin_files_in_this_book) == false)
								continue;
							if (isValidReldateAndInFuture(sFile) == false)
								continue;

							load_lins_as_normal = false;
							break;
						}
					}

					// parse each of the files realy for the .lin files
					if (load_lins_as_normal) {
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

							if (sFile.toLowerCase().endsWith(".lin"))
								new LinChapter('f', "", sFile);
						}
					}

					if (App.observeReleaseDates) {
						for (File file : files) {
							String sFile = file.getName();
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

	public double getLinWeighting() {
		// ==============================================================================================
		return Math.sqrt((double) this.size());
	}

	public LinChapter pickRandomLinFile() {
		// ==============================================================================================
		return get((int) ((double) size() * Math.random()));
	}

}
