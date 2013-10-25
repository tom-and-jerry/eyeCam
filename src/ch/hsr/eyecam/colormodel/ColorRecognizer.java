package ch.hsr.eyecam.colormodel;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import ch.hsr.eyecam.Debug;

/**
 * The ColorRecognizer class is a convenience class for interaction with the Color class. You need to instantiate it with a byte buffer holding data from the camera in NV21 (aka
 * yuv420sp) preview format.
 * 
 * @author Dominik Spengler
 * 
 */
public class ColorRecognizer {
	private final int mHeight;
	private final int mWidth;
	private final byte[] mBuffer;

	private static final int BYTES_PER_PIXEL = 2;
	private static int BYTE_TO_UNSIGNED_MASK = 0xff;

	/**
	 * Simple constructor for instantiating a ColorRecognizer that uses the buffer with the given width and height to get the color data.
	 * 
	 * If you want to reuse the class multiple times, consider using addCallbackBuffer() method of the Camera since there are no getters and setters for the buffer.
	 * 
	 * @see Camera#addCallbackBuffer(byte[])
	 * 
	 * @param buffer
	 *            holding the camera preview data.
	 * @param width
	 *            of the preview buffer.
	 * @param height
	 *            of the preview buffer.
	 */
	public ColorRecognizer(byte[] buffer, int width, int height) {
		mBuffer = buffer;
		mWidth = width;
		mHeight = height;
	}

	/**
	 * This method will return the color of the pixel at location (width, height). The pixel data is taken from the buffer supplied in the constructor.
	 * 
	 * @param width
	 *            location of the pixel to be recognized
	 * @param height
	 *            location of the pixel to be recognized
	 * @return the resource id of the String representation of the color.
	 */
	public int getColorAt(int x, int y) {
		// int[] nv21 = getNv21At(x, y);
		// return Color.nv21toColor(nv21);
		int[] yuv = getYuvAt(x, y);
		return Color.yuvToColor(yuv);
	}

	/**
	 * This method will return the YUV values of the pixel at location (width, height) as contained in the buffer supplied in the constructor.
	 * 
	 * @param width
	 *            location of the pixel to be recognized
	 * @param height
	 *            location of the pixel to be recognized
	 * @return int array with the yuv values
	 */
	private int[] getYuvAt(int x, int y) {
		int[] yuv = new int[3];
		int posUV = mHeight * mWidth + (y / 2) * mWidth + BYTES_PER_PIXEL * (x / 2);

		yuv[0] = mBuffer[y * mWidth + x];
		yuv[1] = mBuffer[posUV + 1];
		yuv[2] = mBuffer[posUV];

		return yuv;
	}

	public int[] getNv21At(int x, int y) {
		int[] nv21 = new int[3];
		int posUV = mHeight * mWidth + (y / 2) * mWidth + BYTES_PER_PIXEL * (x / 2);

		nv21[0] = mBuffer[y * mWidth + x] & BYTE_TO_UNSIGNED_MASK;
		nv21[1] = mBuffer[posUV] & BYTE_TO_UNSIGNED_MASK;
		nv21[2] = mBuffer[posUV + 1] & BYTE_TO_UNSIGNED_MASK;

		return nv21;
	}

	/**
	 * This method will return the RGB values of the pixel at location (width, height). The pixel data is taken from the buffer supplied in the constructor.
	 * 
	 * @param width
	 *            location of the pixel to be recognized
	 * @param height
	 *            location of the pixel to be recognized
	 * @return int array with the RGB values
	 */
	public int[] getRgbAt(int x, int y) {
		int[] yuv = getYuvAt(x, y);
		Color.yuvToRgb(yuv);
		int[] nv21 = getNv21At(x, y);
		return Color.nv21ToRGB(nv21);
		// return Color.yuvToRgb(yuv);
	}

	public int[] getRgbAtNew(int x, int y) {


		ByteArrayOutputStream out = new ByteArrayOutputStream();
		YuvImage yuvImage = new YuvImage(mBuffer, ImageFormat.NV21, mWidth, mHeight, null);
		yuvImage.compressToJpeg(new Rect(0, 0, mWidth, mHeight), 100, out);
		byte[] imageBytes = out.toByteArray();
		Bitmap theImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
		int colorAtPixel = theImage.getPixel(x, y);

		int r = android.graphics.Color.red(colorAtPixel);
		int g = android.graphics.Color.green(colorAtPixel);
		int b = android.graphics.Color.blue(colorAtPixel);
		Debug.msg("RGB from android: R:" + r + " G:" + g + " B:" + b);

		// int posUV = mHeight * mWidth + (y / 2) * mWidth + BYTES_PER_PIXEL * (x / 2);
		//
		// byte yByte = mBuffer[y * mWidth + x];
		// byte crByte = mBuffer[posUV];
		// byte cbByte = mBuffer[posUV + 1];
		//
		// byte[] miniBuffer = new byte[] { yByte, yByte, yByte, yByte, yByte, yByte, yByte, yByte, yByte, yByte, yByte, yByte, crByte, cbByte, crByte,
		// cbByte, crByte, cbByte };
		// ByteArrayOutputStream out = new ByteArrayOutputStream();
		// int width = 4;
		// int height = 3;
		// YuvImage yuvImage = new YuvImage(miniBuffer, ImageFormat.NV21, width, height, null);
		// yuvImage.compressToJpeg(new Rect(0, 0, width, height), 100, out);
		// byte[] imageBytes = out.toByteArray();
		// Bitmap miniBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
		// int colorAtPixel = miniBitmap.getPixel(1, 1);
		// int r = android.graphics.Color.red(colorAtPixel);
		// int g = android.graphics.Color.green(colorAtPixel);
		// int b = android.graphics.Color.blue(colorAtPixel);
		// Debug.msg("RGB from mini-android: R:" + r + " G:" + g + " B:" + b);
		
		return new int[] { r, g, b };
	}
}
