package ch.hsr.eyecam.view;


import ch.hsr.eyecam.EyeCamActivity;
import ch.hsr.eyecam.colormodel.ColorTransform;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class ColorView extends View implements PreviewCallback{
	private Bitmap mBitmap;
	private Handler mHandler;
	private Handler mActivityHandler;
	private static String LOG_TAG = "ch.hsr.eyecam.view.ColorView";

	public ColorView(Context context){
		super(context);
		mHandler.sendEmptyMessage(EyeCamActivity.CAMERA_START_PREVIEW);
	}

	public ColorView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (mBitmap == null && getWidth() > 0 && getHeight() > 0){
			mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.RGB_565);
			mActivityHandler.sendEmptyMessage(EyeCamActivity.CAMERA_START_PREVIEW);
			Log.d(LOG_TAG, "Bitmap size: W: "+getWidth()+" H: "+getHeight());
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawBitmap(mBitmap, 0, 0, null);
	}
	
	public void setHandler(Handler handler) {
		mActivityHandler = handler;
	}
	
	@Override
	public void onPreviewFrame(byte[] data, Camera cam) {
		int width = cam.getParameters().getPreviewSize().width;
		int height = cam.getParameters().getPreviewSize().height;
		
		ColorTransform.transformImageToBitmap(data, width, height, mBitmap);
		cam.addCallbackBuffer(data);
		invalidate();
	}
	
}
