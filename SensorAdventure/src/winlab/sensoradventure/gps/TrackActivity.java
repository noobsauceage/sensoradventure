/**
 * 
 */
package winlab.sensoradventure.gps;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import winlab.sensoradventure.R;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.Toast;
 
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

/**
 * @author malathidharmalingam
 *
 */
public class TrackActivity extends MapActivity {
	private MapView mapView;
	List<String> Latitude = new ArrayList<String>();
	List<String> Longitude = new ArrayList<String>();
	String file[] ={"filea1.txt","filea2.txt"};
	String folder1;
	@Override
	protected boolean isRouteDisplayed() { return false; }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.trackgps);
        mapView = (MapView)findViewById(R.id.mapview1);
 
      //  File folder = new File(Environment.getExternalStorageDirectory(), "GPSLogger01");
    //	boolean isNew = false;
 
    	Bundle extras = getIntent().getExtras(); 
        /*
     
    		Log.i("I am ","inside");
    	   folder1 = extras.getString("folder");
    	   file = extras.getString("file");
    		Log.i("folder",folder1);
        	for(int i=0;i<file.length;i++)	
        	{		
        		Log.i("file",file[i]);
        	}
        */
 
    	for(int i=0;i<file.length;i++)
    	{
        drawPath( mapView,file[i]);
    	}
 
       // drawPath( mapView);
        mapView.setBuiltInZoomControls(true);    
        mapView.getController().setZoom(15);
      
    }
  
 
    private void drawPath(MapView mapView,String file ) {
    	File dir = new File(Environment.getExternalStorageDirectory(), "GPSLogger00");
    	File file1 = new File(dir.getPath(),file);
    	if (file1.exists())
		{
    		Log.i("File","Existssssss");
		}		
		 
    	
    	try {
 
				RouteOverlay routeOverlay = new RouteOverlay();
				BufferedReader br = new BufferedReader(new FileReader(file1));
				 String word;
				 int count=0;
				 while((word=br.readLine()) != null)
				 { 
					 
					 String a[] = word.split(",");
					 a[2]= a[2].trim();
					 a[1] = a[1].trim();
					 Log.i(a[1],a[2]);
 
					 Integer s= count;
					 Log.i("count", s.toString());
					 
					 Log.i("word",word);
					 Latitude.add(a[1]);
					 Longitude.add(a[2]); 
	 
					 
				 }
				 
					for(int i=0;i<Latitude.size();i++)
					{
						Integer s = i;
						GeoPoint geoPoint = new GeoPoint(
								(int) (Double.parseDouble(Latitude.get(i)) * 1E6),
								(int) (Double.parseDouble(Longitude.get(i)) * 1E6));
						routeOverlay.addGeoPoint(geoPoint);
					}
					
				mapView.getOverlays().add(routeOverlay);
	 
		} catch (Exception e) {
			Log.w("RoutePath", e.toString());
		}
    }

}
