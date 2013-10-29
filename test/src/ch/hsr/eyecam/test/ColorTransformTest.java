package ch.hsr.eyecam.test;

import junit.framework.TestCase;
import android.graphics.Bitmap;
import ch.hsr.eyecam.colormodel.ColorTransform;

public class ColorTransformTest extends TestCase {
	private final int samplePictureWidth = 4;
	private final int samplePictureHeight = 4;
	private Bitmap mBitmap;
	
	/**
	 * represents the euclidean color difference
	 * value used as a threshold for color similarity.
	 */
	private final static int COLOR_DELTA = 10;
	private static final int BYTES_PER_PIXEL = 2; 
	
	private final byte[] yuvRed = {  0,  0,  0,  0,
					   				 0,  0,  0,  0,
									 0,  0,  0,  0,
									 0,  0,  0,  0,
									-1 , 0, -1 , 0,
									-1 , 0, -1 , 0};

	private final byte[] yuvGreen = {	0,  0,  0,  0,
										0,  0,  0,  0,
										0,  0,  0,  0,
										0,  0,  0,  0,
										0 , 0, 0, 0,
										0 , 0, 0, 0};
	
	private final byte[] yuvBlue = {	0,  0,  0,  0,
										0,  0,  0,  0,
										0,  0,  0,  0,
										0,  0,  0,  0,
										127 , -1, 127, -1,
										127 , -1, 127, -1};
	private final byte[] yuvBlack= {	0,  0,  0,  0,
										0,  0,  0,  0,
										0,  0,  0,  0,
										0,  0,  0,  0,
										127 , 127, 127, 127,
										127 , 127, 127, 127};
	private final byte[] yuvWhite = {	-1,  -1,  -1,  -1,
										-1,  -1,  -1,  -1,
										-1,  -1,  -1,  -1,
										-1,  -1,  -1,  -1,
										127 , 127, 127, 127,
										127 , 127, 127, 127};
	private final byte[] yuvYellow = {	-1,  -1,  -1,  -1,
										-1,  -1,  -1,  -1,
										-1,  -1,  -1,  -1,
										-1,  -1,  -1,  -1,
										-80 , 0, -80, 0,
										-80 , 0, -80, 0};
	
	public ColorTransformTest(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ColorTransform.setEffect(ColorTransform.COLOR_EFFECT_NONE);
		mBitmap = Bitmap.createBitmap(samplePictureWidth, 
				samplePictureHeight, 
				Bitmap.Config.RGB_565);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testTransformImageToBitmap() {
		int width = samplePictureWidth/2;
		int height = samplePictureHeight/2;
		
		ColorTransform.transformImageToBitmap(yuvRed, 
				samplePictureWidth, samplePictureHeight, mBitmap);
		assertColorSimilar(CorrectedColor.RED, mBitmap.getPixel(width, height));
		
		ColorTransform.transformImageToBitmap(yuvGreen, 
				samplePictureWidth, samplePictureHeight, mBitmap);
		assertColorSimilar(CorrectedColor.GREEN, mBitmap.getPixel(width, height));
		
		ColorTransform.transformImageToBitmap(yuvBlue, 
				samplePictureWidth, samplePictureHeight, mBitmap);
		assertColorSimilar(CorrectedColor.BLUE, mBitmap.getPixel(width, height));
		
		ColorTransform.transformImageToBitmap(yuvBlack, 
				samplePictureWidth, samplePictureHeight, mBitmap);
		assertColorSimilar(android.graphics.Color.BLACK, mBitmap.getPixel(width, height));
		
		ColorTransform.transformImageToBitmap(yuvWhite, 
				samplePictureWidth, samplePictureHeight, mBitmap);
		assertColorSimilar(android.graphics.Color.WHITE, mBitmap.getPixel(width, height));
		
		ColorTransform.transformImageToBitmap(yuvYellow, 
				samplePictureWidth, samplePictureHeight, mBitmap);
		assertColorSimilar(CorrectedColor.YELLOW, mBitmap.getPixel(width, height));
	}

	private void assertColorSimilar(int expected, int actual) {
		int rExp, gExp, bExp;
		int rAct, gAct, bAct;
		int rDif, gDif, bDif;
		
		rExp = (expected & 0xff0000) >> 16;
		gExp = (expected & 0x00ff00) >> 8;
		bExp = (expected & 0x0000ff);
		
		rAct = (actual & 0xff0000) >> 16;
		gAct = (actual & 0x00ff00) >> 8;
		bAct = (actual & 0x0000ff);
		
		rDif = rExp - rAct;
		gDif = gExp - gAct;
		bDif = bExp - bAct;
		
		int deltaE = (int)Math.sqrt(Math.pow(rDif, 2.0)+Math.pow(gDif, 2.0)+Math.pow(bDif, 2.0));
		assertTrue("expected color: " + rExp + "," + gExp + "," +bExp +
				" actual color: " + rAct + "," + gAct + "," +bAct, deltaE<COLOR_DELTA);
	}
	
	public void testColorRecognition(){
		int width = samplePictureWidth/2;
		int height = samplePictureHeight/2;
		int[] color;
		
		// color = getYuv(yuvBlack, width, height);
		// assertEquals(Color.BLACK, Color.yuvToColor(color));
		//
		// color = getYuv(yuvWhite, width, height);
		// assertEquals(Color.WHITE, Color.yuvToColor(color));
		//
		// color = getYuv(yuvBlue, width, height);
		// assertEquals(Color.BLUE, Color.yuvToColor(color));
		//
		// color = getYuv(yuvGreen, width, height);
		// assertEquals(Color.GREEN, Color.yuvToColor(color));
		//
		// color = getYuv(yuvRed, width, height);
		// assertEquals(Color.RED, Color.yuvToColor(color));
		//
		// color = getYuv(yuvYellow, width, height);
		// assertEquals(Color.YELLOW, Color.yuvToColor(color));
	}
	
	private int[] getYuv(byte[] buffer, int width, int height) {
		int[] yuv = new int[3];
		int posUV = samplePictureHeight*samplePictureWidth 
			+ (height/2)*samplePictureWidth + BYTES_PER_PIXEL*(width/2);

		yuv[0] = buffer[height*samplePictureWidth + width];
		yuv[1] = buffer[posUV+1];
		yuv[2] = buffer[posUV];
		
		return yuv;
	}

	/**
	 * the color transformation from YUV to RGB is not as accurate
	 * as wished, so we needed to introduce a CorrectedColor class.
	 */
	private class CorrectedColor {
		// represents (184,0,0)
		public static final int RED = 0xb80000;
		// represents (0,144,0)
		public static final int GREEN = 0x008f00;
		// represents (0,0,240)
		public static final int BLUE = 0x0000f0;
		// represents (255,255,16)
		public static final int YELLOW = 0xffff0f;
	}
}
