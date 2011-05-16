package ch.hsr.eyecam.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import ch.hsr.eyecam.R;


public class PreferencesRadioGroup extends RadioGroup implements OnCheckedChangeListener{
	String mTitle,mKey;
	TypedArray mTypedArray;
	private SharedPreferences mSharedPreferences;
	boolean mEnableSeperator;
	private int mDefaultValue;
	
	private final static int NO_KEY = 0;
	private static final String LOG_TAG = "ch.hsr.eyecam.widget.PreferencesRadioGroup";
	
	public PreferencesRadioGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		Context appContex = getContext().getApplicationContext();
		mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContex);
		
		mTypedArray = context.obtainStyledAttributes(attrs,
				R.styleable.PreferencesRadioGroup);
		mTitle = getString(R.styleable.PreferencesRadioGroup_title
				, R.string.setting_no_title);
		mEnableSeperator = mTypedArray.getBoolean(
				R.styleable.PreferencesRadioGroup_enableSeperator, true);
		mKey = getString(R.styleable.PreferencesRadioGroup_key
				, NO_KEY);
		mDefaultValue = mTypedArray.getInteger(
				R.styleable.PreferencesRadioGroup_defaultValue, 0);
		
		if(mEnableSeperator)addView(new Seperator(context,mTitle));
	}
	
	private String getString(int ResId, int resDefaultValue){
		if(hasKey(ResId, resDefaultValue)) return mTitle;
		
		int stringRes = mTypedArray.getResourceId(ResId, resDefaultValue);
		return getResources().getString(stringRes);
	}

	private boolean hasKey(int ResId, int resDefaultValu) {
		return resDefaultValu == NO_KEY	&& mTypedArray.getString(ResId) == null;
	}
	
	

	@Override
	protected void onFinishInflate() {
		initCheckedValue();
		super.onFinishInflate();
	}
	
	private void initCheckedValue() {
		setOnCheckedChangeListener(this);

		PreferencesRadioButton button;
		int value = mSharedPreferences.getInt(mKey, mDefaultValue);
		Log.d(LOG_TAG, "trying to set value: " + value);
		for (int i = 0; i < getChildCount(); i++){
			if (mEnableSeperator && i == 0) continue;
			button = (PreferencesRadioButton)getChildAt(i);
			Log.d(LOG_TAG, "button: " + button.getText() + " containing value: " + button.getValue());
			if (button.getValue() == value) {
				button.performClick();
				Log.d(LOG_TAG, "successfully set value on: " + button.getText());
				return;
			}
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		PreferencesRadioButton button = (PreferencesRadioButton)findViewById(checkedId);
		if (button == null) return;
		Log.d(LOG_TAG, "Preference changed, key: "+mKey+" to: "+button.getValue());
		
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.putInt(mKey, button.getValue());
		if(!editor.commit()) Log.d(LOG_TAG,"Preferences "+mKey+ " couldn't been committed!");
		
		ViewGroup vg = (ViewGroup) getParent();
		if (vg != null) vg.invalidate();
	}
	
	private class Seperator extends TextView{
		
		public Seperator(Context context) {
			super(context);
		}
		
		public Seperator (Context context, String Title){
			this(context);
			
			setText(Title);
			setTextSize(TypedValue.COMPLEX_UNIT_PT,7);
			setTextColor(Color.WHITE);

			setTypeface(Typeface.DEFAULT, Typeface.BOLD);
			setBackgroundColor(Color.GRAY);
			
			LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
			setLayoutParams(params);
			setGravity(Gravity.CENTER);
		}
		
	}

	
}
