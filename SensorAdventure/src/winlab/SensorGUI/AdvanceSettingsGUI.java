package winlab.SensorGUI;

/*This program is what creates the GUI for the Advanced Settings
 * option under the Android menu button.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
//import java.util.HashMap;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import winlab.sensoradventure.R;
import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import winlab.sensoradventure.*;
import winlab.sensoradventure.gps.AdditionalFeaturesGPS;
/**
 * @author malathidharmalingam
 * Version 1.0
 * This is the AdvanceSettings Screen for setting Sensor Configurations
 * GPS, Microphone and the "Other Sensors" in the Phone
 * It can also start the Additional GPS Features activity
 * and also displays General Notification to User.
 */
public class AdvanceSettingsGUI extends ListActivity implements
		OnClickListener, OnItemSelectedListener {
	//There are Four Panels and Texts for GPS,Microphone,Other Sensors and Servers Respectively
	LinearLayout panel1, panel2, panel3, panel4;
	TextView text1, text2, text3, text4;
	private TextView Notification;
	View openLayout;
	//GPS Configurations
	private Spinner preferredNetworkType;
	private Spinner preferredLoggingrategps;
	//Microphone Configurations
	private Spinner micsampleingrate;
	private Spinner micchannelinput;
	private Spinner micchannelaudio;
	//Other Sampling Rate
	private Spinner othersamplingrate;
	//Server Names
	private Spinner servernames;

	private String[] mPreferredNetworkLabels = { "GPS", "NETWORK" };
	private String[] mloggingrate = { "1", "5", "10", "30", "60" };
	private String[] micloggingrate = { "44.1", "22.05", "16", "11.025" };
	private String[] micchannelrate = { "MONO", "STEREO" };
	private String[] micchannelencoding = { "16", "8" };
	private String[] othersamplingrates1 = { "1", "5", "10", "30", "60" };
	private String[] Server_Names = { "Server1", "Server2", "Server3" };
	Button addition_gps_features;
	private static String[]  file_default_string;
	private int selection = 0;
	StringBuilder text = new StringBuilder();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.advancesettings);
		// Each linear layout contains a drop down menu
		// One LL for each "Advanced Settings" option
		panel1 = (LinearLayout) findViewById(R.id.panel1);
		panel2 = (LinearLayout) findViewById(R.id.panel2);
		panel3 = (LinearLayout) findViewById(R.id.panel3);
		panel4 = (LinearLayout) findViewById(R.id.panel4);

		text1 = (TextView) findViewById(R.id.text1);
		text2 = (TextView) findViewById(R.id.text2);
		text3 = (TextView) findViewById(R.id.text3);
		text4 = (TextView) findViewById(R.id.text4);

		text1.setOnClickListener(this);
		text2.setOnClickListener(this);
		text3.setOnClickListener(this);
		text4.setOnClickListener(this);
		
		Notification =  (TextView) findViewById(R.id.textnotification);

		addition_gps_features = (Button) findViewById(R.id.Additional_GPS_Features);
		//This is for Reading Notification File
				Resources res = getResources();
		file_default_string = res.getStringArray(R.array.gps_default);
		File sdcard = Environment.getExternalStorageDirectory();
		File file2 = new File(sdcard,file_default_string[5]);
		try {
			BufferedReader br = new BufferedReader(new FileReader(file2));
			String line;

			while ((line = br.readLine()) != null) {
				text.append(line);
				text.append('\n');
			}
		}
		catch (IOException e) {
			//You'll need to add proper error handling here
		}
		
		//Reads Notification File for User
		Notification.setText(text); 
		File folder = new File(Environment.getExternalStorageDirectory(),
				file_default_string[6]);

		// If the "SensorConfig" folder exists in Download Directory
		if (folder.exists()) {
			File kmlFile = new File(folder.getPath(),file_default_string[7]);
			// If "Config.txt" exists, use XML parser
			if (kmlFile.exists()) {
				ReadConf readparse = new ReadConf();
				try {
					readparse.parseXML();
				} catch (XmlPullParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// Pull information from the parser into variables
				String mic_sam = readparse.getmicsample();
				micloggingrate = null;
				micloggingrate = mic_sam.split(",");

				String mic_chan = readparse.getmicchannel();
				micchannelrate = null;
				micchannelrate = mic_chan.split(",");

				String mic_enco = readparse.getmicaudio();
				micchannelencoding = null;
				micchannelencoding = mic_enco.split(",");

				String gps_pr = readparse.getgpsprov();
				mPreferredNetworkLabels = null;
				mPreferredNetworkLabels = gps_pr.split(",");

				String gps_log = readparse.getgpslog();
				mloggingrate = null;
				mloggingrate = gps_log.split(",");

				String other_log = readparse.getotherlog();
				othersamplingrates1 = null;
				othersamplingrates1 = other_log.split(",");

			}
			// If "Config.txt" does not exist, use WCF class to create it
			else {
				WriteConfigFile writeconfig = new WriteConfigFile();
				try {
					writeconfig.doExport();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		// If the "SensorConfig" folder does not exist, create it and
		// subsequently create the config file.
		else {
			folder.mkdirs();
			WriteConfigFile writeconfig = new WriteConfigFile();
			try {
				writeconfig.doExport();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Create the fields for GPS settings
		preferredNetworkType = (Spinner) findViewById(R.id.preferredNetworkType);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, mPreferredNetworkLabels);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		preferredNetworkType.setAdapter(adapter);
		selection = 0;

		// Loop through the fields
		for (int k = 0; k < mPreferredNetworkLabels.length; k++)
			// If the current value of GPS provider in main activity is the same
			// as one of the providers in array, set the display as this GPS
			// provider
			if (mPreferredNetworkLabels[k]
					.equals(SensorAdventureActivity.provider))
				selection = k;
		preferredNetworkType.setSelection(selection);

		preferredLoggingrategps = (Spinner) findViewById(R.id.preferredLoggingrategps);
		ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, mloggingrate);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		preferredLoggingrategps.setAdapter(adapter1);
		selection = 0;

		for (int k = 0; k < mloggingrate.length; k++)
			// If the current value of GPS logging rate in main activity is the
			// same
			// as one of the logging rates in array, set the display as this
			// rate
			if (mloggingrate[k].equals(SensorAdventureActivity.lograte))
				selection = k;
		preferredLoggingrategps.setSelection(selection);

		micsampleingrate = (Spinner) findViewById(R.id.micsampleingrate);
		ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, micloggingrate);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		micsampleingrate.setAdapter(adapter2);
		selection = 0;

		for (int k = 0; k < micloggingrate.length; k++)
			// If the current value of microphone sampling rate in main activity
			// is the same
			// as one of the sampling rates in array, set the display as this
			// rate
			if (micloggingrate[k].equals(SensorAdventureActivity.micsampling))
				selection = k;
		micsampleingrate.setSelection(selection);

		micchannelinput = (Spinner) findViewById(R.id.micchannelinput);
		ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, micchannelrate);
		adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		micchannelinput.setAdapter(adapter3);
		selection = 0;

		for (int k = 0; k < micchannelrate.length; k++)
			// If the current value of microphone channel rate in main activity
			// is the same
			// as one of the channel rates in array, set the display as this
			// rate
			if (micchannelrate[k].equals(SensorAdventureActivity.micchannel))
				selection = k;
		micchannelinput.setSelection(selection);

		micchannelaudio = (Spinner) findViewById(R.id.micchannelaudio);
		ArrayAdapter<String> adapter4 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, micchannelencoding);
		adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		micchannelaudio.setAdapter(adapter4);
		selection = 0;

		for (int k = 0; k < micchannelencoding.length; k++)
			// If the current value of microphone encoding in main activity is
			// the same
			// as the encoding in array, set the display as this encoding
			if (micchannelencoding[k].equals(SensorAdventureActivity.micencode))
				selection = k;
		micchannelaudio.setSelection(selection);

		othersamplingrate = (Spinner) findViewById(R.id.othersamplingrate);
		ArrayAdapter<String> adapter8 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, othersamplingrates1);
		adapter8.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		othersamplingrate.setAdapter(adapter8);
		selection = 0;

		for (int k = 0; k < othersamplingrates1.length; k++)
			// If the current value of global sensor sampling rate in main
			// activity
			// is the same as one of them in array, set the display as this rate
			if (othersamplingrates1[k]
					.equals(SensorAdventureActivity.otherlograte))
				selection = k;
		othersamplingrate.setSelection(selection);

		servernames = (Spinner) findViewById(R.id.Servers);
		ArrayAdapter<String> adapter9 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, Server_Names);
		adapter9.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		servernames.setAdapter(adapter9);

		// Set the listeners for each possible click
		preferredNetworkType.setOnItemSelectedListener(networkClick);
		preferredLoggingrategps.setOnItemSelectedListener(gpsLogClick);
		micsampleingrate.setOnItemSelectedListener(micSampleClick);
		micchannelinput.setOnItemSelectedListener(channelClick);
		micchannelaudio.setOnItemSelectedListener(audioClick);
		othersamplingrate.setOnItemSelectedListener(globalSampleClick);
		servernames.setOnItemSelectedListener(serverClick);
		addition_gps_features.setOnClickListener(addGPSClick);
	}

	private OnItemSelectedListener networkClick = new OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> parentView,
				View selectedItemView, int position, long id) {
			SensorAdventureActivity.provider = preferredNetworkType
					.getSelectedItem().toString();
		}

		public void onNothingSelected(AdapterView<?> parentView) {
			// your code here
		}
	};

	private OnItemSelectedListener gpsLogClick = new OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> parentView,
				View selectedItemView, int position, long id) {
			SensorAdventureActivity.lograte = preferredLoggingrategps
					.getSelectedItem().toString();
		}

		public void onNothingSelected(AdapterView<?> parentView) {
			// your code here
		}
	};

	private OnItemSelectedListener micSampleClick = new OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> parentView,
				View selectedItemView, int position, long id) {
			SensorAdventureActivity.micsampling = micsampleingrate
					.getSelectedItem().toString();
		}

		public void onNothingSelected(AdapterView<?> parentView) {
			// your code here
		}
	};

	private OnItemSelectedListener channelClick = new OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> parentView,
				View selectedItemView, int position, long id) {
			SensorAdventureActivity.micchannel = micchannelinput
					.getSelectedItem().toString();
		}

		public void onNothingSelected(AdapterView<?> parentView) {
			// your code here
		}
	};

	private OnItemSelectedListener audioClick = new OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> parentView,
				View selectedItemView, int position, long id) {
			SensorAdventureActivity.micencode = micchannelaudio
					.getSelectedItem().toString();
		}

		public void onNothingSelected(AdapterView<?> parentView) {
			// your code here
		}
	};

	private OnItemSelectedListener globalSampleClick = new OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> parentView,
				View selectedItemView, int position, long id) {
			SensorAdventureActivity.otherlograte = othersamplingrate
					.getSelectedItem().toString();
		}

		public void onNothingSelected(AdapterView<?> parentView) {
			// your code here
		}
	};

	private OnItemSelectedListener serverClick = new OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> parentView,
				View selectedItemView, int position, long id) {
			SensorAdventureActivity.Servers = servernames.getSelectedItem()
					.toString();
		}

		public void onNothingSelected(AdapterView<?> parentView) {
			// your code here
		}
	};

	private OnClickListener addGPSClick = new OnClickListener() {

		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(),
					AdditionalFeaturesGPS.class);
			startActivity(intent);
		}
	};

	// All the methods below this are methods that control the animation
	// speed of the expanding option & control which groups are expanded
	// and which are not.
	public void onClick(View v) {
		hideOthers(v);
	}

	private void hideThemAll() {
		if (openLayout == null)
			return;
		if (openLayout == panel1)
			panel1.startAnimation(new ScaleAnimToHide(1.0f, 1.0f, 1.0f, 0.0f,
					500, panel1, true));
		if (openLayout == panel2)
			panel2.startAnimation(new ScaleAnimToHide(1.0f, 1.0f, 1.0f, 0.0f,
					500, panel2, true));
		if (openLayout == panel3)
			panel3.startAnimation(new ScaleAnimToHide(1.0f, 1.0f, 1.0f, 0.0f,
					500, panel3, true));
		if (openLayout == panel4)
			panel4.startAnimation(new ScaleAnimToHide(1.0f, 1.0f, 1.0f, 0.0f,
					500, panel4, true));
	}

	private void getUriListForImages1() {
		Toast.makeText(this, "Folder does not exist or is Empty",
				Toast.LENGTH_SHORT).show();
	}

	private void getUriListForImages2() {
		Toast.makeText(this, "Fill Folder Name", Toast.LENGTH_SHORT).show();
	}

	private void hideOthers(View layoutView) {
		{
			int v;
			if (layoutView.getId() == R.id.text1) {
				v = panel1.getVisibility();
				if (v != View.VISIBLE) {
					panel1.setVisibility(View.VISIBLE);
					Log.v("CZ", "height..." + panel1.getHeight());
				}

				hideThemAll();
				if (v != View.VISIBLE) {
					panel1.startAnimation(new ScaleAnimToShow(1.0f, 1.0f, 1.0f,
							0.0f, 500, panel1, true));
				}
			} else if (layoutView.getId() == R.id.text2) {
				v = panel2.getVisibility();
				hideThemAll();
				if (v != View.VISIBLE) {
					panel2.startAnimation(new ScaleAnimToShow(1.0f, 1.0f, 1.0f,
							0.0f, 500, panel2, true));
				}
			}

			else if (layoutView.getId() == R.id.text3) {
				v = panel3.getVisibility();
				hideThemAll();
				if (v != View.VISIBLE) {
					panel3.startAnimation(new ScaleAnimToShow(1.0f, 1.0f, 1.0f,
							0.0f, 500, panel3, true));
				}
			} else if (layoutView.getId() == R.id.text4) {
				v = panel4.getVisibility();
				hideThemAll();
				if (v != View.VISIBLE) {
					panel4.startAnimation(new ScaleAnimToShow(1.0f, 1.0f, 1.0f,
							0.0f, 500, panel4, true));
				}
			}

		}
	}

	public class ScaleAnimToHide extends ScaleAnimation {
		private View mView;

		private LayoutParams mLayoutParams;

		private int mMarginBottomFromY, mMarginBottomToY;

		private boolean mVanishAfter = false;

		public ScaleAnimToHide(float fromX, float toX, float fromY, float toY,
				int duration, View view, boolean vanishAfter) {
			super(fromX, toX, fromY, toY);
			setDuration(duration);
			openLayout = null;
			mView = view;
			mVanishAfter = vanishAfter;
			mLayoutParams = (LayoutParams) view.getLayoutParams();
			int height = mView.getHeight();
			mMarginBottomFromY = (int) (height * fromY)
					+ mLayoutParams.bottomMargin - height;
			mMarginBottomToY = (int) (0 - ((height * toY) + mLayoutParams.bottomMargin))
					- height;

			Log.v("CZ", "height..." + height + " , mMarginBottomFromY...."
					+ mMarginBottomFromY + " , mMarginBottomToY.."
					+ mMarginBottomToY);
		}

		@Override
		protected void applyTransformation(float interpolatedTime,
				Transformation t) {

			super.applyTransformation(interpolatedTime, t);
			if (interpolatedTime < 1.0f) {
				int newMarginBottom = mMarginBottomFromY
						+ (int) ((mMarginBottomToY - mMarginBottomFromY) * interpolatedTime);
				mLayoutParams.setMargins(mLayoutParams.leftMargin,
						mLayoutParams.topMargin, mLayoutParams.rightMargin,
						newMarginBottom);
				mView.getParent().requestLayout();
			} else if (mVanishAfter) {
				mView.setVisibility(View.GONE);
			}
		}
	}

	public class ScaleAnimToShow extends ScaleAnimation {

		private View mView;

		private LayoutParams mLayoutParams;

		private int mMarginBottomFromY, mMarginBottomToY;

		private boolean mVanishAfter = false;

		public ScaleAnimToShow(float toX, float fromX, float toY, float fromY,
				int duration, View view, boolean vanishAfter) {
			super(fromX, toX, fromY, toY);
			openLayout = view;
			setDuration(duration);
			mView = view;
			mVanishAfter = vanishAfter;
			mLayoutParams = (LayoutParams) view.getLayoutParams();
			mView.setVisibility(View.VISIBLE);
			int height = mView.getHeight();
			mMarginBottomFromY = 0;
			mMarginBottomToY = height;

		}

		@Override
		protected void applyTransformation(float interpolatedTime,
				Transformation t) {
			super.applyTransformation(interpolatedTime, t);
			if (interpolatedTime < 1.0f) {
				int newMarginBottom = (int) ((mMarginBottomToY - mMarginBottomFromY) * interpolatedTime)
						- mMarginBottomToY;
				mLayoutParams.setMargins(mLayoutParams.leftMargin,
						mLayoutParams.topMargin, mLayoutParams.rightMargin,
						newMarginBottom);
				mView.getParent().requestLayout();
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.AdapterView.OnItemSelectedListener#onItemSelected(android
	 * .widget.AdapterView, android.view.View, int, long)
	 */
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.AdapterView.OnItemSelectedListener#onNothingSelected(android
	 * .widget.AdapterView)
	 */
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

}