package com.jobinbasani.nlw;

import com.jobinbasani.nlw.sql.NlwDataDbHelper;
import com.jobinbasani.nlw.sql.NlwDataContract.NlwDataEntry;
import com.jobinbasani.nlw.util.NlwUtil;

import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.support.v4.app.NavUtils;

public class DetailsActivity extends ListActivity {
	
	private Cursor cursor;
	private SQLiteDatabase db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		// Show the Up button in the action bar.
		setupActionBar();
		Intent detailsIntent = getIntent();
		String country = detailsIntent.getStringExtra(MainActivity.COUNTRY_KEY);
		TextView holidayCountryText = (TextView) findViewById(R.id.holidayCountryInfo);
		holidayCountryText.setText("Upcoming long weekends in "+country);
		cursor = getDetailsCursor(country);
		
		String[] from = new String[] { NlwDataEntry.COLUMN_NAME_NLWDATE, NlwDataEntry.COLUMN_NAME_NLWNAME, NlwDataEntry.COLUMN_NAME_NLWTEXT, NlwDataEntry.COLUMN_NAME_NLWDATE, NlwDataEntry.COLUMN_NAME_NLWDATE };
	    int[] to = new int[] { R.id.detailDateText, R.id.detailHolidayName, R.id.detailHolidayDetails, R.id.detailYearText, R.id.detailMonthText};
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(getBaseContext(), R.layout.nlw_details, cursor, from, to, SimpleCursorAdapter.NO_SELECTION);
		adapter.setViewBinder(new DetailsViewBinder());
		setListAdapter(adapter);
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		cursor.close();
		db.close();
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private Cursor getDetailsCursor(String country){
		NlwDataDbHelper nlwDbHelper = new NlwDataDbHelper(getBaseContext());
		db = nlwDbHelper.getReadableDatabase();
		return db.rawQuery("SELECT * FROM "+NlwDataEntry.TABLE_NAME+" WHERE "+NlwDataEntry.COLUMN_NAME_NLWCOUNTRY+"=? AND "+NlwDataEntry.COLUMN_NAME_NLWDATE+">? ORDER BY "+NlwDataEntry.COLUMN_NAME_NLWDATE, new String[]{country,NlwUtil.getCurrentDateNumber()+""});
	}
	
	private class DetailsViewBinder implements ViewBinder{

		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			if(view.getId() == R.id.detailDateText){
				TextView dateText = (TextView) view;
				int dateNumber = cursor.getInt(columnIndex);
				int year = dateNumber/10000;
				int month = (dateNumber-(year*10000))/100;
				int date = dateNumber-(year*10000)-(month*100);
				dateText.setText(date+"");
				return true;
			}else if(view.getId() == R.id.detailMonthText){
				TextView monthText = (TextView) view;
				int dateNumber = cursor.getInt(columnIndex);
				int year = dateNumber/10000;
				int month = (dateNumber-(year*10000))/100;
				monthText.setText(NlwUtil.getMonthName(month, true));
				return true;
			}else if(view.getId() == R.id.detailYearText){
				TextView yearText = (TextView) view;
				int dateNumber = cursor.getInt(columnIndex);
				int year = (dateNumber/10000)+2000;
				yearText.setText(year+"");
				return true;
			}
			return false;
		}
		
	}

}
