package winlab.sensoradventure;

//Xianyi Gao
import java.io.File;
import java.io.FileWriter;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.*;
//import android.util.Log;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class Accelerometer extends Service implements SensorEventListener {
	// private static final String TAG = "MyService";
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private String fileName = "Accelerometer.txt";
	private File path = Environment
			.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
	private File file = new File(path, fileName);
	private TelephonyManager telephonyManager;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		Toast.makeText(this, "Start taking accelerometer readings",
				Toast.LENGTH_LONG).show();
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
			mSensor = mSensorManager
					.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			try {
				path.mkdirs();
				file.setWritable(true);
				Toast.makeText(this,
						"Data will be saved in " + file.getAbsolutePath(),
						Toast.LENGTH_LONG).show();
				FileWriter output = new FileWriter(file);
				telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
				output.write("Phone ID: " + telephonyManager.getDeviceId());
				output.write("\nTimestamp           x (m/s^2)        y (m/s^2)        z (m/s^2)");
				output.close();
			} catch (Exception e) {
			}
		} else {
			try {
				path.mkdirs();
				file.setWritable(true);
				Toast.makeText(this,
						"Data is saved in " + file.getAbsolutePath(),
						Toast.LENGTH_LONG).show();
				Toast.makeText(this,
						"Sorry! The accelerometer is not found in the device.",
						Toast.LENGTH_LONG).show();
				FileWriter output = new FileWriter(file);
				telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
				output.write("Phone ID: " + telephonyManager.getDeviceId());
				output.write("The accelerometer is not found in the device.\n");
				output.close();

			} catch (Exception e) {
			}
		}
		// Log.d(TAG, "onCreate");

	}

	public void onSensorChanged(SensorEvent event) {
		String str = "";
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			try {
				path.mkdirs();
				file.setWritable(true);
				FileWriter output = new FileWriter(file, true);
				str = String.format("\n%d:%17.10f%17.10f%17.10f",
						System.currentTimeMillis(), event.values[0],
						event.values[1], event.values[2]);
				output.write(str);
				output.close();
			} catch (Exception e) {
			}
		}
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "Stop taking accelerometer readings",
				Toast.LENGTH_LONG).show();
		Toast.makeText(this,
				"Data has been saved in " + file.getAbsolutePath(),
				Toast.LENGTH_LONG).show();
		mSensorManager.unregisterListener(this, mSensor);
		// Log.d(TAG, "onDestroy");
	}

	@Override
	public void onStart(Intent intent, int startid) {
		// int dd;
		// Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
		mSensorManager.registerListener(this, mSensor, 1000);
		// Log.d(TAG, "onStart");
		// while (1==1) {dd=0;}
	}
}