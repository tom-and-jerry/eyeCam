package ch.hsr.eyecam.widget;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import ch.hsr.eyecam.Debug;
import ch.hsr.eyecam.Orientation;

/**
 * This class provides the functionality of showing a menu bubble above the
 * anchor specified in the constructor.
 * 
 * @author Dominik Spengler
 * 
 * @see BubbleView
 * @see PopupWindow
 */
public class MenuBubble extends PopupWindow {

	private static final String LOG_TAG = "MenuBubble";
	private View mAnchorView;
	private BubbleView mBubbleView;
	private View mContentView;
	private ScrollView mScrollView;
	private int mMaxHeight;
	private int mHeight;
	private int mMaxWidth;

	/**
	 * Simple constructor to initialize a MenuBubble with the given anchor.
	 * 
	 * @param anchor
	 *            of the Bubble to be shown above.
	 */
	public MenuBubble(View anchor) {
		super(anchor.getContext());
		mAnchorView = anchor;

		setTouchable(true);
		setClippingEnabled(false);
		setAnimationStyle(android.R.style.Animation_Dialog);
		setBackgroundDrawable(null);
		setWidth(200);
		setHeight(200);
	}

	/**
	 * Simple constructor to initialize a MenuBubble with the given anchor and
	 * the given content.
	 * 
	 * @param anchor
	 *            of the Bubble to be shown above.
	 * @param contentView
	 *            of the Bubble.
	 */
	public MenuBubble(View anchor, View contentView) {
		this(anchor);
		setContentView(contentView);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * The content view will get wrapped in a ScrollView.
	 */
	@Override
	public void setContentView(View contentView) {
		mContentView = contentView;

		mScrollView = new ScrollView(contentView.getContext());
		mScrollView.setFillViewport(false);
		mScrollView.setVerticalScrollBarEnabled(true);
		mScrollView.setScrollbarFadingEnabled(false);
		mScrollView.addView(mContentView);

		mBubbleView = new BubbleView(mScrollView);
		mBubbleView.setOrientation(Orientation.PORTRAIT);
		mBubbleView.setArrowStyle(0);
		initOnTouchListener();

		super.setContentView(mBubbleView);
	}
	
	/**
	 * Make sure the view gets updated everytime somebody clicks in it.
	 */
	private void initOnTouchListener() {
		mBubbleView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					Debug.msg(LOG_TAG, "Touch intercepted");
					if (isOutsideContent(event))
						dismiss();
				}
				return false;
			}
		});
	}

	protected boolean isOutsideContent(MotionEvent event) {
		int[] loc = new int[2];
		mContentView.getLocationOnScreen(loc);
		int height = mContentView.getHeight();
		
		int x = (int) event.getRawX();
		if (x > loc[0]+height){
			Debug.msg(LOG_TAG, "touch outside content");
			return true;
		}

		return false;
	}

	/**
	 * Sets the size of the bubble in device dependent points. If the bubble is
	 * showing when setting the size, it will be dismissed and re-shown. This is
	 * done to ensure the correct measurement of the content view.
	 * 
	 * @see TypedValue#COMPLEX_UNIT_PT
	 * 
	 * @param width
	 *            in pt
	 * @param height
	 *            in pt
	 */
	public void setSize(int width, int height) {
		int iWidth = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_PT, width, mAnchorView.getContext()
						.getResources().getDisplayMetrics());
		int iHeight = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_PT, height, mAnchorView.getContext()
						.getResources().getDisplayMetrics());

		boolean wasShowing = isShowing();
		dismiss();
		setMaxWidth(iWidth);
		setMaxHeight(iHeight);
		if (wasShowing)
			show();
	}

	/**
	 * Sets the maximal Height
	 * 
	 * @param height
	 *            in pixel
	 */
	public void setMaxHeight(int height) {
		mMaxHeight = height;
		super.setHeight(height);

		Debug.msg(LOG_TAG, "setting height: " + height);
	}

	/**
	 * Sets the maximal width
	 * 
	 * @param width
	 *            in pixel
	 */
	public void setMaxWidth(int width) {
		mMaxWidth = width;
		super.setWidth(width);

		Debug.msg(LOG_TAG, "setting width: " + width);
	}

	/**
	 * Sets the new orientation of the content view. If the bubble is showing
	 * when setting the orientation, it will be dismissed and re-shown. This is
	 * done to ensure the correct measurement of the content view.
	 * 
	 * @see BubbleView#setOrientation(Orientation)
	 * @param orientation
	 *            of the content to be shown in.
	 */
	public void setContentOrientation(Orientation orientation) {
		boolean wasShowing = isShowing();
		dismiss();
		mBubbleView.setOrientation(orientation);
		if (orientation == Orientation.PORTRAIT) {
			mHeight = mMaxHeight;
		} else {
			mHeight = mMaxWidth;
		}
		super.setHeight(mHeight);
		if (wasShowing) {
			mBubbleView.updateView();
			show();
		}
	}

	/**
	 * Shows the menu bubble in the middle of the anchor.
	 */
	public void show() {
		showAtLocation(mAnchorView, Gravity.CENTER, 0, 0);
		Debug.msg(LOG_TAG,
				"measured height: " + mContentView.getMeasuredWidth());
		Debug.msg(LOG_TAG,
				"measured width: " + mContentView.getMeasuredHeight());
	}
}
