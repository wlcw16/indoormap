package com.indoormap.framework.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Map;

import android.util.Log;

public class UdpUtil
{
    
    // 超时时间设定
    private static int udpRecvWaitTime = 5000;
	private static final String CODE_MODE = "UTF-8";
    
    // 发送UDP请求串
    public static String send(String ip, int port, Map<String, String> postParam)
    {
        if (null == postParam)
        {
            return null;
        }
        
        StringBuffer sb = new StringBuffer();
        for (String key : postParam.keySet())
        {
            sb.append(key).append("=").append(postParam.get(key)).append("&");
        }
        if (sb.length() > 1)
        {
            sb.deleteCharAt(sb.length() - 1);
        }
        
        return send(ip, port, sb.toString(), false);
    }
    
    // 发送UDP请求串
    public static String send(String ip, int port, String content)
    {
        return send(ip, port, content, false);
    }
    
    // 发送UDP请求串
    private static String send(String ip, int port, String content, boolean isrepeat)
    {
        
        DatagramSocket datagramSocket = null;
        String resp = null;
        try
        {
            datagramSocket = new DatagramSocket();
            
            // 发送数据
            sendData(ip, port, content, datagramSocket);
           // NdcLog.d(TAG, "SEND DATA:" + port);
            
            // 获取响应
            resp = getResponse(datagramSocket);
           // NdcLog.d(TAG, "RESP DATA:" + port);
            
        }
        catch (IOException e)
        {
            
           // NdcLog.d(TAG, "IOException:" + e.getMessage());
            
            if (isrepeat)
            {
                resp = null;
            }
            else
            {
                // NdcLog.d(TAG, "START REPEAT:" + port);
                // 重试
                return send(ip, port, content, true);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (null != datagramSocket)
            {
                datagramSocket.close();
            }
            
        }
        
        return resp;
    }
    
    /**
     * 获取相应结果
     * 
     * @param ds
     * @return
     * @throws SocketException
     * @throws IOException
     * @see [类、类#方法、类#成员]
     */
    private static String getResponse(DatagramSocket ds)
        throws SocketException, IOException
    {
        byte[] recevieData = new byte[1024*64];
        DatagramPacket recvPacket = new DatagramPacket(recevieData, recevieData.length);
        
        // 设定超时时间
        ds.setSoTimeout(4000);
        
        // 接收数据
        ds.receive(recvPacket);
        
        ByteBuffer buffer = ByteBuffer.allocate(recvPacket.getLength());
        
        buffer.put(recvPacket.getData(), 0, recvPacket.getLength());
        
        // 解码
        // byte data[] = DecodeComm.getObjectByte(buffer.array());
        // String resp = HexUtil.bytestoString(data);
        
        // String resp = HexUtil.bytestoString(buffer.array());
        
        // String resp = buffer.toString();

        // String resp = HexUtil.EncodeUtf8ByteToString(buffer.array());
		String resp = new String(buffer.array(), CODE_MODE);
        
        return resp;
    }
    
    /**
     * 发送消息
     * 
     * @param ip 发送的IP
     * @param port 端口
     * @param content 内容
     * @param ds
     * @throws UnknownHostException
     * @throws IOException
     * @see [类、类#方法、类#成员]
     */
    private static void sendData(String ip, int port, String content, DatagramSocket ds)
        throws UnknownHostException, IOException
    {
        InetAddress serverAddr = InetAddress.getByName(ip);
        
        Log.d("TAG", content);
		byte data[] = content.getBytes(CODE_MODE);
        
        // byte sendMesag[] = EncodeComm.packagesStream(data);
        byte sendMesag[] = data;
        
        DatagramPacket dp = new DatagramPacket(sendMesag, sendMesag.length, serverAddr, port);
        ds.setSoTimeout(udpRecvWaitTime);
        ds.send(dp);
    }
    
}
