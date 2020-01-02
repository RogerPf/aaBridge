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
package com.rpsd.ratiolayout;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;

public class PreferredSizeGridLayout extends GridLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BoundableInterface boundableInterface = new PreferredBoundable();

	public PreferredSizeGridLayout() {
		this(1, 0, 0, 0);
	}

	/**
	 * Creates a grid layout with the specified number of rows and columns. All
	 * components in the layout are given equal size.
	 * <p>
	 * One, but not both, of <code>rows</code> and <code>cols</code> can be
	 * zero, which means that any number of objects can be placed in a row or in
	 * a column.
	 * 
	 * @param rows
	 *            the rows, with the value zero meaning any number of rows.
	 * @param cols
	 *            the columns, with the value zero meaning any number of
	 *            columns.
	 */
	public PreferredSizeGridLayout(int rows, int cols) {
		this(rows, cols, 0, 0);
	}

	/**
	 * Creates a grid layout with the specified number of rows and columns. All
	 * components in the layout are given equal size.
	 * <p>
	 * In addition, the horizontal and vertical gaps are set to the specified
	 * values. Horizontal gaps are placed at the left and right edges, and
	 * between each of the columns. Vertical gaps are placed at the top and
	 * bottom edges, and between each of the rows.
	 * <p>
	 * One, but not both, of <code>rows</code> and <code>cols</code> can be
	 * zero, which means that any number of objects can be placed in a row or in
	 * a column.
	 * 
	 * @param rows
	 *            the rows, with the value zero meaning any number of rows.
	 * @param cols
	 *            the columns, with the value zero meaning any number of
	 *            columns.
	 * @param hgap
	 *            the horizontal gap.
	 * @param vgap
	 *            the vertical gap.
	 * @exception IllegalArgumentException
	 *                if the of <code>rows</code> or <code>cols</code> is
	 *                invalid.
	 */
	public PreferredSizeGridLayout(int rows, int cols, int hgap, int vgap) {
		super(rows, cols, hgap, vgap);
	}

	public Dimension preferredLayoutSize(Container parent) {
		synchronized (parent.getTreeLock()) {
//			Insets insets = parent.getInsets();
//			int ncomponents = parent.getComponentCount();
//			int nrows = getRows();
//			int ncols = getColumns();
//
//			if (nrows > 0) {
//				ncols = (ncomponents + nrows - 1) / nrows;
//			}
//			else {
//				nrows = (ncomponents + ncols - 1) / ncols;
//			}
//			int w = 0;
//			int h = 0;
//			for (int i = 0; i < ncomponents; i++) {
//				Component comp = parent.getComponent(i);
//				Dimension d = comp.getPreferredSize();
//				if (w < d.width) {
//					w = d.width;
//				}
//				if (h < d.height) {
//					h = d.height;
//				}
//			}
			return parent.getParent().getSize();
//			return new Dimension(insets.left + insets.right + ncols * w + (ncols - 1) * getHgap(),
//					insets.top + insets.bottom + nrows * h + (nrows - 1) * getVgap());
		}
	}

	/**
	 * Lays out the specified container using this layout.
	 * <p>
	 * This method reshapes the components in the specified target container in
	 * order to satisfy the constraints of the
	 * <code>PreferredSizeGridLayout</code> object.
	 * <p>
	 * The grid layout manager determines the size of individual components by
	 * dividing the free space in the container into equal-sized portions
	 * according to the number of rows and columns in the layout. The
	 * container's free space equals the container's size minus any insets and
	 * any specified horizontal or vertical gap. All components in a grid layout
	 * are given the Minimum of the same size or the preferred size.
	 * 
	 * @see java.awt.Container
	 * @see java.awt.Container#doLayout
	 */
	public void layoutContainer(Container parent) {
		synchronized (parent.getTreeLock()) {
			Insets insets = parent.getInsets();
			int ncomponents = parent.getComponentCount();
			int nrows = getRows();
			int ncols = getColumns();

			if (ncomponents == 0) {
				return;
			}
			if (nrows > 0) {
				ncols = (ncomponents + nrows - 1) / nrows;
			}
			else {
				nrows = (ncomponents + ncols - 1) / ncols;
			}
			int w = parent.getWidth() - (insets.left + insets.right);
			int h = parent.getHeight() - (insets.top + insets.bottom);
			w = (w - (ncols - 1) * getHgap()) / ncols;
			h = (h - (nrows - 1) * getVgap()) / nrows;

			for (int c = 0, x = insets.left; c < ncols; c++, x += w + getHgap()) {
				for (int r = 0, y = insets.top; r < nrows; r++, y += h + getVgap()) {
					int i = r * ncols + c;
					if (i < ncomponents) {
						boundableInterface.setBounds(parent.getComponent(i), x, y, w, h);
					}
				}
			}
		}
	}

	public BoundableInterface getBoundableInterface() {
		return boundableInterface;
	}

	public void setBoundableInterface(BoundableInterface boundableInterface) {
		this.boundableInterface = boundableInterface;
	}

}
