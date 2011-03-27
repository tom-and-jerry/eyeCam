package ch.hsr.eyecam;


import ch.hsr.eyecam.transform.ColorTransform;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;

public class SurfaceViewSimulate extends SurfaceView implements PreviewCallback{
	private Bitmap mBitmap;
	private static String LOG_TAG = "SurfaceViewSimulate";

	public SurfaceViewSimulate(Context context){
		super(context);

		mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.RGB_565);
	}

	public SurfaceViewSimulate(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public void onPreviewFrame(byte[] data, Camera cam) {
		int width = cam.getParameters().getPreviewSize().width;
		int height = cam.getParameters().getPreviewSize().height;
		
		// TODO: find out why mBitmap is null the first time
		if (mBitmap == null) {
			Log.d(LOG_TAG , "Bitmap == null");
			mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.RGB_565);
			Log.d(LOG_TAG, "Bitmap = " +mBitmap);
		}
		
		ColorTransform.transformImageToBitmap(data, width, height, mBitmap);

		Canvas canvas = getHolder().lockCanvas();
		canvas.drawBitmap(mBitmap, 0, 0, null);
		getHolder().unlockCanvasAndPost(canvas);
		
		cam.addCallbackBuffer(data);
	}
	
}
