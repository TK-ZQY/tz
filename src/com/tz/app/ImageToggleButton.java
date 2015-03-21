package com.tz.app;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class ImageToggleButton {
	
	private ImageView iv;
	private int state = TOGGLE_OFF; // Ä¬ÈÏoff 
	
	public static final int TOGGLE_ON = 1;
	public static final int TOGGLE_OFF = 2;
	
	ImageToggleButton(ImageView v, String offM, int lockOffResId, 
			String onM, int lockOnResId, int st) {
		state = st;
		iv = v;
		iv.setOnTouchListener(new ImageToggleButtonOnTouchListener(offM, 
				lockOffResId, onM, lockOnResId));
		
		if (state == TOGGLE_ON) {
			iv.setImageResource(lockOnResId);
		} else if (state == TOGGLE_OFF) {
			iv.setImageResource(lockOffResId);
		}
	}
	
	public void setImageResource(int resId) {
		iv.setImageResource(resId);
	}
	
	public int getState() {
		return state;
	}
	
	class ImageToggleButtonOnTouchListener implements 
		OnTouchListener {
	
		private ImageView keyImage;
		private int capsLockOff;
		private int capsLockOn;
		private String onMsg;
		private String offMsg;

		private long currentTime;
		private long lastTime;
		private int xStep = 0;
		
		ImageToggleButtonOnTouchListener(String offM, int lockOffResId, 
				String onM, int lockOnResId) {
			onMsg = onM;
			offMsg = offM;
			capsLockOff = lockOffResId;
			capsLockOn = lockOnResId;
		}
	
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (v instanceof ImageView) {
				keyImage = (ImageView)v;
			} else 
				return false;
			
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN :
				if (xStep == 0) {
					lastTime = System.currentTimeMillis();
					xStep = 1;
				}
				break;
			case MotionEvent.ACTION_UP :
				if (xStep == 1) {
					currentTime = System.currentTimeMillis();
					if (currentTime - lastTime < 200) {
						if (state == TOGGLE_ON) {
							// ¹Ø±Õ´óÐ´
							state = TOGGLE_OFF;
							keyImage.setImageResource(capsLockOff); 
							if (MainActivity.os != null) {
								MainActivity.os.print("keyboard|" + offMsg);
								MainActivity.os.flush();
							}
						} 
						else {
							state = TOGGLE_ON;
							keyImage.setImageResource(capsLockOn);
							if (MainActivity.os != null) {
								MainActivity.os.print("keyboard|" + onMsg);
								MainActivity.os.flush();
							}
						}
					}
					xStep = 0;
				}
				break;
			}
			return true;
		}
	}
}