package winlab.SensorGUI;

import java.util.ArrayList;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class SensorAdapter extends BaseExpandableListAdapter {

	private Context context;
	private ArrayList<Parent> parents;
	private LayoutInflater inflater;
    public CheckBox[] checkbox=new CheckBox[100];
    public EditText[] edittext=new EditText[100];
    public static String[] value = new String[100]; 
	public SensorAdapter(Context context, ArrayList<Parent> parents) {
		this.context = context;
		this.parents = parents;
		inflater = LayoutInflater.from(context);

	}

	public Object getChild(int groupPosition, int childPosition) {

		return parents.get(groupPosition).getChild(childPosition);
	}

	public long getChildId(int groupPosition, int childPosition) {
		return (long) (groupPosition * 1024 + childPosition); // Max 1024
																// children per
																// group
	}

	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		View v = null;
		if (convertView != null)
			v = convertView;
		else
			v = inflater.inflate(R.layout.child_row, parent, false);
		Child achild = (Child) getChild(groupPosition, childPosition);
		TextView field = (TextView) v.findViewById(R.id.field);
		if (field != null)
			field.setText(achild.getField());
		TextView unit = (TextView) v.findViewById(R.id.unit);
		if (unit != null)
			unit.setText(achild.getUnit());
		edittext[groupPosition+childPosition]=(EditText) v.findViewById(R.id.Field);
        if (edittext[groupPosition+childPosition]!=null)
        	edittext[groupPosition+childPosition].setText(value[groupPosition+childPosition]);
		return v;
	}

	public int getChildrenCount(int groupPosition) {
		return parents.get(groupPosition).getChildren().size();
	}

	public Object getGroup(int groupPosition) {
		return parents.get(groupPosition);
	}

	public int getGroupCount() {
		return parents.size();
	}

	public long getGroupId(int groupPosition) {
		return (long) (groupPosition * 1024); // To be consistent with
												// getChildId
	}

	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		View v = null;
		if (convertView != null)
			v = convertView;
		else
			v = inflater.inflate(R.layout.group_row, parent, false);

		TextView name = (TextView) v.findViewById(R.id.name);
		if (name != null)
			name.setText(parents.get(groupPosition).getName());
		checkbox[groupPosition] = (CheckBox) v.findViewById(R.id.checkBox1);
		if (checkbox[groupPosition] != null)
		{
			checkbox[groupPosition].setChecked(parents.get(groupPosition).getState());
		}
		return v;
	}

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