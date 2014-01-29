package com.example.imagesearch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.loopj.android.image.SmartImageView;

public class ShowImageActivity extends Activity {

	SmartImageView image;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_image);
		
		image = (SmartImageView) findViewById(R.id.iv_imageDisplay);
		ImageResults result = (ImageResults) getIntent().getSerializableExtra("result");
		image.setImageUrl(result.getFull_url());
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_image, menu);
		return true;
	}

	public void goBack (MenuItem mi){
		Intent goBack_intent = new Intent (getApplicationContext(), SearchScreenActivity.class);
		setResult(RESULT_OK, goBack_intent);
		finish();
	}
}
