package com.jobinbasani.nlw.util;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;


public class NlwUtil {

	public static String getMonthName(int month, boolean abbr){
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
		if(abbr){
			monthName = monthName.substring(0, 3);
		}
		return monthName;
	}
	
	public static int getCurrentDateNumber(){
		Calendar rightNow = Calendar.getInstance();
		int year, month, day;
		
		year = Integer.parseInt((rightNow.get(Calendar.YEAR)+"").substring(2, 4))*10000;
		month = (rightNow.get(Calendar.MONTH)+1)*100;
		day = rightNow.get(Calendar.DATE);
		return year+month+day;
	}
	
	public static Calendar getCalendarObject(int nlwDateNumber){
		int year = nlwDateNumber/10000;
		int month = (nlwDateNumber-(year*10000))/100;
		int date = nlwDateNumber-(year*10000)-(month*100);
		year = 2000+year;
		month--;
		return new GregorianCalendar(year, month, date);
	}
	
	public static Intent getOpenCalendarIntent(int nlwDateNumber){
		Calendar cal = getCalendarObject(nlwDateNumber);
		long time = cal.getTime().getTime();
		Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
		builder.appendPath("time");
		builder.appendPath(Long.toString(time));
		Intent calendarIntent = new Intent(Intent.ACTION_VIEW,builder.build());
		return calendarIntent;
	}

}
