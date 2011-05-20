package ch.hsr.eyecam;
import android.util.Log;

public class Debug {

	/** Whether or not to include logging statements in the application. */
	public final static boolean LOGGING = false;
	
	public static void msg(String LOG_TAG, String msg){
		if(LOGGING) Log.d(LOG_TAG, msg);
	}
	
	public static void msg(String LOG_TAG, String msg, Throwable tr){
		if(LOGGING) Log.d(LOG_TAG, msg, tr);
	}
}
