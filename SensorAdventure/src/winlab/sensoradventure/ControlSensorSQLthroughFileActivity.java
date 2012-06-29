package winlab.sensoradventure;

import winlab.file.SnapShotValue;
import winlab.sql.InsertToTable;
import winlab.sql.RunningServiceSQLite;
import winlab.sql.Sensors_SQLite_Setting;
import winlab.sql.Sensors_SQLite_instantReading;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ControlSensorSQLthroughFileActivity extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
	Button buttonStart, buttonStop,buttonStart2, buttonStop2,mark;
	Sensors_SQLite_Setting ok2;
    boolean sensorSQL[]={true,true,true,false,false,false,false,false,false,false,false,false,false,false};
    Sensors_SQLite_instantReading instantTables;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sql);
        
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
        
        
        ok2= new Sensors_SQLite_Setting(this);
        ok2.testAvailableSensors();
        
        
        
        string="SQLite ";
        for (int i=0; i<13; i++)
        	if (Sensors_SQLite_Setting.sensors[i]) string=string+" "+String.format("%d", i+1); 
        Toast.makeText(this, string, Toast.LENGTH_LONG).show();
        
        ok2.selectSensors(sensorSQL);
        SnapShotValue.set();
        
        string="SQL ";
        for (int i=0; i<13; i++)
        	if (Sensors_SQLite_Setting.sensors[i]) string=string+" "+String.format("%d", i+1); 
        Toast.makeText(this, string, Toast.LENGTH_LONG).show();
        
        instantTables = new Sensors_SQLite_instantReading(this);
    	instantTables.open();
        instantTables.deleteTable();
    }
     
    public void onClick(View a) {
    	String str1,str2,str3,str4,str5;
        switch (a.getId()) {
        case R.id.buttonStart2:
        	ok2.selectSensors(sensorSQL);
        	startService(new Intent(this,RunningServiceSQLite.class));
        	break;
        case R.id.buttonStop2:
        	stopService(new Intent(this, RunningServiceSQLite.class));
        	startService(new Intent(this, InsertToTable.class));
        	break;
        case R.id.mark:        	
            for (int i=0; i<13; i++)
            	if (Sensors_SQLite_Setting.sensors[i])
            	switch (i) {
 			   case 0:
 			   case 1:
 			   case 2:
 			   case 3:
 			   case 8:
 			   case 9:
 				  str1=String.format("%d",System.currentTimeMillis());
 				  str2=String.format("%.10f",SnapShotValue.instantValue[i][0]);
				  str3=String.format("%.10f",SnapShotValue.instantValue[i][1]);
				  str4=String.format("%.10f",SnapShotValue.instantValue[i][2]);
				  instantTables.insertTitle1(str1,str2,str3,str4,i);
 			      break;
 			   case 10:
 				  str1=String.format("%d",System.currentTimeMillis());
 				  str2=String.format("%.10f",SnapShotValue.instantValue[i][0]);
				  str3=String.format("%.10f",SnapShotValue.instantValue[i][1]);
				  str4=String.format("%.10f",SnapShotValue.instantValue[i][2]);
				  str5=String.format("%.10f",SnapShotValue.instantValue[i][3]);				  
				  instantTables.insertTitle3(str1,str2,str3,str4,str5,i);
 			      break;
 			   default:  
 				  str1=String.format("%d",System.currentTimeMillis());
 				  str2=String.format("%.10f",SnapShotValue.instantValue[i][0]);
				  instantTables.insertTitle2(str1,str2,i);      
 			      break;
            	}
        } 
   }
}