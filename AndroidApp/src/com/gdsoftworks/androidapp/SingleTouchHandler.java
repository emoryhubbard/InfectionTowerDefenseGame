package com.gdsoftworks.androidapp;

import java.util.ArrayList;
import java.util.List;

import com.gdsoftworks.app.InstancePool;
import com.gdsoftworks.app.InstancePool.InstanceFactory;
import com.gdsoftworks.app.UserInput.TouchEvent;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class SingleTouchHandler implements TouchHandler, OnTouchListener {
	boolean touched;
	float touchX, touchY;
	InstancePool<TouchEvent> pool;
	List<TouchEvent> events = new ArrayList<TouchEvent>();
	List<TouchEvent> eventsBuffer = new ArrayList<TouchEvent> ();
	double scaleX, scaleY;
	
	public SingleTouchHandler(View view, double scaleX, double scaleY) {
		InstanceFactory<TouchEvent> factory = new InstanceFactory<TouchEvent>(){
			public TouchEvent newInstance() {return new TouchEvent();}
		};
		pool = new InstancePool<TouchEvent>(factory, 100);
		view.setOnTouchListener(this);
		this.scaleX=scaleX; this.scaleY=scaleY;
	}
	public boolean onTouch(View view, MotionEvent motionEvent) {
		synchronized (this) {
			TouchEvent event = pool.newObject();
			switch (motionEvent.getAction()) {
			case MotionEvent.ACTION_DOWN:
				event.type = TouchEvent.TOUCH_DOWN;
				touched = true;
				break;
			case MotionEvent.ACTION_MOVE:
				event.type = TouchEvent.TOUCH_DRAGGED;
				touched = true;
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				event.type = TouchEvent.TOUCH_UP;
				touched = false;
				break;
			}
			
			event.x = touchX = (float)(motionEvent.getX() * scaleX);
			event.y = touchY = (float)(motionEvent.getY() * scaleY);
			eventsBuffer.add(event);
			
			return true;
		}
	}
	public boolean isTouchDown(int pointer) {
		synchronized(this) {return pointer==0 ? touched: false;}
	}
	public float getTouchX(int pointer) {synchronized(this) {return touchX;}}
	public float getTouchY(int pointer) {synchronized(this) {return touchY;}}
	public List<TouchEvent> getTouchEvents() {
		synchronized(this) {
			int length = events.size();
			for (int i=0; i<length; i++) pool.free(events.get(i));
			events.clear();
			int len = eventsBuffer.size();
			for (int i=0; i<len; i++) events.add(eventsBuffer.get(i));
			eventsBuffer.clear();
			return events;
		}
	}
	
}