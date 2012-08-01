package winlab.SensorGUI;

/* This is a simple CheckBox GUI for the data configuration options.
 * It retains CheckBox memory via the static boolean array state[].
 * 
 * Written by G.D.C.
 */

import winlab.sensoradventure.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class OptionsGUI extends Activity {
	public CheckBox cb[] = null; // Array to hold the three CBs
	public static boolean state[] = null; // Static boolean array to retain
											// state
											// will not be destroyed when
											// activity is exited.

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.options_main);

		// Initialize cb
		cb = new CheckBox[3];

		// Populate cb
		cb[0] = (CheckBox) findViewById(R.id.checkBox1);
		cb[1] = (CheckBox) findViewById(R.id.checkBox2);
		cb[2] = (CheckBox) findViewById(R.id.checkBox3);

		for (int i = 0; i < 3; i++) {
			cb[i].setOnCheckedChangeListener(checked);
			// Check to see if the state variable exists
			// If so, place the previous states inside of it
			// If not, initialize to false
			if (state == null)
				cb[i].setChecked(false);
			else
				cb[i].setChecked(state[i]);
		}

	}

	// Listener for when a checkbox is clicked
	OnCheckedChangeListener checked = new OnCheckedChangeListener() {

		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			// Create state array if it does not exist
			if (state == null) {
				state = new boolean[3];
			}
			// Change the state variable to match what the user does
			for (int i = 0; i < cb.length; i++) {
				if (cb[i] == arg0) {
					arg0.setChecked(arg1);
					state[i] = arg1;
				}
			}

		}

	};

}
