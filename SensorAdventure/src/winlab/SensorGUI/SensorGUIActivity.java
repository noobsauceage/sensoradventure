package winlab.SensorGUI;

/* To use this GUI, you need to make sure you have the following XML files:
 * guimain.xml
 * group_row.xml
 * child_row.xml
 * Currently, the GUI has strange problems when there are many groups
 * on the screen and throws NullPointerException randomly.
 * Also there appears to be a memory leak. These are all being looked into
 * currently.
 * DO NOT set the ELV's height to "wrap_content" unless you specify
 * the height of the parent/group. The program will appear as if
 * it is not working!
 */

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
	private boolean[] expanded;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.guimain);

		normalSensor.add(new Child("Sampling Rate", "Hz"));

		parents.add(new Parent("Gyroscope", normalSensor, false));
		parents.add(new Parent("Magnetic Field", normalSensor, false));
		parents.add(new Parent("Linear Acceleration", normalSensor, false));
		parents.add(new Parent("Microphone", normalSensor, false));

		sensorAdapter = new SensorAdapter(this, parents);
		expanded = new boolean[parents.size()];
		setListAdapter(sensorAdapter);
		for (int i = 0; i < parents.size(); i++) {
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
	
	public void update(){
		for (int i = 0; i < parents.size(); i++)
			parents.get(i).setState(sensorAdapter.checkbox[i].isChecked());
		for (int i = 0; i < parents.size(); i++)
			if (expanded[i]){
				for (int j = 0; j < parents.size(); j++)
					if (sensorAdapter.edittext[j] != null) {

						if (sensorAdapter.edittext[j].getText()
								.toString().length() == 0)
							SensorAdapter.value[j] = null;
						else
							SensorAdapter.value[j] = sensorAdapter.edittext[
									  j].getText().toString();
					}
			}
		
	}
}
