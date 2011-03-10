package ch.hsr.eyecam.test;

import android.content.res.Configuration;
import android.test.ActivityInstrumentationTestCase2;
import ch.hsr.eyecam.EyeCamActivity;

public class EyeCamActivityTest extends
		ActivityInstrumentationTestCase2<EyeCamActivity> {

	private EyeCamActivity mActivity;
	private Configuration mConfiguration;

	public EyeCamActivityTest() {
		super("ch.hsr.eyecam.EyeCamActivity", EyeCamActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mActivity = this.getActivity();
		mConfiguration = mActivity.getResources().getConfiguration();
	}
	
	public void testRotation(){
		assertEquals("false startup rotation config", 
				mConfiguration.orientation, 
				Configuration.ORIENTATION_LANDSCAPE);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

}
