/**
 * 
 */
package winlab.sensoradventure.gps;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.telephony.TelephonyManager;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;

/**
 * This is the GPSloggerServicedb just for database, The Provider is set to
 * GPS_PROVIDER with 1 minute default logging Once the UI is finalized we will
 * combine the program with the db logging TIMER_DELAY is set to 1 minute
 * default, If you want to change the default you can change it here
 */
public class GPSloggerServicedb extends Service implements LocationListener {

	Location location;
	private static final int gpsMinTime = 0;
	private static final int gpsMinDistance = 0;
	private static final int TIMER_DELAY = 1000;
	private static final int GEOCODER_MAX_RESULTS = 5;

	private LocationManager manager = null;
	private double latitude = 0.0;
	private double longitude = 0.0;
	private double altitude = 0.0;
	private double bearing = 0.0;
	private double accuracy = 0.0;
	private String provider = null;
	private Timer monitoringTimer = null;
	private GPSLoggerSQLite data;

	public GPSloggerServicedb() {
		AppLog.logString("GPSloggerServicedb.GPSloggerServicedb().");
	}

	@Override
	public IBinder onBind(Intent arg0) {
		AppLog.logString("GPSloggerServicedb.onBind().");
		return null;
	}

	@Override
	public void onCreate() {
		AppLog.logString("GPSloggerServicedb.onCreate().");
		data = new GPSLoggerSQLite(this);

		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		AppLog.logString("GPSloggerServicedb.onStart().");
		startLoggingService();
		startMonitoringTimer();
		super.onStart(intent, startId);
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		AppLog.logString("GPSloggerServicedb.onStartCommand().");

		startLoggingService();
		startMonitoringTimer();

		return Service.START_STICKY;
	}

	public void onLocationChanged(Location location) {
		updateChanged(location);

	}

	/**
	 * @param location
	 *            , Whenever there is a change noted, it will get 5 parameters
	 *            from the location object, that is latitude,longitude,
	 *            altitide,bearing,accuracy. If in the future we require more
	 *            elements to be retrived from location field, make the change
	 *            here
	 */
	public void updateChanged(Location location) {
		AppLog.logString("GPSloggerServicedb.onLocationChanged().");
		latitude = location.getLatitude();
		longitude = location.getLongitude();
		altitude = location.getAltitude();
		bearing = location.getBearing();
		accuracy = location.getAccuracy();
	}

	public void onProviderDisabled(String provider) {
		AppLog.logString("GPSloggerServicedb.onProviderDisabled().");
	}

	public void onProviderEnabled(String provider) {
		AppLog.logString("GPSloggerServicedb.onProviderEnabled().");
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		AppLog.logString("GPSloggerServicedb.onStatusChanged().");
	}

	/**
	 * This is where we can default to GPS_PROVIDER, once the Main UI is
	 * finalized we are going to check for Network Provider or Gps Provider
	 */
	public void startLoggingService() {
		manager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		AppLog.logString(LocationManager.GPS_PROVIDER);
		provider = "gps";
	}

	private void stopLoggingService() {
		stopSelf();
	}

	private void startMonitoringTimer() {
		monitoringTimer = new Timer();
		monitoringTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (latitude != 0.0 && longitude != 0.0) {
					monitoringTimer.cancel();
					monitoringTimer = null;

					manager.removeUpdates(GPSloggerServicedb.this);

					saveCoordinates(latitude, longitude, altitude, bearing,
							accuracy, provider);
					stopLoggingService();
				}
			}
		}, GPSloggerServicedb.TIMER_DELAY, GPSloggerServicedb.TIMER_DELAY);

	}

	private String getLocationName(double latitude, double longitude) {
		String name = "";
		Geocoder geocoder = new Geocoder(this);

		try {
			List<Address> address = geocoder.getFromLocation(latitude,
					longitude, GPSloggerServicedb.GEOCODER_MAX_RESULTS);

			name = address.get(0).toString();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return name;
	}

	private void saveCoordinates(double latitude, double longitude,
			double altitude, double bearing, double accuracy, String provider) {
		AppLog.logString("GPSloggerService.onProviderEnabled().");
		TelephonyManager TelephonyMgr1 = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		String device_id = TelephonyMgr1.getDeviceId();
		data.open();
		long currentDeviceTime = Calendar.getInstance().getTimeInMillis();
		data.insertgpsrow(currentDeviceTime, device_id, latitude, longitude,
				altitude, bearing, accuracy, provider, 1);
		String result = "device_id(IMEI)       timestamp (ms)            LAT (degrees)            LONG (degrees)          ALT (degrees)        BEARING         ACCURACY     PROVIDER \n";
		Cursor c = data.getAllgpsdata(1);
		if (c.moveToFirst()) {

			do {
				result = result + c.getString(1) + "    " + c.getString(2)
						+ "    " + c.getString(3) + "      " + c.getString(4)
						+ "      " + c.getString(5) + "           "
						+ c.getString(6) + "\n";
			} while ((c.moveToNext()));

		}
		AppLog.logString("result" + result);
		data.close();

	}
}