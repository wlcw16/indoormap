package com.indoormap.framework.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import com.indoormap.framework.map.MapView;
import com.indoormap.framework.model.DynamicPOI;
import com.indoormap.framework.model.Location;
import com.indoormap.framework.model.Map;
import com.indoormap.framework.model.POI;
import com.indoormap.framework.model.Point;
import com.indoormap.framework.model.PushMessage;
import com.indoormap.framework.model.Zone;

public class MapCommonUtil {

	private static int screenWidth = 0;

	private static int screenHeight = 0;

	private static final int TITLE_HEIGHT = 48;

	public static final String SDCARD_DIR = Environment.getExternalStorageDirectory().toString() + "/IndoorMap/" + MapCommonConstant.PROJECT_FOLDER_NAME + "/";

	public static final String POIHASHMAP_FILENAME = "_poiHashMap.obj";
	public static final String ZONEHASHMAP_FILENAME = "_zoneHashMap.obj";
	public static final String PUSHMESSAGEHASHMAP_FILENAME = "pushMessageHashMap.obj";
	public static final String LOCATIONHASHMAP_FILENAME = "_locationHashMap.obj";
	public static final String DYNAMICPOIHASHMAP_FILENAME = "_dynamicPoiHashMap.obj";
	public static final String MAPMATRIX_FILENAME = "_mapMatrix.obj";

	public static Bitmap getBitmapFromAssets(Context context, String path) throws IOException {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		AssetManager am = context.getAssets();
		InputStream is = am.open(path);
		Bitmap result = BitmapFactory.decodeStream(is, null, opt);
		return result;
	}

	public static Bitmap getBitmapFromInternet(String strUrl) throws IOException {
		Bitmap bitmap = null;
		InputStream is = null;
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		if (strUrl.indexOf("http") == -1) {
			strUrl = "http://" + strUrl;
		}
		URL url = new URL(strUrl);
		URLConnection conn = url.openConnection();
		is = conn.getInputStream();
		bitmap = BitmapFactory.decodeStream(is, null, opt);
		return bitmap;
	}

	@SuppressWarnings("deprecation")
	public static int getScreenWidth(Context context) {
		if (screenWidth != 0) {
			return screenWidth;
		}
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		screenWidth = wm.getDefaultDisplay().getWidth();
		return screenWidth;
	}

	@SuppressWarnings("deprecation")
	public static int getScreenHeight(Context context) {
		if (screenHeight != 0) {
			return screenHeight;
		}
		int top = 0;
		if (context instanceof Activity) {
			top = ((Activity) context).getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
			if (top == 0) {
				top = (int) (TITLE_HEIGHT * getScreenDensity(context));
			}
		}
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		screenHeight = wm.getDefaultDisplay().getHeight() - top;
		return screenHeight;
	}

	public static float getScreenDensity(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metric = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metric);
		return metric.density;
	}

	public static float getScreenDensityDpi(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metric = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metric);
		return metric.densityDpi;
	}

	public static Location getLocationFromData(String locationStr) {
		Location result = null;
		HashMap<String, HashMap<String, Location>> temp = MapCommonData.getInstance().getLocationHashMap();
		for (HashMap<String, Location> hm : temp.values()) {
			for (Location l : hm.values()) {
				if (null != locationStr) {
					if (locationStr.equals(l.getName())) {
						result = l;
						return result;
					}
				}
			}
		}
		return result;
	}

	public static float getInterceptEventY(Context context, float y) {
		return TITLE_HEIGHT * getScreenDensity(context) + y;
	}

	public static float getDistanceBetweenPoint(float x1, float y1, float x2, float y2) {
		return FloatMath.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}

	// 触碰两点间距离
	public static float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	public static void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	public static void midPointIntercepte(Context context, PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = MapCommonUtil.getInterceptEventY(context, event.getY(0)) + MapCommonUtil.getInterceptEventY(context, event.getY(1));
		point.set(x / 2, y / 2);
	}

	// 取手势中心点
	public static Point getMidPoint(Map map, MotionEvent event) {
		Point p = new Point(0, 0, map);
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		p.setX(x / 2);
		p.setY(y / 2);
		return p;
	}

	// 取旋转角度
	public static float rotation(MotionEvent event) {
		double delta_x = (event.getX(0) - event.getX(1));
		double delta_y = (event.getY(0) - event.getY(1));
		double radians = Math.atan2(delta_y, delta_x);
		return (float) Math.toDegrees(radians);
	}

	public static void writeContentToSDCard(String folderName, String fileName, String content) {
		File dir = new File(SDCARD_DIR + folderName);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		String finalName = fileName;
		try {
			finalName = new String(finalName.getBytes(), "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		File file = new File(dir, finalName);

		try {
			if (file.exists()) {
				file.delete();
			}
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(content.getBytes());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static float getMapScale(MapView mapView) {
		float[] values = new float[9];
		mapView.getImgMatrix().getValues(values);
		float scale = (float) Math.sqrt(values[3] * values[3] + values[4] * values[4]);
		return scale;
	}

	public static float getMapRotate(MapView mapView) {
		float[] values = new float[9];
		mapView.getImgMatrix().getValues(values);
		float rotate = (float) Math.acos(values[0]);
		return rotate;
	}

	public static Point event2Point(MapView mapView, MotionEvent event) {
		float[] values = new float[9];
		mapView.getImgMatrix().getValues(values);
		float x = 0;
		float y = 0;
		y = (values[0] * event.getY() - values[3] * event.getX() + values[2] * values[3] - values[0] * values[5]) / (values[0] * values[4] - values[1] * values[4]);
		x = (event.getX() - values[2] - y * values[1]) / values[0];
		x = Math.round(x * 100) / 100f;
		y = Math.round(y * 100) / 100f;
		return new Point(x, y, mapView.getMap());
	}

	// -----------------------------
	// 静态POI信息---------------------------------------------
	public static void savePOIHashMap(String mapName) {
		File file1 = new File(SDCARD_DIR + "StaticPOI/");
		if (!file1.exists()) {
			file1.mkdirs();
		}
		File f = new File(SDCARD_DIR + "StaticPOI/" + mapName + POIHASHMAP_FILENAME);
		if (f.exists()) {
			f.delete();
		}

		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
			oos.writeObject(MapCommonData.getInstance().getPoiHashMap().get(mapName));
			oos.flush();
			oos.close();
		} catch (Exception e) {
		} finally {

		}
	}

	@SuppressWarnings("unchecked")
	public static void loadPOIHashMap(Context context, String mapName) {
		AssetManager am = context.getAssets();
		try {
			InputStream is = am.open("StaticPOI/" + mapName + POIHASHMAP_FILENAME);
			ObjectInputStream ois = new ObjectInputStream(is);
			MapCommonData.getInstance().getPoiHashMap().put(mapName, (HashMap<String, POI>) ois.readObject());
		} catch (Exception e) {
		}
	}

	@SuppressWarnings("unchecked")
	public static void loadPOIHashMap(String mapName) {
		try {
			InputStream is = new FileInputStream(new File(SDCARD_DIR + "StaticPOI/" + mapName + POIHASHMAP_FILENAME));
			ObjectInputStream ois = new ObjectInputStream(is);
			MapCommonData.getInstance().getPoiHashMap().put(mapName, (HashMap<String, POI>) ois.readObject());
		} catch (Exception e) {
		}
	}

	// -------------------------------打点信息工具-------------------------------

	public static void saveLocationHashMap(String mapName) {
		File file1 = new File(SDCARD_DIR + "LocationPoint/");
		if (!file1.exists()) {
			file1.mkdirs();
		}
		File f = new File(SDCARD_DIR + "LocationPoint/" + mapName + LOCATIONHASHMAP_FILENAME);
		if (f.exists()) {
			f.delete();
		}

		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
			oos.writeObject(MapCommonData.getInstance().getLocationHashMap().get(mapName));
			oos.flush();
			oos.close();
		} catch (Exception e) {
		} finally {

		}
	}

	@SuppressWarnings("unchecked")
	public static void loadLocationHashMap(Context context, String mapName) {
		AssetManager am = context.getAssets();
		try {
			InputStream is = am.open("LocationPoint/" + mapName + LOCATIONHASHMAP_FILENAME);
			ObjectInputStream ois = new ObjectInputStream(is);
			MapCommonData.getInstance().getLocationHashMap().put(mapName, (HashMap<String, Location>) ois.readObject());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static void loadLocationHashMap(String mapName) {
		try {
			InputStream is = new FileInputStream(new File(SDCARD_DIR + "LocationPoint/" + mapName + LOCATIONHASHMAP_FILENAME));
			ObjectInputStream ois = new ObjectInputStream(is);
			MapCommonData.getInstance().getLocationHashMap().put(mapName, (HashMap<String, Location>) ois.readObject());
		} catch (Exception e) {
		}
	}

	@SuppressWarnings("unchecked")
	public static void mergeLocationHashMap(String mapName) {
		try {
			InputStream is = new FileInputStream(new File(SDCARD_DIR + "LocationPoint/" + mapName + LOCATIONHASHMAP_FILENAME));
			ObjectInputStream ois = new ObjectInputStream(is);
			HashMap<String, Location> m1 = (HashMap<String, Location>) ois.readObject();

			InputStream is2 = new FileInputStream(new File(SDCARD_DIR + "LocationPoint/" + mapName + LOCATIONHASHMAP_FILENAME + "_2"));
			ObjectInputStream ois2 = new ObjectInputStream(is2);
			HashMap<String, Location> m2 = (HashMap<String, Location>) ois2.readObject();

			HashMap<String, Location> m3 = new HashMap<String, Location>();

			m3.putAll(m1);
			m3.putAll(m2);

			MapCommonData.getInstance().getLocationHashMap().put(mapName, m3);

			saveLocationHashMap(mapName);

		} catch (Exception e) {
		}
	}

	// -------------------------------动态POI工具-------------------------------

	public static void saveDynamicHashMap(String mapName) {
		File file1 = new File(SDCARD_DIR + "DynamicPOI/");
		if (!file1.exists()) {
			file1.mkdirs();
		}
		File f = new File(SDCARD_DIR + "DynamicPOI/" + mapName + DYNAMICPOIHASHMAP_FILENAME);
		if (f.exists()) {
			f.delete();
		}

		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
			oos.writeObject(MapCommonData.getInstance().getDynamicPoiHashMap().get(mapName));
			oos.flush();
			oos.close();
		} catch (Exception e) {
		} finally {

		}
	}

	public static void deleteDynamicFolder(){
		File file1 = new File(SDCARD_DIR + "DynamicPOI/");
		if (file1.exists()) {
			file1.delete();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void loadDynamicHashMap(String mapName) {
		try {
			InputStream is = new FileInputStream(new File(SDCARD_DIR + "DynamicPOI/" + mapName + DYNAMICPOIHASHMAP_FILENAME));
			ObjectInputStream ois = new ObjectInputStream(is);
			MapCommonData.getInstance().getDynamicPoiHashMap().put(mapName, (HashMap<String, DynamicPOI>) ois.readObject());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// -------------------------------地图矩阵工具-------------------------------

	public static void saveMapMatrix(String mapName) {
		File file1 = new File(SDCARD_DIR + "MapMatrix/");
		if (!file1.exists()) {
			file1.mkdirs();
		}
		File f = new File(SDCARD_DIR + "MapMatrix/" + mapName + MAPMATRIX_FILENAME);
		if (f.exists()) {
			f.delete();
		}

		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
			oos.writeObject(MapCommonData.getInstance().getMapHashMap().get(mapName).getMapMatrix());
			oos.flush();
			oos.close();
		} catch (Exception e) {
		} finally {

		}
	}

	public static void loadMapMatrix(String mapName) {
		try {
			InputStream is = new FileInputStream(new File(SDCARD_DIR + "MapMatrix/" + mapName + MAPMATRIX_FILENAME));
			ObjectInputStream ois = new ObjectInputStream(is);
			MapCommonData.getInstance().getMapHashMap().get(mapName).setMapMatrix((byte[][]) ois.readObject());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static byte[][] getMapMatrix(Context context, String mapName) {
		byte[][] result = null;

		AssetManager am = context.getAssets();
		try {
			InputStream is = am.open("MapMatrix/" + mapName + MAPMATRIX_FILENAME);
			ObjectInputStream ois = new ObjectInputStream(is);
			result = (byte[][]) ois.readObject();
		} catch (Exception e) {
		}
		return result;
	}

	public static byte[][] getMapMatrix(String mapName) {
		byte[][] result = null;
		try {
			InputStream is = new FileInputStream(new File(SDCARD_DIR + "MapMatrix/" + mapName + MAPMATRIX_FILENAME));
			ObjectInputStream ois = new ObjectInputStream(is);
			result = (byte[][]) ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// ----------------------------区域工具----------------------------------
	public static void saveZoneHashMap(String mapName) {
		File file1 = new File(SDCARD_DIR + "Zone/");
		if (!file1.exists()) {
			file1.mkdirs();
		}
		File f = new File(SDCARD_DIR + "Zone/" + mapName + ZONEHASHMAP_FILENAME);
		if (f.exists()) {
			f.delete();
		}

		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
			oos.writeObject(MapCommonData.getInstance().getZoneHashMap().get(mapName));
			oos.flush();
			oos.close();
		} catch (Exception e) {
		} finally {

		}
	}

	@SuppressWarnings("unchecked")
	public static void loadZoneHashMap(Context context, String mapName) {
		AssetManager am = context.getAssets();
		try {
			InputStream is = am.open("Zone/" + mapName + ZONEHASHMAP_FILENAME);
			ObjectInputStream ois = new ObjectInputStream(is);
			MapCommonData.getInstance().getZoneHashMap().put(mapName, (HashMap<String, Zone>) ois.readObject());
		} catch (Exception e) {
		}
	}

	@SuppressWarnings("unchecked")
	public static void loadZoneHashMap(String mapName) {
		try {
			InputStream is = new FileInputStream(new File(SDCARD_DIR + "Zone/" + mapName + ZONEHASHMAP_FILENAME));
			ObjectInputStream ois = new ObjectInputStream(is);
			MapCommonData.getInstance().getZoneHashMap().put(mapName, (HashMap<String, Zone>) ois.readObject());
		} catch (Exception e) {
		}
	}

	// ----------------------------pushmessage
	// 工具----------------------------------
	public static void savePushMessage() {
		File file1 = new File(SDCARD_DIR + "PushMessage/");
		if (!file1.exists()) {
			file1.mkdirs();
		}
		File f = new File(SDCARD_DIR + "PushMessage/" + PUSHMESSAGEHASHMAP_FILENAME);
		if (f.exists()) {
			f.delete();
		}

		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
			oos.writeObject(MapCommonData.getInstance().getPushMessageHashMap());
			oos.flush();
			oos.close();
		} catch (Exception e) {
		} finally {

		}
	}

	@SuppressWarnings("unchecked")
	public static void loadPushMessageHashMap() {
		try {
			InputStream is = new FileInputStream(new File(SDCARD_DIR + "PushMessage/" + PUSHMESSAGEHASHMAP_FILENAME));
			ObjectInputStream ois = new ObjectInputStream(is);
			MapCommonData.getInstance().setPushMessage((HashMap<String, PushMessage>) ois.readObject());
		} catch (Exception e) {
		}
	}

	// ----------------------------bitmap 工具----------------------------------

	public static void saveCacheBitmap(String bitmapName, Bitmap bitmap) {
		File file1 = new File(SDCARD_DIR + "BitmapCache/");
		if (!file1.exists()) {
			file1.mkdirs();
		}
		File f = new File(SDCARD_DIR + "BitmapCache/" + bitmapName);
		if (f.exists()) {
			f.delete();
		}

		try {
			FileOutputStream fos = new FileOutputStream(f);

			bitmap.compress(CompressFormat.PNG, 50, fos);

			fos.flush();

			fos.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}

	public static Bitmap loadCacheBitmap(String bitmapName) {
		Bitmap bitmap = null;
		try {
			InputStream is = new FileInputStream(new File(SDCARD_DIR + "BitmapCache/" + bitmapName));
			bitmap = BitmapFactory.decodeStream(is);
//			ObjectInputStream ois = new ObjectInputStream(is);
//			bitmap = (Bitmap) ois.readObject();
		} catch (Exception e) {
		}
		return bitmap;

	}

}
