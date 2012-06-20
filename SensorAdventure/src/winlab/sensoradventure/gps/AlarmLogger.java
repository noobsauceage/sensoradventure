package winlab.sensoradventure.gps;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 *  
 *
 */
public class AlarmLogger extends BroadcastReceiver {
	        @Override
	        public void onReceive(Context context, Intent intent) {
	                context.startService(new Intent(context,GPSloggerService.class));
	        }
	}
 
