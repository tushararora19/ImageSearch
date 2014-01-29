package com.example.imagesearch;

import java.util.ArrayList;

public class Filters {

	ArrayList<String> sizes, colors, types;
	
	public Filters(ArrayList<String> s, ArrayList<String> c, ArrayList<String> t) {
		sizes = s;  
		colors = c;
		types = t;
	}
	
	public ArrayList<String> getSizes() {
		return sizes;
	}
	
	public ArrayList<String> getColors() {
		return colors;
	}
	
	public ArrayList<String> getTypes() {
		return types;
	}
}
