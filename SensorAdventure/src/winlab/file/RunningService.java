// This is the service class that controls the 13 SensorManager sensors
// as they pertain to the file writing configuration.

package winlab.file;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
//import java.util.Calendar;

import winlab.ASL.AndroidSensors;
import winlab.SensorGUI.StartGUI;
import winlab.sensoradventure.SensorAdventureActivity;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
//import android.os.Environment;
import android.os.Environment;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class RunningService extends Service implements SensorEventListener {
	// private Calendar c = Calendar.getInstance();
	private TelephonyManager telephonyManager;
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private int count=0;
	private int sensor_type[]= new int[13];
	public List<PrintWriter> captureFiles = new ArrayList<PrintWriter>();
	private String fileName[] = { "Accelerometer.txt", "MagneticField.txt",
			"Orientation.txt", "Gyroscope.txt", "Light.txt", "Pressure.txt",
			"Temperature.txt", "Proximity.txt", "Gravity.txt",
			"Linear_Acceleration.txt", "Rotation_Vector.txt", "Humidity.txt",
			"Ambient_Temperature.txt" };
	
	private File path = AndroidSensors.DataPath;
	private File upload_path = Environment
			.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS
					+ "/upload_to_server/");
	private File file[] = { new File(path, fileName[0]),
			new File(path, fileName[1]), new File(path, fileName[2]),
			new File(path, fileName[3]), new File(path, fileName[4]),
			new File(path, fileName[5]), new File(path, fileName[6]),
			new File(path, fileName[7]), new File(path, fileName[8]),
			new File(path, fileName[9]), new File(path, fileName[10]),
			new File(path, fileName[11]), new File(path, fileName[12]) };	
	
	
	// private String Direc = "/" + Integer.toString(c.get(Calendar.YEAR)) + "_"
	// + Integer.toString(c.get(Calendar.MONTH) + 1) + "_"
	// + Integer.toString(c.get(Calendar.DATE)) + "_"
	// + Integer.toString(c.get(Calendar.HOUR_OF_DAY)) + "Hr_"
	// + Integer.toString(c.get(Calendar.MINUTE)) + "Min_"
	// + Integer.toString(c.get(Calendar.SECOND)) + "Sec/";



	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@TargetApi(9)
	@Override
	public void onCreate() {
		Toast.makeText(this, "Start taking data", Toast.LENGTH_LONG).show();
		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		for (int i = 0; i < 13; i++)
			// If the sensor exists on the phone
			if (SensorSetting.sensors[i]) {
				// i+1 since SensorManager logs sensors starting from '1' and
				// not '0'
				mSensor = mSensorManager.getDefaultSensor(i + 1);
				if (StartGUI.state[0])
				//For "Write to file" configuration 
				try {
					path.mkdirs();
					file[i].setWritable(true);
					// Toast.makeText(this,"Data will be saved in " +
					// file.getAbsolutePath(), Toast.LENGTH_LONG).show();
					FileWriter output = new FileWriter(file[i]);
					
					output.write("Phone ID: " + telephonyManager.getDeviceId());

					// For a given sensor, begin file writing by writing the
					// header
					switch (i + 1) {
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
				} catch (Exception e) {
				}
				if (StartGUI.state[2])
				//For "upload to server" configuration	
				try{
					upload_path.mkdirs();
					File myFile = new File(upload_path, mSensor.getType() + "_"
							+ mSensor.getName() + ".csv");

					myFile.createNewFile();
					captureFiles
							.add(new PrintWriter(new FileWriter(myFile, false)));
					captureFiles.get(count).println(
							"`timestamp`" + "," + getFields(mSensor.getType()));
					sensor_type[count]=mSensor.getType();
					count++;
				}catch(Exception e){}
			}
	}
	private String getFields(int type) {
		String fields = null;

		switch (type) {
		case 1:
		case 2:
		case 3:
		case 9:
		case 10:
		case 11:
			fields = "`x`" + "," + "`y`" + "," + "`z`";
			break;
		default:
			fields = "`value`" + "," + "`null0`" + "," + "`null1`"; 
		}
		return fields;

	}

	@Override
	public void onStart(Intent intent, int startid) {
		int m;
		for (int i = 0; i < 13; i++)
			// If the sensor exists on the phone, set its update rate from the
			// program
			if (SensorSetting.sensors[i]) {
				m = 1000 * SensorSetting.updateRate[i];
				mSensor = mSensorManager.getDefaultSensor(i + 1);
				mSensorManager.registerListener(this, mSensor, m);
			}
	}

	// onSensorChanged is when the file writing is performed.
	// This event is not firmly scheduled.
	@TargetApi(9)
	public void onSensorChanged(SensorEvent event) {
		String str = "";
		// Discover which sensor has changed, store value in i
		int i = event.sensor.getType();
        if (StartGUI.state[0])
        	//"write to file" configuration
		try {
			path.mkdirs();
			file[i - 1].setWritable(true);
			FileWriter output = new FileWriter(file[i - 1], true);
			// Switch on sensor. Each of these sensors print out x,y,z values.
			switch (i) {
			case 1:
			case 2:
			case 3:
			case 4:
			case 9:
			case 10:
				MarkValue.instantValue[i - 1][0] = event.values[0];
				MarkValue.instantValue[i - 1][1] = event.values[1];
				MarkValue.instantValue[i - 1][2] = event.values[2];
				str = String.format("\n%d%17.10f%17.10f%17.10f",
						System.currentTimeMillis(), event.values[0],
						event.values[1], event.values[2]);
				break;
			// Each of these values print out a single number.
			case 5:
			case 6:
			case 7:
			case 8:
			case 12:
			case 13:
				MarkValue.instantValue[i - 1][0] = event.values[0];
				str = String.format("\n%d%17.10f", System.currentTimeMillis(),
						event.values[0]);
				break;
			// This is four columns; x,y,z, & scalar.
			case 11:
				// If we have 4 values to print.
				if (event.values.length == 4) {
					MarkValue.instantValue[i - 1][0] = event.values[0];
					MarkValue.instantValue[i - 1][1] = event.values[1];
					MarkValue.instantValue[i - 1][2] = event.values[2];
					MarkValue.instantValue[i - 1][3] = event.values[3];
					str = String.format("\n%d%10.5f%10.5f%10.5f%10.5f",
							System.currentTimeMillis(), event.values[0],
							event.values[1], event.values[2], event.values[3]);
				}
				// The only other option is three values.
				else {
					MarkValue.instantValue[i - 1][0] = event.values[0];
					MarkValue.instantValue[i - 1][1] = event.values[1];
					MarkValue.instantValue[i - 1][2] = event.values[2];
					str = String.format("\n%d%10.5f%10.5f%10.5f          NA",
							System.currentTimeMillis(), event.values[0],
							event.values[1], event.values[2]);
				}
				break;
			}
			output.write(str);
			output.close();
		} catch (Exception e) {
		}
        
        if (StartGUI.state[2])
        	//"upload to server" configuration
        {
        	long timestamp;
        	int index = 0;
        	for (int j=0; j<count; j++)
        		if (i==sensor_type[j]) index=j;

    		PrintWriter thiscaptureFile;

    		timestamp = System.currentTimeMillis();

    		thiscaptureFile = captureFiles.get(index);
    		if (thiscaptureFile != null) {
    			thiscaptureFile.print(timestamp);
    			// for( int i = 0 ; i < event.values.length ; ++i ) {
    			for (int k = 0; k < event.values.length; k++) {
    				thiscaptureFile.print("," + event.values[k]);
    			}
    			thiscaptureFile.println();
    		}
	
        }
	}

	// This method must be included but is not used.
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onDestroy() {
		for (int i = 0; i < 13; i++)
			// If the sensor exists on the phone
			if (SensorSetting.sensors[i]) {
				mSensor = mSensorManager.getDefaultSensor(i + 1);
				mSensorManager.unregisterListener(this, mSensor);
			}
			// If it does not, check to see if the file exists.
			// Sometimes a file is written for sensors that we do not have; we
			// must delete them.
			else if (StartGUI.state[0]){
				if (file[i].exists())
					file[i].delete();
			}
		for (int i = 0; i<count; i++)
			captureFiles.get(i).close();
		// Toast.makeText(this, "Data is saved in " + path.getPath(),
		// Toast.LENGTH_LONG).show();

	}

}
