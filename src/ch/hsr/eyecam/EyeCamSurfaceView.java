package ch.hsr.eyecam;


import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

public class EyeCamSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

	private Camera mCamera;
	private SurfaceHolder mHolder;
	private final DisplayMetrics mMetrics = new DisplayMetrics();
	private final static String mTAG = "ch.hsr.EyeCamSurfaceView";
	
	public EyeCamSurfaceView(Context context){
		super(context);
		initHolder();
	}
	public EyeCamSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		((Activity) context).getWindowManager()
        .getDefaultDisplay().getMetrics(mMetrics );
		
		initHolder();
	}

	private boolean isNotNull(Object anyObject) {
		return anyObject != null;
	}
	
	private boolean isNull(Object anyObject){
		return !isNotNull(anyObject);
	}
	private void initHolder() {
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}
	
	private void setupCamera() {
		Camera.Parameters parameters = mCamera.getParameters();	
		Size optSize = getOptimalSize(parameters.getSupportedPreviewSizes());
		parameters.setPreviewSize(optSize.width, optSize.height);
		mCamera.setParameters(parameters);
	}
	
	private double getAbsolutDiff(double aimRatio, double tmpDiffRatio) {
		return Math.abs(aimRatio-tmpDiffRatio);
	}
	
	private void initCamera() {
		if(isNotNull(mCamera)) return;
		mCamera = Camera.open();
		try{
			mCamera.setPreviewDisplay(mHolder);
		} catch (IOException e) {
			releaseCamera();
			Log.v(mTAG,e.getCause()+e.getMessage());
		}
		
	}
	
	private Size getOptimalSize(List<Size> sizeList){
		if(isNull(sizeList))return null;
		Size optSize= null;
		
		optSize = findeExactSize(sizeList);
		if(isNotNull(optSize))return optSize;
		
		optSize = findBestFitSizeByRatio(sizeList);
		return optSize;
	}
	
	private Size findBestFitSizeByRatio(List<Size> sizeList) {
		double aimRatio = mMetrics.widthPixels / mMetrics.heightPixels;
		double diffRatio = Double.MAX_VALUE;
		Size optSize = null;
		for(Size size : sizeList){
			double tmpDiffRatio = (double) size.width / size.height;
			if(getAbsolutDiff(aimRatio, tmpDiffRatio)< diffRatio){
				optSize = size;
				diffRatio = getAbsolutDiff(aimRatio, tmpDiffRatio);
			}
		}
		return optSize;
	}
	private Size findeExactSize(List<Size> sizeList) {
		for(Size size:sizeList){
			if(mMetrics.heightPixels == size.height
					&& mMetrics.widthPixels == size.width)
				return size;
		}
		return null;
	}
	
	private void releaseCamera(){
		if(isNull(mCamera)) return;
		mCamera.release();
		mCamera = null;
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		initCamera();
		setupCamera();
		mCamera.startPreview();
		
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		initCamera();
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mCamera.stopPreview();
		releaseCamera();
	}

}
