package com.indoormap.framework.logic;

import java.util.ArrayList;

import android.os.Handler;

import com.indoormap.framework.common.AStart;
import com.indoormap.framework.model.Map;
import com.indoormap.framework.model.Point;

public class RouteGenerateLogic {
	private static AStart aStart = new AStart();

	public static ArrayList<Point> generateRoute(final Handler handler, Map map, final Point startPoint, final Point endPoint) {
		 return generateRoute(handler, map, startPoint, endPoint, -1);
	}

	public static ArrayList<Point> generateRoute(final Handler handler, Map map, final Point startPoint, final Point endPoint, int from) {
		if (aStart == null) {
			aStart = new AStart();
		}
		aStart.setMap(map.getMapMatrix(), map.getMapMatrix().length, map.getMapMatrix()[0].length);
		ArrayList<Point> rp1 = aStart.getPath(startPoint.getMatrixX(), startPoint.getMatrixY(), endPoint.getMatrixX(), endPoint.getMatrixY(), map);
		return rp1;
	}
	
}
