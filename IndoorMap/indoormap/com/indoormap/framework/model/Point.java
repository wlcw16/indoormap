package com.indoormap.framework.model;

import java.io.Serializable;

public class Point implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Map map;
	
	private float x,y,realX,realY;
	
	private int matrixX,matrixY;
	
	public Point (float x , float y , Map map){
		this.x = x;
		this.y = y;
		this.map = map;
		init();
	}
	
	private void init(){
		realX = x/map.getScale();
		realY = y/map.getScale();
		matrixX = (int) (x/map.getCellSize());
		matrixY = (int) (y/map.getCellSize());
	}

	public Map getMap() {
		return map;
	}

	public void setMap(Map map) {
		this.map = map;
		init();
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
		init();
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
		init();
	}

	public float getRealX() {
		return realX;
	}

	public float getRealY() {
		return realY;
	}

	public int getMatrixX() {
		return matrixX;
	}

	public int getMatrixY() {
		return matrixY;
	}
	
	
}
