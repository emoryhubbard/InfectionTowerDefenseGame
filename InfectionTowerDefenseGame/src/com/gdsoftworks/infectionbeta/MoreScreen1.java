package com.gdsoftworks.infectionbeta;

import gdsoftworks.geometry.Collision;
import gdsoftworks.geometry.FastRectangle;
import gdsoftworks.geometry.Vector;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import com.gdsoftworks.androidapp.opengl.Camera2D;
import com.gdsoftworks.androidapp.opengl.GLApp;
import com.gdsoftworks.androidapp.opengl.GLScreen;
import com.gdsoftworks.androidapp.opengl.SpriteBatcher;
import com.gdsoftworks.app.UserInput.TouchEvent;

public class MoreScreen1 extends GLScreen {
	Camera2D guiCam = new Camera2D(glGraphics, 320, 480);
	SpriteBatcher batcher = new SpriteBatcher(glGraphics, 100);
	FastRectangle nextBounds = new FastRectangle(320-106, 0, 106, 106);
	Vector touchPoint = new Vector();
	public MoreScreen1(GLApp app) {
		super(app);
		Assets.setScreenOrientation(Assets.PORTRAIT);
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
				
				if (Collision.rectContains(nextBounds, touchPoint)) {
					glApp.setScreen(new MoreScreen2(glApp));
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
		batcher.beginBatch(Assets.more1);
		batcher.drawSprite(160, 240, 320, 480, Assets.more1Region);
		batcher.endBatch();
		gl.glDisable(GL10.GL_TEXTURE_2D);
	}
	public void pause() {Settings.save(glApp.getIO());}
}

