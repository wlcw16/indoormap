package com.indoormap.framework.widget;

import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.indoormap.R;
import com.indoormap.framework.common.MapCommonData;
import com.indoormap.framework.common.MapCommonUtil;
import com.indoormap.framework.model.DynamicPOI;
import com.indoormap.framework.model.Location;

public class AttentionRelativeLayout extends RelativeLayout {

	private View baseView;

	private RelativeLayout maskRl;

	private LinearLayout titleLl;

	private ImageView iconIv;

	@SuppressWarnings("unused")
	private ProgressBar iconPs;

	private TextView titleTv;

	private TextView signTv;

	private TextView distanceTv;

	private LinearLayout infoLl;

	private LinearLayout gotoLl;

	private DynamicPOI dynamicPOI;

	private AttentionRelativeLayoutListener attentionRelativeLayoutListener;

	private DynamicPOIDialog dynamicPOIDialog;

	private String locationStr;

	private Bitmap iconBitmap;

	private Handler iconHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			iconIv.setVisibility(VISIBLE);
			// iconPs.setVisibility(GONE);
			iconIv.setImageBitmap(iconBitmap);
		};
	};

	public AttentionRelativeLayout(Context context) {
		super(context);
		init();
	}

	private void init() {
		baseView = LayoutInflater.from(getContext()).inflate(R.layout.attention, this);
		baseView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!isEndAnimationing) {
					endTitltFlyinAnimation();
				}
			}
		});
		maskRl = (RelativeLayout) baseView.findViewById(R.id.attention_mask_rl);
		titleLl = (LinearLayout) baseView.findViewById(R.id.attention_title_ll);
		titleLl.setVisibility(INVISIBLE);
		int width = (int) (MapCommonUtil.getScreenWidth(getContext()) * 0.75f);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		titleLl.setLayoutParams(lp);
		iconIv = (ImageView) baseView.findViewById(R.id.attention_title_icon_iv);
		iconPs = (ProgressBar) baseView.findViewById(R.id.attention_title_icon_ps);
		titleTv = (TextView) baseView.findViewById(R.id.attention_title_popuptitle_tv);
		signTv = (TextView) baseView.findViewById(R.id.attention_title_signnum_tv);
		distanceTv = (TextView) baseView.findViewById(R.id.attention_title_distance_tv);
		infoLl = (LinearLayout) baseView.findViewById(R.id.attention_title_info_ll);
		gotoLl = (LinearLayout) baseView.findViewById(R.id.attention_title_goto_ll);
		dynamicPOIDialog = new DynamicPOIDialog(getContext());
	}

	public void show(ViewGroup parent, DynamicPOI dynamicPOI, String locationStr) {
		parent.addView(this);
		this.dynamicPOI = dynamicPOI;
		this.locationStr = locationStr;
		initIcon();
		initTitle();
		initSign();
		initDistance();
		initInfo();
		gotoInfo();
	}

	private void initIcon() {
		// iconIv.setImageResource(R.drawable.loading_img);

		iconIv.setVisibility(VISIBLE);
		iconIv.setImageResource(R.drawable.placeholder);
		// iconPs.setVisibility(VISIBLE);
		new Thread() {
			public void run() {
				try {
					String tempName = dynamicPOI.getIconPath().replace("/", "_");
					iconBitmap = MapCommonUtil.loadCacheBitmap(tempName);
					if(iconBitmap==null){
						iconBitmap = MapCommonUtil.getBitmapFromInternet(dynamicPOI.getIconPath());
						if(iconBitmap!=null){
							MapCommonUtil.saveCacheBitmap(tempName, iconBitmap);
						}
					}
					iconHandler.sendEmptyMessage(0);
				} catch (IOException e) {
					e.printStackTrace();
				}
			};
		}.start();
		// AnimationDrawable animDance = (AnimationDrawable)
		// this.iconIv.getBackground();
		//
		// animDance.start();
	}

	private void initTitle() {
		titleTv.setText(dynamicPOI.getPopupTitle());
	}

	private void initSign() {
		signTv.setText("此处已有0人签到");
	}

	private void initDistance() {
		if (null == locationStr || "".equals(locationStr)) {
			distanceTv.setText("距离未知，请先定位");
		} else {
			Location l = MapCommonUtil.getLocationFromData(locationStr);
			if (l == null) {
				distanceTv.setText("您不在图书馆内");
			} else {
				int distance = (int) (Math.sqrt((l.getPoint().getX() - dynamicPOI.getX()) * (l.getPoint().getX() - dynamicPOI.getX()) + (l.getPoint().getY() - dynamicPOI.getY()) * (l.getPoint().getY() - dynamicPOI.getY())) / MapCommonData.getInstance().getMapHashMap().get(dynamicPOI.getMapName())
						.getScale());
				distanceTv.setText("距离约" + distance + "米");
			}
		}
	}

	private void initInfo() {
		infoLl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dynamicPOIDialog.show();
				dynamicPOIDialog.init(dynamicPOI, "123");
			}
		});
	}

	private void gotoInfo() {
		gotoLl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (null != attentionRelativeLayoutListener) {
					attentionRelativeLayoutListener.onNaviClick(dynamicPOI);
				}
			}
		});
	}

	public void destroy() {
		ViewGroup parent = (ViewGroup) this.getParent();
		if (parent != null) {
			parent.removeView(this);
			titleLl.setVisibility(INVISIBLE);
			iconIv.setImageResource(R.drawable.placeholder);
			iconIv.setVisibility(VISIBLE);
			// iconPs.setVisibility(VISIBLE);
			if (iconBitmap != null) {
				iconBitmap.recycle();
			}
			if (attentionRelativeLayoutListener != null) {
				attentionRelativeLayoutListener.onDestroy();
			}
		}
	}

	public void startAttention() {
		ScaleAnimation sa = new ScaleAnimation(3, 1, 3, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		sa.setDuration(600);
		AlphaAnimation aa = new AlphaAnimation(0, 1);
		aa.setDuration(600);
		AnimationSet as = new AnimationSet(true);
		as.addAnimation(aa);
		as.addAnimation(sa);
		as.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				startTitltFlyinAnimation();
			}
		});
		maskRl.startAnimation(as);
	}

	public void endAttention() {

		AlphaAnimation aa = new AlphaAnimation(1, 0);
		aa.setDuration(600);
		aa.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				isEndAnimationing = false;
				destroy();
			}
		});

		maskRl.startAnimation(aa);
	}

	private void startTitltFlyinAnimation() {
		final ScaleAnimation sa1 = new ScaleAnimation(0, 0.9f, 0, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0);
		sa1.setDuration(100);
		final ScaleAnimation sa2 = new ScaleAnimation(0.9f, 1.1f, 1.1f, 0.9f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0);
		sa2.setDuration(100);
		final ScaleAnimation sa3 = new ScaleAnimation(1.1f, 1, 0.9f, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0);
		sa3.setDuration(100);
		// final ScaleAnimation sa1 = new ScaleAnimation(1, 1, 0, 1.1f,
		// Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0);
		// sa1.setDuration(100);
		// final ScaleAnimation sa2 = new ScaleAnimation(1f, 1, 1.1f, 0.9f,
		// Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0);
		// sa2.setDuration(100);
		// final ScaleAnimation sa3 = new ScaleAnimation(1f, 1, 0.9f, 1,
		// Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0);
		// sa3.setDuration(100);
		sa1.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				titleLl.setVisibility(VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				titleLl.startAnimation(sa2);
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
				titleLl.startAnimation(sa3);
			}
		});
		titleLl.startAnimation(sa1);
	}

	boolean isEndAnimationing = false;

	private void endTitltFlyinAnimation() {

		final ScaleAnimation sa1 = new ScaleAnimation(1, 1, 1, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0);
		sa1.setDuration(100);
		final ScaleAnimation sa2 = new ScaleAnimation(1f, 1, 1.1f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0);
		sa2.setDuration(100);
		sa1.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				isEndAnimationing = true;
				titleLl.setVisibility(VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				titleLl.startAnimation(sa2);
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
				titleLl.setVisibility(GONE);
				endAttention();
			}
		});
		titleLl.startAnimation(sa1);
	}

	public void setAttentionRelativeLayoutListener(AttentionRelativeLayoutListener attentionRelativeLayoutListener) {
		this.attentionRelativeLayoutListener = attentionRelativeLayoutListener;
	}

	public AttentionRelativeLayoutListener getAttentionRelativeLayoutListener() {
		return attentionRelativeLayoutListener;
	}

	public interface AttentionRelativeLayoutListener {
		public void onDestroy();

		public void onNaviClick(DynamicPOI dynamicPOI);
	}
}
