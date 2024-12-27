package com.gdsoftworks.androidapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.Window;
import android.view.WindowManager;

import com.gdsoftworks.app.App;
import com.gdsoftworks.app.Audio;
import com.gdsoftworks.app.Graphics;
import com.gdsoftworks.app.IO;
import com.gdsoftworks.app.Screen;
import com.gdsoftworks.app.UserInput;

public abstract class FullScreenCanvasApp extends Activity implements App {
	CanvasRefresher refresher;
	Graphics graphics; Audio audio; UserInput input; IO io;
	Screen currentScreen; WakeLock wakeLock;
	
	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState, int bufferWidth, int bufferHeight, boolean landscape) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		Bitmap virtualBuffer = Bitmap.createBitmap(landscape?bufferWidth:bufferHeight, landscape?bufferHeight:bufferWidth,
				Config.RGB_565);
		
		double scaleX = (float)(landscape?bufferWidth:bufferHeight)/getWindowManager().getDefaultDisplay().getWidth();
		double scaleY = (float)(landscape?bufferHeight:bufferWidth)/getWindowManager().getDefaultDisplay().getHeight();
		refresher = new CanvasRefresher(this, this, virtualBuffer);
		graphics = new CanvasGraphics(getAssets(), virtualBuffer);
		io = new AndroidIO(this);
		audio = new AndroidAudio(this);
		input = new TouchAndKeyboardInput(this, refresher, scaleX, scaleY);
		currentScreen = getStartScreen();
		setContentView(refresher);
		
		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "2DGraphicsApp");
	}
	public void onResume() {
		super.onResume();
		wakeLock.acquire();
		currentScreen.resume(); refresher.resume();
	}
	public void onPause() {
		super.onPause();
		wakeLock.release();
		refresher.pause(); currentScreen.pause();
		if (isFinishing()) currentScreen.dispose();
	}
	public UserInput getUserInput() {return input;}
	public IO getIO() {return io;}
	public Graphics getGraphics() {return graphics;}
	public Audio getAudio() {return audio;}
	public void setScreen(Screen screen) {
		if (screen==null)
			throw new IllegalArgumentException("Screen cannot be null");
		currentScreen.pause(); currentScreen.dispose();
		screen.resume(); screen.update(0);
		currentScreen = screen;
	}
	public Screen getCurrentScreen() {return currentScreen;}
	
}
