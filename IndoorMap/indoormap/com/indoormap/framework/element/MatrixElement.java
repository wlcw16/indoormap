package com.indoormap.framework.element;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.indoormap.R;
import com.indoormap.framework.element.base.BaseElementView;
import com.indoormap.framework.map.MapView;
import com.indoormap.framework.model.Point;

public class MatrixElement extends BaseElementView{
	protected Bitmap mapBitmap;
	protected Paint paint;
	protected Matrix matrix;

	public MatrixElement(MapView mapView, Point point) {
		super(mapView, point);
		init();
	}

	@Override
	protected void init() {
		float cellSize = mapView.getMap().getCellSize();
		width = cellSize;
		height = cellSize;
		paint = new Paint();
		matrix = new Matrix();
		mapBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.matrix_bg);
	}
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(mapBitmap, matrix, paint);
		super.onDraw(canvas);
	}
	
	@Override
	public float getBeWidth() {
		float[] values = new float[9];
		mapView.getImgMatrix().getValues(values);
		float scale =(float) Math.sqrt( values[0]*values[0]+values[1]*values[1]);
		return width*scale;
	}
	
	@Override
	public float getBeHeight() {
		float[] values = new float[9];
		mapView.getImgMatrix().getValues(values);
		float scale =(float) Math.sqrt( values[3]*values[3]+values[4]*values[4]);
		return height*scale;
	}
	
	@Override
	public float getDeltaWdith() {
		return getBeWidth()/2f;
	}
	
	@Override
	public float getDeltaHeight() {
		return getBeHeight()/2f;
	}

	@Override
	public void update() {
		float[] values = new float[9];
		mapView.getImgMatrix().getValues(values);
		float scale =(float) Math.sqrt( values[3]*values[3]+values[4]*values[4]);
		matrix.setScale(scale, scale);
		float x = point.getX();
		float y = point.getY();
		layout((int) (values[2]	+ (x * values[0] + y * values[1]) - getDeltaWdith()),
				(int) (values[5]	+ (x * values[3] + y * values[4]) - getDeltaHeight()),
				(int) (values[2] + (x * values[0] + y * values[1])	+ getBeWidth() - getDeltaWdith()), 
				(int) (values[5]	+ (x * values[3] + y * values[4])	+ getBeHeight() - getDeltaHeight())); 
		postInvalidate();
	}
}
