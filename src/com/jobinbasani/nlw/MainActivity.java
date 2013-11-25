package com.jobinbasani.nlw;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import com.jobinbasani.nlw.sql.NlwDataContract.NlwDataEntry;
import com.jobinbasani.nlw.sql.NlwDataDbHelper;
import com.jobinbasani.nlw.util.NlwUtil;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ShareActionProvider;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	SharedPreferences prefs;
	final public static String COUNTRY_KEY = "country";
	public static Context NLW_CONTEXT;
	private int nlwDateNumber;
	private String readMoreLink;
	private ShareActionProvider mShareActionProvider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		NLW_CONTEXT = this;
		
		prefs = getPreferences(MODE_PRIVATE);
		
		new DatabaseLoaderTask().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		MenuItem shareItem = menu.findItem(R.id.shareMenuItem);
		mShareActionProvider = (ShareActionProvider) shareItem.getActionProvider();
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()){
		case R.id.eventMenuItem:
			addNlwEvent();
			break;
		case R.id.feedbackMenuItem:
			sendFeedback();
			break;
		case R.id.shareMenuItem:
			shareNlwDetails();
			break;
		}
		
		return true;
	}

	private void loadPreferences(){
		String defaultCountry = prefs.getString(COUNTRY_KEY, "USA");
		Spinner countrySpinner = (Spinner) findViewById(R.id.countrySelector);
		SpinnerAdapter countryArray = countrySpinner.getAdapter();
		if(defaultCountry.equals(countrySpinner.getSelectedItem().toString())){
			loadNextLongWeekend();
		}
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
	
	private void setNlwDateCLickListener(){
		TextView nlwDateText = (TextView) findViewById(R.id.nlwDateText);
		nlwDateText.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Calendar cal = getCalendarObject();
				long time = cal.getTime().getTime();
				Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
				builder.appendPath("time");
				builder.appendPath(Long.toString(time));
				Intent calendarIntent = new Intent(Intent.ACTION_VIEW,builder.build());
				startActivity(calendarIntent);
			}
		});
	}
	
	private void addNlwEvent(){
		
		Calendar cal = getCalendarObject();
		Intent calendarIntent = new Intent(Intent.ACTION_INSERT);
		calendarIntent.setData(Events.CONTENT_URI);
		calendarIntent.putExtra(Events.ALL_DAY, true);
		calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, cal.getTime().getTime());
		calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, cal.getTime().getTime()+600000);
		startActivity(calendarIntent);
	}
	
	private void sendFeedback(){
		Intent emailIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:"+getResources().getString(R.string.feedbackEmail)+"?subject="+Uri.encode("NLW Feedback")));
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, "NLW Feedback");
		
		List<ResolveInfo> activities = getPackageManager().queryIntentActivities(emailIntent, 0);
		//To prevent Receiver leak bug when only application is available for Intent
		if (activities.size() > 1) {
		    // Create and start the chooser
		    Intent chooser = Intent.createChooser(emailIntent, "Send Feedback");
		    startActivity(chooser);

		  } else {
		    startActivity( emailIntent );
		}
	}
	
	private void shareNlwDetails(){
		if(mShareActionProvider != null){
			TextView holidayText = (TextView)findViewById(R.id.nlwHolidayText);
			TextView holidayDate = (TextView) findViewById(R.id.nlwDateText);
			TextView holidayMonth = (TextView) findViewById(R.id.monthYearText);
			String[] holidayMonthArray = holidayMonth.getText().toString().split(" ");
			Intent shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND);
			shareIntent.putExtra(Intent.EXTRA_TEXT, holidayText.getText()+" on "+holidayMonthArray[0]+" "+holidayDate.getText()+", "+holidayMonthArray[1]+". Read more at "+readMoreLink);
			shareIntent.setType("text/plain");
			mShareActionProvider.setShareIntent(shareIntent);
		}
	}
	
	private Calendar getCalendarObject(){
		int year = nlwDateNumber/10000;
		int month = (nlwDateNumber-(year*10000))/100;
		int date = nlwDateNumber-(year*10000)-(month*100);
		year = 2000+year;
		month--;
		return new GregorianCalendar(year, month, date);
	}
	
	public void onReadMore(View view){
		if(readMoreLink!=null){
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(readMoreLink));
			startActivity(browserIntent);
		}
	}
	
	public void onViewAll(View view){
		Intent viewAllIntent = new Intent(this, DetailsActivity.class);
		Spinner countrySpinner = (Spinner) findViewById(R.id.countrySelector);
		viewAllIntent.putExtra(COUNTRY_KEY, countrySpinner.getSelectedItem().toString());
		startActivity(viewAllIntent);
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
	
	private void loadNextLongWeekend() {
		
		TextView monthYearText = (TextView) findViewById(R.id.monthYearText);
		TextView holidayText = (TextView) findViewById(R.id.nlwHolidayText);
		TextView nlwDateText = (TextView) findViewById(R.id.nlwDateText);
		TextView holidayDetails = (TextView) findViewById(R.id.holidayDetails);
		Spinner countrySelector = (Spinner) findViewById(R.id.countrySelector);
		int currentDateNumber = NlwUtil.getCurrentDateNumber();
		String selectedCountry = countrySelector.getSelectedItem().toString();
		String[] selectionArgs = new String[]{currentDateNumber+"", selectedCountry};
		
		NlwDataDbHelper nlwDbHelper = new NlwDataDbHelper(NLW_CONTEXT);
		SQLiteDatabase db = nlwDbHelper.getWritableDatabase();
		
		Cursor cursor = db.rawQuery("SELECT * FROM "+NlwDataEntry.TABLE_NAME+" WHERE "+NlwDataEntry.COLUMN_NAME_NLWDATE+">? AND "+NlwDataEntry.COLUMN_NAME_NLWCOUNTRY+"=? ORDER BY _ID LIMIT 1", selectionArgs);
		cursor.moveToFirst();
		if(cursor.getCount()>0){
			nlwDateNumber = cursor.getInt(cursor.getColumnIndexOrThrow(NlwDataEntry.COLUMN_NAME_NLWDATE));
			readMoreLink = cursor.getString(cursor.getColumnIndexOrThrow(NlwDataEntry.COLUMN_NAME_NLWWIKI));
			String holiday = cursor.getString(cursor.getColumnIndexOrThrow(NlwDataEntry.COLUMN_NAME_NLWNAME));
			String holidayDetailText = cursor.getString(cursor.getColumnIndexOrThrow(NlwDataEntry.COLUMN_NAME_NLWTEXT));
			int year = nlwDateNumber/10000;
			int month = (nlwDateNumber-(year*10000))/100;
			int date = nlwDateNumber-(year*10000)-(month*100);
			String monthName = NlwUtil.getMonthName(month, false);
			year = 2000+year;
			
			monthYearText.setText(monthName+" "+year);
			nlwDateText.setText(date+"");
			holidayText.setText(holiday);
			holidayDetails.setText(holidayDetailText);
			
		}
		cursor.close();
		db.close();
	}
	
	private class DatabaseLoaderTask extends AsyncTask<Void, Void, Void>{
		
		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MainActivity.this);
	        pDialog.setMessage("Loading ...");
	        pDialog.setIndeterminate(false);
	        pDialog.setCancelable(true);
	        pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			NlwDataDbHelper nlwDbHelper = new NlwDataDbHelper(NLW_CONTEXT);
			SQLiteDatabase db = nlwDbHelper.getWritableDatabase(); //Creates or inserts initial data asynchronously
			db.close();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			pDialog.dismiss();
			setCountrySelectionListener();
			setNlwDateCLickListener();
			loadPreferences();
		}
		
	}

}
