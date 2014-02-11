package com.indoormap.framework.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class Zone implements Serializable{
	private static final long serialVersionUID = 1L;
	private List<Point> edgeList;
	private HashMap<String,Location> locationList;
	private List<Point> displayList;
	private float top=2048;
	private float left=2048;
	private float right;
	private float bottom;
	private String id;
	private String mapName;
	private String floor;
	private String buildingName;
	private String description;
	
	public boolean isInside(Point p){
		if(p.getX()>=left&&p.getX()<=right&&p.getY()>=top&&p.getY()<=bottom){
			return true;
		}else{
			return false;
		}
	}
	
	public List<Point> getEdgeList() {
		return edgeList;
	}
	public void setEdgeList(List<Point> edgeList) {
		this.edgeList = edgeList;
		for(Point p : edgeList){
			if(p.getX()<=left){
				left = p.getX();
			}if(p.getX()>=right){
				right = p.getX();
			}if(p.getY()<=top){
				top = p.getY();
			}if(p.getY()>=bottom){
				bottom = p.getY();
			}
		}
	}
	public  HashMap<String,Location> getLocationList() {
		return locationList;
	}
	public void setLocationList( HashMap<String,Location> locationList) {
		this.locationList = locationList;
	}
	public List<Point> getDisplayList() {
		return displayList;
	}
	public void setDisplayList(List<Point> displayList) {
		this.displayList = displayList;
	}
	public float getTop() {
		return top;
	}
	public void setTop(float top) {
		this.top = top;
	}
	public float getLeft() {
		return left;
	}
	public void setLeft(float left) {
		this.left = left;
	}
	public float getRight() {
		return right;
	}
	public void setRight(float right) {
		this.right = right;
	}
	public float getBottom() {
		return bottom;
	}
	public void setBottom(float bottom) {
		this.bottom = bottom;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMapName() {
		return mapName;
	}
	public void setMapName(String mapName) {
		this.mapName = mapName;
	}
	public String getFloor() {
		return floor;
	}
	public void setFloor(String floor) {
		this.floor = floor;
	}
	public String getBuildingName() {
		return buildingName;
	}
	public void setBuildingName(String buildingName) {
		this.buildingName = buildingName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
