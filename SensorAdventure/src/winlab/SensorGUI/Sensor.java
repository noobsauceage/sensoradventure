package winlab.SensorGUI;

public class Sensor {
    public String field = null;
    public String unit = null;

    public Sensor( String field, String unit) {
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
