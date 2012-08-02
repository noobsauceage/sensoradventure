package winlab.SensorGUI;

import java.io.File;
import java.io.FileWriter;
import winlab.file.RunningService;
import winlab.file.SensorSetting;
import winlab.file.SnapShotValue;
import winlab.sensoradventure.ContinuousRecorder;
import winlab.sensoradventure.R;
import winlab.sensoradventure.SensorAdventureActivity;
import winlab.sensoradventure.gps.GPSloggerService;
import winlab.sql.Sensors_SQLite_Service;
import winlab.sql.Sensors_SQLite_Setting;
import winlab.sql.SnapShot_SQL;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

@TargetApi(9)
public class StartGUI extends Activity {
	private AsyncTask<Void, Void, Void> asyncTask; // add
	private boolean flag = true; //check whether "stop" button is ever pushed
	private boolean flag2 = true;
	private boolean flag3 = true; //check whether "start" button is ever pushed
	private boolean Snapshot_finish = true;
	private boolean[] state; // Which data configuration settings are checked
								// "On"
	private boolean[] sensorCheck; // Which sensors are checked "On" in main
									// activity
	private Button mark, startAndStop, snapshot;
	private EditText editText1, editText2;
	private Chronometer mChronometer;
	private LinearLayout markTimes;
	private LinearLayout runningSensors;
	private String fileName = "Continuous_snapShot.txt";
	private String[] Sensors = { "Accelerometer ", "Magnetic ", "Orientation ",
			"Gyroscope ", "Light ", "Pressure ", "Temperature ", "Proximity ",
			"Gravity ", "L. Accelerometer ", "Rotation ", "Humidity ",
			"A. Temperature ", "Microphone ", "GPS " };
	private int[] rates;
	private int micchanneli, micchannelo, micencode, micsampling;
	private long rate = 100;// add in ms
	private long duration = 5; // add in s
	private FileWriter output;
	private File path;
	private File file;
	private final Handler handler1 = new Handler() {
		public void handleMessage(Message msg) {
			Toast.makeText(StartGUI.this, "Continuous snapShot starts!",
					Toast.LENGTH_LONG).show();
		}
	};
	private final Handler handler2 = new Handler() {
		public void handleMessage(Message msg) {
			Toast.makeText(StartGUI.this, "Continuous snapShot finished!",
					Toast.LENGTH_LONG).show();
		}
	};
	private SensorSetting ok;
	private Sensors_SQLite_Setting ok2;
	private SnapShot_SQL data2;
	private ContinuousRecorder record;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/*
		 * This block of code creates and initializes a series of Views that are
		 * used on the screen.
		 */
		setContentView(R.layout.start_gui);
		editText1 = (EditText) findViewById(R.id.editText1);
		editText2 = (EditText) findViewById(R.id.editText2);

		editText1.setText(String.format("%d", duration));
		editText2.setText(String.format("%d", rate));

		editText1.requestFocus(0);
		editText2.requestFocus(0);

		snapshot = (Button) findViewById(R.id.snapshot);
		snapshot.setOnClickListener(snapClick);
        snapshot.setEnabled(false);
		
		mark = (Button) findViewById(R.id.button1);
		mark.setOnClickListener(markClick);
        mark.setEnabled(false);
		
		startAndStop = (Button) findViewById(R.id.button2);
		startAndStop.setOnClickListener(startClick);

		mChronometer = (Chronometer) findViewById(R.id.chronometer);

		markTimes = (LinearLayout) findViewById(R.id.layout1);
		runningSensors = (LinearLayout) findViewById(R.id.layout2);

		/*
		 * Retrieve the bundle of information from SensorAdventureActivity This
		 * contains important information such as configuration settings, which
		 * sensors have been checked on, and update rates.
		 */

		Bundle extras = getIntent().getExtras();
		sensorCheck = extras.getBooleanArray("sensorCheck");
		state = extras.getBooleanArray("state");
		rates = extras.getIntArray("rates");
		micsampling = rates[13];

		// This block of code sets the configuration for the microphone.
		if (extras.getString("micchannel").equals("MONO")) {
			micchanneli = 16;
			micchannelo = 4;
		}
		if (extras.getString("micchannel").equals("STEREO")) {
			micchanneli = 12;
			micchannelo = 12;
		}
		if (extras.getString("micencode").equals("16"))
			micencode = 2;
		if (extras.getString("micencode").equals("8"))
			micencode = 3;
		record = new ContinuousRecorder(1, micsampling, micchanneli,
				micchannelo, micencode, 3, 1, StartGUI.this);

		// Instantiate the service objects
		ok = new SensorSetting(this);
		ok2 = new Sensors_SQLite_Setting(this);
		ok.selectSensors(sensorCheck);
		ok2.selectSensors(sensorCheck);

		// Print which sensors are on to the screen
		for (int i = 0; i < sensorCheck.length; i++) 
			if (sensorCheck[i]) {
				TextView tv = new TextView(this);
				tv.setText(Sensors[i]);
				runningSensors.addView(tv);
			}

		SnapShotValue.set();
		
		// If 'Write to File' is checked, use the RunningService to write to
		// a file
		if (state[0]) 
			// Pass update rates to the sensors
			SensorSetting.setRate(rates);
			

		// If 'Write to SQLite Database' is checked
		if (state[1]) {
				// Prepare the special SQLite db for the 'Mark' button
				data2 = new SnapShot_SQL(StartGUI.this);
				data2.open();
				data2.deleteTable();
				// Pass the update rates to SQLite service
				Sensors_SQLite_Setting.setRate(rates);
			}

		
	}

	private OnClickListener startClick = new OnClickListener() {
		public void onClick(View a) {
            flag3=false; //"start" button gets pushed.
			// Prepare the timer
			mChronometer.setBase(SystemClock.elapsedRealtime());
			mChronometer.start();

			// If 'Write to File' is checked, use the RunningService to write to
			// a file
			if (state[0]) {
				startService(new Intent(StartGUI.this, RunningService.class));
				// If the microphone is on and SQLite is not checked
				if ((sensorCheck[13]) && (state[1] == false)) {
					record.record();
				}
				// If GPS is on and SQLite is not checked
				if ((sensorCheck[14]) && (state[1] == false)) {
					startService(new Intent(StartGUI.this,
							GPSloggerService.class));
				}
			}

			// If 'Write to SQLite Database' is checked
			if (state[1]) {
				startService(new Intent(StartGUI.this,
						Sensors_SQLite_Service.class));
				// If microphone is on, write to SQLite db
				if (sensorCheck[13]) {
					record.writeToSQLite();
					record.record();
				}
			}

			// Switch the 'Start' button to a 'Stop' button
			startAndStop.setOnClickListener(stopClick);
			startAndStop.setText("Stop");
            mark.setEnabled(true);
            snapshot.setEnabled(true);
		}
	};

	private OnClickListener markClick = new OnClickListener() {

		public void onClick(View a) {

			// If 'Write to File' is selected
			if (state[0])
				// Write to the instant reading file
				SnapShotValue.print();

			// If 'Write to SQLite' is selected
			if (state[1]) {
				try {
					// Attempt insertion into SQLite database
					SnapShotValue.insertSQL(data2);
				} catch (Exception e) {
					Toast.makeText(StartGUI.this, "1", Toast.LENGTH_LONG)
							.show();
				}
			}

			/*
			 * This block of code dynamically adds the time the mark button is
			 * pressed to the LinearLayout on the screen
			 */
			TextView tv = new TextView(StartGUI.this);
			tv.setText(mChronometer.getInstantTime());
			markTimes.addView(tv);
		}
	};

	private OnClickListener stopClick = new OnClickListener() {
		public void onClick(View v) {
			flag = false;

			// If 'Write to File' was selected
			if (state[0]) {
				// Stop the file writing service
				stopService(new Intent(StartGUI.this, RunningService.class));

				// If the microphone is selected & 'Write to SQLite' is not
				// selected
				if ((sensorCheck[13]) && (state[1] == false)) {
					record.stop();
					record.cancel();
				}
			}

			// If 'Write to SQLite' is selected
			if (state[1]) {
				try {
					// for (int j=0; j<13; j++)
					// if (sensorCheck[j]) data2.endTransaction(j);

					// Attempt to copy the database to the sdcard and then close
					// it
					data2.copy();
					data2.close();
				} catch (Exception e) {
					Toast.makeText(StartGUI.this, "2", Toast.LENGTH_LONG)
							.show();
				}
				stopService(new Intent(StartGUI.this,
						Sensors_SQLite_Service.class));

				// If the microphone is on
				if (sensorCheck[13]) {
					record.stop();
					record.cancel();
				}

			}
			Toast.makeText(
					StartGUI.this,
					"Data are stored in: "
							+ SensorAdventureActivity.DataPath.toString() + "/",
					Toast.LENGTH_LONG).show();

			// Stop the timer
			mChronometer.stop();
			// Disable the start/stop button
			startAndStop.setClickable(false);
			mark.setEnabled(false);
            snapshot.setEnabled(false);
		}
	};

	private OnClickListener snapClick = new OnClickListener() {
		public void onClick(View v) {

			// If a previous continuous snapshot has finished, we can begin
			// another one
			if (Snapshot_finish) {
				duration = Long.parseLong(editText1.getText().toString());
				rate = Long.parseLong(editText2.getText().toString());
				asyncTask = new start();
				asyncTask.execute();
			}

		}
	};

	public void onDestroy() {
		//If both "stop" and "start" haven't been pushed
        if (flag && flag3 && state[1])
			try {
				data2.close();
			} catch (Exception e) {}
		// If the 'Stop' button has not been pushed and "Start" button has been pushed
		if (flag && (flag3==false)) {
			// If 'Write to File' has been selected
			if (state[0]) {
				stopService(new Intent(this, RunningService.class));
				if ((sensorCheck[13]) && (state[1] == false)) {
					record.stop();
					record.cancel();
				}
			}

			// If 'Write to SQLite' has been selected
			if (state[1]) {
				try {
					data2.close();
				} catch (Exception e) {
				}
				stopService(new Intent(this, Sensors_SQLite_Service.class));
				if (sensorCheck[13])
				{
					record.stop();
					record.cancel();
				}

			}
			Toast.makeText(
					this,
					"Data are stored in: "
							+ SensorAdventureActivity.DataPath.toString() + "/",
					Toast.LENGTH_LONG).show();
			mChronometer.stop();
		}
		super.onDestroy();
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

	public void print() {
		path = SensorAdventureActivity.DataPath;
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
										SnapShotValue.instantValue[i][0])
								+ "\n";
						str = str
								+ "Accelerometer y (m/s^2): "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][1])
								+ "\n";
						str = str
								+ "Accelerometer z (m/s^2): "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][2])
								+ "\n";
						break;
					case Sensor.TYPE_MAGNETIC_FIELD:
						str = str
								+ "Magnetic Field x (uT): "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][0])
								+ "\n";
						str = str
								+ "Magnetic Field y (uT): "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][1])
								+ "\n";
						str = str
								+ "Magnetic Field z (uT): "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][2])
								+ "\n";
						break;
					case Sensor.TYPE_ORIENTATION:
						str = str
								+ "Orientation x (degrees): "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][0])
								+ "\n";
						str = str
								+ "Orientation y (degrees): "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][1])
								+ "\n";
						str = str
								+ "Orientation z (degrees): "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][2])
								+ "\n";
						break;
					case Sensor.TYPE_GYROSCOPE:
						str = str
								+ "Gyroscope x (rad/s): "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][0])
								+ "\n";
						str = str
								+ "Gyroscope y (rad/s): "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][1])
								+ "\n";
						str = str
								+ "Gyroscope z (rad/s): "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][2])
								+ "\n";

						break;
					case Sensor.TYPE_LIGHT:
						str = str
								+ "Light (lx): "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][0])
								+ "\n";
						break;
					case Sensor.TYPE_PRESSURE:
						str = str
								+ "Pressure (hPa): "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][0])
								+ "\n";
						break;
					case Sensor.TYPE_TEMPERATURE:
						str = str
								+ "Device Temperature (degree Celsius): "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][0])
								+ "\n";
						break;
					case Sensor.TYPE_PROXIMITY:
						str = str
								+ "Proximity (cm)"
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][0])
								+ "\n";
						break;

					case Sensor.TYPE_GRAVITY:
						str = str
								+ "Gravity x (m/s^2): "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][0])
								+ "\n";
						str = str
								+ "Gravity x (m/s^2): "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][1])
								+ "\n";
						str = str
								+ "Gravity x (m/s^2): "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][2])
								+ "\n";

						break;
					case Sensor.TYPE_LINEAR_ACCELERATION:
						str = str
								+ "Linear Accelerometer x (m/s^2): "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][0])
								+ "\n";
						str = str
								+ "Linear Accelerometer y (m/s^2): "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][1])
								+ "\n";
						str = str
								+ "Linear Accelerometer z (m/s^2): "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][2])
								+ "\n";

						break;
					case Sensor.TYPE_ROTATION_VECTOR:
						str = str
								+ "Rotation Vector x unitless: "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][0])
								+ "\n";
						str = str
								+ "Rotation Vector y unitless: "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][1])
								+ "\n";
						str = str
								+ "Rotation Vector z unitless: "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][2])
								+ "\n";
						if (Math.abs(SnapShotValue.instantValue[i][3] - 0) < 1.0e-15)
							str = str
									+ "Rotation Vector scalar:                NA\n";
						else
							str = str
									+ "Rotation Vector scalar: "
									+ String.format("%17.10f",
											SnapShotValue.instantValue[i][3])
									+ "\n";

						break;
					case 12: // Sensor.TYPE_RELATIVE_HUMIDITY
						str = str
								+ "Relative Humidity %: "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][0])
								+ "\n";
						break;
					case 13: // Sensor.TYPE_AMBIENT_TEMPERATURE
						str = str
								+ "Ambient air temperature (degree Celsius): "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][0])
								+ "\n";
						break;
					}
					str = str + "\n";
				}
			str = str
					+ "-----------------------------------------------------\n";
			output.write(str);
			output.close();
		} catch (Exception e) {
		}

	}

}