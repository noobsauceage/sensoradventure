/**
 * 
 */
package winlab.sensoradventure.gps;

import java.io.File;

import com.google.android.maps.MapActivity;

import winlab.sensoradventure.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AdditionalFeaturesGPS extends  Activity{
 
	Button realtimetrack;
	Button trackmap;
	Button database;
	EditText folder;
	EditText file;
	 String foldername; 
	 String filename;  
	String b[]=null;
	String c[]=null;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.advancefeaturesgps);
       folder = (EditText) findViewById(R.id.editText1);
  	 file = (EditText) findViewById(R.id.editText2);
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
	        	  	 file = (EditText) findViewById(R.id.editText2);
	             foldername = folder.getText().toString();
	             filename = file.getText().toString();
	             b = filename.split(",");
	        	 for(int i=0;i<b.length;i++)
	        	 {
	        	 Log.i( foldername,b[i]);
	        	 }
	    
	        	 if( foldername.length() !=0 && filename.length()  !=0)
		         {
	        		 File folder2 = new File(Environment.getExternalStorageDirectory(),foldername);
	        		 if (!folder2.exists())
	        			{
	        			   getUriListForImages1();
	        			}
	        		 int j=0;
	        		 for(int i=0;i<b.length;i++)
        			 {
        				 String am = b[i];
        			  Log.i("Value",am);
        			 File file1 = new File(folder2.getPath(),am);
        			 if (file1.exists())
        				{
        				 c[j] = b[i];
        				 j++;
        				}   
        			 }

        					Intent myIntent = new Intent(AdditionalFeaturesGPS.this, TrackActivity.class);
        			        	myIntent.putExtra("folder", foldername);
        			        	myIntent.putExtra("file", c);
        			        	startActivity(myIntent);
         
	        		 
		         }
	        	 else
	        	 {
	        		 getUriListForImages1();
	        	 }
	        	Intent myIntent = new Intent(AdditionalFeaturesGPS.this, TrackActivity.class);
	        //	myIntent.putExtra("folder", folder1);
	        //	myIntent.putExtra("file", b);
	        	AdditionalFeaturesGPS.this.startActivity(myIntent);
	        
	        }
	    });
       
        database = (Button) findViewById(R.id.database);
        database.setOnClickListener(new OnClickListener() {

	        public void onClick(View v) {
	        	Intent myIntent = new Intent(AdditionalFeaturesGPS.this, BackupDatabase.class);
	        	AdditionalFeaturesGPS.this.startActivity(myIntent);
	        }
	    });
    }
 	
    
	private void getUriListForImages1()
	{
		Toast.makeText(this, "Folder/File does not exist or is Empty", Toast.LENGTH_SHORT).show();
	}
	
 
	
    
}
