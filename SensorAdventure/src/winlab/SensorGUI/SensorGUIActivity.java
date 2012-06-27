package winlab.SensorGUI;

/* To use this GUI, you need to make sure you have the following XML files:
 * guimain.xml
 * group_row.xml
 * child_row.xml
 * DO NOT set the ELV's height to "wrap_content" unless you specify
 * the height of the parent/group. The program will appear as if
 * it is not working!
 * NullPointerException has been fixed.
 */

import java.util.ArrayList;

import android.app.ExpandableListActivity;
import android.os.Bundle;

public class SensorGUIActivity extends ExpandableListActivity {
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
		String[] Sensors = {"Accelerometer",
				"Magnetic", "Orientation", "Gyroscope",
				"Light", "Pressure", "Temperature",
				"Proximity", "Gravity", "L. Accelerometer",
				"Rotation", "Humidity",
				"A. Temperature", "Microphone" };

		normalSensor.add(new Child("Sampling Rate", "Hz"));

		for(int i = 0; i< Sensors.length;i++)
		parents.add(new Parent(Sensors[i], normalSensor, false));

		sensorAdapter = new SensorAdapter(this, parents);
		expanded = new boolean[parents.size()];
		setListAdapter(sensorAdapter);
		for (int i = 0; i < parents.size(); i++) {
			sensorAdapter.value[i] = null;
			expanded[i] = false;
		}
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
		for (int i = 0; i < parents.size(); i++){
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
