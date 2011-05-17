package ch.hsr.eyecam.colormodel;

/**
 * The ColorRecognizer class is a convenience class for interaction
 * with the Color class. You need to instantiate it with a byte buffer
 * holding data from the camera in NV21 (aka yuv420sp) preview format.
 * 
 * @author Dominik Spengler
 *
 */
public class ColorRecognizer {
	private int mHeight;
	private int mWidth;
	private byte[] mBuffer;
	
	private static final int BYTES_PER_PIXEL = 2;

	/**
	 * Simple constructor for instantiating a ColorRecognizer that 
	 * uses the buffer with the given width and height to get the 
	 * color data.
	 * 
	 * If you want to reuse the class multiple times, consider using
	 * addCallbackBuffer() method of the Camera since there are no 
	 * getters and setters for the buffer.
	 * 
	 * @see <a href="http://developer.android.com/reference/
	 * 			android/hardware/Camera.html#addCallbackBuffer(byte[])">
	 * 			android.hardware.Camera.html#addCallbackBuffer(byte[])</a>
	 * 
	 * @param buffer holding the camera preview data.
	 * @param width of the preview buffer.
	 * @param height of the preview buffer.
	 */
	public ColorRecognizer(byte[] buffer, int width, int height) {
		mBuffer = buffer;
		mWidth = width;
		mHeight = height;
	}

	/**
	 * This method will return the color of the pixel at location 
	 * (width, height). The pixel data is taken from the buffer
	 * supplied in the constructor.
	 * 
	 * @param 	width location of the pixel to be recognized
	 * @param 	height location of the pixel to be recognized
	 * @return 	the resource id of the String representation of the
	 * 			color.
	 */
	public int getColorAt(int width, int height){
		int[] yuv = new int[3];
		int posUV = mHeight*mWidth + (height/2)*mWidth + BYTES_PER_PIXEL*(width/2);

		yuv[0] = mBuffer[height*mWidth + width];
		yuv[1] = mBuffer[posUV+1];
		yuv[2] = mBuffer[posUV];
		
		return Color.yuvToColor(yuv);
	}
}
