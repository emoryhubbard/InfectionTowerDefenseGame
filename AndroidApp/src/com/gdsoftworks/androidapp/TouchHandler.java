package com.gdsoftworks.androidapp;

import java.util.List;

import com.gdsoftworks.app.UserInput.TouchEvent;

public interface TouchHandler {
	public boolean isTouchDown(int pointer);
	public float getTouchX(int pointer);
	public float getTouchY(int pointer);
	public List<TouchEvent> getTouchEvents();
}
