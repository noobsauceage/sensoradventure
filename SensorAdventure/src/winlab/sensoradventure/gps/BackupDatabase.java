/**
 * 
 */
package winlab.sensoradventure.gps;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
 
import winlab.sensoradventure.R;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

/**
 * @author malathidharmalingam
 *
 */
public class BackupDatabase extends Activity {
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
        doInBackground() ;
	}
	
	public  Boolean doInBackground() {

        File dbFile =
                 new File(Environment.getDataDirectory() + "/data/winlab.sensoradventure/databases/SensorDatabase1");

        File exportDir = new File(Environment.getExternalStorageDirectory(), "Databasebkup");
        if (!exportDir.exists()) {
           exportDir.mkdirs();
        }
        File file = new File(exportDir, "Senso1.txt");

        try {
           file.createNewFile();
           this.copyFile(dbFile, file);
           return true;
        } catch (IOException e) {
           Log.e("mypck", e.getMessage(), e);
           return false;
        }
     }
	
	
	public void copyFile(File src, File dst) throws IOException {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try {
           inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
           if (inChannel != null)
              inChannel.close();
           if (outChannel != null)
              outChannel.close();
        }
     }
	
	public void copy() {
 
			GPSLoggerSQLite data = new GPSLoggerSQLite(this);
			String result = "device_id(IMEI)       timestamp (ms)            LAT (degrees)            LONG (degrees)          ALT (degrees)        BEARING         ACCURACY     PROVIDER   SPEED\n";
			Log.i("ok","okkkkkkk");
			data.open();
			Cursor c = data.getAllgpsdata(1);		
						if (c.moveToFirst()) {

				do {
					result = result + c.getString(1) + "    " + c.getString(2)
							+ "    " + c.getString(3) + "      " + c.getString(4)
							+ "      " + c.getString(5) + "           "
							+ c.getString(6) + "        "+c.getString(7) + "    " + c.getString(8)
							 +"\n";
				} while ((c.moveToNext()));
 
			AppLog.logString("result" + result);
			data.close();
		}
 
			/*
			if (sd.canWrite()) {
				String currentDBPath = "//data//" + "winlab.sensoradventure"
						+ "//databases//" + "SensorDatabase1";
				String backupDBPath = "/tmp/SensorDatabase";
				File currentDB = new File(data, currentDBPath);
				File backupDB = new File(sd, backupDBPath);

				FileChannel src = new FileInputStream(currentDB).getChannel();
				FileChannel dst = new FileOutputStream(backupDB).getChannel();
				dst.transferFrom(src, 0, src.size());
				src.close();
				dst.close();
				Message msg = handler.obtainMessage();
				msg.arg1 = 1;
				handler.sendMessage(msg);
			}
		} catch (Exception e) {
			Message msg = handler.obtainMessage();
			msg.arg1 = 2;
			handler.sendMessage(msg);

		}
	}

	private final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			Object context = null;
			if (msg.arg1 == 1)
				Toast.makeText((Context) context, "/temp/SensorDatabase",
						Toast.LENGTH_LONG).show();
			if (msg.arg1 == 2)
				Toast.makeText((Context) context, "Failed", Toast.LENGTH_LONG).show();
		}
	};
	*/
	}
}
