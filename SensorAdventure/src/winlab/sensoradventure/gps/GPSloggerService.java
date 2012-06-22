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
 * This is the GPSloggerService just for Files, The Provider is set to
 * GPS_PROVIDER with 1 minute default logging Once the UI is finalized we will
 * combine the program with the db logging TIMER_DELAY is set to 1 minute
 * default, If you want to change the default you can change it here
 */
public class GPSloggerService extends Service implements LocationListener {

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

	public GPSloggerService() {
		AppLog.logString("GPSloggerService.GPSloggerService().");
	}

	@Override
	public IBinder onBind(Intent arg0) {
		AppLog.logString("GPSloggerService.onBind().");
		return null;
	}

	@Override
	public void onCreate() {
		AppLog.logString("GPSloggerService.onCreate().");
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		AppLog.logString("GPSloggerService.onStart().");
		startLoggingService();
		startMonitoringTimer();
		super.onStart(intent, startId);
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		AppLog.logString("GPSloggerService.onStartCommand().");
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
		AppLog.logString("GPSloggerService.onLocationChanged().");
		latitude = location.getLatitude();
		longitude = location.getLongitude();
		altitude = location.getAltitude();
		bearing = location.getBearing();
		accuracy = location.getAccuracy();
	}

	public void onProviderDisabled(String provider) {
		AppLog.logString("GPSloggerService.onProviderDisabled().");
	}

	public void onProviderEnabled(String provider) {
		AppLog.logString("GPSloggerService.onProviderEnabled().");
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		AppLog.logString("GPSloggerService.onStatusChanged().");
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

	/**
	 * This is main part, where the Monitoring occurs , the logging is scheduled
	 * at a fixed rate , and depends on the value of
	 * GPSloggerService.TIMER_DELAY
	 */
	private void startMonitoringTimer() {
		monitoringTimer = new Timer();
		monitoringTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				AppLog.logString("latitude " + latitude + " longitude"
						+ longitude);
				if (latitude != 0.0 && longitude != 0.0) {
					monitoringTimer.cancel();
					monitoringTimer = null;

					manager.removeUpdates(GPSloggerService.this);

					saveCoordinates(latitude, longitude, altitude, bearing,
							accuracy, provider);
					stopLoggingService();
				}
			}
		}, GPSloggerService.TIMER_DELAY, GPSloggerService.TIMER_DELAY);
	}

	private String getLocationName(double latitude, double longitude) {
		String name = "";
		Geocoder geocoder = new Geocoder(this);

		try {
			List<Address> address = geocoder.getFromLocation(latitude,
					longitude, GPSloggerService.GEOCODER_MAX_RESULTS);

			name = address.get(0).toString();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return name;
	}

	/**
	 * @param latitude
	 * @param longitude
	 * @param altitude
	 * @param bearing
	 * @param accuracy
	 * @param provider
	 *            These are all the parameters to this method, this method is
	 *            called when the latitude and longitude are not zeroes, The
	 *            Folder where the file is called GPSLogger, and the file is
	 *            GPSlogfile.txt.
	 */
	private void saveCoordinates(double latitude, double longitude,
			double altitude, double bearing, double accuracy, String provider) {
		AppLog.logString("GPSloggerService.onProviderEnabled().");
		File folder = new File(Environment.getExternalStorageDirectory(),
				"GPSLogger");

		boolean isNew = false;
		if (!folder.exists()) {
			AppLog.logString("Folder does not exist"
					+ Environment.getExternalStorageDirectory());
			folder.mkdirs();
			isNew = true;
		}
		try {
			File kmlFile = new File(folder.getPath(), "GPSlogfile.txt");
			if (!kmlFile.exists()) {
				kmlFile.createNewFile();
				isNew = true;
			}

			if (isNew) {
				FileOutputStream initialWriter = new FileOutputStream(kmlFile,
						true);

				TelephonyManager TelephonyMgr1 = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
				String xml1 = " DEVICE ID " + TelephonyMgr1.getDeviceId();
				initialWriter.write(xml1.getBytes());
				String xml2 = "                                                    ";
				initialWriter.write(xml2.getBytes());
				String xml = "   TIME   ,   LAT    ,    LONG     ,    ALT   ,   BEARING   , ACCURACY, PROVIDER                     ";
				initialWriter.write(xml.getBytes());
				initialWriter.flush();
				initialWriter.close();

			}

			SimpleDateFormat sdf = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ssZ");
			String dateString = sdf.format(new Date());
			long currentDeviceTime = Calendar.getInstance().getTimeInMillis();
			String placemark = currentDeviceTime + ","
					+ String.valueOf(longitude) + ","
					+ String.valueOf(latitude) + "," + bearing + ","
					+ String.valueOf(accuracy) + "," + provider;

			RandomAccessFile fileAccess = new RandomAccessFile(kmlFile, "rw");
			FileLock lock = fileAccess.getChannel().lock();

			fileAccess.seek(kmlFile.length());
			fileAccess.write(placemark.getBytes());

			lock.release();
			fileAccess.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}