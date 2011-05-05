package ch.hsr.eyecam.widget;

import ch.hsr.eyecam.Orientation;
import ch.hsr.eyecam.R;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ScrollView;

public class MenuBubble extends PopupWindow{
	
	private View mAnchorView;
	private BubbleView mBubbleView;
	private int[] mLocation;
	private View mContentView;
	private ScrollView mScrollView;
	private ImageView mArrow;
	private int mArrowWidth;
	
	private static final int MENU_WIDTH = 250;
	private static final int MENU_HEIGHT = 250;

	public MenuBubble(View anchor) {
		super(anchor.getContext());
		Context context  = anchor.getContext();
		mAnchorView = anchor;
		mLocation = new int[2];
		
		
		mArrow = new ImageView(context);
		mArrow.setBackgroundResource(R.drawable.arrow_right);
		mArrowWidth = mArrow.getWidth();
		
		setAnimationStyle(android.R.style.Animation_Dialog);
		setBackgroundDrawable(null);
		setWidth(MENU_WIDTH+mArrowWidth);
		setHeight(MENU_HEIGHT);
	}
	
	public MenuBubble(View anchor, View contentView) {
		this(anchor);
		setContentView(contentView);
	}
	
	@Override
	public void setContentView(View contentView) {
		mContentView = contentView;
		
		mScrollView = new ScrollView(contentView.getContext());
		mScrollView.setFillViewport(false);
		mScrollView.setVerticalScrollBarEnabled(true);
		mScrollView.addView(mContentView);
		
		mBubbleView = new BubbleView(mScrollView);
		mBubbleView.setOrientation(Orientation.PORTRAIT);
		mBubbleView.setArrowStyle(R.drawable.popup_arrow_none);
		
		super.setContentView(mBubbleView);
	}

	public void setContentOrientation(Orientation orientation){
		mBubbleView.setOrientation(orientation);
		if (isShowing()) mBubbleView.updateView();
	}
	
	public void show(){
		mAnchorView.getLocationOnScreen(mLocation);
		showAtLocation(mAnchorView, Gravity.NO_GRAVITY, 
				mLocation[0]-MENU_WIDTH, 
				mLocation[1]-MENU_HEIGHT+mAnchorView.getHeight()+10);
	}
}
