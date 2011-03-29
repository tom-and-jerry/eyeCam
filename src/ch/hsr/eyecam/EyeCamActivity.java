package ch.hsr.eyecam;

import java.util.List;

import ch.hsr.eyecam.view.ColorView;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.util.Log;


public class EyeCamActivity extends Activity {
	private Camera mCamera;
	private ColorView mColorView;
	private final DisplayMetrics mMetrics = new DisplayMetrics();
	private final static String LOG_TAG = "ch.hsr.eyecam.EyeCamActivity";
	public static final int CAMERA_START_PREVIEW = 0;
	public static final int CAMERA_STOP_PREVIEW = 1;
	
	private PowerManager.WakeLock mWakeLock;
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
			case CAMERA_START_PREVIEW:
				mCamera.startPreview();
				break;
			case CAMERA_STOP_PREVIEW:
				mCamera.stopPreview();
			}
		}
	};
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		mColorView = (ColorView) findViewById(R.id.cameraSurface);
		mColorView.setHandler(mHandler);
		getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
		
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "eyeCam");
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		initCamera();
		mWakeLock.acquire();
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		releaseCamera();
		mWakeLock.release();
	}

	@Override
	public boolean onSearchRequested(){
		return false;
	}

	private boolean isNotNull(Object anyObject) {
		return anyObject != null;
	}
	
	private boolean isNull(Object anyObject){
		return !isNotNull(anyObject);
	}
	
	private void initCamera() {
		mCamera= Camera.open();
		Camera.Parameters parameters = mCamera.getParameters();	
		
		Size optSize = getOptimalSize(parameters.getSupportedPreviewSizes());
		for (Size s : parameters.getSupportedPreviewSizes()){
			Log.d(LOG_TAG, "Supported - H:" + s.height + "W:" + s.width);
		}
		parameters.setPreviewSize(optSize.width, optSize.height);
		Log.d(LOG_TAG, "Chosen - H:" +optSize.height + "W:" +optSize.width);
		Log.d(LOG_TAG, "Screen - H:" +mMetrics.heightPixels + "W:" +mMetrics.widthPixels);
		
		mCamera.addCallbackBuffer(new byte[optSize.width*optSize.height*2]);
		
		mCamera.setParameters(parameters);
		mCamera.setPreviewCallbackWithBuffer((PreviewCallback) mColorView);
	}

	private Size getOptimalSize(List<Size> sizeList){
		if(isNull(sizeList)) return null;
		
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
	
	private void releaseCamera(){
		if(isNull(mCamera)) return;
		
		mCamera.setPreviewCallbackWithBuffer(null);
		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
	}
}
