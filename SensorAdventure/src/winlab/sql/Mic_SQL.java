package winlab.sql;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import winlab.sensoradventure.SensorAdventureActivity;

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

public class Mic_SQL {


	public static final String KEY_ROWID = "_id";
	public static final String KEY_SAMPLE = "sample";
	private static final String TAG = "SQLtable";
	private static final String DATABASE_NAME = "MicDatabase.db";
	private static final String DATABASE_TABLE = "MicrophoneTable";
	
	private static final int DATABASE_VERSION = 1;

	private final Context context;

	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;

	public Mic_SQL(Context ctx) {
		try{
			File data = Environment.getDataDirectory();
			String currentDBPath ="//data//" + "winlab.sensoradventure" + "//databases//" + "MicDatabase.db";
			
			File currentDB= new File(data, currentDBPath);
			
			if(currentDB.exists()) currentDB.delete();
			} catch (Exception e){}
		this.context = ctx;

	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

		db.execSQL("create table "
			+ DATABASE_TABLE + " ("
			+ KEY_ROWID + " integer primary key autoincrement, "
			+ KEY_SAMPLE + " blob not null);");
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
	public Mic_SQL open() throws SQLException {
		DBHelper = new DatabaseHelper(context);
		db = DBHelper.getWritableDatabase();
		return this;
	}

	// ---closes the database---
	public void close() {
		db.close();
	}

	public void deleteTable() {
			db.delete(DATABASE_TABLE, null, null);
	}
	public void prepareTransaction(){
		db.beginTransaction();
		
	}
	public void endTransaction(){
		
	db.setTransactionSuccessful();
	db.endTransaction();
	}

	
	public long insertMic(byte[] sample) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_SAMPLE, sample);
		return db.insert(DATABASE_TABLE, null,
				initialValues);
	}

    
	public Cursor getAllTitlesMic() {
		return db.query(DATABASE_TABLE,
				new String[] { KEY_ROWID, KEY_SAMPLE }, null, null, null, null,
				null);
	}
	public void copy() {
		try {
			File sd = SensorAdventureActivity.DataPath;
			File data = Environment.getDataDirectory();

			if (sd.exists()==false) sd.mkdirs();
			String currentDBPath ="//data//" + "winlab.sensoradventure" + "//databases//" + "MicDatabase.db";
			String backupDBPath = "MicDatabase.db";
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
//			if (msg.arg1 == 1)
//				Toast.makeText(context,"see SQLite file at: "
//			            +SensorAdventureActivity.DataPath.toString()+"/MicDatabase.db",
//						Toast.LENGTH_LONG).show();
			if (msg.arg1 == 2)
				Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show();
		}
	};

}
