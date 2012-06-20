 
package winlab.sensoradventure.gps;
 
 

 
import winlab.sensoradventure.R;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Html;
import android.text.util.Linkify;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;
public class MainActivity extends Activity {
        private int currentIntervalChoice = 0;
        private static final int REQUEST_CODE = 0;
        Button Start_log;
    	Button Logging_Interval;
    	Button GPS_Map;
    	Button GPS_Enable;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
   
        boolean isEnabled = isGPSenabled();
        
      //  displayGPSState(isEnabled);
        
 
    	Start_log = (Button) findViewById(R.id.start_logging);
    	Start_log.setVisibility(View.INVISIBLE);
    	Logging_Interval = (Button) findViewById(R.id.logging_interval);
    	Logging_Interval.setVisibility(View.INVISIBLE);
    	 
    	 
    	if(isEnabled)
    	{
    		  addButtonListeners();
              enableControls();
    	}
    	else
    	{
    		 buildAlertMessageNoGps();
    	}
    
    }
    
    public void onResume(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
   
        boolean isEnabled = isGPSenabled();
        
      //  displayGPSState(isEnabled);
        
        /*
    	Start_log = (Button) findViewById(R.id.start_logging);
    	Start_log.setVisibility(View.INVISIBLE);
    	Logging_Interval = (Button) findViewById(R.id.logging_interval);
    	Logging_Interval.setVisibility(View.INVISIBLE);
    	*/
    	 
    	if(isEnabled)
    	{
    		  addButtonListeners();
              enableControls();
    	}
    	else
    	{
    		 buildAlertMessageNoGps();
    	}
    
    }
        private void addButtonListeners() {
        	 
        	Start_log = (Button) findViewById(R.id.start_logging);
        	Start_log.setVisibility(View.VISIBLE);
        	Logging_Interval = (Button) findViewById(R.id.logging_interval);
        	Logging_Interval.setVisibility(View.VISIBLE);
      
        	((Button)findViewById(R.id.start_logging)).setOnClickListener(btnClick);
            ((Button)findViewById(R.id.logging_interval)).setOnClickListener(btnClick);
        }

  
        
        private void toggleLogging(boolean isStart, int interval){
                AlarmManager manager = (AlarmManager)getSystemService(Service.ALARM_SERVICE);
                PendingIntent loggerIntent = PendingIntent.getBroadcast(this, 0,new Intent(this,AlarmReceiver.class), 0);
                
                if(isStart){
                        manager.cancel(loggerIntent);
                        
                        AppSettings.setServiceRunning(this, false);
                        
                        AppLog.logString("Service Stopped.");
                }
                else{
                        setLogFileName();
                        
                        long duration = interval * 60 * 1000;
                        
                        manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                                        SystemClock.elapsedRealtime(), duration, loggerIntent);
                        
                        AppSettings.setServiceRunning(this, true);
                        
                        AppLog.logString("Service Started with interval " + interval + ", Logfile name: " + AppSettings.getLogFileName(this));
                }
        }
        
        private void enableControls(){
                boolean isServiceRunning = AppSettings.getServiceRunning(this);
                String buttonText = "Start Logging";
                
                if(isServiceRunning){
                        buttonText = "Stop Logging";
                        
                        ((Button)findViewById(R.id.logging_interval)).setEnabled(false);
                }
                else{
                        ((Button)findViewById(R.id.logging_interval)).setEnabled(true);
                }
                
                ((Button)findViewById(R.id.start_logging)).setText(buttonText);
        }
        
        private void changeLoggingIntercal(){
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final String loggingIntervals[] = { "1 minutes", "5 minutes", "10 minutes", "1 hour" }; 
                
        builder.setTitle("Logging Interval");
        builder.setSingleChoiceItems(loggingIntervals, currentIntervalChoice, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                                currentIntervalChoice = which;
                                
                                setLoggingInterval(currentIntervalChoice);
                                
                                dialog.dismiss();
                        }
                });
        
        builder.show();
        }
        
        private void setLoggingInterval(int intervalChoice){
                int interval = 1;
                
                switch(intervalChoice){
                        case 0:         interval = 1;   break;
                        case 1:         interval = 5;  break;
                        case 2:         interval = 10;  break;
                        case 3:         interval = 15;  break;
                        default:        interval = 1;   break;
                }
                
                AppSettings.setLoggingInterval(this, interval);
        }
        
        public void setLogFileName(){
           //     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
           //     String dateString = sdf.format(new Date());
            //    String filename = "GPSLog." + dateString + ".txt";
                
                AppSettings.setLogFileName(this, "gpslog.txt");
        }
        
        private View.OnClickListener btnClick = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        switch(v.getId())
                        {
                                case R.id.start_logging:{
                                        toggleLogging(AppSettings.getServiceRunning(MainActivity.this), 
                                                                  AppSettings.getLoggingInterval(MainActivity.this));
                                        
                                        enableControls();       
                                        
                                        break;
                                }
                                case R.id.logging_interval:{
                                        changeLoggingIntercal();
                                        
                                        break;
                                }
                        }
                }
        };
        
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data)
        {
                super.onActivityResult(requestCode, resultCode, data);
                
                if(requestCode == REQUEST_CODE)
                {
                        displayGPSState(isGPSenabled());
                }
        }

        private void displayGPSState(boolean isEnabled)
        {
        	  
        	 
 
        }

        private boolean isGPSenabled()
    {   
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
    
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        
        builder.setTitle("GPS State");
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?");
        builder.setCancelable(false);
        
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() 
                        {
                        public void onClick(final DialogInterface dialog, final int id) 
                        {
                                launchGPSOptions();             
                                finishActivity(id);
                        }
                        });
        
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() 
                        {
                        public void onClick(final DialogInterface dialog, final int id) 
                        {
                                dialog.cancel();
                                
                        }
                        });
        
        builder.create().show();
    }

    private void launchGPSOptions()
    {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, REQUEST_CODE);
    }
}

