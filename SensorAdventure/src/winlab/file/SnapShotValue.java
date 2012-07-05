package winlab.file;

import java.io.File;
import java.io.FileWriter;

import winlab.sql.Sensors_SQLite_Setting;
import winlab.sql.SnapShot_SQL;

import android.os.Environment;

public class SnapShotValue {

	public SnapShotValue(){}
	
	public static double[][] instantValue=new double[13][]; 
			
	public static void set(){
		for (int i=0; i<13; i++)
			switch (i) {
			   case 0:
			   case 1:
			   case 2:
			   case 3:
			   case 8:
			   case 9: instantValue[i]=new double[3];
			           for (int j=0; j<3; j++) instantValue[i][j]=0;
			           break;
			   case 10: instantValue[i]=new double[4];
			           for (int j=0; j<4; j++) instantValue[i][j]=0;
			           break;
			   default: instantValue[i]=new double[1]; 
			            instantValue[i][0]=0;
			            break;
			}
	}
	public static void reset(){
		for (int i=0; i<13; i++)
			switch (i) {
			   case 0:
			   case 1:
			   case 2:
			   case 3:
			   case 8:
			   case 9: 
			           for (int j=0; j<3; j++) instantValue[i][j]=0;
			           break;
			   case 10:
			           for (int j=0; j<4; j++) instantValue[i][j]=0;
			           break;
			   default:  
			            instantValue[i][0]=0;
			            break;
			}
		
	}

   private static File path=SensorSetting.path;
   private static String fileName="Instant_Reading.txt";
   private static File file = new File(path, fileName);
   private static FileWriter output;
   private static boolean flag=true;
   public static void print(){
	    String str="";
	    str=str+"Timestamp (ms): "+String.format("%d",System.currentTimeMillis())+"\n";
	   try {
			path.mkdirs();
			file.setWritable(true);
			if (flag)
			 output = new FileWriter(file);
			  else 
				output = new FileWriter(file,true);
			flag=false;
			for (int i=0; i<13; i++)
				if (SensorSetting.sensors[i])
				{
					switch (i+1) {
					case 1:
					    str=str+"Accelerometer x (m/s^2): "+String.format("%17.10f",SnapShotValue.instantValue[i][0])+"\n";
					    str=str+"Accelerometer y (m/s^2): "+String.format("%17.10f",SnapShotValue.instantValue[i][1])+"\n";
					    str=str+"Accelerometer z (m/s^2): "+String.format("%17.10f",SnapShotValue.instantValue[i][2])+"\n";
						break;
					case 2:
						str=str+"Magnetic Field x (uT): "+String.format("%17.10f",SnapShotValue.instantValue[i][0])+"\n";
					    str=str+"Magnetic Field y (uT): "+String.format("%17.10f",SnapShotValue.instantValue[i][1])+"\n";
					    str=str+"Magnetic Field z (uT): "+String.format("%17.10f",SnapShotValue.instantValue[i][2])+"\n";
						break;
					case 3:
						str=str+"Orientation x (degrees): "+String.format("%17.10f",SnapShotValue.instantValue[i][0])+"\n";
					    str=str+"Orientation y (degrees): "+String.format("%17.10f",SnapShotValue.instantValue[i][1])+"\n";
					    str=str+"Orientation z (degrees): "+String.format("%17.10f",SnapShotValue.instantValue[i][2])+"\n";
						break;
					case 4:
						str=str+"Gyroscope x (rad/s): "+String.format("%17.10f",SnapShotValue.instantValue[i][0])+"\n";
					    str=str+"Gyroscope y (rad/s): "+String.format("%17.10f",SnapShotValue.instantValue[i][1])+"\n";
					    str=str+"Gyroscope z (rad/s): "+String.format("%17.10f",SnapShotValue.instantValue[i][2])+"\n";
						
						break;
					case 5:
						str=str+"Light (lx): "+String.format("%17.10f",SnapShotValue.instantValue[i][0])+"\n";
						break;
					case 6:
						str=str+"Pressure (hPa): "+String.format("%17.10f",SnapShotValue.instantValue[i][0])+"\n";
						break;
					case 7:
						str=str+"Device Temperature (degree Celsius): "+String.format("%17.10f",SnapShotValue.instantValue[i][0])+"\n";
						break;
					case 8:
						str=str+"Proximity (cm)"+String.format("%17.10f",SnapShotValue.instantValue[i][0])+"\n";
					    break;
					
					case 9:
						str=str+"Gravity x (m/s^2): "+String.format("%17.10f",SnapShotValue.instantValue[i][0])+"\n";
					    str=str+"Gravity x (m/s^2): "+String.format("%17.10f",SnapShotValue.instantValue[i][1])+"\n";
					    str=str+"Gravity x (m/s^2): "+String.format("%17.10f",SnapShotValue.instantValue[i][2])+"\n";
						
						break;
					case 10:
						str=str+"Linear Accelerometer x (m/s^2): "+String.format("%17.10f",SnapShotValue.instantValue[i][0])+"\n";
					    str=str+"Linear Accelerometer y (m/s^2): "+String.format("%17.10f",SnapShotValue.instantValue[i][1])+"\n";
					    str=str+"Linear Accelerometer z (m/s^2): "+String.format("%17.10f",SnapShotValue.instantValue[i][2])+"\n";
						
						break;
					case 11:
						str=str+"Rotation Vector x unitless: "+String.format("%17.10f",SnapShotValue.instantValue[i][0])+"\n";
					    str=str+"Rotation Vector y unitless: "+String.format("%17.10f",SnapShotValue.instantValue[i][1])+"\n";
					    str=str+"Rotation Vector z unitless: "+String.format("%17.10f",SnapShotValue.instantValue[i][2])+"\n";
					    if (Math.abs(SnapShotValue.instantValue[i][3]-0)<1.0e-15)
					    str=str+"Rotation Vector scalar:                NA\n";
					    else
					    str=str+"Rotation Vector scalar: "+String.format("%17.10f",SnapShotValue.instantValue[i][3])+"\n";
						
						break;
					case 12:
						str=str+"Relative Humidity %: "+String.format("%17.10f",SnapShotValue.instantValue[i][0])+"\n";
						break;
					case 13:
						str=str+"Ambient air temperature (degree Celsius): "+String.format("%17.10f",SnapShotValue.instantValue[i][0])+"\n";
						break;
					}
					str=str+"\n";
				}
			str=str+"-----------------------------------------------------\n";
			output.write(str);
			output.close();
	   }catch(Exception e){}
	   
   }
public static void insertSQL(SnapShot_SQL instant){
	String timestamp,str1,str2,str3,str4;
	for (int i=0;i<13;i++)
		if (Sensors_SQLite_Setting.sensors[i])
			switch (i+1) {
			case 1: case 2: case 3: case 4: case 9: case 10:
					timestamp=String.format("%d",System.currentTimeMillis());
					str1 = String.format("%.10f", instantValue[i][0]);
					str2 = String.format("%.10f",instantValue[i][1]);
					str3 = String.format("%.10f",instantValue[i][2]);
					instant.insertTitle1(timestamp, str1, str2, str3,i);
					break;
			case 5: case 6: case 7: case 8: case 12: case 13:
				    timestamp=String.format("%d",System.currentTimeMillis());
				    str1 = String.format("%.10f", instantValue[i][0]);	
				    instant.insertTitle2(timestamp, str1,i);
					break;
			 case 11:
				 timestamp=String.format("%d",System.currentTimeMillis());
					str1 = String.format("%.10f", instantValue[i][0]);
					str2 = String.format("%.10f",instantValue[i][1]);
					str3 = String.format("%.10f",instantValue[i][2]);
					if (Math.abs(SnapShotValue.instantValue[i][3]-0)<1.0e-15)
						str4="NA";
					else
					    str4 = String.format("%.10f",instantValue[i][3]);
					instant.insertTitle3(timestamp, str1, str2, str3,str4,i);
					break;
			}
}
}
