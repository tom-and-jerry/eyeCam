package ch.hsr.eyecam.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import ch.hsr.eyecam.Debug;
import ch.hsr.eyecam.EyeCamActivity;
import ch.hsr.eyecam.Orientation;
import ch.hsr.eyecam.colormodel.ColorRecognizer;
import ch.hsr.eyecam.colormodel.ColorTransform;
import ch.hsr.eyecam.widget.BubbleView;
import ch.hsr.eyecam.widget.FloatingColorBubble;

/**
 * A class extending android.view.View and providing a frame for the bitmap that is used to write the transformed preview frames into.
 * 
 * ColorView also provide methods for interaction with the ColorTransform, ColorRecognizer and FloatingBubble classes.
 * 
 * @author Dominik Spengler
 * 
 */
public class ColorView extends View implements PreviewCallback {

	private Bitmap mBitmap;
	private int mPreviewHeight;
	private int mPreviewWidth;
	private boolean mPartialEnabled;
	private byte[] mDataBuffer;

	private FloatingColorBubble mPopup;
	private Handler mActivityHandler;
	private boolean mIsScaled = false;
	private float mScaleFactor;

	private final OnTouchListener mOnTouchListener = new EyeCamViewTouchListener();
	private final OnLongClickListener mOnLongClickListener = new EyeCamViewLongClickListener();

	private int mScreenWidth;
	private int mScreenHeight;

	private static String LOG_TAG = "ch.hsr.eyecam.view.ColorView";

	public ColorView(Context context) {
		super(context);
		init();
	}

	public ColorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		mPopup = new FloatingColorBubble(getContext(), this);

		setOnTouchListener(mOnTouchListener);
		setOnLongClickListener(mOnLongClickListener);
		mPartialEnabled = false;
	}

	private void initBitmap() {
		mBitmap = Bitmap.createBitmap(mPreviewWidth, mPreviewHeight, Bitmap.Config.RGB_565);
		Debug.msg(LOG_TAG, "Bitmap size: W: " + mPreviewWidth + " H: " + mPreviewHeight);
	}

	/**
	 * Method to scale the bitmap in ColorView representing the camera preview to full screen, even if the camera preview is smaller than the screen.
	 * 
	 * Note that only the height will be used to calculate the scale factor. The height will be scaled accordingly in order not to loose the aspect ratio.
	 * 
	 * @param screenWidth
	 *            width you wish to scale the bitmap to
	 * @param screenHeight
	 *            height you wish to scale the bitmap to
	 */
	public void scaleBitmapToFillScreen(int screenWidth, int screenHeight) {
		if (mBitmap.getHeight() < screenHeight) {
			mIsScaled = true;
			mScaleFactor = (float) screenHeight / mBitmap.getHeight();
			mScreenHeight = screenHeight;
			mScreenWidth = (int) (mBitmap.getWidth() * mScaleFactor);
			Debug.msg(LOG_TAG, "Scaling enabled with factor: " + mScaleFactor);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Redraws the Bitmap containing the transformed picture on each call.
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mIsScaled) {
			Bitmap scaledBitmap = Bitmap.createScaledBitmap(mBitmap, mScreenWidth, mScreenHeight, false);
			canvas.drawBitmap(scaledBitmap, 0, 0, null);
			// if (theImage != null) {
			// canvas.drawBitmap(theImage, 0, 0, null);
			// }
		} else
			canvas.drawBitmap(mBitmap, 0, 0, null);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * This is where the transformation calls happen.
	 * 
	 * @see PreviewCallback
	 */
	@Override
	public void onPreviewFrame(byte[] data, Camera cam) {
		ColorTransform.transformImageToBitmap(data, mPreviewWidth, mPreviewHeight, mBitmap);
		cam.addCallbackBuffer(data);
		invalidate();
	}

	/**
	 * This method is used to set the data buffer used for the camera preview.
	 * 
	 * @param callBackBuffer
	 * @param width
	 *            of the preview size
	 * @param height
	 *            of the preview size
	 */
	public void setDataBuffer(byte[] callBackBuffer, int width, int height) {
		mDataBuffer = callBackBuffer;
		mPreviewHeight = height;
		mPreviewWidth = width;
		mPopup.setColorRecognizer(new ColorRecognizer(mDataBuffer, mPreviewWidth, mPreviewHeight));
		initBitmap();
	}

	/**
	 * This method is used to set the orientation of the Popup. Since our application manages screen orientation changes itself, this method needs to be called manually on each
	 * orientation change.
	 * 
	 * @see BubbleView#setOrientation(Orientation)
	 * @param orientation
	 *            of the popup to be displayed in.
	 */
	public void setOrientation(Orientation orientation) {
		mPopup.setOrientation(orientation);
	}

	/**
	 * Enable or disable the "on touch" Popup used to recognize colors.
	 * 
	 * @param showPopup
	 *            true if the popup should be enabled, false otherwise.
	 */
	/*
	 * public void enablePopup(boolean showPopup) { if(!showPopup) { mPopup.dismiss(); } }
	 */

	/**
	 * If the Popup is showing, it will be dismissed. Nothing happens if the Popup is not showing.
	 */
	public void dismissPopup() {
		mPopup.dismiss();
	}

	/**
	 * Sets the size of the text displayed in the Popup.
	 * 
	 * @see FloatingColorBubble#setTextSize(int)
	 * @param size
	 *            in pt
	 */
	public void setPopupTextSize(int size) {
		mPopup.dismiss();
		mPopup.setTextSize(size);
	}

	/**
	 * Enables or disables partial effects. After calling this method you can set the effects using {@link #setEffect(int)}.
	 * 
	 * Please note that calling this method will not update the current effect. You will need to call {@link #setEffect(int)} manually.
	 * 
	 * @see ColorTransform#setPartialEffect(int)
	 * 
	 * @param partialEnabled
	 *            true if enabled, false otherwise.
	 */
	public void enablePartialEffects(boolean partialEnabled) {
		mPartialEnabled = partialEnabled;
	}

	/**
	 * Sets the transformation effect being used for the camera preview.
	 * 
	 * @see ColorTransform#COLOR_EFFECT_DALTONIZE
	 * @see ColorTransform#COLOR_EFFECT_FALSE_COLORS
	 * @see ColorTransform#COLOR_EFFECT_INTENSIFY_DIFFERENCE
	 * @see ColorTransform#COLOR_EFFECT_NONE
	 * @see ColorTransform#COLOR_EFFECT_SIMULATE
	 * 
	 * @param effect
	 *            to be set.
	 */
	public void setEffect(int effect) {
		if (mPartialEnabled)
			ColorTransform.setPartialEffect(effect);
		else
			ColorTransform.setEffect(effect);
	}

	/**
	 * Manually refresh the Bitmap for example when setting a new effect when the camera is not previewing.
	 */
	public void refreshBitmap() {
		Debug.msg(LOG_TAG, "Effect on Previewimage");
		ColorTransform.transformImageToBitmap(mDataBuffer, mPreviewWidth, mPreviewHeight, mBitmap);
		invalidate();
	}

	/**
	 * Set the Handler used to send messages to the Activity acting as the model.
	 * 
	 * @param mHandler
	 */
	public void setActivityHandler(Handler mHandler) {
		mActivityHandler = mHandler;
	}

	/**
	 * Whether or not to show the RGB values in the color recognition popup.
	 * 
	 * @param showRGB
	 */
	public void setShowRGB(boolean showRGB) {
		mPopup.setShowRGB(showRGB);
	}

	/**
	 * Whether or not to show the HSV values in the color recognition popup.
	 * 
	 * @param showHSV
	 */
	public void setShowHSV(boolean showHSV) {
		mPopup.setShowHSV(showHSV);
	}

	class EyeCamViewLongClickListener implements OnLongClickListener {

		@Override
		public boolean onLongClick(View v) {
			mPopup.dismiss();
			mActivityHandler.sendEmptyMessage(EyeCamActivity.SHOW_SETTINGS_MENU);
			return true;
		}
	}

	class EyeCamViewTouchListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				mActivityHandler.sendEmptyMessage(EyeCamActivity.CAMERA_STOP_PREVIEW);
				mPopup.dismiss();

				int x = Math.max(0, (int) event.getX());
				int y = Math.max(0, (int) event.getY());
				int scaleX = scale(x);
				int scaleY = scale(y);
				mPopup.showColorBubbleAt(x, y, scaleX, scaleY);
				mPopup.showColorBubbleAtNew(x, y, scaleX, scaleY);
				Debug.msg(LOG_TAG, "Popup Location on Screen: x: " + x + " y: " + y);
				return false;
			}
			return false;
		}

		private int scale(int x) {
			return mIsScaled ? (int) (x / mScaleFactor) : x;
		}
	}
}
