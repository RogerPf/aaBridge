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
package com.rogerpf.aabridge.view;

import java.util.Locale;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.model.Deal;
import com.rogerpf.aabridge.model.Dir;
import com.rogerpf.aabridge.model.Zzz;

public class HandDisplayGrid extends JPanel {
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;

	private static final int cellx[] = { 1, 2, 1, 0, }; // start from South - swne
	private static final int celly[] = { 0, 1, 2, 1, }; // as this is the BBO order

	int enteredWidth[] = { 0, 0, 0 };
	int enteredHeight[] = { 0, 0, 0 };

	public boolean shrink = false;

	public int wUnits = 0;
	public int hUnits = 0;

	int width = 0;
	int height = 0;

	public HandDisplayGrid(Deal deal) {
		// ==============================================================================================

		setOpaque(false);

		for (Dir dir : Dir.nesw) {
			if (deal.hands[dir.v].countOriginalCards() > 0) {
				enteredWidth[cellx[dir.v]] = 1;
				enteredHeight[celly[dir.v]] = 1;
			}
		}

		wUnits = enteredWidth[0] + enteredWidth[1] + enteredWidth[2];
		hUnits = enteredHeight[0] + enteredHeight[1] + enteredHeight[2];

		String widthStr = "";
		String heightStr = "";
		for (int i : Zzz.zto2) {
			widthStr += String.format(Locale.US, "[%.2f%%]", (100f * (float) enteredWidth[i]) / wUnits);
			heightStr += String.format(Locale.US, "[%.2f%%]", (100f * (float) enteredHeight[i]) / hUnits);
		}

		setLayout(new MigLayout(App.simple, widthStr, heightStr));

		for (Dir dir : Dir.nesw) {
			if (deal.hands[dir.v].countOriginalCards() > 0) {
				HandDisplayPanel hdp = new HandDisplayPanel(dir, deal, false /* not a question hand */);
				hdp.dealMajorChange(deal);
				add(hdp, App.hm3oneHun + ", cell " + cellx[dir.v] + " " + celly[dir.v]);
				enteredWidth[cellx[dir.v]] = 1;
				enteredHeight[celly[dir.v]] = 1;
			}
		}

		if (hUnits == 3 || wUnits == 3) {
			// System.out.println("HandDisplayGrid - " + widthStr + " " + heightStr + "  ====================================");
			@SuppressWarnings("unused")
			int z = 0;
		}

	}

	private static final float widthFactor = 1.7f;
	private static final float handHeightAsFractionOfTupWidth = 0.09f;
	private static final float shrinkFactor = 0.7f;

	public void setPositionReturnSize(int x, int y, int tupWidth, int wh[]) {
		// ==============================================================================================
		/** We base all size calculations on the WIDTH of the current Tutorial Panel
		 *  Note we are being called at 'paint time' so these value must exist valid
		 */
		if (hUnits == 3 || wUnits == 3) {
			@SuppressWarnings("unused")
			int z = 0;
		}

		float shrinkMe = (shrink ? shrinkFactor : 1.0f);

		width = wh[0] = (int) (tupWidth * handHeightAsFractionOfTupWidth * widthFactor * wUnits * shrinkMe + 0.5f);
		height = wh[1] = (int) (tupWidth * handHeightAsFractionOfTupWidth * hUnits * shrinkMe + 0.5f);

		setBounds(x, y, wh[0], wh[1]);
		// System.out.println("setPositionReturnSize " + x + " " + y + " " + wh[0] + " " + wh[1] + " tup width " + App.tup.getWidth());
	}

}
