package ch.hsr.eyecam;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
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
	private String mMenuSizeKey;
	private String mTextSizeKey;
	private String mPartialKey;
	private Camera mCamera;
	private byte[] mCallBackBuffer;
	private boolean mCamIsPreviewing;
	
	private ColorView mColorView;
	private ControlBar mControlBar;
	private Orientation mOrientationCurrent =  Orientation.UNKNOW;
	
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
			}
		}
	};

	private OnTouchListener mOnTouchListener = new OnTouchListener() {	
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (mControlBar.isMenuShowing()) mControlBar.dismissAllChildMenu();
			else {
				stopCameraPreview();
				mControlBar.setCamStateButton(false);
			}
			return false;
		}
	};
	
	private OnSharedPreferenceChangeListener mPrefFilter;
	private final DisplayMetrics mMetrics = new DisplayMetrics();
	
	public final static int CAMERA_START_PREVIEW = 0;
	public final static int CAMERA_STOP_PREVIEW = 1;
	public final static int CAMERA_LIGHT_OFF = 2;
	public final static int CAMERA_LIGHT_ON = 3;
	
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
		mOrientationEventListener.enable();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.settings_menu, menu);
		return true;
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
		mMenuSizeKey = getResources().getString(R.string.menu_size_key);
		mTextSizeKey = getResources().getString(R.string.text_size_key);
		mPartialKey = getResources().getString(R.string.partial_key);
		
		mPrefFilter = new OnSharedPreferenceChangeListener(){
			private int mDefFilter = getResources().getInteger(R.integer.filter_none);
			private int mDefTextSize = getResources().getInteger(R.integer.text_size_medium);
			private int mDefMenuSize = getResources().getInteger(R.integer.menu_size_medium);
			private int mPartialOff = getResources().getInteger(R.integer.partial_off);
			private int mPartialOn = getResources().getInteger(R.integer.partial_on);
			
			@Override
			public void onSharedPreferenceChanged(SharedPreferences shPref, String key) {
				Debug.msg(LOG_TAG, "Preferences changed for key: " + key);
				
				if(key.equals(mFilterKey)){
					mColorView.setEffect(shPref.getInt(key, mDefFilter));
					if (!mCamIsPreviewing) mColorView.refreshBitmap();
				} else if(key.equals(mTextSizeKey)){
					mColorView.setPopupTextSize(shPref.getInt(key, mDefTextSize));
				} else if(key.equals(mMenuSizeKey)){
					mControlBar.setMenuSize(shPref.getInt(key, mDefMenuSize));
				} else if(key.equals(mPartialKey)){
					if (shPref.getInt(key, mPartialOff) == mPartialOn)
						mColorView.enablePartialEffects(true);
					else mColorView.enablePartialEffects(false);
					mColorView.setEffect(shPref.getInt(mFilterKey, mDefFilter));
					if (!mCamIsPreviewing) mColorView.refreshBitmap();
				}
			}
			
		};
		
		SharedPreferences shPref = PreferenceManager.getDefaultSharedPreferences(this);
		shPref.registerOnSharedPreferenceChangeListener(mPrefFilter);
	}
	
	private void initSavedPreferences() {
		SharedPreferences shPref = PreferenceManager.getDefaultSharedPreferences(this);
		mPrefFilter.onSharedPreferenceChanged(shPref, mFilterKey);
		mPrefFilter.onSharedPreferenceChanged(shPref, mMenuSizeKey);
		mPrefFilter.onSharedPreferenceChanged(shPref, mPartialKey);
		mPrefFilter.onSharedPreferenceChanged(shPref, mTextSizeKey);
	}

	/** 
	 * {@inheritDoc}
	 * 
	 * Called after onCreate() and onStart().
	 */
	@Override
	protected void onResume() {
		super.onResume();
		initCamera();
		mWakeLock.acquire();
		mOrientationEventListener.enable();
	}

	private void initCamera() {
		mCamera= Camera.open();
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
		mControlBar.dismissAllChildMenu();
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
	
	/**
	 * {@inheritDoc}
	 * 
	 * This method is overwritten in order to dismiss the menu if
	 * it is showing.
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (mControlBar.isMenuShowing()){
			mControlBar.dismissAllChildMenu();
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}
}
