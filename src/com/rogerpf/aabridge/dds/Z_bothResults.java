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
package com.rogerpf.aabridge.dds;

public class Z_bothResults {
	// ---------------------------------- CLASS -------------------------------------

	public int resp;
	public String errStr;
	public ddTableResults ddTableRes;
	public parResultsMaster parResMaster;

	Z_bothResults() {
		resp = 1; // 1 is dds for OK it worked errStr will be ""
		errStr = "";
		ddTableRes = new ddTableResults();
		parResMaster = new parResultsMaster();
	}

}