package winlab.file;

import java.io.File;
import java.io.FileWriter;

import winlab.sensoradventure.SensorAdventureActivity;
import winlab.sql.Sensors_SQLite_Setting;
import winlab.sql.SnapShot_SQL;
import android.annotation.TargetApi;
import android.hardware.Sensor;

public class SnapShotValue {
	public static double[][] instantValue = new double[13][];
	private static String fileName = "Instant_Reading.txt";
	private static String time = "";
	private static String markFile[] = { "Accelerometer.txt",
			"MagneticField.txt", "Orientation.txt", "Gyroscope.txt",
			"Light.txt", "Pressure.txt", "Temperature.txt", "Proximity.txt",
			"Gravity.txt", "Linear_Acceleration.txt", "Rotation_Vector.txt",
			"Humidity.txt", "Ambient_Temperature.txt" };
	private static File path;
	private static File file;
	private static File otherFile[] = new File[13];
	private static FileWriter output;
	private static boolean flag = true;

	public SnapShotValue() {
	}

	// This method prepares the arrays for file writing.
	public static void set() {
		for (int i = 0; i < 13; i++)
			// If we are using a sensor that has three printable fields,
			// we give an array of size 3.
			switch (i + 1) {
			case Sensor.TYPE_ACCELEROMETER:
			case Sensor.TYPE_MAGNETIC_FIELD:
			case Sensor.TYPE_ORIENTATION:
			case Sensor.TYPE_GYROSCOPE:
			case Sensor.TYPE_GRAVITY:
			case Sensor.TYPE_LINEAR_ACCELERATION:
				instantValue[i] = new double[3];
				for (int j = 0; j < 3; j++)
					instantValue[i][j] = 0;
				break;
			// If we are using a sensor that has four printable fields,
			// we give an array of size 4.
			case Sensor.TYPE_ROTATION_VECTOR:
				instantValue[i] = new double[4];
				for (int j = 0; j < 4; j++)
					instantValue[i][j] = 0;
				break;
			// All other cases default to one printable field.
			default:
				instantValue[i] = new double[1];
				instantValue[i][0] = 0;
				break;
			}
	}

	// This method resets all of the values in each array after
	// the snapshot is finished.
	public static void reset() {
		for (int i = 0; i < 13; i++)
			// If a sensor that has three printable fields
			switch (i + 1) {
			case Sensor.TYPE_ACCELEROMETER:
			case Sensor.TYPE_MAGNETIC_FIELD:
			case Sensor.TYPE_ORIENTATION:
			case Sensor.TYPE_GYROSCOPE:
			case Sensor.TYPE_GRAVITY:
			case Sensor.TYPE_LINEAR_ACCELERATION:
				for (int j = 0; j < 3; j++)
					instantValue[i][j] = 0;
				break;
			// If a sensor has 4 printable fields
			case Sensor.TYPE_ROTATION_VECTOR:
				for (int j = 0; j < 4; j++)
					instantValue[i][j] = 0;
				break;
			// Else, default to one
			default:
				instantValue[i][0] = 0;
				break;
			}

	}

	// This method retrieves the instant value for a sensor.
	public static double[] getInstVal(int sensorType) {
		return instantValue[sensorType - 1];
	}

	// This method performs the file writing portion of the Mark event.
	public static void print() {
		path = SensorAdventureActivity.DataPath;
		file = new File(path, fileName);
		String str = "";
		str = str + "Timestamp (ms): "
				+ String.format("%d", System.currentTimeMillis()) + "\n";
		time = String.format("%d", System.currentTimeMillis());
		try {
			path.mkdirs();
			file.setWritable(true);
			// If the FileWriter does not already exist
			if (flag)
				output = new FileWriter(file);
			else
				output = new FileWriter(file, true);
			flag = false;
			for (int i = 0; i < 13; i++)
				// If a given sensor is set as 'On'
				if (SensorSetting.sensors[i]) {
					switch (i + 1) {
					case Sensor.TYPE_ACCELEROMETER:
						str = str
								+ "Accelerometer x (m/s^2): "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][0])
								+ "\n";
						str = str
								+ "Accelerometer y (m/s^2): "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][1])
								+ "\n";
						str = str
								+ "Accelerometer z (m/s^2): "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][2])
								+ "\n";
						break;
					case Sensor.TYPE_MAGNETIC_FIELD:
						str = str
								+ "Magnetic Field x (uT): "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][0])
								+ "\n";
						str = str
								+ "Magnetic Field y (uT): "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][1])
								+ "\n";
						str = str
								+ "Magnetic Field z (uT): "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][2])
								+ "\n";
						break;
					case Sensor.TYPE_ORIENTATION:
						str = str
								+ "Orientation x (degrees): "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][0])
								+ "\n";
						str = str
								+ "Orientation y (degrees): "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][1])
								+ "\n";
						str = str
								+ "Orientation z (degrees): "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][2])
								+ "\n";
						break;
					case Sensor.TYPE_GYROSCOPE:
						str = str
								+ "Gyroscope x (rad/s): "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][0])
								+ "\n";
						str = str
								+ "Gyroscope y (rad/s): "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][1])
								+ "\n";
						str = str
								+ "Gyroscope z (rad/s): "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][2])
								+ "\n";

						break;
					case Sensor.TYPE_LIGHT:
						str = str
								+ "Light (lx): "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][0])
								+ "\n";
						break;
					case Sensor.TYPE_PRESSURE:
						str = str
								+ "Pressure (hPa): "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][0])
								+ "\n";
						break;
					case Sensor.TYPE_TEMPERATURE:
						str = str
								+ "Device Temperature (degree Celsius): "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][0])
								+ "\n";
						break;
					case Sensor.TYPE_PROXIMITY:
						str = str
								+ "Proximity (cm)"
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][0])
								+ "\n";
						break;

					case Sensor.TYPE_GRAVITY:
						str = str
								+ "Gravity x (m/s^2): "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][0])
								+ "\n";
						str = str
								+ "Gravity x (m/s^2): "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][1])
								+ "\n";
						str = str
								+ "Gravity x (m/s^2): "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][2])
								+ "\n";

						break;
					case Sensor.TYPE_LINEAR_ACCELERATION:
						str = str
								+ "Linear Accelerometer x (m/s^2): "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][0])
								+ "\n";
						str = str
								+ "Linear Accelerometer y (m/s^2): "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][1])
								+ "\n";
						str = str
								+ "Linear Accelerometer z (m/s^2): "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][2])
								+ "\n";

						break;
					case Sensor.TYPE_ROTATION_VECTOR:
						str = str
								+ "Rotation Vector x unitless: "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][0])
								+ "\n";
						str = str
								+ "Rotation Vector y unitless: "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][1])
								+ "\n";
						str = str
								+ "Rotation Vector z unitless: "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][2])
								+ "\n";
						if (Math.abs(SnapShotValue.instantValue[i][3] - 0) < 1.0e-15)
							str = str
									+ "Rotation Vector scalar:                NA\n";
						else
							str = str
									+ "Rotation Vector scalar: "
									+ String.format("%17.10f",
											SnapShotValue.instantValue[i][3])
									+ "\n";

						break;
					case 12: // Sensor.TYPE_RELATIVE_HUMIDITY
						str = str
								+ "Relative Humidity %: "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][0])
								+ "\n";
						break;
					case 13: // Sensor.TYPE_AMBIENT_TEMPERATURE
						str = str
								+ "Ambient air temperature (degree Celsius): "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][0])
								+ "\n";
						break;
					}
					str = str + "\n";
				}
			str = str
					+ "-----------------------------------------------------\n";
			output.write(str);
			output.close();
		} catch (Exception e) {
		}
		print_others(time);
	}

	// This method prints the marker in the file.
	@TargetApi(9)
	private static void print_others(String sysTime) {
		for (int i = 0; i < 13; i++)
			otherFile[i] = new File(path, markFile[i]);
		for (int i = 0; i < 13; i++)
			// If a sensor has been selected
			if (SensorSetting.sensors[i]) {
				try {
					path.mkdirs();
					otherFile[i].setWritable(true);
					output = new FileWriter(otherFile[i], true);
					output.write("\n" + sysTime
							+ "***********MARK*********************");
					output.close();
				} catch (Exception e) {
				}
			}
	}

	// This method inserts the Marker into a seperate SQLite database.
	public static void insertSQL(SnapShot_SQL instant) {
		String timestamp, str1, str2, str3, str4;
		for (int i = 0; i < 13; i++)
			// If a sensor is selected for SQLite insertion
			if (Sensors_SQLite_Setting.sensors[i])
				switch (i + 1) {
				case Sensor.TYPE_ACCELEROMETER:
				case Sensor.TYPE_MAGNETIC_FIELD:
				case Sensor.TYPE_ORIENTATION:
				case Sensor.TYPE_GYROSCOPE:
				case Sensor.TYPE_GRAVITY:
				case Sensor.TYPE_LINEAR_ACCELERATION:
					timestamp = String.format("%d", System.currentTimeMillis());
					str1 = String.format("%.10f", instantValue[i][0]);
					str2 = String.format("%.10f", instantValue[i][1]);
					str3 = String.format("%.10f", instantValue[i][2]);
					instant.insertTitle1(timestamp, str1, str2, str3, i);
					break;
				case Sensor.TYPE_LIGHT:
				case Sensor.TYPE_PRESSURE:
				case Sensor.TYPE_TEMPERATURE:
				case Sensor.TYPE_PROXIMITY:
				case 12:
				case 13:
					timestamp = String.format("%d", System.currentTimeMillis());
					str1 = String.format("%.10f", instantValue[i][0]);
					instant.insertTitle2(timestamp, str1, i);
					break;
				case Sensor.TYPE_ROTATION_VECTOR:
					timestamp = String.format("%d", System.currentTimeMillis());
					str1 = String.format("%.10f", instantValue[i][0]);
					str2 = String.format("%.10f", instantValue[i][1]);
					str3 = String.format("%.10f", instantValue[i][2]);
					if (Math.abs(SnapShotValue.instantValue[i][3] - 0) < 1.0e-15)
						str4 = "NA";
					else
						str4 = String.format("%.10f", instantValue[i][3]);
					instant.insertTitle3(timestamp, str1, str2, str3, str4, i);
					break;
				}
	}
}
