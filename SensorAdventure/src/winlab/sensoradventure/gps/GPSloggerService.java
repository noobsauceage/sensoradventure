package winlab.sensoradventure.gps;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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

	private LocationManager lm;
	private LocationListener locationListener;
	private SQLiteDatabase db;
	
	private static long minTimeMillis = 1000;
	private static long minDistanceMeters = 10;
	private static float minAccuracyMeters = 35;
	
	private int lastStatus = 0;
	private static boolean showingDebugToast = false;
	private GPSLoggerSQLite data;
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
		data = new GPSLoggerSQLite(this);
		startService(new Intent(this, GPSLoggerSQLite.class));
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
				try {
			  
					    saveCoordinatesfile(loc.getLatitude(),loc.getLongitude(),loc.getAltitude(),loc.getBearing(),loc.getAccuracy(),"NETWORK",loc.getSpeed() );
						saveCoordinatesdb(loc.getLatitude(),loc.getLongitude(),loc.getAltitude(),loc.getBearing(),loc.getAccuracy(),"NETWORK",loc.getSpeed() );
						 
				} catch (Exception e) {
					Log.e(tag, e.toString());
				} finally {

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

private void saveCoordinatesfile(double latitude, double longitude, double altitude, double bearing, double accuracy,String provider,double speed){
	AppLog.logString("GPSloggerService.onProviderEnabled().");
	File folder = new File(Environment.getExternalStorageDirectory(), "GPSLog");
	boolean isNew = false;
	if (!folder.exists())
	{
		AppLog.logString("Folder does not exist" +Environment.getExternalStorageDirectory());
		folder.mkdirs();
		isNew = true;
	}
	try {
		File kmlFile = new File(folder.getPath(),"GPSlog1.txt");
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
	        
			String xml = "TIME (ms)    ,          LONG(deg)      ,         LAT(deg)        ,       ALT(m) 		   , 	 BEA(deg)   	     ,              ACCU(m)    , PROV     ,      SPEED(m/s) \n" ;		
			initialWriter.write(xml.getBytes());
			initialWriter.flush();
			initialWriter.close();
 
		}
		String placemark;
		placemark= String.format("%d%s%25s%s%25s%s%25s%s%25s%s%25s%s%10s%s%25s\n",System.currentTimeMillis(),",",String.valueOf(longitude),",",String.valueOf(latitude),",",String.valueOf(altitude),",",bearing,",",String.valueOf(accuracy) ,",",provider,",",String.valueOf(speed));
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

private void saveCoordinatesdb(double latitude, double longitude, double altitude, double bearing, double accuracy,String provider,double speed) throws IOException{
 
	AppLog.logString("GPSloggerService.onProviderEnabled().");
	TelephonyManager TelephonyMgr1 = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
	String device_id = TelephonyMgr1.getDeviceId();
	data.open();
	long currentDeviceTime = Calendar.getInstance().getTimeInMillis();
	data.insertgpsrow(currentDeviceTime, device_id, latitude, longitude,
			altitude, bearing, accuracy, provider, speed);
}
 
	@Override
	public void onCreate() {
		super.onCreate();
		startLoggerService();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();	
		shutdownLoggerService();
	}
 
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