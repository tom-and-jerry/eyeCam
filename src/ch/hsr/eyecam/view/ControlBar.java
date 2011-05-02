package ch.hsr.eyecam.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import ch.hsr.eyecam.EyeCamActivity;
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
	private Handler mActivityHandler;	
	
	private OnClickListener mOnClickListenerPlayPause = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if(((StateImageButton)v).isChecked())
				mActivityHandler.sendEmptyMessage(EyeCamActivity.CAMERA_STOP_PREVIEW);
			else
				mActivityHandler.sendEmptyMessage(EyeCamActivity.CAMERA_START_PREVIEW);
		}
	};

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
	
	public void enableOnLickListerByPlayPausButton(){
		findViewById(R.id.imageButton_Pause).setOnClickListener(mOnClickListenerPlayPause);
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
	

	/**
	 * This method sets the activity handler used to send messages
	 * for starting and stopping the Camera preview since ControlBar
	 * doesn't and shouldn't know about the Camera instance itself.
	 * 
	 * @param handler the activity handler used for message passing.
	 * @see <a href="http://developer.android.com/reference/
	 *		android/os/Handler.html">
	 * 		android.os.Handler</a>
	 */
	public void setActivityHandler(Handler handler) {
		mActivityHandler = handler;
	}
		
}
