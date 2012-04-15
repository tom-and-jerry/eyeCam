package ch.hsr.eyecam.preferences;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.CompoundButton;

/**
 * Custom button for the preferences menu that matches the style
 * of the other preferences widgets.
 * 
 * @author jimmypoms
 *
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
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	}
}
