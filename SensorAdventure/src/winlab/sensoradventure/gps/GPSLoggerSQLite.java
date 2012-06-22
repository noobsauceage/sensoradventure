/**
 * 
 */
package winlab.sensoradventure.gps;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

/**
 * This is the GPSloggerSQLite where we specify the columns and create , insert
 * table into Database The Database name is SensorDatabase and DATABASE_TABLE
 * name is gps
 */
public class GPSLoggerSQLite {

	public static final String KEY_ROWID = "id";
	public static final String KEY_TIME = "timestamp";
	public static final String KEY_LAT = "LAT";
	public static final String KEY_LONG = "LONG";
	public static final String KEY_ALT = "ALT";
	public static final String KEY_BEARING = "BEARING";
	public static final String KEY_ACCURACY = "ACCURACY";
	public static final String KEY_PROVIDER = "PROVIDER";
	public static final String KEY_SAMPLE = "sample";
	private static final String TAG = "SQLtable";
	private static final String DATABASE_NAME = "SensorDatabase";
	private static final String DATABASE_TABLE = "gps";
	private static final int DATABASE_VERSION = 1;

	private final Context context;

	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;

	public GPSLoggerSQLite(Context ctx) {
		this.context = ctx;
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("create table " + DATABASE_TABLE + " (" + KEY_ROWID
					+ " text not null, " + KEY_TIME + " text not null, "
					+ KEY_LAT + " text not null, " + KEY_LONG
					+ " text not null, " + KEY_ALT + " text not null, "
					+ KEY_BEARING + " text not null, " + KEY_ACCURACY
					+ " text not null, " + KEY_PROVIDER + " text not null);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS titles");
			onCreate(db);
		}
	}

	// opens the database
	public GPSLoggerSQLite open() throws SQLException {
		DBHelper = new DatabaseHelper(context);
		db = DBHelper.getWritableDatabase();
		return this;
	}

	// ---closes the database---
	public void close() {
		DBHelper.close();
	}

	public void deleteTable() {
		db.delete(DATABASE_TABLE, null, null);
	}

	// ---insert a gps row into the database---
	public long insertgpsrow(long time, String device_id, double x, double y,
			double z, double bearing, double accuracy, String provider, int i) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_ROWID, device_id);
		initialValues.put(KEY_TIME, time);
		initialValues.put(KEY_LAT, x);
		initialValues.put(KEY_LONG, y);
		initialValues.put(KEY_ALT, z);
		initialValues.put(KEY_BEARING, bearing);
		initialValues.put(KEY_ACCURACY, accuracy);
		initialValues.put(KEY_PROVIDER, provider);
		return db.insert(DATABASE_TABLE, null, initialValues);
	}

	// ---retrieves all gps rows this is basically while testing---
	public Cursor getAllgpsdata(int i) {
		return db.query(DATABASE_TABLE, new String[] { KEY_ROWID, KEY_TIME,
				KEY_LAT, KEY_LONG, KEY_ALT, KEY_BEARING, KEY_ACCURACY,
				KEY_PROVIDER }, null, null, null, null, null);
	}

	public void copy() {
		try {
			File sd = Environment.getExternalStorageDirectory();
			File data = Environment.getDataDirectory();

			if (sd.canWrite()) {
				String currentDBPath = "//data//" + "winlab.CR"
						+ "//databases//" + "SensorDatabase2";
				String backupDBPath = "/temp/SensorDatabase2";
				File currentDB = new File(data, currentDBPath);
				File backupDB = new File(sd, backupDBPath);

				FileChannel src = new FileInputStream(currentDB).getChannel();
				FileChannel dst = new FileOutputStream(backupDB).getChannel();
				dst.transferFrom(src, 0, src.size());
				src.close();
				dst.close();
				Message msg = handler.obtainMessage();
				msg.arg1 = 1;
				handler.sendMessage(msg);

			}
		} catch (Exception e) {

			Message msg = handler.obtainMessage();
			msg.arg1 = 2;
			handler.sendMessage(msg);

		}
	}

	private final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.arg1 == 1)
				Toast.makeText(context, "/temp/SensorDatabase",
						Toast.LENGTH_LONG).show();
			if (msg.arg1 == 2)
				Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show();
		}
	};

}
