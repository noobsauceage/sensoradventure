package winlab.SensorGUI;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import java.util.ArrayList;

public class SensorAdapter extends BaseExpandableListAdapter {
	
    private Context context;
    private ArrayList<String> groups;
    private ArrayList<ArrayList<Sensor>> sensors;
    private LayoutInflater inflater;
    private int group = 0;
   
  
    private ArrayList<Boolean> checkboxStatus = new ArrayList<Boolean>();

    public SensorAdapter(Context context, 
                        ArrayList<String> groups,
						ArrayList<ArrayList<Sensor>> sensors, ArrayList<Boolean> values ) { 
        this.context = context;
		this.groups = groups;
        this.sensors = sensors;
        inflater = LayoutInflater.from( context );
        this.checkboxStatus = values;
        

        
    }

    public Object getChild(int groupPosition, int childPosition) {
        return sensors.get( groupPosition ).get( childPosition );
    }

    public long getChildId(int groupPosition, int childPosition) {
        return (long)( groupPosition*1024+childPosition );  // Max 1024 children per group
    }

    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View v = null;
        if( convertView != null )
            v = convertView;
        else
            v = inflater.inflate(R.layout.child_row, parent, false); 
        Sensor c = (Sensor)getChild( groupPosition, childPosition );
		TextView field = (TextView)v.findViewById( R.id.childname );
		if( field != null )
			field.setText( c.getField() );
		TextView unit = (TextView)v.findViewById( R.id.Unit );


		

        return v;
    }

    public int getChildrenCount(int groupPosition) {
        return sensors.get( groupPosition ).size();
    }

    public Object getGroup(int groupPosition) {
        return groups.get( groupPosition );        
    }

    public int getGroupCount() {
        return groups.size();
    }

    public long getGroupId(int groupPosition) {
        return (long)( groupPosition*1024 );  // To be consistent with getChildId
    } 

    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View v = null;
        if( convertView != null )
            v = convertView;
        else
            v = inflater.inflate(R.layout.group_row, parent, false); 
        String gt = (String)getGroup( groupPosition );
		TextView sensorGroup = (TextView)v.findViewById( R.id.childname );
		if( gt != null )
			sensorGroup.setText( gt );


		
        return v;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    } 

    public void onGroupCollapsed (int groupPosition) {

    	
    } 
    public void onGroupExpanded(int groupPosition) {}
    


}
