package winlab.SensorGUI;



import winlab.ASL.AndroidSensors;
import winlab.file.RunningService;
import winlab.file.SensorSetting;
import winlab.sensoradventure.ContinuousRecorder;
import winlab.sensoradventure.R;
import winlab.sensoradventure.SensorAdventureActivity;
import winlab.sql.Sensors_SQLite_Service;
import winlab.sql.SnapShot_SQL;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
	private boolean flag = true; // check whether "stop" button is ever pushed
	private boolean flag3 = true; // check whether "start" button is ever pushed
	private boolean[] state; // Which data configuration settings are checked
								// "On"
	private boolean[] sensorCheck; // Which sensors are checked "On" in main
									// activity
	private Button mark, startAndStop, snapshot;
	private EditText editText1, editText2;
	private Chronometer mChronometer;
	private LinearLayout markTimes;
	private LinearLayout runningSensors;
	private String[] Sensors = { "Accelerometer ", "Magnetic ", "Orientation ",
			"Gyroscope ", "Light ", "Pressure ", "Temperature ", "Proximity ",
			"Gravity ", "L. Accelerometer ", "Rotation ", "Humidity ",
			"A. Temperature ", "Microphone ", "GPS " };
	private int[] rates;
	private int micchanneli, micchannelo, micencode, micsampling;
	private long rate = 100;// add in ms
	private long duration = 5; // add in s
	private SnapShot_SQL data2;
	private ContinuousRecorder record;
	private AndroidSensors androidSensors;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		androidSensors = new AndroidSensors(this);
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
		androidSensors.configureMicrophone(1, micsampling, micchanneli,
				micchannelo, micencode, 3, 1);

		// Print which sensors are on to the screen
		for (int i = 0; i < sensorCheck.length; i++)
			if (sensorCheck[i]) {
				TextView tv = new TextView(this);
				tv.setText(Sensors[i]);
				runningSensors.addView(tv);
			}

		// If 'Write to File' is checked, use the RunningService to write to
		// a file
		if (state[0])
			// Pass update rates to the sensors
			SensorSetting.setRate(rates);

		androidSensors.batchTest(sensorCheck, state, rates);
		androidSensors.prepareForLogging();

	}

	private OnClickListener startClick = new OnClickListener() {
		public void onClick(View a) {
			flag3 = false; // "start" button gets pushed.
			// Prepare the timer
			mChronometer.setBase(SystemClock.elapsedRealtime());
			mChronometer.start();

			androidSensors.startLogging();

			// Switch the 'Start' button to a 'Stop' button
			startAndStop.setOnClickListener(stopClick);
			startAndStop.setText("Stop");
			mark.setEnabled(true);
			snapshot.setEnabled(true);
		}
	};

	private OnClickListener markClick = new OnClickListener() {

		public void onClick(View a) {

			androidSensors.markEvent();

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
			androidSensors.stopLogging();

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

			androidSensors.performSnapshot(
					Long.parseLong(editText2.getText().toString()),
					Long.parseLong(editText1.getText().toString()));

		}
	};

	public void onDestroy() {
		// If both "stop" and "start" haven't been pushed
		if (flag && flag3 && state[1])
			try {
				data2.close();
			} catch (Exception e) {
			}
		// If the 'Stop' button has not been pushed and "Start" button has been
		// pushed
		if (flag && (flag3 == false)) {
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
				if (sensorCheck[13]) {
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

}