package com.indoormap.framework.widget;

import java.io.IOException;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.indoormap.R;
import com.indoormap.framework.common.MapCommonUtil;
import com.indoormap.framework.model.DynamicPOI;

public class DynamicPOIDialog extends Dialog {

	private LinearLayout mainLl;

	private TextView titleTv;

	private Button closeBtn;

	private RelativeLayout imgRl;

	private ImageView logoIv;

	private TextView opentimeTv;

	private TextView locationTv;

	private ScrollView infoSv;

	private TextView infoTv;

	private Button signBtn;

	private DynamicPOI dynamicPOI;

	private String curLocation;

	private Bitmap curBitmap;

	private Handler bitmapHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			// logoIv.setImageBitmap(curBitmap);
			imgRl.setBackground(new BitmapDrawable(getContext().getResources(), curBitmap));
			logoIv.setVisibility(View.GONE);
		};
	};

	public DynamicPOIDialog(Context context) {
		super(context, R.style.dynamic_poi_dialog);
		this.setCanceledOnTouchOutside(false);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dynamic_poi_dialog);
		findView();
		init();
	}

	private void init() {
		initSize();
	}

	private void initSize() {
		int width = (int) (MapCommonUtil.getScreenWidth(getContext()) * 0.9f);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, LayoutParams.WRAP_CONTENT);
		mainLl.setLayoutParams(lp);

		int heightImg = (int) (MapCommonUtil.getScreenHeight(getContext()) * 0.3f);
		LinearLayout.LayoutParams lpImg = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, heightImg);
		lpImg.leftMargin = 10;
		lpImg.rightMargin = 10;
		lpImg.topMargin = 10;
		lpImg.bottomMargin = 10;
		imgRl.setLayoutParams(lpImg);

		int heightInfo = (int) (MapCommonUtil.getScreenHeight(getContext()) * 0.3f);
		LinearLayout.LayoutParams lpInfo = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, heightInfo);
		lpInfo.leftMargin = 10;
		lpInfo.rightMargin = 10;
		lpInfo.topMargin = 10;
		lpInfo.bottomMargin = 10;
		infoSv.setLayoutParams(lpInfo);

	}

	public void init(DynamicPOI dynamicPOI, String location) {
		this.dynamicPOI = dynamicPOI;
		this.curLocation = location;
		initImg();
		initListener();
		initText();
		// mainLl.setVisibility(View.GONE);
		startFlyInAnimation();
	}

	private void initListener() {
		if (dynamicPOI.getTriggerLocations().indexOf(curLocation) == -1) {
			signBtn.setEnabled(false);
		}else{
			signBtn.setEnabled(true);
		}
		signBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//TODO
				startFlyOutAnimation();
			}
		});
		closeBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startFlyOutAnimation();
			}
		});
	}

	private void initText() {
		titleTv.setText(dynamicPOI.getTitle());
		opentimeTv.setText(dynamicPOI.getOpenTime());
		locationTv.setText(dynamicPOI.getLocation());
		infoTv.setText(dynamicPOI.getDetail());
	}

	private void initImg() {
		logoIv.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				try {
					String tempName = dynamicPOI.getPicPath().split("&")[0].replace("/", "_");
					curBitmap = MapCommonUtil.loadCacheBitmap(tempName);
					if(curBitmap==null){
						curBitmap = MapCommonUtil.getBitmapFromInternet(dynamicPOI.getPicPath().split("&")[0]);
						if(curBitmap!=null){
							MapCommonUtil.saveCacheBitmap(tempName, curBitmap);
						}
					}
					bitmapHandler.sendEmptyMessage(0);
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			};
		}.start();
	}

	private void findView() {
		mainLl = (LinearLayout) findViewById(R.id.dynamic_poi_dialog_main_ll);
		titleTv = (TextView) findViewById(R.id.dynamic_poi_dialog_title_tv);
		closeBtn = (Button) findViewById(R.id.dynamic_poi_dialog_close_btn);
		imgRl = (RelativeLayout) findViewById(R.id.dynamic_poi_dialog_img_rl);
		logoIv = (ImageView) findViewById(R.id.dynamic_poi_dialog_logo_iv);
		opentimeTv = (TextView) findViewById(R.id.dynamic_poi_dialog_opentime_tv);
		locationTv = (TextView) findViewById(R.id.dynamic_poi_dialog_location_tv);
		infoSv = (ScrollView) findViewById(R.id.dynamic_poi_dialog_info_sv);
		infoTv = (TextView) findViewById(R.id.dynamic_poi_dialog_info_tv);
		signBtn = (Button) findViewById(R.id.dynamic_poi_dialog_sign_btn);
	}

	@Override
	public void cancel() {
		super.cancel();
		// logoIv.setImageResource(R.drawable.dialog_pic_logo);
		imgRl.setBackgroundResource(R.drawable.dialog_pic_placeholder);
		if (null != curBitmap) {
			curBitmap.recycle();
		}
	}

	private void startFlyInAnimation() {
		final ScaleAnimation sa1 = new ScaleAnimation(1, 1, 0, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0);
		sa1.setDuration(300);
		final ScaleAnimation sa2 = new ScaleAnimation(1, 1, 1.1f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0);
		sa2.setDuration(100);
		
//		ScaleAnimation sa = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//		sa.setDuration(300);
//		AlphaAnimation aa = new AlphaAnimation(0, 1);
//		aa.setDuration(300);
//		AnimationSet as = new AnimationSet(true);
//		as.addAnimation(aa);
//		as.addAnimation(sa);
//		as.setAnimationListener(new AnimationListener() {
//
//			@Override
//			public void onAnimationStart(Animation animation) {
//			}
//
//			@Override
//			public void onAnimationRepeat(Animation animation) {
//			}
//
//			@Override
//			public void onAnimationEnd(Animation animation) {
//			}
//		});
		
		sa1.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mainLl.startAnimation(sa2);
			}
		});
		sa2.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
			}
		});
		
		mainLl.startAnimation(sa1);
	}

	private void startFlyOutAnimation() {
//		ScaleAnimation sa = new ScaleAnimation(1, 0, 1, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//		sa.setDuration(300);
//		AlphaAnimation aa = new AlphaAnimation(1, 0);
//		aa.setDuration(300);
//		AnimationSet as = new AnimationSet(true);
//		as.addAnimation(aa);
//		as.addAnimation(sa);
//		as.setAnimationListener(new AnimationListener() {
//
//			@Override
//			public void onAnimationStart(Animation animation) {
//			}
//
//			@Override
//			public void onAnimationRepeat(Animation animation) {
//			}
//
//			@Override
//			public void onAnimationEnd(Animation animation) {
//				cancel();
//			}
//		});
//		mainLl.startAnimation(as);
		final ScaleAnimation sa1 = new ScaleAnimation(1, 1, 1, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0);
		sa1.setDuration(300);
		final ScaleAnimation sa2 = new ScaleAnimation(1, 1, 1.1f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0);
		sa2.setDuration(100);
		sa1.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mainLl.startAnimation(sa2);
			}
		});
		sa2.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				cancel();
			}
		});
		mainLl.startAnimation(sa1);
		
	}

}
