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
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import ch.hsr.eyecam.R;


public class PreferencesRadioGroup extends RadioGroup implements OnCheckedChangeListener{
	
	String mTitle,mKey;
	boolean mEnableSeperator;
	TypedArray mTypedArray;
	private final static int DEFAULT_BUTTON = 0;
	private final static int NO_KEY =0;
	private static final String LOG_TAG = "ch.hsr.eyecam.widget.PreferencesRadioGroup";
	public PreferencesRadioGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		mTypedArray = context.obtainStyledAttributes(attrs,
				R.styleable.PreferencesRadioGroup);
		
		mTitle = getString(R.styleable.PreferencesRadioGroup_title
				, R.string.no_title_set);
		
		mEnableSeperator = mTypedArray.getBoolean(
				R.styleable.PreferencesRadioGroup_enableSeperator, true);
		
		mKey = getString(R.styleable.PreferencesRadioGroup_key
				, NO_KEY);
		
		int button = mTypedArray.getInt(R.styleable.PreferencesRadioGroup_defaultRadioButtonIndex
				, DEFAULT_BUTTON);
		if(button != DEFAULT_BUTTON)check(button);
		
		if(mEnableSeperator)addView(new Seperator(context,mTitle));
		
		setOnCheckedChangeListener(this);
	}
	
	private String getString(int ResId, int resDefaultValu){
		if(hasKey(ResId, resDefaultValu)) return mTitle;
		int stringRes = mTypedArray.getResourceId(ResId, resDefaultValu);
		return getResources().getString(stringRes);
	}

	private boolean hasKey(int ResId, int resDefaultValu) {
		return resDefaultValu == NO_KEY	&& mTypedArray.getString(ResId) == null;
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		Log.d(LOG_TAG, "Something have changed => "+mKey+" to: "+((PreferencesRadioButton)findViewById(checkedId)).getValue());
		Context appContex = getContext().getApplicationContext();
		SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(appContex);
		SharedPreferences.Editor editor = sPref.edit();
		editor.putInt(mKey, ((PreferencesRadioButton)findViewById(checkedId)).getValue());
		if(!editor.commit()) Log.d(LOG_TAG,"It couldn't commit!");
	}
	
	private class Seperator extends TextView{
		
		public Seperator(Context context) {
			super(context);
		}
		
		public Seperator (Context context, String Title){
			this(context);
			setText(Title);
			LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
			setLayoutParams(params);
			setGravity(Gravity.CENTER);
			setTextSize(TypedValue.COMPLEX_UNIT_PT,7);
			setTextColor(Color.WHITE);
			setTypeface(Typeface.DEFAULT, Typeface.BOLD);
			setBackgroundColor(Color.GRAY);
		}
		
	}

	
}
