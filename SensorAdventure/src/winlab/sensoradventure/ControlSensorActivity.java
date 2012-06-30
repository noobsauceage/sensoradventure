package winlab.sensoradventure;


import winlab.file.RunningService;
import winlab.file.SensorSetting;
import winlab.file.SnapShotValue;
import winlab.sql.Sensors_SQLite_Service;
import winlab.sql.Sensors_SQLite_Setting;
import winlab.sql.SnapShot_SQL;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ControlSensorActivity extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
	Button buttonStart, buttonStop,buttonStart2, buttonStop2,mark;
	SensorSetting ok;
	Sensors_SQLite_Setting ok2;
	boolean sensor[]={true,true,true,true,false,false,false,false,false,false,false,false,false};
    boolean sensorSQL[]={true,true,true,false,false,false,false,false,false,false,false,false,false,false};
    SnapShot_SQL data2;
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
        
        mark = (Button) findViewById(R.id.mark);
        mark.setOnClickListener(this);
        
        ok = new SensorSetting(this);
        ok.testAvailableSensors();
        ok2= new Sensors_SQLite_Setting(this);
        ok2.testAvailableSensors();
        
        
        for (int i=0; i<13; i++)
        	if (SensorSetting.sensors[i]) string=string+" "+String.format("%d", i+1); 
        Toast.makeText(this, string, Toast.LENGTH_LONG).show();
        string="SQLite ";
        for (int i=0; i<13; i++)
        	if (Sensors_SQLite_Setting.sensors[i]) string=string+" "+String.format("%d", i+1); 
        Toast.makeText(this, string, Toast.LENGTH_LONG).show();
        
        ok.selectSensors(sensor);
        ok2.selectSensors(sensorSQL);
        SnapShotValue.set();
        
        string="";
        for (int i=0; i<13; i++)
        	if (SensorSetting.sensors[i]) string=string+" "+String.format("%d", i+1); 
        Toast.makeText(this, string, Toast.LENGTH_LONG).show();
        
        string="SQL ";
        for (int i=0; i<13; i++)
        	if (Sensors_SQLite_Setting.sensors[i]) string=string+" "+String.format("%d", i+1); 
        Toast.makeText(this, string, Toast.LENGTH_LONG).show();
        
        data2 = new SnapShot_SQL(this);
		data2.open();
		data2.deleteTable();
		data2.prepareTransaction();
		
    }
     
    public void onClick(View a) {
        switch (a.getId()) {
        case R.id.buttonStart:
          ok.selectSensors(sensor);
          startService(new Intent(this,RunningService.class));
          break;
        case R.id.buttonStop:
          stopService(new Intent(this,RunningService.class)); 
          break;
        case R.id.buttonStart2:
        	ok2.selectSensors(sensorSQL);
        	startService(new Intent(this,Sensors_SQLite_Service.class));
        	break;
        case R.id.buttonStop2:
        	data2.endTransaction();
        	data2.close();
        	stopService(new Intent(this, Sensors_SQLite_Service.class));
        	break;
        case R.id.mark:
            SnapShotValue.print();
        	SnapShotValue.insertSQL(data2);
        }
   }
}
