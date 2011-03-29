package ch.hsr.eyecam.test;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.test.ActivityInstrumentationTestCase2;
import ch.hsr.eyecam.EyeCamActivity;

public class EyeCamActivityTest extends
		ActivityInstrumentationTestCase2<EyeCamActivity> {

	private EyeCamActivity mActivity;
	private Configuration mConfiguration;
	private ActivityInfo mActivityInfo;

	public EyeCamActivityTest() {
		super("ch.hsr.eyecam.EyeCamActivity", EyeCamActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mActivity = this.getActivity();
		mConfiguration = mActivity.getResources().getConfiguration();
		mActivityInfo = mActivity.getPackageManager().getActivityInfo(
				mActivity.getComponentName(), 0);
	}
	
	public void testPreconditions(){
		assertNotNull(mActivity);
		assertNotNull(mConfiguration);
		assertNotNull(mActivityInfo);
	}
	
	public void testRotation(){
		assertEquals("false startup rotation config",  
				ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE,
				mActivityInfo.screenOrientation);
		assertEquals("Activity does not handle rotation changes itself", 
				mActivityInfo.configChanges | ActivityInfo.CONFIG_ORIENTATION,
				mActivityInfo.configChanges);
		assertEquals("Activity does not handle hardware keyboard changes itself", 
				mActivityInfo.configChanges | ActivityInfo.CONFIG_KEYBOARD_HIDDEN,
				mActivityInfo.configChanges);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

}
