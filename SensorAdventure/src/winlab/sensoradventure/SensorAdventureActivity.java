package winlab.sensoradventure;

/* This is the main activity for the Sensor Adventure application.
 * It is currently in a mode operational enough for implementation with other code.
 * One issue that it has is when text is entered into the EditText field
 * and some part of the screen (user leaves the cursor dangling
 * on the typed text and proceeds to scroll off screen), the data is lost.
 * The EditText data is saved if the user clicks something else after typing.
 * Any solution to this problem is a welcome sight, but it is fine for now as
 * far as testing goes.
 * -- G.D.C.
 */

import java.util.ArrayList;

import winlab.SensorGUI.Child;
import winlab.SensorGUI.SensorAdapter;
import winlab.SensorGUI.Group;
import android.app.ExpandableListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class SensorAdventureActivity extends ExpandableListActivity {
	private SensorAdapter sensorAdapter;
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
