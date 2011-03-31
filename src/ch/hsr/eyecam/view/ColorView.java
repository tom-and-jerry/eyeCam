package ch.hsr.eyecam.view;

import ch.hsr.eyecam.EyeCamActivity;
import ch.hsr.eyecam.colormodel.ColorTransform;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
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
	private static String LOG_TAG = "ch.hsr.eyecam.view.ColorView";

	public ColorView(Context context) {
		super(context);
		setClickable(true);
	}

	public ColorView(Context context, AttributeSet attrs) {
		super(context, attrs);
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
			mBitmap = Bitmap.createBitmap(getWidth(), getHeight(),
					Bitmap.Config.RGB_565);
			Log.d(LOG_TAG, "Bitmap size: W: " + getWidth() + " H: "
					+ getHeight());
			mActivityHandler.sendEmptyMessage(EyeCamActivity.CAMERA_START_PREVIEW);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawBitmap(mBitmap, 0, 0, null);
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

}
