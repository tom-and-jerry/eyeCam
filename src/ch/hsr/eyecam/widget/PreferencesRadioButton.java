package ch.hsr.eyecam.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.RadioButton;
import ch.hsr.eyecam.R;

public class PreferencesRadioButton extends RadioButton {
	private int mValue =0;

	public PreferencesRadioButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.PreferencesRadioButton);
		mValue = typedArray.getInt(R.styleable.PreferencesRadioButton_refValue, 0);
	}
	
	public int getValue(){
		return mValue;
	}
}
