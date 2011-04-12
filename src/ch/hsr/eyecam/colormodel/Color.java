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
	public static final int PINK = R.string.color_pink;
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
		float[] hsl = new float[3];
		
		float hue = (float) (Math.atan2(yuv[1], yuv[2])*180.0/Math.PI);
		hsl[0] = (hue >0) ? hue : hue+360;
		hsl[1] = (float) Math.sqrt(Math.pow(yuv[1], 2.0)+Math.pow(yuv[2], 2.0));
		hsl[2] = yuv[0] / 128.0f;
		Log.d(LOG_TAG, "h: " + hsl[0] + " s: " + hsl[1] + " l: " + hsl[2]);
		
		return hslToColor(hsl);
	}
	
	public static int hslToColor(float[] hsl){
		int key = (int) hsl[0];
		Log.d(LOG_TAG, "colorKey: " + key);
		for (Integer colorKey : sColorMap.keySet()){
			if (key < colorKey) return sColorMap.get(colorKey).intValue();
		}
		
		return UNKNOWN;
	}
	
	static {
		sColorMap = new TreeMap<Integer, Integer>();
		
		sColorMap.put(new Integer(330), 
				new Integer(ORANGE));
		sColorMap.put(new Integer(270), 
				new Integer(PURPLE));
		sColorMap.put(new Integer(50), 
				new Integer(GREEN));
		sColorMap.put(new Integer(75), 
				new Integer(YELLOW));
		sColorMap.put(new Integer(360), 
				new Integer(RED));
		sColorMap.put(new Integer(25),
				new Integer(RED));
		sColorMap.put(new Integer(140), 
				new Integer(BLUE));
		sColorMap.put(new Integer(190), 
				new Integer(TURQUOISE));
	}
}
