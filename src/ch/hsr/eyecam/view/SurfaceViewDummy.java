package ch.hsr.eyecam.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceView;

public class SurfaceViewDummy extends SurfaceView {
	public SurfaceViewDummy(Context context) {
		super(context);
	}

	public SurfaceViewDummy(Context context, AttributeSet arg1) {
		super(context, arg1);
	}

	public SurfaceViewDummy(Context context, AttributeSet arg1, int arg2) {
		super(context, arg1, arg2);
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
