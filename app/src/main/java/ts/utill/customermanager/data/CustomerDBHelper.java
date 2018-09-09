package ts.utill.customermanager.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CustomerDBHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "CustomerManager.db";
	public static final int DATABASE_VERSION = 2;

	private static CustomerDBHelper sInstance = null;
	private Context _context;

	public static CustomerDBHelper getInstance(Context context) {
		Log.d("TestG", "CustomerDBHelper.getInstance()");
		 if (sInstance == null) {
			 sInstance = new CustomerDBHelper(context.getApplicationContext());
		 }
		 return sInstance;
	}

	private CustomerDBHelper(Context context) {
		 super(context, DATABASE_NAME, null, DATABASE_VERSION);
		 Log.d("TestG", "CustomerDBHelper()");
		 _context = context;
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS customer(idx_customer INTEGER PRIMARY KEY, name TEXT, hp VARCHAR(13), sex INTEGER, age INTEGER, memo TEXT, point TEXT);"); //, point TEXT
		db.execSQL("CREATE TABLE IF NOT EXISTS items(idx_item INTEGER PRIMARY KEY, idx_group INTEGER, name TEXT, price TEXT, unvisible INTEGER);"); //idx_group INTEGER,
		db.execSQL("CREATE TABLE IF NOT EXISTS itemgroups(idx_group INTEGER PRIMARY KEY, name TEXT);");
		db.execSQL("CREATE TABLE IF NOT EXISTS sales(idx_sale INTEGER PRIMARY KEY, idx_customer INTEGER, date DATETIME, salelist TEXT, memo TEXT);");
		db.execSQL("CREATE TABLE IF NOT EXISTS schedule(idx_schedule INTEGER PRIMARY KEY, idx_customer INTEGER, date DATETIME, memo TEXT);");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		Log.d("TestG", "onUpgrade");
		
		/*
		if(true){ // oldVersion == 1
            try {
                db.beginTransaction();
                db.execSQL("ALTER TABLE customer ADD COLUMN point Text DEFAULT '0'");
                db.execSQL("ALTER TABLE items ADD COLUMN idx_group INTEGER DEFAULT 0");
                db.setTransactionSuccessful();
            } catch (IllegalStateException e) {
                Log.d("TestG" , e + "");
            } finally {
                db.endTransaction();
            };
		}
		
		
		
		db.execSQL("DROP TABLE IF EXISTS customer");
		db.execSQL("DROP TABLE IF EXISTS items");
		db.execSQL("DROP TABLE IF EXISTS sales");
		
		if(oldVersion > 1){
			db.execSQL("DROP TABLE IF EXISTS itemgroups");
			db.execSQL("DROP TABLE IF EXISTS schedule");
		}
		
		
		onCreate(db);
		*/
		Log.d("TestG", "version : "+oldVersion + " / " + newVersion);
	}
}
