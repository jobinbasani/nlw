package com.jobinbasani.nlw;

import com.jobinbasani.nlw.sql.NlwDataDbHelper;
import com.jobinbasani.nlw.sql.NlwDataContract.NlwDataEntry;
import com.jobinbasani.nlw.util.NlwUtil;

import android.os.Bundle;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.support.v4.app.NavUtils;

public class DetailsActivity extends ListActivity {
	
	private Cursor cursor;
	private SQLiteDatabase db;
	private static final int OPEN_CALENDAR_ID = 1;
	private static final int ADD_EVENT_ID = 2;
	private static final int READ_MORE_ID = 3;
	private static final int SHARE_ID = 4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		// Show the Up button in the action bar.
		setupActionBar();
		final ListView listView = getListView();
		registerForContextMenu(listView);
		Intent detailsIntent = getIntent();
		String country = detailsIntent.getStringExtra(MainActivity.COUNTRY_KEY);
		TextView holidayCountryText = (TextView) findViewById(R.id.holidayCountryInfo);
		holidayCountryText.setText(getResources().getString(R.string.upcomingWeekendsText)+" "+country);
		cursor = getDetailsCursor(country);
		
		String[] from = new String[] { NlwDataEntry.COLUMN_NAME_NLWDATE, NlwDataEntry.COLUMN_NAME_NLWNAME, NlwDataEntry.COLUMN_NAME_NLWTEXT, NlwDataEntry.COLUMN_NAME_NLWDATE, NlwDataEntry.COLUMN_NAME_NLWDATE };
	    int[] to = new int[] { R.id.detailDateText, R.id.detailHolidayName, R.id.detailHolidayDetails, R.id.detailYearText, R.id.detailMonthText};
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(getBaseContext(), R.layout.nlw_details, cursor, from, to, SimpleCursorAdapter.NO_SELECTION);
		adapter.setViewBinder(new DetailsViewBinder());
		setListAdapter(adapter);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) menuInfo;
		RelativeLayout rl =  (RelativeLayout) acmi.targetView;
		TextView dateText = (TextView) rl.findViewById(R.id.detailHolidayName);
		menu.setHeaderTitle(dateText.getText()+"");
		menu.add(Menu.NONE, OPEN_CALENDAR_ID, 100, getResources().getString(R.string.detailsOpenCalendar));
		menu.add(Menu.NONE, ADD_EVENT_ID, 200, getResources().getString(R.string.addEventAction));
		menu.add(Menu.NONE, READ_MORE_ID, 300, getResources().getString(R.string.readMore));
		menu.add(Menu.NONE, SHARE_ID, 400, getResources().getString(R.string.shareAction));
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item.getMenuInfo();
		RelativeLayout rl =  (RelativeLayout) acmi.targetView;
		TextView dateText = (TextView) rl.findViewById(R.id.detailDateText);
		TextView detailsText = (TextView) rl.findViewById(R.id.detailHolidayDetails);
		TextView holidayText = (TextView) rl.findViewById(R.id.detailHolidayName);
		
		switch(item.getItemId()){
		case OPEN_CALENDAR_ID:
			startActivity(NlwUtil.getOpenCalendarIntent(Integer.parseInt(dateText.getTag()+"")));
			break;
		case ADD_EVENT_ID:
			startActivity(NlwUtil.getAddEventIntent(Integer.parseInt(dateText.getTag()+"")));
			break;
		case READ_MORE_ID:
			startActivity(NlwUtil.getBrowserIntent(detailsText.getTag()+""));
			break;
		case SHARE_ID:
			int dateNumber = Integer.parseInt(dateText.getTag()+"");
			int year = (dateNumber/10000)*10000;
			int month = (dateNumber - year)/100;
			int date = dateNumber-(year+(month*100));
			year = (year/10000)+2000;
			startActivity(NlwUtil.getShareDataIntent(holidayText.getText()+" on "+NlwUtil.getMonthName(month, false)+" "+date+", "+year+" - "+detailsText.getText()+". "+getResources().getString(R.string.readMoreAt)+" "+detailsText.getTag()));
			break;
		}
		return super.onContextItemSelected(item);
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
		case R.id.info_settings:
			AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
			alertBuilder.setTitle(getResources().getString(R.string.infoDialogTitle))
			.setMessage(getResources().getString(R.string.infoDialogMessage))
			.setCancelable(false)
			.setPositiveButton(getResources().getString(R.string.okButtonText), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
				}
			});
			AlertDialog dialog = alertBuilder.create();
			dialog.show();
			break;
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
				dateText.setTag(dateNumber+"");
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
			}else if(view.getId() == R.id.detailHolidayDetails){
				TextView detailsText = (TextView) view;
				detailsText.setText(cursor.getString(columnIndex)+"");
				detailsText.setTag(cursor.getString(cursor.getColumnIndexOrThrow(NlwDataEntry.COLUMN_NAME_NLWWIKI)));
				return true;
			}
			return false;
		}
		
	}

}
