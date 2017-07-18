/***********************************"*********************************"***********
 * Copyright (c) 2013 Roger Pfister.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Roger Pfister - initial API and implementation
 ******************************************************************************/
package com.rogerpf.aabridge.controller;

import java.awt.FontMetrics;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import javax.swing.JComponent;
import javax.swing.JMenuItem;

import com.rogerpf.aabridge.model.Dir;

// @formatter:off

public class Aaf {

	// -----------------------------------------------------------------------------------------------
	
	// Menubar
	public static String  menubar_books       = "";
		                                      
	// Game                                   
	public static String  game_dealerLetter   = "";
	public static String  game_pass           = "";
	public static String  game_nt             = "";
	public static String  game_youSeat        = "";
	public static String  game_youShort       = "";
	
	public static String  game_hidden         = "";
	public static String  game_passedOut      = "";
	public static String  game_over           = "";
	public static String  game_made           = "";
	public static String  game_down           = "";

	public static final String game_double    = "X";  // aaBridge fixed CONSTANT
	public static final String game_redouble  = "XX"; // aaBridge fixed CONSTANT
                                              
                                             
	// Navbar                                        
	public static String  navbar_start        = "";
	public static String  navbar_bid          = "";
	public static String  navbar_lead         = "";
	public static String  navbar_end          = "";
                                              
	// Main button table                      
	public static String  rhp_isOn            = "";
	public static String  rhp_isOff           = "";
	public static String  rhp_hide            = "";
	public static String  rhp_show            = "";
	public static String  cmdBar_step_AM      = "";
	public static String  cmdBar_flow_AM      = "";
                                              
	// Splash and Red arrow hints             
	public static String  splash_wh1          = "";
	public static String  splash_wh2          = "";
	public static String  splash_wh3          = "";
	public static String  splash_deal1        = "";
	public static String  splash_deal2        = "";
	public static String  splash_deal3        = "";
	public static String  redArrow_canEdit    = "";
	public static String  redArrow_newBoard   = "";
	public static String  redArrow_4Cols      = "";
	public static String  redArrow_dragDiv    = "";
                                              
	// The three 'box' instructions		      
	public static String  instruct_bid        = "";
	public static String  instruct_play       = "";
	public static String  instruct_edit       = "";

	//  The two big hints
	public static String  bigHint_notUsed     = "";
	
	public static String  bigHint_youMay      = "";
	public static String  bigHint_clickThe    = "";
	public static String  bigHint_analyse     = "";
	public static String  bigHint_buttonTop   = "";
	public static String  bigHint_clickAny    = "";
	public static String  bigHint_orYou       = "";

	public static String  bigHint_ifYou       = "";
	public static String  bigHint_click       = "";
	public static String  bigHint_edit        = "";
	public static String  bigHint_undo        = "";
	public static String  bigHint_playThe     = "";
	public static String  bigHint_play        = "";
	public static String  bigHint_andCont     = "";
		
	//  Menu Common 
	public static String  menuCmn_help        = "";
	public static String  menuCmn_newUser     = "";
	public static String  menuCmn_howTo       = "";
	public static String  menuCmn_write       = "";
	public static String  menuCmn_whatGoesOn  = "";
	public static String  menuCmn_inside      = "";
			             
	//  File Menu
	public static String  menuFile_open_D     = "";
	public static String  menuFile_save_D     = "";
	public static String  menuFile_saveAs_D   = "";
	public static String  menuFile_openSF_D   = "";
	public static String  menuFile_openASF_D  = "";
	public static String  menuFile_openCASF_D = "";
	public static String  menuFile_openTMHF_D = "";
	public static String  menuFile_processMH  = "";
	public static String  menuFile_paste_D	  = "";
	public static String  menuFile_exit_D  	  = "";
		
	//  Options Menu
	public static String  menuOpt_newDeals_D  = "";
	public static String  menuOpt_autoPlay_D  = "";
	public static String  menuOpt_seat_D      = "";
	public static String  menuOpt_dfc_D       = "";
	public static String  menuOpt_colors_D    = "";
	public static String  menuOpt_size_D	  = "";
	public static String  menuOpt_red_D	      = "";
	public static String  menuOpt_show_D      = "";
		
	// Help & How Do I Menu
	public static String  helpMenu_howdoI     = "";
	
	public static String fwdArrow             = ">";

	// Questions
	public static String  quest_youClk        = "";
	public static String  quest_inCor         = "";
	public static String  quest_cor           = "";
	public static String  quest_yes           = "";
	public static String  quest_no            = "";

	public static String  quest_corHand       = "";
	public static String  quest_left          = "";
	public static String  quest_right         = "";

	// Play Bridge
	public static String  playBridge_click    = "";
	public static String  playBridge_newBoard = "";
	public static String  playBridge_by       = "";	
	public static String  playBridge_toLead   = "";	
	
	// Suit distributions
	public static String  numbersAsWords[]    = { "void", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "eleven", "tweleve", "thirteen" };
	public static String  box_text[]          = { "", "", "", "" };

	
	
	private static void readCachedStrings() {
		// =============================================================
		
		// Menubar
		menubar_books      = Aaf.gT("menubar.books");
			
		// Game 
		game_dealerLetter  = Aaf.gT("game.dealer");
		game_pass          = Aaf.gT("game.pass");
		game_nt            = Aaf.gT("game.nt");                                     
		game_youSeat       = Aaf.gT("game.youSeat");
		game_youShort      = Aaf.gT("game.youShort");
		
		game_hidden        = Aaf.gT("game.hidden");
		game_passedOut     = Aaf.gT("game.passedOut");
		game_over          = Aaf.gT("game.over");
		game_made          = Aaf.gT("game.made");
		game_down          = Aaf.gT("game.down");
				
		// Navbar                                       
		navbar_start       = Aaf.gT("navbar.start");
		navbar_bid         = Aaf.gT("navbar.bid");
		navbar_lead        = Aaf.gT("navbar.lead");
		navbar_end         = Aaf.gT("navbar.end");
		
		// Main button table
		rhp_isOn           = Aaf.gT("rhp.isOn");
		rhp_isOff          = Aaf.gT("rhp.isOff");
		rhp_hide           = Aaf.gT("rhp.hide");
		rhp_show           = Aaf.gT("rhp.show");		
		
		// Splash and Red arrow hints
		splash_wh1		   = Aaf.gT("splash.wheel1"); 
		splash_wh2		   = Aaf.gT("splash.wheel2");  		
		splash_wh3		   = Aaf.gT("splash.wheel3");
		splash_deal1	   = Aaf.gT("splash.deal1"); 
		splash_deal2	   = Aaf.gT("splash.deal2");  		
		splash_deal3	   = Aaf.gT("splash.deal3");
		redArrow_canEdit   = Aaf.gT("redArrow.canEdit"); 
		redArrow_newBoard  = Aaf.gT("redArrow.newBoard"); 
		redArrow_4Cols     = Aaf.gT("redArrow.4Cols"); 
		redArrow_dragDiv   = Aaf.gT("redArrow.dragDiv"); 
			
		// The three 'box' instructions		
		instruct_bid       = "" 
							 + Aaf.gT("instruct.bid1") + "\n"
							 + Aaf.gT("instruct.bid2") + "\n"
							 + Aaf.gT("instruct.bid3") + "\n"
							 + Aaf.gT("instruct.bid4") + "\n"
							 + Aaf.gT("instruct.bid5");
		
		instruct_play      = ""
				             + Aaf.gT("instruct.play1") + "\n"
							 + Aaf.gT("instruct.play2") + "\n"
							 + Aaf.gT("instruct.play3") + "\n"
							 + Aaf.gT("instruct.play4") + "\n"
							 + Aaf.gT("instruct.play5");
							 
		instruct_edit      = ""
				             + Aaf.gT("instruct.edit1") + "\n"
		                     + Aaf.gT("instruct.edit2") + "\n"
		                     + Aaf.gT("instruct.edit3") + "\n"
		                     + Aaf.gT("instruct.edit4") + "\n"
		                     + Aaf.gT("instruct.edit5");

		
		// The two big hints
		bigHint_notUsed    = Aaf.gT("bigHint.notUsed"); 
		
		bigHint_youMay     = Aaf.gT("bigHint.youMay"); 
		bigHint_clickThe   = Aaf.gT("bigHint.clickThe"); 
		bigHint_analyse    = Aaf.gT("bigHint.analyse"); 
		bigHint_buttonTop  = Aaf.gT("bigHint.buttonTop"); 
		bigHint_clickAny   = Aaf.gT("bigHint.clickAny"); 
		bigHint_orYou      = Aaf.gT("bigHint.orYou"); 			
		
		bigHint_ifYou      = Aaf.gT("bigHint.ifYou");     
		bigHint_click      = Aaf.gT("bigHint.click");    
		bigHint_edit       = Aaf.gT("bigHint.edit");     
		bigHint_undo       = Aaf.gT("bigHint.undo");     
		bigHint_playThe    = Aaf.gT("bigHint.playThe");
		bigHint_play       = Aaf.gT("bigHint.play");
		bigHint_andCont    = Aaf.gT("bigHint.andCont");
				
		// Menu Common
		menuCmn_help       = Aaf.gT("menuCmn.help");   
		menuCmn_newUser    = Aaf.gT("menuCmn.newUser");   
		menuCmn_howTo      = Aaf.gT("menuCmn.howTo");    
		menuCmn_write      = Aaf.gT("menuCmn.write");    
		menuCmn_whatGoesOn = Aaf.gT("menuCmn.whatGoesOn"); 
		menuCmn_inside     = Aaf.gT("menuCmn.inside");  

		//  File Menu
		JMenuItem menuItem = new JMenuItem();
		String mid = "  -     ";
		int firstPartLen = Aaf.getMetricsLength(menuItem, Aaf.gT("menuFile.openSF")) + App.menuTabExtra;		
		menuFile_open_D    = spacedOut(menuItem, Aaf.gT("menuFile.open"),    firstPartLen) + mid + Aaf.gT("menuFile.open_TT");
		menuFile_save_D    = spacedOut(menuItem, Aaf.gT("menuFile.save"),    firstPartLen) + mid + Aaf.gT("menuFile.save_TT");
		menuFile_saveAs_D  = spacedOut(menuItem, Aaf.gT("menuFile.saveAs"),  firstPartLen) + mid + Aaf.gT("menuFile.saveAs_TT");
		menuFile_openSF_D  = spacedOut(menuItem, Aaf.gT("menuFile.openSF"),  firstPartLen) + mid + Aaf.gT("menuFile.openSF_TT");
		menuFile_openASF_D = Aaf.gT("menuFile.openASF");
		menuFile_openTMHF_D = Aaf.gT("menuFile.openTMHF");
		menuFile_openCASF_D = Aaf.gT("menuFile.openCASF");
		menuFile_processMH = Aaf.gT("menuFile.processMH");
		menuFile_paste_D   = spacedOut(menuItem, Aaf.gT("menuFile.paste"),   firstPartLen) + mid + Aaf.gT("menuFile.paste_TT");
		menuFile_exit_D    = Aaf.gT("menuFile.exit");
				
		//  Options Menu
		firstPartLen = Aaf.getMetricsLength(menuItem, Aaf.gT("menuOpt.size")) + App.menuTabExtra;
		menuOpt_newDeals_D = spacedOut(menuItem, Aaf.gT("menuOpt.newDeals"), firstPartLen) + mid + Aaf.gT("menuOpt.newDeals_TT");
		menuOpt_autoPlay_D = spacedOut(menuItem, Aaf.gT("menuOpt.autoPlay"), firstPartLen) + mid + Aaf.gT("menuOpt.autoPlay_TT");
		menuOpt_seat_D     = spacedOut(menuItem, Aaf.gT("menuOpt.seat"),     firstPartLen) + mid + Aaf.gT("menuOpt.seat_TT")    ;
		menuOpt_dfc_D      = spacedOut(menuItem, Aaf.gT("menuOpt.dfc"),      firstPartLen) + mid + Aaf.gT("menuOpt.dfc_TT")     ;
		menuOpt_colors_D   = spacedOut(menuItem, Aaf.gT("menuOpt.colors"),   firstPartLen) + mid + Aaf.gT("menuOpt.colors_TT")  ;
		menuOpt_size_D	   = spacedOut(menuItem, Aaf.gT("menuOpt.size"),     firstPartLen) + mid + Aaf.gT("menuOpt.size_TT")    ;
		menuOpt_red_D	   = spacedOut(menuItem, Aaf.gT("menuOpt.red"),      firstPartLen) + mid + Aaf.gT("menuOpt.red_TT")     ;
		menuOpt_show_D     = spacedOut(menuItem, Aaf.gT("menuOpt.show"),     firstPartLen) + mid + Aaf.gT("menuOpt.show_TT")    ;
		
		//  Help & How do I menu
		helpMenu_howdoI    = Aaf.gT("helpMenu.howdoI");
		
		// Common Cmdbar
		fwdArrow           = ">";
		cmdBar_step_AM     = Aaf.addAngleBracket(Aaf.gT("cmdBar.step"), fwdArrow, 4);
		cmdBar_flow_AM     = Aaf.addAngleBracket(Aaf.gT("cmdBar.flow"), fwdArrow, 4);
			
		// Questions
		quest_youClk       = Aaf.gT("quest.youClk");
		quest_inCor        = Aaf.gT("quest.inCor");
		quest_cor          = Aaf.gT("quest.cor");
		quest_yes          = Aaf.gT("quest.yes");
		quest_no           = Aaf.gT("quest.no");

		quest_corHand      = Aaf.gT("quest.corHand");
		quest_left         = Aaf.gT("quest.left");
		quest_right        = Aaf.gT("quest.right");

		String s[]         = Aaf.gT("quest.numbers").split("\\s*,\\s*");
		for (int i=0; i < s.length; i++) {
			if (i >= numbersAsWords.length)
				break;
			if (s[i].isEmpty() == false)
				numbersAsWords[i] = s[i];
		}

		// Play Bridge
		playBridge_click    = Aaf.gT("playBridge.click");
		playBridge_newBoard = Aaf.gT("playBridge.newBoard");
		playBridge_by       = Aaf.gT("playBridge.by");
		playBridge_toLead   = Aaf.gT("playBridge.toLead");
	}
	
	
	private final static String s4 = "    ";
	// -----------------------------------------------------------------------------------------------

	private static String addAngleBracket(String text, String fa, int base_len) {
		if (text.length() < base_len) {
			text = s4.substring(text.length()) + text;
		}
		if (text.length() == base_len) {
			text += "   " + fa;
		}
		else if (text.length() == base_len + 1) {
			text += "  " + fa;
		}
		else {
			text += " " + fa;
		}
		return text;
	}


	public static int getMetricsLength(JComponent c, String s) {
		// =============================================================
		FontMetrics fm = c.getFontMetrics(c.getFont());
		return fm.stringWidth(s);
	}

	public static String spacedOut(JComponent c, String s, int target) {
		// =============================================================
		FontMetrics fm = c.getFontMetrics(c.getFont());
		for (int i = 0; i < 100; i++) { // for safety
			int width = fm.stringWidth(s);
			if (width >= target) 
				break;
			s += " ";
		}
		return s;
	}

	public static final String aa__dot_for_test = "\u25cf";;

	public static String iso_lang_OS = "";
	public static String iso_lang_req = "";
	public static String iso_lang_active = "";
	public static String iso_deck_lang = "";

	public static final String bundleRoot = "UTF-8_properties";
	public static final String bundleAltDeck = "alternate_deck_menu";
	public static final String name_langlist_and_menu = "langlist_and_menu";
	public static final String bundle_prefix = "language_";
	public static final String bundle_dot_ext = ".properties";

	public static final String bundleName_fallback = bundle_prefix + "en_US";
	public static ResourceBundle bundle_fallback = null;

	public static String bundleName_locale = "";
	public static ResourceBundle bundle_locale = null;
	public static boolean locale_bundle_report_missing = true;
	public static boolean locale_bundle_report_blank = true;

	public static LanguageList langList = new LanguageList();
	public static LangdeckList rankList = new LangdeckList();

	public static void loadPreferences(Preferences prefs) {
		// =============================================================
		iso_lang_req = prefs.get("iso_lang_req_r2", "auto");
		iso_deck_lang = prefs.get("iso_deck_lang_r2", "default");
	}

	public static void savePreferences(Preferences prefs) {
		// =============================================================
		prefs.put("iso_lang_req_r2", iso_lang_req);
		prefs.put("iso_deck_lang_r2", iso_deck_lang);
	}

	public static void LoadPrefsPostProcess() {
		// =============================================================
		langList.fillFromLanglistBundle(bundleRoot, name_langlist_and_menu);
		
		validateLocale_readBundles();

		rankList.fillFromDecklistBundle(bundleRoot + App.bundleSep + bundleAltDeck);

		String s = Aaf.gT("game.nsew");
		s = (s.startsWith(Aaf.aa__dot_for_test) ? s.substring(1) : s);

		Dir.initLangDirNSEW(s);
		Dir.initLangDirNSEW(gT("game.north"), gT("game.south"), gT("game.east"), gT("game.west"));

		readCachedStrings();
		
		langList.get(0).setMenuText(Aaf.gT("menuLang.automatic")) ;	
	}

	public static String readfirstlineOfFileOrRes(String resName, String path, char res_type) {
		// ==============================================================================================
		try {
			URLClassLoader betterLoader = null;
			InputStream is;
			if (res_type == 'f') {
				is = new FileInputStream(path);
			}
			else {
				betterLoader = Aaa.makeJarZipLoader(path);
				is = betterLoader.getResourceAsStream(resName);
			}
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			String s = br.readLine();
			br.close();
			isr.close();
			is.close();
			if (betterLoader != null && !App.using_java_6) {
				betterLoader.close();
			}
			return s;
		} catch (Exception e) {
			System.out.println("A1 - cant read " + res_type + "  " + path + "  " + resName);
		}

		return "A1_read_fail";
	}

	public static void validateLocale_readBundles() {
		// =============================================================

		try {
			bundle_fallback = Utf8ResourceBundle.getBundle(bundleRoot + App.bundleSep + bundleName_fallback);
		} catch (Exception e) {
			System.out.println("Failed to read fallback bundle: " + bundleName_fallback + Aaf.bundle_dot_ext);
		}

		Locale locale = Locale.getDefault();

		iso_lang_OS = locale.getLanguage() + "_" + locale.getCountry();
		
// 		iso_lang_OS = "pt_PT";  // testing only

		String iso_lang_desired = "";

		if (iso_lang_req.contentEquals("auto")) {
			iso_lang_desired = iso_lang_OS;
		}
		else {
			iso_lang_desired = iso_lang_req;
		}

		for (LanguageList.LangEntry entry : langList) {
			if (entry.automatic)
				continue;		
			if (entry.iso_lang.contentEquals(iso_lang_desired)) {
				iso_lang_active = iso_lang_desired;
				break;
			}
		}

		if (iso_lang_active.isEmpty()) {
			String lang2letter = iso_lang_desired.substring(0, 2);
			for (LanguageList.LangEntry entry : langList) {
				if (entry.automatic)
					continue;
				if (entry.iso_lang.startsWith(lang2letter)) {
					iso_lang_active = entry.iso_lang;
					break;
				}
			}
		}

		if (iso_lang_active.isEmpty()) {
			iso_lang_active = "en_US";
		}

		if ((iso_lang_active.length() == 5) && !iso_lang_active.contentEquals("en_US")) {

			locale_bundle_report_missing = true;
			locale_bundle_report_blank = true;
			try {
				bundleName_locale = bundle_prefix + iso_lang_active;
				bundle_locale = Utf8ResourceBundle.getBundle(bundleRoot + App.bundleSep + bundleName_locale);
				try {
					String s = bundle_locale.getString("control.reportMissing").trim();
					locale_bundle_report_missing = (!s.contentEquals("false"));
				} catch (Exception e) {
				}
				try {
					String s = bundle_locale.getString("control.reportBlank").trim();
					locale_bundle_report_blank = (!s.contentEquals("false"));
				} catch (Exception e) {
				}
			} catch (Exception e) {
			}
		}

		System.out.println("Language    OS: " + iso_lang_OS + "   Requested: " + iso_lang_req + "   Selected: " + iso_lang_active + "     Deck: " + iso_deck_lang);
		
		// Optermization for fast look up
		App.isUsing__en_US = iso_lang_active.contentEquals("en_US");

	}

	public static void reportMissingResoure(MissingResourceException e, String bundleName) {
		// =============================================================
		try {
			int from = e.getMessage().indexOf("key");
			String s = e.getMessage().substring(from + 3);
			System.out.println("Missing key: " + s + "    not in file: " + bundleName + Aaf.bundle_dot_ext);
		} catch (Exception ex) {
		}
	}

	public static void reportEmptyResoure(String key, String bundleName) {
		// =============================================================
		try {
			System.out.println("Key: " + key + "  exists but is empty,  in file: " + bundleName + Aaf.bundle_dot_ext);
		} catch (Exception e) {
		}
	}


//	static int count = 0;

	public static String gT(String key) {
		// =============================================================
//		System.out.println(++count);

		String prefix = (App.runningExpanded & App.showTheDotTest) ? Aaf.aa__dot_for_test : "";

		if (bundle_locale != null) {
			try {
				String s = bundle_locale.getString(key);
				String fields[] = new String[3];
				String sClean = prefix + removeFieldsAndLeftTrim(s, fields, bundleName_locale, key);

				if (fields[2].contains("blank"))  { 
					return "";
				}
				else if (sClean.isEmpty() == false) {
					return sClean;
				}
				else if (bundle_fallback == null || locale_bundle_report_blank) {
					reportEmptyResoure(key, bundleName_locale); // only report if there is no fallback+
				}
			} catch (MissingResourceException e) {
				if (locale_bundle_report_missing)
					reportMissingResoure(e, bundleName_locale);
			} catch (Exception e) {
			}
		}


		if (bundle_fallback != null) {
			try {
				String s = bundle_fallback.getString(key);
				String fields[] = new String[3];
				String sClean = prefix + removeFieldsAndLeftTrim(s, fields, bundleName_fallback, key);
				
				if (fields[2].contains("blank"))  { 
					return "";
				}
				else if (sClean.isEmpty())
					reportEmptyResoure(key, bundleName_fallback);
				return sClean;

			} catch (MissingResourceException e) {
				reportMissingResoure(e, bundleName_fallback);
			} catch (Exception e) {
			}
		}

		return ".?.";
	}
	
	static final String delim = "<<<T>>>";

	public static String removeFieldsAndLeftTrim(String sIn, String fields[], String bname, String prop) {
		// =============================================================
		fields[0] = ""; // <> translator id
		fields[1] = ""; // [] width guide
		fields[2] = ""; // {} in formation
		
		int k = sIn.indexOf(delim);
		if (k > -1) {
			String t = sIn.substring(k + delim.length());
			int r = ltrim(t).length() - t.trim().length();
			sIn = sIn.substring(0, k) + spaces(r);
		}

		boolean inside = false;
		String content = "";
		int cutFrom = 0;
		for (int i = 0; i < sIn.length(); i++) {
			char c = sIn.charAt(i);
			if (c == ' ') {
				if (inside)
					break;
				else
					continue; // ie strip spaces
			}

			if (c == '<' || c == '{' || c == '[') {
				if (inside) {
					System.out
							.println("Nested  <, {  or  [   found inside PropertyStr / translation  PropName: " + prop + "    file: " + bname + Aaf.bundle_dot_ext);
				}
				inside = true;
				continue;
			}

			if (c == '>' || c == '}' || c == ']') {
				if (inside == false) {
					System.out.println("Extra  >, }  or  ]   found  see  PropertyStr / translation  PropName:  PropName: " + prop + "    file: " + bname
							+ Aaf.bundle_dot_ext);
					break;
				}
				else {
					if (c == '>') {
						fields[0] = content;
					}
					else if (c == ']') {
						fields[1] = content;
					}
					else if (c == '}') {
						fields[2] = content;
					}
					cutFrom = i + 1;
				}
				content = "";
				inside = false;
				continue;
			}
			
            if (inside) {
                content += "" + c;
                continue;
            }

			cutFrom = i;
			break;
		}

		return sIn.substring(cutFrom);
	}
	
	
	public static String spaces(int n) {
		// =============================================================
		char[] chars = new char[n];
		Arrays.fill(chars, ' ');
		return new String(chars);
	}

	public static String ltrim(String s) {
		// =============================================================
		int i = 0;
		while (i < s.length() && Character.isWhitespace(s.charAt(i))) {
			i++;
		}
		return s.substring(i);
	}

	public static String has_iso_lang(String sFile, boolean[] ans, String sep) {
		// =============================================================
		ans[0] = false;
		ans[1] = false;

		int chopFrom = sFile.lastIndexOf(sep); // yes always / as we are catering for . zip encoding

		sFile = sFile.substring(chopFrom + sep.length());

		int chopAt = sFile.lastIndexOf('.');
		for (LanguageList.LangEntry entry : langList) {
			if (entry.automatic)
				continue;
			int ind = sFile.indexOf("__" + entry.iso_lang + "__");
			if (ind > -1) {
				chopAt = ind;
				ans[0] = true;
				ans[1] = entry.iso_lang.contentEquals(iso_lang_active);
				break;
			}
		}

		int i;
		for (i = 0; ('0' <= sFile.charAt(i) && sFile.charAt(i) <= '9'); i++) {
		}
		;
		String s = sFile.substring(i, chopAt);
		s = s.replace('_', ' ').trim();
		return s;
	}

	public static String has_title_iso_lang(String text, boolean[] ans) {
		// =============================================================
		ans[0] = false;
		ans[1] = false;

		for (LanguageList.LangEntry entry : langList) {
			if (entry.automatic)
				continue;
			String s = "__" + entry.iso_lang + "__";
			int ind = text.indexOf(s);
			if (ind > -1) {
				text = text.substring(0, ind);
				ans[0] = true;
				ans[1] = entry.iso_lang.contentEquals(iso_lang_active);
				break;
			}
		}

		return text;
	}

}
