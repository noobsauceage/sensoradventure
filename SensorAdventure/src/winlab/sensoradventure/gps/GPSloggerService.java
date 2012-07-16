package winlab.sensoradventure.gps;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import winlab.file.SnapShotValue;
 
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class GPSloggerService extends Service {

	public static final String DATABASE_NAME = "GPSLOGGERDB";
	public static final String POINTS_TABLE_NAME = "LOCATION_POINTS";
	public static final String TRIPS_TABLE_NAME = "TRIPS";

	private final DecimalFormat sevenSigDigits = new DecimalFormat("0.#######");
	private final DateFormat timestampFormat = new SimpleDateFormat("yyyyMMddHHmmss");

	private LocationManager lm;
	private LocationListener locationListener;
	private SQLiteDatabase db;
	
	private static long minTimeMillis = 2000;
	private static long minDistanceMeters = 10;
	private static float minAccuracyMeters = 35;
	
	private int lastStatus = 0;
	private static boolean showingDebugToast = false;
	
	private static final String tag = "GPSLoggerService";

	/** Called when the activity is first created. */
	private void startLoggerService() {

		// ---use the LocationManager class to obtain GPS locations---
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		locationListener = new MyLocationListener();

		lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 
				minTimeMillis, 
				0,
				locationListener);
		//initDatabase();
	}
	
	private void initDatabase() {
		db = this.openOrCreateDatabase(DATABASE_NAME, SQLiteDatabase.OPEN_READWRITE, null);
		db.execSQL("CREATE TABLE IF NOT EXISTS " +
				POINTS_TABLE_NAME + " (GMTTIMESTAMP VARCHAR, LATITUDE REAL, LONGITUDE REAL," +
						"ALTITUDE REAL, ACCURACY REAL, SPEED REAL, BEARING REAL);");
		db.close();
		Log.i(tag, "Database opened ok");
	}

	private void shutdownLoggerService() {
		lm.removeUpdates(locationListener);
	}
 

public class MyLocationListener implements LocationListener {
		
		public void onLocationChanged(Location loc) {
			Double s = loc.getLatitude();
			Double m  = loc.getLongitude();
			Log.i(s.toString(),m.toString());
			if (loc != null) {
				boolean pointIsRecorded = false;
				try {
			 
						GregorianCalendar greg = new GregorianCalendar();
						TimeZone tz = greg.getTimeZone();
						int offset = tz.getOffset(System.currentTimeMillis());
						greg.add(Calendar.SECOND, (offset/1000) * -1);
						saveCoordinates(loc.getLatitude(),loc.getLongitude(),loc.getAltitude(),loc.getBearing(),loc.getAccuracy(),"gps" );
						 
				} catch (Exception e) {
					Log.e(tag, e.toString());
				} finally {
				 
				}
				if (pointIsRecorded) {
					if (showingDebugToast) Toast.makeText(
							getBaseContext(),
							"Location stored: \nLat: " + sevenSigDigits.format(loc.getLatitude())
									+ " \nLon: " + sevenSigDigits.format(loc.getLongitude())
									+ " \nAlt: " + (loc.hasAltitude() ? loc.getAltitude()+"m":"?")
									+ " \nAcc: " + (loc.hasAccuracy() ? loc.getAccuracy()+"m":"?"),
							Toast.LENGTH_SHORT).show();
				} else {
					if (showingDebugToast) Toast.makeText(
							getBaseContext(),
							"Location not accurate enough: \nLat: " + sevenSigDigits.format(loc.getLatitude())
									+ " \nLon: " + sevenSigDigits.format(loc.getLongitude())
									+ " \nAlt: " + (loc.hasAltitude() ? loc.getAltitude()+"m":"?")
									+ " \nAcc: " + (loc.hasAccuracy() ? loc.getAccuracy()+"m":"?"),
							Toast.LENGTH_SHORT).show();
				}
			}
		}

		public void onProviderDisabled(String provider) {
			if (showingDebugToast) Toast.makeText(getBaseContext(), "onProviderDisabled: " + provider,
					Toast.LENGTH_SHORT).show();

		}

		public void onProviderEnabled(String provider) {
			if (showingDebugToast) Toast.makeText(getBaseContext(), "onProviderEnabled: " + provider,
					Toast.LENGTH_SHORT).show();

		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			String showStatus = null;
			if (status == LocationProvider.AVAILABLE)
				showStatus = "Available";
			if (status == LocationProvider.TEMPORARILY_UNAVAILABLE)
				showStatus = "Temporarily Unavailable";
			if (status == LocationProvider.OUT_OF_SERVICE)
				showStatus = "Out of Service";
			if (status != lastStatus && showingDebugToast) {
				Toast.makeText(getBaseContext(),
						"new status: " + showStatus,
						Toast.LENGTH_SHORT).show();
			}
			lastStatus = status;
		}

	}

private void saveCoordinates(double latitude, double longitude, double altitude, double bearing, double accuracy,String provider){
	AppLog.logString("GPSloggerService.onProviderEnabled().");
	File folder = new File(Environment.getExternalStorageDirectory(), "GPSLogger");
	 
	boolean isNew = false;
	if (!folder.exists())
	{
		AppLog.logString("Folder does not exist" +Environment.getExternalStorageDirectory());
		folder.mkdirs();
		isNew = true;
	}
	try {
		File kmlFile = new File(folder.getPath(),"GPSlogfile.txt");
		if (!kmlFile.exists())
		{
			kmlFile.createNewFile();	
			isNew = true;
		}		
		 
		if(isNew)
		{
			FileOutputStream initialWriter = new FileOutputStream(kmlFile,true);
			TelephonyManager TelephonyMgr1 = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
	        String xml1 = "DEVICE ID "  +TelephonyMgr1.getDeviceId() +"\n";      
	        initialWriter.write( xml1.getBytes());
	        
			String xml = "TIME                      ,LAT                 ,LONG           " +
					"  ,ALT   ,BEA ,ACCU      ,PROV \n" ;		
			initialWriter.write(xml.getBytes());
			initialWriter.flush();
			initialWriter.close();
 
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		String dateString = sdf.format(new Date());
		long currentDeviceTime=Calendar.getInstance().getTimeInMillis();
 
		String placemark =  currentDeviceTime +"," +String.valueOf(longitude) + "," + String.valueOf(latitude) + "  ," 
				+String.valueOf(altitude) + "   ,"+ bearing + "   ," + String.valueOf(accuracy) +"   ," +provider +"\n";
		
		RandomAccessFile fileAccess = new RandomAccessFile(kmlFile, "rw");
	 	FileLock lock = fileAccess.getChannel().lock();
		
	    fileAccess.seek(kmlFile.length());
		fileAccess.write(placemark.getBytes());
 
	 	lock.release();
		fileAccess.close();
	
	}
	catch (IOException e) {
		e.printStackTrace();
	}
	 
}

	private NotificationManager mNM;

	@Override
	public void onCreate() {
		super.onCreate();
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		startLoggerService();

		// Display a notification about us starting. We put an icon in the
		// status bar.
		//showNotification();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		shutdownLoggerService();
 
	}

	/**
	 * Show a notification while this service is running.
	 */
	/*
	private void showNotification() {
		// In this sample, we'll use the same text for the ticker and the
		// expanded notification
		CharSequence text = getText(R.string.local_service_started);

		// Set the icon, scrolling text and timestamp
		Notification notification = new Notification(R.drawable.gpslogger16,
				text, System.currentTimeMillis());

		// The PendingIntent to launch our activity if the user selects this
		// notification
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, GPSLoggerService.class), 0);

		// Set the info for the views that show in the notification panel.
		notification.setLatestEventInfo(this, getText(R.string.service_name),
				text, contentIntent);

		// Send the notification.
		// We use a layout id because it is a unique number. We use it later to
		// cancel.
		mNM.notify(R.string.local_service_started, notification);
	}

*/
	// This is the object that receives interactions from clients. See
	// RemoteService for a more complete example.
	private final IBinder mBinder = new LocalBinder();

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public static void setMinTimeMillis(long _minTimeMillis) {
		minTimeMillis = _minTimeMillis;
	}

	public static long getMinTimeMillis() {
		return minTimeMillis;
	}

	public static void setMinDistanceMeters(long _minDistanceMeters) {
		minDistanceMeters = _minDistanceMeters;
	}

	public static long getMinDistanceMeters() {
		return minDistanceMeters;
	}

	public static float getMinAccuracyMeters() {
		return minAccuracyMeters;
	}
	
	public static void setMinAccuracyMeters(float minAccuracyMeters) {
		GPSloggerService.minAccuracyMeters = minAccuracyMeters;
	}

	public static void setShowingDebugToast(boolean showingDebugToast) {
		GPSloggerService.showingDebugToast = showingDebugToast;
	}

	public static boolean isShowingDebugToast() {
		return showingDebugToast;
	}

	/**
	 * Class for clients to access. Because we know this service always runs in
	 * the same process as its clients, we don't need to deal with IPC.
	 */
	public class LocalBinder extends Binder {
		GPSloggerService getService() {
			return GPSloggerService.this;
		}
	}

}