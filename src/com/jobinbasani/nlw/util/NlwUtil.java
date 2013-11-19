package com.jobinbasani.nlw.util;

import com.jobinbasani.nlw.R;
import com.jobinbasani.nlw.sql.NlwDataDbHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class NlwUtil {
	
	private static NlwUtil nlwUtil;
	private static Context nlwContext;
	
	private NlwUtil(Context context){
		nlwContext = context;
	}
	public static NlwUtil getInstance(Context context){
		if(nlwUtil == null){
			nlwUtil = new NlwUtil(context);
		}
		return nlwUtil;
	}
	public static NlwUtil getInstance(){
		
		return nlwUtil;
	}
	public static void loadInitialData(){
		NlwDataDbHelper nlwDbHelper = new NlwDataDbHelper(nlwContext);
		SQLiteDatabase db = nlwDbHelper.getWritableDatabase();
		String[] nlwData = nlwContext.getResources().getStringArray(R.array.nlwData);
		
	}

}
