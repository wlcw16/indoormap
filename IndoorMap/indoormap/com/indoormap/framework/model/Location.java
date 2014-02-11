package com.indoormap.framework.model;

import java.io.Serializable;

public class Location implements Serializable
{
	private static final long serialVersionUID = 1L;

	private String name;
    
	private String buildingName;
	
	private String mapName;
	
    private String floor;
    
    private Point point;
    
    public Location(String name,String mapName, String floor, Point point)
    {
        this.name = name;
        this.mapName=mapName;
        this.floor = floor;
        this.point = point;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getFloor()
    {
        return floor;
    }
    
    public void setFloor(String floor)
    {
        this.floor = floor;
    }
    
    public Point getPoint()
    {
        return point;
    }
    
    public void setPoint(Point point)
    {
        this.point = point;
    }

	public void setBuildingName(String buildingName) {
		this.buildingName = buildingName;
	}

	public String getBuildingName() {
		return buildingName;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	public String getMapName() {
		return mapName;
	}
    
}
