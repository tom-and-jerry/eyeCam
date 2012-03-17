package ch.hsr.eyecam.widget;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.ScrollView;
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
public class MenuBubble extends PopupWindow{
	
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
	 * @param anchor of the Bubble to be shown above.
	 */
	public MenuBubble(View anchor) {
		super(anchor.getContext());
		mAnchorView = anchor;
		
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
		mBubbleView.setArrowStyle(0);
		
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
		mMaxHeight = height;
		super.setHeight(height);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setWidth(int width) {
		mMaxWidth = width;
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
		if (orientation == Orientation.PORTRAIT)
			mHeight = mMaxHeight;
		else
			mHeight = mMaxWidth;
		if (wasShowing) {
			mBubbleView.updateView();
			show();
		}
	}
	
	/**
	 * Shows the menu bubble above the anchor.
	 */
	public void show(){
		super.setHeight(mHeight);
		showAtLocation(mAnchorView, Gravity.CENTER, 0, 0);
	}
}
