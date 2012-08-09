package winlab.sensoradventure.gps;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * This is the GPSLoggerSQLite where we specify the columns and create , insert
 * table into Database The Database name is SensorDatabase and DATABASE_TABLE
 * name is 
 */
public class GPSLoggerSQLite {

	public static final String KEY_ROWID     = "ID";
	public static final String KEY_TIME      = "TIMESTAMP";
	public static final String KEY_LAT       = "LAT";
	public static final String KEY_LONG      = "LONG";
	public static final String KEY_ALT       = "ALT";
	public static final String KEY_BEARING   = "BEARING";
	public static final String KEY_ACCURACY  = "ACCURACY";
	public static final String KEY_PROVIDER  = "PROVIDER";
	public static final String KEY_SPEED     = "SPEED";
	private static final String TAG = "SQLtable";
	private static String DATABASE_NAME = null;
	private static String DATABASE_TABLE;
	private static final int DATABASE_VERSION = 1;
	private final Context context;

	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;

	public GPSLoggerSQLite(Context ctx,String Dataabse_name,String Database_table) {
		DATABASE_NAME =  Dataabse_name;
		DATABASE_TABLE = Database_table;
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
					+ " text not null, " + KEY_PROVIDER
					+ " text not null, "+ KEY_SPEED + " text not null);");
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
			double z, double bearing, double accuracy, String provider, double  speed) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_ROWID, device_id);
		initialValues.put(KEY_TIME, time);
		initialValues.put(KEY_LAT, x);
		initialValues.put(KEY_LONG, y);
		initialValues.put(KEY_ALT, z);
		initialValues.put(KEY_BEARING, bearing);
		initialValues.put(KEY_ACCURACY, accuracy);
		initialValues.put(KEY_PROVIDER, provider);
		initialValues.put(KEY_SPEED, speed);
		return db.insert(DATABASE_TABLE, null, initialValues);
	}

	// ---retrieves all gps rows this is basically while testing---
	public Cursor getAllgpsdata(int i) {
		return db.query(DATABASE_TABLE, new String[] { KEY_ROWID, KEY_TIME,
				KEY_LAT, KEY_LONG, KEY_ALT, KEY_BEARING, KEY_ACCURACY,
				KEY_PROVIDER,KEY_SPEED }, null, null, null, null, null);
	}

	 
 
 

}
