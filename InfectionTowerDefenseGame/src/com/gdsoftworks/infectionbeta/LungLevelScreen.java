package com.gdsoftworks.infectionbeta;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import gdsoftworks.geometry.Collision;
import gdsoftworks.geometry.FastRectangle;
import gdsoftworks.geometry.Vector;

import javax.microedition.khronos.opengles.GL10;

import com.gdsoftworks.androidapp.FPSCounter;
import com.gdsoftworks.androidapp.opengl.Camera2D;
import com.gdsoftworks.androidapp.opengl.GLApp;
import com.gdsoftworks.androidapp.opengl.GLScreen;
import com.gdsoftworks.androidapp.opengl.SpriteBatcher;
import com.gdsoftworks.androidapp.opengl.TextureRegion;
import com.gdsoftworks.app.InstancePool;
import com.gdsoftworks.app.Sound;
import com.gdsoftworks.app.UserInput.TouchEvent;
import com.gdsoftworks.util.Arrays;
import com.gdsoftworks.util.Strings;

public class LungLevelScreen extends GLScreen {
	FPSCounter fpsCounter = new FPSCounter();
	static final int RUNNING = 0;
	static final int PAUSED = 1;
	static final int ENDING = 2;
	static final double FINGER_OFFSET = .75;
	int state = RUNNING;
	int length = 0;
	boolean expanded = false;
	char[] nanograms = new char[15];
	Vector touch = new Vector();
	Camera2D camera = new Camera2D(glGraphics, 480, 320);
	SpriteBatcher batcher = new SpriteBatcher(glGraphics, 10000);
	LungLevel level;
	LungLevelRenderer levelRenderer;
	FastRectangle optionsBounds = new FastRectangle(426-64, 290-29, 128, 58);
	FastRectangle resumeBounds = new FastRectangle(240-80, 220-46, 160, 92);
	FastRectangle exitBounds = new FastRectangle(240-80, 220-120-46, 160, 92);
	InstancePool<PointsPhantom> phantomPool = new InstancePool<PointsPhantom>(
			new InstancePool.InstanceFactory<PointsPhantom>() {
			public PointsPhantom newInstance() {return new PointsPhantom();}}, 200);
	{for (int i=0; i<200; i++) phantomPool.free(new PointsPhantom());}
	List<PointsPhantom> phantoms = new ArrayList<PointsPhantom>(200);
	List<Card> cards = new LinkedList<Card>(){
		{
			Card.Caller caller = new Card.Caller(){
				public void collapseCards(){
					gadgetsButton.recall();
				}
				public void createGadget(Vector point, int type){
					String name = "";
					switch (type) {
					case Card.SCAFFOLD: name = "scaffold"; break;
					case Card.ION_CATCHER: name = "ionCatcher"; break;
					case Card.LIPOLYZER: name = "lipolyzer"; break;
					case Card.DREXLER_SAW: name = "drexlerSaw"; break;
					}
					LungLevelScreen.this.createGadget(name,
							point.x+FINGER_OFFSET, point.y+FINGER_OFFSET);
				}
			};
			double distance = 480;
			double height = 80; double width = 80;
			double x = 80; double startX = 80;
			double y = 320-80;
			int rowLength = 5; int col = 0; int maxCards = 4;
			for (int i=0; i<maxCards; i++, col++, x+=width) {
				if (col==rowLength) {col=0; x=startX; y-=height;}
				if (i<5)
					add(new Card(new Vector(x, y+distance), new Vector(x, y), caller));
				else if (i<10)
					add(new Card(new Vector(x-distance, y), new Vector(x, y), caller));
				else if (i<15)
					add(new Card(new Vector(x, y-distance), new Vector(x, y), caller));
			}
			get(0).type = Card.SCAFFOLD; get(0).cost = Scaffold.COST;
			get(1).type = Card.ION_CATCHER; get(1).cost = IonCatcher.COST;
			get(2).type = Card.LIPOLYZER; get(2).cost = Lipolyzer.COST;
			get(3).type = Card.DREXLER_SAW; get(3).cost = DrexlerSaw.COST;
		}};
	public GadgetsButton gadgetsButton = new GadgetsButton(cards);
	public NanogramsBar ngBar = new NanogramsBar(0);
	public RotationWidget rotationWidget = new RotationWidget(
			new RotationWidget.Caller(){
				public void setSpin(int spin) {
					switch (spin) {
					case RotationWidget.NONE:
						level.spin = level.NONE;
						break;
					case RotationWidget.CC:
						level.spin = level.CC;
						break;
					case RotationWidget.C:
						level.spin = level.C;
						break;
					}
				}
			});
	public LungLevelScreen(GLApp glApp) {
		super(glApp);
		level = new LungLevel(new LungLevel.Player(){
			public void zap() {Assets.playSound(Assets.spark);}
			public void shoot() {Assets.playSound(Assets.shot);}
			public void stick() {Assets.playSound(Assets.stick);}
			public void cut() {Assets.playSound(Assets.cut);}
			public void gain(int amount, Vector pos) {
				PointsPhantom phantom = phantomPool.newObject();
				length = Strings.buildInt(nanograms, level.nanograms);
				Sound sound = Assets.clang;
				if (amount>=20) sound = Assets.loudClang;
				Assets.playSound(sound);
				phantom.age = 0;
				phantom.alpha = 1;
				phantom.position.set(pos);
				levelRenderer.camera.convertToScreenPoint(phantom.position);
				camera.normalizeScreenPoint(phantom.position);
				phantom.length = Strings.buildInt(phantom.text, amount)+1;
				Arrays.insert(phantom.text, '+', 0);
				phantom.setText();
				phantom.velocity.set(ngBar.position.x+ngBar.OFFSET, ngBar.position.y);
				phantom.velocity.add(-phantom.position.x, -phantom.position.y);
				phantom.velocity.scale(1/phantom.LIFESPAN);
				phantoms.add(phantom);
			}
			public void spend(int amount, Vector pos) {
				length = Strings.buildInt(nanograms, level.nanograms);
				Assets.playSound(Assets.airPowerTool);
				PointsPhantom phantom = phantomPool.newObject();
				phantom.age = 0;
				phantom.alpha = 1;
				phantom.position.set(ngBar.position.x+ngBar.OFFSET, ngBar.position.y);
				phantom.length = Strings.buildInt(phantom.text, -amount);
				phantom.setText();
				phantom.velocity.set(pos);
				levelRenderer.camera.convertToScreenPoint(phantom.velocity);
				camera.normalizeScreenPoint(phantom.velocity);
				phantom.velocity.add(-phantom.position.x, -phantom.position.y);
				phantom.velocity.scale(1/phantom.LIFESPAN);
				phantoms.add(phantom);
			}
			public void addTemplate() {
				rotationWidget.advance(0);
				}
			public void removeTemplate() {
				rotationWidget.advance(1);
				}
			});
		length = Strings.buildInt(nanograms, level.nanograms);
		levelRenderer = new LungLevelRenderer(glGraphics, batcher, level);
	}
	public void update(double deltaTime) {
		app.getUserInput().getKeyEvents();
		switch (state) {
		case RUNNING: updateRunning(deltaTime); break;
		case PAUSED: updatePaused();
		}
	}
	Vector worldTouch = new Vector(); Vector guiTouch = new Vector();
	public void updateRunning(double deltaTime) {
		List<TouchEvent> events = app.getUserInput().getTouchEvents();
		level.update(deltaTime);
		gadgetsButton.update(deltaTime);
		ngBar.update(deltaTime);
		rotationWidget.update(deltaTime);
		updatePhantoms(deltaTime);
		int length = events.size();
		for (int i=0; i<length; i++) {
			TouchEvent event = events.get(i);
			touch.set(event.x, event.y);
			guiTouch.set(touch);
			worldTouch.set(touch);
			camera.normalizeScreenPoint(guiTouch);
			levelRenderer.camera.normalizeScreenPoint(worldTouch);
			camera.normalizeScreenPoint(touch);
			rotationWidget.press(guiTouch);
			if (event.type==TouchEvent.TOUCH_DOWN) {
				int len = cards.size();
				for (int j=0; j<len; j++) {
					Card card = cards.get(j);
					if (Collision.rectContains(card.bounds, guiTouch))
						card.press(worldTouch);
				}
				if (Collision.rectContains(optionsBounds, touch)) {
					state = PAUSED; return;
				}
				else {
					touch.set(event.x, event.y);
					levelRenderer.camera.normalizeScreenPoint(touch);
					level.press(touch.x, touch.y);
				}
			}
			if (event.type==TouchEvent.TOUCH_DRAGGED)
				if (level.template!=null
				&& !Collision.rectContains(rotationWidget.bounds, touch)) {
					touch.set(event.x, event.y);
					levelRenderer.camera.normalizeScreenPoint(touch);
					level.move(touch.x+FINGER_OFFSET, touch.y+FINGER_OFFSET);
					rotationWidget.release();
					if (rotationWidget.position.distanceSquared(guiTouch)
							<rotationWidget.CORNER_DIST*rotationWidget.CORNER_DIST)
						rotationWidget.bump();
				}
			if (event.type==TouchEvent.TOUCH_UP) {
				gadgetsButton.recall();
				touch.set(event.x, event.y);
				camera.normalizeScreenPoint(touch);
				if (level.template!=null) rotationWidget.release();
				if (level.template!=null
						&& !Collision.rectContains(rotationWidget.bounds, touch)) {
					level.attach(); level.destroy(); rotationWidget.release();
				}
				if (Collision.rectContains(gadgetsButton.bounds, guiTouch))
					gadgetsButton.press(guiTouch);
				if (Collision.rectContains(ngBar.bounds, guiTouch))
					ngBar.press(guiTouch);
			}
			if (event.type==TouchEvent.TOUCH_DRAGGED
					|| event.type==TouchEvent.TOUCH_UP) {
				touch.set(event.x, event.y);
				levelRenderer.camera.normalizeScreenPoint(touch);
				level.release(touch.x, touch.y);
			}
		}
	}
	List<PointsPhantom> removePhantoms = new ArrayList<PointsPhantom>(10);
	public void updatePhantoms(double deltaTime) {
		int len = phantoms.size();
		for (int i=0; i<len; i++) {
			PointsPhantom phantom = phantoms.get(i);
			phantom.update(deltaTime);
			if (phantom.age>phantom.LIFESPAN) removePhantoms.add(phantom);
		}
		len = removePhantoms.size();
		for (int i=0; i<len; i++) {
			PointsPhantom phantom = removePhantoms.get(i);
			phantomPool.free(phantom);
			phantoms.remove(phantom);
		}
		removePhantoms.clear();
	}
	public void createGadget(String name, double x, double y) {
		level.create(name, x, y);
		expanded = false;
	}
	public void updatePaused() {
		List<TouchEvent> events = app.getUserInput().getTouchEvents();
		int length = events.size();
		for (int i=0; i<length; i++) {
			TouchEvent event = events.get(i);
			touch.set(event.x, event.y);
			camera.normalizeScreenPoint(touch);
			if (event.type==TouchEvent.TOUCH_DOWN) {
				if (Collision.rectContains(resumeBounds, touch)) {
					state = RUNNING;
				}
				if (Collision.rectContains(exitBounds, touch)) {
					glApp.setScreen(new MainMenuScreen(glApp));
					Assets.setScreenOrientation(Assets.PORTRAIT);
					return;
				}
			}
		}
	}
	public void present() {
		//fpsCounter.logFrame();
		GL10 gl = glGraphics.getGL();
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		levelRenderer.render();
		
		camera.setViewportAndMatrices();
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		batcher.beginBatch(Assets.menuAtlas);
		renderGadgetsButton();
		renderNanograms();
		renderCards();
		renderPhantoms();
		renderRotationWidget();
		switch (state) {
		case RUNNING: presentRunning(); break;
		case PAUSED: presentPaused();
		}
		batcher.endBatch();
		gl.glDisable(GL10.GL_BLEND);
	}
	private void renderGadgetsButton() {
		batcher.drawSprite(gadgetsButton.position.x, gadgetsButton.position.y,
				gadgetsButton.bounds.width, gadgetsButton.bounds.height, Assets.gadgetButton);
	}
	private void renderCards() {
		GL10 gl = glGraphics.getGL();
		int len = cards.size();
		for (int i=0; i<len; i++) {
			Card card = cards.get(i);
			batcher.drawSprite(card.position.x, card.position.y, card.bounds.width,
					card.bounds.height, card.orientation, Assets.card);
			TextureRegion inset = Assets.none;
			switch (card.type) {
			case Card.SCAFFOLD: inset = Assets.scaffold; break;
			case Card.ION_CATCHER: inset = Assets.ionCatcher; break;
			case Card.LIPOLYZER: inset = Assets.lipolyzer; break;
			case Card.DREXLER_SAW: inset = Assets.drexlerSaw; break;
			}
			batcher.drawSprite(card.position.x, card.position.y, card.bounds.width,
					card.bounds.height, card.orientation,
					inset);
			if (card.state==Card.EXPANDED) {
				String text = Integer.toString(card.cost);
				double offset = (text.length()-1)*4;
				if (level.nanograms>card.cost)
					drawDigits(text, card.position.x-offset, card.position.y-28);
				else {
					batcher.endBatch();
					batcher.beginBatch(Assets.menuAtlas);
					drawDigits(text, card.position.x-offset, card.position.y-28);
					gl.glColor4f(1, 0, 0, 1);
					batcher.endBatch();
					batcher.beginBatch(Assets.menuAtlas);
					gl.glColor4f(1, 1, 1, 1);
				}
			}
		}
	}
	private void renderRotationWidget() {
		batcher.drawSprite(rotationWidget.position.x, rotationWidget.position.y,
				rotationWidget.bounds.width, rotationWidget.bounds.height, level.orientation,
				Assets.rotationWidget);
	}
	private void renderPhantoms() {
		GL10 gl = glGraphics.getGL();
		if (batcher.sprites>0) batcher.endBatch();
		int len = phantoms.size();
		for (int i=0; i<len; i++) {
			PointsPhantom phantom = phantoms.get(i);
			batcher.beginBatch(Assets.menuAtlas);
			double offset = (phantom.length-1)*4;
			drawDigits(phantom.text, phantom.length,
					phantom.position.x-offset, phantom.position.y);
			float red = 0; float green = 1;
			if (phantom.text[0]=='-') {red = 1; green = 0;}
			gl.glColor4f(red, green, 0, (float)phantom.alpha);
			batcher.endBatch();
		}
		batcher.beginBatch(Assets.menuAtlas);
		gl.glColor4f(1, 1, 1, 1);
	}
	private void renderNanograms() {
		batcher.drawSprite(ngBar.position.x, ngBar.position.y, ngBar.bounds.width,
				ngBar.bounds.height, ngBar.orientation, Assets.ng);
		double offset = (length-1)*4;
		drawDigits(nanograms, length, ngBar.position.x+ngBar.OFFSET-offset,
				ngBar.position.y);
		
	}
	/**private void presentExpanded() {
		GL10 gl = glGraphics.getGL();
		batcher.drawSprite(100, 160, 200, 320, Assets.expanded);
		drawDigits(gl, batcher, Integer.toString(Scaffold.COST), 20, 320-90);
		drawDigits(gl, batcher, Integer.toString(IonCatcher.COST), 20, 320-69);
		drawDigits(gl, batcher, Integer.toString(Lipolyzer.COST), 20, 320-111);
		drawDigits(gl, batcher, Integer.toString(DrexlerSaw.COST), 20, 320-69+21);
		batcher.drawSprite(260, 160, 75, 75, Assets.gadgetButton);
		batcher.drawSprite(260, 165, 55, 45, Assets.scaffold);
	}
	private void presentCollapsed() {
		batcher.drawSprite(55+9, 320-17-6, 110, 34, Assets.gadgets);
	}**/
	/**private void drawNanograms(GL10 gl, SpriteBatcher batcher, String digits,
			double x, double y) {
		int length = digits.length();
		for (int i=0; i<length; i++) {
			int digitIndex = digits.charAt(i) - '0';
			batcher.drawSprite(x, y, 8, 9, Assets.digits[digitIndex]);
			x+=8;
		}
		x+=8;
		batcher.drawSprite(x, y, 17, 15, Assets.ng);
	}**/
	private void drawDigits(String digits, double x, double y) {
		int length = digits.length();
		for (int i=0; i<length; i++) {
			char digit = digits.charAt(i);
			int digitIndex;
			if (digit=='+') digitIndex = 10;
			else if (digit=='-') digitIndex = 11;
			else digitIndex = digits.charAt(i) - '0';
			batcher.drawSprite(x, y, 8, 9, Assets.digits[digitIndex]);
			x+=8;
		}
	}
	private void drawDigits(char[] digits, int length, double x, double y) {
		for (int i=0; i<length; i++) {
			char digit = digits[i];
			int digitIndex;
			if (digit=='+') digitIndex = 10;
			else if (digit=='-') digitIndex = 11;
			else digitIndex = digits[i] - '0';
			batcher.drawSprite(x, y, 8, 9, Assets.digits[digitIndex]);
			x+=8;
		}
	}
	private void presentRunning() {
		batcher.drawSprite(426, 290, 128, 58, Assets.options);
	}
	private void presentPaused() {
		batcher.drawSprite(240, 220, 160, 92, Assets.resume);
		batcher.drawSprite(240, 220-120, 160, 92, Assets.exit);
	}
	public void pause() {if (state==RUNNING) state=PAUSED;}
}
