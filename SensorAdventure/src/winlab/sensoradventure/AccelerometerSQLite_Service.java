package winlab.sensoradventure;

import android.app.Service;
//import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.*;
//import android.telephony.TelephonyManager;
import android.widget.Toast;

public class AccelerometerSQLite_Service extends Service implements
		SensorEventListener {

	// private TelephonyManager telephonyManager;
	private long Rate = 0;
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private Sensor_SQLite data;

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

			Toast.makeText(this, "Data will be saved in SQLiteDataBase",
					Toast.LENGTH_LONG).show();
			data = new Sensor_SQLite(this);

			data.open();
			data.deleteTable();

			// Toast.makeText(this,"I am here", Toast.LENGTH_LONG).show();
			/*
			 * telephonyManager=(TelephonyManager)
			 * getSystemService(Context.TELEPHONY_SERVICE);
			 * output.write("Phone ID: "+ telephonyManager.getDeviceId());
			 */

		} else {
			data = new Sensor_SQLite(this);

			data.open();
			data.deleteTable();
			for (int i = 0; i < 7; i++)
				data.insertTitle("10", " w", "wq ", "r ",1);
			Toast.makeText(this,
					"Sorry! The accelerometer is not found in the device.",
					Toast.LENGTH_LONG).show();

		}

	}

	public void onSensorChanged(SensorEvent event) {
		String timestamp = "";
		String str1 = "";
		String str2 = "";
		String str3 = "";
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

			timestamp = String.format("%d", System.currentTimeMillis());
			str1 = String.format("%.10f", event.values[0]);
			str2 = String.format("%.10f", event.values[1]);
			str3 = String.format("%.10f", event.values[2]);
			Rate++;
			if (Rate == 10) {
				data.insertTitle(timestamp, str1, str2, str3,1);
				Rate = 0;
			}

		}
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onDestroy() {
		String result = "timestamp (ms)            x (m/s^2)            y (m/s^2)           z (m/s^2)\n";
		int count = 0;

		Toast.makeText(this, "Stop taking accelerometer readings",
				Toast.LENGTH_LONG).show();
		Toast.makeText(this,
				"The top 10 data in the SQLite table will be shown!",
				Toast.LENGTH_LONG).show();
		mSensorManager.unregisterListener(this, mSensor);
		Cursor c = data.getAllTitles(1);
		if (c.moveToFirst()) {
			do {
				count++;
				result = result + c.getString(1) + "    " + c.getString(2)
						+ "    " + c.getString(3) + "      " + c.getString(4)
						+ "\n";
			} while ((c.moveToNext()) && (count < 10));
		}
		data.close();

		Toast.makeText(this, result, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onStart(Intent intent, int startid) {
		mSensorManager.registerListener(this, mSensor, 1000);
	}
}
