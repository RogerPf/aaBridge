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
import java.io.IOException;
import java.io.InputStream;
import java.net.URLClassLoader;

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
			if (file.isFile() && (file.getName().toLowerCase().endsWith(".lin") || file.getName().toLowerCase().endsWith(".pbn"))) {
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
		App.aaBookPanel.matchToAppBook();
		return chapterLoaded;
	}

	/**   
	 */
	public static boolean readLinOrPbnResourseIfExists(String jarOrZipName, String resName) {
		// ==============================================================================================

		boolean fromPbnFile = !resName.toLowerCase().endsWith(App.dotLinExt);

		String origFileName = resName;
		int l = resName.lastIndexOf('/');
		if (l > 0) {
			origFileName = resName.substring(l + 1);
		}

		Lin lin = null;

		try {
			URLClassLoader betterLoader = Aaa.makeJarZipLoader(jarOrZipName);

			InputStream is = betterLoader.getResourceAsStream(resName);

			lin = new Lin(is, "_not_used_", origFileName, fromPbnFile, "");

			is.close();

			if (!App.using_java_6) {
				betterLoader.close();
			}

		} catch (IOException i) {
			System.out.println("lin resource rejected, bad format ?");
		} catch (Exception e) {
			// int z = 0;
		}

		if (lin == null) {
			return false;
		}

		App.mg = new MassGi(lin);

		App.switchToNewMassGi("");

		return true;
	}

	/**   
	 */
	public static boolean readLinOrPbnFileIfExists(String pathWithSep, String dealName, String origSourceFolder) {
		// ==============================================================================================

		if (pathWithSep == null || pathWithSep.contentEquals("")) {
			pathWithSep = App.realSaves_folder;
		}

		File fileIn = new File(pathWithSep + dealName);
		if (!fileIn.exists()) {
			return false;
		}

		Lin lin = null;

		boolean fromPbnFile = !dealName.toLowerCase().endsWith(App.dotLinExt);

		FileInputStream fis = null;

		try {
			fis = new FileInputStream(fileIn);

			lin = new Lin(fis, pathWithSep, dealName, fromPbnFile, origSourceFolder);

			fis.close();

		} catch (IOException i) {
			System.out.println("lin file rejected, bad format ?"); // - but lets try to process it anyway");
			// i.printStackTrace();
			try {
				fis.close();
			} catch (IOException e) {
				System.out.println("lin file fis - failed to close");
				e.printStackTrace();
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (lin == null) {
			return false;
		}

		App.mg = new MassGi(lin);

		App.switchToNewMassGi("");

		return true;
	}

//	/**
//	 */
//	public static File copyFileToTempOtherFolderIfLinFileExists(String pathAndName) {
//		// ==============================================================================================
//
//		File fileIn = new File(pathAndName);
//		if (!fileIn.exists()) {
//			return null;
//		}
//
//		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd__HH-mm-ss");
//		String asFile = App.temp_Other_folder + "01  " /* number removed later */ + fileIn.getName() + "                          " + sdfDate.format(new Date())
//				+ App.getDotAndExtension(fileIn.getName());
//
//		File fileOut = new File(asFile);
//
//		try {
//			InputStream in = new FileInputStream(fileIn);
//			OutputStream out = new FileOutputStream(fileOut);
//
//			// Transfer bytes from in to out
//			byte[] buf = new byte[8 * 1024];
//			int len;
//			while ((len = in.read(buf)) > 0) {
//				out.write(buf, 0, len);
//			}
//			in.close();
//			out.close();
//		} catch (IOException e) {
//			return null;
//		}
//
//		return fileOut;
//	}

}
