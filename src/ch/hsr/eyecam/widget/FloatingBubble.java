package ch.hsr.eyecam.widget;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import ch.hsr.eyecam.Debug;
import ch.hsr.eyecam.Orientation;

/**
 * This class provides the functionality of showing a floating bubble at any
 * location on the screen. This is necessary for displaying the color of the
 * area touched on the paused camera preview.
 * 
 * @author Dominik Spengler
 * 
 * @see BubbleView
 * @see PopupWindow
 */
public class FloatingBubble extends PopupWindow {
	private View mViewParent;
	private BubbleView mBubbleView;
	private Orientation mOrientation;
	private TextView mTextView;
	private TextView mAdditionalText;
	private LinearLayout mContentView;
	private int OFFSET_X = -1;
	private int OFFSET_Y = -1;

	public FloatingBubble(Context context, View parent) {
		super(context);
		mViewParent = parent;

		initContentView(context);
		setAnimationStyle(android.R.style.Animation_Dialog);

		setTouchable(false);
		setClippingEnabled(false);
		setBackgroundDrawable(null);
		setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
		setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
	}

	private void initContentView(Context context) {
		mTextView = new TextView(context);
		mTextView.setTextColor(android.graphics.Color.WHITE);
		mTextView.setGravity(Gravity.CENTER);

		mAdditionalText = new TextView(context);
		mAdditionalText.setTextColor(android.graphics.Color.LTGRAY);
		mAdditionalText.setGravity(Gravity.CENTER);

		mContentView = new LinearLayout(context);
		mContentView.setOrientation(LinearLayout.VERTICAL);
		mContentView.addView(mTextView);
		// mContentView.addView(mAdditionalText);

		mBubbleView = new BubbleView(mContentView);
		setContentView(mBubbleView);
	}

	/**
	 * This method shows a bubble with an arbitrary string resource at any given
	 * location on the screen.
	 * 
	 * @param res
	 *            The String resource to be shown in the bubble.
	 * @param x
	 *            The x position on the screen.
	 * @param y
	 *            The y position on the screen.
	 */
	public void showStringResAt(int res, int x, int y) {
		dismiss();
		if (OFFSET_X == -1)
			getParentLocationOnScreen();

		mContentView.removeView(mAdditionalText);
		if (mAdditionalText.getText().length() > 0)
			mContentView.addView(mAdditionalText);
		mBubbleView.updateView();

		int transX = 0;
		int transY = 0;
		int offset = 0;

		switch (mOrientation) {
		case LANDSCAPE_LEFT:
			transX = mBubbleView.getMeasuredWidth() / 2;
			transY = mBubbleView.getMeasuredHeight() + offset;
			break;
		case LANDSCAPE_RIGHT:
			transX = mBubbleView.getMeasuredWidth() / 2;
			transY = -offset;
			break;
		case PORTRAIT:
			transX = mBubbleView.getMeasuredWidth() + offset;
			transY = mBubbleView.getMeasuredHeight() / 2;
			break;
		}

		mTextView.setText(res);
		showAtLocation(mViewParent, Gravity.NO_GRAVITY,
				(x - transX) + OFFSET_X, (y - transY) + OFFSET_Y);
	}

	private void getParentLocationOnScreen() {
		int[] location = new int[2];
		mViewParent.getLocationOnScreen(location);
		OFFSET_X = location[0];
		OFFSET_Y = location[1];
		Debug.msg("FloatingBubble", "offset_x: " + OFFSET_X);
		Debug.msg("FloatingBubble", "offset_y: " + OFFSET_Y);		
	}

	/**
	 * This method sets the desired orientation of the bubble.
	 * 
	 * Please note that the bubble will be dismissed if it is already showing.
	 * 
	 * @param orientation
	 *            the bubble should be shown in.
	 * @see Orientation
	 */
	public void setOrientation(Orientation orientation) {
		dismiss();
		mOrientation = orientation;
		mBubbleView.setOrientation(orientation);
	}

	/**
	 * Sets the text size of the string shown in the bubble. The size value will
	 * be interpreted as screen dependent points.
	 * 
	 * @see TypedValue#COMPLEX_UNIT_PT
	 * @param size
	 *            in pt
	 */
	public void setTextSize(int size) {
		mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PT, size);
		mAdditionalText.setTextSize(TypedValue.COMPLEX_UNIT_PT, size - 1);
		mBubbleView.updateView();
	}

	public void setAdditionalText(StringBuilder string) {
		mAdditionalText.setText(string);
	}
}
