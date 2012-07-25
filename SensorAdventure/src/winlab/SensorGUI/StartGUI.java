package winlab.SensorGUI;

import java.io.File;
import java.io.FileWriter;
import winlab.file.RunningService;
import winlab.file.SensorSetting;
import winlab.file.SnapShotValue;
import winlab.sensoradventure.ContinuousRecorder;
import winlab.sensoradventure.R;
import winlab.sensoradventure.SensorAdventureActivity;
import winlab.sensoradventure.gps.GPSloggerService;
import winlab.sql.Sensors_SQLite_Service;
import winlab.sql.Sensors_SQLite_Setting;
import winlab.sql.SnapShot_SQL;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

@TargetApi(9)
public class StartGUI extends Activity implements OnClickListener {
	private boolean flag = true;
	private Chronometer mChronometer;
	private Button mark, stop, snapshot;
	private String[] times = new String[10];
	private LinearLayout ll1;
	private LinearLayout ll2;
	private SensorSetting ok;
	private Sensors_SQLite_Setting ok2;
	private SnapShot_SQL data2;
	private boolean[] state;
	private boolean[] sensorCheck;
	private int[] rates;
	private String[] Sensors;
	private ContinuousRecorder record;
    private int micchanneli,micchannelo,micencode,micsampling;
	private AsyncTask<Void, Void, Void> asyncTask;  //add
    private long rate=100;//add in ms
    private long duration=5; //add in s
    private EditText editText1,editText2; //add edittext
    private boolean Snapshot_finish=true; //add
    private final Handler handler1 = new Handler() {
		public void handleMessage(Message msg) {
				Toast.makeText(StartGUI.this, "Continuous snapShot starts!",Toast.LENGTH_LONG).show();
		}
	};
	private final Handler handler2 = new Handler() {
		public void handleMessage(Message msg) {
				Toast.makeText(StartGUI.this, "Continuous snapShot finished!",Toast.LENGTH_LONG).show();
		}
	};
	private File path;
	private String fileName = "Continuous_snapShot.txt";
	private File file;
	private FileWriter output;
	private boolean flag2 = true;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.start_gui);

        editText1=(EditText) findViewById(R.id.editText1);
        editText2=(EditText) findViewById(R.id.editText2);
        editText1.setText(String.format("%d",duration));
        editText2.setText(String.format("%d",rate));
        editText1.requestFocus(0);
        editText2.requestFocus(0);
        snapshot = (Button) findViewById(R.id.snapshot);
        snapshot.setOnClickListener(this);

        mChronometer = (Chronometer) findViewById(R.id.chronometer);

		mark = (Button) findViewById(R.id.button1);
		mark.setOnClickListener(this);
		stop = (Button) findViewById(R.id.button2);
		stop.setOnClickListener(this);

		ll1 = (LinearLayout) findViewById(R.id.layout1);
		ll2 = (LinearLayout) findViewById(R.id.layout2);

		for (int i = 0; i < 10; i++)
			times[i] = "";
		mChronometer.setBase(SystemClock.elapsedRealtime());
		mChronometer.start();

		// Retrieve data on which sensors are on
		// And what upload options exist
		Bundle extras = getIntent().getExtras();
		sensorCheck = extras.getBooleanArray("sensorCheck");
		state = extras.getBooleanArray("state");
		Sensors = extras.getStringArray("Sensors");
		rates = extras.getIntArray("rates");
		micsampling=rates[13];
		if (extras.getString("micchannel").equals("MONO"))
		{
		micchanneli=16;
		micchannelo=4;
		}
		if (extras.getString("micchannel").equals("STEREO"))
		{
		micchanneli=12;
		micchannelo=12;
		}
		if (extras.getString("micencode").equals("16"))
			micencode=2;
		if (extras.getString("micencode").equals("8"))
			micencode=3;
		record = new ContinuousRecorder(1,micsampling,micchanneli,micchannelo,micencode,3,1,StartGUI.this);
        //record = new ContinuousRecorder(StartGUI.this);
		//record.debug();
		ok = new SensorSetting(this);
		ok2 = new Sensors_SQLite_Setting(this);
		ok.selectSensors(sensorCheck);
		ok2.selectSensors(sensorCheck);

		// Print which sensors are on to the screen
		for (int i = 0; i < sensorCheck.length; i++) {
			if (sensorCheck[i]) {
				TextView tv = new TextView(this);
				tv.setText(Sensors[i]);
				ll2.addView(tv);
			}

			SnapShotValue.set();
			if (state[0]) {
				SensorSetting.setRate(rates);
				startService(new Intent(this, RunningService.class));
				if ((sensorCheck[13])&&(state[1]==false)) {
					record.record();
				}
				if ((sensorCheck[14])&&(state[1]==false)) {
					startService(new Intent(this, GPSloggerService.class));
				}
			}

			if (state[1]) {
				data2 = new SnapShot_SQL(this);

				data2.open();
				data2.deleteTable();
//				for (int j=0; j<13; j++)
//					if (sensorCheck[j]) data2.prepareTransaction(j);
				Sensors_SQLite_Setting.setRate(rates);
				startService(new Intent(this, Sensors_SQLite_Service.class));
                if (sensorCheck[13])
                {
                	record.writeToSQLite();
                	record.record();
                }
			}
		}
	}

	public void onClick(View a) {
		switch (a.getId()) {
		case R.id.button1:
			if (state[0])
				SnapShotValue.print();
			if (state[1])
				try {
					SnapShotValue.insertSQL(data2);
				} catch (Exception e) {
					Toast.makeText(this, "1", Toast.LENGTH_LONG).show();
				}
			TextView tv = new TextView(this);
			tv.setText(mChronometer.getInstantTime());
			ll1.addView(tv);
			break;

		case R.id.button2:
			flag = false;
			if (state[0])
			{
				stopService(new Intent(this, RunningService.class));
				if ((sensorCheck[13])&&(state[1]==false))
				{
					record.stop();
				record.cancel();
				}
			}
			if (state[1]) 
			{
				try {
//					for (int j=0; j<13; j++)
//						if (sensorCheck[j]) data2.endTransaction(j);
					data2.copy();
					data2.close();
				} catch (Exception e) {
					Toast.makeText(this, "2", Toast.LENGTH_LONG).show();
				}
				stopService(new Intent(this, Sensors_SQLite_Service.class));
				if (sensorCheck[13])
				{
					record.stop();
				record.cancel();
				}
			}
			mChronometer.stop();
			break;
		case R.id.snapshot:
        	if (Snapshot_finish)
        	{
        	duration=Long.parseLong(editText1.getText().toString());
        	rate=Long.parseLong(editText2.getText().toString());
        	asyncTask = new start();
    		asyncTask.execute();
        	}
        	
		}
	}

	public void onDestroy() {

		if (flag) {
			if (state[0])
			{
				stopService(new Intent(this, RunningService.class));
				if ((sensorCheck[13])&&(state[1]==false))
				{
					record.stop();
				record.cancel();
				}
			}
			if (state[1]) {
				try {
//					for (int j=0; j<13; j++)
//						if (sensorCheck[j]) data2.endTransaction(j);
					data2.close();
				} catch (Exception e) {
				}
				stopService(new Intent(this, Sensors_SQLite_Service.class));
				{
					record.stop();
				record.cancel();
				}
			}
			mChronometer.stop();
		}
		super.onDestroy();
	}

	//added part
    private class start extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... n) {

			long startTime,currentTime,lastTime;


			Snapshot_finish=false;

			Message msg1 = handler1.obtainMessage();
			handler1.sendMessage(msg1);

			startTime=System.currentTimeMillis();
			lastTime=startTime;
			print();
			do
			{

				currentTime=System.currentTimeMillis();
				if (currentTime-lastTime>=rate)
				{
					print();
					lastTime=currentTime;
				}
			}while (currentTime-startTime<duration*1000);

			Message msg2 = handler2.obtainMessage();
			handler2.sendMessage(msg2);
            Snapshot_finish=true;
			return null;
		}


	}
    


   
    


	public void print() {
		path = SensorAdventureActivity.DataPath;
		file = new File(path, fileName);
		String str = "";
		str = str + "Timestamp (ms): "
				+ String.format("%d", System.currentTimeMillis()) + "\n";
		try {
			path.mkdirs();
			file.setWritable(true);
			
			if (flag2)
				output = new FileWriter(file);
			else
				output = new FileWriter(file, true);
			flag2 = false;
			for (int i = 0; i < 13; i++)
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
					case 12: //Sensor.TYPE_RELATIVE_HUMIDITY
						str = str
								+ "Relative Humidity %: "
								+ String.format("%17.10f",
										SnapShotValue.instantValue[i][0])
								+ "\n";
						break;
					case 13: //Sensor.TYPE_AMBIENT_TEMPERATURE
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

	}

}