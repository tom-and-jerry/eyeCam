package ch.hsr.eyecam.colormodel;

public class ColorRecognizer {
	private int mHeight;
	private int mWidth;
	private byte[] mBuffer;
	
	private static final int BYTES_PER_PIXEL = 2;

	public ColorRecognizer(byte[] buffer, int width, int height) {
		mBuffer = buffer;
		mWidth = width;
		mHeight = height;
	}

	public int getColorAt(int width, int height){
		int[] yuv = new int[3];
		int posUV = mHeight*mWidth + (height/2)*mWidth + BYTES_PER_PIXEL*(width/2);

		yuv[0] = mBuffer[height*mWidth + width];
		yuv[1] = mBuffer[posUV+1];
		yuv[2] = mBuffer[posUV];
		
		return Color.yuvToColor(yuv);
	}
}
