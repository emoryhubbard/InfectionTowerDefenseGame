package com.gdsoftworks.infectionbeta;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.gdsoftworks.androidapp.opengl.GLApp;
import com.gdsoftworks.app.Screen;

public class Infection extends GLApp {
	{Assets.main = this;}
	boolean startUp = true;
	public Screen getStartScreen() {return new MainMenuScreen(this);}
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		super.onSurfaceCreated(gl, config);
		if (startUp) {
			Settings.load(getIO()); Assets.load(this); startUp=false;
		}
		else Assets.reload();
	}
	public void onPause() {
		super.onPause();
		if (Settings.soundEnabled) Assets.music.pause();
	}
}
