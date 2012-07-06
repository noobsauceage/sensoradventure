package winlab.contexts;

import winlab.file.SnapShotValue;
import android.content.Context;
import android.hardware.Sensor;
import android.location.Location;

public class Defs {

	Context context;

	public Defs(Context context) {
		this.context = context;
	}


	/*
	 * For now, the getters will just return the instant values However, later
	 * on, I guess you can specify a time and then look through the archived
	 * data (Pass on arg of time... time = 0 means get instVal)
	 */
	// Level 0 Contexts
	// Dictionary of sensor names to sensor type number
	// For the sensors not already in the Android Sensor Mngr
	public static final int MIC = 21;
	public static final int GPS = 22;
	public static final int BLUETOOTH = 23;
	
	public double[] getAccelerometer() {
		return SnapShotValue.getInstVal(Sensor.TYPE_ACCELEROMETER);
	}

	public double[] getMagnetic() {
		return SnapShotValue.getInstVal(Sensor.TYPE_MAGNETIC_FIELD);
	}

	public double[] getOrientation() {
		return SnapShotValue.getInstVal(Sensor.TYPE_ORIENTATION);
	}

	public double[] getGyroscope() {
		return SnapShotValue.getInstVal(Sensor.TYPE_GYROSCOPE);
	}

	public double[] getLight() {
		return SnapShotValue.getInstVal(Sensor.TYPE_LIGHT);
	}

	public double[] getPressure() {
		return SnapShotValue.getInstVal(Sensor.TYPE_PRESSURE);
	}

	public double[] getTemperature() {
		return SnapShotValue.getInstVal(Sensor.TYPE_TEMPERATURE);
	}

	public double[] getProximity() {
		return SnapShotValue.getInstVal(Sensor.TYPE_PROXIMITY);
	}

	public double[] getGravity() {
		return SnapShotValue.getInstVal(Sensor.TYPE_GRAVITY);
	}

	public double[] getLinear_Accelerator() {
		return SnapShotValue.getInstVal(Sensor.TYPE_LINEAR_ACCELERATION);
	}

	public double[] getLinear_Rotation() {
		return SnapShotValue.getInstVal(Sensor.TYPE_ROTATION_VECTOR);
	}

	public double[] getMic() {
		return SnapShotValue.getInstVal(MIC);
	}

	public double[] getGPS() {
		return SnapShotValue.getInstVal(GPS);
	}

	public double[] getBluetooth() {
		return SnapShotValue.getInstVal(BLUETOOTH);
	}
	
	
	// Level 1 Contexts
	// Thresholds to define human readable quantities for each sensor
	public final static double ACC_MOVE_WALK = 1;
	public final static double ACC_MOVE_RUN = 5;
	public final static double MAG_WEAK = 150;
	public final static double MAG_STRONG = 600;
	public final static double ORI_NORTH = 0;
	public final static double ORI_EAST = 90;
	public final static double ORI_SOUTH = 180;
	public final static double ORI_WEST = 270;
	public final static double ORI_FLAT = 0;
	public final static double ORI_RIGHTSIDE_UP = -90;
	public final static double ORI_UPSIDE_DOWN = 90;
	public final static double LIGHT_LIGHT = 10;
	public final static double LIGHT_DARK = 100;
	public final static double PROX_FAR = Integer.MAX_VALUE;
	public final static double PROX_CLOSE = 3;
	
	
	public final static double DRIVNG_SPEED_TOL = 10;
	
	public boolean isDark() {
		return getLight()[0] >= LIGHT_DARK;
	}

	public boolean isClose() {
		return getProximity()[0] <= PROX_CLOSE;
	}


	public boolean isDriving(Location location) {
		// Realistically, it must be > than some tolerance
		return location.getSpeed() > DRIVNG_SPEED_TOL;
	}

}