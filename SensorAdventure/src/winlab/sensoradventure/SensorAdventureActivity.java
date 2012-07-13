package winlab.sensoradventure;

/* This is the main activity for the Sensor Adventure application.
 * Errors with EditText scrolling lost have been (conceivably) fixed.
 * As far as the GUI goes, main bugs have been fixed. Any additional edits
 * to its controls should be to optimize efficiency.
 * -- G.D.C.
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import winlab.SensorGUI.AdvanceSettingsGUI;
import winlab.SensorGUI.Child;
import winlab.SensorGUI.Group;
import winlab.SensorGUI.OptionsGUI;
import winlab.SensorGUI.SensorAdapter;
import winlab.SensorGUI.StartGUI;
import winlab.file.SensorSetting;
import android.app.ExpandableListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class SensorAdventureActivity extends ExpandableListActivity {
	private SensorAdapter sensorAdapter;
	private Button start, config, save;
	private ArrayList<Group> groups = new ArrayList<Group>();
	private ArrayList<Child> normalSensor = new ArrayList<Child>();
	private ArrayList<Child> micSensor = new ArrayList<Child>();
	private boolean[] expanded;
	private String[] Sensors = { "Accelerometer", "Magnetic", "Orientation",
			"Gyroscope", "Light", "Pressure", "Temperature", "Proximity",
			"Gravity", "L. Accelerometer", "Rotation", "Humidity",
			"A. Temperature", "Microphone", "GPS" };
	public static String provider = "GPS";
	public static String lograte = "1";
	public static String micsampling = "44.1";
	public static String micchannel = "MONO";
	public static String micencode = "16";
	public static String accelerorate = "1";
	public static String gyrorate = "1";
	public static String magnetorate = "1";
	public static String otherlograte = "1";
	public static String Last_lograte = "1";
	public static String Servers = "Servers";
	private SensorSetting ok;
	private String fileName = "Save.txt";
	private File path = Environment
			.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
	private File file = new File(path, fileName);
	private int check = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.guimain);

		ok = new SensorSetting(this);
		ok.testAvailableSensors();

		normalSensor.add(new Child("Update   Rate", "ms"));
		micSensor.add(new Child("Sampling Rate", "Hz"));

		try {
			initializeGroups();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		sensorAdapter = new SensorAdapter(this, groups);

		try {
			initializeTexts();
			initializeConfig();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		expanded = new boolean[groups.size()];
		setListAdapter(sensorAdapter);
		for (int i = 0; i < groups.size(); i++) {
			if (!file.isFile())
				SensorAdapter.value[i] = otherlograte;
			expanded[i] = false;
		}
		Last_lograte = otherlograte;
		start = (Button) findViewById(R.id.button1);
		start.setOnClickListener(startClick);
		config = (Button) findViewById(R.id.button2);
		config.setOnClickListener(configClick);
		save = (Button) findViewById(R.id.button3);
		save.setOnClickListener(saveClick);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Toast.makeText(this, "I am called", Toast.LENGTH_LONG).show();
		if (Last_lograte.equals(otherlograte)) {
		} else {
			for (int i = 0; i < groups.size(); i++) {
				SensorAdapter.value[i] = otherlograte;
				if ((sensorAdapter.edittext[i] != null) && (expanded[i]))
					sensorAdapter.edittext[i].setText(otherlograte);

			}
			Last_lograte = otherlograte;
		}
	}

	private OnClickListener startClick = new OnClickListener() {
		public void onClick(View v) {

			Intent intent = new Intent(SensorAdventureActivity.this,
					StartGUI.class);
			boolean[] sensorCheck = new boolean[Sensors.length];
			int[] rates = new int[Sensors.length];
			for (int i = 0; i < Sensors.length; i++) {
				sensorCheck[i] = sensorAdapter.checkbox[i].isChecked();
				if (sensorCheck[i])
					check++;
				if (sensorCheck[i] && SensorAdapter.value[i] != null)
					rates[i] = Integer.parseInt(SensorAdapter.value[i]);
				else if (sensorCheck[i] && SensorAdapter.value[i] == null)
					rates[i] = Integer.parseInt(otherlograte);
			}
			intent.putExtra("Sensors", Sensors);
			intent.putExtra("sensorCheck", sensorCheck);
			intent.putExtra("state", OptionsGUI.state);
			intent.putExtra("rates", rates);
			intent.putExtra("gpsprovide", provider);
			intent.putExtra("gpslograte", lograte);
			intent.putExtra("micsampling", micsampling);
			intent.putExtra("micchannel", micchannel);
			intent.putExtra("micencode", micencode);
			intent.putExtra("accelerorate", accelerorate);
			intent.putExtra("gyrorate", gyrorate);
			intent.putExtra("magnetorate", magnetorate);
			intent.putExtra("otherlograte", otherlograte);
			intent.putExtra("Servers", Servers);
			if (OptionsGUI.state == null) {
				print(1);
				Intent Data_config = new Intent(SensorAdventureActivity.this,
						OptionsGUI.class);
				startActivity(Data_config);
			} else if ((OptionsGUI.state[0] == false)
					&& (OptionsGUI.state[1] == false)
					&& (OptionsGUI.state[2] == false)) {
				print(1);
				Intent Data_config = new Intent(SensorAdventureActivity.this,
						OptionsGUI.class);
				startActivity(Data_config);
			} else if (check == 0) {
				print(2);
			}

			else
				startActivity(intent);
		}
	};

	private void print(int i) {
		switch (i) {
		case 1:
			Toast.makeText(this, "Please select the Data Configuration!",
					Toast.LENGTH_LONG).show();
			break;
		case 2:
			Toast.makeText(this, "Please select a sensor.", Toast.LENGTH_LONG)
					.show();
			break;
		case 3:
			Toast.makeText(this, "Settings have been saved.", Toast.LENGTH_LONG)
			.show();
		}

	}

	private OnClickListener configClick = new OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent(SensorAdventureActivity.this,
					OptionsGUI.class);
			startActivity(intent);
		}
	};

	private OnClickListener saveClick = new OnClickListener() {
		public void onClick(View v) {
			try {
				BufferedWriter output = new BufferedWriter(new FileWriter(file));
				for (int i = 0; i < groups.size(); i++) {
					if (groups.get(i).getState() == false)
						output.write("0 ");
					else
						output.write("1 ");
				}
				output.newLine();
				for (int i = 0; i < groups.size(); i++) {
					output.write(SensorAdapter.value[i]);
					output.newLine();

				}
				if (OptionsGUI.state != null) {
					for (int i = 0; i < OptionsGUI.state.length; i++) {
						if (OptionsGUI.state[i] == false)
							output.write("0 ");
						else
							output.write("1 ");
					}
				}

				output.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			print(3);

		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.settings:
			Intent intent = new Intent(SensorAdventureActivity.this,
					AdvanceSettingsGUI.class);
			startActivity(intent);
		}
		return true;
	}

	public void onContentChanged() {
		super.onContentChanged();
		update();
	}

	public void onGroupExpand(int groupPosition) {
		update();
		expanded[groupPosition] = true;
	}

	public void onGroupCollapse(int groupPosition) {
		update();
		expanded[groupPosition] = false;
	}

	public void update() {
		for (int i = 0; i < groups.size(); i++) {
			groups.get(i).setState(sensorAdapter.checkbox[i].isChecked());
			if (expanded[i]) {
				for (int j = 0; j < groups.size(); j++)
					if ((sensorAdapter.edittext[j] != null) && (expanded[j])) {

						if (sensorAdapter.edittext[j].getText().toString()
								.length() == 0)
							SensorAdapter.value[j] = null;
						else
							SensorAdapter.value[j] = sensorAdapter.edittext[j]
									.getText().toString();
					}
			}
		}

	}

	public void initializeGroups() throws FileNotFoundException {
		Scanner scanner;
		if (file.isFile()) {
			scanner = new Scanner(file).useDelimiter("\\s*");
			for (int i = 0; i < Sensors.length; i++) {

				if (file.isFile()) {
					int b = scanner.nextInt();
					if (i != 13)
						groups.add(new Group(Sensors[i], normalSensor, (b != 0)));
					else
						groups.add(new Group(Sensors[i], micSensor, (b != 0)));
				}
			}

		} else {
			for (int j = 0; j < Sensors.length; j++) {
				if (j != 13)
					groups.add(new Group(Sensors[j], normalSensor, false));
				else
					groups.add(new Group(Sensors[j], micSensor, false));
			}
		}

	}

	public void initializeTexts() throws FileNotFoundException {
		Scanner scanner;
		scanner = new Scanner(file);
		scanner.nextLine();
		if (file.isFile()) {
			for (int i = 0; i < Sensors.length; i++) {
				SensorAdapter.value[i] = scanner.nextLine();

			}
		}
	}

	public void initializeConfig() throws FileNotFoundException {
		Scanner scanner;
		if (file.isFile()) {
			scanner = new Scanner(file).useDelimiter("\\s*");
			for (int i = 0; i < 16; i++) {
				scanner.nextLine();
			}
			if (OptionsGUI.state == null)
				OptionsGUI.state = new boolean[3];
			for (int i = 0; i < OptionsGUI.state.length; i++) {
				if (file.isFile()) {
					int b = scanner.nextInt();
					System.out.println(b);
					OptionsGUI.state[i] = (b != 0);

				}
			}
		}

	}
}
