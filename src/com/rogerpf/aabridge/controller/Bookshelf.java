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
	boolean success = false;
	String shelfDesc = "";

	/**   
	 */
	public Bookshelf() {
		// ==============================================================================================
	}

	private final String booksSlash = "books/";
	private final int booksSlashLength = booksSlash.length();

	private final static String sep = File.separator;

	public JMenu addToMenuBar(ActionListener aListener, JMenuBar menuBar, String desc, int keyEvent, boolean obeySingleBook, int droppedCount) {
		// ==============================================================================================

		if (size() == 0)
			return null;

		JMenu menu = new JMenu(desc);
		menu.setMnemonic(keyEvent);

		menuBar.add(menu);

		if (droppedCount > 0)
			menu.setForeground(Cc.GreenStrong);
		else if (droppedCount < 0)
			menu.setForeground(Cc.RedStrong);

		for (Book book : this) {
			if (obeySingleBook && App.singleBookOnly && (book.frontNumber != 01))
				continue;
			if (book.frontNumber >= 90)
				break;
			JMenuItem menuItem = new JMenuItem(book.displayTitle);
			menuItem.setActionCommand(basePath + book.displayTitle);
			menuItem.addActionListener(aListener);
			menu.add(menuItem);
		}
		if ((obeySingleBook && App.singleBookOnly) == false)
			menu.addSeparator();

		add90sToMenu(aListener, menu, obeySingleBook);

		return menu;
	}

	public void add90sToMenu(ActionListener aListener, JMenu menu, boolean obeySingleBook) {
		// ==============================================================================================
		if (obeySingleBook && App.singleBookOnly)
			return;

		for (Book book : this) {
			if (book.frontNumber < 90)
				continue;
			if (book.frontNumber > 95 && App.devMode == false)
				break;
			JMenuItem menuItem = new JMenuItem(book.displayTitle);
			menuItem.setActionCommand(basePath + book.displayTitle);
			menuItem.addActionListener(aListener);
			if (book.frontNumber == 90)
				menuItem.setMnemonic(KeyEvent.VK_H);
			menu.add(menuItem);
		}
	}

	public Book getBookByIndex(int index) { // the caller must check the range
		// ==============================================================================================
		Book book = get(index);
		book.shelfDesc = shelfDesc;
		return book;
	}

	public Book getAutoOpenBook() { // the caller must check the range
		// ==============================================================================================
		for (int i = 0; i < size(); i++) {
			Book book = get(i);
			if (book.autoOpen) {
				book.shelfDesc = shelfDesc;
				return book;
			}
		}
		return get(0);
	}

	public Book getBookWithChapterPartName(String chapterPartName) {
		// ==============================================================================================

		if (App.multiBookDisplay) { // note we scan backwards to kind the one in then welcome book (if there)
			for (int i = size() - 1; i >= 0; i--) {
				Book book = get(i);
				LinChapter chapter = book.getChapterByDisplayNamePart(chapterPartName);
				if (chapter != null) {
					return book;
				}
			}
		}
		else {
			for (int i = 0; i < size(); i++) {
				Book book = get(i);
				LinChapter chapter = book.getChapterByDisplayNamePart(chapterPartName);
				if (chapter != null) {
					return book;
				}
			}
		}
		return null;
	}

	public String getFirstWordOfBook01Title() {
		Book book = getBookByFrontNumb(01);
		if (book == null)
			return "";
		return book.getFirstWordOfTitle();
	}

	public boolean isDefaultToSingleBook() {
		// ==============================================================================================
		Book book = getBookByFrontNumb(90);
		if (book == null)
			return false;
		return book.defaultToSingleBook;
	}

	public Book getBookByFrontNumb(int frontNumb) {
		// ==============================================================================================
		for (Book book : this) {
			if (book.frontNumber == frontNumb) {
				book.shelfDesc = shelfDesc;
				return book;
			}
		}
		return null;
	}

	public Book getBookByBasePathAndDisplayTitle(String s) {
		// ==============================================================================================
		for (Book book : this) {
			if (s.contentEquals(basePath + book.displayTitle)) {
				book.shelfDesc = shelfDesc;
				return book;
			}
		}
		return null;
	}

	public void fillWithBooks(String basePathIn) {
		// ==============================================================================================
		basePath = basePathIn;
		success = false;

		if (basePath.isEmpty()) {
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

			if (basePath.endsWith(".jar") == false) {

				/* Bodge ALERT ***************************** start */
				/**
				 * During developement we want to display the 'src' versions of the files
				 * NOT the tranisent versions in 'bin' which require a refresh and build to
				 * get updated so we can see them.   NOTE we must be at dev time loading here
				 * as the origin   basePath   given to us was empty.
				 */
				if (basePath.endsWith("bin")) {
					String baseSrc = basePath.substring(0, basePath.length() - 3) + "src";
					if ((new File(baseSrc).isDirectory()))
						basePath = baseSrc;
				}
				/* Bodge ALERT ***************************** end */

				/**
				 * Add the books subfolder as normal
				 */
				basePath += sep + "books";
			}

			/**
			 * Yes this is a very roundabout way to do things - but it means that we can
			 * right here switch to an external jar for testing the book loader while still running
			 * ALL code in the eclipse debugger.
			 */
			// basePath = "C:\\a\\aaBridge_2.0.1.2020.jar"; // >>>>>>> FOR simple testing ONLY <<<<<<<<
		}

		if (basePath.endsWith(".jar")) {
			/** 
			 * A jar file name has been fed to us (eg by being dropped) or we are one
			 */
			File jarFile = new File(basePath);
			if (!jarFile.exists()) {
				System.out.println("Bookshelf:Inspect - Given a jar file name that does not exist " + basePath);
				return;
			}

			Pattern pattern = Pattern.compile("books/[0-9][0-9][ |_].*[.lin]");

			ArrayList<String> contents = (ArrayList<String>) ResourceList.getResourcesFromJarFile(jarFile, pattern);

			StringAmalgum gum = new StringAmalgum();
			for (String s : contents) {
				gum.addFolderOnly(s);
			}

			for (String s : gum) {
				Book book = new Book(basePath, s);
				if (book.size() > 0)
					add(book);
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

			Pattern pattern = Pattern.compile("[0-9][0-9][ |_].*");

			File[] folders = null;
			try {
				folders = baseFolder.listFiles();
				for (File folder : folders) {
					if (!folder.isDirectory())
						continue;
					String name = folder.getName();
					final boolean accept = pattern.matcher(name).matches();
					if (accept) {
						Book book = new Book(basePath, name);
						if (book.size() > 0)
							add(book);
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

	/**   
	 */
	class StringAmalgum extends ArrayList<String> {
		// ---------------------------------- CLASS -------------------------------------
		private static final long serialVersionUID = 1L;

		/**
		 * Should only be called by code combining Jar entries
		 */
		// ==============================================================================================
		public void addFolderOnly(String name) {
			assert (name.startsWith(booksSlash));

			int secondSlashInd = name.indexOf('/', booksSlashLength);
			if (secondSlashInd < booksSlashLength + 1)
				return;

			String folder = name.substring(booksSlashLength, secondSlashInd);

			for (String s : this) {
				if (s.contentEquals(folder))
					return;
			}
			add(folder);
		}
	}

}
