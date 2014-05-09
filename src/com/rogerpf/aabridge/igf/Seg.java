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
package com.rogerpf.aabridge.igf;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.text.AttributedString;
import java.util.ArrayList;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.igf.MassGi.GraInfo;

/**
 */
// ---------------------------------- CLASS ----------------------------------------
// ---------------------------------- CLASS ----------------------------------------
class Ras extends AttributedString {
	// ---------------------------------- CLASS ----------------------------------------
	// ---------------------------------- CLASS ----------------------------------------
	TextLayout tl;
	boolean hidden = false;
	float x;
	float y;
	float height;
	float width;

	Ras(String text) { // Constructor
		// =============================================================================
		super(text);
	}

	Rectangle2D getCorrectedBounds() {
		// =============================================================================
		Rectangle2D bounds = tl.getBounds();
		bounds.setRect(bounds.getX() + x, bounds.getY() + y, bounds.getWidth(), bounds.getHeight());
		return bounds;
	}

	void calcLayout(FontRenderContext frc, float x, float y) {
		// =============================================================================
		this.x = x;
		this.y = y;
		tl = new TextLayout(getIterator(), frc);
		height = tl.getAscent() + tl.getDescent() + tl.getLeading();
		width = tl.getAdvance();
	}
}

/**
 */
// ---------------------------------- CLASS ----------------------------------------
// ---------------------------------- CLASS ----------------------------------------
public class Seg extends ArrayList<Ras> {
	// ---------------------------------- CLASS ----------------------------------------
	// ---------------------------------- CLASS ----------------------------------------
	private static final long serialVersionUID = 1L;
	boolean eol = false; // true tells cetntering that this is the last of the to be centered segments
	boolean open = true; // Closed means that we are still (possibly) on the same line
	boolean lineCentered = false;
	int boxNumber = 0;
	boolean mnBox = false;
	boolean overlappedBox = false;
	float width = 0;
	float height = 0;
	float leftMargin;
	float rightMargin;
	Color fillColor;
	boolean wipeToEndOfScreen = false;
	public Hyperlink hyperlink = null;
	Rectangle2D.Float boxRect;

	/**
	 */
	public Seg(float leftMargin, float rightMargin) { // Constructor
		// =============================================================================
		this.leftMargin = leftMargin;
		this.rightMargin = rightMargin;
	}

	/**
	 * Sets the x for all ras'es and returns the width for the total segment
	 */
	public float setX(float x) {
		// =============================================================================

		float width = 0;

		for (Ras ras : this) {
			ras.x = x + width;
			width += ras.width;
		}
		return width;
	}

	/**
	 */
	public void setHidden() {
		// =============================================================================
		for (Ras ras : this) {
			ras.hidden = true;
		}
	}

	/**
	 */
	public void segAddRas(Ras ras) {
		// =============================================================================
		if (ras.hidden == false) {
			width += ras.width;
			if (ras.height > height)
				height = ras.height;
		}
		add(ras);
	}

	/**
	 */
	public void draw(Graphics2D g2) {
		// =============================================================================
		if (wipeToEndOfScreen) {
//			assert (size() == 1);
			float y = get(0).y;
			Rectangle2D.Float rect = new Rectangle2D.Float(0, y, 9000, 9000); // 9000 is any huge value
			Color fill_color = Aaa.tutorialBackground;
			g2.setColor(fill_color);
			g2.fill(rect);
		}
		else {

			for (Ras ras : this) {
				if (ras.hidden)
					continue;

//				AttributedCharacterIterator charIterator = ras.getIterator();
//				String s = "" + charIterator.first();
//				char c = 0;
//				while ((c = charIterator.next()) != 65535)
//					s += "" + c;
//
//				System.out.println(s); // <<<<<<<<<<<<<<<<<<<<<<<s

				ras.tl.draw(g2, ras.x, ras.y);
			}

		}
	}

}

/**
 */
// ---------------------------------- CLASS ----------------------------------------
// ---------------------------------- CLASS ----------------------------------------
class Segs extends ArrayList<Seg> {
	// ---------------------------------- CLASS ----------------------------------------
	// ---------------------------------- CLASS ----------------------------------------
	private static final long serialVersionUID = 1L;

	SeglinePanel slp;

	/**
	 */
	Segs(SeglinePanel slp) { // Constructor
		// =============================================================================
		this.slp = slp;
	}

	/**
	 */
	Seg getCurSeg() {
		// =============================================================================
		return get(size() - 1);
	}

	/**
	 */
	public Seg addPart(Ras ras, GraInfo gi) {
		// =============================================================================
		return addPart(ras, gi, true /* true => leaveOpen */);
	}

	/**
	 */
	public Seg addPart(Ras ras, GraInfo gi, boolean leaveOpen) {
		// =============================================================================
		Seg seg = getCurSeg();
		int boxNumber = seg.boxNumber; // we pass on the current box number which may be 0

		if (seg.open == false) {
			seg = new Seg(slp.leftMargin, slp.rightMargin);
			seg.boxNumber = boxNumber;
			add(seg);
		}
		seg.segAddRas(ras);
		seg.open = leaveOpen;
		seg.eol = !leaveOpen;

		// leftMargin is captured at construction time (see new Seg above)
		seg.rightMargin = slp.rightMargin;
		seg.lineCentered = gi.capEnv.centered;
		seg.hyperlink = gi.capEnv.hyperlink;

		return seg;
	}

	/**
	 */
	public void addPart_ignoreEOL(Ras ras, GraInfo gi) {
		// =============================================================================
		Seg seg = getCurSeg();
//		if (seg.eol) {
//			seg = new RpfLine(leftMargin, rightMargin);
//			lines.add(seg);
//		}
		seg.segAddRas(ras);

		seg.lineCentered = gi.capEnv.centered;
		seg.rightMargin = slp.rightMargin;
	}

	/**
	 */
	public void centerAsNeeded() {
		// =============================================================================
		int from[] = { -1 };
		int to[] = { -1 };
		while (true) {

			boolean centeringRequired = getNextLine(from, to);

			if (from[0] < 0)
				break;

			if (centeringRequired)
				centerLine(from[0], to[0]);
		}
	}

	/**
	 */
	public boolean getNextLine(int from[], int to[]) {
		// =============================================================================
		from[0] = -1; // says we are done
		int prev = to[0];

		boolean centered = false;

		for (int i = prev + 1; i < size(); i++) {
			Seg seg = get(i);
			centered = centered || seg.lineCentered;
			if (from[0] == -1) {
				from[0] = i;
			}
			to[0] = i;
			if (seg.eol)
				break;
		}

		return centered;
	}

	/**
	 */
	public void centerLine(int from, int to) {
		// =============================================================================

		float width = 0;
		for (int i = from; i <= to; i++) {
			Seg seg = get(i);
			width += seg.width;
		}

		float leftMargin = get(from).leftMargin;
		float rightMargin = get(to).rightMargin;

		float x = ((rightMargin - leftMargin) - width) / 2.0f + leftMargin;

		for (int i = from; i <= to; i++) {
			Seg seg = get(i);
			x = x + seg.setX(x);
		}

	}

	/**
	 */
	public void setBoxToAllHidden(int from) {
		// =============================================================================

		Seg fromSeg = get(from);
		int boxNumber = fromSeg.boxNumber;
		if (boxNumber < 1)
			return;

		for (int i = from; i < size(); i++) {
			Seg seg = get(i);
			if (seg.boxNumber != boxNumber)
				return;
			seg.setHidden();
		}
	}

}
