package ch.hsr.eyecam.preferences;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
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
 * @see PreferencesRadioGroup
 */
public class PreferencesRadioButton extends RadioButton {
	private int mValue;
	private String mDescription;

	public PreferencesRadioButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		setBackgroundResource(R.drawable.settings_selector);

		TypedArray typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.Preferences);
		mDescription = typedArray
				.getString(R.styleable.Preferences_description);
		setTextColor(Color.DKGRAY);

		CharSequence title = getText();
		int length = title.length();
		SpannableString text = new SpannableString(title + "\n" + mDescription);
		text.setSpan(new StyleSpan(Typeface.BOLD), 0, length, 0);
		if (mDescription != null)
			text.setSpan(new RelativeSizeSpan(0.7f), length, length
					+ mDescription.length()+1, 0);

		setText(text);
		typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.PreferencesRadioButton);
		mValue = typedArray.getInt(R.styleable.PreferencesRadioButton_intValue,
				0);
	}

	/**
	 * @return The integer value of the resource
	 */
	public int getValue() {
		return mValue;
	}

}
