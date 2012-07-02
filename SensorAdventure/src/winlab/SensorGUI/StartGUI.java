package winlab.SensorGUI;



import winlab.sensoradventure.R;
import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class StartGUI extends Activity implements OnClickListener{
	TextView text2,text3;
	Chronometer mChronometer;
	Button mark,stop;
	String[] times=new String[10];
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startgui);
        mChronometer = (Chronometer) findViewById(R.id.chronometer);
        text2=(TextView) findViewById(R.id.textView5);
        text3=(TextView) findViewById(R.id.textView6);
        mark =(Button) findViewById(R.id.button1);
        mark.setOnClickListener(this);
        stop =(Button) findViewById(R.id.button2); 
        stop.setOnClickListener(this);
        int x = 0;

        for (int i=0; i<10;i++)
        	times[i]="";
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();
    }
 

    public void onClick(View a) {
    	String output="";
    	switch (a.getId()) {
    	case R.id.button1:
    		for (int j=9; j>0;j--)
    			times[j]=times[j-1];
    		times[0]=mChronometer.getInstantTime();
    		for (int j=0; j<10;j++)
    			output=output+times[j]+"\n";
    		text2.setText(output);
    		break;
    		
    	case R.id.button2:
    		mChronometer.stop();
    		break;
    	}
    }
}