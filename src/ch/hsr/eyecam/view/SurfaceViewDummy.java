package ch.hsr.eyecam.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Dummy class in order to be able to start preview on ICS.
 * 
 * Due to security considerations as of ICS the camera will not 
 * provide preview frames without having a SurfaceHolder registered
 * with the camera.
 * 
 * In order to still be able to start the camera preview the
 * SurfaceViewDummy class extends SurfaceView and makes sure it
 * does not get displayed.
 * 
 * @author jimmypoms
 *
 */
public class SurfaceViewDummy extends SurfaceView {
	public SurfaceViewDummy(Context context) {
		super(context);
		getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public SurfaceViewDummy(Context context, AttributeSet arg1) {
		super(context, arg1);
		getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public SurfaceViewDummy(Context context, AttributeSet arg1, int arg2) {
		super(context, arg1, arg2);
		getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	/**
	 * make sure the SurfaceView does not overlay on the ColorView
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		this.setMeasuredDimension(2, 2);
	}
	
	/**
	 * do nothing.
	 */
	@Override
	protected void onDraw(Canvas canvas) {
	}
}
