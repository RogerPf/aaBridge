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

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Pattern;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import com.rogerpf.aabridge.controller.Book.LinChapter;
import com.rogerpf.aabridge.model.Cc;

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

	/**   
	 */
	public Bookshelf(String idStr) {
		// ==============================================================================================
		first = idStr.isEmpty();
		sort_order = (first) ? 0 : 50;
		shelfname = "books" + idStr;
		shelfDisplayName = "Books" + idStr;

		fillWithBooks();
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

	private final static String sep = File.separator;

	public JMenu addToMenuBar(ActionListener aListener, JMenuBar menuBar) {
		// ==============================================================================================

		if (size() == 0)
			return null;

		String desc = shelfDisplayName;

		JMenu menu = new JMenu(desc);
		if (first) {
			menu.setMnemonic(KeyEvent.VK_B);
		}

		menuBar.add(menu);

		if (first)
			menu.setForeground(Cc.RedStrong);
		else
			menu.setForeground(Cc.GreenStrong);

		for (Book book : this) {
			if ((book.frontNumber >= 90) && shelfname.contentEquals("books" /* ie shelf 1 */))
				continue; // these are added as a special case
			if (book.dividerBefore) {
				menu.addSeparator();
			}
			JMenuItem menuItem = new JMenuItem(book.displayTitle);
			menuItem.setActionCommand(basePath + book.displayTitle);
			menuItem.addActionListener(aListener);
			menu.add(menuItem);
		}

		menu.addSeparator();

		addFirstShelf_90s_toMenu(aListener, menu);

		return menu;
	}

	static public void addFirstShelf_90s_toMenu(ActionListener aListener, JMenu menu) {
		// ==============================================================================================

		Bookshelf firstShelf = App.bookshelfArray.get(0);

		boolean devDividerAdded = false;

		for (Book book : firstShelf) {
			if (book.frontNumber < 90)
				continue;
			if (book.frontNumber >= 96 && App.showDevTestLins == false)
				continue;

			if (book.frontNumber >= 96 && !devDividerAdded) {
				devDividerAdded = true;
				menu.addSeparator();
			}

			JMenuItem menuItem = null;
			if (book.frontNumber == 90) {
				/* Special case because NOW  we do not want the first lin (Welcome)
				 * but instead we want the  New User  one
				 */
				menuItem = new JMenuItem("Help                     New User - Readme", KeyEvent.VK_H);
				menuItem.setActionCommand("open_Welcome_New_User");
				menuItem.addActionListener(aListener);
			}
			else {
				menuItem = new JMenuItem(book.displayTitle);
				menuItem.setActionCommand(firstShelf.basePath + book.displayTitle);
				menuItem.addActionListener(aListener);
			}

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

	public Book getBookByBasePathAndDisplayTitle(String s) {
		// ==============================================================================================
		for (Book book : this) {
			if (s.contentEquals(basePath + book.displayTitle)) {
				return book;
			}
		}
		return null;
	}

	static String all_books_in_this_shelf = "all_books_in_this_shelf";

	public void fillWithBooks() {
		// ==============================================================================================

		success = false;

		/**
		 * Where is our own 'java' code  i.e. 'us'  is located?
		 */
		URL locationMethodUrl = AaBridge.class.getProtectionDomain().getCodeSource().getLocation();
		File locationMethodFile;

		try {
			locationMethodFile = new File(locationMethodUrl.toURI());
		} catch (Exception e1) {
			System.out.println("Bookshelf:Inspect - locationMethodUrl FAILED  help! - " + e1.getMessage());
			return;
		}

		basePath = locationMethodFile.getPath();

		if ((basePath.toLowerCase().endsWith(".jar") || basePath.toLowerCase().endsWith(".zip")) == false) {

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
//		basePath = "C:\\a\\aaBridge_3.1.0.2708.jar"; // >>>>>>> FOR simple testing ONLY <<<<<<<<

		if ((basePath.toLowerCase().endsWith(".jar") || basePath.toLowerCase().endsWith(".zip"))) {
			/** 
			 * A jar file name is us or fake us above for testing - zip must be a dropped file
			 */
			File jarFile = new File(basePath);
			if (!jarFile.exists()) {
				System.out.println("Bookshelf:Inspect - Given a jar file name that does not exist " + basePath);
				return;
			}

			Pattern pattern = Pattern.compile(shelfname + "/[0-9][0-9][ |_].*[.lin|.reldate|.order]");

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
				}

				for (String s : contents) {
					if (s.contains("--DIVIDER--")) {
						divider_next = true;
						continue;
					}
					if (s.endsWith(".lin")) {
						gum.addFolderOnly(s, divider_next);
						divider_next = false;
					}
				}

				boolean divider_carryover = false;
				for (Splus splus : gum) {
					if (divider_carryover) {
						splus.divider = true;
					}

					Book book = new Book(basePath, splus.s, shelfname, shelfDisplayName, splus.divider);
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

			boolean divider_next = false;

			Pattern pattern = Pattern.compile("[0-9][0-9][ |_].*");

			File[] folders = null;
			try {
				folders = baseFolder.listFiles();

				// parse for shelf order (if any)
				for (File file : folders) {
					if (file.isDirectory() || file.getName().endsWith(".order") == false)
						continue;
					setSortOrder(file.getName());
					break;
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
							Book book = new Book(basePath, name, shelfname, shelfDisplayName, divider_next);
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
//			basePath = "C:\\ProgramSmall\\aaBridge"; // >>>>>>> Makes testing simpler <<<<<<<<
//			if (App.devMode)
//				basePath = "C:\\a";
		}
		else {
			// assert(false); why are we here !
			basePath = locationMethodFile.getPath();
		}

		return basePath + sep;
	}

	class Splus {
		// ---------------------------------- CLASS -------------------------------------
		public String s;
		public boolean divider;

		Splus(String s, boolean divider) {
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

			String shelfnameSlash = shelfname + '/';
			int shelfnameSlashLen = shelfnameSlash.length();

			if (name.startsWith(shelfnameSlash) == false) {
				@SuppressWarnings("unused")
				int z = 0;
			}

			int secondSlashInd = name.indexOf('/', shelfnameSlashLen);
			if (secondSlashInd < shelfnameSlashLen + 1)
				return;

			String folder = name.substring(shelfnameSlashLen, secondSlashInd);

			for (Splus splus : this) {
				if (splus.s.contentEquals(folder))
					return;
			}
			add(new Splus(folder, divider));
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

}
