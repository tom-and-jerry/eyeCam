package ch.hsr.eyecam;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Preferences extends PreferenceActivity {
		
	protected static final String LOG_TAG = "ch.hsr.eyecam.PreferenceActivity";
	private OnSharedPreferenceChangeListener mSharedPrefChangeListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		mSharedPrefChangeListener = new OnSharedPreferenceChangeListener(){		
			@Override
			public void onSharedPreferenceChanged(SharedPreferences shPref, String key) {
				String defaultFilter = getResources().getString(R.string.filter_daltonize);
				SavePreferences(key, shPref.getString(key,defaultFilter));
			}			
		};
		SharedPreferences shPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		shPref.registerOnSharedPreferenceChangeListener(mSharedPrefChangeListener);
	}
	
	private void SavePreferences(String key, String value) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
		Debug.msg(LOG_TAG, "Save: key: "+key+" value: "+value);
	}
	
	
}

