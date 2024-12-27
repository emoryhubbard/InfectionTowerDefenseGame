package com.gdsoftworks.androidapp.opengl;

public class Animation {
	public static final int ANIMATION_LOOPING = 0;
	public static final int ANIMATION_NONLOOPING = 1;
	final TextureRegion[] keyFrames; final double frameDuration;
	public Animation(double frameDuration, TextureRegion... keyFrames) {
		this.frameDuration = frameDuration; this.keyFrames = keyFrames;
	}
	public TextureRegion getKeyFrame(double stateTime, int mode) {
		int frameNumber = (int)(stateTime/frameDuration);
		frameNumber = mode==ANIMATION_NONLOOPING?
				Math.min(keyFrames.length-1,  frameNumber): frameNumber%keyFrames.length;
		return keyFrames[frameNumber];
	}
}
