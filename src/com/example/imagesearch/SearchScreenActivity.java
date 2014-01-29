package com.example.imagesearch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

// To DO: 
/*
 * 1. Apply filters should work (first time and also should be retained for future) DONE
 * 2. Indefinite scrolling should work (may be add Load more option) DONE
 * 3 .Support keyboard DONE
 * 4. Formatting (colors / icons / borders etc)
 * 5. tap on any image to show its full screen mode DONE
 * 6. add option for site search DONE (not persistent enough)
 * 6. Optional : share image with frens / email to yourself
 * 7. Optional : download image / save an image to photos folder
 * 8. requests fail after 64 images DONE
 * 9. new search query shows 8 images only and not all (first query shows 40 images) DONE
 */
public class SearchScreenActivity extends Activity {

	private static final String TAG = "SearchScreen";
	private static final String baseUrl = "https://ajax.googleapis.com/ajax/services/search/images?";
	static final int REQUEST_CODE = 1;

	SearchView search_query;
	GridView search_grid;

	ArrayList<ImageResults> img_results = new ArrayList<ImageResults>();
	Img_Adapter img_adapter;

	ArrayList<String> sizeSelected;
	ArrayList<String> colorSelected;
	ArrayList<String> typeSelected;

	String final_url = "";
	String site = "";
	
	String filename = "image_filters.txt";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_screen);

		search_query = (SearchView) findViewById(R.id.sv_search);
		search_grid = (GridView) findViewById(R.id.gv_result);

		setupQueryTextListener();
		setupEndlessScrolling();
		// setupDisplayImgListener();
		readFilters();

	}

	private void setupEndlessScrolling() {
		hideSoftKeyboard(search_query);
		search_grid.setOnScrollListener(new EndlessScrollListener(0){

			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				// Triggered only when new data needs to be appended to the list
				// Add whatever code is needed to append new items to your AdapterView
				Log.d(TAG, "current page: " + page + " total item count= " + totalItemsCount);
				customLoadMoreDataFromApi(page); 
				// or customLoadMoreDataFromApi(totalItemsCount);
			}

		});
	}

	// Append more data into the adapter
	public void customLoadMoreDataFromApi(int offset) {
		// This method probably sends out a network request and appends new data items to your adapter. 
		// Use the offset value and add it as a parameter to your API request to retrieve paginated data.
		// Deserialize API response and then construct new objects to append to the adapter		
		// Log.d(TAG, "current offset: " + offset);
		Toast.makeText(getApplicationContext(), "Loading More..", Toast.LENGTH_SHORT).show();
		if (!search_query.getQuery().toString().equals("")) {
			searchImages (search_query.getQuery().toString(), offset);
		}
	}

	private void parseString(String s){

		if (s.startsWith("Size=")){ // length -1 to remove , at the end
			s = s.substring(s.indexOf("=")+1, s.length()-1);
			addToArray(s, sizeSelected);
		}
		else if (s.startsWith("Color=")){
			s = s.substring(s.indexOf("=")+1, s.length()-1);
			addToArray(s, colorSelected);
		}
		else if (s.startsWith("Type=")){
			s = s.substring(s.indexOf("=")+1, s.length()-1);
			addToArray(s, typeSelected);
		}
		else {

		}
	}

	private void addToArray(String str, ArrayList<String> list){
		StringTokenizer token = new StringTokenizer(str, ",");
		while(token.hasMoreElements()){
			list.add(token.nextElement().toString());
		}
	}

	private void readFilters(){
		File filesDir = this.getFilesDir();
		File image_filter = new File(filesDir, filename);

		if (image_filter.exists()){
			Log.d(TAG, "fileExists");
		} else {
			try {
				image_filter.createNewFile(); 
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try{
			sizeSelected = new ArrayList<String>(); 
			colorSelected = new ArrayList<String>();
			typeSelected = new ArrayList<String>();

			@SuppressWarnings("resource")
			BufferedReader buf = new BufferedReader(new FileReader(image_filter));
			String str = buf.readLine();
			while(str != null){
				parseString (str);
				str = buf.readLine();
			}
		} catch (Exception e){
			Log.d(TAG, e.toString());
		}
	} 

	private void saveFilters(){
		hideSoftKeyboard(search_query);
		File filesDir = this.getFilesDir();
		File image_filter = new File(filesDir, filename);

		try {
			// first delete all contents inside the file.
			image_filter.createNewFile(); 

			PrintWriter pw = new PrintWriter(image_filter);

			// now write the file (note: we are not deleting the file itself, simply clearing its content)
			write_Filter_toFile(image_filter, "size", sizeSelected, pw);
			write_Filter_toFile(image_filter, "color", colorSelected, pw);
			write_Filter_toFile(image_filter, "type", typeSelected, pw);	

			pw.close();
			// after writing new filters, check if query was already there and if so, change results
			if (!search_query.getQuery().toString().equals("")) {
				img_results.clear();
				searchImages (search_query.getQuery().toString(), 0);	// default offset is 0
				setupEndlessScrolling();
			}
		} catch (FileNotFoundException e) {
			Log.d(TAG, e.toString());
		} catch (Exception e) {
			Log.d(TAG, e.toString());
		}

	}

	private void write_Filter_toFile(File image_filter, String s, ArrayList<String> list, PrintWriter pw){
		try{
			if (s.equalsIgnoreCase("size")){
				pw.write("Size=");
			} else if (s.equalsIgnoreCase("color")){
				pw.write("Color=");
			}
			else if (s.equalsIgnoreCase("type")){
				pw.write("Type=");
			}
			else {

			}

			for (int i=0;i<list.size();i++){
				pw.write(list.get(i)+",");
			}
			pw.write("\n");
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	private void setupQueryTextListener() {

		search_query.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				Log.d(TAG, search_query.getQuery().toString()+ " submitted");
				// here is when u get query
				// create an object for a QueryProcessing class to submit and get back response back.
				hideSoftKeyboard(search_query);
				if (!search_query.getQuery().toString().equals("")) {
					img_results.clear();
					if (img_adapter!=null)
						img_adapter.notifyDataSetInvalidated();
					searchImages (search_query.getQuery().toString(), 0);
					setupEndlessScrolling();
				}
				return false; 
			}
			@Override
			public boolean onQueryTextChange(String arg0) {
				Log.d(TAG, search_query.getQuery().toString()+ " changed");
				showSoftKeyboard(search_query);
				return false;
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search_screen, menu);
		return true;
	}

	public void onSettingsClick (MenuItem mi){
		readFilters();

		Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);

		intent.putStringArrayListExtra("sizeSelection", sizeSelected);
		intent.putStringArrayListExtra("colorSelection", colorSelected);
		intent.putStringArrayListExtra("typeSelection", typeSelected);

		startActivityForResult(intent, REQUEST_CODE);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		Log.d(TAG, "Req code: " + requestCode + " Res code: "+ resultCode + " Intent data: " +intent);

		sizeSelected.clear();
		colorSelected.clear();
		typeSelected.clear();

		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK){
			sizeSelected.addAll(intent.getStringArrayListExtra("sizeFilter"));
			colorSelected.addAll(intent.getStringArrayListExtra("colorFilter"));
			typeSelected.addAll(intent.getStringArrayListExtra("typeFilter"));
			site = intent.getStringExtra("site");
			saveFilters();
		} 
	}

	private void showSoftKeyboard(View v) { 
		if (v.requestFocus()){
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
		}
	}

	private void hideSoftKeyboard(View v) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}
	
	private void searchImages(String query, int offset) {
		AsyncHttpClient client = new AsyncHttpClient();

		hideSoftKeyboard(search_query);

		// Google allows max of 8 pages 64 results
		if (offset <=7){
			JsonHttpResponseHandler response_handler = new JsonHttpResponseHandler() {

				@Override
				public void onFailure(Throwable e, JSONObject errorResponse) {
					super.onFailure(e, errorResponse);
					Log.d(TAG, "Request FAILED !!");
					Toast.makeText(getApplicationContext(), "Request Failed..", Toast.LENGTH_LONG).show();
				}

				@Override
				public void onSuccess(int statusCode, JSONObject response) {
					super.onSuccess(statusCode, response);
					// get array of results in this code
					JSONArray img_results_arr = null;
					try {
						// get json object for responseData and then its array for results
						img_results_arr = (response.getJSONObject("responseData")).getJSONArray(("results"));
						// always clear contents inside image results
						// img_results.clear();
						img_results.addAll(ImageResults.parseJsonImageResultsArray(img_results_arr));
						Log.d(TAG, img_results.toString());
						Toast.makeText(getApplicationContext(), "Searching....", Toast.LENGTH_LONG).show();
						showAsImage();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

			};
			readFilters();
			final_url = "";
			final_url = formFinalURL(new StringBuffer(baseUrl + "v=1.0&q=" + Uri.encode(query) + "&rsz=8&start="+(offset*8))); // 8 images per page
			client.get(final_url, response_handler);
			
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(search_query.getWindowToken(), 0);
		} else {
			// searchImages(query, 0);
		}
	}

	private String formFinalURL(StringBuffer basic_url) {
		// no need to change anything or add anything to call if value is Any
		for (int i=0; i<sizeSelected.size();i++){
			if (!sizeSelected.get(i).toLowerCase().trim().equals("Any Size"))
				basic_url = basic_url.append("&imgsz=" + Uri.encode(sizeSelected.get(i).toLowerCase().trim()));
		}
		for (int i=0; i<colorSelected.size();i++){
			if (!sizeSelected.get(i).toLowerCase().trim().equals("Any Color"))
				basic_url = basic_url.append("&imgcolor=" + Uri.encode(colorSelected.get(i).toLowerCase().trim()));
		}
		for (int i=0; i<typeSelected.size();i++){
			if (!sizeSelected.get(i).toLowerCase().trim().equals("Any Type"))
				basic_url = basic_url.append("&imgtype=" + Uri.encode(typeSelected.get(i).toLowerCase().trim()));
		}
		if (!site.equals("")){
			basic_url = basic_url.append("&as_sitesearch="+site.toLowerCase().trim());
		}

		return basic_url.toString();
	}

	private void showAsImage(){
		// use values in img_results array to show on screen
		img_adapter = new Img_Adapter(getApplicationContext(), img_results);
		search_grid.setAdapter(img_adapter);
		setupDisplayImgListener();
		img_adapter.notifyDataSetChanged();
	}

	// on Click method called when any grid image is clicked
	public void setupDisplayImgListener(){

		search_grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View parent, int pos, long arg3) {
				Intent showImg_intent = new Intent(getApplicationContext(), ShowImageActivity.class);
				ImageResults img = img_results.get(pos);
				//showImg_intent.putExtra("url", img.getFull_url().toString());
				// we can do above as well, but we will try to pass imageResults object as a whole
				showImg_intent.putExtra("result", img);
				startActivity(showImg_intent);
			}
		});

	}
}
