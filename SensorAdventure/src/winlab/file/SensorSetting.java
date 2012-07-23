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
	//public static File path;
    public static boolean available_sensors[]={ true, true, true, true, true, true,
		true, true, true, true, true, true, true };
	public void testAvailableSensors() {

		mSensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		for (int i = 1; i <= 13; i++)
			if (mSensorManager.getDefaultSensor(i) == null)
				sensors[i - 1] = false;
			else sensors[i-1]=true;
		for (int i=0; i<13; i++)
			available_sensors[i]=sensors[i];

	}

	public static void setRate(int[] Rate) {
		for (int i = 0; i < 13; i++)
			updateRate[i] = Rate[i];
	}

	public SensorSetting(Context con) {
		this.context = con;

	}

	public void selectSensors(boolean selected[]) {
		for (int i = 0; i < 13; i++)
			sensors[i] = selected[i];
	}

}
