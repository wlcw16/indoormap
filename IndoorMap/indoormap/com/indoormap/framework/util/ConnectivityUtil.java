package com.indoormap.framework.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.util.Log;

/**
 * 
 * 用来判断 WIFI EDGE 是否可用
 * 
 * @author zhuweiliang
 * @version [版本号, 2012-10-21]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class ConnectivityUtil
{
    
    private static ConnectivityManager connectivity;
    
    public static void init(Context context)
    {
        connectivity = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }
    
    /**
     * 判断当前wifi是否连接状态
     * 
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static boolean isWiFiActive()
    {
        
        NetworkInfo wifiNetInfo = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        
        if (wifiNetInfo != null)
        {
            if (wifiNetInfo.isConnected())
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 判断当前wifi是否连接状态
     * 
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static boolean isEdgeActive()
    {
        
        NetworkInfo wifiNetInfo = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        
        if (wifiNetInfo != null)
        {
            if (wifiNetInfo.isConnected())
            {
                Log.d("TAG", "isEdgeActive TRUE");
                return true;
            }
        }
        
        return false;
    }
    
    // 判断wifi 连接状态
    public static String getWifiConnectState()
    {
        
        NetworkInfo wifiNetInfo = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        
        if (null != wifiNetInfo)
        {
            State wifi = wifiNetInfo.getState();
            String state = wifi.toString(); // 显示wifi连接状态
            
            return state;
        }
        
        return null;
    }
    
}
