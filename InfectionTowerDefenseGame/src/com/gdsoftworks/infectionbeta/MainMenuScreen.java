package com.gdsoftworks.infectionbeta;

import java.util.List;

import gdsoftworks.geometry.Collision;
import gdsoftworks.geometry.FastRectangle;
import gdsoftworks.geometry.Vector;

import javax.microedition.khronos.opengles.GL10;

import com.gdsoftworks.androidapp.opengl.Camera2D;
import com.gdsoftworks.androidapp.opengl.GLApp;
import com.gdsoftworks.androidapp.opengl.GLScreen;
import com.gdsoftworks.androidapp.opengl.SpriteBatcher;
import com.gdsoftworks.app.UserInput.TouchEvent;

public class MainMenuScreen extends GLScreen {
	Camera2D guiCam = new Camera2D(glGraphics, 320, 480);
	SpriteBatcher batcher = new SpriteBatcher(glGraphics, 100);
	FastRectangle soundBounds = new FastRectangle(0, 0, 128, 128);
	FastRectangle startBounds = new FastRectangle(160-80, 240+90-35, 160, 70);
	FastRectangle infoBounds = new FastRectangle(160-80, 240-35, 160, 70);
	FastRectangle moreBounds = new FastRectangle(160-80, 240-35-90, 160, 70);
	Vector touchPoint = new Vector();
	public MainMenuScreen(GLApp app) {
		super(app);
	}
	public void update(double deltaTime) {
		glApp.getUserInput().getKeyEvents();
		List<TouchEvent> events = app.getUserInput().getTouchEvents();
		
		int length = events.size();
		for (int i=0; i<length; i++) {
			TouchEvent event = events.get(i);
			if (event.type==TouchEvent.TOUCH_DOWN) {
				touchPoint.set(event.x, event.y);
				guiCam.normalizeScreenPoint(touchPoint);
				
				if (Collision.rectContains(soundBounds, touchPoint)) {
					Settings.soundEnabled = !Settings.soundEnabled;
					if (Settings.soundEnabled) Assets.music.play();
					else Assets.music.pause();
				}
				if (Collision.rectContains(startBounds, touchPoint)) {
					glApp.setScreen(new LungLevelScreen(glApp));
					Assets.setScreenOrientation(Assets.LANDSCAPE);
					return;
				}
				if (Collision.rectContains(infoBounds,  touchPoint)) {
					glApp.setScreen(new HelpScreen1(glApp));
					return;
				} 
				if (Collision.rectContains(moreBounds, touchPoint)) {
					glApp.setScreen(new MoreScreen1(glApp));
					return;
				}
			}
		}
	}
	public void present() {
		GL10 gl = glGraphics.getGL();
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		guiCam.setViewportAndMatrices();
		
		gl.glEnable(GL10.GL_TEXTURE_2D);
		batcher.beginBatch(Assets.mainMenuBackground);
		batcher.drawSprite(160, 240, 320, 480, Assets.mainMenuBackgroundRegion);
		batcher.endBatch();
		
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		batcher.beginBatch(Assets.menuAtlas);
		batcher.drawSprite(160, 420, 256, 128, Assets.logo);
		batcher.drawSprite(160, 240, 160, 262, Assets.mainMenu);
		batcher.drawSprite(64, 64, 128, 128,
				Settings.soundEnabled?Assets.soundOn:Assets.soundOff);
		batcher.endBatch();
		gl.glDisable(GL10.GL_BLEND);
		
		gl.glDisable(GL10.GL_TEXTURE_2D);
	}
	public void pause() {Settings.save(glApp.getIO());}
}
