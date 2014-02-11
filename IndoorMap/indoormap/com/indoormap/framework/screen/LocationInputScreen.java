package com.indoormap.framework.screen;

import java.util.HashMap;
import java.util.Iterator;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.indoormap.framework.common.MapCommonData;
import com.indoormap.framework.common.MapCommonUtil;
import com.indoormap.framework.element.LocationPointElement;
import com.indoormap.framework.layer.base.BaseLayerLayout;
import com.indoormap.framework.map.MapView;
import com.indoormap.framework.map.MapView.OnInvalidataListener;
import com.indoormap.framework.map.MapView.OnMapLoadCompletedListener;
import com.indoormap.framework.model.Location;
import com.indoormap.framework.model.Map;
import com.indoormap.framework.model.Point;
import com.indoormap.framework.screen.base.BaseMapScreen;
import com.indoormap.framework.widget.LocationInputDialog;
import com.indoormap.framework.widget.LocationInputDialog.OnLocationInputDialogClickListener;

public class LocationInputScreen extends BaseMapScreen {

	private Button editLocationBtn;

	private boolean isAddMode = false;

	private BaseLayerLayout locationLayerLayout;

	private LocationInputDialog locationInputDialog;
	
	public LocationInputScreen(Context context, Map map) {
		super(context, map);
	}

	protected void init() {
		super.init();
		isCanRotate = false;
	}

	protected void initMapElemnt() {
		mapView = new MapView(this, map);
		mapView.setOnMapLoadCompletedListener(new OnMapLoadCompletedListener() {

			@Override
			public void onMapLoadCompleted(Map map) {
				LocationInputScreen.super.map = map;
				initLocationLayerLayout();
				initEditLocationBtn();
			}
		});
		mapView.setOnInvalidataListener(new OnInvalidataListener() {
			
			@Override
			public void onInvalidata() {
				moveHook();
			}

			@Override
			public void onInvalidataEnd(int from) {
				
			}
		});
		addView(mapView);
	}

	@Override
	protected void initDialog() {

		super.initDialog();
		locationInputDialog = new LocationInputDialog(getContext());
		
		locationInputDialog.setOnLocationInputDialogClickListener(new OnLocationInputDialogClickListener() {
			
			@Override
			public void onSaveClick(Location location) {
				HashMap<String, Location> temp = MapCommonData.getInstance().getLocationHashMap().get(map.getMapName());
				if (temp == null) {
					temp = new HashMap<String, Location>();
					MapCommonData.getInstance().getLocationHashMap().put(map.getMapName(), temp);
				}
				temp.put(location.getName(), location);
				MapCommonUtil.saveLocationHashMap(map.getMapName());
				initLocationLayerLayout();
				locationInputDialog.cancel();
			}
			
			@Override
			public void onModifyClick(String oldName , Location location) {
				HashMap<String, Location> temp = MapCommonData.getInstance().getLocationHashMap().get(map.getMapName());
				if (temp == null) {
					temp = new HashMap<String, Location>();
					MapCommonData.getInstance().getLocationHashMap().put(map.getMapName(), temp);
				}
				temp.remove(oldName);
				temp.put(location.getName(), location);
				MapCommonUtil.saveLocationHashMap(map.getMapName());
				initLocationLayerLayout();
				locationInputDialog.cancel();
			}
			
			@Override
			public void onDeleteClick(Location location) {
				HashMap<String, Location> temp = MapCommonData.getInstance().getLocationHashMap().get(map.getMapName());
				if (temp == null) {
					temp = new HashMap<String, Location>();
					MapCommonData.getInstance().getLocationHashMap().put(map.getMapName(), temp);
				}
				temp.remove(location.getName());
				MapCommonUtil.saveLocationHashMap(map.getMapName());
				initLocationLayerLayout();
				locationInputDialog.cancel();
			}
			
			@Override
			public void onCancelClick() {
				locationInputDialog.cancel();
			}
		});
	}
	
//	private Point startP;
//	private Point endP;
//	private Point midP;
//	private LocationElement tempLocation;
	private void initLocationLayerLayout() {
		if (locationLayerLayout == null) {
			locationLayerLayout = new BaseLayerLayout(mapView);
			locationLayerLayout.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_UP) {
						float[] values = new float[9];
						mapView.getImgMatrix().getValues(values);
						Point p = new Point((event.getX() - values[2]) / values[0], (event.getY() - values[5]) / values[4], map);
						locationInputDialog.show();
						locationInputDialog.add(map, p);
					}
					return true;
				}
			});
			addView(locationLayerLayout);
		}
		locationLayerLayout.removeAllViews();
		HashMap<String, Location> temp = MapCommonData.getInstance().getLocationHashMap().get(map.getMapName());
		if (temp != null) {
			if (temp.size() > 0) {
				Iterator<?> iter = temp.entrySet().iterator();
				while (iter.hasNext()) {
					@SuppressWarnings("unchecked")
					java.util.Map.Entry<String, Location> entry = (java.util.Map.Entry<String, Location>) iter.next();
					Location value = entry.getValue();
					final LocationPointElement locationPointElement = new LocationPointElement(mapView, value);
					locationPointElement.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							locationInputDialog.show();
							locationInputDialog.modify(locationPointElement.getLocation());
						}
					});
					locationLayerLayout.addView(locationPointElement);
				}
			}
		}

		locationLayerLayout.update();
		
	}

	private void initEditLocationBtn() {
		if (editLocationBtn == null) {
			editLocationBtn = new Button(getContext());
			editLocationBtn.setText("新增打点");
			editLocationBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!isAddMode) {
						startAddLocation();
					} else {
						stopAddLocation();
					}
				}
			});
//			addView(editLocationBtn);
		}
	}

	private void startAddLocation() {
		isAddMode = true;
		editLocationBtn.setText("关闭新增打点");
	}

	private void stopAddLocation() {
		isAddMode = false;
		editLocationBtn.setText("新增打点");
	}

	@Override
	protected void moveHook() {
		if(locationLayerLayout != null){
			locationLayerLayout.update();
		}
		super.moveHook();
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (isAddMode) {
			return false;
		}
		return super.onInterceptTouchEvent(ev);
	}
}
