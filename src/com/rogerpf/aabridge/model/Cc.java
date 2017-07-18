package com.rogerpf.aabridge.model;

import java.awt.Color;
import java.util.ArrayList;

import com.rogerpf.aabridge.controller.App;

//@formatter:off



/**
 */
public class Cc {  // Color Class
	// ---------------------------------- CLASS -------------------------------------
	public enum Ce {
		Strong(0), Weak(1), Weedy(2);
		private Ce(int v) {
			this.v = v;
		}
		public final int v;
	}
	
	public static final int   Dk__Green_Blue_Red_Black    = 0;
	public static final int   Dk__Green_Orange_Red_Blue   = 1;
	public static final int   Dk__Green_Blue_Red_Orange   = 2;
	public static final int   Dk__Black_Red_Red_Black     = 3;
	public static final int   Dk__Gray_Orange_Red_Black   = 4;
	public static final int   Dk__last                    = 4;

	public static       int   deckColorStyle              = Dk__Green_Blue_Red_Black;
	public static       int   deckCardsBlack              = 0;
	

	public static final Color NoChosenSuit       = new Color( 90,  90,  90);

	public static final Color BlackStrong        = new Color( 40,  40,  40);
	public static final Color BlueStrong         = new Color( 40,  70, 220);
	public static final Color GreenStrong        = new Color( 20, 120,  20);
	public static final Color OrangeStrong       = new Color(208,  97,   0);
	public static final Color RedStrong          = new Color(180,  20,  15);

	public static /*final*/ Color BlackWeak          = new Color(140, 140, 140);
	public static /*final*/ Color BlueWeak           = new Color(120, 160, 230);
	public static /*final*/ Color GreenWeak          = new Color( 80, 140,  80); // 100, 180, 140
	public static /*final*/ Color OrangeWeak         = new Color(210, 117, 102);
	public static /*final*/ Color RedWeak            = new Color(190, 140, 140);

	public static final Color BlackWeedy         = new Color(170, 180, 170);
	public static final Color BlueWeedy          = new Color(140, 180, 205);
	public static final Color GreenWeedy         = new Color(100, 180, 100);
	public static final Color OrangeWeedy        = new Color(210, 147, 120);
	public static final Color RedWeedy           = new Color(210, 150, 150);
	
	public static final Color BlackVeryWeedy	 = new Color(200, 200, 200);
    
	public static final Color[] Black         = { BlackStrong,  BlackWeak,  BlackWeedy  };
	public static final Color[] Blue          = { BlueStrong,   BlueWeak,   BlueWeedy   };
	public static final Color[] Green         = { GreenStrong,  GreenWeak,  GreenWeedy  };
	public static final Color[] Orange        = { OrangeStrong, OrangeWeak, OrangeWeedy };
	public static final Color[] Red           = { RedStrong,    RedWeak,    RedWeedy    };
	public static final Color[] Gray          = { BlackWeak,    BlackWeedy, BlackVeryWeedy    };
	
	
	
	

//        clubs diamonds hearts  spades  no_trumps
	public static final Color Green_Blue_Red_Black[][] =
		{ Green, Blue,   Red,    Black,  Black };
	
	public static final Color Green_Orange_Red_Blue[][] =
		{ Green, Orange, Red,    Blue,   Black };
		
	public static final Color Green_Blue_Red_Orange[][] =
		{ Green, Blue,   Red,    Orange, Black };
	
	public static final Color Black_Red_Red_Black[][] =
		{ Black, Red,    Red,    Black,  Black };
	
	public static final Color Black_Red_Orange_Gray[][] =
		{ Gray, Orange,  Red,    Black,  Black };
	
	public static final Color Black_Black_Black_Black[][] =
		{ Black, Black,  Black,  Black,  Black };
	
	public static final Color DeckColors[][][] =
		{ Green_Blue_Red_Black, 
		  Green_Orange_Red_Blue,
		  Green_Blue_Red_Orange,
		  Black_Red_Red_Black,
		  Black_Red_Orange_Gray,
		};
	
	public static final Color DeckBlack[][][] =
		{ Black_Black_Black_Black, 
		  Black_Black_Black_Black,
		  Black_Black_Black_Black,
		  Black_Black_Black_Black,
		  Black_Black_Black_Black,
		};
		
	public static final Color DeckBoth[][][][] =
		{ DeckColors,
		  DeckBlack
		};
			
	public static Color SuitColor(Suit suit, Ce power) {
		return DeckColors[deckColorStyle][suit.v][power.v];
		}
	
	public static Color SuitColorCd(Suit suit, Ce power) {
		return DeckBoth[deckCardsBlack][deckColorStyle][suit.v][power.v];
		}
	
	static protected ArrayList<C> protoColorAy = new ArrayList<C>();

	public static void intensitySliderChange() {
		for (C protoColor : protoColorAy) {
			if (protoColor.active) {
				protoColor.recalc(0); // Tutorial mode
				protoColor.recalc(1); // Deal mode
			}
		}
	}

	public static Color g(int index) {return protoColorAy.get(index).colorTD[App.visualMode == App.Vm_InsideADeal ? 1 : 0];};
	/**
	 */
	static class Cy {
		// ---------------------------------- CLASS -------------------------------------
		int rgb[] = { 0, 0, 0};
		/* constructor */ Cy (int r, int g, int b) {
			this.rgb[0] = r;
			this.rgb[1] = g;
			this.rgb[2] = b;
		}
		/* constructor */ Cy () {
		}
	}
	
	/**
	 */
	static class C {
		// ---------------------------------- CLASS -------------------------------------
		int index;
		boolean active;
		Color colorTD[] = new Color[2];
		Cy dark;
		Cy med;
		Cy light;
		Cy wk;
		
		/* constructor */ C () {
			index = protoColorAy.size();
			active = false;
			colorTD[0] = Color.black;
			colorTD[1] = Color.black;
			dark = new Cy();
			med = new Cy();
			light = new Cy();
			wk = new Cy();
			protoColorAy.add(this);
		}
		
		/* constructor */ C (int index, Cy d, Cy m, Cy l) {
			this.index = index;
			active = true;
			dark = d;
			med = m;
			light = l;
			if (protoColorAy.size() - 1 < index) {
				for (int i = protoColorAy.size(); i <= index; i++) {
					new C();
				}
			}
			colorTD[0] = new Color( m.rgb[0], m.rgb[1], m.rgb[2]);
			colorTD[1] = new Color( m.rgb[0], m.rgb[1], m.rgb[2]);
			protoColorAy.set(index, this);
		}
		
		
		
		
		public void recalc(int index) {  // index is 0 = Tutorial    1 = Deal
			wk = new Cy();
			
			int notch = 0;
			
			if (App.difColorInsideDeal) {
				notch = -40 * index;
			}
			
			int intensity = App.colorIntensity + notch;			
			if (intensity < -255) {
				intensity = -255;
			}
			else if (intensity > 255) {
				intensity = -255;
			}
			
			// System.out.println("index: " + index + "    intensity: " + intensity);
			
			if (intensity == 0) {
				for (int i : Zzz.zto2) {
					wk.rgb[i] = med.rgb[i];
				}
			}
			else if (intensity < 0 && intensity >= -255) {
				for (int i : Zzz.zto2) {
					wk.rgb[i] = dark.rgb[i] + ((med.rgb[i] - dark.rgb[i]) * (255 + intensity))/255;
				}
			}
			else if (intensity > 0 && intensity <=  255) {
				for (int i : Zzz.zto2) {
					if (light.rgb[i] < med.rgb[i])  {
						@SuppressWarnings("unused")
						int z = 0;
					}
					wk.rgb[i] = med.rgb[i] + ((light.rgb[i] - med.rgb[i]) * (intensity))/255;
				}
			}
			else {
				assert(false);
				return;
			}
			
			int tintReducer = 2; // (index == 0) ? 1 : 2;
			
			if (wk.rgb[0] == wk.rgb[1] && wk.rgb[1] == wk.rgb[2]) {
				// greys are untined
			}
			else if (App.colorTint < 0 && App.colorTint >= -50) {
				wk.rgb[0] = (wk.rgb[0] * (150  -  App.colorTint/tintReducer))/150;
			}
			else if (App.colorTint > 0 && App.colorTint <= 50) {
				wk.rgb[2] = (wk.rgb[2] * (150  +  App.colorTint/tintReducer))/150;
			}

			for (int i : Zzz.zto2) {
				if (wk.rgb[i] < 0)
					wk.rgb[i] = 0;
				if (wk.rgb[i] > 255)
					wk.rgb[i] = 255;
			}
			colorTD[index] = new Color( wk.rgb[0], wk.rgb[1], wk.rgb[2]);
		}
	}
	
	public static boolean secondLighterGreen(int color0, int color1) {
		return protoColorAy.get(color0).wk.rgb[1]  <  protoColorAy.get(color1).wk.rgb[1];
	}

	
	public static void colorIntensityChange() {
		BlackWeak = g(blackWeak);
		Black[1] = BlackWeak;
		
		BlueWeak = g(blueWeak);
		Blue[1] = BlueWeak;
		
		GreenWeak = g(greenWeak);
		Green[1] = GreenWeak;
		
		OrangeWeak = g(orangeWeak);
		Orange[1] = OrangeWeak;
		
		RedWeak = g(redWeak);
		Red[1] = RedWeak;
	}
	

	public static final int		baizeGreen	= 0; // new Color( 11, 115,  45);
	public static final int		baizeGreen_c	= 1; // new Color( 11, 115,  45);
	public static final int		darkGrayBg = 4; // new Color(152, 152, 152);
	public static final int     navUnplayedEntered  = 5;
	public static final int     rpfDefBtnColor  = 6;
	public static final int     pointsColor  = 7;
	public static final int     purpleTeamBanner = 8;
	public static final int     youSeatBannerBk = 9;
	public static final int     blackWeak = 11;
	public static final int     blueWeak = 12;
	public static final int     greenWeak = 13;
	public static final int     orangeWeak = 14;
	public static final int     redWeak = 15;

	@SuppressWarnings("unused")
	private static final C      c0 = new C(baizeGreen, new Cy( 40, 90,  45), new Cy(128, 171, 130), new Cy(240, 255, 240));  //170, 210, 170

	@SuppressWarnings("unused")
	private static final C      c1 = new C(baizeGreen_c, new Cy(101, 146, 104), new Cy(138, 185, 140), new Cy(230, 255, 230)); // 124, 168, 126

	@SuppressWarnings("unused")
	private static final C      c4 = new C(darkGrayBg, new Cy( 130, 130, 130), new Cy(152, 152, 152), new Cy(170, 170, 170));

	@SuppressWarnings("unused")
	private static final C      c5 = new C(navUnplayedEntered, new Cy( 40, 40, 40), new Cy(110, 110, 110), new Cy(255, 255, 255));

	@SuppressWarnings("unused")
	private static final C      c6 = new C(rpfDefBtnColor, new Cy( 10, 10, 10), new Cy(110, 110, 110), new Cy(200, 200, 200));
	
	@SuppressWarnings("unused")
	private static final C      c7 = new C(pointsColor, new Cy( 110, 110, 110), new Cy(180, 180, 180), new Cy(255, 255, 255));
	
	@SuppressWarnings("unused")
	private static final C      c8 = new C(purpleTeamBanner, new Cy( 190, 190, 230), new Cy(220, 216, 234), new Cy(230, 220, 240));
	
	@SuppressWarnings("unused")
	private static final C      c9 = new C(youSeatBannerBk, new Cy( 230, 190, 190), new Cy(255, 226, 226), new Cy(255, 226, 226));

	@SuppressWarnings("unused")
	private static final C     c11 = new C(blackWeak,       new Cy( 10, 10,  10),  new Cy(140, 140, 140), new Cy(160, 160, 160));
	
	@SuppressWarnings("unused")
	private static final C     c12 = new C(blueWeak,        new Cy( 40, 70, 220), new Cy(120, 160, 230), new Cy(120, 160, 230));
	
	@SuppressWarnings("unused")
	private static final C     c13 = new C(greenWeak,       new Cy( 20, 120, 20), new Cy( 80, 140,  80), new Cy( 100, 180, 140));
	
	@SuppressWarnings("unused")
	private static final C     c14 = new C(orangeWeak,      new Cy(208,  97,  0), new Cy(210, 117, 102), new Cy(210, 117, 102));
	
	@SuppressWarnings("unused")
	private static final C     c15 = new C(redWeak,         new Cy(180,  20, 15), new Cy(190, 140, 140), new Cy(190, 140, 140));

	
}
