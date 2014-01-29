package com.example.imagesearch;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends Activity {

	private static final String TAG = "SettingActivity";
	private ListView lv_size, lv_color, lv_type;

	private ArrayList<String> sizes = new ArrayList<String>();
	private ArrayList<String> colors = new ArrayList<String>();
	private ArrayList<String> types = new ArrayList<String>();

	private ArrayAdapter<String> size_adapter;
	private ArrayAdapter<String> color_adapter;  
	private ArrayAdapter<String> type_adapter;  


	private ArrayList<String> sizeSelected = new ArrayList<String>();
	private ArrayList<String> colorSelected = new ArrayList<String>();
	private ArrayList<String> typeSelected = new ArrayList<String>();

	MenuItem done;
	TextView tv_reset;
	EditText site_filter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		done = (MenuItem) findViewById(R.id.item_done);
		tv_reset = (TextView) findViewById(R.id.tv_resetAll);
		site_filter = (EditText) findViewById(R.id.et_site);

		populateLists();

		lv_size = (ListView) findViewById(R.id.lv_size);
		lv_color = (ListView) findViewById(R.id.lv_color);
		lv_type = (ListView) findViewById(R.id.lv_type);

		lv_size.setAdapter(size_adapter); 
		lv_color.setAdapter(color_adapter);
		lv_type.setAdapter(type_adapter);

		sizeSelected.clear();
		colorSelected.clear();
		typeSelected.clear();

		sizeSelected.addAll(getIntent().getStringArrayListExtra("sizeSelection"));
		colorSelected.addAll(getIntent().getStringArrayListExtra("colorSelection"));
		typeSelected.addAll(getIntent().getStringArrayListExtra("typeSelection"));

		// Only the first time
		/*resetAllFilters(lv_size);
		resetAllFilters(lv_color);
		resetAllFilters(lv_type);*/
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		Log.d(TAG, "Boolean: " + hasFocus);
		if (hasFocus){
			checkMarkFromPreviousHistory(lv_size, sizeSelected);
			checkMarkFromPreviousHistory(lv_color, colorSelected);
			checkMarkFromPreviousHistory(lv_type, typeSelected);
			setupSiteListener();
		}
	}

	private void setupSiteListener() {
		site_filter.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				showSoftKeyboard(site_filter);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				showSoftKeyboard(site_filter);	
			}
		});
		
		hideSoftKeyboard(site_filter);		
	}

	private void showSoftKeyboard(View v) { 
		if (v.requestFocus()){
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
			site_filter.setImeOptions(EditorInfo.IME_ACTION_NEXT);
		}
	}

	private void hideSoftKeyboard(View v) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	private void checkMarkFromPreviousHistory(ListView lv, ArrayList<String> list) {
		for (int i=0; i<lv.getCount();i++){
			//Log.d(TAG, "ListView: " + lv + " Child: " +lv.getAdapter().getView(i, lv.getChildAt(i), lv));
			//CheckedTextView ctv = (CheckedTextView) lv.getAdapter().getView(i, lv.getChildAt(i), lv).findViewById(R.id.ctv_property);

			View convertView = lv.getChildAt(i);
			if (convertView == null) {
				convertView = LayoutInflater.from(getBaseContext()).inflate(R.layout.filter_list, null);
			}
			CheckedTextView ctv = (CheckedTextView) convertView.findViewById(R.id.ctv_property);

			if (list.contains(ctv.getText().toString().trim())){
				Log.d(TAG, "CB: " + ctv.getText().toString() + " TRUE");
				ctv.setChecked(true);
			} else {
				ctv.setChecked(false);
			}
		}		
	}


	public void setupResetListener(View v){

		resetAllFilters(lv_size);
		sizeSelected.clear(); sizeSelected.add("Any Size");

		resetAllFilters(lv_color);
		colorSelected.clear(); colorSelected.add("Any Color");

		resetAllFilters(lv_type);
		typeSelected.clear(); typeSelected.add("Any Type");

		site_filter.setText("");
	}

	private void resetAllFilters(ListView lv) {

		for (int i=0; i<lv.getChildCount();i++){
			CheckedTextView ctv = (CheckedTextView) lv.getChildAt(i).findViewById(R.id.ctv_property);
			if (ctv.isChecked()){
				ctv.setChecked(false);
			}
			if (ctv.getText().toString().contains("Any")){
				ctv.setChecked(true);
			}
		}		
	}

	public void setupDoneListener(MenuItem mi) {
		Intent done_intent = new Intent(getApplicationContext(), SearchScreenActivity.class);

		done_intent.putStringArrayListExtra("sizeFilter", sizeSelected);
		done_intent.putStringArrayListExtra("colorFilter", colorSelected);
		done_intent.putStringArrayListExtra("typeFilter", typeSelected);
		done_intent.putExtra("site", site_filter.getText().toString().trim());
		Toast.makeText(getApplicationContext(), "Applying Filters....", Toast.LENGTH_LONG).show();

		setResult(RESULT_OK, done_intent);
		finish();
	}

	public void toggle(View v) {
		CheckedTextView tv = (CheckedTextView) v;

		ListView listparent = null; 

		if (sizes.contains(tv.getText().toString().trim())) {
			listparent = lv_size;
		} else if (colors.contains(tv.getText().toString().trim())) {
			listparent = lv_color;
		} else if (types.contains(tv.getText().toString().trim())) {
			listparent = lv_type;
		} 
		if (listparent != null){

			CheckedTextView tv_any = (CheckedTextView) listparent.getChildAt(0).findViewById(R.id.ctv_property);

			if (tv.isChecked()){
				tv.setChecked(false);
				//removeFromSelection(tv, listparent);
				// this is for Any: select Any if none is selected in that list and add to that list
				tv_any.setChecked(true);
				AddToSelection(tv_any, listparent);
			} else {
				AddToSelection(tv, listparent);
				// also clear other checks (allowing only single selection)
				ClearAllChoices(listparent);
				// now select this one
				tv.setChecked(true);
			}
		}

		if (tv.getText().toString().equals("Any Size")){
			// remove all other filters for that type
		} 
	}

	//	private void removeFromSelection(CheckedTextView tv, ListView parent) {
	//		if (parent.getId() == lv_size.getId()){
	//			sizeSelected.remove(tv.getText().toString().trim());
	//		} else if (parent.getId() == lv_color.getId()){
	//			colorSelected.remove(tv.getText().toString().trim());
	//		} if (parent.getId() == lv_type.getId()){
	//			typeSelected.remove(tv.getText().toString().trim());
	//		} else { 
	//
	//		}
	//	}

	private void ClearAllChoices(ListView lv) {
		for (int i=0; i<lv.getChildCount();i++){
			CheckedTextView ctv = (CheckedTextView) lv.getChildAt(i).findViewById(R.id.ctv_property);
			if (ctv.isChecked()){
				ctv.setChecked(false);
			}
		}				
	}

	private void AddToSelection(CheckedTextView tv, ListView parent) {
		//remove already selected item from list and then add new item
		if (parent.getId() == lv_size.getId()){
			sizeSelected.clear();
			sizeSelected.add(tv.getText().toString().trim());
		} else if (parent.getId() == lv_color.getId()){
			colorSelected.clear();
			colorSelected.add(tv.getText().toString().trim());
		} else if (parent.getId() == lv_type.getId()){
			typeSelected.clear();
			typeSelected.add(tv.getText().toString().trim());
		} else { 

		}
	}

	private void populateLists() {

		sizes.add("Any Size"); sizes.add("Small"); sizes.add("Medium"); sizes.add("Large");  
		colors.add("Any Color"); colors.add("Full Color"); colors.add("Black & White"); 
		types.add("Any Type"); types.add("Face"); types.add("Photo"); types.add("Clip Art");

		Filters filters_obj = new Filters(sizes, colors, types);

		size_adapter = new Filter_Adapter(this, filters_obj.getSizes());
		color_adapter = new Filter_Adapter(this, filters_obj.getColors());
		type_adapter = new Filter_Adapter(this, filters_obj.getTypes());

		//size_adapter = new ArrayAdapter<String>(this, R.layout.filter_list, R.id.ctv_property, sizes);
		//color_adapter = new ArrayAdapter<String>(this, R.layout.filter_list, R.id.ctv_property, colors);
		//type_adapter = new ArrayAdapter<String>(this, R.layout.filter_list, R.id.ctv_property, types);
		// lv_type.setAdapter(type_adapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	} 
}
