package com.tz.app;

import java.io.File;

import android.os.Environment;

class SDCardHelper {
	public static boolean isHasSDCard() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}
	
	public static void mkdirs(String directory) {
		File dir = new File(directory);
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}
}