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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.rogerpf.aabridge.igf.MassGi;
import com.rogerpf.aabridge.model.Lin;

/**   
 */
public class BridgeLoader {
	// ---------------------------------- CLASS -------------------------------------

	/**   
	 */
	public static boolean processDroppedList(File[] files) {
		// ==============================================================================================

		/**
		 *  if there are any directories the list then they are tried in order (no recursion)
		 */
		for (File file : files) {
			if (file.isDirectory()) {
				boolean success = makeBookFromPath(file.getPath(), null);
				if (success)
					return true;
			}
		}

		/**
		 *  if there is a zip in the list
		 *  note - deeper in  we can handle a .jar specifically ourselves BUT at this level .jars 
		 *  are not dropable
		 */
		for (File file : files) {
			String low = file.getName().toLowerCase();
			if (file.isFile() && (low.endsWith(".zip") || low.endsWith(".linzip"))) {
				boolean success = makeBookFromPath(file.getPath(), null);
				if (success)
					return true;
			}
		}

		/**
		 *  no dirs or jars (with books) so let try looking for lin's
		 */
		File firstLin = null;
		int linCount = 0;
		for (File file : files) {
			if (file.isFile() && file.getName().toLowerCase().endsWith(".lin")) {
				linCount++;
				if (firstLin == null)
					firstLin = file;
			}
		}

		if (linCount > 0) {
			String parentPath = firstLin.getParent();
			boolean success = makeBookFromPath(parentPath, files);
			return success;
		}

		return false;
	}

	public static boolean makeBookFromPath(String bookPath, File[] onlyThese) {
		// ==============================================================================================
		/** 
		 * Path should now not be empty
		 */
		Book b = new Book(bookPath, onlyThese);
		boolean chapterLoaded = false;
		if (b.size() > 0) {
			chapterLoaded = b.loadChapterByIndex(0);
		}
		if (chapterLoaded) {
			App.book = b;
		}
		App.bookPanel.matchToAppBook();
		return chapterLoaded;
	}

	/**   
	 */
	public static boolean readLinFileIfExists(String pathWithSep, String dealName) {
		// ==============================================================================================

		if (pathWithSep == null || pathWithSep.contentEquals("")) {
			pathWithSep = App.savesPath;
		}

		File fileIn = new File(pathWithSep + dealName);
		if (!fileIn.exists()) {
			return false;
		}

		Lin lin = null;

		FileInputStream fis = null;

		try {
			fis = new FileInputStream(fileIn);

			lin = new Lin(fis, pathWithSep, dealName, App.dotLinExt);

			fis.close();

		} catch (IOException i) {
			System.out.println("lin file rejected, bad format? - but lets try to process it anyway");
			// i.printStackTrace();
			try {
				fis.close();
			} catch (IOException e) {
				System.out.println("lin file fis - failed to close");
				e.printStackTrace();
			}
			// return false;
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (lin == null) {
			return false;
		}

		lin.filename = fileIn.getName();

		App.mg = new MassGi(lin);

		App.switchToNewMassGi("");

		return true;
	}

	/**   
	 */
	public static boolean readLinResourseIfExists(String jarName, String resName) {
		// ==============================================================================================

		Lin lin = null;

		InputStream is = null;

		URL[] urls = null;
		try {

			System.out.println("jarname " + jarName);

			urls = new URL[] { new File(jarName).toURI().toURL() };
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		/* We still need to run on MACs (Snow Leopard and earlier that use Java 6
		 * and Java 6 has no way of freeing the loaded class, ven if one could work
		 * out WHEN that should be done.
		 */
		@SuppressWarnings("resource")
		URLClassLoader classLoader = new URLClassLoader(urls);

		try {
			is = classLoader.getResourceAsStream(resName);

			lin = new Lin(is, "_not_", "_used_", App.dotLinExt);

			is.close();

		} catch (IOException i) {
			System.out.println("lin resource rejected, bad format? - but lets try to process it anyway");
			try {
				is.close();
			} catch (IOException e) {
				System.out.println("lin resourse  is - failed to close");
				// e.printStackTrace();
			}
			// return false;
		} catch (Exception e) {
			e.printStackTrace();
		}

//		try {
//			classLoader.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

		if (lin == null) {
			return false;
		}

		lin.filename = resName;

		App.mg = new MassGi(lin);

		App.switchToNewMassGi("");

		return true;
	}

	/**
	 */
	public static File copyFileToAutoSavesFolderIfLinFileExists(String pathAndName) {
		// ==============================================================================================

		File fileIn = new File(pathAndName);
		if (!fileIn.exists()) {
			return null;
		}

		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd__HH-mm-ss");
		String asFile = App.autoSavesPath + "01  " /* number removed later */+ fileIn.getName() + "                          " + sdfDate.format(new Date())
				+ App.getDotAndExtension(fileIn.getName());

		File fileOut = new File(asFile);

		try {
			InputStream in = new FileInputStream(fileIn);
			OutputStream out = new FileOutputStream(fileOut);

			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		} catch (IOException e) {
			return null;
		}

		return fileOut;
	}

}
