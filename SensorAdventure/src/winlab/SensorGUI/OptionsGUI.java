package winlab.SensorGUI;

import winlab.sensoradventure.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class OptionsGUI extends Activity {
	public CheckBox cb[] = null;
	public static boolean state[] = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.options_main);

		cb = new CheckBox[3];

		cb[0] = (CheckBox) findViewById(R.id.checkBox1);
		cb[1] = (CheckBox) findViewById(R.id.checkBox2);
		cb[2] = (CheckBox) findViewById(R.id.checkBox3);

		for (int i = 0; i < 3; i++) {
			cb[i].setOnCheckedChangeListener(checked);
			if (state == null)
				cb[i].setChecked(false);
			else
				cb[i].setChecked(state[i]);
		}

	}

	OnCheckedChangeListener checked = new OnCheckedChangeListener() {

		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			if (state == null) {
				state = new boolean[3];
			}
			for (int i = 0; i < cb.length; i++) {
				if (cb[i] == arg0) {
					arg0.setChecked(arg1);
					state[i] = arg1;
				}
			}

		}

	};

}
