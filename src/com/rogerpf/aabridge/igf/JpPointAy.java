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

import java.util.ArrayList;

import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.controller.q_;
import com.rogerpf.aabridge.igf.MassGi.GraInfo;

/**
 */
class JpPointAy extends ArrayList<JpPoint> {
	// ---------------------------------- CLASS -------------------------------------
	private static final long serialVersionUID = 1L;
	int next_jp = 0;

	/**
	 */
	public JpPointAy(MassGi mg) {
		// =============================================================================
		/**
		 * We parse the (newly created) GraInfo array looking for qx'es and their related pg's
		 */

		int tot_pg = 0;

		{
			JpPoint jp = null;

			for (GraInfo gi : mg.giAy) {
				int t = gi.qt;

				if (t == q_.qx || jp == null) {
					String name = (t == q_.qx) ? gi.bb.get(0) : (jp == null ? "B" : "");
					jp = new JpPoint(gi, name);
					add(jp);
					jp.add(gi);
					continue;
				}

				if (t == q_.pg || t == q_.lb) {
					tot_pg++;
					jp.add(gi);
				}
			}
		}

		/** 
		 * We now need to parse all the Jp's and calcualte their locations on the line
		 */
		// System.out.println("JpPoints Start");
		int ave = tot_pg / size();
		int sum_large = 0;
//		int sum_small = 0;
		int count_large = 0;
		int count_small = 0;
		for (JpPoint jp : this) {
			if (jp.size() > ave) {
				sum_large += jp.size();
				count_large++;
			}
			else {
//				sum_small += jp.size();
				count_small++;
			}
		}
		/** the small have to share 5% of the total for their line thinkness and 10% for their gap
		 *  The large share 30% of the total for their thickness and 55% for their gap
		 */

		assert (size() == count_large + count_small);

		float sml_thickness = 0.05f / count_small;
		float lge_thickness = 0.30f / count_large;

		float sml_gap = sml_thickness; // two of these to the complete qx
		float lge_gap = 0.55f / sum_large; // allocated in proportion

		float x = 0.0f;
		for (JpPoint jp : this) {
			// System.out.println(jp.size());
			if (jp.size() > ave) {
				jp.large = true;
				jp.gap = lge_gap;
				jp.mark = lge_thickness;
				jp.x0 = x;
				x += lge_thickness;
				jp.x1 = x;
				x += lge_gap * jp.size();
				jp.x2 = x;
			}
			else {
				jp.large = false;
				jp.gap = sml_gap;
				jp.mark = sml_thickness;
				jp.x0 = x;
				x += sml_thickness;
				jp.x1 = x;
				x += sml_gap * 2;
				jp.x2 = x;
			}
		}
		// System.out.println("JpPoints tot: " + jpPointAy.size() + "  pg tot: " + tot_pg + "   Ave " + ave + "   ");

		@SuppressWarnings("unused")
		int z = 0; // put your breakpoint here
	}

	/**
	 */
	public int relLocToGiIndex(float relLoc) {
		// ============================================================================
		// int index = get((int) ((size() - 1) * relLoc)).gi.index;

		for (int i = size() - 1; i >= 0; i--) {
			JpPoint jp = get(i);

			if (i == 2) {
				@SuppressWarnings("unused")
				int z = 0; // put your breakpoint here
			}

			if (jp.x0 > relLoc)
				continue;

			boolean useFirst = (jp.x1 >= relLoc);

			int n = -1;
			for (GraInfo gi : jp) {
				int t = gi.qt;
				n++;
				if (t != q_.pg && t != q_.lb)
					continue;

				float x = jp.x1 + (jp.large ? (jp.gap * n) : ((jp.gap * 2) * n) / (float) jp.size());

				if (useFirst || x >= relLoc) {
					return gi.index;
				}
			}

			/** 
			 * calc diffs means we have to come down from the top and take the first
			 */

			for (int j = jp.size() - 1; j >= 0; j--) {
				GraInfo gi = jp.get(j);
				int t = gi.qt;
				if (t == q_.pg || t == q_.lb)
					return gi.index;
			}

			break; // !
		}

		/**
		 * We have dropped through !!! so find the first pg or lb
		 */

		for (GraInfo gi : App.mg.giAy) {
			int t = gi.qt;
			if (t == q_.pg || t == q_.lb)
				return gi.index;
		}

		return 0;
	}

//	for (int j = jp.size() - 1; j >= 0 j--) {
//	GraInfo gi = jp.get(j);

	/**
	 */
	public float giIndexToRelLoc(int gi_index) {
		// ============================================================================

		for (int i = size() - 1; i >= 0; i--) {
			JpPoint jp = get(i);

			if (jp.gi.index > gi_index)
				continue;

			int n = -1;
			for (GraInfo gi : jp) {
				n++;
				if (gi.index < gi_index)
					continue;

				if (jp.large)
					return jp.x1 + jp.gap * n;
				else
					return jp.x1 + ((jp.gap * 2) * n) / (float) jp.size();
			}
		}
		return 1.0f;
	}

	/**
	 */
	public JpPoint getLast() {
		// ============================================================================
		return get(size() - 1);
	}

	/**
	 */
	public boolean add(JpPoint jp) {
		// ============================================================================
		jp.index = next_jp++;
		return super.add(jp);
	}
}

/**
 */
class JpPoint extends ArrayList<GraInfo> {
	private static final long serialVersionUID = 1L;
	// ---------------------------------- CLASS -------------------------------------
	int index;
	GraInfo gi;
	String name; // or id ?
	public float x0;
	float x1;
	float x2;
	float gap;
	public float mark;
	public boolean large;

	/**
	 */
	JpPoint(GraInfo gi, String name) {
		// ============================================================================
		this.gi = gi;
		this.name = ((name.trim() + "    ").substring(0, 4)).trim();
		this.large = false;
		x0 = x1 = x2 = gap = mark = 0;
	}
}
