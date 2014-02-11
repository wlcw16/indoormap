package com.indoormap.framework.util;

import android.content.Context;
import android.telephony.TelephonyManager;

public class TelephoneUtil {
	private static Context mContext;
	private static TelephonyManager telephonyMng;

	public static void init(Context context) {
		mContext = context;
		telephonyMng = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
	}
	public static String getImei(){
		String result = "";
		if(null!=telephonyMng){
			result = telephonyMng.getDeviceId();
		}
		return result;
	}
	public static String getImsi(){
		String result = "";
		if(null!=telephonyMng){
			result = telephonyMng.getSubscriberId(); 
			if(result==null){
				result = "";
			}
		}
		return result;
	}
}
