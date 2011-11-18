package ch.hsr.eyecam;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.View.OnTouchListener;
import ch.hsr.eyecam.view.ColorView;
import ch.hsr.eyecam.view.ControlBar;

/**
 * This class represents the core of the eyeCam application. It is responsible for
 * the initialization of the Camera and the View and managing all aspects of the
 * life cycle of the application itself.
 * 
 * @author Dominik Spengler, Patrice Mueller
 * @see Activity
 */
public class EyeCamActivity extends Activity {
	private PowerManager.WakeLock mWakeLock;
	private OrientationEventListener mOrientationEventListener;
	private String mFilterKey;
	private String mTextSizeKey;
	private String mPartialKey;
	private String mPrimaryFilterKey;
	private String mSecondaryFilterKey;
	private int mPrimaryFilter;
	private int mSecondaryFilter;
	private Camera mCamera;
	private byte[] mCallBackBuffer;
	private boolean mCamIsPreviewing;
	private ColorView mColorView;
	private ControlBar mControlBar;
	private Orientation mOrientationCurrent =  Orientation.UNKNOW;
	private LoadingCameraInBackground mLoadingCameraInBg;
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
			case CAMERA_START_PREVIEW:
				startCameraPreview();
				break;
			case CAMERA_STOP_PREVIEW:
				stopCameraPreview();
				break;
			case CAMERA_LIGHT_OFF:
				setCameraLight(Camera.Parameters.FLASH_MODE_OFF);
				break;
			case CAMERA_LIGHT_ON:
				setCameraLight(Camera.Parameters.FLASH_MODE_TORCH);
				break;
			case PRIMARY_FILTER_ON:
				Debug.msg(LOG_TAG, "PrimaryFilter is running...."+mPrimaryFilter);
				mColorView.setEffect(mPrimaryFilter);
				break;
			case SECONDARY_FILTER_ON:
				Debug.msg(LOG_TAG, "Secondary Filter is running...."+mSecondaryFilter);
				mColorView.setEffect(mSecondaryFilter);
				break;
			}
		}
	};

	private OnTouchListener mOnTouchListener = new OnTouchListener() {	
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			stopCameraPreview();
			mControlBar.setCamStateButton(false);
			return false;
		}
	};
	
	private class LoadingCameraInBackground extends AsyncTask<Void, Void, Void>{
		
		@Override
		protected void onPreExecute() {
			mColorView.setVisibility(View.INVISIBLE);
			findViewById(R.id.hsr_loading_screen).setVisibility(View.VISIBLE);
		}

		private void waitForCamera(int milliSek){
			try {
				synchronized (this) {
					wait(milliSek);
				}
				return;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		
		@Override
		protected Void doInBackground(Void... params) {
			Debug.msg(LOG_TAG, "starting opening camera...");
			for(int tries= 0;tries <10;++tries){
				try{
					mCamera.reconnect();
				}
				catch (NullPointerException nullPointer){		
					try{
						mCamera = Camera.open();
						Debug.msg(LOG_TAG, tries+" tries until now");
						if(mCamera!=null) tries=10;
					} catch (RuntimeException ex){
						waitForCamera(500);
						continue;
					}
				} catch (IOException ioEx) {
					waitForCamera(500);
					continue;
				}
			}
			return null;
		}
		
		@Override
		protected void onCancelled() {
			Debug.msg(LOG_TAG, "canceling opening camera...");
			while(mCamera == null);
			releaseCamera(true);
		}
		
		@Override
		protected void onPostExecute(Void result) {
			Debug.msg(LOG_TAG, "finish opening camera...");
			if(isCancelled())return;
			configEnvByCameraParams();
			mColorView.setVisibility(View.VISIBLE);
			findViewById(R.id.hsr_loading_screen).setVisibility(View.INVISIBLE);
		}
	}
	
	private OnSharedPreferenceChangeListener mPrefFilter;
	private final DisplayMetrics mMetrics = new DisplayMetrics();
	
	public final static int CAMERA_START_PREVIEW = 0;
	public final static int CAMERA_STOP_PREVIEW = 1;
	public final static int CAMERA_LIGHT_OFF = 2;
	public final static int CAMERA_LIGHT_ON = 3;
	public final static int PRIMARY_FILTER_ON = 4;
	public final static int SECONDARY_FILTER_ON = 5;
	
	private final static String LOG_TAG = "ch.hsr.eyecam.EyeCamActivity";
	
	private void setCameraLight(String cameraFlashMode) {
		Parameters parameters = mCamera.getParameters();
		parameters.setFlashMode(cameraFlashMode);
		mCamera.setParameters(parameters);
	}

	/** 
	 * {@inheritDoc}
	 * 
	 * Called when the activity is first created.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		findViewById(R.id.placeHolder).setOnTouchListener(mOnTouchListener);
		mColorView = (ColorView) findViewById(R.id.cameraSurface);
		mControlBar = (ControlBar) findViewById(R.id.controlBar);
		mControlBar.setActivityHandler(mHandler);
		mControlBar.enableOnClickListeners();
		mControlBar.rotate(Orientation.UNKNOW);
		getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
		
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "eyeCam");
		
		initOrientationEventListener();
		registerPreferenceChangeListener();
		initSavedPreferences();
		Debug.msg(LOG_TAG, "PrimaryFilter: "+ mPrimaryFilter +" SecondaryFilter: "+mSecondaryFilter );
		mOrientationEventListener.enable();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.settings_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.settings_menu){
			startActivity(new Intent(this, Preferences.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	private void initOrientationEventListener() {
		mOrientationEventListener = new OrientationEventListener(this, 
				SensorManager.SENSOR_DELAY_NORMAL) {
			
			@Override
			public void onOrientationChanged(int inputOrientation) {
				Orientation orientation = getCurrentOrientation(inputOrientation);
				if(orientation != mOrientationCurrent){
					mOrientationCurrent = orientation;
					mControlBar.rotate(mOrientationCurrent);
					mColorView.setOrientation(mOrientationCurrent);
					Debug.msg(LOG_TAG, "Orientation: "+mOrientationCurrent);
				}			
			}
			
			private Orientation getCurrentOrientation(int orientationInput){
				int orientation = orientationInput;
				orientation = orientation % 360;
				int boundary_portrait = 45;
				int boundary_landscapeRight = 135;
				int boundary_reversePortrait = 225;
				int boundary_landsacpeLeft= 315;
				
				if (orientation < boundary_portrait) return Orientation.PORTRAIT;
				if (orientation < boundary_landscapeRight) return Orientation.LANDSCAPE_RIGHT;
				if (orientation < boundary_reversePortrait) return Orientation.PORTRAIT;
				if (orientation < boundary_landsacpeLeft) return Orientation.LANDSCAPE_LEFT;
				
				return Orientation.PORTRAIT;
			}
		};
	}

	private void registerPreferenceChangeListener(){
		mFilterKey = getResources().getString(R.string.filter_key);
		
		mPrimaryFilterKey = getResources().getString(R.string.setting_primary_filter_key);
		mSecondaryFilterKey = getResources().getString(R.string.setting_secondary_filter_key);
		
		mTextSizeKey = getResources().getString(R.string.setting_textsize_key);
		mPartialKey = getResources().getString(R.string.setting_key_partial);
		
		mPrefFilter = new OnSharedPreferenceChangeListener(){
			private String mDefFilter = getResources().getString(R.string.filter_daltonize_value);
			private String mDefTextSize = getResources().getString(R.string.text_size_medium);
			
			@Override
			public void onSharedPreferenceChanged(SharedPreferences shPref, String key) {
				Debug.msg(LOG_TAG, "Preferences changed for key: " + key);
				if(key.equals(mPrimaryFilterKey)){
					mPrimaryFilter = getIntPrefernce(shPref,key,mDefFilter);
					mColorView.setEffect(mPrimaryFilter);
					if (!mCamIsPreviewing) mColorView.refreshBitmap();
				}else if(key.equals(mSecondaryFilterKey)){
					mSecondaryFilter = getIntPrefernce(shPref,key,mDefFilter);
					mColorView.setEffect(mSecondaryFilter);
					if (!mCamIsPreviewing) mColorView.refreshBitmap();
				} else if(key.equals(mTextSizeKey)){
					mColorView.setPopupTextSize(getIntPrefernce(shPref,key, mDefTextSize));
				} else if(key.equals(mPartialKey)){
					mColorView.enablePartialEffects(shPref.getBoolean(key, false));
					mColorView.setEffect(getIntPrefernce(shPref,mFilterKey,mDefFilter));
					if (!mCamIsPreviewing) mColorView.refreshBitmap();
				}
			}
			
			private int getIntPrefernce(SharedPreferences shPref, String key, String defaultValue ){
				return Integer.parseInt(shPref.getString(key, mDefFilter));
			}
			
		};
		
		SharedPreferences shPref = PreferenceManager.getDefaultSharedPreferences(this);
		shPref.registerOnSharedPreferenceChangeListener(mPrefFilter);
	}
	
	private void initSavedPreferences() {
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		SharedPreferences shPref = PreferenceManager.getDefaultSharedPreferences(this);
		mPrefFilter.onSharedPreferenceChanged(shPref, mFilterKey);
		mPrefFilter.onSharedPreferenceChanged(shPref, mPartialKey);
		mPrefFilter.onSharedPreferenceChanged(shPref, mTextSizeKey);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Called after onCreate() and onStart().l
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mLoadingCameraInBg = new LoadingCameraInBackground();
		mLoadingCameraInBg.execute();
		mControlBar.setInitState();
		mWakeLock.acquire();
		mOrientationEventListener.enable();
	}

	private void configEnvByCameraParams() {
		Debug.msg(LOG_TAG, "start init camera Pref...");
		Camera.Parameters parameters = mCamera.getParameters();	
		
		Size optSize = getOptimalSize(parameters.getSupportedPreviewSizes());
		for (Size s : parameters.getSupportedPreviewSizes()){
			Debug.msg(LOG_TAG, "Supported - H:" + s.height + "W:" + s.width);
		}
		parameters.setPreviewSize(optSize.width, optSize.height);
		Debug.msg(LOG_TAG, "Chosen - H:" +optSize.height + "W:" +optSize.width);
		Debug.msg(LOG_TAG, "Screen - H:" +mMetrics.heightPixels + "W:" 
				+mMetrics.widthPixels);
		
		disableFlashIfUnsupported(parameters);
		
		mCallBackBuffer = new byte[optSize.width*optSize.height*2];
		mColorView.setDataBuffer(mCallBackBuffer, optSize.width, optSize.height);
		mCamera.setParameters(parameters);
		startCameraPreview();
		mControlBar.setCamStateButton(true);
	}

	private Size getOptimalSize(List<Size> sizeList){
		if(sizeList == null) return null;
		
		double targetRatio = (double) mMetrics.widthPixels / mMetrics.heightPixels;
		int targetHeight = mMetrics.heightPixels;
		double diffRatio = Double.MAX_VALUE;
		Size optSize = null;
		
		for(Size size : sizeList){
			double tmpDiffRatio = (double) size.width / size.height;
			if(Math.abs(targetRatio-tmpDiffRatio)< diffRatio){
				optSize = size;
				diffRatio = Math.abs(targetRatio-tmpDiffRatio) +
							Math.abs(size.height-targetHeight);
				if (diffRatio == 0) return optSize;
			}
		}
		return optSize;
	}

	private void disableFlashIfUnsupported(Camera.Parameters parameters) {
		if(parameters.getSupportedFlashModes() == null){
			mControlBar.enableLightButton(false);
		}
		else if(parameters.getSupportedFlashModes().contains(Camera.Parameters.FLASH_MODE_TORCH))
			mControlBar.enableLightButton(true);
	}

	private void startCameraPreview() {
		mCamera.addCallbackBuffer(mCallBackBuffer);
		mCamera.setPreviewCallbackWithBuffer((PreviewCallback) mColorView);
		mCamera.startPreview();
		mCamIsPreviewing = true;
		
		mColorView.enablePopup(false);
	}

	/** 
	 * {@inheritDoc}
	 * 
	 * Called whenever the Activity will be sent to the background.
	 */
	@Override
	protected void onPause() {
		super.onPause();
		releaseCamera();
		mWakeLock.release();
		mOrientationEventListener.disable();
	}

	private void releaseCamera(){
		if(mCamera==null) return;
		stopCameraPreview();
		mCamera.release();
		mCamera = null;
	}
	
	private void releaseCamera(boolean force){
		if(force && mCamera != null) mCamera.unlock();
		else releaseCamera();
	}

	private void stopCameraPreview() {
		mCamera.setPreviewCallbackWithBuffer(null);
		mCamera.stopPreview();
		mCamIsPreviewing = false;
		mColorView.enablePopup(true);
	}

	/** 
	 * {@inheritDoc}
	 * 
	 * Called whenever the activity will be shut down.
	 */
	@Override
	protected void onDestroy() {
		mOrientationEventListener.disable();
		mColorView.dismissPopup();
		super.onDestroy();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * By returning false, the activity blocks search requests.
	 */
	@Override
	public boolean onSearchRequested(){
		return false;
	}
	
	@Override
	public void onBackPressed() {
		if(mLoadingCameraInBg.getStatus() == Status.RUNNING){
			mLoadingCameraInBg.cancel(true);
			while(!mLoadingCameraInBg.isCancelled());
			Debug.msg(LOG_TAG, "Finish!!!");
		}
		super.onBackPressed();
	}
	
}
