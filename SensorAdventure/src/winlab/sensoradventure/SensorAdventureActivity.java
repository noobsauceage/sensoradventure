package winlab.sensoradventure;

/* This is the main activity for the Sensor Adventure application.
 * Errors with EditText scrolling lost have been (conceivably) fixed.
 * As far as the GUI goes, main bugs have been fixed. Any additional edits
 * to its controls should be to optimize efficiency.
 * -- G.D.C.
 */

import java.util.ArrayList;

 
import winlab.SensorGUI.*;
import winlab.SensorGUI.Child;
import winlab.SensorGUI.Group;
import winlab.SensorGUI.SensorAdapter;
import android.app.ExpandableListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SensorAdventureActivity extends ExpandableListActivity {
	private SensorAdapter sensorAdapter;
	private Button start, config;
	private ArrayList<Group> groups = new ArrayList<Group>();
	private ArrayList<Child> normalSensor = new ArrayList<Child>();
	private boolean[] expanded;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.guimain);
		String[] Sensors = { "Accelerometer", "Magnetic", "Orientation",
				"Gyroscope", "Light", "Pressure", "Temperature", "Proximity",
				"Gravity", "L. Accelerometer", "Rotation", "Humidity",
				"A. Temperature", "Microphone", "GPS" };

		normalSensor.add(new Child("Sampling Rate", "Hz"));

		for (int i = 0; i < Sensors.length; i++)
			groups.add(new Group(Sensors[i], normalSensor, false));

		sensorAdapter = new SensorAdapter(this, groups);
		expanded = new boolean[groups.size()];
		setListAdapter(sensorAdapter);
		for (int i = 0; i < groups.size(); i++) {
			SensorAdapter.value[i] = null;
			expanded[i] = false;
		}
		
		start = (Button)findViewById(R.id.button1);
		start.setOnClickListener(startClick);
		config = (Button)findViewById(R.id.button2);
		config.setOnClickListener(configClick);
	}
	
	private OnClickListener startClick = new OnClickListener() {
		public void onClick(View v) {
			Intent intent  = new Intent( SensorAdventureActivity.this,  ControlSensorSQLthroughFileActivity.class);
			startActivity(intent); 
			
			

		}
	};
	
	private OnClickListener configClick = new OnClickListener() {
		public void onClick(View v) {
			// This should go to a new activity.

		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.settings:
			/*
			// This should go to a new activity.
			Toast.makeText(this, "Pushed", Toast.LENGTH_LONG).show();
			break;*/
			Intent intent  = new Intent( SensorAdventureActivity.this,  AdvanceSettingsGUI.class);
			startActivity(intent); 
		}
		return true;
	}

	public void onContentChanged() {
		super.onContentChanged();
		update();
	}

	public void onGroupExpand(int groupPosition) {
		update();
		expanded[groupPosition] = true;
	}

	public void onGroupCollapse(int groupPosition) {
		update();
		expanded[groupPosition] = false;
	}
	


	public void update() {
		for (int i = 0; i < groups.size(); i++) {
			groups.get(i).setState(sensorAdapter.checkbox[i].isChecked());
			if (expanded[i]) {
				for (int j = 0; j < groups.size(); j++)
					if (sensorAdapter.edittext[j] != null) {

						if (sensorAdapter.edittext[j].getText().toString()
								.length() == 0)
							SensorAdapter.value[j] = null;
						else
							SensorAdapter.value[j] = sensorAdapter.edittext[j]
									.getText().toString();
					}
			}
		}

	}
}
