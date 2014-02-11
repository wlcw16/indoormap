package com.indoormap.framework.element;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.indoormap.R;
import com.indoormap.framework.element.base.BaseElementFrameLayout;
import com.indoormap.framework.map.MapView;
import com.indoormap.framework.model.Location;

public class LocationPointElement extends BaseElementFrameLayout{

	protected Location location;
	
	protected TextView labelTv;
	
	public LocationPointElement(MapView mapView,Location l) {
		super(mapView,l.getPoint());
		this.location = l;
		init();
	}

	@Override
	protected void init() {
		labelTv = new TextView(getContext());
		labelTv.setBackgroundResource(R.drawable.target_2_1);
		String[] name = location.getName().split("_");
		String index = name[3];
		labelTv.setText(index);
		labelTv.setGravity(Gravity.CENTER);
		labelTv.setTextColor(Color.BLACK);
		int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		labelTv.measure(w, h);
		int width = labelTv.getMeasuredWidth();
		int height = labelTv.getMeasuredHeight();
		deltaWidth = width/2f;
		deltaHeight = height/2f;
		this.addView(labelTv);
		this.setBeWidth(width);
		this.setBeHeight(height);
		this.setVisibility(VISIBLE);
		update();
	}
	
	@Override
	public float getDeltaWdith() {
		return width/2f;
	}
	
	@Override
	public float getDeltaHeight() {
		return height;
	}
	
	public Location getLocation(){
		return location;
	}
	
}
