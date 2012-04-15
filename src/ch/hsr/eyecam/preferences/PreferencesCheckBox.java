package ch.hsr.eyecam.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import ch.hsr.eyecam.R;

/**
 * This Checkbox automatically updates the boolean preferences keys as 
 * stated in the XML.
 * 
 * Additionally there is the possibility to add a description.
 * 
 * @author jimmypoms
 * 
 * @see RadioButton
 * @see PreferencesRadioGroup
 */
public class PreferencesCheckBox extends CheckBox implements
		OnCheckedChangeListener {
	private SharedPreferences mSharedPreferences;
	private String mDescription;
	private String mKey;
	private boolean mDefault;

	public PreferencesCheckBox(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.Preferences);
		mDescription = typedArray
				.getString(R.styleable.Preferences_description);
		mKey = typedArray.getString(R.styleable.Preferences_key);

		typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.PreferencesCheckbox);
		mDefault = typedArray.getBoolean(R.styleable.PreferencesCheckbox_boolValue, false);

		setTextColor(Color.DKGRAY);
		setBackgroundResource(R.drawable.settings_selector);
		
		CharSequence title = getText();
		int length = title.length();
		SpannableString text = new SpannableString(title + "\n" + mDescription);
		text.setSpan(new StyleSpan(Typeface.BOLD), 0, length, 0);
		text.setSpan(new RelativeSizeSpan(0.7f), length, length + mDescription.length()+1, 0);
		
		setText(text);
		
		if (isInEditMode())
			return;

		Context appContex = getContext().getApplicationContext();
		mSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(appContex);
	}

	@Override
	protected void onFinishInflate() {
		if (isInEditMode()) 
			return;
		initValue();
		setOnCheckedChangeListener(this);
		super.onFinishInflate();
	}

	protected void initValue() {
		if (mSharedPreferences.getBoolean(mKey, mDefault)){
			performClick();
			onCheckedChanged(null, true);
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		Editor editor = mSharedPreferences.edit();
		editor.putBoolean(mKey, isChecked);
		editor.commit();
	}
}
