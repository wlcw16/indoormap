package com.indoormap.framework.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.indoormap.R;

public class DefineDialog extends Dialog {

	public static final int OK = 1;

	public static final int ERROR = 2;

	public static final int WARNING = 3;

	private int mode;

	private String message;

	private OnDefineDialogClickListener onDefineDialogClickListener;

	private ImageView iconIv;

	private TextView msgIv;

	private Button btn1;

	private Button btn2;

	private String btn1Str;

	private String btn2Str;

	public DefineDialog(Context context, int mode, String message, OnDefineDialogClickListener onDefineDialogClickListener, String btn1, String btn2) {
		super(context, R.style.dialog);
		this.mode = mode;
		this.message = message;
		this.btn1Str = btn1;
		this.btn2Str = btn2;
		this.onDefineDialogClickListener = onDefineDialogClickListener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.define_dialog);
		findView();
		init();
	}

	private void init() {
		switch (mode) {
		case OK:
			iconIv.setImageResource(R.drawable.icon_finish);
			break;
		case ERROR:
			iconIv.setImageResource(R.drawable.icon_error);

			break;
		case WARNING:
			iconIv.setImageResource(R.drawable.icon_warning);
			break;

		}
		if (null != btn1Str) {
			btn1.setText(btn1Str);
		} else {
			btn1.setVisibility(View.GONE);
		}
		if (null != btn2Str) {
			btn2.setText(btn2Str);
		} else {
			btn2.setVisibility(View.GONE);
		}
		msgIv.setText(message);
		btn1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (null != onDefineDialogClickListener) {
					onDefineDialogClickListener.onButton1Click();
				}
			}
		});
		btn2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (null != onDefineDialogClickListener) {
					onDefineDialogClickListener.onButton2Click();
				}
			}
		});
	}

	public void setButton1(String message) {
		if (btn1 != null) {

			btn1.setVisibility(View.VISIBLE);
			btn1.setText(message);
		}
	}

	public void setButton2(String message) {
		if (btn2 != null) {
			btn2.setVisibility(View.VISIBLE);
			btn2.setText(message);
		}
	}
	
	public void updateMessage(String message){
		if(msgIv!=null){
			msgIv.setText(message);
		}
	}

	private void findView() {
		iconIv = (ImageView) findViewById(R.id.define_dialog_icon_iv);
		msgIv = (TextView) findViewById(R.id.define_dialog_message_tv);
		btn1 = (Button) findViewById(R.id.define_dialog_btn1_btn);
		btn2 = (Button) findViewById(R.id.define_dialog_btn2_btn);

	}

	public interface OnDefineDialogClickListener {
		public void onButton1Click();

		public void onButton2Click();
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public OnDefineDialogClickListener getOnDefineDialogClickListener() {
		return onDefineDialogClickListener;
	}

	public void setOnDefineDialogClickListener(OnDefineDialogClickListener onDefineDialogClickListener) {
		this.onDefineDialogClickListener = onDefineDialogClickListener;
	}

}
