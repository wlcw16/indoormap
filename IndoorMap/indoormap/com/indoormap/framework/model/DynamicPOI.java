package com.indoormap.framework.model;

import java.io.Serializable;

public class DynamicPOI implements Serializable{
	private static final long serialVersionUID = 2014010612480001L;
	private String mapName;
	private String id;
	private String buildingName;
	private String floor;
	private String popupTitle;
	private String popupIndex;
	private String title;
	private String openTime;
	private String detail;
	private String picPath;
	private String iconPath;
	private String location;
	private String triggerLocations="";
	private float x;
	private float y;
	private long timestamp;
	private String state;
	public String getMapName() {
		return mapName;
	}
	public void setMapName(String mapName) {
		this.mapName = mapName;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getBuildingName() {
		return buildingName;
	}
	public void setBuildingName(String buildingName) {
		this.buildingName = buildingName;
	}
	public String getFloor() {
		return floor;
	}
	public void setFloor(String floor) {
		this.floor = floor;
	}
	public String getPopupTitle() {
		return popupTitle;
	}
	public void setPopupTitle(String popupTitle) {
		this.popupTitle = popupTitle;
	}
	public String getPopupIndex() {
		return popupIndex;
	}
	public void setPopupIndex(String popupIndex) {
		this.popupIndex = popupIndex;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getOpenTime() {
		return openTime;
	}
	public void setOpenTime(String openTime) {
		this.openTime = openTime;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	public String getPicPath() {
		return picPath;
	}
	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}
	public String getIconPath() {
		return iconPath;
	}
	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String locations) {
		this.location = locations;
	}
	public float getX() {
		return x;
	}
	public void setX(float x) {
		this.x = x;
	}
	public float getY() {
		return y;
	}
	public void setY(float y) {
		this.y = y;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public void setTriggerLocations(String triggerLocations) {
		this.triggerLocations = triggerLocations;
	}
	public String getTriggerLocations() {
		return triggerLocations;
	}
	public void decode(String input){
		try {
			String[] fields = input.split("#");
			mapName = fields[0];
			id= fields[1];
			buildingName = fields[2];
			floor = fields[3];
			popupTitle  = fields[4];
			popupIndex = fields[5];
			title = fields[6];
			openTime = fields[7];
			location = fields[8];
			detail = fields[9];
			picPath = fields[10];
			iconPath = fields[11];
			x = Float.valueOf(fields[12]);
			y = Float.valueOf(fields[13]);
			triggerLocations = fields[14];
			timestamp = Long.valueOf(fields[15]);
			state = fields[16];
		} catch (Exception e) {
		}
	}
	
	public boolean checkIsTrigger(String location){
		boolean result = false;
		if(triggerLocations.indexOf(location)!=-1){
			return result = true;
		}
		return result;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getState() {
		return state;
	}
	
}
