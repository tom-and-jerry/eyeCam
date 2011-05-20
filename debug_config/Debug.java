package ch.hsr.eyecam;
import android.util.Log;

public class Debug {

	/** Whether or not to include logging statements in the application. */
	public final static boolean LOGGING = @CONFIG.LOGGING@;
	
	public static void msg(String LOG_TAG, String msg){
		if(!LOGGING) return;
		Log.d(LOG_TAG, msg);
	}
	
	public static void msg(String LOG_TAG, String msg, Throwable tr){
		if(!LOGGING) return;
		Log.d(LOG_TAG, msg, tr);
	}
}
