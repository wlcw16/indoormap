package com.indoormap.framework.model;

public class RSSInfo
{
    public String ssid;
    public String bssid;
    public int rss;
    
    public String toString()
    {
        return bssid + ";" + rss;
    }
    
    public String getAllInfo()
    {
        // return ssid + ":" + bssid + ":" + rss;
        return bssid + ";" + rss + ";" + ssid;
    }
}
