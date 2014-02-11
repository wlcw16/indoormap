package com.indoormap.framework.widget;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.indoormap.R;
import com.indoormap.framework.common.MapCommonData;
import com.indoormap.framework.screen.base.BaseMapScreen;

public class FloorChangeDialog extends Dialog{

	private Context context;
	
	private LinearLayout floorLl;
	
	public FloorChangeDialog(Context context) {
		super(context, R.style.spinner_dialog);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.floor_change_dialog);
		findView();
	}
	
	public void init(List<String> inputList , final BaseMapScreen bms){
		floorLl.removeAllViews();
		for(final String floor:inputList){
			TextView tv = new TextView(context);
			tv.setText(floor);
			tv.setTextSize(30);
			tv.setTextColor(Color.BLACK);
			tv.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					bms.changeMap(MapCommonData.getInstance().getMapHashMap().get(floor));
					cancel();
				}
			});
			floorLl.addView(tv);
		}
	}
	
	private void findView(){
		floorLl = (LinearLayout)findViewById(R.id.floor_change_dialog_floor_ll);
		
	}
	
}
