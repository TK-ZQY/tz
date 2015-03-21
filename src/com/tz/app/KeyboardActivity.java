package com.tz.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class KeyboardActivity extends Activity {
	
	/**
	 * pannel 1---general keys
	 */
	// group 1
	private ImageView keyQ;
	private ImageView keyW;
	private ImageView keyE;
	private ImageView keyR;
	private ImageView keyT;
	private ImageView keyY;
	private ImageView keyU;
	private ImageView keyI;
	private ImageView keyO;
	private ImageView keyP;
	// group 2
	private ImageView keyA;
	private ImageView keyS;
	private ImageView keyD;
	private ImageView keyF;
	private ImageView keyG;
	private ImageView keyH;
	private ImageView keyJ;
	private ImageView keyK;
	private ImageView keyL;
	// group 3
	private ImageView keyZ;
	private ImageView keyX;
	private ImageView keyC;
	private ImageView keyV;
	private ImageView keyB;
	private ImageView keyN;
	private ImageView keyM;
	private ImageView backspace;
	private ImageToggleButton capsLock;
	// group 4
	private ImageView inputMethod;
	private ImageView space;
	private ImageView pannelSwitcher;
	private ImageView enter;
	/**
	 * pannel 2---special keys
	 */
	// group 1
	private ImageView exclamation;
	private ImageView at;
	private ImageView sharp;
	private ImageView dollar;
	private ImageView percent;
	private ImageView xor;
	private ImageView and;
	private ImageView asterisk;
	private ImageView parenthesisLeft;
	private ImageView parenthesisRight;
	// group 2 
	private ImageView backQuote;
	private ImageView not;
	private ImageView dash;
	private ImageView underline;
	private ImageView equal;
	private ImageView plus;
	private ImageView middleBracketLeft;
	private ImageView middleBracketRight;
	private ImageView bigBracketLeft;
	private ImageView bigBracketRight;
	// group 3
	private ImageView semiColon;
	private ImageView colon;
	private ImageView comma;
	private ImageView period;
	private ImageView singleQuote;
	private ImageView quote;
	private ImageView backslash;
	private ImageView or;
	private ImageView angleBracketLeft;
	private ImageView angleBracketRight;
	// group 4
	private ImageView pannelSwitcher2;
	
	public static Thread keyboardThread;// 线程发送mouse的意图
	public static Object keyboardLock;// 用于同步
	public static KeyboardThreadRunnable keyboardThreadRun;
	
	private boolean isInWordsPannel = true;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("tz", "Keyboard activity starts");
		
		// 设置全屏显示
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		// 键盘面板设置
		if (isInWordsPannel) {
			setContentView(R.layout.keyboard_pannel);
			setGeneralKeys();
		}
		else {
			setContentView(R.layout.keyboard_pannel2);
			setSpecialKeys();
		}
		
		SwitchService.switcher = SwitchService.KEYBOARD_MODE;
		
		keyboardLock = new Object();
		keyboardThreadRun = new KeyboardThreadRunnable();
		keyboardThread = new Thread(keyboardThreadRun);
		keyboardThread.start();
	}
	
	public void onDestroy() {
		super.onDestroy();
		Log.i("tz", "keyboard activity destroyed!");
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
		KeyboardActivity.this.finish();
	}
	
	private void setGeneralKeys() {
		// 初始化按键 q w e r t y u i o p
		keyQ = (ImageView) findViewById(R.id.upperQ);
		keyQ.setOnTouchListener(new SoftKeyboardOnTouchListener('q', 'Q', '1',
				R.drawable.uq, R.drawable.suq));
		keyQ.setLongClickable(true);
	
		keyW = (ImageView) findViewById(R.id.upperW);
		keyW.setOnTouchListener(new SoftKeyboardOnTouchListener('w', 'W', '2',
				R.drawable.uw, R.drawable.suw));
		keyW.setLongClickable(true);
		
		keyE = (ImageView) findViewById(R.id.upperE);
		keyE.setOnTouchListener(new SoftKeyboardOnTouchListener('e', 'E', '3',
				R.drawable.ue, R.drawable.sue));
		keyE.setLongClickable(true);
		
		keyR = (ImageView) findViewById(R.id.upperR);
		keyR.setOnTouchListener(new SoftKeyboardOnTouchListener('r', 'R', '4',
				R.drawable.ur, R.drawable.sur));
		keyR.setLongClickable(true);
		
		keyT = (ImageView) findViewById(R.id.upperT);
		keyT.setOnTouchListener(new SoftKeyboardOnTouchListener('t', 'T', '5',
				R.drawable.ut, R.drawable.sut));
		keyT.setLongClickable(true);
		
		keyY = (ImageView) findViewById(R.id.upperY);
		keyY.setOnTouchListener(new SoftKeyboardOnTouchListener('y', 'Y', '6',
				R.drawable.uy, R.drawable.suy));
		keyY.setLongClickable(true);
		
		keyU = (ImageView) findViewById(R.id.upperU);
		keyU.setOnTouchListener(new SoftKeyboardOnTouchListener('u', 'U', '7',
				R.drawable.uu, R.drawable.suu));
		keyU.setLongClickable(true);
		
		keyI = (ImageView) findViewById(R.id.upperI);
		keyI.setOnTouchListener(new SoftKeyboardOnTouchListener('i', 'I', '8',
				R.drawable.ui, R.drawable.sui));
		keyI.setLongClickable(true);
		
		keyO = (ImageView) findViewById(R.id.upperO);
		keyO.setOnTouchListener(new SoftKeyboardOnTouchListener('o', 'O', '9',
				R.drawable.uo, R.drawable.suo));
		keyO.setLongClickable(true);
		
		keyP = (ImageView) findViewById(R.id.upperP);
		keyP.setOnTouchListener(new SoftKeyboardOnTouchListener('p', 'P', '0',
				R.drawable.up, R.drawable.sup));
		keyP.setLongClickable(true);
		
		// group 2 初始化按键 a s d f g h j k l
		keyA = (ImageView) findViewById(R.id.upperA);
		keyA.setOnTouchListener(new SoftKeyboardOnTouchListener('a', 'A', 'a',
				R.drawable.ua, R.drawable.sua));
		keyA.setLongClickable(true);
		
		keyS = (ImageView) findViewById(R.id.upperS);
		keyS.setOnTouchListener(new SoftKeyboardOnTouchListener('s', 'S', 's',
				R.drawable.us, R.drawable.sus));
		keyS.setLongClickable(true);
		
		keyD = (ImageView) findViewById(R.id.upperD);
		keyD.setOnTouchListener(new SoftKeyboardOnTouchListener('d', 'D', 'd',
				R.drawable.ud, R.drawable.sud));
		keyD.setLongClickable(true);
		
		keyF = (ImageView) findViewById(R.id.upperF);
		keyF.setOnTouchListener(new SoftKeyboardOnTouchListener('f', 'F', 'f',
				R.drawable.uf, R.drawable.suf));
		keyF.setLongClickable(true);
		
		keyG = (ImageView) findViewById(R.id.upperG);
		keyG.setOnTouchListener(new SoftKeyboardOnTouchListener('g', 'G', 'g',
				R.drawable.ug, R.drawable.sug));
		keyG.setLongClickable(true);
		
		keyH = (ImageView) findViewById(R.id.upperH);
		keyH.setOnTouchListener(new SoftKeyboardOnTouchListener('h', 'H', 'h',
				R.drawable.uh, R.drawable.suh));
		keyH.setLongClickable(true);
		
		keyJ = (ImageView) findViewById(R.id.upperJ);
		keyJ.setOnTouchListener(new SoftKeyboardOnTouchListener('j', 'J', 'j',
				R.drawable.uj, R.drawable.suj));
		keyJ.setLongClickable(true);
		
		keyK = (ImageView) findViewById(R.id.upperK);
		keyK.setOnTouchListener(new SoftKeyboardOnTouchListener('k', 'K', 'k',
				R.drawable.uk, R.drawable.suk));
		keyK.setLongClickable(true);
		
		keyL = (ImageView) findViewById(R.id.upperL);
		keyL.setOnTouchListener(new SoftKeyboardOnTouchListener('l', 'L', 'l',
				R.drawable.ul, R.drawable.sul));
		keyL.setLongClickable(true);
		
		// group 3 初始化按键 z x c v b n m CapsLock Backspace
		keyZ = (ImageView) findViewById(R.id.upperZ);
		keyZ.setOnTouchListener(new SoftKeyboardOnTouchListener('z', 'Z', 'z',
				R.drawable.uz, R.drawable.suz));
		keyZ.setLongClickable(true);
		
		keyX = (ImageView) findViewById(R.id.upperX);
		keyX.setOnTouchListener(new SoftKeyboardOnTouchListener('x', 'X', 'x',
				R.drawable.ux, R.drawable.sux));
		keyX.setLongClickable(true);
		
		keyC = (ImageView) findViewById(R.id.upperC);
		keyC.setOnTouchListener(new SoftKeyboardOnTouchListener('c', 'C', 'c',
				R.drawable.uc, R.drawable.suc));
		keyC.setLongClickable(true);
		
		keyV = (ImageView) findViewById(R.id.upperV);
		keyV.setOnTouchListener(new SoftKeyboardOnTouchListener('v', 'V', 'v',
				R.drawable.uv, R.drawable.suv));
		keyV.setLongClickable(true);
		
		keyB = (ImageView) findViewById(R.id.upperB);
		keyB.setOnTouchListener(new SoftKeyboardOnTouchListener('b', 'B', 'b',
				R.drawable.ub, R.drawable.sub));
		keyB.setLongClickable(true);
		
		keyN = (ImageView) findViewById(R.id.upperN);
		keyN.setOnTouchListener(new SoftKeyboardOnTouchListener('n', 'N', 'n',
				R.drawable.un, R.drawable.sun));
		keyN.setLongClickable(true);
		
		keyM = (ImageView) findViewById(R.id.upperM);
		keyM.setOnTouchListener(new SoftKeyboardOnTouchListener('m', 'M', 'm',
				R.drawable.um, R.drawable.sum));
		keyM.setLongClickable(true);
		
		backspace = (ImageView) findViewById(R.id.backspace);
		backspace.setOnTouchListener(new SoftKeyboardOnTouchListener("backspace", "backspace", 
				"backspace", R.drawable.backspace, R.drawable.show_backspace));
		backspace.setLongClickable(true);
		
		capsLock = new ImageToggleButton((ImageView) findViewById(R.id.capsLock), "capsLockOff", 
				R.drawable.caps_lock_off, "capsLockOn", R.drawable.caps_lock_on, 
				ImageToggleButton.TOGGLE_OFF);
		
		// group 4
		inputMethod = (ImageView) findViewById(R.id.inputMethod);
		inputMethod.setOnTouchListener(new SoftKeyboardOnTouchListener("inputMethod", "inputMethod", 
				"inputMethod", R.drawable.input_method, R.drawable.show_input_method));
		inputMethod.setLongClickable(true);
		
		space = (ImageView) findViewById(R.id.space);
		space.setOnTouchListener(new SoftKeyboardOnTouchListener("space", "space", 
				"space", R.drawable.space, R.drawable.show_space));
		space.setLongClickable(true);
		
		enter = (ImageView) findViewById(R.id.enter);
		enter.setOnTouchListener(new SoftKeyboardOnTouchListener("enter", "enter", 
				"enter", R.drawable.enter, R.drawable.show_enter));
		enter.setLongClickable(true);
		
		pannelSwitcher = (ImageView) findViewById(R.id.punctuation);
		pannelSwitcher.setOnTouchListener(new pannelSwitcherOnTouchListener(R.drawable.punctuation, 
				R.drawable.show_punctuation));
	}
	
	private void setSpecialKeys() {
		// group 1
		exclamation = (ImageView) findViewById(R.id.exclamation);
		exclamation.setOnTouchListener(new SoftKeyboardOnTouchListener('!', '!', '!', 
				R.drawable.exclamation, R.drawable.show_exclamation));
		exclamation.setLongClickable(true);
		
		at = (ImageView) findViewById(R.id.at);
		at.setOnTouchListener(new SoftKeyboardOnTouchListener('@', '@', '@', 
				R.drawable.at, R.drawable.show_at));
		at.setLongClickable(true);
		
		sharp = (ImageView) findViewById(R.id.sharp);
		sharp.setOnTouchListener(new SoftKeyboardOnTouchListener('#', '#', '#', 
				R.drawable.sharp, R.drawable.show_sharp));
		sharp.setLongClickable(true);
		
		dollar = (ImageView) findViewById(R.id.dollar);
		dollar.setOnTouchListener(new SoftKeyboardOnTouchListener('$', '$', '$', 
				R.drawable.dollar, R.drawable.show_dollar));
		dollar.setLongClickable(true);
		
		percent = (ImageView) findViewById(R.id.percent);
		percent.setOnTouchListener(new SoftKeyboardOnTouchListener('%', '%', '%', 
				R.drawable.percent, R.drawable.show_percent));
		percent.setLongClickable(true);
		
		xor = (ImageView) findViewById(R.id.xor);
		xor.setOnTouchListener(new SoftKeyboardOnTouchListener('^', '^', '^', 
				R.drawable.xor, R.drawable.show_xor));
		xor.setLongClickable(true);
		
		and = (ImageView) findViewById(R.id.and);
		and.setOnTouchListener(new SoftKeyboardOnTouchListener('&', '&', '&', 
				R.drawable.and, R.drawable.show_and));
		and.setLongClickable(true);
		
		asterisk = (ImageView) findViewById(R.id.asterisk);
		asterisk.setOnTouchListener(new SoftKeyboardOnTouchListener('*', '*', '*', 
				R.drawable.asterisk, R.drawable.show_asterisk));
		asterisk.setLongClickable(true);
		
		parenthesisLeft = (ImageView) findViewById(R.id.parenthesisLeft);
		parenthesisLeft.setOnTouchListener(new SoftKeyboardOnTouchListener('(', '(', '(', 
				R.drawable.parenthesis_left, R.drawable.show_parenthesis_left));
		parenthesisLeft.setLongClickable(true);
		
		parenthesisRight = (ImageView) findViewById(R.id.parenthesis_right);
		parenthesisRight.setOnTouchListener(new SoftKeyboardOnTouchListener(')', ')', ')', 
				R.drawable.parenthesis_right, R.drawable.show_parenthesis_right));
		parenthesisRight.setLongClickable(true);
		
		// group 2
		backQuote = (ImageView) findViewById(R.id.backQuote);
		backQuote.setOnTouchListener(new SoftKeyboardOnTouchListener('`', '`', '`', 
				R.drawable.back_quote, R.drawable.show_back_quote));
		backQuote.setLongClickable(true);
		
		not = (ImageView) findViewById(R.id.not);
		not.setOnTouchListener(new SoftKeyboardOnTouchListener('~', '~', '~', 
				R.drawable.not, R.drawable.show_not));
		not.setLongClickable(true);
		
		dash = (ImageView) findViewById(R.id.dash);
		dash.setOnTouchListener(new SoftKeyboardOnTouchListener('-', '-', '-', 
				R.drawable.dash, R.drawable.show_dash));
		dash.setLongClickable(true);
		
		underline = (ImageView) findViewById(R.id.underline);
		underline.setOnTouchListener(new SoftKeyboardOnTouchListener('_', '_', '_', 
				R.drawable.underline, R.drawable.show_underline));
		underline.setLongClickable(true);
		
		equal = (ImageView) findViewById(R.id.equal);
		equal.setOnTouchListener(new SoftKeyboardOnTouchListener('=', '=', '=', 
				R.drawable.equal, R.drawable.show_equal));
		equal.setLongClickable(true);
		
		plus = (ImageView) findViewById(R.id.plus);
		plus.setOnTouchListener(new SoftKeyboardOnTouchListener('+', '+', '+', 
				R.drawable.plus, R.drawable.show_plus));
		plus.setLongClickable(true);
		
		middleBracketLeft = (ImageView) findViewById(R.id.middleBracketLeft);
		middleBracketLeft.setOnTouchListener(new SoftKeyboardOnTouchListener('[', '[', '[', 
				R.drawable.middle_bracket_left, R.drawable.show_middle_bracket_left));
		middleBracketLeft.setLongClickable(true);
		
		middleBracketRight = (ImageView) findViewById(R.id.middleBracketRight);
		middleBracketRight.setOnTouchListener(new SoftKeyboardOnTouchListener(']', ']', ']', 
				R.drawable.middle_bracket_right, R.drawable.show_middle_bracket_right));
		middleBracketRight.setLongClickable(true);
		
		bigBracketLeft = (ImageView) findViewById(R.id.bigBracketLeft);
		bigBracketLeft.setOnTouchListener(new SoftKeyboardOnTouchListener('{', '{', '{', 
				R.drawable.big_bracket_left, R.drawable.show_big_bracket_left));
		bigBracketLeft.setLongClickable(true);
		
		bigBracketRight = (ImageView) findViewById(R.id.bigBracketRight);
		bigBracketRight.setOnTouchListener(new SoftKeyboardOnTouchListener('}', '}', '}', 
				R.drawable.big_bracket_right, R.drawable.show_big_bracket_right));
		bigBracketRight.setLongClickable(true);
		// group 3
		semiColon = (ImageView) findViewById(R.id.semiColon);
		semiColon.setOnTouchListener(new SoftKeyboardOnTouchListener(';', ';', ';', 
				R.drawable.semi_colon, R.drawable.show_semi_colon));
		semiColon.setLongClickable(true);
		
		colon = (ImageView) findViewById(R.id.colon);
		colon.setOnTouchListener(new SoftKeyboardOnTouchListener(':', ':', ':', 
				R.drawable.colon, R.drawable.show_colon));
		colon.setLongClickable(true);
		
		comma = (ImageView) findViewById(R.id.comma);
		comma.setOnTouchListener(new SoftKeyboardOnTouchListener(',', ',', ',', 
				R.drawable.comma, R.drawable.show_comma));
		comma.setLongClickable(true);
		
		period = (ImageView) findViewById(R.id.period);
		period.setOnTouchListener(new SoftKeyboardOnTouchListener('.', '.', '.', 
				R.drawable.period, R.drawable.show_period));
		period.setLongClickable(true);
		
		singleQuote = (ImageView) findViewById(R.id.singleQuote);
		singleQuote.setOnTouchListener(new SoftKeyboardOnTouchListener("'", "'", "'", 
				R.drawable.single_quote, R.drawable.show_single_quote));
		singleQuote.setLongClickable(true);
		
		quote = (ImageView) findViewById(R.id.quote);
		quote.setOnTouchListener(new SoftKeyboardOnTouchListener('"', '"', '"', 
				R.drawable.quote, R.drawable.show_quote));
		quote.setLongClickable(true);
		
		backslash = (ImageView) findViewById(R.id.backslash);
		backslash.setOnTouchListener(new SoftKeyboardOnTouchListener("\\", "\\", "\\", 
				R.drawable.backslash, R.drawable.show_backslash));
		backslash.setLongClickable(true);
		
		or = (ImageView) findViewById(R.id.or);
		or.setOnTouchListener(new SoftKeyboardOnTouchListener('|', '|', '|', 
				R.drawable.or, R.drawable.show_or));
		or.setLongClickable(true);
		
		angleBracketLeft = (ImageView) findViewById(R.id.angleBracketLeft);
		angleBracketLeft.setOnTouchListener(new SoftKeyboardOnTouchListener('<', '<', '<', 
				R.drawable.angle_bracket_left, R.drawable.show_angle_bracket_left));
		angleBracketLeft.setLongClickable(true);
		
		angleBracketRight = (ImageView) findViewById(R.id.angleBracketRight);
		angleBracketRight.setOnTouchListener(new SoftKeyboardOnTouchListener('>', '>', '>', 
				R.drawable.angle_bracket_right, R.drawable.show_angle_bracket_right));
		angleBracketRight.setLongClickable(true);
		
		try {
			pannelSwitcher2 = (ImageView) findViewById(R.id.punctuation2);
			pannelSwitcher2.setOnTouchListener(new pannelSwitcherOnTouchListener(R.drawable.punctuation, 
					R.drawable.show_punctuation));
		}
		catch (Exception ex) {
			Log.i("tz", ex.getMessage());
		}
	}
	
	class KeyboardThreadRunnable implements Runnable {
		boolean isGotoDestroy = false;
		@Override
		public void run() {
			synchronized (keyboardLock) {
				try {
					Log.i("tz", "keyboard thread wait");
					keyboardLock.wait(); // 等待被唤醒
					Log.i("tz", "keyboard thread awake");
				} 
				catch (Exception e) {
					Log.i("tz", e.getMessage());
				}
				
				if (isGotoDestroy) {
					Log.i("tz", "keyboard thread destroy");
					return;
				}
				
				Intent intent = new Intent();
				intent.setClass(KeyboardActivity.this, MouseActivity.class);
				try {
					startActivity(intent);
				} 
				catch (Exception e) {
					Log.i("tz", e.getMessage());
				}
				KeyboardActivity.this.finish();
			}
			Log.i("tz", "keyboard thread done");
		}
		
		public void finish() {
			isGotoDestroy = true;
			synchronized (keyboardLock) {
				keyboardLock.notify();
			}
		}
	}
	
	class SoftKeyboardOnTouchListener implements
			OnTouchListener, OnGestureListener {
		
		private ImageView keyImage;
		private GestureDetector detector; 
		private String characters;
		private String upperCaseCharacters;
		private String longPressedCharacters;
		private int oringe;
		private int highlight;
		
		SoftKeyboardOnTouchListener(char ch, char upCh, char lpCh, 
				int oringeResId, int highlightResId) {
			detector = new GestureDetector(this);
			characters = "" + ch;
			upperCaseCharacters = "" + upCh;
			longPressedCharacters = "" + lpCh;
			oringe = oringeResId;
			highlight = highlightResId;
		}
		
		SoftKeyboardOnTouchListener(String chs, String upChs, String lpChs, 
				int oringeResId, int highlightResId) {
			detector = new GestureDetector(this);
			characters = chs;
			upperCaseCharacters = upChs;
			longPressedCharacters = lpChs;
			oringe = oringeResId;
			highlight = highlightResId;
		}
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (v instanceof ImageView) {
				keyImage = (ImageView)v;
			} else 
				return false;
			
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN :
				keyImage.setImageResource(highlight);// 高亮点击的键
				break;
			case MotionEvent.ACTION_UP :
				keyImage.setImageResource(oringe); // 恢复原按键图形
				break;
			}
			
			return detector.onTouchEvent(event);
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
			keyImage.setImageResource(oringe); // 恢复原按键图形
			if (MainActivity.os != null) {
				if (capsLock.getState() == ImageToggleButton.TOGGLE_ON) {
					// 如果按键上没有重载其他值，则在大写输入情况下，输出为大写值。
					if (characters.equals(longPressedCharacters)) {
						MainActivity.os.print("keyboard|" + upperCaseCharacters);
					}
					else {
						MainActivity.os.print("keyboard|" + longPressedCharacters);
					}
				} 
				else {
					MainActivity.os.print("keyboard|" + longPressedCharacters);
				}
				MainActivity.os.flush();
			}
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public void onShowPress(MotionEvent e) {
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			keyImage.setImageResource(oringe); // 恢复原按键图形
			if (MainActivity.os != null) {
				if (capsLock.getState() == ImageToggleButton.TOGGLE_ON) {
					MainActivity.os.print("keyboard|" + upperCaseCharacters);
				}
				else {
					MainActivity.os.print("keyboard|" + characters);
				}
				MainActivity.os.flush();
			}
			return true;
		}
	}
	
	class pannelSwitcherOnTouchListener implements 
		OnTouchListener {
		private ImageView keyImage;
		private long currentTime;
		private long lastTime;
		private int xStep = 0;
		
		private int oringeResId;
		private int showResId;
		pannelSwitcherOnTouchListener(int oResId, int sResId) {
			oringeResId = oResId;
			showResId = sResId;
		}
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (v instanceof ImageView) {
				keyImage = (ImageView)v;
			} else 
				return false;
			
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN :
				keyImage.setImageResource(showResId);
				if (xStep == 0) {
					lastTime = System.currentTimeMillis();
					xStep = 1;
				}
				break;
			case MotionEvent.ACTION_UP :
				keyImage.setImageResource(oringeResId);
				if (xStep == 1) {
					currentTime = System.currentTimeMillis();
					if (currentTime - lastTime < 200) {
						// 键盘面板转换
						if (isInWordsPannel) {
							// 关闭大写
							isInWordsPannel = false;
							KeyboardActivity.this.setContentView(R.layout.keyboard_pannel2);
							setSpecialKeys();
						} 
						else {
							isInWordsPannel = true;
							KeyboardActivity.this.setContentView(R.layout.keyboard_pannel);
							setGeneralKeys();
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