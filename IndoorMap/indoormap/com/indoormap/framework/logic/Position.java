package com.indoormap.framework.logic;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.indoormap.framework.common.MapCommonConstant;
import com.indoormap.framework.exception.ServiceException;
import com.indoormap.framework.model.RSSInfo;
import com.indoormap.framework.util.UdpUtil;
import com.indoormap.framework.util.WiFiUtil;

public class Position {

	private static final String POSITION_ADDRESS = "42.120.52.246";
	
	private static final int POSITION_PROT = 8999;
	
	public static final String OK = "ok";

	public static final String ERROR = "error";

	public static final String TIMEOUT = "timeout";
	
	private static int locationIndex1 = 0;
	
	private static int locationIndex2 = 0;
	
	private static boolean isDemoMode = false;
	
	public static List<String> locationList1 = new ArrayList<String>();
	static {
		// locationList1.add("tsinghua_yfl_2hf_04");
		locationList1.add("tsinghua_yfl_1f_08");
		locationList1.add("tsinghua_yfl_1f_07");
		locationList1.add("tsinghua_yfl_1f_06");
		locationList1.add("tsinghua_yfl_1f_03");
		locationList1.add("tsinghua_yfl_1f_04");
		locationList1.add("tsinghua_yfl_1f_20");
		locationList1.add("tsinghua_yfl_1f_01");
		locationList1.add("tsinghua_yfl_1f_02");
	}

	public static List<String> locationList2 = new ArrayList<String>();
	static {
		locationList2.add("tsinghua_yfl_1f_01");
		locationList2.add("tsinghua_yfl_1f_02");
		locationList2.add("tsinghua_yfl_1f_03");
		locationList2.add("tsinghua_yfl_1f_04");
		locationList2.add("tsinghua_yfl_1f_05");
		locationList2.add("tsinghua_yfl_1f_06");
		locationList2.add("tsinghua_yfl_1f_07");
		locationList2.add("tsinghua_yfl_1f_08");
		locationList2.add("tsinghua_yfl_1f_09");
	}

	public static void getCurrentLocationStr(final Handler handler) {
		getCurrentLocationStr(handler, -1);
	}

	public static void getCurrentLocationStr(final Handler handler, final int from) {
		Thread scanThread = new Thread() {

			public void run() {
				Looper.prepare();

				try {
					String responeStr = "";
					if (isDemoMode) {
						Thread.sleep(1000);
						if (from == 1000) {
							responeStr = getImitateStr2();
						} else {
							responeStr = getImitateStr1();
						}
					} else {
						// 1，获取周围AP 信息
						ArrayList<RSSInfo> rssInfoList = getAPList();
						responeStr = getLocationTitle(rssInfoList);
					}

					Log.i(this.getClass().getName(), responeStr);
					Bundle bundle = new Bundle();
					bundle.putString(MapCommonConstant.FLAG, OK);
					bundle.putInt(MapCommonConstant.FROM, from);
					bundle.putString(MapCommonConstant.MESSAGE, responeStr);
					Message message = Message.obtain();
					message.setData(bundle);
					Log.i(this.getClass().getName(), "发送handler时间 : " + System.currentTimeMillis());

					handler.sendMessage(message);

				} catch (Exception e) {
					Bundle bundle = new Bundle();
					bundle.putString(MapCommonConstant.FLAG, ERROR);
					bundle.putString(MapCommonConstant.MESSAGE, e.getMessage());
					Message message = Message.obtain();
					message.setData(bundle);
					handler.sendMessage(message);
				}

			}

		};
		scanThread.start();
	}

	public static ArrayList<RSSInfo> getAPList() throws ServiceException {
		ArrayList<RSSInfo> rssInfoList = SiteSurveyLogic.getScanResult();

		if (rssInfoList.size() == 0) {
			throw new ServiceException(-1, "周围无wifi，请重新开关wifi");
		}
		return rssInfoList;
	}

	public static String getLocationTitle(ArrayList<RSSInfo> rssInfoList) throws ServiceException {

		boolean ifWifiEnable = WiFiUtil.isWifiEnabled();

		SiteSurveyLogic.waitConnectOK(ifWifiEnable);

		String responeStr;
		String reportStr = rssInfoList.toString();
		reportStr = reportStr.replace("[", "");
		reportStr = reportStr.replace("]", "");

		responeStr = UdpUtil.send(POSITION_ADDRESS, POSITION_PROT, "get_rp:" + reportStr);
		Log.d("TAG", "UDP:" + responeStr);

		if (responeStr == null) {
			throw new ServiceException(-1, "网络连接错误，请检查网络");
		} else {
			String split[] = responeStr.split("\\|");
			if (split.length == 2) {
				responeStr = split[0];
			} else {
				throw new ServiceException(-1, "无可用参考点");
			}
		}
		// 对一些加权点进行筛选，返回加权点所表示的location
		if (responeStr.indexOf("-") != -1) {
			responeStr = responeStr.split("-")[0];
		}

		return responeStr;
	}

	public static String getImitateStr1() {
		String result = "";
		if (locationIndex1 <= locationList1.size() - 1) {
			result = locationList1.get(locationIndex1);
			locationIndex1++;
		} else {
			locationIndex1 = 0;
			result = locationList1.get(locationIndex1);
		}
		// result = list.get(a);

		return result;
	}

	public static String getImitateStr2() {
		String result = "";
		if (locationIndex2 <= locationList2.size() - 1) {
			result = locationList2.get(locationIndex2);
			locationIndex2++;
		} else {
			locationIndex2 = 0;
			result = locationList2.get(locationIndex2);
		}

		return result;
	}
}
