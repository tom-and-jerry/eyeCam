package ch.hsr.eyecam.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import ch.hsr.eyecam.Debug;
import ch.hsr.eyecam.EyeCamActivity;
import ch.hsr.eyecam.Orientation;
import ch.hsr.eyecam.R;
import ch.hsr.eyecam.widget.MenuBubble;
import ch.hsr.eyecam.widget.StateImageButton;

/**
 * This class contains all control items like the play/paus or the 
 * settingsbutton. 
 * 
 * It is recommended to add new feature like lightbutton or things like that
 * in this Control-Bar. So that the design of the communication between the
 * contorl-items an the activit-class is consistent.
 * 
 *  
 * @author Patrice Mueller
 * @see <a href="http://developer.android.com/reference/
 * 			android/widget/LinearLayout.html">
 * 			android.widget.LinearLayout</a>
 */

public class ControlBar extends LinearLayout {
	private Animation mAnimationPortraitLeft ,mAnimationPortraitRight
					,mAnimationLeft ,mAnimationRight;
	private Handler mActivityHandler;	
	private Orientation mLastKnowOrientation;
	
	private OnClickListener mOnClickPlayPause = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if(((StateImageButton)v).isChecked()){
				mActivityHandler.sendEmptyMessage(EyeCamActivity.CAMERA_STOP_PREVIEW);
			}
			else
				mActivityHandler.sendEmptyMessage(EyeCamActivity.CAMERA_START_PREVIEW);
		}
	};
	
	private OnClickListener mOnClickLight = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(((StateImageButton)v).isChecked())
				mActivityHandler.sendEmptyMessage(EyeCamActivity.CAMERA_LIGHT_OFF);
			else
				mActivityHandler.sendEmptyMessage(EyeCamActivity.CAMERA_LIGHT_ON);
		}
	};
	
	private OnClickListener mOnClickFilter = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if(((StateImageButton)v).isChecked())
				mActivityHandler.sendEmptyMessage(EyeCamActivity.SECONDARY_FILTER_ON);
			else
				mActivityHandler.sendEmptyMessage(EyeCamActivity.PRIMARY_FILTER_ON);
		}		
	};
	
	private OnLongClickListener mOnLongClickFilter = new OnLongClickListener() {
		
		@Override
		public boolean onLongClick(View v) {
			if(mButtonFilter.isChecked()) 
				mActivityHandler.sendEmptyMessage(EyeCamActivity.SHOW_PRIMARY_FILTER_MENU);
			else 
				mActivityHandler.sendEmptyMessage(EyeCamActivity.SHOW_SECONDARY_FILTER_MENU);
			return true;
		}
	};
	
	private StateImageButton mButtonPlayPause;
	private StateImageButton mButtonLight;
	private StateImageButton mButtonFilter;

	private final static String LOG_TAG = "ch.hsr.eyecam.view.ControlBar";
	
	public ControlBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		if(isInEditMode())return;
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
	
	/**
	 * You can only add a OnClickListener to a Object when it has passed the
	 * initialization process.
	 * 
	 * So you will need to call this method at first after you had build the
	 * ControlBar-Object, to enable all OnlickListeners for the children of the
	 * ContorlBar-Object.
	 */
	public void enableOnClickListeners(){
		mButtonPlayPause = (StateImageButton) findViewById(R.id.imageButton_Pause);
		mButtonLight = (StateImageButton) findViewById(R.id.imageButton_Light);
		mButtonFilter = (StateImageButton) findViewById(R.id.imageButton_Filter);
		
		mButtonPlayPause.setOnClickListener(mOnClickPlayPause);
		mButtonLight.setOnClickListener(mOnClickLight);
		mButtonFilter.setOnClickListener(mOnClickFilter);
		mButtonFilter.setOnLongClickListener(mOnLongClickFilter);
		
		mButtonPlayPause.setImageChange(false);
		mButtonLight.setImageChange(false);
		mButtonFilter.setImageChange(false);
	}
	
	protected void inflateMenu(MenuBubble menu) {
		if (menu.isShowing()) menu.dismiss();
		else menu.show();
	}
	
	
	private void rotateChildViews(Animation animation){
		for(int i=0;i < getChildCount(); ++i)
			getChildAt(i).startAnimation(animation);
	}
	
	/**
	 * Rotate all children of the ContorlBar to the given orientation, with
	 * animation.
	 * 	
	 * @param orientation
	 */
	public void rotate(Orientation orientation){
		
		if(mLastKnowOrientation == Orientation.LANDSCAPE_LEFT
			&& orientation == Orientation.PORTRAIT)
			rotateChildViews(mAnimationPortraitLeft);
		
		if(mLastKnowOrientation == Orientation.LANDSCAPE_RIGHT
				&& orientation == Orientation.PORTRAIT)
				rotateChildViews(mAnimationPortraitRight);
				
		if(orientation == Orientation.LANDSCAPE_LEFT)
			rotateChildViews(mAnimationLeft);
				
		if(orientation == Orientation.LANDSCAPE_RIGHT)
			rotateChildViews(mAnimationRight);
		
		if(orientation == Orientation.UNKNOW){
			rotateChildViews(mAnimationPortraitLeft);
		}
		
		Debug.msg(LOG_TAG, "Turn to "+orientation);					
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
	
	/**
	 * This method is indirection to hold the communication-design clean.
	 *  
	 * @param isEnabled
	 */
	public void enableLightButton(boolean isEnabled) {
		mButtonLight.setEnabled(isEnabled);
	}

	/**
	 * Manage the image change of the play button
	 *  
	 * @param isPlaying
	 */
	public void setButtonPlay(boolean isPlaying) {
		mButtonPlayPause.setChecked(isPlaying);
	}

	/**
	 * Manage the image change of the filter button
	 *  
	 * @param isPrimaryFilter
	 */
	public void setButtonFilter(boolean isPrimaryFilter) {
		mButtonFilter.setChecked(isPrimaryFilter);
	}
	
	/**
	 * Manage the image change of the light button
	 *  
	 * @param hasLight
	 */
	public void setButtonLight(boolean hasLight) {
		mButtonLight.setChecked(hasLight);
	}
	
	/**
	 * Check wether primary filter button is checked.
	 */
	public boolean isPrimaryFilterRunning() {
		return !mButtonFilter.isChecked();
	}
}
