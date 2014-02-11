package com.indoormap.framework.layer;

import java.util.ArrayList;

import android.view.View;

import com.indoormap.framework.common.MapCommonUtil;
import com.indoormap.framework.element.POIElement;
import com.indoormap.framework.layer.base.BaseLayerLayout;
import com.indoormap.framework.map.MapView;
import com.indoormap.framework.model.POI;

public class POILayerLayout extends BaseLayerLayout{

	public POILayerLayout(MapView mapView) {
		super(mapView);
	}
	public void checkVisiable(){
		int count = getChildCount();
		float scale = MapCommonUtil.getMapScale(mapView);
		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			if (child.getVisibility() != GONE) {
				if (child instanceof POIElement) {
					POI poi = ((POIElement) child).getPOI();
					if(poi.getStartVisibilityLevel()<scale&&scale <= poi.getEndVisibilityLevel()){
						child.setVisibility(VISIBLE);
					}else{
						child.setVisibility(INVISIBLE);
					}
				}
			}
		}
		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			if (child.getVisibility() != GONE) {
				if (child instanceof POIElement) {
					if(child.getVisibility()==VISIBLE){
						POIElement beChild = (POIElement) child;
						ArrayList<Integer> tempInvisiableList = beChild.getInvisiableList(beChild);
						if(tempInvisiableList !=null){
							for(Integer in : tempInvisiableList){
								View a = getChildAt(in);
								if(a instanceof POIElement){
									a.setVisibility(INVISIBLE);
//									((POIElement) a).fadeOutAnim();
								}
								
							}
						}
					}
				}
			}
		}
	}
}
