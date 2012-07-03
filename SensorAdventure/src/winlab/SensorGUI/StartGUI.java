package winlab.SensorGUI;

import winlab.sensoradventure.R;
import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class StartGUI extends Activity implements OnClickListener {
	private Chronometer mChronometer;
	private Button mark, stop;
	private String[] times = new String[10];
	private LinearLayout ll1;
	private LinearLayout ll2;

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

		for (int i = 0; i < 10; i++)
			times[i] = "";
		mChronometer.setBase(SystemClock.elapsedRealtime());
		mChronometer.start();

		// Retrieve data on which sensors are on
		// And what upload options exist
		Bundle extras = getIntent().getExtras();
		boolean[] sensorCheck = extras.getBooleanArray("sensorCheck");
		boolean[] state = extras.getBooleanArray("state");
		String[] Sensors = extras.getStringArray("Sensors");
		

		// Print which sensors are on to the screen
		for (int i = 0; i < sensorCheck.length; i++) {
			if (sensorCheck[i]) {
				TextView tv = new TextView(this);
				tv.setText(Sensors[i]);
				ll2.addView(tv);
			}

		}

	}

	public void onClick(View a) {
		switch (a.getId()) {
		case R.id.button1:
			TextView tv = new TextView(this);
			tv.setText(mChronometer.getInstantTime());
			ll1.addView(tv);
			break;

		case R.id.button2:
			mChronometer.stop();
			break;
		}
	}
}