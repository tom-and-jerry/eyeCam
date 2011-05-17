package ch.hsr.eyecam.colormodel;

import java.util.SortedMap;
import java.util.TreeMap;

import android.util.Log;

import ch.hsr.eyecam.R;

public class Color {
	public static final int BLACK = R.string.color_black;
	public static final int GREY = R.string.color_grey;
	public static final int WHITE = R.string.color_white;
	public static final int BLUE = R.string.color_blue;
	public static final int GREEN = R.string.color_green;
	public static final int ORANGE = R.string.color_orange;
	public static final int BROWN = R.string.color_brown;
	public static final int PURPLE = R.string.color_purple;
	public static final int TURQUOISE = R.string.color_turquoise;
	public static final int RED = R.string.color_red;
	public static final int YELLOW = R.string.color_yellow;
	public static final int UNKNOWN = R.string.color_unknown;

	private static final SortedMap<Integer, Integer> sColorMap;
	private static final String LOG_TAG = "ch.hsr.eyecam.colormodel.Color";
	
	/**
	 * Converts the color represented in YUV color space to the
	 * string representation of the color. It does this by using
	 * a sorted map using 
	 * 
	 * @param 	yuv
	 * @return 	the resource id of the String representation of the
	 * 			color.
	 */
	public static int yuvToColor(int[] yuv){
		int[] rgb = new int[3];
		int y = (yuv[0]<0) ? yuv[0]+256 : yuv[0];
		int u = (yuv[1]<0) ? yuv[1]+256 : yuv[1];
		u -= 128;
		int v = (yuv[2]<0) ? yuv[2]+256 : yuv[2];
		v -= 128;
		
		Log.d(LOG_TAG , "YUV Values from Buffer: y: " + y + " u: " + u + " v: " + v);
		rgb[0] = (int) (y + 1.13983*v);
		if (rgb[0] < 0) rgb[0] = 0; else if (rgb[0] > 255) rgb[0] = 255;
		rgb[1] = (int) (y - 0.39465*u - 0.58060*v);
		if (rgb[1] < 0) rgb[1] = 0; else if (rgb[1] > 255) rgb[1] = 255;
		rgb[2] = (int) (y + 2.03211*u);
		if (rgb[2] < 0) rgb[2] = 0; else if (rgb[2] > 255) rgb[2] = 255;
		Log.d(LOG_TAG , "converted RGB Values: r: " + rgb[0] + " g: " + rgb[1] + " b: " + rgb[2]);
		
		return rgbToColor(rgb);
	}
	
	public static int rgbToColor(int[] rgb){
		float[] hsl = new float[3];
		int r = rgb[0];
		int g = rgb[1];
		int b = rgb[2];
		int max = Math.max(Math.max(r, g), b);
		int min = Math.min(Math.min(r, g), b);
		
		float chroma = max - min;
		float luma = (max + min)/510.0f;
		hsl[2] = luma;
		
		if (max == r) hsl[0] = 60*((g-b)/chroma);
		else if (max == g) hsl[0] = 60*(((b-r)/chroma)+2);
		else if (max == b) hsl[0] = 60*(((r-g)/chroma)+4);
		
		if (chroma != 0) hsl[1] = chroma/(255.0f*(1-Math.abs(2*luma-1)));
		Log.d(LOG_TAG , "converted hsl Values: h: " + hsl[0] + " s: " + hsl[1] + " l: " + hsl[2]);
		return hslToColor(hsl);
	}
	
	public static int hslToColor(float[] hsl){
		if (hsl[2] < 0.2) return BLACK;
		else if (hsl[2] > 0.8) return WHITE;
		if (hsl[1] < 0.2) return GREY;
		
		int color = UNKNOWN;
		int key = (int)hsl[0];
		Log.d(LOG_TAG, "colorKey: " + key);
		for (Integer colorKey : sColorMap.keySet()){
			if (key < colorKey) {
				color = sColorMap.get(colorKey).intValue();
				break;
			}
		}
		
		if (color == RED){
			if (hsl[1] >= 0.2 && hsl[1] < 0.35) color = BROWN;
		} else if (color == ORANGE){
			if (hsl[1] >= 0.2 && hsl[1] < 0.65) color = BROWN;
		}
		return color;
	}
	
	static {
		sColorMap = new TreeMap<Integer, Integer>();
		
		sColorMap.put(new Integer(20),
				new Integer(RED));
		sColorMap.put(new Integer(45), 
				new Integer(ORANGE));
		sColorMap.put(new Integer(70), 
				new Integer(YELLOW));
		sColorMap.put(new Integer(150), 
				new Integer(GREEN));
		sColorMap.put(new Integer(200), 
				new Integer(TURQUOISE));
		sColorMap.put(new Integer(265), 
				new Integer(BLUE));
		sColorMap.put(new Integer(330), 
				new Integer(PURPLE));
		sColorMap.put(new Integer(360), 
				new Integer(RED));
	}
}
