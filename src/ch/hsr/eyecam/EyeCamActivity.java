package ch.hsr.eyecam;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
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
	private PowerManager.WakeLock mWakeLock;
	private static final String TAG = "EyeCamActivity";
	
	private SurfaceHolder.Callback mCallback = new SurfaceHolder.Callback() {

		public void surfaceDestroyed(SurfaceHolder holder) {
			if (mCamera != null){
				mCamera.stopPreview();
				releaseCamera();
			}
		}

		public void surfaceCreated(SurfaceHolder holder) {
			setupCamera();
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			setupHolder(holder);
		}
	};

	private DisplayMetrics getDisplaySize() {
		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics; 
	}

	private void setupHolder(SurfaceHolder holder) {
		mHolder = holder;
		mHolder.addCallback(mCallback);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	private void setupCamera() {
		try {
			mCamera.setPreviewDisplay(mHolder);
			Parameters params = modifyCamParameter(mCamera.getParameters());
			mCamera.setParameters(params);
		} catch (IOException e) {
			mCamera.release();
			Log.v(TAG,e.getMessage());
		}
	}

	private Parameters modifyCamParameter(Parameters params) {		
		params.setPreviewSize(getDisplaySize().widthPixels, getDisplaySize().heightPixels);
		return params;
	}

	private void releaseCamera() {
		mCamera.release();
		mCamera = null;
	}

	@Override
	protected void onPause() {
		super.onPause();
		releaseCamera();
		mWakeLock.release(); 
	}

	@Override
	protected void onResume() {
		super.onResume();

		mCamera = Camera.open();
		setupCamera();
		mCamera.startPreview();
		
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
	
		mSurfaceView = (SurfaceView) findViewById(R.id.cameraSurface);
		setupHolder(mSurfaceView.getHolder());
		
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "eyeCam");
		
		
	}

}
