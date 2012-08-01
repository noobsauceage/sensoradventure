/* This is a helper class that communicates with the SensorManager.
 * It is used to determine which sensors are available on the phone,
 * and to set the individual update rates for all 13 of the SensorManager
 * sensors.
 */

package winlab.file;

//import java.io.File;

import android.content.Context;
import android.hardware.SensorManager;

public class SensorSetting {

	private SensorManager mSensorManager;

	private Context context;

	public static boolean sensors[] = { true, true, true, true, true, true,
			true, true, true, true, true, true, true };
	public static int updateRate[] = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
	// public static File path;
	public static boolean available_sensors[] = { true, true, true, true, true,
			true, true, true, true, true, true, true, true };

	// Test to see which sensors exist on the phone
	public void testAvailableSensors() {

		mSensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		for (int i = 1; i <= 13; i++)
			// If a phone does not exist, SensorManager returns 'null'
			if (mSensorManager.getDefaultSensor(i) == null)
				sensors[i - 1] = false;
			// Any other case, the sensor must exist on phone
			else
				sensors[i - 1] = true;

		// Extraneous
		for (int i = 0; i < 13; i++)
			available_sensors[i] = sensors[i];

	}

	public static void setRate(int[] Rate) {
		for (int i = 0; i < 13; i++)
			updateRate[i] = Rate[i];
	}

	public SensorSetting(Context con) {
		this.context = con;

	}

	// This is used in GUI. If the sensor is selected there
	// we map it over to here.
	public void selectSensors(boolean selected[]) {
		for (int i = 0; i < 13; i++)
			sensors[i] = selected[i];
	}

}
