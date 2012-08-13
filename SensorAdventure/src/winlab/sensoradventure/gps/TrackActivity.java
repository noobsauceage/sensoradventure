/**
 * 
 */
package winlab.sensoradventure.gps;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import winlab.sensoradventure.R;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

/**
 * @author malathidharmalingam
 */
public class TrackActivity extends MapActivity {
	private MapView mapView;
	List<String> Latitude = new ArrayList<String>();
	List<String> Longitude = new ArrayList<String>();
	String file[] ;
	String folder1;
	public static int counter11 = 0;
	@Override
	protected boolean isRouteDisplayed() { return false; }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.trackgps);
        Intent intent= getIntent();
        folder1 = intent.getStringExtra("foldername");
        file = intent.getStringArrayExtra("filename");
        mapView = (MapView)findViewById(R.id.mapview1);
    	drawPath( mapView,file[0]);
    	drawPath2( mapView,file[1]);
    	mapView.setBuiltInZoomControls(true);    
    	mapView.getController().setZoom(15);
    
    }
    private void drawPath(MapView mapView,String file ) {
    	File dir = new File(Environment.getExternalStorageDirectory(),folder1);
    	File file1 = new File(dir.getPath(),file);
    	try {
 
				RouteOverlay routeOverlay = new RouteOverlay();
				BufferedReader br = new BufferedReader(new FileReader(file1));
				 String word;
				 while((word=br.readLine()) != null)
				 { 	 
					 String a[] = word.split(",");
					 a[2]= a[2].trim();
					 a[1] = a[1].trim();
					 Latitude.add(a[1]);
					 Longitude.add(a[2]); 	 
				 }	 
					for(int i=0;i<Latitude.size();i++)
					{
						GeoPoint geoPoint = new GeoPoint(
								(int) (Double.parseDouble(Latitude.get(i)) * 1E6),
								(int) (Double.parseDouble(Longitude.get(i)) * 1E6));
						routeOverlay.addGeoPoint(geoPoint);
					}	
				mapView.getOverlays().add(routeOverlay);
				Latitude.clear();
				Longitude.clear();
		        
				 
		} catch (Exception e) {
			Log.w("RoutePath", e.toString());
		}
    }
    
    private void drawPath2(MapView mapView,String file ) {
    	File dir = new File(Environment.getExternalStorageDirectory(),folder1);
    	File file1 = new File(dir.getPath(),file);
    	try {
				RouteOverlay routeOverlay1 = new RouteOverlay();
				BufferedReader br = new BufferedReader(new FileReader(file1));
				 String word;
				 while((word=br.readLine()) != null)
				 { 	 
					 String a[] = word.split(",");
					 a[2]= a[2].trim();
					 a[1] = a[1].trim();
					 Latitude.add(a[1]);
					 Longitude.add(a[2]); 	 
				 }	 
					for(int i=0;i<Latitude.size();i++)
					{
						GeoPoint geoPoint = new GeoPoint(
								(int) (Double.parseDouble(Latitude.get(i)) * 1E6),
								(int) (Double.parseDouble(Longitude.get(i)) * 1E6));
						routeOverlay1.addGeoPoint(geoPoint);
					}	
				mapView.getOverlays().add(routeOverlay1);
				Latitude.clear();
				Longitude.clear();
		        
				 
		} catch (Exception e) {
			Log.w("RoutePath", e.toString());
		}
    }

}
