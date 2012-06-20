package winlab.sensoradventure.gps;
import android.util.Log;
public class AppLogger {
	 private static final String APP_TAG = "GPSLogger";
     
     public static int logString(String message){
             return Log.i(APP_TAG, message);
     }
}


 