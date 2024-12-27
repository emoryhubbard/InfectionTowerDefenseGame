package com.gdsoftworks.app;

public interface Music {
	public Music play(); public Music stop(); public Music pause();
	
	public Music setLooping(boolean looping);
	/**
	 * 
	 * @param volume
	 * @return
	 * @see Sound#play(double)
	 */
	public Music setVolume(double volume);
	public boolean isPlaying(); public boolean isStopped(); public boolean isLooping();
	public void dispose();
}
