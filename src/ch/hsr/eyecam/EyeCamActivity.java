package ch.hsr.eyecam;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;



public class EyeCamActivity extends Activity {
	private Camera mCamera;
	private SurfaceHolder mHolder;
	private SurfaceView mSurfaceView;
	private final DisplayMetrics mMetrics = new DisplayMetrics();
	private final static String LOG_TAG = "ch.hsr.EyeCamActivity";
	
	private PowerManager.WakeLock mWakeLock;
	
	private SurfaceHolder.Callback mCallback = new SurfaceHolder.Callback() {
		
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			//TODO: examine effects of surface change
			Log.d(LOG_TAG, "surfaceChanged from SurfaceHolder.Callback was called");
		}
		
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			initCamera();
		}
		
		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			releaseCamera();
		}

	};
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		mSurfaceView = (SurfaceView) findViewById(R.id.cameraSurface);
		initHolder(mSurfaceView.getHolder());
		getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
		
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "eyeCam");
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		mWakeLock.acquire();
	}

	@Override
	protected void onPause() {
		super.onPause();
		
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
	
	private Size getOptimalSize(List<Size> sizeList){
		if(isNull(sizeList)) return null;
		
		Size optSize = findBestPreviewSize(sizeList);
		return optSize;
	}
	
	private Size findBestPreviewSize(List<Size> sizeList) {
		double targetRatio = (double) mMetrics.widthPixels / mMetrics.heightPixels;
		double diffRatio = Double.MAX_VALUE;
		Size optSize = null;
		
		for(Size size : sizeList){
			double tmpDiffRatio = (double) size.width / size.height;
			if(Math.abs(targetRatio-tmpDiffRatio)< diffRatio){
				optSize = size;
				diffRatio = Math.abs(targetRatio-tmpDiffRatio);
				if (diffRatio == 0) return optSize;
			}
		}
		return optSize;
	}

	private void initHolder(SurfaceHolder holder) {
		mHolder = holder;
		mHolder.addCallback(mCallback);
		//mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}
	
	private void initCamera() {
		//TODO: do not set preview size larger than screen size
		mCamera= Camera.open();
		Camera.Parameters parameters = mCamera.getParameters();	
		
		Size optSize = getOptimalSize(parameters.getSupportedPreviewSizes());
		for (Size s : parameters.getSupportedPreviewSizes()){
			Log.i(LOG_TAG, "Supported - H:" + s.height + "W:" + s.width);
		}
		parameters.setPreviewSize(optSize.width, optSize.height);
		Log.i(LOG_TAG, "Chosen - H:" +optSize.height + "W:" +optSize.width);
		Log.i(LOG_TAG, "Screen - H:" +mMetrics.heightPixels + "W:" +mMetrics.widthPixels);
		
		mCamera.setParameters(parameters);
		mCamera.setPreviewCallback((PreviewCallback) mSurfaceView);
		mCamera.startPreview();
	}
	
	private void releaseCamera(){
		if(isNull(mCamera)) return;
		
		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
	}
}
