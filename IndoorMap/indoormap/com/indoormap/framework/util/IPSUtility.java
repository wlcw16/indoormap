package com.indoormap.framework.util;

import java.util.ArrayList;
import java.util.List;

import android.net.wifi.ScanResult;
import android.util.Log;

import com.indoormap.framework.model.RSSInfo;

public class IPSUtility
{
    public static long waitConnectTimeOut = 5 * 1000;
    
    public static boolean waitConnectOK(boolean ifWifiEnable)
    {
        long time1 = System.currentTimeMillis();
        
        while(true)
        {
            if(( ifWifiEnable && ConnectivityUtil.isWiFiActive()) || ConnectivityUtil.isEdgeActive())
            {
                break;
            }else {
                
                try
                {
                    Thread.sleep(200);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                
                long time2 = System.currentTimeMillis();
                if(time2 - time1 >= waitConnectTimeOut)
                {
                    break;
                }
            }
        }
        
        return true;
    }
    
    public static ArrayList<RSSInfo> getScanResult()
    {
        Log.d("TAG", "开始扫描，请稍后");
        List<ScanResult> scanResultList = WiFiUtil.getAPListBySsid(null);
        Log.d("TAG", "扫描完成，输出扫描结果");
        
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
