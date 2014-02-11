package com.indoormap.framework.element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;

import com.indoormap.framework.common.MapCommonData;
import com.indoormap.framework.common.MapCommonUtil;
import com.indoormap.framework.element.base.BaseElementView;
import com.indoormap.framework.map.MapView;
import com.indoormap.framework.model.Point;
import com.indoormap.framework.model.Zone;

public class ZoneElement extends BaseElementView {
	protected Bitmap backgroudBtimap;
	protected Paint paint;
	protected Matrix imgMatrix;
	protected boolean isCompleted = false;

	protected boolean isAddMode = false;

	protected List<Point> addZoneList = new ArrayList<Point>();

	protected HashMap<String, Zone> zoneList = new HashMap<String, Zone>();

	protected Zone curZone;

	public ZoneElement(MapView mapView, Point point) {
		super(mapView, point);
		init();
	}
	public void init(MapView mapView){
		isCompleted=false;
		this.mapView=mapView;
		imgMatrix = mapView.getImgMatrix();
		zoneList = MapCommonData.getInstance().getZoneHashMap().get(mapView.getMap().getMapName());
		if(zoneList==null){
			zoneList = new HashMap<String, Zone>();
		}
		isCompleted = true;
		postInvalidate();
	}
	@Override
	protected void init() {
		super.init();
		paint = new Paint();
		init(mapView);
		postInvalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (isCompleted) {
			float[] values = new float[9];
			imgMatrix.getValues(values);
			float scale = MapCommonUtil.getMapScale(mapView);
			if (isAddMode) {
				Paint pointPaint = new Paint();
				pointPaint.setColor(Color.BLUE);
				float x = 0;
				float y = 0;
				for (Point p : addZoneList) {
					x = values[2] + p.getX() * values[0] + p.getY() * values[1];
					y = values[5] + p.getX() * values[3] + p.getY() * values[4];
					canvas.drawCircle(x, y, 10 * scale, pointPaint);
				}
			}
			
			for (Zone z : zoneList.values()) {
				if (z.getEdgeList() != null && z.getEdgeList().size() > 2) {
					Path path = new Path();
					Point p = z.getEdgeList().get(0);
					float x = values[2] + p.getX() * values[0] + p.getY() * values[1];
					float y = values[5] + p.getX() * values[3] + p.getY() * values[4];
					path.moveTo(x, y);
					for (int i = 1; i < z.getEdgeList().size(); i++) {
						Point p1 = z.getEdgeList().get(i);
						x = values[2] + p1.getX() * values[0] + p1.getY() * values[1];
						y = values[5] + p1.getX() * values[3] + p1.getY() * values[4];
						path.lineTo(x, y);
					}
					path.close();
					Paint zonePaint = new Paint();
					zonePaint.setColor(Color.RED);
					zonePaint.setAlpha(50);
					Paint curZonePaint = new Paint();
					curZonePaint.setColor(Color.BLUE);
					curZonePaint.setAlpha(50);
					if (curZone != null && z.getId().equals(curZone.getId())) {
						canvas.drawPath(path, curZonePaint);
						if(curZone.getDisplayList()!=null){
							for (Point p1 : curZone.getDisplayList()) {
								x = values[2] + p1.getX() * values[0] + p1.getY() * values[1];
								y = values[5] + p1.getX() * values[3] + p1.getY() * values[4];
								canvas.drawCircle(x, y, 10 * scale, zonePaint);
							}
						}
					} else {
						canvas.drawPath(path, zonePaint);
					}
				}

			}

		}
	}

	@Override
	public void update() {
		postInvalidate();
	}

	public boolean isAddMode() {
		return isAddMode;
	}

	public void setAddMode(boolean isAddMode) {
		this.isAddMode = isAddMode;
	}

	public void clearAddList() {
		addZoneList = new ArrayList<Point>();
		postInvalidate();
	}

	public void addZonePoint(Point p) {
		addZoneList.add(p);
		postInvalidate();
	}

	public void addZone(Zone z) {
		zoneList.put(z.getId(), z);
		postInvalidate();
	}

	public void removeZone(Zone z) {
		zoneList.remove(z.getId());
		postInvalidate();
	}

	public void setCurZone(Zone z) {
		curZone = z;
		postInvalidate();
	}
}
