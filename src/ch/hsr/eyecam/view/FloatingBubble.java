package ch.hsr.eyecam.view;

import ch.hsr.eyecam.Orientation;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * This class provides the functionality of showing a floating bubble at any
 * location on the screen. This is necessary for displaying the color of the 
 * area touched on the paused camera preview.
 * 
 * @author Dominik Spengler
 * 
 * @see BubbleView
 * @see <a href="http://developer.android.com/reference/
 *		android/widget/PopupWindow.html">
 * 		android.widget.PopupWindow</a>
 */
public class FloatingBubble extends PopupWindow {
	private View mViewParent;
	private BubbleView mBubbleView;
	private Orientation mOrientation;
	private TextView mTextView;

	public FloatingBubble(Context context, View parent){
		super(context);
		mViewParent = parent;
		
		initContentView(context);
		setAnimationStyle(android.R.style.Animation_Dialog);
		
		setClippingEnabled(false);
		setBackgroundDrawable(null);
		setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
		setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
	}

	private void initContentView(Context context) {
		mTextView = new TextView(context);
		mTextView.setTextColor(android.graphics.Color.WHITE);
		mTextView.setGravity(Gravity.CENTER);
		mBubbleView = new BubbleView(mTextView);
		setContentView(mBubbleView);
	}

	/**
	 * This method shows a bubble with an arbitrary string resource at any 
	 * given location on the screen.
	 * 
	 * @param res. The String resource to be shown in the bubble.
	 * @param x. The x position on the screen.
	 * @param y. The y position on the screen.
	 */
	public void showStringResAt(int res, int x, int y) {
		dismiss();
		
		mTextView.setText(res);
		int transX = 0;
		int transY = 0;
		int offset = 0;
		
		switch (mOrientation){
		case LANDSCAPE_LEFT:
			transX = mBubbleView.getWidth() /2;
			transY = mBubbleView.getHeight() + offset;
			break;
		case LANDSCAPE_RIGHT:
			transX = mBubbleView.getWidth() /2;
			transY = -offset;
			break;
		case PORTRAIT:
			transX = mBubbleView.getHeight() + offset;
			transY = mBubbleView.getWidth() /2;
			break;
		}
		
		showAtLocation(mViewParent, Gravity.NO_GRAVITY, x - transX, y - transY);
	}

	/**
	 * This method sets the desired orientation of the bubble.
	 * 
	 * Please note that the bubble will be dismissed if it is already showing.
	 * 
	 * @param orientation the bubble should be shown in.
	 * @see Orientation
	 */
	public void setOrientation(Orientation orientation) {
		if (isShowing()) dismiss();
		mOrientation = orientation;
		mBubbleView.setOrientation(orientation);
	}
}
