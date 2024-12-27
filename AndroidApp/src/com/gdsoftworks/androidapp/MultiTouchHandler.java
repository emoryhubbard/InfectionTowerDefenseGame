package com.gdsoftworks.androidapp;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.gdsoftworks.app.InstancePool;
import com.gdsoftworks.app.InstancePool.InstanceFactory;
import com.gdsoftworks.app.UserInput.TouchEvent;

@TargetApi(5)
public class MultiTouchHandler implements TouchHandler, OnTouchListener {
	private static final int MAX_TOUCHPOINTS = 10;
	boolean[] touched = new boolean[MAX_TOUCHPOINTS];
	float[] touchX = new float[MAX_TOUCHPOINTS];
	float[] touchY = new float[MAX_TOUCHPOINTS];
	int[] id = new int[MAX_TOUCHPOINTS];
	InstancePool<TouchEvent> pool;
	List<TouchEvent> events = new ArrayList<TouchEvent>();
	List<TouchEvent> eventsBuffer = new ArrayList<TouchEvent>();
	double scaleX, scaleY;
	
	public MultiTouchHandler(View view, double scaleX, double scaleY) {
		InstanceFactory<TouchEvent> factory = new InstanceFactory<TouchEvent>(){
			public TouchEvent newInstance() {return new TouchEvent();}
		};
		pool = new InstancePool<TouchEvent>(factory, 100);
		view.setOnTouchListener(this);
		this.scaleX=scaleX; this.scaleY=scaleY;
	}
	@SuppressWarnings("deprecation")
	public boolean onTouch(View view, MotionEvent motionEvent) {
		synchronized(this) {
			int action = motionEvent.getAction() & MotionEvent.ACTION_MASK;
			int pointerIndex = (motionEvent.getAction()
					& MotionEvent.ACTION_POINTER_ID_MASK) >>
					MotionEvent.ACTION_POINTER_ID_SHIFT;
			int pointerCount = motionEvent.getPointerCount();TouchEvent event;
			for (int i=0; i<MAX_TOUCHPOINTS; i++) {
				if (i>=pointerCount) {
					touched[i] = false;
					id[i] = -1; continue;
				}
				int pointerID = motionEvent.getPointerId(i);
				if (motionEvent.getAction()!=MotionEvent.ACTION_MOVE && i!=pointerIndex)
					continue;
				switch (action) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_POINTER_DOWN:
					event = pool.newObject();
					event.type = TouchEvent.TOUCH_DOWN;
					event.pointer = pointerID;
					event.x = touchX[i] = (float) (motionEvent.getX(i) * scaleX);
					event.y = touchY[i] = (float) (motionEvent.getY(i) * scaleY);
					touched[i] = true; id[i] = pointerID;
					eventsBuffer.add(event);
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_POINTER_UP:
				case MotionEvent.ACTION_CANCEL:
					event = pool.newObject();
					event.type = TouchEvent.TOUCH_UP;
					event.pointer = pointerID;
					event.x = touchX[i] = (float) (motionEvent.getX(i) * scaleX);
					event.y = touchY[i] = (float) (motionEvent.getY(i) * scaleY);
					touched[i] = false; id[i] = -1;
					eventsBuffer.add(event);
					break;
				case MotionEvent.ACTION_MOVE:
					event = pool.newObject();
					event.type = TouchEvent.TOUCH_DRAGGED;
					event.pointer = pointerID;
					event.x = touchX[i] = (float) (motionEvent.getX(i) * scaleX);
					event.y = touchY[i] = (float) (motionEvent.getY(i) * scaleY);
					touched[i] = true; id[i] = pointerID;
					eventsBuffer.add(event);
					break;
				}
			}
			return true;
		}
	}
	public boolean isTouchDown(int pointer) {
		synchronized(this) {
			int index = getIndex(pointer);
			return (index<0 || index>=MAX_TOUCHPOINTS) ? false: touched[index];
		}
	}
	private int getIndex(int pointerID) {
		for (int i=0; i<MAX_TOUCHPOINTS; i++)
			if (id[i]==pointerID) return i;
		return -1;
	}
	public float getTouchX(int pointer) {
		synchronized(this) {
			int index = getIndex(pointer);
			return (index<0 || index>=MAX_TOUCHPOINTS) ? 0: touchX[index];
		}
	}
	public float getTouchY(int pointer) {
		synchronized(this) {
			int index = getIndex(pointer);
			return (index<0 || index>=MAX_TOUCHPOINTS) ? 0: touchY[index];
		}
	}
	public List<TouchEvent> getTouchEvents() {
		synchronized (this) {
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
