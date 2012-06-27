package winlab.sensoradventure;

import java.util.ArrayList;

import winlab.SensorGUI.Child;
import winlab.SensorGUI.Parent;
import winlab.SensorGUI.SensorAdapter;
import android.app.Activity;
import android.app.ExpandableListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SensorAdventureActivity extends ExpandableListActivity {
	private SensorAdapter sensorAdapter;
	private ArrayList<Parent> parents = new ArrayList<Parent>();
	private ArrayList<Child> normalSensor = new ArrayList<Child>();
	private ArrayList<Child> micChild = new ArrayList<Child>();
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
			parents.add(new Parent(Sensors[i], normalSensor, false));

		sensorAdapter = new SensorAdapter(this, parents);
		expanded = new boolean[parents.size()];
		setListAdapter(sensorAdapter);
		for (int i = 0; i < parents.size(); i++) {
			sensorAdapter.value[i] = null;
			expanded[i] = false;
		}
	}

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
			Toast.makeText(this, "Pushed", Toast.LENGTH_LONG).show();
			break;
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
		for (int i = 0; i < parents.size(); i++) {
			parents.get(i).setState(sensorAdapter.checkbox[i].isChecked());
			if (expanded[i]) {
				for (int j = 0; j < parents.size(); j++)
					if (sensorAdapter.edittext[j] != null) {

						if (sensorAdapter.edittext[j].getText().toString()
								.length() == 0)
							sensorAdapter.value[j] = null;
						else
							sensorAdapter.value[j] = sensorAdapter.edittext[j]
									.getText().toString();
					}
			}
		}

	}
}
