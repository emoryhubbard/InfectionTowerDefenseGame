package com.gdsoftworks.androidapp.opengl;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.gdsoftworks.androidapp.AndroidAudio;
import com.gdsoftworks.androidapp.AndroidIO;
import com.gdsoftworks.androidapp.TouchAndKeyboardInput;
import com.gdsoftworks.app.App;
import com.gdsoftworks.app.Audio;
import com.gdsoftworks.app.Graphics;
import com.gdsoftworks.app.IO;
import com.gdsoftworks.app.Screen;
import com.gdsoftworks.app.SyncPool;
import com.gdsoftworks.app.UserInput;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.Window;
import android.view.WindowManager;

public abstract class GLApp extends Activity implements App, Renderer, Runnable {
	enum AppState {INITIALIZED, RUNNING, PAUSED, FINISHED, IDLE}
	Thread refreshThread = null;
	volatile boolean running = false;
	volatile protected List<RenderCommand> renderCommands = new ArrayList<RenderCommand>(10000);
	final SyncPool<BeginBatch> beginPool =
			new SyncPool<BeginBatch>(new SyncPool.InstanceFactory<BeginBatch>(){
				public BeginBatch newInstance(){return new BeginBatch(GLApp.this);}
			},1000);
	final SyncPool<DrawSprite> spritePool =
			new SyncPool<DrawSprite>(new SyncPool.InstanceFactory<DrawSprite>(){
				public DrawSprite newInstance(){return new DrawSprite(GLApp.this);}
			},100000);
	final SyncPool<EndBatch> endPool =
			new SyncPool<EndBatch>(new SyncPool.InstanceFactory<EndBatch>(){
				public EndBatch newInstance(){return new EndBatch(GLApp.this);}
			},1000);

	final SyncPool<ChangeBlend> blendPool =
			new SyncPool<ChangeBlend>(new SyncPool.InstanceFactory<ChangeBlend>(){
				public ChangeBlend newInstance(){return new ChangeBlend(GLApp.this);}
			},100);
	final SyncPool<ChangeColor> colorPool =
			new SyncPool<ChangeColor>(new SyncPool.InstanceFactory<ChangeColor>(){
				public ChangeColor newInstance(){return new ChangeColor(GLApp.this);}
			},100);
	final SyncPool<ChangeCamera> cameraPool =
			new SyncPool<ChangeCamera>(new SyncPool.InstanceFactory<ChangeCamera>(){
				public ChangeCamera newInstance(){return new ChangeCamera(GLApp.this);}
			},100);
	final SyncPool<Clear> clearPool =
			new SyncPool<Clear>(new SyncPool.InstanceFactory<Clear>(){
				public Clear newInstance(){return new Clear(GLApp.this);}
			},100);
	final SyncPool<Enable> enablePool =
			new SyncPool<Enable>(new SyncPool.InstanceFactory<Enable>(){
				public Enable newInstance(){return new Enable(GLApp.this);}
			},100);
	final SyncPool<Disable> disablePool =
			new SyncPool<Disable>(new SyncPool.InstanceFactory<Disable>(){
				public Disable newInstance(){return new Disable(GLApp.this);}
			},100);
	final SyncPool<DrawModel> modelPool =
			new SyncPool<DrawModel>(new SyncPool.InstanceFactory<DrawModel>(){
				public DrawModel newInstance(){return new DrawModel(GLApp.this);}
			},1000);
	final SyncPool<BindTexture> bindPool =
			new SyncPool<BindTexture>(new SyncPool.InstanceFactory<BindTexture>(){
				public BindTexture newInstance(){return new BindTexture(GLApp.this);}
			},100);
	
	GLSurfaceView glView; GLGraphics glGraphics;
	Audio audio; UserInput input; IO io; Screen screen;
	AppState state = AppState.INITIALIZED;
	Object stateChanged = new Object();
	Object endFrame = new Object();
	long startTime = System.nanoTime();
	WakeLock wakeLock;
	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		glView = new GLSurfaceView(this);
		glView.setRenderer(this);
		setContentView(glView);
		
		glGraphics = new GLGraphics(glView); io = new AndroidIO(this);
		audio = new AndroidAudio(this); input = new TouchAndKeyboardInput(this, glView, 1, 1);
		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "GLApp");
	}
	public void onResume() {
		super.onResume(); glView.onResume(); wakeLock.acquire();
	}
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		glGraphics.setGL(gl);
		
		synchronized(stateChanged) {
			if (state==AppState.INITIALIZED) screen = getStartScreen();
			state = AppState.RUNNING;
			screen.resume(); startTime = System.nanoTime(); resume(); 
		}
	}
	public void onSurfaceChanged(GL10 gl, int width, int height) {}
	public void onDrawFrame(GL10 gl) {
		AppState state = null;
		synchronized(stateChanged) {state=this.state;}
		
		if (state==AppState.RUNNING) {
			synchronized(endFrame) {
				int len = renderCommands.size();
				for (int i=0; i<len; i++) renderCommands.get(i).render(gl);
				endFrame.notifyAll();
			}
		}	
	}
	public void onPause() {
		synchronized(stateChanged) {
			if (isFinishing()) state=AppState.FINISHED;
			else state=AppState.PAUSED;
			while (true) {
				try {stateChanged.wait(); break;} catch(Exception e) {}
			}
		}
		wakeLock.release(); pause(); glView.onPause(); super.onPause();
	}
	public void resume() {
		running = true;
		refreshThread = new Thread(this); refreshThread.start();
	}
	public void run() {
		while (running) {
			AppState state = null;
			synchronized(stateChanged) {state=this.state;}
			
			if (state==AppState.RUNNING) {
				double deltaTime = (System.nanoTime()-startTime)/1000000000d;
				startTime = System.nanoTime();
				screen.update(deltaTime); screen.present();
				synchronized(endFrame) {
					while(true) {
						try {
							endFrame.wait();
							renderCommands = ((GLScreen) screen).renderCommands;
							((GLScreen) screen).renderCommands.clear();
							break;
						} catch (Exception e) {}
					}
				}
				
			}
			if (state==AppState.PAUSED) {
				screen.pause();
				synchronized(stateChanged) {
					this.state=AppState.IDLE; stateChanged.notifyAll();
				}
			}
			if (state==AppState.FINISHED) {
				screen.pause(); screen.dispose();
				synchronized(stateChanged) {
					this.state=AppState.IDLE; stateChanged.notifyAll();
				}
			}	
		}
	}
	public void pause() {
		running = false;
		while (true) {
			try {
				refreshThread.join(); return;
			} catch (Exception e) {}
		}
	}
	public GLGraphics getGLGraphics() {return glGraphics;}
	public Graphics getGraphics() {throw new RuntimeException("Only OpenGL is supported");}
	public UserInput getUserInput() {return input;}
	public IO getIO() {return io;}
	public Audio getAudio() {return audio;}
	public void setScreen(Screen screen) {
		if (screen==null)
			throw new IllegalArgumentException("Screen cannot be null");
		this.screen.pause(); this.screen.dispose();
		screen.resume(); screen.update(0);
		this.screen = screen;
	}
	public Screen getCurrentScreen() {return screen;}
}
