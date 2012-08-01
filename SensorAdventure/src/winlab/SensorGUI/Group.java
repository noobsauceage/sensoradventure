package winlab.SensorGUI;

import java.util.ArrayList;
 /*This is a Group class used in the ExpandableListView in main activity.
  * It contains it's children; an ArrayList of Child objects. Every time
  * a child is added to the children field, we increase the number of fields
  * under the expanded group.
  * The name refers to the name of the group ("Microphone" or "Accelerometer.
  * The boolean variable state refers to the boolean value of the CheckBox 'On'
  * next to it in the GUI.
  * 
  */
public class Group {

	private ArrayList<Child> children = new ArrayList<Child>();
	private String name;
	private boolean state;

	public Group(String name, ArrayList<Child> children, boolean state) {
		this.children = children;
		this.state = state;
		this.name = name;

	}

	public void setState(boolean state1) {
		state = state1;
	}

	public Child getChild(int childPosition) {
		return children.get(childPosition);
	}

	public boolean getState() {
		return state;
	}

	public String getName() {
		return name;
	}

	public ArrayList<Child> getChildren() {
		return children;
	}

}