package com.jobinbasani.nlw;

import java.util.Calendar;

import com.jobinbasani.nlw.sql.NlwDataContract.NlwDataEntry;
import com.jobinbasani.nlw.sql.NlwDataDbHelper;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
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
	public static Context NLW_CONTEXT;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		NLW_CONTEXT = this;
		
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
		
		TextView monthYearText = (TextView) findViewById(R.id.monthYearText);
		TextView holidayText = (TextView) findViewById(R.id.nlwHolidayText);
		TextView nlwDateText = (TextView) findViewById(R.id.nlwDateText);
		Spinner countrySelector = (Spinner) findViewById(R.id.countrySelector);
		int currentDateNumber = getCurrentDateNumber();
		String selectedCountry = countrySelector.getSelectedItem().toString();
		String[] selectionArgs = new String[]{currentDateNumber+"", selectedCountry};
		
		NlwDataDbHelper nlwDbHelper = new NlwDataDbHelper(NLW_CONTEXT);
		SQLiteDatabase db = nlwDbHelper.getWritableDatabase();
		
		Cursor cursor = db.rawQuery("SELECT * FROM "+NlwDataEntry.TABLE_NAME+" WHERE "+NlwDataEntry.COLUMN_NAME_NLWDATE+">? AND "+NlwDataEntry.COLUMN_NAME_NLWCOUNTRY+"=? ORDER BY _ID LIMIT 1", selectionArgs);
		cursor.moveToFirst();
		if(cursor.getCount()>0){
			int dateNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(NlwDataEntry.COLUMN_NAME_NLWDATE)));
			String holiday = cursor.getString(cursor.getColumnIndexOrThrow(NlwDataEntry.COLUMN_NAME_NLWNAME));
			int year = dateNumber/10000;
			int month = (dateNumber-(year*10000))/100;
			int date = dateNumber-(year*10000)-(month*100);
			String monthName = getMonthName(month);
			year = 2000+year;
			
			monthYearText.setText(monthName+" "+year);
			nlwDateText.setText(date+"");
			holidayText.setText(holiday);
			
		}
		cursor.close();
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
	
	private String getMonthName(int month){
		String monthName = "";
		switch(month){
		case 1:
			monthName = "January";
			break;
		case 2:
			monthName = "February";
			break;
		case 3:
			monthName = "March";
			break;
		case 4:
			monthName = "April";
			break;
		case 5:
			monthName = "May";
			break;
		case 6:
			monthName = "June";
			break;
		case 7:
			monthName = "July";
			break;
		case 8:
			monthName = "August";
			break;
		case 9:
			monthName = "September";
			break;
		case 10:
			monthName = "October";
			break;
		case 11:
			monthName = "November";
			break;
		case 12:
			monthName = "December";
			break;
		}
		return monthName;
	}

}
