package winlab.SensorGUI;

import android.os.Bundle;
import android.widget.Toast;
import android.app.ExpandableListActivity;
//import android.view.View;
//import android.widget.CheckBox;
//import android.widget.ExpandableListView;
import java.util.ArrayList;

import winlab.sensoradventure.R;

//import android.util.Log;

public class SensorGUIActivity extends ExpandableListActivity {
	private SensorAdapter sensorAdapter;
	private ArrayList<Parent> parents = new ArrayList<Parent>();
	private ArrayList<Child> normalSensor = new ArrayList<Child>();
	private ArrayList<Child> micChild = new ArrayList<Child>();
	private boolean[] expanded = new boolean[100];

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.guimain);

		normalSensor.add(new Child("Sampling Rate", "Hz"));

		micChild.add(new Child("Sampling Rate", "Hz"));
		micChild.add(new Child("Buffer Size", ""));
		parents.add(new Parent("Gyroscope", normalSensor, false));
		parents.add(new Parent("Magnetic Field", normalSensor, false));
		parents.add(new Parent("Linear Acceleration", normalSensor, false));
		parents.add(new Parent("Microphone", micChild, false));

		sensorAdapter = new SensorAdapter(this, parents);
		setListAdapter(sensorAdapter);
		for (int i = 0; i < 100; i++) {
			SensorAdapter.value[i] = null;
			expanded[i] = false;
		}
	}

	public void onContentChanged() {
		super.onContentChanged();
	}

	public void onGroupExpand(int groupPosition) {
		update();
		expanded[groupPosition] = true;
	}

	public void onGroupCollapse(int groupPosition) {
		update();
		expanded[groupPosition] = false;
	}

	private void update() {
		for (int i = 0; i < parents.size(); i++)
			parents.get(i).setState(sensorAdapter.checkbox[i].isChecked());
		for (int i = 0; i < parents.size(); i++)
			if (expanded[i])
				for (int j = 0; j < sensorAdapter.getChildrenCount(i); j++)
					if (sensorAdapter.edittext[i + j] != null) {

						if (sensorAdapter.edittext[i + j].getText().toString()
								.length() == 0)
							SensorAdapter.value[i + j] = null;
						else
							SensorAdapter.value[i + j] = sensorAdapter.edittext[i
									+ j].getText().toString();
					}

	}
}