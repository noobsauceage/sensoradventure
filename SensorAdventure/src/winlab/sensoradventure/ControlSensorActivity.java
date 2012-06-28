package winlab.sensoradventure;
/*
//Following is the activity example of how to use other sensors file and sqlite classes
//see me(Gao) if u have other questions
//Note that I haven't added in sampling rate option yet.


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ControlSensorActivity extends Activity implements OnClickListener {
	//"buttonStart" and "buttonStop" for saving in files
	//"buttonStart2" and "buttonStop2" for saving in SQLite
	 
	Button buttonStart, buttonStop, buttonStart2, buttonStop2;
	
	//SensorSetting class is used to check available sensors in the phone
	//and set the user selected sensors to implement (for file saving). 
	
	SensorSetting ok;
	
	//Sensors_SQLite_Setting class is used to check available sensors in the phone
	//and set the user selected sensors to implement (for SQLite saving). 
	
	Sensors_SQLite_Setting ok2;
	
	//sensor[] is the selected list of sensors for file
	//sensorSQL[] is the one for SQLite (true for turning on, false for turning off) 
	
	boolean sensor[]={true,true,true,true,false,false,false,false,false,false,false,false,false};
    boolean sensorSQL[]={true,true,true,false,false,false,false,false,false,false,false,false,false,false};
    //note that SQLite version has one more sensor "Microphone"
    
    //sensor list responding to boolean array (in order):  
    // Accelerometer, MagneticField, Orientation, Gyroscope, Light,
    // Pressure, Temperature (device temperature), Proximity,  
    //Gravity, Linear Acceleration, Rotation Vector, Relative humidity, Ambient Temperature
    //(and Microphone for SQLite version)
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        String string="";
        buttonStart = (Button) findViewById(R.id.buttonStart);
        buttonStop = (Button) findViewById(R.id.buttonStop);
        
		//this.getSystemService(Context.TELEPHONY_SERVICE);
        buttonStart.setOnClickListener(this);
        buttonStop.setOnClickListener(this);
        
        buttonStart2 = (Button) findViewById(R.id.buttonStart2);
        buttonStop2 = (Button) findViewById(R.id.buttonStop2);
        
		//this.getSystemService(Context.TELEPHONY_SERVICE);
        buttonStart2.setOnClickListener(this);
        buttonStop2.setOnClickListener(this);
        
        
        ok = new SensorSetting(this);
        ok.testAvailableSensors(); 
        //the result of available sensors is stored in the class member "SenorSetting.sensors[]" a boolean array.
         
         
        ok2= new Sensors_SQLite_Setting(this);
        ok2.testAvailableSensors();
        
        //the result is stored in the class member "Sensors_SQLite_Setting.sensors[]" public and static
        
        //following is the message to show which sensors are available in the phone,
        //and which sensors are picked by the user
        for (int i=0; i<13; i++)
        	if (SensorSetting.sensors[i]) string=string+" "+String.format("%d", i+1); 
        Toast.makeText(this, string, Toast.LENGTH_LONG).show();
        string="SQLite ";
        for (int i=0; i<13; i++)
        	if (Sensors_SQLite_Setting.sensors[i]) string=string+" "+String.format("%d", i+1); 
        Toast.makeText(this, string, Toast.LENGTH_LONG).show();
        
        ok.selectSensors(sensor); //user selection is stored in array sensor[] which can be set above.
        ok2.selectSensors(sensorSQL);
        
        string="";
        for (int i=0; i<13; i++)
        	if (SensorSetting.sensors[i]) string=string+" "+String.format("%d", i+1); 
        Toast.makeText(this, string, Toast.LENGTH_LONG).show();
        
        string="SQL ";
        for (int i=0; i<13; i++)
        	if (Sensors_SQLite_Setting.sensors[i]) string=string+" "+String.format("%d", i+1); 
        Toast.makeText(this, string, Toast.LENGTH_LONG).show();
    }
     
    public void onClick(View a) {
        switch (a.getId()) {
        case R.id.buttonStart:
          startService(new Intent(this,RunningService.class));
          break;
        case R.id.buttonStop:
          stopService(new Intent(this,RunningService.class)); 
          break;
        case R.id.buttonStart2:
        
        	ok2.selectSensors(sensorSQL);
        	//note that I didn't add this command for "buttonStart" (for file)
        	//but it is also needed.
        	//For final revised activity some of the command in onCreate should be 
        	//moved to onClick.
        	
        	 
        	startService(new Intent(this,Sensors_SQLite_Service.class));
        	break;
        case R.id.buttonStop2:
        	stopService(new Intent(this, Sensors_SQLite_Service.class));
        	break;
        }
   }
}*/
