package winlab.sensoradventure.gps;
 
import winlab.sensoradventure.gps.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * This class will be used to start the service
 * Currently it has been set to GPSloggerServicedb so that
 * the logging is done in Database
 */
public class AlarmReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
                context.startService(new Intent(context,GPSloggerServicedb.class));
        }
}