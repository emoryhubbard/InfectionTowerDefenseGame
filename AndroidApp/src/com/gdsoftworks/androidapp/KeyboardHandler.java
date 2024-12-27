package com.gdsoftworks.androidapp;

import java.util.ArrayList;
import java.util.List;

import com.gdsoftworks.app.InstancePool;

import android.view.View;
import android.view.View.OnKeyListener;
import static com.gdsoftworks.app.UserInput.*;
import static com.gdsoftworks.app.InstancePool.*;

public class KeyboardHandler implements OnKeyListener {
	boolean[] pressed = new boolean[128];
	InstancePool<KeyEvent> pool;
	List<KeyEvent> eventsBuffer = new ArrayList<KeyEvent>();
	List<KeyEvent> events = new ArrayList<KeyEvent>();
	
	public KeyboardHandler(View view) {
		InstanceFactory<KeyEvent> factory = new InstanceFactory<KeyEvent>(){
			public KeyEvent newInstance() {return new KeyEvent();}
		};
		pool = new InstancePool<KeyEvent>(factory, 100);
		view.setOnKeyListener(this);
		view.setFocusableInTouchMode(true); view.requestFocus();
		
	}
	public boolean onKey(View v, int keyCode, android.view.KeyEvent androidEvent) {
		if (androidEvent.getAction() == android.view.KeyEvent.ACTION_MULTIPLE)
			return false;
		
		synchronized (this) {
			KeyEvent event = pool.newObject();
			event.keyChar = (char) androidEvent.getUnicodeChar();
			if (androidEvent.getAction() == android.view.KeyEvent.ACTION_DOWN){
				event.type = KeyEvent.KEY_DOWN;
				if (keyCode>0 && keyCode<127) pressed[keyCode] = true;
			}
			if (androidEvent.getAction() == android.view.KeyEvent.ACTION_UP) {
				event.type = KeyEvent.KEY_UP;
				if (keyCode>0 && keyCode <127) pressed[keyCode] = false;
			}
			eventsBuffer.add(event);
		}
		return false;
	}
	public boolean isKeyPressed(int keyCode) {return pressed[keyCode];}
	public List<KeyEvent> getKeyEvents() {
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
