package com.cutv.mobile.cartoon;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CartoonFragmentPage extends BaseFragment{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.fragment_cartoon, null);		
	}
	
	
	@Override
	public void onStart()
	{
		super.onStart();
		setTitle("漫画");
		
	}
	
}
