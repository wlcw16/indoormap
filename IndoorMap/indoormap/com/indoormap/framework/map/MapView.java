package com.indoormap.framework.map;

import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposePathEffect;
import android.graphics.CornerPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.indoormap.framework.common.MapCommonConstant;
import com.indoormap.framework.common.MapCommonUtil;
import com.indoormap.framework.model.Map;
import com.indoormap.framework.model.Point;
import com.indoormap.framework.screen.base.BaseMapScreen;

public class MapView extends View {

	protected BaseMapScreen baseMapScreen;

	protected Map map;

	protected float startWidth;

	protected float startHeight;

	private Bitmap bmHigh;

	private Bitmap bmMid;

	private Bitmap bmLow;
	private float maxRate = 10;
	private float minRate = 0;

	protected Bitmap mapBitmap;

	protected Paint paint;
	protected Matrix imgMatrix;
	protected Handler loadMapHandler;

	private boolean isCompleted = false;

	private boolean isInvalidate = false;

	private boolean isRotate = false;
	private float degreeStart = 0;
	private float degree = 0;
	private float degreeStep = 0;
	private float degreeDx = 0;
	private float degreeDy = 0;

	private boolean isScale = false;
	private float scale = 0;
	private float scaleStep = 0;
	private float scaleDx = 0;
	private float scaleDy = 0;

	private boolean isTranslate = false;
	private float translateX = 0;
	private float translateY = 0;
	private float sumX = 0;
	private float sumY = 0;
	private int translateFrom = 0;
	private float translateStepX = 0;
	private float translateStepY = 0;

	// -----------------路线--------------------
	private Path p1;
	private Paint paint1;
	private Paint paint2;
	private List<Point> path;
	private float phase;
	private int routeAlpha = 0;
	private boolean isNaviMode = false;
	private boolean isNaviRefresh = false;

	private OnMapLoadCompletedListener onMapLoadCompletedListener;

	private OnInvalidataListener onInvalidataListener;

	public MapView(BaseMapScreen baseMapScreen, Map map) {
		super(baseMapScreen.getContext());
		this.baseMapScreen = baseMapScreen;
		this.map = map;
		this.startWidth = map.getWidth();
		this.startHeight = map.getHeight();
		paint = new Paint();
		imgMatrix = new Matrix();
		loadMapHandler = new LoadMapHandler();
		initRouteParams();
		loadMap();
	}

	private void initRouteParams() {
		paint1 = new Paint();
		paint1.setStyle(Paint.Style.STROKE);
		paint1.setStrokeCap(Cap.ROUND);
		paint1.setStrokeJoin(Paint.Join.ROUND);
		paint1.setAntiAlias(true);
		paint1.setColor(Color.WHITE);

		paint2 = new Paint();
		paint2.setStyle(Paint.Style.STROKE);
		paint2.setStrokeCap(Cap.ROUND);
		paint2.setStrokeJoin(Paint.Join.ROUND);
		paint2.setAntiAlias(true);
		paint2.setColor(Color.RED);
	}

	public boolean isNaviMode(){
		return isNaviMode;
	}
	
	public void startNaviMode(List<Point> path) {
		this.path = path;
		if (path != null && path.size() > 1) {
			isNaviMode = true;
			routeAlpha = 0;
			updateNaviMode();
		}else{
			if(path==null){
				Toast.makeText(getContext(), "生成路径失败", Toast.LENGTH_SHORT).show();
			}
			if(path.size()<=1){
				Toast.makeText(getContext(), "您已在终点附近", Toast.LENGTH_SHORT).show();
			}
		}
	}

	public void endNaviMode() {
		isNaviMode = false;
//		isNaviRefresh = false;
//		postInvalidate();
	}

	public void updateNaviMode() {
		if (isNaviMode && !isInvalidate) {
			float[] values = new float[9];
			imgMatrix.getValues(values);
			scale = (float) Math.sqrt(values[3] * values[3] + values[4] * values[4]);
			scale = (float)Math.round(scale*10)/10;
			p1 = new Path();
			float px, py;
			px = (path.get(0).getX() + map.getCellSize() / 2f) * values[0] + (path.get(0).getY() + map.getCellSize() / 2f) * values[1] + values[2];
			py = (path.get(0).getX() + map.getCellSize() / 2f) * values[3] + (path.get(0).getY() + map.getCellSize() / 2f) * values[4] + values[5];
			p1.moveTo(px, py);
			for (int i = 1; i < path.size(); i++) {
				px = (path.get(i).getX() + map.getCellSize() / 2f) * values[0] + (path.get(i).getY() + map.getCellSize() / 2f) * values[1] + values[2];
				py = (path.get(i).getX() + map.getCellSize() / 2f) * values[3] + (path.get(i).getY() + map.getCellSize() / 2f) * values[4] + values[5];
				p1.lineTo(px, py);
			}
			isNaviRefresh = true;
			// postInvalidate();
		}
	}

	PaintFlagsDrawFilter pfdf = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
	
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		if (isCompleted) {
			canvas.drawBitmap(mapBitmap, imgMatrix, paint);
			canvas.setDrawFilter(pfdf); 
			if (isInvalidate) {
				if (isRotate) {
				}
				if (isScale) {
					float curScale = MapCommonUtil.getMapScale(this);
					if (scaleStep > 0) {
						if (curScale * (1 + scaleStep) > scale) {
							isScale = false;
							isInvalidate = false;
							imgMatrix.postScale(scale / curScale, scale / curScale, scaleDx, scaleDy);
							if (onInvalidataListener != null) {
								onInvalidataListener.onInvalidataEnd(-1);
							}
						}
					} else {
						if (curScale * (1 + scaleStep) < scale) {
							isScale = false;
							isInvalidate = false;
							imgMatrix.postScale(scale / curScale, scale / curScale, scaleDx, scaleDy);
							if (onInvalidataListener != null) {
								onInvalidataListener.onInvalidataEnd(-1);
							}
						}
					}
					if (isScale) {
						imgMatrix.postScale(1 + scaleStep, 1 + scaleStep, scaleDx, scaleDy);
					}


				}

				if (isTranslate) {
					sumX += translateStepX;
					sumY += translateStepY;
					if (Math.abs(sumX) > Math.abs(translateX) && Math.abs(sumY) > Math.abs(translateY)) {
						isTranslate = false;
						isInvalidate = false;
						if (onInvalidataListener != null) {
							onInvalidataListener.onInvalidataEnd(translateFrom);
						}

					} 
					if (isTranslate) {
						imgMatrix.postTranslate(translateStepX, translateStepY);
					}

				}
				if (onInvalidataListener != null) {
					onInvalidataListener.onInvalidata();
				}
				invalidate();
			}
			if (isNaviRefresh && !isInvalidate) {
				PathEffect pe1 = new CornerPathEffect(10);
				PathEffect pe2 = new PathDashPathEffect(makePathDash(), 60 * scale, phase, PathDashPathEffect.Style.ROTATE);
				PathEffect pe = new ComposePathEffect(pe2, pe1);
				paint1.setPathEffect(pe);
				paint1.setStrokeWidth(10f * scale);
				paint2.setPathEffect(pe1);
				paint2.setStrokeWidth(15f * scale);
				if(routeAlpha<=255&&isNaviMode){
					paint1.setAlpha(routeAlpha);
					paint2.setAlpha(routeAlpha);
					routeAlpha+=5;
				}else if(!isNaviMode){
					if(routeAlpha>0){
						paint1.setAlpha(routeAlpha);
						paint2.setAlpha(routeAlpha);
						routeAlpha-=5;
					}else{
						isNaviRefresh = false;
					}
				}
				canvas.drawPath(p1, paint2);
				canvas.drawPath(p1, paint1);
				canvas.save();
				phase = phase - 0.5f*scale;
				invalidate();
			}
			
		}

		super.onDraw(canvas);
	}

	private void loadMap() {
		new Thread() {

			public void run() {
				try {
					bmHigh = MapCommonUtil.getBitmapFromAssets(getContext(), map.getMapPicPathHashMap().get(MapCommonConstant.HIGH));
					bmMid = MapCommonUtil.getBitmapFromAssets(getContext(), map.getMapPicPathHashMap().get(MapCommonConstant.MIDDLE));
					bmLow = MapCommonUtil.getBitmapFromAssets(getContext(), map.getMapPicPathHashMap().get(MapCommonConstant.LOW));
					loadMapHandler.sendEmptyMessage(1);
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();
	}

	public void changeMap(Map map) {
		isCompleted = false;
		this.map = map;
		this.startWidth = map.getWidth();
		this.startHeight = map.getHeight();
		paint = new Paint();
		imgMatrix = new Matrix();
		mapGC();
		loadMap();
	}

	public void mapGC() {
		if (null != bmHigh) {
			bmHigh.recycle();
			bmHigh = null;
		}
		if (null != bmMid) {
			bmMid.recycle();
			bmMid = null;
		}
		if (null != bmLow) {
			bmLow.recycle();
			bmLow = null;
		}
		System.gc();
	}

	public void setMapPath(String path) {
		if (MapCommonConstant.HIGH.endsWith(path)) {
			if (mapBitmap != bmHigh) {
				mapBitmap = bmHigh;
			}

		}
		if (MapCommonConstant.MIDDLE.endsWith(path)) {
			if (mapBitmap != bmMid) {
				mapBitmap = bmMid;
			}
		}
		if (MapCommonConstant.LOW.endsWith(path)) {
			if (mapBitmap != bmLow) {
				mapBitmap = bmLow;
			}
		}
	}

	public Matrix getImgMatrix() {
		return imgMatrix;
	}

	public float getMaxRate() {
		return maxRate;
	}

	public float getMinRate() {
		return minRate;
	}

	public float getStartWidth() {
		return startWidth;
	}

	public float getStartHeight() {
		return startHeight;
	}

	public Map getMap() {
		return map;
	}

	public void setOnMapLoadCompletedListener(OnMapLoadCompletedListener onMapLoadCompletedListener) {
		this.onMapLoadCompletedListener = onMapLoadCompletedListener;
	}

	public OnMapLoadCompletedListener getOnMapLoadCompletedListener() {
		return onMapLoadCompletedListener;
	}

	private class LoadMapHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			mapBitmap = bmLow;
			minRate = MapCommonUtil.getScreenWidth(getContext()) / MapView.this.startWidth;
			imgMatrix.setScale(minRate, minRate);
			float[] values = new float[9];
			imgMatrix.getValues(values);
			float y = (MapCommonUtil.getScreenHeight(getContext()) - startHeight * values[4]) / 2;
			imgMatrix.postTranslate(0, y);
			
			isNaviMode=false;
			isNaviRefresh = false;
			
			isCompleted = true;
			postInvalidate();
			if (onMapLoadCompletedListener != null) {
				onMapLoadCompletedListener.onMapLoadCompleted(map);
			}
		}
	}

	public void moveToCenter(Point point, int from) {
		int sWidth = MapCommonUtil.getScreenWidth(getContext()) / 2;
		int sHeight = MapCommonUtil.getScreenHeight(getContext()) / 2;
		// float scale = MapCommonUtil.getMapScale(this);
		float[] values = new float[9];
		imgMatrix.getValues(values);
		float startx = (point.getX() * values[0] + point.getY() * values[1]) + values[2];
		float starty = (point.getX() * values[3] + point.getY() * values[4]) + values[5];
		float x = sWidth - startx;
		float y = sHeight - starty;
		float dx = x / 10;
		float dy = y / 10;
		if (Math.abs(x) < 3 && Math.abs(y) < 3) {
			if (null != onInvalidataListener) {
				onInvalidataListener.onInvalidataEnd(from);
				return;
			}
		}
		translate(x, y, dx, dy, from);

	}

	public void scale(float scale, float step, float dx, float dy) {
		this.scale = scale;
		this.scaleStep = step;
		this.scaleDx = dx;
		this.scaleDy = dy;
		isScale = true;
		isInvalidate = true;
		postInvalidate();
	}

	public void translate(float x, float y, float xStep, float yStep, int from) {
		translateStepX = xStep;
		translateStepY = yStep;
		sumX = 0;
		sumY = 0;
		translateX = x;
		translateY = y;
		isTranslate = true;
		isInvalidate = true;
		translateFrom = from;
		postInvalidate();
	}

	public void rotate(float degree, float step, float dx, float dy) {
		degreeStart = 0;
		this.degree = degree;
		isRotate = true;
	}

	private Path makePathDash() {
		Path p = new Path();
		p.moveTo(4 * scale, 0 * scale);
		p.lineTo(0 * scale, -4 * scale);
		p.lineTo(8 * scale, -4 * scale);
		p.lineTo(12 * scale, 0 * scale);
		p.lineTo(8 * scale, 4 * scale);
		p.lineTo(0 * scale, 4 * scale);
		return p;
	}

	public void setOnInvalidataListener(OnInvalidataListener onInvalidataListener) {
		this.onInvalidataListener = onInvalidataListener;
	}

	public interface OnInvalidataListener {
		public void onInvalidata();

		public void onInvalidataEnd(int from);
	}

	public interface OnMapLoadCompletedListener {
		public void onMapLoadCompleted(Map map);
	}
}
