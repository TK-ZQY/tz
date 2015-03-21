package com.tz.app;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.*;
import android.widget.*;
import android.widget.SeekBar.OnSeekBarChangeListener;

import java.io.*;
import java.net.*;
import java.util.Scanner;

@SuppressLint("HandlerLeak")
public class MainActivity extends Activity {
	
	private Button shutdownBtn = null;
	private Button startBtn = null;
	private Button mouseStartBtn = null;
	private Button keyboardStartBtn = null;
	private SeekBar timeSelect = null;
	private TextView timeoutText = null;
	private AutoCompleteTextView IPAutoText = null;
	private ProgressDialog connectingDialog = null;
	
	static boolean ignoreReceive = false;
	static boolean isShutdownCmdSent = false;
	private boolean isConnecting = false;
	private Context context = null;

	private Thread connectThread = null;
	private Thread receiveThread = null;

	private static final int MSG_IP_VOID = 0;
	private static final int MSG_IP_INVALID = 1;
	private static final int MSG_CON_SUCCESS = 2;
	private static final int MSG_CON_EXCEPTION = 3;
	private static final int MSG_CMD_ARRIVED = 4;
	private static final int MSG_CMD_FAILED = 5;
	private static final int MSG_NO_CONNECTION = 6;
	private static final int MSG_CANCEL_CONNECTION_FAILED = 7;
	private static final int MSG_CANCEL_CONNECTION = 8;

	private Socket sock = null;
	static PrintWriter os = null;
	static BufferedReader is = null;
	static String sIP = null;
	static final int PORT = 8888;

	private TextView logText = null;

	private String cmd = null;
	private int timeout = 0;

	private final int IPTABLEMAX = 100;
	private int IPTableRestIndex = 0;
	private String[] IPTable = new String[IPTABLEMAX];
	private final String APPDIR = "/sdcard/tz";
	private final String IPTABLEFILE = APPDIR + "/iptable";
	private File IPTableFile = new File(IPTABLEFILE);
	private Scanner input = null;
	private PrintWriter output = null;

	private MsgHandler mHandler = new MsgHandler();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this;

		logText = (TextView) findViewById(R.id.logText);

		startBtn = (Button) findViewById(R.id.startButton);
		startBtn.setOnClickListener(new StartBtnClickListener());

		shutdownBtn = (Button) findViewById(R.id.sendButton);
		shutdownBtn.setOnClickListener(new ShutdownBtnClickListener());

		mouseStartBtn = (Button) findViewById(R.id.mouseStartButton);
		mouseStartBtn.setOnClickListener(new MouseBtnClickListener());
		
		keyboardStartBtn = (Button) findViewById(R.id.keyboardStartButton);
		keyboardStartBtn.setOnClickListener(new KeyboardBtnClickListener());
		
		timeSelect = (SeekBar) findViewById(R.id.timeSelect);
		timeSelect.setOnSeekBarChangeListener(new SeekListener());

		timeoutText = (TextView) findViewById(R.id.timeShow);
		timeoutText.setText("定时0分钟后关机");
		
		if (SDCardHelper.isHasSDCard())
			SDCardHelper.mkdirs(APPDIR);
		else 
			Toast.makeText(context, "未插入SD卡，部分数据无法读取", 
					Toast.LENGTH_SHORT).show();
		
		IPTableRemoveNull();
		readIPTableFromFile(IPTableFile);
		logText.append("\nIPTableRestIndex: " + IPTableRestIndex);
		ArrayAdapter<String> IPAutoadapter = new ArrayAdapter<String>(this,
				R.layout.dropdown_layout, IPTable);
		
		IPAutoText = (AutoCompleteTextView) findViewById(R.id.IPAutoText);
		IPAutoText.setAdapter(IPAutoadapter);
		
	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) {
	 * getMenuInflater().inflate(R.menu.activity_main, menu); return true; }
	 */
	
	@Override
	public void onBackPressed() {
		AlertDialog.Builder ackDialog = new AlertDialog.Builder(this);
		ackDialog.setTitle("确认退出吗？");
		ackDialog.setIcon(android.R.drawable.ic_dialog_info);
		ackDialog.setPositiveButton("确定", new PositiveBtnClickListener());
		ackDialog.setNegativeButton("返回", new NegativeBtnClickListener());
		ackDialog.show();
	}
	
	public void readIPTableFromFile(File file) {
		if (SDCardHelper.isHasSDCard()) {
			if (file.exists()) {
				try {
					int i;
					input = new Scanner(file);
					
					for (i = 0; input.hasNext() && i < IPTABLEMAX; i++) {
						IPTable[i] = input.nextLine();
						logText.append("\n" + IPTable[i]);
					}
					IPTableRestIndex = i;
				}
				catch (Exception e) {
					logText.append("Error read:" + e.getStackTrace() + "\n");
				}
			}
			else 
				IPTableRestIndex = 0;
			
			if (input != null) {
				input.close();
				input = null;
			}
		}
	}
	
	public void writeIPTableToFile(File file) {
		if (SDCardHelper.isHasSDCard()) {
			if (!file.exists()) { 
				try {
					file.createNewFile();
				} 
				catch (Exception e) {
					Log.i("tz", "Error: cannot create " +  file.toString());
				}
			}

			try {
				output = new PrintWriter(file);
			
				for (int i = 0; i < IPTABLEMAX && IPTable[i] != null; i++) {
					output.println(IPTable[i]);
				}
			}
			catch (Exception e) {
				Log.i("tz", "Error: output " + IPTABLEFILE + " failed");
			}
			
			if (output != null) {
				output.close();
				output = null;
			}
		}
	}

	public void refreshIPTable(String newIP) {
		int i;
		/*
		 *  添加规则：
		 *  1. 检查 IPTable 是否存在 newIP, 存在则不添加
		 *  2. 如果不存在 且 IPTable 未满, newIP 添加在尾端
		 *  3. 如果已满, 则添加在第一条
		 */
		Log.i("tz", "refresh new IP: " + newIP);  
		if (IPTable[1] == null)
			Log.i("tz", "IPTable[1] == null? True");
		for (i = 0; i < IPTABLEMAX && IPTable[i] != null; i++) { 
			if ( IPTable[i].equals(newIP) )
				return;
		}
		if (i >= IPTABLEMAX)
			IPTable[0] = newIP;
		else {
			IPTable[i] = newIP;
			IPTableRestIndex++;
		}
	}
	
	/* 
	 * IPTableAddNull, IPTableRemoveNull 都是为处理
	 * ArrayAapter 的  IPTable 参数而做的的临时性的解决方法， 有待改进。
	 */
	
	public void IPTableAddNull() {
		for (int i = IPTableRestIndex; i < IPTABLEMAX; i++) {
			IPTable[i] = null;
		}
	}
	
	public void IPTableRemoveNull() {
		for (int i = IPTableRestIndex; i < IPTABLEMAX; i++) {
			IPTable[i] = "";
		}
	}
	
	class PositiveBtnClickListener implements 
					DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int whichs) {
			try {
				sock.close();
			} 
			catch (Exception e){}
			
			IPTableAddNull();
			writeIPTableToFile(IPTableFile);
			MainActivity.this.finish();
		}
	}
	
	class NegativeBtnClickListener implements
					DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
		}
		
	}
	
	/**
	 * Receive 线程的可运行接口类，一直监听输入流. 及将输入，输出流的处理分为两个线程
	 */
	class ReceiveRun implements Runnable {
		public void run() {
			while (true) {
				if (is != null) {
					try {
						int count;
						char[] serverResponse = new char[32];
						
						count = is.read(serverResponse);
						if (count > 0) {
							// 反馈接收信息给用户
							if (ignoreReceive) 
								continue;
							
							// 接收反馈为 "0" 表示发送命令成功
							if (count == 1 && serverResponse[0] == '0') {
								Message msg = new Message();
								msg.what = MSG_CMD_ARRIVED;
								mHandler.sendMessage(msg);
							}
							continue;
						}
						
						if (isConnecting) {
							Message msg = new Message();
							msg.what = MSG_CANCEL_CONNECTION;
							mHandler.sendMessage(msg);
							return;
						}

					} catch (Exception e) {
						if (isConnecting) {
							Message msg = new Message();
							msg.what = MSG_CANCEL_CONNECTION;
							mHandler.sendMessage(msg);
							return;
						}
					}
				}
			}
		}
	}
	
	class StartBtnClickListener implements OnClickListener {
		public void onClick(View v) {
			startConnect();
		}

		public void startConnect() {
			if (isConnecting) {
				try {
					if (sock != null) {
						// 此消息必须先与 sock.close();
						isConnecting = false;
						IPAutoText.setEnabled(true);
						startBtn.setText("开始连接");

						Toast.makeText(context, "断开连接", Toast.LENGTH_SHORT).show();
						
						os.close();
						is.close();
						sock.close();

						sock = null;
						os = null;
						is = null;

						connectThread.interrupt();
						receiveThread.interrupt();
					}
				} catch (Exception e) {
					Message msg = new Message();
					msg.what = MSG_CANCEL_CONNECTION_FAILED;
					mHandler.sendMessage(msg);
					return;
				}
			} else {
				sIP = IPAutoText.getText().toString();
				
				if (sIP.length() <= 0) {
					Message msg = new Message();
					msg.what = MSG_IP_VOID;
					mHandler.sendMessage(msg);
					return;
				}
				if (!isIPvalid(sIP)) {
					Message msg = new Message();
					msg.what = MSG_IP_INVALID;
					mHandler.sendMessage(msg);
					return;
				}
				
				connectingDialog = ProgressDialog.show(MainActivity.this, "正在连接服务器...", 
														"连接中,请稍后...", true, true);
				connectThread = new Thread(new ConnectionRun());
				connectThread.start();
			}
		}
		
		public boolean isIPvalid(String IP) {
			// int index, i = 1;
			// String rest, segment;
			return true;
			/*
			 * if ( IP.length() > 15 ) return false;
			 * 
			 * // IP只包含4段并由“。”分段， 每段为非负数且不超过255 index = IP.indexOf("."); if (
			 * index == 0 || index == -1 || index == IP.length() - 1 ) { return
			 * false; } segment = IP.substring(0, index - 1); rest =
			 * IP.substring(index + 1); if ( Integer.parseInt(segment) < 0 ||
			 * Integer.parseInt(segment) > 255 ) return false;
			 * 
			 * while (i < 3) { index = rest.indexOf("."); if ( index == 0 ||
			 * index == -1 || index == rest.length() - 1 ) { return false; }
			 * 
			 * segment = rest.substring(0, index - 1); rest =
			 * rest.substring(index + 1); if ( Integer.parseInt(segment) < 0 ||
			 * Integer.parseInt(segment) > 255 ) return false; i++; } if (
			 * Integer.parseInt(rest) < 0 || Integer.parseInt(rest) > 255 )
			 * return false;
			 * 
			 * return true;
			 */
		}
	}

	class ConnectionRun implements Runnable {
		@Override
		public void run() {

			try {
				// 连接服务器
				sock = new Socket(sIP, PORT);
				
				// 连接成功
				connectingDialog.dismiss();
				
				// 取得输入、输出流
				is = new BufferedReader(new InputStreamReader(
						sock.getInputStream()));
				os = new PrintWriter(sock.getOutputStream(), true);
				
				IPTableAddNull();
				refreshIPTable(sIP);
				
				Message msg = new Message();
				msg.what = MSG_CON_SUCCESS;
				mHandler.sendMessage(msg);
				
				receiveThread = new Thread(new ReceiveRun());
				receiveThread.start();
			} catch (Exception e) {
				Log.i("tz", e.getMessage());
				
				Message msg = new Message();
				msg.what = MSG_CON_EXCEPTION;
				mHandler.sendMessage(msg);
				return;
			}
			
			while(true) {}
		}
	}

	class ShutdownBtnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			// 向服务器发送shutdown命令，并等待服务器回应
			if (os != null) {
				if (isShutdownCmdSent)
					cmd = "cancel" + "|";
				else
					cmd = "shutdown" + "|" + timeout; 
				
				try {
					os.print(cmd);
					os.flush();
					
					if (isShutdownCmdSent)
						isShutdownCmdSent = false;
					else 
						isShutdownCmdSent = true;
					
					Log.i("tz", "isShutdownCmdSent: " + isShutdownCmdSent);
					//logText.setText("isShutdownCmdSent: " + isShutdownCmdSent);
				} catch (Exception e) {
					Message msg = new Message();
					msg.what = MSG_CMD_FAILED;
					mHandler.sendMessage(msg);
				}
			} else {
				Message msg = new Message();
				msg.what = MSG_NO_CONNECTION;
				mHandler.sendMessage(msg);
			}

		}
	}

	class MouseBtnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			gotoTouchFild();
		}

		public void gotoTouchFild() {
			if (isConnecting) {
				ignoreReceive = true;
				
				SwitchService.switcher = SwitchService.MOUSE_MODE;
				// 开启悬浮窗
				Intent intent = new Intent();
				intent.setAction("com.tz.app.action.SWITCH_SERVICE");
				startService(intent);
				
				intent.setClass(MainActivity.this, MouseActivity.class);
				startActivity(intent);
			} else {
				Message msg = new Message();
				msg.what = MSG_NO_CONNECTION;
				mHandler.sendMessage(msg);
			}
		}
	}
	
	class KeyboardBtnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (isConnecting) {
				ignoreReceive = true;
				
				SwitchService.switcher = SwitchService.KEYBOARD_MODE;
				// 开启悬浮窗
				Intent intent = new Intent();
				intent.setAction("com.tz.app.action.SWITCH_SERVICE");
				startService(intent);
				
				intent.setClass(MainActivity.this, KeyboardActivity.class);
				startActivity(intent);
			} else {
				Message msg = new Message();
				msg.what = MSG_NO_CONNECTION;
				mHandler.sendMessage(msg);
			}
		}
	}
	
	class SeekListener implements OnSeekBarChangeListener {
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			timeout = progress;
			timeoutText.setText("定时" + timeout + "分钟后关机");
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			timeout = 0;
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			timeoutText.setText("定时" + timeout + "分钟后关机");
		}

	}

	class MsgHandler extends Handler {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_IP_VOID:
				Toast.makeText(context, "IP不能为空", Toast.LENGTH_SHORT).show();
				break;
			case MSG_IP_INVALID:
				Toast.makeText(context, "IP地址不合法", Toast.LENGTH_SHORT).show();
				break;
			case MSG_CON_SUCCESS:
				isConnecting = true;
				startBtn.setText("断开连接");
				IPAutoText.setEnabled(false);

				Toast.makeText(context, "连接成功", Toast.LENGTH_SHORT).show();
				break;
			case MSG_CON_EXCEPTION:
				connectingDialog.dismiss();
				Toast.makeText(context, "无法连接到" + sIP, Toast.LENGTH_SHORT)
						.show();
				break;
			case MSG_CMD_ARRIVED:
				if (isShutdownCmdSent) 
					shutdownBtn.setText("取消关机");
				else 
					shutdownBtn.setText("确认关机");
				
				Toast.makeText(getApplicationContext(), "发送命令已到达",
						Toast.LENGTH_SHORT).show();
				break;
			case MSG_CMD_FAILED:
				Toast.makeText(getApplicationContext(), "命令发送失败",
						Toast.LENGTH_SHORT).show();
				break;
			case MSG_NO_CONNECTION:
				isConnecting = false;
				IPAutoText.setEnabled(true);
				startBtn.setText("开始连接");
				
				isShutdownCmdSent = false;
				shutdownBtn.setText("确认关机");
				
				Toast.makeText(getApplicationContext(), "没有连接",
						Toast.LENGTH_SHORT).show();
				break;
			case MSG_CANCEL_CONNECTION:
				isConnecting = false;
				IPAutoText.setEnabled(true);
				startBtn.setText("开始连接");
				
				isShutdownCmdSent = false;
				shutdownBtn.setText("确认关机");

				Toast.makeText(context, "断开连接", Toast.LENGTH_SHORT).show();
				break;
			case MSG_CANCEL_CONNECTION_FAILED:
				Toast.makeText(context, "连接断开错误", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	}

}
