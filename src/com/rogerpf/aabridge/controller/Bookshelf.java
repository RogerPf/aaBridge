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

import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.rogerpf.aabridge.controller.Book.LinChapter;
import com.rogerpf.aabridge.model.Cc;
import com.rogerpf.aabridge.view.AaDragGlassPane;
import com.version.VersionAndBuilt;

/**   
 */
public class Bookshelf extends ArrayList<Book> {
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	public boolean zipped = false;
	public boolean group = false;
	public boolean book = false;
	public String basePath = "";
	public File file;
	public boolean success = false;
	public String shelfname;
	public String shelfDisplayName;
	public boolean first;
	public int sort_order; // 1 - 99
	private boolean shelfName_is_langSpecific = false;
	public String idStr;

	/**   
	 */
	public Bookshelf(String letter, String bookpath) {
		// ==============================================================================================
		String firstExtra = "";
		if (letter.isEmpty()) {
			first = true;
			idStr = "";
			sort_order = 0;
			firstExtra = "      ";
		}
		else {
			idStr = "-" + letter;
			sort_order = 50;
		}

		shelfname = "Books" + idStr;
		shelfDisplayName = firstExtra + Aaf.menubar_books + idStr;

		fillWithBooks(bookpath);
	}

	private void setSortOrder(String name) {
		// ==============================================================================================
		if (first)
			return;

		// we will normally be passed .jar resource names so strip
		int sl = name.lastIndexOf('/'); // always "/" never sep
		if (sl > -1) {
			name = name.substring(sl + 1);
		}

		sort_order = Aaa.extractPositiveIntOrZero(name);
		if (sort_order <= 0)
			sort_order = 50;
		if (sort_order > 99)
			sort_order = 99;
	}

	private void setMenuDisplayName(String text_v, String cpath, char res_type) {
		// ==============================================================================================
		if (first)
			return;

		String text = text_v;

		// System.out.println("setMenuDisplayName:  " + text_v + "   " + cpath + "   " + res_type);

		int ind = text.lastIndexOf(".bar_title");
		if (ind > -1)
			text = text.substring(0, ind); // starts with 00

		// we will normally be passed .jar resource names so strip
		int sl = text.lastIndexOf('/'); // always "/" never sep
		if (sl > -1) {
			text = text.substring(sl + 1);
		}

		boolean ans[] = new boolean[2];
		text = Aaf.has_title_iso_lang(text, ans);
		boolean new_specificLang = ans[0];
		boolean new_activeLang = ans[1];

		if (new_specificLang == true && new_activeLang == false)
			return;

		if (new_specificLang == false && this.shelfName_is_langSpecific)
			return;

		shelfName_is_langSpecific = new_specificLang;

		shelfDisplayName = Aaa.stripFrontDigitsAndClean(text).trim();

		if (shelfDisplayName.isEmpty() && shelfName_is_langSpecific) {
			String s = Aaf.readfirstlineOfFileOrRes(text_v, cpath, res_type);
			if (s.length() > 1) {
				shelfDisplayName = s;
			}
		}
	}

	private final static String sep = File.separator;

	public JMenu createMenu(ActionListener aListener) {
		// ==============================================================================================

		if (size() == 0)
			return null;

		String extra = "";

		if (App.Books_E_renamed_to.isEmpty() == false) {
			shelfDisplayName = App.Books_E_renamed_to;
			App.Books_E_renamed_to = "";
			extra = "  ";
		}

		JMenu menu = new JMenu(extra + shelfDisplayName);

		if (first) {
			menu.setMnemonic(KeyEvent.VK_B);
		}

		if (first)
			menu.setForeground(Cc.RedStrong);
		else
			menu.setForeground(Cc.GreenStrong);

		for (Book book : this) {
			if (first && (book.frontNumber >= 90))
				continue; // these are added as a special case
			if (book.dividerBefore) {
				menu.addSeparator();
			}
			// System.out.println("Path: " + basePath + ",        " + book.displayTitle);

			String disp = book.displayTitle;

			JMenuItem menuItem = new JMenuItem();

			if (shelfname.equals("Books-B")) {
				menu.setForeground(Cc.BlueMenubar);
				int pos1 = disp.indexOf('-');
				if (pos1 > 2 && disp.indexOf('-', pos1 + 1) < 0) { // i.e. there isn't a 2nd one
					String front = disp.substring(0, pos1).trim();
					String back = disp.substring(pos1 + 1).trim();
					front = Aaf.spacedOut(menuItem, front, 90);
					disp = front + " -     " + back;
				}
			}

			menuItem.setText(disp);
			menuItem.setActionCommand(book.getMenuKey(basePath));
			menuItem.addActionListener(aListener);
			menu.add(menuItem);
		}

//		menu.addSeparator();

//		addFirstShelf_90s_toMenu(aListener, menu);

		return menu;
	}

	static public void addFirstShelf_90s_toMenu(ActionListener aListener, JMenu menu) {
		// ==============================================================================================

		Bookshelf firstShelf = App.bookshelfArray.get(0);

		boolean devDividerAdded = false;

		JMenuItem menuItem = new JMenuItem();

		int firstPartLen = Aaf.getMetricsLength(menuItem, Aaf.helpMenu_howdoI) + App.menuTabExtra;
		if (firstPartLen < 60)
			firstPartLen = 60;

		for (Book book : firstShelf) {
			if (book.frontNumber < 90)
				continue;

			if (book.frontNumber == 90) {
				continue;
//				/* 
//				 * Special case because we do not want the first lin (Welcome)
//				 * which is std but instead we want the  New User  lin (from that book the 90 book)
//				 */
//				menuItem = new JMenuItem(Aaf.spacedOut(menuItem, Aaf.menuCmn_help, firstPartLen) + Aaf.menuCmn_newUser);
//				menuItem.setActionCommand("open_Welcome_New_User");
			}
			else if (book.frontNumber == 91) {
				menuItem = new JMenuItem(Aaf.spacedOut(menuItem, "", (firstPartLen * 96) / 130) + "     " + Aaf.menuCmn_write);
				menuItem.setActionCommand(book.getMenuKey(firstShelf.basePath));
//				continue;
			}
			else if (book.frontNumber == 92) {
				String text = Aaf.spacedOut(menuItem, "", (firstPartLen * 96) / 130) + Aaf.menuCmn_whatGoesOn + " " + Aaf.menuCmn_inside;
				menuItem = new JMenuItem(text);
				Font slightlyBiggerFont = menuItem.getFont().deriveFont(menuItem.getFont().getSize() * App.menuInfoSize);
				menuItem.setFont(slightlyBiggerFont);
				menuItem.setActionCommand("localize-how-to");
				menuItem.setForeground(Cc.RedStrong);
			}
			else if (book.frontNumber >= 96 && App.showDevTestLins == true) {
				if (!devDividerAdded) {
					devDividerAdded = true;
					menu.addSeparator();
				}
				menuItem = new JMenuItem(Aaf.spacedOut(menuItem, "", firstPartLen) + book.displayTitle);
			}
			else {
				continue;
			}
			menuItem.setActionCommand(book.getMenuKey(firstShelf.basePath));
			menuItem.addActionListener(aListener);
			menu.add(menuItem);
		}
	}

	public Book getBookByIndex(int index) { // the caller must check the range
		// ==============================================================================================
		Book book = get(index);
		return book;
	}

	public Book getBookWithChapterPartName(String chapterPartName) {
		// ==============================================================================================

		// note we scan backwards to find the one in the welcome book (if there)

		for (int i = size() - 1; i >= 0; i--) {
			Book book = get(i);
//			if (book.bookJarName.contains("Books-E")) {
//				int x =0;
//				x++;
//			}
			LinChapter chapter = book.getChapterByDisplayNamePart(chapterPartName);
			if (chapter != null) {
				return book;
			}
		}
		return null;
	}

	public String getFirstWordOfBook01Title() {
		// ==============================================================================================
		Book book = getBookByFrontNumb(01);
		if (book == null)
			return "";
		return book.getFirstWordOfTitle();
	}

	public Book getBookByFrontNumb(int frontNumb) {
		// ==============================================================================================
		for (Book book : this) {
			if (book.frontNumber == frontNumb) {
				return book;
			}
		}
		return null;
	}

	public Book getBookByBasePathAndBookDisplayTitle(String s) {
		// ==============================================================================================
		for (Book book : this) {
			if (s.contentEquals(book.getMenuKey(basePath))) {
				return book;
			}
		}
		return null;
	}

	static String all_books_in_this_shelf = "all_books_in_this_shelf";

	public void fillWithBooks(String path) {
		// ==============================================================================================
		String basePath = path;

		boolean test = false;

		success = false;

		/**
		 * Where is our own 'java' code  i.e. 'us'  is located?
		 */
		if (basePath.isEmpty()) {
			URL locationMethodUrl = AaBridge.class.getProtectionDomain().getCodeSource().getLocation();
			File locationMethodFile;

			try {
				locationMethodFile = new File(locationMethodUrl.toURI());
			} catch (Exception e1) {
				System.out.println("Bookshelf:Inspect - locationMethodUrl FAILED  help! - " + e1.getMessage());
				return;
			}

			basePath = locationMethodFile.getPath();
		}
//		else {
//			test = true;
//			@SuppressWarnings("unused")
//			int x = 0;
//		}

		String low_bp = basePath.toLowerCase();
		if ((low_bp.endsWith(".jar") || low_bp.endsWith(".zip") || low_bp.endsWith(".linzip")) == false) {

			/* Bodge ALERT ***************************** start */
			/**
			 * During developement we want to display the 'src' versions of the files
			 * NOT the tranisent versions in 'bin' which require a refresh and build to
			 * get updated so we can see them.   NOTE we must be at dev time loading here
			 * as the origin   basePath   given to us was empty.
			 */
			if (basePath.toLowerCase().endsWith("bin")) {
				String baseSrc = basePath.substring(0, basePath.length() - 3) + "src";
				if ((new File(baseSrc).isDirectory()))
					basePath = baseSrc;
			}
			/* Bodge ALERT ***************************** end */

			/**
			 * Add the books subfolder as normal
			 */
			basePath += sep + shelfname;
		}

		/**
		 * Yes this is a very roundabout way to do things - but it means that we can
		 * right here switch to an external jar for testing the book loader while still running
		 * ALL code in the eclipse debugger.
		 */

		/** for basic testing only
		 */
		if (App.debug_using_ghost_jar && basePath.startsWith(App.thisAppBaseFolder)) {
			basePath = App.thisAppBaseJarIncPath;
		}

		String low_bp2 = basePath.toLowerCase();
		if ((low_bp2.endsWith(".jar") || low_bp2.endsWith(".zip") || low_bp2.endsWith(".linzip"))) {
			/** 
			* A jar file name is us or fake us above for testing - zip must be a dropped file
			*/
			File jarFile = new File(basePath);
			if (!jarFile.exists()) {
				System.out.println("Bookshelf:Inspect - Given a jar file name that does not exist " + basePath);
				return;
			}

			String extd = "";
			if (shelfname.contentEquals("Books-E")) {
				extd = ".*";
			}

			Pattern pattern = Pattern.compile(shelfname + extd + "/[0-9][0-9][ |_].*[.lin|.reldate|.order|.bar_title]");

			// shelfname will mean that incorrectly capitalised Books-<letter> shelves will be skipped

			ArrayList<String> contents = (ArrayList<String>) ResourceList.getResourcesFromJarFile(jarFile, pattern);

			boolean process_as_normal = true;
			boolean divider_next = false;

			// parse for whole shelf reldate
			if (App.observeReleaseDates) {
				for (String sFile : contents) {
					if (sFile.endsWith(".reldate") == false)
						continue;
					if (sFile.contains(all_books_in_this_shelf) == false)
						continue;
					if (Book.isValidReldateAndInFuture(sFile) == false)
						continue;

					process_as_normal = false;
					break;
				}
			}

			if (process_as_normal) {
				Amalgum gum = new Amalgum();

				for (String sFile : contents) {
					if (sFile.endsWith(".order") == false)
						continue;
					setSortOrder(sFile);
					break;
				}

				for (String sFile : contents) {
//					System.out.println(sFile);
					if (sFile.endsWith(".bar_title")) {
						setMenuDisplayName(sFile, sFile, 'r');
					}
				}

				for (String s : contents) {
					if (s.contains("--DIVIDER--")) {
						divider_next = true;
						continue;
					}
					if (s.endsWith(".lin") || s.endsWith(".pbn")) {
						gum.addFolderOnly(s, divider_next);
						divider_next = false;
					}
				}

				boolean divider_carryover = false;
				for (Splus splus : gum) {
					if (divider_carryover) {
						splus.divider = true;
					}

					if (test) {
						@SuppressWarnings("unused")
						int x = 0;
					}

					Book book = new Book(this, basePath, splus.s, splus.shelfnamePlus, shelfDisplayName, splus.divider);
					if (book.size() > 0) {
						add(book);
						divider_carryover = false;
					}
					else {
						divider_carryover = splus.divider;
					}
				}
			}

		}

		else {
			/**
			 *  This are NOT a jar file. 
			 *  We are in eclipse getting the local 'books' proto resource 
			 *  OR someone has dragged and dropped a folder on to us
			 *  either way we want only the nn[ ] folders
			 *  
			 *  If we are a local proto resoure then the "books" path addition
			 *  has already been added above
			 */

			basePath += sep;
			File baseFolder = new File(basePath);

			if (baseFolder.exists() == false || baseFolder.isDirectory() == false)
				return;

			try {
				String truefilepath = baseFolder.getCanonicalFile().getPath();
				if (isCapilatisationCorrect(truefilepath, File.separatorChar) == false)
					return;
			} catch (Exception e) {
			}

			boolean divider_next = false;

			Pattern pattern = Pattern.compile("[0-9][0-9][ |_].*");

			File[] folders = null;
			try {
				folders = baseFolder.listFiles();

				// parse for shelf order (if any)
				for (File file : folders) {
					if (file.isDirectory() || !file.getName().endsWith(".order"))
						continue;
					setSortOrder(file.getName());
					break;
				}

				for (File file : folders) {
					String sFile = file.getName();
					if (!file.isDirectory() && sFile.startsWith("00") && sFile.endsWith(".bar_title")) {
						setMenuDisplayName(sFile, file.getCanonicalPath(), 'f');
					}
				}

				boolean process_as_normal = true;

				// parse for whole shelf reldate
				if (App.observeReleaseDates) {
					for (File file : folders) {
						String sFile = file.getName();
						if (sFile.endsWith(".reldate") == false)
							continue;
						if (sFile.contains(all_books_in_this_shelf) == false)
							continue;
						if (Book.isValidReldateAndInFuture(sFile) == false)
							continue;

						process_as_normal = false;
						break;
					}
				}

				if (process_as_normal) {
					for (File folder : folders) {
						String name = folder.getName();
						if (name.contains("--DIVIDER--")) {
							divider_next = true;
							continue;
						}
						final boolean accept = pattern.matcher(name).matches();
						if (accept) {
							Book book = new Book(this, basePath, name, shelfname, shelfDisplayName, divider_next);
							if (book.size() > 0) {
								add(book);
								divider_next = false;
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}

		Collections.sort(this, new Comparator<Book>() {
			public int compare(Book b1, Book b2) {
				return ((b1.frontNumber < b2.frontNumber) ? -1 : 1);
			}
		});

		@SuppressWarnings("unused")
		int z = 0;

	}

	private boolean isCapilatisationCorrect(String path, char sep) {
		// ==============================================================================================
		String folderName = path.substring(path.lastIndexOf(sep) + 1);
		if (folderName.contentEquals("Books"))
			return true;

		if ((folderName.length() != 7) || (folderName.startsWith("Books-") == false))
			return false;

		char cLast = folderName.charAt(6);

		return ('A' <= cLast) && (cLast <= 'Z');
	}

	public static String getBasePathOfJarOrEquivalent() { // just used by deep finesse integreration
		// ==============================================================================================

		/**
		 * Where is our own 'java' code  i.e. 'us'  is located?
		 */
		URL locationMethodUrl = AaBridge.class.getProtectionDomain().getCodeSource().getLocation();
		File locationMethodFile;

		try {
			locationMethodFile = new File(locationMethodUrl.toURI());
		} catch (Exception e1) {
			System.out.println("Bookshelf:getBsePath... - locationMethodUrl FAILED  help! - " + e1.getMessage());
			return sep;
		}

		String basePath = locationMethodFile.getPath();

		if (basePath.toLowerCase().endsWith(".jar")) {

			basePath = locationMethodFile.getParent();

		}
		else if (basePath.toLowerCase().endsWith("bin")) {
			basePath = locationMethodFile.getParent();
		}
		else {
			// assert(false); why are we here !
			basePath = locationMethodFile.getPath();
		}

		return basePath + sep;
	}

	class Splus {
		// ---------------------------------- CLASS -------------------------------------
		public String shelfnamePlus;
		public String s;
		public boolean divider;

		Splus(String shelfnamePlus, String s, boolean divider) {
			this.shelfnamePlus = shelfnamePlus;
			this.s = s;
			this.divider = divider;
		}
	}

	/**   
	 */
	class Amalgum extends ArrayList<Splus> {
		// ---------------------------------- CLASS -------------------------------------
		private static final long serialVersionUID = 1L;

		/**
		 * Should only be called by code combining Jar entries
		 */
		public void addFolderOnly(String name, boolean divider) {
			// ==============================================================================================

			int p = name.indexOf('/');

			String shelfnamePlus = name.substring(0, p); // shelfname + '/';

			if ((shelfnamePlus.length() > 7) && shelfnamePlus.startsWith("Books-E")) {
				String s = App.Books_E_renamed_to = shelfnamePlus.substring(7, p).replace('_', ' ');
				if (s.length() > 1 && ('0' <= s.charAt(0)) && (s.charAt(0) <= '9')) {
					App.Books_E_renamed_to = s.substring(1).trim();
				}
			}

			if (name.startsWith(shelfname) == false) {
				@SuppressWarnings("unused")
				int z = 0;
				assert (false);
			}

			int secondSlashInd = name.indexOf('/', p + 1);
			if (secondSlashInd < p + 1)
				return;

			String folder = name.substring(p + 1, secondSlashInd);

			for (Splus splus : this) {
				if (splus.s.contentEquals(folder))
					return;
			}
			add(new Splus(shelfnamePlus, folder, divider));
		}

	}

	public boolean hasValidBooksForMenu() {
		// ==============================================================================================
		return size() > 0;
	}

	public int randAdjustedSize() {
		// ==============================================================================================
		int tot = 0;
		for (Book book : this) {
			tot += book.randAdjustedSize(first);
		}
		return tot;
	}

	public LinChapter pickRandomLinFile() {
		// ==============================================================================================
		double total_weight = 0;
		for (Book book : this) {
			total_weight += Math.sqrt(book.randAdjustedSize(first));
		}

		double chosen_book = total_weight * Math.random();

		double weight = 0;
		for (Book book : this) {
			weight += Math.sqrt(book.randAdjustedSize(first));
			if (chosen_book <= weight) {
//				System.out.print( "    " + book.displayTitle + " of " + size());
				return book.pickRandomLinFile();
			}
		}
		return null;
	}

	/**   
	 */
	public static void copy_folder_to_desktop(String internal_folder_name, String new_folder_name) {
		// ==============================================================================================

		String target_folder_path_name = App.desktop_folder + File.separator + new_folder_name;

		String marker = "_from_aaBridge_" + VersionAndBuilt.verAndBuildNo();
		File markerFile = new File(target_folder_path_name + File.separator + marker);

		File targetFolder = new File(target_folder_path_name);

		if (targetFolder.exists() && !markerFile.exists()) {
			App.frame.aaDragGlassPane.showOverlayScreen(AaDragGlassPane.docCol_error);
			return;
		}

		// we look at the first book and chapter to see what file type we have jar or dev source
		if (App.runningInJar) {
			copyResourseFolder(internal_folder_name, targetFolder);
		}
		else {
			copyRealFolder(internal_folder_name, targetFolder);
		}

		try {
			markerFile.createNewFile();
		} catch (Exception e) {
		}

		App.frame.aaDragGlassPane.showOverlayScreen(AaDragGlassPane.docCol_copied);

		@SuppressWarnings("unused")
		int z = 0;

	}

	/**   
	 */
	public static void copyResourseFolder(String internal_folder_name, File target_folder) {
		// ==============================================================================================

		target_folder.mkdir();

		File jarFile = new File(App.thisAppBaseJarIncPath);

		int removeLen = internal_folder_name.length() + 1;

		ZipFile zfile;
		try {
			zfile = new ZipFile(jarFile);

			Enumeration<? extends ZipEntry> entries = zfile.entries();

			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				long time = entry.getTime();
				String ename = entry.getName();
				if (ename.startsWith(internal_folder_name) == false)
					continue;
				if (ename.length() <= removeLen)
					continue;

				ename = ename.substring(removeLen);
				// System.out.println("v2  " + ename + "  time " + time);

				File fileOut = new File(target_folder, ename);
				if (entry.isDirectory()) {
					fileOut.mkdirs();
				}
				else {
					fileOut.getParentFile().mkdirs();

					try {
						InputStream is = zfile.getInputStream(entry);
						OutputStream out = new FileOutputStream(fileOut);

						// Transfer bytes from in to out
						byte[] buf = new byte[8 * 1024];
						int len;
						while ((len = is.read(buf)) > 0) {
							out.write(buf, 0, len);
						}
						is.close();
						out.close();
					} catch (IOException e) {
					}

					fileOut.setLastModified(time);
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	/**   
	 */
	public static void copyRealFolder(String internal_folder_name, File target_folder) {
		// ==============================================================================================

		target_folder.mkdir();

		FileInputStream is = null;

		// Get the list of all the potential files
		File[] files = null;

		files = new File(App.thisAppBaseFolder + File.separator + internal_folder_name).listFiles();
		if (files != null) {

			for (File fileIn : files) {

				long time = fileIn.lastModified();

				String target = target_folder + File.separator + fileIn.getName();

				// System.out.println(target);

				File fileOut = new File(target);

				try {
					is = new FileInputStream(fileIn);
					OutputStream out = new FileOutputStream(fileOut);

					// Transfer bytes from in to out
					byte[] buf = new byte[8 * 1024];
					int len;
					while ((len = is.read(buf)) > 0) {
						out.write(buf, 0, len);
					}
					out.close();
					is.close();
					if (time != 0) {
						fileOut.setLastModified(time);
					}
				} catch (IOException e) {
				}
			}
		}
	}

}
