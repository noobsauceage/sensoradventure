package winlab.SensorGUI;

/*This is an Expandable List Adapter that is used to transform a data
 * structure of [TextView] [CheckBox] into the group row and a data
 * structure of [TextView][EditText][TextView] into a child row.
 * To use this GUI, you need to make sure you have the following XML files:
 * group_row.xml
 * child_row.xml
 * Written by G.D.C. and Xianyi Gao.
 */

import java.util.ArrayList;

import winlab.sensoradventure.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class SensorAdapter extends BaseExpandableListAdapter {

	private ArrayList<Group> groups;	// ArrayList containing all the groups from main
	private LayoutInflater inflater;	// Inflater to inflate group_row & child_row from XML into layouts
	public CheckBox[] checkbox;			// Array of CheckBox to hold CheckBox memory
	public EditText[] edittext;			// Array of EditTexts to hold EditText memory
	public static String[] value;		// Array of strings to hold strings written in EditTexts 

	// Public constructor to initialize key data members
	public SensorAdapter(Context context, ArrayList<Group> groups) {
		this.groups = groups;
		inflater = LayoutInflater.from(context);

		checkbox = new CheckBox[groups.size()];
		
		// This loop is needed to avoid NullPointerException in update()
		// See main activity for update() code
		for(int i = 0; i< groups.size(); i++){		
			checkbox[i] = new CheckBox(context);
			checkbox[i].setChecked(false);}
		
		edittext = new EditText[groups.size()];
		value = new String[groups.size()];
	}
	
	// Method to retrieve a specific child of a group.
	public Object getChild(int groupPosition, int childPosition) {

		return groups.get(groupPosition).getChild(childPosition);
	}
	
	// Retrieve an Id of a child. Not really used.
	public long getChildId(int groupPosition, int childPosition) {
		return (long) (groupPosition * 1024 + childPosition); 
	}

	// Creates the view for the Child.
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		View v = null;
		
		/* This set of code recycles the view.
		 * It is commented out to prevent problems with scrolling up and down.
		 * Should anyone fix the scrolling memory problem, it would be great commenting
		 * this out is not good for optimization.
		if (convertView != null)
			v = convertView;
		else */
			
			v = inflater.inflate(R.layout.child_row, parent, false);
			
		/* The following block of code retrieves the specific child that needs to be
		 * drawn and initializes its widgets.
		 */
			
		Child achild = (Child) getChild(groupPosition, childPosition);	
		TextView field = (TextView) v.findViewById(R.id.field);			
		if (field != null)
			field.setText(achild.getField());
		TextView unit = (TextView) v.findViewById(R.id.unit);
		if (unit != null)
			unit.setText(achild.getUnit());
		edittext[groupPosition] = (EditText) v.findViewById(R.id.Field);
		if (edittext[groupPosition] != null)
			edittext[groupPosition].setText(value[groupPosition]);
		return v;
	}

	// Get the # of children in a group. Currently by design it is 1 for all groups.
	public int getChildrenCount(int groupPosition) {
		return groups.get(groupPosition).getChildren().size();
	}
	// Retrieve a particular group.
	public Object getGroup(int groupPosition) {
		return groups.get(groupPosition);
	}
	// Retrieve the number of groups.
	public int getGroupCount() {
		return groups.size();
	}

	// Retrieve the group Id. Not really used.
	public long getGroupId(int groupPosition) {
		return (long) (groupPosition * 1024); 
	}

	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		View v = null;
		
		/* This set of code recycles the view.
		 * It is commented out to prevent problems with scrolling up and down.
		 * Should anyone fix the scrolling memory problem, it would be great commenting
		 * this out is not good for optimization.
		if (convertView != null)
			v = convertView;
		else */
		
		v = inflater.inflate(R.layout.group_row, parent, false);
		
		/* The following block of code retrieves the specific child that needs to be
		 * drawn and initializes its widgets.
		 */

		TextView name = (TextView) v.findViewById(R.id.name);
		if (name != null)
			name.setText(groups.get(groupPosition).getName());
		checkbox[groupPosition] = (CheckBox) v.findViewById(R.id.checkBox1);
		if (checkbox[groupPosition] != null) {
			checkbox[groupPosition].setChecked(groups.get(groupPosition)
					.getState());
		}
		return v;
	}
	
	// The following methods are not used but are required to be here.
	public boolean hasStableIds() {
		return true;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public void onGroupCollapsed(int groupPosition) {

	}

	public void onGroupExpanded(int groupPosition) {
	}
}