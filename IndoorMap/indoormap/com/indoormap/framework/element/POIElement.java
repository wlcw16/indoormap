package com.indoormap.framework.element;

import java.util.ArrayList;
import java.util.HashMap;

import android.graphics.Color;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.indoormap.R;
import com.indoormap.framework.common.MapCommonUtil;
import com.indoormap.framework.element.base.BaseElementFrameLayout;
import com.indoormap.framework.map.MapView;
import com.indoormap.framework.model.POI;

public class POIElement extends BaseElementFrameLayout{

	protected POI poi;

	protected TextView labelTv;

	protected ImageView iconIv;
	private int backgroudRscId = 0;
	
	private static HashMap<String, Integer> poiIconRelationshipHashMap;

	private static HashMap<String, Integer> poiIconRelationshipHashMap_S;
	static {
		poiIconRelationshipHashMap = new HashMap<String, Integer>();
		poiIconRelationshipHashMap.put(POI.ELEVATOR, R.drawable.map_icon_elevator);
		poiIconRelationshipHashMap.put(POI.STAIRS, R.drawable.map_icon_stairs);
		poiIconRelationshipHashMap.put(POI.INFOMATION_DESK, R.drawable.map_icon_info);
		poiIconRelationshipHashMap.put(POI.TOILET, R.drawable.map_icon_wc);
		poiIconRelationshipHashMap.put(POI.ENTRANCE_EXIT, R.drawable.map_icon_exit);
		poiIconRelationshipHashMap.put(POI.ESCALATOR, R.drawable.map_icon_escalator);
		poiIconRelationshipHashMap.put(POI.GATE, R.drawable.map_icon_gate);
		poiIconRelationshipHashMap.put(POI.GUARD, R.drawable.map_icon_guard);
		poiIconRelationshipHashMap.put(POI.FIRE, R.drawable.map_icon_fire);
		poiIconRelationshipHashMap.put(POI.ATM, R.drawable.map_icon_atm);
		poiIconRelationshipHashMap_S = new HashMap<String, Integer>();
		poiIconRelationshipHashMap_S.put(POI.ELEVATOR, R.drawable.map_icon_elevator_s);
		poiIconRelationshipHashMap_S.put(POI.STAIRS, R.drawable.map_icon_stairs_s);
		poiIconRelationshipHashMap_S.put(POI.INFOMATION_DESK, R.drawable.map_icon_info_s);
		poiIconRelationshipHashMap_S.put(POI.TOILET, R.drawable.map_icon_wc_s);
		poiIconRelationshipHashMap_S.put(POI.ENTRANCE_EXIT, R.drawable.map_icon_exit_s);
		poiIconRelationshipHashMap_S.put(POI.ESCALATOR, R.drawable.map_icon_escalator_s);
		poiIconRelationshipHashMap_S.put(POI.GATE, R.drawable.map_icon_gate_s);
		poiIconRelationshipHashMap_S.put(POI.GUARD, R.drawable.map_icon_guard_s);
		poiIconRelationshipHashMap_S.put(POI.FIRE, R.drawable.map_icon_fire_s);
		poiIconRelationshipHashMap_S.put(POI.ATM, R.drawable.map_icon_atm_s);
	}
	
	public POIElement(MapView mapView, POI poi) {
		super(mapView, poi.getPoint());
		this.poi = poi;
		init();
	}

	@Override
	protected void init() {
		labelTv = new TextView(getContext());
		iconIv = new ImageView(getContext());
		labelTv.setGravity(Gravity.CENTER);
//		labelTv.setTextColor(getContext().getResources().getColorStateList(R.color.tsinghua_darkgray));
		labelTv.setShadowLayer(2, 0, 0, Color.GRAY);
		switch (poi.getPoiMode()) {
		case POI.POI_MODE_LABEL:
			labelTv.setTextSize(12);
			labelTv.setText(poi.getName());
			labelTv.setVisibility(VISIBLE);
			iconIv.setVisibility(GONE);
			break;
		case POI.POI_MODE_LETTERING:
			labelTv.setTextSize(12);
			labelTv.setText(poi.getName());
			labelTv.setVisibility(VISIBLE);
			iconIv.setVisibility(GONE);
			break;
		case POI.POI_MODE_ICON:
			iconIv.setImageResource(poiIconRelationshipHashMap.get(poi.getIcon()));
			backgroudRscId = poiIconRelationshipHashMap.get(poi.getIcon());
			labelTv.setVisibility(GONE);
			iconIv.setVisibility(VISIBLE);
			break;
		}
		int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		int width = 0;
		int height = 0;
		if(poi.getPoiMode()==POI.POI_MODE_ICON){
			iconIv.measure(w, h);
			width = iconIv.getMeasuredWidth();
			height = iconIv.getMeasuredHeight();
		}else{
			labelTv.measure(w, h);
			width = labelTv.getMeasuredWidth();
			height = labelTv.getMeasuredHeight();
		}
		deltaWidth = width/2f;
		deltaHeight = height/2f;
		this.addView(labelTv);
		this.addView(iconIv);
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
		return height/2f;
	}
	

	@Override
	public void update() {
		if (poi.getPoiMode() == POI.POI_MODE_ICON) {
			if (MapCommonUtil.getMapScale(mapView) < 0.7) {
				if (backgroudRscId != -1) {
//					labelTv.setBackgroundResource(R.drawable.point_add);

					iconIv.setImageResource(poiIconRelationshipHashMap_S.get(poi.getIcon()));
					backgroudRscId = -1;
					int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
					int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
					iconIv.measure(w, h);
					int width = iconIv.getMeasuredWidth();
					int height = iconIv.getMeasuredHeight();
					if(width != height){
						if(width >height){
							height = width;
						}else{
							width = height;
						}
					}
					this.width = width;
					this.height = height;
				}
			} else {
				if (backgroudRscId != poiIconRelationshipHashMap.get(poi.getIcon())) {
					iconIv.setImageResource(poiIconRelationshipHashMap.get(poi.getIcon()));
					backgroudRscId = poiIconRelationshipHashMap.get(poi.getIcon());
					int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
					int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
					iconIv.measure(w, h);
					int width = iconIv.getMeasuredWidth();
					int height = iconIv.getMeasuredHeight();
					this.width = width;
					this.height = height;
				}
			}

		}
		super.update();
	}
	
	public ArrayList<Integer> getInvisiableList(final View view){
		View currentView = view;
		ArrayList<Integer> result = new ArrayList<Integer>();
		
		if (currentView.getParent() instanceof ViewGroup) {
			ViewGroup currentParent = (ViewGroup) currentView.getParent();
			if (currentParent.getVisibility() != View.VISIBLE) {
				return null;
			}
			
			boolean isAddSelf = false;
			
			int start = indexOfViewInParent(currentView, currentParent);
			for (int i = start + 1; i < currentParent.getChildCount(); i++) {
				Rect viewRect = new Rect();
				boolean temp = view.getGlobalVisibleRect(viewRect);
				if(!temp && !isAddSelf){
					result.add(start);
					isAddSelf = true;
				}
				View otherView = currentParent.getChildAt(i);
				
				if(otherView instanceof POIElement){
					POIElement ov = (POIElement)otherView;
					if(ov.isAnimationing){
						continue;
					}
				}
				if (otherView.getVisibility() == VISIBLE) {
					Rect otherViewRect = new Rect();
					otherView.getGlobalVisibleRect(otherViewRect);
					if (Rect.intersects(viewRect, otherViewRect)) {
						result.add(i);
					}
				}
			}
			if(result==null||result.size()<1){
				return null;
			}else{
				return result;
			}
		}
		return null;
	}
	private int indexOfViewInParent(View view, ViewGroup parent) {
		int index;
		for (index = 0; index < parent.getChildCount(); index++) {
			if (parent.getChildAt(index) == view)
				break;
		}
		return index;
	}

	public POI getPOI(){
		return poi;
	}
	
	private boolean isAnimationing = false;
	
	public void fadeInAnim(){
		AlphaAnimation aa = new AlphaAnimation(0, 1);
		aa.setDuration(200);
		aa.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				setAnimationing(true);
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				setAnimationing(false);
			}
		});
		this.startAnimation(aa);
		
	}
	
	public void fadeOutAnim(){
		AlphaAnimation aa = new AlphaAnimation(1, 0);
		aa.setDuration(200);
		aa.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				setAnimationing(true);
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				setAnimationing(false);
				setVisibility(INVISIBLE);
			}
		});
		this.startAnimation(aa);
	}

	public void setAnimationing(boolean isAnimationing) {
		this.isAnimationing = isAnimationing;
	}

	public boolean isAnimationing() {
		return isAnimationing;
	}

	
	
}
