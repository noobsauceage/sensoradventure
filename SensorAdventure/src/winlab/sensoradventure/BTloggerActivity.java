package winlab.sensoradventure;

import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BTloggerActivity extends Activity {
	private static final String LOG_TAG = "BTLoggerService";
	private final static int INTERVAL = 1000 * 30; // 30sec
	private final static int DISCOVER_TIME = 1000 * 12; // 12sec
	private final ScheduledExecutorService scheduler = Executors
			.newScheduledThreadPool(1);
	ScheduledFuture<?> loggerHandle;
	private static final int REQUEST_ENABLE_BT = 1;
	private PrintWriter captureFile;
	private long timestamp;
	private int numDevices = 0;

	ListView listDevicesFound;
	Button btnScanDevice;
	TextView stateBluetooth;
	BluetoothAdapter bluetoothAdapter;

	ArrayAdapter<String> btArrayAdapter;
	ArrayList<String> macIdList = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		try {
			// Create file
			File dataDir = new File("/sdcard/sensorData/");
			// Make the dir if needed
			dataDir.mkdirs();
			// Make the file
			File myFile = new File(dataDir, "bt_.csv");

			captureFile = new PrintWriter(new FileWriter(myFile, false));
		} catch (Exception e) {// Catch exception if any
			Log.e(LOG_TAG, e.getMessage(), e);
		}

		// csv field names
		captureFile.println("`timestamp`" + "," + "`numDevices`"+ "," + "`macList`");

		//btnScanDevice = (Button) findViewById(R.id.scandevice);

		//stateBluetooth = (TextView) findViewById(R.id.bluetoothstate);
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		//listDevicesFound = (ListView) findViewById(R.id.devicesfound);
		btArrayAdapter = new ArrayAdapter<String>(BTloggerActivity.this,
				android.R.layout.simple_list_item_1);
		listDevicesFound.setAdapter(btArrayAdapter);

		CheckBlueToothState();

		//btnScanDevice.setOnClickListener(btnScanDeviceOnClickListener);
	}

	public void onStart() {
		super.onStart();

		registerReceiver(ActionFoundReceiver, new IntentFilter(
				BluetoothDevice.ACTION_FOUND));

		logStuff();
	}

	public void logStuff() {
		final Runnable logger = new Runnable() {
			public void run() {
				Log.d(LOG_TAG, "Starting scheduled task");
				pollBT();
			}
		};
		loggerHandle = scheduler.scheduleAtFixedRate(logger, INTERVAL / 2,
				INTERVAL, MILLISECONDS);
		scheduler.schedule(new Runnable() {
			public void run() {
				loggerHandle.cancel(true);
			}
		}, 1, HOURS);
	}

	private void pollBT() {
		Log.d(LOG_TAG, "Polling BT");
		// btArrayAdapter.clear();
		numDevices = 0;
		macIdList.clear();
		// btArrayAdapter.notifyDataSetChanged();
		bluetoothAdapter.startDiscovery();

		Log.d(LOG_TAG, "Waiting on discovery");

		try {
			Thread.sleep(DISCOVER_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Manually cancelling it will cause stupid bugs
		// Like the BT will restart discovery and will double count some devices
		// Log.d(LOG_TAG, "Cancelling discovery");
		// bluetoothAdapter.cancelDiscovery();
		// if (bluetoothAdapter.isDiscovering()) {

		// }

		Log.d(LOG_TAG, "Printing");
		timestamp = System.currentTimeMillis();
		// captureFile.println(timestamp + "," + btArrayAdapter.getCount());
		captureFile.println(timestamp + "," + numDevices + "," + TextUtils.join(",", macIdList));

		// Log.d(LOG_TAG, timestamp + "," + btArrayAdapter.getCount());
		Log.d(LOG_TAG, timestamp + "," + numDevices + "," + TextUtils.join(",", macIdList));

	}

	private void CheckBlueToothState() {
		if (bluetoothAdapter == null) {
			stateBluetooth.setText("Bluetooth NOT support");
		} else {
			if (bluetoothAdapter.isEnabled()) {
				if (bluetoothAdapter.isDiscovering()) {
					stateBluetooth
							.setText("Bluetooth is currently in device discovery process.");
				} else {
					stateBluetooth.setText("Bluetooth is Enabled.");
					btnScanDevice.setEnabled(true);
				}
			} else {
				stateBluetooth.setText("Bluetooth is NOT Enabled!");
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
		}
	}

	/*
	private Button.OnClickListener btnScanDeviceOnClickListener = new Button.OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			btArrayAdapter.clear();
			bluetoothAdapter.startDiscovery();
		}
	};
	*/

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == REQUEST_ENABLE_BT) {
			CheckBlueToothState();
		}
	}

	private final BroadcastReceiver ActionFoundReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				
				 BluetoothDevice device = intent
				 .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				 //btArrayAdapter.add(device.getName() + "\n" +
				 //device.getAddress()); 
				 //btArrayAdapter.notifyDataSetChanged();
				 
				 macIdList.add(device.getAddress());
				 

				numDevices++;

				/*
				 * Toast.makeText(getBaseContext(), "# devices: " +
				 * btArrayAdapter.getCount(), Toast.LENGTH_LONG).show();
				 * 
				 * timestamp = System.currentTimeMillis();
				 * captureFile.println(timestamp + "," +
				 * btArrayAdapter.getCount());
				 */
			}
			if (intent.getAction().equals(
					BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
				bluetoothAdapter.cancelDiscovery();
			}

		}
	};

	public void onStop() {
		super.onStop();
		captureFile.close();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(ActionFoundReceiver);
		loggerHandle.cancel(true);
	}

}