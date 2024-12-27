package com.gdsoftworks.androidapp;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

import com.gdsoftworks.app.Music;

public class AndroidMusic implements Music, OnCompletionListener {
	MediaPlayer mediaPlayer; boolean prepared = false;
	
	public AndroidMusic(AssetFileDescriptor assetDescriptor) {
		mediaPlayer = new MediaPlayer();
		try {
			mediaPlayer.setDataSource(assetDescriptor.getFileDescriptor(),
					assetDescriptor.getStartOffset(), assetDescriptor.getLength());
			mediaPlayer.prepare(); prepared=true;
			mediaPlayer.setOnCompletionListener(this);
		} catch (Exception e) {
			RuntimeException re =
					new RuntimeException("Couldn't load music: "+e.getMessage());
			re.initCause(e); throw re;
		}
	}
	public void dispose() {
		if (mediaPlayer.isPlaying()) mediaPlayer.stop();
		mediaPlayer.release();
	}
	public boolean isLooping() {return mediaPlayer.isLooping();}
	public boolean isPlaying() {return mediaPlayer.isPlaying();}
	public boolean isStopped() {return !prepared;}
	public AndroidMusic pause() {
		if (mediaPlayer.isPlaying()) mediaPlayer.pause(); return this;
	}
	public AndroidMusic play() {
		if (mediaPlayer.isPlaying()) return this;
		try {
			synchronized (this) {
				if (!prepared) mediaPlayer.prepare();
				mediaPlayer.start();
			}
		} catch (Exception e) {
			RuntimeException re =
					new RuntimeException("Couldn't play music: "+e.getMessage());
			re.initCause(e); throw re;
		}
		return this;
	}
	public AndroidMusic setLooping(boolean looping) {
		mediaPlayer.setLooping(looping); return this;
	}
	public AndroidMusic setVolume(double volume) {
		mediaPlayer.setVolume((float)volume, (float)volume);
		return this;
	}
	public AndroidMusic stop() {
		mediaPlayer.stop();
		synchronized (this) {prepared=false;}
		return this;
	}
	/**
	 * OnCompletion should only be accessible by media player
	 */
	public void onCompletion(MediaPlayer player) {
		synchronized (this) {prepared=false;}
	}
	
}
