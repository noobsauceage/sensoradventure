package winlab.ASL;

import java.util.ArrayList;

import winlab.file.ContinuousSnapshot;
import winlab.file.RunningService;
import winlab.file.SensorSetting;
import winlab.file.SnapShotValue;
import winlab.sensoradventure.ContinuousRecorder;
import winlab.sensoradventure.SensorAdventureActivity;
import winlab.sensoradventure.gps.GPSloggerService;
import winlab.sql.Sensors_SQLite_Service;
import winlab.sql.Sensors_SQLite_Setting;
import winlab.sql.SnapShot_SQL;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

public class AndroidSensors {

	private SensorSetting sensorSetting;
	private Sensors_SQLite_Setting sqliteSetting;
	private SnapShot_SQL snapshotSQL;
	private ContinuousRecorder record;
	private Context programContext;
	private boolean[] selectedSensors; // Which sensors are selected
	private boolean[] dataConfig; // File,SQLite,Upload to Server
	private int[] rates; // Update/sampling rates for the sensors
	private String[] Sensors = { "Accelerometer ", "Magnetic ", "Orientation ",
			"Gyroscope ", "Light ", "Pressure ", "Temperature ", "Proximity ",
			"Gravity ", "L. Accelerometer ", "Rotation ", "Humidity ",
			"A. Temperature ", "Microphone ", "GPS " };
	private ArrayList<String> availableSensors;
	private ArrayList<String> unavailableSensors;
	private ContinuousSnapshot continuoussnapshot;

	public AndroidSensors(Context context) {
		programContext = context;
		record = new ContinuousRecorder(programContext);
		sqliteSetting = new Sensors_SQLite_Setting(programContext);
		snapshotSQL = new SnapShot_SQL(programContext);
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
			SnapShotValue.print();

		// If 'Write to SQLite' is selected
		if (dataConfig[1]) {
			try {
				// Attempt insertion into SQLite database
				SnapShotValue.insertSQL(snapshotSQL);
			} catch (Exception e) {
				Toast.makeText(programContext, "1", Toast.LENGTH_LONG).show();
			}
		}
	}

	public void prepareForLogging() {
		SnapShotValue.set();
		if (dataConfig[0] == true) {
			sensorSetting.selectSensors(selectedSensors);
			SensorSetting.setRate(rates);

		}
		if (dataConfig[1] == true) {
			sqliteSetting.selectSensors(selectedSensors);
			snapshotSQL.open();
			snapshotSQL.deleteTable();
			Sensors_SQLite_Setting.setRate(rates);
		}

	}

	public void startLogging() {
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
						GPSloggerService.class));
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
		}

		// If 'Write to SQLite' is selected
		if (dataConfig[1]) {
			try {
				// for (int j=0; j<13; j++)
				// if (sensorCheck[j]) data2.endTransaction(j);

				// Attempt to copy the database to the sdcard and then close
				// it
				snapshotSQL.copy();
				snapshotSQL.close();
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

		}
		Toast.makeText(
				programContext,
				"Data is stored in: "
						+ SensorAdventureActivity.DataPath.toString() + "/",
				Toast.LENGTH_LONG).show();
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
		rates[6] = rate;
	}

	public void setGravityRate(int rate) {
		rates[7] = rate;
	}

	public void setLinearAccelerometerRate(int rate) {
		rates[8] = rate;
	}

	public void setRotationVectorRate(int rate) {
		rates[9] = rate;
	}

	public void setAmbientTemperatureRate(int rate) {
		rates[10] = rate;
	}

	public void configureMicrophone(int mic, int sample, int channeli,
			int channelo, int format, int stream, int mode) {
		rates[11] = sample;
		record = new ContinuousRecorder(mic, sample, channeli, channelo,
				format, stream, mode, programContext);
	}

	public void setGPSRate(int rate) {
		rates[12] = rate;
	}

	public void onDestroy() {
	}
}
