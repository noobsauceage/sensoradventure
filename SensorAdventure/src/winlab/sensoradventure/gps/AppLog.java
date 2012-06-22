/**
 * 
 */
package winlab.sensoradventure.gps;
import android.util.Log;
/**
 * This class for Applog, is just to print
 * message in Logcat, on how the App is progressing
 * and to fix issues during development
 */
public class AppLog {
        private static final String APP_TAG = "GPSLogger";      
        public static int logString(String message){
                return Log.i(APP_TAG, message);
        }
}


