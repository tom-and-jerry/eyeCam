package ch.hsr.eyecam.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.RadioButton;
import ch.hsr.eyecam.R;

/**
 * Just added the possibility to add a default value so that you have a 
 * map-construction when you use it with the PreferencesRadioGroup.
 * 
 * @author Patrice Mueller
 *
 * @see RadioButton
 */
public class PreferencesRadioButton extends RadioButton {
	private int mValue;
	private String mDescription;
	
	public PreferencesRadioButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (isInEditMode()) return;
		TypedArray typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.PreferencesRadioButton);
		mValue = typedArray.getInt(R.styleable.PreferencesRadioButton_intValue, 0);
		mDescription = typedArray.getString(R.styleable.PreferencesRadioButton_description);
		setBackgroundResource(R.drawable.settings_selector);
		setTextColor(Color.DKGRAY);
		
		setText(Html.fromHtml("<b>" + getText() + "</b>" +  "<br />" + 
	            "<small>" + mDescription + "</small>"));
	}
	
	/**
	 * @return The integer value of the resource
	 */
	public int getValue(){
		return mValue;
	}
	
}
