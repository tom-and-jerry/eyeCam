package ch.hsr.eyecam.widget;

import ch.hsr.eyecam.Orientation;
import ch.hsr.eyecam.R;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.PopupWindow;
import android.widget.ScrollView;

public class MenuBubble extends PopupWindow{
	
	private View mAnchorView;
	private BubbleView mBubbleView;
	private int[] mLocation;
	private View mContentView;
	private ScrollView mScrollView;
	
	private static int MENU_WIDTH = 200;
	private static int MENU_HEIGHT = 200;
	private static int OFFSET = 20;

	public MenuBubble(View anchor) {
		super(anchor.getContext());
		mAnchorView = anchor;
		mLocation = new int[2];
		
		setTouchable(true);
		setAnimationStyle(android.R.style.Animation_Dialog);
		setBackgroundDrawable(null);
		setWidth(MENU_WIDTH);
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

	public void setSize(int width, int height){
		MENU_HEIGHT = height;
		MENU_WIDTH = width;
		OFFSET = width / 10;
	}
	
	public void setContentOrientation(Orientation orientation){
		mBubbleView.setOrientation(orientation);
		if (isShowing()) mBubbleView.updateView();
	}
	
	public void show(){
		mAnchorView.getLocationOnScreen(mLocation);
		showAtLocation(mAnchorView, Gravity.NO_GRAVITY, 
				mLocation[0]-MENU_WIDTH, 
				mLocation[1]-MENU_HEIGHT+mAnchorView.getHeight()+OFFSET);
	}
}
