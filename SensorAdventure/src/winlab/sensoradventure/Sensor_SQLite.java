package winlab.sensoradventure;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class Sensor_SQLite {
	public static final String KEY_ROWID = "_id";
	public static final String KEY_TIME = "timestamp";
	public static final String KEY_X = "x";
	public static final String KEY_Y = "y";
	public static final String KEY_Z = "z";
	public static final String KEY_SAMPLE = "sample";
	private static final String TAG = "SQLtable";

	private static final String DATABASE_NAME = "SensorDatabase";
	private static final String DATABASE_TABLE[]  = {"AccelerometerTable","LinearAccelerometerTable",
		"GyroscopeTable","MagneticTable","MicrophoneTable"};
	private static final int DATABASE_VERSION = 1;


	private final Context context;

	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;

	public Sensor_SQLite(Context ctx) 
	{
		this.context = ctx;

	}

	private static class DatabaseHelper extends SQLiteOpenHelper 
	{
		DatabaseHelper(Context context) 
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) 
		{
			for(int i = 0; i<DATABASE_TABLE.length-1;i++)
			db.execSQL("create table " + DATABASE_TABLE[i] + " (" + KEY_ROWID + " integer primary key autoincrement, "
					+ KEY_TIME +" text not null, " + KEY_X + " text not null, " + KEY_Y + " text not null, " 
					+ KEY_Z +" text not null);");
			db.execSQL("create table " + DATABASE_TABLE[DATABASE_TABLE.length-1] + " (" + KEY_ROWID + " integer primary key autoincrement, "
					+ KEY_SAMPLE + " text not null);");
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, 
				int newVersion) 
		{
			Log.w(TAG, "Upgrading database from version " + oldVersion 
					+ " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS titles");
			onCreate(db);
		}
	}    
	//opens the database
	public Sensor_SQLite open() throws SQLException 
	{
		DBHelper = new DatabaseHelper(context);
		db = DBHelper.getWritableDatabase();
		return this;
	}

	//---closes the database---    
	public void close() 
	{
		DBHelper.close();
	}

	public void deleteTable() {
		for(int i = 0; i< DATABASE_TABLE.length;i++)
		db.delete(DATABASE_TABLE[i], null, null);
	}

	//---insert a title into the database---
	public long insertTitle(String time, String x, String y, String z,int i) 
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_TIME, time);
		initialValues.put(KEY_X, x);
		initialValues.put(KEY_Y, y);
		initialValues.put(KEY_Z, z);
		return db.insert(DATABASE_TABLE[i], null, initialValues);
	}
	
	public long insertMic(String sample){
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_SAMPLE, sample);
		return db.insert(DATABASE_TABLE[DATABASE_TABLE.length-1], null, initialValues);
	}

	//---deletes a particular title---
	public boolean deleteTitle(long rowId, int i) 
	{
		return db.delete(DATABASE_TABLE[i], KEY_ROWID + 
				"=" + rowId, null) > 0;
	}

	//---retrieves all the titles---
	public Cursor getAllTitles(int i) 
	{
		return db.query(DATABASE_TABLE[i], new String[] {
				KEY_ROWID, 
				KEY_TIME,
				KEY_X,
				KEY_Y,
				KEY_Z}, 
				null, 
				null, 
				null, 
				null, 
				null);
	}
	
	public Cursor getAllTitlesMic() 
	{
		return db.query(DATABASE_TABLE[DATABASE_TABLE.length-1], new String[] {
				KEY_ROWID, 
				KEY_SAMPLE}, 
				null, 
				null, 
				null, 
				null, 
				null);
	}
	
	

	//---retrieves a particular title---
	public Cursor getTitle(long rowId, int i) throws SQLException 
	{
		Cursor mCursor =
				db.query(true, DATABASE_TABLE[i], new String[] {
						KEY_ROWID,
						KEY_TIME, 
						KEY_X,
						KEY_Y,
						KEY_Z
				}, 
				KEY_ROWID + "=" + rowId, 
				null,
				null, 
				null, 
				null, 
				null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	//---updates a title---
	public boolean updateTitle(long rowId, String time, 
			String x, String y, String z, int i) 
	{
		ContentValues args = new ContentValues();
		args.put(KEY_TIME, time);
		args.put(KEY_X, x);
		args.put(KEY_Y, y);
		args.put(KEY_Z, z);
		return db.update(DATABASE_TABLE[i], args, 
				KEY_ROWID + "=" + rowId, null) > 0;
	}
	
	
}
