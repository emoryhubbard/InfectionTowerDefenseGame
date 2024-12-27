package com.gdsoftworks.androidapp;

import android.media.SoundPool;

import com.gdsoftworks.app.Sound;

public class AndroidSound implements Sound {
	int soundID; SoundPool pool;
	
	public AndroidSound(SoundPool soundPool, int soundID) {
		this.soundID=soundID; pool=soundPool;
	}
	public AndroidSound play(double volume) {
		pool.play(soundID,  (float)volume,  (float)volume,  0,  0,  1); return this;
	}
	public void dispose() {pool.unload(soundID);}
}
