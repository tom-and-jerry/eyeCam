package ch.hsr.eyecam;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;



public class EyeCamActivity extends Activity {
	//private PowerManager.WakeLock mWakeLock;

	private WakeLock mWakeLock;

	@Override
	protected void onPause() {
		super.onPause();
		mWakeLock.release(); 
	}

	@Override
	protected void onResume() {
		super.onResume();
		mWakeLock.acquire();
	}

	@Override
	public boolean onSearchRequested(){
		return false;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "eyeCam");
		
		
	}

}
