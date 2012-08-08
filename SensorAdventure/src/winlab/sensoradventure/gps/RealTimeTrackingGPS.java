package winlab.sensoradventure.gps;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import winlab.sensoradventure.R;
import winlab.sensoradventure.gps.GPSLoggerService.MyLocationListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
public class RealTimeTrackingGPS extends MapActivity {
	 private MapView mapView;
	 
		private LocationManager lm;
		private LocationListener locationListener;
  
		    MapController mc;
		    GeoPoint p;
		    MapOverlay mapOverlay = new MapOverlay();
		    TextView stsatview;
		    private boolean satelliteview = true;
		    TextView latitudetxt;
		    TextView longitudetxt;
		    TextView altitudetxt;
		    TextView bearingtxt;
		    TextView speedtxt;
		    TextView accuracytxt;
		    TextView notificationtxt; 
    @Override
    public void onCreate(Bundle savedInstanceState) {
    
        super.onCreate(savedInstanceState);
        setContentView(R.layout.realtimetrack);
        mapView = (MapView)findViewById(R.id.mapview);
        latitudetxt =(TextView)findViewById(R.id.latitude);
        longitudetxt =(TextView)findViewById(R.id.longitude);
        altitudetxt =(TextView)findViewById(R.id.altitude);
        bearingtxt =(TextView)findViewById(R.id.bearing);
        speedtxt =(TextView)findViewById(R.id.speed);
        accuracytxt =(TextView)findViewById(R.id.accuracy);
        notificationtxt =(TextView)findViewById(R.id.notification);
        
        mapView.setBuiltInZoomControls(true);
        mc = mapView.getController();
        mc.setZoom(16);
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationListener = new MyLocationListener();
		 boolean isEnabled = isGPSenabled();
		if(isEnabled)
		{
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 
				0, 
				0,
				locationListener);
		}
		else
		{
			lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 
					0, 
					0,
					locationListener);
		}
		
		stsatview = (Button) findViewById(R.id.stsatview);
        
		stsatview.setOnClickListener(new OnClickListener() {

	        public void onClick(View v) {
	        	if(satelliteview==false)
	        	{
	        		satelliteview=true;
	        	}
	        	else
	        	{
	        		satelliteview = false;
	        	}
	        	mapView.setSatellite(satelliteview);
	        }
	    });
        
    }
	/* (non-Javadoc)
	 * @see com.google.android.maps.MapActivity#isRouteDisplayed()
	 */
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
 

    private boolean isGPSenabled()
  {   
      final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
      
      return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
  }
    
	private void shutdownLoggerService() {
		lm.removeUpdates(locationListener);
	} 

public class MyLocationListener implements LocationListener {
		
		public void onLocationChanged(Location loc) {
			Double s = loc.getLatitude();
			Double m  = loc.getLongitude();
			Log.i(s.toString(),m.toString());
			if (loc != null) {
			       double lat = loc .getLatitude();
		            double lng = loc .getLongitude();
		       
		            GeoPoint point = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));
		            mc.animateTo(point, new Message());
		            mapOverlay.setPointToDraw(point);
		            List<Overlay> listOfOverlays = mapView.getOverlays();
		            listOfOverlays.clear();
		            listOfOverlays.add(mapOverlay);
		            Double latitudes = loc.getLatitude();
		            String lati = latitudes.toString();
		            latitudetxt.setText(lati);
		            
		            Double longitudes = loc.getLongitude();
		            String longi = longitudes.toString();
		            longitudetxt.setText(longi);
		            
		            Double altitudes = loc.getAltitude();
		            String alti = altitudes.toString();
		            altitudetxt.setText(alti);
		            
		            Float Bearing = loc.getBearing();
		            String beari = Bearing.toString();
		            bearingtxt.setText(beari);
		            
		            Float Speed = loc.getSpeed();
		            String speedi = Speed.toString();
		            speedtxt.setText(speedi);
		            
		            Float accuracy = loc.getAccuracy();
		            String accuri = accuracy.toString();
		            accuracytxt.setText(accuri);

		            
			}
			
            
            boolean isEnabled = isGPSenabled();
    		if(isEnabled)
    		{
    		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 
    				0, 
    				0,
    				locationListener);
	            notificationtxt.setText("Provider is GPS");
    		}
    		else
    		{
    			lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 
    					0, 
    					0,
    					locationListener);
    			  notificationtxt.setText(" Provider is Network, Enable GPS for GPS Updates");
    		}
    		
		}

		public void onProviderDisabled(String provider) {
		 

		}

		public void onProviderEnabled(String provider) {
		 

		}

		/* (non-Javadoc)
		 * @see android.location.LocationListener#onStatusChanged(java.lang.String, int, android.os.Bundle)
		 */
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub
			
		}

		 

	}

 

 

	@Override
	public void onDestroy() {
		super.onDestroy();	
		shutdownLoggerService();
	}
 
	 public MapView getMapView() {
	        return this.mapView;
	    }

	    private void initComponents() {
	        mapView = (MapView) findViewById(R.id.mapview);
	    }

 
	    
	class MapOverlay extends Overlay {
        private GeoPoint pointToDraw;

        public void setPointToDraw(GeoPoint point) {
            pointToDraw = point;
        }

        public GeoPoint getPointToDraw() {
            return pointToDraw;
        }

        public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
                long when) {
            super.draw(canvas, mapView, shadow);

            Point screenPts = new Point();
            mapView.getProjection().toPixels(pointToDraw, screenPts);

            Bitmap bmp = BitmapFactory.decodeResource(getResources(),
                    R.drawable.ic_launcher);
            canvas.drawBitmap(bmp, screenPts.x, screenPts.y - 24, null);
            return true;
        }
    }
}
