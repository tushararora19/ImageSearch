package com.example.imagesearch;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

public class Filter_Adapter extends ArrayAdapter<String> {

	ArrayList<String> filters;

	public Filter_Adapter(Context context, ArrayList<String> objects) {
//		super(context, R.layout.filter_list, objects);
		super(context, R.layout.filter_list, R.id.ctv_property, objects);
	}
	
	public ArrayList<String> getFilters() {
		return filters;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Get the data item for this position
		String filter = (String) getItem(position);    
		// Check if an existing view is being reused, otherwise inflate the view
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.filter_list, null);
		}
		
		CheckedTextView ctv = (CheckedTextView) convertView.findViewById(R.id.ctv_property);
		ctv.setText(filter); 
		
		return convertView;
	}
	
	
}
