package winlab.sql;


//import java.io.File;
//import java.io.FileWriter;

import winlab.file.SnapShotValue;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
//import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
//import android.os.Environment;
import android.os.IBinder;
//import android.telephony.TelephonyManager;
import android.widget.Toast;

public class Sensors_SQLite_Service extends Service implements
SensorEventListener{
	
	//private TelephonyManager telephonyManager;
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private Sensors_SQLite data; 
    
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Toast.makeText(this, "Start taking data", Toast.LENGTH_LONG).show();
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		Toast.makeText(this,"Data will be saved in SQLiteDataBase", Toast.LENGTH_LONG).show();
		data = new Sensors_SQLite(this);
		data.open();
		data.deleteTable();
		for (int i=0; i<13; i++)
			if (Sensors_SQLite_Setting.sensors[i])
		                       data.prepareTransaction(i);
		
	}
	
	public void onSensorChanged (SensorEvent event) {
			int i=event.sensor.getType(); 
			String timestamp="";
			String str1 = "";
			String str2 = "";
			String str3 = "";
			String str4 = "";
			switch (i) {
			case 1: case 2: case 3: case 4: case 9: case 10:
					
					timestamp=String.format("%d",System.currentTimeMillis());
					str1 = String.format("%.10f", event.values[0]);
					str2 = String.format("%.10f",event.values[1]);
					str3 = String.format("%.10f",event.values[2]);
					SnapShotValue.instantValue[i-1][0]=event.values[0];
					SnapShotValue.instantValue[i-1][1]=event.values[1];
					SnapShotValue.instantValue[i-1][2]=event.values[2];
					data.insertTitle1(timestamp, str1, str2, str3,i-1);
					break;
			case 5: case 6: case 7: case 8: case 12: case 13:
				    timestamp=String.format("%d",System.currentTimeMillis());
				    str1 = String.format("%.10f", event.values[0]);
				    SnapShotValue.instantValue[i-1][0]=event.values[0];
					data.insertTitle2(timestamp, str1,i-1);
					break;
			 case 11:
				 if (event.values.length==4)
				 {
				    timestamp=String.format("%d",System.currentTimeMillis());
				    str1 = String.format("%.10f", event.values[0]);
					str2 = String.format("%.10f",event.values[1]);
					str3 = String.format("%.10f",event.values[2]);
					str4 = String.format("%.10f",event.values[3]);
					SnapShotValue.instantValue[i-1][0]=event.values[0];
					SnapShotValue.instantValue[i-1][1]=event.values[1];
					SnapShotValue.instantValue[i-1][2]=event.values[2];
					SnapShotValue.instantValue[i-1][3]=event.values[3];
					data.insertTitle3(timestamp, str1, str2, str3, str4, i-1);
				 } else {
					 timestamp=String.format("%d",System.currentTimeMillis());
					    str1 = String.format("%.10f", event.values[0]);
						str2 = String.format("%.10f",event.values[1]);
						str3 = String.format("%.10f",event.values[2]);
		                str4 = "NA";
						SnapShotValue.instantValue[i-1][0]=event.values[0];
						SnapShotValue.instantValue[i-1][1]=event.values[1];
						SnapShotValue.instantValue[i-1][2]=event.values[2];
						data.insertTitle3(timestamp, str1, str2, str3, str4, i-1);
				 }
					break;
				 
				
			}
			}
		
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
		
		@Override
		public void onDestroy() {
			super.onDestroy();
			for (int i=0; i<13; i++)
				if (Sensors_SQLite_Setting.sensors[i])
				{
					try{
					mSensor = mSensorManager.getDefaultSensor(i+1);
					mSensorManager.unregisterListener(this, mSensor);
					}catch (Exception e){Toast.makeText(this, "sensor error", Toast.LENGTH_LONG).show();}
				}
			
			/*
			int count1=0,count2=0,count3=0;
			String result1="", result2="", result3="";
			Cursor c1,c2,c3;
			Toast.makeText(this, "Stop taking readings", Toast.LENGTH_LONG).show();
			
			
			for (int i=0; i<13; i++)
			if (Sensors_SQLite_Setting.sensors[i])
			{	
				count1=0; count2=0; count3=0;
				switch (i+1) {
				  case 1: case 2: case 3: case 4: case 9: case 10:
				       result1="timestamp (ms)            x            y            z \n";
	                   c1 = data.getAllTitles1(i);
		               if (c1.moveToFirst())
		                 {
		                    do {          
		        	              count1++;
		                          result1=result1+c1.getString(1)+"    "+c1.getString(2)+"    "+c1.getString(3)+"      "+c1.getString(4)+"\n";
		                        } while ((c1.moveToNext()) && (count1<10));
		                  }
		               Toast.makeText(this, result1, Toast.LENGTH_LONG).show();
		               break;
				  case 5: case 6: case 7: case 8: case 12: case 13:
					   result2="timestamp (ms)            Value\n";
	                   c2 = data.getAllTitles2(i);
		               if (c2.moveToFirst())
		                 {
		                    do {          
		        	              count2++;
		                          result2=result2+c2.getString(1)+"    "+c2.getString(2)+"\n";
		                        } while ((c2.moveToNext()) && (count2<10));
		                  }
		               
		               Toast.makeText(this, result2, Toast.LENGTH_LONG).show();
		               break;
				  case 11:
					   result3="timestamp (ms)            x            y           z      Scalar\n";
	                   c3 = data.getAllTitles3(i);
		               if (c3.moveToFirst())
		                 {
		                    do {          
		        	              count3++;
		                          result3=result3+c3.getString(1)+"    "+c3.getString(2)+"    "+c3.getString(3)+"      "+c3.getString(4)
		                        		  +"            "+c3.getString(5)+"\n";
		                        } while ((c3.moveToNext()) && (count3<10));
		                  }
		               
		               Toast.makeText(this, result3, Toast.LENGTH_LONG).show();
		               break;
				}
		     }*/
			
			try{
				for (int i=0; i<13; i++)
					if (Sensors_SQLite_Setting.sensors[i])
			          data.endTransaction(i);
			
			data.close();
			}
			catch (Exception e) {Toast.makeText(this, "3", Toast.LENGTH_LONG).show();}
			for (int i=0; i<13; i++) Sensors_SQLite_Setting.sensors[i]=true;
		}
		@Override
		public void onStart(Intent intent, int startid) {
			int m;
			for (int i=0; i<13; i++)
				if (Sensors_SQLite_Setting.sensors[i])
				{
					m=1000*Sensors_SQLite_Setting.updateRate[i];
					mSensor = mSensorManager.getDefaultSensor(i+1);
			        mSensorManager.registerListener(this, mSensor,m);
				}
		}
}




