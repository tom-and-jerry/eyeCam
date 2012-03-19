package ch.hsr.eyecam;

import ch.hsr.eyecam.R.id;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import ch.hsr.eyecam.colormodel.Color;
import ch.hsr.eyecam.widget.FloatingBubble;

public class IntroductionActivity extends Activity {
	public static final String INTRO_PREFIX = "eyecam_introduction";
	private static String INTRO_KEY;
	private int mContentView;
	private SharedPreferences mSharedPreferences;
	private ImageView mPreviewImage;
	private FloatingBubble mFloatingBubble;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		PackageInfo versionInfo = getPackageInfo();
		INTRO_KEY = INTRO_PREFIX + versionInfo.versionCode;

		setStepOne();
	}

	private PackageInfo getPackageInfo() {
		PackageInfo pi = null;
		try {
			pi = getPackageManager().getPackageInfo(getPackageName(),
					PackageManager.GET_ACTIVITIES);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return pi;
	}

	private void setStepOne() {
		setContentView(R.layout.intro_step_one);
		mContentView = R.layout.intro_step_one;
	}

	private void setStepTwo() {
		mContentView = R.layout.intro_step_two;
		setContentView(mContentView);
	}

	private void setStepThree() {
		mContentView = R.layout.intro_step_three;
		setContentView(mContentView);

		mPreviewImage = (ImageView) findViewById(id.preview_image);
		mPreviewImage.setDrawingCacheEnabled(true);
		mPreviewImage.buildDrawingCache(false);

		mFloatingBubble = new FloatingBubble(getApplicationContext(),
				mPreviewImage);
		mFloatingBubble.setOrientation(Orientation.LANDSCAPE_LEFT);
		initOnTouchListener();
		
	}

	private void initOnTouchListener() {
		mPreviewImage.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					int x = (int) event.getX();
					int y = (int) event.getY();
					showColorAt(x, y);
					return true;
				}
				return false;
			}
		});
	}

	private void showColorAt(int x, int y) {
		int rgb = mPreviewImage.getDrawingCache().getPixel(x, y);
		int r = (rgb & 0xff0000) >> 16;
		int g = (rgb & 0x00ff00) >> 8;
		int b = (rgb & 0x0000ff);

		int[] rgbArray = { r, g, b, };

		int colorId = Color.rgbToColor(rgbArray);
		mFloatingBubble.showStringResAt(colorId, x, y);
	}

	public void next(View v) {
		switch (mContentView) {
		case R.layout.intro_step_one:
			setStepTwo();
			break;
		case R.layout.intro_step_two:
			setStepThree();
			break;
		}
	}

	public void finishIntro(View v) {
		mFloatingBubble.dismiss();

		Editor editor = mSharedPreferences.edit();
		editor.putBoolean(INTRO_KEY, true);
		editor.commit();

		finish();
	}

	@Override
	public void onBackPressed() {
		switch (mContentView) {
		case R.layout.intro_step_two:
			setStepOne();
			break;
		case R.layout.intro_step_three:
			mFloatingBubble.dismiss();
			setStepTwo();
			break;
		}
	}

}
