package ch.hsr.eyecam;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class EyeCamSurfacView extends SurfaceView implements SurfaceHolder.Callback {

	private SurfaceHolder mHolder;
	private final DisplayMetrics mMetrics = new DisplayMetrics();
	private Camera mCamera;
	private final static String mTAG = "ch.hsr.EyeCamSurfaceView";
	
	public EyeCamSurfacView(Context context, AttributeSet attrs) {
		super(context, attrs);
		((Activity) context).getWindowManager()
        .getDefaultDisplay().getMetrics(mMetrics );
		
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}
	
	private void setupCamera() {
		Camera.Parameters parameters = mCamera.getParameters();
		parameters.setPictureSize(mMetrics.widthPixels, mMetrics.heightPixels);
		mCamera.setParameters(parameters);
	}
	
	private void initCamera() {
		if(mCamera != null) return;
		try{
			mCamera.setPreviewDisplay(mHolder);
		} catch (Exception e) {
			releaseCamera();
			Log.v(mTAG,e.getCause()+e.getMessage());
		}
		
	}
	
	private void releaseCamera(){
		mCamera.release();
		mCamera = null;
	}
	
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		initCamera();
		setupCamera();
		mCamera.startPreview();
		
	}
	
	public void surfaceCreated(SurfaceHolder holder) {
		initCamera();
	}
	
	public void surfaceDestroyed(SurfaceHolder holder) {
		mCamera.stopPreview();
		releaseCamera();
	}

}
