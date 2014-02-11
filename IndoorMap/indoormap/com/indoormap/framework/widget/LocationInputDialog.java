package com.indoormap.framework.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.indoormap.R;
import com.indoormap.framework.model.Location;
import com.indoormap.framework.model.Map;
import com.indoormap.framework.model.Point;
import com.indoormap.framework.model.RSSInfo;
import com.indoormap.framework.util.IPSUtility;
import com.indoormap.framework.util.UdpUtil;
import com.indoormap.framework.util.WiFiUtil;

public class LocationInputDialog extends Dialog {

	private EditText idEt;

	private EditText xEt;

	private EditText yEt;

	private EditText mapEt;

	private Button saveBtn;

	private Button modifyBtn;

	private Button deleteBtn;

	private Button cancelBtn;

	private Button collectBtn;
	
	private Button resetBtn;
	
	private Button sendBtn;
	
	private Map map;

	private long lastLocationTime;

    public static long locInternalTime = 5 * 1000;

    public static String remoteIP = "42.120.52.246";
    
    public static int remotePort = 8999;
    
	private OnLocationInputDialogClickListener onLocationInputDialogClickListener;

	private DefineProgressDialog defineProgressDialog;
	
	public HashMap<Long, String> reportIPInfoMap = new HashMap<Long, String>();
	
	private Handler progressHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle bundle = msg.getData();
			 if(bundle!=null){
				 String message = bundle.getString("msg");
				 defineProgressDialog.updateMsg(message);
			 }
		}
	};
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			Bundle bundle = msg.getData();
            if(bundle!=null){
                String flag = (String)bundle.get("flag");
                if (flag.contains("finishsetrp"))
                {
        			defineProgressDialog.cancel();
                    showSetRPDialog();
                    lastLocationTime = System.currentTimeMillis();
                    sendBtn.setText("发送("+reportIPInfoMap.size()+")");
                    if(reportIPInfoMap.size()==10){
                    	sendBtn.setEnabled(true);
                    	collectBtn.setEnabled(false);
                    }
                    if(reportIPInfoMap.size()>0){
                    	resetBtn.setEnabled(true);
                    }
                    return;
                }
                if (flag.contains("scanwifing"))
                {
        			defineProgressDialog.cancel();
                    lastLocationTime = System.currentTimeMillis();
                    
                    return;
                }
                if (flag.contains("endreport"))
                {
        			defineProgressDialog.cancel();
        			int mapSize = reportIPInfoMap.size();
        			String alertInfo = "";
                    if( mapSize > 0)
                    {
                        alertInfo = "仍旧有" + mapSize + "条数据未上报， 请继续上报";
                        collectBtn.setEnabled(false);
                        resetBtn.setEnabled(false);
                        sendBtn.setEnabled(true);
                    }else {
                        alertInfo = "全部上报完毕";
                        reportIPInfoMap = new HashMap<Long, String>();
                        collectBtn.setEnabled(true);
                        resetBtn.setEnabled(false);
                        sendBtn.setEnabled(false);
                    }
                    new AlertDialog.Builder(getContext()).setMessage(alertInfo).setPositiveButton("确定", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialoginterface, int i)
                        {
                            // 按钮事件
                        }
                    }).show();
                    sendBtn.setText("发送("+reportIPInfoMap.size()+")");
                    return;
                }
            }
			
			defineProgressDialog.cancel();
		};
	};
	
	public LocationInputDialog(Context context) {
		super(context, R.style.spinner_dialog);
		this.setCanceledOnTouchOutside(false);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_input_dialog);
		findView();
		init();
	}

	private void findView() {
		idEt = (EditText) findViewById(R.id.location_input_dialog_id_ev);
		xEt = (EditText) findViewById(R.id.location_input_dialog_x_ev);
		yEt = (EditText) findViewById(R.id.location_input_dialog_y_ev);
		mapEt = (EditText) findViewById(R.id.location_input_dialog_map_ev);
		saveBtn = (Button) findViewById(R.id.location_input_dialog_save_btn);
		modifyBtn = (Button) findViewById(R.id.location_input_dialog_modify_btn);
		deleteBtn = (Button) findViewById(R.id.location_input_dialog_delete_btn);
		cancelBtn = (Button) findViewById(R.id.location_input_dialog_cancel_btn);
		collectBtn =  (Button) findViewById(R.id.location_input_dialog_collect_btn);
		resetBtn =  (Button) findViewById(R.id.location_input_dialog_clear_btn);
		sendBtn =  (Button) findViewById(R.id.location_input_dialog_report_btn);
	}

	private void init() {
		defineProgressDialog = new DefineProgressDialog(getContext());
		cancelBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (null != onLocationInputDialogClickListener) {
					onLocationInputDialogClickListener.onCancelClick();
				}
			}
		});
		initLocationButtons();
	}

	private void initLocationButtons(){
		collectBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String placeInfo = idEt.getEditableText().toString();
                if(placeInfo.length() == 0)
                {
                    new AlertDialog.Builder(getContext()).setMessage("请填写位置信息").setPositiveButton("确定", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialoginterface, int i)
                        {
                            
                        }
                    }).show();
                    
                    return;
                }
                
                if(defineProgressDialog == null)
                {
                	defineProgressDialog = new DefineProgressDialog(getContext());
                	defineProgressDialog.setCancelable(false);
                }
                
                defineProgressDialog.show();
                defineProgressDialog.updateMsg("开始采集");
                
                Thread scanThread = new Thread()
                {                    
                    public void run()
                    {
                        Looper.prepare();
                        
                        // 两次之间有个采集间隔，采集更多情况的Wifi情况
                        long currTime = System.currentTimeMillis();
                        while (currTime <= lastLocationTime + locInternalTime)
                        {
                            try
                            {
                                Thread.sleep(1000);
                            }
                            catch (InterruptedException e)
                            {
                                e.printStackTrace();
                            }
                            Bundle bundle1 = new Bundle();
                            bundle1.putString("msg", "请等待"+((int)((lastLocationTime + locInternalTime-currTime)/1000))+"秒");
                            Message message1 = Message.obtain();
                            message1.setData(bundle1);
                            progressHandler.sendMessage(message1);
                            currTime = System.currentTimeMillis();
                        }
                        Bundle bundle2 = new Bundle();
                        bundle2.putString("msg", "开始采集");
                        Message message2 = Message.obtain();
                        message2.setData(bundle2);
                        progressHandler.sendMessage(message2);
                        currTime = System.currentTimeMillis();
                        boolean ifWifiEnable = WiFiUtil.isWifiEnabled();
                        ArrayList<RSSInfo> rssInfoList = IPSUtility.getScanResult();
                        
                        if(!ifWifiEnable)
                        {
                            WiFiUtil.closeWifi();
                        }
                        
                        Log.d("TAG", rssInfoList.toString());
                        String placeInfo = idEt.getEditableText().toString();
                        
                        if(rssInfoList.size() == 0)
                        {
                            
                            
                            Bundle bundle1 = new Bundle();
                            bundle1.putString("flag", "scanwifing");
                            Message message1 = Message.obtain();
                            message1.setData(bundle1);
                            handler.sendMessage(message1);

                            new AlertDialog.Builder(getContext()).setMessage("扫描WIFI失败").setPositiveButton("确定", new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialoginterface, int i)
                                {
                                    // 按钮事件
                                }
                            }).show();
                            
                            return;
                        }
                        
                        /*
                        String reportStr = rssInfoList.toString();
                        reportStr = reportStr.replace("[", "");
                        reportStr = reportStr.replace("]", "");
                        */
                        
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 0; i < rssInfoList.size(); i++) {
                            
                            RSSInfo localRssInfo = rssInfoList.get(i);                                
                            if(i < rssInfoList.size() -1)
                            {
                                stringBuilder.append(localRssInfo.getAllInfo() + ",");
                            }else {
                                stringBuilder.append(localRssInfo.getAllInfo());
                            }                                
                        }
                        
                        String reportStr = stringBuilder.toString();                        
                        String finalReportStr = "set_rp:" + placeInfo + "|" + reportStr;
                        
                        // 缓存到内存Cache去
                        // long currTime = System.currentTimeMillis();
                        reportIPInfoMap.put(currTime, finalReportStr);
                        
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("flag", "finishsetrp");
                        Message message1 = Message.obtain();
                        message1.setData(bundle1);
                        handler.sendMessage(message1);
                    }
                };
                
                scanThread.start();
				
			}
		});
		resetBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				reportIPInfoMap = new HashMap<Long, String>();
				collectBtn.setEnabled(true);
				sendBtn.setEnabled(false);
				sendBtn.setText("发送("+reportIPInfoMap.size()+")");
				lastLocationTime = 0;
			}
		});
		sendBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				 if(defineProgressDialog == null)
	                {
	                	defineProgressDialog = new DefineProgressDialog(getContext());
	                	defineProgressDialog.setCancelable(false);
	                }
	                
	                defineProgressDialog.show();
	                defineProgressDialog.updateMsg("开始上报");
	                
				
				Thread reportThread = new Thread()
                {
                    public void run()
                    {
//                    	for(int i = 0 ; i < 3 ; i++){
                    		report();
//                    		if(reportIPInfoMap.size()==0){
//                    			break;
//                    		}
//                    	}
                        
//                    	reportIPInfoMap = new HashMap<Long, String>();
                        Bundle bundle2 = new Bundle();
                        bundle2.putString("flag", "endreport");
                        Message message2 = Message.obtain();
                        message2.setData(bundle2);
                        handler.sendMessage(message2);
                        
                    }
                };
                
                reportThread.start();
			}
		});
		resetBtn.setEnabled(false);
		sendBtn.setEnabled(false);
	}
	
	private void report() {
		boolean ifWifiEnable = WiFiUtil.isWifiEnabled();
        IPSUtility.waitConnectOK(ifWifiEnable);
        
        ArrayList<Long> reportedKeyList = new ArrayList<Long>();
        
        Iterator<?> iter = reportIPInfoMap.entrySet().iterator();
        while (iter.hasNext()) {
            @SuppressWarnings("rawtypes")
			java.util.Map.Entry entry = (java.util.Map.Entry) iter.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            
            String reportInfo = (String) val;
            Bundle bundle2 = new Bundle();
            bundle2.putString("msg", "正在上报："+reportInfo);
            Message message2 = Message.obtain();
            message2.setData(bundle2);
            progressHandler.sendMessage(message2);
            String result = UdpUtil.send(remoteIP, remotePort, reportInfo);
            if(result == null)
            {
            }else{
                reportedKeyList.add((Long)key);
            }
        }
        
        for (int i = 0; i < reportedKeyList.size(); i++)
        {
            Long key = reportedKeyList.get(i);
            reportIPInfoMap.remove(key);
        }
        
        reportedKeyList.clear();
	}
	
	public void add(Map map, Point point) {
		collectBtn.setEnabled(false);
        reportIPInfoMap = new HashMap<Long, String>();
        sendBtn.setText("发送("+reportIPInfoMap.size()+")");
        resetBtn.setEnabled(false);
        sendBtn.setEnabled(false);
		this.map = map;
		String id = map.getMapName()+"_";
		idEt.setText(id);
		idEt.setEnabled(true);
		mapEt.setText(map.getMapName());
		xEt.setText(point.getX() + "");
		yEt.setText(point.getY() + "");
		xEt.setEnabled(false);
		yEt.setEnabled(false);
		saveBtn.setVisibility(View.VISIBLE);
		modifyBtn.setVisibility(View.GONE);
		deleteBtn.setVisibility(View.GONE);

		saveBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (null != onLocationInputDialogClickListener) {
					String[] temp = idEt.getText().toString().split("_");
					
					if(null == temp || temp.length!=4 || null == temp[3]||"".equals(temp[3])){
						Toast.makeText(getContext(), "请检查id格式", Toast.LENGTH_SHORT).show();
						return;
					}
					Location location = new Location(idEt.getText().toString(), LocationInputDialog.this.map.getMapName(), LocationInputDialog.this.map.getFloor(), new Point(Float.valueOf(xEt.getText().toString()), Float.valueOf(yEt.getText().toString()), LocationInputDialog.this.map));
					onLocationInputDialogClickListener.onSaveClick(location);
				}
			}
		});

	}

	public void modify(final Location location) {
		collectBtn.setEnabled(true);
        reportIPInfoMap = new HashMap<Long, String>();
        sendBtn.setText("发送("+reportIPInfoMap.size()+")");
        resetBtn.setEnabled(false);
        sendBtn.setEnabled(false);
		idEt.setText(location.getName());
		idEt.setEnabled(false);
		mapEt.setText(location.getMapName());
		xEt.setText(location.getPoint().getX() + "");
		yEt.setText(location.getPoint().getY() + "");
		xEt.setEnabled(true);
		yEt.setEnabled(true);
		saveBtn.setVisibility(View.GONE);
		modifyBtn.setVisibility(View.VISIBLE);
		deleteBtn.setVisibility(View.VISIBLE);
		
		modifyBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (null != onLocationInputDialogClickListener) {
					String oldName = location.getName();
					String name = location.getName();
//					String temp[] = name.split("_");
//					name = "";
//					for(int i = 0; i < 3;i++){
//						name = name + temp[i] + "_";
//					}
//					name = name + xEt.getText().toString()+"_"+yEt.getText().toString();
					location.setName(name);
					location.getPoint().setX(Float.valueOf(xEt.getText().toString()));
					location.getPoint().setY(Float.valueOf(yEt.getText().toString()));
					
					onLocationInputDialogClickListener.onModifyClick(oldName,location);
				}
			}
		});

		deleteBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (null != onLocationInputDialogClickListener) {
					onLocationInputDialogClickListener.onDeleteClick(location);
				}
				
			}
		});
	}

	 protected void showSetRPDialog()
	    {        
	        new AlertDialog.Builder(getContext()).setMessage("采集完毕").setPositiveButton("确定", new DialogInterface.OnClickListener()
	        {
	            public void onClick(DialogInterface dialoginterface, int i)
	            {
	                // 按钮事件
	            }
	        }).show();
	    }
	
	public void setOnLocationInputDialogClickListener(OnLocationInputDialogClickListener onLocationInputDialogClickListener) {
		this.onLocationInputDialogClickListener = onLocationInputDialogClickListener;
	}

	public OnLocationInputDialogClickListener getOnLocationInputDialogClickListener() {
		return onLocationInputDialogClickListener;
	}

	public interface OnLocationInputDialogClickListener {
		public void onSaveClick(Location location);

		public void onModifyClick(String oldName , Location location);

		public void onDeleteClick(Location location);

		public void onCancelClick();
	}
}
