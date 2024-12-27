package com.gdsoftworks.androidapp;

import java.util.List;

import com.gdsoftworks.app.UserInputAdapter;

import android.content.Context;
import android.os.Build.VERSION;
import android.view.View;

public class TouchAndKeyboardInput extends UserInputAdapter {
	KeyboardHandler keyboardHandler;
	TouchHandler touchHandler;
	
	@SuppressWarnings("deprecation")
	public TouchAndKeyboardInput(Context context, View view, double scaleX, double scaleY) {
		keyboardHandler = new KeyboardHandler(view);
		touchHandler = (Integer.parseInt(VERSION.SDK)<5) ?
				new SingleTouchHandler(view, scaleX, scaleY):
				new MultiTouchHandler(view, scaleX, scaleY);
	}
	public boolean isKeyPressed(int keyCode) {
		return keyboardHandler.isKeyPressed(keyCode);
	}
	public boolean isTouchDown(int pointer) {
		return touchHandler.isTouchDown(pointer);
	}
	public float getTouchX(int pointer) {
		return touchHandler.getTouchX(pointer);
	}
	public float getTouchY(int pointer) {
		return touchHandler.getTouchY(pointer);
	}
	public List<TouchEvent> getTouchEvents() {
		return touchHandler.getTouchEvents();
	}
	public List<KeyEvent> getKeyEvents() {
		return keyboardHandler.getKeyEvents();
	}
}
