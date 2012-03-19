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
 * Just added the possibility to add a default value so that you have a
 * map-construction when you use it with the PreferencesRadioGroup.
 * 
 * @author jimmypoms
 * 
 * @see RadioButton
 */
public class PreferencesCheckBox extends CheckBox implements
		OnCheckedChangeListener {
	private SharedPreferences mSharedPreferences;
	private String mDescription;
	private String mKey;

	public PreferencesCheckBox(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.PreferencesRadioButton);
		mDescription = typedArray
				.getString(R.styleable.PreferencesRadioButton_description);

		typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.PreferencesRadioGroup);
		mKey = typedArray.getString(R.styleable.PreferencesRadioGroup_key);

		setTextColor(Color.DKGRAY);
		setBackgroundResource(R.drawable.settings_selector);
		
		CharSequence title = getText();
		int length = title.length();
		SpannableString text = new SpannableString(title + "\n" + mDescription);
		text.setSpan(new StyleSpan(Typeface.BOLD), 0, length, 0);
		text.setSpan(new RelativeSizeSpan(0.8f), length, length + mDescription.length()+1, 0);
		
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
		setChecked(mSharedPreferences.getBoolean(mKey, false));
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		Editor editor = mSharedPreferences.edit();
		editor.putBoolean(mKey, isChecked);
		editor.commit();
	}
}
