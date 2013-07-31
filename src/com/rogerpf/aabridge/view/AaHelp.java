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

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import version.VersionAndBuilt;

public class AaHelp extends JFrame {

	private static final long serialVersionUID = 1L;

	AaHelp() {
//		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Help for aaBridge    " + VersionAndBuilt.all());
		java.net.URL imageFileURL = AaOuterFrame.class.getResource("aaBridge_proto_icon.png");
		setIconImage(Toolkit.getDefaultToolkit().createImage(imageFileURL));

		JEditorPane editorPane = new RpfEditorPane();
		editorPane.setEditable(false);

		JScrollPane scroller = new JScrollPane(editorPane);
		scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroller.setBorder(BorderFactory.createEmptyBorder());
		setContentPane(scroller);

		java.net.URL helpFileURL = AaHelp.class.getResource("AaHelp.html"); // file with the class
		if (helpFileURL != null) {
			try {
				editorPane.setPage(helpFileURL);
			} catch (IOException e) {
				System.err.println("Attempted to read a bad URL: " + helpFileURL);
			}
		}
		else {
			System.err.println("Couldn't find file");
		}

		// set the outer starting size and position
		setSize(720, 600);
		setLocation(100, 75);

		setVisible(true);
	}
}

class RpfEditorPane extends JEditorPane implements MouseListener {

	private static final long serialVersionUID = 1L;

	RpfEditorPane() {
		addMouseListener(this);
	}

	public void mouseClicked(MouseEvent e) {
		try {
			Desktop.getDesktop().browse(new java.net.URI("http://RogerPf.com"));
		} catch (Exception ev) {
		}
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

}
