package winlab.SensorGUI;

import java.util.ArrayList;

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