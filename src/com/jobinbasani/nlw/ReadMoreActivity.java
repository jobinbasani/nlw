package com.jobinbasani.nlw;

import com.jobinbasani.nlw.util.NlwUtil;

import android.net.Uri;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ReadMoreActivity extends Activity {
	
	private WebView webView;
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_read_more);
		// Show the Up button in the action bar.
		setupActionBar();
		
		Intent readMoreIntent = getIntent();
		final ProgressDialog pd = ProgressDialog.show(this, "", getResources().getString(R.string.loadingText),true);
		String url = readMoreIntent.getStringExtra(NlwUtil.URL_KEY);
		webView = (WebView) findViewById(R.id.webView);
		
		webView.getSettings().setSupportZoom(true);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebChromeClient(new WebChromeClient(){

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if(newProgress >= 80){
					pd.dismiss();
				}
				super.onProgressChanged(view, newProgress);
			}
			
		});
		webView.setWebViewClient(new WebViewClient()); 
		webView.loadUrl(url);
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
