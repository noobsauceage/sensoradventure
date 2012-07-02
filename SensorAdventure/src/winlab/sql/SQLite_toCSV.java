package winlab.sql;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

//import com.sqlitetest.Comment;
//import com.sqlitetest.CommentsDataSource;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;


/*
 * This is a stupid workaround for uploading SQLite3 to MYSQL
 * Basically, it just converts SQLite3 to a csv file
 * and then the SendAll.java will still just upload
 * these csv files as usual.
 * 
 */
public class SQLite_toCSV extends Activity {

	public List<PrintWriter> captureFiles = new ArrayList<PrintWriter>();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		TextView tv = new TextView(this);
		tv.setText("I hate sqlite\n");
		setContentView(tv);

		Sensors_SQLite datasource = new Sensors_SQLite(this);
		datasource.open();

		int sensorType;
		String row;
		for (int i = 0; i < 13; i++) {// ugh
			sensorType = i + 1;
			// Create seperate files for each available sensor
			try {
				// should actually only be a new file after upload

				// Make the dir if needed
				File dataDir = new File("/sdcard/sensorData/");
				// have the object build the directory structure, if needed.
				dataDir.mkdirs();
				// Make the file
				File myFile = new File(dataDir, sensorType + "_" + "sqlite" + ".csv");

				myFile.createNewFile();
				captureFiles.add(new PrintWriter(new FileWriter(myFile, false)));

				// csv field names
				captureFiles.get(i).println("`timestamp`" + "," + getFields(sensorType));

			} catch (Exception e) {
				// Log.e( LOG_TAG, ex.getMessage(), e );
				// Toast.makeText(getBaseContext(),
				// e.getMessage(),Toast.LENGTH_SHORT).show();
			}

			if (Sensors_SQLite_Setting.sensors[i]) {
				String result1 = null;
				Cursor c1;
				String result2 = null;
				Cursor c2;
				String result3 = null;
				Cursor c3;
				switch (sensorType) {
				case 1:
				case 2:
				case 3:
				case 4:
				case 9:
				case 10:
					// result1="timestamp (ms)            x            y            z \n";
					// result1="`timestamp`,`x`,`y`,`z`\n";
					c1 = datasource.getAllTitles1(i);
					if (c1.moveToFirst()) {
						int count1 = 0;
						do {
							count1++;
							row = c1.getString(1) + ","
									+ c1.getString(2) + "," + c1.getString(3)
									+ "," + c1.getString(4);
							captureFiles.get(i).println(row);
							result1 += row;
						} while ((c1.moveToNext()) && (count1 < 10));
					}
					Toast.makeText(this, result1, Toast.LENGTH_SHORT).show();
					break;
				case 5:
				case 6:
				case 7:
				case 8:
				case 12:
				case 13:
					// result2="timestamp (ms)            Value\n";
					c2 = datasource.getAllTitles2(i);
					if (c2.moveToFirst()) {
						int count2 = 0;
						do {
							count2++;
							captureFiles.get(i).println(c2.getString(1) + ","
									+ c2.getString(2));
						} while ((c2.moveToNext()) && (count2 < 10));
					}

					//Toast.makeText(this, result2, Toast.LENGTH_SHORT).show();
					break;
				case 11:
					// result3="timestamp (ms)            x            y           z      Scalar\n";
					c3 = datasource.getAllTitles3(i);
					if (c3.moveToFirst()) {
						int count3 = 0;
						do {
							count3++;
							captureFiles.get(i).println(c3.getString(1) + ","
									+ c3.getString(2) + "," + c3.getString(3)
									+ "," + c3.getString(4) + "            "
									+ c3.getString(5));
						} while ((c3.moveToNext()) && (count3 < 10));
					}

					//Toast.makeText(this, result3, Toast.LENGTH_SHORT).show();
					break;
				}
			}
		}

		for (int i = 0; i < 13; i++)
			Sensors_SQLite_Setting.sensors[i] = true;
		datasource.close();
		Toast.makeText(this, "done", Toast.LENGTH_SHORT).show();
	}

	private String getFields(int type) {
		String fields = null;

		switch (type) {
		case 1:
		case 2:
		case 3:
		case 9:
		case 10:
		case 11:
			fields = "`x`" + "," + "`y`" + "," + "`z`";
			break;
		default:
			fields = "`value`" + "," + "`null0`" + "," + "`null1`";	//stupid
		}
		return fields;

	}
	
	protected void onStop() {
        super.onStop();
        for( int i = 0 ; i < 13 ; ++i ) {
        	captureFiles.get(i).close();
        }
    }
}