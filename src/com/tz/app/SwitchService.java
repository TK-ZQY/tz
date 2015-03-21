package com.tz.app;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.IBinder;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;

/**
 * �������л����������л����ģʽ�ͼ���ģʽ
 * 
 * @author TZ
 *
 */
public class SwitchService extends Service {
	
	// ���ת��״̬
	public static final int MOUSE_MODE = 1;
	public static final int KEYBOARD_MODE = 2;
	public static int switcher;
			
	private int statusBarHeight;// ״̬���߶�
	private View view;// ͸������
	private boolean viewAdded = false;// ͸�������Ƿ��Ѿ���ʾ
	private WindowManager windowManager;
	private WindowManager.LayoutParams layoutParams;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("tz", "Switch service starts");
		
		switch (switcher) {
		case MOUSE_MODE :
			view = LayoutInflater.from(this)
			.inflate(R.layout.floating_keyboard_layout, null);
			break;
		case KEYBOARD_MODE :
			view = LayoutInflater.from(SwitchService.this)
			.inflate(R.layout.floating_mouse_layout, null);
			break;
		default:
			view = LayoutInflater.from(this)
			.inflate(R.layout.floating_keyboard_layout, null);
			break;
		}
		
		windowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE);
		//LayoutParams.TYPE_SYSTEM_ERROR����֤��������������View�����ϲ�
		layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, LayoutParams.TYPE_SYSTEM_ERROR,
				LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT);
		layoutParams.gravity = Gravity.RIGHT|Gravity.TOP; //��������ʼ�����½���ʾ

		view.setOnTouchListener(new SwitchWindowTouchListener());
	}
	
	/**
	 * �������ڴ��������
	 */
	class SwitchWindowTouchListener 
			implements OnTouchListener, OnGestureListener {
		private GestureDetector gDetector = new GestureDetector(this);
		float[] temp = new float[] { 0f, 0f };
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
			
			int eventaction = event.getAction();
			switch (eventaction) {
			case MotionEvent.ACTION_DOWN: // �����¼�����¼����ʱ��ָ����������XY����ֵ
				temp[0] = event.getX();
				temp[1] = event.getY();
				break;

			case MotionEvent.ACTION_MOVE:
				refreshView((int) (event.getRawX() - temp[0]), 
							(int) (event.getRawY() - temp[1]));
				break;
			}
			
			gDetector.onTouchEvent(event);
			
			return true;
		}
		@Override
		public boolean onDown(MotionEvent arg0) {
			return false;
		}
		@Override
		public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
				float arg3) {
			return false;
		}
		@Override
		public void onLongPress(MotionEvent arg0) {
			if (switcher == MOUSE_MODE) {
				// ���ڵ�ActivityΪMouseActivity��׼���л���KeyboardActivity
				
				removeView();
				view = LayoutInflater.from(SwitchService.this)
						.inflate(R.layout.floating_mouse_layout, null);
				view.setOnTouchListener(new SwitchWindowTouchListener());
				refresh();
				
				switcher = KEYBOARD_MODE;
				// ����MouseActivity�ϵĵȴ��߳� 
				synchronized (MouseActivity.mouseLock) {
					MouseActivity.mouseLock.notify();
					Log.i("tz", "mouse thread to be notified");
				}

			} 
			else if (switcher == KEYBOARD_MODE){
				removeView();
				view = LayoutInflater.from(SwitchService.this)
						.inflate(R.layout.floating_keyboard_layout, null);
				view.setOnTouchListener(new SwitchWindowTouchListener());
				refresh();
				
				switcher = MOUSE_MODE;
				
				synchronized (KeyboardActivity.keyboardLock) {
					KeyboardActivity.keyboardLock.notify();
					Log.i("tz", "keyboard thread to be notified");
				}
			}
		}
		@Override
		public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
				float arg3) {
			return false;
		}
		@Override
		public void onShowPress(MotionEvent arg0) {
			
		}
		@Override
		public boolean onSingleTapUp(MotionEvent arg0) {
			return false;
		}	
	}
	
	/**
	 * ˢ��������
	 * 
	 * @param x
	 *            �϶����X������
	 * @param y
	 *            �϶����Y������
	 */
	public void refreshView(int x, int y) {
		//״̬���߶Ȳ�������ȡ����Ȼ�õ���ֵ��0
		if(statusBarHeight == 0){
			View rootView  = view.getRootView();
			Rect r = new Rect();
			rootView.getWindowVisibleDisplayFrame(r);
			statusBarHeight = r.top;
		}
		
		layoutParams.x = x;
		// y���ȥ״̬���ĸ߶ȣ���Ϊ״̬�������û����Ի��Ƶ����򣬲�Ȼ�϶���ʱ���������
		layoutParams.y = y - statusBarHeight;//STATUS_HEIGHT;
		refresh();
	}

	/**
	 * ������������߸��������� �����������û�������� ����Ѿ�����������λ��
	 */
	private void refresh() {
		if (viewAdded) {
			windowManager.updateViewLayout(view, layoutParams);
		} else {
			windowManager.addView(view, layoutParams);
			viewAdded = true;
		}
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		refresh();
	}

	/**
	 * �ر�������
	 */
	public void removeView() {
		if (viewAdded) {
			windowManager.removeView(view);
			viewAdded = false;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i("tz", "Switch service destroy");
		removeView();
		switch (switcher) {
		case MOUSE_MODE:
			MouseActivity.mouseThreadRun.finish();
			break;
		case KEYBOARD_MODE:
			KeyboardActivity.keyboardThreadRun.finish();
		}
	}
	
/*	
	class StatusBarReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			//intent.get
		}
		
	}
*/
}
