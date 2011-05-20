package ch.hsr.eyecam.widget;

import ch.hsr.eyecam.Orientation;
import ch.hsr.eyecam.R;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.PopupWindow;
import android.widget.ScrollView;

/**
 * This class provides the functionality of showing a menu bubble above the 
 * anchor specified in the constructor.
 * 
 * @author Dominik Spengler
 * 
 * @see BubbleView
 * @see PopupWindow
 */
public class MenuBubble extends PopupWindow{
	
	private View mAnchorView;
	private BubbleView mBubbleView;
	private int[] mLocation;
	private View mContentView;
	private ScrollView mScrollView;
	
	private int mWidth;
	private int mHeight;
	private static int OFFSET = 20;

	/**
	 * Simple constructor to initialize a MenuBubble with the given anchor.
	 * 
	 * @param anchor of the Bubble to be shown above.
	 */
	public MenuBubble(View anchor) {
		super(anchor.getContext());
		mAnchorView = anchor;
		mLocation = new int[2];
		
		setTouchable(true);
		setAnimationStyle(android.R.style.Animation_Dialog);
		setBackgroundDrawable(null);
		setWidth(200);
		setHeight(200);
	}
	
	/**
	 * Simple constructor to initialize a MenuBubble with the given anchor
	 * and the given content.
	 * 
	 * @param anchor of the Bubble to be shown above.
	 * @param contentView of the Bubble.
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
		mBubbleView.setArrowStyle(R.drawable.popup_arrow_none);
		mBubbleView.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) dismiss();
			}
		});
		
		super.setContentView(mBubbleView);
	}

	/**
	 * Sets the size of the bubble in device dependent points. If the bubble is
	 * showing when setting the size, it will be dismissed and re-shown. This is
	 * done to ensure the correct measurement of the content view.
	 * 
	 * @see TypedValue#COMPLEX_UNIT_PT
	 * 
	 * @param width in pt
	 * @param height in pt
	 */
	public void setSize(int width, int height){
		int iWidth = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PT, 
				width, mAnchorView.getContext().getResources().getDisplayMetrics());
		int iHeight = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PT, 
				height, mAnchorView.getContext().getResources().getDisplayMetrics());
		
		boolean wasShowing = isShowing();
		dismiss();
		setWidth(iWidth);
		setHeight(iHeight);
		if (wasShowing) show();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setHeight(int height) {
		mHeight= height;
		super.setHeight(height);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setWidth(int width) {
		mWidth = width;
		super.setWidth(width);
	}
	
	/**
	 * Sets the new orientation of the content view. If the bubble is showing
	 * when setting the orientation, it will be dismissed and re-shown. This is
	 * done to ensure the correct measurement of the content view.
	 * 
	 * @see BubbleView#setOrientation(Orientation)
	 * @param orientation of the content to be shown in.
	 */
	public void setContentOrientation(Orientation orientation){
		boolean wasShowing = isShowing();
		dismiss();
		mBubbleView.setOrientation(orientation);
		if (wasShowing) {
			mBubbleView.updateView();
			show();
		}
	}
	
	/**
	 * Shows the menu bubble above the anchor.
	 */
	public void show(){
		mAnchorView.getLocationOnScreen(mLocation);
		showAtLocation(mAnchorView, Gravity.NO_GRAVITY, 
				mLocation[0]-mWidth, 
				mLocation[1]-mHeight+mAnchorView.getHeight()+OFFSET);
	}
}
