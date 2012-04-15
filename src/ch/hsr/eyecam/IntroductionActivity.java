package ch.hsr.eyecam;

import ch.hsr.eyecam.R.id;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import ch.hsr.eyecam.colormodel.Color;
import ch.hsr.eyecam.widget.FloatingBubble;
import ch.hsr.eyecam.widget.MenuBubble;

/**
 * Shows an introduction to eyeCam.
 * 
 * Whether or not the introduction has already run is saved in the shared
 * preferences of the application under the key consisting of the INTRO_PREFIX
 * defined in this class and the version string of the application. This means
 * that it will be shown every single update.
 * 
 * @author jimmypoms
 * 
 */
public class IntroductionActivity extends Activity {
	public static final String INTRO_PREFIX = "eyecam_introduction";
	private static String INTRO_KEY;
	private int mContentView;
	private SharedPreferences mSharedPreferences;
	private ImageView mPreviewImage;
	private FloatingBubble mFloatingBubble;
	private MenuBubble mAppMenu;
	private DisplayMetrics mMetrics = new DisplayMetrics();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		PackageInfo versionInfo = getPackageInfo();
		INTRO_KEY = INTRO_PREFIX + versionInfo.versionCode;
		
		getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
		setStepOne(null);
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

	public void setStepOne(View v) {
		setContentView(R.layout.intro_step_one);
		mContentView = R.layout.intro_step_one;
	}

	public void setStepTwo(View v) {
		mContentView = R.layout.intro_step_two;
		setContentView(mContentView);
	}

	public void setStepThree(View v) {
		mContentView = R.layout.intro_step_three;
		setContentView(mContentView);

		mPreviewImage = (ImageView) findViewById(id.preview_image);
		mPreviewImage.setDrawingCacheEnabled(true);
		mPreviewImage.buildDrawingCache(false);

		LayoutInflater inflater = (LayoutInflater) getBaseContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View contentView = inflater.inflate(R.layout.intro_menu, null);
		View anchorView = findViewById(android.R.id.content).getRootView();
		mAppMenu = new MenuBubble(anchorView, contentView);
		mAppMenu.setContentOrientation(Orientation.LANDSCAPE_LEFT);
		mAppMenu.setMaxWidth((mMetrics.widthPixels / 10) * 9);
		mAppMenu.setMaxHeight((mMetrics.heightPixels / 10) * 4);

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
					return false;
				}
				return false;
			}
		});

		mPreviewImage.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				mAppMenu.show();
				return true;
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
			setStepTwo(null);
			break;
		case R.layout.intro_step_two:
			setStepThree(null);
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
		if (mAppMenu != null && mAppMenu.isShowing())
			mAppMenu.dismiss();
		else {
			switch (mContentView) {
			case R.layout.intro_step_one:
				finish();
				break;
			case R.layout.intro_step_two:
				setStepOne(null);
				break;
			case R.layout.intro_step_three:
				mFloatingBubble.dismiss();
				setStepTwo(null);
				break;
			}
		}
	}

}