package winlab.sql;



import java.io.File;
import java.util.Scanner;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;

public class InsertToTable extends Service{
	private String fileName[] = {"Accelerometer1.txt","MagneticField1.txt",
			"Orientation1.txt","Gyroscope1.txt","Light1.txt","Pressure1.txt","Temperature1.txt",
			"Proximity1.txt","Gravity1.txt","Linear_Acceleration1.txt","Rotation_Vector1.txt","Humidity1.txt","Ambient_Temperature1.txt"};

	private File path = Environment
			.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
	
	private File file [] = {new File(path, fileName[0]),new File(path, fileName[1]),
			new File(path, fileName[2]),new File(path, fileName[3]),new File(path, fileName[4]),
			new File(path, fileName[5]),new File(path, fileName[6]),new File(path, fileName[7]),
			new File(path, fileName[8]),new File(path, fileName[9]),new File(path, fileName[10]),
			new File(path, fileName[11]),new File(path, fileName[12])};
	private Sensors_SQLite data;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		//super.onCreate();
		
		Toast.makeText(this, "Start writing to sqlite database", Toast.LENGTH_LONG).show();
		data = new Sensors_SQLite(this);
		data.open();
		data.deleteTable();
		
	}
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
		Scanner scan;
		String str1,str2,str3,str4,str5;
		data.prepareTransaction();
		for (int i=0; i<13; i++)
			if (Sensors_SQLite_Setting.sensors[i] && file[i].exists())
				try{
					scan = new Scanner(file[i]);
					switch (i+1) {
					case 1:
					case 2:
					case 3:
					case 4:
					case 9:
					case 10:
						while (scan.hasNext())
						{
						  str1=String.format("%.0f",scan.nextDouble());
						  str2=Double.toString(scan.nextDouble());
						  str3=Double.toString(scan.nextDouble());
						  str4=Double.toString(scan.nextDouble());
						  data.insertTitle1(str1,str2,str3,str4,i);
						}
						break;
					case 5:
					case 6:
					case 7:
					case 8:
					case 12:
					case 13:
						while (scan.hasNext())
						{
						  str1=String.format("%.0f",scan.nextDouble());
						  str2=Double.toString(scan.nextDouble());
						  data.insertTitle2(str1,str2,i);
						}
						break;
					case 11:
						while (scan.hasNext())
						{
						  str1=String.format("%.0f",scan.nextDouble());
						  str2=Double.toString(scan.nextDouble());
						  str3=Double.toString(scan.nextDouble());
						  str4=Double.toString(scan.nextDouble());
						  str5=Double.toString(scan.nextDouble());
						  data.insertTitle3(str1,str2,str3,str4,str5,i);
						}
						break;
					}
				}
				catch (Exception e){}
		        data.endTransaction();
				onDestroy();
				return START_NOT_STICKY;

	}
	@Override
	public void onDestroy() {
		Toast.makeText(this, "Finish writing to sqlite database", Toast.LENGTH_LONG).show();

		int count1=0,count2=0,count3=0;
		String result1="", result2="", result3="";
		Cursor c1,c2,c3;
		Toast.makeText(this, "Stop taking readings", Toast.LENGTH_LONG).show();
		Toast.makeText(this, "The top 10 data in the SQLite table will be shown!",Toast.LENGTH_LONG).show();
		
		for (int i=0; i<13; i++)
		if (Sensors_SQLite_Setting.sensors[i])
		{	
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
	     }
		data.close();
		for (int i=0; i<13; i++)
			if (file[i].exists()) 
				file[i].delete();
		//super.onDestroy();
		
	}
	
}
