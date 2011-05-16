package ch.hsr.eyecam.view;

import ch.hsr.eyecam.EyeCamActivity;
import ch.hsr.eyecam.Orientation;
import ch.hsr.eyecam.colormodel.ColorRecognizer;
import ch.hsr.eyecam.colormodel.ColorTransform;
import ch.hsr.eyecam.widget.FloatingBubble;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
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
	private int mPreviewHeight;
	private int mPreviewWidth;
	private boolean mPartialEnabled;
	private byte[] mDataBuffer;
	
	private ColorRecognizer mColorRecognizer;
	private FloatingBubble mPopup;

	private OnTouchListener mOnTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN){
				int x = (int)event.getX();
				int y = (int)event.getY();
				
				if (x < 0) x = 0;
				if (y < 0) y = 0;
				
				int rgb = mBitmap.getPixel(x, y);
				int r = (rgb & 0xff0000) >> 16;
				int g = (rgb & 0x00ff00) >> 8;
				int b = (rgb & 0x0000ff);
				Log.d(LOG_TAG, "RGB Values from Screen: r: " + r + " g: " + g + " b: " + b);
				showColorAt(mColorRecognizer.getColorAt(x, y), x, y);
				return true;
			}
			return false;
		}
	};
	
	private static String LOG_TAG = "ch.hsr.eyecam.view.ColorView";
	
	public ColorView(Context context) {
		super(context);
		initPopup();
		mPartialEnabled = false;
	}

	public ColorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initPopup();
		mPartialEnabled = false;
	}

	private void initBitmap() {
		mBitmap = Bitmap.createBitmap(mPreviewWidth, mPreviewHeight,
				Bitmap.Config.RGB_565);
		Log.d(LOG_TAG, "Bitmap size: W: " + mPreviewWidth + " H: "
				+ mPreviewHeight);
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
		ColorTransform.transformImageToBitmap(data, mPreviewWidth, mPreviewHeight, mBitmap);
		cam.addCallbackBuffer(data);
		invalidate();
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
		mPreviewHeight = height;
		mPreviewWidth = width;
		mColorRecognizer = new ColorRecognizer(mDataBuffer, mPreviewWidth, mPreviewHeight);
		initBitmap();
	}

	public void setOrientation(Orientation orientation) {
		mPopup.setOrientation(orientation);
	}

	public void enablePopup(boolean showPopup) {
		if(showPopup) setOnTouchListener(mOnTouchListener);
		else {
			setOnTouchListener(null);
			mPopup.dismiss();
		}
	}

	public void dismissPopup() {
		mPopup.dismiss();
	}

	/**
	 * 
	 * @param size in pt
	 */
	public void setPopupTextSize(int size) {
		mPopup.dismiss();
		mPopup.setTextSize(size);
	}
	
	public void enablePartialEffects(boolean partialEnabled){
		mPartialEnabled = partialEnabled;
	}

	public void setEffect(int effect) {
		if (mPartialEnabled) ColorTransform.setPartialEffect(effect);
		else ColorTransform.setEffect(effect);
		if(!EyeCamActivity.IS_PREVIEWING){
			Log.d(LOG_TAG,"Effect on Previewimage");
			ColorTransform.transformImageToBitmap(mDataBuffer, mPreviewWidth, mPreviewHeight, mBitmap);
			invalidate();
		}
	}
}
