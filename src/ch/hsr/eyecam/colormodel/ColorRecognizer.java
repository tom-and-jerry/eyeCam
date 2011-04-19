package ch.hsr.eyecam.colormodel;

import android.util.Log;

public class ColorRecognizer {
	private static final String LOG_TAG = "ch.hsr.eyecam.colormodel.ColorRecognizer";
	private static final int BYTES_PER_PIXEL = 2;
	private int mHeight;
	private int mWidth;
	private byte[] mBuffer;

	public ColorRecognizer(byte[] buffer, int width, int height) {
		mBuffer = buffer;
		mWidth = width;
		mHeight = height;
	}

	public int getColorAt(int width, int height){
		int[] yuv = new int[3];
		int posU = mHeight*mWidth + (height/2)*mWidth + BYTES_PER_PIXEL*(width/2);

		yuv[0] = mBuffer[height*mWidth-1 + width];
		yuv[1] = mBuffer[posU];
		yuv[2] = mBuffer[posU+1];
		int y = yuv[0]+128;
		Log.d(LOG_TAG , "YUV Values from Buffer: y: " + y + " u: " + yuv[1] + " v: " + yuv[2]);
		
		return Color.yuvToColor(yuv);
	}
}
