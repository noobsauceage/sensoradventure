package winlab.SensorGUI;

import winlab.file.RunningService;
import winlab.file.SensorSetting;
import winlab.file.SnapShotValue;
import winlab.sensoradventure.ContinuousRecorder;
import winlab.sensoradventure.R;
import winlab.sql.Sensors_SQLite_Service;
import winlab.sql.Sensors_SQLite_Setting;
import winlab.sql.SnapShot_SQL;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class StartGUI extends Activity implements OnClickListener {
	private boolean flag = true;
	private Chronometer mChronometer;
	private Button mark, stop;
	private String[] times = new String[10];
	private LinearLayout ll1;
	private LinearLayout ll2;
	private SensorSetting ok;
	private Sensors_SQLite_Setting ok2;
	private SnapShot_SQL data2;
	private boolean[] state;
	private boolean[] sensorCheck;
	private String[] Sensors;
	private ContinuousRecorder record;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.start_gui);

		mChronometer = (Chronometer) findViewById(R.id.chronometer);

		mark = (Button) findViewById(R.id.button1);
		mark.setOnClickListener(this);
		stop = (Button) findViewById(R.id.button2);
		stop.setOnClickListener(this);

		ll1 = (LinearLayout) findViewById(R.id.layout1);
		ll2 = (LinearLayout) findViewById(R.id.layout2);
		record = new ContinuousRecorder(StartGUI.this);

		for (int i = 0; i < 10; i++)
			times[i] = "";
		mChronometer.setBase(SystemClock.elapsedRealtime());
		mChronometer.start();

		// Retrieve data on which sensors are on
		// And what upload options exist
		Bundle extras = getIntent().getExtras();
		sensorCheck = extras.getBooleanArray("sensorCheck");
		state = extras.getBooleanArray("state");
		Sensors = extras.getStringArray("Sensors");
		ok = new SensorSetting(this);
		ok2 = new Sensors_SQLite_Setting(this);
		ok.selectSensors(sensorCheck);
		ok2.selectSensors(sensorCheck);

		SnapShotValue.set();
		if (state[0]) {
			startService(new Intent(this, RunningService.class));
			if (sensorCheck[13]) {
				record.record();
			}
		}

		if (state[1]) {
			data2 = new SnapShot_SQL(this);
			data2.open();
			data2.deleteTable();
			data2.prepareTransaction();
			startService(new Intent(this, Sensors_SQLite_Service.class));

			// Print which sensors are on to the screen
			for (int i = 0; i < sensorCheck.length; i++) {
				if (sensorCheck[i]) {
					TextView tv = new TextView(this);
					tv.setText(Sensors[i]);
					ll2.addView(tv);
				}

			}
		}
	}

	public void onClick(View a) {
		switch (a.getId()) {
		case R.id.button1:
			if (state[0])
				SnapShotValue.print();
			if (state[1])
				try {
					SnapShotValue.insertSQL(data2);
				} catch (Exception e) {
					Toast.makeText(this, "1", Toast.LENGTH_LONG).show();
				}
			TextView tv = new TextView(this);
			tv.setText(mChronometer.getInstantTime());
			ll1.addView(tv);
			break;

		case R.id.button2:
			flag = false;
			if (state[0])
				stopService(new Intent(this, RunningService.class));
			if (state[1]) {
				try {
					data2.endTransaction();

					data2.close();
				} catch (Exception e) {
					Toast.makeText(this, "2", Toast.LENGTH_LONG).show();
				}
				stopService(new Intent(this, Sensors_SQLite_Service.class));

				if (sensorCheck[13])
					record.stop();
				record.cancel();

			}
			mChronometer.stop();
			break;
		}
	}

	public void onDestroy() {

		if (flag) {
			if (state[0])
				stopService(new Intent(this, RunningService.class));
			if (state[1]) {
				try {
					data2.endTransaction();
					data2.close();
				} catch (Exception e) {
				}
				stopService(new Intent(this, Sensors_SQLite_Service.class));
			}
			mChronometer.stop();
		}
		super.onDestroy();
	}
}