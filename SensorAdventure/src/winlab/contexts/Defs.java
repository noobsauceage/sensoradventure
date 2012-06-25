package winlab.contexts;

import android.content.Context;
import android.hardware.Sensor;
import android.location.Location;

import winlab.sensoradventure.SensorMonitor;

public class Defs {

	Context context;

	public Defs(Context context) {
		this.context = context;
	}

	public final static int ACC_MOVE_WALK = 1;
	public final static int ACC_MOVE_RUN = 5;
	public final static int PROX_FAR = Integer.MAX_VALUE;
	public final static int PROX_CLOSE = 3;
	public final static int LIGHT_LIGHT = 10;
	public final static int LIGHT_DARK = 100;

	public int getLight() {
		// TODO
		/*
		 * SensorManager sensorManager = (SensorManager) context
		 * .getSystemService(Context.SENSOR_SERVICE); Sensor lightSensor =
		 * sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
		 * 
		 * if (lightSensor != null) {
		 * sensorManager.registerListener((SensorEventListener) this,
		 * lightSensor, SensorManager.SENSOR_DELAY_NORMAL); }
		 */
		Sensor lightSensor = SensorMonitor.sensors.get(5);
		return 0;
	}

	public int getProximity() {
		// TODO
		Sensor proxSensor = SensorMonitor.sensors.get(8);
		
		return 0;
	}

	public boolean isInPocket() {
		return isDark() && isClose();
	}

	public boolean isDark() {
		return getLight() >= LIGHT_DARK;
	}

	public boolean isClose() {
		return getProximity() <= PROX_CLOSE;
	}

	public boolean isDriving(Location location) {
		// Realistically, it must be > than some tolerance
		return location.getSpeed() > 0;
	}

}