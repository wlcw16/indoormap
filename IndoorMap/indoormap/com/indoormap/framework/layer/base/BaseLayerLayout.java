package com.indoormap.framework.layer.base;

import android.view.View;
import android.view.ViewGroup;

import com.indoormap.framework.element.base.BaseElement;
import com.indoormap.framework.map.MapView;

public class BaseLayerLayout extends ViewGroup {
	
	protected MapView mapView;

	public BaseLayerLayout(MapView mapView) {
		super(mapView.getContext());
		this.mapView = mapView;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		update();
	}

	
	
	public void update(){
		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			if (child.getVisibility() != GONE) {
				if (child instanceof BaseElement) {
					float[] values = new float[9];
					mapView.getImgMatrix().getValues(values);
					BaseElement beChild = (BaseElement) child;
					beChild.update();
				}
			}
		}
	}
}
