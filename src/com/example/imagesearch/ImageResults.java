package com.example.imagesearch;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

// External Libs: 
// use android smart image view library
// use android asynch http client library

public class ImageResults implements Serializable{

	private static final long serialVersionUID = 7207182588332852853L;
	private static final String TAG = "ImageResults";
	String full_url = "";	// stores 
	String thumb_url = "";

	public ImageResults(JSONObject json){
		try { 
			this.full_url = json.getString("url");
			this.thumb_url = json.getString("tbUrl");
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
		}
	}

	public String getFull_url() {
		return full_url;
	}

	public String getThumb_url() {
		return thumb_url;
	}

	public static ArrayList<ImageResults> parseJsonImageResultsArray(JSONArray img_results_arr){
		ArrayList<ImageResults> results = new ArrayList<ImageResults>();

		try {
			for (int i=0; i<img_results_arr.length(); i++){
				JSONObject json = img_results_arr.getJSONObject(i);
				results.add(new ImageResults(json));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return results;
	}
}
