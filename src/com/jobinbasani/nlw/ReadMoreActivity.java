package com.jobinbasani.nlw;

import com.google.analytics.tracking.android.EasyTracker;
import com.jobinbasani.nlw.util.NlwUtil;

import android.net.Uri;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class ReadMoreActivity extends Activity {
	
	private WebView webView;
	final Activity activity = this;
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.activity_read_more);
		// Show the Up button in the action bar.
		setupActionBar();
		
		Intent readMoreIntent = getIntent();
		String url = readMoreIntent.getStringExtra(NlwUtil.URL_KEY);
		webView = (WebView) findViewById(R.id.webView);
		
		webView.getSettings().setSupportZoom(true);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebChromeClient(new WebChromeClient(){

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				
				activity.setTitle(getResources().getString(R.string.loadingText));
				activity.setProgress(newProgress * 100);
				if(newProgress > 80){
					activity.setTitle(getResources().getString(R.string.title_activity_read_more));
				}
			}
			
		});
		webView.setWebViewClient(new WebViewClient(){

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				Toast.makeText(activity, description, Toast.LENGTH_SHORT).show();
			}
			
		}); 
		webView.loadUrl(url);
	}

	@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.read_more, menu);
		return true;
	}
	
	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case android.R.id.home:
			onBackPressed();
			return true;
		case R.id.open_browser:
			String url = webView.getOriginalUrl();
			Intent browserIntent = new Intent(Intent.ACTION_VIEW);
			browserIntent.setData(Uri.parse(url));
			Intent chooser = Intent.createChooser(browserIntent, getResources().getString(R.string.browserChooserTitle));
			startActivity(chooser);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
