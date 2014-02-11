package com.indoormap.framework.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.indoormap.R;
import com.indoormap.framework.common.MapCommonConstant;
import com.indoormap.framework.common.MapCommonData;
import com.indoormap.framework.model.Map;

public class MapBottomWidget extends LinearLayout {

	private View baseView;

	@SuppressWarnings("unused")
	private LinearLayout bottomLl;

	private LinearLayout topLl;

	private Button floorBtn;

	private Button buildingBtn;

	private Button positionBtn;

	private LinearLayout floorsLl;

	private LinearLayout buildingsLl;

	private OnMapBottomWidgetClickListener onMapBottomWidgetClickListener;
	
	private Map map;

	public MapBottomWidget(Context context) {
		super(context);
		findView();
	}

	public void init(Map map) {
		this.map = map;
		topLl.setVisibility(GONE);
		initFloor();
		initBuilding();
		initPosition();
	}

	private void findView() {
		baseView = LayoutInflater.from(getContext()).inflate(R.layout.map_bottom_widget, this);
		topLl = (LinearLayout) baseView.findViewById(R.id.map_bottom_widget_top_ll);
		bottomLl = (LinearLayout) baseView.findViewById(R.id.map_bottom_widget_bottom_ll);
		floorBtn = (Button) baseView.findViewById(R.id.map_bottom_widget_bottom_floor_btn);
		buildingBtn = (Button) baseView.findViewById(R.id.map_bottom_widget_bottom_building_btn);
		positionBtn = (Button) baseView.findViewById(R.id.map_bottom_widget_bottom_position_btn);
		floorsLl = (LinearLayout) baseView.findViewById(R.id.map_bottom_widget_top_floor_ll);
		buildingsLl = (LinearLayout) baseView.findViewById(R.id.map_bottom_widget_top_building_ll);

	}

	private void initFloor() {
		floorBtn.setText(map.getMapNickname());
		floorBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(topLl.getVisibility()==VISIBLE){
					topLl.setVisibility(GONE);
					floorsLl.setVisibility(INVISIBLE);
					buildingsLl.setVisibility(INVISIBLE);
				}else{
					topLl.setVisibility(VISIBLE);
					floorsLl.setVisibility(VISIBLE);
					buildingsLl.setVisibility(INVISIBLE);
				}
			}
		});
		floorsLl.removeAllViews();
		List<Map> dataList = new ArrayList<Map>();
		HashMap<String, Map> temp = MapCommonData.getInstance().getMapHashMap();
		Iterator<?> iter = temp.entrySet().iterator();
		while (iter.hasNext()) {
			@SuppressWarnings("unchecked")
			java.util.Map.Entry<String, Map> entry = (java.util.Map.Entry<String, Map>) iter.next();
			Map value = entry.getValue();
			//判断项目
			if(MapCommonConstant.PROJECT_NAME.equals(value.getMapName().split("_")[0])){
				//判断建筑物
				if(map.getMapName().split("_")[1].equals(value.getMapName().split("_")[1])){
					dataList.add(value);
				}
			}
		}
		if(dataList.size()>0){
			for(final Map m : dataList){
				Button b = new Button(getContext());
				b.setBackgroundResource(R.drawable.map_btn_floor_bg);
				b.setPadding(0, 0, 0, 0);
				b.setGravity(Gravity.CENTER);
				b.setText(m.getMapNickname());
				b.setTextSize(20);
				b.setTextColor(getContext().getResources().getColorStateList(R.color.white_text));
				TextPaint tp = b.getPaint(); 
				tp.setFakeBoldText(true);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
				lp.topMargin=5;
				b.setLayoutParams(lp);
				if(m.getMapName().equals(map.getMapName())){
					b.setSelected(true);
				}
				b.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						topLl.setVisibility(GONE);
						floorsLl.setVisibility(INVISIBLE);
						buildingsLl.setVisibility(INVISIBLE);
						if(null!=onMapBottomWidgetClickListener){
							onMapBottomWidgetClickListener.onFloorsClick(m);
						}
					}
				});
				floorsLl.addView(b);
			}
		}
	}

	private void initBuilding() {
		buildingBtn.setText(MapCommonData.getInstance().getBuildingList().get(map.getMapName().split("_")[1]));
		buildingBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(topLl.getVisibility()==VISIBLE){
					topLl.setVisibility(GONE);
					floorsLl.setVisibility(INVISIBLE);
					buildingsLl.setVisibility(INVISIBLE);
				}else{
					topLl.setVisibility(VISIBLE);
					floorsLl.setVisibility(INVISIBLE);
					buildingsLl.setVisibility(VISIBLE);
				}
			}
		});
		buildingsLl.removeAllViews();
		HashMap<String, Map> dataList = new HashMap<String, Map>();
		HashMap<String, Map> temp = MapCommonData.getInstance().getMapHashMap();
		Iterator<?> iter = temp.entrySet().iterator();
		while (iter.hasNext()) {
			@SuppressWarnings("unchecked")
			java.util.Map.Entry<String, Map> entry = (java.util.Map.Entry<String, Map>) iter.next();
			Map value = entry.getValue();
			//判断项目
			if(MapCommonConstant.PROJECT_NAME.equals(value.getMapName().split("_")[0])){
				if("1".equals(value.getFloor())){
					dataList.put(value.getMapName().split("_")[1], value);
				}
			}
		}
		if(dataList.size()>0){
			for(final Map m : dataList.values()){
				Button b = new Button(getContext());
				b.setBackgroundResource(R.drawable.map_floorinfo_bg);
				b.setPadding(0, 0, 0, 0);
				b.setGravity(Gravity.CENTER);
				b.setText(MapCommonData.getInstance().getBuildingList().get(map.getMapName().split("_")[1]));
				b.setTextSize(20);
				b.setTextColor(getContext().getResources().getColorStateList(R.color.white_text));
				TextPaint tp = b.getPaint(); 
				tp.setFakeBoldText(true);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
				lp.topMargin=5;
				b.setLayoutParams(lp);
				if(m.getMapName().equals(map.getMapName())){
					b.setSelected(true);
				}
				b.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						topLl.setVisibility(GONE);
						floorsLl.setVisibility(INVISIBLE);
						buildingsLl.setVisibility(INVISIBLE);
						if(null!=onMapBottomWidgetClickListener){
							onMapBottomWidgetClickListener.onBuildingClick(m);
						}
					}
				});
				buildingsLl.addView(b);
			}
		}
	}

	public void setPositionable(boolean input){
		if(input){
			positionBtn.setVisibility(View.VISIBLE);
		}else{
			positionBtn.setVisibility(View.INVISIBLE);
		}
	}
	
	private void initPosition() {
		positionBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				topLl.setVisibility(GONE);
				floorsLl.setVisibility(INVISIBLE);
				buildingsLl.setVisibility(INVISIBLE);
				if(null!=onMapBottomWidgetClickListener){
					onMapBottomWidgetClickListener.onPositionClick(map);
				}
			}
		});
	}

	public void setOnMapBottomWidgetClickListener(OnMapBottomWidgetClickListener onMapBottomWidgetClickListener) {
		this.onMapBottomWidgetClickListener = onMapBottomWidgetClickListener;
	}

	public OnMapBottomWidgetClickListener getOnMapBottomWidgetClickListener() {
		return onMapBottomWidgetClickListener;
	}

	public interface OnMapBottomWidgetClickListener{
		public void onFloorsClick(Map map);
		public void onBuildingClick(Map map);
		public void onPositionClick(Map map);
	}
	
}
