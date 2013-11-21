package com.jobinbasani.nlw.sql;

import com.jobinbasani.nlw.sql.NlwDataContract.NlwDataEntry;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class NlwDataDbHelper extends SQLiteOpenHelper {
	
	public static final int DATABASE_VERSION = 6;
    public static final String DATABASE_NAME = "NlwData.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
        "CREATE TABLE " + NlwDataEntry.TABLE_NAME + " (" +
        		NlwDataEntry._ID + " INTEGER PRIMARY KEY," +
        		NlwDataEntry.COLUMN_NAME_NLWCOUNTRY + TEXT_TYPE + COMMA_SEP +
        		NlwDataEntry.COLUMN_NAME_NLWNAME + TEXT_TYPE + COMMA_SEP +
        		NlwDataEntry.COLUMN_NAME_NLWWIKI + TEXT_TYPE + COMMA_SEP +
        		NlwDataEntry.COLUMN_NAME_NLWTEXT + TEXT_TYPE + 
        " )";
    private static final String SQL_DELETE_ENTRIES =
    	    "DROP TABLE IF EXISTS " + NlwDataEntry.TABLE_NAME;

	public NlwDataDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		Log.d("here", "in consyr");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d("create", "starting");
		db.execSQL(SQL_CREATE_ENTRIES);
		Log.d("Start", "In create");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DELETE_ENTRIES);
		onCreate(db);
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}

}
