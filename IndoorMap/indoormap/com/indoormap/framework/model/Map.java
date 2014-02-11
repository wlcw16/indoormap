package com.indoormap.framework.model;

import java.io.Serializable;
import java.util.HashMap;

public class Map implements Serializable{

	private static final long serialVersionUID = 1L;

	private String id;
	
	private String buildId;
	
	private String mapName;
	
	private String mapNickname;
	
	private HashMap<String, String> mapPicPathHashMap;
	
	private String floor;
	
	private int width;

	private int height;

	
	/**
	 * pixel/real
	 */
	private float scale;
	
	private byte[][] mapMatrix;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBuildId() {
		return buildId;
	}

	public void setBuildId(String buildId) {
		this.buildId = buildId;
	}

	public String getMapName() {
		return mapName;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	public HashMap<String, String> getMapPicPathHashMap() {
		return mapPicPathHashMap;
	}

	public void setMapPicPathHashMap(HashMap<String, String> mapPicPathHashMap) {
		this.mapPicPathHashMap = mapPicPathHashMap;
	}

	public String getFloor() {
		return floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public byte[][] getMapMatrix() {
		return mapMatrix;
	}

	public void setMapMatrix(byte[][] mapMatrix) {
		this.mapMatrix = mapMatrix;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public float getScale() {
		return scale;
	}

	public float getCellSize() {
		float cellSize = 1;
		if(mapMatrix!=null&&width>0&&height>0){
			if(width/mapMatrix.length == height/mapMatrix[0].length){
				cellSize = width/mapMatrix.length;
			}
		}
		return cellSize;
	}

	public void setMapNickname(String mapNickname) {
		this.mapNickname = mapNickname;
	}

	public String getMapNickname() {
		return mapNickname;
	}
	
	
}
