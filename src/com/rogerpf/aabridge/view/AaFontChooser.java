package com.rogerpf.aabridge.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.rogerpf.aabridge.controller.App;

//  @SuppressWarnings("rawtypes")
public class AaFontChooser extends JComboBox<Object> {

	private static final long serialVersionUID = 1L;

	public void selectFamilyIfPresent(String family) {

		if (selectFamily(family))
			return;

		/* help in case .. */
		if (family.contentEquals("Times Roman")) {
			if (selectFamily("Times New Roman") == false) {
				selectFamily("Times");
			}
		}

	}

	private boolean selectFamily(String family) {
		int count = getItemCount();
		for (int i = 0; i < count; i++) {
			String item = (String) getItemAt(i);
			if (item.contentEquals(family)) {
				setSelectedIndex(i);
				return true;
			}
		}
		return false;
	}

//	@SuppressWarnings("unchecked")
	public AaFontChooser(final Component... components) {

		final String[] families = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

		Arrays.sort(families, new Comparator<String>() {
//            @Override
			public int compare(String f1, String f2) {
				return f1.compareTo(f2);
			}
		});

		for (int i = 0; i < families.length; i++) {
			addItem(families[i]);
		}

		addItemListener(new ItemListener() {
//            @Override
			public void itemStateChanged(ItemEvent e) {
				App.fontfamilyOverride = (String) e.getItem();
			}
		});

		// setRenderer(new FontCellRenderer());
		setRenderer(new MyCellRenderer());
	}

//	private static class FontCellRenderer implements ListCellRenderer<Font> {
//
//		protected DefaultListCellRenderer renderer = new DefaultListCellRenderer();
//
//		public Component getListCellRendererComponent(JList<? extends Font> list, Font font, int index, boolean isSelected, boolean cellHasFocus) {
//
//			final Component result = renderer.getListCellRendererComponent(list, font.getName(), index, isSelected, cellHasFocus);
//
//			//setFontPreserveSize(result, font);
//			return result;
//		}
//	}

//	private static void setFontPreserveSize(final Component comp, Font font) {
//		final float size = comp.getFont().getSize();
//		comp.setFont(font.deriveFont(size));
//	}

	class MyCellRenderer extends JLabel implements ListCellRenderer<Object> {
		private static final long serialVersionUID = 1L;

		// Color selectedColor = new Color(113, 142, 170);
		Color selectedColor = new Color(113, 142, 170);

		public MyCellRenderer() {
			setOpaque(true);
		}

		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

			setText(value.toString());

			Color background;
			Color foreground;

			// check if this cell represents the current DnD drop location
			JList.DropLocation dropLocation = list.getDropLocation();
			if (dropLocation != null && !dropLocation.isInsert() && dropLocation.getIndex() == index) {

				background = Color.BLUE;
				foreground = Color.WHITE;

				// check if this cell is selected
			}
			else if (isSelected) {
				background = selectedColor;
				foreground = Color.WHITE;

				// unselected, and not the DnD drop location
			}
			else {
				background = Color.WHITE;
				foreground = Color.BLACK;
			}
			;

			setBackground(background);
			setForeground(foreground);

			return this;
		}
	}

}
