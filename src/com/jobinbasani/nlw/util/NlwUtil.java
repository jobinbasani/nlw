package com.jobinbasani.nlw.util;

import com.jobinbasani.nlw.R;
import com.jobinbasani.nlw.sql.NlwDataContract.NlwDataEntry;
import com.jobinbasani.nlw.sql.NlwDataDbHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

public class NlwUtil {
	
	private static NlwUtil nlwUtil;
	private static Context nlwContext;
	private static int DATAFIELD_COUNT = 5;
	
	private NlwUtil(Context context){
		nlwContext = context;
	}
	public static NlwUtil getInstance(Context context) throws Exception{
		if(context == null && nlwContext == null){
			throw new Exception("Set Context from an activity first!");
		}
		if(nlwUtil == null){
			nlwUtil = new NlwUtil(context);
		}
		return nlwUtil;
	}
	
	public static void loadInitialData(){
		Toast.makeText(nlwContext, "Updating database...", Toast.LENGTH_SHORT).show();
		NlwDataDbHelper nlwDbHelper = new NlwDataDbHelper(nlwContext);
		SQLiteDatabase db = nlwDbHelper.getWritableDatabase();
		String[] nlwData = nlwContext.getResources().getStringArray(R.array.nlwData);
		
		for(int i=0;i<nlwData.length;i++){
			String[] nlwDetails = nlwData[i].split("^");
			if(nlwDetails.length == DATAFIELD_COUNT){
				ContentValues values = new ContentValues();
				values.put(NlwDataEntry._ID, nlwDetails[0]);
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

}
