package ch.hsr.eyecam;

import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;

public class eyeCamActivity extends Activity {
	private Camera mCamera;
	private SurfaceHolder mHolder;
	private SurfaceView mSurfaceView;

	private SurfaceHolder.Callback mCallback = new SurfaceHolder.Callback() {

		public void surfaceDestroyed(SurfaceHolder holder) {
		}

		public void surfaceCreated(SurfaceHolder holder) {
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			setupHolder(holder);
			setupCamera();
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mSurfaceView = (SurfaceView) findViewById(R.id.cameraSurface);
		setupHolder(mSurfaceView.getHolder());
	}

	private void setupHolder(SurfaceHolder holder) {
		mHolder = holder;
		mHolder.addCallback(mCallback);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	protected void onPause() {
		super.onPause();

		mCamera.release();
		mCamera = null;
	}

	@Override
	protected void onResume() {
		super.onResume();

		mCamera = Camera.open();
		setupCamera();
		mCamera.startPreview();
	}

	private void setupCamera() {
		try {
			mCamera.setPreviewDisplay(mHolder);
			Parameters params = modifyCamParameter(mCamera.getParameters());
			mCamera.setParameters(params);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Parameters modifyCamParameter(Parameters params) {
		return params;
	}

}
