package winlab.sensoradventure.gps;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.util.Calendar;
import winlab.ASL.AndroidSensors;
import winlab.SensorGUI.StartGUI;
import winlab.sensoradventure.R;
import winlab.sensoradventure.SensorAdventureActivity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * @author malathidharmalingam
 * Version 1.0
 * This is the class for Logging GPS/Network data into file/SQLdatabase
 * minimum time for 1 sec.
 */
public class GPSLoggerService extends Service {
	private GPSLoggerSQLite data;
	private boolean state[];
	private LocationManager lm;
	private LocationListener locationListener;
	private static Long minTimeMillis = 1000l; 
	private static long minDistanceMeters = 0;
	private static float minAccuracyMeters = 35;
	private static String[]  file_default_string;
	private static final String tag = "GPSLoggerService";
	private String provider = "GPS";
	private static long logsec;
	private String lograte;
	
	
	private void startLoggerService() throws Exception {
		setInitialCondition();
		state = StartGUI.state;
		
		if(state[1]) {
			data = new GPSLoggerSQLite(this,file_default_string[1],file_default_string[2]);
			startService(new Intent(this, GPSLoggerSQLite.class));
		}
		
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationListener = new MyLocationListener();
		
		if(provider.equals("NETWORK")){
		lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 
				minTimeMillis, 
				minDistanceMeters,
				locationListener);
		}
		else{
			lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 
					minTimeMillis, 
					minDistanceMeters,
					locationListener);
		}	 
	}

	private void setInitialCondition() {
		Resources res = getResources();
		file_default_string = res.getStringArray(R.array.gps_default);
		provider = SensorAdventureActivity.provider;
		lograte = SensorAdventureActivity.lograte;
		logsec = Long.parseLong(lograte);
		minTimeMillis = logsec * 1000;
	} 
	
	private void shutdownLoggerService() {
		lm.removeUpdates(locationListener);
	} 

	public class MyLocationListener implements LocationListener {
		
		public void onLocationChanged(Location loc) {
			Double s = loc.getLatitude();
			Double m  = loc.getLongitude();
			Log.i(s.toString(),m.toString());
			Boolean a  = state[0];
			Boolean b  = state[1];
			if (loc != null) {
			  try {
				 if(a && b){
					saveCoordinatesfile(loc.getLatitude(),loc.getLongitude(),loc.getAltitude(),loc.getBearing(),loc.getAccuracy(),SensorAdventureActivity.provider,loc.getSpeed() );
					saveCoordinatesdb(loc.getLatitude(),loc.getLongitude(),loc.getAltitude(),loc.getBearing(),loc.getAccuracy(),SensorAdventureActivity.provider,loc.getSpeed() );
				  }
			 else{
				   if(a ){
							saveCoordinatesfile(loc.getLatitude(),loc.getLongitude(),loc.getAltitude(),loc.getBearing(),loc.getAccuracy(),provider,loc.getSpeed() );
						}
			 else  if(b){
							saveCoordinatesdb(loc.getLatitude(),loc.getLongitude(),loc.getAltitude(),loc.getBearing(),loc.getAccuracy(),provider,loc.getSpeed() );
						}
					}
				} catch (Exception e) {
					Log.e(tag, e.toString());
				} finally {

				}

			}
		}
		/* (non-Javadoc)
		 * @see android.location.LocationListener#onProviderDisabled(java.lang.String)
		 */
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}
		/* (non-Javadoc)
		 * @see android.location.LocationListener#onProviderEnabled(java.lang.String)
		 */
		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		/* (non-Javadoc)
		 * @see android.location.LocationListener#onStatusChanged(java.lang.String, int, android.os.Bundle)
		 */
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}
	}

	public void saveCoordinatesfile(double latitude, double longitude, double altitude, double bearing, double accuracy,String provider,double speed){
		AppLog.logString("GPSLoggerService.onProviderEnabled().");
		File path;
		path=AndroidSensors.DataPath;
	 
		Log.i(path.toString(),"Path File");
		 
		boolean isNew = false;
		if (!path.exists())
		{
			AppLog.logString("Folder does not exist" +Environment.getExternalStorageDirectory());
			path.mkdirs();
			isNew = true;
		}
		try {
			File kmlFile = new File(path,file_default_string[0]);
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

	public void saveCoordinatesdb(double latitude, double longitude, double altitude, double bearing, double accuracy,String provider,double speed) throws IOException{
		AppLog.logString("GPSLoggerService.onProviderEnabled().");
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
		try {
			startLoggerService();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(state[1])
		{
		 	data.close();
		}
		shutdownLoggerService();
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
		GPSLoggerService.minAccuracyMeters = minAccuracyMeters;

	}

	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}