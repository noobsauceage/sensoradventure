package winlab.sql;

import android.content.Context;
import android.hardware.SensorManager;

public class Sensors_SQLite_Setting{
	

	private SensorManager mSensorManager;

	private Context context;

	public static boolean sensors[] = {true,true,true,true,true,true,true,true,true,true,true,true,true,true};
    public static int updateRate[]={1,1,1,1,1,1,1,1,1,1,1,1,1};
	public void testAvailableSensors() {
		
		mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		for (int i=1; i<=13; i++)
		if (mSensorManager.getDefaultSensor(i) == null)
			sensors[i-1]=false;
		
	}
	
	public static void setRate(int[] Rate){
		for (int i=0; i<13; i++)
			updateRate[i]=Rate[i];
	}
	
	public Sensors_SQLite_Setting(Context con) {
		this.context=con;
		
	}
	
	public void selectSensors(boolean selected[]){
		for (int i=0; i<14; i++)
			sensors[i]=selected[i];
	}

}
