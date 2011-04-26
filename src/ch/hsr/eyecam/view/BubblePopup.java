package ch.hsr.eyecam.view;

import ch.hsr.eyecam.Orientation;
import ch.hsr.eyecam.R;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class BubblePopup extends PopupWindow {
	private View mViewParent;
	private BubbleView mBubbleView;
	private Orientation mOrientation;

	public BubblePopup(Context context, View parent){
		super(context);
		mViewParent = parent;
		
		initContentView(context);
		setAnimationStyle(android.R.style.Animation_Dialog);
		
		setBackgroundDrawable(null);
		setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
		setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
	}

	private void initContentView(Context context) {
		mBubbleView = new BubbleView(context);
		setContentView(mBubbleView);
	}

	public void showStringResAt(int res, int x, int y) {
		dismiss();
		mBubbleView.setText(res);
		int transX = 0;
		int transY = 0;
		int offset = 7;
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

	public void setOrientation(Orientation orientation) {
		if (isShowing()) dismiss();
		mOrientation = orientation;
		mBubbleView.setOrientation(orientation);
	}
	
	public class BubbleView extends LinearLayout {

		private Orientation mOrientation;
		private TextView mTextView;
		private Animation mAnimation, mAnimPortrait, mAnimLandscape, mAnimNone;
		
		public BubbleView(Context context) {
			super(context);
			
			mTextView = new TextView(context);
			Drawable d = context.getResources().getDrawable(R.drawable.popup_above);
			mTextView.setBackgroundDrawable(d);
			mTextView.setTextColor(android.graphics.Color.WHITE);
			addView(mTextView);
			
			setOrientation(LinearLayout.VERTICAL);
			
			setBackgroundColor(android.graphics.Color.TRANSPARENT);
			setOrientation(Orientation.LANDSCAPE_LEFT);
			mAnimPortrait = AnimationUtils.loadAnimation(context, R.anim.orientation_portrait);
			mAnimLandscape = AnimationUtils.loadAnimation(context, R.anim.orientation_landscape_right);
			mAnimNone = new RotateAnimation(context, null);
		}
		
		public void setOrientation(Orientation orientation){
			switch(orientation){
			case LANDSCAPE_LEFT:
				mAnimation = mAnimNone;
				break;
			case LANDSCAPE_RIGHT:
				mAnimation = mAnimLandscape;
				break;
			case PORTRAIT:
				mAnimation = mAnimPortrait;
				break;
			}
			mOrientation = orientation;
		}
		
		public void setText(int resid){
			mTextView.setText(resid);
			mTextView.startAnimation(mAnimation);
		}
		
		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			int h = getMeasuredHeight();
			int w = getMeasuredWidth();
			
			if (mOrientation == Orientation.PORTRAIT) setMeasuredDimension(h, w);
		}
	}
}
