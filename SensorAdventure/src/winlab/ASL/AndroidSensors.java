package winlab.ASL;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import winlab.file.ContinuousSnapshot;
import winlab.file.MarkValue;
import winlab.file.RunningService;
import winlab.file.SensorSetting;
import winlab.sensoradventure.UploadToServer;
import winlab.sensoradventure.gps.GPSLoggerService;
import winlab.sql.Mark_SQL;
import winlab.sql.Sensors_SQLite_Service;
import winlab.sql.Sensors_SQLite_Setting;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.widget.Toast;

public class AndroidSensors {

	private SensorSetting sensorSetting;
	private Sensors_SQLite_Setting sqliteSetting;
	private Mark_SQL markSQL;
	private ContinuousRecorder record;
	private Context programContext;
	private boolean[] selectedSensors; // Which sensors are selected
	public static boolean[] dataConfig; // File,SQLite,Upload to Server
	private int[] rates; // Update/sampling rates for the sensors
	private String[] Sensors = { "Accelerometer ", "Magnetic ", "Orientation ",
			"Gyroscope ", "Light ", "Pressure ", "Temperature ", "Proximity ",
			"Gravity ", "L. Accelerometer ", "Rotation ", "Humidity ",
			"A. Temperature ", "Microphone ", "GPS " };
	private ArrayList<String> availableSensors;
	private ArrayList<String> unavailableSensors;
	private ContinuousSnapshot continuoussnapshot;
	public static File DataPath;

	public AndroidSensors(Context context) {
		programContext = context;
		record = new ContinuousRecorder(programContext);
		sqliteSetting = new Sensors_SQLite_Setting(programContext);
		markSQL = new Mark_SQL(programContext);
		sensorSetting = new SensorSetting(programContext);
		selectedSensors = new boolean[15];
		rates = new int[15];
		dataConfig = new boolean[3];
		sensorSetting.testAvailableSensors();

		for (int i = 0; i < selectedSensors.length; i++) {
			selectedSensors[i] = false;
			rates[i] = 0;
			if (i < 3)
				dataConfig[i] = false;
		}

		Calendar c = Calendar.getInstance();
		String Direc = "/" + Integer.toString(c.get(Calendar.YEAR)) + "_"
				+ Integer.toString(c.get(Calendar.MONTH) + 1) + "_"
				+ Integer.toString(c.get(Calendar.DATE)) + "_"
				+ Integer.toString(c.get(Calendar.HOUR_OF_DAY)) + "Hr_"
				+ Integer.toString(c.get(Calendar.MINUTE)) + "Min_"
				+ Integer.toString(c.get(Calendar.SECOND)) + "Sec/";
		DataPath = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS
						+ Direc);

	}

	public void performSnapshot(long rate, long duration) {
		continuoussnapshot = new ContinuousSnapshot(rate, duration,
				programContext);
		continuoussnapshot.performSnapshot();

	}

	public ArrayList<String> determineAvailableSensors() {
		availableSensors = new ArrayList<String>();
		for (int i = 0; i < SensorSetting.available_sensors.length; i++) {
			if (SensorSetting.available_sensors[i]) {
				availableSensors.add(Sensors[i]);
			}
		}

		// For now, assume GPS and Microphone are always available
		availableSensors.add(Sensors[Sensors.length - 2]);
		availableSensors.add(Sensors[Sensors.length - 1]);

		return availableSensors;
	}

	public ArrayList<String> determineUnavailableSensors() {
		unavailableSensors = new ArrayList<String>();
		for (int i = 0; i < SensorSetting.available_sensors.length; i++) {
			if (!SensorSetting.available_sensors[i]) {
				unavailableSensors.add(Sensors[i]);
			}
		}
		return unavailableSensors;
	}

	public void markEvent() {
		// If 'Write to File' is selected
		if (dataConfig[0])
			// Write to the instant reading file
			MarkValue.print();

		// If 'Write to SQLite' is selected
		if (dataConfig[1]) {
			try {
				// Attempt insertion into SQLite database
				MarkValue.insertSQL(markSQL);
			} catch (Exception e) {
				Toast.makeText(programContext, "1", Toast.LENGTH_LONG).show();
			}
		}
	}

	public void prepareForLogging() {
		MarkValue.set();
		record.setPath();
		if ((dataConfig[0] == true) || (dataConfig[2])) {
			sensorSetting.selectSensors(selectedSensors);
			SensorSetting.setRate(rates);

		}
		if (dataConfig[1] == true) {
			sqliteSetting.selectSensors(selectedSensors);
			markSQL.open();
			markSQL.deleteTable();
			Sensors_SQLite_Setting.setRate(rates);
		}

	}

	public void startLogging() {
		if (dataConfig[2])
			programContext.startService(new Intent(programContext,
					RunningService.class));
		if (dataConfig[0] == true) {
			programContext.startService(new Intent(programContext,
					RunningService.class));
			// If the microphone is on and SQLite is not checked
			if ((selectedSensors[13]) && (dataConfig[1] == false)) {
				record.record();
			}
			// If GPS is on and SQLite is not checked
			if ((selectedSensors[14]) && (dataConfig[1] == false)) {
				programContext.startService(new Intent(programContext,
						GPSLoggerService.class));
			}
		}
		// If 'Write to SQLite Database' is checked
		if (dataConfig[1]) {
			programContext.startService(new Intent(programContext,
					Sensors_SQLite_Service.class));
			// If microphone is on, write to SQLite db
			if (selectedSensors[13]) {
				record.writeToSQLite();
				record.record();
			}
			
			if ((selectedSensors[14])) {
				programContext.startService(new Intent(programContext,
						GPSLoggerService.class));
			}
		}
	}

	public void stopLogging() {
		if (dataConfig[0]) {
			// Stop the file writing service
			programContext.stopService(new Intent(programContext,
					RunningService.class));

			// If the microphone is selected & 'Write to SQLite' is not
			// selected
			if ((selectedSensors[13]) && (dataConfig[1] == false)) {
				record.stop();
				record.cancel();
			}
			
			if ((selectedSensors[14]) && (dataConfig[1] == false)) {
				programContext.stopService(new Intent(programContext,
						GPSLoggerService.class));
			}
		}

		// If 'Write to SQLite' is selected
		if (dataConfig[1]) {
			try {
				// for (int j=0; j<13; j++)
				// if (sensorCheck[j]) data2.endTransaction(j);

				// Attempt to copy the database to the sdcard and then close
				// it
				markSQL.copy();
				markSQL.close();
			} catch (Exception e) {
				Toast.makeText(programContext, "2", Toast.LENGTH_LONG).show();
			}
			programContext.stopService(new Intent(programContext,
					Sensors_SQLite_Service.class));

			// If the microphone is on
			if (selectedSensors[13]) {
				record.stop();
				record.cancel();
			}

			if ((selectedSensors[14])) {
				programContext.stopService(new Intent(programContext,
						GPSLoggerService.class));
			}
		}

		if (dataConfig[2]) {
			programContext.stopService(new Intent(programContext,
					RunningService.class));
            UploadToServer uploadtoserver= new UploadToServer(programContext);
            uploadtoserver.startUpload();
            
		}
		Toast.makeText(programContext,
				"Data is stored in: " + DataPath.toString() + "/",
				Toast.LENGTH_LONG).show();
	}

	public void stopFileLogging() {
		// If 'Write to File' has been selected
		if (dataConfig[0]) {
			programContext.stopService(new Intent(programContext,
					RunningService.class));
			if ((selectedSensors[13]) && (dataConfig[1] == false)) {
				record.stop();
				record.cancel();
			}
			
			if ((selectedSensors[14]) && (dataConfig[1] == false)) {
				programContext.stopService(new Intent(programContext,
						GPSLoggerService.class));
			}
		}

	}

	public void closeMarkSQL() {
		if (dataConfig[1])
			markSQL.close();

	}

	public void stopSQLiteLogging() {
		if (dataConfig[1]) {
			try {
				closeMarkSQL();
			} catch (Exception e) {
			}
			programContext.stopService(new Intent(programContext,
					Sensors_SQLite_Service.class));
			if (selectedSensors[13]) {
				record.stop();
				record.cancel();
			}
			if ((selectedSensors[14])) {
				programContext.stopService(new Intent(programContext,
						GPSLoggerService.class));
			}
		}

	}

	public void batchTest(boolean[] sensors, boolean[] data, int[] ratess) {
		for (int i = 0; i < sensors.length; i++) {
			selectedSensors[i] = sensors[i];
			rates[i] = ratess[i];
			if (i < 3)
				dataConfig[i] = data[i];
		}

	}

	public void writeToFile(boolean setting) {
		dataConfig[0] = setting;
	}

	public void writeToSQLite(boolean setting) {
		dataConfig[1] = setting;
	}

	public void uploadToServer(boolean setting) {
		dataConfig[2] = setting;
	}

	public void setAccelerometer(boolean setting) {
		selectedSensors[0] = setting;
	}

	public void setMagneticField(boolean setting) {
		selectedSensors[1] = setting;
	}

	public void setOrientation(boolean setting) {
		selectedSensors[2] = setting;
	}

	public void setGyroscope(boolean setting) {
		selectedSensors[3] = setting;
	}

	public void setLight(boolean setting) {
		selectedSensors[4] = setting;
	}

	public void setPressure(boolean setting) {
		selectedSensors[5] = setting;
	}

	public void setTemperature(boolean setting) {
		selectedSensors[6] = setting;
	}

	public void setProximity(boolean setting) {
		selectedSensors[7] = setting;
	}

	public void setGravity(boolean setting) {
		selectedSensors[8] = setting;
	}

	public void setLinearAccelerometer(boolean setting) {
		selectedSensors[9] = setting;
	}

	public void setRotationVector(boolean setting) {
		selectedSensors[10] = setting;
	}

	public void setHumidity(boolean setting) {
		selectedSensors[11] = setting;
	}

	public void setAmbientTemperature(boolean setting) {
		selectedSensors[12] = setting;
	}

	public void setMicrophone(boolean setting) {
		selectedSensors[13] = setting;
	}

	public void setGPS(boolean setting) {
		selectedSensors[14] = setting;
	}

	public void setAccelerometerRate(int rate) {
		rates[0] = rate;
	}

	public void setMagneticFieldRate(int rate) {
		rates[1] = rate;
	}

	public void setOrientationRate(int rate) {
		rates[2] = rate;
	}

	public void setGyroscopeRate(int rate) {
		rates[3] = rate;
	}

	public void setLightRate(int rate) {
		rates[4] = rate;
	}

	public void setPressureRate(int rate) {
		rates[5] = rate;
	}

	public void setTemperatureRate(int rate) {
		rates[6] = rate;
	}

	public void setProximityRate(int rate) {
		rates[7] = rate;
	}

	public void setGravityRate(int rate) {
		rates[8] = rate;
	}

	public void setLinearAccelerometerRate(int rate) {
		rates[9] = rate;
	}

	public void setRotationVectorRate(int rate) {
		rates[10] = rate;
	}

	public void setHumidityRate(int rate) {
		rates[11] = rate;
	}

	public void setAmbientTemperatureRate(int rate) {
		rates[12] = rate;
	}

	public void setMicrophoneRate(int rate) {
		rates[13] = rate;
	}

	public void setGPSRate(int rate) {
		rates[14] = rate;
	}

	public void configureMicrophone(int mic, int sample, int channeli,
			int channelo, int format, int stream, int mode) {
		rates[13] = sample;
		record = new ContinuousRecorder(mic, sample, channeli, channelo,
				format, stream, mode, programContext);
	}

}
