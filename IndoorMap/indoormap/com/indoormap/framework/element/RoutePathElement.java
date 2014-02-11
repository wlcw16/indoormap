package com.indoormap.framework.element;

import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposePathEffect;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;

import com.indoormap.framework.element.base.BaseElementView;
import com.indoormap.framework.map.MapView;
import com.indoormap.framework.model.Point;

public class RoutePathElement extends BaseElementView {

	private List<Point> path;
	private float scale = 0;
	private Path p1;
	private Paint paint1;
	private Paint paint2;
	
	private boolean isDataFormat = false;

	private boolean isRunning = true;
	public RoutePathElement(MapView mapView, List<Point> path) {
		super(mapView, new Point(0, 0, mapView.getMap()));
		this.path = path;
		init();
	}
	float phase;
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(isDataFormat){
			PathEffect pe1 = new CornerPathEffect(10);
			PathEffect pe2 = new PathDashPathEffect(makePathDash(), 60 * scale, phase, PathDashPathEffect.Style.ROTATE);
			PathEffect pe = new ComposePathEffect(pe2, pe1);
			paint1.setPathEffect(pe);
			paint1.setStrokeWidth(10f * scale);
			paint2.setPathEffect(pe1);
			paint2.setStrokeWidth(15f * scale);
			canvas.drawPath(p1, paint2);
			canvas.drawPath(p1, paint1);
			canvas.save();
			phase = phase - 0.5f * scale;
			if(isRunning){
				invalidate();
			}
		}
		
	}

	@Override
	protected void init() {
		
		paint1 = new Paint();
		paint1.setStyle(Paint.Style.STROKE);
		paint1.setStrokeCap(Cap.ROUND);
		paint1.setStrokeJoin(Paint.Join.ROUND);
		paint1.setAntiAlias(true);
		paint1.setColor(Color.WHITE);
		
		paint2 = new Paint();
		paint2.setStyle(Paint.Style.STROKE);
		paint2.setStrokeCap(Cap.ROUND);
		paint2.setStrokeJoin(Paint.Join.ROUND);
		paint2.setAntiAlias(true);
		paint2.setColor(Color.RED);
		
		float[] values = new float[9];
		mapView.getImgMatrix().getValues(values);
		scale = (float) Math.sqrt(values[3] * values[3] + values[4] * values[4]);
		width = mapView.getMap().getWidth();
		height = mapView.getMap().getHeight();
		
		
		if(path!=null&&path.size()>1){
			isDataFormat = true;
			update();
		}
		
	}
	
	@Override
	public float getBeWidth() {
		float[] values = new float[9];
		mapView.getImgMatrix().getValues(values);
		scale = (float) Math.sqrt(values[0] * values[0] + values[1] * values[1]);
		return width * scale;
	}

	@Override
	public float getBeHeight() {
		float[] values = new float[9];
		mapView.getImgMatrix().getValues(values);
		scale = (float) Math.sqrt(values[3] * values[3] + values[4] * values[4]);
		return height * scale;
	}

	@Override
	public float getDeltaWdith() {
		return 0;
	}

	@Override
	public float getDeltaHeight() {
		return 0;
	}

	@Override
	public void update() {
		isRunning = false;
		float[] values = new float[9];
		mapView.getImgMatrix().getValues(values);
		scale = (float) Math.sqrt(values[3] * values[3] + values[4] * values[4]);
		
		p1 = new Path();
		float px,py;
		px = (path.get(0).getX()+mapView.getMap().getCellSize()/2f)*values[0]+(path.get(0).getY()+mapView.getMap().getCellSize()/2f)*values[1];
		py = (path.get(0).getX()+mapView.getMap().getCellSize()/2f)*values[3]+(path.get(0).getY()+mapView.getMap().getCellSize()/2f)*values[4];
		p1.moveTo(px, py);
		for(int i = 1; i<path.size(); i++){
			px = (path.get(i).getX()+mapView.getMap().getCellSize()/2f)*values[0]+(path.get(i).getY()+mapView.getMap().getCellSize()/2f)*values[1];
			py = (path.get(i).getX()+mapView.getMap().getCellSize()/2f)*values[3]+(path.get(i).getY()+mapView.getMap().getCellSize()/2f)*values[4];
			p1.lineTo(px, py);
		}
	
		isRunning = true;
		postInvalidate();
	}
	
	private Path makePathDash() {
		Path p = new Path();
		p.moveTo(4 * scale, 0 * scale);
		p.lineTo(0 * scale, -4 * scale);
		p.lineTo(8 * scale, -4 * scale);
		p.lineTo(12 * scale, 0 * scale);
		p.lineTo(8 * scale, 4 * scale);
		p.lineTo(0 * scale, 4 * scale);
		return p;
	}
}
