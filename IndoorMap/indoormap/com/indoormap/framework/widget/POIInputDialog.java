package com.indoormap.framework.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.indoormap.R;
import com.indoormap.framework.common.MapCommonData;
import com.indoormap.framework.model.Map;
import com.indoormap.framework.model.POI;
import com.indoormap.framework.model.Point;

public class POIInputDialog extends Dialog {

	
	private EditText idEt;

	private EditText xEt;

	private EditText yEt;

	private EditText mapEt;

	private EditText nameEt;

	private Button saveBtn;

	private Button modifyBtn;

	private Button deleteBtn;
	
	private Button cancelBtn;

	private Spinner typeSp;

	private Spinner iconSp;

	private EditText startEt;

	private EditText endEt;

	private Point point;

	private Map map;

	private Context context;

	private int type;
	
	private String icon;
	
	private OnPOIInputDialogClick onPOIInputDialogClick;

	private static final float START_VISIBALE = 99f;
	
	private static final float END_VISIBALE = 99f;
	
	private static final String[] teypDatas = { "文字", "路标", "图标" };
	private static final String[] iconDatas = { POI.ENTRANCE_EXIT, POI.ELEVATOR, POI.STAIRS, POI.INFOMATION_DESK, POI.TOILET,POI.ATM,POI.ESCALATOR,POI.GATE,POI.GUARD,POI.FIRE};

	public POIInputDialog(Context context) {
		super(context, R.style.spinner_dialog);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.poi_input_dialog);
		findView();
		init();
	}

	private void findView() {
		idEt = (EditText) findViewById(R.id.poi_input_dialog_id_ev);
		xEt = (EditText) findViewById(R.id.poi_input_dialog_x_ev);
		yEt = (EditText) findViewById(R.id.poi_input_dialog_y_ev);
		mapEt = (EditText) findViewById(R.id.poi_input_dialog_map_ev);
		nameEt = (EditText) findViewById(R.id.poi_input_dialog_name_ev);
		saveBtn = (Button) findViewById(R.id.poi_input_dialog_save_btn);
		modifyBtn = (Button)findViewById(R.id.poi_input_dialog_modify_btn);
		deleteBtn = (Button)findViewById(R.id.poi_input_dialog_delete_btn);
		cancelBtn = (Button) findViewById(R.id.poi_input_dialog_cancel_btn);
		typeSp = (Spinner) findViewById(R.id.poi_input_dialog_type_sp);
		iconSp = (Spinner) findViewById(R.id.poi_input_dialog_icon_sp);
		startEt = (EditText) findViewById(R.id.poi_input_dialog_range_start_ev);
		endEt = (EditText) findViewById(R.id.poi_input_dialog_range_end_ev);
	}

	private void init(){
		ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, teypDatas);
		typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		typeSp.setAdapter(typeAdapter);
		typeSp.setOnItemSelectedListener(new TypeListener());
		ArrayAdapter<String> iconAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, iconDatas);
		iconAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		iconSp.setAdapter(iconAdapter);
		iconSp.setOnItemSelectedListener(new IconListener());
		cancelBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (null != onPOIInputDialogClick) {
					onPOIInputDialogClick.onCancelClick();
				}
			}
		});
	}
	
	public void add(Map map, Point point) {
		this.map = map;
		this.point = point;
		String id = map.getMapName()+"_"  + point.getX()+"_"+point.getY();
		idEt.setText(id);
		mapEt.setText(map.getMapName());
		xEt.setText(point.getX() + "");
		yEt.setText(point.getY() + "");
		nameEt.setText("");
		
		int arg2 = typeSp.getSelectedItemPosition();
		if(arg2!=2){
			iconSp.setEnabled(false);
			if(arg2==0){
				startEt.setText("0.4");
			}
		}else{
			iconSp.setEnabled(true);
			if(arg2==2){
				startEt.setText(String.valueOf(START_VISIBALE));
			}
			
		}
		
		endEt.setText(String.valueOf(END_VISIBALE));
		
		saveBtn.setVisibility(View.VISIBLE);
		modifyBtn.setVisibility(View.GONE);
		deleteBtn.setVisibility(View.GONE);
		
		saveBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (null != onPOIInputDialogClick) {
					POI poi = new POI();
					poi.setId(idEt.getText().toString());
					poi.setPoint(POIInputDialog.this.point);
					poi.setMapName(POIInputDialog.this.map.getMapName());
					poi.setPoiMode(type);
					poi.setIcon(icon);
					poi.setStartVisibilityLevel(Float.valueOf(startEt.getText().toString()));
					poi.setEndVisibilityLevel(Float.valueOf(endEt.getText().toString()));
					poi.setName(nameEt.getText().toString());
					onPOIInputDialogClick.onSaveClick(poi);
				}
			}
		});
		

	}

	public void modify(final POI poi){
		idEt.setText(poi.getId());
		mapEt.setText(poi.getMapName());
		xEt.setText(poi.getPoint().getX() + "");
		yEt.setText(poi.getPoint().getY() + "");
		nameEt.setText(poi.getName());
		startEt.setText(poi.getStartVisibilityLevel()+"");
		endEt.setText(poi.getEndVisibilityLevel()+"");
		
		type = poi.getPoiMode();
		icon = poi.getIcon();
		
		typeSp.setSelection(type);
		for(int i = 0 ; i < iconDatas.length ; i++){
			if(iconDatas[i].equals(icon)){
				iconSp.setSelection(i);
			}
		}
		
		saveBtn.setVisibility(View.GONE);
		modifyBtn.setVisibility(View.VISIBLE);
		deleteBtn.setVisibility(View.VISIBLE);
		
		
		modifyBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (null != onPOIInputDialogClick) {
					String oldName = poi.getId();
					String name = poi.getId();
					String temp[] = name.split("_");
					name = "";
					for(int i = 0; i < 3;i++){
						name = name + temp[i] + "_";
					}
					name = name + xEt.getText().toString()+"_"+yEt.getText().toString();
					poi.setId(name);
					
//					poi.setId(idEt.getText().toString());
					poi.setPoint(new Point(Float.valueOf(xEt.getText().toString()), Float.valueOf(yEt.getText().toString()),MapCommonData.getInstance().getMapHashMap().get(poi.getMapName())));
					poi.setMapName(mapEt.getText().toString());
					poi.setPoiMode(type);
					poi.setIcon(icon);
					poi.setStartVisibilityLevel(Float.valueOf(startEt.getText().toString()));
					poi.setEndVisibilityLevel(Float.valueOf(endEt.getText().toString()));
					poi.setName(nameEt.getText().toString());
					onPOIInputDialogClick.onModifyClick(oldName,poi);
				}
			}
		});
		
		deleteBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (null != onPOIInputDialogClick) {
					onPOIInputDialogClick.onDeleteClick(poi);
				}
			}
		});
	}
	
	public void setOnPOIInputDialogClick(OnPOIInputDialogClick onPOIInputDialogClick) {
		this.onPOIInputDialogClick = onPOIInputDialogClick;
	}

	public OnPOIInputDialogClick getOnPOIInputDialogClick() {
		return onPOIInputDialogClick;
	}

	public interface OnPOIInputDialogClick {
		public void onSaveClick(POI poi);
		public void onModifyClick(String oldId , POI poi);
		public void onDeleteClick(POI poi);
		public void onCancelClick();
	}

	class TypeListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			type = arg2;
			if(arg2!=2){
				iconSp.setEnabled(false);
				if(arg2==0){
					startEt.setText("0");
				}
			}else{
				iconSp.setEnabled(true);
				if(arg2==2){
					startEt.setText("0");
				}
				
			}
		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

	class IconListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			icon = iconDatas[arg2];
		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}
}
