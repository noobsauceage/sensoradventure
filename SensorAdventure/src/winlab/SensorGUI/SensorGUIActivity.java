package winlab.SensorGUI;

import android.os.Bundle;
import android.app.ExpandableListActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import java.util.ArrayList;
import android.util.Log;

public class SensorGUIActivity extends ExpandableListActivity
{
	    private SensorAdapter sensorAdapter;

	    /** Called when the activity is first created. */
	    @Override
	    public void onCreate(Bundle bundle)
	    {
	        super.onCreate(bundle);
	        setContentView(R.layout.main);
	        ArrayList<String> groupNames = new ArrayList<String>();
	        String[] sensorNames = {"Accelerometer","Temperature","Gravity","Gyroscope","Light","Magnetic Field"};
	        ArrayList<ArrayList<Sensor>> sensors = new ArrayList<ArrayList<Sensor>>(); 
	        ArrayList<Sensor> sensor = new ArrayList<Sensor>();
	        ArrayList<Boolean> checks = new ArrayList<Boolean>();
	        checks.add(false);
	        checks.add(false);
	        
	        groupNames.add( "Gyroscope" );
		    groupNames.add( "Microphone" );
	        sensor.add(new Sensor("Sampling Rate","Hz")); 
	        sensor.add(new Sensor("Buffer Size", ""));
	        sensors.add( sensor );
	        sensor = new ArrayList<Sensor>();
			sensor.add( new Sensor( "Sampling Rate","Hz"));
	        sensors.add( sensor );

			sensorAdapter = new SensorAdapter( this,groupNames, sensors,checks );
			setListAdapter( sensorAdapter );
	    }

	    public void onContentChanged  () {
	        super.onContentChanged();
	    }

	    public boolean onChildClick(
	            ExpandableListView parent, 
	            View v, 
	            int groupPosition,
	            int childPosition,
	            long id) {
	        CheckBox cb = (CheckBox)v.findViewById( R.id.checkBox1);
	        cb.toggle();

	        return false;
	    }
	    

	    	
	    }
	

