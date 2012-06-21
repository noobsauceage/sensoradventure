package winlab.sensoradventure;

import java.io.File;
import java.io.FileWriter;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class RunningService extends Service implements
SensorEventListener{
	
	private TelephonyManager telephonyManager;
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private String fileName[] = {"Accelerometer.txt","MagneticField.txt",
			"Orientation.txt","Gyroscope.txt","Light.txt","Pressure.txt","Temperature.txt",
			"Proximity.txt","Gravity.txt","Linear_Acceleration.txt","Rotation_Vector.txt","Humidity.txt","Ambient_Temperature.txt"};

	private File path = Environment
			.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
	
	private File file [] = {new File(path, fileName[0]),new File(path, fileName[1]),
			new File(path, fileName[2]),new File(path, fileName[3]),new File(path, fileName[4]),
			new File(path, fileName[5]),new File(path, fileName[6]),new File(path, fileName[7]),
			new File(path, fileName[8]),new File(path, fileName[9]),new File(path, fileName[10]),
			new File(path, fileName[11]),new File(path, fileName[12])};
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		Toast.makeText(this, "Start taking data", Toast.LENGTH_LONG).show();
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		for (int i=0; i<13; i++)
		if (SensorSetting.sensors[i]) {
			mSensor = mSensorManager.getDefaultSensor(i+1);
			try {
				path.mkdirs();
				file[i].setWritable(true);
				//Toast.makeText(this,"Data will be saved in " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
				FileWriter output = new FileWriter(file[i]);
				telephonyManager=(TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
				output.write("Phone ID: "+ telephonyManager.getDeviceId());
				
				switch (i+1) {
				case 1:
				    output.write("\nTimestamp (ms)           x (m/s^2)        y (m/s^2)        z (m/s^2)");
                    break;				
				case 2:
					output.write("\nTimestamp (ms)           x (uT)           y (uT)           z (uT)");
				    break;
				case 3:
					output.write("\nTimestamp (ms)           x (degrees)      y (degrees)      z (degrees)");
	                break;			
				case 4:
					output.write("\nTimestamp (ms)           x (rad/s)        y (rad/s)        z (rad/s)");
				    break;
				case 5:
					output.write("\nTimestamp (ms)         Ambient light level (lx)");
				    break;
				case 6:
					output.write("\nTimestamp (ms)         Ambient air pressure (hPa)");
				    break;
				case 7:
					output.write("\nTimestamp (ms)         Device temperature (degree Celsius)");
	                break;			
				case 8:
					output.write("\nTimestamp (ms)         Proximity (cm)");
		            break;		
				case 9:
					output.write("\nTimestamp (ms)           x (m/s^2)        y (m/s^2)        z (m/s^2)");
				    break;
				case 10:
					output.write("\nTimestamp (ms)           x (m/s^2)        y (m/s^2)        z (m/s^2)");
				    break;
				case 11:
					output.write("\nTimestamp (ms)           x  Unitless      y Unitless       z Unitless       scalar");
				    break;
				case 12:
					output.write("\nTimestamp (ms)          Relative Humidity %");
		            break;		
				case 13:
					output.write("\nTimestamp (ms)          Ambient air temperature (degree Celsius)");
				    break;
				default:
				}
				
				output.close();
			} catch (Exception e) {}
		}  
	}
		public void onSensorChanged (SensorEvent event) {
			String str = "";
			int i=event.sensor.getType(); 
			
				try {
					path.mkdirs();
					file[i-1].setWritable(true);
					FileWriter output = new FileWriter(file[i-1], true);
					switch (i) {
					   case 1: case 2: case 3: case 4: case 9: case 10:
					          str = String.format("\n%d%17.10f%17.10f%17.10f",
							       System.currentTimeMillis(), event.values[0], event.values[1],
							         event.values[2]);
					          break;
					   case 5: case 6: case 7: case 8: case 12: case 13:
						     
						      str = String.format("\n%d%17.10f",
							       System.currentTimeMillis(), event.values[0]);
					          break;
					   case 11:
						   str = String.format("\n%d%10.5f%10.5f%10.5f%10.5f",
							       System.currentTimeMillis(), event.values[0], event.values[1],
							         event.values[2],event.values[3]);
					          break;
					}
					output.write(str);
					output.close();
				} catch (Exception e) {}
}
		
		
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
		
		@Override
		public void onDestroy() {
			
			for (int i=0; i<13; i++)
			 if (SensorSetting.sensors[i])
			 {
				
			   mSensor = mSensorManager.getDefaultSensor(i+1);
			   mSensorManager.unregisterListener(this, mSensor);
			 }
			 else {
				 if (file[i].exists()) file[i].delete();
			 }
			Toast.makeText(this,"Data is saved in " + path.getPath(), Toast.LENGTH_LONG).show();
			for (int i=0; i<13; i++) SensorSetting.sensors[i]=true;
			
		}
		
		@Override
		public void onStart(Intent intent, int startid) {
			for (int i=0; i<13; i++)
				if (SensorSetting.sensors[i])
				{
					mSensor = mSensorManager.getDefaultSensor(i+1);
			        mSensorManager.registerListener(this, mSensor,1000);
				}
		}
}



