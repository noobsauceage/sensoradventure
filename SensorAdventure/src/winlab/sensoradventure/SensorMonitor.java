package winlab.sensoradventure;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;

import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

									// extend service if needed
public class SensorMonitor extends Activity implements SensorEventListener {
    private static final String LOG_TAG = "SensorLoggerService";
 
    // Globals:
    private SensorManager sensorManager = null;
    //private Sensor sensor = null;
    private List<Sensor> sensors;
    public float value;
    public long timestamp;
    public List<PrintWriter> captureFiles= new ArrayList<PrintWriter>();  
    
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate( savedInstanceState );   	
    }
    
    protected void onStart() {
        super.onStart();
        sensorManager = (SensorManager)getSystemService( SENSOR_SERVICE  );
        sensors = sensorManager.getSensorList( Sensor.TYPE_ALL );
        
        // Register listeners for all available sensors
        
        for( int i = 0 ; i < sensors.size() ; ++i ) {
            Sensor ourSensor = sensors.get( i );
	        if( ourSensor != null ) {
	        	sensorManager.registerListener( 
	                this, 
	                ourSensor,
	                SensorManager.SENSOR_DELAY_NORMAL );
	        }
        
	        // Create seperate files for each available sensor
	    	try {
	    		//should actually only be a new file after upload
	    		
	    		// Make the dir if needed
	    		File dataDir = new File("/sdcard/sensorData/");
	    		// have the object build the directory structure, if needed.
	    		dataDir.mkdirs();
	    		// Make the file
	    		File myFile = new File(dataDir, ourSensor.getType() + "_" + ourSensor.getName() + ".csv");

				myFile.createNewFile();	
				captureFiles.add(new PrintWriter( new FileWriter( myFile, false ) ) );
				
				// csv field names
				//captureFiles.get(i).println("`timestamp`" + "," + "`values[0]`" + "," + "`values[1]`"+ "," + "`values[2]`");
				captureFiles.get(i).println("`timestamp`" + "," + getFields(ourSensor.getType()));
				
	        } catch( Exception e) {
	            Log.e( LOG_TAG, e.getMessage(), e );
	        }
        }
    }
    
	private String getFields(int type) {
		String fields = null;

		switch (type) {
		case 1:
		case 2:
		case 3:
		case 9:
		case 10:
		case 11:
			fields = "`x`" + "," + "`y`" + "," + "`z`";
			break;
		default:
			fields = "`value`" + "," + "`null0`" + "," + "`null1`";	//stupid
		}
		return fields;

	}
    
    
    /*	// Might need for SQLite
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //List<Sensor> sensors = sensorManager.getSensorList( Sensor.TYPE_ALL );
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE);
        sensorManager.registerListener(this, sensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
 
        return START_STICKY;
    }
    */
 
    /*
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
 	*/
 	
    //@Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // do nothing
    }
 
    
    //@Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        int index = sensors.indexOf(sensor);
     	
        PrintWriter thiscaptureFile;
        
        timestamp = System.currentTimeMillis();
        
        thiscaptureFile = captureFiles.get(index);
        if( thiscaptureFile != null ) {
        	thiscaptureFile.print(timestamp);
        	//for( int i = 0 ; i < event.values.length ; ++i ) {
        	for( int i = 0 ; i < event.values.length ; ++i ) {
        		thiscaptureFile.print("," + event.values[i]);
        	}
        	thiscaptureFile.println();
		}
    }
     
    /*	// Might need this for SQLite
    private class SensorEventLoggerTask extends
    	AsyncTask<SensorEvent, Void, Void> {
        @Override
        protected Void doInBackground(SensorEvent... events) {
        	SensorEvent event = events[0];
            // log the value******************************************************
        	ContentValues values = new ContentValues();
        	values.put(SensorDataHelper.COL_VALUE, value);
        	values.put(SensorDataHelper.COL_TIMESTAMP, timestamp);
        	getContentResolver().insert(SensorDataProvider.CONTENT_URI,
        	                        values);
        	
        	return null;
        }
    }
    */
    
    protected void onStop() {
        super.onStop();
        for( int i = 0 ; i < sensors.size() ; ++i ) {
        	captureFiles.get(i).close();
        	if( sensors.get( i ) != null ) {
	        	sensorManager.unregisterListener( 
	                this, 
	                sensors.get( i ));
	        }
        }
    }
}