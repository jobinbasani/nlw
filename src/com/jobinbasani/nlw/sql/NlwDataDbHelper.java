package com.jobinbasani.nlw.sql;

import com.jobinbasani.nlw.sql.NlwDataContract.NlwDataEntry;
import com.jobinbasani.nlw.util.NlwUtil;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NlwDataDbHelper extends SQLiteOpenHelper {
	
	public static final int DATABASE_VERSION = 1;
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
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_ENTRIES);
		try {
			NlwUtil.getInstance(null);
			NlwUtil.loadInitialData();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
