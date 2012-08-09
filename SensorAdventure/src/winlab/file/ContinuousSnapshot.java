package winlab.file;

import java.io.File;
import java.io.FileWriter;

import winlab.ASL.AndroidSensors;
import winlab.SensorGUI.StartGUI;
import winlab.sensoradventure.gps.GPSLoggerService;
import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Sensor;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class ContinuousSnapshot {
	private AsyncTask<Void, Void, Void> asyncTask; // add
	private long rate, duration;
	private File path;
	private String fileName = "Continuous_snapShot.txt";
	private File file = new File(path, fileName);
	private boolean flag2 = true;
	private FileWriter output;
	private final Handler handler1, handler2;
	private Context programContext;
	private boolean Snapshot_finish = true;

	public ContinuousSnapshot(long rate, long duration, Context con) {
		programContext = con;
		this.rate = rate;
		this.duration = duration;
		path = AndroidSensors.DataPath;

		handler1 = new Handler() {
			public void handleMessage(Message msg) {
				Toast.makeText(programContext, "Continuous snapShot starts!",
						Toast.LENGTH_LONG).show();
			}
		};

		handler2 = new Handler() {
			public void handleMessage(Message msg) {
				Toast.makeText(programContext, "Continuous snapShot finished!",
						Toast.LENGTH_LONG).show();
			}
		};

	}

	public void performSnapshot() {
		if (Snapshot_finish) {
			asyncTask = new start();
			asyncTask.execute();
		}
	}

	// Asynchronous task that performs the continuous snapshot
	private class start extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... n) {

			long startTime, currentTime, lastTime;

			Snapshot_finish = false;

			// Print the beginning message as a toast
			Message msg1 = handler1.obtainMessage();
			handler1.sendMessage(msg1);

			startTime = System.currentTimeMillis();
			lastTime = startTime;

			print();
			do {
				currentTime = System.currentTimeMillis();
				/*
				 * If the difference between the two intervals are greater than
				 * the rate of data capture
				 */
				if (currentTime - lastTime >= rate) {
					print();
					lastTime = currentTime;
				}
				// While the difference between two intervals is less than
				// duration in seconds
			} while (currentTime - startTime < duration * 1000);

			Message msg2 = handler2.obtainMessage();
			handler2.sendMessage(msg2);
			Snapshot_finish = true;
			return null;
		}
	}

	@TargetApi(9)
	public void print() {
		file = new File(path, fileName);
		String str = "";
		str = str + "Timestamp (ms): "
				+ String.format("%d", System.currentTimeMillis()) + "\n";
		try {
			path.mkdirs();
			  file.setWritable(true);

			// If the output object has not been created yet
			// This method will be called multiple times

			if (flag2)
				output = new FileWriter(file);
			else
				output = new FileWriter(file, true);
			flag2 = false;
			for (int i = 0; i < 13; i++)
				if (SensorSetting.sensors[i]) {
					switch (i + 1) {
					case Sensor.TYPE_ACCELEROMETER:
						str = str
								+ "Accelerometer x (m/s^2): "
								+ String.format("%17.10f",
										MarkValue.instantValue[i][0])
								+ "\n";
						str = str
								+ "Accelerometer y (m/s^2): "
								+ String.format("%17.10f",
										MarkValue.instantValue[i][1])
								+ "\n";
						str = str
								+ "Accelerometer z (m/s^2): "
								+ String.format("%17.10f",
										MarkValue.instantValue[i][2])
								+ "\n";
						break;
					case Sensor.TYPE_MAGNETIC_FIELD:
						str = str
								+ "Magnetic Field x (uT): "
								+ String.format("%17.10f",
										MarkValue.instantValue[i][0])
								+ "\n";
						str = str
								+ "Magnetic Field y (uT): "
								+ String.format("%17.10f",
										MarkValue.instantValue[i][1])
								+ "\n";
						str = str
								+ "Magnetic Field z (uT): "
								+ String.format("%17.10f",
										MarkValue.instantValue[i][2])
								+ "\n";
						break;
					case Sensor.TYPE_ORIENTATION:
						str = str
								+ "Orientation x (degrees): "
								+ String.format("%17.10f",
										MarkValue.instantValue[i][0])
								+ "\n";
						str = str
								+ "Orientation y (degrees): "
								+ String.format("%17.10f",
										MarkValue.instantValue[i][1])
								+ "\n";
						str = str
								+ "Orientation z (degrees): "
								+ String.format("%17.10f",
										MarkValue.instantValue[i][2])
								+ "\n";
						break;
					case Sensor.TYPE_GYROSCOPE:
						str = str
								+ "Gyroscope x (rad/s): "
								+ String.format("%17.10f",
										MarkValue.instantValue[i][0])
								+ "\n";
						str = str
								+ "Gyroscope y (rad/s): "
								+ String.format("%17.10f",
										MarkValue.instantValue[i][1])
								+ "\n";
						str = str
								+ "Gyroscope z (rad/s): "
								+ String.format("%17.10f",
										MarkValue.instantValue[i][2])
								+ "\n";

						break;
					case Sensor.TYPE_LIGHT:
						str = str
								+ "Light (lx): "
								+ String.format("%17.10f",
										MarkValue.instantValue[i][0])
								+ "\n";
						break;
					case Sensor.TYPE_PRESSURE:
						str = str
								+ "Pressure (hPa): "
								+ String.format("%17.10f",
										MarkValue.instantValue[i][0])
								+ "\n";
						break;
					case Sensor.TYPE_TEMPERATURE:
						str = str
								+ "Device Temperature (degree Celsius): "
								+ String.format("%17.10f",
										MarkValue.instantValue[i][0])
								+ "\n";
						break;
					case Sensor.TYPE_PROXIMITY:
						str = str
								+ "Proximity (cm)"
								+ String.format("%17.10f",
										MarkValue.instantValue[i][0])
								+ "\n";
						break;

					case Sensor.TYPE_GRAVITY:
						str = str
								+ "Gravity x (m/s^2): "
								+ String.format("%17.10f",
										MarkValue.instantValue[i][0])
								+ "\n";
						str = str
								+ "Gravity x (m/s^2): "
								+ String.format("%17.10f",
										MarkValue.instantValue[i][1])
								+ "\n";
						str = str
								+ "Gravity x (m/s^2): "
								+ String.format("%17.10f",
										MarkValue.instantValue[i][2])
								+ "\n";

						break;
					case Sensor.TYPE_LINEAR_ACCELERATION:
						str = str
								+ "Linear Accelerometer x (m/s^2): "
								+ String.format("%17.10f",
										MarkValue.instantValue[i][0])
								+ "\n";
						str = str
								+ "Linear Accelerometer y (m/s^2): "
								+ String.format("%17.10f",
										MarkValue.instantValue[i][1])
								+ "\n";
						str = str
								+ "Linear Accelerometer z (m/s^2): "
								+ String.format("%17.10f",
										MarkValue.instantValue[i][2])
								+ "\n";

						break;
					case Sensor.TYPE_ROTATION_VECTOR:
						str = str
								+ "Rotation Vector x unitless: "
								+ String.format("%17.10f",
										MarkValue.instantValue[i][0])
								+ "\n";
						str = str
								+ "Rotation Vector y unitless: "
								+ String.format("%17.10f",
										MarkValue.instantValue[i][1])
								+ "\n";
						str = str
								+ "Rotation Vector z unitless: "
								+ String.format("%17.10f",
										MarkValue.instantValue[i][2])
								+ "\n";
						if (Math.abs(MarkValue.instantValue[i][3] - 0) < 1.0e-15)
							str = str
									+ "Rotation Vector scalar:                NA\n";
						else
							str = str
									+ "Rotation Vector scalar: "
									+ String.format("%17.10f",
											MarkValue.instantValue[i][3])
									+ "\n";

						break;
					case 12: // Sensor.TYPE_RELATIVE_HUMIDITY
						str = str
								+ "Relative Humidity %: "
								+ String.format("%17.10f",
										MarkValue.instantValue[i][0])
								+ "\n";
						break;
					case 13: // Sensor.TYPE_AMBIENT_TEMPERATURE
						str = str
								+ "Ambient air temperature (degree Celsius): "
								+ String.format("%17.10f",
										MarkValue.instantValue[i][0])
								+ "\n";
						break;
					}
					str = str + "\n";
				}
			boolean sensorcheck  =StartGUI.getsensorcheck();
			if(sensorcheck)
			{
				if(GPSLoggerService.getgpsmark()!=null)
				{
					String a[] = GPSLoggerService.getgpsmark();
					str = str + "GPS " +"\n";
					str = str + "Device Id   = " +a[0] + "\n";
					str = str + "Latitude    = " +a[1] + "\n";
					str = str + "Longitude = " +a[2] + "\n";
					str = str + "Altitude     = " +a[3]+ "\n";
					str = str + "Bearing      = " +a[4]+ "\n";
					str = str + "Accuracy   = " +a[5]+ "\n";
					str = str + "Provider    = " +a[6]+ "\n";
					str = str + "Speed	      = " +a[7]+ "\n";
				}
			}
			str = str
					+ "-----------------------------------------------------\n";
			output.write(str);
			output.close();
		} catch (Exception e) {
		}
	}

}
