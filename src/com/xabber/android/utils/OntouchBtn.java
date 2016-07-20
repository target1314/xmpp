package com.xabber.android.utils;

import android.content.Context;
import android.graphics.ColorMatrixColorFilter;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class OntouchBtn extends Button {

	public static final OnTouchListener touchDarkListener = new OnTouchListener() {

		public final float[] BT_SELECTED = new float[] { 1, 0, 0, 0, -50, 0, 1,
				0, 0, -50, 0, 0, 1, 0, -50, 0, 0, 0, 1, 0 };
		public final float[] BT_NOT_SELECTED = new float[] { 1, 0, 0, 0, 0, 0,
				1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0 };

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				v.getBackground().setColorFilter(
						new ColorMatrixColorFilter(BT_SELECTED));
				v.setBackgroundDrawable(v.getBackground());
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				v.getBackground().setColorFilter(
						new ColorMatrixColorFilter(BT_NOT_SELECTED));
				v.setBackgroundDrawable(v.getBackground());
			}
			return false;
		}
	};

	public OntouchBtn(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setOnTouchListener(touchDarkListener);
	}
}
