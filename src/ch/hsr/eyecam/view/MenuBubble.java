package ch.hsr.eyecam.view;

import ch.hsr.eyecam.Orientation;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.ScrollView;

public class MenuBubble extends PopupWindow{
	
	private View mAnchorView;
	private BubbleView mBubbleView;
	private int[] mLocation;
	private ScrollView mContentView;
	
	private static final int MENU_WIDTH = 200;
	private static final int MENU_HIGHT = 200;

	public MenuBubble(View anchor) {
		super(anchor.getContext());
		mAnchorView = anchor;
		mLocation = new int[2];
		
		setAnimationStyle(android.R.style.Animation_Dialog);
		setBackgroundDrawable(null);
		setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
		setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
	}
	
	public MenuBubble(View anchor, View contentView) {
		this(anchor);
		setContentView(contentView);
	}
	
	@Override
	public void setContentView(View contentView) {
		mContentView = new ScrollView(contentView.getContext());
		mContentView.setFillViewport(false);
		mContentView.setVerticalScrollBarEnabled(true);
		mContentView.addView(contentView, MENU_WIDTH, MENU_HIGHT);

		mBubbleView = new BubbleView(mContentView);
		mBubbleView.setOrientation(Orientation.LANDSCAPE_LEFT);
//		mBubbleView.setFrameRotation(false);
		
		super.setContentView(mBubbleView);
	}

	public void setArrowStyle(int arrowStyle){
		mBubbleView.setArrowStyle(arrowStyle);
	}
	
	public void show(){
		mAnchorView.getLocationOnScreen(mLocation);
		showAtLocation(mAnchorView, Gravity.NO_GRAVITY, 
				mLocation[0]-MENU_WIDTH, 
				mLocation[1]-MENU_HIGHT);
	}
}
