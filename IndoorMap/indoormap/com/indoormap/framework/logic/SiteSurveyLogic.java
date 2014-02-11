package com.indoormap.framework.logic;

import java.util.ArrayList;
import java.util.List;

import android.net.wifi.ScanResult;
import android.util.Log;

import com.indoormap.framework.model.RSSInfo;
import com.indoormap.framework.util.ConnectivityUtil;
import com.indoormap.framework.util.WiFiUtil;

public class SiteSurveyLogic
{
    public static String TAG = "SiteSurveyLogiuc";
    
    public static long waitConnectTimeOut = 5 * 1000;
    
    public static String TargetMeRequestURLBase = "http://42.120.52.246:86/targetme.do?method=";
    
    public static String serverurl = "http://42.120.52.246:86";
    
    public static long lastGatherTime = 0;
    
    public static long minGatherTimeInternal = 12;
    
    // 192.168.0.102
    // public static String TargetMeRequestURLBase =
    // "http://192.168.0.102:86/targetme.do?method=";
    
    // 42.120.52.246
    public static boolean waitConnectOK(boolean ifWifiEnable)
    {
        long time1 = System.currentTimeMillis();
        
        while (true)
        {
            if ((ifWifiEnable && ConnectivityUtil.isWiFiActive()) || ConnectivityUtil.isEdgeActive())
            {
                break;
            }
            else
            {
                
                try
                {
                    Thread.sleep(200);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                
                long time2 = System.currentTimeMillis();
                if (time2 - time1 >= waitConnectTimeOut)
                {
                    break;
                }
            }
        }
        
        return true;
    }
    
    // 扫描AP信息
    public static ArrayList<RSSInfo> getScanResult()
    {
        boolean isWifiEnable = WiFiUtil.isWifiEnabled();
        
        Log.d(TAG, "开始扫描，请稍后");
        List<ScanResult> scanResultList = WiFiUtil.getAPListBySsid(null);
        Log.d("TAG", "扫描完成，输出扫描结果");
        
        if (!isWifiEnable)
        {
            WiFiUtil.closeWifi();
        }
        
        ArrayList<RSSInfo> rssInfoList = new ArrayList<RSSInfo>();
        for (int i = 0; i < scanResultList.size(); i++)
        {
            RSSInfo rssInfo = new RSSInfo();
            ScanResult scanResult = scanResultList.get(i);
            rssInfo.ssid = scanResult.SSID;
            rssInfo.bssid = scanResult.BSSID;
            rssInfo.rss = scanResult.level;
            
            rssInfoList.add(rssInfo);
        }
        
        return rssInfoList;
    }
    
  
}
