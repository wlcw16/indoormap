package com.indoormap.framework.element;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;

import com.indoormap.R;
import com.indoormap.framework.element.base.BaseElementView;
import com.indoormap.framework.map.MapView;
import com.indoormap.framework.model.Point;

public class MyLocationElement extends BaseElementView {

	protected Bitmap bitmap;

	protected Bitmap backGroundBitmap;

	protected Paint paint;

	protected Matrix matrix;

	protected List<Bitmap> bitmapList;

	protected int delay = 30;

	protected int index = 0;

	public MyLocationElement(MapView mapView, Point point) {
		super(mapView, point);
		init();
	}

	protected void init() {
		bitmapList = new ArrayList<Bitmap>();
		try {
			BitmapDrawable tempa = (BitmapDrawable)(getContext().getResources().getDrawable(R.drawable.me_a));
			bitmapList.add(tempa.getBitmap());
			BitmapDrawable tempb = (BitmapDrawable)(getContext().getResources().getDrawable(R.drawable.me_b));
			bitmapList.add(tempb.getBitmap());
			BitmapDrawable tempc = (BitmapDrawable)(getContext().getResources().getDrawable(R.drawable.me_c));
			bitmapList.add(tempc.getBitmap());
			BitmapDrawable tempar = (BitmapDrawable)(getContext().getResources().getDrawable(R.drawable.me_around));
			backGroundBitmap = tempar.getBitmap();
			
//			bitmapList.add(MapCommonUtil.getBitmapFromAssets(getContext(), "res/me_a.png"));
//			bitmapList.add(MapCommonUtil.getBitmapFromAssets(getContext(), "res/me_b.png"));
//			bitmapList.add(MapCommonUtil.getBitmapFromAssets(getContext(), "res/me_c.png"));
//			backGroundBitmap = MapCommonUtil.getBitmapFromAssets(getContext(), "res/me_around.png");
			bitmap = bitmapList.get(0);
			paint = new Paint();
			matrix = new Matrix();
			width = 256;
			height = 256;
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public float getBeWidth() {
		float[] values = new float[9];
		mapView.getImgMatrix().getValues(values);
		float scale = (float) Math.sqrt(values[0] * values[0] + values[1] * values[1]);
		return width * scale;
	}

	@Override
	public float getBeHeight() {
		float[] values = new float[9];
		mapView.getImgMatrix().getValues(values);
		float scale = (float) Math.sqrt(values[3] * values[3] + values[4] * values[4]);
		return height * scale;
	}

	@Override
	public float getDeltaWdith() {
		return getBeWidth() / 2f;
	}

	@Override
	public float getDeltaHeight() {
		return getBeHeight() / 2f;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(backGroundBitmap, matrix, paint);

		int i = (int) ((index % (4 * delay)) / delay);
		if (i >= bitmapList.size()) {
			index = 0;
			i = (int) ((index % (4 * delay)) / delay);
		}
		index++;
		bitmap = bitmapList.get(i);
		canvas.drawBitmap(bitmap, matrix, paint);
		canvas.save();
		invalidate();
		super.onDraw(canvas);
	}

	@Override
	public void update() {
		float[] values = new float[9];
		mapView.getImgMatrix().getValues(values);
		float scale = (float) Math.sqrt(values[3] * values[3] + values[4] * values[4]);
		matrix.setScale(scale, scale);
		// matrix.postScale(scale, scale);
		float x = point.getX();
		float y = point.getY();
		layout((int) (values[2] + (x * values[0] + y * values[1]) - getDeltaWdith()), (int) (values[5] + (x * values[3] + y * values[4]) - getDeltaHeight()), (int) (values[2] + (x * values[0] + y * values[1]) + getBeWidth() - getDeltaWdith()), (int) (values[5] + (x * values[3] + y * values[4])
				+ getBeHeight() - getDeltaHeight()));
		postInvalidate();
	}
}
