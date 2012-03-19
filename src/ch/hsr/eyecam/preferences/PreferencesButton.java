package ch.hsr.eyecam.preferences;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;

/**
 * Just added the possibility to add a default value so that you have a 
 * map-construction when you use it with the PreferencesRadioGroup.
 * 
 * @author jimmypoms
 *
 * @see RadioButton
 */
public class PreferencesButton extends PreferencesCheckBox {
	
	public PreferencesButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		setButtonDrawable(new BitmapDrawable());
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		setOnCheckedChangeListener(null);
	}
	
	@Override
	protected void initValue() {
	}
	
}
