package com.tz.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class MouseActivity extends Activity 
		implements OnTouchListener, OnGestureListener, OnDoubleTapListener {
	
	private GestureDetector mGestureDetector;
	private RelativeLayout touchField;
	
	public static Thread mouseThread;// 线程发送Keyboard的意图
	public static Object mouseLock;// 用于同步
	public static MouseThreadRunnable mouseThreadRun;
	
	private long lastTime;
	private long currentTime;
	private int ClickCx = 0;
	private float lastX = 0;
	private float lastY = 0;
	private float currentX = 0;
	private float currentY = 0;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("tz", "Mouse activity starts");
		
		// 设置全屏显示
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.mouse_activity);

		mGestureDetector = new GestureDetector(this);
		
		touchField = (RelativeLayout)findViewById(R.id.touchField);
		touchField.setOnTouchListener(this);
		touchField.setLongClickable(true);
		
		SwitchService.switcher = SwitchService.MOUSE_MODE;
		
		mouseLock = new Object();
		mouseThreadRun = new MouseThreadRunnable();
		mouseThread = new Thread(mouseThreadRun);
		mouseThread.start();
	}
	
	class MouseThreadRunnable implements Runnable {
		boolean isGotoDestroy = false;
		@Override
		public void run() {
			synchronized (mouseLock) {
				try {
					Log.i("tz", "mouse thread wait");
					mouseLock.wait(); // 等待被唤醒
					Log.i("tz", "mouse thread awake");
				} 
				catch (Exception e) {
					Log.i("tz", e.getMessage());
				}
				
				if (isGotoDestroy) {
					Log.i("tz", "mouse thread destroy");
					return;
				}
				
				Intent intent = new Intent();
				intent.setClass(MouseActivity.this, KeyboardActivity.class);
				try {
					startActivity(intent);
				} 
				catch (Exception e) {
					Log.i("tz", e.getMessage());
				}
				MouseActivity.this.finish();
			}
			Log.i("tz", "mouse thread done");
		}
		
		public void finish() {
			isGotoDestroy = true;
			synchronized (mouseLock) {
				mouseLock.notify();
			}
		}
	}
	
	/**
	 * 退出时关闭悬浮窗、取消忽略接收
	 */
	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.setAction("com.tz.app.action.SWITCH_SERVICE");
		stopService(intent);
		
		MainActivity.ignoreReceive = false;
		MouseActivity.this.finish();
	}

	@Override public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.mouse_menu, menu); 
		return true; 
	}

	
	public void onDestroy() {
		super.onDestroy();
		Log.i("tz", "Mouse activity destroyed!");
	}
	
	@Override
	public boolean onDoubleTap(MotionEvent e) {

		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {

		return false;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {

		if (MainActivity.os != null) {
			MainActivity.os.print("lc|");
			MainActivity.os.flush();
		}
		return true;
	}

	@Override
	public boolean onDown(MotionEvent e) {

		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {

		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

		if (MainActivity.os != null) {
			MainActivity.os.print("rc|");
			MainActivity.os.flush();
		}
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		
		if (MainActivity.os != null) {
			MainActivity.os.print("mouse|" + "x" + -distanceX + "y" + -distanceY);
			MainActivity.os.flush();
		}
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {

		return false;
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent e) {
		
		// 拖拽手势：DOWN-UP-DOWN-MOVE 且MOVE需超过一定范围
		// 双击手势：DOWN-UP-DOWN-UP
		switch (e.getAction()) {
		case MotionEvent.ACTION_UP:
			// 完成一次 DOWN-UP
			if (ClickCx == 1) {
				currentTime = System.currentTimeMillis();
				if (currentTime - lastTime < 100) {
					 lastTime = currentTime;
					 ClickCx++;
					 break;
				}
				ClickCx=0;
			}
			else if(ClickCx == 3) {
				// 自定义双击
				currentTime = System.currentTimeMillis();
				if (currentTime - lastTime < 100) {
					if (MainActivity.os != null) {
						MainActivity.os.print("dc|");
						MainActivity.os.flush();
					}
				}
				ClickCx = 0;
			}
			break;
		case MotionEvent.ACTION_DOWN:
			if (ClickCx == 0) {
				lastTime = System.currentTimeMillis();
				ClickCx++;
				break;
			}
			// 第二次出现 DOWN
			else if (ClickCx == 2)
			{
				currentTime = System.currentTimeMillis();
				if (currentTime - lastTime < 300) {
					lastTime = currentTime;
					lastX = e.getX();
					lastY = e.getY();
					ClickCx++;
					break;
				}
				ClickCx=0;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			// 如果完成了  DOWN-UP-DOWN
			if (ClickCx == 3)
			{
				currentTime = System.currentTimeMillis();
				currentX = e.getX();
				currentY = e.getY();
				if (currentTime - lastTime < 100) {
					if (distance(currentX, currentY, lastX, lastY) > 1) {
						if (MainActivity.os != null) {
							MainActivity.os.print("dcm|");
							MainActivity.os.flush();
						}
					}
				}
				ClickCx = 0;
			}
		}
		return mGestureDetector.onTouchEvent(e);
	}
	
	public float distance(float x1, float y1, float x2, float y2) {
		float d = (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
		return FloatMath.sqrt(d);
	}
}