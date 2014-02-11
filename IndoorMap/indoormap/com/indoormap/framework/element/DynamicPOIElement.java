package com.indoormap.framework.element;

import android.graphics.Color;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.indoormap.R;
import com.indoormap.framework.common.MapCommonUtil;
import com.indoormap.framework.element.base.BaseElementFrameLayout;
import com.indoormap.framework.map.MapView;
import com.indoormap.framework.model.DynamicPOI;
import com.indoormap.framework.model.Point;

public class DynamicPOIElement extends BaseElementFrameLayout {

	private DynamicPOI dynamicPOI;

	private LinearLayout nNnCLl;

	private TextView indexTv;
	
	public DynamicPOIElement(MapView mapView, DynamicPOI dynamicPOI) {
		super(mapView, new Point(dynamicPOI.getX(), dynamicPOI.getY(), mapView.getMap()));
		this.dynamicPOI = dynamicPOI;
		init();
	}

	@Override
	protected void init() {
		super.init();
		nNnCLl = new LinearLayout(getContext());
		indexTv = new TextView(getContext());
		TextPaint tp = indexTv.getPaint(); 
		tp.setFakeBoldText(true); 
		indexTv.setTextColor(Color.WHITE);
		indexTv.setText(dynamicPOI.getPopupIndex());
		indexTv.setTextSize(20);
		nNnCLl.setBackgroundResource(R.drawable.notnear);
		nNnCLl.setGravity(Gravity.CENTER_HORIZONTAL);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		lp.topMargin = (int) (12*MapCommonUtil.getScreenDensity(getContext()));
		indexTv.setLayoutParams(lp);
		nNnCLl.addView(indexTv);
		int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		nNnCLl.measure(w, h);
		int width = nNnCLl.getMeasuredWidth();
		int height = nNnCLl.getMeasuredHeight();
		deltaWidth = width / 2f;
		deltaHeight = height;
		addView(nNnCLl);
		this.setBeWidth(width);
		this.setBeHeight(height);
		this.setVisibility(VISIBLE);
		update();
	}

	public void setDynamicPOI(DynamicPOI dynamicPOI) {
		this.dynamicPOI = dynamicPOI;
	}

	public DynamicPOI getDynamicPOI() {
		return dynamicPOI;
	}

	@Override
	public float getDeltaWdith() {
		return width / 2f;
	}

	@Override
	public float getDeltaHeight() {
		return height;
	}
	
	public void startFlyInAnimation(){
		ScaleAnimation sa = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 1f);
		sa.setDuration(300);
		AlphaAnimation aa = new AlphaAnimation(1, 1);
		aa.setDuration(300);
		AnimationSet as = new AnimationSet(true);
		as.addAnimation(aa);
		as.addAnimation(sa);
		as.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
			}
		});
		startAnimation(as);
	}
}
