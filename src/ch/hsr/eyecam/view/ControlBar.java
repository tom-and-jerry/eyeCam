package ch.hsr.eyecam.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import ch.hsr.eyecam.Orientation;
import ch.hsr.eyecam.R;

/**
 * A class extending android.widget.LinearLayout so that the class can
 * control the animation of the children.
 *  
 * @author Patrice Mueller
 * @see <a href="http://developer.android.com/reference/
 * 			android/vwidget/LinearLayout.html">
 * 			android.widget.LinearLayout</a>
 */

public class ControlBar extends LinearLayout {

	private Animation mAnimationPortraitLeft ,mAnimationPortraitRight
					,mAnimationLeft ,mAnimationRight;
	private Orientation mLastKnowOrientation;
	
	
	private final static String LOG_TAG = "ch.hsr.eyecam.view.ControlBar";
	
	public ControlBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		mAnimationPortraitLeft = AnimationUtils.loadAnimation(
				context.getApplicationContext()
				,R.anim.control_to_left_portrait);
		
		mAnimationPortraitRight = AnimationUtils.loadAnimation(
				context.getApplicationContext()
				,R.anim.control_to_right_portrait);
		
		mAnimationLeft = AnimationUtils.loadAnimation(
				context.getApplicationContext()
				,R.anim.control_to_left_lanscape);
		
		mAnimationRight = AnimationUtils.loadAnimation(
				context.getApplicationContext()
				,R.anim.control_to_right_lanscape);
	}
	
	private void rotateButtons(Animation animation){
		for(int i=0;i < getChildCount(); ++i)
			getChildAt(i).startAnimation(animation);
	}
	
	public void rotate(Orientation orientation){
		
		if(mLastKnowOrientation == Orientation.LANDSCAPE_LEFT
			&& orientation == Orientation.PORTRAIT)
			rotateButtons(mAnimationPortraitLeft);
		
		if(mLastKnowOrientation == Orientation.LANDSCAPE_RIGHT
				&& orientation == Orientation.PORTRAIT)
				rotateButtons(mAnimationPortraitRight);
				
		if(orientation == Orientation.LANDSCAPE_LEFT)
			rotateButtons(mAnimationLeft);
				
		if(orientation == Orientation.LANDSCAPE_RIGHT)
			rotateButtons(mAnimationRight);
		
		if(orientation == Orientation.UNKNOW){
			rotateButtons(mAnimationPortraitLeft);
		}
		
		Log.d(LOG_TAG, "Turn to "+orientation);					
		mLastKnowOrientation = orientation;
	}
	
}
