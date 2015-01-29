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
package com.rpsd.bridgefonts;

import java.awt.Font;
import java.io.InputStream;

public class BridgeFonts {

	public final static Font bridgeLightFont = readResourceFont("BridgeLight.ttf");
	public final static Font bridgeBoldFont = readResourceFont("BridgeBold.ttf");
	public final static Font bridgeTextStdFont = readResourceFont("BridgeTextStd.ttf");
	public final static Font faceAndSymbFont = readResourceFont("BridgeFaceAndSymbols.ttf");

	/*
	 * The BridgeFaceAndSymbFont has some characters set to blanks with known width (picas)
	 * these are => 10 + => 100 , => 200 - => 300 . => 500 / => 750 space => 1000
	 * 
	 * C => Club Symbol, D => Diamond etc T => "ten" symbol
	 */

	static Font readResourceFont(String fontFileName) {
		Font temp = null;
		try {
			InputStream stream = BridgeFonts.class.getResourceAsStream(fontFileName);
			temp = Font.createFont(Font.TRUETYPE_FONT, stream);
		} catch (Exception e) {
			System.out.println("Font creation failed");
			temp = Font.decode(""); // A way of getting the default font
		}
		return temp;
	};

}
