package com.tz.app;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * 提供高亮被点击的按键服务(暂未现，也没有在Manifest中注册)
 * @author TZ
 *
 */
public class SoftKeyboardHighlightService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
