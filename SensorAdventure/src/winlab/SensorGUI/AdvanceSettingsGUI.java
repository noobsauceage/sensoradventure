package winlab.SensorGUI;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
 
import winlab.sensoradventure.R;
import android.app.ListActivity;
import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import winlab.sensoradventure.*;
public class AdvanceSettingsGUI extends ListActivity implements OnClickListener,OnItemSelectedListener {
	public OnLongClickListener longClickListner;
	LinearLayout panel1, panel2, panel3,panel4, panel5,panel6,panel7;
	TextView text1, text2, text3,text4, text5, text6,text7;
	View openLayout;
	public static ArrayList<ConfigItem> ConfigInfo;
	private Spinner preferredNetworkType;
	private Spinner preferredLoggingrategps;
	private Spinner micsampleingrate;
	private Spinner micchannelinput;
	private Spinner micchannelaudio;
	private Spinner accelerometerate;
	private Spinner gyroscoperate;
	private Spinner magnetometerrate;
	private Spinner othersamplingrate;
	private Spinner servernames;
 
	private String[] mPreferredNetworkLabels = { "GPS", "NETWORK" };

	private String[] mloggingrate = { "1", "5", "10", "30", "60"};

	private String[] micloggingrate = { "44.1", "22.05", "16", "11.025" };

	private String[] micchannelrate = { "MONO", "STEREO"};

	private String[] micchannelencoding = { "16", "8"};

	private String[] Accelerometer = { "1", "5", "10", "30", "60"};
	
	private String[] Gyroscope = { "1", "5", "10", "30", "60"};
	
	private String[] Magnetometer = { "1", "5", "10", "30", "60" };
	
	private String[] othersamplingrates1 = { "1", "5", "10", "30", "60" };
	
	private String[] Server_Names = { "Server1", "Server2", "Server3" };
	
 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.advancesettings);
		panel1 = (LinearLayout) findViewById(R.id.panel1);
		panel2 = (LinearLayout) findViewById(R.id.panel2);
		panel3 = (LinearLayout) findViewById(R.id.panel3);
		panel4 = (LinearLayout) findViewById(R.id.panel4);
		panel5 = (LinearLayout) findViewById(R.id.panel5);
		panel6 = (LinearLayout) findViewById(R.id.panel6);
		panel7 = (LinearLayout) findViewById(R.id.panel7);
		text1 = (TextView) findViewById(R.id.text1);
		text2 = (TextView) findViewById(R.id.text2);
		text3 = (TextView) findViewById(R.id.text3);
		text4 = (TextView) findViewById(R.id.text4);
		text5 = (TextView) findViewById(R.id.text5);
		text6 = (TextView) findViewById(R.id.text6);
		text7 = (TextView) findViewById(R.id.text7);
		text1.setOnClickListener(this);
		text2.setOnClickListener(this);
		text3.setOnClickListener(this);
		text4.setOnClickListener(this);
		text5.setOnClickListener(this);
		text6.setOnClickListener(this);
		text7.setOnClickListener(this);
		WriteConfigFile a = new WriteConfigFile();
		try {
			a.doExport();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		File folder = new File(Environment.getExternalStorageDirectory(),
				"SensorConfig");
		if (folder.exists()) {
			File kmlFile = new File(folder.getPath(), "Config.txt");
			if(kmlFile.exists())
			{
				//Start Reading from kml file
				
			}
			else
			{
				WriteConfigFile writeconfig = new WriteConfigFile();
				try {
					writeconfig.doExport();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else
		{
			folder.mkdirs();
			WriteConfigFile writeconfig = new WriteConfigFile();
			try {
				writeconfig.doExport();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		preferredNetworkType = (Spinner) findViewById(R.id.preferredNetworkType);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, mPreferredNetworkLabels);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		preferredNetworkType.setAdapter(adapter);
		
		preferredLoggingrategps = (Spinner) findViewById(R.id.preferredLoggingrategps);
		ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, mloggingrate);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		preferredLoggingrategps.setAdapter(adapter1);

		micsampleingrate = (Spinner) findViewById(R.id.micsampleingrate);
		ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, micloggingrate);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		micsampleingrate.setAdapter(adapter2);

		micchannelinput = (Spinner) findViewById(R.id.micchannelinput);
		ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, micchannelrate);
		adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		micchannelinput.setAdapter(adapter3);

		micchannelaudio = (Spinner) findViewById(R.id.micchannelaudio);
		ArrayAdapter<String> adapter4 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, micchannelencoding);
		adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		micchannelaudio.setAdapter(adapter4);

		accelerometerate = (Spinner) findViewById(R.id.accelerometerate);
		ArrayAdapter<String> adapter5 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, Accelerometer);
		adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		accelerometerate.setAdapter(adapter5);
		
		gyroscoperate = (Spinner) findViewById(R.id.gyroscoperate);
		ArrayAdapter<String> adapter6 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, Gyroscope);
		adapter6.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		gyroscoperate.setAdapter(adapter6);
  
		magnetometerrate = (Spinner) findViewById(R.id.magnetometerrate);
		ArrayAdapter<String> adapter7 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, Magnetometer);
		adapter7.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		magnetometerrate.setAdapter(adapter7);
		
		othersamplingrate = (Spinner) findViewById(R.id.othersamplingrate);
		ArrayAdapter<String> adapter8 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, othersamplingrates1);
		adapter8.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		othersamplingrate.setAdapter(adapter8);
	
		servernames= (Spinner) findViewById(R.id.Servers);
		ArrayAdapter<String> adapter9 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, Server_Names);
		adapter9.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		servernames.setAdapter(adapter9);
		
		preferredNetworkType.setOnItemSelectedListener(new OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	SensorAdventureActivity.provider = preferredNetworkType.getSelectedItem().toString(); 
		    }
		    public void onNothingSelected(AdapterView<?> parentView) {
		        // your code here
		    }

		});
			
		preferredLoggingrategps.setOnItemSelectedListener(new OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	SensorAdventureActivity.lograte = preferredLoggingrategps.getSelectedItem().toString(); 
		    }
		    public void onNothingSelected(AdapterView<?> parentView) {
		        // your code here
		    }
		});
				
		micsampleingrate.setOnItemSelectedListener(new OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	SensorAdventureActivity.micsampling = micsampleingrate.getSelectedItem().toString(); 
		    }
 
		    public void onNothingSelected(AdapterView<?> parentView) {
		        // your code here
		    }
		});
		
		micchannelinput.setOnItemSelectedListener(new OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	SensorAdventureActivity.micchannel = micchannelinput.getSelectedItem().toString(); 
		    }
		    public void onNothingSelected(AdapterView<?> parentView) {
		        // your code here
		    }
		});
		
		micchannelaudio.setOnItemSelectedListener(new OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	SensorAdventureActivity.micencode = micchannelaudio.getSelectedItem().toString(); 
		    }
		    public void onNothingSelected(AdapterView<?> parentView) {
		        // your code here
		    }
		});
		
		accelerometerate.setOnItemSelectedListener(new OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	SensorAdventureActivity.accelerorate = accelerometerate.getSelectedItem().toString(); 
		    }
		    public void onNothingSelected(AdapterView<?> parentView) {
		        // your code here
		    }
		});
		
		gyroscoperate.setOnItemSelectedListener(new OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	SensorAdventureActivity.gyrorate = gyroscoperate.getSelectedItem().toString(); 
		    }
		    public void onNothingSelected(AdapterView<?> parentView) {
		        // your code here
		    }
		});
		
		magnetometerrate.setOnItemSelectedListener(new OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	SensorAdventureActivity.magnetorate = magnetometerrate.getSelectedItem().toString(); 
		    }
		    public void onNothingSelected(AdapterView<?> parentView) {
		        // your code here
		    }
		});
		
		othersamplingrate.setOnItemSelectedListener(new OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	SensorAdventureActivity.otherlograte = othersamplingrate.getSelectedItem().toString(); 
		    }
		    public void onNothingSelected(AdapterView<?> parentView) {
		        // your code here
		    }
		});
		
		servernames.setOnItemSelectedListener(new OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	SensorAdventureActivity.Servers = servernames.getSelectedItem().toString(); 
		    }
		    public void onNothingSelected(AdapterView<?> parentView) {
		        // your code here
		    }
		});
	}

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
		if (openLayout == panel5)
			panel5.startAnimation(new ScaleAnimToHide(1.0f, 1.0f, 1.0f, 0.0f,
					500, panel5, true));
		if (openLayout == panel6)
			panel6.startAnimation(new ScaleAnimToHide(1.0f, 1.0f, 1.0f, 0.0f,
					500, panel6, true));
		if (openLayout == panel7)
			panel7.startAnimation(new ScaleAnimToHide(1.0f, 1.0f, 1.0f, 0.0f,
					500, panel7, true));

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
			}
			else if (layoutView.getId() == R.id.text4) {
				v = panel4.getVisibility();
				hideThemAll();
				if (v != View.VISIBLE) {
					panel4.startAnimation(new ScaleAnimToShow(1.0f, 1.0f, 1.0f,
							0.0f, 500, panel4, true));
				}
			}
			else if (layoutView.getId() == R.id.text5) {
				v = panel5.getVisibility();
				hideThemAll();
				if (v != View.VISIBLE) {
					panel5.startAnimation(new ScaleAnimToShow(1.0f, 1.0f, 1.0f,
							0.0f, 500, panel5, true));
				}
			}
			else if (layoutView.getId() == R.id.text6) {
				v = panel6.getVisibility();
				hideThemAll();
				if (v != View.VISIBLE) {
					panel6.startAnimation(new ScaleAnimToShow(1.0f, 1.0f, 1.0f,
							0.0f, 500, panel6, true));
				}
			}
			else if (layoutView.getId() == R.id.text7) {
				v = panel7.getVisibility();
				hideThemAll();
				if (v != View.VISIBLE) {
					panel7
					.startAnimation(new ScaleAnimToShow(1.0f, 1.0f, 1.0f,
							0.0f, 500, panel7, true));
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

	/* (non-Javadoc)
	 * @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(android.widget.AdapterView, android.view.View, int, long)
	 */
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		 
		
	}



	/* (non-Javadoc)
	 * @see android.widget.AdapterView.OnItemSelectedListener#onNothingSelected(android.widget.AdapterView)
	 */
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}

 
}