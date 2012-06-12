package winlab.sensoradventure;
//Xianyi Gao
import java.io.File;
import java.io.FileWriter;

import android.app.Service;
//import android.content.Context;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.*;
//import android.util.Log;
//import android.telephony.TelephonyManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class Gyroscope extends Service implements
SensorEventListener {
	private TelephonyManager telephonyManager;
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private String fileName = "Gyroscope.txt";
	private File path = Environment
			.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
	private File file = new File(path, fileName);
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public void onCreate() {
		Toast.makeText(this, "Start taking gyroscope data", Toast.LENGTH_LONG).show();
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		if (mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
			mSensor = mSensorManager
					.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
			try {
				path.mkdirs();
				file.setWritable(true);
				Toast.makeText(this,"Data will be saved in " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
				FileWriter output = new FileWriter(file);
				telephonyManager=(TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
				output.write("Phone ID: "+ telephonyManager.getDeviceId());
				output.write("\nTimestamp           x (rad/s)        y (rad/s)        z (rad/s)");
				output.close();
			} catch (Exception e) {
			}
		} else {
			try {
				path.mkdirs();
				file.setWritable(true);
				Toast.makeText(this,"Data is saved in " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
				Toast.makeText(this,"Sorry! The gyroscope is not found in the device.", Toast.LENGTH_LONG).show();
				FileWriter output = new FileWriter(file);
				telephonyManager=(TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
				output.write("Phone ID: "+ telephonyManager.getDeviceId());
				output.write("The gyroscope is not found in the device.\n");
				output.close();

			} catch (Exception e) {}
		}
	}
		public void onSensorChanged (SensorEvent event) {
			String str = "";
			if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
				try {
					path.mkdirs();
					file.setWritable(true);
					FileWriter output = new FileWriter(file, true);
					str = String.format("\n%d:%17.10f%17.10f%17.10f",
							System.currentTimeMillis(), event.values[0], event.values[1],
							event.values[2]);
					output.write(str);
					output.close();
				} catch (Exception e) {}
			}
		}
		
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
		
		@Override
		public void onDestroy() {
			Toast.makeText(this, "Stop taking gyroscope data", Toast.LENGTH_LONG).show();
			Toast.makeText(this,"Data has been saved in " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
			mSensorManager.unregisterListener(this, mSensor);
			//Log.d(TAG, "onDestroy");
		}
		
		@Override
		public void onStart(Intent intent, int startid) {
		//	int dd;
			//Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
			mSensorManager.registerListener(this, mSensor,
					1000);
			//Log.d(TAG, "onStart");
			//while (1==1) {dd=0;}
		}

}

