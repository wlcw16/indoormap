package com.indoormap.framework.screen;

import java.util.HashMap;
import java.util.Iterator;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.indoormap.framework.common.MapCommonData;
import com.indoormap.framework.common.MapCommonUtil;
import com.indoormap.framework.element.LocationPointElement;
import com.indoormap.framework.element.MatrixElement;
import com.indoormap.framework.layer.base.BaseLayerLayout;
import com.indoormap.framework.model.Location;
import com.indoormap.framework.model.Map;
import com.indoormap.framework.model.Point;
import com.indoormap.framework.screen.base.BaseMapScreen;

public class MapMartrixInputScreen extends BaseMapScreen {

	private BaseLayerLayout matrixLayerLayout;

	private Button saveBtn;
	protected int mode = 0;
	private Button leftUpBtn;
	private Button leftDwonBtn;
	private Button rightUpBtn;
	private Button rightDownBtn;
	private Button allBtn;
	private Button locationBtn;
	private int xStart = 0;
	private int xEnd = 0;
	private int yStart = 0;
	private int yEnd = 0;
	public MapMartrixInputScreen(Context context, Map map) {
		super(context, map);
	}

	@Override
	protected void init() {
		super.init();
		isCanRotate = false;
		isCheckScale = false;
		xEnd = map.getMapMatrix()[0].length;
		yEnd = map.getMapMatrix().length;
	}

	@Override
	protected void mapCompletedHook() {
		defineProgressDialog.cancel();
		initPOILayerLayout();
		initMatrixLayerLayout();
		initLocationLayerLayout();
		initSaveBtn();
		initMapWidget();
	}

	private void initMatrixLayerLayout() {
		if (matrixLayerLayout == null) {
			matrixLayerLayout = new BaseLayerLayout(mapView);
			matrixLayerLayout.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_UP) {
						float[] values = new float[9];
						mapView.getImgMatrix().getValues(values);
						Point p = new Point((event.getX() - values[2]) / values[0], (event.getY() - values[5]) / values[4], map);
						
						if((p.getMatrixX()>=xStart&&p.getMatrixX()<xEnd)&&(p.getMatrixY()>=yStart&&p.getMatrixY()<yEnd)){
							int heng = p.getMatrixX()-xStart;
							int shu = p.getMatrixY()-yStart;
							int childIndex = shu * (xEnd-xStart) + heng;
							View child = matrixLayerLayout.getChildAt(childIndex);
							child.setVisibility(View.VISIBLE);
							(map.getMapMatrix())[p.getMatrixY()][p.getMatrixX()] = 0;
						}
						
//						int childIndex = p.getMatrixY() * map.getMapMatrix().length + p.getMatrixX();
//						View child = matrixLayerLayout.getChildAt(childIndex);
//						child.setVisibility(View.VISIBLE);
//						(map.getMapMatrix())[p.getMatrixY()][p.getMatrixX()] = 1;
					}
					return true;
				}
			});
			addView(matrixLayerLayout);
		}
		matrixLayerLayout.removeAllViews();

		byte[][] temp = map.getMapMatrix();

		for (int shu = 0; shu < temp.length; shu++) {
			for (int heng = 0; heng < temp[shu].length; heng++) {
				// if(temp[shu][heng] == 0){
				if((heng>=xStart&&heng<xEnd)&&(shu>=yStart&&shu<yEnd)){
					
				}else
				{
					continue;
				}
				
				final MatrixElement me = new MatrixElement(mapView, new Point(heng * 32 + 16, shu * 32 + 16, map));
				me.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						me.setVisibility(INVISIBLE);
						Point p = me.getPoint();
						(map.getMapMatrix())[p.getMatrixY()][p.getMatrixX()] = 1;
					}
				});
				matrixLayerLayout.addView(me);
//				if((heng>=xStart&&heng<xEnd)&&(shu>=yStart&&shu<yEnd)){
//					me.setVisibility(VISIBLE);
//				}else{
//					me.setVisibility(INVISIBLE);
//				}
				if (temp[shu][heng] == 1) {
					me.setVisibility(INVISIBLE);
				}
				// }
			}
		}
		matrixLayerLayout.update();
	}

	private void initSaveBtn() {
		LinearLayout l = new LinearLayout(getContext());
		if (saveBtn == null) {
			saveBtn = new Button(getContext());
			saveBtn.setText("保存");
			saveBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					saveMatrix();
				}
			});
			l.addView(saveBtn);
		}
		if (leftUpBtn == null) {
			leftUpBtn = new Button(getContext());
			leftUpBtn.setText("左上");
			leftUpBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mode =1;
					xStart = 0 ;
					xEnd = map.getMapMatrix()[0].length/2;
					yStart =0;
					yEnd = map.getMapMatrix().length/2;
					initMatrixLayerLayout();
				}
			});
			l.addView(leftUpBtn);
		}
		if (leftDwonBtn == null) {
			leftDwonBtn = new Button(getContext());
			leftDwonBtn.setText("左下");
			leftDwonBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mode =2;
					xStart = 0 ;
					xEnd = map.getMapMatrix()[0].length/2;
					yStart =map.getMapMatrix().length/2;
					yEnd = map.getMapMatrix().length;
					initMatrixLayerLayout();
				}
			});
			l.addView(leftDwonBtn);
		}
		if (rightUpBtn == null) {
			rightUpBtn = new Button(getContext());
			rightUpBtn.setText("右上");
			rightUpBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mode =3;
					xStart = map.getMapMatrix()[0].length/2;
					xEnd = map.getMapMatrix()[0].length;
					yStart =0;
					yEnd = map.getMapMatrix().length/2;
					initMatrixLayerLayout();
				}
			});
			l.addView(rightUpBtn);
		}
		if (rightDownBtn == null) {
			rightDownBtn = new Button(getContext());
			rightDownBtn.setText("右下");
			rightDownBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mode =4;
					xStart = map.getMapMatrix()[0].length/2;
					xEnd = map.getMapMatrix()[0].length;
					yStart =map.getMapMatrix().length/2;
					yEnd = map.getMapMatrix().length;
					initMatrixLayerLayout();
				}
			});
			l.addView(rightDownBtn);
		}
		if (allBtn == null) {
			allBtn = new Button(getContext());
			allBtn.setText("全显示");
			allBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mode =5;
					xStart = 0 ;
					xEnd = map.getMapMatrix()[0].length;
					yStart =0;
					yEnd = map.getMapMatrix().length;
					initMatrixLayerLayout();
				}
			});
			l.addView(allBtn);
		}
		
		if (locationBtn == null) {
			locationBtn = new Button(getContext());
			locationBtn.setText("打点");
			locationBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if(locationLayerLayout.getVisibility()==VISIBLE){
						locationLayerLayout.setVisibility(GONE);
					}else{
						locationLayerLayout.setVisibility(VISIBLE);
						locationLayerLayout.update();
					}
				}
			});
			l.addView(locationBtn);
		}
		
		addView(l);
		
		
	}

	private void saveMatrix() {
		StringBuffer exceptionStr = new StringBuffer();
		exceptionStr.append("{");
		byte[][] matrix = map.getMapMatrix();
		for (int shu = 0; shu < matrix.length; shu++) {
			exceptionStr.append("{");
			for (int heng = 0; heng < matrix[shu].length; heng++) {
				if (heng == matrix[shu].length - 1) {
					exceptionStr.append(matrix[shu][heng] + "");
				} else {
					exceptionStr.append(matrix[shu][heng] + ",");
				}
			}
			if (shu == matrix.length - 1) {
				exceptionStr.append("}};");
			} else {
				exceptionStr.append("},\n");
			}
		}
		MapCommonUtil.writeContentToSDCard("MapMatrix", map.getMapName(), exceptionStr.toString());
		MapCommonUtil.saveMapMatrix(map.getMapName());
	}

	private BaseLayerLayout locationLayerLayout;
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
					Location value = entry.getValue();
					final LocationPointElement locationPointElement = new LocationPointElement(mapView, value);
					locationLayerLayout.addView(locationPointElement);
				}
			}
		}

		locationLayerLayout.update();
		
	}
	
	@Override
	protected void moveHook() {
		super.moveHook();
		if (matrixLayerLayout.getVisibility() == VISIBLE) {
			matrixLayerLayout.setVisibility(INVISIBLE);
		}
		if(locationLayerLayout!=null&&locationLayerLayout.getVisibility()==VISIBLE){
			locationLayerLayout.update();
		}
	}

	@Override
	protected void upHook() {
		super.upHook();
		if (matrixLayerLayout.getVisibility() == INVISIBLE) {
			matrixLayerLayout.setVisibility(VISIBLE);
		}
		if (matrixLayerLayout != null) {
			matrixLayerLayout.update();
		}
		if(locationLayerLayout!=null&&locationLayerLayout.getVisibility()==VISIBLE){
			locationLayerLayout.update();
		}
	}
}
