package com.indoormap.framework.widget;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.indoormap.R;
import com.indoormap.framework.model.Location;
import com.indoormap.framework.model.Map;
import com.indoormap.framework.model.Point;
import com.indoormap.framework.model.Zone;

public class ZoneEditDialog extends Dialog {

	private EditText idEt;
	
	private EditText mapEt;

	private EditText desEt;

	private LinearLayout edgeLl;
	private LinearLayout wifiLl;
	
	private LinearLayout disLl;
	
	private Button modifyBtn;

	private Button deleteBtn;

	private Map map;

	private Zone zone;
	
	private OnZoneEditDialogClickListener zoneEditDialogClickListener;

	
	public ZoneEditDialog(Context context) {
		super(context, R.style.spinner_dialog);
		this.setCanceledOnTouchOutside(false);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zone_edit_dialog);
		findView();
		init();
	}

	private void findView() {
		idEt = (EditText) findViewById(R.id.zone_edit_dialog_id_ev);
		mapEt = (EditText) findViewById(R.id.zone_edit_dialog_map_ev);
		desEt = (EditText) findViewById(R.id.zone_edit_dialog_description_ev);
		edgeLl = (LinearLayout) findViewById(R.id.zone_edit_dialog_edge_ll);
		wifiLl = (LinearLayout) findViewById(R.id.zone_edit_dialog_location_ll);
		disLl = (LinearLayout) findViewById(R.id.zone_edit_dialog_display_ll);
		modifyBtn = (Button) findViewById(R.id.zone_edit_dialog_modify_btn);
		deleteBtn = (Button) findViewById(R.id.zone_edit_dialog_delete_btn);
	}

	private void init() {
		
	}

	
	
	
	

	public void modify(final Zone zone) {
		this.zone = zone;
		idEt.setText(zone.getId());
		idEt.setEnabled(false);
		mapEt.setText(zone.getMapName());
		if(zone.getDescription()==null||"".equals(zone.getDescription())){
			desEt.setText("");
		}else{
			desEt.setText(zone.getDescription());
		}
		modifyBtn.setVisibility(View.VISIBLE);
		deleteBtn.setVisibility(View.VISIBLE);
		initEdgeLl();
		initWifiLl();
		initDisLl();
		modifyBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				zone.setDescription(desEt.getText().toString());
				if (null != zoneEditDialogClickListener) {
					zoneEditDialogClickListener.onModifyClick(zone);
				}
				cancel();
			}
		});

		deleteBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (null != zoneEditDialogClickListener) {
					zoneEditDialogClickListener.onDeleteClick(zone);
				}
				cancel();
			}
		});
	}

	private void initEdgeLl(){
		edgeLl.removeAllViews();
		if(zone.getEdgeList()!=null&&zone.getEdgeList().size()>0){
			for(int i = 0 ; i < zone.getEdgeList().size();i++){
				final Point p = zone.getEdgeList().get(i);
				map = p.getMap();
				LinearLayout ll = new LinearLayout(getContext());
				final EditText xEt = new EditText(getContext());
				xEt.setTextColor(Color.BLACK);
				final EditText yEt = new EditText(getContext());
				yEt.setTextColor(Color.BLACK);
				Button b = new Button(getContext());
				b.setText("ÐÞ¸Ä");
				ll.addView(xEt);
				ll.addView(yEt);
				ll.addView(b);
				xEt.setText(p.getX()+"");
				yEt.setText(p.getY()+"");
				b.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						p.setX(Float.valueOf(xEt.getText().toString()));
						p.setY(Float.valueOf(yEt.getText().toString()));
					}
				});
				edgeLl.addView(ll);
				
			}
		}
	}
	
	private void initWifiLl(){
		if(zone.getLocationList()!=null){
			wifiLl.removeAllViews();
			for(final Location l : zone.getLocationList().values()){
				LinearLayout ll = new LinearLayout(getContext());
				TextView tv = new TextView(getContext());
				tv.setTextColor(Color.BLACK);
				Button b = new Button(getContext());
				b.setText("É¾³ý");
				ll.addView(tv);
				ll.addView(b);
				tv.setText(l.getName());
				b.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						zone.getLocationList().remove(l.getName());
						initWifiLl();
					}
				});
				wifiLl.addView(ll);
			}
		}
	}
	
	ArrayList<EditText> xL = new ArrayList<EditText>();
	ArrayList<EditText> yL = new ArrayList<EditText>();
	
	
	private void initDisLl(){
		disLl.removeAllViews();
		xL.clear();
		yL.clear();
		if(zone.getDisplayList()!=null&&zone.getDisplayList().size()>0){
			for(int i = 0 ; i < zone.getDisplayList().size();i++){
				final Point p = zone.getDisplayList().get(i);
				map = p.getMap();
				LinearLayout ll = new LinearLayout(getContext());
				final EditText xEt = new EditText(getContext());
				xEt.setTextColor(Color.BLACK);
				final EditText yEt = new EditText(getContext());
				yEt.setTextColor(Color.BLACK);
				Button b = new Button(getContext());
				b.setText("É¾³ý");

				Button b1 = new Button(getContext());
				b1.setText("ÐÞ¸Ä");
				ll.addView(xEt);
				ll.addView(yEt);
				ll.addView(b);
				ll.addView(b1);
				xL.add(xEt);
				yL.add(yEt);
				xEt.setText(p.getX()+"");
				yEt.setText(p.getY()+"");
				final int index = i;
				b.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						zone.getDisplayList().remove(index);
						xL.remove(index);
						yL.remove(index);
						initDisLl();
					}
				});
				b1.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						p.setX(Float.valueOf(xEt.getText().toString()));
						p.setY(Float.valueOf(yEt.getText().toString()));
					}
				});
				disLl.addView(ll);
				
			}
		}
	}


	public void setZoneEditDialogClickListener(OnZoneEditDialogClickListener zoneEditDialogClickListener) {
		this.zoneEditDialogClickListener = zoneEditDialogClickListener;
	}

	public OnZoneEditDialogClickListener getZoneEditDialogClickListener() {
		return zoneEditDialogClickListener;
	}

	public interface OnZoneEditDialogClickListener {

		public void onModifyClick(Zone zone);

		public void onDeleteClick(Zone zone);
	}
}
