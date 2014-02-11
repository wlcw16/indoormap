package com.indoormap.framework.screen.base;

import java.util.HashMap;
import java.util.Iterator;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.indoormap.framework.common.MapCommonConstant;
import com.indoormap.framework.common.MapCommonData;
import com.indoormap.framework.common.MapCommonUtil;
import com.indoormap.framework.element.MyLocationElement;
import com.indoormap.framework.element.POIElement;
import com.indoormap.framework.layer.POILayerLayout;
import com.indoormap.framework.layer.base.BaseLayerLayout;
import com.indoormap.framework.logic.Position;
import com.indoormap.framework.map.MapView;
import com.indoormap.framework.map.MapView.OnInvalidataListener;
import com.indoormap.framework.map.MapView.OnMapLoadCompletedListener;
import com.indoormap.framework.model.Location;
import com.indoormap.framework.model.Map;
import com.indoormap.framework.model.POI;
import com.indoormap.framework.model.Point;
import com.indoormap.framework.widget.DefineProgressDialog;
import com.indoormap.framework.widget.MapBottomWidget;
import com.indoormap.framework.widget.MapBottomWidget.OnMapBottomWidgetClickListener;

public class BaseMapScreen extends RelativeLayout {

	protected MapView mapView;

	protected Map map;

	protected POILayerLayout poiLayerLayout;

	protected BaseLayerLayout myLocationLayerLayout;
	
	protected MapBottomWidget mapBottomWidget;
	
	protected MyLocationElement myLocationElement;
	
	protected boolean isCanRotate = true;
	
	protected boolean isCanOperate = true;
	
	protected boolean isCheckScale = true;
	
	protected DefineProgressDialog defineProgressDialog;
	
	protected Handler defineProgressDialogHandler;
	
	protected Handler positionHandler;
	
	protected String myLocationStr;

	protected final static int FROM_LOCATION = 0x0001;
	
	public BaseMapScreen(Context context, Map map) {
		super(context);
		this.map = map;
		init();
	}

	protected void init() {
//		WifiUtil.init(getContext());
//		ConnectivityUtil.init(getContext());
		setBackgroundColor(Color.WHITE);
		initDialog();
		initHandler();
		initMapElemnt();
	}

	protected void initDialog() {
		defineProgressDialog = new DefineProgressDialog(getContext());
	}
	
	protected void initHandler() {
		defineProgressDialogHandler = new Handler(){
			public void handleMessage(android.os.Message msg) {
				if(defineProgressDialog!=null){
					defineProgressDialog.cancel();
				}
			};
		};
		positionHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Bundle bundle = msg.getData();
				if (null != bundle) {
					String flag = bundle.getString(MapCommonConstant.FLAG);
					String mesg = bundle.getString(MapCommonConstant.MESSAGE);
					//
					if (Position.OK.equals(flag)) {
						myLocationStr = mesg;
						// 不在图书馆内
						Location curLocation = MapCommonUtil.getLocationFromData(myLocationStr);
						if(curLocation==null){
							//TODO
							Toast.makeText(getContext(), "您不在清华大学图书馆内", Toast.LENGTH_SHORT).show();
						}
						//不在同一栋楼内
						else if(!curLocation.getName().split("_")[1].equals(map.getMapName().split("_")[1])){
							Map changeMap = MapCommonData.getInstance().getMapHashMap().get(curLocation.getMapName());
							changeMap(changeMap);
							myLocationElement.setVisibility(VISIBLE);
							myLocationElement.setPoint(curLocation.getPoint());
							return;
						}
						//不在同一楼层内
						else if(!curLocation.getName().split("_")[2].equals(map.getMapName().split("_")[2])){
							Map changeMap = MapCommonData.getInstance().getMapHashMap().get(curLocation.getMapName());
							changeMap(changeMap);
							myLocationElement.setVisibility(VISIBLE);
							myLocationElement.setPoint(curLocation.getPoint());
							return;
							
						}
						//同楼层内
						else{
							myLocationElement.setVisibility(VISIBLE);
							myLocationElement.setPoint(curLocation.getPoint());
							mapView.moveToCenter(myLocationElement.getPoint(), FROM_LOCATION);
						}
					}
					// 超时
					else if (Position.ERROR.equals(flag)) {
						// 定位结束
						// TODO
						Toast.makeText(getContext(), mesg, Toast.LENGTH_SHORT).show();
					}
					defineProgressDialog.cancel();
				}
			}
		};
	}
	
	protected void initMapElemnt() {
		mapView = new MapView(this, map);
		defineProgressDialog.show();
		defineProgressDialog.updateMsg("正在加载地图...");
		mapView.setOnMapLoadCompletedListener(new OnMapLoadCompletedListener() {

			@Override
			public void onMapLoadCompleted(Map map) {
				BaseMapScreen.this.map = map;
				mapCompletedHook();
				checkPoiVisiable();
				isCanOperate = true;
			}
		});
		mapView.setOnInvalidataListener(new OnInvalidataListener() {
			
			@Override
			public void onInvalidata() {
				moveHook();
			}

			@Override
			public void onInvalidataEnd(int from) {
				onInvalidataEndHook(from);
			}
		});
		addView(mapView);
	}

	protected void onInvalidataEndHook(int from) {
		
		switch (from) {
		case -1:
//			upHook();
			break;
		case FROM_LOCATION:
			if(MapCommonUtil.getMapScale(mapView)<(1/1.1f)){
				mapView.scale(1, 0.1f, MapCommonUtil.getScreenWidth(getContext())/2f, MapCommonUtil.getScreenHeight(getContext())/2f);
			}
			break;
		}
	}
	
	protected void initMyLocationElement() {
		if (myLocationLayerLayout == null) {
			myLocationLayerLayout = new BaseLayerLayout(mapView);
			addView(myLocationLayerLayout);
		}
		myLocationLayerLayout.removeAllViews();
		
		if(null==myLocationElement){
			myLocationElement = new MyLocationElement(mapView, new Point(0, 0, map));
			myLocationElement.setVisibility(INVISIBLE);
		}
		
		if(myLocationElement.getParent()==null){
			myLocationLayerLayout.addView(myLocationElement);
		}
		
		Location l = MapCommonUtil.getLocationFromData(myLocationStr);
		if(l==null){
			myLocationElement.setVisibility(INVISIBLE);
		}else{
			if(l.getMapName().equals(map.getMapName())){
				myLocationElement.setVisibility(VISIBLE);
			}else{
				myLocationElement.setVisibility(INVISIBLE);
			}
		}
		myLocationElement.update();
	}
	
	protected void initMapWidget() {
		if(mapBottomWidget == null){
			mapBottomWidget = new MapBottomWidget(getContext());
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
			lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			mapBottomWidget.setLayoutParams(lp);
			mapBottomWidget.setOnMapBottomWidgetClickListener(new OnMapBottomWidgetClickListener() {
				
				@Override
				public void onPositionClick(Map map) {
					defineProgressDialog.show();
					defineProgressDialog.updateMsg("定位中...");
					Position.getCurrentLocationStr(positionHandler);
				}
				
				@Override
				public void onFloorsClick(Map map) {
					changeMap(map);
				}
				
				@Override
				public void onBuildingClick(Map map) {
					changeMap(map);
				}
			});
			addView(mapBottomWidget);
		}
		mapBottomWidget.init(map);
	}
	
	protected void mapCompletedHook(){
		initPOILayerLayout();
		initMyLocationElement();
		initMapWidget();
		defineProgressDialog.cancel();
		
	}
	
	protected void initPOILayerLayout() {
		if (poiLayerLayout == null) {
			poiLayerLayout = new POILayerLayout(mapView);
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
						}
					});
					poiLayerLayout.addView(poiElement);
				}
			}
		}
		poiLayerLayout.update();
	}

	

	
	public void changeMap(Map map){
		defineProgressDialog.show();
		defineProgressDialog.updateMsg("地图切换中...");
		isCanOperate = false;
		mapView.changeMap(map);
	}
	
	protected void pinchHook() {

	}

	protected void tapHook() {
		
	}
	
	protected void moveHook() {
		if (poiLayerLayout != null) {
			poiLayerLayout.update();
		}
		if(myLocationLayerLayout!=null){
			myLocationLayerLayout.update();
		}
	}

	protected void upHook() {
		float[] values2 = new float[9];
		mapView.getImgMatrix().getValues(values2);
		float currentScale = (float) Math.sqrt(values2[3] * values2[3] + values2[4] * values2[4]);
		
		
		if (currentScale <= mapView.getMaxRate() && currentScale > 0.7) {
			mapView.setMapPath(MapCommonConstant.HIGH);
		} else if (currentScale <= 0.7 && currentScale > 0.4) {
			mapView.setMapPath(MapCommonConstant.MIDDLE);
		} else if (currentScale <= 0.4) {
			mapView.setMapPath(MapCommonConstant.LOW);
		}
		
		if(isCheckScale){
			if(currentScale-1>0.01||mapView.getMinRate()-currentScale>0.01){
				float postV = 0;
				float scale = 1;
				if(currentScale>1){
					postV = -0.03f;
					scale = 1;
				}
				if(currentScale<mapView.getMinRate()){
					postV = 0.03f;
					scale = mapView.getMinRate();
				}
				mapView.scale(scale, postV, mid.x, mid.y);
			}
		}
		
		if (poiLayerLayout != null) {
			poiLayerLayout.checkVisiable();
		}if(myLocationElement!=null){
			myLocationElement.update();
		}
	}

	protected static final int NONE = 0;
	protected static final int DRAG = 1;
	protected static final int ZOOM = 2;
	protected int mode = NONE;
	protected boolean mIsBeingDragged = false;
	protected int mTouchSlop = 3;
	protected Matrix savedMatrix = new Matrix();
	protected Matrix tempMatrix = new Matrix();
	protected PointF mid = new PointF();
	protected PointF oldMid = new PointF();
	protected float oldRotation = 0;
	protected float mainPointerLastEventX;
	protected float mainPointerLastEventY;
	protected float secondPointerLastEventX;
	protected float secondPointerLastEventY;
	protected float originalDistance;
	protected float originalScale;
	protected float scaleCenterX;
	protected float scaleCenterY;
	protected float scaleStartX;
	protected float scaleStartY;
	protected int activePointerId;
	protected int rimSize = 100;

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if(getCanOperate()){
			float x = ev.getX();
			float y = MapCommonUtil.getInterceptEventY(getContext(), ev.getY());
			final int action = ev.getActionMasked();
			int index = ev.getActionIndex();
			int pointerId = ev.getPointerId(index);
			if ((action == MotionEvent.ACTION_MOVE) && (mIsBeingDragged)) {
				return true;
			}
			switch (action & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_MOVE: {
				if (ev.getPointerCount() > 1) {
					mIsBeingDragged = true;
				}
				final float xDiff = Math.abs(x - mainPointerLastEventX);
				final float yDiff = Math.abs(y - mainPointerLastEventY);
				if (yDiff > mTouchSlop || xDiff > mTouchSlop) {
					mIsBeingDragged = true;
					mainPointerLastEventY = y;
					mainPointerLastEventX = x;
					final ViewParent parent = getParent();
					if (parent != null) {
						parent.requestDisallowInterceptTouchEvent(true);
					}
				}
				break;
			}
			case MotionEvent.ACTION_DOWN: {
				mIsBeingDragged = false;
				mode = DRAG;
				savedMatrix.set(mapView.getImgMatrix());
				mainPointerLastEventX = x;
				mainPointerLastEventY = y;
				activePointerId = pointerId;
				break;
			}

			case MotionEvent.ACTION_POINTER_DOWN:
				mode = ZOOM;
				mIsBeingDragged = true;
				savedMatrix.set(mapView.getImgMatrix());
				mainPointerLastEventX = ev.getX(0);
				mainPointerLastEventY = MapCommonUtil.getInterceptEventY(getContext(), ev.getY(0));
				x = ev.getX(1);
				y = MapCommonUtil.getInterceptEventY(getContext(), ev.getY(1));
				secondPointerLastEventX = x;
				secondPointerLastEventY = y;
				float[] values = new float[9];
				mapView.getImgMatrix().getValues(values);
				originalScale = values[0];
				originalDistance = MapCommonUtil.getDistanceBetweenPoint(mainPointerLastEventX, mainPointerLastEventY, x, y);
				scaleCenterX = (mainPointerLastEventX + x) / 2f;
				scaleCenterY = (mainPointerLastEventY + y) / 2f;
				scaleStartX = values[2];
				scaleStartY = values[5];
				oldRotation = MapCommonUtil.rotation(ev);
				MapCommonUtil.midPointIntercepte(getContext(),mid, ev);
				MapCommonUtil.midPointIntercepte(getContext(),oldMid, ev);
				break;
			case MotionEvent.ACTION_UP:
				mode = NONE;
				mIsBeingDragged = false;
				break;
			}
		}
		return mIsBeingDragged;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(getCanOperate()){
			float x = event.getX();
			float y = event.getY();
			int index = event.getActionIndex();
			int pointerId = event.getPointerId(index);
			// int pointerCount = event.getPointerCount();
			switch (event.getActionMasked()) {
			case MotionEvent.ACTION_DOWN:
				mode = DRAG;
				savedMatrix.set(mapView.getImgMatrix());
				mainPointerLastEventX = x;
				mainPointerLastEventY = y;
				activePointerId = pointerId;
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				mode = ZOOM;
				savedMatrix.set(mapView.getImgMatrix());
				mainPointerLastEventX = event.getX(0);
				mainPointerLastEventY = event.getY(0);
				x = event.getX(1);
				y = event.getY(1);
				secondPointerLastEventX = x;
				secondPointerLastEventY = y;
				float[] values = new float[9];
				mapView.getImgMatrix().getValues(values);
				originalScale = values[0];
				originalDistance = MapCommonUtil.getDistanceBetweenPoint(mainPointerLastEventX, mainPointerLastEventY, x, y);
				scaleCenterX = (mainPointerLastEventX + x) / 2f;
				scaleCenterY = (mainPointerLastEventY + y) / 2f;
				scaleStartX = values[2];
				scaleStartY = values[5];
				oldRotation = MapCommonUtil.rotation(event);
				MapCommonUtil.midPoint(mid, event);
				MapCommonUtil.midPoint(oldMid, event);

				break;
			case MotionEvent.ACTION_MOVE:
				// poiLayerLayout.setVisibility(INVISIBLE);
				if (mode == ZOOM) {

					tempMatrix.set(savedMatrix);
					MapCommonUtil.midPoint(mid, event);
					float currentDistance = MapCommonUtil.spacing(event);
					float currentScale = currentDistance / originalDistance;
					tempMatrix.postScale(currentScale, currentScale, mid.x, mid.y);

					if(isCanRotate){
						float rotation = MapCommonUtil.rotation(event) - oldRotation;
						tempMatrix.postRotate(rotation, mid.x, mid.y);
					}
				
					tempMatrix.postTranslate((mid.x - oldMid.x) * currentScale, (mid.y - oldMid.y) * currentScale);

					mapView.getImgMatrix().set(tempMatrix);

//					Log.i(this.getClass().getSimpleName(), mapView.getImgMatrix().toShortString());
					
					mainPointerLastEventX = event.getX(0);
					mainPointerLastEventY = event.getY(0);
					secondPointerLastEventX = event.getX(1);
					secondPointerLastEventY = event.getY(1);
				} else if (mode == DRAG) {
					if (index == 0 && pointerId == activePointerId) {

						tempMatrix.set(savedMatrix);
						tempMatrix.postTranslate(event.getX() - mainPointerLastEventX, event.getY() - mainPointerLastEventY);
//						Log.i(this.getClass().getSimpleName(), mapView.getImgMatrix().toShortString());			
						mapView.getImgMatrix().set(tempMatrix);
					}
				}
				moveHook();
//				Log.i(this.getClass().getSimpleName(), mapView.getImgMatrix().toString());
				mapView.postInvalidate();
				break;

			case MotionEvent.ACTION_UP:
				if(Math.abs(event.getX(0)-mainPointerLastEventX)<mTouchSlop&&Math.abs(event.getY(0)-mainPointerLastEventY)<mTouchSlop){
					tapHook();
				}
			case MotionEvent.ACTION_POINTER_UP:
				mode = NONE;
				isCanOperate = true;
				upHook();
				break;
			}
		}
		return super.onTouchEvent(event);
	}
	
	protected boolean getCanOperate() {
		return isCanOperate;
	}
	
	protected void mainPointDownHook() {
		
	}
	protected void otherPointDownHook() {
		
	}
	
	public void checkPoiVisiable(){
		upHook();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			if(mapView.isNaviMode()){
				mapView.endNaviMode();
				return true;
			}
		}
		return false;
	}
	
}
