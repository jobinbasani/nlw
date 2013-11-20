package com.jobinbasani.nlw;

import java.util.Calendar;

import com.jobinbasani.nlw.sql.NlwDataContract.NlwDataEntry;
import com.jobinbasani.nlw.sql.NlwDataDbHelper;
import com.jobinbasani.nlw.util.NlwUtil;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	SharedPreferences prefs;
	final public static String COUNTRY_KEY = "country";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		try {
			NlwUtil.getInstance(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		prefs = getPreferences(MODE_PRIVATE);
		setCountrySelectionListener();
		loadPreferences();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void loadPreferences(){
		String defaultCountry = prefs.getString(COUNTRY_KEY, "USA");
		Spinner countrySpinner = (Spinner) findViewById(R.id.countrySelector);
		SpinnerAdapter countryArray = countrySpinner.getAdapter();
		int position = -1;
		for(int i=0;i<countryArray.getCount();i++){
			if(countryArray.getItem(i).toString().equalsIgnoreCase(defaultCountry)){
				position = i;
				break;
			}
		}
		if(position>=0){
			countrySpinner.setSelection(position);
		}
	}
	
	private void setCountrySelectionListener(){
		final Spinner countrySpinner = (Spinner) findViewById(R.id.countrySelector);
		countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				SharedPreferences.Editor editor = prefs.edit();
				editor.putString(COUNTRY_KEY, countrySpinner.getSelectedItem().toString());
				editor.commit();
				loadNextLongWeekend();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
	}
	
	private void loadNextLongWeekend(){
		Calendar rightNow = Calendar.getInstance();
		
		TextView monthYearText = (TextView) findViewById(R.id.monthYearText);
		TextView holidayText = (TextView) findViewById(R.id.nlwHolidayText);
		TextView nlwDateText = (TextView) findViewById(R.id.nlwDateText);
		int currentDateNumber = getCurrentDateNumber();
		monthYearText.setText("December 2013");
		
		nlwDateText.setText(rightNow.get(Calendar.MONTH)+"");
		
		NlwDataDbHelper nlwDbHelper = new NlwDataDbHelper(this);
		SQLiteDatabase db = nlwDbHelper.getReadableDatabase();
		
		String[] projection = {
			    NlwDataEntry._ID,
			    NlwDataEntry.COLUMN_NAME_NLWNAME,
			    NlwDataEntry.COLUMN_NAME_NLWTEXT
			    };
		
		Cursor cursor = db.query(NlwDataEntry.TABLE_NAME, projection, null, null, null, null, null);
		
		//Cursor cursor = db.rawQuery("SELECT * FROM "+NlwDataEntry.TABLE_NAME+" WHERE _ID>? ORDER BY _ID LIMIT 1", new String[]{currentDateNumber+""});
		cursor.moveToFirst();
		String holiday = cursor.getString(cursor.getColumnIndexOrThrow(NlwDataEntry.COLUMN_NAME_NLWNAME));
		holidayText.setText(holiday);
		
		db.close();
	}
	
	private int getCurrentDateNumber(){
		Calendar rightNow = Calendar.getInstance();
		int year, month, day;
		
		year = Integer.parseInt((rightNow.get(Calendar.YEAR)+"").substring(2, 4))*10000;
		month = (rightNow.get(Calendar.MONTH)+1)*100;
		day = rightNow.get(Calendar.DATE);
		return year+month+day;
	}

}
