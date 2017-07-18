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

	public final static Font internationalFont = Font.decode(""); // A way of getting the default font;
	public final static Font internatBoldFont = internationalFont.deriveFont(Font.BOLD);

//	public final static Font bridgeBoldFont = readResourceFont("BridgeTextBold.ttf");
	public final static Font bridgeTextStdFont = readResourceFont("BridgeTextStandard.ttf");

	public final static Font faceAndSymbolFont = readResourceFont("BridgeCardFaceAndSuitSymbol.ttf");

	/*
	 * The BridgeFaceAndSymbFont has some characters set to blanks with known width (picas)
	 * these are => 10 + => 100 , => 200 - => 300 . => 500 / => 750 space => 1000
	 * 
	 * C => Club Symbol, D => Diamond etc T => "ten" symbol
	 */

	static Font readResourceFont(String fontFileName) {
		Font font = null;
		try {
			InputStream stream = BridgeFonts.class.getResourceAsStream(fontFileName);
			font = Font.createFont(Font.TRUETYPE_FONT, stream);
		} catch (Exception e) {
			System.out.println("Font creation failed for: " + fontFileName);
			font = Font.decode(""); // A way of getting the default font
		}
		return font;
	};

}
