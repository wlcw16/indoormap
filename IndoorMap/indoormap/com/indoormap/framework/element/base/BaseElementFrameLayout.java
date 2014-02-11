package com.indoormap.framework.element.base;

import android.widget.FrameLayout;

import com.indoormap.framework.map.MapView;
import com.indoormap.framework.model.Point;

public class BaseElementFrameLayout extends FrameLayout implements BaseElement{

	protected float deltaWidth;

	protected float deltaHeight;

	protected float width;
	
	protected float height;
	
	protected MapView mapView;
	
	protected Point point;
	
	
	public BaseElementFrameLayout(MapView mapView, Point point) {
		super(mapView.getContext());
		this.mapView= mapView;
		this.point = point;
	}

	/**
	 * 子类继承后，在此内进行element初始化。
	 */
	protected void init(){
		
	}
	
	public void setBeWidth(float width) {
		this.width = width;
	}

	public float getBeWidth(){
		return width;
	}
	
	public void setBeHeight(float height) {
		this.height = height;
	}
	public float getBeHeight(){
		return height;
	}
	
	public void setPoint(Point point) {
		this.point = point;
		update();
	}

	public Point getPoint() {
		return point;
	}

	public float getDeltaWdith(){
		return deltaWidth;
	}
	
	public float getDeltaHeight(){
		return deltaHeight;
	}
	
	public void update() {
		float[] values = new float[9];
		mapView.getImgMatrix().getValues(values);
		float x = point.getX();
		float y = point.getY();
		layout((int) (values[2]	+ (x * values[0] + y * values[1]) - getDeltaWdith()),
				(int) (values[5]	+ (x * values[3] + y * values[4]) - getDeltaHeight()),
				(int) (values[2] + (x * values[0] + y * values[1])	+ getBeWidth() - getDeltaWdith()), 
				(int) (values[5]	+ (x * values[3] + y * values[4])	+ getBeHeight() - getDeltaHeight())); 
		
//		layout((int) (values[2] + point.getX()* values[0] - getDeltaWdith()), 
//				(int) (values[5] + point.getY() * values[4] - getDeltaHeight()), 
//				(int) (values[2] + point.getX() * values[0] + getBeWidth() - getDeltaWdith()),
//				(int) (values[5] + point.getY() * values[4] + getBeHeight() - getDeltaHeight()));
	}
	
	
	
	
	
}
