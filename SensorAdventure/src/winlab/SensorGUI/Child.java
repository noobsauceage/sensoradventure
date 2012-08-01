package winlab.SensorGUI;

// This is a Child class for the ExpandableListView in main activity.
// It contains two strings: field and unit.
public class Child {
	private String field;	// Field is the text to the left of EditText object.
							// It would typically be "Update Rate" or "Sampling Rate".
	private String unit;	// Unit is the text to the right of EditText object.
							// It would typically be "ms" or "Hz".

	public Child(String field, String unit) {
		this.field = field;
		this.unit = unit;

	}

	public String getField() {
		return field;
	}

	public String getUnit() {
		return unit;
	}
}
