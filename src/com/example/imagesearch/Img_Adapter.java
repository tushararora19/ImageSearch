package com.example.imagesearch;

import java.util.ArrayList;

import com.loopj.android.image.SmartImageView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class Img_Adapter extends ArrayAdapter<ImageResults> {

	public Img_Adapter(Context context, ArrayList<ImageResults> images) {
		super(context, R.layout.show_img_ingrid, images);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// position : position of the item in the array (item in Grid view)
		// convertView : if the view has already been instantiated, you can take that view and recycle it (saves memory)
		// parent: giving access to Grid view itself
		
		ImageResults imgInfo = getItem(position);
		SmartImageView ivImage;
		
		if (convertView == null){ // i.e. there is no already existing one
			// create the new view (inflate it)
			LayoutInflater inflator = LayoutInflater.from(getContext());
			ivImage = (SmartImageView) inflator.inflate(R.layout.show_img_ingrid, parent, false);
		} else {
			ivImage = (SmartImageView) convertView; // reuse existing one
			ivImage.setImageResource(android.R.color.transparent); // clears out whatever is there
		}
		
		ivImage.setImageUrl(imgInfo.getFull_url());
		return ivImage;
		
	}
	
	

}
