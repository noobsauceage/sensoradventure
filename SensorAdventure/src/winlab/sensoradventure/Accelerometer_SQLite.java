package winlab.sensoradventure;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class Accelerometer_SQLite {
	public static final String KEY_ROWID = "_id";
    public static final String KEY_TIME = "timestamp";
    public static final String KEY_X = "x";
    public static final String KEY_Y = "y";
    public static final String KEY_Z = "z";
    private static final String TAG = "SQLtable";
 
    private static final String DATABASE_NAME = "Accelerometer";
    private static final String DATABASE_TABLE = "AccelerometerTable";
    private static final int DATABASE_VERSION = 1;
 
    private static final String DATABASE_CREATE =
        "create table " + DATABASE_TABLE + " (" + KEY_ROWID + " integer primary key autoincrement, "
        + KEY_TIME +" text not null, " + KEY_X + " text not null, " + KEY_Y + " text not null, " 
        + KEY_Z +" text not null);";
 
    private final Context context; 
    
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;
 
    public Accelerometer_SQLite(Context ctx) 
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
            db.execSQL(DATABASE_CREATE);
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
    public Accelerometer_SQLite open() throws SQLException 
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
    	db.delete(DATABASE_TABLE, null, null);
    }
   
    //---insert a title into the database---
    public long insertTitle(String time, String x, String y, String z) 
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TIME, time);
        initialValues.put(KEY_X, x);
        initialValues.put(KEY_Y, y);
        initialValues.put(KEY_Z, z);
        return db.insert(DATABASE_TABLE, null, initialValues);
    }
 
    //---deletes a particular title---
    public boolean deleteTitle(long rowId) 
    {
        return db.delete(DATABASE_TABLE, KEY_ROWID + 
        		"=" + rowId, null) > 0;
    }
 
    //---retrieves all the titles---
    public Cursor getAllTitles() 
    {
        return db.query(DATABASE_TABLE, new String[] {
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
 
    //---retrieves a particular title---
    public Cursor getTitle(long rowId) throws SQLException 
    {
        Cursor mCursor =
                db.query(true, DATABASE_TABLE, new String[] {
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
    String x, String y, String z) 
    {
        ContentValues args = new ContentValues();
        args.put(KEY_TIME, time);
        args.put(KEY_X, x);
        args.put(KEY_Y, y);
        args.put(KEY_Z, z);
        return db.update(DATABASE_TABLE, args, 
                         KEY_ROWID + "=" + rowId, null) > 0;
    }
}
