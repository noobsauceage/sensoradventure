package winlab.SensorGUI;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;
 
import winlab.sensoradventure.R;
import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import winlab.sensoradventure.*;
import winlab.sensoradventure.gps.AppLog;
import android.widget.Toast;
@SuppressLint("ParserError")
public class AdvanceSettingsGUI extends ListActivity implements OnClickListener,OnItemSelectedListener {
	public OnLongClickListener longClickListner;
	LinearLayout panel1, panel2, panel3,panel4, panel5,panel6,panel7;
	TextView text1, text2, text3,text4, text5, text6,text7;
	View openLayout;
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
    List<Uri>  urilist = new ArrayList();
    String Folder_check;
    String email_check;
    String [] all_emails;
	private String[] othersamplingrates1 = { "1", "5", "10", "30", "60" };
	
	private String[] Server_Names = { "Server1", "Server2", "Server3" };
	Button emailbutton;
    public  EditText folderupload;
    public  EditText emailAddress;
    private int selection=0;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.advancesettings);
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
	    emailbutton = (Button)findViewById(R.id.button1);
	    folderupload = (EditText) findViewById(R.id.folderupload);
	    emailAddress = (EditText) findViewById(R.id.emailAddress);
		 Folder_check = folderupload.getText().toString();
		 email_check = emailAddress.getText().toString();
		 Log.v(Folder_check,Folder_check);

		File folder = new File(Environment.getExternalStorageDirectory(),
				"SensorConfig");
		if (folder.exists()) {
			File kmlFile = new File(folder.getPath(), "Config.txt");
			if(kmlFile.exists())
			{
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
				
				String mic_sam= readparse.getmicsample();
				micloggingrate = null;
				micloggingrate = mic_sam.split(",");
				
				String mic_chan= readparse.getmicchannel();
				micchannelrate = null;
				micchannelrate = mic_chan.split(",");
				
				String mic_enco= readparse.getmicaudio();
				micchannelencoding = null;
				micchannelencoding = mic_enco.split(",");
				
				String gps_pr= readparse.getgpsprov();
				mPreferredNetworkLabels = null;
				mPreferredNetworkLabels = gps_pr.split(",");
				
				String gps_log= readparse.getgpslog();
				mloggingrate = null;
				mloggingrate = gps_log.split(",");
				
				String other_log = readparse.getotherlog();
				othersamplingrates1 = null;
				othersamplingrates1 = other_log.split(",");
				
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
		selection=0;
		for (int k=0;k<mPreferredNetworkLabels.length;k++)
			if (mPreferredNetworkLabels[k].equals(SensorAdventureActivity.provider))
				selection=k;
		preferredNetworkType.setSelection(selection);
		
		
		preferredLoggingrategps = (Spinner) findViewById(R.id.preferredLoggingrategps);
		ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, mloggingrate);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		preferredLoggingrategps.setAdapter(adapter1);
		selection=0;
		for (int k=0;k<mloggingrate.length;k++)
			if (mloggingrate[k].equals(SensorAdventureActivity.lograte))
				selection=k;
		preferredLoggingrategps.setSelection(selection);
		
		
		micsampleingrate = (Spinner) findViewById(R.id.micsampleingrate);
		ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, micloggingrate);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		micsampleingrate.setAdapter(adapter2);
		selection=0;
		for (int k=0;k<micloggingrate.length;k++)
			if (micloggingrate[k].equals(SensorAdventureActivity.micsampling))
				selection=k;
		micsampleingrate.setSelection(selection);

				
		micchannelinput = (Spinner) findViewById(R.id.micchannelinput);
		ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, micchannelrate);
		adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		micchannelinput.setAdapter(adapter3);
		selection=0;
		for (int k=0;k<micchannelrate.length;k++)
			if (micchannelrate[k].equals(SensorAdventureActivity.micchannel))
				selection=k;
		micchannelinput.setSelection(selection);
		
		
		micchannelaudio = (Spinner) findViewById(R.id.micchannelaudio);
		ArrayAdapter<String> adapter4 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, micchannelencoding);
		adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		micchannelaudio.setAdapter(adapter4);
		selection=0;
		for (int k=0;k<micchannelencoding.length;k++)
			if (micchannelencoding[k].equals(SensorAdventureActivity.micchannel))
				selection=k;
		micchannelaudio.setSelection(selection);
 
		othersamplingrate = (Spinner) findViewById(R.id.othersamplingrate);
		ArrayAdapter<String> adapter8 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, othersamplingrates1);
		adapter8.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		othersamplingrate.setAdapter(adapter8);
		selection=0;
		for (int k=0;k<othersamplingrates1.length;k++)
			if (othersamplingrates1[k].equals(SensorAdventureActivity.otherlograte))
				selection=k;
		othersamplingrate.setSelection(selection);
		
		
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
		emailbutton.setOnClickListener(new OnClickListener() {

	        public void onClick(View v) {
	            // TODO Auto-generated method stub
	        	 folderupload = (EditText) findViewById(R.id.folderupload);
	     	     emailAddress = (EditText) findViewById(R.id.emailAddress);
	     		 Folder_check = folderupload.getText().toString();
	     		 email_check = emailAddress.getText().toString();
	        	if( folderupload.getText().length() !=0)
	        	{
	        	Intent sendIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
	        	sendIntent.setType("plain/text");
	        	email_check = emailAddress.getText().toString();
	        	all_emails = email_check.split(",");
	        	sendIntent.putExtra(Intent.EXTRA_EMAIL,all_emails);
	        	sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Sensor Files");

	        	ArrayList<Uri> uriList = getUriListForImages();
	        	if(!uriList.isEmpty())
	        	{
	        	 sendIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList);
	        	 startActivity(Intent.createChooser(sendIntent, "Email:"));
	        	}
	        	else
	        	{
	        		 getUriListForImages1();
	        	}
	        	}
	        	else
	        	{
	        		getUriListForImages2();
	        	}
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
	}

	private void getUriListForImages1()
	{
		Toast.makeText(this, "Folder does not exist or is Empty", Toast.LENGTH_SHORT).show();
	}
	
	private void getUriListForImages2()
	{
		Toast.makeText(this, "Fill Folder Name", Toast.LENGTH_SHORT).show();
	}
	
	private ArrayList<Uri> getUriListForImages()  {

	    ArrayList<Uri> uriList = new ArrayList<Uri>();
	    Folder_check = folderupload.getText().toString();
	    String imageDirectoryPath =  Environment.getExternalStorageDirectory().getAbsolutePath()+ "/"+Folder_check+"/";
	     
	    Log.v(Folder_check,"Folder");
	    File folder1 = new File(Environment.getExternalStorageDirectory(),Folder_check);
		if (folder1.exists())
		{
			File imageDirectory = new File(imageDirectoryPath);
		    String[] fileList = imageDirectory.list();

		    if(fileList.length != 0) {
		        for(int i=0; i<fileList.length; i++)
		        {
		            String file = "file://" + imageDirectoryPath + fileList[i];
		            Uri uriFile = Uri.parse(file);
		            uriList.add(uriFile);

		        }
		    }
		    return uriList;
		}
		 return uriList;
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