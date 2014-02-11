package com.indoormap.framework.screen;

import java.util.HashMap;
import java.util.Iterator;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

import com.indoormap.framework.common.MapCommonData;
import com.indoormap.framework.common.MapCommonUtil;
import com.indoormap.framework.element.POIElement;
import com.indoormap.framework.layer.POILayerLayout;
import com.indoormap.framework.model.Map;
import com.indoormap.framework.model.POI;
import com.indoormap.framework.model.Point;
import com.indoormap.framework.screen.base.BaseMapScreen;
import com.indoormap.framework.widget.POIInputDialog;
import com.indoormap.framework.widget.POIInputDialog.OnPOIInputDialogClick;

public class POIInputScreen extends BaseMapScreen {

	private POIInputDialog poiInputDialog;

	public POIInputScreen(Context context, Map map) {
		super(context, map);
	}

	@Override
	protected void init() {
		super.init();
		isCanRotate = false;
	}

	@Override
	protected void initDialog() {
		super.initDialog();
		poiInputDialog = new POIInputDialog(getContext());
		poiInputDialog.setOnPOIInputDialogClick(new OnPOIInputDialogClick() {

			@Override
			public void onSaveClick(POI poi) {
				HashMap<String, POI> temp = MapCommonData.getInstance().getPoiHashMap().get(map.getMapName());
				if (temp == null) {
					temp = new HashMap<String, POI>();
					MapCommonData.getInstance().getPoiHashMap().put(map.getMapName(), temp);
				}
				temp.put(poi.getId(), poi);
				MapCommonUtil.savePOIHashMap(map.getMapName());
				initPOILayerLayout();
				poiInputDialog.cancel();
			}

			@Override
			public void onCancelClick() {
				poiInputDialog.cancel();
			}

			@Override
			public void onModifyClick(String oldName ,POI poi) {
				HashMap<String, POI> temp = MapCommonData.getInstance().getPoiHashMap().get(map.getMapName());
				if (temp == null) {
					temp = new HashMap<String, POI>();
					MapCommonData.getInstance().getPoiHashMap().put(map.getMapName(), temp);
				}
				temp.remove(oldName);
				temp.put(poi.getId(), poi);
				MapCommonUtil.savePOIHashMap(map.getMapName());
				initPOILayerLayout();
				poiInputDialog.cancel();
			}

			@Override
			public void onDeleteClick(POI poi) {
				HashMap<String, POI> temp = MapCommonData.getInstance().getPoiHashMap().get(map.getMapName());
				if (temp == null) {
					temp = new HashMap<String, POI>();
					MapCommonData.getInstance().getPoiHashMap().put(map.getMapName(), temp);
				}
				temp.remove(poi.getId());
				MapCommonUtil.savePOIHashMap(map.getMapName());
				initPOILayerLayout();
				poiInputDialog.cancel();
			}
		});
	}



	@Override
	protected void initPOILayerLayout() {
		if (poiLayerLayout == null) {
			poiLayerLayout = new POILayerLayout(mapView);
			poiLayerLayout.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_UP) {
						float[] values = new float[9];
						mapView.getImgMatrix().getValues(values);
						Point p = new Point((event.getX() - values[2]) / values[0], (event.getY() - values[5]) / values[4], map);
						poiInputDialog.show();
						poiInputDialog.add(map, p);
					}
					return true;
				}
			});
			addView(poiLayerLayout);
		}
		poiLayerLayout.removeAllViews();

		HashMap<String, POI> temp = MapCommonData.getInstance().getPoiHashMap().get(map.getMapName());
		if (temp != null) {
			if (temp.size() > 0) {
				Iterator<?> iter = temp.entrySet().iterator();
				while (iter.hasNext()) {
					@SuppressWarnings("unchecked")
					java.util.Map.Entry<String, POI> entry = (java.util.Map.Entry<String, POI>) iter.next();
					POI value = entry.getValue();
					final POIElement poiElement = new POIElement(mapView, value);
					poiElement.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							poiInputDialog.show();
							poiInputDialog.modify(poiElement.getPOI());
						}
					});
					poiLayerLayout.addView(poiElement);
				}
			}
		}

		poiLayerLayout.update();
		poiLayerLayout.checkVisiable();
	}

}
