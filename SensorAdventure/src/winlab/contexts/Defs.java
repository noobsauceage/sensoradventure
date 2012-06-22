package winlab.contexts;

import android.location.Location;

public class Defs {

	public final static int ACC_MOVE_WALK = 1;
	public final static int ACC_MOVE_RUN = 5;
	public final static int PROX_FAR = Integer.MAX_VALUE;
	public final static int PROX_CLOSE = 3;
	public final static int LIGHT_LIGHT = 10;
	public final static int LIGHT_DARK = 100;

	public int getLight() {
		// TODO
		return 0;
	}

	public int getProximity() {
		// TODO
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
		return isOnRoad() && isCarMoving(location);
	}

	public boolean isCarMoving(Location location) {
		// Realistically, it must be > than some tolerance
		return location.getSpeed() > 0;
	}

	public boolean isOnRoad() {
		// TO DO.....
		return true;
	}

}