package ch.hsr.eyecam.colormodel;

import android.graphics.Bitmap;
/**
 * A class representing an interface to the colortransform library.
 * 
 * @author Dominik Spengler
 *
 */
public class ColorTransform {

	static {
		System.loadLibrary("colortransform");
	}

	public static final int COLOR_EFFECT_NONE = 0;
	public static final int COLOR_EFFECT_SIMULATE = 1;
	public static final int COLOR_EFFECT_NOY = 2;
	public static final int COLOR_EFFECT_NOU = 3;
	public static final int COLOR_EFFECT_NOV = 4;
	public static final int COLOR_EFFECT_SWITCH_UV = 5;

	/**
	 * Sets the effect to be used for the transformation. The default effect is
	 * {@link #COLOR_EFFECT_NONE}.
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
	 * @param data the source data in yuv420sp
	 * @param width of the source data
	 * @param heigth of the source data
	 * @param bitmap to write the data to
	 */
	public static native void transformImageToBitmap(byte[] data, int width,
			int height, Bitmap bitmap);

	/**
	 * This method will transform the image data given in the byte array
	 * according to the effect and write it to the buffer specified.
	 * 
	 * 
	 * @param data the source data in yuv420sp
	 * @param width of the source data
	 * @param heigth of the source data
	 * @param buffer to write the data to
	 */
	public static native void transformImageToBuffer(byte[] data, int width,
			int height, byte[] buffer);
}
