package ch.hsr.eyecam.widget;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import ch.hsr.eyecam.Debug;
import ch.hsr.eyecam.Orientation;

/**
 * Implements an orientation aware toast. The FloatingBubble class is
 * used as a base for this implementation.
 * 
 * @author jimmypoms
 *
 */
public class ToastBubble {
	public static final long TIME_LONG = 4000;
	public static final long TIME_SHORT = 1000;

	private static final String LOG_TAG = "ToastBubble";
	private static final int OFFSET = 120;
	private Handler mHandler;
	private View mViewParent;
	private FloatingBubble mFloatingBubble;
	private int mGravity;
	private int mOffsetX;
	private int mOffsetY;
	
	private Runnable mRunnable = new Runnable() {
		public void run() {
			mFloatingBubble.dismiss();
		}
	};

	public ToastBubble(Context context, View parent) {
		mHandler = new Handler();
		mFloatingBubble = new FloatingBubble(context, parent);
		mFloatingBubble.setAnimationStyle(android.R.style.Animation_Toast);
		mFloatingBubble.setClippingEnabled(true);
		
		mFloatingBubble.setArrowStyle(BubbleView.ARROW_NONE);
		mFloatingBubble.setTextSize(7);
		mFloatingBubble.setAlpha(200);
		mFloatingBubble.setMargins(5);

		mViewParent = parent;
	}

	/**
	 * Sets the orientation of the toast.
	 * 
	 * @param orientation
	 */
	public void setOrientation(Orientation orientation) {
		switch (orientation) {
		case PORTRAIT:
			mGravity = Gravity.RIGHT | Gravity.CENTER;
			mOffsetX = OFFSET;
			mOffsetY = 0;
			break;
		case LANDSCAPE_LEFT:
			mGravity = Gravity.BOTTOM | Gravity.CENTER;
			mOffsetY = OFFSET / 2;
			mOffsetX = 0;
			break;
		case LANDSCAPE_RIGHT:
			mGravity = Gravity.TOP | Gravity.CENTER;
			mOffsetY = OFFSET / 2;
			mOffsetX = 0;
			break;
		default:
			mGravity = Gravity.NO_GRAVITY;
		}
		mFloatingBubble.setOrientation(orientation);
	}

	/**
	 * show the toast for TIME_SHORT.
	 */
	public void show() {
		show(TIME_SHORT);
	}

	/**
	 * show the toast for a specific time.
	 * 
	 * @param timeout in milliseconds
	 */
	public void show(long timeout) {
		mFloatingBubble.showAtLocation(mViewParent, mGravity, mOffsetX,
				mOffsetY);
		delayDismissPopup(timeout);
		
		Debug.msg(LOG_TAG, "showing toast for " + Long.toString(timeout) + " ms");
	}

	private void delayDismissPopup(long time) {
		mHandler.postDelayed(mRunnable, time);
	}

	/**
	 * Set the text for the toast.
	 * 
	 * @param text
	 */
	public void setText(CharSequence text) {
		mFloatingBubble.setText(text);
	}

	/**
	 * Set the additional text for the toast.
	 * 
	 * @param text
	 */
	public void setAdditionalText(CharSequence text) {
		StringBuilder t;
		if (text == null) {
			t = null;
		} else
			t = new StringBuilder(text);
		mFloatingBubble.setAdditionalText(t);
	}
	
	/**
	 * setWidth of FloatingBubble.
	 * 
	 * @param width
	 */
	public void setWidth(int width) {
		mFloatingBubble.setWidth(width);
	}

	public void setTextSize(int size){
		mFloatingBubble.setTextSize(size);
	}
}
