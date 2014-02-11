package com.indoormap.framework.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;


/**
 * wifi工具类
 * 
 * @author lw
 * 
 */
public class WiFiUtil
{
    
    public static String TAG = "WifiUtil";
    
    /** 定义WifiManager对象 */
    private static WifiManager wifiManager;
    
    private static WifiScanListener wifiListener;
    
    private static Context context;
    
    private static List<ScanResult> apList = new ArrayList<ScanResult>();
    
    public static void init(Context context)
    {
        WiFiUtil.context = context;
        wifiManager = (WifiManager)context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }
    
    public static WifiManager getWifiManager()
    {
        return wifiManager;
    }
    
    /**
     * 获取wifi开关状态
     * 
     * @return
     */
    public static boolean isWifiEnabled()
    {
        
        return wifiManager.isWifiEnabled();
        
    }
    
    /**
     * 获取Wifi连接状态
     * 
     * @return
     */
    public static int getWifiState()
    {
        
        return wifiManager.getWifiState();
        
    }
    
    /**
     * 获取Wifi连接信息
     * 
     * @return
     */
    public static WifiInfo getConnectionInfo()
    {
        return wifiManager.getConnectionInfo();
        
    }
    
    public static int getConnectStatus(int configID)
    {
        int status = wifiManager.getConfiguredNetworks().get(configID).status;
        
        return status;
    }
    
    public static void setWifiMng(WifiManager serviceWifi)
    {
        wifiManager = serviceWifi;
    }
    
    /**
     * 打开wifi
     * 
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static boolean openWifi()
    {
        
        if (isWifiEnabled())
        {
            return true;
        }
        // LogManager.info(LogConstant.Try_to_open_WIFI, "尝试开启WIFI");
        boolean re = wifiManager.setWifiEnabled(true);
        int num = 0;
        while (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING)
        {
            try
            {
                // 为了避免程序一直while循环，让它睡个100毫秒在检测……
                Thread.sleep(100);
                num++;
                if (num >= 10 * 20)
                {
                 // LogManager.info(LogConstant.REPORT_MSG, "WIFI失败");
                    break;
                }
            }
            catch (InterruptedException ie)
            {
            }
        }
     // LogManager.info(LogConstant.WIFI_Open_Success, "WIFI 开启成功");
        return true;
    }
    
    /**
     * 关闭wifi
     * 
     * @see [类、类#方法、类#成员]
     */
    public static void closeWifi()
    {
        closeWifi(false);
    }
    
    /**
     * 关闭wifi
     * 
     * @see [类、类#方法、类#成员]
     */
    public static void closeWifi(boolean iswait)
    {
        
        // check if WiFi connect
        if (wifiManager.isWifiEnabled() || wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING)
        {
         // LogManager.info(LogConstant.WIFI_DISABLED, "尝试关闭WIFI");
            wifiManager.setWifiEnabled(false);
            // 不需要等待这直接退出
            if (!iswait)
            {
                return;
            }
            
            int num = 0;
            while (wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLING)
            {
                if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED)
                {
                    break;
                }
                
                try
                {
                    // 为了避免程序一直while循环，让它睡个100毫秒在检测……
                    Thread.sleep(100);
                    num++;
                    if (num >= 10 * 5)
                    {
                     // LogManager.info(LogConstant.REPORT_MSG, "关闭WIFI失败");
                        break;
                    }
                }
                catch (InterruptedException ie)
                {
                }
            }
            
         // LogManager.info(LogConstant.WIFI_DISABLED, "关闭WIFI");
            
        }
        
    }
    
    /**
     * 连上指定的SSID
     * 
     * @param ssid
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static boolean connectWifiBySSID(String ssid)
    {
        
        // LogManager.info(LogConstant.REPORT_MSG, "开始接入" +
        // NDCConfigInfo.DefaultSSID);
        
        String defaultssid = "\"" + ssid + "\"";
        
        WifiConfiguration targetConfig = getWifiConfigBySSID(ssid);
        // 如果热点没有配置信息，则增加配置信息
        if (null == targetConfig)
        {
            /* Create a WifiConfig */
            WifiConfiguration selectedConfig = new WifiConfiguration();
            selectedConfig.SSID = defaultssid;
            selectedConfig.status = WifiConfiguration.Status.ENABLED;
            selectedConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            selectedConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            selectedConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            selectedConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            selectedConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            selectedConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            selectedConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            selectedConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            selectedConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            int res = wifiManager.addNetwork(selectedConfig);
            wifiManager.saveConfiguration();
            wifiManager.enableNetwork(res, true);
            targetConfig = getWifiConfigBySSID(ssid);
            if (null == targetConfig)
            {
             // LogManager.info(LogConstant.REPORT_MSG, ssid + " has not Configuration");
                return false;
            }
        }
        if (isWifiConnected(ssid))
        {
            return true;
        }
        
        // 接入指定的热点
        enableNetwork(targetConfig.networkId);
        
        long beginTime = System.currentTimeMillis();
        while (true)
        {
            
            sleep(200);
            
            if (System.currentTimeMillis() - beginTime >= 1000 * 20)
            {
             // LogManager.info(LogConstant.ConnectWifiTimeOut, "热点接入超时");
                return false;
            }
            if (!isWifiEnabled())
            {
                // 如果WIFI 关闭，则推出
                return false;
            }
            
            if (isWifiConnected(defaultssid))
            {
                wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL, "123");
                return true;
            }
            
            // 连接失败
            WifiInfo connection = getConnectionInfo();
            if (!ssid.equals(connection.getSSID()))
            {
                enableNetwork(targetConfig.networkId);
            }
            
            if (connection.getSupplicantState() == SupplicantState.DISCONNECTED || connection.getSupplicantState() == SupplicantState.SCANNING)
            {
                
                wifiManager.reconnect();
                
                enableNetwork(targetConfig.networkId);
                
                continue;
                
            }
            
        }
        
        // return false;
        
    }
    
    private static boolean isWifiConnected(String defaultssid)
    {
        defaultssid = defaultssid.replace("\"", "");
        WifiInfo connection = getConnectionInfo();
        // 如果当前wifi 连接的是指定的热点，而且是wifi是活跃状态，则认为接入成功
        if (defaultssid.equals(connection.getSSID()) && ConnectivityUtil.isWiFiActive())
        {
         // NdcLog.d(TAG, "cmccWifi isWiFiActive");
            // LogManager.info(LogConstant.REPORT_MSG, "接入" + defaultssid +
            // "成功");
            return true;
        }
        
        return false;
    }
    
    public static boolean enableNetwork(int networkId)
    {
        boolean flag = wifiManager.enableNetwork(networkId, true);
        
        return flag;
    }
    
    /**
     * 毫秒为单位 <功能详细描述>
     * 
     * @param mm
     * @see [类、类#方法、类#成员]
     */
    private static void sleep(int mm)
    {
        // 稍微延时，增加接入概率
        try
        {
            Thread.sleep(mm);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * 扫描周围可用的AP
     * 
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static List<ScanResult> getAPListBySsid(String ssid)
    {
        startMonitorWifiScan();
        List<ScanResult> result = new ArrayList<ScanResult>();
        
        try
        {
            openWifi();
            
            result = startScan(ssid);
        }
        finally
        {
            stopMonitorWifiScan();
        }
        
        return result;
    }
    
    /**
     * 根据SSID 筛选
     * 
     * @param ssid
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static List<ScanResult> getAllAPResult()
    {        
        StringBuffer sb = new StringBuffer();
        
        List<ScanResult> result = new ArrayList<ScanResult>();
        apList = wifiManager.getScanResults();
        if (apList == null)
        {
            // LogManager.info(LogConstant.REPORT_MSG, "level:" + sb.toString());
            return result;
        }
        
        // 处理扫描结果
        for (ScanResult scanInfo : apList)
        {            
            sb.append(scanInfo.level).append(",");            
            result.add(scanInfo);
            
        }
        
        // LogManager.info(LogConstant.REPORT_MSG, "l=" + sb.toString());
        return result;
    }
    
    /**
     * 根据SSID 筛选
     * 
     * @param ssid
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static List<ScanResult> filterAp(String ssid, boolean ischeckLevel)
    {
        
        List<ScanResult> result = new ArrayList<ScanResult>();
        apList = wifiManager.getScanResults();
        if (apList == null)
        {
            return result;
        }
        
        // 处理扫描结果
        for (ScanResult scanInfo : apList)
        {
            if (!ssid.equals(scanInfo.SSID))
            {
                continue;
            }
            
            result.add(scanInfo);
        }
        
        return result;
    }
    
    /**
     * 扫描
     * 
     * @see [类、类#方法、类#成员]
     */
    public static List<ScanResult> startScan(String ssid)
    {
        wifiListener.listenerSate = -1;
        
        wifiManager.startScan();
        long begintime = System.currentTimeMillis();
        Log.d(TAG, "startScan");
        
        List<ScanResult> result = new ArrayList<ScanResult>();
        int retryNumber = 0;
        
        // 等待扫描结束
        while (true)
        {
            if (wifiListener.listenerSate == 1 || System.currentTimeMillis() - begintime >= 3500)
            {               
                result = getAllAPResult();
                
                Log.d(TAG, "listenerSate=" + wifiListener.listenerSate + " size=" + result.size() + ":" + retryNumber);
                
                if(result.size() > 0)
                {
                    if(ssid != null)
                    {
                        for (int i = 0; i < result.size(); i++)
                        {
                            ScanResult scanResult = result.get(i);
                            if(scanResult.SSID.equals(ssid))
                            {
                                return result;
                            }
                        }
                    }else{
                        break;
                    }
                }else {
                    
                    // 重试一次
                    if(retryNumber ++ > 1)
                    {
                        break;
                    }
                    
                    sleep(1000);
                    continue;
                }
            }
            
            if (System.currentTimeMillis() - begintime >= 10 * 1000)
            {
                break;
            }
            
            sleep(100);
        }

        Log.d(TAG, "endScan");
                
        return result;
    }
    
    /**
     * 根据SSID 获取连接配置
     * 
     * @param ssid
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static WifiConfiguration getWifiConfigBySSID(String ssid)
    {
        WifiConfiguration targetConfig = null;
        
        List<WifiConfiguration> configs = null;
        
        int loopNum = 0;
        while (loopNum++ < 15)
        {
            configs = wifiManager.getConfiguredNetworks();
            if (configs.size() == 0)
            {
                try
                {
                    Thread.sleep(200);
                }
                catch (InterruptedException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                continue;
            }
            else
            {
                break;
            }
        }
        
        for (WifiConfiguration i : configs)
        {
            if (i.SSID != null && i.SSID.equals("\"" + ssid + "\""))
            {
                targetConfig = i;
                // i.status = WifiConfiguration.Status.DISABLED;
                break;
            }
        }
        return targetConfig;
    }
    
    public static boolean saveEapConfig(String ssid, String bssid, String userName, String passString)
    {
        
     // NdcLog.d(TAG, "Call saveEapConfig");
        
        final String ENTERPRISE_EAP = "PEAP";
        
        /* Create a WifiConfig */
        WifiConfiguration selectedConfig = new WifiConfiguration();
        
        /* AP Name */
        selectedConfig.SSID = "\"" + ssid + "\"";
        selectedConfig.BSSID = bssid;
        /* Key Mgmnt */
        selectedConfig.allowedKeyManagement.clear();
        selectedConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
        selectedConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X);
        
        /* Group Ciphers */
        selectedConfig.allowedGroupCiphers.clear();
        selectedConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        selectedConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        selectedConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
        selectedConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        
        /* Pairwise ciphers */
        selectedConfig.allowedPairwiseCiphers.clear();
        selectedConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        selectedConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        
        /* Protocols */
        selectedConfig.allowedProtocols.clear();
        
        selectedConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        selectedConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        
        // selectedConfig.priority = NDCConfigInfo.maxWIFIPriority;
        
        Class[] wcClasses = WifiConfiguration.class.getClasses();
        
        Class wcEnterpriseField = null;
        
        for (Class wcClass : wcClasses)
        {
            
            if (wcClass.getName().indexOf("EnterpriseField") != -1)
            {
                wcEnterpriseField = wcClass;
                break;
            }
        }
        
        Field wcefEap = null, wcefIdentity = null, wcefPassword = null;
        Field[] wcefFields = WifiConfiguration.class.getFields();
        // Dispatching Field vars
        for (Field wcefField : wcefFields)
        {
            
            if (wcefField.getName().equals("eap"))
            {
                wcefEap = wcefField;
            }
            
            // EAP-PEAP 配置的用户名，也就是手机号码
            else if (wcefField.getName().equals("identity"))
            {
                wcefIdentity = wcefField;
            }
            
            // 认证密码
            else if (wcefField.getName().equals("password"))
            {
                wcefPassword = wcefField;
            }
            
        }
        
        Method wcefSetValue = null;
        
        if (wcEnterpriseField != null)
        {
            for (Method m : wcEnterpriseField.getMethods())
            {
                System.out.println("methodName--->" + m.getName());
                if (m.getName().trim().equals("setValue"))
                {
                    wcefSetValue = m;
                    break;
                }
                
            }
        }
        try
        {
            /* EAP Method */
            if (wcEnterpriseField != null)
            {
                wcefSetValue.invoke(wcefEap.get(selectedConfig), ENTERPRISE_EAP);
                
                wcefSetValue.invoke(wcefIdentity.get(selectedConfig), userName);
                
                wcefSetValue.invoke(wcefPassword.get(selectedConfig), passString);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        boolean result = addNetwork(selectedConfig);
        return result;
        
    }
    
    // 添加一个网络并连接
    public static boolean addNetwork(WifiConfiguration selectedConfig)
    {
        
        // boolean res1 = wifiManager.setWifiEnabled(true);
        int res = wifiManager.addNetwork(selectedConfig);
     // NdcLog.d("WifiPreference", "add Network returned " + res);
        boolean b = wifiManager.enableNetwork(selectedConfig.networkId, false);
     // NdcLog.d("WifiPreference", "enableNetwork returned " + b);
        boolean c = wifiManager.saveConfiguration();
     // NdcLog.d("WifiPreference", "Save configuration returned " + c);
        boolean d = wifiManager.enableNetwork(res, true);
     // NdcLog.d("WifiPreference", "enableNetwork returned " + d);
        return c;
    }
    
    // 开始监听wifi扫描信息
    public static void startMonitorWifiScan()
    {
        if (null == wifiListener)
        {
            wifiListener = new WifiScanListener();
        }
        
        // 标识开始监听
        wifiListener.listenerSate = 0;
        
        context.registerReceiver(wifiListener, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }
    
    /*
    // 开始监听wifi扫描信息
    public static void startMonitorSUPPLICANT_STATE_CHANGED()
    {
        if (null == wifiListener)
        {
            wifiListener = new WifiScanListener();
        }
        
        // 标识开始监听
        wifiListener.listenerSate = 0;
        
        IntentFilter filter1 = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter1.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        filter1.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        SystemService.getInstance().registerReceiver(wifiListener, filter1);
        
    }
    
    // 停止监听wifi扫描信息
    public static void stopMonitorSUPPLICANT_STATE_CHANGED()
    {
        context.unregisterReceiver(wifiListener);
    }
    */
    
    // 停止监听wifi扫描信息
    public static void stopMonitorWifiScan()
    {
        context.unregisterReceiver(wifiListener);
    }
    
    static class WifiScanListener extends BroadcastReceiver
    {
        public int listenerSate = -1;
        
        @Override
        public void onReceive(Context context, Intent intent)
        {
            
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
            {
                
                Log.d(TAG, "Finish Wifi Scan");
                
                // 获取扫描的结果
                List<ScanResult> wifiList = wifiManager.getScanResults();
                
                apList = wifiList;
                
                // 标识已经扫描到结果
                listenerSate = 1;
            }
            
            if (intent.getAction().equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION))
            {
                SupplicantState s = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
                
                // LogManager.info(LogConstant.REPORT_MSG, "wifiState2=" +
                // getSupplicantStateText(s) + "|" +
                // WifiInfo.getDetailedStateOf(s));
                
            }
            
        }
        
    }
    
    /**
     * 连上指定的SSID
     * 
     * @param ssid
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static boolean connectWifiBySSID1(String ssid)
    {
        
        // LogManager.info(LogConstant.REPORT_MSG, "开始接入" +
        // NDCConfigInfo.DefaultSSID);
        
        String defaultssid = "\"" + ssid + "\"";
        
        WifiConfiguration targetConfig = getWifiConfigBySSID(ssid);
        
        // 如果热点没有配置信息，则增加配置信息
        // 检查有
        targetConfig = checkConfig(ssid, defaultssid, targetConfig);
        
       //wifiManager.removeNetwork(targetConfig.networkId);
       
        // 接入指定的热点
        // enableNetwork(targetConfig.networkId);
        
        //sleep(2000);
        
        long beginTime = System.currentTimeMillis();
        while (true)
        {
            
            sleep(200);
            
            if (null == targetConfig)
            {
                targetConfig = checkConfig(ssid, defaultssid, targetConfig);
                
                continue;
            }
            
            if (System.currentTimeMillis() - beginTime >= 1000 * 30)
            {
                return false;
            }
            if (!isWifiEnabled())
            {
                // 如果WIFI 关闭，则推出
                return false;
            }
            
            if (isWifiConnected(defaultssid))
            {
                wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL, "123");
                return true;
            }
            
            // 连接失败
            WifiInfo connection = getConnectionInfo();
            if (!ssid.equals(connection.getSSID()))
            {
                enableNetwork(targetConfig.networkId);
            }
            
            if (connection.getSupplicantState() == SupplicantState.DISCONNECTED || connection.getSupplicantState() == SupplicantState.SCANNING)
            {
                
                wifiManager.reconnect();
                
                enableNetwork(targetConfig.networkId);
                
                continue;
                
            }
            
        }
        
        // return false;
        
    }
    
    private static WifiConfiguration checkConfig(String ssid, String defaultssid, WifiConfiguration targetConfig)
    {
        if (null == targetConfig)
        {
            /* Create a WifiConfig */
            WifiConfiguration selectedConfig = new WifiConfiguration();
            selectedConfig.SSID = defaultssid;
            selectedConfig.status = WifiConfiguration.Status.ENABLED;
            selectedConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            selectedConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            selectedConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            selectedConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            selectedConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            selectedConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            selectedConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            selectedConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            selectedConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            int res = wifiManager.addNetwork(selectedConfig);
            wifiManager.saveConfiguration();
            wifiManager.enableNetwork(res, true);
            targetConfig = getWifiConfigBySSID(ssid);
            if (null == targetConfig)
            {
            }
        }
        return targetConfig;
    }
    
    /**
     * 获取国际化<一句话功能简述> <功能详细描述>
     * 
     * @param id
     * @return
     * @see [类、类#方法、类#成员]
     */
    /*
    private static String getLocalString(int id)
    {
        
        return SystemService.getInstance().getString(id);
        
    }
    */
}
