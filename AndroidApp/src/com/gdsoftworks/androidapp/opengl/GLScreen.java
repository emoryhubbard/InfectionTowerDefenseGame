package com.gdsoftworks.androidapp.opengl;

import java.util.ArrayList;
import java.util.List;

import com.gdsoftworks.app.ScreenAdapter;

public abstract class GLScreen extends ScreenAdapter {
	protected final GLApp glApp;
	protected final GLGraphics glGraphics;
	volatile protected List<RenderCommand> renderCommands = new ArrayList<RenderCommand>(10000);
	public GLScreen(GLApp app) {super(app); glApp=app; glGraphics=app.getGLGraphics();}
	public void render(RenderCommand command) {renderCommands.add(command);}
	public BeginBatch newBeginBatch() {return glApp.beginPool.newObject();}
	public DrawSprite newDrawSprite() {return glApp.spritePool.newObject();}
	public DrawModel newDrawModel() {return glApp.modelPool.newObject();}
	public EndBatch newEndBatch() {return glApp.endPool.newObject();}
	public ChangeBlend newChangeBlend() {return glApp.blendPool.newObject();}
	public ChangeColor newChangeColor() {return glApp.colorPool.newObject();}
	public ChangeCamera newChangeCamera() {return glApp.cameraPool.newObject();}
	public Clear newClear() {return glApp.clearPool.newObject();}
	public Enable newEnable() {return glApp.enablePool.newObject();}
	public Disable newDisable() {return glApp.disablePool.newObject();}
	public BindTexture newBindTexture() {return glApp.bindPool.newObject();}
}
