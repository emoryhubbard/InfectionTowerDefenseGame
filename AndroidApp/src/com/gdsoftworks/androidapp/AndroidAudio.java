package com.gdsoftworks.androidapp;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;

import com.gdsoftworks.app.Audio;

public class AndroidAudio implements Audio {
	AssetManager assets;
	SoundPool pool;
	
	public AndroidAudio(Activity activity) {
		activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		this.assets = activity.getAssets();
		this.pool = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);
	}
	public AndroidMusic newMusic(String fileName) {
		try {
			AssetFileDescriptor assetDescriptor = assets.openFd(fileName);
			return new AndroidMusic(assetDescriptor);
		} catch (Exception e) {
			throw new RuntimeException("Couldn't load music file: "+e.getMessage());
		}
	}
	public AndroidSound newSound(String fileName) {
		try {
			AssetFileDescriptor assetDescriptor = assets.openFd(fileName);
			int soundID = pool.load(assetDescriptor,  0);
			return new AndroidSound(pool, soundID);
		} catch (Exception e) {
			throw new RuntimeException("Couldn't load sound file: "+e.getMessage());
		}
	}
}
