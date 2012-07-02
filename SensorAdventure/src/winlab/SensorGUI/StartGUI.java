package winlab.SensorGUI;

import winlab.sensoradventure.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class StartGUI extends Activity implements OnClickListener{
	TextView text1,text2,text3;
	Button mark,stop;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	String str1="";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startgui);
        text1=(TextView) findViewById(R.id.textView1);
        text2=(TextView) findViewById(R.id.textView5);
        text3=(TextView) findViewById(R.id.textView6);
        mark =(Button) findViewById(R.id.button1);
        stop =(Button) findViewById(R.id.button2); 
        
        str1="Running Time: ";
        text1.setText(str1);
  
        
    }
    
    public void onClick(View a) {
    	switch (a.getId()) {
    	case R.id.button1:
    		
    		break;
    		
    	case R.id.button2:
    		
    		break;
    	}
    }
}

