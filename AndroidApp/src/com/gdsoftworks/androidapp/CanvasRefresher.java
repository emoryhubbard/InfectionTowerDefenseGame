package com.gdsoftworks.androidapp;

import com.gdsoftworks.app.App;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

@SuppressLint("ViewConstructor")
public class CanvasRefresher extends SurfaceView implements Runnable {
	App app; Bitmap virtualBuffer;
	Thread refreshThread = null; SurfaceHolder holder;
	volatile boolean running = false;
	
	public CanvasRefresher(Context context, App app, Bitmap virtualBuffer) {
		super(context, null);
		this.app = app; this.virtualBuffer = virtualBuffer; this.holder = getHolder();
	}
	public void resume() {
		running = true;
		refreshThread = new Thread(this); refreshThread.start();
	}
	public void run() {
		Rect dstRect = new Rect();
		long startTime = System.nanoTime();
		while (running) {
			if (!holder.getSurface().isValid()) continue;
			
			double deltaTime = (System.nanoTime()-startTime)/1000000000d;
			startTime = System.nanoTime();
			
			app.getCurrentScreen().update(deltaTime);
			app.getCurrentScreen().present();
			
			Canvas canvas = holder.lockCanvas();
			canvas.getClipBounds(dstRect);
			canvas.drawBitmap(virtualBuffer,  null,  dstRect, null);
			holder.unlockCanvasAndPost(canvas);
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
}
