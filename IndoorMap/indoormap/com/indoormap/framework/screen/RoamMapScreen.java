package com.indoormap.framework.screen;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.indoormap.framework.common.MapCommonData;
import com.indoormap.framework.common.MapCommonUtil;
import com.indoormap.framework.element.DynamicPOIElement;
import com.indoormap.framework.element.RoutePathElement;
import com.indoormap.framework.layer.base.BaseLayerLayout;
import com.indoormap.framework.logic.RouteGenerateLogic;
import com.indoormap.framework.model.DynamicPOI;
import com.indoormap.framework.model.Location;
import com.indoormap.framework.model.Map;
import com.indoormap.framework.model.Point;
import com.indoormap.framework.screen.base.BaseMapScreen;
import com.indoormap.framework.util.UdpUtil;
import com.indoormap.framework.widget.AttentionRelativeLayout;
import com.indoormap.framework.widget.AttentionRelativeLayout.AttentionRelativeLayoutListener;

public class RoamMapScreen extends BaseMapScreen {

	private String dynamicIP = "42.120.52.246";
	private int dynamicPort = 10008;

	private Handler dynamicPOIHandler;

	private BaseLayerLayout dynamicPoiLayerLayout;

	private HashMap<String, DynamicPOIElement> dynamicPOIElementHashMap = new HashMap<String, DynamicPOIElement>();

	private final static int FROM_DYNAMICPOI_CLICK = 0x0005;

	private AttentionRelativeLayout dynamicAttentionRelativeLayout;

	private DynamicPOI currentClickDynamicPOI;
	
	RoutePathElement rpe;
	
	private Handler dynamicFlyInHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
//			curDynamicPOIElement.startFlyInAnimation();
			if(msg.getData()!=null){
				String id = msg.getData().getString("curDynamicPOIElement");
				DynamicPOIElement dpe = dynamicPOIElementHashMap.get(id);
				dpe.setVisibility(View.VISIBLE);
				if(null==dpe.getParent()){
					dynamicPoiLayerLayout.addView(dpe);
				}
				dpe.startFlyInAnimation();
			}
		};
	};
	
	public RoamMapScreen(Context context, Map map) {
		super(context, map);
	}

	@Override
	protected void init() {
		super.init();
	}

	@Override
	protected void initHandler() {
		super.initHandler();
		dynamicPOIHandler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				defineProgressDialog.cancel();
				initDynamicPOIElements();
			};
		};
	}

	@Override
	protected void mapCompletedHook() {
		initPOILayerLayout();
		initMyLocationElement();
		initMapWidget();
//		defineProgressDialog.cancel();
		updateDynamicPOI();
		initDynamicPOILayerLayout();
	}

	@Override
	protected void onInvalidataEndHook(int from) {
		super.onInvalidataEndHook(from);
		switch (from) {
		case FROM_DYNAMICPOI_CLICK:
			if (dynamicAttentionRelativeLayout == null) {
				dynamicAttentionRelativeLayout = new AttentionRelativeLayout(getContext());
				dynamicAttentionRelativeLayout.setAttentionRelativeLayoutListener(new AttentionRelativeLayoutListener() {
					
					@Override
					public void onDestroy() {
						isCanOperate=true;
					}

					@Override
					public void onNaviClick(DynamicPOI dynamicPOI) {
//						Point startP = new Point(1088, 1000, map);
//						Point endP = new Point(592, 1528, map);
						if(myLocationElement.getVisibility()==VISIBLE){
							Point startP = myLocationElement.getPoint();
							Point endP = new Point(dynamicPOI.getX(), dynamicPOI.getY(), map);
							List<Point> path = RouteGenerateLogic.generateRoute(null, map, startP, endP);
							mapView.startNaviMode(path);
							mapView.invalidate();
							dynamicAttentionRelativeLayout.destroy();
						}else{
							Location curLocation = MapCommonUtil.getLocationFromData(myLocationStr);
							if(curLocation!=null){
								if(!curLocation.getMapName().equals(map.getMapName())){
									Map curMap = MapCommonData.getInstance().getMapHashMap().get(curLocation.getMapName());
									Toast.makeText(getContext(), "您现在在"+curMap.getFloor()+"层，请您先前往"+map.getFloor()+"层，再进行导航", Toast.LENGTH_SHORT).show();
								}
							}else{
								Toast.makeText(getContext(), "请先定位", Toast.LENGTH_SHORT).show();
							}
						}
					}
				});
			}
			dynamicAttentionRelativeLayout.show(this, currentClickDynamicPOI,myLocationStr);
			dynamicAttentionRelativeLayout.startAttention();
			isCanOperate=false;
			if(MapCommonUtil.getMapScale(mapView)<(1/1.1f)){
				mapView.scale(1, 0.1f, MapCommonUtil.getScreenWidth(getContext())/2f, MapCommonUtil.getScreenHeight(getContext())/2f);
			}
			break;
			
		}
	}

	private void initDynamicPOILayerLayout() {
		if (dynamicPoiLayerLayout == null) {
			dynamicPoiLayerLayout = new BaseLayerLayout(mapView);
			addView(dynamicPoiLayerLayout);
		}
		dynamicPoiLayerLayout.removeAllViews();
		dynamicPoiLayerLayout.update();
	}

	private int maxIndex = 0;
	private DynamicPOIElement curDynamicPOIElement;
	private void initDynamicPOIElements() {
		maxIndex = 0;
		dynamicPOIElementHashMap = new HashMap<String, DynamicPOIElement>();
		HashMap<String, DynamicPOI> temp = MapCommonData.getInstance().getDynamicPoiHashMap().get(map.getMapName());
		if (temp != null && temp.size() > 0) {
			int i = 1;
			Iterator<?> iter = temp.entrySet().iterator();
			while (iter.hasNext()) {
				@SuppressWarnings("unchecked")
				java.util.Map.Entry<String, DynamicPOI> entry = (java.util.Map.Entry<String, DynamicPOI>) iter.next();
				String key = entry.getKey();
				DynamicPOI value = entry.getValue();
				if(null!=value.getState()&&"1".equals(value.getState())){
					//记录最大的index，以便顺序弹出
					value.setPopupIndex(String.valueOf(i));
					i++;
					int index = Integer.valueOf(value.getPopupIndex());
					if(index>maxIndex){
						maxIndex = index;
					}
					final DynamicPOIElement dpe = new DynamicPOIElement(mapView, value);
					dpe.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							mapView.moveToCenter(dpe.getPoint(), FROM_DYNAMICPOI_CLICK);
							currentClickDynamicPOI = dpe.getDynamicPOI();
						}
					});
					dpe.setVisibility(View.INVISIBLE);
					dynamicPoiLayerLayout.addView(dpe);
					dynamicPOIElementHashMap.put(key, dpe);
				}
				
			}
		}
		
		//开始执行顺序弹出dynamicPOI
		new Thread(){
			public void run() {
				for(int i = 0; i<=maxIndex;i++){
					try {
						//每隔300ms弹出一个poi
						sleep(300);
						Iterator<?> iter = dynamicPOIElementHashMap.entrySet().iterator();
						while (iter.hasNext()) {
							@SuppressWarnings("unchecked")
							java.util.Map.Entry<String, DynamicPOIElement> entry = (java.util.Map.Entry<String, DynamicPOIElement>) iter.next();
							DynamicPOIElement value = entry.getValue();
							if(String.valueOf(i).equals(value.getDynamicPOI().getPopupIndex())){
								curDynamicPOIElement = value;
//								dynamicFlyInHandler.sendEmptyMessage(0);
								Message msg = Message.obtain();
								Bundle b = new Bundle();
								b.putString("curDynamicPOIElement", value.getDynamicPOI().getId());
								msg.setData(b);
								dynamicFlyInHandler.sendMessage(msg);
								break;
							}
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
		
	}

	private void updateDynamicPOI() {
		defineProgressDialog.updateMsg("正在更新漫游数据...");
		new Thread() {
			public void run() {
				HashMap<String, DynamicPOI> hashMap = MapCommonData.getInstance().getDynamicPoiHashMap().get(map.getMapName());
				String message = "";
				// 无数据
				if (hashMap == null || hashMap.size() < 1) {
					hashMap = new HashMap<String, DynamicPOI>();
					MapCommonData.getInstance().getDynamicPoiHashMap().put(map.getMapName(), hashMap);
					message = "01@" + map.getMapName() + "@0";

				}
				// 有数据
				else {
					long timestamp = 0;
					for (DynamicPOI dynamicPOI : hashMap.values()) {
						if (dynamicPOI.getTimestamp() > timestamp) {
							timestamp = dynamicPOI.getTimestamp();
						}
					}
					message = "01@" + map.getMapName() + "@" + timestamp;
				}
				String dynamicPOIStrings = "";
				String respone = UdpUtil.send(dynamicIP, dynamicPort, message);
				if (respone != null) {
					if (respone.indexOf("@") != -1) {
						String resultCode = respone.split("@")[0];
						if ("02".equals(resultCode)) {
							String[] pois = respone.split("@")[1].split("#");
							for(String poi : pois){
								respone = "02@"+poi;
								dynamicPOIStrings = UdpUtil.send(dynamicIP, dynamicPort, respone);
								if (null!=dynamicPOIStrings&&!"".equals(dynamicPOIStrings)) {
									String[] temps = dynamicPOIStrings.split("@");
									for (String input : temps) {
										DynamicPOI dynamicPOI = new DynamicPOI();
										dynamicPOI.decode(input);
										hashMap.put(dynamicPOI.getId(), dynamicPOI);
									}
									MapCommonUtil.saveDynamicHashMap(map.getMapName());
								}
							}
						}
					}
				}

				dynamicPOIHandler.sendEmptyMessage(0);
			};
		}.start();

	}

	@Override
	protected void tapHook() {
		super.tapHook();
	}

	@Override
	protected void moveHook() {
		super.moveHook();
		mapView.updateNaviMode();
		if (dynamicPoiLayerLayout != null) {
			dynamicPoiLayerLayout.update();
		}
	}
}
