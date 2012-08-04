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
import java.util.Calendar;
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
import android.os.AsyncTask;
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
	public static String Last_GPS_lograte="1";
	public static String Last_micsampling="44.1";
	public static String Servers = "Servers";
	private String fileName = "Save.txt";
	private static String Direc = null;
	private String[] Sensors = { "Accelerometer", "Magnetic", "Orientation",
			"Gyroscope", "Light", "Pressure", "Temperature", "Proximity",
			"Gravity", "L. Accelerometer", "Rotation", "Humidity",
			"A. Temperature", "Microphone", "GPS" };	
	private SensorAdapter sensorAdapter;
	private SensorSetting ok;
	private static Calendar c = Calendar.getInstance();
	private Button start, config, save;
	private ArrayList<Group> groups = new ArrayList<Group>();
	private ArrayList<Child> normalSensor = new ArrayList<Child>();
	private ArrayList<Child> micSensor = new ArrayList<Child>();
	private boolean[] expanded;		// Boolean array that stores memory of which Groups are expanded
	public static File DataPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
	private File path = Environment
			.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
	private File file = new File(path, fileName);
	private int check = 0;	// Int that increments if any sensors are checked 'On'
    
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.guimain);

		// Discover which sensors are available on the device
		ok = new SensorSetting(this);
		ok.testAvailableSensors();
        
		// Create the two different Groups for the GUI
		normalSensor.add(new Child("Update   Rate", "ms"));
		micSensor.add(new Child("Sampling Rate", "Hz"));

		// Try to read the Save.txt file for which CheckBoxes were 'On' 
		try {
			initializeGroups();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Create the expandable list adapter
		// This will populate the GUI for us
		sensorAdapter = new SensorAdapter(this, groups);

		// Try to read the Save.txt file for the saved 'Update Rates'
		// Try to read the Save.txt file for the saved Data Config CB states
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
			// If "Save.txt" does not exist
			if (!file.isFile())
			{
				// If we are not looking at the microphone or GPS
				// We initialize the update rates to the global log rate in Advanced Settings
				if ((i!=13)&&(i!=14))
				SensorAdapter.value[i] = otherlograte;
				// If we are looking at the microphone
				if (i==13)
					SensorAdapter.value[i] =""+(int)(Double.parseDouble(micsampling)*1000);
				// If we are looking at the GPS
				if (i==14)
					SensorAdapter.value[i]=lograte;
			}
			// Initialize expanded values to false
			expanded[i] = false;
		}
		
		Last_lograte = otherlograte;
		Last_GPS_lograte=lograte;
		Last_micsampling=micsampling;
		
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
		// If the global sensor lograte is the same as when we return, then do nothing
		if (Last_lograte.equals(otherlograte)) {
		} 
		// If not the same, then change it
		else {
			for (int i = 0; i < 13; i++) {
				SensorAdapter.value[i] = otherlograte;
				// If the edittext objects have been initialized and if any of the groups are expanded
				if ((sensorAdapter.edittext[i] != null) && (expanded[i]))
					sensorAdapter.edittext[i].setText(otherlograte);
			}
			Last_lograte = otherlograte;
		}
		
		// If the GPS lograte is the same as we return, do nothing
		if (Last_GPS_lograte.equals(lograte)){}
		// If not, change it
		else{
			SensorAdapter.value[14] = lograte;
			if ((sensorAdapter.edittext[14] != null) && (expanded[14]))
				sensorAdapter.edittext[14].setText(lograte);
			Last_GPS_lograte=lograte;
		}
		
		// If the microphone lograte is the same as we return, do nothing
		if (Last_micsampling.equals(micsampling)){}
		// If not, change it
		else {
			SensorAdapter.value[13] = ""+(int)(Double.parseDouble(micsampling)*1000);
			if ((sensorAdapter.edittext[13] != null) && (expanded[13]))
				sensorAdapter.edittext[13].setText(SensorAdapter.value[13]);
			Last_micsampling=micsampling;
		}
		
	}

	// If 'Start Logging' is clicked
	private OnClickListener startClick = new OnClickListener() {
		public void onClick(View v) {
			
			Intent intent = new Intent(SensorAdventureActivity.this,
					StartGUI.class);
			// Create an array of booleans to remember which sensors are checked 'On'
			boolean[] sensorCheck = new boolean[Sensors.length];
			
			// Create an array to ints to remember what the Update/Sampling rates are
			int[] rates = new int[Sensors.length];
			
			// Loop and get the rates & 'On' booleans
			for (int i = 0; i < Sensors.length; i++) {
				sensorCheck[i] = sensorAdapter.checkbox[i].isChecked();
				// If any sensor is checked
				if (sensorCheck[i])
					check++;
				// If the update/sampling rates are not null
				if (SensorAdapter.value[i] != null)
					// Convert rates to integers
					rates[i] = Integer.parseInt(SensorAdapter.value[i]);
				else {
					// If we are looking at the microphone
					if (i==13) 
						rates[i]=(int) (Double.parseDouble(micsampling)*1000);
					// If we are looking at the GPS
					if (i==14) 
						rates[i]=Integer.parseInt(lograte);
					// If we are looking at all other sensors
					if ((i!=13)&&(i!=14)) 
						rates[i]=Integer.parseInt(otherlograte);
				}
			}
			// This block of code passes necessary data to StartGUI
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
			
			// If the data configuration screen onCreate was never called
			if (OptionsGUI.state == null) {
				print(1);
				Intent Data_config = new Intent(SensorAdventureActivity.this,
						OptionsGUI.class);
				startActivity(Data_config);
			} 
			// Else if none of the data configurations were selected
			else if ((OptionsGUI.state[0] == false)
					&& (OptionsGUI.state[1] == false)
					&& (OptionsGUI.state[2] == false)) {
				print(1);
				Intent Data_config = new Intent(SensorAdventureActivity.this,
						OptionsGUI.class);
				startActivity(Data_config);
			} 
			// Else if no sensors are selected
			else if (check == 0) {
				print(2);
			}

			// Else, switch to StartGUI activity
			else
			{
				c = Calendar.getInstance();
				Direc = "/" + Integer.toString(c.get(Calendar.YEAR)) + "_"
						+ Integer.toString(c.get(Calendar.MONTH) + 1) + "_"
						+ Integer.toString(c.get(Calendar.DATE)) + "_"
						+ Integer.toString(c.get(Calendar.HOUR_OF_DAY)) + "Hr_"
						+ Integer.toString(c.get(Calendar.MINUTE)) + "Min_"
						+ Integer.toString(c.get(Calendar.SECOND)) + "Sec/";
				DataPath = Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS
								+ Direc);
				startActivity(intent);
			}
		}
	};

	// Method to perform toast prints, depending on situation
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

	// Switches to OptionsGUI if 'Data Configurations' is clicked
	private OnClickListener configClick = new OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent(SensorAdventureActivity.this,
					OptionsGUI.class);
			startActivity(intent);
		}
	};

	// If the 'Save' button is clicked
	private OnClickListener saveClick = new OnClickListener() {
		public void onClick(View v) {
			try {
				BufferedWriter output = new BufferedWriter(new FileWriter(file));
				for (int i = 0; i < groups.size(); i++) {
					// If the 'On' box is not checked
					if (groups.get(i).getState() == false)
						output.write("0 ");
					// Else, the 'On' box is checked
					else
						output.write("1 ");
				}
				output.newLine();
				// Write the update rates to the file
				for (int i = 0; i < groups.size(); i++) {
					output.write(SensorAdapter.value[i]);
					output.newLine();

				}
				// If onCreate was called for OptionsGUI
				if (OptionsGUI.state != null) {
					for (int i = 0; i < OptionsGUI.state.length; i++) {
						// If one of the Data Config Checkboxes is not checked
						if (OptionsGUI.state[i] == false)
							output.write("0 ");
						// Else, if they are checked
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
		// If a group is expanded, remember so
		expanded[groupPosition] = true;
	}

	public void onGroupCollapse(int groupPosition) {
		update();
		// If a group is collapsed, remember so
		expanded[groupPosition] = false;
		
	}

	// Method that updates the Group & Child views depending on changes in the GUI
	// Where changes can include expanding & collapsing the groups
	public void update() {
		for (int i = 0; i < groups.size(); i++) {
			// Update the boolean associated with a group if they have been checked
			groups.get(i).setState(sensorAdapter.checkbox[i].isChecked());
			// If a group is expanded
			if (expanded[i]) {
				for (int j = 0; j < groups.size(); j++)
					// If the Child's EditText exists and the Group containing Child is expanded
					if ((sensorAdapter.edittext[j] != null) && (expanded[j])) {

						// If the Child EditText is empty
						if (sensorAdapter.edittext[j].getText().toString()
								.length() == 0)
							SensorAdapter.value[j] = null;
						// Else, populate it with the stored value
						else
							SensorAdapter.value[j] = sensorAdapter.edittext[j]
									.getText().toString();
					}
			}
		}

	}

	// Method that pulls 'On' CheckBox information from "Save.txt"
	public void initializeGroups() throws FileNotFoundException {
		Scanner scanner;
		// If "Save.txt" exists, create the Groups and initialize them
		if (file.isFile()) {
			// Pull CB "0" & "1" delimited by a space
			scanner = new Scanner(file).useDelimiter("\\s*");
			for (int i = 0; i < Sensors.length; i++) {
				// If "Save.txt" exists
				if (file.isFile()) {
					int b = scanner.nextInt();
					// If we are not looking at the microphone
					if (i != 13)
						groups.add(new Group(Sensors[i], normalSensor, (b != 0)));
					// If we are looking at microphone
					else
						groups.add(new Group(Sensors[i], micSensor, (b != 0)));
				}
			}

		} 
		// If the File does not exists, create the Groups with default values
		else {
			for (int j = 0; j < Sensors.length; j++) {
				// If we are not looking at the microphone
				if (j != 13)
					groups.add(new Group(Sensors[j], normalSensor, false));
				// If we are looking at the microphone
				else
					groups.add(new Group(Sensors[j], micSensor, false));
			}
		}

	}
	// Method that pulls "Update Rate" & "Sampling Rate" information from "Save.txt"
	public void initializeTexts() throws FileNotFoundException {
		Scanner scanner;
		scanner = new Scanner(file);
		// Skip the first line of CB memory
		scanner.nextLine();
		// If "Save.txt" exists
		if (file.isFile()) {
			for (int i = 0; i < Sensors.length; i++) {
				SensorAdapter.value[i] = scanner.nextLine();

			}
		}
	}
	// Method that pulls information from "Save.txt"
	public void initializeConfig() throws FileNotFoundException {
		Scanner scanner;
		// If "Save.txt" exists
		if (file.isFile()) {
			scanner = new Scanner(file).useDelimiter("\\s*");
			// Go through the 16 lines of CB memory & update rates
			for (int i = 0; i < 16; i++) {
				scanner.nextLine();
			}
			// If OptionGUI onCreate has not been called, we will forcibly create its state array
			if (OptionsGUI.state == null)
				OptionsGUI.state = new boolean[3];
			for (int i = 0; i < OptionsGUI.state.length; i++) {
				// If "Save.txt" exists
				if (file.isFile()) {
					int b = scanner.nextInt();
					OptionsGUI.state[i] = (b != 0);

				}
			}
		}

	}
}
