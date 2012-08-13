package winlab.sensoradventure.gps;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.util.Calendar;
import winlab.ASL.AndroidSensors;
import winlab.sensoradventure.R;
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
 * minimum time for logging is 1 sec.
 */
public class GPSLoggerService extends Service {
	private GPSLoggerSQLite data;
	private LocationManager lm;
	private LocationListener locationListener;
	private static int minTimeMillis = 1000; 
	private static long minDistanceMeters = 0;
	private static float minAccuracyMeters = 35;
	private static String[]  file_default_string;
	private static final String tag = "GPSLoggerService";
	private static String provider = "GPS";
	private static int typeoflog =0;
	private static Double  Latitude=0d;
	private static Double Longitude =0d;
	private static Double Altitude;
	private static Float Bearing;
	private static Float Accuracy;
	private static String Provider;
	private static Float Speed;
	public static String device_id;
	private static long currentDeviceTime;
	private static String[] datagps;
	private  static int startdb=0;
	/**
	 * This is the method, that starts logging the procedure
	 * This method checks for 3 values
	 * the rate in millisec, the provider = GPS/Network 
	 * and the type of log , either to file = 0
	 * sqlite = 1  and to both = 2
	 * @throws Exception
	 */
	private void startLoggerService() throws Exception {
		setInitialCondition();
		if(typeoflog == 1 || typeoflog ==2) {
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
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 
					minTimeMillis, 
					minDistanceMeters,
					locationListener);
		}	 
	}

	private void shutdownLoggerService() {
		lm.removeUpdates(locationListener);
	} 

	private void setInitialCondition() {
		Resources res = getResources();
		file_default_string = res.getStringArray(R.array.gps_default);
	} 

	
	public class MyLocationListener implements LocationListener {	
		public void onLocationChanged(Location loc) {
			if (loc != null) {
				Latitude = loc.getLatitude();			
				Longitude = loc.getLongitude();
				Altitude =loc.getAltitude();
				Bearing =loc.getBearing();
				Accuracy=loc.getAccuracy();
				Speed = loc.getSpeed() ;
				Provider = provider;
				currentDeviceTime = Calendar.getInstance().getTimeInMillis();
				TelephonyManager TelephonyMgr1 = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
				device_id = TelephonyMgr1.getDeviceId();
				try {
					if(typeoflog ==2){
						saveCoordinatesfile(loc.getLatitude(),loc.getLongitude(),loc.getAltitude(),loc.getBearing(),loc.getAccuracy(),provider,loc.getSpeed() );
						saveCoordinatesdb(loc.getLatitude(),loc.getLongitude(),loc.getAltitude(),loc.getBearing(),loc.getAccuracy(),provider,loc.getSpeed() );
					}
					else{
						if(typeoflog ==0 ){
							saveCoordinatesfile(loc.getLatitude(),loc.getLongitude(),loc.getAltitude(),loc.getBearing(),loc.getAccuracy(),provider,loc.getSpeed() );
						}
						else  if(typeoflog ==1 ){
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

		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}
		/* (non-Javadoc)
		 * @see android.location.LocationListener#onProviderEnabled(java.lang.String)
		 */

		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		/* (non-Javadoc)
		 * @see android.location.LocationListener#onStatusChanged(java.lang.String, int, android.os.Bundle)
		 */

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}
	}

	/**
	 *  This is the method, which writes to file 
	 *  The File name is in the Strings.xml file, and can be easily
	 *  changed without recompiling the programs
	 */
	public void saveCoordinatesfile(double latitude, double longitude, double altitude, double bearing, double accuracy,String provider,double speed){
		AppLog.logString("GPSLoggerService.onProviderEnabled().");
		File path;
		path=AndroidSensors.DataPath;
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
			placemark= String.format("%d%s%25s%s%25s%s%25s%s%25s%s%25s%s%10s%s%25s\n", currentDeviceTime,",",String.valueOf(longitude),",",String.valueOf(latitude),",",String.valueOf(altitude),",",bearing,",",String.valueOf(accuracy) ,",",provider,",",String.valueOf(speed));
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

	/**
	 *  This is the method, which writes to sqlite db
	 *  The DB name is in the Strings.xml file, and can be easily
	 *  changed without recompiling the programs
	 */
	public void saveCoordinatesdb(double latitude, double longitude, double altitude, double bearing, double accuracy,String provider,double speed) throws IOException{
		AppLog.logString("GPSLoggerService.onProviderEnabled().");
		TelephonyManager TelephonyMgr1 = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		device_id = TelephonyMgr1.getDeviceId();
		data.open();
		startdb=1;
		data.insertgpsrow(currentDeviceTime, device_id, latitude, longitude,
				altitude, bearing, accuracy, provider, speed);
	}

	public void onCreate(Intent intent) {
		super.onCreate();
	}


	/**
	 *  GPS requires 3 values to be set. The Values are from the
	 *  gpsextra which is passed as intent
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Bundle bundle = intent.getExtras();
		datagps =  bundle.getStringArray("gpsextra");
		minTimeMillis = Integer.parseInt(datagps[0]) * 1000;
		typeoflog =  Integer.parseInt(datagps[1]);
		provider = datagps[2];
		try {
			startLoggerService();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return START_STICKY;
	}



	@Override
	public void onDestroy() {
		super.onDestroy();
		if(typeoflog == 1 || typeoflog == 2)
		{
			if(startdb==1)
			{
			data.close();
			}
		}
		shutdownLoggerService();
	}

	@Override
	public boolean stopService(Intent name) {
		if(typeoflog == 1 || typeoflog == 2)
		{
			if(startdb==1)
			{
			data.close();
			}
		}
		shutdownLoggerService();
		return super.stopService(name);

	}

	public static void setMinTimeMillis(int _minTimeMillis) {
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

	public static String [] getgpsmark()
	{
		if(Latitude == 0 && Longitude ==0)
		{
			return null;
		}
		else
		{

			String [] GPSValues = {device_id.toString(),Latitude.toString(),Longitude.toString(),Altitude.toString(),
					Bearing.toString(),Accuracy.toString(),Provider,Speed.toString()};	
			return GPSValues;
		}
	}

}