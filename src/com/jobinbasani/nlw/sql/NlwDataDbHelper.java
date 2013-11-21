package com.jobinbasani.nlw.sql;

import com.jobinbasani.nlw.MainActivity;
import com.jobinbasani.nlw.R;
import com.jobinbasani.nlw.sql.NlwDataContract.NlwDataEntry;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NlwDataDbHelper extends SQLiteOpenHelper {
	
	public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "NlwData.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
        "CREATE TABLE " + NlwDataEntry.TABLE_NAME + " (" +
        		NlwDataEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
        		NlwDataEntry.COLUMN_NAME_NLWDATE + INT_TYPE + COMMA_SEP +
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
		
		String[] nlwData = MainActivity.NLW_CONTEXT.getResources().getStringArray(R.array.nlwData);
		for(int i=0;i<nlwData.length;i++){
			String[] nlwDetails = nlwData[i].split("~");
			if(nlwDetails.length == 5){
				ContentValues values = new ContentValues();
				values.put(NlwDataEntry.COLUMN_NAME_NLWDATE, nlwDetails[0]);
				values.put(NlwDataEntry.COLUMN_NAME_NLWCOUNTRY, nlwDetails[1]);
				values.put(NlwDataEntry.COLUMN_NAME_NLWNAME, nlwDetails[2]);
				values.put(NlwDataEntry.COLUMN_NAME_NLWWIKI, nlwDetails[3]);
				values.put(NlwDataEntry.COLUMN_NAME_NLWTEXT, nlwDetails[4]);

				db.insert(
						NlwDataEntry.TABLE_NAME,
				         null,
				         values);
			}
			
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
