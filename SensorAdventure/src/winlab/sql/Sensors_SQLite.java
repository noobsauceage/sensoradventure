package winlab.sql;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

import winlab.sensoradventure.SensorAdventureActivity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils.InsertHelper;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;


public class Sensors_SQLite {
	public static final String KEY_ROWID = "_id";
	public static final String KEY_TIME = "timestamp";
	public static final String KEY_X = "x";
	public static final String KEY_Y = "y";
	public static final String KEY_Z = "z";
	public static final String KEY_SAMPLE = "sample";
	private static final String TAG = "SQLtable";

	private static final String DATABASE_NAME = "SensorDatabase.db";
	private static final String DATABASE_TABLE[] = { "AccelerometerTable",
			"MagneticTable", "OrientationTable", "GyroscopeTable",
			"LightTable", "PressureTable", "TemperatureTable",
			"ProximityTable", "GravityTable", "LinearAccelerometerTable",
			"RotationVectorTable", "RelativeHumidityTable",
			"AmbientTemperatureTable", "MicrophoneTable" };

	private static final int DATABASE_VERSION = 1;

	private final Context context;

	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;

	private InsertHelper[] ihs = new InsertHelper[DATABASE_TABLE.length];

	public Sensors_SQLite(Context ctx) {
		this.context = ctx;

	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			for (int i = 0; i < DATABASE_TABLE.length - 1; i++)
             if ((i<13)&&(Sensors_SQLite_Setting.sensors[i]))
				switch (i + 1) {
				case 1:
				case 2:
				case 3:
				case 4:
				case 9:
				case 10:

					db.execSQL("create table " + DATABASE_TABLE[i] + " ("
							+ KEY_ROWID
							+ " integer primary key autoincrement, " + KEY_TIME
							+ " text not null, " + KEY_X + " text not null, "
							+ KEY_Y + " text not null, " + KEY_Z
							+ " text not null);");
					break;
				case 5:
				case 6:
				case 7:
				case 8:
				case 12:
				case 13:
					db.execSQL("create table " + DATABASE_TABLE[i] + " ("
							+ KEY_ROWID
							+ " integer primary key autoincrement, " + KEY_TIME
							+ " text not null, " + "Value" + " text not null);");
					break;
				case 11:
					db.execSQL("create table " + DATABASE_TABLE[i] + " ("
							+ KEY_ROWID
							+ " integer primary key autoincrement, " + KEY_TIME
							+ " text not null, " + KEY_X + " text not null, "
							+ KEY_Y + " text not null, " + KEY_Z
							+ " text not null, " + "Scalar"
							+ " text not null);");
					break;

				}
			/*db.execSQL("create table "
					+ DATABASE_TABLE[DATABASE_TABLE.length - 1] + " ("
					+ KEY_ROWID + " integer primary key autoincrement, "
					+ KEY_SAMPLE + " blob not null);");*/
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
	public Sensors_SQLite open() throws SQLException {
		DBHelper = new DatabaseHelper(context);
		db = DBHelper.getWritableDatabase();
		return this;
	}

	// ---closes the database---
	public void close() {
		db.close();
	}

	public void deleteTable() {
		for (int i = 0; i < DATABASE_TABLE.length; i++)
			 if ((i<13)&&(Sensors_SQLite_Setting.sensors[i]))
			db.delete(DATABASE_TABLE[i], null, null);
		//db.execSQL("VACUUM");
	}

	public void prepareTransaction(/*int i*/) {
		db.beginTransaction();
//		ihs[i] = new InsertHelper(db, DATABASE_TABLE[i]);
//		ihs[i].prepareForInsert();

	}

	public void endTransaction(/*int i*/) {
//		ihs[i].execute();
//		ihs[i].close();
		db.setTransactionSuccessful();
		db.endTransaction();
	}
	
	public void prepareHelper(){
		
	}

	// ---insert a title into the database---
	public long insertTitle1(String time, String x, String y, String z, int i) {
/*
		ihs[i].bind(ihs[i].getColumnIndex(KEY_TIME), time);
		ihs[i].bind(ihs[i].getColumnIndex(KEY_X), x);
		ihs[i].bind(ihs[i].getColumnIndex(KEY_Y), y);
		ihs[i].bind(ihs[i].getColumnIndex(KEY_Z), z);*/
		
		  ContentValues initialValues = new ContentValues();
		  initialValues.put(KEY_TIME, time); initialValues.put(KEY_X, x);
		  initialValues.put(KEY_Y, y); initialValues.put(KEY_Z, z); 
		  return
		  db.insert(DATABASE_TABLE[i], null, initialValues);
		 
	}

	public long insertTitle2(String time, String value, int i) {
//		ihs[i].bind(ihs[i].getColumnIndex(KEY_TIME), time);
//		ihs[i].bind(ihs[i].getColumnIndex("Value"),value);
		
		
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_TIME, time);
		initialValues.put("Value", value);
		return db.insert(DATABASE_TABLE[i], null, initialValues);
	}

	public long insertTitle3(String time, String x, String y, String z,
			String scalar, int i) {
		/*ihs[i].bind(ihs[i].getColumnIndex(KEY_TIME),time);
		ihs[i].bind(ihs[i].getColumnIndex(KEY_X), x);
		ihs[i].bind(ihs[i].getColumnIndex(KEY_Y), y);
		ihs[i].bind(ihs[i].getColumnIndex(KEY_Z), z);
		ihs[i].bind(ihs[i].getColumnIndex("Scalar"),scalar);*/
		
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_TIME, time);
		initialValues.put(KEY_X, x);
		initialValues.put(KEY_Y, y);
		initialValues.put(KEY_Z, z);
		initialValues.put("Scalar", scalar);
		return db.insert(DATABASE_TABLE[i], null, initialValues);
	}

	public long insertMic(byte[] sample, int i) {
	//	ihs[i].bind(ihs[i].getColumnIndex(KEY_SAMPLE), sample);

		
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_SAMPLE, sample);
		return db.insert(DATABASE_TABLE[DATABASE_TABLE.length - 1], null,
				initialValues);
	}

	// ---deletes a particular title---
	public boolean deleteTitle(long rowId, int i) {
		return db.delete(DATABASE_TABLE[i], KEY_ROWID + "=" + rowId, null) > 0;
	}

	// ---retrieves all the titles---

	public Cursor getAllTitles1(int i) {
		return db.query(DATABASE_TABLE[i], new String[] { KEY_ROWID, KEY_TIME,
				KEY_X, KEY_Y, KEY_Z }, null, null, null, null, null);
	}

	public Cursor getAllTitles2(int i) {
		return db.query(DATABASE_TABLE[i], new String[] { KEY_ROWID, KEY_TIME,
				"Value" }, null, null, null, null, null);
	}

	public Cursor getAllTitles3(int i) {
		return db.query(DATABASE_TABLE[i], new String[] { KEY_ROWID, KEY_TIME,
				KEY_X, KEY_Y, KEY_Z, "Scalar" }, null, null, null, null, null);
	}

	public Cursor getAllTitlesMic() {
		return db.query(DATABASE_TABLE[DATABASE_TABLE.length - 1],
				new String[] { KEY_ROWID, KEY_SAMPLE }, null, null, null, null,
				null);
	}

	// ---retrieves a particular title---
	public Cursor getTitle1(long rowId, int i) throws SQLException {
		Cursor mCursor = db.query(true, DATABASE_TABLE[i], new String[] {
				KEY_ROWID, KEY_TIME, KEY_X, KEY_Y, KEY_Z }, KEY_ROWID + "="
				+ rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public Cursor getTitle2(long rowId, int i) throws SQLException {
		Cursor mCursor = db.query(true, DATABASE_TABLE[i], new String[] {
				KEY_ROWID, KEY_TIME, "Value" }, KEY_ROWID + "=" + rowId, null,
				null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public Cursor getTitle3(long rowId, int i) throws SQLException {
		Cursor mCursor = db.query(true, DATABASE_TABLE[i], new String[] {
				KEY_ROWID, KEY_TIME, KEY_X, KEY_Y, KEY_Z, "Scalar" }, KEY_ROWID
				+ "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	// ---updates a title---
	public boolean updateTitle1(long rowId, String time, String x, String y,
			String z, int i) {
		ContentValues args = new ContentValues();
		args.put(KEY_TIME, time);
		args.put(KEY_X, x);
		args.put(KEY_Y, y);
		args.put(KEY_Z, z);
		return db
				.update(DATABASE_TABLE[i], args, KEY_ROWID + "=" + rowId, null) > 0;
	}

	public boolean updateTitle2(long rowId, String time, String value, int i) {
		ContentValues args = new ContentValues();
		args.put(KEY_TIME, time);
		args.put("Value", value);
		return db
				.update(DATABASE_TABLE[i], args, KEY_ROWID + "=" + rowId, null) > 0;
	}

	public boolean updateTitle3(long rowId, String time, String x, String y,
			String z, String scalar, int i) {
		ContentValues args = new ContentValues();
		args.put(KEY_TIME, time);
		args.put(KEY_X, x);
		args.put(KEY_Y, y);
		args.put(KEY_Z, z);
		args.put("Scalar", scalar);
		return db
				.update(DATABASE_TABLE[i], args, KEY_ROWID + "=" + rowId, null) > 0;
	}

	public void copy(){
			try {
				File sd = SensorAdventureActivity.DataPath;
				File data = Environment.getDataDirectory();
				
				if (sd.exists()==false) sd.mkdirs();
				String currentDBPath ="//data//" + "winlab.sensoradventure" + "//databases//" + "SensorDatabase.db";
				String backupDBPath = "SensorDatabase.db";
				File currentDB= new File(data, currentDBPath);
					
				File backupDB = new File(sd, backupDBPath);

				FileChannel src = new FileInputStream(currentDB).getChannel();
				FileChannel dst = new FileOutputStream(backupDB).getChannel();
				dst.transferFrom(src, 0, src.size());
				src.close();
				dst.close();
				Message msg = handler.obtainMessage();
				msg.arg1 = 1;
				handler.sendMessage(msg);

				
			} catch (Exception e) {

				Message msg = handler.obtainMessage();
				msg.arg1 = 2;
				handler.sendMessage(msg);

			}
		}

	private final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.arg1 == 1)
				Toast.makeText(context, "see SQLite file at: "
			            +SensorAdventureActivity.DataPath.toString()+"SensorDatabase.db",
						Toast.LENGTH_LONG).show();
			if (msg.arg1 == 2)
				Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show();
		}
	};

}
