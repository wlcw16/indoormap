package com.indoormap.framework.screen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.indoormap.framework.common.MapCommonData;
import com.indoormap.framework.common.MapCommonUtil;
import com.indoormap.framework.element.LocationPointElement;
import com.indoormap.framework.element.ZoneElement;
import com.indoormap.framework.layer.base.BaseLayerLayout;
import com.indoormap.framework.model.Location;
import com.indoormap.framework.model.Map;
import com.indoormap.framework.model.Point;
import com.indoormap.framework.model.Zone;
import com.indoormap.framework.screen.base.BaseMapScreen;
import com.indoormap.framework.widget.ZoneEditDialog;
import com.indoormap.framework.widget.ZoneEditDialog.OnZoneEditDialogClickListener;

public class ZoneInputScreen extends BaseMapScreen {

	protected ZoneElement zoneElement;

	protected Button addBtn;

	protected Button confirmBtn;

	protected Button cancelBtn;
	private Button locationBtn;
	private TextView curZoneTv;

	private BaseLayerLayout locationLayerLayout;
	protected List<Point> addZoneList = new ArrayList<Point>();

	protected HashMap<String, Zone> zoneList = new HashMap<String, Zone>();

	protected boolean isAddMode = false;
	protected boolean isAddLocation = false;
	protected boolean isAddDisplay = false;
	protected ZoneEditDialog zoneEditDialog;
	protected Zone curZone;

	public ZoneInputScreen(Context context, Map map) {
		super(context, map);
	}

	@Override
	protected void init() {
		super.init();
		isCanRotate = false;
		isCheckScale = false;
	}

	@Override
	protected void mapCompletedHook() {
		initData();
		initPOILayerLayout();
		initZoneElement();
		initLocationLayerLayout();
		initMapWidget();
		initBtns();
		defineProgressDialog.cancel();
	}

	@Override
	protected void initDialog() {
		super.initDialog();
		zoneEditDialog = new ZoneEditDialog(getContext());
		zoneEditDialog.setZoneEditDialogClickListener(new OnZoneEditDialogClickListener() {
			
			@Override
			public void onModifyClick(Zone zone) {
				MapCommonUtil.saveZoneHashMap(map.getMapName());
			}
			
			@Override
			public void onDeleteClick(Zone zone) {
				zoneList.remove(zone.getId());
				MapCommonUtil.saveZoneHashMap(map.getMapName());
				curZone = null;
				zoneElement.setCurZone(curZone);
				curZoneTv.setText("当前选择区域:null");
			}
		});
	}

	private void initData() {
		zoneList = MapCommonData.getInstance().getZoneHashMap().get(map.getMapName());
		if (zoneList == null) {
			zoneList = new HashMap<String, Zone>();
		}
	}

	@Override
	protected void initMapWidget() {
		super.initMapWidget();
		mapBottomWidget.setPositionable(false);
	}

	protected void initZoneElement() {
		if (zoneElement == null) {
			zoneElement = new ZoneElement(mapView, new Point(0, 0, map));
			zoneElement.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (isAddMode) {
						if (event.getAction() == MotionEvent.ACTION_UP) {
							Point p = MapCommonUtil.event2Point(mapView, event);
							addZoneList.add(p);
							zoneElement.addZonePoint(p);
							return true;
						}
					} else if(isAddDisplay){
						if (event.getAction() == MotionEvent.ACTION_UP) {
							Point p = MapCommonUtil.event2Point(mapView, event);
							if(curZone.getDisplayList()==null){
								curZone.setDisplayList(new ArrayList<Point>());
							}
							curZone.getDisplayList().add(p);
							zoneElement.postInvalidate();
							return true;
						}
					}else {
						if (event.getAction() == MotionEvent.ACTION_UP) {
							Point p = MapCommonUtil.event2Point(mapView, event);
							for (Zone z : zoneList.values()) {
								if (z.isInside(p)) {
									curZone = z;
									zoneElement.setCurZone(curZone);
									curZoneTv.setText("当前选择区域:" + curZone.getId());
									break;
								}
							}
						}
					}
					return true;
				}
			});
			zoneElement.setAddMode(true);
			addView(zoneElement);
		}
		zoneElement.init(mapView);
	}

	LinearLayout mainLl;
	LinearLayout addZoneLl;
	LinearLayout editZoneLl;
	Button addLocationPointBtn;
	Button addDisplayPointBtn;
	Button editCurZoneBtn;

	private void initBtns() {
		if (null == mainLl) {
			mainLl = new LinearLayout(getContext());
			mainLl.setOrientation(LinearLayout.VERTICAL);
		}

		if (null == addZoneLl) {
			addZoneLl = new LinearLayout(getContext());
		}

		if (addBtn == null) {
			addBtn = new Button(getContext());
			addBtn.setText("新增");
			addBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (isAddMode) {
						isAddMode = false;
						isCanOperate = true;
						addBtn.setText("新增");
						confirmBtn.setVisibility(View.GONE);
						cancelBtn.setVisibility(View.GONE);
						curZoneTv.setVisibility(View.VISIBLE);
						editZoneLl.setVisibility(VISIBLE);
					} else {
						isAddMode = true;
						isCanOperate = false;
						addBtn.setText("关闭");
						confirmBtn.setVisibility(View.VISIBLE);
						cancelBtn.setVisibility(View.VISIBLE);
						curZoneTv.setVisibility(View.GONE);
						editZoneLl.setVisibility(GONE);
					}
				}
			});
			addZoneLl.addView(addBtn);
		}

		if (null == confirmBtn) {
			confirmBtn = new Button(getContext());
			confirmBtn.setText("确认");
			confirmBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (addZoneList.size() > 2) {
						Zone zone = new Zone();
						zone.setEdgeList(addZoneList);
						zone.setMapName(map.getMapName());
						zone.setBuildingName(map.getBuildId());
						zone.setFloor(map.getFloor());
						zone.setId(map.getMapName() + "_" + System.currentTimeMillis());
						zoneList.put(zone.getId(), zone);
						zoneElement.addZone(zone);
						addZoneList = new ArrayList<Point>();
						zoneElement.clearAddList();
						HashMap<String, Zone> temp = MapCommonData.getInstance().getZoneHashMap().get(map.getMapName());
						if (temp == null) {
							temp = new HashMap<String, Zone>();
							MapCommonData.getInstance().getZoneHashMap().put(map.getMapName(), zoneList);
						} else {
							MapCommonData.getInstance().getZoneHashMap().put(map.getMapName(), zoneList);
						}
						MapCommonUtil.saveZoneHashMap(map.getMapName());
					}
				}
			});
			addZoneLl.addView(confirmBtn);
			confirmBtn.setVisibility(View.GONE);
		}
		if (null == cancelBtn) {
			cancelBtn = new Button(getContext());
			cancelBtn.setText("取消");
			cancelBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					addZoneList = new ArrayList<Point>();
					zoneElement.clearAddList();
				}
			});
			addZoneLl.addView(cancelBtn);
			cancelBtn.setVisibility(View.GONE);
		}

		if (locationBtn == null) {
			locationBtn = new Button(getContext());
			locationBtn.setText("打点");
			locationBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (locationLayerLayout.getVisibility() == VISIBLE) {
						locationLayerLayout.setVisibility(GONE);
					} else {
						locationLayerLayout.setVisibility(VISIBLE);
						locationLayerLayout.update();
					}
				}
			});
			addZoneLl.addView(locationBtn);
		}

		if (null == addZoneLl.getParent()) {
			mainLl.addView(addZoneLl);
		}
		if (curZoneTv == null) {
			curZoneTv = new TextView(getContext());
			curZoneTv.setTextColor(Color.BLACK);
			mainLl.addView(curZoneTv);
		}
		curZoneTv.setText("当前选择区域:null");

		if (null == editZoneLl) {
			editZoneLl = new LinearLayout(getContext());
		}
		if (null == editZoneLl.getParent()) {
			addZoneLl.addView(editZoneLl);
		}

		if (addLocationPointBtn == null) {
			addLocationPointBtn = new Button(getContext());
			addLocationPointBtn.setText("增加打点");
			addLocationPointBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (curZone != null) {
						if (!isAddLocation) {
							isAddLocation = true;
							addLocationPointBtn.setText("关闭");
							isAddDisplay = false;
							addDisplayPointBtn.setText("增加显示点");
						} else {
							isAddLocation = false;
							addLocationPointBtn.setText("增加打点");
						}
					} else {
						Toast.makeText(getContext(), "当前选择区域为空", Toast.LENGTH_SHORT).show();
					}

				}
			});
			editZoneLl.addView(addLocationPointBtn);
		}
		if (addDisplayPointBtn == null) {
			addDisplayPointBtn = new Button(getContext());
			addDisplayPointBtn.setText("增加显示点");
			addDisplayPointBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (curZone != null) {
						if (!isAddDisplay) {
							isAddDisplay = true;
							addDisplayPointBtn.setText("关闭");
							isAddLocation = false;
							addLocationPointBtn.setText("增加打点");
						} else {
							isAddDisplay = false;
							addDisplayPointBtn.setText("增加显示点");
						}
					} else {
						Toast.makeText(getContext(), "当前选择区域为空", Toast.LENGTH_SHORT).show();
					}
				}
			});
			editZoneLl.addView(addDisplayPointBtn);
		}
		if (editCurZoneBtn == null) {
			editCurZoneBtn = new Button(getContext());
			editCurZoneBtn.setText("编辑当前区域");
			editCurZoneBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (curZone != null) {

						zoneEditDialog.show();
						zoneEditDialog.modify(curZone);
					} else {
						Toast.makeText(getContext(), "当前选择区域为空", Toast.LENGTH_SHORT).show();
					}
				}
			});
			editZoneLl.addView(editCurZoneBtn);
		}
		if (null == mainLl.getParent()) {
			addView(mainLl);
		}
	}

	private void initLocationLayerLayout() {
		if (locationLayerLayout == null) {
			locationLayerLayout = new BaseLayerLayout(mapView);
			addView(locationLayerLayout);
			locationLayerLayout.setVisibility(View.GONE);
		}
		locationLayerLayout.removeAllViews();
		HashMap<String, Location> temp = MapCommonData.getInstance().getLocationHashMap().get(map.getMapName());
		if (temp != null) {
			if (temp.size() > 0) {
				Iterator<?> iter = temp.entrySet().iterator();
				while (iter.hasNext()) {
					@SuppressWarnings("unchecked")
					java.util.Map.Entry<String, Location> entry = (java.util.Map.Entry<String, Location>) iter.next();
					final Location value = entry.getValue();
					final LocationPointElement locationPointElement = new LocationPointElement(mapView, value);
					locationPointElement.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							if (isAddLocation) {
								HashMap<String, Location> temp = curZone.getLocationList();
								if (temp == null) {
									temp = new HashMap<String, Location>();
									curZone.setLocationList(temp);
								}
								temp.put(locationPointElement.getLocation().getName(), locationPointElement.getLocation());
								Toast.makeText(getContext(), "已增加"+value.getName(), Toast.LENGTH_SHORT).show();
							}
						}
					});
					locationLayerLayout.addView(locationPointElement);
				}
			}
		}

		locationLayerLayout.update();

	}

	@Override
	protected void moveHook() {
		super.moveHook();
		if (zoneElement != null) {
			zoneElement.update();
		}
		if (locationLayerLayout != null && locationLayerLayout.getVisibility() == VISIBLE) {
			locationLayerLayout.update();
		}
	}

}
