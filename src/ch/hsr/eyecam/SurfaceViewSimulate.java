package ch.hsr.eyecam;


import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;

public class SurfaceViewSimulate extends SurfaceView implements PreviewCallback{
	private Matrix matrix = new Matrix();
	private Paint paint = new Paint();
	private static final int NV21_FORMAT = 17;

	public SurfaceViewSimulate(Context context){
		super(context);
	}

	public SurfaceViewSimulate(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public void onPreviewFrame(byte[] data, Camera cam) {
		int w = cam.getParameters().getPreviewSize().width;
		int h = cam.getParameters().getPreviewSize().height;
		
		YuvImage yuv = new YuvImage(data, NV21_FORMAT, w, h, null);
		if (yuv == null) Log.i(VIEW_LOG_TAG,"Bitmap null");
		Rect rect = new Rect(0, 0, w, h);
		ByteArrayOutputStream output_stream = new ByteArrayOutputStream();
		yuv.compressToJpeg(rect, 50, output_stream);
		
		// Convert from Jpeg to Bitmap
		Bitmap bmp = BitmapFactory.decodeByteArray(output_stream.toByteArray(), 0, output_stream.size());
		Canvas canvas = getHolder().lockCanvas();
		canvas.drawBitmap(bmp, matrix, paint);
		getHolder().unlockCanvasAndPost(canvas);
	}
	
}
