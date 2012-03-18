package ch.hsr.eyecam.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
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
		if (isInEditMode())
			return;
		Context appContex = getContext().getApplicationContext();
		mSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(appContex);

		TypedArray typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.PreferencesRadioButton);
		mDescription = typedArray
				.getString(R.styleable.PreferencesRadioButton_description);

		typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.PreferencesRadioGroup);
		mKey = typedArray.getString(R.styleable.PreferencesRadioGroup_key);

		setTextColor(Color.DKGRAY);
		setBackgroundResource(R.drawable.settings_selector);

		setText(Html.fromHtml("<b>" + getText() + "</b>" + "<br />" + "<small>"
				+ mDescription + "</small>"));
	}

	@Override
	protected void onFinishInflate() {
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
