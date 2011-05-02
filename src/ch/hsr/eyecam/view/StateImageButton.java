package ch.hsr.eyecam.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Checkable;
import android.widget.ImageButton;
import ch.hsr.eyecam.R;

/**
 * A class extending android.widget.ImageButton. The idea behind this class
 * is to merge the benefit of the android.widget.ToggleButton and the 
 * android.widget.ImageButton. Or in other words, we add a State to the 
 * android.widget.ImageButton and each State has it own picture. 
 * 
 * The default State is false.
 * 
 * @author Patrice Mueller
 * @see <a href="http://developer.android.com/reference/
 * 		android/widget/ImageButton.html">android.widget.ImageButton</a>

 * 		<a href="http://developer.android.com/reference/
 * 		android/widget/ToggleButton.html">android.widget.ToggleButton</a>
 * 
 */
public class StateImageButton extends ImageButton implements Checkable{

	private boolean mState;
	private int mImgResTrue, mImgResFalse;
	private static String LOG_TAG = "ch.hsr.eyecam.StateImageButton";
	
	/**
	 * Use this constructor if you want to set the attributes without the 
	 * xml-file
	 * @param contex
	 */
	public StateImageButton(Context contex){
		super(contex);
		mState= false;
		setImage();
	}
	
	/**
	 * This constructor should be used if you want to set the attributes by the 
	 * xml-file. We recommend this way.
	 * @param context
	 * @param attrs
	 */
	public StateImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		mState = false;
		
		
		TypedArray typedArrayAttr = context.obtainStyledAttributes(attrs
				,R.styleable.StateImageButton);
	
		mImgResTrue = typedArrayAttr.getResourceId(R.styleable.StateImageButton_imgResTrue
				,R.drawable.ic_menu_sad);
		
		mImgResFalse = typedArrayAttr.getResourceId(R.styleable.StateImageButton_imgResFalse
				,R.drawable.ic_menu_sad);
		setImage();
	}
	
	private void setImage(){
		if(mState) this.setImageResource(mImgResTrue);
		if(!mState) this.setImageResource(mImgResFalse);
	}
	
	/**
	 * To set the image which will display by the state true. It shoul be set 
	 * by the xml-file. 
	 * 
	 * @param resId
	 */
	public void setImgResTrue(int resId){
		mImgResTrue = resId;
	}
	
	/**
	 * To set the image which will display by the state false. It shoul be set 
	 * by the xml-file. 
	 * 
	 * @param resId
	 */
	
	public void setImgResFalse(int resId){
		mImgResFalse = resId;
	}

	@Override
	public boolean isChecked() {
		return mState;
	}

	@Override
	public void setChecked(boolean checked) {
		mState = checked;
		setImage();
	}

	/**
	 * This method should be used if you want to change the State of this
	 * Button. It handles the switch of the Image Resources (recommended)
	 */
	@Override
	public void toggle() {
		setChecked(!isChecked());
		setImage();
		Log.d(LOG_TAG, "State has changed! From:"+!mState +" To: "+mState);
		
	}
	
	@Override
	public boolean performClick() {
		toggle();
		return super.performClick();
	}

}
