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
package version;

//@formatter:off

public class VersionAndBuilt {

	public static int buildNo = 1628;    static String built = "2013 Sept 14";

	// 2013-09-12  1628  Release 1.2.0.1628  Release  see http://RogerPf.com  for built jar
	// 2013-09-12  1627  Tweek the height of the < and > symbols on buttons
	// 2013-09-12  1626  Cleanups and major release
	// 2013-09-12  1625  Minor adjustments and menu additions
	// 2013-09-10  1624  First Release of aaBridge 1.2  now a pure .lin file player
	// 2013-09-10  1623  Default lin player choice now in help menu
	// 2013-09-09  1622  Internal Release
	//---------------------------------------------
	

	static String ver = "1.2.0";
	
	
	public static String verAndBuildNo() {
		return ver + "." + buildNo;
	}
	
//	public static String all() {
//		return ver + "." + buildNo + " - " + built;
//	}
	
	public static String getVer() {
		return ver;
	}
	
	public static String getBuildNo() {
		return "" + buildNo;
	}
	
	public static String getBuilt() {
		return built;
	}
}
