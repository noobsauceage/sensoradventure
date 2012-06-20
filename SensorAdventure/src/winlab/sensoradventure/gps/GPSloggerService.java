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

public class GPSloggerService extends Service implements LocationListener{

	Location location;
	private static final int gpsMinTime = 500;
	private static final int gpsMinDistance = 0;
	private static final int TIMER_DELAY = 1000;
	private static final int GEOCODER_MAX_RESULTS = 5;
	
	private LocationManager manager = null;
	private double latitude = 0.0;
	private double longitude = 0.0;
	private double altitude = 0.0;
	private double  bearing = 0.0;
	private double accuracy = 0.0;
	private Timer monitoringTimer = null;
 
	public GPSloggerService() {
		AppLogger.logString("GPSloggerService.GPSloggerService().");
	}

	@Override
	public IBinder onBind(Intent arg0) {
		AppLogger.logString("GPSloggerService.onBind().");
		return null;
	}

	@Override
	public void onCreate() {
		AppLogger.logString("GPSloggerService.onCreate().");
		super.onCreate();
 
		 
	}

	@Override
	public void onStart(Intent intent, int startId) {
		AppLogger.logString("GPSloggerService.onStart().");
		startLoggingService();
	    startMonitoringTimer();	
		super.onStart(intent, startId);
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		AppLogger.logString("GPSloggerService.onStartCommand().");
		
		startLoggingService();
	 	startMonitoringTimer();
		
		return Service.START_STICKY;
	}

    public void onLocationChanged(Location location) 
    {
    	updateChanged(location);
    	 
    } 
    
	public void updateChanged(Location location) {
		AppLogger.logString("GPSloggerService.onLocationChanged().");
		
		latitude = location.getLatitude();
		longitude = location.getLongitude();
		altitude = location.getAltitude();
        bearing  = location.getBearing();
        accuracy = location.getAccuracy();
	}

	public void onProviderDisabled(String provider) {
		AppLogger.logString("GPSloggerService.onProviderDisabled().");
	}

	public void onProviderEnabled(String provider) {
		AppLogger.logString("GPSloggerService.onProviderEnabled().");
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		AppLogger.logString("GPSloggerService.onStatusChanged().");
	}
	
	public void startLoggingService(){
		 
			manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
			manager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER, 0,0, this);
			AppLogger.logString(LocationManager.NETWORK_PROVIDER);
	}
	
	private void stopLoggingService(){
		stopSelf();
	}
	
	private void startMonitoringTimer(){
		 
		monitoringTimer = new Timer();
		monitoringTimer.scheduleAtFixedRate(
				new TimerTask()
				{
					@Override
					public void run()
					{	 
						AppLogger.logString("latitude "+latitude +" longitude" + longitude);
						if (latitude != 0.0 && longitude  != 0.0)
						{
							monitoringTimer.cancel();
							monitoringTimer = null;
							
							manager.removeUpdates(GPSloggerService.this);
							 
							saveCoordinates(latitude, longitude, altitude, bearing, accuracy);
							stopLoggingService();
						}
					}
				}, 
				GPSloggerService.TIMER_DELAY,
				GPSloggerService.TIMER_DELAY);
	}
	
	private String getLocationName(double latitude, double longiture){
		String name = "";
		Geocoder geocoder = new Geocoder(this);
		
		try {
			List<Address> address = geocoder.getFromLocation(latitude, longiture, GPSloggerService.GEOCODER_MAX_RESULTS);
			
			name = address.get(0).toString();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		
		return name;
	}
	
	private void saveCoordinates(double latitude, double longitude, double altitude, double bearing, double accuracy){
	 
		AppLogger.logString("GPSloggerService.onProviderEnabled().");
		File folder = new File(Environment.getExternalStorageDirectory(), "GPSLoggerTemp");
		 
		boolean isNew = false;
		if (!folder.exists())
		{
			AppLogger.logString("Folder does not exist" +Environment.getExternalStorageDirectory());
			folder.mkdirs();
			if(folder.exists())
			{
				AppLogger.logString("Folder  exists 2");
			}
		 
			isNew = true;
		}
		try {
			File kmlFile = new File(folder.getPath(),"logfile.txt");
			if (!kmlFile.exists())
			{
				kmlFile.createNewFile();	
				isNew = true;
			}		
			 
			if(isNew)
			{
				FileOutputStream initialWriter = new FileOutputStream(kmlFile,true);
			     
		 
				TelephonyManager TelephonyMgr1 = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
		        String xml1 = " DEVICE ID "  +TelephonyMgr1.getDeviceId();      
		        initialWriter.write( xml1.getBytes());
		        String xml2 = "                                                    ";
		        initialWriter.write(xml2.getBytes());
		         
				String xml = "   TIME   ,   LAT    ,    LONG     ,    ALT   ,   BEARING   , ACCURACY                     " ;		
				initialWriter.write(xml.getBytes());
				initialWriter.flush();
				initialWriter.close();
	 
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
			String dateString = sdf.format(new Date());
			long currentDeviceTime=Calendar.getInstance().getTimeInMillis();
			String placemark =  currentDeviceTime +"," +String.valueOf(longitude) + "," + String.valueOf(latitude) + "," 
			+ bearing + "," + String.valueOf(accuracy);
			
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
}