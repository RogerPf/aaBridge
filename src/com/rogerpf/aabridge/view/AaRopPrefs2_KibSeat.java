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

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.Timer;
import javax.swing.border.Border;

import com.rogerpf.aabridge.controller.Aaa;
import com.rogerpf.aabridge.controller.Aaf;
import com.rogerpf.aabridge.controller.App;
import com.rogerpf.aabridge.model.Cc;
import com.rogerpf.aabridge.model.Dir;
import com.rogerpf.aabridge.model.Lin;
import com.rogerpf.aabridge.model.Suit;
import com.rpsd.bridgefonts.BridgeFonts;

import net.miginfocom.swing.MigLayout;

/**   
 */
public class AaRopPrefs2_KibSeat extends ClickPanel implements ItemListener, ActionListener {

	private static final long serialVersionUID = 1L;

	QLabel anyLabel;

	QLabel labelStartsFrom;

	ButtonGroup rbGroupReview = new ButtonGroup();;
	QRadioButton reviewFromBidding;
	QRadioButton reviewFromPlay;
	QCheckBox showOpeningLead;

	QLabel labelLsh_policy;
	ButtonGroup rbGroupPolicy = new ButtonGroup();
	QRadioButton lsh_policy0;
	QRadioButton lsh_policy1;
	QRadioButton lsh_policy2;

	QCheckBox youSeatPartnerVis;

	QLabel labelDlaeQs;

	QButton applyDlae_LHO;
	QButton applyDlae_RHO;
	QButton applyDlae_Declarer;
	QLabel triangleInd;

	QCheckBox sd_dev_visibility;

	QSelfDrawButton btnPinkDot;
	QButton applyDefaults;

	boolean pk_vis = true;

	ButtonGroup pk_or_pap__bg = new ButtonGroup();
	QRadioButton pk;
	QRadioButton pap;

	QLabel papTitle;
	QLabel papHover;

	ButtonGroup pap_LPR_bg = new ButtonGroup();
	QLabel pap_Ptn_lb;
	QRadioButton pap_Ptn;
	QRadioButton pap_LHO;
	QRadioButton pap_RHO;

	ButtonGroup papXesCards = new ButtonGroup();
	QRadioButton papXes;
	QRadioButton papCards;

	QCheckBox papAllSuits;

	QCheckBox papSpades;
	QCheckBox papHearts;
	QCheckBox papDiamonds;
	QCheckBox papClubs;

	QLabel labelPowerKib;
	QLabel rotateHands;

	QCheckBox alwaysShowHidden;
	QCheckBox force_N_HiddenTut;
	QCheckBox force_W_HiddenTut;
	QCheckBox force_E_HiddenTut;
	QCheckBox force_S_HiddenTut;

	QButton twisterLeft;
	QButton twisterRight;

	QCheckBox obeyRtCmd;
	QCheckBox obeyAeCmd;

	public AaRopPrefs2_KibSeat() {
		setOpaque(true); // remember - we derive from ClickPanel which is setOpaque(false)
//		setBackground(SystemColor.control);

		Border bdr0 = BorderFactory.createEmptyBorder(0, 0, 0, 0);
		Border bdr2 = BorderFactory.createEmptyBorder(1, 4, 1, 4);
		Border bdr1 = BorderFactory.createEmptyBorder(1, 3, 1, 0);
		Border bdr4 = BorderFactory.createEmptyBorder(0, 4, 0, 4);
		Border bdr5 = BorderFactory.createEmptyBorder(4, 4, 0, 4);

		setLayout(new MigLayout(App.simple + ", hidemode 1, flowy", "[][]", "[][]"));

		String rbInset = "gapx 7";

		// @formatter:off
		
		add(anyLabel  = new QLabel(Aaf.gT("menuOpt.seat"), ""), "gapx 8, gapy 5");
		    anyLabel.setForeground(Aaa.optionsTitleGreen);
			Font slightlyBiggerFont = anyLabel.getFont().deriveFont(anyLabel.getFont().getSize() * 1.09f);
		    anyLabel.setFont(slightlyBiggerFont);
		    
		// ------------  BIG     Enter the Deal -------------
		
		add(anyLabel  = new QLabel(Aaf.gT("sChoiceTab.enterDeal"), ""), "gapx 0, gapy 2");
	       anyLabel.setForeground(Aaa.optionsTitleGreen);
		   Font biggerFont = anyLabel.getFont().deriveFont(anyLabel.getFont().getSize() * 1.25f);
	       anyLabel.setFont(biggerFont);
	       anyLabel.setForeground(Aaa.optionsTitleGreen);

	    String etd_applies = Aaf.gT("sChoiceTab.etdApplies");

		Border bdr3 = BorderFactory.createEmptyBorder(0, 3, 0, 0);
		
		add(labelStartsFrom  = new QLabel(Aaf.gT("sChoiceTab.etdShows"), ""), "gapx 3, split 2, flowx");
		    labelStartsFrom.setForeground(Aaa.optionsTitleGreen);
		
		add(anyLabel  = new QLabel(etd_applies, ""), "gapx 110");
		    anyLabel.setForeground(Aaa.optionsTitleGreen);
		
		//  -----------  Bidding,   Play    Lead -----------------------
		    
		add(reviewFromBidding= new QRadioButton(this, rbGroupReview, bdr3, App.reviewFromPlay == false,  "p1", Aaf.gT("sChoiceTab.fromBidding"), ""), rbInset );
		add(reviewFromPlay   = new QRadioButton(this, rbGroupReview, bdr3, App.reviewFromPlay == true,   "p1", Aaf.gT("sChoiceTab.fromPlay"), ""), rbInset + ", split 2, flowx" );	
		add(showOpeningLead = new QCheckBox(this, App.showOpeningLead, Aaf.gT("sChoiceTab.fromOpen"), ""), "gapx 5");
		    showOpeningLead.setBorder(bdr1);
		
		String comb =  Aaf.gT("sChoiceTab.non") + "            " + etd_applies;
		String comb_TT = "<html>" + Aaf.gT("sChoiceTab.non") + "<br>" + etd_applies + "</html>";
		
		add(labelLsh_policy   = new QLabel(Aaf.gT("sChoiceTab.non"), comb_TT ), "gapx 3, split 2, flowx, " + (App.onMac ? "gapy 8" : "gapy 16"));
		    labelLsh_policy.setForeground(Aaa.optionsTitleGreen);
		    labelLsh_policy.setFont(slightlyBiggerFont);	

			add(anyLabel  = new QLabel(etd_applies, ""), "gapx 90");
		    anyLabel.setForeground(Aaa.optionsTitleGreen);


		// ------------  Show     leave as is    Hide     --------------
		    
		add(lsh_policy1     = new QRadioButton(this, rbGroupPolicy, bdr3, App.localShowHiddPolicy == 1,  "p1", Aaf.gT("sChoiceTab.show"), ""), rbInset );
		add(lsh_policy2     = new QRadioButton(this, rbGroupPolicy, bdr3, App.localShowHiddPolicy == 2,  "p2", Aaf.gT("sChoiceTab.leaveAs"), ""), rbInset);
		add(lsh_policy0     = new QRadioButton(this, rbGroupPolicy, bdr3, App.localShowHiddPolicy == 0,  "p0", Aaf.gT("sChoiceTab.hide"), ""), rbInset );

		
		// ------------  Ptnr of Kib Seat is set ...     --------------
			
		add(youSeatPartnerVis = new QCheckBox(this, App.youSeatPartnerVis, Aaf.gT("sChoiceTab.pAlways"), Aaf.gT("sChoiceTab.pAlways_TT")), "gapx 3, gapy 2");
		    youSeatPartnerVis.setBorder(bdr1);
		    
	    comb =  "<html>" + Aaf.gT("sChoiceTab.kibSeatF") + "<br>" + Aaf.gT("sChoiceTab.allApplies") + "</html>";
		    
		add(labelDlaeQs       = new QLabel(Aaf.gT("sChoiceTab.kibSeatF"), comb), "gapy 18, gapx 3, split 2, flowx");
		    labelDlaeQs.setForeground(Aaa.optionsTitleGreen);
		    labelDlaeQs.setFont(slightlyBiggerFont);
			
			add(anyLabel  = new QLabel(Aaf.gT("sChoiceTab.allApplies"), ""), "gapx 70");
		    anyLabel.setForeground(Aaa.optionsTitleGreen);

		    
		// -----------   LHO   RHO   Declarer   .... Force kib Seat
		    
		add(applyDlae_LHO = new QButton(this, Aaf.gT("sChoiceTab.lho")), (App.onMac ? "" : "gapx  6, ") + "split 2, flowx");
		add(applyDlae_RHO = new QButton(this, Aaf.gT("sChoiceTab.rho")), (App.onMac ? "" : "gapx 10, "));
		
		if (App.onMac == false) { // no gaps or borders on the MAC - they "break" the buttons appearance or (gaps are not wanted)
			applyDlae_LHO.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));				
			applyDlae_RHO.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));
		}			
				
	    add(applyDlae_Declarer = new QButton(this, Aaf.gT("cmnTab.decl")), "gapx16, split 3, flowx");

	    
	    // ----------   RED triangle --------------------
	        
		add(triangleInd = new QLabel("f" /* f will show as a triangle */,  Aaf.gT("sChoiceTab.triangle_TT")),"gapx 2, hidemode 0"); // will show as a diamond
		Font triangleFont = BridgeFonts.faceAndSymbolFont.deriveFont((float)triangleInd.getFont().getSize() * 1.3f);
		triangleInd.setFont(triangleFont);
		triangleInd.setForeground(Color.red);
		triangleInd.setBorder(bdr3); 
	        
		if (App.onMac == false)
			applyDlae_Declarer.setBorder(BorderFactory.createEmptyBorder(2, 4, 1, 4));
		
		add(anyLabel = new QLabel(Aaf.gT("sChoiceTab.kibSeatp2")),"gapx 98");		  

		
		if (App.study_deal_maker) {
			add(sd_dev_visibility = new QCheckBox(this, App.sd_dev_visibility, Aaf.gT("sChoiceTab.sd_visible")), App.onMac ? "gapy6" : "gapy10");
		}

		
		// ------------   bright PINK dot   -------------
		
	    add(btnPinkDot      = new QSelfDrawButton(this, "" + (char) 0x25cf /* a dot */ + ""), "split 3, flowx");
	    	Font fo = btnPinkDot.getFont();
	    	btnPinkDot.setFont(fo.deriveFont(fo.getSize() * (App.onMac ? 2.0f : 1.2f)));
	        btnPinkDot.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
	        btnPinkDot.setForeground(new Color(0xdd00ff));
	        btnPinkDot.setToolTipText(Aaf.gT("sChoiceTab.applyDef_TT"));

	        
	   // ------------    Apply Defaults  ---------------     

		add(applyDefaults = new QButton(this, Aaf.gT("cmnTab.applyDef")), "gapx 2, " + (App.onMac ? "gapy8" : (App.study_deal_maker ? "gapy8" : "gapy22")));
			applyDefaults.setToolTipText(Aaf.gT("sChoiceTab.applyDef_TT"));			
		if (App.onMac == false)
		    applyDefaults.setBorder(BorderFactory.createEmptyBorder(5, 1, 4, 4));
		
		add(anyLabel = new QLabel(Aaf.gT("sChoiceTab.hoverMsg")), "gapx 90");
		
		
		// -----------    PK  or  PaP
		
		add(pk       = new QRadioButton(this, pk_or_pap__bg, bdr0, pk_vis == true,   "", Aaf.gT("sChoiceTab.pk"), ""), (App.onMac ? "gapy8" : "gapy20") + ", gapx 1, split 3, flowx");
		add(pap      = new QRadioButton(this, pk_or_pap__bg, bdr0, pk_vis == false,  "", Aaf.gT("sChoiceTab.pap"), ""), "gapx 12" );
		add(anyLabel = new QLabel(Aaf.gT("sChoiceTab.pap_ex")), "gapx 105");

		pk.setToolTipText(Aaf.gT("sChoiceTab.pk_or_pap_TT"));
		pap.setToolTipText(Aaf.gT("sChoiceTab.pk_or_pap_TT"));
		

		Border p_bdr_pap = BorderFactory.createEmptyBorder(12, 0, 0, 0);
		Border p_bdr_pk  = BorderFactory.createEmptyBorder( 2, 0, 0, 0);

		
		// -----------    Peek at Partner   
        
	    add(papTitle = new QLabel(Aaf.gT("sChoiceTab.papTitle")), "split 2, flowx");
	    	papTitle.setBorder(p_bdr_pap);
	    	papTitle.setForeground(Aaa.optionsTitleGreen);
	    	papTitle.setFont(slightlyBiggerFont);
	    	papTitle.setToolTipText(Aaf.gT("sChoiceTab.papHover_TT"));
	    
		add(papHover = new QLabel(Aaf.gT("sChoiceTab.papHover")), "gapx 90");
		    papHover.setToolTipText(Aaf.gT("sChoiceTab.papHover_TT"));
	            
		
		add(papXes       = new QRadioButton(this, papXesCards, bdr0, App.papXes == true,   "", Aaf.gT("sChoiceTab.papXes"), ""), ", gapy 2, gapx 6, split 2, flowx");
		add(papCards     = new QRadioButton(this, papXesCards, bdr0, App.papXes == false,  "", Aaf.gT("sChoiceTab.papCards"), ""),"" );
		
		
		add(papAllSuits  = new QCheckBox(this, App.papBits == Suit.papAllSuits,  Aaf.gT("sChoiceTab.papAllSuits"), ""), "gapy 0, gapx 0");
		if (App.onMac == false) { 		
			papAllSuits.setBorder(bdr5); 
		}
		papAllSuits.setForeground(Cc.RedStrong);
	        
		add(papSpades    = new QCheckBox(this, (App.papBits & Suit.papSpades) > 0, Aaf.gT("sChoiceTab.papSpades"), ""),"gapy 0, gapx 5");
		if (App.onMac == false) { 		
			papSpades.setBorder(bdr2);
		}
		papSpades.setForeground(Cc.RedStrong);

		add(papHearts    = new QCheckBox(this, (App.papBits & Suit.papHearts) > 0, Aaf.gT("sChoiceTab.papHearts"), ""),"gapy 0, gapx 5");
		if (App.onMac == false) { 		
			papHearts.setBorder(bdr2);
		}
		papHearts.setForeground(Cc.RedStrong);

		add(papDiamonds  = new QCheckBox(this, (App.papBits & Suit.papDiamonds) > 0, Aaf.gT("sChoiceTab.papDiamonds"), ""),"gapy 0, gapx 5");
		if (App.onMac == false) { 		
			papDiamonds.setBorder(bdr2);
		}
		papDiamonds.setForeground(Cc.RedStrong);

		add(papClubs     = new QCheckBox(this, (App.papBits & Suit.papClubs) > 0, Aaf.gT("sChoiceTab.papClubs"), ""),"gapy 0, gapx 5");
		if (App.onMac == false) { 		
			papClubs.setBorder(bdr2);
		}
		papClubs.setForeground(Cc.RedStrong);
		
		Border bdrP      = BorderFactory.createEmptyBorder(4, 0, 0, 0);
		
		add(pap_Ptn = new QRadioButton(this, pap_LPR_bg, bdrP,  App.pap_who == 2,  "", Aaf.gT("sChoiceTab.Ptn"), ""), "gapx 19, split 2, flowx");
		add(pap_Ptn_lb = new QLabel(Aaf.gT("sChoiceTab.Ptn_ex")), "gapx 130");
		add(pap_LHO = new QRadioButton(this, pap_LPR_bg, bdr0, App.pap_who == 1,  "", Aaf.gT("sChoiceTab.LHO"), ""), "gapx 2, split 2, flowx");
		add(pap_RHO = new QRadioButton(this, pap_LPR_bg, bdr0, App.pap_who == 3,  "", Aaf.gT("sChoiceTab.RHO"), ""), "gapx 12");
		pap_Ptn.setToolTipText(Aaf.gT("sChoiceTab.Ptn_TT"));		
		pap_Ptn_lb.setToolTipText(Aaf.gT("sChoiceTab.Ptn_TT"));		
		pap_LHO.setToolTipText(Aaf.gT("sChoiceTab.Ptn_TT"));
		pap_RHO.setToolTipText(Aaf.gT("sChoiceTab.Ptn_TT"));	
	

	        
		// -----------    Power Kib   

	    add(labelPowerKib = new QLabel(Aaf.gT("sChoiceTab.powerKib"),""));
	        labelPowerKib.setBorder(p_bdr_pk);
	        labelPowerKib.setForeground(Aaa.optionsTitleGreen);
	        labelPowerKib.setFont(slightlyBiggerFont);
	        
		// -----------    Rotate buttons
	        
		add(twisterLeft = new QButton(this, "<"), "gapx 3, gapy 2, split 3, flowx");
			// Border t_bdr = BorderFactory.createEmptyBorder((App.onMac ? 8 : 52), 0, 0, 0);
			// twisterLeft.setBorder(t_bdr);
			twisterLeft.setToolTipText(Aaf.gT("sChoiceTab.anti_TT"));
			twisterLeft.setBorder(bdr4); // even on a MAC  -  they will have to suffer this one "no border" button
		
		add(twisterRight = new QButton(this, ">"), "gapx 5");
			twisterRight.setToolTipText(Aaf.gT("sChoiceTab.clock_TT"));
			// twisterRight.setForeground(Cc.BlueStrong);
			twisterRight.setBorder(bdr4); // even on a MAC  -  they will have to suffer this one "no border" button
	    
		add(rotateHands = new QLabel(Aaf.gT("sChoiceTab.uni")), "gapx 8");
		    rotateHands.setForeground(Cc.RedStrong);
	    		    
		//  ---------- Show all Hands  -------------------   

		add(alwaysShowHidden  = new QCheckBox(this, App.alwaysShowHidden,  Aaf.gT("sChoiceTab.allAlways"), Aaf.gT("sChoiceTab.allAlways_TT")), "gapx 5, gapy 6");
		    alwaysShowHidden.setBorder(bdr0);
		    alwaysShowHidden.setForeground(Cc.RedStrong);

		add(force_N_HiddenTut = new QCheckBox(this, App.force_N_HiddenTut, Dir.getLangDirChar(Dir.North) + "      " + Aaf.gT("sChoiceTab.forceHidden"), ""), "gapx13, gapy4");
    		force_N_HiddenTut.setBorder(bdr2);
    		force_N_HiddenTut.setForeground(Cc.RedStrong);
   
		add(force_W_HiddenTut = new QCheckBox(this, App.force_W_HiddenTut,  "", Dir.getLangDirChar(Dir.West) + "      " + Aaf.gT("sChoiceTab.forceHidden")),  "split 2, flowx");
	        force_W_HiddenTut.setBorder(bdr4);
		    force_W_HiddenTut.setForeground(Cc.RedStrong);
		   
		add(force_E_HiddenTut = new QCheckBox(this, App.force_E_HiddenTut,  Dir.getLangDirChar(Dir.East) + "      " + Aaf.gT("sChoiceTab.forceHidden"), ""));
			force_E_HiddenTut.setBorder(bdr4);
			force_E_HiddenTut.setForeground(Cc.RedStrong);
	
		add(force_S_HiddenTut = new QCheckBox(this, App.force_S_HiddenTut,  Dir.getLangDirChar(Dir.South) + "      " + Aaf.gT("sChoiceTab.forceHidden"), ""), "gapx13");
			force_S_HiddenTut.setBorder(bdr2);
			force_S_HiddenTut.setForeground(Cc.RedStrong);	   
		    
//		Border bdr6 = BorderFactory.createEmptyBorder(12, 0, 0, 0);
//		Border bdr7 = BorderFactory.createEmptyBorder(0, 0, 6, 0);
			
		// -----------  obeyRt   and   obeyAe   ----------- 

		String obeyRt = Aaf.gT("sChoiceTab.obeyRt");
		add(obeyRtCmd = new QCheckBox(this, App.obeyRtCmd, obeyRt), "gapx 3, gapy 15");
		    obeyRtCmd.setToolTipText(obeyRt);
		    obeyRtCmd.setBorder(bdr0);
		    obeyRtCmd.setForeground(Cc.RedStrong);	
		  
		String obeyAe = Aaf.gT("sChoiceTab.obeyAe");
		add(obeyAeCmd = new QCheckBox(this, App.obeyAeCmd, obeyAe), "gapx 3, gapy 6");
	        obeyAeCmd.setToolTipText(obeyAe);
		    obeyAeCmd.setBorder(bdr0);
		    obeyAeCmd.setForeground(Cc.RedStrong);	
		    obeyAeCmd.setVisible(App.devMode);          // devMode only

		// @formatter:on

		showButtonStates();
	}

	public void showButtonStates() {

		boolean en = !App.isStudyDeal();
		boolean enc = en || App.sd_dev_visibility;

		if (App.visualMode == App.Vm_DealAndTutorial) {
			App.mg.refresh_for_youseat_change();
		}

		labelStartsFrom.setEnabled(en);
		reviewFromBidding.setEnabled(en);
		reviewFromPlay.setEnabled(en);
		showOpeningLead.setEnabled(en && App.reviewFromPlay);

		labelLsh_policy.setEnabled(enc);
		lsh_policy0.setEnabled(enc);
		lsh_policy1.setEnabled(enc);
		lsh_policy2.setEnabled(enc);
		youSeatPartnerVis.setEnabled(enc);

		labelDlaeQs.setEnabled(en);
		applyDlae_LHO.setEnabled(en);
		applyDlae_RHO.setEnabled(en);
		applyDlae_Declarer.setEnabled(en);

		triangleInd.setVisible(App.dlaeActive);

		btnPinkDot.setEnabled(enc);
		applyDefaults.setEnabled(enc);

		//  PK   PaP

		if (en == false && !App.sd_dev_visibility) {
			pk_vis = true;
		}

		pk.setSelected(pk_vis == true);
		pap.setSelected(pk_vis == false);
		pk.setEnabled(enc);
		pap.setEnabled(enc);

		// Peak at Partner

		papTitle.setVisible(pk_vis == false);
		papHover.setVisible(pk_vis == false);

		pap_Ptn.setVisible(pk_vis == false);
		pap_Ptn_lb.setVisible(pk_vis == false);
		pap_LHO.setVisible(pk_vis == false);
		pap_RHO.setVisible(pk_vis == false);

		papXes.setVisible(pk_vis == false);
		papCards.setVisible(pk_vis == false);

		papAllSuits.setSelected(App.papBits == Suit.papAllSuits);
		papSpades.setSelected((App.papBits & Suit.papSpades) > 0);
		papHearts.setSelected((App.papBits & Suit.papHearts) > 0);
		papDiamonds.setSelected((App.papBits & Suit.papDiamonds) > 0);
		papClubs.setSelected((App.papBits & Suit.papClubs) > 0);

		papAllSuits.setVisible(pk_vis == false);
		papSpades.setVisible(pk_vis == false);
		papHearts.setVisible(pk_vis == false);
		papDiamonds.setVisible(pk_vis == false);
		papClubs.setVisible(pk_vis == false);

		labelPowerKib.setEnabled(enc);

		boolean rt_found = App.rtFound();
		boolean twister_en = (!rt_found || (rt_found && !App.obeyRtCmd)) && !(App.dlaeActive);
		twisterLeft.setEnabled(enc && twister_en);
		twisterRight.setEnabled(enc && twister_en);
		rotateHands.setEnabled(enc && twister_en);

		alwaysShowHidden.setEnabled(enc);

		alwaysShowHidden.setSelected(App.alwaysShowHidden);
		force_N_HiddenTut.setSelected(App.force_N_HiddenTut);
		force_W_HiddenTut.setSelected(App.force_W_HiddenTut);
		force_E_HiddenTut.setSelected(App.force_E_HiddenTut);
		force_S_HiddenTut.setSelected(App.force_S_HiddenTut);

		force_N_HiddenTut.setEnabled(enc);
		force_W_HiddenTut.setEnabled(enc);
		force_E_HiddenTut.setEnabled(enc);
		force_S_HiddenTut.setEnabled(enc);

		obeyRtCmd.setEnabled(en && rt_found && !App.dlaeActive);
		obeyAeCmd.setEnabled(en && App.mg.lin.ae_count > 0);

		// Power Kib

		labelPowerKib.setVisible(pk_vis);
		twisterLeft.setVisible(pk_vis);
		twisterRight.setVisible(pk_vis);
		rotateHands.setVisible(pk_vis);

		alwaysShowHidden.setVisible(pk_vis);

		force_N_HiddenTut.setVisible(pk_vis);
		force_W_HiddenTut.setVisible(pk_vis);
		force_E_HiddenTut.setVisible(pk_vis);
		force_S_HiddenTut.setVisible(pk_vis);

		obeyRtCmd.setVisible(pk_vis);
		obeyAeCmd.setVisible(pk_vis && App.devMode);          // devMode only
	}

	public void clear_dlaeActive(boolean clear_alwaysShowHidden) {

		App.dlaeActive = false;

		if (clear_alwaysShowHidden) {
			App.alwaysShowHidden = false;
			App.force_N_HiddenTut = false;
			App.force_W_HiddenTut = false;
			App.force_E_HiddenTut = false;
			App.force_S_HiddenTut = false;
		}
		showButtonStates();
	}

	public void setDlaeCommon() {

		App.dlaeActive = true; // Actual dlae val    LHO, Delcarer, RHO   set by caller

		App.reviewFromPlay = false;
		App.showOpeningLead = true;
		reviewFromBidding.setSelected(!App.reviewFromPlay);
		reviewFromPlay.setSelected(App.reviewFromPlay);
		showOpeningLead.setSelected(App.showOpeningLead);

		App.localShowHiddPolicy = 0; // hide
		lsh_policy1.setSelected(false); // 1 = show
		lsh_policy2.setSelected(false); // 2 = no change
		lsh_policy0.setSelected(true);  // 0 = hide

		// App.localShowHidden = true; // well this is the initial default

		App.youSeatPartnerVis = false;
		youSeatPartnerVis.setSelected(App.youSeatPartnerVis);

		App.alwaysShowHidden = false;
		App.force_N_HiddenTut = false;
		App.force_W_HiddenTut = false;
		App.force_E_HiddenTut = false;
		App.force_S_HiddenTut = false;
	}

	public boolean is_compass_or_showAlwaysHidden_Set() {
		// @formatter:off
		return App.force_N_HiddenTut 
		   ||  App.force_W_HiddenTut
		   ||  App.force_E_HiddenTut
		   ||  App.force_S_HiddenTut
		   ||  App.alwaysShowHidden;
		// @formatter:on
	}

	public void clear_compass_and_showAlwaysHidden() {
		App.force_N_HiddenTut = false;
		App.force_W_HiddenTut = false;
		App.force_E_HiddenTut = false;
		App.force_S_HiddenTut = false;
		App.alwaysShowHidden = false;

		force_N_HiddenTut.setSelected(App.force_N_HiddenTut);
		force_W_HiddenTut.setSelected(App.force_W_HiddenTut);
		force_E_HiddenTut.setSelected(App.force_E_HiddenTut);
		force_S_HiddenTut.setSelected(App.force_S_HiddenTut);
		alwaysShowHidden.setSelected(App.alwaysShowHidden);
	}

	/**
	*/
	public Timer papTimer = new Timer(800, new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			// =============================================================
			papTimer.stop();

			App.papBits = 0;

			showButtonStates();
		}
	});

	public void actionPerformed(ActionEvent e) {

		if (App.allConstructionComplete == false)
			return;
		boolean dlae_pressed = false;

		Object source = e.getSource();

		if (source == applyDlae_LHO) {
			setDlaeCommon();
			App.dlaeValue = App.dlae_LHO; // LHO
			dlae_pressed = true;
		}

		else if (source == applyDlae_RHO) {
			setDlaeCommon();
			App.dlaeValue = App.dlae_RHO; // RHO
			dlae_pressed = true;
		}

		else if (source == applyDlae_Declarer) {
			setDlaeCommon();
			App.dlaeValue = App.dlae_Declarer; // Declarer
			dlae_pressed = true;
		}

		else if (source == applyDefaults || source == btnPinkDot) {
			applyDefaults();
			App.frame.rop.p1_AutoPlay.applyDefaults();

			App.savePreferences();
			App.calcCompassPhyOffset();
			App.gbp.dealMajorChange();
		}

		else if (source == twisterLeft) {
			App.allTwister_left();
			App.calcCompassPhyOffset();
			App.gbp.dealMajorChange();
		}

		else if (source == twisterRight) {
			App.allTwister_right();
			App.calcCompassPhyOffset();
			App.gbp.dealMajorChange();
		}

		if (App.allConstructionComplete) {

			if (dlae_pressed) {
				if (App.isVmode_InsideADeal()) {
					App.compassAllTwister = 0;
					Dir seat = App.calcCompassPhyOffset();
					if (seat != null) {
						App.youSeatHint = App.deal.contractCompass.rotate(App.dlaeValue - 2);
						App.deal.youSeatHint = App.deal.contractCompass.rotate(App.dlaeValue - 2);
					}

					App.gbp.dealMajorChange();
				}
			}

			showButtonStates();

			App.frame.repaint();
		}
	}

	void applyDefaults() {

		if (App.study_deal_maker && App.sd_dev_visibility && App.devMode)
			return;

		if (App.dlaeActive) {
			if ((App.mg.lin.linType == Lin.VuGraph) || App.isVmode_InsideADeal()) {
				App.deal.youSeatHint = App.youSeatHint = Dir.South;
			}
		}

		App.dlaeActive = false;
		App.dlaeValue = App.dlae_inactive;

		App.reviewFromPlay = true;
		App.showOpeningLead = true;

		reviewFromBidding.setSelected(!App.reviewFromPlay);
		reviewFromPlay.setSelected(App.reviewFromPlay);
		showOpeningLead.setSelected(App.showOpeningLead);

		App.localShowHiddPolicy = 0;
		lsh_policy1.setSelected(false); // 1 = show
		lsh_policy2.setSelected(false); // 2 = no change
		lsh_policy0.setSelected(true);  // 0 = hide

		// App.localShowHidden = true; // well this is the initial default

		App.youSeatPartnerVis = false;
		youSeatPartnerVis.setSelected(false);

		pk_vis = true;

		App.papBits = 0;
		App.papXes = true;

		App.alwaysShowHidden = false;
		App.force_N_HiddenTut = false;
		App.force_W_HiddenTut = false;
		App.force_E_HiddenTut = false;
		App.force_S_HiddenTut = false;

		App.obeyRtCmd = true;
		obeyRtCmd.setSelected(App.obeyRtCmd);

		App.obeyAeCmd = true;
		obeyAeCmd.setSelected(App.obeyAeCmd);

		showButtonStates();

		App.allTwister_reset();
	}

	/** This listens for the check box changed event */
	public void itemStateChanged(ItemEvent e) {

//		boolean posCalcPlease = false;
//		boolean youSeatChange = false;
		boolean papChange = false;

		boolean b = (e.getStateChange() == ItemEvent.SELECTED);
		Object source = e.getItemSelectable();

		if (source == reviewFromPlay) {
			App.reviewFromPlay = b;
		}
		else if (source == showOpeningLead) {
			App.showOpeningLead = b;
		}
		else if (source == youSeatPartnerVis) {
			App.youSeatPartnerVis = b;
		}
		else if (source == force_N_HiddenTut) {
			App.force_N_HiddenTut = b;
		}
		else if (source == force_W_HiddenTut) {
			App.force_W_HiddenTut = b;
		}
		else if (source == force_E_HiddenTut) {
			App.force_E_HiddenTut = b;
		}
		else if (source == force_S_HiddenTut) {
			App.force_S_HiddenTut = b;
		}
		else if (source == alwaysShowHidden) {
			App.alwaysShowHidden = b;
		}
		else if (source == obeyRtCmd) {
			App.obeyRtCmd = b;
			if (App.obeyRtCmd && App.rtFound()) {
				// we need to clear out any rotations
				App.compassAllTwister = 0;
				App.calcCompassPhyOffset();
			}
		}
		else if (source == obeyAeCmd) {
			App.obeyAeCmd = b;
		}
		else if (source == sd_dev_visibility) {
			App.sd_dev_visibility = b;
			if (b == false) {
				applyDefaults();
			}
		}
		else if (source == pk) {
			pk_vis = b;
		}

		else if (b == false) {

			if (source == papAllSuits) {
				App.papBits = 0;
				papChange = true;
			}
			else if (source == papSpades) {
				App.papBits &= Suit.papSpades ^ Suit.papAllSuits;
				papChange = true;
			}
			else if (source == papHearts) {
				App.papBits &= Suit.papHearts ^ Suit.papAllSuits;
				papChange = true;
			}
			else if (source == papDiamonds) {
				App.papBits &= Suit.papDiamonds ^ Suit.papAllSuits;
				papChange = true;
			}
			else if (source == papClubs) {
				App.papBits &= Suit.papClubs ^ Suit.papAllSuits;
				papChange = true;
			}

		}

		// we are from here only interested in the selected values for the radio buttons

		else if (source == pap_LHO) {
			App.pap_who = 1;
		}
		else if (source == pap_Ptn) {
			App.pap_who = 2;
		}
		else if (source == pap_RHO) {
			App.pap_who = 3;
		}
		else if (source == papAllSuits) {
			App.papBits = Suit.papAllSuits;
			papChange = true;
		}
		else if (source == papSpades) {
			App.papBits |= Suit.papSpades;
			papChange = true;
		}
		else if (source == papHearts) {
			App.papBits |= Suit.papHearts;
			papChange = true;
		}
		else if (source == papDiamonds) {
			App.papBits |= Suit.papDiamonds;
			papChange = true;
		}
		else if (source == papClubs) {
			App.papBits |= Suit.papClubs;
			papChange = true;
		}
		else if (source == lsh_policy0) {
			App.localShowHiddPolicy = 0;
//			posCalcPlease = true;
		}
		else if (source == lsh_policy1) {
			App.localShowHiddPolicy = 1;
			// App.youSeatPartnerVis = false;
			// youSeatPartnerVis.setSelected(false);
//			posCalcPlease = true;
		}
		else if (source == lsh_policy2) {
			App.localShowHiddPolicy = 2;
			// App.youSeatPartnerVis = false;
			// youSeatPartnerVis.setSelected(false);
//			posCalcPlease = true;
		}

		else if (source == papXes) {
			App.papXes = true;
		}
		else if (source == papCards) {
			App.papXes = false;
		}

		if (App.allConstructionComplete) {

//			if (posCalcPlease) {
//				int offsetWanted = 0; // 0 = no extra rotation (keep south south)
//				if (App.dlaeActive) {
//					offsetWanted = App.deal.contractCompass.rotate180().v;
//					offsetWanted = (4 + offsetWanted + (App.dlaeValue - 2)) % 4;
//				}
//				while (App.getCompassPhyOffset() != offsetWanted)
//					App.incOffsetClockwise();
//			}
//
//			if (youSeatChange) {
//				Dir newYou = App.deal.contractCompass;
//				if (App.dlaeActive) {
//					newYou = Dir.directionFromInt((App.deal.contractCompass.v + (App.dlaeValue - 2)) % 4);
//				}
//				App.deal.setYouSeatHint(newYou);
//			}

			if (papChange && App.papBits != 0) {
				papTimer.start();
			}
			else {
				papTimer.stop();
			}

			App.frame.invalidate();
			App.gbp.matchPanelsToDealState();

			showButtonStates();
		}

	}
}
