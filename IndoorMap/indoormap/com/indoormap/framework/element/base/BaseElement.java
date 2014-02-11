package com.indoormap.framework.element.base;

import com.indoormap.framework.model.Point;

public interface BaseElement {
	
	public void setBeWidth(float width);

	public float getBeWidth();

	public void setBeHeight(float height);

	public float getBeHeight();

	public void setPoint(Point point);

	public Point getPoint();

	public float getDeltaWdith();

	public float getDeltaHeight();

	public void update();


}
