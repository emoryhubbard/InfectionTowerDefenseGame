package com.gdsoftworks.app;

import java.util.List;

public abstract class UserInputAdapter implements UserInput {

	public boolean isKeyPresssed(int keyCode) {return false;}
	public boolean isTouchDown(int pointer) {return false;}
	public float getTouchX(int pointer) {return 0;}
	public float getTouchY(int pointer) {return 0;}
	public float getAccelX() {return 0;}
	public float getAccelY() {return 0;}
	public float getAccelZ() {return 0;}
	public List<KeyEvent> getKeyEvents() {return null;}
	public List<TouchEvent> getTouchEvents() {return null;}
}
