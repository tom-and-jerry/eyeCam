package ch.hsr.eyecam.transform;

import android.graphics.Bitmap;

public class ColorTransform {
	
	static {
		System.loadLibrary("colortransform");
	}
	
	public static final int COLOR_EFFECT_NONE = 0;
	public static final int COLOR_EFFECT_SIMULATE = 1;
		
	/**
	 * Sets the effect to be used for the transformation. The default 
	 * effect is {@link #COLOR_EFFECT_NONE}.
	 * 
	 * @param effect
	 * 
	 * @see #COLOR_EFFECT_NONE
	 * @see #COLOR_EFFECT_SIMULATE
	 */
	public static native void setEffect(int effect);
	
	/**
	 * This method will transform the image data given in the byte array
	 * according to the effect and write it to the bitmap specified.
	 * 
	 * 
	 * @param data
	 * @param width
	 * @param heigth
	 * @param bitmap
	 */
	public static native void transformImageToBitmap(byte[] data, int width, int height, Bitmap bitmap);
	
	/**
	 * 
	 * @param data
	 * @param width
	 * @param height
	 * @param buffer
	 */
	public static native void transformImageToBuffer(byte[] data, int width, int height, byte[] buffer);
}
