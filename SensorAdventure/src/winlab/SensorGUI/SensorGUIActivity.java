package winlab.SensorGUI;

import winlab.sensoradventure.R;

import android.os.Bundle;
import android.app.ExpandableListActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import java.util.ArrayList;
import android.util.Log;

public class SensorGUIActivity extends ExpandableListActivity {
	private SensorAdapter sensorAdapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.guimain);

		ArrayList<Parent> parents = new ArrayList<Parent>();
		ArrayList<Child> normalSensor = new ArrayList<Child>();
		normalSensor.add(new Child("Sampling Rate", "Hz"));
		ArrayList<Child> micChild = new ArrayList<Child>();
		micChild.add(new Child("Sampling Rate", "Hz"));
		micChild.add(new Child("Buffer Size", ""));
		parents.add(new Parent("Gyroscope", normalSensor, true));
		parents.add(new Parent("Microphone", micChild, false));

		sensorAdapter = new SensorAdapter(this, parents);
		setListAdapter(sensorAdapter);

	}

	public void onContentChanged() {
		super.onContentChanged();
	}

	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		CheckBox cb = (CheckBox) v.findViewById(R.id.checkBox1);
		cb.toggle();

		return false;
	}

}
