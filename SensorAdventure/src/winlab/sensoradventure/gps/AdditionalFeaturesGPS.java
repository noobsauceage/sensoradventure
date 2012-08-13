package winlab.sensoradventure.gps;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import winlab.sensoradventure.R;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
 
public class AdditionalFeaturesGPS extends  Activity{
	private Button realtimetrack;
	private Button trackmap;
	private Button database;
	private EditText folder;
	private EditText file;
	private String foldername; 
	private String filename;  
	private static String b[];
	private static String c[];
	private static String[]  file_default_string1;
	private static FileWriter output;
	private static String[]  file_default_string;
	private static String[] getfile;
	private static String[] relgetfile;
	List<String> content = new ArrayList<String>();
	private static int counter =0;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.advancefeaturesgps);
        folder = (EditText) findViewById(R.id.editText1);
  	 	file   = (EditText) findViewById(R.id.editText2);
  	 	Resources res = getResources();
  	 	file_default_string = res.getStringArray(R.array.gps_default);
  	 	File foldergps = new File(Environment.getExternalStorageDirectory() + File.separator + file_default_string[8]);
  	 	try {
  	 		BufferedReader br = new BufferedReader(new FileReader(foldergps));
  	 		getfile = br.readLine().split(",");
  	 		folder.setText(getfile[0]);
  	 		file.setText(getfile[1]);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        realtimetrack = (Button) findViewById(R.id.realtimegps);
        realtimetrack.setOnClickListener(new OnClickListener() {
	        public void onClick(View v) {
	        	Intent myIntent = new Intent(AdditionalFeaturesGPS.this, RealTimeTrackingGPS.class);
	        	AdditionalFeaturesGPS.this.startActivity(myIntent);
	        }
	    });
        
        
        trackmap = (Button) findViewById(R.id.trackmap);    
        trackmap.setOnClickListener(new OnClickListener() {
	        public void onClick(View v) {
	        	   folder = (EditText) findViewById(R.id.editText1);
	        	   file   = (EditText) findViewById(R.id.editText2); 
	        	   foldername = folder.getText().toString();
	        	   filename   = file.getText().toString();
	        	   b = filename.split(",");
	        	 for(int i=0;i<b.length;i++){
	        	 content.add(b[i]);
	        	 }
	        	 if( foldername.length() !=0 && filename.length()  !=0){
	        		 File folder2 = new File(Environment.getExternalStorageDirectory(),foldername);
	        		 if (!folder2.exists()){
	        			   getUriListForImages1();
	        			}
	        		
	        		 for(int i=0;i<b.length;i++){
	        			 Integer sac = b.length;
        				 String am = b[i];
        				 File kmlFile = new File(folder2,b[i]);  
        			  if (kmlFile.exists())
        				{
        				  Integer s = counter;
        				  String counter1  = b[i];
        			 	  c=b; 
 ;        			 	 
         				}
        				}
		         }
	        	 else{
	        		 getUriListForImages1();
	        	 }
	        	 counter=0;
	        	Intent myIntent = new Intent(AdditionalFeaturesGPS.this, TrackActivity.class);
	        	myIntent.putExtra("foldername", foldername);
    		 	myIntent.putExtra("filename",c);
    		 	Log.i("Have come here","lala");
	        	startActivity(myIntent);
	        
	        }
	    });
       
        database = (Button) findViewById(R.id.database);
        database.setOnClickListener(new OnClickListener() {

	        public void onClick(View v) {
	        	try {
					doInBackground1();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	try {
					copy();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	    });
    }
 	   
    public  Boolean doInBackground1() throws IOException {
		 File sd = Environment.getExternalStorageDirectory();
	        File data = Environment.getDataDirectory();
	        
	        if (sd.canWrite()) {
	        	Resources res = getResources();
				file_default_string1 = res.getStringArray(R.array.gps_default);
	            String currentDBPath = file_default_string1[4]+"/"+file_default_string1[1];
	            String backupDBPath = file_default_string1[1];
	            Log.i(currentDBPath, backupDBPath);
	            File currentDB = new File(data, currentDBPath);
	            File backupDB = new File(sd, backupDBPath);
	            backupDB.delete();
	            if (currentDB.exists()) {
	                FileChannel src = new FileInputStream(currentDB).getChannel();
	                FileChannel dst = new FileOutputStream(backupDB).getChannel();
	                dst.transferFrom(src, 0, src.size());
	                src.close();
	                dst.close();
	            }
	        }
	        
	        
			return null;
	        
    }
	
	public void copyFile(File src, File dst) throws IOException {
       FileChannel inChannel = new FileInputStream(src).getChannel();
       FileChannel outChannel = new FileOutputStream(dst).getChannel();
       try {
          inChannel.transferTo(0, inChannel.size(), outChannel);
       } finally {
          if (inChannel != null)
             inChannel.close();
          if (outChannel != null)
             outChannel.close();
       }
    }
	
	public void copy() throws IOException {
			File sdcard = Environment.getExternalStorageDirectory();
			Resources res = getResources();
			file_default_string1 = res.getStringArray(R.array.gps_default);
			File kmlFile = new File(sdcard,file_default_string1[3]);
				kmlFile.delete();
				kmlFile.createNewFile();	
				output = new FileWriter(kmlFile,true);
				String xml = "  TIME (ms)    , device_id(IMEI),         LONG(deg)      ,         LAT(deg)        ,       ALT(m) 		   , 	 BEA(deg)   	     ,              ACCU(m)    , PROV     ,      SPEED(m/s) \n" ;
				output.write(xml);
				GPSLoggerSQLite data = new GPSLoggerSQLite(this,file_default_string1[1],file_default_string1[2]);
				data.open();
				Cursor c = data.getAllgpsdata(1);		
				 if (c.moveToFirst()) {
				do {	 	
					String result = String.format("%s%s%s%s%25s%s%25s%s%25s%s%25s%s%25s%s%10s%s%25s\n",c.getString(0),",",c.getString(1),",",c.getString(2),",",c.getString(3),",",c.getString(4),",",c.getString(5) ,",",c.getString(6),",",c.getString(7),",",c.getString(8));
					output.write(result);
				} while ((c.moveToNext()));
				output.close();
				data.close();
		}
	}					
	private void getUriListForImages1()
	{
		Toast.makeText(this, "Folder/File does not exist or is Empty", Toast.LENGTH_SHORT).show();
	}
}
