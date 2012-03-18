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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import ch.hsr.eyecam.R.id;
import ch.hsr.eyecam.view.ColorView;
import ch.hsr.eyecam.view.ControlBar;
import ch.hsr.eyecam.widget.MenuBubble;

/**
 * This class represents the core of the eyeCam application. It is responsible
 * for the initialization of the Camera and the View and managing all aspects of
 * the life cycle of the application itself.
 * 
 * @author Dominik Spengler, Patrice Mueller
 * @see Activity
 */
public class EyeCamActivity extends Activity implements SurfaceHolder.Callback {
	private PowerManager.WakeLock mWakeLock;
	private OrientationEventListener mOrientationEventListener;
	private int mPrimaryFilter;
	private int mSecondaryFilter;
	private Camera mCamera;
	private byte[] mCallBackBuffer;
	private boolean mCamIsPreviewing;
	private ColorView mColorView;
	private ControlBar mControlBar;
	private Orientation mOrientationCurrent = Orientation.UNKNOW;
	private ShowLoadingScreenTask mShowLoadingScreenTask;
	private View mLoadingScreen;
	private SurfaceView mSurfaceView;
	private MenuBubble mAppMenu;
	private MenuBubble mPrimaryFilterMenu;
	private MenuBubble mSecondaryFilterMenu;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (menuIsShowing()){
				dismissMenus();
				return;
			}
			
			switch (msg.what) {
			case CAMERA_START_PREVIEW:
				startCameraPreview();
				break;
			case CAMERA_STOP_PREVIEW:
				stopCameraPreview();
				setCameraLight(Camera.Parameters.FLASH_MODE_OFF);
				break;
			case CAMERA_LIGHT_OFF:
				setCameraLight(Camera.Parameters.FLASH_MODE_OFF);
				break;
			case CAMERA_LIGHT_ON:
				setCameraLight(Camera.Parameters.FLASH_MODE_TORCH);
				break;
			case PRIMARY_FILTER_ON:
				Debug.msg(LOG_TAG, "PrimaryFilter is running...."
						+ mPrimaryFilter);
				setPrimaryFilter();
				break;
			case SECONDARY_FILTER_ON:
				Debug.msg(LOG_TAG, "Secondary Filter is running...."
						+ mSecondaryFilter);
				setSecondaryFilter();
				break;
			case SHOW_PRIMARY_FILTER_MENU:
				stopCameraPreview();
				inflateMenu(mPrimaryFilterMenu);
				break;
			case SHOW_SECONDARY_FILTER_MENU:
				stopCameraPreview();
				inflateMenu(mSecondaryFilterMenu);
				break;
			case SHOW_SETTINGS_MENU:
				inflateMenu(mAppMenu);
				break;
			}
		}
	};
	private boolean mPrimaryPartial;
	private boolean mSecondaryPartial;

	private void setCameraLight(String cameraFlashMode) {
		Parameters parameters = mCamera.getParameters();
		parameters.setFlashMode(cameraFlashMode);
		mCamera.setParameters(parameters);

		if (cameraFlashMode.equals(Camera.Parameters.FLASH_MODE_TORCH))
			mControlBar.setButtonLight(true);
		else
			mControlBar.setButtonLight(false);
	}

	private void setSecondaryFilter() {
		setEffects(mSecondaryFilter, mSecondaryPartial);
		mControlBar.setButtonFilter(false);
	}

	private void setPrimaryFilter() {
		setEffects(mPrimaryFilter, mPrimaryPartial);
		mControlBar.setButtonFilter(true);
	}

	private void inflateMenu(MenuBubble menu) {
		dismissMenus();
		menu.show();
	}

	private void dismissMenus() {
		mPrimaryFilterMenu.dismiss();
		mSecondaryFilterMenu.dismiss();
		mAppMenu.dismiss();
	}

	private void setEffects(int effect, boolean partial) {
		mColorView.enablePartialEffects(partial);
		mColorView.setEffect(effect);
		if (!mCamIsPreviewing)
			mColorView.refreshBitmap();
	}

	private OnSharedPreferenceChangeListener mSharedPrefChangeListener = new OnSharedPreferenceChangeListener() {
		@Override
		public void onSharedPreferenceChanged(SharedPreferences shPref,
				String key) {
			int value = 0;
			if (key.equals(getResources()
					.getString(R.string.key_primary_filter))) {
				value = shPref.getInt(key, 0);
				mPrimaryFilter = value;
				setEffects(value, mPrimaryPartial);
				dismissMenus();
				startCameraPreview();
			} else if (key.equals(getResources().getString(
					R.string.key_secondary_filter))) {
				value = shPref.getInt(key, 0);
				mSecondaryFilter = value;
				setEffects(value, mSecondaryPartial);
				dismissMenus();
				startCameraPreview();
			} else if (key.equals(getResources().getString(
					R.string.key_primary_partial))) {
				mPrimaryPartial = shPref.getBoolean(key, false);
				setEffects(mPrimaryFilter, mPrimaryPartial);
			} else if (key.equals(getResources().getString(
					R.string.key_secondary_partial))) {
				mSecondaryPartial = shPref.getBoolean(key, false);
				setEffects(mSecondaryFilter, mSecondaryPartial);
			} else if (key.equals(getResources().getString(
					R.string.key_text_size))) {
				mColorView.setPopupTextSize(shPref.getInt(key, 5));
				dismissMenus();
			} else if (key.equals(getResources().getString(
					R.string.key_color_rgb))) {
				mColorView.setShowRGB(shPref.getBoolean(key, false));
			} else if (key.equals(getResources().getString(
					R.string.key_color_hsv))) {
				mColorView.setShowHSV(shPref.getBoolean(key, false));
			}
		}
	};

	private class ShowLoadingScreenTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			mColorView.setVisibility(View.INVISIBLE);
			mLoadingScreen.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			Debug.msg(LOG_TAG, "showing Loading Screen ...");
			synchronized (this) {
				try {
					wait(800);
				} catch (InterruptedException e) {
					// do nothing. useless hack in order to show loading screen
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Debug.msg(LOG_TAG, "finish opening camera...");
			if (isCancelled())
				return;
			mColorView.setVisibility(View.VISIBLE);
			mLoadingScreen.setVisibility(View.INVISIBLE);
		}

	}

	private final static DisplayMetrics mMetrics = new DisplayMetrics();

	public final static int CAMERA_START_PREVIEW = 0;
	public final static int CAMERA_STOP_PREVIEW = 1;
	public final static int CAMERA_LIGHT_OFF = 2;
	public final static int CAMERA_LIGHT_ON = 3;
	public final static int PRIMARY_FILTER_ON = 4;
	public final static int SECONDARY_FILTER_ON = 5;
	public final static int SHOW_PRIMARY_FILTER_MENU = 6;
	public final static int SHOW_SECONDARY_FILTER_MENU = 7;
	public final static int SHOW_SETTINGS_MENU = 8;
	public final static String PREFERENCE_FILE = "eyeCamPref";
	private final static String LOG_TAG = "ch.hsr.eyecam.EyeCamActivity";
	private SharedPreferences mSharedPreferences;

	/**
	 * {@inheritDoc}
	 * 
	 * Called when the activity is first created.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mLoadingScreen = (View) findViewById(R.id.hsr_loading_screen);
		mColorView = (ColorView) findViewById(R.id.cameraSurface);
		mColorView.setActivityHandler(mHandler);
		mControlBar = (ControlBar) findViewById(R.id.controlBar);
		mControlBar.setActivityHandler(mHandler);
		mControlBar.enableOnClickListeners();
		mControlBar.rotate(Orientation.UNKNOW);

		mSurfaceView = (SurfaceView) findViewById(id.cameraSurface_dummy);
		mSurfaceView.getHolder().addCallback(this);

		getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
		createMenus();

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
				"eyeCam");

		initOrientationEventListener();
		mOrientationEventListener.enable();
	}

	private void createMenus() {
		LayoutInflater inflater = (LayoutInflater) getBaseContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View contentView = inflater.inflate(R.layout.settings_menu, null);
		mAppMenu = new MenuBubble(mColorView, contentView);

		View primFilterView = inflater.inflate(R.layout.primary_filter_menu,
				null);
		mPrimaryFilterMenu = new MenuBubble(mColorView, primFilterView);

		View secFilterView = inflater.inflate(R.layout.secondary_filter_menu,
				null);
		mSecondaryFilterMenu = new MenuBubble(mColorView, secFilterView);

		setMenuSize(mAppMenu, contentView);
		setMenuSize(mPrimaryFilterMenu, primFilterView);
		setMenuSize(mSecondaryFilterMenu, secFilterView);
	}

	private void setMenuSize(MenuBubble menu, View contentView) {
		menu.setWidth((mMetrics.heightPixels / 10) * 9);
		menu.setHeight((mMetrics.widthPixels / 10) * 8);
	}

	private void initOrientationEventListener() {
		mOrientationEventListener = new OrientationEventListener(this,
				SensorManager.SENSOR_DELAY_NORMAL) {

			@Override
			public void onOrientationChanged(int inputOrientation) {
				Orientation orientation = getCurrentOrientation(inputOrientation);
				if (orientation != mOrientationCurrent) {
					mOrientationCurrent = orientation;
					mControlBar.rotate(mOrientationCurrent);
					mColorView.setOrientation(mOrientationCurrent);
					rotateMenus(mOrientationCurrent);
					Debug.msg(LOG_TAG, "Orientation: " + mOrientationCurrent);
				}
			}

			private Orientation getCurrentOrientation(int orientationInput) {
				int orientation = orientationInput;
				orientation = orientation % 360;
				int boundary_portrait = 45;
				int boundary_landscapeRight = 135;
				int boundary_reversePortrait = 225;
				int boundary_landsacpeLeft = 315;

				if (orientation < boundary_portrait)
					return Orientation.PORTRAIT;
				if (orientation < boundary_landscapeRight)
					return Orientation.LANDSCAPE_RIGHT;
				if (orientation < boundary_reversePortrait)
					return Orientation.PORTRAIT;
				if (orientation < boundary_landsacpeLeft)
					return Orientation.LANDSCAPE_LEFT;

				return Orientation.PORTRAIT;
			}
		};
	}

	private void rotateMenus(Orientation orientation) {
		mAppMenu.setContentOrientation(orientation);
		mPrimaryFilterMenu.setContentOrientation(orientation);
		mSecondaryFilterMenu.setContentOrientation(orientation);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Called after onCreate() and onStart()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		openCamera();
		mShowLoadingScreenTask = new ShowLoadingScreenTask();
		mShowLoadingScreenTask.execute();
		configEnvByCameraParams();
		initSavedPreferences();
		mControlBar.initState();
		mWakeLock.acquire();
		mOrientationEventListener.enable();
	}

	private void initSavedPreferences() {
		mSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		mSharedPreferences
				.registerOnSharedPreferenceChangeListener(mSharedPrefChangeListener);
		setValues(mSharedPreferences);
	}

	private void setValues(SharedPreferences shPref) {
		mPrimaryFilter = getIntSettingValue(shPref,
				R.string.key_primary_filter, R.string.filter_daltonize);
		mSecondaryFilter = getIntSettingValue(shPref,
				R.string.key_secondary_filter, R.string.filter_false_colors);
		mPrimaryPartial = getBooleanSettingValue(shPref,
				R.string.key_primary_partial, false);
		mSecondaryPartial = getBooleanSettingValue(shPref,
				R.string.key_secondary_partial, false);

		mColorView.setPopupTextSize(getIntSettingValue(shPref,
				R.string.key_text_size, 5));
		mColorView.setShowRGB(getBooleanSettingValue(shPref,
				R.string.key_color_rgb, false));
		mColorView.setShowHSV(getBooleanSettingValue(shPref,
				R.string.key_color_hsv, false));
	}

	private int getIntSettingValue(SharedPreferences shPref, int keyId,
			int defaultValue) {
		String keyString = getResources().getString(keyId);
		return shPref.getInt(keyString, defaultValue);
	}

	private boolean getBooleanSettingValue(SharedPreferences shPref,
			int resourcesOfTheKey, boolean defaultValue) {
		String keyString = getResources().getString(resourcesOfTheKey);
		return shPref.getBoolean(keyString, defaultValue);

	}

	private void configEnvByCameraParams() {
		Debug.msg(LOG_TAG, "start init camera Pref...");
		Camera.Parameters parameters = mCamera.getParameters();

		Size optSize = getOptimalSize(parameters.getSupportedPreviewSizes());
		for (Size s : parameters.getSupportedPreviewSizes()) {
			Debug.msg(LOG_TAG, "Supported - H:" + s.height + "W:" + s.width);
		}
		parameters.setPreviewSize(optSize.width, optSize.height);
		Debug.msg(LOG_TAG, "Chosen - H:" + optSize.height + "W:"
				+ optSize.width);
		Debug.msg(LOG_TAG, "Screen - H:" + mMetrics.heightPixels + "W:"
				+ mMetrics.widthPixels);

		disableFlashIfUnsupported(parameters);

		mCallBackBuffer = new byte[optSize.width * optSize.height * 2];
		mColorView
				.setDataBuffer(mCallBackBuffer, optSize.width, optSize.height);
		mCamera.setParameters(parameters);
		mColorView.scaleBitmapToFillScreen(mMetrics.widthPixels,
				mMetrics.heightPixels);
	}

	private Size getOptimalSize(List<Size> sizeList) {
		if (sizeList == null)
			return null;

		int targetWidth = mMetrics.widthPixels;
		int targetHeight = mMetrics.heightPixels;
		double diffSize = Double.MAX_VALUE;

		// do not get higher then 1000 pixel width since
		// even Galaxy Nexus has problem handling it.
		int upperWidthBound = 1000;
		int lowerWidthBound = targetWidth / 2;

		Size optSize = null;

		for (Size size : sizeList) {
			if (size.width > upperWidthBound || size.width < lowerWidthBound)
				continue;

			double tmpRatio = (double) size.height / targetHeight;
			double tmpDiff = tmpRatio * targetWidth - size.width;
			tmpDiff = Math.abs(tmpDiff);

			if (tmpDiff < diffSize) {
				optSize = size;
				diffSize = tmpDiff;
				if (diffSize == 0)
					return optSize;
			}
		}
		return optSize;
	}

	private void disableFlashIfUnsupported(Camera.Parameters parameters) {
		if (parameters.getSupportedFlashModes() == null) {
			mControlBar.enableLightButton(false);
		} else if (parameters.getSupportedFlashModes().contains(
				Camera.Parameters.FLASH_MODE_TORCH))
			mControlBar.enableLightButton(true);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Called whenever the Activity will be sent to the background.
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mWakeLock.release();
		mOrientationEventListener.disable();
	}

	private void openCamera() {
		mCamera = Camera.open();
	}

	private void releaseCamera() {
		if (mCamera == null)
			return;
		stopCameraPreview();
		mCamera.release();
		mCamera = null;
	}

	private void stopCameraPreview() {
		mCamera.setPreviewCallbackWithBuffer(null);
		mCamera.stopPreview();
		mCamIsPreviewing = false;

		mControlBar.setButtonPlay(mCamIsPreviewing);
	}

	private void startCameraPreview() {
		if (mCamIsPreviewing)
			return;

		mCamera.addCallbackBuffer(mCallBackBuffer);
		mCamera.setPreviewCallbackWithBuffer((PreviewCallback) mColorView);
		mCamera.startPreview();
		mCamIsPreviewing = true;

		mControlBar.setButtonPlay(mCamIsPreviewing);
		mColorView.dismissPopup();
	}

	/**
	 * Starting from ICS (and probably generally on Motorola devices camera
	 * preview will not be started if there is no SurfaceHolder attached due to
	 * security. In order to overcome this we have to attach a dummy
	 * SurfaceHolder and make sure it does not get displayed.
	 */
	private void makeSureCameraPreviewStarts() {
		try {
			mCamera.setPreviewDisplay(mSurfaceView.getHolder());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
	public boolean onSearchRequested() {
		return false;
	}

	@Override
	public void onBackPressed() {
		if (menuIsShowing())
			dismissMenus();
		else
			super.onBackPressed();
	}

	private boolean menuIsShowing() {
		return mAppMenu.isShowing() || mPrimaryFilterMenu.isShowing()
				|| mSecondaryFilterMenu.isShowing();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		startCameraPreview();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		makeSureCameraPreviewStarts();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		releaseCamera();
	}
	
	public void openMarket(View v){
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("market://details?id="+getApplication().getPackageName()));
		startActivity(intent);
	}

}
