package ch.hsr.eyecam.view;

import ch.hsr.eyecam.EyeCamActivity;
import ch.hsr.eyecam.Orientation;
import ch.hsr.eyecam.colormodel.ColorRecognizer;
import ch.hsr.eyecam.colormodel.ColorTransform;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * A class extending android.view.View and providing a frame for 
 * the bitmap that is used to write the transformed preview frames
 * into.
 * 
 * @author Dominik Spengler
 * @see <a href="http://developer.android.com/reference/
 * 			android/view/View.html">
 * 			android.view.View</a>
 */
public class ColorView extends View implements PreviewCallback {
	private Bitmap mBitmap;
	private Handler mActivityHandler;
	private byte[] mDataBuffer;
	private ColorRecognizer mColorRecognizer;
	private FloatingBubble mPopup;
	private static String LOG_TAG = "ch.hsr.eyecam.view.ColorView";

	private OnTouchListener mOnTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			//TODO: handle devices with sub-pixel accuracy
			if (event.getAction() == MotionEvent.ACTION_DOWN){
				int x = (int)event.getX();
				int y = (int)event.getY();
				
				if (x < 0) x = 0;
				if (y < 0) y = 0;
				
				int rgb = mBitmap.getPixel(x, y);
				int r = (rgb & 0xf800) >> 11;
				int g = (rgb & 0x07e0) >> 5;
				int b = (rgb & 0x001f);
				Log.d(LOG_TAG, "RGB Values from Screen: r: " + r + " g: " + g + " b: " + b);
				showColorAt(mColorRecognizer.getColorAt(x, y), x, y);
				return true;
			}
			return false;
		}
	};
	
	public ColorView(Context context) {
		this(context,null);

	}

	public ColorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		initPopup();
		setOnTouchListener(mOnTouchListener);
	}

	/**
	 * onLayout() is used as a callback for when to create the bitmap and 
	 * when to start the Camera preview.
	 * 
	 * @see <a href="http://developer.android.com/reference/
     *		android/view/View.html#onLayout(boolean, int, int, int, int)">
     * 		android.view.View#onLayout(boolean, int, int, int, int)</a>
	 */
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (mBitmap == null && getWidth() > 0) {
			initBitmap();
			
			mActivityHandler.sendEmptyMessage(EyeCamActivity.CAMERA_START_PREVIEW);
		}
	}

	private void initBitmap() {
		mBitmap = Bitmap.createBitmap(getWidth(), getHeight(),
				Bitmap.Config.RGB_565);
		Log.d(LOG_TAG, "Bitmap size: W: " + getWidth() + " H: "
				+ getHeight());
	}

	private void initPopup() {
		mPopup = new FloatingBubble(getContext(), this);
	}

	private void showColorAt(int color, int x, int y){
		mPopup.dismiss();
		mPopup.showStringResAt(color, x, y);
		Log.d(LOG_TAG, "Popup Location on Screen: x: " + x + " y: " + y);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawBitmap(mBitmap, 0, 0, null);
	}

	/**
	 * Callback provided by android.hardware.Camera.PreviewCallback
	 * Interface. This is where the transformation calls happen.
	 * 
	 * @see <a href="http://developer.android.com/reference/
     *		android/hardware/Camera.PreviewCallback.html">
     * 		android.hardware.Camera.PreviewCallback</a>
	 */
	@Override
	public void onPreviewFrame(byte[] data, Camera cam) {
		int width = cam.getParameters().getPreviewSize().width;
		int height = cam.getParameters().getPreviewSize().height;

		ColorTransform.transformImageToBitmap(data, width, height, mBitmap);
		cam.addCallbackBuffer(data);
		invalidate();
	}

	/**
	 * This method sets the activity handler used to send messages
	 * for starting and stopping the Camera preview since ColorView
	 * doesn't and shouldn't know about the Camera instance itself.
	 * 
	 * @param handler the activity handler used for message passing.
	 * @see <a href="http://developer.android.com/reference/
	 *		android/os/Handler.html">
	 * 		android.os.Handler</a>
	 */
	public void setActivityHandler(Handler handler) {
		mActivityHandler = handler;
	}

	/**
	 * This method is used to set the data buffer used for the camera
	 * preview.
	 * 
	 * @param callBackBuffer
	 * @param width of the preview size
	 * @param height of the preview size
	 */
	public void setDataBuffer(byte[] callBackBuffer, int width, int height) {
		mDataBuffer = callBackBuffer;
		mColorRecognizer = new ColorRecognizer(mDataBuffer, width, height);
	}

	public void setOrientation(Orientation orientation) {
		mPopup.setOrientation(orientation);
	}
}
